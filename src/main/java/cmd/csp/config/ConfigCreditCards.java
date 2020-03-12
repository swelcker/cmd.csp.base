package cmd.csp.config;

import cmd.csp.base.BaseConfig;
import cmd.csp.interfaces.IConfig;
import cmd.csp.platform.CSPConstants;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ConfigCreditCards extends BaseConfig implements IConfig {

	@Override
	public boolean init(String name, Vertx vertx) {
		super.init(this.getClass().getSimpleName().toLowerCase(), vertx);
		JsonObject jCon = this.addNewConfigGroup(this.getName());
		
		jCon.put("status.in", "400000");
		jCon.put("status.out", "500000");

		jCon.put("pattern.visa", " \\b4[0-9]{12}(?:[0-9]{3})?\\b");
		jCon.put("pattern.master", " \\b(?:5[1-5][0-9]{2}|222[1-9]|22[3-9][0-9]|2[3-6][0-9]{2}|27[01][0-9]|2720)[0-9]{12}\\b");
		jCon.put("pattern.amex", " \\b3[47][0-9]{13}\\b");
		jCon.put("pattern.diners", " \\b3(?:0[0-5]|[68][0-9])[0-9]{11}\\b");
		jCon.put("pattern.discover", " \\b6(?:011|5[0-9]{2})[0-9]{12}\\b");
		jCon.put("pattern.jcb", " \\b(?:2131|1800|35\\d{3})\\d{11}\\b");
		
		
		// #############################
		// final copy store to default
		// #############################
		this.storeDefault=this.store.copy();
		
		
		return true;
	}


}
