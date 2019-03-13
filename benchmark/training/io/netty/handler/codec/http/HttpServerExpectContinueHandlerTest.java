/**
 * Copyright 2017 The Netty Project
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


import HttpHeaderNames.EXPECT;
import HttpResponseStatus.CONTINUE;
import HttpResponseStatus.REQUEST_ENTITY_TOO_LARGE;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.ReferenceCountUtil;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static HttpMethod.GET;
import static HttpResponseStatus.CONTINUE;
import static HttpResponseStatus.REQUEST_ENTITY_TOO_LARGE;
import static HttpVersion.HTTP_1_1;


public class HttpServerExpectContinueHandlerTest {
    @Test
    public void shouldRespondToExpectedHeader() {
        EmbeddedChannel channel = new EmbeddedChannel(new HttpServerExpectContinueHandler() {
            @Override
            protected HttpResponse acceptMessage(HttpRequest request) {
                HttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, CONTINUE);
                response.headers().set("foo", "bar");
                return response;
            }
        });
        HttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, GET, "/");
        HttpUtil.set100ContinueExpected(request, true);
        channel.writeInbound(request);
        HttpResponse response = channel.readOutbound();
        Assert.assertThat(response.status(), CoreMatchers.is(CONTINUE));
        Assert.assertThat(response.headers().get("foo"), CoreMatchers.is("bar"));
        ReferenceCountUtil.release(response);
        HttpRequest processedRequest = channel.readInbound();
        Assert.assertFalse(processedRequest.headers().contains(EXPECT));
        ReferenceCountUtil.release(processedRequest);
        Assert.assertFalse(channel.finishAndReleaseAll());
    }

    @Test
    public void shouldAllowCustomResponses() {
        EmbeddedChannel channel = new EmbeddedChannel(new HttpServerExpectContinueHandler() {
            @Override
            protected HttpResponse acceptMessage(HttpRequest request) {
                return null;
            }

            @Override
            protected HttpResponse rejectResponse(HttpRequest request) {
                return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, REQUEST_ENTITY_TOO_LARGE);
            }
        });
        HttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, GET, "/");
        HttpUtil.set100ContinueExpected(request, true);
        channel.writeInbound(request);
        HttpResponse response = channel.readOutbound();
        Assert.assertThat(response.status(), CoreMatchers.is(REQUEST_ENTITY_TOO_LARGE));
        ReferenceCountUtil.release(response);
        // request was swallowed
        Assert.assertTrue(channel.inboundMessages().isEmpty());
        Assert.assertFalse(channel.finishAndReleaseAll());
    }
}

