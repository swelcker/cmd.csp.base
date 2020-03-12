/**
 * 
 */
package cmd.csp.base;


import cmd.csp.interfaces.IProcessor;
import cmd.csp.platform.CSPConstants;
import cmd.csp.platform.CSPDiscovery;
import cmd.csp.platform.CSPInfo;
import cmd.csp.platform.CSPLogDelegate;
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
import io.vertx.core.metrics.Measured;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.shiro.ShiroAuthOptions;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.servicediscovery.Status;
/**
 * @author swelcker
 *
 */
public class BaseVerticleLooper extends AbstractVerticle implements Measured  {
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

	public BaseVerticleLooper() {
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


 		
 		LOGGER.info("Starting CSP VerticleLooper " + service.getName()  );
 		
 		
		
 		//System.out.println("TEST:"+getBusAddressFor(BaseConstants.BusAdressType.socketjs));
		vertx.setPeriodic(util.configApp().getInteger(CSPConstants.INTERVAL_HEARTBEAT), h-> {
					info.put("heartbeat", System.currentTimeMillis());
	 	 			msgController.setInfo(info);
	 		 		mainBus.send(util.getBusAddressFor(CSPConstants.HEALTH_CONFIG), msgController.toBodyString());
	 		 		LOGGER.info("Heartbeat:"+service.getName()+":"+info.getVerticleRef());
				//System.out.println(new Date().toString() + " ->Heartbeat->"+info.encode());
		});	
		
		

		
		
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
		
		// TODO BUILD THE MAIN Loop through the Processor with iInterval
		if(iInterval>0) {
			vertx.setPeriodic(iInterval, h-> {
				info.put("looper", System.currentTimeMillis());
		 			msgController.setInfo(info);
		 			processController.Process(resPrep->{
		 				if (resPrep.succeeded()) {

		 				}else {
		 				}
		 			});	
		 			
		 			LOGGER.info("LooperTriggered:"+service.getName()+":"+info.getVerticleRef());
			});
		}
	
		
		
		LOGGER.info("BUS Consumer Register Looper " + service.getName()+ " on Address: "+ serviceBusAddress);
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
