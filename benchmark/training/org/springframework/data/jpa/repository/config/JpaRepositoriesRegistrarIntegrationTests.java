/**
 * Copyright 2012-2019 the original author or authors.
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
package org.springframework.data.jpa.repository.config;


import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.sample.UserRepository;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.ClassUtils;


/**
 * Integration test for {@link JpaRepositoriesRegistrar}.
 *
 * @author Oliver Gierke
 * @author Jens Schauder
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class JpaRepositoriesRegistrarIntegrationTests {
    @Autowired
    UserRepository repository;

    @Autowired
    JpaRepositoriesRegistrarIntegrationTests.SampleRepository sampleRepository;

    @Configuration
    @EnableJpaRepositories(basePackages = "org.springframework.data.jpa.repository.sample")
    static class Config {
        @Bean
        public DataSource dataSource() {
            return new EmbeddedDatabaseBuilder().generateUniqueName(true).build();
        }

        @Bean
        public EntityManagerFactory entityManagerFactory() {
            LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
            factory.setDataSource(dataSource());
            factory.setPersistenceUnitName("spring-data-jpa");
            factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
            factory.afterPropertiesSet();
            return factory.getObject();
        }

        @Bean
        public JpaDialect jpaDialect() {
            return new HibernateJpaDialect();
        }

        @Bean
        public PlatformTransactionManager transactionManager() {
            return new org.springframework.orm.jpa.JpaTransactionManager(entityManagerFactory());
        }

        @Bean
        public JpaRepositoriesRegistrarIntegrationTests.SampleRepository sampleRepository() {
            return new JpaRepositoriesRegistrarIntegrationTests.SampleRepository();
        }
    }

    @Test
    public void foo() {
        Assert.assertThat(repository, CoreMatchers.is(CoreMatchers.notNullValue()));
    }

    // DATAJPA-330
    @Test
    public void doesNotProxyPlainAtRepositoryBeans() {
        Assert.assertThat(sampleRepository, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(ClassUtils.isCglibProxy(sampleRepository), CoreMatchers.is(false));
        JpaRepositoriesRegistrarIntegrationTests.assertExceptionTranslationActive(repository);
    }

    @Repository
    static class SampleRepository {}
}

