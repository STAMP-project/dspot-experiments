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
package com.weibo.api.motan.protocol.yar;


import com.weibo.api.motan.rpc.Provider;
import com.weibo.api.motan.rpc.URL;
import com.weibo.api.motan.transport.MessageHandler;
import com.weibo.api.motan.transport.ProviderMessageRouter;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @unknown YarRpcProtocolTest
 * @author zhanglei
 * @unknown 2016?7?27?
 */
public class YarRpcProtocolTest {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testInitRequestRouter() {
        YarRpcProtocol protocol = new YarRpcProtocol();
        URL url = new URL("motan", "localhost", 8002, "urlpath");
        Provider provider = new com.weibo.api.motan.rpc.DefaultProvider(null, url, MessageHandler.class);
        ProviderMessageRouter router = protocol.initRequestRouter(url, provider);
        Assert.assertNotNull(router);
        URL url2 = new URL("motan", "localhost", 8003, "urlpath2");
        Provider provider2 = new com.weibo.api.motan.rpc.DefaultProvider(null, url2, MessageHandler.class);
        ProviderMessageRouter router2 = protocol.initRequestRouter(url2, provider2);
        Assert.assertNotNull(router2);
        Assert.assertFalse(router2.equals(router));
        URL url3 = new URL("motan", "localhost", 8002, "urlpath3");
        Provider provider3 = new com.weibo.api.motan.rpc.DefaultProvider(null, url3, MessageHandler.class);
        ProviderMessageRouter router3 = protocol.initRequestRouter(url3, provider3);
        Assert.assertNotNull(router3);
        Assert.assertTrue(router3.equals(router));
        try {
            protocol.initRequestRouter(url, provider);
            Assert.assertTrue(false);
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("duplicate yar provider"));
        }
    }
}

