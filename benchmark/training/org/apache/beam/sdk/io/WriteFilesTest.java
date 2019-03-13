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
package org.apache.beam.sdk.io;


import DisplayData.Builder;
import StandardResolveOptions.RESOLVE_FILE;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.DefaultFilenamePolicy.Params;
import org.apache.beam.sdk.io.FileBasedSink.DynamicDestinations;
import org.apache.beam.sdk.io.FileBasedSink.FilenamePolicy;
import org.apache.beam.sdk.io.FileBasedSink.OutputFileHints;
import org.apache.beam.sdk.io.fs.ResourceId;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptionsFactoryTest;
import org.apache.beam.sdk.options.ValueProvider.StaticValueProvider;
import org.apache.beam.sdk.testing.NeedsRunner;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.display.DisplayData;
import org.apache.beam.sdk.transforms.display.DisplayDataMatchers;
import org.apache.beam.sdk.transforms.windowing.BoundedWindow;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.IntervalWindow;
import org.apache.beam.sdk.transforms.windowing.PaneInfo;
import org.apache.beam.sdk.transforms.windowing.Sessions;
import org.apache.beam.sdk.transforms.windowing.Window;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionView;
import org.apache.beam.sdk.values.PDone;
import org.apache.beam.vendor.guava.v20_0.com.google.common.base.Optional;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Lists;
import org.hamcrest.Matchers;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static Compression.UNCOMPRESSED;


/**
 * Tests for the WriteFiles PTransform.
 */
@RunWith(JUnit4.class)
public class WriteFilesTest {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Rule
    public final TestPipeline p = TestPipeline.create();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    // covariant cast
    @SuppressWarnings("unchecked")
    private static final PTransform<PCollection<String>, PCollection<String>> IDENTITY_MAP = ((PTransform) (MapElements.via(new org.apache.beam.sdk.transforms.SimpleFunction<String, String>() {
        @Override
        public String apply(String input) {
            return input;
        }
    })));

    private static final PTransform<PCollection<String>, PCollectionView<Integer>> SHARDING_TRANSFORM = new PTransform<PCollection<String>, PCollectionView<Integer>>() {
        @Override
        public PCollectionView<Integer> expand(PCollection<String> input) {
            return null;
        }
    };

    private static class WindowAndReshuffle<T> extends PTransform<PCollection<T>, PCollection<T>> {
        private final Window<T> window;

        public WindowAndReshuffle(Window<T> window) {
            this.window = window;
        }

        private static class AddArbitraryKey<T> extends DoFn<T, KV<Integer, T>> {
            @ProcessElement
            public void processElement(ProcessContext c) {
                c.output(KV.of(ThreadLocalRandom.current().nextInt(), c.element()));
            }
        }

        private static class RemoveArbitraryKey<T> extends DoFn<KV<Integer, Iterable<T>>, T> {
            @ProcessElement
            public void processElement(ProcessContext c) {
                for (T s : c.element().getValue()) {
                    c.output(s);
                }
            }
        }

        @Override
        public PCollection<T> expand(PCollection<T> input) {
            return input.apply(window).apply(org.apache.beam.sdk.transforms.ParDo.of(new WriteFilesTest.WindowAndReshuffle.AddArbitraryKey())).apply(org.apache.beam.sdk.transforms.GroupByKey.create()).apply(org.apache.beam.sdk.transforms.ParDo.of(new WriteFilesTest.WindowAndReshuffle.RemoveArbitraryKey()));
        }
    }

    private static class VerifyFilesExist<DestinationT> extends PTransform<PCollection<KV<DestinationT, String>>, PDone> {
        @Override
        public PDone expand(PCollection<KV<DestinationT, String>> input) {
            input.apply(org.apache.beam.sdk.transforms.Values.create()).apply(FileIO.matchAll().withEmptyMatchTreatment(EmptyMatchTreatment.DISALLOW));
            return PDone.in(input.getPipeline());
        }
    }

