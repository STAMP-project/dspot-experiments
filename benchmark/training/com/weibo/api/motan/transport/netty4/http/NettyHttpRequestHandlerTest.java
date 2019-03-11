/**
 * Copyright 2009-2016 Weibo, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.weibo.api.motan.transport.netty4.http;


import MotanConstants.REGISTRY_HEARTBEAT_SWITCHER;
import NettyHttpRequestHandler.ROOT_PATH;
import com.weibo.api.motan.transport.Channel;
import com.weibo.api.motan.transport.MessageHandler;
import com.weibo.api.motan.util.MotanSwitcherUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import org.jmock.Expectations;
import org.jmock.api.Invocation;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.action.CustomAction;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @unknown NettyHttpRequestHandlerTest
 * @author zhanglei
 * @unknown 2016?7?27?
 */
public class NettyHttpRequestHandlerTest {
    public static JUnit4Mockery mockery = null;

    @Test
    public void testChannelRead0() throws Exception {
        final MessageHandler messageHandler = NettyHttpRequestHandlerTest.mockery.mock(MessageHandler.class);
        final ChannelHandlerContext ctx = NettyHttpRequestHandlerTest.mockery.mock(ChannelHandlerContext.class);
        final FullHttpResponse response = NettyHttpRequestHandlerTest.mockery.mock(FullHttpResponse.class);
        NettyHttpRequestHandlerTest.mockery.checking(new Expectations() {
            {
                allowing(ctx).write(with(any(FullHttpResponse.class)));
                will(new CustomAction("verify") {
                    @Override
                    public Object invoke(Invocation invocation) throws Throwable {
                        FullHttpResponse actualResponse = ((FullHttpResponse) (invocation.getParameter(0)));
                        Assert.assertNotNull(actualResponse);
                        Assert.assertEquals(response, actualResponse);
                        return null;
                    }
                });
                allowing(ctx).flush();
                will(returnValue(null));
                allowing(ctx).close();
                will(returnValue(null));
                atLeast(1).of(messageHandler).handle(with(any(Channel.class)), with(anything()));
                will(returnValue(response));
                allowing(response).headers();
                will(returnValue(new DefaultHttpHeaders()));
            }
        });
        FullHttpRequest httpRequest = buildHttpRequest("anyPath");
        NettyHttpRequestHandler handler = new NettyHttpRequestHandler(null, messageHandler);
        handler.channelRead0(ctx, httpRequest);
    }

    @Test
    public void testServerStatus() throws Exception {
        final MessageHandler messageHandler = NettyHttpRequestHandlerTest.mockery.mock(MessageHandler.class);
        final ChannelHandlerContext ctx = NettyHttpRequestHandlerTest.mockery.mock(ChannelHandlerContext.class);
        NettyHttpRequestHandlerTest.mockery.checking(new Expectations() {
            {
                allowing(ctx).write(with(any(FullHttpResponse.class)));
                will(new CustomAction("verify") {
                    @Override
                    public Object invoke(Invocation invocation) throws Throwable {
                        verifyStatus(((FullHttpResponse) (invocation.getParameter(0))));
                        return null;
                    }
                });
                allowing(ctx).flush();
                will(returnValue(null));
                allowing(ctx).close();
                will(returnValue(null));
                allowing(messageHandler).handle(with(any(Channel.class)), with(anything()));
                will(returnValue(null));
            }
        });
        FullHttpRequest httpRequest = buildHttpRequest(ROOT_PATH);
        NettyHttpRequestHandler handler = new NettyHttpRequestHandler(null, messageHandler);
        // ??????
        MotanSwitcherUtil.setSwitcherValue(REGISTRY_HEARTBEAT_SWITCHER, false);
        handler.channelRead0(ctx, httpRequest);
        // ??????
        MotanSwitcherUtil.setSwitcherValue(REGISTRY_HEARTBEAT_SWITCHER, true);
        handler.channelRead0(ctx, httpRequest);
    }
}

