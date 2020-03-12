package cmd.csp.interfaces;

import cmd.csp.platform.CSPLogDelegate;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public interface IConfig {
	boolean init(String name, Vertx vertx);
	JsonObject getAsJson();
	String getAsString();
	JsonObject getDefault();
	boolean setFromJson(JsonObject jcon);
	boolean setFromString(String strJson);
	void setName(String name);
	String getName();
	void Load();
	void Save();
	void setConfigPath(String path);
	void InitAfterVertx(Vertx vertx);
	void Changed();
	void setLogger(CSPLogDelegate logger);
}
