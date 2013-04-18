package edu.brown.cs32.jcadler.maps.GUI;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.ScrollPaneConstants.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.awt.Rectangle;
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;

import edu.brown.cs32.mlazosjcadler.drawer.*;
import edu.brown.cs32.jcadler.retrieval.Retriever;
import edu.brown.cs32.jcadler.nodeWay.*;
import edu.brown.cs32.jcadler.autocorrect.dictionary.*;
import edu.brown.cs32.mlazos.kdtree.Ndimensional;
import edu.brown.cs32.mlazos.kdtree.KdTree;
import edu.brown.cs32.mlazos.kdtree.KDNode;
import edu.brown.cs32.jcadler.maps.KDPoint;
import edu.brown.cs32.jcadler.dijkstra.Dijkstra;
import edu.brown.cs32.jcadler.Threads.UpdaterThread;
import edu.brown.cs32.jcadler.retrieval.NetworkRetriever;

/**
 *
 * @author john
 */
public class GUI 
{
    private Retriever r;
    private JTextField first;
    private JTextField second;
    
    public GUI(Retriever ret)
    {
        r=ret;
    }
    
    public void runGui() throws IOException
    {
    	
        Drawer d = new BlockDrawer(1500,1500);
        System.out.println("drawer made");
        double minLong = -71.59;
        double maxLong = -71.53;
        double minLat = 41.79;
        double maxLat = 41.85;
        List<Way> ways = r.getWaysInRange(minLong,maxLong,minLat,maxLat,false);
        List<Street> streets = new ArrayList<>();
        for(Way w : ways)
            streets.add(w);
        System.out.println("got "+streets.size()+" streets");
        Screen screen = d.generateMap(streets, minLong,minLat,maxLong,maxLat);
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        ImageIcon init = new ImageIcon(screen.getImage());
        JLabel l = new JLabel(init);
        JScrollPane s = new JScrollPane(l);
        JPanel p = new JPanel();
        p.add(s);
        p.setPreferredSize(new Dimension(500,500));
        s.setSize(new Dimension(500,500));
        s.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        s.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JViewport v = s.getViewport();
        s.setPreferredSize(new Dimension(500,500));
        s.setSize(new Dimension(500,500));
        MouseAdapter m = new mousePanner(l,screen.getImage(),d,minLong,maxLong,minLat,maxLat,
                                         screen.getNodeList(),streets,v);
        p.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        v.addMouseMotionListener(m);
        v.addMouseListener(m);
        v.addMouseWheelListener(m);
        f.getContentPane().add(p,BorderLayout.WEST);
        f.getContentPane().add(createAutocorrectPanel(makeDictionary(),(mousePanner)m),BorderLayout.EAST);
        f.setSize(1050,500);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
        v.scrollRectToVisible(new Rectangle(500,500,500,500));
    }
    
    private JPanel createAutocorrectPanel(Dictionary d, mousePanner m)
    {
    	JPanel overArchingPanel = new JPanel(new BorderLayout());
    	
        List<Analyzer> la = CommandLine.selectAnalyzers(true, true, 2, false, d);
        JTextField txtField = new JTextField(40);
        first = txtField;
        JTextArea txtArea = new JTextArea(5,5);
        txtArea.setEditable(false);
        JPanel p = new JPanel(new BorderLayout());
        JLabel upperText = new JLabel("Address 1");
        txtField.getDocument().addDocumentListener(new LiveUpdater(txtField,txtArea,d,la,false));
        p.add(upperText, BorderLayout.NORTH);
        p.add(txtField, BorderLayout.CENTER);
        p.add(txtArea, BorderLayout.SOUTH);
        Dimension dim = new Dimension(500,
                                      txtField.getPreferredSize().height + txtArea.getPreferredSize().height + 40);
        p.setPreferredSize(dim);
        p.setSize(dim);
        
        List<Analyzer> la2 = CommandLine.selectAnalyzers(true, true, 2, false, d);
        JTextField txtField2 = new JTextField(40);
        second = txtField2;
        JTextArea txtArea2 = new JTextArea(5,5);
        txtArea.setEditable(false);
        JPanel p2 = new JPanel(new BorderLayout());
        JLabel upperText2 = new JLabel("Address 2");
        txtField2.getDocument().addDocumentListener(new LiveUpdater(txtField2,txtArea2,d,la2,false));
        p2.add(upperText2, BorderLayout.NORTH);
        p2.add(txtField2, BorderLayout.CENTER);
        p2.add(txtArea2, BorderLayout.SOUTH);
        Dimension dim2 = new Dimension(500,
                                      txtField2.getPreferredSize().height + txtArea2.getPreferredSize().height + 40);
        p2.setPreferredSize(dim2);
        p2.setSize(dim2);
        
        m.setFirstTextField(txtField);
        m.setSecondTextField(txtField2);
        
        JPanel textFieldPanel = new JPanel(new BorderLayout());
        textFieldPanel.add(p, BorderLayout.NORTH);
        textFieldPanel.add(p2, BorderLayout.SOUTH);
       
        JPanel ButtonPanel = new JPanel();
        
        overArchingPanel.add(textFieldPanel, BorderLayout.NORTH);
        
        JButton findIntersection = new JButton("Find Intersection");
        findIntersection.setActionCommand("findIntersection");
        findIntersection.addActionListener(m);
        
        JButton dijkstraStart = new JButton("Find Route");
        dijkstraStart.setActionCommand("dijkstra");
        dijkstraStart.addActionListener(m);
        
        JButton clear = new JButton("Clear Selections");
        clear.setActionCommand("clear");
        clear.addActionListener(m);
        
        ButtonPanel.add(dijkstraStart);
        ButtonPanel.add(clear);
        ButtonPanel.add(findIntersection);
        ButtonPanel.setSize(new Dimension(250, 400));
              
        overArchingPanel.add(ButtonPanel, BorderLayout.SOUTH);
        
        return overArchingPanel;
    }
    
