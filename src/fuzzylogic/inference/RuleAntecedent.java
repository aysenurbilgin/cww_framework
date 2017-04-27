/*
 * Copyright (c) 2015 Aysenur Bilgin
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fuzzylogic.inference;

import fuzzylogic.type2.zSlices.AgreementMF_zMFs;
import java.io.Serializable;

/**
 *
 * @author abilgin
 */
public class RuleAntecedent implements Serializable, Comparable {

    private String inputName;
    private String inputLabel;
    private AgreementMF_zMFs membershipFunction;

    public RuleAntecedent() {}

    public RuleAntecedent (String name, String label) {
        inputName = name;
        inputLabel = label;
    }
    
    public RuleAntecedent(RuleAntecedent ra) {
        inputName = ra.getName();
        inputLabel = ra.getLabel();
        this.membershipFunction = ra.getMembershipFunction();
    }

    public String getName() {
        return inputName;
    }

    public void setName(String name) {
        inputName = name;
    }

    public String getLabel() {
        return inputLabel;
    }

    public void setLabel(String label) {
        inputLabel = label;
    }

    public AgreementMF_zMFs getMembershipFunction() {
        return membershipFunction;
    }

    public void setMembershipFunction(AgreementMF_zMFs function) {
        this.membershipFunction = function;
    }

    @Override
    public String toString() {

        String s = "";
        s = (new StringBuilder()).append(s).append(this.inputName).append(" is ").append(this.inputLabel).toString();
        return s;
    }
    
        
    //sort according to the label
    public int compareTo(Object ra) {

        if(!(ra instanceof RuleAntecedent)){
            throw new ClassCastException("Invalid object");
        }
        
        if(this == ra) {
            return 0; //same object in memory 
        }
        
        return this.getLabel().compareTo(((RuleAntecedent) ra).getLabel());
    }

}
