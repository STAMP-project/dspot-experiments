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


import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import org.junit.Test;


/**
 *
 *
 * @author <a href="mailto:nmaurer@redhat.com">Norman Maurer</a>
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class Http2CompressionTest extends Http2TestBase {
    private static final String COMPRESS_TEST_STRING = "/*\n" + (((((((((((((" * Copyright (c) 2011-2016 The original author or authors\n" + " * ------------------------------------------------------\n") + " * All rights reserved. This program and the accompanying materials\n") + " * are made available under the terms of the Eclipse Public License v1.0\n") + " * and Apache License v2.0 which accompanies this distribution.\n") + " *\n") + " *     The Eclipse Public License is available at\n") + " *     http://www.eclipse.org/legal/epl-v10.html\n") + " *\n") + " *     The Apache License v2.0 is available at\n") + " *     http://www.opensource.org/licenses/apache2.0.php\n") + " *\n") + " * You may elect to redistribute this code under either of these licenses.\n") + " */");

    HttpServer serverWithMinCompressionLevel;

    HttpServer serverWithMaxCompressionLevel = null;

    HttpClient clientraw = null;

    @Test
    public void testDefaultRequestHeaders() {
        Handler<HttpServerRequest> requestHandler = ( req) -> {
            // assertEquals(2, req.headers().size());
            assertEquals(HttpVersion.HTTP_2, req.version());
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

    public static boolean minCompressionTestPassed = false;

    public static boolean maxCompressionTestPassed = false;

    public static Integer rawMaxCompressionResponseByteCount = null;

    public static Integer rawMinCompressionResponseByteCount = null;
}

