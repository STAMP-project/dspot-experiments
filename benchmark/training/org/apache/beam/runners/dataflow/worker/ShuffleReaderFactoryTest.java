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
import org.apache.beam.sdk.coders.KvCoder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.coders.VoidCoder;
import org.apache.beam.sdk.transforms.windowing.IntervalWindow;
import org.apache.beam.sdk.util.WindowedValue.FullWindowedValueCoder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for UngroupedShuffleReaderFactory, GroupingShuffleReaderFactory, and
 * PartitioningShuffleReaderFactory.
 */
@RunWith(JUnit4.class)
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ShuffleReaderFactoryTest {
    @Test
    public void testCreatePlainUngroupedShuffleReader() throws Exception {
        runTestCreateUngroupedShuffleReader(new byte[]{ ((byte) (225)) }, null, null, StringUtf8Coder.of());
    }

    @Test
    public void testCreateRichUngroupedShuffleReader() throws Exception {
        runTestCreateUngroupedShuffleReader(new byte[]{ ((byte) (226)) }, "aaa", "zzz", BigEndianIntegerCoder.of());
    }

    @Test
    public void testCreatePlainGroupingShuffleReader() throws Exception {
        runTestCreateGroupingShuffleReader(new byte[]{ ((byte) (225)) }, null, null, BigEndianIntegerCoder.of(), StringUtf8Coder.of());
    }

    @Test
    public void testCreateRichGroupingShuffleReader() throws Exception {
        runTestCreateGroupingShuffleReader(new byte[]{ ((byte) (226)) }, "aaa", "zzz", BigEndianIntegerCoder.of(), KvCoder.of(StringUtf8Coder.of(), VoidCoder.of()));
    }

    @Test
    public void testCreatePlainPartitioningShuffleReader() throws Exception {
        runTestCreatePartitioningShuffleReader(new byte[]{ ((byte) (225)) }, null, null, BigEndianIntegerCoder.of(), FullWindowedValueCoder.of(StringUtf8Coder.of(), IntervalWindow.getCoder()));
    }

    @Test
    public void testCreateRichPartitioningShuffleReader() throws Exception {
        runTestCreatePartitioningShuffleReader(new byte[]{ ((byte) (226)) }, "aaa", "zzz", BigEndianIntegerCoder.of(), FullWindowedValueCoder.of(KvCoder.of(StringUtf8Coder.of(), VoidCoder.of()), IntervalWindow.getCoder()));
    }
}

