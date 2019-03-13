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
package org.springframework.boot.autoconfigure.data.couchbase;


import Consistency.STRONGLY_CONSISTENT;
import java.util.Collections;
import java.util.Set;
import org.junit.Test;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseTestConfigurer;
import org.springframework.boot.autoconfigure.data.couchbase.city.City;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.couchbase.config.AbstractReactiveCouchbaseDataConfiguration;
import org.springframework.data.couchbase.config.BeanNames;
import org.springframework.data.couchbase.config.CouchbaseConfigurer;
import org.springframework.data.couchbase.core.RxJavaCouchbaseTemplate;
import org.springframework.data.couchbase.core.convert.CouchbaseCustomConversions;
import org.springframework.data.couchbase.core.mapping.CouchbaseMappingContext;
import org.springframework.data.couchbase.core.mapping.event.ValidatingCouchbaseEventListener;
import org.springframework.data.couchbase.core.query.Consistency;
import org.springframework.data.couchbase.repository.support.IndexManager;
import org.springframework.test.util.ReflectionTestUtils;


/**
 * Tests for {@link CouchbaseReactiveDataAutoConfiguration}.
 *
 * @author Alex Derkach
 */
public class CouchbaseReactiveDataAutoConfigurationTests {
    private AnnotationConfigApplicationContext context;

    @Test
    public void disabledIfCouchbaseIsNotConfigured() {
        load(null);
        assertThat(this.context.getBeansOfType(IndexManager.class)).isEmpty();
    }

    @Test
    public void customConfiguration() {
        load(CouchbaseReactiveDataAutoConfigurationTests.CustomCouchbaseConfiguration.class);
        RxJavaCouchbaseTemplate rxJavaCouchbaseTemplate = this.context.getBean(RxJavaCouchbaseTemplate.class);
        assertThat(rxJavaCouchbaseTemplate.getDefaultConsistency()).isEqualTo(STRONGLY_CONSISTENT);
    }

    @Test
    public void validatorIsPresent() {
        load(CouchbaseTestConfigurer.class);
        assertThat(this.context.getBeansOfType(ValidatingCouchbaseEventListener.class)).hasSize(1);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void entityScanShouldSetInitialEntitySet() {
        load(CouchbaseReactiveDataAutoConfigurationTests.EntityScanConfig.class);
        CouchbaseMappingContext mappingContext = this.context.getBean(CouchbaseMappingContext.class);
        Set<Class<?>> initialEntitySet = ((Set<Class<?>>) (ReflectionTestUtils.getField(mappingContext, "initialEntitySet")));
        assertThat(initialEntitySet).containsOnly(City.class);
    }

    @Test
    public void customConversions() {
        load(CouchbaseReactiveDataAutoConfigurationTests.CustomConversionsConfig.class);
        RxJavaCouchbaseTemplate template = this.context.getBean(RxJavaCouchbaseTemplate.class);
        assertThat(template.getConverter().getConversionService().canConvert(CouchbaseProperties.class, Boolean.class)).isTrue();
    }

    @Configuration
    static class CustomCouchbaseConfiguration extends AbstractReactiveCouchbaseDataConfiguration {
        @Override
        protected CouchbaseConfigurer couchbaseConfigurer() {
            return new CouchbaseTestConfigurer();
        }

        @Override
        protected Consistency getDefaultConsistency() {
            return Consistency.STRONGLY_CONSISTENT;
        }
    }

    @Configuration
    @Import(CouchbaseTestConfigurer.class)
    static class CustomConversionsConfig {
        @Bean(BeanNames.COUCHBASE_CUSTOM_CONVERSIONS)
        public CouchbaseCustomConversions myCustomConversions() {
            return new CouchbaseCustomConversions(Collections.singletonList(new CouchbaseReactiveDataAutoConfigurationTests.MyConverter()));
        }
    }

    @Configuration
    @EntityScan("org.springframework.boot.autoconfigure.data.couchbase.city")
    @Import(CouchbaseReactiveDataAutoConfigurationTests.CustomCouchbaseConfiguration.class)
    static class EntityScanConfig {}

    static class MyConverter implements Converter<CouchbaseProperties, Boolean> {
        @Override
        public Boolean convert(CouchbaseProperties value) {
            return true;
        }
    }
}

