package org.nd4j.linalg.shape.reshape;


import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Assume;
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
@Slf4j
@RunWith(Parameterized.class)
public class ReshapeTests extends BaseNd4jTest {
    public ReshapeTests(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testThreeTwoTwoTwo() {
        INDArray threeTwoTwo = Nd4j.linspace(1, 12, 12).reshape(3, 2, 2);
        INDArray sliceZero = Nd4j.create(new double[][]{ new double[]{ 1, 7 }, new double[]{ 4, 10 } });
        INDArray sliceOne = Nd4j.create(new double[][]{ new double[]{ 2, 8 }, new double[]{ 5, 11 } });
        INDArray sliceTwo = Nd4j.create(new double[][]{ new double[]{ 3, 9 }, new double[]{ 6, 12 } });
        INDArray[] assertions = new INDArray[]{ sliceZero, sliceOne, sliceTwo };
        for (int i = 0; i < (threeTwoTwo.slices()); i++) {
            INDArray sliceI = threeTwoTwo.slice(i);
            Assert.assertEquals(assertions[i], sliceI);
        }
        INDArray linspaced = Nd4j.linspace(1, 4, 4).reshape(2, 2);
        INDArray[] assertionsTwo = new INDArray[]{ Nd4j.create(new double[]{ 1, 3 }), Nd4j.create(new double[]{ 2, 4 }) };
        for (int i = 0; i < (assertionsTwo.length); i++)
            Assert.assertEquals(linspaced.slice(i), assertionsTwo[i]);

    }

    @Test
    public void testColumnVectorReshape() {
        double delta = 0.1;
        INDArray arr = Nd4j.create(1, 3);
        INDArray reshaped = arr.reshape('f', 3, 1);
        BaseNd4jTest.assertArrayEquals(new int[]{ 3, 1 }, reshaped.shape());
        Assert.assertEquals(0.0, reshaped.getDouble(1), delta);
        Assert.assertEquals(0.0, reshaped.getDouble(2), delta);
        log.info("Reshaped: {}", reshaped.shapeInfoDataBuffer().asInt());
        Assume.assumeNotNull(reshaped.toString());
    }
}

