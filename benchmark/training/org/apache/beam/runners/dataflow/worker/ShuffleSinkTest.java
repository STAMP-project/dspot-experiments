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


import TestUtils.INTS;
import TestUtils.NO_INTS;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.beam.sdk.transforms.windowing.IntervalWindow;
import org.apache.beam.sdk.values.KV;
import org.joda.time.Instant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link ShuffleSink}.
 */
@RunWith(JUnit4.class)
public class ShuffleSinkTest {
    private static final List<KV<Integer, String>> NO_KVS = Collections.emptyList();

    private static final List<KV<Integer, String>> KVS = Arrays.asList(KV.of(1, "in 1a"), KV.of(1, "in 1b"), KV.of(2, "in 2a"), KV.of(2, "in 2b"), KV.of(3, "in 3"), KV.of(4, "in 4a"), KV.of(4, "in 4b"), KV.of(4, "in 4c"), KV.of(4, "in 4d"), KV.of(5, "in 5"));

    private static final List<KV<Integer, KV<String, Integer>>> NO_SORTING_KVS = Collections.emptyList();

    private static final List<KV<Integer, KV<String, Integer>>> SORTING_KVS = Arrays.asList(KV.of(1, KV.of("in 1a", 3)), KV.of(1, KV.of("in 1b", 9)), KV.of(2, KV.of("in 2a", 2)), KV.of(2, KV.of("in 2b", 77)), KV.of(3, KV.of("in 3", 33)), KV.of(4, KV.of("in 4a", (-123))), KV.of(4, KV.of("in 4b", 0)), KV.of(4, KV.of("in 4c", (-1))), KV.of(4, KV.of("in 4d", 1)), KV.of(5, KV.of("in 5", 666)));

    private static final Instant timestamp = new Instant(123000);

    private static final IntervalWindow window = new IntervalWindow(ShuffleSinkTest.timestamp, ShuffleSinkTest.timestamp.plus(1000));

    @Test
    public void testWriteEmptyUngroupingShuffleSink() throws Exception {
        runTestWriteUngroupingShuffleSink(NO_INTS);
    }

    @Test
    public void testWriteNonEmptyUngroupingShuffleSink() throws Exception {
        runTestWriteUngroupingShuffleSink(INTS);
    }

    @Test
    public void testWriteEmptyGroupingShuffleSink() throws Exception {
        runTestWriteGroupingShuffleSink(ShuffleSinkTest.NO_KVS);
    }

    @Test
    public void testWriteNonEmptyGroupingShuffleSink() throws Exception {
        runTestWriteGroupingShuffleSink(ShuffleSinkTest.KVS);
    }

    @Test
    public void testWriteEmptyGroupingSortingShuffleSink() throws Exception {
        runTestWriteGroupingSortingShuffleSink(ShuffleSinkTest.NO_SORTING_KVS);
    }

    @Test
    public void testWriteNonEmptyGroupingSortingShuffleSink() throws Exception {
        runTestWriteGroupingSortingShuffleSink(ShuffleSinkTest.SORTING_KVS);
    }
}

