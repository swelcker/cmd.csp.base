package cmd.csp.config;

import org.modeshape.jcr.RepositoryConfiguration;

import cmd.csp.base.BaseConfig;
import cmd.csp.interfaces.IConfig;
import cmd.csp.platform.CSPConstants;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public class ConfigRepository extends BaseConfig implements IConfig {

	@Override
	public boolean init(String name, Vertx vertx) {
		super.init(this.getClass().getSimpleName().toLowerCase(), vertx);
		JsonObject jCon = this.addNewConfigGroup(this.getName());
		
		jCon.put("status.in", "950000");
		jCon.put("status.out", "999000");
		jCon.put(CSPConstants.INTERVAL, 20000);
		
/*		jCon.put("repository.name", "CSPRepository");
		jCon.put("workspaces.predefined", "[]");
		jCon.put("workspaces.default", "CSPWorkspace");
		jCon.put("workspaces.allowCreation", true);
		jCon.put("security.anonymous.roles", "[\"readonly\",\"readwrite\",\"admin\"]");
		jCon.put("security.anonymous.useOnFailedLogin", false);

		jCon.put("security.providers", "[]");
		jCon.put("security.providers.roles.mappings", "[]");
		//username=password
		jCon.put("security.users.pwd", "[\"admin=admin\"]");

		jCon.put("storage.persistence.type", "file");
		jCon.put("storage.persistence.connectionUrl", "./repository/csp.repository");
		jCon.put("storage.persistence.createOnStart", true);
		jCon.put("storage.persistence.dropOnExit", true);

		jCon.put("storage.binaryStorage.type", "file");
		jCon.put("storage.binaryStorage.directory", "target/content/binaries");
		jCon.put("storage.binaryStorage.minimumBinarySizeInBytes", 999);

		jCon.put("sequencing.sequencers.images.classname", "ImageSequencer");
		jCon.put("sequencing.sequencers.images.pathExpression", "default://imagesContainer[@image] => default:/sequenced/images");
*/
		RepositoryConfiguration config  = new RepositoryConfiguration("CSPRepository");
		config.edit().editable().setString(RepositoryConfiguration.FieldName.NAME, "CSPRepository");
		config.edit().editable().setString(RepositoryConfiguration.FieldName.DEFAULT, "CSPWorkspace");
		config.edit().editable().setBoolean(RepositoryConfiguration.FieldName.ALLOW_CREATION, false);
		
		config.edit().editable().setBoolean(RepositoryConfiguration.FieldName.USE_ANONYMOUS_ON_FAILED_LOGINS, true);
		config.edit().apply(config.edit().getChanges());
		RepositoryConfiguration configM  = new RepositoryConfiguration(config.edit(), "CSPRepository");
		
		jCon.put("repository.config", new JsonObject(configM.toString()));
		config = null;
		configM = null;
		
		
		// #############################
		// final copy store to default
		// #############################
		this.storeDefault=this.store.copy();
		
		
		return true;
	}



}
