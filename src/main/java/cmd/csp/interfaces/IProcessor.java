package cmd.csp.interfaces;

import cmd.csp.base.BaseUtil;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;


public interface IProcessor {
	 default void PrepProcessing( Handler<AsyncResult<Void>> resultHandler) {
		resultHandler.handle(Future.succeededFuture());
	};
	 default void PostProcessing(Handler<AsyncResult<Void>> resultHandler) {
		resultHandler.handle(Future.succeededFuture());

	};
	 default void Process(Handler<AsyncResult<JsonObject>> resultHandler) {
		resultHandler.handle(Future.succeededFuture());

	};
	 default void StopProcessor(Handler<AsyncResult<Void>> resultHandler) {
		resultHandler.handle(Future.succeededFuture());

	};
	void applyConfig(JsonObject config);
	boolean ChckAvailability();
	
	void setVertx(Vertx vertx, BaseUtil util);
	void setInput(JsonObject content);
	void setInput(String content);
	void setInfo(JsonObject info);
	JsonObject getResult();
	JsonObject getExampleResult();
	JsonObject getExampleInput();
	
	  public interface ProcessorFactory {
		    /**
		     * Creates a new instance of {@link Rectangle}.
		     * 
		     * @return A new instance of {@link Rectangle}.
		     */
		  IProcessor create(Vertx vertx);
	  }

}
