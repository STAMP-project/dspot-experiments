/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.util;


import ExecConstants.WEB_DISPLAY_FORMAT_DATE;
import ExecConstants.WEB_DISPLAY_FORMAT_TIME;
import ExecConstants.WEB_DISPLAY_FORMAT_TIMESTAMP;
import TypeProtos.MinorType.DATE;
import TypeProtos.MinorType.TIME;
import TypeProtos.MinorType.TIMESTAMP;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.apache.drill.exec.server.options.OptionManager;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class TestValueVectorElementFormatter {
    @Mock
    private OptionManager options;

    @Test
    public void testFormatValueVectorElementTimestampEmptyPattern() {
        Mockito.when(options.getString(WEB_DISPLAY_FORMAT_TIMESTAMP)).thenReturn("");
        ValueVectorElementFormatter formatter = new ValueVectorElementFormatter(options);
        String formattedValue = formatter.format(LocalDateTime.of(2012, 11, 5, 13, 0, 30, 120000000), TIMESTAMP);
        Assert.assertEquals("2012-11-05T13:00:30.120", formattedValue);
    }

    @Test
    public void testFormatValueVectorElementTimestampValidPattern() {
        Mockito.when(options.getString(WEB_DISPLAY_FORMAT_TIMESTAMP)).thenReturn("yyyy-MM-dd HH:mm:ss.SS");
        ValueVectorElementFormatter formatter = new ValueVectorElementFormatter(options);
        String formattedValue = formatter.format(LocalDateTime.of(2012, 11, 5, 13, 0, 30, 120000000), TIMESTAMP);
        Assert.assertEquals("2012-11-05 13:00:30.12", formattedValue);
    }

    @Test
    public void testFormatValueVectorElementDateEmptyPattern() {
        Mockito.when(options.getString(WEB_DISPLAY_FORMAT_DATE)).thenReturn("");
        ValueVectorElementFormatter formatter = new ValueVectorElementFormatter(options);
        String formattedValue = formatter.format(LocalDate.of(2012, 11, 5), DATE);
        Assert.assertEquals("2012-11-05", formattedValue);
    }

    @Test
    public void testFormatValueVectorElementDateValidPattern() {
        Mockito.when(options.getString(WEB_DISPLAY_FORMAT_DATE)).thenReturn("EEE, MMM d, yyyy");
        ValueVectorElementFormatter formatter = new ValueVectorElementFormatter(options);
        String formattedValue = formatter.format(LocalDate.of(2012, 11, 5), DATE);
        Assert.assertEquals("Mon, Nov 5, 2012", formattedValue);
    }

    @Test
    public void testFormatValueVectorElementDateUnsupportedPattern() {
        Mockito.when(options.getString(WEB_DISPLAY_FORMAT_DATE)).thenReturn("yyyy-MM-dd HH:mm:ss.SS");
        ValueVectorElementFormatter formatter = new ValueVectorElementFormatter(options);
        String formattedValue = formatter.format(LocalDate.of(2012, 11, 5), DATE);
        Assert.assertNull(formattedValue);
    }

    @Test
    public void testFormatValueVectorElementTimeEmptyPattern() {
        Mockito.when(options.getString(WEB_DISPLAY_FORMAT_TIME)).thenReturn("");
        ValueVectorElementFormatter formatter = new ValueVectorElementFormatter(options);
        String formattedValue = formatter.format(LocalTime.of(13, 0, 30, 120000000), TIME);
        Assert.assertEquals("13:00:30.120", formattedValue);
    }

    @Test
    public void testFormatValueVectorElementTimeValidPattern() {
        Mockito.when(options.getString(WEB_DISPLAY_FORMAT_TIME)).thenReturn("h:mm:ss a");
        ValueVectorElementFormatter formatter = new ValueVectorElementFormatter(options);
        String formattedValue = formatter.format(LocalTime.of(13, 0, 30), TIME);
        Assert.assertEquals("1:00:30 PM", formattedValue);
    }

    @Test
    public void testFormatValueVectorElementTimeUnsupportedPattern() {
        Mockito.when(options.getString(WEB_DISPLAY_FORMAT_TIME)).thenReturn("yyyy-MM-dd HH:mm:ss.SS");
        ValueVectorElementFormatter formatter = new ValueVectorElementFormatter(options);
        String formattedValue = formatter.format(LocalTime.of(13, 0, 30), TIME);
        Assert.assertNull(formattedValue);
    }

    @Test
    public void testFormatValueVectorElementAllDateTimeFormats() {
        Mockito.when(options.getString(WEB_DISPLAY_FORMAT_TIMESTAMP)).thenReturn("yyyy-MM-dd HH:mm:ss.SS");
        Mockito.when(options.getString(WEB_DISPLAY_FORMAT_DATE)).thenReturn("EEE, MMM d, yyyy");
        Mockito.when(options.getString(WEB_DISPLAY_FORMAT_TIME)).thenReturn("h:mm:ss a");
        ValueVectorElementFormatter formatter = new ValueVectorElementFormatter(options);
        String formattedTimestamp = formatter.format(LocalDateTime.of(2012, 11, 5, 13, 0, 30, 120000000), TIMESTAMP);
        String formattedDate = formatter.format(LocalDate.of(2012, 11, 5), DATE);
        String formattedTime = formatter.format(LocalTime.of(13, 0, 30), TIME);
        Assert.assertEquals("2012-11-05 13:00:30.12", formattedTimestamp);
        Assert.assertEquals("Mon, Nov 5, 2012", formattedDate);
        Assert.assertEquals("1:00:30 PM", formattedTime);
    }
}

