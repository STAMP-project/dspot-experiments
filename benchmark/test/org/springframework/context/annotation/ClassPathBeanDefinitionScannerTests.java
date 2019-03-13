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


import AnnotationConfigUtils.AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME;
import AnnotationConfigUtils.COMMON_ANNOTATION_PROCESSOR_BEAN_NAME;
import AnnotationConfigUtils.CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME;
import AnnotationConfigUtils.EVENT_LISTENER_FACTORY_BEAN_NAME;
import AnnotationConfigUtils.EVENT_LISTENER_PROCESSOR_BEAN_NAME;
import RootBeanDefinition.SCOPE_PROTOTYPE;
import example.scannable.CustomComponent;
import example.scannable.FooService;
import example.scannable.FooServiceImpl;
import example.scannable.NamedStubDao;
import example.scannable.StubFooDao;
import org.aspectj.lang.annotation.Aspect;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation2.NamedStubDao2;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Component;


/**
 *
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @author Chris Beams
 */
public class ClassPathBeanDefinitionScannerTests {
    private static final String BASE_PACKAGE = "example.scannable";

    @Test
    public void testSimpleScanWithDefaultFiltersAndPostProcessors() {
        GenericApplicationContext context = new GenericApplicationContext();
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(context);
        int beanCount = scanner.scan(ClassPathBeanDefinitionScannerTests.BASE_PACKAGE);
        Assert.assertEquals(12, beanCount);
        Assert.assertTrue(context.containsBean("serviceInvocationCounter"));
        Assert.assertTrue(context.containsBean("fooServiceImpl"));
        Assert.assertTrue(context.containsBean("stubFooDao"));
        Assert.assertTrue(context.containsBean("myNamedComponent"));
        Assert.assertTrue(context.containsBean("myNamedDao"));
        Assert.assertTrue(context.containsBean("thoreau"));
        Assert.assertTrue(context.containsBean(CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME));
        Assert.assertTrue(context.containsBean(AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME));
        Assert.assertTrue(context.containsBean(COMMON_ANNOTATION_PROCESSOR_BEAN_NAME));
        Assert.assertTrue(context.containsBean(EVENT_LISTENER_PROCESSOR_BEAN_NAME));
        Assert.assertTrue(context.containsBean(EVENT_LISTENER_FACTORY_BEAN_NAME));
        context.refresh();
        FooServiceImpl fooService = context.getBean("fooServiceImpl", FooServiceImpl.class);
        Assert.assertTrue(context.getDefaultListableBeanFactory().containsSingleton("myNamedComponent"));
        Assert.assertEquals("bar", fooService.foo(123));
        Assert.assertEquals("bar", fooService.lookupFoo(123));
        Assert.assertTrue(context.isPrototype("thoreau"));
    }

    @Test
    public void testSimpleScanWithDefaultFiltersAndPrimaryLazyBean() {
        GenericApplicationContext context = new GenericApplicationContext();
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(context);
        scanner.scan(ClassPathBeanDefinitionScannerTests.BASE_PACKAGE);
        scanner.scan("org.springframework.context.annotation5");
        Assert.assertTrue(context.containsBean("serviceInvocationCounter"));
        Assert.assertTrue(context.containsBean("fooServiceImpl"));
        Assert.assertTrue(context.containsBean("stubFooDao"));
        Assert.assertTrue(context.containsBean("myNamedComponent"));
        Assert.assertTrue(context.containsBean("myNamedDao"));
        Assert.assertTrue(context.containsBean("otherFooDao"));
        context.refresh();
        Assert.assertFalse(context.getBeanFactory().containsSingleton("otherFooDao"));
        Assert.assertFalse(context.getBeanFactory().containsSingleton("fooServiceImpl"));
        FooServiceImpl fooService = context.getBean("fooServiceImpl", FooServiceImpl.class);
        Assert.assertTrue(context.getBeanFactory().containsSingleton("otherFooDao"));
        Assert.assertEquals("other", fooService.foo(123));
        Assert.assertEquals("other", fooService.lookupFoo(123));
    }

