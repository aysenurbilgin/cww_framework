/*
 * Copyright (c) 2015 Aysenur Bilgin
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package activity;

import cbr.Case;
import cbr.Solution;
import cbr.Value;
import fcc.*;
import fuzzylogic.generic.Tuple;
import fuzzylogic.inference.RuleAntecedent;
import fuzzylogic.inference.RuleAntecedentGroup;
import fuzzylogic.inference.RuleConsequentGroup;
import fuzzylogic.type1.T1MF_Trapezoidal;
import fuzzylogic.type2.core.LGT2_EIA;
import fuzzylogic.type2.core.LVDesignerExpert;
import fuzzylogic.type2.interval.IntervalT2MF_Trapezoidal;
import fuzzylogic.type2.zSlices.AgreementEngine;
import fuzzylogic.type2.zSlices.AgreementMF_zMFs;
import gui.Utility;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author abilgin
 */
public class FuzzyCompositeConceptDifficulty extends Word implements Serializable {
    
    private ArrayList<AgreementMF_zMFs[]> inputconceptSets;
    private ArrayList<String> conceptNames;
    //september 2013 changes - sets need to accumulate, but when positive and negative effects change
    private ArrayList<String> positiveEffect;
    private ArrayList<String> negativeEffect;
    private ArrayList<FCCDesignerSD> dimensions;
    private AgreementMF_zMFs[] compositeConceptSets;

    private int numberofslices;
    
    private FCCRuleBasePackage fccrbp;
    private FCCInference ie;
    
    private boolean established = false;
    private final String type = "z";
    
    public FuzzyCompositeConceptDifficulty(String linguisticvariable, String negperc, String posperc) {
        
        super(linguisticvariable, negperc, posperc);
            
        this.fccrbp = new FCCRuleBasePackage();

        prepareSets();
        boolean existRuleBase = fccrbp.setupRuleBase(type+this.getConceptLinguisticVariableName()+"fccrulebase");

        if (!existRuleBase) {
            createRuleBase(type+this.getConceptLinguisticVariableName()+"fccrulebase");
        }

//        System.out.println("Rulebase is : " + fccrbp.getRulebase().toString());

        positiveEffect = new ArrayList<String>();
        negativeEffect = new ArrayList<String>();

        analyzeRulebase();

        //set the inference engine
        ie = new FCCInference(inputconceptSets, numberofslices, positiveEffect, negativeEffect, type);

    }
    
    public AgreementMF_zMFs[] getCompositeConceptSets() {
        return compositeConceptSets;
    }

    boolean getEstablishedStatus() {
        return this.established;
    }
    
    FCCRuleBasePackage getRBInstance() {
        return this.fccrbp;
    }
    
    public ArrayList<AgreementMF_zMFs[]> getInputConceptSets() {
        return this.inputconceptSets;
    }
    
    public ArrayList<String> getConceptNames() {
        return this.conceptNames;
    }
    
    int getNumberofSlices() {
        return this.numberofslices;
    }
    
    public ArrayList<String> getNegativeEffects() {
        return this.negativeEffect;
    }
    
    public ArrayList<String> getPositiveEffects() {
        return this.positiveEffect;
    }

    //prepare the input concept sets here
    private void prepareSets() {

        this.inputconceptSets = new ArrayList<AgreementMF_zMFs[]>();
        this.conceptNames = new ArrayList<String>();

        if (Utility.usingIntervalT2) {

            this.numberofslices = 1;
//            generateIntervalSetsExpert();
            generateIntervalSetsEIA();

        }
        else if (Utility.usingLGT2_EIA) {
            LGT2_EIA expert1 = new LGT2_EIA("eiadataLGT2preptime.csv", "preparation time", "short", "long");
            LGT2_EIA expert2 = new LGT2_EIA("eiadataLGT2cooktime.csv", "cooking time", "quick", "slow");
            LGT2_EIA expert3 = new LGT2_EIA("eiadataLGT2overalltime.csv", "overall time", "little", "big");

            inputconceptSets = new ArrayList<AgreementMF_zMFs[]>();

            inputconceptSets.add(expert1.getAgreementSets());
            inputconceptSets.add(expert2.getAgreementSets());
            inputconceptSets.add(expert3.getAgreementSets());

            conceptNames = new ArrayList<String>();

            conceptNames.add(expert1.getLingVarName());
            conceptNames.add(expert2.getLingVarName());
            conceptNames.add(expert3.getLingVarName());

            numberofslices = 5;

        }
        else {

            ArrayList<String> filesfordim1 = new ArrayList<String>();

            filesfordim1.add("." + File.separator + "data" + File.separator + "bbcfooddinner.csv");

            FCCDesignerSD dim1 = new FCCDesignerSD(filesfordim1);

            ArrayList<String> filesfordim2 = new ArrayList<String>();

            filesfordim2.add("." + File.separator + "data" + File.separator + "bbcfoodbreakfast.csv");

            FCCDesignerSD dim2 = new FCCDesignerSD(filesfordim2);

            this.dimensions = new ArrayList<FCCDesignerSD>();

            dimensions.add(dim1);
            dimensions.add(dim2);

            ConceptGroup cong = new ConceptGroup(dimensions);

            cong.formZsliceSets();

            inputconceptSets = cong.getAgreementSets();

//            cong.visualizeConcepts();

            conceptNames = cong.getConceptNames();

            numberofslices = cong.getNumberofSlices();

        }

    }
    
