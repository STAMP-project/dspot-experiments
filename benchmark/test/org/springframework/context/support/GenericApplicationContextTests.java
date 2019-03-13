/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.context.support;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ObjectUtils;


/**
 *
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 */
public class GenericApplicationContextTests {
    @Test
    public void getBeanForClass() {
        GenericApplicationContext ac = new GenericApplicationContext();
        ac.registerBeanDefinition("testBean", new RootBeanDefinition(String.class));
        ac.refresh();
        Assert.assertEquals("", ac.getBean("testBean"));
        Assert.assertSame(ac.getBean("testBean"), ac.getBean(String.class));
        Assert.assertSame(ac.getBean("testBean"), ac.getBean(CharSequence.class));
        try {
            Assert.assertSame(ac.getBean("testBean"), ac.getBean(Object.class));
            Assert.fail("Should have thrown NoUniqueBeanDefinitionException");
        } catch (NoUniqueBeanDefinitionException ex) {
            // expected
        }
    }

    @Test
    public void withSingletonSupplier() {
        GenericApplicationContext ac = new GenericApplicationContext();
        ac.registerBeanDefinition("testBean", new RootBeanDefinition(String.class, ac::toString));
        ac.refresh();
        Assert.assertSame(ac.getBean("testBean"), ac.getBean("testBean"));
        Assert.assertSame(ac.getBean("testBean"), ac.getBean(String.class));
        Assert.assertSame(ac.getBean("testBean"), ac.getBean(CharSequence.class));
        Assert.assertEquals(ac.toString(), ac.getBean("testBean"));
    }

    @Test
    public void withScopedSupplier() {
        GenericApplicationContext ac = new GenericApplicationContext();
        ac.registerBeanDefinition("testBean", new RootBeanDefinition(String.class, RootBeanDefinition.SCOPE_PROTOTYPE, ac::toString));
        ac.refresh();
        Assert.assertNotSame(ac.getBean("testBean"), ac.getBean("testBean"));
        Assert.assertEquals(ac.getBean("testBean"), ac.getBean(String.class));
        Assert.assertEquals(ac.getBean("testBean"), ac.getBean(CharSequence.class));
        Assert.assertEquals(ac.toString(), ac.getBean("testBean"));
    }

    @Test
    public void accessAfterClosing() {
        GenericApplicationContext ac = new GenericApplicationContext();
        ac.registerBeanDefinition("testBean", new RootBeanDefinition(String.class));
        ac.refresh();
        Assert.assertSame(ac.getBean("testBean"), ac.getBean(String.class));
        Assert.assertSame(ac.getAutowireCapableBeanFactory().getBean("testBean"), ac.getAutowireCapableBeanFactory().getBean(String.class));
        ac.close();
        try {
            Assert.assertSame(ac.getBean("testBean"), ac.getBean(String.class));
            Assert.fail("Should have thrown IllegalStateException");
        } catch (IllegalStateException ex) {
            // expected
        }
        try {
            Assert.assertSame(ac.getAutowireCapableBeanFactory().getBean("testBean"), ac.getAutowireCapableBeanFactory().getBean(String.class));
            Assert.fail("Should have thrown IllegalStateException");
        } catch (IllegalStateException ex) {
            // expected
        }
    }

    @Test
    public void individualBeans() {
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean(GenericApplicationContextTests.BeanA.class);
        context.registerBean(GenericApplicationContextTests.BeanB.class);
        context.registerBean(GenericApplicationContextTests.BeanC.class);
        context.refresh();
        Assert.assertSame(context.getBean(GenericApplicationContextTests.BeanB.class), context.getBean(GenericApplicationContextTests.BeanA.class).b);
        Assert.assertSame(context.getBean(GenericApplicationContextTests.BeanC.class), context.getBean(GenericApplicationContextTests.BeanA.class).c);
        Assert.assertSame(context, context.getBean(GenericApplicationContextTests.BeanB.class).applicationContext);
    }

    @Test
    public void individualNamedBeans() {
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean("a", GenericApplicationContextTests.BeanA.class);
        context.registerBean("b", GenericApplicationContextTests.BeanB.class);
        context.registerBean("c", GenericApplicationContextTests.BeanC.class);
        context.refresh();
        Assert.assertSame(context.getBean("b"), context.getBean("a", GenericApplicationContextTests.BeanA.class).b);
        Assert.assertSame(context.getBean("c"), context.getBean("a", GenericApplicationContextTests.BeanA.class).c);
        Assert.assertSame(context, context.getBean("b", GenericApplicationContextTests.BeanB.class).applicationContext);
    }

    @Test
    public void individualBeanWithSupplier() {
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean(GenericApplicationContextTests.BeanA.class, () -> new org.springframework.context.support.BeanA(context.getBean(.class), context.getBean(.class)));
        context.registerBean(GenericApplicationContextTests.BeanB.class, GenericApplicationContextTests.BeanB::new);
        context.registerBean(GenericApplicationContextTests.BeanC.class, GenericApplicationContextTests.BeanC::new);
        context.refresh();
        Assert.assertTrue(context.getBeanFactory().containsSingleton(GenericApplicationContextTests.BeanA.class.getName()));
        Assert.assertSame(context.getBean(GenericApplicationContextTests.BeanB.class), context.getBean(GenericApplicationContextTests.BeanA.class).b);
        Assert.assertSame(context.getBean(GenericApplicationContextTests.BeanC.class), context.getBean(GenericApplicationContextTests.BeanA.class).c);
        Assert.assertSame(context, context.getBean(GenericApplicationContextTests.BeanB.class).applicationContext);
        Assert.assertArrayEquals(new String[]{ GenericApplicationContextTests.BeanA.class.getName() }, context.getDefaultListableBeanFactory().getDependentBeans(GenericApplicationContextTests.BeanB.class.getName()));
        Assert.assertArrayEquals(new String[]{ GenericApplicationContextTests.BeanA.class.getName() }, context.getDefaultListableBeanFactory().getDependentBeans(GenericApplicationContextTests.BeanC.class.getName()));
    }

