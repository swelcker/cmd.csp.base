/**
 * 
 */
package cmd.csp.base;


import java.util.Date;

import org.pmw.tinylog.Level;

import cmd.csp.interfaces.IProcessor;
import cmd.csp.platform.CSPConstants;
import cmd.csp.platform.CSPDiscovery;
import cmd.csp.platform.CSPInfo;
import cmd.csp.platform.CSPLogDelegate;
import cmd.csp.platform.CSPLogDelegateFactory;
import cmd.csp.platform.CSPMessage;
import cmd.csp.platform.CSPService;
import cmd.csp.utils.TimeWatch;
import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.metrics.Measured;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.shiro.ShiroAuthOptions;
import io.vertx.ext.bridge.BridgeEventType;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.Status;
import io.vertx.servicediscovery.rest.ServiceDiscoveryRestEndpoint;
import io.vertx.servicediscovery.types.HttpEndpoint;
/**
 * @author swelcker
 *
 */
public class BaseVerticle extends AbstractVerticle implements Measured  {
	protected CSPMessage msgController;
	protected SharedData sharedLocalData;
	protected LocalMap<String, Object> sharedLocalMap;
	protected JsonObject sharedLocalMapJson;
	protected CSPInfo info;
	protected String serviceBusAddress="";
	protected CSPDiscovery ctrlDiscovery;
	protected CSPDiscovery ctrlDiscoveryREST;
	protected CircuitBreaker circuitBreaker;
	protected HttpServer httpserver;
	protected Router router;
	protected SockJSHandler sockJSHandler;
	protected EventBus mainBus;
	protected HealthCheckHandler healthCheckHandler ;
	protected HealthCheckHandler pingCheckHandler ;
	protected io.vertx.ext.healthchecks.Status status;
	protected AuthProvider authProvider;
	protected ShiroAuthOptions authOptions;
	protected JsonObject authConfig;
	protected Integer iInterval = 15000;
	protected Integer count=0;
	private IProcessor processController;
	protected CSPService service=null;
	protected BaseRestAPI restAPI=null;
	protected BaseUtil util=null;

	/**
	 * 
	 */
	protected final CSPLogDelegate LOGGER = new CSPLogDelegate(CSPLogDelegate.class.getName());;