    public synchronized void reDesignInputConcept(String nameOfConcept, double newVal) {

        //pick a random dimension and add the new value
        int randomNo = 0;
        this.dimensions.get(randomNo).reorganizeToAdapt(nameOfConcept, newVal);
        
        ConceptGroup cong = new ConceptGroup(dimensions);
        cong.formZsliceSets();
        
        inputconceptSets = cong.getAgreementSets();
        
//        cong.visualizeConcepts();
        
        numberofslices = cong.getNumberofSlices();
        
        //redesign the rulebase
        this.fccrbp.resetRulebase();

        boolean existRuleBase = fccrbp.setupRuleBase(type+this.getConceptLinguisticVariableName()+"fccrulebase");

        if (!existRuleBase) {
            createRuleBase(type+this.getConceptLinguisticVariableName()+"fccrulebase");
        }
        
        //redesign the fcc inference engine
        ie = null;
        //september 2013 analyze the rulebase again
        analyzeRulebase();
        ie = new FCCInference(inputconceptSets, numberofslices, positiveEffect, negativeEffect, type);
        
    }
    
    private synchronized void createRuleBase(String name) {

        //input sequence is preparation time, cooking time
        if (this.type.contains("it2")) {
            
            int counter = 0; //inputconceptSets.size();
            AgreementMF_zMFs[] firstsetgroup = inputconceptSets.get(counter);
            counter ++;
            AgreementMF_zMFs[] secondsetgroup = inputconceptSets.get(counter);


            for (AgreementMF_zMFs aFirstsetgroup : firstsetgroup) {

                for (AgreementMF_zMFs aSecondsetgroup : secondsetgroup) {

                    if (aSecondsetgroup.getName().contains("quick") || aSecondsetgroup.getName().contains("extremely")) {
                        fccrbp.populateRule(new String[]{aFirstsetgroup.getName(), aSecondsetgroup.getName()}, "easy", inputconceptSets, conceptNames, super.getLinguisticVariableName());
                    } else if (aFirstsetgroup.getName().contains("long")) {
                        fccrbp.populateRule(new String[]{aFirstsetgroup.getName(), aSecondsetgroup.getName()}, "challenging", inputconceptSets, conceptNames, super.getLinguisticVariableName());
                    } else if (aFirstsetgroup.getName().contains("short")) {
                        fccrbp.populateRule(new String[]{aFirstsetgroup.getName(), aSecondsetgroup.getName()}, "easy", inputconceptSets, conceptNames, super.getLinguisticVariableName());
                    } else {
                        fccrbp.populateRule(new String[]{aFirstsetgroup.getName(), aSecondsetgroup.getName()}, "challenging", inputconceptSets, conceptNames, super.getLinguisticVariableName());
                    }
                }
            }

            return;
        }
        
        fccrbp.writeRuleBasetoFile(name);
     
    }
    
    
    //function analyzes the rulebase according to the labels of the output
    //the aim is to find the positive affecting labels and negative affecting labels
    //depends highly on the opinion
    private void analyzeRulebase() {
        
        Map<RuleConsequentGroup, Map<String, Map<String, Integer>>> compositeBubbles = new TreeMap<RuleConsequentGroup, Map<String, Map<String, Integer>>>();
        boolean zeroEffect = false;
        
        if (this.type.contains("it2")) {
            
            this.negativeEffect.add("long");
            this.negativeEffect.add("slow");
            
            this.positiveEffect.add("short");
            this.positiveEffect.add("quick");
            
            return;
        }
        
        //find the concept bubbles
        for (Map.Entry<RuleAntecedentGroup, ArrayList<RuleConsequentGroup>> entry: this.fccrbp.getRulebase().entrySet()) {
            
            Map<String, Map<String, Integer>> conceptoccurrences = new TreeMap<String, Map<String, Integer>>();

            for (RuleConsequentGroup rcg : entry.getValue()) {
                if (!compositeBubbles.containsKey(rcg)) {

                    for (RuleAntecedent ra : entry.getKey().getAllAntecedents()) {
                        Map<String, Integer> individuallabels = new TreeMap<String, Integer>();

                        if (!conceptoccurrences.containsKey(ra.getName())) {
                            individuallabels.put(ra.getLabel(), 1);
                        }
                        else {
                            individuallabels = conceptoccurrences.get(ra.getName());
                            individuallabels.put(ra.getLabel(), individuallabels.get(ra.getLabel()) + 1);
                        }
                        conceptoccurrences.put(ra.getName(), individuallabels);
                    }

                } else if (compositeBubbles.containsKey(rcg)) {
                    conceptoccurrences = compositeBubbles.get(rcg);

                    for (RuleAntecedent ra : entry.getKey().getAllAntecedents()) {
                        Map<String, Integer> individuallabels = new TreeMap<String, Integer>();

                        if (!conceptoccurrences.containsKey(ra.getName())) {
                            individuallabels.put(ra.getLabel(), 1);
                        }
                        else {
                            individuallabels = conceptoccurrences.get(ra.getName());

                            if (!individuallabels.containsKey(ra.getLabel())) {
                                individuallabels.put(ra.getLabel(), 0);
                            }
                            individuallabels.put(ra.getLabel(), individuallabels.get(ra.getLabel()) + 1);
                        }
                        conceptoccurrences.put(ra.getName(), individuallabels);
                    }
                }
                compositeBubbles.put(rcg, conceptoccurrences);
            }
        }

        for (Map.Entry<RuleConsequentGroup, Map<String, Map<String, Integer>>> entry: compositeBubbles.entrySet()) {
        
            //for each key in composite bubbles retrieve the value
            Map<String, Map<String, Integer>> conceptnamelist = entry.getValue();
            ArrayList<String> one = new ArrayList<String>();
            
            for (String str: conceptnamelist.keySet()) {
                Map<String, Integer> occurrences = conceptnamelist.get(str);

                int max = 0;
                int sum = 0;
                for (String maxs: occurrences.keySet()) {
                    if (occurrences.get(maxs) >= max) {
                        max = occurrences.get(maxs);
                    }
                    sum += occurrences.get(maxs);
                }
                
                if ((occurrences.size()>1) && (max == sum/occurrences.size())) {
                    //means that both labels have equal effect so a zero effect
                    zeroEffect = true;
                }
                else {
                    zeroEffect = false;
                    for (String ms: occurrences.keySet()) {
                        if (occurrences.get(ms) == max) {
                            one.add(ms);
                        }
                    }
                }
            }
            
            if (entry.getKey().getAllConsequents().get(0).getLabel().contentEquals(this.getNegPerception())) {
                if (zeroEffect) {
                    //september 2013 to be able to maintain the model from getting destructed accumulation of history
                    //for the zero effect make use of history
                    if (!Utility.negativeEffectHistory.isEmpty()) {
                        for (String element : Utility.negativeEffectHistory) {
                            if (!negativeEffect.contains(element)) {
                                negativeEffect.add(element);
                            }
                        }
                    }
                }
                else {
                    for (String element : one) {
                        if (!negativeEffect.contains(element)) {
                            negativeEffect.add(element);
                        }
                    }
                }
                Utility.negativeEffectHistory = new ArrayList<String>();
                Utility.negativeEffectHistory.addAll(negativeEffect);
            }
            else {
                if (zeroEffect) {
                    //september 2013 to be able to maintain the model from getting destructed accumulation of history
                    //for the zero effect make use of history
                    if (!Utility.positiveEffectHistory.isEmpty()) {
                        for (String element : Utility.positiveEffectHistory) {
                            if (!positiveEffect.contains(element)) {
                                positiveEffect.add(element);
                            }
                        }
                    }
                }
                else {
                    for (String element : one) {
                        if (!positiveEffect.contains(element)) {
                            positiveEffect.add(element);
                        }
                    }
                }
                Utility.positiveEffectHistory = new ArrayList<String>();
                Utility.positiveEffectHistory.addAll(positiveEffect);
            }
        }// find pos and neg effects
    }

