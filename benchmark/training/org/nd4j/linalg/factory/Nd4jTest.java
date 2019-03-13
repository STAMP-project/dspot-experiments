package org.nd4j.linalg.factory;


import NDArrayFactory.C;
import NDArrayFactory.FORTRAN;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.val;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.rng.Random;
import org.nd4j.linalg.checkutil.NDArrayCreationUtil;
import org.nd4j.linalg.primitives.Pair;


/**
 *
 */
@RunWith(Parameterized.class)
public class Nd4jTest extends BaseNd4jTest {
    public Nd4jTest(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testRandShapeAndRNG() {
        INDArray ret = Nd4j.rand(new int[]{ 4, 2 }, Nd4j.getRandomFactory().getNewRandomInstance(123));
        INDArray ret2 = Nd4j.rand(new int[]{ 4, 2 }, Nd4j.getRandomFactory().getNewRandomInstance(123));
        Assert.assertEquals(ret, ret2);
    }

    @Test
    public void testRandShapeAndMinMax() {
        INDArray ret = Nd4j.rand(new int[]{ 4, 2 }, (-0.125F), 0.125F, Nd4j.getRandomFactory().getNewRandomInstance(123));
        INDArray ret2 = Nd4j.rand(new int[]{ 4, 2 }, (-0.125F), 0.125F, Nd4j.getRandomFactory().getNewRandomInstance(123));
        Assert.assertEquals(ret, ret2);
    }

    @Test
    public void testCreateShape() {
        INDArray ret = Nd4j.create(new int[]{ 4, 2 });
        Assert.assertEquals(ret.length(), 8);
    }

    @Test
    public void testCreateFromList() {
        List<Double> doubles = Arrays.asList(1.0, 2.0);
        INDArray NdarrayDobules = Nd4j.create(doubles);
        Assert.assertEquals(((Double) (NdarrayDobules.getDouble(0))), doubles.get(0));
        Assert.assertEquals(((Double) (NdarrayDobules.getDouble(1))), doubles.get(1));
        List<Float> floats = Arrays.asList(3.0F, 4.0F);
        INDArray NdarrayFloats = Nd4j.create(floats);
        Assert.assertEquals(((Float) (NdarrayFloats.getFloat(0))), floats.get(0));
        Assert.assertEquals(((Float) (NdarrayFloats.getFloat(1))), floats.get(1));
    }

    @Test
    public void testGetRandom() {
        Random r = Nd4j.getRandom();
        Random t = Nd4j.getRandom();
        Assert.assertEquals(r, t);
    }

    @Test
    public void testGetRandomSetSeed() {
        Random r = Nd4j.getRandom();
        Random t = Nd4j.getRandom();
        Assert.assertEquals(r, t);
        r.setSeed(123);
        Assert.assertEquals(r, t);
    }

    @Test
    public void testOrdering() {
        INDArray fNDArray = Nd4j.create(new float[]{ 1.0F }, FORTRAN);
        Assert.assertEquals(FORTRAN, fNDArray.ordering());
        INDArray cNDArray = Nd4j.create(new float[]{ 1.0F }, C);
        Assert.assertEquals(C, cNDArray.ordering());
    }

    @Test
    public void testMean() {
        INDArray data = Nd4j.create(new double[]{ 4.0, 4.0, 4.0, 4.0, 8.0, 8.0, 8.0, 8.0, 4.0, 4.0, 4.0, 4.0, 8.0, 8.0, 8.0, 8.0, 4.0, 4.0, 4.0, 4.0, 8.0, 8.0, 8.0, 8.0, 4.0, 4.0, 4.0, 4.0, 8.0, 8.0, 8.0, 8, 2.0, 2.0, 2.0, 2.0, 4.0, 4.0, 4.0, 4.0, 2.0, 2.0, 2.0, 2.0, 4.0, 4.0, 4.0, 4.0, 2.0, 2.0, 2.0, 2.0, 4.0, 4.0, 4.0, 4.0, 2.0, 2.0, 2.0, 2.0, 4.0, 4.0, 4.0, 4.0 }, new int[]{ 2, 2, 4, 4 });
        INDArray actualResult = data.mean(0);
        INDArray expectedResult = Nd4j.create(new double[]{ 3.0, 3.0, 3.0, 3.0, 6.0, 6.0, 6.0, 6.0, 3.0, 3.0, 3.0, 3.0, 6.0, 6.0, 6.0, 6.0, 3.0, 3.0, 3.0, 3.0, 6.0, 6.0, 6.0, 6.0, 3.0, 3.0, 3.0, 3.0, 6.0, 6.0, 6.0, 6.0 }, new int[]{ 2, 4, 4 });
        Assert.assertEquals(getFailureMessage(), expectedResult, actualResult);
    }

    @Test
    public void testVar() {
        INDArray data = Nd4j.create(new double[]{ 4.0, 4.0, 4.0, 4.0, 8.0, 8.0, 8.0, 8.0, 4.0, 4.0, 4.0, 4.0, 8.0, 8.0, 8.0, 8.0, 4.0, 4.0, 4.0, 4.0, 8.0, 8.0, 8.0, 8.0, 4.0, 4.0, 4.0, 4.0, 8.0, 8.0, 8.0, 8, 2.0, 2.0, 2.0, 2.0, 4.0, 4.0, 4.0, 4.0, 2.0, 2.0, 2.0, 2.0, 4.0, 4.0, 4.0, 4.0, 2.0, 2.0, 2.0, 2.0, 4.0, 4.0, 4.0, 4.0, 2.0, 2.0, 2.0, 2.0, 4.0, 4.0, 4.0, 4.0 }, new int[]{ 2, 2, 4, 4 });
        INDArray actualResult = data.var(false, 0);
        INDArray expectedResult = Nd4j.create(new double[]{ 1.0, 1.0, 1.0, 1.0, 4.0, 4.0, 4.0, 4.0, 1.0, 1.0, 1.0, 1.0, 4.0, 4.0, 4.0, 4.0, 1.0, 1.0, 1.0, 1.0, 4.0, 4.0, 4.0, 4.0, 1.0, 1.0, 1.0, 1.0, 4.0, 4.0, 4.0, 4.0 }, new int[]{ 2, 4, 4 });
        Assert.assertEquals(getFailureMessage(), expectedResult, actualResult);
    }

    @Test
    public void testVar2() {
        INDArray arr = Nd4j.linspace(1, 6, 6).reshape(2, 3);
        INDArray var = arr.var(false, 0);
        Assert.assertEquals(Nd4j.create(new double[]{ 2.25, 2.25, 2.25 }), var);
    }

    @Test
    public void testExpandDims() {
        final List<Pair<INDArray, String>> testMatricesC = NDArrayCreationUtil.getAllTestMatricesWithShape('c', 3, 5, 57005);
        final List<Pair<INDArray, String>> testMatricesF = NDArrayCreationUtil.getAllTestMatricesWithShape('f', 7, 11, 48879);
        final ArrayList<Pair<INDArray, String>> testMatrices = new ArrayList(testMatricesC);
        testMatrices.addAll(testMatricesF);
        for (Pair<INDArray, String> testMatrixPair : testMatrices) {
            final String recreation = testMatrixPair.getSecond();
            final INDArray testMatrix = testMatrixPair.getFirst();
            final char ordering = testMatrix.ordering();
            val shape = testMatrix.shape();
            final int rank = testMatrix.rank();
            for (int i = -rank; i <= rank; i++) {
                final INDArray expanded = Nd4j.expandDims(testMatrix, i);
                final String message = (((((((((("Expanding in Dimension " + i) + "; Shape before expanding: ") + (Arrays.toString(shape))) + " ") + ordering) + " Order; Shape after expanding: ") + (Arrays.toString(expanded.shape()))) + " ") + (expanded.ordering())) + "; Input Created via: ") + recreation;
                Assert.assertEquals(message, 1, expanded.shape()[(i < 0 ? i + rank : i)]);
                Assert.assertEquals(message, testMatrix.ravel(), expanded.ravel());
                Assert.assertEquals(message, ordering, expanded.ordering());
                testMatrix.assign(Nd4j.rand(shape));
                Assert.assertEquals(message, testMatrix.ravel(), expanded.ravel());
            }
        }
    }
}

