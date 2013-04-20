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
    /**
     * returns the node with the given id, and exits based on the status of exit
     * @param id
     * @param exit
     * @return
     * @throws IOException
     * @throws IllegalArgumentException 
     */
    
    public Node getNode(String id, Boolean exit) throws IOException,IllegalArgumentException;
    /**
     * returns a way with the given id. 
     * @param id
     * @param lazy whether or no the way is lazily instantiated
     * @param exit when to exti
     * @return
     * @throws IOException
     * @throws IllegalArgumentException 
     */
    
    public Way getWay(String id, boolean lazy, Boolean exit) throws IOException,IllegalArgumentException;
    /**
     * gets all nodes within a given latitude, longitude range
     * @param minLong
     * @param maxLong
     * @param minLat
     * @param maxLat
     * @param exit
     * @return
     * @throws IOException 
     */
    
    public List<Node> getNodesInRange(double minLong, double maxLong,
                                      double minLat, double maxLat, Boolean exit) throws IOException;
    /**
     * gets the names of all of the streets 
     * @return 
     */
    public List<String> getNames();
    
    /**
     * gets the intersection of the two streets
     * @param street1
     * @param street2
     * @return
     * @throws IOException 
     */
    public List<Node> getIntersection(String street1, String street2) throws IOException;
    
    /**
     * gets all of the ways within the given longitude, latitude range
     * @param minLong
     * @param maxLong
     * @param minLat
     * @param maxLat
     * @param exit
     * @return
     * @throws IOException 
     */
    public List<Way> getWaysInRange(double minLong, double maxLong,
                                    double minLat, double maxLat, Boolean exit) throws IOException;
    
    /**
     * gets all of the ids of the ways which are associated with a given street name
     * @param name
     * @return 
     */
    public List<String> getNamedStreetIDs(String name);
}
