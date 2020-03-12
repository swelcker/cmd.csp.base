package cmd.csp.config;

import cmd.csp.base.BaseConfig;
import cmd.csp.interfaces.IConfig;
import cmd.csp.platform.CSPConstants;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ConfigArchive extends BaseConfig implements IConfig {

	@Override
	public boolean init(String name, Vertx vertx) {
		super.init(this.getClass().getSimpleName().toLowerCase(), vertx);
		JsonObject jCon = this.addNewConfigGroup(this.getName());
		
		jCon.put("status.in", "950000");
		jCon.put("status.out", "999000");
		jCon.put(CSPConstants.INTERVAL, 20000);
		
		jCon.put("archive.root", "./archive");
		// AR, CPIO, JAR, DUMP, SEVEN, TAR, ZIP
		jCon.put("type", "tar");
		
		// BZIP2, GZIP, PACK200, XZ
		jCon.put("compression.type", "gzip");
		jCon.put("compression.enabled", true);

		
		// #############################
		// final copy store to default
		// #############################
		this.storeDefault=this.store.copy();
		
		
		return true;
	}


}
