package cmd.csp.processors;


import cmd.csp.base.BaseProcessor;
import cmd.csp.interfaces.IProcessor;
import cmd.csp.platform.CSPConstants;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

public class ProcessorGender extends BaseProcessor implements IProcessor {



	public JsonObject GenderNames = new JsonObject();
	private JsonObject UnknownGenderNames = new JsonObject();
	private String strConfKey = this.getClass().getSimpleName().toLowerCase().replace("processor", "config");
	public String dicFileName = CSPConstants.DEFAULT_CONFIG_FILEPATH + strConfKey + "/csp_controllergender.dic";
	public String dicUnknownFileName = CSPConstants.DEFAULT_CONFIG_FILEPATH + strConfKey + "/csp_unknowngender.dic";

	private Boolean isGenderInit=false;	
	private Integer GenderCount=0;

	 //###
	 public String lettersLower = "abcdefghijklmnopqrstuvwxyz";
	 public String lettersUpper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	 public String umlautLower = "àáâãåäæçðèéêëìíîïñòóôõöøœšßþùúûüýÿ";
	 public String umlautUpper = "ÀÁÂÃÅÄÆÇÐÈÉÊËÌÍÎÏÑÒÓÔÕÖØŒŠßÞÙÚÛÜÝŸ";
	 public JsonObject unicodeChars = new JsonObject();

		@Override
		public void PrepProcessing(Handler<AsyncResult<Void>> resultHandler) {
			// TODO Auto-generated method stub
			boolean ret = false;
			ret = this.InitNames();
			super.PrepProcessing(resultHandler);
		}

		@Override
		public void Process(Handler<AsyncResult<JsonObject>> resultHandler) {
			boolean ret = this.InitNames();
			String Result="";
			String Input="";
			if(input.containsKey("0")) Input = input.getString("0");
			Result = getGender(Input);
			Result = Result.replace("F", "Female");
			Result = Result.replace("M", "Male");
			result.put("gender", Result);
			super.Process(resultHandler);
		}
		
		@Override
		public void PostProcessing(Handler<AsyncResult<Void>> resultHandler) {
			// TODO Auto-generated method stub
			super.PostProcessing(resultHandler);
		}

		@Override
		public void applyConfig(JsonObject config) {
			// TODO Auto-generated method stub
			super.applyConfig(config);
		}

		@Override
		public void setInput(JsonObject content) {
			// TODO Auto-generated method stub
			super.setInput(content);
		}

		@Override
		public JsonObject getResult() {
			return super.getResult();
		}
 
