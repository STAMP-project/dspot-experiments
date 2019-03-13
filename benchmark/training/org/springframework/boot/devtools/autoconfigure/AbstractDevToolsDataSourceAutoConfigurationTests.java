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
package org.springframework.boot.devtools.autoconfigure;


import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import javax.sql.DataSource;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Base class for tests for {@link DevToolsDataSourceAutoConfiguration}.
 *
 * @author Andy Wilkinson
 */
public abstract class AbstractDevToolsDataSourceAutoConfigurationTests {
    @Test
    public void singleManuallyConfiguredDataSourceIsNotClosed() throws SQLException {
        ConfigurableApplicationContext context = createContext(AbstractDevToolsDataSourceAutoConfigurationTests.SingleDataSourceConfiguration.class);
        DataSource dataSource = context.getBean(DataSource.class);
        Statement statement = configureDataSourceBehavior(dataSource);
        Mockito.verify(statement, Mockito.never()).execute("SHUTDOWN");
    }

    @Test
    public void multipleDataSourcesAreIgnored() throws SQLException {
        ConfigurableApplicationContext context = createContext(AbstractDevToolsDataSourceAutoConfigurationTests.MultipleDataSourcesConfiguration.class);
        Collection<DataSource> dataSources = context.getBeansOfType(DataSource.class).values();
        for (DataSource dataSource : dataSources) {
            Statement statement = configureDataSourceBehavior(dataSource);
            Mockito.verify(statement, Mockito.never()).execute("SHUTDOWN");
        }
    }

    @Test
    public void emptyFactoryMethodMetadataIgnored() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        DataSource dataSource = Mockito.mock(DataSource.class);
        AnnotatedGenericBeanDefinition beanDefinition = new AnnotatedGenericBeanDefinition(dataSource.getClass());
        context.registerBeanDefinition("dataSource", beanDefinition);
        context.register(DevToolsDataSourceAutoConfiguration.class);
        context.refresh();
        context.close();
    }

    @Configuration
    static class SingleDataSourceConfiguration {
        @Bean
        public DataSource dataSource() {
            return Mockito.mock(DataSource.class);
        }
    }

    @Configuration
    static class MultipleDataSourcesConfiguration {
        @Bean
        public DataSource dataSourceOne() {
            return Mockito.mock(DataSource.class);
        }

        @Bean
        public DataSource dataSourceTwo() {
            return Mockito.mock(DataSource.class);
        }
    }

    @Configuration
    static class DataSourceSpyConfiguration {
        @Bean
        public AbstractDevToolsDataSourceAutoConfigurationTests.DataSourceSpyBeanPostProcessor dataSourceSpyBeanPostProcessor() {
            return new AbstractDevToolsDataSourceAutoConfigurationTests.DataSourceSpyBeanPostProcessor();
        }
    }

    private static class DataSourceSpyBeanPostProcessor implements BeanPostProcessor {
        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            if (bean instanceof DataSource) {
                bean = Mockito.spy(bean);
            }
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            return bean;
        }
    }
}

