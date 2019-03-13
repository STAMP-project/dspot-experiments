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


import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.StringObjectId;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepOption;
import org.pentaho.metastore.api.IMetaStore;


@RunWith(MockitoJUnitRunner.class)
public class MQTTProducerMetaTest {
    private static Class PKG = MQTTProducerMetaTest.class;

    @Mock
    private IMetaStore metaStore;

    @Mock
    private Repository rep;

    @Test
    public void testLoadAndSave() {
        MQTTProducerMeta fromMeta = testMeta();
        MQTTProducerMeta toMeta = MQTTProducerMetaTest.fromXml(fromMeta.getXML());
        Assert.assertEquals("mqtthost:1883", toMeta.mqttServer);
        Assert.assertEquals("client1", toMeta.clientId);
        Assert.assertEquals("test-topic", toMeta.topic);
        Assert.assertEquals("field-topic", toMeta.fieldTopic);
        Assert.assertEquals("1", toMeta.qos);
        Assert.assertEquals("tempvalue", toMeta.messageField);
        Assert.assertEquals("testuser", toMeta.username);
        Assert.assertEquals("test", toMeta.password);
        Assert.assertEquals("1000", toMeta.keepAliveInterval);
        Assert.assertEquals("2000", toMeta.maxInflight);
        Assert.assertEquals("3000", toMeta.connectionTimeout);
        Assert.assertEquals("true", toMeta.cleanSession);
        Assert.assertEquals("/Users/noname/temp", toMeta.storageLevel);
        Assert.assertEquals("mqttHost2:1883", toMeta.serverUris);
        Assert.assertEquals("3", toMeta.mqttVersion);
        Assert.assertEquals("true", toMeta.automaticReconnect);
        Assert.assertThat(toMeta, CoreMatchers.equalTo(fromMeta));
    }

    @Test
    public void testFieldsArePreserved() {
        MQTTProducerMeta meta = new MQTTProducerMeta();
        meta.mqttServer = "mqtthost:1883";
        meta.clientId = "client1";
        meta.topic = "test-topic";
        meta.fieldTopic = "field-topic";
        meta.qos = "2";
        meta.messageField = "temp-message";
        meta.username = "testuser";
        meta.password = "test";
        MQTTProducerMeta toMeta = MQTTProducerMetaTest.fromXml(meta.getXML());
        Assert.assertThat(toMeta, CoreMatchers.equalTo(meta));
    }

    @Test
    public void testRoundTripWithSSLStuff() {
        MQTTProducerMeta meta = new MQTTProducerMeta();
        meta.mqttServer = "mqtthost:1883";
        meta.topic = "test-topic";
        meta.qos = "2";
        meta.messageField = "temp-message";
        meta.setSslConfig(ImmutableMap.of("sslKey", "sslVal", "sslKey2", "sslVal2", "sslKey3", "sslVal3"));
        meta.useSsl = true;
        MQTTProducerMeta rehydrated = MQTTProducerMetaTest.fromXml(meta.getXML());
        Assert.assertThat(true, Is.is(rehydrated.useSsl));
        meta.getSslConfig().keySet().forEach(( key) -> assertThat(meta.getSslConfig().get(key), is(rehydrated.getSslConfig().get(key))));
    }

