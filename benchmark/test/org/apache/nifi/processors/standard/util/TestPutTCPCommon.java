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
package org.apache.nifi.processors.standard.util;


import PutTCP.IDLE_EXPIRATION;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import org.apache.nifi.util.TestRunner;
import org.junit.Test;


public abstract class TestPutTCPCommon {
    private static final String TCP_SERVER_ADDRESS = "127.0.0.1";

    private static final String SERVER_VARIABLE = "ALKJAFLKJDFLSKJSDFLKJSDF";

    private static final String TCP_SERVER_ADDRESS_EL = ("${" + (TestPutTCPCommon.SERVER_VARIABLE)) + "}";

    private static final String UNKNOWN_HOST = "fgdsfgsdffd";

    private static final String INVALID_IP_ADDRESS = "300.300.300.300";

    private static final int MIN_INVALID_PORT = 0;

    private static final int MIN_VALID_PORT = 1;

    private static final int MAX_VALID_PORT = 65535;

    private static final int MAX_INVALID_PORT = 65536;

    private static final int BUFFER_SIZE = 1024;

    private static final int VALID_LARGE_FILE_SIZE = 32768;

    private static final int VALID_SMALL_FILE_SIZE = 64;

    private static final int LOAD_TEST_ITERATIONS = 500;

    private static final int LOAD_TEST_THREAD_COUNT = 1;

    private static final int DEFAULT_ITERATIONS = 1;

    private static final int DEFAULT_THREAD_COUNT = 1;

    private static final char CONTENT_CHAR = 'x';

    private static final int DATA_WAIT_PERIOD = 1000;

    private static final int DEFAULT_TEST_TIMEOUT_PERIOD = 10000;

    private static final int LONG_TEST_TIMEOUT_PERIOD = 100000;

    private static final String OUTGOING_MESSAGE_DELIMITER = "\n";

    private static final String OUTGOING_MESSAGE_DELIMITER_MULTI_CHAR = "{delimiter}\r\n";

    private TCPTestServer server;

    private int tcp_server_port;

    private ArrayBlockingQueue<List<Byte>> recvQueue;

    public boolean ssl;

    public TestRunner runner;

    // Test Data
    private static final String[] EMPTY_FILE = new String[]{ "" };

    private static final String[] VALID_FILES = new String[]{ "abcdefghijklmnopqrstuvwxyz", "zyxwvutsrqponmlkjihgfedcba", "12345678", "343424222", "!@\u00a3$%^&*()_+:|{}[];\\" };

    @Test(timeout = TestPutTCPCommon.DEFAULT_TEST_TIMEOUT_PERIOD)
    public void testValidFiles() throws Exception {
        server = createTestServer(TestPutTCPCommon.TCP_SERVER_ADDRESS, recvQueue, TestPutTCPCommon.OUTGOING_MESSAGE_DELIMITER);
        configureProperties(TestPutTCPCommon.TCP_SERVER_ADDRESS, tcp_server_port, TestPutTCPCommon.OUTGOING_MESSAGE_DELIMITER, false, true);
        sendTestData(TestPutTCPCommon.VALID_FILES);
        checkReceivedAllData(recvQueue, TestPutTCPCommon.VALID_FILES);
        checkInputQueueIsEmpty();
        checkTotalNumConnections(server, 1);
    }

    @Test(timeout = TestPutTCPCommon.DEFAULT_TEST_TIMEOUT_PERIOD)
    public void testValidFilesEL() throws Exception {
        server = createTestServer(TestPutTCPCommon.TCP_SERVER_ADDRESS, recvQueue, TestPutTCPCommon.OUTGOING_MESSAGE_DELIMITER);
        configureProperties(TestPutTCPCommon.TCP_SERVER_ADDRESS_EL, tcp_server_port, TestPutTCPCommon.OUTGOING_MESSAGE_DELIMITER, false, true);
        sendTestData(TestPutTCPCommon.VALID_FILES);
        checkReceivedAllData(recvQueue, TestPutTCPCommon.VALID_FILES);
        checkInputQueueIsEmpty();
        checkTotalNumConnections(server, 1);
    }

