/**
 * Copyright 2017 JanusGraph Authors
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package org.janusgraph.graphdb.idmanagement;


import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Set;
import org.apache.commons.lang3.ArrayUtils;
import org.janusgraph.graphdb.database.idassigner.placement.PartitionIDRange;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


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
            Assertions.assertEquals(16, allIds.size());
            for (int i = 0; i < 16; i++) {
                Assertions.assertTrue(allIds.contains(i));
                Assertions.assertTrue(pr.contains(i));
            }
            Assertions.assertFalse(pr.contains(16));
            verifyRandomSampling(pr);
        }
        pr = new PartitionIDRange(13, 2, 16);
        Assertions.assertTrue(pr.contains(15));
        Assertions.assertTrue(pr.contains(1));
        Assertions.assertEquals(5, pr.getAllContainedIDs().length);
        verifyRandomSampling(pr);
        pr = new PartitionIDRange(512, 2, 2048);
        Assertions.assertEquals(((2048 - 512) + 2), pr.getAllContainedIDs().length);
        verifyRandomSampling(pr);
        pr = new PartitionIDRange(512, 1055, 2048);
        Assertions.assertEquals((1055 - 512), pr.getAllContainedIDs().length);
        verifyRandomSampling(pr);
        try {
            pr = new PartitionIDRange(0, 5, 4);
            Assertions.fail();
        } catch (IllegalArgumentException ignored) {
        }
        try {
            pr = new PartitionIDRange(5, 3, 4);
            Assertions.fail();
        } catch (IllegalArgumentException ignored) {
        }
        try {
            pr = new PartitionIDRange((-1), 3, 4);
            Assertions.fail();
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void convertIDRangesFromBits() {
        PartitionIDRange pr;
        for (int partitionBits : new int[]{ 0, 1, 4, 16, 5, 7, 2 }) {
            pr = Iterables.getOnlyElement(PartitionIDRange.getGlobalRange(partitionBits));
            Assertions.assertEquals((1 << partitionBits), pr.getUpperID());
            Assertions.assertEquals((1 << partitionBits), pr.getAllContainedIDs().length);
            if (partitionBits <= 10)
                verifyRandomSampling(pr);

        }
        try {
            PartitionIDRange.getGlobalRange((-1));
            Assertions.fail();
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void convertIDRangesFromBuffers() {
        PartitionIDRange pr;
        pr = PartitionIDRangeTest.getPIR(2, 2, 6, 3);
        Assertions.assertEquals(2, pr.getAllContainedIDs().length);
        Assertions.assertTrue(pr.contains(1));
        Assertions.assertTrue(pr.contains(2));
        Assertions.assertFalse(pr.contains(3));
        pr = PartitionIDRangeTest.getPIR(2, 3, 6, 3);
        Assertions.assertEquals(1, pr.getAllContainedIDs().length);
        Assertions.assertFalse(pr.contains(1));
        Assertions.assertTrue(pr.contains(2));
        Assertions.assertFalse(pr.contains(3));
        pr = PartitionIDRangeTest.getPIR(4, 2, 6, 3);
        Assertions.assertEquals(8, pr.getAllContainedIDs().length);
        pr = PartitionIDRangeTest.getPIR(2, 6, 6, 3);
        Assertions.assertEquals(4, pr.getAllContainedIDs().length);
        pr = PartitionIDRangeTest.getPIR(2, 7, 7, 3);
        Assertions.assertEquals(4, pr.getAllContainedIDs().length);
        pr = PartitionIDRangeTest.getPIR(2, 10, 9, 4);
        Assertions.assertEquals(3, pr.getAllContainedIDs().length);
        pr = PartitionIDRangeTest.getPIR(2, 5, 15, 4);
        Assertions.assertEquals(1, pr.getAllContainedIDs().length);
        pr = PartitionIDRangeTest.getPIR(2, 9, 16, 4);
        Assertions.assertEquals(1, pr.getAllContainedIDs().length);
        Assertions.assertTrue(pr.contains(3));
        Assertions.assertNull(PartitionIDRangeTest.getPIR(2, 11, 12, 4));
        Assertions.assertNull(PartitionIDRangeTest.getPIR(2, 5, 11, 4));
        Assertions.assertNull(PartitionIDRangeTest.getPIR(2, 9, 12, 4));
        Assertions.assertNull(PartitionIDRangeTest.getPIR(2, 9, 11, 4));
        Assertions.assertNull(PartitionIDRangeTest.getPIR(2, 13, 15, 4));
        Assertions.assertNull(PartitionIDRangeTest.getPIR(2, 13, 3, 4));
        pr = PartitionIDRangeTest.getPIR(2, 15, 14, 4);
        Assertions.assertEquals(3, pr.getAllContainedIDs().length);
        pr = PartitionIDRangeTest.getPIR(1, 7, 6, 3);
        Assertions.assertEquals(1, pr.getAllContainedIDs().length);
        Assertions.assertTrue(pr.contains(0));
    }
}

