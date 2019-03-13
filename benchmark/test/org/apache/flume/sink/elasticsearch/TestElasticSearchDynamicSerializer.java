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


import java.util.Map;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.event.EventBuilder;
import org.elasticsearch.common.collect.Maps;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.junit.Assert;
import org.junit.Test;


public class TestElasticSearchDynamicSerializer {
    @Test
    public void testRoundTrip() throws Exception {
        ElasticSearchDynamicSerializer fixture = new ElasticSearchDynamicSerializer();
        Context context = new Context();
        fixture.configure(context);
        String message = "test body";
        Map<String, String> headers = Maps.newHashMap();
        headers.put("headerNameOne", "headerValueOne");
        headers.put("headerNameTwo", "headerValueTwo");
        headers.put("headerNameThree", "headerValueThree");
        Event event = EventBuilder.withBody(message.getBytes(ElasticSearchEventSerializer.charset));
        event.setHeaders(headers);
        XContentBuilder expected = jsonBuilder().startObject();
        expected.field("body", new String(message.getBytes(), ElasticSearchEventSerializer.charset));
        for (String headerName : headers.keySet()) {
            expected.field(headerName, new String(headers.get(headerName).getBytes(), ElasticSearchEventSerializer.charset));
        }
        expected.endObject();
        XContentBuilder actual = fixture.getContentBuilder(event);
        Assert.assertEquals(new String(expected.bytes().array()), new String(actual.bytes().array()));
    }
}

