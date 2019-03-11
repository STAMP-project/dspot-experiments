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
package org.apache.beam.runners.dataflow.worker;


import org.apache.beam.runners.dataflow.util.CloudObject;
import org.apache.beam.runners.dataflow.worker.counters.CounterSet;
import org.apache.beam.runners.dataflow.worker.util.common.worker.OutputReceiver;
import org.apache.beam.runners.dataflow.worker.util.common.worker.ParDoFn;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.DoFnSchemaInformation;
import org.apache.beam.sdk.transforms.windowing.DefaultTrigger;
import org.apache.beam.sdk.transforms.windowing.GlobalWindows;
import org.apache.beam.sdk.util.DoFnInfo;
import org.apache.beam.sdk.util.SerializableUtils;
import org.apache.beam.sdk.util.StringUtils;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.sdk.values.TupleTag;
import org.apache.beam.sdk.values.WindowingStrategy;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link DefaultParDoFnFactory}.
 */
// TODO: Test side inputs.
@RunWith(JUnit4.class)
public class DefaultParDoFnFactoryTest {
    private static class TestDoFn extends DoFn<Integer, String> {
        final String stringField;

        final long longField;

        TestDoFn(String stringValue, long longValue) {
            this.stringField = stringValue;
            this.longField = longValue;
        }

        @ProcessElement
        public void processElement(ProcessContext c) {
            // This is called to ensure the SimpleParDoFn is fully initialized
        }
    }

    // Miscellaneous default values required by the ParDoFnFactory interface
    private static final ParDoFnFactory DEFAULT_FACTORY = new DefaultParDoFnFactory();

    private static final PipelineOptions DEFAULT_OPTIONS = PipelineOptionsFactory.create();

    private static final DataflowExecutionContext<?> DEFAULT_EXECUTION_CONTEXT = BatchModeExecutionContext.forTesting(DefaultParDoFnFactoryTest.DEFAULT_OPTIONS, "testStage");

    private final CounterSet counterSet = new CounterSet();

    private static final TupleTag<?> MAIN_OUTPUT = new TupleTag("1");

    /**
     * Tests that a {@link SimpleParDoFn} is correctly dispatched to {@code UserParDoFnFactory} and
     * instantiated correctly.
     */
    @Test
    public void testCreateSimpleParDoFn() throws Exception {
        // A serialized DoFn
        String stringFieldValue = "some state";
        long longFieldValue = 42L;
        DefaultParDoFnFactoryTest.TestDoFn fn = new DefaultParDoFnFactoryTest.TestDoFn(stringFieldValue, longFieldValue);
        String serializedFn = StringUtils.byteArrayToJsonString(SerializableUtils.serializeToByteArray(/* side input views */
        /* input coder */
        /* main output */
        DoFnInfo.forFn(fn, WindowingStrategy.globalDefault(), null, null, new TupleTag("output"), DoFnSchemaInformation.create())));
        CloudObject cloudUserFn = CloudObject.forClassName("DoFn");
        addString(cloudUserFn, "serialized_fn", serializedFn);
        // Create the ParDoFn from the serialized DoFn
        ParDoFn parDoFn = DefaultParDoFnFactoryTest.DEFAULT_FACTORY.create(DefaultParDoFnFactoryTest.DEFAULT_OPTIONS, cloudUserFn, null, DefaultParDoFnFactoryTest.MAIN_OUTPUT, ImmutableMap.<TupleTag<?>, Integer>of(DefaultParDoFnFactoryTest.MAIN_OUTPUT, 0), DefaultParDoFnFactoryTest.DEFAULT_EXECUTION_CONTEXT, TestOperationContext.create(counterSet));
        // Test that the factory created the correct class
        Assert.assertThat(parDoFn, Matchers.instanceOf(SimpleParDoFn.class));
        // TODO: move the asserts below into new tests in UserParDoFnFactoryTest, and this test should
        // simply assert that DefaultParDoFnFactory.create() matches UserParDoFnFactory.create()
        // Test that the DoFnInfo reflects the one passed in
        SimpleParDoFn simpleParDoFn = ((SimpleParDoFn) (parDoFn));
        parDoFn.startBundle(new OutputReceiver());
        // DoFnInfo may not yet be initialized until an element is processed
        parDoFn.processElement(WindowedValue.valueInGlobalWindow("foo"));
        @SuppressWarnings("rawtypes")
        DoFnInfo doFnInfo = simpleParDoFn.getDoFnInfo();
        DoFn innerDoFn = ((DefaultParDoFnFactoryTest.TestDoFn) (doFnInfo.getDoFn()));
        Assert.assertThat(innerDoFn, Matchers.instanceOf(DefaultParDoFnFactoryTest.TestDoFn.class));
        Assert.assertThat(doFnInfo.getWindowingStrategy().getWindowFn(), Matchers.instanceOf(GlobalWindows.class));
        Assert.assertThat(doFnInfo.getWindowingStrategy().getTrigger(), Matchers.instanceOf(DefaultTrigger.class));
        // Test that the deserialized user DoFn is as expected
        DefaultParDoFnFactoryTest.TestDoFn actualTestDoFn = ((DefaultParDoFnFactoryTest.TestDoFn) (innerDoFn));
        Assert.assertEquals(stringFieldValue, actualTestDoFn.stringField);
        Assert.assertEquals(longFieldValue, actualTestDoFn.longField);
    }

    @Test
    public void testCreateUnknownParDoFn() throws Exception {
        // A bogus serialized DoFn
        CloudObject cloudUserFn = CloudObject.forClassName("UnknownKindOfDoFn");
        try {
            DefaultParDoFnFactoryTest.DEFAULT_FACTORY.create(DefaultParDoFnFactoryTest.DEFAULT_OPTIONS, cloudUserFn, null, DefaultParDoFnFactoryTest.MAIN_OUTPUT, ImmutableMap.<TupleTag<?>, Integer>of(DefaultParDoFnFactoryTest.MAIN_OUTPUT, 0), DefaultParDoFnFactoryTest.DEFAULT_EXECUTION_CONTEXT, TestOperationContext.create(counterSet));
            Assert.fail("should have thrown an exception");
        } catch (Exception exn) {
            Assert.assertThat(exn.toString(), Matchers.containsString("No known ParDoFnFactory"));
        }
    }
}

