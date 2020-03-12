/**
 * 
 */
package cmd.csp.base;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import cmd.csp.interfaces.IProcessor;
import cmd.csp.platform.CSPConstants;
import cmd.csp.platform.CSPDiscovery;
import cmd.csp.platform.CSPLogDelegate;
import cmd.csp.platform.CSPService;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.shiro.ShiroAuth;
import io.vertx.ext.auth.shiro.ShiroAuthOptions;
import io.vertx.ext.auth.shiro.ShiroAuthRealmType;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.FormLoginHandler;
import io.vertx.ext.web.handler.RedirectAuthHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.UserSessionHandler;
import io.vertx.ext.web.handler.sockjs.BridgeEvent;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.servicediscovery.rest.ServiceDiscoveryRestEndpoint;
/**
 * @author swelcker
 *
 */
public class BaseUtil   {


	public AuthProvider authProvider;
	public ShiroAuthOptions authOptions;
	public JsonObject authConfig;

	protected Vertx vertx=null;
	protected JsonObject config=null;
	protected CSPService service=null;
	
	protected final  Logger LOGGER = LoggerFactory.getLogger(CSPLogDelegate.class.getName());

	
	public BaseUtil(Vertx mvertx, JsonObject mconfig) {
		this.vertx = mvertx;
		this.config = mconfig;
		this.service = getServerEntry(mconfig);
		
		
	}
	