    @Test
    public void testReadFromRepository() throws Exception {
        MQTTProducerMeta testMeta = testMeta();
        testMeta.automaticReconnect = "true";
        testMeta.serverUris = "mqttHost2:1883";
        testMeta.mqttServer = "mqttserver:1883";
        StringObjectId stepId = new StringObjectId("stepId");
        String xml = testMeta.getXML();
        Mockito.when(rep.getStepAttributeString(stepId, "step-xml")).thenReturn(xml);
        MQTTProducerMeta meta = new MQTTProducerMeta();
        meta.readRep(rep, metaStore, stepId, Collections.emptyList());
        meta.readRep(rep, metaStore, stepId, Collections.emptyList());
        Assert.assertEquals("mqttserver:1883", meta.mqttServer);
        Assert.assertEquals("client1", meta.clientId);
        Assert.assertEquals("test-topic", meta.topic);
        Assert.assertEquals("1", meta.qos);
        Assert.assertEquals("tempvalue", meta.messageField);
        Assert.assertEquals("testuser", meta.username);
        Assert.assertEquals("test", meta.password);
        Assert.assertEquals("1000", meta.keepAliveInterval);
        Assert.assertEquals("2000", meta.maxInflight);
        Assert.assertEquals("3000", meta.connectionTimeout);
        Assert.assertEquals("true", meta.cleanSession);
        Assert.assertEquals("/Users/noname/temp", meta.storageLevel);
        Assert.assertEquals("mqttHost2:1883", meta.serverUris);
        Assert.assertEquals("3", meta.mqttVersion);
        Assert.assertEquals("true", meta.automaticReconnect);
    }

    @Test
    public void testSavingToRepository() throws Exception {
        StringObjectId stepId = new StringObjectId("step1");
        StringObjectId transId = new StringObjectId("trans1");
        MQTTProducerMeta localMeta = testMeta();
        localMeta.topic = "weather";
        localMeta.saveRep(rep, metaStore, transId, stepId);
        Mockito.verify(rep).saveStepAttribute(transId, stepId, "step-xml", localMeta.getXML());
        Mockito.verifyNoMoreInteractions(rep);
    }

    @Test
    public void testSaveDefaultEmpty() throws KettleException {
        MQTTProducerMeta defaultMeta = new MQTTProducerMeta();
        defaultMeta.setDefault();
        MQTTProducerMeta toMeta = new MQTTProducerMeta();
        toMeta.mqttServer = "something that's not default";
        // loadXML into toMeta should overwrite the non-default val.
        toMeta.loadXML(MQTTProducerMetaTest.getNode(defaultMeta.getXML()), Collections.emptyList(), metaStore);
        Assert.assertEquals(toMeta, defaultMeta);
        Assert.assertThat(toMeta.mqttServer, Is.is(""));
    }

    @Test
    public void testRetrieveOptions() {
        List<String> keys = Arrays.asList(MQTTConstants.KEEP_ALIVE_INTERVAL, MQTTConstants.MAX_INFLIGHT, MQTTConstants.CONNECTION_TIMEOUT, MQTTConstants.CLEAN_SESSION, MQTTConstants.STORAGE_LEVEL, MQTTConstants.SERVER_URIS, MQTTConstants.MQTT_VERSION, MQTTConstants.AUTOMATIC_RECONNECT);
        MQTTProducerMeta meta = new MQTTProducerMeta();
        meta.setDefault();
        List<StepOption> options = meta.retrieveOptions();
        Assert.assertEquals(8, options.size());
        for (StepOption option : options) {
            Assert.assertEquals("", option.getValue());
            Assert.assertNotNull(option.getText());
            Assert.assertTrue(keys.contains(option.getKey()));
        }
    }

    @Test
    public void testCheckOptions() {
        List<CheckResultInterface> remarks = new ArrayList<>();
        MQTTProducerMeta meta = new MQTTProducerMeta();
        meta.mqttServer = "theserver:1883";
        meta.clientId = "client100";
        meta.topic = "newtopic";
        meta.qos = "2";
        meta.messageField = "Messages";
        meta.username = "testuser";
        meta.password = "test";
        meta.keepAliveInterval = "1000";
        meta.maxInflight = "2000";
        meta.connectionTimeout = "3000";
        meta.cleanSession = "true";
        meta.storageLevel = "/Users/noname/temp";
        meta.serverUris = "mqttHost2:1883";
        meta.mqttVersion = "3";
        meta.automaticReconnect = "true";
        meta.check(remarks, null, null, null, null, null, null, new Variables(), null, null);
        Assert.assertEquals(0, remarks.size());
    }

