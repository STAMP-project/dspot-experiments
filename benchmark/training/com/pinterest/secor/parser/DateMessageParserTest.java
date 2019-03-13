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


import DateMessageParser.defaultDate;
import com.pinterest.secor.common.SecorConfig;
import com.pinterest.secor.message.Message;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
public class DateMessageParserTest extends TestCase {
    private SecorConfig mConfig;

    private Message mFormat1;

    private Message mFormat2;

    private Message mFormat3;

    private Message mInvalidDate;

    private Message mISOFormat;

    private Message mNanosecondISOFormat;

    private Message mNestedISOFormat;

    private long timestamp;

    @Test
    public void testExtractDateUsingInputPattern() throws Exception {
        Mockito.when(mConfig.getMessageTimestampName()).thenReturn("timestamp");
        Mockito.when(mConfig.getString("partitioner.granularity.date.prefix", "dt=")).thenReturn("dt=");
        Mockito.when(mConfig.getString("partitioner.granularity.date.format", "yyyy-MM-dd")).thenReturn("yyyy-MM-dd");
        Mockito.when(mConfig.getMessageTimestampInputPattern()).thenReturn("yyyy-MM-dd HH:mm:ss");
        TestCase.assertEquals("dt=2014-07-30", new DateMessageParser(mConfig).extractPartitions(mFormat1)[0]);
        Mockito.when(mConfig.getMessageTimestampInputPattern()).thenReturn("yyyy/MM/d");
        TestCase.assertEquals("dt=2014-10-25", new DateMessageParser(mConfig).extractPartitions(mFormat2)[0]);
        Mockito.when(mConfig.getMessageTimestampInputPattern()).thenReturn("yyyyy.MMMMM.dd GGG hh:mm aaa");
        TestCase.assertEquals("dt=2001-07-04", new DateMessageParser(mConfig).extractPartitions(mFormat3)[0]);
        Mockito.when(mConfig.getMessageTimestampInputPattern()).thenReturn("yyyy-MM-dd'T'HH:mm:ss'Z'");
        TestCase.assertEquals("dt=2006-01-02", new DateMessageParser(mConfig).extractPartitions(mISOFormat)[0]);
        Mockito.when(mConfig.getMessageTimestampInputPattern()).thenReturn("yyyy-MM-dd'T'HH:mm:ss");
        TestCase.assertEquals("dt=2006-01-02", new DateMessageParser(mConfig).extractPartitions(mISOFormat)[0]);
        Mockito.when(mConfig.getMessageTimestampInputPattern()).thenReturn("yyyy-MM-dd'T'HH:mm:ss");
        TestCase.assertEquals("dt=2006-01-02", new DateMessageParser(mConfig).extractPartitions(mNanosecondISOFormat)[0]);
    }

    @Test
    public void testExtractDateWithWrongEntries() throws Exception {
        Mockito.when(mConfig.getMessageTimestampName()).thenReturn("timestamp");
        Mockito.when(mConfig.getString("partitioner.granularity.date.format", "yyyy-MM-dd")).thenReturn("yyyy-MM-dd");
        // invalid date
        Mockito.when(mConfig.getMessageTimestampInputPattern()).thenReturn("yyyy-MM-dd HH:mm:ss");// any pattern

        TestCase.assertEquals(defaultDate, new DateMessageParser(mConfig).extractPartitions(mInvalidDate)[0]);
        // invalid pattern
        Mockito.when(mConfig.getMessageTimestampInputPattern()).thenReturn("yyy-MM-dd :s");
        TestCase.assertEquals(defaultDate, new DateMessageParser(mConfig).extractPartitions(mFormat1)[0]);
    }

    @Test
    public void testDatePrefix() throws Exception {
        Mockito.when(mConfig.getMessageTimestampName()).thenReturn("timestamp");
        Mockito.when(mConfig.getMessageTimestampInputPattern()).thenReturn("yyyy-MM-dd HH:mm:ss");
        Mockito.when(mConfig.getString("partitioner.granularity.date.prefix", "dt=")).thenReturn("foo");
        Mockito.when(mConfig.getString("partitioner.granularity.date.format", "yyyy-MM-dd")).thenReturn("yyyy-MM-dd");
        TestCase.assertEquals("foo2014-07-30", new DateMessageParser(mConfig).extractPartitions(mFormat1)[0]);
    }

    @Test
    public void testNestedField() throws Exception {
        Mockito.when(mConfig.getMessageTimestampNameSeparator()).thenReturn(".");
        Mockito.when(mConfig.getMessageTimestampName()).thenReturn("meta_data.created");
        Mockito.when(mConfig.getMessageTimestampInputPattern()).thenReturn("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
        Mockito.when(mConfig.getString("partitioner.granularity.date.prefix", "dt=")).thenReturn("dt=");
        Mockito.when(mConfig.getString("partitioner.granularity.date.format", "yyyy-MM-dd")).thenReturn("yyyy-MM-dd");
        TestCase.assertEquals("dt=2016-01-11", new DateMessageParser(mConfig).extractPartitions(mNestedISOFormat)[0]);
    }

    @Test
    public void testCustomDateFormat() throws Exception {
        Mockito.when(mConfig.getMessageTimestampName()).thenReturn("timestamp");
        Mockito.when(mConfig.getMessageTimestampInputPattern()).thenReturn("yyyy-MM-dd HH:mm:ss");
        Mockito.when(mConfig.getString("partitioner.granularity.date.prefix", "dt=")).thenReturn("");
        Mockito.when(mConfig.getString("partitioner.granularity.date.format", "yyyy-MM-dd")).thenReturn("'yr='yyyy'/mo='MM'/dy='dd'/hr='HH");
        TestCase.assertEquals("yr=2014/mo=07/dy=30/hr=10", new DateMessageParser(mConfig).extractPartitions(mFormat1)[0]);
    }
}

