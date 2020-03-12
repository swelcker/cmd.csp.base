package cmd.csp.processors;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.deeplearning4j.bagofwords.vectorizer.BagOfWordsVectorizer;
import org.deeplearning4j.models.embeddings.WeightLookupTable;
import org.deeplearning4j.models.embeddings.inmemory.InMemoryLookupTable;
import org.deeplearning4j.models.embeddings.learning.impl.elements.SkipGram;
import org.deeplearning4j.models.embeddings.loader.VectorsConfiguration;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.deeplearning4j.models.sequencevectors.SequenceVectors;
import org.deeplearning4j.models.sequencevectors.interfaces.SequenceElementFactory;
import org.deeplearning4j.models.sequencevectors.iterators.AbstractSequenceIterator;
import org.deeplearning4j.models.sequencevectors.serialization.VocabWordFactory;
import org.deeplearning4j.models.sequencevectors.transformers.impl.SentenceTransformer;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.models.word2vec.wordstore.VocabConstructor;
import org.deeplearning4j.models.word2vec.wordstore.inmemory.AbstractCache;
import org.deeplearning4j.text.documentiterator.LabelsSource;
import org.deeplearning4j.text.inputsanitation.InputHomogenization;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.util.SerializationUtils;

import cmd.csp.base.BaseProcessor;
import cmd.csp.interfaces.IProcessor;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

public class ProcessorVectorizer extends BaseProcessor implements IProcessor {

	private Boolean isInit=false;	
	private Boolean isBagOfWords=true;	
	private Boolean isTokens=true;	
	private Boolean isParagraph=true;	
	private Boolean isSequence=true;	
	private Boolean isHarmonize=true;	
 
    private int intMinWordFrequency=3;
    private int intLayerSize=100;
    private int intSeed=42;
    private int intWindowSize=5;
    private boolean blnParallelTokenization=false;
    private int intBatchSize=100;
    private int intEpochs=1;
    private boolean blnAdaGrad=false;
    private boolean blnUseUnknown=false;
    private boolean blnUseHierachicSoftmax=false;
    private boolean blnUsePreciseWeight=false;
    private int intIterations=1;
    private int intVectorLength=150;
	
	private JsonObject jcon=new JsonObject() ;
	private String strConfKey = this.getClass().getSimpleName().toLowerCase().replace("processor", "config");
	
	@Override
	public void PrepProcessing(Handler<AsyncResult<Boolean>> resultHandler) {
		boolean ret = this.Init();
		super.PrepProcessing(resultHandler);
	}

	@Override
	public void applyConfig(JsonObject config) {
		
		super.applyConfig(config);
		if(config.containsKey(strConfKey)) {
			JsonObject con = config.getJsonObject(strConfKey);
			
			if (con!=null) {


				isBagOfWords=con.getBoolean("bagofwords.enable", true);
				isTokens=con.getBoolean("tokens.enable", true);
				isParagraph=con.getBoolean("paragraph.enable", true);
				isSequence=con.getBoolean("sequence.enable", true);
				isHarmonize=con.getBoolean("harmonize.enable", true);
				
			    intMinWordFrequency=con.getInteger("minwordfrequency", 3);
			    intLayerSize=con.getInteger("layersize", 100);
			    intSeed=con.getInteger("seed", 42);
			    intWindowSize=con.getInteger("windowsize", 5);

			    blnParallelTokenization=con.getBoolean("paralleltokenization.enable", false);
			    blnAdaGrad=con.getBoolean("adagrad.enable", false);
			    blnUseUnknown=con.getBoolean("unknown.enable", false);
			    blnUseHierachicSoftmax=con.getBoolean("hierachicsoftmax.enable", false);
			    blnUsePreciseWeight=con.getBoolean("preciseweight.enable", false);
			    intBatchSize=con.getInteger("batchsize", 0);
			    intEpochs=con.getInteger("epochs", 1);
			    intIterations=con.getInteger("iterations", 1);
			    intVectorLength=con.getInteger("vectorlength", 150);

			    
			}
		}

	}

	@Override
	public void setInput(JsonObject content) {
		super.setInput(content);
	}
	
	@Override
	public void Process(Handler<AsyncResult<JsonObject>> resultHandler) {
		jcon = new JsonObject();
		
		boolean ret = this.Init();
		JsonObject Result=null;
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

			isInit=true;

			LOGGER.debug("CSP Application: " + this.getClass().getName() +":Loaded and initialized ");

		return isInitialized();
	}
	
