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
package org.apache.nifi.processors.influxdb;


import PutInfluxDB.CHARSET;
import PutInfluxDB.CONSISTENCY_LEVEL;
import PutInfluxDB.CONSISTENCY_LEVEL_ONE;
import PutInfluxDB.DB_NAME;
import PutInfluxDB.INFLUX_DB_CONNECTION_TIMEOUT;
import PutInfluxDB.INFLUX_DB_ERROR_MESSAGE;
import PutInfluxDB.INFLUX_DB_URL;
import PutInfluxDB.MAX_RECORDS_SIZE;
import PutInfluxDB.PASSWORD;
import PutInfluxDB.REL_FAILURE;
import PutInfluxDB.REL_MAX_SIZE_EXCEEDED;
import PutInfluxDB.REL_RETRY;
import PutInfluxDB.REL_SUCCESS;
import PutInfluxDB.RETENTION_POLICY;
import PutInfluxDB.USERNAME;
import java.io.EOFException;
import java.net.SocketTimeoutException;
import java.util.List;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBIOException;
import org.junit.Assert;
import org.junit.Test;


public class TestPutInfluxDB {
    private TestRunner runner;

    private PutInfluxDB mockPutInfluxDB;

    @Test
    public void testDefaultValid() {
        runner.assertValid();
    }

    @Test
    public void testBlankDBUrl() {
        runner.setProperty(INFLUX_DB_URL, "");
        runner.assertNotValid();
    }

    @Test
    public void testEmptyDBName() {
        runner.setProperty(DB_NAME, "");
        runner.assertNotValid();
    }

    @Test
    public void testEmptyConnectionTimeout() {
        runner.setProperty(INFLUX_DB_CONNECTION_TIMEOUT, "");
        runner.assertNotValid();
    }

    @Test
    public void testEmptyUsername() {
        runner = TestRunners.newTestRunner(mockPutInfluxDB);
        runner.setProperty(DB_NAME, "test");
        runner.setProperty(PASSWORD, "password");
        runner.setProperty(INFLUX_DB_URL, "http://dbUrl");
        runner.setProperty(CHARSET, "UTF-8");
        runner.setProperty(CONSISTENCY_LEVEL, CONSISTENCY_LEVEL_ONE.getValue());
        runner.setProperty(RETENTION_POLICY, "autogen");
        runner.setProperty(MAX_RECORDS_SIZE, "1 KB");
        runner.assertValid();
        runner.setProperty(USERNAME, "");
        runner.assertNotValid();
    }

    @Test
    public void testEmptyPassword() {
        runner = TestRunners.newTestRunner(mockPutInfluxDB);
        runner.setProperty(DB_NAME, "test");
        runner.setProperty(USERNAME, "username");
        runner.setProperty(INFLUX_DB_URL, "http://dbUrl");
        runner.setProperty(CHARSET, "UTF-8");
        runner.setProperty(CONSISTENCY_LEVEL, CONSISTENCY_LEVEL_ONE.getValue());
        runner.setProperty(RETENTION_POLICY, "autogen");
        runner.setProperty(MAX_RECORDS_SIZE, "1 KB");
        runner.assertValid();
        runner.setProperty(PASSWORD, "");
        runner.assertNotValid();
    }

    @Test
    public void testPasswordEL() {
        runner = TestRunners.newTestRunner(mockPutInfluxDB);
        runner.setVariable("influxdb.password", "password");
        runner.setProperty(PASSWORD, "${influxdb.password}");
        runner.setProperty(DB_NAME, "test");
        runner.setProperty(USERNAME, "username");
        runner.setProperty(INFLUX_DB_URL, "http://dbUrl");
        runner.setProperty(CHARSET, "UTF-8");
        runner.setProperty(CONSISTENCY_LEVEL, CONSISTENCY_LEVEL_ONE.getValue());
        runner.setProperty(RETENTION_POLICY, "autogen");
        runner.setProperty(MAX_RECORDS_SIZE, "1 KB");
        runner.assertValid();
    }

