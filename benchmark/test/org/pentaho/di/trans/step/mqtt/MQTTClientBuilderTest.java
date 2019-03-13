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


import MQTTClientBuilder.ClientFactory;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.Properties;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.util.StringUtil;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;


@RunWith(MockitoJUnitRunner.class)
public class MQTTClientBuilderTest {
    @Mock
    LogChannelInterface logger;

    @Mock
    MqttCallback callback;

    @Mock
    ClientFactory factory;

    @Mock
    MqttClient client;

    @Mock
    StepInterface step;

    @Mock
    StepMeta meta;

    @Captor
    ArgumentCaptor<MqttConnectOptions> connectOptsCapture;

    @Captor
    ArgumentCaptor<String> clientIdCapture;

    VariableSpace space = new Variables();

    MQTTClientBuilder builder = MQTTClientBuilder.builder();

    @Test
    public void testValidBuilderParams() throws MqttException {
        MqttClient client = builder.withQos("2").withUsername("user").withPassword("pass").withIsSecure(true).withTopics(Collections.singletonList("SomeTopic")).withSslConfig(ImmutableMap.of("ssl.trustStore", "/some/path")).withCallback(callback).withKeepAliveInterval("1000").withMaxInflight("2000").withConnectionTimeout("3000").withCleanSession("true").withStorageLevel("/Users/NoName/Temp").withServerUris("127.0.0.1:3000").withMqttVersion("3").withAutomaticReconnect("false").buildAndConnect();
        Mockito.verify(client).setCallback(callback);
        Mockito.verify(factory).getClient(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(MemoryPersistence.class));
        Mockito.verify(client).connect(connectOptsCapture.capture());
        MqttConnectOptions opts = connectOptsCapture.getValue();
        MatcherAssert.assertThat(opts.getUserName(), CoreMatchers.equalTo("user"));
        MatcherAssert.assertThat(opts.getPassword(), CoreMatchers.equalTo("pass".toCharArray()));
        Properties props = opts.getSSLProperties();
        MatcherAssert.assertThat(props.size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(props.getProperty("com.ibm.ssl.trustStore"), CoreMatchers.equalTo("/some/path"));
        Assert.assertEquals(1000, opts.getKeepAliveInterval());
        Assert.assertEquals(2000, opts.getMaxInflight());
        Assert.assertEquals(3000, opts.getConnectionTimeout());
        Assert.assertEquals(true, opts.isCleanSession());
        Assert.assertArrayEquals(new String[]{ "ssl://127.0.0.1:3000" }, opts.getServerURIs());
        Assert.assertEquals(3, opts.getMqttVersion());
        Assert.assertEquals(false, opts.isAutomaticReconnect());
    }

    @Test
    public void testFailParsingQOSLevelNotInteger() {
        try {
            builder.withQos("hello").buildAndConnect();
            Assert.fail("Should of failed initialization.");
        } catch (Exception e) {
            // Initialization failed because QOS level isn't 0, 1 or 2
            Assert.assertTrue((e instanceof IllegalArgumentException));
        }
    }

    @Test
    public void testFailParsingQOSLevelNotInRange() {
        try {
            builder.withQos("72").buildAndConnect();
            Assert.fail("Should of failed initialization.");
        } catch (Exception e) {
            // Initialization failed because QOS level isn't 0, 1 or 2
            Assert.assertTrue((e instanceof IllegalArgumentException));
        }
    }

    @Test
    public void testEmptyClientId() throws MqttException {
        builder.withClientId("asdf").buildAndConnect();
        Assert.assertEquals("asdf", clientIdCapture.getValue());
        builder.withClientId(null).buildAndConnect();
        Assert.assertFalse(StringUtil.isEmpty(clientIdCapture.getValue()));
        Assert.assertNotEquals("asdf", clientIdCapture.getValue());
        builder.withClientId("").buildAndConnect();
        Assert.assertFalse(StringUtil.isEmpty(clientIdCapture.getValue()));
        Assert.assertNotEquals("asdf", clientIdCapture.getValue());
    }
}