    @Test
    public void testDoubleScan() {
        GenericApplicationContext context = new GenericApplicationContext();
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(context);
        int beanCount = scanner.scan(ClassPathBeanDefinitionScannerTests.BASE_PACKAGE);
        Assert.assertEquals(12, beanCount);
        scanner.scan(ClassPathBeanDefinitionScannerTests.BASE_PACKAGE);
        Assert.assertTrue(context.containsBean("serviceInvocationCounter"));
        Assert.assertTrue(context.containsBean("fooServiceImpl"));
        Assert.assertTrue(context.containsBean("stubFooDao"));
        Assert.assertTrue(context.containsBean("myNamedComponent"));
        Assert.assertTrue(context.containsBean("myNamedDao"));
        Assert.assertTrue(context.containsBean("thoreau"));
    }

    @Test
    public void testSimpleScanWithDefaultFiltersAndNoPostProcessors() {
        GenericApplicationContext context = new GenericApplicationContext();
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(context);
        scanner.setIncludeAnnotationConfig(false);
        int beanCount = scanner.scan(ClassPathBeanDefinitionScannerTests.BASE_PACKAGE);
        Assert.assertEquals(7, beanCount);
        Assert.assertTrue(context.containsBean("serviceInvocationCounter"));
        Assert.assertTrue(context.containsBean("fooServiceImpl"));
        Assert.assertTrue(context.containsBean("stubFooDao"));
        Assert.assertTrue(context.containsBean("myNamedComponent"));
        Assert.assertTrue(context.containsBean("myNamedDao"));
    }

    @Test
    public void testSimpleScanWithDefaultFiltersAndOverridingBean() {
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBeanDefinition("stubFooDao", new RootBeanDefinition(TestBean.class));
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(context);
        scanner.setIncludeAnnotationConfig(false);
        // should not fail!
        scanner.scan(ClassPathBeanDefinitionScannerTests.BASE_PACKAGE);
    }

    @Test
    public void testSimpleScanWithDefaultFiltersAndDefaultBeanNameClash() {
        GenericApplicationContext context = new GenericApplicationContext();
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(context);
        scanner.setIncludeAnnotationConfig(false);
        try {
            scanner.scan("org.springframework.context.annotation3");
            scanner.scan(ClassPathBeanDefinitionScannerTests.BASE_PACKAGE);
            Assert.fail("Should have thrown IllegalStateException");
        } catch (IllegalStateException ex) {
            // expected
            Assert.assertTrue(ex.getMessage().contains("stubFooDao"));
            Assert.assertTrue(ex.getMessage().contains(StubFooDao.class.getName()));
        }
    }

    @Test
    public void testSimpleScanWithDefaultFiltersAndOverriddenEqualNamedBean() {
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBeanDefinition("myNamedDao", new RootBeanDefinition(NamedStubDao.class));
        int initialBeanCount = context.getBeanDefinitionCount();
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(context);
        scanner.setIncludeAnnotationConfig(false);
        int scannedBeanCount = scanner.scan(ClassPathBeanDefinitionScannerTests.BASE_PACKAGE);
        Assert.assertEquals(6, scannedBeanCount);
        Assert.assertEquals((initialBeanCount + scannedBeanCount), context.getBeanDefinitionCount());
        Assert.assertTrue(context.containsBean("serviceInvocationCounter"));
        Assert.assertTrue(context.containsBean("fooServiceImpl"));
        Assert.assertTrue(context.containsBean("stubFooDao"));
        Assert.assertTrue(context.containsBean("myNamedComponent"));
        Assert.assertTrue(context.containsBean("myNamedDao"));
    }

    @Test
    public void testSimpleScanWithDefaultFiltersAndOverriddenCompatibleNamedBean() {
        GenericApplicationContext context = new GenericApplicationContext();
        RootBeanDefinition bd = new RootBeanDefinition(NamedStubDao.class);
        bd.setScope(SCOPE_PROTOTYPE);
        context.registerBeanDefinition("myNamedDao", bd);
        int initialBeanCount = context.getBeanDefinitionCount();
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(context);
        scanner.setIncludeAnnotationConfig(false);
        int scannedBeanCount = scanner.scan(ClassPathBeanDefinitionScannerTests.BASE_PACKAGE);
        Assert.assertEquals(6, scannedBeanCount);
        Assert.assertEquals((initialBeanCount + scannedBeanCount), context.getBeanDefinitionCount());
        Assert.assertTrue(context.containsBean("serviceInvocationCounter"));
        Assert.assertTrue(context.containsBean("fooServiceImpl"));
        Assert.assertTrue(context.containsBean("stubFooDao"));
        Assert.assertTrue(context.containsBean("myNamedComponent"));
        Assert.assertTrue(context.containsBean("myNamedDao"));
    }

