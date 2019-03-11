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
package org.apache.beam.runners.dataflow.worker.util.common.worker;


import com.google.api.client.util.Lists;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.beam.sdk.util.common.Reiterator;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Unit tests for {@link BatchingShuffleEntryReader}.
 */
@RunWith(JUnit4.class)
public final class BatchingShuffleEntryReaderTest {
    private static final byte[] KEY = new byte[]{ 10 };

    private static final byte[] SKEY = new byte[]{ 11 };

    private static final byte[] VALUE = new byte[]{ 12 };

    private static final ShufflePosition START_POSITION = ByteArrayShufflePosition.of("aaa".getBytes(StandardCharsets.UTF_8));

    private static final ShufflePosition END_POSITION = ByteArrayShufflePosition.of("zzz".getBytes(StandardCharsets.UTF_8));

    private static final ShufflePosition NEXT_START_POSITION = ByteArrayShufflePosition.of("next".getBytes(StandardCharsets.UTF_8));

    private static final ShufflePosition SECOND_NEXT_START_POSITION = ByteArrayShufflePosition.of("next-second".getBytes(StandardCharsets.UTF_8));

    @Mock
    private ShuffleBatchReader batchReader;

    private ShuffleEntryReader reader;

    @Test
    public void readerCanRead() throws Exception {
        ShuffleEntry e1 = new ShuffleEntry(BatchingShuffleEntryReaderTest.KEY, BatchingShuffleEntryReaderTest.SKEY, BatchingShuffleEntryReaderTest.VALUE);
        ShuffleEntry e2 = new ShuffleEntry(BatchingShuffleEntryReaderTest.KEY, BatchingShuffleEntryReaderTest.SKEY, BatchingShuffleEntryReaderTest.VALUE);
        ArrayList<ShuffleEntry> entries = new ArrayList<>();
        entries.add(e1);
        entries.add(e2);
        Mockito.when(batchReader.read(BatchingShuffleEntryReaderTest.START_POSITION, BatchingShuffleEntryReaderTest.END_POSITION)).thenReturn(new ShuffleBatchReader.Batch(entries, null));
        List<ShuffleEntry> results = Lists.newArrayList(reader.read(BatchingShuffleEntryReaderTest.START_POSITION, BatchingShuffleEntryReaderTest.END_POSITION));
        Assert.assertThat(results, Matchers.contains(e1, e2));
    }

    @Test
    public void readerIteratorCanBeCopied() throws Exception {
        ShuffleEntry e1 = new ShuffleEntry(BatchingShuffleEntryReaderTest.KEY, BatchingShuffleEntryReaderTest.SKEY, BatchingShuffleEntryReaderTest.VALUE);
        ShuffleEntry e2 = new ShuffleEntry(BatchingShuffleEntryReaderTest.KEY, BatchingShuffleEntryReaderTest.SKEY, BatchingShuffleEntryReaderTest.VALUE);
        ArrayList<ShuffleEntry> entries = new ArrayList<>();
        entries.add(e1);
        entries.add(e2);
        Mockito.when(batchReader.read(BatchingShuffleEntryReaderTest.START_POSITION, BatchingShuffleEntryReaderTest.END_POSITION)).thenReturn(new ShuffleBatchReader.Batch(entries, null));
        Reiterator<ShuffleEntry> it = reader.read(BatchingShuffleEntryReaderTest.START_POSITION, BatchingShuffleEntryReaderTest.END_POSITION);
        Assert.assertThat(it.hasNext(), Matchers.equalTo(Boolean.TRUE));
        Assert.assertThat(it.next(), Matchers.equalTo(e1));
        Reiterator<ShuffleEntry> copy = it.copy();
        Assert.assertThat(it.hasNext(), Matchers.equalTo(Boolean.TRUE));
        Assert.assertThat(it.next(), Matchers.equalTo(e2));
        Assert.assertThat(it.hasNext(), Matchers.equalTo(Boolean.FALSE));
        Assert.assertThat(copy.hasNext(), Matchers.equalTo(Boolean.TRUE));
        Assert.assertThat(copy.next(), Matchers.equalTo(e2));
        Assert.assertThat(copy.hasNext(), Matchers.equalTo(Boolean.FALSE));
    }

