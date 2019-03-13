/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.nifi.processors.kite;


import AbstractKiteConvertProcessor.COMPRESSION_TYPE;
import CodecType.BZIP2;
import ConvertAvroSchema.INPUT_SCHEMA;
import ConvertAvroSchema.OUTPUT_SCHEMA;
import com.google.common.collect.Lists;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.file.DataFileStream;
import org.apache.avro.generic.GenericData.Record;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;


public class TestConvertAvroSchema {
    public static final Schema INPUT_SCHEMA = SchemaBuilder.record("InputTest").fields().requiredString("id").requiredString("primaryColor").optionalString("secondaryColor").optionalString("price").endRecord();

    public static final Schema OUTPUT_SCHEMA = SchemaBuilder.record("Test").fields().requiredLong("id").requiredString("color").optionalDouble("price").endRecord();

    public static final String MAPPING = "[{\"source\":\"primaryColor\", \"target\":\"color\"}]";

    public static final String FAILURE_SUMMARY = "Cannot convert free to double";

    @Test
    public void testBasicConversion() throws IOException {
        TestRunner runner = TestRunners.newTestRunner(ConvertAvroSchema.class);
        runner.assertNotValid();
        runner.setProperty(ConvertAvroSchema.INPUT_SCHEMA, TestConvertAvroSchema.INPUT_SCHEMA.toString());
        runner.setProperty(ConvertAvroSchema.OUTPUT_SCHEMA, TestConvertAvroSchema.OUTPUT_SCHEMA.toString());
        Locale locale = Locale.getDefault();
        runner.setProperty("primaryColor", "color");
        runner.assertValid();
        NumberFormat format = NumberFormat.getInstance(locale);
        // Two valid rows, and one invalid because "free" is not a double.
        Record goodRecord1 = dataBasic("1", "blue", null, null);
        Record goodRecord2 = dataBasic("2", "red", "yellow", format.format(5.5));
        Record badRecord = dataBasic("3", "red", "yellow", "free");
        List<Record> input = Lists.newArrayList(goodRecord1, goodRecord2, badRecord);
        runner.enqueue(TestUtil.streamFor(input));
        runner.run();
        long converted = runner.getCounterValue("Converted records");
        long errors = runner.getCounterValue("Conversion errors");
        Assert.assertEquals("Should convert 2 rows", 2, converted);
        Assert.assertEquals("Should reject 1 rows", 1, errors);
        runner.assertTransferCount("success", 1);
        runner.assertTransferCount("failure", 1);
        MockFlowFile incompatible = runner.getFlowFilesForRelationship("failure").get(0);
        GenericDatumReader<Record> reader = new GenericDatumReader<Record>(TestConvertAvroSchema.INPUT_SCHEMA);
        DataFileStream<Record> stream = new DataFileStream<Record>(new ByteArrayInputStream(runner.getContentAsByteArray(incompatible)), reader);
        int count = 0;
        for (Record r : stream) {
            Assert.assertEquals(badRecord, r);
            count++;
        }
        stream.close();
        Assert.assertEquals(1, count);
        Assert.assertEquals("Should accumulate error messages", TestConvertAvroSchema.FAILURE_SUMMARY, incompatible.getAttribute("errors"));
        GenericDatumReader<Record> successReader = new GenericDatumReader<Record>(TestConvertAvroSchema.OUTPUT_SCHEMA);
        DataFileStream<Record> successStream = new DataFileStream<Record>(new ByteArrayInputStream(runner.getContentAsByteArray(runner.getFlowFilesForRelationship("success").get(0))), successReader);
        count = 0;
        for (Record r : successStream) {
            if (count == 0) {
                Assert.assertEquals(convertBasic(goodRecord1, locale), r);
            } else {
                Assert.assertEquals(convertBasic(goodRecord2, locale), r);
            }
            count++;
        }
        successStream.close();
        Assert.assertEquals(2, count);
    }

