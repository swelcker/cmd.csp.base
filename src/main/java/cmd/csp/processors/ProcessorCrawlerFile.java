package cmd.csp.processors;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.modeshape.common.util.FileUtil;

import cmd.csp.base.BaseProcessor;
import cmd.csp.interfaces.IProcessor;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ProcessorCrawlerFile extends BaseProcessor implements IProcessor {

	private Boolean isInit=false;	
	private Boolean isRunning=false;	
	private JsonObject jcon=new JsonObject() ;
	private JsonArray jarr=new JsonArray() ;
	private String strConfKey = this.getClass().getSimpleName().toLowerCase().replace("processor", "config");

	private String crawlerID="";
	private String crawlerExtensions="";
	private Boolean extensionCaseSensitive=false;
	private Boolean deleteFileAfterImport=false;
	private JsonArray arrayInput=null;
	private JsonArray arrayTrain=null;
	private JsonObject jconTenants=null ;
	private JsonObject jconProjects=null ;
	private JsonObject jconTenant=null ;
	private JsonObject jconProject=null ;
	
	private int maxDocuments=-1;
	
	private Map<String, List<File>> fileList = new HashMap<String, List<File>>();
	private Map<String, List<File>> fileListTrain = new HashMap<String, List<File>>();

	private List<File> currentFileList = null;
	private List<File> currentFileListTrain =null;

	private String currentSourceDir="";
	private String currentTenantProject="";
	private InputStream inputStream;


	
	@Override
	public void PrepProcessing(Handler<AsyncResult<Void>> resultHandler) {
		if(isInit) {
			super.PrepProcessing(resultHandler);
			return;
		}
		isInit=true;
		super.PrepProcessing(resultHandler);
	}
	@Override
	public void Process(Handler<AsyncResult<JsonObject>> resultHandler) {
		if(isRunning || jconTenants==null || jconTenants.isEmpty()) {
			super.Process(resultHandler);
			return;
		}
	      vertx.<Void>executeBlocking(future -> {
	    	  	isRunning = true;

	    	  	if(CheckFileListEmpty()) CreateFileLists();
 	    	  	if(!CheckFileListEmpty()) ProcessFileList();
	    	  	
	    	  	future.complete();
	        }, res -> {

	          if (res.succeeded()) {
	        	  isRunning = false;
	          } else {
	        	  isRunning = false;
	          }
	        });

		super.Process(resultHandler);
	}
	private void ProcessFileList() {
		int icount = 0;
		int icountT = 0;
		if(fileList==null && fileListTrain==null) return ;
		if(fileList.isEmpty() && fileListTrain.isEmpty()) return ;
		if(fileList!=null ) {
			for(String sK:fileList.keySet()) {
				List<File> lf = fileList.get(sK);
				if(lf!=null) {
					if(!lf.isEmpty()) {
						icount = 0;
						processFiles(lf);
					}
				}
			}
		}
		if(fileListTrain!=null ) {
			for(String sK:fileListTrain.keySet()) {
				List<File> lf = fileListTrain.get(sK);
				if(lf!=null) {
					if(!lf.isEmpty()) {
						icountT = 0;
						processFiles(lf);
					}
				}
			}
		}	
	}
	
	private Boolean CheckFileListEmpty() {
		Boolean ret = true;
		int icount = 0;
		if(fileList==null && fileListTrain==null) return ret;
		if(fileList.isEmpty() && fileListTrain.isEmpty()) return ret;
		if(fileList!=null ) {
			for(String sK:fileList.keySet()) {
				List<File> lf = fileList.get(sK);
				if(lf!=null) {
					if(!lf.isEmpty()) {
						icount = icount+lf.size();
					}
				}
			}
		}
		if(fileListTrain!=null ) {
			for(String sK:fileListTrain.keySet()) {
				List<File> lf = fileListTrain.get(sK);
				if(lf!=null) {
					if(!lf.isEmpty()) {
						icount = icount+lf.size();
					}
				}
			}
		}	
		if(icount>0) ret = false;
		return ret;
	}
	private void CreateFileLists() {
		
	  	for(Entry<String, Object> jTenant: jconTenants) {
	  		// Ein tenant nun projects
	  		jconTenant = (JsonObject) jTenant.getValue();
	  		currentTenantProject=jTenant.getKey();
	  		jconProjects = jconTenant.getJsonObject("projects");
    	  	for(Entry<String, Object> jProjects: jconProjects) {
    	  		// Ein project 
    	  		jconProject = (JsonObject) jProjects.getValue();
    	  		currentTenantProject=currentTenantProject +"@"+ jProjects.getKey();
    	  		arrayInput=null;
    	  		arrayTrain=null;
	    	  	for(Entry<String, Object> jPaths: jconProject) {
	    	  		//  nun paths
	    	  		if(jPaths.getKey().toLowerCase().contentEquals("input.paths")) arrayInput = (JsonArray) jPaths.getValue();
	    	  		if(jPaths.getKey().toLowerCase().contentEquals("train.paths")) arrayTrain = (JsonArray) jPaths.getValue();
	    	  	}
	    	  	if(arrayInput!=null) {
	    	  		List<File> lf = new ArrayList<File>();
	    	  		currentFileList = lf;
	    	  		fileList.put(currentTenantProject, lf);
	    	  		for(int i=0; i<arrayInput.size(); i++) {
	    	  			currentSourceDir = arrayInput.getString(i);
	    	  			if(currentSourceDir!=null && !currentSourceDir.isEmpty()) {
	    	  				crawlFiles(currentSourceDir, lf);
	    	  			}
	    	  		}
	    	  	}
	    	  	if(arrayTrain!=null) {
	    	  		List<File> lf = new ArrayList<File>();
	    	  		currentFileListTrain = lf;
	    	  		fileListTrain.put(currentTenantProject, lf);
	    	  		for(int i=0; i<arrayTrain.size(); i++) {
	    	  			currentSourceDir = arrayTrain.getString(i);
	    	  			if(currentSourceDir!=null && !currentSourceDir.isEmpty()) {
	    	  				crawlFiles(currentSourceDir, lf);
	    	  			}
	    	  		}
	    	  	}

	    	  	
    	  	}	    	  		
	  	}
	}
	private void crawlFiles(String dir, List<File> targetFileList) {
		if(dir==null) return;
		if(targetFileList.size()>=maxDocuments && maxDocuments>0) return;
		File files[] = new File(dir).listFiles();
		traverseFolder(files, targetFileList);
	}

	private void traverseFolder(File[] files, List<File> targetFileList) {
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					traverseFolder(file.listFiles(), targetFileList);
				} else {
					String type = FileUtil.getExtension(file.getName()).replace(".", "");
					if (type!=null && type.length() >0) {
						if (extensionCaseSensitive && crawlerExtensions.contains(type) || (!extensionCaseSensitive && crawlerExtensions.toLowerCase().contains(type.toLowerCase()))) {
							targetFileList.add(file);
						}
					}
				}
			}
		}
	}
	private void processFiles( List<File> targetFileList) {
		for (File file : targetFileList) {
				// TODO FileUtils.copyFile(file, new File(outputDir + "\\" + file.getName()));
		}
	}

	private void CleanUp() {
		// TODO delete empty folders in input dirs
	}
	@Override
	public void applyConfig(JsonObject config) {
		if(config.containsKey(strConfKey)) {
			jcon = config.getJsonObject(strConfKey);
			LOGGER.debug(this.getClass().getName()+":applyConfig:BeforeWith: " + jcon.encode());

			crawlerID=jcon.getString("id", "CSPFilesystemCrawler");
			crawlerExtensions=jcon.getString("extensions", "txt, html, pdf, tiff, zip, doc, docx");
			extensionCaseSensitive=jcon.getBoolean("extensions.casesensitive", false);
			deleteFileAfterImport=jcon.getBoolean("deletefileafterimport", true);
			
			jconTenants=jcon.getJsonObject("tenants", null);
			maxDocuments=jcon.getInteger("maxdocuments", 3);
		}

		super.applyConfig(config);
	}
	
	@Override
	public void StopProcessor(Handler<AsyncResult<Void>> resultHandler) {
		super.StopProcessor(resultHandler);
	}

	public Boolean isInitialized() {
		return isInit;
	}
		
}

