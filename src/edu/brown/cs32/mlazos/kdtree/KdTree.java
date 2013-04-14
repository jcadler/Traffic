package edu.brown.cs32.mlazos.kdtree;

import java.util.List;
import java.util.PriorityQueue;

import com.google.common.collect.MinMaxPriorityQueue;

/**
 * 
 * @author mlazos
 *
 * @param <T>
 *
 *A Kd-Tree stores three dimensional points in partitioned three dimensional subspaces.
 *The getQueueOfNeighborsInRadius method returns a priority queue containing all neighbors to a point in a 
 *specific radius around a given point.  The radius must be a non-negative integer.
 *
 *The getQueueOfArbitraryNumberOfNeighbors method returns a minMaxPriorityQueue consisting of a specified number
 *of the closest neighbors to a point.
 *
 */
public interface KdTree<T extends Ndimensional>
{
	public boolean isNode();
	public int getSplitDimension();
	public double getSplitValue();
	public KdTree<T> getLeft();
	public KdTree<T> getRight();
	public PriorityQueue<T> getQueueOfNeighborsInRadius(double radius, T point);
	public MinMaxPriorityQueue<T> getQueueOfArbitraryNumberOfNeighbors(int number, T point);
	public List<T> getPointList();	
}
