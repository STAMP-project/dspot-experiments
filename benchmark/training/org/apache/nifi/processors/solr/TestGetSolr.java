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
package org.apache.nifi.processors.solr;


import CoreAttributes.MIME_TYPE;
import GetSolr.BATCH_SIZE;
import GetSolr.DATE_FILTER;
import GetSolr.MODE_REC;
import GetSolr.REL_SUCCESS;
import GetSolr.RETURN_FIELDS;
import GetSolr.RETURN_TYPE;
import GetSolr.SOLR_QUERY;
import SchemaAccessUtils.SCHEMA_ACCESS_STRATEGY;
import SchemaAccessUtils.SCHEMA_TEXT;
import SchemaAccessUtils.SCHEMA_TEXT_PROPERTY;
import Scope.CLUSTER;
import SolrUtils.RECORD_WRITER;
import com.google.gson.stream.JsonReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.apache.nifi.json.JsonRecordSetWriter;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.reporting.InitializationException;
import org.apache.nifi.util.TestRunner;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Assert;
import org.junit.Test;
import org.xmlunit.matchers.CompareMatcher;


public class TestGetSolr {
    static final String DEFAULT_SOLR_CORE = "testCollection";

    static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);

    static final String DATE_STRING_EARLIER = "1970-01-01T00:00:00.000Z";

    static final String DATE_STRING_LATER = "1970-01-01T00:00:00.001Z";

    static {
        TestGetSolr.DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    private SolrClient solrClient;

    @Test
    public void testLessThanBatchSizeShouldProduceOneFlowFile() throws IOException, SolrServerException {
        final TestGetSolr.TestableProcessor proc = new TestGetSolr.TestableProcessor(solrClient);
        TestRunner runner = TestGetSolr.createDefaultTestRunner(proc);
        runner.setProperty(BATCH_SIZE, "20");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
    }

    @Test
    public void testNoResultsShouldProduceNoOutput() throws IOException, SolrServerException {
        final TestGetSolr.TestableProcessor proc = new TestGetSolr.TestableProcessor(solrClient);
        TestRunner runner = TestGetSolr.createDefaultTestRunner(proc);
        runner.setProperty(SOLR_QUERY, "integer_single:1000");
        runner.setProperty(BATCH_SIZE, "1");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 0);
    }

    @Test(expected = AssertionError.class)
    public void testValidation() throws IOException, SolrServerException {
        final TestGetSolr.TestableProcessor proc = new TestGetSolr.TestableProcessor(solrClient);
        TestRunner runner = TestGetSolr.createDefaultTestRunner(proc);
        runner.setProperty(BATCH_SIZE, "2");
        runner.setProperty(RETURN_TYPE, MODE_REC.getValue());
        runner.run(1);
    }

    @Test
    public void testCompletenessDespiteUpdates() throws IOException, SolrServerException {
        final TestGetSolr.TestableProcessor proc = new TestGetSolr.TestableProcessor(solrClient);
        TestRunner runner = TestGetSolr.createDefaultTestRunner(proc);
        runner.setProperty(BATCH_SIZE, "1");
        runner.run(1, false, true);
        runner.assertQueueEmpty();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 10);
        runner.clearTransferState();
        SolrInputDocument doc0 = new SolrInputDocument();
        doc0.addField("id", "doc0");
        doc0.addField("created", new Date());
        SolrInputDocument doc1 = new SolrInputDocument();
        doc1.addField("id", "doc1");
        doc1.addField("created", new Date());
        solrClient.add(doc0);
        solrClient.add(doc1);
        solrClient.commit();
        runner.run(1, true, false);
        runner.assertQueueEmpty();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 2);
        runner.assertAllFlowFilesContainAttribute(MIME_TYPE.key());
    }

    @Test
    public void testCompletenessDespiteDeletions() throws IOException, SolrServerException {
        final TestGetSolr.TestableProcessor proc = new TestGetSolr.TestableProcessor(solrClient);
        TestRunner runner = TestGetSolr.createDefaultTestRunner(proc);
        runner.setProperty(BATCH_SIZE, "1");
        runner.run(1, false, true);
        runner.assertQueueEmpty();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 10);
        runner.clearTransferState();
        SolrInputDocument doc10 = new SolrInputDocument();
        doc10.addField("id", "doc10");
        doc10.addField("created", new Date());
        SolrInputDocument doc11 = new SolrInputDocument();
        doc11.addField("id", "doc11");
        doc11.addField("created", new Date());
        solrClient.add(doc10);
        solrClient.add(doc11);
        solrClient.deleteById("doc0");
        solrClient.deleteById("doc1");
        solrClient.deleteById("doc2");
        solrClient.commit();
        runner.run(1, true, false);
        runner.assertQueueEmpty();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 2);
        runner.assertAllFlowFilesContainAttribute(MIME_TYPE.key());
    }

    @Test
    public void testInitialDateFilter() throws IOException, ParseException, SolrServerException {
        final Date dateToFilter = TestGetSolr.DATE_FORMAT.parse(TestGetSolr.DATE_STRING_LATER);
        final TestGetSolr.TestableProcessor proc = new TestGetSolr.TestableProcessor(solrClient);
        TestRunner runner = TestGetSolr.createDefaultTestRunner(proc);
        runner.setProperty(DATE_FILTER, TestGetSolr.DATE_FORMAT.format(dateToFilter));
        runner.setProperty(BATCH_SIZE, "1");
        SolrInputDocument doc10 = new SolrInputDocument();
        doc10.addField("id", "doc10");
        doc10.addField("created", dateToFilter);
        SolrInputDocument doc11 = new SolrInputDocument();
        doc11.addField("id", "doc11");
        doc11.addField("created", dateToFilter);
        solrClient.add(doc10);
        solrClient.add(doc11);
        solrClient.commit();
        runner.run(1, true, true);
        runner.assertQueueEmpty();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 2);
        runner.assertAllFlowFilesContainAttribute(MIME_TYPE.key());
    }

    @Test
    public void testPropertyModified() throws IOException, SolrServerException {
        final TestGetSolr.TestableProcessor proc = new TestGetSolr.TestableProcessor(solrClient);
        TestRunner runner = TestGetSolr.createDefaultTestRunner(proc);
        runner.setProperty(BATCH_SIZE, "1");
        runner.run(1, false, true);
        runner.assertQueueEmpty();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 10);
        runner.clearTransferState();
        // Change property contained in propertyNamesForActivatingClearState
        runner.setProperty(RETURN_FIELDS, "id,created,string_multi");
        runner.run(1, false, true);
        runner.assertQueueEmpty();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 10);
        runner.clearTransferState();
        // Change property not contained in propertyNamesForActivatingClearState
        runner.setProperty(BATCH_SIZE, "2");
        runner.run(1, true, true);
        runner.assertQueueEmpty();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 0);
        runner.clearTransferState();
    }

    @Test
    public void testStateCleared() throws IOException, SolrServerException {
        final TestGetSolr.TestableProcessor proc = new TestGetSolr.TestableProcessor(solrClient);
        TestRunner runner = TestGetSolr.createDefaultTestRunner(proc);
        runner.setProperty(BATCH_SIZE, "1");
        runner.run(1, false, true);
        runner.assertQueueEmpty();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 10);
        runner.clearTransferState();
        // run without clearing statemanager
        runner.run(1, false, true);
        runner.assertQueueEmpty();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 0);
        runner.clearTransferState();
        // run with cleared statemanager
        runner.getStateManager().clear(CLUSTER);
        runner.run(1, true, true);
        runner.assertQueueEmpty();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 10);
        runner.clearTransferState();
    }

    @Test
    public void testRecordWriter() throws IOException, InitializationException {
        final TestGetSolr.TestableProcessor proc = new TestGetSolr.TestableProcessor(solrClient);
        TestRunner runner = TestGetSolr.createDefaultTestRunner(proc);
        runner.setProperty(RETURN_TYPE, MODE_REC.getValue());
        runner.setProperty(RETURN_FIELDS, "id,created,integer_single");
        runner.setProperty(BATCH_SIZE, "10");
        final String outputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/test-schema.avsc")));
        final JsonRecordSetWriter jsonWriter = new JsonRecordSetWriter();
        runner.addControllerService("writer", jsonWriter);
        runner.setProperty(jsonWriter, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(jsonWriter, SCHEMA_TEXT, outputSchemaText);
        runner.setProperty(jsonWriter, "Pretty Print JSON", "true");
        runner.setProperty(jsonWriter, "Schema Write Strategy", "full-schema-attribute");
        runner.enableControllerService(jsonWriter);
        runner.setProperty(RECORD_WRITER, "writer");
        runner.run(1, true, true);
        runner.assertQueueEmpty();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        runner.assertAllFlowFilesContainAttribute(MIME_TYPE.key());
        // Check for valid json
        JsonReader reader = new JsonReader(new InputStreamReader(new ByteArrayInputStream(runner.getContentAsByteArray(runner.getFlowFilesForRelationship(REL_SUCCESS).get(0)))));
        reader.beginArray();
        int controlScore = 0;
        while (reader.hasNext()) {
            reader.beginObject();
            while (reader.hasNext()) {
                if (reader.nextName().equals("integer_single"))
                    controlScore += reader.nextInt();
                else
                    reader.skipValue();

            } 
            reader.endObject();
        } 
        Assert.assertEquals(controlScore, 45);
    }

    @Test
    public void testForValidXml() throws IOException, InitializationException, SolrServerException {
        final TestGetSolr.TestableProcessor proc = new TestGetSolr.TestableProcessor(solrClient);
        TestRunner runner = TestGetSolr.createDefaultTestRunner(proc);
        runner.setProperty(SOLR_QUERY, "id:doc1");
        runner.setProperty(RETURN_FIELDS, "id");
        runner.setProperty(BATCH_SIZE, "10");
        runner.run(1, true, true);
        runner.assertQueueEmpty();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        runner.assertAllFlowFilesContainAttribute(MIME_TYPE.key());
        String expectedXml = "<docs><doc boost=\"1.0\"><field name=\"id\">doc1</field></doc></docs>";
        Assert.assertThat(expectedXml, CompareMatcher.isIdenticalTo(new String(runner.getContentAsByteArray(runner.getFlowFilesForRelationship(REL_SUCCESS).get(0)))));
    }

    // Override createSolrClient and return the passed in SolrClient
    private class TestableProcessor extends GetSolr {
        private SolrClient solrClient;

        public TestableProcessor(SolrClient solrClient) {
            this.solrClient = solrClient;
        }

        @Override
        protected SolrClient createSolrClient(ProcessContext context, String solrLocation) {
            return solrClient;
        }
    }
}

