package org.nd4j.linalg.slicing;


import lombok.val;
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
public class SlicingTests extends BaseNd4jTest {
    public SlicingTests(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testSlices() {
        INDArray arr = Nd4j.create(Nd4j.linspace(1, 24, 24).data(), new int[]{ 4, 3, 2 });
        for (int i = 0; i < (arr.slices()); i++) {
            INDArray slice = arr.slice(i).slice(1);
            val slices = slice.slices();
            Assert.assertEquals(2, slices);
        }
    }

    @Test
    public void testSlice() {
        INDArray arr = Nd4j.linspace(1, 24, 24).reshape(4, 3, 2);
        INDArray assertion = Nd4j.create(new double[][]{ new double[]{ 1, 13 }, new double[]{ 5, 17 }, new double[]{ 9, 21 } });
        INDArray firstSlice = arr.slice(0);
        INDArray slice1Assertion = Nd4j.create(new double[][]{ new double[]{ 2, 14 }, new double[]{ 6, 18 }, new double[]{ 10, 22 } });
        INDArray secondSlice = arr.slice(1);
        Assert.assertEquals(assertion, firstSlice);
        Assert.assertEquals(slice1Assertion, secondSlice);
    }
}

