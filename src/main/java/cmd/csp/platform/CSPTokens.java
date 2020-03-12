package cmd.csp.platform;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class CSPTokens {
	public Integer LengthMin=0;
	public Integer LengthAVG=0;
	public Integer LengthMax=0;
	public Integer count=0;
	private JsonArray store = new JsonArray();
	public CSPToken current = null;
	private Integer currentPos = -1;
	private boolean isSaved=false;

	public CSPTokens() {
		currentPos = -1;
		count=0;
	}
	public void CreateNew() {
		SaveCurrent();
		current = new CSPToken();
		currentPos=this.count;
		current.ID = String.valueOf(count++);
		isSaved=false;
	
	}
	public void SaveCurrent() {
		if(current != null && !isSaved) {
			current.addToArray(store);
			isSaved=true;
		};
	
	}
	public void SetPos(int zerobasedpos) {
		if(zerobasedpos>=0 && this.count>0) {
			currentPos = zerobasedpos;
			// stays at last if next reaches end
			if(currentPos>=this.count) currentPos = this.count-1;			
			current = new CSPToken(store.getJsonObject(currentPos));
		}else {
			if(this.count>0) {
				currentPos = 0;
				current = new CSPToken(store.getJsonObject(currentPos));
			}else {
				CreateNew();
			}
		}
	}
	public void SetFirst() {
		if(this.count>0) {
			currentPos = 0;
			this.SetPos(currentPos);
		}
	}
	public void SetLast() {
		if(this.count>0) {
			currentPos = this.count-1;
			this.SetPos(currentPos);
		}
	}
	public void SetNext() {
		if(this.count>0) {
			currentPos = currentPos+1;
			this.SetPos(currentPos);
		}
	}
	public CSPTokens(JsonObject jsonObj) {
		setFromJson( jsonObj);
	}
	public void setFromJson(JsonObject jsonObj) {
		currentPos = -1;
		count=0;
		if(jsonObj.containsKey("length_min")) this.LengthMin=jsonObj.getInteger("length_min");
		if(jsonObj.containsKey("length_max")) this.LengthMax=jsonObj.getInteger("length_max");
		if(jsonObj.containsKey("length_avg")) this.LengthAVG=jsonObj.getInteger("length_avg");
		if(jsonObj.containsKey("count")) this.count=jsonObj.getInteger("count");
		if(jsonObj.containsKey("token")) this.store=jsonObj.getJsonArray("token");
		if(store!=null) {
			this.count=store.size();
			SetFirst();
		}
	}
	public JsonObject getAsJsonObject() {
		JsonObject jcon = new JsonObject();
		jcon.put("length_min", this.LengthMin);
		jcon.put("length_max", this.LengthMax);
		jcon.put("length_avg", this.LengthAVG);
		jcon.put("count", this.count);
		jcon.put("token", this.store);
		
		return jcon.copy();
	}
	public void addToArray(JsonArray jarr) {
		jarr.add(this.getAsJsonObject());
	}
	public void addToJson(JsonObject jcon) {
		jcon.put("tokens", this.getAsJsonObject());
	}
	public void UpdateLength() {
		if(store.size()>0) {
			LengthMin=Integer.MAX_VALUE;
			LengthAVG=0;
			LengthMax=0;
			for(int i=0; i < store.size();i++) {
				JsonObject jo = store.getJsonObject(i);
				CSPToken item = new CSPToken(jo);
				item.UpdateLength();
				if (item.Length<LengthMin) LengthMin = item.Length;
				if (item.Length>LengthMax) LengthMax = item.Length;
				LengthAVG = LengthAVG+item.Length;
			}
			LengthAVG = LengthAVG/store.size();
		}
	}
}
