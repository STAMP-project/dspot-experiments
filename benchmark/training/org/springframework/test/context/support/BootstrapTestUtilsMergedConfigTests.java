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
package org.springframework.test.context.support;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextLoader;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.web.WebDelegatingSmartContextLoader;
import org.springframework.test.context.web.WebMergedContextConfiguration;


/**
 * Unit tests for {@link BootstrapTestUtils} involving {@link MergedContextConfiguration}.
 *
 * @author Sam Brannen
 * @since 3.1
 */
public class BootstrapTestUtilsMergedConfigTests extends AbstractContextConfigurationUtilsTests {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void buildImplicitMergedConfigWithoutAnnotation() {
        Class<?> testClass = AbstractContextConfigurationUtilsTests.Enigma.class;
        MergedContextConfiguration mergedConfig = buildMergedContextConfiguration(testClass);
        assertMergedConfig(mergedConfig, testClass, AbstractContextConfigurationUtilsTests.EMPTY_STRING_ARRAY, AbstractContextConfigurationUtilsTests.EMPTY_CLASS_ARRAY, DelegatingSmartContextLoader.class);
    }

    /**
     *
     *
     * @since 4.3
     */
    @Test
    public void buildMergedConfigWithContextConfigurationWithoutLocationsClassesOrInitializers() {
        exception.expect(IllegalStateException.class);
        exception.expectMessage(CoreMatchers.startsWith(("DelegatingSmartContextLoader was unable to detect defaults, " + "and no ApplicationContextInitializers or ContextCustomizers were declared for context configuration attributes")));
        buildMergedContextConfiguration(BootstrapTestUtilsMergedConfigTests.MissingContextAttributesTestCase.class);
    }

    @Test
    public void buildMergedConfigWithBareAnnotations() {
        Class<?> testClass = AbstractContextConfigurationUtilsTests.BareAnnotations.class;
        MergedContextConfiguration mergedConfig = buildMergedContextConfiguration(testClass);
        assertMergedConfig(mergedConfig, testClass, AbstractContextConfigurationUtilsTests.array("classpath:org/springframework/test/context/support/AbstractContextConfigurationUtilsTests$BareAnnotations-context.xml"), AbstractContextConfigurationUtilsTests.EMPTY_CLASS_ARRAY, DelegatingSmartContextLoader.class);
    }

    @Test
    public void buildMergedConfigWithLocalAnnotationAndLocations() {
        Class<?> testClass = AbstractContextConfigurationUtilsTests.LocationsFoo.class;
        MergedContextConfiguration mergedConfig = buildMergedContextConfiguration(testClass);
        assertMergedConfig(mergedConfig, testClass, AbstractContextConfigurationUtilsTests.array("classpath:/foo.xml"), AbstractContextConfigurationUtilsTests.EMPTY_CLASS_ARRAY, DelegatingSmartContextLoader.class);
    }

    @Test
    public void buildMergedConfigWithMetaAnnotationAndLocations() {
        Class<?> testClass = AbstractContextConfigurationUtilsTests.MetaLocationsFoo.class;
        MergedContextConfiguration mergedConfig = buildMergedContextConfiguration(testClass);
        assertMergedConfig(mergedConfig, testClass, AbstractContextConfigurationUtilsTests.array("classpath:/foo.xml"), AbstractContextConfigurationUtilsTests.EMPTY_CLASS_ARRAY, DelegatingSmartContextLoader.class);
    }

    @Test
    public void buildMergedConfigWithMetaAnnotationAndClasses() {
        buildMergedConfigWithMetaAnnotationAndClasses(BootstrapTestUtilsMergedConfigTests.Dog.class);
        buildMergedConfigWithMetaAnnotationAndClasses(BootstrapTestUtilsMergedConfigTests.WorkingDog.class);
        buildMergedConfigWithMetaAnnotationAndClasses(BootstrapTestUtilsMergedConfigTests.GermanShepherd.class);
    }

    @Test
    public void buildMergedConfigWithLocalAnnotationAndClasses() {
        Class<?> testClass = AbstractContextConfigurationUtilsTests.ClassesFoo.class;
        MergedContextConfiguration mergedConfig = buildMergedContextConfiguration(testClass);
        assertMergedConfig(mergedConfig, testClass, AbstractContextConfigurationUtilsTests.EMPTY_STRING_ARRAY, AbstractContextConfigurationUtilsTests.array(AbstractContextConfigurationUtilsTests.FooConfig.class), DelegatingSmartContextLoader.class);
    }

    /**
     * Introduced to investigate claims made in a discussion on
     * <a href="http://stackoverflow.com/questions/24725438/what-could-cause-a-class-implementing-applicationlistenercontextrefreshedevent">Stack Overflow</a>.
     */
    @Test
    public void buildMergedConfigWithAtWebAppConfigurationWithAnnotationAndClassesOnSuperclass() {
        Class<?> webTestClass = AbstractContextConfigurationUtilsTests.WebClassesFoo.class;
        Class<?> standardTestClass = AbstractContextConfigurationUtilsTests.ClassesFoo.class;
        WebMergedContextConfiguration webMergedConfig = ((WebMergedContextConfiguration) (buildMergedContextConfiguration(webTestClass)));
        MergedContextConfiguration standardMergedConfig = buildMergedContextConfiguration(standardTestClass);
        Assert.assertEquals(webMergedConfig, webMergedConfig);
        Assert.assertEquals(standardMergedConfig, standardMergedConfig);
        Assert.assertNotEquals(standardMergedConfig, webMergedConfig);
        Assert.assertNotEquals(webMergedConfig, standardMergedConfig);
        assertMergedConfig(webMergedConfig, webTestClass, AbstractContextConfigurationUtilsTests.EMPTY_STRING_ARRAY, AbstractContextConfigurationUtilsTests.array(AbstractContextConfigurationUtilsTests.FooConfig.class), WebDelegatingSmartContextLoader.class);
        assertMergedConfig(standardMergedConfig, standardTestClass, AbstractContextConfigurationUtilsTests.EMPTY_STRING_ARRAY, AbstractContextConfigurationUtilsTests.array(AbstractContextConfigurationUtilsTests.FooConfig.class), DelegatingSmartContextLoader.class);
    }

