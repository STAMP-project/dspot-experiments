/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.query.lookup;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.inject.Injector;
import java.io.Closeable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;
import kafka.server.KafkaConfig;
import kafka.server.KafkaServer;
import org.I0Itec.zkclient.ZkClient;
import org.apache.curator.test.TestingServer;
import org.apache.druid.java.util.common.ISE;
import org.apache.druid.java.util.common.StringUtils;
import org.apache.druid.java.util.common.io.Closer;
import org.apache.druid.java.util.common.logger.Logger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 *
 */
public class TestKafkaExtractionCluster {
    private static final Logger log = new Logger(TestKafkaExtractionCluster.class);

    private static final String topicName = "testTopic";

    private static final Map<String, String> kafkaProperties = new HashMap<>();

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private final Closer closer = Closer.create();

    private KafkaServer kafkaServer;

    private KafkaConfig kafkaConfig;

    private TestingServer zkTestServer;

    private ZkClient zkClient;

    private Injector injector;

    private ObjectMapper mapper;

    private KafkaLookupExtractorFactory factory;

    @Test(timeout = 60000L)
    public void testSimpleRename() throws InterruptedException {
        final Properties kafkaProducerProperties = makeProducerProperties();
        final Producer<byte[], byte[]> producer = new Producer(new ProducerConfig(kafkaProducerProperties));
        closer.register(new Closeable() {
            @Override
            public void close() {
                producer.close();
            }
        });
        checkServer();
        assertUpdated(null, "foo");
        assertReverseUpdated(ImmutableList.of(), "foo");
        long events = factory.getCompletedEventCount();
        TestKafkaExtractionCluster.log.info("-------------------------     Sending foo bar     -------------------------------");
        producer.send(new kafka.producer.KeyedMessage(TestKafkaExtractionCluster.topicName, StringUtils.toUtf8("foo"), StringUtils.toUtf8("bar")));
        long start = System.currentTimeMillis();
        while (events == (factory.getCompletedEventCount())) {
            Thread.sleep(100);
            if ((System.currentTimeMillis()) > (start + 60000)) {
                throw new ISE("Took too long to update event");
            }
        } 
        TestKafkaExtractionCluster.log.info("-------------------------     Checking foo bar     -------------------------------");
        assertUpdated("bar", "foo");
        assertReverseUpdated(Collections.singletonList("foo"), "bar");
        assertUpdated(null, "baz");
        checkServer();
        events = factory.getCompletedEventCount();
        TestKafkaExtractionCluster.log.info("-------------------------     Sending baz bat     -------------------------------");
        producer.send(new kafka.producer.KeyedMessage(TestKafkaExtractionCluster.topicName, StringUtils.toUtf8("baz"), StringUtils.toUtf8("bat")));
        while (events == (factory.getCompletedEventCount())) {
            Thread.sleep(10);
            if ((System.currentTimeMillis()) > (start + 60000)) {
                throw new ISE("Took too long to update event");
            }
        } 
        TestKafkaExtractionCluster.log.info("-------------------------     Checking baz bat     -------------------------------");
        Assert.assertEquals("bat", factory.get().apply("baz"));
        Assert.assertEquals(Collections.singletonList("baz"), factory.get().unapply("bat"));
    }
}

