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
package org.apache.flink.formats.parquet.utils;


import GenericData.Array;
import GenericData.Record;
import ImmutableMap.Builder;
import Schema.Type.ARRAY;
import Schema.Type.MAP;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.IndexedRecord;
import org.apache.flink.core.fs.Path;
import org.apache.flink.shaded.guava18.com.google.common.collect.ImmutableList;
import org.apache.flink.shaded.guava18.com.google.common.collect.ImmutableMap;
import org.apache.flink.types.Row;
import org.apache.flink.util.Preconditions;
import org.apache.hadoop.conf.Configuration;
import org.apache.parquet.ParquetReadOptions;
import org.apache.parquet.avro.AvroSchemaConverter;
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.hadoop.util.HadoopInputFile;
import org.apache.parquet.io.InputFile;
import org.apache.parquet.schema.MessageType;
import org.junit.Assert;
import org.junit.Test;


/**
 * Simple test case for reading parquet records.
 */
public class ParquetRecordReaderTest extends TestUtil {
    private final Configuration testConfig = new Configuration();

    @Test
    public void testReadSimpleGroup() throws IOException {
        Long[] array = new Long[]{ 1L };
        GenericData.Record record = set("bar", "test").set("foo", 32L).set("arr", array).build();
        Path path = TestUtil.createTempParquetFile(TestUtil.tempRoot.getRoot(), TestUtil.SIMPLE_SCHEMA, Collections.singletonList(record));
        MessageType readSchema = new AvroSchemaConverter().convert(TestUtil.SIMPLE_SCHEMA);
        ParquetRecordReader<Row> rowReader = new ParquetRecordReader(new RowReadSupport(), readSchema);
        InputFile inputFile = HadoopInputFile.fromPath(new org.apache.hadoop.fs.Path(path.toUri()), testConfig);
        ParquetReadOptions options = ParquetReadOptions.builder().build();
        ParquetFileReader fileReader = new ParquetFileReader(inputFile, options);
        rowReader.initialize(fileReader, testConfig);
        Assert.assertFalse(rowReader.reachEnd());
        Row row = rowReader.nextRecord();
        Assert.assertEquals(3, row.getArity());
        Assert.assertEquals(32L, row.getField(0));
        Assert.assertEquals("test", row.getField(1));
        Assert.assertArrayEquals(array, ((Long[]) (row.getField(2))));
        Assert.assertTrue(rowReader.reachEnd());
    }

