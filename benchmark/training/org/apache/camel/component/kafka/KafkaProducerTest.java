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
import KafkaConstants.PARTITION_KEY;
import KafkaConstants.TOPIC;
import ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.camel.AsyncCallback;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.TypeConverter;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultHeadersMapFactory;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.errors.ApiException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class KafkaProducerTest {
    private KafkaProducer producer;

    private KafkaEndpoint endpoint;

    private KafkaEndpoint fromEndpoint;

    private TypeConverter converter = Mockito.mock(TypeConverter.class);

    private CamelContext context = Mockito.mock(CamelContext.class);

    private Exchange exchange = Mockito.mock(Exchange.class);

    private CamelContext camelContext = Mockito.mock(CamelContext.class);

    private Message in = new org.apache.camel.support.DefaultMessage(camelContext);

    private Message out = new org.apache.camel.support.DefaultMessage(camelContext);

    private AsyncCallback callback = Mockito.mock(AsyncCallback.class);

    @SuppressWarnings({ "unchecked" })
    public KafkaProducerTest() throws Exception {
        KafkaComponent kafka = new KafkaComponent(new DefaultCamelContext());
        kafka.setBrokers("broker1:1234,broker2:4567");
        endpoint = kafka.createEndpoint("kafka:sometopic", "sometopic", new HashMap());
        producer = new KafkaProducer(endpoint);
        fromEndpoint = kafka.createEndpoint("kafka:fromtopic", "fromtopic", new HashMap());
        RecordMetadata rm = new RecordMetadata(null, 0, 0, 0, new Long(0), 0, 0);
        Future future = Mockito.mock(Future.class);
        Mockito.when(future.get()).thenReturn(rm);
        org.apache.kafka.clients.producer.KafkaProducer kp = Mockito.mock(org.apache.kafka.clients.producer.KafkaProducer.class);
        Mockito.when(kp.send(ArgumentMatchers.any(ProducerRecord.class))).thenReturn(future);
        Mockito.when(exchange.getContext()).thenReturn(context);
        Mockito.when(context.getTypeConverter()).thenReturn(converter);
        Mockito.when(converter.tryConvertTo(String.class, exchange, null)).thenReturn(null);
        Mockito.when(camelContext.getHeadersMapFactory()).thenReturn(new DefaultHeadersMapFactory());
        producer.setKafkaProducer(kp);
        producer.setWorkerPool(Executors.newFixedThreadPool(1));
    }

    @Test
    public void testPropertyBuilder() throws Exception {
        Properties props = producer.getProps();
        Assert.assertEquals("broker1:1234,broker2:4567", props.getProperty(BOOTSTRAP_SERVERS_CONFIG));
    }

    @Test
    @SuppressWarnings({ "unchecked" })
    public void processSendsMessage() throws Exception {
        endpoint.getConfiguration().setTopic("sometopic");
        Mockito.when(exchange.getIn()).thenReturn(in);
        Mockito.when(exchange.getOut()).thenReturn(out);
        in.setHeader(PARTITION_KEY, 4);
        producer.process(exchange);
        Mockito.verify(producer.getKafkaProducer()).send(ArgumentMatchers.any(ProducerRecord.class));
        assertRecordMetadataExists();
    }

    @Test(expected = Exception.class)
    @SuppressWarnings({ "unchecked" })
    public void processSendsMessageWithException() throws Exception {
        endpoint.getConfiguration().setTopic("sometopic");
        // setup the exception here
        org.apache.kafka.clients.producer.KafkaProducer kp = producer.getKafkaProducer();
        Mockito.when(kp.send(ArgumentMatchers.any(ProducerRecord.class))).thenThrow(new ApiException());
        Mockito.when(exchange.getIn()).thenReturn(in);
        in.setHeader(PARTITION_KEY, 4);
        producer.process(exchange);
        assertRecordMetadataExists();
    }

    @Test
    public void processAsyncSendsMessage() throws Exception {
        endpoint.getConfiguration().setTopic("sometopic");
        Mockito.when(exchange.getIn()).thenReturn(in);
        Mockito.when(exchange.getOut()).thenReturn(out);
        in.setHeader(PARTITION_KEY, 4);
        producer.process(exchange, callback);
        ArgumentCaptor<Callback> callBackCaptor = ArgumentCaptor.forClass(Callback.class);
        Mockito.verify(producer.getKafkaProducer()).send(ArgumentMatchers.any(ProducerRecord.class), callBackCaptor.capture());
        Callback kafkaCallback = callBackCaptor.getValue();
        kafkaCallback.onCompletion(new RecordMetadata(null, 0, 0, 0, new Long(0), 0, 0), null);
        assertRecordMetadataExists();
    }

    @Test
    public void processAsyncSendsMessageWithException() throws Exception {
        endpoint.getConfiguration().setTopic("sometopic");
        Mockito.when(exchange.getIn()).thenReturn(in);
        Mockito.when(exchange.getOut()).thenReturn(out);
        // setup the exception here
        org.apache.kafka.clients.producer.KafkaProducer kp = producer.getKafkaProducer();
        Mockito.when(kp.send(ArgumentMatchers.any(ProducerRecord.class), ArgumentMatchers.any(Callback.class))).thenThrow(new ApiException());
        in.setHeader(PARTITION_KEY, 4);
        producer.process(exchange, callback);
        ArgumentCaptor<Callback> callBackCaptor = ArgumentCaptor.forClass(Callback.class);
        Mockito.verify(producer.getKafkaProducer()).send(ArgumentMatchers.any(ProducerRecord.class), callBackCaptor.capture());
        Mockito.verify(exchange).setException(ArgumentMatchers.isA(ApiException.class));
        Mockito.verify(callback).done(ArgumentMatchers.eq(true));
        Callback kafkaCallback = callBackCaptor.getValue();
        kafkaCallback.onCompletion(new RecordMetadata(null, 0, 0, 0, new Long(0), 0, 0), null);
        assertRecordMetadataExists();
    }

    @Test
    public void processSendsMessageWithTopicHeaderAndNoTopicInEndPoint() throws Exception {
        endpoint.getConfiguration().setTopic(null);
        Mockito.when(exchange.getIn()).thenReturn(in);
        in.setHeader(TOPIC, "anotherTopic");
        Mockito.when(exchange.getOut()).thenReturn(out);
        producer.process(exchange);
        verifySendMessage("anotherTopic");
        assertRecordMetadataExists();
    }

    @Test
    public void processSendsMessageWithTopicHeaderAndEndPoint() throws Exception {
        endpoint.getConfiguration().setTopic("sometopic");
        Mockito.when(exchange.getIn()).thenReturn(in);
        Mockito.when(exchange.getOut()).thenReturn(out);
        in.setHeader(PARTITION_KEY, 4);
        in.setHeader(TOPIC, "anotherTopic");
        in.setHeader(KEY, "someKey");
        producer.process(exchange);
        verifySendMessage(4, "anotherTopic", "someKey");
        assertRecordMetadataExists();
    }

    @Test
    public void processRequiresTopicInEndpointOrInHeader() throws Exception {
        endpoint.getConfiguration().setTopic(null);
        Mockito.when(exchange.getIn()).thenReturn(in);
        in.setHeader(PARTITION_KEY, 4);
        in.setHeader(KEY, "someKey");
        producer.process(exchange);
        verifySendMessage("sometopic", "someKey");
        assertRecordMetadataExists();
    }

    @Test
    public void processRequiresTopicInConfiguration() throws Exception {
        endpoint.getConfiguration().setTopic("configTopic");
        Mockito.when(exchange.getIn()).thenReturn(in);
        in.setHeader(PARTITION_KEY, 4);
        in.setHeader(KEY, "someKey");
        producer.process(exchange);
        verifySendMessage("configTopic", "someKey");
        assertRecordMetadataExists();
    }

    @Test
    public void processDoesNotRequirePartitionHeader() throws Exception {
        endpoint.getConfiguration().setTopic("sometopic");
        Mockito.when(exchange.getIn()).thenReturn(in);
        Mockito.when(exchange.getOut()).thenReturn(out);
        producer.process(exchange);
        assertRecordMetadataExists();
    }

    @Test
    public void processSendsMessageWithPartitionKeyHeader() throws Exception {
        endpoint.getConfiguration().setTopic("someTopic");
        Mockito.when(exchange.getIn()).thenReturn(in);
        Mockito.when(exchange.getOut()).thenReturn(out);
        in.setHeader(PARTITION_KEY, 4);
        in.setHeader(KEY, "someKey");
        producer.process(exchange);
        verifySendMessage(4, "someTopic", "someKey");
        assertRecordMetadataExists();
    }

    @Test
    public void processSendsMessageWithMessageKeyHeader() throws Exception {
        endpoint.getConfiguration().setTopic("someTopic");
        Mockito.when(exchange.getIn()).thenReturn(in);
        Mockito.when(exchange.getOut()).thenReturn(out);
        in.setHeader(KEY, "someKey");
        producer.process(exchange);
        verifySendMessage("someTopic", "someKey");
        assertRecordMetadataExists();
    }

    @Test
    public void processSendMessageWithBridgeEndpoint() throws Exception {
        endpoint.getConfiguration().setTopic("someTopic");
        endpoint.getConfiguration().setBridgeEndpoint(true);
        Mockito.when(exchange.getIn()).thenReturn(in);
        Mockito.when(exchange.getOut()).thenReturn(out);
        in.setHeader(TOPIC, "anotherTopic");
        in.setHeader(KEY, "someKey");
        in.setHeader(PARTITION_KEY, 4);
        producer.process(exchange);
        verifySendMessage(4, "someTopic", "someKey");
        assertRecordMetadataExists();
    }

    @Test
    public void processSendMessageWithCircularDetected() throws Exception {
        endpoint.getConfiguration().setTopic("sometopic");
        endpoint.getConfiguration().setCircularTopicDetection(true);
        Mockito.when(exchange.getIn()).thenReturn(in);
        Mockito.when(exchange.getOut()).thenReturn(out);
        Mockito.when(exchange.getFromEndpoint()).thenReturn(fromEndpoint);
        // this is the from topic that are from the fromEndpoint
        in.setHeader(TOPIC, "fromtopic");
        in.setHeader(KEY, "somekey");
        producer.process(exchange);
        verifySendMessage("sometopic", "somekey");
        assertRecordMetadataExists();
    }

    @Test
    public void processSendMessageWithNoCircularDetected() throws Exception {
        endpoint.getConfiguration().setTopic("sometopic");
        endpoint.getConfiguration().setCircularTopicDetection(false);
        Mockito.when(exchange.getIn()).thenReturn(in);
        Mockito.when(exchange.getOut()).thenReturn(out);
        Mockito.when(exchange.getFromEndpoint()).thenReturn(fromEndpoint);
        // this is the from topic that are from the fromEndpoint
        in.setHeader(TOPIC, "fromtopic");
        in.setHeader(KEY, "somekey");
        producer.process(exchange);
        // will end up sending back to itself at fromtopic
        verifySendMessage("fromtopic", "somekey");
        assertRecordMetadataExists();
    }

    // Message and Topic Name alone
    @Test
    public void processSendsMessageWithMessageTopicName() throws Exception {
        endpoint.getConfiguration().setTopic("someTopic");
        Mockito.when(exchange.getIn()).thenReturn(in);
        Mockito.when(exchange.getOut()).thenReturn(out);
        producer.process(exchange);
        verifySendMessage("someTopic");
        assertRecordMetadataExists();
    }
}

