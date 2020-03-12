package cmd.csp.platform;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.logging.LogDelegate;

public class CSPLogDelegate implements LogDelegate {
	//public java.util.logging.Logger logger;
	public Logger logger;
	private boolean enableConsoleOutput = false;
	private boolean enableBusLOG = false;
	private Vertx vertx;
	private String serverAddress = "csp.log";
	private JsonObject jmsg;
	 
	private StackTraceElement ste ;
	private Integer logDepth=4;
	

	public CSPLogDelegate(final String name) {
		//io.vertx.core.logging.Log4jLogDelegateFactory
		logger = Logger.getLogger(name);
		//logger.setLevel(org.apache.log4j.Level.INFO);
		//System.out.println(">>> 	Created CSPLogDelegate for "+name);
	}
	
	private String getCallerInfo(Integer depth) {
		String caller="";
		Throwable t = new Throwable();
		//t.printStackTrace();
		while (t.getStackTrace()[depth].getClassName()==this.getClass().getName() || t.getStackTrace()[depth].getFileName()=="Logger.java" ) {
			depth++;
		}
		ste = t.getStackTrace()[depth];
		caller = ste.getClassName()+">"+ste.getMethodName()+"()@"+ste.getLineNumber();

			ste = t.getStackTrace()[++depth];
			caller = ste.getClassName()+">>" +caller;

			caller = caller.replace("io.vertx.core.impl.VertxImpl$", "cspVxImpl.");
		return caller;
	}
	private String getCallerMethod(Integer depth) {
		String caller="";
		Throwable t = new Throwable();
		while (t.getStackTrace()[depth].getClassName()==this.getClass().getName() || t.getStackTrace()[depth].getFileName()=="Logger.java" ) {
			depth++;
		}
		ste = t.getStackTrace()[depth];
		caller = ste.getMethodName();
		return caller;
	}
	private String getCallerClass(Integer depth) {
		String caller="";
		Throwable t = new Throwable();
		while (t.getStackTrace()[depth].getClassName()==this.getClass().getName() || t.getStackTrace()[depth].getFileName()=="Logger.java" ) {
			depth++;
		}
		ste = t.getStackTrace()[depth];
		caller = ste.getClassName();
		return caller;
	}	  
	public boolean isEnableConsoleOutput() {
		return enableConsoleOutput;
	}

	public void setEnableConsoleOutput(boolean enableConsoleOutput) {
		this.enableConsoleOutput = enableConsoleOutput;
	}

	public void enableServerLOG(Vertx vx, String address) {
		if (vx != null) {
			enableBusLOG = true;
			jmsg = new JsonObject();
			vertx = vx;
			if(address != null) serverAddress = address;
			if(enableConsoleOutput) System.out.println("CSP.ControllerLogDelegate.ConsoleOutput: Enabled ServerLOG on Address@"+serverAddress);
		}else {
			if(enableConsoleOutput) System.out.println("CSP.ControllerLogDelegate.ConsoleOutput: ERROR Enable ServerLOG vx isnull");

		}
		
	}
	public void disableServerLOG() {
		enableBusLOG = false;
		if(enableConsoleOutput) System.out.println("CSP.ControllerLogDelegate.ConsoleOutput: Disabled ServerLOG");
		
	}
	
	public void setLevel(org.apache.log4j.Level newLevel) {
		logger.setLevel(newLevel);
	}

	@Override
	public boolean isWarnEnabled() {
		return logger.isEnabledFor(Level.WARN);
	}

	@Override
		public boolean isInfoEnabled() {
			    return logger.isInfoEnabled();
	  }
	
	  @Override
	  public boolean isDebugEnabled() {
	    return logger.isDebugEnabled();
	    
	  }
	
	  @Override
	  public boolean isTraceEnabled() {
	    return logger.isTraceEnabled();
	  }
	
	  @Override
	  public void fatal(final Object message) {
	    log(org.apache.log4j.Level.ERROR, message);
	  }
	
	  @Override
	  public void fatal(final Object message, final Throwable t) {
	    log(org.apache.log4j.Level.ERROR, message, t);
	  }
	
	  @Override
	  public void error(final Object message) {
	    log(org.apache.log4j.Level.ERROR, message);
	  }

		  @Override
		  public void error(Object message, Object... params) {
		    log(org.apache.log4j.Level.ERROR, message, null, params);
		  }

		  public void error(final Object message, final Throwable t) {
		    log(org.apache.log4j.Level.ERROR, message, t);
		  }

		  @Override
		  public void error(Object message, Throwable t, Object... params) {
		    log(org.apache.log4j.Level.ERROR, message, t, params);
		  }

		  public void warn(final Object message) {
		    log(org.apache.log4j.Level.WARN, message);
		  }

		  @Override
		  public void warn(Object message, Object... params) {
		    log(org.apache.log4j.Level.WARN, message, null, params);
		  }

