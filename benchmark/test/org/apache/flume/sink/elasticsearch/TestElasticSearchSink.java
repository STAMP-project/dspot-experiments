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


import ElasticSearchSinkConstants.INDEX_NAME_BUILDER;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang.time.FastDateFormat;
import org.apache.flume.Channel;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.Sink.Status;
import org.apache.flume.Transaction;
import org.apache.flume.conf.ComponentConfiguration;
import org.apache.flume.conf.Configurable;
import org.apache.flume.conf.Configurables;
import org.apache.flume.event.EventBuilder;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Requests;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Assert;
import org.junit.Test;


public class TestElasticSearchSink extends AbstractElasticSearchSinkTest {
    private ElasticSearchSink fixture;

    @Test
    public void shouldIndexOneEvent() throws Exception {
        Configurables.configure(fixture, new Context(parameters));
        Channel channel = bindAndStartChannel(fixture);
        Transaction tx = channel.getTransaction();
        tx.begin();
        Event event = EventBuilder.withBody("event #1 or 1".getBytes());
        channel.put(event);
        tx.commit();
        tx.close();
        fixture.process();
        fixture.stop();
        client.admin().indices().refresh(Requests.refreshRequest(timestampedIndexName)).actionGet();
        assertMatchAllQuery(1, event);
        assertBodyQuery(1, event);
    }

    @Test
    public void shouldIndexInvalidComplexJsonBody() throws Exception {
        parameters.put(ElasticSearchSinkConstants.BATCH_SIZE, "3");
        Configurables.configure(fixture, new Context(parameters));
        Channel channel = bindAndStartChannel(fixture);
        Transaction tx = channel.getTransaction();
        tx.begin();
        Event event1 = EventBuilder.withBody("TEST1 {test}".getBytes());
        channel.put(event1);
        Event event2 = EventBuilder.withBody("{test: TEST2 }".getBytes());
        channel.put(event2);
        Event event3 = EventBuilder.withBody("{\"test\":{ TEST3 {test} }}".getBytes());
        channel.put(event3);
        tx.commit();
        tx.close();
        fixture.process();
        fixture.stop();
        client.admin().indices().refresh(Requests.refreshRequest(timestampedIndexName)).actionGet();
        assertMatchAllQuery(3);
        assertSearch(1, performSearch(QueryBuilders.fieldQuery("@message", "TEST1")), null, event1);
        assertSearch(1, performSearch(QueryBuilders.fieldQuery("@message", "TEST2")), null, event2);
        assertSearch(1, performSearch(QueryBuilders.fieldQuery("@message", "TEST3")), null, event3);
    }

    @Test
    public void shouldIndexComplexJsonEvent() throws Exception {
        Configurables.configure(fixture, new Context(parameters));
        Channel channel = bindAndStartChannel(fixture);
        Transaction tx = channel.getTransaction();
        tx.begin();
        Event event = EventBuilder.withBody("{\"event\":\"json content\",\"num\":1}".getBytes());
        channel.put(event);
        tx.commit();
        tx.close();
        fixture.process();
        fixture.stop();
        client.admin().indices().refresh(Requests.refreshRequest(timestampedIndexName)).actionGet();
        Map<String, Object> expectedBody = new HashMap<String, Object>();
        expectedBody.put("event", "json content");
        expectedBody.put("num", 1);
        assertSearch(1, performSearch(QueryBuilders.matchAllQuery()), expectedBody, event);
        assertSearch(1, performSearch(QueryBuilders.fieldQuery("@message.event", "json")), expectedBody, event);
    }

