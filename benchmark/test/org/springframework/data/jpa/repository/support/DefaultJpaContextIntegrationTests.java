/**
 * Copyright 2015-2019 the original author or authors.
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
package org.springframework.data.jpa.repository.support;


import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.jpa.domain.sample.Category;
import org.springframework.data.jpa.domain.sample.User;
import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.mock.jndi.ExpectedLookupTemplate;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Component;


/**
 * Integration tests for {@link DefaultJpaContext}.
 *
 * @author Oliver Gierke
 * @author Jens Schauder
 * @unknown Marcus Miller - Papa Was A Rolling Stone (Afrodeezia)
 */
public class DefaultJpaContextIntegrationTests {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    static EntityManagerFactory firstEmf;

    static EntityManagerFactory secondEmf;

    EntityManager firstEm;

    EntityManager secondEm;

    JpaContext jpaContext;

    // DATAJPA-669
    @Test
    public void rejectsUnmanagedType() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(Object.class.getSimpleName());
        jpaContext.getEntityManagerByManagedType(Object.class);
    }

    // DATAJPA-669
    @Test
    public void returnsEntitymanagerForUniqueType() {
        Assert.assertThat(jpaContext.getEntityManagerByManagedType(Category.class), CoreMatchers.is(firstEm));
    }

    // DATAJPA-669
    @Test
    public void rejectsRequestForTypeManagedByMultipleEntityManagers() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(User.class.getSimpleName());
        jpaContext.getEntityManagerByManagedType(User.class);
    }

    // DATAJPA-813, DATAJPA-956
    @Test
    public void bootstrapsDefaultJpaContextInSpringContainer() {
        ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(DefaultJpaContextIntegrationTests.Config.class);
        DefaultJpaContextIntegrationTests.ApplicationComponent component = context.getBean(DefaultJpaContextIntegrationTests.ApplicationComponent.class);
        Assert.assertThat(component.context, CoreMatchers.is(CoreMatchers.notNullValue()));
        context.close();
    }

    // DATAJPA-813
    @Test
    public void bootstrapsDefaultJpaContextInSpringContainerWithEntityManagerFromJndi() throws Exception {
        SimpleNamingContextBuilder builder = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
        builder.bind("some/EMF", DefaultJpaContextIntegrationTests.createEntityManagerFactory("spring-data-jpa"));
        builder.bind("some/other/Component", new Object());
        ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("config/jpa-context-with-jndi.xml");
        DefaultJpaContextIntegrationTests.ApplicationComponent component = context.getBean(DefaultJpaContextIntegrationTests.ApplicationComponent.class);
        Assert.assertThat(component.context, CoreMatchers.is(CoreMatchers.notNullValue()));
        context.close();
    }

    @EnableJpaRepositories
    @ComponentScan(includeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, value = DefaultJpaContextIntegrationTests.ApplicationComponent.class), useDefaultFilters = false)
    static class Config {
        @Bean
        public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
            return DefaultJpaContextIntegrationTests.createEntityManagerFactoryBean("spring-data-jpa");
        }

        // A non-EntityManagerFactory JNDI object to make sure the detection doesn't include it
        // see DATAJPA-956
        @Bean
        public JndiObjectFactoryBean jndiObject() throws NamingException {
            JndiObjectFactoryBean bean = new JndiObjectFactoryBean();
            bean.setJndiName("some/DataSource");
            bean.setJndiTemplate(new ExpectedLookupTemplate("some/DataSource", Mockito.mock(DataSource.class)));
            bean.setExpectedType(DataSource.class);
            return bean;
        }
    }

    @Component
    static class ApplicationComponent {
        JpaContext context;

        @Autowired
        public ApplicationComponent(JpaContext context) {
            this.context = context;
        }
    }
}

