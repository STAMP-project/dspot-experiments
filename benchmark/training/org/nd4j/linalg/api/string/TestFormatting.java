package org.nd4j.linalg.api.string;


import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;
import org.nd4j.linalg.string.NDArrayStrings;


/**
 *
 *
 * @author Adam Gibson
 */
@Slf4j
@RunWith(Parameterized.class)
public class TestFormatting extends BaseNd4jTest {
    public TestFormatting(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testTwoByTwo() {
        INDArray arr = Nd4j.create(2, 2, 2, 2);
        System.out.println(new NDArrayStrings().format(arr));
    }

    @Test
    public void testNd4jArrayString() {
        INDArray arr = Nd4j.create(new float[]{ 1.0F, 2.0E7F, 40.838383F, 3.0F }, new int[]{ 2, 2 });
        String serializedData1 = new NDArrayStrings(",", 3).format(arr);
        log.info(("\n" + serializedData1));
        String expected1 = "[[1.000,40.838],\n" + " [2e7,3.000]]";
        Assert.assertEquals(expected1.replaceAll(" ", ""), serializedData1.replaceAll(" ", ""));
        String serializedData2 = new NDArrayStrings().format(arr);
        log.info(("\n" + serializedData2));
        String expected2 = "[[1.0000,40.8384],\n" + " [2e7,3.0000]]";
        Assert.assertEquals(expected2.replaceAll(" ", ""), serializedData2.replaceAll(" ", ""));
        String serializedData3 = new NDArrayStrings(",", "000.00##E0").format(arr);
        String expected3 = "[[100.00E-2,408.3838E-1],\n" + " [200.00E5,300.00E-2]]";
        log.info(("\n" + serializedData3));
        Assert.assertEquals(expected3.replaceAll(" ", ""), serializedData3.replaceAll(" ", ""));
    }

    @Test
    public void testRange() {
        INDArray arr = Nd4j.create(new double[][]{ new double[]{ -1, 0, 1, 0 }, new double[]{ -0.1, 0.1, -10, 10 }, new double[]{ -0.01, 0.01, -100.0, 100.0 }, new double[]{ -0.001, 0.001, -1000.0, 1000.0 }, new double[]{ -1.0E-4, 1.0E-4, -10000.0, 10000.0 }, new double[]{ -1.0E-8, 1.0E-8, -1.0E8, 1.0E8 }, new double[]{ -1.0E-30, 1.0E-30, -1.0E30, 1.0E30 }, new double[]{ -1.0E-50, 1.0E-50, -1.0E50, 1.0E50 }// larger than float
        // larger than float
        // larger than float
         });
        log.info(("\n" + (arr.toString())));
        arr = Nd4j.create(new double[][]{ new double[]{ 10001.0, 100000.0 }, new double[]{ 0.11, 0.269 } });
        arr = arr.reshape(2, 2, 1);
        log.info(("\n" + (arr.toString())));
    }
}

