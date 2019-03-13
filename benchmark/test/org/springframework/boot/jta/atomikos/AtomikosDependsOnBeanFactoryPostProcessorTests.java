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
package org.springframework.boot.jta.atomikos;


import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.jms.extra.MessageDrivenContainer;
import javax.jms.ConnectionFactory;
import javax.sql.DataSource;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Tests for {@link AtomikosDependsOnBeanFactoryPostProcessor}.
 *
 * @author Phillip Webb
 */
public class AtomikosDependsOnBeanFactoryPostProcessorTests {
    private AnnotationConfigApplicationContext context;

    @Test
    public void setsDependsOn() {
        this.context = new AnnotationConfigApplicationContext(AtomikosDependsOnBeanFactoryPostProcessorTests.Config.class);
        assertDependsOn("dataSource");
        assertDependsOn("connectionFactory");
        assertDependsOn("userTransactionManager", "dataSource", "connectionFactory");
        assertDependsOn("messageDrivenContainer", "userTransactionManager");
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
        public UserTransactionManager userTransactionManager() {
            return Mockito.mock(UserTransactionManager.class);
        }

        @Bean
        public MessageDrivenContainer messageDrivenContainer() {
            return Mockito.mock(MessageDrivenContainer.class);
        }

        @Bean
        public static AtomikosDependsOnBeanFactoryPostProcessor atomikosPostProcessor() {
            return new AtomikosDependsOnBeanFactoryPostProcessor();
        }
    }
}

