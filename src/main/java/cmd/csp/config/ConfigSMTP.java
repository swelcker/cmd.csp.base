package cmd.csp.config;

import cmd.csp.base.BaseConfig;
import cmd.csp.interfaces.IConfig;
import cmd.csp.platform.CSPConstants;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;

public class ConfigSMTP extends BaseConfig implements IConfig {

	@Override
	public boolean init(String name, Vertx vertx) {
		super.init(name, vertx);
		JsonObject jCon = this.addNewConfigGroup(this.getName());
		
		jCon.put("status.in", "800000");
		jCon.put("status.out", "900000");

		jCon.put("host", "smtp.esde.onl");
		jCon.put("port", 25);		
		jCon.put("auth.enable", false);
		jCon.put("send.err.message", false);
		jCon.put("user", "maildemo@esde.onl");
		jCon.put("pwd", "Esde@69115");
		jCon.put("recipient.cc", "");
		jCon.put("recipient.bcc", "");
		jCon.put("domain", "esde.onl");
		
		// #############################
		// final copy store to default
		// #############################
		this.storeDefault=this.store.copy();
		
		
		return true;
	}



}
