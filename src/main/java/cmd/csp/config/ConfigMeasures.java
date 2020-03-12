package cmd.csp.config;

import cmd.csp.base.BaseConfig;
import cmd.csp.interfaces.IConfig;
import cmd.csp.platform.CSPConstants;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ConfigMeasures extends BaseConfig implements IConfig {

	@Override
	public boolean init(String name, Vertx vertx) {
		super.init(this.getClass().getSimpleName().toLowerCase(), vertx);
		JsonObject jCon = this.addNewConfigGroup(this.getName());
		
		jCon.put("status.in", "400000");
		jCon.put("status.out", "500000");

		jCon.put("pattern.measures", "(?x)\\d+(?:\\.\\d+)?\\s+(?:(?:fl )?oz(?:\\.|\\b)|lbs?(?:\\.|\\b)|kg(?:\\.|\\b)|kg?\\b|g(?:\\.|\\b)| pc?k(?:\\.|\\b)|ea(?:\\.|\\b)|ml(?:\\.|\\b)|[cq]t(?:\\.|\\b)| liter\\b|ltr(?:\\.|\\b))");
	
		
		// #############################
		// final copy store to default
		// #############################
		this.storeDefault=this.store.copy();
		
		
		return true;
	}



}
