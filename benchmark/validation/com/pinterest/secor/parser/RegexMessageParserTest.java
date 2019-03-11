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
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
public class RegexMessageParserTest extends TestCase {
    private SecorConfig mConfig;

    private Message mMessageWithMillisTimestamp;

    private Message mMessageWithWrongFormatTimestamp;

    private long timestamp;

    @Test
    public void testExtractTimestampMillisFromKafkaTimestamp() throws Exception {
        Mockito.when(mConfig.useKafkaTimestamp()).thenReturn(true);
        RegexMessageParser regexMessageParser = new RegexMessageParser(mConfig);
        TestCase.assertEquals(timestamp, regexMessageParser.getTimestampMillis(mMessageWithMillisTimestamp));
    }

    @Test
    public void testExtractTimestampMillis() throws Exception {
        RegexMessageParser regexMessageParser = new RegexMessageParser(mConfig);
        TestCase.assertEquals(1442960340000L, regexMessageParser.extractTimestampMillis(mMessageWithMillisTimestamp));
    }

    @Test(expected = NumberFormatException.class)
    public void testExtractTimestampMillisEmpty() throws Exception {
        RegexMessageParser regexMessageParser = new RegexMessageParser(mConfig);
        byte[] emptyBytes2 = "".getBytes();
        regexMessageParser.extractTimestampMillis(new Message("test", 0, 0, null, emptyBytes2, timestamp));
    }

    @Test(expected = NumberFormatException.class)
    public void testExtractTimestampMillisException1() throws Exception {
        RegexMessageParser regexMessageParser = new RegexMessageParser(mConfig);
        regexMessageParser.extractTimestampMillis(mMessageWithWrongFormatTimestamp);
    }
}