		  public void warn(final Object message, final Throwable t) {
		    log(org.apache.log4j.Level.WARN, message, t);
		  }

		  @Override
		  public void warn(Object message, Throwable t, Object... params) {
		    log(org.apache.log4j.Level.WARN, message, t, params);
		  }

		  @Override
		  public void info(final Object message) {
		    log(org.apache.log4j.Level.INFO, message);
		  }

		  @Override
		  public void info(Object message, Object... params) {
		    log(org.apache.log4j.Level.INFO, message, null, params);
		  }

		  public void info(final Object message, final Throwable t) {
		    log(org.apache.log4j.Level.INFO, message, t);
		  }

		  @Override
		  public void info(Object message, Throwable t, Object... params) {
		    log(org.apache.log4j.Level.INFO, message, t, params);
		  }

		  public void debug(final Object message) {
		    log(org.apache.log4j.Level.DEBUG, message);
		  }

		  @Override
		  public void debug(Object message, Object... params) {
		    log(org.apache.log4j.Level.DEBUG, message, null, params);
		  }

		  public void debug(final Object message, final Throwable t) {
		    log(org.apache.log4j.Level.DEBUG, message, t);
		  }

		  @Override
		  public void debug(Object message, Throwable t, Object... params) {
		    log(org.apache.log4j.Level.DEBUG, message, t, params);
		  }

		  public void trace(final Object message) {
		    log(org.apache.log4j.Level.TRACE, message);
		  }

		  @Override
		  public void trace(Object message, Object... params) {
		    log(org.apache.log4j.Level.TRACE, message, null, params);
		  }

		  public void trace(final Object message, final Throwable t) {
		    log(org.apache.log4j.Level.TRACE, message, t);
		  }

		  @Override
		  public void trace(Object message, Throwable t, Object... params) {
		    log(org.apache.log4j.Level.TRACE, message, t, params);
		  }

		  private void log(org.apache.log4j.Level level, Object message) {
		    log(level, message, null);
		  }

		  private void log(org.apache.log4j.Level level, Object message, Throwable t, Object... params) {
			  String msg;
			  String msgCon;
			    if (message==null) {
			    	msg = "NULL";
			    }else {
				    if (message instanceof String) {
				    	msg = (String) message;
				    }else {
				    	msg = message.toString();
				    }		    
				}			  

		    if (!logger.isEnabledFor(level)) {
		      return;
		    }
		    msgCon = new Date().toString()+" CSPLog	("+ level.toString() + ":Bus="+ enableBusLOG + "):	" + msg;
		    msg = new Date().toString()+" - (Bus="+ enableBusLOG + ") - " + msg;
			if(enableConsoleOutput) System.out.println(msgCon);		

		    
		    if (enableBusLOG) {
		    	Instant timestamp = Instant.now();
		    	jmsg.put("message", msg);
		    	if (params != null && params.length != 0 ) {
		    		JsonObject jo = new JsonObject();
		    		Integer xi=0;
			 		for (Object temp : params) {
			    		jo.put("param#"+Integer.toString(xi++), temp.toString());
					}	
			    	jmsg.put("parameter", jo);			 		
		    	}
		    	
		    	jmsg.put("timestamp", Timestamp.from(timestamp).toString());
		    	jmsg.put("callerinfo", getCallerInfo(logDepth));
		    	jmsg.put("loggername", this.getClass().getSimpleName());
		    	jmsg.put("sourceclassname", getCallerClass(logDepth));
		    	jmsg.put("sourcemethodname", getCallerMethod(logDepth));
		    	jmsg.put("level", level.toString());
			    	CSPRuntime.putRuntimeInfo(jmsg);
				vertx.eventBus().send(serverAddress, jmsg);
		    	
		    }else {
			    logger.log(level, msg);		    	
		    }
		  }

		  private void log(org.apache.log4j.Level level, Object message, Throwable t) {
			  //System.out.println(level.toString());
		    log(level, message, t, (Object[]) null);
		  }

		  @Override
		  public Object unwrap() {
		    return this;
		  }
			
		  private static org.apache.log4j.Level convertLevel(final org.pmw.tinylog.Level level) {
			  //System.out.println("convertLevel:"+level.toString());
				switch (level) {
					case TRACE:
						return org.apache.log4j.Level.TRACE;
					case DEBUG:
						return org.apache.log4j.Level.DEBUG;
					case INFO:
						return org.apache.log4j.Level.INFO;
					case WARNING:
						return org.apache.log4j.Level.WARN;
					case ERROR:
						return org.apache.log4j.Level.ERROR;
					case OFF:
						return org.apache.log4j.Level.OFF;

					default:
						return org.apache.log4j.Level.INFO;
				}
			}		  
			public void setLevel(org.pmw.tinylog.Level newLevel) {
				logger.setLevel(convertLevel(newLevel));
			}		  
}
