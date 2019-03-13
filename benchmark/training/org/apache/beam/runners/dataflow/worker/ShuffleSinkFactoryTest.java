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


import org.apache.beam.sdk.coders.BigEndianIntegerCoder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.coders.VoidCoder;
import org.apache.beam.sdk.transforms.windowing.IntervalWindow;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.sdk.util.WindowedValue.FullWindowedValueCoder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for ShuffleSinkFactory.
 */
@RunWith(JUnit4.class)
@SuppressWarnings("rawtypes")
public class ShuffleSinkFactoryTest {
    @Test
    public void testCreateUngroupingShuffleSink() throws Exception {
        FullWindowedValueCoder<?> coder = WindowedValue.getFullCoder(StringUtf8Coder.of(), IntervalWindow.getCoder());
        runTestCreateUngroupingShuffleSink(new byte[]{ ((byte) (225)) }, coder, coder);
    }

    @Test
    public void testCreatePartitionShuffleSink() throws Exception {
        runTestCreatePartitioningShuffleSink(new byte[]{ ((byte) (226)) }, BigEndianIntegerCoder.of(), StringUtf8Coder.of());
    }

    @Test
    public void testCreateGroupingShuffleSink() throws Exception {
        runTestCreateGroupingShuffleSink(new byte[]{ ((byte) (226)) }, BigEndianIntegerCoder.of(), WindowedValue.getFullCoder(StringUtf8Coder.of(), IntervalWindow.getCoder()));
    }

    @Test
    public void testCreateGroupingSortingShuffleSink() throws Exception {
        runTestCreateGroupingSortingShuffleSink(new byte[]{ ((byte) (227)) }, BigEndianIntegerCoder.of(), StringUtf8Coder.of(), VoidCoder.of());
    }
}

