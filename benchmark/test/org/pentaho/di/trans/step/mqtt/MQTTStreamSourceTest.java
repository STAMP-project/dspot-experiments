/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.trans.step.mqtt;


import MQTTClientBuilder.ClientFactory;
import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import junit.framework.TestCase;
import org.apache.activemq.broker.BrokerService;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsNot;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.streaming.api.StreamSource;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest(MQTTClientBuilder.class)
public class MQTTStreamSourceTest {
    int port;

    private BrokerService brokerService;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Mock
    MQTTConsumer mqttConsumer;

    @Mock
    MQTTConsumerMeta consumerMeta;

    @Mock
    LogChannelInterface logger;

    @Mock
    StepMeta stepMeta;

    @Test
    public void testMqttStreamSingleTopic() throws Exception {
        StreamSource<List<Object>> source = new MQTTStreamSource(consumerMeta, mqttConsumer);
        source.open();
        final String[] messages = new String[]{ "foo", "bar", "baz" };
        publish("mytopic", messages);
        List<List<Object>> rows = getQuickly(iterateSource(source.flowable().blockingIterable().iterator(), 3));
        MatcherAssert.assertThat(messagesToRows("mytopic", messages), CoreMatchers.equalTo(rows));
        source.close();
    }

    @Test
    public void testMultipleTopics() throws InterruptedException, MqttException {
        Mockito.when(consumerMeta.getTopics()).thenReturn(Arrays.asList("mytopic-1", "vermilion.minotaur", "nosuchtopic"));
        StreamSource<List<Object>> source = new MQTTStreamSource(consumerMeta, mqttConsumer);
        source.open();
        String[] topic1Messages = new String[]{ "foo", "bar", "baz" };
        publish("mytopic-1", topic1Messages);
        String[] topic2Messages = new String[]{ "chuntttttt", "usidor", "arnie" };
        publish("vermilion.minotaur", topic2Messages);
        List<List<Object>> rows = getQuickly(iterateSource(source.flowable().blockingIterable().iterator(), 6));
        List<List<Object>> expectedResults = ImmutableList.<List<Object>>builder().addAll(messagesToRows("mytopic-1", topic1Messages)).addAll(messagesToRows("vermilion.minotaur", topic2Messages)).build();
        // contains any order wan't working for me for some reason, this should be similar
        MatcherAssert.assertThat(expectedResults.size(), CoreMatchers.equalTo(rows.size()));
        rows.forEach(( row) -> TestCase.assertTrue(expectedResults.contains(row)));
        source.close();
    }

    @Test
    public void testServernameCheck() {
        // valid server:port
        StreamSource<List<Object>> source = new MQTTStreamSource(consumerMeta, mqttConsumer);
        source.open();
        source.close();
        Mockito.when(consumerMeta.getMqttServer()).thenReturn(("tcp:/127.0.0.1:" + (port)));
        // invalid tcp://server/port
        source.open();
        Mockito.verify(mqttConsumer).stopAll();
        Mockito.verify(mqttConsumer).logError("java.lang.IllegalArgumentException: MQTT Connection should be specified as servername:port");
    }

    @Test
    public void testClientIdNotReused() {
        MQTTStreamSource source1 = new MQTTStreamSource(consumerMeta, mqttConsumer);
        source1.open();
        MQTTStreamSource source2 = new MQTTStreamSource(consumerMeta, mqttConsumer);
        source2.open();
        MatcherAssert.assertThat(source1.mqttClient.getClientId(), IsNot.not(CoreMatchers.equalTo(source2.mqttClient.getClientId())));
        source1.close();
        source2.close();
    }

    @Test
    public void testMQTTOpenException() throws Exception {
        PowerMockito.mockStatic(MQTTClientBuilder.class);
        MQTTClientBuilder.ClientFactory clientFactory = Mockito.mock(ClientFactory.class);
        MqttClient mqttClient = Mockito.mock(MqttClient.class);
        MQTTClientBuilder builder = Mockito.spy(MQTTClientBuilder.class);
        MqttException mqttException = Mockito.mock(MqttException.class);
        Mockito.when(clientFactory.getClient(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(mqttClient);
        Mockito.when(mqttException.toString()).thenReturn("There is an error connecting");
        Mockito.doThrow(mqttException).when(builder).buildAndConnect();
        PowerMockito.when(MQTTClientBuilder.builder()).thenReturn(builder);
        MQTTStreamSource source = new MQTTStreamSource(consumerMeta, mqttConsumer);
        source.open();
        Mockito.verify(mqttConsumer).stopAll();
        Mockito.verify(mqttConsumer).logError("There is an error connecting");
    }

    @Test
    public void testMqttOnlyStopsOnce() {
        MQTTStreamSource source = new MQTTStreamSource(consumerMeta, mqttConsumer);
        source.open();
        source.close();
        source.close();
        Mockito.verify(mqttConsumer, Mockito.never()).logError(ArgumentMatchers.anyString());
    }
}

