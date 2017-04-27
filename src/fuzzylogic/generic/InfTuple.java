/*
 * Copyright (c) 2015 Aysenur Bilgin
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzylogic.generic;

/**
 * InfTuple is a specific data type for storing necessary information for the inference engine 
 * such as the firing interval of the rule and the centroid interval of the consequent
 * @author abilgin
 */
public class InfTuple {
    
    private Tuple centroidInterval;
    private Tuple firingInterval;

    public InfTuple() {
        this.centroidInterval = new Tuple(0,0);
        this.firingInterval = new Tuple(0,0);
    }

    public InfTuple(Tuple ci, Tuple fi) {
        this.centroidInterval = new Tuple(ci.getLeft(), ci.getRight());
        this.firingInterval = new Tuple(fi.getLeft(), fi.getRight());
    }

    public Tuple getCentroidInterval() {
        return this.centroidInterval;
    }

    public Tuple getFiringInterval() {
        return this.firingInterval;
    }
    
    public void setCentroidInterval(Tuple ci) {
        this.centroidInterval = ci;
    }

    public void setFiringInterval(Tuple fi) {
        this.firingInterval = fi;
    }
    
}