    @Test
    public void testUsernameEL() {
        runner = TestRunners.newTestRunner(mockPutInfluxDB);
        runner.setVariable("influxdb.username", "username");
        runner.setProperty(PASSWORD, "password");
        runner.setProperty(DB_NAME, "test");
        runner.setProperty(USERNAME, "${influxdb.username}");
        runner.setProperty(INFLUX_DB_URL, "http://dbUrl");
        runner.setProperty(CHARSET, "UTF-8");
        runner.setProperty(CONSISTENCY_LEVEL, CONSISTENCY_LEVEL_ONE.getValue());
        runner.setProperty(RETENTION_POLICY, "autogen");
        runner.setProperty(MAX_RECORDS_SIZE, "1 KB");
        runner.assertValid();
    }

    @Test
    public void testCharsetUTF8() {
        runner.setProperty(CHARSET, "UTF-8");
        runner.assertValid();
    }

    @Test
    public void testEmptyConsistencyLevel() {
        runner.setProperty(CONSISTENCY_LEVEL, "");
        runner.assertNotValid();
    }

    @Test
    public void testCharsetBlank() {
        runner.setProperty(CHARSET, "");
        runner.assertNotValid();
    }

    @Test
    public void testZeroMaxDocumentSize() {
        runner.setProperty(MAX_RECORDS_SIZE, "0");
        runner.assertNotValid();
    }

