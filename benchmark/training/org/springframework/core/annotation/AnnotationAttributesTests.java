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
package org.springframework.core.annotation;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Unit tests for {@link AnnotationAttributes}.
 *
 * @author Chris Beams
 * @author Sam Brannen
 * @author Juergen Hoeller
 * @since 3.1.1
 */
public class AnnotationAttributesTests {
    private AnnotationAttributes attributes = new AnnotationAttributes();

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void typeSafeAttributeAccess() {
        AnnotationAttributes nestedAttributes = new AnnotationAttributes();
        nestedAttributes.put("value", 10);
        nestedAttributes.put("name", "algernon");
        attributes.put("name", "dave");
        attributes.put("names", new String[]{ "dave", "frank", "hal" });
        attributes.put("bool1", true);
        attributes.put("bool2", false);
        attributes.put("color", AnnotationAttributesTests.Color.RED);
        attributes.put("class", Integer.class);
        attributes.put("classes", new Class<?>[]{ Number.class, Short.class, Integer.class });
        attributes.put("number", 42);
        attributes.put("anno", nestedAttributes);
        attributes.put("annoArray", new AnnotationAttributes[]{ nestedAttributes });
        Assert.assertThat(attributes.getString("name"), CoreMatchers.equalTo("dave"));
        Assert.assertThat(attributes.getStringArray("names"), CoreMatchers.equalTo(new String[]{ "dave", "frank", "hal" }));
        Assert.assertThat(attributes.getBoolean("bool1"), CoreMatchers.equalTo(true));
        Assert.assertThat(attributes.getBoolean("bool2"), CoreMatchers.equalTo(false));
        Assert.assertThat(attributes.<AnnotationAttributesTests.Color>getEnum("color"), CoreMatchers.equalTo(AnnotationAttributesTests.Color.RED));
        Assert.assertTrue(attributes.getClass("class").equals(Integer.class));
        Assert.assertThat(attributes.getClassArray("classes"), CoreMatchers.equalTo(new Class<?>[]{ Number.class, Short.class, Integer.class }));
        Assert.assertThat(attributes.<Integer>getNumber("number"), CoreMatchers.equalTo(42));
        Assert.assertThat(attributes.getAnnotation("anno").<Integer>getNumber("value"), CoreMatchers.equalTo(10));
        Assert.assertThat(attributes.getAnnotationArray("annoArray")[0].getString("name"), CoreMatchers.equalTo("algernon"));
    }

    @Test
    public void unresolvableClass() throws Exception {
        attributes.put("unresolvableClass", new ClassNotFoundException("myclass"));
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(CoreMatchers.containsString("myclass"));
        attributes.getClass("unresolvableClass");
    }

    @Test
    public void singleElementToSingleElementArrayConversionSupport() throws Exception {
        AnnotationAttributesTests.Filter filter = AnnotationAttributesTests.FilteredClass.class.getAnnotation(AnnotationAttributesTests.Filter.class);
        AnnotationAttributes nestedAttributes = new AnnotationAttributes();
        nestedAttributes.put("name", "Dilbert");
        // Store single elements
        attributes.put("names", "Dogbert");
        attributes.put("classes", Number.class);
        attributes.put("nestedAttributes", nestedAttributes);
        attributes.put("filters", filter);
        // Get back arrays of single elements
        Assert.assertThat(attributes.getStringArray("names"), CoreMatchers.equalTo(new String[]{ "Dogbert" }));
        Assert.assertThat(attributes.getClassArray("classes"), CoreMatchers.equalTo(new Class<?>[]{ Number.class }));
        AnnotationAttributes[] array = attributes.getAnnotationArray("nestedAttributes");
        Assert.assertNotNull(array);
        Assert.assertThat(array.length, CoreMatchers.is(1));
        Assert.assertThat(array[0].getString("name"), CoreMatchers.equalTo("Dilbert"));
        AnnotationAttributesTests.Filter[] filters = attributes.getAnnotationArray("filters", AnnotationAttributesTests.Filter.class);
        Assert.assertNotNull(filters);
        Assert.assertThat(filters.length, CoreMatchers.is(1));
        Assert.assertThat(filters[0].pattern(), CoreMatchers.equalTo("foo"));
    }

    @Test
    public void nestedAnnotations() throws Exception {
        AnnotationAttributesTests.Filter filter = AnnotationAttributesTests.FilteredClass.class.getAnnotation(AnnotationAttributesTests.Filter.class);
        attributes.put("filter", filter);
        attributes.put("filters", new AnnotationAttributesTests.Filter[]{ filter, filter });
        AnnotationAttributesTests.Filter retrievedFilter = attributes.getAnnotation("filter", AnnotationAttributesTests.Filter.class);
        Assert.assertThat(retrievedFilter, CoreMatchers.equalTo(filter));
        Assert.assertThat(retrievedFilter.pattern(), CoreMatchers.equalTo("foo"));
        AnnotationAttributesTests.Filter[] retrievedFilters = attributes.getAnnotationArray("filters", AnnotationAttributesTests.Filter.class);
        Assert.assertNotNull(retrievedFilters);
        Assert.assertEquals(2, retrievedFilters.length);
        Assert.assertThat(retrievedFilters[1].pattern(), CoreMatchers.equalTo("foo"));
    }

