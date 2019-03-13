/**
 * Copyright 2016 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.handler.codec.http;


import HttpHeaderNames.CONNECTION;
import HttpHeaderValues.CLOSE;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.StringUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static HttpMethod.GET;
import static HttpResponseStatus.PROCESSING;


@RunWith(Parameterized.class)
public class HttpServerKeepAliveHandlerTest {
    private static final String REQUEST_KEEP_ALIVE = "REQUEST_KEEP_ALIVE";

    private static final int NOT_SELF_DEFINED_MSG_LENGTH = 0;

    private static final int SET_RESPONSE_LENGTH = 1;

    private static final int SET_MULTIPART = 2;

    private static final int SET_CHUNKED = 4;

    private final boolean isKeepAliveResponseExpected;

    private final HttpVersion httpVersion;

    private final HttpResponseStatus responseStatus;

    private final String sendKeepAlive;

    private final int setSelfDefinedMessageLength;

    private final String setResponseConnection;

    private EmbeddedChannel channel;

    public HttpServerKeepAliveHandlerTest(boolean isKeepAliveResponseExpected, HttpVersion httpVersion, HttpResponseStatus responseStatus, String sendKeepAlive, int setSelfDefinedMessageLength, CharSequence setResponseConnection) {
        this.isKeepAliveResponseExpected = isKeepAliveResponseExpected;
        this.httpVersion = httpVersion;
        this.responseStatus = responseStatus;
        this.sendKeepAlive = sendKeepAlive;
        this.setSelfDefinedMessageLength = setSelfDefinedMessageLength;
        this.setResponseConnection = (setResponseConnection == null) ? null : setResponseConnection.toString();
    }

    @Test
    public void test_KeepAlive() throws Exception {
        FullHttpRequest request = new DefaultFullHttpRequest(httpVersion, GET, "/v1/foo/bar");
        HttpUtil.setKeepAlive(request, HttpServerKeepAliveHandlerTest.REQUEST_KEEP_ALIVE.equals(sendKeepAlive));
        HttpResponse response = new DefaultFullHttpResponse(httpVersion, responseStatus);
        if (!(StringUtil.isNullOrEmpty(setResponseConnection))) {
            response.headers().set(CONNECTION, setResponseConnection);
        }
        setupMessageLength(response);
        Assert.assertTrue(channel.writeInbound(request));
        Object requestForwarded = channel.readInbound();
        Assert.assertEquals(request, requestForwarded);
        ReferenceCountUtil.release(requestForwarded);
        channel.writeAndFlush(response);
        HttpResponse writtenResponse = channel.readOutbound();
        Assert.assertEquals("channel.isOpen", isKeepAliveResponseExpected, channel.isOpen());
        Assert.assertEquals("response keep-alive", isKeepAliveResponseExpected, HttpUtil.isKeepAlive(writtenResponse));
        ReferenceCountUtil.release(writtenResponse);
        Assert.assertFalse(channel.finishAndReleaseAll());
    }

    @Test
    public void testConnectionCloseHeaderHandledCorrectly() throws Exception {
        HttpResponse response = new DefaultFullHttpResponse(httpVersion, responseStatus);
        response.headers().set(CONNECTION, CLOSE);
        setupMessageLength(response);
        channel.writeAndFlush(response);
        HttpResponse writtenResponse = channel.readOutbound();
        Assert.assertFalse(channel.isOpen());
        ReferenceCountUtil.release(writtenResponse);
        Assert.assertFalse(channel.finishAndReleaseAll());
    }

    @Test
    public void testConnectionCloseHeaderHandledCorrectlyForVoidPromise() throws Exception {
        HttpResponse response = new DefaultFullHttpResponse(httpVersion, responseStatus);
        response.headers().set(CONNECTION, CLOSE);
        setupMessageLength(response);
        channel.writeAndFlush(response, channel.voidPromise());
        HttpResponse writtenResponse = channel.readOutbound();
        Assert.assertFalse(channel.isOpen());
        ReferenceCountUtil.release(writtenResponse);
        Assert.assertFalse(channel.finishAndReleaseAll());
    }

    @Test
    public void test_PipelineKeepAlive() {
        FullHttpRequest firstRequest = new DefaultFullHttpRequest(httpVersion, GET, "/v1/foo/bar");
        HttpUtil.setKeepAlive(firstRequest, true);
        FullHttpRequest secondRequest = new DefaultFullHttpRequest(httpVersion, GET, "/v1/foo/bar");
        HttpUtil.setKeepAlive(secondRequest, HttpServerKeepAliveHandlerTest.REQUEST_KEEP_ALIVE.equals(sendKeepAlive));
        FullHttpRequest finalRequest = new DefaultFullHttpRequest(httpVersion, GET, "/v1/foo/bar");
        HttpUtil.setKeepAlive(finalRequest, false);
        FullHttpResponse response = new DefaultFullHttpResponse(httpVersion, responseStatus);
        FullHttpResponse informationalResp = new DefaultFullHttpResponse(httpVersion, PROCESSING);
        HttpUtil.setKeepAlive(response, true);
        HttpUtil.setContentLength(response, 0);
        HttpUtil.setKeepAlive(informationalResp, true);
        Assert.assertTrue(channel.writeInbound(firstRequest, secondRequest, finalRequest));
        Object requestForwarded = channel.readInbound();
        Assert.assertEquals(firstRequest, requestForwarded);
        ReferenceCountUtil.release(requestForwarded);
        channel.writeAndFlush(response.retainedDuplicate());
        HttpResponse firstResponse = channel.readOutbound();
        Assert.assertTrue("channel.isOpen", channel.isOpen());
        Assert.assertTrue("response keep-alive", HttpUtil.isKeepAlive(firstResponse));
        ReferenceCountUtil.release(firstResponse);
        requestForwarded = channel.readInbound();
        Assert.assertEquals(secondRequest, requestForwarded);
        ReferenceCountUtil.release(requestForwarded);
        channel.writeAndFlush(informationalResp);
        HttpResponse writtenInfoResp = channel.readOutbound();
        Assert.assertTrue("channel.isOpen", channel.isOpen());
        Assert.assertTrue("response keep-alive", HttpUtil.isKeepAlive(writtenInfoResp));
        ReferenceCountUtil.release(writtenInfoResp);
        if (!(StringUtil.isNullOrEmpty(setResponseConnection))) {
            response.headers().set(CONNECTION, setResponseConnection);
        } else {
            response.headers().remove(CONNECTION);
        }
        setupMessageLength(response);
        channel.writeAndFlush(response.retainedDuplicate());
        HttpResponse secondResponse = channel.readOutbound();
        Assert.assertEquals("channel.isOpen", isKeepAliveResponseExpected, channel.isOpen());
        Assert.assertEquals("response keep-alive", isKeepAliveResponseExpected, HttpUtil.isKeepAlive(secondResponse));
        ReferenceCountUtil.release(secondResponse);
        requestForwarded = channel.readInbound();
        Assert.assertEquals(finalRequest, requestForwarded);
        ReferenceCountUtil.release(requestForwarded);
        if (isKeepAliveResponseExpected) {
            channel.writeAndFlush(response);
            HttpResponse finalResponse = channel.readOutbound();
            Assert.assertFalse("channel.isOpen", channel.isOpen());
            Assert.assertFalse("response keep-alive", HttpUtil.isKeepAlive(finalResponse));
        }
        ReferenceCountUtil.release(response);
        Assert.assertFalse(channel.finishAndReleaseAll());
    }
}

