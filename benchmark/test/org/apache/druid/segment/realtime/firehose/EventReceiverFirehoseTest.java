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
package org.apache.druid.segment.realtime.firehose;


import AllowAllAuthenticator.ALLOW_ALL_RESULT;
import AuthConfig.DRUID_ALLOW_UNSECURED_PATH;
import AuthConfig.DRUID_AUTHENTICATION_RESULT;
import AuthConfig.DRUID_AUTHORIZATION_CHECKED;
import EventReceiverFirehoseFactory.EventReceiverFirehose;
import Response.Status.BAD_REQUEST;
import Response.Status.FORBIDDEN;
import Response.Status.OK;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import org.apache.commons.io.IOUtils;
import org.apache.druid.data.input.impl.DimensionsSpec;
import org.apache.druid.data.input.impl.MapInputRowParser;
import org.apache.druid.data.input.impl.TimestampSpec;
import org.apache.druid.jackson.DefaultObjectMapper;
import org.apache.druid.java.util.common.DateTimes;
import org.apache.druid.java.util.common.ISE;
import org.apache.druid.java.util.common.concurrent.Execs;
import org.apache.druid.server.metrics.EventReceiverFirehoseMetric;
import org.apache.druid.server.metrics.EventReceiverFirehoseRegister;
import org.apache.druid.server.security.AuthTestUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import static EventReceiverFirehoseFactory.MAX_FIREHOSE_PRODUCERS;


public class EventReceiverFirehoseTest {
    private static final int CAPACITY = 300;

    private static final int NUM_EVENTS = 100;

    private static final long MAX_IDLE_TIME_MILLIS = TimeUnit.SECONDS.toMillis(20);

    private static final String SERVICE_NAME = "test_firehose";

    private final String inputRow = "[{\n" + (("  \"timestamp\":123,\n" + "  \"d1\":\"v1\"\n") + "}]");

    private EventReceiverFirehoseFactory eventReceiverFirehoseFactory;

    private EventReceiverFirehose firehose;

    private EventReceiverFirehoseRegister register = new EventReceiverFirehoseRegister();

    private HttpServletRequest req;

    @Test(timeout = 60000L)
    public void testSingleThread() throws IOException, InterruptedException {
        for (int i = 0; i < (EventReceiverFirehoseTest.NUM_EVENTS); ++i) {
            setUpRequestExpectations(null, null);
            final InputStream inputStream = IOUtils.toInputStream(inputRow, StandardCharsets.UTF_8);
            firehose.addAll(inputStream, req);
            Assert.assertEquals((i + 1), firehose.getCurrentBufferSize());
            inputStream.close();
        }
        EasyMock.verify(req);
        final Iterable<Map.Entry<String, EventReceiverFirehoseMetric>> metrics = register.getMetrics();
        Assert.assertEquals(1, Iterables.size(metrics));
        final Map.Entry<String, EventReceiverFirehoseMetric> entry = Iterables.getLast(metrics);
        Assert.assertEquals(EventReceiverFirehoseTest.SERVICE_NAME, entry.getKey());
        Assert.assertEquals(EventReceiverFirehoseTest.CAPACITY, entry.getValue().getCapacity());
        Assert.assertEquals(EventReceiverFirehoseTest.CAPACITY, firehose.getCapacity());
        Assert.assertEquals(EventReceiverFirehoseTest.NUM_EVENTS, entry.getValue().getCurrentBufferSize());
        Assert.assertEquals(EventReceiverFirehoseTest.NUM_EVENTS, firehose.getCurrentBufferSize());
        for (int i = (EventReceiverFirehoseTest.NUM_EVENTS) - 1; i >= 0; --i) {
            Assert.assertTrue(firehose.hasMore());
            Assert.assertNotNull(firehose.nextRow());
            Assert.assertEquals(i, firehose.getCurrentBufferSize());
        }
        Assert.assertEquals(EventReceiverFirehoseTest.CAPACITY, entry.getValue().getCapacity());
        Assert.assertEquals(EventReceiverFirehoseTest.CAPACITY, firehose.getCapacity());
        Assert.assertEquals(0, entry.getValue().getCurrentBufferSize());
        Assert.assertEquals(0, firehose.getCurrentBufferSize());
        firehose.close();
        Assert.assertFalse(firehose.hasMore());
        Assert.assertEquals(0, Iterables.size(register.getMetrics()));
        awaitDelayedExecutorThreadTerminated();
    }

