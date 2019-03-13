package jcuda.jcublas.ops;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.impl.accum.Max;
import org.nd4j.linalg.api.ops.impl.accum.Mean;
import org.nd4j.linalg.api.ops.impl.accum.Min;
import org.nd4j.linalg.api.ops.impl.accum.Sum;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.util.ArrayUtil;


/**
 *
 *
 * @author raver119@gmail.com
 */
@Ignore
public class CudaAccumTests {
    @Test
    public void testBiggerSum() throws Exception {
        INDArray array = Nd4j.ones(128000, 512);
        array.sum(0);
    }

    /**
     * Sum call
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testPinnedSum() throws Exception {
        // simple way to stop test if we're not on CUDA backend here
        Assert.assertEquals("JcublasLevel1", Nd4j.getBlasWrapper().level1().getClass().getSimpleName());
        INDArray array1 = Nd4j.create(new float[]{ 2.01F, 2.01F, 1.01F, 1.01F, 1.01F, 1.01F, 1.01F, 1.01F, 1.01F, 1.01F, 1.01F, 1.01F, 1.01F, 1.01F, 1.01F });
        Sum sum = new Sum(array1);
        Nd4j.getExecutioner().exec(sum, 1);
        Number resu = sum.getFinalResult();
        System.out.println(("Result: " + resu));
        Assert.assertEquals(17.15F, resu.floatValue(), 0.01F);
    }

    @Test
    public void testPinnedSum2() throws Exception {
        // simple way to stop test if we're not on CUDA backend here
        INDArray array1 = Nd4j.linspace(1, 10000, 100000).reshape(100, 1000);
        Sum sum = new Sum(array1);
        INDArray result;/* = Nd4j.getExecutioner().exec(sum, 0);

        assertEquals(495055.44f, result.getFloat(0), 0.01f);
         */

