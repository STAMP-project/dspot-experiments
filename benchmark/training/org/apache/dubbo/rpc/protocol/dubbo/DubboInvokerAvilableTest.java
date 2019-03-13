/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.rpc.protocol.dubbo;


import Constants.CHANNEL_ATTRIBUTE_READONLY_KEY;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.remoting.exchange.ExchangeClient;
import org.apache.dubbo.rpc.ProxyFactory;
import org.apache.dubbo.rpc.protocol.dubbo.support.ProtocolUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Check available status for dubboInvoker
 */
public class DubboInvokerAvilableTest {
    private static DubboProtocol protocol = DubboProtocol.getDubboProtocol();

    private static ProxyFactory proxy = ExtensionLoader.getExtensionLoader(ProxyFactory.class).getAdaptiveExtension();

    @Test
    public void test_Normal_available() {
        URL url = URL.valueOf("dubbo://127.0.0.1:20883/org.apache.dubbo.rpc.protocol.dubbo.IDemoService");
        ProtocolUtils.export(new DubboInvokerAvilableTest.DemoServiceImpl(), IDemoService.class, url);
        DubboInvoker<?> invoker = ((DubboInvoker<?>) (DubboInvokerAvilableTest.protocol.refer(IDemoService.class, url)));
        Assertions.assertEquals(true, invoker.isAvailable());
        invoker.destroy();
        Assertions.assertEquals(false, invoker.isAvailable());
    }

    @Test
    public void test_Normal_ChannelReadOnly() throws Exception {
        URL url = URL.valueOf("dubbo://127.0.0.1:20883/org.apache.dubbo.rpc.protocol.dubbo.IDemoService");
        ProtocolUtils.export(new DubboInvokerAvilableTest.DemoServiceImpl(), IDemoService.class, url);
        DubboInvoker<?> invoker = ((DubboInvoker<?>) (DubboInvokerAvilableTest.protocol.refer(IDemoService.class, url)));
        Assertions.assertEquals(true, invoker.isAvailable());
        getClients(invoker)[0].setAttribute(CHANNEL_ATTRIBUTE_READONLY_KEY, Boolean.TRUE);
        Assertions.assertEquals(false, invoker.isAvailable());
        // reset status since connection is shared among invokers
        getClients(invoker)[0].removeAttribute(CHANNEL_ATTRIBUTE_READONLY_KEY);
    }

    @Test
    public void test_NoInvokers() throws Exception {
        URL url = URL.valueOf("dubbo://127.0.0.1:20883/org.apache.dubbo.rpc.protocol.dubbo.IDemoService?connections=1");
        ProtocolUtils.export(new DubboInvokerAvilableTest.DemoServiceImpl(), IDemoService.class, url);
        DubboInvoker<?> invoker = ((DubboInvoker<?>) (DubboInvokerAvilableTest.protocol.refer(IDemoService.class, url)));
        ExchangeClient[] clients = getClients(invoker);
        clients[0].close();
        Assertions.assertEquals(false, invoker.isAvailable());
    }

    @Test
    public void test_Lazy_ChannelReadOnly() throws Exception {
        URL url = URL.valueOf("dubbo://127.0.0.1:20883/org.apache.dubbo.rpc.protocol.dubbo.IDemoService?lazy=true&connections=1&timeout=10000");
        ProtocolUtils.export(new DubboInvokerAvilableTest.DemoServiceImpl(), IDemoService.class, url);
        DubboInvoker<?> invoker = ((DubboInvoker<?>) (DubboInvokerAvilableTest.protocol.refer(IDemoService.class, url)));
        Assertions.assertEquals(true, invoker.isAvailable());
        try {
            getClients(invoker)[0].setAttribute(CHANNEL_ATTRIBUTE_READONLY_KEY, Boolean.TRUE);
            Assertions.fail();
        } catch (IllegalStateException e) {
        }
        // invoke method --> init client
        IDemoService service = ((IDemoService) (DubboInvokerAvilableTest.proxy.getProxy(invoker)));
        Assertions.assertEquals("ok", service.get());
        Assertions.assertEquals(true, invoker.isAvailable());
        getClients(invoker)[0].setAttribute(CHANNEL_ATTRIBUTE_READONLY_KEY, Boolean.TRUE);
        Assertions.assertEquals(false, invoker.isAvailable());
    }

    public class DemoServiceImpl implements IDemoService {
        public String get() {
            return "ok";
        }
    }

    public class DemoServiceImpl0 implements IDemoService {
        public String get() {
            return "ok";
        }
    }
}

