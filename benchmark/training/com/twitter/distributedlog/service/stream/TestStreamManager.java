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
package com.twitter.distributedlog.service.stream;


import com.twitter.distributedlog.DistributedLogConfiguration;
import com.twitter.distributedlog.config.DynamicDistributedLogConfiguration;
import com.twitter.distributedlog.namespace.DistributedLogNamespace;
import com.twitter.distributedlog.service.config.StreamConfigProvider;
import com.twitter.distributedlog.service.streamset.Partition;
import com.twitter.distributedlog.service.streamset.StreamPartitionConverter;
import com.twitter.util.Await;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Test Case for StreamManager.
 */
public class TestStreamManager {
    @Rule
    public TestName testName = new TestName();

    ScheduledExecutorService mockExecutorService = Mockito.mock(ScheduledExecutorService.class);

    @Test(timeout = 60000)
    public void testCollectionMethods() throws Exception {
        Stream mockStream = Mockito.mock(Stream.class);
        Mockito.when(mockStream.getStreamName()).thenReturn("stream1");
        Mockito.when(mockStream.getPartition()).thenReturn(new Partition("stream1", 0));
        StreamFactory mockStreamFactory = Mockito.mock(StreamFactory.class);
        StreamPartitionConverter mockPartitionConverter = Mockito.mock(StreamPartitionConverter.class);
        StreamConfigProvider mockStreamConfigProvider = Mockito.mock(StreamConfigProvider.class);
        Mockito.when(mockStreamFactory.create(((String) (ArgumentMatchers.any())), ((DynamicDistributedLogConfiguration) (ArgumentMatchers.any())), ((StreamManager) (ArgumentMatchers.any())))).thenReturn(mockStream);
        StreamManager streamManager = new StreamManagerImpl("", new DistributedLogConfiguration(), mockExecutorService, mockStreamFactory, mockPartitionConverter, mockStreamConfigProvider, Mockito.mock(DistributedLogNamespace.class));
        Assert.assertFalse(streamManager.isAcquired("stream1"));
        Assert.assertEquals(0, streamManager.numAcquired());
        Assert.assertEquals(0, streamManager.numCached());
        streamManager.notifyAcquired(mockStream);
        Assert.assertTrue(streamManager.isAcquired("stream1"));
        Assert.assertEquals(1, streamManager.numAcquired());
        Assert.assertEquals(0, streamManager.numCached());
        streamManager.notifyReleased(mockStream);
        Assert.assertFalse(streamManager.isAcquired("stream1"));
        Assert.assertEquals(0, streamManager.numAcquired());
        Assert.assertEquals(0, streamManager.numCached());
        streamManager.notifyAcquired(mockStream);
        Assert.assertTrue(streamManager.isAcquired("stream1"));
        Assert.assertEquals(1, streamManager.numAcquired());
        Assert.assertEquals(0, streamManager.numCached());
        streamManager.notifyAcquired(mockStream);
        Assert.assertTrue(streamManager.isAcquired("stream1"));
        Assert.assertEquals(1, streamManager.numAcquired());
        Assert.assertEquals(0, streamManager.numCached());
        streamManager.notifyReleased(mockStream);
        Assert.assertFalse(streamManager.isAcquired("stream1"));
        Assert.assertEquals(0, streamManager.numAcquired());
        Assert.assertEquals(0, streamManager.numCached());
        streamManager.notifyReleased(mockStream);
        Assert.assertFalse(streamManager.isAcquired("stream1"));
        Assert.assertEquals(0, streamManager.numAcquired());
        Assert.assertEquals(0, streamManager.numCached());
    }

    @Test
    public void testCreateStream() throws Exception {
        Stream mockStream = Mockito.mock(Stream.class);
        final String streamName = "stream1";
        Mockito.when(mockStream.getStreamName()).thenReturn(streamName);
        StreamFactory mockStreamFactory = Mockito.mock(StreamFactory.class);
        StreamPartitionConverter mockPartitionConverter = Mockito.mock(StreamPartitionConverter.class);
        StreamConfigProvider mockStreamConfigProvider = Mockito.mock(StreamConfigProvider.class);
        Mockito.when(mockStreamFactory.create(((String) (ArgumentMatchers.any())), ((DynamicDistributedLogConfiguration) (ArgumentMatchers.any())), ((StreamManager) (ArgumentMatchers.any())))).thenReturn(mockStream);
        DistributedLogNamespace dlNamespace = Mockito.mock(DistributedLogNamespace.class);
        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);
        StreamManager streamManager = new StreamManagerImpl("", new DistributedLogConfiguration(), executorService, mockStreamFactory, mockPartitionConverter, mockStreamConfigProvider, dlNamespace);
        Assert.assertTrue(Await.ready(streamManager.createStreamAsync(streamName)).isReturn());
        Mockito.verify(dlNamespace).createLog(streamName);
    }
}

