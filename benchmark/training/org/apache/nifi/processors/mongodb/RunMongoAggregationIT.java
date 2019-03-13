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
package org.apache.nifi.processors.mongodb;


import RunMongoAggregation.CLIENT_SERVICE;
import RunMongoAggregation.JSON_EXTENDED;
import RunMongoAggregation.JSON_STANDARD;
import RunMongoAggregation.JSON_TYPE;
import RunMongoAggregation.QUERY;
import RunMongoAggregation.REL_FAILURE;
import RunMongoAggregation.REL_ORIGINAL;
import RunMongoAggregation.REL_RESULTS;
import RunMongoAggregation.URI;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import org.apache.nifi.mongodb.MongoDBClientService;
import org.apache.nifi.mongodb.MongoDBControllerService;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;


public class RunMongoAggregationIT {
    private static final String MONGO_URI = "mongodb://localhost";

    private static final String DB_NAME = String.format("agg_test-%s", Calendar.getInstance().getTimeInMillis());

    private static final String COLLECTION_NAME = "agg_test_data";

    private static final String AGG_ATTR = "mongo.aggregation.query";

    private TestRunner runner;

    private MongoClient mongoClient;

    private Map<String, Integer> mappings;

    private Calendar now = Calendar.getInstance();

    @Test
    public void testAggregation() throws Exception {
        final String queryInput = "[\n" + (((((((((((((("    {\n" + "        \"$project\": {\n") + "            \"_id\": 0,\n") + "            \"val\": 1\n") + "        }\n") + "    },\n") + "    {\n") + "        \"$group\": {\n") + "            \"_id\": \"$val\",\n") + "            \"doc_count\": {\n") + "                \"$sum\": 1\n") + "            }\n") + "        }\n") + "    }\n") + "]");
        runner.setProperty(QUERY, queryInput);
        runner.enqueue("test");
        runner.run(1, true, true);
        evaluateRunner(1);
        runner.clearTransferState();
        runner.setIncomingConnection(false);
        runner.run();// Null parent flowfile

        evaluateRunner(0);
        runner.run();
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_RESULTS);
        for (MockFlowFile mff : flowFiles) {
            String val = mff.getAttribute(RunMongoAggregationIT.AGG_ATTR);
            Assert.assertNotNull("Missing query attribute", val);
            Assert.assertEquals("Value was wrong", val, queryInput);
        }
    }

    @Test
    public void testExpressionLanguageSupport() throws Exception {
        runner.setVariable("fieldName", "$val");
        runner.setProperty(QUERY, ("[\n" + (((((((((((((("    {\n" + "        \"$project\": {\n") + "            \"_id\": 0,\n") + "            \"val\": 1\n") + "        }\n") + "    },\n") + "    {\n") + "        \"$group\": {\n") + "            \"_id\": \"${fieldName}\",\n") + "            \"doc_count\": {\n") + "                \"$sum\": 1\n") + "            }\n") + "        }\n") + "    }\n") + "]")));
        runner.enqueue("test");
        runner.run(1, true, true);
        evaluateRunner(1);
    }

    @Test
    public void testInvalidQuery() {
        runner.setProperty(QUERY, ("[\n" + (((((("    {\n" + "        \"$invalid_stage\": {\n") + "            \"_id\": 0,\n") + "            \"val\": 1\n") + "        }\n") + "    }\n") + "]")));
        runner.enqueue("test");
        runner.run(1, true, true);
        runner.assertTransferCount(REL_RESULTS, 0);
        runner.assertTransferCount(REL_ORIGINAL, 0);
        runner.assertTransferCount(REL_FAILURE, 1);
    }

    @Test
    public void testJsonTypes() throws IOException {
        runner.setProperty(JSON_TYPE, JSON_STANDARD);
        runner.setProperty(QUERY, "[ { \"$project\": { \"myArray\": [ \"$val\", \"$date\" ] } } ]");
        runner.enqueue("test");
        runner.run(1, true, true);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_RESULTS);
        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        for (MockFlowFile mockFlowFile : flowFiles) {
            byte[] raw = runner.getContentAsByteArray(mockFlowFile);
            Map<String, List<String>> read = mapper.readValue(raw, Map.class);
            Assert.assertTrue(read.get("myArray").get(1).equalsIgnoreCase(format.format(now.getTime())));
        }
        runner.clearTransferState();
        runner.setProperty(JSON_TYPE, JSON_EXTENDED);
        runner.enqueue("test");
        runner.run(1, true, true);
        flowFiles = runner.getFlowFilesForRelationship(REL_RESULTS);
        for (MockFlowFile mockFlowFile : flowFiles) {
            byte[] raw = runner.getContentAsByteArray(mockFlowFile);
            Map<String, List<Long>> read = mapper.readValue(raw, Map.class);
            Assert.assertTrue(((read.get("myArray").get(1)) == (now.getTimeInMillis())));
        }
    }

    @Test
    public void testClientService() throws Exception {
        MongoDBClientService clientService = new MongoDBControllerService();
        runner.addControllerService("clientService", clientService);
        runner.removeProperty(URI);
        runner.setProperty(clientService, MongoDBControllerService.URI, RunMongoAggregationIT.MONGO_URI);
        runner.setProperty(CLIENT_SERVICE, "clientService");
        runner.setProperty(QUERY, ("[\n" + ((((("    {\n" + "        \"$project\": {\n") + "            \"_id\": 0,\n") + "            \"val\": 1\n") + "        }\n") + "    }]")));
        runner.enableControllerService(clientService);
        runner.assertValid();
        runner.enqueue("{}");
        runner.run();
        runner.assertTransferCount(REL_RESULTS, 9);
    }
}

