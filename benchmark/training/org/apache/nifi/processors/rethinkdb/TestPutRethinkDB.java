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


import PutRethinkDB.CHARSET;
import PutRethinkDB.CONFLICT_STRATEGY;
import PutRethinkDB.CONFLICT_STRATEGY_UPDATE;
import PutRethinkDB.DB_HOST;
import PutRethinkDB.DB_NAME;
import PutRethinkDB.DB_PORT;
import PutRethinkDB.DURABILITY;
import PutRethinkDB.DURABILITY_HARD;
import PutRethinkDB.MAX_DOCUMENTS_SIZE;
import PutRethinkDB.PASSWORD;
import PutRethinkDB.REL_FAILURE;
import PutRethinkDB.REL_SUCCESS;
import PutRethinkDB.RESULT_ERROR_KEY;
import PutRethinkDB.RESULT_FIRST_ERROR_KEY;
import PutRethinkDB.RESULT_INSERTED_KEY;
import PutRethinkDB.RESULT_WARNINGS_KEY;
import PutRethinkDB.RETHINKDB_ERROR_MESSAGE;
import PutRethinkDB.RETHINKDB_INSERT_RESULT_DELETED_KEY;
import PutRethinkDB.RETHINKDB_INSERT_RESULT_ERROR_KEY;
import PutRethinkDB.RETHINKDB_INSERT_RESULT_FIRST_ERROR_KEY;
import PutRethinkDB.RETHINKDB_INSERT_RESULT_GENERATED_KEYS_KEY;
import PutRethinkDB.RETHINKDB_INSERT_RESULT_INSERTED_KEY;
import PutRethinkDB.RETHINKDB_INSERT_RESULT_REPLACED_KEY;
import PutRethinkDB.RETHINKDB_INSERT_RESULT_SKIPPED_KEY;
import PutRethinkDB.RETHINKDB_INSERT_RESULT_UNCHANGED_KEY;
import PutRethinkDB.TABLE_NAME;
import PutRethinkDB.USERNAME;
import com.rethinkdb.gen.ast.Insert;
import com.rethinkdb.net.Connection;
import java.util.HashMap;
import java.util.List;
import net.minidev.json.JSONObject;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.json.simple.JSONArray;
import org.junit.Assert;
import org.junit.Test;


public class TestPutRethinkDB {
    private TestRunner runner;

    private PutRethinkDB mockPutRethinkDB;

    protected HashMap<String, Object> result = new HashMap<>();

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
    public void testBlankConflictStrategy() {
        runner.setProperty(CONFLICT_STRATEGY, "");
        runner.assertNotValid();
    }

    @Test
    public void testBlankDurability() {
        runner.setProperty(DURABILITY, "");
        runner.assertNotValid();
    }

    @Test
    public void testSizeGreaterThanThreshold() {
        runner.setProperty(MAX_DOCUMENTS_SIZE, "1 B");
        runner.assertValid();
        byte[] bytes = new byte[2];
        for (int i = 0; i < (bytes.length); i++) {
            bytes[i] = 'a';
        }
        runner.enqueue(bytes);
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertNotNull(flowFiles.get(0).getAttribute(RETHINKDB_ERROR_MESSAGE));
    }

