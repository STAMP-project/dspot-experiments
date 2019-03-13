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


import AvroOutputFormat.Codec;
import AvroOutputFormat.Codec.SNAPPY;
import FileSystem.WriteMode.OVERWRITE;
import User.SCHEMA;
import java.io.File;
import java.io.IOException;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.core.fs.Path;
import org.apache.flink.formats.avro.generated.User;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link AvroOutputFormat}.
 */
public class AvroOutputFormatTest {
    @Test
    public void testSetCodec() {
        // given
        final AvroOutputFormat<User> outputFormat = new AvroOutputFormat(User.class);
        // when
        try {
            outputFormat.setCodec(SNAPPY);
        } catch (Exception ex) {
            // then
            Assert.fail("unexpected exception");
        }
    }

    @Test
    public void testSetCodecError() {
        // given
        boolean error = false;
        final AvroOutputFormat<User> outputFormat = new AvroOutputFormat(User.class);
        // when
        try {
            outputFormat.setCodec(null);
        } catch (Exception ex) {
            error = true;
        }
        // then
        Assert.assertTrue(error);
    }

    @Test
    public void testSerialization() throws Exception {
        serializeAndDeserialize(null, null);
        serializeAndDeserialize(null, SCHEMA.);
        for (final AvroOutputFormat.Codec codec : Codec.values()) {
            serializeAndDeserialize(codec, null);
            serializeAndDeserialize(codec, SCHEMA.);
        }
    }

    @Test
    public void testCompression() throws Exception {
        // given
        final Path outputPath = new Path(File.createTempFile("avro-output-file", "avro").getAbsolutePath());
        final AvroOutputFormat<User> outputFormat = new AvroOutputFormat(outputPath, User.class);
        outputFormat.setWriteMode(OVERWRITE);
        final Path compressedOutputPath = new Path(File.createTempFile("avro-output-file", "compressed.avro").getAbsolutePath());
        final AvroOutputFormat<User> compressedOutputFormat = new AvroOutputFormat(compressedOutputPath, User.class);
        compressedOutputFormat.setWriteMode(OVERWRITE);
        compressedOutputFormat.setCodec(SNAPPY);
        // when
        output(outputFormat);
        output(compressedOutputFormat);
        // then
        Assert.assertTrue(((fileSize(outputPath)) > (fileSize(compressedOutputPath))));
        // cleanup
        FileSystem fs = FileSystem.getLocalFileSystem();
        fs.delete(outputPath, false);
        fs.delete(compressedOutputPath, false);
    }

    @Test
    public void testGenericRecord() throws IOException {
        final Path outputPath = new Path(File.createTempFile("avro-output-file", "generic.avro").getAbsolutePath());
        final AvroOutputFormat<GenericRecord> outputFormat = new AvroOutputFormat(outputPath, GenericRecord.class);
        Schema schema = new Schema.Parser().parse("{\"type\":\"record\", \"name\":\"user\", \"fields\": [{\"name\":\"user_name\", \"type\":\"string\"}, {\"name\":\"favorite_number\", \"type\":\"int\"}, {\"name\":\"favorite_color\", \"type\":\"string\"}]}");
        outputFormat.setWriteMode(OVERWRITE);
        outputFormat.setSchema(schema);
        output(outputFormat, schema);
        GenericDatumReader<GenericRecord> reader = new GenericDatumReader(schema);
        DataFileReader<GenericRecord> dataFileReader = new DataFileReader(new File(outputPath.getPath()), reader);
        while (dataFileReader.hasNext()) {
            GenericRecord record = dataFileReader.next();
            Assert.assertEquals(record.get("user_name").toString(), "testUser");
            Assert.assertEquals(record.get("favorite_number"), 1);
            Assert.assertEquals(record.get("favorite_color").toString(), "blue");
        } 
        // cleanup
        FileSystem fs = FileSystem.getLocalFileSystem();
        fs.delete(outputPath, false);
    }
}

