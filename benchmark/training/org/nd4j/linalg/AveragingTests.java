package org.nd4j.linalg;


import DataBuffer.Type;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
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
public class AveragingTests extends BaseNd4jTest {
    private final int THREADS = 16;

    private final int LENGTH = 51200 * 4;

    Type initialType;

    public AveragingTests(Nd4jBackend backend) {
        super(backend);
        this.initialType = Nd4j.dataType();
    }

    @Test
    public void testSingleDeviceAveraging1() throws Exception {
        INDArray array1 = Nd4j.valueArrayOf(LENGTH, 1.0);
        INDArray array2 = Nd4j.valueArrayOf(LENGTH, 2.0);
        INDArray array3 = Nd4j.valueArrayOf(LENGTH, 3.0);
        INDArray array4 = Nd4j.valueArrayOf(LENGTH, 4.0);
        INDArray array5 = Nd4j.valueArrayOf(LENGTH, 5.0);
        INDArray array6 = Nd4j.valueArrayOf(LENGTH, 6.0);
        INDArray array7 = Nd4j.valueArrayOf(LENGTH, 7.0);
        INDArray array8 = Nd4j.valueArrayOf(LENGTH, 8.0);
        INDArray array9 = Nd4j.valueArrayOf(LENGTH, 9.0);
        INDArray array10 = Nd4j.valueArrayOf(LENGTH, 10.0);
        INDArray array11 = Nd4j.valueArrayOf(LENGTH, 11.0);
        INDArray array12 = Nd4j.valueArrayOf(LENGTH, 12.0);
        INDArray array13 = Nd4j.valueArrayOf(LENGTH, 13.0);
        INDArray array14 = Nd4j.valueArrayOf(LENGTH, 14.0);
        INDArray array15 = Nd4j.valueArrayOf(LENGTH, 15.0);
        INDArray array16 = Nd4j.valueArrayOf(LENGTH, 16.0);
        long time1 = System.currentTimeMillis();
        INDArray arrayMean = Nd4j.averageAndPropagate(new INDArray[]{ array1, array2, array3, array4, array5, array6, array7, array8, array9, array10, array11, array12, array13, array14, array15, array16 });
        long time2 = System.currentTimeMillis();
        System.out.println(("Execution time: " + (time2 - time1)));
        Assert.assertNotEquals(null, arrayMean);
        Assert.assertEquals(8.5F, arrayMean.getFloat(12), 0.1F);
        Assert.assertEquals(8.5F, arrayMean.getFloat(150), 0.1F);
        Assert.assertEquals(8.5F, arrayMean.getFloat(475), 0.1F);
        Assert.assertEquals(8.5F, array1.getFloat(475), 0.1F);
        Assert.assertEquals(8.5F, array2.getFloat(475), 0.1F);
        Assert.assertEquals(8.5F, array3.getFloat(475), 0.1F);
        Assert.assertEquals(8.5F, array5.getFloat(475), 0.1F);
        Assert.assertEquals(8.5F, array16.getFloat(475), 0.1F);
        Assert.assertEquals(8.5, arrayMean.meanNumber().doubleValue(), 0.01);
        Assert.assertEquals(8.5, array1.meanNumber().doubleValue(), 0.01);
        Assert.assertEquals(8.5, array2.meanNumber().doubleValue(), 0.01);
        Assert.assertEquals(arrayMean, array16);
    }

    @Test
    public void testSingleDeviceAveraging2() throws Exception {
        INDArray exp = Nd4j.linspace(1, LENGTH, LENGTH);
        List<INDArray> arrays = new ArrayList<>();
        for (int i = 0; i < (THREADS); i++)
            arrays.add(exp.dup());

        INDArray mean = Nd4j.averageAndPropagate(arrays);
        Assert.assertEquals(exp, mean);
        for (int i = 0; i < (THREADS); i++)
            Assert.assertEquals(exp, arrays.get(i));

    }

    @Test
    public void testAccumulation1() {
        INDArray array1 = Nd4j.create(100).assign(1.0);
        INDArray array2 = Nd4j.create(100).assign(2.0);
        INDArray array3 = Nd4j.create(100).assign(3.0);
        INDArray exp = Nd4j.create(100).assign(6.0);
        INDArray accum = Nd4j.accumulate(new INDArray[]{ array1, array2, array3 });
        Assert.assertEquals(exp, accum);
    }

    @Test
    public void testAccumulation2() {
        INDArray array1 = Nd4j.create(100).assign(1.0);
        INDArray array2 = Nd4j.create(100).assign(2.0);
        INDArray array3 = Nd4j.create(100).assign(3.0);
        INDArray target = Nd4j.create(100);
        INDArray exp = Nd4j.create(100).assign(6.0);
        INDArray accum = Nd4j.accumulate(target, new INDArray[]{ array1, array2, array3 });
        Assert.assertEquals(exp, accum);
        Assert.assertTrue((accum == target));
    }

    @Test
    public void testAccumulation3() {
        // we want to ensure that cuda backend is able to launch this op on cpu
        Nd4j.getAffinityManager().allowCrossDeviceAccess(false);
        INDArray array1 = Nd4j.create(100).assign(1.0);
        INDArray array2 = Nd4j.create(100).assign(2.0);
        INDArray array3 = Nd4j.create(100).assign(3.0);
        INDArray target = Nd4j.create(100);
        INDArray exp = Nd4j.create(100).assign(6.0);
        INDArray accum = Nd4j.accumulate(target, new INDArray[]{ array1, array2, array3 });
        Assert.assertEquals(exp, accum);
        Assert.assertTrue((accum == target));
        Nd4j.getAffinityManager().allowCrossDeviceAccess(true);
    }
}

