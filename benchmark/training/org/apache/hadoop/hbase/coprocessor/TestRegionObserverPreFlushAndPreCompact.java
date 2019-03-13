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


import java.io.IOException;
import java.util.Optional;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.regionserver.FlushLifeCycleTracker;
import org.apache.hadoop.hbase.regionserver.InternalScanner;
import org.apache.hadoop.hbase.regionserver.RegionCoprocessorHost;
import org.apache.hadoop.hbase.regionserver.ScanType;
import org.apache.hadoop.hbase.regionserver.Store;
import org.apache.hadoop.hbase.regionserver.compactions.CompactionLifeCycleTracker;
import org.apache.hadoop.hbase.regionserver.compactions.CompactionRequest;
import org.apache.hadoop.hbase.testclassification.CoprocessorTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;


/**
 * Test that we fail if a Coprocessor tries to return a null scanner out
 * {@link RegionObserver#preFlush(ObserverContext, Store, InternalScanner, FlushLifeCycleTracker)}
 * or {@link RegionObserver#preCompact(ObserverContext, Store, InternalScanner, ScanType,
 * CompactionLifeCycleTracker, CompactionRequest)}
 *
 * @see <a href=https://issues.apache.org/jira/browse/HBASE-19122>HBASE-19122</a>
 */
@Category({ CoprocessorTests.class, SmallTests.class })
public class TestRegionObserverPreFlushAndPreCompact {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRegionObserverPreFlushAndPreCompact.class);

    @Rule
    public TestName name = new TestName();

    /**
     * Coprocessor that returns null when preCompact or preFlush is called.
     */
    public static class TestRegionObserver implements RegionCoprocessor , RegionObserver {
        @Override
        public InternalScanner preFlush(ObserverContext<RegionCoprocessorEnvironment> c, Store store, InternalScanner scanner, FlushLifeCycleTracker tracker) throws IOException {
            return null;
        }

        @Override
        public InternalScanner preCompact(ObserverContext<RegionCoprocessorEnvironment> c, Store store, InternalScanner scanner, ScanType scanType, CompactionLifeCycleTracker tracker, CompactionRequest request) throws IOException {
            return null;
        }

        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }
    }

    /**
     * Ensure we get expected exception when we try to return null from a preFlush call.
     *
     * @throws IOException
     * 		We expect it to throw {@link CoprocessorException}
     */
    @Test(expected = CoprocessorException.class)
    public void testPreFlushReturningNull() throws IOException {
        RegionCoprocessorHost rch = getRegionCoprocessorHost();
        rch.preFlush(null, null, null);
    }

    /**
     * Ensure we get expected exception when we try to return null from a preCompact call.
     *
     * @throws IOException
     * 		We expect it to throw {@link CoprocessorException}
     */
    @Test(expected = CoprocessorException.class)
    public void testPreCompactReturningNull() throws IOException {
        RegionCoprocessorHost rch = getRegionCoprocessorHost();
        rch.preCompact(null, null, null, null, null, null);
    }
}

