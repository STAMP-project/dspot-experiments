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


import org.junit.Test;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.web.context.support.GenericWebApplicationContext;


/**
 * Unit tests for {@link BootstrapTestUtils} involving {@link ApplicationContextInitializer}s.
 *
 * @author Sam Brannen
 * @since 3.1
 */
@SuppressWarnings("unchecked")
public class BootstrapTestUtilsContextInitializerTests extends AbstractContextConfigurationUtilsTests {
    @Test
    public void buildMergedConfigWithSingleLocalInitializer() {
        Class<?> testClass = BootstrapTestUtilsContextInitializerTests.SingleInitializer.class;
        MergedContextConfiguration mergedConfig = buildMergedContextConfiguration(testClass);
        assertMergedConfig(mergedConfig, testClass, AbstractContextConfigurationUtilsTests.EMPTY_STRING_ARRAY, AbstractContextConfigurationUtilsTests.EMPTY_CLASS_ARRAY, initializers(BootstrapTestUtilsContextInitializerTests.FooInitializer.class), DelegatingSmartContextLoader.class);
    }

    @Test
    public void buildMergedConfigWithLocalInitializerAndConfigClass() {
        Class<?> testClass = BootstrapTestUtilsContextInitializerTests.InitializersFoo.class;
        MergedContextConfiguration mergedConfig = buildMergedContextConfiguration(testClass);
        assertMergedConfig(mergedConfig, testClass, AbstractContextConfigurationUtilsTests.EMPTY_STRING_ARRAY, classes(AbstractContextConfigurationUtilsTests.FooConfig.class), initializers(BootstrapTestUtilsContextInitializerTests.FooInitializer.class), DelegatingSmartContextLoader.class);
    }

    @Test
    public void buildMergedConfigWithLocalAndInheritedInitializer() {
        Class<?> testClass = BootstrapTestUtilsContextInitializerTests.InitializersBar.class;
        MergedContextConfiguration mergedConfig = buildMergedContextConfiguration(testClass);
        assertMergedConfig(mergedConfig, testClass, AbstractContextConfigurationUtilsTests.EMPTY_STRING_ARRAY, classes(AbstractContextConfigurationUtilsTests.FooConfig.class, AbstractContextConfigurationUtilsTests.BarConfig.class), initializers(BootstrapTestUtilsContextInitializerTests.FooInitializer.class, BootstrapTestUtilsContextInitializerTests.BarInitializer.class), DelegatingSmartContextLoader.class);
    }

    @Test
    public void buildMergedConfigWithOverriddenInitializers() {
        Class<?> testClass = BootstrapTestUtilsContextInitializerTests.OverriddenInitializersBar.class;
        MergedContextConfiguration mergedConfig = buildMergedContextConfiguration(testClass);
        assertMergedConfig(mergedConfig, testClass, AbstractContextConfigurationUtilsTests.EMPTY_STRING_ARRAY, classes(AbstractContextConfigurationUtilsTests.FooConfig.class, AbstractContextConfigurationUtilsTests.BarConfig.class), initializers(BootstrapTestUtilsContextInitializerTests.BarInitializer.class), DelegatingSmartContextLoader.class);
    }

    @Test
    public void buildMergedConfigWithOverriddenInitializersAndClasses() {
        Class<?> testClass = BootstrapTestUtilsContextInitializerTests.OverriddenInitializersAndClassesBar.class;
        MergedContextConfiguration mergedConfig = buildMergedContextConfiguration(testClass);
        assertMergedConfig(mergedConfig, testClass, AbstractContextConfigurationUtilsTests.EMPTY_STRING_ARRAY, classes(AbstractContextConfigurationUtilsTests.BarConfig.class), initializers(BootstrapTestUtilsContextInitializerTests.BarInitializer.class), DelegatingSmartContextLoader.class);
    }

    private static class FooInitializer implements ApplicationContextInitializer<GenericApplicationContext> {
        @Override
        public void initialize(GenericApplicationContext applicationContext) {
        }
    }

    private static class BarInitializer implements ApplicationContextInitializer<GenericWebApplicationContext> {
        @Override
        public void initialize(GenericWebApplicationContext applicationContext) {
        }
    }

    @ContextConfiguration(initializers = BootstrapTestUtilsContextInitializerTests.FooInitializer.class)
    private static class SingleInitializer {}

    @ContextConfiguration(classes = AbstractContextConfigurationUtilsTests.FooConfig.class, initializers = BootstrapTestUtilsContextInitializerTests.FooInitializer.class)
    private static class InitializersFoo {}

    @ContextConfiguration(classes = AbstractContextConfigurationUtilsTests.BarConfig.class, initializers = BootstrapTestUtilsContextInitializerTests.BarInitializer.class)
    private static class InitializersBar extends BootstrapTestUtilsContextInitializerTests.InitializersFoo {}

    @ContextConfiguration(classes = AbstractContextConfigurationUtilsTests.BarConfig.class, initializers = BootstrapTestUtilsContextInitializerTests.BarInitializer.class, inheritInitializers = false)
    private static class OverriddenInitializersBar extends BootstrapTestUtilsContextInitializerTests.InitializersFoo {}

    @ContextConfiguration(classes = AbstractContextConfigurationUtilsTests.BarConfig.class, inheritLocations = false, initializers = BootstrapTestUtilsContextInitializerTests.BarInitializer.class, inheritInitializers = false)
    private static class OverriddenInitializersAndClassesBar extends BootstrapTestUtilsContextInitializerTests.InitializersFoo {}
}

