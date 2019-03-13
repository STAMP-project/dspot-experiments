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
package com.alipay.sofa.isle.spring.health;


import HealthCheckConstants.SOFABOOT_MODULE_CHECK_RETRY_COUNT;
import HealthCheckConstants.SOFABOOT_MODULE_CHECK_RETRY_DEFAULT_COUNT;
import HealthCheckConstants.SOFABOOT_MODULE_CHECK_RETRY_DEFAULT_INTERVAL;
import HealthCheckConstants.SOFABOOT_MODULE_CHECK_RETRY_INTERVAL;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;


/**
 *
 *
 * @author abby.zh
 * @since 2.4.10
 */
public class SofaModuleHealthCheckerTest {
    private ConfigurableApplicationContext applicationContext;

    @Test
    public void testDefaultConfig() {
        SpringApplication springApplication = new SpringApplication(SofaModuleHealthCheckerTest.EmptyConfiguration.class);
        this.applicationContext = springApplication.run(new String[]{  });
        SofaModuleHealthChecker sofaModuleHealthChecker = applicationContext.getBean(SofaModuleHealthChecker.class);
        Assert.assertEquals(SOFABOOT_MODULE_CHECK_RETRY_DEFAULT_COUNT, sofaModuleHealthChecker.getRetryCount());
        Assert.assertEquals(SOFABOOT_MODULE_CHECK_RETRY_DEFAULT_INTERVAL, sofaModuleHealthChecker.getRetryTimeInterval());
    }

    @Test
    public void testCustomConfig() {
        int customRetryCount = 10;
        int customRetryInterval = 30;
        Map<String, Object> properties = new HashMap<>();
        properties.put(SOFABOOT_MODULE_CHECK_RETRY_COUNT, customRetryCount);
        properties.put(SOFABOOT_MODULE_CHECK_RETRY_INTERVAL, customRetryInterval);
        SpringApplication springApplication = new SpringApplication(SofaModuleHealthCheckerTest.EmptyConfiguration.class);
        springApplication.setDefaultProperties(properties);
        this.applicationContext = springApplication.run(new String[]{  });
        SofaModuleHealthChecker sofaModuleHealthChecker = applicationContext.getBean(SofaModuleHealthChecker.class);
        Assert.assertEquals(customRetryCount, sofaModuleHealthChecker.getRetryCount());
        Assert.assertEquals(customRetryInterval, sofaModuleHealthChecker.getRetryTimeInterval());
    }

    @EnableAutoConfiguration
    @Configuration
    static class EmptyConfiguration {}
}

