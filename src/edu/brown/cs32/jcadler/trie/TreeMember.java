package edu.brown.cs32.jcadler.trie;

import java.util.List;
/**
 * A node in a tree (general definition)
 * @author John Adler
 */
public abstract class TreeMember<K,T>
{
    protected List<K> kids;
    
    /**
     * Create a node for a tree with the children k
     * @param k the children of the current node
     */
    public TreeMember(List<K> k)
    {
        kids=k;
    }
    
    /**
     * inserts the value t into the tree, starting from the current node
     * @param t the value to be inserted
     */
    public abstract void insert(T t);
    
    /**
     * inserts the node k into the current node's list of children
     * @param k the node to be added
     */
    public void insertKid(K k)
    {
        kids.add(k);
    }
    
    /**
     * returns the data of the current node
     * @return the data stored by the current node
     */
    public abstract T getData();
    
    /**
     * returns the child of the current node at position i
     * @param i the index of the child to be returned
     * @return the specified child
     */
    public K goDown(int i)
    {
        return kids.get(i);
    }
}
