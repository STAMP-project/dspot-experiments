/**
 * Copyright 2015 The gRPC Authors
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
package io.grpc.netty;


import ChannelOption.SO_LINGER;
import GrpcUtil.CONTENT_TYPE_GRPC;
import GrpcUtil.CONTENT_TYPE_KEY;
import GrpcUtil.TE_HEADER;
import GrpcUtil.TE_TRAILERS;
import GrpcUtil.USER_AGENT_KEY;
import Metadata.ASCII_STRING_MARSHALLER;
import Metadata.Key;
import Status.CANCELLED;
import Status.INTERNAL;
import Status.UNAVAILABLE;
import Status.UNKNOWN;
import io.grpc.InternalChannelz;
import io.grpc.InternalChannelz.SocketOptions;
import io.grpc.Metadata;
import io.grpc.Status;
import io.netty.channel.Channel;
import io.netty.channel.ConnectTimeoutException;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.socket.oio.OioSocketChannel;
import io.netty.handler.codec.http2.DefaultHttp2Headers;
import io.netty.handler.codec.http2.Http2Error;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.util.AsciiString;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link Utils}.
 */
@RunWith(JUnit4.class)
public class UtilsTest {
    private final Metadata.Key<String> userKey = Key.of("user-key", ASCII_STRING_MARSHALLER);

    private final String userValue = "user-value";

    @Test
    public void testStatusFromThrowable() {
        Status s = CANCELLED.withDescription("msg");
        Assert.assertSame(s, Utils.statusFromThrowable(new Exception(s.asException())));
        Throwable t;
        t = new ConnectTimeoutException("msg");
        UtilsTest.assertStatusEquals(UNAVAILABLE.withCause(t), Utils.statusFromThrowable(t));
        t = new io.netty.handler.codec.http2.Http2Exception(Http2Error.INTERNAL_ERROR, "msg");
        UtilsTest.assertStatusEquals(INTERNAL.withCause(t), Utils.statusFromThrowable(t));
        t = new Exception("msg");
        UtilsTest.assertStatusEquals(UNKNOWN.withCause(t), Utils.statusFromThrowable(t));
    }

    @Test
    public void convertClientHeaders_sanitizes() {
        Metadata metaData = new Metadata();
        // Intentionally being explicit here rather than relying on any pre-defined lists of headers,
        // since the goal of this test is to validate the correctness of such lists in the first place.
        metaData.put(CONTENT_TYPE_KEY, "to-be-removed");
        metaData.put(USER_AGENT_KEY, "to-be-removed");
        metaData.put(TE_HEADER, "to-be-removed");
        metaData.put(userKey, userValue);
        String scheme = "https";
        String userAgent = "user-agent";
        String method = "POST";
        String authority = "authority";
        String path = "//testService/test";
        Http2Headers output = Utils.convertClientHeaders(metaData, new AsciiString(scheme), new AsciiString(path), new AsciiString(authority), new AsciiString(method), new AsciiString(userAgent));
        DefaultHttp2Headers headers = new DefaultHttp2Headers();
        for (Map.Entry<CharSequence, CharSequence> entry : output) {
            headers.add(entry.getKey(), entry.getValue());
        }
        // 7 reserved headers, 1 user header
        Assert.assertEquals((7 + 1), headers.size());
        // Check the 3 reserved headers that are non pseudo
        // Users can not create pseudo headers keys so no need to check for them here
        Assert.assertEquals(CONTENT_TYPE_GRPC, headers.get(CONTENT_TYPE_KEY.name()).toString());
        Assert.assertEquals(userAgent, headers.get(USER_AGENT_KEY.name()).toString());
        Assert.assertEquals(TE_TRAILERS, headers.get(TE_HEADER.name()).toString());
        // Check the user header is in tact
        Assert.assertEquals(userValue, headers.get(userKey.name()).toString());
    }

    // AsciiString.equals
    @Test
    @SuppressWarnings("UndefinedEquals")
    public void convertServerHeaders_sanitizes() {
        Metadata metaData = new Metadata();
        // Intentionally being explicit here rather than relying on any pre-defined lists of headers,
        // since the goal of this test is to validate the correctness of such lists in the first place.
        metaData.put(CONTENT_TYPE_KEY, "to-be-removed");
        metaData.put(TE_HEADER, "to-be-removed");
        metaData.put(USER_AGENT_KEY, "to-be-removed");
        metaData.put(userKey, userValue);
        Http2Headers output = Utils.convertServerHeaders(metaData);
        DefaultHttp2Headers headers = new DefaultHttp2Headers();
        for (Map.Entry<CharSequence, CharSequence> entry : output) {
            headers.add(entry.getKey(), entry.getValue());
        }
        // 2 reserved headers, 1 user header
        Assert.assertEquals((2 + 1), headers.size());
        Assert.assertEquals(Utils.CONTENT_TYPE_GRPC, headers.get(CONTENT_TYPE_KEY.name()));
    }

    @Test
    public void channelOptionsTest_noLinger() {
        Channel channel = new EmbeddedChannel();
        Assert.assertNull(channel.config().getOption(SO_LINGER));
        InternalChannelz.SocketOptions socketOptions = Utils.getSocketOptions(channel);
        Assert.assertNull(socketOptions.lingerSeconds);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void channelOptionsTest_oio() {
        Channel channel = new OioSocketChannel();
        SocketOptions socketOptions = UtilsTest.setAndValidateGeneric(channel);
        Assert.assertEquals(250, ((int) (socketOptions.soTimeoutMillis)));
    }

    @Test
    public void channelOptionsTest_nio() {
        Channel channel = new NioSocketChannel();
        SocketOptions socketOptions = UtilsTest.setAndValidateGeneric(channel);
        Assert.assertNull(socketOptions.soTimeoutMillis);
    }
}

