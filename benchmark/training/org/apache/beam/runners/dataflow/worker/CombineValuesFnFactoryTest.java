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


import Combine.AccumulatingCombineFn;
import Combine.AccumulatingCombineFn.Accumulator;
import Combine.CombineFn;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.apache.beam.runners.dataflow.worker.util.common.worker.ParDoFn;
import org.apache.beam.runners.dataflow.worker.util.common.worker.Receiver;
import org.apache.beam.sdk.coders.BigEndianIntegerCoder;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.CoderException;
import org.apache.beam.sdk.coders.CoderRegistry;
import org.apache.beam.sdk.coders.CustomCoder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.util.CoderUtils;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.sdk.util.common.ElementByteSizeObserver;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.TupleTag;
import org.apache.beam.sdk.values.TypeDescriptor;
import org.apache.beam.sdk.values.WindowingStrategy;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link CombineValuesFnFactory}.
 */
@RunWith(JUnit4.class)
public class CombineValuesFnFactoryTest {
    /**
     * Example AccumulatingCombineFn.
     */
    public static class MeanInts extends AccumulatingCombineFn<Integer, CombineValuesFnFactoryTest.CountSum, String> {
        @Override
        public CombineValuesFnFactoryTest.CountSum createAccumulator() {
            return new CombineValuesFnFactoryTest.CountSum(0, 0.0);
        }

        @Override
        public Coder<CombineValuesFnFactoryTest.CountSum> getAccumulatorCoder(CoderRegistry registry, Coder<Integer> inputCoder) {
            return new CombineValuesFnFactoryTest.CountSumCoder();
        }
    }

    static class CountSum implements Accumulator<Integer, CombineValuesFnFactoryTest.CountSum, String> {
        long count;

        double sum;

        @Override
        public void addInput(Integer element) {
            (count)++;
            sum += element.doubleValue();
        }

        @Override
        public void mergeAccumulator(CombineValuesFnFactoryTest.CountSum accumulator) {
            count += accumulator.count;
            sum += accumulator.sum;
        }

        @Override
        public String extractOutput() {
            return String.format("%.1f", ((count) == 0 ? 0.0 : (sum) / (count)));
        }

        public CountSum(long count, double sum) {
            this.count = count;
            this.sum = sum;
        }

        @Override
        public int hashCode() {
            return Objects.hash(count, sum);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == (this)) {
                return true;
            }
            if (!(obj instanceof CombineValuesFnFactoryTest.CountSum)) {
                return false;
            }
            CombineValuesFnFactoryTest.CountSum other = ((CombineValuesFnFactoryTest.CountSum) (obj));
            return ((this.count) == (other.count)) && ((Math.abs(((this.sum) - (other.sum)))) < 0.1);
        }

