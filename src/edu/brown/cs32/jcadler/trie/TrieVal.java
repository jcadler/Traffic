package edu.brown.cs32.jcadler.trie;

import java.util.List;

/**
 * a value which represents a character in the trie or the end of a word in
 * the trie (differentiated by the isWord() method)
 * @author john
 */
public interface TrieVal 
{
    /**
     * returns true if the current objest is a word, false if it is a word
     * @return 
     */
    public boolean isWord();
    /**
     * gets the character of the current Letter
     * @return 
     */
    public String getChar();
    /**
     * gets the word of the current Word
     * @return 
     */
    public String getWord();
    /**
     * gets the WordInts of the current word
     * @return 
     */
    public List<WordInt> getWordNums();
    /**
     * gets the WordInts which correspond to the strings in l, setting their
     * num values to 0 if they are not present in the current Word
     * @param l the string with which to grab the WordInts
     * @return 
     */
    public List<WordInt> getWordNums(List<String> l);
    
    /**
     * adds the string s to the current Word
     * @param s 
     */
    public void addWord(String s);
}
