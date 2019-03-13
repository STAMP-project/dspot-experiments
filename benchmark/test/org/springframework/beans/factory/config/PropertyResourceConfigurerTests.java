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
package org.springframework.beans.factory.config;


import PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_NEVER;
import PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_OVERRIDE;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.io.Resource;
import org.springframework.tests.sample.beans.IndexedTestBean;
import org.springframework.tests.sample.beans.TestBean;
import org.springframework.util.StringUtils;


/**
 * Unit tests for various {@link PropertyResourceConfigurer} implementations including:
 * {@link PropertyPlaceholderConfigurer}, {@link PropertyOverrideConfigurer} and
 * {@link PreferencesPlaceholderConfigurer}.
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @author Phillip Webb
 * @since 02.10.2003
 * @see PropertyPlaceholderConfigurerTests
 */
@SuppressWarnings("deprecation")
public class PropertyResourceConfigurerTests {
    static {
        System.setProperty("java.util.prefs.PreferencesFactory", PropertyResourceConfigurerTests.MockPreferencesFactory.class.getName());
    }

    private static final Class<?> CLASS = PropertyResourceConfigurerTests.class;

    private static final Resource TEST_PROPS = qualifiedResource(PropertyResourceConfigurerTests.CLASS, "test.properties");

    private static final Resource XTEST_PROPS = qualifiedResource(PropertyResourceConfigurerTests.CLASS, "xtest.properties");// does not exist


    private static final Resource TEST_PROPS_XML = qualifiedResource(PropertyResourceConfigurerTests.CLASS, "test.properties.xml");

    private final DefaultListableBeanFactory factory = new DefaultListableBeanFactory();

    @Test
    public void testPropertyOverrideConfigurer() {
        BeanDefinition def1 = BeanDefinitionBuilder.genericBeanDefinition(TestBean.class).getBeanDefinition();
        factory.registerBeanDefinition("tb1", def1);
        BeanDefinition def2 = BeanDefinitionBuilder.genericBeanDefinition(TestBean.class).getBeanDefinition();
        factory.registerBeanDefinition("tb2", def2);
        PropertyOverrideConfigurer poc1;
        PropertyOverrideConfigurer poc2;
        {
            poc1 = new PropertyOverrideConfigurer();
            Properties props = new Properties();
            props.setProperty("tb1.age", "99");
            props.setProperty("tb2.name", "test");
            poc1.setProperties(props);
        }
        {
            poc2 = new PropertyOverrideConfigurer();
            Properties props = new Properties();
            props.setProperty("tb2.age", "99");
            props.setProperty("tb2.name", "test2");
            poc2.setProperties(props);
        }
        // emulate what happens when BFPPs are added to an application context: It's LIFO-based
        poc2.postProcessBeanFactory(factory);
        poc1.postProcessBeanFactory(factory);
        TestBean tb1 = ((TestBean) (factory.getBean("tb1")));
        TestBean tb2 = ((TestBean) (factory.getBean("tb2")));
        Assert.assertEquals(99, tb1.getAge());
        Assert.assertEquals(99, tb2.getAge());
        Assert.assertEquals(null, tb1.getName());
        Assert.assertEquals("test", tb2.getName());
    }

    @Test
    public void testPropertyOverrideConfigurerWithNestedProperty() {
        BeanDefinition def = BeanDefinitionBuilder.genericBeanDefinition(IndexedTestBean.class).getBeanDefinition();
        factory.registerBeanDefinition("tb", def);
        PropertyOverrideConfigurer poc;
        poc = new PropertyOverrideConfigurer();
        Properties props = new Properties();
        props.setProperty("tb.array[0].age", "99");
        props.setProperty("tb.list[1].name", "test");
        poc.setProperties(props);
        poc.postProcessBeanFactory(factory);
        IndexedTestBean tb = ((IndexedTestBean) (factory.getBean("tb")));
        Assert.assertEquals(99, tb.getArray()[0].getAge());
        Assert.assertEquals("test", ((TestBean) (tb.getList().get(1))).getName());
    }

