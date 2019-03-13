/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.spring.boot.cloud;


import org.apache.camel.spring.boot.CamelAutoConfiguration;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.core.env.Environment;


public class CamelCloudServiceCallConfigurationTest {
    @Test
    public void testConfiguration() {
        new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(CamelAutoConfiguration.class, CamelCloudAutoConfiguration.class, CamelCloudServiceChooserAutoConfiguration.class)).withPropertyValues("camel.cloud.enabled=false", "camel.cloud.service-discovery.enabled=false", "camel.cloud.service-filter.enabled=false", "camel.cloud.service-chooser.enabled=true", "camel.cloud.load-balancer.enabled=false", "debug=false").run(( context) -> {
            Environment env = context.getEnvironment();
            assertFalse(env.getProperty("camel.cloud.enabled", .class));
            assertFalse(env.getProperty("camel.cloud.service-discovery.enabled", .class));
            assertFalse(env.getProperty("camel.cloud.service-filter.enabled", .class));
            assertTrue(env.getProperty("camel.cloud.service-chooser.enabled", .class));
            assertFalse(env.getProperty("camel.cloud.load-balancer.enabled", .class));
            assertTrue(context.getBeansOfType(.class).isEmpty());
            assertTrue(context.getBeansOfType(.class).isEmpty());
            assertTrue(context.getBeansOfType(.class).isEmpty());
            assertTrue(context.getBeansOfType(.class).isEmpty());
        });
    }
}

