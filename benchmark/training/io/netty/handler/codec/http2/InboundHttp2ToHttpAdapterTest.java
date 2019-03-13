/**
 * Copyright 2014 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License, version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.netty.handler.codec.http2;


import CharsetUtil.UTF_8;
import HttpConversionUtil.ExtensionHeaderNames.SCHEME;
import HttpConversionUtil.ExtensionHeaderNames.STREAM_DEPENDENCY_ID;
import HttpConversionUtil.ExtensionHeaderNames.STREAM_ID;
import HttpConversionUtil.ExtensionHeaderNames.STREAM_PROMISE_ID;
import HttpConversionUtil.ExtensionHeaderNames.STREAM_WEIGHT;
import HttpHeaderNames.CONTENT_LENGTH;
import HttpHeaderNames.COOKIE;
import HttpHeaderNames.EXPECT;
import HttpHeaderNames.HOST;
import HttpHeaderValues.CONTINUE;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.AsciiString;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Testing the {@link InboundHttp2ToHttpAdapter} and base class {@link InboundHttp2ToHttpAdapter} for HTTP/2
 * frames into {@link HttpObject}s
 */
public class InboundHttp2ToHttpAdapterTest {
    private List<FullHttpMessage> capturedRequests;

    private List<FullHttpMessage> capturedResponses;

    @Mock
    private InboundHttp2ToHttpAdapterTest.HttpResponseListener serverListener;

    @Mock
    private InboundHttp2ToHttpAdapterTest.HttpResponseListener clientListener;

    @Mock
    private InboundHttp2ToHttpAdapterTest.HttpSettingsListener settingsListener;

    private Http2ConnectionHandler serverHandler;

    private Http2ConnectionHandler clientHandler;

    private ServerBootstrap sb;

    private Bootstrap cb;

    private Channel serverChannel;

    private volatile Channel serverConnectedChannel;

    private Channel clientChannel;

    private CountDownLatch serverLatch;

    private CountDownLatch clientLatch;

    private CountDownLatch serverLatch2;

    private CountDownLatch clientLatch2;

    private CountDownLatch settingsLatch;

    private int maxContentLength;

    private InboundHttp2ToHttpAdapterTest.HttpResponseDelegator serverDelegator;

    private InboundHttp2ToHttpAdapterTest.HttpResponseDelegator clientDelegator;

    private InboundHttp2ToHttpAdapterTest.HttpSettingsDelegator settingsDelegator;

    private Http2Exception clientException;

    @Test
    public void clientRequestSingleHeaderNoDataFrames() throws Exception {
        boostrapEnv(1, 1, 1);
        final FullHttpRequest request = new io.netty.handler.codec.http.DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/some/path/resource2", true);
        try {
            HttpHeaders httpHeaders = request.headers();
            httpHeaders.set(SCHEME.text(), "https");
            httpHeaders.set(HOST, "example.org");
            httpHeaders.setInt(STREAM_ID.text(), 3);
            httpHeaders.setInt(CONTENT_LENGTH, 0);
            httpHeaders.setShort(STREAM_WEIGHT.text(), ((short) (16)));
            final Http2Headers http2Headers = new DefaultHttp2Headers().method(new AsciiString("GET")).scheme(new AsciiString("https")).authority(new AsciiString("example.org")).path(new AsciiString("/some/path/resource2"));
            Http2TestUtil.runInChannel(clientChannel, new Http2TestUtil.Http2Runnable() {
                @Override
                public void run() throws Http2Exception {
                    clientHandler.encoder().writeHeaders(ctxClient(), 3, http2Headers, 0, true, newPromiseClient());
                    clientChannel.flush();
                }
            });
            awaitRequests();
            ArgumentCaptor<FullHttpMessage> requestCaptor = ArgumentCaptor.forClass(FullHttpMessage.class);
            Mockito.verify(serverListener).messageReceived(requestCaptor.capture());
            capturedRequests = requestCaptor.getAllValues();
            Assert.assertEquals(request, capturedRequests.get(0));
        } finally {
            request.release();
        }
    }