        result = Nd4j.getExecutioner().exec(sum, 1);
        result = Nd4j.getExecutioner().exec(sum, 1);
        Assert.assertEquals(50945.52F, result.getFloat(0), 0.01F);
    }

    @Test
    public void testPinnedSum3() throws Exception {
        // simple way to stop test if we're not on CUDA backend here
        INDArray array1 = Nd4j.linspace(1, 100000, 100000).reshape(100, 1000);
        for (int x = 0; x < 100000; x++) {
            Assert.assertEquals((("Failed on iteration [" + x) + "]"), (x + 1), array1.getFloat(x), 0.01F);
        }
    }

    @Test
    public void testPinnedSumNumber() throws Exception {
        // simple way to stop test if we're not on CUDA backend here
        INDArray array1 = Nd4j.linspace(1, 10000, 10000);
        float sum = array1.sumNumber().floatValue();
        Assert.assertEquals(5.0005E7, sum, 1.0F);
    }

    @Test
    public void testPinnedSumNumber2() throws Exception {
        // simple way to stop test if we're not on CUDA backend here
        INDArray array1 = Nd4j.ones(128000);
        long time1 = System.currentTimeMillis();
        float sum = array1.sumNumber().floatValue();
        long time2 = System.currentTimeMillis();
        System.out.println(("Execution time: " + (time2 - time1)));
        Assert.assertEquals(128000.0F, sum, 0.01F);
    }

    @Test
    public void testPinnedSumNumber3() throws Exception {
        // simple way to stop test if we're not on CUDA backend here
        INDArray array1 = Nd4j.ones(12800000);
        float sum = array1.sumNumber().floatValue();
        Assert.assertEquals(1.28E7F, sum, 0.01F);
    }

    @Test
    public void testStdev0() {
        double[][] ind = new double[][]{ new double[]{ 5.1, 3.5, 1.4 }, new double[]{ 4.9, 3.0, 1.4 }, new double[]{ 4.7, 3.2, 1.3 } };
        INDArray in = Nd4j.create(ind);
        INDArray stdev = in.std(0);
        INDArray exp = Nd4j.create(new double[]{ 0.2, 0.25166114784, 0.05773502692 });
        System.out.println(("Exp dtype: " + (exp.data().dataType())));
        System.out.println(("Exp dtype: " + (exp.data().dataType())));
        System.out.println(("Array: " + (Arrays.toString(exp.data().asFloat()))));
        Assert.assertEquals(exp, stdev);
    }

    @Test
    public void testStdev1() {
        double[][] ind = new double[][]{ new double[]{ 5.1, 3.5, 1.4 }, new double[]{ 4.9, 3.0, 1.4 }, new double[]{ 4.7, 3.2, 1.3 } };
        INDArray in = Nd4j.create(ind);
        INDArray stdev = in.std(1);
        INDArray exp = Nd4j.create(new double[]{ 1.855622088, 1.7521415468, 1.7039170559 });
        Assert.assertEquals(exp, stdev);
    }

    @Test
    public void testStdevNum() {
        INDArray in = Nd4j.linspace(1, 1000, 10000);
        float stdev = in.stdNumber().floatValue();
        Assert.assertEquals(288.42972F, stdev, 0.001F);
    }

    /**
     * Mean call
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testPinnedMean() throws Exception {
        // simple way to stop test if we're not on CUDA backend here
        Assert.assertEquals("JcublasLevel1", Nd4j.getBlasWrapper().level1().getClass().getSimpleName());
        INDArray array1 = Nd4j.create(new float[]{ 2.01F, 2.01F, 1.01F, 1.01F, 1.01F, 1.01F, 1.01F, 1.01F, 1.01F, 1.01F, 1.01F, 1.01F, 1.01F, 1.01F, 1.01F });
        INDArray array2 = Nd4j.create(new float[]{ 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F });
        Mean mean = new Mean(array1);
        Nd4j.getExecutioner().exec(mean, 1);
        Number resu = mean.getFinalResult();
        // INDArray result = Nd4j.getExecutioner().exec(new Mean(array1), 1);
        System.out.println(("Array1: " + array1));
        System.out.println(("Result: " + resu));
        Assert.assertEquals(1.14F, resu.floatValue(), 0.01F);
    }

    @Test
    public void testSum2() {
        INDArray n = Nd4j.create(Nd4j.linspace(1, 8, 8).data(), new int[]{ 2, 2, 2 });
        System.out.println(("N result: " + n));
        INDArray test = Nd4j.create(new float[]{ 3, 7, 11, 15 }, new int[]{ 2, 2 });
        System.out.println(("Test result: " + test));
        INDArray sum = n.sum((-1));
        System.out.println(("Sum result: " + sum));
        Assert.assertEquals(test, sum);
    }

    @Test
    public void testMax() {
        INDArray n = Nd4j.linspace(1, 15, 15);
        float max = n.maxNumber().floatValue();
        Assert.assertEquals(15.0F, max, 0.001F);
    }

    @Test
    public void testSum3() {
        INDArray n = Nd4j.linspace(1, 1000, 128000).reshape(128, 1000);
        long time1 = System.currentTimeMillis();
        INDArray sum = n.sum(new int[]{ 0 });
        long time2 = System.currentTimeMillis();
        System.out.println(("Time elapsed: " + (time2 - time1)));
        System.out.println(("Sum: " + sum));
        System.out.println(("Sum.Length: " + (sum.length())));
        System.out.println(("elementWiseStride: " + (n.elementWiseStride())));
        System.out.println(("elementStride: " + (n.elementStride())));
        Assert.assertEquals(63565.02F, sum.getFloat(0), 0.01F);
        Assert.assertEquals(63566.02F, sum.getFloat(1), 0.01F);
    }

    @Test
    public void testSum3_1() throws Exception {
        INDArray n = Nd4j.linspace(1, 128000, 128000).reshape(128, 1000);
        long time1 = System.currentTimeMillis();
        INDArray sum = n.sum(new int[]{ 0 });
        long time2 = System.currentTimeMillis();
        System.out.println(("Time elapsed: " + (time2 - time1)));
        System.out.println(("Sum: " + sum));
        System.out.println(("Sum.Length: " + (sum.length())));
        System.out.println(("elementWiseStride: " + (n.elementWiseStride())));
        System.out.println(("elementStride: " + (n.elementStride())));
        Assert.assertEquals(8128128.0F, sum.getFloat(0), 0.01F);
        Assert.assertEquals(8128256.0F, sum.getFloat(1), 0.01F);
        Assert.assertEquals(8128512.0F, sum.getFloat(3), 0.01F);
        Assert.assertEquals(8128640.0F, sum.getFloat(4), 0.01F);
    }

    @Test
    public void testSum4() {
        INDArray n = Nd4j.linspace(1, 1000, 128000).reshape(128, 1000);
        long time1 = System.currentTimeMillis();
        INDArray sum = n.sum(new int[]{ 1 });
        long time2 = System.currentTimeMillis();
        System.out.println(("Execution time: " + (time2 - time1)));
        System.out.println(("elementWiseStride: " + (n.elementWiseStride())));
        System.out.println(("elementStride: " + (n.elementStride())));
        Assert.assertEquals(4898.4707F, sum.getFloat(0), 0.01F);
        Assert.assertEquals(12703.209F, sum.getFloat(1), 0.01F);
    }

    @Test
    public void testSum5() {
        INDArray n = Nd4j.linspace(1, 1000, 128000).reshape(128, 1000);
        INDArray sum = n.sum(new int[]{ 1 });
        INDArray sum2 = n.sum(new int[]{ -1 });
        INDArray sum3 = n.sum(new int[]{ 0 });
        System.out.println(("elementWiseStride: " + (n.elementWiseStride())));
        System.out.println(("elementStride: " + (n.elementStride())));
        Assert.assertEquals(4898.4707F, sum.getFloat(0), 0.01F);
        Assert.assertEquals(12703.209F, sum.getFloat(1), 0.01F);
        Assert.assertEquals(sum, sum2);
        Assert.assertNotEquals(sum, sum3);
        Assert.assertEquals(63565.023F, sum3.getFloat(0), 0.01F);
        Assert.assertEquals(63570.008F, sum3.getFloat(5), 0.01F);
    }

    @Test
    public void testSum6() {
        INDArray n = Nd4j.linspace(1, 1000, 128000).reshape(128, 10, 10, 10);
        INDArray sum0 = n.sum(new int[]{ 0 });
        INDArray sum1 = n.sum(new int[]{ 1 });
        INDArray sum3 = n.sum(new int[]{ 3 });
        INDArray sumN = n.sum(new int[]{ -1 });
        INDArray sum2 = n.sum(new int[]{ 2 });
        System.out.println(("elementWiseStride: " + (n.elementWiseStride())));
        System.out.println(("elementStride: " + (n.elementStride())));
        Assert.assertEquals(63565.023F, sum0.getFloat(0), 0.01F);
        Assert.assertEquals(63570.008F, sum0.getFloat(5), 0.01F);
        Assert.assertEquals(45.12137F, sum1.getFloat(0), 0.01F);
        Assert.assertEquals(45.511604F, sum1.getFloat(5), 0.01F);
        Assert.assertEquals(10.351214F, sum3.getFloat(0), 0.01F);
        Assert.assertEquals(14.25359F, sum3.getFloat(5), 0.01F);
        Assert.assertEquals(14.25359F, sumN.getFloat(5), 0.01F);
        Assert.assertEquals(13.74628F, sum2.getFloat(3), 0.01F);
    }

    @Test
    public void testSum3Of4_2222() {
        int[] shape = new int[]{ 2, 2, 2, 2 };
        int length = ArrayUtil.prod(shape);
        INDArray arrC = Nd4j.linspace(1, length, length).reshape(shape);
        INDArray arrF = Nd4j.create(arrC.shape()).reshape('f', arrC.shape()).assign(arrC);
        System.out.println(("Arrf: " + arrF));
        System.out.println(("Arrf: " + (Arrays.toString(arrF.data().asFloat()))));
        System.out.println(("ArrF shapeInfo: " + (arrF.shapeInfoDataBuffer())));
        System.out.println("----------------------------");
        int[][] dimsToSum = new int[][]{ new int[]{ 0, 1, 2 }, new int[]{ 0, 1, 3 }, new int[]{ 0, 2, 3 }, new int[]{ 1, 2, 3 } };
        double[][] expD = new double[][]{ new double[]{ 64, 72 }, new double[]{ 60, 76 }, new double[]{ 52, 84 }, new double[]{ 36, 100 } };
        for (int i = 0; i < (dimsToSum.length); i++) {
            int[] d = dimsToSum[i];
            INDArray outC = arrC.sum(d);
            INDArray outF = arrF.sum(d);
            INDArray exp = Nd4j.create(expD[i], outC.shape());
            Assert.assertEquals(exp, outC);
            Assert.assertEquals(exp, outF);
            System.out.println(((((("PASSED:" + (Arrays.toString(d))) + "\t") + outC) + "\t") + outF));
        }
    }

    @Test
    public void testDimensionMax() {
        INDArray linspace = Nd4j.linspace(1, 6, 6).reshape('f', 2, 3);
        int axis = 0;
        INDArray row = linspace.slice(axis);
        System.out.println(("Linspace: " + linspace));
        System.out.println(("Row: " + row));
        System.out.println(("Row shapeInfo: " + (row.shapeInfoDataBuffer())));
        Max max = new Max(row);
        double max2 = Nd4j.getExecutioner().execAndReturn(max).getFinalResult().doubleValue();
        Assert.assertEquals(5.0, max2, 0.1);
        Min min = new Min(row);
        double min2 = Nd4j.getExecutioner().execAndReturn(min).getFinalResult().doubleValue();
        Assert.assertEquals(1.0, min2, 0.1);
    }

    @Test
    public void testNorm2() throws Exception {
        INDArray array1 = Nd4j.create(new float[]{ 2.01F, 2.01F, 1.01F, 1.01F, 1.01F, 1.01F, 1.01F, 1.01F, 1.01F, 1.01F, 1.01F, 1.01F, 1.01F, 1.01F, 1.01F });
        INDArray result = array1.norm2(1);
        System.out.println(result);
        Assert.assertEquals(4.62F, result.getDouble(0), 0.001);
    }

    @Test
    public void testSumF() throws Exception {
        INDArray arrc = Nd4j.linspace(1, 6, 6).reshape('c', 3, 2);
        INDArray arrf = Nd4j.create(new double[6], new int[]{ 3, 2 }, 'f').assign(arrc);
        System.out.println(("ArrC: " + arrc));
        System.out.println(("ArrC buffer: " + (Arrays.toString(arrc.data().asFloat()))));
        System.out.println(("ArrF: " + arrf));
        System.out.println(("ArrF buffer: " + (Arrays.toString(arrf.data().asFloat()))));
        System.out.println(("ArrF shape: " + (arrf.shapeInfoDataBuffer())));
        INDArray cSum = arrc.sum(0);
        INDArray fSum = arrf.sum(0);
        Assert.assertEquals(Nd4j.create(new float[]{ 9.0F, 12.0F }), fSum);
    }

    @Test
    public void testMax1() throws Exception {
        INDArray array1 = Nd4j.linspace(1, 76800, 76800).reshape(256, 300);
        long time1 = System.currentTimeMillis();
        INDArray array = array1.max(1);
        long time2 = System.currentTimeMillis();
        System.out.println(("Time elapsed: " + (time2 - time1)));
        Assert.assertEquals(256, array.length());
        for (int x = 0; x < 256; x++) {
            Assert.assertEquals(((x + 1) * 300), array.getFloat(x), 0.01F);
        }
    }

    @Test
    public void testMax0() throws Exception {
        INDArray array1 = Nd4j.linspace(1, 76800, 76800).reshape(256, 300);
        long time1 = System.currentTimeMillis();
        INDArray array = array1.max(0);
        long time2 = System.currentTimeMillis();
        System.out.println(("Array1 shapeInfo: " + (array1.shapeInfoDataBuffer())));
        System.out.println(("Result shapeInfo: " + (array.shapeInfoDataBuffer())));
        System.out.println(("Time elapsed: " + (time2 - time1)));
        Assert.assertEquals(300, array.length());
        for (int x = 0; x < 300; x++) {
            Assert.assertEquals(("Failed on x: " + x), ((76800 - ((array1.columns()) - x)) + 1), array.getFloat(x), 0.01F);
        }
    }

    @Test
    public void testMax1_2() throws Exception {
        INDArray array1 = Nd4j.linspace(1, 7680000, 7680000).reshape(2560, 3000);
        /* for (int x = 0; x < 7680000; x++) {
        assertEquals(x+1, array1.getFloat(x), 0.001f);
        }
         */
        long time1 = System.currentTimeMillis();
        INDArray array = array1.max(1);
        long time2 = System.currentTimeMillis();
        System.out.println(("Time elapsed: " + (time2 - time1)));
        Assert.assertEquals(2560, array.length());
        // System.out.println("Array: " + array);
        for (int x = 0; x < 2560; x++) {
            Assert.assertEquals(("Failed on x:" + x), ((x + 1) * 3000), array.getFloat(x), 0.01F);
        }
    }
}

