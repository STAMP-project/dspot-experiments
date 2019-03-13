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
package org.apache.beam.runners.flink;


import PCollection.IsBounded.BOUNDED;
import PCollection.IsBounded.UNBOUNDED;
import Read.Bounded;
import Read.Unbounded;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.beam.runners.flink.FlinkStreamingTransformTranslators.UnboundedSourceWrapperNoValueWithRecordId;
import org.apache.beam.runners.flink.translation.wrappers.streaming.io.UnboundedSourceWrapper;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.io.BoundedSource;
import org.apache.beam.sdk.io.Read;
import org.apache.beam.sdk.io.UnboundedSource;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.transformations.SourceTransformation;
import org.apache.flink.streaming.api.transformations.StreamTransformation;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for Flink streaming transform translators.
 */
public class FlinkStreamingTransformTranslatorsTest {
    @Test
    public void readSourceTranslatorBoundedWithMaxParallelism() {
        final int maxParallelism = 6;
        final int parallelism = 2;
        Read.Bounded transform = Read.from(new FlinkStreamingTransformTranslatorsTest.TestBoundedSource(maxParallelism));
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(parallelism);
        env.setMaxParallelism(maxParallelism);
        StreamTransformation<?> sourceTransform = applyReadSourceTransform(transform, BOUNDED, env);
        UnboundedSourceWrapperNoValueWithRecordId source = ((UnboundedSourceWrapperNoValueWithRecordId) (((SourceTransformation<?>) (sourceTransform)).getOperator().getUserFunction()));
        Assert.assertEquals(maxParallelism, source.getUnderlyingSource().getSplitSources().size());
    }

    @Test
    public void readSourceTranslatorBoundedWithoutMaxParallelism() {
        final int parallelism = 2;
        Read.Bounded transform = Read.from(new FlinkStreamingTransformTranslatorsTest.TestBoundedSource(parallelism));
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(parallelism);
        StreamTransformation<?> sourceTransform = applyReadSourceTransform(transform, BOUNDED, env);
        UnboundedSourceWrapperNoValueWithRecordId source = ((UnboundedSourceWrapperNoValueWithRecordId) (((SourceTransformation<?>) (sourceTransform)).getOperator().getUserFunction()));
        Assert.assertEquals(parallelism, source.getUnderlyingSource().getSplitSources().size());
    }

    @Test
    public void readSourceTranslatorUnboundedWithMaxParallelism() {
        final int maxParallelism = 6;
        final int parallelism = 2;
        Read.Unbounded transform = Read.from(new FlinkStreamingTransformTranslatorsTest.TestUnboundedSource());
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(parallelism);
        env.setMaxParallelism(maxParallelism);
        StreamTransformation<?> sourceTransform = applyReadSourceTransform(transform, UNBOUNDED, env);
        UnboundedSourceWrapper source = ((UnboundedSourceWrapper) (getOperator().getUserFunction()));
        Assert.assertEquals(maxParallelism, source.getSplitSources().size());
    }

    @Test
    public void readSourceTranslatorUnboundedWithoutMaxParallelism() {
        final int parallelism = 2;
        Read.Unbounded transform = Read.from(new FlinkStreamingTransformTranslatorsTest.TestUnboundedSource());
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(parallelism);
        StreamTransformation<?> sourceTransform = applyReadSourceTransform(transform, UNBOUNDED, env);
        UnboundedSourceWrapper source = ((UnboundedSourceWrapper) (getOperator().getUserFunction()));
        Assert.assertEquals(parallelism, source.getSplitSources().size());
    }

    /**
     * {@link BoundedSource} for testing purposes of read transform translators.
     */
    private static class TestBoundedSource extends BoundedSource<String> {
        private final int bytes;

        private TestBoundedSource(int bytes) {
            this.bytes = bytes;
        }

        @Override
        public List<? extends BoundedSource<String>> split(long desiredBundleSizeBytes, PipelineOptions options) throws Exception {
            List<BoundedSource<String>> splits = new ArrayList<>();
            long remaining = bytes;
            while (remaining > 0) {
                remaining -= desiredBundleSizeBytes;
                splits.add(this);
            } 
            return splits;
        }

        @Override
        public long getEstimatedSizeBytes(PipelineOptions options) throws Exception {
            return bytes;
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

    /**
     * {@link UnboundedSource} for testing purposes of read transform translators.
     */
    private static class TestUnboundedSource extends UnboundedSource<String, UnboundedSource.CheckpointMark> {
        @Override
        public List<? extends UnboundedSource<String, CheckpointMark>> split(int desiredNumSplits, PipelineOptions options) throws Exception {
            List<UnboundedSource<String, CheckpointMark>> splits = new ArrayList<>();
            for (int i = 0; i < desiredNumSplits; i++) {
                splits.add(this);
            }
            return splits;
        }

        @Override
        public UnboundedReader<String> createReader(PipelineOptions options, @Nullable
        CheckpointMark checkpointMark) throws IOException {
            return null;
        }

        @Override
        public Coder<CheckpointMark> getCheckpointMarkCoder() {
            return null;
        }
    }
}

