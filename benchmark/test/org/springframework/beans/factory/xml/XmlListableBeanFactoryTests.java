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
package org.springframework.beans.factory.xml;


import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.tests.sample.beans.ITestBean;
import org.springframework.tests.sample.beans.LifecycleBean;
import org.springframework.tests.sample.beans.TestBean;
import org.springframework.tests.sample.beans.factory.DummyFactory;


/**
 *
 *
 * @author Juergen Hoeller
 * @since 09.11.2003
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class XmlListableBeanFactoryTests extends AbstractListableBeanFactoryTests {
    private DefaultListableBeanFactory parent;

    private DefaultListableBeanFactory factory;

    @Test
    @Override
    public void count() {
        assertCount(24);
    }

    @Test
    public void beanCount() {
        assertTestBeanCount(13);
    }

    @Test
    public void lifecycleMethods() {
        LifecycleBean bean = ((LifecycleBean) (getBeanFactory().getBean("lifecycle")));
        bean.businessMethod();
    }

    @Test
    public void protectedLifecycleMethods() {
        ProtectedLifecycleBean bean = ((ProtectedLifecycleBean) (getBeanFactory().getBean("protectedLifecycle")));
        bean.businessMethod();
    }

    @Test
    public void descriptionButNoProperties() {
        TestBean validEmpty = ((TestBean) (getBeanFactory().getBean("validEmptyWithDescription")));
        Assert.assertEquals(0, validEmpty.getAge());
    }

    /**
     * Test that properties with name as well as id creating an alias up front.
     */
    @Test
    public void autoAliasing() {
        List beanNames = Arrays.asList(getListableBeanFactory().getBeanDefinitionNames());
        TestBean tb1 = ((TestBean) (getBeanFactory().getBean("aliased")));
        TestBean alias1 = ((TestBean) (getBeanFactory().getBean("myalias")));
        Assert.assertTrue((tb1 == alias1));
        List tb1Aliases = Arrays.asList(getBeanFactory().getAliases("aliased"));
        Assert.assertEquals(2, tb1Aliases.size());
        Assert.assertTrue(tb1Aliases.contains("myalias"));
        Assert.assertTrue(tb1Aliases.contains("youralias"));
        Assert.assertTrue(beanNames.contains("aliased"));
        Assert.assertFalse(beanNames.contains("myalias"));
        Assert.assertFalse(beanNames.contains("youralias"));
        TestBean tb2 = ((TestBean) (getBeanFactory().getBean("multiAliased")));
        TestBean alias2 = ((TestBean) (getBeanFactory().getBean("alias1")));
        TestBean alias3 = ((TestBean) (getBeanFactory().getBean("alias2")));
        TestBean alias3a = ((TestBean) (getBeanFactory().getBean("alias3")));
        TestBean alias3b = ((TestBean) (getBeanFactory().getBean("alias4")));
        Assert.assertTrue((tb2 == alias2));
        Assert.assertTrue((tb2 == alias3));
        Assert.assertTrue((tb2 == alias3a));
        Assert.assertTrue((tb2 == alias3b));
        List tb2Aliases = Arrays.asList(getBeanFactory().getAliases("multiAliased"));
        Assert.assertEquals(4, tb2Aliases.size());
        Assert.assertTrue(tb2Aliases.contains("alias1"));
        Assert.assertTrue(tb2Aliases.contains("alias2"));
        Assert.assertTrue(tb2Aliases.contains("alias3"));
        Assert.assertTrue(tb2Aliases.contains("alias4"));
        Assert.assertTrue(beanNames.contains("multiAliased"));
        Assert.assertFalse(beanNames.contains("alias1"));
        Assert.assertFalse(beanNames.contains("alias2"));
        Assert.assertFalse(beanNames.contains("alias3"));
        Assert.assertFalse(beanNames.contains("alias4"));
        TestBean tb3 = ((TestBean) (getBeanFactory().getBean("aliasWithoutId1")));
        TestBean alias4 = ((TestBean) (getBeanFactory().getBean("aliasWithoutId2")));
        TestBean alias5 = ((TestBean) (getBeanFactory().getBean("aliasWithoutId3")));
        Assert.assertTrue((tb3 == alias4));
        Assert.assertTrue((tb3 == alias5));
        List tb3Aliases = Arrays.asList(getBeanFactory().getAliases("aliasWithoutId1"));
        Assert.assertEquals(2, tb3Aliases.size());
        Assert.assertTrue(tb3Aliases.contains("aliasWithoutId2"));
        Assert.assertTrue(tb3Aliases.contains("aliasWithoutId3"));
        Assert.assertTrue(beanNames.contains("aliasWithoutId1"));
        Assert.assertFalse(beanNames.contains("aliasWithoutId2"));
        Assert.assertFalse(beanNames.contains("aliasWithoutId3"));
        TestBean tb4 = ((TestBean) (getBeanFactory().getBean(((TestBean.class.getName()) + "#0"))));
        Assert.assertEquals(null, tb4.getName());
        Map drs = getListableBeanFactory().getBeansOfType(DummyReferencer.class, false, false);
        Assert.assertEquals(5, drs.size());
        Assert.assertTrue(drs.containsKey(((DummyReferencer.class.getName()) + "#0")));
        Assert.assertTrue(drs.containsKey(((DummyReferencer.class.getName()) + "#1")));
        Assert.assertTrue(drs.containsKey(((DummyReferencer.class.getName()) + "#2")));
    }

    @Test
    public void factoryNesting() {
        ITestBean father = ((ITestBean) (getBeanFactory().getBean("father")));
        Assert.assertTrue("Bean from root context", (father != null));
        TestBean rod = ((TestBean) (getBeanFactory().getBean("rod")));
        Assert.assertTrue("Bean from child context", "Rod".equals(rod.getName()));
        Assert.assertTrue("Bean has external reference", ((rod.getSpouse()) == father));
        rod = ((TestBean) (parent.getBean("rod")));
        Assert.assertTrue("Bean from root context", "Roderick".equals(rod.getName()));
    }

    @Test
    public void factoryReferences() {
        DummyFactory factory = ((DummyFactory) (getBeanFactory().getBean("&singletonFactory")));
        DummyReferencer ref = ((DummyReferencer) (getBeanFactory().getBean("factoryReferencer")));
        Assert.assertTrue(((ref.getTestBean1()) == (ref.getTestBean2())));
        Assert.assertTrue(((ref.getDummyFactory()) == factory));
        DummyReferencer ref2 = ((DummyReferencer) (getBeanFactory().getBean("factoryReferencerWithConstructor")));
        Assert.assertTrue(((ref2.getTestBean1()) == (ref2.getTestBean2())));
        Assert.assertTrue(((ref2.getDummyFactory()) == factory));
    }

    @Test
    public void prototypeReferences() {
        // check that not broken by circular reference resolution mechanism
        DummyReferencer ref1 = ((DummyReferencer) (getBeanFactory().getBean("prototypeReferencer")));
        Assert.assertTrue("Not referencing same bean twice", ((ref1.getTestBean1()) != (ref1.getTestBean2())));
        DummyReferencer ref2 = ((DummyReferencer) (getBeanFactory().getBean("prototypeReferencer")));
        Assert.assertTrue("Not the same referencer", (ref1 != ref2));
        Assert.assertTrue("Not referencing same bean twice", ((ref2.getTestBean1()) != (ref2.getTestBean2())));
        Assert.assertTrue("Not referencing same bean twice", ((ref1.getTestBean1()) != (ref2.getTestBean1())));
        Assert.assertTrue("Not referencing same bean twice", ((ref1.getTestBean2()) != (ref2.getTestBean2())));
        Assert.assertTrue("Not referencing same bean twice", ((ref1.getTestBean1()) != (ref2.getTestBean2())));
    }

    @Test
    public void beanPostProcessor() {
        TestBean kerry = ((TestBean) (getBeanFactory().getBean("kerry")));
        TestBean kathy = ((TestBean) (getBeanFactory().getBean("kathy")));
        DummyFactory factory = ((DummyFactory) (getBeanFactory().getBean("&singletonFactory")));
        TestBean factoryCreated = ((TestBean) (getBeanFactory().getBean("singletonFactory")));
        Assert.assertTrue(kerry.isPostProcessed());
        Assert.assertTrue(kathy.isPostProcessed());
        Assert.assertTrue(factory.isPostProcessed());
        Assert.assertTrue(factoryCreated.isPostProcessed());
    }

    @Test
    public void emptyValues() {
        TestBean rod = ((TestBean) (getBeanFactory().getBean("rod")));
        TestBean kerry = ((TestBean) (getBeanFactory().getBean("kerry")));
        Assert.assertTrue("Touchy is empty", "".equals(rod.getTouchy()));
        Assert.assertTrue("Touchy is empty", "".equals(kerry.getTouchy()));
    }

    @Test
    public void commentsAndCdataInValue() {
        TestBean bean = ((TestBean) (getBeanFactory().getBean("commentsInValue")));
        Assert.assertEquals("Failed to handle comments and CDATA properly", "this is a <!--comment-->", bean.getName());
    }
}

