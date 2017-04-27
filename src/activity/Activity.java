/*
 * Copyright (c) 2015 Aysenur Bilgin
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package activity;

import cbr.Feature;
import cbr.Index;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author abilgin
 */
public class Activity extends Index implements Serializable, Comparable {
   
    private int numberOfActivation;
    
    protected Activity() {
        super.index = (String) "cook";
        super.features = new ArrayList<Feature>();
        this.numberOfActivation = 0;
        //create features
        populateFeatures();
        
    }
    
    public Activity (Activity a) {
        super.index = a.getIndex();
        super.features= a.getFeatures();
        this.numberOfActivation = a.getNumberOfActivation();
    }
        
    public int getNumberOfActivation() {
        return this.numberOfActivation;
    }

    public void setNumberOfActivation(int numberOfActivation) {
        this.numberOfActivation = numberOfActivation;
    }
    
    public int incrementNumberOfActivation() {
        this.numberOfActivation += 1;
        return this.numberOfActivation;
    }
    

    @Override
    public String getIndex() {
        return (String)super.index;
    }
    
    @Override
    public Feature getFeature(String name) {
        
        for (Feature f: super.features) {
            if (f.getName().equalsIgnoreCase(name)) {
                return f;
            }
        }
        return null;

    }
    
    @Override
    public ArrayList<Feature> getFeatures() {
        return super.features;
    }

    private void populateFeatures() {
        
        //create features which are prioritized for the specified activity
        //so far there are two activities : rest and cook and the features will be assigned manually including the weights
        
        if (((String)super.index).equalsIgnoreCase("rest")) {
            super.features.add(new Feature("tiredness", 0.6));
            super.features.add(new Feature("hungriness", 0.2));
            super.features.add(new Feature("leisuretime", 0.2));
        }
        else if (((String)super.index).equalsIgnoreCase("cook")) {
            super.features.add(new Feature("tiredness", 0.3));
            super.features.add(new Feature("hungriness", 0.5));
            super.features.add(new Feature("leisuretime", 0.4));
        }
        else {
            //any activity where there are no weights on the features
            super.features.add(new Feature("tiredness", 1.0));
            super.features.add(new Feature("hungriness", 1.0));
            super.features.add(new Feature("leisuretime", 1.0));
        }
    
    }

    @Override
    public int compareTo(Object a) {
        return ((String)super.index).compareTo(((Activity)a).getIndex());
    }
    
}
