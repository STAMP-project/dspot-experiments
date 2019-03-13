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
package org.apache.druid.java.util.emitter.core;


import ContentEncoding.GZIP;
import HttpHeaders.Names.AUTHORIZATION;
import HttpHeaders.Names.CONTENT_ENCODING;
import HttpHeaders.Names.CONTENT_TYPE;
import HttpResponseStatus.BAD_REQUEST;
import HttpResponseStatus.CREATED;
import HttpResponseStatus.FORBIDDEN;
import HttpVersion.HTTP_1_1;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.Unpooled;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import org.apache.druid.java.util.common.CompressionUtils;
import org.apache.druid.java.util.common.StringUtils;
import org.apache.druid.java.util.emitter.service.UnitEvent;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;
import org.asynchttpclient.netty.EagerResponseBodyPart;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class EmitterTest {
    private static final ObjectMapper jsonMapper = new ObjectMapper();

    public static String TARGET_URL = "http://metrics.foo.bar/";

    public static final Response OK_RESPONSE = Stream.of(EmitterTest.responseBuilder(HTTP_1_1, CREATED)).map(( b) -> {
        b.accumulate(new EagerResponseBodyPart(Unpooled.wrappedBuffer("Yay".getBytes(StandardCharsets.UTF_8)), true));
        return b.build();
    }).findFirst().get();

    public static final Response BAD_RESPONSE = Stream.of(EmitterTest.responseBuilder(HTTP_1_1, FORBIDDEN)).map(( b) -> {
        b.accumulate(new EagerResponseBodyPart(Unpooled.wrappedBuffer("Not yay".getBytes(StandardCharsets.UTF_8)), true));
        return b.build();
    }).findFirst().get();

    MockHttpClient httpClient;

    HttpPostEmitter emitter;

    @Test
    public void testSanity() throws Exception {
        final List<UnitEvent> events = Arrays.asList(new UnitEvent("test", 1), new UnitEvent("test", 2));
        emitter = sizeBasedEmitter(2);
        httpClient.setGoHandler(new GoHandler() {
            @Override
            protected ListenableFuture<Response> go(Request request) throws JsonProcessingException {
                Assert.assertEquals(EmitterTest.TARGET_URL, request.getUrl());
                Assert.assertEquals("application/json", request.getHeaders().get(CONTENT_TYPE));
                Assert.assertEquals(StringUtils.format("[%s,%s]\n", EmitterTest.jsonMapper.writeValueAsString(events.get(0)), EmitterTest.jsonMapper.writeValueAsString(events.get(1))), StandardCharsets.UTF_8.decode(request.getByteBufferData().slice()).toString());
                return GoHandlers.immediateFuture(EmitterTest.okResponse());
            }
        }.times(1));
        for (UnitEvent event : events) {
            emitter.emit(event);
        }
        waitForEmission(emitter, 1);
        closeNoFlush(emitter);
        Assert.assertTrue(httpClient.succeeded());
    }

    @Test
    public void testSanityWithGeneralizedCreation() throws Exception {
        final List<UnitEvent> events = Arrays.asList(new UnitEvent("test", 1), new UnitEvent("test", 2));
        emitter = sizeBasedEmitterGeneralizedCreation(2);
        httpClient.setGoHandler(new GoHandler() {
            @Override
            protected ListenableFuture<Response> go(Request request) throws JsonProcessingException {
                Assert.assertEquals(EmitterTest.TARGET_URL, request.getUrl());
                Assert.assertEquals("application/json", request.getHeaders().get(CONTENT_TYPE));
                Assert.assertEquals(StringUtils.format("[%s,%s]\n", EmitterTest.jsonMapper.writeValueAsString(events.get(0)), EmitterTest.jsonMapper.writeValueAsString(events.get(1))), StandardCharsets.UTF_8.decode(request.getByteBufferData().slice()).toString());
                return GoHandlers.immediateFuture(EmitterTest.okResponse());
            }
        }.times(1));
        for (UnitEvent event : events) {
            emitter.emit(event);
        }
        waitForEmission(emitter, 1);
        closeNoFlush(emitter);
        Assert.assertTrue(httpClient.succeeded());
    }

    @Test
    public void testSizeBasedEmission() throws Exception {
        emitter = sizeBasedEmitter(3);
        httpClient.setGoHandler(GoHandlers.failingHandler());
        emitter.emit(new UnitEvent("test", 1));
        emitter.emit(new UnitEvent("test", 2));
        httpClient.setGoHandler(GoHandlers.passingHandler(EmitterTest.okResponse()).times(1));
        emitter.emit(new UnitEvent("test", 3));
        waitForEmission(emitter, 1);
        httpClient.setGoHandler(GoHandlers.failingHandler());
        emitter.emit(new UnitEvent("test", 4));
        emitter.emit(new UnitEvent("test", 5));
        closeAndExpectFlush(emitter);
        Assert.assertTrue(httpClient.succeeded());
    }

    @Test
    public void testTimeBasedEmission() throws Exception {
        final int timeBetweenEmissions = 100;
        emitter = timeBasedEmitter(timeBetweenEmissions);
        final CountDownLatch latch = new CountDownLatch(1);
        httpClient.setGoHandler(new GoHandler() {
            @Override
            protected ListenableFuture<Response> go(Request request) {
                latch.countDown();
                return GoHandlers.immediateFuture(EmitterTest.okResponse());
            }
        }.times(1));
        long emitTime = System.currentTimeMillis();
        emitter.emit(new UnitEvent("test", 1));
        latch.await();
        long timeWaited = (System.currentTimeMillis()) - emitTime;
        Assert.assertTrue(StringUtils.format("timeWaited[%s] !< %s", timeWaited, (timeBetweenEmissions * 2)), (timeWaited < (timeBetweenEmissions * 2)));
        waitForEmission(emitter, 1);
        final CountDownLatch thisLatch = new CountDownLatch(1);
        httpClient.setGoHandler(new GoHandler() {
            @Override
            protected ListenableFuture<Response> go(Request request) {
                thisLatch.countDown();
                return GoHandlers.immediateFuture(EmitterTest.okResponse());
            }
        }.times(1));
        emitTime = System.currentTimeMillis();
        emitter.emit(new UnitEvent("test", 2));
        thisLatch.await();
        timeWaited = (System.currentTimeMillis()) - emitTime;
        Assert.assertTrue(StringUtils.format("timeWaited[%s] !< %s", timeWaited, (timeBetweenEmissions * 2)), (timeWaited < (timeBetweenEmissions * 2)));
        waitForEmission(emitter, 2);
        closeNoFlush(emitter);
        Assert.assertTrue("httpClient.succeeded()", httpClient.succeeded());
    }

    @Test(timeout = 60000L)
    public void testFailedEmission() throws Exception {
        final UnitEvent event1 = new UnitEvent("test", 1);
        final UnitEvent event2 = new UnitEvent("test", 2);
        emitter = sizeBasedEmitter(1);
        Assert.assertEquals(0, emitter.getTotalEmittedEvents());
        Assert.assertEquals(0, emitter.getSuccessfulSendingTimeCounter().getTimeSumAndCount());
        Assert.assertEquals(0, emitter.getFailedSendingTimeCounter().getTimeSumAndCount());
        httpClient.setGoHandler(new GoHandler() {
            @Override
            protected ListenableFuture<Response> go(Request request) {
                Response response = EmitterTest.responseBuilder(HTTP_1_1, BAD_REQUEST).build();
                return GoHandlers.immediateFuture(response);
            }
        });
        emitter.emit(event1);
        emitter.flush();
        waitForEmission(emitter, 1);
        Assert.assertTrue(httpClient.succeeded());
        // Failed to emit the first event.
        Assert.assertEquals(0, emitter.getTotalEmittedEvents());
        Assert.assertEquals(0, emitter.getSuccessfulSendingTimeCounter().getTimeSumAndCount());
        Assert.assertTrue(((emitter.getFailedSendingTimeCounter().getTimeSumAndCount()) > 0));
        httpClient.setGoHandler(new GoHandler() {
            @Override
            protected ListenableFuture<Response> go(Request request) {
                return GoHandlers.immediateFuture(EmitterTest.okResponse());
            }
        }.times(2));
        emitter.emit(event2);
        emitter.flush();
        waitForEmission(emitter, 2);
        closeNoFlush(emitter);
        // Failed event is emitted inside emitter thread, there is no other way to wait for it other than joining the
        // emitterThread
        emitter.joinEmitterThread();
        // Succeed to emit both events.
        Assert.assertEquals(2, emitter.getTotalEmittedEvents());
        Assert.assertTrue(((emitter.getSuccessfulSendingTimeCounter().getTimeSumAndCount()) > 0));
        Assert.assertTrue(((emitter.getFailedSendingTimeCounter().getTimeSumAndCount()) > 0));
        Assert.assertTrue(httpClient.succeeded());
    }

    @Test
    public void testBasicAuthenticationAndNewlineSeparating() throws Exception {
        final List<UnitEvent> events = Arrays.asList(new UnitEvent("test", 1), new UnitEvent("test", 2));
        emitter = manualFlushEmitterWithBasicAuthenticationAndNewlineSeparating("foo:bar");
        httpClient.setGoHandler(new GoHandler() {
            @Override
            protected ListenableFuture<Response> go(Request request) throws JsonProcessingException {
                Assert.assertEquals(EmitterTest.TARGET_URL, request.getUrl());
                Assert.assertEquals("application/json", request.getHeaders().get(CONTENT_TYPE));
                Assert.assertEquals(("Basic " + (StringUtils.encodeBase64String(StringUtils.toUtf8("foo:bar")))), request.getHeaders().get(AUTHORIZATION));
                Assert.assertEquals(StringUtils.format("%s\n%s\n", EmitterTest.jsonMapper.writeValueAsString(events.get(0)), EmitterTest.jsonMapper.writeValueAsString(events.get(1))), StandardCharsets.UTF_8.decode(request.getByteBufferData().slice()).toString());
                return GoHandlers.immediateFuture(EmitterTest.okResponse());
            }
        }.times(1));
        for (UnitEvent event : events) {
            emitter.emit(event);
        }
        emitter.flush();
        waitForEmission(emitter, 1);
        closeNoFlush(emitter);
        Assert.assertTrue(httpClient.succeeded());
    }

    @Test
    public void testBatchSplitting() throws Exception {
        final byte[] big = new byte[500 * 1024];
        for (int i = 0; i < (big.length); i++) {
            big[i] = 'x';
        }
        final String bigString = StringUtils.fromUtf8(big);
        final List<UnitEvent> events = Arrays.asList(new UnitEvent(bigString, 1), new UnitEvent(bigString, 2), new UnitEvent(bigString, 3), new UnitEvent(bigString, 4));
        final AtomicInteger counter = new AtomicInteger();
        emitter = manualFlushEmitterWithBatchSize((1024 * 1024));
        Assert.assertEquals(0, emitter.getTotalEmittedEvents());
        Assert.assertEquals(0, emitter.getSuccessfulSendingTimeCounter().getTimeSumAndCount());
        Assert.assertEquals(0, emitter.getFailedSendingTimeCounter().getTimeSumAndCount());
        httpClient.setGoHandler(new GoHandler() {
            @Override
            protected ListenableFuture<Response> go(Request request) throws JsonProcessingException {
                Assert.assertEquals(EmitterTest.TARGET_URL, request.getUrl());
                Assert.assertEquals("application/json", request.getHeaders().get(CONTENT_TYPE));
                Assert.assertEquals(StringUtils.format("[%s,%s]\n", EmitterTest.jsonMapper.writeValueAsString(events.get(counter.getAndIncrement())), EmitterTest.jsonMapper.writeValueAsString(events.get(counter.getAndIncrement()))), StandardCharsets.UTF_8.decode(request.getByteBufferData().slice()).toString());
                return GoHandlers.immediateFuture(EmitterTest.okResponse());
            }
        }.times(3));
        for (UnitEvent event : events) {
            emitter.emit(event);
        }
        waitForEmission(emitter, 1);
        Assert.assertEquals(2, emitter.getTotalEmittedEvents());
        Assert.assertTrue(((emitter.getSuccessfulSendingTimeCounter().getTimeSumAndCount()) > 0));
        Assert.assertEquals(0, emitter.getFailedSendingTimeCounter().getTimeSumAndCount());
        emitter.flush();
        waitForEmission(emitter, 2);
        Assert.assertEquals(4, emitter.getTotalEmittedEvents());
        Assert.assertTrue(((emitter.getSuccessfulSendingTimeCounter().getTimeSumAndCount()) > 0));
        Assert.assertEquals(0, emitter.getFailedSendingTimeCounter().getTimeSumAndCount());
        closeNoFlush(emitter);
        Assert.assertTrue(httpClient.succeeded());
    }

    @Test
    public void testGzipContentEncoding() throws Exception {
        final List<UnitEvent> events = Arrays.asList(new UnitEvent("plain-text", 1), new UnitEvent("plain-text", 2));
        emitter = sizeBasedEmitterWithContentEncoding(2, GZIP);
        httpClient.setGoHandler(new GoHandler() {
            @Override
            protected ListenableFuture<Response> go(Request request) throws IOException {
                Assert.assertEquals(EmitterTest.TARGET_URL, request.getUrl());
                Assert.assertEquals("application/json", request.getHeaders().get(CONTENT_TYPE));
                Assert.assertEquals(HttpHeaders.Values.GZIP, request.getHeaders().get(CONTENT_ENCODING));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ByteBuffer data = request.getByteBufferData().slice();
                byte[] dataArray = new byte[data.remaining()];
                data.get(dataArray);
                CompressionUtils.gunzip(new ByteArrayInputStream(dataArray), baos);
                Assert.assertEquals(StringUtils.format("[%s,%s]\n", EmitterTest.jsonMapper.writeValueAsString(events.get(0)), EmitterTest.jsonMapper.writeValueAsString(events.get(1))), baos.toString(StandardCharsets.UTF_8.name()));
                return GoHandlers.immediateFuture(EmitterTest.okResponse());
            }
        }.times(1));
        for (UnitEvent event : events) {
            emitter.emit(event);
        }
        waitForEmission(emitter, 1);
        closeNoFlush(emitter);
        Assert.assertTrue(httpClient.succeeded());
    }
}

