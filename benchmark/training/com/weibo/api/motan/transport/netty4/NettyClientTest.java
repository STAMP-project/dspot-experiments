/**
 * Copyright 2009-2016 Weibo, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.weibo.api.motan.transport.netty4;


import MotanConstants.ASYNC_SUFFIX;
import URLParamType.minClientConnection;
import URLParamType.requestTimeout;
import com.weibo.api.motan.exception.MotanServiceException;
import com.weibo.api.motan.transport.Channel;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author maijunsheng
 * @author sunnights
 */
public class NettyClientTest {
    private NettyServer nettyServer;

    private NettyClient nettyClient;

    private DefaultRequest request;

    private URL url;

    private String interfaceName = "com.weibo.api.motan.protocol.example.IHello";

    @Test
    public void testNormal() throws InterruptedException {
        nettyClient = new NettyClient(url);
        nettyClient.open();
        Response response;
        try {
            response = nettyClient.request(request);
            Object result = response.getValue();
            Assert.assertNotNull(result);
            Assert.assertEquals(((("method: " + (request.getMethodName())) + " requestId: ") + (request.getRequestId())), result);
        } catch (MotanServiceException e) {
            Assert.assertTrue(false);
        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testAsync() {
        nettyClient = new NettyClient(url);
        nettyClient.open();
        RpcContext.getContext().putAttribute(ASYNC_SUFFIX, true);
        Response response;
        try {
            response = nettyClient.request(request);
            Assert.assertTrue((response instanceof ResponseFuture));
            Object result = response.getValue();
            RpcContext.destroy();
            Assert.assertNotNull(result);
            Assert.assertEquals(((("method: " + (request.getMethodName())) + " requestId: ") + (request.getRequestId())), result);
        } catch (MotanServiceException e) {
            Assert.assertTrue(false);
        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testAbNormal() throws InterruptedException {
        // requestTimeout ???????0
        url.addParameter(requestTimeout.getName(), requestTimeout.getValue());
        nettyClient = new NettyClient(url);
        // nettyClient???????INIT
        try {
            nettyClient.request(request);
            Assert.fail("Netty Client should not be active!");
        } catch (MotanServiceException e) {
            Assert.assertTrue(true);
        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testAbNormal2() throws InterruptedException {
        // ?????????????????????client??????
        url.addParameter(minClientConnection.getName(), "1");
        url.addParameter(requestTimeout.getName(), "1");
        NettyClientTest.NettyTestClient nettyClient = new NettyClientTest.NettyTestClient(url);
        this.nettyClient = nettyClient;
        open();
        try {
            nettyClient.getChannel0();
        } catch (Exception e) {
        }
        Thread.sleep(3000);
        try {
            nettyClient.request(request);
        } catch (MotanServiceException e) {
            Assert.assertFalse(isAvailable());
            resetErrorCount();
            Assert.assertTrue(isAvailable());
        } catch (Exception e) {
        }
    }

    @Test
    public void testClient() throws InterruptedException {
        nettyServer.close();
        NettyClientTest.NettyTestClient nettyClient = new NettyClientTest.NettyTestClient(url);
        this.nettyClient = nettyClient;
        open();
        for (Channel channel : nettyClient.getChannels()) {
            Assert.assertFalse(channel.isAvailable());
        }
        nettyServer.open();
        try {
            nettyClient.request(request);
        } catch (Exception e) {
        }
        Thread.sleep(3000);
        for (Channel channel : nettyClient.getChannels()) {
            Assert.assertTrue(channel.isAvailable());
        }
    }

    class NettyTestClient extends NettyClient {
        public NettyTestClient(URL url) {
            super(url);
        }

        public ArrayList<Channel> getChannels() {
            return super.channels;
        }

        public Channel getChannel0() {
            return getChannel();
        }
    }
}