    @Test
    public void testSizeGreaterThanThresholdUsingEL() {
        runner.setVariable("max.record.size", "1 B");
        runner.setProperty(MAX_RECORDS_SIZE, "${max.record.size}");
        runner.assertValid();
        byte[] bytes = new byte[2];
        for (int i = 0; i < (bytes.length); i++) {
            bytes[i] = 'a';
        }
        runner.enqueue(bytes);
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_MAX_SIZE_EXCEEDED, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_MAX_SIZE_EXCEEDED);
        Assert.assertEquals(flowFiles.get(0).getAttribute(INFLUX_DB_ERROR_MESSAGE), ("Max records size exceeded " + (bytes.length)));
    }

    @Test
    public void testSizeGreaterThanThreshold() {
        runner.setProperty(MAX_RECORDS_SIZE, "1 B");
        runner.assertValid();
        byte[] bytes = new byte[2];
        for (int i = 0; i < (bytes.length); i++) {
            bytes[i] = 'a';
        }
        runner.enqueue(bytes);
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_MAX_SIZE_EXCEEDED, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_MAX_SIZE_EXCEEDED);
        Assert.assertEquals(flowFiles.get(0).getAttribute(INFLUX_DB_ERROR_MESSAGE), ("Max records size exceeded " + (bytes.length)));
    }

    @Test
    public void testValidSingleMeasurement() {
        runner.setProperty(MAX_RECORDS_SIZE, "1 MB");
        runner.assertValid();
        byte[] bytes = "test".getBytes();
        runner.enqueue(bytes);
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(flowFiles.get(0).getAttribute(INFLUX_DB_ERROR_MESSAGE), null);
    }

    @Test
    public void testWriteThrowsException() {
        mockPutInfluxDB = new PutInfluxDB() {
            @Override
            protected void writeToInfluxDB(ProcessContext context, String consistencyLevel, String database, String retentionPolicy, String records) {
                throw new RuntimeException("WriteException");
            }
        };
        runner = TestRunners.newTestRunner(mockPutInfluxDB);
        runner.setProperty(DB_NAME, "test");
        runner.setProperty(USERNAME, "u1");
        runner.setProperty(PASSWORD, "p1");
        runner.setProperty(CHARSET, "UTF-8");
        runner.setProperty(INFLUX_DB_URL, "http://dbUrl");
        runner.setProperty(CONSISTENCY_LEVEL, CONSISTENCY_LEVEL_ONE.getValue());
        runner.setProperty(RETENTION_POLICY, "autogen");
        runner.setProperty(MAX_RECORDS_SIZE, "1 KB");
        runner.assertValid();
        byte[] bytes = "test".getBytes();
        runner.enqueue(bytes);
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertEquals(flowFiles.get(0).getAttribute(INFLUX_DB_ERROR_MESSAGE), "WriteException");
    }

    @Test
    public void testWriteThrowsIOException() {
        mockPutInfluxDB = new PutInfluxDB() {
            @Override
            protected void writeToInfluxDB(ProcessContext context, String consistencyLevel, String database, String retentionPolicy, String records) {
                throw new InfluxDBIOException(new EOFException("EOFException"));
            }
        };
        runner = TestRunners.newTestRunner(mockPutInfluxDB);
        runner.setProperty(DB_NAME, "test");
        runner.setProperty(USERNAME, "u1");
        runner.setProperty(PASSWORD, "p1");
        runner.setProperty(CHARSET, "UTF-8");
        runner.setProperty(INFLUX_DB_URL, "http://dbUrl");
        runner.setProperty(CONSISTENCY_LEVEL, CONSISTENCY_LEVEL_ONE.getValue());
        runner.setProperty(RETENTION_POLICY, "autogen");
        runner.setProperty(MAX_RECORDS_SIZE, "1 KB");
        runner.assertValid();
        byte[] bytes = "test".getBytes();
        runner.enqueue(bytes);
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertEquals(flowFiles.get(0).getAttribute(INFLUX_DB_ERROR_MESSAGE), "java.io.EOFException: EOFException");
    }

    @Test
    public void testWriteThrowsSocketTimeoutException() {
        mockPutInfluxDB = new PutInfluxDB() {
            @Override
            protected void writeToInfluxDB(ProcessContext context, String consistencyLevel, String database, String retentionPolicy, String records) {
                throw new InfluxDBIOException(new SocketTimeoutException("SocketTimeoutException"));
            }
        };
        runner = TestRunners.newTestRunner(mockPutInfluxDB);
        runner.setProperty(DB_NAME, "test");
        runner.setProperty(USERNAME, "u1");
        runner.setProperty(PASSWORD, "p1");
        runner.setProperty(CHARSET, "UTF-8");
        runner.setProperty(INFLUX_DB_URL, "http://dbUrl");
        runner.setProperty(CONSISTENCY_LEVEL, CONSISTENCY_LEVEL_ONE.getValue());
        runner.setProperty(RETENTION_POLICY, "autogen");
        runner.setProperty(MAX_RECORDS_SIZE, "1 KB");
        runner.assertValid();
        byte[] bytes = "test".getBytes();
        runner.enqueue(bytes);
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_RETRY, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_RETRY);
        Assert.assertEquals(flowFiles.get(0).getAttribute(INFLUX_DB_ERROR_MESSAGE), "java.net.SocketTimeoutException: SocketTimeoutException");
    }

    @Test
    public void testTriggerThrowsException() {
        mockPutInfluxDB = new PutInfluxDB() {
            @Override
            protected InfluxDB getInfluxDB(ProcessContext context) {
                throw new RuntimeException("testException");
            }
        };
        runner = TestRunners.newTestRunner(mockPutInfluxDB);
        runner.setProperty(DB_NAME, "test");
        runner.setProperty(USERNAME, "u1");
        runner.setProperty(PASSWORD, "p1");
        runner.setProperty(CHARSET, "UTF-8");
        runner.setProperty(INFLUX_DB_URL, "http://dbUrl");
        runner.setProperty(CONSISTENCY_LEVEL, CONSISTENCY_LEVEL_ONE.getValue());
        runner.setProperty(RETENTION_POLICY, "autogen");
        runner.setProperty(MAX_RECORDS_SIZE, "1 KB");
        runner.assertValid();
        byte[] bytes = "test".getBytes();
        runner.enqueue(bytes);
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertEquals(flowFiles.get(0).getAttribute(INFLUX_DB_ERROR_MESSAGE), "testException");
    }

    @Test
    public void testValidArrayMeasurement() {
        runner.setProperty(MAX_RECORDS_SIZE, "1 MB");
        runner.assertValid();
        runner.enqueue("test rain=2\ntest rain=3".getBytes());
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(flowFiles.get(0).getAttribute(INFLUX_DB_ERROR_MESSAGE), null);
    }

    @Test
    public void testInvalidEmptySingleMeasurement() {
        byte[] bytes = "".getBytes();
        runner.enqueue(bytes);
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertEquals(flowFiles.get(0).getAttribute(INFLUX_DB_ERROR_MESSAGE), "Empty measurement size 0");
    }
}

