package edu.brown.cs32.jcadler.SAX;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;

/**
 *
 * @author john
 */
public class trafficHandler extends DefaultHandler
{
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
    {
        System.out.println("URI: "+uri);
        System.out.println("local name: "+localName);
        System.out.println("qName: "+qName);
        System.out.println("Attribute hello "+attributes.getValue("hello"));
    }
}
