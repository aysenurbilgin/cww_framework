/*
 * Copyright (c) 2015 Aysenur Bilgin
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzylogic.generic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Resource is a data type containing the name of the linguistic label (perception) and the modifier
 * together with the range of inputs and also finding the critical values such as the min, max, most prevailing etc.
 * @author abilgin
 */
public class Resource implements Serializable {
    
    private double prevailing;                  //holds the most prevailing value to be the base transition point
    private double min;                         //holds the minimum of the experience
    private double max;                         //holds the maximum of the experience
    private double weightedAvg;                 //holds the weighted average value of the experience
    private String perception;                  //holds the perception as one category of two opposite sides
    private String modifier;                    //holds the modifier string
    private String label;                       //holds the modifier+perception= the whole word
    private Map<Double, Integer> experience;    //holds the numeric past data as experience in a structured way with the count of occurrences
       
    public Resource(String linguisticLabel, ArrayList<Double> experienceData) {
        
        String[] wordList = linguisticLabel.split("\\s+");  
        
        if (wordList.length > 1) {
            this.modifier = wordList[0];
            this.perception = wordList[1];   
        }
        else {
            this.perception = wordList[0];
            this.modifier = "";
        }
        
        this.label = linguisticLabel;
        this.experience = new TreeMap<Double,Integer>();
        setAllExperience(experienceData);
        //find the relevant data from the experience
        setAnalytics();

    }
    
    public Resource(String linguisticLabel, Map<Double, Integer> exp) {
        
        String[] wordList = linguisticLabel.split("\\s+");  
        
        if (wordList.length > 1) {
            this.modifier = wordList[0];
            this.perception = wordList[1];   
        }
        else {
            this.perception = wordList[0];
            this.modifier = "";
        }
        
        this.label = linguisticLabel;
        this.experience = new TreeMap<Double,Integer>();
        this.experience = exp;
        //find the relevant data from the experience
        setAnalytics();

    }
    
    public Resource(ArrayList<Double> experienceData) {
        
        this.perception = "not yet available";
        this.modifier = "not yet available";
        this.label = "not yet available";
        this.experience = new TreeMap<Double,Integer>();
        setAllExperience(experienceData);
        //find the relevant data from the experience
        setAnalytics();

    }

    public Resource(String linguisticLabel) {
        
        String[] wordList = linguisticLabel.split("\\s+");  
        
        if (wordList.length > 1) {
            this.modifier = wordList[0];
            this.perception = wordList[1];   
        }
        else {
            this.perception = wordList[0];
            this.modifier = "";
        }
        
        this.label = linguisticLabel;
        //set up the experience which is to be filled one by one
        this.experience = new TreeMap<Double,Integer>();
        
    }
    
    
    public Resource(Resource r) {
        
        this.prevailing = r.getPrevailingValue();
        this.min = r.getMin();
        this.max = r.getMax();
        this.weightedAvg = r.getWeightedAvg();
        this.perception = r.getPerception();
        this.modifier = r.getModifier();
        this.label = r.getLabel();
        this.experience = new TreeMap<Double,Integer>();
        this.experience = r.getExperience();
        
    }

    public double getWeightedAvg() {
        return weightedAvg;
    }
    
    public void setWeightedAvg(double weightedAvg) {
        this.weightedAvg = weightedAvg;
    }
    
    public double getPrevailingValue() {
        //System.out.println("Returning prevailing value of "+this.prevailing + " for "+this.label);
        return this.prevailing;
    }
    
    public void setPrevailingValue(double prevailingValue) {
        this.prevailing = prevailingValue;
    }
    
    public double getMin() {
        return this.min;
    }
    
    public void setMin(double minimum) {
        this.min = minimum;
    }
    
    public double getMax() {
        return this.max;
    }
    
    public void setMax(double maximum) {
        this.max = maximum;
    }
    
    public String getPerception() {
        return this.perception;
    }
    
    public void setPerception(String perceivedLabel) {
        this.perception = perceivedLabel;
    }
    
    public String getModifier() {
        return this.modifier;
    }
    
    public void setModifier(String modifierLabel) {
        this.modifier = modifierLabel;
    }
    
    public String getLabel() {
        return this.label;
    }
    
    public Map<Double, Integer> getExperience() {
        return experience;
    }
    
    /**
     * Get the raw experience data and store it in a structured way
     * @param rawExperience 
     */
    private void setAllExperience(ArrayList<Double> rawExperience) {
        
        double val;
        for (int  i = 0; i < rawExperience.size(); i++) {
            val = rawExperience.get(i);
            if( !this.experience.containsKey(val)) {
                this.experience.put(val, 0 );
            }
            this.experience.put(val, this.experience.get(val) + 1);
        }
    
    }
    
    public void addToExperience(double val) {
        
        if( !this.experience.containsKey(val)) {
            this.experience.put(val, 0 );
        }
        
        this.experience.put(val, this.experience.get(val) + 1);
        
        setAnalytics();
    
    }
    
    private void setAnalytics() {
        
        //find the relevant data from the experience
        if (!this.experience.isEmpty()) {
            
            // Get a set of the entries 
            Set set = this.experience.entrySet(); 
            // Get an iterator 
            Iterator i = set.iterator(); 
            // Find the min, max and most prevailing elements 
            double tempmin = 0, tempmax = 0, tempprevail = 0, nom = 0;
            int count = 0, occ = 0, occCount = 0, denom = 0;
            
            while(i.hasNext()) { 
                
                Map.Entry me = (Map.Entry)i.next(); 
                if (count == 0) {
                    tempmin = (Double) me.getKey(); 
                }
                
                if (count == this.experience.size() - 1) {
                    tempmax = (Double) me.getKey();
                }
                
                if (occ <= (Integer) me.getValue()) {
                    occ = (Integer) me.getValue();   
                }
                
                denom += (Integer) me.getValue();
                nom += (Double) me.getKey() * (Integer)me.getValue();
                count++;
                
            }
            
            //find the most prevailing average as there might be occurrences of the same amount
            i = set.iterator(); 
            while(i.hasNext()) {
                
                Map.Entry me = (Map.Entry)i.next(); 
                if ((Integer)me.getValue() == occ) {
                    tempprevail += (Double) me.getKey();
                    occCount++;
                }
            }

            this.min = tempmin;
            this.max = tempmax;
            this.prevailing = (Double) tempprevail/occCount;
            this.weightedAvg = (Double) nom/denom;
        }
    }
    
    public void printResource() {
        
        System.out.println("Resource: Experience: " + this.experience);
        System.out.println("Maximum : " + this.max);
        System.out.println("Minimum : " + this.min);
        System.out.println("Most prevailing : " + this.prevailing);
        System.out.println("Weighted average : " + this.weightedAvg);
        System.out.println("Modifier : " + this.modifier);
        System.out.println("Perception : " + this.perception);
        
    }
    
    public Tuple getSupport() {
        return new Tuple(min, max);
    }
    
    
    @Override
    public String toString() {
        String s = "";
        if (this.modifier.equalsIgnoreCase("none") || this.modifier.equalsIgnoreCase("")) {
            s = this.perception.toString();
        }
        else {
            s = (new StringBuilder()).append(this.modifier).append(" ").append(this.perception).toString();
        }
        s = (new StringBuilder()).append(s).append(" ( ").append(this.prevailing).append(" - ").append(this.weightedAvg).append(" )").toString();
        return s;
    }
    
}
