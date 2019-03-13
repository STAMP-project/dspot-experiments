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
public class CudaIndexReduceTests {
    @Test
    public void testPinnedIMax() throws Exception {
        // simple way to stop test if we're not on CUDA backend here
        Assert.assertEquals("JcublasLevel1", Nd4j.getBlasWrapper().level1().getClass().getSimpleName());
        INDArray array1 = Nd4j.create(new float[]{ 1.0F, 0.1F, 2.0F, 3.0F, 4.0F, 5.0F });
        int idx = getFinalResult();
        System.out.println(("Array1: " + array1));
        Assert.assertEquals(5, idx);
    }

    @Test
    public void testPinnedIMax4() throws Exception {
        // simple way to stop test if we're not on CUDA backend here
        INDArray array1 = Nd4j.create(new float[]{ 0.0F, 0.0F, 0.0F, 2.0F, 2.0F, 0.0F });
        int idx = getFinalResult();
        System.out.println(("Array1: " + array1));
        Assert.assertEquals(3, idx);
    }

    @Test
    public void testPinnedIMaxLarge() throws Exception {
        // simple way to stop test if we're not on CUDA backend here
        Assert.assertEquals("JcublasLevel1", Nd4j.getBlasWrapper().level1().getClass().getSimpleName());
        INDArray array1 = Nd4j.linspace(1, 1024, 1024);
        int idx = getFinalResult();
        System.out.println(("Array1: " + array1));
        Assert.assertEquals(1023, idx);
    }

    @Test
    public void testIMaxLargeLarge() throws Exception {
        INDArray array1 = Nd4j.linspace(1, 1000, 12800);
        int idx = getFinalResult();
        Assert.assertEquals(12799, idx);
    }

    @Test
    public void testIamaxC() {
        INDArray linspace = Nd4j.linspace(1, 4, 4).dup('c');
        Assert.assertEquals(3, Nd4j.getBlasWrapper().iamax(linspace));
    }

    @Test
    public void testIamaxF() {
        INDArray linspace = Nd4j.linspace(1, 4, 4).dup('f');
        Assert.assertEquals(3, Nd4j.getBlasWrapper().iamax(linspace));
    }

    @Test
    public void testIMax2() {
        INDArray array1 = Nd4j.linspace(1, 1000, 128000).reshape(128, 1000);
        long time1 = System.currentTimeMillis();
        INDArray argMax = Nd4j.argMax(array1, 1);
        long time2 = System.currentTimeMillis();
        System.out.println(("Execution time: " + (time2 - time1)));
        for (int i = 0; i < 128; i++) {
            Assert.assertEquals(999.0F, argMax.getFloat(i), 1.0E-4F);
        }
    }

    @Test
    public void testIMaxAlongDimension() throws Exception {
        INDArray array = Nd4j.linspace(1, 491520, 491520).reshape(10, 3, 4, 64, 64);
        INDArray result = Nd4j.argMax(array, 4);
        System.out.println(("Result shapeInfo: " + (result.shapeInfoDataBuffer())));
        System.out.println(("Result length: " + (result.length())));
        // System.out.println("Result: " + result);
    }

    @Test
    public void testIMax3() {
        INDArray array1 = Nd4j.linspace(1, 1000, 128000).reshape(128, 1000);
        INDArray argMax = Nd4j.argMax(array1, 0);
        System.out.println(("ARgmax length: " + (argMax.length())));
        for (int i = 0; i < 1000; i++) {
            Assert.assertEquals((("Failed iteration: [" + i) + "]"), 127, argMax.getFloat(i), 1.0E-4F);
        }
    }

    @Test
    public void testIMax4() {
        INDArray array1 = Nd4j.linspace(1, 1000, 128000).reshape(128, 1000);
        long time1 = System.currentTimeMillis();
        INDArray argMax = Nd4j.argMax(array1, 0, 1);
        long time2 = System.currentTimeMillis();
        System.out.println(("Execution time: " + (time2 - time1)));
        Assert.assertEquals(127999.0F, argMax.getFloat(0), 0.001F);
    }

