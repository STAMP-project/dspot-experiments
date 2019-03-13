/**
 * Copyright 2002-2012 the original author or authors.
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
package org.springframework.beans.factory;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Written with the intention of reproducing SPR-7318.
 *
 * @author Chris Beams
 */
public class FactoryBeanLookupTests {
    private BeanFactory beanFactory;

    @Test
    public void factoryBeanLookupByNameDereferencing() {
        Object fooFactory = beanFactory.getBean("&fooFactory");
        Assert.assertThat(fooFactory, CoreMatchers.instanceOf(FooFactoryBean.class));
    }

    @Test
    public void factoryBeanLookupByType() {
        FooFactoryBean fooFactory = beanFactory.getBean(FooFactoryBean.class);
        Assert.assertNotNull(fooFactory);
    }

    @Test
    public void factoryBeanLookupByTypeAndNameDereference() {
        FooFactoryBean fooFactory = beanFactory.getBean("&fooFactory", FooFactoryBean.class);
        Assert.assertNotNull(fooFactory);
    }

    @Test
    public void factoryBeanObjectLookupByName() {
        Object fooFactory = beanFactory.getBean("fooFactory");
        Assert.assertThat(fooFactory, CoreMatchers.instanceOf(Foo.class));
    }

    @Test
    public void factoryBeanObjectLookupByNameAndType() {
        Foo foo = beanFactory.getBean("fooFactory", Foo.class);
        Assert.assertNotNull(foo);
    }
}

