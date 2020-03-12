package cmd.csp.platform;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import io.vertx.core.json.JsonObject;

public class CSPRuntime {


    /**
    * Returns Infos
    *
    */
	public static void putRuntimeInfo(JsonObject jobj) {
		putRuntimeInfoShort(jobj);
		jobj.put("mxversion", getManagementSpecVersion());
		jobj.put("vmname", getVmName());
		jobj.put("vmversion", getVmVersion());
		jobj.put("javaversion", getJavaVersion());
		jobj.put("javaruntimename", getJavaRuntimeName());
		jobj.put("osname", getOSName());
		jobj.put("isbootclasspathsupported", isBootClassPathSupported());
	}
	public static void putRuntimeInfoShort(JsonObject jobj) {
		jobj.put("uuid", createUUIDString());
		jobj.put("verticlename", getVerticleName());
		jobj.put("hostname", getIHostName());
		jobj.put("hostaddress", getIHostAddress());
		jobj.put("pid", getPID());
	}
	public static boolean isAtLeastJava9() {
		String version = getJavaVersion();
		if (version == null) {
			return false;
		} else {
			int index = version.indexOf('.');
			if (index > 0) {
				version = version.substring(0, index);
			}
			return version.matches("[0-9]{1,8}") && Integer.parseInt(version) >= 9;
		}
	}	
	
	public static String getJavaVersion() {
		String version = System.getProperty("java.version");
		if (version == null) {
			return "unknown";
		} else {
			return version;
		}
	}
	public static String getJavaRuntimeName() {
		return System.getProperty("java.runtime.name");
	}

	public static String getOSName() {
		return System.getProperty("os.name");
	}
	public static boolean isAndroid() {
		return System.getProperty("java.runtime.name").equalsIgnoreCase("Android Runtime");
	}

	public static boolean isWindows() {
		return System.getProperty("os.name").startsWith("Windows");
	}
	
   public static String createUUIDString() {
		return UUID.randomUUID().toString();
	}

   public static String getIHostName() {
     String hostName = "";
     try {
       hostName = InetAddress.getLocalHost().getHostName();
     } catch (UnknownHostException e) {
       e.printStackTrace();
     }
     return hostName;
   }
   public static String getIHostAddress() {
       String hostAddress = "";
       try {
       	hostAddress = InetAddress.getLocalHost().getHostAddress();
       } catch (UnknownHostException e) {
         e.printStackTrace();
       }
       return hostAddress;
     } 
   
   public static String getPID() {
       String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
       if (processName != null && processName.length() > 0) {
         try {
           return processName.split("@")[0];
         } catch (Exception e) {
           return "0";
         }
       }

       return "0";
     }
   
   public static String getVerticleName() {
       return ManagementFactory.getRuntimeMXBean().getName();
   } 
   public static String getManagementSpecVersion() {
       return ManagementFactory.getRuntimeMXBean().getManagementSpecVersion();
   } 
   public static String getVmName() {
       return ManagementFactory.getRuntimeMXBean().getVmName();
   } 
   public static String getVmVersion() {
       return ManagementFactory.getRuntimeMXBean().getVmVersion();
   } 
   public static boolean isBootClassPathSupported() {
       return ManagementFactory.getRuntimeMXBean().isBootClassPathSupported();
   } 
   
   
   


}
