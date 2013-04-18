package edu.brown.cs32.mlazosjcadler.drawer;

import java.awt.image.BufferedImage;
import java.util.List;
import edu.brown.cs32.jcadler.maps.KDPoint;

/**
 * 
 * @author mlazos
 *A drawer generates map from a list of streets, and can optionally generate a map with streets to be highlighted
 */
public interface Drawer 
{
	public Screen generateMap(List<Street> streets, Double upperLeftX, Double upperLeftY, Double lowerRightX, Double lowerRightY);
	public Screen generateMap(List<Street> streets, List<Street> path, Double upperLeftX, Double upperLeftY, Double lowerRightX, Double lowerRightY);
        public BufferedImage markIntersection(Double x, Double y, 
                                              Double upperLeftX, Double upperLeftY, 
                                              Double lowerRightX, Double lowerRightY);
        public BufferedImage eraseIntersections(Double upperLeftX, Double upperLeftY, 
                                                Double lowerRightX, Double lowerRightY);
    public KDPoint imagePointToKDPoint(Double x, Double y,Double upperLeftX, 
                                       Double upperLeftY, Double lowerRightX, Double lowerRightY);
}
