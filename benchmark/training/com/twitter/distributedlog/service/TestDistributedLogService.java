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
package com.twitter.distributedlog.service;


import DLSN.InvalidDLSN;
import StatusCode.CHECKSUM_FAILED;
import StatusCode.FOUND;
import StatusCode.SERVICE_UNAVAILABLE;
import StatusCode.STREAM_UNAVAILABLE;
import StatusCode.SUCCESS;
import StreamStatus.BACKOFF;
import StreamStatus.CLOSED;
import StreamStatus.FAILED;
import StreamStatus.INITIALIZED;
import com.google.common.collect.Lists;
import com.twitter.distributedlog.DLSN;
import com.twitter.distributedlog.DistributedLogConfiguration;
import com.twitter.distributedlog.TestDistributedLogBase;
import com.twitter.distributedlog.exceptions.OwnershipAcquireFailedException;
import com.twitter.distributedlog.exceptions.StreamUnavailableException;
import com.twitter.distributedlog.service.config.ServerConfiguration;
import com.twitter.distributedlog.service.stream.Stream;
import com.twitter.distributedlog.service.stream.StreamImpl;
import com.twitter.distributedlog.service.stream.StreamImpl.StreamStatus;
import com.twitter.distributedlog.service.stream.StreamManagerImpl;
import com.twitter.distributedlog.service.stream.WriteOp;
import com.twitter.distributedlog.service.streamset.DelimiterStreamPartitionConverter;
import com.twitter.distributedlog.thrift.service.HeartbeatOptions;
import com.twitter.distributedlog.thrift.service.StatusCode;
import com.twitter.distributedlog.thrift.service.WriteContext;
import com.twitter.distributedlog.thrift.service.WriteResponse;
import com.twitter.distributedlog.util.FutureUtils;
import com.twitter.distributedlog.util.ProtocolUtils;
import com.twitter.util.Await;
import com.twitter.util.Future;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.bookkeeper.feature.SettableFeature;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test Case for DistributedLog Service
 */
public class TestDistributedLogService extends TestDistributedLogBase {
    static final Logger logger = LoggerFactory.getLogger(TestDistributedLogService.class);

    @Rule
    public TestName testName = new TestName();

    private ServerConfiguration serverConf;

    private DistributedLogConfiguration dlConf;

    private URI uri;

    private final CountDownLatch latch = new CountDownLatch(1);

    private DistributedLogServiceImpl service;

    @Test(timeout = 60000)
    public void testAcquireStreams() throws Exception {
        String streamName = testName.getMethodName();
        StreamImpl s0 = createUnstartedStream(service, streamName);
        s0.suspendAcquiring();
        DistributedLogServiceImpl service1 = createService(serverConf, dlConf);
        StreamImpl s1 = createUnstartedStream(service1, streamName);
        s1.suspendAcquiring();
        // create write ops
        WriteOp op0 = createWriteOp(service, streamName, 0L);
        s0.submit(op0);
        WriteOp op1 = createWriteOp(service1, streamName, 1L);
        s1.submit(op1);
        // check pending size
        Assert.assertEquals("Write Op 0 should be pending in service 0", 1, s0.numPendingOps());
        Assert.assertEquals("Write Op 1 should be pending in service 1", 1, s1.numPendingOps());
        // start acquiring s0
        s0.resumeAcquiring().start();
        WriteResponse wr0 = Await.result(op0.result());
        Assert.assertEquals("Op 0 should succeed", SUCCESS, wr0.getHeader().getCode());
        Assert.assertEquals("Service 0 should acquire stream", INITIALIZED, s0.getStatus());
        Assert.assertNotNull(s0.getManager());
        Assert.assertNotNull(s0.getWriter());
        Assert.assertNull(s0.getLastException());
        // start acquiring s1
        s1.resumeAcquiring().start();
        WriteResponse wr1 = Await.result(op1.result());
        Assert.assertEquals("Op 1 should fail", FOUND, wr1.getHeader().getCode());
        Assert.assertEquals("Service 1 should be in BACKOFF state", BACKOFF, s1.getStatus());
        Assert.assertNotNull(s1.getManager());
        Assert.assertNull(s1.getWriter());
        Assert.assertNotNull(s1.getLastException());
        Assert.assertTrue(((s1.getLastException()) instanceof OwnershipAcquireFailedException));
        service1.shutdown();
    }