    @Test(timeout = TestPutTCPCommon.DEFAULT_TEST_TIMEOUT_PERIOD)
    public void testPruneSenders() throws Exception {
        server = createTestServer(TestPutTCPCommon.TCP_SERVER_ADDRESS, recvQueue, TestPutTCPCommon.OUTGOING_MESSAGE_DELIMITER);
        configureProperties(TestPutTCPCommon.TCP_SERVER_ADDRESS, tcp_server_port, TestPutTCPCommon.OUTGOING_MESSAGE_DELIMITER, false, true);
        sendTestData(TestPutTCPCommon.VALID_FILES);
        Thread.sleep(10);
        checkRelationships(TestPutTCPCommon.VALID_FILES.length, 0);
        checkReceivedAllData(recvQueue, TestPutTCPCommon.VALID_FILES);
        checkInputQueueIsEmpty();
        checkTotalNumConnections(server, 1);
        runner.setProperty(IDLE_EXPIRATION, "1 second");
        Thread.sleep(2000);
        runner.run(1, false, false);
        runner.clearTransferState();
        sendTestData(TestPutTCPCommon.VALID_FILES);
        checkReceivedAllData(recvQueue, TestPutTCPCommon.VALID_FILES);
        checkInputQueueIsEmpty();
        checkTotalNumConnections(server, 2);
    }

    @Test(timeout = TestPutTCPCommon.DEFAULT_TEST_TIMEOUT_PERIOD)
    public void testMultiCharDelimiter() throws Exception {
        server = createTestServer(TestPutTCPCommon.TCP_SERVER_ADDRESS, recvQueue, TestPutTCPCommon.OUTGOING_MESSAGE_DELIMITER_MULTI_CHAR);
        configureProperties(TestPutTCPCommon.TCP_SERVER_ADDRESS, tcp_server_port, TestPutTCPCommon.OUTGOING_MESSAGE_DELIMITER_MULTI_CHAR, false, true);
        sendTestData(TestPutTCPCommon.VALID_FILES);
        checkReceivedAllData(recvQueue, TestPutTCPCommon.VALID_FILES);
        checkInputQueueIsEmpty();
        checkTotalNumConnections(server, 1);
    }

    @Test(timeout = TestPutTCPCommon.DEFAULT_TEST_TIMEOUT_PERIOD)
    public void testConnectionPerFlowFile() throws Exception {
        server = createTestServer(TestPutTCPCommon.TCP_SERVER_ADDRESS, recvQueue, TestPutTCPCommon.OUTGOING_MESSAGE_DELIMITER);
        configureProperties(TestPutTCPCommon.TCP_SERVER_ADDRESS, tcp_server_port, TestPutTCPCommon.OUTGOING_MESSAGE_DELIMITER, true, true);
        sendTestData(TestPutTCPCommon.VALID_FILES);
        checkReceivedAllData(recvQueue, TestPutTCPCommon.VALID_FILES);
        checkInputQueueIsEmpty();
        checkTotalNumConnections(server, TestPutTCPCommon.VALID_FILES.length);
    }

    @Test(timeout = TestPutTCPCommon.DEFAULT_TEST_TIMEOUT_PERIOD)
    public void testConnectionFailure() throws Exception {
        server = createTestServer(TestPutTCPCommon.TCP_SERVER_ADDRESS, recvQueue, TestPutTCPCommon.OUTGOING_MESSAGE_DELIMITER);
        configureProperties(TestPutTCPCommon.TCP_SERVER_ADDRESS, tcp_server_port, TestPutTCPCommon.OUTGOING_MESSAGE_DELIMITER, false, true);
        sendTestData(TestPutTCPCommon.VALID_FILES);
        checkReceivedAllData(recvQueue, TestPutTCPCommon.VALID_FILES);
        checkInputQueueIsEmpty();
        checkTotalNumConnections(server, 1);
        removeTestServer(server);
        runner.clearTransferState();
        sendTestData(TestPutTCPCommon.VALID_FILES);
        Thread.sleep(10);
        checkNoDataReceived(recvQueue);
        checkInputQueueIsEmpty();
        checkTotalNumConnections(server, 1);
        server = createTestServer(TestPutTCPCommon.TCP_SERVER_ADDRESS, recvQueue, TestPutTCPCommon.OUTGOING_MESSAGE_DELIMITER);
        configureProperties(TestPutTCPCommon.TCP_SERVER_ADDRESS, tcp_server_port, TestPutTCPCommon.OUTGOING_MESSAGE_DELIMITER, false, true);
        sendTestData(TestPutTCPCommon.VALID_FILES);
        checkReceivedAllData(recvQueue, TestPutTCPCommon.VALID_FILES);
        checkInputQueueIsEmpty();
        checkTotalNumConnections(server, 1);
    }

