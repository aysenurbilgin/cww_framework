/*
 * Copyright (c) 2015 Aysenur Bilgin
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cbr;

import fuzzylogic.generic.Tuple;
import fuzzylogic.type2.interval.IntervalT2MF_Interface;
import fuzzylogic.type2.zSlices.AgreementMF_zMFs;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author abilgin
 */
public class Value implements Serializable {
    
    private Object value;
    private Object correspondingMF;
    
    public Value(Object v) {
          
        if (v instanceof AgreementMF_zMFs) {
            this.value = (AgreementMF_zMFs) v;
            this.correspondingMF = (AgreementMF_zMFs) v;
        }
        else if (v instanceof Tuple) {
            this.value = (Tuple) v;
        }
        else if (v instanceof String) {
            this.value = (String) v;
        }
        else if (v instanceof Double) {
            this.value = (Tuple) new Tuple((Double)v, (Double)v);
        }
        
    }
    
    public Value(Value v) {
        
        if (v.getValue() instanceof AgreementMF_zMFs) {
            this.value = (AgreementMF_zMFs) v.getValue();
            this.correspondingMF = (AgreementMF_zMFs) v.getValue();
        }
        else if (v.getValue() instanceof Tuple) {
            this.value = (Tuple) v.getValue();
        }
        else if (v.getValue() instanceof String) {
            this.value = (String) v.getValue();
        }
        else if (v.getValue() instanceof Double) {
            this.value = (Tuple) new Tuple((Double)v.getValue(), (Double)v.getValue());
        }
        
    }
    
    public Object getValue() {
        return this.value;
    }
    
    //not used for the moment
    public AgreementMF_zMFs getMFValue(Feature f) {
        
        if (this.value instanceof Tuple) {
            this.correspondingMF = convertIntervaltoMF(f);
        }
        else if (this.value instanceof String) {
            this.correspondingMF = convertPerceptiontoMF(f);
        }

        return (AgreementMF_zMFs)this.correspondingMF;
    }
    
    public ArrayList<RetrievedSlice> getSlicesValue(String fname) {
        
        if (this.value instanceof Tuple) {
            this.correspondingMF = convertIntervaltoSlice(fname);
        }
        else if (this.value instanceof String) {
            this.correspondingMF = convertModifiertoSlices(fname);
        }

        return (ArrayList<RetrievedSlice>)this.correspondingMF;
    }
    
    /**
     * Method finds the slice name which is having the highest zLevel
     * @return 
     */
    
    public String getConsensusSliceName(Object obj) {
        
        String highestSlice = "not known yet";
        int zVal = 0;
        
        if (obj instanceof String) {
            for (RetrievedSlice rs: getSlicesValue((String)obj)) {
                int zrs = rs.getZLevel();
                if ( zrs >= zVal) {
                    zVal = zrs;
                    highestSlice = rs.getName();
                }
            }
        }
        else if (obj instanceof AgreementMF_zMFs[]) {

            for (RetrievedSlice rs: convertIntervaltoSlice((AgreementMF_zMFs[])obj)) {
                int zrs = rs.getZLevel();
                if ( zrs >= zVal) {
                    zVal = zrs;
                    highestSlice = rs.getName();
//                    System.out.println("VALUE highest slice : " + highestSlice);
                }
            }

        }
        else {

        }
        return highestSlice;
    }
    
    
    /*
     * Gets the interval value of either the interval from FCC or the domain of the MF that it has been assigned
     */
    public Tuple getIntervalValue() {
        
        if (this.value instanceof Tuple) {
            return (Tuple)this.value;
        }
        else if (this.value instanceof AgreementMF_zMFs) {
            return ((AgreementMF_zMFs)this.value).getDomain();
        }
        else if (this.value instanceof IntervalT2MF_Interface) {
            return ((IntervalT2MF_Interface)this.value).getDomain();
        }
        else {
            return new Tuple (0,0);
        }
    }
    
    public String getStringValue() {
        return this.value.toString();
    }
    
    public Double getDoubleValue() {
        if (this.value instanceof Tuple) {
            return (Double) ((Tuple)this.value).getAverage();
        }
        
        return (Double)this.value;
    }
    
    /*
     * Method gets the interval for the value which is a Tuple and finds in which mf the tuple sits in
     */
    private AgreementMF_zMFs convertIntervaltoMF(Feature f) {

        Map currentVocabulary = Vocabulary.getInstance();
        double max = 0.0;
        AgreementMF_zMFs corrSet = null;
        
        for (AgreementMF_zMFs set: (AgreementMF_zMFs[])currentVocabulary.get(f.getName())) {
            //find the maximum firing average strength from both sets
            if (max <= calculateFS((Tuple)value, set)) {
                max = calculateFS((Tuple)value, set);
                corrSet = new AgreementMF_zMFs(set);
            }
        }
        
        return corrSet;

    }

    private AgreementMF_zMFs convertPerceptiontoMF(Feature f) {
        
        //get the string and convert it into intervals of mf as the string might have the modifier as well
        String[] wordList = ((String)this.value).split("\\s+");  
        String modifier = "", perception = "";
        
        if (wordList.length > 1) {
            for (int i = 0; i < wordList.length-1; i++) {
                modifier = modifier.concat(wordList[i]);  
                if (i+1 != wordList.length-1) {
                    modifier = modifier.concat(" ");
                }
            }
            perception = wordList[wordList.length-1];   
        }
        else {
            perception = wordList[0];
            modifier = "";
        }
        //find the modifier in the vocabulary
        //if it does not exist - learning process
        //retrieve the slices related to the modifier
        return Vocabulary.retrieveMFforPerception(f.getName(), perception);
    
    }
    
