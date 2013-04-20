package edu.brown.cs32.jcadler.Threads;

import edu.brown.cs32.jcadler.retrieval.Retriever;
import edu.brown.cs32.mlazosjcadler.drawer.Street;
import java.util.List;
import javax.swing.JViewport;
import java.awt.Point;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.util.ArrayList;
import edu.brown.cs32.jcadler.nodeWay.*;
import edu.brown.cs32.mlazosjcadler.drawer.Drawer;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import edu.brown.cs32.mlazosjcadler.drawer.Street;
import edu.brown.cs32.mlazosjcadler.drawer.Screen;

/**
 *
 * @author john
 */
public class UpdaterThread extends Thread
{
    private BigDecimal minLat;
    private BigDecimal minLong;
    private BigDecimal maxLat;
    private BigDecimal maxLong;
    private BigDecimal diffLong;
    private BigDecimal diffLat;
    private JViewport viewport;
    private JLabel img;
    private Retriever r;
    private List<Street> streets;
    private Drawer draw;
    private boolean running;
    private boolean run;
    private boolean update;
    private boolean zoom;
    private final String runSynch = "running";
    private final String synch = "synch";
    private final String redrawSynch = "redrawing";
    
    public UpdaterThread(BigDecimal mnLt, BigDecimal mnLg, BigDecimal mxLt, BigDecimal mxLg, 
                         Retriever ret, JViewport view, Drawer d, JLabel lbl)
    {
        minLat=mnLt;
        minLong=mnLg;
        maxLat=mxLt;
        maxLong=mxLg;
        viewport = view;
        img = lbl;
        diffLong = (maxLong.subtract(minLong)).divide(BigDecimal.valueOf(3));
        diffLat = (maxLat.subtract(minLat)).divide(BigDecimal.valueOf(3));
        draw = d;
        r = ret;
        streets=null;
        running = false;
        run = true;
        update = false;
        this.start();
        zoom=false;
    }
    
    public void run()
    {
        while(run)
        {
            synchronized(runSynch)
            {
                running = true;
            }
            streets=null;
            boolean redraw = false;
            Point viewPos = viewport.getViewPosition();
            synchronized(synch)
            {
                if(zoom)
                    redraw=true;
                if(viewPos.getX()+250>1000)
                {
                    minLong=minLong.add(diffLong);
                    maxLong=maxLong.add(diffLong);
                    viewPos.setLocation(250, viewPos.getY());
                    redraw=true;
                }
                else if(viewPos.getX()+250<500)
                {
                    minLong=minLong.subtract(diffLong);
                    maxLong=maxLong.subtract(diffLong);
                    viewPos.setLocation(750,viewPos.getY());
                    redraw=true;
                }
                else if(viewPos.getY()+250<500)
                {
                    minLat=minLat.subtract(diffLat);
                    maxLat=maxLat.subtract(diffLat);
                    viewPos.setLocation(viewPos.getX(),750);
                    redraw=true;
                }
                else if(viewPos.getY()+250>1000)
                {
                    minLat=minLat.add(diffLat);
                    maxLat=maxLat.add(diffLat);
                    viewPos.setLocation(viewPos.getX(),250);
                    redraw=true;
                }
            }
            synchronized(redrawSynch)
            {
                if(redraw)
                {
                    System.out.println("redrawing");
                    List<Way> ways;
                    try
                    {
                    ways = r.getWaysInRange(minLong.doubleValue(), maxLong.doubleValue(), 
                                            minLat.doubleValue(), maxLat.doubleValue(), false);
                    }
                    catch(IOException e)
                    {
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                        ways = new ArrayList<>();
                    }
                    List<Street> streets = new ArrayList<>();
                    streets.addAll(ways);
                    Screen screen = draw.generateMap(streets, minLong.doubleValue(), minLat.doubleValue(), 
                                                     maxLong.doubleValue(), maxLat.doubleValue());
                    img.setIcon(new ImageIcon(screen.getImage()));
                    viewport.repaint();
                    System.out.println("done drawing");
                }
                redraw=false;
                zoom=false;
            }
            synchronized(runSynch)
            {
                running = false;
            }
            update=false;
            while(!update)
            {
                synchronized(synch)
                {
                    try
                    {
                        synch.wait();
                    }
                    catch(InterruptedException e)
                    {
                        System.out.println("interrupted");
                    }
                }
            }
        }
    }
    
    public List<Street> getStreets()
    {
        synchronized(redrawSynch)
        {
            return streets;
        }
    }
    
    public BigDecimal getMinLong()
    {
        synchronized(synch)
        {
            return minLong;
        }
    }
    
    public BigDecimal getMinLat()
    {
        synchronized(synch)
        {
            return minLat;
        }
    }
    
    public BigDecimal getMaxLong()
    {
        synchronized(synch)
        {
            return maxLong;
        }
    }
    
    public BigDecimal getMaxLat()
    {
        synchronized(synch)
        {
            return maxLat;
        }
    }
    
    public boolean running()
    {
        synchronized(runSynch)
        {
            return running;
        }
    }
    
    public void update()
    {
        synchronized(synch)
        {
            update = true;
            synch.notifyAll();
        }
    }
    
    public void zoomOut()
    {
        minLong=minLong.add(BigDecimal.valueOf(0.01));
        maxLong=maxLong.add(BigDecimal.valueOf(0.01));
        minLat=minLat.add(BigDecimal.valueOf(0.01));
        maxLat=maxLat.add(BigDecimal.valueOf(0.01));
        diffLong = (maxLong.subtract(minLong)).divide(BigDecimal.valueOf(3));
        diffLat = (maxLat.subtract(minLat)).divide(BigDecimal.valueOf(3));
        zoom=true;
        System.out.println("zooming out");
        update();
    }
    
    public void zoomIn()
    {
        minLong=minLong.add(BigDecimal.valueOf(0.01));
        maxLong=maxLong.add(BigDecimal.valueOf(0.01));
        minLat=minLat.add(BigDecimal.valueOf(0.01));
        maxLat=maxLat.add(BigDecimal.valueOf(0.01));
        diffLong = (maxLong.subtract(minLong)).divide(BigDecimal.valueOf(3));
        diffLat = (maxLat.subtract(minLat)).divide(BigDecimal.valueOf(3));
        zoom=true;
        System.out.println("zooming in");
        update();
    }
}
