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
package com.pinterest.secor.parser;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinterest.secor.common.SecorConfig;
import com.pinterest.secor.message.Message;
import java.util.HashMap;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
public class MessagePackParserTest extends TestCase {
    SecorConfig mConfig;

    private MessagePackParser mMessagePackParser;

    private Message mMessageWithSecondsTimestamp;

    private Message mMessageWithMillisTimestamp;

    private Message mMessageWithMillisFloatTimestamp;

    private Message mMessageWithMillisStringTimestamp;

    private ObjectMapper mObjectMapper;

    private long timestamp;

    @Test
    public void testExtractTimestampMillisFromKafkaTimestamp() throws Exception {
        Mockito.when(mConfig.useKafkaTimestamp()).thenReturn(true);
        mMessagePackParser = new MessagePackParser(mConfig);
        TestCase.assertEquals(timestamp, mMessagePackParser.getTimestampMillis(mMessageWithSecondsTimestamp));
        TestCase.assertEquals(timestamp, mMessagePackParser.getTimestampMillis(mMessageWithMillisTimestamp));
        TestCase.assertEquals(timestamp, mMessagePackParser.getTimestampMillis(mMessageWithMillisFloatTimestamp));
        TestCase.assertEquals(timestamp, mMessagePackParser.getTimestampMillis(mMessageWithMillisStringTimestamp));
    }

    @Test
    public void testExtractTimestampMillis() throws Exception {
        TestCase.assertEquals(1405970352000L, mMessagePackParser.getTimestampMillis(mMessageWithSecondsTimestamp));
        TestCase.assertEquals(1405970352123L, mMessagePackParser.getTimestampMillis(mMessageWithMillisTimestamp));
        TestCase.assertEquals(1405970352123L, mMessagePackParser.getTimestampMillis(mMessageWithMillisFloatTimestamp));
        TestCase.assertEquals(1405970352123L, mMessagePackParser.getTimestampMillis(mMessageWithMillisStringTimestamp));
    }

    @Test(expected = NullPointerException.class)
    public void testMissingTimestamp() throws Exception {
        HashMap<String, Object> mapWithoutTimestamp = new HashMap<String, Object>();
        mapWithoutTimestamp.put("email", "mary@example.com");
        Message nMessageWithoutTimestamp = new Message("test", 0, 0, null, mObjectMapper.writeValueAsBytes(mapWithoutTimestamp), timestamp);
        mMessagePackParser.getTimestampMillis(nMessageWithoutTimestamp);
    }

    @Test(expected = NumberFormatException.class)
    public void testUnsupportedTimestampFormat() throws Exception {
        HashMap<String, Object> mapWitUnsupportedFormatTimestamp = new HashMap<String, Object>();
        mapWitUnsupportedFormatTimestamp.put("ts", "2014-11-14T18:12:52.878Z");
        Message nMessageWithUnsupportedFormatTimestamp = new Message("test", 0, 0, null, mObjectMapper.writeValueAsBytes(mapWitUnsupportedFormatTimestamp), timestamp);
        mMessagePackParser.getTimestampMillis(nMessageWithUnsupportedFormatTimestamp);
    }

    @Test(expected = NullPointerException.class)
    public void testNullTimestamp() throws Exception {
        HashMap<String, Object> mapWitNullTimestamp = new HashMap<String, Object>();
        mapWitNullTimestamp.put("ts", null);
        Message nMessageWithNullTimestamp = new Message("test", 0, 0, null, mObjectMapper.writeValueAsBytes(mapWitNullTimestamp), timestamp);
        mMessagePackParser.getTimestampMillis(nMessageWithNullTimestamp);
    }

    @Test
    public void testExtractPartitions() throws Exception {
        String expectedPartition = "dt=2014-07-21";
        String[] resultSeconds = mMessagePackParser.extractPartitions(mMessageWithSecondsTimestamp);
        TestCase.assertEquals(1, resultSeconds.length);
        TestCase.assertEquals(expectedPartition, resultSeconds[0]);
        String[] resultMillis = mMessagePackParser.extractPartitions(mMessageWithMillisTimestamp);
        TestCase.assertEquals(1, resultMillis.length);
        TestCase.assertEquals(expectedPartition, resultMillis[0]);
    }
}

