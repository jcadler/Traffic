package edu.brown.cs32.jcadler.utfFile;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.nio.charset.Charset;
import java.io.EOFException;
import java.util.Map;
import java.util.HashMap;

/**
 * An extension of UTFRandAccessFile which takes into account paging
 * @author john
 */
public class UTFPager extends UTFRandAccessFile 
{
    /**
     * stores the mapping of the block ID to the position in the file
     */
    private Map<String,Integer> pages;
    
    public UTFPager(String name) throws IOException
    {
        super(name);
    }
   
    /**
     * gets all of the lines in the file, while grabbing the start positions
     * of each page and storing them in pages
     * @return a list containing the line positions
     * @throws IOException if there was something wrong with reading the file
     */
    @Override
    protected List<Integer> getLines() throws IOException
    {
        List<Integer> l = new ArrayList<>();
        Map<String,Integer> ps = new HashMap<>();
        List<Byte> line = new ArrayList<>();
        int i=0;
        int p=0;
        byte b;
        String crrnt="";
        int last=0;
        try
        {
            while(true)
            {
                b=super.raf.readByte();
                line.add(b);
                if(b==10)
                {
                    l.add(i);
                    if(l.size()>1)
                    {
                        
                        String str = new String(byteListToArray(line),Charset.forName("UTF-8"));
                        str=str.split("\t")[0];
                        str=str.substring(str.indexOf("/", 1)+1,str.indexOf(".")); 
                        if(!str.equals(crrnt))
                        {
                            ps.put(str,last);
                            crrnt=str;
                        }
                    }
                    line.clear();
                    last=i+1;
                }
                i++;
            }
        }
        catch(EOFException e)
        {
            ps.put(""+(Integer.parseInt(crrnt)+1),super.length);
            pages=ps;
            return l;
        }
    }
    
    /**
     * turns a list of Bytes into an array of bytes (for creating new strings)
     * @param l
     * @return 
     */
    public static byte[] byteListToArray(List<Byte> l)
    {
        Byte[] b = l.toArray(new Byte[0]);
        byte[] ret = new byte[b.length];
        for(int i=0;i<b.length;i++)
            ret[i]=b[i].byteValue();
        return ret;
    }
    
    /**
     * gets the number of pages in the file
     * @return 
     */
    public int getNumPages()
    {
        return pages.size();
    }
    
    /**
     * gets all lines in the page indicated by i
     * @param i
     * @return
     * @throws IOException 
     */
    public List<String> getPage(int i, Boolean exitAble) throws IOException
    {
        if(checkExit(exitAble))
            return null;
        List<String> ret;
        int next = getNextPage(i);
        byte[] read;
        if(i==pages.size()-1)
            read=new byte[super.length-pages.get(""+i)];
        else
            read=new byte[pages.get(""+next)-pages.get(""+i)];
        if(checkExit(exitAble))
            return null;
        super.raf.seek(pages.get(""+i));
        super.raf.read(read);
        if(checkExit(exitAble))
            return null;
        ret=byteArrayToStringList(read);
        return ret;
    }
    
    /**
     * gets the next page id in the file, starting from i
     * @param i
     * @return 
     */
    private int getNextPage(int i)
    {
        int next = i+1;
        while(!pages.containsKey(""+next))
            next++;
        return next;
    }
    
    /**
     * gets all pages within the id range indicated by low and high (inclusive)
     * @param low
     * @param high
     * @return
     * @throws IOException 
     */
    public List<String> getPageRange(int low, int high) throws IOException
    {
        List<String> ret;
        if(pages.get(""+low)==null)
            low=getNextPage(low);
        byte[] read = new byte[pages.get(""+getNextPage(high))-pages.get(""+low)];
        super.raf.seek(pages.get(""+low));
        super.raf.read(read);
        ret=byteArrayToStringList(read);
        return ret;
    }
    /**
     * convertts an array of bytes to a list of strings, where each string is an
     * individual line
     * @param read
     * @return 
     */
    private List<String> byteArrayToStringList(byte[] read)
    {
        List<String> ret = new ArrayList<>();
        String str = new String(read,Charset.forName("UTF-8"));
        ret = Arrays.asList(str.split("\n"));
        return ret;
    }
    
    private boolean checkExit(Boolean exit)
    {
        synchronized(exit)
        {
            if(exit)
                return true;
            return false;
        }
    }
}
