package edu.brown.cs32.jcadler.dijkstra;

import edu.brown.cs32.jcadler.nodeWay.Node;
import edu.brown.cs32.jcadler.search.NodeWay;
import edu.brown.cs32.jcadler.search.Root;
import edu.brown.cs32.jcadler.retrieval.Retriever;
import java.util.List;
import java.io.IOException;

/**
 *
 * @author john
 */
public class Dijkstra
{
    private Retriever r;
    
    public Dijkstra(Retriever ret)
    {
        r=ret;
    }
    
    public List<NodeWay> getMinDistance(Node start, Node end) throws IOException
    {
        Root rt = new Root(start,r);
        return rt.minPathTo(end);
    }
}
