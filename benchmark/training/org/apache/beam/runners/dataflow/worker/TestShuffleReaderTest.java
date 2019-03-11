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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.beam.sdk.values.KV;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests of TestShuffleReader.
 */
@RunWith(JUnit4.class)
public class TestShuffleReaderTest {
    static final String START_KEY = "ddd";

    static final String END_KEY = "mmm";

    static final List<KV<String, KV<String, String>>> NO_ENTRIES = Collections.emptyList();

    static final List<KV<String, KV<String, String>>> IN_RANGE_ENTRIES = Arrays.<KV<String, KV<String, String>>>asList(KV.of("ddd", KV.of("1", "in 1")), KV.of("ddd", KV.of("2", "in 1")), KV.of("ddd", KV.of("3", "in 1")), KV.of("dddd", KV.of("1", "in 2")), KV.of("dddd", KV.of("2", "in 2")), KV.of("de", KV.of("1", "in 3")), KV.of("ee", KV.of("1", "in 4")), KV.of("ee", KV.of("2", "in 4")), KV.of("ee", KV.of("3", "in 4")), KV.of("ee", KV.of("4", "in 4")), KV.of("mm", KV.of("1", "in 5")));

    static final List<KV<String, KV<String, String>>> BEFORE_RANGE_ENTRIES = Arrays.<KV<String, KV<String, String>>>asList(KV.of("", KV.of("1", "out 1")), KV.of("dd", KV.of("1", "out 2")));

    static final List<KV<String, KV<String, String>>> AFTER_RANGE_ENTRIES = Arrays.<KV<String, KV<String, String>>>asList(KV.of("mmm", KV.of("1", "out 3")), KV.of("mmm", KV.of("2", "out 3")), KV.of("mmmm", KV.of("1", "out 4")), KV.of("mn", KV.of("1", "out 5")), KV.of("zzz", KV.of("1", "out 6")));

    static final List<KV<String, KV<String, String>>> OUT_OF_RANGE_ENTRIES = new ArrayList<>();

    static {
        TestShuffleReaderTest.OUT_OF_RANGE_ENTRIES.addAll(TestShuffleReaderTest.BEFORE_RANGE_ENTRIES);
        TestShuffleReaderTest.OUT_OF_RANGE_ENTRIES.addAll(TestShuffleReaderTest.AFTER_RANGE_ENTRIES);
    }

    static final List<KV<String, KV<String, String>>> ALL_ENTRIES = new ArrayList<>();

    static {
        TestShuffleReaderTest.ALL_ENTRIES.addAll(TestShuffleReaderTest.BEFORE_RANGE_ENTRIES);
        TestShuffleReaderTest.ALL_ENTRIES.addAll(TestShuffleReaderTest.IN_RANGE_ENTRIES);
        TestShuffleReaderTest.ALL_ENTRIES.addAll(TestShuffleReaderTest.AFTER_RANGE_ENTRIES);
    }

    @Test
    public void testEmpty() {
        runTest(TestShuffleReaderTest.NO_ENTRIES, TestShuffleReaderTest.NO_ENTRIES, null, null);
    }

    @Test
    public void testEmptyWithRange() {
        runTest(TestShuffleReaderTest.NO_ENTRIES, TestShuffleReaderTest.NO_ENTRIES, TestShuffleReaderTest.START_KEY, TestShuffleReaderTest.END_KEY);
    }

    @Test
    public void testNonEmpty() {
        runTest(TestShuffleReaderTest.ALL_ENTRIES, TestShuffleReaderTest.NO_ENTRIES, null, null);
    }

    @Test
    public void testNonEmptyWithAllInRange() {
        runTest(TestShuffleReaderTest.IN_RANGE_ENTRIES, TestShuffleReaderTest.NO_ENTRIES, TestShuffleReaderTest.START_KEY, TestShuffleReaderTest.END_KEY);
    }

    @Test
    public void testNonEmptyWithSomeOutOfRange() {
        runTest(TestShuffleReaderTest.IN_RANGE_ENTRIES, TestShuffleReaderTest.OUT_OF_RANGE_ENTRIES, TestShuffleReaderTest.START_KEY, TestShuffleReaderTest.END_KEY);
    }

    @Test
    public void testNonEmptyWithAllOutOfRange() {
        runTest(TestShuffleReaderTest.NO_ENTRIES, TestShuffleReaderTest.OUT_OF_RANGE_ENTRIES, TestShuffleReaderTest.START_KEY, TestShuffleReaderTest.END_KEY);
    }
}