    @Test
    public void testSimpleScanWithDefaultFiltersAndSameBeanTwice() {
        GenericApplicationContext context = new GenericApplicationContext();
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(context);
        scanner.setIncludeAnnotationConfig(false);
        // should not fail!
        scanner.scan(ClassPathBeanDefinitionScannerTests.BASE_PACKAGE);
        scanner.scan(ClassPathBeanDefinitionScannerTests.BASE_PACKAGE);
    }

    @Test
    public void testSimpleScanWithDefaultFiltersAndSpecifiedBeanNameClash() {
        GenericApplicationContext context = new GenericApplicationContext();
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(context);
        scanner.setIncludeAnnotationConfig(false);
        try {
            scanner.scan("org.springframework.context.annotation2");
            scanner.scan(ClassPathBeanDefinitionScannerTests.BASE_PACKAGE);
            Assert.fail("Must have thrown IllegalStateException");
        } catch (IllegalStateException expected) {
            Assert.assertTrue(expected.getMessage().contains("myNamedDao"));
            Assert.assertTrue(expected.getMessage().contains(NamedStubDao.class.getName()));
            Assert.assertTrue(expected.getMessage().contains(NamedStubDao2.class.getName()));
        }
    }

    @Test
    public void testCustomIncludeFilterWithoutDefaultsButIncludingPostProcessors() {
        GenericApplicationContext context = new GenericApplicationContext();
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(context, false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(CustomComponent.class));
        int beanCount = scanner.scan(ClassPathBeanDefinitionScannerTests.BASE_PACKAGE);
        Assert.assertEquals(6, beanCount);
        Assert.assertTrue(context.containsBean("messageBean"));
        Assert.assertTrue(context.containsBean(AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME));
        Assert.assertTrue(context.containsBean(COMMON_ANNOTATION_PROCESSOR_BEAN_NAME));
        Assert.assertTrue(context.containsBean(EVENT_LISTENER_PROCESSOR_BEAN_NAME));
        Assert.assertTrue(context.containsBean(EVENT_LISTENER_FACTORY_BEAN_NAME));
    }

    @Test
    public void testCustomIncludeFilterWithoutDefaultsAndNoPostProcessors() {
        GenericApplicationContext context = new GenericApplicationContext();
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(context, false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(CustomComponent.class));
        int beanCount = scanner.scan(ClassPathBeanDefinitionScannerTests.BASE_PACKAGE);
        Assert.assertEquals(6, beanCount);
        Assert.assertTrue(context.containsBean("messageBean"));
        Assert.assertFalse(context.containsBean("serviceInvocationCounter"));
        Assert.assertFalse(context.containsBean("fooServiceImpl"));
        Assert.assertFalse(context.containsBean("stubFooDao"));
        Assert.assertFalse(context.containsBean("myNamedComponent"));
        Assert.assertFalse(context.containsBean("myNamedDao"));
        Assert.assertTrue(context.containsBean(AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME));
        Assert.assertTrue(context.containsBean(COMMON_ANNOTATION_PROCESSOR_BEAN_NAME));
        Assert.assertTrue(context.containsBean(EVENT_LISTENER_PROCESSOR_BEAN_NAME));
        Assert.assertTrue(context.containsBean(EVENT_LISTENER_FACTORY_BEAN_NAME));
    }

