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
package org.netty.http.server;


import ChannelOption.SO_REUSEADDR;
import CharsetUtil.UTF_8;
import HttpHeaders.Values.CLOSE;
import HttpMethod.POST;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.Future;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.server.common.server.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Integration Test which demonstrates Netty framework error in case when parses Multipart-mixed
 * POST HTTP request. In case when multipart entity ends with odd number of '0x0D' bytes, Netty
 * framework append one more '0x0D' byte.
 *
 * To extract multipart entities used flowing code:
 *
 * HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);
 * HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(factory, request); InterfaceHttpData
 * data = decoder.getBodyHttpData(HTTP_TEST_ATTRIBUTE); Attribute attribute = (Attribute) data;
 * requestData = attribute.get();
 *
 * Test initialize Netty server and produce Http request with POST multipart entity.
 *
 * @author Andrey Panasenko
 */
public class NettyHttpTestIT {
    /**
     * Multipart entity Name in POST request
     */
    public static final String HTTP_TEST_ATTRIBUTE = "Test-attribute";

    public static final String HTTP_RESPONSE_CONTENT_TYPE = "x-application/kaaproject";

    /**
     * The Constant LOG.
     */
    private static final Logger LOG = LoggerFactory.getLogger(NettyHttpTestIT.class);

    /**
     * Netty bind port
     */
    private static final int HTTP_BIND_PORT = 9878;

    private static final int DEFAULT_REQUEST_MAX_SIZE = 2048;

