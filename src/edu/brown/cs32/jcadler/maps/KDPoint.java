package edu.brown.cs32.jcadler.maps;

import edu.brown.cs32.mlazos.kdtree.Ndimensional;

/**
 *
 * @author john
 */
public class KDPoint implements Ndimensional
{
    private double x;
    private double y;
    
    public KDPoint(double ex, double yee)
    {
        x=ex;
        y=yee;
    }
    
    public String getID()
    {
        return "this doesn't matter";
    }
    
    public Double getDimension(int dim)
    {
        int n = dim%2;
        if(n==0)
            return x;
        if(n==1)
            return y;
        else
            throw new Error("This isn't mathematically possible");
    }
    
    public int getNumDimensions()
    {
        return 2;
    }
}
