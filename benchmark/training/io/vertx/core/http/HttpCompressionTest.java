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


import HttpHeaders.ACCEPT_ENCODING;
import HttpHeaders.DEFLATE_GZIP;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import org.junit.Test;


/**
 *
 */
public class HttpCompressionTest extends HttpTestBase {
    private static final String COMPRESS_TEST_STRING = "/*\n" + (((((((((((((" * Copyright (c) 2011-2016 The original author or authors\n" + " * ------------------------------------------------------\n") + " * All rights reserved. This program and the accompanying materials\n") + " * are made available under the terms of the Eclipse Public License v1.0\n") + " * and Apache License v2.0 which accompanies this distribution.\n") + " *\n") + " *     The Eclipse Public License is available at\n") + " *     http://www.eclipse.org/legal/epl-v10.html\n") + " *\n") + " *     The Apache License v2.0 is available at\n") + " *     http://www.opensource.org/licenses/apache2.0.php\n") + " *\n") + " * You may elect to redistribute this code under either of these licenses.\n") + " */");

    private HttpServer serverWithMinCompressionLevel;

    private HttpServer serverWithMaxCompressionLevel = null;

    private HttpClient clientraw = null;

    @Test
    public void testSkipEncoding() throws Exception {
        serverWithMaxCompressionLevel.requestHandler(( req) -> {
            assertNotNull(req.headers().get("Accept-Encoding"));
            req.response().putHeader(HttpHeaders.CONTENT_ENCODING, HttpHeaders.IDENTITY).end(Buffer.buffer(COMPRESS_TEST_STRING).toString(CharsetUtil.UTF_8));
        });
        startServer(serverWithMaxCompressionLevel);
        clientraw.get(((HttpTestBase.DEFAULT_HTTP_PORT) + 1), HttpTestBase.DEFAULT_HTTP_HOST, "some-uri", onSuccess(( resp) -> {
            resp.bodyHandler(( responseBuffer) -> {
                String responseBody = responseBuffer.toString(CharsetUtil.UTF_8);
                assertEquals(COMPRESS_TEST_STRING, responseBody);
                testComplete();
            });
        })).putHeader(ACCEPT_ENCODING, DEFLATE_GZIP).end();
        await();
    }

    @Test
    public void testDefaultRequestHeaders() {
        Handler<HttpServerRequest> requestHandler = ( req) -> {
            assertEquals(2, req.headers().size());
            // assertEquals("localhost:" + DEFAULT_HTTP_PORT, req.headers().get("host"));
            assertNotNull(req.headers().get("Accept-Encoding"));
            req.response().end(Buffer.buffer(COMPRESS_TEST_STRING).toString(CharsetUtil.UTF_8));
        };
        serverWithMinCompressionLevel.requestHandler(requestHandler);
        serverWithMaxCompressionLevel.requestHandler(requestHandler);
        serverWithMinCompressionLevel.listen(onSuccess(( serverReady) -> {
            testMinCompression();
            testRawMinCompression();
        }));
        serverWithMaxCompressionLevel.listen(onSuccess(( serverReady) -> {
            testMaxCompression();
            testRawMaxCompression();
        }));
        await();
    }

    private static boolean minCompressionTestPassed = false;

    private static boolean maxCompressionTestPassed = false;

    private static Integer rawMaxCompressionResponseByteCount = null;

    private static Integer rawMinCompressionResponseByteCount = null;
}

