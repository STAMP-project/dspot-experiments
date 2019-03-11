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
package org.springframework.data.jpa.repository.config;


import AuditingBeanDefinitionParser.AUDITING_ENTITY_LISTENER_CLASS_NAME;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanDefinitionParsingException;
import org.springframework.instrument.classloading.ShadowingClassLoader;


/**
 * Integration tests for {@link AuditingBeanDefinitionParser}.
 *
 * @author Oliver Gierke
 * @author Thomas Darimont
 */
public class AuditingBeanDefinitionParserTests {
    @Test
    public void settingDatesIsConfigured() {
        assertSetDatesIsSetTo("auditing/auditing-namespace-context.xml", "true");
    }

    @Test
    public void notSettingDatesIsConfigured() {
        assertSetDatesIsSetTo("auditing/auditing-namespace-context2.xml", "false");
    }

    // DATAJPA-9
    @Test
    public void wiresDateTimeProviderIfConfigured() {
        BeanDefinition definition = getBeanDefinition("auditing/auditing-namespace-context3.xml");
        PropertyValue value = definition.getPropertyValues().getPropertyValue("dateTimeProvider");
        Assert.assertThat(value, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(value.getValue(), CoreMatchers.is(CoreMatchers.instanceOf(RuntimeBeanReference.class)));
        Assert.assertThat(getBeanName(), CoreMatchers.is("dateTimeProvider"));
        BeanFactory factory = loadFactoryFrom("auditing/auditing-namespace-context3.xml");
        Object bean = factory.getBean(AUDITING_ENTITY_LISTENER_CLASS_NAME);
        Assert.assertThat(bean, CoreMatchers.is(CoreMatchers.notNullValue()));
    }

    // DATAJPA-367
    @Test(expected = BeanDefinitionParsingException.class)
    public void shouldThrowBeanDefinitionParsingExceptionIfClassFromSpringAspectsJarCannotBeFound() {
        ShadowingClassLoader scl = new ShadowingClassLoader(getClass().getClassLoader());
        scl.excludeClass(AUDITING_ENTITY_LISTENER_CLASS_NAME);
        loadFactoryFrom("auditing/auditing-namespace-context.xml", scl);
    }
}

