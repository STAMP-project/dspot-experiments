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


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
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
@SpringBootTest(classes = AnsAutoServiceRegistrationTests.TestConfig.class, properties = { "spring.application.name=myTestService1", "spring.cloud.alicloud.ans.server-list=127.0.0.1", "spring.cloud.alicloud.ans.server-port=8080", "spring.cloud.alicloud.ans.secure=true", "spring.cloud.alicloud.ans.endpoint=test-endpoint" }, webEnvironment = RANDOM_PORT)
public class AnsAutoServiceRegistrationTests {
    @Autowired
    private AnsRegistration registration;

    @Autowired
    private AnsAutoServiceRegistration ansAutoServiceRegistration;

    @LocalServerPort
    private int port;

    @Autowired
    private AnsProperties properties;

    @Autowired
    private InetUtils inetUtils;

    @Test
    public void contextLoads() throws Exception {
        Assert.assertNotNull("AnsRegistration was not created", registration);
        Assert.assertNotNull("AnsProperties was not created", properties);
        Assert.assertNotNull("AnsAutoServiceRegistration was not created", ansAutoServiceRegistration);
        checkoutAnsDiscoveryServerList();
        checkoutAnsDiscoveryServerPort();
        checkoutAnsDiscoveryServiceName();
        checkoutAnsDiscoveryServiceIP();
        checkoutAnsDiscoveryServicePort();
        checkoutAnsDiscoverySecure();
        checkAutoRegister();
        checkoutEndpoint();
    }

    @Configuration
    @EnableAutoConfiguration
    @ImportAutoConfiguration({ AutoServiceRegistrationConfiguration.class, AnsDiscoveryClientAutoConfiguration.class, AnsAutoConfiguration.class })
    public static class TestConfig {}
}

