package edu.brown.cs32.jcadler.autocorrect.dictionary;

import edu.brown.cs32.jcadler.autocorrect.dictionary.Dictionary;
import java.util.List;
import java.util.ArrayList;

/**
 * Used to find strings which are the result of mergin two words using the
 * supplied dictionary
 * @author john
 */
public class Whitespace implements Analyzer
{
    
    private Dictionary dict;
    
    public Whitespace(Dictionary d)
    {
        dict=d;
    }
    
    public List<String> analyze(String s)
    {
        List<String> ret = new ArrayList<>();
        for(int i=1;i<s.length()-1;i++)
        {
            if(dict.containsWord(s.substring(0,i)) && dict.containsWord(s.substring(i)))
                ret.add(s.substring(0,i)+" "+s.substring(i));
        }
        return ret;
    }
    
}