package edu.brown.cs32.jcadler.nodeWay;

import java.util.List;
import java.util.Collections;
import edu.brown.cs32.jcadler.retrieval.Retriever;
import java.io.IOException;
import edu.brown.cs32.mlazos.kdtree.Ndimensional;

/**
 * an intersection of streets, used by Root class (Dijkstra's algorithm)
 * @author john
 */
public class Node implements Ndimensional
{
    private String id;
    double lat;
    double lng;
    private List<String> wIDs;
    private Retriever r;
    
    public Node(String i,List<String> ws, double lt, double lg)
    {
        id=i;
        wIDs=ws;
        r=null;
        lat=lt;
        lng=lg;
    }
    
    /**
     * lazily creates a new node
     * @param i
     * @param ret 
     */
    public Node(String i,Retriever ret)
    {
        id=i;
        wIDs=null;
        lat=0;
        lng=0;
        r=ret;
    }
    
    public String getID()
    {
        return id;
    }
    
    public List<String> getWayIDs() throws IOException
    {
        init();
        return Collections.unmodifiableList(wIDs);
    }
    
    public double getLatitude() throws IOException
    {
        init();
        return lat;
    }
    
    public double getLongitude() throws IOException
    {
        init();
        return lng;
    }
    
    /**
     * initializes the node, if it has been instantiated lazily
     * @throws IOException 
     */
    private void init() throws IOException
    {
        if(wIDs==null)
        {
            Node n = r.getNode(id,false);
            wIDs=n.getWayIDs();
            lat = n.getLatitude();
            lng = n.getLongitude();
        }
    }
    
    public Double getDimension(int dim)
    {
        try
        {
            int n = dim%2;
            if(n==0)
                return getLongitude();
            if(n==1)
                return getLatitude();
            else
                throw new Error("THIS IS MATHEMATICALLY IMPOSSIBLE!");
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    public int getNumDimensions()
    {
        return 2;
    }
    
    public String getXMLString()
    {
        String ids = "";
        for(String s : wIDs)
            ids+=s+",";
        return "<node id=\""+id+"\" lat=\""+lat+"\" long=\""+lng+"\" wIDs=\""+ids+"\" />";
    }
}