    /**
     * Test a WriteFiles transform with a PCollection of elements.
     */
    @Test
    @Category(NeedsRunner.class)
    public void testWrite() throws IOException {
        List<String> inputs = Arrays.asList("Critical canary", "Apprehensive eagle", "Intimidating pigeon", "Pedantic gull", "Frisky finch");
        runWrite(inputs, WriteFilesTest.IDENTITY_MAP, getBaseOutputFilename(), WriteFiles.to(makeSimpleSink()));
    }

    /**
     * Test that WriteFiles with an empty input still produces one shard.
     */
    @Test
    @Category(NeedsRunner.class)
    public void testEmptyWrite() throws IOException {
        runWrite(Collections.emptyList(), WriteFilesTest.IDENTITY_MAP, getBaseOutputFilename(), WriteFiles.to(makeSimpleSink()));
        /* expectRemovedTempDirectory */
        WriteFilesTest.checkFileContents(getBaseOutputFilename(), Collections.emptyList(), Optional.of(1), true);
    }

    /**
     * Test that WriteFiles with a configured number of shards produces the desired number of shards
     * even when there are many elements.
     */
    @Test
    @Category(NeedsRunner.class)
    public void testShardedWrite() throws IOException {
        runShardedWrite(Arrays.asList("one", "two", "three", "four", "five", "six"), WriteFilesTest.IDENTITY_MAP, getBaseOutputFilename(), WriteFiles.to(makeSimpleSink()));
    }

    @Test
    @Category(NeedsRunner.class)
    public void testCustomShardedWrite() throws IOException {
        // Flag to validate that the pipeline options are passed to the Sink
        WriteFilesTest.WriteOptions options = TestPipeline.testingPipelineOptions().as(WriteFilesTest.WriteOptions.class);
        options.setTestFlag("test_value");
        Pipeline p = TestPipeline.create(options);
        List<String> inputs = new ArrayList<>();
        // Prepare timestamps for the elements.
        List<Long> timestamps = new ArrayList<>();
        for (long i = 0; i < 1000; i++) {
            inputs.add(Integer.toString(3));
            timestamps.add((i + 1));
        }
        SimpleSink<Void> sink = makeSimpleSink();
        WriteFiles<String, ?, String> write = WriteFiles.to(sink).withSharding(new WriteFilesTest.LargestInt());
        p.apply(org.apache.beam.sdk.transforms.Create.timestamped(inputs, timestamps).withCoder(org.apache.beam.sdk.coders.StringUtf8Coder.of())).apply(WriteFilesTest.IDENTITY_MAP).apply(write).getPerDestinationOutputFilenames().apply(new WriteFilesTest.VerifyFilesExist());
        p.run();
        /* expectRemovedTempDirectory */
        WriteFilesTest.checkFileContents(getBaseOutputFilename(), inputs, Optional.of(3), true);
    }

    /**
     * Test that WriteFiles with a configured number of shards produces the desired number of shard
     * even when there are too few elements.
     */
    @Test
    @Category(NeedsRunner.class)
    public void testExpandShardedWrite() throws IOException {
        runShardedWrite(Arrays.asList("one", "two", "three", "four", "five", "six"), WriteFilesTest.IDENTITY_MAP, getBaseOutputFilename(), WriteFiles.to(makeSimpleSink()).withNumShards(20));
    }

    /**
     * Test a WriteFiles transform with an empty PCollection.
     */
    @Test
    @Category(NeedsRunner.class)
    public void testWriteWithEmptyPCollection() throws IOException {
        List<String> inputs = new ArrayList<>();
        runWrite(inputs, WriteFilesTest.IDENTITY_MAP, getBaseOutputFilename(), WriteFiles.to(makeSimpleSink()));
    }

    /**
     * Test a WriteFiles with a windowed PCollection.
     */
    @Test
    @Category(NeedsRunner.class)
    public void testWriteWindowed() throws IOException {
        List<String> inputs = Arrays.asList("Critical canary", "Apprehensive eagle", "Intimidating pigeon", "Pedantic gull", "Frisky finch");
        runWrite(inputs, new WriteFilesTest.WindowAndReshuffle(Window.into(FixedWindows.of(Duration.millis(2)))), getBaseOutputFilename(), WriteFiles.to(makeSimpleSink()));
    }

