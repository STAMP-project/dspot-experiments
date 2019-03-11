/**
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.hadoop.serialization.handler.write.impl;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.elasticsearch.hadoop.serialization.handler.write.SerializationFailure;
import org.elasticsearch.hadoop.util.DateUtils;
import org.elasticsearch.hadoop.util.StringUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class SerializationEventConverterTest {
    @Test
    public void generateEvent() throws Exception {
        Map<String, Object> document = new HashMap<String, Object>();
        document.put("field", "value");
        SerializationEventConverter eventConverter = new SerializationEventConverter();
        SerializationFailure iaeFailure = new SerializationFailure(new IllegalArgumentException("garbage"), document, new ArrayList<String>());
        String rawEvent = eventConverter.getRawEvent(iaeFailure);
        Assert.assertEquals("{field=value}", rawEvent);
        String timestamp = eventConverter.getTimestamp(iaeFailure);
        Assert.assertTrue(StringUtils.hasText(timestamp));
        Assert.assertTrue(((DateUtils.parseDate(timestamp).getTime().getTime()) > 1L));
        String exceptionType = eventConverter.renderExceptionType(iaeFailure);
        Assert.assertEquals("illegal_argument_exception", exceptionType);
        String exceptionMessage = eventConverter.renderExceptionMessage(iaeFailure);
        Assert.assertEquals("garbage", exceptionMessage);
        String eventMessage = eventConverter.renderEventMessage(iaeFailure);
        Assert.assertEquals("Could not construct bulk entry from record", eventMessage);
    }

    @Test
    public void generateEventWritable() throws Exception {
        MapWritable document = new MapWritable();
        document.put(new Text("field"), new Text("value"));
        SerializationEventConverter eventConverter = new SerializationEventConverter();
        SerializationFailure iaeFailure = new SerializationFailure(new IllegalArgumentException("garbage"), document, new ArrayList<String>());
        String rawEvent = eventConverter.getRawEvent(iaeFailure);
        Assert.assertThat(rawEvent, Matchers.startsWith("org.apache.hadoop.io.MapWritable@"));
        String timestamp = eventConverter.getTimestamp(iaeFailure);
        Assert.assertTrue(StringUtils.hasText(timestamp));
        Assert.assertTrue(((DateUtils.parseDate(timestamp).getTime().getTime()) > 1L));
        String exceptionType = eventConverter.renderExceptionType(iaeFailure);
        Assert.assertEquals("illegal_argument_exception", exceptionType);
        String exceptionMessage = eventConverter.renderExceptionMessage(iaeFailure);
        Assert.assertEquals("garbage", exceptionMessage);
        String eventMessage = eventConverter.renderEventMessage(iaeFailure);
        Assert.assertEquals("Could not construct bulk entry from record", eventMessage);
    }
}

