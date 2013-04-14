package edu.brown.cs32.mlazos.kdtree;

import java.util.List;
import java.util.PriorityQueue;

import com.google.common.collect.MinMaxPriorityQueue;

/**
 * 
 * @author mlazos
 *
 * @param <T>
 *A leaf stores points, and does not have children.  
 *It is the terminating node at the bottom of the Kd-Tree.
 */

public class Leaf<T extends Ndimensional> implements KdTree<T> 
{
	// a leaf is a terminal node that stores a list of points.
	private List<T> pointList_;
	
	public Leaf(List<T> pList)
	{
		pointList_ = pList;
	}
	
	public boolean isNode()
	{
		return false;
	}
	
	@Override
	public int getSplitDimension()
	{
		throw new Error("Leaf node has no split axis to get.");
	}
	
	@Override
	public double getSplitValue()
	{
		throw new Error("Leaf node has no split value to get.");
	}
	
	@Override
	public KdTree<T> getLeft()
	{
		throw new Error("Leaf node has no left child to get.");
	}

	@Override
	public KdTree<T> getRight()
	{
		throw new Error("Leaf node has no right child to get.");
	}
	
	@Override
	public PriorityQueue<T> getQueueOfNeighborsInRadius(double r, T p)
	{
		CompareDist<T> distComparator = new CompareDist<T>(p);
		PriorityQueue<T> qOfNeighborsInRadius = new PriorityQueue<T>(100, distComparator);
		
		for(T point : pointList_)
		{
			if(point.getID() != p.getID()) // if the parameter p is the current point being added, don't include it.
			{
				if(distComparator.computeDist(point, p) <= r*r) // square the distance, since no square roots are taken in the comparator
				{
					qOfNeighborsInRadius.add(point);
				}
			}
		}
		
		return qOfNeighborsInRadius;
	}
	
	@Override
	public MinMaxPriorityQueue<T> getQueueOfArbitraryNumberOfNeighbors(int num, T p)
	{
		CompareDist<T> distComparator = new CompareDist<T>(p);
		MinMaxPriorityQueue.Builder<T> builder = MinMaxPriorityQueue.orderedBy(distComparator);
		if(num == 0) // if num is zero, return an empty queue, because a queue of zero size cannot be created.
		{		
			builder = builder.maximumSize(1); 
			MinMaxPriorityQueue<T> qOfNeighbors = builder.create();
			
			return qOfNeighbors;
		}

		builder = builder.maximumSize(num); 
		MinMaxPriorityQueue<T> qOfNeighbors = builder.create();	
		
		for(T point : pointList_)
		{
			if(point.getID() != p.getID()) // if the point being added is the point that was passed as a parameter, don't include it.
			{
				qOfNeighbors.add(point);
			}
		}
		
		return qOfNeighbors;
	}

	@Override
	public List<T> getPointList()
	{
		return pointList_;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object obj)
	{
		if(getClass().equals(obj.getClass()))
		{		
			if(pointList_.equals(((KdTree<T>) obj).getPointList()))
			{
				return true;	
			}
		}
		return false;
	}

	
}