    private Dictionary makeDictionary()
    {
        Dictionary dict = new Dictionary();
        for(String str : r.getNames())
        {
            String put = str.replaceAll("[^A-Za-z ]","").trim().toLowerCase();
            String[] split = put.split(" ");
            if(split.length>=1)
            {
                String last="";
                if(!split[0].trim().equals(""))
                {
                    dict.insertWord(split[0].trim());
                    last=split[0].trim();
                }
                for(int i=1;i<split.length;i++)
                {
                    
                    String insert = split[i].trim();
                    if(insert.equals(""))
                        continue;
                    dict.insertWord(insert);
                    if(!last.equals(""))
                        dict.addNextWord(last, insert);
                    last=insert;
                }
            }
        }
        return dict;
    }
    
    /**
     * used to monitor mouse events and move the JViewport as necessary
     * a full description is in the readme
     */
    private class mousePanner extends MouseAdapter implements ActionListener
    {
        private final Point crrnt = new Point();
        private JLabel img;
        private Drawer draw;
        private List<Ndimensional> crrntNodes;
        private List<Street> crrntStreets;
        private Node start;
        private Node end;
        private JTextField first;
        private JTextField second;
        private boolean running;
        private UpdaterThread thread;
        private final String synchOnThis = "SYNCHONTHISPLEASE!";
        
        public mousePanner(JLabel i, BufferedImage im, Drawer d, 
                           double mnLg, double mxLg, double mnLt, double mxLt,
                           List<Ndimensional> nodes, List<Street> streets, JViewport view) throws IOException
        {
            img=i;
            draw=d;
            BigDecimal minLong = BigDecimal.valueOf(mnLg);
            BigDecimal maxLong = BigDecimal.valueOf(mxLg);
            BigDecimal minLat = BigDecimal.valueOf(mnLt);
            BigDecimal maxLat = BigDecimal.valueOf(mxLt);
            thread = new UpdaterThread(minLat,minLong,maxLat,maxLong,r,view,draw,img);
            start = null;
            end = null;
            crrntNodes = nodes;
            crrntStreets = streets;
            running = false;
        }
                
        @Override
        public void mouseDragged(MouseEvent e)
        {
            if(!thread.running())
                thread.update();
            JViewport view = (JViewport)e.getSource();
            Point moved = e.getPoint();
            Point viewPos = view.getViewPosition();
            viewPos.translate((int)(crrnt.getX()-moved.getX()),(int)(crrnt.getY()-moved.getY()));
            view.setViewPosition(viewPos);
            view.repaint();
            crrnt.setLocation(moved);
        }
        
        @Override
        public void mouseWheelMoved(MouseWheelEvent e)
        {
        }
        
        @Override
        public void mousePressed(MouseEvent e)
        {
            crrnt.setLocation(e.getPoint());
        }
        
