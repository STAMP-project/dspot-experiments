/**
 * Copyright 2017-2019 the original author or authors.
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
package org.springframework.data.jpa.repository.support;


import SimpleEntityPathResolver.INSTANCE;
import com.querydsl.core.types.EntityPath;
import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.sample.UserRepository;
import org.springframework.data.querydsl.EntityPathResolver;


/**
 * Unit tests for {@link EntityPathResolver} related tests on {@link JpaRepositoryFactoryBean}.
 *
 * @author Jens Schauder
 * @author Oliver Gierke
 */
public class JpaRepositoryFactoryBeanEntityPathResolverIntegrationTests {
    @Configuration
    @ImportResource("classpath:infrastructure.xml")
    @EnableJpaRepositories(basePackageClasses = UserRepository.class// 
    , includeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = UserRepository.class))
    static class BaseConfig {
        static final EntityPathResolver RESOLVER = new EntityPathResolver() {
            @Override
            public <T> EntityPath<T> createPath(Class<T> domainClass) {
                return null;
            }
        };
    }

    @Configuration
    static class FirstEntityPathResolver {
        @Bean
        EntityPathResolver firstEntityPathResolver() {
            return JpaRepositoryFactoryBeanEntityPathResolverIntegrationTests.BaseConfig.RESOLVER;
        }
    }

    @Configuration
    static class SecondEntityPathResolver {
        @Bean
        EntityPathResolver secondEntityPathResolver() {
            return JpaRepositoryFactoryBeanEntityPathResolverIntegrationTests.BaseConfig.RESOLVER;
        }
    }

    // DATAJPA-1234, DATAJPA-1394
    @Test
    public void usesSimpleEntityPathResolverByDefault() {
        JpaRepositoryFactoryBeanEntityPathResolverIntegrationTests.assertEntityPathResolver(INSTANCE, JpaRepositoryFactoryBeanEntityPathResolverIntegrationTests.BaseConfig.class);
    }

    // DATAJPA-1234, DATAJPA-1394
    @Test
    public void usesExplicitlyRegisteredEntityPathResolver() {
        JpaRepositoryFactoryBeanEntityPathResolverIntegrationTests.assertEntityPathResolver(JpaRepositoryFactoryBeanEntityPathResolverIntegrationTests.BaseConfig.RESOLVER, JpaRepositoryFactoryBeanEntityPathResolverIntegrationTests.BaseConfig.class, JpaRepositoryFactoryBeanEntityPathResolverIntegrationTests.FirstEntityPathResolver.class);
    }

    // DATAJPA-1234, DATAJPA-1394
    @Test
    public void rejectsMulitpleEntityPathResolvers() {
        assertThatExceptionOfType(BeanCreationException.class).isThrownBy(() -> {
            AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(.class, .class, .class);
            context.close();
        }).withCauseExactlyInstanceOf(NoUniqueBeanDefinitionException.class);
    }
}

