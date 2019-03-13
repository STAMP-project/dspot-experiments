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


import SpringBootTest.WebEnvironment;
import com.alipay.sofa.healthcheck.base.SofaBootTestApplication;
import com.alipay.sofa.healthcheck.core.HealthIndicatorProcessor;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;


/**
 *
 *
 * @author qilong.zql
 * @since 3.0.0
 */
@SpringBootApplication
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SofaBootTestApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class ReactiveHealthIndicatorTest {
    @Autowired
    private HealthIndicatorProcessor healthIndicatorProcessor;

    @Test
    public void testReadinessCheckFailedHttpCode() {
        Map<String, Health> healthMap = new HashMap<>();
        boolean isHealth = healthIndicatorProcessor.readinessHealthCheck(healthMap);
        Assert.assertTrue(isHealth);
        Assert.assertNotNull(healthMap.get("reactiveDemo"));
        Assert.assertNotNull(healthMap.get("reactiveDemo").getDetails().get("reactiveTest"));
    }

    @Configuration
    static class ReactiveHealthIndicatorConfiguration {
        @Bean
        public ReactiveHealthIndicator reactiveDemoHealthIndicator() {
            return () -> Mono.just(Health.up().withDetail("reactiveTest", new HashMap<>()).build());
        }
    }
}

