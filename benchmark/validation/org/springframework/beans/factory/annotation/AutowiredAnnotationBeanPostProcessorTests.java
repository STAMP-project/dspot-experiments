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
package org.springframework.beans.factory.annotation;


import RootBeanDefinition.SCOPE_PROTOTYPE;
import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.Ordered;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.Order;
import org.springframework.tests.sample.beans.ITestBean;
import org.springframework.tests.sample.beans.IndexedTestBean;
import org.springframework.tests.sample.beans.NestedTestBean;
import org.springframework.tests.sample.beans.TestBean;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.SerializationTestUtils;


/**
 *
 *
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @author Sam Brannen
 * @author Chris Beams
 * @author Stephane Nicoll
 */
public class AutowiredAnnotationBeanPostProcessorTests {
    private DefaultListableBeanFactory bf;

    private AutowiredAnnotationBeanPostProcessor bpp;

    @Test
    public void testIncompleteBeanDefinition() {
        bf.registerBeanDefinition("testBean", new GenericBeanDefinition());
        try {
            bf.getBean("testBean");
            Assert.fail("Should have thrown BeanCreationException");
        } catch (BeanCreationException ex) {
            Assert.assertTrue(((ex.getRootCause()) instanceof IllegalStateException));
        }
    }

    @Test
    public void testResourceInjection() {
        RootBeanDefinition bd = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.ResourceInjectionBean.class);
        bd.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", bd);
        TestBean tb = new TestBean();
        bf.registerSingleton("testBean", tb);
        AutowiredAnnotationBeanPostProcessorTests.ResourceInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.ResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(tb, bean.getTestBean());
        Assert.assertSame(tb, bean.getTestBean2());
        bean = ((AutowiredAnnotationBeanPostProcessorTests.ResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(tb, bean.getTestBean());
        Assert.assertSame(tb, bean.getTestBean2());
    }

    @Test
    public void testExtendedResourceInjection() {
        RootBeanDefinition bd = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.TypedExtendedResourceInjectionBean.class);
        bd.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", bd);
        TestBean tb = new TestBean();
        bf.registerSingleton("testBean", tb);
        NestedTestBean ntb = new NestedTestBean();
        bf.registerSingleton("nestedTestBean", ntb);
        AutowiredAnnotationBeanPostProcessorTests.TypedExtendedResourceInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.TypedExtendedResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(tb, bean.getTestBean());
        Assert.assertSame(tb, bean.getTestBean2());
        Assert.assertSame(tb, bean.getTestBean3());
        Assert.assertSame(tb, bean.getTestBean4());
        Assert.assertSame(ntb, bean.getNestedTestBean());
        Assert.assertSame(bf, bean.getBeanFactory());
        bean = ((AutowiredAnnotationBeanPostProcessorTests.TypedExtendedResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(tb, bean.getTestBean());
        Assert.assertSame(tb, bean.getTestBean2());
        Assert.assertSame(tb, bean.getTestBean3());
        Assert.assertSame(tb, bean.getTestBean4());
        Assert.assertSame(ntb, bean.getNestedTestBean());
        Assert.assertSame(bf, bean.getBeanFactory());
        String[] depBeans = bf.getDependenciesForBean("annotatedBean");
        Assert.assertEquals(2, depBeans.length);
        Assert.assertEquals("testBean", depBeans[0]);
        Assert.assertEquals("nestedTestBean", depBeans[1]);
    }

    @Test
    public void testExtendedResourceInjectionWithDestruction() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.TypedExtendedResourceInjectionBean.class));
        bf.registerBeanDefinition("testBean", new RootBeanDefinition(TestBean.class));
        NestedTestBean ntb = new NestedTestBean();
        bf.registerSingleton("nestedTestBean", ntb);
        TestBean tb = bf.getBean("testBean", TestBean.class);
        AutowiredAnnotationBeanPostProcessorTests.TypedExtendedResourceInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.TypedExtendedResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(tb, bean.getTestBean());
        Assert.assertSame(tb, bean.getTestBean2());
        Assert.assertSame(tb, bean.getTestBean3());
        Assert.assertSame(tb, bean.getTestBean4());
        Assert.assertSame(ntb, bean.getNestedTestBean());
        Assert.assertSame(bf, bean.getBeanFactory());
        Assert.assertArrayEquals(new String[]{ "testBean", "nestedTestBean" }, bf.getDependenciesForBean("annotatedBean"));
        bf.destroySingleton("testBean");
        Assert.assertFalse(bf.containsSingleton("testBean"));
        Assert.assertFalse(bf.containsSingleton("annotatedBean"));
        Assert.assertTrue(bean.destroyed);
        Assert.assertSame(0, bf.getDependenciesForBean("annotatedBean").length);
    }

    @Test
    public void testExtendedResourceInjectionWithOverriding() {
        RootBeanDefinition annotatedBd = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.TypedExtendedResourceInjectionBean.class);
        TestBean tb2 = new TestBean();
        annotatedBd.getPropertyValues().add("testBean2", tb2);
        bf.registerBeanDefinition("annotatedBean", annotatedBd);
        TestBean tb = new TestBean();
        bf.registerSingleton("testBean", tb);
        NestedTestBean ntb = new NestedTestBean();
        bf.registerSingleton("nestedTestBean", ntb);
        AutowiredAnnotationBeanPostProcessorTests.TypedExtendedResourceInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.TypedExtendedResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(tb, bean.getTestBean());
        Assert.assertSame(tb2, bean.getTestBean2());
        Assert.assertSame(tb, bean.getTestBean3());
        Assert.assertSame(tb, bean.getTestBean4());
        Assert.assertSame(ntb, bean.getNestedTestBean());
        Assert.assertSame(bf, bean.getBeanFactory());
    }

    @Test
    public void testExtendedResourceInjectionWithSkippedOverriddenMethods() {
        RootBeanDefinition annotatedBd = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.OverriddenExtendedResourceInjectionBean.class);
        bf.registerBeanDefinition("annotatedBean", annotatedBd);
        TestBean tb = new TestBean();
        bf.registerSingleton("testBean", tb);
        NestedTestBean ntb = new NestedTestBean();
        bf.registerSingleton("nestedTestBean", ntb);
        AutowiredAnnotationBeanPostProcessorTests.OverriddenExtendedResourceInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.OverriddenExtendedResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(tb, bean.getTestBean());
        Assert.assertNull(bean.getTestBean2());
        Assert.assertSame(tb, bean.getTestBean3());
        Assert.assertSame(tb, bean.getTestBean4());
        Assert.assertSame(ntb, bean.getNestedTestBean());
        Assert.assertNull(bean.getBeanFactory());
        Assert.assertTrue(bean.baseInjected);
        Assert.assertTrue(bean.subInjected);
    }

    @Test
    public void testExtendedResourceInjectionWithDefaultMethod() {
        RootBeanDefinition annotatedBd = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.DefaultMethodResourceInjectionBean.class);
        bf.registerBeanDefinition("annotatedBean", annotatedBd);
        TestBean tb = new TestBean();
        bf.registerSingleton("testBean", tb);
        NestedTestBean ntb = new NestedTestBean();
        bf.registerSingleton("nestedTestBean", ntb);
        AutowiredAnnotationBeanPostProcessorTests.DefaultMethodResourceInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.DefaultMethodResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(tb, bean.getTestBean());
        Assert.assertNull(bean.getTestBean2());
        Assert.assertSame(tb, bean.getTestBean3());
        Assert.assertSame(tb, bean.getTestBean4());
        Assert.assertSame(ntb, bean.getNestedTestBean());
        Assert.assertNull(bean.getBeanFactory());
        Assert.assertTrue(bean.baseInjected);
        Assert.assertTrue(bean.subInjected);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testExtendedResourceInjectionWithAtRequired() {
        bf.addBeanPostProcessor(new RequiredAnnotationBeanPostProcessor());
        RootBeanDefinition bd = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.TypedExtendedResourceInjectionBean.class);
        bd.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", bd);
        TestBean tb = new TestBean();
        bf.registerSingleton("testBean", tb);
        NestedTestBean ntb = new NestedTestBean();
        bf.registerSingleton("nestedTestBean", ntb);
        AutowiredAnnotationBeanPostProcessorTests.TypedExtendedResourceInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.TypedExtendedResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(tb, bean.getTestBean());
        Assert.assertSame(tb, bean.getTestBean2());
        Assert.assertSame(tb, bean.getTestBean3());
        Assert.assertSame(tb, bean.getTestBean4());
        Assert.assertSame(ntb, bean.getNestedTestBean());
        Assert.assertSame(bf, bean.getBeanFactory());
    }

    @Test
    public void testOptionalResourceInjection() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.OptionalResourceInjectionBean.class));
        TestBean tb = new TestBean();
        bf.registerSingleton("testBean", tb);
        IndexedTestBean itb = new IndexedTestBean();
        bf.registerSingleton("indexedTestBean", itb);
        NestedTestBean ntb1 = new NestedTestBean();
        bf.registerSingleton("nestedTestBean1", ntb1);
        NestedTestBean ntb2 = new NestedTestBean();
        bf.registerSingleton("nestedTestBean2", ntb2);
        AutowiredAnnotationBeanPostProcessorTests.OptionalResourceInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.OptionalResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(tb, bean.getTestBean());
        Assert.assertSame(tb, bean.getTestBean2());
        Assert.assertSame(tb, bean.getTestBean3());
        Assert.assertSame(tb, bean.getTestBean4());
        Assert.assertSame(itb, bean.getIndexedTestBean());
        Assert.assertEquals(2, bean.getNestedTestBeans().length);
        Assert.assertSame(ntb1, bean.getNestedTestBeans()[0]);
        Assert.assertSame(ntb2, bean.getNestedTestBeans()[1]);
        Assert.assertEquals(2, bean.nestedTestBeansField.length);
        Assert.assertSame(ntb1, bean.nestedTestBeansField[0]);
        Assert.assertSame(ntb2, bean.nestedTestBeansField[1]);
    }

    @Test
    public void testOptionalCollectionResourceInjection() {
        RootBeanDefinition rbd = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.OptionalCollectionResourceInjectionBean.class);
        rbd.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", rbd);
        TestBean tb = new TestBean();
        bf.registerSingleton("testBean", tb);
        IndexedTestBean itb = new IndexedTestBean();
        bf.registerSingleton("indexedTestBean", itb);
        NestedTestBean ntb1 = new NestedTestBean();
        bf.registerSingleton("nestedTestBean1", ntb1);
        NestedTestBean ntb2 = new NestedTestBean();
        bf.registerSingleton("nestedTestBean2", ntb2);
        // Two calls to verify that caching doesn't break re-creation.
        AutowiredAnnotationBeanPostProcessorTests.OptionalCollectionResourceInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.OptionalCollectionResourceInjectionBean) (bf.getBean("annotatedBean")));
        bean = ((AutowiredAnnotationBeanPostProcessorTests.OptionalCollectionResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(tb, bean.getTestBean());
        Assert.assertSame(tb, bean.getTestBean2());
        Assert.assertSame(tb, bean.getTestBean3());
        Assert.assertSame(tb, bean.getTestBean4());
        Assert.assertSame(itb, bean.getIndexedTestBean());
        Assert.assertEquals(2, bean.getNestedTestBeans().size());
        Assert.assertSame(ntb1, bean.getNestedTestBeans().get(0));
        Assert.assertSame(ntb2, bean.getNestedTestBeans().get(1));
        Assert.assertEquals(2, bean.nestedTestBeansSetter.size());
        Assert.assertSame(ntb1, bean.nestedTestBeansSetter.get(0));
        Assert.assertSame(ntb2, bean.nestedTestBeansSetter.get(1));
        Assert.assertEquals(2, bean.nestedTestBeansField.size());
        Assert.assertSame(ntb1, bean.nestedTestBeansField.get(0));
        Assert.assertSame(ntb2, bean.nestedTestBeansField.get(1));
    }

    @Test
    public void testOptionalCollectionResourceInjectionWithSingleElement() {
        RootBeanDefinition rbd = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.OptionalCollectionResourceInjectionBean.class);
        rbd.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", rbd);
        TestBean tb = new TestBean();
        bf.registerSingleton("testBean", tb);
        IndexedTestBean itb = new IndexedTestBean();
        bf.registerSingleton("indexedTestBean", itb);
        NestedTestBean ntb1 = new NestedTestBean();
        bf.registerSingleton("nestedTestBean1", ntb1);
        // Two calls to verify that caching doesn't break re-creation.
        AutowiredAnnotationBeanPostProcessorTests.OptionalCollectionResourceInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.OptionalCollectionResourceInjectionBean) (bf.getBean("annotatedBean")));
        bean = ((AutowiredAnnotationBeanPostProcessorTests.OptionalCollectionResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(tb, bean.getTestBean());
        Assert.assertSame(tb, bean.getTestBean2());
        Assert.assertSame(tb, bean.getTestBean3());
        Assert.assertSame(tb, bean.getTestBean4());
        Assert.assertSame(itb, bean.getIndexedTestBean());
        Assert.assertEquals(1, bean.getNestedTestBeans().size());
        Assert.assertSame(ntb1, bean.getNestedTestBeans().get(0));
        Assert.assertEquals(1, bean.nestedTestBeansSetter.size());
        Assert.assertSame(ntb1, bean.nestedTestBeansSetter.get(0));
        Assert.assertEquals(1, bean.nestedTestBeansField.size());
        Assert.assertSame(ntb1, bean.nestedTestBeansField.get(0));
    }

    @Test
    public void testOptionalResourceInjectionWithIncompleteDependencies() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.OptionalResourceInjectionBean.class));
        TestBean tb = new TestBean();
        bf.registerSingleton("testBean", tb);
        AutowiredAnnotationBeanPostProcessorTests.OptionalResourceInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.OptionalResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(tb, bean.getTestBean());
        Assert.assertSame(tb, bean.getTestBean2());
        Assert.assertSame(tb, bean.getTestBean3());
        Assert.assertNull(bean.getTestBean4());
        Assert.assertNull(bean.getNestedTestBeans());
    }

    @Test
    public void testOptionalResourceInjectionWithNoDependencies() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.OptionalResourceInjectionBean.class));
        AutowiredAnnotationBeanPostProcessorTests.OptionalResourceInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.OptionalResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertNull(bean.getTestBean());
        Assert.assertNull(bean.getTestBean2());
        Assert.assertNull(bean.getTestBean3());
        Assert.assertNull(bean.getTestBean4());
        Assert.assertNull(bean.getNestedTestBeans());
    }

    @Test
    public void testOrderedResourceInjection() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.OptionalResourceInjectionBean.class));
        TestBean tb = new TestBean();
        bf.registerSingleton("testBean", tb);
        IndexedTestBean itb = new IndexedTestBean();
        bf.registerSingleton("indexedTestBean", itb);
        AutowiredAnnotationBeanPostProcessorTests.OrderedNestedTestBean ntb1 = new AutowiredAnnotationBeanPostProcessorTests.OrderedNestedTestBean();
        ntb1.setOrder(2);
        bf.registerSingleton("nestedTestBean1", ntb1);
        AutowiredAnnotationBeanPostProcessorTests.OrderedNestedTestBean ntb2 = new AutowiredAnnotationBeanPostProcessorTests.OrderedNestedTestBean();
        ntb2.setOrder(1);
        bf.registerSingleton("nestedTestBean2", ntb2);
        AutowiredAnnotationBeanPostProcessorTests.OptionalResourceInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.OptionalResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(tb, bean.getTestBean());
        Assert.assertSame(tb, bean.getTestBean2());
        Assert.assertSame(tb, bean.getTestBean3());
        Assert.assertSame(tb, bean.getTestBean4());
        Assert.assertSame(itb, bean.getIndexedTestBean());
        Assert.assertEquals(2, bean.getNestedTestBeans().length);
        Assert.assertSame(ntb2, bean.getNestedTestBeans()[0]);
        Assert.assertSame(ntb1, bean.getNestedTestBeans()[1]);
        Assert.assertEquals(2, bean.nestedTestBeansField.length);
        Assert.assertSame(ntb2, bean.nestedTestBeansField[0]);
        Assert.assertSame(ntb1, bean.nestedTestBeansField[1]);
    }

    @Test
    public void testAnnotationOrderedResourceInjection() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.OptionalResourceInjectionBean.class));
        TestBean tb = new TestBean();
        bf.registerSingleton("testBean", tb);
        IndexedTestBean itb = new IndexedTestBean();
        bf.registerSingleton("indexedTestBean", itb);
        AutowiredAnnotationBeanPostProcessorTests.FixedOrder2NestedTestBean ntb1 = new AutowiredAnnotationBeanPostProcessorTests.FixedOrder2NestedTestBean();
        bf.registerSingleton("nestedTestBean1", ntb1);
        AutowiredAnnotationBeanPostProcessorTests.FixedOrder1NestedTestBean ntb2 = new AutowiredAnnotationBeanPostProcessorTests.FixedOrder1NestedTestBean();
        bf.registerSingleton("nestedTestBean2", ntb2);
        AutowiredAnnotationBeanPostProcessorTests.OptionalResourceInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.OptionalResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(tb, bean.getTestBean());
        Assert.assertSame(tb, bean.getTestBean2());
        Assert.assertSame(tb, bean.getTestBean3());
        Assert.assertSame(tb, bean.getTestBean4());
        Assert.assertSame(itb, bean.getIndexedTestBean());
        Assert.assertEquals(2, bean.getNestedTestBeans().length);
        Assert.assertSame(ntb2, bean.getNestedTestBeans()[0]);
        Assert.assertSame(ntb1, bean.getNestedTestBeans()[1]);
        Assert.assertEquals(2, bean.nestedTestBeansField.length);
        Assert.assertSame(ntb2, bean.nestedTestBeansField[0]);
        Assert.assertSame(ntb1, bean.nestedTestBeansField[1]);
    }

    @Test
    public void testOrderedCollectionResourceInjection() {
        RootBeanDefinition rbd = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.OptionalCollectionResourceInjectionBean.class);
        rbd.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", rbd);
        TestBean tb = new TestBean();
        bf.registerSingleton("testBean", tb);
        IndexedTestBean itb = new IndexedTestBean();
        bf.registerSingleton("indexedTestBean", itb);
        AutowiredAnnotationBeanPostProcessorTests.OrderedNestedTestBean ntb1 = new AutowiredAnnotationBeanPostProcessorTests.OrderedNestedTestBean();
        ntb1.setOrder(2);
        bf.registerSingleton("nestedTestBean1", ntb1);
        AutowiredAnnotationBeanPostProcessorTests.OrderedNestedTestBean ntb2 = new AutowiredAnnotationBeanPostProcessorTests.OrderedNestedTestBean();
        ntb2.setOrder(1);
        bf.registerSingleton("nestedTestBean2", ntb2);
        // Two calls to verify that caching doesn't break re-creation.
        AutowiredAnnotationBeanPostProcessorTests.OptionalCollectionResourceInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.OptionalCollectionResourceInjectionBean) (bf.getBean("annotatedBean")));
        bean = ((AutowiredAnnotationBeanPostProcessorTests.OptionalCollectionResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(tb, bean.getTestBean());
        Assert.assertSame(tb, bean.getTestBean2());
        Assert.assertSame(tb, bean.getTestBean3());
        Assert.assertSame(tb, bean.getTestBean4());
        Assert.assertSame(itb, bean.getIndexedTestBean());
        Assert.assertEquals(2, bean.getNestedTestBeans().size());
        Assert.assertSame(ntb2, bean.getNestedTestBeans().get(0));
        Assert.assertSame(ntb1, bean.getNestedTestBeans().get(1));
        Assert.assertEquals(2, bean.nestedTestBeansSetter.size());
        Assert.assertSame(ntb2, bean.nestedTestBeansSetter.get(0));
        Assert.assertSame(ntb1, bean.nestedTestBeansSetter.get(1));
        Assert.assertEquals(2, bean.nestedTestBeansField.size());
        Assert.assertSame(ntb2, bean.nestedTestBeansField.get(0));
        Assert.assertSame(ntb1, bean.nestedTestBeansField.get(1));
    }

    @Test
    public void testAnnotationOrderedCollectionResourceInjection() {
        RootBeanDefinition rbd = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.OptionalCollectionResourceInjectionBean.class);
        rbd.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", rbd);
        TestBean tb = new TestBean();
        bf.registerSingleton("testBean", tb);
        IndexedTestBean itb = new IndexedTestBean();
        bf.registerSingleton("indexedTestBean", itb);
        AutowiredAnnotationBeanPostProcessorTests.FixedOrder2NestedTestBean ntb1 = new AutowiredAnnotationBeanPostProcessorTests.FixedOrder2NestedTestBean();
        bf.registerSingleton("nestedTestBean1", ntb1);
        AutowiredAnnotationBeanPostProcessorTests.FixedOrder1NestedTestBean ntb2 = new AutowiredAnnotationBeanPostProcessorTests.FixedOrder1NestedTestBean();
        bf.registerSingleton("nestedTestBean2", ntb2);
        // Two calls to verify that caching doesn't break re-creation.
        AutowiredAnnotationBeanPostProcessorTests.OptionalCollectionResourceInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.OptionalCollectionResourceInjectionBean) (bf.getBean("annotatedBean")));
        bean = ((AutowiredAnnotationBeanPostProcessorTests.OptionalCollectionResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(tb, bean.getTestBean());
        Assert.assertSame(tb, bean.getTestBean2());
        Assert.assertSame(tb, bean.getTestBean3());
        Assert.assertSame(tb, bean.getTestBean4());
        Assert.assertSame(itb, bean.getIndexedTestBean());
        Assert.assertEquals(2, bean.getNestedTestBeans().size());
        Assert.assertSame(ntb2, bean.getNestedTestBeans().get(0));
        Assert.assertSame(ntb1, bean.getNestedTestBeans().get(1));
        Assert.assertEquals(2, bean.nestedTestBeansSetter.size());
        Assert.assertSame(ntb2, bean.nestedTestBeansSetter.get(0));
        Assert.assertSame(ntb1, bean.nestedTestBeansSetter.get(1));
        Assert.assertEquals(2, bean.nestedTestBeansField.size());
        Assert.assertSame(ntb2, bean.nestedTestBeansField.get(0));
        Assert.assertSame(ntb1, bean.nestedTestBeansField.get(1));
    }

    @Test
    public void testConstructorResourceInjection() {
        RootBeanDefinition bd = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.ConstructorResourceInjectionBean.class);
        bd.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", bd);
        TestBean tb = new TestBean();
        bf.registerSingleton("testBean", tb);
        NestedTestBean ntb = new NestedTestBean();
        bf.registerSingleton("nestedTestBean", ntb);
        AutowiredAnnotationBeanPostProcessorTests.ConstructorResourceInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.ConstructorResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(tb, bean.getTestBean());
        Assert.assertSame(tb, bean.getTestBean2());
        Assert.assertSame(tb, bean.getTestBean3());
        Assert.assertSame(tb, bean.getTestBean4());
        Assert.assertSame(ntb, bean.getNestedTestBean());
        Assert.assertSame(bf, bean.getBeanFactory());
        bean = ((AutowiredAnnotationBeanPostProcessorTests.ConstructorResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(tb, bean.getTestBean());
        Assert.assertSame(tb, bean.getTestBean2());
        Assert.assertSame(tb, bean.getTestBean3());
        Assert.assertSame(tb, bean.getTestBean4());
        Assert.assertSame(ntb, bean.getNestedTestBean());
        Assert.assertSame(bf, bean.getBeanFactory());
    }

    @Test
    public void testConstructorResourceInjectionWithNullFromFactoryBean() {
        RootBeanDefinition bd = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.ConstructorResourceInjectionBean.class);
        bd.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", bd);
        TestBean tb = new TestBean();
        bf.registerSingleton("testBean", tb);
        bf.registerBeanDefinition("nestedTestBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.NullNestedTestBeanFactoryBean.class));
        bf.registerSingleton("nestedTestBean2", new NestedTestBean());
        AutowiredAnnotationBeanPostProcessorTests.ConstructorResourceInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.ConstructorResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(tb, bean.getTestBean());
        Assert.assertSame(tb, bean.getTestBean2());
        Assert.assertSame(tb, bean.getTestBean3());
        Assert.assertSame(tb, bean.getTestBean4());
        Assert.assertNull(bean.getNestedTestBean());
        Assert.assertSame(bf, bean.getBeanFactory());
        bean = ((AutowiredAnnotationBeanPostProcessorTests.ConstructorResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(tb, bean.getTestBean());
        Assert.assertSame(tb, bean.getTestBean2());
        Assert.assertSame(tb, bean.getTestBean3());
        Assert.assertSame(tb, bean.getTestBean4());
        Assert.assertNull(bean.getNestedTestBean());
        Assert.assertSame(bf, bean.getBeanFactory());
    }

    @Test
    public void testConstructorResourceInjectionWithNullFromFactoryMethod() {
        RootBeanDefinition bd = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.ConstructorResourceInjectionBean.class);
        bd.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", bd);
        RootBeanDefinition tb = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.NullFactoryMethods.class);
        tb.setFactoryMethodName("createTestBean");
        bf.registerBeanDefinition("testBean", tb);
        RootBeanDefinition ntb = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.NullFactoryMethods.class);
        ntb.setFactoryMethodName("createNestedTestBean");
        bf.registerBeanDefinition("nestedTestBean", ntb);
        bf.registerSingleton("nestedTestBean2", new NestedTestBean());
        AutowiredAnnotationBeanPostProcessorTests.ConstructorResourceInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.ConstructorResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertNull(bean.getTestBean());
        Assert.assertNull(bean.getTestBean2());
        Assert.assertNull(bean.getTestBean3());
        Assert.assertNull(bean.getTestBean4());
        Assert.assertNull(bean.getNestedTestBean());
        Assert.assertSame(bf, bean.getBeanFactory());
        bean = ((AutowiredAnnotationBeanPostProcessorTests.ConstructorResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertNull(bean.getTestBean());
        Assert.assertNull(bean.getTestBean2());
        Assert.assertNull(bean.getTestBean3());
        Assert.assertNull(bean.getTestBean4());
        Assert.assertNull(bean.getNestedTestBean());
        Assert.assertSame(bf, bean.getBeanFactory());
    }

    @Test
    public void testConstructorResourceInjectionWithMultipleCandidates() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.ConstructorsResourceInjectionBean.class));
        TestBean tb = new TestBean();
        bf.registerSingleton("testBean", tb);
        NestedTestBean ntb1 = new NestedTestBean();
        bf.registerSingleton("nestedTestBean1", ntb1);
        NestedTestBean ntb2 = new NestedTestBean();
        bf.registerSingleton("nestedTestBean2", ntb2);
        AutowiredAnnotationBeanPostProcessorTests.ConstructorsResourceInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.ConstructorsResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertNull(bean.getTestBean3());
        Assert.assertSame(tb, bean.getTestBean4());
        Assert.assertEquals(2, bean.getNestedTestBeans().length);
        Assert.assertSame(ntb1, bean.getNestedTestBeans()[0]);
        Assert.assertSame(ntb2, bean.getNestedTestBeans()[1]);
    }

    @Test
    public void testConstructorResourceInjectionWithNoCandidatesAndNoFallback() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.ConstructorWithoutFallbackBean.class));
        try {
            bf.getBean("annotatedBean");
            Assert.fail("Should have thrown UnsatisfiedDependencyException");
        } catch (UnsatisfiedDependencyException ex) {
            // expected
            Assert.assertSame(AutowiredAnnotationBeanPostProcessorTests.ConstructorWithoutFallbackBean.class, ex.getInjectionPoint().getMethodParameter().getDeclaringClass());
        }
    }

    @Test
    public void testConstructorResourceInjectionWithCollectionAndNullFromFactoryBean() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.ConstructorsCollectionResourceInjectionBean.class));
        TestBean tb = new TestBean();
        bf.registerSingleton("testBean", tb);
        bf.registerBeanDefinition("nestedTestBean1", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.NullNestedTestBeanFactoryBean.class));
        NestedTestBean ntb2 = new NestedTestBean();
        bf.registerSingleton("nestedTestBean2", ntb2);
        AutowiredAnnotationBeanPostProcessorTests.ConstructorsCollectionResourceInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.ConstructorsCollectionResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertNull(bean.getTestBean3());
        Assert.assertSame(tb, bean.getTestBean4());
        Assert.assertEquals(1, bean.getNestedTestBeans().size());
        Assert.assertSame(ntb2, bean.getNestedTestBeans().get(0));
        Map<String, NestedTestBean> map = bf.getBeansOfType(NestedTestBean.class);
        Assert.assertNull(map.get("nestedTestBean1"));
        Assert.assertSame(ntb2, map.get("nestedTestBean2"));
    }

    @Test
    public void testConstructorResourceInjectionWithMultipleCandidatesAsCollection() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.ConstructorsCollectionResourceInjectionBean.class));
        TestBean tb = new TestBean();
        bf.registerSingleton("testBean", tb);
        NestedTestBean ntb1 = new NestedTestBean();
        bf.registerSingleton("nestedTestBean1", ntb1);
        NestedTestBean ntb2 = new NestedTestBean();
        bf.registerSingleton("nestedTestBean2", ntb2);
        AutowiredAnnotationBeanPostProcessorTests.ConstructorsCollectionResourceInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.ConstructorsCollectionResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertNull(bean.getTestBean3());
        Assert.assertSame(tb, bean.getTestBean4());
        Assert.assertEquals(2, bean.getNestedTestBeans().size());
        Assert.assertSame(ntb1, bean.getNestedTestBeans().get(0));
        Assert.assertSame(ntb2, bean.getNestedTestBeans().get(1));
    }

    @Test
    public void testConstructorResourceInjectionWithMultipleOrderedCandidates() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.ConstructorsResourceInjectionBean.class));
        TestBean tb = new TestBean();
        bf.registerSingleton("testBean", tb);
        AutowiredAnnotationBeanPostProcessorTests.FixedOrder2NestedTestBean ntb1 = new AutowiredAnnotationBeanPostProcessorTests.FixedOrder2NestedTestBean();
        bf.registerSingleton("nestedTestBean1", ntb1);
        AutowiredAnnotationBeanPostProcessorTests.FixedOrder1NestedTestBean ntb2 = new AutowiredAnnotationBeanPostProcessorTests.FixedOrder1NestedTestBean();
        bf.registerSingleton("nestedTestBean2", ntb2);
        AutowiredAnnotationBeanPostProcessorTests.ConstructorsResourceInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.ConstructorsResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertNull(bean.getTestBean3());
        Assert.assertSame(tb, bean.getTestBean4());
        Assert.assertEquals(2, bean.getNestedTestBeans().length);
        Assert.assertSame(ntb2, bean.getNestedTestBeans()[0]);
        Assert.assertSame(ntb1, bean.getNestedTestBeans()[1]);
    }

    @Test
    public void testConstructorResourceInjectionWithMultipleCandidatesAsOrderedCollection() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.ConstructorsCollectionResourceInjectionBean.class));
        TestBean tb = new TestBean();
        bf.registerSingleton("testBean", tb);
        AutowiredAnnotationBeanPostProcessorTests.FixedOrder2NestedTestBean ntb1 = new AutowiredAnnotationBeanPostProcessorTests.FixedOrder2NestedTestBean();
        bf.registerSingleton("nestedTestBean1", ntb1);
        AutowiredAnnotationBeanPostProcessorTests.FixedOrder1NestedTestBean ntb2 = new AutowiredAnnotationBeanPostProcessorTests.FixedOrder1NestedTestBean();
        bf.registerSingleton("nestedTestBean2", ntb2);
        AutowiredAnnotationBeanPostProcessorTests.ConstructorsCollectionResourceInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.ConstructorsCollectionResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertNull(bean.getTestBean3());
        Assert.assertSame(tb, bean.getTestBean4());
        Assert.assertEquals(2, bean.getNestedTestBeans().size());
        Assert.assertSame(ntb2, bean.getNestedTestBeans().get(0));
        Assert.assertSame(ntb1, bean.getNestedTestBeans().get(1));
    }

    @Test
    public void testSingleConstructorInjectionWithMultipleCandidatesAsRequiredVararg() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.SingleConstructorVarargBean.class));
        TestBean tb = new TestBean();
        bf.registerSingleton("testBean", tb);
        AutowiredAnnotationBeanPostProcessorTests.FixedOrder2NestedTestBean ntb1 = new AutowiredAnnotationBeanPostProcessorTests.FixedOrder2NestedTestBean();
        bf.registerSingleton("nestedTestBean1", ntb1);
        AutowiredAnnotationBeanPostProcessorTests.FixedOrder1NestedTestBean ntb2 = new AutowiredAnnotationBeanPostProcessorTests.FixedOrder1NestedTestBean();
        bf.registerSingleton("nestedTestBean2", ntb2);
        AutowiredAnnotationBeanPostProcessorTests.SingleConstructorVarargBean bean = ((AutowiredAnnotationBeanPostProcessorTests.SingleConstructorVarargBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(tb, bean.getTestBean());
        Assert.assertEquals(2, bean.getNestedTestBeans().size());
        Assert.assertSame(ntb2, bean.getNestedTestBeans().get(0));
        Assert.assertSame(ntb1, bean.getNestedTestBeans().get(1));
    }

    @Test
    public void testSingleConstructorInjectionWithEmptyVararg() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.SingleConstructorVarargBean.class));
        TestBean tb = new TestBean();
        bf.registerSingleton("testBean", tb);
        AutowiredAnnotationBeanPostProcessorTests.SingleConstructorVarargBean bean = ((AutowiredAnnotationBeanPostProcessorTests.SingleConstructorVarargBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(tb, bean.getTestBean());
        Assert.assertNotNull(bean.getNestedTestBeans());
        Assert.assertTrue(bean.getNestedTestBeans().isEmpty());
    }

    @Test
    public void testSingleConstructorInjectionWithMultipleCandidatesAsRequiredCollection() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.SingleConstructorRequiredCollectionBean.class));
        TestBean tb = new TestBean();
        bf.registerSingleton("testBean", tb);
        AutowiredAnnotationBeanPostProcessorTests.FixedOrder2NestedTestBean ntb1 = new AutowiredAnnotationBeanPostProcessorTests.FixedOrder2NestedTestBean();
        bf.registerSingleton("nestedTestBean1", ntb1);
        AutowiredAnnotationBeanPostProcessorTests.FixedOrder1NestedTestBean ntb2 = new AutowiredAnnotationBeanPostProcessorTests.FixedOrder1NestedTestBean();
        bf.registerSingleton("nestedTestBean2", ntb2);
        AutowiredAnnotationBeanPostProcessorTests.SingleConstructorRequiredCollectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.SingleConstructorRequiredCollectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(tb, bean.getTestBean());
        Assert.assertEquals(2, bean.getNestedTestBeans().size());
        Assert.assertSame(ntb2, bean.getNestedTestBeans().get(0));
        Assert.assertSame(ntb1, bean.getNestedTestBeans().get(1));
    }

    @Test
    public void testSingleConstructorInjectionWithEmptyCollection() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.SingleConstructorRequiredCollectionBean.class));
        TestBean tb = new TestBean();
        bf.registerSingleton("testBean", tb);
        AutowiredAnnotationBeanPostProcessorTests.SingleConstructorRequiredCollectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.SingleConstructorRequiredCollectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(tb, bean.getTestBean());
        Assert.assertNotNull(bean.getNestedTestBeans());
        Assert.assertTrue(bean.getNestedTestBeans().isEmpty());
    }

    @Test
    public void testSingleConstructorInjectionWithMultipleCandidatesAsOrderedCollection() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.SingleConstructorOptionalCollectionBean.class));
        TestBean tb = new TestBean();
        bf.registerSingleton("testBean", tb);
        AutowiredAnnotationBeanPostProcessorTests.FixedOrder2NestedTestBean ntb1 = new AutowiredAnnotationBeanPostProcessorTests.FixedOrder2NestedTestBean();
        bf.registerSingleton("nestedTestBean1", ntb1);
        AutowiredAnnotationBeanPostProcessorTests.FixedOrder1NestedTestBean ntb2 = new AutowiredAnnotationBeanPostProcessorTests.FixedOrder1NestedTestBean();
        bf.registerSingleton("nestedTestBean2", ntb2);
        AutowiredAnnotationBeanPostProcessorTests.SingleConstructorOptionalCollectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.SingleConstructorOptionalCollectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(tb, bean.getTestBean());
        Assert.assertEquals(2, bean.getNestedTestBeans().size());
        Assert.assertSame(ntb2, bean.getNestedTestBeans().get(0));
        Assert.assertSame(ntb1, bean.getNestedTestBeans().get(1));
    }

    @Test
    public void testSingleConstructorInjectionWithEmptyCollectionAsNull() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.SingleConstructorOptionalCollectionBean.class));
        TestBean tb = new TestBean();
        bf.registerSingleton("testBean", tb);
        AutowiredAnnotationBeanPostProcessorTests.SingleConstructorOptionalCollectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.SingleConstructorOptionalCollectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(tb, bean.getTestBean());
        Assert.assertNull(bean.getNestedTestBeans());
    }

    @Test(expected = UnsatisfiedDependencyException.class)
    public void testSingleConstructorInjectionWithMissingDependency() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.SingleConstructorOptionalCollectionBean.class));
        bf.getBean("annotatedBean");
    }

    @Test(expected = UnsatisfiedDependencyException.class)
    public void testSingleConstructorInjectionWithNullDependency() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.SingleConstructorOptionalCollectionBean.class));
        RootBeanDefinition tb = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.NullFactoryMethods.class);
        tb.setFactoryMethodName("createTestBean");
        bf.registerBeanDefinition("testBean", tb);
        bf.getBean("annotatedBean");
    }

    @Test
    public void testConstructorResourceInjectionWithMultipleCandidatesAndFallback() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.ConstructorsResourceInjectionBean.class));
        TestBean tb = new TestBean();
        bf.registerSingleton("testBean", tb);
        AutowiredAnnotationBeanPostProcessorTests.ConstructorsResourceInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.ConstructorsResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(tb, bean.getTestBean3());
        Assert.assertNull(bean.getTestBean4());
    }

    @Test
    public void testConstructorResourceInjectionWithMultipleCandidatesAndDefaultFallback() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.ConstructorsResourceInjectionBean.class));
        AutowiredAnnotationBeanPostProcessorTests.ConstructorsResourceInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.ConstructorsResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertNull(bean.getTestBean3());
        Assert.assertNull(bean.getTestBean4());
    }

    @Test
    public void testConstructorInjectionWithMap() {
        RootBeanDefinition bd = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.MapConstructorInjectionBean.class);
        bd.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", bd);
        TestBean tb1 = new TestBean("tb1");
        bf.registerSingleton("testBean1", tb1);
        RootBeanDefinition tb2 = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.NullFactoryMethods.class);
        tb2.setFactoryMethodName("createTestBean");
        bf.registerBeanDefinition("testBean2", tb2);
        AutowiredAnnotationBeanPostProcessorTests.MapConstructorInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.MapConstructorInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertEquals(1, bean.getTestBeanMap().size());
        Assert.assertSame(tb1, bean.getTestBeanMap().get("testBean1"));
        Assert.assertNull(bean.getTestBeanMap().get("testBean2"));
        bean = ((AutowiredAnnotationBeanPostProcessorTests.MapConstructorInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertEquals(1, bean.getTestBeanMap().size());
        Assert.assertSame(tb1, bean.getTestBeanMap().get("testBean1"));
        Assert.assertNull(bean.getTestBeanMap().get("testBean2"));
    }

    @Test
    public void testFieldInjectionWithMap() {
        RootBeanDefinition bd = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.MapFieldInjectionBean.class);
        bd.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", bd);
        TestBean tb1 = new TestBean("tb1");
        TestBean tb2 = new TestBean("tb2");
        bf.registerSingleton("testBean1", tb1);
        bf.registerSingleton("testBean2", tb2);
        AutowiredAnnotationBeanPostProcessorTests.MapFieldInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.MapFieldInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertEquals(2, bean.getTestBeanMap().size());
        Assert.assertTrue(bean.getTestBeanMap().keySet().contains("testBean1"));
        Assert.assertTrue(bean.getTestBeanMap().keySet().contains("testBean2"));
        Assert.assertTrue(bean.getTestBeanMap().values().contains(tb1));
        Assert.assertTrue(bean.getTestBeanMap().values().contains(tb2));
        bean = ((AutowiredAnnotationBeanPostProcessorTests.MapFieldInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertEquals(2, bean.getTestBeanMap().size());
        Assert.assertTrue(bean.getTestBeanMap().keySet().contains("testBean1"));
        Assert.assertTrue(bean.getTestBeanMap().keySet().contains("testBean2"));
        Assert.assertTrue(bean.getTestBeanMap().values().contains(tb1));
        Assert.assertTrue(bean.getTestBeanMap().values().contains(tb2));
    }

    @Test
    public void testMethodInjectionWithMap() {
        RootBeanDefinition bd = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.MapMethodInjectionBean.class);
        bd.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", bd);
        TestBean tb = new TestBean();
        bf.registerSingleton("testBean", tb);
        AutowiredAnnotationBeanPostProcessorTests.MapMethodInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.MapMethodInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertEquals(1, bean.getTestBeanMap().size());
        Assert.assertTrue(bean.getTestBeanMap().keySet().contains("testBean"));
        Assert.assertTrue(bean.getTestBeanMap().values().contains(tb));
        Assert.assertSame(tb, bean.getTestBean());
        bean = ((AutowiredAnnotationBeanPostProcessorTests.MapMethodInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertEquals(1, bean.getTestBeanMap().size());
        Assert.assertTrue(bean.getTestBeanMap().keySet().contains("testBean"));
        Assert.assertTrue(bean.getTestBeanMap().values().contains(tb));
        Assert.assertSame(tb, bean.getTestBean());
    }

    @Test
    public void testMethodInjectionWithMapAndMultipleMatches() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.MapMethodInjectionBean.class));
        bf.registerBeanDefinition("testBean1", new RootBeanDefinition(TestBean.class));
        bf.registerBeanDefinition("testBean2", new RootBeanDefinition(TestBean.class));
        try {
            bf.getBean("annotatedBean");
            Assert.fail("should have failed, more than one bean of type");
        } catch (UnsatisfiedDependencyException ex) {
            // expected
            Assert.assertSame(AutowiredAnnotationBeanPostProcessorTests.MapMethodInjectionBean.class, ex.getInjectionPoint().getMethodParameter().getDeclaringClass());
        }
    }

    @Test
    public void testMethodInjectionWithMapAndMultipleMatchesButOnlyOneAutowireCandidate() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.MapMethodInjectionBean.class));
        bf.registerBeanDefinition("testBean1", new RootBeanDefinition(TestBean.class));
        RootBeanDefinition rbd2 = new RootBeanDefinition(TestBean.class);
        rbd2.setAutowireCandidate(false);
        bf.registerBeanDefinition("testBean2", rbd2);
        AutowiredAnnotationBeanPostProcessorTests.MapMethodInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.MapMethodInjectionBean) (bf.getBean("annotatedBean")));
        TestBean tb = ((TestBean) (bf.getBean("testBean1")));
        Assert.assertEquals(1, bean.getTestBeanMap().size());
        Assert.assertTrue(bean.getTestBeanMap().keySet().contains("testBean1"));
        Assert.assertTrue(bean.getTestBeanMap().values().contains(tb));
        Assert.assertSame(tb, bean.getTestBean());
    }

    @Test
    public void testMethodInjectionWithMapAndNoMatches() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.MapMethodInjectionBean.class));
        AutowiredAnnotationBeanPostProcessorTests.MapMethodInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.MapMethodInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertNull(bean.getTestBeanMap());
        Assert.assertNull(bean.getTestBean());
    }

    @Test
    public void testConstructorInjectionWithTypedMapAsBean() {
        RootBeanDefinition bd = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.MapConstructorInjectionBean.class);
        bd.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", bd);
        AutowiredAnnotationBeanPostProcessorTests.MyTestBeanMap tbm = new AutowiredAnnotationBeanPostProcessorTests.MyTestBeanMap();
        tbm.put("testBean1", new TestBean("tb1"));
        tbm.put("testBean2", new TestBean("tb2"));
        bf.registerSingleton("testBeans", tbm);
        bf.registerSingleton("otherMap", new Properties());
        AutowiredAnnotationBeanPostProcessorTests.MapConstructorInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.MapConstructorInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(tbm, bean.getTestBeanMap());
        bean = ((AutowiredAnnotationBeanPostProcessorTests.MapConstructorInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(tbm, bean.getTestBeanMap());
    }

    @Test
    public void testConstructorInjectionWithPlainMapAsBean() {
        RootBeanDefinition bd = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.MapConstructorInjectionBean.class);
        bd.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", bd);
        RootBeanDefinition tbm = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.CollectionFactoryMethods.class);
        tbm.setUniqueFactoryMethodName("testBeanMap");
        bf.registerBeanDefinition("myTestBeanMap", tbm);
        bf.registerSingleton("otherMap", new HashMap());
        AutowiredAnnotationBeanPostProcessorTests.MapConstructorInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.MapConstructorInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(bf.getBean("myTestBeanMap"), bean.getTestBeanMap());
        bean = ((AutowiredAnnotationBeanPostProcessorTests.MapConstructorInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(bf.getBean("myTestBeanMap"), bean.getTestBeanMap());
    }

    @Test
    public void testConstructorInjectionWithCustomMapAsBean() {
        RootBeanDefinition bd = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.CustomMapConstructorInjectionBean.class);
        bd.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", bd);
        RootBeanDefinition tbm = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.CustomCollectionFactoryMethods.class);
        tbm.setUniqueFactoryMethodName("testBeanMap");
        bf.registerBeanDefinition("myTestBeanMap", tbm);
        bf.registerSingleton("testBean1", new TestBean());
        bf.registerSingleton("testBean2", new TestBean());
        AutowiredAnnotationBeanPostProcessorTests.CustomMapConstructorInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.CustomMapConstructorInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(bf.getBean("myTestBeanMap"), bean.getTestBeanMap());
        bean = ((AutowiredAnnotationBeanPostProcessorTests.CustomMapConstructorInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(bf.getBean("myTestBeanMap"), bean.getTestBeanMap());
    }

    @Test
    public void testConstructorInjectionWithPlainHashMapAsBean() {
        RootBeanDefinition bd = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.QualifiedMapConstructorInjectionBean.class);
        bd.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", bd);
        bf.registerBeanDefinition("myTestBeanMap", new RootBeanDefinition(HashMap.class));
        AutowiredAnnotationBeanPostProcessorTests.QualifiedMapConstructorInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.QualifiedMapConstructorInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(bf.getBean("myTestBeanMap"), bean.getTestBeanMap());
        bean = ((AutowiredAnnotationBeanPostProcessorTests.QualifiedMapConstructorInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(bf.getBean("myTestBeanMap"), bean.getTestBeanMap());
    }

    @Test
    public void testConstructorInjectionWithTypedSetAsBean() {
        RootBeanDefinition bd = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.SetConstructorInjectionBean.class);
        bd.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", bd);
        AutowiredAnnotationBeanPostProcessorTests.MyTestBeanSet tbs = new AutowiredAnnotationBeanPostProcessorTests.MyTestBeanSet();
        tbs.add(new TestBean("tb1"));
        tbs.add(new TestBean("tb2"));
        bf.registerSingleton("testBeans", tbs);
        bf.registerSingleton("otherSet", new HashSet());
        AutowiredAnnotationBeanPostProcessorTests.SetConstructorInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.SetConstructorInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(tbs, bean.getTestBeanSet());
        bean = ((AutowiredAnnotationBeanPostProcessorTests.SetConstructorInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(tbs, bean.getTestBeanSet());
    }

    @Test
    public void testConstructorInjectionWithPlainSetAsBean() {
        RootBeanDefinition bd = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.SetConstructorInjectionBean.class);
        bd.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", bd);
        RootBeanDefinition tbs = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.CollectionFactoryMethods.class);
        tbs.setUniqueFactoryMethodName("testBeanSet");
        bf.registerBeanDefinition("myTestBeanSet", tbs);
        bf.registerSingleton("otherSet", new HashSet());
        AutowiredAnnotationBeanPostProcessorTests.SetConstructorInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.SetConstructorInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(bf.getBean("myTestBeanSet"), bean.getTestBeanSet());
        bean = ((AutowiredAnnotationBeanPostProcessorTests.SetConstructorInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(bf.getBean("myTestBeanSet"), bean.getTestBeanSet());
    }

    @Test
    public void testConstructorInjectionWithCustomSetAsBean() {
        RootBeanDefinition bd = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.CustomSetConstructorInjectionBean.class);
        bd.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", bd);
        RootBeanDefinition tbs = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.CustomCollectionFactoryMethods.class);
        tbs.setUniqueFactoryMethodName("testBeanSet");
        bf.registerBeanDefinition("myTestBeanSet", tbs);
        AutowiredAnnotationBeanPostProcessorTests.CustomSetConstructorInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.CustomSetConstructorInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(bf.getBean("myTestBeanSet"), bean.getTestBeanSet());
        bean = ((AutowiredAnnotationBeanPostProcessorTests.CustomSetConstructorInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(bf.getBean("myTestBeanSet"), bean.getTestBeanSet());
    }

    @Test
    public void testSelfReference() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.SelfInjectionBean.class));
        AutowiredAnnotationBeanPostProcessorTests.SelfInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.SelfInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(bean, bean.reference);
        Assert.assertNull(bean.referenceCollection);
    }

    @Test
    public void testSelfReferenceWithOther() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.SelfInjectionBean.class));
        bf.registerBeanDefinition("annotatedBean2", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.SelfInjectionBean.class));
        AutowiredAnnotationBeanPostProcessorTests.SelfInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.SelfInjectionBean) (bf.getBean("annotatedBean")));
        AutowiredAnnotationBeanPostProcessorTests.SelfInjectionBean bean2 = ((AutowiredAnnotationBeanPostProcessorTests.SelfInjectionBean) (bf.getBean("annotatedBean2")));
        Assert.assertSame(bean2, bean.reference);
        Assert.assertEquals(1, bean.referenceCollection.size());
        Assert.assertSame(bean2, bean.referenceCollection.get(0));
    }

    @Test
    public void testSelfReferenceCollection() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.SelfInjectionCollectionBean.class));
        AutowiredAnnotationBeanPostProcessorTests.SelfInjectionCollectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.SelfInjectionCollectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(bean, bean.reference);
        Assert.assertNull(bean.referenceCollection);
    }

    @Test
    public void testSelfReferenceCollectionWithOther() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.SelfInjectionCollectionBean.class));
        bf.registerBeanDefinition("annotatedBean2", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.SelfInjectionCollectionBean.class));
        AutowiredAnnotationBeanPostProcessorTests.SelfInjectionCollectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.SelfInjectionCollectionBean) (bf.getBean("annotatedBean")));
        AutowiredAnnotationBeanPostProcessorTests.SelfInjectionCollectionBean bean2 = ((AutowiredAnnotationBeanPostProcessorTests.SelfInjectionCollectionBean) (bf.getBean("annotatedBean2")));
        Assert.assertSame(bean2, bean.reference);
        Assert.assertSame(1, bean2.referenceCollection.size());
        Assert.assertSame(bean2, bean.referenceCollection.get(0));
    }

    @Test
    public void testObjectFactoryFieldInjection() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.ObjectFactoryFieldInjectionBean.class));
        bf.registerBeanDefinition("testBean", new RootBeanDefinition(TestBean.class));
        AutowiredAnnotationBeanPostProcessorTests.ObjectFactoryFieldInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.ObjectFactoryFieldInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(bf.getBean("testBean"), bean.getTestBean());
    }

    @Test
    public void testObjectFactoryConstructorInjection() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.ObjectFactoryConstructorInjectionBean.class));
        bf.registerBeanDefinition("testBean", new RootBeanDefinition(TestBean.class));
        AutowiredAnnotationBeanPostProcessorTests.ObjectFactoryConstructorInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.ObjectFactoryConstructorInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(bf.getBean("testBean"), bean.getTestBean());
    }

    @Test
    public void testObjectFactoryInjectionIntoPrototypeBean() {
        RootBeanDefinition annotatedBeanDefinition = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.ObjectFactoryFieldInjectionBean.class);
        annotatedBeanDefinition.setScope(BeanDefinition.SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", annotatedBeanDefinition);
        bf.registerBeanDefinition("testBean", new RootBeanDefinition(TestBean.class));
        AutowiredAnnotationBeanPostProcessorTests.ObjectFactoryFieldInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.ObjectFactoryFieldInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(bf.getBean("testBean"), bean.getTestBean());
        AutowiredAnnotationBeanPostProcessorTests.ObjectFactoryFieldInjectionBean anotherBean = ((AutowiredAnnotationBeanPostProcessorTests.ObjectFactoryFieldInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertNotSame(anotherBean, bean);
        Assert.assertSame(bf.getBean("testBean"), anotherBean.getTestBean());
    }

    @Test
    public void testObjectFactoryQualifierInjection() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.ObjectFactoryQualifierInjectionBean.class));
        RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
        bd.addQualifier(new AutowireCandidateQualifier(Qualifier.class, "testBean"));
        bf.registerBeanDefinition("dependencyBean", bd);
        bf.registerBeanDefinition("dependencyBean2", new RootBeanDefinition(TestBean.class));
        AutowiredAnnotationBeanPostProcessorTests.ObjectFactoryQualifierInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.ObjectFactoryQualifierInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(bf.getBean("dependencyBean"), bean.getTestBean());
    }

    @Test
    public void testObjectFactoryQualifierProviderInjection() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.ObjectFactoryQualifierInjectionBean.class));
        RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
        bd.setQualifiedElement(ReflectionUtils.findMethod(getClass(), "testBeanQualifierProvider"));
        bf.registerBeanDefinition("dependencyBean", bd);
        bf.registerBeanDefinition("dependencyBean2", new RootBeanDefinition(TestBean.class));
        AutowiredAnnotationBeanPostProcessorTests.ObjectFactoryQualifierInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.ObjectFactoryQualifierInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(bf.getBean("dependencyBean"), bean.getTestBean());
    }

    @Test
    public void testObjectFactorySerialization() throws Exception {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.ObjectFactoryFieldInjectionBean.class));
        bf.registerBeanDefinition("testBean", new RootBeanDefinition(TestBean.class));
        bf.setSerializationId("test");
        AutowiredAnnotationBeanPostProcessorTests.ObjectFactoryFieldInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.ObjectFactoryFieldInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(bf.getBean("testBean"), bean.getTestBean());
        bean = ((AutowiredAnnotationBeanPostProcessorTests.ObjectFactoryFieldInjectionBean) (SerializationTestUtils.serializeAndDeserialize(bean)));
        Assert.assertSame(bf.getBean("testBean"), bean.getTestBean());
    }

    @Test
    public void testObjectProviderInjectionWithPrototype() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.ObjectProviderInjectionBean.class));
        RootBeanDefinition tbd = new RootBeanDefinition(TestBean.class);
        tbd.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("testBean", tbd);
        AutowiredAnnotationBeanPostProcessorTests.ObjectProviderInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.ObjectProviderInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertEquals(bf.getBean("testBean"), bean.getTestBean());
        Assert.assertEquals(bf.getBean("testBean", "myName"), bean.getTestBean("myName"));
        Assert.assertEquals(bf.getBean("testBean"), bean.getOptionalTestBean());
        Assert.assertEquals(bf.getBean("testBean"), bean.getOptionalTestBeanWithDefault());
        Assert.assertEquals(bf.getBean("testBean"), bean.consumeOptionalTestBean());
        Assert.assertEquals(bf.getBean("testBean"), bean.getUniqueTestBean());
        Assert.assertEquals(bf.getBean("testBean"), bean.getUniqueTestBeanWithDefault());
        Assert.assertEquals(bf.getBean("testBean"), bean.consumeUniqueTestBean());
        List<?> testBeans = bean.iterateTestBeans();
        Assert.assertEquals(1, testBeans.size());
        Assert.assertTrue(testBeans.contains(bf.getBean("testBean")));
        testBeans = bean.forEachTestBeans();
        Assert.assertEquals(1, testBeans.size());
        Assert.assertTrue(testBeans.contains(bf.getBean("testBean")));
        testBeans = bean.streamTestBeans();
        Assert.assertEquals(1, testBeans.size());
        Assert.assertTrue(testBeans.contains(bf.getBean("testBean")));
        testBeans = bean.sortedTestBeans();
        Assert.assertEquals(1, testBeans.size());
        Assert.assertTrue(testBeans.contains(bf.getBean("testBean")));
    }

    @Test
    public void testObjectProviderInjectionWithSingletonTarget() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.ObjectProviderInjectionBean.class));
        bf.registerBeanDefinition("testBean", new RootBeanDefinition(TestBean.class));
        AutowiredAnnotationBeanPostProcessorTests.ObjectProviderInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.ObjectProviderInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(bf.getBean("testBean"), bean.getTestBean());
        Assert.assertSame(bf.getBean("testBean"), bean.getOptionalTestBean());
        Assert.assertSame(bf.getBean("testBean"), bean.getOptionalTestBeanWithDefault());
        Assert.assertEquals(bf.getBean("testBean"), bean.consumeOptionalTestBean());
        Assert.assertSame(bf.getBean("testBean"), bean.getUniqueTestBean());
        Assert.assertSame(bf.getBean("testBean"), bean.getUniqueTestBeanWithDefault());
        Assert.assertEquals(bf.getBean("testBean"), bean.consumeUniqueTestBean());
        List<?> testBeans = bean.iterateTestBeans();
        Assert.assertEquals(1, testBeans.size());
        Assert.assertTrue(testBeans.contains(bf.getBean("testBean")));
        testBeans = bean.forEachTestBeans();
        Assert.assertEquals(1, testBeans.size());
        Assert.assertTrue(testBeans.contains(bf.getBean("testBean")));
        testBeans = bean.streamTestBeans();
        Assert.assertEquals(1, testBeans.size());
        Assert.assertTrue(testBeans.contains(bf.getBean("testBean")));
        testBeans = bean.sortedTestBeans();
        Assert.assertEquals(1, testBeans.size());
        Assert.assertTrue(testBeans.contains(bf.getBean("testBean")));
    }

    @Test
    public void testObjectProviderInjectionWithTargetNotAvailable() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.ObjectProviderInjectionBean.class));
        AutowiredAnnotationBeanPostProcessorTests.ObjectProviderInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.ObjectProviderInjectionBean) (bf.getBean("annotatedBean")));
        try {
            bean.getTestBean();
            Assert.fail("Should have thrown NoSuchBeanDefinitionException");
        } catch (NoSuchBeanDefinitionException ex) {
            // expected
        }
        Assert.assertNull(bean.getOptionalTestBean());
        Assert.assertNull(bean.consumeOptionalTestBean());
        Assert.assertEquals(new TestBean("default"), bean.getOptionalTestBeanWithDefault());
        Assert.assertEquals(new TestBean("default"), bean.getUniqueTestBeanWithDefault());
        Assert.assertNull(bean.getUniqueTestBean());
        Assert.assertNull(bean.consumeUniqueTestBean());
        List<?> testBeans = bean.iterateTestBeans();
        Assert.assertTrue(testBeans.isEmpty());
        testBeans = bean.forEachTestBeans();
        Assert.assertTrue(testBeans.isEmpty());
        testBeans = bean.streamTestBeans();
        Assert.assertTrue(testBeans.isEmpty());
        testBeans = bean.sortedTestBeans();
        Assert.assertTrue(testBeans.isEmpty());
    }

    @Test
    public void testObjectProviderInjectionWithTargetNotUnique() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.ObjectProviderInjectionBean.class));
        bf.registerBeanDefinition("testBean1", new RootBeanDefinition(TestBean.class));
        bf.registerBeanDefinition("testBean2", new RootBeanDefinition(TestBean.class));
        AutowiredAnnotationBeanPostProcessorTests.ObjectProviderInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.ObjectProviderInjectionBean) (bf.getBean("annotatedBean")));
        try {
            bean.getTestBean();
            Assert.fail("Should have thrown NoUniqueBeanDefinitionException");
        } catch (NoUniqueBeanDefinitionException ex) {
            // expected
        }
        try {
            bean.getOptionalTestBean();
            Assert.fail("Should have thrown NoUniqueBeanDefinitionException");
        } catch (NoUniqueBeanDefinitionException ex) {
            // expected
        }
        try {
            bean.consumeOptionalTestBean();
            Assert.fail("Should have thrown NoUniqueBeanDefinitionException");
        } catch (NoUniqueBeanDefinitionException ex) {
            // expected
        }
        Assert.assertNull(bean.getUniqueTestBean());
        Assert.assertNull(bean.consumeUniqueTestBean());
        List<?> testBeans = bean.iterateTestBeans();
        Assert.assertEquals(2, testBeans.size());
        Assert.assertSame(bf.getBean("testBean1"), testBeans.get(0));
        Assert.assertSame(bf.getBean("testBean2"), testBeans.get(1));
        testBeans = bean.forEachTestBeans();
        Assert.assertEquals(2, testBeans.size());
        Assert.assertSame(bf.getBean("testBean1"), testBeans.get(0));
        Assert.assertSame(bf.getBean("testBean2"), testBeans.get(1));
        testBeans = bean.streamTestBeans();
        Assert.assertEquals(2, testBeans.size());
        Assert.assertSame(bf.getBean("testBean1"), testBeans.get(0));
        Assert.assertSame(bf.getBean("testBean2"), testBeans.get(1));
        testBeans = bean.sortedTestBeans();
        Assert.assertEquals(2, testBeans.size());
        Assert.assertSame(bf.getBean("testBean1"), testBeans.get(0));
        Assert.assertSame(bf.getBean("testBean2"), testBeans.get(1));
    }

    @Test
    public void testObjectProviderInjectionWithTargetPrimary() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.ObjectProviderInjectionBean.class));
        RootBeanDefinition tb1 = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.TestBeanFactory.class);
        tb1.setFactoryMethodName("newTestBean1");
        tb1.setPrimary(true);
        bf.registerBeanDefinition("testBean1", tb1);
        RootBeanDefinition tb2 = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.TestBeanFactory.class);
        tb2.setFactoryMethodName("newTestBean2");
        tb2.setLazyInit(true);
        bf.registerBeanDefinition("testBean2", tb2);
        AutowiredAnnotationBeanPostProcessorTests.ObjectProviderInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.ObjectProviderInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(bf.getBean("testBean1"), bean.getTestBean());
        Assert.assertSame(bf.getBean("testBean1"), bean.getOptionalTestBean());
        Assert.assertSame(bf.getBean("testBean1"), bean.consumeOptionalTestBean());
        Assert.assertSame(bf.getBean("testBean1"), bean.getUniqueTestBean());
        Assert.assertSame(bf.getBean("testBean1"), bean.consumeUniqueTestBean());
        Assert.assertFalse(bf.containsSingleton("testBean2"));
        List<?> testBeans = bean.iterateTestBeans();
        Assert.assertEquals(2, testBeans.size());
        Assert.assertSame(bf.getBean("testBean1"), testBeans.get(0));
        Assert.assertSame(bf.getBean("testBean2"), testBeans.get(1));
        testBeans = bean.forEachTestBeans();
        Assert.assertEquals(2, testBeans.size());
        Assert.assertSame(bf.getBean("testBean1"), testBeans.get(0));
        Assert.assertSame(bf.getBean("testBean2"), testBeans.get(1));
        testBeans = bean.streamTestBeans();
        Assert.assertEquals(2, testBeans.size());
        Assert.assertSame(bf.getBean("testBean1"), testBeans.get(0));
        Assert.assertSame(bf.getBean("testBean2"), testBeans.get(1));
        testBeans = bean.sortedTestBeans();
        Assert.assertEquals(2, testBeans.size());
        Assert.assertSame(bf.getBean("testBean2"), testBeans.get(0));
        Assert.assertSame(bf.getBean("testBean1"), testBeans.get(1));
    }

    @Test
    public void testObjectProviderInjectionWithUnresolvedOrderedStream() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.ObjectProviderInjectionBean.class));
        RootBeanDefinition tb1 = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.TestBeanFactory.class);
        tb1.setFactoryMethodName("newTestBean1");
        tb1.setPrimary(true);
        bf.registerBeanDefinition("testBean1", tb1);
        RootBeanDefinition tb2 = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.TestBeanFactory.class);
        tb2.setFactoryMethodName("newTestBean2");
        tb2.setLazyInit(true);
        bf.registerBeanDefinition("testBean2", tb2);
        AutowiredAnnotationBeanPostProcessorTests.ObjectProviderInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.ObjectProviderInjectionBean) (bf.getBean("annotatedBean")));
        List<?> testBeans = bean.sortedTestBeans();
        Assert.assertEquals(2, testBeans.size());
        Assert.assertSame(bf.getBean("testBean2"), testBeans.get(0));
        Assert.assertSame(bf.getBean("testBean1"), testBeans.get(1));
    }

    @Test
    public void testCustomAnnotationRequiredFieldResourceInjection() {
        bpp.setAutowiredAnnotationType(AutowiredAnnotationBeanPostProcessorTests.MyAutowired.class);
        bpp.setRequiredParameterName("optional");
        bpp.setRequiredParameterValue(false);
        bf.registerBeanDefinition("customBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.CustomAnnotationRequiredFieldResourceInjectionBean.class));
        TestBean tb = new TestBean();
        bf.registerSingleton("testBean", tb);
        AutowiredAnnotationBeanPostProcessorTests.CustomAnnotationRequiredFieldResourceInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.CustomAnnotationRequiredFieldResourceInjectionBean) (bf.getBean("customBean")));
        Assert.assertSame(tb, bean.getTestBean());
    }

    @Test
    public void testCustomAnnotationRequiredFieldResourceInjectionFailsWhenNoDependencyFound() {
        bpp.setAutowiredAnnotationType(AutowiredAnnotationBeanPostProcessorTests.MyAutowired.class);
        bpp.setRequiredParameterName("optional");
        bpp.setRequiredParameterValue(false);
        bf.registerBeanDefinition("customBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.CustomAnnotationRequiredFieldResourceInjectionBean.class));
        try {
            bf.getBean("customBean");
            Assert.fail("Should have thrown UnsatisfiedDependencyException");
        } catch (UnsatisfiedDependencyException ex) {
            // expected
            Assert.assertSame(AutowiredAnnotationBeanPostProcessorTests.CustomAnnotationRequiredFieldResourceInjectionBean.class, ex.getInjectionPoint().getField().getDeclaringClass());
        }
    }

    @Test
    public void testCustomAnnotationRequiredFieldResourceInjectionFailsWhenMultipleDependenciesFound() {
        bpp.setAutowiredAnnotationType(AutowiredAnnotationBeanPostProcessorTests.MyAutowired.class);
        bpp.setRequiredParameterName("optional");
        bpp.setRequiredParameterValue(false);
        bf.registerBeanDefinition("customBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.CustomAnnotationRequiredFieldResourceInjectionBean.class));
        TestBean tb1 = new TestBean();
        bf.registerSingleton("testBean1", tb1);
        TestBean tb2 = new TestBean();
        bf.registerSingleton("testBean2", tb2);
        try {
            bf.getBean("customBean");
            Assert.fail("Should have thrown UnsatisfiedDependencyException");
        } catch (UnsatisfiedDependencyException ex) {
            // expected
            Assert.assertSame(AutowiredAnnotationBeanPostProcessorTests.CustomAnnotationRequiredFieldResourceInjectionBean.class, ex.getInjectionPoint().getField().getDeclaringClass());
        }
    }

    @Test
    public void testCustomAnnotationRequiredMethodResourceInjection() {
        bpp.setAutowiredAnnotationType(AutowiredAnnotationBeanPostProcessorTests.MyAutowired.class);
        bpp.setRequiredParameterName("optional");
        bpp.setRequiredParameterValue(false);
        bf.registerBeanDefinition("customBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.CustomAnnotationRequiredMethodResourceInjectionBean.class));
        TestBean tb = new TestBean();
        bf.registerSingleton("testBean", tb);
        AutowiredAnnotationBeanPostProcessorTests.CustomAnnotationRequiredMethodResourceInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.CustomAnnotationRequiredMethodResourceInjectionBean) (bf.getBean("customBean")));
        Assert.assertSame(tb, bean.getTestBean());
    }

    @Test
    public void testCustomAnnotationRequiredMethodResourceInjectionFailsWhenNoDependencyFound() {
        bpp.setAutowiredAnnotationType(AutowiredAnnotationBeanPostProcessorTests.MyAutowired.class);
        bpp.setRequiredParameterName("optional");
        bpp.setRequiredParameterValue(false);
        bf.registerBeanDefinition("customBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.CustomAnnotationRequiredMethodResourceInjectionBean.class));
        try {
            bf.getBean("customBean");
            Assert.fail("Should have thrown UnsatisfiedDependencyException");
        } catch (UnsatisfiedDependencyException ex) {
            // expected
            Assert.assertSame(AutowiredAnnotationBeanPostProcessorTests.CustomAnnotationRequiredMethodResourceInjectionBean.class, ex.getInjectionPoint().getMethodParameter().getDeclaringClass());
        }
    }

    @Test
    public void testCustomAnnotationRequiredMethodResourceInjectionFailsWhenMultipleDependenciesFound() {
        bpp.setAutowiredAnnotationType(AutowiredAnnotationBeanPostProcessorTests.MyAutowired.class);
        bpp.setRequiredParameterName("optional");
        bpp.setRequiredParameterValue(false);
        bf.registerBeanDefinition("customBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.CustomAnnotationRequiredMethodResourceInjectionBean.class));
        TestBean tb1 = new TestBean();
        bf.registerSingleton("testBean1", tb1);
        TestBean tb2 = new TestBean();
        bf.registerSingleton("testBean2", tb2);
        try {
            bf.getBean("customBean");
            Assert.fail("Should have thrown UnsatisfiedDependencyException");
        } catch (UnsatisfiedDependencyException ex) {
            // expected
            Assert.assertSame(AutowiredAnnotationBeanPostProcessorTests.CustomAnnotationRequiredMethodResourceInjectionBean.class, ex.getInjectionPoint().getMethodParameter().getDeclaringClass());
        }
    }

    @Test
    public void testCustomAnnotationOptionalFieldResourceInjection() {
        bpp.setAutowiredAnnotationType(AutowiredAnnotationBeanPostProcessorTests.MyAutowired.class);
        bpp.setRequiredParameterName("optional");
        bpp.setRequiredParameterValue(false);
        bf.registerBeanDefinition("customBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.CustomAnnotationOptionalFieldResourceInjectionBean.class));
        TestBean tb = new TestBean();
        bf.registerSingleton("testBean", tb);
        AutowiredAnnotationBeanPostProcessorTests.CustomAnnotationOptionalFieldResourceInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.CustomAnnotationOptionalFieldResourceInjectionBean) (bf.getBean("customBean")));
        Assert.assertSame(tb, bean.getTestBean3());
        Assert.assertNull(bean.getTestBean());
        Assert.assertNull(bean.getTestBean2());
    }

    @Test
    public void testCustomAnnotationOptionalFieldResourceInjectionWhenNoDependencyFound() {
        bpp.setAutowiredAnnotationType(AutowiredAnnotationBeanPostProcessorTests.MyAutowired.class);
        bpp.setRequiredParameterName("optional");
        bpp.setRequiredParameterValue(false);
        bf.registerBeanDefinition("customBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.CustomAnnotationOptionalFieldResourceInjectionBean.class));
        AutowiredAnnotationBeanPostProcessorTests.CustomAnnotationOptionalFieldResourceInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.CustomAnnotationOptionalFieldResourceInjectionBean) (bf.getBean("customBean")));
        Assert.assertNull(bean.getTestBean3());
        Assert.assertNull(bean.getTestBean());
        Assert.assertNull(bean.getTestBean2());
    }

    @Test
    public void testCustomAnnotationOptionalFieldResourceInjectionWhenMultipleDependenciesFound() {
        bpp.setAutowiredAnnotationType(AutowiredAnnotationBeanPostProcessorTests.MyAutowired.class);
        bpp.setRequiredParameterName("optional");
        bpp.setRequiredParameterValue(false);
        bf.registerBeanDefinition("customBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.CustomAnnotationOptionalFieldResourceInjectionBean.class));
        TestBean tb1 = new TestBean();
        bf.registerSingleton("testBean1", tb1);
        TestBean tb2 = new TestBean();
        bf.registerSingleton("testBean2", tb2);
        try {
            bf.getBean("customBean");
            Assert.fail("Should have thrown UnsatisfiedDependencyException");
        } catch (UnsatisfiedDependencyException ex) {
            // expected
            Assert.assertSame(AutowiredAnnotationBeanPostProcessorTests.CustomAnnotationOptionalFieldResourceInjectionBean.class, ex.getInjectionPoint().getField().getDeclaringClass());
        }
    }

    @Test
    public void testCustomAnnotationOptionalMethodResourceInjection() {
        bpp.setAutowiredAnnotationType(AutowiredAnnotationBeanPostProcessorTests.MyAutowired.class);
        bpp.setRequiredParameterName("optional");
        bpp.setRequiredParameterValue(false);
        bf.registerBeanDefinition("customBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.CustomAnnotationOptionalMethodResourceInjectionBean.class));
        TestBean tb = new TestBean();
        bf.registerSingleton("testBean", tb);
        AutowiredAnnotationBeanPostProcessorTests.CustomAnnotationOptionalMethodResourceInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.CustomAnnotationOptionalMethodResourceInjectionBean) (bf.getBean("customBean")));
        Assert.assertSame(tb, bean.getTestBean3());
        Assert.assertNull(bean.getTestBean());
        Assert.assertNull(bean.getTestBean2());
    }

    @Test
    public void testCustomAnnotationOptionalMethodResourceInjectionWhenNoDependencyFound() {
        bpp.setAutowiredAnnotationType(AutowiredAnnotationBeanPostProcessorTests.MyAutowired.class);
        bpp.setRequiredParameterName("optional");
        bpp.setRequiredParameterValue(false);
        bf.registerBeanDefinition("customBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.CustomAnnotationOptionalMethodResourceInjectionBean.class));
        AutowiredAnnotationBeanPostProcessorTests.CustomAnnotationOptionalMethodResourceInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.CustomAnnotationOptionalMethodResourceInjectionBean) (bf.getBean("customBean")));
        Assert.assertNull(bean.getTestBean3());
        Assert.assertNull(bean.getTestBean());
        Assert.assertNull(bean.getTestBean2());
    }

    @Test
    public void testCustomAnnotationOptionalMethodResourceInjectionWhenMultipleDependenciesFound() {
        bpp.setAutowiredAnnotationType(AutowiredAnnotationBeanPostProcessorTests.MyAutowired.class);
        bpp.setRequiredParameterName("optional");
        bpp.setRequiredParameterValue(false);
        bf.registerBeanDefinition("customBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.CustomAnnotationOptionalMethodResourceInjectionBean.class));
        TestBean tb1 = new TestBean();
        bf.registerSingleton("testBean1", tb1);
        TestBean tb2 = new TestBean();
        bf.registerSingleton("testBean2", tb2);
        try {
            bf.getBean("customBean");
            Assert.fail("Should have thrown UnsatisfiedDependencyException");
        } catch (UnsatisfiedDependencyException ex) {
            // expected
            Assert.assertSame(AutowiredAnnotationBeanPostProcessorTests.CustomAnnotationOptionalMethodResourceInjectionBean.class, ex.getInjectionPoint().getMethodParameter().getDeclaringClass());
        }
    }

    /**
     * Verifies that a dependency on a {@link FactoryBean} can be autowired via
     * {@link Autowired @Autowired}, specifically addressing the JIRA issue
     * raised in <a
     * href="http://opensource.atlassian.com/projects/spring/browse/SPR-4040"
     * target="_blank">SPR-4040</a>.
     */
    @Test
    public void testBeanAutowiredWithFactoryBean() {
        bf.registerBeanDefinition("factoryBeanDependentBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.FactoryBeanDependentBean.class));
        bf.registerSingleton("stringFactoryBean", new AutowiredAnnotationBeanPostProcessorTests.StringFactoryBean());
        final AutowiredAnnotationBeanPostProcessorTests.StringFactoryBean factoryBean = ((AutowiredAnnotationBeanPostProcessorTests.StringFactoryBean) (bf.getBean("&stringFactoryBean")));
        final AutowiredAnnotationBeanPostProcessorTests.FactoryBeanDependentBean bean = ((AutowiredAnnotationBeanPostProcessorTests.FactoryBeanDependentBean) (bf.getBean("factoryBeanDependentBean")));
        Assert.assertNotNull("The singleton StringFactoryBean should have been registered.", factoryBean);
        Assert.assertNotNull("The factoryBeanDependentBean should have been registered.", bean);
        Assert.assertEquals("The FactoryBeanDependentBean should have been autowired 'by type' with the StringFactoryBean.", factoryBean, bean.getFactoryBean());
    }

    @Test
    public void testGenericsBasedFieldInjection() {
        RootBeanDefinition bd = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.RepositoryFieldInjectionBean.class);
        bd.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", bd);
        String sv = "X";
        bf.registerSingleton("stringValue", sv);
        Integer iv = 1;
        bf.registerSingleton("integerValue", iv);
        AutowiredAnnotationBeanPostProcessorTests.StringRepository sr = new AutowiredAnnotationBeanPostProcessorTests.StringRepository();
        bf.registerSingleton("stringRepo", sr);
        AutowiredAnnotationBeanPostProcessorTests.IntegerRepository ir = new AutowiredAnnotationBeanPostProcessorTests.IntegerRepository();
        bf.registerSingleton("integerRepo", ir);
        AutowiredAnnotationBeanPostProcessorTests.RepositoryFieldInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.RepositoryFieldInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(sv, bean.string);
        Assert.assertSame(iv, bean.integer);
        Assert.assertSame(1, bean.stringArray.length);
        Assert.assertSame(1, bean.integerArray.length);
        Assert.assertSame(sv, bean.stringArray[0]);
        Assert.assertSame(iv, bean.integerArray[0]);
        Assert.assertSame(1, bean.stringList.size());
        Assert.assertSame(1, bean.integerList.size());
        Assert.assertSame(sv, bean.stringList.get(0));
        Assert.assertSame(iv, bean.integerList.get(0));
        Assert.assertSame(1, bean.stringMap.size());
        Assert.assertSame(1, bean.integerMap.size());
        Assert.assertSame(sv, bean.stringMap.get("stringValue"));
        Assert.assertSame(iv, bean.integerMap.get("integerValue"));
        Assert.assertSame(sr, bean.stringRepository);
        Assert.assertSame(ir, bean.integerRepository);
        Assert.assertSame(1, bean.stringRepositoryArray.length);
        Assert.assertSame(1, bean.integerRepositoryArray.length);
        Assert.assertSame(sr, bean.stringRepositoryArray[0]);
        Assert.assertSame(ir, bean.integerRepositoryArray[0]);
        Assert.assertSame(1, bean.stringRepositoryList.size());
        Assert.assertSame(1, bean.integerRepositoryList.size());
        Assert.assertSame(sr, bean.stringRepositoryList.get(0));
        Assert.assertSame(ir, bean.integerRepositoryList.get(0));
        Assert.assertSame(1, bean.stringRepositoryMap.size());
        Assert.assertSame(1, bean.integerRepositoryMap.size());
        Assert.assertSame(sr, bean.stringRepositoryMap.get("stringRepo"));
        Assert.assertSame(ir, bean.integerRepositoryMap.get("integerRepo"));
    }

    @Test
    public void testGenericsBasedFieldInjectionWithSubstitutedVariables() {
        RootBeanDefinition bd = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.RepositoryFieldInjectionBeanWithSubstitutedVariables.class);
        bd.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", bd);
        String sv = "X";
        bf.registerSingleton("stringValue", sv);
        Integer iv = 1;
        bf.registerSingleton("integerValue", iv);
        AutowiredAnnotationBeanPostProcessorTests.StringRepository sr = new AutowiredAnnotationBeanPostProcessorTests.StringRepository();
        bf.registerSingleton("stringRepo", sr);
        AutowiredAnnotationBeanPostProcessorTests.IntegerRepository ir = new AutowiredAnnotationBeanPostProcessorTests.IntegerRepository();
        bf.registerSingleton("integerRepo", ir);
        AutowiredAnnotationBeanPostProcessorTests.RepositoryFieldInjectionBeanWithSubstitutedVariables bean = ((AutowiredAnnotationBeanPostProcessorTests.RepositoryFieldInjectionBeanWithSubstitutedVariables) (bf.getBean("annotatedBean")));
        Assert.assertSame(sv, bean.string);
        Assert.assertSame(iv, bean.integer);
        Assert.assertSame(1, bean.stringArray.length);
        Assert.assertSame(1, bean.integerArray.length);
        Assert.assertSame(sv, bean.stringArray[0]);
        Assert.assertSame(iv, bean.integerArray[0]);
        Assert.assertSame(1, bean.stringList.size());
        Assert.assertSame(1, bean.integerList.size());
        Assert.assertSame(sv, bean.stringList.get(0));
        Assert.assertSame(iv, bean.integerList.get(0));
        Assert.assertSame(1, bean.stringMap.size());
        Assert.assertSame(1, bean.integerMap.size());
        Assert.assertSame(sv, bean.stringMap.get("stringValue"));
        Assert.assertSame(iv, bean.integerMap.get("integerValue"));
        Assert.assertSame(sr, bean.stringRepository);
        Assert.assertSame(ir, bean.integerRepository);
        Assert.assertSame(1, bean.stringRepositoryArray.length);
        Assert.assertSame(1, bean.integerRepositoryArray.length);
        Assert.assertSame(sr, bean.stringRepositoryArray[0]);
        Assert.assertSame(ir, bean.integerRepositoryArray[0]);
        Assert.assertSame(1, bean.stringRepositoryList.size());
        Assert.assertSame(1, bean.integerRepositoryList.size());
        Assert.assertSame(sr, bean.stringRepositoryList.get(0));
        Assert.assertSame(ir, bean.integerRepositoryList.get(0));
        Assert.assertSame(1, bean.stringRepositoryMap.size());
        Assert.assertSame(1, bean.integerRepositoryMap.size());
        Assert.assertSame(sr, bean.stringRepositoryMap.get("stringRepo"));
        Assert.assertSame(ir, bean.integerRepositoryMap.get("integerRepo"));
    }

    @Test
    public void testGenericsBasedFieldInjectionWithQualifiers() {
        RootBeanDefinition bd = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.RepositoryFieldInjectionBeanWithQualifiers.class);
        bd.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", bd);
        AutowiredAnnotationBeanPostProcessorTests.StringRepository sr = new AutowiredAnnotationBeanPostProcessorTests.StringRepository();
        bf.registerSingleton("stringRepo", sr);
        AutowiredAnnotationBeanPostProcessorTests.IntegerRepository ir = new AutowiredAnnotationBeanPostProcessorTests.IntegerRepository();
        bf.registerSingleton("integerRepo", ir);
        AutowiredAnnotationBeanPostProcessorTests.RepositoryFieldInjectionBeanWithQualifiers bean = ((AutowiredAnnotationBeanPostProcessorTests.RepositoryFieldInjectionBeanWithQualifiers) (bf.getBean("annotatedBean")));
        Assert.assertSame(sr, bean.stringRepository);
        Assert.assertSame(ir, bean.integerRepository);
        Assert.assertSame(1, bean.stringRepositoryArray.length);
        Assert.assertSame(1, bean.integerRepositoryArray.length);
        Assert.assertSame(sr, bean.stringRepositoryArray[0]);
        Assert.assertSame(ir, bean.integerRepositoryArray[0]);
        Assert.assertSame(1, bean.stringRepositoryList.size());
        Assert.assertSame(1, bean.integerRepositoryList.size());
        Assert.assertSame(sr, bean.stringRepositoryList.get(0));
        Assert.assertSame(ir, bean.integerRepositoryList.get(0));
        Assert.assertSame(1, bean.stringRepositoryMap.size());
        Assert.assertSame(1, bean.integerRepositoryMap.size());
        Assert.assertSame(sr, bean.stringRepositoryMap.get("stringRepo"));
        Assert.assertSame(ir, bean.integerRepositoryMap.get("integerRepo"));
    }

    @Test
    public void testGenericsBasedFieldInjectionWithMocks() {
        RootBeanDefinition bd = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.RepositoryFieldInjectionBeanWithQualifiers.class);
        bd.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", bd);
        RootBeanDefinition rbd = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.MocksControl.class);
        bf.registerBeanDefinition("mocksControl", rbd);
        rbd = new RootBeanDefinition();
        rbd.setFactoryBeanName("mocksControl");
        rbd.setFactoryMethodName("createMock");
        rbd.getConstructorArgumentValues().addGenericArgumentValue(AutowiredAnnotationBeanPostProcessorTests.Repository.class);
        bf.registerBeanDefinition("stringRepo", rbd);
        rbd = new RootBeanDefinition();
        rbd.setFactoryBeanName("mocksControl");
        rbd.setFactoryMethodName("createMock");
        rbd.getConstructorArgumentValues().addGenericArgumentValue(AutowiredAnnotationBeanPostProcessorTests.Repository.class);
        rbd.setQualifiedElement(ReflectionUtils.findField(getClass(), "integerRepositoryQualifierProvider"));
        bf.registerBeanDefinition("integerRepository", rbd);// Bean name not matching qualifier

        AutowiredAnnotationBeanPostProcessorTests.RepositoryFieldInjectionBeanWithQualifiers bean = ((AutowiredAnnotationBeanPostProcessorTests.RepositoryFieldInjectionBeanWithQualifiers) (bf.getBean("annotatedBean")));
        AutowiredAnnotationBeanPostProcessorTests.Repository<?> sr = bf.getBean("stringRepo", AutowiredAnnotationBeanPostProcessorTests.Repository.class);
        AutowiredAnnotationBeanPostProcessorTests.Repository<?> ir = bf.getBean("integerRepository", AutowiredAnnotationBeanPostProcessorTests.Repository.class);
        Assert.assertSame(sr, bean.stringRepository);
        Assert.assertSame(ir, bean.integerRepository);
        Assert.assertSame(1, bean.stringRepositoryArray.length);
        Assert.assertSame(1, bean.integerRepositoryArray.length);
        Assert.assertSame(sr, bean.stringRepositoryArray[0]);
        Assert.assertSame(ir, bean.integerRepositoryArray[0]);
        Assert.assertSame(1, bean.stringRepositoryList.size());
        Assert.assertSame(1, bean.integerRepositoryList.size());
        Assert.assertSame(sr, bean.stringRepositoryList.get(0));
        Assert.assertSame(ir, bean.integerRepositoryList.get(0));
        Assert.assertSame(1, bean.stringRepositoryMap.size());
        Assert.assertSame(1, bean.integerRepositoryMap.size());
        Assert.assertSame(sr, bean.stringRepositoryMap.get("stringRepo"));
        Assert.assertSame(ir, bean.integerRepositoryMap.get("integerRepository"));
    }

    @Test
    public void testGenericsBasedFieldInjectionWithSimpleMatch() {
        RootBeanDefinition bd = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.RepositoryFieldInjectionBeanWithSimpleMatch.class);
        bd.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", bd);
        bf.registerSingleton("repo", new AutowiredAnnotationBeanPostProcessorTests.StringRepository());
        AutowiredAnnotationBeanPostProcessorTests.RepositoryFieldInjectionBeanWithSimpleMatch bean = ((AutowiredAnnotationBeanPostProcessorTests.RepositoryFieldInjectionBeanWithSimpleMatch) (bf.getBean("annotatedBean")));
        AutowiredAnnotationBeanPostProcessorTests.Repository<?> repo = bf.getBean("repo", AutowiredAnnotationBeanPostProcessorTests.Repository.class);
        Assert.assertSame(repo, bean.repository);
        Assert.assertSame(repo, bean.stringRepository);
        Assert.assertSame(1, bean.repositoryArray.length);
        Assert.assertSame(1, bean.stringRepositoryArray.length);
        Assert.assertSame(repo, bean.repositoryArray[0]);
        Assert.assertSame(repo, bean.stringRepositoryArray[0]);
        Assert.assertSame(1, bean.repositoryList.size());
        Assert.assertSame(1, bean.stringRepositoryList.size());
        Assert.assertSame(repo, bean.repositoryList.get(0));
        Assert.assertSame(repo, bean.stringRepositoryList.get(0));
        Assert.assertSame(1, bean.repositoryMap.size());
        Assert.assertSame(1, bean.stringRepositoryMap.size());
        Assert.assertSame(repo, bean.repositoryMap.get("repo"));
        Assert.assertSame(repo, bean.stringRepositoryMap.get("repo"));
        Assert.assertArrayEquals(new String[]{ "repo" }, bf.getBeanNamesForType(ResolvableType.forClassWithGenerics(AutowiredAnnotationBeanPostProcessorTests.Repository.class, String.class)));
    }

    @Test
    public void testGenericsBasedFactoryBeanInjectionWithBeanDefinition() {
        RootBeanDefinition bd = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.RepositoryFactoryBeanInjectionBean.class);
        bd.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", bd);
        bf.registerBeanDefinition("repoFactoryBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.RepositoryFactoryBean.class));
        AutowiredAnnotationBeanPostProcessorTests.RepositoryFactoryBeanInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.RepositoryFactoryBeanInjectionBean) (bf.getBean("annotatedBean")));
        AutowiredAnnotationBeanPostProcessorTests.RepositoryFactoryBean<?> repoFactoryBean = bf.getBean("&repoFactoryBean", AutowiredAnnotationBeanPostProcessorTests.RepositoryFactoryBean.class);
        Assert.assertSame(repoFactoryBean, bean.repositoryFactoryBean);
    }

    @Test
    public void testGenericsBasedFactoryBeanInjectionWithSingletonBean() {
        RootBeanDefinition bd = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.RepositoryFactoryBeanInjectionBean.class);
        bd.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", bd);
        bf.registerSingleton("repoFactoryBean", new AutowiredAnnotationBeanPostProcessorTests.RepositoryFactoryBean());
        AutowiredAnnotationBeanPostProcessorTests.RepositoryFactoryBeanInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.RepositoryFactoryBeanInjectionBean) (bf.getBean("annotatedBean")));
        AutowiredAnnotationBeanPostProcessorTests.RepositoryFactoryBean<?> repoFactoryBean = bf.getBean("&repoFactoryBean", AutowiredAnnotationBeanPostProcessorTests.RepositoryFactoryBean.class);
        Assert.assertSame(repoFactoryBean, bean.repositoryFactoryBean);
    }

    @Test
    public void testGenericsBasedFieldInjectionWithSimpleMatchAndMock() {
        RootBeanDefinition bd = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.RepositoryFieldInjectionBeanWithSimpleMatch.class);
        bd.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", bd);
        RootBeanDefinition rbd = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.MocksControl.class);
        bf.registerBeanDefinition("mocksControl", rbd);
        rbd = new RootBeanDefinition();
        rbd.setFactoryBeanName("mocksControl");
        rbd.setFactoryMethodName("createMock");
        rbd.getConstructorArgumentValues().addGenericArgumentValue(AutowiredAnnotationBeanPostProcessorTests.Repository.class);
        bf.registerBeanDefinition("repo", rbd);
        AutowiredAnnotationBeanPostProcessorTests.RepositoryFieldInjectionBeanWithSimpleMatch bean = ((AutowiredAnnotationBeanPostProcessorTests.RepositoryFieldInjectionBeanWithSimpleMatch) (bf.getBean("annotatedBean")));
        AutowiredAnnotationBeanPostProcessorTests.Repository<?> repo = bf.getBean("repo", AutowiredAnnotationBeanPostProcessorTests.Repository.class);
        Assert.assertSame(repo, bean.repository);
        Assert.assertSame(repo, bean.stringRepository);
        Assert.assertSame(1, bean.repositoryArray.length);
        Assert.assertSame(1, bean.stringRepositoryArray.length);
        Assert.assertSame(repo, bean.repositoryArray[0]);
        Assert.assertSame(repo, bean.stringRepositoryArray[0]);
        Assert.assertSame(1, bean.repositoryList.size());
        Assert.assertSame(1, bean.stringRepositoryList.size());
        Assert.assertSame(repo, bean.repositoryList.get(0));
        Assert.assertSame(repo, bean.stringRepositoryList.get(0));
        Assert.assertSame(1, bean.repositoryMap.size());
        Assert.assertSame(1, bean.stringRepositoryMap.size());
        Assert.assertSame(repo, bean.repositoryMap.get("repo"));
        Assert.assertSame(repo, bean.stringRepositoryMap.get("repo"));
    }

    @Test
    public void testGenericsBasedFieldInjectionWithSimpleMatchAndMockito() {
        RootBeanDefinition bd = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.RepositoryFieldInjectionBeanWithSimpleMatch.class);
        bd.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", bd);
        RootBeanDefinition rbd = new RootBeanDefinition();
        rbd.setBeanClassName(Mockito.class.getName());
        rbd.setFactoryMethodName("mock");
        // TypedStringValue used to be equivalent to an XML-defined argument String
        rbd.getConstructorArgumentValues().addGenericArgumentValue(new TypedStringValue(AutowiredAnnotationBeanPostProcessorTests.Repository.class.getName()));
        bf.registerBeanDefinition("repo", rbd);
        AutowiredAnnotationBeanPostProcessorTests.RepositoryFieldInjectionBeanWithSimpleMatch bean = ((AutowiredAnnotationBeanPostProcessorTests.RepositoryFieldInjectionBeanWithSimpleMatch) (bf.getBean("annotatedBean")));
        AutowiredAnnotationBeanPostProcessorTests.Repository<?> repo = bf.getBean("repo", AutowiredAnnotationBeanPostProcessorTests.Repository.class);
        Assert.assertSame(repo, bean.repository);
        Assert.assertSame(repo, bean.stringRepository);
        Assert.assertSame(1, bean.repositoryArray.length);
        Assert.assertSame(1, bean.stringRepositoryArray.length);
        Assert.assertSame(repo, bean.repositoryArray[0]);
        Assert.assertSame(repo, bean.stringRepositoryArray[0]);
        Assert.assertSame(1, bean.repositoryList.size());
        Assert.assertSame(1, bean.stringRepositoryList.size());
        Assert.assertSame(repo, bean.repositoryList.get(0));
        Assert.assertSame(repo, bean.stringRepositoryList.get(0));
        Assert.assertSame(1, bean.repositoryMap.size());
        Assert.assertSame(1, bean.stringRepositoryMap.size());
        Assert.assertSame(repo, bean.repositoryMap.get("repo"));
        Assert.assertSame(repo, bean.stringRepositoryMap.get("repo"));
    }

    @Test
    public void testGenericsBasedMethodInjection() {
        RootBeanDefinition bd = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.RepositoryMethodInjectionBean.class);
        bd.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", bd);
        String sv = "X";
        bf.registerSingleton("stringValue", sv);
        Integer iv = 1;
        bf.registerSingleton("integerValue", iv);
        AutowiredAnnotationBeanPostProcessorTests.StringRepository sr = new AutowiredAnnotationBeanPostProcessorTests.StringRepository();
        bf.registerSingleton("stringRepo", sr);
        AutowiredAnnotationBeanPostProcessorTests.IntegerRepository ir = new AutowiredAnnotationBeanPostProcessorTests.IntegerRepository();
        bf.registerSingleton("integerRepo", ir);
        AutowiredAnnotationBeanPostProcessorTests.RepositoryMethodInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.RepositoryMethodInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(sv, bean.string);
        Assert.assertSame(iv, bean.integer);
        Assert.assertSame(1, bean.stringArray.length);
        Assert.assertSame(1, bean.integerArray.length);
        Assert.assertSame(sv, bean.stringArray[0]);
        Assert.assertSame(iv, bean.integerArray[0]);
        Assert.assertSame(1, bean.stringList.size());
        Assert.assertSame(1, bean.integerList.size());
        Assert.assertSame(sv, bean.stringList.get(0));
        Assert.assertSame(iv, bean.integerList.get(0));
        Assert.assertSame(1, bean.stringMap.size());
        Assert.assertSame(1, bean.integerMap.size());
        Assert.assertSame(sv, bean.stringMap.get("stringValue"));
        Assert.assertSame(iv, bean.integerMap.get("integerValue"));
        Assert.assertSame(sr, bean.stringRepository);
        Assert.assertSame(ir, bean.integerRepository);
        Assert.assertSame(1, bean.stringRepositoryArray.length);
        Assert.assertSame(1, bean.integerRepositoryArray.length);
        Assert.assertSame(sr, bean.stringRepositoryArray[0]);
        Assert.assertSame(ir, bean.integerRepositoryArray[0]);
        Assert.assertSame(1, bean.stringRepositoryList.size());
        Assert.assertSame(1, bean.integerRepositoryList.size());
        Assert.assertSame(sr, bean.stringRepositoryList.get(0));
        Assert.assertSame(ir, bean.integerRepositoryList.get(0));
        Assert.assertSame(1, bean.stringRepositoryMap.size());
        Assert.assertSame(1, bean.integerRepositoryMap.size());
        Assert.assertSame(sr, bean.stringRepositoryMap.get("stringRepo"));
        Assert.assertSame(ir, bean.integerRepositoryMap.get("integerRepo"));
    }

    @Test
    public void testGenericsBasedMethodInjectionWithSubstitutedVariables() {
        RootBeanDefinition bd = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.RepositoryMethodInjectionBeanWithSubstitutedVariables.class);
        bd.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", bd);
        String sv = "X";
        bf.registerSingleton("stringValue", sv);
        Integer iv = 1;
        bf.registerSingleton("integerValue", iv);
        AutowiredAnnotationBeanPostProcessorTests.StringRepository sr = new AutowiredAnnotationBeanPostProcessorTests.StringRepository();
        bf.registerSingleton("stringRepo", sr);
        AutowiredAnnotationBeanPostProcessorTests.IntegerRepository ir = new AutowiredAnnotationBeanPostProcessorTests.IntegerRepository();
        bf.registerSingleton("integerRepo", ir);
        AutowiredAnnotationBeanPostProcessorTests.RepositoryMethodInjectionBeanWithSubstitutedVariables bean = ((AutowiredAnnotationBeanPostProcessorTests.RepositoryMethodInjectionBeanWithSubstitutedVariables) (bf.getBean("annotatedBean")));
        Assert.assertSame(sv, bean.string);
        Assert.assertSame(iv, bean.integer);
        Assert.assertSame(1, bean.stringArray.length);
        Assert.assertSame(1, bean.integerArray.length);
        Assert.assertSame(sv, bean.stringArray[0]);
        Assert.assertSame(iv, bean.integerArray[0]);
        Assert.assertSame(1, bean.stringList.size());
        Assert.assertSame(1, bean.integerList.size());
        Assert.assertSame(sv, bean.stringList.get(0));
        Assert.assertSame(iv, bean.integerList.get(0));
        Assert.assertSame(1, bean.stringMap.size());
        Assert.assertSame(1, bean.integerMap.size());
        Assert.assertSame(sv, bean.stringMap.get("stringValue"));
        Assert.assertSame(iv, bean.integerMap.get("integerValue"));
        Assert.assertSame(sr, bean.stringRepository);
        Assert.assertSame(ir, bean.integerRepository);
        Assert.assertSame(1, bean.stringRepositoryArray.length);
        Assert.assertSame(1, bean.integerRepositoryArray.length);
        Assert.assertSame(sr, bean.stringRepositoryArray[0]);
        Assert.assertSame(ir, bean.integerRepositoryArray[0]);
        Assert.assertSame(1, bean.stringRepositoryList.size());
        Assert.assertSame(1, bean.integerRepositoryList.size());
        Assert.assertSame(sr, bean.stringRepositoryList.get(0));
        Assert.assertSame(ir, bean.integerRepositoryList.get(0));
        Assert.assertSame(1, bean.stringRepositoryMap.size());
        Assert.assertSame(1, bean.integerRepositoryMap.size());
        Assert.assertSame(sr, bean.stringRepositoryMap.get("stringRepo"));
        Assert.assertSame(ir, bean.integerRepositoryMap.get("integerRepo"));
    }

    @Test
    public void testGenericsBasedConstructorInjection() {
        RootBeanDefinition bd = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.RepositoryConstructorInjectionBean.class);
        bd.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", bd);
        AutowiredAnnotationBeanPostProcessorTests.StringRepository sr = new AutowiredAnnotationBeanPostProcessorTests.StringRepository();
        bf.registerSingleton("stringRepo", sr);
        AutowiredAnnotationBeanPostProcessorTests.IntegerRepository ir = new AutowiredAnnotationBeanPostProcessorTests.IntegerRepository();
        bf.registerSingleton("integerRepo", ir);
        AutowiredAnnotationBeanPostProcessorTests.RepositoryConstructorInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.RepositoryConstructorInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(sr, bean.stringRepository);
        Assert.assertSame(ir, bean.integerRepository);
        Assert.assertSame(1, bean.stringRepositoryArray.length);
        Assert.assertSame(1, bean.integerRepositoryArray.length);
        Assert.assertSame(sr, bean.stringRepositoryArray[0]);
        Assert.assertSame(ir, bean.integerRepositoryArray[0]);
        Assert.assertSame(1, bean.stringRepositoryList.size());
        Assert.assertSame(1, bean.integerRepositoryList.size());
        Assert.assertSame(sr, bean.stringRepositoryList.get(0));
        Assert.assertSame(ir, bean.integerRepositoryList.get(0));
        Assert.assertSame(1, bean.stringRepositoryMap.size());
        Assert.assertSame(1, bean.integerRepositoryMap.size());
        Assert.assertSame(sr, bean.stringRepositoryMap.get("stringRepo"));
        Assert.assertSame(ir, bean.integerRepositoryMap.get("integerRepo"));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testGenericsBasedConstructorInjectionWithNonTypedTarget() {
        RootBeanDefinition bd = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.RepositoryConstructorInjectionBean.class);
        bd.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", bd);
        AutowiredAnnotationBeanPostProcessorTests.GenericRepository gr = new AutowiredAnnotationBeanPostProcessorTests.GenericRepository();
        bf.registerSingleton("genericRepo", gr);
        AutowiredAnnotationBeanPostProcessorTests.RepositoryConstructorInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.RepositoryConstructorInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(gr, bean.stringRepository);
        Assert.assertSame(gr, bean.integerRepository);
        Assert.assertSame(1, bean.stringRepositoryArray.length);
        Assert.assertSame(1, bean.integerRepositoryArray.length);
        Assert.assertSame(gr, bean.stringRepositoryArray[0]);
        Assert.assertSame(gr, bean.integerRepositoryArray[0]);
        Assert.assertSame(1, bean.stringRepositoryList.size());
        Assert.assertSame(1, bean.integerRepositoryList.size());
        Assert.assertSame(gr, bean.stringRepositoryList.get(0));
        Assert.assertSame(gr, bean.integerRepositoryList.get(0));
        Assert.assertSame(1, bean.stringRepositoryMap.size());
        Assert.assertSame(1, bean.integerRepositoryMap.size());
        Assert.assertSame(gr, bean.stringRepositoryMap.get("genericRepo"));
        Assert.assertSame(gr, bean.integerRepositoryMap.get("genericRepo"));
    }

    @Test
    public void testGenericsBasedConstructorInjectionWithNonGenericTarget() {
        RootBeanDefinition bd = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.RepositoryConstructorInjectionBean.class);
        bd.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", bd);
        AutowiredAnnotationBeanPostProcessorTests.SimpleRepository ngr = new AutowiredAnnotationBeanPostProcessorTests.SimpleRepository();
        bf.registerSingleton("simpleRepo", ngr);
        AutowiredAnnotationBeanPostProcessorTests.RepositoryConstructorInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.RepositoryConstructorInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(ngr, bean.stringRepository);
        Assert.assertSame(ngr, bean.integerRepository);
        Assert.assertSame(1, bean.stringRepositoryArray.length);
        Assert.assertSame(1, bean.integerRepositoryArray.length);
        Assert.assertSame(ngr, bean.stringRepositoryArray[0]);
        Assert.assertSame(ngr, bean.integerRepositoryArray[0]);
        Assert.assertSame(1, bean.stringRepositoryList.size());
        Assert.assertSame(1, bean.integerRepositoryList.size());
        Assert.assertSame(ngr, bean.stringRepositoryList.get(0));
        Assert.assertSame(ngr, bean.integerRepositoryList.get(0));
        Assert.assertSame(1, bean.stringRepositoryMap.size());
        Assert.assertSame(1, bean.integerRepositoryMap.size());
        Assert.assertSame(ngr, bean.stringRepositoryMap.get("simpleRepo"));
        Assert.assertSame(ngr, bean.integerRepositoryMap.get("simpleRepo"));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testGenericsBasedConstructorInjectionWithMixedTargets() {
        RootBeanDefinition bd = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.RepositoryConstructorInjectionBean.class);
        bd.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", bd);
        AutowiredAnnotationBeanPostProcessorTests.StringRepository sr = new AutowiredAnnotationBeanPostProcessorTests.StringRepository();
        bf.registerSingleton("stringRepo", sr);
        AutowiredAnnotationBeanPostProcessorTests.GenericRepository gr = new AutowiredAnnotationBeanPostProcessorTests.GenericRepositorySubclass();
        bf.registerSingleton("genericRepo", gr);
        AutowiredAnnotationBeanPostProcessorTests.RepositoryConstructorInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.RepositoryConstructorInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(sr, bean.stringRepository);
        Assert.assertSame(gr, bean.integerRepository);
        Assert.assertSame(1, bean.stringRepositoryArray.length);
        Assert.assertSame(1, bean.integerRepositoryArray.length);
        Assert.assertSame(sr, bean.stringRepositoryArray[0]);
        Assert.assertSame(gr, bean.integerRepositoryArray[0]);
        Assert.assertSame(1, bean.stringRepositoryList.size());
        Assert.assertSame(1, bean.integerRepositoryList.size());
        Assert.assertSame(sr, bean.stringRepositoryList.get(0));
        Assert.assertSame(gr, bean.integerRepositoryList.get(0));
        Assert.assertSame(1, bean.stringRepositoryMap.size());
        Assert.assertSame(1, bean.integerRepositoryMap.size());
        Assert.assertSame(sr, bean.stringRepositoryMap.get("stringRepo"));
        Assert.assertSame(gr, bean.integerRepositoryMap.get("genericRepo"));
    }

    @Test
    public void testGenericsBasedConstructorInjectionWithMixedTargetsIncludingNonGeneric() {
        RootBeanDefinition bd = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.RepositoryConstructorInjectionBean.class);
        bd.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", bd);
        AutowiredAnnotationBeanPostProcessorTests.StringRepository sr = new AutowiredAnnotationBeanPostProcessorTests.StringRepository();
        bf.registerSingleton("stringRepo", sr);
        AutowiredAnnotationBeanPostProcessorTests.SimpleRepository ngr = new AutowiredAnnotationBeanPostProcessorTests.SimpleRepositorySubclass();
        bf.registerSingleton("simpleRepo", ngr);
        AutowiredAnnotationBeanPostProcessorTests.RepositoryConstructorInjectionBean bean = ((AutowiredAnnotationBeanPostProcessorTests.RepositoryConstructorInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(sr, bean.stringRepository);
        Assert.assertSame(ngr, bean.integerRepository);
        Assert.assertSame(1, bean.stringRepositoryArray.length);
        Assert.assertSame(1, bean.integerRepositoryArray.length);
        Assert.assertSame(sr, bean.stringRepositoryArray[0]);
        Assert.assertSame(ngr, bean.integerRepositoryArray[0]);
        Assert.assertSame(1, bean.stringRepositoryList.size());
        Assert.assertSame(1, bean.integerRepositoryList.size());
        Assert.assertSame(sr, bean.stringRepositoryList.get(0));
        Assert.assertSame(ngr, bean.integerRepositoryList.get(0));
        Assert.assertSame(1, bean.stringRepositoryMap.size());
        Assert.assertSame(1, bean.integerRepositoryMap.size());
        Assert.assertSame(sr, bean.stringRepositoryMap.get("stringRepo"));
        Assert.assertSame(ngr, bean.integerRepositoryMap.get("simpleRepo"));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testGenericsBasedInjectionIntoMatchingTypeVariable() {
        RootBeanDefinition bd = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.GenericInterface1Impl.class);
        bd.setFactoryMethodName("create");
        bf.registerBeanDefinition("bean1", bd);
        bf.registerBeanDefinition("bean2", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.GenericInterface2Impl.class));
        AutowiredAnnotationBeanPostProcessorTests.GenericInterface1Impl bean1 = ((AutowiredAnnotationBeanPostProcessorTests.GenericInterface1Impl) (bf.getBean("bean1")));
        AutowiredAnnotationBeanPostProcessorTests.GenericInterface2Impl bean2 = ((AutowiredAnnotationBeanPostProcessorTests.GenericInterface2Impl) (bf.getBean("bean2")));
        Assert.assertSame(bean2, bean1.gi2);
        Assert.assertEquals(ResolvableType.forClass(AutowiredAnnotationBeanPostProcessorTests.GenericInterface1Impl.class), bd.getResolvableType());
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testGenericsBasedInjectionIntoUnresolvedTypeVariable() {
        RootBeanDefinition bd = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.GenericInterface1Impl.class);
        bd.setFactoryMethodName("createPlain");
        bf.registerBeanDefinition("bean1", bd);
        bf.registerBeanDefinition("bean2", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.GenericInterface2Impl.class));
        AutowiredAnnotationBeanPostProcessorTests.GenericInterface1Impl bean1 = ((AutowiredAnnotationBeanPostProcessorTests.GenericInterface1Impl) (bf.getBean("bean1")));
        AutowiredAnnotationBeanPostProcessorTests.GenericInterface2Impl bean2 = ((AutowiredAnnotationBeanPostProcessorTests.GenericInterface2Impl) (bf.getBean("bean2")));
        Assert.assertSame(bean2, bean1.gi2);
        Assert.assertEquals(ResolvableType.forClass(AutowiredAnnotationBeanPostProcessorTests.GenericInterface1Impl.class), bd.getResolvableType());
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testGenericsBasedInjectionIntoTypeVariableSelectingBestMatch() {
        RootBeanDefinition bd = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.GenericInterface1Impl.class);
        bd.setFactoryMethodName("create");
        bf.registerBeanDefinition("bean1", bd);
        bf.registerBeanDefinition("bean2", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.GenericInterface2Impl.class));
        bf.registerBeanDefinition("bean2a", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.ReallyGenericInterface2Impl.class));
        bf.registerBeanDefinition("bean2b", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.PlainGenericInterface2Impl.class));
        AutowiredAnnotationBeanPostProcessorTests.GenericInterface1Impl bean1 = ((AutowiredAnnotationBeanPostProcessorTests.GenericInterface1Impl) (bf.getBean("bean1")));
        AutowiredAnnotationBeanPostProcessorTests.GenericInterface2Impl bean2 = ((AutowiredAnnotationBeanPostProcessorTests.GenericInterface2Impl) (bf.getBean("bean2")));
        Assert.assertSame(bean2, bean1.gi2);
        Assert.assertArrayEquals(new String[]{ "bean1" }, bf.getBeanNamesForType(ResolvableType.forClassWithGenerics(AutowiredAnnotationBeanPostProcessorTests.GenericInterface1.class, String.class)));
        Assert.assertArrayEquals(new String[]{ "bean2" }, bf.getBeanNamesForType(ResolvableType.forClassWithGenerics(AutowiredAnnotationBeanPostProcessorTests.GenericInterface2.class, String.class)));
    }

    @Test
    public void testGenericsBasedInjectionWithBeanDefinitionTargetResolvableType() {
        RootBeanDefinition bd1 = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.GenericInterface2Bean.class);
        bd1.setTargetType(ResolvableType.forClassWithGenerics(AutowiredAnnotationBeanPostProcessorTests.GenericInterface2Bean.class, String.class));
        bf.registerBeanDefinition("bean1", bd1);
        RootBeanDefinition bd2 = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.GenericInterface2Bean.class);
        bd2.setTargetType(ResolvableType.forClassWithGenerics(AutowiredAnnotationBeanPostProcessorTests.GenericInterface2Bean.class, Integer.class));
        bf.registerBeanDefinition("bean2", bd2);
        bf.registerBeanDefinition("bean3", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.MultiGenericFieldInjection.class));
        Assert.assertEquals("bean1 a bean2 123", bf.getBean("bean3").toString());
        Assert.assertEquals(ResolvableType.forClassWithGenerics(AutowiredAnnotationBeanPostProcessorTests.GenericInterface2Bean.class, String.class), bd1.getResolvableType());
        Assert.assertEquals(ResolvableType.forClassWithGenerics(AutowiredAnnotationBeanPostProcessorTests.GenericInterface2Bean.class, Integer.class), bd2.getResolvableType());
    }

    @Test
    public void testCircularTypeReference() {
        bf.registerBeanDefinition("bean1", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.StockServiceImpl.class));
        bf.registerBeanDefinition("bean2", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.StockMovementDaoImpl.class));
        bf.registerBeanDefinition("bean3", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.StockMovementImpl.class));
        bf.registerBeanDefinition("bean4", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.StockMovementInstructionImpl.class));
        AutowiredAnnotationBeanPostProcessorTests.StockServiceImpl service = bf.getBean(AutowiredAnnotationBeanPostProcessorTests.StockServiceImpl.class);
        Assert.assertSame(bf.getBean(AutowiredAnnotationBeanPostProcessorTests.StockMovementDaoImpl.class), service.stockMovementDao);
    }

    @Test
    public void testBridgeMethodHandling() {
        bf.registerBeanDefinition("bean1", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.MyCallable.class));
        bf.registerBeanDefinition("bean2", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.SecondCallable.class));
        bf.registerBeanDefinition("bean3", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.FooBar.class));
        Assert.assertNotNull(bf.getBean(AutowiredAnnotationBeanPostProcessorTests.FooBar.class));
    }

    @Test
    public void testSingleConstructorWithProvidedArgument() {
        RootBeanDefinition bd = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.ProvidedArgumentBean.class);
        bd.getConstructorArgumentValues().addGenericArgumentValue(Collections.singletonList("value"));
        bf.registerBeanDefinition("beanWithArgs", bd);
        Assert.assertNotNull(bf.getBean(AutowiredAnnotationBeanPostProcessorTests.ProvidedArgumentBean.class));
    }

    @Test
    public void testAnnotatedDefaultConstructor() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.AnnotatedDefaultConstructorBean.class));
        Assert.assertNotNull(bf.getBean("annotatedBean"));
    }

    // SPR-15125
    @Test
    public void testFactoryBeanSelfInjection() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.SelfInjectingFactoryBean.class));
        AutowiredAnnotationBeanPostProcessorTests.SelfInjectingFactoryBean bean = bf.getBean(AutowiredAnnotationBeanPostProcessorTests.SelfInjectingFactoryBean.class);
        Assert.assertSame(bf.getBean("annotatedBean"), bean.testBean);
    }

    // SPR-15125
    @Test
    public void testFactoryBeanSelfInjectionViaFactoryMethod() {
        RootBeanDefinition bd = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessorTests.SelfInjectingFactoryBean.class);
        bd.setFactoryMethodName("create");
        bf.registerBeanDefinition("annotatedBean", bd);
        AutowiredAnnotationBeanPostProcessorTests.SelfInjectingFactoryBean bean = bf.getBean(AutowiredAnnotationBeanPostProcessorTests.SelfInjectingFactoryBean.class);
        Assert.assertSame(bf.getBean("annotatedBean"), bean.testBean);
    }

    @Qualifier("integerRepo")
    private AutowiredAnnotationBeanPostProcessorTests.Repository<?> integerRepositoryQualifierProvider;

    public static class ResourceInjectionBean {
        @Autowired(required = false)
        private TestBean testBean;

        private TestBean testBean2;

        @Autowired
        public void setTestBean2(TestBean testBean2) {
            if ((this.testBean2) != null) {
                throw new IllegalStateException("Already called");
            }
            this.testBean2 = testBean2;
        }

        public TestBean getTestBean() {
            return this.testBean;
        }

        public TestBean getTestBean2() {
            return this.testBean2;
        }
    }

    static class NonPublicResourceInjectionBean<T> extends AutowiredAnnotationBeanPostProcessorTests.ResourceInjectionBean {
        @Autowired
        public final ITestBean testBean3 = null;

        private T nestedTestBean;

        private ITestBean testBean4;

        protected BeanFactory beanFactory;

        public boolean baseInjected = false;

        public NonPublicResourceInjectionBean() {
        }

        @Override
        @Autowired
        @Required
        @SuppressWarnings("deprecation")
        public void setTestBean2(TestBean testBean2) {
            super.setTestBean2(testBean2);
        }

        @Autowired
        private void inject(ITestBean testBean4, T nestedTestBean) {
            this.testBean4 = testBean4;
            this.nestedTestBean = nestedTestBean;
        }

        @Autowired
        private void inject(ITestBean testBean4) {
            this.baseInjected = true;
        }

        @Autowired
        protected void initBeanFactory(BeanFactory beanFactory) {
            this.beanFactory = beanFactory;
        }

        public ITestBean getTestBean3() {
            return this.testBean3;
        }

        public ITestBean getTestBean4() {
            return this.testBean4;
        }

        public T getNestedTestBean() {
            return this.nestedTestBean;
        }

        public BeanFactory getBeanFactory() {
            return this.beanFactory;
        }
    }

    public static class TypedExtendedResourceInjectionBean extends AutowiredAnnotationBeanPostProcessorTests.NonPublicResourceInjectionBean<NestedTestBean> implements DisposableBean {
        public boolean destroyed = false;

        @Override
        public void destroy() {
            this.destroyed = true;
        }
    }

    public static class OverriddenExtendedResourceInjectionBean extends AutowiredAnnotationBeanPostProcessorTests.NonPublicResourceInjectionBean<NestedTestBean> {
        public boolean subInjected = false;

        @Override
        public void setTestBean2(TestBean testBean2) {
            super.setTestBean2(testBean2);
        }

        @Override
        protected void initBeanFactory(BeanFactory beanFactory) {
            this.beanFactory = beanFactory;
        }

        @Autowired
        private void inject(ITestBean testBean4) {
            this.subInjected = true;
        }
    }

    public interface InterfaceWithDefaultMethod {
        @Autowired
        void setTestBean2(TestBean testBean2);

        @Autowired
        default void injectDefault(ITestBean testBean4) {
            markSubInjected();
        }

        void markSubInjected();
    }

    public static class DefaultMethodResourceInjectionBean extends AutowiredAnnotationBeanPostProcessorTests.NonPublicResourceInjectionBean<NestedTestBean> implements AutowiredAnnotationBeanPostProcessorTests.InterfaceWithDefaultMethod {
        public boolean subInjected = false;

        @Override
        public void setTestBean2(TestBean testBean2) {
            super.setTestBean2(testBean2);
        }

        @Override
        protected void initBeanFactory(BeanFactory beanFactory) {
            this.beanFactory = beanFactory;
        }

        @Override
        public void markSubInjected() {
            subInjected = true;
        }
    }

    public static class OptionalResourceInjectionBean extends AutowiredAnnotationBeanPostProcessorTests.ResourceInjectionBean {
        @Autowired(required = false)
        protected ITestBean testBean3;

        private IndexedTestBean indexedTestBean;

        private NestedTestBean[] nestedTestBeans;

        @Autowired(required = false)
        public NestedTestBean[] nestedTestBeansField;

        private ITestBean testBean4;

        @Override
        @Autowired(required = false)
        public void setTestBean2(TestBean testBean2) {
            super.setTestBean2(testBean2);
        }

        @Autowired(required = false)
        private void inject(ITestBean testBean4, NestedTestBean[] nestedTestBeans, IndexedTestBean indexedTestBean) {
            this.testBean4 = testBean4;
            this.indexedTestBean = indexedTestBean;
            this.nestedTestBeans = nestedTestBeans;
        }

        public ITestBean getTestBean3() {
            return this.testBean3;
        }

        public ITestBean getTestBean4() {
            return this.testBean4;
        }

        public IndexedTestBean getIndexedTestBean() {
            return this.indexedTestBean;
        }

        public NestedTestBean[] getNestedTestBeans() {
            return this.nestedTestBeans;
        }
    }

    public static class OptionalCollectionResourceInjectionBean extends AutowiredAnnotationBeanPostProcessorTests.ResourceInjectionBean {
        @Autowired(required = false)
        protected ITestBean testBean3;

        private IndexedTestBean indexedTestBean;

        private List<NestedTestBean> nestedTestBeans;

        public List<NestedTestBean> nestedTestBeansSetter;

        @Autowired(required = false)
        public List<NestedTestBean> nestedTestBeansField;

        private ITestBean testBean4;

        @Override
        @Autowired(required = false)
        public void setTestBean2(TestBean testBean2) {
            super.setTestBean2(testBean2);
        }

        @Autowired(required = false)
        private void inject(ITestBean testBean4, List<NestedTestBean> nestedTestBeans, IndexedTestBean indexedTestBean) {
            this.testBean4 = testBean4;
            this.indexedTestBean = indexedTestBean;
            this.nestedTestBeans = nestedTestBeans;
        }

        @Autowired(required = false)
        public void setNestedTestBeans(List<NestedTestBean> nestedTestBeans) {
            this.nestedTestBeansSetter = nestedTestBeans;
        }

        public ITestBean getTestBean3() {
            return this.testBean3;
        }

        public ITestBean getTestBean4() {
            return this.testBean4;
        }

        public IndexedTestBean getIndexedTestBean() {
            return this.indexedTestBean;
        }

        public List<NestedTestBean> getNestedTestBeans() {
            return this.nestedTestBeans;
        }
    }

    public static class ConstructorResourceInjectionBean extends AutowiredAnnotationBeanPostProcessorTests.ResourceInjectionBean {
        @Autowired(required = false)
        protected ITestBean testBean3;

        private ITestBean testBean4;

        private NestedTestBean nestedTestBean;

        private ConfigurableListableBeanFactory beanFactory;

        public ConstructorResourceInjectionBean() {
            throw new UnsupportedOperationException();
        }

        public ConstructorResourceInjectionBean(ITestBean testBean3) {
            throw new UnsupportedOperationException();
        }

        @Autowired
        public ConstructorResourceInjectionBean(@Autowired(required = false)
        ITestBean testBean4, @Autowired(required = false)
        NestedTestBean nestedTestBean, ConfigurableListableBeanFactory beanFactory) {
            this.testBean4 = testBean4;
            this.nestedTestBean = nestedTestBean;
            this.beanFactory = beanFactory;
        }

        public ConstructorResourceInjectionBean(NestedTestBean nestedTestBean) {
            throw new UnsupportedOperationException();
        }

        public ConstructorResourceInjectionBean(ITestBean testBean3, ITestBean testBean4, NestedTestBean nestedTestBean) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Autowired(required = false)
        public void setTestBean2(TestBean testBean2) {
            super.setTestBean2(testBean2);
        }

        public ITestBean getTestBean3() {
            return this.testBean3;
        }

        public ITestBean getTestBean4() {
            return this.testBean4;
        }

        public NestedTestBean getNestedTestBean() {
            return this.nestedTestBean;
        }

        public ConfigurableListableBeanFactory getBeanFactory() {
            return this.beanFactory;
        }
    }

    public static class ConstructorsResourceInjectionBean {
        protected ITestBean testBean3;

        private ITestBean testBean4;

        private NestedTestBean[] nestedTestBeans;

        public ConstructorsResourceInjectionBean() {
        }

        @Autowired(required = false)
        public ConstructorsResourceInjectionBean(ITestBean testBean3) {
            this.testBean3 = testBean3;
        }

        @Autowired(required = false)
        public ConstructorsResourceInjectionBean(ITestBean testBean4, NestedTestBean[] nestedTestBeans) {
            this.testBean4 = testBean4;
            this.nestedTestBeans = nestedTestBeans;
        }

        public ConstructorsResourceInjectionBean(NestedTestBean nestedTestBean) {
            throw new UnsupportedOperationException();
        }

        public ConstructorsResourceInjectionBean(ITestBean testBean3, ITestBean testBean4, NestedTestBean nestedTestBean) {
            throw new UnsupportedOperationException();
        }

        public ITestBean getTestBean3() {
            return this.testBean3;
        }

        public ITestBean getTestBean4() {
            return this.testBean4;
        }

        public NestedTestBean[] getNestedTestBeans() {
            return this.nestedTestBeans;
        }
    }

    public static class ConstructorWithoutFallbackBean {
        protected ITestBean testBean3;

        @Autowired(required = false)
        public ConstructorWithoutFallbackBean(ITestBean testBean3) {
            this.testBean3 = testBean3;
        }

        public ITestBean getTestBean3() {
            return this.testBean3;
        }
    }

    public static class ConstructorsCollectionResourceInjectionBean {
        protected ITestBean testBean3;

        private ITestBean testBean4;

        private List<NestedTestBean> nestedTestBeans;

        public ConstructorsCollectionResourceInjectionBean() {
        }

        @Autowired(required = false)
        public ConstructorsCollectionResourceInjectionBean(ITestBean testBean3) {
            this.testBean3 = testBean3;
        }

        @Autowired(required = false)
        public ConstructorsCollectionResourceInjectionBean(ITestBean testBean4, List<NestedTestBean> nestedTestBeans) {
            this.testBean4 = testBean4;
            this.nestedTestBeans = nestedTestBeans;
        }

        public ConstructorsCollectionResourceInjectionBean(NestedTestBean nestedTestBean) {
            throw new UnsupportedOperationException();
        }

        public ConstructorsCollectionResourceInjectionBean(ITestBean testBean3, ITestBean testBean4, NestedTestBean nestedTestBean) {
            throw new UnsupportedOperationException();
        }

        public ITestBean getTestBean3() {
            return this.testBean3;
        }

        public ITestBean getTestBean4() {
            return this.testBean4;
        }

        public List<NestedTestBean> getNestedTestBeans() {
            return this.nestedTestBeans;
        }
    }

    public static class SingleConstructorVarargBean {
        private ITestBean testBean;

        private List<NestedTestBean> nestedTestBeans;

        public SingleConstructorVarargBean(ITestBean testBean, NestedTestBean... nestedTestBeans) {
            this.testBean = testBean;
            this.nestedTestBeans = Arrays.asList(nestedTestBeans);
        }

        public ITestBean getTestBean() {
            return this.testBean;
        }

        public List<NestedTestBean> getNestedTestBeans() {
            return this.nestedTestBeans;
        }
    }

    public static class SingleConstructorRequiredCollectionBean {
        private ITestBean testBean;

        private List<NestedTestBean> nestedTestBeans;

        public SingleConstructorRequiredCollectionBean(ITestBean testBean, List<NestedTestBean> nestedTestBeans) {
            this.testBean = testBean;
            this.nestedTestBeans = nestedTestBeans;
        }

        public ITestBean getTestBean() {
            return this.testBean;
        }

        public List<NestedTestBean> getNestedTestBeans() {
            return this.nestedTestBeans;
        }
    }

    public static class SingleConstructorOptionalCollectionBean {
        private ITestBean testBean;

        private List<NestedTestBean> nestedTestBeans;

        public SingleConstructorOptionalCollectionBean(ITestBean testBean, @Autowired(required = false)
        List<NestedTestBean> nestedTestBeans) {
            this.testBean = testBean;
            this.nestedTestBeans = nestedTestBeans;
        }

        public ITestBean getTestBean() {
            return this.testBean;
        }

        public List<NestedTestBean> getNestedTestBeans() {
            return this.nestedTestBeans;
        }
    }

    @SuppressWarnings("serial")
    public static class MyTestBeanMap extends LinkedHashMap<String, TestBean> {}

    @SuppressWarnings("serial")
    public static class MyTestBeanSet extends LinkedHashSet<TestBean> {}

    public static class MapConstructorInjectionBean {
        private Map<String, TestBean> testBeanMap;

        @Autowired
        public MapConstructorInjectionBean(Map<String, TestBean> testBeanMap) {
            this.testBeanMap = testBeanMap;
        }

        public Map<String, TestBean> getTestBeanMap() {
            return this.testBeanMap;
        }
    }

    public static class QualifiedMapConstructorInjectionBean {
        private Map<String, TestBean> testBeanMap;

        @Autowired
        public QualifiedMapConstructorInjectionBean(@Qualifier("myTestBeanMap")
        Map<String, TestBean> testBeanMap) {
            this.testBeanMap = testBeanMap;
        }

        public Map<String, TestBean> getTestBeanMap() {
            return this.testBeanMap;
        }
    }

    public static class SetConstructorInjectionBean {
        private Set<TestBean> testBeanSet;

        @Autowired
        public SetConstructorInjectionBean(Set<TestBean> testBeanSet) {
            this.testBeanSet = testBeanSet;
        }

        public Set<TestBean> getTestBeanSet() {
            return this.testBeanSet;
        }
    }

    public static class SelfInjectionBean {
        @Autowired
        public AutowiredAnnotationBeanPostProcessorTests.SelfInjectionBean reference;

        @Autowired(required = false)
        public List<AutowiredAnnotationBeanPostProcessorTests.SelfInjectionBean> referenceCollection;
    }

    @SuppressWarnings("serial")
    public static class SelfInjectionCollectionBean extends LinkedList<AutowiredAnnotationBeanPostProcessorTests.SelfInjectionCollectionBean> {
        @Autowired
        public AutowiredAnnotationBeanPostProcessorTests.SelfInjectionCollectionBean reference;

        @Autowired(required = false)
        public List<AutowiredAnnotationBeanPostProcessorTests.SelfInjectionCollectionBean> referenceCollection;
    }

    public static class MapFieldInjectionBean {
        @Autowired
        private Map<String, TestBean> testBeanMap;

        public Map<String, TestBean> getTestBeanMap() {
            return this.testBeanMap;
        }
    }

    public static class MapMethodInjectionBean {
        private TestBean testBean;

        private Map<String, TestBean> testBeanMap;

        @Autowired(required = false)
        public void setTestBeanMap(TestBean testBean, Map<String, TestBean> testBeanMap) {
            this.testBean = testBean;
            this.testBeanMap = testBeanMap;
        }

        public TestBean getTestBean() {
            return this.testBean;
        }

        public Map<String, TestBean> getTestBeanMap() {
            return this.testBeanMap;
        }
    }

    @SuppressWarnings("serial")
    public static class ObjectFactoryFieldInjectionBean implements Serializable {
        @Autowired
        private ObjectFactory<TestBean> testBeanFactory;

        public TestBean getTestBean() {
            return this.testBeanFactory.getObject();
        }
    }

    @SuppressWarnings("serial")
    public static class ObjectFactoryConstructorInjectionBean implements Serializable {
        private final ObjectFactory<TestBean> testBeanFactory;

        public ObjectFactoryConstructorInjectionBean(ObjectFactory<TestBean> testBeanFactory) {
            this.testBeanFactory = testBeanFactory;
        }

        public TestBean getTestBean() {
            return this.testBeanFactory.getObject();
        }
    }

    public static class ObjectFactoryQualifierInjectionBean {
        @Autowired
        @Qualifier("testBean")
        private ObjectFactory<?> testBeanFactory;

        public TestBean getTestBean() {
            return ((TestBean) (this.testBeanFactory.getObject()));
        }
    }

    public static class ObjectProviderInjectionBean {
        @Autowired
        private ObjectProvider<TestBean> testBeanProvider;

        private TestBean consumedTestBean;

        public TestBean getTestBean() {
            return this.testBeanProvider.getObject();
        }

        public TestBean getTestBean(String name) {
            return this.testBeanProvider.getObject(name);
        }

        public TestBean getOptionalTestBean() {
            return this.testBeanProvider.getIfAvailable();
        }

        public TestBean getOptionalTestBeanWithDefault() {
            return this.testBeanProvider.getIfAvailable(() -> new TestBean("default"));
        }

        public TestBean consumeOptionalTestBean() {
            this.testBeanProvider.ifAvailable(( tb) -> consumedTestBean = tb);
            return consumedTestBean;
        }

        public TestBean getUniqueTestBean() {
            return this.testBeanProvider.getIfUnique();
        }

        public TestBean getUniqueTestBeanWithDefault() {
            return this.testBeanProvider.getIfUnique(() -> new TestBean("default"));
        }

        public TestBean consumeUniqueTestBean() {
            this.testBeanProvider.ifUnique(( tb) -> consumedTestBean = tb);
            return consumedTestBean;
        }

        public List<TestBean> iterateTestBeans() {
            List<TestBean> resolved = new LinkedList<>();
            for (TestBean tb : this.testBeanProvider) {
                resolved.add(tb);
            }
            return resolved;
        }

        public List<TestBean> forEachTestBeans() {
            List<TestBean> resolved = new LinkedList<>();
            this.testBeanProvider.forEach(resolved::add);
            return resolved;
        }

        public List<TestBean> streamTestBeans() {
            return this.testBeanProvider.stream().collect(Collectors.toList());
        }

        public List<TestBean> sortedTestBeans() {
            return this.testBeanProvider.orderedStream().collect(Collectors.toList());
        }
    }

    public static class CustomAnnotationRequiredFieldResourceInjectionBean {
        @AutowiredAnnotationBeanPostProcessorTests.MyAutowired(optional = false)
        private TestBean testBean;

        public TestBean getTestBean() {
            return this.testBean;
        }
    }

    public static class CustomAnnotationRequiredMethodResourceInjectionBean {
        private TestBean testBean;

        @AutowiredAnnotationBeanPostProcessorTests.MyAutowired(optional = false)
        public void setTestBean(TestBean testBean) {
            this.testBean = testBean;
        }

        public TestBean getTestBean() {
            return this.testBean;
        }
    }

    public static class CustomAnnotationOptionalFieldResourceInjectionBean extends AutowiredAnnotationBeanPostProcessorTests.ResourceInjectionBean {
        @AutowiredAnnotationBeanPostProcessorTests.MyAutowired(optional = true)
        private TestBean testBean3;

        public TestBean getTestBean3() {
            return this.testBean3;
        }
    }

    public static class CustomAnnotationOptionalMethodResourceInjectionBean extends AutowiredAnnotationBeanPostProcessorTests.ResourceInjectionBean {
        private TestBean testBean3;

        @AutowiredAnnotationBeanPostProcessorTests.MyAutowired(optional = true)
        protected void setTestBean3(TestBean testBean3) {
            this.testBean3 = testBean3;
        }

        public TestBean getTestBean3() {
            return this.testBean3;
        }
    }

    @Target({ ElementType.METHOD, ElementType.FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MyAutowired {
        boolean optional() default false;
    }

    /**
     * Bean with a dependency on a {@link FactoryBean}.
     */
    private static class FactoryBeanDependentBean {
        @Autowired
        private FactoryBean<?> factoryBean;

        public final FactoryBean<?> getFactoryBean() {
            return this.factoryBean;
        }
    }

    public static class StringFactoryBean implements FactoryBean<String> {
        @Override
        public String getObject() throws Exception {
            return "";
        }

        @Override
        public Class<String> getObjectType() {
            return String.class;
        }

        @Override
        public boolean isSingleton() {
            return true;
        }
    }

    public static class OrderedNestedTestBean extends NestedTestBean implements Ordered {
        private int order;

        public void setOrder(int order) {
            this.order = order;
        }

        @Override
        public int getOrder() {
            return this.order;
        }
    }

    @Order(1)
    public static class FixedOrder1NestedTestBean extends NestedTestBean {}

    @Order(2)
    public static class FixedOrder2NestedTestBean extends NestedTestBean {}

    public interface Repository<T> {}

    public static class StringRepository implements AutowiredAnnotationBeanPostProcessorTests.Repository<String> {}

    public static class IntegerRepository implements AutowiredAnnotationBeanPostProcessorTests.Repository<Integer> {}

    public static class GenericRepository<T> implements AutowiredAnnotationBeanPostProcessorTests.Repository<T> {}

    @SuppressWarnings("rawtypes")
    public static class GenericRepositorySubclass extends AutowiredAnnotationBeanPostProcessorTests.GenericRepository {}

    @SuppressWarnings("rawtypes")
    public static class SimpleRepository implements AutowiredAnnotationBeanPostProcessorTests.Repository {}

    public static class SimpleRepositorySubclass extends AutowiredAnnotationBeanPostProcessorTests.SimpleRepository {}

    public static class RepositoryFactoryBean<T> implements FactoryBean<T> {
        @Override
        public T getObject() {
            throw new IllegalStateException();
        }

        @Override
        public Class<?> getObjectType() {
            return Object.class;
        }

        @Override
        public boolean isSingleton() {
            return false;
        }
    }

    public static class RepositoryFieldInjectionBean {
        @Autowired
        public String string;

        @Autowired
        public Integer integer;

        @Autowired
        public String[] stringArray;

        @Autowired
        public Integer[] integerArray;

        @Autowired
        public List<String> stringList;

        @Autowired
        public List<Integer> integerList;

        @Autowired
        public Map<String, String> stringMap;

        @Autowired
        public Map<String, Integer> integerMap;

        @Autowired
        public AutowiredAnnotationBeanPostProcessorTests.Repository<String> stringRepository;

        @Autowired
        public AutowiredAnnotationBeanPostProcessorTests.Repository<Integer> integerRepository;

        @Autowired
        public AutowiredAnnotationBeanPostProcessorTests.Repository<String>[] stringRepositoryArray;

        @Autowired
        public AutowiredAnnotationBeanPostProcessorTests.Repository<Integer>[] integerRepositoryArray;

        @Autowired
        public List<AutowiredAnnotationBeanPostProcessorTests.Repository<String>> stringRepositoryList;

        @Autowired
        public List<AutowiredAnnotationBeanPostProcessorTests.Repository<Integer>> integerRepositoryList;

        @Autowired
        public Map<String, AutowiredAnnotationBeanPostProcessorTests.Repository<String>> stringRepositoryMap;

        @Autowired
        public Map<String, AutowiredAnnotationBeanPostProcessorTests.Repository<Integer>> integerRepositoryMap;
    }

    public static class RepositoryFieldInjectionBeanWithVariables<S, I> {
        @Autowired
        public S string;

        @Autowired
        public I integer;

        @Autowired
        public S[] stringArray;

        @Autowired
        public I[] integerArray;

        @Autowired
        public List<S> stringList;

        @Autowired
        public List<I> integerList;

        @Autowired
        public Map<String, S> stringMap;

        @Autowired
        public Map<String, I> integerMap;

        @Autowired
        public AutowiredAnnotationBeanPostProcessorTests.Repository<S> stringRepository;

        @Autowired
        public AutowiredAnnotationBeanPostProcessorTests.Repository<I> integerRepository;

        @Autowired
        public AutowiredAnnotationBeanPostProcessorTests.Repository<S>[] stringRepositoryArray;

        @Autowired
        public AutowiredAnnotationBeanPostProcessorTests.Repository<I>[] integerRepositoryArray;

        @Autowired
        public List<AutowiredAnnotationBeanPostProcessorTests.Repository<S>> stringRepositoryList;

        @Autowired
        public List<AutowiredAnnotationBeanPostProcessorTests.Repository<I>> integerRepositoryList;

        @Autowired
        public Map<String, AutowiredAnnotationBeanPostProcessorTests.Repository<S>> stringRepositoryMap;

        @Autowired
        public Map<String, AutowiredAnnotationBeanPostProcessorTests.Repository<I>> integerRepositoryMap;
    }

    public static class RepositoryFieldInjectionBeanWithSubstitutedVariables extends AutowiredAnnotationBeanPostProcessorTests.RepositoryFieldInjectionBeanWithVariables<String, Integer> {}

    public static class RepositoryFieldInjectionBeanWithQualifiers {
        @Autowired
        @Qualifier("stringRepo")
        public AutowiredAnnotationBeanPostProcessorTests.Repository<?> stringRepository;

        @Autowired
        @Qualifier("integerRepo")
        public AutowiredAnnotationBeanPostProcessorTests.Repository<?> integerRepository;

        @Autowired
        @Qualifier("stringRepo")
        public AutowiredAnnotationBeanPostProcessorTests.Repository<?>[] stringRepositoryArray;

        @Autowired
        @Qualifier("integerRepo")
        public AutowiredAnnotationBeanPostProcessorTests.Repository<?>[] integerRepositoryArray;

        @Autowired
        @Qualifier("stringRepo")
        public List<AutowiredAnnotationBeanPostProcessorTests.Repository<?>> stringRepositoryList;

        @Autowired
        @Qualifier("integerRepo")
        public List<AutowiredAnnotationBeanPostProcessorTests.Repository<?>> integerRepositoryList;

        @Autowired
        @Qualifier("stringRepo")
        public Map<String, AutowiredAnnotationBeanPostProcessorTests.Repository<?>> stringRepositoryMap;

        @Autowired
        @Qualifier("integerRepo")
        public Map<String, AutowiredAnnotationBeanPostProcessorTests.Repository<?>> integerRepositoryMap;
    }

    public static class RepositoryFieldInjectionBeanWithSimpleMatch {
        @Autowired
        public AutowiredAnnotationBeanPostProcessorTests.Repository<?> repository;

        @Autowired
        public AutowiredAnnotationBeanPostProcessorTests.Repository<String> stringRepository;

        @Autowired
        public AutowiredAnnotationBeanPostProcessorTests.Repository<?>[] repositoryArray;

        @Autowired
        public AutowiredAnnotationBeanPostProcessorTests.Repository<String>[] stringRepositoryArray;

        @Autowired
        public List<AutowiredAnnotationBeanPostProcessorTests.Repository<?>> repositoryList;

        @Autowired
        public List<AutowiredAnnotationBeanPostProcessorTests.Repository<String>> stringRepositoryList;

        @Autowired
        public Map<String, AutowiredAnnotationBeanPostProcessorTests.Repository<?>> repositoryMap;

        @Autowired
        public Map<String, AutowiredAnnotationBeanPostProcessorTests.Repository<String>> stringRepositoryMap;
    }

    public static class RepositoryFactoryBeanInjectionBean {
        @Autowired
        public AutowiredAnnotationBeanPostProcessorTests.RepositoryFactoryBean<?> repositoryFactoryBean;
    }

    public static class RepositoryMethodInjectionBean {
        public String string;

        public Integer integer;

        public String[] stringArray;

        public Integer[] integerArray;

        public List<String> stringList;

        public List<Integer> integerList;

        public Map<String, String> stringMap;

        public Map<String, Integer> integerMap;

        public AutowiredAnnotationBeanPostProcessorTests.Repository<String> stringRepository;

        public AutowiredAnnotationBeanPostProcessorTests.Repository<Integer> integerRepository;

        public AutowiredAnnotationBeanPostProcessorTests.Repository<String>[] stringRepositoryArray;

        public AutowiredAnnotationBeanPostProcessorTests.Repository<Integer>[] integerRepositoryArray;

        public List<AutowiredAnnotationBeanPostProcessorTests.Repository<String>> stringRepositoryList;

        public List<AutowiredAnnotationBeanPostProcessorTests.Repository<Integer>> integerRepositoryList;

        public Map<String, AutowiredAnnotationBeanPostProcessorTests.Repository<String>> stringRepositoryMap;

        public Map<String, AutowiredAnnotationBeanPostProcessorTests.Repository<Integer>> integerRepositoryMap;

        @Autowired
        public void setString(String string) {
            this.string = string;
        }

        @Autowired
        public void setInteger(Integer integer) {
            this.integer = integer;
        }

        @Autowired
        public void setStringArray(String[] stringArray) {
            this.stringArray = stringArray;
        }

        @Autowired
        public void setIntegerArray(Integer[] integerArray) {
            this.integerArray = integerArray;
        }

        @Autowired
        public void setStringList(List<String> stringList) {
            this.stringList = stringList;
        }

        @Autowired
        public void setIntegerList(List<Integer> integerList) {
            this.integerList = integerList;
        }

        @Autowired
        public void setStringMap(Map<String, String> stringMap) {
            this.stringMap = stringMap;
        }

        @Autowired
        public void setIntegerMap(Map<String, Integer> integerMap) {
            this.integerMap = integerMap;
        }

        @Autowired
        public void setStringRepository(AutowiredAnnotationBeanPostProcessorTests.Repository<String> stringRepository) {
            this.stringRepository = stringRepository;
        }

        @Autowired
        public void setIntegerRepository(AutowiredAnnotationBeanPostProcessorTests.Repository<Integer> integerRepository) {
            this.integerRepository = integerRepository;
        }

        @Autowired
        public void setStringRepositoryArray(AutowiredAnnotationBeanPostProcessorTests.Repository<String>[] stringRepositoryArray) {
            this.stringRepositoryArray = stringRepositoryArray;
        }

        @Autowired
        public void setIntegerRepositoryArray(AutowiredAnnotationBeanPostProcessorTests.Repository<Integer>[] integerRepositoryArray) {
            this.integerRepositoryArray = integerRepositoryArray;
        }

        @Autowired
        public void setStringRepositoryList(List<AutowiredAnnotationBeanPostProcessorTests.Repository<String>> stringRepositoryList) {
            this.stringRepositoryList = stringRepositoryList;
        }

        @Autowired
        public void setIntegerRepositoryList(List<AutowiredAnnotationBeanPostProcessorTests.Repository<Integer>> integerRepositoryList) {
            this.integerRepositoryList = integerRepositoryList;
        }

        @Autowired
        public void setStringRepositoryMap(Map<String, AutowiredAnnotationBeanPostProcessorTests.Repository<String>> stringRepositoryMap) {
            this.stringRepositoryMap = stringRepositoryMap;
        }

        @Autowired
        public void setIntegerRepositoryMap(Map<String, AutowiredAnnotationBeanPostProcessorTests.Repository<Integer>> integerRepositoryMap) {
            this.integerRepositoryMap = integerRepositoryMap;
        }
    }

    public static class RepositoryMethodInjectionBeanWithVariables<S, I> {
        public S string;

        public I integer;

        public S[] stringArray;

        public I[] integerArray;

        public List<S> stringList;

        public List<I> integerList;

        public Map<String, S> stringMap;

        public Map<String, I> integerMap;

        public AutowiredAnnotationBeanPostProcessorTests.Repository<S> stringRepository;

        public AutowiredAnnotationBeanPostProcessorTests.Repository<I> integerRepository;

        public AutowiredAnnotationBeanPostProcessorTests.Repository<S>[] stringRepositoryArray;

        public AutowiredAnnotationBeanPostProcessorTests.Repository<I>[] integerRepositoryArray;

        public List<AutowiredAnnotationBeanPostProcessorTests.Repository<S>> stringRepositoryList;

        public List<AutowiredAnnotationBeanPostProcessorTests.Repository<I>> integerRepositoryList;

        public Map<String, AutowiredAnnotationBeanPostProcessorTests.Repository<S>> stringRepositoryMap;

        public Map<String, AutowiredAnnotationBeanPostProcessorTests.Repository<I>> integerRepositoryMap;

        @Autowired
        public void setString(S string) {
            this.string = string;
        }

        @Autowired
        public void setInteger(I integer) {
            this.integer = integer;
        }

        @Autowired
        public void setStringArray(S[] stringArray) {
            this.stringArray = stringArray;
        }

        @Autowired
        public void setIntegerArray(I[] integerArray) {
            this.integerArray = integerArray;
        }

        @Autowired
        public void setStringList(List<S> stringList) {
            this.stringList = stringList;
        }

        @Autowired
        public void setIntegerList(List<I> integerList) {
            this.integerList = integerList;
        }

        @Autowired
        public void setStringMap(Map<String, S> stringMap) {
            this.stringMap = stringMap;
        }

        @Autowired
        public void setIntegerMap(Map<String, I> integerMap) {
            this.integerMap = integerMap;
        }

        @Autowired
        public void setStringRepository(AutowiredAnnotationBeanPostProcessorTests.Repository<S> stringRepository) {
            this.stringRepository = stringRepository;
        }

        @Autowired
        public void setIntegerRepository(AutowiredAnnotationBeanPostProcessorTests.Repository<I> integerRepository) {
            this.integerRepository = integerRepository;
        }

        @Autowired
        public void setStringRepositoryArray(AutowiredAnnotationBeanPostProcessorTests.Repository<S>[] stringRepositoryArray) {
            this.stringRepositoryArray = stringRepositoryArray;
        }

        @Autowired
        public void setIntegerRepositoryArray(AutowiredAnnotationBeanPostProcessorTests.Repository<I>[] integerRepositoryArray) {
            this.integerRepositoryArray = integerRepositoryArray;
        }

        @Autowired
        public void setStringRepositoryList(List<AutowiredAnnotationBeanPostProcessorTests.Repository<S>> stringRepositoryList) {
            this.stringRepositoryList = stringRepositoryList;
        }

        @Autowired
        public void setIntegerRepositoryList(List<AutowiredAnnotationBeanPostProcessorTests.Repository<I>> integerRepositoryList) {
            this.integerRepositoryList = integerRepositoryList;
        }

        @Autowired
        public void setStringRepositoryMap(Map<String, AutowiredAnnotationBeanPostProcessorTests.Repository<S>> stringRepositoryMap) {
            this.stringRepositoryMap = stringRepositoryMap;
        }

        @Autowired
        public void setIntegerRepositoryMap(Map<String, AutowiredAnnotationBeanPostProcessorTests.Repository<I>> integerRepositoryMap) {
            this.integerRepositoryMap = integerRepositoryMap;
        }
    }

    public static class RepositoryMethodInjectionBeanWithSubstitutedVariables extends AutowiredAnnotationBeanPostProcessorTests.RepositoryMethodInjectionBeanWithVariables<String, Integer> {}

    public static class RepositoryConstructorInjectionBean {
        public AutowiredAnnotationBeanPostProcessorTests.Repository<String> stringRepository;

        public AutowiredAnnotationBeanPostProcessorTests.Repository<Integer> integerRepository;

        public AutowiredAnnotationBeanPostProcessorTests.Repository<String>[] stringRepositoryArray;

        public AutowiredAnnotationBeanPostProcessorTests.Repository<Integer>[] integerRepositoryArray;

        public List<AutowiredAnnotationBeanPostProcessorTests.Repository<String>> stringRepositoryList;

        public List<AutowiredAnnotationBeanPostProcessorTests.Repository<Integer>> integerRepositoryList;

        public Map<String, AutowiredAnnotationBeanPostProcessorTests.Repository<String>> stringRepositoryMap;

        public Map<String, AutowiredAnnotationBeanPostProcessorTests.Repository<Integer>> integerRepositoryMap;

        @Autowired
        public RepositoryConstructorInjectionBean(AutowiredAnnotationBeanPostProcessorTests.Repository<String> stringRepository, AutowiredAnnotationBeanPostProcessorTests.Repository<Integer> integerRepository, AutowiredAnnotationBeanPostProcessorTests.Repository<String>[] stringRepositoryArray, AutowiredAnnotationBeanPostProcessorTests.Repository<Integer>[] integerRepositoryArray, List<AutowiredAnnotationBeanPostProcessorTests.Repository<String>> stringRepositoryList, List<AutowiredAnnotationBeanPostProcessorTests.Repository<Integer>> integerRepositoryList, Map<String, AutowiredAnnotationBeanPostProcessorTests.Repository<String>> stringRepositoryMap, Map<String, AutowiredAnnotationBeanPostProcessorTests.Repository<Integer>> integerRepositoryMap) {
            this.stringRepository = stringRepository;
            this.integerRepository = integerRepository;
            this.stringRepositoryArray = stringRepositoryArray;
            this.integerRepositoryArray = integerRepositoryArray;
            this.stringRepositoryList = stringRepositoryList;
            this.integerRepositoryList = integerRepositoryList;
            this.stringRepositoryMap = stringRepositoryMap;
            this.integerRepositoryMap = integerRepositoryMap;
        }
    }

    /**
     * Pseudo-implementation of EasyMock's {@code MocksControl} class.
     */
    public static class MocksControl {
        @SuppressWarnings("unchecked")
        public <T> T createMock(Class<T> toMock) {
            return ((T) (Proxy.newProxyInstance(AutowiredAnnotationBeanPostProcessorTests.class.getClassLoader(), new Class<?>[]{ toMock }, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    throw new UnsupportedOperationException("mocked!");
                }
            })));
        }
    }

    public interface GenericInterface1<T> {
        String doSomethingGeneric(T o);
    }

    public static class GenericInterface1Impl<T> implements AutowiredAnnotationBeanPostProcessorTests.GenericInterface1<T> {
        @Autowired
        private AutowiredAnnotationBeanPostProcessorTests.GenericInterface2<T> gi2;

        @Override
        public String doSomethingGeneric(T o) {
            return ((gi2.doSomethingMoreGeneric(o)) + "_somethingGeneric_") + o;
        }

        public static AutowiredAnnotationBeanPostProcessorTests.GenericInterface1<String> create() {
            return new AutowiredAnnotationBeanPostProcessorTests.StringGenericInterface1Impl();
        }

        public static AutowiredAnnotationBeanPostProcessorTests.GenericInterface1<String> createErased() {
            return new AutowiredAnnotationBeanPostProcessorTests.GenericInterface1Impl<>();
        }

        @SuppressWarnings("rawtypes")
        public static AutowiredAnnotationBeanPostProcessorTests.GenericInterface1 createPlain() {
            return new AutowiredAnnotationBeanPostProcessorTests.GenericInterface1Impl();
        }
    }

    public static class StringGenericInterface1Impl extends AutowiredAnnotationBeanPostProcessorTests.GenericInterface1Impl<String> {}

    public interface GenericInterface2<K> {
        String doSomethingMoreGeneric(K o);
    }

    public static class GenericInterface2Impl implements AutowiredAnnotationBeanPostProcessorTests.GenericInterface2<String> {
        @Override
        public String doSomethingMoreGeneric(String o) {
            return "somethingMoreGeneric_" + o;
        }
    }

    public static class ReallyGenericInterface2Impl implements AutowiredAnnotationBeanPostProcessorTests.GenericInterface2<Object> {
        @Override
        public String doSomethingMoreGeneric(Object o) {
            return "somethingMoreGeneric_" + o;
        }
    }

    public static class GenericInterface2Bean<K> implements BeanNameAware , AutowiredAnnotationBeanPostProcessorTests.GenericInterface2<K> {
        private String name;

        @Override
        public void setBeanName(String name) {
            this.name = name;
        }

        @Override
        public String doSomethingMoreGeneric(K o) {
            return ((this.name) + " ") + o;
        }
    }

    public static class MultiGenericFieldInjection {
        @Autowired
        private AutowiredAnnotationBeanPostProcessorTests.GenericInterface2<String> stringBean;

        @Autowired
        private AutowiredAnnotationBeanPostProcessorTests.GenericInterface2<Integer> integerBean;

        @Override
        public String toString() {
            return ((this.stringBean.doSomethingMoreGeneric("a")) + " ") + (this.integerBean.doSomethingMoreGeneric(123));
        }
    }

    @SuppressWarnings("rawtypes")
    public static class PlainGenericInterface2Impl implements AutowiredAnnotationBeanPostProcessorTests.GenericInterface2 {
        @Override
        public String doSomethingMoreGeneric(Object o) {
            return "somethingMoreGeneric_" + o;
        }
    }

    @SuppressWarnings("rawtypes")
    public interface StockMovement<P extends AutowiredAnnotationBeanPostProcessorTests.StockMovementInstruction> {}

    @SuppressWarnings("rawtypes")
    public interface StockMovementInstruction<C extends AutowiredAnnotationBeanPostProcessorTests.StockMovement> {}

    @SuppressWarnings("rawtypes")
    public interface StockMovementDao<S extends AutowiredAnnotationBeanPostProcessorTests.StockMovement> {}

    @SuppressWarnings("rawtypes")
    public static class StockMovementImpl<P extends AutowiredAnnotationBeanPostProcessorTests.StockMovementInstruction> implements AutowiredAnnotationBeanPostProcessorTests.StockMovement<P> {}

    @SuppressWarnings("rawtypes")
    public static class StockMovementInstructionImpl<C extends AutowiredAnnotationBeanPostProcessorTests.StockMovement> implements AutowiredAnnotationBeanPostProcessorTests.StockMovementInstruction<C> {}

    @SuppressWarnings("rawtypes")
    public static class StockMovementDaoImpl<E extends AutowiredAnnotationBeanPostProcessorTests.StockMovement> implements AutowiredAnnotationBeanPostProcessorTests.StockMovementDao<E> {}

    public static class StockServiceImpl {
        @Autowired
        @SuppressWarnings("rawtypes")
        private AutowiredAnnotationBeanPostProcessorTests.StockMovementDao<AutowiredAnnotationBeanPostProcessorTests.StockMovement> stockMovementDao;
    }

    public static class MyCallable implements Callable<Thread> {
        @Override
        public Thread call() throws Exception {
            return null;
        }
    }

    public static class SecondCallable implements Callable<Thread> {
        @Override
        public Thread call() throws Exception {
            return null;
        }
    }

    public abstract static class Foo<T extends Runnable, RT extends Callable<T>> {
        private RT obj;

        protected void setObj(RT obj) {
            if ((this.obj) != null) {
                throw new IllegalStateException("Already called");
            }
            this.obj = obj;
        }
    }

    public static class FooBar extends AutowiredAnnotationBeanPostProcessorTests.Foo<Thread, AutowiredAnnotationBeanPostProcessorTests.MyCallable> {
        @Override
        @Autowired
        public void setObj(AutowiredAnnotationBeanPostProcessorTests.MyCallable obj) {
            super.setObj(obj);
        }
    }

    public static class NullNestedTestBeanFactoryBean implements FactoryBean<NestedTestBean> {
        @Override
        public NestedTestBean getObject() {
            return null;
        }

        @Override
        public Class<?> getObjectType() {
            return NestedTestBean.class;
        }

        @Override
        public boolean isSingleton() {
            return true;
        }
    }

    public static class NullFactoryMethods {
        public static TestBean createTestBean() {
            return null;
        }

        public static NestedTestBean createNestedTestBean() {
            return null;
        }
    }

    public static class ProvidedArgumentBean {
        public ProvidedArgumentBean(String[] args) {
        }
    }

    public static class CollectionFactoryMethods {
        public static Map<String, TestBean> testBeanMap() {
            Map<String, TestBean> tbm = new LinkedHashMap<>();
            tbm.put("testBean1", new TestBean("tb1"));
            tbm.put("testBean2", new TestBean("tb2"));
            return tbm;
        }

        public static Set<TestBean> testBeanSet() {
            Set<TestBean> tbs = new LinkedHashSet<>();
            tbs.add(new TestBean("tb1"));
            tbs.add(new TestBean("tb2"));
            return tbs;
        }
    }

    public static class CustomCollectionFactoryMethods {
        public static AutowiredAnnotationBeanPostProcessorTests.CustomMap<String, TestBean> testBeanMap() {
            AutowiredAnnotationBeanPostProcessorTests.CustomMap<String, TestBean> tbm = new AutowiredAnnotationBeanPostProcessorTests.CustomHashMap<>();
            tbm.put("testBean1", new TestBean("tb1"));
            tbm.put("testBean2", new TestBean("tb2"));
            return tbm;
        }

        public static AutowiredAnnotationBeanPostProcessorTests.CustomSet<TestBean> testBeanSet() {
            AutowiredAnnotationBeanPostProcessorTests.CustomSet<TestBean> tbs = new AutowiredAnnotationBeanPostProcessorTests.CustomHashSet<>();
            tbs.add(new TestBean("tb1"));
            tbs.add(new TestBean("tb2"));
            return tbs;
        }
    }

    public static class CustomMapConstructorInjectionBean {
        private AutowiredAnnotationBeanPostProcessorTests.CustomMap<String, TestBean> testBeanMap;

        @Autowired
        public CustomMapConstructorInjectionBean(AutowiredAnnotationBeanPostProcessorTests.CustomMap<String, TestBean> testBeanMap) {
            this.testBeanMap = testBeanMap;
        }

        public AutowiredAnnotationBeanPostProcessorTests.CustomMap<String, TestBean> getTestBeanMap() {
            return this.testBeanMap;
        }
    }

    public static class CustomSetConstructorInjectionBean {
        private AutowiredAnnotationBeanPostProcessorTests.CustomSet<TestBean> testBeanSet;

        @Autowired
        public CustomSetConstructorInjectionBean(AutowiredAnnotationBeanPostProcessorTests.CustomSet<TestBean> testBeanSet) {
            this.testBeanSet = testBeanSet;
        }

        public AutowiredAnnotationBeanPostProcessorTests.CustomSet<TestBean> getTestBeanSet() {
            return this.testBeanSet;
        }
    }

    public interface CustomMap<K, V> extends Map<K, V> {}

    @SuppressWarnings("serial")
    public static class CustomHashMap<K, V> extends LinkedHashMap<K, V> implements AutowiredAnnotationBeanPostProcessorTests.CustomMap<K, V> {}

    public interface CustomSet<E> extends Set<E> {}

    @SuppressWarnings("serial")
    public static class CustomHashSet<E> extends LinkedHashSet<E> implements AutowiredAnnotationBeanPostProcessorTests.CustomSet<E> {}

    public static class AnnotatedDefaultConstructorBean {
        @Autowired
        public AnnotatedDefaultConstructorBean() {
        }
    }

    public static class SelfInjectingFactoryBean implements FactoryBean<TestBean> {
        private final TestBean exposedTestBean = new TestBean();

        @Autowired
        TestBean testBean;

        @Override
        public TestBean getObject() {
            return exposedTestBean;
        }

        @Override
        public Class<?> getObjectType() {
            return TestBean.class;
        }

        @Override
        public boolean isSingleton() {
            return true;
        }

        public static AutowiredAnnotationBeanPostProcessorTests.SelfInjectingFactoryBean create() {
            return new AutowiredAnnotationBeanPostProcessorTests.SelfInjectingFactoryBean();
        }
    }

    public static class TestBeanFactory {
        @Order(1)
        public static TestBean newTestBean1() {
            return new TestBean();
        }

        @Order(0)
        public static TestBean newTestBean2() {
            return new TestBean();
        }
    }
}

