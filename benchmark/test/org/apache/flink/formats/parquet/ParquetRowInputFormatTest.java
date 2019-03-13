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
package org.apache.flink.formats.parquet;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.avro.generic.IndexedRecord;
import org.apache.avro.specific.SpecificRecord;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.core.fs.FileInputSplit;
import org.apache.flink.core.fs.Path;
import org.apache.flink.formats.parquet.generated.SimpleRecord;
import org.apache.flink.formats.parquet.utils.TestUtil;
import org.apache.flink.types.Row;
import org.apache.flink.util.InstantiationUtil;
import org.apache.parquet.avro.AvroSchemaConverter;
import org.apache.parquet.schema.MessageType;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Simple test case for reading {@link org.apache.flink.types.Row} from Parquet files.
 */
public class ParquetRowInputFormatTest {
    private static final AvroSchemaConverter SCHEMA_CONVERTER = new AvroSchemaConverter();

    @ClassRule
    public static TemporaryFolder tempRoot = new TemporaryFolder();

    @Test
    public void testReadRowFromSimpleRecord() throws IOException {
        Tuple3<Class<? extends SpecificRecord>, SpecificRecord, Row> simple = TestUtil.getSimpleRecordTestData();
        Path path = TestUtil.createTempParquetFile(ParquetRowInputFormatTest.tempRoot.getRoot(), TestUtil.SIMPLE_SCHEMA, Arrays.asList(simple.f1, simple.f1));
        MessageType simpleType = ParquetRowInputFormatTest.SCHEMA_CONVERTER.convert(TestUtil.SIMPLE_SCHEMA);
        ParquetRowInputFormat inputFormat = new ParquetRowInputFormat(path, simpleType);
        inputFormat.setRuntimeContext(TestUtil.getMockRuntimeContext());
        FileInputSplit[] splits = inputFormat.createInputSplits(1);
        Assert.assertEquals(1, splits.length);
        inputFormat.open(splits[0]);
        Row row = inputFormat.nextRecord(null);
        Assert.assertNotNull(row);
        Assert.assertEquals(simple.f2, row);
        row = inputFormat.nextRecord(null);
        Assert.assertNotNull(row);
        Assert.assertEquals(simple.f2, row);
        Assert.assertTrue(inputFormat.reachedEnd());
    }

    @Test
    public void testMultiSplitsSimpleRecord() throws IOException {
        List<IndexedRecord> records = new ArrayList<>();
        for (long i = 0; i < 1000; i++) {
            records.add(SimpleRecord.newBuilder().setFoo(i).setBar("row").setArr(Collections.singletonList(1L)).build());
        }
        File tempFolder = ParquetRowInputFormatTest.tempRoot.newFolder();
        // created a parquet file with 10 row groups. Each row group has 100 records
        TestUtil.createTempParquetFile(tempFolder, TestUtil.SIMPLE_SCHEMA, records);
        TestUtil.createTempParquetFile(tempFolder, TestUtil.SIMPLE_SCHEMA, records);
        TestUtil.createTempParquetFile(tempFolder, TestUtil.SIMPLE_SCHEMA, records);
        MessageType simpleType = ParquetRowInputFormatTest.SCHEMA_CONVERTER.convert(TestUtil.SIMPLE_SCHEMA);
        ParquetRowInputFormat inputFormat = new ParquetRowInputFormat(new Path(tempFolder.getPath()), simpleType);
        inputFormat.setRuntimeContext(TestUtil.getMockRuntimeContext());
        FileInputSplit[] splits = inputFormat.createInputSplits(3);
        Assert.assertEquals(3, splits.length);
        for (FileInputSplit s : splits) {
            inputFormat.open(s);
            long cnt = 0;
            while (!(inputFormat.reachedEnd())) {
                Row row = inputFormat.nextRecord(null);
                Assert.assertNotNull(row);
                Assert.assertEquals(cnt, ((long) (row.getField(0))));
                Assert.assertEquals("row", row.getField(1));
                Assert.assertArrayEquals(new Long[]{ 1L }, ((Long[]) (row.getField(2))));
                cnt++;
            } 
            Assert.assertEquals(1000, cnt);
        }
    }

