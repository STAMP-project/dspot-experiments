package org.nd4j.linalg.api.blas;


import org.junit.Assert;
import org.junit.Ignore;
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
 * @author Audrey Loeffel
 */
// temporary ignored
@Ignore
@RunWith(Parameterized.class)
public class SparseCOOLevel2Test extends BaseNd4jTest {
    // matrix = [[1, 2], [0, 0]]
    private double[] data = new double[]{ 1, 2 };

    private int[][] indexes = new int[][]{ new int[]{ 0, 0 }, new int[]{ 0, 1 } };

    private int[] shape = new int[]{ 2, 2 };

    public SparseCOOLevel2Test(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testGemv() {
        INDArray array1 = Nd4j.createSparseCOO(data, indexes, shape);
        INDArray array2 = Nd4j.linspace(1, 2, 2).reshape(2, 1);
        INDArray array3 = array1.mmul(array2);// should be [5, 0]

        Assert.assertEquals(2, array3.length());
        Assert.assertEquals(5, array3.getFloat(0), 1.0E-5);
        Assert.assertEquals(0, array3.getFloat(1), 1.0E-5);
    }
}

