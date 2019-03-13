/**
 * Copyright 2008-2019 the original author or authors.
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
package org.springframework.data.jpa.domain.support;


import AuditingBeanFactoryPostProcessor.BEAN_CONFIGURER_ASPECT_BEAN_NAME;
import javax.persistence.EntityManagerFactory;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;


/**
 * Unit test for {@link AuditingBeanFactoryPostProcessor}.
 *
 * @author Oliver Gierke
 * @author Thomas Darimont
 */
public class AuditingBeanFactoryPostProcessorUnitTests {
    DefaultListableBeanFactory beanFactory;

    AuditingBeanFactoryPostProcessor processor;

    @Test
    public void beanConfigurerAspectShouldBeConfiguredAfterPostProcessing() throws Exception {
        processor.postProcessBeanFactory(beanFactory);
        Assert.assertThat(beanFactory.isBeanNameInUse(BEAN_CONFIGURER_ASPECT_BEAN_NAME), is(true));
    }

    // DATAJPA-265
    @Test(expected = IllegalStateException.class)
    public void rejectsConfigurationWithoutSpringConfigured() {
        processor.postProcessBeanFactory(new DefaultListableBeanFactory());
    }

    // DATAJPA-265
    @Test
    public void setsDependsOnOnEntityManagerFactory() {
        processor.postProcessBeanFactory(beanFactory);
        String[] emfDefinitionNames = beanFactory.getBeanNamesForType(EntityManagerFactory.class);
        for (String emfDefinitionName : emfDefinitionNames) {
            BeanDefinition emfDefinition = beanFactory.getBeanDefinition(emfDefinitionName);
            Assert.assertThat(emfDefinition, is(notNullValue()));
            Assert.assertThat(emfDefinition.getDependsOn(), is(arrayContaining(BEAN_CONFIGURER_ASPECT_BEAN_NAME)));
        }
    }

    // DATAJPA-453
    @Test
    public void findsEntityManagerFactoryInParentBeanFactory() {
        DefaultListableBeanFactory childFactory = new DefaultListableBeanFactory(getBeanFactory());
        processor.postProcessBeanFactory(childFactory);
    }
}

