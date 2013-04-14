package edu.brown.cs32.jcadler.retrieval;

import edu.brown.cs32.jcadler.nodeWay.Node;
import edu.brown.cs32.jcadler.nodeWay.Way;
import java.util.List;
import java.io.IOException;

/**
 *
 * @author john
 */
public interface Retriever 
{
    public Node getNode(String id, Boolean exit) throws IOException,IllegalArgumentException;
    public Way getWay(String id, boolean lazy, Boolean exit) throws IOException,IllegalArgumentException;
    public List<Node> getNodesInRange(double minLong, double maxLong,
                                      double minLat, double maxLat, Boolean exit) throws IOException;
    public List<String> getNames();
    public List<Node> getIntersection(String street1, String street2) throws IOException;
    public List<Way> getWaysInRange(double minLong, double maxLong,
                                    double minLat, double maxLat, Boolean exit) throws IOException;
}
