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
package com.weibo.api.motan.transport.netty;


import MotanConstants.ASYNC_SUFFIX;
import URLParamType.maxClientConnection;
import URLParamType.requestTimeout;
import com.weibo.api.motan.exception.MotanServiceException;
import junit.framework.Assert;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


/**
 *
 *
 * @author maijunsheng
 * @version ?????2013-6-7
 */
public class NettyClientTest {
    private NettyServer nettyServer;

    private NettyClient nettyClient;

    private DefaultRequest request;

    private URL url;

    @Test
    public void testNormal() {
        nettyClient = new NettyClient(url);
        nettyClient.open();
        Response response;
        try {
            response = nettyClient.request(request);
            Object result = response.getValue();
            Assert.assertNotNull(result);
            Assert.assertEquals(((("method: " + (request.getMethodName())) + " requestId: ") + (request.getRequestId())), result);
        } catch (MotanServiceException e) {
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(false);
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
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    @Test
    public void testAbNormal() {
        // requestTimeout ???????0
        url.addParameter(requestTimeout.getName(), requestTimeout.getValue());
        nettyClient = new NettyClient(url);
        // nettyClient???????INIT
        try {
            nettyClient.request(request);
            fail("Netty Client should not be active!");
        } catch (MotanServiceException e) {
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(false);
        }
        // ?????????????????????client??????
        url.addParameter(maxClientConnection.getName(), "1");
        url.addParameter(requestTimeout.getName(), "1");
        nettyClient = new NettyClient(url);
        nettyClient.open();
        try {
            nettyClient.request(request);
        } catch (MotanServiceException e) {
            assertFalse(nettyClient.isAvailable());
            nettyClient.resetErrorCount();
            assertTrue(nettyClient.isAvailable());
        } catch (Exception e) {
        }
    }
}

