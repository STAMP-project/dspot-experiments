package com.github.dockerjava.netty;


import CharsetUtil.UTF_8;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DefaultDockerClientConfig.Builder;
import com.github.dockerjava.core.DockerClientBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpServerCodec;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class NettyDockerCmdExecFactoryConfigTest {
    @Test
    public void testNettyDockerCmdExecFactoryConfigWithApiVersion() throws Exception {
        int dockerPort = getFreePort();
        NettyDockerCmdExecFactory factory = new NettyDockerCmdExecFactory();
        Builder configBuilder = new DefaultDockerClientConfig.Builder().withDockerTlsVerify(false).withDockerHost(("tcp://localhost:" + dockerPort)).withApiVersion("1.23");
        DockerClient client = DockerClientBuilder.getInstance(configBuilder).withDockerCmdExecFactory(factory).build();
        NettyDockerCmdExecFactoryConfigTest.FakeDockerServer server = new NettyDockerCmdExecFactoryConfigTest.FakeDockerServer(dockerPort);
        server.start();
        try {
            client.versionCmd().exec();
            List<HttpRequest> requests = server.getRequests();
            Assert.assertEquals(requests.size(), 1);
            Assert.assertEquals(requests.get(0).uri(), "/v1.23/version");
        } finally {
            server.stop();
        }
    }

    @Test
    public void testNettyDockerCmdExecFactoryConfigWithoutApiVersion() throws Exception {
        int dockerPort = getFreePort();
        NettyDockerCmdExecFactory factory = new NettyDockerCmdExecFactory();
        Builder configBuilder = new DefaultDockerClientConfig.Builder().withDockerTlsVerify(false).withDockerHost(("tcp://localhost:" + dockerPort));
        DockerClient client = DockerClientBuilder.getInstance(configBuilder).withDockerCmdExecFactory(factory).build();
        NettyDockerCmdExecFactoryConfigTest.FakeDockerServer server = new NettyDockerCmdExecFactoryConfigTest.FakeDockerServer(dockerPort);
        server.start();
        try {
            client.versionCmd().exec();
            List<HttpRequest> requests = server.getRequests();
            Assert.assertEquals(requests.size(), 1);
            Assert.assertEquals(requests.get(0).uri(), "/version");
        } finally {
            server.stop();
        }
    }

    private class FakeDockerServer {
        private final int port;

        private final NioEventLoopGroup parent;

        private final NioEventLoopGroup child;

        private final List<HttpRequest> requests = new ArrayList<>();

        private Channel channel;

        private FakeDockerServer(int port) {
            this.port = port;
            this.parent = new NioEventLoopGroup();
            this.child = new NioEventLoopGroup();
        }

        private void start() throws Exception {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(parent, child).channel(io.netty.channel.socket.nio.NioServerSocketChannel.class).childHandler(new io.netty.channel.ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    pipeline.addLast("codec", new HttpServerCodec());
                    pipeline.addLast("httpHandler", new io.netty.channel.SimpleChannelInboundHandler<Object>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext context, Object message) throws Exception {
                            if (message instanceof HttpRequest) {
                                // Keep track of processed requests
                                HttpRequest request = ((HttpRequest) (message));
                                requests.add(request);
                            }
                            if (message instanceof HttpContent) {
                                // Write an empty JSON response back to the client
                                FullHttpResponse response = new io.netty.handler.codec.http.DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.copiedBuffer("{}", UTF_8));
                                response.headers().set(CONTENT_TYPE, "application/json; charset=UTF-8");
                                response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
                                context.writeAndFlush(response);
                            }
                        }
                    });
                }
            });
            channel = bootstrap.bind(port).syncUninterruptibly().channel();
        }

        private void stop() throws Exception {
            parent.shutdownGracefully();
            child.shutdownGracefully();
            channel.closeFuture().sync();
        }

        private List<HttpRequest> getRequests() {
            return requests;
        }
    }
}

