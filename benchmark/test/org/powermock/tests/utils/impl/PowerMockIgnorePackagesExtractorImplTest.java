/**
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.powermock.tests.utils.impl;


import org.junit.Test;
import org.powermock.configuration.Configuration;
import org.powermock.configuration.ConfigurationFactory;
import org.powermock.configuration.GlobalConfiguration;
import org.powermock.configuration.PowerMockConfiguration;
import org.powermock.core.classloader.annotations.PowerMockIgnore;


@SuppressWarnings("unchecked")
public class PowerMockIgnorePackagesExtractorImplTest {
    private PowerMockIgnorePackagesExtractorImpl objectUnderTest;

    @Test
    public void should_find_ignore_packages_in_the_whole_class_hierarchy() throws Exception {
        final String[] values = new String[]{ "ignore0", "ignore1", "ignore2", "ignore3" };
        final String[] expectedValues = calculateExpectedValues(values);
        final String[] packagesToIgnore = objectUnderTest.getPackagesToIgnore(PowerMockIgnorePackagesExtractorImplTest.IgnoreAnnotatedDemoClass.class);
        assertThat(packagesToIgnore).as("Packages added to ignore").hasSize(expectedValues.length).containsExactlyInAnyOrder(expectedValues);
    }

    @Test
    public void should_scan_interfaces_when_search_package_to_ignore() {
        final String[] values = new String[]{ "ignore4", "ignore5", "ignore6" };
        String[] expected = calculateExpectedValues(values);
        final String[] packagesToIgnore = objectUnderTest.getPackagesToIgnore(PowerMockIgnorePackagesExtractorImplTest.IgnoreAnnotationFromInterfaces.class);
        assertThat(packagesToIgnore).as("Packages from interfaces added to ignore").hasSize(expected.length).contains(expected);
    }

    @Test
    public void should_include_global_powermock_ignore_to_list_of_package_to_ignore() {
        final String[] globalIgnore = new String[]{ "org.somepacakge.*", "org.otherpackage.Class" };
        GlobalConfiguration.setConfigurationFactory(new ConfigurationFactory() {
            @Override
            public <T extends Configuration<T>> T create(final Class<T> configurationType) {
                PowerMockConfiguration powerMockConfiguration = new PowerMockConfiguration();
                powerMockConfiguration.setGlobalIgnore(globalIgnore);
                return ((T) (powerMockConfiguration));
            }
        });
        String[] packagesToIgnore = objectUnderTest.getPackagesToIgnore(PowerMockIgnorePackagesExtractorImplTest.ClassWithoutAnnotation.class);
        assertThat(packagesToIgnore).as("Packages from configuration is added to ignore").hasSize(2).containsOnly(globalIgnore);
    }

    @Test
    public void should_not_include_global_powermock_ignore_when_annotation_use_global_ignore_false() {
        final String[] globalIgnore = new String[]{ "org.somepacakge.*", "org.otherpackage.Class" };
        GlobalConfiguration.setConfigurationFactory(new ConfigurationFactory() {
            @Override
            public <T extends Configuration<T>> T create(final Class<T> configurationType) {
                PowerMockConfiguration powerMockConfiguration = new PowerMockConfiguration();
                powerMockConfiguration.setGlobalIgnore(globalIgnore);
                return ((T) (powerMockConfiguration));
            }
        });
        String[] packagesToIgnore = objectUnderTest.getPackagesToIgnore(PowerMockIgnorePackagesExtractorImplTest.ClassWithAnnotationUseFalse.class);
        assertThat(packagesToIgnore).as("Packages from global ignore is not added").hasSize(2).containsOnly("ignore6", "ignore5");
    }

    @Test
    public void should_not_include_global_powermock_ignore_when_annotation_use_global_ignore_false_on_parent_class() {
        final String[] globalIgnore = new String[]{ "org.somepacakge.*", "org.otherpackage.Class" };
        GlobalConfiguration.setConfigurationFactory(new ConfigurationFactory() {
            @Override
            public <T extends Configuration<T>> T create(final Class<T> configurationType) {
                PowerMockConfiguration powerMockConfiguration = new PowerMockConfiguration();
                powerMockConfiguration.setGlobalIgnore(globalIgnore);
                return ((T) (powerMockConfiguration));
            }
        });
        String[] packagesToIgnore = objectUnderTest.getPackagesToIgnore(PowerMockIgnorePackagesExtractorImplTest.IgnoreAnnotatedWithGlobalIgnoreParent.class);
        assertThat(packagesToIgnore).as("Packages from global ignore is not added").hasSize(4).containsOnly("ignore0", "ignore1", "ignore6", "ignore5");
    }

    private static class ClassWithoutAnnotation {}

    @PowerMockIgnore({ "ignore0", "ignore1" })
    private class IgnoreAnnotatedDemoClass extends PowerMockIgnorePackagesExtractorImplTest.IgnoreAnnotatedDemoClassParent {}

    @PowerMockIgnore("ignore2")
    private class IgnoreAnnotatedDemoClassParent extends PowerMockIgnorePackagesExtractorImplTest.IgnoreAnnotatedDemoClassGrandParent {}

    @PowerMockIgnore("ignore3")
    private class IgnoreAnnotatedDemoClassGrandParent {}

    private static class IgnoreAnnotationFromInterfaces implements PowerMockIgnorePackagesExtractorImplTest.IgnoreAnnotatedDemoInterfaceParent1 , PowerMockIgnorePackagesExtractorImplTest.IgnoreAnnotatedDemoInterfaceParent2 {}

    @PowerMockIgnore("ignore5")
    private interface IgnoreAnnotatedDemoInterfaceGrandParent {}

    @PowerMockIgnore("ignore4")
    private interface IgnoreAnnotatedDemoInterfaceParent1 extends PowerMockIgnorePackagesExtractorImplTest.IgnoreAnnotatedDemoInterfaceGrandParent {}

    @PowerMockIgnore("ignore6")
    private interface IgnoreAnnotatedDemoInterfaceParent2 extends PowerMockIgnorePackagesExtractorImplTest.IgnoreAnnotatedDemoInterfaceGrandParent {}

    @PowerMockIgnore(value = "ignore6", globalIgnore = false)
    private class ClassWithAnnotationUseFalse implements PowerMockIgnorePackagesExtractorImplTest.IgnoreAnnotatedDemoInterfaceGrandParent {}

    @PowerMockIgnore({ "ignore0", "ignore1" })
    private class IgnoreAnnotatedWithGlobalIgnoreParent extends PowerMockIgnorePackagesExtractorImplTest.ClassWithAnnotationUseFalse {}
}

