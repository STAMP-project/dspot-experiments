/**
 * Copyright 2009-2016 Weibo, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.weibo.api.motan.transport.netty4.yar;


import HttpResponseStatus.OK;
import com.weibo.api.motan.protocol.yar.AttachmentRequest;
import com.weibo.api.motan.protocol.yar.YarMessageRouter;
import com.weibo.api.motan.protocol.yar.YarProtocolUtil;
import com.weibo.api.motan.rpc.DefaultResponse;
import com.weibo.api.motan.rpc.Request;
import com.weibo.api.motan.rpc.Response;
import com.weibo.api.motan.rpc.URL;
import com.weibo.api.motan.transport.Channel;
import com.weibo.api.motan.transport.MessageHandler;
import com.weibo.api.motan.transport.TransportException;
import com.weibo.api.motan.transport.netty4.http.Netty4HttpServer;
import com.weibo.yar.YarRequest;
import com.weibo.yar.YarResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @unknown YarMessageHandlerWarpperTest
 * @author zhanglei
 * @unknown 2016?7?27?
 */
public class YarMessageHandlerWarpperTest {
    public String uri = "/testpath?param1=a&param2=b&param3=c";

    @Test
    public void testHandle() throws Exception {
        YarRequest yarRequest = new YarRequest(123, "JSON", "testmethod", new Object[]{ "params", 456 });
        final YarResponse yarResponse = YarProtocolUtil.buildDefaultErrorResponse("test err", "JSON");
        YarMessageHandlerWarpper handler = new YarMessageHandlerWarpper(new YarMessageRouter() {
            @Override
            public Object handle(Channel channel, Object message) {
                AttachmentRequest request = ((AttachmentRequest) (message));
                verifyAttachments(request.getAttachments());
                return yarResponse;
            }
        });
        FullHttpResponse httpResponse = ((FullHttpResponse) (handler.handle(new YarMessageHandlerWarpperTest.MockChannel(), buildHttpRequest(yarRequest, uri))));
        Assert.assertNotNull(httpResponse);
        Assert.assertNotNull(httpResponse.content());
        YarResponse retYarResponse = getYarResponse(httpResponse);
        Assert.assertNotNull(retYarResponse);
        Assert.assertEquals(yarResponse, retYarResponse);
    }

    @Test
    public void testAbnormal() throws Exception {
        final String errmsg = "rpc process error";
        YarMessageHandlerWarpper handler = new YarMessageHandlerWarpper(new YarMessageRouter() {
            @Override
            public Object handle(Channel channel, Object message) {
                throw new RuntimeException(errmsg);
            }
        });
        // yar??????
        FullHttpResponse httpResponse = ((FullHttpResponse) (handler.handle(new YarMessageHandlerWarpperTest.MockChannel(), buildHttpRequest(null, uri))));
        Assert.assertNotNull(httpResponse);
        Assert.assertEquals(OK, httpResponse.getStatus());
        YarResponse retYarResponse = getYarResponse(httpResponse);
        Assert.assertNotNull(retYarResponse);
        Assert.assertNotNull(retYarResponse.getError());
        // yar????????????????
        YarRequest yarRequest = new YarRequest(123, "JSON", "testmethod", new Object[]{ "params", 456 });
        httpResponse = ((FullHttpResponse) (handler.handle(new YarMessageHandlerWarpperTest.MockChannel(), buildHttpRequest(yarRequest, uri))));
        Assert.assertNotNull(httpResponse);
        Assert.assertEquals(OK, httpResponse.getStatus());
        retYarResponse = getYarResponse(httpResponse);
        Assert.assertNotNull(retYarResponse);
        Assert.assertEquals(errmsg, retYarResponse.getError());
    }

    class MockChannel extends Netty4HttpServer {
        public MockChannel() {
            super(null, null);
        }

        public MockChannel(URL url, MessageHandler messageHandler) {
            super(url, messageHandler);
        }

        @Override
        public Response request(Request request) throws TransportException {
            return new DefaultResponse();
        }
    }
}

