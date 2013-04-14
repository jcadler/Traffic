package edu.brown.cs32.jcadler.retrieval;

import edu.brown.cs32.jcadler.utfFile.UTFNameGather;
import edu.brown.cs32.jcadler.utfFile.UTFPager;
import edu.brown.cs32.jcadler.nodeWay.Node;
import edu.brown.cs32.jcadler.nodeWay.Way;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.math.BigDecimal;

/**
 *
 * @author john
 */
public class FileRetriever implements Retriever
{
    private UTFNameGather index;
    private UTFPager ways;
    private UTFPager nodes;
    /**
     * the order of the way fields
     */
    private Map<String,Integer> wayOrder;
    /**
     * the order of the node fields
     */
    private Map<String,Integer> nodeOrder;
    private Map<String,Way> wayCache;
    private Map<String,Node> nodeCache;
    private Map<Integer,List<String>> nodePageCache;
    private Map<Integer,List<String>> wayPageCache;
    
    public FileRetriever(String iName, String wName, String nName) throws IOException
    {
        index=new UTFNameGather(iName);
        ways=new UTFPager(wName);
        nodes=new UTFPager(nName);
        wayOrder=new HashMap<>();
        nodeOrder=new HashMap<>();
        nodePageCache=new HashMap<>();
        wayPageCache=new HashMap<>();
        wayCache = new HashMap<>();
        nodeCache = new HashMap<>();
        List<String> wOrder = Arrays.asList(ways.getLine(0).split("\t"));
        List<String> nOrder = Arrays.asList(nodes.getLine(0).split("\t"));
        wayOrder.put("id",wOrder.indexOf("id"));
        wayOrder.put("name",wOrder.indexOf("name"));
        wayOrder.put("start",wOrder.indexOf("start"));
        wayOrder.put("end",wOrder.indexOf("end"));
        nodeOrder.put("id",nOrder.indexOf("id"));
        nodeOrder.put("latitude",nOrder.indexOf("latitude"));
        nodeOrder.put("longitude",nOrder.indexOf("longitude"));
        nodeOrder.put("ways",nOrder.indexOf("ways"));
    }
    
    /**
     * gets the node with the specified ID for the thread with the given name
     * @param id
     * @param threadID
     * @return
     * @throws IOException
     * @throws IllegalArgumentException 
     */
    public synchronized Node getNode(String id, Boolean exit) throws IOException,IllegalArgumentException
    {
        if(checkExit(exit))
            return null;
        if(nodeCache.containsKey(id))
            return nodeCache.get(id);
        int blockID = getBlockID(id);
        List<String> ns;
        if(nodePageCache.containsKey(blockID))
        {
            ns=nodePageCache.get(blockID);
        }
        else
        {
            if(checkExit(exit))
                return null;
            ns=nodes.getPage(blockID,exit);
            nodePageCache.put(blockID,ns);
            if(checkExit(exit))
                return null;
        }
        for(String s : ns)
        {
            if(checkExit(exit))
                return null;
            Node n = stringToNode(s);
            nodeCache.put(n.getID(),n);
            if(n.getID().equals(id))
                return n;
        }
        throw new IllegalArgumentException(id+" is a node id which does not exist");
    }
    
    /**
     * gets a Way, instantiating lazily or not as specified for the indicated thread
     * @param id
     * @param lazy
     * @param threadID
     * @return
     * @throws IOException
     * @throws IllegalArgumentException 
     */
    public synchronized Way getWay(String id, boolean lazy, Boolean exit) throws IOException,IllegalArgumentException
    {
        if(checkExit(exit))
            return null;
        if(wayCache.containsKey(id))
            return wayCache.get(id);
        int blockID = getBlockID(id);
        List<String> ws;
        if(wayPageCache.containsKey(blockID))
        {
            ws=wayPageCache.get(blockID);
        }
        else
        {
            if(checkExit(exit))
                return null;
            ws=ways.getPage(blockID,exit);
            wayPageCache.put(blockID,ws);
            if(checkExit(exit))
                return null;
        }
        for(int i=0;i<ws.size();i++)
        {
            if(checkExit(exit))
                return null;
            Way w;
            if(lazy)
                w = stringToWayLazy(ws.get(i));
            else
            {
                w = stringToWay(ws.get(i),exit);
                wayCache.put(w.getID(),w);
            }
            if(w.getID().equals(id))
            {
                wayCache.put(id,w);
                return w;
            }
        }
        throw new IllegalArgumentException(id+" is a way id which does not exist");
    }
    