    @Test
    public void clientRequestSingleHeaderCookieSplitIntoMultipleEntries() throws Exception {
        boostrapEnv(1, 1, 1);
        final FullHttpRequest request = new io.netty.handler.codec.http.DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/some/path/resource2", true);
        try {
            HttpHeaders httpHeaders = request.headers();
            httpHeaders.set(SCHEME.text(), "https");
            httpHeaders.set(HOST, "example.org");
            httpHeaders.setInt(STREAM_ID.text(), 3);
            httpHeaders.setInt(CONTENT_LENGTH, 0);
            httpHeaders.set(COOKIE, "a=b; c=d; e=f");
            httpHeaders.setShort(STREAM_WEIGHT.text(), ((short) (16)));
            final Http2Headers http2Headers = new DefaultHttp2Headers().method(new AsciiString("GET")).scheme(new AsciiString("https")).authority(new AsciiString("example.org")).path(new AsciiString("/some/path/resource2")).add(COOKIE, "a=b").add(COOKIE, "c=d").add(COOKIE, "e=f");
            Http2TestUtil.runInChannel(clientChannel, new Http2TestUtil.Http2Runnable() {
                @Override
                public void run() throws Http2Exception {
                    clientHandler.encoder().writeHeaders(ctxClient(), 3, http2Headers, 0, true, newPromiseClient());
                    clientChannel.flush();
                }
            });
            awaitRequests();
            ArgumentCaptor<FullHttpMessage> requestCaptor = ArgumentCaptor.forClass(FullHttpMessage.class);
            Mockito.verify(serverListener).messageReceived(requestCaptor.capture());
            capturedRequests = requestCaptor.getAllValues();
            Assert.assertEquals(request, capturedRequests.get(0));
        } finally {
            request.release();
        }
    }

    @Test
    public void clientRequestSingleHeaderCookieSplitIntoMultipleEntries2() throws Exception {
        boostrapEnv(1, 1, 1);
        final FullHttpRequest request = new io.netty.handler.codec.http.DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/some/path/resource2", true);
        try {
            HttpHeaders httpHeaders = request.headers();
            httpHeaders.set(SCHEME.text(), "https");
            httpHeaders.set(HOST, "example.org");
            httpHeaders.setInt(STREAM_ID.text(), 3);
            httpHeaders.setInt(CONTENT_LENGTH, 0);
            httpHeaders.set(COOKIE, "a=b; c=d; e=f");
            httpHeaders.setShort(STREAM_WEIGHT.text(), ((short) (16)));
            final Http2Headers http2Headers = new DefaultHttp2Headers().method(new AsciiString("GET")).scheme(new AsciiString("https")).authority(new AsciiString("example.org")).path(new AsciiString("/some/path/resource2")).add(COOKIE, "a=b; c=d").add(COOKIE, "e=f");
            Http2TestUtil.runInChannel(clientChannel, new Http2TestUtil.Http2Runnable() {
                @Override
                public void run() throws Http2Exception {
                    clientHandler.encoder().writeHeaders(ctxClient(), 3, http2Headers, 0, true, newPromiseClient());
                    clientChannel.flush();
                }
            });
            awaitRequests();
            ArgumentCaptor<FullHttpMessage> requestCaptor = ArgumentCaptor.forClass(FullHttpMessage.class);
            Mockito.verify(serverListener).messageReceived(requestCaptor.capture());
            capturedRequests = requestCaptor.getAllValues();
            Assert.assertEquals(request, capturedRequests.get(0));
        } finally {
            request.release();
        }
    }

    @Test
    public void clientRequestSingleHeaderNonAsciiShouldThrow() throws Exception {
        boostrapEnv(1, 1, 1);
        final Http2Headers http2Headers = new DefaultHttp2Headers().method(new AsciiString("GET")).scheme(new AsciiString("https")).authority(new AsciiString("example.org")).path(new AsciiString("/some/path/resource2")).add(new AsciiString("??".getBytes(UTF_8)), new AsciiString("??".getBytes(UTF_8)));
        Http2TestUtil.runInChannel(clientChannel, new Http2TestUtil.Http2Runnable() {
            @Override
            public void run() throws Http2Exception {
                clientHandler.encoder().writeHeaders(ctxClient(), 3, http2Headers, 0, true, newPromiseClient());
                clientChannel.flush();
            }
        });
        awaitResponses();
        Assert.assertTrue(Http2Exception.isStreamError(clientException));
    }

