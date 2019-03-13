package org.nd4j.linalg.api.indexing;


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
public class IndexingIterationTests extends BaseNd4jTest {
    public IndexingIterationTests(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testAll() {
        INDArrayIndex all = NDArrayIndex.all();
        INDArray init = Nd4j.create(2, 2);
        all.init(init, 1);
        Assert.assertTrue(all.hasNext());
        Assert.assertEquals(0, all.current());
        Assert.assertEquals(0, all.next());
        Assert.assertEquals(2, all.length());
        Assert.assertEquals(1, all.next());
        Assert.assertFalse(all.hasNext());
    }

    @Test
    public void testInterval() {
        INDArrayIndex interval = NDArrayIndex.interval(0, 2);
        Assert.assertTrue(interval.hasNext());
        Assert.assertEquals(2, interval.length());
        Assert.assertEquals(0, interval.next());
        Assert.assertEquals(1, interval.next());
        Assert.assertFalse(interval.hasNext());
    }

    @Test
    public void testIntervalInclusive() {
        INDArrayIndex interval = NDArrayIndex.interval(0, 1, 2, true);
        Assert.assertTrue(interval.hasNext());
        Assert.assertEquals(3, interval.end());
        Assert.assertEquals(3, interval.length());
        Assert.assertEquals(0, interval.next());
        Assert.assertEquals(1, interval.next());
        Assert.assertTrue(interval.hasNext());
        Assert.assertEquals(2, interval.next());
        Assert.assertFalse(interval.hasNext());
    }

    @Test
    public void testIntervalWithStride() {
        INDArrayIndex interval = NDArrayIndex.interval(3, 2, 6);
        Assert.assertTrue(interval.hasNext());
        Assert.assertEquals(2, interval.length());
        Assert.assertEquals(3, interval.next());
        Assert.assertTrue(interval.hasNext());
        Assert.assertEquals(5, interval.next());
        Assert.assertFalse(interval.hasNext());
    }

    @Test
    public void testNewAxis() {
        INDArrayIndex newAxis = NDArrayIndex.newAxis();
        Assert.assertEquals(0, newAxis.length());
        Assert.assertFalse(newAxis.hasNext());
    }

    @Test
    public void testIntervalStrideGreaterThan1() {
        INDArrayIndex interval = NDArrayIndex.interval(0, 2, 2);
        Assert.assertTrue(interval.hasNext());
        Assert.assertEquals(1, interval.length());
        Assert.assertEquals(0, interval.next());
        Assert.assertFalse(interval.hasNext());
    }

    @Test
    public void testPoint() {
        INDArrayIndex point = new PointIndex(1);
        Assert.assertTrue(point.hasNext());
        Assert.assertEquals(1, point.length());
        Assert.assertEquals(1, point.next());
        Assert.assertFalse(point.hasNext());
    }

    @Test
    public void testEmpty() {
        INDArrayIndex empty = new NDArrayIndexEmpty();
        Assert.assertFalse(empty.hasNext());
        Assert.assertEquals(0, empty.length());
    }

    @Test
    public void testSpecifiedIndex() {
        INDArrayIndex indArrayIndex = new SpecifiedIndex(2);
        Assert.assertEquals(1, indArrayIndex.length());
        Assert.assertTrue(indArrayIndex.hasNext());
        Assert.assertEquals(2, indArrayIndex.next());
        Assert.assertEquals(2, indArrayIndex.current());
        Assert.assertEquals(2, indArrayIndex.end());
        Assert.assertEquals(indArrayIndex.offset(), indArrayIndex.end());
    }
}

