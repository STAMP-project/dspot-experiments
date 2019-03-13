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


import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessor;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.RegionObserver;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runners.Parameterized;

import static RegionReplicaUtil.DEFAULT_REPLICA_ID;


public abstract class AbstractTestAsyncTableRegionReplicasRead {
    protected static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    protected static TableName TABLE_NAME = TableName.valueOf("async");

    protected static byte[] FAMILY = Bytes.toBytes("cf");

    protected static byte[] QUALIFIER = Bytes.toBytes("cq");

    protected static byte[] ROW = Bytes.toBytes("row");

    protected static byte[] VALUE = Bytes.toBytes("value");

    protected static int REPLICA_COUNT = 3;

    protected static AsyncConnection ASYNC_CONN;

    @Rule
    public TestName testName = new TestName();

    @Parameterized.Parameter
    public Supplier<AsyncTable<?>> getTable;

    protected static volatile boolean FAIL_PRIMARY_GET = false;

    protected static ConcurrentMap<Integer, AtomicInteger> REPLICA_ID_TO_COUNT = new ConcurrentHashMap<>();

    public static final class FailPrimaryGetCP implements RegionCoprocessor , RegionObserver {
        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }

        private void recordAndTryFail(ObserverContext<RegionCoprocessorEnvironment> c) throws IOException {
            RegionInfo region = c.getEnvironment().getRegionInfo();
            if (!(region.getTable().equals(AbstractTestAsyncTableRegionReplicasRead.TABLE_NAME))) {
                return;
            }
            AbstractTestAsyncTableRegionReplicasRead.REPLICA_ID_TO_COUNT.computeIfAbsent(region.getReplicaId(), ( k) -> new AtomicInteger()).incrementAndGet();
            if (((region.getReplicaId()) == (DEFAULT_REPLICA_ID)) && (AbstractTestAsyncTableRegionReplicasRead.FAIL_PRIMARY_GET)) {
                throw new IOException("Inject error");
            }
        }

        @Override
        public void preGetOp(ObserverContext<RegionCoprocessorEnvironment> c, Get get, List<Cell> result) throws IOException {
            recordAndTryFail(c);
        }

        @Override
        public void preScannerOpen(ObserverContext<RegionCoprocessorEnvironment> c, Scan scan) throws IOException {
            recordAndTryFail(c);
        }
    }

    @Test
    public void testNoReplicaRead() throws Exception {
        AbstractTestAsyncTableRegionReplicasRead.FAIL_PRIMARY_GET = false;
        AbstractTestAsyncTableRegionReplicasRead.REPLICA_ID_TO_COUNT.clear();
        AsyncTable<?> table = getTable.get();
        readAndCheck(table, (-1));
        // the primary region is fine and the primary timeout is 1 second which is long enough, so we
        // should not send any requests to secondary replicas even if the consistency is timeline.
        Thread.sleep(5000);
        Assert.assertEquals(0, AbstractTestAsyncTableRegionReplicasRead.getSecondaryGetCount());
    }

    @Test
    public void testReplicaRead() throws Exception {
        // fail the primary get request
        AbstractTestAsyncTableRegionReplicasRead.FAIL_PRIMARY_GET = true;
        AbstractTestAsyncTableRegionReplicasRead.REPLICA_ID_TO_COUNT.clear();
        // make sure that we could still get the value from secondary replicas
        AsyncTable<?> table = getTable.get();
        readAndCheck(table, (-1));
        // make sure that the primary request has been canceled
        Thread.sleep(5000);
        int count = AbstractTestAsyncTableRegionReplicasRead.getPrimaryGetCount();
        Thread.sleep(10000);
        Assert.assertEquals(count, AbstractTestAsyncTableRegionReplicasRead.getPrimaryGetCount());
    }

    @Test
    public void testReadSpecificReplica() throws Exception {
        AbstractTestAsyncTableRegionReplicasRead.FAIL_PRIMARY_GET = false;
        AbstractTestAsyncTableRegionReplicasRead.REPLICA_ID_TO_COUNT.clear();
        AsyncTable<?> table = getTable.get();
        for (int replicaId = 0; replicaId < (AbstractTestAsyncTableRegionReplicasRead.REPLICA_COUNT); replicaId++) {
            readAndCheck(table, replicaId);
            Assert.assertEquals(1, AbstractTestAsyncTableRegionReplicasRead.REPLICA_ID_TO_COUNT.get(replicaId).get());
        }
    }
}

