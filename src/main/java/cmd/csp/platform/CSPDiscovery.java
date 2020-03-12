package cmd.csp.platform;

import java.util.List;

import cmd.csp.base.BaseUtil;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;
import io.vertx.servicediscovery.Status;
import io.vertx.servicediscovery.types.MessageSource;



public class CSPDiscovery {
	public ServiceDiscovery discovery ;
	private Record record;
	protected Record publishedRecord;
	private String recordServiceName;
	@SuppressWarnings("unused")
	private String discoveryAddress;
	private String recordServiceInterface;
	private String recordType;
	private Status recordStatus= Status.UNKNOWN;
	private JsonObject location = new JsonObject();
	protected JsonObject recordMetaData = new JsonObject();
	private ServiceDiscoveryOptions discoveryOptions= new ServiceDiscoveryOptions();
	private Vertx vertx;
	private boolean isInitialized;
	private boolean isPublished;
	protected List<Record> listOfAvailableServices;
	protected JsonObject jsonOfAvailableServices;
	protected final CSPLogDelegate LOGGER = new CSPLogDelegate(CSPLogDelegate.class.getName());;
	protected BaseUtil util=null;

	// A service type is for example: `http-endpoint`, `data source`, `message source`. 
	public CSPDiscovery() {
		// TODO Auto-generated constructor stub
	}
	


	private void updateList( Handler<AsyncResult<Void>> resultHandler) {
		// TODO produce list
		if (listOfAvailableServices != null) listOfAvailableServices.clear();
		discovery.getRecords(new JsonObject(), ar -> {
			  if (ar.succeeded()) {
				  listOfAvailableServices = ar.result();
				  resultHandler.handle(Future.succeededFuture());
			  } else {
				    LOGGER.warn("ERR Failed to get list of available Services "+discovery.toString());
				    resultHandler.handle(Future.failedFuture(ar.cause()));
			    // lookup failed
			  }
			});
	}

	public void getListOfAvailableServices(Handler<AsyncResult<JsonObject>> resultHandler) {
		this.updateList(ar->{
			if (ar.succeeded()) {
				if (listOfAvailableServices != null) {
					jsonOfAvailableServices = new JsonObject();
					  listOfAvailableServices.forEach(tmpRec ->{
						  //System.out.println("Key:" +Integer.toString(tmpRec.hashCode())+" Json: " +tmpRec.toJson().toString());
						  jsonOfAvailableServices.put(Integer.toString(tmpRec.hashCode()), tmpRec.toJson());
				  });					
					  resultHandler.handle(Future.succeededFuture(jsonOfAvailableServices));				
				}else {
				    resultHandler.handle(Future.failedFuture("DiscoveryService list is empty"));					
				}
			}else {
			    resultHandler.handle(Future.failedFuture(ar.cause()));
			}
		});
	}

	public void getEntryOfAvailableServices(Integer ihashCode, Handler<AsyncResult<JsonObject>> resultHandler) {
		if (jsonOfAvailableServices != null) {			
			if (jsonOfAvailableServices.containsKey(Integer.toString(ihashCode))) {
				  resultHandler.handle(Future.succeededFuture(jsonOfAvailableServices.getJsonObject(Integer.toString(ihashCode))));
				
			}else {
			    resultHandler.handle(Future.failedFuture("DiscoveryService entry not found"));				
			}				
		}else {
		    resultHandler.handle(Future.failedFuture("DiscoveryService list is empty"));			
		}
		
	}
	public void Init(Vertx vx, BaseUtil util) {
		vertx = vx;
		discoveryAddress = CSPConstants.DEFAULT_DISCOVERY_ADDRESS.toString();
		discoveryOptions.setName(CSPConstants.DEFAULT_DISCOVERY_ADDRESS);
		discoveryOptions.setAnnounceAddress(CSPConstants.DEFAULT_DISCOVERY_ANNOUNCE_SUBADDRESS);
		discoveryOptions.setUsageAddress(CSPConstants.DEFAULT_DISCOVERY_USAGE_SUBADDRESS);
		
		discovery = ServiceDiscovery.create(vertx, discoveryOptions);
		isInitialized = true;
		Record record = MessageSource.createRecord(getRecordServiceName(), discoveryAddress, getRecordType(), new JsonObject());
		setRecord(record);
		setStatusDown();

		if (util.configApp().getBoolean("log.console.enable", false) ) LOGGER.setEnableConsoleOutput(true);
		if (util.configApp().getBoolean("log.serverlog.enable", false) ) LOGGER.enableServerLOG(vertx, null);

	}

