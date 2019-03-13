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
package org.apache.camel.component.rabbitmq;


import AMQP.BasicProperties;
import RabbitMQConstants.APP_ID;
import RabbitMQConstants.CLUSTERID;
import RabbitMQConstants.CONTENT_ENCODING;
import RabbitMQConstants.CONTENT_TYPE;
import RabbitMQConstants.CORRELATIONID;
import RabbitMQConstants.DELIVERY_MODE;
import RabbitMQConstants.EXPIRATION;
import RabbitMQConstants.MESSAGE_ID;
import RabbitMQConstants.PRIORITY;
import RabbitMQConstants.REPLY_TO;
import RabbitMQConstants.TIMESTAMP;
import RabbitMQConstants.TYPE;
import RabbitMQConstants.USERID;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Connection;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class RabbitMQProducerTest {
    private RabbitMQEndpoint endpoint = Mockito.mock(RabbitMQEndpoint.class);

    private Exchange exchange = Mockito.mock(Exchange.class);

    private Message message = new org.apache.camel.support.DefaultMessage(new DefaultCamelContext());

    private Connection conn = Mockito.mock(Connection.class);

    @Test
    public void testPropertiesUsesContentTypeHeader() throws IOException {
        RabbitMQProducer producer = new RabbitMQProducer(endpoint);
        message.setHeader(CONTENT_TYPE, "application/json");
        AMQP.BasicProperties props = producer.buildProperties(exchange).build();
        Assert.assertEquals("application/json", props.getContentType());
    }

    @Test
    public void testPropertiesUsesCorrelationHeader() throws IOException {
        RabbitMQProducer producer = new RabbitMQProducer(endpoint);
        message.setHeader(CORRELATIONID, "124544");
        AMQP.BasicProperties props = producer.buildProperties(exchange).build();
        Assert.assertEquals("124544", props.getCorrelationId());
    }

    @Test
    public void testPropertiesUsesUserIdHeader() throws IOException {
        RabbitMQProducer producer = new RabbitMQProducer(endpoint);
        message.setHeader(USERID, "abcd");
        AMQP.BasicProperties props = producer.buildProperties(exchange).build();
        Assert.assertEquals("abcd", props.getUserId());
    }

    @Test
    public void testPropertiesUsesMessageIdHeader() throws IOException {
        RabbitMQProducer producer = new RabbitMQProducer(endpoint);
        message.setHeader(MESSAGE_ID, "abvasweaqQQ");
        AMQP.BasicProperties props = producer.buildProperties(exchange).build();
        Assert.assertEquals("abvasweaqQQ", props.getMessageId());
    }

    @Test
    public void testPropertiesUsesDeliveryModeHeader() throws IOException {
        RabbitMQProducer producer = new RabbitMQProducer(endpoint);
        message.setHeader(DELIVERY_MODE, "444");
        AMQP.BasicProperties props = producer.buildProperties(exchange).build();
        Assert.assertEquals(444, props.getDeliveryMode().intValue());
    }

    @Test
    public void testPropertiesUsesClusterIdHeader() throws IOException {
        RabbitMQProducer producer = new RabbitMQProducer(endpoint);
        message.setHeader(CLUSTERID, "abtasg5r");
        AMQP.BasicProperties props = producer.buildProperties(exchange).build();
        Assert.assertEquals("abtasg5r", props.getClusterId());
    }

    @Test
    public void testPropertiesUsesReplyToHeader() throws IOException {
        RabbitMQProducer producer = new RabbitMQProducer(endpoint);
        message.setHeader(REPLY_TO, "bbbbdfgdfg");
        AMQP.BasicProperties props = producer.buildProperties(exchange).build();
        Assert.assertEquals("bbbbdfgdfg", props.getReplyTo());
    }

    @Test
    public void testPropertiesUsesPriorityHeader() throws IOException {
        RabbitMQProducer producer = new RabbitMQProducer(endpoint);
        message.setHeader(PRIORITY, "15");
        AMQP.BasicProperties props = producer.buildProperties(exchange).build();
        Assert.assertEquals(15, props.getPriority().intValue());
    }

    @Test
    public void testPropertiesUsesExpirationHeader() throws IOException {
        RabbitMQProducer producer = new RabbitMQProducer(endpoint);
        message.setHeader(EXPIRATION, "thursday");
        AMQP.BasicProperties props = producer.buildProperties(exchange).build();
        Assert.assertEquals("thursday", props.getExpiration());
    }

    @Test
    public void testPropertiesUsesTypeHeader() throws IOException {
        RabbitMQProducer producer = new RabbitMQProducer(endpoint);
        message.setHeader(TYPE, "sometype");
        AMQP.BasicProperties props = producer.buildProperties(exchange).build();
        Assert.assertEquals("sometype", props.getType());
    }

    @Test
    public void testPropertiesUsesContentEncodingHeader() throws IOException {
        RabbitMQProducer producer = new RabbitMQProducer(endpoint);
        message.setHeader(CONTENT_ENCODING, "qwergghdfdfgdfgg");
        AMQP.BasicProperties props = producer.buildProperties(exchange).build();
        Assert.assertEquals("qwergghdfdfgdfgg", props.getContentEncoding());
    }

    @Test
    public void testPropertiesAppIdHeader() throws IOException {
        RabbitMQProducer producer = new RabbitMQProducer(endpoint);
        message.setHeader(APP_ID, "qweeqwe");
        AMQP.BasicProperties props = producer.buildProperties(exchange).build();
        Assert.assertEquals("qweeqwe", props.getAppId());
    }

    @Test
    public void testPropertiesUsesTimestampHeaderAsLongValue() throws IOException {
        RabbitMQProducer producer = new RabbitMQProducer(endpoint);
        message.setHeader(TIMESTAMP, "12345123");
        AMQP.BasicProperties props = producer.buildProperties(exchange).build();
        Assert.assertEquals(12345123, props.getTimestamp().getTime());
    }

    @Test
    public void testPropertiesUsesTimestampHeaderAsDateValue() throws IOException {
        Date timestamp = new Date();
        RabbitMQProducer producer = new RabbitMQProducer(endpoint);
        message.setHeader(TIMESTAMP, timestamp);
        AMQP.BasicProperties props = producer.buildProperties(exchange).build();
        Assert.assertEquals(timestamp, props.getTimestamp());
    }

    @Test
    public void testPropertiesUsesCustomHeaders() throws IOException {
        RabbitMQProducer producer = new RabbitMQProducer(endpoint);
        Map<String, Object> customHeaders = new HashMap<>();
        customHeaders.put("stringHeader", "A string");
        customHeaders.put("bigDecimalHeader", new BigDecimal("12.34"));
        customHeaders.put("integerHeader", 42);
        customHeaders.put("doubleHeader", 42.24);
        customHeaders.put("booleanHeader", true);
        customHeaders.put("dateHeader", new Date(0));
        customHeaders.put("byteArrayHeader", "foo".getBytes());
        customHeaders.put("invalidHeader", new RabbitMQProducerTest.Something());
        message.setHeaders(customHeaders);
        AMQP.BasicProperties props = producer.buildProperties(exchange).build();
        Assert.assertEquals("A string", props.getHeaders().get("stringHeader"));
        Assert.assertEquals(new BigDecimal("12.34"), props.getHeaders().get("bigDecimalHeader"));
        Assert.assertEquals(42, props.getHeaders().get("integerHeader"));
        Assert.assertEquals(42.24, props.getHeaders().get("doubleHeader"));
        Assert.assertEquals(true, props.getHeaders().get("booleanHeader"));
        Assert.assertEquals(new Date(0), props.getHeaders().get("dateHeader"));
        Assert.assertArrayEquals("foo".getBytes(), ((byte[]) (props.getHeaders().get("byteArrayHeader"))));
        Assert.assertNull(props.getHeaders().get("invalidHeader"));
    }

    private static class Something {}
}

