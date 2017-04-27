/*
 * Copyright (c) 2015 Aysenur Bilgin
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fcc;

/**
 *
 * @author abilgin
 */
public interface PerceptualJudgment {
      
    
    public String getLinguisticVariableName();
    
    public String getNegPerception();
    
    public String getPosPerception();
    
    public void setLinguisticVariableName(String lv);
    
    public void setNegPerception(String np);
    
    public void setPosPerception(String pp);

    
}
