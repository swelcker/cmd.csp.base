package cmd.csp.config;

import cmd.csp.base.BaseConfig;
import cmd.csp.interfaces.IConfig;
import cmd.csp.platform.CSPConstants;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ConfigCurrency extends BaseConfig implements IConfig {

	@Override
	public boolean init(String name, Vertx vertx) {
		super.init(this.getClass().getSimpleName().toLowerCase(), vertx);
		JsonObject jCon = this.addNewConfigGroup(this.getName());
		
		jCon.put("status.in", "400000");
		jCon.put("status.out", "500000");

		jCon.put("pattern.currency", "[+-]?[0-9]{1,3}(?:[0-9]*(?:[.,][0-9]{0,2})?|(?:,[0-9]{3})*(?:\\.[0-9]{0,2})?|(?:\\.[0-9]{3})*(?:,[0-9]{0,2})?)");
		
		
		// #############################
		// final copy store to default
		// #############################
		this.storeDefault=this.store.copy();
		
		
		return true;
	}


}