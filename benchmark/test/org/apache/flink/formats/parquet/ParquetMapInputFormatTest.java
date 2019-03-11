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


import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.avro.specific.SpecificRecord;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.core.fs.FileInputSplit;
import org.apache.flink.core.fs.Path;
import org.apache.flink.formats.parquet.utils.TestUtil;
import org.apache.flink.types.Row;
import org.apache.parquet.avro.AvroSchemaConverter;
import org.apache.parquet.schema.MessageType;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Test cases for reading Map from Parquet files.
 */
public class ParquetMapInputFormatTest {
    private static final AvroSchemaConverter SCHEMA_CONVERTER = new AvroSchemaConverter();

    @ClassRule
    public static TemporaryFolder tempRoot = new TemporaryFolder();

    @Test
    @SuppressWarnings("unchecked")
    public void testReadMapFromNestedRecord() throws IOException {
        Tuple3<Class<? extends SpecificRecord>, SpecificRecord, Row> nested = TestUtil.getNestedRecordTestData();
        Path path = TestUtil.createTempParquetFile(ParquetMapInputFormatTest.tempRoot.getRoot(), TestUtil.NESTED_SCHEMA, Collections.singletonList(nested.f1));
        MessageType nestedType = ParquetMapInputFormatTest.SCHEMA_CONVERTER.convert(TestUtil.NESTED_SCHEMA);
        ParquetMapInputFormat inputFormat = new ParquetMapInputFormat(path, nestedType);
        inputFormat.setRuntimeContext(TestUtil.getMockRuntimeContext());
        FileInputSplit[] splits = inputFormat.createInputSplits(1);
        Assert.assertEquals(1, splits.length);
        inputFormat.open(splits[0]);
        Map map = inputFormat.nextRecord(null);
        Assert.assertNotNull(map);
        Assert.assertEquals(5, map.size());
        Assert.assertArrayEquals(((Long[]) (nested.f2.getField(3))), ((Long[]) (map.get("arr"))));
        Assert.assertArrayEquals(((String[]) (nested.f2.getField(4))), ((String[]) (map.get("strArray"))));
        Map<String, String> mapItem = ((Map<String, String>) (((Map) (map.get("nestedMap"))).get("mapItem")));
        Assert.assertEquals(2, mapItem.size());
        Assert.assertEquals("map", mapItem.get("type"));
        Assert.assertEquals("hashMap", mapItem.get("value"));
        List<Map<String, String>> nestedArray = ((List<Map<String, String>>) (map.get("nestedArray")));
        Assert.assertEquals(1, nestedArray.size());
        Assert.assertEquals("color", nestedArray.get(0).get("type"));
        Assert.assertEquals("yellow", nestedArray.get(0).get("value"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testProjectedReadMapFromNestedRecord() throws IOException {
        Tuple3<Class<? extends SpecificRecord>, SpecificRecord, Row> nested = TestUtil.getNestedRecordTestData();
        Path path = TestUtil.createTempParquetFile(ParquetMapInputFormatTest.tempRoot.getRoot(), TestUtil.NESTED_SCHEMA, Collections.singletonList(nested.f1));
        MessageType nestedType = ParquetMapInputFormatTest.SCHEMA_CONVERTER.convert(TestUtil.NESTED_SCHEMA);
        ParquetMapInputFormat inputFormat = new ParquetMapInputFormat(path, nestedType);
        inputFormat.selectFields(Collections.singletonList("nestedMap").toArray(new String[0]));
        inputFormat.setRuntimeContext(TestUtil.getMockRuntimeContext());
        FileInputSplit[] splits = inputFormat.createInputSplits(1);
        Assert.assertEquals(1, splits.length);
        inputFormat.open(splits[0]);
        Map map = inputFormat.nextRecord(null);
        Assert.assertNotNull(map);
        Assert.assertEquals(1, map.size());
        Map<String, String> mapItem = ((Map<String, String>) (((Map) (map.get("nestedMap"))).get("mapItem")));
        Assert.assertEquals(2, mapItem.size());
        Assert.assertEquals("map", mapItem.get("type"));
        Assert.assertEquals("hashMap", mapItem.get("value"));
    }
}

