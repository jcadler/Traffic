package edu.brown.cs32.jcadler.maps.GUI;

import edu.brown.cs32.jcadler.autocorrect.dictionary.Analyzer;
import edu.brown.cs32.jcadler.autocorrect.dictionary.Dictionary;
import edu.brown.cs32.jcadler.autocorrect.dictionary.Levenshtein;
import edu.brown.cs32.jcadler.autocorrect.dictionary.Prefix;
import edu.brown.cs32.jcadler.autocorrect.dictionary.Whitespace;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Collections;
import edu.brown.cs32.jcadler.trie.WordInt;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;

/**
 * Runs the command line portion of autocorrect. Also stores the 
 * makeDictionary, and parseInput methods, which are used
 * by both the gui and command line portions
 * @author john
 */
public final class CommandLine 
{
    /**
     * runs the command line loop, given the desired function of autocorrect
     * @param prefix
     * @param white
     * @param lev
     * @param smart
     * @param files
     * @throws IOException 
     */
    public static void run(boolean prefix, boolean white, int lev, 
                           boolean smart, List<String> files) throws IOException
    {
        Dictionary dict=makeDictionary(files);
        if(dict==null)
            return;
        List<Analyzer> as = selectAnalyzers(prefix,white,lev,smart,dict);
        Scanner in=new Scanner(System.in);
        String user;
        System.out.println("Ready");
        while(true)
        {
            user=in.nextLine();
            String test=user;
            test.trim();
            if(test.equals(""))
                break;
            if(user.substring(user.length()-1).equals(" "))
            {
                System.out.println();
                continue;
            }
            user=user.toLowerCase();
            user=user.trim();
            user=user.replaceAll("[^a-z]+"," ");
            String[] split = user.split(" ");
            String phrase = "";
            for(int i=0;i<split.length-1;i++)
                phrase=phrase+split[i]+" ";
            List<String> l = parseInput(user,dict,as,smart);
            for(String s : l)
                System.out.println(phrase+s);
            System.out.println();
        }
    }
    
    /**
     * creates a dictionary using words from the given corpus, defined by
     * files given by the list of file paths, returns null if none of the
     * files could be read.
     * @param files
     * @return 
     */
    public static Dictionary makeDictionary(List<String> files)
    {
        List<List<String>> in = new ArrayList<>();
        List<String> get = new ArrayList<>();
        List<List<String>> put = new ArrayList<>();
        Dictionary dict=new Dictionary();
        String current;
        int numWrong=0;
        for(String s : files)
        {
            System.out.println(s);
            try(BufferedReader read=new BufferedReader(new FileReader(s)))
            {
                while((current=read.readLine())!=null)
                    get.add(current);
                in.add(get);
            }
            catch(IOException io)
            {
                System.out.print("ERROR:");
                System.err.println(io.getMessage());
                numWrong++;
                continue;
            }
        }
        if(numWrong==files.size())
            return null;
        get=new ArrayList<>();
        for(List<String> l : in)
        {
            for(String s : l)
            {
                s=s.replaceAll("[^\\p{Alpha}]+"," ");
                s=s.toLowerCase();
                s=s.trim();
                get.addAll(Arrays.asList(s.split(" ")));
            }
            put.add(get);
        }
        for(List<String> l : put)
        {
            if(l.isEmpty())
                continue;
            while(l.contains(""))
                l.remove("");
            dict.insertWord(l.get(0));
            for(int i=1;i<l.size();i++)
            {
                dict.insertWord(l.get(i));
                dict.addNextWord(l.get(i-1),l.get(i));
            }
        }
        return dict;
    }
    
    /**
     * eliminates duplicate from the given list
     * @param l
     * @return 
     */
    private static List<String> elimDuplicates(List<String> l)
    {
        List<String> ret = new ArrayList<>();
        for(String s : l)
        {
            if(!ret.contains(s))
                ret.add(s);
        }
        return ret;
    }
    
    /**
     * parses the user input (removing non-alpha characters and splitting the 
     * string), and finds the closes matches to the final word using the given
     * analyzers
     * @param user
     * @param dict
     * @param as
     * @param smart
     * @return 
     */
    public static List<String> parseInput(String user, Dictionary dict, List<Analyzer> as,boolean smart)
    {
        List<String> ret = new ArrayList<>();
        user=user.toLowerCase();
        user=user.trim();
        user=user.replaceAll("[^a-z]+"," ");
        String[] split = user.split(" ");
        String last;
        String prev="";
        if(split.length==1)
            last=split[0];
        else
        {
            last=split[split.length-1];
            prev=split[split.length-2];
        }
        List<String> possible=new ArrayList<>();
        for(Analyzer a : as)
            possible.addAll(a.analyze(last));
        boolean best=false;
        if(dict.containsWord(last))
        {
            best=true;
            possible.add(last);
        }
        possible=elimDuplicates(possible);
        List<Ranker> rank;
        if(!prev.equals(""))
        {
            List<WordInt> wrds = dict.getWords(prev);
            rank = new ArrayList<>();
            String check;
            if(wrds!=null)
            {
                for(WordInt wi : wrds)
                {
                    if(possible.contains(wi.getWord()))
                    {
                        String cur = wi.getWord();
                        check=cur;
                        boolean good;
                        if(cur.contains(" "))
                            cur=check.split(" ")[0];
                        good=best && cur.equals(last);
                        rank.add(RankFactory.createRanker(cur,last,wi.getNum(),dict.getCount(check),good,smart));
                        possible.remove(wi.getWord());
                    }
                }
            }
            for(String str : possible)
            {
                check=str;
                if(str.contains(" "))
                    check=str.split(" ")[0];
                rank.add(RankFactory.createRanker(str,last,0,dict.getCount(check),best && str.equals(last),smart));
            }
        }
        else
        {
            rank=new ArrayList<>();
            for(String str : possible)
            {
                String check=str;
                if(str.contains(" "))
                    check=str.split(" ")[0];
                rank.add(RankFactory.createRanker(str,last,0,dict.getCount(check),best && str.equals(last),smart));
            }
        }
            Collections.sort(rank);
            int i=0;
            while(i<5 && i<rank.size())
            {
                ret.add(rank.get(i).getWord());
                i++;
            }
        return ret;
    }
    
    public static List<Analyzer> selectAnalyzers(boolean prefix, boolean white, int lev, 
                                                 boolean smart,Dictionary dict)
    {
        List<Analyzer> as=new ArrayList<>();
        if(prefix)
            as.add(new Prefix(dict));
        if(white)
            as.add(new Whitespace(dict));
        if(lev>=0)
            as.add(new Levenshtein(dict,lev));
        return as;
    }
}
    
