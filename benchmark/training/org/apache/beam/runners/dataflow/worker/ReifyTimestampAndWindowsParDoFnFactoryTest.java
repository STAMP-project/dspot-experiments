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


import PaneInfo.NO_FIRING;
import org.apache.avro.reflect.Nullable;
import org.apache.beam.runners.dataflow.worker.util.common.worker.Receiver;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.joda.time.Instant;
import org.junit.Test;


/**
 * Tests for {@link ReifyTimestampAndWindowsParDoFnFactory}
 */
public class ReifyTimestampAndWindowsParDoFnFactoryTest {
    @Test
    public void testSingleWindow() throws Exception {
        verifyReifiedIsInTheSameWindows(WindowedValue.of(KV.of(42, "bizzle"), new Instant(73), new org.apache.beam.sdk.transforms.windowing.IntervalWindow(new Instant(5), new Instant(15)), NO_FIRING));
    }

    @Test
    public void testMultiWindowStaysCompressed() throws Exception {
        verifyReifiedIsInTheSameWindows(WindowedValue.of(KV.of(42, "bizzle"), new Instant(73), ImmutableList.of(new org.apache.beam.sdk.transforms.windowing.IntervalWindow(new Instant(5), new Instant(15)), new org.apache.beam.sdk.transforms.windowing.IntervalWindow(new Instant(17), new Instant(97))), NO_FIRING));
    }

    private static class SingleValueReceiver<T> implements Receiver {
        @Nullable
        public T reified = null;

        @Override
        public void process(Object outputElem) throws Exception {
            ReifyTimestampAndWindowsParDoFnFactoryTest.SingleValueReceiver.checkState(((reified) == null), "Element output more than once");
            reified = ((T) (outputElem));
        }
    }
}

