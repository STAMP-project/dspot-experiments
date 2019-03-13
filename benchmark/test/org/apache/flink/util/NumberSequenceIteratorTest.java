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
 * Tests for the {@link NumberSequenceIterator}.
 */
public class NumberSequenceIteratorTest extends TestLogger {
    @Test
    public void testSplitRegular() {
        NumberSequenceIteratorTest.testSplitting(new NumberSequenceIterator(0, 10), 2);
        NumberSequenceIteratorTest.testSplitting(new NumberSequenceIterator(100, 100000), 7);
        NumberSequenceIteratorTest.testSplitting(new NumberSequenceIterator((-100), 0), 5);
        NumberSequenceIteratorTest.testSplitting(new NumberSequenceIterator((-100), 100), 3);
    }

    @Test
    public void testSplittingLargeRangesBy2() {
        NumberSequenceIteratorTest.testSplitting(new NumberSequenceIterator(0, Long.MAX_VALUE), 2);
        NumberSequenceIteratorTest.testSplitting(new NumberSequenceIterator((-1000000000L), Long.MAX_VALUE), 2);
        NumberSequenceIteratorTest.testSplitting(new NumberSequenceIterator(Long.MIN_VALUE, Long.MAX_VALUE), 2);
    }

    @Test
    public void testSplittingTooSmallRanges() {
        NumberSequenceIteratorTest.testSplitting(new NumberSequenceIterator(0, 0), 2);
        NumberSequenceIteratorTest.testSplitting(new NumberSequenceIterator((-5), (-5)), 2);
        NumberSequenceIteratorTest.testSplitting(new NumberSequenceIterator((-5), (-4)), 3);
        NumberSequenceIteratorTest.testSplitting(new NumberSequenceIterator(10, 15), 10);
    }
}

