/*
 * Copyright (c) 2015 Aysenur Bilgin
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fuzzylogic.inference;

import fuzzylogic.type2.zSlices.GenT2zMF_Trapezoidal;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author abilgin
 */
public class RuleConsequentGroup implements Serializable, Comparable {

    private ArrayList<RuleConsequent> members;

    public RuleConsequentGroup() {
        members = new ArrayList<RuleConsequent>();
    }
    
    public RuleConsequentGroup (RuleConsequentGroup rcg) {
        members = new ArrayList<RuleConsequent>();
        members.addAll(rcg.getAllConsequents());
    }

    public RuleConsequentGroup (ArrayList<RuleConsequent> mems) {
        members = new ArrayList<RuleConsequent>();
        members.addAll(mems);
    }

    public ArrayList<RuleConsequent> getAllConsequents() {
        return members;
    }

    public void setConsequent(RuleConsequent anotherOutput) {
         members.add(anotherOutput);
    }

    public RuleConsequent getConsequent(int index) {
        return members.get(index);
    }
    
    public void setMembershipFunction(int membno, GenT2zMF_Trapezoidal outSet) {
        members.get(membno).setMembershipFunction(outSet);
    }

    public int getSize() {
        return members.size();
    }
    
    public int getIndex(RuleConsequent src) {
        
        for (int i = 0; i < members.size(); i++) {
            
            if (members.get(i).getName().equalsIgnoreCase(src.getName()) && members.get(i).getLabel().equalsIgnoreCase(src.getLabel())) {
                return i;
            }
        }
        
        return -1;

    }

    @Override
    public String toString() {

        String s = "";
        String conjunction = " and ";

        for (RuleConsequent anOutput: members) {
            if (anOutput.equals(members.get(members.size()-1))) {
                conjunction = "";
            }
            s = (new StringBuilder()).append(s).append(anOutput.toString()).append(conjunction).toString();
        }
        
        //System.out.println("RuleConsequentGroup string output should be : "+s);

        return s;
    }
    
    public int compareTo(Object otherrcg) {

        if(!(otherrcg instanceof RuleConsequentGroup)){
            throw new ClassCastException("Invalid object");
        }
        
        if(this == otherrcg) {
            return 0; //same object in memory 
        }
        
        int result = 0; 
        
        for (int k = 0; k < this.getSize(); k++) {
        
            result = this.getConsequent(k).getLabel().compareTo(((RuleConsequentGroup) otherrcg).getConsequent(k).getLabel());
            
            if (result != 0) {
                break;
            }
        
        }
    
        return result;

    }


}
