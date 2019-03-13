/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hbase.regionserver.wal;


import HConstants.NO_SEQNUM;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(SmallTests.class)
public class TestSequenceIdAccounting {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSequenceIdAccounting.class);

    private static final byte[] ENCODED_REGION_NAME = Bytes.toBytes("r");

    private static final byte[] FAMILY_NAME = Bytes.toBytes("cf");

    private static final Set<byte[]> FAMILIES;

    static {
        FAMILIES = new HashSet<>();
        TestSequenceIdAccounting.FAMILIES.add(TestSequenceIdAccounting.FAMILY_NAME);
    }

    @Test
    public void testStartCacheFlush() {
        SequenceIdAccounting sida = new SequenceIdAccounting();
        sida.getOrCreateLowestSequenceIds(TestSequenceIdAccounting.ENCODED_REGION_NAME);
        Map<byte[], Long> m = new HashMap<>();
        m.put(TestSequenceIdAccounting.ENCODED_REGION_NAME, NO_SEQNUM);
        Assert.assertEquals(NO_SEQNUM, ((long) (sida.startCacheFlush(TestSequenceIdAccounting.ENCODED_REGION_NAME, TestSequenceIdAccounting.FAMILIES))));
        sida.completeCacheFlush(TestSequenceIdAccounting.ENCODED_REGION_NAME);
        long sequenceid = 1;
        sida.update(TestSequenceIdAccounting.ENCODED_REGION_NAME, TestSequenceIdAccounting.FAMILIES, sequenceid, true);
        // Only one family so should return NO_SEQNUM still.
        Assert.assertEquals(NO_SEQNUM, ((long) (sida.startCacheFlush(TestSequenceIdAccounting.ENCODED_REGION_NAME, TestSequenceIdAccounting.FAMILIES))));
        sida.completeCacheFlush(TestSequenceIdAccounting.ENCODED_REGION_NAME);
        long currentSequenceId = sequenceid;
        sida.update(TestSequenceIdAccounting.ENCODED_REGION_NAME, TestSequenceIdAccounting.FAMILIES, sequenceid, true);
        final Set<byte[]> otherFamily = new HashSet<>(1);
        otherFamily.add(Bytes.toBytes("otherCf"));
        sida.update(TestSequenceIdAccounting.ENCODED_REGION_NAME, TestSequenceIdAccounting.FAMILIES, (++sequenceid), true);
        // Should return oldest sequence id in the region.
        Assert.assertEquals(currentSequenceId, ((long) (sida.startCacheFlush(TestSequenceIdAccounting.ENCODED_REGION_NAME, otherFamily))));
        sida.completeCacheFlush(TestSequenceIdAccounting.ENCODED_REGION_NAME);
    }

    @Test
    public void testAreAllLower() {
        SequenceIdAccounting sida = new SequenceIdAccounting();
        sida.getOrCreateLowestSequenceIds(TestSequenceIdAccounting.ENCODED_REGION_NAME);
        Map<byte[], Long> m = new HashMap<>();
        m.put(TestSequenceIdAccounting.ENCODED_REGION_NAME, NO_SEQNUM);
        Assert.assertTrue(sida.areAllLower(m, null));
        long sequenceid = 1;
        sida.update(TestSequenceIdAccounting.ENCODED_REGION_NAME, TestSequenceIdAccounting.FAMILIES, sequenceid, true);
        sida.update(TestSequenceIdAccounting.ENCODED_REGION_NAME, TestSequenceIdAccounting.FAMILIES, (sequenceid++), true);
        sida.update(TestSequenceIdAccounting.ENCODED_REGION_NAME, TestSequenceIdAccounting.FAMILIES, (sequenceid++), true);
        Assert.assertTrue(sida.areAllLower(m, null));
        m.put(TestSequenceIdAccounting.ENCODED_REGION_NAME, sequenceid);
        Assert.assertFalse(sida.areAllLower(m, null));
        ArrayList<byte[]> regions = new ArrayList<>();
        Assert.assertFalse(sida.areAllLower(m, regions));
        Assert.assertEquals(1, regions.size());
        Assert.assertArrayEquals(TestSequenceIdAccounting.ENCODED_REGION_NAME, regions.get(0));
        long lowest = sida.getLowestSequenceId(TestSequenceIdAccounting.ENCODED_REGION_NAME);
        Assert.assertEquals("Lowest should be first sequence id inserted", 1, lowest);
        m.put(TestSequenceIdAccounting.ENCODED_REGION_NAME, lowest);
        Assert.assertFalse(sida.areAllLower(m, null));
        // Now make sure above works when flushing.
        sida.startCacheFlush(TestSequenceIdAccounting.ENCODED_REGION_NAME, TestSequenceIdAccounting.FAMILIES);
        Assert.assertFalse(sida.areAllLower(m, null));
        m.put(TestSequenceIdAccounting.ENCODED_REGION_NAME, NO_SEQNUM);
        Assert.assertTrue(sida.areAllLower(m, null));
        // Let the flush complete and if we ask if the sequenceid is lower, should be yes since no edits
        sida.completeCacheFlush(TestSequenceIdAccounting.ENCODED_REGION_NAME);
        m.put(TestSequenceIdAccounting.ENCODED_REGION_NAME, sequenceid);
        Assert.assertTrue(sida.areAllLower(m, null));
        // Flush again but add sequenceids while we are flushing.
        sida.update(TestSequenceIdAccounting.ENCODED_REGION_NAME, TestSequenceIdAccounting.FAMILIES, (sequenceid++), true);
        sida.update(TestSequenceIdAccounting.ENCODED_REGION_NAME, TestSequenceIdAccounting.FAMILIES, (sequenceid++), true);
        sida.update(TestSequenceIdAccounting.ENCODED_REGION_NAME, TestSequenceIdAccounting.FAMILIES, (sequenceid++), true);
        lowest = sida.getLowestSequenceId(TestSequenceIdAccounting.ENCODED_REGION_NAME);
        m.put(TestSequenceIdAccounting.ENCODED_REGION_NAME, lowest);
        Assert.assertFalse(sida.areAllLower(m, null));
        sida.startCacheFlush(TestSequenceIdAccounting.ENCODED_REGION_NAME, TestSequenceIdAccounting.FAMILIES);
        // The cache flush will clear out all sequenceid accounting by region.
        Assert.assertEquals(NO_SEQNUM, sida.getLowestSequenceId(TestSequenceIdAccounting.ENCODED_REGION_NAME));
        sida.completeCacheFlush(TestSequenceIdAccounting.ENCODED_REGION_NAME);
        // No new edits have gone in so no sequenceid to work with.
        Assert.assertEquals(NO_SEQNUM, sida.getLowestSequenceId(TestSequenceIdAccounting.ENCODED_REGION_NAME));
        // Make an edit behind all we'll put now into sida.
        m.put(TestSequenceIdAccounting.ENCODED_REGION_NAME, sequenceid);
        sida.update(TestSequenceIdAccounting.ENCODED_REGION_NAME, TestSequenceIdAccounting.FAMILIES, (++sequenceid), true);
        sida.update(TestSequenceIdAccounting.ENCODED_REGION_NAME, TestSequenceIdAccounting.FAMILIES, (++sequenceid), true);
        sida.update(TestSequenceIdAccounting.ENCODED_REGION_NAME, TestSequenceIdAccounting.FAMILIES, (++sequenceid), true);
        Assert.assertTrue(sida.areAllLower(m, null));
    }

    @Test
    public void testFindLower() {
        SequenceIdAccounting sida = new SequenceIdAccounting();
        sida.getOrCreateLowestSequenceIds(TestSequenceIdAccounting.ENCODED_REGION_NAME);
        Map<byte[], Long> m = new HashMap<>();
        m.put(TestSequenceIdAccounting.ENCODED_REGION_NAME, NO_SEQNUM);
        long sequenceid = 1;
        sida.update(TestSequenceIdAccounting.ENCODED_REGION_NAME, TestSequenceIdAccounting.FAMILIES, sequenceid, true);
        sida.update(TestSequenceIdAccounting.ENCODED_REGION_NAME, TestSequenceIdAccounting.FAMILIES, (sequenceid++), true);
        sida.update(TestSequenceIdAccounting.ENCODED_REGION_NAME, TestSequenceIdAccounting.FAMILIES, (sequenceid++), true);
        Assert.assertTrue(((sida.findLower(m)) == null));
        m.put(TestSequenceIdAccounting.ENCODED_REGION_NAME, sida.getLowestSequenceId(TestSequenceIdAccounting.ENCODED_REGION_NAME));
        Assert.assertTrue(((sida.findLower(m).length) == 1));
        m.put(TestSequenceIdAccounting.ENCODED_REGION_NAME, ((sida.getLowestSequenceId(TestSequenceIdAccounting.ENCODED_REGION_NAME)) - 1));
        Assert.assertTrue(((sida.findLower(m)) == null));
    }
}

