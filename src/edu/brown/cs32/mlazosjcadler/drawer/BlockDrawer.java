package edu.brown.cs32.mlazosjcadler.drawer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import edu.brown.cs32.jcadler.maps.KDPoint;

import javax.imageio.ImageIO;

import edu.brown.cs32.mlazos.kdtree.Ndimensional;

/**
 * 
 * @author mlazos
 *This class generates a map from a list of ways.  Additionally, it can generate highlighted streets with an overloaded method
 * with a second list of ways that are part of the highlighted path.
 * 
 *The class caches streets in 9 cells.   When the user moves, new data is provided and the class uses data in old cells to require fewer disk operations.*
 *
 */
public class BlockDrawer implements Drawer 
{
	private BufferedImage map;
	private Graphics2D mapGraphics;
	private HashSet<Ndimensional> nodesOnScreen;
	private HashMap<Integer, ArrayList<Street>>  cells; // Cells are arranged in a matrix with rows numbered 1,2,3 ; 4,5,6; 7,8,9;
	
	//Initializes a map graphics object, and the image object.
	public BlockDrawer(int width, int height)
	{
		map = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		mapGraphics = map.createGraphics();
		mapGraphics.setBackground(Color.LIGHT_GRAY);
		mapGraphics.setStroke(new BasicStroke(2));
		cells = new HashMap<Integer, ArrayList<Street>>(6);
		
		cells.put(1, new ArrayList<Street>());
		cells.put(2, new ArrayList<Street>());
		cells.put(3, new ArrayList<Street>());
		cells.put(4, new ArrayList<Street>());
		cells.put(5, new ArrayList<Street>());
		cells.put(6, new ArrayList<Street>());
		cells.put(7, new ArrayList<Street>());
		cells.put(8, new ArrayList<Street>());
		cells.put(9, new ArrayList<Street>());
	}
	
	
	//This function generates a map graphic from a list of Ways.
	public Screen generateMap(List<Street> streets, Double screenUpperLeftX, Double screenUpperLeftY, Double screenLowerRightX, Double screenLowerRightY)
	{
		mapGraphics.clearRect(0, 0, map.getWidth(), map.getHeight());
		nodesOnScreen = new HashSet<Ndimensional>(6000);
		
		drawStreets(streets, Color.BLACK, screenUpperLeftX, screenUpperLeftY, screenLowerRightX, screenLowerRightY);
		
		return new Screen(nodesOnScreen, map);//deepCopy(map));
	}
	
	//This function generates the map graphic with a path highlighted in green
	public Screen generateMap(List<Street> streets, List<Street> path, Double screenUpperLeftX, Double screenUpperLeftY, Double screenLowerRightX, Double screenLowerRightY)
	{	
		mapGraphics.clearRect(0, 0, map.getWidth(), map.getHeight());
		nodesOnScreen = new HashSet<Ndimensional>(6000);

		drawStreets(streets, Color.BLACK, screenUpperLeftX, screenUpperLeftY, screenLowerRightX, screenLowerRightY);
		drawStreets(path, new Color((int)(50*.935),(int)(205*.935),(int)(50*.935)), screenUpperLeftX, screenUpperLeftY, screenLowerRightX, screenLowerRightY);
		
		return new Screen(nodesOnScreen, map);//deepCopy(map));
	}
	
	//This function draws streets, along with names (not yet) if the name is not longer than the street.
	private void drawStreets(List<Street> streets, Color clr , Double screenUpperLeftX, Double screenUpperLeftY, Double screenLowerRightX, Double screenLowerRightY)
	{
		for(Street w : streets)
		{	
			drawStreet(w, clr, screenUpperLeftX, screenUpperLeftY, screenLowerRightX, screenLowerRightY);
		}			
	}
	
	
	//This function draws streets without adding them to the cache
	private void drawStreetsWithoutCache(List<Street> streets, Color clr , Double screenUpperLeftX, Double screenUpperLeftY, Double screenLowerRightX, Double screenLowerRightY)
	{
		for(Street w : streets)
		{	
			drawStreetWithoutCache(w, clr, screenUpperLeftX, screenUpperLeftY, screenLowerRightX, screenLowerRightY);
		}			
	}
	
