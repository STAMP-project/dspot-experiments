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
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Durability;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.testclassification.CoprocessorTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Threads;
import org.apache.hadoop.hbase.wal.WALEdit;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Test that a coprocessor can open a connection and write to another table, inside a hook.
 */
@Category({ CoprocessorTests.class, MediumTests.class })
public class TestOpenTableInCoprocessor {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestOpenTableInCoprocessor.class);

    private static final TableName otherTable = TableName.valueOf("otherTable");

    private static final TableName primaryTable = TableName.valueOf("primary");

    private static final byte[] family = new byte[]{ 'f' };

    private static boolean[] completed = new boolean[1];

    /**
     * Custom coprocessor that just copies the write to another table.
     */
    public static class SendToOtherTableCoprocessor implements RegionCoprocessor , RegionObserver {
        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }

        @Override
        public void prePut(final ObserverContext<RegionCoprocessorEnvironment> e, final Put put, final WALEdit edit, final Durability durability) throws IOException {
            try (Table table = e.getEnvironment().getConnection().getTable(TestOpenTableInCoprocessor.otherTable)) {
                table.put(put);
                TestOpenTableInCoprocessor.completed[0] = true;
            }
        }
    }

    private static boolean[] completedWithPool = new boolean[1];

    /**
     * Coprocessor that creates an HTable with a pool to write to another table
     */
    public static class CustomThreadPoolCoprocessor implements RegionCoprocessor , RegionObserver {
        /**
         * Get a pool that has only ever one thread. A second action added to the pool (running
         * concurrently), will cause an exception.
         *
         * @return 
         */
        private ExecutorService getPool() {
            int maxThreads = 1;
            long keepAliveTime = 60;
            ThreadPoolExecutor pool = new ThreadPoolExecutor(1, maxThreads, keepAliveTime, TimeUnit.SECONDS, new SynchronousQueue(), Threads.newDaemonThreadFactory("hbase-table"));
            pool.allowCoreThreadTimeOut(true);
            return pool;
        }

        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }

        @Override
        public void prePut(final ObserverContext<RegionCoprocessorEnvironment> e, final Put put, final WALEdit edit, final Durability durability) throws IOException {
            try (Table table = e.getEnvironment().getConnection().getTable(TestOpenTableInCoprocessor.otherTable, getPool())) {
                Put p = new Put(new byte[]{ 'a' });
                p.addColumn(TestOpenTableInCoprocessor.family, null, new byte[]{ 'a' });
                try {
                    table.batch(Collections.singletonList(put), null);
                } catch (InterruptedException e1) {
                    throw new IOException(e1);
                }
                TestOpenTableInCoprocessor.completedWithPool[0] = true;
            }
        }
    }

    private static HBaseTestingUtility UTIL = new HBaseTestingUtility();

    @Test
    public void testCoprocessorCanCreateConnectionToRemoteTable() throws Throwable {
        runCoprocessorConnectionToRemoteTable(TestOpenTableInCoprocessor.SendToOtherTableCoprocessor.class, TestOpenTableInCoprocessor.completed);
    }

    @Test
    public void testCoprocessorCanCreateConnectionToRemoteTableWithCustomPool() throws Throwable {
        runCoprocessorConnectionToRemoteTable(TestOpenTableInCoprocessor.CustomThreadPoolCoprocessor.class, TestOpenTableInCoprocessor.completedWithPool);
    }
}