    /**
     * Test a WriteFiles with sessions.
     */
    @Test
    @Category(NeedsRunner.class)
    public void testWriteWithSessions() throws IOException {
        List<String> inputs = Arrays.asList("Critical canary", "Apprehensive eagle", "Intimidating pigeon", "Pedantic gull", "Frisky finch");
        runWrite(inputs, new WriteFilesTest.WindowAndReshuffle(Window.into(Sessions.withGapDuration(Duration.millis(1)))), getBaseOutputFilename(), WriteFiles.to(makeSimpleSink()));
    }

    @Test
    @Category(NeedsRunner.class)
    public void testWriteSpilling() throws IOException {
        List<String> inputs = Lists.newArrayList();
        for (int i = 0; i < 100; ++i) {
            inputs.add(("mambo_number_" + i));
        }
        runWrite(inputs, Window.into(FixedWindows.of(Duration.millis(2))), getBaseOutputFilename(), WriteFiles.to(makeSimpleSink()).withMaxNumWritersPerBundle(2).withWindowedWrites().withNumShards(1));
    }

    @Test
    public void testBuildWrite() {
        SimpleSink<Void> sink = makeSimpleSink();
        WriteFiles<String, ?, String> write = WriteFiles.to(sink).withNumShards(3);
        Assert.assertThat(((SimpleSink<Void>) (write.getSink())), Matchers.is(sink));
        PTransform<PCollection<String>, PCollectionView<Integer>> originalSharding = write.getComputeNumShards();
        Assert.assertThat(write.getComputeNumShards(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(write.getNumShardsProvider(), Matchers.instanceOf(StaticValueProvider.class));
        Assert.assertThat(write.getNumShardsProvider().get(), Matchers.equalTo(3));
        Assert.assertThat(write.getComputeNumShards(), Matchers.equalTo(originalSharding));
        WriteFiles<String, ?, ?> write2 = write.withSharding(WriteFilesTest.SHARDING_TRANSFORM);
        Assert.assertThat(((SimpleSink<Void>) (write2.getSink())), Matchers.is(sink));
        Assert.assertThat(write2.getComputeNumShards(), Matchers.equalTo(WriteFilesTest.SHARDING_TRANSFORM));
        // original unchanged
        WriteFiles<String, ?, ?> writeUnsharded = write2.withRunnerDeterminedSharding();
        Assert.assertThat(writeUnsharded.getComputeNumShards(), Matchers.nullValue());
        Assert.assertThat(write.getComputeNumShards(), Matchers.equalTo(originalSharding));
    }

    @Test
    public void testDisplayData() {
        DynamicDestinations<String, Void, String> dynamicDestinations = DynamicFileDestinations.constant(DefaultFilenamePolicy.fromParams(new Params().withBaseFilename(getBaseOutputDirectory().resolve("file", RESOLVE_FILE)).withShardTemplate("-SS-of-NN")));
        SimpleSink<Void> sink = new SimpleSink<Void>(getBaseOutputDirectory(), dynamicDestinations, UNCOMPRESSED) {
            @Override
            public void populateDisplayData(DisplayData.Builder builder) {
                builder.add(DisplayData.item("foo", "bar"));
            }
        };
        WriteFiles<String, ?, String> write = WriteFiles.to(sink);
        DisplayData displayData = DisplayData.from(write);
        Assert.assertThat(displayData, DisplayDataMatchers.hasDisplayItem("sink", sink.getClass()));
        Assert.assertThat(displayData, DisplayDataMatchers.includesDisplayDataFor("sink", sink));
    }

    @Test
    @Category(NeedsRunner.class)
    public void testUnboundedNeedsWindowed() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Must use windowed writes when applying WriteFiles to an unbounded PCollection");
        SimpleSink<Void> sink = makeSimpleSink();
        p.apply(org.apache.beam.sdk.transforms.Create.of("foo")).setIsBoundedInternal(IsBounded.UNBOUNDED).apply(WriteFiles.to(sink));
        p.run();
    }

    @Test
    @Category(NeedsRunner.class)
    public void testUnboundedWritesNeedSharding() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(("When applying WriteFiles to an unbounded PCollection, " + "must specify number of output shards explicitly"));
        SimpleSink<Void> sink = makeSimpleSink();
        p.apply(org.apache.beam.sdk.transforms.Create.of("foo")).setIsBoundedInternal(IsBounded.UNBOUNDED).apply(WriteFiles.to(sink).withWindowedWrites());
        p.run();
    }