    @Test
    public void testGetCurrentState() throws IOException {
        List<IndexedRecord> records = new ArrayList<>();
        for (long i = 0; i < 1000; i++) {
            records.add(SimpleRecord.newBuilder().setFoo(i).setBar("row").setArr(Collections.singletonList(1L)).build());
        }
        // created a parquet file with 10 row groups. Each row group has 100 records
        Path path = TestUtil.createTempParquetFile(ParquetRowInputFormatTest.tempRoot.newFolder(), TestUtil.SIMPLE_SCHEMA, records);
        MessageType simpleType = ParquetRowInputFormatTest.SCHEMA_CONVERTER.convert(TestUtil.SIMPLE_SCHEMA);
        ParquetRowInputFormat inputFormat = new ParquetRowInputFormat(path, simpleType);
        inputFormat.setRuntimeContext(TestUtil.getMockRuntimeContext());
        FileInputSplit[] splits = inputFormat.createInputSplits(1);
        Assert.assertEquals(1, splits.length);
        inputFormat.open(splits[0]);
        // get read position and check it (block 0, record 0)
        Tuple2<Long, Long> checkpointedPos = inputFormat.getCurrentState();
        Assert.assertEquals(0L, checkpointedPos.f0.longValue());
        Assert.assertEquals(0L, checkpointedPos.f1.longValue());
        // check if this is the end
        Assert.assertFalse(inputFormat.reachedEnd());
        // get read position and check again
        checkpointedPos = inputFormat.getCurrentState();
        Assert.assertEquals(0L, checkpointedPos.f0.longValue());
        Assert.assertEquals(0L, checkpointedPos.f1.longValue());
        // read 199 records
        for (int i = 0; i < 199; i++) {
            Assert.assertFalse(inputFormat.reachedEnd());
            Assert.assertNotNull(inputFormat.nextRecord(null));
        }
        // get read position and check it (block 1, record 99)
        checkpointedPos = inputFormat.getCurrentState();
        Assert.assertEquals(1L, checkpointedPos.f0.longValue());
        Assert.assertEquals(99L, checkpointedPos.f1.longValue());
        // check if this is the end
        Assert.assertFalse(inputFormat.reachedEnd());
        // get read position and check again
        checkpointedPos = inputFormat.getCurrentState();
        Assert.assertEquals(1L, checkpointedPos.f0.longValue());
        Assert.assertEquals(99L, checkpointedPos.f1.longValue());
        // read one more record
        Assert.assertNotNull(inputFormat.nextRecord(null));
        // get read position and check it (block 2, record 0)
        checkpointedPos = inputFormat.getCurrentState();
        Assert.assertEquals(2L, checkpointedPos.f0.longValue());
        Assert.assertEquals(0L, checkpointedPos.f1.longValue());
        // check if this is the end
        Assert.assertFalse(inputFormat.reachedEnd());
        // get read position and check again
        checkpointedPos = inputFormat.getCurrentState();
        Assert.assertEquals(2L, checkpointedPos.f0.longValue());
        Assert.assertEquals(0L, checkpointedPos.f1.longValue());
        // read one more record
        Assert.assertNotNull(inputFormat.nextRecord(null));
        // get read position and check it (block 2, record 1)
        checkpointedPos = inputFormat.getCurrentState();
        Assert.assertEquals(2L, checkpointedPos.f0.longValue());
        Assert.assertEquals(1L, checkpointedPos.f1.longValue());
        // check if this is the end
        Assert.assertFalse(inputFormat.reachedEnd());
        // get read position and check again
        checkpointedPos = inputFormat.getCurrentState();
        Assert.assertEquals(2L, checkpointedPos.f0.longValue());
        Assert.assertEquals(1L, checkpointedPos.f1.longValue());
        // read remaining 798 records
        for (int i = 0; i < 799; i++) {
            Assert.assertFalse(inputFormat.reachedEnd());
            Assert.assertNotNull(inputFormat.nextRecord(null));
        }
        // get read position and check it (block -1, record -1, marks a fully consumed split)
        checkpointedPos = inputFormat.getCurrentState();
        Assert.assertEquals((-1L), checkpointedPos.f0.longValue());
        Assert.assertEquals((-1L), checkpointedPos.f1.longValue());
        // we are at the end
        Assert.assertTrue(inputFormat.reachedEnd());
        // get read position and check again
        checkpointedPos = inputFormat.getCurrentState();
        Assert.assertEquals((-1L), checkpointedPos.f0.longValue());
        Assert.assertEquals((-1L), checkpointedPos.f1.longValue());
    }

