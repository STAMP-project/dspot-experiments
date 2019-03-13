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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package org.apache.avro.mapreduce;


import GenericData.Record;
import Schema.Type.STRING;
import TextStats.SCHEMA;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.avro.Schema;
import org.apache.avro.file.CodecFactory;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileStream;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.hadoop.io.AvroDatumConverter;
import org.apache.avro.hadoop.io.AvroDatumConverterFactory;
import org.apache.avro.hadoop.io.AvroKeyValue;
import org.apache.avro.io.DatumReader;
import org.apache.avro.mapred.AvroValue;
import org.apache.avro.reflect.ReflectData;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.junit.Assert;
import org.junit.Test;


public class TestAvroKeyValueRecordWriter {
    @Test
    public void testWriteRecords() throws IOException {
        Job job = new Job();
        AvroJob.setOutputValueSchema(job, SCHEMA.);
        TaskAttemptContext context = createMock(TaskAttemptContext.class);
        replay(context);
        AvroDatumConverterFactory factory = new AvroDatumConverterFactory(job.getConfiguration());
        AvroDatumConverter<Text, ?> keyConverter = factory.create(Text.class);
        AvroValue<TextStats> avroValue = new AvroValue(null);
        @SuppressWarnings("unchecked")
        AvroDatumConverter<AvroValue<TextStats>, ?> valueConverter = factory.create(((Class<AvroValue<TextStats>>) (avroValue.getClass())));
        CodecFactory compressionCodec = CodecFactory.nullCodec();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // Use a writer to generate a Avro container file in memory.
        // Write two records: <'apple', TextStats('apple')> and <'banana', TextStats('banana')>.
        AvroKeyValueRecordWriter<Text, AvroValue<TextStats>> writer = new AvroKeyValueRecordWriter(keyConverter, valueConverter, new ReflectData(), compressionCodec, outputStream);
        TextStats appleStats = new TextStats();
        appleStats.name = "apple";
        writer.write(new Text("apple"), new AvroValue(appleStats));
        TextStats bananaStats = new TextStats();
        bananaStats.name = "banana";
        writer.write(new Text("banana"), new AvroValue(bananaStats));
        writer.close(context);
        verify(context);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        Schema readerSchema = AvroKeyValue.getSchema(Schema.create(STRING), SCHEMA.);
        DatumReader<GenericRecord> datumReader = new org.apache.avro.specific.SpecificDatumReader(readerSchema);
        DataFileStream<GenericRecord> avroFileReader = new DataFileStream(inputStream, datumReader);
        // Verify that the first record was written.
        Assert.assertTrue(avroFileReader.hasNext());
        AvroKeyValue<CharSequence, TextStats> firstRecord = new AvroKeyValue(avroFileReader.next());
        Assert.assertNotNull(firstRecord.get());
        Assert.assertEquals("apple", firstRecord.getKey().toString());
        Assert.assertEquals("apple", firstRecord.getValue().name.toString());
        // Verify that the second record was written;
        Assert.assertTrue(avroFileReader.hasNext());
        AvroKeyValue<CharSequence, TextStats> secondRecord = new AvroKeyValue(avroFileReader.next());
        Assert.assertNotNull(secondRecord.get());
        Assert.assertEquals("banana", secondRecord.getKey().toString());
        Assert.assertEquals("banana", secondRecord.getValue().name.toString());
        // That's all, folks.
        Assert.assertFalse(avroFileReader.hasNext());
        avroFileReader.close();
    }

    public static class R1 {
        String attribute;
    }