	public IProcessor  Init(Router router, CSPDiscovery ctrlDiscovery, SockJSHandler sockJSHandler) {
		ctrlDiscovery.setRecordServiceInterface(this.getClass().getName());
		ctrlDiscovery.setRecordServiceName(service.getName());
		ctrlDiscovery.setRecordType(service.getType());
		ctrlDiscovery.Init(vertx, this);
		
		IProcessor processController =null;
		// Get Class instance
		Class<?> clazz;
		try {
			clazz = Class.forName(service.getProcessor());
			Constructor<?> cons = clazz.getDeclaredConstructor();
			cons.setAccessible(true);
			processController = (IProcessor) cons.newInstance();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		

	      
		
		// MUST Stay here after ctrlDiscovery INIT
	    if (configApp().getBoolean("bridge.js.enable")) {
	    	router.route("/" + configAddresses().getString("bridge.js") + "/*").handler(sockJSHandler);			
			ServiceDiscoveryRestEndpoint.create(router, ctrlDiscovery.discovery);
	    };

		ctrlDiscovery.Publish(ar->{
			if (ar.succeeded()) {
			}else {
			}
		});
		
		return processController;

	}
	public JsonObject processRequestedConfig(String strConfig) {
		  
	       if(strConfig==null) return null;
	       if(strConfig.isEmpty()) return null;
	       if(!strConfig.contains("=")  ) return null;
	       JsonObject jcon = new JsonObject();
	       String[] tempArray;

	       /* delimiter */
	       String delimiter = ":";
	       String delimiterEntry = "=";

	       /* given string will be split by the argument delimiter provided. */
	       tempArray = strConfig.split(delimiter);
	       if(tempArray==null) tempArray =  new String [] {strConfig};
	       if(tempArray.length<1) tempArray =  new String [] {strConfig};


	       /* print substrings */
	       for (int i = 0; i < tempArray.length; i++) {
	    	   String[] entry =tempArray[i].split(delimiterEntry);
	    	   if(entry==null) entry =  new String [] {tempArray[i]};
	    	   if(entry.length<1) entry =  new String [] {tempArray[i]};
	    	   if(entry.length<2 ) {
	    		   jcon.put(entry[0], entry[0]);
	    	   }else {
	    		   if(!jcon.containsKey(entry[0]) && isInt(entry[1])) jcon.put(entry[0], Integer.parseInt(entry[1]));
	    		   if(!jcon.containsKey(entry[0]) && isDouble(entry[1])) jcon.put(entry[0], Double.parseDouble(entry[1]));
	    		   if(!jcon.containsKey(entry[0]) && isFloat(entry[1])) jcon.put(entry[0], Float.parseFloat(entry[1]));
	    		   if(!jcon.containsKey(entry[0]) && isBoolean(entry[1])) jcon.put(entry[0],  Boolean.parseBoolean(entry[1]));
	    		   if(!jcon.containsKey(entry[0])) jcon.put(entry[0],  entry[1]);
	    	   }


	       }
	       return jcon.copy();


	  }
	  public JsonObject processRequestedInput(String strInput) {
		  
	       if(strInput==null) return null;
	       if(strInput.isEmpty()) return null;
	       JsonObject jcon = new JsonObject();
	       String[] tempArray;

	       /* delimiter */
	       String delimiter = ":";
	       String delimiter2 = "@";
	       String delimiterFinal="";
	       if(strInput.contains(delimiter+delimiter+delimiter) || strInput.contains(delimiter2+delimiter2+delimiter2)) {
	    	   delimiter = delimiter+delimiter+delimiter;
	    	   delimiter2 = delimiter2+delimiter2+delimiter2;
	       }else if(strInput.contains(delimiter+delimiter) || strInput.contains(delimiter2+delimiter2)) {
	    	   delimiter = delimiter+delimiter;
	    	   delimiter2 = delimiter2+delimiter2;
	       }else {
	    	   jcon.put("0", strInput);
	    	   return jcon;
	       }
	       if(strInput.contains(delimiter)) {
	    	   delimiterFinal = delimiter;
	       }else {
	    	   delimiterFinal=delimiter2;
	       }
	       
	       /* given string will be split by the argument delimiter provided. */
	       tempArray = strInput.split(delimiterFinal);
	       if(tempArray==null) tempArray =  new String [] {strInput};
	       if(tempArray.length<1) tempArray =  new String [] {strInput};


	       /* print substrings */
	       for (int i = 0; i < tempArray.length; i++) {
    		   if(!jcon.containsKey(String.valueOf(i)) && isInt(tempArray[i])) jcon.put(String.valueOf(i), Integer.parseInt(tempArray[i]));
    		   if(!jcon.containsKey(String.valueOf(i)) && isDouble(tempArray[i])) jcon.put(String.valueOf(i), Double.parseDouble(tempArray[i]));
    		   if(!jcon.containsKey(String.valueOf(i)) && isFloat(tempArray[i])) jcon.put(String.valueOf(i), Float.parseFloat(tempArray[i]));
    		   if(!jcon.containsKey(String.valueOf(i)) && isBoolean(tempArray[i])) jcon.put(String.valueOf(i),  Boolean.parseBoolean(tempArray[i]));
    		   if(!jcon.containsKey(String.valueOf(i))) jcon.put(String.valueOf(i), tempArray[i]);

	       }
	       return jcon.copy();


	  }
	  public static boolean isInt(String s)
	  {
		  if(s==null) return false;
	   try
	    { int i = Integer.parseInt(s); return true; }

	   catch(NumberFormatException er)
	    { return false; }
	  }
	  public static boolean isDouble(String s)
	  {
		  if(s==null) return false;
	   try
	    { double i = Double.parseDouble(s); return true; }

	   catch(NumberFormatException er)
	    { return false; }
	  }	  
	  public static boolean isFloat(String s)
	  {
		  if(s==null) return false;
	   try
	    { float i = Float.parseFloat(s); return true; }

	   catch(NumberFormatException er)
	    { return false; }
	  }	  
	  public static boolean isBoolean(String s)
	  {
		  if(s==null) return false;
		  boolean i = Boolean.parseBoolean(s); 
		  if(!i) {
			  if(s.toLowerCase().contentEquals("false")) i = true;
		  }
		  return i; 
	  }	  
	  
	  /**
	   * Create http server for the REST service.
	   *
	   * @param router router instance
	   * @param host   http host
	   * @param port   http port
	   * @return async result of the procedure
	   */
	  public Future<Void> createHttpServer(Router router, String host, int port) {
	    Future<HttpServer> httpServerFuture = Future.future();
	    vertx.createHttpServer()
	      .requestHandler(router::accept)
	      .listen(port, host, httpServerFuture.completer());
	    return httpServerFuture.map(r -> null);
	  }
	


	public CSPService getServerEntry(JsonObject jcon) {
		CSPService jo;
		if (jcon.containsKey("verticle")) {
			jo = new CSPService(jcon.getJsonObject("verticle"));

		}else {
			jo = new CSPService();
		}	
		
		return jo;
	}


	  /**
	   * A helper method that publish logs on the event bus.
	   *
	   * @param type log type
	   * @param data log message data
	   */




    public String getBusAddressFor(String sName) {
    	if(configAddresses().containsKey(sName)) {
    		return 	configAddresses().getString(sName);
    	}else {
    		return "";
    	}
	}


	public final void addHTTPRoutes(Router router){

	  	  router.route().handler(CookieHandler.create());
	      router.route().handler(BodyHandler.create());
	      router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));
	      // Simple auth service which uses a properties file for user/role info
	      authOptions = new ShiroAuthOptions();
	      authConfig = new JsonObject();
	      authOptions.setType(ShiroAuthRealmType.PROPERTIES);
	      authOptions.setConfig(authConfig);
	      
	      authProvider = ShiroAuth.create(vertx, authOptions);
	      router.route().handler( UserSessionHandler.create(authProvider));
	      router.route("/").handler(RedirectAuthHandler.create(authProvider, "index.html#!/login"));
	      router.route("/#!/").handler(RedirectAuthHandler.create(authProvider, "index.html#!/login"));
	      router.route("/login").handler(FormLoginHandler.create(authProvider));