	public BaseVerticle() {
	}

	
	@Override
	public void start(Future<Void> fut) throws Exception{
				
		mainBus = vertx.eventBus();
		restAPI = new BaseRestAPI(vertx);
		router = Router.router(vertx);
		
		util = new BaseUtil(vertx, config());

		if (util.configApp().getBoolean("log.console.enable", false) ) LOGGER.setEnableConsoleOutput(true);
		if (util.configApp().getBoolean("log.serverlog.enable", false) ) LOGGER.enableServerLOG(vertx, null);

		healthCheckHandler = HealthCheckHandler.create(vertx);
		pingCheckHandler = HealthCheckHandler.create(vertx);
		
		registerHealthPingHandler();
		service = util.getServerEntry(config());
		
	    iInterval = util.configVerticle().getInteger(CSPConstants.INTERVAL, 0);
	   // System.out.println(util.configVerticle().encodePrettily());

		sharedLocalData= vertx.sharedData();
		sharedLocalMap = sharedLocalData.getLocalMap(CSPConstants.SharedDataType.CSP_LOCAL.name());
		if (sharedLocalMap.isEmpty()) {
			sharedLocalMapJson = new JsonObject();
		}else {
			sharedLocalMapJson = new JsonObject(sharedLocalMap);
			//System.out.println("BaseServer sharedLocalMapJson: "+sharedLocalMapJson.encode());
		}

		ctrlDiscovery = new CSPDiscovery();
		ctrlDiscovery.setDiscoveryAddress(util.configAddresses().getString(CSPConstants.DISCOVERY_CONFIG));
		serviceBusAddress = util.getBusAddressFor(service.getName());
		// TODO set Registry Values

		info = new CSPInfo();
		msgController = new CSPMessage();
		info.initRuntimeInfo(vertx.hashCode(), this.deploymentID(), this.toString());
		
	    // init circuit breaker instance
	    JsonObject cbOptions = config().getJsonObject("circuit-breaker") != null ?
	      config().getJsonObject("circuit-breaker") : new JsonObject();
	    circuitBreaker = CircuitBreaker.create(cbOptions.getString("name", "csp.circuit-breaker"), vertx,
	      new CircuitBreakerOptions()
	        .setMaxFailures(cbOptions.getInteger("max-failures", 5))
	        .setTimeout(cbOptions.getLong("timeout", 10000L))
	        .setFallbackOnFailure(true)
	        .setResetTimeout(cbOptions.getLong("reset-timeout", 30000L))
	    );


 		
 		LOGGER.info("Starting CSP Verticle " + service.getName()  );
 		if (util.configApp().getBoolean("bridge.js.enable")) {
 			SockJSHandlerOptions sjoptions = new SockJSHandlerOptions();
 			sockJSHandler = SockJSHandler.create(vertx, sjoptions);
 			BridgeOptions options = new BridgeOptions()
 			        .addOutboundPermitted(new PermittedOptions().setAddressRegex(".*"))
 	        		.addInboundPermitted(new PermittedOptions().setAddressRegex(".*"));

 			sockJSHandler.bridge(options, be -> {
 	            try {
 	                if (be.type() == BridgeEventType.SOCKET_CREATED) {
 	                	util.handleSocketOpenEvent(be);
 	                }
 	                else if(be.type() ==BridgeEventType.REGISTER) {
 	                	util.handleRegisterEvent(be);
 	                }
 	                else if(be.type() ==BridgeEventType.UNREGISTER) {
 	                	util.handleUnregisterEvent(be);
 	                }
 	                else if(be.type() ==BridgeEventType.SOCKET_CLOSED) {
 	                	util.handleSocketCloseEvent(be);
 	                }
 	            } catch (Exception e) {

 	            } finally {
 	                be.complete(true);
 	            }
 	        });
 			//addRoutes();
 		
 		}
 		
 		
		

		
		processController = util.Init(router, ctrlDiscovery, sockJSHandler);
		if(processController==null) {
			fut.fail("Can't create ProcessController");
			
		}
		processController.setVertx(vertx, util);
		processController.setInfo(info.getJson());
	    processController.applyConfig(config());
		processController.PrepProcessing(resPrep->{
			if (resPrep.succeeded()) {
			}else {
			}
		});	

 		//System.out.println("TEST:"+getBusAddressFor(BaseConstants.BusAdressType.socketjs));
		vertx.setPeriodic(util.configApp().getInteger(CSPConstants.INTERVAL_HEARTBEAT), h-> {
					info.put("heartbeat", System.currentTimeMillis());
	 	 			msgController.setInfo(info);
	 		 		mainBus.send(util.getBusAddressFor(CSPConstants.HEALTH_CONFIG), msgController.toBodyString());
	 		 		LOGGER.info("Heartbeat:"+service.getName()+":"+info.getVerticleRef());
				//System.out.println(new Date().toString() + " ->Heartbeat->"+info.encode());
		});	
		// Rest API
		if(service.isApion()) {
		    router.route().handler(BodyHandler.create());
		    // API route
		    router.get(BaseRestAPI.API_RETRIEVE).handler(this::apiRetrieve);
		    router.post(BaseRestAPI.API_POST).handler(this::apiPost);

		    initRestAPI();
		}

		LOGGER.info("BUS Consumer Register " + service.getName()+ " on Address: "+ serviceBusAddress);
		mainBus.consumer(serviceBusAddress, ar->{

			msgController.fromBodyString(ar.body().toString());
			if (msgController.isActionRequest() && msgController.isTypeAPI()) {
				  LOGGER.info("BUS Retrieve APIRequest " + service.getName()+ " on Address: "+ serviceBusAddress);
			      TimeWatch watch3 = TimeWatch.start();
				  processController.setInfo(info.getJson());
				  JsonObject conf =null;
				  JsonObject confm =null;				 
				  JsonObject confF =null;				 
				  if(msgController.getConfig()!=null) {
					  conf=msgController.getConfig().copy();
					  confm = new JsonObject();
					  confm.put(service.getConfigKey(), conf);
				  }
				  if(confm!=null) {
						//System.out.println("Call Config: " + confm.encodePrettily());
						confF = config().copy();
						confF.mergeIn(confm, true);
						//System.out.println("Final Config: " + confF.encodePrettily());
					  processController.applyConfig(confF.copy());
				  }else {
					  processController.applyConfig(config());
				  }
				
			      processController.setInput(msgController.getInputs());
			      processController.Process( resProc->{
			      });
				  msgController.addHeader("engineTimeNanoSeconds", String.valueOf(watch3.time()));
			      
			      msgController.addResults(processController.getResult().copy());
			      msgController.setActionToReply();
				  ar.reply(msgController.toBodyString());
			}
			if (msgController.isActionReply() && msgController.isTypeAPI()) {
				LOGGER.info("BUS Retrieve APIReply " + service.getName()+ " on Address: "+ serviceBusAddress+ "	: " + msgController.getSenderName());
				
			}

		});
		
		
		
		
		fut.complete();

	}
	
