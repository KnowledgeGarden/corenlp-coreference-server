package org.topicquests.corenlp;


import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;

/**
 * Created by dakshins on 06/04/18.
 * @see https://github.com/soundarmoorthy/cygnet
 * Modified by park
 */
public class CorefHttpServer 
{
    private HttpServer server;
    private static final int port = 9080;

    public CorefHttpServer() throws Exception
    {
        server = HttpServer.create(new InetSocketAddress(port), 0);
    }
    
    private void start() {
    	server.start();
    }
    private void initializeRoutine() throws Exception
    {
        server.setExecutor(Executors.newFixedThreadPool(8));
        server.createContext("/search", new SearchHandler());
    }
    
    public static void main (String [] strings) 
    {
        System.out.println("Beging server initialization...");
        System.out.println("Initialization sequence complete. Server up and running");
        System.out.println("Browse to http://localhost:"+port);
        try {
        	CorefHttpServer app = new CorefHttpServer();
        	app.initializeRoutine();
        	app.start();
        } catch (Exception e) {
        	throw new RuntimeException(e);
        }
    }
}