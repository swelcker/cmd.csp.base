package cmd.csp.config;

import cmd.csp.base.BaseConfig;
import cmd.csp.interfaces.IConfig;
import cmd.csp.platform.CSPConstants;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ConfigConverter extends BaseConfig implements IConfig {

	@Override
	public boolean init(String name, Vertx vertx) {
		super.init(this.getClass().getSimpleName().toLowerCase(), vertx);
		JsonObject jCon = this.addNewConfigGroup(this.getName());
		
		jCon.put("status.in", "250000");
		jCon.put("status.out", "300000");
		jCon.put("batch.amount", 10);
		jCon.put(CSPConstants.INTERVAL, 20000);

		jCon.put("textfile.enable", true);
		jCon.put("hastext.threshold", 100);

		jCon.put("use.originalpdf.if.hastext.enable", true);
		jCon.put("emltomsg.enable", true);

		jCon.put("templates.root", "templates");
		jCon.put("templates.filetype.mail", ".tpl");
		jCon.put("templates.filetype.attachment", ".docx");
		jCon.put("attachment.name.inemla", "COGITO-Answer.pdf");
		jCon.put("templates.varfile.enable", true);
		jCon.put("templates.tpl.enable", true);
		jCon.put("templates.docx.enable", true);
		jCon.put("converter.docxtopdf.enable", true);
		jCon.put("templates.bylanguage.enable", true);

		
		// #############################
		// final copy store to default
		// #############################
		this.storeDefault=this.store.copy();
		
		
		return true;
	}


}
