/*
 * Copyright (c) 2015 Aysenur Bilgin
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cbr;

import java.io.Serializable;

/**
 *
 * @author abilgin
 */
public class Feature implements Serializable, Comparable {
    
    private String fname;
    private double fweight;
    
    public Feature(Feature f) {
        
        
        this.fname = f.getName();
        this.fweight = f.getWeight();
    }
    
    public Feature(String name, double weight) {
        this.fname = name;
        this.fweight = weight;
    }
    
    //constructor for query case, the default weight is 1.0
    public Feature(String name) {
        this.fname = name;
        this.fweight = 1.0;
    }
    
    public Feature() {
        this.fname = "N/A";
        this.fweight = 0.0;
    }
    
    public void setName(String name) {
        this.fname = name;
    }
    
    public void setWeight(double weight) {
        this.fweight = weight;
    }
    
    public String getName() {
        return this.fname;
    }
    
    public double getWeight() {
        return this.fweight;
    }
    
    @Override
    public String toString() {
        String s = "";
        s = (new StringBuilder()).append(s).append("Feature: ").append(this.fname).append(", weight: ").append(this.fweight).toString();
        return s;
    }

    @Override
    public int compareTo(Object t) {
        
        return this.fname.compareTo(((Feature)t).getName());
        
    }
    
    @Override
    public boolean equals( Object obj ) {

        boolean flag = false;
        
        if (obj instanceof Feature) {
            Feature f = (Feature) obj;

            if (this.getName().equalsIgnoreCase(f.getName()) && (this.getWeight() == f.getWeight())) {
                flag = true;
            }
        }
        
        return flag;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.fname != null ? this.fname.hashCode() : 0);
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.fweight) ^ (Double.doubleToLongBits(this.fweight) >>> 32));
        return hash;
    }
}