    @Test
    public void testPropertyOverrideConfigurerWithNestedPropertyAndDotInBeanName() {
        BeanDefinition def = BeanDefinitionBuilder.genericBeanDefinition(IndexedTestBean.class).getBeanDefinition();
        factory.registerBeanDefinition("my.tb", def);
        PropertyOverrideConfigurer poc;
        poc = new PropertyOverrideConfigurer();
        Properties props = new Properties();
        props.setProperty("my.tb_array[0].age", "99");
        props.setProperty("my.tb_list[1].name", "test");
        poc.setProperties(props);
        poc.setBeanNameSeparator("_");
        poc.postProcessBeanFactory(factory);
        IndexedTestBean tb = ((IndexedTestBean) (factory.getBean("my.tb")));
        Assert.assertEquals(99, tb.getArray()[0].getAge());
        Assert.assertEquals("test", ((TestBean) (tb.getList().get(1))).getName());
    }

    @Test
    public void testPropertyOverrideConfigurerWithNestedMapPropertyAndDotInMapKey() {
        BeanDefinition def = BeanDefinitionBuilder.genericBeanDefinition(IndexedTestBean.class).getBeanDefinition();
        factory.registerBeanDefinition("tb", def);
        PropertyOverrideConfigurer poc;
        poc = new PropertyOverrideConfigurer();
        Properties props = new Properties();
        props.setProperty("tb.map[key1]", "99");
        props.setProperty("tb.map[key2.ext]", "test");
        poc.setProperties(props);
        poc.postProcessBeanFactory(factory);
        IndexedTestBean tb = ((IndexedTestBean) (factory.getBean("tb")));
        Assert.assertEquals("99", tb.getMap().get("key1"));
        Assert.assertEquals("test", tb.getMap().get("key2.ext"));
    }

    @Test
    public void testPropertyOverrideConfigurerWithHeldProperties() {
        BeanDefinition def = BeanDefinitionBuilder.genericBeanDefinition(PropertyResourceConfigurerTests.PropertiesHolder.class).getBeanDefinition();
        factory.registerBeanDefinition("tb", def);
        PropertyOverrideConfigurer poc;
        poc = new PropertyOverrideConfigurer();
        Properties props = new Properties();
        props.setProperty("tb.heldProperties[mail.smtp.auth]", "true");
        poc.setProperties(props);
        poc.postProcessBeanFactory(factory);
        PropertyResourceConfigurerTests.PropertiesHolder tb = ((PropertyResourceConfigurerTests.PropertiesHolder) (factory.getBean("tb")));
        Assert.assertEquals("true", tb.getHeldProperties().getProperty("mail.smtp.auth"));
    }

    @Test
    public void testPropertyOverrideConfigurerWithPropertiesFile() {
        BeanDefinition def = BeanDefinitionBuilder.genericBeanDefinition(IndexedTestBean.class).getBeanDefinition();
        factory.registerBeanDefinition("tb", def);
        PropertyOverrideConfigurer poc = new PropertyOverrideConfigurer();
        poc.setLocation(PropertyResourceConfigurerTests.TEST_PROPS);
        poc.postProcessBeanFactory(factory);
        IndexedTestBean tb = ((IndexedTestBean) (factory.getBean("tb")));
        Assert.assertEquals(99, tb.getArray()[0].getAge());
        Assert.assertEquals("test", ((TestBean) (tb.getList().get(1))).getName());
    }

    @Test
    public void testPropertyOverrideConfigurerWithInvalidPropertiesFile() {
        BeanDefinition def = BeanDefinitionBuilder.genericBeanDefinition(IndexedTestBean.class).getBeanDefinition();
        factory.registerBeanDefinition("tb", def);
        PropertyOverrideConfigurer poc = new PropertyOverrideConfigurer();
        poc.setLocations(PropertyResourceConfigurerTests.TEST_PROPS, PropertyResourceConfigurerTests.XTEST_PROPS);
        poc.setIgnoreResourceNotFound(true);
        poc.postProcessBeanFactory(factory);
        IndexedTestBean tb = ((IndexedTestBean) (factory.getBean("tb")));
        Assert.assertEquals(99, tb.getArray()[0].getAge());
        Assert.assertEquals("test", ((TestBean) (tb.getList().get(1))).getName());
    }

