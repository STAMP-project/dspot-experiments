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
public class Level3Test extends BaseNd4jTest {
    public Level3Test(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testGemm1() throws Exception {
        INDArray array1 = Nd4j.linspace(1, 100, 100).reshape(1, 100);
        INDArray array2 = Nd4j.linspace(1, 100, 100).reshape(100, 1);
        INDArray array3 = array1.mmul(array2);
        Assert.assertEquals(338350.0F, array3.getFloat(0), 0.001F);
    }

    @Test
    public void testGemm2() throws Exception {
        INDArray array1 = Nd4j.linspace(1, 100, 100).reshape('f', 1, 100);
        INDArray array2 = Nd4j.linspace(1, 100, 100).reshape('f', 100, 1);
        INDArray array3 = array1.mmul(array2);
        Assert.assertEquals(338350.0F, array3.getFloat(0), 0.001F);
    }

    @Test
    public void testGemm3() throws Exception {
        INDArray array1 = Nd4j.linspace(1, 1000, 1000).reshape(10, 100);
        INDArray array2 = Nd4j.linspace(1, 1000, 1000).reshape(100, 10);
        INDArray array3 = array1.mmul(array2);
        // System.out.println("Array3: " + Arrays.toString(array3.data().asFloat()));
        Assert.assertEquals(3338050.0F, array3.data().getFloat(0), 0.001F);
        Assert.assertEquals(8298050.0F, array3.data().getFloat(1), 0.001F);
        Assert.assertEquals(3343100.0F, array3.data().getFloat(10), 0.001F);
        Assert.assertEquals(8313100.0F, array3.data().getFloat(11), 0.001F);
        Assert.assertEquals(3348150.0F, array3.data().getFloat(20), 0.001F);
        Assert.assertEquals(8328150.0F, array3.data().getFloat(21), 0.001F);
    }

    @Test
    public void testGemm4() throws Exception {
        INDArray array1 = Nd4j.linspace(1, 1000, 1000).reshape(10, 100);
        INDArray array2 = Nd4j.linspace(1, 1000, 1000).reshape('f', 100, 10);
        INDArray array3 = array1.mmul(array2);
        // System.out.println("Array3: " + Arrays.toString(array3.data().asFloat()));
        Assert.assertEquals(338350.0F, array3.data().getFloat(0), 0.001F);
        Assert.assertEquals(843350.0F, array3.data().getFloat(1), 0.001F);
        Assert.assertEquals(843350.0F, array3.data().getFloat(10), 0.001F);
        Assert.assertEquals(2348350.0F, array3.data().getFloat(11), 0.001F);
        Assert.assertEquals(1348350.0F, array3.data().getFloat(20), 0.001F);
        Assert.assertEquals(3853350.0F, array3.data().getFloat(21), 0.001F);
    }

    @Test
    public void testGemm5() throws Exception {
        INDArray array1 = Nd4j.linspace(1, 1000, 1000).reshape('f', 10, 100);
        INDArray array2 = Nd4j.linspace(1, 1000, 1000).reshape(100, 10);
        INDArray array3 = array1.mmul(array2);
        // System.out.println("Array3: " + Arrays.toString(array3.data().asFloat()));
        // assertEquals(3.29341E7f, array3.data().getFloat(0),10f);
        Assert.assertEquals(3.29837E7F, array3.data().getFloat(1), 10.0F);
        Assert.assertEquals(3.3835E7F, array3.data().getFloat(99), 10.0F);
    }

    @Test
    public void testGemm6() throws Exception {
        INDArray array1 = Nd4j.linspace(1, 1000, 1000).reshape('f', 10, 100);
        INDArray array2 = Nd4j.linspace(1, 1000, 1000).reshape('f', 100, 10);
        INDArray array3 = array1.mmul(array2);
        // System.out.println("Array3: " + Arrays.toString(array3.data().asFloat()));
        Assert.assertEquals(3338050.0F, array3.data().getFloat(0), 0.001F);
        Assert.assertEquals(3343100.0F, array3.data().getFloat(1), 0.001F);
        Assert.assertEquals(8298050.0F, array3.data().getFloat(10), 0.001F);
        Assert.assertEquals(8313100.0F, array3.data().getFloat(11), 0.001F);
        Assert.assertEquals(1.325805E7F, array3.data().getFloat(20), 5.0F);
        Assert.assertEquals(1.32831E7F, array3.data().getFloat(21), 5.0F);
    }
}

