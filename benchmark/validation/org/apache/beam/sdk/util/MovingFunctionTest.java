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
package org.apache.beam.sdk.util;


import Combine.BinaryCombineLongFn;
import org.apache.beam.sdk.transforms.Combine;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests {@link MovingFunction}.
 */
@RunWith(JUnit4.class)
public class MovingFunctionTest {
    private static final long SAMPLE_PERIOD = 100;

    private static final int SAMPLE_UPDATE = 10;

    private static final int SIGNIFICANT_BUCKETS = 2;

    private static final int SIGNIFICANT_SAMPLES = 10;

    private static final BinaryCombineLongFn SUM = new Combine.BinaryCombineLongFn() {
        @Override
        public long apply(long left, long right) {
            return left + right;
        }

        @Override
        public long identity() {
            return 0;
        }
    };

    @Test
    public void significantSamples() {
        MovingFunction f = newFunc();
        Assert.assertFalse(f.isSignificant());
        for (int i = 0; i < ((MovingFunctionTest.SIGNIFICANT_SAMPLES) - 1); i++) {
            f.add(0, 0);
            Assert.assertFalse(f.isSignificant());
        }
        f.add(0, 0);
        Assert.assertTrue(f.isSignificant());
    }

    @Test
    public void significantBuckets() {
        MovingFunction f = newFunc();
        Assert.assertFalse(f.isSignificant());
        f.add(0, 0);
        Assert.assertFalse(f.isSignificant());
        f.add(MovingFunctionTest.SAMPLE_UPDATE, 0);
        Assert.assertTrue(f.isSignificant());
    }

    @Test
    public void sum() {
        MovingFunction f = newFunc();
        for (int i = 0; i < (MovingFunctionTest.SAMPLE_PERIOD); i++) {
            f.add(i, i);
            Assert.assertEquals((((i + 1) * i) / 2), f.get(i));
        }
    }

    @Test
    public void movingSum() {
        MovingFunction f = newFunc();
        int lost = 0;
        for (int i = 0; i < ((MovingFunctionTest.SAMPLE_PERIOD) * 2); i++) {
            f.add(i, 1);
            if ((i >= (MovingFunctionTest.SAMPLE_PERIOD)) && ((i % (MovingFunctionTest.SAMPLE_UPDATE)) == 0)) {
                lost += MovingFunctionTest.SAMPLE_UPDATE;
            }
            Assert.assertEquals(((i + 1) - lost), f.get(i));
        }
    }

    @Test
    public void jumpingSum() {
        MovingFunction f = newFunc();
        f.add(0, 1);
        f.add(((MovingFunctionTest.SAMPLE_PERIOD) - 1), 1);
        Assert.assertEquals(2, f.get(((MovingFunctionTest.SAMPLE_PERIOD) - 1)));
        Assert.assertEquals(1, f.get(((MovingFunctionTest.SAMPLE_PERIOD) + (3 * (MovingFunctionTest.SAMPLE_UPDATE)))));
        Assert.assertEquals(0, f.get(((MovingFunctionTest.SAMPLE_PERIOD) * 2)));
    }

    @Test
    public void properlyFlushStaleValues() {
        MovingFunction f = newFunc();
        f.add(0, 1);
        f.add(((MovingFunctionTest.SAMPLE_PERIOD) * 3), 1);
        Assert.assertEquals(1, f.get(((MovingFunctionTest.SAMPLE_PERIOD) * 3)));
    }
}