    @Test
    public void testUsingReflection() throws Exception {
        Job job = new Job();
        Schema schema = ReflectData.get().getSchema(TestAvroKeyValueRecordWriter.R1.class);
        AvroJob.setOutputValueSchema(job, schema);
        TaskAttemptContext context = createMock(TaskAttemptContext.class);
        replay(context);
        TestAvroKeyValueRecordWriter.R1 record = new TestAvroKeyValueRecordWriter.R1();
        record.attribute = "test";
        AvroValue<TestAvroKeyValueRecordWriter.R1> avroValue = new AvroValue(record);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        AvroDatumConverterFactory factory = new AvroDatumConverterFactory(job.getConfiguration());
        AvroDatumConverter<Text, ?> keyConverter = factory.create(Text.class);
        @SuppressWarnings("unchecked")
        AvroDatumConverter<AvroValue<TestAvroKeyValueRecordWriter.R1>, TestAvroKeyValueRecordWriter.R1> valueConverter = factory.create(((Class<AvroValue<TestAvroKeyValueRecordWriter.R1>>) (avroValue.getClass())));
        AvroKeyValueRecordWriter<Text, AvroValue<TestAvroKeyValueRecordWriter.R1>> writer = new AvroKeyValueRecordWriter(keyConverter, valueConverter, new ReflectData(), CodecFactory.nullCodec(), outputStream);
        writer.write(new Text("reflectionData"), avroValue);
        writer.close(context);
        verify(context);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        Schema readerSchema = AvroKeyValue.getSchema(Schema.create(STRING), schema);
        DatumReader<GenericRecord> datumReader = new org.apache.avro.reflect.ReflectDatumReader(readerSchema);
        DataFileStream<GenericRecord> avroFileReader = new DataFileStream(inputStream, datumReader);
        // Verify that the first record was written.
        Assert.assertTrue(avroFileReader.hasNext());
        // Verify that the record holds the same data that we've written
        AvroKeyValue<CharSequence, TestAvroKeyValueRecordWriter.R1> firstRecord = new AvroKeyValue(avroFileReader.next());
        Assert.assertNotNull(firstRecord.get());
        Assert.assertEquals("reflectionData", firstRecord.getKey().toString());
        Assert.assertEquals(record.attribute, firstRecord.getValue().attribute);
    }

    @Test
    public void testSyncableWriteRecords() throws IOException {
        Job job = new Job();
        AvroJob.setOutputValueSchema(job, SCHEMA.);
        TaskAttemptContext context = createMock(TaskAttemptContext.class);
        replay(context);
        AvroDatumConverterFactory factory = new AvroDatumConverterFactory(job.getConfiguration());
        AvroDatumConverter<Text, ?> keyConverter = factory.create(Text.class);
        AvroValue<TextStats> avroValue = new AvroValue(null);
        @SuppressWarnings("unchecked")
        AvroDatumConverter<AvroValue<TextStats>, ?> valueConverter = factory.create(((Class<AvroValue<TextStats>>) (avroValue.getClass())));
        CodecFactory compressionCodec = CodecFactory.nullCodec();
        FileOutputStream outputStream = new FileOutputStream(new File("target/temp.avro"));
        // Write a marker followed by each record: <'apple', TextStats('apple')> and <'banana', TextStats('banana')>.
        AvroKeyValueRecordWriter<Text, AvroValue<TextStats>> writer = new AvroKeyValueRecordWriter(keyConverter, valueConverter, new ReflectData(), compressionCodec, outputStream);
        TextStats appleStats = new TextStats();
        appleStats.name = "apple";
        long pointOne = writer.sync();
        writer.write(new Text("apple"), new AvroValue(appleStats));
        TextStats bananaStats = new TextStats();
        bananaStats.name = "banana";
        long pointTwo = writer.sync();
        writer.write(new Text("banana"), new AvroValue(bananaStats));
        writer.close(context);
        verify(context);
        Configuration conf = new Configuration();
        conf.set("fs.default.name", "file:///");
        Path avroFile = new Path("target/temp.avro");
        DataFileReader<GenericData.Record> avroFileReader = new DataFileReader(new org.apache.avro.mapred.FsInput(avroFile, conf), new org.apache.avro.specific.SpecificDatumReader());
        avroFileReader.seek(pointTwo);
        // Verify that the second record was written;
        Assert.assertTrue(avroFileReader.hasNext());
        AvroKeyValue<CharSequence, TextStats> secondRecord = new AvroKeyValue(avroFileReader.next());
        Assert.assertNotNull(secondRecord.get());
        Assert.assertEquals("banana", secondRecord.getKey().toString());
        Assert.assertEquals("banana", secondRecord.getValue().name.toString());
        avroFileReader.seek(pointOne);
        // Verify that the first record was written.
        Assert.assertTrue(avroFileReader.hasNext());
        AvroKeyValue<CharSequence, TextStats> firstRecord = new AvroKeyValue(avroFileReader.next());
        Assert.assertNotNull(firstRecord.get());
        Assert.assertEquals("apple", firstRecord.getKey().toString());
        Assert.assertEquals("apple", firstRecord.getValue().name.toString());
        // That's all, folks.
        avroFileReader.close();
    }
}

