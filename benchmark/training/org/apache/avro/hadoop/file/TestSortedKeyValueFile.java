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
package org.apache.avro.hadoop.file;


import Schema.Type.LONG;
import Schema.Type.STRING;
import SortedKeyValueFile.Writer.Options;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;
import org.apache.avro.file.CodecFactory;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.FileReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.hadoop.io.AvroKeyValue;
import org.apache.avro.io.DatumReader;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static SortedKeyValueFile.DATA_FILENAME;
import static SortedKeyValueFile.INDEX_FILENAME;


public class TestSortedKeyValueFile {
    private static final Logger LOG = LoggerFactory.getLogger(TestSortedKeyValueFile.class);

    @Rule
    public TemporaryFolder mTempDir = new TemporaryFolder();

    @Test(expected = IllegalArgumentException.class)
    public void testWriteOutOfSortedOrder() throws IOException {
        TestSortedKeyValueFile.LOG.debug("Writing some records to a SortedKeyValueFile...");
        Configuration conf = new Configuration();
        SortedKeyValueFile.Writer.Options options = new SortedKeyValueFile.Writer.Options().withKeySchema(Schema.create(STRING)).withValueSchema(Schema.create(STRING)).withConfiguration(conf).withPath(new Path(mTempDir.getRoot().getPath(), "myfile")).withIndexInterval(2);// Index every other record.

        SortedKeyValueFile.Writer<CharSequence, CharSequence> writer = new SortedKeyValueFile.Writer<>(options);
        Utf8 key = new Utf8();
        // re-use key, to test copied
        try {
            writer.append(key.set("banana"), "Banana");
            writer.append(key.set("apple"), "Apple");// Ruh, roh!

        } finally {
            writer.close();
        }
    }

    @Test
    public void testNamedCodecs() throws IOException {
        Configuration conf = new Configuration();
        Path myfile = new Path(mTempDir.getRoot().getPath(), "myfile");
        Schema key = Schema.create(STRING);
        Schema value = Schema.create(STRING);
        Schema recordSchema = AvroKeyValue.getSchema(key, value);
        DatumReader<GenericRecord> datumReader = SpecificData.get().createDatumReader(recordSchema);
        DataFileReader<GenericRecord> reader;
        SortedKeyValueFile.Writer.Options options = new SortedKeyValueFile.Writer.Options().withKeySchema(key).withValueSchema(value).withConfiguration(conf).withPath(myfile);
        SortedKeyValueFile.Writer<CharSequence, CharSequence> writer;
        for (String codec : new String[]{ "null", "deflate", "snappy", "bzip2" }) {
            TestSortedKeyValueFile.LOG.debug((("Using " + codec) + "codec for a SortedKeyValueFile..."));
            options.withCodec(codec);
            writer = new SortedKeyValueFile.Writer<>(options);
            writer.close();
            reader = new DataFileReader(new org.apache.avro.mapred.FsInput(new Path(myfile, DATA_FILENAME), conf), datumReader);
            Assert.assertEquals(codec, reader.getMetaString("avro.codec"));
            reader.close();
        }
    }

    @Test
    public void testDeflateClassCodec() throws IOException {
        Configuration conf = new Configuration();
        Path myfile = new Path(mTempDir.getRoot().getPath(), "myfile");
        Schema key = Schema.create(STRING);
        Schema value = Schema.create(STRING);
        Schema recordSchema = AvroKeyValue.getSchema(key, value);
        DatumReader<GenericRecord> datumReader = SpecificData.get().createDatumReader(recordSchema);
        DataFileReader<GenericRecord> reader;
        TestSortedKeyValueFile.LOG.debug("Using CodecFactory.deflateCodec() for a SortedKeyValueFile...");
        SortedKeyValueFile.Writer.Options options = new SortedKeyValueFile.Writer.Options().withKeySchema(key).withValueSchema(value).withConfiguration(conf).withPath(myfile).withCodec(CodecFactory.deflateCodec(9));
        SortedKeyValueFile.Writer<CharSequence, CharSequence> writer = new SortedKeyValueFile.Writer<>(options);
        writer.close();
        reader = new DataFileReader(new org.apache.avro.mapred.FsInput(new Path(myfile, DATA_FILENAME), conf), datumReader);
        Assert.assertEquals("deflate", reader.getMetaString("avro.codec"));
        reader.close();
    }

    @Test
    public void testBadCodec() throws IOException {
        TestSortedKeyValueFile.LOG.debug("Using a bad codec for a SortedKeyValueFile...");
        try {
            SortedKeyValueFile.Writer.Options options = new SortedKeyValueFile.Writer.Options().withCodec("foobar");
        } catch (AvroRuntimeException e) {
            Assert.assertEquals("Unrecognized codec: foobar", e.getMessage());
        }
    }

