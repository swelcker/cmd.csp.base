package cmd.csp.platform;

import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class CSPService {

	private String name="";
	private String processor="";
	private String verticle="BaseVerticle";
	private String config="";
	private String configkey="";
	private boolean apion=false;
	private String apihost="";
	private Integer apiport=0;
	private boolean enabled=false;
	private boolean worker=false;
	private Integer instances=1;
	private String type="";
	private HttpServerOptions hso = new HttpServerOptions();
	public CSPService() {
		// TODO Auto-generated constructor stub
	}
	public CSPService(JsonObject jsonObj) {
		if(jsonObj.containsKey("name")) this.name=(jsonObj.getString("name"));
		if(jsonObj.containsKey("processor")) this.processor=(jsonObj.getString("processor"));
		if(jsonObj.containsKey("verticle")) this.verticle=(jsonObj.getString("verticle"));
		if(jsonObj.containsKey("config")) this.config=(jsonObj.getString("config"));
		if(jsonObj.containsKey("configkey")) this.configkey=(jsonObj.getString("configkey"));
		if(jsonObj.containsKey("api.on")) this.apion=(jsonObj.getBoolean("api.on"));
		if(jsonObj.containsKey("api.host")) this.apihost=(jsonObj.getString("api.host"));
		if(jsonObj.containsKey("api.port")) this.apiport=(jsonObj.getInteger("api.port"));
		if(jsonObj.containsKey("type")) this.type=(jsonObj.getString("type"));
		if(jsonObj.containsKey("enabled")) this.enabled=(jsonObj.getBoolean("enabled"));
		if(jsonObj.containsKey("worker")) this.worker=(jsonObj.getBoolean("worker"));
		if(jsonObj.containsKey("instances")) this.instances=(jsonObj.getInteger("instances"));
		if(jsonObj.containsKey("http")) this.hso=new HttpServerOptions(jsonObj.getJsonObject("http"));
		this.apihost = hso.getHost();
		this.apiport = hso.getPort();
	}
	public JsonObject getAsJsonObject() {
		JsonObject jo = new JsonObject();
		jo.put("name",  name);
		jo.put("processor",  processor);
		jo.put("verticle",  verticle);
		jo.put("config",  config);
		jo.put("configkey",  configkey);
		jo.put("api.on", enabled);
		jo.put("api.host",  apihost);
		jo.put("api.port", apiport);
		jo.put("type", type);
		jo.put("enabled", enabled);
		jo.put("worker", worker);
		jo.put("instances", instances);
		jo.put("http", hso.toJson());
		
		return jo;
	}
	public HttpServerOptions getHttpConfig() {
		return hso;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVerticle() {
		return verticle;
	}
	public void setVerticle(String verticle) {
		this.verticle = CSPConstants.CORECLASSNAME+ ".base." + verticle;
	}
	public String getProcessor() {
		return processor;
	}
	public void setProcessor(String processor) {
		this.processor = CSPConstants.CORECLASSNAME+ ".processors." + processor;
	}
	public String getConfig() {
		return config;
	}
	public void setConfig(String config) {
		this.config = CSPConstants.CORECLASSNAME+ ".config." + config;
	}
	public String getConfigKey() {
		return configkey;
	}
	public void setConfigKey(String configkey) {
		this.configkey = configkey.toLowerCase().replace("processor", "config");
	}
	public boolean isApion() {
		return apion;
	}
	public void setApion(boolean enabled) {
		this.apion = enabled;
	}
	public String getApihost() {
		return apihost;
	}
	public void setApihost(String apihost) {
		this.apihost =  apihost;
		hso.setHost(apihost);
	}
	public Integer getApiport() {
		return apiport;
	}
	public void setApiport(Integer apiport) {
		this.apiport = apiport;
		hso.setPort(apiport);
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public boolean isWorker() {
		return worker;
	}
	public void setWorker(boolean worker) {
		this.worker = worker;
	}
	public Integer getInstances() {
		return instances;
	}
	public void setInstances(Integer instances) {
		this.instances = instances;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void addToArray(JsonArray jarr) {
		jarr.add(this.getAsJsonObject());
	}


}
