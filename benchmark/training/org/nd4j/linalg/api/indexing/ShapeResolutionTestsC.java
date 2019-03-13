package org.nd4j.linalg.api.indexing;


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
import org.nd4j.linalg.indexing.ShapeOffsetResolution;
import org.nd4j.linalg.util.ArrayUtil;


/**
 *
 *
 * @author Adam Gibson
 */
@RunWith(Parameterized.class)
public class ShapeResolutionTestsC extends BaseNd4jTest {
    public ShapeResolutionTestsC(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testRowVectorShapeOneZeroOffset() {
        INDArray arr = Nd4j.create(2, 2);
        ShapeOffsetResolution resolution = new ShapeOffsetResolution(arr);
        // row 0
        resolution.exec(NDArrayIndex.point(0));
        long[] oneIndexShape = ArrayUtil.copy(resolution.getShapes());
        BaseNd4jTest.assertArrayEquals(new long[]{ 1, 2 }, oneIndexShape);
        long[] oneIndexOffsets = ArrayUtil.copy(resolution.getOffsets());
        BaseNd4jTest.assertArrayEquals(new long[]{ 0, 0 }, oneIndexOffsets);
        Assert.assertEquals(0, resolution.getOffset());
        long[] oneIndexStrides = ArrayUtil.copy(resolution.getStrides());
        BaseNd4jTest.assertArrayEquals(new long[]{ 1, 1 }, oneIndexStrides);
    }

    @Test
    public void testIntervalFirstShapeResolution() {
        INDArray arr = Nd4j.linspace(1, 6, 6).reshape(3, 2);
        ShapeOffsetResolution resolution = new ShapeOffsetResolution(arr);
        resolution.exec(NDArrayIndex.interval(1, 3));
        Assert.assertFalse(Arrays.equals(arr.shape(), resolution.getShapes()));
    }

    @Test
    public void testRowVectorShapeOneOneOffset() {
        INDArray arr = Nd4j.create(2, 2);
        ShapeOffsetResolution resolution = new ShapeOffsetResolution(arr);
        // row 0
        resolution.exec(NDArrayIndex.point(1));
        long[] oneIndexShape = ArrayUtil.copy(resolution.getShapes());
        BaseNd4jTest.assertArrayEquals(new long[]{ 1, 2 }, oneIndexShape);
        Assert.assertEquals(2, resolution.getOffset());
        long[] oneIndexStrides = ArrayUtil.copy(resolution.getStrides());
        BaseNd4jTest.assertArrayEquals(new long[]{ 1, 1 }, oneIndexStrides);
    }

    @Test
    public void testRowVectorShapeTwoOneOffset() {
        INDArray arr = Nd4j.create(2, 2);
        ShapeOffsetResolution resolution = new ShapeOffsetResolution(arr);
        // row 0
        resolution.exec(NDArrayIndex.point(1), NDArrayIndex.all());
        long[] oneIndexShape = ArrayUtil.copy(resolution.getShapes());
        BaseNd4jTest.assertArrayEquals(new long[]{ 1, 2 }, oneIndexShape);
        long[] oneIndexOffsets = ArrayUtil.copy(resolution.getOffsets());
        BaseNd4jTest.assertArrayEquals(new long[]{ 0, 0 }, oneIndexOffsets);
        Assert.assertEquals(2, resolution.getOffset());
        long[] oneIndexStrides = ArrayUtil.copy(resolution.getStrides());
        BaseNd4jTest.assertArrayEquals(new long[]{ 1, 1 }, oneIndexStrides);
    }

    @Test
    public void testColumnVectorShapeZeroOffset() {
        INDArray arr = Nd4j.create(2, 2);
        ShapeOffsetResolution resolution = new ShapeOffsetResolution(arr);
        resolution.exec(NDArrayIndex.all(), NDArrayIndex.point(0));
        Assert.assertEquals(0, resolution.getOffset());
        long[] strides = resolution.getStrides();
        BaseNd4jTest.assertArrayEquals(new long[]{ 2, 1 }, resolution.getShapes());
        BaseNd4jTest.assertArrayEquals(new long[]{ 2, 1 }, strides);
    }

    @Test
    public void testColumnVectorShapeOneOffset() {
        INDArray arr = Nd4j.linspace(1, 4, 4).reshape(2, 2);
        ShapeOffsetResolution resolution = new ShapeOffsetResolution(arr);
        resolution.exec(NDArrayIndex.all(), NDArrayIndex.point(1));
        Assert.assertEquals(1, resolution.getOffset());
        long[] strides = resolution.getStrides();
        BaseNd4jTest.assertArrayEquals(new long[]{ 2, 1 }, resolution.getShapes());
        BaseNd4jTest.assertArrayEquals(new long[]{ 2, 1 }, strides);
    }

    @Test
    public void testPartiallyOutOfRangeIndices() {
        INDArray arr = Nd4j.linspace(1, 4, 4).reshape(2, 2);
        ShapeOffsetResolution resolution = new ShapeOffsetResolution(arr);
        resolution.exec(NDArrayIndex.interval(0, 2), NDArrayIndex.interval(1, 4));
        BaseNd4jTest.assertArrayEquals(new long[]{ 2, 1 }, resolution.getShapes());
    }

    @Test
    public void testOutOfRangeIndices() {
        INDArray arr = Nd4j.linspace(1, 4, 4).reshape(2, 2);
        ShapeOffsetResolution resolution = new ShapeOffsetResolution(arr);
        try {
            resolution.exec(NDArrayIndex.interval(0, 2), NDArrayIndex.interval(2, 4));
            Assert.fail("Out of range index should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // do nothing
        }
    }

    @Test
    public void testOutOfRangeIndices2() {
        INDArray arr = Nd4j.create(1, 4, 4);
        ShapeOffsetResolution resolution = new ShapeOffsetResolution(arr);
        try {
            resolution.exec(NDArrayIndex.point(1), NDArrayIndex.point(0), NDArrayIndex.point(0));
            Assert.fail("Out of range index should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // do nothing
        }
    }

    @Test
    public void testIndexAll() {
        INDArray arr = Nd4j.create(2, 2);
        ShapeOffsetResolution resolution = new ShapeOffsetResolution(arr);
        resolution.exec(NDArrayIndex.all(), NDArrayIndex.all());
        Assert.assertEquals(resolution.getShapes()[0], 2);
        Assert.assertEquals(resolution.getShapes()[1], 2);
    }

    @Test
    public void testFlatIndexPointInterval() {
        INDArray zeros = Nd4j.zeros(1, 4);
        INDArrayIndex x = NDArrayIndex.point(0);
        INDArrayIndex y = NDArrayIndex.interval(1, 2, true);
        INDArray value = Nd4j.ones(1, 2);
        zeros.put(new INDArrayIndex[]{ x, y }, value);
        INDArray assertion = Nd4j.create(new double[]{ 0.0, 1.0, 1.0, 0.0 });
        Assert.assertEquals(assertion, zeros);
    }

    @Test
    public void testVectorIndexPointPoint() {
        INDArray zeros = Nd4j.zeros(1, 4);
        INDArrayIndex x = NDArrayIndex.point(0);
        INDArrayIndex y = NDArrayIndex.point(2);
        INDArray value = Nd4j.ones(1, 1);
        zeros.put(new INDArrayIndex[]{ x, y }, value);
        INDArray assertion = Nd4j.create(new double[]{ 0.0, 0.0, 1.0, 0.0 });
        Assert.assertEquals(assertion, zeros);
    }

    @Test
    public void testVectorIndexPointPointOutOfRange() {
        INDArray zeros = Nd4j.zeros(1, 4);
        INDArrayIndex x = NDArrayIndex.point(0);
        INDArrayIndex y = NDArrayIndex.point(4);
        INDArray value = Nd4j.ones(1, 1);
        try {
            zeros.put(new INDArrayIndex[]{ x, y }, value);
            Assert.fail("Out of range index should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // do nothing
        }
    }

    @Test
    public void testVectorIndexPointPointOutOfRange2() {
        INDArray zeros = Nd4j.zeros(1, 4);
        INDArrayIndex x = NDArrayIndex.point(1);
        INDArrayIndex y = NDArrayIndex.point(2);
        INDArray value = Nd4j.ones(1, 1);
        try {
            zeros.put(new INDArrayIndex[]{ x, y }, value);
            Assert.fail("Out of range index should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // do nothing
        }
    }
}

