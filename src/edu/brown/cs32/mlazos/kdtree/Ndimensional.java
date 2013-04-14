package edu.brown.cs32.mlazos.kdtree;

//This interface is to ensure that the data given to the tree is three dimensional

/**
 * 
 * @author mlazos
 *
 *This interface requires objects to have functions that return dimensions and an ID.
 */
public interface Ndimensional 
{
	public String getID();
	public Double getDimension(int dim);
	public int getNumDimensions(); 
}