    private void generateIntervalSetsEIA() {

        //for each concept
        //cooking time - support: 0.0 - 1.0
        this.inputconceptSets.add(interval_initEIA(new String[]{"quick", "slow"}));
        this.conceptNames.add("cooking time");

        //preparation time - support: 0.0 - 1.0
        this.inputconceptSets.add(interval_initEIA(new String[]{"short", "long"}));
        this.conceptNames.add("preparation time");

        //overall time - support: 0.0 - 1.0
        this.inputconceptSets.add(interval_initEIA(new String[]{"little", "big"}));
        this.conceptNames.add("overall time");

    }

    private AgreementMF_zMFs[] interval_initEIA(String[] labels) {

        int numberSlice = 1;

        T1MF_Trapezoidal extremely_left_upper, very_left_upper, left_upper, right_upper, very_right_upper, extremely_right_upper;
        T1MF_Trapezoidal extremely_left_lower, very_left_lower, left_lower, right_lower, very_right_lower, extremely_right_lower;

        IntervalT2MF_Trapezoidal[] interval_extremely_left;
        IntervalT2MF_Trapezoidal[] interval_very_left;
        IntervalT2MF_Trapezoidal[] interval_left;
        IntervalT2MF_Trapezoidal[] interval_right;
        IntervalT2MF_Trapezoidal[] interval_very_right;
        IntervalT2MF_Trapezoidal[] interval_extremely_right;

        /*
        short
        0	0	0.183503419	2.632993162	0	0	0.09175171	1.316496581	1
        0.378679656	1.5	2.5	4.621320344	0.792893219	1.75	1.75	2.207106781	0.646446609
        1.378679656	3	4	5.621320344	2.585786438	3.5	3.5	4.414213562	0.646446609
        3.378679656	5	6.5	8.621320344	4.585786438	5.6	5.6	6.414213562	0.575735931
        6.585786438	7.5	8	9.414213562	6.792893219	7.666666667	7.666666667	8.207106781	0.76429774
        7.367006838	9.816496581	10	10	8.683503419	9.90824829	10	10	1

        quick
        0	0	0.275255129	3.949489743	0	0	0.09175171	1.316496581	1
        0.585786438	1.5	2	3.414213562	0.792893219	1.666666667	1.666666667	2.207106781	0.76429774
        1.171572875	3	4.5	6.828427125	2.792893219	3.6	3.6	4.207106781	0.575735931
        3.378679656	5.5	6.5	7.621320344	5.792893219	6.25	6.25	7.207106781	0.646446609
        5.378679656	7	8	9.621320344	6.792893219	7.5	7.5	8.207106781	0.646446609
        7.367006838	9.816496581	10	10	8.683503419	9.90824829	10	10	1

        little
        0	0	0.183503419	2.632993162	0	0	0.09175171	1.316496581	1
        0.378679656	2	3	4.621320344	1.792893219	2.5	2.5	3.207106781	0.646446609
        1.378679656	3	4	5.621320344	2.585786438	3.5	3.5	4.414213562	0.646446609
        3.378679656	5.5	7	8.621320344	5.792893219	6.4	6.4	7.207106781	0.575735931
        6.585786438	7.5	8	9.414213562	6.792893219	7.666666667	7.666666667	8.207106781	0.76429774
        7.367006838	9.816496581	10	10	8.683503419	9.90824829	10	10	1
        */


        if (labels[0].contains("short")) {

            extremely_left_upper = new T1MF_Trapezoidal("extremely_" + labels[0] + "_upper", new double[]{0, 0, 0.183503419, 2.632993162});
            very_left_upper = new T1MF_Trapezoidal("very_" + labels[0] + "_upper", new double[]{0.378679656, 1.5, 2.5, 4.621320344});
            left_upper = new T1MF_Trapezoidal(labels[0] + "_upper", new double[]{1.378679656, 3, 4, 5.621320344});
            right_upper = new T1MF_Trapezoidal(labels[1] + "_upper", new double[]{3.378679656, 5, 6.5, 8.621320344});
            very_right_upper = new T1MF_Trapezoidal("very_" + labels[1] + "_upper", new double[]{6.585786438, 7.5, 8, 9.414213562});
            extremely_right_upper = new T1MF_Trapezoidal("extremely_" + labels[1] + "_upper", new double[]{7.367006838, 9.816496581, 10, 10});

            extremely_left_lower = new T1MF_Trapezoidal("extremely_" + labels[0] + "_lower", new double[]{0, 0, 0.09175171, 1.316496581}, new double[]{1.0, 1.0});
            very_left_lower = new T1MF_Trapezoidal("very_" + labels[0] + "_lower", new double[]{0.792893219, 1.75, 1.75, 2.207106781}, new double[]{0.646446609, 0.646446609});
            left_lower = new T1MF_Trapezoidal(labels[0] + "_lower", new double[]{2.585786438, 3.5,	3.5,	4.414213562}, new double[]{0.646446609, 0.646446609});
            right_lower = new T1MF_Trapezoidal(labels[1] + "_lower", new double[]{4.585786438,	5.6,	5.6,	6.414213562	}, new double[]{0.575735931, 0.575735931});
            very_right_lower = new T1MF_Trapezoidal("very_" + labels[1] + "_lower", new double[]{6.792893219, 7.666666667, 7.666666667, 8.207106781	}, new double[]{0.76429774, 0.76429774});
            extremely_right_lower = new T1MF_Trapezoidal("extremely_" + labels[1] + "_lower", new double[]{8.683503419,	9.90824829,	10,	10}, new double[]{1.0, 1.0});

        }
        else if (labels[0].contains("quick")) {

            extremely_left_upper = new T1MF_Trapezoidal("extremely_" + labels[0] + "_upper", new double[]{0,	0,	0.275255129,	3.949489743});
            very_left_upper = new T1MF_Trapezoidal("very_" + labels[0] + "_upper", new double[]{0.585786438,	1.5,	2,	3.414213562});
            left_upper = new T1MF_Trapezoidal(labels[0] + "_upper", new double[]{1.171572875,	3,	4.5,	6.828427125});
            right_upper = new T1MF_Trapezoidal(labels[1] + "_upper", new double[]{3.378679656,	5.5,	6.5,	7.621320344});
            very_right_upper = new T1MF_Trapezoidal("very_" + labels[1] + "_upper", new double[]{5.378679656,	7,	8,	9.621320344});
            extremely_right_upper = new T1MF_Trapezoidal("extremely_" + labels[1] + "_upper", new double[]{7.367006838,	9.816496581,	10,	10});

            extremely_left_lower = new T1MF_Trapezoidal("extremely_" + labels[0] + "_lower", new double[]{0,	0,	0.09175171,	1.316496581}, new double[]{1.0, 1.0});
            very_left_lower = new T1MF_Trapezoidal("very_" + labels[0] + "_lower", new double[]{0.792893219,	1.666666667,	1.666666667,	2.207106781	}, new double[]{0.76429774, 0.76429774});
            left_lower = new T1MF_Trapezoidal(labels[0] + "_lower", new double[]{2.792893219,	3.6,	3.6,	4.207106781	}, new double[]{0.575735931,0.575735931});
            right_lower = new T1MF_Trapezoidal(labels[1] + "_lower", new double[]{5.792893219,	6.25,	6.25,	7.207106781	}, new double[]{0.646446609, 0.646446609});
            very_right_lower = new T1MF_Trapezoidal("very_" + labels[1] + "_lower", new double[]{6.792893219,	7.5,	7.5,	8.207106781	}, new double[]{0.646446609, 0.646446609});
            extremely_right_lower = new T1MF_Trapezoidal("extremely_" + labels[1] + "_lower", new double[]{8.683503419,	9.90824829,	10,	10}, new double[]{1.0, 1.0});

        }
        else {

            extremely_left_upper = new T1MF_Trapezoidal("extremely_" + labels[0] + "_upper", new double[]{0,	0,	0.183503419,	2.632993162});
            very_left_upper = new T1MF_Trapezoidal("very_" + labels[0] + "_upper", new double[]{0.378679656,	2,	3,	4.621320344});
            left_upper = new T1MF_Trapezoidal(labels[0] + "_upper", new double[]{1.378679656,	3,	4,	5.621320344});
            right_upper = new T1MF_Trapezoidal(labels[1] + "_upper", new double[]{3.378679656,	5.5	,7,	8.621320344});
            very_right_upper = new T1MF_Trapezoidal("very_" + labels[1] + "_upper", new double[]{6.585786438,	7.5,	8,	9.414213562});
            extremely_right_upper = new T1MF_Trapezoidal("extremely_" + labels[1] + "_upper", new double[]{7.367006838,	9.816496581,	10,	10});

            extremely_left_lower = new T1MF_Trapezoidal("extremely_" + labels[0] + "_lower", new double[]{0, 0, 0.09175171,	1.316496581}, new double[]{1.0, 1.0});
            very_left_lower = new T1MF_Trapezoidal("very_" + labels[0] + "_lower", new double[]{1.792893219,	2.5,	2.5,	3.207106781	}, new double[]{0.646446609, 0.646446609});
            left_lower = new T1MF_Trapezoidal(labels[0] + "_lower", new double[]{2.585786438,	3.5,	3.5,	4.414213562	}, new double[]{0.646446609, 0.646446609});
            right_lower = new T1MF_Trapezoidal(labels[1] + "_lower", new double[]{5.792893219,	6.4,	6.4, 7.207106781	}, new double[]{0.575735931, 0.575735931});
            very_right_lower = new T1MF_Trapezoidal("very_" + labels[1] + "_lower", new double[]{6.792893219,	7.666666667,	7.666666667,	8.207106781	}, new double[]{0.76429774, 0.76429774});
            extremely_right_lower = new T1MF_Trapezoidal("extremely_" + labels[1] + "_lower", new double[]{8.683503419,	9.90824829	,10	,10}, new double[]{1.0, 1.0});

        }

        interval_extremely_left = new IntervalT2MF_Trapezoidal[numberSlice];
        interval_very_left = new IntervalT2MF_Trapezoidal[numberSlice];
        interval_left = new IntervalT2MF_Trapezoidal[numberSlice];
        interval_right = new IntervalT2MF_Trapezoidal[numberSlice];
        interval_very_right = new IntervalT2MF_Trapezoidal[numberSlice];
        interval_extremely_right = new IntervalT2MF_Trapezoidal[numberSlice];

        for(int currentSet = 0; currentSet < numberSlice; currentSet++) {
            interval_extremely_left[currentSet] = new IntervalT2MF_Trapezoidal("extremely_"+labels[0]+"_IT2_Level"+(currentSet+1), extremely_left_lower, extremely_left_upper);
            interval_very_left[currentSet] = new IntervalT2MF_Trapezoidal("very_"+labels[0]+"_IT2_Level"+(currentSet+1), very_left_lower, very_left_upper);
            interval_left[currentSet] = new IntervalT2MF_Trapezoidal(labels[0]+"_IT2_Level"+(currentSet+1), left_lower, left_upper);
            interval_right[currentSet] = new IntervalT2MF_Trapezoidal(labels[1]+"_IT2_Level"+(currentSet+1), right_lower, right_upper);
            interval_very_right[currentSet] = new IntervalT2MF_Trapezoidal("very_"+labels[1]+"_IT2_Level"+(currentSet+1), very_right_lower, very_right_upper);
            interval_extremely_right[currentSet] = new IntervalT2MF_Trapezoidal("extremely_"+labels[1]+"_IT2_Level"+(currentSet+1), extremely_right_lower, extremely_right_upper);
        }

        AgreementEngine aE = new AgreementEngine();
        AgreementMF_zMFs agreement_extremely_left = aE.findAgreement("extremely "+labels[0], interval_extremely_left);
        AgreementMF_zMFs agreement_very_left = aE.findAgreement("very "+labels[0], interval_very_left);
        AgreementMF_zMFs agreement_left = aE.findAgreement(labels[0], interval_left);
        AgreementMF_zMFs agreement_right = aE.findAgreement(labels[1], interval_right);
        AgreementMF_zMFs agreement_very_right = aE.findAgreement("very "+labels[1], interval_very_right);
        AgreementMF_zMFs agreement_extremely_right = aE.findAgreement("extremely "+labels[1], interval_extremely_right);

        return new AgreementMF_zMFs[]{agreement_extremely_left, agreement_very_left, agreement_left, agreement_right, agreement_very_right, agreement_extremely_right};

    }
    