    @Test
    public void testRecoverySimpleRecord() throws IOException {
        List<IndexedRecord> records = new ArrayList<>();
        for (long i = 0; i < 1000; i++) {
            records.add(SimpleRecord.newBuilder().setFoo(i).setBar("row").setArr(Collections.singletonList(1L)).build());
        }
        // created a parquet file with 10 row groups. Each row group has 100 records
        Path path = TestUtil.createTempParquetFile(ParquetRowInputFormatTest.tempRoot.newFolder(), TestUtil.SIMPLE_SCHEMA, records);
        MessageType simpleType = ParquetRowInputFormatTest.SCHEMA_CONVERTER.convert(TestUtil.SIMPLE_SCHEMA);
        ParquetRowInputFormat inputFormat = new ParquetRowInputFormat(path, simpleType);
        inputFormat.setRuntimeContext(TestUtil.getMockRuntimeContext());
        FileInputSplit[] splits = inputFormat.createInputSplits(1);
        Assert.assertEquals(1, splits.length);
        inputFormat.open(splits[0]);
        // take a checkpoint
        Tuple2<Long, Long> checkpointedPos = inputFormat.getCurrentState();
        // check correct position (block 0, record 0)
        Assert.assertEquals(0L, checkpointedPos.f0.longValue());
        Assert.assertEquals(0L, checkpointedPos.f1.longValue());
        inputFormat.reopen(splits[0], checkpointedPos);
        // read 252 records
        long cnt = 0;
        while (cnt < 252) {
            Assert.assertFalse(inputFormat.reachedEnd());
            Row row = inputFormat.nextRecord(null);
            Assert.assertNotNull(row);
            Assert.assertEquals(cnt, ((long) (row.getField(0))));
            Assert.assertEquals("row", row.getField(1));
            Assert.assertArrayEquals(new Long[]{ 1L }, ((Long[]) (row.getField(2))));
            cnt++;
        } 
        // take a checkpoint
        checkpointedPos = inputFormat.getCurrentState();
        // check correct position (3rd block, 52th record)
        Assert.assertEquals(2L, checkpointedPos.f0.longValue());
        Assert.assertEquals(52L, checkpointedPos.f1.longValue());
        // continue reading for another 252 records
        while (cnt < 504) {
            Assert.assertFalse(inputFormat.reachedEnd());
            Row row = inputFormat.nextRecord(null);
            Assert.assertNotNull(row);
            Assert.assertEquals(cnt, ((long) (row.getField(0))));
            Assert.assertEquals("row", row.getField(1));
            Assert.assertArrayEquals(new Long[]{ 1L }, ((Long[]) (row.getField(2))));
            cnt++;
        } 
        // reset to checkpointed position
        inputFormat.close();
        inputFormat.reopen(splits[0], checkpointedPos);
        // reset counter and continue reading until the end
        cnt = 252;
        while (!(inputFormat.reachedEnd())) {
            Row row = inputFormat.nextRecord(null);
            Assert.assertNotNull(row);
            Assert.assertEquals(cnt, ((long) (row.getField(0))));
            Assert.assertEquals("row", row.getField(1));
            Assert.assertArrayEquals(new Long[]{ 1L }, ((Long[]) (row.getField(2))));
            cnt++;
        } 
        Assert.assertTrue(inputFormat.reachedEnd());
        // reset to end of the split
        inputFormat.close();
        inputFormat.reopen(splits[0], Tuple2.of((-1L), (-1L)));
        Assert.assertTrue(inputFormat.reachedEnd());
        // reset to start of last block
        inputFormat.close();
        inputFormat.reopen(splits[0], Tuple2.of(9L, 0L));
        cnt = 900;
        while (!(inputFormat.reachedEnd())) {
            Row row = inputFormat.nextRecord(null);
            Assert.assertNotNull(row);
            Assert.assertEquals(cnt, ((long) (row.getField(0))));
            Assert.assertEquals("row", row.getField(1));
            Assert.assertArrayEquals(new Long[]{ 1L }, ((Long[]) (row.getField(2))));
            cnt++;
        } 
        Assert.assertTrue(inputFormat.reachedEnd());
        // reset to end of 8th block
        inputFormat.close();
        inputFormat.reopen(splits[0], Tuple2.of(8L, 99L));
        cnt = 899;
        while (!(inputFormat.reachedEnd())) {
            Row row = inputFormat.nextRecord(null);
            Assert.assertNotNull(row);
            Assert.assertEquals(cnt, ((long) (row.getField(0))));
            Assert.assertEquals("row", row.getField(1));
            Assert.assertArrayEquals(new Long[]{ 1L }, ((Long[]) (row.getField(2))));
            cnt++;
        } 
        Assert.assertTrue(inputFormat.reachedEnd());
    }

