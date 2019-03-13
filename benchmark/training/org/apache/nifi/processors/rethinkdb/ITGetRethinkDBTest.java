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
package org.apache.nifi.processors.rethinkdb;


import AbstractRethinkDBProcessor.MAX_DOCUMENTS_SIZE;
import AbstractRethinkDBProcessor.REL_FAILURE;
import AbstractRethinkDBProcessor.REL_NOT_FOUND;
import AbstractRethinkDBProcessor.REL_SUCCESS;
import AbstractRethinkDBProcessor.RETHINKDB_ERROR_MESSAGE;
import GetRethinkDB.RETHINKDB_DOCUMENT_ID;
import RethinkDB.r;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minidev.json.JSONObject;
import org.apache.nifi.util.MockFlowFile;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


/**
 * Integration test for getting documents from RethinkDB. Please ensure that the RethinkDB is running
 * on local host with default port and has database test with table test and user
 * <code>admin</code> with password <code>admin</code> before running the integration tests or set the attributes in the
 * test accordingly.
 */
@Ignore("Comment this out for running tests against a real instance of RethinkDB")
public class ITGetRethinkDBTest extends ITAbstractRethinkDBTest {
    @Test
    public void testGetDocumentById() {
        JSONObject message = new JSONObject();
        message.put("id", "1");
        message.put("value", "one");
        r.db(dbName).table(table).insert(message).run(connection);
        long count = r.db(dbName).table(table).count().run(connection);
        Assert.assertEquals("Count should be same", 1L, count);
        runner.setProperty(RETHINKDB_DOCUMENT_ID, "${rethinkdb.id}");
        runner.assertValid();
        Map<String, String> props = new HashMap<>();
        props.put("rethinkdb.id", "1");
        runner.enqueue(new byte[]{  }, props);
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals("Flow file count should be same", 1, flowFiles.size());
        Assert.assertEquals("Error should be null", null, flowFiles.get(0).getAttribute(RETHINKDB_ERROR_MESSAGE));
        Assert.assertEquals("Content should be same size", message.toString().length(), flowFiles.get(0).getSize());
        flowFiles.get(0).assertContentEquals(message.toString());
    }

    @Test
    public void testGetDocumentByIdNotFound() {
        JSONObject message = new JSONObject();
        message.put("id", "1");
        message.put("value", "one");
        r.db(dbName).table(table).insert(message).run(connection);
        long count = r.db(dbName).table(table).count().run(connection);
        Assert.assertEquals("Count should be same", 1L, count);
        runner.setProperty(RETHINKDB_DOCUMENT_ID, "${rethinkdb.id}");
        runner.assertValid();
        Map<String, String> props = new HashMap<>();
        props.put("rethinkdb.id", String.valueOf(System.currentTimeMillis()));
        runner.enqueue(new byte[]{  }, props);
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_NOT_FOUND, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_NOT_FOUND);
        Assert.assertEquals("Flow file count should be same", 1, flowFiles.size());
        Assert.assertNotNull("Error should not be null", flowFiles.get(0).getAttribute(RETHINKDB_ERROR_MESSAGE));
        Assert.assertEquals("Content should be same size", 0, flowFiles.get(0).getSize());
    }

    @Test
    public void testGetDocumentByHardCodedId() {
        JSONObject message = new JSONObject();
        message.put("id", "2");
        message.put("value", "two");
        r.db(dbName).table(table).insert(message).run(connection);
        long count = r.db(dbName).table(table).count().run(connection);
        Assert.assertEquals("Count should be same", 1, count);
        runner.setProperty(RETHINKDB_DOCUMENT_ID, "2");
        runner.assertValid();
        Map<String, String> props = new HashMap<>();
        runner.enqueue(new byte[]{  }, props);
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals("Flow file count should be same", 1, flowFiles.size());
        Assert.assertEquals("Error should be null", null, flowFiles.get(0).getAttribute(RETHINKDB_ERROR_MESSAGE));
        Assert.assertEquals("Content should be same size", message.toString().length(), flowFiles.get(0).getSize());
        flowFiles.get(0).assertContentEquals(message.toString());
    }

    @Test
    public void testGetDocumentTooBig() {
        JSONObject message = new JSONObject();
        message.put("id", "2");
        message.put("value", "two");
        r.db(dbName).table(table).insert(message).run(connection);
        long count = r.db(dbName).table(table).count().run(connection);
        Assert.assertEquals("Count should be same", 1, count);
        runner.setProperty(RETHINKDB_DOCUMENT_ID, "2");
        runner.setProperty(MAX_DOCUMENTS_SIZE, "2B");
        runner.assertValid();
        Map<String, String> props = new HashMap<>();
        runner.enqueue(new byte[]{  }, props);
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertEquals("Flow file count should be same", 1, flowFiles.size());
        String errorMessage = flowFiles.get(0).getAttribute(RETHINKDB_ERROR_MESSAGE);
        Assert.assertNotNull("Error should be not be null", errorMessage);
        Assert.assertEquals("Error message should be same", (("Document too big size " + (message.toJSONString().length())) + " bytes"), errorMessage);
    }
}

