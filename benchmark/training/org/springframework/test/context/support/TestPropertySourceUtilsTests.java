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
package org.springframework.test.context.support;


import MockPropertySource.MOCK_PROPERTIES_PROPERTY_SOURCE_NAME;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.AnnotationConfigurationException;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.TestPropertySource;


/**
 * Unit tests for {@link TestPropertySourceUtils}.
 *
 * @author Sam Brannen
 * @since 4.1
 */
public class TestPropertySourceUtilsTests {
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    private static final String[] KEY_VALUE_PAIR = new String[]{ "key = value" };

    private static final String[] FOO_LOCATIONS = new String[]{ "classpath:/foo.properties" };

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void emptyAnnotation() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(CoreMatchers.startsWith("Could not detect default properties file for test"));
        expectedException.expectMessage(CoreMatchers.containsString("EmptyPropertySources.properties"));
        buildMergedTestPropertySources(TestPropertySourceUtilsTests.EmptyPropertySources.class);
    }

    @Test
    public void extendedEmptyAnnotation() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(CoreMatchers.startsWith("Could not detect default properties file for test"));
        expectedException.expectMessage(CoreMatchers.containsString("ExtendedEmptyPropertySources.properties"));
        buildMergedTestPropertySources(TestPropertySourceUtilsTests.ExtendedEmptyPropertySources.class);
    }

    @Test
    public void value() {
        TestPropertySourceUtilsTests.assertMergedTestPropertySources(TestPropertySourceUtilsTests.ValuePropertySources.class, TestPropertySourceUtilsTests.asArray("classpath:/value.xml"), TestPropertySourceUtilsTests.EMPTY_STRING_ARRAY);
    }

    @Test
    public void locationsAndValueAttributes() {
        expectedException.expect(AnnotationConfigurationException.class);
        buildMergedTestPropertySources(TestPropertySourceUtilsTests.LocationsAndValuePropertySources.class);
    }

    @Test
    public void locationsAndProperties() {
        TestPropertySourceUtilsTests.assertMergedTestPropertySources(TestPropertySourceUtilsTests.LocationsAndPropertiesPropertySources.class, TestPropertySourceUtilsTests.asArray("classpath:/foo1.xml", "classpath:/foo2.xml"), TestPropertySourceUtilsTests.asArray("k1a=v1a", "k1b: v1b"));
    }

    @Test
    public void inheritedLocationsAndProperties() {
        TestPropertySourceUtilsTests.assertMergedTestPropertySources(TestPropertySourceUtilsTests.InheritedPropertySources.class, TestPropertySourceUtilsTests.asArray("classpath:/foo1.xml", "classpath:/foo2.xml"), TestPropertySourceUtilsTests.asArray("k1a=v1a", "k1b: v1b"));
    }

    @Test
    public void extendedLocationsAndProperties() {
        TestPropertySourceUtilsTests.assertMergedTestPropertySources(TestPropertySourceUtilsTests.ExtendedPropertySources.class, TestPropertySourceUtilsTests.asArray("classpath:/foo1.xml", "classpath:/foo2.xml", "classpath:/bar1.xml", "classpath:/bar2.xml"), TestPropertySourceUtilsTests.asArray("k1a=v1a", "k1b: v1b", "k2a v2a", "k2b: v2b"));
    }

    @Test
    public void overriddenLocations() {
        TestPropertySourceUtilsTests.assertMergedTestPropertySources(TestPropertySourceUtilsTests.OverriddenLocationsPropertySources.class, TestPropertySourceUtilsTests.asArray("classpath:/baz.properties"), TestPropertySourceUtilsTests.asArray("k1a=v1a", "k1b: v1b", "key = value"));
    }

    @Test
    public void overriddenProperties() {
        TestPropertySourceUtilsTests.assertMergedTestPropertySources(TestPropertySourceUtilsTests.OverriddenPropertiesPropertySources.class, TestPropertySourceUtilsTests.asArray("classpath:/foo1.xml", "classpath:/foo2.xml", "classpath:/baz.properties"), TestPropertySourceUtilsTests.KEY_VALUE_PAIR);
    }

    @Test
    public void overriddenLocationsAndProperties() {
        TestPropertySourceUtilsTests.assertMergedTestPropertySources(TestPropertySourceUtilsTests.OverriddenLocationsAndPropertiesPropertySources.class, TestPropertySourceUtilsTests.asArray("classpath:/baz.properties"), TestPropertySourceUtilsTests.KEY_VALUE_PAIR);
    }

    @Test
    public void addPropertiesFilesToEnvironmentWithNullContext() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("must not be null");
        addPropertiesFilesToEnvironment(((ConfigurableApplicationContext) (null)), TestPropertySourceUtilsTests.FOO_LOCATIONS);
    }

    @Test
    public void addPropertiesFilesToEnvironmentWithContextAndNullLocations() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("must not be null");
        addPropertiesFilesToEnvironment(Mockito.mock(ConfigurableApplicationContext.class), ((String[]) (null)));
    }

    @Test
    public void addPropertiesFilesToEnvironmentWithNullEnvironment() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("must not be null");
        addPropertiesFilesToEnvironment(((ConfigurableEnvironment) (null)), Mockito.mock(ResourceLoader.class), TestPropertySourceUtilsTests.FOO_LOCATIONS);
    }

    @Test
    public void addPropertiesFilesToEnvironmentWithEnvironmentAndNullLocations() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("must not be null");
        addPropertiesFilesToEnvironment(new MockEnvironment(), Mockito.mock(ResourceLoader.class), ((String[]) (null)));
    }

    @Test
    public void addPropertiesFilesToEnvironmentWithSinglePropertyFromVirtualFile() {
        ConfigurableEnvironment environment = new MockEnvironment();
        MutablePropertySources propertySources = environment.getPropertySources();
        propertySources.remove(MOCK_PROPERTIES_PROPERTY_SOURCE_NAME);
        Assert.assertEquals(0, propertySources.size());
        String pair = "key = value";
        ByteArrayResource resource = new ByteArrayResource(pair.getBytes(), ("from inlined property: " + pair));
        ResourceLoader resourceLoader = Mockito.mock(ResourceLoader.class);
        Mockito.when(resourceLoader.getResource(ArgumentMatchers.anyString())).thenReturn(resource);
        addPropertiesFilesToEnvironment(environment, resourceLoader, TestPropertySourceUtilsTests.FOO_LOCATIONS);
        Assert.assertEquals(1, propertySources.size());
        Assert.assertEquals("value", environment.getProperty("key"));
    }

    @Test
    public void addInlinedPropertiesToEnvironmentWithNullContext() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("context");
        addInlinedPropertiesToEnvironment(((ConfigurableApplicationContext) (null)), TestPropertySourceUtilsTests.KEY_VALUE_PAIR);
    }

    @Test
    public void addInlinedPropertiesToEnvironmentWithContextAndNullInlinedProperties() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("inlined");
        addInlinedPropertiesToEnvironment(Mockito.mock(ConfigurableApplicationContext.class), ((String[]) (null)));
    }

    @Test
    public void addInlinedPropertiesToEnvironmentWithNullEnvironment() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("environment");
        addInlinedPropertiesToEnvironment(((ConfigurableEnvironment) (null)), TestPropertySourceUtilsTests.KEY_VALUE_PAIR);
    }

    @Test
    public void addInlinedPropertiesToEnvironmentWithEnvironmentAndNullInlinedProperties() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("inlined");
        addInlinedPropertiesToEnvironment(new MockEnvironment(), ((String[]) (null)));
    }

    @Test
    public void addInlinedPropertiesToEnvironmentWithMalformedUnicodeInValue() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Failed to load test environment property");
        addInlinedPropertiesToEnvironment(new MockEnvironment(), TestPropertySourceUtilsTests.asArray("key = \\uZZZZ"));
    }

    @Test
    public void addInlinedPropertiesToEnvironmentWithMultipleKeyValuePairsInSingleInlinedProperty() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Failed to load exactly one test environment property");
        addInlinedPropertiesToEnvironment(new MockEnvironment(), TestPropertySourceUtilsTests.asArray("a=b\nx=y"));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void addInlinedPropertiesToEnvironmentWithEmptyProperty() {
        ConfigurableEnvironment environment = new MockEnvironment();
        MutablePropertySources propertySources = environment.getPropertySources();
        propertySources.remove(MOCK_PROPERTIES_PROPERTY_SOURCE_NAME);
        Assert.assertEquals(0, propertySources.size());
        addInlinedPropertiesToEnvironment(environment, TestPropertySourceUtilsTests.asArray("  "));
        Assert.assertEquals(1, propertySources.size());
        Assert.assertEquals(0, ((Map) (propertySources.iterator().next().getSource())).size());
    }

    @Test
    public void convertInlinedPropertiesToMapWithNullInlinedProperties() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("inlined");
        convertInlinedPropertiesToMap(((String[]) (null)));
    }

    @TestPropertySource
    static class EmptyPropertySources {}

    @TestPropertySource
    static class ExtendedEmptyPropertySources extends TestPropertySourceUtilsTests.EmptyPropertySources {}

    @TestPropertySource(locations = "/foo", value = "/bar")
    static class LocationsAndValuePropertySources {}

    @TestPropertySource("/value.xml")
    static class ValuePropertySources {}

    @TestPropertySource(locations = { "/foo1.xml", "/foo2.xml" }, properties = { "k1a=v1a", "k1b: v1b" })
    static class LocationsAndPropertiesPropertySources {}

    static class InheritedPropertySources extends TestPropertySourceUtilsTests.LocationsAndPropertiesPropertySources {}

    @TestPropertySource(locations = { "/bar1.xml", "/bar2.xml" }, properties = { "k2a v2a", "k2b: v2b" })
    static class ExtendedPropertySources extends TestPropertySourceUtilsTests.LocationsAndPropertiesPropertySources {}

    @TestPropertySource(locations = "/baz.properties", properties = "key = value", inheritLocations = false)
    static class OverriddenLocationsPropertySources extends TestPropertySourceUtilsTests.LocationsAndPropertiesPropertySources {}

    @TestPropertySource(locations = "/baz.properties", properties = "key = value", inheritProperties = false)
    static class OverriddenPropertiesPropertySources extends TestPropertySourceUtilsTests.LocationsAndPropertiesPropertySources {}

    @TestPropertySource(locations = "/baz.properties", properties = "key = value", inheritLocations = false, inheritProperties = false)
    static class OverriddenLocationsAndPropertiesPropertySources extends TestPropertySourceUtilsTests.LocationsAndPropertiesPropertySources {}
}

