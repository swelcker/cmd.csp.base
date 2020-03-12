package cmd.csp.processors;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.*;


import com.optimaize.langdetect.ngram.NgramFilter;
import com.optimaize.langdetect.ngram.StandardNgramFilter;

import cmd.csp.base.BaseProcessor;
import cmd.csp.interfaces.IProcessor;
import cmd.csp.platform.CSPTokenizer;
import cmd.csp.utils.Normalizer;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import smile.nlp.keyword.CooccurrenceKeywords;
import smile.nlp.tokenizer.BreakIteratorSentenceSplitter;
import smile.nlp.tokenizer.SimpleSentenceSplitter;
import smile.nlp.tokenizer.SimpleTokenizer;

public class ProcessorTokenizer extends BaseProcessor implements IProcessor {

	private Boolean isInit=false;	
	private Boolean isNgram=false;	
	private Boolean isTokens=true;	
	private Boolean isSentence=false;	
	private Boolean isNormalize=true;	
	private Integer intGramLength=3;	
	private String strLocale="";	
	private Locale locale = null;
    private NgramFilter filter=null;
    private Character setTextPadding = ' ';

    private BreakIteratorSentenceSplitter biSentenceJava;
    private SimpleSentenceSplitter ssSplitter;
    private SimpleTokenizer sT = new SimpleTokenizer(false);
    private Normalizer normalizer = new Normalizer();
    private CSPTokenizer tokenizer = null;
	private JsonObject jconM=null ;

	private SnowballStemmer stemmer;
	
	private String strConfKey = this.getClass().getSimpleName().toLowerCase().replace("processor", "config");
	
	@Override
	public void PrepProcessing(Handler<AsyncResult<Void>> resultHandler) {
		boolean ret = this.Init();
		super.PrepProcessing(resultHandler);
	}

	@Override
	public void applyConfig(JsonObject config) {
		
		super.applyConfig(config);
		if(config.containsKey(strConfKey)) {
			JsonObject con = config.getJsonObject(strConfKey);
			
			if (con!=null) {


				strLocale=con.getString("locale", "");
				intGramLength=con.getInteger("ngrams.length", 3);
				isNgram=con.getBoolean("ngrams.enable", false);
				isTokens=con.getBoolean("tokens.enable", true);
				isSentence=con.getBoolean("sentences.enable", false);
				isNormalize=con.getBoolean("normalize.enable", true);
			
				normalizer.onWhitespaces = con.getBoolean("normalize.onWhitespaces", true);
				normalizer.onFormatChars = con.getBoolean("normalize.onFormatChars", true);
				normalizer.onQuotesDouble = con.getBoolean("normalize.onQuotesDouble", true);
				normalizer.onQuotesSingle = con.getBoolean("normalize.onQuotesSingle", true);
				normalizer.onDash = con.getBoolean("normalize.onDash", true);
				normalizer.onTrim = con.getBoolean("normalize.onTrim", true);
				normalizer.onUnicode = con.getBoolean("normalize.onUnicode", true);
				normalizer.onUpper = con.getBoolean("normalize.onUpper", false);
				normalizer.onLower = con.getBoolean("normalize.onLower", false);
				normalizer.onCRLF = con.getBoolean("normalize.onCRLF", false);
				normalizer.onDeleteNum = con.getBoolean("normalize.onDeleteNum", false);
				normalizer.onNormalizeNum = con.getBoolean("normalize.onNormalizeNum", false);
				normalizer.onSpecialChar = con.getBoolean("normalize.onSpecialChar", false);

			}
			if(strLocale.length()>0) {
				locale = Locale.forLanguageTag(strLocale);
			}
			if(locale==null) {
				locale = Locale.getDefault();
			}			
			switch(locale.getISO3Language().toLowerCase()){
				case "ara":stemmer=new ArabicStemmer();break;
				case "dan":stemmer=new DanishStemmer();break;
				case "nld":stemmer=new DutchStemmer();break;
				case "eng":stemmer=new EnglishStemmer();break;
				case "fin":stemmer=new FinnishStemmer();break;
				case "fra":stemmer=new FrenchStemmer();break;
				case "deu":stemmer=new GermanStemmer();break;
				case "hun":stemmer=new HungarianStemmer();break;
				case "ind":stemmer=new IndonesianStemmer();break;
				case "gle":stemmer=new IrishStemmer();break;
				case "ita":stemmer=new ItalianStemmer();break;
				case "nep":stemmer=new NepaliStemmer();break;
				case "nor":stemmer=new NorwegianStemmer();break;
				case "por":stemmer=new PortugueseStemmer();break;
				case "ron":stemmer=new RomanianStemmer();break;
				case "spa":stemmer=new SpanishStemmer();break;
				case "rus":stemmer=new RussianStemmer();break;
				case "swe":stemmer=new SwedishStemmer();break;
				case "tam":stemmer=new TamilStemmer();break;
				case "tur":stemmer=new TurkishStemmer();break;
				default:stemmer=new NaiveStemmer();break;
			}
		}


	}