    @Test
    public void testCustomIncludeFilterAndDefaults() {
        GenericApplicationContext context = new GenericApplicationContext();
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(context, true);
        scanner.addIncludeFilter(new AnnotationTypeFilter(CustomComponent.class));
        int beanCount = scanner.scan(ClassPathBeanDefinitionScannerTests.BASE_PACKAGE);
        Assert.assertEquals(13, beanCount);
        Assert.assertTrue(context.containsBean("messageBean"));
        Assert.assertTrue(context.containsBean("serviceInvocationCounter"));
        Assert.assertTrue(context.containsBean("fooServiceImpl"));
        Assert.assertTrue(context.containsBean("stubFooDao"));
        Assert.assertTrue(context.containsBean("myNamedComponent"));
        Assert.assertTrue(context.containsBean("myNamedDao"));
        Assert.assertTrue(context.containsBean(AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME));
        Assert.assertTrue(context.containsBean(COMMON_ANNOTATION_PROCESSOR_BEAN_NAME));
        Assert.assertTrue(context.containsBean(EVENT_LISTENER_PROCESSOR_BEAN_NAME));
        Assert.assertTrue(context.containsBean(EVENT_LISTENER_FACTORY_BEAN_NAME));
    }

    @Test
    public void testCustomAnnotationExcludeFilterAndDefaults() {
        GenericApplicationContext context = new GenericApplicationContext();
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(context, true);
        scanner.addExcludeFilter(new AnnotationTypeFilter(Aspect.class));
        int beanCount = scanner.scan(ClassPathBeanDefinitionScannerTests.BASE_PACKAGE);
        Assert.assertEquals(11, beanCount);
        Assert.assertFalse(context.containsBean("serviceInvocationCounter"));
        Assert.assertTrue(context.containsBean("fooServiceImpl"));
        Assert.assertTrue(context.containsBean("stubFooDao"));
        Assert.assertTrue(context.containsBean("myNamedComponent"));
        Assert.assertTrue(context.containsBean("myNamedDao"));
        Assert.assertTrue(context.containsBean(AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME));
        Assert.assertTrue(context.containsBean(COMMON_ANNOTATION_PROCESSOR_BEAN_NAME));
        Assert.assertTrue(context.containsBean(EVENT_LISTENER_PROCESSOR_BEAN_NAME));
    }

    @Test
    public void testCustomAssignableTypeExcludeFilterAndDefaults() {
        GenericApplicationContext context = new GenericApplicationContext();
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(context, true);
        scanner.addExcludeFilter(new AssignableTypeFilter(FooService.class));
        int beanCount = scanner.scan(ClassPathBeanDefinitionScannerTests.BASE_PACKAGE);
        Assert.assertEquals(11, beanCount);
        Assert.assertFalse(context.containsBean("fooServiceImpl"));
        Assert.assertTrue(context.containsBean("serviceInvocationCounter"));
        Assert.assertTrue(context.containsBean("stubFooDao"));
        Assert.assertTrue(context.containsBean("myNamedComponent"));
        Assert.assertTrue(context.containsBean("myNamedDao"));
        Assert.assertTrue(context.containsBean(AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME));
        Assert.assertTrue(context.containsBean(COMMON_ANNOTATION_PROCESSOR_BEAN_NAME));
        Assert.assertTrue(context.containsBean(EVENT_LISTENER_PROCESSOR_BEAN_NAME));
        Assert.assertTrue(context.containsBean(EVENT_LISTENER_FACTORY_BEAN_NAME));
    }

    @Test
    public void testCustomAssignableTypeExcludeFilterAndDefaultsWithoutPostProcessors() {
        GenericApplicationContext context = new GenericApplicationContext();
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(context, true);
        scanner.setIncludeAnnotationConfig(false);
        scanner.addExcludeFilter(new AssignableTypeFilter(FooService.class));
        int beanCount = scanner.scan(ClassPathBeanDefinitionScannerTests.BASE_PACKAGE);
        Assert.assertEquals(6, beanCount);
        Assert.assertFalse(context.containsBean("fooServiceImpl"));
        Assert.assertTrue(context.containsBean("serviceInvocationCounter"));
        Assert.assertTrue(context.containsBean("stubFooDao"));
        Assert.assertTrue(context.containsBean("myNamedComponent"));
        Assert.assertTrue(context.containsBean("myNamedDao"));
        Assert.assertFalse(context.containsBean(AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME));
        Assert.assertFalse(context.containsBean(COMMON_ANNOTATION_PROCESSOR_BEAN_NAME));
    }