    @Test
    public void testPropertyOverrideConfigurerWithPropertiesXmlFile() {
        BeanDefinition def = BeanDefinitionBuilder.genericBeanDefinition(IndexedTestBean.class).getBeanDefinition();
        factory.registerBeanDefinition("tb", def);
        PropertyOverrideConfigurer poc = new PropertyOverrideConfigurer();
        poc.setLocation(PropertyResourceConfigurerTests.TEST_PROPS_XML);
        poc.postProcessBeanFactory(factory);
        IndexedTestBean tb = ((IndexedTestBean) (factory.getBean("tb")));
        Assert.assertEquals(99, tb.getArray()[0].getAge());
        Assert.assertEquals("test", ((TestBean) (tb.getList().get(1))).getName());
    }

    @Test
    public void testPropertyOverrideConfigurerWithConvertProperties() {
        BeanDefinition def = BeanDefinitionBuilder.genericBeanDefinition(IndexedTestBean.class).getBeanDefinition();
        factory.registerBeanDefinition("tb", def);
        PropertyResourceConfigurerTests.ConvertingOverrideConfigurer bfpp = new PropertyResourceConfigurerTests.ConvertingOverrideConfigurer();
        Properties props = new Properties();
        props.setProperty("tb.array[0].name", "99");
        props.setProperty("tb.list[1].name", "test");
        setProperties(props);
        bfpp.postProcessBeanFactory(factory);
        IndexedTestBean tb = ((IndexedTestBean) (factory.getBean("tb")));
        Assert.assertEquals("X99", tb.getArray()[0].getName());
        Assert.assertEquals("Xtest", ((TestBean) (tb.getList().get(1))).getName());
    }

    @Test
    public void testPropertyOverrideConfigurerWithInvalidKey() {
        factory.registerBeanDefinition("tb1", genericBeanDefinition(TestBean.class).getBeanDefinition());
        factory.registerBeanDefinition("tb2", genericBeanDefinition(TestBean.class).getBeanDefinition());
        {
            PropertyOverrideConfigurer poc = new PropertyOverrideConfigurer();
            poc.setIgnoreInvalidKeys(true);
            Properties props = new Properties();
            props.setProperty("argh", "hgra");
            props.setProperty("tb2.name", "test");
            props.setProperty("tb2.nam", "test");
            props.setProperty("tb3.name", "test");
            poc.setProperties(props);
            poc.postProcessBeanFactory(factory);
            Assert.assertEquals("test", factory.getBean("tb2", TestBean.class).getName());
        }
        {
            PropertyOverrideConfigurer poc = new PropertyOverrideConfigurer();
            Properties props = new Properties();
            props.setProperty("argh", "hgra");
            props.setProperty("tb2.age", "99");
            props.setProperty("tb2.name", "test2");
            poc.setProperties(props);
            poc.setOrder(0);// won't actually do anything since we're not processing through an app ctx

            try {
                poc.postProcessBeanFactory(factory);
            } catch (BeanInitializationException ex) {
                // prove that the processor chokes on the invalid key
                Assert.assertTrue(ex.getMessage().toLowerCase().contains("argh"));
            }
        }
    }

    @Test
    public void testPropertyOverrideConfigurerWithIgnoreInvalidKeys() {
        factory.registerBeanDefinition("tb1", genericBeanDefinition(TestBean.class).getBeanDefinition());
        factory.registerBeanDefinition("tb2", genericBeanDefinition(TestBean.class).getBeanDefinition());
        {
            PropertyOverrideConfigurer poc = new PropertyOverrideConfigurer();
            Properties props = new Properties();
            props.setProperty("tb2.age", "99");
            props.setProperty("tb2.name", "test2");
            poc.setProperties(props);
            poc.setOrder(0);// won't actually do anything since we're not processing through an app ctx

            poc.postProcessBeanFactory(factory);
        }
        {
            PropertyOverrideConfigurer poc = new PropertyOverrideConfigurer();
            poc.setIgnoreInvalidKeys(true);
            Properties props = new Properties();
            props.setProperty("argh", "hgra");
            props.setProperty("tb1.age", "99");
            props.setProperty("tb2.name", "test");
            poc.setProperties(props);
            poc.postProcessBeanFactory(factory);
        }
        TestBean tb1 = ((TestBean) (factory.getBean("tb1")));
        TestBean tb2 = ((TestBean) (factory.getBean("tb2")));
        Assert.assertEquals(99, tb1.getAge());
        Assert.assertEquals(99, tb2.getAge());
        Assert.assertEquals(null, tb1.getName());
        Assert.assertEquals("test", tb2.getName());
    }

