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
package org.springframework.beans.factory.support;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.tests.sample.beans.TestBean;


/**
 *
 *
 * @author Karl Pietrzak
 * @author Juergen Hoeller
 */
public class LookupMethodTests {
    private DefaultListableBeanFactory beanFactory;

    @Test
    public void testWithoutConstructorArg() {
        LookupMethodTests.AbstractBean bean = ((LookupMethodTests.AbstractBean) (beanFactory.getBean("abstractBean")));
        Assert.assertNotNull(bean);
        Object expected = bean.get();
        Assert.assertEquals(TestBean.class, expected.getClass());
    }

    @Test
    public void testWithOverloadedArg() {
        LookupMethodTests.AbstractBean bean = ((LookupMethodTests.AbstractBean) (beanFactory.getBean("abstractBean")));
        Assert.assertNotNull(bean);
        TestBean expected = bean.get("haha");
        Assert.assertEquals(TestBean.class, expected.getClass());
        Assert.assertEquals("haha", expected.getName());
    }

    @Test
    public void testWithOneConstructorArg() {
        LookupMethodTests.AbstractBean bean = ((LookupMethodTests.AbstractBean) (beanFactory.getBean("abstractBean")));
        Assert.assertNotNull(bean);
        TestBean expected = bean.getOneArgument("haha");
        Assert.assertEquals(TestBean.class, expected.getClass());
        Assert.assertEquals("haha", expected.getName());
    }

    @Test
    public void testWithTwoConstructorArg() {
        LookupMethodTests.AbstractBean bean = ((LookupMethodTests.AbstractBean) (beanFactory.getBean("abstractBean")));
        Assert.assertNotNull(bean);
        TestBean expected = bean.getTwoArguments("haha", 72);
        Assert.assertEquals(TestBean.class, expected.getClass());
        Assert.assertEquals("haha", expected.getName());
        Assert.assertEquals(72, expected.getAge());
    }

    @Test
    public void testWithThreeArgsShouldFail() {
        LookupMethodTests.AbstractBean bean = ((LookupMethodTests.AbstractBean) (beanFactory.getBean("abstractBean")));
        Assert.assertNotNull(bean);
        try {
            bean.getThreeArguments("name", 1, 2);
            Assert.fail("TestBean does not have a three arg constructor so this should not have worked");
        } catch (AbstractMethodError ex) {
        }
    }

    @Test
    public void testWithOverriddenLookupMethod() {
        LookupMethodTests.AbstractBean bean = ((LookupMethodTests.AbstractBean) (beanFactory.getBean("extendedBean")));
        Assert.assertNotNull(bean);
        TestBean expected = bean.getOneArgument("haha");
        Assert.assertEquals(TestBean.class, expected.getClass());
        Assert.assertEquals("haha", expected.getName());
        Assert.assertTrue(expected.isJedi());
    }

    public abstract static class AbstractBean {
        public abstract TestBean get();

        public abstract TestBean get(String name);// overloaded


        public abstract TestBean getOneArgument(String name);

        public abstract TestBean getTwoArguments(String name, int age);

        public abstract TestBean getThreeArguments(String name, int age, int anotherArg);
    }
}

