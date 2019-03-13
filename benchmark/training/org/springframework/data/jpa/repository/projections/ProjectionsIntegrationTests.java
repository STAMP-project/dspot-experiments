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
package org.springframework.data.jpa.repository.projections;


import EmbeddedDatabaseType.HSQL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManagerFactory;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.sql.DataSource;
import lombok.Data;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;


/**
 * Integration tests for the behavior of projections.
 *
 * @author Jens Schauder
 */
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ProjectionsIntegrationTests.Config.class)
public class ProjectionsIntegrationTests {
    @Autowired
    ProjectionsIntegrationTests.DummyEntityWithCollectionRepository repository;

    // DATAJPA-1173
    @Test
    public void findAllFindsTheSingleEntity() {
        assertThat(findAll()).hasSize(1);
    }

    // DATAJPA-1173
    @Test
    public void findAllProjectedFindsTheSingleEntity() {
        assertThat(repository.findAllProjectedBy()).hasSize(1);
    }

    @Data
    @Entity(name = "Dummy")
    @Table(name = "DummyEntity")
    static class DummyEntityWithCollection {
        @GeneratedValue
        @Id
        Long id;

        String name;

        @OneToMany(cascade = CascadeType.ALL)
        @JoinColumn(name = "subs")
        List<ProjectionsIntegrationTests.SubEntity> subs = new ArrayList<>();

        String otherAttribute;
    }

    @Data
    @Entity
    @Table(name = "SubEntity")
    static class SubEntity {
        @GeneratedValue
        @Id
        Long id;

        String name;

        String otherAttribute;
    }

    interface DummyEntityProjection {
        String getName();

        List<ProjectionsIntegrationTests.SubEntityProjection> getSubs();
    }

    interface SubEntityProjection {
        String getName();
    }

    interface DummyEntityWithCollectionRepository extends JpaRepository<ProjectionsIntegrationTests.DummyEntityWithCollection, Long> {
        List<ProjectionsIntegrationTests.DummyEntityProjection> findAllProjectedBy();
    }

    @EnableJpaRepositories(considerNestedRepositories = true)
    @EnableTransactionManagement
    static class Config {
        @Bean
        DataSource dataSource() {
            return // 
            // 
            // 
            // 
            // 
            new EmbeddedDatabaseBuilder().generateUniqueName(true).setType(HSQL).setScriptEncoding("UTF-8").ignoreFailedDrops(true).build();
        }

        @Bean
        AbstractEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
            LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
            factoryBean.setDataSource(dataSource);
            factoryBean.setPersistenceUnitRootLocation("simple-persistence");
            factoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
            factoryBean.setPackagesToScan(this.getClass().getPackage().getName());
            Properties properties = new Properties();
            properties.setProperty("hibernate.hbm2ddl.auto", "create");
            properties.setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
            factoryBean.setJpaProperties(properties);
            return factoryBean;
        }

        @Bean
        PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
            return new org.springframework.orm.jpa.JpaTransactionManager(emf);
        }
    }
}

