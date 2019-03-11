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


import com.pinterest.secor.common.SecorConfig;
import com.pinterest.secor.message.Message;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import junit.framework.TestCase;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
public class SplitByFieldMessageParserTest extends TestCase {
    private SecorConfig mConfig;

    private Message mMessageWithTypeAndTimestamp;

    private Message mMessageWithoutTimestamp;

    private Message mMessageWithoutType;

    private long timestamp;

    @Test
    public void testExtractTypeAndTimestamp() throws Exception {
        SplitByFieldMessageParser jsonMessageParser = new SplitByFieldMessageParser(mConfig);
        TestCase.assertEquals(1405911096000L, jsonMessageParser.extractTimestampMillis(((JSONObject) (JSONValue.parse(mMessageWithTypeAndTimestamp.getPayload())))));
        TestCase.assertEquals(1405911096123L, jsonMessageParser.extractTimestampMillis(((JSONObject) (JSONValue.parse(mMessageWithoutType.getPayload())))));
        TestCase.assertEquals("event1", jsonMessageParser.extractEventType(((JSONObject) (JSONValue.parse(mMessageWithTypeAndTimestamp.getPayload())))));
        TestCase.assertEquals("event2", jsonMessageParser.extractEventType(((JSONObject) (JSONValue.parse(mMessageWithoutTimestamp.getPayload())))));
    }

    @Test(expected = RuntimeException.class)
    public void testExtractTimestampMillisExceptionNoTimestamp() throws Exception {
        SplitByFieldMessageParser jsonMessageParser = new SplitByFieldMessageParser(mConfig);
        // Throws exception if there's no timestamp, for any reason.
        jsonMessageParser.extractTimestampMillis(((JSONObject) (JSONValue.parse(mMessageWithoutTimestamp.getPayload()))));
    }

    @Test(expected = ClassCastException.class)
    public void testExtractTimestampMillisException1() throws Exception {
        SplitByFieldMessageParser jsonMessageParser = new SplitByFieldMessageParser(mConfig);
        byte[] emptyBytes1 = new byte[]{  };
        jsonMessageParser.extractTimestampMillis(((JSONObject) (JSONValue.parse(emptyBytes1))));
    }

    @Test(expected = ClassCastException.class)
    public void testExtractTimestampMillisException2() throws Exception {
        SplitByFieldMessageParser jsonMessageParser = new SplitByFieldMessageParser(mConfig);
        byte[] emptyBytes2 = "".getBytes();
        jsonMessageParser.extractTimestampMillis(((JSONObject) (JSONValue.parse(emptyBytes2))));
    }

    @Test(expected = RuntimeException.class)
    public void testExtractTimestampMillisExceptionNoType() throws Exception {
        SplitByFieldMessageParser jsonMessageParser = new SplitByFieldMessageParser(mConfig);
        // Throws exception if there's no timestamp, for any reason.
        jsonMessageParser.extractEventType(((JSONObject) (JSONValue.parse(mMessageWithoutType.getPayload()))));
    }

    @Test
    public void testExtractPartitions() throws Exception {
        SplitByFieldMessageParser jsonMessageParser = new SplitByFieldMessageParser(mConfig);
        String expectedEventTypePartition = "event1";
        String expectedDtPartition = "dt=2014-07-21";
        String[] result = jsonMessageParser.extractPartitions(mMessageWithTypeAndTimestamp);
        TestCase.assertEquals(2, result.length);
        TestCase.assertEquals(expectedEventTypePartition, result[0]);
        TestCase.assertEquals(expectedDtPartition, result[1]);
    }

    @Test
    public void testExtractHourlyPartitions() throws Exception {
        Mockito.when(TimestampedMessageParser.usingHourly(mConfig)).thenReturn(true);
        SplitByFieldMessageParser jsonMessageParser = new SplitByFieldMessageParser(mConfig);
        String expectedEventTypePartition = "event1";
        String expectedDtPartition = "dt=2014-07-21";
        String expectedHrPartition = "hr=02";
        String[] result = jsonMessageParser.extractPartitions(mMessageWithTypeAndTimestamp);
        TestCase.assertEquals(3, result.length);
        TestCase.assertEquals(expectedEventTypePartition, result[0]);
        TestCase.assertEquals(expectedDtPartition, result[1]);
        TestCase.assertEquals(expectedHrPartition, result[2]);
    }

    @Test
    public void testExtractHourlyPartitionsForNonUTCTimezone() throws Exception {
        Mockito.when(mConfig.getTimeZone()).thenReturn(TimeZone.getTimeZone("IST"));
        Mockito.when(TimestampedMessageParser.usingHourly(mConfig)).thenReturn(true);
        SplitByFieldMessageParser jsonMessageParser = new SplitByFieldMessageParser(mConfig);
        String expectedEventTypePartition = "event1";
        String expectedDtPartition = "dt=2014-07-21";
        String expectedHrPartition = "hr=08";
        String[] result = jsonMessageParser.extractPartitions(mMessageWithTypeAndTimestamp);
        TestCase.assertEquals(3, result.length);
        TestCase.assertEquals(expectedEventTypePartition, result[0]);
        TestCase.assertEquals(expectedDtPartition, result[1]);
        TestCase.assertEquals(expectedHrPartition, result[2]);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetFinalizedUptoPartitions() throws Exception {
        SplitByFieldMessageParser jsonMessageParser = new SplitByFieldMessageParser(mConfig);
        List<Message> lastMessages = new ArrayList<Message>();
        lastMessages.add(mMessageWithTypeAndTimestamp);
        List<Message> committedMessages = new ArrayList<Message>();
        committedMessages.add(mMessageWithTypeAndTimestamp);
        jsonMessageParser.getFinalizedUptoPartitions(lastMessages, committedMessages);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetPreviousPartitions() throws Exception {
        SplitByFieldMessageParser jsonMessageParser = new SplitByFieldMessageParser(mConfig);
        String[] partitions = new String[]{ "event1", "dt=2014-07-21" };
        jsonMessageParser.getPreviousPartitions(partitions);
    }
}

