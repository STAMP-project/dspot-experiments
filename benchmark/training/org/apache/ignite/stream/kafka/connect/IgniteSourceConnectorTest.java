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
package org.apache.ignite.stream.kafka.connect;


import IgniteSourceConstants.CACHE_FILTER_CLASS;
import java.util.Map;
import org.apache.ignite.Ignite;
import org.apache.ignite.stream.kafka.TestKafkaBroker;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.apache.kafka.common.utils.Utils;
import org.apache.kafka.connect.runtime.Herder;
import org.apache.kafka.connect.runtime.Worker;
import org.junit.Test;


/**
 * Tests for {@link IgniteSourceConnector}.
 */
public class IgniteSourceConnectorTest extends GridCommonAbstractTest {
    /**
     * Number of input messages.
     */
    private static final int EVENT_CNT = 100;

    /**
     * Cache name.
     */
    private static final String CACHE_NAME = "testCache";

    /**
     * Test topics created by connector.
     */
    private static final String[] TOPICS = new String[]{ "src-test1", "src-test2" };

    /**
     * Worker id.
     */
    private static final String WORKER_ID = "workerId";

    /**
     * Test Kafka broker.
     */
    private TestKafkaBroker kafkaBroker;

    /**
     * Worker to run tasks.
     */
    private Worker worker;

    /**
     * Workers' herder.
     */
    private Herder herder;

    /**
     * Ignite server node shared among tests.
     */
    private static Ignite grid;

    /**
     * Tests data flow from injecting data into grid and transferring it to Kafka cluster
     * without user-specified filter.
     *
     * @throws Exception
     * 		Thrown in case of the failure.
     */
    @Test
    public void testEventsInjectedIntoKafkaWithoutFilter() throws Exception {
        Map<String, String> srcProps = makeSourceProps(Utils.join(IgniteSourceConnectorTest.TOPICS, ","));
        srcProps.remove(CACHE_FILTER_CLASS);
        doTest(srcProps, false);
    }

    /**
     * Tests data flow from injecting data into grid and transferring it to Kafka cluster.
     *
     * @throws Exception
     * 		Thrown in case of the failure.
     */
    @Test
    public void testEventsInjectedIntoKafka() throws Exception {
        doTest(makeSourceProps(Utils.join(IgniteSourceConnectorTest.TOPICS, ",")), true);
    }
}

