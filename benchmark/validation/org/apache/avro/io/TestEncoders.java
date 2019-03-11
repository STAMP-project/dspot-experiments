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
package org.apache.avro.io;


import Type.INT;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import org.apache.avro.AvroTypeException;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static java.nio.channels.FileChannel.MapMode.READ_ONLY;


public class TestEncoders {
    private static final int ENCODER_BUFFER_SIZE = 32;

    private static final int EXAMPLE_DATA_SIZE = 17;

    private static EncoderFactory factory = EncoderFactory.get();

    @Rule
    public TemporaryFolder DIR = new TemporaryFolder();

    @Test
    public void testBinaryEncoderInit() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        BinaryEncoder enc = TestEncoders.factory.binaryEncoder(out, null);
        Assert.assertSame(enc, TestEncoders.factory.binaryEncoder(out, enc));
    }

    @Test(expected = NullPointerException.class)
    public void testBadBinaryEncoderInit() {
        TestEncoders.factory.binaryEncoder(null, null);
    }

    @Test
    public void testBlockingBinaryEncoderInit() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        BinaryEncoder reuse = null;
        reuse = TestEncoders.factory.blockingBinaryEncoder(out, reuse);
        Assert.assertSame(reuse, TestEncoders.factory.blockingBinaryEncoder(out, reuse));
        // comparison
    }

    @Test(expected = NullPointerException.class)
    public void testBadBlockintBinaryEncoderInit() {
        TestEncoders.factory.binaryEncoder(null, null);
    }

    @Test
    public void testDirectBinaryEncoderInit() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        BinaryEncoder enc = TestEncoders.factory.directBinaryEncoder(out, null);
        Assert.assertSame(enc, TestEncoders.factory.directBinaryEncoder(out, enc));
    }

    @Test(expected = NullPointerException.class)
    public void testBadDirectBinaryEncoderInit() {
        TestEncoders.factory.directBinaryEncoder(null, null);
    }

    @Test
    public void testJsonEncoderInit() throws IOException {
        Schema s = Schema.parse("\"int\"");
        OutputStream out = new ByteArrayOutputStream();
        TestEncoders.factory.jsonEncoder(s, out);
        JsonEncoder enc = TestEncoders.factory.jsonEncoder(s, new JsonFactory().createJsonGenerator(out, JsonEncoding.UTF8));
        enc.configure(out);
    }

    @Test(expected = NullPointerException.class)
    public void testBadJsonEncoderInitOS() throws IOException {
        TestEncoders.factory.jsonEncoder(Schema.create(INT), ((OutputStream) (null)));
    }

    @Test(expected = NullPointerException.class)
    public void testBadJsonEncoderInit() throws IOException {
        TestEncoders.factory.jsonEncoder(Schema.create(INT), ((JsonGenerator) (null)));
    }

    @Test
    public void testJsonEncoderNewlineDelimited() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        Schema ints = Schema.create(INT);
        Encoder e = TestEncoders.factory.jsonEncoder(ints, out);
        String separator = System.getProperty("line.separator");
        GenericDatumWriter<Integer> writer = new GenericDatumWriter(ints);
        writer.write(1, e);
        writer.write(2, e);
        e.flush();
        Assert.assertEquals((("1" + separator) + "2"), out.toString());
    }

    @Test
    public void testValidatingEncoderInit() throws IOException {
        Schema s = Schema.parse("\"int\"");
        OutputStream out = new ByteArrayOutputStream();
        Encoder e = TestEncoders.factory.directBinaryEncoder(out, null);
        TestEncoders.factory.validatingEncoder(s, e).configure(e);
    }

    @Test
    public void testJsonRecordOrdering() throws IOException {
        String value = "{\"b\": 2, \"a\": 1}";
        Schema schema = new Schema.Parser().parse(("{\"type\": \"record\", \"name\": \"ab\", \"fields\": [" + ("{\"name\": \"a\", \"type\": \"int\"}, {\"name\": \"b\", \"type\": \"int\"}" + "]}")));
        GenericDatumReader<Object> reader = new GenericDatumReader(schema);
        Decoder decoder = DecoderFactory.get().jsonDecoder(schema, value);
        Object o = reader.read(null, decoder);
        Assert.assertEquals("{\"a\": 1, \"b\": 2}", o.toString());
    }

    @Test(expected = AvroTypeException.class)
    public void testJsonExcessFields() throws IOException {
        String value = "{\"b\": { \"b3\": 1.4, \"b2\": 3.14, \"b1\": \"h\"}, \"a\": {\"a0\": 45, \"a2\":true, \"a1\": null}}";
        Schema schema = new Schema.Parser().parse(("{\"type\": \"record\", \"name\": \"ab\", \"fields\": [\n" + (((("{\"name\": \"a\", \"type\": {\"type\":\"record\",\"name\":\"A\",\"fields\":\n" + "[{\"name\":\"a1\", \"type\":\"null\"}, {\"name\":\"a2\", \"type\":\"boolean\"}]}},\n") + "{\"name\": \"b\", \"type\": {\"type\":\"record\",\"name\":\"B\",\"fields\":\n") + "[{\"name\":\"b1\", \"type\":\"string\"}, {\"name\":\"b2\", \"type\":\"float\"}, {\"name\":\"b3\", \"type\":\"double\"}]}}\n") + "]}")));
        GenericDatumReader<Object> reader = new GenericDatumReader(schema);
        Decoder decoder = DecoderFactory.get().jsonDecoder(schema, value);
        reader.read(null, decoder);
    }

    @Test
    public void testJsonRecordOrdering2() throws IOException {
        String value = "{\"b\": { \"b3\": 1.4, \"b2\": 3.14, \"b1\": \"h\"}, \"a\": {\"a2\":true, \"a1\": null}}";
        Schema schema = new Schema.Parser().parse(("{\"type\": \"record\", \"name\": \"ab\", \"fields\": [\n" + (((("{\"name\": \"a\", \"type\": {\"type\":\"record\",\"name\":\"A\",\"fields\":\n" + "[{\"name\":\"a1\", \"type\":\"null\"}, {\"name\":\"a2\", \"type\":\"boolean\"}]}},\n") + "{\"name\": \"b\", \"type\": {\"type\":\"record\",\"name\":\"B\",\"fields\":\n") + "[{\"name\":\"b1\", \"type\":\"string\"}, {\"name\":\"b2\", \"type\":\"float\"}, {\"name\":\"b3\", \"type\":\"double\"}]}}\n") + "]}")));
        GenericDatumReader<Object> reader = new GenericDatumReader(schema);
        Decoder decoder = DecoderFactory.get().jsonDecoder(schema, value);
        Object o = reader.read(null, decoder);
        Assert.assertEquals("{\"a\": {\"a1\": null, \"a2\": true}, \"b\": {\"b1\": \"h\", \"b2\": 3.14, \"b3\": 1.4}}", o.toString());
    }

    @Test
    public void testJsonRecordOrderingWithProjection() throws IOException {
        String value = "{\"b\": { \"b3\": 1.4, \"b2\": 3.14, \"b1\": \"h\"}, \"a\": {\"a2\":true, \"a1\": null}}";
        Schema writerSchema = new Schema.Parser().parse(("{\"type\": \"record\", \"name\": \"ab\", \"fields\": [\n" + (((("{\"name\": \"a\", \"type\": {\"type\":\"record\",\"name\":\"A\",\"fields\":\n" + "[{\"name\":\"a1\", \"type\":\"null\"}, {\"name\":\"a2\", \"type\":\"boolean\"}]}},\n") + "{\"name\": \"b\", \"type\": {\"type\":\"record\",\"name\":\"B\",\"fields\":\n") + "[{\"name\":\"b1\", \"type\":\"string\"}, {\"name\":\"b2\", \"type\":\"float\"}, {\"name\":\"b3\", \"type\":\"double\"}]}}\n") + "]}")));
        Schema readerSchema = new Schema.Parser().parse(("{\"type\": \"record\", \"name\": \"ab\", \"fields\": [\n" + (("{\"name\": \"a\", \"type\": {\"type\":\"record\",\"name\":\"A\",\"fields\":\n" + "[{\"name\":\"a1\", \"type\":\"null\"}, {\"name\":\"a2\", \"type\":\"boolean\"}]}}\n") + "]}")));
        GenericDatumReader<Object> reader = new GenericDatumReader(writerSchema, readerSchema);
        Decoder decoder = DecoderFactory.get().jsonDecoder(writerSchema, value);
        Object o = reader.read(null, decoder);
        Assert.assertEquals("{\"a\": {\"a1\": null, \"a2\": true}}", o.toString());
    }

    @Test
    public void testJsonRecordOrderingWithProjection2() throws IOException {
        String value = "{\"b\": { \"b1\": \"h\", \"b2\": [3.14, 3.56], \"b3\": 1.4}, \"a\": {\"a2\":true, \"a1\": null}}";
        Schema writerSchema = new Schema.Parser().parse(("{\"type\": \"record\", \"name\": \"ab\", \"fields\": [\n" + (((("{\"name\": \"a\", \"type\": {\"type\":\"record\",\"name\":\"A\",\"fields\":\n" + "[{\"name\":\"a1\", \"type\":\"null\"}, {\"name\":\"a2\", \"type\":\"boolean\"}]}},\n") + "{\"name\": \"b\", \"type\": {\"type\":\"record\",\"name\":\"B\",\"fields\":\n") + "[{\"name\":\"b1\", \"type\":\"string\"}, {\"name\":\"b2\", \"type\":{\"type\":\"array\", \"items\":\"float\"}}, {\"name\":\"b3\", \"type\":\"double\"}]}}\n") + "]}")));
        Schema readerSchema = new Schema.Parser().parse(("{\"type\": \"record\", \"name\": \"ab\", \"fields\": [\n" + (("{\"name\": \"a\", \"type\": {\"type\":\"record\",\"name\":\"A\",\"fields\":\n" + "[{\"name\":\"a1\", \"type\":\"null\"}, {\"name\":\"a2\", \"type\":\"boolean\"}]}}\n") + "]}")));
        GenericDatumReader<Object> reader = new GenericDatumReader(writerSchema, readerSchema);
        Decoder decoder = DecoderFactory.get().jsonDecoder(writerSchema, value);
        Object o = reader.read(null, decoder);
        Assert.assertEquals("{\"a\": {\"a1\": null, \"a2\": true}}", o.toString());
    }

    @Test
    public void testArrayBackedByteBuffer() throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(someBytes(TestEncoders.EXAMPLE_DATA_SIZE));
        testWithBuffer(buffer);
    }

    @Test
    public void testMappedByteBuffer() throws IOException {
        Path file = Paths.get(((DIR.getRoot().getPath()) + "testMappedByteBuffer.avro"));
        Files.write(file, someBytes(TestEncoders.EXAMPLE_DATA_SIZE));
        MappedByteBuffer buffer = FileChannel.open(file, StandardOpenOption.READ).map(READ_ONLY, 0, TestEncoders.EXAMPLE_DATA_SIZE);
        testWithBuffer(buffer);
    }
}

