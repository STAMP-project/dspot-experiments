package org.nd4j.linalg.shape.concat;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;


/**
 *
 *
 * @author Adam Gibson
 */
@RunWith(Parameterized.class)
public class ConcatTestsC extends BaseNd4jTest {
    public ConcatTestsC(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testConcatVertically() {
        INDArray rowVector = Nd4j.ones(5);
        INDArray other = Nd4j.ones(5);
        INDArray concat = Nd4j.vstack(other, rowVector);
        Assert.assertEquals(((rowVector.rows()) * 2), concat.rows());
        Assert.assertEquals(rowVector.columns(), concat.columns());
        INDArray arr2 = Nd4j.create(5, 5);
        INDArray slice1 = arr2.slice(0);
        INDArray slice2 = arr2.slice(1);
        INDArray arr3 = Nd4j.create(2, 5);
        INDArray vstack = Nd4j.vstack(slice1, slice2);
        Assert.assertEquals(arr3, vstack);
        INDArray col1 = arr2.getColumn(0);
        INDArray col2 = arr2.getColumn(1);
        INDArray vstacked = Nd4j.vstack(col1, col2);
        Assert.assertEquals(Nd4j.create(10, 1), vstacked);
    }

    @Test
    public void testConcatScalars() {
        INDArray first = Nd4j.arange(0, 1).reshape(1, 1);
        INDArray second = Nd4j.arange(0, 1).reshape(1, 1);
        INDArray firstRet = Nd4j.concat(0, first, second);
        Assert.assertTrue(firstRet.isColumnVector());
        INDArray secondRet = Nd4j.concat(1, first, second);
        Assert.assertTrue(secondRet.isRowVector());
    }

    @Test
    public void testConcatScalars1() {
        INDArray first = Nd4j.scalar(1);
        INDArray second = Nd4j.scalar(2);
        INDArray third = Nd4j.scalar(3);
        INDArray result = Nd4j.concat(0, first, second, third);
        Assert.assertEquals(1.0F, result.getFloat(0), 0.01F);
        Assert.assertEquals(2.0F, result.getFloat(1), 0.01F);
        Assert.assertEquals(3.0F, result.getFloat(2), 0.01F);
    }

    @Test
    public void testConcatVectors1() {
        INDArray first = Nd4j.ones(10);
        INDArray second = Nd4j.ones(10);
        INDArray third = Nd4j.ones(10);
        INDArray result = Nd4j.concat(0, first, second, third);
        Assert.assertEquals(3, result.rows());
        Assert.assertEquals(10, result.columns());
        System.out.println(result);
        for (int x = 0; x < 30; x++) {
            Assert.assertEquals(1.0F, result.getFloat(x), 0.001F);
        }
    }

    @Test
    public void testConcatMatrices() {
        INDArray a = Nd4j.linspace(1, 4, 4).reshape(2, 2);
        INDArray b = a.dup();
        INDArray concat1 = Nd4j.concat(1, a, b);
        INDArray oneAssertion = Nd4j.create(new double[][]{ new double[]{ 1, 2, 1, 2 }, new double[]{ 3, 4, 3, 4 } });
        System.out.println(("Assertion: " + (Arrays.toString(oneAssertion.data().asFloat()))));
        System.out.println(("Result: " + (Arrays.toString(concat1.data().asFloat()))));
        Assert.assertEquals(oneAssertion, concat1);
        INDArray concat = Nd4j.concat(0, a, b);
        INDArray zeroAssertion = Nd4j.create(new double[][]{ new double[]{ 1, 2 }, new double[]{ 3, 4 }, new double[]{ 1, 2 }, new double[]{ 3, 4 } });
        Assert.assertEquals(zeroAssertion, concat);
    }

    @Test
    public void testAssign() {
        INDArray vector = Nd4j.linspace(1, 5, 5);
        vector.assign(1);
        Assert.assertEquals(Nd4j.ones(5), vector);
        INDArray twos = Nd4j.ones(2, 2);
        INDArray rand = Nd4j.rand(2, 2);
        twos.assign(rand);
        Assert.assertEquals(rand, twos);
        INDArray tensor = Nd4j.rand(((long) (3)), 3, 3, 3);
        INDArray ones = Nd4j.ones(3, 3, 3);
        Assert.assertTrue(Arrays.equals(tensor.shape(), ones.shape()));
        ones.assign(tensor);
        Assert.assertEquals(tensor, ones);
    }

    @Test
    public void testConcatRowVectors() {
        INDArray rowVector = Nd4j.create(new double[]{ 1, 2, 3, 4, 5, 6 }, new int[]{ 1, 6 });
        INDArray matrix = Nd4j.create(new double[]{ 7, 8, 9, 10, 11, 12 }, new int[]{ 1, 6 });
        INDArray assertion1 = Nd4j.create(new double[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 }, new int[]{ 1, 12 });
        INDArray assertion0 = Nd4j.create(new double[][]{ new double[]{ 1, 2, 3, 4, 5, 6 }, new double[]{ 7, 8, 9, 10, 11, 12 } });
        INDArray concat1 = Nd4j.hstack(rowVector, matrix);
        INDArray concat0 = Nd4j.vstack(rowVector, matrix);
        Assert.assertEquals(assertion1, concat1);
        Assert.assertEquals(assertion0, concat0);
    }

    @Test
    public void testConcat3d() {
        INDArray first = Nd4j.linspace(1, 24, 24).reshape('c', 2, 3, 4);
        INDArray second = Nd4j.linspace(24, 36, 12).reshape('c', 1, 3, 4);
        INDArray third = Nd4j.linspace(36, 48, 12).reshape('c', 1, 3, 4);
        // ConcatV2, dim 0
        INDArray exp = Nd4j.create(((2 + 1) + 1), 3, 4);
        exp.put(new INDArrayIndex[]{ NDArrayIndex.interval(0, 2), NDArrayIndex.all(), NDArrayIndex.all() }, first);
        exp.put(new INDArrayIndex[]{ NDArrayIndex.point(2), NDArrayIndex.all(), NDArrayIndex.all() }, second);
        exp.put(new INDArrayIndex[]{ NDArrayIndex.point(3), NDArrayIndex.all(), NDArrayIndex.all() }, third);
        INDArray concat0 = Nd4j.concat(0, first, second, third);
        Assert.assertEquals(exp, concat0);
        // ConcatV2, dim 1
        second = Nd4j.linspace(24, 32, 8).reshape('c', 2, 1, 4);
        for (int i = 0; i < (second.tensorssAlongDimension(1)); i++) {
            INDArray secondTad = second.javaTensorAlongDimension(i, 1);
            System.out.println(second.tensorAlongDimension(i, 1));
        }
        third = Nd4j.linspace(32, 48, 16).reshape('c', 2, 2, 4);
        exp = Nd4j.create(2, ((3 + 1) + 2), 4);
        exp.put(new INDArrayIndex[]{ NDArrayIndex.all(), NDArrayIndex.interval(0, 3), NDArrayIndex.all() }, first);
        exp.put(new INDArrayIndex[]{ NDArrayIndex.all(), NDArrayIndex.point(3), NDArrayIndex.all() }, second);
        exp.put(new INDArrayIndex[]{ NDArrayIndex.all(), NDArrayIndex.interval(4, 6), NDArrayIndex.all() }, third);
        INDArray concat1 = Nd4j.concat(1, first, second, third);
        Assert.assertEquals(exp, concat1);
        // ConcatV2, dim 2
        second = Nd4j.linspace(24, 36, 12).reshape('c', 2, 3, 2);
        third = Nd4j.linspace(36, 42, 6).reshape('c', 2, 3, 1);
        exp = Nd4j.create(2, 3, ((4 + 2) + 1));
        exp.put(new INDArrayIndex[]{ NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.interval(0, 4) }, first);
        exp.put(new INDArrayIndex[]{ NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.interval(4, 6) }, second);
        exp.put(new INDArrayIndex[]{ NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(6) }, third);
        INDArray concat2 = Nd4j.concat(2, first, second, third);
        Assert.assertEquals(exp, concat2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConcatVector() {
        System.out.println(Nd4j.concat(0, Nd4j.ones(1000000), Nd4j.create(1)));
    }
}