    @Test(timeout = 60000)
    public void testAcquireStreamsWhenExceedMaxCachedPartitions() throws Exception {
        String streamName = (testName.getMethodName()) + "_0000";
        DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.addConfiguration(dlConf);
        confLocal.setMaxCachedPartitionsPerProxy(1);
        ServerConfiguration serverConfLocal = new ServerConfiguration();
        serverConfLocal.addConfiguration(serverConf);
        serverConfLocal.setStreamPartitionConverterClass(DelimiterStreamPartitionConverter.class);
        DistributedLogServiceImpl serviceLocal = createService(serverConfLocal, confLocal);
        Stream stream = serviceLocal.getLogWriter(streamName);
        // stream is cached
        Assert.assertNotNull(stream);
        Assert.assertEquals(1, serviceLocal.getStreamManager().numCached());
        // create write ops
        WriteOp op0 = createWriteOp(service, streamName, 0L);
        stream.submit(op0);
        WriteResponse wr0 = Await.result(op0.result());
        Assert.assertEquals("Op 0 should succeed", SUCCESS, wr0.getHeader().getCode());
        Assert.assertEquals(1, serviceLocal.getStreamManager().numAcquired());
        // should fail to acquire another partition
        try {
            serviceLocal.getLogWriter(((testName.getMethodName()) + "_0001"));
            Assert.fail("Should fail to acquire new streams");
        } catch (StreamUnavailableException sue) {
            // expected
        }
        Assert.assertEquals(1, serviceLocal.getStreamManager().numCached());
        Assert.assertEquals(1, serviceLocal.getStreamManager().numAcquired());
        // should be able to acquire partitions from other streams
        String anotherStreamName = (testName.getMethodName()) + "-another_0001";
        Stream anotherStream = serviceLocal.getLogWriter(anotherStreamName);
        Assert.assertNotNull(anotherStream);
        Assert.assertEquals(2, serviceLocal.getStreamManager().numCached());
        // create write ops
        WriteOp op1 = createWriteOp(service, anotherStreamName, 0L);
        anotherStream.submit(op1);
        WriteResponse wr1 = Await.result(op1.result());
        Assert.assertEquals("Op 1 should succeed", SUCCESS, wr1.getHeader().getCode());
        Assert.assertEquals(2, serviceLocal.getStreamManager().numAcquired());
    }

    @Test(timeout = 60000)
    public void testAcquireStreamsWhenExceedMaxAcquiredPartitions() throws Exception {
        String streamName = (testName.getMethodName()) + "_0000";
        DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.addConfiguration(dlConf);
        confLocal.setMaxCachedPartitionsPerProxy((-1));
        confLocal.setMaxAcquiredPartitionsPerProxy(1);
        ServerConfiguration serverConfLocal = new ServerConfiguration();
        serverConfLocal.addConfiguration(serverConf);
        serverConfLocal.setStreamPartitionConverterClass(DelimiterStreamPartitionConverter.class);
        DistributedLogServiceImpl serviceLocal = createService(serverConfLocal, confLocal);
        Stream stream = serviceLocal.getLogWriter(streamName);
        // stream is cached
        Assert.assertNotNull(stream);
        Assert.assertEquals(1, serviceLocal.getStreamManager().numCached());
        // create write ops
        WriteOp op0 = createWriteOp(service, streamName, 0L);
        stream.submit(op0);
        WriteResponse wr0 = Await.result(op0.result());
        Assert.assertEquals("Op 0 should succeed", SUCCESS, wr0.getHeader().getCode());
        Assert.assertEquals(1, serviceLocal.getStreamManager().numAcquired());
        // should be able to cache partitions from same stream
        String anotherStreamName = (testName.getMethodName()) + "_0001";
        Stream anotherStream = serviceLocal.getLogWriter(anotherStreamName);
        Assert.assertNotNull(anotherStream);
        Assert.assertEquals(2, serviceLocal.getStreamManager().numCached());
        // create write ops
        WriteOp op1 = createWriteOp(service, anotherStreamName, 0L);
        anotherStream.submit(op1);
        WriteResponse wr1 = Await.result(op1.result());
        Assert.assertEquals("Op 1 should fail", STREAM_UNAVAILABLE, wr1.getHeader().getCode());
        Assert.assertEquals(1, serviceLocal.getStreamManager().numAcquired());
    }

