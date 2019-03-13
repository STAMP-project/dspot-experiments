/**
 * Copyright (c) 2011-2017 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package io.vertx.core.http;


import HttpHeaders.APPLICATION_X_WWW_FORM_URLENCODED;
import HttpHeaders.CONNECTION;
import HttpHeaders.TEXT_HTML;
import HttpHeaders.USER_AGENT;
import HttpMethod.CONNECT;
import HttpMethod.DELETE;
import HttpMethod.GET;
import HttpMethod.HEAD;
import HttpMethod.OPTIONS;
import HttpMethod.OTHER;
import HttpMethod.PATCH;
import HttpMethod.POST;
import HttpMethod.PUT;
import HttpMethod.TRACE;
import io.netty.handler.codec.compression.DecompressionException;
import io.netty.handler.codec.http2.Http2Exception;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.dns.AddressResolverOptions;
import io.vertx.core.streams.Pump;
import io.vertx.test.core.TestUtils;
import io.vertx.test.core.VertxTestBase;
import io.vertx.test.netty.TestLoggerFactory;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 *
 *
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
/* @Test
public void testReset() throws Exception {
CountDownLatch latch = new CountDownLatch(1);
server.requestHandler(req -> {
req.exceptionHandler(err -> {
System.out.println("GOT ERR");
});
req.endHandler(v -> {
System.out.println("GOT END");
latch.countDown();
});
});
startServer();
HttpClientRequest req = client.get(DEFAULT_HTTP_PORT, DEFAULT_HTTP_HOST, "/somepath", resp -> {});
req.end();
awaitLatch(latch);
req.reset();

await();
}
 */
public abstract class HttpTest extends HttpTestBase {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    protected File testDir;

    @Test
    public void testClientRequestArguments() throws Exception {
        HttpClientRequest req = client.request(PUT, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, noOpHandler());
        TestUtils.assertNullPointerException(() -> req.putHeader(((String) (null)), "someValue"));
        TestUtils.assertNullPointerException(() -> req.putHeader(((CharSequence) (null)), "someValue"));
        TestUtils.assertNullPointerException(() -> req.putHeader("someKey", ((Iterable<String>) (null))));
        TestUtils.assertNullPointerException(() -> req.write(((Buffer) (null))));
        TestUtils.assertNullPointerException(() -> req.write(((String) (null))));
        TestUtils.assertNullPointerException(() -> req.write(null, "UTF-8"));
        TestUtils.assertNullPointerException(() -> req.write("someString", null));
        TestUtils.assertNullPointerException(() -> req.end(((Buffer) (null))));
        TestUtils.assertNullPointerException(() -> req.end(((String) (null))));
        TestUtils.assertNullPointerException(() -> req.end(null, "UTF-8"));
        TestUtils.assertNullPointerException(() -> req.end("someString", null));
        TestUtils.assertIllegalArgumentException(() -> req.setTimeout(0));
    }

    @Test
    public void testClientChaining() {
        server.requestHandler(noOpHandler());
        server.listen(onSuccess(( server) -> {
            HttpClientRequest req = client.request(HttpMethod.PUT, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, noOpHandler());
            assertTrue(((req.setChunked(true)) == req));
            assertTrue(((req.sendHead()) == req));
            assertTrue(((req.write("foo", "UTF-8")) == req));
            assertTrue(((req.write("foo")) == req));
            assertTrue(((req.write(Buffer.buffer("foo"))) == req));
            testComplete();
        }));
        await();
    }

    @Test
    public void testListenSocketAddress() {
        NetClient netClient = vertx.createNetClient();
        server = vertx.createHttpServer().requestHandler(( req) -> req.response().end());
        SocketAddress sockAddress = SocketAddress.inetSocketAddress(HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST);
        server.listen(sockAddress, onSuccess(( server) -> {
            netClient.connect(sockAddress, onSuccess(( sock) -> {
                sock.handler(( buf) -> {
                    assertTrue("Response is not an http 200", buf.toString("UTF-8").startsWith("HTTP/1.1 200 OK"));
                    testComplete();
                });
                sock.write("GET / HTTP/1.1\r\n\r\n");
            }));
        }));
        try {
            await();
        } finally {
            netClient.close();
        }
    }

    @Test
    public void testListenDomainSocketAddress() throws Exception {
        Vertx vx = Vertx.vertx(new VertxOptions().setPreferNativeTransport(true));
        Assume.assumeTrue("Native transport must be enabled", vx.isNativeTransportEnabled());
        NetClient netClient = vx.createNetClient();
        HttpServer httpserver = vx.createHttpServer().requestHandler(( req) -> req.response().end());
        File sockFile = TestUtils.tmpFile(".sock");
        SocketAddress sockAddress = SocketAddress.domainSocketAddress(sockFile.getAbsolutePath());
        httpserver.listen(sockAddress, onSuccess(( server) -> {
            netClient.connect(sockAddress, onSuccess(( sock) -> {
                sock.handler(( buf) -> {
                    assertTrue("Response is not an http 200", buf.toString("UTF-8").startsWith("HTTP/1.1 200 OK"));
                    testComplete();
                });
                sock.write("GET / HTTP/1.1\r\n\r\n");
            }));
        }));
        try {
            await();
        } finally {
            vx.close();
        }
    }

    @Test
    public void testLowerCaseHeaders() {
        server.requestHandler(( req) -> {
            assertEquals("foo", req.headers().get("Foo"));
            assertEquals("foo", req.headers().get("foo"));
            assertEquals("foo", req.headers().get("fOO"));
            assertTrue(req.headers().contains("Foo"));
            assertTrue(req.headers().contains("foo"));
            assertTrue(req.headers().contains("fOO"));
            req.response().putHeader("Quux", "quux");
            assertEquals("quux", req.response().headers().get("Quux"));
            assertEquals("quux", req.response().headers().get("quux"));
            assertEquals("quux", req.response().headers().get("qUUX"));
            assertTrue(req.response().headers().contains("Quux"));
            assertTrue(req.response().headers().contains("quux"));
            assertTrue(req.response().headers().contains("qUUX"));
            req.response().end();
        });
        server.listen(onSuccess(( server) -> {
            HttpClientRequest req = client.request(HttpMethod.GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, onSuccess(( resp) -> {
                assertEquals("quux", resp.headers().get("Quux"));
                assertEquals("quux", resp.headers().get("quux"));
                assertEquals("quux", resp.headers().get("qUUX"));
                assertTrue(resp.headers().contains("Quux"));
                assertTrue(resp.headers().contains("quux"));
                assertTrue(resp.headers().contains("qUUX"));
                testComplete();
            }));
            req.putHeader("Foo", "foo");
            assertEquals("foo", req.headers().get("Foo"));
            assertEquals("foo", req.headers().get("foo"));
            assertEquals("foo", req.headers().get("fOO"));
            assertTrue(req.headers().contains("Foo"));
            assertTrue(req.headers().contains("foo"));
            assertTrue(req.headers().contains("fOO"));
            req.end();
        }));
        await();
    }

    @Test
    public void testServerActualPortWhenSet() {
        server.requestHandler(( request) -> {
            request.response().end("hello");
        }).listen(( ar) -> {
            assertEquals(ar.result().actualPort(), HttpTestBase.DEFAULT_HTTP_PORT);
            vertx.createHttpClient(createBaseClientOptions()).getNow(ar.result().actualPort(), HttpTestBase.DEFAULT_HTTP_HOST, "/", onSuccess(( response) -> {
                assertEquals(response.statusCode(), 200);
                response.bodyHandler(( body) -> {
                    assertEquals(body.toString("UTF-8"), "hello");
                    testComplete();
                });
            }));
        });
        await();
    }

    @Test
    public void testServerActualPortWhenZero() {
        server = vertx.createHttpServer(setHost(HttpTestBase.DEFAULT_HTTP_HOST));
        server.requestHandler(( request) -> {
            request.response().end("hello");
        }).listen(( ar) -> {
            assertTrue(((ar.result().actualPort()) != 0));
            vertx.createHttpClient(createBaseClientOptions()).getNow(ar.result().actualPort(), HttpTestBase.DEFAULT_HTTP_HOST, "/", onSuccess(( response) -> {
                assertEquals(response.statusCode(), 200);
                response.bodyHandler(( body) -> {
                    assertEquals(body.toString("UTF-8"), "hello");
                    testComplete();
                });
            }));
        });
        await();
    }

    @Test
    public void testServerActualPortWhenZeroPassedInListen() {
        server = vertx.createHttpServer(setHost(HttpTestBase.DEFAULT_HTTP_HOST));
        server.requestHandler(( request) -> {
            request.response().end("hello");
        }).listen(0, ( ar) -> {
            assertTrue(((ar.result().actualPort()) != 0));
            vertx.createHttpClient(createBaseClientOptions()).getNow(ar.result().actualPort(), HttpTestBase.DEFAULT_HTTP_HOST, "/", onSuccess(( response) -> {
                assertEquals(response.statusCode(), 200);
                response.bodyHandler(( body) -> {
                    assertEquals(body.toString("UTF-8"), "hello");
                    testComplete();
                });
            }));
        });
        await();
    }

    @Test
    public void testRequestNPE() {
        String uri = "/some-uri?foo=bar";
        TestUtils.assertNullPointerException(() -> client.request(GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, uri, null));
        TestUtils.assertNullPointerException(() -> client.request(((HttpMethod) (null)), HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, uri, ( resp) -> {
        }));
        TestUtils.assertNullPointerException(() -> client.requestAbs(((HttpMethod) (null)), "http://someuri", ( resp) -> {
        }));
        TestUtils.assertNullPointerException(() -> client.request(GET, 8080, "localhost", "/somepath", null));
        TestUtils.assertNullPointerException(() -> client.request(((HttpMethod) (null)), 8080, "localhost", "/somepath", ( resp) -> {
        }));
        TestUtils.assertNullPointerException(() -> client.request(GET, 8080, null, "/somepath", ( resp) -> {
        }));
        TestUtils.assertNullPointerException(() -> client.request(GET, 8080, "localhost", null, ( resp) -> {
        }));
    }

    @Test
    public void testInvalidAbsoluteURI() {
        try {
            client.requestAbs(GET, "ijdijwidjqwoijd192d192192ej12d", ( resp) -> {
            }).end();
            fail("Should throw exception");
        } catch (VertxException e) {
            // OK
        }
    }

    @Test
    public void testPutHeadersOnRequest() {
        server.requestHandler(( req) -> {
            assertEquals("bar", req.headers().get("foo"));
            assertEquals("bar", req.getHeader("foo"));
            req.response().end();
        });
        server.listen(onSuccess(( server) -> {
            client.request(HttpMethod.GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, onSuccess(( resp) -> {
                assertEquals(200, resp.statusCode());
                testComplete();
            })).putHeader("foo", "bar").end();
        }));
        await();
    }

    @Test
    public void testPutHeaderReplacesPreviousHeaders() throws Exception {
        server.requestHandler(( req) -> req.response().putHeader("Location", "http://example1.org").putHeader("location", "http://example2.org").end());
        server.listen(onSuccess(( server) -> {
            client.request(HttpMethod.GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, onSuccess(( resp) -> {
                assertEquals(singletonList("http://example2.org"), resp.headers().getAll("LocatioN"));
                testComplete();
            })).end();
        }));
        await();
    }

    @Test
    public void testSimpleGET() {
        String uri = "/some-uri?foo=bar";
        testSimpleRequest(uri, GET, ( resp) -> testComplete());
    }

    @Test
    public void testSimplePUT() {
        String uri = "/some-uri?foo=bar";
        testSimpleRequest(uri, PUT, ( resp) -> testComplete());
    }

    @Test
    public void testSimplePOST() {
        String uri = "/some-uri?foo=bar";
        testSimpleRequest(uri, POST, ( resp) -> testComplete());
    }

    @Test
    public void testSimpleDELETE() {
        String uri = "/some-uri?foo=bar";
        testSimpleRequest(uri, DELETE, ( resp) -> testComplete());
    }

    @Test
    public void testSimpleHEAD() {
        String uri = "/some-uri?foo=bar";
        testSimpleRequest(uri, HEAD, ( resp) -> testComplete());
    }

    @Test
    public void testSimpleTRACE() {
        String uri = "/some-uri?foo=bar";
        testSimpleRequest(uri, TRACE, ( resp) -> testComplete());
    }

    @Test
    public void testSimpleCONNECT() {
        String uri = "/some-uri?foo=bar";
        testSimpleRequest(uri, CONNECT, ( resp) -> testComplete());
    }

    @Test
    public void testSimpleOPTIONS() {
        String uri = "/some-uri?foo=bar";
        testSimpleRequest(uri, OPTIONS, ( resp) -> testComplete());
    }

    @Test
    public void testSimplePATCH() {
        String uri = "/some-uri?foo=bar";
        testSimpleRequest(uri, PATCH, ( resp) -> testComplete());
    }

    @Test
    public void testSimpleGETAbsolute() {
        String uri = "/some-uri?foo=bar";
        testSimpleRequest(uri, GET, true, ( resp) -> testComplete());
    }

    @Test
    public void testEmptyPathGETAbsolute() {
        String uri = "";
        testSimpleRequest(uri, GET, true, ( resp) -> testComplete());
    }

    @Test
    public void testNoPathButQueryGETAbsolute() {
        String uri = "?foo=bar";
        testSimpleRequest(uri, GET, true, ( resp) -> testComplete());
    }

    @Test
    public void testSimplePUTAbsolute() {
        String uri = "/some-uri?foo=bar";
        testSimpleRequest(uri, PUT, true, ( resp) -> testComplete());
    }

    @Test
    public void testSimplePOSTAbsolute() {
        String uri = "/some-uri?foo=bar";
        testSimpleRequest(uri, POST, true, ( resp) -> testComplete());
    }

    @Test
    public void testSimpleDELETEAbsolute() {
        String uri = "/some-uri?foo=bar";
        testSimpleRequest(uri, DELETE, true, ( resp) -> testComplete());
    }

    @Test
    public void testSimpleHEADAbsolute() {
        String uri = "/some-uri?foo=bar";
        testSimpleRequest(uri, HEAD, true, ( resp) -> testComplete());
    }

    @Test
    public void testSimpleTRACEAbsolute() {
        String uri = "/some-uri?foo=bar";
        testSimpleRequest(uri, TRACE, true, ( resp) -> testComplete());
    }

