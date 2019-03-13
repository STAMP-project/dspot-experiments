/**
 * Copyright 2002-2013 the original author or authors.
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
package org.springframework.beans.factory.config;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.NestedCheckedException;
import org.springframework.core.NestedRuntimeException;


/**
 * Unit tests for {@link ServiceLocatorFactoryBean}.
 *
 * @author Colin Sampaleanu
 * @author Rick Evans
 * @author Chris Beams
 */
public class ServiceLocatorFactoryBeanTests {
    private DefaultListableBeanFactory bf;

    @Test
    public void testNoArgGetter() {
        bf.registerBeanDefinition("testService", genericBeanDefinition(ServiceLocatorFactoryBeanTests.TestService.class).getBeanDefinition());
        bf.registerBeanDefinition("factory", genericBeanDefinition(ServiceLocatorFactoryBean.class).addPropertyValue("serviceLocatorInterface", ServiceLocatorFactoryBeanTests.TestServiceLocator.class).getBeanDefinition());
        ServiceLocatorFactoryBeanTests.TestServiceLocator factory = ((ServiceLocatorFactoryBeanTests.TestServiceLocator) (bf.getBean("factory")));
        ServiceLocatorFactoryBeanTests.TestService testService = factory.getTestService();
        Assert.assertNotNull(testService);
    }

    @Test
    public void testErrorOnTooManyOrTooFew() throws Exception {
        bf.registerBeanDefinition("testService", genericBeanDefinition(ServiceLocatorFactoryBeanTests.TestService.class).getBeanDefinition());
        bf.registerBeanDefinition("testServiceInstance2", genericBeanDefinition(ServiceLocatorFactoryBeanTests.TestService.class).getBeanDefinition());
        bf.registerBeanDefinition("factory", genericBeanDefinition(ServiceLocatorFactoryBean.class).addPropertyValue("serviceLocatorInterface", ServiceLocatorFactoryBeanTests.TestServiceLocator.class).getBeanDefinition());
        bf.registerBeanDefinition("factory2", genericBeanDefinition(ServiceLocatorFactoryBean.class).addPropertyValue("serviceLocatorInterface", ServiceLocatorFactoryBeanTests.TestServiceLocator2.class).getBeanDefinition());
        bf.registerBeanDefinition("factory3", genericBeanDefinition(ServiceLocatorFactoryBean.class).addPropertyValue("serviceLocatorInterface", ServiceLocatorFactoryBeanTests.TestService2Locator.class).getBeanDefinition());
        try {
            ServiceLocatorFactoryBeanTests.TestServiceLocator factory = ((ServiceLocatorFactoryBeanTests.TestServiceLocator) (bf.getBean("factory")));
            factory.getTestService();
            Assert.fail("Must fail on more than one matching type");
        } catch (NoSuchBeanDefinitionException ex) {
            /* expected */
        }
        try {
            ServiceLocatorFactoryBeanTests.TestServiceLocator2 factory = ((ServiceLocatorFactoryBeanTests.TestServiceLocator2) (bf.getBean("factory2")));
            factory.getTestService(null);
            Assert.fail("Must fail on more than one matching type");
        } catch (NoSuchBeanDefinitionException ex) {
            /* expected */
        }
        try {
            ServiceLocatorFactoryBeanTests.TestService2Locator factory = ((ServiceLocatorFactoryBeanTests.TestService2Locator) (bf.getBean("factory3")));
            factory.getTestService();
            Assert.fail("Must fail on no matching types");
        } catch (NoSuchBeanDefinitionException ex) {
            /* expected */
        }
    }

