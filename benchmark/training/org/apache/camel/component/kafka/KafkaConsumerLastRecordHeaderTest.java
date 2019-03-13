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
import java.util.List;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.junit.Test;


public class KafkaConsumerLastRecordHeaderTest extends BaseEmbeddedKafkaTest {
    private static final String TOPIC = "last-record";

    @EndpointInject(uri = "mock:result")
    private MockEndpoint result;

    private KafkaProducer<String, String> producer;

    /**
     * When consuming data with autoCommitEnable=false
     * Then the LAST_RECORD_BEFORE_COMMIT header must be always defined
     * And it should be true only for the last one
     */
    @Test
    public void shouldStartFromBeginningWithEmptyOffsetRepository() throws InterruptedException {
        result.expectedMessageCount(5);
        result.expectedBodiesReceived("message-0", "message-1", "message-2", "message-3", "message-4");
        for (int i = 0; i < 5; i++) {
            producer.send(new org.apache.kafka.clients.producer.ProducerRecord(KafkaConsumerLastRecordHeaderTest.TOPIC, "1", ("message-" + i)));
        }
        result.assertIsSatisfied(3000);
        List<Exchange> exchanges = result.getExchanges();
        for (int i = 0; i < (exchanges.size()); i++) {
            Boolean header = exchanges.get(i).getIn().getHeader(LAST_RECORD_BEFORE_COMMIT, Boolean.class);
            assertNotNull(("Header not set for #" + i), header);
            assertEquals(("Header invalid for #" + i), header, (i == ((exchanges.size()) - 1)));
        }
    }
}

