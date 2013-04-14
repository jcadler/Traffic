/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.brown.cs32.jcadler.SAX;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.PrintWriter;
import java.io.IOException;

/**
 *
 * @author john
 */
public class Main 
{
    
    public static void main(String[] args)
    {
        try
        {
            ServerSocket s = new ServerSocket(9850);
            Socket sock = s.accept();
            SAXParserFactory f = SAXParserFactory.newInstance();
            SAXParser p = f.newSAXParser();
            p.parse(sock.getInputStream(), new trafficHandler(sock));
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
    
    private static class trafficHandler extends DefaultHandler
    {
        private String request;
        private PrintWriter out;
        
        public trafficHandler(Socket s) throws IOException
        {
            request=null;
            out = new PrintWriter(s.getOutputStream());
        }
                
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
        {
            System.out.println(qName);
            out.println("name: "+qName);
            for(int i=0;i<attributes.getLength();i++)
                out.println("Attribute: "+attributes.getValue(i));
            out.flush();
            /*if(qName.equals("request"))
                request = attributes.getValue("type");
            else if(request!=null)
            {
                switch(request)
                {
                    case "dijkstra":
                        dijkstraHandle(qName,attributes);
                        break;
                    case "getNames":
                        nameHandle();
                        break;
                    case "getWaysInRange":
                        getWaysHandle(qName,attributes);
                        break;
                    case "getIntersections":
                        intersectionHandle(qName,attributes);
                        break;
                }
            }*/
        }
        
        private void dijkstraHandle(String name, Attributes attributes)
        {
            if(name.equals("Way"))
                System.out.println("A way at position lat: "+attributes.getValue("Lat")+
                                   " and y: "+attributes.getValue("Long"));
        }
        
        private void nameHandle()
        {
        }
        
        private void getWaysHandle(String name, Attributes attributes)
        {
        }
        
        private void intersectionHandle(String name, Attributes attributes)
        {
        }
    }
}
