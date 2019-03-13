/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.kafka.pubsub;


import java.nio.charset.StandardCharsets;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.KafkaException;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processors.kafka.pubsub.ConsumerPool.PoolStats;
import org.apache.nifi.provenance.ProvenanceReporter;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ConsumerPoolTest {
    private Consumer<byte[], byte[]> consumer = null;

    private ProcessSession mockSession = null;

    private ProcessContext mockContext = Mockito.mock(ProcessContext.class);

    private ProvenanceReporter mockReporter = null;

    private ConsumerPool testPool = null;

    private ConsumerPool testDemarcatedPool = null;

    private ComponentLog logger = null;

    @Test
    public void validatePoolSimpleCreateClose() throws Exception {
        Mockito.when(consumer.poll(ArgumentMatchers.anyLong())).thenReturn(ConsumerPoolTest.createConsumerRecords("nifi", 0, 0L, new byte[][]{  }));
        try (final ConsumerLease lease = testPool.obtainConsumer(mockSession, mockContext)) {
            lease.poll();
        }
        try (final ConsumerLease lease = testPool.obtainConsumer(mockSession, mockContext)) {
            lease.poll();
        }
        try (final ConsumerLease lease = testPool.obtainConsumer(mockSession, mockContext)) {
            lease.poll();
        }
        try (final ConsumerLease lease = testPool.obtainConsumer(mockSession, mockContext)) {
            lease.poll();
        }
        testPool.close();
        Mockito.verify(mockSession, Mockito.times(0)).create();
        Mockito.verify(mockSession, Mockito.times(0)).commit();
        final PoolStats stats = testPool.getPoolStats();
        Assert.assertEquals(1, stats.consumerCreatedCount);
        Assert.assertEquals(1, stats.consumerClosedCount);
        Assert.assertEquals(4, stats.leasesObtainedCount);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void validatePoolSimpleCreatePollClose() throws Exception {
        final byte[][] firstPassValues = new byte[][]{ "Hello-1".getBytes(StandardCharsets.UTF_8), "Hello-2".getBytes(StandardCharsets.UTF_8), "Hello-3".getBytes(StandardCharsets.UTF_8) };
        final ConsumerRecords<byte[], byte[]> firstRecs = ConsumerPoolTest.createConsumerRecords("foo", 1, 1L, firstPassValues);
        Mockito.when(consumer.poll(ArgumentMatchers.anyLong())).thenReturn(firstRecs, ConsumerPoolTest.createConsumerRecords("nifi", 0, 0L, new byte[][]{  }));
        try (final ConsumerLease lease = testPool.obtainConsumer(mockSession, mockContext)) {
            lease.poll();
            lease.commit();
        }
        testPool.close();
        Mockito.verify(mockSession, Mockito.times(3)).create();
        Mockito.verify(mockSession, Mockito.times(1)).commit();
        final PoolStats stats = testPool.getPoolStats();
        Assert.assertEquals(1, stats.consumerCreatedCount);
        Assert.assertEquals(1, stats.consumerClosedCount);
        Assert.assertEquals(1, stats.leasesObtainedCount);
    }

    @Test
    public void validatePoolSimpleBatchCreateClose() throws Exception {
        Mockito.when(consumer.poll(ArgumentMatchers.anyLong())).thenReturn(ConsumerPoolTest.createConsumerRecords("nifi", 0, 0L, new byte[][]{  }));
        for (int i = 0; i < 100; i++) {
            try (final ConsumerLease lease = testPool.obtainConsumer(mockSession, mockContext)) {
                for (int j = 0; j < 100; j++) {
                    lease.poll();
                }
            }
        }
        testPool.close();
        Mockito.verify(mockSession, Mockito.times(0)).create();
        Mockito.verify(mockSession, Mockito.times(0)).commit();
        final PoolStats stats = testPool.getPoolStats();
        Assert.assertEquals(1, stats.consumerCreatedCount);
        Assert.assertEquals(1, stats.consumerClosedCount);
        Assert.assertEquals(100, stats.leasesObtainedCount);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void validatePoolBatchCreatePollClose() throws Exception {
        final byte[][] firstPassValues = new byte[][]{ "Hello-1".getBytes(StandardCharsets.UTF_8), "Hello-2".getBytes(StandardCharsets.UTF_8), "Hello-3".getBytes(StandardCharsets.UTF_8) };
        final ConsumerRecords<byte[], byte[]> firstRecs = ConsumerPoolTest.createConsumerRecords("foo", 1, 1L, firstPassValues);
        Mockito.when(consumer.poll(ArgumentMatchers.anyLong())).thenReturn(firstRecs, ConsumerPoolTest.createConsumerRecords("nifi", 0, 0L, new byte[][]{  }));
        try (final ConsumerLease lease = testDemarcatedPool.obtainConsumer(mockSession, mockContext)) {
            lease.poll();
            lease.commit();
        }
        testDemarcatedPool.close();
        Mockito.verify(mockSession, Mockito.times(1)).create();
        Mockito.verify(mockSession, Mockito.times(1)).commit();
        final PoolStats stats = testDemarcatedPool.getPoolStats();
        Assert.assertEquals(1, stats.consumerCreatedCount);
        Assert.assertEquals(1, stats.consumerClosedCount);
        Assert.assertEquals(1, stats.leasesObtainedCount);
    }

    @Test
    public void validatePoolConsumerFails() throws Exception {
        Mockito.when(consumer.poll(ArgumentMatchers.anyLong())).thenThrow(new KafkaException("oops"));
        try (final ConsumerLease lease = testPool.obtainConsumer(mockSession, mockContext)) {
            try {
                lease.poll();
                Assert.fail();
            } catch (final KafkaException ke) {
            }
        }
        testPool.close();
        Mockito.verify(mockSession, Mockito.times(0)).create();
        Mockito.verify(mockSession, Mockito.times(0)).commit();
        final PoolStats stats = testPool.getPoolStats();
        Assert.assertEquals(1, stats.consumerCreatedCount);
        Assert.assertEquals(1, stats.consumerClosedCount);
        Assert.assertEquals(1, stats.leasesObtainedCount);
    }
}

