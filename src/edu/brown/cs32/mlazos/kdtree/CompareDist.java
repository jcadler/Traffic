package edu.brown.cs32.mlazos.kdtree;

import java.math.BigDecimal;
import java.util.Comparator;

/**
 * 
 * @author mlazos
 *
 * @param <T>
 * 
 * This comparator will compare two three dimensional objects by their distance to a set third object.
 * The comparison point can be set with the constructor, and changed with the setComparison point method.
 */
public class CompareDist<T extends Ndimensional> implements Comparator<T> 
{
	//This comparator will compare two three dimensional data pieces by 
	//their distance to a set third data piece
	private T point_;
	
	public CompareDist(T p)
	{
		point_ = p;
	}
	
	public void setComparisonPoint(T p)
	{
		point_ = p;
	}
	
	public T getComparisonPoint()
	{
		return point_;
	}
	
	public int compare(T p1, T p2)
	{
		double dist1 = computeDist(p1, point_);
		double dist2 = computeDist(p2, point_);
		
		if(dist1 < dist2)
		{
			return -1;
		}
		else
		{
			if(dist1 == dist2)
			{
				return 0;
			}
			else
			{
				return 1;
			}
		}
	}
	
	public double computeDist(T p1, T p2)
	{
		BigDecimal squaredDistBD = BigDecimal.ZERO;
		
		BigDecimal x1;
		BigDecimal x2;
		BigDecimal x1x2diff;
		
		for(int i = 0; i < p1.getNumDimensions(); i++)
		{
			x1 = BigDecimal.valueOf(p1.getDimension(i));
			x2 = BigDecimal.valueOf(p2.getDimension(i));
			
			
			x1x2diff = (x1.subtract(x2));
			
			squaredDistBD = squaredDistBD.add(x1x2diff.multiply(x1x2diff));
										 
		}
		
		return  squaredDistBD.doubleValue();
	}
	
	
}
