/*
 * Copyright (c) 2015 Aysenur Bilgin
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzylogic.type1.core;

import fuzzylogic.generic.Resource;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

/**
 *
 * @author abilgin
 */
public class MFGenerator {
    
    private Integer numberoflabels;                 //number of output linguistic labels to be modelled
    private ArrayList<String> headings;             //headings for the input/output type
    private ArrayList<ArrayList<String>> allData;   //collect all the data per file to analyze
    private Map<String, Resource> categorizedData;  //this is the structure where the labels and the corresponding resource is kept 
    private double min;                             //holds the minimum of the categorizedData
    private double max;                             //holds the maximum of the categorizedData
    
    /**
     * Get the filename and create a membership function from scratch
     * Find the most prevailing values according to the number of modifiers which is a parameter
     * @param filename is the file where the necessary data resides
     */
    public MFGenerator(String filename) {
        
        headings = new ArrayList<String>();
        categorizedData = new TreeMap();
        //parse the data file
        parseDataFile(filename);
        
        //analyze the data and form the resources
        analyzeDataSISO();
        
    }
    
    public MFGenerator() {
             
        headings = new ArrayList<String>();
        categorizedData = new TreeMap();
        
    }
     
    /**
     * The data file format for reading application is as follows
     * Type of Interaction	Time of Day	Light Sensor Value	Input Linguistic Label	Reading Light Level	Output Linguistic Label
     * Important ones to be used in the analysis are light sensor value and the output linguistic label
     * @param strFile 
     */
    private void parseDataFile(String strFile) {

        allData = new ArrayList<ArrayList<String>>();
        
         try {

            //csv file containing data

            //create BufferedReader to read csv file
            BufferedReader br = new BufferedReader( new FileReader(strFile));
            String strLine = "";
            StringTokenizer st = null;
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
        
        //the data is presented in two columns as the input and the corresponding output word respectively (SISO: SingleInputSingleOutput)
        //need to find the unique linguistic labels and put them in separate Resource data types
        //important: we need to find the relationship between input and the output to decide on the perception
        
        for (int i = 0; i < allData.size(); i++) {

            String outputword = allData.get(i).get(1);
            if (!categorizedData.containsKey(outputword)) {
                
                Resource temp = new Resource(outputword);
                temp.addToExperience(Double.parseDouble(allData.get(i).get(0)));
                categorizedData.put(outputword, temp);
                
            }
            else if (categorizedData.containsKey(outputword)) {
                
                //then we have the key and the value should be added to the list of values to be analyzed
                categorizedData.get(outputword).addToExperience(Double.parseDouble(allData.get(i).get(0)));
                
            }//end if
            
        }//end for
        
        this.numberoflabels =  this.categorizedData.size();
        findMinMax();              
                
    }//end analyzeDataSISO
    
    private void findMinMax() {
        
        ArrayList<Double> list = new ArrayList<Double>(); 
        
        for ( Map.Entry<String, Resource> entry : categorizedData.entrySet()) {
            
            list.add(entry.getValue().getMin());
            list.add(entry.getValue().getMax());
            
        }
        
        this.min = Collections.min(list);
        this.max = Collections.max(list);
        
    }

    public Integer getNumberOfLabels() {
        return this.numberoflabels;
    }
    
    public void feedFile(String filename) {
        
        //parse the data file
        parseDataFile(filename);
        
        //analyze the data and form the resources
        analyzeDataSISO();
        
    }
    
    public Map getResources() {
        return this.categorizedData;
    }

    public double getMin() {
        return this.min;
    }

    public double getMax() {
        return this.max;
    }
    
}
