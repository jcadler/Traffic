package edu.brown.cs32.jcadler.trie;

import java.util.List;
import java.util.ArrayList;
/**
 * The node of a trie within the tree
 * @author John Adler
 */
public class TrieNode extends TreeMember<TrieNode,TrieVal>
{
    private TrieVal tv;
    
    /**
     * Creates an empty trie with it's value equal to null
     */
    public TrieNode()
    {
        super(new ArrayList<TrieNode>());
        tv=null;
    }
    
    /**
     * Creates a tree with the current node as the root, with children whose 
     * values correspond to the characters in s
     * @param s the string which is used to seed the trie
     */
    public TrieNode(String s)
    {
        super(new ArrayList<TrieNode>());
        tv=new Letter(s.substring(0,1));
        insert(s,s);
    }
    
    /**
     * creates a new node with the value tv
     * @param val the value with which to initialize the current node
     */
    private TrieNode(TrieVal val)
    {
        super(new ArrayList<TrieNode>());
        tv=val;
    }
    
    /**
     * returns the value of the current node
     * @return the value of the current node
     */
    public TrieVal getData()
    {
        return tv;
    }
    
    /**
     * inserts the string into the trie, starting at the current node
     * @param s the string to be inserted
     */
    public void insert(String s)
    {
        insert(s,s);
    }
    
    /**
     * inserts the string s by moving down the tree recursively, either moving
     * into nodes which contain the first letter of the given string, or creating
     * the requisite nodes. Once a string of a single letter is reached, a Word 
     * node is added to the children of the node corresponding to the single letter,
     * who is initialized with the full string
     * @param s the string to insert
     * @param full the full word
     */
    private void insert(String s, String full)
    {
        if(s.length()==0)
            throw new Error("Cannot insert an empty string");
        if(tv.isWord())
            throw new Error("Cannot insert into the end of a word");
        if(!tv.getChar().equals(s.substring(0,1)))
            throw new Error("The word "+s+" does not correspond to the "+tv.getChar()+" trie");
        if(s.length()==1)
        {
            boolean insrt=true;
            for(TrieNode tn : super.kids)
            {
                if(tn.getData().isWord() && tn.getData().getWord().equals(full))
                {
                    insrt=false;
                    break;
                }
            }
            if(insrt)
                insert(new Word(full));
        }
        else
        {
            String next=s.substring(1,2);
            for(TrieNode t : super.kids)
            {
                if(!t.getData().isWord() && t.getData().getChar().equals(next))
                {
                    t.insert(s.substring(1),full);
                    return;
                }
            }
            TrieNode ins = new TrieNode(new Letter(next));
            ins.insert(s.substring(1),full);
            super.insertKid(ins);
        }
    }
    
    /**
     * inserts the given value into the chilren of the current node
     * @param val the value to insert
     */
    public void insert(TrieVal val)
    {
        super.insertKid(new TrieNode(val));
    }
    
    /**
     * add the next word to the frequency counter of the first
     * @param first the word to which next is added
     * @param next the word to add
     */
    public void addNextWord(String first, String next)
    {
        addNextWord(first,first,next);
    }
    
    /**
     * the same as the previous addNextWord, but preserves the full name of the
     * first word from recursive calls
     * @param first
     * @param full
     * @param next 
     */
    private void addNextWord(String first, String full, String next)
    {
        if(first.length()==1)
        {
            for(TrieVal t : unwrap(super.kids))
            {
                if(t.isWord() && t.getWord().equals(full))
                {
                    t.addWord(next);
                    return;
                }
            }
            Word wrd=new Word(first);
            wrd.addWord(next);
            super.insertKid(new TrieNode(wrd));
            return;
        }
        String down = first.substring(1,2);
        for(TrieNode n : super.kids)
        {
            if(!n.getData().isWord() && n.getData().getChar().equals(down))
            {
                n.addNextWord(first.substring(1),full,next);
                return;
            }
        }
    }
    