	private void initRestAPI() {
		ctrlDiscoveryREST = new CSPDiscovery();
		ctrlDiscoveryREST.setDiscoveryAddress(util.configAddresses().getString(CSPConstants.DISCOVERY_CONFIG));

		ctrlDiscoveryREST.setRecordServiceInterface(this.getClass().getName());
		ctrlDiscoveryREST.setRecordServiceName(this.getClass().getName());
		ctrlDiscoveryREST.setRecordType(HttpEndpoint.TYPE);
		ctrlDiscoveryREST.Init(vertx, util);

		  Record record = HttpEndpoint.createRecord(service.getName()+".API", false, service.getApihost(), 
				  service.getApiport(), "/", new JsonObject());
		  ctrlDiscoveryREST.setRecord(record);

		  LOGGER.info("Starting RestAPIServer for "+ service.getName() + " on "+ service.getApihost() + ":" + service.getApiport());
		  httpserver = vertx.createHttpServer(service.getHttpConfig());
		  httpserver.requestHandler(router::accept);
			// MUST Stay here after registry INIT
		    if (util.configApp().getBoolean("bridge.js.enable")) {
		    	router.route("/bridge.js/*").handler(sockJSHandler);			
				ServiceDiscoveryRestEndpoint.create(router, ctrlDiscoveryREST.discovery);
		    }


		    //addHTTPRoutes();
			//router.route(config().getString(BaseConstants.STATIC_RESOURCE_ROUTE)).handler(StaticHandler.create(config().getString(BaseConstants.STATIC_RESOURCE_HANDLER)));
		  httpserver.listen( result -> {
			  if (result.succeeded()) {
			    	   info.setPort(httpserver.actualPort());
			    	   ctrlDiscoveryREST.Publish(ar->{
							if (ar.succeeded()) {
								
							}else {
								
							}
						});
             } else {
             }
		}
		);
	}
	  private void apiRetrieve(RoutingContext context) {
		 try {
		      TimeWatch watch = TimeWatch.start();
		      final String reqcontent = context.request().getParam("message");
		      final String reqservice = context.request().getParam("service");
		      final String reqconfig = context.request().getParam("config");
		      // do something
			      
		      if(reqservice.isEmpty() || reqservice==null || util.getBusAddressFor(reqservice).length()<1) {
			        context.response()
			          .setStatusCode(400)
			          .putHeader("content-type", "text/plain")
			          //.putHeader("content-type", "application/json")
			          //.end(result.encodePrettily());
			          .end("service unknown");
		    	  
		      }else {
			      
			      
			      CSPMessage procMessage = new CSPMessage();
			      // TODO call Processor
			      LOGGER.info("APIRetrieve " + service.getName()+ " ServiceRequested: " + reqservice );
			      procMessage.setInfo(info);
			      procMessage.setActionToRequest();
			      procMessage.setTypeAsAPI();
			      procMessage.addHeader("processor", service.getName());
			      procMessage.addHeader("requestedService", reqservice);
			      procMessage.addConfig(util.processRequestedConfig(reqconfig));
			      procMessage.addHeader("address", util.getBusAddressFor(reqservice));
			      procMessage.addHeader("datetime", new Date().toString());
			      procMessage.setSender(service.getName(), info.getDeploymentID());
			      procMessage.addInputs(util.processRequestedInput(reqcontent));
			      
			      TimeWatch watch2 = TimeWatch.start();
			      mainBus.send(util.getBusAddressFor(reqservice), procMessage.toBodyString(), rep->{
			    	  	if(rep!=null && !rep.failed()) {
							msgController.fromBodyString(rep.result().body().toString());
							msgController.addHeader("executionTimeNanoSeconds", String.valueOf(watch.time()));
							msgController.addHeader("processingTimeNanoSeconds", String.valueOf(watch2.time()));
							if (msgController.isActionReply() && msgController.isTypeAPI()) {
								LOGGER.info("APIReply from ReplyHandler " + service.getName()+ " on Address: "+ util.getBusAddressFor(reqservice));
						        context.response()
						          .setStatusCode(200)
						          .putHeader("content-type", "text/plain")
						          //.putHeader("content-type", "application/json")
						          //.end(result.encodePrettily());
						          .end(msgController.toBodyString());
							}else {
								LOGGER.info("APIReply SomethingWrong from ReplyHandler " + service.getName()+ " on Address: "+ util.getBusAddressFor(reqservice)+ "	: " + msgController.toString());
						        context.response()
						          .setStatusCode(400)
						          .putHeader("content-type", "text/plain")
						          //.putHeader("content-type", "application/json")
						          //.end(result.encodePrettily());
						          .end("somethingwrong");
							}
			    	  	}else {
							LOGGER.info("APIReply NULL from ReplyHandler " + service.getName()+ " on Address: "+ util.getBusAddressFor(reqservice));
					        context.response()
					          .setStatusCode(400)
					          .putHeader("content-type", "text/plain")
					          //.putHeader("content-type", "application/json")
					          //.end(result.encodePrettily());
					          .end("null");
						}
			    	  
			      });
			      
		     }

		      
	    } catch (NumberFormatException ex) {
	    	restAPI.notFound(context);
	    }
	  }
	  private void apiPost(RoutingContext context) {
		 try {
		      TimeWatch watch = TimeWatch.start();
		      final String reqcontent = context.getBodyAsString();
		      final String reqservice = context.request().getParam("service");
		      final String reqconfig = context.request().getParam("config");
		      // do something
			      
		      if(reqservice.isEmpty() || reqservice==null || util.getBusAddressFor(reqservice).length()<1) {
			        context.response()
			          .setStatusCode(400)
			          .putHeader("content-type", "text/plain")
			          //.putHeader("content-type", "application/json")
			          //.end(result.encodePrettily());
			          .end("service unknown");
		    	  
		      }else {
			      
			      
			      CSPMessage procMessage = new CSPMessage();
			      // TODO call Processor
			      LOGGER.info("APIRetrieve " + service.getName()+ " ServiceRequested: " + reqservice );
			      procMessage.setInfo(info);
			      procMessage.setActionToRequest();
			      procMessage.setTypeAsAPI();
			      procMessage.addHeader("processor", service.getName());
			      procMessage.addHeader("requestedService", reqservice);
			      procMessage.addConfig(util.processRequestedConfig(reqconfig));
			      procMessage.addHeader("address", util.getBusAddressFor(reqservice));
			      procMessage.addHeader("datetime", new Date().toString());
			      
			      procMessage.addInputs(util.processRequestedInput(reqcontent));
			      
			      TimeWatch watch2 = TimeWatch.start();
			      mainBus.send(util.getBusAddressFor(reqservice), procMessage.toBodyString(), rep->{
			    	  	if(rep!=null && !rep.failed()) {
							msgController.fromBodyString(rep.result().body().toString());
							msgController.addHeader("executionTimeNanoSeconds", String.valueOf(watch.time()));
							msgController.addHeader("processingTimeNanoSeconds", String.valueOf(watch2.time()));
							if (msgController.isActionReply() && msgController.isTypeAPI()) {
								LOGGER.info("APIReply from ReplyHandler " + service.getName()+ " on Address: "+ util.getBusAddressFor(reqservice));
						        context.response()
						          .setStatusCode(200)
						          .putHeader("content-type", "text/plain")
						          //.putHeader("content-type", "application/json")
						          //.end(result.encodePrettily());
						          .end(msgController.toBodyString());
							}else {
								LOGGER.info("APIReply SomethingWrong from ReplyHandler " + service.getName()+ " on Address: "+ util.getBusAddressFor(reqservice)+ "	: " + msgController.toString());
						        context.response()
						          .setStatusCode(400)
						          .putHeader("content-type", "text/plain")
						          //.putHeader("content-type", "application/json")
						          //.end(result.encodePrettily());
						          .end("somethingwrong");
							}
			    	  	}else {
							LOGGER.info("APIReply NULL from ReplyHandler " + service.getName()+ " on Address: "+ util.getBusAddressFor(reqservice));
					        context.response()
					          .setStatusCode(400)
					          .putHeader("content-type", "text/plain")
					          //.putHeader("content-type", "application/json")
					          //.end(result.encodePrettily());
					          .end("null");
						}
			    	  
			      });
			      
		     }

		      
	    } catch (NumberFormatException ex) {
	    	restAPI.notFound(context);
	    }
	  }


  
	 
