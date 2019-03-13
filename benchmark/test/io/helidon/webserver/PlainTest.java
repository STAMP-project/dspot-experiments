/**
 * Copyright (c) 2017, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.helidon.webserver;


import Http.Method.GET;
import Http.Method.POST;
import Http.Method.TRACE;
import io.helidon.common.CollectionsHelper;
import io.helidon.webserver.utils.SocketHttpClient;
import java.util.Map;
import java.util.logging.Logger;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.Test;


/**
 * The PlainTest.
 */
public class PlainTest {
    private static final Logger LOGGER = Logger.getLogger(PlainTest.class.getName());

    private static WebServer webServer;

    @Test
    public void getTest() throws Exception {
        String s = SocketHttpClient.sendAndReceive(GET, null, PlainTest.webServer);
        MatcherAssert.assertThat(cutPayloadAndCheckHeadersFormat(s), CoreMatchers.is("9\nIt works!\n0\n\n"));
        Map<String, String> headers = cutHeaders(s);
        MatcherAssert.assertThat(headers, hasEntry("connection", "keep-alive"));
    }

    @Test
    public void getDeferredTest() throws Exception {
        String s = SocketHttpClient.sendAndReceive("/deferred", GET, null, PlainTest.webServer);
        MatcherAssert.assertThat(cutPayloadAndCheckHeadersFormat(s), CoreMatchers.is("d\nI\'m deferred!\n0\n\n"));
    }

    @Test
    public void getWithPayloadDeferredTest() throws Exception {
        String s = SocketHttpClient.sendAndReceive("/deferred", GET, "illegal-payload", PlainTest.webServer);
        MatcherAssert.assertThat(cutPayloadAndCheckHeadersFormat(s), CoreMatchers.is("d\nI\'m deferred!\n0\n\n"));
    }

    @Test
    public void getWithLargePayloadDeferredTest() throws Exception {
        String s = SocketHttpClient.sendAndReceive("/deferred", GET, SocketHttpClient.longData(100000).toString(), PlainTest.webServer);
        MatcherAssert.assertThat(cutPayloadAndCheckHeadersFormat(s), CoreMatchers.is("d\nI\'m deferred!\n0\n\n"));
    }

    @Test
    public void getWithPayloadTest() throws Exception {
        String s = SocketHttpClient.sendAndReceive(GET, "test-payload", PlainTest.webServer);
        MatcherAssert.assertThat(cutPayloadAndCheckHeadersFormat(s), CoreMatchers.is("9\nIt works!\n0\n\n"));
    }

    @Test
    public void postNoPayloadTest() throws Exception {
        String s = SocketHttpClient.sendAndReceive(POST, null, PlainTest.webServer);
        MatcherAssert.assertThat(cutPayloadAndCheckHeadersFormat(s), CoreMatchers.is("13\nIt works! Payload: \n0\n\n"));
    }

    @Test
    public void simplePostTest() throws Exception {
        String s = SocketHttpClient.sendAndReceive(POST, "test-payload", PlainTest.webServer);
        MatcherAssert.assertThat(cutPayloadAndCheckHeadersFormat(s), CoreMatchers.is("1f\nIt works! Payload: test-payload\n0\n\n"));
    }

    @Test
    public void twoGetsTest() throws Exception {
        getTest();
        getTest();
    }

    @Test
    public void twoGetsWithPayloadTest() throws Exception {
        getWithPayloadTest();
        getWithPayloadTest();
    }

    @Test
    public void testTwoGetsTheSameConnection() throws Exception {
        // open
        try (SocketHttpClient s = new SocketHttpClient(PlainTest.webServer)) {
            // get
            s.request(GET);
            // assert
            MatcherAssert.assertThat(cutPayloadAndCheckHeadersFormat(s.receive()), CoreMatchers.is("9\nIt works!\n0\n\n"));
            // get
            s.request(GET);
            // assert
            MatcherAssert.assertThat(cutPayloadAndCheckHeadersFormat(s.receive()), CoreMatchers.is("9\nIt works!\n0\n\n"));
        }
    }

