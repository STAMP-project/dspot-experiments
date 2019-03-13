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
package org.springframework.beans.factory.xml;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.tests.sample.beans.TestBean;


/**
 *
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 */
public class FactoryMethodTests {
    @Test
    public void testFactoryMethodsSingletonOnTargetClass() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(xbf);
        reader.loadBeanDefinitions(new ClassPathResource("factory-methods.xml", getClass()));
        TestBean tb = ((TestBean) (xbf.getBean("defaultTestBean")));
        Assert.assertEquals("defaultInstance", tb.getName());
        Assert.assertEquals(1, tb.getAge());
        FactoryMethods fm = ((FactoryMethods) (xbf.getBean("default")));
        Assert.assertEquals(0, fm.getNum());
        Assert.assertEquals("default", fm.getName());
        Assert.assertEquals("defaultInstance", fm.getTestBean().getName());
        Assert.assertEquals("setterString", fm.getStringValue());
        fm = ((FactoryMethods) (xbf.getBean("testBeanOnly")));
        Assert.assertEquals(0, fm.getNum());
        Assert.assertEquals("default", fm.getName());
        // This comes from the test bean
        Assert.assertEquals("Juergen", fm.getTestBean().getName());
        fm = ((FactoryMethods) (xbf.getBean("full")));
        Assert.assertEquals(27, fm.getNum());
        Assert.assertEquals("gotcha", fm.getName());
        Assert.assertEquals("Juergen", fm.getTestBean().getName());
        FactoryMethods fm2 = ((FactoryMethods) (xbf.getBean("full")));
        Assert.assertSame(fm, fm2);
        xbf.destroySingletons();
        Assert.assertTrue(tb.wasDestroyed());
    }

    @Test
    public void testFactoryMethodsWithInvalidDestroyMethod() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(xbf);
        reader.loadBeanDefinitions(new ClassPathResource("factory-methods.xml", getClass()));
        try {
            xbf.getBean("defaultTestBeanWithInvalidDestroyMethod");
            Assert.fail("Should have thrown BeanCreationException");
        } catch (BeanCreationException ex) {
            // expected
        }
    }

    @Test
    public void testFactoryMethodsWithNullInstance() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(xbf);
        reader.loadBeanDefinitions(new ClassPathResource("factory-methods.xml", getClass()));
        Assert.assertEquals("null", xbf.getBean("null").toString());
        try {
            xbf.getBean("nullWithProperty");
            Assert.fail("Should have thrown BeanCreationException");
        } catch (BeanCreationException ex) {
            // expected
        }
    }

    @Test
    public void testFactoryMethodsWithNullValue() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(xbf);
        reader.loadBeanDefinitions(new ClassPathResource("factory-methods.xml", getClass()));
        FactoryMethods fm = ((FactoryMethods) (xbf.getBean("fullWithNull")));
        Assert.assertEquals(27, fm.getNum());
        Assert.assertEquals(null, fm.getName());
        Assert.assertEquals("Juergen", fm.getTestBean().getName());
        fm = ((FactoryMethods) (xbf.getBean("fullWithGenericNull")));
        Assert.assertEquals(27, fm.getNum());
        Assert.assertEquals(null, fm.getName());
        Assert.assertEquals("Juergen", fm.getTestBean().getName());
        fm = ((FactoryMethods) (xbf.getBean("fullWithNamedNull")));
        Assert.assertEquals(27, fm.getNum());
        Assert.assertEquals(null, fm.getName());
        Assert.assertEquals("Juergen", fm.getTestBean().getName());
    }

    @Test
    public void testFactoryMethodsWithAutowire() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(xbf);
        reader.loadBeanDefinitions(new ClassPathResource("factory-methods.xml", getClass()));
        FactoryMethods fm = ((FactoryMethods) (xbf.getBean("fullWithAutowire")));
        Assert.assertEquals(27, fm.getNum());
        Assert.assertEquals("gotchaAutowired", fm.getName());
        Assert.assertEquals("Juergen", fm.getTestBean().getName());
    }

    @Test
    public void testProtectedFactoryMethod() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(xbf);
        reader.loadBeanDefinitions(new ClassPathResource("factory-methods.xml", getClass()));
        TestBean tb = ((TestBean) (xbf.getBean("defaultTestBean.protected")));
        Assert.assertEquals(1, tb.getAge());
    }

    @Test
    public void testPrivateFactoryMethod() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(xbf);
        reader.loadBeanDefinitions(new ClassPathResource("factory-methods.xml", getClass()));
        TestBean tb = ((TestBean) (xbf.getBean("defaultTestBean.private")));
        Assert.assertEquals(1, tb.getAge());
    }

    @Test
    public void testFactoryMethodsPrototypeOnTargetClass() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(xbf);
        reader.loadBeanDefinitions(new ClassPathResource("factory-methods.xml", getClass()));
        FactoryMethods fm = ((FactoryMethods) (xbf.getBean("defaultPrototype")));
        FactoryMethods fm2 = ((FactoryMethods) (xbf.getBean("defaultPrototype")));
        Assert.assertEquals(0, fm.getNum());
        Assert.assertEquals("default", fm.getName());
        Assert.assertEquals("defaultInstance", fm.getTestBean().getName());
        Assert.assertEquals("setterString", fm.getStringValue());
        Assert.assertEquals(fm.getNum(), fm2.getNum());
        Assert.assertEquals(fm.getStringValue(), fm2.getStringValue());
        // The TestBean is created separately for each bean
        Assert.assertNotSame(fm.getTestBean(), fm2.getTestBean());
        Assert.assertNotSame(fm, fm2);
        fm = ((FactoryMethods) (xbf.getBean("testBeanOnlyPrototype")));
        fm2 = ((FactoryMethods) (xbf.getBean("testBeanOnlyPrototype")));
        Assert.assertEquals(0, fm.getNum());
        Assert.assertEquals("default", fm.getName());
        // This comes from the test bean
        Assert.assertEquals("Juergen", fm.getTestBean().getName());
        Assert.assertEquals(fm.getNum(), fm2.getNum());
        Assert.assertEquals(fm.getStringValue(), fm2.getStringValue());
        // The TestBean reference is resolved to a prototype in the factory
        Assert.assertSame(fm.getTestBean(), fm2.getTestBean());
        Assert.assertNotSame(fm, fm2);
        fm = ((FactoryMethods) (xbf.getBean("fullPrototype")));
        fm2 = ((FactoryMethods) (xbf.getBean("fullPrototype")));
        Assert.assertEquals(27, fm.getNum());
        Assert.assertEquals("gotcha", fm.getName());
        Assert.assertEquals("Juergen", fm.getTestBean().getName());
        Assert.assertEquals(fm.getNum(), fm2.getNum());
        Assert.assertEquals(fm.getStringValue(), fm2.getStringValue());
        // The TestBean reference is resolved to a prototype in the factory
        Assert.assertSame(fm.getTestBean(), fm2.getTestBean());
        Assert.assertNotSame(fm, fm2);
    }

    /**
     * Tests where the static factory method is on a different class.
     */
    @Test
    public void testFactoryMethodsOnExternalClass() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(xbf);
        reader.loadBeanDefinitions(new ClassPathResource("factory-methods.xml", getClass()));
        Assert.assertEquals(TestBean.class, xbf.getType("externalFactoryMethodWithoutArgs"));
        Assert.assertEquals(TestBean.class, xbf.getType("externalFactoryMethodWithArgs"));
        String[] names = xbf.getBeanNamesForType(TestBean.class);
        Assert.assertTrue(Arrays.asList(names).contains("externalFactoryMethodWithoutArgs"));
        Assert.assertTrue(Arrays.asList(names).contains("externalFactoryMethodWithArgs"));
        TestBean tb = ((TestBean) (xbf.getBean("externalFactoryMethodWithoutArgs")));
        Assert.assertEquals(2, tb.getAge());
        Assert.assertEquals("Tristan", tb.getName());
        tb = ((TestBean) (xbf.getBean("externalFactoryMethodWithArgs")));
        Assert.assertEquals(33, tb.getAge());
        Assert.assertEquals("Rod", tb.getName());
        Assert.assertEquals(TestBean.class, xbf.getType("externalFactoryMethodWithoutArgs"));
        Assert.assertEquals(TestBean.class, xbf.getType("externalFactoryMethodWithArgs"));
        names = xbf.getBeanNamesForType(TestBean.class);
        Assert.assertTrue(Arrays.asList(names).contains("externalFactoryMethodWithoutArgs"));
        Assert.assertTrue(Arrays.asList(names).contains("externalFactoryMethodWithArgs"));
    }

    @Test
    public void testInstanceFactoryMethodWithoutArgs() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(xbf);
        reader.loadBeanDefinitions(new ClassPathResource("factory-methods.xml", getClass()));
        InstanceFactory.count = 0;
        xbf.preInstantiateSingletons();
        Assert.assertEquals(1, InstanceFactory.count);
        FactoryMethods fm = ((FactoryMethods) (xbf.getBean("instanceFactoryMethodWithoutArgs")));
        Assert.assertEquals("instanceFactory", fm.getTestBean().getName());
        Assert.assertEquals(1, InstanceFactory.count);
        FactoryMethods fm2 = ((FactoryMethods) (xbf.getBean("instanceFactoryMethodWithoutArgs")));
        Assert.assertEquals("instanceFactory", fm2.getTestBean().getName());
        Assert.assertSame(fm2, fm);
        Assert.assertEquals(1, InstanceFactory.count);
    }

    @Test
    public void testFactoryMethodNoMatchingStaticMethod() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(xbf);
        reader.loadBeanDefinitions(new ClassPathResource("factory-methods.xml", getClass()));
        try {
            xbf.getBean("noMatchPrototype");
            Assert.fail("No static method matched");
        } catch (BeanCreationException ex) {
            // Ok
        }
    }

    @Test
    public void testNonExistingFactoryMethod() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(xbf);
        reader.loadBeanDefinitions(new ClassPathResource("factory-methods.xml", getClass()));
        try {
            xbf.getBean("invalidPrototype");
            Assert.fail("Should have thrown BeanCreationException");
        } catch (BeanCreationException ex) {
            Assert.assertTrue(ex.getMessage().contains("nonExisting(TestBean)"));
        }
    }

    @Test
    public void testFactoryMethodArgumentsForNonExistingMethod() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(xbf);
        reader.loadBeanDefinitions(new ClassPathResource("factory-methods.xml", getClass()));
        try {
            xbf.getBean("invalidPrototype", new TestBean());
            Assert.fail("Should have thrown BeanCreationException");
        } catch (BeanCreationException ex) {
            Assert.assertTrue(ex.getMessage().contains("nonExisting(TestBean)"));
        }
    }

    @Test
    public void testCanSpecifyFactoryMethodArgumentsOnFactoryMethodPrototype() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(xbf);
        reader.loadBeanDefinitions(new ClassPathResource("factory-methods.xml", getClass()));
        TestBean tbArg = new TestBean();
        tbArg.setName("arg1");
        TestBean tbArg2 = new TestBean();
        tbArg2.setName("arg2");
        FactoryMethods fm1 = ((FactoryMethods) (xbf.getBean("testBeanOnlyPrototype", tbArg)));
        Assert.assertEquals(0, fm1.getNum());
        Assert.assertEquals("default", fm1.getName());
        // This comes from the test bean
        Assert.assertEquals("arg1", fm1.getTestBean().getName());
        FactoryMethods fm2 = ((FactoryMethods) (xbf.getBean("testBeanOnlyPrototype", tbArg2)));
        Assert.assertEquals("arg2", fm2.getTestBean().getName());
        Assert.assertEquals(fm1.getNum(), fm2.getNum());
        Assert.assertEquals(fm2.getStringValue(), "testBeanOnlyPrototypeDISetterString");
        Assert.assertEquals(fm2.getStringValue(), fm2.getStringValue());
        // The TestBean reference is resolved to a prototype in the factory
        Assert.assertSame(fm2.getTestBean(), fm2.getTestBean());
        Assert.assertNotSame(fm1, fm2);
        FactoryMethods fm3 = ((FactoryMethods) (xbf.getBean("testBeanOnlyPrototype", tbArg2, new Integer(1), "myName")));
        Assert.assertEquals(1, fm3.getNum());
        Assert.assertEquals("myName", fm3.getName());
        Assert.assertEquals("arg2", fm3.getTestBean().getName());
        FactoryMethods fm4 = ((FactoryMethods) (xbf.getBean("testBeanOnlyPrototype", tbArg)));
        Assert.assertEquals(0, fm4.getNum());
        Assert.assertEquals("default", fm4.getName());
        Assert.assertEquals("arg1", fm4.getTestBean().getName());
    }

    @Test
    public void testCanSpecifyFactoryMethodArgumentsOnSingleton() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(xbf);
        reader.loadBeanDefinitions(new ClassPathResource("factory-methods.xml", getClass()));
        // First getBean call triggers actual creation of the singleton bean
        TestBean tb = new TestBean();
        FactoryMethods fm1 = ((FactoryMethods) (xbf.getBean("testBeanOnly", tb)));
        Assert.assertSame(tb, fm1.getTestBean());
        FactoryMethods fm2 = ((FactoryMethods) (xbf.getBean("testBeanOnly", new TestBean())));
        Assert.assertSame(fm1, fm2);
        Assert.assertSame(tb, fm2.getTestBean());
    }

    @Test
    public void testCannotSpecifyFactoryMethodArgumentsOnSingletonAfterCreation() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(xbf);
        reader.loadBeanDefinitions(new ClassPathResource("factory-methods.xml", getClass()));
        // First getBean call triggers actual creation of the singleton bean
        FactoryMethods fm1 = ((FactoryMethods) (xbf.getBean("testBeanOnly")));
        TestBean tb = fm1.getTestBean();
        FactoryMethods fm2 = ((FactoryMethods) (xbf.getBean("testBeanOnly", new TestBean())));
        Assert.assertSame(fm1, fm2);
        Assert.assertSame(tb, fm2.getTestBean());
    }

    @Test
    public void testFactoryMethodWithDifferentReturnType() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(xbf);
        reader.loadBeanDefinitions(new ClassPathResource("factory-methods.xml", getClass()));
        // Check that listInstance is not considered a bean of type FactoryMethods.
        Assert.assertTrue(List.class.isAssignableFrom(xbf.getType("listInstance")));
        String[] names = xbf.getBeanNamesForType(FactoryMethods.class);
        Assert.assertTrue((!(Arrays.asList(names).contains("listInstance"))));
        names = xbf.getBeanNamesForType(List.class);
        Assert.assertTrue(Arrays.asList(names).contains("listInstance"));
        xbf.preInstantiateSingletons();
        Assert.assertTrue(List.class.isAssignableFrom(xbf.getType("listInstance")));
        names = xbf.getBeanNamesForType(FactoryMethods.class);
        Assert.assertTrue((!(Arrays.asList(names).contains("listInstance"))));
        names = xbf.getBeanNamesForType(List.class);
        Assert.assertTrue(Arrays.asList(names).contains("listInstance"));
        List<?> list = ((List<?>) (xbf.getBean("listInstance")));
        Assert.assertEquals(Collections.EMPTY_LIST, list);
    }

    @Test
    public void testFactoryMethodForJavaMailSession() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(xbf);
        reader.loadBeanDefinitions(new ClassPathResource("factory-methods.xml", getClass()));
        MailSession session = ((MailSession) (xbf.getBean("javaMailSession")));
        Assert.assertEquals("someuser", session.getProperty("mail.smtp.user"));
        Assert.assertEquals("somepw", session.getProperty("mail.smtp.password"));
    }
}

