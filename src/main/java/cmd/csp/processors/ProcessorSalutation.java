package cmd.csp.processors;

import cmd.csp.base.BaseProcessor;
import cmd.csp.interfaces.IProcessor;
import cmd.csp.platform.CSPConstants;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

public class ProcessorSalutation extends BaseProcessor implements IProcessor {

	public JsonObject Salutations = new JsonObject();
	private JsonObject UnknownSalutations = new JsonObject();
	private String strConfKey = this.getClass().getSimpleName().toLowerCase().replace("processor", "config");
	public String salutFileName = CSPConstants.DEFAULT_CONFIG_FILEPATH  + strConfKey + "/csp_controllersalutation.dic";
	public String salutUnknownFileName = CSPConstants.DEFAULT_CONFIG_FILEPATH + strConfKey + "/csp_unknownsalutation.dic";

	private Boolean isSalutationInit=false;	
	private Integer SalutationCount=0;

	@Override
	public void PrepProcessing(Handler<AsyncResult<Void>> resultHandler) {
		// TODO Auto-generated method stub
		boolean ret = false;
		ret = this.InitSalutations();
		super.PrepProcessing(resultHandler);
	}

	@Override
	public void Process(Handler<AsyncResult<JsonObject>> resultHandler) {
		
		boolean ret = this.InitSalutations();
		String Result="";
		String Input0="";
		String Input1="";
		if(input.containsKey("0")) Input0 = input.getString("0").toLowerCase();
		if(input.containsKey("1")) Input1 = input.getString("1").toUpperCase();
		Result = getSalutation(Input0, Input1);
		result.put("salutation", Result);
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



 
		 public ProcessorSalutation() {
				// TODO Auto-generated constructor stub
			 Salutations.put("de_M", "r Herr");
			 Salutations.put("de_F", " Frau");

			 Salutations.put("en_M", "Sir");
			 Salutations.put("en_F", "Madam");

			 Salutations.put("it_M", "Signor");
			 Salutations.put("it_F", "Signora");
			 
			 Salutations.put("es_M", "Señor");
			 Salutations.put("es_F", "Señora");

			 Salutations.put("fr_M", "Monsieur");
			 Salutations.put("fr_F", "Madame");

			 Salutations.put("ru_M", "Уважаемый");
			 Salutations.put("ru_F", "Уважаемая");

			 Salutations.put("ar_M", "Sa'adat Assayid");
			 Salutations.put("ar_F", "Sa'adat As'Sayyidah");

			 Salutations.put("ca_M", "Distingit senyor");
			 Salutations.put("ca_F", "Distingida senyora");

			 Salutations.put("nl_M", "Geachte heer");
			 Salutations.put("nl_F", "Geachte mevrouw");

/*			 be Belarusian
			 br Breton
			 bg Bulgarian
			 cs Czech
			 da Danish
			 el Greek
			 et Estonian
			 fi Finnish
			 he Hebrew
			 hi Hindi
			 hr Croatian
			 hu Hungarian
			 ja Japanese
			 lt Lithuanian
			 no Norwegian
			 pl Polish
			 pt Portuguese
			 ro Romanian
			 sk Slovak
			 sl Slovene
			 sv Swedish
			 tr Turkish
			 uk Ukrainian
			 zh-cn Simplified Chinese
			 zh-tw Traditional Chinese*/
 }


			public Boolean InitSalutations() {
				if(isSalutationInit) return true;
				if (!FileExist(salutFileName)) {
					isSalutationInit=false;
					SalutationCount = 0;
					SaveSalutations();
				}
					Salutations = Salutations.mergeIn(loadJsonFromFile(salutFileName),true);
					isSalutationInit=true;
					SalutationCount = Salutations.size();
					LOGGER.info("ControllerSalutation:Loaded and initialized salutations with "+ SalutationCount + " entries.");
				
				if (!FileExist(salutUnknownFileName)) {
				}else {
					UnknownSalutations = loadJsonFromFile(salutUnknownFileName);
				}
				return isSalutationsInitialized();
			}
			
			public String getSalutation(String strLanguage, String strGenderMF) {
				if(!isSalutationsInitialized()) return "";
				if(strLanguage==null) return null;
				if(strLanguage.isEmpty()) return null;
				if(strGenderMF==null) return null;
				if(strGenderMF.isEmpty()) return null;

				String sN = strLanguage.toLowerCase().trim()+"_"+strGenderMF.toUpperCase();
				if(!isSalutationInit) {
					this.InitSalutations();
				}
				if(SalutationCount>0) {
					if(Salutations.containsKey(sN)) {
						return Salutations.getString(sN);
					}else {
						UnknownSalutations.put(sN, "?");
						saveJsonAsFile(salutUnknownFileName, UnknownSalutations);
						return "?";
					}
		
				}
				return "";
				
			}
			public Boolean AddSalutationMale(String strLanguage, String strSalutation) {
				return AddToSalutations(strLanguage+ "_M", strSalutation);
			}
			public Boolean AddSalutationFemale(String strLanguage, String strSalutation) {
				return AddToSalutations(strLanguage+ "_F", strSalutation);
			}
			private Boolean AddToSalutations(String Key, String Value) {
				if(Salutations.containsKey(Key) || (Value==null)) {
					return false;
				}else {
					Salutations.put(Key, Value);
					return true;
				}
			}
			public void SaveSalutations() {
				saveJsonAsFile(salutFileName, Salutations);
			}

			public Boolean isSalutationsInitialized() {
				return isSalutationInit;
			}

			public Integer getSalutationsCount() {
				return SalutationCount;
			}
			
}

