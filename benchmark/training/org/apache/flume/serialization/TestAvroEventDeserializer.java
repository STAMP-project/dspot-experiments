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
package org.apache.flume.serialization;


import AvroEventDeserializer.AVRO_SCHEMA_HEADER_HASH;
import AvroEventDeserializer.AVRO_SCHEMA_HEADER_LITERAL;
import AvroEventDeserializer.AvroSchemaType.HASH;
import AvroEventDeserializer.AvroSchemaType.LITERAL;
import AvroEventDeserializer.Builder;
import AvroEventDeserializer.CONFIG_SCHEMA_TYPE_KEY;
import Schema.Field;
import Schema.Type.STRING;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import org.apache.avro.Schema;
import org.apache.avro.SchemaNormalization;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.commons.codec.binary.Hex;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestAvroEventDeserializer {
    private static final Logger logger = LoggerFactory.getLogger(TestAvroEventDeserializer.class);

    private static final Schema schema;

    static {
        schema = Schema.createRecord("MyRecord", "", "org.apache.flume", false);
        Schema.Field field = new Schema.Field("foo", Schema.create(STRING), "", null);
        TestAvroEventDeserializer.schema.setFields(Collections.singletonList(field));
    }

    @Test
    public void resetTest() throws IOException {
        File tempFile = newTestFile(true);
        String target = tempFile.getAbsolutePath();
        TestAvroEventDeserializer.logger.info("Target: {}", target);
        TransientPositionTracker tracker = new TransientPositionTracker(target);
        AvroEventDeserializer.Builder desBuilder = new AvroEventDeserializer.Builder();
        EventDeserializer deserializer = desBuilder.build(new Context(), new ResettableFileInputStream(tempFile, tracker));
        BinaryDecoder decoder = null;
        DatumReader<GenericRecord> reader = new org.apache.avro.generic.GenericDatumReader<GenericRecord>(TestAvroEventDeserializer.schema);
        decoder = DecoderFactory.get().binaryDecoder(deserializer.readEvent().getBody(), decoder);
        Assert.assertEquals("bar", reader.read(null, decoder).get("foo").toString());
        deserializer.reset();
        decoder = DecoderFactory.get().binaryDecoder(deserializer.readEvent().getBody(), decoder);
        Assert.assertEquals("bar", reader.read(null, decoder).get("foo").toString());
        deserializer.mark();
        decoder = DecoderFactory.get().binaryDecoder(deserializer.readEvent().getBody(), decoder);
        Assert.assertEquals("baz", reader.read(null, decoder).get("foo").toString());
        deserializer.reset();
        decoder = DecoderFactory.get().binaryDecoder(deserializer.readEvent().getBody(), decoder);
        Assert.assertEquals("baz", reader.read(null, decoder).get("foo").toString());
        Assert.assertNull(deserializer.readEvent());
    }

    @Test
    public void testSchemaHash() throws IOException, NoSuchAlgorithmException {
        File tempFile = newTestFile(true);
        String target = tempFile.getAbsolutePath();
        TestAvroEventDeserializer.logger.info("Target: {}", target);
        TransientPositionTracker tracker = new TransientPositionTracker(target);
        Context context = new Context();
        context.put(CONFIG_SCHEMA_TYPE_KEY, HASH.toString());
        ResettableInputStream in = new ResettableFileInputStream(tempFile, tracker);
        EventDeserializer des = new AvroEventDeserializer.Builder().build(context, in);
        Event event = des.readEvent();
        String eventSchemaHash = event.getHeaders().get(AVRO_SCHEMA_HEADER_HASH);
        String expectedSchemaHash = Hex.encodeHexString(SchemaNormalization.parsingFingerprint("CRC-64-AVRO", TestAvroEventDeserializer.schema));
        Assert.assertEquals(expectedSchemaHash, eventSchemaHash);
    }

    @Test
    public void testSchemaLiteral() throws IOException {
        File tempFile = newTestFile(true);
        String target = tempFile.getAbsolutePath();
        TestAvroEventDeserializer.logger.info("Target: {}", target);
        TransientPositionTracker tracker = new TransientPositionTracker(target);
        Context context = new Context();
        context.put(CONFIG_SCHEMA_TYPE_KEY, LITERAL.toString());
        ResettableInputStream in = new ResettableFileInputStream(tempFile, tracker);
        EventDeserializer des = new AvroEventDeserializer.Builder().build(context, in);
        Event event = des.readEvent();
        String eventSchema = event.getHeaders().get(AVRO_SCHEMA_HEADER_LITERAL);
        Assert.assertEquals(TestAvroEventDeserializer.schema.toString(), eventSchema);
    }
}

