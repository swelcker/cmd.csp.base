package cmd.csp.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.github.chungkwong.classifier.BayesianClassifierFactory;
import com.github.chungkwong.classifier.Category;
import com.github.chungkwong.classifier.ClassificationResult;
import com.github.chungkwong.classifier.Classifier;
import com.github.chungkwong.classifier.ClassifierFactory;
import com.github.chungkwong.classifier.KNearestClassifierFactory;
import com.github.chungkwong.classifier.Starter;
import com.github.chungkwong.classifier.SvmClassifierFactory;
import com.github.chungkwong.classifier.TfIdfClassifierFactory;
import com.github.chungkwong.classifier.Trainable;
import com.github.chungkwong.classifier.validator.DataDivider;
import com.github.chungkwong.classifier.validator.DataSet;
import com.github.chungkwong.classifier.validator.Sample;
import com.github.chungkwong.classifier.validator.SplitDataSet;
import com.github.chungkwong.classifier.validator.Validator;

import cmd.csp.platform.CSPLogDelegate;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;



public class MachineLearning {


	protected final CSPLogDelegate LOGGER = new CSPLogDelegate(CSPLogDelegate.class.getName());;

	private float confidence=0.0f;

	private float treshold=0.0f;

	protected ClassifierFactory classifierFactory;

	protected Trainable<String> model;

	protected Classifier<String> classifier;

	protected List <Sample<String>> sam = new ArrayList<Sample<String>>();    

	protected Category catP= new Category("positive");
	protected Category catN= new Category("negative");
	protected Locale locl=Locale.GERMAN;
	
	  
	public MachineLearning() {
	}
	public void init() {
		classifierFactory=Starter.getDefaultClassifierFactory(locl);
		model= classifierFactory.createModel();
	}
	public void init(Locale locale) {
		locl = locale;
		this.init();
	}
	public void setLocale(Locale locale) {
		locl = locale;
		this.init();
	}
	public Locale getLocale() {
		return locl;
	}

	/* needs to be executed before you can call classify*/
	public void learnClassifier	() {
		classifier=classifierFactory.getClassifier(model);
	}
	
	public float getTreshold() {
		return treshold;
	}

	public void setTreshold(float treshold) {
		this.treshold = treshold;
	}


	public float Confidence() {
		return confidence;
	}
	

	public List<ClassificationResult> getCandidates(String strToClassify, int minTextLength, int maxCategories) {
		if (strToClassify == null  ) {
	      return null;
	    }
		if (strToClassify.isEmpty()  ) {
		      return null;
		}
		if (minTextLength == 0  ) {
			minTextLength = 3;
		}
		if (maxCategories == 0  ) {
			maxCategories = 1;
		}
		if (strToClassify.length()<minTextLength  ) {
		      return null;
		}
		
	    String currentText = "";
	    currentText = strToClassify;
	  	currentText = normalizeText(currentText);
	   	if(currentText.length()>minTextLength) {
		  	    List<ClassificationResult> res = classifier.getCandidates(currentText, maxCategories);
		  	    return res;
	   	}
		    return null;
	}
	public List<ClassificationResult> getCandidates(String strToClassify, int minTextLength) {
		if (strToClassify == null  ) {
	      return null;
	    }
		if (strToClassify.isEmpty()  ) {
		      return null;
		}
		if (minTextLength == 0  ) {
			minTextLength = 3;
		}
		if (strToClassify.length()<minTextLength  ) {
		      return null;
		}
		
	    String currentText = "";
	    currentText = strToClassify;
	  	currentText = normalizeText(currentText);
	   	if(currentText.length()>minTextLength) {
		  	    List<ClassificationResult> res = classifier.getCandidates(currentText);
		  	    return res;
	   	}
		    return null;
	}
	public Category classifyText(String strToClassify, int minTextLength) {
		if (strToClassify == null  ) {
	      return null;
	    }
		if (strToClassify.isEmpty()  ) {
		      return null;
		}
		if (minTextLength == 0  ) {
			minTextLength = 3;
		}
		if (strToClassify.length()<minTextLength  ) {
		      return null;
		}
		
	    String currentText = "";
	    currentText = strToClassify;
	  	currentText = normalizeText(currentText);
	   	if(currentText.length()>minTextLength) {
		  	    ClassificationResult res = classifier.classify(currentText);
		  	    if(res.getScore()>=this.getTreshold()) {
		  	    		System.out.println( "Class: "+
		  	            	    res.getCategory() +
		  	                    " %: "+ res.getScore()*100.0 + " Block: "+currentText);
		  	    		return res.getCategory();
		  	    }
	  	}
		    return null;
	}	

