package cmd.csp.platform;


import io.vertx.core.spi.logging.LogDelegate;
import io.vertx.core.spi.logging.LogDelegateFactory;

public class CSPLogDelegateFactory implements LogDelegateFactory {

		  
	@Override
	public LogDelegate createDelegate(String name) {
		//System.out.println(" +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ CSPLogDelegateFactory: createDelegate");
		return new  CSPLogDelegate(name);

	}

}
