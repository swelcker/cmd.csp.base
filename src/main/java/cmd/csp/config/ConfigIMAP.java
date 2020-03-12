package cmd.csp.config;

import cmd.csp.base.BaseConfig;
import cmd.csp.interfaces.IConfig;
import cmd.csp.platform.CSPConstants;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ConfigIMAP extends BaseConfig implements IConfig {

	@Override
	public boolean init(String name, Vertx vertx) {
		super.init(this.getClass().getSimpleName().toLowerCase(), vertx);
		JsonObject jCon = this.addNewConfigGroup(this.getName());
		
		jCon.put("status.in", "000000");
		jCon.put("status.out", "200000");
		jCon.put(CSPConstants.INTERVAL, 20000);

		jCon.put("delete.after.receive", true);
		jCon.put("auth.enable", false);
		jCon.put("host", "imap.esde.onl");
		jCon.put("port", 143);		
		jCon.put("user", "maildemo@esde.onl");
		jCon.put("pwd", "Esde@69115");
		jCon.put("mbox", "INBOX");
		jCon.put("batch.amount", 5);
		jCon.put("files.store.incsp.enable", false);
		jCon.put("files.store.infolder.enable", true);
		jCon.put("detection.gender.enable", true);
		
		// #############################
		// final copy store to default
		// #############################
		this.storeDefault=this.store.copy();
		
		
		return true;
	}


}
