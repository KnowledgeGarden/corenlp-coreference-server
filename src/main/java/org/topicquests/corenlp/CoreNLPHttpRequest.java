package org.topicquests.corenlp;
/**
 * Created by dakshins on 07/04/18.
 * @see https://github.com/soundarmoorthy/cygnet
 */
import com.sun.net.httpserver.HttpExchange;

public class CoreNLPHttpRequest
{
    private final String searchTextHeaderText="searchText=";
    private HttpExchange exchange;
    private String searchString, queryString;
    private boolean valid;
    
    public CoreNLPHttpRequest() {
    }
    
    void setExchange(final HttpExchange exchange)
    {
        queryString =  searchString = "";
        this.exchange = exchange;
        valid = parse();
    }
    
    
    public String searchText()
    {
        return queryString;
    }
    
    public boolean valid()
    {
        return valid && searchHeaderPresent();
    }
    
    private boolean parse()
    {
        try
        {
            queryString = exchange.getRequestURI().getQuery();
            System.out.println("QS "+queryString);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
        return true;
    }
    
    private boolean searchHeaderPresent()
    {
        return queryString.startsWith(searchTextHeaderText);
    }
    
}
