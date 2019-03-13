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
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.io.UnboundedSource.CheckpointMark;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.transforms.display.DisplayData;
import org.apache.beam.sdk.transforms.display.DisplayDataMatchers;
import org.hamcrest.MatcherAssert;
import org.joda.time.Duration;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link Read}.
 */
@RunWith(JUnit4.class)
public class ReadTest implements Serializable {
    @Rule
    public transient ExpectedException thrown = ExpectedException.none();

    @Test
    public void failsWhenCustomBoundedSourceIsNotSerializable() {
        thrown.expect(IllegalArgumentException.class);
        Read.from(new ReadTest.NotSerializableBoundedSource());
    }

    @Test
    public void succeedsWhenCustomBoundedSourceIsSerializable() {
        Read.from(new ReadTest.SerializableBoundedSource());
    }

    @Test
    public void failsWhenCustomUnboundedSourceIsNotSerializable() {
        thrown.expect(IllegalArgumentException.class);
        Read.from(new ReadTest.NotSerializableUnboundedSource());
    }

    @Test
    public void succeedsWhenCustomUnboundedSourceIsSerializable() {
        Read.from(new ReadTest.SerializableUnboundedSource());
    }

    @Test
    public void testDisplayData() {
        ReadTest.SerializableBoundedSource boundedSource = new ReadTest.SerializableBoundedSource() {
            @Override
            public void populateDisplayData(DisplayData.Builder builder) {
                builder.add(DisplayData.item("foo", "bar"));
            }
        };
        ReadTest.SerializableUnboundedSource unboundedSource = new ReadTest.SerializableUnboundedSource() {
            @Override
            public void populateDisplayData(DisplayData.Builder builder) {
                builder.add(DisplayData.item("foo", "bar"));
            }
        };
        Duration maxReadTime = Duration.standardMinutes(2345);
        Read.Bounded<String> bounded = Read.from(boundedSource);
        BoundedReadFromUnboundedSource<String> unbounded = Read.from(unboundedSource).withMaxNumRecords(1234).withMaxReadTime(maxReadTime);
        DisplayData boundedDisplayData = DisplayData.from(bounded);
        MatcherAssert.assertThat(boundedDisplayData, DisplayDataMatchers.hasDisplayItem("source", boundedSource.getClass()));
        MatcherAssert.assertThat(boundedDisplayData, DisplayDataMatchers.includesDisplayDataFor("source", boundedSource));
        DisplayData unboundedDisplayData = DisplayData.from(unbounded);
        MatcherAssert.assertThat(unboundedDisplayData, DisplayDataMatchers.hasDisplayItem("source", unboundedSource.getClass()));
        MatcherAssert.assertThat(unboundedDisplayData, DisplayDataMatchers.includesDisplayDataFor("source", unboundedSource));
        MatcherAssert.assertThat(unboundedDisplayData, DisplayDataMatchers.hasDisplayItem("maxRecords", 1234));
        MatcherAssert.assertThat(unboundedDisplayData, DisplayDataMatchers.hasDisplayItem("maxReadTime", maxReadTime));
    }

    private abstract static class CustomBoundedSource extends BoundedSource<String> {
        @Override
        public List<? extends BoundedSource<String>> split(long desiredBundleSizeBytes, PipelineOptions options) throws Exception {
            return null;
        }

        @Override
        public long getEstimatedSizeBytes(PipelineOptions options) throws Exception {
            return 0;
        }

        @Override
        public BoundedReader<String> createReader(PipelineOptions options) throws IOException {
            return null;
        }

        @Override
        public Coder<String> getOutputCoder() {
            return StringUtf8Coder.of();
        }
    }

    private static class NotSerializableBoundedSource extends ReadTest.CustomBoundedSource {
        @SuppressWarnings("unused")
        private final ReadTest.NotSerializableClass notSerializableClass = new ReadTest.NotSerializableClass();
    }

    private static class SerializableBoundedSource extends ReadTest.CustomBoundedSource {}

    private abstract static class CustomUnboundedSource extends UnboundedSource<String, ReadTest.NoOpCheckpointMark> {
        @Override
        public List<? extends UnboundedSource<String, ReadTest.NoOpCheckpointMark>> split(int desiredNumSplits, PipelineOptions options) throws Exception {
            return null;
        }

        @Override
        public UnboundedReader<String> createReader(PipelineOptions options, ReadTest.NoOpCheckpointMark checkpointMark) {
            return null;
        }

        @Override
        @Nullable
        public Coder<ReadTest.NoOpCheckpointMark> getCheckpointMarkCoder() {
            return null;
        }

        @Override
        public boolean requiresDeduping() {
            return true;
        }

        @Override
        public Coder<String> getOutputCoder() {
            return StringUtf8Coder.of();
        }
    }

    private static class NoOpCheckpointMark implements CheckpointMark {
        @Override
        public void finalizeCheckpoint() throws IOException {
        }
    }

    private static class NotSerializableUnboundedSource extends ReadTest.CustomUnboundedSource {
        @SuppressWarnings("unused")
        private final ReadTest.NotSerializableClass notSerializableClass = new ReadTest.NotSerializableClass();
    }

    private static class SerializableUnboundedSource extends ReadTest.CustomUnboundedSource {}

    private static class NotSerializableClass {}
}

