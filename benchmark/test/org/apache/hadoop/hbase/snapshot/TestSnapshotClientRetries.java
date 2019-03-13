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
package org.apache.hadoop.hbase.snapshot;


import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TestTableName;
import org.apache.hadoop.hbase.client.SnapshotDescription;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.coprocessor.MasterCoprocessor;
import org.apache.hadoop.hbase.coprocessor.MasterCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.MasterObserver;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MediumTests.class })
public class TestSnapshotClientRetries {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSnapshotClientRetries.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final Logger LOG = LoggerFactory.getLogger(TestSnapshotClientRetries.class);

    @Rule
    public TestTableName TEST_TABLE = new TestTableName();

    @Test(expected = SnapshotExistsException.class)
    public void testSnapshotAlreadyExist() throws Exception {
        final String snapshotName = "testSnapshotAlreadyExist";
        TestSnapshotClientRetries.TEST_UTIL.createTable(TEST_TABLE.getTableName(), "f");
        TestSnapshotClientRetries.TEST_UTIL.getAdmin().snapshot(snapshotName, TEST_TABLE.getTableName());
        snapshotAndAssertOneRetry(snapshotName, TEST_TABLE.getTableName());
    }

    @Test(expected = SnapshotDoesNotExistException.class)
    public void testCloneNonExistentSnapshot() throws Exception {
        final String snapshotName = "testCloneNonExistentSnapshot";
        cloneAndAssertOneRetry(snapshotName, TEST_TABLE.getTableName());
    }

    public static class MasterSyncObserver implements MasterCoprocessor , MasterObserver {
        volatile AtomicInteger snapshotCount = null;

        volatile AtomicInteger cloneCount = null;

        @Override
        public Optional<MasterObserver> getMasterObserver() {
            return Optional.of(this);
        }

        @Override
        public void preSnapshot(final ObserverContext<MasterCoprocessorEnvironment> ctx, final SnapshotDescription snapshot, final TableDescriptor hTableDescriptor) throws IOException {
            if ((snapshotCount) != null) {
                snapshotCount.incrementAndGet();
            }
        }

        @Override
        public void preCloneSnapshot(final ObserverContext<MasterCoprocessorEnvironment> ctx, final SnapshotDescription snapshot, final TableDescriptor hTableDescriptor) throws IOException {
            if ((cloneCount) != null) {
                cloneCount.incrementAndGet();
            }
        }
    }
}

