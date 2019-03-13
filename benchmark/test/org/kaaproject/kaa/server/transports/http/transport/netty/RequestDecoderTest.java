/**
 * Copyright 2014-2016 CyberVision, Inc.
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
package org.kaaproject.kaa.server.transports.http.transport.netty;


import HttpMethod.GET;
import HttpMethod.POST;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import org.junit.Test;
import org.kaaproject.kaa.server.common.server.BadRequestException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class RequestDecoderTest {
    private static RequestDecoder requestDecoder;

    private ChannelHandlerContext channelHandlerContext;

    @Test(expected = BadRequestException.class)
    public void noSuccessResult() throws Exception {
        HttpObject httpObject = createHttpRequestMock(null, false, HttpRequest.class);
        RequestDecoderTest.requestDecoder.channelRead0(null, httpObject);
    }

    @Test
    public void httpPostRequestTest() throws Exception {
        HttpObject request = createHttpRequestMock(POST, true, HttpRequest.class);
        RequestDecoderTest.requestDecoder.channelRead0(channelHandlerContext, request);
        Mockito.verify(channelHandlerContext).fireChannelRead(ArgumentMatchers.any(Object.class));
    }

    @Test(expected = BadRequestException.class)
    public void invalidMethodRequestTest() throws Exception {
        HttpObject request = createHttpRequestMock(GET, true, HttpRequest.class);
        RequestDecoderTest.requestDecoder.channelRead0(channelHandlerContext, request);
    }

    @Test
    public void nonHttpRequestObjectTest() throws Exception {
        HttpObject request = createHttpRequestMock(POST, true, HttpObject.class);
        RequestDecoderTest.requestDecoder.channelRead0(channelHandlerContext, request);
        Mockito.verify(channelHandlerContext, Mockito.never()).fireChannelRead(ArgumentMatchers.any(Object.class));
    }
}

