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
package org.apache.nifi.xml;


import XMLRecordSetWriter.ALWAYS_SUPPRESS;
import XMLRecordSetWriter.ARRAY_TAG_NAME;
import XMLRecordSetWriter.ARRAY_WRAPPING;
import XMLRecordSetWriter.RECORD_TAG_NAME;
import XMLRecordSetWriter.ROOT_TAG_NAME;
import XMLRecordSetWriter.SUPPRESS_NULLS;
import XMLRecordSetWriter.USE_PROPERTY_AS_WRAPPER;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.avro.Schema;
import org.apache.nifi.avro.AvroTypeUtil;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.reporting.InitializationException;
import org.apache.nifi.schema.access.SchemaNotFoundException;
import org.apache.nifi.serialization.RecordSetWriter;
import org.apache.nifi.serialization.record.RecordSchema;
import org.apache.nifi.serialization.record.SchemaIdentifier;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.xmlunit.diff.ElementSelectors;
import org.xmlunit.matchers.CompareMatcher;


public class TestXMLRecordSetWriter {
    @Test
    public void testDefault() throws IOException, InitializationException {
        XMLRecordSetWriter writer = new XMLRecordSetWriter();
        TestRunner runner = setup(writer);
        runner.setProperty(writer, ROOT_TAG_NAME, "root");
        runner.enableControllerService(writer);
        runner.enqueue("");
        runner.run();
        runner.assertQueueEmpty();
        runner.assertAllFlowFilesTransferred(TestXMLRecordSetWriterProcessor.SUCCESS, 1);
        String expected = "<root><array_record><array_field>1</array_field><array_field></array_field><array_field>3</array_field>" + (("<name1>val1</name1><name2></name2></array_record>" + "<array_record><array_field>1</array_field><array_field></array_field><array_field>3</array_field>") + "<name1>val1</name1><name2></name2></array_record></root>");
        String actual = new String(runner.getContentAsByteArray(runner.getFlowFilesForRelationship(TestXMLRecordSetWriterProcessor.SUCCESS).get(0)));
        Assert.assertThat(expected, CompareMatcher.isSimilarTo(actual).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testDefaultSingleRecord() throws IOException, InitializationException {
        XMLRecordSetWriter writer = new XMLRecordSetWriter();
        TestRunner runner = setup(writer);
        runner.setProperty(TestXMLRecordSetWriterProcessor.MULTIPLE_RECORDS, "false");
        runner.enableControllerService(writer);
        runner.enqueue("");
        runner.run();
        runner.assertQueueEmpty();
        runner.assertAllFlowFilesTransferred(TestXMLRecordSetWriterProcessor.SUCCESS, 1);
        String expected = "<array_record><array_field>1</array_field><array_field></array_field><array_field>3</array_field>" + "<name1>val1</name1><name2></name2></array_record>";
        String actual = new String(runner.getContentAsByteArray(runner.getFlowFilesForRelationship(TestXMLRecordSetWriterProcessor.SUCCESS).get(0)));
        Assert.assertThat(expected, CompareMatcher.isSimilarTo(actual).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testRootAndRecordNaming() throws IOException, InitializationException {
        XMLRecordSetWriter writer = new XMLRecordSetWriter();
        TestRunner runner = setup(writer);
        runner.setProperty(writer, ROOT_TAG_NAME, "ROOT_NODE");
        runner.setProperty(writer, RECORD_TAG_NAME, "RECORD_NODE");
        runner.enableControllerService(writer);
        runner.enqueue("");
        runner.run();
        runner.assertQueueEmpty();
        runner.assertAllFlowFilesTransferred(TestXMLRecordSetWriterProcessor.SUCCESS, 1);
        String expected = "<ROOT_NODE><RECORD_NODE><array_field>1</array_field><array_field></array_field><array_field>3</array_field>" + (("<name1>val1</name1><name2></name2></RECORD_NODE>" + "<RECORD_NODE><array_field>1</array_field><array_field></array_field><array_field>3</array_field>") + "<name1>val1</name1><name2></name2></RECORD_NODE></ROOT_NODE>");
        String actual = new String(runner.getContentAsByteArray(runner.getFlowFilesForRelationship(TestXMLRecordSetWriterProcessor.SUCCESS).get(0)));
        Assert.assertThat(expected, CompareMatcher.isSimilarTo(actual).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testSchemaRootRecordNaming() throws IOException, InitializationException {
        String avroSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/xml/testschema3")));
        Schema avroSchema = new Schema.Parser().parse(avroSchemaText);
        SchemaIdentifier schemaId = SchemaIdentifier.builder().name("schemaName").build();
        RecordSchema recordSchema = AvroTypeUtil.createSchema(avroSchema, avroSchemaText, schemaId);
        XMLRecordSetWriter writer = new TestXMLRecordSetWriter._XMLRecordSetWriter(recordSchema);
        TestRunner runner = setup(writer);
        runner.setProperty(writer, ROOT_TAG_NAME, "ROOT_NODE");
        runner.enableControllerService(writer);
        runner.enqueue("");
        runner.run();
        runner.assertQueueEmpty();
        runner.assertAllFlowFilesTransferred(TestXMLRecordSetWriterProcessor.SUCCESS, 1);
        String expected = "<ROOT_NODE><array_record><array_field>1</array_field><array_field></array_field><array_field>3</array_field>" + (("<name1>val1</name1><name2></name2></array_record>" + "<array_record><array_field>1</array_field><array_field></array_field><array_field>3</array_field>") + "<name1>val1</name1><name2></name2></array_record></ROOT_NODE>");
        String actual = new String(runner.getContentAsByteArray(runner.getFlowFilesForRelationship(TestXMLRecordSetWriterProcessor.SUCCESS).get(0)));
        Assert.assertThat(expected, CompareMatcher.isSimilarTo(actual).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testNullSuppression() throws IOException, InitializationException {
        XMLRecordSetWriter writer = new XMLRecordSetWriter();
        TestRunner runner = setup(writer);
        runner.setProperty(writer, ROOT_TAG_NAME, "root");
        runner.setProperty(writer, RECORD_TAG_NAME, "record");
        runner.setProperty(writer, SUPPRESS_NULLS, ALWAYS_SUPPRESS);
        runner.enableControllerService(writer);
        runner.enqueue("");
        runner.run();
        runner.assertQueueEmpty();
        runner.assertAllFlowFilesTransferred(TestXMLRecordSetWriterProcessor.SUCCESS, 1);
        String expected = "<root><record><array_field>1</array_field><array_field>3</array_field>" + (("<name1>val1</name1></record>" + "<record><array_field>1</array_field><array_field>3</array_field>") + "<name1>val1</name1></record></root>");
        String actual = new String(runner.getContentAsByteArray(runner.getFlowFilesForRelationship(TestXMLRecordSetWriterProcessor.SUCCESS).get(0)));
        Assert.assertThat(expected, CompareMatcher.isSimilarTo(actual).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testArrayWrapping() throws IOException, InitializationException {
        XMLRecordSetWriter writer = new XMLRecordSetWriter();
        TestRunner runner = setup(writer);
        runner.setProperty(writer, ROOT_TAG_NAME, "root");
        runner.setProperty(writer, RECORD_TAG_NAME, "record");
        runner.setProperty(writer, ARRAY_WRAPPING, USE_PROPERTY_AS_WRAPPER);
        runner.setProperty(writer, ARRAY_TAG_NAME, "wrap");
        runner.enableControllerService(writer);
        runner.enqueue("");
        runner.run();
        runner.assertQueueEmpty();
        runner.assertAllFlowFilesTransferred(TestXMLRecordSetWriterProcessor.SUCCESS, 1);
        String expected = "<root><record><wrap><array_field>1</array_field><array_field></array_field><array_field>3</array_field></wrap>" + (("<name1>val1</name1><name2></name2></record>" + "<record><wrap><array_field>1</array_field><array_field></array_field><array_field>3</array_field></wrap>") + "<name1>val1</name1><name2></name2></record></root>");
        String actual = new String(runner.getContentAsByteArray(runner.getFlowFilesForRelationship(TestXMLRecordSetWriterProcessor.SUCCESS).get(0)));
        Assert.assertThat(expected, CompareMatcher.isSimilarTo(actual).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testValidation() throws IOException, InitializationException {
        XMLRecordSetWriter writer = new XMLRecordSetWriter();
        TestRunner runner = setup(writer);
        runner.setProperty(writer, ROOT_TAG_NAME, "root");
        runner.setProperty(writer, RECORD_TAG_NAME, "record");
        runner.setProperty(writer, ARRAY_WRAPPING, USE_PROPERTY_AS_WRAPPER);
        runner.assertNotValid(writer);
        runner.setProperty(writer, ARRAY_TAG_NAME, "array-tag-name");
        runner.assertValid(writer);
        runner.enableControllerService(writer);
        runner.enqueue("");
        String message = "Processor has 1 validation failures:\n" + (("'xml_writer' validated against 'xml_writer' is invalid because Controller Service is not valid: " + "'array_tag_name' is invalid because if property 'array_wrapping' is defined as 'Use Property as Wrapper' ") + "or \'Use Property for Elements\' the property \'Array Tag Name\' has to be set.\n");
        try {
            runner.run();
        } catch (AssertionError e) {
            Assert.assertEquals(message, e.getMessage());
        }
    }

    static class _XMLRecordSetWriter extends XMLRecordSetWriter {
        RecordSchema recordSchema;

        _XMLRecordSetWriter(RecordSchema recordSchema) {
            this.recordSchema = recordSchema;
        }

        @Override
        public RecordSetWriter createWriter(ComponentLog logger, RecordSchema schema, OutputStream out) throws IOException, SchemaNotFoundException {
            return super.createWriter(logger, this.recordSchema, out);
        }
    }
}

