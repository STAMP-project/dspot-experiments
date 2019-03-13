/**
 * Copyright 2002-2019 the original author or authors.
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


import DummyFactory.SINGLETON_NAME;
import XmlBeanDefinitionReader.VALIDATION_NONE;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.logging.LogFactory;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanIsAbstractException;
import org.springframework.beans.factory.CannotLoadBeanClassException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.MethodReplacer;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.UrlResource;
import org.springframework.tests.sample.beans.DependenciesBean;
import org.springframework.tests.sample.beans.DerivedTestBean;
import org.springframework.tests.sample.beans.ITestBean;
import org.springframework.tests.sample.beans.IndexedTestBean;
import org.springframework.tests.sample.beans.ResourceTestBean;
import org.springframework.tests.sample.beans.TestBean;
import org.springframework.tests.sample.beans.factory.DummyFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.SerializationTestUtils;
import org.springframework.util.StopWatch;
import org.xml.sax.InputSource;


/**
 * Miscellaneous tests for XML bean definitions.
 *
 * @author Juergen Hoeller
 * @author Rod Johnson
 * @author Rick Evans
 * @author Chris Beams
 * @author Sam Brannen
 */
public class XmlBeanFactoryTests {
    private static final Class<?> CLASS = XmlBeanFactoryTests.class;

    private static final String CLASSNAME = XmlBeanFactoryTests.CLASS.getSimpleName();

    private static final ClassPathResource AUTOWIRE_CONTEXT = XmlBeanFactoryTests.classPathResource("-autowire.xml");

    private static final ClassPathResource CHILD_CONTEXT = XmlBeanFactoryTests.classPathResource("-child.xml");

    private static final ClassPathResource CLASS_NOT_FOUND_CONTEXT = XmlBeanFactoryTests.classPathResource("-classNotFound.xml");

    private static final ClassPathResource COMPLEX_FACTORY_CIRCLE_CONTEXT = XmlBeanFactoryTests.classPathResource("-complexFactoryCircle.xml");

    private static final ClassPathResource CONSTRUCTOR_ARG_CONTEXT = XmlBeanFactoryTests.classPathResource("-constructorArg.xml");

    private static final ClassPathResource CONSTRUCTOR_OVERRIDES_CONTEXT = XmlBeanFactoryTests.classPathResource("-constructorOverrides.xml");

    private static final ClassPathResource DELEGATION_OVERRIDES_CONTEXT = XmlBeanFactoryTests.classPathResource("-delegationOverrides.xml");

    private static final ClassPathResource DEP_CARG_AUTOWIRE_CONTEXT = XmlBeanFactoryTests.classPathResource("-depCargAutowire.xml");

    private static final ClassPathResource DEP_CARG_INNER_CONTEXT = XmlBeanFactoryTests.classPathResource("-depCargInner.xml");

    private static final ClassPathResource DEP_CARG_CONTEXT = XmlBeanFactoryTests.classPathResource("-depCarg.xml");

    private static final ClassPathResource DEP_DEPENDSON_INNER_CONTEXT = XmlBeanFactoryTests.classPathResource("-depDependsOnInner.xml");

    private static final ClassPathResource DEP_DEPENDSON_CONTEXT = XmlBeanFactoryTests.classPathResource("-depDependsOn.xml");

    private static final ClassPathResource DEP_PROP = XmlBeanFactoryTests.classPathResource("-depProp.xml");

    private static final ClassPathResource DEP_PROP_ABN_CONTEXT = XmlBeanFactoryTests.classPathResource("-depPropAutowireByName.xml");

    private static final ClassPathResource DEP_PROP_ABT_CONTEXT = XmlBeanFactoryTests.classPathResource("-depPropAutowireByType.xml");

    private static final ClassPathResource DEP_PROP_MIDDLE_CONTEXT = XmlBeanFactoryTests.classPathResource("-depPropInTheMiddle.xml");

    private static final ClassPathResource DEP_PROP_INNER_CONTEXT = XmlBeanFactoryTests.classPathResource("-depPropInner.xml");

    private static final ClassPathResource DEP_MATERIALIZE_CONTEXT = XmlBeanFactoryTests.classPathResource("-depMaterializeThis.xml");

    private static final ClassPathResource FACTORY_CIRCLE_CONTEXT = XmlBeanFactoryTests.classPathResource("-factoryCircle.xml");

    private static final ClassPathResource INITIALIZERS_CONTEXT = XmlBeanFactoryTests.classPathResource("-initializers.xml");

    private static final ClassPathResource INVALID_CONTEXT = XmlBeanFactoryTests.classPathResource("-invalid.xml");

    private static final ClassPathResource INVALID_NO_SUCH_METHOD_CONTEXT = XmlBeanFactoryTests.classPathResource("-invalidOverridesNoSuchMethod.xml");

    private static final ClassPathResource COLLECTIONS_XSD_CONTEXT = XmlBeanFactoryTests.classPathResource("-localCollectionsUsingXsd.xml");

    private static final ClassPathResource MISSING_CONTEXT = XmlBeanFactoryTests.classPathResource("-missing.xml");

    private static final ClassPathResource OVERRIDES_CONTEXT = XmlBeanFactoryTests.classPathResource("-overrides.xml");

    private static final ClassPathResource PARENT_CONTEXT = XmlBeanFactoryTests.classPathResource("-parent.xml");

    private static final ClassPathResource NO_SUCH_FACTORY_METHOD_CONTEXT = XmlBeanFactoryTests.classPathResource("-noSuchFactoryMethod.xml");

    private static final ClassPathResource RECURSIVE_IMPORT_CONTEXT = XmlBeanFactoryTests.classPathResource("-recursiveImport.xml");

    private static final ClassPathResource RESOURCE_CONTEXT = XmlBeanFactoryTests.classPathResource("-resource.xml");

    private static final ClassPathResource TEST_WITH_DUP_NAMES_CONTEXT = XmlBeanFactoryTests.classPathResource("-testWithDuplicateNames.xml");

    private static final ClassPathResource TEST_WITH_DUP_NAME_IN_ALIAS_CONTEXT = XmlBeanFactoryTests.classPathResource("-testWithDuplicateNameInAlias.xml");

    private static final ClassPathResource REFTYPES_CONTEXT = XmlBeanFactoryTests.classPathResource("-reftypes.xml");

    private static final ClassPathResource DEFAULT_LAZY_CONTEXT = XmlBeanFactoryTests.classPathResource("-defaultLazyInit.xml");

    private static final ClassPathResource DEFAULT_AUTOWIRE_CONTEXT = XmlBeanFactoryTests.classPathResource("-defaultAutowire.xml");

    // SPR-2368
    @Test
    public void testCollectionsReferredToAsRefLocals() {
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(factory).loadBeanDefinitions(XmlBeanFactoryTests.COLLECTIONS_XSD_CONTEXT);
        factory.preInstantiateSingletons();
    }

