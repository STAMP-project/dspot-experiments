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
package com.alipay.sofa.rpc.test.client;


import com.alipay.sofa.rpc.client.AllConnectConnectionHolder;
import com.alipay.sofa.rpc.client.ClientProxyInvoker;
import com.alipay.sofa.rpc.client.Cluster;
import com.alipay.sofa.rpc.client.ProviderHelper;
import com.alipay.sofa.rpc.config.ConsumerConfig;
import com.alipay.sofa.rpc.config.ServerConfig;
import com.alipay.sofa.rpc.proxy.ProxyFactory;
import com.alipay.sofa.rpc.test.ActivelyDestroyTest;
import com.alipay.sofa.rpc.test.HelloService;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author <a href="mailto:zhanggeng.zg@antfin.com">GengZhang</a>
 */
public class AllConnectConnectionHolderTest extends ActivelyDestroyTest {
    private static ServerConfig serverConfig1;

    private static ServerConfig serverConfig2;

    @Test
    public void getAvailableClientTransport1() throws Exception {
        ConsumerConfig<HelloService> consumerConfig = new ConsumerConfig<HelloService>().setInterfaceId(HelloService.class.getName()).setDirectUrl("bolt://127.0.0.1:22221").setConnectionHolder("all").setRegister(false).setLazy(true).setTimeout(3000);
        HelloService helloService = consumerConfig.refer();
        ClientProxyInvoker invoker = ((ClientProxyInvoker) (ProxyFactory.getInvoker(helloService, consumerConfig.getProxy())));
        Cluster cluster = invoker.getCluster();
        Assert.assertTrue(((cluster.getConnectionHolder()) instanceof AllConnectConnectionHolder));
        AllConnectConnectionHolder holder = ((AllConnectConnectionHolder) (cluster.getConnectionHolder()));
        Assert.assertTrue(holder.isAvailableEmpty());
        Assert.assertNull(holder.getAvailableClientTransport(ProviderHelper.toProviderInfo("bolt://127.0.0.1:22221?serialization=hessian2")));
    }

    @Test
    public void getAvailableClientTransport2() throws Exception {
        ConsumerConfig<HelloService> consumerConfig = new ConsumerConfig<HelloService>().setInterfaceId(HelloService.class.getName()).setDirectUrl("bolt://127.0.0.1:22223,bolt://127.0.0.1:22224").setConnectionHolder("all").setRegister(false).setLazy(true).setTimeout(3000);
        HelloService helloService = consumerConfig.refer();
        ClientProxyInvoker invoker = ((ClientProxyInvoker) (ProxyFactory.getInvoker(helloService, consumerConfig.getProxy())));
        Cluster cluster = invoker.getCluster();
        Assert.assertTrue(((cluster.getConnectionHolder()) instanceof AllConnectConnectionHolder));
        AllConnectConnectionHolder holder = ((AllConnectConnectionHolder) (cluster.getConnectionHolder()));
        Assert.assertTrue(holder.isAvailableEmpty());
        Assert.assertNotNull(holder.getAvailableClientTransport(ProviderHelper.toProviderInfo("bolt://127.0.0.1:22223")));
        Assert.assertNotNull(holder.getAvailableClientTransport(ProviderHelper.toProviderInfo("bolt://127.0.0.1:22224")));
        consumerConfig.unRefer();
    }
}