    Map<RuleAntecedentGroup, ArrayList<RuleConsequentGroup>> getRulebase() {
        return this.fccrbp.getRulebase();
    }
    
    int getRulebaseSize() {
        return this.fccrbp.getRulebaseSize();
    }

    public Tuple evaluateConceptfor(ArrayList<Double> inpVals) {
        return this.ie.newevaluateMO(inpVals, this.getRulebase());
    }
    
    String getConceptLinguisticVariableName() {
        return super.getLinguisticVariableName();
    }
    
    public AgreementMF_zMFs[] establishCompositeSets() {

        Map<String, Double> outputword = new TreeMap<String, Double>();
        double pPositive = 0.0, pNegative = 0.0;
        int count = 0;
                
        //get all the cases and put them through inference to count the number of concepts
        Activity ind = Utility.PUBLIC_BASE.lookForActivity("cook");
        for (Case c: Utility.PUBLIC_BASE.getCasebase().get(ind)) {
            //establish the concept according to all of the solutions
            for (Solution s : c.getSolutions()) {
                count++;
                //get the mapping for this solution
                Map<String, Double> m = this.ie.findFiringProportions(s.getSolutionFValues(), this.getRulebase());

                for (String ms : m.keySet()) {
                    String[] split = ms.split(" ", 2);
                    if (split.length > 1) {
                        if (!outputword.containsKey(split[1])) {
                            outputword.put(split[1], 0.0);
                        }
                        outputword.put(split[1], outputword.get(split[1]) + m.get(split[1]));
                    } else {
                        if (!outputword.containsKey(ms)) {
                            outputword.put(ms, 0.0);
                        }
                        outputword.put(ms, outputword.get(ms) + m.get(ms));
                    }
                }
            }
        }

        //we have all the occurrences of the output
        //divide them by the number of cases and assign to proportions
        for (String s: outputword.keySet()) {
            if (super.isPositive(s)) {
                pPositive = outputword.get(s)/(count*this.getRulebaseSize());
            }
            else if (super.isNegative(s)) {
                pNegative = outputword.get(s)/(count*this.getRulebaseSize());
            }
        }

        //there are 3 possibilities of proportions
        //ideal case: when the sum of proportions are smaller than 1.0
        double sumProp = pPositive + pNegative;
        LVDesignerExpert composite;
        if (sumProp <= 1.0) {
            //get the overlap and spread it
            double right = 1.0 - pPositive;
            if ((right-pNegative) > 0.1){
                System.out.println("Composite set establishment successful!");
                composite = new LVDesignerExpert(super.getNegPerception(), super.getPosPerception(), false, pNegative, right);
            }
            else {
                composite = new LVDesignerExpert(super.getNegPerception(), super.getPosPerception(), false, pNegative - 0.05 , right + 0.05);
            }
        }
        else {
            //use the default settings which is the middle way
            System.err.println("FCCDIFFICULTY: Fatal error: Right+Left cannot be bigger than 1");
            return null;
        }
        
        composite.formZsliceSets();
        
        this.compositeConceptSets = composite.getFuzzySets();
        this.established = true;
        
        return this.compositeConceptSets;
        
    }

