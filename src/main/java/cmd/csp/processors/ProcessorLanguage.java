package cmd.csp.processors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.LocaleUtils;

import com.google.common.base.Optional;
import com.optimaize.langdetect.DetectedLanguage;
import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.i18n.LdLocale;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import com.optimaize.langdetect.text.RemoveMinorityScriptsTextFilter;
import com.optimaize.langdetect.text.TextObject;
import com.optimaize.langdetect.text.TextObjectFactory;
import com.optimaize.langdetect.text.TextObjectFactoryBuilder;
import com.optimaize.langdetect.text.UrlTextFilter;

import cmd.csp.base.BaseProcessor;
import cmd.csp.interfaces.IProcessor;
import cmd.csp.platform.CSPConstants;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ProcessorLanguage extends BaseProcessor implements IProcessor {

	//language Detection
	private List<LanguageProfile> languageProfiles;

	private LanguageDetector languageDetector;
	private TextObjectFactory textObjectFactory;
	private TextObject textObject;
	private com.google.common.base.Optional<LdLocale> lang;
	private Boolean isLanguageInit=false;	
	private String language="";
	private Optional<String> region;
	private Optional<String> script;
	private List<DetectedLanguage> listLang;
	private double minConfidence=0.49;
	private double probalityThreshold=0.19;
	private double leadingTextPercentage=0.5;
	private Integer maxTextLength=10000;
	private double minorityTextThreshold=0.3;
	private Boolean treatAsShortText=true;
	private Boolean treatAsLeadingText=false;
	private Boolean applyMinorityfilter=true;
	private String content="";
	private JsonArray disabledLanguages=null;
	private String strConfKey = this.getClass().getSimpleName().toLowerCase().replace("processor", "config");

	@Override
	public void PrepProcessing(Handler<AsyncResult<Void>> resultHandler) {
		// TODO Auto-generated method stub
		Init();
		super.PrepProcessing(resultHandler);
	}

	@Override
	public void Process( Handler<AsyncResult<JsonObject>> resultHandler) {
		String Result="";
		String Input="";
		if(input.containsKey("0")) Input = input.getString("0");
		setContent(Input);
		JsonObject jcon = new JsonObject();
		AddDetectionToJson(jcon);
		result.put("language", jcon);

		super.Process( resultHandler);
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
		if(config.containsKey(strConfKey)) {
			JsonObject con = config.getJsonObject(strConfKey);
			
			if (con!=null) {
				setTreatAsShortText(con.getBoolean("treatas.shorttext", false));
				setTreatAsLeadingText(con.getBoolean("treatas.leadingtext", true));
				setProbalityThreshold(con.getDouble("probability.threshold", 0.19));
				setMinorityTextThreshold(con.getDouble("minority.threshold", 0.3));
				setMinConfidence(con.getDouble("confidence.min", 0.49));
				setMaxTextLength(con.getInteger("textlength.max", 10000));
				setLeadingTextPercentage(con.getDouble("leadingtext.percent", 0.5));
				setDisabledLanguages(con.getJsonArray("languages.disable"));
				setApplyMinorityfilter(con.getBoolean("minority.enable", true));
			}
		}

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
	
	
	public ProcessorLanguage() {

		// TODO Auto-generated constructor stub
		try {
			languageProfiles= new LanguageProfileReader().readAllBuiltIn();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	private void disablelanguages() {
		if(disabledLanguages!=null) {
			if(disabledLanguages.size()>0) {
				List<LanguageProfile> lngp = new ArrayList<LanguageProfile>(); 
				languageProfiles.forEach(ltemp->{
					disabledLanguages.forEach(dl->{
						if( ((String) dl).toLowerCase().contains(ltemp.getLocale().getLanguage().toLowerCase())){
							lngp.add(ltemp);
						}
					});
					
				});
				if(lngp.size()>0) {
					lngp.forEach(lp->{
						languageProfiles.remove(lp);
					});
				}
			}
		}
	}
	public Boolean Init() {
		if(!isLanguageInitialized()) {
				disablelanguages();
					//build language detector:
					languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
				        .withProfiles(languageProfiles)
				        .minimalConfidence(minConfidence)
				        .probabilityThreshold(probalityThreshold)
				        .build();
					//create a text object factory
	
					if(isTreatAsShortText()) {
						textObjectFactory = new TextObjectFactoryBuilder()
				            .maxTextLength(maxTextLength)
			                .build();
						
					}else {
						if(isApplyMinorityfilter()) {
							textObjectFactory = new TextObjectFactoryBuilder()
				                .maxTextLength(maxTextLength)
				                .withTextFilter(UrlTextFilter.getInstance())
				                .withTextFilter(RemoveMinorityScriptsTextFilter.forThreshold(minorityTextThreshold))
				                .build();
							
						}else {
							textObjectFactory = new TextObjectFactoryBuilder()
				                .maxTextLength(maxTextLength)
				                .withTextFilter(UrlTextFilter.getInstance())
				                .build();
							
						}
					}
					isLanguageInit=true;

		}
		return isLanguageInit;
	}
    public Boolean ReInit() {
    	isLanguageInit=false;
    	return Init();
    }
 	public List<String> ProbabilitiesStringList() {
 		if(textObject.length()<1) return null;
		if(!isLanguageInit) return null; // Not ready while Init didnt happen

		listLang = languageDetector.getProbabilities(textObject);
		List<String> ls = new ArrayList<String>();
		
		String langc="";
		Integer ic = 0;
        for(DetectedLanguage dl:listLang) {
        	ic++;
        	langc = langc + dl.getLocale().getLanguage() + " (" +toPercentage(dl.getProbability(),2)+")";					            	
        	if(ic<listLang.size()) langc=langc+", ";
        	ls.add(langc);
        }		
		return ls;
		
	}
 	public String ProbabilitiesString() {
		if(!isLanguageInit) return ""; // Not ready while Init didnt happen
		if(textObject.length()<1) return "";

		listLang = languageDetector.getProbabilities(textObject);
		List<String> ls = new ArrayList<String>();
		
		String langc="";
		Integer ic = 0;
        for(DetectedLanguage dl:listLang) {
        	ic++;
        	langc = langc + dl.getLocale().getLanguage() + " (" +toPercentage(dl.getProbability(),2)+")";					            	
        	if(ic<listLang.size()) langc=langc+", ";
        	ls.add(langc);
        }		
		return langc;
		
	}
 	public List<DetectedLanguage> ProbabilitiesLanguages() {
		if(!isLanguageInit) return null; // Not ready while Init didnt happen
		if(textObject.length()<1) return null;
	    listLang = languageDetector.getProbabilities(textObject);
		return listLang;
		
	}
	public String Detect() {
		if(!isLanguageInit) return ""; // Not ready while Init didnt happen
		if(textObject.length()<1) return null;
	    lang = languageDetector.detect(textObject);

	    if(!lang.isPresent()) {
			this.ProbabilitiesLanguages();
			if(!listLang.isEmpty()) {
		        String langc="";
		        Integer ic=0;
		        for(DetectedLanguage dl:listLang) {
		        	ic++;
		        	langc = langc + dl.getLocale().getLanguage() + " (" +toPercentage(dl.getProbability(),2)+")";
		        	if(ic<listLang.size()) langc=langc+", ";
		        	dl.getLocale().getLanguage();
		        		language=dl.getLocale().getLanguage();
		        		region = dl.getLocale().getRegion();
		        	    script = dl.getLocale().getScript();
		        	    break;
		        }
			}else {
				language = "";
			}
				
			
		}else {
		    language = lang.get().getLanguage();
		    region = lang.get().getRegion();
		    script = lang.get().getScript();			
		}
	    
	    return this.getLanguage();
	}
	public void AddDetectionDefaultToJson(JsonObject jTo) {
		jTo.clear();
        jTo.put("selected", ("de"));
        jTo.put("languages", ("detection not active"));
    	jTo.put("locale_1", "de");
    	jTo.put("percentage_1", "100%");
        jTo.put("rule", "none");
        jTo.put("date", new Date().toString());
        jTo.put("ocr", LocaleUtils.toLocale("de").getISO3Language());
    	jTo.put("count", 1);

	}
	public void AddDetectionToJson(JsonObject jTo) {
			// language
		if(!isLanguageInit) return; // Not ready while Init didnt happen
		if(textObject.length()<1) return ;

		jTo.clear();
		String localStr=content.substring(0, (int) (content.length()*leadingTextPercentage));
		String lngShort="";
		String lngLong="";
		
		if(isTreatAsShortText()) {
	        jTo.put("rule", "none");
			this.Detect();
			this.ProbabilitiesLanguages();
			
		}else {
	        jTo.put("rule", "leading_language");
	        textObject = textObjectFactory.forText(localStr);
			this.Detect();
			this.ProbabilitiesLanguages();
		}
        jTo.put("date", new Date().toString());
		
		if(!listLang.isEmpty()) {
			Integer ic=0;
		     
	        String langc="";
	        for(DetectedLanguage dl:listLang) {
	        	ic++;
	        	langc = langc + dl.getLocale().getLanguage() + " (" +toPercentage(dl.getProbability(),2)+")";
	        	if(ic<listLang.size()) langc=langc+", ";
	        	jTo.put("locale."+ic, (dl.getLocale().getLanguage()));
	        	jTo.put("percentage_"+ic, (toPercentage(dl.getProbability(),2)));
	        	jTo.put("count",ic);
	        }
	        jTo.put("languages", (langc));
	        jTo.put("selected", (language));
	        jTo.put("locale.ocr", LocaleUtils.toLocale(language).getISO3Language());
		
		}
	}
	public String getLanguage() {
		return language;
	}
	public Optional<String> getRegion() {
		return region;
	}
	public Optional<String> getScript() {
		return script;
	}
	public Boolean isLanguageInitialized() {
		return isLanguageInit;
	}
	public TextObjectFactory getTextObjectFactory() {
		return textObjectFactory;
	}
	public TextObject getTextObject() {
		return textObject;
	}
	public List<LanguageProfile> getLanguageProfiles() {
		return languageProfiles;
	}
	public void setLanguageProfiles(List<LanguageProfile> languageProfiles) {
		this.languageProfiles = languageProfiles;
	}
	public double getMinConfidence() {
		return minConfidence;
	}
	public void setMinConfidence(double minConfidence) {
		this.minConfidence = minConfidence;
	}
	public double getProbalityThreshold() {
		return probalityThreshold;
	}
	public void setProbalityThreshold(double probalityThreshold) {
		this.probalityThreshold = probalityThreshold;
	}
	public double getLeadingTextPercentage() {
		return leadingTextPercentage;
	}
	public void setLeadingTextPercentage(double leadingTextPercentage) {
		if(leadingTextPercentage<0.) leadingTextPercentage = 1.0;
		if(leadingTextPercentage>1.0) leadingTextPercentage = 1.0;
	
		this.leadingTextPercentage = leadingTextPercentage;
	}
	public Integer getMaxTextLength() {
		return maxTextLength;
	}
	public void setMaxTextLength(Integer maxTextLength) {
		this.maxTextLength = maxTextLength;
	}
	public double getMinorityTextThreshold() {
		return minorityTextThreshold;
	}
	public void setMinorityTextThreshold(double minorityTextThreshold) {
		this.minorityTextThreshold = minorityTextThreshold;
	}
	public Boolean isTreatAsShortText() {
		return treatAsShortText;
	}
	public void setTreatAsShortText(Boolean treatAsShortText) {
		this.treatAsShortText = treatAsShortText;
	}
	public Boolean isTreatAsLeadingText() {
		return treatAsLeadingText;
	}
	public void setTreatAsLeadingText(Boolean treatAsLeadingText) {
		this.treatAsLeadingText = treatAsLeadingText;
	}
	public Boolean isApplyMinorityfilter() {
		return applyMinorityfilter;
	}
	public void setApplyMinorityfilter(Boolean applyMinorityfilter) {
		this.applyMinorityfilter = applyMinorityfilter;
	}


	public void setContent(String content) {
		this.content = content;

		this.content=this.content.replaceAll("[\\d.]", "");
		this.content=this.content.replaceAll("[+:-@<>/.,;#*]", " ");
 		textObject = textObjectFactory.forText(this.content);		   
	}
	public JsonArray getDisabledLanguages() {
		return disabledLanguages;
	}
	public void setDisabledLanguages(JsonArray disabledLanguages) {
		this.disabledLanguages = disabledLanguages;
	}

}