	@Override
	public void setInput(JsonObject content) {
		super.setInput(content);
	}
	
	@Override
	public void Process(Handler<AsyncResult<JsonObject>> resultHandler) {
		//jconM = new JsonObject();
		
		boolean ret = this.Init();
		JsonObject Result=null;
		String Input="";

		if(locale==null) {
			locale = Locale.getDefault();
		}			
		
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
		filter=StandardNgramFilter.getInstance();
		setTextPadding = ' ';

			isInit=true;

			LOGGER.debug("CSP Application: " + this.getClass().getName() +":Loaded and initialized ");

		return isInitialized();
	}
	
	public JsonObject getResult(String strContent) {
		if(!isInitialized()) return null;
		if(strContent==null) return null;
		if(strContent.isEmpty()) return null;
		LOGGER.debug(this.getClass().getName()+":getResult:ReadyToDetect: " + strContent);
		tokenizer = null;
		tokenizer = new CSPTokenizer();
		if(strContent!=null) {
			tokenizer.Original=strContent;
			tokenizer.Normalized = normalizer.Process(strContent);
			tokenizer.Grams = CooccurrenceKeywords.of(strContent).toString();
			if(isNormalize) strContent = normalizer.Process(strContent);
			if(isSentence) {
				getSentences(strContent);
			}
			if(isTokens) {
				getTokens(strContent);
			}
			tokenizer.UpdateLength();

		}
		return tokenizer.getAsJsonObject();
	}
	
	public Boolean isInitialized() {
		return isInit;
	}

