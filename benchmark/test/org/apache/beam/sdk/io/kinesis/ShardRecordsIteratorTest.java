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


import com.amazonaws.services.kinesis.model.ExpiredIteratorException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;


/**
 * Tests {@link ShardRecordsIterator}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShardRecordsIteratorTest {
    private static final String INITIAL_ITERATOR = "INITIAL_ITERATOR";

    private static final String SECOND_ITERATOR = "SECOND_ITERATOR";

    private static final String SECOND_REFRESHED_ITERATOR = "SECOND_REFRESHED_ITERATOR";

    private static final String THIRD_ITERATOR = "THIRD_ITERATOR";

    private static final String STREAM_NAME = "STREAM_NAME";

    private static final String SHARD_ID = "SHARD_ID";

    @Mock
    private SimplifiedKinesisClient kinesisClient;

    @Mock
    private ShardCheckpoint firstCheckpoint;

    @Mock
    private ShardCheckpoint aCheckpoint;

    @Mock
    private ShardCheckpoint bCheckpoint;

    @Mock
    private ShardCheckpoint cCheckpoint;

    @Mock
    private ShardCheckpoint dCheckpoint;

    @Mock
    private GetKinesisRecordsResult firstResult;

    @Mock
    private GetKinesisRecordsResult secondResult;

    @Mock
    private GetKinesisRecordsResult thirdResult;

    @Mock
    private KinesisRecord a;

    @Mock
    private KinesisRecord b;

    @Mock
    private KinesisRecord c;

    @Mock
    private KinesisRecord d;

    @Mock
    private RecordFilter recordFilter;

    private ShardRecordsIterator iterator;

    @Test
    public void goesThroughAvailableRecords() throws IOException, KinesisShardClosedException, TransientKinesisException {
        Mockito.when(firstResult.getRecords()).thenReturn(Arrays.asList(a, b, c));
        Mockito.when(secondResult.getRecords()).thenReturn(Collections.singletonList(d));
        Mockito.when(thirdResult.getRecords()).thenReturn(Collections.emptyList());
        assertThat(iterator.getCheckpoint()).isEqualTo(firstCheckpoint);
        assertThat(iterator.readNextBatch()).isEqualTo(Arrays.asList(a, b, c));
        assertThat(iterator.readNextBatch()).isEqualTo(Collections.singletonList(d));
        assertThat(iterator.readNextBatch()).isEqualTo(Collections.emptyList());
    }

    @Test
    public void conformingRecordsMovesCheckpoint() throws IOException, TransientKinesisException {
        Mockito.when(firstResult.getRecords()).thenReturn(Arrays.asList(a, b, c));
        Mockito.when(secondResult.getRecords()).thenReturn(Collections.singletonList(d));
        Mockito.when(thirdResult.getRecords()).thenReturn(Collections.emptyList());
        iterator.ackRecord(a);
        assertThat(iterator.getCheckpoint()).isEqualTo(aCheckpoint);
        iterator.ackRecord(b);
        assertThat(iterator.getCheckpoint()).isEqualTo(bCheckpoint);
        iterator.ackRecord(c);
        assertThat(iterator.getCheckpoint()).isEqualTo(cCheckpoint);
        iterator.ackRecord(d);
        assertThat(iterator.getCheckpoint()).isEqualTo(dCheckpoint);
    }

    @Test
    public void refreshesExpiredIterator() throws IOException, KinesisShardClosedException, TransientKinesisException {
        Mockito.when(firstResult.getRecords()).thenReturn(Collections.singletonList(a));
        Mockito.when(secondResult.getRecords()).thenReturn(Collections.singletonList(b));
        Mockito.when(kinesisClient.getRecords(ShardRecordsIteratorTest.SECOND_ITERATOR, ShardRecordsIteratorTest.STREAM_NAME, ShardRecordsIteratorTest.SHARD_ID)).thenThrow(ExpiredIteratorException.class);
        Mockito.when(aCheckpoint.getShardIterator(kinesisClient)).thenReturn(ShardRecordsIteratorTest.SECOND_REFRESHED_ITERATOR);
        Mockito.when(kinesisClient.getRecords(ShardRecordsIteratorTest.SECOND_REFRESHED_ITERATOR, ShardRecordsIteratorTest.STREAM_NAME, ShardRecordsIteratorTest.SHARD_ID)).thenReturn(secondResult);
        assertThat(iterator.readNextBatch()).isEqualTo(Collections.singletonList(a));
        iterator.ackRecord(a);
        assertThat(iterator.readNextBatch()).isEqualTo(Collections.singletonList(b));
        assertThat(iterator.readNextBatch()).isEqualTo(Collections.emptyList());
    }

    private static class IdentityAnswer implements Answer<Object> {
        @Override
        public Object answer(InvocationOnMock invocation) throws Throwable {
            return invocation.getArguments()[0];
        }
    }
}

