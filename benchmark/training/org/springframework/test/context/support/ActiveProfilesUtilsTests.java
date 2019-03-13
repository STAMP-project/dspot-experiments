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


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.springframework.core.annotation.AnnotationConfigurationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ActiveProfilesResolver;
import org.springframework.util.StringUtils;


/**
 * Unit tests for {@link ActiveProfilesUtils} involving resolution of active bean
 * definition profiles.
 *
 * @author Sam Brannen
 * @author Michail Nikolaev
 * @since 3.1
 */
public class ActiveProfilesUtilsTests extends AbstractContextConfigurationUtilsTests {
    @Test
    public void resolveActiveProfilesWithoutAnnotation() {
        assertResolvedProfiles(AbstractContextConfigurationUtilsTests.Enigma.class, AbstractContextConfigurationUtilsTests.EMPTY_STRING_ARRAY);
    }

    @Test
    public void resolveActiveProfilesWithNoProfilesDeclared() {
        assertResolvedProfiles(AbstractContextConfigurationUtilsTests.BareAnnotations.class, AbstractContextConfigurationUtilsTests.EMPTY_STRING_ARRAY);
    }

    @Test
    public void resolveActiveProfilesWithEmptyProfiles() {
        assertResolvedProfiles(ActiveProfilesUtilsTests.EmptyProfiles.class, AbstractContextConfigurationUtilsTests.EMPTY_STRING_ARRAY);
    }

    @Test
    public void resolveActiveProfilesWithDuplicatedProfiles() {
        assertResolvedProfiles(ActiveProfilesUtilsTests.DuplicatedProfiles.class, "foo", "bar", "baz");
    }

    @Test
    public void resolveActiveProfilesWithLocalAndInheritedDuplicatedProfiles() {
        assertResolvedProfiles(ActiveProfilesUtilsTests.ExtendedDuplicatedProfiles.class, "foo", "bar", "baz", "cat", "dog");
    }

    @Test
    public void resolveActiveProfilesWithLocalAnnotation() {
        assertResolvedProfiles(AbstractContextConfigurationUtilsTests.LocationsFoo.class, "foo");
    }

    @Test
    public void resolveActiveProfilesWithInheritedAnnotationAndLocations() {
        assertResolvedProfiles(ActiveProfilesUtilsTests.InheritedLocationsFoo.class, "foo");
    }

    @Test
    public void resolveActiveProfilesWithInheritedAnnotationAndClasses() {
        assertResolvedProfiles(ActiveProfilesUtilsTests.InheritedClassesFoo.class, "foo");
    }

    @Test
    public void resolveActiveProfilesWithLocalAndInheritedAnnotations() {
        assertResolvedProfiles(AbstractContextConfigurationUtilsTests.LocationsBar.class, "foo", "bar");
    }

    @Test
    public void resolveActiveProfilesWithOverriddenAnnotation() {
        assertResolvedProfiles(ActiveProfilesUtilsTests.Animals.class, "dog", "cat");
    }

    /**
     *
     *
     * @since 4.0
     */
    @Test
    public void resolveActiveProfilesWithMetaAnnotation() {
        assertResolvedProfiles(AbstractContextConfigurationUtilsTests.MetaLocationsFoo.class, "foo");
    }

    /**
     *
     *
     * @since 4.0
     */
    @Test
    public void resolveActiveProfilesWithMetaAnnotationAndOverrides() {
        assertResolvedProfiles(AbstractContextConfigurationUtilsTests.MetaLocationsFooWithOverrides.class, "foo");
    }

    /**
     *
     *
     * @since 4.0
     */
    @Test
    public void resolveActiveProfilesWithMetaAnnotationAndOverriddenAttributes() {
        assertResolvedProfiles(AbstractContextConfigurationUtilsTests.MetaLocationsFooWithOverriddenAttributes.class, "foo1", "foo2");
    }

    /**
     *
     *
     * @since 4.0
     */
    @Test
    public void resolveActiveProfilesWithLocalAndInheritedMetaAnnotations() {
        assertResolvedProfiles(AbstractContextConfigurationUtilsTests.MetaLocationsBar.class, "foo", "bar");
    }

    /**
     *
     *
     * @since 4.0
     */
    @Test
    public void resolveActiveProfilesWithOverriddenMetaAnnotation() {
        assertResolvedProfiles(ActiveProfilesUtilsTests.MetaAnimals.class, "dog", "cat");
    }

