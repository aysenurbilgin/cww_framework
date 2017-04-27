/*
 * Copyright (c) 2015 Aysenur Bilgin
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzylogic.type1.core;

import fuzzylogic.generic.Tuple;

/**
 *
 * @author abilgin
 */
public interface T1Interface {
    
    public Tuple getSupport();
    
    public double getMembershipDegree(double x);
    
    public Tuple getMembershipDegree(T1Interface nsinput);

    public double[] getPoints();

    public boolean isLeftShoulder();

    public boolean isRightShoulder();
    
}