    @Test
    public void testSimpleCONNECTAbsolute() {
        String uri = "/some-uri?foo=bar";
        testSimpleRequest(uri, CONNECT, true, ( resp) -> testComplete());
    }

    @Test
    public void testSimpleOPTIONSAbsolute() {
        String uri = "/some-uri?foo=bar";
        testSimpleRequest(uri, OPTIONS, true, ( resp) -> testComplete());
    }

    @Test
    public void testSimplePATCHAbsolute() {
        String uri = "/some-uri?foo=bar";
        testSimpleRequest(uri, PATCH, true, ( resp) -> testComplete());
    }

    @Test
    public void testServerChaining() {
        server.requestHandler(( req) -> {
            assertTrue(((req.response().setChunked(true)) == (req.response())));
            assertTrue(((req.response().write("foo", "UTF-8")) == (req.response())));
            assertTrue(((req.response().write("foo")) == (req.response())));
            testComplete();
        });
        server.listen(onSuccess(( server) -> {
            client.request(HttpMethod.PUT, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, noOpHandler()).end();
        }));
        await();
    }

    @Test
    public void testServerChainingSendFile() throws Exception {
        File file = setupFile("test-server-chaining.dat", "blah");
        server.requestHandler(( req) -> {
            assertTrue(((req.response().sendFile(file.getAbsolutePath())) == (req.response())));
            assertTrue(req.response().ended());
            file.delete();
            testComplete();
        });
        server.listen(onSuccess(( server) -> {
            client.request(HttpMethod.PUT, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, noOpHandler()).end();
        }));
        await();
    }

    @Test
    public void testResponseEndHandlers1() {
        waitFor(2);
        AtomicInteger cnt = new AtomicInteger();
        server.requestHandler(( req) -> {
            req.response().headersEndHandler(( v) -> {
                // Insert another header
                req.response().putHeader("extraheader", "wibble");
                assertEquals(0, cnt.getAndIncrement());
            });
            req.response().bodyEndHandler(( v) -> {
                assertEquals(0, req.response().bytesWritten());
                assertEquals(1, cnt.getAndIncrement());
                complete();
            });
            req.response().end();
        }).listen(onSuccess(( server) -> {
            client.request(HttpMethod.GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, "/", onSuccess(( res) -> {
                assertEquals(200, res.statusCode());
                assertEquals("wibble", res.headers().get("extraheader"));
                complete();
            })).end();
        }));
        await();
    }

    @Test
    public void testResponseEndHandlers2() {
        waitFor(2);
        AtomicInteger cnt = new AtomicInteger();
        String content = "blah";
        server.requestHandler(( req) -> {
            req.response().headersEndHandler(( v) -> {
                // Insert another header
                req.response().putHeader("extraheader", "wibble");
                assertEquals(0, cnt.getAndIncrement());
            });
            req.response().bodyEndHandler(( v) -> {
                assertEquals(content.length(), req.response().bytesWritten());
                assertEquals(1, cnt.getAndIncrement());
                complete();
            });
            req.response().end(content);
        }).listen(onSuccess(( server) -> {
            client.request(HttpMethod.GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, "/", onSuccess(( res) -> {
                assertEquals(200, res.statusCode());
                assertEquals("wibble", res.headers().get("extraheader"));
                res.bodyHandler(( buff) -> {
                    assertEquals(Buffer.buffer(content), buff);
                    complete();
                });
            })).end();
        }));
        await();
    }

    @Test
    public void testResponseEndHandlersChunkedResponse() {
        waitFor(2);
        AtomicInteger cnt = new AtomicInteger();
        String chunk = "blah";
        int numChunks = 6;
        StringBuilder content = new StringBuilder(((chunk.length()) * numChunks));
        IntStream.range(0, numChunks).forEach(( i) -> content.append(chunk));
        server.requestHandler(( req) -> {
            req.response().headersEndHandler(( v) -> {
                // Insert another header
                req.response().putHeader("extraheader", "wibble");
                assertEquals(0, cnt.getAndIncrement());
            });
            req.response().bodyEndHandler(( v) -> {
                assertEquals(content.length(), req.response().bytesWritten());
                assertEquals(1, cnt.getAndIncrement());
                complete();
            });
            req.response().setChunked(true);
            // note that we have a -1 here because the last chunk is written via end(chunk)
            IntStream.range(0, (numChunks - 1)).forEach(( x) -> req.response().write(chunk));
            // End with a chunk to ensure size is correctly calculated
            req.response().end(chunk);
        }).listen(onSuccess(( server) -> {
            client.request(HttpMethod.GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, "/", onSuccess(( res) -> {
                assertEquals(200, res.statusCode());
                assertEquals("wibble", res.headers().get("extraheader"));
                res.bodyHandler(( buff) -> {
                    assertEquals(Buffer.buffer(content.toString()), buff);
                    complete();
                });
            })).end();
        }));
        await();
    }

    @Test
    public void testResponseEndHandlersSendFile() throws Exception {
        waitFor(2);
        AtomicInteger cnt = new AtomicInteger();
        String content = "iqdioqwdqwiojqwijdwqd";
        File toSend = setupFile("somefile.txt", content);
        server.requestHandler(( req) -> {
            req.response().headersEndHandler(( v) -> {
                // Insert another header
                req.response().putHeader("extraheader", "wibble");
                assertEquals(0, cnt.getAndIncrement());
            });
            req.response().bodyEndHandler(( v) -> {
                assertEquals(content.length(), req.response().bytesWritten());
                assertEquals(1, cnt.getAndIncrement());
                complete();
            });
            req.response().sendFile(toSend.getAbsolutePath());
        }).listen(onSuccess(( server) -> {
            client.request(HttpMethod.GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, "/", onSuccess(( res) -> {
                assertEquals(200, res.statusCode());
                assertEquals("wibble", res.headers().get("extraheader"));
                res.bodyHandler(( buff) -> {
                    assertEquals(Buffer.buffer(content), buff);
                    complete();
                });
            })).end();
        }));
        await();
    }

    @Test
    public void testAbsoluteURI() {
        testURIAndPath((("http://localhost:" + (HttpTestBase.DEFAULT_HTTP_PORT)) + "/this/is/a/path/foo.html"), "/this/is/a/path/foo.html");
    }

    @Test
    public void testRelativeURI() {
        testURIAndPath("/this/is/a/path/foo.html", "/this/is/a/path/foo.html");
    }

    @Test
    public void testAbsoluteURIWithHttpSchemaInQuery() {
        testURIAndPath((("http://localhost:" + (HttpTestBase.DEFAULT_HTTP_PORT)) + "/correct/path?url=http://localhost:8008/wrong/path"), "/correct/path");
    }

    @Test
    public void testRelativeURIWithHttpSchemaInQuery() {
        testURIAndPath("/correct/path?url=http://localhost:8008/wrong/path", "/correct/path");
    }

    @Test
    public void testAbsoluteURIEmptyPath() {
        testURIAndPath((("http://localhost:" + (HttpTestBase.DEFAULT_HTTP_PORT)) + "/"), "/");
    }

    @Test
    public void testParamUmlauteDecoding() throws UnsupportedEncodingException {
        testParamDecoding("\u00e4\u00fc\u00f6");
    }

    @Test
    public void testParamPlusDecoding() throws UnsupportedEncodingException {
        testParamDecoding("+");
    }

    @Test
    public void testParamPercentDecoding() throws UnsupportedEncodingException {
        testParamDecoding("%");
    }

    @Test
    public void testParamSpaceDecoding() throws UnsupportedEncodingException {
        testParamDecoding(" ");
    }

    @Test
    public void testParamNormalDecoding() throws UnsupportedEncodingException {
        testParamDecoding("hello");
    }

    @Test
    public void testParamAltogetherDecoding() throws UnsupportedEncodingException {
        testParamDecoding("\u00e4\u00fc\u00f6+% hello");
    }

    @Test
    public void testParamsAmpersand() {
        testParams('&');
    }

    @Test
    public void testParamsSemiColon() {
        testParams(';');
    }

    @Test
    public void testNoParams() {
        server.requestHandler(( req) -> {
            assertNull(req.query());
            assertTrue(req.params().isEmpty());
            req.response().end();
        });
        server.listen(onSuccess(( server) -> {
            client.request(HttpMethod.GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, ( resp) -> testComplete()).end();
        }));
        await();
    }

    @Test
    public void testDefaultRequestHeaders() {
        server.requestHandler(( req) -> {
            if ((req.version()) == HttpVersion.HTTP_1_1) {
                assertEquals(1, req.headers().size());
                assertEquals(("localhost:" + (HttpTestBase.DEFAULT_HTTP_PORT)), req.headers().get("host"));
            } else {
                assertEquals(4, req.headers().size());
                assertEquals("https", req.headers().get(":scheme"));
                assertEquals("GET", req.headers().get(":method"));
                assertEquals("some-uri", req.headers().get(":path"));
                assertEquals(("localhost:" + (HttpTestBase.DEFAULT_HTTP_PORT)), req.headers().get(":authority"));
            }
            req.response().end();
        });
        server.listen(onSuccess(( server) -> {
            client.request(HttpMethod.GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, ( resp) -> testComplete()).end();
        }));
        await();
    }

    @Test
    public void testRequestHeadersWithCharSequence() {
        HashMap<CharSequence, String> headers = new HashMap<>();
        headers.put(TEXT_HTML, "text/html");
        headers.put(USER_AGENT, "User-Agent");
        headers.put(APPLICATION_X_WWW_FORM_URLENCODED, "application/x-www-form-urlencoded");
        server.requestHandler(( req) -> {
            assertTrue(((headers.size()) < (req.headers().size())));
            headers.forEach(( k, v) -> assertEquals(v, req.headers().get(k)));
            headers.forEach(( k, v) -> assertEquals(v, req.getHeader(k)));
            req.response().end();
        });
        server.listen(onSuccess(( server) -> {
            HttpClientRequest req = client.request(HttpMethod.GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, ( resp) -> testComplete());
            headers.forEach(( k, v) -> req.headers().add(k, v));
            req.end();
        }));
        await();
    }

    @Test
    public void testRequestHeadersPutAll() {
        testRequestHeaders(false);
    }

    @Test
    public void testRequestHeadersIndividually() {
        testRequestHeaders(true);
    }

    @Test
    public void testResponseHeadersPutAll() {
        testResponseHeaders(false);
    }

    @Test
    public void testResponseHeadersIndividually() {
        testResponseHeaders(true);
    }

    @Test
    public void testResponseHeadersWithCharSequence() {
        HashMap<CharSequence, String> headers = new HashMap<>();
        headers.put(TEXT_HTML, "text/html");
        headers.put(USER_AGENT, "User-Agent");
        headers.put(APPLICATION_X_WWW_FORM_URLENCODED, "application/x-www-form-urlencoded");
        server.requestHandler(( req) -> {
            headers.forEach(( k, v) -> req.response().headers().add(k, v));
            req.response().end();
        });
        server.listen(onSuccess(( server) -> {
            client.request(HttpMethod.GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, onSuccess(( resp) -> {
                assertTrue(((headers.size()) < (resp.headers().size())));
                headers.forEach(( k, v) -> assertEquals(v, resp.headers().get(k)));
                headers.forEach(( k, v) -> assertEquals(v, resp.getHeader(k)));
                testComplete();
            })).end();
        }));
        await();
    }

    @Test
    public void testResponseMultipleSetCookieInHeader() {
        testResponseMultipleSetCookie(true, false);
    }

    @Test
    public void testResponseMultipleSetCookieInTrailer() {
        testResponseMultipleSetCookie(false, true);
    }

    @Test
    public void testResponseMultipleSetCookieInHeaderAndTrailer() {
        testResponseMultipleSetCookie(true, true);
    }

    @Test
    public void testUseRequestAfterComplete() {
        server.requestHandler(noOpHandler());
        server.listen(onSuccess(( server) -> {
            HttpClientRequest req = client.request(HttpMethod.POST, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, noOpHandler());
            req.end();
            Buffer buff = Buffer.buffer();
            assertIllegalStateException(() -> req.end());
            assertIllegalStateException(() -> req.continueHandler(noOpHandler()));
            assertIllegalStateException(() -> req.drainHandler(noOpHandler()));
            assertIllegalStateException(() -> req.end("foo"));
            assertIllegalStateException(() -> req.end(buff));
            assertIllegalStateException(() -> req.end("foo", "UTF-8"));
            assertIllegalStateException(() -> req.sendHead());
            assertIllegalStateException(() -> req.setChunked(false));
            assertIllegalStateException(() -> req.setWriteQueueMaxSize(123));
            assertIllegalStateException(() -> req.write(buff));
            assertIllegalStateException(() -> req.write("foo"));
            assertIllegalStateException(() -> req.write("foo", "UTF-8"));
            assertIllegalStateException(() -> req.write(buff));
            assertIllegalStateException(() -> req.writeQueueFull());
            testComplete();
        }));
        await();
    }

    @Test
    public void testRequestBodyBufferAtEnd() {
        Buffer body = TestUtils.randomBuffer(1000);
        server.requestHandler(( req) -> req.bodyHandler(( buffer) -> {
            assertEquals(body, buffer);
            req.response().end();
        }));
        server.listen(onSuccess(( server) -> {
            client.request(HttpMethod.POST, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, ( resp) -> testComplete()).end(body);
        }));
        await();
    }

    @Test
    public void testRequestBodyStringDefaultEncodingAtEnd() {
        testRequestBodyStringAtEnd(null);
    }

    @Test
    public void testRequestBodyStringUTF8AtEnd() {
        testRequestBodyStringAtEnd("UTF-8");
    }

    @Test
    public void testRequestBodyStringUTF16AtEnd() {
        testRequestBodyStringAtEnd("UTF-16");
    }

    @Test
    public void testRequestBodyWriteChunked() {
        testRequestBodyWrite(true);
    }

    @Test
    public void testRequestBodyWriteNonChunked() {
        testRequestBodyWrite(false);
    }

    @Test
    public void testRequestBodyWriteStringChunkedDefaultEncoding() {
        testRequestBodyWriteString(true, null);
    }

    @Test
    public void testRequestBodyWriteStringChunkedUTF8() {
        testRequestBodyWriteString(true, "UTF-8");
    }

    @Test
    public void testRequestBodyWriteStringChunkedUTF16() {
        testRequestBodyWriteString(true, "UTF-16");
    }

    @Test
    public void testRequestBodyWriteStringNonChunkedDefaultEncoding() {
        testRequestBodyWriteString(false, null);
    }

