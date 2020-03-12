package cmd.csp.platform;

import cmd.csp.platform.CSPRuntime;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class CSPInfo extends JsonObject  {

	protected final  Logger LOGGER = LoggerFactory.getLogger(CSPLogDelegate.class.getName());


    public CSPInfo() {
    	super();
    }
	public String getRuntimeInfo() {
		return this.toString(); //.encode();
	}	
	public void initRuntimeInfo(Integer iHashcode, String sDeploymentID, String sVerticleRef) {
		this.put("hashcode", ""+Integer.toString(iHashcode));
		this.put("deploymentid", sDeploymentID);
		this.put("verticleref", sVerticleRef);
    	CSPRuntime.putRuntimeInfoShort(this);

	}	
	public String getHashcode() {return checkString("hashcode");}
	public String getDeploymentID() {return checkString("deploymentid");}
	public String getVerticleRef() {return checkString("verticleref");}
	public String getVerticleName() {return checkString("verticlename");}
	public String getHostname() {return checkString("hostname");}
	public String getHostaddress() {return checkString("hostaddress");}
	public String getServicePID() {return checkString("pid");}
	public String getUUID() 	{return checkString("uuid");}
	public String getServiceType() 	{return checkString("servicetype");}
	public String getLastConnection() {return checkString("lastconnection");}
    public String getServiceName() 	{return checkString("servicename");}
    public String getServiceURL() 	{return checkString("url");}
    public String getDescription()	{return checkString("description");}
    public Integer getPort() 		{return checkInteger("port");}  
	public String NewUUID() 	{return CSPRuntime.createUUIDString();}
	
	public String getRuntimeInfoPretty() {
		return this.encodePrettily();
	}
     public void setLastConnection(String lastConnection) {
		this.put("lastconnection", lastConnection);
    }
    public void setServiceURL(String sURL) {
        this.put("url", sURL);
    }     
    public void setServiceName(String sName) {
        this.put("servicename", sName);
    }     
    public void setDescription(String sDesc) {
        this.put("description", sDesc);
    }     
   
    public void setPort(Integer iPort) {
        this.put("port", iPort);
    }	
  public JsonObject getJson() {
	  return (JsonObject) this;
  }
    @SuppressWarnings("unused")
	private JsonObject checkJson(String sGet) {
    	if (this.containsKey(sGet)){
    		return this.getJsonObject(sGet);
    		
    	}else {
    		return new JsonObject();
    	}
    }
    private String checkString(String sGet) {
    	if (this.containsKey(sGet)){
    		return this.getString(sGet);
    		
    	}else {
    		return "";
    	}
    }
    private Integer checkInteger(String sGet) {
    	if (this.containsKey(sGet)){
    		return this.getInteger(sGet);
    		
    	}else {
    		return -1;
    	}
    }
    @SuppressWarnings("unused")
	private long checkLong(String sGet) {
    	if (this.containsKey(sGet)){
    		return this.getLong(sGet);
    		
    	}else {
    		return -1;
    	}
    }
    @SuppressWarnings("unused")
	private boolean checkBoolean(String sGet) {
    	if (this.containsKey(sGet)){
    		return this.getBoolean(sGet);
    		
    	}else {
    		return false;
    	}
    }
    


}
