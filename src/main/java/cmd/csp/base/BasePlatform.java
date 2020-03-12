/**
 * 
 */
package cmd.csp.base;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.pmw.tinylog.Level;

import cmd.csp.config.ConfigApp;
import cmd.csp.interfaces.IConfig;
import cmd.csp.platform.CSPConstants;
import cmd.csp.platform.CSPInfo;
import cmd.csp.platform.CSPLogDelegate;
import cmd.csp.platform.CSPLogDelegateFactory;
import cmd.csp.platform.CSPService;
import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.datagram.DatagramSocket;
import io.vertx.core.datagram.DatagramSocketOptions;
import io.vertx.core.dns.DnsClient;
import io.vertx.core.dns.DnsClientOptions;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.core.spi.logging.LogDelegate;
import io.vertx.ext.dropwizard.DropwizardMetricsOptions;
import io.vertx.ext.dropwizard.Match;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
/**
 * @author swelcker
 *
 */
public class BasePlatform{
	private LocalMap<String, Object> mapLocal;


	public static DropwizardMetricsOptions optionsMetrics;

	public boolean isVertxReady;
	
	public ConfigApp configApp;
	private boolean enableConsoleOutput = false;
	private boolean enableServerLogging = false;
	private static Integer nInstances; 
	private static String sResult="";
	public Vertx vertx;
	public static VertxOptions optionsVertx;	
	private static EventBusOptions optionsBus;

	public static HttpClientOptions optionsHTTPClient;
	public static DatagramSocketOptions optionsUDPClient;
	public static NetClientOptions optionsTCPClient;
	public static DnsClientOptions optionsDNSClient;

	
	public static Router router;
	public static HashMap<String, Route> routes = new HashMap<>();

	public static HttpClient httpClient;
	public static NetClient tcpClient;
	public static DatagramSocket udpClient;
	public static DnsClient dnsClient;

	public CSPInfo info;
	private CSPLogDelegate lDCSP = new CSPLogDelegate(CSPLogDelegate.class.getName());
	public  Logger LOGGER ;
	//private static ControllerLogDelegate lD;
    private String[] inputFormats ;
    private String[] ouputFormats ;

	public BasePlatform() {
		this(false, false);
	}

	public BasePlatform(boolean consoleOutput, boolean logtoServerLOG) {
		isVertxReady = false;
		enableConsoleOutput = consoleOutput;
		enableServerLogging = logtoServerLOG;
		
		
		initConfig();
		enableConsoleOutput= configApp.getAppConfig().getBoolean("log.console.enable", enableConsoleOutput);
		enableServerLogging= configApp.getAppConfig().getBoolean("log.serverlog.enable", enableServerLogging);

		//if (enableConsoleOutput || enableServerLogging ) {
			System.setProperty(LoggerFactory.LOGGER_DELEGATE_FACTORY_CLASS_NAME, CSPLogDelegateFactory.class.getName());
			
			LoggerFactory.initialise();
		//}
			
		LOGGER = LoggerFactory.getLogger(this.getClass().getName());
			
		lDCSP.setLevel(Level.valueOf(configApp.getAppConfig().getString("log.serverlog.level", "ERROR").toUpperCase()));

		//System.out.println(ControllerLogDelegate.class.getName());
		if (enableConsoleOutput || enableServerLogging ) {
			//TODO
			if (enableConsoleOutput ) {			
				lDCSP.setEnableConsoleOutput(true);
				System.out.println("CSP Application: Activated Console Output via csp.ControllerLogDelegate");
			}
			if (enableServerLogging ) {	
				lDCSP.enableServerLOG(vertx, null);
				System.out.println("CSP Application: Activated Server Logging via csp.ControllerLogDelegate");
			}
		}
		else {
			//lD = LOGGER.getDelegate();
		}
		LOGGER = new Logger((LogDelegate) lDCSP);
      
		info = new CSPInfo();
		info.initRuntimeInfo(this.hashCode(), "CSP.APP@"+this.hashCode(), this.toString());


			EventConfigLoaded();

	}
    private static String getDefaultCharSet() {
        OutputStreamWriter writer = new OutputStreamWriter(new ByteArrayOutputStream());
        String enc = writer.getEncoding();
        return enc;
    }
    
