/**
 * Copyright (C) 2011 Clearspring Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.clearspring.analytics.stream.cardinality;


import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class TestLogLog {
    @Test
    public void testSerialization() throws IOException {
        LogLog hll = new LogLog(8);
        hll.offer("a");
        hll.offer("b");
        hll.offer("c");
        hll.offer("d");
        hll.offer("e");
        LogLog hll2 = new LogLog(hll.getBytes());
        Assert.assertEquals(hll.cardinality(), hll2.cardinality());
    }

    @Test
    public void testHighCardinality() {
        long start = System.currentTimeMillis();
        LogLog loglog = new LogLog(10);
        int size = 10000000;
        for (int i = 0; i < size; i++) {
            loglog.offer(TestICardinality.streamElement(i));
        }
        System.out.println(("time: " + ((System.currentTimeMillis()) - start)));
        long estimate = loglog.cardinality();
        double err = (Math.abs((estimate - size))) / ((double) (size));
        System.out.println(err);
        Assert.assertTrue((err < 0.11));
    }

    @Test
    public void testHighCardinalityHighOrder() {
        long start = System.currentTimeMillis();
        LogLog loglog = new LogLog(25);
        int size = 100000000;
        for (int i = 0; i < size; i++) {
            loglog.offer(TestICardinality.streamElement(i));
        }
        System.out.println(("time: " + ((System.currentTimeMillis()) - start)));
        long estimate = loglog.cardinality();
        double err = (Math.abs((estimate - size))) / ((double) (size));
        System.out.println(size);
        System.out.println(estimate);
        System.out.println(err);
        Assert.assertTrue((err < 0.06));
    }

    @Test
    public void testMerge() throws CardinalityMergeException {
        int numToMerge = 5;
        int bits = 16;
        int cardinality = 1000000;
        LogLog[] loglogs = new LogLog[numToMerge];
        LogLog baseline = new LogLog(bits);
        for (int i = 0; i < numToMerge; i++) {
            loglogs[i] = new LogLog(bits);
            for (int j = 0; j < cardinality; j++) {
                double val = Math.random();
                loglogs[i].offer(val);
                baseline.offer(val);
            }
        }
        LogLog hll = loglogs[0];
        loglogs = Arrays.asList(loglogs).subList(1, loglogs.length).toArray(new LogLog[0]);
        long mergedEstimate = hll.merge(loglogs).cardinality();
        long baselineEstimate = baseline.cardinality();
        System.out.println(("Baseline estimate: " + baselineEstimate));
        Assert.assertEquals(mergedEstimate, baselineEstimate);
    }
}

