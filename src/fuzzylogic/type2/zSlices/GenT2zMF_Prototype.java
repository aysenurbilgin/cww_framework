/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzylogic.type2.zSlices;

import fuzzylogic.generic.BadParametersException;
import fuzzylogic.generic.Tuple;
import fuzzylogic.type1.T1MF_Discretized;
import fuzzylogic.type1.T1MF_Interface;
import fuzzylogic.type2.general.zSlice.GenT2zMF_Interface;
import fuzzylogic.type2.interval.IntervalT2MF_Interface;

/*
Copyright (c) 2012,2013,2014 Christian Wagner
All rights reserved.
 */

public abstract class GenT2zMF_Prototype
  implements GenT2zMF_Interface
{
  protected IntervalT2MF_Interface[] zSlices;
  protected Tuple domain;
  protected String name;
  protected int numberOfzLevels;
  protected double z_stepSize;
  protected double[] slices_zValues;
  protected Tuple[] slices_fs;
  private final boolean DEBUG = false;

  public GenT2zMF_Prototype(String name)
  {
    this.name = name;
  }

  public Object clone()
  {
    System.err.println("Cloning for GenT2zMF_Trapezoidal needs to be re-implemented.");
    return null;
  }

    @Override
  public int getNumberOfSlices()
  {
    return this.numberOfzLevels;
  }

    @Override
  public IntervalT2MF_Interface getZSlice(int slice_number)
  {
    //System.out.println("Returning zSlice number: " + slice_number);
    if (slice_number >= getNumberOfSlices())
      throw new BadParametersException("The zSlice reference " + slice_number + " is invalid as the set has only " + getNumberOfSlices() + " zSlices.");
    return this.zSlices[slice_number];
  }

    @Override
  public double getZValue(int slice_number)
  {
    if (slice_number >= getNumberOfSlices())
      throw new BadParametersException("The zSlice reference " + slice_number + " is invalid as the set has only " + getNumberOfSlices() + " zSlices.");
    if (this.slices_zValues == null)
      setZValues();
    return this.slices_zValues[slice_number];
  }

  public String getName()
  {
    return this.name;
  }

  private void setZValues()
  {
    double stepSize = 1.0D / getNumberOfSlices();
    double currentStep = stepSize;
    this.slices_zValues = new double[getNumberOfSlices()];
    for (int i = 0; i < this.slices_zValues.length; i++)
    {
      this.slices_zValues[i] = currentStep;
      currentStep += stepSize;
    }
  }

  public double getFSWeightedAverage(double x)
  {
    double numerator = 0.0D; double denominator = 0.0D;
    for (int i = 0; i < getNumberOfSlices(); i++)
    {
      numerator += getZSlice(i).getFSAverage(x) * getZValue(i);
      denominator += getZValue(i);
    }
    return numerator / denominator;
  }

    @Override
  public T1MF_Interface getFS(double x)
  {
    T1MF_Discretized slice = new T1MF_Discretized(this.numberOfzLevels);

//    System.out.println("GenT2zMF has a zDiscretizationlevel of: " + this.numberOfzLevels);

    for (int i = 0; i < this.numberOfzLevels; i++)
    {
      Tuple temp = getZSlice(i).getFS(x);
//      System.out.println("On slice number" + i + " (" + getZSlice(i).getName() + ") with x = " + x + " getFS() returns: " + temp);

      if ((temp.getLeft() != 0.0D) || (temp.getRight() != 0.0D))
      {
        slice.addPoint(new Tuple(getZValue(i), temp.getLeft()));
        slice.addPoint(new Tuple(getZValue(i), temp.getRight()));

        if (x == 6.0D)
        {
//          System.out.println("Adding Tuple: " + new Tuple(getZValue(i), temp.getLeft()).toString());
//          System.out.println("Adding Tuple: " + new Tuple(getZValue(i), temp.getRight()).toString());
        }
      }

    }

    if (slice.getNumberOfPoints() > 0)
      return slice;
    return null;
  }

  public double[] getZValues()
  {
    if (this.slices_zValues == null)
      setZValues();
    return this.slices_zValues;
  }

  public Tuple getDomain()
  {
    return this.domain;
  }

  public boolean isLeftShoulder() {
    System.out.println("Shoulder methods not implemented!");
    return false;
  }

  public boolean isRightShoulder() {
    System.out.println("Shoulder methods not implemented!");
    return false;
  }

  public double getLeftShoulderStart() {
    System.out.println("Shoulder methods not implemented!");
    return (0.0D / 0.0D);
  }

  public double getRightShoulderStart() {
    System.out.println("Shoulder methods not implemented!");
    return (0.0D / 0.0D);
  }
}
