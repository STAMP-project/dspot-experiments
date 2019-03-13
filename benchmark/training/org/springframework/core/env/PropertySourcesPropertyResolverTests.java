/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.core.env;


import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.mock.env.MockPropertySource;


/**
 *
 *
 * @author Chris Beams
 * @since 3.1
 */
public class PropertySourcesPropertyResolverTests {
    private Properties testProperties;

    private MutablePropertySources propertySources;

    private ConfigurablePropertyResolver propertyResolver;

    @Test
    public void containsProperty() {
        Assert.assertThat(propertyResolver.containsProperty("foo"), is(false));
        testProperties.put("foo", "bar");
        Assert.assertThat(propertyResolver.containsProperty("foo"), is(true));
    }

    @Test
    public void getProperty() {
        Assert.assertThat(propertyResolver.getProperty("foo"), nullValue());
        testProperties.put("foo", "bar");
        Assert.assertThat(propertyResolver.getProperty("foo"), is("bar"));
    }

    @Test
    public void getProperty_withDefaultValue() {
        Assert.assertThat(propertyResolver.getProperty("foo", "myDefault"), is("myDefault"));
        testProperties.put("foo", "bar");
        Assert.assertThat(propertyResolver.getProperty("foo"), is("bar"));
    }

    @Test
    public void getProperty_propertySourceSearchOrderIsFIFO() {
        MutablePropertySources sources = new MutablePropertySources();
        PropertyResolver resolver = new PropertySourcesPropertyResolver(sources);
        sources.addFirst(new MockPropertySource("ps1").withProperty("pName", "ps1Value"));
        Assert.assertThat(resolver.getProperty("pName"), equalTo("ps1Value"));
        sources.addFirst(new MockPropertySource("ps2").withProperty("pName", "ps2Value"));
        Assert.assertThat(resolver.getProperty("pName"), equalTo("ps2Value"));
        sources.addFirst(new MockPropertySource("ps3").withProperty("pName", "ps3Value"));
        Assert.assertThat(resolver.getProperty("pName"), equalTo("ps3Value"));
    }

    @Test
    public void getProperty_withExplicitNullValue() {
        // java.util.Properties does not allow null values (because Hashtable does not)
        Map<String, Object> nullableProperties = new HashMap<>();
        propertySources.addLast(new MapPropertySource("nullableProperties", nullableProperties));
        nullableProperties.put("foo", null);
        Assert.assertThat(propertyResolver.getProperty("foo"), nullValue());
    }

    @Test
    public void getProperty_withTargetType_andDefaultValue() {
        Assert.assertThat(propertyResolver.getProperty("foo", Integer.class, 42), equalTo(42));
        testProperties.put("foo", 13);
        Assert.assertThat(propertyResolver.getProperty("foo", Integer.class, 42), equalTo(13));
    }

    @Test
    public void getProperty_withStringArrayConversion() {
        testProperties.put("foo", "bar,baz");
        Assert.assertThat(propertyResolver.getProperty("foo", String[].class), equalTo(new String[]{ "bar", "baz" }));
    }

    @Test
    public void getProperty_withNonConvertibleTargetType() {
        testProperties.put("foo", "bar");
        class TestType {}
        try {
            propertyResolver.getProperty("foo", TestType.class);
            Assert.fail("Expected ConverterNotFoundException due to non-convertible types");
        } catch (ConverterNotFoundException ex) {
            // expected
        }
    }

    @Test
    public void getProperty_doesNotCache_replaceExistingKeyPostConstruction() {
        String key = "foo";
        String value1 = "bar";
        String value2 = "biz";
        HashMap<String, Object> map = new HashMap<>();
        map.put(key, value1);// before construction

        MutablePropertySources propertySources = new MutablePropertySources();
        propertySources.addFirst(new MapPropertySource("testProperties", map));
        PropertyResolver propertyResolver = new PropertySourcesPropertyResolver(propertySources);
        Assert.assertThat(propertyResolver.getProperty(key), equalTo(value1));
        map.put(key, value2);// after construction and first resolution

        Assert.assertThat(propertyResolver.getProperty(key), equalTo(value2));
    }

    @Test
    public void getProperty_doesNotCache_addNewKeyPostConstruction() {
        HashMap<String, Object> map = new HashMap<>();
        MutablePropertySources propertySources = new MutablePropertySources();
        propertySources.addFirst(new MapPropertySource("testProperties", map));
        PropertyResolver propertyResolver = new PropertySourcesPropertyResolver(propertySources);
        Assert.assertThat(propertyResolver.getProperty("foo"), equalTo(null));
        map.put("foo", "42");
        Assert.assertThat(propertyResolver.getProperty("foo"), equalTo("42"));
    }

