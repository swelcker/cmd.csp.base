package cmd.csp.config;

import cmd.csp.base.BaseConfig;
import cmd.csp.interfaces.IConfig;
import cmd.csp.platform.CSPConstants;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ConfigTokenizer extends BaseConfig implements IConfig {

	@Override
	public boolean init(String name, Vertx vertx) {
		super.init(this.getClass().getSimpleName().toLowerCase(), vertx);
		JsonObject jCon = this.addNewConfigGroup(this.getName());
		
		jCon.put("status.in", "400000");
		jCon.put("status.out", "500000");

		jCon.put("normalize.enable", true);
		jCon.put("normalize.onWhitespaces", true);
		jCon.put("normalize.onFormatChars", true);
		jCon.put("normalize.onQuotesDouble", true);
		jCon.put("normalize.onQuotesSingle", true);
		jCon.put("normalize.onDash", true);
		jCon.put("normalize.onTrim", true);
		jCon.put("normalize.onUnicode", true);
		jCon.put("normalize.onUpper", false);
		jCon.put("normalize.onLower", false);
		jCon.put("normalize.onCRLF", false);
		jCon.put("normalize.onDeleteNum", false);
		jCon.put("normalize.onNormalizeNum", false);
		jCon.put("normalize.onSpecialChar", false);
		
		jCon.put("sentences.enable", false);
		jCon.put("tokens.enable", false);
		jCon.put("ngrams.enable", false);
		jCon.put("ngrams.length", 3);
		jCon.put("locale", "de_DE");
		
	
		// #############################
		// final copy store to default
		// #############################
		this.storeDefault=this.store.copy();
		
		return true;
	}


}
