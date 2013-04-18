package edu.brown.cs32.mlazosjcadler.drawer;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.imageio.*;

import edu.brown.cs32.mlazos.kdtree.Ndimensional;

public class Screen 
{
	private BufferedImage image;
	private Set<Ndimensional>nodes;

	public Screen(Set<Ndimensional> nodesOnScreen, BufferedImage img)
	{
		image = img;
		nodes = nodesOnScreen;
                
                File outputfile = new File("scaled." + "png");
                try
	    {	

		ImageIO.write(img, "png", outputfile);
	    
	    }
	    catch(IOException e)
	    {
		   
	    }
                
	}
	
	public List<Ndimensional> getNodeList()
	{
		return new ArrayList<Ndimensional>(nodes);
	}
	
	public BufferedImage getImage()
	{
		return image;
	}
}
