package edu.brown.cs32.jcadler.trie;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
        
/**
 *
 * @author john
 */
public class Word implements TrieVal
{
    private HashMap<String,Integer> wrdMap;
    private List<String> wrds;
    private String wrd;
    
    public Word(String w)
    {
        wrdMap=new HashMap<>();
        wrds=new ArrayList<>();
        wrd=w;
    }
    
    public String getWord()
    {
        return wrd;
    }
    
    public boolean isWord()
    {
        return true;
    }
    
    public String getChar()
    {
        throw new Error("This is not a letter");
    }
    
    public List<WordInt> getWordNums()
    {
        List<WordInt> ret = new ArrayList<>();
        for(String s : wrds)
            ret.add(new WordInt(s,wrdMap.get(s)));
        return ret;
    }
    
    public List<WordInt> getWordNums(List<String> l)
    {
        List<WordInt> ret = new ArrayList<>();
        for(String s : l)
        {
            if(!wrds.contains(s))
                ret.add(new WordInt(s,0));
            else
                ret.add(new WordInt(s,wrdMap.get(s)));
        }
        return ret;
    }
    
    public void addWord(String s)
    {
        if(!wrds.contains(s))
        {
            wrds.add(s);
            wrdMap.put(s,1);
        }
        else
        {
            wrdMap.put(s,wrdMap.get(s)+1);
        }
    }
}
