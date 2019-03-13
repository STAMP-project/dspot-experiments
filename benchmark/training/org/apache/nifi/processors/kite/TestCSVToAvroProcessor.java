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
import CodecType.DEFLATE;
import ConvertCSVToAvro.CHARSET;
import ConvertCSVToAvro.DELIMITER;
import ConvertCSVToAvro.QUOTE;
import ConvertCSVToAvro.SCHEMA;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.file.DataFileStream;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;


public class TestCSVToAvroProcessor {
    public static final Schema SCHEMA = SchemaBuilder.record("Test").fields().requiredLong("id").requiredString("color").optionalDouble("price").endRecord();

    public static final String CSV_CONTENT = "" + (("1,green\n" + ",blue,\n")// invalid, ID is missing
     + "2,grey,12.95");

    public static final String FAILURE_CONTENT = "" + ",blue,\n";// invalid, ID is missing


    public static final String TSV_CONTENT = "" + (("1\tgreen\n" + "\tblue\t\n")// invalid, ID is missing
     + "2\tgrey\t12.95");

    public static final String FAILURE_SUMMARY = "" + "Field id: cannot make \"long\" value: \'\': Field id type:LONG pos:0 not set and has no default value";

    /**
     * Test for a schema that is not a JSON but does not throw exception when trying to parse as an URI
     */
    @Test
    public void testSchemeValidation() throws IOException {
        TestRunner runner = TestRunners.newTestRunner(ConvertCSVToAvro.class);
        runner.setProperty(ConvertCSVToAvro.SCHEMA, "column1;column2");
        runner.assertNotValid();
        runner.setProperty(ConvertCSVToAvro.SCHEMA, "src/test/resources/Shapes_header.csv.avro");
        runner.assertNotValid();
        runner.setProperty(ConvertCSVToAvro.SCHEMA, ("file:" + (new File("src/test/resources/Shapes_header.csv.avro").getAbsolutePath())));
        runner.assertValid();
        runner.setProperty(ConvertCSVToAvro.SCHEMA, "");
        runner.assertNotValid();
    }

    /**
     * Basic test for tab separated files, similar to #test
     */
    @Test
    public void testTabSeparatedConversion() throws IOException {
        TestRunner runner = TestRunners.newTestRunner(ConvertCSVToAvro.class);
        runner.assertNotValid();
        runner.setProperty(ConvertCSVToAvro.SCHEMA, TestCSVToAvroProcessor.SCHEMA.toString());
        runner.setProperty(DELIMITER, "\\t");
        runner.assertValid();
        runner.enqueue(TestUtil.streamFor(TestCSVToAvroProcessor.TSV_CONTENT));
        runner.run();
        long converted = runner.getCounterValue("Converted records");
        long errors = runner.getCounterValue("Conversion errors");
        Assert.assertEquals("Should convert 2 rows", 2, converted);
        Assert.assertEquals("Should reject 1 row", 1, errors);
        runner.assertTransferCount("success", 1);
        runner.assertTransferCount("failure", 0);
        runner.assertTransferCount("incompatible", 1);
        MockFlowFile incompatible = runner.getFlowFilesForRelationship("incompatible").get(0);
        String failureContent = new String(runner.getContentAsByteArray(incompatible), StandardCharsets.UTF_8);
        Assert.assertEquals("Should reject an invalid string and double", TestCSVToAvroProcessor.TSV_CONTENT, failureContent);
        Assert.assertEquals("Should accumulate error messages", TestCSVToAvroProcessor.FAILURE_SUMMARY, incompatible.getAttribute("errors"));
    }

    @Test
    public void testBasicConversion() throws IOException {
        TestRunner runner = TestRunners.newTestRunner(ConvertCSVToAvro.class);
        runner.assertNotValid();
        runner.setProperty(ConvertCSVToAvro.SCHEMA, TestCSVToAvroProcessor.SCHEMA.toString());
        runner.assertValid();
        runner.enqueue(TestUtil.streamFor(TestCSVToAvroProcessor.CSV_CONTENT));
        runner.run();
        long converted = runner.getCounterValue("Converted records");
        long errors = runner.getCounterValue("Conversion errors");
        Assert.assertEquals("Should convert 2 rows", 2, converted);
        Assert.assertEquals("Should reject 1 row", 1, errors);
        runner.assertTransferCount("success", 1);
        runner.assertTransferCount("failure", 0);
        runner.assertTransferCount("incompatible", 1);
        MockFlowFile incompatible = runner.getFlowFilesForRelationship("incompatible").get(0);
        String failureContent = new String(runner.getContentAsByteArray(incompatible), StandardCharsets.UTF_8);
        Assert.assertEquals("Should reject an invalid string and double", TestCSVToAvroProcessor.CSV_CONTENT, failureContent);
        Assert.assertEquals("Should accumulate error messages", TestCSVToAvroProcessor.FAILURE_SUMMARY, incompatible.getAttribute("errors"));
    }