    // Test DynamicDestinations class. Expects user values to be string-encoded integers.
    // Stores the integer mod 5 as the destination, and uses that in the file prefix.
    static class TestDestinations extends DynamicDestinations<String, Integer, String> {
        private ResourceId baseOutputDirectory;

        TestDestinations(ResourceId baseOutputDirectory) {
            this.baseOutputDirectory = baseOutputDirectory;
        }

        @Override
        public String formatRecord(String record) {
            return "record_" + record;
        }

        @Override
        public Integer getDestination(String element) {
            return (Integer.valueOf(element)) % 5;
        }

        @Override
        public Integer getDefaultDestination() {
            return 0;
        }

        @Override
        public FilenamePolicy getFilenamePolicy(Integer destination) {
            return new WriteFilesTest.PerWindowFiles(baseOutputDirectory.resolve(("file_" + destination), RESOLVE_FILE), "simple");
        }
    }

    @Test
    @Category(NeedsRunner.class)
    public void testDynamicDestinationsBounded() throws Exception {
        testDynamicDestinationsHelper(true, false);
    }

    @Test
    @Category(NeedsRunner.class)
    public void testDynamicDestinationsUnbounded() throws Exception {
        testDynamicDestinationsHelper(false, false);
    }

    @Test
    @Category(NeedsRunner.class)
    public void testDynamicDestinationsFillEmptyShards() throws Exception {
        testDynamicDestinationsHelper(true, true);
    }

    @Test
    public void testShardedDisplayData() {
        DynamicDestinations<String, Void, String> dynamicDestinations = DynamicFileDestinations.constant(DefaultFilenamePolicy.fromParams(new Params().withBaseFilename(getBaseOutputDirectory().resolve("file", RESOLVE_FILE)).withShardTemplate("-SS-of-NN")));
        SimpleSink<Void> sink = new SimpleSink<Void>(getBaseOutputDirectory(), dynamicDestinations, UNCOMPRESSED) {
            @Override
            public void populateDisplayData(DisplayData.Builder builder) {
                builder.add(DisplayData.item("foo", "bar"));
            }
        };
        WriteFiles<String, ?, String> write = WriteFiles.to(sink).withNumShards(1);
        DisplayData displayData = DisplayData.from(write);
        Assert.assertThat(displayData, DisplayDataMatchers.hasDisplayItem("sink", sink.getClass()));
        Assert.assertThat(displayData, DisplayDataMatchers.includesDisplayDataFor("sink", sink));
        Assert.assertThat(displayData, DisplayDataMatchers.hasDisplayItem("numShards", 1));
    }

