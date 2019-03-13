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


import WorkItem.Builder;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.apache.beam.runners.core.DoFnRunner;
import org.apache.beam.runners.core.KeyedWorkItem;
import org.apache.beam.runners.core.StepContext;
import org.apache.beam.runners.dataflow.worker.util.ListOutputManager;
import org.apache.beam.runners.dataflow.worker.windmill.Windmill.InputMessageBundle;
import org.apache.beam.runners.dataflow.worker.windmill.Windmill.WorkItem;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.CollectionCoder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.transforms.windowing.BoundedWindow;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.IntervalWindow;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.TupleTag;
import org.apache.beam.sdk.values.WindowingStrategy;
import org.apache.beam.vendor.grpc.v1p13p1.com.google.protobuf.ByteString;
import org.hamcrest.Matchers;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link StreamingGroupAlsoByWindowReshuffleFn}.
 */
@RunWith(JUnit4.class)
public class StreamingGroupAlsoByWindowsReshuffleDoFnTest {
    private static final String KEY = "k";

    private static final long WORK_TOKEN = 1000L;

    private static final String SOURCE_COMPUTATION_ID = "sourceComputationId";

    private Coder<IntervalWindow> windowCoder = IntervalWindow.getCoder();

    private Coder<Collection<IntervalWindow>> windowsCoder = CollectionCoder.of(windowCoder);

    private StepContext stepContext;

    @Test
    public void testEmpty() throws Exception {
        TupleTag<KV<String, Iterable<String>>> outputTag = new TupleTag();
        ListOutputManager outputManager = new ListOutputManager();
        DoFnRunner<KeyedWorkItem<String, String>, KV<String, Iterable<String>>> runner = makeRunner(outputTag, outputManager, WindowingStrategy.of(FixedWindows.of(Duration.millis(10))));
        runner.startBundle();
        runner.finishBundle();
        List<?> result = outputManager.getOutput(outputTag);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testFixedWindows() throws Exception {
        TupleTag<KV<String, Iterable<String>>> outputTag = new TupleTag();
        ListOutputManager outputManager = new ListOutputManager();
        DoFnRunner<KeyedWorkItem<String, String>, KV<String, Iterable<String>>> runner = makeRunner(outputTag, outputManager, WindowingStrategy.of(FixedWindows.of(Duration.millis(10))));
        runner.startBundle();
        WorkItem.Builder workItem = WorkItem.newBuilder();
        workItem.setKey(ByteString.copyFromUtf8(StreamingGroupAlsoByWindowsReshuffleDoFnTest.KEY));
        workItem.setWorkToken(StreamingGroupAlsoByWindowsReshuffleDoFnTest.WORK_TOKEN);
        InputMessageBundle.Builder messageBundle = workItem.addMessageBundlesBuilder();
        messageBundle.setSourceComputationId(StreamingGroupAlsoByWindowsReshuffleDoFnTest.SOURCE_COMPUTATION_ID);
        Coder<String> valueCoder = StringUtf8Coder.of();
        addElement(messageBundle, Arrays.asList(window(0, 10)), new Instant(1), valueCoder, "v1");
        addElement(messageBundle, Arrays.asList(window(0, 10)), new Instant(2), valueCoder, "v2");
        addElement(messageBundle, Arrays.asList(window(0, 10)), new Instant(0), valueCoder, "v0");
        addElement(messageBundle, Arrays.asList(window(10, 20)), new Instant(13), valueCoder, "v3");
        runner.processElement(createValue(workItem, valueCoder));
        runner.finishBundle();
        List<WindowedValue<KV<String, Iterable<String>>>> result = outputManager.getOutput(outputTag);
        Assert.assertEquals(4, result.size());
        WindowedValue<KV<String, Iterable<String>>> item0 = result.get(0);
        Assert.assertEquals(StreamingGroupAlsoByWindowsReshuffleDoFnTest.KEY, item0.getValue().getKey());
        Assert.assertThat(item0.getValue().getValue(), Matchers.containsInAnyOrder("v1"));
        Assert.assertEquals(new Instant(1), item0.getTimestamp());
        Assert.assertThat(item0.getWindows(), Matchers.<BoundedWindow>contains(window(0, 10)));
        WindowedValue<KV<String, Iterable<String>>> item1 = result.get(1);
        Assert.assertEquals(StreamingGroupAlsoByWindowsReshuffleDoFnTest.KEY, item1.getValue().getKey());
        Assert.assertThat(item1.getValue().getValue(), Matchers.containsInAnyOrder("v2"));
        Assert.assertEquals(new Instant(2), item1.getTimestamp());
        Assert.assertThat(item1.getWindows(), Matchers.<BoundedWindow>contains(window(0, 10)));
        WindowedValue<KV<String, Iterable<String>>> item2 = result.get(2);
        Assert.assertEquals(StreamingGroupAlsoByWindowsReshuffleDoFnTest.KEY, item2.getValue().getKey());
        Assert.assertThat(item2.getValue().getValue(), Matchers.containsInAnyOrder("v0"));
        Assert.assertEquals(new Instant(0), item2.getTimestamp());
        Assert.assertThat(item2.getWindows(), Matchers.<BoundedWindow>contains(window(0, 10)));
        WindowedValue<KV<String, Iterable<String>>> item3 = result.get(3);
        Assert.assertEquals(StreamingGroupAlsoByWindowsReshuffleDoFnTest.KEY, item3.getValue().getKey());
        Assert.assertThat(item3.getValue().getValue(), Matchers.containsInAnyOrder("v3"));
        Assert.assertEquals(new Instant(13), item3.getTimestamp());
        Assert.assertThat(item3.getWindows(), Matchers.<BoundedWindow>contains(window(10, 20)));
    }
}