    @Test
    public void shouldIndexFiveEvents() throws Exception {
        // Make it so we only need to call process once
        parameters.put(ElasticSearchSinkConstants.BATCH_SIZE, "5");
        Configurables.configure(fixture, new Context(parameters));
        Channel channel = bindAndStartChannel(fixture);
        int numberOfEvents = 5;
        Event[] events = new Event[numberOfEvents];
        Transaction tx = channel.getTransaction();
        tx.begin();
        for (int i = 0; i < numberOfEvents; i++) {
            String body = (("event #" + i) + " of ") + numberOfEvents;
            Event event = EventBuilder.withBody(body.getBytes());
            events[i] = event;
            channel.put(event);
        }
        tx.commit();
        tx.close();
        fixture.process();
        fixture.stop();
        client.admin().indices().refresh(Requests.refreshRequest(timestampedIndexName)).actionGet();
        assertMatchAllQuery(numberOfEvents, events);
        assertBodyQuery(5, events);
    }

    @Test
    public void shouldIndexFiveEventsOverThreeBatches() throws Exception {
        parameters.put(ElasticSearchSinkConstants.BATCH_SIZE, "2");
        Configurables.configure(fixture, new Context(parameters));
        Channel channel = bindAndStartChannel(fixture);
        int numberOfEvents = 5;
        Event[] events = new Event[numberOfEvents];
        Transaction tx = channel.getTransaction();
        tx.begin();
        for (int i = 0; i < numberOfEvents; i++) {
            String body = (("event #" + i) + " of ") + numberOfEvents;
            Event event = EventBuilder.withBody(body.getBytes());
            events[i] = event;
            channel.put(event);
        }
        tx.commit();
        tx.close();
        int count = 0;
        Status status = Status.READY;
        while (status != (Status.BACKOFF)) {
            count++;
            status = fixture.process();
        } 
        fixture.stop();
        Assert.assertEquals(3, count);
        client.admin().indices().refresh(Requests.refreshRequest(timestampedIndexName)).actionGet();
        assertMatchAllQuery(numberOfEvents, events);
        assertBodyQuery(5, events);
    }

    @Test
    public void shouldParseConfiguration() {
        parameters.put(ElasticSearchSinkConstants.HOSTNAMES, "10.5.5.27");
        parameters.put(ElasticSearchSinkConstants.CLUSTER_NAME, "testing-cluster-name");
        parameters.put(ElasticSearchSinkConstants.INDEX_NAME, "testing-index-name");
        parameters.put(ElasticSearchSinkConstants.INDEX_TYPE, "testing-index-type");
        parameters.put(ElasticSearchSinkConstants.TTL, "10");
        fixture = new ElasticSearchSink();
        fixture.configure(new Context(parameters));
        String[] expected = new String[]{ "10.5.5.27" };
        Assert.assertEquals("testing-cluster-name", fixture.getClusterName());
        Assert.assertEquals("testing-index-name", fixture.getIndexName());
        Assert.assertEquals("testing-index-type", fixture.getIndexType());
        Assert.assertEquals(TimeUnit.DAYS.toMillis(10), fixture.getTTLMs());
        Assert.assertArrayEquals(expected, fixture.getServerAddresses());
    }

    @Test
    public void shouldParseConfigurationUsingDefaults() {
        parameters.put(ElasticSearchSinkConstants.HOSTNAMES, "10.5.5.27");
        parameters.remove(ElasticSearchSinkConstants.INDEX_NAME);
        parameters.remove(ElasticSearchSinkConstants.INDEX_TYPE);
        parameters.remove(ElasticSearchSinkConstants.CLUSTER_NAME);
        fixture = new ElasticSearchSink();
        fixture.configure(new Context(parameters));
        String[] expected = new String[]{ "10.5.5.27" };
        Assert.assertEquals(AbstractElasticSearchSinkTest.DEFAULT_INDEX_NAME, fixture.getIndexName());
        Assert.assertEquals(AbstractElasticSearchSinkTest.DEFAULT_INDEX_TYPE, fixture.getIndexType());
        Assert.assertEquals(AbstractElasticSearchSinkTest.DEFAULT_CLUSTER_NAME, fixture.getClusterName());
        Assert.assertArrayEquals(expected, fixture.getServerAddresses());
    }

