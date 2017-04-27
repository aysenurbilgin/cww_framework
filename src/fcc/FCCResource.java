/*
 * Copyright (c) 2015 Aysenur Bilgin
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fcc;

import fuzzylogic.generic.Resource;
import fuzzylogic.generic.Tuple;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * FCCResource is a data type finding its own labels
 * given the range of inputs and also finding the critical values such as the min, max, most prevailing etc.
 * @author abilgin
 */
public class FCCResource implements Serializable{
    
    private Map<Double, Integer> experience;    //holds the numeric past data as experience in a structured way with the count of occurrences
    private Map<Integer, String> codebook;
    private ArrayList<Resource> resources;
    private Tuple support;
    private Word linguistics;
    private double min;
    private double max;
    
    
    public FCCResource(Word linguisticVarData, Map<Integer, String> codebook, ArrayList<Double> experienceData) {
        
        this.linguistics = linguisticVarData;
        this.experience = new TreeMap<Double,Integer>();
        this.codebook = new TreeMap<Integer, String>();
        this.codebook.putAll(codebook);
        this.resources = new ArrayList<Resource>();
        this.support = new Tuple();
        setAllExperience(experienceData);
        setAnalytics();

    }
    
    public FCCResource(FCCResource fccres) {
        this.experience = new TreeMap<Double, Integer>();
        this.experience = fccres.getExperience();
        this.codebook = new TreeMap<Integer, String>();
        this.codebook = fccres.getCodebook();
        this.resources = new ArrayList<Resource>();
        this.resources = fccres.getResources();
        this.support = new Tuple(fccres.getSupport());
        this.linguistics = new Word(fccres.getLinguistics());
        this.min = fccres.getMin();
        this.max = fccres.getMax();
    }
    
    /**
     * Get the raw experience data and store it in a structured way with occurrences
     * @param rawExperience 
     */
    private void setAllExperience(ArrayList<Double> rawExperience) {
        
        double val;
        int count = 0;
        for (int  i = 0; i < rawExperience.size(); i++) {
            
            val = rawExperience.get(i);
            
            if( !this.experience.containsKey(val)) {
                this.experience.put(val, 0 );
            }
            
            this.experience.put(val, this.experience.get(val) + 1);
            count++;
        }
    
    }
    
    public void addToExperience(double val) {
        
        if( !this.experience.containsKey(val)) {
            this.experience.put(val, 0 );
        }
        
        this.experience.put(val, this.experience.get(val) + 1);
        
        resetForAnalytics();
        setAnalytics();
    
    }
    
    private void setAnalytics() {
        
        //find the relevant data from the experience
        if (!this.experience.isEmpty()) {
            
            //find the weighted average
            double avg = findAverage();
            
            Map<Double, Integer> negTemp = new TreeMap();
            Map<Double, Integer> posTemp = new TreeMap();
            
            //divide experience into two perceptions according to the average value
            for (Double key: this.experience.keySet()) {
                int value = this.experience.get(key);
                //if the value is smaller than average put it on the negative perception
                if (key <= avg) {
                    negTemp.put(key, value);
                }
                else {
                    posTemp.put(key, value);
                }
                
            }

            
            //then further divide the negative and positive perception into modifiers
            //the criteria is to break the stream into the number of codebook contents
            int minnumberofelements = (int) Math.ceil((double)negTemp.size()/(double)codebook.size());
            String s;

            Map<Double, Integer> partialresource = new TreeMap<Double, Integer>();
            int counter = 1, modifiercode = 0;
            
            for (Map.Entry <Double, Integer> current : negTemp.entrySet()) {
                partialresource.put(current.getKey(), current.getValue());
            
                //then it is time to change the resource
                if (counter >= minnumberofelements) {
                    
                    s = (new StringBuilder()).append(codebook.get(modifiercode)).append(" ").append(this.linguistics.getNegPerception()).toString();
                    this.resources.add(new Resource(s, partialresource));
                    //System.out.println("FCCResource - resources: "+this.resources.toString());
                    partialresource = new TreeMap<Double, Integer>();
                    modifiercode++;
                    counter = 0;
                    
                }

                counter++;
                
            }
            //for the last set of resources repeat the operation
            //if you have not consumed all of the modifiers!!
            while (codebook.get(modifiercode)!= null) {
                s = (new StringBuilder()).append(codebook.get(modifiercode)).append(" ").append(this.linguistics.getNegPerception()).toString();
                //if the prevailing value is smaller than the previous one
                //it means that there not enough members in the array
                //so duplicate the values but not the string
                if (partialresource.isEmpty()) {
                    partialresource = this.resources.get(this.resources.size()-1).getExperience();
                }
                this.resources.add(new Resource(s, partialresource));
                modifiercode++;
            }
            
            //repeat for the right perception
            minnumberofelements = (int) Math.ceil((double)posTemp.size()/(double)codebook.size());

            partialresource = new TreeMap<Double, Integer>();

            counter = 1; 
            modifiercode = codebook.size()-1;

            for (Map.Entry <Double, Integer> current : posTemp.entrySet()) {
                 partialresource.put(current.getKey(), current.getValue());
            
                //then it is time to change the resource
                if (counter >= minnumberofelements) {
                    
                    s = (new StringBuilder()).append(codebook.get(modifiercode)).append(" ").append(this.linguistics.getPosPerception()).toString();
                    this.resources.add(new Resource(s, partialresource));
                    partialresource = new TreeMap<Double, Integer>();
                    modifiercode--;
                    counter = 0;
                    
                }
                counter++;
                
            }
            //for the last set of resources repeat the operation
            while (codebook.get(modifiercode) != null) {
                s = (new StringBuilder()).append(codebook.get(modifiercode)).append(" ").append(this.linguistics.getPosPerception()).toString();
                if (partialresource.isEmpty()) {
                    partialresource = this.resources.get(this.resources.size()-1).getExperience();
                }
                this.resources.add(new Resource(s, partialresource));
                modifiercode--;
            }
            
            reviseAndCorrectRecource();

            double mintemp = Double.POSITIVE_INFINITY, maxtemp = Double.NEGATIVE_INFINITY;
            
            //try finding the min max from experience not resource
            for (Double d: this.experience.keySet()) {
                
                if(d < mintemp) {
                    mintemp = d;
                }
                if(d > maxtemp) {
                    maxtemp = d;
                }
            }
            
            this.min = mintemp;
            this.max = maxtemp;
            this.support = new Tuple(min, max);
        }
    }

