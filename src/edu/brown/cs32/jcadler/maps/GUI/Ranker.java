package edu.brown.cs32.jcadler.maps.GUI;

/**
 * Used to encapsulate the two ranking types (smart vs. spec defined), such that 
 * either can be used when running autocorrect
 * @author john
 */
public abstract class Ranker<T extends Ranker<T>> implements Comparable<T>
{
    /**
     * used for sorting
     * @param t
     * @return 
     */
    public abstract int compareTo(T t);
    
    /**
     * gets the word associated with the current object
     * @return 
     */
    public abstract String getWord();
}
