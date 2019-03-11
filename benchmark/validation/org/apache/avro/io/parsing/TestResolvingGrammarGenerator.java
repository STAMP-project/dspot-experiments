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
package org.apache.avro.io.parsing;


import GenericData.Record;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import org.apache.avro.AvroTypeException;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericData;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class TestResolvingGrammarGenerator {
    private final Schema schema;

    private final JsonNode data;

    public TestResolvingGrammarGenerator(String jsonSchema, String jsonData) throws IOException {
        this.schema = Schema.parse(jsonSchema);
        JsonFactory factory = new JsonFactory();
        ObjectMapper mapper = new ObjectMapper(factory);
        this.data = mapper.readTree(new StringReader(jsonData));
    }

    @Test
    public void test() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        EncoderFactory factory = EncoderFactory.get();
        Encoder e = factory.validatingEncoder(schema, factory.binaryEncoder(baos, null));
        ResolvingGrammarGenerator.encode(e, schema, data);
        e.flush();
    }

    @Test
    public void testRecordMissingRequiredFieldError() throws Exception {
        Schema schemaWithoutField = SchemaBuilder.record("MyRecord").namespace("ns").fields().name("field1").type().stringType().noDefault().endRecord();
        Schema schemaWithField = SchemaBuilder.record("MyRecord").namespace("ns").fields().name("field1").type().stringType().noDefault().name("field2").type().stringType().noDefault().endRecord();
        GenericData.Record record = set("field1", "someValue").build();
        byte[] data = writeRecord(schemaWithoutField, record);
        try {
            readRecord(schemaWithField, data);
            Assert.fail("Expected exception not thrown");
        } catch (AvroTypeException typeException) {
            Assert.assertEquals("Incorrect exception message", "Found ns.MyRecord, expecting ns.MyRecord, missing required field field2", typeException.getMessage());
        }
    }
}

