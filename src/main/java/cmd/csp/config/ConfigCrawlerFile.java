package cmd.csp.config;

import cmd.csp.base.BaseConfig;
import cmd.csp.interfaces.IConfig;
import cmd.csp.platform.CSPConstants;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ConfigCrawlerFile extends BaseConfig implements IConfig {

	@Override
	public boolean init(String name, Vertx vertx) {
		super.init(this.getClass().getSimpleName().toLowerCase(), vertx);
		JsonObject jCon = this.addNewConfigGroup(this.getName());
		
		jCon.put("status.out", "100000");
		jCon.put("status.in", "000000");
		jCon.put(CSPConstants.INTERVAL, 10000);

		
		// TODO
		//String strXML = XML.toString(new JSONObject(jCon.encode()));
		

		jCon.put("id", "CSPFilesystemCrawler");
		jCon.put("extensions", "txt, html, pdf, tiff, zip, doc, docx");
		jCon.put("extensions.casesensitive", false);
		jCon.put("deletefileafterimport", true);
		jCon.put("maxdocuments", 3);
		
		JsonObject jConTenants = new JsonObject();
		JsonObject jConTenant = new JsonObject();
		JsonObject jConProjects = new JsonObject();
		JsonObject jConProject = new JsonObject();

		jConProjects.put("CSPDefaultProject", jConProject);
		jConTenant.put("projects", jConProject);
		jConTenants.put("CSPDefaultTenant", jConProject);
		
		JsonArray jarr = new JsonArray();
		jarr.add("./crawler/file/input/normal");
		jConProject.put("input.paths", jarr.copy());

		jarr.clear();
		jarr.add("./crawler/file/input/train");
		
		jConProject.put("train.paths", jarr.copy());

		jCon.put("tenants", jConTenants);

	


		// #############################
		// final copy store to default
		// #############################
		this.storeDefault=this.store.copy();
		
		
		return true;
	}


}
