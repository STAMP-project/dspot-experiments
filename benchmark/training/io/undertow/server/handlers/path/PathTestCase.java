/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
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
package io.undertow.server.handlers.path;


import StatusCodes.NOT_FOUND;
import StatusCodes.OK;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.PathHandler;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpClientUtils;
import io.undertow.testutils.TestHttpClient;
import io.undertow.util.HttpString;
import java.io.IOException;
import java.util.Collections;
import java.util.Deque;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests that the path handler works as expected
 *
 * @author Stuart Douglas
 */
@RunWith(DefaultServer.class)
public class PathTestCase {
    public static final String MATCHED = "matched";

    public static final String PATH = "path";

    @Test
    public void testBasicPathHanding() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            final PathHandler handler = new PathHandler();
            handler.addPrefixPath("a", new PathTestCase.RemainingPathHandler("/a"));
            handler.addPrefixPath("/aa", new PathTestCase.RemainingPathHandler("/aa"));
            handler.addExactPath("/aa", new HttpHandler() {
                @Override
                public void handleRequest(HttpServerExchange exchange) throws Exception {
                    exchange.getResponseSender().send(((("Exact /aa match:" + (exchange.getRelativePath())) + ":") + (exchange.getResolvedPath())));
                }
            });
            handler.addPrefixPath("/aa/anotherSubPath", new PathTestCase.RemainingPathHandler("/aa/anotherSubPath"));
            final PathHandler sub = new PathHandler();
            handler.addPrefixPath("/path", sub);
            sub.addPrefixPath("/subpath", new PathTestCase.RemainingPathHandler("/subpath"));
            sub.addPrefixPath("/", new PathTestCase.RemainingPathHandler("/path"));
            DefaultServer.setRootHandler(handler);
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/notamatchingpath"));
            HttpResponse result = client.execute(get);
            Assert.assertEquals(NOT_FOUND, result.getStatusLine().getStatusCode());
            HttpClientUtils.readResponse(result);
            get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/"));
            result = client.execute(get);
            Assert.assertEquals(NOT_FOUND, result.getStatusLine().getStatusCode());
            HttpClientUtils.readResponse(result);
            runPathTest(client, "/path", "/path", "");
            runPathTest(client, "/path/a", "/path", "/a");
            runPathTest(client, "/path/subpath", "/subpath", "");
            runPathTest(client, "/path/subpath/", "/subpath", "/");
            runPathTest(client, "/path/subpath/foo", "/subpath", "/foo");
            runPathTest(client, "/a", "/a", "");
            runPathTest(client, "/aa/anotherSubPath", "/aa/anotherSubPath", "");
            runPathTest(client, "/aa/anotherSubPath/bob", "/aa/anotherSubPath", "/bob");
            runPathTest(client, "/aa/b?a=b", "/aa", "/b", Collections.singletonMap("a", "b"));
            runPathTest(client, "/path/:bar/baz", "/path", "/:bar/baz");
            // now test the exact path match
            get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/aa"));
            result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            Assert.assertEquals("Exact /aa match::/aa", HttpClientUtils.readResponse(result));
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    private static class RemainingPathHandler implements HttpHandler {
        private final String matched;

        private RemainingPathHandler(String matched) {
            this.matched = matched;
        }

        @Override
        public void handleRequest(HttpServerExchange exchange) throws Exception {
            exchange.getResponseHeaders().add(new HttpString(PathTestCase.MATCHED), matched);
            exchange.getResponseHeaders().add(new HttpString(PathTestCase.PATH), exchange.getRelativePath());
            for (Map.Entry<String, Deque<String>> param : exchange.getQueryParameters().entrySet()) {
                exchange.getResponseHeaders().put(new HttpString(param.getKey()), param.getValue().getFirst());
            }
            exchange.endExchange();
        }
    }
}

