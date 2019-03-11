/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.ozone.web.client;


import ChannelOption.SO_KEEPALIVE;
import ChannelOption.SO_REUSEADDR;
import HttpResponseStatus.CREATED;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.logging.LogLevel;
import io.netty.util.CharsetUtil;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.hadoop.ozone.MiniOzoneCluster;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for Ozone client connection reuse with Apache HttpClient and Netty
 * based HttpClient.
 */
public class TestOzoneClient {
    private static Logger log = Logger.getLogger(TestOzoneClient.class);

    private static int testVolumeCount = 5;

    private static MiniOzoneCluster cluster = null;

    private static String endpoint = null;

    @Test(timeout = 5000)
    public void testNewConnectionPerRequest() throws IOException, URISyntaxException {
        for (int i = 0; i < (TestOzoneClient.testVolumeCount); i++) {
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                createVolume(getRandomVolumeName(i), httpClient);
            }
        }
    }

    /**
     * Object handler should be able to serve multiple requests from
     * a single http client. This allows the client side to reuse
     * http connections in a connection pool instead of creating a new
     * connection per request which consumes resource heavily.
     */
    @Test(timeout = 5000)
    public void testReuseWithApacheHttpClient() throws IOException, URISyntaxException {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(200);
        cm.setDefaultMaxPerRoute(20);
        try (CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build()) {
            for (int i = 0; i < (TestOzoneClient.testVolumeCount); i++) {
                createVolume(getRandomVolumeName(i), httpClient);
            }
        }
    }

    @Test(timeout = 10000)
    public void testReuseWithNettyHttpClient() throws IOException, InterruptedException, URISyntaxException {
        URI uri = new URI(TestOzoneClient.endpoint);
        String host = ((uri.getHost()) == null) ? "127.0.0.1" : uri.getHost();
        int port = uri.getPort();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup).channel(NioSocketChannel.class).option(SO_KEEPALIVE, true).option(SO_REUSEADDR, true).handler(new io.netty.channel.ChannelInitializer<SocketChannel>() {
                /**
                 * This method will be called once the {@link Channel} was
                 * registered. After the method returns this instance
                 * will be removed from the {@link ChannelPipeline}
                 * of the {@link Channel}.
                 *
                 * @param ch
                 * 		the {@link Channel} which was registered.
                 * @throws Exception
                 * 		is thrown if an error occurs.
                 * 		In that case the {@link Channel} will be closed.
                 */
                @Override
                public void initChannel(SocketChannel ch) {
                    ChannelPipeline p = ch.pipeline();
                    // Comment the following line if you don't want client http trace
                    p.addLast("log", new io.netty.handler.logging.LoggingHandler(LogLevel.INFO));
                    p.addLast(new HttpClientCodec());
                    p.addLast(new HttpContentDecompressor());
                    p.addLast(new TestOzoneClient.NettyHttpClientHandler());
                }
            });
            Channel ch = b.connect(host, port).sync().channel();
            for (int i = 0; i < (TestOzoneClient.testVolumeCount); i++) {
                String volumeName = getRandomVolumeName(i);
                try {
                    sendNettyCreateVolumeRequest(ch, volumeName);
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Thread.sleep(1000);
            ch.close();
            // Wait for the server to close the connection.
            ch.closeFuture().sync();
        } catch (Exception ex) {
            TestOzoneClient.log.error("Error received in client setup", ex);
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    class NettyHttpClientHandler extends SimpleChannelInboundHandler<HttpObject> {
        @Override
        public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {
            if (msg instanceof HttpResponse) {
                HttpResponse response = ((HttpResponse) (msg));
                TestOzoneClient.log.info(("STATUS: " + (response.getStatus())));
                TestOzoneClient.log.info(("VERSION: " + (response.getProtocolVersion())));
                Assert.assertEquals(CREATED.code(), response.getStatus().code());
            }
            if (msg instanceof HttpContent) {
                HttpContent content = ((HttpContent) (msg));
                TestOzoneClient.log.info(content.content().toString(CharsetUtil.UTF_8));
                if (content instanceof LastHttpContent) {
                    TestOzoneClient.log.info("END OF CONTENT");
                }
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            TestOzoneClient.log.error("Exception upon channel read", cause);
            ctx.close();
        }
    }
}