    public AgreementMF_zMFs[] establishCompositeSets2(double overallTime) {

        Map<String, Double> outputword = new TreeMap<String, Double>();
        double pPositive = 0.0, pNegative = 0.0;
        int count = 0;

        //get all the cases and put them through inference to count the number of concepts
        Activity ind = Utility.PUBLIC_BASE.lookForActivity("cook");
        for (Case c: Utility.PUBLIC_BASE.getCasebase().get(ind)) {
            //establish the concept according to all of the solutions
            for (Solution s : c.getSolutions()) {
                count++;

                ArrayList<Double> inpVals = new ArrayList<Double>();
                inpVals.addAll(s.getSolutionFValues());
                inpVals.add(overallTime);
                Map<String, Double> m = this.ie.findFiringProportions(inpVals, this.getRulebase());

                for (String ms : m.keySet()) {
                    String[] split = ms.split(" ", 2);
                    if (split.length > 1) {
                        if (!outputword.containsKey(split[1])) {
                            outputword.put(split[1], 0.0);
                        }
                        outputword.put(split[1], outputword.get(split[1]) + m.get(split[1]));
                    } else {
                        if (!outputword.containsKey(ms)) {
                            outputword.put(ms, 0.0);
                        }
                        outputword.put(ms, outputword.get(ms) + m.get(ms));
                    }
                }
            }
        }

        //we have all the occurrences of the output
        //divide them by the number of cases and assign to proportions
        for (String s: outputword.keySet()) {
            if (super.isPositive(s)) {
                pPositive = outputword.get(s)/(count*this.getRulebaseSize());
            }
            else if (super.isNegative(s)) {
                pNegative = outputword.get(s)/(count*this.getRulebaseSize());
            }
        }

        //there are 3 possibilities of proportions
        //ideal case: when the sum of proportions are smaller than 1.0
        double sumProp = pPositive + pNegative;
        LVDesignerExpert composite;
        if (sumProp <= 1.0) {
            //get the overlap and spread it
            double right = 1.0 - pPositive;
            if ((right-pNegative) > 0.1){
                System.out.println("Composite set establishment successful!");
                composite = new LVDesignerExpert(super.getNegPerception(), super.getPosPerception(), false, pNegative, right);
            }
            else {
                //predefined FOU
                if (pNegative < 0.05) {
                    composite = new LVDesignerExpert(super.getNegPerception(), super.getPosPerception(), false, pNegative , right);
                }
                else {
                    composite = new LVDesignerExpert(super.getNegPerception(), super.getPosPerception(), false, pNegative - 0.05 , right + 0.05);
                }
            }
        }
        else {
            //use the default settings which is the middle way
            System.err.println("FCCDIFFICULTY: Fatal error: Right+Left cannot be bigger than 1");
            return null;
        }

        composite.formZsliceSets();

        this.compositeConceptSets = composite.getFuzzySets();
        this.established = true;

        return this.compositeConceptSets;

    }
    