	//This function handles drawing streets
	private void drawStreet(Street w, Color clr, Double screenUpperLeftX, Double screenUpperLeftY, Double screenLowerRightX, Double screenLowerRightY)
	{
		Double initialx1 = w.getX1();
		Double initialy1 = w.getY1();
		Double initialx2 = w.getX2();
		Double initialy2 = w.getY2();
		
		
		// Given the bounds of the screen in "the world", scale the x and y values so their positions in this
		//image with fixed dimensions are proportional
		//to their position in the world view.
		Double screenWidth = Math.abs(screenUpperLeftX - screenLowerRightX);
		Double x1Ratio = Math.abs(initialx1 - screenUpperLeftX)/screenWidth;
		Double x1 = x1Ratio * map.getWidth();
		
		Double x2Ratio = Math.abs(initialx2 - screenUpperLeftX)/screenWidth;
		Double x2 = x2Ratio * map.getWidth();
		
		Double screenHeight = Math.abs(screenUpperLeftY - screenLowerRightY);
		Double y1Ratio = Math.abs(initialy1 - screenUpperLeftY)/screenHeight;
		Double y1 = y1Ratio * map.getHeight();
		
		Double y2Ratio = Math.abs(initialy2 - screenUpperLeftY)/screenHeight;
		Double y2 = y2Ratio * map.getHeight();
		
		//scale the red color depending on traffic
		Double redness = w.getTraffic();
		Double scale = (redness - 1)/4.0 * 255.0;
		clr = new Color((int)(clr.getRed() + scale), clr.getGreen(),clr.getBlue());
		
		mapGraphics.setColor(clr);
		mapGraphics.draw(new Line2D.Double(x1,y1,x2,y2));
		
		placeStreetInCellList(w, x1, y1);
		
		nodesOnScreen.add(w.getStartPoint());
		nodesOnScreen.add(w.getEndPoint());
		
		
	}

	//this function draws a street without storing them in cells.
	private void drawStreetWithoutCache(Street w, Color clr, Double screenUpperLeftX, Double screenUpperLeftY, Double screenLowerRightX, Double screenLowerRightY)
	{
		Double initialx1 = w.getX1();
		Double initialy1 = w.getY1();
		Double initialx2 = w.getX2();
		Double initialy2 = w.getY2();
		
		
		// Given the bounds of the screen in "the world", scale the x and y values so their positions in this
		//image with fixed dimensions are proportional
		//to their position in the world view.
		Double screenWidth = Math.abs(screenUpperLeftX - screenLowerRightX);
		Double x1Ratio = Math.abs(initialx1 - screenUpperLeftX)/screenWidth;
		Double x1 = x1Ratio * map.getWidth();
		
		Double x2Ratio = Math.abs(initialx2 - screenUpperLeftX)/screenWidth;
		Double x2 = x2Ratio * map.getWidth();
		
		Double screenHeight = Math.abs(screenUpperLeftY - screenLowerRightY);
		Double y1Ratio = Math.abs(initialy1 - screenUpperLeftY)/screenHeight;
		Double y1 = y1Ratio * map.getHeight();
		
		Double y2Ratio = Math.abs(initialy2 - screenUpperLeftY)/screenHeight;
		Double y2 = y2Ratio * map.getHeight();
		
		if(clr.getRed() == 0 && clr.getBlue() == 0 && clr.getGreen() == 0) // if street is going to be painted black
		{
			//scale the red color depending on traffic
			Double redness = w.getTraffic();
			Double scale = (redness - 1)/4.0 * 255.0;
			clr = new Color((int)(scale*1.0), clr.getGreen(), clr.getBlue());
		}
		else //it's green
		{
			Double darkness = w.getTraffic();
			Double redBlueScale = 48.0 - (darkness - 1.0)/4.0 * 48.0;
			Double greenScale = 192.0 - (darkness - 1.0)/4.0* 92.0;
			clr = new Color((int)(redBlueScale * 1.0), (int)(greenScale * 1.0), (int)(redBlueScale * 1.0));
		}
		
		mapGraphics.setColor(clr);
		mapGraphics.draw(new Line2D.Double(x1,y1,x2,y2));
		
		nodesOnScreen.add(w.getStartPoint());
		nodesOnScreen.add(w.getEndPoint());
	}
	
	
	//the following methods do redrawing for move operations
	
