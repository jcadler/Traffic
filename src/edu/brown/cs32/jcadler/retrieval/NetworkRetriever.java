package edu.brown.cs32.jcadler.retrieval;

import edu.brown.cs32.jcadler.nodeWay.Node;
import edu.brown.cs32.jcadler.nodeWay.Way;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.net.Socket;
import java.io.PrintStream;
import javax.xml.parsers.SAXParserFactory;
import edu.brown.cs32.mlazos.server.XMLParseThread;
import edu.brown.cs32.jcadler.dijkstra.Dijkstra;
import edu.brown.cs32.jcadler.search.NodeWay;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * retrieves way, node and name info from across a network
 * @author john
 */
public class NetworkRetriever implements Dijkstra, Retriever
{
    private Socket sock;
    private PrintStream out;
    private List<String> names;
    private List<Way> ways;
    private List<Node> intersections;
    private ClientXMLHandler p;
    private boolean done;
    private XMLParseThread thread;
    private final String wait = "waiting";
    
    /**
     * creats a new NetworkRetriever
     * @param port the port through which to connect
     * @param server hostname of the server
     * @throws IOException 
     */
    public NetworkRetriever(int port, String server) throws IOException
    {
        sock = new Socket(server,port);
        out = new PrintStream(sock.getOutputStream(),false,"UTF-8");
        p=new ClientXMLHandler();
        done=false;
        SAXParserFactory f = SAXParserFactory.newInstance();
        out.println("<init>");
        out.flush();
        try
        {
            thread = new XMLParseThread(f.newSAXParser(),p,sock.getInputStream(),wait);
            thread.start();
        }
        catch(Exception e)
        {
            throw new IOException("somethine happened with the XML parser: "+e.getMessage());
        }
    }
    
    /**
     * the client never needs to get a node from the server, and therefore this
     * method is not implemented
     * @param id
     * @param exit
     * @return
     * @throws IOException
     * @throws IllegalArgumentException 
     */
    public Node getNode(String id, Boolean exit) throws IOException,IllegalArgumentException
    {
        return null;
    }
    
    
    /**
     * the client never needs to request a specific node from the server, therefore
     * leaving this method unimplemented
     * @param id
     * @param lazy
     * @param exit
     * @return 
     */
    public Way getWay(String id, boolean lazy, Boolean exit)
    {
        return null;
    }
    
    /**
     * this method is abstracted by getWaysInRange and is therefore left
     * unimplemented
     * @param minLong
     * @param maxLong
     * @param minLat
     * @param maxLat
     * @param exit
     * @return
     * @throws IOException 
     */
    public List<Node> getNodesInRange(double minLong, double maxLong,
                                      double minLat, double maxLat, Boolean exit) throws IOException
    {
        return null;
    }
    
    /**
     * this method is only invoked on the server, and is therefore not implemented
     * @param name
     * @return 
     */
    public List<String> getNamedStreetIDs(String name)
    {
        return null;
    }
    
    /**
     * 
     * @return 
     */
    public synchronized List<String> getNames()
    {
        p.setType("getNames");
        out.println("<request type=\"getNames\"/>");
        out.flush();
        try
        {
            while(!done)
            {
                synchronized(wait)
                {
                    wait.wait();
                }
            }
            return names;
        }
        catch(Exception e)
        {
            return null;
        }
        finally
        {
            done=false;
            names=null;
        }
    }
    
    public synchronized List<Way> getWaysInRange(double minLong, double maxLong,
                                                 double minLat, double maxLat, Boolean exit) throws IOException
    {
        p.setType("getWaysInRange");
        out.printf("<request type=\"getWaysInRange\" minLat=\""+minLat+"\" minLong=\""+minLong+
                   "\" maxLat=\""+maxLat+"\" maxLong=\""+maxLong+"\"/>\n");
        out.flush();
        try
        {
            while(!done)
            {
                synchronized(wait)
                {
                    wait.wait();
                }
            }
            return ways;
        }
        catch(Exception e)
        {
            return null;
        }
        finally
        {
            done=false;
            ways=null;
        }
    }
    
    public List<Node> getIntersection(String street1, String street2)
    {
        out.println("<request type=\"getIntersections\" street1=\""+street1+"\" street2=\""+street2+"\"/>");
        out.flush();
        try
        {
            while(!done)
            {
                synchronized(wait)
                {
                    wait.wait();
                }
            }
            return intersections;
        }
        catch(Exception e)
        {
            done=false;
            return null;
        }
    }
    
