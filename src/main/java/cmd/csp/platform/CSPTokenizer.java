package cmd.csp.platform;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class CSPTokenizer {

	private JsonObject store = new JsonObject();
	public CSPTokens tokens = new CSPTokens();
	public CSPSentences sentences = new CSPSentences();

	public String Original="";
	public String Normalized="";
	public String Type="";
	public String Grams="";
	public String Stemmed="";
	public Double Score=0.;
	public Double Partition=0.;
	public Integer LengthOriginal=0;
	public Integer LengthNormalized=0;
	public Integer LengthStemmed=0;
	public String BagOfWords="";

	
	public CSPTokenizer() {
		// TODO Auto-generated constructor stub
	}

	public CSPTokenizer(JsonObject jsonObj) {
		if(jsonObj.containsKey("original")) this.Original=(jsonObj.getString("original"));
		if(jsonObj.containsKey("normalized")) this.Normalized=(jsonObj.getString("normalized"));
		if(jsonObj.containsKey("stemmed")) this.Stemmed=(jsonObj.getString("stemmed"));
		if(jsonObj.containsKey("grams")) this.Grams=(jsonObj.getString("grams"));
		if(jsonObj.containsKey("type")) this.Type=(jsonObj.getString("type"));
		if(jsonObj.containsKey("score")) this.Score=(jsonObj.getDouble("score"));
		if(jsonObj.containsKey("partition")) this.Partition=(jsonObj.getDouble("partition"));
		if(jsonObj.containsKey("original.length")) this.LengthOriginal=jsonObj.getInteger("original.length");
		if(jsonObj.containsKey("normalized.length")) this.LengthNormalized=jsonObj.getInteger("normalized.length");
		if(jsonObj.containsKey("stemmed.length")) this.LengthStemmed=jsonObj.getInteger("stemmed.length");
		if(jsonObj.containsKey("bagofwords")) this.BagOfWords=(jsonObj.getString("bagofwords"));

		
		if(jsonObj.containsKey("tokens")) this.tokens= new CSPTokens(jsonObj.getJsonObject("tokens"));
		if(jsonObj.containsKey("sentences")) this.sentences= new CSPSentences(jsonObj.getJsonObject("sentences"));
		
	}
	public JsonObject getAsJsonObject() {
		JsonObject jcon = new JsonObject();
		jcon.put("original", this.Original);
		jcon.put("normalized", this.Normalized);
		jcon.put("stemmed", this.Stemmed);
		jcon.put("grams", this.Grams);
		jcon.put("type", this.Type);
		jcon.put("score", this.Score);
		jcon.put("partition", this.Partition);
		jcon.put("original.length", this.LengthOriginal);
		jcon.put("normalized.length", this.LengthNormalized);
		jcon.put("stemmed.length", this.LengthStemmed);
		jcon.put("bagofwords", this.BagOfWords);

		sentences.addToJson(jcon);
		tokens.addToJson(jcon);

		return jcon.copy();
	}
	public void addToArray(JsonArray jarr) {
		jarr.add(this.getAsJsonObject());
	}
	public void addToJson(JsonObject jcon) {
		jcon.put("tokenizer", this.getAsJsonObject());
	}
	public void UpdateLength() {
		this.LengthOriginal=Original.length();
		this.LengthNormalized=Normalized.length();
		this.LengthStemmed=Stemmed.length();
		this.sentences.UpdateLength();
		this.tokens.UpdateLength();
	}
}
