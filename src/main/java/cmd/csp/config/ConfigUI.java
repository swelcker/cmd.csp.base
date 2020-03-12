package cmd.csp.config;

import cmd.csp.base.BaseConfig;
import cmd.csp.interfaces.IConfig;
import cmd.csp.platform.CSPConstants;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;

public class ConfigUI extends BaseConfig implements IConfig {

	@Override
	public boolean init(String name, Vertx vertx) {
		super.init(name, vertx);
		JsonObject jCon = this.addNewConfigGroup(this.getName());
		
		jCon.put(CSPConstants.SUBROOT, "/uiserver");
		HttpServerOptions hso = new HttpServerOptions();
		hso.setPort(8095);
		hso.setHost("localhost");
		jCon.mergeIn(hso.toJson());		
		
		// #############################
		// final copy store to default
		// #############################
		this.storeDefault=this.store.copy();
		
		return true;
	}



}