	public void trainPositive(JsonObject jcon) {
		trainModel(jcon, catP );
	}
	public void trainNegativ(JsonObject jcon) {
		trainModel(jcon, catN );
	}
	public void trainNewCategory(JsonObject jcon, String newCategoryName) {
		if(newCategoryName==null) return;
		if(newCategoryName.length()<1) return;
		
		Category newCat= new Category(newCategoryName);
		trainModel(jcon, newCat );
	}
	
	protected void trainModel(JsonObject jcon, Category category ) {
		if (jcon == null  ) {
	      return ;
	    }

		
	    for (String key : jcon.fieldNames()) {
	    	if(jcon.getValue(key) instanceof JsonArray) {
	    		JsonArray jarr = jcon.getJsonArray(key);
		          for (int i=0; i < jarr.size(); i++) {
		              Object arrObj = jarr.getValue(i);
		              if (arrObj instanceof String) {
		              	String currentText=(String) arrObj;
		              	currentText = normalizeText(currentText);
		          	    if(currentText!=null) {
		        	    	if(currentText.length()>1) {
		        	    		model.train(currentText, category);
		         					sam.add(new Sample<>(currentText,category));
		         					
		         					System.out.println( "Trained Class: "+
		        	            	    category.getName() +
		        	                    " Data:"+currentText);
		
		        	    	}
		        	    }
		              }
		          }


	    	}
	    }


		
	    return ;
}
	public void logValidator(){
	    
		Validator<String> validator=new Validator<>();
		ClassifierFactory[] classifierFactories=getStandardClassifierFactories(locl);
	
		DataSet<String> dataset=new DataSet<>(()->sam.stream(),"PDFXtractor.ML");
		SplitDataSet[] splitDataSets=getSplitDataSets(dataset);
		
		validator.validate(splitDataSets,classifierFactories);
	
		System.out.println(validator.toString());
		System.out.println("Best: "+ validator.selectMostAccurate().toString());
 }
	private static ClassifierFactory[] getStandardClassifierFactories(Locale locale){
		ClassifierFactory factory1=Starter.getDefaultClassifierFactory(locale,false,new TfIdfClassifierFactory());
		ClassifierFactory factory2=Starter.getDefaultClassifierFactory(locale,false,new BayesianClassifierFactory());
		ClassifierFactory factory3=Starter.getDefaultClassifierFactory(locale,false,new KNearestClassifierFactory().setK(3));
		ClassifierFactory factory4=Starter.getDefaultClassifierFactory(locale,false,new SvmClassifierFactory());
		return new ClassifierFactory[]{factory1,factory2,factory3,factory4};
	}
	private static SplitDataSet[] getSplitDataSets(DataSet<String> dataSet){
		return new SplitDataSet[]{
			DataDivider.randomSplit(dataSet,0.5),
			DataDivider.sequentialSplit(dataSet,0.5),
			DataDivider.noSplit(dataSet)};
	}

public String normalizeText(String inputText) {
	  String currentText = inputText;
	currentText = currentText.replace("@", "");
	currentText = currentText.replace("€", "");
	currentText = currentText.replace("$", "");
	currentText = currentText.replace("%", "");
	currentText = currentText.replace("&", "");
	currentText = currentText.replace("!", "");
	currentText = currentText.replace("*", "");
	currentText = currentText.replace("#", "");
	currentText = currentText.replace("_", " ");
	currentText = currentText.replace(",", "");
	currentText = currentText.replace(".", "");
	currentText = currentText.replace(":", "");
	currentText = currentText.replace(";", "");
	currentText = currentText.replace("-", "");
	currentText = currentText.replace("0", "");
	currentText = currentText.replace("1", "");
	currentText = currentText.replace("2", "");
	currentText = currentText.replace("3", "");
	currentText = currentText.replace("4", "");
	currentText = currentText.replace("5", "");
	currentText = currentText.replace("6", "");
	currentText = currentText.replace("7", "");
	currentText = currentText.replace("8", "");
	currentText = currentText.replace("9", "");
	currentText = currentText.replace("  ", " ");
	return currentText.trim();
}
	public String convertTextNoNum(String inputText) {
		String searchText = inputText;
		searchText = searchText.replace("0", "");
		searchText = searchText.replace("1", "");
		searchText = searchText.replace("2", "");
		searchText = searchText.replace("3", "");
		searchText = searchText.replace("4", "");
		searchText = searchText.replace("5", "");
		searchText = searchText.replace("6", "");
		searchText = searchText.replace("7", "");
		searchText = searchText.replace("8", "");
		searchText = searchText.replace("9", "");
		searchText = searchText.replace(",", "");
		searchText = searchText.replace(".", "");
		searchText = searchText.replace("{", "");
		searchText = searchText.replace("}", "");
		searchText = searchText.replace("[", "");
		searchText = searchText.replace("]", "");
		searchText = searchText.replace(";", "");
		searchText = searchText.replace(":", "");
		searchText = searchText.replace("(", "");
		searchText = searchText.replace(")", "");
		return searchText;
	}



}
