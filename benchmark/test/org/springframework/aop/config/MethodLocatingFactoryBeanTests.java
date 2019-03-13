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
package org.springframework.aop.config;


import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.BeanFactory;


/**
 *
 *
 * @author Rick Evans
 * @author Chris Beams
 */
public class MethodLocatingFactoryBeanTests {
    private static final String BEAN_NAME = "string";

    private MethodLocatingFactoryBean factory;

    private BeanFactory beanFactory;

    @Test
    public void testIsSingleton() {
        Assert.assertTrue(factory.isSingleton());
    }

    @Test
    public void testGetObjectType() {
        Assert.assertEquals(Method.class, factory.getObjectType());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithNullTargetBeanName() {
        factory.setMethodName("toString()");
        factory.setBeanFactory(beanFactory);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithEmptyTargetBeanName() {
        factory.setTargetBeanName("");
        factory.setMethodName("toString()");
        factory.setBeanFactory(beanFactory);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithNullTargetMethodName() {
        factory.setTargetBeanName(MethodLocatingFactoryBeanTests.BEAN_NAME);
        factory.setBeanFactory(beanFactory);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithEmptyTargetMethodName() {
        factory.setTargetBeanName(MethodLocatingFactoryBeanTests.BEAN_NAME);
        factory.setMethodName("");
        factory.setBeanFactory(beanFactory);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWhenTargetBeanClassCannotBeResolved() {
        factory.setTargetBeanName(MethodLocatingFactoryBeanTests.BEAN_NAME);
        factory.setMethodName("toString()");
        factory.setBeanFactory(beanFactory);
        Mockito.verify(beanFactory).getType(MethodLocatingFactoryBeanTests.BEAN_NAME);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSunnyDayPath() throws Exception {
        BDDMockito.given(beanFactory.getType(MethodLocatingFactoryBeanTests.BEAN_NAME)).willReturn(((Class) (String.class)));
        factory.setTargetBeanName(MethodLocatingFactoryBeanTests.BEAN_NAME);
        factory.setMethodName("toString()");
        factory.setBeanFactory(beanFactory);
        Object result = factory.getObject();
        Assert.assertNotNull(result);
        Assert.assertTrue((result instanceof Method));
        Method method = ((Method) (result));
        Assert.assertEquals("Bingo", method.invoke("Bingo"));
    }

    @Test(expected = IllegalArgumentException.class)
    @SuppressWarnings("unchecked")
    public void testWhereMethodCannotBeResolved() {
        BDDMockito.given(beanFactory.getType(MethodLocatingFactoryBeanTests.BEAN_NAME)).willReturn(((Class) (String.class)));
        factory.setTargetBeanName(MethodLocatingFactoryBeanTests.BEAN_NAME);
        factory.setMethodName("loadOfOld()");
        factory.setBeanFactory(beanFactory);
    }
}