    @Test
    public void testRefToSeparatePrototypeInstances() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(xbf);
        reader.setValidationMode(VALIDATION_NONE);
        reader.loadBeanDefinitions(XmlBeanFactoryTests.REFTYPES_CONTEXT);
        TestBean emma = ((TestBean) (xbf.getBean("emma")));
        TestBean georgia = ((TestBean) (xbf.getBean("georgia")));
        ITestBean emmasJenks = emma.getSpouse();
        ITestBean georgiasJenks = georgia.getSpouse();
        Assert.assertTrue("Emma and georgia think they have a different boyfriend", (emmasJenks != georgiasJenks));
        Assert.assertTrue("Emmas jenks has right name", emmasJenks.getName().equals("Andrew"));
        Assert.assertTrue("Emmas doesn't equal new ref", (emmasJenks != (xbf.getBean("jenks"))));
        Assert.assertTrue("Georgias jenks has right name", emmasJenks.getName().equals("Andrew"));
        Assert.assertTrue("They are object equal", emmasJenks.equals(georgiasJenks));
        Assert.assertTrue("They object equal direct ref", emmasJenks.equals(xbf.getBean("jenks")));
    }

    @Test
    public void testRefToSingleton() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(xbf);
        reader.setValidationMode(VALIDATION_NONE);
        reader.loadBeanDefinitions(new org.springframework.core.io.support.EncodedResource(XmlBeanFactoryTests.REFTYPES_CONTEXT, "ISO-8859-1"));
        TestBean jen = ((TestBean) (xbf.getBean("jenny")));
        TestBean dave = ((TestBean) (xbf.getBean("david")));
        TestBean jenks = ((TestBean) (xbf.getBean("jenks")));
        ITestBean davesJen = dave.getSpouse();
        ITestBean jenksJen = jenks.getSpouse();
        Assert.assertTrue("1 jen instance", (davesJen == jenksJen));
        Assert.assertTrue("1 jen instance", (davesJen == jen));
    }

    @Test
    public void testInnerBeans() throws IOException {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(xbf);
        reader.setValidationMode(VALIDATION_NONE);
        InputStream inputStream = getClass().getResourceAsStream(XmlBeanFactoryTests.REFTYPES_CONTEXT.getPath());
        try {
            reader.loadBeanDefinitions(new InputSource(inputStream));
        } finally {
            inputStream.close();
        }
        // Let's create the outer bean named "innerBean",
        // to check whether it doesn't create any conflicts
        // with the actual inner beans named "innerBean".
        xbf.getBean("innerBean");
        TestBean hasInnerBeans = ((TestBean) (xbf.getBean("hasInnerBeans")));
        Assert.assertEquals(5, hasInnerBeans.getAge());
        TestBean inner1 = ((TestBean) (hasInnerBeans.getSpouse()));
        Assert.assertNotNull(inner1);
        Assert.assertEquals("innerBean#1", inner1.getBeanName());
        Assert.assertEquals("inner1", inner1.getName());
        Assert.assertEquals(6, inner1.getAge());
        Assert.assertNotNull(hasInnerBeans.getFriends());
        Object[] friends = hasInnerBeans.getFriends().toArray();
        Assert.assertEquals(3, friends.length);
        DerivedTestBean inner2 = ((DerivedTestBean) (friends[0]));
        Assert.assertEquals("inner2", inner2.getName());
        Assert.assertTrue(inner2.getBeanName().startsWith(DerivedTestBean.class.getName()));
        Assert.assertFalse(xbf.containsBean("innerBean#1"));
        Assert.assertNotNull(inner2);
        Assert.assertEquals(7, inner2.getAge());
        TestBean innerFactory = ((TestBean) (friends[1]));
        Assert.assertEquals(SINGLETON_NAME, innerFactory.getName());
        TestBean inner5 = ((TestBean) (friends[2]));
        Assert.assertEquals("innerBean#2", inner5.getBeanName());
        Assert.assertNotNull(hasInnerBeans.getSomeMap());
        Assert.assertEquals(2, hasInnerBeans.getSomeMap().size());
        TestBean inner3 = ((TestBean) (hasInnerBeans.getSomeMap().get("someKey")));
        Assert.assertEquals("Jenny", inner3.getName());
        Assert.assertEquals(30, inner3.getAge());
        TestBean inner4 = ((TestBean) (hasInnerBeans.getSomeMap().get("someOtherKey")));
        Assert.assertEquals("inner4", inner4.getName());
        Assert.assertEquals(9, inner4.getAge());
        TestBean hasInnerBeansForConstructor = ((TestBean) (xbf.getBean("hasInnerBeansForConstructor")));
        TestBean innerForConstructor = ((TestBean) (hasInnerBeansForConstructor.getSpouse()));
        Assert.assertNotNull(innerForConstructor);
        Assert.assertEquals("innerBean#3", innerForConstructor.getBeanName());
        Assert.assertEquals("inner1", innerForConstructor.getName());
        Assert.assertEquals(6, innerForConstructor.getAge());
        hasInnerBeansForConstructor = ((TestBean) (xbf.getBean("hasInnerBeansAsPrototype")));
        innerForConstructor = ((TestBean) (hasInnerBeansForConstructor.getSpouse()));
        Assert.assertNotNull(innerForConstructor);
        Assert.assertEquals("innerBean", innerForConstructor.getBeanName());
        Assert.assertEquals("inner1", innerForConstructor.getName());
        Assert.assertEquals(6, innerForConstructor.getAge());
        hasInnerBeansForConstructor = ((TestBean) (xbf.getBean("hasInnerBeansAsPrototype")));
        innerForConstructor = ((TestBean) (hasInnerBeansForConstructor.getSpouse()));
        Assert.assertNotNull(innerForConstructor);
        Assert.assertEquals("innerBean", innerForConstructor.getBeanName());
        Assert.assertEquals("inner1", innerForConstructor.getName());
        Assert.assertEquals(6, innerForConstructor.getAge());
        xbf.destroySingletons();
        Assert.assertTrue(inner1.wasDestroyed());
        Assert.assertTrue(inner2.wasDestroyed());
        Assert.assertTrue(((innerFactory.getName()) == null));
        Assert.assertTrue(inner5.wasDestroyed());
    }

    @Test
    public void testInnerBeansWithoutDestroy() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(xbf);
        reader.setValidationMode(VALIDATION_NONE);
        reader.loadBeanDefinitions(XmlBeanFactoryTests.REFTYPES_CONTEXT);
        // Let's create the outer bean named "innerBean",
        // to check whether it doesn't create any conflicts
        // with the actual inner beans named "innerBean".
        xbf.getBean("innerBean");
        TestBean hasInnerBeans = ((TestBean) (xbf.getBean("hasInnerBeansWithoutDestroy")));
        Assert.assertEquals(5, hasInnerBeans.getAge());
        TestBean inner1 = ((TestBean) (hasInnerBeans.getSpouse()));
        Assert.assertNotNull(inner1);
        Assert.assertTrue(inner1.getBeanName().startsWith("innerBean"));
        Assert.assertEquals("inner1", inner1.getName());
        Assert.assertEquals(6, inner1.getAge());
        Assert.assertNotNull(hasInnerBeans.getFriends());
        Object[] friends = hasInnerBeans.getFriends().toArray();
        Assert.assertEquals(3, friends.length);
        DerivedTestBean inner2 = ((DerivedTestBean) (friends[0]));
        Assert.assertEquals("inner2", inner2.getName());
        Assert.assertTrue(inner2.getBeanName().startsWith(DerivedTestBean.class.getName()));
        Assert.assertNotNull(inner2);
        Assert.assertEquals(7, inner2.getAge());
        TestBean innerFactory = ((TestBean) (friends[1]));
        Assert.assertEquals(SINGLETON_NAME, innerFactory.getName());
        TestBean inner5 = ((TestBean) (friends[2]));
        Assert.assertTrue(inner5.getBeanName().startsWith("innerBean"));
    }

    @Test
    public void testFailsOnInnerBean() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(xbf);
        reader.setValidationMode(VALIDATION_NONE);
        reader.loadBeanDefinitions(XmlBeanFactoryTests.REFTYPES_CONTEXT);
        try {
            xbf.getBean("failsOnInnerBean");
        } catch (BeanCreationException ex) {
            // Check whether message contains outer bean name.
            ex.printStackTrace();
            Assert.assertTrue(ex.getMessage().contains("failsOnInnerBean"));
            Assert.assertTrue(ex.getMessage().contains("someMap"));
        }
        try {
            xbf.getBean("failsOnInnerBeanForConstructor");
        } catch (BeanCreationException ex) {
            // Check whether message contains outer bean name.
            ex.printStackTrace();
            Assert.assertTrue(ex.getMessage().contains("failsOnInnerBeanForConstructor"));
            Assert.assertTrue(ex.getMessage().contains("constructor argument"));
        }
    }

    @Test
    public void testInheritanceFromParentFactoryPrototype() {
        DefaultListableBeanFactory parent = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(parent).loadBeanDefinitions(XmlBeanFactoryTests.PARENT_CONTEXT);
        DefaultListableBeanFactory child = new DefaultListableBeanFactory(parent);
        new XmlBeanDefinitionReader(child).loadBeanDefinitions(XmlBeanFactoryTests.CHILD_CONTEXT);
        Assert.assertEquals(TestBean.class, child.getType("inheritsFromParentFactory"));
        TestBean inherits = ((TestBean) (child.getBean("inheritsFromParentFactory")));
        // Name property value is overridden
        Assert.assertTrue(inherits.getName().equals("override"));
        // Age property is inherited from bean in parent factory
        Assert.assertTrue(((inherits.getAge()) == 1));
        TestBean inherits2 = ((TestBean) (child.getBean("inheritsFromParentFactory")));
        Assert.assertFalse((inherits2 == inherits));
    }

    @Test
    public void testInheritanceWithDifferentClass() {
        DefaultListableBeanFactory parent = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(parent).loadBeanDefinitions(XmlBeanFactoryTests.PARENT_CONTEXT);
        DefaultListableBeanFactory child = new DefaultListableBeanFactory(parent);
        new XmlBeanDefinitionReader(child).loadBeanDefinitions(XmlBeanFactoryTests.CHILD_CONTEXT);
        Assert.assertEquals(DerivedTestBean.class, child.getType("inheritsWithClass"));
        DerivedTestBean inherits = ((DerivedTestBean) (child.getBean("inheritsWithDifferentClass")));
        // Name property value is overridden
        Assert.assertTrue(inherits.getName().equals("override"));
        // Age property is inherited from bean in parent factory
        Assert.assertTrue(((inherits.getAge()) == 1));
        Assert.assertTrue(inherits.wasInitialized());
    }

    @Test
    public void testInheritanceWithClass() {
        DefaultListableBeanFactory parent = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(parent).loadBeanDefinitions(XmlBeanFactoryTests.PARENT_CONTEXT);
        DefaultListableBeanFactory child = new DefaultListableBeanFactory(parent);
        new XmlBeanDefinitionReader(child).loadBeanDefinitions(XmlBeanFactoryTests.CHILD_CONTEXT);
        Assert.assertEquals(DerivedTestBean.class, child.getType("inheritsWithClass"));
        DerivedTestBean inherits = ((DerivedTestBean) (child.getBean("inheritsWithClass")));
        // Name property value is overridden
        Assert.assertTrue(inherits.getName().equals("override"));
        // Age property is inherited from bean in parent factory
        Assert.assertTrue(((inherits.getAge()) == 1));
        Assert.assertTrue(inherits.wasInitialized());
    }

    @Test
    public void testPrototypeInheritanceFromParentFactoryPrototype() {
        DefaultListableBeanFactory parent = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(parent).loadBeanDefinitions(XmlBeanFactoryTests.PARENT_CONTEXT);
        DefaultListableBeanFactory child = new DefaultListableBeanFactory(parent);
        new XmlBeanDefinitionReader(child).loadBeanDefinitions(XmlBeanFactoryTests.CHILD_CONTEXT);
        Assert.assertEquals(TestBean.class, child.getType("prototypeInheritsFromParentFactoryPrototype"));
        TestBean inherits = ((TestBean) (child.getBean("prototypeInheritsFromParentFactoryPrototype")));
        // Name property value is overridden
        Assert.assertTrue(inherits.getName().equals("prototype-override"));
        // Age property is inherited from bean in parent factory
        Assert.assertTrue(((inherits.getAge()) == 2));
        TestBean inherits2 = ((TestBean) (child.getBean("prototypeInheritsFromParentFactoryPrototype")));
        Assert.assertFalse((inherits2 == inherits));
        inherits2.setAge(13);
        Assert.assertTrue(((inherits2.getAge()) == 13));
        // Shouldn't have changed first instance
        Assert.assertTrue(((inherits.getAge()) == 2));
    }

    @Test
    public void testPrototypeInheritanceFromParentFactorySingleton() {
        DefaultListableBeanFactory parent = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(parent).loadBeanDefinitions(XmlBeanFactoryTests.PARENT_CONTEXT);
        DefaultListableBeanFactory child = new DefaultListableBeanFactory(parent);
        new XmlBeanDefinitionReader(child).loadBeanDefinitions(XmlBeanFactoryTests.CHILD_CONTEXT);
        TestBean inherits = ((TestBean) (child.getBean("protoypeInheritsFromParentFactorySingleton")));
        // Name property value is overridden
        Assert.assertTrue(inherits.getName().equals("prototypeOverridesInheritedSingleton"));
        // Age property is inherited from bean in parent factory
        Assert.assertTrue(((inherits.getAge()) == 1));
        TestBean inherits2 = ((TestBean) (child.getBean("protoypeInheritsFromParentFactorySingleton")));
        Assert.assertFalse((inherits2 == inherits));
        inherits2.setAge(13);
        Assert.assertTrue(((inherits2.getAge()) == 13));
        // Shouldn't have changed first instance
        Assert.assertTrue(((inherits.getAge()) == 1));
    }

    @Test
    public void testAutowireModeNotInherited() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(xbf);
        reader.loadBeanDefinitions(XmlBeanFactoryTests.OVERRIDES_CONTEXT);
        TestBean david = ((TestBean) (xbf.getBean("magicDavid")));
        // the parent bean is autowiring
        Assert.assertNotNull(david.getSpouse());
        TestBean derivedDavid = ((TestBean) (xbf.getBean("magicDavidDerived")));
        // this fails while it inherits from the child bean
        Assert.assertNull("autowiring not propagated along child relationships", derivedDavid.getSpouse());
    }

    @Test
    public void testAbstractParentBeans() {
        DefaultListableBeanFactory parent = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(parent).loadBeanDefinitions(XmlBeanFactoryTests.PARENT_CONTEXT);
        parent.preInstantiateSingletons();
        Assert.assertTrue(parent.isSingleton("inheritedTestBeanWithoutClass"));
        // abstract beans should not match
        Map<?, ?> tbs = parent.getBeansOfType(TestBean.class);
        Assert.assertEquals(2, tbs.size());
        Assert.assertTrue(tbs.containsKey("inheritedTestBeanPrototype"));
        Assert.assertTrue(tbs.containsKey("inheritedTestBeanSingleton"));
        // abstract bean should throw exception on creation attempt
        try {
            parent.getBean("inheritedTestBeanWithoutClass");
            Assert.fail("Should have thrown BeanIsAbstractException");
        } catch (BeanIsAbstractException ex) {
            // expected
        }
        // non-abstract bean should work, even if it serves as parent
        Assert.assertTrue(((parent.getBean("inheritedTestBeanPrototype")) instanceof TestBean));
    }

    @Test
    public void testDependenciesMaterializeThis() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(xbf).loadBeanDefinitions(XmlBeanFactoryTests.DEP_MATERIALIZE_CONTEXT);
        Assert.assertEquals(2, xbf.getBeansOfType(DummyBo.class, true, false).size());
        Assert.assertEquals(3, xbf.getBeansOfType(DummyBo.class, true, true).size());
        Assert.assertEquals(3, xbf.getBeansOfType(DummyBo.class, true, false).size());
        Assert.assertEquals(3, xbf.getBeansOfType(DummyBo.class).size());
        Assert.assertEquals(2, xbf.getBeansOfType(DummyBoImpl.class, true, true).size());
        Assert.assertEquals(1, xbf.getBeansOfType(DummyBoImpl.class, false, true).size());
        Assert.assertEquals(2, xbf.getBeansOfType(DummyBoImpl.class).size());
        DummyBoImpl bos = ((DummyBoImpl) (xbf.getBean("boSingleton")));
        DummyBoImpl bop = ((DummyBoImpl) (xbf.getBean("boPrototype")));
        Assert.assertNotSame(bos, bop);
        Assert.assertTrue(((bos.dao) == (bop.dao)));
    }

    @Test
    public void testChildOverridesParentBean() {
        DefaultListableBeanFactory parent = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(parent).loadBeanDefinitions(XmlBeanFactoryTests.PARENT_CONTEXT);
        DefaultListableBeanFactory child = new DefaultListableBeanFactory(parent);
        new XmlBeanDefinitionReader(child).loadBeanDefinitions(XmlBeanFactoryTests.CHILD_CONTEXT);
        TestBean inherits = ((TestBean) (child.getBean("inheritedTestBean")));
        // Name property value is overridden
        Assert.assertTrue(inherits.getName().equals("overrideParentBean"));
        // Age property is inherited from bean in parent factory
        Assert.assertTrue(((inherits.getAge()) == 1));
        TestBean inherits2 = ((TestBean) (child.getBean("inheritedTestBean")));
        Assert.assertTrue((inherits2 != inherits));
    }

    /**
     * Check that a prototype can't inherit from a bogus parent.
     * If a singleton does this the factory will fail to load.
     */
    @Test
    public void testBogusParentageFromParentFactory() {
        DefaultListableBeanFactory parent = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(parent).loadBeanDefinitions(XmlBeanFactoryTests.PARENT_CONTEXT);
        DefaultListableBeanFactory child = new DefaultListableBeanFactory(parent);
        new XmlBeanDefinitionReader(child).loadBeanDefinitions(XmlBeanFactoryTests.CHILD_CONTEXT);
        try {
            child.getBean("bogusParent", TestBean.class);
            Assert.fail();
        } catch (BeanDefinitionStoreException ex) {
            // check exception message contains the name
            Assert.assertTrue(ex.getMessage().contains("bogusParent"));
            Assert.assertTrue(((ex.getCause()) instanceof NoSuchBeanDefinitionException));
        }
    }

    /**
     * Note that prototype/singleton distinction is <b>not</b> inherited.
     * It's possible for a subclass singleton not to return independent
     * instances even if derived from a prototype
     */
    @Test
    public void testSingletonInheritsFromParentFactoryPrototype() {
        DefaultListableBeanFactory parent = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(parent).loadBeanDefinitions(XmlBeanFactoryTests.PARENT_CONTEXT);
        DefaultListableBeanFactory child = new DefaultListableBeanFactory(parent);
        new XmlBeanDefinitionReader(child).loadBeanDefinitions(XmlBeanFactoryTests.CHILD_CONTEXT);
        TestBean inherits = ((TestBean) (child.getBean("singletonInheritsFromParentFactoryPrototype")));
        // Name property value is overridden
        Assert.assertTrue(inherits.getName().equals("prototype-override"));
        // Age property is inherited from bean in parent factory
        Assert.assertTrue(((inherits.getAge()) == 2));
        TestBean inherits2 = ((TestBean) (child.getBean("singletonInheritsFromParentFactoryPrototype")));
        Assert.assertTrue((inherits2 == inherits));
    }

    @Test
    public void testSingletonFromParent() {
        DefaultListableBeanFactory parent = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(parent).loadBeanDefinitions(XmlBeanFactoryTests.PARENT_CONTEXT);
        TestBean beanFromParent = ((TestBean) (parent.getBean("inheritedTestBeanSingleton")));
        DefaultListableBeanFactory child = new DefaultListableBeanFactory(parent);
        new XmlBeanDefinitionReader(child).loadBeanDefinitions(XmlBeanFactoryTests.CHILD_CONTEXT);
        TestBean beanFromChild = ((TestBean) (child.getBean("inheritedTestBeanSingleton")));
        Assert.assertTrue("singleton from parent and child is the same", (beanFromParent == beanFromChild));
    }

    @Test
    public void testNestedPropertyValue() {
        DefaultListableBeanFactory parent = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(parent).loadBeanDefinitions(XmlBeanFactoryTests.PARENT_CONTEXT);
        DefaultListableBeanFactory child = new DefaultListableBeanFactory(parent);
        new XmlBeanDefinitionReader(child).loadBeanDefinitions(XmlBeanFactoryTests.CHILD_CONTEXT);
        IndexedTestBean bean = ((IndexedTestBean) (child.getBean("indexedTestBean")));
        Assert.assertEquals("name applied correctly", "myname", bean.getArray()[0].getName());
    }

    @Test
    public void testCircularReferences() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(xbf);
        reader.setValidationMode(VALIDATION_NONE);
        reader.loadBeanDefinitions(XmlBeanFactoryTests.REFTYPES_CONTEXT);
        TestBean jenny = ((TestBean) (xbf.getBean("jenny")));
        TestBean david = ((TestBean) (xbf.getBean("david")));
        TestBean ego = ((TestBean) (xbf.getBean("ego")));
        TestBean complexInnerEgo = ((TestBean) (xbf.getBean("complexInnerEgo")));
        TestBean complexEgo = ((TestBean) (xbf.getBean("complexEgo")));
        Assert.assertTrue("Correct circular reference", ((jenny.getSpouse()) == david));
        Assert.assertTrue("Correct circular reference", ((david.getSpouse()) == jenny));
        Assert.assertTrue("Correct circular reference", ((ego.getSpouse()) == ego));
        Assert.assertTrue("Correct circular reference", ((complexInnerEgo.getSpouse().getSpouse()) == complexInnerEgo));
        Assert.assertTrue("Correct circular reference", ((complexEgo.getSpouse().getSpouse()) == complexEgo));
    }

    @Test
    public void testCircularReferenceWithFactoryBeanFirst() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(xbf);
        reader.setValidationMode(VALIDATION_NONE);
        reader.loadBeanDefinitions(XmlBeanFactoryTests.REFTYPES_CONTEXT);
        xbf.getBean("egoBridge");
        TestBean complexEgo = ((TestBean) (xbf.getBean("complexEgo")));
        Assert.assertTrue("Correct circular reference", ((complexEgo.getSpouse().getSpouse()) == complexEgo));
    }

    @Test
    public void testCircularReferenceWithTwoFactoryBeans() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(xbf);
        reader.setValidationMode(VALIDATION_NONE);
        reader.loadBeanDefinitions(XmlBeanFactoryTests.REFTYPES_CONTEXT);
        TestBean ego1 = ((TestBean) (xbf.getBean("ego1")));
        Assert.assertTrue("Correct circular reference", ((ego1.getSpouse().getSpouse()) == ego1));
        TestBean ego3 = ((TestBean) (xbf.getBean("ego3")));
        Assert.assertTrue("Correct circular reference", ((ego3.getSpouse().getSpouse()) == ego3));
    }

    @Test
    public void testCircularReferencesWithNotAllowed() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        xbf.setAllowCircularReferences(false);
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(xbf);
        reader.setValidationMode(VALIDATION_NONE);
        reader.loadBeanDefinitions(XmlBeanFactoryTests.REFTYPES_CONTEXT);
        try {
            xbf.getBean("jenny");
            Assert.fail("Should have thrown BeanCreationException");
        } catch (BeanCreationException ex) {
            Assert.assertTrue(ex.contains(BeanCurrentlyInCreationException.class));
        }
    }

    @Test
    public void testCircularReferencesWithWrapping() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(xbf);
        reader.setValidationMode(VALIDATION_NONE);
        reader.loadBeanDefinitions(XmlBeanFactoryTests.REFTYPES_CONTEXT);
        xbf.addBeanPostProcessor(new XmlBeanFactoryTests.WrappingPostProcessor());
        try {
            xbf.getBean("jenny");
            Assert.fail("Should have thrown BeanCreationException");
        } catch (BeanCreationException ex) {
            Assert.assertTrue(ex.contains(BeanCurrentlyInCreationException.class));
        }
    }

    @Test
    public void testCircularReferencesWithWrappingAndRawInjectionAllowed() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        xbf.setAllowRawInjectionDespiteWrapping(true);
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(xbf);
        reader.setValidationMode(VALIDATION_NONE);
        reader.loadBeanDefinitions(XmlBeanFactoryTests.REFTYPES_CONTEXT);
        xbf.addBeanPostProcessor(new XmlBeanFactoryTests.WrappingPostProcessor());
        ITestBean jenny = ((ITestBean) (xbf.getBean("jenny")));
        ITestBean david = ((ITestBean) (xbf.getBean("david")));
        Assert.assertTrue(AopUtils.isAopProxy(jenny));
        Assert.assertTrue(AopUtils.isAopProxy(david));
        Assert.assertSame(david, jenny.getSpouse());
        Assert.assertNotSame(jenny, david.getSpouse());
        Assert.assertEquals("Jenny", david.getSpouse().getName());
        Assert.assertSame(david, david.getSpouse().getSpouse());
        Assert.assertTrue(AopUtils.isAopProxy(jenny.getSpouse()));
        Assert.assertTrue((!(AopUtils.isAopProxy(david.getSpouse()))));
    }

    @Test
    public void testFactoryReferenceCircle() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(xbf).loadBeanDefinitions(XmlBeanFactoryTests.FACTORY_CIRCLE_CONTEXT);
        TestBean tb = ((TestBean) (xbf.getBean("singletonFactory")));
        DummyFactory db = ((DummyFactory) (xbf.getBean("&singletonFactory")));
        Assert.assertTrue((tb == (db.getOtherTestBean())));
    }

    @Test
    public void testFactoryReferenceWithDoublePrefix() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(xbf).loadBeanDefinitions(XmlBeanFactoryTests.FACTORY_CIRCLE_CONTEXT);
        Assert.assertThat(xbf.getBean("&&singletonFactory"), CoreMatchers.instanceOf(DummyFactory.class));
    }

    @Test
    public void testComplexFactoryReferenceCircle() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(xbf).loadBeanDefinitions(XmlBeanFactoryTests.COMPLEX_FACTORY_CIRCLE_CONTEXT);
        xbf.getBean("proxy1");
        // check that unused instances from autowiring got removed
        Assert.assertEquals(4, xbf.getSingletonCount());
        // properly create the remaining two instances
        xbf.getBean("proxy2");
        Assert.assertEquals(5, xbf.getSingletonCount());
    }

    @Test(expected = BeanCreationException.class)
    public void noSuchFactoryBeanMethod() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(xbf).loadBeanDefinitions(XmlBeanFactoryTests.NO_SUCH_FACTORY_METHOD_CONTEXT);
        Assert.assertNotNull(xbf.getBean("defaultTestBean"));
    }

    @Test
    public void testInitMethodIsInvoked() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(xbf).loadBeanDefinitions(XmlBeanFactoryTests.INITIALIZERS_CONTEXT);
        XmlBeanFactoryTests.DoubleInitializer in = ((XmlBeanFactoryTests.DoubleInitializer) (xbf.getBean("init-method1")));
        // Initializer should have doubled value
        Assert.assertEquals(14, in.getNum());
    }

    /**
     * Test that if a custom initializer throws an exception, it's handled correctly
     */
    @Test
    public void testInitMethodThrowsException() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(xbf).loadBeanDefinitions(XmlBeanFactoryTests.INITIALIZERS_CONTEXT);
        try {
            xbf.getBean("init-method2");
            Assert.fail();
        } catch (BeanCreationException ex) {
            Assert.assertTrue(ex.getResourceDescription().contains("initializers.xml"));
            Assert.assertEquals("init-method2", ex.getBeanName());
            Assert.assertTrue(((ex.getCause()) instanceof IOException));
        }
    }

    @Test
    public void testNoSuchInitMethod() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(xbf).loadBeanDefinitions(XmlBeanFactoryTests.INITIALIZERS_CONTEXT);
        try {
            xbf.getBean("init-method3");
            Assert.fail();
        } catch (FatalBeanException ex) {
            // check message is helpful
            Assert.assertTrue(ex.getMessage().contains("initializers.xml"));
            Assert.assertTrue(ex.getMessage().contains("init-method3"));
            Assert.assertTrue(ex.getMessage().contains("init"));
        }
    }

    /**
     * Check that InitializingBean method is called first.
     */
    @Test
    public void testInitializingBeanAndInitMethod() {
        XmlBeanFactoryTests.InitAndIB.constructed = false;
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(xbf).loadBeanDefinitions(XmlBeanFactoryTests.INITIALIZERS_CONTEXT);
        Assert.assertFalse(XmlBeanFactoryTests.InitAndIB.constructed);
        xbf.preInstantiateSingletons();
        Assert.assertFalse(XmlBeanFactoryTests.InitAndIB.constructed);
        XmlBeanFactoryTests.InitAndIB iib = ((XmlBeanFactoryTests.InitAndIB) (xbf.getBean("init-and-ib")));
        Assert.assertTrue(XmlBeanFactoryTests.InitAndIB.constructed);
        Assert.assertTrue(((iib.afterPropertiesSetInvoked) && (iib.initMethodInvoked)));
        Assert.assertTrue(((!(iib.destroyed)) && (!(iib.customDestroyed))));
        xbf.destroySingletons();
        Assert.assertTrue(((iib.destroyed) && (iib.customDestroyed)));
        xbf.destroySingletons();
        Assert.assertTrue(((iib.destroyed) && (iib.customDestroyed)));
    }

    /**
     * Check that InitializingBean method is not called twice.
     */
    @Test
    public void testInitializingBeanAndSameInitMethod() {
        XmlBeanFactoryTests.InitAndIB.constructed = false;
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(xbf).loadBeanDefinitions(XmlBeanFactoryTests.INITIALIZERS_CONTEXT);
        Assert.assertFalse(XmlBeanFactoryTests.InitAndIB.constructed);
        xbf.preInstantiateSingletons();
        Assert.assertFalse(XmlBeanFactoryTests.InitAndIB.constructed);
        XmlBeanFactoryTests.InitAndIB iib = ((XmlBeanFactoryTests.InitAndIB) (xbf.getBean("ib-same-init")));
        Assert.assertTrue(XmlBeanFactoryTests.InitAndIB.constructed);
        Assert.assertTrue(((iib.afterPropertiesSetInvoked) && (!(iib.initMethodInvoked))));
        Assert.assertTrue(((!(iib.destroyed)) && (!(iib.customDestroyed))));
        xbf.destroySingletons();
        Assert.assertTrue(((iib.destroyed) && (!(iib.customDestroyed))));
        xbf.destroySingletons();
        Assert.assertTrue(((iib.destroyed) && (!(iib.customDestroyed))));
    }

    @Test
    public void testDefaultLazyInit() {
        XmlBeanFactoryTests.InitAndIB.constructed = false;
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(xbf).loadBeanDefinitions(XmlBeanFactoryTests.DEFAULT_LAZY_CONTEXT);
        Assert.assertFalse(XmlBeanFactoryTests.InitAndIB.constructed);
        xbf.preInstantiateSingletons();
        Assert.assertTrue(XmlBeanFactoryTests.InitAndIB.constructed);
        try {
            xbf.getBean("lazy-and-bad");
        } catch (BeanCreationException ex) {
            Assert.assertTrue(((ex.getCause()) instanceof IOException));
        }
    }

    @Test(expected = BeanDefinitionStoreException.class)
    public void noSuchXmlFile() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(xbf).loadBeanDefinitions(XmlBeanFactoryTests.MISSING_CONTEXT);
    }

    @Test(expected = BeanDefinitionStoreException.class)
    public void invalidXmlFile() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(xbf).loadBeanDefinitions(XmlBeanFactoryTests.INVALID_CONTEXT);
    }

    @Test
    public void testAutowire() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(xbf).loadBeanDefinitions(XmlBeanFactoryTests.AUTOWIRE_CONTEXT);
        TestBean spouse = new TestBean("kerry", 0);
        xbf.registerSingleton("spouse", spouse);
        doTestAutowire(xbf);
    }

    @Test
    public void testAutowireWithParent() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(xbf).loadBeanDefinitions(XmlBeanFactoryTests.AUTOWIRE_CONTEXT);
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        MutablePropertyValues pvs = new MutablePropertyValues();
        pvs.add("name", "kerry");
        RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
        bd.setPropertyValues(pvs);
        lbf.registerBeanDefinition("spouse", bd);
        xbf.setParentBeanFactory(lbf);
        doTestAutowire(xbf);
    }

    @Test
    public void testAutowireWithDefault() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(xbf).loadBeanDefinitions(XmlBeanFactoryTests.DEFAULT_AUTOWIRE_CONTEXT);
        DependenciesBean rod1 = ((DependenciesBean) (xbf.getBean("rod1")));
        // should have been autowired
        Assert.assertNotNull(rod1.getSpouse());
        Assert.assertTrue(rod1.getSpouse().getName().equals("Kerry"));
        DependenciesBean rod2 = ((DependenciesBean) (xbf.getBean("rod2")));
        // should have been autowired
        Assert.assertNotNull(rod2.getSpouse());
        Assert.assertTrue(rod2.getSpouse().getName().equals("Kerry"));
    }

    @Test
    public void testAutowireByConstructor() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(xbf).loadBeanDefinitions(XmlBeanFactoryTests.CONSTRUCTOR_ARG_CONTEXT);
        ConstructorDependenciesBean rod1 = ((ConstructorDependenciesBean) (xbf.getBean("rod1")));
        TestBean kerry = ((TestBean) (xbf.getBean("kerry2")));
        // should have been autowired
        Assert.assertEquals(kerry, rod1.getSpouse1());
        Assert.assertEquals(0, rod1.getAge());
        Assert.assertEquals(null, rod1.getName());
        ConstructorDependenciesBean rod2 = ((ConstructorDependenciesBean) (xbf.getBean("rod2")));
        TestBean kerry1 = ((TestBean) (xbf.getBean("kerry1")));
        TestBean kerry2 = ((TestBean) (xbf.getBean("kerry2")));
        // should have been autowired
        Assert.assertEquals(kerry2, rod2.getSpouse1());
        Assert.assertEquals(kerry1, rod2.getSpouse2());
        Assert.assertEquals(0, rod2.getAge());
        Assert.assertEquals(null, rod2.getName());
        ConstructorDependenciesBean rod = ((ConstructorDependenciesBean) (xbf.getBean("rod3")));
        IndexedTestBean other = ((IndexedTestBean) (xbf.getBean("other")));
        // should have been autowired
        Assert.assertEquals(kerry, rod.getSpouse1());
        Assert.assertEquals(kerry, rod.getSpouse2());
        Assert.assertEquals(other, rod.getOther());
        Assert.assertEquals(0, rod.getAge());
        Assert.assertEquals(null, rod.getName());
        xbf.getBean("rod4", ConstructorDependenciesBean.class);
        // should have been autowired
        Assert.assertEquals(kerry, rod.getSpouse1());
        Assert.assertEquals(kerry, rod.getSpouse2());
        Assert.assertEquals(other, rod.getOther());
        Assert.assertEquals(0, rod.getAge());
        Assert.assertEquals(null, rod.getName());
    }

    @Test
    public void testAutowireByConstructorWithSimpleValues() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(xbf).loadBeanDefinitions(XmlBeanFactoryTests.CONSTRUCTOR_ARG_CONTEXT);
        ConstructorDependenciesBean rod5 = ((ConstructorDependenciesBean) (xbf.getBean("rod5")));
        TestBean kerry1 = ((TestBean) (xbf.getBean("kerry1")));
        TestBean kerry2 = ((TestBean) (xbf.getBean("kerry2")));
        IndexedTestBean other = ((IndexedTestBean) (xbf.getBean("other")));
        // should have been autowired
        Assert.assertEquals(kerry2, rod5.getSpouse1());
        Assert.assertEquals(kerry1, rod5.getSpouse2());
        Assert.assertEquals(other, rod5.getOther());
        Assert.assertEquals(99, rod5.getAge());
        Assert.assertEquals("myname", rod5.getName());
        DerivedConstructorDependenciesBean rod6 = ((DerivedConstructorDependenciesBean) (xbf.getBean("rod6")));
        // should have been autowired
        Assert.assertTrue(rod6.initialized);
        Assert.assertTrue((!(rod6.destroyed)));
        Assert.assertEquals(kerry2, rod6.getSpouse1());
        Assert.assertEquals(kerry1, rod6.getSpouse2());
        Assert.assertEquals(other, rod6.getOther());
        Assert.assertEquals(0, rod6.getAge());
        Assert.assertEquals(null, rod6.getName());
        xbf.destroySingletons();
        Assert.assertTrue(rod6.destroyed);
    }

    @Test
    public void testRelatedCausesFromConstructorResolution() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(xbf).loadBeanDefinitions(XmlBeanFactoryTests.CONSTRUCTOR_ARG_CONTEXT);
        try {
            xbf.getBean("rod2Accessor");
        } catch (BeanCreationException ex) {
            Assert.assertTrue(ex.toString().contains("touchy"));
            ex.printStackTrace();
            Assert.assertNull(ex.getRelatedCauses());
        }
    }

    @Test
    public void testConstructorArgResolution() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(xbf).loadBeanDefinitions(XmlBeanFactoryTests.CONSTRUCTOR_ARG_CONTEXT);
        TestBean kerry1 = ((TestBean) (xbf.getBean("kerry1")));
        TestBean kerry2 = ((TestBean) (xbf.getBean("kerry2")));
        ConstructorDependenciesBean rod9 = ((ConstructorDependenciesBean) (xbf.getBean("rod9")));
        Assert.assertEquals(99, rod9.getAge());
        ConstructorDependenciesBean rod9a = ((ConstructorDependenciesBean) (xbf.getBean("rod9", 98)));
        Assert.assertEquals(98, rod9a.getAge());
        ConstructorDependenciesBean rod9b = ((ConstructorDependenciesBean) (xbf.getBean("rod9", "myName")));
        Assert.assertEquals("myName", rod9b.getName());
        ConstructorDependenciesBean rod9c = ((ConstructorDependenciesBean) (xbf.getBean("rod9", 97)));
        Assert.assertEquals(97, rod9c.getAge());
        ConstructorDependenciesBean rod10 = ((ConstructorDependenciesBean) (xbf.getBean("rod10")));
        Assert.assertEquals(null, rod10.getName());
        ConstructorDependenciesBean rod11 = ((ConstructorDependenciesBean) (xbf.getBean("rod11")));
        Assert.assertEquals(kerry2, rod11.getSpouse1());
        ConstructorDependenciesBean rod12 = ((ConstructorDependenciesBean) (xbf.getBean("rod12")));
        Assert.assertEquals(kerry1, rod12.getSpouse1());
        Assert.assertNull(rod12.getSpouse2());
        ConstructorDependenciesBean rod13 = ((ConstructorDependenciesBean) (xbf.getBean("rod13")));
        Assert.assertEquals(kerry1, rod13.getSpouse1());
        Assert.assertEquals(kerry2, rod13.getSpouse2());
        ConstructorDependenciesBean rod14 = ((ConstructorDependenciesBean) (xbf.getBean("rod14")));
        Assert.assertEquals(kerry1, rod14.getSpouse1());
        Assert.assertEquals(kerry2, rod14.getSpouse2());
        ConstructorDependenciesBean rod15 = ((ConstructorDependenciesBean) (xbf.getBean("rod15")));
        Assert.assertEquals(kerry2, rod15.getSpouse1());
        Assert.assertEquals(kerry1, rod15.getSpouse2());
        ConstructorDependenciesBean rod16 = ((ConstructorDependenciesBean) (xbf.getBean("rod16")));
        Assert.assertEquals(kerry2, rod16.getSpouse1());
        Assert.assertEquals(kerry1, rod16.getSpouse2());
        Assert.assertEquals(29, rod16.getAge());
        ConstructorDependenciesBean rod17 = ((ConstructorDependenciesBean) (xbf.getBean("rod17")));
        Assert.assertEquals(kerry1, rod17.getSpouse1());
        Assert.assertEquals(kerry2, rod17.getSpouse2());
        Assert.assertEquals(29, rod17.getAge());
    }

    @Test
    public void testPrototypeWithExplicitArguments() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(xbf).loadBeanDefinitions(XmlBeanFactoryTests.CONSTRUCTOR_ARG_CONTEXT);
        SimpleConstructorArgBean cd1 = ((SimpleConstructorArgBean) (xbf.getBean("rod18")));
        Assert.assertEquals(0, cd1.getAge());
        SimpleConstructorArgBean cd2 = ((SimpleConstructorArgBean) (xbf.getBean("rod18", 98)));
        Assert.assertEquals(98, cd2.getAge());
        SimpleConstructorArgBean cd3 = ((SimpleConstructorArgBean) (xbf.getBean("rod18", "myName")));
        Assert.assertEquals("myName", cd3.getName());
        SimpleConstructorArgBean cd4 = ((SimpleConstructorArgBean) (xbf.getBean("rod18")));
        Assert.assertEquals(0, cd4.getAge());
        SimpleConstructorArgBean cd5 = ((SimpleConstructorArgBean) (xbf.getBean("rod18", 97)));
        Assert.assertEquals(97, cd5.getAge());
    }

    @Test
    public void testConstructorArgWithSingleMatch() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(xbf).loadBeanDefinitions(XmlBeanFactoryTests.CONSTRUCTOR_ARG_CONTEXT);
        File file = ((File) (xbf.getBean("file")));
        Assert.assertEquals(((File.separator) + "test"), file.getPath());
    }

    @Test(expected = BeanCreationException.class)
    public void throwsExceptionOnTooManyArguments() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(xbf).loadBeanDefinitions(XmlBeanFactoryTests.CONSTRUCTOR_ARG_CONTEXT);
        xbf.getBean("rod7", ConstructorDependenciesBean.class);
    }

    @Test(expected = UnsatisfiedDependencyException.class)
    public void throwsExceptionOnAmbiguousResolution() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(xbf).loadBeanDefinitions(XmlBeanFactoryTests.CONSTRUCTOR_ARG_CONTEXT);
        xbf.getBean("rod8", ConstructorDependenciesBean.class);
    }

    @Test
    public void testDependsOn() {
        doTestDependencies(XmlBeanFactoryTests.DEP_DEPENDSON_CONTEXT, 1);
    }

    @Test
    public void testDependsOnInInnerBean() {
        doTestDependencies(XmlBeanFactoryTests.DEP_DEPENDSON_INNER_CONTEXT, 4);
    }

    @Test
    public void testDependenciesThroughConstructorArguments() {
        doTestDependencies(XmlBeanFactoryTests.DEP_CARG_CONTEXT, 1);
    }

    @Test
    public void testDependenciesThroughConstructorArgumentAutowiring() {
        doTestDependencies(XmlBeanFactoryTests.DEP_CARG_AUTOWIRE_CONTEXT, 1);
    }

    @Test
    public void testDependenciesThroughConstructorArgumentsInInnerBean() {
        doTestDependencies(XmlBeanFactoryTests.DEP_CARG_INNER_CONTEXT, 1);
    }

    @Test
    public void testDependenciesThroughProperties() {
        doTestDependencies(XmlBeanFactoryTests.DEP_PROP, 1);
    }

    @Test
    public void testDependenciesThroughPropertiesWithInTheMiddle() {
        doTestDependencies(XmlBeanFactoryTests.DEP_PROP_MIDDLE_CONTEXT, 1);
    }

    @Test
    public void testDependenciesThroughPropertyAutowiringByName() {
        doTestDependencies(XmlBeanFactoryTests.DEP_PROP_ABN_CONTEXT, 1);
    }

    @Test
    public void testDependenciesThroughPropertyAutowiringByType() {
        doTestDependencies(XmlBeanFactoryTests.DEP_PROP_ABT_CONTEXT, 1);
    }

    @Test
    public void testDependenciesThroughPropertiesInInnerBean() {
        doTestDependencies(XmlBeanFactoryTests.DEP_PROP_INNER_CONTEXT, 1);
    }

    /**
     * When using a BeanFactory. singletons are of course not pre-instantiated.
     * So rubbish class names in bean defs must now not be 'resolved' when the
     * bean def is being parsed, 'cos everything on a bean def is now lazy, but
     * must rather only be picked up when the bean is instantiated.
     */
    @Test
    public void testClassNotFoundWithDefaultBeanClassLoader() {
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(factory).loadBeanDefinitions(XmlBeanFactoryTests.CLASS_NOT_FOUND_CONTEXT);
        // cool, no errors, so the rubbish class name in the bean def was not resolved
        try {
            // let's resolve the bean definition; must blow up
            factory.getBean("classNotFound");
            Assert.fail("Must have thrown a CannotLoadBeanClassException");
        } catch (CannotLoadBeanClassException ex) {
            Assert.assertTrue(ex.getResourceDescription().contains("classNotFound.xml"));
            Assert.assertTrue(((ex.getCause()) instanceof ClassNotFoundException));
        }
    }

    @Test
    public void testClassNotFoundWithNoBeanClassLoader() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(bf);
        reader.setBeanClassLoader(null);
        reader.loadBeanDefinitions(XmlBeanFactoryTests.CLASS_NOT_FOUND_CONTEXT);
        Assert.assertEquals("WhatALotOfRubbish", bf.getBeanDefinition("classNotFound").getBeanClassName());
    }

    @Test
    public void testResourceAndInputStream() throws IOException {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(xbf).loadBeanDefinitions(XmlBeanFactoryTests.RESOURCE_CONTEXT);
        // comes from "resourceImport.xml"
        ResourceTestBean resource1 = ((ResourceTestBean) (xbf.getBean("resource1")));
        // comes from "resource.xml"
        ResourceTestBean resource2 = ((ResourceTestBean) (xbf.getBean("resource2")));
        Assert.assertTrue(((resource1.getResource()) instanceof ClassPathResource));
        StringWriter writer = new StringWriter();
        FileCopyUtils.copy(new InputStreamReader(resource1.getResource().getInputStream()), writer);
        Assert.assertEquals("test", writer.toString());
        writer = new StringWriter();
        FileCopyUtils.copy(new InputStreamReader(resource1.getInputStream()), writer);
        Assert.assertEquals("test", writer.toString());
        writer = new StringWriter();
        FileCopyUtils.copy(new InputStreamReader(resource2.getResource().getInputStream()), writer);
        Assert.assertEquals("test", writer.toString());
        writer = new StringWriter();
        FileCopyUtils.copy(new InputStreamReader(resource2.getInputStream()), writer);
        Assert.assertEquals("test", writer.toString());
    }

    @Test
    public void testClassPathResourceWithImport() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(xbf).loadBeanDefinitions(XmlBeanFactoryTests.RESOURCE_CONTEXT);
        // comes from "resourceImport.xml"
        xbf.getBean("resource1", ResourceTestBean.class);
        // comes from "resource.xml"
        xbf.getBean("resource2", ResourceTestBean.class);
    }

    @Test
    public void testUrlResourceWithImport() {
        URL url = getClass().getResource(XmlBeanFactoryTests.RESOURCE_CONTEXT.getPath());
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(xbf).loadBeanDefinitions(new UrlResource(url));
        // comes from "resourceImport.xml"
        xbf.getBean("resource1", ResourceTestBean.class);
        // comes from "resource.xml"
        xbf.getBean("resource2", ResourceTestBean.class);
    }

    @Test
    public void testFileSystemResourceWithImport() throws URISyntaxException {
        String file = getClass().getResource(XmlBeanFactoryTests.RESOURCE_CONTEXT.getPath()).toURI().getPath();
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(xbf).loadBeanDefinitions(new FileSystemResource(file));
        // comes from "resourceImport.xml"
        xbf.getBean("resource1", ResourceTestBean.class);
        // comes from "resource.xml"
        xbf.getBean("resource2", ResourceTestBean.class);
    }

    @Test(expected = BeanDefinitionStoreException.class)
    public void recursiveImport() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(xbf).loadBeanDefinitions(XmlBeanFactoryTests.RECURSIVE_IMPORT_CONTEXT);
    }

    /**
     *
     *
     * @since 3.2.8 and 4.0.2
     * @see <a href="https://jira.spring.io/browse/SPR-10785">SPR-10785</a> and <a
    href="https://jira.spring.io/browse/SPR-11420">SPR-11420</a>
     */
    @Test
    public void methodInjectedBeanMustBeOfSameEnhancedCglibSubclassTypeAcrossBeanFactories() {
        Class<?> firstClass = null;
        for (int i = 0; i < 10; i++) {
            DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
            new XmlBeanDefinitionReader(bf).loadBeanDefinitions(XmlBeanFactoryTests.OVERRIDES_CONTEXT);
            final Class<?> currentClass = bf.getBean("overrideOneMethod").getClass();
            Assert.assertTrue((("Method injected bean class [" + currentClass) + "] must be a CGLIB enhanced subclass."), ClassUtils.isCglibProxyClass(currentClass));
            if (firstClass == null) {
                firstClass = currentClass;
            } else {
                Assert.assertEquals(firstClass, currentClass);
            }
        }
    }

    @Test
    public void lookupOverrideMethodsWithSetterInjection() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(xbf);
        reader.loadBeanDefinitions(XmlBeanFactoryTests.OVERRIDES_CONTEXT);
        lookupOverrideMethodsWithSetterInjection(xbf, "overrideOneMethod", true);
        // Should work identically on subclass definition, in which lookup
        // methods are inherited
        lookupOverrideMethodsWithSetterInjection(xbf, "overrideInheritedMethod", true);
        // Check cost of repeated construction of beans with method overrides
        // Will pick up misuse of CGLIB
        int howMany = 100;
        StopWatch sw = new StopWatch();
        sw.start((("Look up " + howMany) + " prototype bean instances with method overrides"));
        for (int i = 0; i < howMany; i++) {
            lookupOverrideMethodsWithSetterInjection(xbf, "overrideOnPrototype", false);
        }
        sw.stop();
        // System.out.println(sw);
        if (!(LogFactory.getLog(DefaultListableBeanFactory.class).isDebugEnabled())) {
            Assert.assertTrue(((sw.getTotalTimeMillis()) < 2000));
        }
        // Now test distinct bean with swapped value in factory, to ensure the two are independent
        OverrideOneMethod swappedOom = ((OverrideOneMethod) (xbf.getBean("overrideOneMethodSwappedReturnValues")));
        TestBean tb = swappedOom.getPrototypeDependency();
        Assert.assertEquals("David", tb.getName());
        tb = swappedOom.protectedOverrideSingleton();
        Assert.assertEquals("Jenny", tb.getName());
    }

    @Test
    public void testReplaceMethodOverrideWithSetterInjection() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(xbf);
        reader.loadBeanDefinitions(XmlBeanFactoryTests.DELEGATION_OVERRIDES_CONTEXT);
        OverrideOneMethod oom = ((OverrideOneMethod) (xbf.getBean("overrideOneMethod")));
        // Same contract as for overrides.xml
        TestBean jenny1 = oom.getPrototypeDependency();
        Assert.assertEquals("Jenny", jenny1.getName());
        TestBean jenny2 = oom.getPrototypeDependency();
        Assert.assertEquals("Jenny", jenny2.getName());
        Assert.assertNotSame(jenny1, jenny2);
        TestBean notJenny = oom.getPrototypeDependency("someParam");
        Assert.assertTrue((!("Jenny".equals(notJenny.getName()))));
        // Now try protected method, and singleton
        TestBean dave1 = oom.protectedOverrideSingleton();
        Assert.assertEquals("David", dave1.getName());
        TestBean dave2 = oom.protectedOverrideSingleton();
        Assert.assertEquals("David", dave2.getName());
        Assert.assertSame(dave1, dave2);
        // Check unadvised behaviour
        String str = "woierowijeiowiej";
        Assert.assertEquals(str, oom.echo(str));
        // Now test replace
        String s = "this is not a palindrome";
        String reverse = new StringBuffer(s).reverse().toString();
        Assert.assertEquals("Should have overridden to reverse, not echo", reverse, oom.replaceMe(s));
        Assert.assertEquals("Should have overridden no-arg overloaded replaceMe method to return fixed value", FixedMethodReplacer.VALUE, oom.replaceMe());
        OverrideOneMethodSubclass ooms = ((OverrideOneMethodSubclass) (xbf.getBean("replaceVoidMethod")));
        XmlBeanFactoryTests.DoSomethingReplacer dos = ((XmlBeanFactoryTests.DoSomethingReplacer) (xbf.getBean("doSomethingReplacer")));
        Assert.assertEquals(null, dos.lastArg);
        String s1 = "";
        String s2 = "foo bar black sheep";
        ooms.doSomething(s1);
        Assert.assertEquals(s1, dos.lastArg);
        ooms.doSomething(s2);
        Assert.assertEquals(s2, dos.lastArg);
    }

    @Test
    public void lookupOverrideOneMethodWithConstructorInjection() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(xbf);
        reader.loadBeanDefinitions(XmlBeanFactoryTests.CONSTRUCTOR_OVERRIDES_CONTEXT);
        ConstructorInjectedOverrides cio = ((ConstructorInjectedOverrides) (xbf.getBean("constructorOverrides")));
        // Check that the setter was invoked...
        // We should be able to combine Constructor and
        // Setter Injection
        Assert.assertEquals("Setter string was set", "from property element", cio.getSetterString());
        // Jenny is a singleton
        TestBean jenny = ((TestBean) (xbf.getBean("jenny")));
        Assert.assertSame(jenny, cio.getTestBean());
        Assert.assertSame(jenny, cio.getTestBean());
        FactoryMethods fm1 = cio.createFactoryMethods();
        FactoryMethods fm2 = cio.createFactoryMethods();
        Assert.assertNotSame("FactoryMethods reference is to a prototype", fm1, fm2);
        Assert.assertSame("The two prototypes hold the same singleton reference", fm1.getTestBean(), fm2.getTestBean());
    }

    @Test
    public void testRejectsOverrideOfBogusMethodName() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(xbf);
        try {
            reader.loadBeanDefinitions(XmlBeanFactoryTests.INVALID_NO_SUCH_METHOD_CONTEXT);
            xbf.getBean("constructorOverrides");
            Assert.fail("Shouldn't allow override of bogus method");
        } catch (BeanDefinitionStoreException ex) {
            // Check that the bogus method name was included in the error message
            Assert.assertTrue("Bogus method name correctly reported", ex.getMessage().contains("bogusMethod"));
        }
    }

    @Test
    public void serializableMethodReplacerAndSuperclass() throws IOException {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(xbf);
        reader.loadBeanDefinitions(XmlBeanFactoryTests.DELEGATION_OVERRIDES_CONTEXT);
        SerializableMethodReplacerCandidate s = ((SerializableMethodReplacerCandidate) (xbf.getBean("serializableReplacer")));
        String forwards = "this is forwards";
        String backwards = new StringBuffer(forwards).reverse().toString();
        Assert.assertEquals(backwards, s.replaceMe(forwards));
        // SPR-356: lookup methods & method replacers are not serializable.
        Assert.assertFalse("Lookup methods and method replacers are not meant to be serializable.", SerializationTestUtils.isSerializable(s));
    }

    @Test
    public void testInnerBeanInheritsScopeFromConcreteChildDefinition() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(xbf);
        reader.loadBeanDefinitions(XmlBeanFactoryTests.OVERRIDES_CONTEXT);
        TestBean jenny1 = ((TestBean) (xbf.getBean("jennyChild")));
        Assert.assertEquals(1, jenny1.getFriends().size());
        Object friend1 = jenny1.getFriends().iterator().next();
        Assert.assertTrue((friend1 instanceof TestBean));
        TestBean jenny2 = ((TestBean) (xbf.getBean("jennyChild")));
        Assert.assertEquals(1, jenny2.getFriends().size());
        Object friend2 = jenny2.getFriends().iterator().next();
        Assert.assertTrue((friend2 instanceof TestBean));
        Assert.assertNotSame(jenny1, jenny2);
        Assert.assertNotSame(friend1, friend2);
    }

    @Test
    public void testConstructorArgWithSingleSimpleTypeMatch() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(xbf).loadBeanDefinitions(XmlBeanFactoryTests.CONSTRUCTOR_ARG_CONTEXT);
        SingleSimpleTypeConstructorBean bean = ((SingleSimpleTypeConstructorBean) (xbf.getBean("beanWithBoolean")));
        Assert.assertTrue(bean.isSingleBoolean());
        SingleSimpleTypeConstructorBean bean2 = ((SingleSimpleTypeConstructorBean) (xbf.getBean("beanWithBoolean2")));
        Assert.assertTrue(bean2.isSingleBoolean());
    }

    @Test
    public void testConstructorArgWithDoubleSimpleTypeMatch() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(xbf).loadBeanDefinitions(XmlBeanFactoryTests.CONSTRUCTOR_ARG_CONTEXT);
        SingleSimpleTypeConstructorBean bean = ((SingleSimpleTypeConstructorBean) (xbf.getBean("beanWithBooleanAndString")));
        Assert.assertTrue(bean.isSecondBoolean());
        Assert.assertEquals("A String", bean.getTestString());
        SingleSimpleTypeConstructorBean bean2 = ((SingleSimpleTypeConstructorBean) (xbf.getBean("beanWithBooleanAndString2")));
        Assert.assertTrue(bean2.isSecondBoolean());
        Assert.assertEquals("A String", bean2.getTestString());
    }

    @Test
    public void testDoubleBooleanAutowire() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(xbf).loadBeanDefinitions(XmlBeanFactoryTests.CONSTRUCTOR_ARG_CONTEXT);
        XmlBeanFactoryTests.DoubleBooleanConstructorBean bean = ((XmlBeanFactoryTests.DoubleBooleanConstructorBean) (xbf.getBean("beanWithDoubleBoolean")));
        Assert.assertEquals(Boolean.TRUE, bean.boolean1);
        Assert.assertEquals(Boolean.FALSE, bean.boolean2);
    }

    @Test
    public void testDoubleBooleanAutowireWithIndex() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(xbf).loadBeanDefinitions(XmlBeanFactoryTests.CONSTRUCTOR_ARG_CONTEXT);
        XmlBeanFactoryTests.DoubleBooleanConstructorBean bean = ((XmlBeanFactoryTests.DoubleBooleanConstructorBean) (xbf.getBean("beanWithDoubleBooleanAndIndex")));
        Assert.assertEquals(Boolean.FALSE, bean.boolean1);
        Assert.assertEquals(Boolean.TRUE, bean.boolean2);
    }

    @Test
    public void testLenientDependencyMatching() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(xbf).loadBeanDefinitions(XmlBeanFactoryTests.CONSTRUCTOR_ARG_CONTEXT);
        XmlBeanFactoryTests.LenientDependencyTestBean bean = ((XmlBeanFactoryTests.LenientDependencyTestBean) (xbf.getBean("lenientDependencyTestBean")));
        Assert.assertTrue(((bean.tb) instanceof DerivedTestBean));
    }

    @Test
    public void testLenientDependencyMatchingFactoryMethod() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(xbf).loadBeanDefinitions(XmlBeanFactoryTests.CONSTRUCTOR_ARG_CONTEXT);
        XmlBeanFactoryTests.LenientDependencyTestBean bean = ((XmlBeanFactoryTests.LenientDependencyTestBean) (xbf.getBean("lenientDependencyTestBeanFactoryMethod")));
        Assert.assertTrue(((bean.tb) instanceof DerivedTestBean));
    }

    @Test
    public void testNonLenientDependencyMatching() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(xbf).loadBeanDefinitions(XmlBeanFactoryTests.CONSTRUCTOR_ARG_CONTEXT);
        AbstractBeanDefinition bd = ((AbstractBeanDefinition) (xbf.getBeanDefinition("lenientDependencyTestBean")));
        bd.setLenientConstructorResolution(false);
        try {
            xbf.getBean("lenientDependencyTestBean");
            Assert.fail("Should have thrown BeanCreationException");
        } catch (BeanCreationException ex) {
            // expected
            ex.printStackTrace();
            Assert.assertTrue(ex.getMostSpecificCause().getMessage().contains("Ambiguous"));
        }
    }

    @Test
    public void testNonLenientDependencyMatchingFactoryMethod() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(xbf).loadBeanDefinitions(XmlBeanFactoryTests.CONSTRUCTOR_ARG_CONTEXT);
        AbstractBeanDefinition bd = ((AbstractBeanDefinition) (xbf.getBeanDefinition("lenientDependencyTestBeanFactoryMethod")));
        bd.setLenientConstructorResolution(false);
        try {
            xbf.getBean("lenientDependencyTestBeanFactoryMethod");
            Assert.fail("Should have thrown BeanCreationException");
        } catch (BeanCreationException ex) {
            // expected
            ex.printStackTrace();
            Assert.assertTrue(ex.getMostSpecificCause().getMessage().contains("Ambiguous"));
        }
    }

    @Test
    public void testJavaLangStringConstructor() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(xbf).loadBeanDefinitions(XmlBeanFactoryTests.CONSTRUCTOR_ARG_CONTEXT);
        AbstractBeanDefinition bd = ((AbstractBeanDefinition) (xbf.getBeanDefinition("string")));
        bd.setLenientConstructorResolution(false);
        String str = ((String) (xbf.getBean("string")));
        Assert.assertEquals("test", str);
    }

    @Test
    public void testCustomStringConstructor() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(xbf).loadBeanDefinitions(XmlBeanFactoryTests.CONSTRUCTOR_ARG_CONTEXT);
        AbstractBeanDefinition bd = ((AbstractBeanDefinition) (xbf.getBeanDefinition("stringConstructor")));
        bd.setLenientConstructorResolution(false);
        XmlBeanFactoryTests.StringConstructorTestBean tb = ((XmlBeanFactoryTests.StringConstructorTestBean) (xbf.getBean("stringConstructor")));
        Assert.assertEquals("test", tb.name);
    }

    @Test
    public void testPrimitiveConstructorArray() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(xbf).loadBeanDefinitions(XmlBeanFactoryTests.CONSTRUCTOR_ARG_CONTEXT);
        XmlBeanFactoryTests.ConstructorArrayTestBean bean = ((XmlBeanFactoryTests.ConstructorArrayTestBean) (xbf.getBean("constructorArray")));
        Assert.assertTrue(((bean.array) instanceof int[]));
        Assert.assertEquals(1, ((int[]) (bean.array)).length);
        Assert.assertEquals(1, ((int[]) (bean.array))[0]);
    }

    @Test
    public void testIndexedPrimitiveConstructorArray() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(xbf).loadBeanDefinitions(XmlBeanFactoryTests.CONSTRUCTOR_ARG_CONTEXT);
        XmlBeanFactoryTests.ConstructorArrayTestBean bean = ((XmlBeanFactoryTests.ConstructorArrayTestBean) (xbf.getBean("indexedConstructorArray")));
        Assert.assertTrue(((bean.array) instanceof int[]));
        Assert.assertEquals(1, ((int[]) (bean.array)).length);
        Assert.assertEquals(1, ((int[]) (bean.array))[0]);
    }

    @Test
    public void testStringConstructorArrayNoType() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(xbf).loadBeanDefinitions(XmlBeanFactoryTests.CONSTRUCTOR_ARG_CONTEXT);
        XmlBeanFactoryTests.ConstructorArrayTestBean bean = ((XmlBeanFactoryTests.ConstructorArrayTestBean) (xbf.getBean("constructorArrayNoType")));
        Assert.assertTrue(((bean.array) instanceof String[]));
        Assert.assertEquals(0, ((String[]) (bean.array)).length);
    }

    @Test
    public void testStringConstructorArrayNoTypeNonLenient() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(xbf).loadBeanDefinitions(XmlBeanFactoryTests.CONSTRUCTOR_ARG_CONTEXT);
        AbstractBeanDefinition bd = ((AbstractBeanDefinition) (xbf.getBeanDefinition("constructorArrayNoType")));
        bd.setLenientConstructorResolution(false);
        XmlBeanFactoryTests.ConstructorArrayTestBean bean = ((XmlBeanFactoryTests.ConstructorArrayTestBean) (xbf.getBean("constructorArrayNoType")));
        Assert.assertTrue(((bean.array) instanceof String[]));
        Assert.assertEquals(0, ((String[]) (bean.array)).length);
    }

    @Test
    public void testConstructorWithUnresolvableParameterName() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(xbf).loadBeanDefinitions(XmlBeanFactoryTests.CONSTRUCTOR_ARG_CONTEXT);
        AtomicInteger bean = ((AtomicInteger) (xbf.getBean("constructorUnresolvableName")));
        Assert.assertEquals(1, bean.get());
        bean = ((AtomicInteger) (xbf.getBean("constructorUnresolvableNameWithIndex")));
        Assert.assertEquals(1, bean.get());
    }

    @Test
    public void testWithDuplicateName() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        try {
            new XmlBeanDefinitionReader(xbf).loadBeanDefinitions(XmlBeanFactoryTests.TEST_WITH_DUP_NAMES_CONTEXT);
            Assert.fail("Duplicate name not detected");
        } catch (BeansException ex) {
            Assert.assertTrue(ex.getMessage().contains("Bean name 'foo'"));
        }
    }

    @Test
    public void testWithDuplicateNameInAlias() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        try {
            new XmlBeanDefinitionReader(xbf).loadBeanDefinitions(XmlBeanFactoryTests.TEST_WITH_DUP_NAME_IN_ALIAS_CONTEXT);
            Assert.fail("Duplicate name not detected");
        } catch (BeansException e) {
            Assert.assertTrue(e.getMessage().contains("Bean name 'foo'"));
        }
    }

    @Test
    public void testOverrideMethodByArgTypeAttribute() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(xbf);
        reader.loadBeanDefinitions(XmlBeanFactoryTests.DELEGATION_OVERRIDES_CONTEXT);
        OverrideOneMethod oom = ((OverrideOneMethod) (xbf.getBean("overrideOneMethodByAttribute")));
        Assert.assertEquals("should not replace", "replaceMe:1", oom.replaceMe(1));
        Assert.assertEquals("should replace", "cba", oom.replaceMe("abc"));
    }

    @Test
    public void testOverrideMethodByArgTypeElement() {
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(xbf);
        reader.loadBeanDefinitions(XmlBeanFactoryTests.DELEGATION_OVERRIDES_CONTEXT);
        OverrideOneMethod oom = ((OverrideOneMethod) (xbf.getBean("overrideOneMethodByElement")));
        Assert.assertEquals("should not replace", "replaceMe:1", oom.replaceMe(1));
        Assert.assertEquals("should replace", "cba", oom.replaceMe("abc"));
    }

    public static class DoSomethingReplacer implements MethodReplacer {
        public Object lastArg;

        @Override
        public Object reimplement(Object obj, Method method, Object[] args) throws Throwable {
            Assert.assertEquals(1, args.length);
            Assert.assertEquals("doSomething", method.getName());
            lastArg = args[0];
            return null;
        }
    }

    public static class BadInitializer {
        /**
         * Init method
         */
        public void init2() throws IOException {
            throw new IOException();
        }
    }

    public static class DoubleInitializer {
        private int num;

        public int getNum() {
            return num;
        }

        public void setNum(int i) {
            num = i;
        }

        /**
         * Init method
         */
        public void init() {
            this.num *= 2;
        }
    }

    public static class InitAndIB implements DisposableBean , InitializingBean {
        public static boolean constructed;

        public boolean afterPropertiesSetInvoked;

        public boolean initMethodInvoked;

        public boolean destroyed;

        public boolean customDestroyed;

        public InitAndIB() {
            XmlBeanFactoryTests.InitAndIB.constructed = true;
        }

        @Override
        public void afterPropertiesSet() {
            if (this.initMethodInvoked) {
                Assert.fail();
            }
            if (this.afterPropertiesSetInvoked) {
                throw new IllegalStateException("Already initialized");
            }
            this.afterPropertiesSetInvoked = true;
        }

        /**
         * Init method
         */
        public void customInit() throws IOException {
            if (!(this.afterPropertiesSetInvoked)) {
                Assert.fail();
            }
            if (this.initMethodInvoked) {
                throw new IllegalStateException("Already customInitialized");
            }
            this.initMethodInvoked = true;
        }

        @Override
        public void destroy() {
            if (this.customDestroyed) {
                Assert.fail();
            }
            if (this.destroyed) {
                throw new IllegalStateException("Already destroyed");
            }
            this.destroyed = true;
        }

        public void customDestroy() {
            if (!(this.destroyed)) {
                Assert.fail();
            }
            if (this.customDestroyed) {
                throw new IllegalStateException("Already customDestroyed");
            }
            this.customDestroyed = true;
        }
    }

    public static class PreparingBean1 implements DisposableBean {
        public static boolean prepared = false;

        public static boolean destroyed = false;

        public PreparingBean1() {
            XmlBeanFactoryTests.PreparingBean1.prepared = true;
        }

        @Override
        public void destroy() {
            XmlBeanFactoryTests.PreparingBean1.destroyed = true;
        }
    }

    public static class PreparingBean2 implements DisposableBean {
        public static boolean prepared = false;

        public static boolean destroyed = false;

        public PreparingBean2() {
            XmlBeanFactoryTests.PreparingBean2.prepared = true;
        }

        @Override
        public void destroy() {
            XmlBeanFactoryTests.PreparingBean2.destroyed = true;
        }
    }

    public static class DependingBean implements DisposableBean , InitializingBean {
        public static int destroyCount = 0;

        public boolean destroyed = false;

        public DependingBean() {
        }

        public DependingBean(XmlBeanFactoryTests.PreparingBean1 bean1, XmlBeanFactoryTests.PreparingBean2 bean2) {
        }

        public void setBean1(XmlBeanFactoryTests.PreparingBean1 bean1) {
        }

        public void setBean2(XmlBeanFactoryTests.PreparingBean2 bean2) {
        }

        public void setInTheMiddleBean(XmlBeanFactoryTests.InTheMiddleBean bean) {
        }

        @Override
        public void afterPropertiesSet() {
            if (!((XmlBeanFactoryTests.PreparingBean1.prepared) && (XmlBeanFactoryTests.PreparingBean2.prepared))) {
                throw new IllegalStateException("Need prepared PreparingBeans!");
            }
        }

        @Override
        public void destroy() {
            if ((XmlBeanFactoryTests.PreparingBean1.destroyed) || (XmlBeanFactoryTests.PreparingBean2.destroyed)) {
                throw new IllegalStateException("Should not be destroyed after PreparingBeans");
            }
            destroyed = true;
            (XmlBeanFactoryTests.DependingBean.destroyCount)++;
        }
    }

    public static class InTheMiddleBean {
        public void setBean1(XmlBeanFactoryTests.PreparingBean1 bean1) {
        }

        public void setBean2(XmlBeanFactoryTests.PreparingBean2 bean2) {
        }
    }

    public static class HoldingBean implements DisposableBean {
        public static int destroyCount = 0;

        private XmlBeanFactoryTests.DependingBean dependingBean;

        public boolean destroyed = false;

        public void setDependingBean(XmlBeanFactoryTests.DependingBean dependingBean) {
            this.dependingBean = dependingBean;
        }

        @Override
        public void destroy() {
            if (this.dependingBean.destroyed) {
                throw new IllegalStateException("Should not be destroyed after DependingBean");
            }
            this.destroyed = true;
            (XmlBeanFactoryTests.HoldingBean.destroyCount)++;
        }
    }

    public static class DoubleBooleanConstructorBean {
        private Boolean boolean1;

        private Boolean boolean2;

        public DoubleBooleanConstructorBean(Boolean b1, Boolean b2) {
            this.boolean1 = b1;
            this.boolean2 = b2;
        }

        public DoubleBooleanConstructorBean(String s1, String s2) {
            throw new IllegalStateException("Don't pick this constructor");
        }

        public static XmlBeanFactoryTests.DoubleBooleanConstructorBean create(Boolean b1, Boolean b2) {
            return new XmlBeanFactoryTests.DoubleBooleanConstructorBean(b1, b2);
        }

        public static XmlBeanFactoryTests.DoubleBooleanConstructorBean create(String s1, String s2) {
            return new XmlBeanFactoryTests.DoubleBooleanConstructorBean(s1, s2);
        }
    }

    public static class LenientDependencyTestBean {
        public final ITestBean tb;

        public LenientDependencyTestBean(ITestBean tb) {
            this.tb = tb;
        }

        public LenientDependencyTestBean(TestBean tb) {
            this.tb = tb;
        }

        public LenientDependencyTestBean(DerivedTestBean tb) {
            this.tb = tb;
        }

        @SuppressWarnings("rawtypes")
        public LenientDependencyTestBean(Map[] m) {
            throw new IllegalStateException("Don't pick this constructor");
        }

        public static XmlBeanFactoryTests.LenientDependencyTestBean create(ITestBean tb) {
            return new XmlBeanFactoryTests.LenientDependencyTestBean(tb);
        }

        public static XmlBeanFactoryTests.LenientDependencyTestBean create(TestBean tb) {
            return new XmlBeanFactoryTests.LenientDependencyTestBean(tb);
        }

        public static XmlBeanFactoryTests.LenientDependencyTestBean create(DerivedTestBean tb) {
            return new XmlBeanFactoryTests.LenientDependencyTestBean(tb);
        }
    }

    public static class ConstructorArrayTestBean {
        public final Object array;

        public ConstructorArrayTestBean(int[] array) {
            this.array = array;
        }

        public ConstructorArrayTestBean(float[] array) {
            this.array = array;
        }

        public ConstructorArrayTestBean(short[] array) {
            this.array = array;
        }

        public ConstructorArrayTestBean(String[] array) {
            this.array = array;
        }
    }

    public static class StringConstructorTestBean {
        public final String name;

        public StringConstructorTestBean(String name) {
            this.name = name;
        }
    }

    public static class WrappingPostProcessor implements BeanPostProcessor {
        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            ProxyFactory pf = new ProxyFactory(bean);
            return pf.getProxy();
        }
    }
}

