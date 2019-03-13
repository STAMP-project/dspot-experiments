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
package org.apache.nifi.processors.standard;


import java.net.DatagramPacket;
import java.util.concurrent.ArrayBlockingQueue;
import org.apache.nifi.util.TestRunner;
import org.junit.Test;


public class TestPutUDP {
    private static final String UDP_SERVER_ADDRESS = "127.0.0.1";

    private static final String SERVER_VARIABLE = "ALKJAFLKJDFLSKJSDFLKJSDF";

    private static final String UDP_SERVER_ADDRESS_EL = ("${" + (TestPutUDP.SERVER_VARIABLE)) + "}";

    private static final String UNKNOWN_HOST = "fgdsfgsdffd";

    private static final String INVALID_IP_ADDRESS = "300.300.300.300";

    private static final int BUFFER_SIZE = 1024;

    private static final int VALID_LARGE_FILE_SIZE = 32768;

    private static final int VALID_SMALL_FILE_SIZE = 64;

    private static final int INVALID_LARGE_FILE_SIZE = 1000000;

    private static final int LOAD_TEST_ITERATIONS = 500;

    private static final int LOAD_TEST_THREAD_COUNT = 1;

    private static final int DEFAULT_ITERATIONS = 1;

    private static final int DEFAULT_THREAD_COUNT = 1;

    private static final char CONTENT_CHAR = 'x';

    private static final int DATA_WAIT_PERIOD = 1000;

    private static final int DEFAULT_TEST_TIMEOUT_PERIOD = 10000;

    private static final int LONG_TEST_TIMEOUT_PERIOD = 100000;

    private UDPTestServer server;

    private TestRunner runner;

    private ArrayBlockingQueue<DatagramPacket> recvQueue;

    // Test Data
    private static final String[] EMPTY_FILE = new String[]{ "" };

    private static final String[] VALID_FILES = new String[]{ "abcdefghijklmnopqrstuvwxyz", "zyxwvutsrqponmlkjihgfedcba", "12345678", "343424222", "!@\u00a3$%^&*()_+:|{}[];\\" };

    @Test(timeout = TestPutUDP.DEFAULT_TEST_TIMEOUT_PERIOD)
    public void testValidFiles() throws Exception {
        configureProperties(TestPutUDP.UDP_SERVER_ADDRESS, true);
        sendTestData(TestPutUDP.VALID_FILES);
        checkReceivedAllData(TestPutUDP.VALID_FILES);
        checkInputQueueIsEmpty();
    }

    @Test(timeout = TestPutUDP.DEFAULT_TEST_TIMEOUT_PERIOD)
    public void testValidFilesEL() throws Exception {
        configureProperties(TestPutUDP.UDP_SERVER_ADDRESS_EL, true);
        sendTestData(TestPutUDP.VALID_FILES);
        checkReceivedAllData(TestPutUDP.VALID_FILES);
        checkInputQueueIsEmpty();
    }

    @Test(timeout = TestPutUDP.DEFAULT_TEST_TIMEOUT_PERIOD)
    public void testEmptyFile() throws Exception {
        configureProperties(TestPutUDP.UDP_SERVER_ADDRESS, true);
        sendTestData(TestPutUDP.EMPTY_FILE);
        checkRelationships(TestPutUDP.EMPTY_FILE.length, 0);
        checkNoDataReceived();
        checkInputQueueIsEmpty();
    }

    @Test(timeout = TestPutUDP.DEFAULT_TEST_TIMEOUT_PERIOD)
    public void testlargeValidFile() throws Exception {
        configureProperties(TestPutUDP.UDP_SERVER_ADDRESS, true);
        final String[] testData = createContent(TestPutUDP.VALID_LARGE_FILE_SIZE);
        sendTestData(testData);
        checkReceivedAllData(testData);
        checkInputQueueIsEmpty();
    }

    @Test(timeout = TestPutUDP.LONG_TEST_TIMEOUT_PERIOD)
    public void testlargeInvalidFile() throws Exception {
        configureProperties(TestPutUDP.UDP_SERVER_ADDRESS, true);
        String[] testData = createContent(TestPutUDP.INVALID_LARGE_FILE_SIZE);
        sendTestData(testData);
        checkRelationships(0, testData.length);
        checkNoDataReceived();
        checkInputQueueIsEmpty();
        // Check that the processor recovers and can send the next valid file
        testData = createContent(TestPutUDP.VALID_LARGE_FILE_SIZE);
        sendTestData(testData);
        checkReceivedAllData(testData);
        checkInputQueueIsEmpty();
    }

    @Test(timeout = TestPutUDP.DEFAULT_TEST_TIMEOUT_PERIOD)
    public void testReconfiguration() throws Exception {
        configureProperties(TestPutUDP.UDP_SERVER_ADDRESS, true);
        sendTestData(TestPutUDP.VALID_FILES);
        checkReceivedAllData(TestPutUDP.VALID_FILES);
        reset(TestPutUDP.UDP_SERVER_ADDRESS, 0, TestPutUDP.BUFFER_SIZE);
        configureProperties(TestPutUDP.UDP_SERVER_ADDRESS, true);
        sendTestData(TestPutUDP.VALID_FILES);
        checkReceivedAllData(TestPutUDP.VALID_FILES);
        reset(TestPutUDP.UDP_SERVER_ADDRESS, 0, TestPutUDP.BUFFER_SIZE);
        configureProperties(TestPutUDP.UDP_SERVER_ADDRESS, true);
        sendTestData(TestPutUDP.VALID_FILES);
        checkReceivedAllData(TestPutUDP.VALID_FILES);
        checkInputQueueIsEmpty();
    }

    @Test(timeout = TestPutUDP.LONG_TEST_TIMEOUT_PERIOD)
    public void testLoadTest() throws Exception {
        final String[] testData = createContent(TestPutUDP.VALID_SMALL_FILE_SIZE);
        configureProperties(TestPutUDP.UDP_SERVER_ADDRESS, true);
        sendTestData(testData, TestPutUDP.LOAD_TEST_ITERATIONS, TestPutUDP.LOAD_TEST_THREAD_COUNT);
        checkReceivedAllData(testData, TestPutUDP.LOAD_TEST_ITERATIONS);
        checkInputQueueIsEmpty();
    }
}

