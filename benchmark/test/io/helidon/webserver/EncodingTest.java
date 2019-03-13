/**
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
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
import io.helidon.webserver.utils.SocketHttpClient;
import java.util.Map;
import java.util.logging.Logger;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


/**
 * The PlainTest.
 */
public class EncodingTest {
    private static final Logger LOGGER = Logger.getLogger(EncodingTest.class.getName());

    private static WebServer webServer;

    /**
     * Test path decoding and matching.
     *
     * @throws Exception
     * 		If an error occurs.
     */
    @Test
    public void testEncodedUrl() throws Exception {
        String s = SocketHttpClient.sendAndReceive("/f%6F%6F", GET, null, EncodingTest.webServer);
        MatcherAssert.assertThat(cutPayloadAndCheckHeadersFormat(s), CoreMatchers.is("9\nIt works!\n0\n\n"));
        Map<String, String> headers = cutHeaders(s);
        MatcherAssert.assertThat(headers, hasEntry("connection", "keep-alive"));
    }

    /**
     * Test path decoding with params and matching.
     *
     * @throws Exception
     * 		If an error occurs.
     */
    @Test
    public void testEncodedUrlParams() throws Exception {
        String s = SocketHttpClient.sendAndReceive("/f%6F%6F/b%61%72", GET, null, EncodingTest.webServer);
        MatcherAssert.assertThat(cutPayloadAndCheckHeadersFormat(s), CoreMatchers.is("3\nbar\n0\n\n"));
        Map<String, String> headers = cutHeaders(s);
        MatcherAssert.assertThat(headers, hasEntry("connection", "keep-alive"));
    }
}

