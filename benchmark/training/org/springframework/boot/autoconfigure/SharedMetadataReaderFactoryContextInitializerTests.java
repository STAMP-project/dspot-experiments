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
package org.springframework.boot.autoconfigure;


import SharedMetadataReaderFactoryContextInitializer.BEAN_NAME;
import WebApplicationType.NONE;
import java.util.List;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;


/**
 * Tests for {@link SharedMetadataReaderFactoryContextInitializer}.
 *
 * @author Dave Syer
 */
public class SharedMetadataReaderFactoryContextInitializerTests {
    @Test
    public void checkOrderOfInitializer() {
        SpringApplication application = new SpringApplication(SharedMetadataReaderFactoryContextInitializerTests.TestConfig.class);
        application.setWebApplicationType(NONE);
        @SuppressWarnings("unchecked")
        List<ApplicationContextInitializer<?>> initializers = ((List<ApplicationContextInitializer<?>>) (ReflectionTestUtils.getField(application, "initializers")));
        // Simulate what would happen if an initializer was added using spring.factories
        // and happened to be loaded first
        initializers.add(0, new SharedMetadataReaderFactoryContextInitializerTests.Initializer());
        GenericApplicationContext context = ((GenericApplicationContext) (application.run()));
        BeanDefinition definition = context.getBeanDefinition(BEAN_NAME);
        assertThat(definition.getAttribute("seen")).isEqualTo(true);
    }

    protected static class TestConfig {}

    static class Initializer implements ApplicationContextInitializer<GenericApplicationContext> {
        @Override
        public void initialize(GenericApplicationContext applicationContext) {
            applicationContext.addBeanFactoryPostProcessor(new SharedMetadataReaderFactoryContextInitializerTests.PostProcessor());
        }
    }

    static class PostProcessor implements BeanDefinitionRegistryPostProcessor {
        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        }

        @Override
        public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
            for (String name : registry.getBeanDefinitionNames()) {
                BeanDefinition definition = registry.getBeanDefinition(name);
                definition.setAttribute("seen", true);
            }
        }
    }
}

