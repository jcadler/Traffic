package edu.brown.cs32.mlazos.server;

import javax.xml.parsers.SAXParser;
import org.xml.sax.helpers.DefaultHandler;
import java.io.InputStream;

/**
 * Thread which parses the incoming xml from the client/server
 * @author john
 */
public class XMLParseThread extends Thread
{
    private SAXParser p;
    private DefaultHandler handle;//the handler for the parser
    private InputStream stream;
    private boolean running; //indicates whether the current thread is running
    private String handlerWaiter; //the object on which the handler waits
    private final String wait="wait";
    
    public XMLParseThread(SAXParser parse, DefaultHandler h, InputStream s, String waiter)
    {
        super("XMLParseTread");
        p = parse;
        handle = h;
        stream = s;
        handlerWaiter=waiter;
    }
    
    @Override
    public void run()
    {
        running =true;
        try
        {
            p.parse(stream,handle);//start the parser
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        notRunning();//indicate that the parser is no longer running
        synchronized(handlerWaiter)
        {
            handlerWaiter.notifyAll();
        }
        return;
    }
    
    /**
     * indicates to everyone that the thread is no longer running
     */
    private void notRunning()
    {
        synchronized(wait)
        {
            running=false;
            synchronized(handlerWaiter)
            {
                handlerWaiter.notifyAll();
            }
        }
    }
    
    /**
     * indicates whether the thread is currently running
     * @return 
     */
    public boolean running()
    {
        synchronized(wait)
        {
            return running;
        }
    }
}
