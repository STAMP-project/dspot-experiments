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
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.tests.sample.beans.ITestBean;
import org.springframework.tests.sample.beans.IndexedTestBean;
import org.springframework.tests.sample.beans.NestedTestBean;
import org.springframework.tests.sample.beans.TestBean;
import org.springframework.util.SerializationTestUtils;


/**
 * Unit tests for {@link org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor}
 * processing the JSR-330 {@link javax.inject.Inject} annotation.
 *
 * @author Juergen Hoeller
 * @since 3.0
 */
public class InjectAnnotationBeanPostProcessorTests {
    private DefaultListableBeanFactory bf;

    private AutowiredAnnotationBeanPostProcessor bpp;

    @Test
    public void testIncompleteBeanDefinition() {
        bf.registerBeanDefinition("testBean", new GenericBeanDefinition());
        try {
            bf.getBean("testBean");
        } catch (BeanCreationException ex) {
            Assert.assertTrue(((ex.getRootCause()) instanceof IllegalStateException));
        }
    }

    @Test
    public void testResourceInjection() {
        RootBeanDefinition bd = new RootBeanDefinition(InjectAnnotationBeanPostProcessorTests.ResourceInjectionBean.class);
        bd.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", bd);
        TestBean tb = new TestBean();
        bf.registerSingleton("testBean", tb);
        InjectAnnotationBeanPostProcessorTests.ResourceInjectionBean bean = ((InjectAnnotationBeanPostProcessorTests.ResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(tb, bean.getTestBean());
        Assert.assertSame(tb, bean.getTestBean2());
        bean = ((InjectAnnotationBeanPostProcessorTests.ResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(tb, bean.getTestBean());
        Assert.assertSame(tb, bean.getTestBean2());
    }

    @Test
    public void testExtendedResourceInjection() {
        RootBeanDefinition bd = new RootBeanDefinition(InjectAnnotationBeanPostProcessorTests.TypedExtendedResourceInjectionBean.class);
        bd.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", bd);
        TestBean tb = new TestBean();
        bf.registerSingleton("testBean", tb);
        NestedTestBean ntb = new NestedTestBean();
        bf.registerSingleton("nestedTestBean", ntb);
        InjectAnnotationBeanPostProcessorTests.TypedExtendedResourceInjectionBean bean = ((InjectAnnotationBeanPostProcessorTests.TypedExtendedResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(tb, bean.getTestBean());
        Assert.assertSame(tb, bean.getTestBean2());
        Assert.assertSame(tb, bean.getTestBean3());
        Assert.assertSame(tb, bean.getTestBean4());
        Assert.assertSame(ntb, bean.getNestedTestBean());
        Assert.assertSame(bf, bean.getBeanFactory());
        bean = ((InjectAnnotationBeanPostProcessorTests.TypedExtendedResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(tb, bean.getTestBean());
        Assert.assertSame(tb, bean.getTestBean2());
        Assert.assertSame(tb, bean.getTestBean3());
        Assert.assertSame(tb, bean.getTestBean4());
        Assert.assertSame(ntb, bean.getNestedTestBean());
        Assert.assertSame(bf, bean.getBeanFactory());
    }

    @Test
    public void testExtendedResourceInjectionWithOverriding() {
        RootBeanDefinition annotatedBd = new RootBeanDefinition(InjectAnnotationBeanPostProcessorTests.TypedExtendedResourceInjectionBean.class);
        TestBean tb2 = new TestBean();
        annotatedBd.getPropertyValues().add("testBean2", tb2);
        bf.registerBeanDefinition("annotatedBean", annotatedBd);
        TestBean tb = new TestBean();
        bf.registerSingleton("testBean", tb);
        NestedTestBean ntb = new NestedTestBean();
        bf.registerSingleton("nestedTestBean", ntb);
        InjectAnnotationBeanPostProcessorTests.TypedExtendedResourceInjectionBean bean = ((InjectAnnotationBeanPostProcessorTests.TypedExtendedResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(tb, bean.getTestBean());
        Assert.assertSame(tb2, bean.getTestBean2());
        Assert.assertSame(tb, bean.getTestBean3());
        Assert.assertSame(tb, bean.getTestBean4());
        Assert.assertSame(ntb, bean.getNestedTestBean());
        Assert.assertSame(bf, bean.getBeanFactory());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testExtendedResourceInjectionWithAtRequired() {
        bf.addBeanPostProcessor(new RequiredAnnotationBeanPostProcessor());
        RootBeanDefinition bd = new RootBeanDefinition(InjectAnnotationBeanPostProcessorTests.TypedExtendedResourceInjectionBean.class);
        bd.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", bd);
        TestBean tb = new TestBean();
        bf.registerSingleton("testBean", tb);
        NestedTestBean ntb = new NestedTestBean();
        bf.registerSingleton("nestedTestBean", ntb);
        InjectAnnotationBeanPostProcessorTests.TypedExtendedResourceInjectionBean bean = ((InjectAnnotationBeanPostProcessorTests.TypedExtendedResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(tb, bean.getTestBean());
        Assert.assertSame(tb, bean.getTestBean2());
        Assert.assertSame(tb, bean.getTestBean3());
        Assert.assertSame(tb, bean.getTestBean4());
        Assert.assertSame(ntb, bean.getNestedTestBean());
        Assert.assertSame(bf, bean.getBeanFactory());
    }

    @Test
    public void testConstructorResourceInjection() {
        RootBeanDefinition bd = new RootBeanDefinition(InjectAnnotationBeanPostProcessorTests.ConstructorResourceInjectionBean.class);
        bd.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", bd);
        TestBean tb = new TestBean();
        bf.registerSingleton("testBean", tb);
        NestedTestBean ntb = new NestedTestBean();
        bf.registerSingleton("nestedTestBean", ntb);
        InjectAnnotationBeanPostProcessorTests.ConstructorResourceInjectionBean bean = ((InjectAnnotationBeanPostProcessorTests.ConstructorResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(tb, bean.getTestBean());
        Assert.assertSame(tb, bean.getTestBean2());
        Assert.assertSame(tb, bean.getTestBean3());
        Assert.assertSame(tb, bean.getTestBean4());
        Assert.assertSame(ntb, bean.getNestedTestBean());
        Assert.assertSame(bf, bean.getBeanFactory());
        bean = ((InjectAnnotationBeanPostProcessorTests.ConstructorResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(tb, bean.getTestBean());
        Assert.assertSame(tb, bean.getTestBean2());
        Assert.assertSame(tb, bean.getTestBean3());
        Assert.assertSame(tb, bean.getTestBean4());
        Assert.assertSame(ntb, bean.getNestedTestBean());
        Assert.assertSame(bf, bean.getBeanFactory());
    }

    @Test
    public void testConstructorResourceInjectionWithMultipleCandidatesAsCollection() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(InjectAnnotationBeanPostProcessorTests.ConstructorsCollectionResourceInjectionBean.class));
        TestBean tb = new TestBean();
        bf.registerSingleton("testBean", tb);
        NestedTestBean ntb1 = new NestedTestBean();
        bf.registerSingleton("nestedTestBean1", ntb1);
        NestedTestBean ntb2 = new NestedTestBean();
        bf.registerSingleton("nestedTestBean2", ntb2);
        InjectAnnotationBeanPostProcessorTests.ConstructorsCollectionResourceInjectionBean bean = ((InjectAnnotationBeanPostProcessorTests.ConstructorsCollectionResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertNull(bean.getTestBean3());
        Assert.assertSame(tb, bean.getTestBean4());
        Assert.assertEquals(2, bean.getNestedTestBeans().size());
        Assert.assertSame(ntb1, bean.getNestedTestBeans().get(0));
        Assert.assertSame(ntb2, bean.getNestedTestBeans().get(1));
    }

    @Test
    public void testConstructorResourceInjectionWithMultipleCandidatesAndFallback() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(InjectAnnotationBeanPostProcessorTests.ConstructorsResourceInjectionBean.class));
        TestBean tb = new TestBean();
        bf.registerSingleton("testBean", tb);
        InjectAnnotationBeanPostProcessorTests.ConstructorsResourceInjectionBean bean = ((InjectAnnotationBeanPostProcessorTests.ConstructorsResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(tb, bean.getTestBean3());
        Assert.assertNull(bean.getTestBean4());
    }

    @Test
    public void testConstructorInjectionWithMap() {
        RootBeanDefinition bd = new RootBeanDefinition(InjectAnnotationBeanPostProcessorTests.MapConstructorInjectionBean.class);
        bd.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", bd);
        TestBean tb1 = new TestBean();
        TestBean tb2 = new TestBean();
        bf.registerSingleton("testBean1", tb1);
        bf.registerSingleton("testBean2", tb1);
        InjectAnnotationBeanPostProcessorTests.MapConstructorInjectionBean bean = ((InjectAnnotationBeanPostProcessorTests.MapConstructorInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertEquals(2, bean.getTestBeanMap().size());
        Assert.assertTrue(bean.getTestBeanMap().keySet().contains("testBean1"));
        Assert.assertTrue(bean.getTestBeanMap().keySet().contains("testBean2"));
        Assert.assertTrue(bean.getTestBeanMap().values().contains(tb1));
        Assert.assertTrue(bean.getTestBeanMap().values().contains(tb2));
        bean = ((InjectAnnotationBeanPostProcessorTests.MapConstructorInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertEquals(2, bean.getTestBeanMap().size());
        Assert.assertTrue(bean.getTestBeanMap().keySet().contains("testBean1"));
        Assert.assertTrue(bean.getTestBeanMap().keySet().contains("testBean2"));
        Assert.assertTrue(bean.getTestBeanMap().values().contains(tb1));
        Assert.assertTrue(bean.getTestBeanMap().values().contains(tb2));
    }

    @Test
    public void testFieldInjectionWithMap() {
        RootBeanDefinition bd = new RootBeanDefinition(InjectAnnotationBeanPostProcessorTests.MapFieldInjectionBean.class);
        bd.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", bd);
        TestBean tb1 = new TestBean();
        TestBean tb2 = new TestBean();
        bf.registerSingleton("testBean1", tb1);
        bf.registerSingleton("testBean2", tb1);
        InjectAnnotationBeanPostProcessorTests.MapFieldInjectionBean bean = ((InjectAnnotationBeanPostProcessorTests.MapFieldInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertEquals(2, bean.getTestBeanMap().size());
        Assert.assertTrue(bean.getTestBeanMap().keySet().contains("testBean1"));
        Assert.assertTrue(bean.getTestBeanMap().keySet().contains("testBean2"));
        Assert.assertTrue(bean.getTestBeanMap().values().contains(tb1));
        Assert.assertTrue(bean.getTestBeanMap().values().contains(tb2));
        bean = ((InjectAnnotationBeanPostProcessorTests.MapFieldInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertEquals(2, bean.getTestBeanMap().size());
        Assert.assertTrue(bean.getTestBeanMap().keySet().contains("testBean1"));
        Assert.assertTrue(bean.getTestBeanMap().keySet().contains("testBean2"));
        Assert.assertTrue(bean.getTestBeanMap().values().contains(tb1));
        Assert.assertTrue(bean.getTestBeanMap().values().contains(tb2));
    }

    @Test
    public void testMethodInjectionWithMap() {
        RootBeanDefinition bd = new RootBeanDefinition(InjectAnnotationBeanPostProcessorTests.MapMethodInjectionBean.class);
        bd.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", bd);
        TestBean tb = new TestBean();
        bf.registerSingleton("testBean", tb);
        InjectAnnotationBeanPostProcessorTests.MapMethodInjectionBean bean = ((InjectAnnotationBeanPostProcessorTests.MapMethodInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertEquals(1, bean.getTestBeanMap().size());
        Assert.assertTrue(bean.getTestBeanMap().keySet().contains("testBean"));
        Assert.assertTrue(bean.getTestBeanMap().values().contains(tb));
        Assert.assertSame(tb, bean.getTestBean());
        bean = ((InjectAnnotationBeanPostProcessorTests.MapMethodInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertEquals(1, bean.getTestBeanMap().size());
        Assert.assertTrue(bean.getTestBeanMap().keySet().contains("testBean"));
        Assert.assertTrue(bean.getTestBeanMap().values().contains(tb));
        Assert.assertSame(tb, bean.getTestBean());
    }

    @Test
    public void testMethodInjectionWithMapAndMultipleMatches() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(InjectAnnotationBeanPostProcessorTests.MapMethodInjectionBean.class));
        bf.registerBeanDefinition("testBean1", new RootBeanDefinition(TestBean.class));
        bf.registerBeanDefinition("testBean2", new RootBeanDefinition(TestBean.class));
        try {
            bf.getBean("annotatedBean");
            Assert.fail("should have failed, more than one bean of type");
        } catch (BeanCreationException e) {
            // expected
        }
    }

    @Test
    public void testMethodInjectionWithMapAndMultipleMatchesButOnlyOneAutowireCandidate() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(InjectAnnotationBeanPostProcessorTests.MapMethodInjectionBean.class));
        bf.registerBeanDefinition("testBean1", new RootBeanDefinition(TestBean.class));
        RootBeanDefinition rbd2 = new RootBeanDefinition(TestBean.class);
        rbd2.setAutowireCandidate(false);
        bf.registerBeanDefinition("testBean2", rbd2);
        InjectAnnotationBeanPostProcessorTests.MapMethodInjectionBean bean = ((InjectAnnotationBeanPostProcessorTests.MapMethodInjectionBean) (bf.getBean("annotatedBean")));
        TestBean tb = ((TestBean) (bf.getBean("testBean1")));
        Assert.assertEquals(1, bean.getTestBeanMap().size());
        Assert.assertTrue(bean.getTestBeanMap().keySet().contains("testBean1"));
        Assert.assertTrue(bean.getTestBeanMap().values().contains(tb));
        Assert.assertSame(tb, bean.getTestBean());
    }

    @Test
    public void testObjectFactoryInjection() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(InjectAnnotationBeanPostProcessorTests.ObjectFactoryQualifierFieldInjectionBean.class));
        RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
        bd.addQualifier(new AutowireCandidateQualifier(Qualifier.class, "testBean"));
        bf.registerBeanDefinition("testBean", bd);
        bf.registerBeanDefinition("testBean2", new RootBeanDefinition(TestBean.class));
        InjectAnnotationBeanPostProcessorTests.ObjectFactoryQualifierFieldInjectionBean bean = ((InjectAnnotationBeanPostProcessorTests.ObjectFactoryQualifierFieldInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(bf.getBean("testBean"), bean.getTestBean());
    }

    @Test
    public void testObjectFactoryQualifierInjection() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(InjectAnnotationBeanPostProcessorTests.ObjectFactoryQualifierFieldInjectionBean.class));
        RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
        bd.addQualifier(new AutowireCandidateQualifier(Qualifier.class, "testBean"));
        bf.registerBeanDefinition("testBean", bd);
        InjectAnnotationBeanPostProcessorTests.ObjectFactoryQualifierFieldInjectionBean bean = ((InjectAnnotationBeanPostProcessorTests.ObjectFactoryQualifierFieldInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(bf.getBean("testBean"), bean.getTestBean());
    }

    @Test
    public void testObjectFactoryFieldInjectionIntoPrototypeBean() {
        RootBeanDefinition annotatedBeanDefinition = new RootBeanDefinition(InjectAnnotationBeanPostProcessorTests.ObjectFactoryQualifierFieldInjectionBean.class);
        annotatedBeanDefinition.setScope(BeanDefinition.SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", annotatedBeanDefinition);
        RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
        bd.addQualifier(new AutowireCandidateQualifier(Qualifier.class, "testBean"));
        bf.registerBeanDefinition("testBean", bd);
        bf.registerBeanDefinition("testBean2", new RootBeanDefinition(TestBean.class));
        InjectAnnotationBeanPostProcessorTests.ObjectFactoryQualifierFieldInjectionBean bean = ((InjectAnnotationBeanPostProcessorTests.ObjectFactoryQualifierFieldInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(bf.getBean("testBean"), bean.getTestBean());
        InjectAnnotationBeanPostProcessorTests.ObjectFactoryQualifierFieldInjectionBean anotherBean = ((InjectAnnotationBeanPostProcessorTests.ObjectFactoryQualifierFieldInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertNotSame(anotherBean, bean);
        Assert.assertSame(bf.getBean("testBean"), bean.getTestBean());
    }

    @Test
    public void testObjectFactoryMethodInjectionIntoPrototypeBean() {
        RootBeanDefinition annotatedBeanDefinition = new RootBeanDefinition(InjectAnnotationBeanPostProcessorTests.ObjectFactoryQualifierMethodInjectionBean.class);
        annotatedBeanDefinition.setScope(BeanDefinition.SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", annotatedBeanDefinition);
        RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
        bd.addQualifier(new AutowireCandidateQualifier(Qualifier.class, "testBean"));
        bf.registerBeanDefinition("testBean", bd);
        bf.registerBeanDefinition("testBean2", new RootBeanDefinition(TestBean.class));
        InjectAnnotationBeanPostProcessorTests.ObjectFactoryQualifierMethodInjectionBean bean = ((InjectAnnotationBeanPostProcessorTests.ObjectFactoryQualifierMethodInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(bf.getBean("testBean"), bean.getTestBean());
        InjectAnnotationBeanPostProcessorTests.ObjectFactoryQualifierMethodInjectionBean anotherBean = ((InjectAnnotationBeanPostProcessorTests.ObjectFactoryQualifierMethodInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertNotSame(anotherBean, bean);
        Assert.assertSame(bf.getBean("testBean"), bean.getTestBean());
    }

    @Test
    public void testObjectFactoryWithBeanField() throws Exception {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(InjectAnnotationBeanPostProcessorTests.ObjectFactoryFieldInjectionBean.class));
        bf.registerBeanDefinition("testBean", new RootBeanDefinition(TestBean.class));
        bf.setSerializationId("test");
        InjectAnnotationBeanPostProcessorTests.ObjectFactoryFieldInjectionBean bean = ((InjectAnnotationBeanPostProcessorTests.ObjectFactoryFieldInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(bf.getBean("testBean"), bean.getTestBean());
        bean = ((InjectAnnotationBeanPostProcessorTests.ObjectFactoryFieldInjectionBean) (SerializationTestUtils.serializeAndDeserialize(bean)));
        Assert.assertSame(bf.getBean("testBean"), bean.getTestBean());
    }

    @Test
    public void testObjectFactoryWithBeanMethod() throws Exception {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(InjectAnnotationBeanPostProcessorTests.ObjectFactoryMethodInjectionBean.class));
        bf.registerBeanDefinition("testBean", new RootBeanDefinition(TestBean.class));
        bf.setSerializationId("test");
        InjectAnnotationBeanPostProcessorTests.ObjectFactoryMethodInjectionBean bean = ((InjectAnnotationBeanPostProcessorTests.ObjectFactoryMethodInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(bf.getBean("testBean"), bean.getTestBean());
        bean = ((InjectAnnotationBeanPostProcessorTests.ObjectFactoryMethodInjectionBean) (SerializationTestUtils.serializeAndDeserialize(bean)));
        Assert.assertSame(bf.getBean("testBean"), bean.getTestBean());
    }

    @Test
    public void testObjectFactoryWithTypedListField() throws Exception {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(InjectAnnotationBeanPostProcessorTests.ObjectFactoryListFieldInjectionBean.class));
        bf.registerBeanDefinition("testBean", new RootBeanDefinition(TestBean.class));
        bf.setSerializationId("test");
        InjectAnnotationBeanPostProcessorTests.ObjectFactoryListFieldInjectionBean bean = ((InjectAnnotationBeanPostProcessorTests.ObjectFactoryListFieldInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(bf.getBean("testBean"), bean.getTestBean());
        bean = ((InjectAnnotationBeanPostProcessorTests.ObjectFactoryListFieldInjectionBean) (SerializationTestUtils.serializeAndDeserialize(bean)));
        Assert.assertSame(bf.getBean("testBean"), bean.getTestBean());
    }

    @Test
    public void testObjectFactoryWithTypedListMethod() throws Exception {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(InjectAnnotationBeanPostProcessorTests.ObjectFactoryListMethodInjectionBean.class));
        bf.registerBeanDefinition("testBean", new RootBeanDefinition(TestBean.class));
        bf.setSerializationId("test");
        InjectAnnotationBeanPostProcessorTests.ObjectFactoryListMethodInjectionBean bean = ((InjectAnnotationBeanPostProcessorTests.ObjectFactoryListMethodInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(bf.getBean("testBean"), bean.getTestBean());
        bean = ((InjectAnnotationBeanPostProcessorTests.ObjectFactoryListMethodInjectionBean) (SerializationTestUtils.serializeAndDeserialize(bean)));
        Assert.assertSame(bf.getBean("testBean"), bean.getTestBean());
    }

    @Test
    public void testObjectFactoryWithTypedMapField() throws Exception {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(InjectAnnotationBeanPostProcessorTests.ObjectFactoryMapFieldInjectionBean.class));
        bf.registerBeanDefinition("testBean", new RootBeanDefinition(TestBean.class));
        bf.setSerializationId("test");
        InjectAnnotationBeanPostProcessorTests.ObjectFactoryMapFieldInjectionBean bean = ((InjectAnnotationBeanPostProcessorTests.ObjectFactoryMapFieldInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(bf.getBean("testBean"), bean.getTestBean());
        bean = ((InjectAnnotationBeanPostProcessorTests.ObjectFactoryMapFieldInjectionBean) (SerializationTestUtils.serializeAndDeserialize(bean)));
        Assert.assertSame(bf.getBean("testBean"), bean.getTestBean());
    }

    @Test
    public void testObjectFactoryWithTypedMapMethod() throws Exception {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(InjectAnnotationBeanPostProcessorTests.ObjectFactoryMapMethodInjectionBean.class));
        bf.registerBeanDefinition("testBean", new RootBeanDefinition(TestBean.class));
        bf.setSerializationId("test");
        InjectAnnotationBeanPostProcessorTests.ObjectFactoryMapMethodInjectionBean bean = ((InjectAnnotationBeanPostProcessorTests.ObjectFactoryMapMethodInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(bf.getBean("testBean"), bean.getTestBean());
        bean = ((InjectAnnotationBeanPostProcessorTests.ObjectFactoryMapMethodInjectionBean) (SerializationTestUtils.serializeAndDeserialize(bean)));
        Assert.assertSame(bf.getBean("testBean"), bean.getTestBean());
    }

    /**
     * Verifies that a dependency on a {@link org.springframework.beans.factory.FactoryBean}
     * can be autowired via {@link org.springframework.beans.factory.annotation.Autowired @Inject},
     * specifically addressing SPR-4040.
     */
    @Test
    public void testBeanAutowiredWithFactoryBean() {
        bf.registerBeanDefinition("factoryBeanDependentBean", new RootBeanDefinition(InjectAnnotationBeanPostProcessorTests.FactoryBeanDependentBean.class));
        bf.registerSingleton("stringFactoryBean", new InjectAnnotationBeanPostProcessorTests.StringFactoryBean());
        final InjectAnnotationBeanPostProcessorTests.StringFactoryBean factoryBean = ((InjectAnnotationBeanPostProcessorTests.StringFactoryBean) (bf.getBean("&stringFactoryBean")));
        final InjectAnnotationBeanPostProcessorTests.FactoryBeanDependentBean bean = ((InjectAnnotationBeanPostProcessorTests.FactoryBeanDependentBean) (bf.getBean("factoryBeanDependentBean")));
        Assert.assertNotNull("The singleton StringFactoryBean should have been registered.", factoryBean);
        Assert.assertNotNull("The factoryBeanDependentBean should have been registered.", bean);
        Assert.assertEquals("The FactoryBeanDependentBean should have been autowired 'by type' with the StringFactoryBean.", factoryBean, bean.getFactoryBean());
    }

    @Test
    public void testNullableFieldInjectionWithBeanAvailable() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(InjectAnnotationBeanPostProcessorTests.NullableFieldInjectionBean.class));
        bf.registerBeanDefinition("testBean", new RootBeanDefinition(TestBean.class));
        InjectAnnotationBeanPostProcessorTests.NullableFieldInjectionBean bean = ((InjectAnnotationBeanPostProcessorTests.NullableFieldInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(bf.getBean("testBean"), bean.getTestBean());
    }

    @Test
    public void testNullableFieldInjectionWithBeanNotAvailable() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(InjectAnnotationBeanPostProcessorTests.NullableFieldInjectionBean.class));
        InjectAnnotationBeanPostProcessorTests.NullableFieldInjectionBean bean = ((InjectAnnotationBeanPostProcessorTests.NullableFieldInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertNull(bean.getTestBean());
    }

    @Test
    public void testNullableMethodInjectionWithBeanAvailable() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(InjectAnnotationBeanPostProcessorTests.NullableMethodInjectionBean.class));
        bf.registerBeanDefinition("testBean", new RootBeanDefinition(TestBean.class));
        InjectAnnotationBeanPostProcessorTests.NullableMethodInjectionBean bean = ((InjectAnnotationBeanPostProcessorTests.NullableMethodInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertSame(bf.getBean("testBean"), bean.getTestBean());
    }

    @Test
    public void testNullableMethodInjectionWithBeanNotAvailable() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(InjectAnnotationBeanPostProcessorTests.NullableMethodInjectionBean.class));
        InjectAnnotationBeanPostProcessorTests.NullableMethodInjectionBean bean = ((InjectAnnotationBeanPostProcessorTests.NullableMethodInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertNull(bean.getTestBean());
    }

    @Test
    public void testOptionalFieldInjectionWithBeanAvailable() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(InjectAnnotationBeanPostProcessorTests.OptionalFieldInjectionBean.class));
        bf.registerBeanDefinition("testBean", new RootBeanDefinition(TestBean.class));
        InjectAnnotationBeanPostProcessorTests.OptionalFieldInjectionBean bean = ((InjectAnnotationBeanPostProcessorTests.OptionalFieldInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertTrue(bean.getTestBean().isPresent());
        Assert.assertSame(bf.getBean("testBean"), bean.getTestBean().get());
    }

    @Test
    public void testOptionalFieldInjectionWithBeanNotAvailable() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(InjectAnnotationBeanPostProcessorTests.OptionalFieldInjectionBean.class));
        InjectAnnotationBeanPostProcessorTests.OptionalFieldInjectionBean bean = ((InjectAnnotationBeanPostProcessorTests.OptionalFieldInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertFalse(bean.getTestBean().isPresent());
    }

    @Test
    public void testOptionalMethodInjectionWithBeanAvailable() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(InjectAnnotationBeanPostProcessorTests.OptionalMethodInjectionBean.class));
        bf.registerBeanDefinition("testBean", new RootBeanDefinition(TestBean.class));
        InjectAnnotationBeanPostProcessorTests.OptionalMethodInjectionBean bean = ((InjectAnnotationBeanPostProcessorTests.OptionalMethodInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertTrue(bean.getTestBean().isPresent());
        Assert.assertSame(bf.getBean("testBean"), bean.getTestBean().get());
    }

    @Test
    public void testOptionalMethodInjectionWithBeanNotAvailable() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(InjectAnnotationBeanPostProcessorTests.OptionalMethodInjectionBean.class));
        InjectAnnotationBeanPostProcessorTests.OptionalMethodInjectionBean bean = ((InjectAnnotationBeanPostProcessorTests.OptionalMethodInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertFalse(bean.getTestBean().isPresent());
    }

    @Test
    public void testOptionalListFieldInjectionWithBeanAvailable() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(InjectAnnotationBeanPostProcessorTests.OptionalListFieldInjectionBean.class));
        bf.registerBeanDefinition("testBean", new RootBeanDefinition(TestBean.class));
        InjectAnnotationBeanPostProcessorTests.OptionalListFieldInjectionBean bean = ((InjectAnnotationBeanPostProcessorTests.OptionalListFieldInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertTrue(bean.getTestBean().isPresent());
        Assert.assertSame(bf.getBean("testBean"), bean.getTestBean().get().get(0));
    }

    @Test
    public void testOptionalListFieldInjectionWithBeanNotAvailable() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(InjectAnnotationBeanPostProcessorTests.OptionalListFieldInjectionBean.class));
        InjectAnnotationBeanPostProcessorTests.OptionalListFieldInjectionBean bean = ((InjectAnnotationBeanPostProcessorTests.OptionalListFieldInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertFalse(bean.getTestBean().isPresent());
    }

    @Test
    public void testOptionalListMethodInjectionWithBeanAvailable() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(InjectAnnotationBeanPostProcessorTests.OptionalListMethodInjectionBean.class));
        bf.registerBeanDefinition("testBean", new RootBeanDefinition(TestBean.class));
        InjectAnnotationBeanPostProcessorTests.OptionalListMethodInjectionBean bean = ((InjectAnnotationBeanPostProcessorTests.OptionalListMethodInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertTrue(bean.getTestBean().isPresent());
        Assert.assertSame(bf.getBean("testBean"), bean.getTestBean().get().get(0));
    }

    @Test
    public void testOptionalListMethodInjectionWithBeanNotAvailable() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(InjectAnnotationBeanPostProcessorTests.OptionalListMethodInjectionBean.class));
        InjectAnnotationBeanPostProcessorTests.OptionalListMethodInjectionBean bean = ((InjectAnnotationBeanPostProcessorTests.OptionalListMethodInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertFalse(bean.getTestBean().isPresent());
    }

    @Test
    public void testProviderOfOptionalFieldInjectionWithBeanAvailable() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(InjectAnnotationBeanPostProcessorTests.ProviderOfOptionalFieldInjectionBean.class));
        bf.registerBeanDefinition("testBean", new RootBeanDefinition(TestBean.class));
        InjectAnnotationBeanPostProcessorTests.ProviderOfOptionalFieldInjectionBean bean = ((InjectAnnotationBeanPostProcessorTests.ProviderOfOptionalFieldInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertTrue(bean.getTestBean().isPresent());
        Assert.assertSame(bf.getBean("testBean"), bean.getTestBean().get());
    }

    @Test
    public void testProviderOfOptionalFieldInjectionWithBeanNotAvailable() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(InjectAnnotationBeanPostProcessorTests.ProviderOfOptionalFieldInjectionBean.class));
        InjectAnnotationBeanPostProcessorTests.ProviderOfOptionalFieldInjectionBean bean = ((InjectAnnotationBeanPostProcessorTests.ProviderOfOptionalFieldInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertFalse(bean.getTestBean().isPresent());
    }

    @Test
    public void testProviderOfOptionalMethodInjectionWithBeanAvailable() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(InjectAnnotationBeanPostProcessorTests.ProviderOfOptionalMethodInjectionBean.class));
        bf.registerBeanDefinition("testBean", new RootBeanDefinition(TestBean.class));
        InjectAnnotationBeanPostProcessorTests.ProviderOfOptionalMethodInjectionBean bean = ((InjectAnnotationBeanPostProcessorTests.ProviderOfOptionalMethodInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertTrue(bean.getTestBean().isPresent());
        Assert.assertSame(bf.getBean("testBean"), bean.getTestBean().get());
    }

    @Test
    public void testProviderOfOptionalMethodInjectionWithBeanNotAvailable() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(InjectAnnotationBeanPostProcessorTests.ProviderOfOptionalMethodInjectionBean.class));
        InjectAnnotationBeanPostProcessorTests.ProviderOfOptionalMethodInjectionBean bean = ((InjectAnnotationBeanPostProcessorTests.ProviderOfOptionalMethodInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertFalse(bean.getTestBean().isPresent());
    }

    @Test
    public void testAnnotatedDefaultConstructor() {
        bf.registerBeanDefinition("annotatedBean", new RootBeanDefinition(InjectAnnotationBeanPostProcessorTests.AnnotatedDefaultConstructorBean.class));
        Assert.assertNotNull(bf.getBean("annotatedBean"));
    }

    public static class ResourceInjectionBean {
        @Inject
        private TestBean testBean;

        private TestBean testBean2;

        @Inject
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

    public static class ExtendedResourceInjectionBean<T> extends InjectAnnotationBeanPostProcessorTests.ResourceInjectionBean {
        @Inject
        protected ITestBean testBean3;

        private T nestedTestBean;

        private ITestBean testBean4;

        private BeanFactory beanFactory;

        public ExtendedResourceInjectionBean() {
        }

        @Override
        @Inject
        @Required
        @SuppressWarnings("deprecation")
        public void setTestBean2(TestBean testBean2) {
            super.setTestBean2(testBean2);
        }

        @Inject
        private void inject(ITestBean testBean4, T nestedTestBean) {
            this.testBean4 = testBean4;
            this.nestedTestBean = nestedTestBean;
        }

        @Inject
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

    public static class TypedExtendedResourceInjectionBean extends InjectAnnotationBeanPostProcessorTests.ExtendedResourceInjectionBean<NestedTestBean> {}

    public static class OptionalResourceInjectionBean extends InjectAnnotationBeanPostProcessorTests.ResourceInjectionBean {
        @Inject
        protected ITestBean testBean3;

        private IndexedTestBean indexedTestBean;

        private NestedTestBean[] nestedTestBeans;

        @Inject
        public NestedTestBean[] nestedTestBeansField;

        private ITestBean testBean4;

        @Override
        @Inject
        public void setTestBean2(TestBean testBean2) {
            super.setTestBean2(testBean2);
        }

        @Inject
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

    public static class OptionalCollectionResourceInjectionBean extends InjectAnnotationBeanPostProcessorTests.ResourceInjectionBean {
        @Inject
        protected ITestBean testBean3;

        private IndexedTestBean indexedTestBean;

        private List<NestedTestBean> nestedTestBeans;

        public List<NestedTestBean> nestedTestBeansSetter;

        @Inject
        public List<NestedTestBean> nestedTestBeansField;

        private ITestBean testBean4;

        @Override
        @Inject
        public void setTestBean2(TestBean testBean2) {
            super.setTestBean2(testBean2);
        }

        @Inject
        private void inject(ITestBean testBean4, List<NestedTestBean> nestedTestBeans, IndexedTestBean indexedTestBean) {
            this.testBean4 = testBean4;
            this.indexedTestBean = indexedTestBean;
            this.nestedTestBeans = nestedTestBeans;
        }

        @Inject
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

    public static class ConstructorResourceInjectionBean extends InjectAnnotationBeanPostProcessorTests.ResourceInjectionBean {
        @Inject
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

        @Inject
        public ConstructorResourceInjectionBean(ITestBean testBean4, NestedTestBean nestedTestBean, ConfigurableListableBeanFactory beanFactory) {
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
        @Inject
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

        @Inject
        public ConstructorsResourceInjectionBean(ITestBean testBean3) {
            this.testBean3 = testBean3;
        }

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

    public static class ConstructorsCollectionResourceInjectionBean {
        protected ITestBean testBean3;

        private ITestBean testBean4;

        private List<NestedTestBean> nestedTestBeans;

        public ConstructorsCollectionResourceInjectionBean() {
        }

        public ConstructorsCollectionResourceInjectionBean(ITestBean testBean3) {
            this.testBean3 = testBean3;
        }

        @Inject
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

    public static class MapConstructorInjectionBean {
        private Map<String, TestBean> testBeanMap;

        @Inject
        public MapConstructorInjectionBean(Map<String, TestBean> testBeanMap) {
            this.testBeanMap = testBeanMap;
        }

        public Map<String, TestBean> getTestBeanMap() {
            return this.testBeanMap;
        }
    }

    public static class MapFieldInjectionBean {
        @Inject
        private Map<String, TestBean> testBeanMap;

        public Map<String, TestBean> getTestBeanMap() {
            return this.testBeanMap;
        }
    }

    public static class MapMethodInjectionBean {
        private TestBean testBean;

        private Map<String, TestBean> testBeanMap;

        @Inject
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
        @Inject
        private Provider<TestBean> testBeanFactory;

        public TestBean getTestBean() {
            return this.testBeanFactory.get();
        }
    }

    @SuppressWarnings("serial")
    public static class ObjectFactoryMethodInjectionBean implements Serializable {
        private Provider<TestBean> testBeanFactory;

        @Inject
        public void setTestBeanFactory(Provider<TestBean> testBeanFactory) {
            this.testBeanFactory = testBeanFactory;
        }

        public TestBean getTestBean() {
            return this.testBeanFactory.get();
        }
    }

    public static class ObjectFactoryQualifierFieldInjectionBean {
        @Inject
        @Named("testBean")
        private Provider<?> testBeanFactory;

        public TestBean getTestBean() {
            return ((TestBean) (this.testBeanFactory.get()));
        }
    }

    public static class ObjectFactoryQualifierMethodInjectionBean {
        private Provider<?> testBeanFactory;

        @Inject
        @Named("testBean")
        public void setTestBeanFactory(Provider<?> testBeanFactory) {
            this.testBeanFactory = testBeanFactory;
        }

        public TestBean getTestBean() {
            return ((TestBean) (this.testBeanFactory.get()));
        }
    }

    @SuppressWarnings("serial")
    public static class ObjectFactoryListFieldInjectionBean implements Serializable {
        @Inject
        private Provider<List<TestBean>> testBeanFactory;

        public void setTestBeanFactory(Provider<List<TestBean>> testBeanFactory) {
            this.testBeanFactory = testBeanFactory;
        }

        public TestBean getTestBean() {
            return this.testBeanFactory.get().get(0);
        }
    }

    @SuppressWarnings("serial")
    public static class ObjectFactoryListMethodInjectionBean implements Serializable {
        private Provider<List<TestBean>> testBeanFactory;

        @Inject
        public void setTestBeanFactory(Provider<List<TestBean>> testBeanFactory) {
            this.testBeanFactory = testBeanFactory;
        }

        public TestBean getTestBean() {
            return this.testBeanFactory.get().get(0);
        }
    }

    @SuppressWarnings("serial")
    public static class ObjectFactoryMapFieldInjectionBean implements Serializable {
        @Inject
        private Provider<Map<String, TestBean>> testBeanFactory;

        public void setTestBeanFactory(Provider<Map<String, TestBean>> testBeanFactory) {
            this.testBeanFactory = testBeanFactory;
        }

        public TestBean getTestBean() {
            return this.testBeanFactory.get().values().iterator().next();
        }
    }

    @SuppressWarnings("serial")
    public static class ObjectFactoryMapMethodInjectionBean implements Serializable {
        private Provider<Map<String, TestBean>> testBeanFactory;

        @Inject
        public void setTestBeanFactory(Provider<Map<String, TestBean>> testBeanFactory) {
            this.testBeanFactory = testBeanFactory;
        }

        public TestBean getTestBean() {
            return this.testBeanFactory.get().values().iterator().next();
        }
    }

    /**
     * Bean with a dependency on a {@link org.springframework.beans.factory.FactoryBean}.
     */
    private static class FactoryBeanDependentBean {
        @Inject
        private FactoryBean<?> factoryBean;

        public final FactoryBean<?> getFactoryBean() {
            return this.factoryBean;
        }
    }

    public static class StringFactoryBean implements FactoryBean<String> {
        @Override
        public String getObject() {
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

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Nullable {}

    public static class NullableFieldInjectionBean {
        @Inject
        @InjectAnnotationBeanPostProcessorTests.Nullable
        private TestBean testBean;

        public TestBean getTestBean() {
            return this.testBean;
        }
    }

    public static class NullableMethodInjectionBean {
        private TestBean testBean;

        @Inject
        public void setTestBean(@InjectAnnotationBeanPostProcessorTests.Nullable
        TestBean testBean) {
            this.testBean = testBean;
        }

        public TestBean getTestBean() {
            return this.testBean;
        }
    }

    public static class OptionalFieldInjectionBean {
        @Inject
        private Optional<TestBean> testBean;

        public Optional<TestBean> getTestBean() {
            return this.testBean;
        }
    }

    public static class OptionalMethodInjectionBean {
        private Optional<TestBean> testBean;

        @Inject
        public void setTestBean(Optional<TestBean> testBean) {
            this.testBean = testBean;
        }

        public Optional<TestBean> getTestBean() {
            return this.testBean;
        }
    }

    public static class OptionalListFieldInjectionBean {
        @Inject
        private Optional<List<TestBean>> testBean;

        public Optional<List<TestBean>> getTestBean() {
            return this.testBean;
        }
    }

    public static class OptionalListMethodInjectionBean {
        private Optional<List<TestBean>> testBean;

        @Inject
        public void setTestBean(Optional<List<TestBean>> testBean) {
            this.testBean = testBean;
        }

        public Optional<List<TestBean>> getTestBean() {
            return this.testBean;
        }
    }

    public static class ProviderOfOptionalFieldInjectionBean {
        @Inject
        private Provider<Optional<TestBean>> testBean;

        public Optional<TestBean> getTestBean() {
            return this.testBean.get();
        }
    }

    public static class ProviderOfOptionalMethodInjectionBean {
        private Provider<Optional<TestBean>> testBean;

        @Inject
        public void setTestBean(Provider<Optional<TestBean>> testBean) {
            this.testBean = testBean;
        }

        public Optional<TestBean> getTestBean() {
            return this.testBean.get();
        }
    }

    public static class AnnotatedDefaultConstructorBean {
        @Inject
        public AnnotatedDefaultConstructorBean() {
        }
    }
}