    @Test
    public void testPropertyPlaceholderConfigurer() {
        doTestPropertyPlaceholderConfigurer(false);
    }

    @Test
    public void testPropertyPlaceholderConfigurerWithParentChildSeparation() {
        doTestPropertyPlaceholderConfigurer(true);
    }

    @Test
    public void testPropertyPlaceholderConfigurerWithSystemPropertyFallback() {
        factory.registerBeanDefinition("tb", genericBeanDefinition(TestBean.class).addPropertyValue("country", "${os.name}").getBeanDefinition());
        PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
        ppc.postProcessBeanFactory(factory);
        TestBean tb = ((TestBean) (factory.getBean("tb")));
        Assert.assertEquals(System.getProperty("os.name"), tb.getCountry());
    }

    @Test
    public void testPropertyPlaceholderConfigurerWithSystemPropertyNotUsed() {
        factory.registerBeanDefinition("tb", genericBeanDefinition(TestBean.class).addPropertyValue("country", "${os.name}").getBeanDefinition());
        PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
        Properties props = new Properties();
        props.setProperty("os.name", "myos");
        ppc.setProperties(props);
        ppc.postProcessBeanFactory(factory);
        TestBean tb = ((TestBean) (factory.getBean("tb")));
        Assert.assertEquals("myos", tb.getCountry());
    }

    @Test
    public void testPropertyPlaceholderConfigurerWithOverridingSystemProperty() {
        factory.registerBeanDefinition("tb", genericBeanDefinition(TestBean.class).addPropertyValue("country", "${os.name}").getBeanDefinition());
        PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
        Properties props = new Properties();
        props.put("os.name", "myos");
        ppc.setProperties(props);
        ppc.setSystemPropertiesMode(SYSTEM_PROPERTIES_MODE_OVERRIDE);
        ppc.postProcessBeanFactory(factory);
        TestBean tb = ((TestBean) (factory.getBean("tb")));
        Assert.assertEquals(System.getProperty("os.name"), tb.getCountry());
    }

    @Test
    public void testPropertyPlaceholderConfigurerWithUnresolvableSystemProperty() {
        factory.registerBeanDefinition("tb", genericBeanDefinition(TestBean.class).addPropertyValue("touchy", "${user.dir}").getBeanDefinition());
        PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
        ppc.setSystemPropertiesMode(SYSTEM_PROPERTIES_MODE_NEVER);
        try {
            ppc.postProcessBeanFactory(factory);
            Assert.fail("Should have thrown BeanDefinitionStoreException");
        } catch (BeanDefinitionStoreException ex) {
            // expected
            Assert.assertTrue(ex.getMessage().contains("user.dir"));
        }
    }

    @Test
    public void testPropertyPlaceholderConfigurerWithUnresolvablePlaceholder() {
        factory.registerBeanDefinition("tb", genericBeanDefinition(TestBean.class).addPropertyValue("name", "${ref}").getBeanDefinition());
        PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
        try {
            ppc.postProcessBeanFactory(factory);
            Assert.fail("Should have thrown BeanDefinitionStoreException");
        } catch (BeanDefinitionStoreException ex) {
            // expected
            Assert.assertTrue(ex.getMessage().contains("ref"));
        }
    }

    @Test
    public void testPropertyPlaceholderConfigurerWithIgnoreUnresolvablePlaceholder() {
        factory.registerBeanDefinition("tb", genericBeanDefinition(TestBean.class).addPropertyValue("name", "${ref}").getBeanDefinition());
        PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
        ppc.setIgnoreUnresolvablePlaceholders(true);
        ppc.postProcessBeanFactory(factory);
        TestBean tb = ((TestBean) (factory.getBean("tb")));
        Assert.assertEquals("${ref}", tb.getName());
    }

