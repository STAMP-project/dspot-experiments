package com.thinkaurelius.titan.graphdb.idmanagement;


import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.thinkaurelius.titan.graphdb.database.idassigner.placement.PartitionIDRange;
import java.util.Arrays;
import java.util.Set;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class PartitionIDRangeTest {
    @Test
    public void basicIDRangeTest() {
        PartitionIDRange pr;
        for (int[] bounds : new int[][]{ new int[]{ 0, 16 }, new int[]{ 5, 5 }, new int[]{ 9, 9 }, new int[]{ 0, 0 } }) {
            pr = new PartitionIDRange(bounds[0], bounds[1], 16);
            Set<Integer> allIds = Sets.newHashSet(Arrays.asList(ArrayUtils.toObject(pr.getAllContainedIDs())));
            Assert.assertEquals(16, allIds.size());
            for (int i = 0; i < 16; i++) {
                Assert.assertTrue(allIds.contains(i));
                Assert.assertTrue(pr.contains(i));
            }
            Assert.assertFalse(pr.contains(16));
            verifyRandomSampling(pr);
        }
        pr = new PartitionIDRange(13, 2, 16);
        Assert.assertTrue(pr.contains(15));
        Assert.assertTrue(pr.contains(1));
        Assert.assertEquals(5, pr.getAllContainedIDs().length);
        verifyRandomSampling(pr);
        pr = new PartitionIDRange(512, 2, 2048);
        Assert.assertEquals(((2048 - 512) + 2), pr.getAllContainedIDs().length);
        verifyRandomSampling(pr);
        pr = new PartitionIDRange(512, 1055, 2048);
        Assert.assertEquals((1055 - 512), pr.getAllContainedIDs().length);
        verifyRandomSampling(pr);
        try {
            pr = new PartitionIDRange(0, 5, 4);
            Assert.fail();
        } catch (IllegalArgumentException e) {
        }
        try {
            pr = new PartitionIDRange(5, 3, 4);
            Assert.fail();
        } catch (IllegalArgumentException e) {
        }
        try {
            pr = new PartitionIDRange((-1), 3, 4);
            Assert.fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void convertIDRangesFromBits() {
        PartitionIDRange pr;
        for (int partitionBits : new int[]{ 0, 1, 4, 16, 5, 7, 2 }) {
            pr = Iterables.getOnlyElement(PartitionIDRange.getGlobalRange(partitionBits));
            Assert.assertEquals((1 << partitionBits), pr.getUpperID());
            Assert.assertEquals((1 << partitionBits), pr.getAllContainedIDs().length);
            if (partitionBits <= 10)
                verifyRandomSampling(pr);

        }
        try {
            PartitionIDRange.getGlobalRange((-1));
            Assert.fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void convertIDRangesFromBuffers() {
        PartitionIDRange pr;
        pr = PartitionIDRangeTest.getPIR(2, 2, 6, 3);
        Assert.assertEquals(2, pr.getAllContainedIDs().length);
        Assert.assertTrue(pr.contains(1));
        Assert.assertTrue(pr.contains(2));
        Assert.assertFalse(pr.contains(3));
        pr = PartitionIDRangeTest.getPIR(2, 3, 6, 3);
        Assert.assertEquals(1, pr.getAllContainedIDs().length);
        Assert.assertFalse(pr.contains(1));
        Assert.assertTrue(pr.contains(2));
        Assert.assertFalse(pr.contains(3));
        pr = PartitionIDRangeTest.getPIR(4, 2, 6, 3);
        Assert.assertEquals(8, pr.getAllContainedIDs().length);
        pr = PartitionIDRangeTest.getPIR(2, 6, 6, 3);
        Assert.assertEquals(4, pr.getAllContainedIDs().length);
        pr = PartitionIDRangeTest.getPIR(2, 7, 7, 3);
        Assert.assertEquals(4, pr.getAllContainedIDs().length);
        pr = PartitionIDRangeTest.getPIR(2, 10, 9, 4);
        Assert.assertEquals(3, pr.getAllContainedIDs().length);
        pr = PartitionIDRangeTest.getPIR(2, 5, 15, 4);
        Assert.assertEquals(1, pr.getAllContainedIDs().length);
        pr = PartitionIDRangeTest.getPIR(2, 9, 16, 4);
        Assert.assertEquals(1, pr.getAllContainedIDs().length);
        Assert.assertTrue(pr.contains(3));
        Assert.assertNull(PartitionIDRangeTest.getPIR(2, 11, 12, 4));
        Assert.assertNull(PartitionIDRangeTest.getPIR(2, 5, 11, 4));
        Assert.assertNull(PartitionIDRangeTest.getPIR(2, 9, 12, 4));
        Assert.assertNull(PartitionIDRangeTest.getPIR(2, 9, 11, 4));
        Assert.assertNull(PartitionIDRangeTest.getPIR(2, 13, 15, 4));
        Assert.assertNull(PartitionIDRangeTest.getPIR(2, 13, 3, 4));
        pr = PartitionIDRangeTest.getPIR(2, 15, 14, 4);
        Assert.assertEquals(3, pr.getAllContainedIDs().length);
        pr = PartitionIDRangeTest.getPIR(1, 7, 6, 3);
        Assert.assertEquals(1, pr.getAllContainedIDs().length);
        Assert.assertTrue(pr.contains(0));
    }
}

