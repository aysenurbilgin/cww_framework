/*
 * Copyright (c) 2015 Aysenur Bilgin
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzylogic.type2.core;

import fuzzylogic.generic.Slice;
import fuzzylogic.generic.Tuple;

/**
 * GT2Interface is the interface for all general type 2 membership functions
 * @author abilgin
 */
public interface GT2Interface {
    
    public Tuple getSupport();

    public String getLinguisticLabel();

    public void setLinguisticLabel(String lbl);
    
    public double getUpper(double x);
    
    public double getLower(double x);
    
    public double getConfidenceAngle();

    public void setConfidenceAngle(double teta);
    
    public Tuple getMembershipInterval(double x);

    public Slice getSlice(Object o);
    
    public double getSecondaryDegree(Slice s);
    
}