    public List<Way> getMinDistance(Node start, Node end) throws IOException
    {
        try
        {
            String send = "<request type=\"dijkstra\">\n";
            send+=start.getXMLString(true)+"\n";
            send+=end.getXMLString(false)+"\n";
            send+="</request>";
            p.setType("dijkstra");
            out.println(send);
            out.flush();
            while(!done)
            {
                synchronized(wait)
                {
                    wait.wait();
                }
            }
        }
        catch(Exception e)
        {
            return null;
        }
        return ways;
    }
    
    /**
     * handler for the SAX parser
     */
    private class ClientXMLHandler extends DefaultHandler
    {
        private String type;
        private Map<String,Node> nodes;
        
        @Override
        public void startElement(String uri, String localName, String name, Attributes a)
        {
            done=false;
            if(name.equals("init"))// the root node should be ignored
                return;
            switch(type) //check the type of request/response and act accordingly
            {
                case "getNames":
                    names(name,a);
                    break;
                case "dijkstra":
                    dijkstra(name,a);
                    break;
                case "getWaysInRange":
                    waysInRange(name,a);
                    break;
                case "getInteresections":
                    intersections(name,a);
                    break;
            }
        }
        
        @Override
        public void endElement(String uri, String localName, String name)
        {
            if(name.equals("response"))
            {
                synchronized(wait)
                {
                    done=true;
                    wait.notifyAll();
                }
            }
        }
        
        public void setType(String t)
        {
            type=t;
        }
        
        private void intersections(String name, Attributes a) throws IllegalArgumentException
        {
            switch(name)
            {
                case "response":
                    intersections=new ArrayList<>();
                    break;
                case "node":
                    Node n = XMLToNode(a);            if(type.equals("getIntersections"))
            {
                
            }
                    intersections.add(n);
                    break;
                default:
                    throw new IllegalArgumentException("malformed intersections response");
            }
        }
        
        private void names(String name, Attributes a) throws IllegalArgumentException
        {
            if(name.equals("response"))
                names = Arrays.asList(a.getValue("names").split(","));
            else
                throw new IllegalArgumentException("There should only be a response element for getNames");
        }
        
        private void waysInRange(String name, Attributes a)
        {
            switch(name)
            {
                case "response":
                    ways = new ArrayList<>();
                    nodes = new HashMap<>();
                    break;
                case "node":
                    Node n = XMLToNode(a);
                    nodes.put(n.getID(),n);
                    break;
                case "way":
                    Way w = XMLToWay(a);
                    ways.add(w);
                    break;
                default:
                    throw new IllegalArgumentException("malformed getWayInRange response");
            }
                        
        }
        
        private void dijkstra(String name, Attributes a) throws IllegalArgumentException
        {
            switch(name)
            {
                case "response":
                    ways = new ArrayList<>();
                    break;
                case "node":
                    Node n = XMLToNode(a);
                    nodes.put(n.getID(),n);
                    break;
                case "way":
                    Way w = XMLToWay(a);
                    ways.add(w);
                    break;
                case "noPath":
                    ways=null;
                    break;
                default:
                    throw new IllegalArgumentException("malformed dijkstra response");
            }
        }
        
        private Node XMLToNode(Attributes a) throws IllegalArgumentException
        {
            String id = a.getValue("id");
            if(id==null)
                throw new IllegalArgumentException("The id you gave was invalid");
            double latitude;
            double longitude;
            try
            {
                latitude = Double.parseDouble(a.getValue("lat"));
                longitude = Double.parseDouble(a.getValue("long"));
            }
            catch(Exception e)
            {
                throw new IllegalArgumentException("The latitude and longitude need to be in the right format");
            }
            if(a.getValue("wIDs")==null)
                throw new IllegalArgumentException("There needs to be a list of wIDs");
            List<String> wIDs = Arrays.asList(a.getValue("wIDs").split(","));
            return new Node(id,wIDs,latitude,longitude);
        }
        
        private Way XMLToWay(Attributes a) throws IllegalArgumentException
        {
            String id = a.getValue("id");
            if(id==null)
                throw new IllegalArgumentException("The id you gave was invalid");
            String name = a.getValue("name");
            if(name==null)
                throw new IllegalArgumentException("You need a valid name for the way");
            if(a.getValue("startID")==null || a.getValue("endID")==null)
                throw new IllegalArgumentException("You need valid start and end nodes");
            Node start = nodes.get(a.getValue("startID"));
            Node end = nodes.get(a.getValue("endID"));
            Double d = Double.parseDouble(a.getValue("traffic"));
            return new Way(id,name,start,end,d);
        }
        
    }
}
