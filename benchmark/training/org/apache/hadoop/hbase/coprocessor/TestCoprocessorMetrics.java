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


import ClientProtos.MutationProto.MutationType.PUT;
import MultiRowMutationService.BlockingInterface;
import MutateRowsRequest.Builder;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CoprocessorEnvironment;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HRegionLocation;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.RegionLocator;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.ipc.CoprocessorRpcChannel;
import org.apache.hadoop.hbase.metrics.Counter;
import org.apache.hadoop.hbase.metrics.Metric;
import org.apache.hadoop.hbase.metrics.MetricRegistries;
import org.apache.hadoop.hbase.metrics.MetricRegistry;
import org.apache.hadoop.hbase.metrics.MetricRegistryInfo;
import org.apache.hadoop.hbase.metrics.Timer;
import org.apache.hadoop.hbase.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.protobuf.generated.MultiRowMutationProtos.MultiRowMutationService;
import org.apache.hadoop.hbase.protobuf.generated.MultiRowMutationProtos.MutateRowsRequest;
import org.apache.hadoop.hbase.protobuf.generated.MultiRowMutationProtos.MutateRowsResponse;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.testclassification.CoprocessorTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.wal.WALEdit;
import org.apache.hadoop.hbase.wal.WALKey;
import org.apache.hbase.thirdparty.com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Testing of coprocessor metrics end-to-end.
 */
@Category({ CoprocessorTests.class, MediumTests.class })
public class TestCoprocessorMetrics {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCoprocessorMetrics.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestCoprocessorMetrics.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static final byte[] foo = Bytes.toBytes("foo");

    private static final byte[] bar = Bytes.toBytes("bar");

    @Rule
    public TestName name = new TestName();

    /**
     * MasterObserver that has a Timer metric for create table operation.
     */
    public static class CustomMasterObserver implements MasterCoprocessor , MasterObserver {
        private Timer createTableTimer;

        private long start = Long.MIN_VALUE;

        @Override
        public void preCreateTable(ObserverContext<MasterCoprocessorEnvironment> ctx, TableDescriptor desc, RegionInfo[] regions) throws IOException {
            // we rely on the fact that there is only 1 instance of our MasterObserver
            this.start = System.currentTimeMillis();
        }

        @Override
        public void postCreateTable(ObserverContext<MasterCoprocessorEnvironment> ctx, TableDescriptor desc, RegionInfo[] regions) throws IOException {
            if ((this.start) > 0) {
                long time = (System.currentTimeMillis()) - (start);
                TestCoprocessorMetrics.LOG.info(("Create table took: " + time));
                createTableTimer.updateMillis(time);
            }
        }

        @Override
        public void start(CoprocessorEnvironment env) throws IOException {
            if (env instanceof MasterCoprocessorEnvironment) {
                MetricRegistry registry = getMetricRegistryForMaster();
                createTableTimer = registry.timer("CreateTable");
            }
        }

        @Override
        public Optional<MasterObserver> getMasterObserver() {
            return Optional.of(this);
        }
    }

    /**
     * RegionServerObserver that has a Counter for rollWAL requests.
     */
    public static class CustomRegionServerObserver implements RegionServerCoprocessor , RegionServerObserver {
        /**
         * This is the Counter metric object to keep track of the current count across invocations
         */
        private Counter rollWALCounter;

        @Override
        public Optional<RegionServerObserver> getRegionServerObserver() {
            return Optional.of(this);
        }

        @Override
        public void postRollWALWriterRequest(ObserverContext<RegionServerCoprocessorEnvironment> ctx) throws IOException {
            // Increment the Counter whenever the coprocessor is called
            rollWALCounter.increment();
        }

        @Override
        public void start(CoprocessorEnvironment env) throws IOException {
            if (env instanceof RegionServerCoprocessorEnvironment) {
                MetricRegistry registry = getMetricRegistryForRegionServer();
                if ((rollWALCounter) == null) {
                    rollWALCounter = registry.counter("rollWALRequests");
                }
            }
        }
    }

    /**
     * WALObserver that has a Counter for walEdits written.
     */
    public static class CustomWALObserver implements WALCoprocessor , WALObserver {
        private Counter walEditsCount;

        @Override
        public void postWALWrite(ObserverContext<? extends WALCoprocessorEnvironment> ctx, RegionInfo info, WALKey logKey, WALEdit logEdit) throws IOException {
            walEditsCount.increment();
        }

        @Override
        public void start(CoprocessorEnvironment env) throws IOException {
            if (env instanceof WALCoprocessorEnvironment) {
                MetricRegistry registry = getMetricRegistryForRegionServer();
                if ((walEditsCount) == null) {
                    walEditsCount = registry.counter("walEditsCount");
                }
            }
        }

