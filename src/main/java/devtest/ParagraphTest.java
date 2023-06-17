/**
 * 
 */
package devtest;

import org.topicquests.support.SimpleHttpClient;
import org.topicquests.support.api.IResult;
import java.net.URLEncoder;

/**
 * @author jackpark
 *
 */
public class ParagraphTest {
	private SimpleHttpClient client;
	private final String BASE_URL = "http://localhost:9080/search?searchText=";
	private final String PARA =
		"Saccharomyces cerevisiae var. boulardii is the most significant probiotic yeast species. S. cerevisiae var. boulardii is a eukaryotic organism that has been used in scientific investigations since the time of its discovery [2]. This model organism has unique importance because of its alterable and flexible genome.";
	/**
	 * 
	 */
	public ParagraphTest() {
		try {
			client = new SimpleHttpClient();
			client.setTimeout(10000);
			String queryString = URLEncoder.encode(PARA, "UTF-8");
			IResult r = client.get(BASE_URL, queryString);
			System.out.println("A "+r.getErrorString());
			System.out.println("B\n"+r.getResultObject());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.exit(0);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ParagraphTest();
	}

}
/**
 * {"corefs":[{"chainId":"CHAIN32","cargo":[{"chain":"\"model + organism +\"","sentence":"3"},{"chain":"\"its\"","sentence":"3]"}]}],"mentions":[["searchText = Saccharomyces + cerevisiae + var. + boulardii +","var. + boulardii +","+","the + most + significant + probiotic + yeast + species","most + significant + probiotic","+","significant + probiotic","probiotic","yeast + species"],["+ S. + cerevisiae + var. + boulardii +","var. + boulardii +","boulardii +","+ a + eukaryotic + organism + that + has + been","eukaryotic","organism + that + has + been","+","+","used + in + scientific + investigations + since + the + time + of + its + discovery + [ 2 ] .","in","scientific","investigations + since","+","the + time + of","its","discovery + [ 2 ] .","[ 2 ]"],["This + model + organism +","model + organism +","+ unique + importance +","+ importance +","+","of + its + alterable + and + flexible + genome","its","alterable","+ flexible + genome"]]}
 */
