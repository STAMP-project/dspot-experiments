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
package org.apache.beam.runners.core.construction;


import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.beam.runners.core.construction.UnboundedReadFromBoundedSource.BoundedToUnboundedSourceAdapter;
import org.apache.beam.runners.core.construction.UnboundedReadFromBoundedSource.BoundedToUnboundedSourceAdapter.Checkpoint;
import org.apache.beam.runners.core.construction.UnboundedReadFromBoundedSource.BoundedToUnboundedSourceAdapter.CheckpointCoder;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.SerializableCoder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.io.BoundedSource;
import org.apache.beam.sdk.io.CountingSource;
import org.apache.beam.sdk.io.FileBasedSource;
import org.apache.beam.sdk.io.Read;
import org.apache.beam.sdk.io.UnboundedSource;
import org.apache.beam.sdk.io.fs.MatchResult.Metadata;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.options.ValueProvider.StaticValueProvider;
import org.apache.beam.sdk.testing.CoderProperties;
import org.apache.beam.sdk.testing.NeedsRunner;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Count;
import org.apache.beam.sdk.transforms.Distinct;
import org.apache.beam.sdk.transforms.Max;
import org.apache.beam.sdk.transforms.Min;
import org.apache.beam.sdk.util.CoderUtils;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.TimestampedValue;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Lists;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link UnboundedReadFromBoundedSource}.
 */
@RunWith(JUnit4.class)
public class UnboundedReadFromBoundedSourceTest {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Rule
    public transient ExpectedException thrown = ExpectedException.none();

    @Rule
    public TestPipeline p = TestPipeline.create();

    @Test
    public void testCheckpointCoderNulls() throws Exception {
        CheckpointCoder<String> coder = new CheckpointCoder(StringUtf8Coder.of());
        Checkpoint<String> emptyCheckpoint = new Checkpoint(null, null);
        Checkpoint<String> decodedEmptyCheckpoint = CoderUtils.decodeFromByteArray(coder, CoderUtils.encodeToByteArray(coder, emptyCheckpoint));
        Assert.assertNull(decodedEmptyCheckpoint.getResidualElements());
        Assert.assertNull(decodedEmptyCheckpoint.getResidualSource());
    }

    @Test
    public void testCheckpointCoderIsSerializableWithWellKnownCoderType() throws Exception {
        CoderProperties.coderSerializable(new CheckpointCoder(Coder.INSTANCE));
    }

    @Test
    @Category(NeedsRunner.class)
    public void testBoundedToUnboundedSourceAdapter() throws Exception {
        long numElements = 100;
        BoundedSource<Long> boundedSource = CountingSource.upTo(numElements);
        UnboundedSource<Long, Checkpoint<Long>> unboundedSource = new BoundedToUnboundedSourceAdapter(boundedSource);
        PCollection<Long> output = p.apply(Read.from(unboundedSource).withMaxNumRecords(numElements));
        // Count == numElements
        PAssert.thatSingleton(output.apply("Count", Count.globally())).isEqualTo(numElements);
        // Unique count == numElements
        PAssert.thatSingleton(output.apply(Distinct.create()).apply("UniqueCount", Count.globally())).isEqualTo(numElements);
        // Min == 0
        PAssert.thatSingleton(output.apply("Min", Min.globally())).isEqualTo(0L);
        // Max == numElements-1
        PAssert.thatSingleton(output.apply("Max", Max.globally())).isEqualTo((numElements - 1));
        p.run();
    }

    @Test
    public void testCountingSourceToUnboundedCheckpoint() throws Exception {
        long numElements = 100;
        BoundedSource<Long> countingSource = CountingSource.upTo(numElements);
        List<Long> expected = Lists.newArrayList();
        for (long i = 0; i < numElements; ++i) {
            expected.add(i);
        }
        testBoundedToUnboundedSourceAdapterCheckpoint(countingSource, expected);
    }

    @Test
    public void testUnsplittableSourceToUnboundedCheckpoint() throws Exception {
        String baseName = "test-input";
        File compressedFile = tmpFolder.newFile((baseName + ".gz"));
        byte[] input = UnboundedReadFromBoundedSourceTest.generateInput(100);
        UnboundedReadFromBoundedSourceTest.writeFile(compressedFile, input);
        BoundedSource<Byte> source = new UnboundedReadFromBoundedSourceTest.UnsplittableSource(compressedFile.getPath(), 1);
        List<Byte> expected = Lists.newArrayList();
        for (byte i : input) {
            expected.add(i);
        }
        testBoundedToUnboundedSourceAdapterCheckpoint(source, expected);
    }

    @Test
    public void testCountingSourceToUnboundedCheckpointRestart() throws Exception {
        long numElements = 100;
        BoundedSource<Long> countingSource = CountingSource.upTo(numElements);
        List<Long> expected = Lists.newArrayList();
        for (long i = 0; i < numElements; ++i) {
            expected.add(i);
        }
        testBoundedToUnboundedSourceAdapterCheckpointRestart(countingSource, expected);
    }

