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


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.beam.sdk.transforms.windowing.IntervalWindow;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Lists;
import org.joda.time.Instant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for PartitioningShuffleReader.
 */
@RunWith(JUnit4.class)
public class PartitioningShuffleReaderTest {
    private static final List<WindowedValue<KV<Integer, String>>> NO_KVS = Collections.emptyList();

    private static final Instant timestamp = new Instant(123000);

    private static final IntervalWindow window = new IntervalWindow(PartitioningShuffleReaderTest.timestamp, PartitioningShuffleReaderTest.timestamp.plus(1000));

    private static final List<WindowedValue<KV<Integer, String>>> KVS = Arrays.asList(WindowedValue.of(KV.of(1, "in 1a"), PartitioningShuffleReaderTest.timestamp, Lists.newArrayList(PartitioningShuffleReaderTest.window), NO_FIRING), WindowedValue.of(KV.of(1, "in 1b"), PartitioningShuffleReaderTest.timestamp, Lists.newArrayList(PartitioningShuffleReaderTest.window), NO_FIRING), WindowedValue.of(KV.of(2, "in 2a"), PartitioningShuffleReaderTest.timestamp, Lists.newArrayList(PartitioningShuffleReaderTest.window), NO_FIRING), WindowedValue.of(KV.of(2, "in 2b"), PartitioningShuffleReaderTest.timestamp, Lists.newArrayList(PartitioningShuffleReaderTest.window), NO_FIRING), WindowedValue.of(KV.of(3, "in 3"), PartitioningShuffleReaderTest.timestamp, Lists.newArrayList(PartitioningShuffleReaderTest.window), NO_FIRING), WindowedValue.of(KV.of(4, "in 4a"), PartitioningShuffleReaderTest.timestamp, Lists.newArrayList(PartitioningShuffleReaderTest.window), NO_FIRING), WindowedValue.of(KV.of(4, "in 4b"), PartitioningShuffleReaderTest.timestamp, Lists.newArrayList(PartitioningShuffleReaderTest.window), NO_FIRING), WindowedValue.of(KV.of(4, "in 4c"), PartitioningShuffleReaderTest.timestamp, Lists.newArrayList(PartitioningShuffleReaderTest.window), NO_FIRING), WindowedValue.of(KV.of(4, "in 4d"), PartitioningShuffleReaderTest.timestamp, Lists.newArrayList(PartitioningShuffleReaderTest.window), NO_FIRING), WindowedValue.of(KV.of(5, "in 5"), PartitioningShuffleReaderTest.timestamp, Lists.newArrayList(PartitioningShuffleReaderTest.window), NO_FIRING));

    @Test
    public void testReadEmptyShuffleData() throws Exception {
        runTestReadFromShuffle(PartitioningShuffleReaderTest.NO_KVS);
    }

    @Test
    public void testReadNonEmptyShuffleData() throws Exception {
        runTestReadFromShuffle(PartitioningShuffleReaderTest.KVS);
    }
}

