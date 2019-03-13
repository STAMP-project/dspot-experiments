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
package org.springframework.context.annotation;


import ClassUtils.CGLIB_CLASS_SEPARATOR;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.scope.ScopedObject;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation4.DependencyBean;
import org.springframework.context.annotation4.FactoryMethodComponent;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.tests.context.SimpleMapScope;


/**
 *
 *
 * @author Mark Pollack
 * @author Juergen Hoeller
 */
public class ClassPathFactoryBeanDefinitionScannerTests {
    private static final String BASE_PACKAGE = FactoryMethodComponent.class.getPackage().getName();

    @Test
    public void testSingletonScopedFactoryMethod() {
        GenericApplicationContext context = new GenericApplicationContext();
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(context);
        context.getBeanFactory().registerScope("request", new SimpleMapScope());
        scanner.scan(ClassPathFactoryBeanDefinitionScannerTests.BASE_PACKAGE);
        context.registerBeanDefinition("clientBean", new RootBeanDefinition(ClassPathFactoryBeanDefinitionScannerTests.QualifiedClientBean.class));
        context.refresh();
        FactoryMethodComponent fmc = context.getBean("factoryMethodComponent", FactoryMethodComponent.class);
        Assert.assertFalse(fmc.getClass().getName().contains(CGLIB_CLASS_SEPARATOR));
        TestBean tb = ((TestBean) (context.getBean("publicInstance")));// 2

        Assert.assertEquals("publicInstance", getName());
        TestBean tb2 = ((TestBean) (context.getBean("publicInstance")));// 2

        Assert.assertEquals("publicInstance", getName());
        Assert.assertSame(tb2, tb);
        tb = ((TestBean) (context.getBean("protectedInstance")));// 3

        Assert.assertEquals("protectedInstance", getName());
        Assert.assertSame(tb, context.getBean("protectedInstance"));
        Assert.assertEquals("0", getCountry());
        tb2 = context.getBean("protectedInstance", TestBean.class);// 3

        Assert.assertEquals("protectedInstance", getName());
        Assert.assertSame(tb2, tb);
        tb = context.getBean("privateInstance", TestBean.class);// 4

        Assert.assertEquals("privateInstance", getName());
        Assert.assertEquals(1, getAge());
        tb2 = context.getBean("privateInstance", TestBean.class);// 4

        Assert.assertEquals(2, getAge());
        Assert.assertNotSame(tb2, tb);
        Object bean = context.getBean("requestScopedInstance");// 5

        Assert.assertTrue(AopUtils.isCglibProxy(bean));
        Assert.assertTrue((bean instanceof ScopedObject));
        ClassPathFactoryBeanDefinitionScannerTests.QualifiedClientBean clientBean = context.getBean("clientBean", ClassPathFactoryBeanDefinitionScannerTests.QualifiedClientBean.class);
        Assert.assertSame(context.getBean("publicInstance"), clientBean.testBean);
        Assert.assertSame(context.getBean("dependencyBean"), clientBean.dependencyBean);
        Assert.assertSame(context, clientBean.applicationContext);
    }

    public static class QualifiedClientBean {
        @Autowired
        @Qualifier("public")
        public TestBean testBean;

        @Autowired
        public DependencyBean dependencyBean;

        @Autowired
        AbstractApplicationContext applicationContext;
    }
}