    /**
     * gets all of the nodes with the range of latitudes and longitudes
     * @param minLong
     * @param maxLong
     * @param minLat
     * @param maxLat
     * @param threadID
     * @return
     * @throws IOException 
     */
    public synchronized List<Node> getNodesInRange(double minLong, double maxLong, 
                                                   double minLat, double maxLat, Boolean exit) throws IOException
    {
        if(checkExit(exit))
            return null;
        BigDecimal mnLt = BigDecimal.valueOf(minLat);
        BigDecimal mxLt = BigDecimal.valueOf(maxLat);
        BigDecimal mnLg = BigDecimal.valueOf(minLong);
        BigDecimal mxLg = BigDecimal.valueOf(maxLong);
        int minLatID = (int)mnLt.multiply(new BigDecimal(100)).doubleValue();
        int maxLatID = (int)mxLt.multiply(new BigDecimal(100)).doubleValue();
        List<String> ns = new ArrayList<>();
        List<Integer> already = new ArrayList<>();
        List<Node> everything = new ArrayList<>();
        List<Node> ret = new ArrayList<>();
        for(int i : nodePageCache.keySet())
        {
            if(checkExit(exit))
                return null;
            if(minLatID<i && maxLatID>i)
                already.add(i);
        }
        for(int i=minLatID;i<=maxLatID;i++)
        {
            if(checkExit(exit))
                return null;
            if(already.contains(i))
                ns.addAll(nodePageCache.get(i));
            else
            {
                List<String> got = nodes.getPage(i,exit);
                ns.addAll(got);
                nodePageCache.put(i,got);
            }
        }
        if(checkExit(exit))
            return null;
        for(int i=0;i<ns.size();i++)
            everything.add(stringToNode(ns.get(i)));
        for(Node n : everything)
        {
            if(checkExit(exit))
                return null;
            BigDecimal lng = BigDecimal.valueOf(n.getLongitude());
            BigDecimal lat = BigDecimal.valueOf(n.getLatitude());
            int scale = getMaxScale(mnLt,mxLt,mnLg,mxLg,lng,lat);
            lng=lng.setScale(scale);
            lat=lat.setScale(scale);
            mnLt=mnLt.setScale(scale);
            mxLt=mxLt.setScale(scale);
            mnLg=mnLg.setScale(scale);
            mxLg=mxLg.setScale(scale);
            if(lng.unscaledValue().intValue()>=mnLg.unscaledValue().intValue() 
                    && lng.unscaledValue().intValue()<=mxLg.unscaledValue().intValue() && 
               lat.unscaledValue().intValue()>=mnLt.unscaledValue().intValue() 
                    && lat.unscaledValue().intValue()<=mxLt.unscaledValue().intValue())
                ret.add(n);
        }
        return ret;
    }
    
    public List<Way> getWaysInRange(double minLat, double maxLat,
                                    double minLong, double maxLong, Boolean exit) throws IOException
    {
        List<String> wIDs = new ArrayList<>();
        List<Way> ret = new ArrayList<>();
        for(Node n : getNodesInRange(minLat,maxLat,minLong,maxLong,exit))
            wIDs.addAll(n.getWayIDs());
        for(String s : wIDs)
            ret.add(getWay(s,false,exit));
        return ret;
    }
    
    private int getMaxScale(BigDecimal... deci)
    {
        Integer ret=null;
        for(BigDecimal d : deci)
        {
            if(ret==null || d.scale()>ret)
                ret=d.scale();
        }
        return ret;
    }
    
    public List<String> getNames()
    {
        return index.getNames();
    }
    
    /**
     * gets the intersections of the given streets
     * @param street1
     * @param street2
     * @return
     * @throws IOException 
     */
    public List<Node> getIntersection(String street1, String street2) throws IOException
    {
        List<String> ns1 = index.getNamePage(street1);
        List<String> ns2 = index.getNamePage(street2);
        List<String> ret = new ArrayList<>();
        List<Node> retNodes = new ArrayList<>();
        ns1 = getNodeIDs(ns1);
        ns2 = getNodeIDs(ns2);
        for(String s : ns1)
        {
            if(ns2.contains(s))
                ret.add(s);
        }
        for(String s : ret)
        {
            retNodes.add(getNode(s,false));
        }
        return retNodes;
    }
    
    private List<String> getNodeIDs(List<String> l)
    {
        List<String> ret=null;
        for(String s : l)
        {
            String[] split = s.split("\t");
            split=split[1].split(",");
            ret=Arrays.asList(split);
        }
        return ret;
    }
    
    private Node stringToNode(String str)
    {
        String[] s = (str+"~").split("\t");
        s[s.length-1]=s[s.length-1].substring(0,s[s.length-1].length()-1);
        if(s[nodeOrder.get("ways")].equals(""))
            return new Node(s[nodeOrder.get("id")],
                            new ArrayList<String>(),
                            Double.parseDouble(s[nodeOrder.get("latitude")]),
                            Double.parseDouble(s[nodeOrder.get("longitude")]));
        else
            return new Node(s[nodeOrder.get("id")],
                            Arrays.asList(s[nodeOrder.get("ways")].split(",")),
                            Double.parseDouble(s[nodeOrder.get("latitude")]),
                            Double.parseDouble(s[nodeOrder.get("longitude")]));
    }
    
    private Way stringToWayLazy(String str)
    {
        String[] s = (str+"~").split("\t");
        s[s.length-1]=s[s.length-1].substring(0,s[s.length-1].length()-1);
        return new Way(s[wayOrder.get("id")],
                       s[wayOrder.get("name")],
                       s[wayOrder.get("start")],
                       s[wayOrder.get("end")],
                       this);
    }
    
    private Way stringToWay(String str, Boolean exit) throws IOException
    {
        String[] s = (str+"~").split("\t");
        s[s.length-1]=s[s.length-1].substring(0,s[s.length-1].length()-1);
        return new Way(s[wayOrder.get("id")],
                       s[wayOrder.get("name")],
                       getNode(s[wayOrder.get("start")],exit),
                       getNode(s[wayOrder.get("end")],exit));
    }
    
    private int getBlockID(String id)
    {
        return Integer.parseInt(id.substring(3,7));
    }
    
    private boolean checkExit(Boolean exit)
    {
        synchronized(exit)
        {
            if(exit)
                return true;
            return false;
        }
    }
}