    @Test
    public void testPropertyPlaceholderConfigurerWithEmptyStringAsNull() {
        factory.registerBeanDefinition("tb", genericBeanDefinition(TestBean.class).addPropertyValue("name", "").getBeanDefinition());
        PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
        ppc.setNullValue("");
        ppc.postProcessBeanFactory(factory);
        TestBean tb = ((TestBean) (factory.getBean("tb")));
        Assert.assertNull(tb.getName());
    }

    @Test
    public void testPropertyPlaceholderConfigurerWithEmptyStringInPlaceholderAsNull() {
        factory.registerBeanDefinition("tb", genericBeanDefinition(TestBean.class).addPropertyValue("name", "${ref}").getBeanDefinition());
        PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
        ppc.setNullValue("");
        Properties props = new Properties();
        props.put("ref", "");
        ppc.setProperties(props);
        ppc.postProcessBeanFactory(factory);
        TestBean tb = ((TestBean) (factory.getBean("tb")));
        Assert.assertNull(tb.getName());
    }

    @Test
    public void testPropertyPlaceholderConfigurerWithNestedPlaceholderInKey() {
        factory.registerBeanDefinition("tb", genericBeanDefinition(TestBean.class).addPropertyValue("name", "${my${key}key}").getBeanDefinition());
        PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
        Properties props = new Properties();
        props.put("key", "new");
        props.put("mynewkey", "myname");
        ppc.setProperties(props);
        ppc.postProcessBeanFactory(factory);
        TestBean tb = ((TestBean) (factory.getBean("tb")));
        Assert.assertEquals("myname", tb.getName());
    }

    @Test
    public void testPropertyPlaceholderConfigurerWithPlaceholderInAlias() {
        factory.registerBeanDefinition("tb", genericBeanDefinition(TestBean.class).getBeanDefinition());
        factory.registerAlias("tb", "${alias}");
        PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
        Properties props = new Properties();
        props.put("alias", "tb2");
        ppc.setProperties(props);
        ppc.postProcessBeanFactory(factory);
        TestBean tb = ((TestBean) (factory.getBean("tb")));
        TestBean tb2 = ((TestBean) (factory.getBean("tb2")));
        Assert.assertSame(tb, tb2);
    }

    @Test
    public void testPropertyPlaceholderConfigurerWithSelfReferencingPlaceholderInAlias() {
        factory.registerBeanDefinition("tb", genericBeanDefinition(TestBean.class).getBeanDefinition());
        factory.registerAlias("tb", "${alias}");
        PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
        Properties props = new Properties();
        props.put("alias", "tb");
        ppc.setProperties(props);
        ppc.postProcessBeanFactory(factory);
        TestBean tb = ((TestBean) (factory.getBean("tb")));
        Assert.assertNotNull(tb);
        Assert.assertEquals(0, factory.getAliases("tb").length);
    }

    @Test
    public void testPropertyPlaceholderConfigurerWithCircularReference() {
        factory.registerBeanDefinition("tb", genericBeanDefinition(TestBean.class).addPropertyValue("age", "${age}").addPropertyValue("name", "name${var}").getBeanDefinition());
        PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
        Properties props = new Properties();
        props.setProperty("age", "99");
        props.setProperty("var", "${m}");
        props.setProperty("m", "${var}");
        ppc.setProperties(props);
        try {
            ppc.postProcessBeanFactory(factory);
            Assert.fail("Should have thrown BeanDefinitionStoreException");
        } catch (BeanDefinitionStoreException ex) {
            // expected
        }
    }

    @Test
    public void testPropertyPlaceholderConfigurerWithDefaultProperties() {
        factory.registerBeanDefinition("tb", genericBeanDefinition(TestBean.class).addPropertyValue("touchy", "${test}").getBeanDefinition());
        PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
        Properties props = new Properties();
        props.put("test", "mytest");
        ppc.setProperties(props);
        ppc.postProcessBeanFactory(factory);
        TestBean tb = ((TestBean) (factory.getBean("tb")));
        Assert.assertEquals("mytest", tb.getTouchy());
    }