    /**
     *
     *
     * @since 4.0
     */
    @Test
    public void resolveActiveProfilesWithResolver() {
        assertResolvedProfiles(ActiveProfilesUtilsTests.FooActiveProfilesResolverTestCase.class, "foo");
    }

    /**
     *
     *
     * @since 4.0
     */
    @Test
    public void resolveActiveProfilesWithInheritedResolver() {
        assertResolvedProfiles(ActiveProfilesUtilsTests.InheritedFooActiveProfilesResolverTestCase.class, "foo");
    }

    /**
     *
     *
     * @since 4.0
     */
    @Test
    public void resolveActiveProfilesWithMergedInheritedResolver() {
        assertResolvedProfiles(ActiveProfilesUtilsTests.MergedInheritedFooActiveProfilesResolverTestCase.class, "foo", "bar");
    }

    /**
     *
     *
     * @since 4.0
     */
    @Test
    public void resolveActiveProfilesWithOverridenInheritedResolver() {
        assertResolvedProfiles(ActiveProfilesUtilsTests.OverridenInheritedFooActiveProfilesResolverTestCase.class, "bar");
    }

    /**
     *
     *
     * @since 4.0
     */
    @Test
    public void resolveActiveProfilesWithResolverAndProfiles() {
        assertResolvedProfiles(ActiveProfilesUtilsTests.ResolverAndProfilesTestCase.class, "bar");
    }

    /**
     *
     *
     * @since 4.0
     */
    @Test
    public void resolveActiveProfilesWithResolverAndValue() {
        assertResolvedProfiles(ActiveProfilesUtilsTests.ResolverAndValueTestCase.class, "bar");
    }

    /**
     *
     *
     * @since 4.0
     */
    @Test(expected = AnnotationConfigurationException.class)
    public void resolveActiveProfilesWithConflictingProfilesAndValue() {
        resolveActiveProfiles(ActiveProfilesUtilsTests.ConflictingProfilesAndValueTestCase.class);
    }

    /**
     *
     *
     * @since 4.0
     */
    @Test(expected = IllegalStateException.class)
    public void resolveActiveProfilesWithResolverWithoutDefaultConstructor() {
        resolveActiveProfiles(ActiveProfilesUtilsTests.NoDefaultConstructorActiveProfilesResolverTestCase.class);
    }

    /**
     * This test verifies that the actual test class, not the composed annotation,
     * is passed to the resolver.
     *
     * @since 4.0.3
     */
    @Test
    public void resolveActiveProfilesWithMetaAnnotationAndTestClassVerifyingResolver() {
        Class<ActiveProfilesUtilsTests.TestClassVerifyingActiveProfilesResolverTestCase> testClass = ActiveProfilesUtilsTests.TestClassVerifyingActiveProfilesResolverTestCase.class;
        assertResolvedProfiles(testClass, testClass.getSimpleName());
    }

    /**
     * This test verifies that {@link DefaultActiveProfilesResolver} can be declared explicitly.
     *
     * @since 4.1.5
     */
    @Test
    public void resolveActiveProfilesWithDefaultActiveProfilesResolver() {
        assertResolvedProfiles(ActiveProfilesUtilsTests.DefaultActiveProfilesResolverTestCase.class, "default");
    }

    /**
     * This test verifies that {@link DefaultActiveProfilesResolver} can be extended.
     *
     * @since 4.1.5
     */
    @Test
    public void resolveActiveProfilesWithExtendedDefaultActiveProfilesResolver() {
        assertResolvedProfiles(ActiveProfilesUtilsTests.ExtendedDefaultActiveProfilesResolverTestCase.class, "default", "foo");
    }

    // -------------------------------------------------------------------------
    @ActiveProfiles({ "    ", "\t" })
    private static class EmptyProfiles {}

    @ActiveProfiles({ "foo", "bar", "  foo", "bar  ", "baz" })
    private static class DuplicatedProfiles {}

    @ActiveProfiles({ "cat", "dog", "  foo", "bar  ", "cat" })
    private static class ExtendedDuplicatedProfiles extends ActiveProfilesUtilsTests.DuplicatedProfiles {}

    @ActiveProfiles(profiles = { "dog", "cat" }, inheritProfiles = false)
    private static class Animals extends AbstractContextConfigurationUtilsTests.LocationsBar {}

    @ActiveProfiles(profiles = { "dog", "cat" }, inheritProfiles = false)
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    private static @interface MetaAnimalsConfig {}