    @Test
    public void testReadRowFromNestedRecord() throws IOException {
        Tuple3<Class<? extends SpecificRecord>, SpecificRecord, Row> nested = TestUtil.getNestedRecordTestData();
        Path path = TestUtil.createTempParquetFile(ParquetRowInputFormatTest.tempRoot.newFolder(), TestUtil.NESTED_SCHEMA, Collections.singletonList(nested.f1));
        MessageType nestedType = ParquetRowInputFormatTest.SCHEMA_CONVERTER.convert(TestUtil.NESTED_SCHEMA);
        ParquetRowInputFormat inputFormat = new ParquetRowInputFormat(path, nestedType);
        inputFormat.setRuntimeContext(TestUtil.getMockRuntimeContext());
        FileInputSplit[] splits = inputFormat.createInputSplits(1);
        Assert.assertEquals(1, splits.length);
        inputFormat.open(splits[0]);
        Row row = inputFormat.nextRecord(null);
        Assert.assertNotNull(row);
        Assert.assertEquals(7, row.getArity());
        Assert.assertEquals(nested.f2.getField(0), row.getField(0));
        Assert.assertEquals(nested.f2.getField(1), row.getField(1));
        Assert.assertArrayEquals(((Long[]) (nested.f2.getField(3))), ((Long[]) (row.getField(3))));
        Assert.assertArrayEquals(((String[]) (nested.f2.getField(4))), ((String[]) (row.getField(4))));
        Assert.assertEquals(nested.f2.getField(5), row.getField(5));
        Assert.assertArrayEquals(((Row[]) (nested.f2.getField(6))), ((Row[]) (row.getField(6))));
    }

    @Test
    public void testProjectedRowFromNestedRecord() throws Exception {
        Tuple3<Class<? extends SpecificRecord>, SpecificRecord, Row> nested = TestUtil.getNestedRecordTestData();
        Path path = TestUtil.createTempParquetFile(ParquetRowInputFormatTest.tempRoot.newFolder(), TestUtil.NESTED_SCHEMA, Collections.singletonList(nested.f1));
        MessageType nestedType = ParquetRowInputFormatTest.SCHEMA_CONVERTER.convert(TestUtil.NESTED_SCHEMA);
        ParquetRowInputFormat inputFormat = new ParquetRowInputFormat(path, nestedType);
        inputFormat.setRuntimeContext(TestUtil.getMockRuntimeContext());
        inputFormat.selectFields(new String[]{ "bar", "nestedMap" });
        FileInputSplit[] splits = inputFormat.createInputSplits(1);
        Assert.assertEquals(1, splits.length);
        inputFormat.open(splits[0]);
        Row row = inputFormat.nextRecord(null);
        Assert.assertNotNull(row);
        Assert.assertEquals(2, row.getArity());
        Assert.assertEquals(nested.f2.getField(2), row.getField(0));
        Assert.assertEquals(nested.f2.getField(5), row.getField(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidProjectionOfNestedRecord() throws Exception {
        Tuple3<Class<? extends SpecificRecord>, SpecificRecord, Row> nested = TestUtil.getNestedRecordTestData();
        Path path = TestUtil.createTempParquetFile(ParquetRowInputFormatTest.tempRoot.newFolder(), TestUtil.NESTED_SCHEMA, Collections.singletonList(nested.f1));
        MessageType nestedType = ParquetRowInputFormatTest.SCHEMA_CONVERTER.convert(TestUtil.NESTED_SCHEMA);
        ParquetRowInputFormat inputFormat = new ParquetRowInputFormat(path, nestedType);
        inputFormat.setRuntimeContext(TestUtil.getMockRuntimeContext());
        inputFormat.selectFields(new String[]{ "bar", "celona" });
    }

    @Test
    public void testSerialization() throws Exception {
        Tuple3<Class<? extends SpecificRecord>, SpecificRecord, Row> simple = TestUtil.getSimpleRecordTestData();
        Path path = TestUtil.createTempParquetFile(ParquetRowInputFormatTest.tempRoot.newFolder(), TestUtil.SIMPLE_SCHEMA, Collections.singletonList(simple.f1));
        MessageType simpleType = ParquetRowInputFormatTest.SCHEMA_CONVERTER.convert(TestUtil.SIMPLE_SCHEMA);
        ParquetRowInputFormat inputFormat = new ParquetRowInputFormat(path, simpleType);
        byte[] bytes = InstantiationUtil.serializeObject(inputFormat);
        ParquetRowInputFormat copy = InstantiationUtil.deserializeObject(bytes, getClass().getClassLoader());
        copy.setRuntimeContext(TestUtil.getMockRuntimeContext());
        FileInputSplit[] splits = copy.createInputSplits(1);
        Assert.assertEquals(1, splits.length);
        copy.open(splits[0]);
        Row row = copy.nextRecord(null);
        Assert.assertNotNull(row);
        Assert.assertEquals(simple.f2, row);
    }
}

