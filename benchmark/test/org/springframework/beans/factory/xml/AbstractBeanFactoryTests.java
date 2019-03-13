/**
 * Copyright 2002-2015 the original author or authors.
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
package org.springframework.beans.factory.xml;


import java.beans.PropertyEditorSupport;
import java.util.StringTokenizer;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanIsNotAFactoryException;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.tests.sample.beans.LifecycleBean;
import org.springframework.tests.sample.beans.MustBeInitialized;
import org.springframework.tests.sample.beans.TestBean;
import org.springframework.tests.sample.beans.factory.DummyFactory;


/**
 * Subclasses must initialize the bean factory and any other variables they need.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Sam Brannen
 */
public abstract class AbstractBeanFactoryTests {
    /**
     * Roderick bean inherits from rod, overriding name only.
     */
    @Test
    public void inheritance() {
        Assert.assertTrue(getBeanFactory().containsBean("rod"));
        Assert.assertTrue(getBeanFactory().containsBean("roderick"));
        TestBean rod = ((TestBean) (getBeanFactory().getBean("rod")));
        TestBean roderick = ((TestBean) (getBeanFactory().getBean("roderick")));
        Assert.assertTrue("not == ", (rod != roderick));
        Assert.assertTrue("rod.name is Rod", rod.getName().equals("Rod"));
        Assert.assertTrue("rod.age is 31", ((rod.getAge()) == 31));
        Assert.assertTrue("roderick.name is Roderick", roderick.getName().equals("Roderick"));
        Assert.assertTrue("roderick.age was inherited", ((roderick.getAge()) == (rod.getAge())));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getBeanWithNullArg() {
        getBeanFactory().getBean(((String) (null)));
    }

    /**
     * Test that InitializingBean objects receive the afterPropertiesSet() callback
     */
    @Test
    public void initializingBeanCallback() {
        MustBeInitialized mbi = ((MustBeInitialized) (getBeanFactory().getBean("mustBeInitialized")));
        // The dummy business method will throw an exception if the
        // afterPropertiesSet() callback wasn't invoked
        mbi.businessMethod();
    }

    /**
     * Test that InitializingBean/BeanFactoryAware/DisposableBean objects receive the
     * afterPropertiesSet() callback before BeanFactoryAware callbacks
     */
    @Test
    public void lifecycleCallbacks() {
        LifecycleBean lb = ((LifecycleBean) (getBeanFactory().getBean("lifecycle")));
        Assert.assertEquals("lifecycle", lb.getBeanName());
        // The dummy business method will throw an exception if the
        // necessary callbacks weren't invoked in the right order.
        lb.businessMethod();
        Assert.assertTrue("Not destroyed", (!(lb.isDestroyed())));
    }

    @Test
    public void findsValidInstance() {
        Object o = getBeanFactory().getBean("rod");
        Assert.assertTrue("Rod bean is a TestBean", (o instanceof TestBean));
        TestBean rod = ((TestBean) (o));
        Assert.assertTrue("rod.name is Rod", rod.getName().equals("Rod"));
        Assert.assertTrue("rod.age is 31", ((rod.getAge()) == 31));
    }

    @Test
    public void getInstanceByMatchingClass() {
        Object o = getBeanFactory().getBean("rod", TestBean.class);
        Assert.assertTrue("Rod bean is a TestBean", (o instanceof TestBean));
    }

    @Test
    public void getInstanceByNonmatchingClass() {
        try {
            getBeanFactory().getBean("rod", BeanFactory.class);
            Assert.fail("Rod bean is not of type BeanFactory; getBeanInstance(rod, BeanFactory.class) should throw BeanNotOfRequiredTypeException");
        } catch (BeanNotOfRequiredTypeException ex) {
            // So far, so good
            Assert.assertTrue("Exception has correct bean name", ex.getBeanName().equals("rod"));
            Assert.assertTrue("Exception requiredType must be BeanFactory.class", ex.getRequiredType().equals(BeanFactory.class));
            Assert.assertTrue("Exception actualType as TestBean.class", TestBean.class.isAssignableFrom(ex.getActualType()));
            Assert.assertTrue("Actual type is correct", ((ex.getActualType()) == (getBeanFactory().getBean("rod").getClass())));
        }
    }

    @Test
    public void getSharedInstanceByMatchingClass() {
        Object o = getBeanFactory().getBean("rod", TestBean.class);
        Assert.assertTrue("Rod bean is a TestBean", (o instanceof TestBean));
    }

    @Test
    public void getSharedInstanceByMatchingClassNoCatch() {
        Object o = getBeanFactory().getBean("rod", TestBean.class);
        Assert.assertTrue("Rod bean is a TestBean", (o instanceof TestBean));
    }

    @Test
    public void getSharedInstanceByNonmatchingClass() {
        try {
            getBeanFactory().getBean("rod", BeanFactory.class);
            Assert.fail("Rod bean is not of type BeanFactory; getBeanInstance(rod, BeanFactory.class) should throw BeanNotOfRequiredTypeException");
        } catch (BeanNotOfRequiredTypeException ex) {
            // So far, so good
            Assert.assertTrue("Exception has correct bean name", ex.getBeanName().equals("rod"));
            Assert.assertTrue("Exception requiredType must be BeanFactory.class", ex.getRequiredType().equals(BeanFactory.class));
            Assert.assertTrue("Exception actualType as TestBean.class", TestBean.class.isAssignableFrom(ex.getActualType()));
        }
    }

    @Test
    public void sharedInstancesAreEqual() {
        Object o = getBeanFactory().getBean("rod");
        Assert.assertTrue("Rod bean1 is a TestBean", (o instanceof TestBean));
        Object o1 = getBeanFactory().getBean("rod");
        Assert.assertTrue("Rod bean2 is a TestBean", (o1 instanceof TestBean));
        Assert.assertTrue("Object equals applies", (o == o1));
    }

    @Test
    public void prototypeInstancesAreIndependent() {
        TestBean tb1 = ((TestBean) (getBeanFactory().getBean("kathy")));
        TestBean tb2 = ((TestBean) (getBeanFactory().getBean("kathy")));
        Assert.assertTrue("ref equal DOES NOT apply", (tb1 != tb2));
        Assert.assertTrue("object equal true", tb1.equals(tb2));
        tb1.setAge(1);
        tb2.setAge(2);
        Assert.assertTrue("1 age independent = 1", ((tb1.getAge()) == 1));
        Assert.assertTrue("2 age independent = 2", ((tb2.getAge()) == 2));
        Assert.assertTrue("object equal now false", (!(tb1.equals(tb2))));
    }

    @Test(expected = BeansException.class)
    public void notThere() {
        Assert.assertFalse(getBeanFactory().containsBean("Mr Squiggle"));
        getBeanFactory().getBean("Mr Squiggle");
    }

    @Test
    public void validEmpty() {
        Object o = getBeanFactory().getBean("validEmpty");
        Assert.assertTrue("validEmpty bean is a TestBean", (o instanceof TestBean));
        TestBean ve = ((TestBean) (o));
        Assert.assertTrue("Valid empty has defaults", ((((ve.getName()) == null) && ((ve.getAge()) == 0)) && ((ve.getSpouse()) == null)));
    }

    @Test
    public void grandparentDefinitionFoundInBeanFactory() throws Exception {
        TestBean dad = ((TestBean) (getBeanFactory().getBean("father")));
        Assert.assertTrue("Dad has correct name", dad.getName().equals("Albert"));
    }

    @Test
    public void factorySingleton() throws Exception {
        Assert.assertTrue(getBeanFactory().isSingleton("&singletonFactory"));
        Assert.assertTrue(getBeanFactory().isSingleton("singletonFactory"));
        TestBean tb = ((TestBean) (getBeanFactory().getBean("singletonFactory")));
        Assert.assertTrue(("Singleton from factory has correct name, not " + (tb.getName())), tb.getName().equals(DummyFactory.SINGLETON_NAME));
        DummyFactory factory = ((DummyFactory) (getBeanFactory().getBean("&singletonFactory")));
        TestBean tb2 = ((TestBean) (getBeanFactory().getBean("singletonFactory")));
        Assert.assertTrue("Singleton references ==", (tb == tb2));
        Assert.assertTrue("FactoryBean is BeanFactoryAware", ((factory.getBeanFactory()) != null));
    }

    @Test
    public void factoryPrototype() throws Exception {
        Assert.assertTrue(getBeanFactory().isSingleton("&prototypeFactory"));
        Assert.assertFalse(getBeanFactory().isSingleton("prototypeFactory"));
        TestBean tb = ((TestBean) (getBeanFactory().getBean("prototypeFactory")));
        Assert.assertTrue((!(tb.getName().equals(DummyFactory.SINGLETON_NAME))));
        TestBean tb2 = ((TestBean) (getBeanFactory().getBean("prototypeFactory")));
        Assert.assertTrue("Prototype references !=", (tb != tb2));
    }

    /**
     * Check that we can get the factory bean itself.
     * This is only possible if we're dealing with a factory
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getFactoryItself() throws Exception {
        Assert.assertNotNull(getBeanFactory().getBean("&singletonFactory"));
    }

    /**
     * Check that afterPropertiesSet gets called on factory
     *
     * @throws Exception
     * 		
     */
    @Test
    public void factoryIsInitialized() throws Exception {
        TestBean tb = ((TestBean) (getBeanFactory().getBean("singletonFactory")));
        Assert.assertNotNull(tb);
        DummyFactory factory = ((DummyFactory) (getBeanFactory().getBean("&singletonFactory")));
        Assert.assertTrue("Factory was initialized because it implemented InitializingBean", factory.wasInitialized());
    }

    /**
     * It should be illegal to dereference a normal bean as a factory.
     */
    @Test(expected = BeanIsNotAFactoryException.class)
    public void rejectsFactoryGetOnNormalBean() {
        getBeanFactory().getBean("&rod");
    }

    // TODO: refactor in AbstractBeanFactory (tests for AbstractBeanFactory)
    // and rename this class
    @Test
    public void aliasing() {
        BeanFactory bf = getBeanFactory();
        if (!(bf instanceof ConfigurableBeanFactory)) {
            return;
        }
        ConfigurableBeanFactory cbf = ((ConfigurableBeanFactory) (bf));
        String alias = "rods alias";
        try {
            cbf.getBean(alias);
            Assert.fail("Shouldn't permit factory get on normal bean");
        } catch (NoSuchBeanDefinitionException ex) {
            // Ok
            Assert.assertTrue(alias.equals(ex.getBeanName()));
        }
        // Create alias
        cbf.registerAlias("rod", alias);
        Object rod = getBeanFactory().getBean("rod");
        Object aliasRod = getBeanFactory().getBean(alias);
        Assert.assertTrue((rod == aliasRod));
    }

    public static class TestBeanEditor extends PropertyEditorSupport {
        @Override
        public void setAsText(String text) {
            TestBean tb = new TestBean();
            StringTokenizer st = new StringTokenizer(text, "_");
            tb.setName(st.nextToken());
            tb.setAge(Integer.parseInt(st.nextToken()));
            setValue(tb);
        }
    }
}