    @Test
    public void testCustomShardStrategyDisplayData() {
        DynamicDestinations<String, Void, String> dynamicDestinations = DynamicFileDestinations.constant(DefaultFilenamePolicy.fromParams(new Params().withBaseFilename(getBaseOutputDirectory().resolve("file", RESOLVE_FILE)).withShardTemplate("-SS-of-NN")));
        SimpleSink<Void> sink = new SimpleSink<Void>(getBaseOutputDirectory(), dynamicDestinations, UNCOMPRESSED) {
            @Override
            public void populateDisplayData(DisplayData.Builder builder) {
                builder.add(DisplayData.item("foo", "bar"));
            }
        };
        WriteFiles<String, ?, String> write = WriteFiles.to(sink).withSharding(new PTransform<PCollection<String>, PCollectionView<Integer>>() {
            @Override
            public PCollectionView<Integer> expand(PCollection<String> input) {
                return null;
            }

            @Override
            public void populateDisplayData(DisplayData.Builder builder) {
                builder.add(DisplayData.item("spam", "ham"));
            }
        });
        DisplayData displayData = DisplayData.from(write);
        Assert.assertThat(displayData, DisplayDataMatchers.hasDisplayItem("sink", sink.getClass()));
        Assert.assertThat(displayData, DisplayDataMatchers.includesDisplayDataFor("sink", sink));
        Assert.assertThat(displayData, DisplayDataMatchers.hasDisplayItem("spam", "ham"));
    }

    private static class PerWindowFiles extends FilenamePolicy {
        private static final DateTimeFormatter FORMATTER = ISODateTimeFormat.hourMinuteSecondMillis();

        private final ResourceId baseFilename;

        private final String suffix;

        public PerWindowFiles(ResourceId baseFilename, String suffix) {
            this.baseFilename = baseFilename;
            this.suffix = suffix;
        }

        public String filenamePrefixForWindow(IntervalWindow window) {
            String prefix = (baseFilename.isDirectory()) ? "" : firstNonNull(baseFilename.getFilename(), "");
            return String.format("%s%s-%s", prefix, WriteFilesTest.PerWindowFiles.FORMATTER.print(window.start()), WriteFilesTest.PerWindowFiles.FORMATTER.print(window.end()));
        }

        @Override
        public ResourceId windowedFilename(int shardNumber, int numShards, BoundedWindow window, PaneInfo paneInfo, OutputFileHints outputFileHints) {
            DecimalFormat df = new DecimalFormat("0000");
            IntervalWindow intervalWindow = ((IntervalWindow) (window));
            String filename = String.format("%s-%s-of-%s%s%s", filenamePrefixForWindow(intervalWindow), df.format(shardNumber), df.format(numShards), outputFileHints.getSuggestedFilenameSuffix(), suffix);
            return baseFilename.getCurrentDirectory().resolve(filename, RESOLVE_FILE);
        }

        @Override
        public ResourceId unwindowedFilename(int shardNumber, int numShards, OutputFileHints outputFileHints) {
            DecimalFormat df = new DecimalFormat("0000");
            String prefix = (baseFilename.isDirectory()) ? "" : firstNonNull(baseFilename.getFilename(), "");
            String filename = String.format("%s-%s-of-%s%s%s", prefix, df.format(shardNumber), df.format(numShards), outputFileHints.getSuggestedFilenameSuffix(), suffix);
            return baseFilename.getCurrentDirectory().resolve(filename, RESOLVE_FILE);
        }
    }

    /**
     * Options for test, exposed for PipelineOptionsFactory.
     */
    public interface WriteOptions extends PipelineOptionsFactoryTest.TestPipelineOptions {
        @Description("Test flag and value")
        String getTestFlag();

        void setTestFlag(String value);
    }

    /**
     * Outputs the largest integer in a {@link PCollection} into a {@link PCollectionView}. The input
     * {@link PCollection} must be convertible to integers via {@link Integer#valueOf(String)}
     */
    private static class LargestInt extends PTransform<PCollection<String>, PCollectionView<Integer>> {
        @Override
        public PCollectionView<Integer> expand(PCollection<String> input) {
            return input.apply(org.apache.beam.sdk.transforms.ParDo.of(new DoFn<String, Integer>() {
                @ProcessElement
                public void toInteger(ProcessContext ctxt) {
                    ctxt.output(Integer.valueOf(ctxt.element()));
                }
            })).apply(org.apache.beam.sdk.transforms.Top.largest(1)).apply(org.apache.beam.sdk.transforms.Flatten.iterables()).apply(org.apache.beam.sdk.transforms.View.asSingleton());
        }
    }
}

