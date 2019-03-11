/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.kafka;


import GetKafka.AUTO_OFFSET_RESET;
import GetKafka.BATCH_SIZE;
import GetKafka.REL_SUCCESS;
import GetKafka.SMALLEST;
import GetKafka.TOPIC;
import GetKafka.ZOOKEEPER_CONNECTION_STRING;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.apache.nifi.processors.kafka.test.EmbeddedKafka;
import org.apache.nifi.processors.kafka.test.EmbeddedKafkaProducerHelper;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


// The test is valid and should be ran when working on this module. @Ignore is
// to speed up the overall build
@Ignore
public class GetKafkaIntegrationTests {
    private static EmbeddedKafka kafkaLocal;

    private static EmbeddedKafkaProducerHelper producerHelper;

    /**
     * Will set auto-offset to 'smallest' to ensure that all events (the once
     * that were sent before and after consumer startup) are received.
     */
    @Test
    public void testGetAllMessages() throws Exception {
        String topicName = "testGetAllMessages";
        GetKafka getKafka = new GetKafka();
        final TestRunner runner = TestRunners.newTestRunner(getKafka);
        runner.setProperty(ZOOKEEPER_CONNECTION_STRING, ("localhost:" + (GetKafkaIntegrationTests.kafkaLocal.getZookeeperPort())));
        runner.setProperty(TOPIC, topicName);
        runner.setProperty(BATCH_SIZE, "5");
        runner.setProperty(AUTO_OFFSET_RESET, SMALLEST);
        runner.setProperty("consumer.timeout.ms", "300");
        GetKafkaIntegrationTests.producerHelper.sendEvent(topicName, "Hello-1");
        GetKafkaIntegrationTests.producerHelper.sendEvent(topicName, "Hello-2");
        GetKafkaIntegrationTests.producerHelper.sendEvent(topicName, "Hello-3");
        final CountDownLatch latch = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    runner.run(20, false);
                } finally {
                    latch.countDown();
                }
            }
        }).start();
        // Thread.sleep(1000);
        GetKafkaIntegrationTests.producerHelper.sendEvent(topicName, "Hello-4");
        GetKafkaIntegrationTests.producerHelper.sendEvent(topicName, "Hello-5");
        GetKafkaIntegrationTests.producerHelper.sendEvent(topicName, "Hello-6");
        latch.await();
        final List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        // must be two since we sent 6 messages with batch of 5
        Assert.assertEquals(2, flowFiles.size());
        MockFlowFile flowFile = flowFiles.get(0);
        String[] events = new String(flowFile.toByteArray()).split("\\s+");
        Assert.assertEquals(5, events.length);
        // spot check
        Assert.assertEquals("Hello-1", events[0]);
        Assert.assertEquals("Hello-4", events[3]);
        flowFile = flowFiles.get(1);
        events = new String(flowFile.toByteArray()).split("\\s+");
        Assert.assertEquals(1, events.length);
        getKafka.shutdownConsumer();
    }

    /**
     * Based on auto-offset set to 'largest' events sent before consumer start
     * should not be consumed.
     */
    @Test
    public void testGetOnlyMessagesAfterConsumerStartup() throws Exception {
        String topicName = "testGetOnlyMessagesAfterConsumerStartup";
        GetKafka getKafka = new GetKafka();
        final TestRunner runner = TestRunners.newTestRunner(getKafka);
        runner.setProperty(ZOOKEEPER_CONNECTION_STRING, ("localhost:" + (GetKafkaIntegrationTests.kafkaLocal.getZookeeperPort())));
        runner.setProperty(TOPIC, topicName);
        runner.setProperty(BATCH_SIZE, "5");
        runner.setProperty("consumer.timeout.ms", "300");
        GetKafkaIntegrationTests.producerHelper.sendEvent(topicName, "Hello-1");
        GetKafkaIntegrationTests.producerHelper.sendEvent(topicName, "Hello-2");
        GetKafkaIntegrationTests.producerHelper.sendEvent(topicName, "Hello-3");
        final CountDownLatch latch = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    runner.run(20, false);
                } finally {
                    latch.countDown();
                }
            }
        }).start();
        latch.await();
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(0, flowFiles.size());
        GetKafkaIntegrationTests.producerHelper.sendEvent(topicName, "Hello-4");
        GetKafkaIntegrationTests.producerHelper.sendEvent(topicName, "Hello-5");
        GetKafkaIntegrationTests.producerHelper.sendEvent(topicName, "Hello-6");
        latch.await();
        runner.run(5, false);
        flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        // must be single since we should only be receiving 4,5 and 6 in batch
        // of 5
        Assert.assertEquals(1, flowFiles.size());
        MockFlowFile flowFile = flowFiles.get(0);
        String[] events = new String(flowFile.toByteArray()).split("\\s+");
        Assert.assertEquals(3, events.length);
        Assert.assertEquals("Hello-4", events[0]);
        Assert.assertEquals("Hello-5", events[1]);
        Assert.assertEquals("Hello-6", events[2]);
        getKafka.shutdownConsumer();
    }
}

