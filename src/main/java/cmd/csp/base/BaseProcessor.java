package cmd.csp.base;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

import com.twelvemonkeys.io.FileUtil;

import cmd.csp.interfaces.IProcessor;
import cmd.csp.platform.CSPLogDelegate;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;


public class BaseProcessor  implements IProcessor{
	
	protected JsonObject input=new JsonObject();
	protected JsonObject config=new JsonObject();
	protected JsonObject result=new JsonObject();
	
	protected JsonObject exampleInput=new JsonObject();
	protected JsonObject exampleResult=new JsonObject();
	protected boolean blnPrepProcessing=false;
	protected boolean blnProcessing=false;
	protected boolean blnPostProcessing=false;
	protected JsonObject info=null;
	protected final CSPLogDelegate LOGGER = new CSPLogDelegate(CSPLogDelegate.class.getName());;
	
	protected String[] inputFormats ;
	protected Vertx vertx;
	protected BaseUtil util=null;

	public BaseProcessor() {
	    inputFormats = ImageIO.getReaderFormatNames();
	}


	@Override
	public void StopProcessor(Handler<AsyncResult<Void>> resultHandler) {
		blnPostProcessing=false;
		blnPrepProcessing=false;
		blnProcessing=false;
 		result.clear();
 		input.clear();
		
 		LOGGER.info(this.getClass().getSimpleName()+ ":Stop.Processor");
		resultHandler.handle(Future.succeededFuture());
		
	}
	
	@Override
	public void PrepProcessing(Handler<AsyncResult<Void>> resultHandler) {
		if(blnPrepProcessing) {
 			resultHandler.handle(Future.failedFuture("Still connected, disconnect first"));	
			return;
			 
 		}
		blnPostProcessing=false;
		blnProcessing=false;
		
		blnPrepProcessing = true;
		
		if (blnPrepProcessing) {
	 		LOGGER.info(this.getClass().getSimpleName()+":Prep.Processing");	 			
 		}else {
	 		LOGGER.info(this.getClass().getSimpleName()+":Prep.Processing:Failure");	 			
 		}
 		result.clear();
 		input.clear();
      
      resultHandler.handle(Future.succeededFuture());
    }
	@Override
	public void PostProcessing(Handler<AsyncResult<Void>> resultHandler) {

		blnPostProcessing=true;
		blnPrepProcessing=false;
		blnProcessing=false;
 		result.clear();
 		input.clear();
		
 		LOGGER.info(this.getClass().getSimpleName()+ ":Post.Processing");
		resultHandler.handle(Future.succeededFuture());
	}

	@Override
	public void Process(Handler<AsyncResult<JsonObject>> resultHandler) {

		if (!blnPrepProcessing) resultHandler.handle(Future.failedFuture("Prep.Processing not called first"));
		
		LOGGER.info(this.getClass().getSimpleName()+":Process");
	    resultHandler.handle(Future.succeededFuture(getResult().copy()));


	}
	


	@Override
	public void setInfo(JsonObject info) {
		this.info = info;
		
	}
	@Override
	public void applyConfig(JsonObject config) {
		// TODO 
		this.config = config.copy();
		
	}
	@Override
	public boolean ChckAvailability() {
		// TODO 
		return true;
	}
	@Override
	public void setInput(String content) {
 		result.clear();
 		input.clear();
	
 		this.input= new JsonObject().put("inputs", content);
		
	}
	@Override
	public void setInput(JsonObject content) {
 		result.clear();
 		input.clear();
	
 		if(content!=null) this.input=content.copy();
	}
	@Override
	public JsonObject getResult() {
		return this.result;
	}
	@Override
	public JsonObject getExampleResult() {
		// TODO 
		return this.exampleResult.copy();
	}
	@Override
	public JsonObject getExampleInput() {
		// TODO
		return this.exampleInput.copy();
	}
	
