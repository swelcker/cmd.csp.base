package cmd.csp.engine;

import static org.simmetrics.builders.StringMetricBuilder.with;

import java.util.ArrayList;
import java.util.Collection;

import org.simmetrics.StringMetric;
import org.simmetrics.metrics.CosineSimilarity;
import org.simmetrics.simplifiers.Simplifiers;
import org.simmetrics.tokenizers.Tokenizers;

import cmd.csp.platform.CSPLogDelegate;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;



public class Similarity {


	private StringMetric metric;
	private float resultSimilarity=0.0f;
	private FuzzySearch fuzzy;
	private float resultFuzzy=0.0f;
	private float confidence=0.0f;
	private String searchText="";
	private String foundText="";
	private float treshold=0.0f;
		  
	protected final CSPLogDelegate LOGGER = new CSPLogDelegate(CSPLogDelegate.class.getName());;
	  
	public Similarity() {
	    metric =
	    		with(new CosineSimilarity<String>())
	    		.simplify(Simplifiers.replaceNonWord())
	    		.tokenize(Tokenizers.whitespace())
	    		.build();
	}
	public float confidenceHitMetrics(String text, String search) {
		this.setSearchText(text);
		search = search.replace("_", " ");
		search = search.replace("*", "");
		search = search.replace("#", "");
		
		resultSimilarity = metric.compare(this.searchText , search);
		
		return resultSimilarity;
	}	
	public boolean hasHitMetrics(String text, String search) {
		boolean ret=false;
		
		confidence = confidenceHitMetrics(text , search);
		
		if (confidence >= treshold ) {
			ret = true;
			foundText = search;
			
		}else {
			foundText = "";
			ret = false;
		}
		
		
		return ret;
	}
	public float confidenceHitFuzzy(String text, String search) {
		this.setSearchText(text);
		search = search.replace("_", " ");
		search = search.replace("#", "");

		if(search.contains("*")) {
			search = search.replace("*", "");
			resultFuzzy = FuzzySearch.partialRatio(this.searchText , search);
			
		}else {
			search = search.replace("*", "");
			resultFuzzy = FuzzySearch.ratio(this.searchText , search);
		}

		resultFuzzy = resultFuzzy/100f;

		
		return resultFuzzy;
	}

	public boolean hasHitFuzzy(String text, String search) {
		boolean ret=false;

		confidence = confidenceHitFuzzy(text , search);
		
		if (confidence >= treshold ) {
			ret = true;
			foundText = search;
			
		}else {
			foundText = "";
			ret = false;
		}
		
		
		return ret;
	}
	public float confidenceSingleWord(String text, String search) {
		
		boolean ret=false;
		this.setSearchText(text);
		Collection<String> col= new ArrayList<String>();
		ExtractedResult res;
		
		search = search.replace("_", " ");
		search = search.replace("#", "");
		col.add(search);
			search = search.replace("*", "");
			res = FuzzySearch.extractOne(this.searchText , col);

		confidence = res.getScore()/100f;
		
		
		return confidence;

	}	
	public boolean hasSingleWord(String text, String search) {
		boolean ret=false;
		
			confidence = confidenceSingleWord(text , search);

		
		if (confidence >= treshold ) {
			ret = true;
			foundText = search;
			
		}else {
			foundText = "";
			ret = false;
		}
		
		
		return ret;

	}
	
	
	
	
	public float getTreshold() {
		return treshold;
	}

	public void setTreshold(float treshold) {
		this.treshold = treshold;
	}
	public StringMetric EngineMetric() {
		return metric;
	}

	public FuzzySearch EngineFuzzy() {
		return fuzzy;
	}

	public float Confidence() {
		return confidence;
	}

	public void setSearchText(String searchText) {

		this.searchText = searchText;
	}
	public void setSearchTextNoNum(String searchText) {
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
		this.searchText = searchText;
	}
	public String FoundText() {
		return this.foundText;
	}



}