	public void getSentences(String strInput) {
		
		if(isSentence) {
			if(strInput.length()>0) {
				
				biSentenceJava	= new BreakIteratorSentenceSplitter(Locale.GERMAN);	
				String[] iter = biSentenceJava.split(strInput);	
				
		        Integer ic=0;
		        Integer senLength=0;
		        Integer allLength=0;
		        Integer s = 0;
		        
		        String sen="";
	    		allLength= strInput.length();
		        for (int i = 0; i < iter.length; i++) {
		        	ic++;
		        	sen = iter[i];
		        	if(sen.length()>0) {
			    		sen=sen.trim();
			    		senLength= sen.length();
			    		tokenizer.sentences.CreateNew();
			    		tokenizer.sentences.current.Value = sen;
			    		tokenizer.sentences.current.Length = senLength;
			    		tokenizer.sentences.current.Start = s;
			    		tokenizer.sentences.current.End = s+senLength;
			    		tokenizer.sentences.current.Normalized = normalizer.Process(sen);
			    		tokenizer.sentences.current.Keywords=CooccurrenceKeywords.of(sen).toString();
			    		s = s+senLength+1;
			    		if(senLength>0 && allLength>0) {
			    			tokenizer.sentences.current.Partition = (100./(allLength/senLength));
			    		}else {
			    			tokenizer.sentences.current.Partition = 0.;
			    		}
			    		if(isNgram) {
			    			tokenizer.sentences.current.NGrams= ExtractGramsAsJson(sen).encodePrettily();
			    		}
			    		if(sen.length()>1) {
							String[] tk = sT.split(sen);	
					        for (int ti = 0; ti < tk.length; ti++) {
								String tkn=tk[ti];
								int tknLength=tkn.length();
								if(tknLength>0) {
						    		stemmer.setCurrent(tkn);
						    		stemmer.stem();
						    		tokenizer.sentences.current.Stemmed = tokenizer.sentences.current.Stemmed + stemmer.getCurrent()+" ";
						    	}
							}
					        tokenizer.sentences.current.Stemmed = tokenizer.sentences.current.Stemmed.trim();
					        tokenizer.Stemmed = tokenizer.Stemmed + tokenizer.sentences.current.Stemmed ;
			    		}//tokens
			    		tokenizer.sentences.SaveCurrent();
		        	} // sen length
		        }// for
			}// input length
		}// is sentence
	}
	public void getTokens(String strInput) {
		
		if(isSentence) {
			if(strInput.length()>0) {
				
				biSentenceJava	= new BreakIteratorSentenceSplitter(Locale.GERMAN);	
				String[] iter = biSentenceJava.split(strInput);	
				
		        int tknLength=0;
		        int senLength=0;
		        int s = 0;

		        int ic=0;
		        int ictk=0;
		        int overalltokens=0;
		        String sen="";
		        String tkn="";
		        JsonObject countSen=null;
		        JsonObject countOverall=this.ExtractCountedTokens(strInput);
				tokenizer.BagOfWords = countOverall.encodePrettily();

		        for (int i = 0; i < iter.length; i++) {
		        	ic++;
		        	sen = iter[i];
		        	if(sen.length()>0) {
			    		sen=sen.trim();
			    		senLength= sen.length();
			    		countSen = this.ExtractCountedTokens(sen);
			    		
			    		if(sen.length()>1) {
							String[] tk = sT.split(sen);	
							ictk=0;
					        int st = 0;
					        for (int ti = 0; ti < tk.length; ti++) {
								ictk++;
								overalltokens++;
								tkn=tk[ti];
								tknLength=tkn.length();
								if(tknLength>0) {
						        	tokenizer.tokens.CreateNew();
						        	tokenizer.tokens.current.SentenceID = i;

						    		tokenizer.tokens.current.Value = tkn;
						    		stemmer.setCurrent(tkn);
						    		stemmer.stem();
						    		tokenizer.tokens.current.Stemmed = stemmer.getCurrent();
						    		tokenizer.tokens.current.Length = tknLength;
						    		tokenizer.tokens.current.Start = s;
						    		tokenizer.tokens.current.End = s+tknLength;
						    		tokenizer.tokens.current.StartInSentence = st;
						    		tokenizer.tokens.current.EndInSentence = st+tknLength;
						    		tokenizer.tokens.current.Normalized = normalizer.Process(tkn);
						    		tokenizer.tokens.current.Partition = (100./(senLength/tknLength));
						    		s = s+(tknLength+1);
						    		st = st+(tknLength+1);
							        
							        
						    		if(isNgram) {
						    			tokenizer.tokens.current.NGrams= ExtractGramsAsJson(tkn).encodePrettily();
						    		}
						    		
						    		//add token
						    		if(countSen.containsKey(tkn)){
						    			tokenizer.tokens.current.FrequencySentence=countSen.getInteger(tkn);
						    		}
						    		if(countOverall.containsKey(tkn)){
						    			tokenizer.tokens.current.FrequencyOverall=countOverall.getInteger(tkn);
						    		}
						    		tokenizer.tokens.SaveCurrent();

						    	}

							}		    			
			    		}//tokens			    		
		        	} // sen length
		        }// for
			}// input length
		}// is sentence
	}	


    public Integer getGramLengths() {
        return intGramLength;
    }

