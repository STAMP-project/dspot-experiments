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


import java.util.List;
import java.util.Objects;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.common.utils.DubboAppender;
import org.apache.dubbo.common.utils.LogUtil;
import org.apache.dubbo.remoting.exchange.ExchangeClient;
import org.apache.dubbo.rpc.Exporter;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.ProxyFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ReferenceCountExchangeClientTest {
    public static ProxyFactory proxy = ExtensionLoader.getExtensionLoader(ProxyFactory.class).getAdaptiveExtension();

    private static DubboProtocol protocol = DubboProtocol.getDubboProtocol();

    Exporter<?> demoExporter;

    Exporter<?> helloExporter;

    Invoker<ReferenceCountExchangeClientTest.IDemoService> demoServiceInvoker;

    Invoker<ReferenceCountExchangeClientTest.IHelloService> helloServiceInvoker;

    ReferenceCountExchangeClientTest.IDemoService demoService;

    ReferenceCountExchangeClientTest.IHelloService helloService;

    ExchangeClient demoClient;

    ExchangeClient helloClient;

    String errorMsg = "safe guard client , should not be called ,must have a bug";

    /**
     * test connection sharing
     */
    @Test
    public void test_share_connect() {
        init(0, 1);
        Assertions.assertEquals(demoClient.getLocalAddress(), helloClient.getLocalAddress());
        Assertions.assertEquals(demoClient, helloClient);
        destoy();
    }

    /**
     * test connection not sharing
     */
    @Test
    public void test_not_share_connect() {
        init(1, 1);
        Assertions.assertNotSame(demoClient.getLocalAddress(), helloClient.getLocalAddress());
        Assertions.assertNotSame(demoClient, helloClient);
        destoy();
    }

    /**
     * test using multiple shared connections
     */
    @Test
    public void test_mult_share_connect() {
        // here a three shared connection is established between a consumer process and a provider process.
        final int shareConnectionNum = 3;
        init(0, shareConnectionNum);
        List<ReferenceCountExchangeClient> helloReferenceClientList = getReferenceClientList(helloServiceInvoker);
        Assertions.assertEquals(shareConnectionNum, helloReferenceClientList.size());
        List<ReferenceCountExchangeClient> demoReferenceClientList = getReferenceClientList(demoServiceInvoker);
        Assertions.assertEquals(shareConnectionNum, demoReferenceClientList.size());
        // because helloServiceInvoker and demoServiceInvoker use share connect? so client list must be equal
        Assertions.assertTrue(Objects.equals(helloReferenceClientList, demoReferenceClientList));
        Assertions.assertEquals(demoClient.getLocalAddress(), helloClient.getLocalAddress());
        Assertions.assertEquals(demoClient, helloClient);
        destoy();
    }

    /**
     * test counter won't count down incorrectly when invoker is destroyed for multiple times
     */
    @Test
    public void test_multi_destory() {
        init(0, 1);
        DubboAppender.doStart();
        DubboAppender.clear();
        demoServiceInvoker.destroy();
        demoServiceInvoker.destroy();
        Assertions.assertEquals("hello", helloService.hello());
        Assertions.assertEquals(0, LogUtil.findMessage(errorMsg), "should not  warning message");
        LogUtil.checkNoError();
        DubboAppender.doStop();
        destoy();
    }

    /**
     * Test against invocation still succeed even if counter has error
     */
    @Test
    public void test_counter_error() {
        init(0, 1);
        DubboAppender.doStart();
        DubboAppender.clear();
        // because the two interfaces are initialized, the ReferenceCountExchangeClient reference counter is 2
        ReferenceCountExchangeClient client = getReferenceClient(helloServiceInvoker);
        // close once, counter counts down from 2 to 1, no warning occurs
        client.close();
        Assertions.assertEquals("hello", helloService.hello());
        Assertions.assertEquals(0, LogUtil.findMessage(errorMsg), "should not warning message");
        // generally a client can only be closed once, here it is closed twice, counter is incorrect
        client.close();
        // wait close done.
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Assertions.fail();
        }
        // due to the effect of LazyConnectExchangeClient, the client will be "revived" whenever there is a call.
        Assertions.assertEquals("hello", helloService.hello());
        Assertions.assertEquals(1, LogUtil.findMessage(errorMsg), "should warning message");
        // output one error every 5000 invocations.
        Assertions.assertEquals("hello", helloService.hello());
        Assertions.assertEquals(1, LogUtil.findMessage(errorMsg), "should warning message");
        DubboAppender.doStop();
        // status switch to available once invoke again
        Assertions.assertEquals(true, helloServiceInvoker.isAvailable(), "client status available");
        /**
         * This is the third time to close the same client. Under normal circumstances,
         * a client value should be closed once (that is, the shutdown operation is irreversible).
         * After closing, the value of the reference counter of the client has become -1.
         *
         * But this is a bit special, because after the client is closed twice, there are several calls to helloService,
         * that is, the client inside the ReferenceCountExchangeClient is actually active, so the third shutdown here is still effective,
         * let the resurrection After the client is really closed.
         */
        client.close();
        // client has been replaced with lazy client. lazy client is fetched from referenceclientmap, and since it's
        // been invoked once, it's close status is false
        Assertions.assertEquals(false, client.isClosed(), "client status close");
        Assertions.assertEquals(false, helloServiceInvoker.isAvailable(), "client status close");
        destoy();
    }

    public interface IDemoService {
        public String demo();
    }

    public interface IHelloService {
        public String hello();
    }

    public class DemoServiceImpl implements ReferenceCountExchangeClientTest.IDemoService {
        public String demo() {
            return "demo";
        }
    }

    public class HelloServiceImpl implements ReferenceCountExchangeClientTest.IHelloService {
        public String hello() {
            return "hello";
        }
    }
}

