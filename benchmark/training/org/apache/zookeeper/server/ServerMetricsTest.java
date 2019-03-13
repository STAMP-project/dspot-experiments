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
package org.apache.zookeeper.server;


import org.apache.zookeeper.ZKTestCase;
import org.apache.zookeeper.server.metric.AvgMinMaxCounter;
import org.apache.zookeeper.server.metric.SimpleCounter;
import org.junit.Test;


public class ServerMetricsTest extends ZKTestCase {
    private static final int RANDOM_TRIALS = 100;

    private static final int RANDOM_SIZE = 100;

    @Test
    public void testAvgMinMaxCounter() {
        final AvgMinMaxCounter metric = new AvgMinMaxCounter("test");
        testAvgMinMaxCounter(metric, 0);
        testAvgMinMaxCounter(metric, 1);
        for (int i = 0; i < (ServerMetricsTest.RANDOM_TRIALS); ++i) {
            testAvgMinMaxCounter(metric, ServerMetricsTest.RANDOM_SIZE);
        }
    }

    @Test
    public void testSimpleCounter() {
        SimpleCounter metric = new SimpleCounter("test");
        testSimpleCounter(metric, 0);
        testSimpleCounter(metric, 1);
        for (int i = 0; i < (ServerMetricsTest.RANDOM_TRIALS); ++i) {
            testSimpleCounter(metric, ServerMetricsTest.RANDOM_SIZE);
        }
    }
}

