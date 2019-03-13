package org.nd4j.linalg.jcublas.ops.executioner;


import DataBuffer.Type.DOUBLE;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.buffer.util.DataTypeUtil;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.impl.scalar.ScalarAdd;
import org.nd4j.linalg.factory.Nd4j;


/**
 * These tests are meant to run with GridExecutioner as current one
 *
 * @author raver119@gmail.com
 */
public class GridRunningTests {
    @Test
    public void testScalarPassing1() throws Exception {
        INDArray array = Nd4j.create(5);
        INDArray exp = Nd4j.create(new float[]{ 6.0F, 6.0F, 6.0F, 6.0F, 6.0F });
        CudaGridExecutioner executioner = ((CudaGridExecutioner) (Nd4j.getExecutioner()));
        ScalarAdd opA = new ScalarAdd(array, 1.0F);
        ScalarAdd opB = new ScalarAdd(array, 2.0F);
        ScalarAdd opC = new ScalarAdd(array, 3.0F);
        Nd4j.getExecutioner().exec(opA);
        Assert.assertEquals(1, executioner.getQueueLength());
        Nd4j.getExecutioner().exec(opB);
        Assert.assertEquals(1, executioner.getQueueLength());
        Nd4j.getExecutioner().exec(opC);
        Assert.assertEquals(1, executioner.getQueueLength());
        Assert.assertEquals(exp, array);
        Assert.assertEquals(0, executioner.getQueueLength());
    }

    @Test
    public void testScalarPassing2() throws Exception {
        INDArray array = Nd4j.create(5);
        INDArray exp = Nd4j.create(new float[]{ 6.0F, 6.0F, 6.0F, 6.0F, 6.0F });
        CudaGridExecutioner executioner = ((CudaGridExecutioner) (Nd4j.getExecutioner()));
        ScalarAdd opA = new ScalarAdd(array, 1.0F);
        ScalarAdd opB = new ScalarAdd(array, 2.0F);
        ScalarAdd opC = new ScalarAdd(array, 3.0F);
        INDArray res1 = Nd4j.getExecutioner().execAndReturn(opA);
        Assert.assertEquals(1, executioner.getQueueLength());
        INDArray res2 = Nd4j.getExecutioner().execAndReturn(opB);
        Assert.assertEquals(1, executioner.getQueueLength());
        INDArray res3 = Nd4j.getExecutioner().execAndReturn(opC);
        Assert.assertEquals(1, executioner.getQueueLength());
        Assert.assertEquals(exp, array);
        Assert.assertEquals(0, executioner.getQueueLength());
        Assert.assertTrue((res1 == res2));
        Assert.assertTrue((res3 == res2));
    }

    @Test
    public void testMul_Scalar1() throws Exception {
        DataTypeUtil.setDTypeForContext(DOUBLE);
        INDArray x = Nd4j.create(new double[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });
        INDArray y = Nd4j.create(10).assign(3.0E-6);
        x.muli(y);
        x.divi(2.2E-6);
        flushQueueBlocking();
        INDArray eX = Nd4j.create(new double[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });
        flushQueueBlocking();
        INDArray eY = Nd4j.create(10).assign(3.0E-6);
        flushQueueBlocking();
        eX.muli(eY);
        flushQueueBlocking();
        System.out.println(("Data before divi2: " + (Arrays.toString(eX.data().asDouble()))));
        eX.divi(2.2E-6);
        flushQueueBlocking();
        System.out.println(("Data1: " + (Arrays.toString(x.data().asDouble()))));
        System.out.println(("Data2: " + (Arrays.toString(eX.data().asDouble()))));
        Assert.assertEquals(eX, x);
    }
}

