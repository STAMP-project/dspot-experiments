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
package org.springframework.beans.factory;


import AnnotationAwareOrderComparator.INSTANCE;
import AutowireCapableBeanFactory.AUTOWIRE_BY_NAME;
import RootBeanDefinition.AUTOWIRE_BY_TYPE;
import RootBeanDefinition.AUTOWIRE_CONSTRUCTOR;
import RootBeanDefinition.DEPENDENCY_CHECK_OBJECTS;
import RootBeanDefinition.SCOPE_PROTOTYPE;
import TestGroup.PERFORMANCE;
import java.io.Closeable;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import javax.annotation.Priority;
import javax.security.auth.Subject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionOverrideException;
import org.springframework.beans.factory.support.ChildBeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.ConstructorDependenciesBean;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.lang.Nullable;
import org.springframework.tests.Assume;
import org.springframework.tests.sample.beans.DependenciesBean;
import org.springframework.tests.sample.beans.DerivedTestBean;
import org.springframework.tests.sample.beans.ITestBean;
import org.springframework.tests.sample.beans.LifecycleBean;
import org.springframework.tests.sample.beans.NestedTestBean;
import org.springframework.tests.sample.beans.SideEffectBean;
import org.springframework.tests.sample.beans.TestBean;
import org.springframework.tests.sample.beans.factory.DummyFactory;
import org.springframework.util.SerializationTestUtils;
import org.springframework.util.StopWatch;
import org.springframework.util.StringValueResolver;


/**
 * Tests properties population and autowire behavior.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Rick Evans
 * @author Sam Brannen
 * @author Chris Beams
 * @author Phillip Webb
 * @author Stephane Nicoll
 */
public class DefaultListableBeanFactoryTests {
    private static final Log factoryLog = LogFactory.getLog(DefaultListableBeanFactory.class);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testUnreferencedSingletonWasInstantiated() {
        DefaultListableBeanFactoryTests.KnowsIfInstantiated.clearInstantiationRecord();
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        Properties p = new Properties();
        p.setProperty("x1.(class)", DefaultListableBeanFactoryTests.KnowsIfInstantiated.class.getName());
        Assert.assertTrue("singleton not instantiated", (!(DefaultListableBeanFactoryTests.KnowsIfInstantiated.wasInstantiated())));
        registerBeanDefinitions(p);
        lbf.preInstantiateSingletons();
        Assert.assertTrue("singleton was instantiated", DefaultListableBeanFactoryTests.KnowsIfInstantiated.wasInstantiated());
    }

    @Test
    public void testLazyInitialization() {
        DefaultListableBeanFactoryTests.KnowsIfInstantiated.clearInstantiationRecord();
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        Properties p = new Properties();
        p.setProperty("x1.(class)", DefaultListableBeanFactoryTests.KnowsIfInstantiated.class.getName());
        p.setProperty("x1.(lazy-init)", "true");
        Assert.assertTrue("singleton not instantiated", (!(DefaultListableBeanFactoryTests.KnowsIfInstantiated.wasInstantiated())));
        registerBeanDefinitions(p);
        Assert.assertTrue("singleton not instantiated", (!(DefaultListableBeanFactoryTests.KnowsIfInstantiated.wasInstantiated())));
        lbf.preInstantiateSingletons();
        Assert.assertTrue("singleton not instantiated", (!(DefaultListableBeanFactoryTests.KnowsIfInstantiated.wasInstantiated())));
        lbf.getBean("x1");
        Assert.assertTrue("singleton was instantiated", DefaultListableBeanFactoryTests.KnowsIfInstantiated.wasInstantiated());
    }

