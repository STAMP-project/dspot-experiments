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
package org.apache.nifi.processors.mongodb.gridfs;


import CoreAttributes.FILENAME;
import FetchGridFS.FILE_NAME;
import FetchGridFS.OPERATION_MODE;
import FetchGridFS.QUERY;
import FetchGridFS.QUERY_ATTRIBUTE;
import FetchGridFS.REL_FAILURE;
import FetchGridFS.REL_ORIGINAL;
import FetchGridFS.REL_SUCCESS;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.nifi.components.AllowableValue;
import org.apache.nifi.processors.mongodb.QueryHelper;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


public class FetchGridFSIT extends GridFSITTestBase {
    TestRunner runner;

    static final String BUCKET = "get_test_bucket";

    @Test
    public void testGetOneByName() {
        final String fileName = "get_by_name.txt";
        final String content = "Hello, world";
        ObjectId id = writeTestFile(fileName, content, FetchGridFSIT.BUCKET, new HashMap<>());
        Assert.assertNotNull(id);
        String query = String.format("{\"filename\": \"%s\"}", fileName);
        runner.enqueue(query);
        runner.run();
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.assertTransferCount(REL_SUCCESS, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        byte[] rawData = runner.getContentAsByteArray(flowFiles.get(0));
        Assert.assertEquals("Data did not match for the file", new String(rawData), content);
        runner.clearTransferState();
        runner.setProperty(QUERY, query);
        runner.enqueue("test");
        runner.run();
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.assertTransferCount(REL_SUCCESS, 1);
        flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        rawData = runner.getContentAsByteArray(flowFiles.get(0));
        Assert.assertEquals("Data did not match for the file", new String(rawData), content);
    }

    @Test
    public void testGetMany() {
        String baseName = "test_file_%d.txt";
        String content = "Hello, world take %d";
        for (int index = 0; index < 5; index++) {
            ObjectId id = writeTestFile(String.format(baseName, index), String.format(content, index), FetchGridFSIT.BUCKET, new HashMap<>());
            Assert.assertNotNull(id);
        }
        AllowableValue[] values = new AllowableValue[]{ QueryHelper.MODE_MANY_COMMITS, QueryHelper.MODE_ONE_COMMIT };
        for (AllowableValue value : values) {
            String query = "{}";
            runner.setProperty(OPERATION_MODE, value);
            runner.enqueue(query);
            runner.run();
            runner.assertTransferCount(REL_FAILURE, 0);
            runner.assertTransferCount(REL_ORIGINAL, 1);
            runner.assertTransferCount(REL_SUCCESS, 5);
            runner.clearTransferState();
        }
    }

    @Test
    public void testQueryAttribute() {
        final String fileName = "get_by_name.txt";
        final String content = "Hello, world";
        ObjectId id = writeTestFile(fileName, content, FetchGridFSIT.BUCKET, new HashMap<>());
        Assert.assertNotNull(id);
        final String queryAttr = "gridfs.query.used";
        final Map<String, String> attrs = new HashMap<>();
        attrs.put(FILENAME.key(), fileName);
        runner.setProperty(FILE_NAME, String.format("${%s}", FILENAME.key()));
        runner.setProperty(QUERY_ATTRIBUTE, queryAttr);
        runner.enqueue(content, attrs);
        runner.run();
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.assertTransferCount(REL_SUCCESS, 1);
        MockFlowFile mff = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        String attr = mff.getAttribute(queryAttr);
        Assert.assertNotNull("Query attribute was null.", attr);
        Assert.assertTrue("Wrong content.", attr.contains("filename"));
        runner.clearTransferState();
        id = writeTestFile(fileName, content, FetchGridFSIT.BUCKET, new HashMap<String, Object>() {
            {
                put("lookupKey", "xyz");
            }
        });
        Assert.assertNotNull(id);
        String query = "{ \"metadata\": { \"lookupKey\": \"xyz\" }}";
        runner.removeProperty(FILE_NAME);
        runner.setProperty(QUERY, query);
        runner.enqueue(content, attrs);
        runner.run();
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.assertTransferCount(REL_SUCCESS, 1);
        mff = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        attr = mff.getAttribute(queryAttr);
        Assert.assertNotNull("Query attribute was null.", attr);
        Assert.assertTrue("Wrong content.", attr.contains("metadata"));
    }

    @Test
    public void testGetQueryFromBody() {
        runner.enqueue("{}");
        testQueryFromSource(0, 1, 1);
    }

    @Test
    public void testGetQueryFromQueryParam() {
        runner.setProperty(QUERY, "{}");
        runner.enqueue("");
        testQueryFromSource(0, 1, 1);
    }

    @Test
    public void testGetQueryFromFileNameParam() {
        Map<String, String> attr = new HashMap<>();
        attr.put(FILENAME.key(), "get_by_name.txt");
        runner.setProperty(FILE_NAME, String.format("${%s}", FILENAME.key()));
        runner.enqueue("test", attr);
        testQueryFromSource(0, 1, 1);
    }
}