	public static String toPercentage(double n, int digits){
	    return String.format("%."+digits+"f",n*100)+"%";
	}
	public String getUUIDString() {
		return UUID.randomUUID().toString();
	}
	public static String[] getStringArray(JsonArray jsonArray) {
	    String[] stringArray = null;
	    if (jsonArray != null) {
	        int length = jsonArray.size();
	        stringArray = new String[length];
	        for (int i = 0; i < length; i++) {
	            stringArray[i] = jsonArray.getString(i);
	        }
	    }
	    return stringArray;
	}
	//#########
	public Boolean isFileImageOrPDF(String strPathFilename) {
		String fT="";
		fT = FileUtil.getExtension(strPathFilename);
		if(fT.length()>0) {
			if(isFileImage(strPathFilename)) {
					return true;
			}else {
				if(fT.toUpperCase().contentEquals("PDF")) {
					return true;
				}else {
					return false;
				}
			}
		}else {
			return false;
		}	
	}
	public Boolean isFileImage(String strPathFilename) {
		String fT="";
		fT = FileUtil.getExtension(strPathFilename);
		if(fT.length()>0) {
			if(inputFormats.length>0) {
				if(Arrays.asList(inputFormats).contains(fT)) {
					return true;
				}else {
					return false;
				}
			}else {
				return false;
			}
		}else {
			return false;
		}
	}
	protected boolean FileExist(String strPathFilename) {
		File myfa = Paths.get(strPathFilename).toFile();
		if (myfa.exists() && myfa.isFile() && myfa.canRead()) {
			return true;
		} else {
			return false;
		}	
	}
	protected void saveJsonAsFile(String strPathFilename, JsonObject content) {
		Path path = Paths.get(strPathFilename);
		try {
			Files.write(path, content.encodePrettily().getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	protected void saveStringAsFile(String strPathFilename, String content) {
		Path path = Paths.get(strPathFilename);
		try {
			Files.write(path, content.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	protected JsonObject loadJsonFromFile(String strPathFilename) {
		try {
			//System.out.println("loadJsonFromFile: " +strPathFilename);
			String js = new String(Files.readAllBytes(Paths.get(strPathFilename)));
			if (js.length()>0) {
				return new JsonObject(js);
			}else {
				return new JsonObject();
			}
		} catch (IOException e) {

			e.printStackTrace();
			return null;
		}
	}
	protected void FileFolderDelete(File FileFolder) {
		if (FileFolder.exists() && FileFolder.isDirectory() ) {
			try {
				System.gc();
				FileUtils.deleteDirectory(FileFolder);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
	    	if (!FileFolder.delete()) {
	    				FileFolder.deleteOnExit();
	    	}
	    }	
	}	
	protected boolean FolderExist(String strFolderName) {
		File myfa = Paths.get(strFolderName).toFile();
		if (myfa.exists() && myfa.isDirectory() ) {
			return true;
		} else {
			return false;
		}	
	}
	protected void MakeDirs(String strFolderName) {	
		if (!FolderExist(strFolderName)) {
			try {
				FileUtils.forceMkdir(new File(Paths.get(strFolderName).toString()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	protected boolean FileFolderRename(String strFileName, String strNewFileName) {
		Boolean pbln=true;
		try {
			File oldName = new File(strFileName);
		    File newName = new File(strNewFileName);
		    System.gc();
		    FileUtils.moveDirectory(oldName, newName);
		    
		} catch (Exception  e) {
			pbln=false;
			e.printStackTrace();
		}
    		return pbln;
  	}


	@Override
	public void setVertx(Vertx vertx, BaseUtil util) {
		this.vertx = vertx;
		this.util = util;
		if (util.configApp().getBoolean("log.console.enable", false) ) LOGGER.setEnableConsoleOutput(true);
		if (util.configApp().getBoolean("log.serverlog.enable", false) ) LOGGER.enableServerLOG(vertx, null);
		
	}


}
