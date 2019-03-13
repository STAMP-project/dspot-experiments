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


import ClientProtos.Result.Builder;
import ClientProtos.ScanResponse;
import HConstants.CATALOG_FAMILY;
import HConstants.CLUSTER_ID_DEFAULT;
import HConstants.HBASE_CLIENT_META_OPERATION_TIMEOUT;
import HConstants.HBASE_CLIENT_RETRIES_NUMBER;
import HConstants.REGIONINFO_QUALIFIER;
import HConstants.SERVER_QUALIFIER;
import TableName.META_TABLE_NAME;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.hbase.CellComparatorImpl;
import org.apache.hadoop.hbase.DoNotRetryIOException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.MetaTableAccessor;
import org.apache.hadoop.hbase.RegionLocations;
import org.apache.hadoop.hbase.RegionTooBusyException;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.regionserver.RegionServerStoppedException;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.shaded.protobuf.generated.ClientProtos;
import org.apache.hadoop.hbase.shaded.protobuf.generated.ClientProtos.BulkLoadHFileRequest;
import org.apache.hadoop.hbase.shaded.protobuf.generated.ClientProtos.BulkLoadHFileResponse;
import org.apache.hadoop.hbase.shaded.protobuf.generated.ClientProtos.CleanupBulkLoadRequest;
import org.apache.hadoop.hbase.shaded.protobuf.generated.ClientProtos.CleanupBulkLoadResponse;
import org.apache.hadoop.hbase.shaded.protobuf.generated.ClientProtos.ClientService;
import org.apache.hadoop.hbase.shaded.protobuf.generated.ClientProtos.ClientService.BlockingInterface;
import org.apache.hadoop.hbase.shaded.protobuf.generated.ClientProtos.CoprocessorServiceRequest;
import org.apache.hadoop.hbase.shaded.protobuf.generated.ClientProtos.CoprocessorServiceResponse;
import org.apache.hadoop.hbase.shaded.protobuf.generated.ClientProtos.GetRequest;
import org.apache.hadoop.hbase.shaded.protobuf.generated.ClientProtos.GetResponse;
import org.apache.hadoop.hbase.shaded.protobuf.generated.ClientProtos.MultiRequest;
import org.apache.hadoop.hbase.shaded.protobuf.generated.ClientProtos.MultiResponse;
import org.apache.hadoop.hbase.shaded.protobuf.generated.ClientProtos.MutateRequest;
import org.apache.hadoop.hbase.shaded.protobuf.generated.ClientProtos.MutateResponse;
import org.apache.hadoop.hbase.shaded.protobuf.generated.ClientProtos.PrepareBulkLoadRequest;
import org.apache.hadoop.hbase.shaded.protobuf.generated.ClientProtos.PrepareBulkLoadResponse;
import org.apache.hadoop.hbase.shaded.protobuf.generated.ClientProtos.ScanRequest;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Pair;
import org.apache.hadoop.hbase.util.Threads;
import org.apache.hadoop.util.Tool;
import org.apache.hbase.thirdparty.com.google.protobuf.ByteString;
import org.apache.hbase.thirdparty.com.google.protobuf.RpcController;
import org.apache.hbase.thirdparty.com.google.protobuf.ServiceException;
import org.apache.hbase.thirdparty.com.google.protobuf.UnsafeByteOperations;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static RegionInfoBuilder.FIRST_META_REGIONINFO;


/**
 * Test client behavior w/o setting up a cluster.
 * Mock up cluster emissions.
 */
