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
import com.alipay.sofa.healthcheck.bean.ApplicationHealthCheckCallback;
import com.alipay.sofa.healthcheck.bean.MiddlewareHealthCheckCallback;
import com.alipay.sofa.healthcheck.core.AfterReadinessCheckCallbackProcessor;
import java.util.HashMap;
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
public class AfterHealthReadinessCheckCallbackProcessorTest extends BaseHealthCheckTest {
    @Configuration
    static class AfterReadinessCheckCallbackConfiguration {
        @Bean
        public MiddlewareHealthCheckCallback middlewareHealthCheckCallback(@Value("${after-readiness-check-callback-a.health:false}")
        boolean health) {
            return new MiddlewareHealthCheckCallback(health);
        }

        @Bean
        public ApplicationHealthCheckCallback applicationHealthCheckCallback(@Value("${after-readiness-check-callback-b.mark:false}")
        boolean mark) {
            return new ApplicationHealthCheckCallback(mark);
        }
    }

    @Test
    public void testAfterReadinessCheckCallbackMarked() {
        initApplicationContext(true, false);
        AfterReadinessCheckCallbackProcessor afterReadinessCheckCallbackProcessor = applicationContext.getBean(AfterReadinessCheckCallbackProcessor.class);
        ApplicationHealthCheckCallback applicationHealthCheckCallback = applicationContext.getBean(ApplicationHealthCheckCallback.class);
        HashMap<String, Health> hashMap = new HashMap<>();
        boolean result = afterReadinessCheckCallbackProcessor.afterReadinessCheckCallback(hashMap);
        Assert.assertTrue(result);
        Assert.assertTrue(applicationHealthCheckCallback.isMark());
        Assert.assertTrue(((hashMap.size()) == 2));
        Health middleHealth = hashMap.get("middlewareHealthCheckCallback");
        Health applicationHealth = hashMap.get("applicationHealthCheckCallback");
        Assert.assertNotNull(middleHealth);
        Assert.assertNotNull(applicationHealth);
        Assert.assertTrue(middleHealth.getStatus().equals(UP));
        Assert.assertTrue(applicationHealth.getStatus().equals(UP));
        Assert.assertTrue(((middleHealth.getDetails().size()) == 1));
        Assert.assertTrue(((applicationHealth.getDetails().size()) == 1));
        Assert.assertTrue("server is ok".equals(middleHealth.getDetails().get("server")));
        Assert.assertTrue("port is ok".equals(applicationHealth.getDetails().get("port")));
    }

    @Test
    public void testAfterReadinessCheckCallbackUnMarked() {
        initApplicationContext(false, false);
        AfterReadinessCheckCallbackProcessor afterReadinessCheckCallbackProcessor = applicationContext.getBean(AfterReadinessCheckCallbackProcessor.class);
        ApplicationHealthCheckCallback applicationHealthCheckCallback = applicationContext.getBean(ApplicationHealthCheckCallback.class);
        HashMap<String, Health> hashMap = new HashMap<>();
        boolean result = afterReadinessCheckCallbackProcessor.afterReadinessCheckCallback(hashMap);
        Assert.assertFalse(result);
        Assert.assertTrue(applicationHealthCheckCallback.isMark());
        Assert.assertTrue(((hashMap.size()) == 2));
        Health middleHealth = hashMap.get("middlewareHealthCheckCallback");
        Health applicationHealth = hashMap.get("applicationHealthCheckCallback");
        Assert.assertNotNull(middleHealth);
        Assert.assertNotNull(applicationHealth);
        Assert.assertTrue(middleHealth.getStatus().equals(DOWN));
        Assert.assertTrue(applicationHealth.getStatus().equals(UP));
        Assert.assertTrue(((middleHealth.getDetails().size()) == 1));
        Assert.assertTrue("server is bad".equals(middleHealth.getDetails().get("server")));
        Assert.assertTrue("port is ok".equals(applicationHealth.getDetails().get("port")));
    }
}

