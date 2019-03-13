/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2019 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.undertow.server.handlers;


import PathTemplateMatch.ATTACHMENT_KEY;
import UndertowOptions.DECODE_URL;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.RoutingHandler;
import io.undertow.testutils.TestHttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Carter Kozak
 */
public class URLDecodingHandlerTestCase {
    private static int PORT = 7890;

    @Test
    public void testDoesNotDecodeByDefault() throws Exception {
        // By default Undertow decodes upon accepting requests, see UndertowOptions.DECODE_URL.
        // If this is enabled, the URLDecodingHandler should no-op
        Undertow undertow = Undertow.builder().addHttpListener(URLDecodingHandlerTestCase.PORT, "0.0.0.0").setHandler(new URLDecodingHandler(new HttpHandler() {
            @Override
            public void handleRequest(HttpServerExchange exchange) throws Exception {
                exchange.getResponseSender().send(exchange.getRelativePath());
            }
        }, "UTF-8")).build();
        undertow.start();
        try {
            TestHttpClient client = new TestHttpClient();
            // '%253E' decodes to '%3E', which would decode to '>' if decoded twice
            try (CloseableHttpResponse response = client.execute(new HttpGet((("http://localhost:" + (URLDecodingHandlerTestCase.PORT)) + "/%253E")))) {
                Assert.assertEquals("/%3E", URLDecodingHandlerTestCase.getResponseString(response));
            }
        } finally {
            undertow.stop();
        }
    }

    @Test
    public void testDecodesWhenUrlDecodingIsDisabled() throws Exception {
        // When UndertowOptions.DECODE_URL is disabled, the URLDecodingHandler should decode values.
        Undertow undertow = Undertow.builder().setServerOption(DECODE_URL, false).addHttpListener(URLDecodingHandlerTestCase.PORT, "0.0.0.0").setHandler(new URLDecodingHandler(new HttpHandler() {
            @Override
            public void handleRequest(HttpServerExchange exchange) throws Exception {
                exchange.getResponseSender().send(exchange.getRelativePath());
            }
        }, "UTF-8")).build();
        undertow.start();
        try {
            TestHttpClient client = new TestHttpClient();
            // '%253E' decodes to '%3E', which would decode to '>' if decoded twice
            try (CloseableHttpResponse response = client.execute(new HttpGet((("http://localhost:" + (URLDecodingHandlerTestCase.PORT)) + "/%253E")))) {
                Assert.assertEquals("/%3E", URLDecodingHandlerTestCase.getResponseString(response));
            }
        } finally {
            undertow.stop();
        }
    }

    @Test
    public void testDecodeCharactersInMatchedPaths() throws Exception {
        // When UndertowOptions.DECODE_URL is disabled, the URLDecodingHandler should decode values.
        Undertow undertow = Undertow.builder().setServerOption(DECODE_URL, false).addHttpListener(URLDecodingHandlerTestCase.PORT, "0.0.0.0").setHandler(new RoutingHandler().get("/api/{pathParam}/tail", new URLDecodingHandler(new HttpHandler() {
            @Override
            public void handleRequest(HttpServerExchange exchange) throws Exception {
                String matched = exchange.getAttachment(ATTACHMENT_KEY).getParameters().get("pathParam");
                exchange.getResponseSender().send(matched);
            }
        }, "UTF-8"))).build();
        undertow.start();
        try {
            TestHttpClient client = new TestHttpClient();
            // '%253E' decodes to '%3E', which would decode to '>' if decoded twice
            try (CloseableHttpResponse response = client.execute(new HttpGet((("http://localhost:" + (URLDecodingHandlerTestCase.PORT)) + "/api/test%2Ftest+test%2Btest%20test/tail")))) {
                Assert.assertEquals("test/test+test+test test", URLDecodingHandlerTestCase.getResponseString(response));
            }
        } finally {
            undertow.stop();
        }
    }

    @Test
    public void testMultipleURLDecodingHandlers() throws Exception {
        // When multiple URLDecodingHandler are present, only the first handler to consume an exchange should decode
        Undertow undertow = Undertow.builder().setServerOption(DECODE_URL, false).addHttpListener(URLDecodingHandlerTestCase.PORT, "0.0.0.0").setHandler(new URLDecodingHandler(new URLDecodingHandler(new HttpHandler() {
            @Override
            public void handleRequest(HttpServerExchange exchange) throws Exception {
                exchange.getResponseSender().send(exchange.getRelativePath());
            }
        }, "UTF-8"), "UTF-8")).build();
        undertow.start();
        try {
            TestHttpClient client = new TestHttpClient();
            // '%253E' decodes to '%3E', which would decode to '>' if decoded twice
            try (CloseableHttpResponse response = client.execute(new HttpGet((("http://localhost:" + (URLDecodingHandlerTestCase.PORT)) + "/%253E")))) {
                Assert.assertEquals("/%3E", URLDecodingHandlerTestCase.getResponseString(response));
            }
        } finally {
            undertow.stop();
        }
    }
}

