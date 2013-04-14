package edu.brown.cs32.jcadler.search;

import java.util.PriorityQueue;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import edu.brown.cs32.jcadler.nodeWay.Way;
import edu.brown.cs32.jcadler.nodeWay.Node;
import edu.brown.cs32.jcadler.retrieval.Retriever;

/**
 * implements Dijkstra's algorithm
 * @author john
 */
public class Root
{
    private PriorityQueue<NodeWay> crrnt;
    private Node start;
    private Retriever r;
    
    public Root(Node s, Retriever ret)
    {
        crrnt=new PriorityQueue<>();
        start=s;
        r=ret;
    }
    
    /**
     * gets the minimum path from start to the specified end node,
     * the implementation specifics are discussed in the readme
     * @param end
     * @return
     * @throws IOException 
     */
    public List<NodeWay> minPathTo(Node end) throws IOException
    {
        List<NodeWay> p = new ArrayList<>();
        p.add(new NodeWay(null,start,end,null));
        List<NodeWay> children = getChildren(start,end,p);
        List<String> already = new ArrayList<>();
        crrnt.addAll(children);
        NodeWay rem=crrnt.poll();
        System.out.println("end ID: "+end.getID());
        while(rem!=null && !rem.getWay().getEnd().getID().equals(end.getID()))
        {
            System.out.println("loop start");
            System.out.println("ID: "+rem.getNode().getID());
            if(already.contains(rem.getNode().getID()))
            {
                rem=crrnt.poll();
                continue;
            }
            already.add(rem.getNode().getID());
            p=rem.getParents();
            p.add(rem);
            List<NodeWay> add = getChildren(rem.getNode(),end,p);
            System.out.println("got children");
            crrnt.addAll(add);
            rem = crrnt.poll();
            System.out.println("polled");
            System.out.println(rem.getWay().getName());
        }
        if(rem==null)
            return null;
        List<NodeWay> ret = rem.getParents();
        ret.add(rem);
        return ret;
    }
    
    /**
     * gets the children of the specified node, n, with the end node e
     * used for A* and parents used to instantiate the returned NodeWays
     * @param n
     * @param e
     * @param parents
     * @return
     * @throws IOException 
     */
    private List<NodeWay> getChildren(Node n, Node e, List<NodeWay> parents) throws IOException
    {
        List<String> ws = n.getWayIDs();
        List<Way> children = new ArrayList<>();
        for(String s : ws)
            children.add(r.getWay(s,true,false));
        List<NodeWay> ret = new ArrayList<>();
        for(Way w : children)
            ret.add(new NodeWay(w,w.getEnd().getID(),parents,r,e));
        return ret;
    }
}