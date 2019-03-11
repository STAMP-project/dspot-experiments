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
package org.apache.beam.sdk.io.kinesis;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.beam.vendor.guava.v20_0.com.google.common.base.Stopwatch;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;


/**
 * Tests {@link ShardReadersPool}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShardReadersPoolTest {
    private static final int TIMEOUT_IN_MILLIS = ((int) (TimeUnit.SECONDS.toMillis(10)));

    @Mock
    private ShardRecordsIterator firstIterator;

    @Mock
    private ShardRecordsIterator secondIterator;

    @Mock
    private ShardRecordsIterator thirdIterator;

    @Mock
    private ShardRecordsIterator fourthIterator;

    @Mock
    private ShardCheckpoint firstCheckpoint;

    @Mock
    private ShardCheckpoint secondCheckpoint;

    @Mock
    private SimplifiedKinesisClient kinesis;

    @Mock
    private KinesisRecord a;

    @Mock
    private KinesisRecord b;

    @Mock
    private KinesisRecord c;

    @Mock
    private KinesisRecord d;

    private ShardReadersPool shardReadersPool;

    @Test
    public void shouldReturnAllRecords() throws KinesisShardClosedException, TransientKinesisException {
        Mockito.when(firstIterator.readNextBatch()).thenReturn(Collections.emptyList()).thenReturn(ImmutableList.of(a, b)).thenReturn(Collections.emptyList());
        Mockito.when(secondIterator.readNextBatch()).thenReturn(Collections.singletonList(c)).thenReturn(Collections.singletonList(d)).thenReturn(Collections.emptyList());
        shardReadersPool.start();
        List<KinesisRecord> fetchedRecords = new ArrayList<>();
        while ((fetchedRecords.size()) < 4) {
            CustomOptional<KinesisRecord> nextRecord = shardReadersPool.nextRecord();
            if (nextRecord.isPresent()) {
                fetchedRecords.add(nextRecord.get());
            }
        } 
        assertThat(fetchedRecords).containsExactlyInAnyOrder(a, b, c, d);
    }

    @Test
    public void shouldReturnAbsentOptionalWhenNoRecords() throws KinesisShardClosedException, TransientKinesisException {
        Mockito.when(firstIterator.readNextBatch()).thenReturn(Collections.emptyList());
        Mockito.when(secondIterator.readNextBatch()).thenReturn(Collections.emptyList());
        shardReadersPool.start();
        CustomOptional<KinesisRecord> nextRecord = shardReadersPool.nextRecord();
        assertThat(nextRecord.isPresent()).isFalse();
    }

    @Test
    public void shouldCheckpointReadRecords() throws KinesisShardClosedException, TransientKinesisException {
        Mockito.when(firstIterator.readNextBatch()).thenReturn(ImmutableList.of(a, b)).thenReturn(Collections.emptyList());
        Mockito.when(secondIterator.readNextBatch()).thenReturn(Collections.singletonList(c)).thenReturn(Collections.singletonList(d)).thenReturn(Collections.emptyList());
        shardReadersPool.start();
        int recordsFound = 0;
        while (recordsFound < 4) {
            CustomOptional<KinesisRecord> nextRecord = shardReadersPool.nextRecord();
            if (nextRecord.isPresent()) {
                recordsFound++;
                KinesisRecord kinesisRecord = nextRecord.get();
                if ("shard1".equals(kinesisRecord.getShardId())) {
                    Mockito.verify(firstIterator).ackRecord(kinesisRecord);
                } else {
                    Mockito.verify(secondIterator).ackRecord(kinesisRecord);
                }
            }
        } 
    }

    @Test
    public void shouldInterruptKinesisReadingAndStopShortly() throws KinesisShardClosedException, TransientKinesisException {
        Mockito.when(firstIterator.readNextBatch()).thenAnswer(((Answer<List<KinesisRecord>>) (( invocation) -> {
            Thread.sleep(((ShardReadersPoolTest.TIMEOUT_IN_MILLIS) / 2));
            return Collections.emptyList();
        })));
        shardReadersPool.start();
        Stopwatch stopwatch = Stopwatch.createStarted();
        shardReadersPool.stop();
        assertThat(stopwatch.elapsed(TimeUnit.MILLISECONDS)).isLessThan(ShardReadersPoolTest.TIMEOUT_IN_MILLIS);
    }

    @Test
    public void shouldInterruptPuttingRecordsToQueueAndStopShortly() throws KinesisShardClosedException, TransientKinesisException {
        Mockito.when(firstIterator.readNextBatch()).thenReturn(ImmutableList.of(a, b, c));
        KinesisReaderCheckpoint checkpoint = new KinesisReaderCheckpoint(ImmutableList.of(firstCheckpoint, secondCheckpoint));
        ShardReadersPool shardReadersPool = new ShardReadersPool(kinesis, checkpoint, 2);
        shardReadersPool.start();
        Stopwatch stopwatch = Stopwatch.createStarted();
        shardReadersPool.stop();
        assertThat(stopwatch.elapsed(TimeUnit.MILLISECONDS)).isLessThan(ShardReadersPoolTest.TIMEOUT_IN_MILLIS);
    }

    @Test
    public void shouldDetectThatNotAllShardsAreUpToDate() throws TransientKinesisException {
        Mockito.when(firstIterator.isUpToDate()).thenReturn(true);
        Mockito.when(secondIterator.isUpToDate()).thenReturn(false);
        shardReadersPool.start();
        assertThat(shardReadersPool.allShardsUpToDate()).isFalse();
    }

    @Test
    public void shouldDetectThatAllShardsAreUpToDate() throws TransientKinesisException {
        Mockito.when(firstIterator.isUpToDate()).thenReturn(true);
        Mockito.when(secondIterator.isUpToDate()).thenReturn(true);
        shardReadersPool.start();
        assertThat(shardReadersPool.allShardsUpToDate()).isTrue();
    }

    @Test
    public void shouldStopReadingShardAfterReceivingShardClosedException() throws Exception {
        Mockito.when(firstIterator.readNextBatch()).thenThrow(KinesisShardClosedException.class);
        Mockito.when(firstIterator.findSuccessiveShardRecordIterators()).thenReturn(Collections.emptyList());
        shardReadersPool.start();
        Mockito.verify(firstIterator, Mockito.timeout(ShardReadersPoolTest.TIMEOUT_IN_MILLIS).times(1)).readNextBatch();
        Mockito.verify(secondIterator, Mockito.timeout(ShardReadersPoolTest.TIMEOUT_IN_MILLIS).atLeast(2)).readNextBatch();
    }

    @Test
    public void shouldStartReadingSuccessiveShardsAfterReceivingShardClosedException() throws Exception {
        Mockito.when(firstIterator.readNextBatch()).thenThrow(KinesisShardClosedException.class);
        Mockito.when(firstIterator.findSuccessiveShardRecordIterators()).thenReturn(ImmutableList.of(thirdIterator, fourthIterator));
        shardReadersPool.start();
        Mockito.verify(thirdIterator, Mockito.timeout(ShardReadersPoolTest.TIMEOUT_IN_MILLIS).atLeast(2)).readNextBatch();
        Mockito.verify(fourthIterator, Mockito.timeout(ShardReadersPoolTest.TIMEOUT_IN_MILLIS).atLeast(2)).readNextBatch();
    }

    @Test
    public void shouldStopReadersPoolWhenLastShardReaderStopped() throws Exception {
        Mockito.when(firstIterator.readNextBatch()).thenThrow(KinesisShardClosedException.class);
        Mockito.when(firstIterator.findSuccessiveShardRecordIterators()).thenReturn(Collections.emptyList());
        shardReadersPool.start();
        Mockito.verify(firstIterator, Mockito.timeout(ShardReadersPoolTest.TIMEOUT_IN_MILLIS).times(1)).readNextBatch();
    }

    @Test
    public void shouldStopReadersPoolAlsoWhenExceptionsOccurDuringStopping() throws Exception {
        Mockito.when(firstIterator.readNextBatch()).thenThrow(KinesisShardClosedException.class);
        Mockito.when(firstIterator.findSuccessiveShardRecordIterators()).thenThrow(TransientKinesisException.class).thenReturn(Collections.emptyList());
        shardReadersPool.start();
        Mockito.verify(firstIterator, Mockito.timeout(ShardReadersPoolTest.TIMEOUT_IN_MILLIS).times(2)).readNextBatch();
    }

    @Test
    public void shouldReturnAbsentOptionalWhenStartedWithNoIterators() throws Exception {
        KinesisReaderCheckpoint checkpoint = new KinesisReaderCheckpoint(Collections.emptyList());
        shardReadersPool = Mockito.spy(new ShardReadersPool(kinesis, checkpoint));
        Mockito.doReturn(firstIterator).when(shardReadersPool).createShardIterator(ArgumentMatchers.eq(kinesis), ArgumentMatchers.any(ShardCheckpoint.class));
        shardReadersPool.start();
        assertThat(shardReadersPool.nextRecord()).isEqualTo(CustomOptional.absent());
    }

    @Test
    public void shouldForgetClosedShardIterator() throws Exception {
        Mockito.when(firstIterator.readNextBatch()).thenThrow(KinesisShardClosedException.class);
        List<ShardRecordsIterator> emptyList = Collections.emptyList();
        Mockito.when(firstIterator.findSuccessiveShardRecordIterators()).thenReturn(emptyList);
        shardReadersPool.start();
        Mockito.verify(shardReadersPool).startReadingShards(ImmutableList.of(firstIterator, secondIterator));
        Mockito.verify(shardReadersPool, Mockito.timeout(ShardReadersPoolTest.TIMEOUT_IN_MILLIS)).startReadingShards(emptyList);
        KinesisReaderCheckpoint checkpointMark = shardReadersPool.getCheckpointMark();
        assertThat(checkpointMark.iterator()).extracting("shardId", String.class).containsOnly("shard2").doesNotContain("shard1");
    }
}

