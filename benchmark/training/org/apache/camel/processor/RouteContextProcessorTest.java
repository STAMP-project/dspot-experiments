/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.processor;


import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.junit.Ignore;
import org.junit.Test;


/**
 * This is a manual test to run
 */
@Ignore("Manual test")
public class RouteContextProcessorTest extends ContextTestSupport {
    // Number of concurrent processing threads
    public static final int CONCURRENCY = 10;

    // Additional resequencer time-out above theoretical time-out
    public static final long SAFETY_TIMEOUT = 100;

    // Additional resequencer capacity above theoretical capacity
    public static final int SAFETY_CAPACITY = 10;

    // Resequencer time-out
    public static final long TIMEOUT = (RouteContextProcessorTest.SAFETY_TIMEOUT) + ((RouteContextProcessorTest.RandomSleepProcessor.MAX_PROCESS_TIME) - (RouteContextProcessorTest.RandomSleepProcessor.MIN_PROCESS_TIME));

    // Resequencer capacity
    public static final int CAPACITY = (RouteContextProcessorTest.SAFETY_CAPACITY) + ((int) (((RouteContextProcessorTest.CONCURRENCY) * (RouteContextProcessorTest.TIMEOUT)) / (RouteContextProcessorTest.RandomSleepProcessor.MIN_PROCESS_TIME)));

    private static final int NUMBER_OF_MESSAGES = 10000;

    @Test
    public void testForkAndJoin() throws InterruptedException {
        // enable the other test method for manual testing
    }

    /**
     * Simulation processor that sleeps a random time between MIN_PROCESS_TIME
     * and MAX_PROCESS_TIME milliseconds.
     */
    public static class RandomSleepProcessor implements Processor {
        public static final long MIN_PROCESS_TIME = 5;

        public static final long MAX_PROCESS_TIME = 50;

        @Override
        public void process(Exchange arg0) throws Exception {
            long processTime = ((long) ((RouteContextProcessorTest.RandomSleepProcessor.MIN_PROCESS_TIME) + ((Math.random()) * ((RouteContextProcessorTest.RandomSleepProcessor.MAX_PROCESS_TIME) - (RouteContextProcessorTest.RandomSleepProcessor.MIN_PROCESS_TIME)))));
            Thread.sleep(processTime);
        }
    }
}

