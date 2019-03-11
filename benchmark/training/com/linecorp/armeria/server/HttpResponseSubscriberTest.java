/**
 * Copyright 2019 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.server;


import HttpMethod.GET;
import MediaType.PLAIN_TEXT_UTF_8;
import com.linecorp.armeria.common.HttpData;
import com.linecorp.armeria.common.HttpHeaders;
import com.linecorp.armeria.internal.HttpObjectEncoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.DefaultChannelPromise;
import io.netty.handler.codec.http2.Http2Error;
import io.netty.util.ReferenceCountUtil;
import org.junit.Test;
import org.mockito.Mockito;
import org.reactivestreams.Subscription;


public class HttpResponseSubscriberTest {
    @Test
    public void contentPreview() {
        final HttpHeaders headers = HttpHeaders.of(GET, "/");
        final DefaultServiceRequestContext sctx = HttpResponseSubscriberTest.serviceRequestContext(headers);
        final HttpResponseSubscriber responseSubscriber = HttpResponseSubscriberTest.responseSubscriber(headers, sctx);
        responseSubscriber.onSubscribe(Mockito.mock(Subscription.class));
        responseSubscriber.onNext(HttpHeaders.of(200).contentType(PLAIN_TEXT_UTF_8));
        responseSubscriber.onNext(new com.linecorp.armeria.unsafe.ByteBufHttpData(HttpResponseSubscriberTest.newBuffer("hello"), true));
        responseSubscriber.onComplete();
        assertThat(sctx.log().responseContentPreview()).isEqualTo("hello");
    }

    private static class ImmediateWriteEmulator extends HttpObjectEncoder {
        private Channel channel;

        ImmediateWriteEmulator(Channel channel) {
            this.channel = channel;
        }

        @Override
        protected Channel channel() {
            return channel;
        }

        @Override
        protected ChannelFuture doWriteHeaders(int id, int streamId, HttpHeaders headers, boolean endStream) {
            return successChannelFuture();
        }

        @Override
        protected ChannelFuture doWriteData(int id, int streamId, HttpData data, boolean endStream) {
            ReferenceCountUtil.safeRelease(data);
            return successChannelFuture();
        }

        @Override
        protected ChannelFuture doWriteReset(int id, int streamId, Http2Error error) {
            return successChannelFuture();
        }

        private ChannelFuture successChannelFuture() {
            final DefaultChannelPromise future = new DefaultChannelPromise(channel);
            future.setSuccess();
            return future;
        }

        @Override
        protected void doClose() {
        }
    }
}