    @Test
    public void testMultipleCustomExcludeFiltersAndDefaults() {
        GenericApplicationContext context = new GenericApplicationContext();
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(context, true);
        scanner.addExcludeFilter(new AssignableTypeFilter(FooService.class));
        scanner.addExcludeFilter(new AnnotationTypeFilter(Aspect.class));
        int beanCount = scanner.scan(ClassPathBeanDefinitionScannerTests.BASE_PACKAGE);
        Assert.assertEquals(10, beanCount);
        Assert.assertFalse(context.containsBean("fooServiceImpl"));
        Assert.assertFalse(context.containsBean("serviceInvocationCounter"));
        Assert.assertTrue(context.containsBean("stubFooDao"));
        Assert.assertTrue(context.containsBean("myNamedComponent"));
        Assert.assertTrue(context.containsBean("myNamedDao"));
        Assert.assertTrue(context.containsBean(AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME));
        Assert.assertTrue(context.containsBean(COMMON_ANNOTATION_PROCESSOR_BEAN_NAME));
        Assert.assertTrue(context.containsBean(EVENT_LISTENER_PROCESSOR_BEAN_NAME));
        Assert.assertTrue(context.containsBean(EVENT_LISTENER_FACTORY_BEAN_NAME));
    }

    @Test
    public void testCustomBeanNameGenerator() {
        GenericApplicationContext context = new GenericApplicationContext();
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(context);
        scanner.setBeanNameGenerator(new ClassPathBeanDefinitionScannerTests.TestBeanNameGenerator());
        int beanCount = scanner.scan(ClassPathBeanDefinitionScannerTests.BASE_PACKAGE);
        Assert.assertEquals(12, beanCount);
        Assert.assertFalse(context.containsBean("fooServiceImpl"));
        Assert.assertTrue(context.containsBean("fooService"));
        Assert.assertTrue(context.containsBean("serviceInvocationCounter"));
        Assert.assertTrue(context.containsBean("stubFooDao"));
        Assert.assertTrue(context.containsBean("myNamedComponent"));
        Assert.assertTrue(context.containsBean("myNamedDao"));
        Assert.assertTrue(context.containsBean(AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME));
        Assert.assertTrue(context.containsBean(COMMON_ANNOTATION_PROCESSOR_BEAN_NAME));
        Assert.assertTrue(context.containsBean(EVENT_LISTENER_PROCESSOR_BEAN_NAME));
        Assert.assertTrue(context.containsBean(EVENT_LISTENER_FACTORY_BEAN_NAME));
    }

    @Test
    public void testMultipleBasePackagesWithDefaultsOnly() {
        GenericApplicationContext singlePackageContext = new GenericApplicationContext();
        ClassPathBeanDefinitionScanner singlePackageScanner = new ClassPathBeanDefinitionScanner(singlePackageContext);
        GenericApplicationContext multiPackageContext = new GenericApplicationContext();
        ClassPathBeanDefinitionScanner multiPackageScanner = new ClassPathBeanDefinitionScanner(multiPackageContext);
        int singlePackageBeanCount = singlePackageScanner.scan(ClassPathBeanDefinitionScannerTests.BASE_PACKAGE);
        Assert.assertEquals(12, singlePackageBeanCount);
        multiPackageScanner.scan(ClassPathBeanDefinitionScannerTests.BASE_PACKAGE, "org.springframework.dao.annotation");
        // assertTrue(multiPackageBeanCount > singlePackageBeanCount);
    }

    @Test
    public void testMultipleScanCalls() {
        GenericApplicationContext context = new GenericApplicationContext();
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(context);
        int initialBeanCount = context.getBeanDefinitionCount();
        int scannedBeanCount = scanner.scan(ClassPathBeanDefinitionScannerTests.BASE_PACKAGE);
        Assert.assertEquals(12, scannedBeanCount);
        Assert.assertEquals(scannedBeanCount, ((context.getBeanDefinitionCount()) - initialBeanCount));
        int addedBeanCount = scanner.scan("org.springframework.aop.aspectj.annotation");
        Assert.assertEquals(((initialBeanCount + scannedBeanCount) + addedBeanCount), context.getBeanDefinitionCount());
    }

