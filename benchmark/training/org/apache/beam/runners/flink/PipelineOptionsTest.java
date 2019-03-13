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


import CheckpointingMode.EXACTLY_ONCE;
import ExecutionMode.PIPELINED;
import GlobalWindow.INSTANCE;
import PaneInfo.NO_FIRING;
import java.util.Collections;
import java.util.HashMap;
import org.apache.beam.runners.flink.translation.wrappers.streaming.DoFnOperator;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.DoFnSchemaInformation;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.sdk.values.TupleTag;
import org.apache.beam.sdk.values.WindowingStrategy;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.util.OneInputStreamOperatorTestHarness;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for serialization and deserialization of {@link PipelineOptions} in {@link DoFnOperator}.
 */
public class PipelineOptionsTest {
    /**
     * Pipeline options.
     */
    public interface MyOptions extends FlinkPipelineOptions {
        @Description("Bla bla bla")
        @Default.String("Hello")
        String getTestOption();

        void setTestOption(String value);
    }

    private static PipelineOptionsTest.MyOptions options = PipelineOptionsFactory.fromArgs("--testOption=nothing").as(PipelineOptionsTest.MyOptions.class);

    /**
     * These defaults should only be changed with a very good reason.
     */
    @Test
    public void testDefaults() {
        FlinkPipelineOptions options = PipelineOptionsFactory.as(FlinkPipelineOptions.class);
        MatcherAssert.assertThat(options.getParallelism(), Is.is((-1)));
        MatcherAssert.assertThat(options.getMaxParallelism(), Is.is((-1)));
        MatcherAssert.assertThat(options.getFlinkMaster(), Is.is("[auto]"));
        MatcherAssert.assertThat(options.getFilesToStage(), Is.is(IsNull.nullValue()));
        MatcherAssert.assertThat(options.getLatencyTrackingInterval(), Is.is(0L));
        MatcherAssert.assertThat(options.isShutdownSourcesOnFinalWatermark(), Is.is(false));
        MatcherAssert.assertThat(options.getObjectReuse(), Is.is(false));
        MatcherAssert.assertThat(options.getCheckpointingMode(), Is.is(EXACTLY_ONCE));
        MatcherAssert.assertThat(options.getMinPauseBetweenCheckpoints(), Is.is((-1L)));
        MatcherAssert.assertThat(options.getCheckpointingInterval(), Is.is((-1L)));
        MatcherAssert.assertThat(options.getCheckpointTimeoutMillis(), Is.is((-1L)));
        MatcherAssert.assertThat(options.getFailOnCheckpointingErrors(), Is.is(true));
        MatcherAssert.assertThat(options.getNumberOfExecutionRetries(), Is.is((-1)));
        MatcherAssert.assertThat(options.getExecutionRetryDelay(), Is.is((-1L)));
        MatcherAssert.assertThat(options.getRetainExternalizedCheckpointsOnCancellation(), Is.is(false));
        MatcherAssert.assertThat(options.getStateBackend(), Is.is(IsNull.nullValue()));
        MatcherAssert.assertThat(options.getMaxBundleSize(), Is.is(1000L));
        MatcherAssert.assertThat(options.getMaxBundleTimeMills(), Is.is(1000L));
        MatcherAssert.assertThat(options.getExecutionModeForBatch(), Is.is(PIPELINED));
        MatcherAssert.assertThat(options.getSavepointPath(), Is.is(IsNull.nullValue()));
        MatcherAssert.assertThat(options.getAllowNonRestoredState(), Is.is(false));
    }

    @Test(expected = Exception.class)
    public void parDoBaseClassPipelineOptionsNullTest() {
        TupleTag<String> mainTag = new TupleTag("main-output");
        Coder<WindowedValue<String>> coder = WindowedValue.getValueOnlyCoder(StringUtf8Coder.of());
        /* key coder */
        /* key selector */
        new DoFnOperator(new PipelineOptionsTest.TestDoFn(), "stepName", coder, null, Collections.emptyMap(), mainTag, Collections.emptyList(), new DoFnOperator.MultiOutputOutputManagerFactory<>(mainTag, coder), WindowingStrategy.globalDefault(), new HashMap(), Collections.emptyList(), null, null, null, DoFnSchemaInformation.create());
    }

    /**
     * Tests that PipelineOptions are present after serialization.
     */
    @Test
    public void parDoBaseClassPipelineOptionsSerializationTest() throws Exception {
        TupleTag<String> mainTag = new TupleTag("main-output");
        Coder<WindowedValue<String>> coder = WindowedValue.getValueOnlyCoder(StringUtf8Coder.of());
        DoFnOperator<String, String> doFnOperator = /* key coder */
        /* key selector */
        new DoFnOperator(new PipelineOptionsTest.TestDoFn(), "stepName", coder, null, Collections.emptyMap(), mainTag, Collections.emptyList(), new DoFnOperator.MultiOutputOutputManagerFactory<>(mainTag, coder), WindowingStrategy.globalDefault(), new HashMap(), Collections.emptyList(), PipelineOptionsTest.options, null, null, DoFnSchemaInformation.create());
        final byte[] serialized = SerializationUtils.serialize(doFnOperator);
        @SuppressWarnings("unchecked")
        DoFnOperator<Object, Object> deserialized = SerializationUtils.deserialize(serialized);
        TypeInformation<WindowedValue<Object>> typeInformation = TypeInformation.of(new org.apache.flink.api.common.typeinfo.TypeHint<WindowedValue<Object>>() {});
        OneInputStreamOperatorTestHarness<WindowedValue<Object>, WindowedValue<Object>> testHarness = new OneInputStreamOperatorTestHarness(deserialized, typeInformation.createSerializer(new ExecutionConfig()));
        testHarness.open();
        // execute once to access options
        testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord(WindowedValue.of(new Object(), Instant.now(), INSTANCE, NO_FIRING)));
        testHarness.close();
    }

    private static class TestDoFn extends DoFn<String, String> {
        @ProcessElement
        public void processElement(ProcessContext c) throws Exception {
            Assert.assertNotNull(c.getPipelineOptions());
            Assert.assertEquals(PipelineOptionsTest.options.getTestOption(), c.getPipelineOptions().as(PipelineOptionsTest.MyOptions.class).getTestOption());
        }
    }
}