    @Test
    public void testTwoPostsTheSameConnection() throws Exception {
        // open
        try (SocketHttpClient s = new SocketHttpClient(PlainTest.webServer)) {
            // post
            s.request(POST, "test-payload-1");
            // assert
            MatcherAssert.assertThat(cutPayloadAndCheckHeadersFormat(s.receive()), CoreMatchers.is("21\nIt works! Payload: test-payload-1\n0\n\n"));
            // post
            s.request(POST, "test-payload-2");
            // assert
            MatcherAssert.assertThat(cutPayloadAndCheckHeadersFormat(s.receive()), CoreMatchers.is("21\nIt works! Payload: test-payload-2\n0\n\n"));
        }
    }

    @Test
    public void postGetPostGetTheSameConnection() throws Exception {
        try (SocketHttpClient s = new SocketHttpClient(PlainTest.webServer)) {
            // post
            s.request(POST, "test-payload-1");
            // assert
            MatcherAssert.assertThat(cutPayloadAndCheckHeadersFormat(s.receive()), CoreMatchers.is("21\nIt works! Payload: test-payload-1\n0\n\n"));
            // get
            s.request(GET);
            // assert
            MatcherAssert.assertThat(cutPayloadAndCheckHeadersFormat(s.receive()), CoreMatchers.is("9\nIt works!\n0\n\n"));
            // post
            s.request(POST, "test-payload-2");
            // assert
            MatcherAssert.assertThat(cutPayloadAndCheckHeadersFormat(s.receive()), CoreMatchers.is("21\nIt works! Payload: test-payload-2\n0\n\n"));
            // get
            s.request(GET);
            // assert
            MatcherAssert.assertThat(cutPayloadAndCheckHeadersFormat(s.receive()), CoreMatchers.is("9\nIt works!\n0\n\n"));
        }
    }

    @Test
    public void getWithLargePayloadCausesConnectionClose() throws Exception {
        // open
        try (SocketHttpClient s = new SocketHttpClient(PlainTest.webServer)) {
            // get
            s.request(GET, SocketHttpClient.longData(100000).toString());
            // assert
            MatcherAssert.assertThat(cutPayloadAndCheckHeadersFormat(s.receive()), CoreMatchers.is("9\nIt works!\n0\n\n"));
            SocketHttpClient.assertConnectionIsClosed(s);
        }
    }

    @Test
    public void traceWithAnyPayloadCausesConnectionCloseButDoesNotFail() throws Exception {
        // open
        try (SocketHttpClient s = new SocketHttpClient(PlainTest.webServer)) {
            // get
            s.request(TRACE, "/trace", "small");
            // assert
            MatcherAssert.assertThat(cutPayloadAndCheckHeadersFormat(s.receive()), CoreMatchers.is("9\nIn trace!\n0\n\n"));
            SocketHttpClient.assertConnectionIsClosed(s);
        }
    }

    @Test
    public void traceWithAnyPayloadCausesConnectionCloseAndBadRequestWhenHandled() throws Exception {
        // open
        try (SocketHttpClient s = new SocketHttpClient(PlainTest.webServer)) {
            // get
            s.request(TRACE, "small");
            // assert that the Handler.of ContentReader transforms the exception to 400 error
            MatcherAssert.assertThat(s.receive(), CoreMatchers.startsWith("HTTP/1.1 400 Bad Request\n"));
            SocketHttpClient.assertConnectionIsClosed(s);
        }
    }

    @Test
    public void deferredGetWithLargePayloadCausesConnectionClose() throws Exception {
        // open
        try (SocketHttpClient s = new SocketHttpClient(PlainTest.webServer)) {
            // get
            s.request(GET, "/deferred", SocketHttpClient.longData(100000).toString());
            // assert
            MatcherAssert.assertThat(cutPayloadAndCheckHeadersFormat(s.receive()), CoreMatchers.is("d\nI\'m deferred!\n0\n\n"));
            SocketHttpClient.assertConnectionIsClosed(s);
        }
    }

    @Test
    public void getWithIllegalSmallEnoughPayloadDoesntCauseConnectionClose() throws Exception {
        // open
        try (SocketHttpClient s = new SocketHttpClient(PlainTest.webServer)) {
            // get
            s.request(GET, "illegal-but-small-enough-payload");
            // assert
            MatcherAssert.assertThat(cutPayloadAndCheckHeadersFormat(s.receive()), CoreMatchers.is("9\nIt works!\n0\n\n"));
            SocketHttpClient.assertConnectionIsOpen(s);
        }
    }

