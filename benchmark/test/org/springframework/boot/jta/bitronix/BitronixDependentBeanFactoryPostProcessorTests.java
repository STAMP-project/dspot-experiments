/**
 * Copyright 2012-2017 the original author or authors.
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
package org.springframework.boot.jta.bitronix;


import bitronix.tm.BitronixTransactionManager;
import javax.jms.ConnectionFactory;
import javax.sql.DataSource;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Tests for {@link BitronixDependentBeanFactoryPostProcessor}.
 *
 * @author Phillip Webb
 */
public class BitronixDependentBeanFactoryPostProcessorTests {
    private AnnotationConfigApplicationContext context;

    @Test
    public void setsDependsOn() {
        DefaultListableBeanFactory beanFactory = Mockito.spy(new DefaultListableBeanFactory());
        this.context = new AnnotationConfigApplicationContext(beanFactory);
        this.context.register(BitronixDependentBeanFactoryPostProcessorTests.Config.class);
        this.context.refresh();
        String name = "bitronixTransactionManager";
        Mockito.verify(beanFactory).registerDependentBean(name, "dataSource");
        Mockito.verify(beanFactory).registerDependentBean(name, "connectionFactory");
        this.context.close();
    }

    @Configuration
    static class Config {
        @Bean
        public DataSource dataSource() {
            return Mockito.mock(DataSource.class);
        }

        @Bean
        public ConnectionFactory connectionFactory() {
            return Mockito.mock(ConnectionFactory.class);
        }

        @Bean
        public BitronixTransactionManager bitronixTransactionManager() {
            return Mockito.mock(BitronixTransactionManager.class);
        }

        @Bean
        public static BitronixDependentBeanFactoryPostProcessor bitronixPostProcessor() {
            return new BitronixDependentBeanFactoryPostProcessor();
        }
    }
}