	public Screen MoveUp(List<Street> streets, List<Street> path, Double upperLeftX, Double upperLeftY, Double lowerRightX, Double lowerRightY)
	{
		mapGraphics.clearRect(0, 0, map.getWidth(), map.getHeight());
		nodesOnScreen = new HashSet<Ndimensional>(400);
		
		cells.remove(7);
		cells.remove(8);
		cells.remove(9);
		
		cells.put(7, cells.get(4));
		cells.put(8, cells.get(5));
		cells.put(9, cells.get(6));
		
		cells.put(4, cells.get(1));
		cells.put(5, cells.get(2));
		cells.put(6, cells.get(3));
		
		cells.remove(1);
		cells.remove(2);
		cells.remove(3);
		
		
		for(Integer key : cells.keySet())
		{
			List<Street> list = cells.get(key);
			drawStreetsWithoutCache(list, Color.BLACK, upperLeftX, upperLeftY, lowerRightX, lowerRightY);
		}
		
		cells.put(1, new ArrayList<Street>());
		cells.put(2, new ArrayList<Street>());
		cells.put(3, new ArrayList<Street>());
		

		drawStreets(streets, Color.BLACK, upperLeftX, upperLeftY, lowerRightX, lowerRightY);

		drawStreetsWithoutCache(path, new Color(48,192,48), upperLeftX, upperLeftY, lowerRightX, lowerRightY);
			
		
		return new Screen(nodesOnScreen, map);//deepCopy(map));
	}
	
	public Screen MoveUp(List<Street> streets, Double upperLeftX, Double upperLeftY, Double lowerRightX, Double lowerRightY)
	{
		mapGraphics.clearRect(0, 0, map.getWidth(), map.getHeight());
		nodesOnScreen = new HashSet<Ndimensional>(400);
		
		cells.remove(7);
		cells.remove(8);
		cells.remove(9);
		
		cells.put(7, cells.get(4));
		cells.put(8, cells.get(5));
		cells.put(9, cells.get(6));
		
		cells.put(4, cells.get(1));
		cells.put(5, cells.get(2));
		cells.put(6, cells.get(3));
		
		cells.remove(1);
		cells.remove(2);
		cells.remove(3);
		
		for(Integer key : cells.keySet())
		{
			List<Street> list = cells.get(key);
			drawStreetsWithoutCache(list, Color.BLACK, upperLeftX, upperLeftY, lowerRightX, lowerRightY);
		}
		
		cells.put(1, new ArrayList<Street>());
		cells.put(2, new ArrayList<Street>());
		cells.put(3, new ArrayList<Street>());
		

		drawStreets(streets, Color.BLACK, upperLeftX, upperLeftY, lowerRightX, lowerRightY);

		return new Screen(nodesOnScreen, map);//deepCopy(map));
	}
	
	public Screen MoveDown(List<Street> streets, List<Street> path, Double upperLeftX, Double upperLeftY, Double lowerRightX, Double lowerRightY)
	{
		mapGraphics.clearRect(0, 0, map.getWidth(), map.getHeight());
		nodesOnScreen = new HashSet<Ndimensional>(400);
		
		cells.remove(1);
		cells.remove(2);
		cells.remove(3);
		
		cells.put(1, cells.get(4));
		cells.put(2, cells.get(5));
		cells.put(3, cells.get(6));
		
		cells.put(4, cells.get(7));
		cells.put(5, cells.get(8));
		cells.put(6, cells.get(9));
		
		cells.remove(7);
		cells.remove(8);
		cells.remove(9);
		
		for(Integer key : cells.keySet())
		{
			List<Street> list = cells.get(key);
			drawStreetsWithoutCache(list, Color.BLACK, upperLeftX, upperLeftY, lowerRightX, lowerRightY);
		}
		
		cells.put(7, new ArrayList<Street>());
		cells.put(8, new ArrayList<Street>());
		cells.put(9, new ArrayList<Street>());
		
		drawStreets(streets, Color.BLACK, upperLeftX, upperLeftY, lowerRightX, lowerRightY);

		drawStreetsWithoutCache(path, new Color((int)(50*.935),(int)(205*.935),(int)(50*.935)), upperLeftX, upperLeftY, lowerRightX, lowerRightY);
			
		return new Screen(nodesOnScreen, map);//deepCopy(map));
		
	}
	