    @Test
    public void testFactoryBeanDidNotCreatePrototype() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        Properties p = new Properties();
        p.setProperty("x1.(class)", DummyFactory.class.getName());
        // Reset static state
        DummyFactory.reset();
        p.setProperty("x1.singleton", "false");
        Assert.assertTrue("prototype not instantiated", (!(DummyFactory.wasPrototypeCreated())));
        registerBeanDefinitions(p);
        Assert.assertTrue("prototype not instantiated", (!(DummyFactory.wasPrototypeCreated())));
        Assert.assertEquals(TestBean.class, lbf.getType("x1"));
        lbf.preInstantiateSingletons();
        Assert.assertTrue("prototype not instantiated", (!(DummyFactory.wasPrototypeCreated())));
        lbf.getBean("x1");
        Assert.assertEquals(TestBean.class, lbf.getType("x1"));
        Assert.assertTrue(lbf.containsBean("x1"));
        Assert.assertTrue(lbf.containsBean("&x1"));
        Assert.assertTrue("prototype was instantiated", DummyFactory.wasPrototypeCreated());
    }

    @Test
    public void testPrototypeFactoryBeanIgnoredByNonEagerTypeMatching() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        Properties p = new Properties();
        p.setProperty("x1.(class)", DummyFactory.class.getName());
        // Reset static state
        DummyFactory.reset();
        p.setProperty("x1.(singleton)", "false");
        p.setProperty("x1.singleton", "false");
        registerBeanDefinitions(p);
        Assert.assertTrue("prototype not instantiated", (!(DummyFactory.wasPrototypeCreated())));
        String[] beanNames = lbf.getBeanNamesForType(TestBean.class, true, false);
        Assert.assertEquals(0, beanNames.length);
        beanNames = lbf.getBeanNamesForAnnotation(SuppressWarnings.class);
        Assert.assertEquals(0, beanNames.length);
        Assert.assertFalse(lbf.containsSingleton("x1"));
        Assert.assertTrue(lbf.containsBean("x1"));
        Assert.assertTrue(lbf.containsBean("&x1"));
        Assert.assertFalse(lbf.isSingleton("x1"));
        Assert.assertFalse(lbf.isSingleton("&x1"));
        Assert.assertTrue(lbf.isPrototype("x1"));
        Assert.assertTrue(lbf.isPrototype("&x1"));
        Assert.assertTrue(lbf.isTypeMatch("x1", TestBean.class));
        Assert.assertFalse(lbf.isTypeMatch("&x1", TestBean.class));
        Assert.assertTrue(lbf.isTypeMatch("&x1", DummyFactory.class));
        Assert.assertTrue(lbf.isTypeMatch("&x1", ResolvableType.forClass(DummyFactory.class)));
        Assert.assertTrue(lbf.isTypeMatch("&x1", ResolvableType.forClassWithGenerics(FactoryBean.class, Object.class)));
        Assert.assertFalse(lbf.isTypeMatch("&x1", ResolvableType.forClassWithGenerics(FactoryBean.class, String.class)));
        Assert.assertEquals(TestBean.class, lbf.getType("x1"));
        Assert.assertEquals(DummyFactory.class, lbf.getType("&x1"));
        Assert.assertTrue("prototype not instantiated", (!(DummyFactory.wasPrototypeCreated())));
    }

    @Test
    public void testSingletonFactoryBeanIgnoredByNonEagerTypeMatching() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        Properties p = new Properties();
        p.setProperty("x1.(class)", DummyFactory.class.getName());
        // Reset static state
        DummyFactory.reset();
        p.setProperty("x1.(singleton)", "false");
        p.setProperty("x1.singleton", "true");
        registerBeanDefinitions(p);
        Assert.assertTrue("prototype not instantiated", (!(DummyFactory.wasPrototypeCreated())));
        String[] beanNames = lbf.getBeanNamesForType(TestBean.class, true, false);
        Assert.assertEquals(0, beanNames.length);
        beanNames = lbf.getBeanNamesForAnnotation(SuppressWarnings.class);
        Assert.assertEquals(0, beanNames.length);
        Assert.assertFalse(lbf.containsSingleton("x1"));
        Assert.assertTrue(lbf.containsBean("x1"));
        Assert.assertTrue(lbf.containsBean("&x1"));
        Assert.assertFalse(lbf.isSingleton("x1"));
        Assert.assertFalse(lbf.isSingleton("&x1"));
        Assert.assertTrue(lbf.isPrototype("x1"));
        Assert.assertTrue(lbf.isPrototype("&x1"));
        Assert.assertTrue(lbf.isTypeMatch("x1", TestBean.class));
        Assert.assertFalse(lbf.isTypeMatch("&x1", TestBean.class));
        Assert.assertTrue(lbf.isTypeMatch("&x1", DummyFactory.class));
        Assert.assertTrue(lbf.isTypeMatch("&x1", ResolvableType.forClass(DummyFactory.class)));
        Assert.assertTrue(lbf.isTypeMatch("&x1", ResolvableType.forClassWithGenerics(FactoryBean.class, Object.class)));
        Assert.assertFalse(lbf.isTypeMatch("&x1", ResolvableType.forClassWithGenerics(FactoryBean.class, String.class)));
        Assert.assertEquals(TestBean.class, lbf.getType("x1"));
        Assert.assertEquals(DummyFactory.class, lbf.getType("&x1"));
        Assert.assertTrue("prototype not instantiated", (!(DummyFactory.wasPrototypeCreated())));
    }

    @Test
    public void testNonInitializedFactoryBeanIgnoredByNonEagerTypeMatching() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        Properties p = new Properties();
        p.setProperty("x1.(class)", DummyFactory.class.getName());
        // Reset static state
        DummyFactory.reset();
        p.setProperty("x1.singleton", "false");
        registerBeanDefinitions(p);
        Assert.assertTrue("prototype not instantiated", (!(DummyFactory.wasPrototypeCreated())));
        String[] beanNames = lbf.getBeanNamesForType(TestBean.class, true, false);
        Assert.assertEquals(0, beanNames.length);
        beanNames = lbf.getBeanNamesForAnnotation(SuppressWarnings.class);
        Assert.assertEquals(0, beanNames.length);
        Assert.assertFalse(lbf.containsSingleton("x1"));
        Assert.assertTrue(lbf.containsBean("x1"));
        Assert.assertTrue(lbf.containsBean("&x1"));
        Assert.assertFalse(lbf.isSingleton("x1"));
        Assert.assertTrue(lbf.isSingleton("&x1"));
        Assert.assertTrue(lbf.isPrototype("x1"));
        Assert.assertFalse(lbf.isPrototype("&x1"));
        Assert.assertTrue(lbf.isTypeMatch("x1", TestBean.class));
        Assert.assertFalse(lbf.isTypeMatch("&x1", TestBean.class));
        Assert.assertTrue(lbf.isTypeMatch("&x1", DummyFactory.class));
        Assert.assertTrue(lbf.isTypeMatch("&x1", ResolvableType.forClass(DummyFactory.class)));
        Assert.assertTrue(lbf.isTypeMatch("&x1", ResolvableType.forClassWithGenerics(FactoryBean.class, Object.class)));
        Assert.assertFalse(lbf.isTypeMatch("&x1", ResolvableType.forClassWithGenerics(FactoryBean.class, String.class)));
        Assert.assertEquals(TestBean.class, lbf.getType("x1"));
        Assert.assertEquals(DummyFactory.class, lbf.getType("&x1"));
        Assert.assertTrue("prototype not instantiated", (!(DummyFactory.wasPrototypeCreated())));
    }

    @Test
    public void testInitializedFactoryBeanFoundByNonEagerTypeMatching() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        Properties p = new Properties();
        p.setProperty("x1.(class)", DummyFactory.class.getName());
        // Reset static state
        DummyFactory.reset();
        p.setProperty("x1.singleton", "false");
        registerBeanDefinitions(p);
        lbf.preInstantiateSingletons();
        Assert.assertTrue("prototype not instantiated", (!(DummyFactory.wasPrototypeCreated())));
        String[] beanNames = lbf.getBeanNamesForType(TestBean.class, true, false);
        Assert.assertEquals(1, beanNames.length);
        Assert.assertEquals("x1", beanNames[0]);
        Assert.assertTrue(lbf.containsSingleton("x1"));
        Assert.assertTrue(lbf.containsBean("x1"));
        Assert.assertTrue(lbf.containsBean("&x1"));
        Assert.assertTrue(lbf.containsLocalBean("x1"));
        Assert.assertTrue(lbf.containsLocalBean("&x1"));
        Assert.assertFalse(lbf.isSingleton("x1"));
        Assert.assertTrue(lbf.isSingleton("&x1"));
        Assert.assertTrue(lbf.isPrototype("x1"));
        Assert.assertFalse(lbf.isPrototype("&x1"));
        Assert.assertTrue(lbf.isTypeMatch("x1", TestBean.class));
        Assert.assertFalse(lbf.isTypeMatch("&x1", TestBean.class));
        Assert.assertTrue(lbf.isTypeMatch("&x1", DummyFactory.class));
        Assert.assertTrue(lbf.isTypeMatch("x1", Object.class));
        Assert.assertTrue(lbf.isTypeMatch("&x1", Object.class));
        Assert.assertEquals(TestBean.class, lbf.getType("x1"));
        Assert.assertEquals(DummyFactory.class, lbf.getType("&x1"));
        Assert.assertTrue("prototype not instantiated", (!(DummyFactory.wasPrototypeCreated())));
        lbf.registerAlias("x1", "x2");
        Assert.assertTrue(lbf.containsBean("x2"));
        Assert.assertTrue(lbf.containsBean("&x2"));
        Assert.assertTrue(lbf.containsLocalBean("x2"));
        Assert.assertTrue(lbf.containsLocalBean("&x2"));
        Assert.assertFalse(lbf.isSingleton("x2"));
        Assert.assertTrue(lbf.isSingleton("&x2"));
        Assert.assertTrue(lbf.isPrototype("x2"));
        Assert.assertFalse(lbf.isPrototype("&x2"));
        Assert.assertTrue(lbf.isTypeMatch("x2", TestBean.class));
        Assert.assertFalse(lbf.isTypeMatch("&x2", TestBean.class));
        Assert.assertTrue(lbf.isTypeMatch("&x2", DummyFactory.class));
        Assert.assertTrue(lbf.isTypeMatch("x2", Object.class));
        Assert.assertTrue(lbf.isTypeMatch("&x2", Object.class));
        Assert.assertEquals(TestBean.class, lbf.getType("x2"));
        Assert.assertEquals(DummyFactory.class, lbf.getType("&x2"));
        Assert.assertEquals(1, lbf.getAliases("x1").length);
        Assert.assertEquals("x2", lbf.getAliases("x1")[0]);
        Assert.assertEquals(1, lbf.getAliases("&x1").length);
        Assert.assertEquals("&x2", lbf.getAliases("&x1")[0]);
        Assert.assertEquals(1, lbf.getAliases("x2").length);
        Assert.assertEquals("x1", lbf.getAliases("x2")[0]);
        Assert.assertEquals(1, lbf.getAliases("&x2").length);
        Assert.assertEquals("&x1", lbf.getAliases("&x2")[0]);
    }

    @Test
    public void testStaticFactoryMethodFoundByNonEagerTypeMatching() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition rbd = new RootBeanDefinition(DefaultListableBeanFactoryTests.TestBeanFactory.class);
        rbd.setFactoryMethodName("createTestBean");
        lbf.registerBeanDefinition("x1", rbd);
        DefaultListableBeanFactoryTests.TestBeanFactory.initialized = false;
        String[] beanNames = lbf.getBeanNamesForType(TestBean.class, true, false);
        Assert.assertEquals(1, beanNames.length);
        Assert.assertEquals("x1", beanNames[0]);
        Assert.assertFalse(lbf.containsSingleton("x1"));
        Assert.assertTrue(lbf.containsBean("x1"));
        Assert.assertFalse(lbf.containsBean("&x1"));
        Assert.assertTrue(lbf.isSingleton("x1"));
        Assert.assertFalse(lbf.isSingleton("&x1"));
        Assert.assertFalse(lbf.isPrototype("x1"));
        Assert.assertFalse(lbf.isPrototype("&x1"));
        Assert.assertTrue(lbf.isTypeMatch("x1", TestBean.class));
        Assert.assertFalse(lbf.isTypeMatch("&x1", TestBean.class));
        Assert.assertEquals(TestBean.class, lbf.getType("x1"));
        Assert.assertEquals(null, lbf.getType("&x1"));
        Assert.assertFalse(DefaultListableBeanFactoryTests.TestBeanFactory.initialized);
    }

    @Test
    public void testStaticPrototypeFactoryMethodFoundByNonEagerTypeMatching() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition rbd = new RootBeanDefinition(DefaultListableBeanFactoryTests.TestBeanFactory.class);
        rbd.setScope(SCOPE_PROTOTYPE);
        rbd.setFactoryMethodName("createTestBean");
        lbf.registerBeanDefinition("x1", rbd);
        DefaultListableBeanFactoryTests.TestBeanFactory.initialized = false;
        String[] beanNames = lbf.getBeanNamesForType(TestBean.class, true, false);
        Assert.assertEquals(1, beanNames.length);
        Assert.assertEquals("x1", beanNames[0]);
        Assert.assertFalse(lbf.containsSingleton("x1"));
        Assert.assertTrue(lbf.containsBean("x1"));
        Assert.assertFalse(lbf.containsBean("&x1"));
        Assert.assertFalse(lbf.isSingleton("x1"));
        Assert.assertFalse(lbf.isSingleton("&x1"));
        Assert.assertTrue(lbf.isPrototype("x1"));
        Assert.assertFalse(lbf.isPrototype("&x1"));
        Assert.assertTrue(lbf.isTypeMatch("x1", TestBean.class));
        Assert.assertFalse(lbf.isTypeMatch("&x1", TestBean.class));
        Assert.assertEquals(TestBean.class, lbf.getType("x1"));
        Assert.assertEquals(null, lbf.getType("&x1"));
        Assert.assertFalse(DefaultListableBeanFactoryTests.TestBeanFactory.initialized);
    }

    @Test
    public void testNonStaticFactoryMethodFoundByNonEagerTypeMatching() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition factoryBd = new RootBeanDefinition(DefaultListableBeanFactoryTests.TestBeanFactory.class);
        lbf.registerBeanDefinition("factory", factoryBd);
        RootBeanDefinition rbd = new RootBeanDefinition(DefaultListableBeanFactoryTests.TestBeanFactory.class);
        rbd.setFactoryBeanName("factory");
        rbd.setFactoryMethodName("createTestBeanNonStatic");
        lbf.registerBeanDefinition("x1", rbd);
        DefaultListableBeanFactoryTests.TestBeanFactory.initialized = false;
        String[] beanNames = lbf.getBeanNamesForType(TestBean.class, true, false);
        Assert.assertEquals(1, beanNames.length);
        Assert.assertEquals("x1", beanNames[0]);
        Assert.assertFalse(lbf.containsSingleton("x1"));
        Assert.assertTrue(lbf.containsBean("x1"));
        Assert.assertFalse(lbf.containsBean("&x1"));
        Assert.assertTrue(lbf.isSingleton("x1"));
        Assert.assertFalse(lbf.isSingleton("&x1"));
        Assert.assertFalse(lbf.isPrototype("x1"));
        Assert.assertFalse(lbf.isPrototype("&x1"));
        Assert.assertTrue(lbf.isTypeMatch("x1", TestBean.class));
        Assert.assertFalse(lbf.isTypeMatch("&x1", TestBean.class));
        Assert.assertEquals(TestBean.class, lbf.getType("x1"));
        Assert.assertEquals(null, lbf.getType("&x1"));
        Assert.assertFalse(DefaultListableBeanFactoryTests.TestBeanFactory.initialized);
    }

    @Test
    public void testNonStaticPrototypeFactoryMethodFoundByNonEagerTypeMatching() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition factoryBd = new RootBeanDefinition(DefaultListableBeanFactoryTests.TestBeanFactory.class);
        lbf.registerBeanDefinition("factory", factoryBd);
        RootBeanDefinition rbd = new RootBeanDefinition();
        rbd.setFactoryBeanName("factory");
        rbd.setFactoryMethodName("createTestBeanNonStatic");
        rbd.setScope(SCOPE_PROTOTYPE);
        lbf.registerBeanDefinition("x1", rbd);
        DefaultListableBeanFactoryTests.TestBeanFactory.initialized = false;
        String[] beanNames = lbf.getBeanNamesForType(TestBean.class, true, false);
        Assert.assertEquals(1, beanNames.length);
        Assert.assertEquals("x1", beanNames[0]);
        Assert.assertFalse(lbf.containsSingleton("x1"));
        Assert.assertTrue(lbf.containsBean("x1"));
        Assert.assertFalse(lbf.containsBean("&x1"));
        Assert.assertTrue(lbf.containsLocalBean("x1"));
        Assert.assertFalse(lbf.containsLocalBean("&x1"));
        Assert.assertFalse(lbf.isSingleton("x1"));
        Assert.assertFalse(lbf.isSingleton("&x1"));
        Assert.assertTrue(lbf.isPrototype("x1"));
        Assert.assertFalse(lbf.isPrototype("&x1"));
        Assert.assertTrue(lbf.isTypeMatch("x1", TestBean.class));
        Assert.assertFalse(lbf.isTypeMatch("&x1", TestBean.class));
        Assert.assertTrue(lbf.isTypeMatch("x1", Object.class));
        Assert.assertFalse(lbf.isTypeMatch("&x1", Object.class));
        Assert.assertEquals(TestBean.class, lbf.getType("x1"));
        Assert.assertEquals(null, lbf.getType("&x1"));
        Assert.assertFalse(DefaultListableBeanFactoryTests.TestBeanFactory.initialized);
        lbf.registerAlias("x1", "x2");
        Assert.assertTrue(lbf.containsBean("x2"));
        Assert.assertFalse(lbf.containsBean("&x2"));
        Assert.assertTrue(lbf.containsLocalBean("x2"));
        Assert.assertFalse(lbf.containsLocalBean("&x2"));
        Assert.assertFalse(lbf.isSingleton("x2"));
        Assert.assertFalse(lbf.isSingleton("&x2"));
        Assert.assertTrue(lbf.isPrototype("x2"));
        Assert.assertFalse(lbf.isPrototype("&x2"));
        Assert.assertTrue(lbf.isTypeMatch("x2", TestBean.class));
        Assert.assertFalse(lbf.isTypeMatch("&x2", TestBean.class));
        Assert.assertTrue(lbf.isTypeMatch("x2", Object.class));
        Assert.assertFalse(lbf.isTypeMatch("&x2", Object.class));
        Assert.assertEquals(TestBean.class, lbf.getType("x2"));
        Assert.assertEquals(null, lbf.getType("&x2"));
        Assert.assertEquals(1, lbf.getAliases("x1").length);
        Assert.assertEquals("x2", lbf.getAliases("x1")[0]);
        Assert.assertEquals(1, lbf.getAliases("&x1").length);
        Assert.assertEquals("&x2", lbf.getAliases("&x1")[0]);
        Assert.assertEquals(1, lbf.getAliases("x2").length);
        Assert.assertEquals("x1", lbf.getAliases("x2")[0]);
        Assert.assertEquals(1, lbf.getAliases("&x2").length);
        Assert.assertEquals("&x1", lbf.getAliases("&x2")[0]);
    }

    @Test
    public void testEmpty() {
        ListableBeanFactory lbf = new DefaultListableBeanFactory();
        Assert.assertTrue("No beans defined --> array != null", ((lbf.getBeanDefinitionNames()) != null));
        Assert.assertTrue("No beans defined after no arg constructor", ((lbf.getBeanDefinitionNames().length) == 0));
        Assert.assertTrue("No beans defined after no arg constructor", ((lbf.getBeanDefinitionCount()) == 0));
    }

    @Test
    public void testEmptyPropertiesPopulation() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        Properties p = new Properties();
        registerBeanDefinitions(p);
        Assert.assertTrue("No beans defined after ignorable invalid", ((lbf.getBeanDefinitionCount()) == 0));
    }

    @Test
    public void testHarmlessIgnorableRubbish() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        Properties p = new Properties();
        p.setProperty("foo", "bar");
        p.setProperty("qwert", "er");
        new org.springframework.beans.factory.support.PropertiesBeanDefinitionReader(lbf).registerBeanDefinitions(p, "test");
        Assert.assertTrue("No beans defined after harmless ignorable rubbish", ((lbf.getBeanDefinitionCount()) == 0));
    }

    @Test
    public void testPropertiesPopulationWithNullPrefix() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        Properties p = new Properties();
        p.setProperty("test.(class)", TestBean.class.getName());
        p.setProperty("test.name", "Tony");
        p.setProperty("test.age", "48");
        int count = new org.springframework.beans.factory.support.PropertiesBeanDefinitionReader(lbf).registerBeanDefinitions(p);
        Assert.assertTrue(("1 beans registered, not " + count), (count == 1));
        testSingleTestBean(lbf);
    }

    @Test
    public void testPropertiesPopulationWithPrefix() {
        String PREFIX = "beans.";
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        Properties p = new Properties();
        p.setProperty((PREFIX + "test.(class)"), TestBean.class.getName());
        p.setProperty((PREFIX + "test.name"), "Tony");
        p.setProperty((PREFIX + "test.age"), "0x30");
        int count = new org.springframework.beans.factory.support.PropertiesBeanDefinitionReader(lbf).registerBeanDefinitions(p, PREFIX);
        Assert.assertTrue(("1 beans registered, not " + count), (count == 1));
        testSingleTestBean(lbf);
    }

    @Test
    public void testSimpleReference() {
        String PREFIX = "beans.";
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        Properties p = new Properties();
        p.setProperty((PREFIX + "rod.(class)"), TestBean.class.getName());
        p.setProperty((PREFIX + "rod.name"), "Rod");
        p.setProperty((PREFIX + "kerry.(class)"), TestBean.class.getName());
        p.setProperty((PREFIX + "kerry.name"), "Kerry");
        p.setProperty((PREFIX + "kerry.age"), "35");
        p.setProperty((PREFIX + "kerry.spouse(ref)"), "rod");
        int count = new org.springframework.beans.factory.support.PropertiesBeanDefinitionReader(lbf).registerBeanDefinitions(p, PREFIX);
        Assert.assertTrue(("2 beans registered, not " + count), (count == 2));
        TestBean kerry = lbf.getBean("kerry", TestBean.class);
        Assert.assertTrue("Kerry name is Kerry", "Kerry".equals(kerry.getName()));
        ITestBean spouse = kerry.getSpouse();
        Assert.assertTrue("Kerry spouse is non null", (spouse != null));
        Assert.assertTrue("Kerry spouse name is Rod", "Rod".equals(spouse.getName()));
    }

    @Test
    public void testPropertiesWithDotsInKey() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        Properties p = new Properties();
        p.setProperty("tb.(class)", TestBean.class.getName());
        p.setProperty("tb.someMap[my.key]", "my.value");
        int count = new org.springframework.beans.factory.support.PropertiesBeanDefinitionReader(lbf).registerBeanDefinitions(p);
        Assert.assertTrue(("1 beans registered, not " + count), (count == 1));
        Assert.assertEquals(1, lbf.getBeanDefinitionCount());
        TestBean tb = lbf.getBean("tb", TestBean.class);
        Assert.assertEquals("my.value", tb.getSomeMap().get("my.key"));
    }

    @Test
    public void testUnresolvedReference() {
        String PREFIX = "beans.";
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        Properties p = new Properties();
        try {
            p.setProperty((PREFIX + "kerry.(class)"), TestBean.class.getName());
            p.setProperty((PREFIX + "kerry.name"), "Kerry");
            p.setProperty((PREFIX + "kerry.age"), "35");
            p.setProperty((PREFIX + "kerry.spouse(ref)"), "rod");
            new org.springframework.beans.factory.support.PropertiesBeanDefinitionReader(lbf).registerBeanDefinitions(p, PREFIX);
            lbf.getBean("kerry");
            Assert.fail("Unresolved reference should have been detected");
        } catch (BeansException ex) {
            // cool
        }
    }

    @Test
    public void testSelfReference() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        MutablePropertyValues pvs = new MutablePropertyValues();
        pvs.add("spouse", new RuntimeBeanReference("self"));
        RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
        bd.setPropertyValues(pvs);
        lbf.registerBeanDefinition("self", bd);
        TestBean self = ((TestBean) (lbf.getBean("self")));
        Assert.assertEquals(self, self.getSpouse());
    }

    @Test
    public void testPossibleMatches() {
        try {
            DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
            MutablePropertyValues pvs = new MutablePropertyValues();
            pvs.add("ag", "foobar");
            RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
            bd.setPropertyValues(pvs);
            lbf.registerBeanDefinition("tb", bd);
            lbf.getBean("tb");
            Assert.fail("Should throw exception on invalid property");
        } catch (BeanCreationException ex) {
            Assert.assertTrue(((ex.getCause()) instanceof NotWritablePropertyException));
            NotWritablePropertyException cause = ((NotWritablePropertyException) (ex.getCause()));
            // expected
            Assert.assertEquals(1, cause.getPossibleMatches().length);
            Assert.assertEquals("age", cause.getPossibleMatches()[0]);
        }
    }

    @Test
    public void testPrototype() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        Properties p = new Properties();
        p.setProperty("kerry.(class)", TestBean.class.getName());
        p.setProperty("kerry.age", "35");
        registerBeanDefinitions(p);
        TestBean kerry1 = ((TestBean) (lbf.getBean("kerry")));
        TestBean kerry2 = ((TestBean) (lbf.getBean("kerry")));
        Assert.assertTrue("Non null", (kerry1 != null));
        Assert.assertTrue("Singletons equal", (kerry1 == kerry2));
        lbf = new DefaultListableBeanFactory();
        p = new Properties();
        p.setProperty("kerry.(class)", TestBean.class.getName());
        p.setProperty("kerry.(scope)", "prototype");
        p.setProperty("kerry.age", "35");
        registerBeanDefinitions(p);
        kerry1 = ((TestBean) (lbf.getBean("kerry")));
        kerry2 = ((TestBean) (lbf.getBean("kerry")));
        Assert.assertTrue("Non null", (kerry1 != null));
        Assert.assertTrue("Prototypes NOT equal", (kerry1 != kerry2));
        lbf = new DefaultListableBeanFactory();
        p = new Properties();
        p.setProperty("kerry.(class)", TestBean.class.getName());
        p.setProperty("kerry.(scope)", "singleton");
        p.setProperty("kerry.age", "35");
        registerBeanDefinitions(p);
        kerry1 = ((TestBean) (lbf.getBean("kerry")));
        kerry2 = ((TestBean) (lbf.getBean("kerry")));
        Assert.assertTrue("Non null", (kerry1 != null));
        Assert.assertTrue("Specified singletons equal", (kerry1 == kerry2));
    }

    @Test
    public void testPrototypeCircleLeadsToException() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        Properties p = new Properties();
        p.setProperty("kerry.(class)", TestBean.class.getName());
        p.setProperty("kerry.(singleton)", "false");
        p.setProperty("kerry.age", "35");
        p.setProperty("kerry.spouse", "*rod");
        p.setProperty("rod.(class)", TestBean.class.getName());
        p.setProperty("rod.(singleton)", "false");
        p.setProperty("rod.age", "34");
        p.setProperty("rod.spouse", "*kerry");
        registerBeanDefinitions(p);
        try {
            lbf.getBean("kerry");
            Assert.fail("Should have thrown BeanCreationException");
        } catch (BeanCreationException ex) {
            // expected
            Assert.assertTrue(ex.contains(BeanCurrentlyInCreationException.class));
        }
    }

    @Test
    public void testPrototypeExtendsPrototype() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        Properties p = new Properties();
        p.setProperty("wife.(class)", TestBean.class.getName());
        p.setProperty("wife.name", "kerry");
        p.setProperty("kerry.(parent)", "wife");
        p.setProperty("kerry.age", "35");
        registerBeanDefinitions(p);
        TestBean kerry1 = ((TestBean) (lbf.getBean("kerry")));
        TestBean kerry2 = ((TestBean) (lbf.getBean("kerry")));
        Assert.assertEquals("kerry", kerry1.getName());
        Assert.assertNotNull("Non null", kerry1);
        Assert.assertTrue("Singletons equal", (kerry1 == kerry2));
        lbf = new DefaultListableBeanFactory();
        p = new Properties();
        p.setProperty("wife.(class)", TestBean.class.getName());
        p.setProperty("wife.name", "kerry");
        p.setProperty("wife.(singleton)", "false");
        p.setProperty("kerry.(parent)", "wife");
        p.setProperty("kerry.(singleton)", "false");
        p.setProperty("kerry.age", "35");
        registerBeanDefinitions(p);
        Assert.assertFalse(lbf.isSingleton("kerry"));
        kerry1 = ((TestBean) (lbf.getBean("kerry")));
        kerry2 = ((TestBean) (lbf.getBean("kerry")));
        Assert.assertTrue("Non null", (kerry1 != null));
        Assert.assertTrue("Prototypes NOT equal", (kerry1 != kerry2));
        lbf = new DefaultListableBeanFactory();
        p = new Properties();
        p.setProperty("kerry.(class)", TestBean.class.getName());
        p.setProperty("kerry.(singleton)", "true");
        p.setProperty("kerry.age", "35");
        registerBeanDefinitions(p);
        kerry1 = ((TestBean) (lbf.getBean("kerry")));
        kerry2 = ((TestBean) (lbf.getBean("kerry")));
        Assert.assertTrue("Non null", (kerry1 != null));
        Assert.assertTrue("Specified singletons equal", (kerry1 == kerry2));
    }

    @Test
    public void testCanReferenceParentBeanFromChildViaAlias() {
        final String EXPECTED_NAME = "Juergen";
        final int EXPECTED_AGE = 41;
        RootBeanDefinition parentDefinition = new RootBeanDefinition(TestBean.class);
        parentDefinition.setAbstract(true);
        parentDefinition.getPropertyValues().add("name", EXPECTED_NAME);
        parentDefinition.getPropertyValues().add("age", EXPECTED_AGE);
        ChildBeanDefinition childDefinition = new ChildBeanDefinition("alias");
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
        factory.registerBeanDefinition("parent", parentDefinition);
        factory.registerBeanDefinition("child", childDefinition);
        factory.registerAlias("parent", "alias");
        TestBean child = ((TestBean) (factory.getBean("child")));
        Assert.assertEquals(EXPECTED_NAME, child.getName());
        Assert.assertEquals(EXPECTED_AGE, child.getAge());
        Assert.assertEquals("Use cached merged bean definition", factory.getMergedBeanDefinition("child"), factory.getMergedBeanDefinition("child"));
    }

    @Test
    public void testGetTypeWorksAfterParentChildMerging() {
        RootBeanDefinition parentDefinition = new RootBeanDefinition(TestBean.class);
        ChildBeanDefinition childDefinition = new ChildBeanDefinition("parent", DerivedTestBean.class, null, null);
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
        factory.registerBeanDefinition("parent", parentDefinition);
        factory.registerBeanDefinition("child", childDefinition);
        factory.freezeConfiguration();
        Assert.assertEquals(TestBean.class, factory.getType("parent"));
        Assert.assertEquals(DerivedTestBean.class, factory.getType("child"));
    }

    @Test
    public void testNameAlreadyBound() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        Properties p = new Properties();
        p.setProperty("kerry.(class)", TestBean.class.getName());
        p.setProperty("kerry.age", "35");
        registerBeanDefinitions(p);
        try {
            registerBeanDefinitions(p);
        } catch (BeanDefinitionStoreException ex) {
            Assert.assertEquals("kerry", ex.getBeanName());
            // expected
        }
    }

    @Test
    public void testAliasCircle() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        lbf.registerAlias("test", "test2");
        lbf.registerAlias("test2", "test3");
        try {
            lbf.registerAlias("test3", "test2");
            Assert.fail("Should have thrown IllegalStateException");
        } catch (IllegalStateException ex) {
            // expected
        }
        try {
            lbf.registerAlias("test3", "test");
            Assert.fail("Should have thrown IllegalStateException");
        } catch (IllegalStateException ex) {
            // expected
        }
        lbf.registerAlias("test", "test3");
    }

    @Test
    public void testBeanDefinitionOverriding() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        lbf.registerBeanDefinition("test", new RootBeanDefinition(TestBean.class));
        lbf.registerBeanDefinition("test", new RootBeanDefinition(NestedTestBean.class));
        lbf.registerAlias("otherTest", "test2");
        lbf.registerAlias("test", "test2");
        Assert.assertTrue(((lbf.getBean("test")) instanceof NestedTestBean));
        Assert.assertTrue(((lbf.getBean("test2")) instanceof NestedTestBean));
    }

    @Test
    public void testBeanDefinitionRemoval() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        lbf.setAllowBeanDefinitionOverriding(false);
        lbf.registerBeanDefinition("test", new RootBeanDefinition(TestBean.class));
        lbf.registerAlias("test", "test2");
        lbf.preInstantiateSingletons();
        lbf.removeBeanDefinition("test");
        lbf.removeAlias("test2");
        lbf.registerBeanDefinition("test", new RootBeanDefinition(NestedTestBean.class));
        lbf.registerAlias("test", "test2");
        Assert.assertTrue(((lbf.getBean("test")) instanceof NestedTestBean));
        Assert.assertTrue(((lbf.getBean("test2")) instanceof NestedTestBean));
    }

    @Test
    public void testBeanDefinitionOverridingNotAllowed() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        lbf.setAllowBeanDefinitionOverriding(false);
        BeanDefinition oldDef = new RootBeanDefinition(TestBean.class);
        BeanDefinition newDef = new RootBeanDefinition(NestedTestBean.class);
        lbf.registerBeanDefinition("test", oldDef);
        try {
            lbf.registerBeanDefinition("test", newDef);
            Assert.fail("Should have thrown BeanDefinitionOverrideException");
        } catch (BeanDefinitionOverrideException ex) {
            Assert.assertEquals("test", ex.getBeanName());
            Assert.assertSame(newDef, ex.getBeanDefinition());
            Assert.assertSame(oldDef, ex.getExistingDefinition());
        }
    }

    @Test
    public void testBeanDefinitionOverridingWithAlias() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        lbf.registerBeanDefinition("test", new RootBeanDefinition(TestBean.class));
        lbf.registerAlias("test", "testAlias");
        lbf.registerBeanDefinition("test", new RootBeanDefinition(NestedTestBean.class));
        lbf.registerAlias("test", "testAlias");
        Assert.assertTrue(((lbf.getBean("test")) instanceof NestedTestBean));
        Assert.assertTrue(((lbf.getBean("testAlias")) instanceof NestedTestBean));
    }

    @Test
    public void testAliasChaining() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        lbf.registerBeanDefinition("test", new RootBeanDefinition(NestedTestBean.class));
        lbf.registerAlias("test", "testAlias");
        lbf.registerAlias("testAlias", "testAlias2");
        lbf.registerAlias("testAlias2", "testAlias3");
        Object bean = lbf.getBean("test");
        Assert.assertSame(bean, lbf.getBean("testAlias"));
        Assert.assertSame(bean, lbf.getBean("testAlias2"));
        Assert.assertSame(bean, lbf.getBean("testAlias3"));
    }

    @Test
    public void testBeanReferenceWithNewSyntax() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        Properties p = new Properties();
        p.setProperty("r.(class)", TestBean.class.getName());
        p.setProperty("r.name", "rod");
        p.setProperty("k.(class)", TestBean.class.getName());
        p.setProperty("k.name", "kerry");
        p.setProperty("k.spouse", "*r");
        registerBeanDefinitions(p);
        TestBean k = ((TestBean) (lbf.getBean("k")));
        TestBean r = ((TestBean) (lbf.getBean("r")));
        Assert.assertTrue(((k.getSpouse()) == r));
    }

    @Test
    public void testCanEscapeBeanReferenceSyntax() {
        String name = "*name";
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        Properties p = new Properties();
        p.setProperty("r.(class)", TestBean.class.getName());
        p.setProperty("r.name", ("*" + name));
        registerBeanDefinitions(p);
        TestBean r = ((TestBean) (lbf.getBean("r")));
        Assert.assertTrue(r.getName().equals(name));
    }

    @Test
    public void testCustomEditor() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        lbf.addPropertyEditorRegistrar(new PropertyEditorRegistrar() {
            @Override
            public void registerCustomEditors(PropertyEditorRegistry registry) {
                NumberFormat nf = NumberFormat.getInstance(Locale.GERMAN);
                registry.registerCustomEditor(Float.class, new CustomNumberEditor(Float.class, nf, true));
            }
        });
        MutablePropertyValues pvs = new MutablePropertyValues();
        pvs.add("myFloat", "1,1");
        RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
        bd.setPropertyValues(pvs);
        lbf.registerBeanDefinition("testBean", bd);
        TestBean testBean = ((TestBean) (lbf.getBean("testBean")));
        Assert.assertTrue(((testBean.getMyFloat().floatValue()) == 1.1F));
    }

    @Test
    public void testCustomConverter() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        GenericConversionService conversionService = new DefaultConversionService();
        conversionService.addConverter(new org.springframework.core.convert.converter.Converter<String, Float>() {
            @Override
            public Float convert(String source) {
                try {
                    NumberFormat nf = NumberFormat.getInstance(Locale.GERMAN);
                    return nf.parse(source).floatValue();
                } catch (ParseException ex) {
                    throw new IllegalArgumentException(ex);
                }
            }
        });
        lbf.setConversionService(conversionService);
        MutablePropertyValues pvs = new MutablePropertyValues();
        pvs.add("myFloat", "1,1");
        RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
        bd.setPropertyValues(pvs);
        lbf.registerBeanDefinition("testBean", bd);
        TestBean testBean = ((TestBean) (lbf.getBean("testBean")));
        Assert.assertTrue(((testBean.getMyFloat().floatValue()) == 1.1F));
    }

    @Test
    public void testCustomEditorWithBeanReference() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        lbf.addPropertyEditorRegistrar(new PropertyEditorRegistrar() {
            @Override
            public void registerCustomEditors(PropertyEditorRegistry registry) {
                NumberFormat nf = NumberFormat.getInstance(Locale.GERMAN);
                registry.registerCustomEditor(Float.class, new CustomNumberEditor(Float.class, nf, true));
            }
        });
        MutablePropertyValues pvs = new MutablePropertyValues();
        pvs.add("myFloat", new RuntimeBeanReference("myFloat"));
        RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
        bd.setPropertyValues(pvs);
        lbf.registerBeanDefinition("testBean", bd);
        lbf.registerSingleton("myFloat", "1,1");
        TestBean testBean = ((TestBean) (lbf.getBean("testBean")));
        Assert.assertTrue(((testBean.getMyFloat().floatValue()) == 1.1F));
    }

    @Test
    public void testCustomTypeConverter() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        NumberFormat nf = NumberFormat.getInstance(Locale.GERMAN);
        lbf.setTypeConverter(new DefaultListableBeanFactoryTests.CustomTypeConverter(nf));
        MutablePropertyValues pvs = new MutablePropertyValues();
        pvs.add("myFloat", "1,1");
        ConstructorArgumentValues cav = new ConstructorArgumentValues();
        cav.addIndexedArgumentValue(0, "myName");
        cav.addIndexedArgumentValue(1, "myAge");
        lbf.registerBeanDefinition("testBean", new RootBeanDefinition(TestBean.class, cav, pvs));
        TestBean testBean = ((TestBean) (lbf.getBean("testBean")));
        Assert.assertEquals("myName", testBean.getName());
        Assert.assertEquals(5, testBean.getAge());
        Assert.assertTrue(((testBean.getMyFloat().floatValue()) == 1.1F));
    }

    @Test
    public void testCustomTypeConverterWithBeanReference() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        NumberFormat nf = NumberFormat.getInstance(Locale.GERMAN);
        lbf.setTypeConverter(new DefaultListableBeanFactoryTests.CustomTypeConverter(nf));
        MutablePropertyValues pvs = new MutablePropertyValues();
        pvs.add("myFloat", new RuntimeBeanReference("myFloat"));
        ConstructorArgumentValues cav = new ConstructorArgumentValues();
        cav.addIndexedArgumentValue(0, "myName");
        cav.addIndexedArgumentValue(1, "myAge");
        lbf.registerBeanDefinition("testBean", new RootBeanDefinition(TestBean.class, cav, pvs));
        lbf.registerSingleton("myFloat", "1,1");
        TestBean testBean = ((TestBean) (lbf.getBean("testBean")));
        Assert.assertEquals("myName", testBean.getName());
        Assert.assertEquals(5, testBean.getAge());
        Assert.assertTrue(((testBean.getMyFloat().floatValue()) == 1.1F));
    }

    @Test
    public void testRegisterExistingSingletonWithReference() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        Properties p = new Properties();
        p.setProperty("test.(class)", TestBean.class.getName());
        p.setProperty("test.name", "Tony");
        p.setProperty("test.age", "48");
        p.setProperty("test.spouse(ref)", "singletonObject");
        registerBeanDefinitions(p);
        Object singletonObject = new TestBean();
        lbf.registerSingleton("singletonObject", singletonObject);
        Assert.assertTrue(lbf.isSingleton("singletonObject"));
        Assert.assertEquals(TestBean.class, lbf.getType("singletonObject"));
        TestBean test = ((TestBean) (lbf.getBean("test")));
        Assert.assertEquals(singletonObject, lbf.getBean("singletonObject"));
        Assert.assertEquals(singletonObject, test.getSpouse());
        Map<?, ?> beansOfType = lbf.getBeansOfType(TestBean.class, false, true);
        Assert.assertEquals(2, beansOfType.size());
        Assert.assertTrue(beansOfType.containsValue(test));
        Assert.assertTrue(beansOfType.containsValue(singletonObject));
        beansOfType = lbf.getBeansOfType(null, false, true);
        Assert.assertEquals(2, beansOfType.size());
        Iterator<String> beanNames = lbf.getBeanNamesIterator();
        Assert.assertEquals("test", beanNames.next());
        Assert.assertEquals("singletonObject", beanNames.next());
        Assert.assertFalse(beanNames.hasNext());
        Assert.assertTrue(lbf.containsSingleton("test"));
        Assert.assertTrue(lbf.containsSingleton("singletonObject"));
        Assert.assertTrue(lbf.containsBeanDefinition("test"));
        Assert.assertFalse(lbf.containsBeanDefinition("singletonObject"));
    }

    @Test
    public void testRegisterExistingSingletonWithNameOverriding() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        Properties p = new Properties();
        p.setProperty("test.(class)", TestBean.class.getName());
        p.setProperty("test.name", "Tony");
        p.setProperty("test.age", "48");
        p.setProperty("test.spouse(ref)", "singletonObject");
        registerBeanDefinitions(p);
        lbf.registerBeanDefinition("singletonObject", new RootBeanDefinition(PropertiesFactoryBean.class));
        Object singletonObject = new TestBean();
        lbf.registerSingleton("singletonObject", singletonObject);
        lbf.preInstantiateSingletons();
        Assert.assertTrue(lbf.isSingleton("singletonObject"));
        Assert.assertEquals(TestBean.class, lbf.getType("singletonObject"));
        TestBean test = ((TestBean) (lbf.getBean("test")));
        Assert.assertEquals(singletonObject, lbf.getBean("singletonObject"));
        Assert.assertEquals(singletonObject, test.getSpouse());
        Map<?, ?> beansOfType = lbf.getBeansOfType(TestBean.class, false, true);
        Assert.assertEquals(2, beansOfType.size());
        Assert.assertTrue(beansOfType.containsValue(test));
        Assert.assertTrue(beansOfType.containsValue(singletonObject));
        beansOfType = lbf.getBeansOfType(null, false, true);
        Iterator<String> beanNames = lbf.getBeanNamesIterator();
        Assert.assertEquals("test", beanNames.next());
        Assert.assertEquals("singletonObject", beanNames.next());
        Assert.assertFalse(beanNames.hasNext());
        Assert.assertEquals(2, beansOfType.size());
        Assert.assertTrue(lbf.containsSingleton("test"));
        Assert.assertTrue(lbf.containsSingleton("singletonObject"));
        Assert.assertTrue(lbf.containsBeanDefinition("test"));
        Assert.assertTrue(lbf.containsBeanDefinition("singletonObject"));
    }

    @Test
    public void testRegisterExistingSingletonWithAutowire() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        MutablePropertyValues pvs = new MutablePropertyValues();
        pvs.add("name", "Tony");
        pvs.add("age", "48");
        RootBeanDefinition bd = new RootBeanDefinition(DependenciesBean.class);
        bd.setPropertyValues(pvs);
        bd.setDependencyCheck(DEPENDENCY_CHECK_OBJECTS);
        bd.setAutowireMode(AUTOWIRE_BY_TYPE);
        lbf.registerBeanDefinition("test", bd);
        Object singletonObject = new TestBean();
        lbf.registerSingleton("singletonObject", singletonObject);
        Assert.assertTrue(lbf.containsBean("singletonObject"));
        Assert.assertTrue(lbf.isSingleton("singletonObject"));
        Assert.assertEquals(TestBean.class, lbf.getType("singletonObject"));
        Assert.assertEquals(0, lbf.getAliases("singletonObject").length);
        DependenciesBean test = ((DependenciesBean) (lbf.getBean("test")));
        Assert.assertEquals(singletonObject, lbf.getBean("singletonObject"));
        Assert.assertEquals(singletonObject, test.getSpouse());
    }

    @Test
    public void testRegisterExistingSingletonWithAlreadyBound() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        Object singletonObject = new TestBean();
        lbf.registerSingleton("singletonObject", singletonObject);
        try {
            lbf.registerSingleton("singletonObject", singletonObject);
            Assert.fail("Should have thrown IllegalStateException");
        } catch (IllegalStateException ex) {
            // expected
        }
    }

    @Test
    public void testReregisterBeanDefinition() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition bd1 = new RootBeanDefinition(TestBean.class);
        bd1.setScope(SCOPE_PROTOTYPE);
        lbf.registerBeanDefinition("testBean", bd1);
        Assert.assertTrue(((lbf.getBean("testBean")) instanceof TestBean));
        RootBeanDefinition bd2 = new RootBeanDefinition(NestedTestBean.class);
        bd2.setScope(SCOPE_PROTOTYPE);
        lbf.registerBeanDefinition("testBean", bd2);
        Assert.assertTrue(((lbf.getBean("testBean")) instanceof NestedTestBean));
    }

    @Test
    public void testArrayPropertyWithAutowiring() throws MalformedURLException {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        bf.registerSingleton("resource1", new UrlResource("http://localhost:8080"));
        bf.registerSingleton("resource2", new UrlResource("http://localhost:9090"));
        RootBeanDefinition rbd = new RootBeanDefinition(DefaultListableBeanFactoryTests.ArrayBean.class);
        rbd.setAutowireMode(AUTOWIRE_BY_TYPE);
        bf.registerBeanDefinition("arrayBean", rbd);
        DefaultListableBeanFactoryTests.ArrayBean ab = ((DefaultListableBeanFactoryTests.ArrayBean) (bf.getBean("arrayBean")));
        Assert.assertEquals(new UrlResource("http://localhost:8080"), ab.getResourceArray()[0]);
        Assert.assertEquals(new UrlResource("http://localhost:9090"), ab.getResourceArray()[1]);
    }

    @Test
    public void testArrayPropertyWithOptionalAutowiring() throws MalformedURLException {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        RootBeanDefinition rbd = new RootBeanDefinition(DefaultListableBeanFactoryTests.ArrayBean.class);
        rbd.setAutowireMode(AUTOWIRE_BY_TYPE);
        bf.registerBeanDefinition("arrayBean", rbd);
        DefaultListableBeanFactoryTests.ArrayBean ab = ((DefaultListableBeanFactoryTests.ArrayBean) (bf.getBean("arrayBean")));
        Assert.assertNull(ab.getResourceArray());
    }

    @Test
    public void testArrayConstructorWithAutowiring() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        bf.registerSingleton("integer1", new Integer(4));
        bf.registerSingleton("integer2", new Integer(5));
        RootBeanDefinition rbd = new RootBeanDefinition(DefaultListableBeanFactoryTests.ArrayBean.class);
        rbd.setAutowireMode(AUTOWIRE_CONSTRUCTOR);
        bf.registerBeanDefinition("arrayBean", rbd);
        DefaultListableBeanFactoryTests.ArrayBean ab = ((DefaultListableBeanFactoryTests.ArrayBean) (bf.getBean("arrayBean")));
        Assert.assertEquals(new Integer(4), ab.getIntegerArray()[0]);
        Assert.assertEquals(new Integer(5), ab.getIntegerArray()[1]);
    }

    @Test
    public void testArrayConstructorWithOptionalAutowiring() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        RootBeanDefinition rbd = new RootBeanDefinition(DefaultListableBeanFactoryTests.ArrayBean.class);
        rbd.setAutowireMode(AUTOWIRE_CONSTRUCTOR);
        bf.registerBeanDefinition("arrayBean", rbd);
        DefaultListableBeanFactoryTests.ArrayBean ab = ((DefaultListableBeanFactoryTests.ArrayBean) (bf.getBean("arrayBean")));
        Assert.assertNull(ab.getIntegerArray());
    }

    @Test
    public void testDoubleArrayConstructorWithAutowiring() throws MalformedURLException {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        bf.registerSingleton("integer1", new Integer(4));
        bf.registerSingleton("integer2", new Integer(5));
        bf.registerSingleton("resource1", new UrlResource("http://localhost:8080"));
        bf.registerSingleton("resource2", new UrlResource("http://localhost:9090"));
        RootBeanDefinition rbd = new RootBeanDefinition(DefaultListableBeanFactoryTests.ArrayBean.class);
        rbd.setAutowireMode(AUTOWIRE_CONSTRUCTOR);
        bf.registerBeanDefinition("arrayBean", rbd);
        DefaultListableBeanFactoryTests.ArrayBean ab = ((DefaultListableBeanFactoryTests.ArrayBean) (bf.getBean("arrayBean")));
        Assert.assertEquals(new Integer(4), ab.getIntegerArray()[0]);
        Assert.assertEquals(new Integer(5), ab.getIntegerArray()[1]);
        Assert.assertEquals(new UrlResource("http://localhost:8080"), ab.getResourceArray()[0]);
        Assert.assertEquals(new UrlResource("http://localhost:9090"), ab.getResourceArray()[1]);
    }

    @Test
    public void testDoubleArrayConstructorWithOptionalAutowiring() throws MalformedURLException {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        bf.registerSingleton("resource1", new UrlResource("http://localhost:8080"));
        bf.registerSingleton("resource2", new UrlResource("http://localhost:9090"));
        RootBeanDefinition rbd = new RootBeanDefinition(DefaultListableBeanFactoryTests.ArrayBean.class);
        rbd.setAutowireMode(AUTOWIRE_CONSTRUCTOR);
        bf.registerBeanDefinition("arrayBean", rbd);
        DefaultListableBeanFactoryTests.ArrayBean ab = ((DefaultListableBeanFactoryTests.ArrayBean) (bf.getBean("arrayBean")));
        Assert.assertNull(ab.getIntegerArray());
        Assert.assertNull(ab.getResourceArray());
    }

    @Test
    public void testExpressionInStringArray() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        BeanExpressionResolver beanExpressionResolver = Mockito.mock(BeanExpressionResolver.class);
        Mockito.when(beanExpressionResolver.evaluate(ArgumentMatchers.eq("#{foo}"), ArgumentMatchers.any(BeanExpressionContext.class))).thenReturn("classpath:/org/springframework/beans/factory/xml/util.properties");
        bf.setBeanExpressionResolver(beanExpressionResolver);
        RootBeanDefinition rbd = new RootBeanDefinition(PropertiesFactoryBean.class);
        MutablePropertyValues pvs = new MutablePropertyValues();
        pvs.add("locations", new String[]{ "#{foo}" });
        rbd.setPropertyValues(pvs);
        bf.registerBeanDefinition("myProperties", rbd);
        Properties properties = ((Properties) (bf.getBean("myProperties")));
        Assert.assertEquals("bar", properties.getProperty("foo"));
    }

    @Test
    public void testAutowireWithNoDependencies() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
        lbf.registerBeanDefinition("rod", bd);
        Assert.assertEquals(1, lbf.getBeanDefinitionCount());
        Object registered = lbf.autowire(DefaultListableBeanFactoryTests.NoDependencies.class, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, false);
        Assert.assertEquals(1, lbf.getBeanDefinitionCount());
        Assert.assertTrue((registered instanceof DefaultListableBeanFactoryTests.NoDependencies));
    }

    @Test
    public void testAutowireWithSatisfiedJavaBeanDependency() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        MutablePropertyValues pvs = new MutablePropertyValues();
        pvs.add("name", "Rod");
        RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
        bd.setPropertyValues(pvs);
        lbf.registerBeanDefinition("rod", bd);
        Assert.assertEquals(1, lbf.getBeanDefinitionCount());
        // Depends on age, name and spouse (TestBean)
        Object registered = lbf.autowire(DependenciesBean.class, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
        Assert.assertEquals(1, lbf.getBeanDefinitionCount());
        DependenciesBean kerry = ((DependenciesBean) (registered));
        TestBean rod = ((TestBean) (lbf.getBean("rod")));
        Assert.assertSame(rod, kerry.getSpouse());
    }

    @Test
    public void testAutowireWithSatisfiedConstructorDependency() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        MutablePropertyValues pvs = new MutablePropertyValues();
        pvs.add("name", "Rod");
        RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
        bd.setPropertyValues(pvs);
        lbf.registerBeanDefinition("rod", bd);
        Assert.assertEquals(1, lbf.getBeanDefinitionCount());
        Object registered = lbf.autowire(DefaultListableBeanFactoryTests.ConstructorDependency.class, AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR, false);
        Assert.assertEquals(1, lbf.getBeanDefinitionCount());
        DefaultListableBeanFactoryTests.ConstructorDependency kerry = ((DefaultListableBeanFactoryTests.ConstructorDependency) (registered));
        TestBean rod = ((TestBean) (lbf.getBean("rod")));
        Assert.assertSame(rod, kerry.spouse);
    }

    @Test
    public void testAutowireWithTwoMatchesForConstructorDependency() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
        lbf.registerBeanDefinition("rod", bd);
        RootBeanDefinition bd2 = new RootBeanDefinition(TestBean.class);
        lbf.registerBeanDefinition("rod2", bd2);
        try {
            lbf.autowire(DefaultListableBeanFactoryTests.ConstructorDependency.class, AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR, false);
            Assert.fail("Should have thrown UnsatisfiedDependencyException");
        } catch (UnsatisfiedDependencyException ex) {
            // expected
            Assert.assertTrue(ex.getMessage().contains("rod"));
            Assert.assertTrue(ex.getMessage().contains("rod2"));
        }
    }

    @Test
    public void testAutowireWithUnsatisfiedConstructorDependency() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        MutablePropertyValues pvs = new MutablePropertyValues();
        pvs.addPropertyValue(new PropertyValue("name", "Rod"));
        RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
        bd.setPropertyValues(pvs);
        lbf.registerBeanDefinition("rod", bd);
        Assert.assertEquals(1, lbf.getBeanDefinitionCount());
        try {
            lbf.autowire(DefaultListableBeanFactoryTests.UnsatisfiedConstructorDependency.class, AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR, true);
            Assert.fail("Should have unsatisfied constructor dependency on SideEffectBean");
        } catch (UnsatisfiedDependencyException ex) {
            // expected
        }
    }

    @Test
    public void testAutowireConstructor() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
        lbf.registerBeanDefinition("spouse", bd);
        ConstructorDependenciesBean bean = ((ConstructorDependenciesBean) (lbf.autowire(ConstructorDependenciesBean.class, AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR, true)));
        Object spouse = lbf.getBean("spouse");
        Assert.assertTrue(((bean.getSpouse1()) == spouse));
        Assert.assertTrue(((BeanFactoryUtils.beanOfType(lbf, TestBean.class)) == spouse));
    }

    @Test
    public void testAutowireBeanByName() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
        lbf.registerBeanDefinition("spouse", bd);
        DependenciesBean bean = ((DependenciesBean) (lbf.autowire(DependenciesBean.class, AUTOWIRE_BY_NAME, true)));
        TestBean spouse = ((TestBean) (lbf.getBean("spouse")));
        Assert.assertEquals(spouse, bean.getSpouse());
        Assert.assertTrue(((BeanFactoryUtils.beanOfType(lbf, TestBean.class)) == spouse));
    }

    @Test
    public void testAutowireBeanByNameWithDependencyCheck() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
        lbf.registerBeanDefinition("spous", bd);
        try {
            lbf.autowire(DependenciesBean.class, AUTOWIRE_BY_NAME, true);
            Assert.fail("Should have thrown UnsatisfiedDependencyException");
        } catch (UnsatisfiedDependencyException ex) {
            // expected
        }
    }

    @Test
    public void testAutowireBeanByNameWithNoDependencyCheck() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
        lbf.registerBeanDefinition("spous", bd);
        DependenciesBean bean = ((DependenciesBean) (lbf.autowire(DependenciesBean.class, AUTOWIRE_BY_NAME, false)));
        Assert.assertNull(bean.getSpouse());
    }

    @Test
    public void testDependsOnCycle() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition bd1 = new RootBeanDefinition(TestBean.class);
        bd1.setDependsOn("tb2");
        lbf.registerBeanDefinition("tb1", bd1);
        RootBeanDefinition bd2 = new RootBeanDefinition(TestBean.class);
        bd2.setDependsOn("tb1");
        lbf.registerBeanDefinition("tb2", bd2);
        try {
            lbf.preInstantiateSingletons();
            Assert.fail("Should have thrown BeanCreationException");
        } catch (BeanCreationException ex) {
            // expected
            Assert.assertTrue(ex.getMessage().contains("Circular"));
            Assert.assertTrue(ex.getMessage().contains("'tb2'"));
            Assert.assertTrue(ex.getMessage().contains("'tb1'"));
        }
    }

    @Test
    public void testImplicitDependsOnCycle() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition bd1 = new RootBeanDefinition(TestBean.class);
        bd1.setDependsOn("tb2");
        lbf.registerBeanDefinition("tb1", bd1);
        RootBeanDefinition bd2 = new RootBeanDefinition(TestBean.class);
        bd2.setDependsOn("tb3");
        lbf.registerBeanDefinition("tb2", bd2);
        RootBeanDefinition bd3 = new RootBeanDefinition(TestBean.class);
        bd3.setDependsOn("tb1");
        lbf.registerBeanDefinition("tb3", bd3);
        try {
            lbf.preInstantiateSingletons();
            Assert.fail("Should have thrown BeanCreationException");
        } catch (BeanCreationException ex) {
            // expected
            Assert.assertTrue(ex.getMessage().contains("Circular"));
            Assert.assertTrue(ex.getMessage().contains("'tb3'"));
            Assert.assertTrue(ex.getMessage().contains("'tb1'"));
        }
    }

    @Test(expected = NoSuchBeanDefinitionException.class)
    public void testGetBeanByTypeWithNoneFound() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        lbf.getBean(TestBean.class);
    }

    @Test
    public void testGetBeanByTypeDefinedInParent() {
        DefaultListableBeanFactory parent = new DefaultListableBeanFactory();
        RootBeanDefinition bd1 = new RootBeanDefinition(TestBean.class);
        parent.registerBeanDefinition("bd1", bd1);
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory(parent);
        TestBean bean = lbf.getBean(TestBean.class);
        Assert.assertThat(bean.getBeanName(), equalTo("bd1"));
    }

    @Test(expected = NoUniqueBeanDefinitionException.class)
    public void testGetBeanByTypeWithAmbiguity() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition bd1 = new RootBeanDefinition(TestBean.class);
        RootBeanDefinition bd2 = new RootBeanDefinition(TestBean.class);
        lbf.registerBeanDefinition("bd1", bd1);
        lbf.registerBeanDefinition("bd2", bd2);
        lbf.getBean(TestBean.class);
    }

    @Test
    public void testGetBeanByTypeWithPrimary() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition bd1 = new RootBeanDefinition(TestBean.class);
        bd1.setLazyInit(true);
        RootBeanDefinition bd2 = new RootBeanDefinition(TestBean.class);
        bd2.setPrimary(true);
        lbf.registerBeanDefinition("bd1", bd1);
        lbf.registerBeanDefinition("bd2", bd2);
        TestBean bean = lbf.getBean(TestBean.class);
        Assert.assertThat(bean.getBeanName(), equalTo("bd2"));
        Assert.assertFalse(lbf.containsSingleton("bd1"));
    }

    @Test
    public void testGetBeanByTypeWithMultiplePrimary() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition bd1 = new RootBeanDefinition(TestBean.class);
        bd1.setPrimary(true);
        RootBeanDefinition bd2 = new RootBeanDefinition(TestBean.class);
        bd2.setPrimary(true);
        lbf.registerBeanDefinition("bd1", bd1);
        lbf.registerBeanDefinition("bd2", bd2);
        thrown.expect(NoUniqueBeanDefinitionException.class);
        thrown.expectMessage(containsString("more than one 'primary'"));
        lbf.getBean(TestBean.class);
    }

    @Test
    public void testGetBeanByTypeWithPriority() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        lbf.setDependencyComparator(INSTANCE);
        RootBeanDefinition bd1 = new RootBeanDefinition(DefaultListableBeanFactoryTests.HighPriorityTestBean.class);
        RootBeanDefinition bd2 = new RootBeanDefinition(DefaultListableBeanFactoryTests.LowPriorityTestBean.class);
        RootBeanDefinition bd3 = new RootBeanDefinition(DefaultListableBeanFactoryTests.NullTestBeanFactoryBean.class);
        lbf.registerBeanDefinition("bd1", bd1);
        lbf.registerBeanDefinition("bd2", bd2);
        lbf.registerBeanDefinition("bd3", bd3);
        lbf.preInstantiateSingletons();
        TestBean bean = lbf.getBean(TestBean.class);
        Assert.assertThat(bean.getBeanName(), equalTo("bd1"));
    }

    @Test
    public void testMapInjectionWithPriority() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        lbf.setDependencyComparator(INSTANCE);
        RootBeanDefinition bd1 = new RootBeanDefinition(DefaultListableBeanFactoryTests.HighPriorityTestBean.class);
        RootBeanDefinition bd2 = new RootBeanDefinition(DefaultListableBeanFactoryTests.LowPriorityTestBean.class);
        RootBeanDefinition bd3 = new RootBeanDefinition(DefaultListableBeanFactoryTests.NullTestBeanFactoryBean.class);
        RootBeanDefinition bd4 = new RootBeanDefinition(DefaultListableBeanFactoryTests.TestBeanRecipient.class, RootBeanDefinition.AUTOWIRE_CONSTRUCTOR, false);
        lbf.registerBeanDefinition("bd1", bd1);
        lbf.registerBeanDefinition("bd2", bd2);
        lbf.registerBeanDefinition("bd3", bd3);
        lbf.registerBeanDefinition("bd4", bd4);
        lbf.preInstantiateSingletons();
        TestBean bean = lbf.getBean(DefaultListableBeanFactoryTests.TestBeanRecipient.class).testBean;
        Assert.assertThat(bean.getBeanName(), equalTo("bd1"));
    }

    @Test
    public void testGetBeanByTypeWithMultiplePriority() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        lbf.setDependencyComparator(INSTANCE);
        RootBeanDefinition bd1 = new RootBeanDefinition(DefaultListableBeanFactoryTests.HighPriorityTestBean.class);
        RootBeanDefinition bd2 = new RootBeanDefinition(DefaultListableBeanFactoryTests.HighPriorityTestBean.class);
        lbf.registerBeanDefinition("bd1", bd1);
        lbf.registerBeanDefinition("bd2", bd2);
        thrown.expect(NoUniqueBeanDefinitionException.class);
        thrown.expectMessage(containsString("Multiple beans found with the same priority"));
        thrown.expectMessage(containsString("5"));// conflicting priority

        lbf.getBean(TestBean.class);
    }

    @Test
    public void testGetBeanByTypeWithPriorityAndNullInstance() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        lbf.setDependencyComparator(INSTANCE);
        RootBeanDefinition bd1 = new RootBeanDefinition(DefaultListableBeanFactoryTests.HighPriorityTestBean.class);
        RootBeanDefinition bd2 = new RootBeanDefinition(DefaultListableBeanFactoryTests.NullTestBeanFactoryBean.class);
        lbf.registerBeanDefinition("bd1", bd1);
        lbf.registerBeanDefinition("bd2", bd2);
        TestBean bean = lbf.getBean(TestBean.class);
        Assert.assertThat(bean.getBeanName(), equalTo("bd1"));
    }

    @Test
    public void testGetBeanByTypePrimaryHasPrecedenceOverPriority() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        lbf.setDependencyComparator(INSTANCE);
        RootBeanDefinition bd1 = new RootBeanDefinition(DefaultListableBeanFactoryTests.HighPriorityTestBean.class);
        RootBeanDefinition bd2 = new RootBeanDefinition(TestBean.class);
        bd2.setPrimary(true);
        lbf.registerBeanDefinition("bd1", bd1);
        lbf.registerBeanDefinition("bd2", bd2);
        TestBean bean = lbf.getBean(TestBean.class);
        Assert.assertThat(bean.getBeanName(), equalTo("bd2"));
    }

    @Test
    public void testGetBeanByTypeFiltersOutNonAutowireCandidates() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition bd1 = new RootBeanDefinition(TestBean.class);
        RootBeanDefinition bd2 = new RootBeanDefinition(TestBean.class);
        RootBeanDefinition na1 = new RootBeanDefinition(TestBean.class);
        na1.setAutowireCandidate(false);
        lbf.registerBeanDefinition("bd1", bd1);
        lbf.registerBeanDefinition("na1", na1);
        TestBean actual = lbf.getBean(TestBean.class);// na1 was filtered

        Assert.assertSame(lbf.getBean("bd1", TestBean.class), actual);
        lbf.registerBeanDefinition("bd2", bd2);
        try {
            lbf.getBean(TestBean.class);
            Assert.fail("Should have thrown NoSuchBeanDefinitionException");
        } catch (NoSuchBeanDefinitionException ex) {
            // expected
        }
    }

    @Test
    public void testGetBeanByTypeInstanceWithNoneFound() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        try {
            lbf.getBean(DefaultListableBeanFactoryTests.ConstructorDependency.class);
            Assert.fail("Should have thrown NoSuchBeanDefinitionException");
        } catch (NoSuchBeanDefinitionException ex) {
            // expected
        }
        try {
            lbf.getBean(DefaultListableBeanFactoryTests.ConstructorDependency.class, 42);
            Assert.fail("Should have thrown NoSuchBeanDefinitionException");
        } catch (NoSuchBeanDefinitionException ex) {
            // expected
        }
        ObjectProvider<DefaultListableBeanFactoryTests.ConstructorDependency> provider = lbf.getBeanProvider(DefaultListableBeanFactoryTests.ConstructorDependency.class);
        try {
            provider.getObject();
            Assert.fail("Should have thrown NoSuchBeanDefinitionException");
        } catch (NoSuchBeanDefinitionException ex) {
            // expected
        }
        try {
            provider.getObject(42);
            Assert.fail("Should have thrown NoSuchBeanDefinitionException");
        } catch (NoSuchBeanDefinitionException ex) {
            // expected
        }
        Assert.assertNull(provider.getIfAvailable());
        Assert.assertNull(provider.getIfUnique());
    }

    @Test
    public void testGetBeanByTypeInstanceDefinedInParent() {
        DefaultListableBeanFactory parent = new DefaultListableBeanFactory();
        RootBeanDefinition bd1 = createConstructorDependencyBeanDefinition(99);
        parent.registerBeanDefinition("bd1", bd1);
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory(parent);
        DefaultListableBeanFactoryTests.ConstructorDependency bean = lbf.getBean(DefaultListableBeanFactoryTests.ConstructorDependency.class);
        Assert.assertThat(bean.beanName, equalTo("bd1"));
        Assert.assertThat(bean.spouseAge, equalTo(99));
        bean = lbf.getBean(DefaultListableBeanFactoryTests.ConstructorDependency.class, 42);
        Assert.assertThat(bean.beanName, equalTo("bd1"));
        Assert.assertThat(bean.spouseAge, equalTo(42));
        ObjectProvider<DefaultListableBeanFactoryTests.ConstructorDependency> provider = lbf.getBeanProvider(DefaultListableBeanFactoryTests.ConstructorDependency.class);
        bean = provider.getObject();
        Assert.assertThat(bean.beanName, equalTo("bd1"));
        Assert.assertThat(bean.spouseAge, equalTo(99));
        bean = provider.getObject(42);
        Assert.assertThat(bean.beanName, equalTo("bd1"));
        Assert.assertThat(bean.spouseAge, equalTo(42));
        bean = provider.getIfAvailable();
        Assert.assertThat(bean.beanName, equalTo("bd1"));
        Assert.assertThat(bean.spouseAge, equalTo(99));
        bean = provider.getIfUnique();
        Assert.assertThat(bean.beanName, equalTo("bd1"));
        Assert.assertThat(bean.spouseAge, equalTo(99));
    }

    @Test
    public void testGetBeanByTypeInstanceWithAmbiguity() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition bd1 = createConstructorDependencyBeanDefinition(99);
        RootBeanDefinition bd2 = new RootBeanDefinition(DefaultListableBeanFactoryTests.ConstructorDependency.class);
        bd2.setScope(SCOPE_PROTOTYPE);
        bd2.getConstructorArgumentValues().addGenericArgumentValue("43");
        lbf.registerBeanDefinition("bd1", bd1);
        lbf.registerBeanDefinition("bd2", bd2);
        try {
            lbf.getBean(DefaultListableBeanFactoryTests.ConstructorDependency.class);
            Assert.fail("Should have thrown NoUniqueBeanDefinitionException");
        } catch (NoUniqueBeanDefinitionException ex) {
            // expected
        }
        try {
            lbf.getBean(DefaultListableBeanFactoryTests.ConstructorDependency.class, 42);
            Assert.fail("Should have thrown NoUniqueBeanDefinitionException");
        } catch (NoUniqueBeanDefinitionException ex) {
            // expected
        }
        ObjectProvider<DefaultListableBeanFactoryTests.ConstructorDependency> provider = lbf.getBeanProvider(DefaultListableBeanFactoryTests.ConstructorDependency.class);
        try {
            provider.getObject();
            Assert.fail("Should have thrown NoUniqueBeanDefinitionException");
        } catch (NoUniqueBeanDefinitionException ex) {
            // expected
        }
        try {
            provider.getObject(42);
            Assert.fail("Should have thrown NoUniqueBeanDefinitionException");
        } catch (NoUniqueBeanDefinitionException ex) {
            // expected
        }
        try {
            provider.getIfAvailable();
            Assert.fail("Should have thrown NoUniqueBeanDefinitionException");
        } catch (NoUniqueBeanDefinitionException ex) {
            // expected
        }
        Assert.assertNull(provider.getIfUnique());
        Set<Object> resolved = new HashSet<>();
        for (DefaultListableBeanFactoryTests.ConstructorDependency instance : provider) {
            resolved.add(instance);
        }
        Assert.assertEquals(2, resolved.size());
        Assert.assertTrue(resolved.contains(lbf.getBean("bd1")));
        Assert.assertTrue(resolved.contains(lbf.getBean("bd2")));
        resolved = new HashSet<>();
        provider.forEach(resolved::add);
        Assert.assertEquals(2, resolved.size());
        Assert.assertTrue(resolved.contains(lbf.getBean("bd1")));
        Assert.assertTrue(resolved.contains(lbf.getBean("bd2")));
        resolved = provider.stream().collect(Collectors.toSet());
        Assert.assertEquals(2, resolved.size());
        Assert.assertTrue(resolved.contains(lbf.getBean("bd1")));
        Assert.assertTrue(resolved.contains(lbf.getBean("bd2")));
    }

    @Test
    public void testGetBeanByTypeInstanceWithPrimary() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition bd1 = createConstructorDependencyBeanDefinition(99);
        RootBeanDefinition bd2 = createConstructorDependencyBeanDefinition(43);
        bd2.setPrimary(true);
        lbf.registerBeanDefinition("bd1", bd1);
        lbf.registerBeanDefinition("bd2", bd2);
        DefaultListableBeanFactoryTests.ConstructorDependency bean = lbf.getBean(DefaultListableBeanFactoryTests.ConstructorDependency.class);
        Assert.assertThat(bean.beanName, equalTo("bd2"));
        Assert.assertThat(bean.spouseAge, equalTo(43));
        bean = lbf.getBean(DefaultListableBeanFactoryTests.ConstructorDependency.class, 42);
        Assert.assertThat(bean.beanName, equalTo("bd2"));
        Assert.assertThat(bean.spouseAge, equalTo(42));
        ObjectProvider<DefaultListableBeanFactoryTests.ConstructorDependency> provider = lbf.getBeanProvider(DefaultListableBeanFactoryTests.ConstructorDependency.class);
        bean = provider.getObject();
        Assert.assertThat(bean.beanName, equalTo("bd2"));
        Assert.assertThat(bean.spouseAge, equalTo(43));
        bean = provider.getObject(42);
        Assert.assertThat(bean.beanName, equalTo("bd2"));
        Assert.assertThat(bean.spouseAge, equalTo(42));
        bean = provider.getIfAvailable();
        Assert.assertThat(bean.beanName, equalTo("bd2"));
        Assert.assertThat(bean.spouseAge, equalTo(43));
        bean = provider.getIfUnique();
        Assert.assertThat(bean.beanName, equalTo("bd2"));
        Assert.assertThat(bean.spouseAge, equalTo(43));
        Set<Object> resolved = new HashSet<>();
        for (DefaultListableBeanFactoryTests.ConstructorDependency instance : provider) {
            resolved.add(instance);
        }
        Assert.assertEquals(2, resolved.size());
        Assert.assertTrue(resolved.contains(lbf.getBean("bd1")));
        Assert.assertTrue(resolved.contains(lbf.getBean("bd2")));
        resolved = new HashSet<>();
        provider.forEach(resolved::add);
        Assert.assertEquals(2, resolved.size());
        Assert.assertTrue(resolved.contains(lbf.getBean("bd1")));
        Assert.assertTrue(resolved.contains(lbf.getBean("bd2")));
        resolved = provider.stream().collect(Collectors.toSet());
        Assert.assertEquals(2, resolved.size());
        Assert.assertTrue(resolved.contains(lbf.getBean("bd1")));
        Assert.assertTrue(resolved.contains(lbf.getBean("bd2")));
    }

    @Test
    public void testGetBeanByTypeInstanceWithMultiplePrimary() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition bd1 = createConstructorDependencyBeanDefinition(99);
        RootBeanDefinition bd2 = createConstructorDependencyBeanDefinition(43);
        bd1.setPrimary(true);
        bd2.setPrimary(true);
        lbf.registerBeanDefinition("bd1", bd1);
        lbf.registerBeanDefinition("bd2", bd2);
        thrown.expect(NoUniqueBeanDefinitionException.class);
        thrown.expectMessage(containsString("more than one 'primary'"));
        lbf.getBean(DefaultListableBeanFactoryTests.ConstructorDependency.class, 42);
    }

    @Test
    public void testGetBeanByTypeInstanceFiltersOutNonAutowireCandidates() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition bd1 = createConstructorDependencyBeanDefinition(99);
        RootBeanDefinition bd2 = createConstructorDependencyBeanDefinition(43);
        RootBeanDefinition na1 = createConstructorDependencyBeanDefinition(21);
        na1.setAutowireCandidate(false);
        lbf.registerBeanDefinition("bd1", bd1);
        lbf.registerBeanDefinition("na1", na1);
        DefaultListableBeanFactoryTests.ConstructorDependency actual = lbf.getBean(DefaultListableBeanFactoryTests.ConstructorDependency.class, 42);// na1 was filtered

        Assert.assertThat(actual.beanName, equalTo("bd1"));
        lbf.registerBeanDefinition("bd2", bd2);
        try {
            lbf.getBean(TestBean.class, 67);
            Assert.fail("Should have thrown NoSuchBeanDefinitionException");
        } catch (NoSuchBeanDefinitionException ex) {
            // expected
        }
    }

    @Test
    public void testBeanProviderSerialization() throws Exception {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        lbf.setSerializationId("test");
        ObjectProvider<DefaultListableBeanFactoryTests.ConstructorDependency> provider = lbf.getBeanProvider(DefaultListableBeanFactoryTests.ConstructorDependency.class);
        ObjectProvider deserialized = ((ObjectProvider) (SerializationTestUtils.serializeAndDeserialize(provider)));
        try {
            deserialized.getObject();
            Assert.fail("Should have thrown NoSuchBeanDefinitionException");
        } catch (NoSuchBeanDefinitionException ex) {
            // expected
        }
        try {
            deserialized.getObject(42);
            Assert.fail("Should have thrown NoSuchBeanDefinitionException");
        } catch (NoSuchBeanDefinitionException ex) {
            // expected
        }
        Assert.assertNull(deserialized.getIfAvailable());
        Assert.assertNull(deserialized.getIfUnique());
    }

    @Test
    public void testGetBeanWithArgsNotCreatedForFactoryBeanChecking() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition bd1 = new RootBeanDefinition(DefaultListableBeanFactoryTests.ConstructorDependency.class);
        bd1.setScope(SCOPE_PROTOTYPE);
        lbf.registerBeanDefinition("bd1", bd1);
        RootBeanDefinition bd2 = new RootBeanDefinition(DefaultListableBeanFactoryTests.ConstructorDependencyFactoryBean.class);
        bd2.setScope(SCOPE_PROTOTYPE);
        lbf.registerBeanDefinition("bd2", bd2);
        DefaultListableBeanFactoryTests.ConstructorDependency bean = lbf.getBean(DefaultListableBeanFactoryTests.ConstructorDependency.class, 42);
        Assert.assertThat(bean.beanName, equalTo("bd1"));
        Assert.assertThat(bean.spouseAge, equalTo(42));
        Assert.assertEquals(1, lbf.getBeanNamesForType(DefaultListableBeanFactoryTests.ConstructorDependency.class).length);
        Assert.assertEquals(1, lbf.getBeanNamesForType(DefaultListableBeanFactoryTests.ConstructorDependencyFactoryBean.class).length);
        Assert.assertEquals(1, lbf.getBeanNamesForType(ResolvableType.forClassWithGenerics(FactoryBean.class, Object.class)).length);
        Assert.assertEquals(0, lbf.getBeanNamesForType(ResolvableType.forClassWithGenerics(FactoryBean.class, String.class)).length);
    }

    @Test
    public void testAutowireBeanByType() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
        lbf.registerBeanDefinition("test", bd);
        DependenciesBean bean = ((DependenciesBean) (lbf.autowire(DependenciesBean.class, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true)));
        TestBean test = ((TestBean) (lbf.getBean("test")));
        Assert.assertEquals(test, bean.getSpouse());
    }

    /**
     * Verifies that a dependency on a {@link FactoryBean} can be autowired
     * <em>by type</em>, specifically addressing the JIRA issue raised in <a
     * href="http://opensource.atlassian.com/projects/spring/browse/SPR-4040"
     * target="_blank">SPR-4040</a>.
     */
    @Test
    public void testAutowireBeanWithFactoryBeanByType() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition bd = new RootBeanDefinition(DefaultListableBeanFactoryTests.LazyInitFactory.class);
        lbf.registerBeanDefinition("factoryBean", bd);
        DefaultListableBeanFactoryTests.LazyInitFactory factoryBean = ((DefaultListableBeanFactoryTests.LazyInitFactory) (lbf.getBean("&factoryBean")));
        Assert.assertNotNull("The FactoryBean should have been registered.", factoryBean);
        DefaultListableBeanFactoryTests.FactoryBeanDependentBean bean = ((DefaultListableBeanFactoryTests.FactoryBeanDependentBean) (lbf.autowire(DefaultListableBeanFactoryTests.FactoryBeanDependentBean.class, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true)));
        Assert.assertEquals("The FactoryBeanDependentBean should have been autowired 'by type' with the LazyInitFactory.", factoryBean, bean.getFactoryBean());
    }

    @Test
    public void testGetTypeForAbstractFactoryBean() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition bd = new RootBeanDefinition(DefaultListableBeanFactoryTests.FactoryBeanThatShouldntBeCalled.class);
        bd.setAbstract(true);
        lbf.registerBeanDefinition("factoryBean", bd);
        Assert.assertNull(lbf.getType("factoryBean"));
    }

    @Test
    public void testGetBeanNamesForTypeBeforeFactoryBeanCreation() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        lbf.registerBeanDefinition("factoryBean", new RootBeanDefinition(DefaultListableBeanFactoryTests.FactoryBeanThatShouldntBeCalled.class));
        Assert.assertFalse(lbf.containsSingleton("factoryBean"));
        String[] beanNames = lbf.getBeanNamesForType(Runnable.class, false, false);
        Assert.assertEquals(1, beanNames.length);
        Assert.assertEquals("&factoryBean", beanNames[0]);
        beanNames = lbf.getBeanNamesForType(Callable.class, false, false);
        Assert.assertEquals(1, beanNames.length);
        Assert.assertEquals("&factoryBean", beanNames[0]);
        beanNames = lbf.getBeanNamesForType(DefaultListableBeanFactoryTests.RepositoryFactoryInformation.class, false, false);
        Assert.assertEquals(1, beanNames.length);
        Assert.assertEquals("&factoryBean", beanNames[0]);
        beanNames = lbf.getBeanNamesForType(FactoryBean.class, false, false);
        Assert.assertEquals(1, beanNames.length);
        Assert.assertEquals("&factoryBean", beanNames[0]);
    }

    @Test
    public void testGetBeanNamesForTypeAfterFactoryBeanCreation() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        lbf.registerBeanDefinition("factoryBean", new RootBeanDefinition(DefaultListableBeanFactoryTests.FactoryBeanThatShouldntBeCalled.class));
        lbf.getBean("&factoryBean");
        String[] beanNames = lbf.getBeanNamesForType(Runnable.class, false, false);
        Assert.assertEquals(1, beanNames.length);
        Assert.assertEquals("&factoryBean", beanNames[0]);
        beanNames = lbf.getBeanNamesForType(Callable.class, false, false);
        Assert.assertEquals(1, beanNames.length);
        Assert.assertEquals("&factoryBean", beanNames[0]);
        beanNames = lbf.getBeanNamesForType(DefaultListableBeanFactoryTests.RepositoryFactoryInformation.class, false, false);
        Assert.assertEquals(1, beanNames.length);
        Assert.assertEquals("&factoryBean", beanNames[0]);
        beanNames = lbf.getBeanNamesForType(FactoryBean.class, false, false);
        Assert.assertEquals(1, beanNames.length);
        Assert.assertEquals("&factoryBean", beanNames[0]);
    }

    /**
     * Verifies that a dependency on a {@link FactoryBean} can <strong>not</strong>
     * be autowired <em>by name</em>, as &amp; is an illegal character in
     * Java method names. In other words, you can't name a method
     * {@code set&amp;FactoryBean(...)}.
     */
    @Test(expected = TypeMismatchException.class)
    public void testAutowireBeanWithFactoryBeanByName() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition bd = new RootBeanDefinition(DefaultListableBeanFactoryTests.LazyInitFactory.class);
        lbf.registerBeanDefinition("factoryBean", bd);
        DefaultListableBeanFactoryTests.LazyInitFactory factoryBean = ((DefaultListableBeanFactoryTests.LazyInitFactory) (lbf.getBean("&factoryBean")));
        Assert.assertNotNull("The FactoryBean should have been registered.", factoryBean);
        lbf.autowire(DefaultListableBeanFactoryTests.FactoryBeanDependentBean.class, AUTOWIRE_BY_NAME, true);
    }

    @Test
    public void testAutowireBeanByTypeWithTwoMatches() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
        RootBeanDefinition bd2 = new RootBeanDefinition(TestBean.class);
        lbf.registerBeanDefinition("test", bd);
        lbf.registerBeanDefinition("spouse", bd2);
        try {
            lbf.autowire(DependenciesBean.class, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
            Assert.fail("Should have thrown UnsatisfiedDependencyException");
        } catch (UnsatisfiedDependencyException ex) {
            // expected
            Assert.assertTrue(ex.getMessage().contains("test"));
            Assert.assertTrue(ex.getMessage().contains("spouse"));
        }
    }

    @Test
    public void testAutowireBeanByTypeWithDependencyCheck() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        try {
            lbf.autowire(DependenciesBean.class, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
            Assert.fail("Should have thrown UnsatisfiedDependencyException");
        } catch (UnsatisfiedDependencyException ex) {
            // expected
        }
    }

    @Test
    public void testAutowireBeanByTypeWithNoDependencyCheck() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        DependenciesBean bean = ((DependenciesBean) (lbf.autowire(DependenciesBean.class, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, false)));
        Assert.assertNull(bean.getSpouse());
    }

    @Test
    public void testAutowireBeanByTypeWithTwoMatchesAndOnePrimary() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
        bd.setPrimary(true);
        RootBeanDefinition bd2 = new RootBeanDefinition(TestBean.class);
        lbf.registerBeanDefinition("test", bd);
        lbf.registerBeanDefinition("spouse", bd2);
        DependenciesBean bean = ((DependenciesBean) (lbf.autowire(DependenciesBean.class, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true)));
        Assert.assertThat(bean.getSpouse(), equalTo(lbf.getBean("test")));
    }

    @Test
    public void testAutowireBeanByTypeWithTwoPrimaryCandidates() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
        bd.setPrimary(true);
        RootBeanDefinition bd2 = new RootBeanDefinition(TestBean.class);
        bd2.setPrimary(true);
        lbf.registerBeanDefinition("test", bd);
        lbf.registerBeanDefinition("spouse", bd2);
        try {
            lbf.autowire(DependenciesBean.class, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
            Assert.fail("Should have thrown UnsatisfiedDependencyException");
        } catch (UnsatisfiedDependencyException ex) {
            // expected
            Assert.assertNotNull("Exception should have cause", ex.getCause());
            Assert.assertEquals("Wrong cause type", NoUniqueBeanDefinitionException.class, ex.getCause().getClass());
        }
    }

    @Test
    public void testAutowireBeanByTypeWithTwoMatchesAndPriority() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        lbf.setDependencyComparator(INSTANCE);
        RootBeanDefinition bd = new RootBeanDefinition(DefaultListableBeanFactoryTests.HighPriorityTestBean.class);
        RootBeanDefinition bd2 = new RootBeanDefinition(DefaultListableBeanFactoryTests.LowPriorityTestBean.class);
        lbf.registerBeanDefinition("test", bd);
        lbf.registerBeanDefinition("spouse", bd2);
        DependenciesBean bean = ((DependenciesBean) (lbf.autowire(DependenciesBean.class, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true)));
        Assert.assertThat(bean.getSpouse(), equalTo(lbf.getBean("test")));
    }

    @Test
    public void testAutowireBeanByTypeWithIdenticalPriorityCandidates() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        lbf.setDependencyComparator(INSTANCE);
        RootBeanDefinition bd = new RootBeanDefinition(DefaultListableBeanFactoryTests.HighPriorityTestBean.class);
        RootBeanDefinition bd2 = new RootBeanDefinition(DefaultListableBeanFactoryTests.HighPriorityTestBean.class);
        lbf.registerBeanDefinition("test", bd);
        lbf.registerBeanDefinition("spouse", bd2);
        try {
            lbf.autowire(DependenciesBean.class, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
            Assert.fail("Should have thrown UnsatisfiedDependencyException");
        } catch (UnsatisfiedDependencyException ex) {
            // expected
            Assert.assertNotNull("Exception should have cause", ex.getCause());
            Assert.assertEquals("Wrong cause type", NoUniqueBeanDefinitionException.class, ex.getCause().getClass());
            Assert.assertTrue(ex.getMessage().contains("5"));// conflicting priority

        }
    }

    @Test
    public void testAutowireBeanByTypePrimaryTakesPrecedenceOverPriority() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        lbf.setDependencyComparator(INSTANCE);
        RootBeanDefinition bd = new RootBeanDefinition(DefaultListableBeanFactoryTests.HighPriorityTestBean.class);
        RootBeanDefinition bd2 = new RootBeanDefinition(TestBean.class);
        bd2.setPrimary(true);
        lbf.registerBeanDefinition("test", bd);
        lbf.registerBeanDefinition("spouse", bd2);
        DependenciesBean bean = ((DependenciesBean) (lbf.autowire(DependenciesBean.class, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true)));
        Assert.assertThat(bean.getSpouse(), equalTo(lbf.getBean("spouse")));
    }

    @Test
    public void testAutowireExistingBeanByName() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
        lbf.registerBeanDefinition("spouse", bd);
        DependenciesBean existingBean = new DependenciesBean();
        lbf.autowireBeanProperties(existingBean, AUTOWIRE_BY_NAME, true);
        TestBean spouse = ((TestBean) (lbf.getBean("spouse")));
        Assert.assertEquals(existingBean.getSpouse(), spouse);
        Assert.assertSame(spouse, BeanFactoryUtils.beanOfType(lbf, TestBean.class));
    }

    @Test
    public void testAutowireExistingBeanByNameWithDependencyCheck() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
        lbf.registerBeanDefinition("spous", bd);
        DependenciesBean existingBean = new DependenciesBean();
        try {
            lbf.autowireBeanProperties(existingBean, AUTOWIRE_BY_NAME, true);
            Assert.fail("Should have thrown UnsatisfiedDependencyException");
        } catch (UnsatisfiedDependencyException ex) {
            // expected
        }
    }

    @Test
    public void testAutowireExistingBeanByNameWithNoDependencyCheck() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
        lbf.registerBeanDefinition("spous", bd);
        DependenciesBean existingBean = new DependenciesBean();
        lbf.autowireBeanProperties(existingBean, AUTOWIRE_BY_NAME, false);
        Assert.assertNull(existingBean.getSpouse());
    }

    @Test
    public void testAutowireExistingBeanByType() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
        lbf.registerBeanDefinition("test", bd);
        DependenciesBean existingBean = new DependenciesBean();
        lbf.autowireBeanProperties(existingBean, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
        TestBean test = ((TestBean) (lbf.getBean("test")));
        Assert.assertEquals(existingBean.getSpouse(), test);
    }

    @Test
    public void testAutowireExistingBeanByTypeWithDependencyCheck() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        DependenciesBean existingBean = new DependenciesBean();
        try {
            lbf.autowireBeanProperties(existingBean, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
            Assert.fail("Should have thrown UnsatisfiedDependencyException");
        } catch (UnsatisfiedDependencyException expected) {
        }
    }

    @Test
    public void testAutowireExistingBeanByTypeWithNoDependencyCheck() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        DependenciesBean existingBean = new DependenciesBean();
        lbf.autowireBeanProperties(existingBean, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, false);
        Assert.assertNull(existingBean.getSpouse());
    }

    @Test
    public void testInvalidAutowireMode() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        try {
            lbf.autowireBeanProperties(new TestBean(), AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR, false);
            Assert.fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testApplyBeanPropertyValues() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        MutablePropertyValues pvs = new MutablePropertyValues();
        pvs.add("age", "99");
        RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
        bd.setPropertyValues(pvs);
        lbf.registerBeanDefinition("test", bd);
        TestBean tb = new TestBean();
        Assert.assertEquals(0, tb.getAge());
        lbf.applyBeanPropertyValues(tb, "test");
        Assert.assertEquals(99, tb.getAge());
    }

    @Test
    public void testApplyBeanPropertyValuesWithIncompleteDefinition() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        MutablePropertyValues pvs = new MutablePropertyValues();
        pvs.add("age", "99");
        RootBeanDefinition bd = new RootBeanDefinition();
        bd.setPropertyValues(pvs);
        lbf.registerBeanDefinition("test", bd);
        TestBean tb = new TestBean();
        Assert.assertEquals(0, tb.getAge());
        lbf.applyBeanPropertyValues(tb, "test");
        Assert.assertEquals(99, tb.getAge());
        Assert.assertNull(tb.getBeanFactory());
        Assert.assertNull(tb.getSpouse());
    }

    @Test
    public void testCreateBean() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        TestBean tb = lbf.createBean(TestBean.class);
        Assert.assertSame(lbf, tb.getBeanFactory());
        lbf.destroyBean(tb);
    }

    @Test
    public void testCreateBeanWithDisposableBean() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        DerivedTestBean tb = lbf.createBean(DerivedTestBean.class);
        Assert.assertSame(lbf, tb.getBeanFactory());
        lbf.destroyBean(tb);
        Assert.assertTrue(tb.wasDestroyed());
    }

    @Test
    public void testConfigureBean() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        MutablePropertyValues pvs = new MutablePropertyValues();
        pvs.add("age", "99");
        RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
        bd.setPropertyValues(pvs);
        lbf.registerBeanDefinition("test", bd);
        TestBean tb = new TestBean();
        Assert.assertEquals(0, tb.getAge());
        lbf.configureBean(tb, "test");
        Assert.assertEquals(99, tb.getAge());
        Assert.assertSame(lbf, tb.getBeanFactory());
        Assert.assertNull(tb.getSpouse());
    }

    @Test
    public void testConfigureBeanWithAutowiring() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
        lbf.registerBeanDefinition("spouse", bd);
        MutablePropertyValues pvs = new MutablePropertyValues();
        pvs.add("age", "99");
        RootBeanDefinition tbd = new RootBeanDefinition(TestBean.class);
        tbd.setAutowireMode(RootBeanDefinition.AUTOWIRE_BY_NAME);
        lbf.registerBeanDefinition("test", tbd);
        TestBean tb = new TestBean();
        lbf.configureBean(tb, "test");
        Assert.assertSame(lbf, tb.getBeanFactory());
        TestBean spouse = ((TestBean) (lbf.getBean("spouse")));
        Assert.assertEquals(spouse, tb.getSpouse());
    }

    @Test
    public void testExtensiveCircularReference() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        for (int i = 0; i < 1000; i++) {
            MutablePropertyValues pvs = new MutablePropertyValues();
            pvs.addPropertyValue(new PropertyValue("spouse", new RuntimeBeanReference(("bean" + (i < 99 ? i + 1 : 0)))));
            RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
            bd.setPropertyValues(pvs);
            lbf.registerBeanDefinition(("bean" + i), bd);
        }
        lbf.preInstantiateSingletons();
        for (int i = 0; i < 1000; i++) {
            TestBean bean = ((TestBean) (lbf.getBean(("bean" + i))));
            TestBean otherBean = ((TestBean) (lbf.getBean(("bean" + (i < 99 ? i + 1 : 0)))));
            Assert.assertTrue(((bean.getSpouse()) == otherBean));
        }
    }

    @Test
    public void testCircularReferenceThroughAutowiring() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition bd = new RootBeanDefinition(DefaultListableBeanFactoryTests.ConstructorDependencyBean.class);
        bd.setAutowireMode(AUTOWIRE_CONSTRUCTOR);
        lbf.registerBeanDefinition("test", bd);
        try {
            lbf.preInstantiateSingletons();
            Assert.fail("Should have thrown UnsatisfiedDependencyException");
        } catch (UnsatisfiedDependencyException expected) {
        }
    }

    @Test
    public void testCircularReferenceThroughFactoryBeanAutowiring() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition bd = new RootBeanDefinition(DefaultListableBeanFactoryTests.ConstructorDependencyFactoryBean.class);
        bd.setAutowireMode(AUTOWIRE_CONSTRUCTOR);
        lbf.registerBeanDefinition("test", bd);
        try {
            lbf.preInstantiateSingletons();
            Assert.fail("Should have thrown UnsatisfiedDependencyException");
        } catch (UnsatisfiedDependencyException expected) {
        }
    }

    @Test
    public void testCircularReferenceThroughFactoryBeanTypeCheck() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition bd = new RootBeanDefinition(DefaultListableBeanFactoryTests.ConstructorDependencyFactoryBean.class);
        bd.setAutowireMode(AUTOWIRE_CONSTRUCTOR);
        lbf.registerBeanDefinition("test", bd);
        try {
            lbf.getBeansOfType(String.class);
            Assert.fail("Should have thrown UnsatisfiedDependencyException");
        } catch (UnsatisfiedDependencyException expected) {
        }
    }

    @Test
    public void testAvoidCircularReferenceThroughAutowiring() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition bd = new RootBeanDefinition(DefaultListableBeanFactoryTests.ConstructorDependencyFactoryBean.class);
        bd.setAutowireMode(AUTOWIRE_CONSTRUCTOR);
        lbf.registerBeanDefinition("test", bd);
        RootBeanDefinition bd2 = new RootBeanDefinition(String.class);
        bd2.setAutowireMode(AUTOWIRE_CONSTRUCTOR);
        lbf.registerBeanDefinition("string", bd2);
        lbf.preInstantiateSingletons();
    }

    @Test
    public void testConstructorDependencyWithClassResolution() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition bd = new RootBeanDefinition(DefaultListableBeanFactoryTests.ConstructorDependencyWithClassResolution.class);
        bd.getConstructorArgumentValues().addGenericArgumentValue("java.lang.String");
        lbf.registerBeanDefinition("test", bd);
        lbf.preInstantiateSingletons();
    }

    @Test
    public void testConstructorDependencyWithUnresolvableClass() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition bd = new RootBeanDefinition(DefaultListableBeanFactoryTests.ConstructorDependencyWithClassResolution.class);
        bd.getConstructorArgumentValues().addGenericArgumentValue("java.lang.Strin");
        lbf.registerBeanDefinition("test", bd);
        try {
            lbf.preInstantiateSingletons();
            Assert.fail("Should have thrown UnsatisfiedDependencyException");
        } catch (UnsatisfiedDependencyException expected) {
            Assert.assertTrue(expected.toString().contains("java.lang.Strin"));
        }
    }

    @Test
    public void testBeanDefinitionWithInterface() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        lbf.registerBeanDefinition("test", new RootBeanDefinition(ITestBean.class));
        try {
            lbf.getBean("test");
            Assert.fail("Should have thrown BeanCreationException");
        } catch (BeanCreationException ex) {
            Assert.assertEquals("test", ex.getBeanName());
            Assert.assertTrue(ex.getMessage().toLowerCase().contains("interface"));
        }
    }

    @Test
    public void testBeanDefinitionWithAbstractClass() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        lbf.registerBeanDefinition("test", new RootBeanDefinition(AbstractBeanFactory.class));
        try {
            lbf.getBean("test");
            Assert.fail("Should have thrown BeanCreationException");
        } catch (BeanCreationException ex) {
            Assert.assertEquals("test", ex.getBeanName());
            Assert.assertTrue(ex.getMessage().toLowerCase().contains("abstract"));
        }
    }

    @Test
    public void testPrototypeFactoryBeanNotEagerlyCalled() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        lbf.registerBeanDefinition("test", new RootBeanDefinition(DefaultListableBeanFactoryTests.FactoryBeanThatShouldntBeCalled.class));
        lbf.preInstantiateSingletons();
    }

    @Test
    public void testLazyInitFactory() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        lbf.registerBeanDefinition("test", new RootBeanDefinition(DefaultListableBeanFactoryTests.LazyInitFactory.class));
        lbf.preInstantiateSingletons();
        DefaultListableBeanFactoryTests.LazyInitFactory factory = ((DefaultListableBeanFactoryTests.LazyInitFactory) (lbf.getBean("&test")));
        Assert.assertFalse(factory.initialized);
    }

    @Test
    public void testSmartInitFactory() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        lbf.registerBeanDefinition("test", new RootBeanDefinition(DefaultListableBeanFactoryTests.EagerInitFactory.class));
        lbf.preInstantiateSingletons();
        DefaultListableBeanFactoryTests.EagerInitFactory factory = ((DefaultListableBeanFactoryTests.EagerInitFactory) (lbf.getBean("&test")));
        Assert.assertTrue(factory.initialized);
    }

    @Test
    public void testPrototypeFactoryBeanNotEagerlyCalledInCaseOfBeanClassName() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        lbf.registerBeanDefinition("test", new RootBeanDefinition(DefaultListableBeanFactoryTests.FactoryBeanThatShouldntBeCalled.class.getName(), null, null));
        lbf.preInstantiateSingletons();
    }

    @Test
    public void testPrototypeStringCreatedRepeatedly() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition stringDef = new RootBeanDefinition(String.class);
        stringDef.setScope(SCOPE_PROTOTYPE);
        stringDef.getConstructorArgumentValues().addGenericArgumentValue(new TypedStringValue("value"));
        lbf.registerBeanDefinition("string", stringDef);
        String val1 = lbf.getBean("string", String.class);
        String val2 = lbf.getBean("string", String.class);
        Assert.assertEquals("value", val1);
        Assert.assertEquals("value", val2);
        Assert.assertNotSame(val1, val2);
    }

    @Test
    public void testPrototypeWithArrayConversionForConstructor() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        List<String> list = new org.springframework.beans.factory.support.ManagedList();
        list.add("myName");
        list.add("myBeanName");
        RootBeanDefinition bd = new RootBeanDefinition(DerivedTestBean.class);
        bd.setScope(SCOPE_PROTOTYPE);
        bd.getConstructorArgumentValues().addGenericArgumentValue(list);
        lbf.registerBeanDefinition("test", bd);
        DerivedTestBean tb = ((DerivedTestBean) (lbf.getBean("test")));
        Assert.assertEquals("myName", tb.getName());
        Assert.assertEquals("myBeanName", tb.getBeanName());
        DerivedTestBean tb2 = ((DerivedTestBean) (lbf.getBean("test")));
        Assert.assertTrue((tb != tb2));
        Assert.assertEquals("myName", tb2.getName());
        Assert.assertEquals("myBeanName", tb2.getBeanName());
    }

    @Test
    public void testPrototypeWithArrayConversionForFactoryMethod() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        List<String> list = new org.springframework.beans.factory.support.ManagedList();
        list.add("myName");
        list.add("myBeanName");
        RootBeanDefinition bd = new RootBeanDefinition(DerivedTestBean.class);
        bd.setScope(SCOPE_PROTOTYPE);
        bd.setFactoryMethodName("create");
        bd.getConstructorArgumentValues().addGenericArgumentValue(list);
        lbf.registerBeanDefinition("test", bd);
        DerivedTestBean tb = ((DerivedTestBean) (lbf.getBean("test")));
        Assert.assertEquals("myName", tb.getName());
        Assert.assertEquals("myBeanName", tb.getBeanName());
        DerivedTestBean tb2 = ((DerivedTestBean) (lbf.getBean("test")));
        Assert.assertTrue((tb != tb2));
        Assert.assertEquals("myName", tb2.getName());
        Assert.assertEquals("myBeanName", tb2.getBeanName());
    }

    @Test
    public void testPrototypeCreationIsFastEnough() {
        Assume.group(PERFORMANCE);
        Assume.notLogging(DefaultListableBeanFactoryTests.factoryLog);
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition rbd = new RootBeanDefinition(TestBean.class);
        rbd.setScope(SCOPE_PROTOTYPE);
        lbf.registerBeanDefinition("test", rbd);
        lbf.freezeConfiguration();
        StopWatch sw = new StopWatch();
        sw.start("prototype");
        for (int i = 0; i < 100000; i++) {
            lbf.getBean("test");
        }
        sw.stop();
        // System.out.println(sw.getTotalTimeMillis());
        Assert.assertTrue(("Prototype creation took too long: " + (sw.getTotalTimeMillis())), ((sw.getTotalTimeMillis()) < 3000));
    }

    @Test
    public void testPrototypeCreationWithDependencyCheckIsFastEnough() {
        Assume.group(PERFORMANCE);
        Assume.notLogging(DefaultListableBeanFactoryTests.factoryLog);
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition rbd = new RootBeanDefinition(LifecycleBean.class);
        rbd.setScope(SCOPE_PROTOTYPE);
        rbd.setDependencyCheck(DEPENDENCY_CHECK_OBJECTS);
        lbf.registerBeanDefinition("test", rbd);
        lbf.addBeanPostProcessor(new LifecycleBean.PostProcessor());
        lbf.freezeConfiguration();
        StopWatch sw = new StopWatch();
        sw.start("prototype");
        for (int i = 0; i < 100000; i++) {
            lbf.getBean("test");
        }
        sw.stop();
        // System.out.println(sw.getTotalTimeMillis());
        Assert.assertTrue(("Prototype creation took too long: " + (sw.getTotalTimeMillis())), ((sw.getTotalTimeMillis()) < 3000));
    }

    @Test
    public void testPrototypeCreationWithResolvedConstructorArgumentsIsFastEnough() {
        Assume.group(PERFORMANCE);
        Assume.notLogging(DefaultListableBeanFactoryTests.factoryLog);
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition rbd = new RootBeanDefinition(TestBean.class);
        rbd.setScope(SCOPE_PROTOTYPE);
        rbd.getConstructorArgumentValues().addGenericArgumentValue(new RuntimeBeanReference("spouse"));
        lbf.registerBeanDefinition("test", rbd);
        lbf.registerBeanDefinition("spouse", new RootBeanDefinition(TestBean.class));
        lbf.freezeConfiguration();
        TestBean spouse = ((TestBean) (lbf.getBean("spouse")));
        StopWatch sw = new StopWatch();
        sw.start("prototype");
        for (int i = 0; i < 100000; i++) {
            TestBean tb = ((TestBean) (lbf.getBean("test")));
            Assert.assertSame(spouse, tb.getSpouse());
        }
        sw.stop();
        // System.out.println(sw.getTotalTimeMillis());
        Assert.assertTrue(("Prototype creation took too long: " + (sw.getTotalTimeMillis())), ((sw.getTotalTimeMillis()) < 4000));
    }

    @Test
    public void testPrototypeCreationWithPropertiesIsFastEnough() {
        Assume.group(PERFORMANCE);
        Assume.notLogging(DefaultListableBeanFactoryTests.factoryLog);
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition rbd = new RootBeanDefinition(TestBean.class);
        rbd.setScope(SCOPE_PROTOTYPE);
        rbd.getPropertyValues().add("name", "juergen");
        rbd.getPropertyValues().add("age", "99");
        lbf.registerBeanDefinition("test", rbd);
        lbf.freezeConfiguration();
        StopWatch sw = new StopWatch();
        sw.start("prototype");
        for (int i = 0; i < 100000; i++) {
            TestBean tb = ((TestBean) (lbf.getBean("test")));
            Assert.assertEquals("juergen", tb.getName());
            Assert.assertEquals(99, tb.getAge());
        }
        sw.stop();
        // System.out.println(sw.getTotalTimeMillis());
        Assert.assertTrue(("Prototype creation took too long: " + (sw.getTotalTimeMillis())), ((sw.getTotalTimeMillis()) < 4000));
    }

    @Test
    public void testPrototypeCreationWithResolvedPropertiesIsFastEnough() {
        Assume.group(PERFORMANCE);
        Assume.notLogging(DefaultListableBeanFactoryTests.factoryLog);
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition rbd = new RootBeanDefinition(TestBean.class);
        rbd.setScope(SCOPE_PROTOTYPE);
        rbd.getPropertyValues().add("spouse", new RuntimeBeanReference("spouse"));
        lbf.registerBeanDefinition("test", rbd);
        lbf.registerBeanDefinition("spouse", new RootBeanDefinition(TestBean.class));
        lbf.freezeConfiguration();
        TestBean spouse = ((TestBean) (lbf.getBean("spouse")));
        StopWatch sw = new StopWatch();
        sw.start("prototype");
        for (int i = 0; i < 100000; i++) {
            TestBean tb = ((TestBean) (lbf.getBean("test")));
            Assert.assertSame(spouse, tb.getSpouse());
        }
        sw.stop();
        // System.out.println(sw.getTotalTimeMillis());
        Assert.assertTrue(("Prototype creation took too long: " + (sw.getTotalTimeMillis())), ((sw.getTotalTimeMillis()) < 4000));
    }

    @Test
    public void testSingletonLookupByNameIsFastEnough() {
        Assume.group(PERFORMANCE);
        Assume.notLogging(DefaultListableBeanFactoryTests.factoryLog);
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        lbf.registerBeanDefinition("test", new RootBeanDefinition(TestBean.class));
        lbf.freezeConfiguration();
        StopWatch sw = new StopWatch();
        sw.start("singleton");
        for (int i = 0; i < 1000000; i++) {
            lbf.getBean("test");
        }
        sw.stop();
        // System.out.println(sw.getTotalTimeMillis());
        Assert.assertTrue(("Singleton lookup took too long: " + (sw.getTotalTimeMillis())), ((sw.getTotalTimeMillis()) < 1000));
    }

    @Test
    public void testSingletonLookupByTypeIsFastEnough() {
        Assume.group(PERFORMANCE);
        Assume.notLogging(DefaultListableBeanFactoryTests.factoryLog);
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        lbf.registerBeanDefinition("test", new RootBeanDefinition(TestBean.class));
        lbf.freezeConfiguration();
        StopWatch sw = new StopWatch();
        sw.start("singleton");
        for (int i = 0; i < 1000000; i++) {
            lbf.getBean(TestBean.class);
        }
        sw.stop();
        // System.out.println(sw.getTotalTimeMillis());
        Assert.assertTrue(("Singleton lookup took too long: " + (sw.getTotalTimeMillis())), ((sw.getTotalTimeMillis()) < 1000));
    }

    @Test
    public void testBeanPostProcessorWithWrappedObjectAndDisposableBean() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition bd = new RootBeanDefinition(DefaultListableBeanFactoryTests.BeanWithDisposableBean.class);
        lbf.registerBeanDefinition("test", bd);
        lbf.addBeanPostProcessor(new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) {
                return new TestBean();
            }

            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) {
                return bean;
            }
        });
        DefaultListableBeanFactoryTests.BeanWithDisposableBean.closed = false;
        lbf.preInstantiateSingletons();
        lbf.destroySingletons();
        Assert.assertTrue("Destroy method invoked", DefaultListableBeanFactoryTests.BeanWithDisposableBean.closed);
    }

    @Test
    public void testBeanPostProcessorWithWrappedObjectAndCloseable() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition bd = new RootBeanDefinition(DefaultListableBeanFactoryTests.BeanWithCloseable.class);
        lbf.registerBeanDefinition("test", bd);
        lbf.addBeanPostProcessor(new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) {
                return new TestBean();
            }

            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) {
                return bean;
            }
        });
        DefaultListableBeanFactoryTests.BeanWithDisposableBean.closed = false;
        lbf.preInstantiateSingletons();
        lbf.destroySingletons();
        Assert.assertTrue("Destroy method invoked", DefaultListableBeanFactoryTests.BeanWithCloseable.closed);
    }

    @Test
    public void testBeanPostProcessorWithWrappedObjectAndDestroyMethod() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition bd = new RootBeanDefinition(DefaultListableBeanFactoryTests.BeanWithDestroyMethod.class);
        bd.setDestroyMethodName("close");
        lbf.registerBeanDefinition("test", bd);
        lbf.addBeanPostProcessor(new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) {
                return new TestBean();
            }

            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) {
                return bean;
            }
        });
        DefaultListableBeanFactoryTests.BeanWithDestroyMethod.closeCount = 0;
        lbf.preInstantiateSingletons();
        lbf.destroySingletons();
        Assert.assertEquals("Destroy methods invoked", 1, DefaultListableBeanFactoryTests.BeanWithDestroyMethod.closeCount);
    }

    @Test
    public void testDestroyMethodOnInnerBean() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition innerBd = new RootBeanDefinition(DefaultListableBeanFactoryTests.BeanWithDestroyMethod.class);
        innerBd.setDestroyMethodName("close");
        RootBeanDefinition bd = new RootBeanDefinition(DefaultListableBeanFactoryTests.BeanWithDestroyMethod.class);
        bd.setDestroyMethodName("close");
        bd.getPropertyValues().add("inner", innerBd);
        lbf.registerBeanDefinition("test", bd);
        DefaultListableBeanFactoryTests.BeanWithDestroyMethod.closeCount = 0;
        lbf.preInstantiateSingletons();
        lbf.destroySingletons();
        Assert.assertEquals("Destroy methods invoked", 2, DefaultListableBeanFactoryTests.BeanWithDestroyMethod.closeCount);
    }

    @Test
    public void testDestroyMethodOnInnerBeanAsPrototype() {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition innerBd = new RootBeanDefinition(DefaultListableBeanFactoryTests.BeanWithDestroyMethod.class);
        innerBd.setScope(SCOPE_PROTOTYPE);
        innerBd.setDestroyMethodName("close");
        RootBeanDefinition bd = new RootBeanDefinition(DefaultListableBeanFactoryTests.BeanWithDestroyMethod.class);
        bd.setDestroyMethodName("close");
        bd.getPropertyValues().add("inner", innerBd);
        lbf.registerBeanDefinition("test", bd);
        DefaultListableBeanFactoryTests.BeanWithDestroyMethod.closeCount = 0;
        lbf.preInstantiateSingletons();
        lbf.destroySingletons();
        Assert.assertEquals("Destroy methods invoked", 1, DefaultListableBeanFactoryTests.BeanWithDestroyMethod.closeCount);
    }

    @Test
    public void testFindTypeOfSingletonFactoryMethodOnBeanInstance() {
        findTypeOfPrototypeFactoryMethodOnBeanInstance(true);
    }

    @Test
    public void testFindTypeOfPrototypeFactoryMethodOnBeanInstance() {
        findTypeOfPrototypeFactoryMethodOnBeanInstance(false);
    }

    @Test(expected = IllegalStateException.class)
    public void testScopingBeanToUnregisteredScopeResultsInAnException() {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(TestBean.class);
        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        beanDefinition.setScope("he put himself so low could hardly look me in the face");
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
        factory.registerBeanDefinition("testBean", beanDefinition);
        factory.getBean("testBean");
    }

    @Test
    public void testExplicitScopeInheritanceForChildBeanDefinitions() {
        String theChildScope = "bonanza!";
        RootBeanDefinition parent = new RootBeanDefinition();
        parent.setScope(SCOPE_PROTOTYPE);
        AbstractBeanDefinition child = BeanDefinitionBuilder.childBeanDefinition("parent").getBeanDefinition();
        child.setBeanClass(TestBean.class);
        child.setScope(theChildScope);
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
        factory.registerBeanDefinition("parent", parent);
        factory.registerBeanDefinition("child", child);
        AbstractBeanDefinition def = ((AbstractBeanDefinition) (factory.getBeanDefinition("child")));
        Assert.assertEquals("Child 'scope' not overriding parent scope (it must).", theChildScope, def.getScope());
    }

    @Test
    public void testScopeInheritanceForChildBeanDefinitions() {
        RootBeanDefinition parent = new RootBeanDefinition();
        parent.setScope("bonanza!");
        AbstractBeanDefinition child = new ChildBeanDefinition("parent");
        child.setBeanClass(TestBean.class);
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
        factory.registerBeanDefinition("parent", parent);
        factory.registerBeanDefinition("child", child);
        BeanDefinition def = factory.getMergedBeanDefinition("child");
        Assert.assertEquals("Child 'scope' not inherited", "bonanza!", def.getScope());
    }

    @Test
    public void testFieldSettingWithInstantiationAwarePostProcessorNoShortCircuit() {
        doTestFieldSettingWithInstantiationAwarePostProcessor(false);
    }

    @Test
    public void testFieldSettingWithInstantiationAwarePostProcessorWithShortCircuit() {
        doTestFieldSettingWithInstantiationAwarePostProcessor(true);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testInitSecurityAwarePrototypeBean() {
        final DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        RootBeanDefinition bd = new RootBeanDefinition(DefaultListableBeanFactoryTests.TestSecuredBean.class);
        bd.setScope(ConfigurableBeanFactory.SCOPE_PROTOTYPE);
        bd.setInitMethodName("init");
        lbf.registerBeanDefinition("test", bd);
        final Subject subject = new Subject();
        subject.getPrincipals().add(new DefaultListableBeanFactoryTests.TestPrincipal("user1"));
        DefaultListableBeanFactoryTests.TestSecuredBean bean = ((DefaultListableBeanFactoryTests.TestSecuredBean) (Subject.doAsPrivileged(subject, new PrivilegedAction() {
            @Override
            public Object run() {
                return lbf.getBean("test");
            }
        }, null)));
        Assert.assertNotNull(bean);
        Assert.assertEquals("user1", bean.getUserName());
    }

    @Test
    public void testContainsBeanReturnsTrueEvenForAbstractBeanDefinition() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        bf.registerBeanDefinition("abs", BeanDefinitionBuilder.rootBeanDefinition(TestBean.class).setAbstract(true).getBeanDefinition());
        Assert.assertThat(bf.containsBean("abs"), equalTo(true));
        Assert.assertThat(bf.containsBean("bogus"), equalTo(false));
    }

    @Test
    public void resolveEmbeddedValue() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        StringValueResolver r1 = Mockito.mock(StringValueResolver.class);
        StringValueResolver r2 = Mockito.mock(StringValueResolver.class);
        StringValueResolver r3 = Mockito.mock(StringValueResolver.class);
        bf.addEmbeddedValueResolver(r1);
        bf.addEmbeddedValueResolver(r2);
        bf.addEmbeddedValueResolver(r3);
        BDDMockito.given(r1.resolveStringValue("A")).willReturn("B");
        BDDMockito.given(r2.resolveStringValue("B")).willReturn(null);
        BDDMockito.given(r3.resolveStringValue(ArgumentMatchers.isNull())).willThrow(new IllegalArgumentException());
        bf.resolveEmbeddedValue("A");
        Mockito.verify(r1).resolveStringValue("A");
        Mockito.verify(r2).resolveStringValue("B");
        Mockito.verify(r3, Mockito.never()).resolveStringValue(ArgumentMatchers.isNull());
    }

    @Test
    public void populatedJavaUtilOptionalBean() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        RootBeanDefinition bd = new RootBeanDefinition(Optional.class);
        bd.setFactoryMethodName("of");
        bd.getConstructorArgumentValues().addGenericArgumentValue("CONTENT");
        bf.registerBeanDefinition("optionalBean", bd);
        Assert.assertEquals(Optional.of("CONTENT"), bf.getBean(Optional.class));
    }

    @Test
    public void emptyJavaUtilOptionalBean() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        RootBeanDefinition bd = new RootBeanDefinition(Optional.class);
        bd.setFactoryMethodName("empty");
        bf.registerBeanDefinition("optionalBean", bd);
        Assert.assertSame(Optional.empty(), bf.getBean(Optional.class));
    }

    @Test
    public void testNonPublicEnum() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        RootBeanDefinition bd = new RootBeanDefinition(DefaultListableBeanFactoryTests.NonPublicEnumHolder.class);
        bd.getConstructorArgumentValues().addGenericArgumentValue("VALUE_1");
        bf.registerBeanDefinition("holderBean", bd);
        DefaultListableBeanFactoryTests.NonPublicEnumHolder holder = ((DefaultListableBeanFactoryTests.NonPublicEnumHolder) (bf.getBean("holderBean")));
        Assert.assertEquals(DefaultListableBeanFactoryTests.NonPublicEnum.VALUE_1, holder.getNonPublicEnum());
    }

    /**
     * Test that by-type bean lookup caching is working effectively by searching for a
     * bean of type B 10K times within a container having 1K additional beans of type A.
     * Prior to by-type caching, each bean lookup would traverse the entire container
     * (all 1001 beans), performing expensive assignability checks, etc. Now these
     * operations are necessary only once, providing a dramatic performance improvement.
     * On load-free modern hardware (e.g. an 8-core MPB), this method should complete well
     * under the 1000 ms timeout, usually ~= 300ms. With caching removed and on the same
     * hardware the method will take ~13000 ms. See SPR-6870.
     */
    @Test(timeout = 1000)
    public void testByTypeLookupIsFastEnough() {
        Assume.group(PERFORMANCE);
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        for (int i = 0; i < 1000; i++) {
            bf.registerBeanDefinition(("a" + i), new RootBeanDefinition(DefaultListableBeanFactoryTests.A.class));
        }
        bf.registerBeanDefinition("b", new RootBeanDefinition(DefaultListableBeanFactoryTests.B.class));
        bf.freezeConfiguration();
        for (int i = 0; i < 10000; i++) {
            bf.getBean(DefaultListableBeanFactoryTests.B.class);
        }
    }

    @Test(timeout = 1000)
    public void testRegistrationOfManyBeanDefinitionsIsFastEnough() {
        Assume.group(PERFORMANCE);
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        bf.registerBeanDefinition("b", new RootBeanDefinition(DefaultListableBeanFactoryTests.B.class));
        // bf.getBean("b");
        for (int i = 0; i < 100000; i++) {
            bf.registerBeanDefinition(("a" + i), new RootBeanDefinition(DefaultListableBeanFactoryTests.A.class));
        }
    }

    @Test(timeout = 1000)
    public void testRegistrationOfManySingletonsIsFastEnough() {
        Assume.group(PERFORMANCE);
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        bf.registerBeanDefinition("b", new RootBeanDefinition(DefaultListableBeanFactoryTests.B.class));
        // bf.getBean("b");
        for (int i = 0; i < 100000; i++) {
            bf.registerSingleton(("a" + i), new DefaultListableBeanFactoryTests.A());
        }
    }

    static class A {}

    static class B {}

    public static class NoDependencies {
        private NoDependencies() {
        }
    }

    public static class ConstructorDependency implements BeanNameAware {
        public TestBean spouse;

        public int spouseAge;

        private String beanName;

        public ConstructorDependency(TestBean spouse) {
            this.spouse = spouse;
        }

        public ConstructorDependency(int spouseAge) {
            this.spouseAge = spouseAge;
        }

        @SuppressWarnings("unused")
        private ConstructorDependency(TestBean spouse, TestBean otherSpouse) {
            throw new IllegalArgumentException("Should never be called");
        }

        @Override
        public void setBeanName(String name) {
            this.beanName = name;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            DefaultListableBeanFactoryTests.ConstructorDependency that = ((DefaultListableBeanFactoryTests.ConstructorDependency) (o));
            return (((spouseAge) == (that.spouseAge)) && (Objects.equals(spouse, that.spouse))) && (Objects.equals(beanName, that.beanName));
        }

        @Override
        public int hashCode() {
            return Objects.hash(spouse, spouseAge, beanName);
        }
    }

    public static class UnsatisfiedConstructorDependency {
        public UnsatisfiedConstructorDependency(TestBean t, SideEffectBean b) {
        }
    }

    public static class ConstructorDependencyBean {
        public ConstructorDependencyBean(DefaultListableBeanFactoryTests.ConstructorDependencyBean dependency) {
        }
    }

    public static class ConstructorDependencyFactoryBean implements FactoryBean<Object> {
        public ConstructorDependencyFactoryBean(String dependency) {
        }

        @Override
        public Object getObject() {
            return "test";
        }

        @Override
        public Class<?> getObjectType() {
            return String.class;
        }

        @Override
        public boolean isSingleton() {
            return true;
        }
    }

    public static class ConstructorDependencyWithClassResolution {
        public ConstructorDependencyWithClassResolution(Class<?> clazz) {
        }

        public ConstructorDependencyWithClassResolution() {
        }
    }

    public static class BeanWithDisposableBean implements DisposableBean {
        private static boolean closed;

        @Override
        public void destroy() {
            DefaultListableBeanFactoryTests.BeanWithDisposableBean.closed = true;
        }
    }

    public static class BeanWithCloseable implements Closeable {
        private static boolean closed;

        @Override
        public void close() {
            DefaultListableBeanFactoryTests.BeanWithCloseable.closed = true;
        }
    }

    public abstract static class BaseClassWithDestroyMethod {
        public abstract DefaultListableBeanFactoryTests.BaseClassWithDestroyMethod close();
    }

    public static class BeanWithDestroyMethod extends DefaultListableBeanFactoryTests.BaseClassWithDestroyMethod {
        private static int closeCount = 0;

        private DefaultListableBeanFactoryTests.BeanWithDestroyMethod inner;

        public void setInner(DefaultListableBeanFactoryTests.BeanWithDestroyMethod inner) {
            this.inner = inner;
        }

        @Override
        public DefaultListableBeanFactoryTests.BeanWithDestroyMethod close() {
            (DefaultListableBeanFactoryTests.BeanWithDestroyMethod.closeCount)++;
            return this;
        }
    }

    public static class BeanWithFactoryMethod {
        private String name;

        public void setName(String name) {
            this.name = name;
        }

        public TestBean create() {
            TestBean tb = new TestBean();
            tb.setName(this.name);
            return tb;
        }

        public TestBean createWithArgs(String arg) {
            TestBean tb = new TestBean();
            tb.setName(arg);
            return tb;
        }

        public Object createGeneric() {
            return create();
        }
    }

    public interface Repository<T, ID extends Serializable> {}

    public interface RepositoryFactoryInformation<T, ID extends Serializable> {}

    public abstract static class RepositoryFactoryBeanSupport<T extends DefaultListableBeanFactoryTests.Repository<S, ID>, S, ID extends Serializable> implements DefaultListableBeanFactoryTests.RepositoryFactoryInformation<S, ID> , FactoryBean<T> {}

    public static class FactoryBeanThatShouldntBeCalled<T extends DefaultListableBeanFactoryTests.Repository<S, ID>, S, ID extends Serializable> extends DefaultListableBeanFactoryTests.RepositoryFactoryBeanSupport<T, S, ID> implements Runnable , Callable<T> {
        @Override
        public T getObject() {
            throw new IllegalStateException();
        }

        @Override
        public Class<?> getObjectType() {
            return null;
        }

        @Override
        public boolean isSingleton() {
            return false;
        }

        @Override
        public void run() {
            throw new IllegalStateException();
        }

        @Override
        public T call() {
            throw new IllegalStateException();
        }
    }

    public static class LazyInitFactory implements FactoryBean<Object> {
        public boolean initialized = false;

        @Override
        public Object getObject() {
            this.initialized = true;
            return "";
        }

        @Override
        public Class<?> getObjectType() {
            return String.class;
        }

        @Override
        public boolean isSingleton() {
            return true;
        }
    }

    public static class EagerInitFactory implements SmartFactoryBean<Object> {
        public boolean initialized = false;

        @Override
        public Object getObject() {
            this.initialized = true;
            return "";
        }

        @Override
        public Class<?> getObjectType() {
            return String.class;
        }

        @Override
        public boolean isSingleton() {
            return true;
        }

        @Override
        public boolean isPrototype() {
            return false;
        }

        @Override
        public boolean isEagerInit() {
            return true;
        }
    }

    public static class TestBeanFactory {
        public static boolean initialized = false;

        public TestBeanFactory() {
            DefaultListableBeanFactoryTests.TestBeanFactory.initialized = true;
        }

        public static TestBean createTestBean() {
            return new TestBean();
        }

        public TestBean createTestBeanNonStatic() {
            return new TestBean();
        }
    }

    public static class ArrayBean {
        private Integer[] integerArray;

        private Resource[] resourceArray;

        public ArrayBean() {
        }

        public ArrayBean(Integer[] integerArray) {
            this.integerArray = integerArray;
        }

        public ArrayBean(Integer[] integerArray, Resource[] resourceArray) {
            this.integerArray = integerArray;
            this.resourceArray = resourceArray;
        }

        public Integer[] getIntegerArray() {
            return this.integerArray;
        }

        public void setResourceArray(Resource[] resourceArray) {
            this.resourceArray = resourceArray;
        }

        public Resource[] getResourceArray() {
            return this.resourceArray;
        }
    }

    /**
     * Bean with a dependency on a {@link FactoryBean}.
     */
    @SuppressWarnings("unused")
    private static class FactoryBeanDependentBean {
        private FactoryBean<?> factoryBean;

        public final FactoryBean<?> getFactoryBean() {
            return this.factoryBean;
        }

        public final void setFactoryBean(final FactoryBean<?> factoryBean) {
            this.factoryBean = factoryBean;
        }
    }

    private static class CustomTypeConverter implements TypeConverter {
        private final NumberFormat numberFormat;

        public CustomTypeConverter(NumberFormat numberFormat) {
            this.numberFormat = numberFormat;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Object convertIfNecessary(Object value, @Nullable
        Class requiredType) {
            if ((value instanceof String) && (Float.class.isAssignableFrom(requiredType))) {
                try {
                    return new Float(this.numberFormat.parse(((String) (value))).floatValue());
                } catch (ParseException ex) {
                    throw new TypeMismatchException(value, requiredType, ex);
                }
            } else
                if ((value instanceof String) && (int.class.isAssignableFrom(requiredType))) {
                    return new Integer(5);
                } else {
                    return value;
                }

        }

        @Override
        @SuppressWarnings("unchecked")
        public Object convertIfNecessary(Object value, @Nullable
        Class requiredType, @Nullable
        MethodParameter methodParam) {
            return convertIfNecessary(value, requiredType);
        }

        @Override
        @SuppressWarnings("unchecked")
        public Object convertIfNecessary(Object value, @Nullable
        Class requiredType, @Nullable
        Field field) {
            return convertIfNecessary(value, requiredType);
        }
    }

    private static class TestPrincipal implements Principal {
        private String name;

        public TestPrincipal(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == (this)) {
                return true;
            }
            if (!(obj instanceof DefaultListableBeanFactoryTests.TestPrincipal)) {
                return false;
            }
            DefaultListableBeanFactoryTests.TestPrincipal p = ((DefaultListableBeanFactoryTests.TestPrincipal) (obj));
            return this.name.equals(p.name);
        }

        @Override
        public int hashCode() {
            return this.name.hashCode();
        }
    }

    @SuppressWarnings("unused")
    private static class TestSecuredBean {
        private String userName;

        public void init() {
            AccessControlContext acc = AccessController.getContext();
            Subject subject = Subject.getSubject(acc);
            if (subject == null) {
                return;
            }
            setNameFromPrincipal(subject.getPrincipals());
        }

        private void setNameFromPrincipal(Set<Principal> principals) {
            if (principals == null) {
                return;
            }
            for (Iterator<Principal> it = principals.iterator(); it.hasNext();) {
                Principal p = it.next();
                this.userName = p.getName();
                return;
            }
        }

        public String getUserName() {
            return this.userName;
        }
    }

    @SuppressWarnings("unused")
    private static class KnowsIfInstantiated {
        private static boolean instantiated;

        public static void clearInstantiationRecord() {
            DefaultListableBeanFactoryTests.KnowsIfInstantiated.instantiated = false;
        }

        public static boolean wasInstantiated() {
            return DefaultListableBeanFactoryTests.KnowsIfInstantiated.instantiated;
        }

        public KnowsIfInstantiated() {
            DefaultListableBeanFactoryTests.KnowsIfInstantiated.instantiated = true;
        }
    }

    @Priority(5)
    private static class HighPriorityTestBean extends TestBean {}

    @Priority(500)
    private static class LowPriorityTestBean extends TestBean {}

    private static class NullTestBeanFactoryBean<T> implements FactoryBean<TestBean> {
        @Override
        public TestBean getObject() {
            return null;
        }

        @Override
        public Class<?> getObjectType() {
            return TestBean.class;
        }

        @Override
        public boolean isSingleton() {
            return true;
        }
    }

    private static class TestBeanRecipient {
        public TestBean testBean;

        public TestBeanRecipient(TestBean testBean) {
            this.testBean = testBean;
        }
    }

    enum NonPublicEnum {

        VALUE_1,
        VALUE_2;}

    static class NonPublicEnumHolder {
        final DefaultListableBeanFactoryTests.NonPublicEnum nonPublicEnum;

        public NonPublicEnumHolder(DefaultListableBeanFactoryTests.NonPublicEnum nonPublicEnum) {
            this.nonPublicEnum = nonPublicEnum;
        }

        public DefaultListableBeanFactoryTests.NonPublicEnum getNonPublicEnum() {
            return nonPublicEnum;
        }
    }
}