    @Test
    public void buildMergedConfigWithLocalAnnotationAndOverriddenContextLoaderAndLocations() {
        Class<?> testClass = AbstractContextConfigurationUtilsTests.PropertiesLocationsFoo.class;
        Class<? extends ContextLoader> expectedContextLoaderClass = GenericPropertiesContextLoader.class;
        MergedContextConfiguration mergedConfig = buildMergedContextConfiguration(testClass);
        assertMergedConfig(mergedConfig, testClass, AbstractContextConfigurationUtilsTests.array("classpath:/foo.properties"), AbstractContextConfigurationUtilsTests.EMPTY_CLASS_ARRAY, expectedContextLoaderClass);
    }

    @Test
    public void buildMergedConfigWithLocalAnnotationAndOverriddenContextLoaderAndClasses() {
        Class<?> testClass = AbstractContextConfigurationUtilsTests.PropertiesClassesFoo.class;
        Class<? extends ContextLoader> expectedContextLoaderClass = GenericPropertiesContextLoader.class;
        MergedContextConfiguration mergedConfig = buildMergedContextConfiguration(testClass);
        assertMergedConfig(mergedConfig, testClass, AbstractContextConfigurationUtilsTests.EMPTY_STRING_ARRAY, AbstractContextConfigurationUtilsTests.array(AbstractContextConfigurationUtilsTests.FooConfig.class), expectedContextLoaderClass);
    }

    @Test
    public void buildMergedConfigWithLocalAndInheritedAnnotationsAndLocations() {
        Class<?> testClass = AbstractContextConfigurationUtilsTests.LocationsBar.class;
        String[] expectedLocations = AbstractContextConfigurationUtilsTests.array("/foo.xml", "/bar.xml");
        MergedContextConfiguration mergedConfig = buildMergedContextConfiguration(testClass);
        assertMergedConfig(mergedConfig, testClass, expectedLocations, AbstractContextConfigurationUtilsTests.EMPTY_CLASS_ARRAY, AnnotationConfigContextLoader.class);
    }

    @Test
    public void buildMergedConfigWithLocalAndInheritedAnnotationsAndClasses() {
        Class<?> testClass = AbstractContextConfigurationUtilsTests.ClassesBar.class;
        Class<?>[] expectedClasses = AbstractContextConfigurationUtilsTests.array(AbstractContextConfigurationUtilsTests.FooConfig.class, AbstractContextConfigurationUtilsTests.BarConfig.class);
        MergedContextConfiguration mergedConfig = buildMergedContextConfiguration(testClass);
        assertMergedConfig(mergedConfig, testClass, AbstractContextConfigurationUtilsTests.EMPTY_STRING_ARRAY, expectedClasses, AnnotationConfigContextLoader.class);
    }

    @Test
    public void buildMergedConfigWithAnnotationsAndOverriddenLocations() {
        Class<?> testClass = AbstractContextConfigurationUtilsTests.OverriddenLocationsBar.class;
        String[] expectedLocations = AbstractContextConfigurationUtilsTests.array("/bar.xml");
        MergedContextConfiguration mergedConfig = buildMergedContextConfiguration(testClass);
        assertMergedConfig(mergedConfig, testClass, expectedLocations, AbstractContextConfigurationUtilsTests.EMPTY_CLASS_ARRAY, AnnotationConfigContextLoader.class);
    }

    @Test
    public void buildMergedConfigWithAnnotationsAndOverriddenClasses() {
        Class<?> testClass = AbstractContextConfigurationUtilsTests.OverriddenClassesBar.class;
        Class<?>[] expectedClasses = AbstractContextConfigurationUtilsTests.array(AbstractContextConfigurationUtilsTests.BarConfig.class);
        MergedContextConfiguration mergedConfig = buildMergedContextConfiguration(testClass);
        assertMergedConfig(mergedConfig, testClass, AbstractContextConfigurationUtilsTests.EMPTY_STRING_ARRAY, expectedClasses, AnnotationConfigContextLoader.class);
    }

    @ContextConfiguration
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public static @interface SpringAppConfig {
        Class<?>[] classes() default {  };
    }

    @BootstrapTestUtilsMergedConfigTests.SpringAppConfig(classes = { AbstractContextConfigurationUtilsTests.FooConfig.class, AbstractContextConfigurationUtilsTests.BarConfig.class })
    public abstract static class Dog {}

    public abstract static class WorkingDog extends BootstrapTestUtilsMergedConfigTests.Dog {}

    public static class GermanShepherd extends BootstrapTestUtilsMergedConfigTests.WorkingDog {}

    @ContextConfiguration
    static class MissingContextAttributesTestCase {}
}