    private double findAverage() {
        
        double sumnom = 0.0, sumdenom = 0.0;
        
        for (Double key : this.experience.keySet()) {
            
            sumnom += key * this.experience.get(key);
            sumdenom += this.experience.get(key);
        }
        
        return sumnom/sumdenom;
    }
    
    public Map<Integer, String> getCodebook() {
        return this.codebook;
    }

    public Map<Double, Integer> getExperience() {
        return this.experience;
    }
    
    public ArrayList<Resource> getResources() {
        return this.resources;
    }
    
    public int getNumberofResources() {
        return this.resources.size();
    }
    
    public Tuple getSupport() {
        return this.support;
        
    }
    
    public double getMin() {
        return this.min;
    }
    
    public double getMax() {
        return this.max;
    }
    
    public Word getLinguistics() {
        return this.linguistics;
    }
    
    @Override
    public String toString() {
        String s = "";
        s = (new StringBuilder()).append(s).append("FCCResource: ").append(this.experience.toString()).toString();
        return s;
    }

    private void resetForAnalytics() {
        
        this.resources = new ArrayList<Resource>();
        this.support = new Tuple();
        
    }

    /**
     * Method looks at the resource and tries to differentiate between the values for prevailing numbers
     * because in the future when they are put into a map, data is lost
     * also can be regarded as forcing the number of elements to be processed to be the same throughout the entire implementation
     * the idea is simply assumed to add a dummy value to each
     */
    private void reviseAndCorrectRecource() {
        
        boolean duplicateflag = false;
        Map<Double, Integer> occmap = new TreeMap<Double, Integer>();
        for (Resource r: this.resources) {
            if (!occmap.containsKey(r.getPrevailingValue())) {
                occmap.put(r.getPrevailingValue(), 0);
            }
            else {
                duplicateflag = true;
            }
            occmap.put(r.getPrevailingValue(), occmap.get(r.getPrevailingValue()) + 1);
        }
        
        if (!duplicateflag) {
            return;
        }
        
        //now correction should be applied     
        //find the values having more than 1 occurrence
        Iterator<Map.Entry<Double, Integer>> it = occmap.entrySet().iterator();
        Map<Double, Integer> occmapcopy = new TreeMap<Double, Integer>();
        //create a copy of the map
        for (Map.Entry<Double, Integer> e: occmap.entrySet()) {
            occmapcopy.put(e.getKey(), e.getValue());
        }

        Iterator<Map.Entry<Double, Integer>> innerit = occmapcopy.entrySet().iterator();
        double prev = -1.0;
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            Map.Entry entryin = (Map.Entry)innerit.next();
            double d = (Double)entry.getKey();
            
            if (occmap.get(d) > 1) {
                //then d has a duplicate
                //find the value d in resources and adapt it
                //System.out.println("Duplicate found!!");
                for (int i = 0; i < this.resources.size(); i++) {
                    if (d == this.resources.get(i).getPrevailingValue()) {
                        //check whether there is a value after it
                        double afterval, inc;
                        if (it.hasNext()) {
                            entryin = (Map.Entry)innerit.next();
                            afterval = (Double)entryin.getKey();
                            inc = (afterval - d) / (2.0D * (occmap.get(d)-1));
                            
                            for (int j = 0; j < occmap.get(d)-1; j++) {
                                this.resources.get(i+j+1).setPrevailingValue(d+(inc*(j+1)));
                                this.resources.get(i+j+1).setWeightedAvg(d+(inc*(j+1)));
                            }
                            i = i + occmap.get(d)-1;
                        }
                        else {
                            //since this is the last one decrease it
                            //apply the same idea with the prev value
                            inc = (d - prev) / (2.0D * (occmap.get(d)-1));
                            
                            for (int j = occmap.get(d)-1; j > 0; j--) {
                                this.resources.get(i+j-1).setPrevailingValue(d-(inc*j));
                                this.resources.get(i+j-1).setWeightedAvg(d-(inc*j));
                            }
                            i = i + occmap.get(d)-1;
                        }
                    }
                    else {
                        //System.out.println("Do nothing!");
                    }
                }//end for
                
                //reset the iterator
                innerit = occmapcopy.entrySet().iterator();
            }
            prev = d;
            
        }// end while
        
    }
    
    
}
