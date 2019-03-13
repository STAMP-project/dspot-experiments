/**
 * Copyright 2012-2017 the original author or authors.
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


import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.testsupport.testcontainers.ElasticsearchContainer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchMappingContext;


/**
 * Tests for {@link ElasticsearchDataAutoConfiguration}.
 *
 * @author Phillip Webb
 * @author Artur Konczak
 */
public class ElasticsearchDataAutoConfigurationTests {
    @ClassRule
    public static ElasticsearchContainer elasticsearch = new ElasticsearchContainer();

    private AnnotationConfigApplicationContext context;

    @Test
    public void templateBackOffWithNoClient() {
        this.context = new AnnotationConfigApplicationContext(ElasticsearchDataAutoConfiguration.class);
        assertThat(this.context.getBeansOfType(ElasticsearchTemplate.class)).isEmpty();
    }

    @Test
    public void templateExists() {
        this.context = new AnnotationConfigApplicationContext();
        TestPropertyValues.of(("spring.data.elasticsearch.cluster-nodes:localhost:" + (ElasticsearchDataAutoConfigurationTests.elasticsearch.getMappedTransportPort())), "spring.data.elasticsearch.cluster-name:docker-cluster").applyTo(this.context);
        this.context.register(PropertyPlaceholderAutoConfiguration.class, ElasticsearchAutoConfiguration.class, ElasticsearchDataAutoConfiguration.class);
        this.context.refresh();
        assertHasSingleBean(ElasticsearchTemplate.class);
    }

    @Test
    public void mappingContextExists() {
        this.context = new AnnotationConfigApplicationContext();
        TestPropertyValues.of(("spring.data.elasticsearch.cluster-nodes:localhost:" + (ElasticsearchDataAutoConfigurationTests.elasticsearch.getMappedTransportPort())), "spring.data.elasticsearch.cluster-name:docker-cluster").applyTo(this.context);
        this.context.register(PropertyPlaceholderAutoConfiguration.class, ElasticsearchAutoConfiguration.class, ElasticsearchDataAutoConfiguration.class);
        this.context.refresh();
        assertHasSingleBean(SimpleElasticsearchMappingContext.class);
    }

    @Test
    public void converterExists() {
        this.context = new AnnotationConfigApplicationContext();
        TestPropertyValues.of(("spring.data.elasticsearch.cluster-nodes:localhost:" + (ElasticsearchDataAutoConfigurationTests.elasticsearch.getMappedTransportPort())), "spring.data.elasticsearch.cluster-name:docker-cluster").applyTo(this.context);
        this.context.register(PropertyPlaceholderAutoConfiguration.class, ElasticsearchAutoConfiguration.class, ElasticsearchDataAutoConfiguration.class);
        this.context.refresh();
        assertHasSingleBean(ElasticsearchConverter.class);
    }
}

