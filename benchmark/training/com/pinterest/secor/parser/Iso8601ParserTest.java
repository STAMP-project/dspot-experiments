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
public class Iso8601ParserTest extends TestCase {
    private SecorConfig mConfig;

    private Message mFormat1;

    private Message mFormat2;

    private Message mFormat3;

    private Message mFormat4;

    private Message mInvalidDate;

    private Message mNestedISOFormat;

    private Message mNanosecondISOFormat;

    private Message mMissingDate;

    private long timestamp;

    @Test
    public void testExtractTimestampMillis() throws Exception {
        Iso8601MessageParser parser = new Iso8601MessageParser(mConfig);
        TestCase.assertEquals(1406717600001L, parser.getTimestampMillis(mFormat1));
        TestCase.assertEquals(1406631200000L, parser.getTimestampMillis(mFormat2));
        TestCase.assertEquals(994204800000L, parser.getTimestampMillis(mFormat3));
        TestCase.assertEquals(1456943774000L, parser.getTimestampMillis(mFormat4));
        TestCase.assertEquals(1136246399999L, parser.getTimestampMillis(mNanosecondISOFormat));
        // Return 0 if there's no timestamp, for any reason.
        TestCase.assertEquals(0L, parser.getTimestampMillis(mInvalidDate));
        TestCase.assertEquals(0L, parser.getTimestampMillis(mMissingDate));
    }

    @Test
    public void testExtractNestedTimestampMillis() throws Exception {
        Mockito.when(mConfig.getMessageTimestampNameSeparator()).thenReturn(".");
        Mockito.when(mConfig.getMessageTimestampName()).thenReturn("meta_data.created");
        Iso8601MessageParser parser = new Iso8601MessageParser(mConfig);
        TestCase.assertEquals(1452513028647L, parser.getTimestampMillis(mNestedISOFormat));
    }
}