    /**
     * Creates the n-grams for a given text in the order they occur.
     *
     * <p>Example: extractSortedGrams("Foo bar", 2) => [Fo,oo,o , b,ba,ar]</p>
     *
     * @param  text
     * @return The grams, empty if the input was empty or if none for that gramLength fits.
     */
    public List<String> ExtractGramsAsList( CharSequence text) {
        text = applyPadding(text);
        int len = text.length();

        //the actual size will be totalNumGrams or less (filter)
        int totalNumGrams = intGramLength;

        if (totalNumGrams <= 0) {
            return Collections.emptyList();
        }
        List<String> grams = new ArrayList<>(totalNumGrams);

            int numGrams = len - (intGramLength -1);
            if (numGrams >= 1) { //yes can be negative
                for (int pos=0; pos<numGrams; pos++) {
                    String gram = text.subSequence(pos, pos + intGramLength).toString();
                    if (filter==null || filter.use(gram)) {
                        grams.add(gram);
                    }
                }
            }

        return grams;
    }
    public JsonObject ExtractGramsAsJsonArray( CharSequence text) {
        text = applyPadding(text);
        int len = text.length();
        JsonArray jng = new JsonArray();
        JsonObject jngM = new JsonObject();
       //the actual size will be totalNumGrams or less (filter)
        int totalNumGrams = intGramLength;

        if (totalNumGrams <= 0) {
            return jngM;
        }
        List<String> grams = new ArrayList<>(totalNumGrams);

            int numGrams = len - (intGramLength -1);
            if (numGrams >= 1) { //yes can be negative
            	
                for (Integer pos=0; pos<numGrams; pos++) {
                    String gram = text.subSequence(pos, pos + intGramLength).toString();
                    if (filter==null || filter.use(gram)) {
                        jng.add(gram);
                    }
                }
                jngM.put("NGRAM:"+intGramLength.toString(), jng);
            }

        return jngM;
    }
     public JsonObject ExtractGramsAsJson( CharSequence text) {
        text = applyPadding(text);
        int len = text.length();
        JsonObject jng = new JsonObject();
        JsonObject jngM = new JsonObject();
        //the actual size will be totalNumGrams or less (filter)
        int totalNumGrams = intGramLength;

        if (totalNumGrams <= 0) {
            return jngM;
        }
        List<String> grams = new ArrayList<>(totalNumGrams);

            int numGrams = len - (intGramLength -1);
            if (numGrams >= 1) { //yes can be negative
            	
                for (Integer pos=0; pos<numGrams; pos++) {
                    String gram = text.subSequence(pos, pos + intGramLength).toString();
                    if (filter==null || filter.use(gram)) {
                        jng.put(pos.toString(), gram);
                    }
                }
                jngM.put("NGRAM:"+intGramLength.toString(), jng);
            }

        return jngM;
    }
    /**
     * @return Key = ngram, value = count
     *         The order is as the n-grams appeared first in the string.
     *
     */
    public JsonObject ExtractCountedTokens( String text) {
        JsonObject grams = new JsonObject();
		String[] tk = sT.split(text);	
        for (int ti = 0; ti < tk.length; ti++) {
            Integer counter = grams.getInteger(tk[ti]);
            if (counter==null) {
                grams.put(tk[ti], 1);
            } else {
                grams.put(tk[ti], counter+1);
            }
		}
        return grams;
    }


    private void _extractCounted(CharSequence text, int gramLength, int len,JsonObject grams) {
        int endPos = len - (gramLength -1);
        for (int pos=0; pos<endPos; pos++) {
            String gram = text.subSequence(pos, pos + gramLength).toString();
            if (filter==null || filter.use(gram)) {
                Integer counter = grams.getInteger(gram);
                if (counter==null) {
                    grams.put(gram, 1);
                } else {
                    grams.put(gram, counter+1);
                }
            }
        }
    }
    public JsonObject ExtractCountedGrams( CharSequence text) {
        text = applyPadding(text);
        int len = text.length();


        JsonObject grams = new JsonObject();

            _extractCounted(text, intGramLength, len, grams);

        return grams;

    }


     private static int guessNumDistinctiveGrams(int textLength, int gramLength) {
        switch (gramLength) {
            case 1:
                return Math.min(80, textLength);
            case 2:
                if (textLength < 40) return textLength;
                if (textLength < 100) return (int)(textLength*0.8);
                if (textLength < 1000) return (int)(textLength * 0.6);
                return (int)(textLength * 0.5);
            case 3:
                if (textLength < 40) return textLength;
                if (textLength < 100) return (int)(textLength*0.9);
                if (textLength < 1000) return (int)(textLength * 0.8);
                return (int)(textLength * 0.6);
            case 4:
            case 5:
            default:
                if (textLength < 100) return textLength;
                if (textLength < 1000) return (int)(textLength * 0.95);
                return (int)(textLength * 0.9);
        }
    }

    private CharSequence applyPadding(CharSequence text) {
        if (setTextPadding==null) return text;
        if (text.length()==0) return text;
        if (text.charAt(0)==setTextPadding && text.charAt(text.length()-1)==setTextPadding) {
            return text;
        }
        StringBuilder sb = new StringBuilder();
        if (text.charAt(0) != setTextPadding) {
            sb.append(setTextPadding);
        }
        sb.append(text);
        if (text.charAt(text.length()-1) != setTextPadding) {
            sb.append(setTextPadding);
        }
        return sb;
    }		
}

