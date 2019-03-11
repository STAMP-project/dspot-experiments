/**
 * Copyright (C) 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.alicloud.ans.registry;


import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.alicloud.ans.AnsAutoConfiguration;
import org.springframework.cloud.alicloud.ans.AnsDiscoveryClientAutoConfiguration;
import org.springframework.cloud.alicloud.context.ans.AnsProperties;
import org.springframework.cloud.client.serviceregistry.AutoServiceRegistrationConfiguration;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;


/**
 *
 *
 * @author xiaojing
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AnsAutoServiceRegistrationIpNetworkInterfaceTests.TestConfig.class, properties = { "spring.application.name=myTestService1", "spring.cloud.alicloud.ans.server-list=127.0.0.1", "spring.cloud.alicloud.ans.server-port=8080" }, webEnvironment = RANDOM_PORT)
public class AnsAutoServiceRegistrationIpNetworkInterfaceTests {
    @Autowired
    private AnsRegistration registration;

    @Autowired
    private AnsAutoServiceRegistration ansAutoServiceRegistration;

    @Autowired
    private AnsProperties properties;

    @Autowired
    private InetUtils inetUtils;

    @Test
    public void contextLoads() throws Exception {
        Assert.assertNotNull("AnsRegistration was not created", registration);
        Assert.assertNotNull("AnsProperties was not created", properties);
        Assert.assertNotNull("AnsAutoServiceRegistration was not created", ansAutoServiceRegistration);
        checkoutAnsDiscoveryServiceIP();
    }

    @Configuration
    @EnableAutoConfiguration
    @ImportAutoConfiguration({ AutoServiceRegistrationConfiguration.class, AnsDiscoveryClientAutoConfiguration.class, AnsAutoConfiguration.class })
    public static class TestConfig {
        static boolean hasValidNetworkInterface = false;

        static String netWorkInterfaceName;

        static {
            try {
                Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
                while ((enumeration.hasMoreElements()) && (!(AnsAutoServiceRegistrationIpNetworkInterfaceTests.TestConfig.hasValidNetworkInterface))) {
                    NetworkInterface networkInterface = enumeration.nextElement();
                    Enumeration<InetAddress> inetAddress = networkInterface.getInetAddresses();
                    while (inetAddress.hasMoreElements()) {
                        InetAddress currentAddress = inetAddress.nextElement();
                        if ((currentAddress instanceof Inet4Address) && (!(currentAddress.isLoopbackAddress()))) {
                            AnsAutoServiceRegistrationIpNetworkInterfaceTests.TestConfig.hasValidNetworkInterface = true;
                            AnsAutoServiceRegistrationIpNetworkInterfaceTests.TestConfig.netWorkInterfaceName = networkInterface.getName();
                            System.setProperty("spring.cloud.alicloud.ans.client-interface-name", networkInterface.getName());
                            break;
                        }
                    } 
                } 
            } catch (Exception e) {
            }
        }
    }
}

