package cmd.csp.config;

import cmd.csp.base.BaseConfig;
import cmd.csp.interfaces.IConfig;
import cmd.csp.platform.CSPConstants;
import cmd.csp.utils.WFLItem;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.types.MessageSource;

public class ConfigApp extends BaseConfig implements IConfig {




	@Override
	public boolean init(String name, Vertx vertx) {
		super.init(CSPConstants.ConfigType.app.toString(), vertx);
		JsonObject jCon=null;
		
		jCon = this.addNewConfigGroup(this.getName());
				
		jCon.put("clustered", true);
		jCon.put("maxverticlesperserver", 16);
		jCon.put(CSPConstants.INTERVAL_HEARTBEAT, 60000);
		jCon.put(CSPConstants.INTERVAL_CONFIG, 300000);
		jCon.put("log.console.enable", false);
		jCon.put("log.serverlog.enable", false);
		jCon.put("log.serverlog.periodicflush.enable", true);
		jCon.put("log.serverlog.levelpublish.enable", true);
		jCon.put("log.serverlog.buffered", true);
		jCon.put("log.serverlog.path", "c:/cspbin/log/");
		jCon.put("log.serverlog.filename", "csp_server.log");
		jCon.put("log.serverlog.level.available", "TRACE, DEBUG, INFO, WARNING, ERROR, OFF");
		jCon.put("log.serverlog.level", "INFO");
		jCon.put("log.serverlog.format", "{date:yyyy-MM-dd HH:mm:ss}:{level}:[{pid}@{verticlename}@{threadid}@{callerinfo}]:{message} ({parameter})");
		jCon.put("log.serverlog.maxstacktraceelements", 40);
		jCon.put("log.serverlog.filepolicy.flushinterval", 5000);
		jCon.put("log.serverlog.filepolicy.entrycount", 10000);
		jCon.put("log.serverlog.filepolicy.sizekb", 5000);
		jCon.put("log.serverlog.filepolicy.startup", true);
		jCon.put("log.serverlog.filepolicy.hourly", false);
		jCon.put("log.serverlog.filepolicy.daily", true);
		jCon.put("log.serverlog.filepolicy.weekly", false);
		jCon.put("log.serverlog.filepolicy.monthly", false);
		jCon.put("log.serverlog.filepolicy.yearly", false);
		jCon.put("log.serverlog.filepolicy.backupcount", 300);
		jCon.put("bridge.js.enable", true);
		jCon.put("bus.monitored.handler.match", "*");
		jCon.put("http.server.monitored.handler.match", "/*");
		jCon.put("services.timeout", 30000);
		jCon.put("metrics.enable", true);
		jCon.put("metrics.name", "cspMetrics");
		jCon.put("metrics.jmx.enable", true);
		jCon.put("metrics.jmx.domain", "cspJMX");
		jCon.put("host", "127.0.0.1");
		jCon.put("batch.root", "C:/csp/");
		jCon.put("lock.sign", "XLCK_");		


		
		// definition
		jCon = this.addNewConfigGroup(CSPConstants.ConfigType.definition.toString());

		//jCon.put("server.bus", ControllerConstants.CORECLASSNAME+ ".server.ServerBUS");
		jCon.put("service.tokenizer", this.createServerEntry("service.tokenizer", "BaseVerticle", "ProcessorTokenizer", "ConfigTokenizer", true, "localhost", 8100, MessageSource.TYPE, true,true, 1));
		jCon.put("service.gender", this.createServerEntry("service.gender","BaseVerticle", "ProcessorGender", "ConfigGender", true, "127.0.0.1", 8101, MessageSource.TYPE, true,false, 1));
		jCon.put("service.language", this.createServerEntry("service.language", "BaseVerticle", "ProcessorLanguage", "ConfigLanguage", true, "localhost", 8102, MessageSource.TYPE, true,false, 1));
		jCon.put("service.salutation", this.createServerEntry("service.salutation", "BaseVerticle", "ProcessorSalutation", "ConfigSalutation", true, "localhost", 8103, MessageSource.TYPE, true,false, 1));
		jCon.put("service.ibanbic", this.createServerEntry("service.ibanbic", "BaseVerticle", "ProcessorIBANBIC", "ConfigIBANBIC", true, "localhost", 8104, MessageSource.TYPE, true,false, 1));
		
		jCon.put("service.currency", this.createServerEntry("service.currency", "BaseVerticle", "ProcessorCurrency", "ConfigCurrency", true, "localhost", 8105, MessageSource.TYPE, true,false, 1));
		jCon.put("service.ip", this.createServerEntry("service.ip", "BaseVerticle", "ProcessorIP", "ConfigIP", true, "localhost", 8106, MessageSource.TYPE, true,false, 1));
		jCon.put("service.isbn", this.createServerEntry("service.isbn", "BaseVerticle", "ProcessorISBN", "ConfigISBN", true, "localhost", 8107, MessageSource.TYPE, true,false, 1));
		jCon.put("service.measures", this.createServerEntry("service.measures", "BaseVerticle", "ProcessorMeasures", "ConfigMeasures", true, "localhost", 8108, MessageSource.TYPE, true,false, 1));
		jCon.put("service.url", this.createServerEntry("service.url", "BaseVerticle", "ProcessorURL", "ConfigURL", true, "localhost", 8109, MessageSource.TYPE, true,false, 1));
		jCon.put("service.vat", this.createServerEntry("service.vat", "BaseVerticle", "ProcessorVAT", "ConfigVAT", true, "localhost", 8110, MessageSource.TYPE, true,false, 1));
		jCon.put("service.email", this.createServerEntry("service.email", "BaseVerticle", "ProcessorEMAIL", "ConfigEMail", true, "localhost", 8111, MessageSource.TYPE, true,false, 1));
		jCon.put("service.creditcards", this.createServerEntry("service.creditcards", "BaseVerticle", "ProcessorCreditCards", "ConfigCreditCards", true, "localhost", 8112, MessageSource.TYPE, true,false, 1));

		jCon.put("service.regex", this.createServerEntry("service.regex", "BaseVerticle", "ProcessorRegEX", "ConfigRegEX", true, "localhost", 8113, MessageSource.TYPE, true,false, 1));
		jCon.put("service.repository", this.createServerEntry("service.repository", "BaseVerticle", "ProcessorRepository", "ConfigRepository", true, "localhost", 8199, MessageSource.TYPE, true,true, 1));
		jCon.put("service.archive", this.createServerEntry("service.archive", "BaseVerticle", "ProcessorArchive", "ConfigArchive", true, "localhost", 8114, MessageSource.TYPE, true,true, 1));

		jCon.put("service.crawlerfile", this.createServerEntry("service.crawlerfile", "BaseVerticleLooper", "ProcessorCrawlerFile", "ConfigCrawlerFile", true, "localhost", 8114, MessageSource.TYPE, true,true, 1));

		
		
		// addresses
		jCon = this.addNewConfigGroup(CSPConstants.ConfigType.addresses.toString());
		jCon.put("service.tokenizer", "csp.tokenizer");
		jCon.put("service.gender", "csp.gender");
		jCon.put("service.language", "csp.language");
		jCon.put("service.salutation", "csp.salutation");
		jCon.put("service.ibanbic", "csp.ibanbic");

		jCon.put("service.currency", "csp.currency");
		jCon.put("service.ip", "csp.ip");
		jCon.put("service.isbn", "csp.isbn");
		jCon.put("service.measures", "csp.measures");
		jCon.put("service.url", "csp.url");
		jCon.put("service.vat", "csp.vat");
		jCon.put("service.email", "csp.email");
		jCon.put("service.creditcards", "csp.creditcards");

		jCon.put("service.regex", "csp.regex");
		jCon.put("service.repository", "csp.repository");
		jCon.put("service.archive", "csp.archive");

		jCon.put("service.crawlerfile", "csp.crawlerfile");


		jCon.put("bridge.js", "csp.bridge.js");
		jCon.put("manager", "csp.manager");
		jCon.put(CSPConstants.ADDRESS_CONFIG, "csp.config");
		jCon.put(CSPConstants.DISCOVERY_CONFIG, "csp.discovery");
		jCon.put(CSPConstants.HEALTH_CONFIG, "csp.health");

		

		
		// vertx
		jCon = this.addNewConfigGroup(CSPConstants.ConfigType.appvertx.toString());

		jCon.put("eventLoopPoolSize",16);
		jCon.put("workerPoolSize",20);
		jCon.put("internalBlockingPoolSize",20);
		jCon.put("blockedThreadCheckInterval",1000);
		jCon.put("maxEventLoopExecuteTime",2000000000);
		jCon.put("maxWorkerExecuteTime",2140000000);
		jCon.put("clusterManager", "");
		jCon.put("haEnabled",false);
		jCon.put("fileCachingEnabled",true);
		jCon.put("preferNativeTransport",false);
		jCon.put("quorumSize",1);
		jCon.put("haGroup", "cspHA");	


		
		jCon = this.addNewConfigGroup(CSPConstants.ConfigType.workflows.toString());		
		JsonArray jarr = new JsonArray();
		JsonObject jo = new JsonObject();
		new WFLItem("server.pop", true, "000000", "200000").addToArray(jarr);
		new WFLItem("server.preconverter", true, "200000", "250000").addToArray(jarr);
		new WFLItem("server.smtp", true, "800000", "900000").addToArray(jarr);
		new WFLItem("rule.move", true, "100000", "200000").addToArray(jarr);

		jo.put("description", "CSP Demo - EMail Cat/Ext");
		jo.put("wflitems", jarr);


		jCon.put("csp", jo.copy());

		
		// #############################
		// final copy default to store
		// #############################
		this.storeDefault=this.store.copy();
		
		
		return true;
	}
	
	
	public JsonObject getAppConfig() {
		return this.getConfigGroup(CSPConstants.ConfigType.app.toString());
	}
	public JsonObject getDefinitionConfig() {
		return this.getConfigGroup(CSPConstants.ConfigType.definition.toString());
	}
	public JsonObject getAddressesConfig() {
		return this.getConfigGroup(CSPConstants.ConfigType.addresses.toString());
	}
	public JsonObject getWorkflowsConfig() {
		return this.getConfigGroup(CSPConstants.ConfigType.workflows.toString());
	}
	public String getBusAddressFor(String sServer) {
		if (getAddressesConfig().containsKey(sServer)) {
			return getAddressesConfig().getString(sServer);
		}else {
			return "";
		}
	}
	public JsonObject getServerListForDeployment() {
		JsonObject jServer = new JsonObject();
		this.getConfigGroup(CSPConstants.ConfigType.definition.toString()).forEach(ctemp->{
				jServer.put(ctemp.getKey(), ctemp.getValue());
		});	
		return jServer;		
	}
	public JsonObject getServerListForDefinition() {
		JsonObject jServer = new JsonObject();
		this.getConfigGroup(CSPConstants.ConfigType.definition.toString()).forEach(ctemp->{
				jServer.put(ctemp.getValue().toString(), ctemp.getKey());
		});	
		return jServer;		
	}

}
