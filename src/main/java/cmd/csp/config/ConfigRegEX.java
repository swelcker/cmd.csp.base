package cmd.csp.config;

import cmd.csp.base.BaseConfig;
import cmd.csp.interfaces.IConfig;
import cmd.csp.platform.CSPConstants;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ConfigRegEX extends BaseConfig implements IConfig {

	@Override
	public boolean init(String name, Vertx vertx) {
		super.init(this.getClass().getSimpleName().toLowerCase(), vertx);
		JsonObject jCon = this.addNewConfigGroup(this.getName());
		
		jCon.put("status.in", "400000");
		jCon.put("status.out", "500000");

		jCon.put("pattern.htmltag", " \\b<([a-z]+)([^<]+)*(?:>(.*)<\\/\\1>|\\s+\\/>)\\b");
		jCon.put("pattern.dateUS", " \\b(((0[13578]|(10|12))[/](0[1-9]|[1-2][0-9]|3[0-1]))|(02/(0[1-9]|[1-2][0-9]))|((0[469]|11)[/](0[1-9]|[1-2][0-9]|30)))/[0-9]{4}\\b");
		jCon.put("pattern.dateEU", " \\b(((0[1-9]|[1-2][0-9]|3[0-1]))|(02/(0[1-9]|[1-2][0-9]))|((0[469]|11)[-.](0[13578]|(10|12))[-.](0[1-9]|[1-2][0-9]|30)))/[0-9]{4}\\b");
		jCon.put("pattern.time", " \\b(20|21|22|23|[01]\\d|\\d)((:[0-5]\\d){1,2})\\b");
		jCon.put("pattern.percent", " \\b-?[0-9]{0,2}(\\.[0-9]{1,2})?%?$|^-?(100)(\\.[0]{1,2})?%?\\b");
		jCon.put("pattern.color", " \\b#?([a-f0-9]{6}|[a-f0-9]{3})\\b");
		jCon.put("pattern.plz", " \\b((?:0[1-46-9]\\d{3})|(?:[1-357-9]\\d{4})|(?:[4][0-24-9]\\d{3})|(?:[6][013-9]\\d{3}))\\b");
		
		
		
		// #############################
		// final copy store to default
		// #############################
		this.storeDefault=this.store.copy();
		
		
		return true;
	}


}
