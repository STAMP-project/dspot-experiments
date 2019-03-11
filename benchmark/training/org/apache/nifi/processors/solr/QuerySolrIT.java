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


import QuerySolr.AMOUNT_DOCUMENTS_TO_RETURN;
import QuerySolr.ATTRIBUTE_CURSOR_MARK;
import QuerySolr.ATTRIBUTE_QUERY_TIME;
import QuerySolr.ATTRIBUTE_SOLR_CONNECT;
import QuerySolr.ATTRIBUTE_SOLR_START;
import QuerySolr.ATTRIBUTE_SOLR_STATUS;
import QuerySolr.EXCEPTION;
import QuerySolr.EXCEPTION_MESSAGE;
import QuerySolr.FACETS;
import QuerySolr.FAILURE;
import QuerySolr.MODE_REC;
import QuerySolr.ORIGINAL;
import QuerySolr.RESULTS;
import QuerySolr.RETURN_ALL_RESULTS;
import QuerySolr.RETURN_TYPE;
import QuerySolr.SOLR_PARAM_FIELD_LIST;
import QuerySolr.SOLR_PARAM_QUERY;
import QuerySolr.SOLR_PARAM_REQUEST_HANDLER;
import QuerySolr.SOLR_PARAM_ROWS;
import QuerySolr.SOLR_PARAM_SORT;
import QuerySolr.SOLR_PARAM_START;
import QuerySolr.STATS;
import SchemaAccessUtils.SCHEMA_ACCESS_STRATEGY;
import SchemaAccessUtils.SCHEMA_TEXT;
import SchemaAccessUtils.SCHEMA_TEXT_PROPERTY;
import SolrUtils.RECORD_WRITER;
import SolrUtils.SOLR_TYPE;
import SolrUtils.SOLR_TYPE_CLOUD;
import com.google.gson.stream.JsonReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import org.apache.nifi.json.JsonRecordSetWriter;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.reporting.InitializationException;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.solr.client.solrj.SolrClient;
import org.junit.Assert;
import org.junit.Test;
import org.xmlunit.matchers.CompareMatcher;


