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
package com.alipay.sofa.rpc.test.async;


import RpcConstants.INVOKER_TYPE_CALLBACK;
import com.alipay.sofa.rpc.config.ConsumerConfig;
import com.alipay.sofa.rpc.config.ProviderConfig;
import com.alipay.sofa.rpc.config.ServerConfig;
import com.alipay.sofa.rpc.context.RpcInvokeContext;
import com.alipay.sofa.rpc.core.exception.SofaRpcException;
import com.alipay.sofa.rpc.log.Logger;
import com.alipay.sofa.rpc.log.LoggerFactory;
import com.alipay.sofa.rpc.test.ActivelyDestroyTest;
import com.alipay.sofa.rpc.test.HelloService;
import com.alipay.sofa.rpc.test.HelloServiceImpl;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author <a href="mailto:zhanggeng.zg@antfin.com">GengZhang</a>
 */
public class AsyncChainTest extends ActivelyDestroyTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncChainTest.class);

    @Test
    public void testAll() {
        ServerConfig serverConfig2 = new ServerConfig().setPort(22222).setDaemon(false);
        // C??????
        ProviderConfig<HelloService> CProvider = new ProviderConfig<HelloService>().setInterfaceId(HelloService.class.getName()).setRef(new HelloServiceImpl(1000)).setServer(serverConfig2);
        CProvider.export();
        // B?C????
        ConsumerConfig<HelloService> BConsumer = // .setOnReturn() // ??? ??????
        new ConsumerConfig<HelloService>().setInterfaceId(HelloService.class.getName()).setInvokeType(INVOKER_TYPE_CALLBACK).setTimeout(3000).setDirectUrl("bolt://127.0.0.1:22222");
        HelloService helloService = BConsumer.refer();
        // B??????
        ServerConfig serverConfig3 = new ServerConfig().setPort(22223).setDaemon(false);
        ProviderConfig<AsyncHelloService> BProvider = new ProviderConfig<AsyncHelloService>().setInterfaceId(AsyncHelloService.class.getName()).setRef(new AsyncHelloServiceImpl(helloService)).setServer(serverConfig3);
        BProvider.export();
        // A?B????
        ConsumerConfig<AsyncHelloService> AConsumer = new ConsumerConfig<AsyncHelloService>().setInterfaceId(AsyncHelloService.class.getName()).setInvokeType(INVOKER_TYPE_CALLBACK).setTimeout(3000).setDirectUrl("bolt://127.0.0.1:22223");
        AsyncHelloService asyncHelloService = AConsumer.refer();
        final CountDownLatch[] latch = new CountDownLatch[1];
        latch[0] = new CountDownLatch(1);
        final Object[] ret = new Object[1];
        // ???????--??
        RpcInvokeContext.getContext().setResponseCallback(buildCallback(ret, latch));
        String ret0 = asyncHelloService.sayHello("xxx", 22);
        Assert.assertNull(ret0);// ?????null

        try {
            latch[0].await(5000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ignore) {
        }
        Assert.assertTrue(((ret[0]) instanceof String));
        // ???????--????
        ret[0] = null;
        latch[0] = new CountDownLatch(1);
        RpcInvokeContext.getContext().setResponseCallback(buildCallback(ret, latch));
        ret0 = asyncHelloService.appException("xxx");
        Assert.assertNull(ret0);// ?????null

        try {
            latch[0].await(5000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ignore) {
        }
        Assert.assertTrue(((ret[0]) instanceof RuntimeException));
        // ???????--rpc??
        ret[0] = null;
        latch[0] = new CountDownLatch(1);
        RpcInvokeContext.getContext().setResponseCallback(buildCallback(ret, latch));
        ret0 = asyncHelloService.rpcException("xxx");
        Assert.assertNull(ret0);// ?????null

        try {
            latch[0].await(5000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ignore) {
        }
        Assert.assertTrue(((ret[0]) instanceof SofaRpcException));
        Assert.assertTrue(getMessage().contains("bbb"));
        // ????????--??
        ConsumerConfig<AsyncHelloService> AConsumer2 = new ConsumerConfig<AsyncHelloService>().setInterfaceId(AsyncHelloService.class.getName()).setTimeout(3000).setDirectUrl("bolt://127.0.0.1:22223");
        AsyncHelloService syncHelloService = AConsumer2.refer();
        String s2 = syncHelloService.sayHello("yyy", 22);
        Assert.assertNotNull(s2);
    }
}