    @Test
    public void testCheckOptionsFail() {
        List<CheckResultInterface> remarks = new ArrayList<>();
        MQTTProducerMeta meta = new MQTTProducerMeta();
        meta.mqttServer = "theserver:1883";
        meta.clientId = "client100";
        meta.topic = "newtopic";
        meta.qos = "2";
        meta.messageField = "Messages";
        meta.username = "testuser";
        meta.keepAliveInterval = "asdf";
        meta.maxInflight = "asdf";
        meta.connectionTimeout = "asdf";
        meta.cleanSession = "asdf";
        meta.automaticReconnect = "adsf";
        meta.mqttVersion = "asdf";
        meta.check(remarks, null, null, null, null, null, null, new Variables(), null, null);
        Assert.assertEquals(6, remarks.size());
        Assert.assertTrue(remarks.get(0).getText().contains(BaseMessages.getString(MQTTProducerMetaTest.PKG, ("MQTTDialog.Options." + (MQTTConstants.KEEP_ALIVE_INTERVAL)))));
        Assert.assertTrue(remarks.get(1).getText().contains(BaseMessages.getString(MQTTProducerMetaTest.PKG, ("MQTTDialog.Options." + (MQTTConstants.MAX_INFLIGHT)))));
        Assert.assertTrue(remarks.get(2).getText().contains(BaseMessages.getString(MQTTProducerMetaTest.PKG, ("MQTTDialog.Options." + (MQTTConstants.CONNECTION_TIMEOUT)))));
        Assert.assertTrue(remarks.get(3).getText().contains(BaseMessages.getString(MQTTProducerMetaTest.PKG, ("MQTTDialog.Options." + (MQTTConstants.CLEAN_SESSION)))));
        Assert.assertTrue(remarks.get(4).getText().contains(BaseMessages.getString(MQTTProducerMetaTest.PKG, ("MQTTDialog.Options." + (MQTTConstants.MQTT_VERSION)))));
        Assert.assertTrue(remarks.get(5).getText().contains(BaseMessages.getString(MQTTProducerMetaTest.PKG, ("MQTTDialog.Options." + (MQTTConstants.AUTOMATIC_RECONNECT)))));
    }

    @Test
    public void testVarSubstitution() {
        MQTTProducerMeta mqttProducerMeta = new MQTTProducerMeta();
        mqttProducerMeta.mqttServer = "${server}";
        mqttProducerMeta.messageField = "${message}";
        mqttProducerMeta.topic = "${topic}";
        mqttProducerMeta.fieldTopic = "${fieldTopic}";
        mqttProducerMeta.setSslConfig(ImmutableMap.of("key1", "${val1}", "key2", "${val2}"));
        StepMeta stepMeta = new StepMeta("mqtt step", mqttProducerMeta);
        stepMeta.setParentTransMeta(new org.pentaho.di.trans.TransMeta(new Variables()));
        mqttProducerMeta.setParentStepMeta(stepMeta);
        VariableSpace variables = new Variables();
        variables.setVariable("server", "myserver");
        variables.setVariable("message", "mymessage");
        variables.setVariable("topic", "mytopic");
        variables.setVariable("fieldTopic", "myfieldtopic");
        variables.setVariable("val1", "sslVal1");
        variables.setVariable("val2", "sslVal2");
        MQTTProducerMeta substitutedMeta = ((MQTTProducerMeta) (mqttProducerMeta.withVariables(variables)));
        Assert.assertThat("myserver", CoreMatchers.equalTo(substitutedMeta.mqttServer));
        Assert.assertThat("mymessage", CoreMatchers.equalTo(substitutedMeta.messageField));
        Assert.assertThat("mytopic", CoreMatchers.equalTo(substitutedMeta.topic));
        Assert.assertThat("myfieldtopic", CoreMatchers.equalTo(substitutedMeta.fieldTopic));
        Assert.assertThat("sslVal1", CoreMatchers.equalTo(substitutedMeta.getSslConfig().get("key1")));
        Assert.assertThat("sslVal2", CoreMatchers.equalTo(substitutedMeta.getSslConfig().get("key2")));
    }
}