    private ArrayList<RetrievedSlice> convertModifiertoSlices(String fname) {
        
        //get the string and convert it into intervals of mf as the string might have the modifier as well
        String[] wordList = ((String)this.value).split("\\s+");  
        String modifier = "", perception = "";
        
        if (wordList.length > 1) {
            for (int i = 0; i < wordList.length-1; i++) {
                modifier = modifier.concat(wordList[i]);  
                if (i+1 != wordList.length-1) {
                    modifier = modifier.concat(" ");
                }
            }
            perception = wordList[wordList.length-1];   
        }
        else {
            perception = wordList[0];
            modifier = "";
        }
        
        perception = perception.trim();
        modifier = modifier.trim();
        //find the modifier in the vocabulary
        //if it does not exist - learning process
        //retrieve the slices related to the modifier
        return Vocabulary.retrieveSlicesforModifier(fname, modifier, perception);
  
    }

    private double calculateFS(Tuple val, AgreementMF_zMFs set) {
        
        int discretizationsteps = 1000;
        
        double start = val.getLeft();
        double end = val.getRight();
        double iter = (end - start)/(discretizationsteps - 1);
        
        double setTotal = 0.0;
        
        for (int i = 0; i < set.getNumberofSlices(); i++) {
            
            IntervalT2MF_Interface slice = set.getZSlice(i);
            
            //first check whether the value is out of bounds
            if (iter == 0.0) {
                setTotal += slice.getFSAverage(start);
//                System.out.println("Value: Set total of slice: "+slice.getName()+" set total: "+setTotal);
            }
            else {
                double sliceTotal = 0.0;
                double xtotal = 0.0;
                //modification: applied the centroid formula!!
                for (int disc = 0; disc < discretizationsteps; disc++) {   
                    double x = start + iter*disc;
                    sliceTotal += slice.getFSAverage(x)*x;   
                    xtotal += x;
                }

                setTotal+= sliceTotal/xtotal;
            }
        }
        
        return setTotal/(double)set.getNumberOfSlices();
        
    }
    
    private double calculateFSforSlice(Tuple val, IntervalT2MF_Interface slice) {
        
        int discretizationsteps = 1000;
        
        double start = val.getLeft();
        double end = val.getRight();
        double iter = (end - start)/(discretizationsteps - 1);

        //in the case of Tuple showing a double value
        if (iter == 0.0) {
            return slice.getFSAverage(start); 
        }
        
        double sliceTotal = 0.0;
        double xtotal = 0.0;
        //modification: applied the centroid formula!!
        for (int disc = 0; disc < discretizationsteps; disc++) {  
            
            double x = start + iter*disc;
            sliceTotal += slice.getFSAverage(x)*x;   
            xtotal += x;
        }
    
        return sliceTotal/xtotal;
        
    }
    
    @Override
    public String toString() {
        String s = "";
        s = (new StringBuilder()).append(s).append("Value: ").append(this.value.toString()).append(", corresponding MF: ").append(this.correspondingMF).toString();
        return s;
    }

    private ArrayList<RetrievedSlice> convertIntervaltoSlice(Object obj) {
  
        double max = Double.NEGATIVE_INFINITY;
        AgreementMF_zMFs corrSet = null;
        RetrievedSlice corrSlice = null;
        AgreementMF_zMFs[] searchsets = null;
        
        if (obj instanceof String) {
            Map currentVocabulary = Vocabulary.getInstance();
            searchsets = (AgreementMF_zMFs[])currentVocabulary.get(((String)obj).toLowerCase());
        }
        else if (obj instanceof AgreementMF_zMFs[]) {
            searchsets = (AgreementMF_zMFs[])obj;
        }
        
        for (AgreementMF_zMFs set: searchsets) {
            //find the maximum firing average strength from both sets
            double cfs = calculateFS((Tuple)value, set);
            if (max <= cfs) {
                max = cfs;
                corrSet = new AgreementMF_zMFs(set);
//                System.out.println("Value: The maximum fs "+ max + " and the set is "+corrSet.toString());
            }
               
        }
        
        //after finding the set now find the slices that give more firing
        //this time the slice value is taken into account
        max = 0.0;
        for (int i = 0; i < corrSet.getNumberOfSlices(); i++) {
            
            IntervalT2MF_Interface slice = corrSet.getZSlice(i);
            double cfs = calculateFSforSlice((Tuple)value, slice);  //sonuc surekli extremely easy veya hard cikio kurtulmak icin deniorum
            if (max <= cfs) {
                max = cfs;
                corrSlice = new RetrievedSlice(slice, i, corrSet.getNumberOfSlices());
//                System.out.println("Value: The maximum fs "+ max + " and the slice is "+corrSlice.toString());
            }
        }
        
        ArrayList<RetrievedSlice> retSlice = new ArrayList<RetrievedSlice>();
        retSlice.add(corrSlice);
        
//        System.out.println("Value: Returned slices are "+retSlice.toString());
        
        return retSlice;
    }
    
    @Override
    public boolean equals( Object obj ) {

        boolean flag = false;
        
        if (obj instanceof Value) {
            Value v = (Value) obj;

            if (this.hashCode() == v.hashCode()) {
                flag = true;
            }
        }
        
        return flag;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + (this.value != null ? this.value.hashCode() : 0);
        //hash = 89 * hash + (this.correspondingMF != null ? this.correspondingMF.hashCode() : 0);
        return hash;
    }
        
}