    @Test
    public void testRequestBodyWriteStringNonChunkedUTF8() {
        testRequestBodyWriteString(false, "UTF-8");
    }

    @Test
    public void testRequestBodyWriteStringNonChunkedUTF16() {
        testRequestBodyWriteString(false, "UTF-16");
    }

    @Test
    public void testRequestWrite() {
        int times = 3;
        Buffer chunk = TestUtils.randomBuffer(1000);
        server.requestHandler(( req) -> {
            req.bodyHandler(( buff) -> {
                Buffer expected = Buffer.buffer();
                for (int i = 0; i < times; i++) {
                    expected.appendBuffer(chunk);
                }
                assertEquals(expected, buff);
                testComplete();
            });
        });
        server.listen(onSuccess(( s) -> {
            HttpClientRequest req = client.request(HttpMethod.POST, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, noOpHandler());
            req.setChunked(true);
            int padding = 5;
            for (int i = 0; i < times; i++) {
                Buffer paddedChunk = TestUtils.leftPad(padding, chunk);
                assertEquals(paddedChunk.getByteBuf().readerIndex(), padding);
                req.write(paddedChunk);
            }
            req.end();
        }));
        await();
    }

    @Test
    public void testConnectWithoutResponseHandler() throws Exception {
        try {
            client.request(GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI).end();
            fail();
        } catch (IllegalStateException expected) {
        }
        try {
            client.request(GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI).end("whatever");
            fail();
        } catch (IllegalStateException expected) {
        }
        try {
            client.request(GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI).end("whatever", "UTF-8");
            fail();
        } catch (IllegalStateException expected) {
        }
        try {
            client.request(GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI).end(Buffer.buffer("whatever"));
            fail();
        } catch (IllegalStateException expected) {
        }
        try {
            client.request(GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI).sendHead();
            fail();
        } catch (IllegalStateException expected) {
        }
        try {
            client.request(GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI).write(Buffer.buffer("whatever"));
            fail();
        } catch (IllegalStateException expected) {
        }
        try {
            client.request(GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI).write("whatever");
            fail();
        } catch (IllegalStateException expected) {
        }
        try {
            client.request(GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI).write("whatever", "UTF-8");
            fail();
        } catch (IllegalStateException expected) {
        }
    }

    @Test
    public void testClientExceptionHandlerCalledWhenFailingToConnect() throws Exception {
        client.request(GET, 9998, "255.255.255.255", HttpTestBase.DEFAULT_TEST_URI, onFailure(( err) -> testComplete())).exceptionHandler(( error) -> fail("Exception handler should not be called")).end();
        await();
    }

    @Test
    public void testClientExceptionHandlerCalledWhenServerTerminatesConnection() throws Exception {
        int numReqs = 10;
        CountDownLatch latch = new CountDownLatch(numReqs);
        server.requestHandler(( request) -> {
            request.response().close();
        }).listen(HttpTestBase.DEFAULT_HTTP_PORT, onSuccess(( s) -> {
            // Exception handler should be called for any requests in the pipeline if connection is closed
            for (int i = 0; i < numReqs; i++) {
                client.request(HttpMethod.GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, onFailure(( err) -> {
                    latch.countDown();
                })).exceptionHandler(( error) -> fail("Exception handler should not be called")).end();
            }
        }));
        awaitLatch(latch);
    }

    @Test
    public void testClientExceptionHandlerCalledWhenServerTerminatesConnectionAfterPartialResponse() throws Exception {
        server.requestHandler(( request) -> {
            // Write partial response then close connection before completing it
            request.response().setChunked(true).write("foo").close();
        }).listen(HttpTestBase.DEFAULT_HTTP_PORT, onSuccess(( s) -> {
            // Exception handler should be called for any requests in the pipeline if connection is closed
            client.request(HttpMethod.GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, onSuccess(( resp) -> resp.exceptionHandler(( t) -> testComplete()))).exceptionHandler(( error) -> fail()).end();
        }));
        await();
    }

    @Test
    public void testContextExceptionHandlerCalledWhenExceptionOnDataHandler() throws Exception {
        server.requestHandler(( request) -> {
            request.response().end("foo");
        }).listen(HttpTestBase.DEFAULT_HTTP_PORT, onSuccess(( s) -> {
            // Exception handler should be called for any exceptions in the data handler
            client.request(HttpMethod.GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, onSuccess(( resp) -> {
                RuntimeException cause = new RuntimeException("should be caught");
                resp.exceptionHandler(( err) -> {
                    if (err == cause) {
                        testComplete();
                    }
                });
                resp.handler(( data) -> {
                    throw cause;
                });
            })).end();
        }));
        await();
    }

    @Test
    public void testClientExceptionHandlerCalledWhenExceptionOnBodyHandler() throws Exception {
        server.requestHandler(( request) -> {
            request.response().end("foo");
        }).listen(HttpTestBase.DEFAULT_HTTP_PORT, onSuccess(( s) -> {
            // Exception handler should be called for any exceptions in the data handler
            client.request(HttpMethod.GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, onSuccess(( resp) -> {
                RuntimeException cause = new RuntimeException("should be caught");
                resp.exceptionHandler(( err) -> {
                    if (err == cause) {
                        testComplete();
                    }
                });
                resp.bodyHandler(( data) -> {
                    throw cause;
                });
            })).end();
        }));
        await();
    }

    @Test
    public void testNoExceptionHandlerCalledWhenResponseEnded() throws Exception {
        server.requestHandler(( req) -> {
            HttpServerResponse resp = req.response();
            req.exceptionHandler(this::fail);
            resp.exceptionHandler(( err) -> {
                err.printStackTrace();
            });
            resp.end();
        }).listen(HttpTestBase.DEFAULT_HTTP_PORT, onSuccess(( s) -> {
            client.get(HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, onSuccess(( resp) -> {
                resp.endHandler(( v) -> {
                    vertx.setTimer(100, ( tid) -> testComplete());
                });
                resp.exceptionHandler(( t) -> {
                    fail("Should not be called");
                });
            })).exceptionHandler(( t) -> {
                fail("Should not be called");
            }).end();
        }));
        await();
    }

    @Test
    public void testServerExceptionHandlerOnClose() {
        vertx.createHttpServer().requestHandler(( req) -> {
            HttpServerResponse resp = req.response();
            AtomicInteger reqExceptionHandlerCount = new AtomicInteger();
            AtomicInteger respExceptionHandlerCount = new AtomicInteger();
            AtomicInteger respEndHandlerCount = new AtomicInteger();
            req.exceptionHandler(( err) -> {
                assertEquals(1, reqExceptionHandlerCount.incrementAndGet());
                assertEquals(1, respExceptionHandlerCount.get());
                assertEquals(1, respEndHandlerCount.get());
                assertTrue(resp.closed());
                assertFalse(resp.ended());
                try {
                    resp.end();
                } catch ( ignore) {
                    // Expected
                }
            });
            resp.exceptionHandler(( err) -> {
                assertEquals(0, reqExceptionHandlerCount.get());
                assertEquals(1, respExceptionHandlerCount.incrementAndGet());
                assertEquals(0, respEndHandlerCount.get());
            });
            resp.endHandler(( v) -> {
                assertEquals(0, reqExceptionHandlerCount.get());
                assertEquals(1, respExceptionHandlerCount.get());
                assertEquals(1, respEndHandlerCount.incrementAndGet());
            });
            req.connection().closeHandler(( v) -> {
                assertEquals(1, reqExceptionHandlerCount.get());
                assertEquals(1, respExceptionHandlerCount.get());
                assertEquals(1, respEndHandlerCount.get());
                testComplete();
            });
        }).listen(HttpTestBase.DEFAULT_HTTP_PORT, ( ar) -> {
            HttpClient client = vertx.createHttpClient();
            HttpClientRequest req = client.put(HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, "/somerui", ( handler) -> {
            }).setChunked(true);
            req.sendHead(( v) -> {
                req.connection().close();
            });
        });
        await();
    }

    @Test
    public void testClientRequestExceptionHandlerCalledWhenConnectionClosed() throws Exception {
        server.requestHandler(( req) -> {
            req.handler(( buff) -> {
                req.connection().close();
            });
        });
        startServer();
        HttpClientRequest req = client.post(HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, "/somepath", onFailure(( err) -> {
        })).setChunked(true);
        req.exceptionHandler(( err) -> {
            testComplete();
        });
        req.write("chunk");
        await();
    }

    @Test
    public void testClientResponseExceptionHandlerCalledWhenConnectionClosed() throws Exception {
        AtomicReference<HttpConnection> conn = new AtomicReference<>();
        server.requestHandler(( req) -> {
            conn.set(req.connection());
            req.response().setChunked(true).write("chunk");
        });
        startServer();
        client.getNow(HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, "/somepath", onSuccess(( resp) -> {
            resp.handler(( buff) -> {
                conn.get().close();
            });
            resp.exceptionHandler(( err) -> {
                testComplete();
            });
        }));
        await();
    }

    @Test
    public void testDefaultStatus() {
        testStatusCode((-1), null);
    }

    @Test
    public void testDefaultOther() {
        // Doesn't really matter which one we choose
        testStatusCode(405, null);
    }

    @Test
    public void testOverrideStatusMessage() {
        testStatusCode(404, "some message");
    }

    @Test
    public void testOverrideDefaultStatusMessage() {
        testStatusCode((-1), "some other message");
    }

    @Test
    public void testResponseTrailersPutAll() {
        testResponseTrailers(false);
    }

    @Test
    public void testResponseTrailersPutIndividually() {
        testResponseTrailers(true);
    }

    @Test
    public void testResponseNoTrailers() {
        server.requestHandler(( req) -> {
            req.response().setChunked(true);
            req.response().end();
        });
        server.listen(onSuccess(( s) -> {
            client.request(HttpMethod.GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, onSuccess(( resp) -> {
                resp.endHandler(( v) -> {
                    assertTrue(resp.trailers().isEmpty());
                    testComplete();
                });
            })).end();
        }));
        await();
    }

    @Test
    public void testUseResponseAfterComplete() throws Exception {
        server.requestHandler(( req) -> {
            HttpServerResponse resp = req.response();
            assertFalse(resp.ended());
            resp.end();
            assertTrue(resp.ended());
            checkHttpServerResponse(resp);
            testComplete();
        });
        startServer();
        client.getNow(HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, noOpHandler());
        await();
    }

    @Test
    public void testResponseBodyBufferAtEnd() {
        Buffer body = TestUtils.randomBuffer(1000);
        server.requestHandler(( req) -> {
            req.response().end(body);
        });
        server.listen(onSuccess(( s) -> {
            client.request(HttpMethod.GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, onSuccess(( resp) -> {
                resp.bodyHandler(( buff) -> {
                    assertEquals(body, buff);
                    testComplete();
                });
            })).end();
        }));
        await();
    }

    @Test
    public void testResponseBodyWriteChunked() {
        testResponseBodyWrite(true);
    }

    @Test
    public void testResponseBodyWriteNonChunked() {
        testResponseBodyWrite(false);
    }

    @Test
    public void testResponseBodyWriteStringChunkedDefaultEncoding() {
        testResponseBodyWriteString(true, null);
    }

    @Test
    public void testResponseBodyWriteStringChunkedUTF8() {
        testResponseBodyWriteString(true, "UTF-8");
    }

    @Test
    public void testResponseBodyWriteStringChunkedUTF16() {
        testResponseBodyWriteString(true, "UTF-16");
    }

    @Test
    public void testResponseBodyWriteStringNonChunkedDefaultEncoding() {
        testResponseBodyWriteString(false, null);
    }

    @Test
    public void testResponseBodyWriteStringNonChunkedUTF8() {
        testResponseBodyWriteString(false, "UTF-8");
    }

    @Test
    public void testResponseBodyWriteStringNonChunkedUTF16() {
        testResponseBodyWriteString(false, "UTF-16");
    }

    @Test
    public void testResponseWrite() {
        Buffer body = TestUtils.randomBuffer(1000);
        server.requestHandler(( req) -> {
            req.response().setChunked(true);
            req.response().write(body);
            req.response().end();
        });
        server.listen(onSuccess(( s) -> {
            client.request(HttpMethod.POST, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, onSuccess(( resp) -> {
                resp.bodyHandler(( buff) -> {
                    assertEquals(body, buff);
                    testComplete();
                });
            })).end();
        }));
        await();
    }

    @Test
    public void testSendFile() throws Exception {
        String content = TestUtils.randomUnicodeString(10000);
        sendFile("test-send-file.html", content, false, ( handler) -> client.get(HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, handler));
    }

    @Test
    public void testSendFileWithHandler() throws Exception {
        String content = TestUtils.randomUnicodeString(10000);
        sendFile("test-send-file.html", content, true, ( handler) -> client.get(HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, handler));
    }

    @Test
    public void testSendFileWithConnectionCloseHeader() throws Exception {
        String content = TestUtils.randomUnicodeString(((1024 * 1024) * 2));
        sendFile("test-send-file.html", content, false, ( handler) -> client.get(HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, handler).putHeader(CONNECTION, "close"));
    }

    @Test
    public void testSendNonExistingFile() throws Exception {
        server.requestHandler(( req) -> {
            final Context ctx = vertx.getOrCreateContext();
            req.response().sendFile("/not/existing/path", ( event) -> {
                assertEquals(ctx, vertx.getOrCreateContext());
                if (event.failed()) {
                    req.response().end("failed");
                }
            });
        });
        server.listen(onSuccess(( s) -> {
            client.request(HttpMethod.GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, onSuccess(( resp) -> {
                resp.bodyHandler(( buff) -> {
                    assertEquals("failed", buff.toString());
                    testComplete();
                });
            })).end();
        }));
        await();
    }

    @Test
    public void testSendFileOverrideHeaders() throws Exception {
        String content = TestUtils.randomUnicodeString(10000);
        File file = setupFile("test-send-file.html", content);
        server.requestHandler(( req) -> {
            req.response().putHeader("Content-Type", "wibble");
            req.response().sendFile(file.getAbsolutePath());
        });
        server.listen(onSuccess(( s) -> {
            client.request(HttpMethod.GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, onSuccess(( resp) -> {
                assertEquals(file.length(), Long.parseLong(resp.headers().get("content-length")));
                assertEquals("wibble", resp.headers().get("content-type"));
                resp.bodyHandler(( buff) -> {
                    assertEquals(content, buff.toString());
                    file.delete();
                    testComplete();
                });
            })).end();
        }));
        await();
    }