    @Test
    public void getPropertySources_replacePropertySource() {
        propertySources = new MutablePropertySources();
        propertyResolver = new PropertySourcesPropertyResolver(propertySources);
        propertySources.addLast(new MockPropertySource("local").withProperty("foo", "localValue"));
        propertySources.addLast(new MockPropertySource("system").withProperty("foo", "systemValue"));
        // 'local' was added first so has precedence
        Assert.assertThat(propertyResolver.getProperty("foo"), equalTo("localValue"));
        // replace 'local' with new property source
        propertySources.replace("local", new MockPropertySource("new").withProperty("foo", "newValue"));
        // 'system' now has precedence
        Assert.assertThat(propertyResolver.getProperty("foo"), equalTo("newValue"));
        Assert.assertThat(propertySources.size(), is(2));
    }

    @Test
    public void getRequiredProperty() {
        testProperties.put("exists", "xyz");
        Assert.assertThat(propertyResolver.getRequiredProperty("exists"), is("xyz"));
        try {
            propertyResolver.getRequiredProperty("bogus");
            Assert.fail("expected IllegalStateException");
        } catch (IllegalStateException ex) {
            // expected
        }
    }

    @Test
    public void getRequiredProperty_withStringArrayConversion() {
        testProperties.put("exists", "abc,123");
        Assert.assertThat(propertyResolver.getRequiredProperty("exists", String[].class), equalTo(new String[]{ "abc", "123" }));
        try {
            propertyResolver.getRequiredProperty("bogus", String[].class);
            Assert.fail("expected IllegalStateException");
        } catch (IllegalStateException ex) {
            // expected
        }
    }

    @Test
    public void resolvePlaceholders() {
        MutablePropertySources propertySources = new MutablePropertySources();
        propertySources.addFirst(new MockPropertySource().withProperty("key", "value"));
        PropertyResolver resolver = new PropertySourcesPropertyResolver(propertySources);
        Assert.assertThat(resolver.resolvePlaceholders("Replace this ${key}"), equalTo("Replace this value"));
    }

    @Test
    public void resolvePlaceholders_withUnresolvable() {
        MutablePropertySources propertySources = new MutablePropertySources();
        propertySources.addFirst(new MockPropertySource().withProperty("key", "value"));
        PropertyResolver resolver = new PropertySourcesPropertyResolver(propertySources);
        Assert.assertThat(resolver.resolvePlaceholders("Replace this ${key} plus ${unknown}"), equalTo("Replace this value plus ${unknown}"));
    }

