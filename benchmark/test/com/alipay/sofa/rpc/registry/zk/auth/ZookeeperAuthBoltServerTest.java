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
package com.alipay.sofa.rpc.registry.zk.auth;


import RpcConstants.REGISTRY_PROTOCOL_ZK;
import com.alipay.sofa.rpc.config.ConsumerConfig;
import com.alipay.sofa.rpc.config.ProviderConfig;
import com.alipay.sofa.rpc.config.RegistryConfig;
import com.alipay.sofa.rpc.config.ServerConfig;
import com.alipay.sofa.rpc.log.Logger;
import com.alipay.sofa.rpc.log.LoggerFactory;
import com.alipay.sofa.rpc.test.EchoService;
import com.alipay.sofa.rpc.test.EchoServiceImpl;
import java.util.HashMap;
import java.util.Map;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.KeeperException;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author <a href="mailto:jjzxjgy@126.com">jianyang</a>
 */
public class ZookeeperAuthBoltServerTest {
    protected static final Logger LOGGER = LoggerFactory.getLogger(ZookeeperAuthBoltServerTest.class);

    private static ServerConfig serverConfig;

    private static RegistryConfig registryConfig;

    private static Map<String, String> parameters = new HashMap<String, String>();

    /**
     * Zookeeper zkClient
     */
    private static CuratorFramework zkClient;

    @Test
    public void testUseCorrentAuth() {
        ZookeeperAuthBoltServerTest.parameters.put("scheme", "digest");
        // ???????????????????user1:passwd1,user2:passwd2
        ZookeeperAuthBoltServerTest.parameters.put("addAuth", "sofazk:rpc1");
        ZookeeperAuthBoltServerTest.registryConfig = new RegistryConfig().setProtocol(REGISTRY_PROTOCOL_ZK).setAddress("127.0.0.1:2181/authtest").setParameters(ZookeeperAuthBoltServerTest.parameters);
        ZookeeperAuthBoltServerTest.serverConfig = // ?????????12200
        // ?????????bolt
        new ServerConfig().setProtocol("bolt").setPort(12200).setDaemon(false);// ?????

        ProviderConfig<EchoService> providerConfig = // ????
        // ????
        new ProviderConfig<EchoService>().setRegistry(ZookeeperAuthBoltServerTest.registryConfig).setInterfaceId(EchoService.class.getName()).setRef(new EchoServiceImpl()).setServer(ZookeeperAuthBoltServerTest.serverConfig);// ?????

        providerConfig.export();// ????

        ConsumerConfig<EchoService> consumerConfig = // ????
        // ????
        new ConsumerConfig<EchoService>().setRegistry(ZookeeperAuthBoltServerTest.registryConfig).setInterfaceId(EchoService.class.getName()).setProtocol("bolt").setTimeout(3000).setConnectTimeout((10 * 1000));
        EchoService echoService = consumerConfig.refer();
        String result = echoService.echoStr("auth test");
        Assert.assertEquals("auth test", result);
    }

    @Test
    public void testUseNoMatchAuth() {
        ZookeeperAuthBoltServerTest.parameters.put("scheme", "digest");
        // ???????????????????user1:passwd1,user2:passwd2
        ZookeeperAuthBoltServerTest.parameters.put("addAuth", "sofazk:rpc2");
        ZookeeperAuthBoltServerTest.registryConfig = new RegistryConfig().setProtocol(REGISTRY_PROTOCOL_ZK).setAddress("127.0.0.1:2181/authtest").setParameters(ZookeeperAuthBoltServerTest.parameters);
        ZookeeperAuthBoltServerTest.serverConfig = // ?????????12200
        // ?????????bolt
        new ServerConfig().setProtocol("bolt").setPort(12200).setDaemon(false);// ?????

        ProviderConfig<EchoService> providerConfig = // ????
        // ????
        new ProviderConfig<EchoService>().setRegistry(ZookeeperAuthBoltServerTest.registryConfig).setInterfaceId(EchoService.class.getName()).setRef(new EchoServiceImpl()).setServer(ZookeeperAuthBoltServerTest.serverConfig);// ?????

        try {
            providerConfig.export();// ????

            Assert.fail("auth is not right, but publish success");
        } catch (Exception ex) {
            ZookeeperAuthBoltServerTest.LOGGER.error("exception is", ex);
            if ((ex.getCause()) instanceof KeeperException.NoAuthException) {
                Assert.assertTrue(true);
            } else {
                Assert.fail("auth is not right, but throw not auth error exception");
            }
        }
        ConsumerConfig<EchoService> consumerConfig = // ????
        // ????
        new ConsumerConfig<EchoService>().setRegistry(ZookeeperAuthBoltServerTest.registryConfig).setInterfaceId(EchoService.class.getName()).setProtocol("bolt").setTimeout(3000).setConnectTimeout((10 * 1000));
        try {
            consumerConfig.refer();// ????

            Assert.fail("auth is not right, but consumer refer success");
        } catch (Exception ex) {
            ZookeeperAuthBoltServerTest.LOGGER.error("exception is", ex);
            if ((ex.getCause()) instanceof KeeperException.NoAuthException) {
                Assert.assertTrue(true);
            } else {
                Assert.fail("auth is not right, but throw not auth error exception");
            }
        }
    }
}

