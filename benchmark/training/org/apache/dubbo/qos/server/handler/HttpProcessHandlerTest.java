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
package org.apache.dubbo.qos.server.handler;


import ChannelFutureListener.CLOSE;
import HttpMethod.GET;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class HttpProcessHandlerTest {
    @Test
    public void test1() throws Exception {
        ChannelHandlerContext context = Mockito.mock(ChannelHandlerContext.class);
        ChannelFuture future = Mockito.mock(ChannelFuture.class);
        Mockito.when(context.writeAndFlush(ArgumentMatchers.any(FullHttpResponse.class))).thenReturn(future);
        HttpRequest message = Mockito.mock(HttpRequest.class);
        Mockito.when(message.getUri()).thenReturn("test");
        HttpProcessHandler handler = new HttpProcessHandler();
        handler.channelRead0(context, message);
        Mockito.verify(future).addListener(CLOSE);
        ArgumentCaptor<FullHttpResponse> captor = ArgumentCaptor.forClass(FullHttpResponse.class);
        Mockito.verify(context).writeAndFlush(captor.capture());
        FullHttpResponse response = captor.getValue();
        MatcherAssert.assertThat(response.getStatus().code(), Matchers.equalTo(404));
    }

    @Test
    public void test2() throws Exception {
        ChannelHandlerContext context = Mockito.mock(ChannelHandlerContext.class);
        ChannelFuture future = Mockito.mock(ChannelFuture.class);
        Mockito.when(context.writeAndFlush(ArgumentMatchers.any(FullHttpResponse.class))).thenReturn(future);
        HttpRequest message = Mockito.mock(HttpRequest.class);
        Mockito.when(message.getUri()).thenReturn("localhost:80/greeting");
        Mockito.when(message.getMethod()).thenReturn(GET);
        HttpProcessHandler handler = new HttpProcessHandler();
        handler.channelRead0(context, message);
        Mockito.verify(future).addListener(CLOSE);
        ArgumentCaptor<FullHttpResponse> captor = ArgumentCaptor.forClass(FullHttpResponse.class);
        Mockito.verify(context).writeAndFlush(captor.capture());
        FullHttpResponse response = captor.getValue();
        MatcherAssert.assertThat(response.getStatus().code(), Matchers.equalTo(200));
    }

    @Test
    public void test3() throws Exception {
        ChannelHandlerContext context = Mockito.mock(ChannelHandlerContext.class);
        ChannelFuture future = Mockito.mock(ChannelFuture.class);
        Mockito.when(context.writeAndFlush(ArgumentMatchers.any(FullHttpResponse.class))).thenReturn(future);
        HttpRequest message = Mockito.mock(HttpRequest.class);
        Mockito.when(message.getUri()).thenReturn("localhost:80/test");
        Mockito.when(message.getMethod()).thenReturn(GET);
        HttpProcessHandler handler = new HttpProcessHandler();
        handler.channelRead0(context, message);
        Mockito.verify(future).addListener(CLOSE);
        ArgumentCaptor<FullHttpResponse> captor = ArgumentCaptor.forClass(FullHttpResponse.class);
        Mockito.verify(context).writeAndFlush(captor.capture());
        FullHttpResponse response = captor.getValue();
        MatcherAssert.assertThat(response.getStatus().code(), Matchers.equalTo(404));
    }
}

