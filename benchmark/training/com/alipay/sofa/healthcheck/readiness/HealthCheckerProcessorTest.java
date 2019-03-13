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
package com.alipay.sofa.healthcheck.readiness;


import Status.DOWN;
import Status.UP;
import com.alipay.sofa.healthcheck.base.BaseHealthCheckTest;
import com.alipay.sofa.healthcheck.bean.DiskHealthChecker;
import com.alipay.sofa.healthcheck.bean.MemoryHealthChecker;
import com.alipay.sofa.healthcheck.bean.NetworkHealthChecker;
import com.alipay.sofa.healthcheck.configuration.SofaBootHealthCheckAutoConfiguration;
import com.alipay.sofa.healthcheck.core.HealthChecker;
import com.alipay.sofa.healthcheck.core.HealthCheckerProcessor;
import com.alipay.sofa.healthcheck.utils.HealthCheckUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 *
 *
 * @author liangen
 * @author qilong.zql
 * @version 2.3.0
 */
public class HealthCheckerProcessorTest extends BaseHealthCheckTest {
    @Configuration
    static class HealthCheckerConfiguration {
        @Bean
        public DiskHealthChecker diskHealthChecker() {
            return new DiskHealthChecker();
        }

        @Bean
        public NetworkHealthChecker networkHealthChecker(@Value("${network-health-checker.strict:false}")
        boolean strict, @Value("${network-health-checker.retry-count:0}")
        int retryCount) {
            return new NetworkHealthChecker(strict, retryCount);
        }

        @Bean
        public MemoryHealthChecker memoryHealthChecker(@Value("${memory-health-checker.count:0}")
        int count, @Value("${memory-health-checker.strict:false}")
        boolean strict, @Value("${memory-health-checker.retry-count:0}")
        int retryCount) {
            return new MemoryHealthChecker(count, strict, retryCount);
        }
    }

    @Test
    public void testInterfaceOrder() {
        initApplicationContext(new HashMap<>(), HealthCheckerProcessorTest.HealthCheckerConfiguration.class, SofaBootHealthCheckAutoConfiguration.class);
        Map<String, HealthChecker> beansOfType = applicationContext.getBeansOfType(HealthChecker.class);
        Map<String, HealthChecker> orderedResult = HealthCheckUtils.sortMapAccordingToValue(beansOfType, applicationContext.getAutowireCapableBeanFactory());
        List<String> healthCheckerId = new java.util.ArrayList(orderedResult.keySet());
        Assert.assertEquals("memoryHealthChecker", healthCheckerId.get(0));
        Assert.assertEquals("networkHealthChecker", healthCheckerId.get(1));
        Assert.assertEquals("diskHealthChecker", healthCheckerId.get(2));
    }

    @Test
    public void testReadinessCheckComponentForRetry() {
        initApplicationContext(0, true, 20);
        HashMap<String, Health> hashMap = new HashMap<>();
        HealthCheckerProcessor healthCheckerProcessor = applicationContext.getBean(HealthCheckerProcessor.class);
        MemoryHealthChecker memoryHealthChecker = applicationContext.getBean(MemoryHealthChecker.class);
        boolean result = healthCheckerProcessor.readinessHealthCheck(hashMap);
        Health memoryHealth = hashMap.get("memoryHealthChecker");
        Health networkHealth = hashMap.get("networkHealthChecker");
        Assert.assertTrue(result);
        Assert.assertTrue(((memoryHealthChecker.getCount()) == 6));
        Assert.assertTrue(((hashMap.size()) == 3));
        Assert.assertNotNull(memoryHealth);
        Assert.assertNotNull(networkHealth);
        Assert.assertTrue(memoryHealth.getStatus().equals(UP));
        Assert.assertTrue(networkHealth.getStatus().equals(UP));
        Assert.assertTrue("memory is ok".equals(memoryHealth.getDetails().get("memory")));
        Assert.assertTrue("network is ok".equals(networkHealth.getDetails().get("network")));
    }

    @Test
    public void testReadinessCheckComponentForStrict() {
        initApplicationContext(0, true, 4);
        HashMap<String, Health> hashMap = new HashMap<>();
        HealthCheckerProcessor healthCheckerProcessor = applicationContext.getBean(HealthCheckerProcessor.class);
        MemoryHealthChecker memoryHealthChecker = applicationContext.getBean(MemoryHealthChecker.class);
        boolean result = healthCheckerProcessor.readinessHealthCheck(hashMap);
        Health memoryHealth = hashMap.get("memoryHealthChecker");
        Health networkHealth = hashMap.get("networkHealthChecker");
        Assert.assertFalse(result);
        Assert.assertTrue(((memoryHealthChecker.getCount()) == 4));
        Assert.assertTrue(((hashMap.size()) == 3));
        Assert.assertNotNull(memoryHealth);
        Assert.assertNotNull(networkHealth);
        Assert.assertTrue(memoryHealth.getStatus().equals(DOWN));
        Assert.assertTrue(networkHealth.getStatus().equals(UP));
        Assert.assertTrue("memory is bad".equals(memoryHealth.getDetails().get("memory")));
        Assert.assertTrue("network is ok".equals(networkHealth.getDetails().get("network")));
    }

    @Test
    public void testStartupCheckComponentForNotStrict() {
        initApplicationContext(0, false, 4);
        HashMap<String, Health> hashMap = new HashMap<>();
        HealthCheckerProcessor healthCheckerProcessor = applicationContext.getBean(HealthCheckerProcessor.class);
        MemoryHealthChecker memoryHealthChecker = applicationContext.getBean(MemoryHealthChecker.class);
        boolean result = healthCheckerProcessor.readinessHealthCheck(hashMap);
        Health memoryHealth = hashMap.get("memoryHealthChecker");
        Health networkHealth = hashMap.get("networkHealthChecker");
        Assert.assertTrue(result);
        Assert.assertTrue(((memoryHealthChecker.getCount()) == 4));
        Assert.assertTrue(((hashMap.size()) == 3));
        Assert.assertNotNull(memoryHealth);
        Assert.assertNotNull(networkHealth);
        Assert.assertTrue(memoryHealth.getStatus().equals(DOWN));
        Assert.assertTrue(networkHealth.getStatus().equals(UP));
        Assert.assertTrue("memory is bad".equals(memoryHealth.getDetails().get("memory")));
        Assert.assertTrue("network is ok".equals(networkHealth.getDetails().get("network")));
    }

    @Test
    public void testHttpCheckComponent() {
        initApplicationContext(4, false, 5);
        HashMap<String, Health> hashMap = new HashMap<>();
        HealthCheckerProcessor healthCheckerProcessor = applicationContext.getBean(HealthCheckerProcessor.class);
        MemoryHealthChecker memoryHealthChecker = applicationContext.getBean(MemoryHealthChecker.class);
        boolean result = healthCheckerProcessor.livenessHealthCheck(hashMap);
        Health memoryHealth = hashMap.get("memoryHealthChecker");
        Health networkHealth = hashMap.get("networkHealthChecker");
        Assert.assertTrue(true);
        Assert.assertTrue(((memoryHealthChecker.getCount()) == 5));
        Assert.assertTrue(((hashMap.size()) == 3));
        Assert.assertNotNull(memoryHealth);
        Assert.assertNotNull(networkHealth);
        Assert.assertTrue(memoryHealth.getStatus().equals(DOWN));
        Assert.assertTrue(networkHealth.getStatus().equals(UP));
        Assert.assertTrue("memory is bad".equals(memoryHealth.getDetails().get("memory")));
        Assert.assertTrue("network is ok".equals(networkHealth.getDetails().get("network")));
    }
}