        @Override
        public String toString() {
            return org.apache.beam.vendor.guava.v20_0.com.google.common.base.MoreObjects.toStringHelper(this).add("count", count).add("sum", sum).toString();
        }
    }

    /**
     * An example "cheap" accumulator coder.
     */
    public static class CountSumCoder extends CustomCoder<CombineValuesFnFactoryTest.CountSum> {
        public CountSumCoder() {
        }

        @Override
        public void encode(CombineValuesFnFactoryTest.CountSum value, OutputStream outStream) throws IOException, CoderException {
            DataOutputStream dataStream = new DataOutputStream(outStream);
            dataStream.writeLong(value.count);
            dataStream.writeDouble(value.sum);
        }

        @Override
        public CombineValuesFnFactoryTest.CountSum decode(InputStream inStream) throws IOException, CoderException {
            DataInputStream dataStream = new DataInputStream(inStream);
            long count = dataStream.readLong();
            double sum = dataStream.readDouble();
            return new CombineValuesFnFactoryTest.CountSum(count, sum);
        }

        @Override
        public void verifyDeterministic() {
        }

        @Override
        public boolean isRegisterByteSizeObserverCheap(CombineValuesFnFactoryTest.CountSum value) {
            return true;
        }

        @Override
        public void registerByteSizeObserver(CombineValuesFnFactoryTest.CountSum value, ElementByteSizeObserver observer) throws Exception {
            observer.update(((long) (16)));
        }
    }

    static class TestReceiver implements Receiver {
        List<Object> receivedElems = new ArrayList<>();

        @Override
        public void process(Object outputElem) {
            receivedElems.add(outputElem);
        }
    }

    private static final ParDoFnFactory parDoFnFactory = new CombineValuesFnFactory();

    private static final TupleTag<?> MAIN_OUTPUT = new TupleTag("output");

    @Test
    public void testCombineValuesFnAll() throws Exception {
        CombineValuesFnFactoryTest.TestReceiver receiver = new CombineValuesFnFactoryTest.TestReceiver();
        CombineFn<Integer, CombineValuesFnFactoryTest.CountSum, String> combiner = new CombineValuesFnFactoryTest.MeanInts();
        ParDoFn combineParDoFn = createCombineValuesFn(CombinePhase.ALL, combiner, StringUtf8Coder.of(), BigEndianIntegerCoder.of(), new CombineValuesFnFactoryTest.CountSumCoder(), WindowingStrategy.globalDefault());
        combineParDoFn.startBundle(receiver);
        combineParDoFn.processElement(WindowedValue.valueInGlobalWindow(KV.of("a", Arrays.asList(5, 6, 7))));
        combineParDoFn.processElement(WindowedValue.valueInGlobalWindow(KV.of("b", Arrays.asList(1, 3, 7))));
        combineParDoFn.processElement(WindowedValue.valueInGlobalWindow(KV.of("c", Arrays.asList(3, 6, 8, 9))));
        combineParDoFn.finishBundle();
        Object[] expectedReceivedElems = new Object[]{ WindowedValue.valueInGlobalWindow(KV.of("a", String.format("%.1f", 6.0))), WindowedValue.valueInGlobalWindow(KV.of("b", String.format("%.1f", 3.7))), WindowedValue.valueInGlobalWindow(KV.of("c", String.format("%.1f", 6.5))) };
        Assert.assertArrayEquals(expectedReceivedElems, receiver.receivedElems.toArray());
    }

    @Test
    public void testCombineValuesFnAdd() throws Exception {
        CombineValuesFnFactoryTest.TestReceiver receiver = new CombineValuesFnFactoryTest.TestReceiver();
        CombineValuesFnFactoryTest.MeanInts mean = new CombineValuesFnFactoryTest.MeanInts();
        CombineFn<Integer, CombineValuesFnFactoryTest.CountSum, String> combiner = mean;
        ParDoFn combineParDoFn = createCombineValuesFn(CombinePhase.ADD, combiner, StringUtf8Coder.of(), BigEndianIntegerCoder.of(), new CombineValuesFnFactoryTest.CountSumCoder(), WindowingStrategy.globalDefault());
        combineParDoFn.startBundle(receiver);
        combineParDoFn.processElement(WindowedValue.valueInGlobalWindow(KV.of("a", Arrays.asList(5, 6, 7))));
        combineParDoFn.processElement(WindowedValue.valueInGlobalWindow(KV.of("b", Arrays.asList(1, 3, 7))));
        combineParDoFn.processElement(WindowedValue.valueInGlobalWindow(KV.of("c", Arrays.asList(3, 6, 8, 9))));
        combineParDoFn.finishBundle();
        Object[] expectedReceivedElems = new Object[]{ WindowedValue.valueInGlobalWindow(KV.of("a", new CombineValuesFnFactoryTest.CountSum(3, 18))), WindowedValue.valueInGlobalWindow(KV.of("b", new CombineValuesFnFactoryTest.CountSum(3, 11))), WindowedValue.valueInGlobalWindow(KV.of("c", new CombineValuesFnFactoryTest.CountSum(4, 26))) };
        Assert.assertArrayEquals(expectedReceivedElems, receiver.receivedElems.toArray());
    }

    @Test
    public void testCombineValuesFnMerge() throws Exception {
        CombineValuesFnFactoryTest.TestReceiver receiver = new CombineValuesFnFactoryTest.TestReceiver();
        CombineValuesFnFactoryTest.MeanInts mean = new CombineValuesFnFactoryTest.MeanInts();
        CombineFn<Integer, CombineValuesFnFactoryTest.CountSum, String> combiner = mean;
        ParDoFn combineParDoFn = createCombineValuesFn(CombinePhase.MERGE, combiner, StringUtf8Coder.of(), BigEndianIntegerCoder.of(), new CombineValuesFnFactoryTest.CountSumCoder(), WindowingStrategy.globalDefault());
        combineParDoFn.startBundle(receiver);
        combineParDoFn.processElement(WindowedValue.valueInGlobalWindow(KV.of("a", Arrays.asList(new CombineValuesFnFactoryTest.CountSum(3, 6), new CombineValuesFnFactoryTest.CountSum(2, 9), new CombineValuesFnFactoryTest.CountSum(1, 12)))));
        combineParDoFn.processElement(WindowedValue.valueInGlobalWindow(KV.of("b", Arrays.asList(new CombineValuesFnFactoryTest.CountSum(2, 20), new CombineValuesFnFactoryTest.CountSum(1, 1)))));
        combineParDoFn.finishBundle();
        Object[] expectedReceivedElems = new Object[]{ WindowedValue.valueInGlobalWindow(KV.of("a", new CombineValuesFnFactoryTest.CountSum(6, 27))), WindowedValue.valueInGlobalWindow(KV.of("b", new CombineValuesFnFactoryTest.CountSum(3, 21))) };
        Assert.assertArrayEquals(expectedReceivedElems, receiver.receivedElems.toArray());
    }

    @Test
    public void testCombineValuesFnExtract() throws Exception {
        CombineValuesFnFactoryTest.TestReceiver receiver = new CombineValuesFnFactoryTest.TestReceiver();
        CombineValuesFnFactoryTest.MeanInts mean = new CombineValuesFnFactoryTest.MeanInts();
        CombineFn<Integer, CombineValuesFnFactoryTest.CountSum, String> combiner = mean;
        ParDoFn combineParDoFn = createCombineValuesFn(CombinePhase.EXTRACT, combiner, StringUtf8Coder.of(), BigEndianIntegerCoder.of(), new CombineValuesFnFactoryTest.CountSumCoder(), WindowingStrategy.globalDefault());
        combineParDoFn.startBundle(receiver);
        combineParDoFn.processElement(WindowedValue.valueInGlobalWindow(KV.of("a", new CombineValuesFnFactoryTest.CountSum(6, 27))));
        combineParDoFn.processElement(WindowedValue.valueInGlobalWindow(KV.of("b", new CombineValuesFnFactoryTest.CountSum(3, 21))));
        combineParDoFn.finishBundle();
        Assert.assertArrayEquals(new Object[]{ WindowedValue.valueInGlobalWindow(KV.of("a", String.format("%.1f", 4.5))), WindowedValue.valueInGlobalWindow(KV.of("b", String.format("%.1f", 7.0))) }, receiver.receivedElems.toArray());
    }

    @Test
    public void testCombineValuesFnCoders() throws Exception {
        CoderRegistry registry = CoderRegistry.createDefault();
        CombineValuesFnFactoryTest.MeanInts meanInts = new CombineValuesFnFactoryTest.MeanInts();
        CombineValuesFnFactoryTest.CountSum countSum = new CombineValuesFnFactoryTest.CountSum(6, 27);
        Coder<CombineValuesFnFactoryTest.CountSum> coder = meanInts.getAccumulatorCoder(registry, registry.getCoder(TypeDescriptor.of(Integer.class)));
        Assert.assertEquals(countSum, CoderUtils.decodeFromByteArray(coder, CoderUtils.encodeToByteArray(coder, countSum)));
    }
}