		 public ProcessorGender() {
			 unicodeChars.put("256", "<A/>");
			 unicodeChars.put("257", "<a/>");
			 unicodeChars.put("258", "<A^>");
			 unicodeChars.put("258", "<Â>"); 
			 unicodeChars.put("259", "<a^>");
			 unicodeChars.put("259", "<â>"); 
			 unicodeChars.put("260", "<A,>");
			 unicodeChars.put("261", "<a,>");
			 unicodeChars.put("262", "<C´>");
			 unicodeChars.put("263", "<c´>");
			 unicodeChars.put("268", "<C^>");
			 unicodeChars.put("268", "<CH>");
			 unicodeChars.put("269", "<c^>");
			 unicodeChars.put("269", "<ch>");
			 unicodeChars.put("271", "<d´>");
			 unicodeChars.put("272", "<Ð>");
			 unicodeChars.put("272", "<DJ>");
			 unicodeChars.put("273", "<ð>");
			 unicodeChars.put("273", "<dj>");
			 unicodeChars.put("274", "<E/>");
			 unicodeChars.put("275", "<e/>");
			 unicodeChars.put("278", "<E°>");
			 unicodeChars.put("279", "<e°>");
			 unicodeChars.put("280", "<E,>");
			 unicodeChars.put("281", "<e,>");
			 unicodeChars.put("282", "<E^>");
			 unicodeChars.put("282", "<Ê>");
			 unicodeChars.put("283", "<e^>");
			 unicodeChars.put("283", "<ê>");
			 unicodeChars.put("286", "<G^>");
			 unicodeChars.put("287", "<g^>");
			 unicodeChars.put("290", "<G,>");
			 unicodeChars.put("291", "<g´>");
			 unicodeChars.put("298", "<I/>");
			 unicodeChars.put("299", "<i/>");
			 unicodeChars.put("304", "<I°>");
			 unicodeChars.put("305", "<i>");
			 unicodeChars.put("306", "<IJ>");
			 unicodeChars.put("307", "<ij>");
			 unicodeChars.put("310", "<K,>");
			 unicodeChars.put("311", "<k,>");
			 unicodeChars.put("315", "<L,>");
			 unicodeChars.put("316", "<l,>");
			 unicodeChars.put("317", "<L´>");
			 unicodeChars.put("318", "<l´>");
			 unicodeChars.put("321", "<L/>");
			 unicodeChars.put("322", "<l/>");
			 unicodeChars.put("325", "<N,>");
			 unicodeChars.put("326", "<n,>");
			 unicodeChars.put("327", "<N^>");
			 unicodeChars.put("328", "<n^>");
			 unicodeChars.put("336", "<Ö>");
			 unicodeChars.put("337", "<ö>");
			 unicodeChars.put("338", "<OE>");
			 unicodeChars.put("338", "Œ"); 
			 unicodeChars.put("339", "<oe>");
			 unicodeChars.put("339", "œ"); 
			 unicodeChars.put("344", "<R^>");
			 unicodeChars.put("345", "<r^>");
			 unicodeChars.put("350", "<S,>");
			 unicodeChars.put("351", "<s,>");
			 unicodeChars.put("352", "<S^>");
			 unicodeChars.put("352", "Š"); 
			 unicodeChars.put("352", "<SCH>");
			 unicodeChars.put("352", "<SH>");
			 unicodeChars.put("353", "<s^>");
			 unicodeChars.put("353", "š");  
			 unicodeChars.put("353", "<sch>");
			 unicodeChars.put("353", "<sh>");
			 unicodeChars.put("354", "<T,>");
			 unicodeChars.put("355", "<t,>");
			 unicodeChars.put("357", "<t´>");
			 unicodeChars.put("362", "<U/>");
			 unicodeChars.put("363", "<u/>");
			 unicodeChars.put("366", "<U°>");
			 unicodeChars.put("367", "<u°>");
			 unicodeChars.put("370", "<U,>");
			 unicodeChars.put("371", "<u,>");
			 unicodeChars.put("379", "<Z°>");
			 unicodeChars.put("380", "<z°>");
			 unicodeChars.put("381", "<Z^>");
			 unicodeChars.put("382", "<z^>");
			 unicodeChars.put("7838", "<ß>"); 
			 
 
 }



			/**
			 * Get all names for a specified gender
			 *
			 * @param string $gender
			 * @return array
			 */
			public Boolean InitNames() {
				if(isGenderInit) return true;
				LOGGER.debug("CSP Application: ControllerGender: Start InitNames ");
				if (!FileExist(dicFileName)) {
					isGenderInit=false;
					GenderCount = 0;
					LOGGER.debug("ControllerGender Dictionary not exists: "+ dicFileName);
				}else {
					LOGGER.debug("ControllerGender InitNames:loadDictionaryFromFile "+ dicFileName);
					GenderNames = loadJsonFromFile(dicFileName);
					isGenderInit=true;
					GenderCount = GenderNames.size();
					LOGGER.info("ControllerGender Initialized with "+ GenderCount + " entries.");
				}
				if (!FileExist(dicUnknownFileName)) {
				}else {
					UnknownGenderNames = loadJsonFromFile(dicUnknownFileName);
				}
				return isGenderInitialized();
			}
			
			public String getGender(String strGivenName) {
				if(!isGenderInitialized()) return "Not initialized";
				if(strGivenName!=null) {
					String sN = strGivenName.toLowerCase().trim().replace(" ", "").replace("-", "");
					LOGGER.debug("ControllerGender Key: " + sN);

					if(GenderCount>0) {
						if(GenderNames.containsKey(sN)) {
							LOGGER.debug("ControllerGender Contains Key: " + sN);
							LOGGER.debug("ControllerGender Gender: " + GenderNames.getString(sN));
							return GenderNames.getString(sN);
						}else {
							UnknownGenderNames.put(sN, "?");
							saveJsonAsFile(dicUnknownFileName, UnknownGenderNames);
							return "?";
						}
			
					}
				}
				return "unknown";
				
			}
			public Boolean AddMale(String GivenName) {
				return AddToDictionary(GivenName, "M");
			}
			public Boolean AddFemale(String GivenName) {
				return AddToDictionary(GivenName, "F");
			}
			private Boolean AddToDictionary(String GivenName, String strGenderMF) {
				if(GenderNames.containsKey(GivenName) || (strGenderMF==null)) {
					return false;
				}else {
					GenderNames.put(GivenName, strGenderMF);
					return true;
				}
			}
			public void SaveDictionary() {
				saveJsonAsFile(dicFileName, GenderNames);
			}

			public Boolean isGenderInitialized() {
				return isGenderInit;
			}

			public Integer getGenderCount() {
				return GenderCount;
			}
			
}

