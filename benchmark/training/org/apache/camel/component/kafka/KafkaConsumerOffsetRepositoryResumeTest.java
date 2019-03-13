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


import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.MemoryStateRepository;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.junit.Test;


public class KafkaConsumerOffsetRepositoryResumeTest extends BaseEmbeddedKafkaTest {
    private static final String TOPIC = "offset-resume";

    @EndpointInject(uri = "mock:result")
    private MockEndpoint result;

    private KafkaProducer<String, String> producer;

    private MemoryStateRepository stateRepository;

    /**
     * Given an offset repository with values
     * When consuming with this repository
     * Then we're consuming from the saved offsets
     */
    @Test
    public void shouldResumeFromAnyParticularOffset() throws InterruptedException {
        result.expectedMessageCount(3);
        result.expectedBodiesReceivedInAnyOrder("message-6", "message-8", "message-9");
        result.assertIsSatisfied(3000);
        assertEquals("partition-0", "4", stateRepository.getState(((KafkaConsumerOffsetRepositoryResumeTest.TOPIC) + "/0")));
        assertEquals("partition-1", "4", stateRepository.getState(((KafkaConsumerOffsetRepositoryResumeTest.TOPIC) + "/1")));
    }
}

