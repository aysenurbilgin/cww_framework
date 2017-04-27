/*
 * Copyright (c) 2015 Aysenur Bilgin
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fcc;

import fuzzylogic.generic.Tuple;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

/**
 *
 * @author abilgin
 */
public class FCCMFGenerator implements Serializable {
    
    private int numberoflabels;                         //number of output linguistic labels to be modelled
    private ArrayList<String> headings;                 //headings for the input/output type
    private ArrayList<ArrayList<String>> allData;       //collect all the data per file to analyze
    private Map<String, FCCResource> categorizedData;   //this is the structure where the labels and the corresponding resource is kept
    private Map<Integer, String> codebook;
    private ArrayList<Word> linguisticVariables;

    
    /**
     * Get the filename and create a membership function from scratch
     * Find the most prevailing values according to the number of modifiers which is a parameter
     * @param filename is the file where the necessary data resides
     */
    public FCCMFGenerator(String filename) {
        
        int countofmodifiers = 0;
        
        headings = new ArrayList<String>();
        
        categorizedData = new TreeMap<String, FCCResource>();
        
        codebook = new TreeMap();
        codebook.put(countofmodifiers, "extremely"); 
        countofmodifiers++;
        codebook.put(countofmodifiers, "very"); 
        countofmodifiers++;
        codebook.put(countofmodifiers, ""); 
        
        linguisticVariables = new ArrayList<Word>(); 

        //parse the data file
        parseDataFile(filename);
        
        //analyze the data and form the resources
        analyzeDataSISO();
        
    }
    
    public FCCMFGenerator() {
        
        int countofmodifiers = 0;
             
        headings = new ArrayList<String>();
        
        categorizedData = new TreeMap();
        
        codebook = new TreeMap();
        codebook.put(countofmodifiers, "extremely"); 
        countofmodifiers++;
        codebook.put(countofmodifiers, "very"); 
        countofmodifiers++;
        codebook.put(countofmodifiers, ""); 

        linguisticVariables = new ArrayList<Word>();
        
    }
    
    public FCCMFGenerator(FCCMFGenerator gen) {
             
        this.headings = new ArrayList<String>();
        this.headings = gen.getHeadings();
        
        this.categorizedData = new TreeMap();
        this.categorizedData = gen.getResources();
        
        this.codebook = new TreeMap();
        this.codebook = gen.getCodebook();
        
        this.linguisticVariables = new ArrayList<Word>();
        this.linguisticVariables = gen.getLinguisticVariables();
        
        this.allData = new ArrayList<ArrayList<String>>();
        this.allData = gen.getAllData();
        
        this.numberoflabels = gen.getNumberOfLabels();
        
    }
     
    /**
     * The data file format for reading application is as follows
     * concepts as many
     * Important ones to be used in the analysis are light sensor value and the output linguistic label
     * @param strFile 
     */
    private void parseDataFile(String strFile) {

        allData = new ArrayList<ArrayList<String>>();
        
         try {
            //create BufferedReader to read csv file
            BufferedReader br = new BufferedReader( new FileReader(strFile));
            String strLine;
            StringTokenizer st;
            int lineNumber = 0, tokenNumber = 0;

            //first line of the file consists of headings
            strLine = br.readLine();
            lineNumber++;

            //break comma separated line using ","
            st = new StringTokenizer(strLine, ",");

            while(st.hasMoreTokens()) {

                tokenNumber++;
                String current = st.nextToken();
                if (!headings.contains(current.toLowerCase())) {
                    headings.add(current.toLowerCase());
                }

            }

            //reset token number
            tokenNumber = 0;

            boolean isLoggingComplete = false;
            ArrayList<String> allDataLine;

            //read comma separated file line by line until logging is complete
            while(!isLoggingComplete) {

                strLine = br.readLine();

                if (strLine.contains("Logging completed")) {
                    isLoggingComplete = true;
                }
                else if(strLine == null ? "" != null : !strLine.equals("")) {
                    //System.out.println(strLine);
                    lineNumber++;

                    //break comma separated line using ","
                    st = new StringTokenizer(strLine, ",");

                    allDataLine = new ArrayList<String>();

                    while(st.hasMoreTokens()) {

                        tokenNumber++;
                        allDataLine.add(st.nextToken());
                    }

                    allData.add(allDataLine);

                    //reset token number
                    tokenNumber = 0;
                }
            }//end of while - logging

        }
        catch(Exception e) {
            e.printStackTrace();
        }

    }