    @ActiveProfiles(resolver = ActiveProfilesUtilsTests.TestClassVerifyingActiveProfilesResolver.class)
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    private static @interface MetaResolverConfig {}

    @ActiveProfilesUtilsTests.MetaAnimalsConfig
    private static class MetaAnimals extends AbstractContextConfigurationUtilsTests.MetaLocationsBar {}

    private static class InheritedLocationsFoo extends AbstractContextConfigurationUtilsTests.LocationsFoo {}

    private static class InheritedClassesFoo extends AbstractContextConfigurationUtilsTests.ClassesFoo {}

    @ActiveProfiles(resolver = ActiveProfilesUtilsTests.NullActiveProfilesResolver.class)
    private static class NullActiveProfilesResolverTestCase {}

    @ActiveProfiles(resolver = ActiveProfilesUtilsTests.NoDefaultConstructorActiveProfilesResolver.class)
    private static class NoDefaultConstructorActiveProfilesResolverTestCase {}

    @ActiveProfiles(resolver = ActiveProfilesUtilsTests.FooActiveProfilesResolver.class)
    private static class FooActiveProfilesResolverTestCase {}

    private static class InheritedFooActiveProfilesResolverTestCase extends ActiveProfilesUtilsTests.FooActiveProfilesResolverTestCase {}

    @ActiveProfiles(resolver = ActiveProfilesUtilsTests.BarActiveProfilesResolver.class)
    private static class MergedInheritedFooActiveProfilesResolverTestCase extends ActiveProfilesUtilsTests.InheritedFooActiveProfilesResolverTestCase {}

    @ActiveProfiles(resolver = ActiveProfilesUtilsTests.BarActiveProfilesResolver.class, inheritProfiles = false)
    private static class OverridenInheritedFooActiveProfilesResolverTestCase extends ActiveProfilesUtilsTests.InheritedFooActiveProfilesResolverTestCase {}

    @ActiveProfiles(resolver = ActiveProfilesUtilsTests.BarActiveProfilesResolver.class, profiles = "ignored by custom resolver")
    private static class ResolverAndProfilesTestCase {}

    @ActiveProfiles(resolver = ActiveProfilesUtilsTests.BarActiveProfilesResolver.class, value = "ignored by custom resolver")
    private static class ResolverAndValueTestCase {}

    @ActiveProfilesUtilsTests.MetaResolverConfig
    private static class TestClassVerifyingActiveProfilesResolverTestCase {}

    @ActiveProfiles(profiles = "default", resolver = DefaultActiveProfilesResolver.class)
    private static class DefaultActiveProfilesResolverTestCase {}

    @ActiveProfiles(profiles = "default", resolver = ActiveProfilesUtilsTests.ExtendedDefaultActiveProfilesResolver.class)
    private static class ExtendedDefaultActiveProfilesResolverTestCase {}

    @ActiveProfiles(profiles = "conflict 1", value = "conflict 2")
    private static class ConflictingProfilesAndValueTestCase {}

    private static class FooActiveProfilesResolver implements ActiveProfilesResolver {
        @Override
        public String[] resolve(Class<?> testClass) {
            return new String[]{ "foo" };
        }
    }

    private static class BarActiveProfilesResolver implements ActiveProfilesResolver {
        @Override
        public String[] resolve(Class<?> testClass) {
            return new String[]{ "bar" };
        }
    }

    private static class NullActiveProfilesResolver implements ActiveProfilesResolver {
        @Override
        public String[] resolve(Class<?> testClass) {
            return null;
        }
    }

    private static class NoDefaultConstructorActiveProfilesResolver implements ActiveProfilesResolver {
        @SuppressWarnings("unused")
        NoDefaultConstructorActiveProfilesResolver(Object argument) {
        }

        @Override
        public String[] resolve(Class<?> testClass) {
            return null;
        }
    }

    private static class TestClassVerifyingActiveProfilesResolver implements ActiveProfilesResolver {
        @Override
        public String[] resolve(Class<?> testClass) {
            return testClass.isAnnotation() ? new String[]{ "@" + (testClass.getSimpleName()) } : new String[]{ testClass.getSimpleName() };
        }
    }

    private static class ExtendedDefaultActiveProfilesResolver extends DefaultActiveProfilesResolver {
        @Override
        public String[] resolve(Class<?> testClass) {
            List<String> profiles = new java.util.ArrayList(Arrays.asList(super.resolve(testClass)));
            profiles.add("foo");
            return StringUtils.toStringArray(profiles);
        }
    }
}

