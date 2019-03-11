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
package org.apache.beam.runners.dataflow;


import BatchViewOverrides.BatchViewAsList.ToIsmRecordForNonGlobalWindowDoFn;
import BatchViewOverrides.BatchViewAsMap.ToMapDoFn;
import BatchViewOverrides.BatchViewAsMultimap.ToIsmMetadataRecordForKeyDoFn;
import BatchViewOverrides.BatchViewAsMultimap.ToIsmMetadataRecordForSizeDoFn;
import BatchViewOverrides.BatchViewAsMultimap.ToMultimapDoFn;
import BatchViewOverrides.BatchViewAsSingleton.IsmRecordForSingularValuePerWindowDoFn;
import GlobalWindow.INSTANCE;
import PaneInfo.NO_FIRING;
import java.util.List;
import java.util.Map;
import org.apache.beam.runners.dataflow.BatchViewOverrides.TransformedMap;
import org.apache.beam.runners.dataflow.internal.IsmFormat;
import org.apache.beam.runners.dataflow.internal.IsmFormat.IsmRecord;
import org.apache.beam.runners.dataflow.internal.IsmFormat.IsmRecordCoder;
import org.apache.beam.runners.dataflow.internal.IsmFormat.MetadataKeyCoder;
import org.apache.beam.sdk.coders.BigEndianLongCoder;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.VarLongCoder;
import org.apache.beam.sdk.transforms.DoFnTester;
import org.apache.beam.sdk.transforms.windowing.GlobalWindow;
import org.apache.beam.sdk.transforms.windowing.IntervalWindow;
import org.apache.beam.sdk.util.CoderUtils;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.sdk.util.WindowedValue.FullWindowedValueCoder;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.TupleTag;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.hamcrest.Matchers;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link BatchViewOverrides}.
 */
