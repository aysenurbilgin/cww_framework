/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fuzzylogic.type2.zSlices;

import fuzzylogic.type2.interval.IntervalT2MF_Interface;
import fuzzylogic.type2.interval.IntervalT2MF_Intersection;

import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

/**
 *
 * @author Christian Wagner
 * Copyright 2010 Christian Wagner All Rights Reserved.
 */
public class AgreementEngine
{
    private final boolean SHOWCONTEXT = true;

    public AgreementMF_zMFs findAgreement(String name, IntervalT2MF_Interface[] sets)
    {
        /*
        if(SHOWCONTEXT)System.out.println("Number of sets for agreement computation: "+sets.length);
        Vector<IntervalT2MF_Interface[]> zLevelsRaw = new Vector();   //a vector holding the individual sets generated (through
                                            //intersection) for this zLevel WITHOUT applying the union.
        Vector<IntervalT2MF_Interface> zLevelsFinal = new Vector();  // a vector holding the final set for each zLeve
//IntervalT2MF_Interface[] temp = new IntervalT2MF_Interface[1];
        //set the flag
        boolean agreementFoundFlag = true;  //flag to check when nomore intersections between a certain number of sets are found.

        //add an empty zLevel 0 for consistency (so zLevel 1 is at 1 in the Vector)
        //zLevelsRaw.add(null);
        //zLevelsFinal.add(null);   //skipped for efficiency and compatibility with standard zSlices

        for(int zLevel=0;(agreementFoundFlag&&(zLevel<sets.length));zLevel++)
        {
            if(SHOWCONTEXT)System.out.println("zLevel = "+zLevel);
            //for the first zLevel, take all opinions together, for the second
            //one, look for the agreement between two, for the third...
            if(zLevel==0)
            {
                zLevelsRaw.add(sets);
                zLevelsFinal.add(new IntervalT2MF_Union(sets));
                //zLevelsFinal.add(computeUnion(sets));
            }
            else
            {
                IntervalT2MF_Interface[] intersections = computeIntersections(zLevelsRaw.elementAt(0),zLevelsRaw.elementAt(zLevel-1));
                if(intersections.length!=0)
                {
                    zLevelsRaw.add(intersections);
                    zLevelsFinal.add(new IntervalT2MF_Union(zLevelsRaw.elementAt(zLevel)));
                    //zLevelsFinal.add(computeUnion(zLevelsRaw.elementAt(zLevel)));
                }
                else
                    agreementFoundFlag=false;
            }

        }

         *
         */

        Map<Integer, IntervalT2MF_Interface> zLevelsFinal = new TreeMap();

        for (int i=0; i<sets.length; i++) {
            zLevelsFinal.put(i,sets[i]);
        }

//        System.out.println(zLevelsFinal);
        return new AgreementMF_zMFs(name, zLevelsFinal);
    }

    /**
     * Computes the intersections between an array of interval type-2 source and
     * target sets by computing the intersections between all combinations of
     * sets. The resulting intersections are returned as an array.
     * This method optimises the computation of the intersection compared to the
     * brute force approach of intersecting all sets by omitting redundancies.
     * @param sourceSets The actual sets from the source (i.e. basic interval type-2)
     * @param targetSets The sets from previous zlevels: potentially already intersections
     * @return An array of intersections which has been pruned (only
     * intersection sets which resulted in an non-empty set are included.
     */
    private IntervalT2MF_Interface[] computeIntersections(IntervalT2MF_Interface[] sourceSets, IntervalT2MF_Interface[] targetSets)
    {
        Vector<IntervalT2MF_Intersection> intersections = new Vector();
        IntervalT2MF_Intersection intersection;

        for (int currentSourceSet=0;currentSourceSet<sourceSets.length;currentSourceSet++)
        {
            int sourceSetHashcode = ((IntervalT2MF_Interface)sourceSets[currentSourceSet]).hashCode();

            for(int currentTargetSet=0;currentTargetSet<targetSets.length;currentTargetSet++)
            {//System.out.println("currentSourceSet = "+currentSourceSet+"    currentTargetSet = "+currentTargetSet);
                try{
                    //check if the target set has already been intersected with the source set.
                    //if yes, than there is no point in intersecting and adding it again...
                    //if(!((IntervalT2MF_Intersection)targetSets[currentTargetSet]).containsSet(sourceSets[currentSourceSet]))

                    //System.out.println("Source name: "+((IntervalT2MF_Interface)sourceSets[currentSourceSet]).getName()+"  hcode: "+this.hashCode());
                    if(!((IntervalT2MF_Intersection)targetSets[currentTargetSet]).containsSet(sourceSets[currentSourceSet]))
                    {
                        if(!detectMatchingIntersection(sourceSets[currentSourceSet], targetSets[currentTargetSet], intersections))
                        {
                            //System.out.println("Adding intersection between "+sourceSets[currentSourceSet].getName()+"   and   "+targetSets[currentTargetSet].getName());
                            intersection  = new IntervalT2MF_Intersection(sourceSets[currentSourceSet], targetSets[currentTargetSet]);
                            if(intersection.intersectionExists())
                                intersections.add(intersection);
                        }
                    }//else System.out.println("We have found that these sets had already been intersected.");
                }catch(ClassCastException cce)
                {
                    //if it wasnt an intersection at all we can proceed... (avoid intersection with oneself)
                    if (sourceSetHashcode!=targetSets[currentTargetSet].hashCode())
                    {
                        if(!detectMatchingIntersection(sourceSets[currentSourceSet], targetSets[currentTargetSet], intersections))
                        {
                            //System.out.println("(E)Adding intersection between "+sourceSets[currentSourceSet].getName()+"   and   "+targetSets[currentTargetSet].getName());
                            intersection  = new IntervalT2MF_Intersection(sourceSets[currentSourceSet], targetSets[currentTargetSet]);
                            if(intersection.intersectionExists())
                            {
                                intersections.add(intersection);
                            }
                        }
                    }
                }
            }
        }
        IntervalT2MF_Interface[] returnValue = new IntervalT2MF_Interface[intersections.size()];
        return intersections.toArray(returnValue);
        //return (IntervalT2MF_Interface[])intersections.toArray();
    }

//    private IntervalT2MF_Interface computeUnion(IntervalT2MF_Interface[] sets)
//    {
//        if(sets.length==0)
//            return null;
//        else
//        if (sets.length==1)
//            return sets[0];
//
//        IntervalT2MF_Union union = new IntervalT2MF_Union(sets[0], sets[1]);
//        for(int currentSet=2;currentSet<sets.length;currentSet++)
//        {
//            union = new IntervalT2MF_Union(union, sets[currentSet]);
//            //System.out.println("computed union for set "+currentSet+" (out of "+sets.length+")");
//        }
//        return union;
//    }

    /**
     * Checks an existing vector of intersections and returns true if the vector
     * already contains an intersection instance of the source and target sets.
     * @param sourceSet
     * @param targetSet
     * @param intersections
     * @return
     */
    private boolean detectMatchingIntersection(IntervalT2MF_Interface sourceSet, IntervalT2MF_Interface targetSet, Vector<IntervalT2MF_Intersection> intersections)
    {

        for(IntervalT2MF_Intersection inter : intersections)
        {
            if(inter.containsSet(sourceSet)&&inter.containsSet(targetSet))
                    return true;
        }
        return false;
    }

}