    @Test(timeout = 60000L)
    public void testMultipleThreads() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        EasyMock.expect(req.getAttribute(DRUID_AUTHORIZATION_CHECKED)).andReturn(null).anyTimes();
        EasyMock.expect(req.getAttribute(DRUID_ALLOW_UNSECURED_PATH)).andReturn(null).anyTimes();
        EasyMock.expect(req.getAttribute(DRUID_AUTHENTICATION_RESULT)).andReturn(ALLOW_ALL_RESULT).anyTimes();
        req.setAttribute(DRUID_AUTHORIZATION_CHECKED, true);
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(req.getContentType()).andReturn("application/json").times((2 * (EventReceiverFirehoseTest.NUM_EVENTS)));
        EasyMock.expect(req.getHeader("X-Firehose-Producer-Id")).andReturn(null).times((2 * (EventReceiverFirehoseTest.NUM_EVENTS)));
        EasyMock.replay(req);
        final ExecutorService executorService = Execs.singleThreaded("single_thread");
        final Future future = executorService.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                for (int i = 0; i < (EventReceiverFirehoseTest.NUM_EVENTS); ++i) {
                    final InputStream inputStream = IOUtils.toInputStream(inputRow, StandardCharsets.UTF_8);
                    firehose.addAll(inputStream, req);
                    inputStream.close();
                }
                return true;
            }
        });
        for (int i = 0; i < (EventReceiverFirehoseTest.NUM_EVENTS); ++i) {
            final InputStream inputStream = IOUtils.toInputStream(inputRow, StandardCharsets.UTF_8);
            firehose.addAll(inputStream, req);
            inputStream.close();
        }
        future.get(10, TimeUnit.SECONDS);
        EasyMock.verify(req);
        final Iterable<Map.Entry<String, EventReceiverFirehoseMetric>> metrics = register.getMetrics();
        Assert.assertEquals(1, Iterables.size(metrics));
        final Map.Entry<String, EventReceiverFirehoseMetric> entry = Iterables.getLast(metrics);
        Assert.assertEquals(EventReceiverFirehoseTest.SERVICE_NAME, entry.getKey());
        Assert.assertEquals(EventReceiverFirehoseTest.CAPACITY, entry.getValue().getCapacity());
        Assert.assertEquals(EventReceiverFirehoseTest.CAPACITY, firehose.getCapacity());
        Assert.assertEquals((2 * (EventReceiverFirehoseTest.NUM_EVENTS)), entry.getValue().getCurrentBufferSize());
        Assert.assertEquals((2 * (EventReceiverFirehoseTest.NUM_EVENTS)), firehose.getCurrentBufferSize());
        for (int i = (2 * (EventReceiverFirehoseTest.NUM_EVENTS)) - 1; i >= 0; --i) {
            Assert.assertTrue(firehose.hasMore());
            Assert.assertNotNull(firehose.nextRow());
            Assert.assertEquals(i, firehose.getCurrentBufferSize());
        }
        Assert.assertEquals(EventReceiverFirehoseTest.CAPACITY, entry.getValue().getCapacity());
        Assert.assertEquals(EventReceiverFirehoseTest.CAPACITY, firehose.getCapacity());
        Assert.assertEquals(0, entry.getValue().getCurrentBufferSize());
        Assert.assertEquals(0, firehose.getCurrentBufferSize());
        firehose.close();
        Assert.assertFalse(firehose.hasMore());
        Assert.assertEquals(0, Iterables.size(register.getMetrics()));
        awaitDelayedExecutorThreadTerminated();
        executorService.shutdownNow();
    }

    @Test(expected = ISE.class)
    public void testDuplicateRegistering() {
        EventReceiverFirehoseFactory eventReceiverFirehoseFactory2 = new EventReceiverFirehoseFactory(EventReceiverFirehoseTest.SERVICE_NAME, EventReceiverFirehoseTest.CAPACITY, EventReceiverFirehoseTest.MAX_IDLE_TIME_MILLIS, null, new DefaultObjectMapper(), new DefaultObjectMapper(), register, AuthTestUtils.TEST_AUTHORIZER_MAPPER);
        EventReceiverFirehoseFactory.EventReceiverFirehose firehose2 = ((EventReceiverFirehoseFactory.EventReceiverFirehose) (eventReceiverFirehoseFactory2.connect(new MapInputRowParser(new org.apache.druid.data.input.impl.JSONParseSpec(new TimestampSpec("timestamp", "auto", null), new DimensionsSpec(DimensionsSpec.getDefaultSchemas(ImmutableList.of("d1")), null, null), null, null)), null)));
    }

    @Test(timeout = 60000L)
    public void testShutdownWithPrevTime() throws Exception {
        EasyMock.expect(req.getAttribute(DRUID_AUTHORIZATION_CHECKED)).andReturn(null).anyTimes();
        EasyMock.expect(req.getAttribute(DRUID_ALLOW_UNSECURED_PATH)).andReturn(null).anyTimes();
        EasyMock.expect(req.getAttribute(DRUID_AUTHENTICATION_RESULT)).andReturn(ALLOW_ALL_RESULT).anyTimes();
        req.setAttribute(DRUID_AUTHORIZATION_CHECKED, true);
        EasyMock.expectLastCall().anyTimes();
        EasyMock.replay(req);
        firehose.shutdown(DateTimes.nowUtc().minusMinutes(2).toString(), req);
        awaitFirehoseClosed();
        awaitDelayedExecutorThreadTerminated();
    }

    @Test(timeout = 60000L)
    public void testShutdown() throws Exception {
        EasyMock.expect(req.getAttribute(DRUID_AUTHORIZATION_CHECKED)).andReturn(null).anyTimes();
        EasyMock.expect(req.getAttribute(DRUID_ALLOW_UNSECURED_PATH)).andReturn(null).anyTimes();
        EasyMock.expect(req.getAttribute(DRUID_AUTHENTICATION_RESULT)).andReturn(ALLOW_ALL_RESULT).anyTimes();
        req.setAttribute(DRUID_AUTHORIZATION_CHECKED, true);
        EasyMock.expectLastCall().anyTimes();
        EasyMock.replay(req);
        firehose.shutdown(DateTimes.nowUtc().plusMillis(100).toString(), req);
        awaitFirehoseClosed();
        awaitDelayedExecutorThreadTerminated();
    }

    @Test
    public void testProducerSequence() throws IOException {
        for (int i = 0; i < (EventReceiverFirehoseTest.NUM_EVENTS); ++i) {
            setUpRequestExpectations("producer", String.valueOf(i));
            final InputStream inputStream = IOUtils.toInputStream(inputRow, StandardCharsets.UTF_8);
            firehose.addAll(inputStream, req);
            Assert.assertEquals((i + 1), firehose.getCurrentBufferSize());
            inputStream.close();
        }
        EasyMock.verify(req);
        final Iterable<Map.Entry<String, EventReceiverFirehoseMetric>> metrics = register.getMetrics();
        Assert.assertEquals(1, Iterables.size(metrics));
        final Map.Entry<String, EventReceiverFirehoseMetric> entry = Iterables.getLast(metrics);
        Assert.assertEquals(EventReceiverFirehoseTest.SERVICE_NAME, entry.getKey());
        Assert.assertEquals(EventReceiverFirehoseTest.CAPACITY, entry.getValue().getCapacity());
        Assert.assertEquals(EventReceiverFirehoseTest.CAPACITY, firehose.getCapacity());
        Assert.assertEquals(EventReceiverFirehoseTest.NUM_EVENTS, entry.getValue().getCurrentBufferSize());
        Assert.assertEquals(EventReceiverFirehoseTest.NUM_EVENTS, firehose.getCurrentBufferSize());
        for (int i = (EventReceiverFirehoseTest.NUM_EVENTS) - 1; i >= 0; --i) {
            Assert.assertTrue(firehose.hasMore());
            Assert.assertNotNull(firehose.nextRow());
            Assert.assertEquals(i, firehose.getCurrentBufferSize());
        }
        Assert.assertEquals(EventReceiverFirehoseTest.CAPACITY, entry.getValue().getCapacity());
        Assert.assertEquals(EventReceiverFirehoseTest.CAPACITY, firehose.getCapacity());
        Assert.assertEquals(0, entry.getValue().getCurrentBufferSize());
        Assert.assertEquals(0, firehose.getCurrentBufferSize());
        firehose.close();
        Assert.assertFalse(firehose.hasMore());
        Assert.assertEquals(0, Iterables.size(register.getMetrics()));
    }

    @Test
    public void testLowProducerSequence() throws IOException {
        for (int i = 0; i < (EventReceiverFirehoseTest.NUM_EVENTS); ++i) {
            setUpRequestExpectations("producer", "1");
            final InputStream inputStream = IOUtils.toInputStream(inputRow, StandardCharsets.UTF_8);
            final Response response = firehose.addAll(inputStream, req);
            Assert.assertEquals(OK.getStatusCode(), response.getStatus());
            Assert.assertEquals(1, firehose.getCurrentBufferSize());
            inputStream.close();
        }
        EasyMock.verify(req);
        firehose.close();
    }

    @Test
    public void testMissingProducerSequence() throws IOException {
        setUpRequestExpectations("producer", null);
        final InputStream inputStream = IOUtils.toInputStream(inputRow, StandardCharsets.UTF_8);
        final Response response = firehose.addAll(inputStream, req);
        Assert.assertEquals(BAD_REQUEST.getStatusCode(), response.getStatus());
        inputStream.close();
        EasyMock.verify(req);
        firehose.close();
    }

    @Test
    public void testTooManyProducerIds() throws IOException {
        for (int i = 0; i < ((MAX_FIREHOSE_PRODUCERS) - 1); i++) {
            setUpRequestExpectations(("producer-" + i), "0");
            final InputStream inputStream = IOUtils.toInputStream(inputRow, StandardCharsets.UTF_8);
            final Response response = firehose.addAll(inputStream, req);
            Assert.assertEquals(OK.getStatusCode(), response.getStatus());
            inputStream.close();
            Assert.assertTrue(firehose.hasMore());
            Assert.assertNotNull(firehose.nextRow());
        }
        setUpRequestExpectations("toomany", "0");
        final InputStream inputStream = IOUtils.toInputStream(inputRow, StandardCharsets.UTF_8);
        final Response response = firehose.addAll(inputStream, req);
        Assert.assertEquals(FORBIDDEN.getStatusCode(), response.getStatus());
        inputStream.close();
        EasyMock.verify(req);
        firehose.close();
    }

    @Test
    public void testNaNProducerSequence() throws IOException {
        setUpRequestExpectations("producer", "foo");
        final InputStream inputStream = IOUtils.toInputStream(inputRow, StandardCharsets.UTF_8);
        final Response response = firehose.addAll(inputStream, req);
        Assert.assertEquals(BAD_REQUEST.getStatusCode(), response.getStatus());
        inputStream.close();
        EasyMock.verify(req);
        firehose.close();
    }
}

