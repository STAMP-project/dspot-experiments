/**
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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
package com.alibaba.csp.sentinel.transport.command.netty;


import com.alibaba.csp.sentinel.Constants;
import com.alibaba.csp.sentinel.config.SentinelConfig;
import io.netty.channel.embedded.EmbeddedChannel;
import java.nio.charset.Charset;
import org.junit.Test;


/**
 * Test cases for {@link HttpServerHandler}.
 *
 * @author cdfive
 * @unknown 2018-12-17
 */
public class HttpServerHandlerTest {
    private static String CRLF = "\r\n";

    private static String SENTINEL_CHARSET_NAME = SentinelConfig.charset();

    private static Charset SENTINEL_CHARSET = Charset.forName(HttpServerHandlerTest.SENTINEL_CHARSET_NAME);

    private static EmbeddedChannel embeddedChannel;

    @Test
    public void testInvalidCommand() {
        String httpRequestStr = ((("GET / HTTP/1.1" + (HttpServerHandlerTest.CRLF)) + "Host: localhost:8719") + (HttpServerHandlerTest.CRLF)) + (HttpServerHandlerTest.CRLF);
        String body = "Invalid command";
        processError(httpRequestStr, body);
    }

    @Test
    public void testUnknownCommand() {
        String httpRequestStr = ((("GET /aaa HTTP/1.1" + (HttpServerHandlerTest.CRLF)) + "Host: localhost:8719") + (HttpServerHandlerTest.CRLF)) + (HttpServerHandlerTest.CRLF);
        String body = String.format("Unknown command \"%s\"", "aaa");
        processError(httpRequestStr, body);
    }

    @Test
    public void testVersionCommand() {
        String httpRequestStr = ((("GET /version HTTP/1.1" + (HttpServerHandlerTest.CRLF)) + "Host: localhost:8719") + (HttpServerHandlerTest.CRLF)) + (HttpServerHandlerTest.CRLF);
        String body = Constants.SENTINEL_VERSION;
        processSuccess(httpRequestStr, body);
    }

    @Test
    public void testGetRuleCommandInvalidType() {
        String httpRequestStr = ((("GET /getRules HTTP/1.1" + (HttpServerHandlerTest.CRLF)) + "Host: localhost:8719") + (HttpServerHandlerTest.CRLF)) + (HttpServerHandlerTest.CRLF);
        String body = "invalid type";
        processFailed(httpRequestStr, body);
    }

    @Test
    public void testGetRuleCommandFlowEmptyRule() {
        String httpRequestStr = ((("GET /getRules?type=flow HTTP/1.1" + (HttpServerHandlerTest.CRLF)) + "Host: localhost:8719") + (HttpServerHandlerTest.CRLF)) + (HttpServerHandlerTest.CRLF);
        String body = "[]";
        processSuccess(httpRequestStr, body);
    }
}

