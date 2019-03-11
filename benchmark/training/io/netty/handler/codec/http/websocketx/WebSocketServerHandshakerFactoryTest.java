/**
 * Copyright 2015 The Netty Project
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
package io.netty.handler.codec.http.websocketx;


import HttpHeaderNames.SEC_WEBSOCKET_VERSION;
import HttpResponseStatus.UPGRADE_REQUIRED;
import WebSocketVersion.V13;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.util.ReferenceCountUtil;
import org.junit.Assert;
import org.junit.Test;


public class WebSocketServerHandshakerFactoryTest {
    @Test
    public void testUnsupportedVersion() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel();
        WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ch);
        ch.runPendingTasks();
        Object msg = ch.readOutbound();
        if (!(msg instanceof FullHttpResponse)) {
            Assert.fail(("Got wrong response " + msg));
        }
        FullHttpResponse response = ((FullHttpResponse) (msg));
        Assert.assertEquals(UPGRADE_REQUIRED, response.status());
        Assert.assertEquals(V13.toHttpHeaderValue(), response.headers().get(SEC_WEBSOCKET_VERSION));
        Assert.assertTrue(HttpUtil.isContentLengthSet(response));
        Assert.assertEquals(0, HttpUtil.getContentLength(response));
        ReferenceCountUtil.release(response);
        Assert.assertFalse(ch.finish());
    }
}

