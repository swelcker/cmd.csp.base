package cmd.csp.processors;


import org.modeshape.jcr.ModeShapeEngine;

import cmd.csp.base.BaseProcessor;
import cmd.csp.interfaces.IProcessor;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ProcessorRepository extends BaseProcessor implements IProcessor {

	private Boolean isInit=false;	
	private JsonObject jcon=new JsonObject() ;
	private JsonArray jarr=new JsonArray() ;
	private String strConfKey = this.getClass().getSimpleName().toLowerCase().replace("processor", "config");
	private ModeShapeEngine engine = null;
	
	@Override
	public void PrepProcessing(Handler<AsyncResult<Void>> resultHandler) {
		boolean ret = this.Init();
		super.PrepProcessing(resultHandler);
	}

	@Override
	public void applyConfig(JsonObject config) {

		if(config.containsKey(strConfKey)) {
			jcon = config.getJsonObject(strConfKey);
			LOGGER.debug(this.getClass().getName()+":applyConfig:BeforeWith: " + jcon.encode());
			for(String field : jcon.fieldNames()) {

				
			}
		}

		super.applyConfig(config);
	}

	@Override
	public void setInput(JsonObject content) {
		super.setInput(content);
	}
	
	@Override
	public void Process(Handler<AsyncResult<JsonObject>> resultHandler) {
		jarr = new JsonArray();
		jcon = new JsonObject();
		
		boolean ret = this.Init();
		JsonArray Result=null;
		String Input="";
		
		for(String field : input.fieldNames()) {
				String val = input.getString(field);
				Input = Input + " "+ val;
		}
		Input = Input.trim();

		Result = getResult(Input);
		result.put(this.getClass().getSimpleName().toLowerCase().replace("processor", ""), Result);
		super.Process(resultHandler);
	}

	@Override
	public JsonObject getResult() {
		return super.getResult();
	}

	@Override
	public void PostProcessing(Handler<AsyncResult<Void>> resultHandler) {
		super.PostProcessing(resultHandler);
	}

	// ############################### 

	public Boolean Init() {
		if(isInit) return true;
			engine = new ModeShapeEngine();
			isInit=true;

			LOGGER.debug("CSP Application: " + this.getClass().getName() +":Loaded and initialized ");

		return isInitialized();
	}
	
	public JsonArray getResult(String strContent) {
		// System.out.println(this.getClass().getName()+":getResult: " + strContent);
		if(!isInitialized()) return null;
		if(strContent==null) return null;
		if(strContent.isEmpty()) return null;
		jarr.clear();
		LOGGER.debug(this.getClass().getName()+":getResult:ReadyToGetResult: " + strContent);
		if(strContent!=null) {

		}
		return jarr;
	}
	
	public Boolean isInitialized() {
		return isInit;
	}

		
}

