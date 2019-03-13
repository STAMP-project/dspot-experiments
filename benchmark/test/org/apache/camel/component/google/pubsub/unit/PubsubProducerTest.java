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
package org.apache.camel.component.google.pubsub.unit;


import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.Producer;
import org.apache.camel.component.google.pubsub.GooglePubsubProducer;
import org.apache.camel.component.google.pubsub.PubsubTestSupport;
import org.junit.Test;


public class PubsubProducerTest extends PubsubTestSupport {
    private static final String TEST_TOPIC_NAME = "test-topic-name";

    @EndpointInject(uri = "google-pubsub:{{project.id}}:" + (PubsubProducerTest.TEST_TOPIC_NAME))
    private Endpoint to;

    @EndpointInject(uri = "direct:from")
    private Endpoint from;

    @Test
    public void testProducerConfiguration() throws Exception {
        // :1 indicates first of a component type in Camel context
        Endpoint endpoint = context.hasEndpoint(String.format("google-pubsub:%s:%s:1", PubsubTestSupport.PROJECT_ID, PubsubProducerTest.TEST_TOPIC_NAME));
        assertNotNull(String.format("Endpoint 'google-pubsub:%s:$s' is not found in Camel Context", PubsubTestSupport.PROJECT_ID, PubsubProducerTest.TEST_TOPIC_NAME), endpoint);
        Producer producer = endpoint.createProducer();
        assertTrue((producer instanceof GooglePubsubProducer));
    }
}

