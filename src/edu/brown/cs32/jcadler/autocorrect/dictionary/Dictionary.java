package edu.brown.cs32.jcadler.autocorrect.dictionary;

import edu.brown.cs32.jcadler.trie.TrieNode;
import edu.brown.cs32.jcadler.trie.WordInt;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * Storage class for the TrieNodes which make up the full trie.
 * All methods (unless otherwise noted) simply pass on the function
 * call to the requisite TrieNode object (nodes are sorted according
 * to the letter of the alphabet to which the root node corresponds). In
 * addition, the Dictionary keeps track of the net frequency of each word 
 * in the corpus
 * @author John Adler
 */
public class Dictionary 
{
    private TrieNode[] dict;
    private HashMap<String,Integer> count;
    
    /**
     * Creates a new dictionary with all nodes initialized to null
     */
    public Dictionary()
    {
        dict = new TrieNode[26];
        for(int i=0;i<26;i++)
            dict[i]=null;
        count=new HashMap<>();
    }
    
    /**
     * returns the count corresponding to the given string, 0 if the string
     * has not been counted
     * @param s
     * @return 
     */
    public int getCount(String s)
    {
        if(count.containsKey(s))
            return count.get(s);
        else
            return 0;
    }
    
    /**
     * inrements the word counter, and then inserts the word into the corresponding
     * trie
     * @param wrd 
     */
    public void insertWord(String wrd)
    {
        if(wrd.length()==0)
            throw new Error("That is not a word");
        if(!count.containsKey(wrd))
            count.put(wrd,1);
        else
            count.put(wrd,count.get(wrd)+1);
        int pos = getWordPos(wrd);
        if(dict[pos]==null)
            dict[pos]=new TrieNode(wrd);
        else
            dict[pos].insert(wrd);
    }
    
    public void addNextWord(String first, String next)
    {
        dict[getWordPos(first)].addNextWord(first,next);
    }
    
    protected TrieNode getNode(int i)
    {
        return dict[i];
    }
    
    public List<TrieNode> getNodes(String s)
    {
        return dict[getWordPos(s)].getNodes(s);
    }
    
    public List<WordInt> getWords(String s)
    {
        if(dict[getWordPos(s)]!=null)
            return dict[getWordPos(s)].getWords(s);
        return new ArrayList<>();
    }
    
    public boolean contains(String s)
    {
        return dict[getWordPos(s)]!=null && dict[getWordPos(s)].contains(s);
    }
    
    public boolean containsWord(String s)
    {
        return dict[getWordPos(s)]!=null && dict[getWordPos(s)].containsWord(s,s);
    }
    
    /**
     * gets the index which the string corresponds to (using the first letter of 
     * the string) the trie in which it may be found
     * @param wrd
     * @return 
     */
    public static int getWordPos(String wrd)
    {
        int val = Character.getNumericValue(wrd.toCharArray()[0])-10;
        return val;
    }
}
