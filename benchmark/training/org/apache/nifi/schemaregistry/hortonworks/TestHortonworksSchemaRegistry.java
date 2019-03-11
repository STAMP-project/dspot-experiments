/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.schemaregistry.hortonworks;


import HortonworksSchemaRegistry.CACHE_EXPIRATION;
import HortonworksSchemaRegistry.CACHE_SIZE;
import HortonworksSchemaRegistry.URL;
import SchemaCompatibility.NONE;
import com.hortonworks.registries.schemaregistry.SchemaMetadata;
import com.hortonworks.registries.schemaregistry.SchemaMetadataInfo;
import com.hortonworks.registries.schemaregistry.SchemaVersionInfo;
import com.hortonworks.registries.schemaregistry.client.SchemaRegistryClient;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.controller.ConfigurationContext;
import org.apache.nifi.serialization.record.RecordSchema;
import org.apache.nifi.serialization.record.SchemaIdentifier;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestHortonworksSchemaRegistry {
    private HortonworksSchemaRegistry registry;

    private SchemaRegistryClient client;

    private final Map<String, SchemaVersionInfo> schemaVersionInfoMap = new HashMap<>();

    private final Map<String, SchemaMetadataInfo> schemaMetadataInfoMap = new HashMap<>();

    @Test
    public void testCacheUsed() throws Exception {
        final String text = new String(Files.readAllBytes(Paths.get("src/test/resources/empty-schema.avsc")));
        final SchemaVersionInfo info = new SchemaVersionInfo(1L, "unit-test", 2, text, System.currentTimeMillis(), "description");
        schemaVersionInfoMap.put("unit-test", info);
        final SchemaMetadata metadata = new SchemaMetadata.Builder("unit-test").compatibility(NONE).evolve(true).schemaGroup("group").type("AVRO").build();
        final Constructor<SchemaMetadataInfo> ctr = SchemaMetadataInfo.class.getDeclaredConstructor(SchemaMetadata.class, Long.class, Long.class);
        ctr.setAccessible(true);
        final SchemaMetadataInfo schemaMetadataInfo = ctr.newInstance(metadata, 1L, System.currentTimeMillis());
        schemaMetadataInfoMap.put("unit-test", schemaMetadataInfo);
        final Map<PropertyDescriptor, String> properties = new HashMap<>();
        properties.put(URL, "http://localhost:44444");
        properties.put(CACHE_EXPIRATION, "5 mins");
        properties.put(CACHE_SIZE, "1000");
        final ConfigurationContext configurationContext = new org.apache.nifi.util.MockConfigurationContext(properties, null);
        registry.enable(configurationContext);
        for (int i = 0; i < 10000; i++) {
            final SchemaIdentifier schemaIdentifier = SchemaIdentifier.builder().name("unit-test").build();
            final RecordSchema schema = registry.retrieveSchema(schemaIdentifier);
            Assert.assertNotNull(schema);
        }
        Mockito.verify(client, Mockito.times(1)).getLatestSchemaVersionInfo(ArgumentMatchers.any(String.class));
    }
}

