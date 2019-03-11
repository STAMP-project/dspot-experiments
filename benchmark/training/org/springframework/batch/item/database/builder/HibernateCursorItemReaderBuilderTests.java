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
import javax.sql.DataSource;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.database.HibernateCursorItemReader;
import org.springframework.batch.item.database.orm.HibernateNativeQueryProvider;
import org.springframework.batch.item.sample.Foo;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactory;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;


/**
 *
 *
 * @author Michael Minella
 */
public class HibernateCursorItemReaderBuilderTests {
    private SessionFactory sessionFactory;

    private ConfigurableApplicationContext context;

    @Test
    public void testConfiguration() throws Exception {
        HibernateCursorItemReader<Foo> reader = new HibernateCursorItemReaderBuilder<Foo>().name("fooReader").sessionFactory(this.sessionFactory).fetchSize(2).currentItemCount(2).maxItemCount(4).queryName("allFoos").useStatelessSession(true).build();
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
    }

    @Test
    public void testConfigurationNoSaveState() throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("value", 2);
        HibernateCursorItemReader<Foo> reader = new HibernateCursorItemReaderBuilder<Foo>().name("fooReader").sessionFactory(this.sessionFactory).queryString("from Foo foo where foo.id > :value").parameterValues(parameters).saveState(false).build();
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
        HibernateNativeQueryProvider<Foo> provider = new HibernateNativeQueryProvider();
        provider.setEntityClass(Foo.class);
        provider.setSqlQuery("select * from T_FOOS");
        provider.afterPropertiesSet();
        HibernateCursorItemReader<Foo> reader = new HibernateCursorItemReaderBuilder<Foo>().name("fooReader").sessionFactory(this.sessionFactory).queryProvider(provider).build();
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
    public void testConfigurationNativeQuery() throws Exception {
        HibernateCursorItemReader<Foo> reader = new HibernateCursorItemReaderBuilder<Foo>().name("fooReader").sessionFactory(this.sessionFactory).nativeQuery("select * from T_FOOS").entityClass(Foo.class).build();
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
            new HibernateCursorItemReaderBuilder<Foo>().fetchSize((-2)).build();
            Assert.fail("fetch size must be >= 0");
        } catch (IllegalStateException ise) {
            Assert.assertEquals("fetchSize must not be negative", ise.getMessage());
        }
        try {
            new HibernateCursorItemReaderBuilder<Foo>().build();
            Assert.fail("sessionFactory is required");
        } catch (IllegalStateException ise) {
            Assert.assertEquals("A SessionFactory must be provided", ise.getMessage());
        }
        try {
            new HibernateCursorItemReaderBuilder<Foo>().sessionFactory(this.sessionFactory).saveState(true).build();
            Assert.fail("name is required when saveState is true");
        } catch (IllegalStateException ise) {
            Assert.assertEquals("A name is required when saveState is set to true.", ise.getMessage());
        }
        try {
            new HibernateCursorItemReaderBuilder<Foo>().sessionFactory(this.sessionFactory).saveState(false).build();
            Assert.fail(("A HibernateQueryProvider, queryName, queryString, " + "or both the nativeQuery and entityClass must be configured"));
        } catch (IllegalStateException ise) {
            Assert.assertEquals(("A HibernateQueryProvider, queryName, queryString, " + "or both the nativeQuery and entityClass must be configured"), ise.getMessage());
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
        public SessionFactory sessionFactory() throws Exception {
            LocalSessionFactoryBean factoryBean = new LocalSessionFactoryBean();
            factoryBean.setDataSource(dataSource());
            factoryBean.setMappingLocations(new ClassPathResource("/org/springframework/batch/item/database/Foo.hbm.xml", getClass()));
            factoryBean.afterPropertiesSet();
            return factoryBean.getObject();
        }
    }
}

