package edu.brown.cs32.jcadler.utfFile;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.nio.charset.Charset;
import java.util.Collections;
import java.io.EOFException;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
/**
 * Gathers the names and the name pages from the index file
 * @author john
 */
public class UTFNameGather extends UTFRandAccessFile
{
    
    /**
     * all of the names of the streets in the index file
     */
    private List<String> names;
    /**
     * all of the pages (sets of names which are the same)
     */
    private Map<String,Integer> namePages;
    
    public UTFNameGather(String name) throws IOException
    {
        super(name);
    }
    
    /**
     * gets all of the lines from the index file, while at the same time
     * gathering up all names
     * @return
     * @throws IOException 
     */
    @Override
    protected List<Integer> getLines() throws IOException
    {
        namePages = new HashMap<>();
        names=new ArrayList<>();
        List<Integer> l = new ArrayList<>();
        List<Byte> line = new ArrayList<>();
        int i=0;
        int last=0;
        String crrnt="";
        byte b;
        try
        {
            while(true)
            {
                b=raf.readByte();
                if(b==10)
                {
                    l.add(i);
                    String str=new String(UTFPager.byteListToArray(line),Charset.forName("UTF-8"));
                    str=str.split("\t")[0];
                    if(!names.contains(str))
                        names.add(str);
                    if(!str.equals(crrnt))
                    {
                        if(crrnt.equals(""))
                        {
                            crrnt=str;
                            continue;
                        }
                        namePages.put(str+"begin",last);
                        namePages.put(crrnt+"end",last);
                        crrnt=str;
                    }
                    line.clear();
                    last=i+1;
                }
                else
                    line.add(b);
                i++;
            }
        }
        catch(EOFException e)
        {
            return l;
        }
    }
    
    /**
     * gets the list of names
     * @return 
     */
    public List<String> getNames()
    {
        return Collections.unmodifiableList(names);
    }
    
    /**
     * gets the page associated with the given name
     * @param name
     * @return
     * @throws IOException
     * @throws IllegalArgumentException 
     */
    public List<String> getNamePage(String name) throws IOException, IllegalArgumentException
    {
        byte[] read;
        if(namePages.containsKey(name+"begin"))
        {
            int top;
            if(!namePages.containsKey(name+"end"))
                top=super.length;
            else
                top=namePages.get(name+"end");
            read = new byte[top-namePages.get(name+"begin")];
            super.raf.seek(namePages.get(name+"begin"));
            super.raf.read(read);
            String lines = new String(read,Charset.forName("UTF-8"));
            return Arrays.asList(lines.split("\n"));
        }
        throw new IllegalArgumentException(name+" is not the name of a street!");
    }
}
