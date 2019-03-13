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
package org.springframework.beans.factory.support;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.tests.sample.beans.TestBean;


/**
 *
 *
 * @author Juergen Hoeller
 */
public class BeanDefinitionTests {
    @Test
    public void beanDefinitionEquality() {
        RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
        bd.setAbstract(true);
        bd.setLazyInit(true);
        bd.setScope("request");
        RootBeanDefinition otherBd = new RootBeanDefinition(TestBean.class);
        Assert.assertTrue((!(bd.equals(otherBd))));
        Assert.assertTrue((!(otherBd.equals(bd))));
        otherBd.setAbstract(true);
        otherBd.setLazyInit(true);
        otherBd.setScope("request");
        Assert.assertTrue(bd.equals(otherBd));
        Assert.assertTrue(otherBd.equals(bd));
        Assert.assertTrue(((bd.hashCode()) == (otherBd.hashCode())));
    }

    @Test
    public void beanDefinitionEqualityWithPropertyValues() {
        RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
        bd.getPropertyValues().add("name", "myName");
        bd.getPropertyValues().add("age", "99");
        RootBeanDefinition otherBd = new RootBeanDefinition(TestBean.class);
        otherBd.getPropertyValues().add("name", "myName");
        Assert.assertTrue((!(bd.equals(otherBd))));
        Assert.assertTrue((!(otherBd.equals(bd))));
        otherBd.getPropertyValues().add("age", "11");
        Assert.assertTrue((!(bd.equals(otherBd))));
        Assert.assertTrue((!(otherBd.equals(bd))));
        otherBd.getPropertyValues().add("age", "99");
        Assert.assertTrue(bd.equals(otherBd));
        Assert.assertTrue(otherBd.equals(bd));
        Assert.assertTrue(((bd.hashCode()) == (otherBd.hashCode())));
    }

    @Test
    public void beanDefinitionEqualityWithConstructorArguments() {
        RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
        bd.getConstructorArgumentValues().addGenericArgumentValue("test");
        bd.getConstructorArgumentValues().addIndexedArgumentValue(1, new Integer(5));
        RootBeanDefinition otherBd = new RootBeanDefinition(TestBean.class);
        otherBd.getConstructorArgumentValues().addGenericArgumentValue("test");
        Assert.assertTrue((!(bd.equals(otherBd))));
        Assert.assertTrue((!(otherBd.equals(bd))));
        otherBd.getConstructorArgumentValues().addIndexedArgumentValue(1, new Integer(9));
        Assert.assertTrue((!(bd.equals(otherBd))));
        Assert.assertTrue((!(otherBd.equals(bd))));
        otherBd.getConstructorArgumentValues().addIndexedArgumentValue(1, new Integer(5));
        Assert.assertTrue(bd.equals(otherBd));
        Assert.assertTrue(otherBd.equals(bd));
        Assert.assertTrue(((bd.hashCode()) == (otherBd.hashCode())));
    }

    @Test
    public void beanDefinitionEqualityWithTypedConstructorArguments() {
        RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
        bd.getConstructorArgumentValues().addGenericArgumentValue("test", "int");
        bd.getConstructorArgumentValues().addIndexedArgumentValue(1, new Integer(5), "long");
        RootBeanDefinition otherBd = new RootBeanDefinition(TestBean.class);
        otherBd.getConstructorArgumentValues().addGenericArgumentValue("test", "int");
        otherBd.getConstructorArgumentValues().addIndexedArgumentValue(1, new Integer(5));
        Assert.assertTrue((!(bd.equals(otherBd))));
        Assert.assertTrue((!(otherBd.equals(bd))));
        otherBd.getConstructorArgumentValues().addIndexedArgumentValue(1, new Integer(5), "int");
        Assert.assertTrue((!(bd.equals(otherBd))));
        Assert.assertTrue((!(otherBd.equals(bd))));
        otherBd.getConstructorArgumentValues().addIndexedArgumentValue(1, new Integer(5), "long");
        Assert.assertTrue(bd.equals(otherBd));
        Assert.assertTrue(otherBd.equals(bd));
        Assert.assertTrue(((bd.hashCode()) == (otherBd.hashCode())));
    }

    @Test
    public void beanDefinitionHolderEquality() {
        RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
        bd.setAbstract(true);
        bd.setLazyInit(true);
        bd.setScope("request");
        BeanDefinitionHolder holder = new BeanDefinitionHolder(bd, "bd");
        RootBeanDefinition otherBd = new RootBeanDefinition(TestBean.class);
        Assert.assertTrue((!(bd.equals(otherBd))));
        Assert.assertTrue((!(otherBd.equals(bd))));
        otherBd.setAbstract(true);
        otherBd.setLazyInit(true);
        otherBd.setScope("request");
        BeanDefinitionHolder otherHolder = new BeanDefinitionHolder(bd, "bd");
        Assert.assertTrue(holder.equals(otherHolder));
        Assert.assertTrue(otherHolder.equals(holder));
        Assert.assertTrue(((holder.hashCode()) == (otherHolder.hashCode())));
    }

    @Test
    public void beanDefinitionMerging() {
        RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
        bd.getConstructorArgumentValues().addGenericArgumentValue("test");
        bd.getConstructorArgumentValues().addIndexedArgumentValue(1, new Integer(5));
        bd.getPropertyValues().add("name", "myName");
        bd.getPropertyValues().add("age", "99");
        bd.setQualifiedElement(getClass());
        GenericBeanDefinition childBd = new GenericBeanDefinition();
        childBd.setParentName("bd");
        RootBeanDefinition mergedBd = new RootBeanDefinition(bd);
        mergedBd.overrideFrom(childBd);
        Assert.assertEquals(2, mergedBd.getConstructorArgumentValues().getArgumentCount());
        Assert.assertEquals(2, mergedBd.getPropertyValues().size());
        Assert.assertEquals(bd, mergedBd);
        mergedBd.getConstructorArgumentValues().getArgumentValue(1, null).setValue(new Integer(9));
        Assert.assertEquals(new Integer(5), bd.getConstructorArgumentValues().getArgumentValue(1, null).getValue());
        Assert.assertEquals(getClass(), bd.getQualifiedElement());
    }
}

