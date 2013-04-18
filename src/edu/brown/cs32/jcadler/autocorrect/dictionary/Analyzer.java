package edu.brown.cs32.jcadler.autocorrect.dictionary;

import java.util.List;

/**
 * Allows multiple types of analysis to be run on the given string
 * @author john
 */
public interface Analyzer 
{
    public List<String> analyze(String s);
}
