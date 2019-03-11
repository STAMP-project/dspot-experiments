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
package org.apache.avro.hadoop.io;


import AvroSequenceFile.Reader.Options;
import Schema.Type.INT;
import Schema.Type.STRING;
import java.io.File;
import java.io.IOException;
import org.apache.avro.Schema;
import org.apache.avro.mapred.AvroKey;
import org.apache.avro.mapred.AvroValue;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class TestAvroSequenceFile {
    // Disable checkstyle for this variable.  It must be public to work with JUnit @Rule.
    // CHECKSTYLE:OFF
    @Rule
    public TemporaryFolder mTempDir = new TemporaryFolder();

    // CHECKSTYLE:ON
    /**
     * Tests that reading and writing avro data works.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testReadAvro() throws IOException {
        Path sequenceFilePath = new Path(new File(mTempDir.getRoot(), "output.seq").getPath());
        writeSequenceFile(sequenceFilePath, AvroKey.class, AvroValue.class, Schema.create(STRING), Schema.create(INT), new AvroKey<CharSequence>("one"), new AvroValue(1), new AvroKey<CharSequence>("two"), new AvroValue(2));
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        AvroSequenceFile.Reader.Options options = new AvroSequenceFile.Reader.Options().withFileSystem(fs).withInputPath(sequenceFilePath).withKeySchema(Schema.create(STRING)).withValueSchema(Schema.create(INT)).withConfiguration(conf);
        SequenceFile.Reader reader = new AvroSequenceFile.Reader(options);
        AvroKey<CharSequence> key = new AvroKey();
        AvroValue<Integer> value = new AvroValue();
        // Read the first record.
        key = ((AvroKey<CharSequence>) (reader.next(key)));
        Assert.assertNotNull(key);
        Assert.assertEquals("one", key.datum().toString());
        value = ((AvroValue<Integer>) (reader.getCurrentValue(value)));
        Assert.assertNotNull(value);
        Assert.assertEquals(1, value.datum().intValue());
        // Read the second record.
        key = ((AvroKey<CharSequence>) (reader.next(key)));
        Assert.assertNotNull(key);
        Assert.assertEquals("two", key.datum().toString());
        value = ((AvroValue<Integer>) (reader.getCurrentValue(value)));
        Assert.assertNotNull(value);
        Assert.assertEquals(2, value.datum().intValue());
        Assert.assertNull("Should be no more records.", reader.next(key));
    }

    /**
     * Tests that reading and writing avro records without a reader schema works.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testReadAvroWithoutReaderSchemas() throws IOException {
        Path sequenceFilePath = new Path(new File(mTempDir.getRoot(), "output.seq").getPath());
        writeSequenceFile(sequenceFilePath, AvroKey.class, AvroValue.class, Schema.create(STRING), Schema.create(INT), new AvroKey<CharSequence>("one"), new AvroValue(1), new AvroKey<CharSequence>("two"), new AvroValue(2));
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        AvroSequenceFile.Reader.Options options = new AvroSequenceFile.Reader.Options().withFileSystem(fs).withInputPath(sequenceFilePath).withConfiguration(conf);
        SequenceFile.Reader reader = new AvroSequenceFile.Reader(options);
        AvroKey<CharSequence> key = new AvroKey();
        AvroValue<Integer> value = new AvroValue();
        // Read the first record.
        key = ((AvroKey<CharSequence>) (reader.next(key)));
        Assert.assertNotNull(key);
        Assert.assertEquals("one", key.datum().toString());
        value = ((AvroValue<Integer>) (reader.getCurrentValue(value)));
        Assert.assertNotNull(value);
        Assert.assertEquals(1, value.datum().intValue());
        // Read the second record.
        key = ((AvroKey<CharSequence>) (reader.next(key)));
        Assert.assertNotNull(key);
        Assert.assertEquals("two", key.datum().toString());
        value = ((AvroValue<Integer>) (reader.getCurrentValue(value)));
        Assert.assertNotNull(value);
        Assert.assertEquals(2, value.datum().intValue());
        Assert.assertNull("Should be no more records.", reader.next(key));
    }

    /**
     * Tests that reading and writing ordinary Writables still works.
     */
    @Test
    public void testReadWritables() throws IOException {
        Path sequenceFilePath = new Path(new File(mTempDir.getRoot(), "output.seq").getPath());
        writeSequenceFile(sequenceFilePath, Text.class, IntWritable.class, null, null, new Text("one"), new IntWritable(1), new Text("two"), new IntWritable(2));
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        AvroSequenceFile.Reader.Options options = new AvroSequenceFile.Reader.Options().withFileSystem(fs).withInputPath(sequenceFilePath).withConfiguration(conf);
        SequenceFile.Reader reader = new AvroSequenceFile.Reader(options);
        Text key = new Text();
        IntWritable value = new IntWritable();
        // Read the first record.
        Assert.assertTrue(reader.next(key));
        Assert.assertEquals("one", key.toString());
        reader.getCurrentValue(value);
        Assert.assertNotNull(value);
        Assert.assertEquals(1, value.get());
        // Read the second record.
        Assert.assertTrue(reader.next(key));
        Assert.assertEquals("two", key.toString());
        reader.getCurrentValue(value);
        Assert.assertNotNull(value);
        Assert.assertEquals(2, value.get());
        Assert.assertFalse("Should be no more records.", reader.next(key));
    }
}

