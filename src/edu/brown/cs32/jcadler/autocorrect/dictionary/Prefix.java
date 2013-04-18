package edu.brown.cs32.jcadler.autocorrect.dictionary;

import edu.brown.cs32.jcadler.autocorrect.dictionary.Dictionary;
import java.util.List;
import java.util.ArrayList;
import edu.brown.cs32.jcadler.trie.TrieNode;

/**
 * Used to find all prefixes related to a supplied word, using the supplied
 * dictionary
 * @author john
 */
public class Prefix implements Analyzer
{
    private Dictionary dict;
    public Prefix(Dictionary d)
    {
        dict=d;
    }
    
    /**
     * returns all possible word which have the string s as their prefix
     * @param s
     * @return 
     */
    public List<String> analyze(String s)
    {
        List<String> collect = new ArrayList<>();
        List<String> ret = new ArrayList<>();
        if(dict.contains(s))
        {
            List<TrieNode> wrd=dict.getNodes(s);
            if(wrd.size()!=s.length())
                return ret;
            for(String str : getAllWords(wrd.get(wrd.size()-1)))
            {
                collect.add(s+str.substring(1));
            }
        }
        for(String str : collect)
        {
            if(!ret.contains(str))
                ret.add(str);
        }
        return ret;
    }
    
    /**
     * gets all words which are below the supplied node
     * @param tn
     * @return 
     */
    protected List<String> getAllWords(TrieNode tn)
    {
        List<String> ret = new ArrayList<>();
        for(TrieNode n : tn.getKids())
        {
            if(n.getData().isWord())
                ret.add(tn.getData().getChar());
            else
            {
                List<String> suffixes = getAllWords(n);
                for(String s : suffixes)
                    ret.add(tn.getData().getChar() + s);
            }
        }
        return ret;
    }
}
