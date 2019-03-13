/**
 * Copyright 2017 the original author or authors.
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
package org.springframework.batch.item.database.builder;


import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.orm.JpaNativeQueryProvider;
import org.springframework.batch.item.sample.Foo;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactory;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.test.util.ReflectionTestUtils;


/**
 *
 *
 * @author Michael Minella
 */
public class JpaPagingItemReaderBuilderTests {
    private EntityManagerFactory entityManagerFactory;

    private ConfigurableApplicationContext context;

    @Test
    public void testConfiguration() throws Exception {
        JpaPagingItemReader<Foo> reader = new JpaPagingItemReaderBuilder<Foo>().name("fooReader").entityManagerFactory(this.entityManagerFactory).currentItemCount(2).maxItemCount(4).pageSize(5).transacted(false).queryString("select f from Foo f ").build();
        reader.afterPropertiesSet();
        ExecutionContext executionContext = new ExecutionContext();
        reader.open(executionContext);
        Foo item1 = reader.read();
        Foo item2 = reader.read();
        Assert.assertNull(reader.read());
        reader.update(executionContext);
        reader.close();
        Assert.assertEquals(3, item1.getId());
        Assert.assertEquals("bar3", item1.getName());
        Assert.assertEquals(3, item1.getValue());
        Assert.assertEquals(4, item2.getId());
        Assert.assertEquals("bar4", item2.getName());
        Assert.assertEquals(4, item2.getValue());
        Assert.assertEquals(2, executionContext.size());
        Assert.assertEquals(5, ReflectionTestUtils.getField(reader, "pageSize"));
        Assert.assertFalse(((Boolean) (ReflectionTestUtils.getField(reader, "transacted"))));
    }

    @Test
    public void testConfigurationNoSaveState() throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("value", 2);
        JpaPagingItemReader<Foo> reader = new JpaPagingItemReaderBuilder<Foo>().name("fooReader").entityManagerFactory(this.entityManagerFactory).queryString("select f from Foo f where f.id > :value").parameterValues(parameters).saveState(false).build();
        reader.afterPropertiesSet();
        ExecutionContext executionContext = new ExecutionContext();
        reader.open(executionContext);
        int i = 0;
        while ((reader.read()) != null) {
            i++;
        } 
        reader.update(executionContext);
        reader.close();
        Assert.assertEquals(3, i);
        Assert.assertEquals(0, executionContext.size());
    }

    @Test
    public void testConfigurationQueryProvider() throws Exception {
        JpaNativeQueryProvider<Foo> provider = new JpaNativeQueryProvider();
        provider.setEntityClass(Foo.class);
        provider.setSqlQuery("select * from T_FOOS");
        provider.afterPropertiesSet();
        JpaPagingItemReader<Foo> reader = new JpaPagingItemReaderBuilder<Foo>().name("fooReader").entityManagerFactory(this.entityManagerFactory).queryProvider(provider).build();
        reader.afterPropertiesSet();
        ExecutionContext executionContext = new ExecutionContext();
        reader.open(executionContext);
        int i = 0;
        while ((reader.read()) != null) {
            i++;
        } 
        reader.update(executionContext);
        reader.close();
        Assert.assertEquals(5, i);
    }

    @Test
    public void testValidation() {
        try {
            new JpaPagingItemReaderBuilder<Foo>().entityManagerFactory(this.entityManagerFactory).pageSize((-2)).build();
            Assert.fail("pageSize must be >= 0");
        } catch (IllegalArgumentException iae) {
            Assert.assertEquals("pageSize must be greater than zero", iae.getMessage());
        }
        try {
            new JpaPagingItemReaderBuilder<Foo>().build();
            Assert.fail("An EntityManagerFactory is required");
        } catch (IllegalArgumentException iae) {
            Assert.assertEquals("An EntityManagerFactory is required", iae.getMessage());
        }
        try {
            new JpaPagingItemReaderBuilder<Foo>().entityManagerFactory(this.entityManagerFactory).saveState(true).build();
            Assert.fail("A name is required when saveState is set to true");
        } catch (IllegalArgumentException iae) {
            Assert.assertEquals("A name is required when saveState is set to true", iae.getMessage());
        }
        try {
            new JpaPagingItemReaderBuilder<Foo>().entityManagerFactory(this.entityManagerFactory).saveState(false).build();
            Assert.fail("Query string is required when queryProvider is null");
        } catch (IllegalArgumentException iae) {
            Assert.assertEquals("Query string is required when queryProvider is null", iae.getMessage());
        }
    }

    @Configuration
    public static class TestDataSourceConfiguration {
        @Bean
        public DataSource dataSource() {
            return new EmbeddedDatabaseFactory().getDatabase();
        }

        @Bean
        public DataSourceInitializer initializer(DataSource dataSource) {
            DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
            dataSourceInitializer.setDataSource(dataSource);
            Resource create = new ClassPathResource("org/springframework/batch/item/database/init-foo-schema-hsqldb.sql");
            dataSourceInitializer.setDatabasePopulator(new org.springframework.jdbc.datasource.init.ResourceDatabasePopulator(create));
            return dataSourceInitializer;
        }

        @Bean
        public LocalContainerEntityManagerFactoryBean entityManagerFactory() throws Exception {
            LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
            entityManagerFactoryBean.setDataSource(dataSource());
            entityManagerFactoryBean.setPersistenceUnitName("bar");
            entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
            return entityManagerFactoryBean;
        }
    }
}

