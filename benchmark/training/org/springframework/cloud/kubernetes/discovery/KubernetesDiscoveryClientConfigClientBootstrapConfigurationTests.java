/**
 * Copyright 2013-2019 the original author or authors.
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
package org.springframework.cloud.kubernetes.discovery;


import java.util.Collections;
import junit.framework.TestCase;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 *
 *
 * @author Zhanwei Wang
 */
public class KubernetesDiscoveryClientConfigClientBootstrapConfigurationTests {
    private AnnotationConfigApplicationContext context;

    @Test
    public void onWhenRequested() throws Exception {
        setup("server.port=7000", "spring.cloud.config.discovery.enabled=true", "spring.cloud.kubernetes.discovery.enabled:true", "spring.cloud.kubernetes.enabled:true", "spring.application.name:test", "spring.cloud.config.discovery.service-id:configserver");
        TestCase.assertEquals(1, this.context.getParent().getBeanNamesForType(DiscoveryClient.class).length);
        DiscoveryClient client = this.context.getParent().getBean(DiscoveryClient.class);
        Mockito.verify(client, Mockito.atLeast(2)).getInstances("configserver");
        ConfigClientProperties locator = this.context.getBean(ConfigClientProperties.class);
        TestCase.assertEquals("http://fake:8888/", locator.getUri()[0]);
    }

    @Configuration
    protected static class EnvironmentKnobbler {
        @Bean
        public KubernetesDiscoveryClient kubernetesDiscoveryClient() {
            KubernetesDiscoveryClient client = Mockito.mock(KubernetesDiscoveryClient.class);
            ServiceInstance instance = new DefaultServiceInstance("configserver1", "configserver", "fake", 8888, false);
            BDDMockito.given(client.getInstances("configserver")).willReturn(Collections.singletonList(instance));
            return client;
        }
    }
}