@Category({ ClientTests.class, SmallTests.class })
public class TestClientNoCluster extends Configured implements Tool {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestClientNoCluster.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestClientNoCluster.class);

    private Configuration conf;

    public static final ServerName META_SERVERNAME = ServerName.valueOf("meta.example.org", 16010, 12345);

    /**
     * Simple cluster registry inserted in place of our usual zookeeper based one.
     */
    static class SimpleRegistry extends DoNothingAsyncRegistry {
        final ServerName META_HOST = TestClientNoCluster.META_SERVERNAME;

        public SimpleRegistry(Configuration conf) {
            super(conf);
        }

        @Override
        public CompletableFuture<RegionLocations> getMetaRegionLocation() {
            return CompletableFuture.completedFuture(new RegionLocations(new org.apache.hadoop.hbase.HRegionLocation(FIRST_META_REGIONINFO, META_HOST)));
        }

        @Override
        public CompletableFuture<String> getClusterId() {
            return CompletableFuture.completedFuture(CLUSTER_ID_DEFAULT);
        }

        @Override
        public CompletableFuture<Integer> getCurrentNrHRS() {
            return CompletableFuture.completedFuture(1);
        }
    }

    /**
     * Test that operation timeout prevails over rpc default timeout and retries, etc.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testRpcTimeout() throws IOException {
        Configuration localConfig = HBaseConfiguration.create(this.conf);
        // This override mocks up our exists/get call to throw a RegionServerStoppedException.
        localConfig.set("hbase.client.connection.impl", TestClientNoCluster.RpcTimeoutConnection.class.getName());
        int pause = 10;
        localConfig.setInt("hbase.client.pause", pause);
        localConfig.setInt(HBASE_CLIENT_RETRIES_NUMBER, 10);
        // Set the operation timeout to be < the pause.  Expectation is that after first pause, we will
        // fail out of the rpc because the rpc timeout will have been set to the operation tiemout
        // and it has expired.  Otherwise, if this functionality is broke, all retries will be run --
        // all ten of them -- and we'll get the RetriesExhaustedException exception.
        localConfig.setInt(HBASE_CLIENT_META_OPERATION_TIMEOUT, (pause - 1));
        Connection connection = ConnectionFactory.createConnection(localConfig);
        Table table = connection.getTable(META_TABLE_NAME);
        Throwable t = null;
        try {
            // An exists call turns into a get w/ a flag.
            table.exists(new Get(Bytes.toBytes("abc")));
        } catch (SocketTimeoutException e) {
            // I expect this exception.
            TestClientNoCluster.LOG.info("Got expected exception", e);
            t = e;
        } catch (RetriesExhaustedException e) {
            // This is the old, unwanted behavior.  If we get here FAIL!!!
            Assert.fail();
        } finally {
            table.close();
            connection.close();
        }
        Assert.assertTrue((t != null));
    }

    @Test
    public void testDoNotRetryMetaTableAccessor() throws IOException {
        this.conf.set("hbase.client.connection.impl", TestClientNoCluster.RegionServerStoppedOnScannerOpenConnection.class.getName());
        try (Connection connection = ConnectionFactory.createConnection(conf)) {
            MetaTableAccessor.fullScanRegions(connection);
        }
    }

    @Test
    public void testDoNotRetryOnScanNext() throws IOException {
        this.conf.set("hbase.client.connection.impl", TestClientNoCluster.RegionServerStoppedOnScannerOpenConnection.class.getName());
        // Go against meta else we will try to find first region for the table on construction which
        // means we'll have to do a bunch more mocking.  Tests that go against meta only should be
        // good for a bit of testing.
        Connection connection = ConnectionFactory.createConnection(this.conf);
        Table table = connection.getTable(META_TABLE_NAME);
        ResultScanner scanner = table.getScanner(CATALOG_FAMILY);
        try {
            Result result = null;
            while ((result = scanner.next()) != null) {
                TestClientNoCluster.LOG.info(Objects.toString(result));
            } 
        } finally {
            scanner.close();
            table.close();
            connection.close();
        }
    }

    @Test
    public void testRegionServerStoppedOnScannerOpen() throws IOException {
        this.conf.set("hbase.client.connection.impl", TestClientNoCluster.RegionServerStoppedOnScannerOpenConnection.class.getName());
        // Go against meta else we will try to find first region for the table on construction which
        // means we'll have to do a bunch more mocking.  Tests that go against meta only should be
        // good for a bit of testing.
        Connection connection = ConnectionFactory.createConnection(conf);
        Table table = connection.getTable(META_TABLE_NAME);
        ResultScanner scanner = table.getScanner(CATALOG_FAMILY);
        try {
            Result result = null;
            while ((result = scanner.next()) != null) {
                TestClientNoCluster.LOG.info(Objects.toString(result));
            } 
        } finally {
            scanner.close();
            table.close();
            connection.close();
        }
    }

    @Test
    public void testConnectionClosedOnRegionLocate() throws IOException {
        Configuration testConf = new Configuration(this.conf);
        testConf.setInt(HBASE_CLIENT_RETRIES_NUMBER, 2);
        // Go against meta else we will try to find first region for the table on construction which
        // means we'll have to do a bunch more mocking. Tests that go against meta only should be
        // good for a bit of testing.
        Connection connection = ConnectionFactory.createConnection(testConf);
        Table table = connection.getTable(META_TABLE_NAME);
        connection.close();
        try {
            Get get = new Get(Bytes.toBytes("dummyRow"));
            table.get(get);
            Assert.fail("Should have thrown DoNotRetryException but no exception thrown");
        } catch (Exception e) {
            if (!(e instanceof DoNotRetryIOException)) {
                String errMsg = "Should have thrown DoNotRetryException but actually " + (e.getClass().getSimpleName());
                TestClientNoCluster.LOG.error(errMsg, e);
                Assert.fail(errMsg);
            }
        } finally {
            table.close();
        }
    }

    /**
     * Override to shutdown going to zookeeper for cluster id and meta location.
     */
    static class RegionServerStoppedOnScannerOpenConnection extends ConnectionImplementation {
        final BlockingInterface stub;

        RegionServerStoppedOnScannerOpenConnection(Configuration conf, ExecutorService pool, User user) throws IOException {
            super(conf, pool, user);
            // Mock up my stub so open scanner returns a scanner id and then on next, we throw
            // exceptions for three times and then after that, we return no more to scan.
            this.stub = Mockito.mock(BlockingInterface.class);
            long sid = 12345L;
            try {
                Mockito.when(stub.scan(((RpcController) (Mockito.any())), ((ClientProtos.ScanRequest) (Mockito.any())))).thenReturn(ScanResponse.newBuilder().setScannerId(sid).build()).thenThrow(new ServiceException(new RegionServerStoppedException("From Mockito"))).thenReturn(ScanResponse.newBuilder().setScannerId(sid).setMoreResults(false).build());
            } catch (ServiceException e) {
                throw new IOException(e);
            }
        }

        @Override
        public BlockingInterface getClient(ServerName sn) throws IOException {
            return this.stub;
        }
    }

    /**
     * Override to check we are setting rpc timeout right.
     */
    static class RpcTimeoutConnection extends ConnectionImplementation {
        final BlockingInterface stub;

        RpcTimeoutConnection(Configuration conf, ExecutorService pool, User user) throws IOException {
            super(conf, pool, user);
            // Mock up my stub so an exists call -- which turns into a get -- throws an exception
            this.stub = Mockito.mock(BlockingInterface.class);
            try {
                Mockito.when(stub.get(((RpcController) (Mockito.any())), ((ClientProtos.GetRequest) (Mockito.any())))).thenThrow(new ServiceException(new RegionServerStoppedException("From Mockito")));
            } catch (ServiceException e) {
                throw new IOException(e);
            }
        }

        @Override
        public BlockingInterface getClient(ServerName sn) throws IOException {
            return this.stub;
        }
    }

    /**
     * Fake many regionservers and many regions on a connection implementation.
     */
    static class ManyServersManyRegionsConnection extends ConnectionImplementation {
        // All access should be synchronized
        final Map<ServerName, ClientService.BlockingInterface> serversByClient;

        /**
         * Map of faked-up rows of a 'meta table'.
         */
        final SortedMap<byte[], Pair<HRegionInfo, ServerName>> meta;

        final AtomicLong sequenceids = new AtomicLong(0);

        private final Configuration conf;

        ManyServersManyRegionsConnection(Configuration conf, ExecutorService pool, User user) throws IOException {
            super(conf, pool, user);
            int serverCount = conf.getInt("hbase.test.servers", 10);
            this.serversByClient = new HashMap(serverCount);
            this.meta = TestClientNoCluster.makeMeta(Bytes.toBytes(conf.get("hbase.test.tablename", Bytes.toString(TestClientNoCluster.BIG_USER_TABLE))), conf.getInt("hbase.test.regions", 100), conf.getLong("hbase.test.namespace.span", 1000), serverCount);
            this.conf = conf;
        }

        @Override
        public BlockingInterface getClient(ServerName sn) throws IOException {
            // if (!sn.toString().startsWith("meta")) LOG.info(sn);
            ClientService.BlockingInterface stub = null;
            synchronized(this.serversByClient) {
                stub = this.serversByClient.get(sn);
                if (stub == null) {
                    stub = new TestClientNoCluster.FakeServer(this.conf, meta, sequenceids);
                    this.serversByClient.put(sn, stub);
                }
            }
            return stub;
        }
    }

    /**
     * Fake 'server'.
     * Implements the ClientService responding as though it were a 'server' (presumes a new
     * ClientService.BlockingInterface made per server).
     */
    static class FakeServer implements ClientService.BlockingInterface {
        private AtomicInteger multiInvocationsCount = new AtomicInteger(0);

        private final SortedMap<byte[], Pair<HRegionInfo, ServerName>> meta;

        private final AtomicLong sequenceids;

        private final long multiPause;

        private final int tooManyMultiRequests;

        FakeServer(final Configuration c, final SortedMap<byte[], Pair<HRegionInfo, ServerName>> meta, final AtomicLong sequenceids) {
            this.meta = meta;
            this.sequenceids = sequenceids;
            // Pause to simulate the server taking time applying the edits.  This will drive up the
            // number of threads used over in client.
            this.multiPause = c.getLong("hbase.test.multi.pause.when.done", 0);
            this.tooManyMultiRequests = c.getInt("hbase.test.multi.too.many", 3);
        }

        @Override
        public GetResponse get(RpcController controller, GetRequest request) throws ServiceException {
            boolean metaRegion = TestClientNoCluster.isMetaRegion(request.getRegion().getValue().toByteArray(), request.getRegion().getType());
            if (!metaRegion) {
                return doGetResponse(request);
            }
            return TestClientNoCluster.doMetaGetResponse(meta, request);
        }

        private GetResponse doGetResponse(GetRequest request) {
            ClientProtos.Result.Builder resultBuilder = ClientProtos.Result.newBuilder();
            ByteString row = request.getGet().getRow();
            resultBuilder.addCell(TestClientNoCluster.getStartCode(row));
            GetResponse.Builder builder = GetResponse.newBuilder();
            builder.setResult(resultBuilder.build());
            return builder.build();
        }

        @Override
        public MutateResponse mutate(RpcController controller, MutateRequest request) throws ServiceException {
            throw new NotImplementedException(HConstants.NOT_IMPLEMENTED);
        }

        @Override
        public org.apache.hadoop.hbase.shaded.protobuf.generated.ClientProtos.ScanResponse scan(RpcController controller, ScanRequest request) throws ServiceException {
            // Presume it is a scan of meta for now. Not all scans provide a region spec expecting
            // the server to keep reference by scannerid.  TODO.
            return TestClientNoCluster.doMetaScanResponse(meta, sequenceids, request);
        }

        @Override
        public BulkLoadHFileResponse bulkLoadHFile(RpcController controller, BulkLoadHFileRequest request) throws ServiceException {
            throw new NotImplementedException(HConstants.NOT_IMPLEMENTED);
        }

        @Override
        public CoprocessorServiceResponse execService(RpcController controller, CoprocessorServiceRequest request) throws ServiceException {
            throw new NotImplementedException(HConstants.NOT_IMPLEMENTED);
        }

        @Override
        public MultiResponse multi(RpcController controller, MultiRequest request) throws ServiceException {
            int concurrentInvocations = this.multiInvocationsCount.incrementAndGet();
            try {
                if (concurrentInvocations >= (tooManyMultiRequests)) {
                    throw new ServiceException(new RegionTooBusyException(("concurrentInvocations=" + concurrentInvocations)));
                }
                Threads.sleep(multiPause);
                return TestClientNoCluster.doMultiResponse(meta, sequenceids, request);
            } finally {
                this.multiInvocationsCount.decrementAndGet();
            }
        }

        @Override
        public CoprocessorServiceResponse execRegionServerService(RpcController controller, CoprocessorServiceRequest request) throws ServiceException {
            throw new NotImplementedException(HConstants.NOT_IMPLEMENTED);
        }

        @Override
        public PrepareBulkLoadResponse prepareBulkLoad(RpcController controller, PrepareBulkLoadRequest request) throws ServiceException {
            throw new NotImplementedException(HConstants.NOT_IMPLEMENTED);
        }

        @Override
        public CleanupBulkLoadResponse cleanupBulkLoad(RpcController controller, CleanupBulkLoadRequest request) throws ServiceException {
            throw new NotImplementedException(HConstants.NOT_IMPLEMENTED);
        }
    }

    private static final ByteString CATALOG_FAMILY_BYTESTRING = UnsafeByteOperations.unsafeWrap(CATALOG_FAMILY);

    private static final ByteString REGIONINFO_QUALIFIER_BYTESTRING = UnsafeByteOperations.unsafeWrap(REGIONINFO_QUALIFIER);

    private static final ByteString SERVER_QUALIFIER_BYTESTRING = UnsafeByteOperations.unsafeWrap(SERVER_QUALIFIER);

    private static final byte[] BIG_USER_TABLE = Bytes.toBytes("t");

    /**
     * Comparator for meta row keys.
     */
    private static class MetaRowsComparator implements Comparator<byte[]> {
        private final CellComparatorImpl delegate = CellComparatorImpl.META_COMPARATOR;

        @Override
        public int compare(byte[] left, byte[] right) {
            return delegate.compareRows(new KeyValue.KeyOnlyKeyValue(left), right, 0, right.length);
        }
    }
}