    @Test
    public void testErrorOnTooManyOrTooFewWithCustomServiceLocatorException() {
        bf.registerBeanDefinition("testService", genericBeanDefinition(ServiceLocatorFactoryBeanTests.TestService.class).getBeanDefinition());
        bf.registerBeanDefinition("testServiceInstance2", genericBeanDefinition(ServiceLocatorFactoryBeanTests.TestService.class).getBeanDefinition());
        bf.registerBeanDefinition("factory", genericBeanDefinition(ServiceLocatorFactoryBean.class).addPropertyValue("serviceLocatorInterface", ServiceLocatorFactoryBeanTests.TestServiceLocator.class).addPropertyValue("serviceLocatorExceptionClass", ServiceLocatorFactoryBeanTests.CustomServiceLocatorException1.class).getBeanDefinition());
        bf.registerBeanDefinition("factory2", genericBeanDefinition(ServiceLocatorFactoryBean.class).addPropertyValue("serviceLocatorInterface", ServiceLocatorFactoryBeanTests.TestServiceLocator2.class).addPropertyValue("serviceLocatorExceptionClass", ServiceLocatorFactoryBeanTests.CustomServiceLocatorException2.class).getBeanDefinition());
        bf.registerBeanDefinition("factory3", genericBeanDefinition(ServiceLocatorFactoryBean.class).addPropertyValue("serviceLocatorInterface", ServiceLocatorFactoryBeanTests.TestService2Locator.class).addPropertyValue("serviceLocatorExceptionClass", ServiceLocatorFactoryBeanTests.CustomServiceLocatorException3.class).getBeanDefinition());
        try {
            ServiceLocatorFactoryBeanTests.TestServiceLocator factory = ((ServiceLocatorFactoryBeanTests.TestServiceLocator) (bf.getBean("factory")));
            factory.getTestService();
            Assert.fail("Must fail on more than one matching type");
        } catch (ServiceLocatorFactoryBeanTests.CustomServiceLocatorException1 expected) {
            Assert.assertTrue(((getCause()) instanceof NoSuchBeanDefinitionException));
        }
        try {
            ServiceLocatorFactoryBeanTests.TestServiceLocator2 factory2 = ((ServiceLocatorFactoryBeanTests.TestServiceLocator2) (bf.getBean("factory2")));
            factory2.getTestService(null);
            Assert.fail("Must fail on more than one matching type");
        } catch (ServiceLocatorFactoryBeanTests.CustomServiceLocatorException2 expected) {
            Assert.assertTrue(((getCause()) instanceof NoSuchBeanDefinitionException));
        }
        try {
            ServiceLocatorFactoryBeanTests.TestService2Locator factory3 = ((ServiceLocatorFactoryBeanTests.TestService2Locator) (bf.getBean("factory3")));
            factory3.getTestService();
            Assert.fail("Must fail on no matching type");
        } catch (ServiceLocatorFactoryBeanTests.CustomServiceLocatorException3 ex) {
            /* expected */
        }
    }