    @Test
    public void testBasicConversionWithCompression() throws IOException {
        TestRunner runner = TestRunners.newTestRunner(ConvertAvroSchema.class);
        runner.assertNotValid();
        runner.setProperty(ConvertAvroSchema.INPUT_SCHEMA, TestConvertAvroSchema.INPUT_SCHEMA.toString());
        runner.setProperty(ConvertAvroSchema.OUTPUT_SCHEMA, TestConvertAvroSchema.OUTPUT_SCHEMA.toString());
        runner.setProperty(COMPRESSION_TYPE, BZIP2.toString());
        Locale locale = Locale.getDefault();
        runner.setProperty("primaryColor", "color");
        runner.assertValid();
        NumberFormat format = NumberFormat.getInstance(locale);
        // Two valid rows, and one invalid because "free" is not a double.
        Record goodRecord1 = dataBasic("1", "blue", null, null);
        Record goodRecord2 = dataBasic("2", "red", "yellow", format.format(5.5));
        Record badRecord = dataBasic("3", "red", "yellow", "free");
        List<Record> input = Lists.newArrayList(goodRecord1, goodRecord2, badRecord);
        runner.enqueue(TestUtil.streamFor(input));
        runner.run();
        long converted = runner.getCounterValue("Converted records");
        long errors = runner.getCounterValue("Conversion errors");
        Assert.assertEquals("Should convert 2 rows", 2, converted);
        Assert.assertEquals("Should reject 1 rows", 1, errors);
        runner.assertTransferCount("success", 1);
        runner.assertTransferCount("failure", 1);
        MockFlowFile incompatible = runner.getFlowFilesForRelationship("failure").get(0);
        GenericDatumReader<Record> reader = new GenericDatumReader<Record>(TestConvertAvroSchema.INPUT_SCHEMA);
        DataFileStream<Record> stream = new DataFileStream<Record>(new ByteArrayInputStream(runner.getContentAsByteArray(incompatible)), reader);
        int count = 0;
        for (Record r : stream) {
            Assert.assertEquals(badRecord, r);
            count++;
        }
        stream.close();
        Assert.assertEquals(1, count);
        Assert.assertEquals("Should accumulate error messages", TestConvertAvroSchema.FAILURE_SUMMARY, incompatible.getAttribute("errors"));
        GenericDatumReader<Record> successReader = new GenericDatumReader<Record>(TestConvertAvroSchema.OUTPUT_SCHEMA);
        DataFileStream<Record> successStream = new DataFileStream<Record>(new ByteArrayInputStream(runner.getContentAsByteArray(runner.getFlowFilesForRelationship("success").get(0))), successReader);
        count = 0;
        for (Record r : successStream) {
            if (count == 0) {
                Assert.assertEquals(convertBasic(goodRecord1, locale), r);
            } else {
                Assert.assertEquals(convertBasic(goodRecord2, locale), r);
            }
            count++;
        }
        successStream.close();
        Assert.assertEquals(2, count);
    }

    @Test
    public void testBasicConversionWithLocales() throws IOException {
        testBasicConversionWithLocale("en_US");
        testBasicConversionWithLocale("fr_FR");
    }

    @Test
    public void testNestedConversion() throws IOException {
        TestRunner runner = TestRunners.newTestRunner(ConvertAvroSchema.class);
        runner.assertNotValid();
        runner.setProperty(ConvertAvroSchema.INPUT_SCHEMA, TestAvroRecordConverter.NESTED_RECORD_SCHEMA.toString());
        runner.setProperty(ConvertAvroSchema.OUTPUT_SCHEMA, TestAvroRecordConverter.UNNESTED_OUTPUT_SCHEMA.toString());
        runner.setProperty("parent.id", "parentId");
        runner.assertValid();
        // Two valid rows
        Record goodRecord1 = dataNested(1L, "200", null, null);
        Record goodRecord2 = dataNested(2L, "300", 5L, "ParentCompany");
        List<Record> input = Lists.newArrayList(goodRecord1, goodRecord2);
        runner.enqueue(TestUtil.streamFor(input));
        runner.run();
        long converted = runner.getCounterValue("Converted records");
        long errors = runner.getCounterValue("Conversion errors");
        Assert.assertEquals("Should convert 2 rows", 2, converted);
        Assert.assertEquals("Should reject 0 rows", 0, errors);
        runner.assertTransferCount("success", 1);
        runner.assertTransferCount("failure", 0);
        GenericDatumReader<Record> successReader = new GenericDatumReader<Record>(TestAvroRecordConverter.UNNESTED_OUTPUT_SCHEMA);
        DataFileStream<Record> successStream = new DataFileStream<Record>(new ByteArrayInputStream(runner.getContentAsByteArray(runner.getFlowFilesForRelationship("success").get(0))), successReader);
        int count = 0;
        for (Record r : successStream) {
            if (count == 0) {
                Assert.assertEquals(convertNested(goodRecord1), r);
            } else {
                Assert.assertEquals(convertNested(goodRecord2), r);
            }
            count++;
        }
        successStream.close();
        Assert.assertEquals(2, count);
    }
}