    @Test(timeout = 60000)
    public void testCloseShouldErrorOutPendingOps() throws Exception {
        String streamName = testName.getMethodName();
        StreamImpl s = createUnstartedStream(service, streamName);
        int numWrites = 10;
        List<Future<WriteResponse>> futureList = new ArrayList<Future<WriteResponse>>(numWrites);
        for (int i = 0; i < numWrites; i++) {
            WriteOp op = createWriteOp(service, streamName, i);
            s.submit(op);
            futureList.add(op.result());
        }
        Assert.assertEquals(numWrites, s.numPendingOps());
        Await.result(s.requestClose("close stream"));
        Assert.assertEquals(((("Stream " + streamName) + " is set to ") + (StreamStatus.CLOSED)), CLOSED, s.getStatus());
        for (int i = 0; i < numWrites; i++) {
            Future<WriteResponse> future = futureList.get(i);
            WriteResponse wr = Await.result(future);
            Assert.assertEquals(("Pending op should fail with " + (StatusCode.STREAM_UNAVAILABLE)), STREAM_UNAVAILABLE, wr.getHeader().getCode());
        }
    }

    @Test(timeout = 60000)
    public void testCloseTwice() throws Exception {
        String streamName = testName.getMethodName();
        StreamImpl s = createUnstartedStream(service, streamName);
        int numWrites = 10;
        List<Future<WriteResponse>> futureList = new ArrayList<Future<WriteResponse>>(numWrites);
        for (int i = 0; i < numWrites; i++) {
            WriteOp op = createWriteOp(service, streamName, i);
            s.submit(op);
            futureList.add(op.result());
        }
        Assert.assertEquals(numWrites, s.numPendingOps());
        Future<Void> closeFuture0 = s.requestClose("close 0");
        Assert.assertTrue(((("Stream " + streamName) + " should be set to ") + (StreamStatus.CLOSING)), (((StreamStatus.CLOSING) == (s.getStatus())) || ((StreamStatus.CLOSED) == (s.getStatus()))));
        Future<Void> closeFuture1 = s.requestClose("close 1");
        Assert.assertTrue(((("Stream " + streamName) + " should be set to ") + (StreamStatus.CLOSING)), (((StreamStatus.CLOSING) == (s.getStatus())) || ((StreamStatus.CLOSED) == (s.getStatus()))));
        Await.result(closeFuture0);
        Assert.assertEquals(((("Stream " + streamName) + " should be set to ") + (StreamStatus.CLOSED)), CLOSED, s.getStatus());
        Await.result(closeFuture1);
        Assert.assertEquals(((("Stream " + streamName) + " should be set to ") + (StreamStatus.CLOSED)), CLOSED, s.getStatus());
        for (int i = 0; i < numWrites; i++) {
            Future<WriteResponse> future = futureList.get(i);
            WriteResponse wr = Await.result(future);
            Assert.assertEquals(("Pending op should fail with " + (StatusCode.STREAM_UNAVAILABLE)), STREAM_UNAVAILABLE, wr.getHeader().getCode());
        }
    }

    @Test(timeout = 60000)
    public void testFailRequestsDuringClosing() throws Exception {
        String streamName = testName.getMethodName();
        StreamImpl s = createUnstartedStream(service, streamName);
        Future<Void> closeFuture = s.requestClose("close");
        Assert.assertTrue(((("Stream " + streamName) + " should be set to ") + (StreamStatus.CLOSING)), (((StreamStatus.CLOSING) == (s.getStatus())) || ((StreamStatus.CLOSED) == (s.getStatus()))));
        WriteOp op1 = createWriteOp(service, streamName, 0L);
        s.submit(op1);
        WriteResponse response1 = Await.result(op1.result());
        Assert.assertEquals((("Op should fail with " + (StatusCode.STREAM_UNAVAILABLE)) + " if it is closing"), STREAM_UNAVAILABLE, response1.getHeader().getCode());
        Await.result(closeFuture);
        Assert.assertEquals(((("Stream " + streamName) + " should be set to ") + (StreamStatus.CLOSED)), CLOSED, s.getStatus());
        WriteOp op2 = createWriteOp(service, streamName, 1L);
        s.submit(op2);
        WriteResponse response2 = Await.result(op2.result());
        Assert.assertEquals((("Op should fail with " + (StatusCode.STREAM_UNAVAILABLE)) + " if it is closed"), STREAM_UNAVAILABLE, response2.getHeader().getCode());
    }

