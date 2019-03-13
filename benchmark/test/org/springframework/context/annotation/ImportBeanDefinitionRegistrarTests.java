/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.context.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.MessageSource;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;


/**
 * Integration tests for {@link ImportBeanDefinitionRegistrar}.
 *
 * @author Oliver Gierke
 * @author Chris Beams
 */
public class ImportBeanDefinitionRegistrarTests {
    @Test
    public void shouldInvokeAwareMethodsInImportBeanDefinitionRegistrar() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ImportBeanDefinitionRegistrarTests.Config.class);
        context.getBean(MessageSource.class);
        Assert.assertThat(ImportBeanDefinitionRegistrarTests.SampleRegistrar.beanFactory, CoreMatchers.is(context.getBeanFactory()));
        Assert.assertThat(ImportBeanDefinitionRegistrarTests.SampleRegistrar.classLoader, CoreMatchers.is(context.getBeanFactory().getBeanClassLoader()));
        Assert.assertThat(ImportBeanDefinitionRegistrarTests.SampleRegistrar.resourceLoader, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(ImportBeanDefinitionRegistrarTests.SampleRegistrar.environment, CoreMatchers.is(context.getEnvironment()));
    }

    @ImportBeanDefinitionRegistrarTests.Sample
    @Configuration
    static class Config {}

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Import(ImportBeanDefinitionRegistrarTests.SampleRegistrar.class)
    public @interface Sample {}

    private static class SampleRegistrar implements BeanClassLoaderAware , BeanFactoryAware , EnvironmentAware , ResourceLoaderAware , ImportBeanDefinitionRegistrar {
        static ClassLoader classLoader;

        static ResourceLoader resourceLoader;

        static BeanFactory beanFactory;

        static Environment environment;

        @Override
        public void setBeanClassLoader(ClassLoader classLoader) {
            ImportBeanDefinitionRegistrarTests.SampleRegistrar.classLoader = classLoader;
        }

        @Override
        public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
            ImportBeanDefinitionRegistrarTests.SampleRegistrar.beanFactory = beanFactory;
        }

        @Override
        public void setResourceLoader(ResourceLoader resourceLoader) {
            ImportBeanDefinitionRegistrarTests.SampleRegistrar.resourceLoader = resourceLoader;
        }

        @Override
        public void setEnvironment(Environment environment) {
            ImportBeanDefinitionRegistrarTests.SampleRegistrar.environment = environment;
        }

        @Override
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        }
    }
}

