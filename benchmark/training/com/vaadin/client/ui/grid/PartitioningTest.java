package com.vaadin.client.ui.grid;


import com.vaadin.shared.Range;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("static-method")
public class PartitioningTest {
    @Test
    public void selfRangeTest() {
        final Range range = Range.between(0, 10);
        final Range[] partitioning = range.partitionWith(range);
        Assert.assertTrue("before is empty", partitioning[0].isEmpty());
        Assert.assertTrue("inside is self", partitioning[1].equals(range));
        Assert.assertTrue("after is empty", partitioning[2].isEmpty());
    }

    @Test
    public void beforeRangeTest() {
        final Range beforeRange = Range.between(0, 10);
        final Range afterRange = Range.between(10, 20);
        final Range[] partitioning = beforeRange.partitionWith(afterRange);
        Assert.assertTrue("before is self", partitioning[0].equals(beforeRange));
        Assert.assertTrue("inside is empty", partitioning[1].isEmpty());
        Assert.assertTrue("after is empty", partitioning[2].isEmpty());
    }

    @Test
    public void afterRangeTest() {
        final Range beforeRange = Range.between(0, 10);
        final Range afterRange = Range.between(10, 20);
        final Range[] partitioning = afterRange.partitionWith(beforeRange);
        Assert.assertTrue("before is empty", partitioning[0].isEmpty());
        Assert.assertTrue("inside is empty", partitioning[1].isEmpty());
        Assert.assertTrue("after is self", partitioning[2].equals(afterRange));
    }

    @Test
    public void beforeAndInsideRangeTest() {
        final Range beforeRange = Range.between(0, 10);
        final Range afterRange = Range.between(5, 15);
        final Range[] partitioning = beforeRange.partitionWith(afterRange);
        Assert.assertEquals("before", Range.between(0, 5), partitioning[0]);
        Assert.assertEquals("inside", Range.between(5, 10), partitioning[1]);
        Assert.assertTrue("after is empty", partitioning[2].isEmpty());
    }

    @Test
    public void insideRangeTest() {
        final Range fullRange = Range.between(0, 20);
        final Range insideRange = Range.between(5, 15);
        final Range[] partitioning = insideRange.partitionWith(fullRange);
        Assert.assertTrue("before is empty", partitioning[0].isEmpty());
        Assert.assertEquals("inside", Range.between(5, 15), partitioning[1]);
        Assert.assertTrue("after is empty", partitioning[2].isEmpty());
    }

    @Test
    public void insideAndBelowTest() {
        final Range beforeRange = Range.between(0, 10);
        final Range afterRange = Range.between(5, 15);
        final Range[] partitioning = afterRange.partitionWith(beforeRange);
        Assert.assertTrue("before is empty", partitioning[0].isEmpty());
        Assert.assertEquals("inside", Range.between(5, 10), partitioning[1]);
        Assert.assertEquals("after", Range.between(10, 15), partitioning[2]);
    }

    @Test
    public void aboveAndBelowTest() {
        final Range fullRange = Range.between(0, 20);
        final Range insideRange = Range.between(5, 15);
        final Range[] partitioning = fullRange.partitionWith(insideRange);
        Assert.assertEquals("before", Range.between(0, 5), partitioning[0]);
        Assert.assertEquals("inside", Range.between(5, 15), partitioning[1]);
        Assert.assertEquals("after", Range.between(15, 20), partitioning[2]);
    }
}