    @Test
    public void individualBeanWithSupplierAndCustomizer() {
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean(GenericApplicationContextTests.BeanA.class, () -> new org.springframework.context.support.BeanA(context.getBean(.class), context.getBean(.class)), ( bd) -> bd.setLazyInit(true));
        context.registerBean(GenericApplicationContextTests.BeanB.class, GenericApplicationContextTests.BeanB::new);
        context.registerBean(GenericApplicationContextTests.BeanC.class, GenericApplicationContextTests.BeanC::new);
        context.refresh();
        Assert.assertFalse(context.getBeanFactory().containsSingleton(GenericApplicationContextTests.BeanA.class.getName()));
        Assert.assertSame(context.getBean(GenericApplicationContextTests.BeanB.class), context.getBean(GenericApplicationContextTests.BeanA.class).b);
        Assert.assertSame(context.getBean(GenericApplicationContextTests.BeanC.class), context.getBean(GenericApplicationContextTests.BeanA.class).c);
        Assert.assertSame(context, context.getBean(GenericApplicationContextTests.BeanB.class).applicationContext);
    }

    @Test
    public void individualNamedBeanWithSupplier() {
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean("a", GenericApplicationContextTests.BeanA.class, () -> new org.springframework.context.support.BeanA(context.getBean(.class), context.getBean(.class)));
        context.registerBean("b", GenericApplicationContextTests.BeanB.class, GenericApplicationContextTests.BeanB::new);
        context.registerBean("c", GenericApplicationContextTests.BeanC.class, GenericApplicationContextTests.BeanC::new);
        context.refresh();
        Assert.assertTrue(context.getBeanFactory().containsSingleton("a"));
        Assert.assertSame(context.getBean("b", GenericApplicationContextTests.BeanB.class), context.getBean(GenericApplicationContextTests.BeanA.class).b);
        Assert.assertSame(context.getBean("c"), context.getBean("a", GenericApplicationContextTests.BeanA.class).c);
        Assert.assertSame(context, context.getBean("b", GenericApplicationContextTests.BeanB.class).applicationContext);
    }

    @Test
    public void individualNamedBeanWithSupplierAndCustomizer() {
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean("a", GenericApplicationContextTests.BeanA.class, () -> new org.springframework.context.support.BeanA(context.getBean(.class), context.getBean(.class)), ( bd) -> bd.setLazyInit(true));
        context.registerBean("b", GenericApplicationContextTests.BeanB.class, GenericApplicationContextTests.BeanB::new);
        context.registerBean("c", GenericApplicationContextTests.BeanC.class, GenericApplicationContextTests.BeanC::new);
        context.refresh();
        Assert.assertFalse(context.getBeanFactory().containsSingleton("a"));
        Assert.assertSame(context.getBean("b", GenericApplicationContextTests.BeanB.class), context.getBean(GenericApplicationContextTests.BeanA.class).b);
        Assert.assertSame(context.getBean("c"), context.getBean("a", GenericApplicationContextTests.BeanA.class).c);
        Assert.assertSame(context, context.getBean("b", GenericApplicationContextTests.BeanB.class).applicationContext);
    }

    @Test
    public void individualBeanWithNullReturningSupplier() {
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean("a", GenericApplicationContextTests.BeanA.class, () -> null);
        context.registerBean("b", GenericApplicationContextTests.BeanB.class, GenericApplicationContextTests.BeanB::new);
        context.registerBean("c", GenericApplicationContextTests.BeanC.class, GenericApplicationContextTests.BeanC::new);
        context.refresh();
        Assert.assertTrue(ObjectUtils.containsElement(context.getBeanNamesForType(GenericApplicationContextTests.BeanA.class), "a"));
        Assert.assertTrue(ObjectUtils.containsElement(context.getBeanNamesForType(GenericApplicationContextTests.BeanB.class), "b"));
        Assert.assertTrue(ObjectUtils.containsElement(context.getBeanNamesForType(GenericApplicationContextTests.BeanC.class), "c"));
        Assert.assertTrue(context.getBeansOfType(GenericApplicationContextTests.BeanA.class).isEmpty());
        Assert.assertSame(context.getBean(GenericApplicationContextTests.BeanB.class), context.getBeansOfType(GenericApplicationContextTests.BeanB.class).values().iterator().next());
        Assert.assertSame(context.getBean(GenericApplicationContextTests.BeanC.class), context.getBeansOfType(GenericApplicationContextTests.BeanC.class).values().iterator().next());
    }

    static class BeanA {
        GenericApplicationContextTests.BeanB b;

        GenericApplicationContextTests.BeanC c;

        public BeanA(GenericApplicationContextTests.BeanB b, GenericApplicationContextTests.BeanC c) {
            this.b = b;
            this.c = c;
        }
    }

    static class BeanB implements ApplicationContextAware {
        ApplicationContext applicationContext;

        public BeanB() {
        }

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) {
            this.applicationContext = applicationContext;
        }
    }

    static class BeanC {}
}

