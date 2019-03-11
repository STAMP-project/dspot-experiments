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
package org.apache.hadoop.examples.pi.math;


import java.util.Random;
import org.junit.Test;

import static LongLong.SIZE;


public class TestLongLong {
    static final Random RAN = new Random();

    static final long MASK = (1L << ((SIZE) >> 1)) - 1;

    @Test
    public void testMultiplication() {
        for (int i = 0; i < 100; i++) {
            final long a = TestLongLong.nextPositiveLong();
            final long b = TestLongLong.nextPositiveLong();
            TestLongLong.verifyMultiplication(a, b);
        }
        final long max = (Long.MAX_VALUE) & (TestLongLong.MASK);
        TestLongLong.verifyMultiplication(max, max);
    }

    @Test
    public void testRightShift() {
        for (int i = 0; i < 1000; i++) {
            final long a = TestLongLong.nextPositiveLong();
            final long b = TestLongLong.nextPositiveLong();
            TestLongLong.verifyMultiplication(a, b);
        }
    }
}

