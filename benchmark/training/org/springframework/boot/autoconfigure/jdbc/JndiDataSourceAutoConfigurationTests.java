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
package org.springframework.boot.autoconfigure.jdbc;


import java.util.Set;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.test.util.ReflectionTestUtils;


/**
 * Tests for {@link JndiDataSourceAutoConfiguration}
 *
 * @author Andy Wilkinson
 */
public class JndiDataSourceAutoConfigurationTests {
    private ClassLoader threadContextClassLoader;

    private String initialContextFactory;

    private AnnotationConfigApplicationContext context;

    @Test
    public void dataSourceIsAvailableFromJndi() throws IllegalStateException, NamingException {
        DataSource dataSource = new BasicDataSource();
        configureJndi("foo", dataSource);
        this.context = new AnnotationConfigApplicationContext();
        TestPropertyValues.of("spring.datasource.jndi-name:foo").applyTo(this.context);
        this.context.register(JndiDataSourceAutoConfiguration.class);
        this.context.refresh();
        assertThat(this.context.getBean(DataSource.class)).isEqualTo(dataSource);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void mbeanDataSourceIsExcludedFromExport() throws IllegalStateException, NamingException {
        DataSource dataSource = new BasicDataSource();
        configureJndi("foo", dataSource);
        this.context = new AnnotationConfigApplicationContext();
        TestPropertyValues.of("spring.datasource.jndi-name:foo").applyTo(this.context);
        this.context.register(JndiDataSourceAutoConfiguration.class, JndiDataSourceAutoConfigurationTests.MBeanExporterConfiguration.class);
        this.context.refresh();
        assertThat(this.context.getBean(DataSource.class)).isEqualTo(dataSource);
        MBeanExporter exporter = this.context.getBean(MBeanExporter.class);
        Set<String> excludedBeans = ((Set<String>) (ReflectionTestUtils.getField(exporter, "excludedBeans")));
        assertThat(excludedBeans).containsExactly("dataSource");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void mbeanDataSourceIsExcludedFromExportByAllExporters() throws IllegalStateException, NamingException {
        DataSource dataSource = new BasicDataSource();
        configureJndi("foo", dataSource);
        this.context = new AnnotationConfigApplicationContext();
        TestPropertyValues.of("spring.datasource.jndi-name:foo").applyTo(this.context);
        this.context.register(JndiDataSourceAutoConfiguration.class, JndiDataSourceAutoConfigurationTests.MBeanExporterConfiguration.class, JndiDataSourceAutoConfigurationTests.AnotherMBeanExporterConfiguration.class);
        this.context.refresh();
        assertThat(this.context.getBean(DataSource.class)).isEqualTo(dataSource);
        for (MBeanExporter exporter : this.context.getBeansOfType(MBeanExporter.class).values()) {
            Set<String> excludedBeans = ((Set<String>) (ReflectionTestUtils.getField(exporter, "excludedBeans")));
            assertThat(excludedBeans).containsExactly("dataSource");
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void standardDataSourceIsNotExcludedFromExport() throws IllegalStateException, NamingException {
        DataSource dataSource = Mockito.mock(DataSource.class);
        configureJndi("foo", dataSource);
        this.context = new AnnotationConfigApplicationContext();
        TestPropertyValues.of("spring.datasource.jndi-name:foo").applyTo(this.context);
        this.context.register(JndiDataSourceAutoConfiguration.class, JndiDataSourceAutoConfigurationTests.MBeanExporterConfiguration.class);
        this.context.refresh();
        assertThat(this.context.getBean(DataSource.class)).isEqualTo(dataSource);
        MBeanExporter exporter = this.context.getBean(MBeanExporter.class);
        Set<String> excludedBeans = ((Set<String>) (ReflectionTestUtils.getField(exporter, "excludedBeans")));
        assertThat(excludedBeans).isEmpty();
    }

    @Configuration
    static class MBeanExporterConfiguration {
        @Bean
        MBeanExporter mbeanExporter() {
            return new MBeanExporter();
        }
    }

    @Configuration
    static class AnotherMBeanExporterConfiguration {
        @Bean
        MBeanExporter anotherMbeanExporter() {
            return new MBeanExporter();
        }
    }
}

