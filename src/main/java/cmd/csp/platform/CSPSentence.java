package cmd.csp.platform;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class CSPSentence {
	public String ID="";
	public String Value="";
	public String Normalized="";
	public String NGrams="";
	public String Stemmed="";
	public String Min="";
	public String Keywords="";

	public String Type="";
	public Integer Start=0;
	public Integer End=0;
	public Double Score=0.;
	public Double Partition=0.;
	public Integer Length=0;
	public CSPSentence() {
		// TODO Auto-generated constructor stub
	}
	public CSPSentence(JsonObject jsonObj) {
		if(jsonObj.containsKey("id")) this.ID=(jsonObj.getString("id"));
		if(jsonObj.containsKey("value")) this.Value=(jsonObj.getString("value"));
		if(jsonObj.containsKey("normalized")) this.Normalized=(jsonObj.getString("normalized"));
		if(jsonObj.containsKey("type")) this.Type=(jsonObj.getString("type"));
		if(jsonObj.containsKey("start")) this.Start=(jsonObj.getInteger("start"));
		if(jsonObj.containsKey("end")) this.End=(jsonObj.getInteger("end"));
		if(jsonObj.containsKey("score")) this.Score=(jsonObj.getDouble("score"));
		if(jsonObj.containsKey("partition")) this.Partition=(jsonObj.getDouble("partition"));
		if(jsonObj.containsKey("length")) this.Length=jsonObj.getInteger("length");
		if(jsonObj.containsKey("ngrams")) this.NGrams=(jsonObj.getString("ngrams"));
		if(jsonObj.containsKey("stemmed")) this.Stemmed=(jsonObj.getString("stemmed"));
		if(jsonObj.containsKey("min")) this.Min=(jsonObj.getString("min"));
		if(jsonObj.containsKey("keywords")) this.Keywords=(jsonObj.getString("keywords"));

	}
	public JsonObject getAsJsonObject() {
		JsonObject jcon = new JsonObject();
		jcon.put("id", this.ID);
		jcon.put("value", this.Value);
		jcon.put("normalized", this.Normalized);
		jcon.put("ngrams", this.NGrams);
		jcon.put("stemmed", this.Stemmed);
		jcon.put("min", this.Min);
		jcon.put("keywords", this.Keywords);
		jcon.put("type", this.Type);
		jcon.put("score", this.Score);
		jcon.put("start", this.Start);
		jcon.put("end", this.End);
		jcon.put("partition", this.Partition);
		jcon.put("length", this.Length);
		
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