    @Test
    public void testBeanAutowiredWithAnnotationConfigEnabled() {
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBeanDefinition("myBf", new RootBeanDefinition(StaticListableBeanFactory.class));
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(context);
        scanner.setBeanNameGenerator(new ClassPathBeanDefinitionScannerTests.TestBeanNameGenerator());
        int beanCount = scanner.scan(ClassPathBeanDefinitionScannerTests.BASE_PACKAGE);
        Assert.assertEquals(12, beanCount);
        context.refresh();
        FooServiceImpl fooService = context.getBean("fooService", FooServiceImpl.class);
        StaticListableBeanFactory myBf = ((StaticListableBeanFactory) (context.getBean("myBf")));
        MessageSource ms = ((MessageSource) (context.getBean("messageSource")));
        Assert.assertTrue(fooService.isInitCalled());
        Assert.assertEquals("bar", fooService.foo(123));
        Assert.assertEquals("bar", fooService.lookupFoo(123));
        Assert.assertSame(context.getDefaultListableBeanFactory(), fooService.beanFactory);
        Assert.assertEquals(2, fooService.listableBeanFactory.size());
        Assert.assertSame(context.getDefaultListableBeanFactory(), fooService.listableBeanFactory.get(0));
        Assert.assertSame(myBf, fooService.listableBeanFactory.get(1));
        Assert.assertSame(context, fooService.resourceLoader);
        Assert.assertSame(context, fooService.resourcePatternResolver);
        Assert.assertSame(context, fooService.eventPublisher);
        Assert.assertSame(ms, fooService.messageSource);
        Assert.assertSame(context, fooService.context);
        Assert.assertEquals(1, fooService.configurableContext.length);
        Assert.assertSame(context, fooService.configurableContext[0]);
        Assert.assertSame(context, fooService.genericContext);
    }

    @Test
    public void testBeanNotAutowiredWithAnnotationConfigDisabled() {
        GenericApplicationContext context = new GenericApplicationContext();
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(context);
        scanner.setIncludeAnnotationConfig(false);
        scanner.setBeanNameGenerator(new ClassPathBeanDefinitionScannerTests.TestBeanNameGenerator());
        int beanCount = scanner.scan(ClassPathBeanDefinitionScannerTests.BASE_PACKAGE);
        Assert.assertEquals(7, beanCount);
        context.refresh();
        try {
            context.getBean("fooService");
        } catch (BeanCreationException expected) {
            Assert.assertTrue(expected.contains(BeanInstantiationException.class));
            // @Lookup method not substituted
        }
    }

    @Test
    public void testAutowireCandidatePatternMatches() {
        GenericApplicationContext context = new GenericApplicationContext();
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(context);
        scanner.setIncludeAnnotationConfig(true);
        scanner.setBeanNameGenerator(new ClassPathBeanDefinitionScannerTests.TestBeanNameGenerator());
        scanner.setAutowireCandidatePatterns("*FooDao");
        scanner.scan(ClassPathBeanDefinitionScannerTests.BASE_PACKAGE);
        context.refresh();
        FooServiceImpl fooService = ((FooServiceImpl) (context.getBean("fooService")));
        Assert.assertEquals("bar", fooService.foo(123));
        Assert.assertEquals("bar", fooService.lookupFoo(123));
    }

    @Test
    public void testAutowireCandidatePatternDoesNotMatch() {
        GenericApplicationContext context = new GenericApplicationContext();
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(context);
        scanner.setIncludeAnnotationConfig(true);
        scanner.setBeanNameGenerator(new ClassPathBeanDefinitionScannerTests.TestBeanNameGenerator());
        scanner.setAutowireCandidatePatterns("*NoSuchDao");
        scanner.scan(ClassPathBeanDefinitionScannerTests.BASE_PACKAGE);
        try {
            context.refresh();
            context.getBean("fooService");
            Assert.fail("BeanCreationException expected; fooDao should not have been an autowire-candidate");
        } catch (BeanCreationException expected) {
            Assert.assertTrue(((expected.getMostSpecificCause()) instanceof NoSuchBeanDefinitionException));
        }
    }

    private static class TestBeanNameGenerator extends AnnotationBeanNameGenerator {
        @Override
        public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
            String beanName = super.generateBeanName(definition, registry);
            return beanName.replace("Impl", "");
        }
    }

    @Component("toBeIgnored")
    public class NonStaticInnerClass {}
}