    @Test
    public void clientRequestOneDataFrame() throws Exception {
        boostrapEnv(1, 1, 1);
        final String text = "hello world";
        final ByteBuf content = Unpooled.copiedBuffer(text.getBytes());
        final FullHttpRequest request = new io.netty.handler.codec.http.DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/some/path/resource2", content, true);
        try {
            HttpHeaders httpHeaders = request.headers();
            httpHeaders.setInt(STREAM_ID.text(), 3);
            httpHeaders.setInt(CONTENT_LENGTH, text.length());
            httpHeaders.setShort(STREAM_WEIGHT.text(), ((short) (16)));
            final Http2Headers http2Headers = new DefaultHttp2Headers().method(new AsciiString("GET")).path(new AsciiString("/some/path/resource2"));
            Http2TestUtil.runInChannel(clientChannel, new Http2TestUtil.Http2Runnable() {
                @Override
                public void run() throws Http2Exception {
                    clientHandler.encoder().writeHeaders(ctxClient(), 3, http2Headers, 0, false, newPromiseClient());
                    clientHandler.encoder().writeData(ctxClient(), 3, content.retainedDuplicate(), 0, true, newPromiseClient());
                    clientChannel.flush();
                }
            });
            awaitRequests();
            ArgumentCaptor<FullHttpMessage> requestCaptor = ArgumentCaptor.forClass(FullHttpMessage.class);
            Mockito.verify(serverListener).messageReceived(requestCaptor.capture());
            capturedRequests = requestCaptor.getAllValues();
            Assert.assertEquals(request, capturedRequests.get(0));
        } finally {
            request.release();
        }
    }

    @Test
    public void clientRequestMultipleDataFrames() throws Exception {
        boostrapEnv(1, 1, 1);
        final String text = "hello world big time data!";
        final ByteBuf content = Unpooled.copiedBuffer(text.getBytes());
        final FullHttpRequest request = new io.netty.handler.codec.http.DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/some/path/resource2", content, true);
        try {
            HttpHeaders httpHeaders = request.headers();
            httpHeaders.setInt(STREAM_ID.text(), 3);
            httpHeaders.setInt(CONTENT_LENGTH, text.length());
            httpHeaders.setShort(STREAM_WEIGHT.text(), ((short) (16)));
            final Http2Headers http2Headers = new DefaultHttp2Headers().method(new AsciiString("GET")).path(new AsciiString("/some/path/resource2"));
            final int midPoint = (text.length()) / 2;
            Http2TestUtil.runInChannel(clientChannel, new Http2TestUtil.Http2Runnable() {
                @Override
                public void run() throws Http2Exception {
                    clientHandler.encoder().writeHeaders(ctxClient(), 3, http2Headers, 0, false, newPromiseClient());
                    clientHandler.encoder().writeData(ctxClient(), 3, content.retainedSlice(0, midPoint), 0, false, newPromiseClient());
                    clientHandler.encoder().writeData(ctxClient(), 3, content.retainedSlice(midPoint, ((text.length()) - midPoint)), 0, true, newPromiseClient());
                    clientChannel.flush();
                }
            });
            awaitRequests();
            ArgumentCaptor<FullHttpMessage> requestCaptor = ArgumentCaptor.forClass(FullHttpMessage.class);
            Mockito.verify(serverListener).messageReceived(requestCaptor.capture());
            capturedRequests = requestCaptor.getAllValues();
            Assert.assertEquals(request, capturedRequests.get(0));
        } finally {
            request.release();
        }
    }

    @Test
    public void clientRequestMultipleEmptyDataFrames() throws Exception {
        boostrapEnv(1, 1, 1);
        final String text = "";
        final ByteBuf content = Unpooled.copiedBuffer(text.getBytes());
        final FullHttpRequest request = new io.netty.handler.codec.http.DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/some/path/resource2", content, true);
        try {
            HttpHeaders httpHeaders = request.headers();
            httpHeaders.setInt(STREAM_ID.text(), 3);
            httpHeaders.setInt(CONTENT_LENGTH, text.length());
            httpHeaders.setShort(STREAM_WEIGHT.text(), ((short) (16)));
            final Http2Headers http2Headers = new DefaultHttp2Headers().method(new AsciiString("GET")).path(new AsciiString("/some/path/resource2"));
            Http2TestUtil.runInChannel(clientChannel, new Http2TestUtil.Http2Runnable() {
                @Override
                public void run() throws Http2Exception {
                    clientHandler.encoder().writeHeaders(ctxClient(), 3, http2Headers, 0, false, newPromiseClient());
                    clientHandler.encoder().writeData(ctxClient(), 3, content.retain(), 0, false, newPromiseClient());
                    clientHandler.encoder().writeData(ctxClient(), 3, content.retain(), 0, false, newPromiseClient());
                    clientHandler.encoder().writeData(ctxClient(), 3, content.retain(), 0, true, newPromiseClient());
                    clientChannel.flush();
                }
            });
            awaitRequests();
            ArgumentCaptor<FullHttpMessage> requestCaptor = ArgumentCaptor.forClass(FullHttpMessage.class);
            Mockito.verify(serverListener).messageReceived(requestCaptor.capture());
            capturedRequests = requestCaptor.getAllValues();
            Assert.assertEquals(request, capturedRequests.get(0));
        } finally {
            request.release();
        }
    }

