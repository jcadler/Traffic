package edu.brown.cs32.jcadler.maps.GUI;

/**
 * Produces a Ranker object depending on whether smart ranking is desired
 * @author john
 */
public class RankFactory 
{
    public static Ranker<?> createRanker(String wrd, String input, int prob, 
                                         int freq, boolean best, boolean smart)
    {
        if(smart)
            return new Smart(wrd,input,freq);
        return new WordPos(wrd,prob,freq,best);
    }
}
