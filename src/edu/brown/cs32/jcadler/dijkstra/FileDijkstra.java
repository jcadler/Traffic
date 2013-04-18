package edu.brown.cs32.jcadler.dijkstra;

import edu.brown.cs32.jcadler.nodeWay.Node;
import edu.brown.cs32.jcadler.nodeWay.Way;
import edu.brown.cs32.jcadler.search.NodeWay;
import edu.brown.cs32.jcadler.search.Root;
import edu.brown.cs32.jcadler.retrieval.Retriever;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

/**
 *
 * @author john
 */
public class FileDijkstra implements Dijkstra
{
    private Retriever r;
    
    public FileDijkstra(Retriever ret)
    {
        r=ret;
    }
    
    public List<Way> getMinDistance(Node start, Node end) throws IOException
    {
        Root rt = new Root(start,r);
        List<Way> ret = new ArrayList<>();
        List<NodeWay> mid = rt.minPathTo(end);
        for(int i=0;i<mid.size();i++)
        {
            if(mid.get(i).getWay()!=null)
                ret.add(mid.get(i).getWay());
        }
        return ret;
    }
}
