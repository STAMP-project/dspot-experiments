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
package org.springframework.boot.autoconfigure.data.cassandra;


import com.datastax.driver.core.Session;
import java.util.Set;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.data.cassandra.city.City;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.core.ReactiveCassandraTemplate;
import org.springframework.data.cassandra.core.mapping.CassandraMappingContext;
import org.springframework.data.cassandra.core.mapping.SimpleUserTypeResolver;
import org.springframework.test.util.ReflectionTestUtils;


/**
 * Tests for {@link CassandraReactiveDataAutoConfiguration}.
 *
 * @author Edd? Mel?ndez
 * @author Stephane Nicoll
 * @author Mark Paluch
 */
public class CassandraReactiveDataAutoConfigurationTests {
    private AnnotationConfigApplicationContext context;

    @Test
    public void templateExists() {
        load("spring.data.cassandra.keyspaceName:boot_test");
        assertThat(this.context.getBeanNamesForType(ReactiveCassandraTemplate.class)).hasSize(1);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void entityScanShouldSetInitialEntitySet() {
        load(CassandraReactiveDataAutoConfigurationTests.EntityScanConfig.class, "spring.data.cassandra.keyspaceName:boot_test");
        CassandraMappingContext mappingContext = this.context.getBean(CassandraMappingContext.class);
        Set<Class<?>> initialEntitySet = ((Set<Class<?>>) (ReflectionTestUtils.getField(mappingContext, "initialEntitySet")));
        assertThat(initialEntitySet).containsOnly(City.class);
    }

    @Test
    public void userTypeResolverShouldBeSet() {
        load("spring.data.cassandra.keyspaceName:boot_test");
        CassandraMappingContext mappingContext = this.context.getBean(CassandraMappingContext.class);
        assertThat(ReflectionTestUtils.getField(mappingContext, "userTypeResolver")).isInstanceOf(SimpleUserTypeResolver.class);
    }

    @Configuration
    static class TestConfiguration {
        @Bean
        public Session session() {
            return Mockito.mock(Session.class);
        }
    }

    @Configuration
    @EntityScan("org.springframework.boot.autoconfigure.data.cassandra.city")
    static class EntityScanConfig {}
}