    @Test
    public void testSendFileNotFound() throws Exception {
        server.requestHandler(( req) -> {
            req.response().putHeader("Content-Type", "wibble");
            req.response().sendFile("nosuchfile.html");
        });
        server.listen(onSuccess(( s) -> {
            client.request(HttpMethod.GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, onFailure(( err) -> {
            })).end();
            vertx.setTimer(100, ( tid) -> testComplete());
        }));
        await();
    }

    @Test
    public void testSendFileNotFoundWithHandler() throws Exception {
        server.requestHandler(( req) -> {
            req.response().putHeader("Content-Type", "wibble");
            req.response().sendFile("nosuchfile.html", onFailure(( t) -> {
                assertTrue((t instanceof FileNotFoundException));
                testComplete();
            }));
        });
        server.listen(onSuccess(( s) -> {
            client.request(HttpMethod.GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, onFailure(( resp) -> {
            })).end();
        }));
        await();
    }

    @Test
    public void testSendFileDirectoryWithHandler() throws Exception {
        File dir = testFolder.newFolder();
        server.requestHandler(( req) -> {
            req.response().putHeader("Content-Type", "wibble");
            req.response().sendFile(dir.getAbsolutePath(), onFailure(( t) -> {
                assertTrue((t instanceof FileNotFoundException));
                testComplete();
            }));
        });
        server.listen(onSuccess(( s) -> {
            client.request(HttpMethod.GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, onFailure(( err) -> {
            })).end();
        }));
        await();
    }

    @Test
    public void testSendOpenRangeFileFromClasspath() {
        vertx.createHttpServer(new HttpServerOptions().setPort(8080)).requestHandler(( res) -> {
            res.response().sendFile("webroot/somefile.html", 6);
        }).listen(onSuccess(( res) -> {
            vertx.createHttpClient(new HttpClientOptions()).request(HttpMethod.GET, 8080, "localhost", "/", onSuccess(( resp) -> {
                resp.bodyHandler(( buff) -> {
                    assertTrue(buff.toString().startsWith("<body>blah</body></html>"));
                    testComplete();
                });
            })).end();
        }));
        await();
    }

    @Test
    public void testSendRangeFileFromClasspath() {
        vertx.createHttpServer(new HttpServerOptions().setPort(8080)).requestHandler(( res) -> {
            res.response().sendFile("webroot/somefile.html", 6, 6);
        }).listen(onSuccess(( res) -> {
            vertx.createHttpClient(new HttpClientOptions()).request(HttpMethod.GET, 8080, "localhost", "/", onSuccess(( resp) -> {
                resp.bodyHandler(( buff) -> {
                    assertEquals("<body>", buff.toString());
                    testComplete();
                });
            })).end();
        }));
        await();
    }

    @Test
    public void test100ContinueHandledAutomatically() throws Exception {
        Buffer toSend = TestUtils.randomBuffer(1000);
        server.requestHandler(( req) -> {
            req.bodyHandler(( data) -> {
                assertEquals(toSend, data);
                req.response().end();
            });
        });
        server.listen(onSuccess(( s) -> {
            HttpClientRequest req = client.request(HttpMethod.PUT, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, onSuccess(( resp) -> {
                resp.endHandler(( v) -> testComplete());
            }));
            req.headers().set("Expect", "100-continue");
            req.setChunked(true);
            req.continueHandler(( v) -> {
                req.write(toSend);
                req.end();
            });
            req.sendHead();
        }));
        await();
    }

    @Test
    public void test100ContinueHandledManually() throws Exception {
        server.close();
        server = vertx.createHttpServer(createBaseServerOptions().setHandle100ContinueAutomatically(false));
        Buffer toSend = TestUtils.randomBuffer(1000);
        server.requestHandler(( req) -> {
            assertEquals("100-continue", req.getHeader("expect"));
            req.response().writeContinue();
            req.bodyHandler(( data) -> {
                assertEquals(toSend, data);
                req.response().end();
            });
        });
        server.listen(onSuccess(( s) -> {
            HttpClientRequest req = client.request(HttpMethod.PUT, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, onSuccess(( resp) -> {
                resp.endHandler(( v) -> testComplete());
            }));
            req.headers().set("Expect", "100-continue");
            req.setChunked(true);
            req.continueHandler(( v) -> {
                req.write(toSend);
                req.end();
            });
            req.sendHead();
        }));
        await();
    }

    @Test
    public void test100ContinueRejectedManually() throws Exception {
        server.close();
        server = vertx.createHttpServer(createBaseServerOptions().setHandle100ContinueAutomatically(false));
        server.requestHandler(( req) -> {
            req.response().setStatusCode(405).end();
            req.bodyHandler(( data) -> {
                fail("body should not be received");
            });
        });
        server.listen(onSuccess(( s) -> {
            HttpClientRequest req = client.request(HttpMethod.PUT, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, onSuccess(( resp) -> {
                assertEquals(405, resp.statusCode());
                testComplete();
            }));
            req.headers().set("Expect", "100-continue");
            req.setChunked(true);
            req.continueHandler(( v) -> {
                fail("should not be called");
            });
            req.sendHead();
        }));
        await();
    }

    @Test
    public void testClientDrainHandler() {
        pausingServer(( resumeFuture) -> {
            HttpClientRequest req = client.request(GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, noOpHandler());
            req.setChunked(true);
            assertFalse(req.writeQueueFull());
            req.setWriteQueueMaxSize(1000);
            Buffer buff = TestUtils.randomBuffer(10000);
            vertx.setPeriodic(1, ( id) -> {
                req.write(buff);
                if (req.writeQueueFull()) {
                    vertx.cancelTimer(id);
                    req.drainHandler(( v) -> {
                        assertFalse(req.writeQueueFull());
                        testComplete();
                    });
                    // Tell the server to resume
                    resumeFuture.complete();
                }
            });
        });
        await();
    }

    @Test
    public void testClientExceptionHandlerCalledWhenExceptionOnDrainHandler() {
        pausingServer(( resumeFuture) -> {
            HttpClientRequest req = client.request(GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, noOpHandler());
            req.setChunked(true);
            assertFalse(req.writeQueueFull());
            req.setWriteQueueMaxSize(1000);
            Buffer buff = TestUtils.randomBuffer(10000);
            vertx.setPeriodic(1, ( id) -> {
                req.write(buff);
                if (req.writeQueueFull()) {
                    vertx.cancelTimer(id);
                    RuntimeException cause = new RuntimeException("error");
                    req.exceptionHandler(( err) -> {
                        // Called a second times when testComplete is called and close the http client
                        if (err == cause) {
                            testComplete();
                        }
                    });
                    req.drainHandler(( v) -> {
                        throw cause;
                    });
                    // Tell the server to resume
                    resumeFuture.complete();
                }
            });
        });
        await();
    }

    @Test
    public void testServerDrainHandler() {
        drainingServer(( resumeFuture) -> {
            client.request(GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, onSuccess(( resp) -> {
                resp.pause();
                resumeFuture.setHandler(( ar) -> resp.resume());
            })).end();
        });
        await();
    }

