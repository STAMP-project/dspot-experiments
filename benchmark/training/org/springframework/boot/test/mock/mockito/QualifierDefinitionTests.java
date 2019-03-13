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
package org.springframework.boot.test.mock.mockito;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;


/**
 * Tests for {@link QualifierDefinition}.
 *
 * @author Phillip Webb
 */
public class QualifierDefinitionTests {
    @Mock
    private ConfigurableListableBeanFactory beanFactory;

    @Captor
    private ArgumentCaptor<DependencyDescriptor> descriptorCaptor;

    @Test
    public void forElementFieldIsNullShouldReturnNull() {
        assertThat(QualifierDefinition.forElement(((Field) (null)))).isNull();
    }

    @Test
    public void forElementWhenElementIsNotFieldShouldReturnNull() {
        assertThat(QualifierDefinition.forElement(getClass())).isNull();
    }

    @Test
    public void forElementWhenElementIsFieldWithNoQualifiersShouldReturnNull() {
        QualifierDefinition definition = QualifierDefinition.forElement(ReflectionUtils.findField(QualifierDefinitionTests.ConfigA.class, "noQualifier"));
        assertThat(definition).isNull();
    }

    @Test
    public void forElementWhenElementIsFieldWithQualifierShouldReturnDefinition() {
        QualifierDefinition definition = QualifierDefinition.forElement(ReflectionUtils.findField(QualifierDefinitionTests.ConfigA.class, "directQualifier"));
        assertThat(definition).isNotNull();
    }

    @Test
    public void matchesShouldCallBeanFactory() {
        Field field = ReflectionUtils.findField(QualifierDefinitionTests.ConfigA.class, "directQualifier");
        QualifierDefinition qualifierDefinition = QualifierDefinition.forElement(field);
        qualifierDefinition.matches(this.beanFactory, "bean");
        Mockito.verify(this.beanFactory).isAutowireCandidate(ArgumentMatchers.eq("bean"), this.descriptorCaptor.capture());
        assertThat(this.descriptorCaptor.getValue().getAnnotatedElement()).isEqualTo(field);
    }

    @Test
    public void applyToShouldSetQualifierElement() {
        Field field = ReflectionUtils.findField(QualifierDefinitionTests.ConfigA.class, "directQualifier");
        QualifierDefinition qualifierDefinition = QualifierDefinition.forElement(field);
        RootBeanDefinition definition = new RootBeanDefinition();
        qualifierDefinition.applyTo(definition);
        assertThat(definition.getQualifiedElement()).isEqualTo(field);
    }

    @Test
    public void hashCodeAndEqualsShouldWorkOnDifferentClasses() {
        QualifierDefinition directQualifier1 = QualifierDefinition.forElement(ReflectionUtils.findField(QualifierDefinitionTests.ConfigA.class, "directQualifier"));
        QualifierDefinition directQualifier2 = QualifierDefinition.forElement(ReflectionUtils.findField(QualifierDefinitionTests.ConfigB.class, "directQualifier"));
        QualifierDefinition differentDirectQualifier1 = QualifierDefinition.forElement(ReflectionUtils.findField(QualifierDefinitionTests.ConfigA.class, "differentDirectQualifier"));
        QualifierDefinition differentDirectQualifier2 = QualifierDefinition.forElement(ReflectionUtils.findField(QualifierDefinitionTests.ConfigB.class, "differentDirectQualifier"));
        QualifierDefinition customQualifier1 = QualifierDefinition.forElement(ReflectionUtils.findField(QualifierDefinitionTests.ConfigA.class, "customQualifier"));
        QualifierDefinition customQualifier2 = QualifierDefinition.forElement(ReflectionUtils.findField(QualifierDefinitionTests.ConfigB.class, "customQualifier"));
        assertThat(directQualifier1.hashCode()).isEqualTo(directQualifier2.hashCode());
        assertThat(differentDirectQualifier1.hashCode()).isEqualTo(differentDirectQualifier2.hashCode());
        assertThat(customQualifier1.hashCode()).isEqualTo(customQualifier2.hashCode());
        assertThat(differentDirectQualifier1).isEqualTo(differentDirectQualifier1).isEqualTo(differentDirectQualifier2).isNotEqualTo(directQualifier2);
        assertThat(directQualifier1).isEqualTo(directQualifier1).isEqualTo(directQualifier2).isNotEqualTo(differentDirectQualifier1);
        assertThat(customQualifier1).isEqualTo(customQualifier1).isEqualTo(customQualifier2).isNotEqualTo(differentDirectQualifier1);
    }

    @Configuration
    static class ConfigA {
        @MockBean
        private Object noQualifier;

        @MockBean
        @Qualifier("test")
        private Object directQualifier;

        @MockBean
        @Qualifier("different")
        private Object differentDirectQualifier;

        @MockBean
        @QualifierDefinitionTests.CustomQualifier
        private Object customQualifier;
    }

    static class ConfigB {
        @MockBean
        @Qualifier("test")
        private Object directQualifier;

        @MockBean
        @Qualifier("different")
        private Object differentDirectQualifier;

        @MockBean
        @QualifierDefinitionTests.CustomQualifier
        private Object customQualifier;
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface CustomQualifier {}
}