    @Test(timeout = 60000)
    public void testServiceTimeout() throws Exception {
        DistributedLogConfiguration confLocal = newLocalConf();
        confLocal.setOutputBufferSize(Integer.MAX_VALUE).setImmediateFlushEnabled(false).setPeriodicFlushFrequencyMilliSeconds(0);
        ServerConfiguration serverConfLocal = newLocalServerConf();
        serverConfLocal.addConfiguration(serverConf);
        serverConfLocal.setServiceTimeoutMs(200).setStreamProbationTimeoutMs(100);
        String streamName = testName.getMethodName();
        // create a new service with 200ms timeout
        DistributedLogServiceImpl localService = createService(serverConfLocal, confLocal);
        StreamManagerImpl streamManager = ((StreamManagerImpl) (localService.getStreamManager()));
        int numWrites = 10;
        List<Future<WriteResponse>> futureList = new ArrayList<Future<WriteResponse>>(numWrites);
        for (int i = 0; i < numWrites; i++) {
            futureList.add(localService.write(streamName, createRecord(i)));
        }
        Assert.assertTrue((("Stream " + streamName) + " should be cached"), streamManager.getCachedStreams().containsKey(streamName));
        StreamImpl s = ((StreamImpl) (streamManager.getCachedStreams().get(streamName)));
        // the stream should be set CLOSING
        while (((StreamStatus.CLOSING) != (s.getStatus())) && ((StreamStatus.CLOSED) != (s.getStatus()))) {
            TimeUnit.MILLISECONDS.sleep(20);
        } 
        Assert.assertNotNull("Writer should be initialized", s.getWriter());
        Assert.assertNull("No exception should be thrown", s.getLastException());
        Future<Void> closeFuture = s.getCloseFuture();
        Await.result(closeFuture);
        for (int i = 0; i < numWrites; i++) {
            Assert.assertTrue("Write should not fail before closing", futureList.get(i).isDefined());
            WriteResponse response = Await.result(futureList.get(i));
            Assert.assertTrue(("Op should fail with " + (StatusCode.WRITE_CANCELLED_EXCEPTION)), ((((StatusCode.BK_TRANSMIT_ERROR) == (response.getHeader().getCode())) || ((StatusCode.WRITE_EXCEPTION) == (response.getHeader().getCode()))) || ((StatusCode.WRITE_CANCELLED_EXCEPTION) == (response.getHeader().getCode()))));
        }
        while (streamManager.getCachedStreams().containsKey(streamName)) {
            TimeUnit.MILLISECONDS.sleep(20);
        } 
        Assert.assertFalse("Stream should be removed from cache", streamManager.getCachedStreams().containsKey(streamName));
        Assert.assertFalse("Stream should be removed from acquired cache", streamManager.getAcquiredStreams().containsKey(streamName));
        localService.shutdown();
    }

    @Test(timeout = 60000)
    public void testNonDurableWrite() throws Exception {
        DistributedLogConfiguration confLocal = newLocalConf();
        confLocal.setOutputBufferSize(Integer.MAX_VALUE).setImmediateFlushEnabled(false).setPeriodicFlushFrequencyMilliSeconds(0).setDurableWriteEnabled(false);
        ServerConfiguration serverConfLocal = new ServerConfiguration();
        serverConfLocal.addConfiguration(serverConf);
        serverConfLocal.enableDurableWrite(false);
        serverConfLocal.setServiceTimeoutMs(Integer.MAX_VALUE).setStreamProbationTimeoutMs(Integer.MAX_VALUE);
        String streamName = testName.getMethodName();
        DistributedLogServiceImpl localService = createService(serverConfLocal, confLocal);
        StreamManagerImpl streamManager = ((StreamManagerImpl) (localService.getStreamManager()));
        int numWrites = 10;
        List<Future<WriteResponse>> futureList = new ArrayList<Future<WriteResponse>>();
        for (int i = 0; i < numWrites; i++) {
            futureList.add(localService.write(streamName, createRecord(i)));
        }
        Assert.assertTrue((("Stream " + streamName) + " should be cached"), streamManager.getCachedStreams().containsKey(streamName));
        List<WriteResponse> resultList = FutureUtils.result(Future.collect(futureList));
        for (WriteResponse wr : resultList) {
            Assert.assertEquals(InvalidDLSN, DLSN.deserialize(wr.getDlsn()));
        }
        localService.shutdown();
    }