    @Test
    public void testBasicConversionWithCompression() throws IOException {
        TestRunner runner = TestRunners.newTestRunner(ConvertCSVToAvro.class);
        runner.assertNotValid();
        runner.setProperty(ConvertCSVToAvro.SCHEMA, TestCSVToAvroProcessor.SCHEMA.toString());
        runner.setProperty(COMPRESSION_TYPE, DEFLATE.toString());
        runner.assertValid();
        runner.enqueue(TestUtil.streamFor(TestCSVToAvroProcessor.CSV_CONTENT));
        runner.run();
        long converted = runner.getCounterValue("Converted records");
        long errors = runner.getCounterValue("Conversion errors");
        Assert.assertEquals("Should convert 2 rows", 2, converted);
        Assert.assertEquals("Should reject 1 row", 1, errors);
        runner.assertTransferCount("success", 1);
        runner.assertTransferCount("failure", 0);
        runner.assertTransferCount("incompatible", 1);
        MockFlowFile incompatible = runner.getFlowFilesForRelationship("incompatible").get(0);
        String failureContent = new String(runner.getContentAsByteArray(incompatible), StandardCharsets.UTF_8);
        Assert.assertEquals("Should reject an invalid string and double", TestCSVToAvroProcessor.CSV_CONTENT, failureContent);
        Assert.assertEquals("Should accumulate error messages", TestCSVToAvroProcessor.FAILURE_SUMMARY, incompatible.getAttribute("errors"));
    }

    @Test
    public void testAlternateCharset() throws IOException {
        TestRunner runner = TestRunners.newTestRunner(ConvertCSVToAvro.class);
        runner.setProperty(ConvertCSVToAvro.SCHEMA, TestCSVToAvroProcessor.SCHEMA.toString());
        runner.setProperty(CHARSET, "utf16");
        runner.assertValid();
        runner.enqueue(TestUtil.streamFor(TestCSVToAvroProcessor.CSV_CONTENT, Charset.forName("UTF-16")));
        runner.run();
        long converted = runner.getCounterValue("Converted records");
        long errors = runner.getCounterValue("Conversion errors");
        Assert.assertEquals("Should convert 2 rows", 2, converted);
        Assert.assertEquals("Should reject 1 row", 1, errors);
        runner.assertTransferCount("success", 1);
        runner.assertTransferCount("failure", 0);
        runner.assertTransferCount("incompatible", 1);
        MockFlowFile incompatible = runner.getFlowFilesForRelationship("incompatible").get(0);
        Assert.assertEquals("Should accumulate error messages", TestCSVToAvroProcessor.FAILURE_SUMMARY, incompatible.getAttribute("errors"));
    }

    @Test
    public void testOnlyErrors() throws IOException {
        TestRunner runner = TestRunners.newTestRunner(ConvertCSVToAvro.class);
        runner.assertNotValid();
        runner.setProperty(ConvertCSVToAvro.SCHEMA, TestCSVToAvroProcessor.SCHEMA.toString());
        runner.assertValid();
        runner.enqueue(TestUtil.streamFor(TestCSVToAvroProcessor.FAILURE_CONTENT));
        runner.run();
        long converted = runner.getCounterValue("Converted records");
        long errors = runner.getCounterValue("Conversion errors");
        Assert.assertEquals("Should convert 0 rows", 0, converted);
        Assert.assertEquals("Should reject 1 row", 1, errors);
        runner.assertTransferCount("success", 0);
        runner.assertTransferCount("failure", 1);
        runner.assertTransferCount("incompatible", 0);
        MockFlowFile incompatible = runner.getFlowFilesForRelationship("failure").get(0);
        Assert.assertEquals("Should set an error message", TestCSVToAvroProcessor.FAILURE_SUMMARY, incompatible.getAttribute("errors"));
    }