    private void analyzeDataSISO() {
        
        //the data is presented in several columns as many as the raw concepts
        //need to find the sorted and unique values and put them in fuzzy sets
        Word tempWord;
        
        //for each heading
        for (int i = 0; i < headings.size(); i++) {
            
            tempWord = null;
            if (headings.get(i).equalsIgnoreCase("preparation time")) {
                tempWord = new Word("preparation time", "short", "long");
                linguisticVariables.add(tempWord);
            }
            else if (headings.get(i).equalsIgnoreCase("cooking time")) {
                tempWord = new Word("cooking time", "quick", "slow");
                linguisticVariables.add(tempWord);
            }
            //september 2013 adding overall time input
            else if (headings.get(i).equalsIgnoreCase("overall time")) {
                tempWord = new Word("zoverall time", "little", "big");
                linguisticVariables.add(tempWord);
            }

            Collections.sort(linguisticVariables);
             
            ArrayList<Double> temp = new ArrayList<Double>();

            //for each value
            for (int j = 0; j < allData.size(); j++) {
                
                //add the value to the arraylist to be turned into Resource
                temp.add(Double.parseDouble(allData.get(j).get(i)));
                
            }

            categorizedData.put(tempWord.getLinguisticVariableName(), new FCCResource(tempWord, codebook, temp));

        }//end for
        
        setNumberOfLabels();
                
    }//end analyzeDataSISO
    
    public void adaptToNewExperience(String variableName, double newvalue) {
        
        boolean exists = false;
        
        for (String s: this.categorizedData.keySet()) {
            //find the appropriate variable name and place it
            if (s.equalsIgnoreCase(variableName)) {
                this.categorizedData.get(s).addToExperience(newvalue);
                exists = true;
            }
        }
        
        if (!exists) {
            System.err.println("FCCMFGenerator: Could not find "+variableName+" in data!");
        }
        
        //the number of labels might have changed after adaptation
        setNumberOfLabels();
    }
   

    public void printMFDetails() {

        for (String key : categorizedData.keySet()) {

            for (int i = 0; i < categorizedData.get(key).getResources().size(); i++) {
                categorizedData.get(key).getResources().get(i).printResource();
                System.out.println();
            }
        }

    }
    
    public void feedFile(String filename) {
        
        //parse the data file
        parseDataFile(filename);
        
        //analyze the data and form the resources
        analyzeDataSISO();
        
        //printMFDetails();
        
    }
    
    public Integer getNumberOfLabels() {
        return this.numberoflabels;
    }
    
    public ArrayList<ArrayList<String>> getAllData() {
        return allData;
    }

    public Map<String, FCCResource> getCategorizedData() {
        return categorizedData;
    }

    public Map<Integer, String> getCodebook() {
        return codebook;
    }

    public ArrayList<Word> getLinguisticVariables() {
        return linguisticVariables;
    }
    
    public Map<String, FCCResource> getResources() {
        return this.categorizedData;
    }

    public ArrayList<Double> getMin() {
        
        ArrayList<Double> minList = new ArrayList<Double>();
        
        for (String key : categorizedData.keySet()) {
            
            minList.add(categorizedData.get(key).getMin());
            
        }
        return minList;
    }

    public ArrayList<Double> getMax() {
        
        ArrayList<Double> maxList = new ArrayList<Double>();
        
        for (String key : categorizedData.keySet()) {
            
            maxList.add(categorizedData.get(key).getMax());
            
        }
        return maxList;
    }
    
    public ArrayList<Tuple> getSupport() {
        
        ArrayList<Tuple> sup = new ArrayList<Tuple>();
        
        for (String key : categorizedData.keySet()) {      
            sup.add(new Tuple(categorizedData.get(key).getMin(),categorizedData.get(key).getMax()));     
        }

        return sup;
        
    }
    
    public int getNumberofConcepts() {
        return this.linguisticVariables.size();
    }
    
    public Word getLinguistics(int num) {
        return this.linguisticVariables.get(num);
    }
    
    public ArrayList<String> getHeadings() {
        
        ArrayList<String> hdrs = new ArrayList<String>();
        
        for (String key : categorizedData.keySet()) {
            hdrs.add(key);
        }
        
        return hdrs;
    }

    private void setNumberOfLabels() {

        //find min between the linguistic variables
        int min = Integer.MAX_VALUE;
        for (FCCResource fres: this.categorizedData.values()) {
            if(min > fres.getNumberofResources()) {
                min = fres.getNumberofResources();
            }
        }
        this.numberoflabels = min;
        
    }
    
}