public class QuerySolrIT {
    /* This integration test expects a Solr instance running locally in SolrCloud mode, coordinated by a single ZooKeeper
    instance accessible with the ZooKeeper-Connect-String "localhost:2181".
     */
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);

    private static final SimpleDateFormat DATE_FORMAT_SOLR_COLLECTION = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US);

    private static String SOLR_COLLECTION;

    private static String ZK_CONFIG_PATH;

    private static String ZK_CONFIG_NAME;

    private static String SOLR_LOCATION = "localhost:2181";

    static {
        QuerySolrIT.DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = new Date();
        QuerySolrIT.SOLR_COLLECTION = (QuerySolrIT.DATE_FORMAT_SOLR_COLLECTION.format(date)) + "_QuerySolrIT";
        QuerySolrIT.ZK_CONFIG_PATH = "src/test/resources/solr/testCollection/conf";
        QuerySolrIT.ZK_CONFIG_NAME = "QuerySolrIT_config";
    }

    @Test
    public void testAllFacetCategories() throws IOException {
        SolrClient solrClient = QuerySolrIT.createSolrClient();
        TestRunner runner = createRunnerWithSolrClient(solrClient);
        runner.setProperty("facet", "true");
        runner.setProperty("facet.field", "integer_multi");
        runner.setProperty("facet.interval", "integer_single");
        runner.setProperty("facet.interval.set.1", "[4,7]");
        runner.setProperty("facet.interval.set.2", "[5,7]");
        runner.setProperty("facet.range", "created");
        runner.setProperty("facet.range.start", "NOW/MINUTE");
        runner.setProperty("facet.range.end", "NOW/MINUTE+1MINUTE");
        runner.setProperty("facet.range.gap", "+20SECOND");
        runner.setProperty("facet.query.1", "*:*");
        runner.setProperty("facet.query.2", "integer_multi:2");
        runner.setProperty("facet.query.3", "integer_multi:3");
        runner.enqueue(new ByteArrayInputStream(new byte[0]));
        runner.run();
        runner.assertTransferCount(FACETS, 1);
        JsonReader reader = new JsonReader(new InputStreamReader(new ByteArrayInputStream(runner.getContentAsByteArray(runner.getFlowFilesForRelationship(FACETS).get(0)))));
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("facet_queries")) {
                Assert.assertEquals(30, returnCheckSumForArrayOfJsonObjects(reader));
            } else
                if (name.equals("facet_fields")) {
                    reader.beginObject();
                    Assert.assertEquals(reader.nextName(), "integer_multi");
                    Assert.assertEquals(returnCheckSumForArrayOfJsonObjects(reader), 30);
                    reader.endObject();
                } else
                    if (name.equals("facet_ranges")) {
                        reader.beginObject();
                        Assert.assertEquals(reader.nextName(), "created");
                        Assert.assertEquals(returnCheckSumForArrayOfJsonObjects(reader), 10);
                        reader.endObject();
                    } else
                        if (name.equals("facet_intervals")) {
                            reader.beginObject();
                            Assert.assertEquals(reader.nextName(), "integer_single");
                            Assert.assertEquals(returnCheckSumForArrayOfJsonObjects(reader), 7);
                            reader.endObject();
                        }



        } 
        reader.endObject();
        reader.close();
        solrClient.close();
    }

    @Test
    public void testFacetTrueButNull() throws IOException {
        SolrClient solrClient = QuerySolrIT.createSolrClient();
        TestRunner runner = createRunnerWithSolrClient(solrClient);
        runner.setProperty("facet", "true");
        runner.setProperty("stats", "true");
        runner.enqueue(new ByteArrayInputStream(new byte[0]));
        runner.run();
        runner.assertTransferCount(RESULTS, 1);
        runner.assertTransferCount(FACETS, 1);
        runner.assertTransferCount(STATS, 1);
        // Check for empty nestet Objects in JSON
        JsonReader reader = new JsonReader(new InputStreamReader(new ByteArrayInputStream(runner.getContentAsByteArray(runner.getFlowFilesForRelationship(FACETS).get(0)))));
        reader.beginObject();
        while (reader.hasNext()) {
            if (reader.nextName().equals("facet_queries")) {
                reader.beginArray();
                Assert.assertFalse(reader.hasNext());
                reader.endArray();
            } else {
                reader.beginObject();
                Assert.assertFalse(reader.hasNext());
                reader.endObject();
            }
        } 
        reader.endObject();
        JsonReader reader_stats = new JsonReader(new InputStreamReader(new ByteArrayInputStream(runner.getContentAsByteArray(runner.getFlowFilesForRelationship(STATS).get(0)))));
        reader_stats.beginObject();
        Assert.assertEquals(reader_stats.nextName(), "stats_fields");
        reader_stats.beginObject();
        Assert.assertFalse(reader_stats.hasNext());
        reader_stats.endObject();
        reader_stats.endObject();
        reader.close();
        reader_stats.close();
        solrClient.close();
    }

    @Test
    public void testStats() throws IOException {
        SolrClient solrClient = QuerySolrIT.createSolrClient();
        TestRunner runner = createRunnerWithSolrClient(solrClient);
        runner.setProperty("stats", "true");
        runner.setProperty("stats.field", "integer_single");
        runner.enqueue(new ByteArrayInputStream(new byte[0]));
        runner.run();
        runner.assertTransferCount(STATS, 1);
        JsonReader reader = new JsonReader(new InputStreamReader(new ByteArrayInputStream(runner.getContentAsByteArray(runner.getFlowFilesForRelationship(STATS).get(0)))));
        reader.beginObject();
        Assert.assertEquals(reader.nextName(), "stats_fields");
        reader.beginObject();
        Assert.assertEquals(reader.nextName(), "integer_single");
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "min" :
                    Assert.assertEquals(reader.nextString(), "0.0");
                    break;
                case "max" :
                    Assert.assertEquals(reader.nextString(), "9.0");
                    break;
                case "count" :
                    Assert.assertEquals(reader.nextInt(), 10);
                    break;
                case "sum" :
                    Assert.assertEquals(reader.nextString(), "45.0");
                    break;
                default :
                    reader.skipValue();
                    break;
            }
        } 
        reader.endObject();
        reader.endObject();
        reader.endObject();
        reader.close();
        solrClient.close();
    }

    @Test
    public void testRelationshipRoutings() throws IOException {
        SolrClient solrClient = QuerySolrIT.createSolrClient();
        TestRunner runner = createRunnerWithSolrClient(solrClient);
        runner.setProperty("facet", "true");
        runner.setProperty("stats", "true");
        // Set request handler for request failure
        runner.setProperty(SOLR_PARAM_REQUEST_HANDLER, "/nonexistentrequesthandler");
        // Processor has no input connection and fails
        runner.setNonLoopConnection(false);
        runner.run(1, false);
        runner.assertAllFlowFilesTransferred(FAILURE, 1);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(FAILURE).get(0);
        flowFile.assertAttributeExists(EXCEPTION);
        flowFile.assertAttributeExists(EXCEPTION_MESSAGE);
        runner.clearTransferState();
        // Processor has an input connection and fails
        runner.setNonLoopConnection(true);
        runner.enqueue(new byte[0]);
        runner.run(1, false);
        runner.assertAllFlowFilesTransferred(FAILURE, 1);
        flowFile = runner.getFlowFilesForRelationship(FAILURE).get(0);
        flowFile.assertAttributeExists(EXCEPTION);
        flowFile.assertAttributeExists(EXCEPTION_MESSAGE);
        runner.clearTransferState();
        // Set request handler for successful request
        runner.setProperty(SOLR_PARAM_REQUEST_HANDLER, "/select");
        // Processor has no input connection and succeeds
        runner.setNonLoopConnection(false);
        runner.run(1, false);
        runner.assertTransferCount(RESULTS, 1);
        runner.assertTransferCount(FACETS, 1);
        runner.assertTransferCount(STATS, 1);
        flowFile = runner.getFlowFilesForRelationship(RESULTS).get(0);
        flowFile.assertAttributeExists(ATTRIBUTE_SOLR_CONNECT);
        flowFile.assertAttributeExists(ATTRIBUTE_SOLR_STATUS);
        flowFile.assertAttributeExists(ATTRIBUTE_CURSOR_MARK);
        flowFile.assertAttributeExists(ATTRIBUTE_QUERY_TIME);
        runner.clearTransferState();
        // Processor has an input connection and succeeds
        runner.setNonLoopConnection(true);
        runner.enqueue(new byte[0]);
        runner.run(1, true);
        runner.assertTransferCount(RESULTS, 1);
        runner.assertTransferCount(FACETS, 1);
        runner.assertTransferCount(STATS, 1);
        runner.assertTransferCount(ORIGINAL, 1);
        runner.assertAllFlowFilesContainAttribute(ATTRIBUTE_SOLR_CONNECT);
        flowFile = runner.getFlowFilesForRelationship(RESULTS).get(0);
        flowFile.assertAttributeExists(ATTRIBUTE_SOLR_CONNECT);
        flowFile.assertAttributeExists(ATTRIBUTE_SOLR_STATUS);
        flowFile.assertAttributeExists(ATTRIBUTE_CURSOR_MARK);
        flowFile.assertAttributeExists(ATTRIBUTE_QUERY_TIME);
        flowFile = runner.getFlowFilesForRelationship(FACETS).get(0);
        flowFile.assertAttributeExists(ATTRIBUTE_SOLR_CONNECT);
        flowFile.assertAttributeExists(ATTRIBUTE_SOLR_STATUS);
        flowFile.assertAttributeExists(ATTRIBUTE_CURSOR_MARK);
        flowFile.assertAttributeExists(ATTRIBUTE_QUERY_TIME);
        flowFile = runner.getFlowFilesForRelationship(STATS).get(0);
        flowFile.assertAttributeExists(ATTRIBUTE_SOLR_CONNECT);
        flowFile.assertAttributeExists(ATTRIBUTE_SOLR_STATUS);
        flowFile.assertAttributeExists(ATTRIBUTE_CURSOR_MARK);
        flowFile.assertAttributeExists(ATTRIBUTE_QUERY_TIME);
        runner.clearTransferState();
        solrClient.close();
    }

    @Test
    public void testExpressionLanguageForProperties() throws IOException {
        SolrClient solrClient = QuerySolrIT.createSolrClient();
        TestRunner runner = createRunnerWithSolrClient(solrClient);
        runner.setProperty(SOLR_TYPE, SOLR_TYPE_CLOUD.getValue());
        runner.setProperty(SOLR_PARAM_QUERY, "${query}");
        runner.setProperty(SOLR_PARAM_REQUEST_HANDLER, "${handler}");
        runner.setProperty(SOLR_PARAM_FIELD_LIST, "${fields}");
        runner.setProperty(SOLR_PARAM_SORT, "${sort}");
        runner.setProperty(SOLR_PARAM_START, "${start}");
        runner.setProperty(SOLR_PARAM_ROWS, "${rows}");
        runner.enqueue(new byte[0], new HashMap<String, String>() {
            {
                put("query", "id:(doc0 OR doc1 OR doc2 OR doc3)");
                put("handler", "/select");
                put("fields", "id");
                put("sort", "id desc");
                put("start", "1");
                put("rows", "2");
            }
        });
        runner.run();
        runner.assertTransferCount(RESULTS, 1);
        String expectedXml = "<docs><doc boost=\"1.0\"><field name=\"id\">doc2</field></doc><doc boost=\"1.0\"><field name=\"id\">doc1</field></doc></docs>";
        Assert.assertThat(expectedXml, CompareMatcher.isIdenticalTo(new String(runner.getContentAsByteArray(runner.getFlowFilesForRelationship(RESULTS).get(0)))));
        solrClient.close();
    }

    @Test
    public void testSingleFilterQuery() throws IOException {
        SolrClient solrClient = QuerySolrIT.createSolrClient();
        TestRunner runner = createRunnerWithSolrClient(solrClient);
        runner.setProperty(SOLR_PARAM_SORT, "id asc");
        runner.setProperty(SOLR_PARAM_FIELD_LIST, "id");
        runner.setProperty("fq", "id:(doc2 OR doc3)");
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertTransferCount(RESULTS, 1);
        String expectedXml = "<docs><doc boost=\"1.0\"><field name=\"id\">doc2</field></doc><doc boost=\"1.0\"><field name=\"id\">doc3</field></doc></docs>";
        Assert.assertThat(expectedXml, CompareMatcher.isIdenticalTo(new String(runner.getContentAsByteArray(runner.getFlowFilesForRelationship(RESULTS).get(0)))));
        solrClient.close();
    }

    @Test
    public void testMultipleFilterQueries() throws IOException {
        SolrClient solrClient = QuerySolrIT.createSolrClient();
        TestRunner runner = createRunnerWithSolrClient(solrClient);
        runner.setProperty(SOLR_PARAM_SORT, "id asc");
        runner.setProperty(SOLR_PARAM_FIELD_LIST, "id");
        runner.setProperty("fq.1", "id:(doc0 OR doc1 OR doc2 OR doc3)");
        runner.setProperty("fq.2", "id:(doc1 OR doc2 OR doc3 OR doc4)");
        runner.setProperty("fq.3", "id:(doc2 OR doc3 OR doc4 OR doc5)");
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertTransferCount(RESULTS, 1);
        String expectedXml = "<docs><doc boost=\"1.0\"><field name=\"id\">doc2</field></doc><doc boost=\"1.0\"><field name=\"id\">doc3</field></doc></docs>";
        Assert.assertThat(expectedXml, CompareMatcher.isIdenticalTo(new String(runner.getContentAsByteArray(runner.getFlowFilesForRelationship(RESULTS).get(0)))));
        solrClient.close();
    }

    @Test
    public void testStandardResponse() throws IOException {
        SolrClient solrClient = QuerySolrIT.createSolrClient();
        TestRunner runner = createRunnerWithSolrClient(solrClient);
        runner.setProperty(SOLR_PARAM_QUERY, "id:(doc0 OR doc1)");
        runner.setProperty(SOLR_PARAM_FIELD_LIST, "id");
        runner.setProperty(SOLR_PARAM_SORT, "id desc");
        runner.setNonLoopConnection(false);
        runner.run();
        runner.assertAllFlowFilesTransferred(RESULTS, 1);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(RESULTS).get(0);
        flowFile.assertAttributeExists(ATTRIBUTE_CURSOR_MARK);
        flowFile.assertAttributeExists(ATTRIBUTE_SOLR_STATUS);
        flowFile.assertAttributeExists(ATTRIBUTE_QUERY_TIME);
        String expectedXml = "<docs><doc boost=\"1.0\"><field name=\"id\">doc1</field></doc><doc boost=\"1.0\"><field name=\"id\">doc0</field></doc></docs>";
        Assert.assertThat(expectedXml, CompareMatcher.isIdenticalTo(new String(runner.getContentAsByteArray(flowFile))));
        solrClient.close();
    }

    @Test
    public void testPreserveOriginalContent() throws IOException {
        SolrClient solrClient = QuerySolrIT.createSolrClient();
        TestRunner runner = createRunnerWithSolrClient(solrClient);
        runner.setProperty(SOLR_PARAM_QUERY, "id:doc0");
        runner.setProperty(SOLR_PARAM_FIELD_LIST, "id");
        String content = "test content 123";
        runner.enqueue(content);
        runner.run();
        runner.assertTransferCount(RESULTS, 1);
        runner.assertTransferCount(ORIGINAL, 1);
        String expectedXml = "<docs><doc boost=\"1.0\"><field name=\"id\">doc0</field></doc></docs>";
        Assert.assertThat(expectedXml, CompareMatcher.isIdenticalTo(new String(runner.getContentAsByteArray(runner.getFlowFilesForRelationship(RESULTS).get(0)))));
        Assert.assertEquals(content, new String(runner.getContentAsByteArray(runner.getFlowFilesForRelationship(ORIGINAL).get(0))));
        solrClient.close();
    }

    @Test
    public void testRetrievalOfFullResults() throws IOException {
        SolrClient solrClient = QuerySolrIT.createSolrClient();
        TestRunner runner = createRunnerWithSolrClient(solrClient);
        runner.setProperty(SOLR_PARAM_FIELD_LIST, "id");
        runner.setProperty(SOLR_PARAM_SORT, "id asc");
        runner.setProperty(SOLR_PARAM_ROWS, "2");
        runner.setProperty(AMOUNT_DOCUMENTS_TO_RETURN, RETURN_ALL_RESULTS);
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertTransferCount(RESULTS, 5);
        runner.assertTransferCount(ORIGINAL, 1);
        runner.assertTransferCount(STATS, 0);
        runner.assertTransferCount(FACETS, 0);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(RESULTS);
        Integer documentCounter = 0;
        Integer startParam = 0;
        for (MockFlowFile flowFile : flowFiles) {
            Map<String, String> attributes = flowFile.getAttributes();
            Assert.assertEquals(attributes.get(ATTRIBUTE_SOLR_START), startParam.toString());
            startParam += 2;
            StringBuffer expectedXml = new StringBuffer().append("<docs><doc boost=\"1.0\"><field name=\"id\">doc").append((documentCounter++)).append("</field></doc><doc boost=\"1.0\"><field name=\"id\">doc").append((documentCounter++)).append("</field></doc></docs>");
            Assert.assertThat(expectedXml.toString(), CompareMatcher.isIdenticalTo(new String(runner.getContentAsByteArray(flowFile))));
        }
        solrClient.close();
    }

    @Test
    public void testRetrievalOfFullResults2() throws IOException {
        SolrClient solrClient = QuerySolrIT.createSolrClient();
        TestRunner runner = createRunnerWithSolrClient(solrClient);
        runner.setProperty(SOLR_PARAM_FIELD_LIST, "id");
        runner.setProperty(SOLR_PARAM_SORT, "id asc");
        runner.setProperty(SOLR_PARAM_ROWS, "3");
        runner.setProperty(AMOUNT_DOCUMENTS_TO_RETURN, RETURN_ALL_RESULTS);
        runner.setProperty("facet", "true");
        runner.setProperty("stats", "true");
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertTransferCount(RESULTS, 4);
        runner.assertTransferCount(ORIGINAL, 1);
        runner.assertTransferCount(FACETS, 1);
        runner.assertTransferCount(STATS, 1);
        solrClient.close();
    }

    @Test
    public void testRetrievalOfFullResults3() throws IOException {
        SolrClient solrClient = QuerySolrIT.createSolrClient();
        TestRunner runner = createRunnerWithSolrClient(solrClient);
        runner.setProperty(SOLR_PARAM_FIELD_LIST, "id");
        runner.setProperty(SOLR_PARAM_SORT, "id asc");
        runner.setProperty(SOLR_PARAM_ROWS, "3");
        runner.setProperty(AMOUNT_DOCUMENTS_TO_RETURN, RETURN_ALL_RESULTS);
        runner.setProperty("facet", "true");
        runner.setProperty("stats", "true");
        runner.setNonLoopConnection(false);
        runner.run();
        runner.assertTransferCount(RESULTS, 4);
        runner.assertTransferCount(ORIGINAL, 0);
        runner.assertTransferCount(FACETS, 1);
        runner.assertTransferCount(STATS, 1);
        solrClient.close();
    }

    @Test
    public void testRecordResponse() throws IOException, InitializationException {
        SolrClient solrClient = QuerySolrIT.createSolrClient();
        TestRunner runner = createRunnerWithSolrClient(solrClient);
        runner.setProperty(RETURN_TYPE, MODE_REC.getValue());
        runner.setProperty(SOLR_PARAM_FIELD_LIST, "id,created,integer_single");
        runner.setProperty(SOLR_PARAM_ROWS, "10");
        final String outputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/test-schema.avsc")));
        final JsonRecordSetWriter jsonWriter = new JsonRecordSetWriter();
        runner.addControllerService("writer", jsonWriter);
        runner.setProperty(jsonWriter, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(jsonWriter, SCHEMA_TEXT, outputSchemaText);
        runner.setProperty(jsonWriter, "Pretty Print JSON", "true");
        runner.setProperty(jsonWriter, "Schema Write Strategy", "full-schema-attribute");
        runner.enableControllerService(jsonWriter);
        runner.setProperty(RECORD_WRITER, "writer");
        runner.setNonLoopConnection(false);
        runner.run(1);
        runner.assertQueueEmpty();
        runner.assertTransferCount(RESULTS, 1);
        JsonReader reader = new JsonReader(new InputStreamReader(new ByteArrayInputStream(runner.getContentAsByteArray(runner.getFlowFilesForRelationship(RESULTS).get(0)))));
        reader.beginArray();
        int controlScore = 0;
        while (reader.hasNext()) {
            reader.beginObject();
            while (reader.hasNext()) {
                if (reader.nextName().equals("integer_single")) {
                    controlScore += reader.nextInt();
                } else {
                    reader.skipValue();
                }
            } 
            reader.endObject();
        } 
        reader.close();
        solrClient.close();
        Assert.assertEquals(controlScore, 45);
    }

    // Override createSolrClient and return the passed in SolrClient
    private class TestableProcessor extends QuerySolr {
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

