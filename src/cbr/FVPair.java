/*
 * Copyright (c) 2015 Aysenur Bilgin
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cbr;

import fuzzylogic.generic.Tuple;
import fuzzylogic.type2.zSlices.AgreementMF_zMFs;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author abilgin
 */
public class FVPair implements Serializable {
    
    private Feature feature;
    private Value value;       //value can be a membership function or an interval or string or number
    
    public FVPair(Feature f, Value v) {
        this.feature = f;
        this.value = v;
    }
    
    public FVPair(FVPair fvp) {
        this.feature = fvp.getFeature();
        this.value = fvp.getValue();
    }
    
    public FVPair(Feature f) {
        this.feature = f;
    }
    
    public Value getValue() {
        return this.value;
    }
    
    public void setValue(Value v) {
        this.value = v;
    }
    
    public Feature getFeature() {
        return this.feature;
    }
    
    public void setFeature(Feature f) {
        this.feature = f;
    }
    
    public AgreementMF_zMFs getMFValue() {
        
        return this.value.getMFValue(this.feature);
    }
    
    public ArrayList<RetrievedSlice> getSliceValue() {
        
        return this.value.getSlicesValue(this.feature.getName());
     
    }
    
    public Tuple getCombinedSliceIntervalValue() {
        
        double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
        
        for (RetrievedSlice s: getSliceValue()) {
            
            double mins = s.getSlice().getDomain().getLeft();
            double maxs = s.getSlice().getDomain().getRight();
            
            if (mins <= min) {
                min = mins;
            }
            if (maxs >= max) {
                max = maxs;
            }
            
        }
        
        return new Tuple(min, max);
    }
    
    @Override
    public boolean equals( Object obj ) {

        boolean flag = false;
        
        if (obj instanceof FVPair) {
            FVPair fvp = (FVPair) obj;

            if (this.getFeature().equals(fvp.getFeature()) && this.getValue().equals(fvp.getValue())) {
                flag = true;
            }
        }
        
        return flag;
    }

    @Override
    public String toString() {
        return "FVPair{" + "feature=" + feature + ", value=" + value + '}';
    }
    
    

}