    @Test
    public void testEmptyContent() throws IOException {
        TestRunner runner = TestRunners.newTestRunner(ConvertCSVToAvro.class);
        runner.assertNotValid();
        runner.setProperty(ConvertCSVToAvro.SCHEMA, TestCSVToAvroProcessor.SCHEMA.toString());
        runner.assertValid();
        runner.enqueue(TestUtil.streamFor(""));
        runner.run();
        long converted = runner.getCounterValue("Converted records");
        long errors = runner.getCounterValue("Conversion errors");
        Assert.assertEquals("Should convert 0 rows", 0, converted);
        Assert.assertEquals("Should reject 0 row", 0, errors);
        runner.assertTransferCount("success", 0);
        runner.assertTransferCount("failure", 1);
        runner.assertTransferCount("incompatible", 0);
        MockFlowFile incompatible = runner.getFlowFilesForRelationship("failure").get(0);
        Assert.assertEquals("Should set an error message", "No incoming records", incompatible.getAttribute("errors"));
    }

    @Test
    public void testBasicConversionNoErrors() throws IOException {
        TestRunner runner = TestRunners.newTestRunner(ConvertCSVToAvro.class);
        runner.assertNotValid();
        runner.setProperty(ConvertCSVToAvro.SCHEMA, TestCSVToAvroProcessor.SCHEMA.toString());
        runner.assertValid();
        runner.enqueue(TestUtil.streamFor("1,green\n2,blue,\n3,grey,12.95"));
        runner.run();
        long converted = runner.getCounterValue("Converted records");
        long errors = runner.getCounterValue("Conversion errors");
        Assert.assertEquals("Should convert 3 rows", 3, converted);
        Assert.assertEquals("Should reject 0 row", 0, errors);
        runner.assertTransferCount("success", 1);
        runner.assertTransferCount("failure", 0);
        runner.assertTransferCount("incompatible", 0);
    }

    @Test
    public void testExpressionLanguageBasedCSVProperties() throws IOException {
        TestRunner runner = TestRunners.newTestRunner(ConvertCSVToAvro.class);
        runner.assertNotValid();
        runner.setProperty(ConvertCSVToAvro.SCHEMA, TestCSVToAvroProcessor.SCHEMA.toString());
        runner.assertValid();
        runner.setProperty(DELIMITER, "${csv.delimiter}");
        runner.setProperty(QUOTE, "${csv.quote}");
        HashMap<String, String> flowFileAttributes = new HashMap<String, String>();
        flowFileAttributes.put("csv.delimiter", "|");
        flowFileAttributes.put("csv.quote", "~");
        runner.enqueue(TestUtil.streamFor("1|green\n2|~blue|field~|\n3|grey|12.95"), flowFileAttributes);
        runner.run();
        long converted = runner.getCounterValue("Converted records");
        long errors = runner.getCounterValue("Conversion errors");
        Assert.assertEquals("Should convert 3 rows", 3, converted);
        Assert.assertEquals("Should reject 0 row", 0, errors);
        runner.assertTransferCount("success", 1);
        runner.assertTransferCount("failure", 0);
        runner.assertTransferCount("incompatible", 0);
        final InputStream in = new ByteArrayInputStream(runner.getFlowFilesForRelationship("success").get(0).toByteArray());
        final DatumReader<GenericRecord> datumReader = new org.apache.avro.generic.GenericDatumReader();
        try (DataFileStream<GenericRecord> dataFileReader = new DataFileStream(in, datumReader)) {
            Assert.assertTrue(dataFileReader.hasNext());
            GenericRecord record = dataFileReader.next();
            Assert.assertEquals(1L, record.get("id"));
            Assert.assertEquals("green", record.get("color").toString());
            Assert.assertNull(record.get("price"));
            Assert.assertTrue(dataFileReader.hasNext());
            record = dataFileReader.next();
            Assert.assertEquals(2L, record.get("id"));
            Assert.assertEquals("blue|field", record.get("color").toString());
            Assert.assertNull(record.get("price"));
            Assert.assertTrue(dataFileReader.hasNext());
            record = dataFileReader.next();
            Assert.assertEquals(3L, record.get("id"));
            Assert.assertEquals("grey", record.get("color").toString());
            Assert.assertEquals(12.95, record.get("price"));
        }
    }
}

