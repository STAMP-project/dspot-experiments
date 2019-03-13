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


import CachingShuffleBatchReader.BatchRange;
import ShuffleBatchReader.Batch;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


/**
 * Unit tests for {@link CachingShuffleBatchReader}.
 */
@RunWith(JUnit4.class)
public final class CachingShuffleBatchReaderTest {
    private final Batch testBatch = new ShuffleBatchReader.Batch(new ArrayList<ShuffleEntry>(), null);

    @Test
    public void readerShouldCacheReads() throws IOException {
        ShuffleBatchReader base = Mockito.mock(ShuffleBatchReader.class);
        CachingShuffleBatchReader reader = new CachingShuffleBatchReader(base);
        Mockito.when(base.read(null, null)).thenReturn(testBatch);
        // N.B. We need to capture the result of reader.read() in order to ensure
        // that there's a strong reference to it, preventing it from being
        // collected.  Not that this should be an issue in tests, but it's good to
        // be solid.
        ShuffleBatchReader.Batch read = reader.read(null, null);
        Assert.assertThat(read, Matchers.equalTo(testBatch));
        Assert.assertThat(reader.read(null, null), Matchers.equalTo(testBatch));
        Assert.assertThat(reader.read(null, null), Matchers.equalTo(testBatch));
        Assert.assertThat(reader.read(null, null), Matchers.equalTo(testBatch));
        Assert.assertThat(reader.read(null, null), Matchers.equalTo(testBatch));
        Mockito.verify(base, Mockito.times(1)).read(null, null);
    }

    @Test
    public void readerShouldNotCacheExceptions() throws IOException {
        ShuffleBatchReader base = Mockito.mock(ShuffleBatchReader.class);
        CachingShuffleBatchReader reader = new CachingShuffleBatchReader(base);
        Mockito.when(base.read(null, null)).thenThrow(new IOException("test")).thenReturn(testBatch);
        try {
            reader.read(null, null);
            Assert.fail("expected an IOException");
        } catch (Exception e) {
            // Nothing to do -- exception is expected.
        }
        Assert.assertThat(reader.read(null, null), Matchers.equalTo(testBatch));
        Mockito.verify(base, Mockito.times(2)).read(null, null);
    }

    @Test
    public void readerShouldRereadEvictedBatches() throws IOException, ExecutionException {
        ShuffleBatchReader base = Mockito.mock(ShuffleBatchReader.class);
        CachingShuffleBatchReader reader = new CachingShuffleBatchReader(base);
        Mockito.when(base.read(null, null)).thenReturn(testBatch);
        ShuffleBatchReader.Batch read = reader.read(null, null);
        Assert.assertThat(read, Matchers.equalTo(testBatch));
        Mockito.verify(base, Mockito.times(1)).read(null, null);
        CachingShuffleBatchReader.BatchRange range = new CachingShuffleBatchReader.BatchRange(null, null);
        CachingShuffleBatchReader.Batch batch = reader.cache.get(range);
        Assert.assertThat(batch, Matchers.notNullValue());
        reader.cache.invalidateAll();
        read = reader.read(null, null);
        Assert.assertThat(read, Matchers.equalTo(testBatch));
        Mockito.verify(base, Mockito.times(2)).read(null, null);
    }
}