        @Override
        public Optional<WALObserver> getWALObserver() {
            return Optional.of(this);
        }
    }

    /**
     * RegionObserver that has a Counter for preGet()
     */
    public static class CustomRegionObserver implements RegionCoprocessor , RegionObserver {
        private Counter preGetCounter;

        @Override
        public void preGetOp(ObserverContext<RegionCoprocessorEnvironment> e, Get get, List<Cell> results) throws IOException {
            preGetCounter.increment();
        }

        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }

        @Override
        public void start(CoprocessorEnvironment env) throws IOException {
            if (env instanceof RegionCoprocessorEnvironment) {
                MetricRegistry registry = getMetricRegistryForRegionServer();
                if ((preGetCounter) == null) {
                    preGetCounter = registry.counter("preGetRequests");
                }
            }
        }
    }

    public static class CustomRegionObserver2 extends TestCoprocessorMetrics.CustomRegionObserver {}

    /**
     * RegionEndpoint to test metrics from endpoint calls
     */
    public static class CustomRegionEndpoint extends MultiRowMutationEndpoint {
        private Timer endpointExecution;

        @Override
        public void mutateRows(RpcController controller, MutateRowsRequest request, RpcCallback<MutateRowsResponse> done) {
            long start = System.nanoTime();
            super.mutateRows(controller, request, done);
            endpointExecution.updateNanos(((System.nanoTime()) - start));
        }

        @Override
        public void start(CoprocessorEnvironment env) throws IOException {
            super.start(env);
            if (env instanceof RegionCoprocessorEnvironment) {
                MetricRegistry registry = getMetricRegistryForRegionServer();
                if ((endpointExecution) == null) {
                    endpointExecution = registry.timer("EndpointExecution");
                }
            }
        }
    }

    @Test
    public void testMasterObserver() throws IOException {
        // Find out the MetricRegistry used by the CP using the global registries
        MetricRegistryInfo info = MetricsCoprocessor.createRegistryInfoForMasterCoprocessor(TestCoprocessorMetrics.CustomMasterObserver.class.getName());
        Optional<MetricRegistry> registry = MetricRegistries.global().get(info);
        Assert.assertTrue(registry.isPresent());
        Optional<Metric> metric = registry.get().get("CreateTable");
        Assert.assertTrue(metric.isPresent());
        try (Connection connection = ConnectionFactory.createConnection(TestCoprocessorMetrics.UTIL.getConfiguration());Admin admin = connection.getAdmin()) {
            Timer createTableTimer = ((Timer) (metric.get()));
            long prevCount = createTableTimer.getHistogram().getCount();
            TestCoprocessorMetrics.LOG.info("Creating table");
            admin.createTable(new org.apache.hadoop.hbase.HTableDescriptor(TableName.valueOf(name.getMethodName())).addFamily(new HColumnDescriptor("foo")));
            Assert.assertEquals(1, ((createTableTimer.getHistogram().getCount()) - prevCount));
        }
    }

    @Test
    public void testRegionServerObserver() throws IOException {
        try (Connection connection = ConnectionFactory.createConnection(TestCoprocessorMetrics.UTIL.getConfiguration());Admin admin = connection.getAdmin()) {
            TestCoprocessorMetrics.LOG.info("Rolling WALs");
            admin.rollWALWriter(TestCoprocessorMetrics.UTIL.getMiniHBaseCluster().getServerHoldingMeta());
        }
        // Find out the MetricRegistry used by the CP using the global registries
        MetricRegistryInfo info = MetricsCoprocessor.createRegistryInfoForRSCoprocessor(TestCoprocessorMetrics.CustomRegionServerObserver.class.getName());
        Optional<MetricRegistry> registry = MetricRegistries.global().get(info);
        Assert.assertTrue(registry.isPresent());
        Optional<Metric> metric = registry.get().get("rollWALRequests");
        Assert.assertTrue(metric.isPresent());
        Counter rollWalRequests = ((Counter) (metric.get()));
        Assert.assertEquals(1, rollWalRequests.getCount());
    }

    @Test
    public void testWALObserver() throws IOException {
        // Find out the MetricRegistry used by the CP using the global registries
        MetricRegistryInfo info = MetricsCoprocessor.createRegistryInfoForWALCoprocessor(TestCoprocessorMetrics.CustomWALObserver.class.getName());
        Optional<MetricRegistry> registry = MetricRegistries.global().get(info);
        Assert.assertTrue(registry.isPresent());
        Optional<Metric> metric = registry.get().get("walEditsCount");
        Assert.assertTrue(metric.isPresent());
        try (Connection connection = ConnectionFactory.createConnection(TestCoprocessorMetrics.UTIL.getConfiguration());Admin admin = connection.getAdmin()) {
            admin.createTable(new org.apache.hadoop.hbase.HTableDescriptor(TableName.valueOf(name.getMethodName())).addFamily(new HColumnDescriptor("foo")));
            Counter rollWalRequests = ((Counter) (metric.get()));
            long prevCount = rollWalRequests.getCount();
            Assert.assertTrue((prevCount > 0));
            try (Table table = connection.getTable(TableName.valueOf(name.getMethodName()))) {
                table.put(new Put(TestCoprocessorMetrics.foo).addColumn(TestCoprocessorMetrics.foo, TestCoprocessorMetrics.foo, TestCoprocessorMetrics.foo));
            }
            Assert.assertEquals(1, ((rollWalRequests.getCount()) - prevCount));
        }
    }

    @Test
    public void testRegionObserverSingleRegion() throws IOException {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        try (Connection connection = ConnectionFactory.createConnection(TestCoprocessorMetrics.UTIL.getConfiguration());Admin admin = connection.getAdmin()) {
            admin.createTable(// add the coprocessor for the region
            new org.apache.hadoop.hbase.HTableDescriptor(tableName).addFamily(new HColumnDescriptor(TestCoprocessorMetrics.foo)).addCoprocessor(TestCoprocessorMetrics.CustomRegionObserver.class.getName()));
            try (Table table = connection.getTable(tableName)) {
                table.get(new Get(TestCoprocessorMetrics.foo));
                table.get(new Get(TestCoprocessorMetrics.foo));// 2 gets

            }
        }
        assertPreGetRequestsCounter(TestCoprocessorMetrics.CustomRegionObserver.class);
    }

    @Test
    public void testRegionObserverMultiRegion() throws IOException {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        try (Connection connection = ConnectionFactory.createConnection(TestCoprocessorMetrics.UTIL.getConfiguration());Admin admin = connection.getAdmin()) {
            admin.createTable(// add the coprocessor for the region
            new org.apache.hadoop.hbase.HTableDescriptor(tableName).addFamily(new HColumnDescriptor(TestCoprocessorMetrics.foo)).addCoprocessor(TestCoprocessorMetrics.CustomRegionObserver.class.getName()), new byte[][]{ TestCoprocessorMetrics.foo });// create with 2 regions

            try (Table table = connection.getTable(tableName);RegionLocator locator = connection.getRegionLocator(tableName)) {
                table.get(new Get(TestCoprocessorMetrics.bar));
                table.get(new Get(TestCoprocessorMetrics.foo));// 2 gets to 2 separate regions

                Assert.assertEquals(2, locator.getAllRegionLocations().size());
                Assert.assertNotEquals(locator.getRegionLocation(TestCoprocessorMetrics.bar).getRegionInfo(), locator.getRegionLocation(TestCoprocessorMetrics.foo).getRegionInfo());
            }
        }
        assertPreGetRequestsCounter(TestCoprocessorMetrics.CustomRegionObserver.class);
    }

    @Test
    public void testRegionObserverMultiTable() throws IOException {
        final TableName tableName1 = TableName.valueOf(((name.getMethodName()) + "1"));
        final TableName tableName2 = TableName.valueOf(((name.getMethodName()) + "2"));
        try (Connection connection = ConnectionFactory.createConnection(TestCoprocessorMetrics.UTIL.getConfiguration());Admin admin = connection.getAdmin()) {
            admin.createTable(// add the coprocessor for the region
            new org.apache.hadoop.hbase.HTableDescriptor(tableName1).addFamily(new HColumnDescriptor(TestCoprocessorMetrics.foo)).addCoprocessor(TestCoprocessorMetrics.CustomRegionObserver.class.getName()));
            admin.createTable(// add the coprocessor for the region
            new org.apache.hadoop.hbase.HTableDescriptor(tableName2).addFamily(new HColumnDescriptor(TestCoprocessorMetrics.foo)).addCoprocessor(TestCoprocessorMetrics.CustomRegionObserver.class.getName()));
            try (Table table1 = connection.getTable(tableName1);Table table2 = connection.getTable(tableName2)) {
                table1.get(new Get(TestCoprocessorMetrics.bar));
                table2.get(new Get(TestCoprocessorMetrics.foo));// 2 gets to 2 separate tables

            }
        }
        assertPreGetRequestsCounter(TestCoprocessorMetrics.CustomRegionObserver.class);
    }

    @Test
    public void testRegionObserverMultiCoprocessor() throws IOException {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        try (Connection connection = ConnectionFactory.createConnection(TestCoprocessorMetrics.UTIL.getConfiguration());Admin admin = connection.getAdmin()) {
            admin.createTable(// add the coprocessor for the region. We add two different coprocessors
            new org.apache.hadoop.hbase.HTableDescriptor(tableName).addFamily(new HColumnDescriptor(TestCoprocessorMetrics.foo)).addCoprocessor(TestCoprocessorMetrics.CustomRegionObserver.class.getName()).addCoprocessor(TestCoprocessorMetrics.CustomRegionObserver2.class.getName()));
            try (Table table = connection.getTable(tableName)) {
                table.get(new Get(TestCoprocessorMetrics.foo));
                table.get(new Get(TestCoprocessorMetrics.foo));// 2 gets

            }
        }
        // we will have two counters coming from two coprocs, in two different MetricRegistries
        assertPreGetRequestsCounter(TestCoprocessorMetrics.CustomRegionObserver.class);
        assertPreGetRequestsCounter(TestCoprocessorMetrics.CustomRegionObserver2.class);
    }

    @Test
    public void testRegionObserverAfterRegionClosed() throws IOException {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        try (Connection connection = ConnectionFactory.createConnection(TestCoprocessorMetrics.UTIL.getConfiguration());Admin admin = connection.getAdmin()) {
            admin.createTable(// add the coprocessor for the region
            new org.apache.hadoop.hbase.HTableDescriptor(tableName).addFamily(new HColumnDescriptor(TestCoprocessorMetrics.foo)).addCoprocessor(TestCoprocessorMetrics.CustomRegionObserver.class.getName()), new byte[][]{ TestCoprocessorMetrics.foo });// create with 2 regions

            try (Table table = connection.getTable(tableName)) {
                table.get(new Get(TestCoprocessorMetrics.foo));
                table.get(new Get(TestCoprocessorMetrics.foo));// 2 gets

            }
            assertPreGetRequestsCounter(TestCoprocessorMetrics.CustomRegionObserver.class);
            // close one of the regions
            try (RegionLocator locator = connection.getRegionLocator(tableName)) {
                HRegionLocation loc = locator.getRegionLocation(TestCoprocessorMetrics.foo);
                admin.unassign(loc.getRegionInfo().getEncodedNameAsBytes(), true);
                HRegionServer server = TestCoprocessorMetrics.UTIL.getMiniHBaseCluster().getRegionServer(loc.getServerName());
                waitFor(30000, () -> (server.getOnlineRegion(loc.getRegionInfo().getRegionName())) == null);
                Assert.assertNull(server.getOnlineRegion(loc.getRegionInfo().getRegionName()));
            }
            // with only 1 region remaining, we should still be able to find the Counter
            assertPreGetRequestsCounter(TestCoprocessorMetrics.CustomRegionObserver.class);
            // close the table
            admin.disableTable(tableName);
            MetricRegistryInfo info = MetricsCoprocessor.createRegistryInfoForRegionCoprocessor(TestCoprocessorMetrics.CustomRegionObserver.class.getName());
            // ensure that MetricRegistry is deleted
            Optional<MetricRegistry> registry = MetricRegistries.global().get(info);
            Assert.assertFalse(registry.isPresent());
        }
    }

    @Test
    public void testRegionObserverEndpoint() throws ServiceException, IOException {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        try (Connection connection = ConnectionFactory.createConnection(TestCoprocessorMetrics.UTIL.getConfiguration());Admin admin = connection.getAdmin()) {
            admin.createTable(// add the coprocessor for the region
            new org.apache.hadoop.hbase.HTableDescriptor(tableName).addFamily(new HColumnDescriptor(TestCoprocessorMetrics.foo)).addCoprocessor(TestCoprocessorMetrics.CustomRegionEndpoint.class.getName()));
            try (Table table = connection.getTable(tableName)) {
                List<Mutation> mutations = Lists.newArrayList(new Put(TestCoprocessorMetrics.foo), new Put(TestCoprocessorMetrics.bar));
                MutateRowsRequest.Builder mrmBuilder = MutateRowsRequest.newBuilder();
                for (Mutation mutation : mutations) {
                    mrmBuilder.addMutationRequest(ProtobufUtil.toMutation(PUT, mutation));
                }
                CoprocessorRpcChannel channel = table.coprocessorService(TestCoprocessorMetrics.bar);
                MultiRowMutationService.BlockingInterface service = MultiRowMutationService.newBlockingStub(channel);
                MutateRowsRequest mrm = mrmBuilder.build();
                service.mutateRows(null, mrm);
            }
        }
        // Find out the MetricRegistry used by the CP using the global registries
        MetricRegistryInfo info = MetricsCoprocessor.createRegistryInfoForRegionCoprocessor(TestCoprocessorMetrics.CustomRegionEndpoint.class.getName());
        Optional<MetricRegistry> registry = MetricRegistries.global().get(info);
        Assert.assertTrue(registry.isPresent());
        Optional<Metric> metric = registry.get().get("EndpointExecution");
        Assert.assertTrue(metric.isPresent());
        Timer endpointExecutions = ((Timer) (metric.get()));
        Assert.assertEquals(1, endpointExecutions.getHistogram().getCount());
    }
}

