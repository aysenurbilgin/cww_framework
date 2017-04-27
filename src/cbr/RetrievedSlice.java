/*
 * Copyright (c) 2015 Aysenur Bilgin
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cbr;

import fuzzylogic.type2.interval.IntervalT2MF_Interface;
import java.io.Serializable;

/**
 *
 * @author abilgin
 */
public class RetrievedSlice implements Serializable {
    
    private IntervalT2MF_Interface slice;
    private double zVal;
    private int slicenum;

    public RetrievedSlice(IntervalT2MF_Interface slice, double zVal, int sliceno) {
        this.slice = slice;
        this.zVal = zVal;
        this.slicenum = sliceno;
    }
    
    public RetrievedSlice(IntervalT2MF_Interface slice, int sliceno, int totalnumberofslices) {
        this.slice = slice;
        this.zVal = (double)(sliceno+1)/totalnumberofslices;
        this.slicenum = sliceno;
    }
    
    
    public IntervalT2MF_Interface getSlice() {
        return this.slice;
    }
    
    public String getName() {
        return this.slice.getName();
    }
    
    public double getZValue() {
        return this.zVal;
    }
    
    public int getZLevel() {
        return this.slicenum;
    }
    
    @Override
    public String toString() {
        String s = "";
        s = (new StringBuilder()).append(s).append("Retrieved Slice: ").append(this.slice.getName()).append(", zLevel: ").append(this.slicenum).toString();
        return s;
    }
    
}
