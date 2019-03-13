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
package org.springframework.test.util;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.test.context.ContextConfiguration;


/**
 * Unit tests for {@link MetaAnnotationUtils} that verify support for overridden
 * meta-annotation attributes.
 *
 * <p>See <a href="https://jira.spring.io/browse/SPR-10181">SPR-10181</a>.
 *
 * @author Sam Brannen
 * @since 4.0
 * @see MetaAnnotationUtilsTests
 */
public class OverriddenMetaAnnotationAttributesTests {
    @Test
    public void contextConfigurationValue() throws Exception {
        Class<OverriddenMetaAnnotationAttributesTests.MetaValueConfigTestCase> declaringClass = OverriddenMetaAnnotationAttributesTests.MetaValueConfigTestCase.class;
        AnnotationDescriptor<ContextConfiguration> descriptor = findAnnotationDescriptor(declaringClass, ContextConfiguration.class);
        Assert.assertNotNull(descriptor);
        Assert.assertEquals(declaringClass, descriptor.getRootDeclaringClass());
        Assert.assertEquals(OverriddenMetaAnnotationAttributesTests.MetaValueConfig.class, descriptor.getComposedAnnotationType());
        Assert.assertEquals(ContextConfiguration.class, descriptor.getAnnotationType());
        Assert.assertNotNull(descriptor.getComposedAnnotation());
        Assert.assertEquals(OverriddenMetaAnnotationAttributesTests.MetaValueConfig.class, descriptor.getComposedAnnotationType());
        // direct access to annotation value:
        Assert.assertArrayEquals(new String[]{ "foo.xml" }, descriptor.getAnnotation().value());
    }

    @Test
    public void overriddenContextConfigurationValue() throws Exception {
        Class<?> declaringClass = OverriddenMetaAnnotationAttributesTests.OverriddenMetaValueConfigTestCase.class;
        AnnotationDescriptor<ContextConfiguration> descriptor = findAnnotationDescriptor(declaringClass, ContextConfiguration.class);
        Assert.assertNotNull(descriptor);
        Assert.assertEquals(declaringClass, descriptor.getRootDeclaringClass());
        Assert.assertEquals(OverriddenMetaAnnotationAttributesTests.MetaValueConfig.class, descriptor.getComposedAnnotationType());
        Assert.assertEquals(ContextConfiguration.class, descriptor.getAnnotationType());
        Assert.assertNotNull(descriptor.getComposedAnnotation());
        Assert.assertEquals(OverriddenMetaAnnotationAttributesTests.MetaValueConfig.class, descriptor.getComposedAnnotationType());
        // direct access to annotation value:
        Assert.assertArrayEquals(new String[]{ "foo.xml" }, descriptor.getAnnotation().value());
        // overridden attribute:
        AnnotationAttributes attributes = descriptor.getAnnotationAttributes();
        // NOTE: we would like to be able to override the 'value' attribute; however,
        // Spring currently does not allow overrides for the 'value' attribute.
        // See SPR-11393 for related discussions.
        Assert.assertArrayEquals(new String[]{ "foo.xml" }, attributes.getStringArray("value"));
    }

    @Test
    public void contextConfigurationLocationsAndInheritLocations() throws Exception {
        Class<OverriddenMetaAnnotationAttributesTests.MetaLocationsConfigTestCase> declaringClass = OverriddenMetaAnnotationAttributesTests.MetaLocationsConfigTestCase.class;
        AnnotationDescriptor<ContextConfiguration> descriptor = findAnnotationDescriptor(declaringClass, ContextConfiguration.class);
        Assert.assertNotNull(descriptor);
        Assert.assertEquals(declaringClass, descriptor.getRootDeclaringClass());
        Assert.assertEquals(OverriddenMetaAnnotationAttributesTests.MetaLocationsConfig.class, descriptor.getComposedAnnotationType());
        Assert.assertEquals(ContextConfiguration.class, descriptor.getAnnotationType());
        Assert.assertNotNull(descriptor.getComposedAnnotation());
        Assert.assertEquals(OverriddenMetaAnnotationAttributesTests.MetaLocationsConfig.class, descriptor.getComposedAnnotationType());
        // direct access to annotation attributes:
        Assert.assertArrayEquals(new String[]{ "foo.xml" }, descriptor.getAnnotation().locations());
        Assert.assertFalse(descriptor.getAnnotation().inheritLocations());
    }

    @Test
    public void overriddenContextConfigurationLocationsAndInheritLocations() throws Exception {
        Class<?> declaringClass = OverriddenMetaAnnotationAttributesTests.OverriddenMetaLocationsConfigTestCase.class;
        AnnotationDescriptor<ContextConfiguration> descriptor = findAnnotationDescriptor(declaringClass, ContextConfiguration.class);
        Assert.assertNotNull(descriptor);
        Assert.assertEquals(declaringClass, descriptor.getRootDeclaringClass());
        Assert.assertEquals(OverriddenMetaAnnotationAttributesTests.MetaLocationsConfig.class, descriptor.getComposedAnnotationType());
        Assert.assertEquals(ContextConfiguration.class, descriptor.getAnnotationType());
        Assert.assertNotNull(descriptor.getComposedAnnotation());
        Assert.assertEquals(OverriddenMetaAnnotationAttributesTests.MetaLocationsConfig.class, descriptor.getComposedAnnotationType());
        // direct access to annotation attributes:
        Assert.assertArrayEquals(new String[]{ "foo.xml" }, descriptor.getAnnotation().locations());
        Assert.assertFalse(descriptor.getAnnotation().inheritLocations());
        // overridden attributes:
        AnnotationAttributes attributes = descriptor.getAnnotationAttributes();
        Assert.assertArrayEquals(new String[]{ "bar.xml" }, attributes.getStringArray("locations"));
        Assert.assertTrue(attributes.getBoolean("inheritLocations"));
    }

    // -------------------------------------------------------------------------
    @ContextConfiguration("foo.xml")
    @Retention(RetentionPolicy.RUNTIME)
    static @interface MetaValueConfig {
        String[] value() default {  };
    }

    @OverriddenMetaAnnotationAttributesTests.MetaValueConfig
    public static class MetaValueConfigTestCase {}

    @OverriddenMetaAnnotationAttributesTests.MetaValueConfig("bar.xml")
    public static class OverriddenMetaValueConfigTestCase {}

    @ContextConfiguration(locations = "foo.xml", inheritLocations = false)
    @Retention(RetentionPolicy.RUNTIME)
    static @interface MetaLocationsConfig {
        String[] locations() default {  };

        boolean inheritLocations();
    }

    @OverriddenMetaAnnotationAttributesTests.MetaLocationsConfig(inheritLocations = true)
    static class MetaLocationsConfigTestCase {}

    @OverriddenMetaAnnotationAttributesTests.MetaLocationsConfig(locations = "bar.xml", inheritLocations = true)
    static class OverriddenMetaLocationsConfigTestCase {}
}