    @Test
    public void testValidSingleMessage() {
        runner.setProperty(MAX_DOCUMENTS_SIZE, "1 MB");
        runner.assertValid();
        result.remove(RESULT_FIRST_ERROR_KEY);
        result.remove(RESULT_WARNINGS_KEY);
        JSONObject message = new JSONObject();
        message.put("hello", "rethinkdb");
        byte[] bytes = message.toJSONString().getBytes();
        runner.enqueue(bytes);
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_DELETED_KEY), "0");
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_ERROR_KEY), "0");
        Assert.assertNotNull(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_GENERATED_KEYS_KEY));
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_INSERTED_KEY), "1");
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_REPLACED_KEY), "0");
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_SKIPPED_KEY), "0");
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_UNCHANGED_KEY), "0");
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_FIRST_ERROR_KEY), "null");
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_FIRST_ERROR_KEY), "null");
    }

    @Test
    public void testInsertThrowsException() {
        mockPutRethinkDB = new PutRethinkDB() {
            @Override
            protected Connection makeConnection() {
                return null;
            }

            @Override
            protected HashMap<String, Object> runInsert(Insert insert) {
                throw new RuntimeException("testException");
            }
        };
        runner = TestRunners.newTestRunner(mockPutRethinkDB);
        runner.setProperty(DB_NAME, "test");
        runner.setProperty(DB_HOST, "host1");
        runner.setProperty(DB_PORT, "1234");
        runner.setProperty(USERNAME, "u1");
        runner.setProperty(PASSWORD, "p1");
        runner.setProperty(TABLE_NAME, "t1");
        runner.setProperty(CHARSET, "UTF-8");
        runner.setProperty(CONFLICT_STRATEGY, CONFLICT_STRATEGY_UPDATE);
        runner.setProperty(DURABILITY, DURABILITY_HARD);
        runner.setProperty(MAX_DOCUMENTS_SIZE, "1 KB");
        runner.assertValid();
        JSONObject message = new JSONObject();
        message.put("hello", "rethinkdb");
        byte[] bytes = message.toJSONString().getBytes();
        runner.enqueue(bytes);
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_DELETED_KEY), null);
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_ERROR_KEY), null);
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_GENERATED_KEYS_KEY), null);
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_INSERTED_KEY), null);
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_REPLACED_KEY), null);
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_SKIPPED_KEY), null);
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_UNCHANGED_KEY), null);
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_FIRST_ERROR_KEY), null);
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_ERROR_MESSAGE), "testException");
    }

    @Test(expected = AssertionError.class)
    public void testMakeConnectionThrowsException() {
        mockPutRethinkDB = new PutRethinkDB() {
            @Override
            protected Connection makeConnection() {
                throw new RuntimeException("testException");
            }
        };
        runner = TestRunners.newTestRunner(mockPutRethinkDB);
        runner.setProperty(DB_NAME, "test");
        runner.setProperty(DB_HOST, "host1");
        runner.setProperty(DB_PORT, "1234");
        runner.setProperty(USERNAME, "u1");
        runner.setProperty(PASSWORD, "p1");
        runner.setProperty(TABLE_NAME, "t1");
        runner.setProperty(CHARSET, "UTF-8");
        runner.setProperty(CONFLICT_STRATEGY, CONFLICT_STRATEGY_UPDATE);
        runner.setProperty(DURABILITY, DURABILITY_HARD);
        runner.setProperty(MAX_DOCUMENTS_SIZE, "1 KB");
        runner.assertValid();
        JSONObject message = new JSONObject();
        message.put("hello", "rethinkdb");
        byte[] bytes = message.toJSONString().getBytes();
        runner.enqueue(bytes);
        runner.run(1, true, true);
    }

    @Test
    public void testMessageError() {
        runner.setProperty(MAX_DOCUMENTS_SIZE, "1 MB");
        runner.assertValid();
        result.put(RESULT_FIRST_ERROR_KEY, "Error occured");
        result.put(RESULT_ERROR_KEY, 1L);
        result.put(RESULT_INSERTED_KEY, 0L);
        result.remove(RESULT_WARNINGS_KEY);
        JSONObject message = new JSONObject();
        message.put("hello", "rethinkdb");
        byte[] bytes = message.toJSONString().getBytes();
        runner.enqueue(bytes);
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_DELETED_KEY), "0");
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_ERROR_KEY), "1");
        Assert.assertNotNull(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_GENERATED_KEYS_KEY));
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_INSERTED_KEY), "0");
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_REPLACED_KEY), "0");
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_SKIPPED_KEY), "0");
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_UNCHANGED_KEY), "0");
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_FIRST_ERROR_KEY), "Error occured");
    }

    @Test
    public void testValidArrayMessage() {
        runner.setProperty(MAX_DOCUMENTS_SIZE, "1 MB");
        runner.assertValid();
        result.remove(RESULT_FIRST_ERROR_KEY);
        result.remove(RESULT_WARNINGS_KEY);
        result.put(RESULT_INSERTED_KEY, 2L);
        JSONObject message1 = new JSONObject();
        message1.put("hello", "rethinkdb");
        JSONObject message2 = new JSONObject();
        message2.put("hello2", "rethinkdb2");
        JSONArray array = new JSONArray();
        array.add(message1);
        array.add(message2);
        byte[] bytes = array.toJSONString().getBytes();
        runner.enqueue(bytes);
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_DELETED_KEY), "0");
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_ERROR_KEY), "0");
        Assert.assertNotNull(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_GENERATED_KEYS_KEY));
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_INSERTED_KEY), "2");
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_REPLACED_KEY), "0");
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_SKIPPED_KEY), "0");
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_UNCHANGED_KEY), "0");
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_FIRST_ERROR_KEY), "null");
    }

    @Test
    public void testInvalidSingleMessage() {
        byte[] bytes = "bad json".getBytes();
        runner.enqueue(bytes);
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_DELETED_KEY), null);
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_ERROR_KEY), null);
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_GENERATED_KEYS_KEY), null);
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_INSERTED_KEY), null);
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_REPLACED_KEY), null);
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_SKIPPED_KEY), null);
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_UNCHANGED_KEY), null);
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_FIRST_ERROR_KEY), null);
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_FIRST_ERROR_KEY), null);
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_ERROR_MESSAGE), "null");
    }

    @Test
    public void testInvalidEmptySingleMessage() {
        byte[] bytes = "".getBytes();
        runner.enqueue(bytes);
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_DELETED_KEY), null);
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_ERROR_KEY), null);
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_GENERATED_KEYS_KEY), null);
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_INSERTED_KEY), null);
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_REPLACED_KEY), null);
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_SKIPPED_KEY), null);
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_UNCHANGED_KEY), null);
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_FIRST_ERROR_KEY), null);
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_INSERT_RESULT_FIRST_ERROR_KEY), null);
        Assert.assertEquals(flowFiles.get(0).getAttribute(RETHINKDB_ERROR_MESSAGE), "Empty message size 0");
    }
}

