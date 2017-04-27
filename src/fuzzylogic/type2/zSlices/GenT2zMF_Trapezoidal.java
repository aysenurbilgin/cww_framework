/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzylogic.type2.zSlices;

import fuzzylogic.generic.Tuple;
import fuzzylogic.type1.T1MF_Discretized;
import fuzzylogic.type2.interval.IntervalT2MF_Trapezoidal;

/*
Copyright (c) 2012,2013,2014 Christian Wagner
All rights reserved.
 */

public class GenT2zMF_Trapezoidal extends GenT2zMF_Prototype
{
  private IntervalT2MF_Trapezoidal primer;
  private final boolean DEBUG = false;

  public GenT2zMF_Trapezoidal(String name, IntervalT2MF_Trapezoidal primer, int numberOfzLevels)
  {
    super(name);

    this.numberOfzLevels = numberOfzLevels;
    this.domain = primer.getDomain();
    this.primer = primer;
    this.slices_fs = new Tuple[numberOfzLevels];
    this.slices_zValues = new double[numberOfzLevels];

    this.z_stepSize = (1.0D / numberOfzLevels);

    this.zSlices = new IntervalT2MF_Trapezoidal[numberOfzLevels];
    double left_stepsize = (primer.getLMF().getA() - primer.getUMF().getA()) / (numberOfzLevels - 1) / 2.0D;
    double right_stepsize = (primer.getUMF().getD() - primer.getLMF().getD()) / (numberOfzLevels - 1) / 2.0D;

    double[] inner = new double[4];
    double[] outer = new double[4];
    System.arraycopy(primer.getLMF().getParameters(), 0, inner, 0, 4);
    System.arraycopy(primer.getUMF().getParameters(), 0, outer, 0, 4);

    this.zSlices[0] = new IntervalT2MF_Trapezoidal("Slice 0", primer.getLMF(), primer.getUMF());

    this.slices_zValues[0] = this.z_stepSize;

    for (int i = 1; i < numberOfzLevels; i++)
    {
      this.slices_zValues[i] = (this.slices_zValues[(i - 1)] + this.z_stepSize);

      inner[0] -= left_stepsize; inner[1] -= left_stepsize; inner[2] += right_stepsize; inner[3] += right_stepsize;
      outer[0] += left_stepsize; outer[1] += left_stepsize; outer[2] -= right_stepsize; outer[3] -= right_stepsize;

      this.zSlices[i] = new IntervalT2MF_Trapezoidal("Slice " + i, inner, outer);
    }
  }

  public GenT2zMF_Trapezoidal(String name, IntervalT2MF_Trapezoidal primer0, IntervalT2MF_Trapezoidal primer1, int numberOfzLevels)
  {
    super(name);

    this.numberOfzLevels = numberOfzLevels;
    this.domain = primer0.getDomain();
    this.slices_fs = new Tuple[numberOfzLevels];
    this.slices_zValues = new double[numberOfzLevels];
    this.zSlices = new IntervalT2MF_Trapezoidal[numberOfzLevels];

    this.zSlices[0] = primer0;
    ((IntervalT2MF_Trapezoidal)this.zSlices[0]).setName(getName() + "_Slice_0");
    this.zSlices[(this.zSlices.length - 1)] = primer1;

    this.z_stepSize = (1.0D / numberOfzLevels);
    this.slices_zValues[0] = this.z_stepSize;
    this.slices_zValues[(this.zSlices.length - 1)] = 1.0D;

    double lsu = (primer1.getUMF().getA() - primer0.getUMF().getA()) / (numberOfzLevels - 1);
    double lsl = (primer0.getLMF().getA() - primer1.getLMF().getA()) / (numberOfzLevels - 1);

    double rsu = (primer0.getUMF().getD() - primer1.getUMF().getD()) / (numberOfzLevels - 1);
    double rsl = (primer1.getLMF().getD() - primer0.getLMF().getD()) / (numberOfzLevels - 1);

    double[] inner = new double[4];
    double[] outer = new double[4];

    System.arraycopy(primer0.getLMF().getParameters(), 0, inner, 0, 3);
    System.arraycopy(primer0.getUMF().getParameters(), 0, outer, 0, 3);

    for (int i = 1; i < numberOfzLevels - 1; i++)
    {
      this.slices_zValues[i] = (this.slices_zValues[(i - 1)] + this.z_stepSize);
      inner[0] -= lsl; inner[3] += rsl;
      outer[0] += lsu; outer[3] -= rsu;

      this.zSlices[i] = new IntervalT2MF_Trapezoidal(getName() + "_Slice_" + i, inner, outer);
    }
  }

  public GenT2zMF_Trapezoidal(String name, IntervalT2MF_Trapezoidal[] primers)
  {
    super(name);
    this.numberOfzLevels = primers.length;
    this.domain = primers[0].getDomain();

    this.slices_fs = new Tuple[this.numberOfzLevels];
    this.slices_zValues = new double[this.numberOfzLevels];

    this.zSlices = new IntervalT2MF_Trapezoidal[this.numberOfzLevels];
    this.z_stepSize = (1.0D / this.numberOfzLevels);

    this.slices_zValues[0] = this.z_stepSize;

    System.arraycopy(primers, 0, this.zSlices, 0, primers.length);
    for (int i = 0; i < this.numberOfzLevels; i++)
    {
      this.slices_zValues[i] = (this.z_stepSize * (i + 1));
    }
  }

  public Object clone()
  {
    System.out.println("Cloning for GenT2zMF_Trapezoidal needs to be re-implemented.");
    return null;
  }

  public IntervalT2MF_Trapezoidal getZSlice(int slice_number)
  {
    return (IntervalT2MF_Trapezoidal)this.zSlices[slice_number];
  }

  public T1MF_Discretized getFS(double x)
  {
    T1MF_Discretized slice = new T1MF_Discretized(this.numberOfzLevels);

    for (int i = 0; i < this.numberOfzLevels; i++)
    {
      Tuple temp = getZSlice(i).getFS(x);

      if ((temp.getLeft() != 0.0D) || (temp.getRight() != 0.0D))
      {
        slice.addPoint(new Tuple(getZValue(i), temp.getLeft()));
        slice.addPoint(new Tuple(getZValue(i), temp.getRight()));

        if (x == 6.0D)
        {
          System.out.println("Adding Tuple: " + new Tuple(getZValue(i), temp.getLeft()).toString());
          System.out.println("Adding Tuple: " + new Tuple(getZValue(i), temp.getRight()).toString());
        }
      }

    }

    if (slice.getNumberOfPoints() > 0)
      return slice;
    return null;
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
