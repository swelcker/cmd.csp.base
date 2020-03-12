package cmd.csp.config;

import cmd.csp.base.BaseConfig;
import cmd.csp.interfaces.IConfig;
import cmd.csp.platform.CSPConstants;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;

public class ConfigRestCall extends BaseConfig implements IConfig {

	@Override
	public boolean init(String name, Vertx vertx) {
		super.init(name, vertx);
		JsonObject jCon = this.addNewConfigGroup(this.getName());
		
				
		jCon.put("status.in", "600000");
		jCon.put("status.out", "700000");
		jCon.put("host", "esde.onl");
		jCon.put("port", 8091);		
		jCon.put("es_port", 8820);		
		jCon.put("auth.enable", true);
		jCon.put("keepalive", true);
		jCon.put("timeout.connect", 10000);		
		jCon.put("timeout.request", 5000);		
		jCon.put("min.length.uri", 25);		
		jCon.put("user", "SuperAdmin");
		jCon.put("pwd", "Cogito");
		jCon.put("urlAPI", "/cogito/v1/annotation/annotate/");
		jCon.put("cartridge", "");
		jCon.put("procedure", "");
		jCon.put("language.fixed", "");
		jCon.put("language.usefrompreprocess", true);
		jCon.put("post.content.only", true);
		jCon.put("schema", "http");
		jCon.put("result.format", "json");
		jCon.put("cache.control", "no-cache");
		jCon.put("content.type.app", "application/json");
		jCon.put("content.type.binary", "application/octet-stream");
		jCon.put("content.type.result", "text/plain; charset=\\\"utf-8\\\"");
		jCon.put("result.storeincsp", false);
		
		// #############################
		// final copy store to default
		// #############################
		this.storeDefault=this.store.copy();
		
		
		return true;
	}



}