	      // Implement logout
	      router.route("/logout").handler(context -> {
	        context.clearUser();
	        context.response().putHeader("location", "/").setStatusCode(302).end();
	      });
	      
	      
			router.route(configStaticHandler().getString(CSPConstants.STATIC_RESOURCE_ROUTE)).handler(StaticHandler.create(configStaticHandler().getString(CSPConstants.STATIC_RESOURCE_HANDLER)));
			router.route().handler(StaticHandler.create()
					.setWebRoot(configStaticHandler().getString(CSPConstants.StaticHandlerType.webroot.toString(), "webroot"))
					.setFilesReadOnly(configStaticHandler().getBoolean(CSPConstants.StaticHandlerType.filesreadonly.toString(), true))
					.setMaxAgeSeconds(configStaticHandler().getInteger(CSPConstants.StaticHandlerType.maxageseconds.toString(), 86400)) // One day
					.setCachingEnabled(configStaticHandler().getBoolean(CSPConstants.StaticHandlerType.enablecaching.toString(), false))
					.setDirectoryListing(configStaticHandler().getBoolean(CSPConstants.StaticHandlerType.enabledirectorylisting.toString(), false))
					.setDirectoryTemplate(configStaticHandler().getString(CSPConstants.StaticHandlerType.directorylistingtemplate.toString(), "csp-web-directory.html"))
					.setIncludeHidden(configStaticHandler().getBoolean(CSPConstants.StaticHandlerType.includehidden.toString(), true))
					.setCacheEntryTimeout(configStaticHandler().getInteger(CSPConstants.StaticHandlerType.cacheentrytimeout.toString(), 30000)) // 30 seconds
					.setIndexPage(configStaticHandler().getString(CSPConstants.StaticHandlerType.indexpage.toString(), "/index.html"))
					.setMaxCacheSize(configStaticHandler().getInteger(CSPConstants.StaticHandlerType.maxcachesize.toString(), 10000))
					.setAlwaysAsyncFS(configStaticHandler().getBoolean(CSPConstants.StaticHandlerType.alwaysuseasyncfilesystem.toString(), false))
					.setEnableFSTuning(configStaticHandler().getBoolean(CSPConstants.StaticHandlerType.enablefstuning.toString(), true))
					.setMaxAvgServeTimeNs(configStaticHandler().getInteger(CSPConstants.StaticHandlerType.maxavgservertime.toString(), 1000000)) // 1ms
					.setEnableRangeSupport(configStaticHandler().getBoolean(CSPConstants.StaticHandlerType.enablerangesupport.toString(), true))
					.setAllowRootFileSystemAccess(configStaticHandler().getBoolean(CSPConstants.StaticHandlerType.enablerootfilesystemaccess.toString(), false))
					.setSendVaryHeader(configStaticHandler().getBoolean(CSPConstants.StaticHandlerType.enablesendvaryheader.toString(), true))					
					);
	}
	
	public void handleSocketOpenEvent(BridgeEvent be){
      String host =be.socket().remoteAddress().toString();
      String localAddress = be.socket().localAddress().toString();
      LOGGER.info("SockJS Eventbus Bridge: Connection opened: Host: " + host + " Local address: " + localAddress);
  }

  public void handleRegisterEvent(BridgeEvent be){
      String host =be.socket().remoteAddress().toString();
      String localAddress = be.socket().localAddress().toString();
      String address = be.getRawMessage().getString("address").trim();
      LOGGER.info("SockJS Eventbus Bridge: Register event: Address: " + address + " Host: " + host + " Local address: " + localAddress);
  }

  public void handleUnregisterEvent(BridgeEvent be){
      String host =be.socket().remoteAddress().toString();
      String localAddress = be.socket().localAddress().toString();
      String address = be.getRawMessage().getString("address").trim();
      LOGGER.info("SockJS Eventbus Bridge: Unregister event: Address: " + address + " Host: " + host + " Local address: " + localAddress);
  }

  public void handleSocketCloseEvent(BridgeEvent be){
      String host =be.socket().remoteAddress().toString();
      String localAddress = be.socket().localAddress().toString();
      LOGGER.info("SockJS Eventbus Bridge: Connection closed: Host: " + host + " Local address: " + localAddress);
  }

  public JsonObject configApp() {
	  //System.out.println("Config: " + config.encodePrettily());
	  if(config.containsKey("app")) {
		  return config.getJsonObject("app");
	  }else {
		  return new JsonObject();
	  }
  }
  public JsonObject configVerticle() {
	  //System.out.println("Config: " + config.encodePrettily());
	  if(config.containsKey(service.getConfigKey())) {
		  return config.getJsonObject(service.getConfigKey());
	  }else {
		  return new JsonObject();
	  }
  }
  public JsonObject configWfl() {
	  JsonObject jcon = new JsonObject();
	  jcon.put("configname", configName());
	  jcon.put("classname", className());
	  if(config.containsKey("workflows")) {
		  jcon.put("workflows",config.getJsonObject("workflows"));
	  }else {
		  jcon.put("workflows",new JsonObject());
	  }
	  return jcon;
  }
  public JsonObject configStaticHandler() {
	  if(config.containsKey("statichandler")) {
		  return config.getJsonObject("statichandler");
	  }else {
		  return new JsonObject();
	  }
  }
  public JsonObject configAddresses() {
	  if(config.containsKey("addresses")) {
		  return config.getJsonObject("addresses");
	  }else {
		  return new JsonObject();
	  }
  }
  public String configName() {
	  if(config.containsKey("configname")) {
		  return config.getString("configname");
	  }else {
		  return "";
	  }
  }
  public String className() {
	  if(config.containsKey("classname")) {
		  return config.getString("classname");
	  }else {
		  return "";
	  }
  }
  
}
