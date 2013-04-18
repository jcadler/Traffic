package edu.brown.cs32.jcadler.trie;

/**
 * represents the number of times, num, the word wrd has appeared after the
 * word stored in the overarching Word object
 * @author john
 */
public class WordInt implements Comparable<WordInt>
{
    private String wrd;
    private int num;
    
    /**
     * Creates a WordInt with wrd value s and num value n
     * @param s
     * @param n 
     */
    public WordInt(String s, int n)
    {
        wrd=s;
        num=n;
    }
    
    public String getWord()
    {
        return wrd;
    }
    
    public int getNum()
    {
        return num;
    }
    
    public boolean equals(Object o)
    {
        if(o instanceof WordInt)
            return equals((WordInt)o);
        return false;
    }
    
    public boolean equals(WordInt wi)
    {
        return wrd.equals(wi.getWord()) && num==wi.getNum();
    }
    
    public int compareTo(WordInt wi)
    {
        if(wi.getNum()==num)
        {
            return wrd.compareTo(wi.getWord());
        }
        else if (wi.getNum()>num)
            return -1;
        else
            return 1;
    }
}