    @Test
    public void clientRequestTrailingHeaders() throws Exception {
        boostrapEnv(1, 1, 1);
        final String text = "some data";
        final ByteBuf content = Unpooled.copiedBuffer(text.getBytes());
        final FullHttpRequest request = new io.netty.handler.codec.http.DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/some/path/resource2", content, true);
        try {
            HttpHeaders httpHeaders = request.headers();
            httpHeaders.setInt(STREAM_ID.text(), 3);
            httpHeaders.setInt(CONTENT_LENGTH, text.length());
            httpHeaders.setShort(STREAM_WEIGHT.text(), ((short) (16)));
            HttpHeaders trailingHeaders = request.trailingHeaders();
            trailingHeaders.set(Http2TestUtil.of("Foo"), Http2TestUtil.of("goo"));
            trailingHeaders.set(Http2TestUtil.of("fOo2"), Http2TestUtil.of("goo2"));
            trailingHeaders.add(Http2TestUtil.of("foO2"), Http2TestUtil.of("goo3"));
            final Http2Headers http2Headers = new DefaultHttp2Headers().method(new AsciiString("GET")).path(new AsciiString("/some/path/resource2"));
            final Http2Headers http2Headers2 = new DefaultHttp2Headers().set(new AsciiString("foo"), new AsciiString("goo")).set(new AsciiString("foo2"), new AsciiString("goo2")).add(new AsciiString("foo2"), new AsciiString("goo3"));
            Http2TestUtil.runInChannel(clientChannel, new Http2TestUtil.Http2Runnable() {
                @Override
                public void run() throws Http2Exception {
                    clientHandler.encoder().writeHeaders(ctxClient(), 3, http2Headers, 0, false, newPromiseClient());
                    clientHandler.encoder().writeData(ctxClient(), 3, content.retainedDuplicate(), 0, false, newPromiseClient());
                    clientHandler.encoder().writeHeaders(ctxClient(), 3, http2Headers2, 0, true, newPromiseClient());
                    clientChannel.flush();
                }
            });
            awaitRequests();
            ArgumentCaptor<FullHttpMessage> requestCaptor = ArgumentCaptor.forClass(FullHttpMessage.class);
            Mockito.verify(serverListener).messageReceived(requestCaptor.capture());
            capturedRequests = requestCaptor.getAllValues();
            Assert.assertEquals(request, capturedRequests.get(0));
        } finally {
            request.release();
        }
    }

