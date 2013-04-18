package edu.brown.cs32.jcadler.autocorrect.dictionary;

import edu.brown.cs32.jcadler.trie.TrieNode;

import java.util.List;
import java.util.ArrayList;

/**
 * Used to find words that are within a levenshtein distance of n of a given word
 * using the supplied dictionary
 * @author John Adler
 */
public class Levenshtein implements Analyzer {

    private Dictionary dict;
    private int n;

    /**
     * creates a Levenshtein object with the default distance and the supplied
     * dictionary
     * @param d 
     */
    public Levenshtein(Dictionary d) 
    {
        dict = d;
        n = 2;
    }

    public Levenshtein(Dictionary d, int num) {
        dict = d;
        n = num;
    }
    /**
     * returns the list of words that are within levenshtein distance n of 
     * the given string. This is accomplished by finding every possible
     * insertion/substituion/deletion mutation of the supplied string,
     * constrained by the nodes in the supplied dictionary
     * @param s
     * @return 
     */
    public List<String> analyze(String s) {
        List<String> collect = new ArrayList<>();
        List<String> bucket = new ArrayList<>();
        List<String> iter = new ArrayList<>();
        List<String> ret = new ArrayList<>();
        iter.add(s);
        for (int i = 0; i < n; i++) 
        {
            for (String str : iter) 
            {
                bucket.addAll(insertionFront(str));
                bucket.addAll(substitution(str));
                if (dict.getNode(Dictionary.getWordPos(str)) != null) 
                {
                    if(str.equals("halp"))
                        System.out.println("hello");
                    bucket.addAll(substituteWord(str));
                    bucket.addAll(insertion(str));
                }
                if (str.length() != 1) 
                {
                    bucket.addAll(deletion(str));
                }
            }
            collect.addAll(bucket);
            iter.clear();
            iter.addAll(bucket);
            bucket.clear();
        }
        for (String str : collect) 
        {
            if (!ret.contains(str) && dict.containsWord(str)) {
                ret.add(str);
            }
        }
        return ret;
    }

    /**
     * finds all possible words that result from a singe substitution within
     * the given string. This method checks the first letter, and then calls
     * the substituteWord method in order to cover the rest of the word
     * @param s
     * @return 
     */
    public List<String> substitution(String s) {
        List<String> ret = new ArrayList<>();
        for (int i = 0; i < 26; i++) 
        {
            String c = s.substring(0, 1);
            TrieNode cur = dict.getNode(i);
            if (dict.getNode(i) == null || cur.getData().isWord() || cur.getData().getChar().equals(c)) 
                continue;
            String current = cur.getData().getChar();
            ret.add(current + s.substring(1));
        }
        return ret;
    }

    /**
     * returns every possible string which results from a single substituion 
     * in the given string
     * @param s
     * @return 
     */
    public List<String> substituteWord(String s) 
    {
        List<TrieNode> wrd = dict.getNodes(s);
        List<TrieNode> kids;
        List<String> ret = new ArrayList<>();
        String c = "";
        for (int i = 0; i < wrd.size(); i++) 
        {
            kids = wrd.get(i).getKids();
            if (i != s.length() - 1) 
            {
                c = s.substring(i + 1, i + 2);
            }
            for (TrieNode nde : kids) 
            {
                if (i == s.length() - 1) 
                {
                    break;
                }
                if (!nde.getData().isWord() && !nde.getData().getChar().equals(c)) 
                {
                    ret.add(s.substring(0, i + 1) + nde.getData().getChar() + s.substring(i + 2));
                }
            }
        }
        return ret;
    }

    /**
     * returns the strings which result from inserting a letter into the front
     * of s
     * @param s
     * @return 
     */
    public List<String> insertionFront(String s) {
        List<String> ret = new ArrayList<>();
        TrieNode cur;
        for (int i = 0; i < 26; i++)//loop through the beginning letters
        {
            cur = dict.getNode(i);
            if (cur != null && !cur.getData().getChar().equals(s.substring(0, 1))) 
            {
                String c = cur.getData().getChar();
                ret.add(c + s);
            }
        }
        return ret;
    }

    /**
     * returns the strings which result from inserting a letter throughout
     * the rest of the string
     * @param s
     * @return 
     */
    public List<String> insertion(String s) {
        List<String> ret = new ArrayList<>();
        List<TrieNode> wrd = dict.getNodes(s);
        TrieNode cur;
        for (int i = 0; i < wrd.size(); i++)//then all letters within the word itself
        {
            cur = wrd.get(i);
            for (TrieNode tn : cur.getKids()) {
                if (!tn.getData().isWord()
                        && (i == wrd.size() - 1
                        || !tn.getData().getChar().equals(s.substring(i + 1, i + 2)))) {
                    ret.add(s.substring(0, i + 1) + tn.getData().getChar() + s.substring(i + 1));
                }
            }
        }
        return ret;
    }

    /**
     * returns the strings which result from all possible single letter
     * deletions in the given string
     * @param s
     * @return 
     */
    public List<String> deletion(String s) {
        List<String> ret = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            ret.add(s.substring(0, i) + s.substring(i + 1));
        }
        return ret;
    }
}