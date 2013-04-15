package edu.brown.cs32.mlazosjcadler.drawer;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class DrawerTest {

	/**
	 * @param args
	 */
	public static void test(String[] args) 
	{
		List<Street> list = new ArrayList<Street>();
		List<Street> list2 = new ArrayList<Street>();
		
		for(int i = 0; i < 10; i++)
		{
			list.add(new StreetStub(new Double(0), new Double(5000), new Double(5000)));
			list2.add(new StreetStub(new Double(0), new Double(5000), new Double(5000)));
		}
		
		
		BlockDrawer bd = new BlockDrawer(1000, 1000);
		
		long start = System.currentTimeMillis();
		
		BufferedImage img = bd.generateMap(list, list2, -100.0, -100.0, 5000.0, 5000.0).getImage();
		
		long end = System.currentTimeMillis();
		
		System.out.println(end-start);
		
		BufferedImage img2 = bd.generateMap(list, list2, 0.0, 0.0, 1000.0, 1000.0).getImage();
		
	    File outputfile = new File("scaled." + "png");
	    try
	    {	

		ImageIO.write(img, "png", outputfile);
	    
	    }
	    catch(IOException e)
	    {
		   
	    }
	    
	    outputfile = new File("unscaled." + "png");
	    try
	    {	
		  
		ImageIO.write(img2, "png", outputfile);
	    
	    }
	    catch(IOException e)
	    {
		   
	    }
	}
}
