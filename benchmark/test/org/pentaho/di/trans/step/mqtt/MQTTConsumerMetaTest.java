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


import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.StringObjectId;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepOption;
import org.pentaho.metastore.api.IMetaStore;


@RunWith(MockitoJUnitRunner.class)
public class MQTTConsumerMetaTest {
    private static Class PKG = MQTTConsumerMetaTest.class;

    @Mock
    private IMetaStore metastore;

    @Mock
    private Repository rep;

    private MQTTConsumerMeta meta = new MQTTConsumerMeta();

    @Test
    public void testLoadAndSave() throws KettleXMLException {
        MQTTConsumerMeta startingMeta = getTestMeta();
        metaMatchesTestMetaFields(MQTTConsumerMetaTest.fromXml(startingMeta.getXML()));
    }

    @Test
    public void testXmlHasAllFields() {
        String serverName = "some_cluster";
        meta.setDefault();
        meta.setMqttServer(serverName);
        ArrayList<String> topicList = new ArrayList<>();
        topicList.add("temperature");
        meta.setTopics(topicList);
        meta.setQos("1");
        meta.setUsername("testuser");
        meta.setPassword("test");
        meta.setUseSsl(true);
        meta.setTransformationPath("/home/pentaho/myKafkaTransformation.ktr");
        meta.setBatchSize("54321");
        meta.setBatchDuration("987");
        StepMeta stepMeta = new StepMeta();
        TransMeta transMeta = Mockito.mock(TransMeta.class);
        stepMeta.setParentTransMeta(transMeta);
        meta.setParentStepMeta(stepMeta);
        // tests serialization/deserialization round trip
        TestCase.assertTrue(meta.equals(MQTTConsumerMetaTest.fromXml(meta.getXML())));
    }

    @Test
    public void testReadsFromRepository() throws Exception {
        StringObjectId stepId = new StringObjectId("stepId");
        MQTTConsumerMeta testMeta = getTestMeta();
        String xml = testMeta.getXML();
        Mockito.when(rep.getStepAttributeString(stepId, "step-xml")).thenReturn(xml);
        meta.readRep(rep, metastore, stepId, Collections.emptyList());
        metaMatchesTestMetaFields(meta);
    }

    @Test
    public void testSavesToRepository() throws Exception {
        StringObjectId stepId = new StringObjectId("step1");
        StringObjectId transId = new StringObjectId("trans1");
        ArrayList<String> topicList = new ArrayList<>();
        topicList.add("temperature");
        MQTTConsumerMeta localMeta = getTestMeta2(topicList);
        localMeta.saveRep(rep, metastore, transId, stepId);
        Mockito.verify(rep).saveStepAttribute(transId, stepId, "step-xml", localMeta.getXML());
        Mockito.verifyNoMoreInteractions(rep);
    }

    @Test
    public void testSaveDefaultEmptyConnection() {
        MQTTConsumerMeta roundTrippedMeta = MQTTConsumerMetaTest.fromXml(meta.getXML());
        MatcherAssert.assertThat(roundTrippedMeta, CoreMatchers.equalTo(meta));
        TestCase.assertTrue(roundTrippedMeta.getMqttServer().isEmpty());
    }

    @Test
    public void testGetSslConfig() {
        meta.setDefault();
        meta.setTopics(Collections.singletonList("mytopic"));
        Map<String, String> config = meta.getSslConfig();
        TestCase.assertTrue(((config.size()) > 0));
        MatcherAssert.assertThat(meta.sslKeys.size(), CoreMatchers.equalTo(config.size()));
        TestCase.assertTrue(config.keySet().containsAll(meta.sslKeys));
        for (int i = 0; i < (meta.sslKeys.size()); i++) {
            MatcherAssert.assertThat(config.get(meta.sslKeys.get(i)), CoreMatchers.equalTo(meta.sslValues.get(i)));
        }
        MQTTConsumerMeta roundTrip = MQTTConsumerMetaTest.fromXml(meta.getXML());
        TestCase.assertTrue(meta.equals(roundTrip));
    }