    @Test(timeout = 60000)
    public void testWriteOpNoChecksum() throws Exception {
        DistributedLogServiceImpl localService = createConfiguredLocalService();
        WriteContext ctx = new WriteContext();
        Future<WriteResponse> result = localService.writeWithContext("test", getTestDataBuffer(), ctx);
        WriteResponse resp = Await.result(result);
        Assert.assertEquals(SUCCESS, resp.getHeader().getCode());
        localService.shutdown();
    }

    @Test(timeout = 60000)
    public void testTruncateOpNoChecksum() throws Exception {
        DistributedLogServiceImpl localService = createConfiguredLocalService();
        WriteContext ctx = new WriteContext();
        Future<WriteResponse> result = localService.truncate("test", new DLSN(1, 2, 3).serialize(), ctx);
        WriteResponse resp = Await.result(result);
        Assert.assertEquals(SUCCESS, resp.getHeader().getCode());
        localService.shutdown();
    }

    @Test(timeout = 60000)
    public void testStreamOpNoChecksum() throws Exception {
        DistributedLogServiceImpl localService = createConfiguredLocalService();
        WriteContext ctx = new WriteContext();
        Future<WriteResponse> result = localService.release("test", ctx);
        WriteResponse resp = Await.result(result);
        Assert.assertEquals(SUCCESS, resp.getHeader().getCode());
        result = localService.delete("test", ctx);
        resp = Await.result(result);
        Assert.assertEquals(SUCCESS, resp.getHeader().getCode());
        result = localService.heartbeat("test", ctx);
        resp = Await.result(result);
        Assert.assertEquals(SUCCESS, resp.getHeader().getCode());
        localService.shutdown();
    }

    @Test(timeout = 60000)
    public void testWriteOpChecksumBadChecksum() throws Exception {
        DistributedLogServiceImpl localService = createConfiguredLocalService();
        WriteContext ctx = new WriteContext().setCrc32(999);
        Future<WriteResponse> result = localService.writeWithContext("test", getTestDataBuffer(), ctx);
        WriteResponse resp = Await.result(result);
        Assert.assertEquals(CHECKSUM_FAILED, resp.getHeader().getCode());
        localService.shutdown();
    }

    @Test(timeout = 60000)
    public void testWriteOpChecksumBadStream() throws Exception {
        DistributedLogServiceImpl localService = createConfiguredLocalService();
        WriteContext ctx = new WriteContext().setCrc32(ProtocolUtils.writeOpCRC32("test", getTestDataBuffer().array()));
        Future<WriteResponse> result = localService.writeWithContext("test1", getTestDataBuffer(), ctx);
        WriteResponse resp = Await.result(result);
        Assert.assertEquals(CHECKSUM_FAILED, resp.getHeader().getCode());
        localService.shutdown();
    }

    @Test(timeout = 60000)
    public void testWriteOpChecksumBadData() throws Exception {
        DistributedLogServiceImpl localService = createConfiguredLocalService();
        ByteBuffer buffer = getTestDataBuffer();
        WriteContext ctx = new WriteContext().setCrc32(ProtocolUtils.writeOpCRC32("test", buffer.array()));
        // Overwrite 1 byte to corrupt data.
        buffer.put(1, ((byte) (171)));
        Future<WriteResponse> result = localService.writeWithContext("test", buffer, ctx);
        WriteResponse resp = Await.result(result);
        Assert.assertEquals(CHECKSUM_FAILED, resp.getHeader().getCode());
        localService.shutdown();
    }

