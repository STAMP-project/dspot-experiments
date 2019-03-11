/**
 * Copyright (c) 2016 Network New Technologies Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.networknt;


import io.undertow.Undertow;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EncodeDecodeHandlerTest {
    static final Logger logger = LoggerFactory.getLogger(EncodeDecodeHandlerTest.class);

    static Undertow server = null;

    static volatile String message;

    @Test
    public void testDeflateEncoding() throws Exception {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; ++i) {
            sb.append("a message");
        }
        runTest(sb.toString(), "deflate");
        runTest("Hello World", "deflate");
    }

    @Test
    public void testGzipEncoding() throws Exception {
        runTest("Hello World", "gzip");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; ++i) {
            sb.append("a message");
        }
        runTest(sb.toString(), "gzip");
    }
}

