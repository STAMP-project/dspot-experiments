/**
 * Copyright 2014-2019 the original author or authors.
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


import javax.persistence.EntityManagerFactory;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;


/**
 * Unit tests for {@link EntityManagerBeanDefinitionRegistrarPostProcessor}.
 *
 * @author Oliver Gierke
 */
public class EntityManagerBeanDefinitionRegistrarPostProcessorUnitTests {
    // DATAJPA-453
    @Test
    public void findsBeanDefinitionInParentBeanFactory() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.registerBeanDefinition("factory", new RootBeanDefinition(LocalContainerEntityManagerFactoryBean.class));
        ConfigurableListableBeanFactory childFactory = new DefaultListableBeanFactory(beanFactory);
        BeanFactoryPostProcessor processor = new EntityManagerBeanDefinitionRegistrarPostProcessor();
        processor.postProcessBeanFactory(childFactory);
        Assert.assertThat(beanFactory.getBeanDefinitionCount(), CoreMatchers.is(2));
    }

    // DATAJPA-1005, DATAJPA-1045
    @Test
    public void discoversFactoryBeanReturningConcreteEntityManagerFactoryType() {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(EntityManagerBeanDefinitionRegistrarPostProcessorUnitTests.StubEntityManagerFactoryBean.class);
        builder.addConstructorArgValue(EntityManagerBeanDefinitionRegistrarPostProcessorUnitTests.SpecialEntityManagerFactory.class);
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.registerBeanDefinition("factory", builder.getBeanDefinition());
        BeanFactoryPostProcessor processor = new EntityManagerBeanDefinitionRegistrarPostProcessor();
        processor.postProcessBeanFactory(beanFactory);
        Assert.assertThat(beanFactory.getBeanDefinitionCount(), CoreMatchers.is(2));
    }

    interface SpecialEntityManagerFactory extends EntityManagerFactory {}

    static class StubEntityManagerFactoryBean extends LocalContainerEntityManagerFactoryBean {
        private final Class<? extends EntityManagerFactory> emfType;

        public StubEntityManagerFactoryBean(Class<? extends EntityManagerFactory> emfType) {
            this.emfType = emfType;
        }

        @Override
        public Class<? extends EntityManagerFactory> getObjectType() {
            return emfType;
        }

        @Override
        protected EntityManagerFactory createEntityManagerFactoryProxy(EntityManagerFactory emf) {
            return Mockito.mock(emfType);
        }
    }
}