    @Test
    public void testSetSslConfig() {
        meta.setDefault();
        meta.setTopics(Arrays.asList("foo", "bar", "bop"));
        Map<String, String> fakeConfig = ImmutableMap.of("key1", "value1", "key2", "value2", "key3", "value3", "key4", "value4");
        meta.setSslConfig(fakeConfig);
        MQTTConsumerMeta deserMeta = MQTTConsumerMetaTest.fromXml(meta.getXML());
        MatcherAssert.assertThat(fakeConfig, CoreMatchers.equalTo(deserMeta.getSslConfig()));
        TestCase.assertTrue(meta.equals(deserMeta));
    }

    @Test
    public void testRetrieveOptions() {
        List<String> keys = Arrays.asList(MQTTConstants.KEEP_ALIVE_INTERVAL, MQTTConstants.MAX_INFLIGHT, MQTTConstants.CONNECTION_TIMEOUT, MQTTConstants.CLEAN_SESSION, MQTTConstants.STORAGE_LEVEL, MQTTConstants.SERVER_URIS, MQTTConstants.MQTT_VERSION, MQTTConstants.AUTOMATIC_RECONNECT);
        MQTTConsumerMeta meta = new MQTTConsumerMeta();
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
    public void testCheckDefaults() {
        List<CheckResultInterface> remarks = new ArrayList<>();
        meta.check(remarks, null, null, null, null, null, null, new Variables(), null, null);
        Assert.assertEquals(0, remarks.size());
    }

    @Test
    public void testCheckFailAll() {
        List<CheckResultInterface> remarks = new ArrayList<>();
        meta.setKeepAliveInterval("asdf");
        meta.setMaxInflight("asdf");
        meta.setConnectionTimeout("asdf");
        meta.setCleanSession("asdf");
        meta.setAutomaticReconnect("adsf");
        meta.setMqttVersion("9");
        meta.check(remarks, null, null, null, null, null, null, new Variables(), null, null);
        Assert.assertEquals(6, remarks.size());
        Assert.assertEquals(BaseMessages.getString(MQTTConsumerMetaTest.PKG, "MQTTMeta.CheckResult.NotANumber", BaseMessages.getString(MQTTConsumerMetaTest.PKG, ("MQTTDialog.Options." + (MQTTConstants.KEEP_ALIVE_INTERVAL)))), remarks.get(0).getText());
        Assert.assertEquals(BaseMessages.getString(MQTTConsumerMetaTest.PKG, "MQTTMeta.CheckResult.NotANumber", BaseMessages.getString(MQTTConsumerMetaTest.PKG, ("MQTTDialog.Options." + (MQTTConstants.MAX_INFLIGHT)))), remarks.get(1).getText());
        Assert.assertEquals(BaseMessages.getString(MQTTConsumerMetaTest.PKG, "MQTTMeta.CheckResult.NotANumber", BaseMessages.getString(MQTTConsumerMetaTest.PKG, ("MQTTDialog.Options." + (MQTTConstants.CONNECTION_TIMEOUT)))), remarks.get(2).getText());
        Assert.assertEquals(BaseMessages.getString(MQTTConsumerMetaTest.PKG, "MQTTMeta.CheckResult.NotABoolean", BaseMessages.getString(MQTTConsumerMetaTest.PKG, ("MQTTDialog.Options." + (MQTTConstants.CLEAN_SESSION)))), remarks.get(3).getText());
        Assert.assertEquals(BaseMessages.getString(MQTTConsumerMetaTest.PKG, "MQTTMeta.CheckResult.NotCorrectVersion", BaseMessages.getString(MQTTConsumerMetaTest.PKG, ("MQTTDialog.Options." + (MQTTConstants.MQTT_VERSION)))), remarks.get(4).getText());
        Assert.assertEquals(BaseMessages.getString(MQTTConsumerMetaTest.PKG, "MQTTMeta.CheckResult.NotABoolean", BaseMessages.getString(MQTTConsumerMetaTest.PKG, ("MQTTDialog.Options." + (MQTTConstants.AUTOMATIC_RECONNECT)))), remarks.get(5).getText());
    }
}

