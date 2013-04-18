package edu.brown.cs32.jcadler.dijkstra;

import java.util.List;
import edu.brown.cs32.jcadler.nodeWay.Node;
import edu.brown.cs32.jcadler.nodeWay.Way;
import java.io.IOException;

/**
 *
 * @author john
 */
public interface Dijkstra 
{
    public List<Way> getMinDistance(Node start, Node end) throws IOException;
}
