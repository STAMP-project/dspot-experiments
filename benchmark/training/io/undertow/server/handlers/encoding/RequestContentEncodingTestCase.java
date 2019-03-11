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
package io.undertow.server.handlers.encoding;


import io.undertow.testutils.DefaultServer;
import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * This is not part of the HTTP spec
 *
 * @author Stuart Douglas
 */
@RunWith(DefaultServer.class)
public class RequestContentEncodingTestCase {
    private static volatile String message;

    /**
     * Tests the use of the deflate contentent encoding
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testDeflateEncoding() throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; ++i) {
            sb.append("a message");
        }
        runTest(sb.toString(), "deflate");
        runTest("Hello World", "deflate");
    }

    @Test
    public void testGzipEncoding() throws IOException {
        runTest("Hello World", "gzip");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; ++i) {
            sb.append("a message");
        }
        runTest(sb.toString(), "gzip");
    }
}