    /**
     * returns a list of the values of the given list l
     * @param l the list of TrieNodes to unwrap
     * @return the unwrapped list
     */
    private List<TrieVal> unwrap(List<TrieNode> l)
    {
        List<TrieVal> v = new ArrayList<>();
        for(TrieNode t : l)
            v.add(t.getData());
        return v;
    }
    
    /**
     * returns the frequency counter list assicated with the given string
     * @param s the string from which to fetch the counter
     * @return the list of WordInts which represent the frequency counter
     *         of the given word
     */
    public List<WordInt> getWords(String s)
    {
        return getWords(s,s);
    }
    
    /**
     * The same as the previous getWords, but preserves the full name of the given
     * string from recursive calls
     * @param s
     * @param full the preserved word
     * @return 
     */
    
    private List<WordInt> getWords(String s,String full)
    {
        if(s.length()==1)
        {
            for(TrieVal v : unwrap(super.kids))
            {
                if(v.isWord() && v.getWord().equals(full))
                    return v.getWordNums();
            }
            return null;
        }
        else
        {
            for(TrieNode n : super.kids)
            {
                if(!n.getData().isWord() && n.getData().getChar().equals(s.substring(1,2)))
                    return n.getWords(s.substring(1),full);
            }
            return null;
        }
    }
    
    /**
     * checks if the trie contains the given string
     * @param s the string to check
     * @return true if s is found, false if not
     */
    public boolean contains(String s)
    {
        if(s.length()==0)
            return true;
        if(s.length()==1)
            return !tv.isWord() && s.equals(tv.getChar());
        if(!tv.isWord() && tv.getChar().equals(s.substring(0,1)))
        {
            for(TrieNode n : super.kids)
            {
                if(!n.getData().isWord() && n.getData().getChar().equals(s.substring(1,2)))
                    return n.contains(s.substring(1));
            }
        }
        return false;
    }
    
    /**
     * checks if s is a word contained within the current trie
     * @param s the string to check
     * @param full preserves s from recursive calls
     * @return true if s is a word, false if not
     */
    public boolean containsWord(String s, String full)
    {
        if(s.length()==0)
            throw new Error("That is not a word");
        if(s.length()==1)
        {
            if(tv.isWord() || !tv.getChar().equals(s))
                return false;
            for(TrieVal v : unwrap(super.kids))
            {

                if(v.isWord() && v.getWord().equals(full))
                    return true;
            }
            return false;
        }
        for(TrieNode n : super.kids)
        {
            if(!n.getData().isWord() && n.getData().getChar().equals(s.substring(1,2)))
                return n.containsWord(s.substring(1),full);
        }
        return false;
    }
    
    /**
     * returns all of the characters of the children of the current node
     * @return the characters of the children of the current node
     */
    public List<String> getChildChars()
    {
        List<String> ret = new ArrayList<>();
        for(TrieVal v : unwrap(super.kids))
        {
            if(v.isWord())
                continue;
            ret.add(v.getChar());
        }
        return ret;
    }
    
    /**
     * gets the node representing the final character of the given string, null
     * if no such node exists
     * @param s
     * @return 
     */
    public TrieNode getNode(String s)
    {
        if(s.length()==1)
        {
            if(s.equals(tv.getChar()))
                return this;
            return null;
        }
        for(TrieNode n : super.kids)
        {
            if(!n.getData().isWord() && n.getData().getChar().equals(s.substring(1,2)))
                return n.getNode(s.substring(1));
        }
        return null;
    }
    
    /**
     * returns the list of nodes which are the closest match to the string s
     * (e.g. if "nodes" isn't contained but "no" is, the nodes which have "n" and
     * "o" will be returned)
     * @param s
     * @return 
     */
    public List<TrieNode> getNodes(String s)
    {
        List<TrieNode> ret = new ArrayList<>();
        ret.add(this);
        if(s.length()==1)
            return ret;
        for(TrieNode n : super.kids)
        {
            if(!n.getData().isWord() && n.getData().getChar().equals(s.substring(1,2)))
                ret.addAll(n.getNodes(s.substring(1)));
        }
        return ret;
    }
    
    /**
     * returns the list of children of the current node
     * @return 
     */
    public List<TrieNode> getKids()
    {
        return super.kids;
    }
}
