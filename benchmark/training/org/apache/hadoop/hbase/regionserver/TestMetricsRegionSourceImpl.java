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
package org.apache.hadoop.hbase.regionserver;


import org.apache.hadoop.hbase.CompatibilitySingletonFactory;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.testclassification.MetricsTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ MetricsTests.class, SmallTests.class })
public class TestMetricsRegionSourceImpl {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMetricsRegionSourceImpl.class);

    @SuppressWarnings("SelfComparison")
    @Test
    public void testCompareToHashCodeEquals() {
        MetricsRegionServerSourceFactory fact = CompatibilitySingletonFactory.getInstance(MetricsRegionServerSourceFactory.class);
        MetricsRegionSource one = fact.createRegion(new TestMetricsRegionSourceImpl.RegionWrapperStub("TEST"));
        MetricsRegionSource oneClone = fact.createRegion(new TestMetricsRegionSourceImpl.RegionWrapperStub("TEST"));
        MetricsRegionSource two = fact.createRegion(new TestMetricsRegionSourceImpl.RegionWrapperStub("TWO"));
        Assert.assertEquals(0, one.compareTo(oneClone));
        Assert.assertEquals(one.hashCode(), oneClone.hashCode());
        Assert.assertNotEquals(one, two);
        Assert.assertNotEquals(0, one.compareTo(two));
        Assert.assertNotEquals(0, two.compareTo(one));
        Assert.assertNotEquals(one.compareTo(two), two.compareTo(one));
        Assert.assertEquals(0, two.compareTo(two));
    }

    @Test(expected = RuntimeException.class)
    public void testNoGetRegionServerMetricsSourceImpl() {
        // This should throw an exception because MetricsRegionSourceImpl should only
        // be created by a factory.
        CompatibilitySingletonFactory.getInstance(MetricsRegionSource.class);
    }

    static class RegionWrapperStub implements MetricsRegionWrapper {
        private String regionName;

        RegionWrapperStub(String regionName) {
            this.regionName = regionName;
        }

        @Override
        public String getTableName() {
            return null;
        }

        @Override
        public String getNamespace() {
            return null;
        }

        @Override
        public String getRegionName() {
            return this.regionName;
        }

        @Override
        public long getNumStores() {
            return 0;
        }

        @Override
        public long getNumStoreFiles() {
            return 0;
        }

        @Override
        public long getMemStoreSize() {
            return 0;
        }

        @Override
        public long getStoreFileSize() {
            return 0;
        }

        @Override
        public long getReadRequestCount() {
            return 0;
        }

        @Override
        public long getFilteredReadRequestCount() {
            return 0;
        }

        @Override
        public long getMaxStoreFileAge() {
            return 0;
        }

        @Override
        public long getMinStoreFileAge() {
            return 0;
        }

        @Override
        public long getAvgStoreFileAge() {
            return 0;
        }

        @Override
        public long getNumReferenceFiles() {
            return 0;
        }

        @Override
        public long getCpRequestCount() {
            return 0;
        }

        @Override
        public long getWriteRequestCount() {
            return 0;
        }

        @Override
        public long getNumFilesCompacted() {
            return 0;
        }

        @Override
        public long getNumBytesCompacted() {
            return 0;
        }

        @Override
        public long getLastMajorCompactionAge() {
            return 0;
        }

        @Override
        public long getNumCompactionsCompleted() {
            return 0;
        }

        @Override
        public long getNumCompactionsFailed() {
            return 0;
        }

        @Override
        public int getRegionHashCode() {
            return regionName.hashCode();
        }

        /**
         * Always return 0 for testing
         */
        @Override
        public int getReplicaId() {
            return 0;
        }

        @Override
        public long getNumCompactionsQueued() {
            return 0;
        }

        @Override
        public long getNumFlushesQueued() {
            return 0;
        }

        @Override
        public long getMaxCompactionQueueSize() {
            return 0;
        }

        @Override
        public long getMaxFlushQueueSize() {
            return 0;
        }

        @Override
        public long getTotalRequestCount() {
            return 0;
        }
    }
}

