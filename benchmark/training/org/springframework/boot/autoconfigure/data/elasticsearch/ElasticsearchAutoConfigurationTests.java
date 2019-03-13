/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.autoconfigure.data.elasticsearch;


import java.util.List;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.testsupport.testcontainers.ElasticsearchContainer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Tests for {@link ElasticsearchAutoConfiguration}.
 *
 * @author Phillip Webb
 * @author Andy Wilkinson
 */
public class ElasticsearchAutoConfigurationTests {
    @ClassRule
    public static ElasticsearchContainer elasticsearch = new ElasticsearchContainer();

    private AnnotationConfigApplicationContext context;

    @Test
    public void useExistingClient() {
        this.context = new AnnotationConfigApplicationContext();
        this.context.register(ElasticsearchAutoConfigurationTests.CustomConfiguration.class, PropertyPlaceholderAutoConfiguration.class, ElasticsearchAutoConfiguration.class);
        this.context.refresh();
        assertThat(this.context.getBeanNamesForType(Client.class).length).isEqualTo(1);
        assertThat(this.context.getBean("myClient")).isSameAs(this.context.getBean(Client.class));
    }

    @Test
    public void createTransportClient() {
        this.context = new AnnotationConfigApplicationContext();
        TestPropertyValues.of(("spring.data.elasticsearch.cluster-nodes:localhost:" + (ElasticsearchAutoConfigurationTests.elasticsearch.getMappedTransportPort())), "spring.data.elasticsearch.cluster-name:docker-cluster").applyTo(this.context);
        this.context.register(PropertyPlaceholderAutoConfiguration.class, ElasticsearchAutoConfiguration.class);
        this.context.refresh();
        List<DiscoveryNode> connectedNodes = this.context.getBean(org.elasticsearch.client.transport.TransportClient.class).connectedNodes();
        assertThat(connectedNodes).hasSize(1);
    }

    @Configuration
    static class CustomConfiguration {
        @Bean
        public Client myClient() {
            return Mockito.mock(Client.class);
        }
    }
}

