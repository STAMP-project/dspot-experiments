package org.nd4j.linalg.lapack;


import DataBuffer.Type;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;


/**
 *
 *
 * @author raver119@gmail.com
 */
@Slf4j
@RunWith(Parameterized.class)
public class LapackTestsF extends BaseNd4jTest {
    Type initialType;

    public LapackTestsF(Nd4jBackend backend) {
        super(backend);
        initialType = Nd4j.dataType();
    }

    @Test
    public void testGetRF1DifferentOrders() throws Exception {
        INDArray a = Nd4j.create(new double[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9 }, new int[]{ 3, 3 }, 'c').dup('f');
        INDArray exp = Nd4j.create(new double[]{ 7.0, 8.0, 9.0, 0.14285715, 0.85714287, 1.7142857, 0.5714286, 0.5, 0.0 }, new int[]{ 3, 3 }, 'c').dup('f');
        INDArray r = Nd4j.getNDArrayFactory().lapack().getrf(a);
        Assert.assertEquals(exp, a);
    }
}

