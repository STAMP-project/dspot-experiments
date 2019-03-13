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


import BeanDefinition.SCOPE_PROTOTYPE;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.tests.sample.beans.TestBean;


/**
 *
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public class BeanDefinitionBuilderTests {
    @Test
    public void beanClassWithSimpleProperty() {
        String[] dependsOn = new String[]{ "A", "B", "C" };
        BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(TestBean.class);
        bdb.setScope(SCOPE_PROTOTYPE);
        bdb.addPropertyReference("age", "15");
        for (int i = 0; i < (dependsOn.length); i++) {
            bdb.addDependsOn(dependsOn[i]);
        }
        RootBeanDefinition rbd = ((RootBeanDefinition) (bdb.getBeanDefinition()));
        Assert.assertFalse(rbd.isSingleton());
        Assert.assertEquals(TestBean.class, rbd.getBeanClass());
        Assert.assertTrue("Depends on was added", Arrays.equals(dependsOn, rbd.getDependsOn()));
        Assert.assertTrue(rbd.getPropertyValues().contains("age"));
    }

    @Test
    public void beanClassWithFactoryMethod() {
        BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(TestBean.class, "create");
        RootBeanDefinition rbd = ((RootBeanDefinition) (bdb.getBeanDefinition()));
        Assert.assertTrue(rbd.hasBeanClass());
        Assert.assertEquals(TestBean.class, rbd.getBeanClass());
        Assert.assertEquals("create", rbd.getFactoryMethodName());
    }

    @Test
    public void beanClassName() {
        BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(TestBean.class.getName());
        RootBeanDefinition rbd = ((RootBeanDefinition) (bdb.getBeanDefinition()));
        Assert.assertFalse(rbd.hasBeanClass());
        Assert.assertEquals(TestBean.class.getName(), rbd.getBeanClassName());
    }

    @Test
    public void beanClassNameWithFactoryMethod() {
        BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(TestBean.class.getName(), "create");
        RootBeanDefinition rbd = ((RootBeanDefinition) (bdb.getBeanDefinition()));
        Assert.assertFalse(rbd.hasBeanClass());
        Assert.assertEquals(TestBean.class.getName(), rbd.getBeanClassName());
        Assert.assertEquals("create", rbd.getFactoryMethodName());
    }
}