    @Test
    public void clientRequestStreamDependencyInHttpMessageFlow() throws Exception {
        boostrapEnv(1, 2, 1);
        final String text = "hello world big time data!";
        final ByteBuf content = Unpooled.copiedBuffer(text.getBytes());
        final String text2 = "hello world big time data...number 2!!";
        final ByteBuf content2 = Unpooled.copiedBuffer(text2.getBytes());
        final FullHttpRequest request = new io.netty.handler.codec.http.DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.PUT, "/some/path/resource", content, true);
        final FullHttpMessage request2 = new io.netty.handler.codec.http.DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.PUT, "/some/path/resource2", content2, true);
        try {
            HttpHeaders httpHeaders = request.headers();
            httpHeaders.setInt(STREAM_ID.text(), 3);
            httpHeaders.setInt(CONTENT_LENGTH, text.length());
            httpHeaders.setShort(STREAM_WEIGHT.text(), ((short) (16)));
            HttpHeaders httpHeaders2 = request2.headers();
            httpHeaders2.setInt(STREAM_ID.text(), 5);
            httpHeaders2.setInt(STREAM_DEPENDENCY_ID.text(), 3);
            httpHeaders2.setShort(STREAM_WEIGHT.text(), ((short) (123)));
            httpHeaders2.setInt(CONTENT_LENGTH, text2.length());
            final Http2Headers http2Headers = new DefaultHttp2Headers().method(new AsciiString("PUT")).path(new AsciiString("/some/path/resource"));
            final Http2Headers http2Headers2 = new DefaultHttp2Headers().method(new AsciiString("PUT")).path(new AsciiString("/some/path/resource2"));
            Http2TestUtil.runInChannel(clientChannel, new Http2TestUtil.Http2Runnable() {
                @Override
                public void run() throws Http2Exception {
                    clientHandler.encoder().writeHeaders(ctxClient(), 3, http2Headers, 0, false, newPromiseClient());
                    clientHandler.encoder().writeHeaders(ctxClient(), 5, http2Headers2, 3, ((short) (123)), true, 0, false, newPromiseClient());
                    clientChannel.flush();// Headers are queued in the flow controller and so flush them.

                    clientHandler.encoder().writeData(ctxClient(), 3, content.retainedDuplicate(), 0, true, newPromiseClient());
                    clientHandler.encoder().writeData(ctxClient(), 5, content2.retainedDuplicate(), 0, true, newPromiseClient());
                    clientChannel.flush();
                }
            });
            awaitRequests();
            ArgumentCaptor<FullHttpMessage> httpObjectCaptor = ArgumentCaptor.forClass(FullHttpMessage.class);
            Mockito.verify(serverListener, Mockito.times(2)).messageReceived(httpObjectCaptor.capture());
            capturedRequests = httpObjectCaptor.getAllValues();
            Assert.assertEquals(request, capturedRequests.get(0));
            Assert.assertEquals(request2, capturedRequests.get(1));
        } finally {
            request.release();
            request2.release();
        }
    }

    @Test
    public void serverRequestPushPromise() throws Exception {
        boostrapEnv(1, 1, 1);
        final String text = "hello world big time data!";
        final ByteBuf content = Unpooled.copiedBuffer(text.getBytes());
        final String text2 = "hello world smaller data?";
        final ByteBuf content2 = Unpooled.copiedBuffer(text2.getBytes());
        final FullHttpMessage response = new io.netty.handler.codec.http.DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content, true);
        final FullHttpMessage response2 = new io.netty.handler.codec.http.DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CREATED, content2, true);
        final FullHttpMessage request = new io.netty.handler.codec.http.DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/push/test", true);
        try {
            HttpHeaders httpHeaders = response.headers();
            httpHeaders.setInt(STREAM_ID.text(), 3);
            httpHeaders.setInt(CONTENT_LENGTH, text.length());
            httpHeaders.setShort(STREAM_WEIGHT.text(), ((short) (16)));
            HttpHeaders httpHeaders2 = response2.headers();
            httpHeaders2.set(SCHEME.text(), "https");
            httpHeaders2.set(HOST, "example.org");
            httpHeaders2.setInt(STREAM_ID.text(), 5);
            httpHeaders2.setInt(STREAM_PROMISE_ID.text(), 3);
            httpHeaders2.setInt(CONTENT_LENGTH, text2.length());
            httpHeaders = request.headers();
            httpHeaders.setInt(STREAM_ID.text(), 3);
            httpHeaders.setInt(CONTENT_LENGTH, 0);
            httpHeaders.setShort(STREAM_WEIGHT.text(), ((short) (16)));
            final Http2Headers http2Headers3 = new DefaultHttp2Headers().method(new AsciiString("GET")).path(new AsciiString("/push/test"));
            Http2TestUtil.runInChannel(clientChannel, new Http2TestUtil.Http2Runnable() {
                @Override
                public void run() throws Http2Exception {
                    clientHandler.encoder().writeHeaders(ctxClient(), 3, http2Headers3, 0, true, newPromiseClient());
                    clientChannel.flush();
                }
            });
            awaitRequests();
            ArgumentCaptor<FullHttpMessage> requestCaptor = ArgumentCaptor.forClass(FullHttpMessage.class);
            Mockito.verify(serverListener).messageReceived(requestCaptor.capture());
            capturedRequests = requestCaptor.getAllValues();
            Assert.assertEquals(request, capturedRequests.get(0));
            final Http2Headers http2Headers = new DefaultHttp2Headers().status(new AsciiString("200"));
            // The PUSH_PROMISE frame includes a header block that contains a
            // complete set of request header fields that the server attributes to
            // the request.
            // https://tools.ietf.org/html/rfc7540#section-8.2.1
            // Therefore, we should consider the case where there is no Http response status.
            final Http2Headers http2Headers2 = new DefaultHttp2Headers().scheme(new AsciiString("https")).authority(new AsciiString("example.org"));
            Http2TestUtil.runInChannel(serverConnectedChannel, new Http2TestUtil.Http2Runnable() {
                @Override
                public void run() throws Http2Exception {
                    serverHandler.encoder().writeHeaders(ctxServer(), 3, http2Headers, 0, false, newPromiseServer());
                    serverHandler.encoder().writePushPromise(ctxServer(), 3, 2, http2Headers2, 0, newPromiseServer());
                    serverHandler.encoder().writeData(ctxServer(), 3, content.retainedDuplicate(), 0, true, newPromiseServer());
                    serverHandler.encoder().writeData(ctxServer(), 5, content2.retainedDuplicate(), 0, true, newPromiseServer());
                    serverConnectedChannel.flush();
                }
            });
            awaitResponses();
            ArgumentCaptor<FullHttpMessage> responseCaptor = ArgumentCaptor.forClass(FullHttpMessage.class);
            Mockito.verify(clientListener).messageReceived(responseCaptor.capture());
            capturedResponses = responseCaptor.getAllValues();
            Assert.assertEquals(response, capturedResponses.get(0));
        } finally {
            request.release();
            response.release();
            response2.release();
        }
    }

    @Test
    public void serverResponseHeaderInformational() throws Exception {
        boostrapEnv(1, 2, 1, 2, 1);
        final FullHttpMessage request = new io.netty.handler.codec.http.DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.PUT, "/info/test", true);
        HttpHeaders httpHeaders = request.headers();
        httpHeaders.setInt(STREAM_ID.text(), 3);
        httpHeaders.set(EXPECT, CONTINUE);
        httpHeaders.setInt(CONTENT_LENGTH, 0);
        httpHeaders.setShort(STREAM_WEIGHT.text(), ((short) (16)));
        final Http2Headers http2Headers = new DefaultHttp2Headers().method(new AsciiString("PUT")).path(new AsciiString("/info/test")).set(new AsciiString(EXPECT.toString()), new AsciiString(CONTINUE.toString()));
        final FullHttpMessage response = new io.netty.handler.codec.http.DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
        final String text = "a big payload";
        final ByteBuf payload = Unpooled.copiedBuffer(text.getBytes());
        final FullHttpMessage request2 = request.replace(payload);
        final FullHttpMessage response2 = new io.netty.handler.codec.http.DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        try {
            Http2TestUtil.runInChannel(clientChannel, new Http2TestUtil.Http2Runnable() {
                @Override
                public void run() throws Http2Exception {
                    clientHandler.encoder().writeHeaders(ctxClient(), 3, http2Headers, 0, false, newPromiseClient());
                    clientChannel.flush();
                }
            });
            awaitRequests();
            httpHeaders = response.headers();
            httpHeaders.setInt(STREAM_ID.text(), 3);
            httpHeaders.setInt(CONTENT_LENGTH, 0);
            final Http2Headers http2HeadersResponse = new DefaultHttp2Headers().status(new AsciiString("100"));
            Http2TestUtil.runInChannel(serverConnectedChannel, new Http2TestUtil.Http2Runnable() {
                @Override
                public void run() throws Http2Exception {
                    serverHandler.encoder().writeHeaders(ctxServer(), 3, http2HeadersResponse, 0, false, newPromiseServer());
                    serverConnectedChannel.flush();
                }
            });
            awaitResponses();
            httpHeaders = request2.headers();
            httpHeaders.setInt(CONTENT_LENGTH, text.length());
            httpHeaders.remove(EXPECT);
            Http2TestUtil.runInChannel(clientChannel, new Http2TestUtil.Http2Runnable() {
                @Override
                public void run() {
                    clientHandler.encoder().writeData(ctxClient(), 3, payload.retainedDuplicate(), 0, true, newPromiseClient());
                    clientChannel.flush();
                }
            });
            awaitRequests2();
            httpHeaders = response2.headers();
            httpHeaders.setInt(STREAM_ID.text(), 3);
            httpHeaders.setInt(CONTENT_LENGTH, 0);
            httpHeaders.setShort(STREAM_WEIGHT.text(), ((short) (16)));
            final Http2Headers http2HeadersResponse2 = new DefaultHttp2Headers().status(new AsciiString("200"));
            Http2TestUtil.runInChannel(serverConnectedChannel, new Http2TestUtil.Http2Runnable() {
                @Override
                public void run() throws Http2Exception {
                    serverHandler.encoder().writeHeaders(ctxServer(), 3, http2HeadersResponse2, 0, true, newPromiseServer());
                    serverConnectedChannel.flush();
                }
            });
            awaitResponses2();
            ArgumentCaptor<FullHttpMessage> requestCaptor = ArgumentCaptor.forClass(FullHttpMessage.class);
            Mockito.verify(serverListener, Mockito.times(2)).messageReceived(requestCaptor.capture());
            capturedRequests = requestCaptor.getAllValues();
            Assert.assertEquals(2, capturedRequests.size());
            // We do not expect to have this header in the captured request so remove it now.
            Assert.assertNotNull(request.headers().remove("x-http2-stream-weight"));
            Assert.assertEquals(request, capturedRequests.get(0));
            Assert.assertEquals(request2, capturedRequests.get(1));
            ArgumentCaptor<FullHttpMessage> responseCaptor = ArgumentCaptor.forClass(FullHttpMessage.class);
            Mockito.verify(clientListener, Mockito.times(2)).messageReceived(responseCaptor.capture());
            capturedResponses = responseCaptor.getAllValues();
            Assert.assertEquals(2, capturedResponses.size());
            Assert.assertEquals(response, capturedResponses.get(0));
            Assert.assertEquals(response2, capturedResponses.get(1));
        } finally {
            request.release();
            request2.release();
            response.release();
            response2.release();
        }
    }

    @Test
    public void propagateSettings() throws Exception {
        boostrapEnv(1, 1, 2);
        final Http2Settings settings = new Http2Settings().pushEnabled(true);
        Http2TestUtil.runInChannel(clientChannel, new Http2TestUtil.Http2Runnable() {
            @Override
            public void run() {
                clientHandler.encoder().writeSettings(ctxClient(), settings, newPromiseClient());
                clientChannel.flush();
            }
        });
        Assert.assertTrue(settingsLatch.await(5, TimeUnit.SECONDS));
        ArgumentCaptor<Http2Settings> settingsCaptor = ArgumentCaptor.forClass(Http2Settings.class);
        Mockito.verify(settingsListener, Mockito.times(2)).messageReceived(settingsCaptor.capture());
        Assert.assertEquals(settings, settingsCaptor.getValue());
    }

    private interface HttpResponseListener {
        void messageReceived(HttpObject obj);
    }

    private interface HttpSettingsListener {
        void messageReceived(Http2Settings settings);
    }

    private static final class HttpResponseDelegator extends SimpleChannelInboundHandler<HttpObject> {
        private final InboundHttp2ToHttpAdapterTest.HttpResponseListener listener;

        private final CountDownLatch latch;

        private final CountDownLatch latch2;

        HttpResponseDelegator(InboundHttp2ToHttpAdapterTest.HttpResponseListener listener, CountDownLatch latch, CountDownLatch latch2) {
            super(false);
            this.listener = listener;
            this.latch = latch;
            this.latch2 = latch2;
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
            listener.messageReceived(msg);
            latch.countDown();
            latch2.countDown();
        }
    }

    private static final class HttpSettingsDelegator extends SimpleChannelInboundHandler<Http2Settings> {
        private final InboundHttp2ToHttpAdapterTest.HttpSettingsListener listener;

        private final CountDownLatch latch;

        HttpSettingsDelegator(InboundHttp2ToHttpAdapterTest.HttpSettingsListener listener, CountDownLatch latch) {
            super(false);
            this.listener = listener;
            this.latch = latch;
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Http2Settings settings) throws Exception {
            listener.messageReceived(settings);
            latch.countDown();
        }
    }
}