	public Screen MoveDown(List<Street> streets, Double upperLeftX, Double upperLeftY, Double lowerRightX, Double lowerRightY)
	{
		mapGraphics.clearRect(0, 0, map.getWidth(), map.getHeight());
		nodesOnScreen = new HashSet<Ndimensional>(400);
		
		cells.remove(1);
		cells.remove(2);
		cells.remove(3);
		
		cells.put(1, cells.get(4));
		cells.put(2, cells.get(5));
		cells.put(3, cells.get(6));
		
		cells.put(4, cells.get(7));
		cells.put(5, cells.get(8));
		cells.put(6, cells.get(9));
		
		cells.remove(7);
		cells.remove(8);
		cells.remove(9);
		
		for(Integer key : cells.keySet())
		{
			List<Street> list = cells.get(key);
			drawStreetsWithoutCache(list, Color.BLACK, upperLeftX, upperLeftY, lowerRightX, lowerRightY);
		}
		
		cells.put(7, new ArrayList<Street>());
		cells.put(8, new ArrayList<Street>());
		cells.put(9, new ArrayList<Street>());
		
		drawStreets(streets, Color.BLACK, upperLeftX, upperLeftY, lowerRightX, lowerRightY);

		
		return new Screen(nodesOnScreen, map);//deepCopy(map));
	}
	
	public Screen MoveLeft(List<Street> streets, List<Street> path, Double upperLeftX, Double upperLeftY, Double lowerRightX, Double lowerRightY)
	{
		
		mapGraphics.clearRect(0, 0, map.getWidth(), map.getHeight());
		nodesOnScreen = new HashSet<Ndimensional>(400);
		
		cells.remove(3);
		cells.remove(6);
		cells.remove(9);
		
		cells.put(3, cells.get(2));
		cells.put(6, cells.get(5));
		cells.put(9, cells.get(8));
		
		cells.put(2, cells.get(1));
		cells.put(5, cells.get(4));
		cells.put(8, cells.get(7));
		
		cells.remove(1);
		cells.remove(4);
		cells.remove(7);

		for(Integer key : cells.keySet())
		{
			List<Street> list = cells.get(key);
			drawStreetsWithoutCache(list, Color.BLACK, upperLeftX, upperLeftY, lowerRightX, lowerRightY);
		}
		
		cells.put(1, new ArrayList<Street>());
		cells.put(4, new ArrayList<Street>());
		cells.put(7, new ArrayList<Street>());
		
		drawStreets(streets, Color.BLACK, upperLeftX, upperLeftY, lowerRightX, lowerRightY);

		drawStreetsWithoutCache(path, new Color((int)(50*.935),(int)(205*.935),(int)(50*.935)), upperLeftX, upperLeftY, lowerRightX, lowerRightY);
			
		
		return new Screen(nodesOnScreen, map);//deepCopy(map));
		
		
		
	}
	
	public Screen MoveLeft(List<Street> streets, Double upperLeftX, Double upperLeftY, Double lowerRightX, Double lowerRightY)
	{
		
		mapGraphics.clearRect(0, 0, map.getWidth(), map.getHeight());
		nodesOnScreen = new HashSet<Ndimensional>(400);
		
		cells.remove(3);
		cells.remove(6);
		cells.remove(9);
		
		cells.put(3, cells.get(2));
		cells.put(6, cells.get(5));
		cells.put(9, cells.get(8));
		
		cells.put(2, cells.get(1));
		cells.put(5, cells.get(4));
		cells.put(8, cells.get(7));
		
		cells.remove(1);
		cells.remove(4);
		cells.remove(7);
		
		for(Integer key : cells.keySet())
		{
			List<Street> list = cells.get(key);
			drawStreetsWithoutCache(list, Color.BLACK, upperLeftX, upperLeftY, lowerRightX, lowerRightY);
		}
		
		cells.put(1, new ArrayList<Street>());
		cells.put(4, new ArrayList<Street>());
		cells.put(7, new ArrayList<Street>());
		
		drawStreets(streets, Color.BLACK, upperLeftX, upperLeftY, lowerRightX, lowerRightY);
		
		return new Screen(nodesOnScreen, map);//deepCopy(map));
				
	}
	
