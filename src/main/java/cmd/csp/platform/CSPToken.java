package cmd.csp.platform;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class CSPToken {
	public String ID="";
	public String Value="";
	public String Normalized="";
	public String Type="";
	public String NGrams="";
	public String Stemmed="";
	public Integer SentenceID=0;
	public Integer Start=0;
	public Integer End=0;
	public Integer StartInSentence=0;
	public Integer EndInSentence=0;
	public Double Score=0.;
	public Double Partition=0.;
	public Integer Length=0;
	public Integer FrequencyOverall=0;
	public Integer FrequencySentence=0;
	public boolean isKeyword=false;
	public boolean isMin=false;
	
	
	public CSPToken() {
		// TODO Auto-generated constructor stub
	}
	public CSPToken(JsonObject jsonObj) {
		if(jsonObj.containsKey("id")) this.ID=(jsonObj.getString("id"));
		if(jsonObj.containsKey("value")) this.Value=(jsonObj.getString("value"));
		if(jsonObj.containsKey("normalized")) this.Normalized=(jsonObj.getString("normalized"));
		if(jsonObj.containsKey("type")) this.Type=(jsonObj.getString("type"));
		if(jsonObj.containsKey("sentenceid")) this.SentenceID=(jsonObj.getInteger("sentenceid"));
		if(jsonObj.containsKey("start")) this.Start=(jsonObj.getInteger("start"));
		if(jsonObj.containsKey("end")) this.End=(jsonObj.getInteger("end"));
		if(jsonObj.containsKey("startinsentence")) this.StartInSentence=(jsonObj.getInteger("startinsentence"));
		if(jsonObj.containsKey("endinsentence")) this.EndInSentence=(jsonObj.getInteger("endinsentence"));
		if(jsonObj.containsKey("score")) this.Score=(jsonObj.getDouble("score"));
		if(jsonObj.containsKey("partition")) this.Partition=(jsonObj.getDouble("partition"));
		if(jsonObj.containsKey("length")) this.Length=jsonObj.getInteger("length");
		if(jsonObj.containsKey("ngrams")) this.NGrams=(jsonObj.getString("ngrams"));
		if(jsonObj.containsKey("stemmed")) this.Stemmed=(jsonObj.getString("stemmed"));
		if(jsonObj.containsKey("frequency.sentence")) this.FrequencySentence=jsonObj.getInteger("frequency.sentence");
		if(jsonObj.containsKey("frequency.overall")) this.FrequencyOverall=jsonObj.getInteger("frequency.overall");
		if(jsonObj.containsKey("iskeyword")) this.isKeyword=jsonObj.getBoolean("iskeyword");
		if(jsonObj.containsKey("ismin")) this.isMin=jsonObj.getBoolean("ismin");
	}
	public JsonObject getAsJsonObject() {
		JsonObject jcon = new JsonObject();
		jcon.put("id", this.ID);
		jcon.put("value", this.Value);
		jcon.put("normalized", this.Normalized);
		jcon.put("type", this.Type);
		jcon.put("sentenceid", this.SentenceID);
		jcon.put("start", this.Start);
		jcon.put("end", this.End);
		jcon.put("startinsentence", this.StartInSentence);
		jcon.put("endinsentence", this.EndInSentence);
		jcon.put("score", this.Score);
		jcon.put("partition", this.Partition);
		jcon.put("length", this.Length);
		jcon.put("frequency.sentence", this.FrequencySentence);
		jcon.put("frequency.overall", this.FrequencyOverall);
		jcon.put("ngrams", this.NGrams);
		jcon.put("stemmed", this.Stemmed);
		jcon.put("iskeyword", this.isKeyword);
		jcon.put("ismin", this.isMin);
		
		return jcon.copy();
	}
	public void addToArray(JsonArray jarr) {
		jarr.add(this.getAsJsonObject());
	}
	public void addToJson(JsonObject jcon) {
		jcon.put(ID, this.getAsJsonObject());
	}
	public void UpdateLength() {
		this.Length=Value.length();
		
	}
}
