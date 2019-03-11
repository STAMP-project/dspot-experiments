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
package com.pinterest.secor.common;


import com.pinterest.secor.message.ParsedMessage;
import java.util.Arrays;
import junit.framework.TestCase;


/**
 * LogFileTest tests the logic operating on lof file paths.
 *
 * @author Pawel Garbacki (pawel@pinterest.com)
 */
public class LogFilePathTest extends TestCase {
    private static final String PREFIX = "/some_parent_dir";

    private static final String TOPIC = "some_topic";

    private static final String[] PARTITIONS = new String[]{ "some_partition", "some_other_partition" };

    private static final int GENERATION = 10;

    private static final int KAFKA_PARTITION = 0;

    private static final long LAST_COMMITTED_OFFSET = 100;

    private static final String PATH = "/some_parent_dir/some_topic/some_partition/some_other_partition/" + "10_0_00000000000000000100";

    private static final String CRC_PATH = "/some_parent_dir/some_topic/some_partition/some_other_partition/" + ".10_0_00000000000000000100.crc";

    private LogFilePath mLogFilePath;

    private long timestamp;

    public void testConstructFromMessage() throws Exception {
        ParsedMessage message = new ParsedMessage(LogFilePathTest.TOPIC, LogFilePathTest.KAFKA_PARTITION, 1000, null, "some_payload".getBytes(), LogFilePathTest.PARTITIONS, timestamp);
        LogFilePath logFilePath = new LogFilePath(LogFilePathTest.PREFIX, LogFilePathTest.GENERATION, LogFilePathTest.LAST_COMMITTED_OFFSET, message, "");
        TestCase.assertEquals(LogFilePathTest.PATH, logFilePath.getLogFilePath());
    }

    public void testConstructFromPath() throws Exception {
        LogFilePath logFilePath = new LogFilePath("/some_parent_dir", LogFilePathTest.PATH);
        TestCase.assertEquals(LogFilePathTest.PATH, logFilePath.getLogFilePath());
        TestCase.assertEquals(LogFilePathTest.TOPIC, logFilePath.getTopic());
        TestCase.assertTrue(Arrays.equals(LogFilePathTest.PARTITIONS, logFilePath.getPartitions()));
        TestCase.assertEquals(LogFilePathTest.GENERATION, logFilePath.getGeneration());
        TestCase.assertEquals(LogFilePathTest.KAFKA_PARTITION, logFilePath.getKafkaPartition());
        TestCase.assertEquals(LogFilePathTest.LAST_COMMITTED_OFFSET, logFilePath.getOffset());
    }

    public void testGetters() throws Exception {
        TestCase.assertEquals(LogFilePathTest.TOPIC, mLogFilePath.getTopic());
        TestCase.assertTrue(Arrays.equals(LogFilePathTest.PARTITIONS, mLogFilePath.getPartitions()));
        TestCase.assertEquals(LogFilePathTest.GENERATION, mLogFilePath.getGeneration());
        TestCase.assertEquals(LogFilePathTest.KAFKA_PARTITION, mLogFilePath.getKafkaPartition());
        TestCase.assertEquals(LogFilePathTest.LAST_COMMITTED_OFFSET, mLogFilePath.getOffset());
    }

    public void testGetLogFilePath() throws Exception {
        TestCase.assertEquals(LogFilePathTest.PATH, mLogFilePath.getLogFilePath());
    }

    public void testGetLogFileCrcPath() throws Exception {
        TestCase.assertEquals(LogFilePathTest.CRC_PATH, mLogFilePath.getLogFileCrcPath());
    }
}

