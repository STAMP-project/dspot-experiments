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
package com.alipay.sofa.infra.utils;


import SofaBootInfraConstants.APP_NAME_KEY;
import com.alipay.sofa.infra.base.AbstractTestBase;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;


/**
 * SOFABootEnvUtils Tester.
 *
 * @author <guanchao.ygc>
 * @version 1.0
 * @since <pre>2.5.0</pre>
 */
public class SOFABootEnvUtilsTest extends AbstractTestBase implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    private static AtomicLong bootstrapContext = new AtomicLong(0L);

    private static AtomicLong applicatioinContext = new AtomicLong(0L);

    private static ConfigurableEnvironment bootstrapEnvironment;

    private static ConfigurableEnvironment applicationEnvironment;

    /**
     * Method: isSpringCloudBootstrapEnvironment(Environment environment)
     */
    @Test
    public void testIsSpringCloudBootstrapEnvironment() {
        Environment environment = ctx.getEnvironment();
        Assert.assertFalse(SOFABootEnvUtils.isSpringCloudBootstrapEnvironment(environment));
        Assert.assertEquals(1L, SOFABootEnvUtilsTest.bootstrapContext.get());
        Assert.assertEquals(1L, SOFABootEnvUtilsTest.applicatioinContext.get());
        Assert.assertFalse(SOFABootEnvUtils.isSpringCloudBootstrapEnvironment(null));
        Assert.assertEquals("infra-test", SOFABootEnvUtilsTest.bootstrapEnvironment.getProperty(APP_NAME_KEY));
        Assert.assertEquals("infra-test", SOFABootEnvUtilsTest.applicationEnvironment.getProperty(APP_NAME_KEY));
        Assert.assertEquals("INFO", SOFABootEnvUtilsTest.bootstrapEnvironment.getProperty("logging.level.com.alipay.test"));
        Assert.assertEquals("INFO", SOFABootEnvUtilsTest.applicationEnvironment.getProperty("logging.level.com.alipay.test"));
        Assert.assertEquals("WARN", SOFABootEnvUtilsTest.bootstrapEnvironment.getProperty("logging.level.com.test.demo"));
        Assert.assertEquals("WARN", SOFABootEnvUtilsTest.applicationEnvironment.getProperty("logging.level.com.test.demo"));
        Assert.assertEquals("./logs", SOFABootEnvUtilsTest.bootstrapEnvironment.getProperty("logging.path"));
        Assert.assertEquals("./logs", SOFABootEnvUtilsTest.applicationEnvironment.getProperty("logging.path"));
        Assert.assertEquals(null, SOFABootEnvUtilsTest.bootstrapEnvironment.getProperty("any.key"));
        Assert.assertEquals("any.value", SOFABootEnvUtilsTest.applicationEnvironment.getProperty("any.key"));
    }
}

