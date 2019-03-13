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
package org.apache.hadoop.mapreduce;


import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static FileSystemCounter.BYTES_READ;
import static TaskCounter.CPU_MILLISECONDS;


/**
 * TestCounters checks the sanity and recoverability of {@code Counters}
 */
public class TestCounters {
    static final Logger LOG = LoggerFactory.getLogger(TestCounters.class);

    /**
     * Verify counter value works
     */
    @Test
    public void testCounterValue() {
        final int NUMBER_TESTS = 100;
        final int NUMBER_INC = 10;
        final Random rand = new Random();
        for (int i = 0; i < NUMBER_TESTS; i++) {
            long initValue = rand.nextInt();
            long expectedValue = initValue;
            Counter counter = new Counters().findCounter("test", "foo");
            counter.setValue(initValue);
            Assert.assertEquals("Counter value is not initialized correctly", expectedValue, counter.getValue());
            for (int j = 0; j < NUMBER_INC; j++) {
                int incValue = rand.nextInt();
                counter.increment(incValue);
                expectedValue += incValue;
                Assert.assertEquals("Counter value is not incremented correctly", expectedValue, counter.getValue());
            }
            expectedValue = rand.nextInt();
            counter.setValue(expectedValue);
            Assert.assertEquals("Counter value is not set correctly", expectedValue, counter.getValue());
        }
    }

    @Test
    public void testLimits() {
        for (int i = 0; i < 3; ++i) {
            // make sure limits apply to separate containers
            testMaxCounters(new Counters());
            testMaxGroups(new Counters());
        }
    }

    @Test
    public void testCountersIncrement() {
        Counters fCounters = new Counters();
        Counter fCounter = fCounters.findCounter(TestCounters.FRAMEWORK_COUNTER);
        fCounter.setValue(100);
        Counter gCounter = fCounters.findCounter("test", "foo");
        gCounter.setValue(200);
        Counters counters = new Counters();
        counters.incrAllCounters(fCounters);
        Counter counter;
        for (CounterGroup cg : fCounters) {
            CounterGroup group = counters.getGroup(cg.getName());
            if (group.getName().equals("test")) {
                counter = counters.findCounter("test", "foo");
                Assert.assertEquals(200, counter.getValue());
            } else {
                counter = counters.findCounter(TestCounters.FRAMEWORK_COUNTER);
                Assert.assertEquals(100, counter.getValue());
            }
        }
    }

    static final Enum<?> FRAMEWORK_COUNTER = CPU_MILLISECONDS;

    static final long FRAMEWORK_COUNTER_VALUE = 8;

    static final String FS_SCHEME = "HDFS";

    static final FileSystemCounter FS_COUNTER = BYTES_READ;

    static final long FS_COUNTER_VALUE = 10;
}

