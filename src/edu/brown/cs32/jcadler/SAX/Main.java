/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.brown.cs32.jcadler.SAX;

import edu.brown.cs32.jcadler.retrieval.Retriever;
import edu.brown.cs32.jcadler.retrieval.FileRetriever;

/**
 *
 * @author john
 */
public class Main 
{
    
    public static void main(String[] args)
    {
        if(args.length!=6)
            System.out.println("Can only be run with 6 arguments!");
        else
        {
            Retriever r = new FileRetriever(args[2],args[0],args[1]);
        }
    }
}
