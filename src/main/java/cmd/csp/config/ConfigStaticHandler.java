package cmd.csp.config;

import cmd.csp.base.BaseConfig;
import cmd.csp.interfaces.IConfig;
import cmd.csp.platform.CSPConstants;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;

public class ConfigStaticHandler extends BaseConfig implements IConfig {

	@Override
	public boolean init(String name, Vertx vertx) {
		super.init(CSPConstants.ConfigType.statichandler.toString(), vertx);

		JsonObject jCon = this.addNewConfigGroup(this.getName());
		jCon.put(CSPConstants.StaticHandlerType.webroot.toString(), "webroot");
		jCon.put(CSPConstants.StaticHandlerType.filesreadonly.toString(), true);
		jCon.put(CSPConstants.StaticHandlerType.maxageseconds.toString(), 86400); // One day
		jCon.put(CSPConstants.StaticHandlerType.enablecaching.toString(), true);
		jCon.put(CSPConstants.StaticHandlerType.enabledirectorylisting.toString(), false);
		jCon.put(CSPConstants.StaticHandlerType.directorylistingtemplate.toString(), "csp-web-directory.html");
		jCon.put(CSPConstants.StaticHandlerType.includehidden.toString(), true);
		jCon.put(CSPConstants.StaticHandlerType.cacheentrytimeout.toString(), 30000); // 30 seconds
		jCon.put(CSPConstants.StaticHandlerType.indexpage.toString(), "/index.html");
		jCon.put(CSPConstants.StaticHandlerType.maxcachesize.toString(), 10000);
		jCon.put(CSPConstants.StaticHandlerType.alwaysuseasyncfilesystem.toString(), false);
		jCon.put(CSPConstants.StaticHandlerType.enablefstuning.toString(), true);
		jCon.put(CSPConstants.StaticHandlerType.maxavgservertime.toString(), 1000000); // 1ms
		jCon.put(CSPConstants.StaticHandlerType.enablerangesupport.toString(), true);
		jCon.put(CSPConstants.StaticHandlerType.enablerootfilesystemaccess.toString(), false);
		jCon.put(CSPConstants.StaticHandlerType.enablesendvaryheader.toString(), true);
		jCon.put(CSPConstants.STATIC_RESOURCE_ROUTE, "/assets/*");
		jCon.put(CSPConstants.STATIC_RESOURCE_HANDLER, "assets");
		
		
		// #############################
		// final copy store to default
		// #############################
		this.storeDefault=this.store.copy();
		
		return true;
	}



}