    @Test
    public void testWriter() throws IOException {
        TestSortedKeyValueFile.LOG.debug("Writing some records to a SortedKeyValueFile...");
        Configuration conf = new Configuration();
        SortedKeyValueFile.Writer.Options options = new SortedKeyValueFile.Writer.Options().withKeySchema(Schema.create(STRING)).withValueSchema(Schema.create(STRING)).withConfiguration(conf).withPath(new Path(mTempDir.getRoot().getPath(), "myfile")).withIndexInterval(2);// Index every other record.

        SortedKeyValueFile.Writer<CharSequence, CharSequence> writer = new SortedKeyValueFile.Writer<>(options);
        try {
            writer.append("apple", "Apple");// Will be indexed.

            writer.append("banana", "Banana");
            writer.append("carrot", "Carrot");// Will be indexed.

            writer.append("durian", "Durian");
        } finally {
            writer.close();
        }
        TestSortedKeyValueFile.LOG.debug("Checking the generated directory...");
        File directory = new File(mTempDir.getRoot().getPath(), "myfile");
        Assert.assertTrue(directory.exists());
        TestSortedKeyValueFile.LOG.debug("Checking the generated index file...");
        File indexFile = new File(directory, INDEX_FILENAME);
        DatumReader<GenericRecord> indexReader = new org.apache.avro.generic.GenericDatumReader(AvroKeyValue.getSchema(options.getKeySchema(), Schema.create(LONG)));
        FileReader<GenericRecord> indexFileReader = DataFileReader.openReader(indexFile, indexReader);
        List<AvroKeyValue<CharSequence, Long>> indexRecords = new ArrayList<>();
        try {
            for (GenericRecord indexRecord : indexFileReader) {
                indexRecords.add(new AvroKeyValue(indexRecord));
            }
        } finally {
            indexFileReader.close();
        }
        Assert.assertEquals(2, indexRecords.size());
        Assert.assertEquals("apple", indexRecords.get(0).getKey().toString());
        TestSortedKeyValueFile.LOG.debug(("apple's position in the file: " + (indexRecords.get(0).getValue())));
        Assert.assertEquals("carrot", indexRecords.get(1).getKey().toString());
        TestSortedKeyValueFile.LOG.debug(("carrot's position in the file: " + (indexRecords.get(1).getValue())));
        TestSortedKeyValueFile.LOG.debug("Checking the generated data file...");
        File dataFile = new File(directory, DATA_FILENAME);
        DatumReader<GenericRecord> dataReader = new org.apache.avro.generic.GenericDatumReader(AvroKeyValue.getSchema(options.getKeySchema(), options.getValueSchema()));
        DataFileReader<GenericRecord> dataFileReader = new DataFileReader(dataFile, dataReader);
        try {
            dataFileReader.seek(indexRecords.get(0).getValue());
            Assert.assertTrue(dataFileReader.hasNext());
            AvroKeyValue<CharSequence, CharSequence> appleRecord = new AvroKeyValue(dataFileReader.next());
            Assert.assertEquals("apple", appleRecord.getKey().toString());
            Assert.assertEquals("Apple", appleRecord.getValue().toString());
            dataFileReader.seek(indexRecords.get(1).getValue());
            Assert.assertTrue(dataFileReader.hasNext());
            AvroKeyValue<CharSequence, CharSequence> carrotRecord = new AvroKeyValue(dataFileReader.next());
            Assert.assertEquals("carrot", carrotRecord.getKey().toString());
            Assert.assertEquals("Carrot", carrotRecord.getValue().toString());
            Assert.assertTrue(dataFileReader.hasNext());
            AvroKeyValue<CharSequence, CharSequence> durianRecord = new AvroKeyValue(dataFileReader.next());
            Assert.assertEquals("durian", durianRecord.getKey().toString());
            Assert.assertEquals("Durian", durianRecord.getValue().toString());
        } finally {
            dataFileReader.close();
        }
    }

