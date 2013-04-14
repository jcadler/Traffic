package edu.brown.cs32.mlazos.server;

import javax.xml.parsers.SAXParser;
import org.xml.sax.helpers.DefaultHandler;
import java.io.InputStream;

/**
 *
 * @author john
 */
public class XMLParseThread extends Thread
{
    private SAXParser p;
    private DefaultHandler handle;
    private InputStream stream;
    private boolean running;
    private String handlerWaiter;
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
            p.parse(stream,handle);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        notRunning();
        synchronized(handlerWaiter)
        {
            handlerWaiter.notifyAll();
        }
        return;
    }
    
    private void notRunning()
    {
        synchronized(wait)
        {
            running=false;
        }
    }
    
    public boolean running()
    {
        synchronized(wait)
        {
            return running;
        }
    }
}
