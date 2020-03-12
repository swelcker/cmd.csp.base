package cmd.csp.config;

import cmd.csp.base.BaseConfig;
import cmd.csp.interfaces.IConfig;
import cmd.csp.platform.CSPConstants;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ConfigVectorizer extends BaseConfig implements IConfig {

	@Override
	public boolean init(String name, Vertx vertx) {
		super.init(this.getClass().getSimpleName().toLowerCase(), vertx);
		JsonObject jCon = this.addNewConfigGroup(this.getName());
		
		jCon.put("status.in", "400000");
		jCon.put("status.out", "500000");

		jCon.put("harmonize.enable", true);
		
		jCon.put("bagofwords.enable", true);
		jCon.put("tokens.enable", true);
		jCon.put("paragraph.enable", true);
		jCon.put("sequence.enable", true);
		jCon.put("harmonize.enable", true);
		
		jCon.put("minwordfrequency", 3);
		jCon.put("layersize", 100);
		jCon.put("seed", 42);
		jCon.put("windowsize", 5);

		jCon.put("paralleltokenization.enable", false);
		jCon.put("adagrad.enable", false);
		jCon.put("unknown.enable", false);
		jCon.put("hierachicsoftmax.enable", false);
		jCon.put("preciseweight.enable", false);
		jCon.put("batchsize", 0);
		jCon.put("epochs", 1);
		jCon.put("iterations", 1);
		jCon.put("vectorlength", 150);
	
		// #############################
		// final copy store to default
		// #############################
		this.storeDefault=this.store.copy();
		
		return true;
	}


}
