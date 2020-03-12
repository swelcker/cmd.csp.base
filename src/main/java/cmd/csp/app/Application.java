/**
 * 
 */
package cmd.csp.app;

import cmd.csp.base.BasePlatform;

/**
 * @author swelcker *
 */
public class Application  {
	private static  BasePlatform baseApp;

	public Application() {}

	public static void main(String[] args) {

		baseApp = new BasePlatform();

		baseApp.main(ar->{
			if (ar.succeeded()) {
				
			}
		});	
	}
} 	