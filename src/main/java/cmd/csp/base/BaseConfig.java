package cmd.csp.base;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;


import cmd.csp.interfaces.IConfig;
import cmd.csp.platform.CSPConstants;
import cmd.csp.platform.CSPLogDelegate;
import cmd.csp.platform.CSPMessage;
import cmd.csp.platform.CSPService;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class BaseConfig implements IConfig {
	protected JsonObject store = null;
	protected JsonObject storeDefault = null;
	protected String name="csp.config";
	protected String path="./config";
	protected Vertx vertx;
	protected boolean dirty = false;
	protected CSPLogDelegate LOGGER = new CSPLogDelegate(CSPLogDelegate.class.getName());;
	
	protected ConfigStoreOptions storeFile; 
	protected ConfigStoreOptions storeBus; 
	protected ConfigRetrieverOptions retrieverOptions;
	protected ConfigRetriever retrieverConfig;
	
	protected String messageChangeDetected = "CSP Config change detected" ;
	protected String messageUpdated = "CSP Config updated" ;
	protected String messageLoadFailed= "ERROR CSP Config file failed to load" ;
	public int scanPeriod=300000;
	
	public BaseConfig() {
		this.store = new JsonObject();
		this.storeDefault = new JsonObject();
		
	}
/*	public BaseConfig(Vertx vertx) {
		this.init(null, vertx);
	}
	public BaseConfig(Vertx vertx, JsonObject jcon) {
		this.init(null, vertx);
		this.setFromJson(jcon);
	}
	public BaseConfig(Vertx vertx, String name) {
		this.init(name, vertx);
	}
	public BaseConfig(Vertx vertx, JsonObject jcon, String name) {
		this.init(name, vertx);
		this.setFromJson(jcon);
	}*/
	@Override
	public boolean init(String name, Vertx vertx) {
		this.name=name;
		this.vertx=vertx;
		return true;
	}

	@Override
	public JsonObject getAsJson() {
		return store;
	}

	@Override
	public String getAsString() {
		return store.encodePrettily();
	}

	@Override
	public JsonObject getDefault() {
		return storeDefault;
	}

	@Override
	public boolean setFromJson(JsonObject jcon) {
		if(jcon == null) return false;
		if(jcon.isEmpty()) return false;
		if(jcon.encode().length()<1) return false;

		this.store = jcon.copy();
		return true;
	}

	@Override
	public boolean setFromString(String strJson) {
		if(strJson == null) return false;
		if(strJson.isEmpty()) return false;
		if(strJson.length()<1) return false;
		
		this.store = new JsonObject(strJson);
		return true;
	}

	@Override
	public void setName(String name) {
		this.name=name;
	}
	@Override
	public void Changed() {
	}

	@Override
	public String getName() {
		return this.name;
	}
	@Override
	public void Load() {
		try {
			if (vertx.fileSystem().existsBlocking(getConfigFilename())) {									

				JsonObject jcon = new JsonObject(new String(Files.readAllBytes(Paths.get(getConfigFilename()))));
				store.mergeIn(jcon, true);
				Changed();
			}
		} catch (IOException e) {

			e.printStackTrace();
		}		
		
	}
	public boolean isConfigFileExist() {
		boolean ret=false;
		File myfa = new File(getConfigFilename());
		
		if (myfa.exists() && myfa.isFile() && myfa.canRead()) {
			ret = true;
		} else {
			ret=false;
		}	
		return ret;
	}
	@Override
	public void Save() {
		Buffer buff =  Buffer.buffer(store.encodePrettily().getBytes(StandardCharsets.UTF_8));
		this.MakeDirs(getConfigFilename());
		vertx.fileSystem().writeFileBlocking(getConfigFilename(), buff);
		
	}
	protected void MakeDirs(String strFolderName) {	
			try {
				FileUtils.forceMkdir(new File(Paths.get(strFolderName).getParent().toString()));
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	@Override
	public void setConfigPath(String path) {
		this.path = path;
		
	}
	
	public String getConfigFilename() {
		return Paths.get(this.path, this.getName(), this.getName()+".cfg").toString();
		
	}
	@Override
	public void InitAfterVertx(Vertx vertx) {
		this.vertx=vertx;

		setStoreBus(new ConfigStoreOptions()
				.setType("event-bus")
				.setConfig(new JsonObject()
						.put("address", CSPConstants.ADDRESS_CONFIG))
				);
		if (this.isConfigFileExist()) {
			setStoreFile(new ConfigStoreOptions()
					.setType("file")
					.setConfig(new JsonObject().put("path",this.getConfigFilename())));

			setRetrieverOptions(new ConfigRetrieverOptions()
					.setScanPeriod(scanPeriod)
					.addStore(storeFile)
					.addStore(storeBus));
		}else {
			this.Save();
			setRetrieverOptions(new ConfigRetrieverOptions()
					.setScanPeriod(scanPeriod)
					.addStore(storeBus));

		}
		
		retrieverConfig = ConfigRetriever.create(vertx, this.getRetrieverOptions());
		
		Future<JsonObject> future = ConfigRetriever.getConfigAsFuture(retrieverConfig);
		future.setHandler(ar -> {
		  if (ar.failed()) {
		    // Failed to retrieve the configuration
			  LOGGER.error(messageLoadFailed+": "+ this.getConfigFilename());
		  } else {
			  LOGGER.info("ConfigRetriever->Loaded Configuration as future: "+ this.getConfigFilename());
			  store.mergeIn(ar.result(), true);
		  }
		});
		
		retrieverConfig.listen(change -> {
			  // Previous configuration
			  LOGGER.info(messageChangeDetected);
			  store = change.getNewConfiguration();
			});		
		vertx.setPeriodic(scanPeriod, h-> {
			if (dirty){
					Save();
					
					CSPMessage jo = new CSPMessage();
						jo.setActionToInfo();
						jo.setTypeAsConfig();
						jo.addData(store);
						vertx.eventBus().send( CSPConstants.ADDRESS_CONFIG, jo.toBodyString());
						dirty = false;

			}
	       });		        
		
	}

	// ###########################
	public JsonObject createServerEntry(String name, String verticleClass, String processorClass, String configClass, boolean isApiOn, String apiHost, int apiPort, String verticleType, boolean isenabled, boolean isworker, int instances ) {
		CSPService jo = new CSPService();
		jo.setName( name);
		jo.setProcessor( processorClass);
		jo.setVerticle( verticleClass);
		jo.setConfig(configClass);
		jo.setConfigKey(processorClass);
		jo.setApion(isApiOn);
		jo.setApihost(apiHost);
		jo.setApiport(apiPort);
		jo.setType(verticleType);
		jo.setEnabled(isenabled);
		jo.setWorker(isworker);
		jo.setInstances(instances);
		
		return jo.getAsJsonObject();
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
	public JsonObject getCopy () {
		return store.copy();
	}
	public void setBoolean(String strGroup, String sGroup, boolean value) {
		if (store.containsKey(strGroup.toLowerCase())) {
			store.getJsonObject(strGroup.toLowerCase()).put(sGroup, value);
		}else {
		}	
	}
	public void setInteger(String strGroup, String sGroup, Integer value) {
		if (store.containsKey(strGroup.toLowerCase())) {
			store.getJsonObject(strGroup.toLowerCase()).put(sGroup, value);
		}else {
		}	
	}
	public void setString(String strGroup, String sGroup, String value) {
		if (store.containsKey(strGroup.toLowerCase())) {
			store.getJsonObject(strGroup.toLowerCase()).put(sGroup, value);
		}else {
		}	
	}
	public boolean getBoolean(String strGroup, String sGroup) {
		if (store.containsKey(strGroup.toLowerCase())) {
			return store.getJsonObject(strGroup.toLowerCase()).getBoolean(sGroup);
		}else {
			return false;
		}	
	}
	public String getString(String strGroup, String sGroup) {
		if (store.containsKey(strGroup.toLowerCase())) {
			return store.getJsonObject(strGroup.toLowerCase()).getString(sGroup);
		}else {
			return "";
		}	
	}
	public Integer getInteger(String strGroup, String sGroup) {
		if (store.containsKey(strGroup.toLowerCase())) {
			return store.getJsonObject(strGroup.toLowerCase()).getInteger(sGroup);
		}else {
			return -1;
		}
	}
	public Object getValue(String strGroup, String sGroup) {
		if (store.containsKey(strGroup.toLowerCase())) {
			return store.getJsonObject(strGroup.toLowerCase()).getValue(sGroup);
		}else {
			return "";
		}
	}
	public Object getValueOrDef(String strGroup, String sGroup, Object def) {
		if (store.containsKey(strGroup.toLowerCase())) {
			return store.getJsonObject(strGroup.toLowerCase()).getValue(sGroup, def);
		}else {
			return def;
		}
	}


	public ConfigRetrieverOptions getRetrieverOptions() {
		return retrieverOptions;
	}
	public void setRetrieverOptions(ConfigRetrieverOptions options) {
		this.retrieverOptions = options;
	}
	public ConfigStoreOptions getStoreFile() {
		return storeFile;
	}
	public void setStoreFile(ConfigStoreOptions sFile) {
		this.storeFile = sFile;
	}
	public ConfigStoreOptions getStoreBus() {
		return storeBus;
	}
	public void setStoreBus(ConfigStoreOptions sBus) {
		this.storeBus = sBus;
	}
	public JsonObject addNewConfigGroup (String sType) {
		 JsonObject newGroup = new JsonObject();
		 store.put(sType.toLowerCase(), newGroup);
		 return newGroup;
	}
	public JsonObject getConfigGroup (String sType) {
		if (store.containsKey(sType.toLowerCase())) {
			 return store.getJsonObject(sType.toLowerCase());
		}else {
			return new JsonObject();
		}
	}	 
	public JsonObject getConfigGroupMerged (String smType, String sType) {
		if (smType.toLowerCase() == sType.toLowerCase()) {
			return getConfigGroup(sType);
		}else {
			if (store.containsKey(sType.toLowerCase())) {
				if (store.containsKey(smType.toLowerCase())) {
					JsonObject cj = new JsonObject();
					JsonObject cmj = new JsonObject();
					cj = store.getJsonObject(sType.toLowerCase()).copy();
					cmj = store.getJsonObject(smType.toLowerCase()).copy();
					cj.mergeIn(cmj, true);
					//System.out.println("Merged: " + cj.toString());
					 return cj;
					 
				}else {
					return store.getJsonObject(sType.toLowerCase());
				}			 
			}else {
				return new JsonObject();
			}			
		}

	}

	public void setConfigBoolean(CSPConstants.ConfigType cType, String sParameter, boolean value) {
		if (store.containsKey(cType.toString().toLowerCase())) {
			store.getJsonObject(cType.toString().toLowerCase()).put(sParameter, value);
			LOGGER.info("Config Parameter("+sParameter+") updated:" + value);
			dirty=true;
		}else {
	
			LOGGER.warn("Parameter not exist, setConfigBoolean:" + cType.toString().toLowerCase() + sParameter);
		}	
	}
	public void setConfigInteger(CSPConstants.ConfigType cType, String sParameter, Integer value) {
		if (store.containsKey(cType.toString().toLowerCase())) {
			store.getJsonObject(cType.toString().toLowerCase()).put(sParameter, value);
			LOGGER.info("Config Parameter("+sParameter+") updated:" + value);
			dirty=true;
		}else {
			LOGGER.warn("Parameter not exist, setConfigInteger:" + cType.toString().toLowerCase() + sParameter);
		}	
	}
	public void setConfigString(CSPConstants.ConfigType cType, String sParameter, String value) {
		if (store.containsKey(cType.toString().toLowerCase())) {
			store.getJsonObject(cType.toString().toLowerCase()).put(sParameter, value);
			LOGGER.info("Config Parameter("+sParameter+") updated:" + value);
			dirty=true;
		}else {
			LOGGER.warn("Parameter not exist, setConfigString:" + cType.toString().toLowerCase() + sParameter);
		}	
	}
	public boolean getConfigBoolean(CSPConstants.ConfigType cType, String sParameter) {
		if (store.containsKey(cType.toString().toLowerCase())) {
			return store.getJsonObject(cType.toString().toLowerCase()).getBoolean(sParameter);
		}else {
			LOGGER.warn("Parameter not exist, getConfigBoolean:" + cType.toString().toLowerCase() + sParameter);
			return false;
		}	
	}
	
	public String getConfigString(CSPConstants.ConfigType cType, String sParameter) {
		if (store.containsKey(cType.toString().toLowerCase())) {
			return store.getJsonObject(cType.toString().toLowerCase()).getString(sParameter);
		}else {
			LOGGER.warn("Parameter not exist, getConfigString:" + cType.toString().toLowerCase() + sParameter);
			return "";
		}	
	}

	public Integer getConfigInteger(CSPConstants.ConfigType cType, String sParameter) {
		if (store.containsKey(cType.toString().toLowerCase())) {
			return store.getJsonObject(cType.toString().toLowerCase()).getInteger(sParameter);
		}else {
			LOGGER.warn("Parameter not exist, getConfigInteger:" + cType.toString().toLowerCase() + sParameter);
			return -1;
		}
	}

	public Object getConfigValue(CSPConstants.ConfigType cType, String sParameter) {
		if (store.containsKey(cType.toString().toLowerCase())) {
			return store.getJsonObject(cType.toString().toLowerCase()).getValue(sParameter);
		}else {
			LOGGER.warn("Parameter not exist, getConfigValue:" + cType.toString().toLowerCase() + sParameter);
			return "";
		}
	}

	public Object getConfigValueOrDef(CSPConstants.ConfigType cType, String sParameter, Object def) {
		if (store.containsKey(cType.toString().toLowerCase())) {
			return store.getJsonObject(cType.toString().toLowerCase()).getValue(sParameter, def);
		}else {
			LOGGER.warn("Parameter not exist, getConfigValueOrDef:" + cType.toString().toLowerCase() + sParameter);
			return def;
		}
	}
	@Override
	public void setLogger(CSPLogDelegate logger) {
		this.LOGGER = logger;
		
	}

}