    @Test(timeout = 60000)
    public void testStreamOpChecksumBadChecksum() throws Exception {
        DistributedLogServiceImpl localService = createConfiguredLocalService();
        WriteContext ctx = new WriteContext().setCrc32(999);
        Future<WriteResponse> result = localService.heartbeat("test", ctx);
        WriteResponse resp = Await.result(result);
        Assert.assertEquals(CHECKSUM_FAILED, resp.getHeader().getCode());
        result = localService.release("test", ctx);
        resp = Await.result(result);
        Assert.assertEquals(CHECKSUM_FAILED, resp.getHeader().getCode());
        result = localService.delete("test", ctx);
        resp = Await.result(result);
        Assert.assertEquals(CHECKSUM_FAILED, resp.getHeader().getCode());
        localService.shutdown();
    }

    @Test(timeout = 60000)
    public void testTruncateOpChecksumBadChecksum() throws Exception {
        DistributedLogServiceImpl localService = createConfiguredLocalService();
        WriteContext ctx = new WriteContext().setCrc32(999);
        Future<WriteResponse> result = localService.truncate("test", new DLSN(1, 2, 3).serialize(), ctx);
        WriteResponse resp = Await.result(result);
        Assert.assertEquals(CHECKSUM_FAILED, resp.getHeader().getCode());
        localService.shutdown();
    }

    @Test(timeout = 60000)
    public void testStreamOpBadChecksumWithChecksumDisabled() throws Exception {
        String streamName = testName.getMethodName();
        SettableFeature disabledFeature = new SettableFeature("", 0);
        WriteOp writeOp0 = getWriteOp(streamName, disabledFeature, 919191L);
        WriteOp writeOp1 = getWriteOp(streamName, disabledFeature, 919191L);
        try {
            writeOp0.preExecute();
            Assert.fail("should have thrown");
        } catch (Exception ex) {
        }
        disabledFeature.set(1);
        writeOp1.preExecute();
    }

    @Test(timeout = 60000)
    public void testStreamOpGoodChecksumWithChecksumDisabled() throws Exception {
        String streamName = testName.getMethodName();
        SettableFeature disabledFeature = new SettableFeature("", 1);
        WriteOp writeOp0 = getWriteOp(streamName, disabledFeature, ProtocolUtils.writeOpCRC32(streamName, "test".getBytes()));
        WriteOp writeOp1 = getWriteOp(streamName, disabledFeature, ProtocolUtils.writeOpCRC32(streamName, "test".getBytes()));
        writeOp0.preExecute();
        disabledFeature.set(0);
        writeOp1.preExecute();
    }

    @Test(timeout = 60000)
    public void testCloseStreamsShouldFlush() throws Exception {
        DistributedLogConfiguration confLocal = newLocalConf();
        confLocal.setOutputBufferSize(Integer.MAX_VALUE).setImmediateFlushEnabled(false).setPeriodicFlushFrequencyMilliSeconds(0);
        String streamNamePrefix = testName.getMethodName();
        DistributedLogServiceImpl localService = createService(serverConf, confLocal);
        StreamManagerImpl streamManager = ((StreamManagerImpl) (localService.getStreamManager()));
        int numStreams = 10;
        int numWrites = 10;
        List<Future<WriteResponse>> futureList = Lists.newArrayListWithExpectedSize((numStreams * numWrites));
        for (int i = 0; i < numStreams; i++) {
            String streamName = (streamNamePrefix + "-") + i;
            HeartbeatOptions hbOptions = new HeartbeatOptions();
            hbOptions.setSendHeartBeatToReader(true);
            // make sure the first log segment of each stream created
            FutureUtils.result(localService.heartbeatWithOptions(streamName, new WriteContext(), hbOptions));
            for (int j = 0; j < numWrites; j++) {
                futureList.add(localService.write(streamName, createRecord(((i * numWrites) + j))));
            }
        }
        Assert.assertEquals((("There should be " + numStreams) + " streams in cache"), numStreams, streamManager.getCachedStreams().size());
        while ((streamManager.getAcquiredStreams().size()) < numStreams) {
            TimeUnit.MILLISECONDS.sleep(20);
        } 
        Future<List<Void>> closeResult = localService.closeStreams();
        List<Void> closedStreams = Await.result(closeResult);
        Assert.assertEquals((("There should be " + numStreams) + " streams closed"), numStreams, closedStreams.size());
        // all writes should be flushed
        for (Future<WriteResponse> future : futureList) {
            WriteResponse response = Await.result(future);
            Assert.assertTrue(("Op should succeed or be rejected : " + (response.getHeader().getCode())), ((((StatusCode.SUCCESS) == (response.getHeader().getCode())) || ((StatusCode.WRITE_EXCEPTION) == (response.getHeader().getCode()))) || ((StatusCode.STREAM_UNAVAILABLE) == (response.getHeader().getCode()))));
        }
        Assert.assertTrue("There should be no streams in the cache", streamManager.getCachedStreams().isEmpty());
        Assert.assertTrue("There should be no streams in the acquired cache", streamManager.getAcquiredStreams().isEmpty());
        localService.shutdown();
    }