    private static String[] hex = new String[]{ "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };

    /**
     * Thread executor, used for HttpClient operation and Netty starter operation
     */
    private static ExecutorService executor = null;

    private NettyHttpTestIT.NettyStarter netty = null;

    /**
     * Byte array filled out with sending bytes in multipart entity
     */
    private byte[] sendingArray;

    /**
     * Test which demonstrates Netty parsing bug.
     * In case of multipart POST entity ends with CR with odd number,
     * Netty return one more CR byte in multipart entity body.
     */
    @Test
    public void testOddCr() {
        NettyHttpTestIT.LOG.info("Starting HTTP test");
        Assert.assertNotNull(netty);
        String host = "localhost";
        Random rnd = new Random();
        int arrayLength = 16;
        sendingArray = new byte[arrayLength];
        rnd.nextBytes(sendingArray);
        // sendingArray[arrayLength-5] = 0x0d;
        // sendingArray[arrayLength-4] = 0x0d;
        sendingArray[(arrayLength - 3)] = 13;
        sendingArray[(arrayLength - 2)] = 13;
        sendingArray[(arrayLength - 1)] = 13;
        try {
            NettyHttpTestIT.LOG.info("Starting HTTP test to {}:{} ", host, NettyHttpTestIT.HTTP_BIND_PORT);
            NettyHttpTestIT.HttpTestClient test = new NettyHttpTestIT.HttpTestClient(host, NettyHttpTestIT.HTTP_BIND_PORT, sendingArray);
            NettyHttpTestIT.executor.submit(test);
            byte[] response = test.getResponseBody();
            Assert.assertEquals(ByteBuffer.wrap(sendingArray), ByteBuffer.wrap(response));
        } catch (IOException e) {
            NettyHttpTestIT.LOG.error("Error: ", e);
            Assert.fail(e.toString());
        }
        NettyHttpTestIT.LOG.info("Test complete");
    }

    /**
     * Netty starter class.
     * Initialize netty framework.
     * Start Netty.
     * Shutdown netty.
     *
     * @author Andrey Panasenko
     */
    public class NettyStarter implements Runnable {
        private EventLoopGroup bossGroup;

        private EventLoopGroup workerGroup;

        private ServerBootstrap bServer;

        private EventExecutorGroup eventExecutor;

        private Channel bindChannel;

        public NettyStarter() throws InterruptedException {
            NettyHttpTestIT.LOG.info("NettyHttpServer Initializing...");
            bossGroup = new NioEventLoopGroup();
            NettyHttpTestIT.LOG.trace("NettyHttpServer bossGroup created.");
            workerGroup = new NioEventLoopGroup();
            NettyHttpTestIT.LOG.trace("NettyHttpServer workGroup created.");
            bServer = new ServerBootstrap();
            NettyHttpTestIT.LOG.trace("NettyHttpServer ServerBootstrap created.");
            eventExecutor = new DefaultEventExecutorGroup(1);
            NettyHttpTestIT.LOG.trace("NettyHttpServer Task Executor created.");
            NettyHttpTestIT.DefaultServerInitializer sInit = new NettyHttpTestIT.DefaultServerInitializer(eventExecutor);
            NettyHttpTestIT.LOG.trace("NettyHttpServer InitClass instance created.");
            NettyHttpTestIT.LOG.trace("NettyHttpServer InitClass instance Init().");
            bServer.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(sInit).option(SO_REUSEADDR, true);
            NettyHttpTestIT.LOG.trace("NettyHttpServer ServerBootstrap group initialized.");
            bindChannel = bServer.bind(NettyHttpTestIT.HTTP_BIND_PORT).sync().channel();
        }

        public void shutdown() {
            NettyHttpTestIT.LOG.info("NettyHttpServer stopping...");
            if ((bossGroup) != null) {
                try {
                    Future<? extends Object> f = bossGroup.shutdownGracefully();
                    f.await();
                } catch (InterruptedException e) {
                    NettyHttpTestIT.LOG.trace("NettyHttpServer stopping: bossGroup error", e);
                } finally {
                    bossGroup = null;
                    NettyHttpTestIT.LOG.trace("NettyHttpServer stopping: bossGroup stoped");
                }
            }
            if ((workerGroup) != null) {
                try {
                    Future<? extends Object> f = workerGroup.shutdownGracefully();
                    f.await();
                } catch (InterruptedException e) {
                    NettyHttpTestIT.LOG.trace("NettyHttpServer stopping: workerGroup error", e);
                } finally {
                    workerGroup = null;
                    NettyHttpTestIT.LOG.trace("NettyHttpServer stopping: workerGroup stopped");
                }
            }
            if ((eventExecutor) != null) {
                try {
                    Future<? extends Object> f = eventExecutor.shutdownGracefully();
                    f.await();
                } catch (InterruptedException e) {
                    NettyHttpTestIT.LOG.trace("NettyHttpServer stopping: task executor error", e);
                } finally {
                    eventExecutor = null;
                    NettyHttpTestIT.LOG.trace("NettyHttpServer stopping: task executor stopped.");
                }
            }
        }

        /* (non-Javadoc)
        @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            NettyHttpTestIT.LOG.info("NettyHttpServer starting...");
            try {
                bindChannel.closeFuture().sync();
            } catch (InterruptedException e) {
                NettyHttpTestIT.LOG.error("NettyHttpServer error", e);
            } finally {
                shutdown();
                NettyHttpTestIT.LOG.info("NettyHttpServer shut down");
            }
        }
    }

    /**
     * Netty channel initializer.
     *
     * @author Andrey Panasenko
     */
    public class DefaultServerInitializer extends ChannelInitializer<SocketChannel> {
        private EventExecutorGroup eventExecutor;

        public DefaultServerInitializer(EventExecutorGroup eventExecutor) {
            this.eventExecutor = eventExecutor;
        }

        /* (non-Javadoc)
        @see io.netty.channel.ChannelInitializer#initChannel(io.netty.channel.Channel)
         */
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            final ChannelPipeline p = ch.pipeline();
            NettyHttpTestIT.LOG.info("New connection from {}", ch.remoteAddress().toString());
            p.addLast("httpDecoder", new HttpRequestDecoder());
            p.addLast("httpAggregator", new HttpObjectAggregator(NettyHttpTestIT.DEFAULT_REQUEST_MAX_SIZE));
            p.addLast("httpEncoder", new HttpResponseEncoder());
            p.addLast("handler", new NettyHttpTestIT.DefaultHandler(eventExecutor));
            p.addLast("httpExceptionHandler", new NettyHttpTestIT.DefaultExceptionHandler());
        }
    }

    /**
     * HTTP Request handler.
     *
     * @author Andrey Panasenko
     */
    public class DefaultHandler extends SimpleChannelInboundHandler<HttpRequest> {
        private EventExecutorGroup eventExecutor;

        public DefaultHandler(EventExecutorGroup eventExecutor) {
            this.eventExecutor = eventExecutor;
        }

        /* (non-Javadoc)
        @see io.netty.channel.SimpleChannelInboundHandler#channelRead0(io.netty.channel.ChannelHandlerContext, java.lang.Object)
         */
        @Override
        protected void channelRead0(final ChannelHandlerContext ctx, HttpRequest msg) throws Exception {
            NettyHttpTestIT.HttpHandler handler = new NettyHttpTestIT.HttpHandler();
            handler.setHttpRequest(msg);
            final Future<NettyHttpTestIT.HttpHandler> future = ((Future<NettyHttpTestIT.HttpHandler>) (eventExecutor.submit(handler)));
            future.addListener(new io.netty.util.concurrent.GenericFutureListener<Future<NettyHttpTestIT.HttpHandler>>() {
                @Override
                public void operationComplete(Future<NettyHttpTestIT.HttpHandler> future) throws Exception {
                    NettyHttpTestIT.LOG.trace("HttpHandler().operationComplete...");
                    if (future.isSuccess()) {
                        HttpResponse response = future.get().getHttpResponse();
                        if (response != null) {
                            ctx.writeAndFlush(response);
                        } else {
                            ctx.fireExceptionCaught(new Exception("Error creating response"));
                        }
                    } else {
                        ctx.fireExceptionCaught(future.cause());
                    }
                }
            });
        }
    }

    /**
     * HTTP Request handler.
     * Parse HTTP Request.
     * Check received bytes[] in multipart entity with sending bytes[]
     * Produce response if check correct.
     *
     * @author Andrey Panasenko
     */
    public class HttpHandler implements Callable<NettyHttpTestIT.HttpHandler> {
        private FullHttpResponse response;

        private byte[] requestData;

        /* (non-Javadoc)
        @see java.util.concurrent.Callable#call()
         */
        @Override
        public NettyHttpTestIT.HttpHandler call() throws Exception {
            if ((requestData.length) <= 0) {
                NettyHttpTestIT.LOG.error("HttpRequest not received byte[]");
                throw new BadRequestException("HttpRequest not received byte[]");
            }
            NettyHttpTestIT.LOG.info(NettyHttpTestIT.bytesToString(sendingArray, ":"));
            NettyHttpTestIT.LOG.info(NettyHttpTestIT.bytesToString(requestData, ":"));
            Assert.assertEquals(ByteBuffer.wrap(sendingArray), ByteBuffer.wrap(requestData));
            NettyHttpTestIT.LOG.info("Received array equalse sent array");
            response = new io.netty.handler.codec.http.DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.copiedBuffer(requestData));
            response.headers().set(CONTENT_TYPE, NettyHttpTestIT.HTTP_RESPONSE_CONTENT_TYPE);
            response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
            response.headers().set(CONNECTION, CLOSE);
            return this;
        }

        public void setHttpRequest(HttpRequest request) throws IOException, BadRequestException {
            if (request == null) {
                NettyHttpTestIT.LOG.error("HttpRequest not initialized");
                throw new BadRequestException("HttpRequest not initialized");
            }
            if (!(request.getMethod().equals(POST))) {
                NettyHttpTestIT.LOG.error("Got invalid HTTP method: expecting only POST");
                throw new BadRequestException((("Incorrect method " + (request.getMethod().toString())) + ", expected POST"));
            }
            HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(factory, request);
            InterfaceHttpData data = decoder.getBodyHttpData(NettyHttpTestIT.HTTP_TEST_ATTRIBUTE);
            if (data == null) {
                NettyHttpTestIT.LOG.error("HTTP Resolve request inccorect, {} attribute not found", NettyHttpTestIT.HTTP_TEST_ATTRIBUTE);
                throw new BadRequestException((("HTTP Resolve request inccorect, " + (NettyHttpTestIT.HTTP_TEST_ATTRIBUTE)) + " attribute not found"));
            }
            Attribute attribute = ((Attribute) (data));
            requestData = attribute.get();
            NettyHttpTestIT.LOG.trace("Name {}, type {} found, data size {}", data.getName(), data.getHttpDataType().name(), requestData.length);
        }

        public HttpResponse getHttpResponse() {
            return response;
        }
    }

