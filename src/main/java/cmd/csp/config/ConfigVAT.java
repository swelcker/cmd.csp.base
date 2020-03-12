package cmd.csp.config;

import cmd.csp.base.BaseConfig;
import cmd.csp.interfaces.IConfig;
import cmd.csp.platform.CSPConstants;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ConfigVAT extends BaseConfig implements IConfig {

	@Override
	public boolean init(String name, Vertx vertx) {
		super.init(this.getClass().getSimpleName().toLowerCase(), vertx);
		JsonObject jCon = this.addNewConfigGroup(this.getName());
		
		jCon.put("status.in", "400000");
		jCon.put("status.out", "500000");

		jCon.put("pattern.vat", "((DK|FI|HU|LU|MT|SI)(-)?\\d{8})|((BE|EE|DE|EL|LT|PT)(-)?\\d{9})|((PL|SK)(-)?\\d{10})|((IT|LV)(-)?\\d{11})|((LT|SE)(-)?\\d{12})|(AT(-)?U\\d{8})|(CY(-)?\\d{8}[A-Z])|(CZ(-)?\\d{8,10})|(FR(-)?[\\dA-HJ-NP-Z]{2}\\d{9})|(IE(-)?\\d[A-Z\\d]\\d{5}[A-Z])|(NL(-)?\\d{9}B\\d{2})|(ES(-)?[A-Z\\d]\\d{7}[A-Z\\d])");
		
		
		// #############################
		// final copy store to default
		// #############################
		this.storeDefault=this.store.copy();
		
		return true;
	}

}