    @Test(timeout = TestPutTCPCommon.DEFAULT_TEST_TIMEOUT_PERIOD)
    public void testEmptyFile() throws Exception {
        server = createTestServer(TestPutTCPCommon.TCP_SERVER_ADDRESS, recvQueue, TestPutTCPCommon.OUTGOING_MESSAGE_DELIMITER);
        configureProperties(TestPutTCPCommon.TCP_SERVER_ADDRESS, tcp_server_port, TestPutTCPCommon.OUTGOING_MESSAGE_DELIMITER, false, true);
        sendTestData(TestPutTCPCommon.EMPTY_FILE);
        Thread.sleep(10);
        checkRelationships(TestPutTCPCommon.EMPTY_FILE.length, 0);
        checkEmptyMessageReceived(recvQueue);
        checkInputQueueIsEmpty();
        checkTotalNumConnections(server, 1);
    }

    @Test(timeout = TestPutTCPCommon.DEFAULT_TEST_TIMEOUT_PERIOD)
    public void testlargeValidFile() throws Exception {
        server = createTestServer(TestPutTCPCommon.TCP_SERVER_ADDRESS, recvQueue, TestPutTCPCommon.OUTGOING_MESSAGE_DELIMITER);
        configureProperties(TestPutTCPCommon.TCP_SERVER_ADDRESS, tcp_server_port, TestPutTCPCommon.OUTGOING_MESSAGE_DELIMITER, true, true);
        final String[] testData = createContent(TestPutTCPCommon.VALID_LARGE_FILE_SIZE);
        sendTestData(testData);
        checkReceivedAllData(recvQueue, testData);
        checkInputQueueIsEmpty();
        checkTotalNumConnections(server, testData.length);
    }

    @Test(timeout = TestPutTCPCommon.DEFAULT_TEST_TIMEOUT_PERIOD)
    public void testInvalidPort() throws Exception {
        configureProperties(TestPutTCPCommon.UNKNOWN_HOST, TestPutTCPCommon.MIN_INVALID_PORT, TestPutTCPCommon.OUTGOING_MESSAGE_DELIMITER, false, false);
        configureProperties(TestPutTCPCommon.UNKNOWN_HOST, TestPutTCPCommon.MIN_VALID_PORT, TestPutTCPCommon.OUTGOING_MESSAGE_DELIMITER, false, true);
        configureProperties(TestPutTCPCommon.UNKNOWN_HOST, TestPutTCPCommon.MAX_VALID_PORT, TestPutTCPCommon.OUTGOING_MESSAGE_DELIMITER, false, true);
        configureProperties(TestPutTCPCommon.UNKNOWN_HOST, TestPutTCPCommon.MAX_INVALID_PORT, TestPutTCPCommon.OUTGOING_MESSAGE_DELIMITER, false, false);
    }

    @Test(timeout = TestPutTCPCommon.LONG_TEST_TIMEOUT_PERIOD)
    public void testLoadTest() throws Exception {
        server = createTestServer(TestPutTCPCommon.TCP_SERVER_ADDRESS, recvQueue, TestPutTCPCommon.OUTGOING_MESSAGE_DELIMITER);
        Thread.sleep(1000);
        final String[] testData = createContent(TestPutTCPCommon.VALID_SMALL_FILE_SIZE);
        configureProperties(TestPutTCPCommon.TCP_SERVER_ADDRESS, tcp_server_port, TestPutTCPCommon.OUTGOING_MESSAGE_DELIMITER, false, true);
        sendTestData(testData, TestPutTCPCommon.LOAD_TEST_ITERATIONS, TestPutTCPCommon.LOAD_TEST_THREAD_COUNT);
        checkReceivedAllData(recvQueue, testData, TestPutTCPCommon.LOAD_TEST_ITERATIONS);
        checkInputQueueIsEmpty();
        checkTotalNumConnections(server, 1);
    }
}