    @Test
    public void testConnectionErrorsGetReportedToHandlers() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(3);
        // This one should cause an error in the Client Exception handler, because it has no exception handler set specifically.
        HttpClientRequest req1 = client.request(GET, 9998, HttpTestBase.DEFAULT_HTTP_HOST, "someurl1", onFailure(( resp) -> {
            latch.countDown();
        }));
        req1.exceptionHandler(( t) -> {
            fail("Should not be called");
        });
        HttpClientRequest req2 = client.request(GET, 9998, HttpTestBase.DEFAULT_HTTP_HOST, "someurl2", onFailure(( resp) -> {
            latch.countDown();
        }));
        AtomicInteger req2Exceptions = new AtomicInteger();
        req2.exceptionHandler(( t) -> {
            assertEquals("More than one call to req2 exception handler was not expected", 1, req2Exceptions.incrementAndGet());
            latch.countDown();
        });
        req1.end();
        req2.sendHead();
        awaitLatch(latch);
        testComplete();
    }

    @Test
    public void testRequestTimesoutWhenIndicatedPeriodExpiresWithoutAResponseFromRemoteServer() {
        server.requestHandler(noOpHandler());// No response handler so timeout triggers

        AtomicBoolean failed = new AtomicBoolean();
        server.listen(onSuccess(( s) -> {
            HttpClientRequest req = client.request(HttpMethod.GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, onFailure(( t) -> {
                // Catch the first, the second is going to be a connection closed exception when the
                // server is shutdown on testComplete
                if (failed.compareAndSet(false, true)) {
                    assertTrue(("Expected to end with timeout exception but ended with other exception: " + t), (t instanceof TimeoutException));
                    testComplete();
                }
            }));
            req.setTimeout(1000);
            req.end();
        }));
        await();
    }

    @Test
    public void testRequestTimeoutCanceledWhenRequestHasAnOtherError() {
        AtomicReference<Throwable> exception = new AtomicReference<>();
        // There is no server running, should fail to connect
        client.request(GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, onFailure(exception::set)).setTimeout(800).end();
        vertx.setTimer(1500, ( id) -> {
            assertNotNull("Expected an exception to be set", exception.get());
            assertFalse(("Expected to not end with timeout exception, but did: " + (exception.get())), ((exception.get()) instanceof TimeoutException));
            testComplete();
        });
        await();
    }

    @Test
    public void testRequestTimeoutCanceledWhenRequestEndsNormally() {
        server.requestHandler(( req) -> req.response().end());
        server.listen(onSuccess(( s) -> {
            AtomicReference<Throwable> exception = new AtomicReference<>();
            // There is no server running, should fail to connect
            HttpClientRequest req = client.request(HttpMethod.GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, noOpHandler());
            req.exceptionHandler(exception::set);
            req.setTimeout(500);
            req.end();
            vertx.setTimer(1000, ( id) -> {
                assertNull("Did not expect any exception", exception.get());
                testComplete();
            });
        }));
        await();
    }

    @Test
    public void testHttpClientRequestTimeoutResetsTheConnection() throws Exception {
        waitFor(3);
        server.requestHandler(( req) -> {
            AtomicBoolean errored = new AtomicBoolean();
            req.exceptionHandler(( err) -> {
                if (errored.compareAndSet(false, true)) {
                    complete();
                }
            });
        });
        startServer();
        HttpClientRequest req = client.request(GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, onFailure(( err) -> {
            complete();
        }));
        AtomicBoolean errored = new AtomicBoolean();
        req.exceptionHandler(( err) -> {
            if (errored.compareAndSet(false, true)) {
                complete();
            }
        });
        CountDownLatch latch = new CountDownLatch(1);
        req.setChunked(true).sendHead(( version) -> latch.countDown());
        awaitLatch(latch);
        req.setTimeout(100);
        await();
    }

    @Test
    public void testConnectInvalidPort() {
        client.request(GET, 9998, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, onFailure(( err) -> testComplete())).exceptionHandler(( t) -> fail("Exception handler should not be called")).end();
        await();
    }

    @Test
    public void testConnectInvalidHost() {
        client.request(GET, 9998, "255.255.255.255", HttpTestBase.DEFAULT_TEST_URI, onFailure(( resp) -> testComplete())).exceptionHandler(( t) -> fail("Exception handler should not be called")).end();
        await();
    }

    @Test
    public void testSetHandlersAfterListening() throws Exception {
        server.requestHandler(noOpHandler());
        server.listen(onSuccess(( s) -> {
            assertIllegalStateException(() -> server.requestHandler(noOpHandler()));
            assertIllegalStateException(() -> server.websocketHandler(noOpHandler()));
            testComplete();
        }));
        await();
    }

    @Test
    public void testSetHandlersAfterListening2() throws Exception {
        server.requestHandler(noOpHandler());
        server.listen(onSuccess(( v) -> testComplete()));
        TestUtils.assertIllegalStateException(() -> server.requestHandler(noOpHandler()));
        TestUtils.assertIllegalStateException(() -> server.websocketHandler(noOpHandler()));
        await();
    }

    @Test
    public void testListenNoHandlers() throws Exception {
        TestUtils.assertIllegalStateException(() -> server.listen(( ar) -> {
        }));
    }

    @Test
    public void testListenNoHandlers2() throws Exception {
        TestUtils.assertIllegalStateException(() -> server.listen());
    }

    @Test
    public void testListenTwice() throws Exception {
        server.requestHandler(noOpHandler());
        server.listen(onSuccess(( v) -> testComplete()));
        TestUtils.assertIllegalStateException(() -> server.listen());
        await();
    }

    @Test
    public void testListenTwice2() throws Exception {
        server.requestHandler(noOpHandler());
        server.listen(( ar) -> {
            assertTrue(ar.succeeded());
            assertIllegalStateException(() -> server.listen());
            testComplete();
        });
        await();
    }

    @Test
    public void testHeadCanSetContentLength() {
        server.requestHandler(( req) -> {
            assertEquals(HttpMethod.HEAD, req.method());
            // Head never contains a body but it can contain a Content-Length header
            // Since headers from HEAD must correspond EXACTLY with corresponding headers for GET
            req.response().headers().set("Content-Length", String.valueOf(41));
            req.response().end();
        });
        server.listen(onSuccess(( s) -> {
            client.request(HttpMethod.HEAD, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, onSuccess(( resp) -> {
                assertEquals("41", resp.headers().get("Content-Length"));
                resp.endHandler(( v) -> testComplete());
            })).end();
        }));
        await();
    }

    @Test
    public void testHeadDoesNotSetAutomaticallySetContentLengthHeader() throws Exception {
        MultiMap respHeaders = checkEmptyHttpResponse(HEAD, 200, MultiMap.caseInsensitiveMultiMap());
        assertNull(respHeaders.get("content-length"));
        assertNull(respHeaders.get("transfer-encoding"));
    }

    @Test
    public void testHeadAllowsContentLengthHeader() throws Exception {
        MultiMap respHeaders = checkEmptyHttpResponse(HEAD, 200, MultiMap.caseInsensitiveMultiMap().set("content-length", "34"));
        assertEquals("34", respHeaders.get("content-length"));
        assertNull(respHeaders.get("transfer-encoding"));
    }

    @Test
    public void testHeadRemovesTransferEncodingHeader() throws Exception {
        MultiMap respHeaders = checkEmptyHttpResponse(HEAD, 200, MultiMap.caseInsensitiveMultiMap().set("transfer-encoding", "chunked"));
        assertNull(respHeaders.get("content-length"));
        assertNull(respHeaders.get("transfer-encoding"));
    }

    @Test
    public void testNoContentRemovesContentLengthHeader() throws Exception {
        MultiMap respHeaders = checkEmptyHttpResponse(GET, 204, MultiMap.caseInsensitiveMultiMap().set("content-length", "34"));
        assertNull(respHeaders.get("content-length"));
        assertNull(respHeaders.get("transfer-encoding"));
    }

    @Test
    public void testNoContentRemovesTransferEncodingHeader() throws Exception {
        MultiMap respHeaders = checkEmptyHttpResponse(GET, 204, MultiMap.caseInsensitiveMultiMap().set("transfer-encoding", "chunked"));
        assertNull(respHeaders.get("content-length"));
        assertNull(respHeaders.get("transfer-encoding"));
    }

    @Test
    public void testResetContentSetsContentLengthHeader() throws Exception {
        MultiMap respHeaders = checkEmptyHttpResponse(GET, 205, MultiMap.caseInsensitiveMultiMap());
        assertEquals("0", respHeaders.get("content-length"));
        assertNull(respHeaders.get("transfer-encoding"));
    }

    @Test
    public void testResetContentRemovesTransferEncodingHeader() throws Exception {
        MultiMap respHeaders = checkEmptyHttpResponse(GET, 205, MultiMap.caseInsensitiveMultiMap().set("transfer-encoding", "chunked"));
        assertEquals("0", respHeaders.get("content-length"));
        assertNull(respHeaders.get("transfer-encoding"));
    }

    @Test
    public void testNotModifiedDoesNotSetAutomaticallySetContentLengthHeader() throws Exception {
        MultiMap respHeaders = checkEmptyHttpResponse(GET, 304, MultiMap.caseInsensitiveMultiMap());
        assertNull(respHeaders.get("content-length"));
        assertNull(respHeaders.get("transfer-encoding"));
    }

    @Test
    public void testNotModifiedAllowsContentLengthHeader() throws Exception {
        MultiMap respHeaders = checkEmptyHttpResponse(GET, 304, MultiMap.caseInsensitiveMultiMap().set("content-length", "34"));
        assertEquals("34", respHeaders.get("Content-Length"));
        assertNull(respHeaders.get("transfer-encoding"));
    }

    @Test
    public void testNotModifiedRemovesTransferEncodingHeader() throws Exception {
        MultiMap respHeaders = checkEmptyHttpResponse(GET, 304, MultiMap.caseInsensitiveMultiMap().set("transfer-encoding", "chunked"));
        assertNull(respHeaders.get("content-length"));
        assertNull(respHeaders.get("transfer-encoding"));
    }

    @Test
    public void test1xxRemovesContentLengthHeader() throws Exception {
        MultiMap respHeaders = checkEmptyHttpResponse(GET, 102, MultiMap.caseInsensitiveMultiMap().set("content-length", "34"));
        assertNull(respHeaders.get("content-length"));
        assertNull(respHeaders.get("transfer-encoding"));
    }

    @Test
    public void test1xxRemovesTransferEncodingHeader() throws Exception {
        MultiMap respHeaders = checkEmptyHttpResponse(GET, 102, MultiMap.caseInsensitiveMultiMap().set("transfer-encoding", "chunked"));
        assertNull(respHeaders.get("content-length"));
        assertNull(respHeaders.get("transfer-encoding"));
    }

    @Test
    public void testHeadHasNoContentLengthByDefault() {
        server.requestHandler(( req) -> {
            assertEquals(HttpMethod.HEAD, req.method());
            // By default HEAD does not have a content-length header
            req.response().end();
        });
        server.listen(onSuccess(( s) -> {
            client.request(HttpMethod.HEAD, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, onSuccess(( resp) -> {
                assertNull(resp.headers().get(HttpHeaders.CONTENT_LENGTH));
                resp.endHandler(( v) -> testComplete());
            })).end();
        }));
        await();
    }

    @Test
    public void testHeadButCanSetContentLength() {
        server.requestHandler(( req) -> {
            assertEquals(HttpMethod.HEAD, req.method());
            // By default HEAD does not have a content-length header but it can contain a content-length header
            // if explicitly set
            req.response().putHeader(HttpHeaders.CONTENT_LENGTH, "41").end();
        });
        server.listen(onSuccess(( s) -> {
            client.request(HttpMethod.HEAD, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, onSuccess(( resp) -> {
                assertEquals("41", resp.headers().get(HttpHeaders.CONTENT_LENGTH));
                resp.endHandler(( v) -> testComplete());
            })).end();
        }));
        await();
    }

    @Test
    public void testRemoteAddress() {
        server.requestHandler(( req) -> {
            assertEquals("127.0.0.1", req.remoteAddress().host());
            req.response().end();
        });
        server.listen(onSuccess(( s) -> {
            client.request(HttpMethod.GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, onSuccess(( resp) -> resp.endHandler(( v) -> testComplete()))).end();
        }));
        await();
    }

    @Test
    public void testGetAbsoluteURI() {
        server.requestHandler(( req) -> {
            assertEquals(((((req.scheme()) + "://localhost:") + (HttpTestBase.DEFAULT_HTTP_PORT)) + "/foo/bar"), req.absoluteURI());
            req.response().end();
        });
        server.listen(onSuccess(( s) -> {
            client.request(HttpMethod.GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, "/foo/bar", onSuccess(( resp) -> resp.endHandler(( v) -> testComplete()))).end();
        }));
        await();
    }

    @Test
    public void testListenInvalidPort() throws Exception {
        /* Port 7 is free for use by any application in Windows, so this test fails. */
        Assume.assumeFalse(System.getProperty("os.name").startsWith("Windows"));
        server.close();
        server = vertx.createHttpServer(new HttpServerOptions().setPort(7));
        server.requestHandler(noOpHandler()).listen(onFailure(( server) -> testComplete()));
        await();
    }

    @Test
    public void testListenInvalidHost() {
        server.close();
        server = vertx.createHttpServer(setHost("iqwjdoqiwjdoiqwdiojwd"));
        server.requestHandler(noOpHandler());
        server.listen(onFailure(( s) -> testComplete()));
    }

    @Test
    public void testPauseResumeClientResponseWontCallEndHandlePrematurely() throws Exception {
        Buffer expected = Buffer.buffer(TestUtils.randomAlphaString(8192));
        server.requestHandler(( req) -> {
            req.response().end(expected);
        });
        startServer();
        client.getNow(HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, onSuccess(( resp) -> {
            resp.bodyHandler(( body) -> {
                assertEquals(expected, body);
                testComplete();
            });
            // Check that pause resume won't call the end handler prematurely
            resp.pause();
            resp.resume();
        }));
        await();
    }

    @Test
    public void testPauseClientResponse() {
        int numWrites = 10;
        int numBytes = 100;
        server.requestHandler(( req) -> {
            req.response().setChunked(true);
            // Send back a big response in several chunks
            for (int i = 0; i < numWrites; i++) {
                req.response().write(TestUtils.randomBuffer(numBytes));
            }
            req.response().end();
        });
        AtomicBoolean paused = new AtomicBoolean();
        Buffer totBuff = Buffer.buffer();
        HttpClientRequest clientRequest = client.request(GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, onSuccess(( resp) -> {
            resp.pause();
            paused.set(true);
            resp.handler(( chunk) -> {
                if (paused.get()) {
                    fail("Shouldn't receive chunks when paused");
                } else {
                    totBuff.appendBuffer(chunk);
                }
            });
            resp.endHandler(( v) -> {
                if (paused.get()) {
                    fail("Shouldn't receive chunks when paused");
                } else {
                    assertEquals((numWrites * numBytes), totBuff.length());
                    testComplete();
                }
            });
            vertx.setTimer(500, ( id) -> {
                paused.set(false);
                resp.resume();
            });
        }));
        server.listen(onSuccess(( s) -> clientRequest.end()));
        await();
    }

    @Test
    public void testDeliverPausedBufferWhenResume() throws Exception {
        testDeliverPausedBufferWhenResume(( block) -> vertx.setTimer(10, ( id) -> block.run()));
    }

    @Test
    public void testDeliverPausedBufferWhenResumeOnOtherThread() throws Exception {
        ExecutorService exec = Executors.newSingleThreadExecutor();
        try {
            testDeliverPausedBufferWhenResume(( block) -> exec.execute(() -> {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    fail(e);
                    Thread.currentThread().interrupt();
                }
                block.run();
            }));
        } finally {
            exec.shutdown();
        }
    }

    @Test
    public void testClearPausedBuffersWhenResponseEnds() throws Exception {
        Buffer data = TestUtils.randomBuffer(20);
        int num = 10;
        waitFor(num);
        server.requestHandler(( req) -> {
            req.response().end(data);
        });
        startServer();
        client.close();
        client = vertx.createHttpClient(createBaseClientOptions().setMaxPoolSize(1).setKeepAlive(true));
        for (int i = 0; i < num; i++) {
            client.request(GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, onSuccess(( resp) -> {
                resp.bodyHandler(( buff) -> {
                    assertEquals(data, buff);
                    complete();
                });
                resp.pause();
                vertx.setTimer(10, ( id) -> {
                    resp.resume();
                });
            })).end();
        }
        await();
    }

    @Test
    public void testPausedHttpServerRequest() throws Exception {
        CompletableFuture<Void> resumeCF = new CompletableFuture<>();
        Buffer expected = Buffer.buffer();
        server.requestHandler(( req) -> {
            req.pause();
            AtomicBoolean paused = new AtomicBoolean(true);
            Buffer body = Buffer.buffer();
            req.handler(( buff) -> {
                assertFalse(paused.get());
                body.appendBuffer(buff);
            });
            resumeCF.thenAccept(( v) -> {
                paused.set(false);
                req.resume();
            });
            req.endHandler(( v) -> {
                assertEquals(expected, body);
                req.response().end();
            });
        });
        startServer();
        HttpClientRequest req = client.put(HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTPS_HOST, HttpTestBase.DEFAULT_TEST_URI, onSuccess(( resp) -> {
            resp.endHandler(( v) -> {
                testComplete();
            });
        })).exceptionHandler(this::fail).setChunked(true);
        while (!(req.writeQueueFull())) {
            Buffer buff = Buffer.buffer(TestUtils.randomAlphaString(1024));
            expected.appendBuffer(buff);
            req.write(buff);
        } 
        resumeCF.complete(null);
        req.end();
        await();
    }

    @Test
    public void testHttpServerRequestPausedDuringLastChunk() throws Exception {
        server.requestHandler(( req) -> {
            AtomicBoolean ended = new AtomicBoolean();
            AtomicBoolean paused = new AtomicBoolean();
            req.handler(( buff) -> {
                assertEquals("small", buff.toString());
                req.pause();
                paused.set(true);
                vertx.setTimer(20, ( id) -> {
                    assertFalse(ended.get());
                    paused.set(false);
                    req.resume();
                });
            });
            req.endHandler(( v) -> {
                assertFalse(paused.get());
                ended.set(true);
                req.response().end();
            });
        });
        startServer();
        client.close();
        client = vertx.createHttpClient(createBaseClientOptions().setMaxPoolSize(1));
        client.put(HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, "/someuri", ( resp) -> {
            complete();
        }).end("small");
        await();
    }

    @Test
    public void testHttpClientResponsePausedDuringLastChunk() throws Exception {
        server.requestHandler(( req) -> {
            req.response().end("small");
        });
        startServer();
        client.close();
        client = vertx.createHttpClient(createBaseClientOptions().setMaxPoolSize(1));
        client.getNow(HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, "/someuri", onSuccess(( resp) -> {
            AtomicBoolean ended = new AtomicBoolean();
            AtomicBoolean paused = new AtomicBoolean();
            resp.handler(( buff) -> {
                assertEquals("small", buff.toString());
                resp.pause();
                paused.set(true);
                vertx.setTimer(20, ( id) -> {
                    assertFalse(ended.get());
                    paused.set(false);
                    resp.resume();
                });
            });
            resp.endHandler(( v) -> {
                assertFalse(paused.get());
                ended.set(true);
                complete();
            });
        }));
        await();
    }

    @Test
    public void testFormUploadEmptyFile() throws Exception {
        testFormUploadFile("", false, false);
    }

    @Test
    public void testFormUploadSmallFile() throws Exception {
        testFormUploadFile(TestUtils.randomAlphaString(100), false, false);
    }

    @Test
    public void testFormUploadMediumFile() throws Exception {
        testFormUploadFile(TestUtils.randomAlphaString(20000), false, false);
    }

    @Test
    public void testFormUploadLargeFile() throws Exception {
        testFormUploadFile(TestUtils.randomAlphaString(((4 * 1024) * 1024)), false, false);
    }

    @Test
    public void testFormUploadEmptyFileStreamToDisk() throws Exception {
        testFormUploadFile("", true, false);
    }

    @Test
    public void testFormUploadSmallFileStreamToDisk() throws Exception {
        testFormUploadFile(TestUtils.randomAlphaString(100), true, false);
    }

    @Test
    public void testFormUploadMediumFileStreamToDisk() throws Exception {
        testFormUploadFile(TestUtils.randomAlphaString((20 * 1024)), true, false);
    }

    @Test
    public void testFormUploadLargeFileStreamToDisk() throws Exception {
        testFormUploadFile(TestUtils.randomAlphaString(((4 * 1024) * 1024)), true, false);
    }

    @Test
    public void testBrokenFormUploadEmptyFile() throws Exception {
        testFormUploadFile("", true, true);
    }

    @Test
    public void testBrokenFormUploadSmallFile() throws Exception {
        testFormUploadFile(TestUtils.randomAlphaString(100), true, true);
    }

    @Test
    public void testBrokenFormUploadMediumFile() throws Exception {
        testFormUploadFile(TestUtils.randomAlphaString((20 * 1024)), true, true);
    }

    @Test
    public void testBrokenFormUploadLargeFile() throws Exception {
        testFormUploadFile(TestUtils.randomAlphaString(((4 * 1024) * 1024)), true, true);
    }

    @Test
    public void testBrokenFormUploadEmptyFileStreamToDisk() throws Exception {
        testFormUploadFile("", true, true);
    }

    @Test
    public void testBrokenFormUploadSmallFileStreamToDisk() throws Exception {
        testFormUploadFile(TestUtils.randomAlphaString(100), true, true);
    }

    @Test
    public void testBrokenFormUploadMediumFileStreamToDisk() throws Exception {
        testFormUploadFile(TestUtils.randomAlphaString((20 * 1024)), true, true);
    }

    @Test
    public void testBrokenFormUploadLargeFileStreamToDisk() throws Exception {
        testFormUploadFile(TestUtils.randomAlphaString(((4 * 1024) * 1024)), true, true);
    }

    @Test
    public void testFormUploadAttributes() throws Exception {
        AtomicInteger attributeCount = new AtomicInteger();
        server.requestHandler(( req) -> {
            if ((req.method()) == HttpMethod.POST) {
                assertEquals(req.path(), "/form");
                req.response().setChunked(true);
                req.setExpectMultipart(true);
                req.uploadHandler(( upload) -> upload.handler(( buffer) -> {
                    fail("Should get here");
                }));
                req.endHandler(( v) -> {
                    MultiMap attrs = req.formAttributes();
                    attributeCount.set(attrs.size());
                    assertEquals("vert x", attrs.get("framework"));
                    assertEquals("vert x", req.getFormAttribute("framework"));
                    assertEquals("jvm", attrs.get("runson"));
                    assertEquals("jvm", req.getFormAttribute("runson"));
                    req.response().end();
                });
            }
        });
        server.listen(onSuccess(( s) -> {
            HttpClientRequest req = client.request(HttpMethod.POST, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, "/form", onSuccess(( resp) -> {
                // assert the response
                assertEquals(200, resp.statusCode());
                resp.bodyHandler(( body) -> {
                    assertEquals(0, body.length());
                });
                assertEquals(2, attributeCount.get());
                testComplete();
            }));
            try {
                Buffer buffer = Buffer.buffer();
                // Make sure we have one param that needs url encoding
                buffer.appendString((("framework=" + (URLEncoder.encode("vert x", "UTF-8"))) + "&runson=jvm"), "UTF-8");
                req.headers().set("content-length", String.valueOf(buffer.length()));
                req.headers().set("content-type", "application/x-www-form-urlencoded");
                req.write(buffer).end();
            } catch ( e) {
                fail(e.getMessage());
            }
        }));
        await();
    }

    @Test
    public void testFormUploadAttributes2() throws Exception {
        AtomicInteger attributeCount = new AtomicInteger();
        server.requestHandler(( req) -> {
            if ((req.method()) == HttpMethod.POST) {
                assertEquals(req.path(), "/form");
                req.setExpectMultipart(true);
                req.uploadHandler(( event) -> event.handler(( buffer) -> {
                    fail("Should not get here");
                }));
                req.endHandler(( v) -> {
                    MultiMap attrs = req.formAttributes();
                    attributeCount.set(attrs.size());
                    assertEquals("junit-testUserAlias", attrs.get("origin"));
                    assertEquals("admin@foo.bar", attrs.get("login"));
                    assertEquals("admin", attrs.get("pass word"));
                    req.response().end();
                });
            }
        });
        server.listen(onSuccess(( s) -> {
            HttpClientRequest req = client.request(HttpMethod.POST, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, "/form", onSuccess(( resp) -> {
                // assert the response
                assertEquals(200, resp.statusCode());
                resp.bodyHandler(( body) -> {
                    assertEquals(0, body.length());
                });
                assertEquals(3, attributeCount.get());
                testComplete();
            }));
            Buffer buffer = Buffer.buffer();
            buffer.appendString("origin=junit-testUserAlias&login=admin%40foo.bar&pass+word=admin");
            req.headers().set("content-length", String.valueOf(buffer.length()));
            req.headers().set("content-type", "application/x-www-form-urlencoded");
            req.write(buffer).end();
        }));
        await();
    }

    @Test
    public void testHostHeaderOverridePossible() {
        server.requestHandler(( req) -> {
            assertEquals("localhost:4444", req.host());
            req.response().end();
        });
        server.listen(onSuccess(( s) -> {
            HttpClientRequest req = client.request(HttpMethod.GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, ( resp) -> testComplete());
            req.setHost("localhost:4444");
            req.end();
        }));
        await();
    }

    @Test
    public void testResponseBodyWriteFixedString() {
        String body = "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
        Buffer bodyBuff = Buffer.buffer(body);
        server.requestHandler(( req) -> {
            req.response().setChunked(true);
            req.response().write(body);
            req.response().end();
        });
        server.listen(onSuccess(( s) -> {
            client.request(HttpMethod.GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, onSuccess(( resp) -> {
                resp.bodyHandler(( buff) -> {
                    assertEquals(bodyBuff, buff);
                    testComplete();
                });
            })).end();
        }));
        await();
    }

    @Test
    public void testResponseDataTimeout() {
        Buffer expected = TestUtils.randomBuffer(1000);
        server.requestHandler(( req) -> {
            req.response().setChunked(true).write(expected);
        });
        server.listen(onSuccess(( s) -> {
            Buffer received = Buffer.buffer();
            HttpClientRequest req = client.request(HttpMethod.GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, onSuccess(( resp) -> {
                resp.request().setTimeout(500);
                resp.handler(received::appendBuffer);
            }));
            AtomicInteger count = new AtomicInteger();
            req.exceptionHandler(( t) -> {
                if ((count.getAndIncrement()) == 0) {
                    assertTrue((t instanceof TimeoutException));
                    assertEquals(expected, received);
                    testComplete();
                }
            });
            req.sendHead();
        }));
        await();
    }

    @Test
    public void testClientMultiThreaded() throws Exception {
        int numThreads = 10;
        Thread[] threads = new Thread[numThreads];
        CountDownLatch latch = new CountDownLatch(numThreads);
        server.requestHandler(( req) -> {
            req.response().putHeader("count", req.headers().get("count"));
            req.response().end();
        }).listen(( ar) -> {
            assertTrue(ar.succeeded());
            for (int i = 0; i < numThreads; i++) {
                int index = i;
                threads[i] = new Thread() {
                    public void run() {
                        client.request(HttpMethod.GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, "/", onSuccess(( res) -> {
                            assertEquals(200, res.statusCode());
                            assertEquals(String.valueOf(index), res.headers().get("count"));
                            latch.countDown();
                        })).putHeader("count", String.valueOf(index)).end();
                    }
                };
                threads[i].start();
            }
        });
        awaitLatch(latch);
        for (int i = 0; i < numThreads; i++) {
            threads[i].join();
        }
    }

    @Test
    public void testInVerticle() throws Exception {
        testInVerticle(false);
    }

    @Test
    public void testMultipleServerClose() {
        this.server = vertx.createHttpServer(new HttpServerOptions().setPort(HttpTestBase.DEFAULT_HTTP_PORT));
        AtomicInteger times = new AtomicInteger();
        // We assume the endHandler and the close completion handler are invoked in the same context task
        ThreadLocal stack = new ThreadLocal();
        stack.set(true);
        server.requestStream().endHandler(( v) -> {
            assertNull(stack.get());
            assertTrue(Vertx.currentContext().isEventLoopContext());
            times.incrementAndGet();
        });
        server.close(( ar1) -> {
            assertNull(stack.get());
            assertTrue(Vertx.currentContext().isEventLoopContext());
            server.close(( ar2) -> {
                server.close(( ar3) -> {
                    assertEquals(1, times.get());
                    testComplete();
                });
            });
        });
        await();
    }

    @Test
    public void testRequestEnded() {
        server.requestHandler(( req) -> {
            assertFalse(req.isEnded());
            req.endHandler(( v) -> {
                assertTrue(req.isEnded());
                try {
                    req.endHandler(( v2) -> {
                    });
                    fail("Shouldn't be able to set end handler");
                } catch ( e) {
                    // OK
                }
                try {
                    req.setExpectMultipart(true);
                    fail("Shouldn't be able to set expect multipart");
                } catch ( e) {
                    // OK
                }
                try {
                    req.bodyHandler(( v2) -> {
                    });
                    fail("Shouldn't be able to set body handler");
                } catch ( e) {
                    // OK
                }
                try {
                    req.handler(( v2) -> {
                    });
                    fail("Shouldn't be able to set handler");
                } catch ( e) {
                    // OK
                }
                req.response().setStatusCode(200).end();
            });
        });
        server.listen(( ar) -> {
            assertTrue(ar.succeeded());
            client.getNow(HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, "/blah", onSuccess(( resp) -> {
                assertEquals(200, resp.statusCode());
                testComplete();
            }));
        });
        await();
    }

    @Test
    public void testRequestEndedNoEndHandler() {
        server.requestHandler(( req) -> {
            assertFalse(req.isEnded());
            req.response().setStatusCode(200).end();
            vertx.setTimer(500, ( v) -> {
                assertTrue(req.isEnded());
                try {
                    req.endHandler(( v2) -> {
                    });
                    fail("Shouldn't be able to set end handler");
                } catch ( e) {
                    // OK
                }
                try {
                    req.setExpectMultipart(true);
                    fail("Shouldn't be able to set expect multipart");
                } catch ( e) {
                    // OK
                }
                try {
                    req.bodyHandler(( v2) -> {
                    });
                    fail("Shouldn't be able to set body handler");
                } catch ( e) {
                    // OK
                }
                try {
                    req.handler(( v2) -> {
                    });
                    fail("Shouldn't be able to set handler");
                } catch ( e) {
                    // OK
                }
                testComplete();
            });
        });
        server.listen(( ar) -> {
            assertTrue(ar.succeeded());
            client.getNow(HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, "/blah", onSuccess(( resp) -> {
                assertEquals(200, resp.statusCode());
            }));
        });
        await();
    }

    @Test
    public void testAbsoluteURIServer() {
        server.close();
        // Listen on all addresses
        server = vertx.createHttpServer(createBaseServerOptions().setHost("0.0.0.0"));
        server.requestHandler(( req) -> {
            String absURI = req.absoluteURI();
            assertEquals(((req.scheme()) + "://localhost:8080/path"), absURI);
            req.response().end();
        });
        server.listen(onSuccess(( s) -> {
            String host = "localhost";
            String path = "/path";
            int port = 8080;
            client.getNow(port, host, path, onSuccess(( resp) -> {
                assertEquals(200, resp.statusCode());
                testComplete();
            }));
        }));
        await();
    }

    @Test
    public void testDumpManyRequestsOnQueue() throws Exception {
        int sendRequests = 10000;
        AtomicInteger receivedRequests = new AtomicInteger();
        vertx.createHttpServer(createBaseServerOptions()).requestHandler(( r) -> {
            r.response().end();
            if ((receivedRequests.incrementAndGet()) == sendRequests) {
                testComplete();
            }
        }).listen(onSuccess(( s) -> {
            HttpClientOptions ops = createBaseClientOptions().setDefaultPort(HttpTestBase.DEFAULT_HTTP_PORT).setPipelining(true).setKeepAlive(true);
            HttpClient client = vertx.createHttpClient(ops);
            IntStream.range(0, sendRequests).forEach(( x) -> client.getNow("/", ( r) -> {
            }));
        }));
        await();
    }

    @Test
    public void testOtherMethodWithRawMethod() throws Exception {
        try {
            client.request(OTHER, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, "/somepath", ( resp) -> {
            }).end();
            fail();
        } catch (IllegalStateException expected) {
        }
    }

    @Test
    public void testOtherMethodRequest() throws Exception {
        server.requestHandler(( r) -> {
            assertEquals(HttpMethod.OTHER, r.method());
            assertEquals("COPY", r.rawMethod());
            r.response().end();
        }).listen(onSuccess(( s) -> {
            client.request(HttpMethod.OTHER, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, "/somepath", ( resp) -> {
                testComplete();
            }).setRawMethod("COPY").end();
        }));
        await();
    }

    @Test
    public void testClientLocalConnectionHandler() throws Exception {
        testClientConnectionHandler(true, false);
    }

    @Test
    public void testClientGlobalConnectionHandler() throws Exception {
        testClientConnectionHandler(false, true);
    }

    @Test
    public void testClientConnectionHandler() throws Exception {
        testClientConnectionHandler(true, true);
    }

    @Test
    public void testServerConnectionHandler() throws Exception {
        AtomicInteger status = new AtomicInteger();
        AtomicReference<HttpConnection> connRef = new AtomicReference<>();
        Context serverCtx = vertx.getOrCreateContext();
        server.connectionHandler(( conn) -> {
            assertSameEventLoop(serverCtx, Vertx.currentContext());
            assertEquals(0, status.getAndIncrement());
            assertNull(connRef.getAndSet(conn));
        });
        server.requestHandler(( req) -> {
            assertEquals(1, status.getAndIncrement());
            assertSame(connRef.get(), req.connection());
            req.response().end();
        });
        startServer(serverCtx, server);
        client.getNow(HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, "/somepath", ( resp) -> {
            testComplete();
        });
        await();
    }

    @Test
    public void testClientConnectionClose() throws Exception {
        // Test client connection close + server close handler
        CountDownLatch latch = new CountDownLatch(1);
        server.requestHandler(( req) -> {
            AtomicInteger len = new AtomicInteger();
            req.handler(( buff) -> {
                if ((len.addAndGet(buff.length())) == 1024) {
                    latch.countDown();
                }
            });
            req.connection().closeHandler(( v) -> {
                testComplete();
            });
        });
        CountDownLatch listenLatch = new CountDownLatch(1);
        server.listen(onSuccess(( s) -> listenLatch.countDown()));
        awaitLatch(listenLatch);
        HttpClientRequest req = client.post(HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, "/somepath", onFailure(( err) -> {
        }));
        req.setChunked(true);
        req.write(TestUtils.randomBuffer(1024));
        awaitLatch(latch);
        req.connection().close();
        await();
    }

    @Test
    public void testServerConnectionClose() throws Exception {
        // Test server connection close + client close handler
        server.requestHandler(( req) -> {
            req.connection().close();
        });
        CountDownLatch listenLatch = new CountDownLatch(1);
        server.listen(onSuccess(( s) -> listenLatch.countDown()));
        awaitLatch(listenLatch);
        client.post(HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, "/somepath", onFailure(( err) -> {
        })).connectionHandler(( conn) -> {
            conn.closeHandler(( v) -> {
                testComplete();
            });
        }).sendHead();
        await();
    }

    @Test
    public void testNoLogging() throws Exception {
        TestLoggerFactory factory = testLogging();
        assertFalse(factory.hasName("io.netty.handler.codec.http2.Http2FrameLogger"));
    }

    @Test
    public void testServerLogging() throws Exception {
        server.close();
        server = vertx.createHttpServer(createBaseServerOptions().setLogActivity(true));
        TestLoggerFactory factory = testLogging();
        if ((this) instanceof Http1xTest) {
            assertTrue(factory.hasName("io.netty.handler.logging.LoggingHandler"));
        } else {
            assertTrue(factory.hasName("io.netty.handler.codec.http2.Http2FrameLogger"));
        }
    }

    @Test
    public void testClientLogging() throws Exception {
        client.close();
        client = vertx.createHttpClient(createBaseClientOptions().setLogActivity(true));
        TestLoggerFactory factory = testLogging();
        if ((this) instanceof Http1xTest) {
            assertTrue(factory.hasName("io.netty.handler.logging.LoggingHandler"));
        } else {
            assertTrue(factory.hasName("io.netty.handler.codec.http2.Http2FrameLogger"));
        }
    }

    @Test
    public void testClientLocalAddress() throws Exception {
        String expectedAddress = TestUtils.loopbackAddress();
        client.close();
        client = vertx.createHttpClient(createBaseClientOptions().setLocalAddress(expectedAddress));
        server.requestHandler(( req) -> {
            assertEquals(expectedAddress, req.remoteAddress().host());
            req.response().end();
        });
        startServer();
        client.getNow(HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, "/somepath", onSuccess(( resp) -> {
            assertEquals(200, resp.statusCode());
            testComplete();
        }));
        await();
    }

    @Test
    public void testFollowRedirectGetOn301() throws Exception {
        testFollowRedirect(GET, GET, 301, 200, 2, "http://localhost:8080/redirected", "http://localhost:8080/redirected");
    }

    @Test
    public void testFollowRedirectPostOn301() throws Exception {
        testFollowRedirect(POST, GET, 301, 301, 1, "http://localhost:8080/redirected", "http://localhost:8080/somepath");
    }

    @Test
    public void testFollowRedirectPutOn301() throws Exception {
        testFollowRedirect(PUT, GET, 301, 301, 1, "http://localhost:8080/redirected", "http://localhost:8080/somepath");
    }

    @Test
    public void testFollowRedirectGetOn302() throws Exception {
        testFollowRedirect(GET, GET, 302, 200, 2, "http://localhost:8080/redirected", "http://localhost:8080/redirected");
    }

    @Test
    public void testFollowRedirectPostOn302() throws Exception {
        testFollowRedirect(POST, GET, 302, 302, 1, "http://localhost:8080/redirected", "http://localhost:8080/somepath");
    }

    @Test
    public void testFollowRedirectPutOn302() throws Exception {
        testFollowRedirect(PUT, GET, 302, 302, 1, "http://localhost:8080/redirected", "http://localhost:8080/somepath");
    }

    @Test
    public void testFollowRedirectGetOn303() throws Exception {
        testFollowRedirect(GET, GET, 303, 200, 2, "http://localhost:8080/redirected", "http://localhost:8080/redirected");
    }

    @Test
    public void testFollowRedirectPostOn303() throws Exception {
        testFollowRedirect(POST, GET, 303, 200, 2, "http://localhost:8080/redirected", "http://localhost:8080/redirected");
    }

    @Test
    public void testFollowRedirectPutOn303() throws Exception {
        testFollowRedirect(PUT, GET, 303, 200, 2, "http://localhost:8080/redirected", "http://localhost:8080/redirected");
    }

    @Test
    public void testFollowRedirectNotOn304() throws Exception {
        testFollowRedirect(GET, GET, 304, 304, 1, "http://localhost:8080/redirected", "http://localhost:8080/somepath");
    }

    @Test
    public void testFollowRedirectGetOn307() throws Exception {
        testFollowRedirect(GET, GET, 307, 200, 2, "http://localhost:8080/redirected", "http://localhost:8080/redirected");
    }

    @Test
    public void testFollowRedirectPostOn307() throws Exception {
        testFollowRedirect(POST, POST, 307, 307, 1, "http://localhost:8080/redirected", "http://localhost:8080/somepath");
    }

    @Test
    public void testFollowRedirectPutOn307() throws Exception {
        testFollowRedirect(PUT, PUT, 307, 307, 1, "http://localhost:8080/redirected", "http://localhost:8080/somepath");
    }

    @Test
    public void testFollowRedirectWithRelativeLocation() throws Exception {
        testFollowRedirect(GET, GET, 301, 200, 2, "/another", "http://localhost:8080/another");
    }

    @Test
    public void testFollowRedirectWithBody() throws Exception {
        testFollowRedirectWithBody(Function.identity());
    }

    @Test
    public void testFollowRedirectWithPaddedBody() throws Exception {
        testFollowRedirectWithBody(( buff) -> TestUtils.leftPad(1, buff));
    }

    @Test
    public void testFollowRedirectWithChunkedBody() throws Exception {
        Buffer buff1 = Buffer.buffer(TestUtils.randomAlphaString(2048));
        Buffer buff2 = Buffer.buffer(TestUtils.randomAlphaString(2048));
        Buffer expected = Buffer.buffer().appendBuffer(buff1).appendBuffer(buff2);
        AtomicBoolean redirected = new AtomicBoolean();
        CountDownLatch latch = new CountDownLatch(1);
        server.requestHandler(( req) -> {
            boolean redirect = redirected.compareAndSet(false, true);
            if (redirect) {
                latch.countDown();
            }
            if (redirect) {
                assertEquals(HttpMethod.PUT, req.method());
                req.bodyHandler(( body) -> {
                    assertEquals(body, expected);
                    String scheme = (createBaseServerOptions().isSsl()) ? "https" : "http";
                    req.response().setStatusCode(303).putHeader(HttpHeaders.LOCATION, (scheme + "://localhost:8080/whatever")).end();
                });
            } else {
                assertEquals(HttpMethod.GET, req.method());
                req.response().end();
            }
        });
        startServer();
        HttpClientRequest req = client.put(HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, "/somepath", onSuccess(( resp) -> {
            assertEquals(200, resp.statusCode());
            testComplete();
        })).setFollowRedirects(true).setChunked(true).write(buff1);
        awaitLatch(latch);
        req.end(buff2);
        await();
    }

    @Test
    public void testFollowRedirectWithRequestNotEnded() throws Exception {
        testFollowRedirectWithRequestNotEnded(false);
    }

    @Test
    public void testFollowRedirectWithRequestNotEndedFailing() throws Exception {
        testFollowRedirectWithRequestNotEnded(true);
    }

    @Test
    public void testFollowRedirectSendHeadThenBody() throws Exception {
        Buffer expected = Buffer.buffer(TestUtils.randomAlphaString(2048));
        AtomicBoolean redirected = new AtomicBoolean();
        server.requestHandler(( req) -> {
            if (redirected.compareAndSet(false, true)) {
                assertEquals(HttpMethod.PUT, req.method());
                req.bodyHandler(( body) -> {
                    assertEquals(body, expected);
                    req.response().setStatusCode(303).putHeader(HttpHeaders.LOCATION, "/whatever").end();
                });
            } else {
                assertEquals(HttpMethod.GET, req.method());
                req.response().end();
            }
        });
        startServer();
        HttpClientRequest req = client.put(HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, "/somepath", onSuccess(( resp) -> {
            assertEquals(200, resp.statusCode());
            testComplete();
        })).setFollowRedirects(true);
        req.putHeader("Content-Length", ("" + (expected.length())));
        req.exceptionHandler(this::fail);
        req.sendHead(( v) -> {
            req.end(expected);
        });
        await();
    }

    @Test
    public void testFollowRedirectLimit() throws Exception {
        AtomicInteger redirects = new AtomicInteger();
        server.requestHandler(( req) -> {
            int val = redirects.incrementAndGet();
            if (val > 16) {
                fail();
            } else {
                String scheme = (createBaseServerOptions().isSsl()) ? "https" : "http";
                req.response().setStatusCode(301).putHeader(HttpHeaders.LOCATION, (scheme + "://localhost:8080/otherpath")).end();
            }
        });
        startServer();
        client.get(HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, "/somepath", onSuccess(( resp) -> {
            assertEquals(16, redirects.get());
            assertEquals(301, resp.statusCode());
            assertEquals("/otherpath", resp.request().path());
            testComplete();
        })).setFollowRedirects(true).end();
        await();
    }

    @Test
    public void testFollowRedirectPropagatesTimeout() throws Exception {
        AtomicInteger redirections = new AtomicInteger();
        server.requestHandler(( req) -> {
            switch (redirections.getAndIncrement()) {
                case 0 :
                    String scheme = (createBaseServerOptions().isSsl()) ? "https" : "http";
                    req.response().setStatusCode(307).putHeader(HttpHeaders.LOCATION, (scheme + "://localhost:8080/whatever")).end();
                    break;
            }
        });
        startServer();
        AtomicBoolean done = new AtomicBoolean();
        client.get(HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, "/somepath", onFailure(( t) -> {
            if (done.compareAndSet(false, true)) {
                assertEquals(2, redirections.get());
                testComplete();
            }
        })).setFollowRedirects(true).setTimeout(500).end();
        await();
    }

    @Test
    public void testFollowRedirectHost() throws Exception {
        String scheme = (createBaseClientOptions().isSsl()) ? "https" : "http";
        waitFor(2);
        HttpServerOptions options = createBaseServerOptions();
        int port = (options.getPort()) + 1;
        options.setPort(port);
        AtomicInteger redirects = new AtomicInteger();
        server.requestHandler(( req) -> {
            redirects.incrementAndGet();
            req.response().setStatusCode(301).putHeader(HttpHeaders.LOCATION, (((scheme + "://localhost:") + port) + "/whatever")).end();
        });
        startServer();
        HttpServer server2 = vertx.createHttpServer(options);
        server2.requestHandler(( req) -> {
            assertEquals(1, redirects.get());
            assertEquals((((scheme + "://localhost:") + port) + "/whatever"), req.absoluteURI());
            req.response().end();
            complete();
        });
        startServer(server2);
        client.get(HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, "/somepath", onSuccess(( resp) -> {
            assertEquals((((scheme + "://localhost:") + port) + "/whatever"), resp.request().absoluteURI());
            complete();
        })).setFollowRedirects(true).setHost(("localhost:" + (options.getPort()))).end();
        await();
    }

    @Test
    public void testFollowRedirectWithCustomHandler() throws Exception {
        String scheme = (createBaseClientOptions().isSsl()) ? "https" : "http";
        waitFor(2);
        HttpServerOptions options = createBaseServerOptions();
        int port = (options.getPort()) + 1;
        options.setPort(port);
        AtomicInteger redirects = new AtomicInteger();
        server.requestHandler(( req) -> {
            redirects.incrementAndGet();
            req.response().setStatusCode(301).putHeader(HttpHeaders.LOCATION, (((scheme + "://localhost:") + port) + "/whatever")).end();
        });
        startServer();
        HttpServer server2 = vertx.createHttpServer(options);
        server2.requestHandler(( req) -> {
            assertEquals(1, redirects.get());
            assertEquals((((scheme + "://localhost:") + port) + "/custom"), req.absoluteURI());
            req.response().end();
            complete();
        });
        startServer(server2);
        client.redirectHandler(( resp) -> {
            Future<HttpClientRequest> fut = io.vertx.core.Future.future();
            vertx.setTimer(25, ( id) -> {
                HttpClientRequest req = client.getAbs((((scheme + "://localhost:") + port) + "/custom"));
                req.putHeader("foo", "foo_another");
                req.setHost(("localhost:" + port));
                fut.complete(req);
            });
            return fut;
        });
        client.get(HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, "/somepath", onSuccess(( resp) -> {
            assertEquals((((scheme + "://localhost:") + port) + "/custom"), resp.request().absoluteURI());
            complete();
        })).setFollowRedirects(true).putHeader("foo", "foo_value").setHost(("localhost:" + (options.getPort()))).end();
        await();
    }

    @Test
    public void testDefaultRedirectHandler() throws Exception {
        testFoo("http://example.com", "http://example.com");
        testFoo("http://example.com/somepath", "http://example.com/somepath");
        testFoo("http://example.com:8000", "http://example.com:8000");
        testFoo("http://example.com:8000/somepath", "http://example.com:8000/somepath");
        testFoo("https://example.com", "https://example.com");
        testFoo("https://example.com/somepath", "https://example.com/somepath");
        testFoo("https://example.com:8000", "https://example.com:8000");
        testFoo("https://example.com:8000/somepath", "https://example.com:8000/somepath");
        testFoo("whatever://example.com", null);
        testFoo("http://", null);
        testFoo("http://:8080/somepath", null);
    }

    @Test
    public void testFollowRedirectEncodedParams() throws Exception {
        String value1 = "\ud55c\uae00";
        String value2 = "A B+C";
        String value3 = "123 \u20ac";
        server.requestHandler(( req) -> {
            switch (req.path()) {
                case "/first/call/from/client" :
                    StringBuilder location = null;
                    try {
                        location = new StringBuilder().append(req.scheme()).append("://").append(HttpTestBase.DEFAULT_HTTP_HOST).append(':').append(HttpTestBase.DEFAULT_HTTP_PORT).append("/redirected/from/client?").append("encoded1=").append(URLEncoder.encode(value1, "UTF-8")).append('&').append("encoded2=").append(URLEncoder.encode(value2, "UTF-8")).append('&').append("encoded3=").append(URLEncoder.encode(value3, "UTF-8"));
                    } catch ( e) {
                        fail(e);
                    }
                    req.response().setStatusCode(302).putHeader("location", location.toString()).end();
                    break;
                case "/redirected/from/client" :
                    assertEquals(value1, req.params().get("encoded1"));
                    assertEquals(value2, req.params().get("encoded2"));
                    assertEquals(value3, req.params().get("encoded3"));
                    req.response().end();
                    break;
                default :
                    fail(("Unknown path: " + (req.path())));
            }
        });
        startServer();
        client.get(HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, "/first/call/from/client", onSuccess(( resp) -> {
            assertEquals(200, resp.statusCode());
            testComplete();
        })).setFollowRedirects(true).end();
        await();
    }

    @Test
    public void testServerResponseCloseHandlerNotHoldingLock() throws Exception {
        waitFor(7);
        server.requestHandler(( req) -> {
            HttpConnection conn = req.connection();
            req.exceptionHandler(( err) -> {
                assertFalse(Thread.holdsLock(conn));
                complete();
            });
            HttpServerResponse resp = req.response();
            resp.exceptionHandler(( err) -> {
                assertFalse(Thread.holdsLock(conn));
                complete();
            });
            resp.closeHandler(( v) -> {
                assertFalse(Thread.holdsLock(conn));
                complete();
            });
            conn.closeHandler(( err) -> {
                assertFalse(Thread.holdsLock(conn));
                complete();
            });
            resp.setChunked(true).write("hello");
        });
        startServer();
        HttpClientRequest req = client.get(HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, "/somepath", onSuccess(( resp) -> {
            assertEquals(200, resp.statusCode());
            HttpConnection conn = resp.request().connection();
            resp.exceptionHandler(( err) -> {
                assertFalse(Thread.holdsLock(conn));
                complete();
            });
            conn.closeHandler(( v) -> {
                assertFalse(Thread.holdsLock(conn));
                complete();
            });
            conn.close();
        }));
        req.exceptionHandler(( err) -> {
            assertFalse(Thread.holdsLock(req.connection()));
            complete();
        });
        req.setChunked(true).sendHead();
        await();
    }

    @Test
    public void testCloseHandlerWhenConnectionEnds() throws Exception {
        server.requestHandler(( req) -> {
            req.response().closeHandler(( v) -> {
                testComplete();
            });
            req.response().setChunked(true).write("some-data");
        });
        startServer();
        client.getNow(HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, "/somepath", onSuccess(( resp) -> {
            resp.handler(( v) -> {
                resp.request().connection().close();
            });
        }));
        await();
    }

    @Test
    public void testCloseHandlerWhenConnectionClose() throws Exception {
        server.requestHandler(( req) -> {
            HttpServerResponse resp = req.response();
            resp.setChunked(true).write("some-data");
            resp.closeHandler(( v) -> {
                checkHttpServerResponse(resp);
                testComplete();
            });
        });
        startServer();
        client.getNow(HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, "/somepath", onSuccess(( resp) -> {
            resp.handler(( v) -> {
                resp.request().connection().close();
            });
        }));
        await();
    }

    @Test
    public void testClientDecompressionError() throws Exception {
        waitFor(2);
        server.requestHandler(( req) -> {
            req.response().putHeader("Content-Encoding", "gzip").end("long response with mismatched encoding causes connection leaks");
        });
        startServer();
        AtomicInteger exceptionCount = new AtomicInteger();
        client.close();
        client = vertx.createHttpClient(createBaseClientOptions().setTryUseCompression(true));
        client.get(HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, onSuccess(( resp) -> {
            resp.exceptionHandler(( err) -> {
                if ((exceptionCount.incrementAndGet()) == 1) {
                    if (err instanceof Http2Exception) {
                        complete();
                        // Connection is not closed for HTTP/2 only the streams so we need to force it
                        resp.request().connection().close();
                    } else
                        if (err instanceof DecompressionException) {
                            complete();
                        }

                }
            });
        })).connectionHandler(( conn) -> {
            conn.closeHandler(( v) -> {
                complete();
            });
        }).end();
        await();
    }

    @Test
    public void testContainsValueString() {
        server.requestHandler(( req) -> {
            assertTrue(req.headers().contains("Foo", "foo", false));
            assertFalse(req.headers().contains("Foo", "fOo", false));
            req.response().putHeader("quux", "quux");
            req.response().end();
        });
        server.listen(onSuccess(( server) -> {
            HttpClientRequest req = client.request(HttpMethod.GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, onSuccess(( resp) -> {
                assertTrue(resp.headers().contains("Quux", "quux", false));
                assertFalse(resp.headers().contains("Quux", "quUx", false));
                testComplete();
            }));
            req.putHeader("foo", "foo");
            req.end();
        }));
        await();
    }

    @Test
    public void testContainsValueStringIgnoreCase() {
        server.requestHandler(( req) -> {
            assertTrue(req.headers().contains("Foo", "foo", true));
            assertTrue(req.headers().contains("Foo", "fOo", true));
            req.response().putHeader("quux", "quux");
            req.response().end();
        });
        server.listen(onSuccess(( server) -> {
            HttpClientRequest req = client.request(HttpMethod.GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, onSuccess(( resp) -> {
                assertTrue(resp.headers().contains("Quux", "quux", true));
                assertTrue(resp.headers().contains("Quux", "quUx", true));
                testComplete();
            }));
            req.putHeader("foo", "foo");
            req.end();
        }));
        await();
    }

    @Test
    public void testContainsValueCharSequence() {
        CharSequence Foo = HttpHeaders.createOptimized("Foo");
        CharSequence foo = HttpHeaders.createOptimized("foo");
        CharSequence fOo = HttpHeaders.createOptimized("fOo");
        CharSequence Quux = HttpHeaders.createOptimized("Quux");
        CharSequence quux = HttpHeaders.createOptimized("quux");
        CharSequence quUx = HttpHeaders.createOptimized("quUx");
        server.requestHandler(( req) -> {
            assertTrue(req.headers().contains(Foo, foo, false));
            assertFalse(req.headers().contains(Foo, fOo, false));
            req.response().putHeader(quux, quux);
            req.response().end();
        });
        server.listen(onSuccess(( server) -> {
            HttpClientRequest req = client.request(HttpMethod.GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, onSuccess(( resp) -> {
                assertTrue(resp.headers().contains(Quux, quux, false));
                assertFalse(resp.headers().contains(Quux, quUx, false));
                testComplete();
            }));
            req.putHeader(foo, foo);
            req.end();
        }));
        await();
    }

    @Test
    public void testContainsValueCharSequenceIgnoreCase() {
        CharSequence Foo = HttpHeaders.createOptimized("Foo");
        CharSequence foo = HttpHeaders.createOptimized("foo");
        CharSequence fOo = HttpHeaders.createOptimized("fOo");
        CharSequence Quux = HttpHeaders.createOptimized("Quux");
        CharSequence quux = HttpHeaders.createOptimized("quux");
        CharSequence quUx = HttpHeaders.createOptimized("quUx");
        server.requestHandler(( req) -> {
            assertTrue(req.headers().contains(Foo, foo, true));
            assertTrue(req.headers().contains(Foo, fOo, true));
            req.response().putHeader(quux, quux);
            req.response().end();
        });
        server.listen(onSuccess(( server) -> {
            HttpClientRequest req = client.request(HttpMethod.GET, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, onSuccess(( resp) -> {
                assertTrue(resp.headers().contains(Quux, quux, true));
                assertTrue(resp.headers().contains(Quux, quUx, true));
                testComplete();
            }));
            req.putHeader(foo, foo);
            req.end();
        }));
        await();
    }

    @Test
    public void testBytesReadRequest() throws Exception {
        int length = 2048;
        Buffer expected = Buffer.buffer(TestUtils.randomAlphaString(length));
        server.requestHandler(( req) -> {
            req.bodyHandler(( buffer) -> {
                assertEquals(req.bytesRead(), length);
                req.response().end();
            });
        });
        startServer();
        client.post(HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, onSuccess(( resp) -> {
            resp.bodyHandler(( buff) -> {
                testComplete();
            });
        })).exceptionHandler(this::fail).putHeader("content-length", String.valueOf(length)).write(expected).end();
        await();
    }

    @Test
    public void testClientSynchronousConnectFailures() {
        System.setProperty("vertx.disableDnsResolver", "true");
        Vertx vertx = Vertx.vertx(new VertxOptions().setAddressResolverOptions(new AddressResolverOptions().setQueryTimeout(100)));
        try {
            int poolSize = 2;
            HttpClient client = vertx.createHttpClient(new HttpClientOptions().setMaxPoolSize(poolSize));
            AtomicInteger failures = new AtomicInteger();
            vertx.runOnContext(( v) -> {
                for (int i = 0; i < (poolSize + 1); i++) {
                    AtomicBoolean f = new AtomicBoolean();
                    client.getAbs("http://invalid-host-name.foo.bar", onFailure(( resp) -> {
                        if (f.compareAndSet(false, true)) {
                            if ((failures.incrementAndGet()) == (poolSize + 1)) {
                                testComplete();
                            }
                        }
                    })).end();
                }
            });
            await();
        } finally {
            vertx.close();
            System.setProperty("vertx.disableDnsResolver", "false");
        }
    }

    @Test
    public void testClientConnectInvalidPort() {
        client.getNow((-1), "localhost", "/someuri", onFailure(( err) -> {
            assertEquals(err.getClass(), .class);
            assertEquals(err.getMessage(), "port p must be in range 0 <= p <= 65535");
            testComplete();
        }));
        await();
    }

    @Test
    public void testHttpClientRequestHeadersDontContainCROrLF() throws Exception {
        server.requestHandler(( req) -> {
            req.headers().forEach(( header) -> {
                String name = header.getKey();
                switch (name.toLowerCase()) {
                    case "host" :
                    case ":method" :
                    case ":path" :
                    case ":scheme" :
                    case ":authority" :
                        break;
                    default :
                        fail(("Unexpected header " + name));
                }
            });
            testComplete();
        });
        startServer();
        HttpClientRequest req = client.get(HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, ( resp) -> {
        });
        List<BiConsumer<String, String>> list = Arrays.asList(req::putHeader, req.headers()::set, req.headers()::add);
        list.forEach(( cs) -> {
            try {
                req.putHeader("header-name: header-value\r\nanother-header", "another-value");
                fail();
            } catch (IllegalArgumentException e) {
            }
        });
        assertEquals(0, req.headers().size());
        req.end();
        await();
    }

    @Test
    public void testHttpServerResponseHeadersDontContainCROrLF() throws Exception {
        server.requestHandler(( req) -> {
            List<BiConsumer<String, String>> list = Arrays.asList(req.response()::putHeader, req.response().headers()::set, req.response().headers()::add);
            list.forEach(( cs) -> {
                try {
                    cs.accept("header-name: header-value\r\nanother-header", "another-value");
                    fail();
                } catch ( e) {
                }
            });
            assertEquals(Collections.emptySet(), req.response().headers().names());
            req.response().end();
        });
        startServer();
        client.getNow(HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, onSuccess(( resp) -> {
            resp.headers().forEach(( header) -> {
                String name = header.getKey();
                switch (name.toLowerCase()) {
                    case ":status" :
                    case "content-length" :
                        break;
                    default :
                        fail(("Unexpected header " + name));
                }
            });
            testComplete();
        }));
        await();
    }

    @Test
    public void testDisableIdleTimeoutInPool() throws Exception {
        server.requestHandler(( req) -> {
            req.response().end();
        });
        startServer();
        client.close();
        client = vertx.createHttpClient(createBaseClientOptions().setIdleTimeout(1).setMaxPoolSize(1).setKeepAliveTimeout(10));
        client.getNow(HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, onSuccess(( resp) -> {
            resp.endHandler(( v1) -> {
                AtomicBoolean closed = new AtomicBoolean();
                resp.request().connection().closeHandler(( v2) -> {
                    closed.set(true);
                });
                vertx.setTimer(2000, ( id) -> {
                    assertFalse(closed.get());
                    testComplete();
                });
            });
        }));
        await();
    }

    @Test
    public void testHttpConnect() {
        Buffer buffer = TestUtils.randomBuffer(128);
        Buffer received = Buffer.buffer();
        CompletableFuture<Void> closeSocket = new CompletableFuture<>();
        vertx.createNetServer(new NetServerOptions().setPort(1235)).connectHandler(( socket) -> {
            socket.handler(socket::write);
            closeSocket.thenAccept(( v) -> {
                socket.close();
            });
        }).listen(onSuccess(( netServer) -> {
            server.requestHandler(( req) -> {
                vertx.createNetClient(new NetClientOptions()).connect(netServer.actualPort(), "localhost", onSuccess(( dst) -> {
                    req.response().setStatusCode(200);
                    req.response().setStatusMessage("Connection established");
                    // Now create a NetSocket
                    NetSocket src = req.netSocket();
                    // Create pumps which echo stuff
                    Pump pump1 = Pump.pump(src, dst).start();
                    Pump pump2 = Pump.pump(dst, src).start();
                    dst.closeHandler(( v) -> {
                        pump1.stop();
                        pump2.stop();
                        src.close();
                    });
                }));
            });
            server.listen(onSuccess(( s) -> {
                client.request(HttpMethod.CONNECT, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, onSuccess(( resp) -> {
                    assertEquals(200, resp.statusCode());
                    NetSocket socket = resp.netSocket();
                    socket.handler(( buff) -> {
                        received.appendBuffer(buff);
                        if ((received.length()) == (buffer.length())) {
                            closeSocket.complete(null);
                        }
                    });
                    socket.closeHandler(( v) -> {
                        assertEquals(buffer, received);
                        testComplete();
                    });
                    socket.write(buffer);
                })).sendHead();
            }));
        }));
        await();
    }

    @Test
    public void testAccessNetSocketPendingResponseDataPaused() {
        testAccessNetSocketPendingResponseData(true);
    }

    @Test
    public void testAccessNetSocketPendingResponseDataNotPaused() {
        testAccessNetSocketPendingResponseData(false);
    }

    @Test
    public void testHttpInvalidConnectResponseEnded() {
        waitFor(2);
        server.requestHandler(( req) -> {
            req.response().end();
            try {
                req.netSocket();
                fail();
            } catch ( e) {
                complete();
            }
        });
        server.listen(onSuccess(( s) -> {
            client.request(HttpMethod.CONNECT, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, onSuccess(( resp) -> {
                assertEquals(200, resp.statusCode());
                complete();
            })).end();
        }));
        await();
    }

    @Test
    public void testHttpInvalidConnectResponseChunked() {
        waitFor(2);
        server.requestHandler(( req) -> {
            req.response().setChunked(true).write("some-chunk");
            try {
                req.netSocket();
                fail();
            } catch ( e) {
                complete();
            }
        });
        server.listen(onSuccess(( s) -> {
            client.request(HttpMethod.CONNECT, HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, onSuccess(( resp) -> {
                assertEquals(200, resp.statusCode());
                complete();
            })).end();
        }));
        await();
    }

    @Test
    public void testEndFromAnotherThread() throws Exception {
        waitFor(2);
        disableThreadChecks();
        server.requestHandler(( req) -> {
            req.response().endHandler(( v) -> {
                complete();
            });
            new Thread(() -> {
                req.response().end();
            }).start();
        });
        startServer();
        client.getNow(HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, HttpTestBase.DEFAULT_TEST_URI, onSuccess(( resp) -> {
            assertEquals(200, resp.statusCode());
            complete();
        }));
        await();
    }
}

