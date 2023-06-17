package org.topicquests.corenlp;
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
    public static final String
		CHAIN_ID_KEY		= "chainId",
		CARGO_KEY			= "cargo",
		CHAIN_KEY			= "chain",
		SENTENCE_ID_KEY		= "sentence";
    
    public CoreNLPWrapper() {
    	Properties props = new Properties();
	    props.setProperty("annotators", "tokenize,pos,lemma,ner,parse,coref, kbp");
	    props.setProperty("coref.algorithm", "neural");
	    pipeline = new StanfordCoreNLP(props, false);
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
			corefs.add(jc);
		}
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
 			ptx = x.indexOf("in sentence");
 			ch = x.substring(0, ptx).trim();
 			if (ch.startsWith("["))
 				ch = ch.substring(1);
 			ptx += "in sentence".length();
 			sent = x.substring(ptx).trim();
 			jo.addProperty(CHAIN_KEY, ch);
 			jo.addProperty(SENTENCE_ID_KEY, sent);
 			cargo.add(jo);
 		}
 		return result;
 	}
    
    
    public CoreNLPHttpResponse response(String paragraph)
    {
    	
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
