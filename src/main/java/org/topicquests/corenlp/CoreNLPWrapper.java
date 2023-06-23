package org.topicquests.corenlp;
import java.net.URLDecoder;
import java.util.Map;
import java.util.Properties;

import org.topicquests.support.ResultPojo;
import org.topicquests.support.api.IResult;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import edu.stanford.nlp.util.ArrayMap;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.Mention;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.logging.RedwoodConfiguration;

/**
 * Created by dakshins on 07/04/18.
 * @see https://github.com/soundarmoorthy/cygnet
 */
public class CoreNLPWrapper
{
    private StanfordCoreNLP pipeline;
    private StanfordCoreNLP sentpipe;
    public static final String
		CHAIN_ID_KEY		= "chainId",
		CARGO_KEY			= "cargo",
		CHAIN_KEY			= "chain",
		SENTENCE_ID_KEY		= "sentence";
    
    public CoreNLPWrapper() {
    	Properties props = new Properties();
	    props.setProperty("annotators", "tokenize,pos,lemma,ner,parse,coref, kbp");//, tokenize
	    props.setProperty("coref.algorithm", "neural");
	    pipeline = new StanfordCoreNLP(props, false);
	    props = new Properties();
	    // set the list of annotators to run
	    props.setProperty("annotators", "tokenize");
	    // build pipeline
	    sentpipe = new StanfordCoreNLP(props);
   }


    public JsonObject findCoreferences(String paragraph) {
    	System.out.println("Finding: "+paragraph);
		JsonObject result = new JsonObject();
		Annotation document = new Annotation(paragraph);
		pipeline.annotate(document);
		JsonArray corefs = new JsonArray();
		result.add("corefs", corefs);
		// these take the form
		// CHAIN11-["a eukaryotic organism that has been used in scientific investigations since the time of its discovery [ 2 ]" in sentence 2,
		//          "This model organism" in sentence 3]
		JsonObject jo, jc;
		// coreferences
		for (CorefChain cc : document.get(CorefCoreAnnotations.CorefChainAnnotation.class).values()) {
			//jo = new JsonObject();
			jc =parseChain(cc.toString());
			System.out.println("CoReFs");
			corefs.add(jc);
		}
		System.out.println("mentions");
		//mentions
		JsonArray mentions = new JsonArray();
		result.add("mentions", mentions);
		JsonArray ms;
		for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
		      ms = new JsonArray();
		      mentions.add(ms);
		      for (Mention m : sentence.get(CorefCoreAnnotations.CorefMentionsAnnotation.class)) {
		        ms.add(m.toString());
		      }
		}
		// sentences
		CoreDocument doc = new CoreDocument(paragraph);
		sentpipe.annotate(doc);
		System.out.println("Sentences");
		JsonArray sentences = new JsonArray();
		result.add("sentences", sentences);
		for (CoreSentence sent : doc.sentences()) {
			System.out.println("Sent "+sent);

			sentences.add(sent.text());
		}
		System.out.println("Sentences2\n"+sentences);

		return result;
	}
    
 // CHAIN11-["a eukaryotic organism that has been used in scientific investigations since the time of its discovery [ 2 ]" in sentence 2,
 	//          "This model organism" in sentence 3]
 	JsonObject parseChain(String chain) {
 		System.out.println("PARSING "+chain);
 		
 		JsonObject result = new JsonObject();
 		JsonObject jo = new JsonObject();
 		int where = chain.indexOf("-");
 		String chainId = chain.substring(0, where);
 		result.addProperty(CHAIN_ID_KEY, chainId);
 		JsonArray cargo = new JsonArray();
 		result.add(CARGO_KEY, cargo);
 		where = where+1;
 		String array = chain.substring(where);
 		String [] chunks = array.split(",");
 		String x,ch, sent;
 		int len = chunks.length;
 		int ptx;
 		// coreferences
 		for (int i=0;i<len;i++) {
 			jo = new JsonObject();
 			x = chunks[i].trim();
 	 		System.out.println("PARSING-1 "+x);
			ptx = x.indexOf("in sentence");
 			ch = x.substring(0, ptx).trim();
 			if (ch.startsWith("["))
 				ch = ch.substring(1);
 			if (ch.endsWith("]"))
 				ch = ch.substring(0, (ch.length()-1));
 			ch = ch.replace("\\\"", "\"");
 			ptx += "in sentence".length();
 	 		System.out.println("PARSING-2 "+ptx);
			sent = x.substring(ptx).trim();
			if (sent.endsWith("]"))
				sent = sent.substring(0, (sent.length()-1));
 			jo.addProperty(CHAIN_KEY, ch);
 			jo.addProperty(SENTENCE_ID_KEY, sent);
 			cargo.add(jo);
 	 		System.out.println("PARSING-3 "+cargo);
 		}
 		return result;
 	}
    
    
    public CoreNLPHttpResponse response(String text)
    {
    	
    	String paragraph = text.substring("searchText=".length()).trim();
    	System.out.println("X "+paragraph);
    	try {
    		paragraph = URLDecoder.decode(paragraph, "UTF-8");
    	} catch(Exception e) {
    		throw new RuntimeException(e);
    	}

    	System.out.println("Y "+paragraph);

        JsonObject cargo = findCoreferences(paragraph);

        return CoreNLPHttpResponse.NEW
                .withBody(cargo.toString())
                .withHttpCode(200)
                .withHeaders(headers());
    }

    public Map<String, String> headers()
    {
        ArrayMap<String,String> map = new ArrayMap<String, String>();
        map.put("ContentType", "application/json");
        return map;
    }
}