	public Screen MoveRight(List<Street> streets, List<Street> path, Double upperLeftX, Double upperLeftY, Double lowerRightX, Double lowerRightY)
	{
		mapGraphics.clearRect(0, 0, map.getWidth(), map.getHeight());
		nodesOnScreen = new HashSet<Ndimensional>(400);
		
		cells.remove(1);
		cells.remove(4);
		cells.remove(7);
		
		cells.put(1, cells.get(2));
		cells.put(4, cells.get(5));
		cells.put(7, cells.get(8));
		
		cells.put(2, cells.get(3));
		cells.put(5, cells.get(6));
		cells.put(8, cells.get(9));
		
		cells.remove(3);
		cells.remove(6);
		cells.remove(9);
		
		for(Integer key : cells.keySet())
		{
			List<Street> list = cells.get(key);
			drawStreetsWithoutCache(list, Color.BLACK, upperLeftX, upperLeftY, lowerRightX, lowerRightY);
		}
		
		cells.put(3, new ArrayList<Street>());
		cells.put(6, new ArrayList<Street>());
		cells.put(9, new ArrayList<Street>());
		
		drawStreets(streets, Color.BLACK, upperLeftX, upperLeftY, lowerRightX, lowerRightY);

		drawStreetsWithoutCache(path, new Color((int)(50*.935),(int)(205*.935),(int)(50*.935)), upperLeftX, upperLeftY, lowerRightX, lowerRightY);
			
		
		
		return new Screen(nodesOnScreen, map);//deepCopy(map));
		
	}
	
	//This method does redrawing that is required when moving right
	public Screen MoveRight(List<Street> streets, Double upperLeftX, Double upperLeftY, Double lowerRightX, Double lowerRightY)
	{
		mapGraphics.clearRect(0, 0, map.getWidth(), map.getHeight());
		nodesOnScreen = new HashSet<Ndimensional>(400);
		
		cells.remove(1);
		cells.remove(4);
		cells.remove(7);
		
		cells.put(1, cells.get(2));
		cells.put(4, cells.get(5));
		cells.put(7, cells.get(8));
		
		cells.put(2, cells.get(3));
		cells.put(5, cells.get(6));
		cells.put(8, cells.get(9));
		
		cells.remove(3);
		cells.remove(6);
		cells.remove(9);
		
		for(Integer key : cells.keySet())
		{
			List<Street> list = cells.get(key);
			drawStreetsWithoutCache(list, Color.BLACK, upperLeftX, upperLeftY, lowerRightX, lowerRightY);
		}
		
		cells.put(3, new ArrayList<Street>());
		cells.put(6, new ArrayList<Street>());
		cells.put(9, new ArrayList<Street>());
		
		drawStreets(streets, Color.BLACK, upperLeftX, upperLeftY, lowerRightX, lowerRightY);
		
		return new Screen(nodesOnScreen, map);//deepCopy(map));
	}
	
