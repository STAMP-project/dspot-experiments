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
package org.springframework.boot.autoconfigure.jms;


import javax.jms.ConnectionFactory;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;


/**
 * Tests for {@link JndiConnectionFactoryAutoConfiguration}.
 * PersistenceExceptionTranslationAutoConfigurationTests
 *
 * @author Stephane Nicoll
 */
public class JndiConnectionFactoryAutoConfigurationTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(JndiConnectionFactoryAutoConfiguration.class));

    private ClassLoader threadContextClassLoader;

    private String initialContextFactory;

    @Test
    public void detectNoAvailableCandidates() {
        this.contextRunner.run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void detectWithJmsXAConnectionFactory() {
        ConnectionFactory connectionFactory = configureConnectionFactory("java:/JmsXA");
        this.contextRunner.run(assertConnectionFactory(connectionFactory));
    }

    @Test
    public void detectWithXAConnectionFactory() {
        ConnectionFactory connectionFactory = configureConnectionFactory("java:/XAConnectionFactory");
        this.contextRunner.run(assertConnectionFactory(connectionFactory));
    }

    @Test
    public void jndiNamePropertySet() {
        ConnectionFactory connectionFactory = configureConnectionFactory("java:comp/env/myCF");
        this.contextRunner.withPropertyValues("spring.jms.jndi-name=java:comp/env/myCF").run(assertConnectionFactory(connectionFactory));
    }

    @Test
    public void jndiNamePropertySetWithResourceRef() {
        ConnectionFactory connectionFactory = configureConnectionFactory("java:comp/env/myCF");
        this.contextRunner.withPropertyValues("spring.jms.jndi-name=myCF").run(assertConnectionFactory(connectionFactory));
    }

    @Test
    public void jndiNamePropertySetWithWrongValue() {
        this.contextRunner.withPropertyValues("spring.jms.jndi-name=doesNotExistCF").run(( context) -> {
            assertThat(context).hasFailed();
            assertThat(context).getFailure().isInstanceOf(.class).hasMessageContaining("doesNotExistCF");
        });
    }
}