    @Test
    public void readerShouldMergeMultipleBatchResults() throws Exception {
        ShuffleEntry e1 = new ShuffleEntry(BatchingShuffleEntryReaderTest.KEY, BatchingShuffleEntryReaderTest.SKEY, BatchingShuffleEntryReaderTest.VALUE);
        List<ShuffleEntry> e1s = Collections.singletonList(e1);
        ShuffleEntry e2 = new ShuffleEntry(BatchingShuffleEntryReaderTest.KEY, BatchingShuffleEntryReaderTest.SKEY, BatchingShuffleEntryReaderTest.VALUE);
        List<ShuffleEntry> e2s = Collections.singletonList(e2);
        Mockito.when(batchReader.read(BatchingShuffleEntryReaderTest.START_POSITION, BatchingShuffleEntryReaderTest.END_POSITION)).thenReturn(new ShuffleBatchReader.Batch(e1s, BatchingShuffleEntryReaderTest.NEXT_START_POSITION));
        Mockito.when(batchReader.read(BatchingShuffleEntryReaderTest.NEXT_START_POSITION, BatchingShuffleEntryReaderTest.END_POSITION)).thenReturn(new ShuffleBatchReader.Batch(e2s, null));
        List<ShuffleEntry> results = Lists.newArrayList(reader.read(BatchingShuffleEntryReaderTest.START_POSITION, BatchingShuffleEntryReaderTest.END_POSITION));
        Assert.assertThat(results, Matchers.contains(e1, e2));
        Mockito.verify(batchReader).read(BatchingShuffleEntryReaderTest.START_POSITION, BatchingShuffleEntryReaderTest.END_POSITION);
        Mockito.verify(batchReader).read(BatchingShuffleEntryReaderTest.NEXT_START_POSITION, BatchingShuffleEntryReaderTest.END_POSITION);
        Mockito.verifyNoMoreInteractions(batchReader);
    }

    @Test
    public void readerShouldMergeMultipleBatchResultsIncludingEmptyShards() throws Exception {
        List<ShuffleEntry> e1s = new ArrayList<>();
        List<ShuffleEntry> e2s = new ArrayList<>();
        ShuffleEntry e3 = new ShuffleEntry(BatchingShuffleEntryReaderTest.KEY, BatchingShuffleEntryReaderTest.SKEY, BatchingShuffleEntryReaderTest.VALUE);
        List<ShuffleEntry> e3s = Collections.singletonList(e3);
        Mockito.when(batchReader.read(BatchingShuffleEntryReaderTest.START_POSITION, BatchingShuffleEntryReaderTest.END_POSITION)).thenReturn(new ShuffleBatchReader.Batch(e1s, BatchingShuffleEntryReaderTest.NEXT_START_POSITION));
        Mockito.when(batchReader.read(BatchingShuffleEntryReaderTest.NEXT_START_POSITION, BatchingShuffleEntryReaderTest.END_POSITION)).thenReturn(new ShuffleBatchReader.Batch(e2s, BatchingShuffleEntryReaderTest.SECOND_NEXT_START_POSITION));
        Mockito.when(batchReader.read(BatchingShuffleEntryReaderTest.SECOND_NEXT_START_POSITION, BatchingShuffleEntryReaderTest.END_POSITION)).thenReturn(new ShuffleBatchReader.Batch(e3s, null));
        List<ShuffleEntry> results = Lists.newArrayList(reader.read(BatchingShuffleEntryReaderTest.START_POSITION, BatchingShuffleEntryReaderTest.END_POSITION));
        Assert.assertThat(results, Matchers.contains(e3));
        Mockito.verify(batchReader).read(BatchingShuffleEntryReaderTest.START_POSITION, BatchingShuffleEntryReaderTest.END_POSITION);
        Mockito.verify(batchReader).read(BatchingShuffleEntryReaderTest.NEXT_START_POSITION, BatchingShuffleEntryReaderTest.END_POSITION);
        Mockito.verify(batchReader).read(BatchingShuffleEntryReaderTest.SECOND_NEXT_START_POSITION, BatchingShuffleEntryReaderTest.END_POSITION);
        Mockito.verifyNoMoreInteractions(batchReader);
    }
}

