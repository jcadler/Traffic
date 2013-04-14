package edu.brown.cs32.mlazos.server;
import java.io.*;
import java.net.*;
import java.util.List;
import java.util.ArrayList;
import javax.xml.parsers.SAXParser;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import java.util.Arrays;

import edu.brown.cs32.jcadler.nodeWay.Node;
import edu.brown.cs32.jcadler.nodeWay.Way;
import edu.brown.cs32.jcadler.retrieval.Retriever;
import edu.brown.cs32.jcadler.dijkstra.Dijkstra;
import edu.brown.cs32.jcadler.search.NodeWay;

/**
 * Encapsulate IO for the given client {@link Socket}, with a group of
 * other clients in the given {@link ClientPool}.
 */
public class ClientHandler extends Thread 
{
	private ClientPool _pool;
	private Boolean _running;
	private Socket _client;
	private BufferedReader _input;
	private PrintStream _output;
        private String request;
	private Double minLat;
	private Double minLong;
	private Double maxLat;
	private Double maxLong;
        private Node start;
        private Node end;
	private String street1;
	private String street2;
        private Retriever r;
        private Dijkstra d;
        private SAXParser p;
        private final String waiter = "wait";
	
	/**
	 * Constructs a {@link ClientHandler} on the given client with the given pool.
	 * 
	 * @param - the group of clients
	 * @param client the client to handle
	 * @throws IOException if the client socket is invalid
	 * @throws IllegalArgumentException if pool or client is null
	 */
	public ClientHandler(ClientPool pool, Socket client, Retriever ret,SAXParser parse) throws IOException 
	{
            super("ClientHandler");
		if (pool == null || client == null) 
		{
			throw new IllegalArgumentException("Cannot accept null arguments.");
		}
		
		_pool = pool;
		_client = client;
                _output = new PrintStream(_client.getOutputStream(),false,"UTF-8");
                r = ret;
                d = new Dijkstra(r);
                request = null;
                p = parse;
	}
	
