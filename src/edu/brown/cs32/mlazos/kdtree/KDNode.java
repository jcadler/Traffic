package edu.brown.cs32.mlazos.kdtree;


import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import com.google.common.collect.MinMaxPriorityQueue;

/**
 * 
 * @author mlazos
 *
 * @param <T>
 *
 *A node contains a split axis, a split value, and a left and right sub-tree. 
 *Nodes do not have stored points.
 */
public class KDNode<T extends Ndimensional> implements KdTree<T> 
{
	private int splitDim_; // Split axis is the axis that divides the Node's children
	private double splitValue_; // Split value is the value that divides the Node's children
	private KdTree<T> left_; // The Left child of the Node
	private KdTree<T> right_; // The Right child of the Node
		
	public KDNode(){};
	
	@Override
	public boolean isNode()
	{
		return true;
	}
	
	@Override
	public int getSplitDimension()
	{
		return splitDim_;
	}
	
	
	private void setSplitDimension(int d)
	{
		splitDim_ = d;
	}
	
	@Override
	public double getSplitValue()
	{
		return splitValue_;
	}
	
	
	private void setSplitValue(double v)
	{
		splitValue_ = v;
	}
	
	@Override
	public KdTree<T> getLeft()
	{
		return left_;
	}
	
	
	private void setLeft(KdTree<T> t)
	{
		left_ = t;
	}
	
	@Override
	public KdTree<T> getRight()
	{
		return right_;
	}
	

	private void setRight(KdTree<T> t)
	{
		right_ = t;
	}
	
	@Override
	public List<T> getPointList()
	{
		throw new Error("Nodes have no stored points.");
	}
	
	//this method builds a kd tree from a list of data
	public static <X extends Ndimensional> KdTree<X> buildTree(List<X> pointList, int depth)
	{
		int size = pointList.size();
		
		if(size <= 100) //return a terminating leaf node
		{
			//System.out.println(pointList.get(0).toString());
			return new Leaf<X>(pointList);
		}
		else  // build new node
		{
			List<X> leftList;
			List<X> rightList;
			int halfLength;
			double splitValue;
			
			if(size % 2 == 0) // if the list is of even size, split list evenly
			{
				halfLength = size/2 - 1; // split length of list, and reindex to match the array index
			}
			else // else allow the right list to be one point larger 
			{
				halfLength = (int)Math.floor(size/2);				
			}
			
			CompareDim<X> comp = new CompareDim<X>(depth);
			
			Collections.sort(pointList, comp);
			
			leftList = pointList.subList(0, halfLength + 1); // the first argument is inclusive, while the second is exclusive 
			rightList = pointList.subList(halfLength + 1, size); 
			
			splitValue = rightList.get(0).getDimension(depth);
			
			KDNode<X> currNode = new KDNode<X>();	
			
			currNode.setSplitDimension(depth); 
			currNode.setSplitValue(splitValue);
			currNode.setLeft(KDNode.buildTree(leftList, depth + 1));
			currNode.setRight(KDNode.buildTree(rightList, depth + 1));
			
	
			return currNode;
			
		}
	}
	
	//this function returns all points in the tree within a radius to the point p
	//r must be greater than zero
	public PriorityQueue<T> getQueueOfNeighborsInRadius(double r, T p)
	{
		CompareDist<T> distComparator = new CompareDist<T>(p); // compares distance to p
		PriorityQueue<T> qOfNeighbors = new PriorityQueue<T>(100, distComparator);	//constructs priority queue based on distance to p	

				
		if(p.getDimension(splitDim_) < splitValue_)
		{
			qOfNeighbors = left_.getQueueOfNeighborsInRadius(r, p);
			
			if(p.getDimension(splitDim_) + r >= splitValue_)
			{
				qOfNeighbors.addAll(right_.getQueueOfNeighborsInRadius(r,p));
			}
		}
		else
		{
			qOfNeighbors = right_.getQueueOfNeighborsInRadius(r, p);
			
			if(p.getDimension(splitDim_) - r < splitValue_)
			{
				qOfNeighbors.addAll(left_.getQueueOfNeighborsInRadius(r, p));
			}	
		}
		
		return  qOfNeighbors;
	}
	
	//this function returns a min max priority queue containing the nearest neighbors
	//num must be greater than zero.
	public MinMaxPriorityQueue<T> getQueueOfArbitraryNumberOfNeighbors(int num, T p)
	{
		CompareDist<T> distComparator = new CompareDist<T>(p);
		MinMaxPriorityQueue<T> qOfNeighbors;
		T maxPoint;
		double maxDist;
		

		
		if(p.getDimension(splitDim_) < splitValue_)
		{
			qOfNeighbors = left_.getQueueOfArbitraryNumberOfNeighbors(num, p);
			
			maxPoint = qOfNeighbors.peekLast(); // get max distance in the queue to see if there could possibly be closer points in the tree
			
			maxDist = Math.sqrt(distComparator.computeDist(maxPoint, p)); // the output distance is squared
			
			if(p.getDimension(splitDim_) + maxDist >= splitValue_ )
			{
				qOfNeighbors.addAll(right_.getQueueOfArbitraryNumberOfNeighbors(num, p));
			}
		}
		else
		{
			qOfNeighbors = right_.getQueueOfArbitraryNumberOfNeighbors(num, p);
			
			maxPoint = qOfNeighbors.peekLast(); // get max distance in the queue to see if there could possibly be closer points in the tree
			
			maxDist = Math.sqrt(distComparator.computeDist(maxPoint, p)); // the output distance is squared
			
			if(p.getDimension(splitDim_) - maxDist < splitValue_)
			{
				qOfNeighbors.addAll(left_.getQueueOfArbitraryNumberOfNeighbors(num, p));
			}	
		} 
		
		return  qOfNeighbors;
	}
	
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object obj)
	{

		if(getClass().equals(obj.getClass()))
		{
			if(splitValue_ == ((KdTree<T>) obj).getSplitValue())
			{
				if(splitDim_ == ((KdTree<T>) obj).getSplitDimension())
				{
					if(left_.equals(((KdTree<T>) obj).getLeft()))
					{
						if(right_.equals(((KdTree<T>) obj).getRight()))
						{
							return true;
						}
					}
				}
			}
		}
		

		
		return false;
	}

}
