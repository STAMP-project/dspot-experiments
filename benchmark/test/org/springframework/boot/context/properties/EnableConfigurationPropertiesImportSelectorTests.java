/**
 * Copyright 2012-2019 the original author or authors.
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
package org.springframework.boot.context.properties;


import EnableConfigurationPropertiesImportSelector.ConfigurationPropertiesBeanRegistrar;
import java.io.IOException;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.type.AnnotationMetadata;


/**
 * Tests for {@link EnableConfigurationPropertiesImportSelector}.
 *
 * @author Madhura Bhave
 * @author Stephane Nicoll
 */
public class EnableConfigurationPropertiesImportSelectorTests {
    private final EnableConfigurationPropertiesImportSelector importSelector = new EnableConfigurationPropertiesImportSelector();

    private final ConfigurationPropertiesBeanRegistrar registrar = new EnableConfigurationPropertiesImportSelector.ConfigurationPropertiesBeanRegistrar();

    private final DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

    @Test
    public void selectImports() {
        String[] imports = this.importSelector.selectImports(Mockito.mock(AnnotationMetadata.class));
        assertThat(imports).containsExactly(ConfigurationPropertiesBeanRegistrar.class.getName(), ConfigurationPropertiesBindingPostProcessorRegistrar.class.getName());
    }

    @Test
    public void typeWithDefaultConstructorShouldRegisterGenericBeanDefinition() throws Exception {
        this.registrar.registerBeanDefinitions(getAnnotationMetadata(EnableConfigurationPropertiesImportSelectorTests.TestConfiguration.class), this.beanFactory);
        BeanDefinition beanDefinition = this.beanFactory.getBeanDefinition("foo-org.springframework.boot.context.properties.EnableConfigurationPropertiesImportSelectorTests$FooProperties");
        assertThat(beanDefinition).isExactlyInstanceOf(GenericBeanDefinition.class);
    }

    @Test
    public void typeWithAutowiredOnConstructorShouldRegisterGenericBeanDefinition() throws Exception {
        this.registrar.registerBeanDefinitions(getAnnotationMetadata(EnableConfigurationPropertiesImportSelectorTests.TestConfiguration.class), this.beanFactory);
        BeanDefinition beanDefinition = this.beanFactory.getBeanDefinition("bar-org.springframework.boot.context.properties.EnableConfigurationPropertiesImportSelectorTests$BarProperties");
        assertThat(beanDefinition).isExactlyInstanceOf(GenericBeanDefinition.class);
    }

    @Test
    public void typeWithOneConstructorWithParametersShouldRegisterConfigurationPropertiesBeanDefinition() throws Exception {
        this.registrar.registerBeanDefinitions(getAnnotationMetadata(EnableConfigurationPropertiesImportSelectorTests.TestConfiguration.class), this.beanFactory);
        BeanDefinition beanDefinition = this.beanFactory.getBeanDefinition("baz-org.springframework.boot.context.properties.EnableConfigurationPropertiesImportSelectorTests$BazProperties");
        assertThat(beanDefinition).isExactlyInstanceOf(ConfigurationPropertiesBeanDefinition.class);
    }

    @Test
    public void typeWithMultipleConstructorsShouldRegisterGenericBeanDefinition() throws Exception {
        this.registrar.registerBeanDefinitions(getAnnotationMetadata(EnableConfigurationPropertiesImportSelectorTests.TestConfiguration.class), this.beanFactory);
        BeanDefinition beanDefinition = this.beanFactory.getBeanDefinition("bing-org.springframework.boot.context.properties.EnableConfigurationPropertiesImportSelectorTests$BingProperties");
        assertThat(beanDefinition).isExactlyInstanceOf(GenericBeanDefinition.class);
    }

    @Test
    public void typeWithNoAnnotationShouldFail() {
        assertThatIllegalArgumentException().isThrownBy(() -> this.registrar.registerBeanDefinitions(getAnnotationMetadata(.class), this.beanFactory)).withMessageContaining("No ConfigurationProperties annotation found").withMessageContaining(EnableConfigurationPropertiesImportSelectorTests.class.getName());
    }

    @Test
    public void registrationWithDuplicatedTypeShouldRegisterSingleBeanDefinition() throws IOException {
        DefaultListableBeanFactory factory = Mockito.spy(this.beanFactory);
        this.registrar.registerBeanDefinitions(getAnnotationMetadata(EnableConfigurationPropertiesImportSelectorTests.DuplicateConfiguration.class), factory);
        Mockito.verify(factory, Mockito.times(1)).registerBeanDefinition(ArgumentMatchers.anyString(), ArgumentMatchers.any());
    }

    @Test
    public void registrationWithNoTypeShouldNotRegisterAnything() throws IOException {
        DefaultListableBeanFactory factory = Mockito.spy(this.beanFactory);
        this.registrar.registerBeanDefinitions(getAnnotationMetadata(EnableConfigurationPropertiesImportSelectorTests.EmptyConfiguration.class), factory);
        Mockito.verifyZeroInteractions(factory);
    }

    @EnableConfigurationProperties({ EnableConfigurationPropertiesImportSelectorTests.FooProperties.class, EnableConfigurationPropertiesImportSelectorTests.BarProperties.class, EnableConfigurationPropertiesImportSelectorTests.BazProperties.class, EnableConfigurationPropertiesImportSelectorTests.BingProperties.class })
    static class TestConfiguration {}

    @EnableConfigurationProperties(EnableConfigurationPropertiesImportSelectorTests.class)
    static class InvalidConfiguration {}

    @EnableConfigurationProperties({ EnableConfigurationPropertiesImportSelectorTests.FooProperties.class, EnableConfigurationPropertiesImportSelectorTests.FooProperties.class })
    static class DuplicateConfiguration {}

    @EnableConfigurationProperties
    static class EmptyConfiguration {}

    @ConfigurationProperties(prefix = "foo")
    static class FooProperties {}

    @ConfigurationProperties(prefix = "bar")
    public static class BarProperties {
        @Autowired
        public BarProperties(String foo) {
        }
    }

    @ConfigurationProperties(prefix = "baz")
    public static class BazProperties {
        public BazProperties(String foo) {
        }
    }

    @ConfigurationProperties(prefix = "bing")
    public static class BingProperties {
        public BingProperties() {
        }

        public BingProperties(String foo) {
        }
    }
}

