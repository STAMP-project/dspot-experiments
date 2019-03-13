package org.nd4j.linalg.api.blas;


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
@RunWith(Parameterized.class)
public class Level2Test extends BaseNd4jTest {
    public Level2Test(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testGemv1() throws Exception {
        INDArray array1 = Nd4j.linspace(1, 1000, 1000).reshape(10, 100);
        INDArray array2 = Nd4j.linspace(1, 100, 100).reshape(100, 1);
        INDArray array3 = array1.mmul(array2);
        Assert.assertEquals(10, array3.length());
        Assert.assertEquals(338350.0F, array3.getFloat(0), 0.001F);
        Assert.assertEquals(843350.0F, array3.getFloat(1), 0.001F);
        Assert.assertEquals(1348350.0F, array3.getFloat(2), 0.001F);
        Assert.assertEquals(1853350.0F, array3.getFloat(3), 0.001F);
    }

    @Test
    public void testGemv2() throws Exception {
        INDArray array1 = Nd4j.linspace(1, 1000, 1000).reshape(10, 100);
        INDArray array2 = Nd4j.linspace(1, 100, 100).reshape('f', 100, 1);
        INDArray array3 = array1.mmul(array2);
        Assert.assertEquals(10, array3.length());
        Assert.assertEquals(338350.0F, array3.getFloat(0), 0.001F);
        Assert.assertEquals(843350.0F, array3.getFloat(1), 0.001F);
        Assert.assertEquals(1348350.0F, array3.getFloat(2), 0.001F);
        Assert.assertEquals(1853350.0F, array3.getFloat(3), 0.001F);
    }

    @Test
    public void testGemv3() throws Exception {
        INDArray array1 = Nd4j.linspace(1, 1000, 1000).reshape('f', 10, 100);
        INDArray array2 = Nd4j.linspace(1, 100, 100).reshape('f', 100, 1);
        INDArray array3 = array1.mmul(array2);
        Assert.assertEquals(10, array3.length());
        Assert.assertEquals(3338050.0F, array3.getFloat(0), 0.001F);
        Assert.assertEquals(3343100.0F, array3.getFloat(1), 0.001F);
        Assert.assertEquals(3348150.0F, array3.getFloat(2), 0.001F);
        Assert.assertEquals(3353200.0F, array3.getFloat(3), 0.001F);
    }

    @Test
    public void testGemv4() throws Exception {
        INDArray array1 = Nd4j.linspace(1, 1000, 1000).reshape('f', 10, 100);
        INDArray array2 = Nd4j.linspace(1, 100, 100).reshape(100, 1);
        INDArray array3 = array1.mmul(array2);
        Assert.assertEquals(10, array3.length());
        Assert.assertEquals(3338050.0F, array3.getFloat(0), 0.001F);
        Assert.assertEquals(3343100.0F, array3.getFloat(1), 0.001F);
        Assert.assertEquals(3348150.0F, array3.getFloat(2), 0.001F);
        Assert.assertEquals(3353200.0F, array3.getFloat(3), 0.001F);
    }

    @Test
    public void testGemv5() throws Exception {
        INDArray array1 = Nd4j.linspace(1, 1000, 1000).reshape(10, 100);
        INDArray array2 = Nd4j.linspace(1, 100, 100).reshape(100, 1);
        INDArray array3 = Nd4j.create(10);
        array1.mmul(array2, array3);
        Assert.assertEquals(10, array3.length());
        Assert.assertEquals(338350.0F, array3.getFloat(0), 0.001F);
        Assert.assertEquals(843350.0F, array3.getFloat(1), 0.001F);
        Assert.assertEquals(1348350.0F, array3.getFloat(2), 0.001F);
        Assert.assertEquals(1853350.0F, array3.getFloat(3), 0.001F);
    }

    @Test
    public void testGemv6() throws Exception {
        INDArray array1 = Nd4j.linspace(1, 1000, 1000).reshape('f', 10, 100);
        INDArray array2 = Nd4j.linspace(1, 100, 100).reshape(100, 1);
        INDArray array3 = Nd4j.create(10);
        array1.mmul(array2, array3);
        Assert.assertEquals(10, array3.length());
        Assert.assertEquals(3338050.0F, array3.getFloat(0), 0.001F);
        Assert.assertEquals(3343100.0F, array3.getFloat(1), 0.001F);
        Assert.assertEquals(3348150.0F, array3.getFloat(2), 0.001F);
        Assert.assertEquals(3353200.0F, array3.getFloat(3), 0.001F);
    }

    @Test
    public void testGemv7() throws Exception {
        INDArray array1 = Nd4j.linspace(1, 1000, 1000).reshape('f', 10, 100);
        INDArray array2 = Nd4j.linspace(1, 100, 100).reshape(100, 1);
        INDArray array3 = Nd4j.create(10);
        array1.mmul(array2, array3);
        Assert.assertEquals(10, array3.length());
        Assert.assertEquals(3338050.0F, array3.getFloat(0), 0.001F);
        Assert.assertEquals(3343100.0F, array3.getFloat(1), 0.001F);
        Assert.assertEquals(3348150.0F, array3.getFloat(2), 0.001F);
        Assert.assertEquals(3353200.0F, array3.getFloat(3), 0.001F);
    }
}

