package edu.brown.cs32.jcadler.maps;

import java.util.Scanner;
import edu.brown.cs32.jcadler.retrieval.Retriever;
import edu.brown.cs32.jcadler.dijkstra.Dijkstra;
import edu.brown.cs32.jcadler.nodeWay.*;
import edu.brown.cs32.jcadler.search.NodeWay;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.IOException;
import edu.brown.cs32.mlazos.kdtree.Ndimensional;
import edu.brown.cs32.mlazos.kdtree.KdTree;
import edu.brown.cs32.mlazos.kdtree.KDNode;
import edu.brown.cs32.jcadler.retrieval.NetworkRetriever;

/**
 *
 * @author john
 */
public class REPL 
{
    private Retriever r;
    
    public REPL(Retriever ret)
    {
        r=ret;
    }
    
    public void runREPL() throws IOException
    {
        Scanner in = new Scanner(System.in);
        String input;
        Dijkstra d = (NetworkRetriever)r;
        System.out.println("Ready");
        while(true)
        {
            input = in.nextLine();
            if(input.equals(""))
                break;
            List<String> commands = parseInput(input);
            if(commands.size()!=4)
                throw new Error("You can only provide 4 arguments");
            double[] latitudes = new double[2];
            double[] longitudes = new double[2];
            List<Way> nws;
            Node start;
            Node end;
            try
            {
                latitudes[0] = Double.parseDouble(commands.get(0));
                latitudes[1] = Double.parseDouble(commands.get(2));
                longitudes[0] = Double.parseDouble(commands.get(1));
                longitudes[1] = Double.parseDouble(commands.get(3));
                Ndimensional n;
                Ndimensional[] startEnd = new Ndimensional[2];
                for(int i=0;i<2;i++)
                {
                    n = new KDPoint(longitudes[i],latitudes[i]);
                    double minLat = latitudes[i]-0.01;
                    double minLong = longitudes[i]-0.01;
                    double maxLat = latitudes[i]+0.01;
                    double maxLong = longitudes[i]+0.01;
                    List<Node> nodes = r.getNodesInRange(minLong, maxLong, minLat, maxLat,false);
                    List<Ndimensional> KDSeed = new ArrayList<>();
                    for(Node nd : nodes)
                        KDSeed.add((Ndimensional)nd);
                    startEnd[i] = getNearestNeighbor(KDSeed,new KDPoint(longitudes[i],latitudes[i]));
                }
                start = (Node)startEnd[0];
                end = (Node)startEnd[1];
            }
            catch(Exception e)
            {
                List<Node> intersections = r.getIntersection(commands.get(0),commands.get(1));
                if(intersections.isEmpty())
                {
                    System.out.println(commands.get(0)+" and "+commands.get(1)+" do not intersect");
                    return;
                }
                start = intersections.get(0);
                intersections = r.getIntersection(commands.get(2),commands.get(3));
                if(intersections.isEmpty())
                {
                    System.out.println(commands.get(0)+" and "+commands.get(1)+" do not intersect");
                    return;
                }
                end = intersections.get(0);
            }
            nws = d.getMinDistance(start, end);
            if(nws==null)
                    System.out.println(start.getID()+" -/- "+end.getID());
                else
                {
                    for(Way w : nws)
                        System.out.println(w.getStart().getID()+" -> "+w.getEnd().getID()+" : "+w.getID());
                }
        }
    }
    
    private List<String> parseInput(String in)
    {
        List<String> ret = new ArrayList<>();
        if(in.contains("\""))
        {
            String[] split = in.split("\"");
            for(String s : split)
            {
                if(s.trim().equals(""))
                    continue;
                ret.add(s.trim());
            }
            return ret;
        }
        return Arrays.asList(in.split(" "));
    }
    
    private Ndimensional getNearestNeighbor(List<Ndimensional> list, Ndimensional start)
    {
    	KdTree<Ndimensional> tree = KDNode.buildTree(list, 0);
    	Ndimensional result = tree.getQueueOfArbitraryNumberOfNeighbors(1,start).poll();
    	return result;
    }
}
