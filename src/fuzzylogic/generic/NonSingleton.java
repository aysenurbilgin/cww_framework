/*
 * Copyright (c) 2015 Aysenur Bilgin
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzylogic.generic;

import fuzzylogic.type1.core.T1Interface;

/**
 *
 * @author abilgin
 */
public interface NonSingleton {
    
    public static final int nsdiscretizationLevel = 5000;
    public static final double nstolerance = 0.0001;
    
    public Tuple getMembershipDegree(T1Interface input);
    
}