	public JsonObject getResult(String strContent) {
		if(!isInitialized()) return null;
		if(strContent==null) return null;
		if(strContent.isEmpty()) return null;
		jcon.clear();
		LOGGER.debug(this.getClass().getName()+":getResult:ReadyToDetect: " + strContent);
		if(strContent!=null) {

			if(isParagraph) {
				jcon.put("paragraph", getParagraphVectors(strContent));
			}
			if(isTokens) {
				jcon.put("tokens", getTokenVectors(strContent));
			}
			if(isBagOfWords) {
				jcon.put("bagofwords", getBagOfWords(strContent));
			}
			if(isSequence) {
				jcon.put("bagofwords", getSequenceVectors(strContent));
			}
		}
		return jcon;
	}
	
	public Boolean isInitialized() {
		return isInit;
	}
	public String getSequenceVectors(String strInput) {
		String strContent=HarmonizeText(strInput);
		String strVec = "";
		if(strContent.length()>0) {
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
		    InputStream targetStream = new ByteArrayInputStream(strContent.getBytes());
		    SentenceIterator iter = new BasicLineIterator(targetStream);
		    TokenizerFactory t = new DefaultTokenizerFactory();
	        t.setTokenPreProcessor(new CommonPreprocessor());
	        AbstractCache<VocabWord> vocabCache = new AbstractCache.Builder<VocabWord>().build();
	        SequenceElementFactory f1 = new VocabWordFactory();


	        /*
	            Now we need the way to convert lines into Sequences of VocabWords.
	            In this example that's SentenceTransformer
	         */
	        SentenceTransformer transformer = new SentenceTransformer.Builder()
	                .iterator(iter)
	                .tokenizerFactory(t)
	                .build();


	        /*
	            And we pack that transformer into AbstractSequenceIterator
	         */
	        AbstractSequenceIterator<VocabWord> sequenceIterator =
	            new AbstractSequenceIterator.Builder<>(transformer).build();


	        /*
	            Now we should build vocabulary out of sequence iterator.
	            We can skip this phase, and just set AbstractVectors.resetModel(TRUE), and vocabulary will be mastered internally
	        */
	        VocabConstructor<VocabWord> constructor = new VocabConstructor.Builder<VocabWord>()
	                .addSource(sequenceIterator, 5)
	                .setTargetVocabCache(vocabCache)
	                .build();

	        constructor.buildJointVocabulary(false, true);

	        /*
	            Time to build WeightLookupTable instance for our new model
	        */

	        WeightLookupTable<VocabWord> lookupTable = new InMemoryLookupTable.Builder<VocabWord>()
	                .vectorLength(intVectorLength)
	                .useAdaGrad(blnAdaGrad)
	                .cache(vocabCache)
	                .build();

	         /*
	             reset model is viable only if you're setting AbstractVectors.resetModel() to false
	             if set to True - it will be called internally
	        */
	        lookupTable.resetWeights(true);

	        /*
	            Now we can build AbstractVectors model, that suits our needs
	         */
	        SequenceVectors<VocabWord> vectors = new SequenceVectors.Builder<VocabWord>(new VectorsConfiguration())
	                // minimum number of occurencies for each element in training corpus. All elements below this value will be ignored
	                // Please note: this value has effect only if resetModel() set to TRUE, for internal model building. Otherwise it'll be ignored, and actual vocabulary content will be used
	                .minWordFrequency(intMinWordFrequency)

	                // WeightLookupTable
	                .lookupTable(lookupTable)

	                // abstract iterator that covers training corpus
	                .iterate(sequenceIterator)

	                // vocabulary built prior to modelling
	                .vocabCache(vocabCache)

	                // batchSize is the number of sequences being processed by 1 thread at once
	                // this value actually matters if you have iterations > 1
	                .batchSize(intBatchSize)

	                // number of iterations over batch
	                .iterations(intIterations)

	                // number of iterations over whole training corpus
	                .epochs(intEpochs)

	                // if set to true, vocabulary will be built from scratches internally
	                // otherwise externally provided vocab will be used
	                .resetModel(false)


	                /*
	                    These two methods define our training goals. At least one goal should be set to TRUE.
	                 */
	                .trainElementsRepresentation(true)
	                .trainSequencesRepresentation(false)

	                /*
	                    Specifies elements learning algorithms. SkipGram, for example.
	                 */
	                .elementsLearningAlgorithm(new SkipGram<VocabWord>())

	                .build();

	        /*
	            Now, after all options are set, we just call fit()
	         */
	        vectors.fit();	        
	        try {
				WordVectorSerializer.writeSequenceVectors(vectors,  f1, bo);
				strVec = bo.toString(Charset.defaultCharset() );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return strVec;
	}
	public String getBagOfWords(String strInput) {
		String strContent=HarmonizeText(strInput);
		String strVec = "";
		if(strContent.length()>0) {
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
		    InputStream targetStream = new ByteArrayInputStream(strContent.getBytes());
		    SentenceIterator iter = new BasicLineIterator(targetStream);
		    TokenizerFactory t = new DefaultTokenizerFactory();
	        t.setTokenPreProcessor(new CommonPreprocessor());
	        

			BagOfWordsVectorizer vec = new BagOfWordsVectorizer.Builder()
	                .allowParallelTokenization(blnParallelTokenization)
	                .setMinWordFrequency(intMinWordFrequency)
                    .setStopWords(new ArrayList<String>())
                    .setTokenizerFactory(t)
                    .setIterator(iter)
	                .build();

	        vec.fit();
	        
	        SerializationUtils.writeObject(vec, bo);
			strVec = bo.toString(Charset.defaultCharset() );

		}
		return strVec;
	}
	public String getTokenVectors(String strInput) {
		String strContent=HarmonizeText(strInput);
		String strVec = "";
		if(strContent.length()>0) {
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
		    InputStream targetStream = new ByteArrayInputStream(strContent.getBytes());
		    SentenceIterator iter = new BasicLineIterator(targetStream);
		    TokenizerFactory t = new DefaultTokenizerFactory();
	        t.setTokenPreProcessor(new CommonPreprocessor());
	        

			Word2Vec vec = new Word2Vec.Builder()
	                .minWordFrequency(intMinWordFrequency)
	                .layerSize(intLayerSize)
	                .seed(intSeed)
	                .windowSize(intWindowSize)
	                .iterate(iter)
	                .tokenizerFactory(t)
	                .build();
            
	        vec.fit();
	        
	        try {
				WordVectorSerializer.writeWordVectors(vec, bo);
				strVec = bo.toString(Charset.defaultCharset() );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return strVec;
	}
	public String getParagraphVectors(String strInput) {
		String strContent=HarmonizeText(strInput);
		String strVec = "";
		if(strContent.length()>0) {
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
		    InputStream targetStream = new ByteArrayInputStream(strContent.getBytes());
		    SentenceIterator iter = new BasicLineIterator(targetStream);
		    TokenizerFactory t = new DefaultTokenizerFactory();
	        t.setTokenPreProcessor(new CommonPreprocessor());
	        
	        AbstractCache<VocabWord> cache = new AbstractCache<>();
	        LabelsSource source = new LabelsSource("DOC_");

			ParagraphVectors vec = new ParagraphVectors.Builder()
	                .minWordFrequency(intMinWordFrequency)
	                .layerSize(intLayerSize)
	                .seed(intSeed)
	                .windowSize(intWindowSize)
	                .allowParallelTokenization(blnParallelTokenization)
	                .epochs(intEpochs)
	                .useAdaGrad(blnAdaGrad)
	                .useUnknown(blnUseUnknown)
	                .useHierarchicSoftmax(blnUseHierachicSoftmax)
	                .usePreciseWeightInit(blnUsePreciseWeight)
	                .iterations(intIterations)
	                .iterate(iter)
	                .tokenizerFactory(t)
	                .batchSize(intBatchSize)
	                .learningRate(0.025)
	                .labelsSource(source)
	                .trainWordVectors(false)
	                .vocabCache(cache)
	                .sampling(0)
	                .build();

			vec.fit();
	        
	        try {
				WordVectorSerializer.writeParagraphVectors(vec, bo);
				strVec = bo.toString(Charset.defaultCharset() );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return strVec;
	}
	public String HarmonizeText(String strContent) {
		String hS = strContent;
		if(isHarmonize) {
			if(strContent.length()>0) {
				InputHomogenization ih = new InputHomogenization(strContent, true);
				hS = ih.transform();			
			}		
		}
		return hS;

	}
}

