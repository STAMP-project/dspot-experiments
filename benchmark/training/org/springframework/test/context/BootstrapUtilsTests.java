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
package org.springframework.test.context;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.springframework.test.context.support.DefaultTestContextBootstrapper;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.context.web.WebTestContextBootstrapper;


/**
 * Unit tests for {@link BootstrapUtils}.
 *
 * @author Sam Brannen
 * @author Phillip Webb
 * @since 4.2
 */
public class BootstrapUtilsTests {
    private final CacheAwareContextLoaderDelegate delegate = Mockito.mock(CacheAwareContextLoaderDelegate.class);

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void resolveTestContextBootstrapperWithEmptyBootstrapWithAnnotation() {
        BootstrapContext bootstrapContext = BootstrapTestUtils.buildBootstrapContext(BootstrapUtilsTests.EmptyBootstrapWithAnnotationClass.class, delegate);
        exception.expect(IllegalStateException.class);
        exception.expectMessage("Specify @BootstrapWith's 'value' attribute");
        BootstrapUtils.resolveTestContextBootstrapper(bootstrapContext);
    }

    @Test
    public void resolveTestContextBootstrapperWithDoubleMetaBootstrapWithAnnotations() {
        BootstrapContext bootstrapContext = BootstrapTestUtils.buildBootstrapContext(BootstrapUtilsTests.DoubleMetaAnnotatedBootstrapWithAnnotationClass.class, delegate);
        exception.expect(IllegalStateException.class);
        exception.expectMessage("Configuration error: found multiple declarations of @BootstrapWith");
        exception.expectMessage(BootstrapUtilsTests.FooBootstrapper.class.getName());
        exception.expectMessage(BootstrapUtilsTests.BarBootstrapper.class.getName());
        BootstrapUtils.resolveTestContextBootstrapper(bootstrapContext);
    }

    @Test
    public void resolveTestContextBootstrapperForNonAnnotatedClass() {
        assertBootstrapper(BootstrapUtilsTests.NonAnnotatedClass.class, DefaultTestContextBootstrapper.class);
    }

    @Test
    public void resolveTestContextBootstrapperForWebAppConfigurationAnnotatedClass() {
        assertBootstrapper(BootstrapUtilsTests.WebAppConfigurationAnnotatedClass.class, WebTestContextBootstrapper.class);
    }

    @Test
    public void resolveTestContextBootstrapperWithDirectBootstrapWithAnnotation() {
        assertBootstrapper(BootstrapUtilsTests.DirectBootstrapWithAnnotationClass.class, BootstrapUtilsTests.FooBootstrapper.class);
    }

    @Test
    public void resolveTestContextBootstrapperWithInheritedBootstrapWithAnnotation() {
        assertBootstrapper(BootstrapUtilsTests.InheritedBootstrapWithAnnotationClass.class, BootstrapUtilsTests.FooBootstrapper.class);
    }

    @Test
    public void resolveTestContextBootstrapperWithMetaBootstrapWithAnnotation() {
        assertBootstrapper(BootstrapUtilsTests.MetaAnnotatedBootstrapWithAnnotationClass.class, BootstrapUtilsTests.BarBootstrapper.class);
    }

    @Test
    public void resolveTestContextBootstrapperWithDuplicatingMetaBootstrapWithAnnotations() {
        assertBootstrapper(BootstrapUtilsTests.DuplicateMetaAnnotatedBootstrapWithAnnotationClass.class, BootstrapUtilsTests.FooBootstrapper.class);
    }

    /**
     *
     *
     * @since 5.1
     */
    @Test
    public void resolveTestContextBootstrapperWithLocalDeclarationThatOverridesMetaBootstrapWithAnnotations() {
        assertBootstrapper(BootstrapUtilsTests.LocalDeclarationAndMetaAnnotatedBootstrapWithAnnotationClass.class, BootstrapUtilsTests.EnigmaBootstrapper.class);
    }

    // -------------------------------------------------------------------
    static class FooBootstrapper extends DefaultTestContextBootstrapper {}

    static class BarBootstrapper extends DefaultTestContextBootstrapper {}

    static class EnigmaBootstrapper extends DefaultTestContextBootstrapper {}

    @BootstrapWith(BootstrapUtilsTests.FooBootstrapper.class)
    @Retention(RetentionPolicy.RUNTIME)
    @interface BootWithFoo {}

    @BootstrapWith(BootstrapUtilsTests.FooBootstrapper.class)
    @Retention(RetentionPolicy.RUNTIME)
    @interface BootWithFooAgain {}

    @BootstrapWith(BootstrapUtilsTests.BarBootstrapper.class)
    @Retention(RetentionPolicy.RUNTIME)
    @interface BootWithBar {}

    // Invalid
    @BootstrapWith
    static class EmptyBootstrapWithAnnotationClass {}

    // Invalid
    @BootstrapUtilsTests.BootWithBar
    @BootstrapUtilsTests.BootWithFoo
    static class DoubleMetaAnnotatedBootstrapWithAnnotationClass {}

    static class NonAnnotatedClass {}

    @BootstrapWith(BootstrapUtilsTests.FooBootstrapper.class)
    static class DirectBootstrapWithAnnotationClass {}

    static class InheritedBootstrapWithAnnotationClass extends BootstrapUtilsTests.DirectBootstrapWithAnnotationClass {}

    @BootstrapUtilsTests.BootWithBar
    static class MetaAnnotatedBootstrapWithAnnotationClass {}

    @BootstrapUtilsTests.BootWithFoo
    @BootstrapUtilsTests.BootWithFooAgain
    static class DuplicateMetaAnnotatedBootstrapWithAnnotationClass {}

    @BootstrapUtilsTests.BootWithFoo
    @BootstrapUtilsTests.BootWithBar
    @BootstrapWith(BootstrapUtilsTests.EnigmaBootstrapper.class)
    static class LocalDeclarationAndMetaAnnotatedBootstrapWithAnnotationClass {}

    @WebAppConfiguration
    static class WebAppConfigurationAnnotatedClass {}
}

