package cmd.csp.config;

import cmd.csp.base.BaseConfig;
import cmd.csp.interfaces.IConfig;
import cmd.csp.platform.CSPConstants;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ConfigLanguage extends BaseConfig implements IConfig {

	@Override
	public boolean init(String name, Vertx vertx) {
		super.init(this.getClass().getSimpleName().toLowerCase(), vertx);
		JsonObject jCon = this.addNewConfigGroup(this.getName());
		
		jCon.put("status.in", "400000");
		jCon.put("status.out", "500000");

		jCon.put("confidence.min", 0.49);
		jCon.put("probability.threshold", 0.19);
		jCon.put("leadingtext.percent", 0.5);
		jCon.put("textlength.max", 10000);
		jCon.put("minority.threshold", 0.3);
		jCon.put("treatas.leadingtext", false);
		jCon.put("treatas.shorttext", true);
		jCon.put("minority.enable", true);
		jCon.put("languages.disabled", InitDisabledLanguages());
		jCon.put("sentences.enable", true);
		jCon.put("tokens.enable", true);
		
		
		// #############################
		// final copy store to default
		// #############################
		this.storeDefault=this.store.copy();
		
		return true;
	}

	// SUPPORT FUNCTIONS #######################################
	private JsonArray InitDisabledLanguages() {
		JsonArray jarr = new JsonArray();
		jarr.add("af"); 
		jarr.add("an"); 
		jarr.add("ast"); 
		jarr.add("br"); 
		jarr.add("bn"); 
		jarr.add("ccy"); 
		jarr.add("eu"); 
		jarr.add("fa"); 
		jarr.add("ga"); 
		jarr.add("gl"); 
		jarr.add("gu"); 
		jarr.add("he"); 
		jarr.add("hi"); 
		jarr.add("ht"); 
		jarr.add("id"); 
		jarr.add("km"); 
		jarr.add("ml"); 
		jarr.add("mr"); 
		jarr.add("ms"); 
		jarr.add("ne"); 
		jarr.add("oc"); 
		jarr.add("pa"); 
		jarr.add("so"); 
		jarr.add("sw"); 
		jarr.add("ta"); 
		jarr.add("te"); 
		jarr.add("th"); 
		jarr.add("tl"); 
		jarr.add("ur"); 
		jarr.add("vi"); 
		jarr.add("wa"); 
		jarr.add("yi");
	
		return jarr;
	}
	private JsonArray InitPhoneLanguages() {
		JsonArray jarr = new JsonArray();
		jarr.add("de"); 
		jarr.add("at"); 
		jarr.add("ch"); 
	
		return jarr;
	}

}
