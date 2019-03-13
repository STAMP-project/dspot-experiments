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
package org.kaaproject.kaa.client.configuration;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.junit.Test;
import org.kaaproject.kaa.client.configuration.manager.DefaultConfigurationManagerTest;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class DefaultConfigurationProcessorTest {
    @Test
    public void testUpdates() throws IOException {
        GenericDeltaReceiver receiver = Mockito.mock(GenericDeltaReceiver.class);
        DefaultConfigurationProcessor processor = new DefaultConfigurationProcessor();
        processor.subscribeForUpdates(receiver);
        processor.subscribeForUpdates(receiver);
        processor.subscribeForUpdates(null);
        URL schemaUrl = Thread.currentThread().getContextClassLoader().getResource("configuration/manager/complexFieldsDeltaSchema.json");
        Schema schema = new Schema.Parser().parse(new File(schemaUrl.getPath()));
        processor.onSchemaUpdated(schema);
        processor.onSchemaUpdated(null);
        GenericRecord delta = new org.apache.avro.generic.GenericData.Record(DefaultConfigurationManagerTest.getDeltaSchemaByFullName(schema, "org.kaa.config.testT"));
        byte[] rawData = DefaultConfigurationProcessorTest.getSerializedDelta(schema, delta);
        processor.processConfigurationData(ByteBuffer.wrap(rawData), true);
        processor.processConfigurationData(null, true);
        processor.unsubscribeFromUpdates(receiver);
        processor.unsubscribeFromUpdates(null);
        processor.processConfigurationData(ByteBuffer.wrap(rawData), true);
        Mockito.verify(receiver, Mockito.times(1)).onDeltaReceived(ArgumentMatchers.eq(0), ArgumentMatchers.eq(delta), ArgumentMatchers.eq(true));
    }

    @Test
    public void testOnProcessedCallback() throws IOException {
        ConfigurationProcessedObserver callback = Mockito.mock(ConfigurationProcessedObserver.class);
        DefaultConfigurationProcessor processor = new DefaultConfigurationProcessor();
        processor.addOnProcessedCallback(callback);
        processor.addOnProcessedCallback(callback);
        processor.addOnProcessedCallback(null);
        URL schemaUrl = Thread.currentThread().getContextClassLoader().getResource("configuration/manager/complexFieldsDeltaSchema.json");
        Schema schema = new Schema.Parser().parse(new File(schemaUrl.getPath()));
        processor.onSchemaUpdated(schema);
        GenericRecord delta = new org.apache.avro.generic.GenericData.Record(DefaultConfigurationManagerTest.getDeltaSchemaByFullName(schema, "org.kaa.config.testT"));
        byte[] rawData = DefaultConfigurationProcessorTest.getSerializedDelta(schema, delta);
        processor.processConfigurationData(ByteBuffer.wrap(rawData), true);
        processor.processConfigurationData(null, true);
        processor.removeOnProcessedCallback(callback);
        processor.removeOnProcessedCallback(null);
        processor.processConfigurationData(ByteBuffer.wrap(rawData), true);
        Mockito.verify(callback, Mockito.times(1)).onConfigurationProcessed();
    }

    @Test(expected = ConfigurationRuntimeException.class)
    public void testUpdatesWithNullSchema() throws IOException {
        DefaultConfigurationProcessor processor = new DefaultConfigurationProcessor();
        URL schemaUrl = Thread.currentThread().getContextClassLoader().getResource("configuration/manager/complexFieldsDeltaSchema.json");
        Schema schema = new Schema.Parser().parse(new File(schemaUrl.getPath()));
        GenericRecord delta = new org.apache.avro.generic.GenericData.Record(DefaultConfigurationManagerTest.getDeltaSchemaByFullName(schema, "org.kaa.config.testT"));
        byte[] rawData = DefaultConfigurationProcessorTest.getSerializedDelta(schema, delta);
        processor.processConfigurationData(ByteBuffer.wrap(rawData), true);
    }
}

