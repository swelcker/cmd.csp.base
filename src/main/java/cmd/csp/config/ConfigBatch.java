package cmd.csp.config;

import cmd.csp.base.BaseConfig;
import cmd.csp.interfaces.IConfig;
import cmd.csp.platform.CSPConstants;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ConfigBatch extends BaseConfig implements IConfig {

	@Override
	public boolean init(String name, Vertx vertx) {
		super.init(this.getClass().getSimpleName().toLowerCase(), vertx);
		JsonObject jCon = this.addNewConfigGroup(this.getName());
		
		jCon.put("status.out", "100000");
		jCon.put("status.in", "000000");
		jCon.put("batch.amount", 10);
		jCon.put(CSPConstants.INTERVAL, 20000);
		jCon.put("lock.resolve.enable", true);
		jCon.put("lock.resolve.timelap", 100000);

		
		// #############################
		// final copy store to default
		// #############################
		this.storeDefault=this.store.copy();
		
		
		return true;
	}


}
