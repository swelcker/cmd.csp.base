package cmd.csp.platform;

import java.util.UUID;

import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class CSPMessage {

	private JsonObject msg = new JsonObject();
	private JsonObject msgSender = new JsonObject();
	private DeliveryOptions opt = new DeliveryOptions();
	protected final  Logger LOGGER = LoggerFactory.getLogger(CSPLogDelegate.class.getName());
	private static String TYPE="type";
	private static String ACTION="action";
	private static String API="api";
	private static String NORMAL="normal";
	private static String HEALTH="health";
	private static String INFO="info";
	private static String CONFIG="config";
	private static String REQUEST="request";
	private static String REPLY="reply";

	private static String DOCUMENTS="documents";
	private static String DATA="data";
	private static String CONTENT="content";
	private static String INPUTS="inputs";
	private static String RESULTS="results";
	private static String PROCESS="processing";

	private static String MSGUID="msg.uid";

	private static String DELIVERY="deliveryOptions";
	private static String MESSAGE="messageObject";
	private static String SENDER="sender";
	private static String SENDERNAME="name";
	private static String SENDERUID="uid";


	public CSPMessage() {
		// TODO Auto-generated constructor stub
	}
	
	public void setTimeout(Long timeout) {
		opt.setSendTimeout(timeout);
	}
	public void setCodecName(String codecName) {
		opt.setCodecName(codecName);
	}
	public JsonObject toJsonDeliveryOptions() {
		return opt.toJson();
	}
	
	public void setActionTo(String sAction) {
		opt.addHeader(ACTION, sAction);
	}
	public void setTypeAs(String sType) {
		opt.addHeader(TYPE, sType);
	}
	public void setTypeAsNormal() {
		opt.addHeader(TYPE, NORMAL);
	}
	public void setTypeAsAPI() {
		opt.addHeader(TYPE, API);
	}
	public void setTypeAsConfig() {
		opt.addHeader(TYPE, CONFIG);
	}
	public void setTypeAsHealth() {
		opt.addHeader(TYPE, HEALTH);
	}
	public void setActionToInfo() {
		opt.addHeader(ACTION, INFO);
	}
	public void setActionToRequest() {
		opt.addHeader(ACTION, REQUEST);
	}
	public void setActionToReply() {
		opt.addHeader(ACTION, REPLY);
	}
	public boolean isAction(String action) {
		boolean ret=false;
		if(opt==null) return ret;
		if(opt.getHeaders().contains(ACTION, action, true)) ret = true;
		return ret;
	}
	public boolean isType(String type) {
		boolean ret=false;
		if(opt==null) return ret;
		if(opt.getHeaders().contains(TYPE, type, true)) ret = true;
		return ret;
	}
	
	public boolean isActionReply() {
		return isAction(REPLY);
	}
	public boolean isActionInfo() {
		return isAction(INFO);
	}
	public boolean isActionRequest() {
		return isAction(REQUEST);
	}
	public boolean isTypeNormal() {
		return isType(NORMAL);
	}
	public boolean isTypeAPI() {
		return isType(API);
	}
	public boolean isTypeConfig() {
		return isType(CONFIG);
	}
	public boolean isTypeHealth() {
		return isType(HEALTH);
	}
	
	public void addHeader(String sKey, String sAction) {
		opt.addHeader(sKey, sAction);
	}
	
	public DeliveryOptions getDeliveryOptions() {
		return opt;
	}
	public void setInfo(CSPInfo bInfo) {
		checkuid();		
		msg.put("info",bInfo); //.toString());
	}
	private Object getFrom(String strO) {
		if(msg==null) return null;
		if(msg.isEmpty()) return null;
		if(msg.containsKey(strO)) {
			return msg.getValue(strO);
		}else {
			return null;
		}
		
	}
	public Object getDocuments() {
		return getFrom(DOCUMENTS);
	}
	public JsonObject getSender() {
		Object obj = getFrom(SENDER);
		if(obj==null) {
			msgSender = new JsonObject();
			msgSender.put(SENDERNAME, "na");
			msgSender.put(SENDERUID, "na");
			return msgSender ;
		}else {
			return (JsonObject) obj;
		}
	}
	public JsonObject getConfig() {
		Object obj = getFrom(CONFIG);
		if(obj==null) {
			return null ;
		}else {
			return (JsonObject) obj;
		}
	}
	public Object getData() {
		return getFrom(DATA);
	}
	public JsonObject getInputs() {
		Object obj = getFrom(INPUTS);
		if(obj==null) {
			return null ;
		}else {
			return (JsonObject) obj;
		}
	}
	public Object getResults() {
		return getFrom(RESULTS);
	}
	public Object getContent() {
		return getFrom(CONTENT);
	}
	public Object getProcessing() {
		return getFrom(PROCESS);
	}
	public void setSender(String name, String uid) {
		checkuid();		
		if(msgSender==null) msgSender = new JsonObject();
		msgSender.put(SENDERNAME, name);
		msgSender.put(SENDERUID, uid);
	}
	public void setSender(JsonObject sValue) {
		checkuid();		
		if(sValue!=null) msgSender = sValue; //.toString());
	}
	public String getSenderUID() {
		checkuid();		
		if(msgSender==null) return "";
		if(msgSender.containsKey(SENDERUID)) return msgSender.getString(SENDERUID);
		return "";
	}
	public String getSenderName() {
		checkuid();		
		if(msgSender==null) return "";
		if(msgSender.containsKey(SENDERNAME)) return msgSender.getString(SENDERNAME);
		return "";
	}
	public void setSenderName(String strO) {
		checkuid();		
		if(msgSender==null) msgSender = new JsonObject();
		msgSender.put(SENDERNAME, strO);
	}
	public void setSenderUID(String strO) {
		checkuid();		
		if(msgSender==null) msgSender = new JsonObject();
		msgSender.put(SENDERUID, strO);
	}
	public void addConfig(JsonObject sValue) {
		checkuid();		
		msg.put(CONFIG,sValue); //.toString());
	}
	public void addDocuments(Object sValue) {
		checkuid();		
		msg.put(DOCUMENTS,sValue); //.toString());
	}
	public void addData(Object sValue) {
		checkuid();		
		msg.put(DATA,sValue); //.toString());
	}
	public void addInputs(JsonObject sValue) {
		checkuid();		
		msg.put(INPUTS,sValue); //.toString());
	}
	public void addContent(Object sValue) {
		checkuid();		
		msg.put(CONTENT,sValue); //.toString());
	}
	public void addResults(Object sValue) {
		checkuid();		
		msg.put(RESULTS,sValue); //.toString());
	}
	public void addProcessing(Object sValue) {
		checkuid();		
		msg.put(PROCESS,sValue); //.toString());
	}
	public void mergeIntoMessage(JsonObject message) {
		checkuid();		
		msg.mergeIn(message, true);
	}

	public JsonObject toJson() {
		checkuid();	
		JsonObject jcon = new JsonObject();
		jcon.put(MESSAGE, msg.copy());
		jcon.put(DELIVERY, toJsonDeliveryOptions().copy());
		jcon.put(SENDER, getSender().copy());
		return jcon;
	}
	
	public void fromBodyString(String httpBody) {
		if(httpBody==null) return;
		if(httpBody.isEmpty()) return;
		if(!httpBody.contains("{") || !httpBody.contains("}")) return;
		
		JsonObject jcon = new JsonObject(httpBody);
		if(jcon.containsKey(MESSAGE)) msg = jcon.getJsonObject(MESSAGE);
		if(jcon.containsKey(DELIVERY)) opt = new DeliveryOptions(jcon.getJsonObject(DELIVERY));
		if(jcon.containsKey(SENDER)) msgSender = jcon.getJsonObject(SENDER);
		checkuid();	
		
		
	}
	public String toBodyString() {
		checkuid();		
		return toJson().encodePrettily();
	}
	@Override
	public String toString() {
		return toJson().encode();
		
	}
	public String getUUIDString() {
		return UUID.randomUUID().toString();
	}
	private void checkuid() {
		if (!msg.containsKey(MSGUID)) msg.put(MSGUID, getUUIDString());		
	
	}
}