    @Test
    public void testUnsplittableSourceToUnboundedCheckpointRestart() throws Exception {
        String baseName = "test-input";
        File compressedFile = tmpFolder.newFile((baseName + ".gz"));
        byte[] input = UnboundedReadFromBoundedSourceTest.generateInput(1000);
        UnboundedReadFromBoundedSourceTest.writeFile(compressedFile, input);
        BoundedSource<Byte> source = new UnboundedReadFromBoundedSourceTest.UnsplittableSource(compressedFile.getPath(), 1);
        List<Byte> expected = Lists.newArrayList();
        for (byte i : input) {
            expected.add(i);
        }
        testBoundedToUnboundedSourceAdapterCheckpointRestart(source, expected);
    }

    @Test
    public void testReadBeforeStart() throws Exception {
        thrown.expect(NoSuchElementException.class);
        BoundedSource<Long> countingSource = CountingSource.upTo(100);
        BoundedToUnboundedSourceAdapter<Long> unboundedSource = new BoundedToUnboundedSourceAdapter(countingSource);
        PipelineOptions options = PipelineOptionsFactory.create();
        unboundedSource.createReader(options, null).getCurrent();
    }

    @Test
    public void testInvokesSplitWithDefaultNumSplitsTooLarge() throws Exception {
        UnboundedSource<Long, ?> unboundedCountingSource = new BoundedToUnboundedSourceAdapter<Long>(CountingSource.upTo(1));
        PipelineOptions options = PipelineOptionsFactory.create();
        List<?> splits = unboundedCountingSource.split(100, options);
        Assert.assertEquals(1, splits.size());
        Assert.assertNotEquals(splits.get(0), unboundedCountingSource);
    }

    @Test
    public void testInvokingSplitProducesAtLeastOneSplit() throws Exception {
        UnboundedSource<Long, ?> unboundedCountingSource = new BoundedToUnboundedSourceAdapter<Long>(CountingSource.upTo(0));
        PipelineOptions options = PipelineOptionsFactory.create();
        List<?> splits = unboundedCountingSource.split(100, options);
        Assert.assertEquals(1, splits.size());
        Assert.assertNotEquals(splits.get(0), unboundedCountingSource);
    }

    @Test
    public void testReadFromCheckpointBeforeStart() throws Exception {
        thrown.expect(NoSuchElementException.class);
        BoundedSource<Long> countingSource = CountingSource.upTo(100);
        BoundedToUnboundedSourceAdapter<Long> unboundedSource = new BoundedToUnboundedSourceAdapter(countingSource);
        PipelineOptions options = PipelineOptionsFactory.create();
        List<TimestampedValue<Long>> elements = ImmutableList.of(TimestampedValue.of(1L, new Instant(1L)));
        Checkpoint<Long> checkpoint = new Checkpoint(elements, countingSource);
        unboundedSource.createReader(options, checkpoint).getCurrent();
    }

    /**
     * Unsplittable source for use in tests.
     */
    private static class UnsplittableSource extends FileBasedSource<Byte> {
        public UnsplittableSource(String fileOrPatternSpec, long minBundleSize) {
            super(StaticValueProvider.of(fileOrPatternSpec), minBundleSize);
        }

        public UnsplittableSource(Metadata metadata, long minBundleSize, long startOffset, long endOffset) {
            super(metadata, minBundleSize, startOffset, endOffset);
        }

        @Override
        protected UnboundedReadFromBoundedSourceTest.UnsplittableSource createForSubrangeOfFile(Metadata metadata, long start, long end) {
            return new UnboundedReadFromBoundedSourceTest.UnsplittableSource(metadata, getMinBundleSize(), start, end);
        }

        @Override
        protected UnboundedReadFromBoundedSourceTest.UnsplittableSource.UnsplittableReader createSingleFileReader(PipelineOptions options) {
            return new UnboundedReadFromBoundedSourceTest.UnsplittableSource.UnsplittableReader(this);
        }

        @Override
        public Coder<Byte> getOutputCoder() {
            return SerializableCoder.of(Byte.class);
        }

        private static class UnsplittableReader extends FileBasedReader<Byte> {
            ByteBuffer buff = ByteBuffer.allocate(1);

            Byte current;

            long offset;

            ReadableByteChannel channel;

            public UnsplittableReader(UnboundedReadFromBoundedSourceTest.UnsplittableSource source) {
                super(source);
                offset = (getStartOffset()) - 1;
            }

            @Override
            public Byte getCurrent() throws NoSuchElementException {
                return current;
            }

            @Override
            public boolean allowsDynamicSplitting() {
                return false;
            }

            @Override
            protected boolean isAtSplitPoint() {
                return true;
            }

            @Override
            protected void startReading(ReadableByteChannel channel) throws IOException {
                this.channel = channel;
            }

            @Override
            protected boolean readNextRecord() throws IOException {
                buff.clear();
                if ((channel.read(buff)) != 1) {
                    return false;
                }
                current = buff.get(0);
                offset += 1;
                return true;
            }

            @Override
            protected long getCurrentOffset() {
                return offset;
            }
        }
    }
}

