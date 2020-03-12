package cmd.csp.config;

import cmd.csp.base.BaseConfig;
import cmd.csp.interfaces.IConfig;
import cmd.csp.platform.CSPConstants;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ConfigEventBus extends BaseConfig implements IConfig {

	@Override
	public boolean init(String name, Vertx vertx) {
		super.init(CSPConstants.ConfigType.eventbus.toString() + "_" + name, vertx);
		JsonObject jCon = this.addNewConfigGroup(this.getName());
		
		EventBusOptions ebo = new EventBusOptions();
		ebo.setClusterPublicPort(0);
		jCon.mergeIn( ebo.toJson());
		
		
		// #############################
		// final copy store to default
		// #############################
		this.storeDefault=this.store.copy();
		
		
		return true;
	}

}
