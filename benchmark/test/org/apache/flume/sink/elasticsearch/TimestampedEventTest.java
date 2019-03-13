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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.flume.sink.elasticsearch;


import com.google.common.collect.Maps;
import java.util.Map;
import org.apache.flume.event.SimpleEvent;
import org.junit.Assert;
import org.junit.Test;


public class TimestampedEventTest {
    static final long FIXED_TIME_MILLIS = 123456789L;

    @Test
    public void shouldEnsureTimestampHeaderPresentInTimestampedEvent() {
        SimpleEvent base = new SimpleEvent();
        TimestampedEvent timestampedEvent = new TimestampedEvent(base);
        Assert.assertEquals(TimestampedEventTest.FIXED_TIME_MILLIS, timestampedEvent.getTimestamp());
        Assert.assertEquals(String.valueOf(TimestampedEventTest.FIXED_TIME_MILLIS), timestampedEvent.getHeaders().get("timestamp"));
    }

    @Test
    public void shouldUseExistingTimestampHeaderInTimestampedEvent() {
        SimpleEvent base = new SimpleEvent();
        Map<String, String> headersWithTimestamp = Maps.newHashMap();
        headersWithTimestamp.put("timestamp", "-321");
        base.setHeaders(headersWithTimestamp);
        TimestampedEvent timestampedEvent = new TimestampedEvent(base);
        Assert.assertEquals((-321L), timestampedEvent.getTimestamp());
        Assert.assertEquals("-321", timestampedEvent.getHeaders().get("timestamp"));
    }

    @Test
    public void shouldUseExistingAtTimestampHeaderInTimestampedEvent() {
        SimpleEvent base = new SimpleEvent();
        Map<String, String> headersWithTimestamp = Maps.newHashMap();
        headersWithTimestamp.put("@timestamp", "-999");
        base.setHeaders(headersWithTimestamp);
        TimestampedEvent timestampedEvent = new TimestampedEvent(base);
        Assert.assertEquals((-999L), timestampedEvent.getTimestamp());
        Assert.assertEquals("-999", timestampedEvent.getHeaders().get("@timestamp"));
        Assert.assertNull(timestampedEvent.getHeaders().get("timestamp"));
    }

    @Test
    public void shouldPreserveBodyAndNonTimestampHeadersInTimestampedEvent() {
        SimpleEvent base = new SimpleEvent();
        base.setBody(new byte[]{ 1, 2, 3, 4 });
        Map<String, String> headersWithTimestamp = Maps.newHashMap();
        headersWithTimestamp.put("foo", "bar");
        base.setHeaders(headersWithTimestamp);
        TimestampedEvent timestampedEvent = new TimestampedEvent(base);
        Assert.assertEquals("bar", timestampedEvent.getHeaders().get("foo"));
        Assert.assertArrayEquals(base.getBody(), timestampedEvent.getBody());
    }
}

