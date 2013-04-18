package edu.brown.cs32.jcadler.trie;

import java.util.List;

/**
 *
 * @author john
 */
public class Letter implements TrieVal
{
    private String c;
    
    public Letter(String ch)
    {
        if(ch.length()>1 || ch.length()==0)
            throw new Error("You cannot make a trieval of that lenghth");
        c=ch;
    }
    
    public boolean isWord()
    {
        return false;
    }
    
    public String getWord()
    {
        throw new Error("This is not a word");
    }
    
    public String getChar()
    {
        return c;
    }
    
    public List<WordInt> getWordNums()
    {
        throw new Error("You cannot get the words of a letter");
    }
    
    public List<WordInt> getWordNums(List<String> l)
    {
        throw new Error("You cannot get the words of a letter");
    }
    
    public void addWord(String s)
    {
        throw new Error("You cannot add a word to a letter");
    }
}