    @Test
    public void testPropertyPlaceholderConfigurerWithInlineDefault() {
        factory.registerBeanDefinition("tb", genericBeanDefinition(TestBean.class).addPropertyValue("touchy", "${test:mytest}").getBeanDefinition());
        PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
        ppc.postProcessBeanFactory(factory);
        TestBean tb = ((TestBean) (factory.getBean("tb")));
        Assert.assertEquals("mytest", tb.getTouchy());
    }

    @Test
    public void testPropertyPlaceholderConfigurerWithAliases() {
        factory.registerBeanDefinition("tb", genericBeanDefinition(TestBean.class).addPropertyValue("touchy", "${test}").getBeanDefinition());
        factory.registerAlias("tb", "${myAlias}");
        factory.registerAlias("${myTarget}", "alias2");
        PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
        Properties props = new Properties();
        props.put("test", "mytest");
        props.put("myAlias", "alias");
        props.put("myTarget", "tb");
        ppc.setProperties(props);
        ppc.postProcessBeanFactory(factory);
        TestBean tb = ((TestBean) (factory.getBean("tb")));
        Assert.assertEquals("mytest", tb.getTouchy());
        tb = ((TestBean) (factory.getBean("alias")));
        Assert.assertEquals("mytest", tb.getTouchy());
        tb = ((TestBean) (factory.getBean("alias2")));
        Assert.assertEquals("mytest", tb.getTouchy());
    }

    @Test
    public void testPreferencesPlaceholderConfigurer() {
        factory.registerBeanDefinition("tb", genericBeanDefinition(TestBean.class).addPropertyValue("name", "${myName}").addPropertyValue("age", "${myAge}").addPropertyValue("touchy", "${myTouchy}").getBeanDefinition());
        PreferencesPlaceholderConfigurer ppc = new PreferencesPlaceholderConfigurer();
        Properties props = new Properties();
        props.put("myAge", "99");
        ppc.setProperties(props);
        Preferences.systemRoot().put("myName", "myNameValue");
        Preferences.systemRoot().put("myTouchy", "myTouchyValue");
        Preferences.userRoot().put("myTouchy", "myOtherTouchyValue");
        ppc.afterPropertiesSet();
        ppc.postProcessBeanFactory(factory);
        TestBean tb = ((TestBean) (factory.getBean("tb")));
        Assert.assertEquals("myNameValue", tb.getName());
        Assert.assertEquals(99, tb.getAge());
        Assert.assertEquals("myOtherTouchyValue", tb.getTouchy());
        Preferences.userRoot().remove("myTouchy");
        Preferences.systemRoot().remove("myTouchy");
        Preferences.systemRoot().remove("myName");
    }

    @Test
    public void testPreferencesPlaceholderConfigurerWithCustomTreePaths() {
        factory.registerBeanDefinition("tb", genericBeanDefinition(TestBean.class).addPropertyValue("name", "${myName}").addPropertyValue("age", "${myAge}").addPropertyValue("touchy", "${myTouchy}").getBeanDefinition());
        PreferencesPlaceholderConfigurer ppc = new PreferencesPlaceholderConfigurer();
        Properties props = new Properties();
        props.put("myAge", "99");
        ppc.setProperties(props);
        ppc.setSystemTreePath("mySystemPath");
        ppc.setUserTreePath("myUserPath");
        Preferences.systemRoot().node("mySystemPath").put("myName", "myNameValue");
        Preferences.systemRoot().node("mySystemPath").put("myTouchy", "myTouchyValue");
        Preferences.userRoot().node("myUserPath").put("myTouchy", "myOtherTouchyValue");
        ppc.afterPropertiesSet();
        ppc.postProcessBeanFactory(factory);
        TestBean tb = ((TestBean) (factory.getBean("tb")));
        Assert.assertEquals("myNameValue", tb.getName());
        Assert.assertEquals(99, tb.getAge());
        Assert.assertEquals("myOtherTouchyValue", tb.getTouchy());
        Preferences.userRoot().node("myUserPath").remove("myTouchy");
        Preferences.systemRoot().node("mySystemPath").remove("myTouchy");
        Preferences.systemRoot().node("mySystemPath").remove("myName");
    }

