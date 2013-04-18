package edu.brown.cs32.jcadler.maps.GUI;

/**
 *
 * @author john
 */
public class Smart extends Ranker<Smart>
{
    String wrd;
    String input;
    int frequency;
    
    public Smart(String w, String in, int f)
    {
        wrd=w;
        input=in;
        frequency=f;
    }
    
    public int compareTo(Smart s)
    {
        if(wrd.contains(" "))
            return -1;
        else if(s.getWord().contains(" "))
            return 1;
        if(wrd.contains(input) && s.getWord().contains(input))
            return wrd.length()-s.getWord().length();
        else if(wrd.contains(input))
            return -1;
        else if(s.getWord().contains(input))
            return 1;
        else
        {
            if(frequency<s.getFreq())
                return -1;
            else if(frequency==s.getFreq())
                return 0;
            else
                return 1;
        }
    }
    
    public String getWord()
    {
        return wrd;
    }
    
    public int getFreq()
    {
        return frequency;
    }
}
