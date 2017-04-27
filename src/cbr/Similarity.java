/*
 * Copyright (c) 2015 Aysenur Bilgin
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cbr;

import fuzzylogic.generic.Tuple;
import fuzzylogic.type2.core.LVDesignerExpert;
import fuzzylogic.type2.interval.IntervalT2MF_Interface;

import java.util.ArrayList;

/**
 * Similarity is a complex data type depending on the two instances that are being compared
 * @author abilgin
 */
public class Similarity {
    
    public static final double similarityThreshold = 0.5;
    public static final int nBestCases = 3;
    private static int discretizationsteps = 1000;

    /**
     * Jaccard similarity applied on the individual slices
     * @param queryset is the slice that is put as query
     * @param set2
     * @param querydomain
     * @return 
     */
    public static double findJaccardSimilarity(ArrayList<RetrievedSlice> queryset, ArrayList<RetrievedSlice> set2, Tuple querydomain) {
        
        double similarityvalue = 0.0, weighttotal = 0.0;
        ArrayList<Double> comparisonList = new ArrayList<Double>();

        //as jaccard similarity is symmetric, calculate each slice with the other 
        //the zvalues have been taken into account 
        //however to increase the performance we will add a function of distance as weight
        //distance is the zSlice value difference between the sets so the weight will be assigned as (1-difference/numberofslices)
        //for the eventual normalization, divide the sum by the total amount of similarity calculations which is multiplication of the sizes of retrieved slices
        for (int i = 0; i < set2.size(); i++) {
            
            int zval2 = set2.get(i).getZLevel(); 
            double controlVal = 0.0;
            
            for (int j = 0; j < queryset.size(); j++) {
                
                int difference = Math.abs(queryset.get(j).getZLevel() - zval2);
                double weight = 1.0 - (difference/LVDesignerExpert.getNozSlices());
                double sim = getBasicJaccardIT2(queryset.get(j).getSlice(), queryset.get(j).getZValue(), set2.get(i).getSlice(), set2.get(i).getZValue(), querydomain);
                similarityvalue += weight * sim;
                weighttotal += weight;
                controlVal += weight * sim;
            }
            comparisonList.add(controlVal);
        }
        
        if (comparisonList.size() > 1) {
            // if the comparison list size is bigger than 1 then there could be values to compare
            boolean symmetric = true;
            double ccomp = comparisonList.get(0);
            for (int i = 1; i < comparisonList.size(); i++) {
                if (comparisonList.get(i) == ccomp) {
                    symmetric &= true;
                    ccomp = comparisonList.get(i);
                }
                else {
                    symmetric &= false; 
                }
            }

            if (symmetric) {
                //similarity should be 1.0
                return 1.0;
            }
        }

        return similarityvalue/weighttotal;
    }

    /**
     * Basic Jaccard similarity is indeed tailored for zSlices as it takes into account the zValue for calculating the mf degree of each slice
     * @param queryintset
     * @param queryz
     * @param otherset
     * @param otherz
     * @param querydomain
     * @return 
     */
    private static double getBasicJaccardIT2(IntervalT2MF_Interface queryintset, double queryz, IntervalT2MF_Interface otherset, double otherz, Tuple querydomain) {
                
        //formula is taken from 2009 Wu and Mendel for interval type-2 set
        //s = (sum(min(upper-a, upper-b)) + sum(min(lower-a, lower-b)))/ (sum(max(upper-a, upper-b)) + sum(max(lower-a, lower-b)))
        //adjust the formula for a retrievedslice case
        double summinu = 0, summinl = 0, summaxu = 0, summaxl = 0;

        double end1 = querydomain.getRight();
        double start1 = querydomain.getLeft();
        double iter1 = (end1-start1)/(discretizationsteps-1);

        double start2 = start1;
        double iter2 = iter1;

        for (int disc = 0; disc < discretizationsteps; disc++) {

            summinu += Math.min(queryintset.getFS(start1).getRight() * queryz, otherset.getFS(start2).getRight() * otherz);
            summinl += Math.min(queryintset.getFS(start1).getLeft() * queryz, otherset.getFS(start2).getLeft() * otherz);
            summaxu += Math.max(queryintset.getFS(start1).getRight() * queryz, otherset.getFS(start2).getRight() * otherz);
            summaxl += Math.max(queryintset.getFS(start1).getLeft() * queryz, otherset.getFS(start2).getLeft() * otherz);
            start1 += iter1;
            start2 += iter2;
        }

        return (summinu + summinl) / (summaxu + summaxl);
        
    }
}