    @Test
    public void unconsumedSmallPostDataDoesNotCauseConnectionClose() throws Exception {
        // open
        try (SocketHttpClient s = new SocketHttpClient(PlainTest.webServer)) {
            // get
            s.request(POST, "/unconsumed", "not-consumed-payload");
            // assert
            MatcherAssert.assertThat(cutPayloadAndCheckHeadersFormat(s.receive()), CoreMatchers.is("15\nPayload not consumed!\n0\n\n"));
            SocketHttpClient.assertConnectionIsOpen(s);
        }
    }

    @Test
    public void unconsumedLargePostDataCausesConnectionClose() throws Exception {
        // open
        try (SocketHttpClient s = new SocketHttpClient(PlainTest.webServer)) {
            // get
            s.request(POST, "/unconsumed", SocketHttpClient.longData(100000).toString());
            // assert
            MatcherAssert.assertThat(cutPayloadAndCheckHeadersFormat(s.receive()), CoreMatchers.is("15\nPayload not consumed!\n0\n\n"));
            SocketHttpClient.assertConnectionIsClosed(s);
        }
    }

    @Test
    public void unconsumedDeferredLargePostDataCausesConnectionClose() throws Exception {
        // open
        try (SocketHttpClient s = new SocketHttpClient(PlainTest.webServer)) {
            // get
            s.request(POST, "/deferred", SocketHttpClient.longData(100000).toString());
            // assert
            MatcherAssert.assertThat(cutPayloadAndCheckHeadersFormat(s.receive()), CoreMatchers.is("d\nI\'m deferred!\n0\n\n"));
            SocketHttpClient.assertConnectionIsClosed(s);
        }
    }

    @Test
    public void errorHandlerWithGetPayloadDoesNotCauseConnectionClose() throws Exception {
        // open
        try (SocketHttpClient s = new SocketHttpClient(PlainTest.webServer)) {
            // get
            s.request(GET, "/exception", "not-consumed-payload");
            // assert
            MatcherAssert.assertThat(s.receive(), CoreMatchers.startsWith("HTTP/1.1 500 Internal Server Error\n"));
            SocketHttpClient.assertConnectionIsOpen(s);
        }
    }

    @Test
    public void errorHandlerWithPostDataDoesNotCauseConnectionClose() throws Exception {
        // open
        try (SocketHttpClient s = new SocketHttpClient(PlainTest.webServer)) {
            // get
            s.request(POST, "/exception", "not-consumed-payload");
            // assert
            MatcherAssert.assertThat(s.receive(), CoreMatchers.startsWith("HTTP/1.1 500 Internal Server Error\n"));
            SocketHttpClient.assertConnectionIsOpen(s);
        }
    }

    @Test
    public void testConnectionCloseWhenKeepAliveOff() throws Exception {
        try (SocketHttpClient s = new SocketHttpClient(PlainTest.webServer)) {
            // get
            s.request(GET, "/", null, CollectionsHelper.listOf("Connection: close"));
            // assert
            MatcherAssert.assertThat(cutPayloadAndCheckHeadersFormat(s.receive()), CoreMatchers.is("It works!\n"));
            SocketHttpClient.assertConnectionIsClosed(s);
        }
    }

    @Test
    public void testForcedChunkedWithConnectionCloseHeader() throws Exception {
        String s = SocketHttpClient.sendAndReceive("/force-chunked", GET, null, CollectionsHelper.listOf("Connection: close"), PlainTest.webServer);
        MatcherAssert.assertThat(cutPayloadAndCheckHeadersFormat(s), CoreMatchers.is("4\nabcd\n0\n\n"));
        Map<String, String> headers = cutHeaders(s);
        MatcherAssert.assertThat(headers, CoreMatchers.not(IsMapContaining.hasKey("connection")));
        MatcherAssert.assertThat(headers, hasEntry(Http.Header.TRANSFER_ENCODING, "chunked"));
    }

    @Test
    public void testConnectionCloseHeader() throws Exception {
        String s = SocketHttpClient.sendAndReceive("/", GET, null, CollectionsHelper.listOf("Connection: close"), PlainTest.webServer);
        MatcherAssert.assertThat(cutPayloadAndCheckHeadersFormat(s), CoreMatchers.is("It works!\n"));
        Map<String, String> headers = cutHeaders(s);
        MatcherAssert.assertThat(headers, CoreMatchers.not(IsMapContaining.hasKey("connection")));
    }

    @Test
    public void name() throws Exception {
        for (byte b : "myData".getBytes()) {
            System.out.println(b);
        }
    }
}

