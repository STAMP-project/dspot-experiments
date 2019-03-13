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
package org.springframework.boot.autoconfigure.jdbc;


import javax.sql.DataSource;
import javax.sql.XADataSource;
import org.hsqldb.jdbc.pool.JDBCXADataSource;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.jdbc.XADataSourceWrapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Tests for {@link XADataSourceAutoConfiguration}.
 *
 * @author Phillip Webb
 */
public class XADataSourceAutoConfigurationTests {
    @Test
    public void wrapExistingXaDataSource() {
        ApplicationContext context = createContext(XADataSourceAutoConfigurationTests.WrapExisting.class);
        context.getBean(DataSource.class);
        XADataSource source = context.getBean(XADataSource.class);
        XADataSourceAutoConfigurationTests.MockXADataSourceWrapper wrapper = context.getBean(XADataSourceAutoConfigurationTests.MockXADataSourceWrapper.class);
        assertThat(wrapper.getXaDataSource()).isEqualTo(source);
    }

    @Test
    public void createFromUrl() {
        ApplicationContext context = createContext(XADataSourceAutoConfigurationTests.FromProperties.class, "spring.datasource.url:jdbc:hsqldb:mem:test", "spring.datasource.username:un");
        context.getBean(DataSource.class);
        XADataSourceAutoConfigurationTests.MockXADataSourceWrapper wrapper = context.getBean(XADataSourceAutoConfigurationTests.MockXADataSourceWrapper.class);
        JDBCXADataSource dataSource = ((JDBCXADataSource) (wrapper.getXaDataSource()));
        assertThat(dataSource).isNotNull();
        assertThat(dataSource.getUrl()).isEqualTo("jdbc:hsqldb:mem:test");
        assertThat(dataSource.getUser()).isEqualTo("un");
    }

    @Test
    public void createFromClass() throws Exception {
        ApplicationContext context = createContext(XADataSourceAutoConfigurationTests.FromProperties.class, "spring.datasource.xa.data-source-class-name:org.hsqldb.jdbc.pool.JDBCXADataSource", "spring.datasource.xa.properties.login-timeout:123");
        context.getBean(DataSource.class);
        XADataSourceAutoConfigurationTests.MockXADataSourceWrapper wrapper = context.getBean(XADataSourceAutoConfigurationTests.MockXADataSourceWrapper.class);
        JDBCXADataSource dataSource = ((JDBCXADataSource) (wrapper.getXaDataSource()));
        assertThat(dataSource).isNotNull();
        assertThat(dataSource.getLoginTimeout()).isEqualTo(123);
    }

    @Configuration
    static class WrapExisting {
        @Bean
        public XADataSourceAutoConfigurationTests.MockXADataSourceWrapper wrapper() {
            return new XADataSourceAutoConfigurationTests.MockXADataSourceWrapper();
        }

        @Bean
        public XADataSource xaDataSource() {
            return Mockito.mock(XADataSource.class);
        }
    }

    @Configuration
    static class FromProperties {
        @Bean
        public XADataSourceAutoConfigurationTests.MockXADataSourceWrapper wrapper() {
            return new XADataSourceAutoConfigurationTests.MockXADataSourceWrapper();
        }
    }

    private static class MockXADataSourceWrapper implements XADataSourceWrapper {
        private XADataSource dataSource;

        @Override
        public DataSource wrapDataSource(XADataSource dataSource) {
            this.dataSource = dataSource;
            return Mockito.mock(DataSource.class);
        }

        public XADataSource getXaDataSource() {
            return this.dataSource;
        }
    }
}

