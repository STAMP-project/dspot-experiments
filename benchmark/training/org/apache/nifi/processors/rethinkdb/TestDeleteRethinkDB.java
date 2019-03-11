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


import AbstractRethinkDBProcessor.CHARSET;
import AbstractRethinkDBProcessor.DB_HOST;
import AbstractRethinkDBProcessor.DB_NAME;
import AbstractRethinkDBProcessor.DB_PORT;
import AbstractRethinkDBProcessor.DOCUMENT_ID_EMPTY_MESSAGE;
import AbstractRethinkDBProcessor.MAX_DOCUMENTS_SIZE;
import AbstractRethinkDBProcessor.PASSWORD;
import AbstractRethinkDBProcessor.REL_FAILURE;
import AbstractRethinkDBProcessor.REL_NOT_FOUND;
import AbstractRethinkDBProcessor.REL_SUCCESS;
import AbstractRethinkDBProcessor.RETHINKDB_DOCUMENT_ID;
import AbstractRethinkDBProcessor.RETHINKDB_ERROR_MESSAGE;
import AbstractRethinkDBProcessor.TABLE_NAME;
import AbstractRethinkDBProcessor.USERNAME;
import DeleteRethinkDB.DURABILITY;
import DeleteRethinkDB.RESULT_CHANGES_KEY;
import DeleteRethinkDB.RESULT_DELETED_KEY;
import com.google.gson.Gson;
import com.rethinkdb.net.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;


public class TestDeleteRethinkDB {
    private static final String DOCUMENT_ID = "id1";

    private TestRunner runner;

    private AbstractRethinkDBProcessor mockDeleteRethinkDB;

    private Map<String, Object> document = new HashMap<>();

    @Test
    public void testDefaultValid() {
        runner.assertValid();
    }

    @Test
    public void testBlankHost() {
        runner.setProperty(DB_HOST, "");
        runner.assertNotValid();
    }

    @Test
    public void testEmptyPort() {
        runner.setProperty(DB_PORT, "");
        runner.assertNotValid();
    }

    @Test
    public void testEmptyDBName() {
        runner.setProperty(DB_NAME, "");
        runner.assertNotValid();
    }

    @Test
    public void testEmptyUsername() {
        runner.setProperty(USERNAME, "");
        runner.assertNotValid();
    }

    @Test
    public void testEmptyPassword() {
        runner.setProperty(PASSWORD, "p1");
        runner.assertValid();
    }

    @Test
    public void testCharsetUTF8() {
        runner.setProperty(CHARSET, "UTF-8");
        runner.assertValid();
    }

    @Test
    public void testCharsetBlank() {
        runner.setProperty(CHARSET, "");
        runner.assertNotValid();
    }

    @Test
    public void testZeroMaxDocumentSize() {
        runner.setProperty(MAX_DOCUMENTS_SIZE, "0");
        runner.assertNotValid();
    }

    @Test
    public void testBlankDurability() {
        runner.setProperty(DURABILITY, "");
        runner.assertNotValid();
    }

    @Test
    public void testNotFound() {
        runner.assertValid();
        document.put(RESULT_DELETED_KEY, 0L);
        HashMap<String, String> props = new HashMap<>();
        props.put("rethinkdb.id", TestDeleteRethinkDB.DOCUMENT_ID);
        runner.enqueue(new byte[]{  }, props);
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_NOT_FOUND, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_NOT_FOUND);
        Assert.assertNotNull(flowFiles.get(0).getAttribute(RETHINKDB_ERROR_MESSAGE));
        flowFiles.get(0).assertAttributeEquals(RETHINKDB_ERROR_MESSAGE, "Deleted count should be 1 but was 0 for document with id 'id1'");
    }

    @Test
    public void testBlankId() {
        runner.assertValid();
        runner.setProperty(RETHINKDB_DOCUMENT_ID, "${rethinkdb.id}");
        Map<String, String> props = new HashMap<>();
        runner.enqueue(new byte[]{  }, props);
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertNotNull(flowFiles.get(0).getAttribute(RETHINKDB_ERROR_MESSAGE));
        flowFiles.get(0).assertAttributeEquals(RETHINKDB_ERROR_MESSAGE, DOCUMENT_ID_EMPTY_MESSAGE);
    }

    @Test
    public void testNullId() {
        runner.assertValid();
        runner.setProperty(RETHINKDB_DOCUMENT_ID, "${rethinkdb.id}");
        Map<String, String> props = new HashMap<>();
        props.put("rethinkdb.id", null);
        runner.enqueue(new byte[]{  }, props);
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertNotNull(flowFiles.get(0).getAttribute(RETHINKDB_ERROR_MESSAGE));
        flowFiles.get(0).assertAttributeEquals(RETHINKDB_ERROR_MESSAGE, DOCUMENT_ID_EMPTY_MESSAGE);
    }

    @Test
    public void testValidSingleDelete() {
        runner.assertValid();
        HashMap<String, String> props = new HashMap<>();
        props.put("rethinkdb.id", TestDeleteRethinkDB.DOCUMENT_ID);
        runner.enqueue(new byte[]{  }, props);
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        Gson gson = new Gson();
        String json = gson.toJson(((List) (document.get(RESULT_CHANGES_KEY))).get(0));
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        flowFiles.get(0).assertContentEquals(json.toString());
        Assert.assertNull(flowFiles.get(0).getAttribute(RETHINKDB_ERROR_MESSAGE));
    }

    @Test
    public void testGetThrowsException() {
        mockDeleteRethinkDB = new DeleteRethinkDB() {
            @Override
            protected Connection makeConnection() {
                return null;
            }

            @Override
            protected Map<String, Object> deleteDocument(String id, String durablity, Boolean returnChanges) {
                throw new RuntimeException("testException");
            }
        };
        runner = TestRunners.newTestRunner(mockDeleteRethinkDB);
        runner.setProperty(DB_NAME, "test");
        runner.setProperty(DB_HOST, "host1");
        runner.setProperty(DB_PORT, "1234");
        runner.setProperty(USERNAME, "u1");
        runner.setProperty(PASSWORD, "p1");
        runner.setProperty(TABLE_NAME, "t1");
        runner.setProperty(CHARSET, "UTF-8");
        runner.setProperty(RETHINKDB_DOCUMENT_ID, TestDeleteRethinkDB.DOCUMENT_ID);
        runner.assertValid();
        HashMap<String, String> props = new HashMap<>();
        props.put("rethinkdb.id", TestDeleteRethinkDB.DOCUMENT_ID);
        runner.enqueue(new byte[]{  }, props);
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertNotNull(flowFiles.get(0).getAttribute(RETHINKDB_ERROR_MESSAGE));
        flowFiles.get(0).assertAttributeEquals(RETHINKDB_ERROR_MESSAGE, "testException");
    }
}

