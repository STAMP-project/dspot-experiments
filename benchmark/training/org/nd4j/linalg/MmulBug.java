/**
 * Created by susaneraly on 8/26/16.
 */
package org.nd4j.linalg;


import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;


public class MmulBug {
    @Test
    public void simpleTest() throws Exception {
        INDArray m1 = Nd4j.create(new double[][]{ new double[]{ 1.0 }, new double[]{ 2.0 }, new double[]{ 3.0 }, new double[]{ 4.0 } });
        m1 = m1.reshape(2, 2);
        INDArray m2 = Nd4j.create(new double[][]{ new double[]{ 1.0, 2.0, 3.0, 4.0 } });
        m2 = m2.reshape(2, 2);
        m2.setOrder('f');
        // mmul gives the correct result
        INDArray correctResult;
        correctResult = m1.mmul(m2);
        System.out.println("================");
        System.out.println(m1);
        System.out.println(m2);
        System.out.println(correctResult);
        System.out.println("================");
        INDArray newResult = Nd4j.zeros(correctResult.shape(), 'c');
        m1.mmul(m2, newResult);
        Assert.assertEquals(correctResult, newResult);
        // But not so mmuli (which is somewhat mixed)
        INDArray target = Nd4j.linspace(1, 4, 4).reshape(2, 2);
        target = m1.mmuli(m2, m1);
        Assert.assertEquals(true, target.equals(correctResult));
        Assert.assertEquals(true, m1.equals(correctResult));
    }
}