	public void run() 
	{
		System.out.println("running");
		_running = true;
		_pool.add(this);
		
		try
		{
                    XMLParseThread parser = new XMLParseThread(p,new ServerXMLHandler(),_client.getInputStream(),waiter);
                    parser.start();
                    System.out.println("started parsing");
                    synchronized(waiter)
                    {
                        waiter.wait();
                    }
                    System.out.println("parsing");
                    while(_running && parser.running()) //handle request, send response; if a request is bad, kill and return.
                    {
                        if(request!=null)
                        {
                            switch(request)
                            {
                                case "getWaysInRange":
                                    getWays();
                                    break;
                                case "dijkstra":
                                    findShortestPath();
                                    break;
                                case "getNames":
                                    getNames();
                                    break;
                                case "getIntersection":
                                    getIntersection();
                                    break;
                            }
                        }
                        request=null;
                        synchronized(waiter)
                        {
                            waiter.wait();
                        }
                    }
                }
		catch(InterruptedException e)
		{
                    System.out.print("problem in the client handler");
			System.out.println(e.getMessage());
		}
                catch(IllegalArgumentException e)
                {
                    _output.println("<respone>\nmalformed request args, please try again\n</response>");
                    _output.flush();
                }
                catch(Exception e)
                {
                    System.out.print("problem in the client handler ");
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
                System.out.println("leaving");
	}

	//The following methods generate and print response data
	
        private void getWays() throws IllegalArgumentException
        {
            System.out.println("getting ways");
            System.out.println("minLat: "+minLat);
            System.out.println("minLong: "+minLong);
            System.out.println("maxLat: "+maxLat);
            System.out.println("maxLong: "+maxLong);
            String sendBack="<response>\n";
            try
            {
                List<Way> ways = r.getWaysInRange(minLong, maxLong, minLat, maxLat, false);
                List<Node> nodes = new ArrayList<>();
                for(Way w : ways)
                {
                    nodes.add(w.getStart());
                    nodes.add(w.getEnd());
                }
                for(Node n : nodes)
                    sendBack+=nodeToXMLString(n)+"\n";
                for(Way w : ways)
                    sendBack+=wayToXMLString(w)+"\n";
            }
            catch(IOException e)
            {
                System.out.print("problem in getWays");
                System.out.println(e.getMessage());
            }
            sendBack+="</response>";
            _output.println(sendBack);
            _output.flush();
            minLat=null;
            minLong=null;
            maxLat=null;
            maxLong=null;
        }
        
	private void findShortestPath() throws IllegalArgumentException
	{
            if(start==null || end==null)
                throw new IllegalArgumentException("need both end and start in dijkstra");
            List<NodeWay> result;
            try
            {
                result = d.getMinDistance(start,end);
            }
            catch(IOException e)
            {
                result = null;
            }
            String sendBack="<response>\n";
            if(result==null)
                sendBack+="<noPath/>\n";
            else
            {
                List<Way> ways = new ArrayList<>();
                List<Node> nodes = new ArrayList<>();
                for(NodeWay nw : result)
                {
                    if(nw.getWay()==null)
                        continue;
                    ways.add(nw.getWay());
                    nodes.add(nw.getWay().getStart());
                    nodes.add(nw.getWay().getEnd());
                }
                for(Node n : nodes)
                    sendBack+=nodeToXMLString(n)+"\n";
                for(Way w : ways)
                    sendBack+=wayToXMLString(w)+"\n";
            }
            sendBack+="</response>";
            _output.println(sendBack);
            _output.flush();
            start = null;
            end = null;
	}
	
	private void getNames()
	{
            System.out.println("getting names");
            String sendBack = "<response>\n";
            List<String> names = r.getNames();
            for(String s : names)
                sendBack+=s+"\n";
            sendBack+="</response>";
            _output.println(sendBack);
            _output.flush();
	}
	
	private void getIntersection()
	{
            String sendBack = "<response>\n";
            try
            {
                List<Node> ret = r.getIntersection(street1, street2);
                for(Node n : ret)
                    sendBack+=nodeToXMLString(n)+"\n";
            }
            catch(IOException e)
            {
                System.out.print("problem in getIntersection");
                System.out.println(e.getMessage());
            }
            sendBack+="</response>\n";
            _output.println(sendBack);
            _output.flush();
            street1=null;
            street2=null;
	}
        
        private String wayToXMLString(Way w)
        {
            return "<way id=\""+w.getID()+"\" name=\""+w.getName()+
                    "\" startID=\""+w.getStart().getID()+
                    "\" endID=\""+w.getEnd().getID()+"\"/>";
        }
        
        private String nodeToXMLString(Node n)
        {
            try
            {
                String wIDs="";
                for(String s : n.getWayIDs())
                    wIDs+=s+",";
                wIDs=wIDs.substring(0,wIDs.length()-1);
                return "<node id=\""+n.getID()+"\" lat=\""+n.getLatitude()+
                       "\" long=\""+n.getLongitude()+"\" wIDs=\""+wIDs+"\"/>";
            }
            catch(IOException e)
            {
                return null;
            }
        }

	/**
	 * Close this socket and its related streams.
	 * 
	 * @throws IOException Passed up from socket
	 */
	public void kill() throws IOException 
	{
		_running = false;
		_pool.remove(this);
		_input.close();
		_output.close();
		_client.close();
	}
        
        private class ServerXMLHandler extends DefaultHandler
        {
            private final String waitOnMe = "waiting";
            
            @Override
            public void startElement(String uri, String localName, 
                                     String name, Attributes a) throws IllegalArgumentException
            {
                System.out.println("name: "+name);
                if(request==null && name.equals("request"))
                    request = a.getValue("type");
                if(request!=null)
                {
                    switch(request)
                    {
                        case "getWaysInRange":
                            getWaysHandle(name,a);
                            break;
                        case "dijkstra":
                            dijkstraHandle(name,a);
                            break;
                        case "getNames":
                            break;
                        case "getIntersection":
                            intersectionHandle(name,a);
                            break;
                    }
                }
            }
            
            @Override
            public void endElement(String uri, String localName, String name)
            {
                System.out.println("end of request");
                if(name.equals("request"))
                {
                    synchronized(waiter)
                    {
                        waiter.notifyAll();
                    }
                }
            }
            
            private void getWaysHandle(String name, Attributes a) throws IllegalArgumentException
            {
                try
                {
                    minLat = Double.parseDouble(a.getValue("minLat"));
                    maxLat = Double.parseDouble(a.getValue("maxLat"));
                    minLong = Double.parseDouble(a.getValue("minLong"));
                    maxLong = Double.parseDouble(a.getValue("maxLong"));
                }
                catch(Exception e)
                {
                    System.out.print("problem in waysHandle");
                    System.out.println(e.getMessage());
                    throw new IllegalArgumentException("wrong arguments in getWays");
                }
            }
            
            private void dijkstraHandle(String name, Attributes a) throws IllegalArgumentException
            {
                if(name.equals("node"))
                {
                    if(a.getValue("type").equals("start"))
                        start = nodeFromAttributes(a);
                    else if(a.getValue("type").equals("end"))
                        end = nodeFromAttributes(a);
                    else
                        throw new IllegalArgumentException("no type for node in dijkstra");
                }
                else
                    throw new IllegalArgumentException("no node in dijkstra");
            }
            
            private Node nodeFromAttributes(Attributes a) throws IllegalArgumentException
            {
                String id = a.getValue("id");
                if(id==null)
                    throw new IllegalArgumentException("no id");
                double lat;
                double lng;
                try
                {
                    lat = Double.parseDouble(a.getValue("lat"));
                    lng = Double.parseDouble(a.getValue("long"));
                }
                catch(Exception e)
                {
                    throw new IllegalArgumentException("missing lat or long in dijkstra");
                }
                List<String> wIDs = Arrays.asList(a.getValue("wIDs").split(","));
                return new Node(id,wIDs,lat,lng);
            }
            
            private void intersectionHandle(String name, Attributes a) throws IllegalArgumentException
            {
                try
                {
                    if(street1!=null || street2!=null)
                        waitOnMe.wait();
                }
                catch(Exception e)
                {
                    System.out.print("problem in intersectionHandle");
                    System.out.println(e.getMessage());
                }
                if(name.equals("request"))
                {
                    street1 = a.getValue("street1");
                    if(street1==null)
                        throw new IllegalArgumentException("no street1 in intersection");
                    street2 = a.getValue("street2");
                    if(street2==null)
                        throw new IllegalArgumentException("no street2 in intersection");
                }
            }
        }
}