	public  void main(Handler<AsyncResult<Void>> resultHandler) {

		System.out.println("Startup initialized...");
	    System.out.println("System file.encoding:" + System.getProperty("file.encoding"));
	    System.out.println("System Default Charset:" + Charset.defaultCharset());
	    System.out.println("System Default Charset in Use:" + getDefaultCharSet());
	    inputFormats = ImageIO.getReaderFormatNames();
	    ouputFormats = ImageIO.getWriterFormatNames();

	    System.out.println("Available Image.InputFormats:" + Arrays.toString(inputFormats));
	    System.out.println("Available Image.OutputFormats:" + Arrays.toString(ouputFormats));

	    if (!System.getProperty("file.encoding").toUpperCase().contains("UTF-8")){
	    	System.out.println("ERROR: Wrong System file.encoding set for the JVM! Need to start JVM/CSP Application with: java -Dfile.encoding=utf-8 ..." );
	    	resultHandler.handle(Future.failedFuture("Wrong System file.encoding set for the JVM"));
	    }
	    //-Dfile.encoding=utf-8
	    
		initVertx(ar->{
			if(ar.succeeded()) {
				System.out.println("Started Succesfully!");
				
				System.out.println("Started Manager@Address:"+configApp.getAddressesConfig().getString("manager"));
				vertx.eventBus().consumer(configApp.getAddressesConfig().getString("manager"), armgr->{

					HandleManagerTasks(armgr);
				});
				resultHandler.handle(Future.succeededFuture());
			}else {
				System.out.println("ERROR failed to start: "+ar.cause().toString());
				resultHandler.handle(Future.failedFuture(ar.cause()));
			
			}
		});

	}
	
	private final  void HandleManagerTasks( Message<Object> armgr) {
		// Handle additional deploy and undeploy and some manager stuff, like openshift +- threads
		// keep it simple as this is just one simple java thread
		// try to deliver performance overview to understand which service needs more threads and which less

			
	}
	
	private final  void initAfterVertxCreated( Handler<AsyncResult<Void>> resultHandler) {
		// Now deploy some verticle
		configApp.InitAfterVertx(vertx);
		initDeploy();		

			resultHandler.handle(Future.succeededFuture());
	}
	public final void initDeploy(){
	    // Deploy specifying some config
	    LOGGER.info("Start deploying Services and Verticles " + this);
	    // Deploy it as a worker verticle
//	    vertx.deployVerticle("io.vertx.example.core.verticle.deploy.OtherVerticle", new DeploymentOptions().setWorker(true));
	    configApp.getServerListForDeployment().forEach(ctemp->{
	    	
	    	CSPService jcon = new CSPService((JsonObject) ctemp.getValue());
	    	deployCSPVerticle(jcon, res -> {
	    	
	    	});
	    });
	 	LOGGER.info("Finished deploying Server: " + this);
	}

