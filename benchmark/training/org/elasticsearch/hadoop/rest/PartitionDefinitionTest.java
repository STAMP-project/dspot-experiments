/**
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.hadoop.rest;


import java.io.IOException;
import org.elasticsearch.hadoop.cfg.PropertiesSettings;
import org.elasticsearch.hadoop.serialization.dto.mapping.Mapping;
import org.elasticsearch.hadoop.util.BytesArray;
import org.junit.Test;


public class PartitionDefinitionTest {
    @Test
    public void testWritable() throws IOException {
        Mapping mapping = getTestMapping();
        PropertiesSettings settings = new PropertiesSettings();
        settings.setProperty("setting1", "value1");
        settings.setProperty("setting2", "value2");
        PartitionDefinition expected = new PartitionDefinition(settings, mapping, "foo", 12, new String[]{ "localhost:9200", "otherhost:9200" });
        BytesArray bytes = PartitionDefinitionTest.writeWritablePartition(expected);
        PartitionDefinition def = PartitionDefinitionTest.readWritablePartition(bytes);
        PartitionDefinitionTest.assertPartitionEquals(expected, def);
    }

    @Test
    public void testSerializable() throws IOException, ClassNotFoundException {
        Mapping mapping = getTestMapping();
        PropertiesSettings settings = new PropertiesSettings();
        settings.setProperty("setting1", "value1");
        settings.setProperty("setting2", "value2");
        PartitionDefinition expected = new PartitionDefinition(settings, mapping, "bar", 37, new String[]{ "localhost:9200", "otherhost:9200" });
        BytesArray bytes = PartitionDefinitionTest.writeSerializablePartition(expected);
        PartitionDefinition def = PartitionDefinitionTest.readSerializablePartition(bytes);
        PartitionDefinitionTest.assertPartitionEquals(expected, def);
    }

    @Test
    public void testWritableWithSlice() throws IOException {
        Mapping mapping = getTestMapping();
        PropertiesSettings settings = new PropertiesSettings();
        settings.setProperty("setting1", "value1");
        settings.setProperty("setting2", "value2");
        PartitionDefinition expected = new PartitionDefinition(settings, mapping, "foo", 12, new PartitionDefinition.Slice(10, 27), new String[]{ "localhost:9200", "otherhost:9200" });
        BytesArray bytes = PartitionDefinitionTest.writeWritablePartition(expected);
        PartitionDefinition def = PartitionDefinitionTest.readWritablePartition(bytes);
        PartitionDefinitionTest.assertPartitionEquals(expected, def);
    }

    @Test
    public void testSerializableWithSlice() throws IOException, ClassNotFoundException {
        Mapping mapping = getTestMapping();
        PropertiesSettings settings = new PropertiesSettings();
        settings.setProperty("setting1", "value1");
        settings.setProperty("setting2", "value2");
        PartitionDefinition expected = new PartitionDefinition(settings, mapping, "bar", 37, new PartitionDefinition.Slice(13, 35), new String[]{ "localhost:9200", "otherhost:9200" });
        BytesArray bytes = PartitionDefinitionTest.writeSerializablePartition(expected);
        PartitionDefinition def = PartitionDefinitionTest.readSerializablePartition(bytes);
        PartitionDefinitionTest.assertPartitionEquals(expected, def);
    }
}