@RunWith(JUnit4.class)
public class BatchViewOverridesTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testBatchViewAsSingletonToIsmRecord() throws Exception {
        DoFnTester<KV<Integer, Iterable<KV<GlobalWindow, WindowedValue<String>>>>, IsmRecord<WindowedValue<String>>> doFnTester = DoFnTester.of(new IsmRecordForSingularValuePerWindowDoFn<String, GlobalWindow>(GlobalWindow.Coder.INSTANCE));
        Assert.assertThat(doFnTester.processBundle(ImmutableList.of(KV.of(0, ImmutableList.of(KV.of(INSTANCE, valueInGlobalWindow("a")))))), Matchers.contains(IsmRecord.of(ImmutableList.of(INSTANCE), valueInGlobalWindow("a"))));
    }

    @Test
    public void testBatchViewAsSingletonToIsmRecordWithMultipleValuesThrowsException() throws Exception {
        DoFnTester<KV<Integer, Iterable<KV<GlobalWindow, WindowedValue<String>>>>, IsmRecord<WindowedValue<String>>> doFnTester = DoFnTester.of(new IsmRecordForSingularValuePerWindowDoFn<String, GlobalWindow>(GlobalWindow.Coder.INSTANCE));
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("found for singleton within window");
        doFnTester.processBundle(ImmutableList.of(KV.of(0, ImmutableList.of(KV.of(INSTANCE, valueInGlobalWindow("a")), KV.of(INSTANCE, valueInGlobalWindow("b"))))));
    }

    @Test
    public void testBatchViewAsListToIsmRecordForGlobalWindow() throws Exception {
        DoFnTester<String, IsmRecord<WindowedValue<String>>> doFnTester = DoFnTester.of(new BatchViewOverrides.BatchViewAsList.ToIsmRecordForGlobalWindowDoFn<String>());
        // The order of the output elements is important relative to processing order
        Assert.assertThat(doFnTester.processBundle(ImmutableList.of("a", "b", "c")), Matchers.contains(IsmRecord.of(ImmutableList.of(INSTANCE, 0L), valueInGlobalWindow("a")), IsmRecord.of(ImmutableList.of(INSTANCE, 1L), valueInGlobalWindow("b")), IsmRecord.of(ImmutableList.of(INSTANCE, 2L), valueInGlobalWindow("c"))));
    }

    @Test
    public void testBatchViewAsListToIsmRecordForNonGlobalWindow() throws Exception {
        DoFnTester<KV<Integer, Iterable<KV<IntervalWindow, WindowedValue<Long>>>>, IsmRecord<WindowedValue<Long>>> doFnTester = DoFnTester.of(new ToIsmRecordForNonGlobalWindowDoFn<Long, IntervalWindow>(IntervalWindow.getCoder()));
        IntervalWindow windowA = new IntervalWindow(new Instant(0), new Instant(10));
        IntervalWindow windowB = new IntervalWindow(new Instant(10), new Instant(20));
        IntervalWindow windowC = new IntervalWindow(new Instant(20), new Instant(30));
        Iterable<KV<Integer, Iterable<KV<IntervalWindow, WindowedValue<Long>>>>> inputElements = ImmutableList.of(KV.of(1, ((Iterable<KV<IntervalWindow, WindowedValue<Long>>>) (ImmutableList.of(KV.of(windowA, WindowedValue.of(110L, new Instant(1), windowA, NO_FIRING)), KV.of(windowA, WindowedValue.of(111L, new Instant(3), windowA, NO_FIRING)), KV.of(windowA, WindowedValue.of(112L, new Instant(4), windowA, NO_FIRING)), KV.of(windowB, WindowedValue.of(120L, new Instant(12), windowB, NO_FIRING)), KV.of(windowB, WindowedValue.of(121L, new Instant(14), windowB, NO_FIRING)))))), KV.of(2, ((Iterable<KV<IntervalWindow, WindowedValue<Long>>>) (ImmutableList.of(KV.of(windowC, WindowedValue.of(210L, new Instant(25), windowC, NO_FIRING)))))));
        // The order of the output elements is important relative to processing order
        Assert.assertThat(doFnTester.processBundle(inputElements), Matchers.contains(IsmRecord.of(ImmutableList.of(windowA, 0L), WindowedValue.of(110L, new Instant(1), windowA, NO_FIRING)), IsmRecord.of(ImmutableList.of(windowA, 1L), WindowedValue.of(111L, new Instant(3), windowA, NO_FIRING)), IsmRecord.of(ImmutableList.of(windowA, 2L), WindowedValue.of(112L, new Instant(4), windowA, NO_FIRING)), IsmRecord.of(ImmutableList.of(windowB, 0L), WindowedValue.of(120L, new Instant(12), windowB, NO_FIRING)), IsmRecord.of(ImmutableList.of(windowB, 1L), WindowedValue.of(121L, new Instant(14), windowB, NO_FIRING)), IsmRecord.of(ImmutableList.of(windowC, 0L), WindowedValue.of(210L, new Instant(25), windowC, NO_FIRING))));
    }

    @Test
    public void testToIsmRecordForMapLikeDoFn() throws Exception {
        TupleTag<KV<Integer, KV<IntervalWindow, Long>>> outputForSizeTag = new TupleTag();
        TupleTag<KV<Integer, KV<IntervalWindow, Long>>> outputForEntrySetTag = new TupleTag();
        Coder<Long> keyCoder = VarLongCoder.of();
        Coder<IntervalWindow> windowCoder = IntervalWindow.getCoder();
        IsmRecordCoder<WindowedValue<Long>> ismCoder = IsmRecordCoder.of(1, 2, ImmutableList.of(MetadataKeyCoder.of(keyCoder), IntervalWindow.getCoder(), BigEndianLongCoder.of()), FullWindowedValueCoder.of(VarLongCoder.of(), windowCoder));
        DoFnTester<KV<Integer, Iterable<KV<KV<Long, IntervalWindow>, WindowedValue<Long>>>>, IsmRecord<WindowedValue<Long>>> doFnTester = DoFnTester.of(/* unique keys */
        new BatchViewOverrides.BatchViewAsMultimap.ToIsmRecordForMapLikeDoFn<>(outputForSizeTag, outputForEntrySetTag, windowCoder, keyCoder, ismCoder, false));
        IntervalWindow windowA = new IntervalWindow(new Instant(0), new Instant(10));
        IntervalWindow windowB = new IntervalWindow(new Instant(10), new Instant(20));
        IntervalWindow windowC = new IntervalWindow(new Instant(20), new Instant(30));
        Iterable<KV<Integer, Iterable<KV<KV<Long, IntervalWindow>, WindowedValue<Long>>>>> inputElements = ImmutableList.of(KV.of(1, // same window same key as to previous
        // same window different key as to previous
        // different window same key as to previous
        // different window and different key as to previous
        ((Iterable<KV<KV<Long, IntervalWindow>, WindowedValue<Long>>>) (ImmutableList.of(KV.of(KV.of(1L, windowA), WindowedValue.of(110L, new Instant(1), windowA, NO_FIRING)), KV.of(KV.of(1L, windowA), WindowedValue.of(111L, new Instant(2), windowA, NO_FIRING)), KV.of(KV.of(2L, windowA), WindowedValue.of(120L, new Instant(3), windowA, NO_FIRING)), KV.of(KV.of(2L, windowB), WindowedValue.of(210L, new Instant(11), windowB, NO_FIRING)), KV.of(KV.of(3L, windowB), WindowedValue.of(220L, new Instant(12), windowB, NO_FIRING)))))), KV.of(2, // different shard
        ((Iterable<KV<KV<Long, IntervalWindow>, WindowedValue<Long>>>) (ImmutableList.of(KV.of(KV.of(4L, windowC), WindowedValue.of(330L, new Instant(21), windowC, NO_FIRING)))))));
        // The order of the output elements is important relative to processing order
        Assert.assertThat(doFnTester.processBundle(inputElements), Matchers.contains(IsmRecord.of(ImmutableList.of(1L, windowA, 0L), WindowedValue.of(110L, new Instant(1), windowA, NO_FIRING)), IsmRecord.of(ImmutableList.of(1L, windowA, 1L), WindowedValue.of(111L, new Instant(2), windowA, NO_FIRING)), IsmRecord.of(ImmutableList.of(2L, windowA, 0L), WindowedValue.of(120L, new Instant(3), windowA, NO_FIRING)), IsmRecord.of(ImmutableList.of(2L, windowB, 0L), WindowedValue.of(210L, new Instant(11), windowB, NO_FIRING)), IsmRecord.of(ImmutableList.of(3L, windowB, 0L), WindowedValue.of(220L, new Instant(12), windowB, NO_FIRING)), IsmRecord.of(ImmutableList.of(4L, windowC, 0L), WindowedValue.of(330L, new Instant(21), windowC, NO_FIRING))));
        // Verify the number of unique keys per window.
        Assert.assertThat(doFnTester.takeOutputElements(outputForSizeTag), Matchers.contains(KV.of(ismCoder.hash(ImmutableList.of(IsmFormat.getMetadataKey(), windowA)), KV.of(windowA, 2L)), KV.of(ismCoder.hash(ImmutableList.of(IsmFormat.getMetadataKey(), windowB)), KV.of(windowB, 2L)), KV.of(ismCoder.hash(ImmutableList.of(IsmFormat.getMetadataKey(), windowC)), KV.of(windowC, 1L))));
        // Verify the output for the unique keys.
        Assert.assertThat(doFnTester.takeOutputElements(outputForEntrySetTag), Matchers.contains(KV.of(ismCoder.hash(ImmutableList.of(IsmFormat.getMetadataKey(), windowA)), KV.of(windowA, 1L)), KV.of(ismCoder.hash(ImmutableList.of(IsmFormat.getMetadataKey(), windowA)), KV.of(windowA, 2L)), KV.of(ismCoder.hash(ImmutableList.of(IsmFormat.getMetadataKey(), windowB)), KV.of(windowB, 2L)), KV.of(ismCoder.hash(ImmutableList.of(IsmFormat.getMetadataKey(), windowB)), KV.of(windowB, 3L)), KV.of(ismCoder.hash(ImmutableList.of(IsmFormat.getMetadataKey(), windowC)), KV.of(windowC, 4L))));
    }

    @Test
    public void testToIsmRecordForMapLikeDoFnWithoutUniqueKeysThrowsException() throws Exception {
        TupleTag<KV<Integer, KV<IntervalWindow, Long>>> outputForSizeTag = new TupleTag();
        TupleTag<KV<Integer, KV<IntervalWindow, Long>>> outputForEntrySetTag = new TupleTag();
        Coder<Long> keyCoder = VarLongCoder.of();
        Coder<IntervalWindow> windowCoder = IntervalWindow.getCoder();
        IsmRecordCoder<WindowedValue<Long>> ismCoder = IsmRecordCoder.of(1, 2, ImmutableList.of(MetadataKeyCoder.of(keyCoder), IntervalWindow.getCoder(), BigEndianLongCoder.of()), FullWindowedValueCoder.of(VarLongCoder.of(), windowCoder));
        DoFnTester<KV<Integer, Iterable<KV<KV<Long, IntervalWindow>, WindowedValue<Long>>>>, IsmRecord<WindowedValue<Long>>> doFnTester = DoFnTester.of(/* unique keys */
        new BatchViewOverrides.BatchViewAsMultimap.ToIsmRecordForMapLikeDoFn<>(outputForSizeTag, outputForEntrySetTag, windowCoder, keyCoder, ismCoder, true));
        IntervalWindow windowA = new IntervalWindow(new Instant(0), new Instant(10));
        Iterable<KV<Integer, Iterable<KV<KV<Long, IntervalWindow>, WindowedValue<Long>>>>> inputElements = ImmutableList.of(KV.of(1, // same window same key as to previous
        ((Iterable<KV<KV<Long, IntervalWindow>, WindowedValue<Long>>>) (ImmutableList.of(KV.of(KV.of(1L, windowA), WindowedValue.of(110L, new Instant(1), windowA, NO_FIRING)), KV.of(KV.of(1L, windowA), WindowedValue.of(111L, new Instant(2), windowA, NO_FIRING)))))));
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Unique keys are expected but found key");
        doFnTester.processBundle(inputElements);
    }

    @Test
    public void testToIsmMetadataRecordForSizeDoFn() throws Exception {
        Coder<Long> keyCoder = VarLongCoder.of();
        Coder<IntervalWindow> windowCoder = IntervalWindow.getCoder();
        IsmRecordCoder<WindowedValue<Long>> ismCoder = IsmRecordCoder.of(1, 2, ImmutableList.of(MetadataKeyCoder.of(keyCoder), IntervalWindow.getCoder(), BigEndianLongCoder.of()), FullWindowedValueCoder.of(VarLongCoder.of(), windowCoder));
        DoFnTester<KV<Integer, Iterable<KV<IntervalWindow, Long>>>, IsmRecord<WindowedValue<Long>>> doFnTester = DoFnTester.of(new ToIsmMetadataRecordForSizeDoFn<Long, Long, IntervalWindow>(windowCoder));
        IntervalWindow windowA = new IntervalWindow(new Instant(0), new Instant(10));
        IntervalWindow windowB = new IntervalWindow(new Instant(10), new Instant(20));
        IntervalWindow windowC = new IntervalWindow(new Instant(20), new Instant(30));
        Iterable<KV<Integer, Iterable<KV<IntervalWindow, Long>>>> inputElements = ImmutableList.of(KV.of(1, ((Iterable<KV<IntervalWindow, Long>>) (ImmutableList.of(KV.of(windowA, 2L), KV.of(windowA, 3L), KV.of(windowB, 7L))))), KV.of(ismCoder.hash(ImmutableList.of(IsmFormat.getMetadataKey(), windowB)), ((Iterable<KV<IntervalWindow, Long>>) (ImmutableList.of(KV.of(windowC, 9L))))));
        // The order of the output elements is important relative to processing order
        Assert.assertThat(doFnTester.processBundle(inputElements), Matchers.contains(IsmRecord.<WindowedValue<Long>>meta(ImmutableList.of(IsmFormat.getMetadataKey(), windowA, 0L), CoderUtils.encodeToByteArray(VarLongCoder.of(), 5L)), IsmRecord.<WindowedValue<Long>>meta(ImmutableList.of(IsmFormat.getMetadataKey(), windowB, 0L), CoderUtils.encodeToByteArray(VarLongCoder.of(), 7L)), IsmRecord.<WindowedValue<Long>>meta(ImmutableList.of(IsmFormat.getMetadataKey(), windowC, 0L), CoderUtils.encodeToByteArray(VarLongCoder.of(), 9L))));
    }

    @Test
    public void testToIsmMetadataRecordForKeyDoFn() throws Exception {
        Coder<Long> keyCoder = VarLongCoder.of();
        Coder<IntervalWindow> windowCoder = IntervalWindow.getCoder();
        IsmRecordCoder<WindowedValue<Long>> ismCoder = IsmRecordCoder.of(1, 2, ImmutableList.of(MetadataKeyCoder.of(keyCoder), IntervalWindow.getCoder(), BigEndianLongCoder.of()), FullWindowedValueCoder.of(VarLongCoder.of(), windowCoder));
        DoFnTester<KV<Integer, Iterable<KV<IntervalWindow, Long>>>, IsmRecord<WindowedValue<Long>>> doFnTester = DoFnTester.of(new ToIsmMetadataRecordForKeyDoFn<Long, Long, IntervalWindow>(keyCoder, windowCoder));
        IntervalWindow windowA = new IntervalWindow(new Instant(0), new Instant(10));
        IntervalWindow windowB = new IntervalWindow(new Instant(10), new Instant(20));
        IntervalWindow windowC = new IntervalWindow(new Instant(20), new Instant(30));
        Iterable<KV<Integer, Iterable<KV<IntervalWindow, Long>>>> inputElements = ImmutableList.of(KV.of(1, // same window as previous
        // different window as previous
        ((Iterable<KV<IntervalWindow, Long>>) (ImmutableList.of(KV.of(windowA, 2L), KV.of(windowA, 3L), KV.of(windowB, 3L))))), KV.of(ismCoder.hash(ImmutableList.of(IsmFormat.getMetadataKey(), windowB)), ((Iterable<KV<IntervalWindow, Long>>) (ImmutableList.of(KV.of(windowC, 3L))))));
        // The order of the output elements is important relative to processing order
        Assert.assertThat(doFnTester.processBundle(inputElements), Matchers.contains(IsmRecord.<WindowedValue<Long>>meta(ImmutableList.of(IsmFormat.getMetadataKey(), windowA, 1L), CoderUtils.encodeToByteArray(VarLongCoder.of(), 2L)), IsmRecord.<WindowedValue<Long>>meta(ImmutableList.of(IsmFormat.getMetadataKey(), windowA, 2L), CoderUtils.encodeToByteArray(VarLongCoder.of(), 3L)), IsmRecord.<WindowedValue<Long>>meta(ImmutableList.of(IsmFormat.getMetadataKey(), windowB, 1L), CoderUtils.encodeToByteArray(VarLongCoder.of(), 3L)), IsmRecord.<WindowedValue<Long>>meta(ImmutableList.of(IsmFormat.getMetadataKey(), windowC, 1L), CoderUtils.encodeToByteArray(VarLongCoder.of(), 3L))));
    }

    @Test
    public void testToMapDoFn() throws Exception {
        Coder<IntervalWindow> windowCoder = IntervalWindow.getCoder();
        DoFnTester<KV<Integer, Iterable<KV<IntervalWindow, WindowedValue<KV<Long, Long>>>>>, IsmRecord<WindowedValue<TransformedMap<Long, WindowedValue<Long>, Long>>>> doFnTester = DoFnTester.of(new ToMapDoFn<Long, Long, IntervalWindow>(windowCoder));
        IntervalWindow windowA = new IntervalWindow(new Instant(0), new Instant(10));
        IntervalWindow windowB = new IntervalWindow(new Instant(10), new Instant(20));
        IntervalWindow windowC = new IntervalWindow(new Instant(20), new Instant(30));
        Iterable<KV<Integer, Iterable<KV<IntervalWindow, WindowedValue<KV<Long, Long>>>>>> inputElements = ImmutableList.of(KV.of(1, ((Iterable<KV<IntervalWindow, WindowedValue<KV<Long, Long>>>>) (ImmutableList.of(KV.of(windowA, WindowedValue.of(KV.of(1L, 11L), new Instant(3), windowA, NO_FIRING)), KV.of(windowA, WindowedValue.of(KV.of(2L, 21L), new Instant(7), windowA, NO_FIRING)), KV.of(windowB, WindowedValue.of(KV.of(2L, 21L), new Instant(13), windowB, NO_FIRING)), KV.of(windowB, WindowedValue.of(KV.of(3L, 31L), new Instant(15), windowB, NO_FIRING)))))), KV.of(2, ((Iterable<KV<IntervalWindow, WindowedValue<KV<Long, Long>>>>) (ImmutableList.of(KV.of(windowC, WindowedValue.of(KV.of(4L, 41L), new Instant(25), windowC, NO_FIRING)))))));
        // The order of the output elements is important relative to processing order
        List<IsmRecord<WindowedValue<TransformedMap<Long, WindowedValue<Long>, Long>>>> output = doFnTester.processBundle(inputElements);
        Assert.assertEquals(3, output.size());
        Map<Long, Long> outputMap;
        outputMap = output.get(0).getValue().getValue();
        Assert.assertEquals(2, outputMap.size());
        Assert.assertEquals(ImmutableMap.of(1L, 11L, 2L, 21L), outputMap);
        outputMap = output.get(1).getValue().getValue();
        Assert.assertEquals(2, outputMap.size());
        Assert.assertEquals(ImmutableMap.of(2L, 21L, 3L, 31L), outputMap);
        outputMap = output.get(2).getValue().getValue();
        Assert.assertEquals(1, outputMap.size());
        Assert.assertEquals(ImmutableMap.of(4L, 41L), outputMap);
    }

    @Test
    public void testToMultimapDoFn() throws Exception {
        Coder<IntervalWindow> windowCoder = IntervalWindow.getCoder();
        DoFnTester<KV<Integer, Iterable<KV<IntervalWindow, WindowedValue<KV<Long, Long>>>>>, IsmRecord<WindowedValue<TransformedMap<Long, Iterable<WindowedValue<Long>>, Iterable<Long>>>>> doFnTester = DoFnTester.of(new ToMultimapDoFn<Long, Long, IntervalWindow>(windowCoder));
        IntervalWindow windowA = new IntervalWindow(new Instant(0), new Instant(10));
        IntervalWindow windowB = new IntervalWindow(new Instant(10), new Instant(20));
        IntervalWindow windowC = new IntervalWindow(new Instant(20), new Instant(30));
        Iterable<KV<Integer, Iterable<KV<IntervalWindow, WindowedValue<KV<Long, Long>>>>>> inputElements = ImmutableList.of(KV.of(1, // [BEAM-5184] Specifically test with a duplicate value to ensure that
        // duplicate key/values are not lost.
        ((Iterable<KV<IntervalWindow, WindowedValue<KV<Long, Long>>>>) (ImmutableList.of(KV.of(windowA, WindowedValue.of(KV.of(1L, 11L), new Instant(3), windowA, NO_FIRING)), KV.of(windowA, WindowedValue.of(KV.of(1L, 11L), new Instant(3), windowA, NO_FIRING)), KV.of(windowA, WindowedValue.of(KV.of(1L, 12L), new Instant(5), windowA, NO_FIRING)), KV.of(windowA, WindowedValue.of(KV.of(2L, 21L), new Instant(7), windowA, NO_FIRING)), KV.of(windowB, WindowedValue.of(KV.of(2L, 21L), new Instant(13), windowB, NO_FIRING)), KV.of(windowB, WindowedValue.of(KV.of(3L, 31L), new Instant(15), windowB, NO_FIRING)))))), KV.of(2, ((Iterable<KV<IntervalWindow, WindowedValue<KV<Long, Long>>>>) (ImmutableList.of(KV.of(windowC, WindowedValue.of(KV.of(4L, 41L), new Instant(25), windowC, NO_FIRING)))))));
        // The order of the output elements is important relative to processing order
        List<IsmRecord<WindowedValue<TransformedMap<Long, Iterable<WindowedValue<Long>>, Iterable<Long>>>>> output = doFnTester.processBundle(inputElements);
        Assert.assertEquals(3, output.size());
        Map<Long, Iterable<Long>> outputMap;
        outputMap = output.get(0).getValue().getValue();
        Assert.assertEquals(2, outputMap.size());
        Assert.assertThat(outputMap.get(1L), Matchers.containsInAnyOrder(11L, 11L, 12L));
        Assert.assertThat(outputMap.get(2L), Matchers.containsInAnyOrder(21L));
        outputMap = output.get(1).getValue().getValue();
        Assert.assertEquals(2, outputMap.size());
        Assert.assertThat(outputMap.get(2L), Matchers.containsInAnyOrder(21L));
        Assert.assertThat(outputMap.get(3L), Matchers.containsInAnyOrder(31L));
        outputMap = output.get(2).getValue().getValue();
        Assert.assertEquals(1, outputMap.size());
        Assert.assertThat(outputMap.get(4L), Matchers.containsInAnyOrder(41L));
    }
}