    @Test
    public void shouldParseMultipleHostUsingDefaultPorts() {
        parameters.put(ElasticSearchSinkConstants.HOSTNAMES, "10.5.5.27,10.5.5.28,10.5.5.29");
        fixture = new ElasticSearchSink();
        fixture.configure(new Context(parameters));
        String[] expected = new String[]{ "10.5.5.27", "10.5.5.28", "10.5.5.29" };
        Assert.assertArrayEquals(expected, fixture.getServerAddresses());
    }

    @Test
    public void shouldParseMultipleHostWithWhitespacesUsingDefaultPorts() {
        parameters.put(ElasticSearchSinkConstants.HOSTNAMES, " 10.5.5.27 , 10.5.5.28 , 10.5.5.29 ");
        fixture = new ElasticSearchSink();
        fixture.configure(new Context(parameters));
        String[] expected = new String[]{ "10.5.5.27", "10.5.5.28", "10.5.5.29" };
        Assert.assertArrayEquals(expected, fixture.getServerAddresses());
    }

    @Test
    public void shouldParseMultipleHostAndPorts() {
        parameters.put(ElasticSearchSinkConstants.HOSTNAMES, "10.5.5.27:9300,10.5.5.28:9301,10.5.5.29:9302");
        fixture = new ElasticSearchSink();
        fixture.configure(new Context(parameters));
        String[] expected = new String[]{ "10.5.5.27:9300", "10.5.5.28:9301", "10.5.5.29:9302" };
        Assert.assertArrayEquals(expected, fixture.getServerAddresses());
    }

    @Test
    public void shouldParseMultipleHostAndPortsWithWhitespaces() {
        parameters.put(ElasticSearchSinkConstants.HOSTNAMES, " 10.5.5.27 : 9300 , 10.5.5.28 : 9301 , 10.5.5.29 : 9302 ");
        fixture = new ElasticSearchSink();
        fixture.configure(new Context(parameters));
        String[] expected = new String[]{ "10.5.5.27:9300", "10.5.5.28:9301", "10.5.5.29:9302" };
        Assert.assertArrayEquals(expected, fixture.getServerAddresses());
    }

    @Test
    public void shouldAllowCustomElasticSearchIndexRequestBuilderFactory() throws Exception {
        parameters.put(ElasticSearchSinkConstants.SERIALIZER, TestElasticSearchSink.CustomElasticSearchIndexRequestBuilderFactory.class.getName());
        fixture.configure(new Context(parameters));
        Channel channel = bindAndStartChannel(fixture);
        Transaction tx = channel.getTransaction();
        tx.begin();
        String body = "{ foo: \"bar\" }";
        Event event = EventBuilder.withBody(body.getBytes());
        channel.put(event);
        tx.commit();
        tx.close();
        fixture.process();
        fixture.stop();
        Assert.assertEquals(((fixture.getIndexName()) + "-05_17_36_789"), TestElasticSearchSink.CustomElasticSearchIndexRequestBuilderFactory.actualIndexName);
        Assert.assertEquals(fixture.getIndexType(), TestElasticSearchSink.CustomElasticSearchIndexRequestBuilderFactory.actualIndexType);
        Assert.assertArrayEquals(event.getBody(), TestElasticSearchSink.CustomElasticSearchIndexRequestBuilderFactory.actualEventBody);
        Assert.assertTrue(TestElasticSearchSink.CustomElasticSearchIndexRequestBuilderFactory.hasContext);
    }

