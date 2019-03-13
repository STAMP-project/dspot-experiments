package org.nd4j.linalg.api.rng;


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
 * @author Adam Gibson
 */
@RunWith(Parameterized.class)
public class RngTests extends BaseNd4jTest {
    public RngTests(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testRngConstitency() {
        Nd4j.getRandom().setSeed(123);
        INDArray arr = Nd4j.rand(1, 5);
        Nd4j.getRandom().setSeed(123);
        INDArray arr2 = Nd4j.rand(1, 5);
        Assert.assertEquals(arr, arr2);
    }

    @Test
    public void testRandomWithOrder() {
        Nd4j.getRandom().setSeed(12345);
        int rows = 20;
        int cols = 20;
        int dim2 = 7;
        INDArray arr = Nd4j.rand('c', rows, cols);
        BaseNd4jTest.assertArrayEquals(new long[]{ rows, cols }, arr.shape());
        Assert.assertEquals('c', arr.ordering());
        Assert.assertTrue(((arr.minNumber().doubleValue()) >= 0.0));
        Assert.assertTrue(((arr.maxNumber().doubleValue()) <= 1.0));
        INDArray arr2 = Nd4j.rand('f', rows, cols);
        BaseNd4jTest.assertArrayEquals(new long[]{ rows, cols }, arr2.shape());
        Assert.assertEquals('f', arr2.ordering());
        Assert.assertTrue(((arr2.minNumber().doubleValue()) >= 0.0));
        Assert.assertTrue(((arr2.maxNumber().doubleValue()) <= 1.0));
        INDArray arr3 = Nd4j.rand('c', new int[]{ rows, cols, dim2 });
        BaseNd4jTest.assertArrayEquals(new long[]{ rows, cols, dim2 }, arr3.shape());
        Assert.assertEquals('c', arr3.ordering());
        Assert.assertTrue(((arr3.minNumber().doubleValue()) >= 0.0));
        Assert.assertTrue(((arr3.maxNumber().doubleValue()) <= 1.0));
        INDArray arr4 = Nd4j.rand('f', new int[]{ rows, cols, dim2 });
        BaseNd4jTest.assertArrayEquals(new long[]{ rows, cols, dim2 }, arr4.shape());
        Assert.assertEquals('f', arr4.ordering());
        Assert.assertTrue(((arr4.minNumber().doubleValue()) >= 0.0));
        Assert.assertTrue(((arr4.maxNumber().doubleValue()) <= 1.0));
        INDArray narr = Nd4j.randn('c', rows, cols);
        BaseNd4jTest.assertArrayEquals(new long[]{ rows, cols }, narr.shape());
        Assert.assertEquals('c', narr.ordering());
        Assert.assertEquals(narr.meanNumber().doubleValue(), 0.0, 0.05);
        INDArray narr2 = Nd4j.randn('f', rows, cols);
        BaseNd4jTest.assertArrayEquals(new long[]{ rows, cols }, narr2.shape());
        Assert.assertEquals('f', narr2.ordering());
        Assert.assertEquals(narr2.meanNumber().doubleValue(), 0.0, 0.05);
        INDArray narr3 = Nd4j.randn('c', new int[]{ rows, cols, dim2 });
        BaseNd4jTest.assertArrayEquals(new long[]{ rows, cols, dim2 }, narr3.shape());
        Assert.assertEquals('c', narr3.ordering());
        Assert.assertEquals(narr3.meanNumber().doubleValue(), 0.0, 0.05);
        INDArray narr4 = Nd4j.randn('f', new int[]{ rows, cols, dim2 });
        BaseNd4jTest.assertArrayEquals(new long[]{ rows, cols, dim2 }, narr4.shape());
        Assert.assertEquals('f', narr4.ordering());
        Assert.assertEquals(narr4.meanNumber().doubleValue(), 0.0, 0.05);
    }
}

