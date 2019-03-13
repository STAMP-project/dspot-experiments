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
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
public class ThriftMessageParserTest extends TestCase {
    private SecorConfig mConfig;

    private long timestamp;

    @Test
    public void testExtractTimestampFromKafkaTimestamp() throws Exception {
        Mockito.when(mConfig.getBoolean("kafka.useTimestamp", false)).thenReturn(true);
        Mockito.when(mConfig.getMessageTimestampName()).thenReturn("blasdjlkjasdkl");
        Mockito.when(mConfig.getMessageTimestampId()).thenReturn(1);
        Mockito.when(mConfig.getMessageTimestampType()).thenReturn("i64");
        Mockito.when(mConfig.getThriftProtocolClass()).thenReturn("org.apache.thrift.protocol.TBinaryProtocol");
        ThriftMessageParser parser = new ThriftMessageParser(mConfig);
        TestCase.assertEquals(1405970352000L, parser.extractTimestampMillis(buildMessage(1405970352L, 1, 2L)));
        TestCase.assertEquals(1405970352123L, parser.extractTimestampMillis(buildMessage(1405970352123L, 1, 2L)));
    }

    @Test
    public void testExtractTimestamp() throws Exception {
        Mockito.when(mConfig.getMessageTimestampName()).thenReturn("blasdjlkjasdkl");
        Mockito.when(mConfig.getMessageTimestampId()).thenReturn(1);
        Mockito.when(mConfig.getMessageTimestampType()).thenReturn("i64");
        Mockito.when(mConfig.getThriftProtocolClass()).thenReturn("org.apache.thrift.protocol.TBinaryProtocol");
        ThriftMessageParser parser = new ThriftMessageParser(mConfig);
        TestCase.assertEquals(1405970352000L, parser.extractTimestampMillis(buildMessage(1405970352L, 1, 2L)));
        TestCase.assertEquals(1405970352123L, parser.extractTimestampMillis(buildMessage(1405970352123L, 1, 2L)));
    }

    @Test
    public void testExtractTimestampTwo() throws Exception {
        Mockito.when(mConfig.getMessageTimestampName()).thenReturn("timestampTwo");
        Mockito.when(mConfig.getMessageTimestampId()).thenReturn(3);
        Mockito.when(mConfig.getMessageTimestampType()).thenReturn("i32");
        Mockito.when(mConfig.getThriftProtocolClass()).thenReturn("org.apache.thrift.protocol.TBinaryProtocol");
        ThriftMessageParser parser = new ThriftMessageParser(mConfig);
        TestCase.assertEquals(1405970352000L, parser.extractTimestampMillis(buildMessage(1L, 1405970352, 2L)));
        TestCase.assertEquals(145028289000L, parser.extractTimestampMillis(buildMessage(1L, 145028289, 2L)));
    }

    @Test
    public void testExtractTimestampThree() throws Exception {
        Mockito.when(mConfig.getMessageTimestampName()).thenReturn("timestampThree");
        Mockito.when(mConfig.getMessageTimestampId()).thenReturn(6);
        Mockito.when(mConfig.getMessageTimestampType()).thenReturn("i64");
        Mockito.when(mConfig.getThriftProtocolClass()).thenReturn("org.apache.thrift.protocol.TBinaryProtocol");
        ThriftMessageParser parser = new ThriftMessageParser(mConfig);
        TestCase.assertEquals(1405970352000L, parser.extractTimestampMillis(buildMessage(1L, 2, 1405970352L)));
        TestCase.assertEquals(1405970352123L, parser.extractTimestampMillis(buildMessage(1L, 2, 1405970352123L)));
    }

    @Test(expected = NullPointerException.class)
    public void testAttemptExtractInvalidField() throws Exception {
        Mockito.when(mConfig.getMessageTimestampName()).thenReturn("requiredField");
        Mockito.when(mConfig.getMessageTimestampId()).thenReturn(2);
        Mockito.when(mConfig.getMessageTimestampType()).thenReturn("i64");
        ThriftMessageParser parser = new ThriftMessageParser(mConfig);
        parser.extractTimestampMillis(buildMessage(1L, 2, 3L));
    }
}

