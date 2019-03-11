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
package org.apache.flink.util;


import org.junit.Test;


/**
 * Tests for the {@link LongValueSequenceIterator}.
 */
public class LongValueSequenceIteratorTest extends TestLogger {
    @Test
    public void testSplitRegular() {
        LongValueSequenceIteratorTest.testSplitting(new org.apache.flink.util.LongValueSequenceIterator(0, 10), 2);
        LongValueSequenceIteratorTest.testSplitting(new org.apache.flink.util.LongValueSequenceIterator(100, 100000), 7);
        LongValueSequenceIteratorTest.testSplitting(new org.apache.flink.util.LongValueSequenceIterator((-100), 0), 5);
        LongValueSequenceIteratorTest.testSplitting(new org.apache.flink.util.LongValueSequenceIterator((-100), 100), 3);
    }

    @Test
    public void testSplittingLargeRangesBy2() {
        LongValueSequenceIteratorTest.testSplitting(new org.apache.flink.util.LongValueSequenceIterator(0, Long.MAX_VALUE), 2);
        LongValueSequenceIteratorTest.testSplitting(new org.apache.flink.util.LongValueSequenceIterator((-1000000000L), Long.MAX_VALUE), 2);
        LongValueSequenceIteratorTest.testSplitting(new org.apache.flink.util.LongValueSequenceIterator(Long.MIN_VALUE, Long.MAX_VALUE), 2);
    }

    @Test
    public void testSplittingTooSmallRanges() {
        LongValueSequenceIteratorTest.testSplitting(new org.apache.flink.util.LongValueSequenceIterator(0, 0), 2);
        LongValueSequenceIteratorTest.testSplitting(new org.apache.flink.util.LongValueSequenceIterator((-5), (-5)), 2);
        LongValueSequenceIteratorTest.testSplitting(new org.apache.flink.util.LongValueSequenceIterator((-5), (-4)), 3);
        LongValueSequenceIteratorTest.testSplitting(new org.apache.flink.util.LongValueSequenceIterator(10, 15), 10);
    }
}

