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
package org.apache.hadoop.hbase.coprocessor;


import Coprocessor.PRIORITY_HIGHEST;
import Coprocessor.PRIORITY_LOWEST;
import Coprocessor.PRIORITY_USER;
import java.io.IOException;
import java.util.Optional;
import junit.framework.TestCase;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Coprocessor;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.client.Durability;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.regionserver.RegionCoprocessorHost;
import org.apache.hadoop.hbase.testclassification.CoprocessorTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.wal.WALEdit;
import org.junit.ClassRule;
import org.junit.experimental.categories.Category;


@Category({ CoprocessorTests.class, SmallTests.class })
public class TestRegionObserverStacking extends TestCase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRegionObserverStacking.class);

    private static HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    static final Path DIR = getDataTestDir();

    public static class ObserverA implements RegionCoprocessor , RegionObserver {
        long id;

        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }

        @Override
        public void postPut(final ObserverContext<RegionCoprocessorEnvironment> c, final Put put, final WALEdit edit, final Durability durability) throws IOException {
            id = System.currentTimeMillis();
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
        }
    }

    public static class ObserverB implements RegionCoprocessor , RegionObserver {
        long id;

        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }

        @Override
        public void postPut(final ObserverContext<RegionCoprocessorEnvironment> c, final Put put, final WALEdit edit, final Durability durability) throws IOException {
            id = System.currentTimeMillis();
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
        }
    }

    public static class ObserverC implements RegionCoprocessor , RegionObserver {
        long id;

        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }

        @Override
        public void postPut(final ObserverContext<RegionCoprocessorEnvironment> c, final Put put, final WALEdit edit, final Durability durability) throws IOException {
            id = System.currentTimeMillis();
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
        }
    }

    public void testRegionObserverStacking() throws Exception {
        byte[] ROW = Bytes.toBytes("testRow");
        byte[] TABLE = Bytes.toBytes(this.getClass().getSimpleName());
        byte[] A = Bytes.toBytes("A");
        byte[][] FAMILIES = new byte[][]{ A };
        Configuration conf = TestRegionObserverStacking.TEST_UTIL.getConfiguration();
        HRegion region = initHRegion(TABLE, getClass().getName(), conf, FAMILIES);
        RegionCoprocessorHost h = region.getCoprocessorHost();
        h.load(TestRegionObserverStacking.ObserverA.class, PRIORITY_HIGHEST, conf);
        h.load(TestRegionObserverStacking.ObserverB.class, PRIORITY_USER, conf);
        h.load(TestRegionObserverStacking.ObserverC.class, PRIORITY_LOWEST, conf);
        Put put = new Put(ROW);
        put.addColumn(A, A, A);
        region.put(put);
        Coprocessor c = h.findCoprocessor(TestRegionObserverStacking.ObserverA.class.getName());
        long idA = ((TestRegionObserverStacking.ObserverA) (c)).id;
        c = h.findCoprocessor(TestRegionObserverStacking.ObserverB.class.getName());
        long idB = ((TestRegionObserverStacking.ObserverB) (c)).id;
        c = h.findCoprocessor(TestRegionObserverStacking.ObserverC.class.getName());
        long idC = ((TestRegionObserverStacking.ObserverC) (c)).id;
        TestCase.assertTrue((idA < idB));
        TestCase.assertTrue((idB < idC));
        HBaseTestingUtility.closeRegionAndWAL(region);
    }
}

