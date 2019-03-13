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


import KafkaConstants.KEY;
import KafkaConstants.OFFSET;
import KafkaConstants.PARTITION;
import KafkaConstants.TIMESTAMP;
import KafkaConstants.TOPIC;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class KafkaEndpointTest {
    private KafkaEndpoint endpoint;

    @Mock
    private ConsumerRecord<String, String> mockRecord;

    @Mock
    private KafkaComponent mockKafkaComponent;

    @Test
    public void createKafkaExchangeShouldSetHeaders() {
        Mockito.when(mockRecord.key()).thenReturn("somekey");
        Mockito.when(mockRecord.topic()).thenReturn("topic");
        Mockito.when(mockRecord.partition()).thenReturn(4);
        Mockito.when(mockRecord.offset()).thenReturn(56L);
        Mockito.when(mockRecord.timestamp()).thenReturn(1518026587392L);
        Exchange exchange = endpoint.createKafkaExchange(mockRecord);
        Message inMessage = exchange.getIn();
        Assert.assertNotNull(inMessage);
        Assert.assertEquals("somekey", inMessage.getHeader(KEY));
        Assert.assertEquals("topic", inMessage.getHeader(TOPIC));
        Assert.assertEquals(4, inMessage.getHeader(PARTITION));
        Assert.assertEquals(56L, inMessage.getHeader(OFFSET));
        Assert.assertEquals(1518026587392L, inMessage.getHeader(TIMESTAMP));
    }

    @Test
    public void isSingletonShoudlReturnTrue() {
        Assert.assertTrue(endpoint.isSingleton());
    }
}

