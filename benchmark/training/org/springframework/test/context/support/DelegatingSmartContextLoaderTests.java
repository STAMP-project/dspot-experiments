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
package org.springframework.test.context.support;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextLoader;
import org.springframework.test.context.MergedContextConfiguration;


/**
 * Unit tests for {@link DelegatingSmartContextLoader}.
 *
 * @author Sam Brannen
 * @since 3.1
 */
public class DelegatingSmartContextLoaderTests {
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    private static final Class<?>[] EMPTY_CLASS_ARRAY = new Class<?>[0];

    private final DelegatingSmartContextLoader loader = new DelegatingSmartContextLoader();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    // --- SmartContextLoader - processContextConfiguration() ------------------
    @Test
    public void processContextConfigurationWithDefaultXmlConfigGeneration() {
        ContextConfigurationAttributes configAttributes = new ContextConfigurationAttributes(DelegatingSmartContextLoaderTests.XmlTestCase.class, DelegatingSmartContextLoaderTests.EMPTY_STRING_ARRAY, DelegatingSmartContextLoaderTests.EMPTY_CLASS_ARRAY, true, null, true, ContextLoader.class);
        loader.processContextConfiguration(configAttributes);
        Assert.assertEquals(1, configAttributes.getLocations().length);
        DelegatingSmartContextLoaderTests.assertEmpty(configAttributes.getClasses());
    }

    @Test
    public void processContextConfigurationWithDefaultConfigurationClassGeneration() {
        ContextConfigurationAttributes configAttributes = new ContextConfigurationAttributes(DelegatingSmartContextLoaderTests.ConfigClassTestCase.class, DelegatingSmartContextLoaderTests.EMPTY_STRING_ARRAY, DelegatingSmartContextLoaderTests.EMPTY_CLASS_ARRAY, true, null, true, ContextLoader.class);
        loader.processContextConfiguration(configAttributes);
        Assert.assertEquals(1, configAttributes.getClasses().length);
        DelegatingSmartContextLoaderTests.assertEmpty(configAttributes.getLocations());
    }

    @Test
    public void processContextConfigurationWithDefaultXmlConfigAndConfigurationClassGeneration() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(CoreMatchers.containsString("both default locations AND default configuration classes were detected"));
        ContextConfigurationAttributes configAttributes = new ContextConfigurationAttributes(DelegatingSmartContextLoaderTests.ImproperDuplicateDefaultXmlAndConfigClassTestCase.class, DelegatingSmartContextLoaderTests.EMPTY_STRING_ARRAY, DelegatingSmartContextLoaderTests.EMPTY_CLASS_ARRAY, true, null, true, ContextLoader.class);
        loader.processContextConfiguration(configAttributes);
    }

    @Test
    public void processContextConfigurationWithLocation() {
        String[] locations = new String[]{ "classpath:/foo.xml" };
        ContextConfigurationAttributes configAttributes = new ContextConfigurationAttributes(getClass(), locations, DelegatingSmartContextLoaderTests.EMPTY_CLASS_ARRAY, true, null, true, ContextLoader.class);
        loader.processContextConfiguration(configAttributes);
        Assert.assertArrayEquals(locations, configAttributes.getLocations());
        DelegatingSmartContextLoaderTests.assertEmpty(configAttributes.getClasses());
    }

    @Test
    public void processContextConfigurationWithConfigurationClass() {
        Class<?>[] classes = new Class<?>[]{ getClass() };
        ContextConfigurationAttributes configAttributes = new ContextConfigurationAttributes(getClass(), DelegatingSmartContextLoaderTests.EMPTY_STRING_ARRAY, classes, true, null, true, ContextLoader.class);
        loader.processContextConfiguration(configAttributes);
        Assert.assertArrayEquals(classes, configAttributes.getClasses());
        DelegatingSmartContextLoaderTests.assertEmpty(configAttributes.getLocations());
    }

    // --- SmartContextLoader - loadContext() ----------------------------------
    @Test(expected = IllegalArgumentException.class)
    public void loadContextWithNullConfig() throws Exception {
        MergedContextConfiguration mergedConfig = null;
        loader.loadContext(mergedConfig);
    }

    @Test
    public void loadContextWithoutLocationsAndConfigurationClasses() throws Exception {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(CoreMatchers.startsWith("Neither"));
        expectedException.expectMessage(CoreMatchers.containsString("was able to load an ApplicationContext from"));
        MergedContextConfiguration mergedConfig = new MergedContextConfiguration(getClass(), DelegatingSmartContextLoaderTests.EMPTY_STRING_ARRAY, DelegatingSmartContextLoaderTests.EMPTY_CLASS_ARRAY, DelegatingSmartContextLoaderTests.EMPTY_STRING_ARRAY, loader);
        loader.loadContext(mergedConfig);
    }

    /**
     *
     *
     * @since 4.1
     */
    @Test
    public void loadContextWithLocationsAndConfigurationClasses() throws Exception {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(CoreMatchers.startsWith("Neither"));
        expectedException.expectMessage(CoreMatchers.endsWith("declare either 'locations' or 'classes' but not both."));
        MergedContextConfiguration mergedConfig = new MergedContextConfiguration(getClass(), new String[]{ "test.xml" }, new Class<?>[]{ getClass() }, DelegatingSmartContextLoaderTests.EMPTY_STRING_ARRAY, loader);
        loader.loadContext(mergedConfig);
    }

    @Test
    public void loadContextWithXmlConfig() throws Exception {
        MergedContextConfiguration mergedConfig = new MergedContextConfiguration(DelegatingSmartContextLoaderTests.XmlTestCase.class, new String[]{ "classpath:/org/springframework/test/context/support/DelegatingSmartContextLoaderTests$XmlTestCase-context.xml" }, DelegatingSmartContextLoaderTests.EMPTY_CLASS_ARRAY, DelegatingSmartContextLoaderTests.EMPTY_STRING_ARRAY, loader);
        assertApplicationContextLoadsAndContainsFooString(mergedConfig);
    }

    @Test
    public void loadContextWithConfigurationClass() throws Exception {
        MergedContextConfiguration mergedConfig = new MergedContextConfiguration(DelegatingSmartContextLoaderTests.ConfigClassTestCase.class, DelegatingSmartContextLoaderTests.EMPTY_STRING_ARRAY, new Class<?>[]{ DelegatingSmartContextLoaderTests.ConfigClassTestCase.Config.class }, DelegatingSmartContextLoaderTests.EMPTY_STRING_ARRAY, loader);
        assertApplicationContextLoadsAndContainsFooString(mergedConfig);
    }

    // --- ContextLoader -------------------------------------------------------
    @Test(expected = UnsupportedOperationException.class)
    public void processLocations() {
        loader.processLocations(getClass(), DelegatingSmartContextLoaderTests.EMPTY_STRING_ARRAY);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void loadContextFromLocations() throws Exception {
        loader.loadContext(DelegatingSmartContextLoaderTests.EMPTY_STRING_ARRAY);
    }

    // -------------------------------------------------------------------------
    static class XmlTestCase {}

    static class ConfigClassTestCase {
        @Configuration
        static class Config {
            @Bean
            public String foo() {
                return new String("foo");
            }
        }

        static class NotAConfigClass {}
    }

    static class ImproperDuplicateDefaultXmlAndConfigClassTestCase {
        // intentionally empty: we just need the class to be present to fail
        // the test
        @Configuration
        static class Config {}
    }
}

