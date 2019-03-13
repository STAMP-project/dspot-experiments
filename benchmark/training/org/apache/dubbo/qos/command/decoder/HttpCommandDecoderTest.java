/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.qos.command.decoder;


import HttpHeaders.EMPTY_HEADERS;
import HttpMethod.GET;
import HttpMethod.POST;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import org.apache.dubbo.qos.command.CommandContext;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class HttpCommandDecoderTest {
    @Test
    public void decodeGet() throws Exception {
        HttpRequest request = Mockito.mock(HttpRequest.class);
        Mockito.when(request.getUri()).thenReturn("localhost:80/test");
        Mockito.when(request.getMethod()).thenReturn(GET);
        CommandContext context = HttpCommandDecoder.decode(request);
        MatcherAssert.assertThat(context.getCommandName(), Matchers.equalTo("test"));
        MatcherAssert.assertThat(context.isHttp(), Matchers.is(true));
        Mockito.when(request.getUri()).thenReturn("localhost:80/test?a=b&c=d");
        context = HttpCommandDecoder.decode(request);
        MatcherAssert.assertThat(context.getArgs(), Matchers.arrayContaining("b", "d"));
    }

    @Test
    public void decodePost() throws Exception {
        FullHttpRequest request = Mockito.mock(FullHttpRequest.class);
        Mockito.when(request.getUri()).thenReturn("localhost:80/test");
        Mockito.when(request.getMethod()).thenReturn(POST);
        Mockito.when(request.headers()).thenReturn(EMPTY_HEADERS);
        ByteBuf buf = Unpooled.copiedBuffer("a=b&c=d", StandardCharsets.UTF_8);
        Mockito.when(request.content()).thenReturn(buf);
        CommandContext context = HttpCommandDecoder.decode(request);
        MatcherAssert.assertThat(context.getCommandName(), Matchers.equalTo("test"));
        MatcherAssert.assertThat(context.isHttp(), Matchers.is(true));
        MatcherAssert.assertThat(context.getArgs(), Matchers.arrayContaining("b", "d"));
    }
}