	@Override
	public void stop(Future<Void> fut) throws Exception{
		mainBus.consumer(serviceBusAddress).unregister();
		processController.PostProcessing(resPost->{

		});
		ctrlDiscovery.setStatusDown();
		ctrlDiscovery.UnPublish(ar->{
			if (ar.succeeded()) {
				ctrlDiscovery.Close();
				
			}
		});
		LOGGER.info("Stopping CSP Service" );

	}

    public void setStatus(Status cStatus) {
		ctrlDiscovery.setRecordStatus(cStatus);
    }
	public String getServiceStatus() {
		return ctrlDiscovery.getRecordStatus();
	}
	  
	public void ConfigProcessController() {

			
	}
	public void registerHealthPingHandler() {
		
		healthCheckHandler.register("csp-health-check", 2000, future -> {
			  // Do the check ....
			  future.complete(io.vertx.ext.healthchecks.Status.OK(new JsonObject().put("csp-health-check", info.getRuntimeInfo())));

			});	
		pingCheckHandler.register("csp-ping-check", 2000, future -> {
			  // Do the check ....
			  // Upon success do
			  future.complete(io.vertx.ext.healthchecks.Status.OK(new JsonObject().put("ping", info.getRuntimeInfo())));
			});
		router.get("/health*").handler(healthCheckHandler);
		router.get("/ping*").handler(pingCheckHandler);

	}


	@Override
	public boolean isMetricsEnabled() {
		// TODO Auto-generated method stub
		return false;
	} 
}