        @Override
        public void mouseClicked(MouseEvent e)
        {
            BigDecimal minLong = thread.getMinLong();
            BigDecimal minLat = thread.getMinLat();
            BigDecimal maxLong = thread.getMaxLong();
            BigDecimal maxLat = thread.getMaxLat();
            JViewport viewport = (JViewport)e.getSource();
            Point view = viewport.getViewPosition();
            Point screenPos = e.getPoint();
            KDPoint p = draw.imagePointToKDPoint(view.getX()+screenPos.getX(),view.getY()+screenPos.getY(),
                                                 minLong.doubleValue(),minLat.doubleValue(),
                                                 maxLong.doubleValue(),maxLat.doubleValue());
            BufferedImage update;
            try
            {
                if(start==null)
                {
                    start = (Node)getNearestNeighbor(crrntNodes,p);
                    update = draw.markIntersection(start.getLongitude(),start.getLatitude(),
                                                   minLong.doubleValue(), minLat.doubleValue(), 
                                                   maxLong.doubleValue(), maxLat.doubleValue());
                }
                else if(end==null)
                {
                    end = (Node)getNearestNeighbor(crrntNodes,p);
                    update = draw.markIntersection(end.getLongitude(),end.getLatitude(),
                                                   minLong.doubleValue(), minLat.doubleValue(), 
                                                   maxLong.doubleValue(), maxLat.doubleValue());
                }
                else
                {
                    start = (Node)getNearestNeighbor(crrntNodes,p);
                    draw.eraseIntersections(minLong.doubleValue(), minLat.doubleValue(), 
                                            maxLong.doubleValue(), maxLat.doubleValue());
                    update = draw.markIntersection(start.getLongitude(),end.getLatitude(),
                                                    minLong.doubleValue(), minLat.doubleValue(), 
                                                    maxLong.doubleValue(), maxLat.doubleValue());
                    end=null;
                }
                img.setIcon(new ImageIcon(update));
            }
            catch(IOException ex)
            {
                System.out.println(ex.getMessage());
            }
        }
        
        @Override
        public void actionPerformed(ActionEvent e)
        {
            BigDecimal minLong = thread.getMinLong();
            BigDecimal minLat = thread.getMinLat();
            BigDecimal maxLong = thread.getMaxLong();
            BigDecimal maxLat = thread.getMaxLat();
            String command = e.getActionCommand();
            if(command.equals("clear"))
            {
                Screen update = draw.generateMap(crrntStreets,minLong.doubleValue(),minLat.doubleValue(),
                                                 maxLong.doubleValue(),maxLat.doubleValue());
                img.setIcon(new ImageIcon(update.getImage()));
            }
            else if(command.equals("dijkstra"))
            {
                Dijkstra dij = (NetworkRetriever)r;
                try
                {
                    List<Way> result = dij.getMinDistance(start, end);
                    List<Street> path = new ArrayList<>();
                    if(result!=null)
                    {
                        for(Way w : result)
                            path.add(w);
                        Screen update = draw.generateMap(crrntStreets, path,
                                                         minLong.doubleValue(), minLat.doubleValue(), 
                                                         maxLong.doubleValue(), maxLat.doubleValue());
                        img.setIcon(new ImageIcon(update.getImage()));
                    }
                    else
                        System.out.println("no path");
                }
                catch(IOException ex)
                {
                    System.out.println(ex.getMessage());
                }
            }
            else if(command.equals("findIntersection"))
            {
                try
                {
                    List<Node> intersections = r.getIntersection(first.getText(), second.getText());
                    if(intersections.isEmpty())
                        return;
                    BufferedImage update;
                    if(start==null)
                    {
                        start = intersections.get(0);
                        update = draw.markIntersection(start.getLongitude(),start.getLatitude(),
                                                       minLong.doubleValue(), minLat.doubleValue(), 
                                                       maxLong.doubleValue(), maxLat.doubleValue());
                    }
                    else if(end==null)
                    {
                        end = intersections.get(0);
                        update = draw.markIntersection(end.getLongitude(),end.getLatitude(),
                                                       minLong.doubleValue(), minLat.doubleValue(), 
                                                       maxLong.doubleValue(), maxLat.doubleValue());
                    }
                    else
                    {
                        start = intersections.get(0);
                        draw.eraseIntersections(minLong.doubleValue(), minLat.doubleValue(), 
                                                maxLong.doubleValue(), maxLat.doubleValue());
                        update = draw.markIntersection(start.getLongitude(),end.getLatitude(),
                                                        minLong.doubleValue(), minLat.doubleValue(), 
                                                        maxLong.doubleValue(), maxLat.doubleValue());
                        end=null;
                    }
                    img.setIcon(new ImageIcon(update));
                }
                catch(Exception ex)
                {
                    System.out.println(ex.getMessage());
                }
            }
        }
        
        public void setFirstTextField(JTextField one)
        {
            first = one;
        }
        
        public void setSecondTextField(JTextField two)
        {
            second = two;
        }
        
        private Ndimensional getNearestNeighbor(List<Ndimensional> list, Ndimensional start)
        {
            KdTree<Ndimensional> tree = KDNode.buildTree(list, 0);
            Ndimensional result = tree.getQueueOfArbitraryNumberOfNeighbors(1,start).poll();
            return result;
        }
    }
}
