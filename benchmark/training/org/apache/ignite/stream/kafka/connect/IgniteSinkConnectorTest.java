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


import IgniteSinkConstants.SINGLE_TUPLE_EXTRACTOR_CLASS;
import java.util.AbstractMap;
import java.util.Map;
import org.apache.ignite.Ignite;
import org.apache.ignite.stream.StreamSingleTupleExtractor;
import org.apache.ignite.stream.kafka.TestKafkaBroker;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.apache.kafka.common.utils.Utils;
import org.apache.kafka.connect.runtime.Herder;
import org.apache.kafka.connect.runtime.Worker;
import org.apache.kafka.connect.sink.SinkRecord;
import org.junit.Test;


/**
 * Tests for {@link IgniteSinkConnector}.
 */
public class IgniteSinkConnectorTest extends GridCommonAbstractTest {
    /**
     * Number of input messages.
     */
    private static final int EVENT_CNT = 10000;

    /**
     * Cache name.
     */
    private static final String CACHE_NAME = "testCache";

    /**
     * Test topics.
     */
    private static final String[] TOPICS = new String[]{ "sink-test1", "sink-test2" };

    /**
     * Kafka partition.
     */
    private static final int PARTITIONS = 3;

    /**
     * Kafka replication factor.
     */
    private static final int REPLICATION_FACTOR = 1;

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
     * Ignite server node.
     */
    private static Ignite grid;

    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testSinkPutsWithoutTransformation() throws Exception {
        Map<String, String> sinkProps = makeSinkProps(Utils.join(IgniteSinkConnectorTest.TOPICS, ","));
        sinkProps.remove(SINGLE_TUPLE_EXTRACTOR_CLASS);
        testSinkPuts(sinkProps, false);
    }

    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testSinkPutsWithTransformation() throws Exception {
        testSinkPuts(makeSinkProps(Utils.join(IgniteSinkConnectorTest.TOPICS, ",")), true);
    }

    /**
     * Test transformer.
     */
    static class TestExtractor implements StreamSingleTupleExtractor<SinkRecord, String, String> {
        /**
         * {@inheritDoc }
         */
        @Override
        public Map.Entry<String, String> extract(SinkRecord msg) {
            String[] parts = ((String) (msg.value())).split("_");
            return new AbstractMap.SimpleEntry<String, String>(parts[0], parts[1]);
        }
    }
}

