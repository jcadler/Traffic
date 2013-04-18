package edu.brown.cs32.jcadler.maps;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.DocumentListener;
import javax.swing.*;
import javax.swing.JLabel;
import java.awt.*;
import java.awt.event.*;
import javax.swing.ScrollPaneConstants.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.List;
import java.util.ArrayList;
import edu.brown.cs32.mlazosjcadler.drawer.*;
import edu.brown.cs32.jcadler.retrieval.Retriever;
import edu.brown.cs32.jcadler.nodeWay.*;
import edu.brown.cs32.jcadler.autocorrect.dictionary.*;
import java.math.BigDecimal;
import edu.brown.cs32.mlazos.kdtree.Ndimensional;
import edu.brown.cs32.mlazos.kdtree.KdTree;
import edu.brown.cs32.mlazos.kdtree.KDNode;
import edu.brown.cs32.jcadler.maps.KDPoint;

public class testGUI {
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
	
	public static JTextField textBox1;
	public static JTextField textBox2;
        private Retriever r;


	public void runGUI(Retriever ret) throws IOException
        {
            r=ret;
            Drawer d = new BlockDrawer(1500,1500);
            System.out.println("drawer made");
            double minLong = -71.44;
            double maxLong = -71.38;
            double minLat = 41.79;
            double maxLat = 41.85;
            List<Node> nodes = r.getNodesInRange(minLong,maxLong,minLat,maxLat,false);
            List<Street> streets = getWays(nodes);
            Screen screen = d.generateMap(streets, minLong,minLat,maxLong,maxLat);
            ImageIcon init = new ImageIcon(screen.getImage());
            JLabel l = new JLabel(init);
            JScrollPane s = new JScrollPane(l);
            s.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
            s.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            JViewport v = s.getViewport();        
            MouseAdapter m = new mousePanner(l,screen.getImage(),d);
            v.addMouseMotionListener(m);
            v.addMouseListener(m);
            v.addMouseWheelListener(m);
            JFrame frame = new JFrame("Maps");
            JPanel right = new JPanel();
            right.add(s);
            right.setSize(500,500);
            right.setPreferredSize(new Dimension(500,500));
            frame.add(right, BorderLayout.EAST);
            frame.setPreferredSize(new Dimension(3000, 2000));
            frame.setSize(new Dimension(3000, 2000));
            frame.setResizable(false);
            frame.setLayout(new BorderLayout());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new MapsGUI(), BorderLayout.WEST);
            frame.pack();
            frame.setVisible(true);
	}
	
	/**
	 * 
	 * @author mlazos
	 *
	 *This class is the GUI for Autocorrect. 
	 *It has a private inner class which alerts the GUI if the user has made changes 
	 *to the text field.
	 */
	private class MapsGUI extends JPanel
	{
	    /**
		 * This class is the GUI window
		 */
		private static final long serialVersionUID = 1L;
		protected JTextField textField;
	    protected JTextArea textArea;
		protected JTextField textField2;
	    protected JTextArea textArea2;
	 
	    public MapsGUI() 
	    {
	        this.setLayout(new BorderLayout());
	        Dimension size = new Dimension(250, 500);
	        
	        JPanel overArchingPanel = new JPanel();
	        overArchingPanel.setLayout(new BorderLayout());
	        
	        JPanel panel1 = new JPanel();
	        panel1.setLayout(new BorderLayout());
	        
	        JLabel upperField = new JLabel("Enter Address 1");
	        textField = new JTextField(30);
	        textArea = new JTextArea(5, 5);
	        textArea.setEditable(false);
	        JScrollPane scrollPane = new JScrollPane(textArea);
	        
	        panel1.add(upperField, BorderLayout.NORTH);
	        panel1.add(textField, BorderLayout.CENTER);
	        panel1.add(scrollPane, BorderLayout.SOUTH);
	        
	        
	        overArchingPanel.add(panel1, BorderLayout.NORTH);
	        
	        
	        JPanel panel2 = new JPanel();
	        panel2.setLayout(new BorderLayout());
	      
	        JLabel lowerField = new JLabel("Enter Address 2");
	        
	        textField2 = new JTextField(30);
	        textArea2 = new JTextArea(5, 5);
	        textArea2.setEditable(false);
	        JScrollPane scrollPane2 = new JScrollPane(textArea2);
	        
	        panel2.add(lowerField, BorderLayout.NORTH);
	        panel2.add(textField2, BorderLayout.CENTER);
	        panel2.add(scrollPane2, BorderLayout.SOUTH);
	        
	        overArchingPanel.add(panel2, BorderLayout.SOUTH);
	        
	        JPanel ButtonPanel = new JPanel();
	        
	        JButton findIntersection = new JButton("Find Intersection");
	        
	        JButton djikstraStart = new JButton("Find Route");
	        
	        JButton clear = new JButton("Clear Selections");
	        
	        ButtonPanel.add(djikstraStart);
	        ButtonPanel.add(clear);
	        ButtonPanel.add(findIntersection);
	        ButtonPanel.setSize(new Dimension(250, 430));
	              
	        
	        this.add(overArchingPanel, BorderLayout.NORTH);
	        
	        class DjikstraListener implements ActionListener
	        {
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					
					
				}	
	        }
	        
	        class ClearActionListener implements ActionListener
	        {

				@Override
				public void actionPerformed(ActionEvent e) 
				{
				
					
				}
	        	
	        }
	        
	        djikstraStart.addActionListener(new DjikstraListener());
	        clear.addActionListener(new ClearActionListener());
	        
	        this.add(ButtonPanel, BorderLayout.CENTER);
	    }
	 
	    

	}
	
	private  List<Street> getWays(List<Node> l) throws IOException
    {
        List<Street> ret = new ArrayList<>();
        List<String> wIDs = new ArrayList<>();
        for(Node n : l)
            wIDs.addAll(n.getWayIDs());
        int i=0;
        for(String s : wIDs)
        {
            ret.add(r.getWay(s,false,false));
            i++;
        }
        return ret;
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
    
    private class mousePanner extends MouseAdapter
    {
        private final Point crrnt = new Point();
        private JLabel img;
        private Drawer draw;
        private BigDecimal minLong;
        private BigDecimal maxLong;
        private BigDecimal minLat;
        private BigDecimal maxLat;
        private BigDecimal diffLong;
        private BigDecimal diffLat;
        private List<Ndimensional> crrntNodes;
        private Node start;
        private Node end;
        
        public mousePanner(JLabel i, BufferedImage im, Drawer d) throws IOException
        {
            img=i;
            draw=d;
            minLong = BigDecimal.valueOf(-71.46);
            maxLong = BigDecimal.valueOf(-71.4);
            minLat = BigDecimal.valueOf(41.83);
            maxLat = BigDecimal.valueOf(41.89);
            diffLong = (maxLong.subtract(minLong)).divide(BigDecimal.valueOf(3));
            diffLat = (maxLat.subtract(minLat)).divide(BigDecimal.valueOf(3));
            start = null;
            end = null;
        }
                
        @Override
        public void mouseDragged(MouseEvent e)
        {
            JViewport view = (JViewport)e.getSource();
            Point moved = e.getPoint();
            Point viewPos = view.getViewPosition();
            viewPos.translate((int)(crrnt.getX()-moved.getX()),(int)(crrnt.getY()-moved.getY()));
            if(viewPos.getX()+250>1000)
            {
                minLong=minLong.add(diffLong);
                maxLong=maxLong.add(diffLong);
                viewPos.setLocation(250, viewPos.getY());
            }
            else if(viewPos.getX()+250<500)
            {
                minLong=minLong.subtract(diffLong);
                maxLong=maxLong.subtract(diffLong);
                viewPos.setLocation(750,viewPos.getY());
            }
            else if(viewPos.getY()+250<500)
            {
                minLat=minLat.subtract(diffLat);
                maxLat=maxLat.subtract(diffLat);
                viewPos.setLocation(viewPos.getX(),750);
            }
            else if(viewPos.getY()+250>1000)
            {
                minLat=minLat.add(diffLat);
                maxLat=maxLat.add(diffLat);
                viewPos.setLocation(viewPos.getX(),250);
            }
            else
            {
                view.setViewPosition(viewPos);
                view.repaint();
                crrnt.setLocation(moved);
                return;
            }
            try
            {
                redraw();
                view.setViewPosition(viewPos);
                view.repaint();
                crrnt.setLocation(moved);
            }
            catch(Exception except)
            {
                System.out.println(except.getMessage());
                except.printStackTrace();
            }
        }
        
        @Override
        public void mouseWheelMoved(MouseWheelEvent e)
        {
            int rotate = e.getWheelRotation();
            if(rotate>=0)
            {
                for(int i=0;i<rotate;i++)
                {
                    minLat=minLat.subtract(BigDecimal.valueOf(0.01));
                    minLong=minLong.subtract(BigDecimal.valueOf(0.01));
                    maxLat=maxLat.add(BigDecimal.valueOf(0.01));
                    maxLong=maxLong.add(BigDecimal.valueOf(0.01));
                }
            }
            else
            {
                for(int i=0;i<Math.abs(rotate);i++)
                {
                    BigDecimal minLatChange=minLat.add(BigDecimal.valueOf(0.01));
                    BigDecimal minLongChange=minLong.add(BigDecimal.valueOf(0.01));
                    BigDecimal maxLatChange=maxLat.subtract(BigDecimal.valueOf(0.01));
                    BigDecimal maxLongChange=maxLong.subtract(BigDecimal.valueOf(0.01));
                    if(minLatChange.compareTo(maxLatChange)<0 && 
                       minLongChange.compareTo(maxLongChange)<0)
                    {
                        minLat=minLatChange;
                        minLong=minLongChange;
                        maxLat=maxLatChange;
                        maxLong=maxLongChange;
                    }
                }
            }
            diffLong = (maxLong.subtract(minLong)).divide(BigDecimal.valueOf(3));
            diffLat = (maxLat.subtract(minLat)).divide(BigDecimal.valueOf(3));
            try
            {
            redraw();
            }
            catch(IOException except)
            {
                System.out.println(except.getMessage());
                except.printStackTrace();
            }
        }
        
        @Override
        public void mousePressed(MouseEvent e)
        {
            crrnt.setLocation(e.getPoint());
        }
        
        @Override
        public void mouseClicked(MouseEvent e)
        {
            Point screenLoc = e.getLocationOnScreen();
            Point viewLoc = ((JViewport)e.getSource()).getViewPosition();
            Point imageLoc = new Point((int)(viewLoc.getX()+screenLoc.getX()),(int)(viewLoc.getY()+screenLoc.getY()));
            KDPoint p = draw.imagePointToKDPoint(imageLoc.getX(),imageLoc.getY(),
                                                 minLong.doubleValue(),minLat.doubleValue(),
                                                 maxLong.doubleValue(),maxLat.doubleValue());
            if(start==null)
                start = (Node)getNearestNeighbor(crrntNodes,p);
            else
                end = (Node)getNearestNeighbor(crrntNodes,p);
        }
        
        private void redraw() throws IOException
        {
            List<Street> streets=getWays(r.getNodesInRange(minLong.doubleValue(), maxLong.doubleValue(), 
                                         minLat.doubleValue(), maxLat.doubleValue(),false));
            Screen scrn = draw.generateMap(streets, minLong.doubleValue(), minLat.doubleValue(), 
                                                       maxLong.doubleValue(), maxLat.doubleValue());
            crrntNodes = scrn.getNodeList();
            img.setIcon(new ImageIcon(scrn.getImage()));
        }
        
        private Ndimensional getNearestNeighbor(List<Ndimensional> list, Ndimensional start)
        {
            KdTree<Ndimensional> tree = KDNode.buildTree(list, 0);
            Ndimensional result = tree.getQueueOfArbitraryNumberOfNeighbors(1,start).poll();
            return result;
        }
    }
 

    /*public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() 
        {
            public void run() 
            {
                new testGUI().runGUI();
            }
        });
    }*/
}