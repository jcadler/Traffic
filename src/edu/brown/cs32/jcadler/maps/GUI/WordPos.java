package edu.brown.cs32.jcadler.maps.GUI;

/**
 *
 * @author john
 */
public class WordPos extends Ranker<WordPos>
{
    private String wrd;
    private int prob;
    private int count;
    private boolean best;
    
    public WordPos(String w,int p,int c,boolean b)
    {
        wrd=w;
        prob=p;
        count=c;
        best=b;
    }
    
    public int compareTo(WordPos wp)
    {
        if(best)
            return -1;
        if(wp.getBest())
            return 1;
        if(prob!=0 || wp.getProb()!=0)
        {
            if(prob<wp.getProb())
                return 1;
            else if(prob>wp.getProb())
                return -1;
            else
                return noProb(wp);
        }
        return noProb(wp);
    }
    
    private int noProb(WordPos wp)
    {
        if(count>wp.getCount())
            return -1;
        else if(count<wp.getCount())
            return 1;
        else
            return wrd.compareTo(wp.getWord());
    }
    
    public String getWord()
    {
        return wrd;
    }
    
    public int getProb()
    {
        return prob;
    }
    
    public int getCount()
    {
        return count;
    }
    
    public boolean getBest()
    {
        return best;
    }
    
}