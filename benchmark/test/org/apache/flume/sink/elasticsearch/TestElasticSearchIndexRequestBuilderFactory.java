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


import ElasticSearchIndexRequestBuilderFactory.df;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.Map;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.conf.ComponentConfiguration;
import org.apache.flume.conf.sink.SinkConfiguration;
import org.apache.flume.event.SimpleEvent;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.io.BytesStream;
import org.elasticsearch.common.io.FastByteArrayOutputStream;
import org.junit.Assert;
import org.junit.Test;


public class TestElasticSearchIndexRequestBuilderFactory extends AbstractElasticSearchSinkTest {
    private static final Client FAKE_CLIENT = null;

    private EventSerializerIndexRequestBuilderFactory factory;

    private TestElasticSearchIndexRequestBuilderFactory.FakeEventSerializer serializer;

    @Test
    public void shouldUseUtcAsBasisForDateFormat() {
        Assert.assertEquals("Coordinated Universal Time", factory.fastDateFormat.getTimeZone().getDisplayName());
    }

    @Test
    public void indexNameShouldBePrefixDashFormattedTimestamp() {
        long millis = 987654321L;
        Assert.assertEquals(("prefix-" + (factory.fastDateFormat.format(millis))), factory.getIndexName("prefix", millis));
    }

    @Test
    public void shouldEnsureTimestampHeaderPresentInTimestampedEvent() {
        SimpleEvent base = new SimpleEvent();
        TimestampedEvent timestampedEvent = new TimestampedEvent(base);
        Assert.assertEquals(AbstractElasticSearchSinkTest.FIXED_TIME_MILLIS, timestampedEvent.getTimestamp());
        Assert.assertEquals(String.valueOf(AbstractElasticSearchSinkTest.FIXED_TIME_MILLIS), timestampedEvent.getHeaders().get("timestamp"));
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

    @Test
    public void shouldSetIndexNameTypeAndSerializedEventIntoIndexRequest() throws Exception {
        String indexPrefix = "qwerty";
        String indexType = "uiop";
        Event event = new SimpleEvent();
        IndexRequestBuilder indexRequestBuilder = factory.createIndexRequest(TestElasticSearchIndexRequestBuilderFactory.FAKE_CLIENT, indexPrefix, indexType, event);
        Assert.assertEquals(((indexPrefix + '-') + (df.format(AbstractElasticSearchSinkTest.FIXED_TIME_MILLIS))), indexRequestBuilder.request().index());
        Assert.assertEquals(indexType, indexRequestBuilder.request().type());
        Assert.assertArrayEquals(TestElasticSearchIndexRequestBuilderFactory.FakeEventSerializer.FAKE_BYTES, indexRequestBuilder.request().source().array());
    }

    @Test
    public void shouldSetIndexNameFromTimestampHeaderWhenPresent() throws Exception {
        String indexPrefix = "qwerty";
        String indexType = "uiop";
        Event event = new SimpleEvent();
        event.getHeaders().put("timestamp", "1213141516");
        IndexRequestBuilder indexRequestBuilder = factory.createIndexRequest(null, indexPrefix, indexType, event);
        Assert.assertEquals(((indexPrefix + '-') + (df.format(1213141516L))), indexRequestBuilder.request().index());
    }

    @Test
    public void shouldSetIndexNameTypeFromHeaderWhenPresent() throws Exception {
        String indexPrefix = "%{index-name}";
        String indexType = "%{index-type}";
        String indexValue = "testing-index-name-from-headers";
        String typeValue = "testing-index-type-from-headers";
        Event event = new SimpleEvent();
        event.getHeaders().put("index-name", indexValue);
        event.getHeaders().put("index-type", typeValue);
        IndexRequestBuilder indexRequestBuilder = factory.createIndexRequest(null, indexPrefix, indexType, event);
        Assert.assertEquals(((indexValue + '-') + (df.format(AbstractElasticSearchSinkTest.FIXED_TIME_MILLIS))), indexRequestBuilder.request().index());
        Assert.assertEquals(typeValue, indexRequestBuilder.request().type());
    }

    @Test
    public void shouldConfigureEventSerializer() throws Exception {
        Assert.assertFalse(serializer.configuredWithContext);
        factory.configure(new Context());
        Assert.assertTrue(serializer.configuredWithContext);
        Assert.assertFalse(serializer.configuredWithComponentConfiguration);
        factory.configure(new SinkConfiguration("name"));
        Assert.assertTrue(serializer.configuredWithComponentConfiguration);
    }

    static class FakeEventSerializer implements ElasticSearchEventSerializer {
        static final byte[] FAKE_BYTES = new byte[]{ 9, 8, 7, 6 };

        boolean configuredWithContext;

        boolean configuredWithComponentConfiguration;

        @Override
        public BytesStream getContentBuilder(Event event) throws IOException {
            FastByteArrayOutputStream fbaos = new FastByteArrayOutputStream(4);
            fbaos.write(TestElasticSearchIndexRequestBuilderFactory.FakeEventSerializer.FAKE_BYTES);
            return fbaos;
        }

        @Override
        public void configure(Context arg0) {
            configuredWithContext = true;
        }

        @Override
        public void configure(ComponentConfiguration arg0) {
            configuredWithComponentConfiguration = true;
        }
    }
}