	public void Publish(Handler<AsyncResult<Void>> resultHandler) {
		if (isInitialized) {
			setStatusUp();
			getRecord().setLocation(location);
			getRecord().setMetadata(recordMetaData);
			getRecord().setName(recordServiceName);
			getRecord().setStatus(recordStatus);
			getRecord().setType(recordType);
			
			discovery.publish(getRecord(), ar -> {
				  if (ar.succeeded()) {
				    // publication succeeded
					  publishedRecord = ar.result();
					  isPublished = true;
					  LOGGER.info("ServiceDiscovery Published " + getRecord().getName() + " RecordID: "+getRecord().getRegistration());
					  resultHandler.handle(Future.succeededFuture());
				  } else {
				    LOGGER.warn("ServiceDiscovery Failed to Publish " + getRecord().getName() + " Record: "+ar.cause());
				    resultHandler.handle(Future.failedFuture(ar.cause()));
				  }
			});		
		}
	}
	public void UnPublish(Handler<AsyncResult<Void>> resultHandler) {
		if (isPublished) {
			discovery.unpublish(publishedRecord.getRegistration(), ar -> {
				  if (ar.succeeded()) {
				    // Ok
					  LOGGER.info("ServiceDiscovery UnPublished RecordID: "+publishedRecord.getRegistration());
					  resultHandler.handle(Future.succeededFuture());
			  } else {
				    // cannot un-publish the service, may have already been removed, or the record is not published
				  LOGGER.warn("ServiceDiscovery Failed to UnPublish Record: "+ar.cause());
				    resultHandler.handle(Future.failedFuture(ar.cause()));
				  }
				});			
		}		
	}
	private void recNeedsUpdate() {
		if (isInitialized) {getRecord().setStatus(recordStatus);}
		if (isPublished) {
			publishedRecord.setStatus(recordStatus);
			discovery.update(publishedRecord,  ar->{
				if (ar.succeeded()) {
					LOGGER.info("ServiceDiscovery updated RecordID: "+publishedRecord.getRegistration());
				}else {
					LOGGER.warn("ServiceDiscovery Failed to update RecordID: "+publishedRecord.getRegistration());
					
				}
			});
		}
	}
	public void setStatusDown() {
		this.recordStatus = Status.DOWN;
		this.recNeedsUpdate();
	}
	public void setStatusOutOfService() {
		this.recordStatus = Status.OUT_OF_SERVICE;
		this.recNeedsUpdate();
	}	
	public void setStatusUnknown() {
		this.recordStatus = Status.UNKNOWN;
		this.recNeedsUpdate();
	}	
	public void setStatusUp() {
		this.recordStatus = Status.UP;
		this.recNeedsUpdate();
	}	
	public void setRecordStatus(Status recordStatus) {
		this.recordStatus = recordStatus;
		this.recNeedsUpdate();
	}
	public JsonObject getLocation() {
		return location;
	}
	public void setLocation(JsonObject location) {
		this.location = location;
	}
	public String getRecordStatus() {
		return recordStatus.toString();
	}
	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}
	public String getRecordServiceName() {
		return recordServiceName;
	}

	public void setRecordServiceName(String recordServiceName) {
		this.recordServiceName = recordServiceName;
		discoveryOptions.setName(recordServiceName);
		this.recNeedsUpdate();
	}

	public String getAnnounceAddress() {
		return discoveryOptions.getAnnounceAddress();
	}
	public String getUsageAddress() {
		return discoveryOptions.getUsageAddress();
	}
	public void setDiscoveryAddress(String discoveryAddress) {
		this.discoveryAddress = discoveryAddress;
		discoveryOptions.setAnnounceAddress(discoveryAddress+CSPConstants.DEFAULT_DISCOVERY_ANNOUNCE_SUBADDRESS);
		discoveryOptions.setUsageAddress(discoveryAddress+CSPConstants.DEFAULT_DISCOVERY_USAGE_SUBADDRESS);
		this.recNeedsUpdate();
	}

	public String getRecordServiceInterface() {
		return recordServiceInterface;
	}

	public void setRecordServiceInterface(String recordServiceInterface) {
		this.recordServiceInterface = recordServiceInterface;
	}

	public JsonObject getRecordMetaData() {
		return recordMetaData;
	}

	public void setRecordMetaData(JsonObject recordMetaData) {
		this.recordMetaData = recordMetaData;
		this.recNeedsUpdate();
	}
	public void Close() {
		if (isInitialized) discovery.close();

	}
	public Record getRecord() {
		return record;
	}
	public void setRecord(Record record) {
		JsonObject jcon = vertx.getOrCreateContext().config().copy();
		if(jcon.containsKey("app")) jcon.remove("app");
		if(jcon.containsKey("statichandler")) jcon.remove("statichandler");
		if(jcon.containsKey("addresses")) jcon.remove("addresses");
		
		JsonObject jf = new JsonObject();
		jf.put("server", jcon.copy());
		jcon = vertx.getOrCreateContext().config().copy();
		if(jcon.containsKey("app")) jf.put("app", jcon.getJsonObject("app").copy());
		if(jcon.containsKey("addresses")) jf.put("addresses", jcon.getJsonObject("addresses").copy());
		
		this.record = record;
		this.setRecordServiceName(record.getName());
		this.setLocation(record.getLocation());
		this.setRecordType(record.getType());
		this.setRecordStatus(record.getStatus());
		this.setRecordMetaData(jf);
		this.recNeedsUpdate();
	}

}
