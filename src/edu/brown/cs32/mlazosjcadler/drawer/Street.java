package edu.brown.cs32.mlazosjcadler.drawer;

import edu.brown.cs32.mlazos.kdtree.Ndimensional;

public interface Street 
{
	public String getStreetName();
	public Double getX1();
	public Double getY1();
	public Double getX2();
	public Double getY2();
	public Double getTraffic();
	public Boolean awayFromX1Y1(); // this is the direction of the way
	public Ndimensional getStartPoint();
	public Ndimensional getEndPoint();
}
