package cmd.csp.processors;


import java.io.File;
import java.io.IOException;

import org.rauschig.jarchivelib.ArchiveEntry;
import org.rauschig.jarchivelib.ArchiveFormat;
import org.rauschig.jarchivelib.ArchiveStream;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.rauschig.jarchivelib.CompressionType;

import cmd.csp.base.BaseProcessor;
import cmd.csp.interfaces.IProcessor;
import cmd.csp.utils.UtilsBase;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ProcessorArchive extends BaseProcessor implements IProcessor {

	private Boolean isInit=false;	
	private JsonObject jcon=new JsonObject() ;
	private JsonArray jarr=new JsonArray() ;
	private String strConfKey = this.getClass().getSimpleName().toLowerCase().replace("processor", "config");
	private Archiver archiver=null;
	
	@Override
	public void PrepProcessing(Handler<AsyncResult<Void>> resultHandler) {
		boolean ret = this.Init();
		super.PrepProcessing(resultHandler);
	}

	@Override
	public void applyConfig(JsonObject config) {

		if(config.containsKey(strConfKey)) {
			jcon = config.getJsonObject(strConfKey);
			LOGGER.debug(this.getClass().getName()+":applyConfig:BeforeWith: " + jcon.encode());
			for(String field : jcon.fieldNames()) {

				
			}
		}

		super.applyConfig(config);
	}

	@Override
	public void setInput(JsonObject content) {
		super.setInput(content);
	}
	
	@Override
	public void Process(Handler<AsyncResult<JsonObject>> resultHandler) {
		jarr = new JsonArray();
		jcon = new JsonObject();
		
		boolean ret = this.Init();
		JsonArray Result=null;
		String Input="";
		
		for(String field : input.fieldNames()) {
				String val = input.getString(field);
				Input = Input + " "+ val;
		}
		Input = Input.trim();

		Result = getResult(Input);
		result.put(this.getClass().getSimpleName().toLowerCase().replace("processor", ""), Result);
		super.Process(resultHandler);
	}

	@Override
	public JsonObject getResult() {
		return super.getResult();
	}

	@Override
	public void PostProcessing(Handler<AsyncResult<Void>> resultHandler) {
		super.PostProcessing(resultHandler);
	}

	// ############################### 

	public Boolean Init() {
		if(isInit) return true;
			archiver = ArchiverFactory.createArchiver(ArchiveFormat.ZIP, CompressionType.GZIP);
			isInit=true;

			LOGGER.debug("CSP Application: " + this.getClass().getName() +":Loaded and initialized ");

		return isInitialized();
	}
	
	public JsonArray getResult(String strContent) {
		// System.out.println(this.getClass().getName()+":getResult: " + strContent);
		if(!isInitialized()) return null;
		if(strContent==null) return null;
		if(strContent.isEmpty()) return null;
		jarr.clear();
		LOGGER.debug(this.getClass().getName()+":getResult:ReadyToGetResult: " + strContent);
		if(strContent!=null) {

		}
		return jarr;
	}
	
	public Boolean isInitialized() {
		return isInit;
	}
	protected void getArchiveContentList(File archive) {
		ArchiveStream stream = null;
		ArchiveEntry entry;

		try {
			stream = archiver.stream(archive);
			while((entry = stream.getNextEntry()) != null) {
			    // access each archive entry individually using the stream
			    // or extract it using entry.extract(destination)
			    // or fetch meta-data using entry.getName(), entry.isDirectory(), ...
			}
			stream.close();	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	protected void createArchiveFile(File fileorfolderInput, File folderOutput, String archiveName ) {
		if(fileorfolderInput==null || folderOutput==null) return;
		if(archiveName==null || archiveName.isEmpty()) archiveName = "CSPArchive-" + UtilsBase.createUUIDString();

		Archiver archiver = ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP);
		try {
			File archive = archiver.create(archiveName, folderOutput, fileorfolderInput);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
		
}

