/**
 * Copyright 2002-2017 the original author or authors.
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


import BeanDefinition.SCOPE_PROTOTYPE;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJB;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.jndi.support.SimpleJndiBeanFactory;
import org.springframework.tests.mock.jndi.ExpectedLookupTemplate;
import org.springframework.tests.sample.beans.INestedTestBean;
import org.springframework.tests.sample.beans.ITestBean;
import org.springframework.tests.sample.beans.NestedTestBean;
import org.springframework.util.SerializationTestUtils;


/**
 *
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 */
public class CommonAnnotationBeanPostProcessorTests {
    @Test
    public void testPostConstructAndPreDestroy() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        bf.addBeanPostProcessor(new CommonAnnotationBeanPostProcessor());
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(CommonAnnotationBeanPostProcessorTests.AnnotatedInitDestroyBean.class));
        CommonAnnotationBeanPostProcessorTests.AnnotatedInitDestroyBean bean = ((CommonAnnotationBeanPostProcessorTests.AnnotatedInitDestroyBean) (bf.getBean("annotatedBean")));
        Assert.assertTrue(bean.initCalled);
        bf.destroySingletons();
        Assert.assertTrue(bean.destroyCalled);
    }

    @Test
    public void testPostConstructAndPreDestroyWithPostProcessor() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        bf.addBeanPostProcessor(new CommonAnnotationBeanPostProcessorTests.InitDestroyBeanPostProcessor());
        bf.addBeanPostProcessor(new CommonAnnotationBeanPostProcessor());
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(CommonAnnotationBeanPostProcessorTests.AnnotatedInitDestroyBean.class));
        CommonAnnotationBeanPostProcessorTests.AnnotatedInitDestroyBean bean = ((CommonAnnotationBeanPostProcessorTests.AnnotatedInitDestroyBean) (bf.getBean("annotatedBean")));
        Assert.assertTrue(bean.initCalled);
        bf.destroySingletons();
        Assert.assertTrue(bean.destroyCalled);
    }

    @Test
    public void testPostConstructAndPreDestroyWithApplicationContextAndPostProcessor() {
        GenericApplicationContext ctx = new GenericApplicationContext();
        ctx.registerBeanDefinition("bpp1", new RootBeanDefinition(CommonAnnotationBeanPostProcessorTests.InitDestroyBeanPostProcessor.class));
        ctx.registerBeanDefinition("bpp2", new RootBeanDefinition(CommonAnnotationBeanPostProcessor.class));
        ctx.registerBeanDefinition("annotatedBean", new RootBeanDefinition(CommonAnnotationBeanPostProcessorTests.AnnotatedInitDestroyBean.class));
        ctx.refresh();
        CommonAnnotationBeanPostProcessorTests.AnnotatedInitDestroyBean bean = ((CommonAnnotationBeanPostProcessorTests.AnnotatedInitDestroyBean) (ctx.getBean("annotatedBean")));
        Assert.assertTrue(bean.initCalled);
        ctx.close();
        Assert.assertTrue(bean.destroyCalled);
    }

    @Test
    public void testPostConstructAndPreDestroyWithManualConfiguration() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        InitDestroyAnnotationBeanPostProcessor bpp = new InitDestroyAnnotationBeanPostProcessor();
        bpp.setInitAnnotationType(PostConstruct.class);
        bpp.setDestroyAnnotationType(PreDestroy.class);
        bf.addBeanPostProcessor(bpp);
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(CommonAnnotationBeanPostProcessorTests.AnnotatedInitDestroyBean.class));
        CommonAnnotationBeanPostProcessorTests.AnnotatedInitDestroyBean bean = ((CommonAnnotationBeanPostProcessorTests.AnnotatedInitDestroyBean) (bf.getBean("annotatedBean")));
        Assert.assertTrue(bean.initCalled);
        bf.destroySingletons();
        Assert.assertTrue(bean.destroyCalled);
    }

    @Test
    public void testPostProcessorWithNullBean() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        bf.addBeanPostProcessor(new CommonAnnotationBeanPostProcessor());
        RootBeanDefinition rbd = new RootBeanDefinition(CommonAnnotationBeanPostProcessorTests.NullFactory.class);
        rbd.setFactoryMethodName("create");
        bf.registerBeanDefinition("bean", rbd);
        Assert.assertEquals("null", bf.getBean("bean").toString());
        bf.destroySingletons();
    }

    @Test
    public void testSerialization() throws Exception {
        CommonAnnotationBeanPostProcessor bpp = new CommonAnnotationBeanPostProcessor();
        CommonAnnotationBeanPostProcessor bpp2 = ((CommonAnnotationBeanPostProcessor) (SerializationTestUtils.serializeAndDeserialize(bpp)));
        CommonAnnotationBeanPostProcessorTests.AnnotatedInitDestroyBean bean = new CommonAnnotationBeanPostProcessorTests.AnnotatedInitDestroyBean();
        bpp2.postProcessBeforeDestruction(bean, "annotatedBean");
        Assert.assertTrue(bean.destroyCalled);
    }

    @Test
    public void testSerializationWithManualConfiguration() throws Exception {
        InitDestroyAnnotationBeanPostProcessor bpp = new InitDestroyAnnotationBeanPostProcessor();
        bpp.setInitAnnotationType(PostConstruct.class);
        bpp.setDestroyAnnotationType(PreDestroy.class);
        InitDestroyAnnotationBeanPostProcessor bpp2 = ((InitDestroyAnnotationBeanPostProcessor) (SerializationTestUtils.serializeAndDeserialize(bpp)));
        CommonAnnotationBeanPostProcessorTests.AnnotatedInitDestroyBean bean = new CommonAnnotationBeanPostProcessorTests.AnnotatedInitDestroyBean();
        bpp2.postProcessBeforeDestruction(bean, "annotatedBean");
        Assert.assertTrue(bean.destroyCalled);
    }

    @Test
    public void testResourceInjection() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        CommonAnnotationBeanPostProcessor bpp = new CommonAnnotationBeanPostProcessor();
        bpp.setResourceFactory(bf);
        bf.addBeanPostProcessor(bpp);
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(CommonAnnotationBeanPostProcessorTests.ResourceInjectionBean.class));
        TestBean tb = new TestBean();
        bf.registerSingleton("testBean", tb);
        TestBean tb2 = new TestBean();
        bf.registerSingleton("testBean2", tb2);
        CommonAnnotationBeanPostProcessorTests.ResourceInjectionBean bean = ((CommonAnnotationBeanPostProcessorTests.ResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertTrue(bean.initCalled);
        Assert.assertTrue(bean.init2Called);
        Assert.assertTrue(bean.init3Called);
        Assert.assertSame(tb, bean.getTestBean());
        Assert.assertSame(tb2, bean.getTestBean2());
        bf.destroySingletons();
        Assert.assertTrue(bean.destroyCalled);
        Assert.assertTrue(bean.destroy2Called);
        Assert.assertTrue(bean.destroy3Called);
    }

    @Test
    public void testResourceInjectionWithPrototypes() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        CommonAnnotationBeanPostProcessor bpp = new CommonAnnotationBeanPostProcessor();
        bpp.setResourceFactory(bf);
        bf.addBeanPostProcessor(bpp);
        RootBeanDefinition abd = new RootBeanDefinition(CommonAnnotationBeanPostProcessorTests.ResourceInjectionBean.class);
        abd.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", abd);
        RootBeanDefinition tbd1 = new RootBeanDefinition(TestBean.class);
        tbd1.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("testBean", tbd1);
        RootBeanDefinition tbd2 = new RootBeanDefinition(TestBean.class);
        tbd2.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("testBean2", tbd2);
        CommonAnnotationBeanPostProcessorTests.ResourceInjectionBean bean = ((CommonAnnotationBeanPostProcessorTests.ResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertTrue(bean.initCalled);
        Assert.assertTrue(bean.init2Called);
        Assert.assertTrue(bean.init3Called);
        TestBean tb = bean.getTestBean();
        TestBean tb2 = bean.getTestBean2();
        Assert.assertNotNull(tb);
        Assert.assertNotNull(tb2);
        CommonAnnotationBeanPostProcessorTests.ResourceInjectionBean anotherBean = ((CommonAnnotationBeanPostProcessorTests.ResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertNotSame(anotherBean, bean);
        Assert.assertNotSame(anotherBean.getTestBean(), tb);
        Assert.assertNotSame(anotherBean.getTestBean2(), tb2);
        bf.destroyBean("annotatedBean", bean);
        Assert.assertTrue(bean.destroyCalled);
        Assert.assertTrue(bean.destroy2Called);
        Assert.assertTrue(bean.destroy3Called);
    }

    @Test
    public void testResourceInjectionWithResolvableDependencyType() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        CommonAnnotationBeanPostProcessor bpp = new CommonAnnotationBeanPostProcessor();
        bpp.setBeanFactory(bf);
        bf.addBeanPostProcessor(bpp);
        RootBeanDefinition abd = new RootBeanDefinition(CommonAnnotationBeanPostProcessorTests.ExtendedResourceInjectionBean.class);
        abd.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", abd);
        RootBeanDefinition tbd = new RootBeanDefinition(TestBean.class);
        tbd.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("testBean4", tbd);
        bf.registerResolvableDependency(BeanFactory.class, bf);
        bf.registerResolvableDependency(INestedTestBean.class, new org.springframework.beans.factory.ObjectFactory<Object>() {
            @Override
            public Object getObject() throws BeansException {
                return new NestedTestBean();
            }
        });
        PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
        Properties props = new Properties();
        props.setProperty("tb", "testBean4");
        ppc.setProperties(props);
        ppc.postProcessBeanFactory(bf);
        CommonAnnotationBeanPostProcessorTests.ExtendedResourceInjectionBean bean = ((CommonAnnotationBeanPostProcessorTests.ExtendedResourceInjectionBean) (bf.getBean("annotatedBean")));
        INestedTestBean tb = bean.getTestBean6();
        Assert.assertNotNull(tb);
        CommonAnnotationBeanPostProcessorTests.ExtendedResourceInjectionBean anotherBean = ((CommonAnnotationBeanPostProcessorTests.ExtendedResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertNotSame(anotherBean, bean);
        Assert.assertNotSame(anotherBean.getTestBean6(), tb);
        String[] depBeans = bf.getDependenciesForBean("annotatedBean");
        Assert.assertEquals(1, depBeans.length);
        Assert.assertEquals("testBean4", depBeans[0]);
    }

    @Test
    public void testResourceInjectionWithDefaultMethod() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        CommonAnnotationBeanPostProcessor bpp = new CommonAnnotationBeanPostProcessor();
        bpp.setBeanFactory(bf);
        bf.addBeanPostProcessor(bpp);
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(CommonAnnotationBeanPostProcessorTests.DefaultMethodResourceInjectionBean.class));
        TestBean tb2 = new TestBean();
        bf.registerSingleton("testBean2", tb2);
        NestedTestBean tb7 = new NestedTestBean();
        bf.registerSingleton("testBean7", tb7);
        CommonAnnotationBeanPostProcessorTests.DefaultMethodResourceInjectionBean bean = ((CommonAnnotationBeanPostProcessorTests.DefaultMethodResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(tb2, bean.getTestBean2());
        Assert.assertSame(2, bean.counter);
        bf.destroySingletons();
        Assert.assertSame(3, bean.counter);
    }

    @Test
    public void testResourceInjectionWithTwoProcessors() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        CommonAnnotationBeanPostProcessor bpp = new CommonAnnotationBeanPostProcessor();
        bpp.setResourceFactory(bf);
        bf.addBeanPostProcessor(bpp);
        CommonAnnotationBeanPostProcessor bpp2 = new CommonAnnotationBeanPostProcessor();
        bpp2.setResourceFactory(bf);
        bf.addBeanPostProcessor(bpp2);
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(CommonAnnotationBeanPostProcessorTests.ResourceInjectionBean.class));
        TestBean tb = new TestBean();
        bf.registerSingleton("testBean", tb);
        TestBean tb2 = new TestBean();
        bf.registerSingleton("testBean2", tb2);
        CommonAnnotationBeanPostProcessorTests.ResourceInjectionBean bean = ((CommonAnnotationBeanPostProcessorTests.ResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertTrue(bean.initCalled);
        Assert.assertTrue(bean.init2Called);
        Assert.assertSame(tb, bean.getTestBean());
        Assert.assertSame(tb2, bean.getTestBean2());
        bf.destroySingletons();
        Assert.assertTrue(bean.destroyCalled);
        Assert.assertTrue(bean.destroy2Called);
    }

    @Test
    public void testResourceInjectionFromJndi() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        CommonAnnotationBeanPostProcessor bpp = new CommonAnnotationBeanPostProcessor();
        SimpleJndiBeanFactory resourceFactory = new SimpleJndiBeanFactory();
        ExpectedLookupTemplate jndiTemplate = new ExpectedLookupTemplate();
        TestBean tb = new TestBean();
        jndiTemplate.addObject("java:comp/env/testBean", tb);
        TestBean tb2 = new TestBean();
        jndiTemplate.addObject("java:comp/env/testBean2", tb2);
        resourceFactory.setJndiTemplate(jndiTemplate);
        bpp.setResourceFactory(resourceFactory);
        bf.addBeanPostProcessor(bpp);
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(CommonAnnotationBeanPostProcessorTests.ResourceInjectionBean.class));
        CommonAnnotationBeanPostProcessorTests.ResourceInjectionBean bean = ((CommonAnnotationBeanPostProcessorTests.ResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertTrue(bean.initCalled);
        Assert.assertTrue(bean.init2Called);
        Assert.assertSame(tb, bean.getTestBean());
        Assert.assertSame(tb2, bean.getTestBean2());
        bf.destroySingletons();
        Assert.assertTrue(bean.destroyCalled);
        Assert.assertTrue(bean.destroy2Called);
    }

    @Test
    public void testExtendedResourceInjection() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        CommonAnnotationBeanPostProcessor bpp = new CommonAnnotationBeanPostProcessor();
        bpp.setBeanFactory(bf);
        bf.addBeanPostProcessor(bpp);
        bf.registerResolvableDependency(BeanFactory.class, bf);
        PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
        Properties props = new Properties();
        props.setProperty("tb", "testBean3");
        ppc.setProperties(props);
        ppc.postProcessBeanFactory(bf);
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(CommonAnnotationBeanPostProcessorTests.ExtendedResourceInjectionBean.class));
        bf.registerBeanDefinition("annotatedBean2", new RootBeanDefinition(CommonAnnotationBeanPostProcessorTests.NamedResourceInjectionBean.class));
        bf.registerBeanDefinition("annotatedBean3", new RootBeanDefinition(CommonAnnotationBeanPostProcessorTests.ConvertedResourceInjectionBean.class));
        TestBean tb = new TestBean();
        bf.registerSingleton("testBean", tb);
        TestBean tb2 = new TestBean();
        bf.registerSingleton("testBean2", tb2);
        TestBean tb3 = new TestBean();
        bf.registerSingleton("testBean3", tb3);
        TestBean tb4 = new TestBean();
        bf.registerSingleton("testBean4", tb4);
        NestedTestBean tb6 = new NestedTestBean();
        bf.registerSingleton("value", "5");
        bf.registerSingleton("xy", tb6);
        bf.registerAlias("xy", "testBean9");
        CommonAnnotationBeanPostProcessorTests.ExtendedResourceInjectionBean bean = ((CommonAnnotationBeanPostProcessorTests.ExtendedResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertTrue(bean.initCalled);
        Assert.assertTrue(bean.init2Called);
        Assert.assertSame(tb, bean.getTestBean());
        Assert.assertSame(tb2, bean.getTestBean2());
        Assert.assertSame(tb4, bean.getTestBean3());
        Assert.assertSame(tb3, bean.getTestBean4());
        Assert.assertSame(tb6, bean.testBean5);
        Assert.assertSame(tb6, bean.testBean6);
        Assert.assertSame(bf, bean.beanFactory);
        CommonAnnotationBeanPostProcessorTests.NamedResourceInjectionBean bean2 = ((CommonAnnotationBeanPostProcessorTests.NamedResourceInjectionBean) (bf.getBean("annotatedBean2")));
        Assert.assertSame(tb6, bean2.testBean);
        CommonAnnotationBeanPostProcessorTests.ConvertedResourceInjectionBean bean3 = ((CommonAnnotationBeanPostProcessorTests.ConvertedResourceInjectionBean) (bf.getBean("annotatedBean3")));
        Assert.assertSame(5, bean3.value);
        bf.destroySingletons();
        Assert.assertTrue(bean.destroyCalled);
        Assert.assertTrue(bean.destroy2Called);
    }

    @Test
    public void testExtendedResourceInjectionWithOverriding() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        CommonAnnotationBeanPostProcessor bpp = new CommonAnnotationBeanPostProcessor();
        bpp.setBeanFactory(bf);
        bf.addBeanPostProcessor(bpp);
        bf.registerResolvableDependency(BeanFactory.class, bf);
        PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
        Properties props = new Properties();
        props.setProperty("tb", "testBean3");
        ppc.setProperties(props);
        ppc.postProcessBeanFactory(bf);
        RootBeanDefinition annotatedBd = new RootBeanDefinition(CommonAnnotationBeanPostProcessorTests.ExtendedResourceInjectionBean.class);
        TestBean tb5 = new TestBean();
        annotatedBd.getPropertyValues().add("testBean2", tb5);
        bf.registerBeanDefinition("annotatedBean", annotatedBd);
        bf.registerBeanDefinition("annotatedBean2", new RootBeanDefinition(CommonAnnotationBeanPostProcessorTests.NamedResourceInjectionBean.class));
        TestBean tb = new TestBean();
        bf.registerSingleton("testBean", tb);
        TestBean tb2 = new TestBean();
        bf.registerSingleton("testBean2", tb2);
        TestBean tb3 = new TestBean();
        bf.registerSingleton("testBean3", tb3);
        TestBean tb4 = new TestBean();
        bf.registerSingleton("testBean4", tb4);
        NestedTestBean tb6 = new NestedTestBean();
        bf.registerSingleton("xy", tb6);
        CommonAnnotationBeanPostProcessorTests.ExtendedResourceInjectionBean bean = ((CommonAnnotationBeanPostProcessorTests.ExtendedResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertTrue(bean.initCalled);
        Assert.assertTrue(bean.init2Called);
        Assert.assertSame(tb, bean.getTestBean());
        Assert.assertSame(tb5, bean.getTestBean2());
        Assert.assertSame(tb4, bean.getTestBean3());
        Assert.assertSame(tb3, bean.getTestBean4());
        Assert.assertSame(tb6, bean.testBean5);
        Assert.assertSame(tb6, bean.testBean6);
        Assert.assertSame(bf, bean.beanFactory);
        try {
            bf.getBean("annotatedBean2");
        } catch (BeanCreationException ex) {
            Assert.assertTrue(((ex.getRootCause()) instanceof NoSuchBeanDefinitionException));
            NoSuchBeanDefinitionException innerEx = ((NoSuchBeanDefinitionException) (ex.getRootCause()));
            Assert.assertEquals("testBean9", innerEx.getBeanName());
        }
        bf.destroySingletons();
        Assert.assertTrue(bean.destroyCalled);
        Assert.assertTrue(bean.destroy2Called);
    }

    @Test
    public void testExtendedEjbInjection() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        CommonAnnotationBeanPostProcessor bpp = new CommonAnnotationBeanPostProcessor();
        bpp.setBeanFactory(bf);
        bf.addBeanPostProcessor(bpp);
        bf.registerResolvableDependency(BeanFactory.class, bf);
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(CommonAnnotationBeanPostProcessorTests.ExtendedEjbInjectionBean.class));
        TestBean tb = new TestBean();
        bf.registerSingleton("testBean", tb);
        TestBean tb2 = new TestBean();
        bf.registerSingleton("testBean2", tb2);
        TestBean tb3 = new TestBean();
        bf.registerSingleton("testBean3", tb3);
        TestBean tb4 = new TestBean();
        bf.registerSingleton("testBean4", tb4);
        NestedTestBean tb6 = new NestedTestBean();
        bf.registerSingleton("xy", tb6);
        bf.registerAlias("xy", "testBean9");
        CommonAnnotationBeanPostProcessorTests.ExtendedEjbInjectionBean bean = ((CommonAnnotationBeanPostProcessorTests.ExtendedEjbInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertTrue(bean.initCalled);
        Assert.assertTrue(bean.init2Called);
        Assert.assertSame(tb, bean.getTestBean());
        Assert.assertSame(tb2, bean.getTestBean2());
        Assert.assertSame(tb4, bean.getTestBean3());
        Assert.assertSame(tb3, bean.getTestBean4());
        Assert.assertSame(tb6, bean.testBean5);
        Assert.assertSame(tb6, bean.testBean6);
        Assert.assertSame(bf, bean.beanFactory);
        bf.destroySingletons();
        Assert.assertTrue(bean.destroyCalled);
        Assert.assertTrue(bean.destroy2Called);
    }

    @Test
    public void testLazyResolutionWithResourceField() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        CommonAnnotationBeanPostProcessor bpp = new CommonAnnotationBeanPostProcessor();
        bpp.setBeanFactory(bf);
        bf.addBeanPostProcessor(bpp);
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(CommonAnnotationBeanPostProcessorTests.LazyResourceFieldInjectionBean.class));
        bf.registerBeanDefinition("testBean", new RootBeanDefinition(TestBean.class));
        CommonAnnotationBeanPostProcessorTests.LazyResourceFieldInjectionBean bean = ((CommonAnnotationBeanPostProcessorTests.LazyResourceFieldInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertFalse(bf.containsSingleton("testBean"));
        bean.testBean.setName("notLazyAnymore");
        Assert.assertTrue(bf.containsSingleton("testBean"));
        TestBean tb = ((TestBean) (bf.getBean("testBean")));
        Assert.assertEquals("notLazyAnymore", getName());
    }

    @Test
    public void testLazyResolutionWithResourceMethod() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        CommonAnnotationBeanPostProcessor bpp = new CommonAnnotationBeanPostProcessor();
        bpp.setBeanFactory(bf);
        bf.addBeanPostProcessor(bpp);
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(CommonAnnotationBeanPostProcessorTests.LazyResourceMethodInjectionBean.class));
        bf.registerBeanDefinition("testBean", new RootBeanDefinition(TestBean.class));
        CommonAnnotationBeanPostProcessorTests.LazyResourceMethodInjectionBean bean = ((CommonAnnotationBeanPostProcessorTests.LazyResourceMethodInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertFalse(bf.containsSingleton("testBean"));
        bean.testBean.setName("notLazyAnymore");
        Assert.assertTrue(bf.containsSingleton("testBean"));
        TestBean tb = ((TestBean) (bf.getBean("testBean")));
        Assert.assertEquals("notLazyAnymore", getName());
    }

    @Test
    public void testLazyResolutionWithCglibProxy() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        CommonAnnotationBeanPostProcessor bpp = new CommonAnnotationBeanPostProcessor();
        bpp.setBeanFactory(bf);
        bf.addBeanPostProcessor(bpp);
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(CommonAnnotationBeanPostProcessorTests.LazyResourceCglibInjectionBean.class));
        bf.registerBeanDefinition("testBean", new RootBeanDefinition(TestBean.class));
        CommonAnnotationBeanPostProcessorTests.LazyResourceCglibInjectionBean bean = ((CommonAnnotationBeanPostProcessorTests.LazyResourceCglibInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertFalse(bf.containsSingleton("testBean"));
        setName("notLazyAnymore");
        Assert.assertTrue(bf.containsSingleton("testBean"));
        TestBean tb = ((TestBean) (bf.getBean("testBean")));
        Assert.assertEquals("notLazyAnymore", getName());
    }

    public static class AnnotatedInitDestroyBean {
        public boolean initCalled = false;

        public boolean destroyCalled = false;

        @PostConstruct
        private void init() {
            if (this.initCalled) {
                throw new IllegalStateException("Already called");
            }
            this.initCalled = true;
        }

        @PreDestroy
        private void destroy() {
            if (this.destroyCalled) {
                throw new IllegalStateException("Already called");
            }
            this.destroyCalled = true;
        }
    }

    public static class InitDestroyBeanPostProcessor implements DestructionAwareBeanPostProcessor {
        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            if (bean instanceof CommonAnnotationBeanPostProcessorTests.AnnotatedInitDestroyBean) {
                Assert.assertFalse(((CommonAnnotationBeanPostProcessorTests.AnnotatedInitDestroyBean) (bean)).initCalled);
            }
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            if (bean instanceof CommonAnnotationBeanPostProcessorTests.AnnotatedInitDestroyBean) {
                Assert.assertTrue(((CommonAnnotationBeanPostProcessorTests.AnnotatedInitDestroyBean) (bean)).initCalled);
            }
            return bean;
        }

        @Override
        public void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException {
            if (bean instanceof CommonAnnotationBeanPostProcessorTests.AnnotatedInitDestroyBean) {
                Assert.assertFalse(((CommonAnnotationBeanPostProcessorTests.AnnotatedInitDestroyBean) (bean)).destroyCalled);
            }
        }

        @Override
        public boolean requiresDestruction(Object bean) {
            return true;
        }
    }

    public static class ResourceInjectionBean extends CommonAnnotationBeanPostProcessorTests.AnnotatedInitDestroyBean {
        public boolean init2Called = false;

        public boolean init3Called = false;

        public boolean destroy2Called = false;

        public boolean destroy3Called = false;

        @Resource
        private TestBean testBean;

        private TestBean testBean2;

        @PostConstruct
        protected void init2() {
            if (((this.testBean) == null) || ((this.testBean2) == null)) {
                throw new IllegalStateException("Resources not injected");
            }
            if (!(this.initCalled)) {
                throw new IllegalStateException("Superclass init method not called yet");
            }
            if (this.init2Called) {
                throw new IllegalStateException("Already called");
            }
            this.init2Called = true;
        }

        @PostConstruct
        private void init() {
            if (this.init3Called) {
                throw new IllegalStateException("Already called");
            }
            this.init3Called = true;
        }

        @PreDestroy
        protected void destroy2() {
            if (this.destroyCalled) {
                throw new IllegalStateException("Superclass destroy called too soon");
            }
            if (this.destroy2Called) {
                throw new IllegalStateException("Already called");
            }
            this.destroy2Called = true;
        }

        @PreDestroy
        private void destroy() {
            if (this.destroyCalled) {
                throw new IllegalStateException("Superclass destroy called too soon");
            }
            if (this.destroy3Called) {
                throw new IllegalStateException("Already called");
            }
            this.destroy3Called = true;
        }

        @Resource
        public void setTestBean2(TestBean testBean2) {
            if ((this.testBean2) != null) {
                throw new IllegalStateException("Already called");
            }
            this.testBean2 = testBean2;
        }

        public TestBean getTestBean() {
            return testBean;
        }

        public TestBean getTestBean2() {
            return testBean2;
        }
    }

    static class NonPublicResourceInjectionBean<B> extends CommonAnnotationBeanPostProcessorTests.ResourceInjectionBean {
        @Resource(name = "testBean4", type = TestBean.class)
        protected ITestBean testBean3;

        private B testBean4;

        @Resource
        INestedTestBean testBean5;

        INestedTestBean testBean6;

        @Resource
        BeanFactory beanFactory;

        @Override
        @Resource
        public void setTestBean2(TestBean testBean2) {
            super.setTestBean2(testBean2);
        }

        @Resource(name = "${tb}", type = ITestBean.class)
        private void setTestBean4(B testBean4) {
            if ((this.testBean4) != null) {
                throw new IllegalStateException("Already called");
            }
            this.testBean4 = testBean4;
        }

        @Resource
        public void setTestBean6(INestedTestBean testBean6) {
            if ((this.testBean6) != null) {
                throw new IllegalStateException("Already called");
            }
            this.testBean6 = testBean6;
        }

        public ITestBean getTestBean3() {
            return testBean3;
        }

        public B getTestBean4() {
            return testBean4;
        }

        public INestedTestBean getTestBean5() {
            return testBean5;
        }

        public INestedTestBean getTestBean6() {
            return testBean6;
        }

        @Override
        @PostConstruct
        protected void init2() {
            if (((this.testBean3) == null) || ((this.testBean4) == null)) {
                throw new IllegalStateException("Resources not injected");
            }
            super.init2();
        }

        @Override
        @PreDestroy
        protected void destroy2() {
            super.destroy2();
        }
    }

    public static class ExtendedResourceInjectionBean extends CommonAnnotationBeanPostProcessorTests.NonPublicResourceInjectionBean<ITestBean> {}

    public interface InterfaceWithDefaultMethod {
        @Resource
        void setTestBean2(TestBean testBean2);

        @Resource
        default void setTestBean7(INestedTestBean testBean7) {
            increaseCounter();
        }

        @PostConstruct
        default void initDefault() {
            increaseCounter();
        }

        @PreDestroy
        default void destroyDefault() {
            increaseCounter();
        }

        void increaseCounter();
    }

    public static class DefaultMethodResourceInjectionBean extends CommonAnnotationBeanPostProcessorTests.ResourceInjectionBean implements CommonAnnotationBeanPostProcessorTests.InterfaceWithDefaultMethod {
        public int counter = 0;

        @Override
        public void increaseCounter() {
            (counter)++;
        }
    }

    public static class ExtendedEjbInjectionBean extends CommonAnnotationBeanPostProcessorTests.ResourceInjectionBean {
        @EJB(name = "testBean4", beanInterface = TestBean.class)
        protected ITestBean testBean3;

        private ITestBean testBean4;

        @EJB
        private INestedTestBean testBean5;

        private INestedTestBean testBean6;

        @Resource
        private BeanFactory beanFactory;

        @Override
        @EJB
        public void setTestBean2(TestBean testBean2) {
            super.setTestBean2(testBean2);
        }

        @EJB(beanName = "testBean3", beanInterface = ITestBean.class)
        private void setTestBean4(ITestBean testBean4) {
            if ((this.testBean4) != null) {
                throw new IllegalStateException("Already called");
            }
            this.testBean4 = testBean4;
        }

        @EJB
        public void setTestBean6(INestedTestBean testBean6) {
            if ((this.testBean6) != null) {
                throw new IllegalStateException("Already called");
            }
            this.testBean6 = testBean6;
        }

        public ITestBean getTestBean3() {
            return testBean3;
        }

        public ITestBean getTestBean4() {
            return testBean4;
        }

        @Override
        @PostConstruct
        protected void init2() {
            if (((this.testBean3) == null) || ((this.testBean4) == null)) {
                throw new IllegalStateException("Resources not injected");
            }
            super.init2();
        }

        @Override
        @PreDestroy
        protected void destroy2() {
            super.destroy2();
        }
    }

    private static class NamedResourceInjectionBean {
        @Resource(name = "testBean9")
        private INestedTestBean testBean;
    }

    private static class ConvertedResourceInjectionBean {
        @Resource(name = "value")
        private int value;
    }

    private static class LazyResourceFieldInjectionBean {
        @Resource
        @Lazy
        private ITestBean testBean;
    }

    private static class LazyResourceMethodInjectionBean {
        private ITestBean testBean;

        @Resource
        @Lazy
        public void setTestBean(ITestBean testBean) {
            this.testBean = testBean;
        }
    }

    private static class LazyResourceCglibInjectionBean {
        private TestBean testBean;

        @Resource
        @Lazy
        public void setTestBean(TestBean testBean) {
            this.testBean = testBean;
        }
    }

    @SuppressWarnings("unused")
    private static class NullFactory {
        public static Object create() {
            return null;
        }
    }
}

