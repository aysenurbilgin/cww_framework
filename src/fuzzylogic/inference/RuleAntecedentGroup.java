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
import java.util.ArrayList;

/**
 *
 * @author abilgin
 */
public class RuleAntecedentGroup implements Serializable, Comparable {

    private ArrayList<RuleAntecedent> members;

    public RuleAntecedentGroup() {
        members = new ArrayList<RuleAntecedent>();
    }
    
    public RuleAntecedentGroup(RuleAntecedentGroup rag) {
        members = new ArrayList<RuleAntecedent>();
        members.addAll(rag.getAllAntecedents());
    }

    public RuleAntecedentGroup (ArrayList<RuleAntecedent> mems) {
        members = new ArrayList<RuleAntecedent>();
        members.addAll(mems);
    }

    public ArrayList<RuleAntecedent> getAllAntecedents() {
        return members;
    }

    public void setAntecedent(RuleAntecedent anotherInput) {
         members.add(anotherInput);
    }

    public RuleAntecedent getAntecedent(int index) {
        return members.get(index);
    }

    public void setMembershipFunction(int membno, AgreementMF_zMFs inSet) {
        members.get(membno).setMembershipFunction(inSet);
    }

    public int getSize() {
        return members.size();
    }
    
    @Override
    public String toString() {

        String s = "";
        String conjunction = " and ";
        
        ArrayList<RuleAntecedent> temp = new ArrayList<RuleAntecedent>();
        
        temp.addAll(members);

        for (RuleAntecedent anInput: temp) {
            if (anInput.equals(temp.get(temp.size()-1))) {
                conjunction = "";
            }
            s = (new StringBuilder()).append(s).append(anInput.toString()).append(conjunction).toString();
        }
        
        temp.clear();

        return s;
    }

    public int compareTo(Object otherrag) {

        if(!(otherrag instanceof RuleAntecedentGroup)){
            throw new ClassCastException("Invalid object");
        }
        
        if(this == otherrag) {
            return 0; //same object in memory 
        }
        
        int result = 0; 
        
        for (int k = 0; k < this.getSize(); k++) {
        
            result = this.getAntecedent(k).getLabel().compareTo(((RuleAntecedentGroup) otherrag).getAntecedent(k).getLabel());
            
            if (result != 0) {
                break;
            }
        
        }
    
        return result;

    }
}
