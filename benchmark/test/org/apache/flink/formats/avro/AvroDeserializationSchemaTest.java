/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.formats.avro;


import java.util.Random;
import org.apache.avro.generic.GenericRecord;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.formats.avro.generated.Address;
import org.apache.flink.formats.avro.utils.AvroTestUtils;
import org.apache.flink.formats.avro.utils.TestDataGenerator;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link AvroDeserializationSchema}.
 */
public class AvroDeserializationSchemaTest {
    private static final Address address = TestDataGenerator.generateRandomAddress(new Random());

    @Test
    public void testGenericRecord() throws Exception {
        DeserializationSchema<GenericRecord> deserializationSchema = AvroDeserializationSchema.forGeneric(AvroDeserializationSchemaTest.address.getSchema());
        byte[] encodedAddress = AvroTestUtils.writeRecord(AvroDeserializationSchemaTest.address, Address.getClassSchema());
        GenericRecord genericRecord = deserializationSchema.deserialize(encodedAddress);
        Assert.assertEquals(AvroDeserializationSchemaTest.address.getCity(), genericRecord.get("city").toString());
        Assert.assertEquals(AvroDeserializationSchemaTest.address.getNum(), genericRecord.get("num"));
        Assert.assertEquals(AvroDeserializationSchemaTest.address.getState(), genericRecord.get("state").toString());
    }

    @Test
    public void testSpecificRecordWithConfluentSchemaRegistry() throws Exception {
        DeserializationSchema<Address> deserializer = AvroDeserializationSchema.forSpecific(Address.class);
        byte[] encodedAddress = AvroTestUtils.writeRecord(AvroDeserializationSchemaTest.address, Address.getClassSchema());
        Address deserializedAddress = deserializer.deserialize(encodedAddress);
        Assert.assertEquals(AvroDeserializationSchemaTest.address, deserializedAddress);
    }
}