    @Test
    public void getEnumWithNullAttributeName() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("must not be null or empty");
        attributes.getEnum(null);
    }

    @Test
    public void getEnumWithEmptyAttributeName() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("must not be null or empty");
        attributes.getEnum("");
    }

    @Test
    public void getEnumWithUnknownAttributeName() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Attribute 'bogus' not found");
        attributes.getEnum("bogus");
    }

    @Test
    public void getEnumWithTypeMismatch() {
        attributes.put("color", "RED");
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(CoreMatchers.containsString("Attribute 'color' is of type String, but Enum was expected"));
        attributes.getEnum("color");
    }

    @Test
    public void getAliasedStringWithImplicitAliases() {
        String value = "metaverse";
        List<String> aliases = Arrays.asList("value", "location1", "location2", "location3", "xmlFile", "groovyScript");
        attributes = new AnnotationAttributes(AnnotationUtilsTests.ImplicitAliasesContextConfig.class);
        attributes.put("value", value);
        AnnotationUtils.postProcessAnnotationAttributes(null, attributes, false);
        aliases.stream().forEach(( alias) -> Assert.assertEquals(value, attributes.getString(alias)));
        attributes = new AnnotationAttributes(AnnotationUtilsTests.ImplicitAliasesContextConfig.class);
        attributes.put("location1", value);
        AnnotationUtils.postProcessAnnotationAttributes(null, attributes, false);
        aliases.stream().forEach(( alias) -> Assert.assertEquals(value, attributes.getString(alias)));
        attributes = new AnnotationAttributes(AnnotationUtilsTests.ImplicitAliasesContextConfig.class);
        attributes.put("value", value);
        attributes.put("location1", value);
        attributes.put("xmlFile", value);
        attributes.put("groovyScript", value);
        AnnotationUtils.postProcessAnnotationAttributes(null, attributes, false);
        aliases.stream().forEach(( alias) -> Assert.assertEquals(value, attributes.getString(alias)));
    }

    @Test
    public void getAliasedStringArrayWithImplicitAliases() {
        String[] value = new String[]{ "test.xml" };
        List<String> aliases = Arrays.asList("value", "location1", "location2", "location3", "xmlFile", "groovyScript");
        attributes = new AnnotationAttributes(AnnotationUtilsTests.ImplicitAliasesContextConfig.class);
        attributes.put("location1", value);
        AnnotationUtils.postProcessAnnotationAttributes(null, attributes, false);
        aliases.stream().forEach(( alias) -> Assert.assertArrayEquals(value, attributes.getStringArray(alias)));
        attributes = new AnnotationAttributes(AnnotationUtilsTests.ImplicitAliasesContextConfig.class);
        attributes.put("value", value);
        AnnotationUtils.postProcessAnnotationAttributes(null, attributes, false);
        aliases.stream().forEach(( alias) -> Assert.assertArrayEquals(value, attributes.getStringArray(alias)));
        attributes = new AnnotationAttributes(AnnotationUtilsTests.ImplicitAliasesContextConfig.class);
        attributes.put("location1", value);
        attributes.put("value", value);
        AnnotationUtils.postProcessAnnotationAttributes(null, attributes, false);
        aliases.stream().forEach(( alias) -> Assert.assertArrayEquals(value, attributes.getStringArray(alias)));
        attributes = new AnnotationAttributes(AnnotationUtilsTests.ImplicitAliasesContextConfig.class);
        attributes.put("location1", value);
        AnnotationUtils.registerDefaultValues(attributes);
        AnnotationUtils.postProcessAnnotationAttributes(null, attributes, false);
        aliases.stream().forEach(( alias) -> Assert.assertArrayEquals(value, attributes.getStringArray(alias)));
        attributes = new AnnotationAttributes(AnnotationUtilsTests.ImplicitAliasesContextConfig.class);
        attributes.put("value", value);
        AnnotationUtils.registerDefaultValues(attributes);
        AnnotationUtils.postProcessAnnotationAttributes(null, attributes, false);
        aliases.stream().forEach(( alias) -> Assert.assertArrayEquals(value, attributes.getStringArray(alias)));
        attributes = new AnnotationAttributes(AnnotationUtilsTests.ImplicitAliasesContextConfig.class);
        AnnotationUtils.registerDefaultValues(attributes);
        AnnotationUtils.postProcessAnnotationAttributes(null, attributes, false);
        aliases.stream().forEach(( alias) -> Assert.assertArrayEquals(new String[]{ "" }, attributes.getStringArray(alias)));
    }

    enum Color {

        RED,
        WHITE,
        BLUE;}

    @Retention(RetentionPolicy.RUNTIME)
    @interface Filter {
        @AliasFor(attribute = "classes")
        Class<?>[] value() default {  };

        @AliasFor(attribute = "value")
        Class<?>[] classes() default {  };

        String pattern();
    }

    @AnnotationAttributesTests.Filter(pattern = "foo")
    static class FilteredClass {}
}

