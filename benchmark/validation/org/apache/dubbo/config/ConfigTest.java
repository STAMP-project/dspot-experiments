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
package org.apache.dubbo.config;


import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;
import org.apache.dubbo.service.DemoService;
import org.apache.dubbo.service.DemoServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ConfigTest {
    private ApplicationConfig applicationConfig = new ApplicationConfig("first-dubbo-test");

    private RegistryConfig registryConfig = new RegistryConfig("multicast://224.5.6.7:1234");

    @Test
    public void testConfig() {
        ServiceConfig<DemoService> service = new ServiceConfig();
        service.setApplication(applicationConfig);
        service.setRegistry(registryConfig);
        service.setInterface(DemoService.class);
        service.setRef(new DemoServiceImpl());
        service.export();
        ReferenceConfig<DemoService> reference = new ReferenceConfig();
        reference.setApplication(applicationConfig);
        reference.setRegistry(registryConfig);
        reference.setInterface(DemoService.class);
        DemoService demoService = reference.get();
        String message = demoService.sayHello("dubbo");
        Assertions.assertEquals("hello dubbo", message);
    }
}

