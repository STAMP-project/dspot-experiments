/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.parquet;


import ConvertAvroToParquet.RECORD_COUNT_ATTRIBUTE;
import ConvertAvroToParquet.SUCCESS;
import CoreAttributes.FILENAME;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.avro.generic.GenericRecord;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.example.GroupReadSupport;
import org.apache.parquet.hadoop.metadata.BlockMetaData;
import org.apache.parquet.hadoop.metadata.ParquetMetadata;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for ConvertAvroToParquet processor
 */
public class TestConvertAvroToParquet {
    private ConvertAvroToParquet processor;

    private TestRunner runner;

    private List<GenericRecord> records = new ArrayList<>();

    File tmpAvro = new File("target/test.avro");

    File tmpParquet = new File("target/test.parquet");

    @Test
    public void test_Processor() throws Exception {
        FileInputStream fileInputStream = new FileInputStream(tmpAvro);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int readedBytes;
        byte[] buf = new byte[1024];
        while ((readedBytes = fileInputStream.read(buf)) > 0) {
            out.write(buf, 0, readedBytes);
        } 
        out.close();
        Map<String, String> attributes = new HashMap<String, String>() {
            {
                put(FILENAME.key(), "test.avro");
            }
        };
        runner.enqueue(out.toByteArray(), attributes);
        runner.run();
        runner.assertAllFlowFilesTransferred(SUCCESS, 1);
        MockFlowFile resultFlowFile = runner.getFlowFilesForRelationship(SUCCESS).get(0);
        // assert meta data
        Assert.assertEquals("1", resultFlowFile.getAttribute(RECORD_COUNT_ATTRIBUTE));
        Assert.assertEquals("test.parquet", resultFlowFile.getAttribute(FILENAME.key()));
    }

    @Test
    public void test_Meta_Info() throws Exception {
        FileInputStream fileInputStream = new FileInputStream(tmpAvro);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int readedBytes;
        byte[] buf = new byte[1024];
        while ((readedBytes = fileInputStream.read(buf)) > 0) {
            out.write(buf, 0, readedBytes);
        } 
        out.close();
        Map<String, String> attributes = new HashMap<String, String>() {
            {
                put(FILENAME.key(), "test.avro");
            }
        };
        runner.enqueue(out.toByteArray(), attributes);
        runner.run();
        MockFlowFile resultFlowFile = runner.getFlowFilesForRelationship(SUCCESS).get(0);
        // Save the flowfile
        byte[] resultContents = runner.getContentAsByteArray(resultFlowFile);
        FileOutputStream fos = new FileOutputStream(tmpParquet);
        fos.write(resultContents);
        fos.flush();
        fos.close();
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.getLocal(conf);
        ParquetMetadata metaData;
        metaData = ParquetFileReader.readFooter(conf, new Path(tmpParquet.getAbsolutePath()), NO_FILTER);
        // #number of records
        long nParquetRecords = 0;
        for (BlockMetaData meta : metaData.getBlocks()) {
            nParquetRecords += meta.getRowCount();
        }
        long nAvroRecord = records.size();
        Assert.assertEquals(nParquetRecords, nAvroRecord);
    }

    @Test
    public void test_Data() throws Exception {
        FileInputStream fileInputStream = new FileInputStream(tmpAvro);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int readedBytes;
        byte[] buf = new byte[1024];
        while ((readedBytes = fileInputStream.read(buf)) > 0) {
            out.write(buf, 0, readedBytes);
        } 
        out.close();
        Map<String, String> attributes = new HashMap<String, String>() {
            {
                put(FILENAME.key(), "test.avro");
            }
        };
        runner.enqueue(out.toByteArray(), attributes);
        runner.run();
        MockFlowFile resultFlowFile = runner.getFlowFilesForRelationship(SUCCESS).get(0);
        // Save the flowfile
        byte[] resultContents = runner.getContentAsByteArray(resultFlowFile);
        FileOutputStream fos = new FileOutputStream(tmpParquet);
        fos.write(resultContents);
        fos.flush();
        fos.close();
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.getLocal(conf);
        ParquetReader<Group> reader = ParquetReader.builder(new GroupReadSupport(), new Path(tmpParquet.getAbsolutePath())).withConf(conf).build();
        List<Group> parquetRecords = new ArrayList<Group>();
        Group current;
        current = reader.read();
        while (current != null) {
            Assert.assertTrue((current instanceof Group));
            parquetRecords.add(current);
            current = reader.read();
        } 
        Group firstRecord = parquetRecords.get(0);
        // Primitive
        Assert.assertEquals(firstRecord.getInteger("myint", 0), 1);
        Assert.assertEquals(firstRecord.getLong("mylong", 0), 2);
        Assert.assertEquals(firstRecord.getBoolean("myboolean", 0), true);
        Assert.assertEquals(firstRecord.getFloat("myfloat", 0), 3.1, 1.0E-4);
        Assert.assertEquals(firstRecord.getDouble("mydouble", 0), 4.1, 0.001);
        Assert.assertEquals(firstRecord.getString("mybytes", 0), "hello");
        Assert.assertEquals(firstRecord.getString("mystring", 0), "hello");
        // Nested
        Assert.assertEquals(firstRecord.getGroup("mynestedrecord", 0).getInteger("mynestedint", 0), 1);
        // Array
        Assert.assertEquals(firstRecord.getGroup("myarray", 0).getGroup("list", 0).getInteger("element", 0), 1);
        Assert.assertEquals(firstRecord.getGroup("myarray", 0).getGroup("list", 1).getInteger("element", 0), 2);
        // Map
        Assert.assertEquals(firstRecord.getGroup("mymap", 0).getGroup("map", 0).getInteger("value", 0), 1);
        Assert.assertEquals(firstRecord.getGroup("mymap", 0).getGroup("map", 1).getInteger("value", 0), 2);
        // Fixed
        Assert.assertEquals(firstRecord.getString("myfixed", 0), "A");
    }
}

