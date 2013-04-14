package edu.brown.cs32.mlazos.kdtree;

import java.util.Comparator;

public class CompareDim<T extends Ndimensional> implements Comparator<T> 
{
	private int dim;
	
	public CompareDim(int d)
	{
		dim = d;
	}
	
	public int compare(T tD1, T tD2)
	{
		if(tD1.getDimension(dim) < tD2.getDimension(dim))
		{
			return -1;
		}
		else
		{
			if(tD1.getDimension(dim) == tD2.getDimension(dim))
			{
				return 0;
			}
			else
			{
				return 1;
			}
		}
	}
}
