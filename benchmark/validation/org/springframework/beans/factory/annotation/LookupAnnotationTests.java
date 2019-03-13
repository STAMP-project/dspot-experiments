/**
 * Copyright 2002-2016 the original author or authors.
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


import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.tests.sample.beans.TestBean;


/**
 *
 *
 * @author Karl Pietrzak
 * @author Juergen Hoeller
 */
public class LookupAnnotationTests {
    private DefaultListableBeanFactory beanFactory;

    @Test
    public void testWithoutConstructorArg() {
        LookupAnnotationTests.AbstractBean bean = ((LookupAnnotationTests.AbstractBean) (beanFactory.getBean("abstractBean")));
        Assert.assertNotNull(bean);
        Object expected = bean.get();
        Assert.assertEquals(TestBean.class, expected.getClass());
        Assert.assertSame(bean, beanFactory.getBean(LookupAnnotationTests.BeanConsumer.class).abstractBean);
    }

    @Test
    public void testWithOverloadedArg() {
        LookupAnnotationTests.AbstractBean bean = ((LookupAnnotationTests.AbstractBean) (beanFactory.getBean("abstractBean")));
        Assert.assertNotNull(bean);
        TestBean expected = bean.get("haha");
        Assert.assertEquals(TestBean.class, expected.getClass());
        Assert.assertEquals("haha", expected.getName());
        Assert.assertSame(bean, beanFactory.getBean(LookupAnnotationTests.BeanConsumer.class).abstractBean);
    }

    @Test
    public void testWithOneConstructorArg() {
        LookupAnnotationTests.AbstractBean bean = ((LookupAnnotationTests.AbstractBean) (beanFactory.getBean("abstractBean")));
        Assert.assertNotNull(bean);
        TestBean expected = bean.getOneArgument("haha");
        Assert.assertEquals(TestBean.class, expected.getClass());
        Assert.assertEquals("haha", expected.getName());
        Assert.assertSame(bean, beanFactory.getBean(LookupAnnotationTests.BeanConsumer.class).abstractBean);
    }

    @Test
    public void testWithTwoConstructorArg() {
        LookupAnnotationTests.AbstractBean bean = ((LookupAnnotationTests.AbstractBean) (beanFactory.getBean("abstractBean")));
        Assert.assertNotNull(bean);
        TestBean expected = bean.getTwoArguments("haha", 72);
        Assert.assertEquals(TestBean.class, expected.getClass());
        Assert.assertEquals("haha", expected.getName());
        Assert.assertEquals(72, expected.getAge());
        Assert.assertSame(bean, beanFactory.getBean(LookupAnnotationTests.BeanConsumer.class).abstractBean);
    }

    @Test
    public void testWithThreeArgsShouldFail() {
        LookupAnnotationTests.AbstractBean bean = ((LookupAnnotationTests.AbstractBean) (beanFactory.getBean("abstractBean")));
        Assert.assertNotNull(bean);
        try {
            bean.getThreeArguments("name", 1, 2);
            Assert.fail("TestBean does not have a three arg constructor so this should not have worked");
        } catch (AbstractMethodError ex) {
        }
        Assert.assertSame(bean, beanFactory.getBean(LookupAnnotationTests.BeanConsumer.class).abstractBean);
    }

    @Test
    public void testWithEarlyInjection() {
        LookupAnnotationTests.AbstractBean bean = beanFactory.getBean("beanConsumer", LookupAnnotationTests.BeanConsumer.class).abstractBean;
        Assert.assertNotNull(bean);
        Object expected = bean.get();
        Assert.assertEquals(TestBean.class, expected.getClass());
        Assert.assertSame(bean, beanFactory.getBean(LookupAnnotationTests.BeanConsumer.class).abstractBean);
    }

    public abstract static class AbstractBean {
        @Lookup
        public abstract TestBean get();

        @Lookup
        public abstract TestBean get(String name);// overloaded


        @Lookup
        public abstract TestBean getOneArgument(String name);

        @Lookup
        public abstract TestBean getTwoArguments(String name, int age);

        public abstract TestBean getThreeArguments(String name, int age, int anotherArg);
    }

    public static class BeanConsumer {
        @Autowired
        LookupAnnotationTests.AbstractBean abstractBean;
    }
}

