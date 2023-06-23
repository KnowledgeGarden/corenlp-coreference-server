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
{
	"corefs": [{
		"chainId": "CHAIN8",
		"cargo": [{
			"chain": "\"Saccharomyces cerevisiae var. boulardii\"",
			"sentence": "1"
		}, {
			"chain": "\"S. cerevisiae var. boulardii\"",
			"sentence": "2"
		}, {
			"chain": "\"its\"",
			"sentence": "2"
		}]
	}, {
		"chainId": "CHAIN11",
		"cargo": [{
			"chain": "\"a eukaryotic organism that has been used in scientific investigations since the time of its discovery [ 2 ]\"",
			"sentence": "2"
		}, {
			"chain": "\"This model organism\"",
			"sentence": "3"
		}, {
			"chain": "\"its\"",
			"sentence": "3"
		}]
	}],
	"mentions": [
		["Saccharomyces cerevisiae var. boulardii", "the most significant probiotic yeast species"],
		["2", "S. cerevisiae var. boulardii", "a eukaryotic organism that has been used in scientific investigations since the time of its discovery [ 2 ]", "scientific investigations", "the time of its discovery", "its discovery", "its"],
		["This model organism", "its alterable and flexible genome", "its"]
	],
	"sentences": ["Saccharomyces cerevisiae var. boulardii is the most significant probiotic yeast species.", "S. cerevisiae var. boulardii is a eukaryotic organism that has been used in scientific investigations since the time of its discovery [2].", "This model organism has unique importance because of its alterable and flexible genome."]
}
 */
