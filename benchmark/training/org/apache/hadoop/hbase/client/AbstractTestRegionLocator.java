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
package org.apache.hadoop.hbase.client;


import HConstants.EMPTY_START_ROW;
import TableName.META_TABLE_NAME;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HRegionLocation;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Pair;
import org.junit.Assert;
import org.junit.Test;


public abstract class AbstractTestRegionLocator {
    protected static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    protected static TableName TABLE_NAME = TableName.valueOf("Locator");

    protected static byte[] FAMILY = Bytes.toBytes("family");

    protected static int REGION_REPLICATION = 3;

    protected static byte[][] SPLIT_KEYS;

    @Test
    public void testStartEndKeys() throws IOException {
        assertStartKeys(getStartKeys(AbstractTestRegionLocator.TABLE_NAME));
        assertEndKeys(getEndKeys(AbstractTestRegionLocator.TABLE_NAME));
        Pair<byte[][], byte[][]> startEndKeys = getStartEndKeys(AbstractTestRegionLocator.TABLE_NAME);
        assertStartKeys(startEndKeys.getFirst());
        assertEndKeys(startEndKeys.getSecond());
    }

    @Test
    public void testGetRegionLocation() throws IOException {
        for (int i = 0; i <= (AbstractTestRegionLocator.SPLIT_KEYS.length); i++) {
            for (int replicaId = 0; replicaId < (AbstractTestRegionLocator.REGION_REPLICATION); replicaId++) {
                assertRegionLocation(getRegionLocation(AbstractTestRegionLocator.TABLE_NAME, getStartKey(i), replicaId), i, replicaId);
            }
        }
    }

    @Test
    public void testGetRegionLocations() throws IOException {
        for (int i = 0; i <= (AbstractTestRegionLocator.SPLIT_KEYS.length); i++) {
            List<HRegionLocation> locs = getRegionLocations(AbstractTestRegionLocator.TABLE_NAME, getStartKey(i));
            Assert.assertEquals(AbstractTestRegionLocator.REGION_REPLICATION, locs.size());
            for (int replicaId = 0; replicaId < (AbstractTestRegionLocator.REGION_REPLICATION); replicaId++) {
                assertRegionLocation(locs.get(replicaId), i, replicaId);
            }
        }
    }

    @Test
    public void testGetAllRegionLocations() throws IOException {
        List<HRegionLocation> locs = getAllRegionLocations(AbstractTestRegionLocator.TABLE_NAME);
        Assert.assertEquals(((AbstractTestRegionLocator.REGION_REPLICATION) * ((AbstractTestRegionLocator.SPLIT_KEYS.length) + 1)), locs.size());
        Collections.sort(locs, ( l1, l2) -> {
            int c = Bytes.compareTo(l1.getRegion().getStartKey(), l2.getRegion().getStartKey());
            if (c != 0) {
                return c;
            }
            return Integer.compare(l1.getRegion().getReplicaId(), l2.getRegion().getReplicaId());
        });
        for (int i = 0; i <= (AbstractTestRegionLocator.SPLIT_KEYS.length); i++) {
            for (int replicaId = 0; replicaId < (AbstractTestRegionLocator.REGION_REPLICATION); replicaId++) {
                assertRegionLocation(locs.get(((i * (AbstractTestRegionLocator.REGION_REPLICATION)) + replicaId)), i, replicaId);
            }
        }
    }

    @Test
    public void testMeta() throws IOException {
        assertMetaStartOrEndKeys(getStartKeys(META_TABLE_NAME));
        assertMetaStartOrEndKeys(getEndKeys(META_TABLE_NAME));
        Pair<byte[][], byte[][]> startEndKeys = getStartEndKeys(META_TABLE_NAME);
        assertMetaStartOrEndKeys(startEndKeys.getFirst());
        assertMetaStartOrEndKeys(startEndKeys.getSecond());
        for (int replicaId = 0; replicaId < (AbstractTestRegionLocator.REGION_REPLICATION); replicaId++) {
            assertMetaRegionLocation(getRegionLocation(META_TABLE_NAME, EMPTY_START_ROW, replicaId), replicaId);
        }
        assertMetaRegionLocations(getRegionLocations(META_TABLE_NAME, EMPTY_START_ROW));
        assertMetaRegionLocations(getAllRegionLocations(META_TABLE_NAME));
    }
}