    @Test
    public void testStringArgGetter() throws Exception {
        bf.registerBeanDefinition("testService", genericBeanDefinition(ServiceLocatorFactoryBeanTests.TestService.class).getBeanDefinition());
        bf.registerBeanDefinition("factory", genericBeanDefinition(ServiceLocatorFactoryBean.class).addPropertyValue("serviceLocatorInterface", ServiceLocatorFactoryBeanTests.TestServiceLocator2.class).getBeanDefinition());
        // test string-arg getter with null id
        ServiceLocatorFactoryBeanTests.TestServiceLocator2 factory = ((ServiceLocatorFactoryBeanTests.TestServiceLocator2) (bf.getBean("factory")));
        @SuppressWarnings("unused")
        ServiceLocatorFactoryBeanTests.TestService testBean = factory.getTestService(null);
        // now test with explicit id
        testBean = factory.getTestService("testService");
        // now verify failure on bad id
        try {
            factory.getTestService("bogusTestService");
            Assert.fail("Illegal operation allowed");
        } catch (NoSuchBeanDefinitionException ex) {
            /* expected */
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoServiceLocatorInterfaceSupplied() throws Exception {
        new ServiceLocatorFactoryBean().afterPropertiesSet();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWhenServiceLocatorInterfaceIsNotAnInterfaceType() throws Exception {
        ServiceLocatorFactoryBean factory = new ServiceLocatorFactoryBean();
        factory.setServiceLocatorInterface(getClass());
        factory.afterPropertiesSet();
        // should throw, bad (non-interface-type) serviceLocator interface supplied
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWhenServiceLocatorExceptionClassToExceptionTypeWithOnlyNoArgCtor() throws Exception {
        ServiceLocatorFactoryBean factory = new ServiceLocatorFactoryBean();
        factory.setServiceLocatorExceptionClass(ServiceLocatorFactoryBeanTests.ExceptionClassWithOnlyZeroArgCtor.class);
        // should throw, bad (invalid-Exception-type) serviceLocatorException class supplied
    }

    @Test(expected = IllegalArgumentException.class)
    @SuppressWarnings("unchecked")
    public void testWhenServiceLocatorExceptionClassIsNotAnExceptionSubclass() throws Exception {
        ServiceLocatorFactoryBean factory = new ServiceLocatorFactoryBean();
        factory.setServiceLocatorExceptionClass(((Class) (getClass())));
        // should throw, bad (non-Exception-type) serviceLocatorException class supplied
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testWhenServiceLocatorMethodCalledWithTooManyParameters() throws Exception {
        ServiceLocatorFactoryBean factory = new ServiceLocatorFactoryBean();
        factory.setServiceLocatorInterface(ServiceLocatorFactoryBeanTests.ServiceLocatorInterfaceWithExtraNonCompliantMethod.class);
        factory.afterPropertiesSet();
        ServiceLocatorFactoryBeanTests.ServiceLocatorInterfaceWithExtraNonCompliantMethod locator = ((ServiceLocatorFactoryBeanTests.ServiceLocatorInterfaceWithExtraNonCompliantMethod) (factory.getObject()));
        locator.getTestService("not", "allowed");// bad method (too many args, doesn't obey class contract)

    }

    @Test
    public void testRequiresListableBeanFactoryAndChokesOnAnythingElse() throws Exception {
        BeanFactory beanFactory = Mockito.mock(BeanFactory.class);
        try {
            ServiceLocatorFactoryBean factory = new ServiceLocatorFactoryBean();
            factory.setBeanFactory(beanFactory);
        } catch (FatalBeanException ex) {
            // expected
        }
    }

    public static class TestService {}

    public static class ExtendedTestService extends ServiceLocatorFactoryBeanTests.TestService {}

    public static class TestService2 {}

    public static interface TestServiceLocator {
        ServiceLocatorFactoryBeanTests.TestService getTestService();
    }

    public static interface TestServiceLocator2 {
        ServiceLocatorFactoryBeanTests.TestService getTestService(String id) throws ServiceLocatorFactoryBeanTests.CustomServiceLocatorException2;
    }

    public static interface TestServiceLocator3 {
        ServiceLocatorFactoryBeanTests.TestService getTestService();

        ServiceLocatorFactoryBeanTests.TestService getTestService(String id);

        ServiceLocatorFactoryBeanTests.TestService getTestService(int id);

        ServiceLocatorFactoryBeanTests.TestService someFactoryMethod();
    }

    public static interface TestService2Locator {
        ServiceLocatorFactoryBeanTests.TestService2 getTestService() throws ServiceLocatorFactoryBeanTests.CustomServiceLocatorException3;
    }

    public static interface ServiceLocatorInterfaceWithExtraNonCompliantMethod {
        ServiceLocatorFactoryBeanTests.TestService2 getTestService();

        ServiceLocatorFactoryBeanTests.TestService2 getTestService(String serviceName, String defaultNotAllowedParameter);
    }

    @SuppressWarnings("serial")
    public static class CustomServiceLocatorException1 extends NestedRuntimeException {
        public CustomServiceLocatorException1(String message, Throwable cause) {
            super(message, cause);
        }
    }

    @SuppressWarnings("serial")
    public static class CustomServiceLocatorException2 extends NestedCheckedException {
        public CustomServiceLocatorException2(Throwable cause) {
            super("", cause);
        }
    }

    @SuppressWarnings("serial")
    public static class CustomServiceLocatorException3 extends NestedCheckedException {
        public CustomServiceLocatorException3(String message) {
            super(message);
        }
    }

    @SuppressWarnings("serial")
    public static class ExceptionClassWithOnlyZeroArgCtor extends Exception {}
}

