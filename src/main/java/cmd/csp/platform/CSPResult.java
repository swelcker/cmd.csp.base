package cmd.csp.platform;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class CSPResult {
	private String Name="";
	private String Value="";
	private String Type="";
	private Integer Start=0;
	private Integer End=0;
	private Double Score=99.9;
	private String Info="";
	public CSPResult() {
		// TODO Auto-generated constructor stub
	}
	public CSPResult(JsonObject jsonObj) {
		if(jsonObj.containsKey("name")) this.Name(jsonObj.getString("name"));
		if(jsonObj.containsKey("value")) this.Value(jsonObj.getString("value"));
		if(jsonObj.containsKey("type")) this.Type(jsonObj.getString("type"));
		if(jsonObj.containsKey("score")) this.Score(jsonObj.getDouble("score"));
		if(jsonObj.containsKey("start")) this.Start(jsonObj.getInteger("start"));
		if(jsonObj.containsKey("end")) this.End(jsonObj.getInteger("end"));
		if(jsonObj.containsKey("info")) this.Info=(jsonObj.getString("info"));
	}
	public JsonObject getAsJsonObject() {
		JsonObject jcon = new JsonObject();
		jcon.put("name", this.Name());
		jcon.put("value", this.Value());
		jcon.put("type", this.Type());
		jcon.put("score", this.Score());
		jcon.put("start", this.Start());
		jcon.put("end", this.End());
		jcon.put("info", this.getInfo());
		
		return jcon;
	}
	public void addToArray(JsonArray jarr) {
		jarr.add(this.getAsJsonObject());
	}
	public String Name() {
		return Name;
	}

	public void Name(String name) {
		Name = name;
	}

	public String Value() {
		return Value;
	}

	public void Value(String value) {
		Value = value;
	}

	public String Type() {
		return Type;
	}

	public void Type(String type) {
		Type = type;
	}

	public Integer Start() {
		return Start;
	}

	public void Start(Integer start) {
		Start = start;
	}

	public Integer End() {
		return End;
	}

	public void End(Integer end) {
		End = end;
	}

	public Double Score() {
		return Score;
	}

	public void Score(Double score) {
		Score = score;
	}
	public String getInfo() {
		if(this.Info.length()>0) {
			
			
		}else {
			this.Info = "";
		
		}
		return Info;
	}
	public void addInfo(String info) {
		//System.out.println("addinfo::"+info);
		if(info.length()<1) return;
		if(info.endsWith(",")) {
			info = info.substring(0, info.length()-1);
		}
		if(info.startsWith(",")) info=info.substring(1);
		
		info=info.trim();
		if(this.Type.toUpperCase().contains(info.toUpperCase())) return;
		if(this.Info.length()>0) {
			this.Info = this.Info + "; " + info;
			
		}else {
			if(info.length()>0) Info = info;
		
		}
	}

}