    @Test
    public void testIMaxDimensional() throws Exception {
        INDArray toArgMax = Nd4j.linspace(1, 24, 24).reshape(4, 3, 2);
        INDArray valueArray = Nd4j.valueArrayOf(new int[]{ 4, 2 }, 2.0);
        INDArray valueArrayTwo = Nd4j.valueArrayOf(new int[]{ 3, 2 }, 3.0);
        INDArray valueArrayThree = Nd4j.valueArrayOf(new int[]{ 4, 3 }, 1.0);
        INDArray argMax = Nd4j.argMax(toArgMax, 1);
        Assert.assertEquals(valueArray, argMax);
        INDArray argMaxZero = Nd4j.argMax(toArgMax, 0);
        Assert.assertEquals(valueArrayTwo, argMaxZero);
        INDArray argMaxTwo = Nd4j.argMax(toArgMax, 2);
        Assert.assertEquals(valueArrayThree, argMaxTwo);
    }

    @Test
    public void testPinnedIMax2() throws Exception {
        // simple way to stop test if we're not on CUDA backend here
        Assert.assertEquals("JcublasLevel1", Nd4j.getBlasWrapper().level1().getClass().getSimpleName());
        INDArray array1 = Nd4j.create(new float[]{ 6.0F, 0.1F, 2.0F, 3.0F, 7.0F, 5.0F });
        int idx = getFinalResult();
        System.out.println(("Array1: " + array1));
        Assert.assertEquals(4, idx);
    }

    @Test
    public void testPinnedIMax3() throws Exception {
        // simple way to stop test if we're not on CUDA backend here
        Assert.assertEquals("JcublasLevel1", Nd4j.getBlasWrapper().level1().getClass().getSimpleName());
        INDArray array1 = Nd4j.create(new float[]{ 6.0F, 0.1F, 2.0F, 3.0F, 7.0F, 9.0F });
        int idx = getFinalResult();
        System.out.println(("Array1: " + array1));
        Assert.assertEquals(5, idx);
    }

    @Test
    public void testPinnedIMin() throws Exception {
        // simple way to stop test if we're not on CUDA backend here
        Assert.assertEquals("JcublasLevel1", Nd4j.getBlasWrapper().level1().getClass().getSimpleName());
        INDArray array1 = Nd4j.create(new float[]{ 1.0F, 0.1F, 2.0F, 3.0F, 4.0F, 5.0F });
        int idx = getFinalResult();
        System.out.println(("Array1: " + array1));
        Assert.assertEquals(1, idx);
    }

    @Test
    public void testPinnedIMin2() throws Exception {
        // simple way to stop test if we're not on CUDA backend here
        Assert.assertEquals("JcublasLevel1", Nd4j.getBlasWrapper().level1().getClass().getSimpleName());
        INDArray array1 = Nd4j.create(new float[]{ 0.1F, 1.1F, 2.0F, 3.0F, 4.0F, 5.0F });
        int idx = getFinalResult();
        System.out.println(("Array1: " + array1));
        Assert.assertEquals(0, idx);
    }

    @Test
    public void testIMaxF1() throws Exception {
        Nd4j.getRandom().setSeed(12345);
        INDArray arr = Nd4j.rand('f', 10, 2);
        for (int i = 0; i < 10; i++) {
            INDArray row = arr.getRow(i);
            int maxIdx;
            if ((row.getDouble(0)) > (row.getDouble(1)))
                maxIdx = 0;
            else
                maxIdx = 1;

            INDArray argmax = Nd4j.argMax(row, 1);
            double argmaxd = argmax.getDouble(0);
            Assert.assertEquals(maxIdx, ((int) (argmaxd)));
            System.out.println(row);
            System.out.println(argmax);
            System.out.println(((("exp: " + maxIdx) + ", act: ") + argmaxd));
        }
    }
}