    @Test
    public void testPreferencesPlaceholderConfigurerWithPathInPlaceholder() {
        factory.registerBeanDefinition("tb", genericBeanDefinition(TestBean.class).addPropertyValue("name", "${mypath/myName}").addPropertyValue("age", "${myAge}").addPropertyValue("touchy", "${myotherpath/myTouchy}").getBeanDefinition());
        PreferencesPlaceholderConfigurer ppc = new PreferencesPlaceholderConfigurer();
        Properties props = new Properties();
        props.put("myAge", "99");
        ppc.setProperties(props);
        ppc.setSystemTreePath("mySystemPath");
        ppc.setUserTreePath("myUserPath");
        Preferences.systemRoot().node("mySystemPath").node("mypath").put("myName", "myNameValue");
        Preferences.systemRoot().node("mySystemPath/myotherpath").put("myTouchy", "myTouchyValue");
        Preferences.userRoot().node("myUserPath/myotherpath").put("myTouchy", "myOtherTouchyValue");
        ppc.afterPropertiesSet();
        ppc.postProcessBeanFactory(factory);
        TestBean tb = ((TestBean) (factory.getBean("tb")));
        Assert.assertEquals("myNameValue", tb.getName());
        Assert.assertEquals(99, tb.getAge());
        Assert.assertEquals("myOtherTouchyValue", tb.getTouchy());
        Preferences.userRoot().node("myUserPath/myotherpath").remove("myTouchy");
        Preferences.systemRoot().node("mySystemPath/myotherpath").remove("myTouchy");
        Preferences.systemRoot().node("mySystemPath/mypath").remove("myName");
    }

    static class PropertiesHolder {
        private Properties props = new Properties();

        public Properties getHeldProperties() {
            return props;
        }

        public void setHeldProperties(Properties props) {
            this.props = props;
        }
    }

    private static class ConvertingOverrideConfigurer extends PropertyOverrideConfigurer {
        @Override
        protected String convertPropertyValue(String originalValue) {
            return "X" + originalValue;
        }
    }

    /**
     * {@link PreferencesFactory} to create {@link MockPreferences}.
     */
    public static class MockPreferencesFactory implements PreferencesFactory {
        private final Preferences userRoot = new PropertyResourceConfigurerTests.MockPreferences();

        private final Preferences systemRoot = new PropertyResourceConfigurerTests.MockPreferences();

        @Override
        public Preferences systemRoot() {
            return this.systemRoot;
        }

        @Override
        public Preferences userRoot() {
            return this.userRoot;
        }
    }

    /**
     * Mock implementation of {@link Preferences} that behaves the same regardless of the
     * underlying operating system and will never throw security exceptions.
     */
    public static class MockPreferences extends AbstractPreferences {
        private static Map<String, String> values = new HashMap<>();

        private static Map<String, AbstractPreferences> children = new HashMap<>();

        public MockPreferences() {
            super(null, "");
        }

        protected MockPreferences(AbstractPreferences parent, String name) {
            super(parent, name);
        }

        @Override
        protected void putSpi(String key, String value) {
            PropertyResourceConfigurerTests.MockPreferences.values.put(key, value);
        }

        @Override
        protected String getSpi(String key) {
            return PropertyResourceConfigurerTests.MockPreferences.values.get(key);
        }

        @Override
        protected void removeSpi(String key) {
            PropertyResourceConfigurerTests.MockPreferences.values.remove(key);
        }

        @Override
        protected void removeNodeSpi() throws BackingStoreException {
        }

        @Override
        protected String[] keysSpi() throws BackingStoreException {
            return StringUtils.toStringArray(PropertyResourceConfigurerTests.MockPreferences.values.keySet());
        }

        @Override
        protected String[] childrenNamesSpi() throws BackingStoreException {
            return StringUtils.toStringArray(PropertyResourceConfigurerTests.MockPreferences.children.keySet());
        }

        @Override
        protected AbstractPreferences childSpi(String name) {
            AbstractPreferences child = PropertyResourceConfigurerTests.MockPreferences.children.get(name);
            if (child == null) {
                child = new PropertyResourceConfigurerTests.MockPreferences(this, name);
                PropertyResourceConfigurerTests.MockPreferences.children.put(name, child);
            }
            return child;
        }

        @Override
        protected void syncSpi() throws BackingStoreException {
        }

        @Override
        protected void flushSpi() throws BackingStoreException {
        }
    }
}

