/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.spring.config.annotation;


import java.util.concurrent.atomic.AtomicInteger;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.eventhandling.AnnotationEventHandlerAdapter;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.SimpleEventBus;
import org.axonframework.messaging.annotation.ParameterResolverFactory;
import org.axonframework.messaging.annotation.UnsupportedHandlerException;
import org.axonframework.spring.config.AnnotationDriven;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.jmx.support.RegistrationPolicy;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 *
 *
 * @author Allard Buijze
 */
@ContextConfiguration(classes = { SpringBeanParameterResolverFactoryTest.AppContext.class })
@RunWith(SpringJUnit4ClassRunner.class)
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
public class SpringBeanParameterResolverFactoryTest {
    @Autowired
    private SpringBeanParameterResolverFactoryTest.AnnotatedEventHandlerWithResources annotatedHandler;

    @Autowired
    private ApplicationContext applicationContext;

    private static final AtomicInteger counter = new AtomicInteger();

    @Autowired
    private ParameterResolverFactory parameterResolver;

    @Test
    public void testMethodsAreProperlyInjected() throws Exception {
        Assert.assertNotNull(annotatedHandler);
        new AnnotationEventHandlerAdapter(annotatedHandler, parameterResolver).handle(asEventMessage("Hello"));
        // make sure the invocation actually happened
        Assert.assertEquals(1, SpringBeanParameterResolverFactoryTest.counter.get());
    }

    @Test
    public void testNewInstanceIsCreatedEachTimePrototypeResourceIsInjected() throws Exception {
        Object handler = applicationContext.getBean("prototypeResourceHandler");
        AnnotationEventHandlerAdapter adapter = new AnnotationEventHandlerAdapter(handler, applicationContext.getBean(ParameterResolverFactory.class));
        adapter.handle(asEventMessage("Hello1"));
        adapter.handle(asEventMessage("Hello2"));
        Assert.assertEquals(2, SpringBeanParameterResolverFactoryTest.counter.get());
    }

    @Test(expected = UnsupportedHandlerException.class)
    public void testMethodsAreProperlyInjected_ErrorOnMissingParameterType() {
        // this should generate an error
        new AnnotationEventHandlerAdapter(applicationContext.getBean("missingResourceHandler"), parameterResolver);
    }

    @Test(expected = UnsupportedHandlerException.class)
    public void testMethodsAreProperlyInjected_ErrorOnDuplicateParameterType() {
        // this should generate an error
        new AnnotationEventHandlerAdapter(applicationContext.getBean("duplicateResourceHandler"), parameterResolver);
    }

    @Test
    @DirtiesContext
    public void testMethodsAreProperlyInjected_DuplicateParameterTypeWithPrimary() throws Exception {
        // this should generate an error
        new AnnotationEventHandlerAdapter(applicationContext.getBean("duplicateResourceHandlerWithPrimary"), parameterResolver).handle(asEventMessage("Hi there"));
        Assert.assertEquals(1, SpringBeanParameterResolverFactoryTest.counter.get());
    }

    @Test
    @DirtiesContext
    public void testMethodsAreProperlyInjected_DuplicateParameterTypeWithQualifier() throws Exception {
        new AnnotationEventHandlerAdapter(applicationContext.getBean("duplicateResourceHandlerWithQualifier"), parameterResolver).handle(asEventMessage("Hi there"));
        Assert.assertEquals(1, SpringBeanParameterResolverFactoryTest.counter.get());
    }

    @Test
    @DirtiesContext
    public void testMethodsAreProperlyInjected_QualifierPrecedesPrimary() throws Exception {
        new AnnotationEventHandlerAdapter(applicationContext.getBean("duplicateResourceHandlerWithQualifierAndPrimary"), parameterResolver).handle(asEventMessage("Hi there"));
        Assert.assertEquals(1, SpringBeanParameterResolverFactoryTest.counter.get());
    }

    public interface DuplicateResourceWithPrimary {
        boolean isPrimary();
    }

    public interface DuplicateResource {}

    public interface DuplicateResourceWithQualifier {}

    @AnnotationDriven
    @Configuration
    public static class AppContext {
        @Bean(name = "annotatedHandler")
        public SpringBeanParameterResolverFactoryTest.AnnotatedEventHandlerWithResources createHandler() {
            return new SpringBeanParameterResolverFactoryTest.AnnotatedEventHandlerWithResources();
        }

        @Lazy
        @Bean
        public SpringBeanParameterResolverFactoryTest.MissingResourceHandler missingResourceHandler() {
            return new SpringBeanParameterResolverFactoryTest.MissingResourceHandler();
        }

        @Lazy
        @Bean
        public SpringBeanParameterResolverFactoryTest.DuplicateResourceHandler duplicateResourceHandler() {
            return new SpringBeanParameterResolverFactoryTest.DuplicateResourceHandler();
        }

        @Lazy
        @Bean
        public SpringBeanParameterResolverFactoryTest.DuplicateResourceHandlerWithPrimary duplicateResourceHandlerWithPrimary() {
            return new SpringBeanParameterResolverFactoryTest.DuplicateResourceHandlerWithPrimary();
        }

