package edu.brown.cs32.jcadler.SAX;

import edu.brown.cs32.jcadler.retrieval.Retriever;
import edu.brown.cs32.jcadler.retrieval.FileRetriever;
import edu.brown.cs32.mlazos.server.Server;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class Main 
{
    
    public static void main(String[] args)
    {
        if(args.length!=6)
            System.out.println("Can only be run with 6 arguments!");
        else
        {
            Retriever r;
            int trafficPort;
            int serverPort;
            try
            {
                Map<String,Double> traffic = new ConcurrentHashMap<>();
                r = new FileRetriever(args[2],args[0],args[1],traffic);
                trafficPort = Integer.parseInt(args[4]);
                serverPort = Integer.parseInt(args[5]);
                Server s = new Server(serverPort,trafficPort,r,traffic);
                s.start();
                s.join();
            }
            catch(Exception e)
            {
                System.out.println(e.getMessage());
                return;
            }
        }
    }
}
