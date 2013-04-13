package edu.brown.cs32.jcadler.nodeWay;

import java.io.IOException;
import edu.brown.cs32.jcadler.retrieval.Retriever;
import edu.brown.cs32.mlazosjcadler.drawer.Street;
import edu.brown.cs32.mlazos.kdtree.Ndimensional;

/**
 * The representation of a street (used for both the Root class (Dijkstra's algorithm)
 * and for drawing streets on the map
 * @author john
 */
public class Way implements Street
{
    private String name;
    private String id;
    private Node start;
    private Node end;
    
    public Way(String i,String n, String startID, String endID, Retriever r)
    {
        id=i;
        name=n;
        start=new Node(startID,r);
        end=new Node(endID,r);
    }
    
    public Way(String i, String n, Node s, Node e)
    {
        id=i;
        name=n;
        start=s;
        end=e;
    }
    
    public String getName()
    {
        return name;
    }
    
    public String getID()
    {
        return id;
    }
    
    public Node getStart()
    {
        return start;
    }
    
    public Node getEnd()
    {
        return end;
    }
    
    /**
     * gets the weight of the street for use in Dijkstra's
     * @return
     * @throws IOException 
     */
    public double getWeight() throws IOException
    {
        return Math.sqrt(Math.pow(end.getLatitude()-start.getLatitude(),2)+
                         Math.pow(end.getLongitude()-start.getLongitude(),2));
    }
    
    public String getStreetName()
    {
        return name;
    }
    
    public Double getX1()
    {
        try
        {
            return start.getLongitude();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    public Double getY1()
    {
        try
        {
            return start.getLatitude();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    public Double getX2()
    {
        try
        {
            return end.getLongitude();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    public Double getY2()
    {
        try
        {
            return end.getLatitude();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    public Boolean awayFromX1Y1()
    {
        return true;
    }
    
    public Ndimensional getStartPoint()
    {
        return start;
    }
    
    public Ndimensional getEndPoint()
    {
        return end;
    }
    
    public Double getTraffic()
    {
        return 1d;
    }
}