    @Test
    public void testReadMultipleSimpleGroup() throws IOException {
        Long[] array = new Long[]{ 1L };
        List<IndexedRecord> records = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            GenericData.Record record = set("bar", "test").set("foo", i).set("arr", array).build();
            records.add(record);
        }
        Path path = TestUtil.createTempParquetFile(TestUtil.tempRoot.getRoot(), TestUtil.SIMPLE_SCHEMA, records);
        MessageType readSchema = new AvroSchemaConverter().convert(TestUtil.SIMPLE_SCHEMA);
        ParquetRecordReader<Row> rowReader = new ParquetRecordReader(new RowReadSupport(), readSchema);
        InputFile inputFile = HadoopInputFile.fromPath(new org.apache.hadoop.fs.Path(path.toUri()), testConfig);
        ParquetReadOptions options = ParquetReadOptions.builder().build();
        ParquetFileReader fileReader = new ParquetFileReader(inputFile, options);
        rowReader.initialize(fileReader, testConfig);
        Assert.assertTrue((!(rowReader.reachEnd())));
        for (long i = 0; i < 100; i++) {
            Assert.assertFalse(rowReader.reachEnd());
            Row row = rowReader.nextRecord();
            Assert.assertEquals(3, row.getArity());
            Assert.assertEquals(i, row.getField(0));
            Assert.assertEquals("test", row.getField(1));
            Assert.assertArrayEquals(array, ((Long[]) (row.getField(2))));
        }
        Assert.assertTrue(rowReader.reachEnd());
    }

    @Test
    public void testReadNestedGroup() throws IOException {
        Schema schema = unWrapSchema(TestUtil.NESTED_SCHEMA.getField("bar").schema());
        GenericData.Record barRecord = new org.apache.avro.generic.GenericRecordBuilder(schema).set("spam", 31L).build();
        GenericData.Record record = new org.apache.avro.generic.GenericRecordBuilder(TestUtil.NESTED_SCHEMA).set("foo", 32L).set("bar", barRecord).build();
        Path path = TestUtil.createTempParquetFile(TestUtil.tempRoot.getRoot(), TestUtil.NESTED_SCHEMA, Collections.singletonList(record));
        MessageType readSchema = new AvroSchemaConverter().convert(TestUtil.NESTED_SCHEMA);
        ParquetRecordReader<Row> rowReader = new ParquetRecordReader(new RowReadSupport(), readSchema);
        InputFile inputFile = HadoopInputFile.fromPath(new org.apache.hadoop.fs.Path(path.toUri()), testConfig);
        ParquetReadOptions options = ParquetReadOptions.builder().build();
        ParquetFileReader fileReader = new ParquetFileReader(inputFile, options);
        rowReader.initialize(fileReader, testConfig);
        Assert.assertFalse(rowReader.reachEnd());
        Row row = rowReader.nextRecord();
        Assert.assertEquals(7, row.getArity());
        Assert.assertEquals(32L, row.getField(0));
        Assert.assertEquals(31L, getField(0));
        Assert.assertTrue(rowReader.reachEnd());
    }

    @Test
    public void testMapGroup() throws IOException {
        Preconditions.checkState(unWrapSchema(TestUtil.NESTED_SCHEMA.getField("spamMap").schema()).getType().equals(MAP));
        Builder<String, String> map = ImmutableMap.builder();
        map.put("testKey", "testValue");
        GenericRecord record = new org.apache.avro.generic.GenericRecordBuilder(TestUtil.NESTED_SCHEMA).set("foo", 32L).set("spamMap", map.build()).build();
        Path path = TestUtil.createTempParquetFile(TestUtil.tempRoot.getRoot(), TestUtil.NESTED_SCHEMA, Collections.singletonList(record));
        MessageType readSchema = new AvroSchemaConverter().convert(TestUtil.NESTED_SCHEMA);
        ParquetRecordReader<Row> rowReader = new ParquetRecordReader(new RowReadSupport(), readSchema);
        InputFile inputFile = HadoopInputFile.fromPath(new org.apache.hadoop.fs.Path(path.toUri()), testConfig);
        ParquetReadOptions options = ParquetReadOptions.builder().build();
        ParquetFileReader fileReader = new ParquetFileReader(inputFile, options);
        rowReader.initialize(fileReader, testConfig);
        Assert.assertFalse(rowReader.reachEnd());
        Row row = rowReader.nextRecord();
        Assert.assertEquals(7, row.getArity());
        Assert.assertEquals(32L, row.getField(0));
        Map<?, ?> result = ((Map<?, ?>) (row.getField(1)));
        Assert.assertEquals(result.get("testKey").toString(), "testValue");
        Assert.assertTrue(rowReader.reachEnd());
    }

    @Test
    public void testArrayGroup() throws IOException {
        Schema arraySchema = unWrapSchema(TestUtil.NESTED_SCHEMA.getField("arr").schema());
        Preconditions.checkState(arraySchema.getType().equals(ARRAY));
        List<Long> arrayData = new ArrayList<>();
        arrayData.add(1L);
        arrayData.add(1000L);
        List<String> arrayString = new ArrayList<>();
        arrayString.add("abcd");
        @SuppressWarnings("unchecked")
        GenericData.Array array = new GenericData.Array(arraySchema, arrayData);
        GenericRecord record = new org.apache.avro.generic.GenericRecordBuilder(TestUtil.NESTED_SCHEMA).set("foo", 32L).set("arr", array).set("strArray", arrayString).build();
        Path path = TestUtil.createTempParquetFile(TestUtil.tempRoot.getRoot(), TestUtil.NESTED_SCHEMA, Collections.singletonList(record));
        MessageType readSchema = new AvroSchemaConverter().convert(TestUtil.NESTED_SCHEMA);
        ParquetRecordReader<Row> rowReader = new ParquetRecordReader(new RowReadSupport(), readSchema);
        InputFile inputFile = HadoopInputFile.fromPath(new org.apache.hadoop.fs.Path(path.toUri()), testConfig);
        ParquetReadOptions options = ParquetReadOptions.builder().build();
        ParquetFileReader fileReader = new ParquetFileReader(inputFile, options);
        rowReader.initialize(fileReader, testConfig);
        Assert.assertFalse(rowReader.reachEnd());
        Row row = rowReader.nextRecord();
        Assert.assertEquals(7, row.getArity());
        Assert.assertEquals(32L, row.getField(0));
        Long[] result = ((Long[]) (row.getField(3)));
        Assert.assertEquals(1L, result[0].longValue());
        Assert.assertEquals(1000L, result[1].longValue());
        String[] strResult = ((String[]) (row.getField(4)));
        Assert.assertEquals("abcd", strResult[0]);
    }

    @Test
    public void testNestedMapGroup() throws IOException {
        Schema nestedMapSchema = unWrapSchema(TestUtil.NESTED_SCHEMA.getField("nestedMap").schema());
        Preconditions.checkState(nestedMapSchema.getType().equals(MAP));
        Schema mapValueSchema = nestedMapSchema.getValueType();
        GenericRecord mapValue = set("value", "nested_value").build();
        Builder<String, GenericRecord> map = ImmutableMap.builder();
        map.put("testKey", mapValue);
        GenericRecord record = new org.apache.avro.generic.GenericRecordBuilder(TestUtil.NESTED_SCHEMA).set("nestedMap", map.build()).set("foo", 34L).build();
        Path path = TestUtil.createTempParquetFile(TestUtil.tempRoot.getRoot(), TestUtil.NESTED_SCHEMA, Collections.singletonList(record));
        MessageType readSchema = new AvroSchemaConverter().convert(TestUtil.NESTED_SCHEMA);
        ParquetRecordReader<Row> rowReader = new ParquetRecordReader(new RowReadSupport(), readSchema);
        InputFile inputFile = HadoopInputFile.fromPath(new org.apache.hadoop.fs.Path(path.toUri()), testConfig);
        ParquetReadOptions options = ParquetReadOptions.builder().build();
        ParquetFileReader fileReader = new ParquetFileReader(inputFile, options);
        rowReader.initialize(fileReader, testConfig);
        Assert.assertFalse(rowReader.reachEnd());
        Row row = rowReader.nextRecord();
        Assert.assertEquals(7, row.getArity());
        Assert.assertEquals(34L, row.getField(0));
        Map result = ((Map) (row.getField(5)));
        Row nestedRow = ((Row) (result.get("testKey")));
        Assert.assertEquals("nested", nestedRow.getField(0));
        Assert.assertEquals("nested_value", nestedRow.getField(1));
    }

    @Test
    public void testNestedArrayGroup() throws IOException {
        Schema nestedArraySchema = unWrapSchema(TestUtil.NESTED_SCHEMA.getField("nestedArray").schema());
        Preconditions.checkState(nestedArraySchema.getType().equals(ARRAY));
        Schema arrayItemSchema = nestedArraySchema.getElementType();
        GenericRecord item = set("value", "nested_value").build();
        ImmutableList.Builder<GenericRecord> list = ImmutableList.builder();
        list.add(item);
        GenericRecord record = new org.apache.avro.generic.GenericRecordBuilder(TestUtil.NESTED_SCHEMA).set("nestedArray", list.build()).set("foo", 34L).build();
        Path path = TestUtil.createTempParquetFile(TestUtil.tempRoot.getRoot(), TestUtil.NESTED_SCHEMA, Collections.singletonList(record));
        MessageType readSchema = new AvroSchemaConverter().convert(TestUtil.NESTED_SCHEMA);
        ParquetRecordReader<Row> rowReader = new ParquetRecordReader(new RowReadSupport(), readSchema);
        InputFile inputFile = HadoopInputFile.fromPath(new org.apache.hadoop.fs.Path(path.toUri()), testConfig);
        ParquetReadOptions options = ParquetReadOptions.builder().build();
        ParquetFileReader fileReader = new ParquetFileReader(inputFile, options);
        rowReader.initialize(fileReader, testConfig);
        Assert.assertFalse(rowReader.reachEnd());
        Row row = rowReader.nextRecord();
        Assert.assertEquals(7, row.getArity());
        Assert.assertEquals(34L, row.getField(0));
        Object[] result = ((Object[]) (row.getField(6)));
        Assert.assertEquals(1, result.length);
        Row nestedRow = ((Row) (result[0]));
        Assert.assertEquals("nested", nestedRow.getField(0));
        Assert.assertEquals("nested_value", nestedRow.getField(1));
    }
}

