/**
 * Copyright 2016 Netflix, Inc.
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
package io.reactivex.netty.examples.http.interceptors.simple;


import HttpHeaderNames.TRANSFER_ENCODING;
import HttpHeaderValues.CHUNKED;
import InterceptingServer.INTERCEPTOR_HEADER_NAME;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.reactivex.netty.examples.ExamplesTestUtil;
import io.reactivex.netty.protocol.http.internal.HttpMessageFormatter;
import java.util.Queue;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class SimpleInterceptorTest {
    @Test(timeout = 60000)
    public void testSimpleInterceptor() throws Exception {
        Queue<String> output = ExamplesTestUtil.runClientInMockedEnvironment(InterceptingClient.class);
        HttpResponse expectedHeader = new io.netty.handler.codec.http.DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        expectedHeader.headers().add(((CharSequence) (INTERCEPTOR_HEADER_NAME)), "client");
        expectedHeader.headers().add(((CharSequence) (INTERCEPTOR_HEADER_NAME)), "server");
        expectedHeader.headers().add(TRANSFER_ENCODING, CHUNKED);
        String expectedHeaderString = HttpMessageFormatter.formatResponse(expectedHeader.protocolVersion(), expectedHeader.status(), expectedHeader.headers().iteratorCharSequence());
        MatcherAssert.assertThat("Unexpected number of messages echoed", output, hasSize(2));
        MatcherAssert.assertThat("Unexpected response.", output, contains(expectedHeaderString, "Hello World!"));
    }
}