    @Test
    public void resolvePlaceholders_withDefaultValue() {
        MutablePropertySources propertySources = new MutablePropertySources();
        propertySources.addFirst(new MockPropertySource().withProperty("key", "value"));
        PropertyResolver resolver = new PropertySourcesPropertyResolver(propertySources);
        Assert.assertThat(resolver.resolvePlaceholders("Replace this ${key} plus ${unknown:defaultValue}"), equalTo("Replace this value plus defaultValue"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void resolvePlaceholders_withNullInput() {
        new PropertySourcesPropertyResolver(new MutablePropertySources()).resolvePlaceholders(null);
    }

    @Test
    public void resolveRequiredPlaceholders() {
        MutablePropertySources propertySources = new MutablePropertySources();
        propertySources.addFirst(new MockPropertySource().withProperty("key", "value"));
        PropertyResolver resolver = new PropertySourcesPropertyResolver(propertySources);
        Assert.assertThat(resolver.resolveRequiredPlaceholders("Replace this ${key}"), equalTo("Replace this value"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void resolveRequiredPlaceholders_withUnresolvable() {
        MutablePropertySources propertySources = new MutablePropertySources();
        propertySources.addFirst(new MockPropertySource().withProperty("key", "value"));
        PropertyResolver resolver = new PropertySourcesPropertyResolver(propertySources);
        resolver.resolveRequiredPlaceholders("Replace this ${key} plus ${unknown}");
    }

    @Test
    public void resolveRequiredPlaceholders_withDefaultValue() {
        MutablePropertySources propertySources = new MutablePropertySources();
        propertySources.addFirst(new MockPropertySource().withProperty("key", "value"));
        PropertyResolver resolver = new PropertySourcesPropertyResolver(propertySources);
        Assert.assertThat(resolver.resolveRequiredPlaceholders("Replace this ${key} plus ${unknown:defaultValue}"), equalTo("Replace this value plus defaultValue"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void resolveRequiredPlaceholders_withNullInput() {
        new PropertySourcesPropertyResolver(new MutablePropertySources()).resolveRequiredPlaceholders(null);
    }

    @Test
    public void setRequiredProperties_andValidateRequiredProperties() {
        // no properties have been marked as required -> validation should pass
        propertyResolver.validateRequiredProperties();
        // mark which properties are required
        propertyResolver.setRequiredProperties("foo", "bar");
        // neither foo nor bar properties are present -> validating should throw
        try {
            propertyResolver.validateRequiredProperties();
            Assert.fail("expected validation exception");
        } catch (MissingRequiredPropertiesException ex) {
            Assert.assertThat(ex.getMessage(), equalTo(("The following properties were declared as required " + "but could not be resolved: [foo, bar]")));
        }
        // add foo property -> validation should fail only on missing 'bar' property
        testProperties.put("foo", "fooValue");
        try {
            propertyResolver.validateRequiredProperties();
            Assert.fail("expected validation exception");
        } catch (MissingRequiredPropertiesException ex) {
            Assert.assertThat(ex.getMessage(), equalTo(("The following properties were declared as required " + "but could not be resolved: [bar]")));
        }
        // add bar property -> validation should pass, even with an empty string value
        testProperties.put("bar", "");
        propertyResolver.validateRequiredProperties();
    }

    @Test
    public void resolveNestedPropertyPlaceholders() {
        MutablePropertySources ps = new MutablePropertySources();
        // cyclic reference right
        ps.addFirst(// cyclic reference left
        // unresolvable w/ default
        // unresolvable placeholder
        // deeply nested placeholders
        // nested placeholders
        new MockPropertySource().withProperty("p1", "v1").withProperty("p2", "v2").withProperty("p3", "${p1}:${p2}").withProperty("p4", "${p3}").withProperty("p5", "${p1}:${p2}:${bogus}").withProperty("p6", "${p1}:${p2}:${bogus:def}").withProperty("pL", "${pR}").withProperty("pR", "${pL}"));
        ConfigurablePropertyResolver pr = new PropertySourcesPropertyResolver(ps);
        Assert.assertThat(pr.getProperty("p1"), equalTo("v1"));
        Assert.assertThat(pr.getProperty("p2"), equalTo("v2"));
        Assert.assertThat(pr.getProperty("p3"), equalTo("v1:v2"));
        Assert.assertThat(pr.getProperty("p4"), equalTo("v1:v2"));
        try {
            pr.getProperty("p5");
        } catch (IllegalArgumentException ex) {
            Assert.assertThat(ex.getMessage(), containsString("Could not resolve placeholder \'bogus\' in value \"${p1}:${p2}:${bogus}\""));
        }
        Assert.assertThat(pr.getProperty("p6"), equalTo("v1:v2:def"));
        try {
            pr.getProperty("pL");
        } catch (IllegalArgumentException ex) {
            Assert.assertTrue(ex.getMessage().toLowerCase().contains("circular"));
        }
    }

    @Test
    public void ignoreUnresolvableNestedPlaceholdersIsConfigurable() {
        MutablePropertySources ps = new MutablePropertySources();
        // unresolvable placeholder
        ps.addFirst(// unresolvable w/ default
        new MockPropertySource().withProperty("p1", "v1").withProperty("p2", "v2").withProperty("p3", "${p1}:${p2}:${bogus:def}").withProperty("p4", "${p1}:${p2}:${bogus}"));
        ConfigurablePropertyResolver pr = new PropertySourcesPropertyResolver(ps);
        Assert.assertThat(pr.getProperty("p1"), equalTo("v1"));
        Assert.assertThat(pr.getProperty("p2"), equalTo("v2"));
        Assert.assertThat(pr.getProperty("p3"), equalTo("v1:v2:def"));
        // placeholders nested within the value of "p4" are unresolvable and cause an
        // exception by default
        try {
            pr.getProperty("p4");
        } catch (IllegalArgumentException ex) {
            Assert.assertThat(ex.getMessage(), containsString("Could not resolve placeholder \'bogus\' in value \"${p1}:${p2}:${bogus}\""));
        }
        // relax the treatment of unresolvable nested placeholders
        pr.setIgnoreUnresolvableNestedPlaceholders(true);
        // and observe they now pass through unresolved
        Assert.assertThat(pr.getProperty("p4"), equalTo("v1:v2:${bogus}"));
        // resolve[Nested]Placeholders methods behave as usual regardless the value of
        // ignoreUnresolvableNestedPlaceholders
        Assert.assertThat(pr.resolvePlaceholders("${p1}:${p2}:${bogus}"), equalTo("v1:v2:${bogus}"));
        try {
            pr.resolveRequiredPlaceholders("${p1}:${p2}:${bogus}");
        } catch (IllegalArgumentException ex) {
            Assert.assertThat(ex.getMessage(), containsString("Could not resolve placeholder \'bogus\' in value \"${p1}:${p2}:${bogus}\""));
        }
    }
}

