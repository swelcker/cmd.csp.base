package cmd.csp.utils;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class WFLItem {

		private String name="";
		private String actor="server.";
		private String type="";
		private String condition="";
		private Boolean enabled=false;
		private String statusIn="000000";
		private String statusOut="100000";
		private String statusFalse="000999";
		private String statusInvalid="000650";

		public WFLItem() {
			// TODO Auto-generated constructor stub
		}
		public WFLItem(String strName, Boolean blnEnabled, String strStatusIn, String strStatusOut) {
			this.setActor(strName);
			this.setName(strName);
			this.setEnabled(blnEnabled);
			this.setStatusIn(strStatusIn);
			this.setStatusOut(strStatusOut);
		}
		public WFLItem(JsonObject jsonObj) {
			if(jsonObj.containsKey("name")) this.setName(jsonObj.getString("name"));
			if(jsonObj.containsKey("actor")) this.setActor(jsonObj.getString("actor"));
			if(jsonObj.containsKey("type")) this.setType(jsonObj.getString("type"));
			if(jsonObj.containsKey("condition")) this.setCondition(jsonObj.getString("condition"));
			if(jsonObj.containsKey("enabled")) this.setEnabled(jsonObj.getBoolean("enabled"));
			if(jsonObj.containsKey("status.in")) this.setStatusIn(jsonObj.getString("status.in"));
			if(jsonObj.containsKey("status.out")) this.setStatusOut(jsonObj.getString("status.out"));
			if(jsonObj.containsKey("status.false")) this.setStatusFalse(jsonObj.getString("status.false"));
			if(jsonObj.containsKey("status.invalid")) this.setStatusInvalid(jsonObj.getString("status.invalid"));
		}
			
		public JsonObject getAsJsonObject() {
			JsonObject jcon = new JsonObject();
			jcon.put("name", this.Name());
			jcon.put("actor", this.Actor());
			jcon.put("type", this.Type());
			jcon.put("enabled", this.Enabled());
			jcon.put("status.in", this.StatusIn());
			jcon.put("status.out", this.StatusOut());
			jcon.put("status.false", this.StatusFalse());
			jcon.put("status.invalid", this.StatusInvalid());
			jcon.put("condition", this.Condition());
			
			return jcon;
		}
		public void addToArray(JsonArray jarr) {
			jarr.add(this.getAsJsonObject());
		}
		public String Name() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String Actor() {
			return actor;
		}
		public void setActor(String actor) {
			this.actor = actor;
		}
		public String Type() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String Condition() {
			return condition;
		}
		public void setCondition(String condition) {
			this.condition = condition;
		}
		public Boolean Enabled() {
			return enabled;
		}
		public void setEnabled(Boolean enabled) {
			this.enabled = enabled;
		}
		public String StatusIn() {
			return statusIn;
		}
		public void setStatusIn(String statusIn) {
			this.statusIn = statusIn;
		}
		public String StatusOut() {
			return statusOut;
		}
		public void setStatusOut(String statusOut) {
			this.statusOut = statusOut;
		}
		public String StatusFalse() {
			return statusFalse;
		}
		public void setStatusFalse(String statusFalse) {
			this.statusFalse = statusFalse;
		}
		public String StatusInvalid() {
			return statusInvalid;
		}
		public void setStatusInvalid(String statusInvalid) {
			this.statusInvalid = statusInvalid;
		}
}
