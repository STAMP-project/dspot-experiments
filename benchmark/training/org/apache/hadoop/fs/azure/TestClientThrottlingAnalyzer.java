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
package org.apache.hadoop.fs.azure;


import org.apache.hadoop.fs.contract.ContractTestUtils.NanoTimer;
import org.junit.Test;


/**
 * Tests for <code>ClientThrottlingAnalyzer</code>.
 */
public class TestClientThrottlingAnalyzer extends AbstractWasbTestWithTimeout {
    private static final int ANALYSIS_PERIOD = 1000;

    private static final int ANALYSIS_PERIOD_PLUS_10_PERCENT = (TestClientThrottlingAnalyzer.ANALYSIS_PERIOD) + ((TestClientThrottlingAnalyzer.ANALYSIS_PERIOD) / 10);

    private static final long MEGABYTE = 1024 * 1024;

    private static final int MAX_ACCEPTABLE_PERCENT_DIFFERENCE = 20;

    /**
     * Ensure that there is no waiting (sleepDuration = 0) if the metrics have
     * never been updated.  This validates proper initialization of
     * ClientThrottlingAnalyzer.
     */
    @Test
    public void testNoMetricUpdatesThenNoWaiting() {
        ClientThrottlingAnalyzer analyzer = new ClientThrottlingAnalyzer("test", TestClientThrottlingAnalyzer.ANALYSIS_PERIOD);
        validate(0, analyzer.getSleepDuration());
        sleep(TestClientThrottlingAnalyzer.ANALYSIS_PERIOD_PLUS_10_PERCENT);
        validate(0, analyzer.getSleepDuration());
    }

    /**
     * Ensure that there is no waiting (sleepDuration = 0) if the metrics have
     * only been updated with successful requests.
     */
    @Test
    public void testOnlySuccessThenNoWaiting() {
        ClientThrottlingAnalyzer analyzer = new ClientThrottlingAnalyzer("test", TestClientThrottlingAnalyzer.ANALYSIS_PERIOD);
        analyzer.addBytesTransferred((8 * (TestClientThrottlingAnalyzer.MEGABYTE)), false);
        validate(0, analyzer.getSleepDuration());
        sleep(TestClientThrottlingAnalyzer.ANALYSIS_PERIOD_PLUS_10_PERCENT);
        validate(0, analyzer.getSleepDuration());
    }

    /**
     * Ensure that there is waiting (sleepDuration != 0) if the metrics have
     * only been updated with failed requests.  Also ensure that the
     * sleepDuration decreases over time.
     */
    @Test
    public void testOnlyErrorsAndWaiting() {
        ClientThrottlingAnalyzer analyzer = new ClientThrottlingAnalyzer("test", TestClientThrottlingAnalyzer.ANALYSIS_PERIOD);
        validate(0, analyzer.getSleepDuration());
        analyzer.addBytesTransferred((4 * (TestClientThrottlingAnalyzer.MEGABYTE)), true);
        sleep(TestClientThrottlingAnalyzer.ANALYSIS_PERIOD_PLUS_10_PERCENT);
        final int expectedSleepDuration1 = 1100;
        validateLessThanOrEqual(expectedSleepDuration1, analyzer.getSleepDuration());
        sleep((10 * (TestClientThrottlingAnalyzer.ANALYSIS_PERIOD)));
        final int expectedSleepDuration2 = 900;
        validateLessThanOrEqual(expectedSleepDuration2, analyzer.getSleepDuration());
    }

    /**
     * Ensure that there is waiting (sleepDuration != 0) if the metrics have
     * only been updated with both successful and failed requests.  Also ensure
     * that the sleepDuration decreases over time.
     */
    @Test
    public void testSuccessAndErrorsAndWaiting() {
        ClientThrottlingAnalyzer analyzer = new ClientThrottlingAnalyzer("test", TestClientThrottlingAnalyzer.ANALYSIS_PERIOD);
        validate(0, analyzer.getSleepDuration());
        analyzer.addBytesTransferred((8 * (TestClientThrottlingAnalyzer.MEGABYTE)), false);
        analyzer.addBytesTransferred((2 * (TestClientThrottlingAnalyzer.MEGABYTE)), true);
        sleep(TestClientThrottlingAnalyzer.ANALYSIS_PERIOD_PLUS_10_PERCENT);
        NanoTimer timer = new NanoTimer();
        analyzer.suspendIfNecessary();
        final int expectedElapsedTime = 126;
        fuzzyValidate(expectedElapsedTime, timer.elapsedTimeMs(), TestClientThrottlingAnalyzer.MAX_ACCEPTABLE_PERCENT_DIFFERENCE);
        sleep((10 * (TestClientThrottlingAnalyzer.ANALYSIS_PERIOD)));
        final int expectedSleepDuration = 110;
        validateLessThanOrEqual(expectedSleepDuration, analyzer.getSleepDuration());
    }

    /**
     * Ensure that there is waiting (sleepDuration != 0) if the metrics have
     * only been updated with many successful and failed requests.  Also ensure
     * that the sleepDuration decreases to zero over time.
     */
    @Test
    public void testManySuccessAndErrorsAndWaiting() {
        ClientThrottlingAnalyzer analyzer = new ClientThrottlingAnalyzer("test", TestClientThrottlingAnalyzer.ANALYSIS_PERIOD);
        validate(0, analyzer.getSleepDuration());
        final int numberOfRequests = 20;
        for (int i = 0; i < numberOfRequests; i++) {
            analyzer.addBytesTransferred((8 * (TestClientThrottlingAnalyzer.MEGABYTE)), false);
            analyzer.addBytesTransferred((2 * (TestClientThrottlingAnalyzer.MEGABYTE)), true);
        }
        sleep(TestClientThrottlingAnalyzer.ANALYSIS_PERIOD_PLUS_10_PERCENT);
        NanoTimer timer = new NanoTimer();
        analyzer.suspendIfNecessary();
        fuzzyValidate(7, timer.elapsedTimeMs(), TestClientThrottlingAnalyzer.MAX_ACCEPTABLE_PERCENT_DIFFERENCE);
        sleep((10 * (TestClientThrottlingAnalyzer.ANALYSIS_PERIOD)));
        validate(0, analyzer.getSleepDuration());
    }
}

