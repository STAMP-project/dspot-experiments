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
import org.apache.beam.sdk.transforms.windowing.IntervalWindow;
import org.joda.time.Instant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for UngroupedShuffleReader.
 */
@RunWith(JUnit4.class)
public class UngroupedShuffleReaderTest {
    private static final Instant timestamp = new Instant(123000);

    private static final IntervalWindow window = new IntervalWindow(UngroupedShuffleReaderTest.timestamp, UngroupedShuffleReaderTest.timestamp.plus(1000));

    @Test
    public void testReadEmptyShuffleData() throws Exception {
        runTestReadFromShuffle(NO_INTS);
    }

    @Test
    public void testReadNonEmptyShuffleData() throws Exception {
        runTestReadFromShuffle(INTS);
    }
}

