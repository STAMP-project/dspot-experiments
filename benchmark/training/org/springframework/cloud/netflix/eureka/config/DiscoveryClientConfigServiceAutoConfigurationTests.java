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
package org.springframework.cloud.netflix.eureka.config;


import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import java.util.Arrays;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.cloud.netflix.eureka.CloudEurekaClient;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 *
 *
 * @author Dave Syer
 */
public class DiscoveryClientConfigServiceAutoConfigurationTests {
    private AnnotationConfigApplicationContext context;

    @Test
    public void onWhenRequested() throws Exception {
        setup("spring.cloud.config.discovery.enabled=true", "eureka.instance.metadataMap.foo:bar", "eureka.instance.nonSecurePort:7001", "eureka.instance.hostname:foo");
        assertThat(this.context.getBeanNamesForType(EurekaDiscoveryClientConfigServiceAutoConfiguration.class).length).isEqualTo(1);
        EurekaClient eurekaClient = this.context.getParent().getBean(EurekaClient.class);
        Mockito.verify(eurekaClient, Mockito.times(2)).getInstancesByVipAddress(DEFAULT_CONFIG_SERVER, false);
        Mockito.verify(eurekaClient, Mockito.times(1)).shutdown();
        ConfigClientProperties locator = this.context.getBean(ConfigClientProperties.class);
        assertThat(locator.getUri()[0]).isEqualTo("http://foo:7001/");
        ApplicationInfoManager infoManager = this.context.getBean(ApplicationInfoManager.class);
        assertThat(infoManager.getInfo().getMetadata().get("foo")).isEqualTo("bar");
    }

    @Configuration
    protected static class EnvironmentKnobbler {
        @Bean
        public EurekaClient eurekaClient(ApplicationInfoManager manager) {
            InstanceInfo info = manager.getInfo();
            EurekaClient client = Mockito.mock(CloudEurekaClient.class);
            BDDMockito.given(client.getInstancesByVipAddress(DEFAULT_CONFIG_SERVER, false)).willReturn(Arrays.asList(info));
            return client;
        }
    }
}

