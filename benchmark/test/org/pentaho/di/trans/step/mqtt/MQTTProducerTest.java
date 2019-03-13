/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2018 by Hitachi Vantara : http://www.pentaho.com
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


import java.nio.charset.Charset;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.logging.LogChannelInterfaceFactory;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.step.StepMetaDataCombi;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest(MQTTClientBuilder.class)
public class MQTTProducerTest {
    @Mock
    MqttClient mqttClient;

    @Mock
    LogChannelInterfaceFactory logChannelFactory;

    @Mock
    LogChannelInterface logChannel;

    private Trans trans;

    @Test
    public void testSendRowToProducer() throws Exception {
        Mockito.when(mqttClient.isConnected()).thenReturn(true);
        handleAsSecondRow();
        Mockito.doAnswer(( invocation) -> {
            String topic = ((String) (invocation.getArguments()[0]));
            MqttMessage message = ((MqttMessage) (invocation.getArguments()[1]));
            Assert.assertEquals("TestWinning", topic);
            Assert.assertEquals(0, message.getQos());
            Assert.assertEquals("#winning", new String(message.getPayload(), Charset.forName("utf-8")));
            return null;
        }).when(mqttClient).publish(ArgumentMatchers.any(), ArgumentMatchers.any());
        trans.startThreads();
        trans.waitUntilFinished();
        Mockito.verify(mqttClient).disconnect();
        Assert.assertEquals(4, trans.getSteps().get(1).step.getLinesOutput());
    }

    @Test
    public void testInvalidQOS() throws Exception {
        trans.setVariable("qos", "hello");
        trans.prepareExecution(new String[]{  });
        // Need to set first = false again since prepareExecution was called with the new variable.
        StepMetaDataCombi combi = trans.getSteps().get(1);
        MQTTProducer step = ((MQTTProducer) (combi.step));
        step.first = false;
        trans.startThreads();
        trans.waitUntilFinished();
        Mockito.verify(logChannel).logError(ArgumentMatchers.eq("MQTT Producer - Quality of Service level hello is invalid. Please set a level of 0, 1, or 2"), ArgumentMatchers.any(IllegalArgumentException.class));
        Mockito.verify(mqttClient, Mockito.never()).publish(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void testMqttClientIsMemoized() throws Exception {
        PowerMockito.mockStatic(MQTTClientBuilder.class);
        MQTTClientBuilder clientBuilder = Mockito.spy(MQTTClientBuilder.class);
        MqttClient mqttClient = Mockito.mock(MqttClient.class);
        Mockito.doReturn(mqttClient).when(clientBuilder).buildAndConnect();
        PowerMockito.when(MQTTClientBuilder.builder()).thenReturn(clientBuilder);
        trans.startThreads();
        trans.waitUntilFinished();
        StepMetaDataCombi combi = trans.getSteps().get(1);
        MQTTProducer step = ((MQTTProducer) (combi.step));
        // verify repeated retrieval of client returns the *same* client.
        Assert.assertEquals(step.client.get(), step.client.get());
    }

    @Test
    public void testFeedbackSize() throws Exception {
        handleAsSecondRow();
        StepMetaDataCombi combi = trans.getSteps().get(1);
        MQTTProducer step = ((MQTTProducer) (combi.step));
        Mockito.when(logChannel.isBasic()).thenReturn(true);
        step.getTransMeta().setFeedbackSize(1);
        trans.startThreads();
        trans.waitUntilFinished();
        Mockito.verify(logChannel).logBasic(ArgumentMatchers.eq("Linenr1"));
    }

    @Test
    public void testMqttConnectException() throws Exception {
        PowerMockito.mockStatic(MQTTClientBuilder.class);
        MQTTClientBuilder clientBuilder = Mockito.spy(MQTTClientBuilder.class);
        MqttException mqttException = Mockito.mock(MqttException.class);
        Mockito.when(mqttException.toString()).thenReturn("There was an error connecting");
        Mockito.doThrow(mqttException).when(clientBuilder).buildAndConnect();
        PowerMockito.when(MQTTClientBuilder.builder()).thenReturn(clientBuilder);
        trans.startThreads();
        trans.waitUntilFinished();
        Mockito.verify(logChannel).logError(ArgumentMatchers.eq("There was an error connecting"), ArgumentMatchers.any(RuntimeException.class));
    }

    @Test
    public void testErrorOnPublishStopsAll() throws Exception {
        handleAsSecondRow();
        MqttException mqttException = Mockito.mock(MqttException.class);
        Mockito.when(mqttException.getMessage()).thenReturn("publish failed");
        Mockito.when(mqttClient.isConnected()).thenReturn(true, false);
        Mockito.doThrow(mqttException).when(mqttClient).publish(ArgumentMatchers.any(), ArgumentMatchers.any());
        trans.startThreads();
        trans.waitUntilFinished();
        Mockito.verify(mqttClient).disconnect();
        Mockito.verify(logChannel).logError(("MQTT Producer - Received an exception publishing the message." + "  Check that Quality of Service level 0 is supported by your MQTT Broker"));
        Mockito.verify(logChannel).logError("publish failed", mqttException);
        Assert.assertEquals(0, trans.getSteps().get(1).step.getLinesOutput());
    }
}

