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


import java.util.HashMap;
import java.util.Map;
import org.apache.flume.Event;
import org.apache.flume.event.SimpleEvent;
import org.junit.Assert;
import org.junit.Test;


public class TimeBasedIndexNameBuilderTest {
    private TimeBasedIndexNameBuilder indexNameBuilder;

    @Test
    public void shouldUseUtcAsBasisForDateFormat() {
        Assert.assertEquals("Coordinated Universal Time", indexNameBuilder.getFastDateFormat().getTimeZone().getDisplayName());
    }

    @Test
    public void indexNameShouldBePrefixDashFormattedTimestamp() {
        long time = 987654321L;
        Event event = new SimpleEvent();
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("timestamp", Long.toString(time));
        event.setHeaders(headers);
        Assert.assertEquals(("prefix-" + (indexNameBuilder.getFastDateFormat().format(time))), indexNameBuilder.getIndexName(event));
    }
}