	public void deployCSPVerticle( CSPService jConfig, Handler<AsyncResult<JsonObject>> resultHandler) {
		nInstances=0;
		nInstances = jConfig.getInstances();
		LOGGER.info("Deploy Verticle:"+jConfig.getName()+" with Instances:"+nInstances);
		IConfig cspConfig = null;
		
		// Get Class instance
		Class<?> clazz;
		try {
			clazz = Class.forName(jConfig.getConfig());
			Constructor<?> cons = clazz.getDeclaredConstructor();
			cons.setAccessible(true);
			cspConfig = (IConfig) cons.newInstance();
			cspConfig.setLogger(lDCSP);
			cspConfig.init(jConfig.getConfig(), vertx);
			cspConfig.InitAfterVertx(vertx);
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			resultHandler.handle(Future.failedFuture("Cant Instantiate Config:"+e.getMessage()));
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			resultHandler.handle(Future.failedFuture("Cant Instantiate Config:"+e.getMessage()));
		} catch (SecurityException e) {
			e.printStackTrace();
			resultHandler.handle(Future.failedFuture("Cant Instantiate Config:"+e.getMessage()));
		} catch (InstantiationException e) {
			e.printStackTrace();
			resultHandler.handle(Future.failedFuture("Cant Instantiate Config:"+e.getMessage()));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			resultHandler.handle(Future.failedFuture("Cant Instantiate Config:"+e.getMessage()));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			resultHandler.handle(Future.failedFuture("Cant Instantiate Config:"+e.getMessage()));
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			resultHandler.handle(Future.failedFuture("Cant Instantiate Config:"+e.getMessage()));
		}

		
		JsonObject newconfig= cspConfig.getAsJson();
		newconfig.put("verticle", jConfig.getAsJsonObject());
		newconfig.mergeIn(configApp.getAsJson());
		
		vertx.deployVerticle(jConfig.getVerticle(),new DeploymentOptions()
				.setWorker(jConfig.isWorker())
				.setConfig(newconfig)
				.setMaxWorkerExecuteTime(Long.MAX_VALUE)
				.setInstances(nInstances), deployment -> {
			  if (deployment.succeeded()) {

				  
				  
				    sResult = deployment.result();
					LOGGER.info("Succesfull Deployment (Worker:"+jConfig.isWorker()+") "+jConfig.getName()+" UID: " + sResult);
					JsonObject js = new JsonObject();
					js.put("result", sResult);
					resultHandler.handle(Future.succeededFuture(js));
				  } else {
				    // deployment failed
				    sResult = deployment.cause().getMessage();
					LOGGER.warn("ERR Deployment (Worker:"+jConfig.isWorker()+") "+jConfig.getName()+" Not succesfull: " + sResult);	
					deployment.cause().printStackTrace();
					resultHandler.handle(Future.failedFuture(deployment.cause()));
			  }
		});
	}
	private final  void initVertx(Handler<AsyncResult<Void>> resultHandler){
		ClusterManager mgr = new HazelcastClusterManager();
		optionsVertx = new VertxOptions(configApp.getConfigGroup(CSPConstants.ConfigType.appvertx.toString()));
		optionsVertx
			.setClustered(configApp.getConfigBoolean(CSPConstants.ConfigType.app, "clustered"))
			.setClusterManager(mgr);

		initBusOptions();
		EventOptionsSet();
		initMetricsOptions();
			Vertx.clusteredVertx(optionsVertx, res -> {
			  if (res.succeeded()) {
			    vertx = res.result();
				isVertxReady = true;
				
				if (enableServerLogging) {
					//TODO
					//lD.enableServerLOG(vertx, configApp.getAddressesConfig().getString("server.log"));		
				}
				SharedData sd = vertx.sharedData();
				mapLocal = sd.getLocalMap(CSPConstants.SharedDataType.CSP_LOCAL.name());
				
				for (String fn : configApp.store.fieldNames()) {
					JsonObject mo = (JsonObject) configApp.store.getValue(fn);
					mapLocal.put(fn, mo.encodePrettily());
				}
				
			    LOGGER.info("Started clustered CSP Platform: " + this);
			    
			    initAfterVertxCreated(ar->{
			    	if(ar.succeeded()) {
					    EventVertxCreatedSuccess();
			    		
			    	}else {
						  LOGGER.warn("Failed to initAfterVertxCreated CSP Platform: " + this.getClass().getName()+"@"+ ar.cause());
						  EventVertxCreatedFailed();		    		
			    	}
			    });

			    resultHandler.handle(Future.succeededFuture());
				        
			  } else {
				  LOGGER.warn("Failed to start CSP Platform: " + this.getClass().getName()+"@"+ res.cause());
				  EventVertxCreatedFailed();
					resultHandler.handle(Future.failedFuture(res.cause()));

			  }
			});		
			
	}

	// To override by Parent


	public void EventConfigLoaded() {if (enableConsoleOutput ) LOGGER.info("ConfigLoaded " );}	
	public void EventOptionsSet() {if (enableConsoleOutput ) LOGGER.info("OptionsSet " );}	
	public void EventVertxCreatedSuccess() {if (enableConsoleOutput ) LOGGER.info("VerticleCreatedSuccess " );}
	public void EventVertxCreatedFailed() {if (enableConsoleOutput ) LOGGER.info("VerticleCreatedFailed " );}
	
	private final void initConfig(){
		configApp = new ConfigApp();	
		configApp.init("csp.config.app", vertx);
	}


	private final void initMetricsOptions(){
		optionsMetrics = new DropwizardMetricsOptions();
		optionsMetrics.setEnabled(configApp.getConfigBoolean(CSPConstants.ConfigType.app, "metrics.enable"));
		optionsMetrics.setJmxEnabled(configApp.getConfigBoolean(CSPConstants.ConfigType.app, "metrics.jmx.enable"));
		optionsMetrics.setBaseName(configApp.getConfigString(CSPConstants.ConfigType.app, "metrics.name"));
		optionsMetrics.setJmxDomain(configApp.getConfigString(CSPConstants.ConfigType.app, "metrics.jmx.domain"));
		
		optionsMetrics.addMonitoredHttpServerUri(new Match().setValue(configApp.getConfigString(CSPConstants.ConfigType.app, "http.server.monitored.handler.match")));
		optionsMetrics.addMonitoredEventBusHandler(new Match().setValue(configApp.getConfigString(CSPConstants.ConfigType.app, "bus.monitored.handler.match")));
		
		optionsVertx.setMetricsOptions(optionsMetrics);
	}
	
	private final void initBusOptions(){
		optionsBus = new EventBusOptions(configApp.getConfigGroup("server.bus"));
		optionsVertx.setEventBusOptions(optionsBus);     
	}

	
	


}
