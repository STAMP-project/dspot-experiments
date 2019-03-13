/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.pinterest.secor.common;


import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import junit.framework.TestCase;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
public class SecorSchemaRegistryClientTest extends TestCase {
    private KafkaAvroDeserializer kafkaAvroDeserializer;

    private SchemaRegistryClient schemaRegistryClient;

    private SecorSchemaRegistryClient secorSchemaRegistryClient;

    private SecorConfig secorConfig;

    private SpecificDatumWriter<GenericRecord> writer;

    private KafkaAvroSerializer avroSerializer;

    @Test
    public void testDecodeMessage() throws Exception {
        Schema schemaV1 = SchemaBuilder.record("Foo").fields().name("data_field_1").type().intType().noDefault().name("timestamp").type().longType().noDefault().endRecord();
        // backward compatible schema change
        Schema schemaV2 = SchemaBuilder.record("Foo").fields().name("data_field_1").type().intType().noDefault().name("data_field_2").type().stringType().noDefault().name("timestamp").type().longType().noDefault().endRecord();
        GenericRecord record1 = set("data_field_1", 1).set("timestamp", 1467176315L).build();
        GenericRecord record2 = set("data_field_1", 1).set("data_field_2", "hello").set("timestamp", 1467176316L).build();
        GenericRecord output = secorSchemaRegistryClient.decodeMessage("test-avr-topic", avroSerializer.serialize("test-avr-topic", record1));
        TestCase.assertEquals(secorSchemaRegistryClient.getSchema("test-avr-topic"), schemaV1);
        TestCase.assertEquals(output.get("data_field_1"), 1);
        TestCase.assertEquals(output.get("timestamp"), 1467176315L);
        output = secorSchemaRegistryClient.decodeMessage("test-avr-topic", avroSerializer.serialize("test-avr-topic", record2));
        TestCase.assertEquals(secorSchemaRegistryClient.getSchema("test-avr-topic"), schemaV2);
        TestCase.assertEquals(output.get("data_field_1"), 1);
        TestCase.assertTrue(StringUtils.equals(output.get("data_field_2").toString(), "hello"));
        TestCase.assertEquals(output.get("timestamp"), 1467176316L);
    }
}

