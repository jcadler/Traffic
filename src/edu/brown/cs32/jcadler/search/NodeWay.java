package edu.brown.cs32.jcadler.search;

import edu.brown.cs32.jcadler.nodeWay.Node;
import edu.brown.cs32.jcadler.nodeWay.Way;
import edu.brown.cs32.jcadler.retrieval.Retriever;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents a single node, and the path used to get to it
 * @author john
 */
public class NodeWay implements Comparable<NodeWay>
{
    private List<NodeWay> parents;
    private Way up;
    private Node crrnt;
    private double soFar;
    private Node end;
    
    /**
     * lazily instantiates the NodeWay, leaving the current node lazily instantiated
     * @param w
     * @param id
     * @param p
     * @param ret
     * @param e
     * @throws IOException 
     */
    public NodeWay(Way w, String id, List<NodeWay> p, Retriever ret, Node e) throws IOException
    {
        up=w;
        crrnt=new Node(id,ret);
        parents=p;
        end=e;
        soFar=0;
        if(p!=null)
        {
            for(NodeWay nw : p)
                soFar += nw.getWeight();
        }
    }
    
    /**
     * instantiates the current NodeWay with the specified node and end node
     * @param w
     * @param c
     * @param e
     * @param p
     * @throws IOException 
     */
    public NodeWay(Way w, Node c, Node e, List<NodeWay> p) throws IOException
    {
        up=w;
        crrnt=c;
        end=e;
        parents=p;
        soFar=0;
        if(p!=null)
        {
            for(NodeWay nw : p)
                soFar += nw.getWeight();
        }
    }
    
    public Node getNode()
    {
        return crrnt;
    }
    
    public Way getWay()
    {
        return up;
    }
    
    public double getWeight() throws IOException
    {
        return soFar+distToEnd();
    }
    
    public List<NodeWay> getParents()
    {
        return new ArrayList<>(parents);
    }
    
    public int compareTo(NodeWay nw)
    {
        try
        {
            if(nw.getWeight()<getWeight())
                return 1;
            else if(nw.getWeight()==getWeight())
                return 0;
            else
                return -1;
        }
        catch(IOException e)
        {
            return 0;
        }
    }
    
    /**
     * gets the distance to the end from the current node in the NodeWay
     * @return
     * @throws IOException 
     */
    public double distToEnd() throws IOException
    {
        return Math.sqrt(Math.pow(end.getLatitude()-crrnt.getLatitude(),2)+
                         Math.pow(end.getLongitude()-crrnt.getLongitude(),2));
    }
}
