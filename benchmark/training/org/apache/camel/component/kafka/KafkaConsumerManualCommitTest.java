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
package org.apache.camel.component.kafka;


import KafkaConstants.LAST_RECORD_BEFORE_COMMIT;
import java.io.IOException;
import java.util.stream.StreamSupport;
import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Ignore;
import org.junit.Test;


@Ignore
public class KafkaConsumerManualCommitTest extends BaseEmbeddedKafkaTest {
    public static final String TOPIC = "test";

    @EndpointInject(uri = ("kafka:" + (KafkaConsumerManualCommitTest.TOPIC)) + "?groupId=group1&sessionTimeoutMs=30000&autoCommitEnable=false&allowManualCommit=true&interceptorClasses=org.apache.camel.component.kafka.MockConsumerInterceptor")
    private Endpoint from;

    @EndpointInject(uri = "mock:result")
    private MockEndpoint to;

    private KafkaProducer<String, String> producer;

    @Test
    public void kafkaManualCommit() throws IOException, InterruptedException {
        to.expectedMessageCount(5);
        to.expectedBodiesReceivedInAnyOrder("message-0", "message-1", "message-2", "message-3", "message-4");
        // The LAST_RECORD_BEFORE_COMMIT header should include a value as we use manual commit
        to.allMessages().header(LAST_RECORD_BEFORE_COMMIT).isNotNull();
        for (int k = 0; k < 5; k++) {
            String msg = "message-" + k;
            ProducerRecord<String, String> data = new ProducerRecord(KafkaConsumerManualCommitTest.TOPIC, "1", msg);
            producer.send(data);
        }
        to.assertIsSatisfied(3000);
        assertEquals(5, StreamSupport.stream(MockConsumerInterceptor.recordsCaptured.get(0).records(KafkaConsumerManualCommitTest.TOPIC).spliterator(), false).count());
    }
}

