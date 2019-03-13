package jcuda.jcublas.ops;


import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;


/**
 *
 *
 * @author raver119@gmail.com
 */
@Ignore
public class CublasTests {
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
        Assert.assertEquals(3.293408E7F, array3.data().getFloat(0), 10.0F);
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

    @Test
    public void testGemm7() throws Exception {
        INDArray array1 = Nd4j.linspace(1, 1000, 1000).reshape(10, 100);
        INDArray array2 = Nd4j.linspace(1, 1000, 1000).reshape(100, 10);
        INDArray array3 = Nd4j.create(10, 10);
        array1.mmul(array2, array3);
        // System.out.println("Array3: " + Arrays.toString(array3.data().asFloat()));
        Assert.assertEquals(3338050.0F, array3.data().getFloat(0), 0.001F);
        Assert.assertEquals(8298050.0F, array3.data().getFloat(1), 0.001F);
        Assert.assertEquals(3343100.0F, array3.data().getFloat(10), 0.001F);
        Assert.assertEquals(8313100.0F, array3.data().getFloat(11), 0.001F);
        Assert.assertEquals(3348150.0F, array3.data().getFloat(20), 0.001F);
        Assert.assertEquals(8328150.0F, array3.data().getFloat(21), 0.001F);
    }

    @Test
    public void testGemm8() throws Exception {
        INDArray array1 = Nd4j.ones(10, 10);
        INDArray array2 = Nd4j.ones(10, 10);
        INDArray array3 = Nd4j.create(10, 10);
        array1.mmul(array2, array3);
        Assert.assertEquals(10.0F, array3.data().getFloat(0), 0.001F);
        Assert.assertEquals(10.0F, array3.data().getFloat(1), 0.001F);
        Assert.assertEquals(10.0F, array3.data().getFloat(10), 0.001F);
        Assert.assertEquals(10.0F, array3.data().getFloat(11), 0.001F);
        Assert.assertEquals(10.0F, array3.data().getFloat(20), 0.001F);
        Assert.assertEquals(10.0F, array3.data().getFloat(21), 0.001F);
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
}

