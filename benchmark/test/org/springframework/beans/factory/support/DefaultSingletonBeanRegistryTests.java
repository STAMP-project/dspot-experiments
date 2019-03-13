/**
 * Copyright 2002-2014 the original author or authors.
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
import org.springframework.beans.BeansException;
import org.springframework.tests.sample.beans.DerivedTestBean;
import org.springframework.tests.sample.beans.TestBean;


/**
 *
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 04.07.2006
 */
public class DefaultSingletonBeanRegistryTests {
    @Test
    public void testSingletons() {
        DefaultSingletonBeanRegistry beanRegistry = new DefaultSingletonBeanRegistry();
        TestBean tb = new TestBean();
        beanRegistry.registerSingleton("tb", tb);
        Assert.assertSame(tb, beanRegistry.getSingleton("tb"));
        TestBean tb2 = ((TestBean) (beanRegistry.getSingleton("tb2", new org.springframework.beans.factory.ObjectFactory<Object>() {
            @Override
            public Object getObject() throws BeansException {
                return new TestBean();
            }
        })));
        Assert.assertSame(tb2, beanRegistry.getSingleton("tb2"));
        Assert.assertSame(tb, beanRegistry.getSingleton("tb"));
        Assert.assertSame(tb2, beanRegistry.getSingleton("tb2"));
        Assert.assertEquals(2, beanRegistry.getSingletonCount());
        String[] names = beanRegistry.getSingletonNames();
        Assert.assertEquals(2, names.length);
        Assert.assertEquals("tb", names[0]);
        Assert.assertEquals("tb2", names[1]);
        beanRegistry.destroySingletons();
        Assert.assertEquals(0, beanRegistry.getSingletonCount());
        Assert.assertEquals(0, beanRegistry.getSingletonNames().length);
    }

    @Test
    public void testDisposableBean() {
        DefaultSingletonBeanRegistry beanRegistry = new DefaultSingletonBeanRegistry();
        DerivedTestBean tb = new DerivedTestBean();
        beanRegistry.registerSingleton("tb", tb);
        beanRegistry.registerDisposableBean("tb", tb);
        Assert.assertSame(tb, beanRegistry.getSingleton("tb"));
        Assert.assertSame(tb, beanRegistry.getSingleton("tb"));
        Assert.assertEquals(1, beanRegistry.getSingletonCount());
        String[] names = beanRegistry.getSingletonNames();
        Assert.assertEquals(1, names.length);
        Assert.assertEquals("tb", names[0]);
        Assert.assertFalse(tb.wasDestroyed());
        beanRegistry.destroySingletons();
        Assert.assertEquals(0, beanRegistry.getSingletonCount());
        Assert.assertEquals(0, beanRegistry.getSingletonNames().length);
        Assert.assertTrue(tb.wasDestroyed());
    }

    @Test
    public void testDependentRegistration() {
        DefaultSingletonBeanRegistry beanRegistry = new DefaultSingletonBeanRegistry();
        beanRegistry.registerDependentBean("a", "b");
        beanRegistry.registerDependentBean("b", "c");
        beanRegistry.registerDependentBean("c", "b");
        Assert.assertTrue(beanRegistry.isDependent("a", "b"));
        Assert.assertTrue(beanRegistry.isDependent("b", "c"));
        Assert.assertTrue(beanRegistry.isDependent("c", "b"));
        Assert.assertTrue(beanRegistry.isDependent("a", "c"));
        Assert.assertFalse(beanRegistry.isDependent("c", "a"));
        Assert.assertFalse(beanRegistry.isDependent("b", "a"));
        Assert.assertFalse(beanRegistry.isDependent("a", "a"));
        Assert.assertTrue(beanRegistry.isDependent("b", "b"));
        Assert.assertTrue(beanRegistry.isDependent("c", "c"));
    }
}

