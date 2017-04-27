/*
 * Copyright (c) 2015 Aysenur Bilgin
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fuzzylogic.inference;

import fuzzylogic.generic.Tuple;
import fuzzylogic.type2.zSlices.GenT2zMF_Prototype;
import java.io.Serializable;

/**
 *
 * @author abilgin
 */
public class RuleConsequent implements Serializable {

    private String outputName;
    private String outputLabel;
    private Tuple[] centroidInterval;
    private GenT2zMF_Prototype membershipFunction;

    public RuleConsequent() {}

    public RuleConsequent (String name, String label) {
        outputName = name;
        outputLabel = label;
    }
    
    public RuleConsequent (RuleConsequent rc) {
        outputName = rc.getName();
        outputLabel = rc.getLabel();
        this.membershipFunction = rc.getMembershipFunction();
    }

    public String getName() {
        return outputName;
    }

    public void setName(String name) {
        outputName = name;
    }

    public String getLabel() {
        return outputLabel;
    }

    public void setLabel(String label) {
        outputLabel = label;
    }

    public GenT2zMF_Prototype getMembershipFunction() {
        return membershipFunction;
    }

    public void setMembershipFunction(GenT2zMF_Prototype function) {
        this.membershipFunction = function;

    }

}
