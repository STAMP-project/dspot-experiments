/**
 * Copyright 2014-2016 CyberVision, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaaproject.kaa.client.configuration.manager;


import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.client.common.CommonRecord;
import org.kaaproject.kaa.client.common.CommonValue;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class DefaultConfigurationManagerTest {
    @Test
    public void testComplexDelta() throws IOException {
        DefaultConfigurationManager manager = new DefaultConfigurationManager();
        ConfigurationReceiver receiver = Mockito.mock(ConfigurationReceiver.class);
        manager.subscribeForConfigurationUpdates(receiver);
        URL schemaUrl = Thread.currentThread().getContextClassLoader().getResource("configuration/manager/complexFieldsDeltaSchema.json");
        Schema schema = new Schema.Parser().parse(new File(schemaUrl.getPath()));
        // First full resync delta
        GenericRecord delta = new org.apache.avro.generic.GenericData.Record(DefaultConfigurationManagerTest.getDeltaSchemaByFullName(schema, "org.kaa.config.testT"));
        DefaultConfigurationManagerTest.fillComplexFullResyncDelta(delta);
        manager.onDeltaReceived(0, delta, true);
        CommonRecord rootConfig = manager.getConfiguration();
        manager.onConfigurationProcessed();
        Assert.assertTrue(rootConfig.hasField("testField1"));
        Assert.assertEquals("abc", rootConfig.getField("testField1").getString());
        Assert.assertTrue(rootConfig.getField("testField1").isString());
        Assert.assertFalse(rootConfig.getField("testField1").isInteger());
        Assert.assertTrue(rootConfig.hasField("testField2"));
        Assert.assertTrue(rootConfig.getField("testField2").isRecord());
        Assert.assertTrue(rootConfig.getField("testField2").getRecord().getField("testField3").isInteger());
        Assert.assertEquals(new Integer(456), rootConfig.getField("testField2").getRecord().getField("testField3").getInteger());
        // Partial update delta
        DefaultConfigurationManagerTest.fillComplexPartialDelta(delta);
        manager.onDeltaReceived(0, delta, false);
        rootConfig = manager.getConfiguration();
        manager.onConfigurationProcessed();
        Mockito.verify(receiver, Mockito.times(2)).onConfigurationUpdated(ArgumentMatchers.any(CommonRecord.class));
        Assert.assertTrue(rootConfig.hasField("testField1"));
        Assert.assertEquals("abc", rootConfig.getField("testField1").getString());
        Assert.assertTrue(rootConfig.getField("testField1").isString());
        Assert.assertTrue(rootConfig.hasField("testField2"));
        Assert.assertTrue(rootConfig.getField("testField2").isRecord());
        Assert.assertTrue(rootConfig.getField("testField2").getRecord().getField("testField3").isInteger());
        Assert.assertEquals(new Integer(654), rootConfig.getField("testField2").getRecord().getField("testField3").getInteger());
    }

    @Test
    public void testArrayFieldsDelta() throws IOException, URISyntaxException {
        DefaultConfigurationManager manager = new DefaultConfigurationManager();
        ConfigurationReceiver receiver = Mockito.mock(ConfigurationReceiver.class);
        manager.subscribeForConfigurationUpdates(receiver);
        URL schemaUrl = Thread.currentThread().getContextClassLoader().getResource("configuration/manager/arrayFieldsDeltaSchema.json");
        Schema schema = new Schema.Parser().parse(new File(schemaUrl.getPath()));
        // First full resync delta
        GenericRecord delta = new org.apache.avro.generic.GenericData.Record(DefaultConfigurationManagerTest.getDeltaSchemaByFullName(schema, "org.kaa.config.testT"));
        DefaultConfigurationManagerTest.fillArrayFullResyncDelta(delta);
        manager.onDeltaReceived(0, delta, true);
        manager.onConfigurationProcessed();
        CommonRecord rootConfig = manager.getConfiguration();
        List<CommonValue> configArray = rootConfig.getField("testField1").getArray().getList();
        Assert.assertTrue(rootConfig.getField("testField1").isArray());
        Assert.assertEquals(3, configArray.size());
        Assert.assertEquals(new Integer(1), configArray.get(0).getRecord().getField("testField2").getInteger());
        Assert.assertEquals(new Integer(2), configArray.get(1).getRecord().getField("testField2").getInteger());
        Assert.assertEquals(new Integer(3), configArray.get(2).getRecord().getField("testField2").getInteger());
        // Partial update of the item
        GenericRecord itemRecord2 = new org.apache.avro.generic.GenericData.Record(DefaultConfigurationManagerTest.getDeltaSchemaByFullName(schema, "org.kaa.config.testRecordItemT"));
        DefaultConfigurationManagerTest.fillArrayItemUpdateDelta(itemRecord2);
        manager.onDeltaReceived(1, itemRecord2, false);
        manager.onConfigurationProcessed();
        rootConfig = manager.getConfiguration();
        configArray = rootConfig.getField("testField1").getArray().getList();
        Assert.assertEquals(new Integer(1), configArray.get(0).getRecord().getField("testField2").getInteger());
        Assert.assertEquals(new Integer(22), configArray.get(1).getRecord().getField("testField2").getInteger());
        Assert.assertEquals(new Integer(3), configArray.get(2).getRecord().getField("testField2").getInteger());
        // Removing one item by uuid
        DefaultConfigurationManagerTest.fillArrayItemRemoveDelta(delta);
        manager.onDeltaReceived(0, delta, false);
        manager.onConfigurationProcessed();
        rootConfig = manager.getConfiguration();
        configArray = rootConfig.getField("testField1").getArray().getList();
        Assert.assertEquals(2, configArray.size());
        Assert.assertEquals(new Integer(22), configArray.get(0).getRecord().getField("testField2").getInteger());
        Assert.assertEquals(new Integer(3), configArray.get(1).getRecord().getField("testField2").getInteger());
        // Reseting container
        DefaultConfigurationManagerTest.fillArrayResetDelta(delta);
        manager.onDeltaReceived(0, delta, false);
        manager.onConfigurationProcessed();
        rootConfig = manager.getConfiguration();
        configArray = rootConfig.getField("testField1").getArray().getList();
        Assert.assertTrue(configArray.isEmpty());
        Mockito.verify(receiver, Mockito.times(4)).onConfigurationUpdated(ArgumentMatchers.any(CommonRecord.class));
    }
}