    @Test
    public void testReader() throws IOException {
        Configuration conf = new Configuration();
        SortedKeyValueFile.Writer.Options writerOptions = new SortedKeyValueFile.Writer.Options().withKeySchema(Schema.create(STRING)).withValueSchema(Schema.create(STRING)).withConfiguration(conf).withPath(new Path(mTempDir.getRoot().getPath(), "myfile")).withIndexInterval(2);// Index every other record.

        SortedKeyValueFile.Writer<CharSequence, CharSequence> writer = new SortedKeyValueFile.Writer<>(writerOptions);
        try {
            writer.append("apple", "Apple");// Will be indexed.

            writer.append("banana", "Banana");
            writer.append("carrot", "Carrot");// Will be indexed.

            writer.append("durian", "Durian");
        } finally {
            writer.close();
        }
        TestSortedKeyValueFile.LOG.debug("Reading the file back using a reader...");
        SortedKeyValueFile.Reader.Options readerOptions = new SortedKeyValueFile.Reader.Options().withKeySchema(Schema.create(STRING)).withValueSchema(Schema.create(STRING)).withConfiguration(conf).withPath(new Path(mTempDir.getRoot().getPath(), "myfile"));
        SortedKeyValueFile.Reader<CharSequence, CharSequence> reader = new SortedKeyValueFile.Reader<>(readerOptions);
        try {
            Assert.assertEquals("Carrot", reader.get("carrot").toString());
            Assert.assertEquals("Banana", reader.get("banana").toString());
            Assert.assertNull(reader.get("a-vegetable"));
            Assert.assertNull(reader.get("beet"));
            Assert.assertNull(reader.get("zzz"));
        } finally {
            reader.close();
        }
    }

    public static class Stringy implements Comparable<TestSortedKeyValueFile.Stringy> {
        private String s;

        public Stringy() {
        }

        public Stringy(String s) {
            this.s = s;
        }

        @Override
        public String toString() {
            return s;
        }

        @Override
        public int hashCode() {
            return s.hashCode();
        }

        @Override
        public boolean equals(Object that) {
            return this.s.equals(that.toString());
        }

        @Override
        public int compareTo(TestSortedKeyValueFile.Stringy that) {
            return this.s.compareTo(that.s);
        }
    }

    @Test
    public void testAlternateModel() throws Exception {
        TestSortedKeyValueFile.LOG.debug("Writing some reflect records...");
        ReflectData model = ReflectData.get();
        Configuration conf = new Configuration();
        SortedKeyValueFile.Writer.Options options = new SortedKeyValueFile.Writer.Options().withKeySchema(model.getSchema(TestSortedKeyValueFile.Stringy.class)).withValueSchema(model.getSchema(TestSortedKeyValueFile.Stringy.class)).withConfiguration(conf).withPath(new Path(mTempDir.getRoot().getPath(), "reflect")).withDataModel(model).withIndexInterval(2);
        SortedKeyValueFile.Writer<TestSortedKeyValueFile.Stringy, TestSortedKeyValueFile.Stringy> writer = new SortedKeyValueFile.Writer<>(options);
        try {
            writer.append(new TestSortedKeyValueFile.Stringy("apple"), new TestSortedKeyValueFile.Stringy("Apple"));
            writer.append(new TestSortedKeyValueFile.Stringy("banana"), new TestSortedKeyValueFile.Stringy("Banana"));
            writer.append(new TestSortedKeyValueFile.Stringy("carrot"), new TestSortedKeyValueFile.Stringy("Carrot"));
            writer.append(new TestSortedKeyValueFile.Stringy("durian"), new TestSortedKeyValueFile.Stringy("Durian"));
        } finally {
            writer.close();
        }
        TestSortedKeyValueFile.LOG.debug("Reading the file back using a reader...");
        SortedKeyValueFile.Reader.Options readerOptions = new SortedKeyValueFile.Reader.Options().withKeySchema(model.getSchema(TestSortedKeyValueFile.Stringy.class)).withValueSchema(model.getSchema(TestSortedKeyValueFile.Stringy.class)).withConfiguration(conf).withPath(new Path(mTempDir.getRoot().getPath(), "reflect")).withDataModel(model);
        SortedKeyValueFile.Reader<TestSortedKeyValueFile.Stringy, TestSortedKeyValueFile.Stringy> reader = new SortedKeyValueFile.Reader<>(readerOptions);
        try {
            Assert.assertEquals(new TestSortedKeyValueFile.Stringy("Carrot"), reader.get(new TestSortedKeyValueFile.Stringy("carrot")));
            Assert.assertEquals(new TestSortedKeyValueFile.Stringy("Banana"), reader.get(new TestSortedKeyValueFile.Stringy("banana")));
            Assert.assertNull(reader.get(new TestSortedKeyValueFile.Stringy("a-vegetable")));
            Assert.assertNull(reader.get(new TestSortedKeyValueFile.Stringy("beet")));
            Assert.assertNull(reader.get(new TestSortedKeyValueFile.Stringy("zzz")));
        } finally {
            reader.close();
        }
    }
}