    public synchronized AgreementMF_zMFs[] getCompositeSets() {
        
        if (!established) {
            return establishCompositeSets();
        }
        else {
            return this.compositeConceptSets;
        }
    }

    //september 2013 adding overall time
    public synchronized AgreementMF_zMFs[] getCompositeSets2(double overallTime) {

        if (!established) {
            return establishCompositeSets2(overallTime);
        }
        else {
            return this.compositeConceptSets;
        }
    }

    public void performLearningUsingNumbers(double overalltime, String cooktime, String preptime, String difficultyLevel) {

        //the user has specified feedback questions and answers make up rules
        //check whether the answers in numbers match with the words of feedback using the sets
        //assume they are the same so find the linguistic labels and modify the rulebase
        AgreementMF_zMFs difficultySet = findMF(difficultyLevel);
        AgreementMF_zMFs cooktimeSet, preptimeSet;

        ArrayList<AgreementMF_zMFs> feedbackSets = new ArrayList<AgreementMF_zMFs>();

        if (Utility.usingLGT2_EIA || Utility.usingLGT2_optimised) {
            cooktimeSet = findMF(cooktime, inputconceptSets.get(1));
            preptimeSet = findMF(preptime, inputconceptSets.get(0));
            feedbackSets.add(preptimeSet);
            feedbackSets.add(cooktimeSet);
        }
        else {
            cooktimeSet = findMF(cooktime, inputconceptSets.get(0));
            preptimeSet = findMF(preptime, inputconceptSets.get(1));
            feedbackSets.add(cooktimeSet);
            feedbackSets.add(preptimeSet);
        }

        this.fccrbp.doLearning(overalltime, feedbackSets, difficultyLevel, inputconceptSets, conceptNames, difficultySet);

        this.fccrbp.writeRuleBasetoFile(type+this.getConceptLinguisticVariableName()+"fccrulebase_"+Utility.USER_NAME);

        System.out.println("The new rulebase: ");
        System.out.println(this.fccrbp.toString());

        this.establishCompositeSets2(overalltime);

    }

    //november2013
    //added for assisting the population of pearson correlation file
    //gets the system response for a given input
    public String getSystemResponse(double stimuliInput, String cookorprep) {

        //for the input list
        //this is pure zSlice LGT2 implementation
        int index_max_input, index = 0;

        if (cookorprep.equalsIgnoreCase("cook")) {
            index = 0;
        } else {
            index = 1;
        }

        Value v = new Value(stimuliInput);
        String comparison = v.getConsensusSliceName(inputconceptSets.get(index));

        return comparison;
    }

    private AgreementMF_zMFs findMF(String str, AgreementMF_zMFs[] inputSets) {

        for (AgreementMF_zMFs fset : inputSets) {
            if (str.toLowerCase().contains(fset.getName())) {
                return fset;
            }
        }

        return null;

    }

    private AgreementMF_zMFs findMF(String difficultyLevel) {
        
        for (AgreementMF_zMFs fset : this.compositeConceptSets) {
            if (fset.getName().contains(difficultyLevel)) {
                return fset;
            }
        }
        
        return null;
        
    }

    
}