	//this method determines where a street should be placed in the 9 cells
	private void placeStreetInCellList(Street s, Double x1, Double y1)
	{
		//first row
		if(x1 <= map.getWidth()/3 && y1 <= map.getHeight()/3)
		{
			ArrayList<Street> cell = cells.get(1);
			cell.add(s);
			cells.put(1, cell);
		}
		
		if(x1 >= map.getWidth()/3 && y1 <= map.getHeight()/3 && x1 <= map.getWidth()*2/3)
		{
			ArrayList<Street> cell = cells.get(2);
			cell.add(s);
			cells.put(2, cell);
		}
		
		if(x1 >= map.getWidth()*2/3 && y1 <= map.getHeight()/3)
		{
			ArrayList<Street> cell = cells.get(3);
			cell.add(s);
			cells.put(3, cell);
		}
		
		
		//second row
		if(x1 <= map.getWidth()*1/3 && y1 >= map.getHeight()/3 && y1 <= map.getHeight()*2/3)
		{
			ArrayList<Street> cell = cells.get(4);
			cell.add(s);
			cells.put(4, cell);
		}
		
		if(x1 >= map.getWidth()*1/3 && x1 <= map.getWidth()*2/3 && y1 >= map.getHeight()*1/3 && y1 <= map.getHeight()*2/3)
		{
			ArrayList<Street> cell = cells.get(5);
			cell.add(s);
			cells.put(5, cell);
		}
		
		if(x1 >= map.getWidth()*2/3 && y1 >= map.getHeight()/3 && y1 <= map.getHeight()*2/3)
		{
			ArrayList<Street> cell = cells.get(3);
			cell.add(s);
			cells.put(3, cell);
		}
		
		
		
		//third row
		
		if(x1 <= map.getWidth()/3 && y1 >= map.getHeight()*2/3)
		{
			ArrayList<Street> cell = cells.get(7);
			cell.add(s);
			cells.put(7, cell);
		}
		
		if(x1 >= map.getWidth()/3 && y1 >= map.getHeight()*2/3 && x1 <= map.getWidth()*2/3)
		{
			ArrayList<Street> cell = cells.get(8);
			cell.add(s);
			cells.put(8, cell);
		}
		
		if(x1 >= map.getWidth()*2/3 && y1 >= map.getHeight()*2/3)
		{
			ArrayList<Street> cell = cells.get(9);
			cell.add(s);
			cells.put(9, cell);
		}
		
	}
	
	public BufferedImage markIntersection(Double x, Double y, Double upperLeftX, Double upperLeftY, Double lowerRightX, Double lowerRightY)
	{
		Double screenWidth = Math.abs(upperLeftX - lowerRightX);
		Double x1Ratio = Math.abs(x - upperLeftX)/screenWidth;
		Double x1 = x1Ratio * map.getWidth();
		
		Double screenHeight = Math.abs(upperLeftY - lowerRightY);
		Double y1Ratio = Math.abs(y - upperLeftY)/screenHeight;
		Double y1 = y1Ratio * map.getHeight();
		
		mapGraphics.draw(new Ellipse2D.Double(x1 - 20.0, y1 - 20.0, 20.0, 20.0));
		
		return map;//deepCopy(map);
	}
	
	public BufferedImage eraseIntersections(Double upperLeftX, Double upperLeftY, Double lowerRightX, Double lowerRightY)
	{
		mapGraphics.clearRect(0, 0, map.getWidth(), map.getHeight());
		nodesOnScreen = new HashSet<Ndimensional>(400);
		
		for(Integer key : cells.keySet())
		{
			drawStreetsWithoutCache(cells.get(key), Color.BLACK, upperLeftX, upperLeftY, lowerRightX, lowerRightY);
		}
		
		return map;//deepCopy(map);
		
	}
	
	public BufferedImage deepCopy(BufferedImage img) 
	{
		 ColorModel cm = img.getColorModel();
		 boolean isAlphaPremultiplied = img.isAlphaPremultiplied();
		 WritableRaster raster = img.copyData(null);
		 return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
	
	public KDPoint imagePointToKDPoint(Double x, Double y,Double upperLeftX, 
                                                  Double upperLeftY, Double lowerRightX, Double lowerRightY)
	{
		Double screenWidth = Math.abs(upperLeftX - lowerRightX);
		Double x1Ratio = Math.abs(x/map.getWidth());
		Double x1 = x1Ratio * screenWidth + upperLeftX;
		
		Double screenHeight = Math.abs(upperLeftY - lowerRightY);
		Double y1Ratio = Math.abs(y/map.getHeight());
		Double y1 = y1Ratio * screenHeight + upperLeftY;	
		
		return new KDPoint(x1,y1);
	}
	
	public void setDimensions(int width, int height)
	{
		map = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		mapGraphics = map.createGraphics();
	}
	
}