        @Lazy
        @Bean
        public SpringBeanParameterResolverFactoryTest.DuplicateResourceHandlerWithQualifier duplicateResourceHandlerWithQualifier() {
            return new SpringBeanParameterResolverFactoryTest.DuplicateResourceHandlerWithQualifier();
        }

        @Lazy
        @Bean
        public SpringBeanParameterResolverFactoryTest.DuplicateResourceHandlerWithQualifierAndPrimary duplicateResourceHandlerWithQualifierAndPrimary() {
            return new SpringBeanParameterResolverFactoryTest.DuplicateResourceHandlerWithQualifierAndPrimary();
        }

        @Lazy
        @Bean
        public SpringBeanParameterResolverFactoryTest.PrototypeResourceHandler prototypeResourceHandler() {
            return new SpringBeanParameterResolverFactoryTest.PrototypeResourceHandler();
        }

        @Bean
        public SpringBeanParameterResolverFactoryTest.DuplicateResource duplicateResource1() {
            return new SpringBeanParameterResolverFactoryTest.DuplicateResource() {};
        }

        @Bean
        public SpringBeanParameterResolverFactoryTest.DuplicateResource duplicateResource2() {
            return new SpringBeanParameterResolverFactoryTest.DuplicateResource() {};
        }

        @Primary
        @Bean
        public SpringBeanParameterResolverFactoryTest.DuplicateResourceWithPrimary duplicateResourceWithPrimary1() {
            return () -> true;
        }

        @Bean
        public SpringBeanParameterResolverFactoryTest.DuplicateResourceWithPrimary duplicateResourceWithPrimary2() {
            return () -> false;
        }

        @Bean
        public SpringBeanParameterResolverFactoryTest.DuplicateResourceWithQualifier duplicateResourceWithQualifier1() {
            return new SpringBeanParameterResolverFactoryTest.DuplicateResourceWithQualifier() {};
        }

        @Bean("qualifiedByName")
        public SpringBeanParameterResolverFactoryTest.DuplicateResourceWithQualifier duplicateResourceWithQualifier2() {
            return new SpringBeanParameterResolverFactoryTest.DuplicateResourceWithQualifier() {};
        }

        @Bean
        @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
        public SpringBeanParameterResolverFactoryTest.PrototypeResource prototypeResource() {
            return new SpringBeanParameterResolverFactoryTest.PrototypeResource() {};
        }

        @Bean
        public CommandBus commandBus() {
            return SimpleCommandBus.builder().build();
        }

        @Bean
        public EventBus eventBus() {
            return SimpleEventBus.builder().build();
        }
    }

    public interface PrototypeResource {}

    public static class MissingResourceHandler {
        @EventHandler
        public void handle(String message, SpringBeanParameterResolverFactoryTest.ThisResourceReallyDoesntExist dataSource) {
            SpringBeanParameterResolverFactoryTest.counter.incrementAndGet();
        }
    }

    public static class DuplicateResourceHandler {
        @EventHandler
        public void handle(String message, SpringBeanParameterResolverFactoryTest.DuplicateResource dataSource) {
            SpringBeanParameterResolverFactoryTest.counter.incrementAndGet();
        }
    }

    public static class DuplicateResourceHandlerWithPrimary {
        @EventHandler
        public void handle(String message, SpringBeanParameterResolverFactoryTest.DuplicateResourceWithPrimary dataSource) {
            SpringBeanParameterResolverFactoryTest.counter.incrementAndGet();
        }
    }

    public static class DuplicateResourceHandlerWithQualifier {
        @EventHandler
        public void handle(String message, @Qualifier("qualifiedByName")
        SpringBeanParameterResolverFactoryTest.DuplicateResourceWithQualifier resource) {
            SpringBeanParameterResolverFactoryTest.counter.incrementAndGet();
        }
    }

    public static class DuplicateResourceHandlerWithQualifierAndPrimary {
        @EventHandler
        public void handle(String message, @Qualifier("duplicateResourceWithPrimary2")
        SpringBeanParameterResolverFactoryTest.DuplicateResourceWithPrimary resource) {
            Assert.assertFalse("expect the non-primary bean to be autowired here", resource.isPrimary());
            SpringBeanParameterResolverFactoryTest.counter.incrementAndGet();
        }
    }

    public static class PrototypeResourceHandler {
        private SpringBeanParameterResolverFactoryTest.PrototypeResource resource;

        @EventHandler
        public void handle(String message, SpringBeanParameterResolverFactoryTest.PrototypeResource resource) {
            Assert.assertNotEquals(this.resource, (this.resource = resource));
            SpringBeanParameterResolverFactoryTest.counter.incrementAndGet();
        }
    }

    public static class AnnotatedEventHandlerWithResources {
        @EventHandler
        public void handle(String message, CommandBus commandBus, EventBus eventBus) {
            Assert.assertNotNull(message);
            Assert.assertNotNull(commandBus);
            Assert.assertNotNull(eventBus);
            SpringBeanParameterResolverFactoryTest.counter.incrementAndGet();
        }
    }

    private interface ThisResourceReallyDoesntExist {}
}

