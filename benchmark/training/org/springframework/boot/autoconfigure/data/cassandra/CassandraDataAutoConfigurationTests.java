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


import ComponentScan.Filter;
import com.datastax.driver.core.Session;
import java.util.Collections;
import java.util.Set;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.data.cassandra.city.City;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.convert.CassandraCustomConversions;
import org.springframework.data.cassandra.core.mapping.CassandraMappingContext;
import org.springframework.data.cassandra.core.mapping.SimpleUserTypeResolver;
import org.springframework.test.util.ReflectionTestUtils;


/**
 * Tests for {@link CassandraDataAutoConfiguration}.
 *
 * @author Edd? Mel?ndez
 * @author Mark Paluch
 * @author Stephane Nicoll
 */
public class CassandraDataAutoConfigurationTests {
    private AnnotationConfigApplicationContext context;

    @Test
    public void templateExists() {
        load(CassandraDataAutoConfigurationTests.TestExcludeConfiguration.class);
        assertThat(this.context.getBeanNamesForType(CassandraTemplate.class).length).isEqualTo(1);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void entityScanShouldSetInitialEntitySet() {
        load(CassandraDataAutoConfigurationTests.EntityScanConfig.class);
        CassandraMappingContext mappingContext = this.context.getBean(CassandraMappingContext.class);
        Set<Class<?>> initialEntitySet = ((Set<Class<?>>) (ReflectionTestUtils.getField(mappingContext, "initialEntitySet")));
        assertThat(initialEntitySet).containsOnly(City.class);
    }

    @Test
    public void userTypeResolverShouldBeSet() {
        load();
        CassandraMappingContext mappingContext = this.context.getBean(CassandraMappingContext.class);
        assertThat(ReflectionTestUtils.getField(mappingContext, "userTypeResolver")).isInstanceOf(SimpleUserTypeResolver.class);
    }

    @Test
    public void defaultConversions() {
        load();
        CassandraTemplate template = this.context.getBean(CassandraTemplate.class);
        assertThat(template.getConverter().getConversionService().canConvert(CassandraDataAutoConfigurationTests.Person.class, String.class)).isFalse();
    }

    @Test
    public void customConversions() {
        load(CassandraDataAutoConfigurationTests.CustomConversionConfig.class);
        CassandraTemplate template = this.context.getBean(CassandraTemplate.class);
        assertThat(template.getConverter().getConversionService().canConvert(CassandraDataAutoConfigurationTests.Person.class, String.class)).isTrue();
    }

    @Configuration
    @ComponentScan(excludeFilters = @Filter(classes = { Session.class }, type = FilterType.ASSIGNABLE_TYPE))
    static class TestExcludeConfiguration {}

    @Configuration
    static class TestConfiguration {
        @Bean
        public Session getObject() {
            return Mockito.mock(Session.class);
        }
    }

    @Configuration
    @EntityScan("org.springframework.boot.autoconfigure.data.cassandra.city")
    static class EntityScanConfig {}

    @Configuration
    static class CustomConversionConfig {
        @Bean
        public CassandraCustomConversions myCassandraCustomConversions() {
            return new CassandraCustomConversions(Collections.singletonList(new CassandraDataAutoConfigurationTests.MyConverter()));
        }
    }

    private static class MyConverter implements Converter<CassandraDataAutoConfigurationTests.Person, String> {
        @Override
        public String convert(CassandraDataAutoConfigurationTests.Person o) {
            return null;
        }
    }

    private static class Person {}
}

