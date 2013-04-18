package edu.brown.cs32.jcadler.maps.GUI;

import edu.brown.cs32.jcadler.autocorrect.dictionary.Analyzer;
import edu.brown.cs32.jcadler.autocorrect.dictionary.Dictionary;
import javax.swing.event.*;
import javax.swing.*;
import java.util.List;

/**
 *
 * @author john
 */
public class LiveUpdater implements DocumentListener
{
    private JTextField field;
    private JTextArea area;
    private Dictionary dict;
    private List<Analyzer> la;
    boolean smart;
    
    public LiveUpdater(JTextField f, JTextArea a, Dictionary d, List<Analyzer> as, boolean s)
    {
        field=f;
        area=a;
        dict=d;
        la=as;
        smart=s;
    }
    
    public void changedUpdate(DocumentEvent e)
    {}
    
    public void insertUpdate(DocumentEvent e)
    {
        react();
    }
    
    public void removeUpdate(DocumentEvent e)
    {
        react();
    }
    
    private void react()
    {
        String txt = field.getText();
        if(txt.equals(""))
        {
            area.selectAll();
            area.replaceSelection("");
            return;
        }
        List<String> result = CommandLine.parseInput(txt,dict,la,smart);
        txt=txt.toLowerCase();
        txt=txt.trim();
        txt=txt.replaceAll("[^a-z]+"," ");
        String[] split = txt.split(" ");
        String phrase = "";
        for(int i=0;i<split.length-1;i++)
            phrase=phrase+split[i]+" ";
        String fin="";
        for(String s : result)
            fin+=phrase+s+"\n";
        area.selectAll();
        area.replaceSelection(fin);
    }
}
