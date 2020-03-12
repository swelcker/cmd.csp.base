package cmd.csp.platform;



public class CSPConstants {
	public enum WorkItemState {
		  INACTIVE,
		  ACTIVE,
		  COMPLETE,
		  FAILED,
		  DELAYED
		}

	public enum ServiceStatus{
    	OUT_OF_SERVICE, DOWN, UP, UNKNOWN
    }
    public enum SharedDataType{
    	CSP_LOCAL, CSP_CLUSTER
    }    
/*    public enum ServerType{
    	SMTP, PROCESSING, UI, EB, METRICS, POP, IMAP, GENDER, PREFILES, PRETOKENIZER, PREDETECT, PRECONVERTER, POSTPROCESSING, ARCHIVE, BATCH, FTP, FILECRAWLER, WEBCRAWLER, WORKFLOW
    }*/

	public enum ConfigType{
		definition, statichandler, app, typematch, appvertx, addresses, eventbus, workflows
	}

	public enum StaticHandlerType{
		webroot, filesreadonly, maxageseconds, enablecaching, enabledirectorylisting, directorylistingtemplate, includehidden, cacheentrytimeout, indexpage, maxcachesize, 
		alwaysuseasyncfilesystem, enablefstuning, maxavgservertime, enablerangesupport,enablerootfilesystemaccess,enablesendvaryheader
	}
	public static final String INTERVAL = "interval";
	public static final String INTERVAL_HEARTBEAT = "interval.heartbeat";
	public static final String INTERVAL_CONFIG = "interval.config";
	public static final String INTERVAL_METRICS = "interval.config";
	public static final String ADDRESS_CONFIG = "csp.config";
	public static final String DISCOVERY_CONFIG = "csp.discovery";
	public static final String HEALTH_CONFIG = "csp.health";
	public static final String STATIC_RESOURCE_ROUTE = "static.resourceroute";
	public static final String STATIC_RESOURCE_HANDLER = "static.resourcehandler";
	public static final String HOST = "host";
	public static final String PORT = "port";
	public static final String SUBROOT = "subroot";
	public static final String SSL = "ssl";
	public static final String AVAILABLE_SERVICES = "enable";
	public static final String CORECLASSNAME = "cmd.csp";
	//public static final String CORELOGGER = "com.csp.controller.ControllerLogDelegateFactory"; //csp.logger
	//com.csp.controller.ControllerLogDelegate
	public static final String DEFAULT_CONFIG_FILENAME = "csp.config.main.v4.json";
	public static final String DEFAULT_CONFIG_FILEPATH = "./config/";
	public static final String DEFAULT_DISCOVERY_ADDRESS = "csp.discovery";
	public static final String DEFAULT_DISCOVERY_ANNOUNCE_SUBADDRESS = ".announce";
	public static final String DEFAULT_DISCOVERY_USAGE_SUBADDRESS = ".usage";
	

	
	
	public CSPConstants() {
		// TODO Auto-generated constructor stub
	}

}
