package edu.brown.cs32.jcadler.utfFile;

import java.io.RandomAccessFile;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.nio.charset.Charset;

/**
 * Reads arbitrary lines from a UTF-8 encoded file
 * @author john
 */
public class UTFRandAccessFile
{
    protected RandomAccessFile raf;
    protected List<Integer> newLines;
    protected int length;
    
    public UTFRandAccessFile(String name) throws IOException
    {
        raf=new RandomAccessFile(name,"r");
        length=(int)raf.length();
        newLines=getLines();
    }
    
    /**
     * true if there is the file ends with a new line character, false if not
     * @return 
     */
    public boolean endLine()
    {
        return newLines.get(newLines.size()-1)==length-1;
    }
    
    /**
     * gets the positions of all new line characters in the provided file
     * @return
     * @throws IOException 
     */
    protected List<Integer> getLines() throws IOException
    {
        List<Integer> l = new ArrayList<>();
        int i=0;
        byte b;
        while((b=raf.readByte())!=-1)
        {
            if(b==10)
                l.add(i);
            i++;
        }
        return l;
    }
    
    /**
     * returns the number of lines in the file
     * @return 
     */
    public int getNumLines()
    {
        return newLines.size();
    }
    
    /**
     * returns the line which is indicated by the given integer from the file
     * @param i
     * @return
     * @throws IOException 
     */
    public String getLine(int i) throws IOException
    {
        byte[] read;
        String ret;
        int end,begin;
        if(i==0)
        {
            begin=0;
            end=newLines.get(0);
        }
        else if(i==newLines.size() && newLines.get(newLines.size()-1)!=length-1)
        {
            begin=newLines.get(newLines.size()-1)+1;
            end=length;
        }
        else
        {
            begin=newLines.get(i-1)+1;
            end=newLines.get(i);
        }
        raf.seek(begin);
        read=new byte[end-begin];
        raf.read(read);
        ret = new String(read,Charset.forName("UTF8"));
        raf.seek(0);
        return ret;
    }
}