    @Test
    public void shouldParseFullyQualifiedTTLs() {
        Map<String, Long> testTTLMap = new HashMap<String, Long>();
        testTTLMap.put("1ms", Long.valueOf(1));
        testTTLMap.put("1s", Long.valueOf(1000));
        testTTLMap.put("1m", Long.valueOf(60000));
        testTTLMap.put("1h", Long.valueOf(3600000));
        testTTLMap.put("1d", Long.valueOf(86400000));
        testTTLMap.put("1w", Long.valueOf(604800000));
        testTTLMap.put("1", Long.valueOf(86400000));
        parameters.put(ElasticSearchSinkConstants.HOSTNAMES, "10.5.5.27");
        parameters.put(ElasticSearchSinkConstants.CLUSTER_NAME, "testing-cluster-name");
        parameters.put(ElasticSearchSinkConstants.INDEX_NAME, "testing-index-name");
        parameters.put(ElasticSearchSinkConstants.INDEX_TYPE, "testing-index-type");
        for (String ttl : testTTLMap.keySet()) {
            parameters.put(ElasticSearchSinkConstants.TTL, ttl);
            fixture = new ElasticSearchSink();
            fixture.configure(new Context(parameters));
            String[] expected = new String[]{ "10.5.5.27" };
            Assert.assertEquals("testing-cluster-name", fixture.getClusterName());
            Assert.assertEquals("testing-index-name", fixture.getIndexName());
            Assert.assertEquals("testing-index-type", fixture.getIndexType());
            Assert.assertEquals(((long) (testTTLMap.get(ttl))), fixture.getTTLMs());
            Assert.assertArrayEquals(expected, fixture.getServerAddresses());
        }
    }

    public static final class CustomElasticSearchIndexRequestBuilderFactory extends AbstractElasticSearchIndexRequestBuilderFactory {
        static String actualIndexName;

        static String actualIndexType;

        static byte[] actualEventBody;

        static boolean hasContext;

        public CustomElasticSearchIndexRequestBuilderFactory() {
            super(FastDateFormat.getInstance("HH_mm_ss_SSS", TimeZone.getTimeZone("EST5EDT")));
        }

        @Override
        protected void prepareIndexRequest(IndexRequestBuilder indexRequest, String indexName, String indexType, Event event) throws IOException {
            TestElasticSearchSink.CustomElasticSearchIndexRequestBuilderFactory.actualIndexName = indexName;
            TestElasticSearchSink.CustomElasticSearchIndexRequestBuilderFactory.actualIndexType = indexType;
            TestElasticSearchSink.CustomElasticSearchIndexRequestBuilderFactory.actualEventBody = event.getBody();
            indexRequest.setIndex(indexName).setType(indexType).setSource(event.getBody());
        }

        @Override
        public void configure(Context arg0) {
            TestElasticSearchSink.CustomElasticSearchIndexRequestBuilderFactory.hasContext = true;
        }

        @Override
        public void configure(ComponentConfiguration arg0) {
            // no-op
        }
    }

    @Test
    public void shouldFailToConfigureWithInvalidSerializerClass() throws Exception {
        parameters.put(ElasticSearchSinkConstants.SERIALIZER, "java.lang.String");
        try {
            Configurables.configure(fixture, new Context(parameters));
        } catch (ClassCastException e) {
            // expected
        }
        parameters.put(ElasticSearchSinkConstants.SERIALIZER, TestElasticSearchSink.FakeConfigurable.class.getName());
        try {
            Configurables.configure(fixture, new Context(parameters));
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void shouldUseSpecifiedSerializer() throws Exception {
        Context context = new Context();
        context.put(ElasticSearchSinkConstants.SERIALIZER, "org.apache.flume.sink.elasticsearch.FakeEventSerializer");
        Assert.assertNull(fixture.getEventSerializer());
        fixture.configure(context);
        Assert.assertTrue(((fixture.getEventSerializer()) instanceof FakeEventSerializer));
    }

    @Test
    public void shouldUseSpecifiedIndexNameBuilder() throws Exception {
        Context context = new Context();
        context.put(INDEX_NAME_BUILDER, "org.apache.flume.sink.elasticsearch.FakeIndexNameBuilder");
        Assert.assertNull(fixture.getIndexNameBuilder());
        fixture.configure(context);
        Assert.assertTrue(((fixture.getIndexNameBuilder()) instanceof FakeIndexNameBuilder));
    }

    public static class FakeConfigurable implements Configurable {
        @Override
        public void configure(Context arg0) {
            // no-op
        }
    }
}