    @Test(timeout = 60000)
    public void testCloseStreamsShouldAbort() throws Exception {
        DistributedLogConfiguration confLocal = newLocalConf();
        confLocal.setOutputBufferSize(Integer.MAX_VALUE).setImmediateFlushEnabled(false).setPeriodicFlushFrequencyMilliSeconds(0);
        String streamNamePrefix = testName.getMethodName();
        DistributedLogServiceImpl localService = createService(serverConf, confLocal);
        StreamManagerImpl streamManager = ((StreamManagerImpl) (localService.getStreamManager()));
        int numStreams = 10;
        int numWrites = 10;
        List<Future<WriteResponse>> futureList = Lists.newArrayListWithExpectedSize((numStreams * numWrites));
        for (int i = 0; i < numStreams; i++) {
            String streamName = (streamNamePrefix + "-") + i;
            HeartbeatOptions hbOptions = new HeartbeatOptions();
            hbOptions.setSendHeartBeatToReader(true);
            // make sure the first log segment of each stream created
            FutureUtils.result(localService.heartbeatWithOptions(streamName, new WriteContext(), hbOptions));
            for (int j = 0; j < numWrites; j++) {
                futureList.add(localService.write(streamName, createRecord(((i * numWrites) + j))));
            }
        }
        Assert.assertEquals((("There should be " + numStreams) + " streams in cache"), numStreams, streamManager.getCachedStreams().size());
        while ((streamManager.getAcquiredStreams().size()) < numStreams) {
            TimeUnit.MILLISECONDS.sleep(20);
        } 
        for (Stream s : streamManager.getAcquiredStreams().values()) {
            StreamImpl stream = ((StreamImpl) (s));
            stream.setStatus(FAILED);
        }
        Future<List<Void>> closeResult = localService.closeStreams();
        List<Void> closedStreams = Await.result(closeResult);
        Assert.assertEquals((("There should be " + numStreams) + " streams closed"), numStreams, closedStreams.size());
        // all writes should be flushed
        for (Future<WriteResponse> future : futureList) {
            WriteResponse response = Await.result(future);
            Assert.assertTrue(((("Op should fail with " + (StatusCode.BK_TRANSMIT_ERROR)) + " or be rejected : ") + (response.getHeader().getCode())), ((((StatusCode.BK_TRANSMIT_ERROR) == (response.getHeader().getCode())) || ((StatusCode.WRITE_EXCEPTION) == (response.getHeader().getCode()))) || ((StatusCode.WRITE_CANCELLED_EXCEPTION) == (response.getHeader().getCode()))));
        }
        // acquired streams should all been removed after we close them
        Assert.assertTrue("There should be no streams in the acquired cache", streamManager.getAcquiredStreams().isEmpty());
        localService.shutdown();
        // cached streams wouldn't be removed immediately after streams are closed
        // but they should be removed after we shutdown the service
        Assert.assertTrue("There should be no streams in the cache after shutting down the service", streamManager.getCachedStreams().isEmpty());
    }

    @Test(timeout = 60000)
    public void testShutdown() throws Exception {
        service.shutdown();
        StreamManagerImpl streamManager = ((StreamManagerImpl) (service.getStreamManager()));
        WriteResponse response = Await.result(service.write(testName.getMethodName(), createRecord(0L)));
        Assert.assertEquals(("Write should fail with " + (StatusCode.SERVICE_UNAVAILABLE)), SERVICE_UNAVAILABLE, response.getHeader().getCode());
        Assert.assertTrue("There should be no streams created after shutdown", streamManager.getCachedStreams().isEmpty());
        Assert.assertTrue("There should be no streams acquired after shutdown", streamManager.getAcquiredStreams().isEmpty());
    }
}

