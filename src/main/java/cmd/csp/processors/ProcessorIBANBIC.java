package cmd.csp.processors;


import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cmd.csp.base.BaseProcessor;
import cmd.csp.interfaces.IProcessor;
import cmd.csp.platform.CSPConstants;
import cmd.csp.platform.CSPResult;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ProcessorIBANBIC extends BaseProcessor implements IProcessor {

	private Boolean isInit=false;	
	private Map<String, String> lstPattern = new HashMap<String, String>();
	private JsonObject jcon=new JsonObject() ;
	private JsonArray jarr=new JsonArray() ;
	private String strConfKey = this.getClass().getSimpleName().toLowerCase().replace("processor", "config");
	
	@Override
	public void PrepProcessing(Handler<AsyncResult<Void>> resultHandler) {
		boolean ret = this.Init();
		super.PrepProcessing(resultHandler);
	}

	@Override
	public void applyConfig(JsonObject config) {

		lstPattern.clear();
		if(config.containsKey(strConfKey)) {
			jcon = config.getJsonObject(strConfKey);
			LOGGER.debug(this.getClass().getName()+":applyConfig:BeforeWith: " + jcon.encode());
			for(String field : jcon.fieldNames()) {
				if(field.toLowerCase().startsWith("pattern.")) {
					String val = jcon.getString(field);
					field = field.toLowerCase().replace("pattern.", "");
					LOGGER.debug(this.getClass().getName()+":applyConfig: " + field +": Pattern: " + val);
					lstPattern.put(field, val);
				}
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

			isInit=true;

			LOGGER.debug("CSP Application: " + this.getClass().getName() +":Loaded and initialized ");

		return isInitialized();
	}
	
	public JsonArray getResult(String strContent) {
		// System.out.println(this.getClass().getName()+":getResult: " + strContent);
		if(!isInitialized()) return null;
		if(strContent==null) return null;
		if(strContent.isEmpty()) return null;
		if(lstPattern==null) return null;
		if(lstPattern.size()<1) return null;
		jarr.clear();
		LOGGER.debug(this.getClass().getName()+":getResult:ReadyToDetect: " + strContent);
		if(strContent!=null) {
			for(String key:lstPattern.keySet()) {
				String strPat = lstPattern.get(key);
				LOGGER.debug(this.getClass().getName()+":Detect:" + key +":Pattern:" + strPat);
		        Pattern pat = Pattern.compile(strPat);
		        Matcher match = pat.matcher(strContent);
		        DetectMatch(match, key);		
			}
		}
		return jarr;
	}
	
	public Boolean isInitialized() {
		return isInit;
	}


    protected void DetectMatch(Matcher match, String strName) {
	        while(match.find()) {
	        	CSPResult jd = new CSPResult();
	        	jd.Name(strName);
	        	jd.Value(match.group());
	        	jd.Start(match.start());
	        	jd.End(match.end());
	        	jarr.add(jd.getAsJsonObject().copy());
	        	jd = null;
	        }
    }			
}

