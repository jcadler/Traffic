package edu.brown.cs32.mlazosjcadler.drawer;

import edu.brown.cs32.mlazos.kdtree.Ndimensional;

public class StreetStub implements Street 
{

	private Double width;
	private Double height;
	private Double x1;
	private Double y1;
	private Double x2;
	private Double y2;
	private Boolean away;
	
	public StreetStub(Double upperCornerBound, Double w, Double h)
	{
		width = w;
		height = h;
		x1 = upperCornerBound + Math.random()* (width - upperCornerBound - 1);
		y1 = upperCornerBound + Math.random() * (height - upperCornerBound - 1);
		x2 = upperCornerBound + Math.random() * (width - upperCornerBound - 1);
		y2 = upperCornerBound + Math.random() * (height - upperCornerBound - 1);
		away = Math.random() >= .5;
	}
	
	public StreetStub(Double x, Double y)
	{
		x1 = 0.0;
		y1 = 0.0;
		x2 = x;
		y2 = y;
		away = true;
	}
	
	@Override
	public String getStreetName() 
	{
		return new String("Fake Street Drive");
	}

	@Override
	public Double getX1() 
	{
		return x1;
	}

	@Override
	public Double getY1() 
	{
		return y1;
	}

	@Override
	public Double getX2() 
	{
		return x2;
	}

	@Override
	public Double getY2() 
	{
		return y2; 
	}
	
	@Override
	public Boolean awayFromX1Y1()
	{
		return away;
	}

	@Override
	public Ndimensional getStartPoint()
	{
		return null;
	}
	
	@Override
	public Ndimensional getEndPoint()
	{
		return null;
	}
        
        @Override
        public Double getTraffic()
        {
            return 1.0;
        }
}