    /**
     * Netty exception handler.
     *
     * @author Andrey Panasenko
     */
    public class DefaultExceptionHandler extends ChannelInboundHandlerAdapter {
        @Override
        public final void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
            NettyHttpTestIT.LOG.error("Exception caught", cause);
            HttpResponseStatus status;
            if (cause instanceof BadRequestException) {
                status = BAD_REQUEST;
            } else {
                status = INTERNAL_SERVER_ERROR;
            }
            String content = cause.getMessage();
            FullHttpResponse response = new io.netty.handler.codec.http.DefaultFullHttpResponse(HTTP_1_1, status, Unpooled.copiedBuffer(content, UTF_8));
            response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
            response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
            response.headers().set(CONNECTION, CLOSE);
            ChannelFuture future = ctx.writeAndFlush(response);
            future.addListener(ChannelFutureListener.CLOSE);
            ctx.close();
        }
    }

    /**
     * Test client.
     * Generate HTTP request.
     * Open URLConnection to Netty.
     * Return bytes[] from HTTP response body.
     *
     * @author Andrey Panasenko
     */
    public class HttpTestClient implements Runnable {
        /**
         * boundary size
         */
        public static final int BOUNDARY_LENGTH = 35;

        /**
         * ContentType constant string
         */
        public static final String CONTENT_TYPE_CONST = "multipart/form-data; boundary=";

        /**
         * ContentDisposition constant string
         */
        public static final String CONTENT_DISPOSITION = "Content-Disposition: form-data; ";

        /**
         * Content name filed
         */
        public static final String CONTENT_NAME = "name=";

        /**
         * CRLF
         */
        public static final String crlf = "\r\n";

        /**
         * The pool of ASCII chars to be used for generating a multipart boundary.
         */
        private final char[] MULTIPART_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

        /**
         * Generated boundary
         */
        private String boundary;

        /**
         * Random number generator
         */
        private Random rnd = new Random();

        private HttpURLConnection connection;

        private byte[] object;

        private byte[] response;

        private boolean responseComplete = false;

        private Object sync = new Object();

        public HttpTestClient(String host, int port, byte[] object) throws IOException, MalformedURLException {
            String url = ((("http://" + host) + ":") + port) + "/test";
            connection = ((HttpURLConnection) (new URL(url).openConnection()));
            this.object = object;
            boundary = getRandomString(NettyHttpTestIT.HttpTestClient.BOUNDARY_LENGTH);
        }

        /* (non-Javadoc)
        @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            NettyHttpTestIT.LOG.info("Run Http test to {}", connection.getURL().toString());
            List<Byte> bodyArray = new Vector<>();
            try {
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", ((NettyHttpTestIT.HttpTestClient.CONTENT_TYPE_CONST) + (boundary)));
                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                dumbObject(NettyHttpTestIT.HTTP_TEST_ATTRIBUTE, object, out);
                out.flush();
                out.close();
                DataInputStream r = new DataInputStream(connection.getInputStream());
                while (true) {
                    bodyArray.add(new Byte(r.readByte()));
                } 
            } catch (EOFException eof) {
                response = new byte[bodyArray.size()];
                for (int i = 0; i < (response.length); i++) {
                    response[i] = bodyArray.get(i);
                }
            } catch (IOException e) {
                NettyHttpTestIT.LOG.error("Error request HTTP to {}", connection.getURL().toString());
            } finally {
                connection.disconnect();
                synchronized(sync) {
                    responseComplete = true;
                    sync.notify();
                }
            }
        }

        /**
         * Return HTTP response body.
         * Method blocks, until body received.
         *
         * @return byte[]
         */
        public byte[] getResponseBody() {
            synchronized(sync) {
                if (!(responseComplete)) {
                    try {
                        sync.wait();
                    } catch (InterruptedException e) {
                        NettyHttpTestIT.LOG.error("Error wait ", e);
                    }
                }
            }
            return response;
        }

        /**
         * generate random String.
         *
         * @param int
         * 		size of String
         * @return random String
         */
        public String getRandomString(int size) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < size; i++) {
                int j = rnd.nextInt(MULTIPART_CHARS.length);
                sb.append(MULTIPART_CHARS[j]);
            }
            return sb.toString();
        }

        /**
         * Write multipart entity
         *
         * @param name
         * 		multipart entity name
         * @param bytes
         * 		multipart entity body
         * @param out
         * 		DataOutputStream connection stream
         * @throws IOException
         * 		throws in case ot write error.
         */
        public void dumbObject(String name, byte[] bytes, DataOutputStream out) throws IOException {
            out.writeBytes((("--" + (boundary)) + (NettyHttpTestIT.HttpTestClient.crlf)));
            out.writeBytes(((((((NettyHttpTestIT.HttpTestClient.CONTENT_DISPOSITION) + (NettyHttpTestIT.HttpTestClient.CONTENT_NAME)) + "\"") + name) + "\"") + (NettyHttpTestIT.HttpTestClient.crlf)));
            out.writeBytes(("Content-Type: application/octet-stream" + (NettyHttpTestIT.HttpTestClient.crlf)));
            out.writeBytes(NettyHttpTestIT.HttpTestClient.crlf);
            out.write(bytes);
            out.writeBytes(NettyHttpTestIT.HttpTestClient.crlf);
            out.writeBytes(((("--" + (boundary)) + "--") + (NettyHttpTestIT.HttpTestClient.crlf)));
        }
    }
}

