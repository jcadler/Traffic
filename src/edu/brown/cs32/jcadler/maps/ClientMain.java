package edu.brown.cs32.jcadler.maps;

import edu.brown.cs32.jcadler.maps.GUI.GUI;
import edu.brown.cs32.jcadler.retrieval.Retriever;
import edu.brown.cs32.jcadler.retrieval.NetworkRetriever;
import java.io.IOException;

/**
 *
 * @author john
 */
public class ClientMain
{
    public static void main(String[] args)
    {
        if(args.length!=2)
        {
            System.out.println("can only accept 2 arguments");
            return;
        }
        try
        {
            Retriever r = new NetworkRetriever(Integer.parseInt(args[1]),args[0]);
            GUI g = new GUI(r);
            g.runGui();
        }
        catch(IOException e)
        {
            System.out.println("Could not connect to the server");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        catch(NumberFormatException e)
        {
            System.out.println("The second number must be a port number");
            return;
        }
    }
}
