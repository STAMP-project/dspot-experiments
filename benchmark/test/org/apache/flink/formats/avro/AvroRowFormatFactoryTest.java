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


import java.util.Map;
import org.apache.flink.formats.avro.generated.User;
import org.apache.flink.table.descriptors.Avro;
import org.apache.flink.util.TestLogger;
import org.junit.Test;


/**
 * Tests for the {@link AvroRowFormatFactory}.
 */
public class AvroRowFormatFactoryTest extends TestLogger {
    private static final Class<User> AVRO_SPECIFIC_RECORD = User.class;

    private static final String AVRO_SCHEMA = User.getClassSchema().toString();

    @Test
    public void testRecordClass() {
        final Map<String, String> properties = new Avro().recordClass(AvroRowFormatFactoryTest.AVRO_SPECIFIC_RECORD).toProperties();
        testRecordClassDeserializationSchema(properties);
        testRecordClassSerializationSchema(properties);
    }

    @Test
    public void testAvroSchema() {
        final Map<String, String> properties = new Avro().avroSchema(AvroRowFormatFactoryTest.AVRO_SCHEMA).toProperties();
        testAvroSchemaSerializationSchema(properties);
        testAvroSchemaDeserializationSchema(properties);
    }
}

