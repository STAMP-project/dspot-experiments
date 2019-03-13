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


import Schema.Type.INT;
import Schema.Type.STRING;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.hadoop.io.AvroKeyValue;
import org.apache.avro.io.DatumReader;
import org.apache.avro.mapred.AvroKey;
import org.apache.avro.mapred.AvroValue;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests that Avro container files of generic records with two fields 'key' and 'value'
 * can be read by the AvroKeyValueInputFormat.
 */
public class TestKeyValueInput {
    @Rule
    public TemporaryFolder mTempDir = new TemporaryFolder();

    /**
     * A mapper for indexing documents.
     */
    public static class IndexMapper extends Mapper<AvroKey<Integer>, AvroValue<CharSequence>, Text, IntWritable> {
        @Override
        protected void map(AvroKey<Integer> docid, AvroValue<CharSequence> body, Context context) throws IOException, InterruptedException {
            for (String token : body.datum().toString().split(" ")) {
                context.write(new Text(token), new IntWritable(docid.datum()));
            }
        }
    }

    /**
     * A reducer for aggregating token to docid mapping into a hitlist.
     */
    public static class IndexReducer extends Reducer<Text, IntWritable, Text, AvroValue<List<Integer>>> {
        @Override
        protected void reduce(Text token, Iterable<IntWritable> docids, Context context) throws IOException, InterruptedException {
            List<Integer> hitlist = new ArrayList<>();
            for (IntWritable docid : docids) {
                hitlist.add(docid.get());
            }
            context.write(token, new AvroValue(hitlist));
        }
    }

    @Test
    public void testKeyValueInput() throws IOException, ClassNotFoundException, InterruptedException {
        // Create a test input file.
        File inputFile = createInputFile();
        // Configure the job input.
        Job job = new Job();
        FileInputFormat.setInputPaths(job, new Path(inputFile.getAbsolutePath()));
        job.setInputFormatClass(AvroKeyValueInputFormat.class);
        AvroJob.setInputKeySchema(job, Schema.create(INT));
        AvroJob.setInputValueSchema(job, Schema.create(STRING));
        // Configure a mapper.
        job.setMapperClass(TestKeyValueInput.IndexMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);
        // Configure a reducer.
        job.setReducerClass(TestKeyValueInput.IndexReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(AvroValue.class);
        AvroJob.setOutputValueSchema(job, Schema.createArray(Schema.create(INT)));
        // Configure the output format.
        job.setOutputFormatClass(AvroKeyValueOutputFormat.class);
        Path outputPath = new Path(mTempDir.getRoot().getPath(), "out-index");
        FileOutputFormat.setOutputPath(job, outputPath);
        // Run the job.
        Assert.assertTrue(job.waitForCompletion(true));
        // Verify that the output Avro container file has the expected data.
        File avroFile = new File(outputPath.toString(), "part-r-00000.avro");
        DatumReader<GenericRecord> datumReader = new org.apache.avro.specific.SpecificDatumReader(AvroKeyValue.getSchema(Schema.create(STRING), Schema.createArray(Schema.create(INT))));
        DataFileReader<GenericRecord> avroFileReader = new DataFileReader(avroFile, datumReader);
        Assert.assertTrue(avroFileReader.hasNext());
        AvroKeyValue<CharSequence, List<Integer>> appleRecord = new AvroKeyValue(avroFileReader.next());
        Assert.assertNotNull(appleRecord.get());
        Assert.assertEquals("apple", appleRecord.getKey().toString());
        List<Integer> appleDocs = appleRecord.getValue();
        Assert.assertEquals(3, appleDocs.size());
        Assert.assertTrue(appleDocs.contains(1));
        Assert.assertTrue(appleDocs.contains(2));
        Assert.assertTrue(appleDocs.contains(3));
        Assert.assertTrue(avroFileReader.hasNext());
        AvroKeyValue<CharSequence, List<Integer>> bananaRecord = new AvroKeyValue(avroFileReader.next());
        Assert.assertNotNull(bananaRecord.get());
        Assert.assertEquals("banana", bananaRecord.getKey().toString());
        List<Integer> bananaDocs = bananaRecord.getValue();
        Assert.assertEquals(2, bananaDocs.size());
        Assert.assertTrue(bananaDocs.contains(1));
        Assert.assertTrue(bananaDocs.contains(2));
        Assert.assertTrue(avroFileReader.hasNext());
        AvroKeyValue<CharSequence, List<Integer>> carrotRecord = new AvroKeyValue(avroFileReader.next());
        Assert.assertEquals("carrot", carrotRecord.getKey().toString());
        List<Integer> carrotDocs = carrotRecord.getValue();
        Assert.assertEquals(1, carrotDocs.size());
        Assert.assertTrue(carrotDocs.contains(1));
        Assert.assertFalse(avroFileReader.hasNext());
        avroFileReader.close();
    }

    @Test
    public void testKeyValueInputMapOnly() throws IOException, ClassNotFoundException, InterruptedException {
        // Create a test input file.
        File inputFile = createInputFile();
        // Configure the job input.
        Job job = new Job();
        FileInputFormat.setInputPaths(job, new Path(inputFile.getAbsolutePath()));
        job.setInputFormatClass(AvroKeyValueInputFormat.class);
        AvroJob.setInputKeySchema(job, Schema.create(INT));
        AvroJob.setInputValueSchema(job, Schema.create(STRING));
        // Configure the identity mapper.
        AvroJob.setMapOutputKeySchema(job, Schema.create(INT));
        AvroJob.setMapOutputValueSchema(job, Schema.create(STRING));
        // Configure zero reducers.
        job.setNumReduceTasks(0);
        job.setOutputKeyClass(AvroKey.class);
        job.setOutputValueClass(AvroValue.class);
        // Configure the output format.
        job.setOutputFormatClass(AvroKeyValueOutputFormat.class);
        Path outputPath = new Path(mTempDir.getRoot().getPath(), "out-index");
        FileOutputFormat.setOutputPath(job, outputPath);
        // Run the job.
        Assert.assertTrue(job.waitForCompletion(true));
        // Verify that the output Avro container file has the expected data.
        File avroFile = new File(outputPath.toString(), "part-m-00000.avro");
        DatumReader<GenericRecord> datumReader = new org.apache.avro.specific.SpecificDatumReader(AvroKeyValue.getSchema(Schema.create(INT), Schema.create(STRING)));
        DataFileReader<GenericRecord> avroFileReader = new DataFileReader(avroFile, datumReader);
        Assert.assertTrue(avroFileReader.hasNext());
        AvroKeyValue<Integer, CharSequence> record1 = new AvroKeyValue(avroFileReader.next());
        Assert.assertNotNull(record1.get());
        Assert.assertEquals(1, record1.getKey().intValue());
        Assert.assertEquals("apple banana carrot", record1.getValue().toString());
        Assert.assertTrue(avroFileReader.hasNext());
        AvroKeyValue<Integer, CharSequence> record2 = new AvroKeyValue(avroFileReader.next());
        Assert.assertNotNull(record2.get());
        Assert.assertEquals(2, record2.getKey().intValue());
        Assert.assertEquals("apple banana", record2.getValue().toString());
        Assert.assertTrue(avroFileReader.hasNext());
        AvroKeyValue<Integer, CharSequence> record3 = new AvroKeyValue(avroFileReader.next());
        Assert.assertNotNull(record3.get());
        Assert.assertEquals(3, record3.getKey().intValue());
        Assert.assertEquals("apple", record3.getValue().toString());
        Assert.assertFalse(avroFileReader.hasNext());
        avroFileReader.close();
    }
}

