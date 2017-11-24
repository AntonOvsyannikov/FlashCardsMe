
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Administrator
 */
public class CSVReader2 {
    private String separator;
    private BufferedReader br = null;
    public long linesCount = 0;
    
    // TODO: не совсем тут все правильно - надо различать ненайденный и закрытый файл 
    CSVReader2(File file, String encoding, String separator) {
//        List<Integer> l = new ArrayList<>();
//        for (Integer i : l) {}
        this.separator = separator;
        try {
             br = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalArgumentException();
        } catch (FileNotFoundException ex) {
        }
        
        try (LineNumberReader lnr = new LineNumberReader(new InputStreamReader(new FileInputStream(file), encoding))){
            lnr.skip(Long.MAX_VALUE);
            linesCount = lnr.getLineNumber() + 1;
            lnr.close();
        } catch (Exception ex) {}
    }


    void close() {
        if (br != null) try { br.close(); } catch (IOException ex) {}
    }
    
   
    List<String> readRow() {
        if (br == null) return null;
        String s = null;
        try {
            s = br.readLine();
        } catch (IOException ex) {
                return null;
        } 
        if (s == null) return null;
        return new ArrayList<>(Arrays.asList(s.split(separator)));        
    }

}
