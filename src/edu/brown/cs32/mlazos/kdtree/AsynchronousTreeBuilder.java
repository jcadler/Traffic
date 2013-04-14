package edu.brown.cs32.mlazos.kdtree;

import java.util.List;
import java.util.concurrent.Callable;

public class AsynchronousTreeBuilder<T extends Ndimensional> implements Callable<KdTree<T>> 
{
	private List<T> points;
	
	public AsynchronousTreeBuilder(List<T> pointList)
	{
		points = pointList;
	}
	
	@Override
	public KdTree<T> call() throws Exception 
	{
		return KDNode.buildTree(points, 0);
	}
}
