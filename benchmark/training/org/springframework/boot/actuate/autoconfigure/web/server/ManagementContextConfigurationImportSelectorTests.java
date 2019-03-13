/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.actuate.autoconfigure.web.server;


import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;
import org.springframework.boot.actuate.autoconfigure.web.ManagementContextConfiguration;
import org.springframework.boot.actuate.autoconfigure.web.ManagementContextType;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.StandardAnnotationMetadata;


/**
 * Tests for {@link ManagementContextConfigurationImportSelector}.
 *
 * @author Phillip Webb
 * @author Andy Wilkinson
 */
public class ManagementContextConfigurationImportSelectorTests {
    @Test
    public void selectImportsShouldOrderResult() {
        String[] imports = new ManagementContextConfigurationImportSelectorTests.TestManagementContextConfigurationsImportSelector(ManagementContextConfigurationImportSelectorTests.C.class, ManagementContextConfigurationImportSelectorTests.A.class, ManagementContextConfigurationImportSelectorTests.D.class, ManagementContextConfigurationImportSelectorTests.B.class).selectImports(new StandardAnnotationMetadata(ManagementContextConfigurationImportSelectorTests.EnableChildContext.class));
        assertThat(imports).containsExactly(ManagementContextConfigurationImportSelectorTests.A.class.getName(), ManagementContextConfigurationImportSelectorTests.B.class.getName(), ManagementContextConfigurationImportSelectorTests.C.class.getName(), ManagementContextConfigurationImportSelectorTests.D.class.getName());
    }

    @Test
    public void selectImportsFiltersChildOnlyConfigurationWhenUsingSameContext() {
        String[] imports = new ManagementContextConfigurationImportSelectorTests.TestManagementContextConfigurationsImportSelector(ManagementContextConfigurationImportSelectorTests.ChildOnly.class, ManagementContextConfigurationImportSelectorTests.SameOnly.class, ManagementContextConfigurationImportSelectorTests.A.class).selectImports(new StandardAnnotationMetadata(ManagementContextConfigurationImportSelectorTests.EnableSameContext.class));
        assertThat(imports).containsExactlyInAnyOrder(ManagementContextConfigurationImportSelectorTests.SameOnly.class.getName(), ManagementContextConfigurationImportSelectorTests.A.class.getName());
    }

    @Test
    public void selectImportsFiltersSameOnlyConfigurationWhenUsingChildContext() {
        String[] imports = new ManagementContextConfigurationImportSelectorTests.TestManagementContextConfigurationsImportSelector(ManagementContextConfigurationImportSelectorTests.ChildOnly.class, ManagementContextConfigurationImportSelectorTests.SameOnly.class, ManagementContextConfigurationImportSelectorTests.A.class).selectImports(new StandardAnnotationMetadata(ManagementContextConfigurationImportSelectorTests.EnableChildContext.class));
        assertThat(imports).containsExactlyInAnyOrder(ManagementContextConfigurationImportSelectorTests.ChildOnly.class.getName(), ManagementContextConfigurationImportSelectorTests.A.class.getName());
    }

    private static final class TestManagementContextConfigurationsImportSelector extends ManagementContextConfigurationImportSelector {
        private final List<String> factoryNames;

        private TestManagementContextConfigurationsImportSelector(Class<?>... classes) {
            this.factoryNames = Stream.of(classes).map(Class::getName).collect(Collectors.toList());
        }

        @Override
        protected List<String> loadFactoryNames() {
            return this.factoryNames;
        }
    }

    @Order(1)
    private static class A {}

    @Order(2)
    private static class B {}

    @Order(3)
    private static class C {}

    static class D {}

    @ManagementContextConfiguration(ManagementContextType.CHILD)
    static class ChildOnly {}

    @ManagementContextConfiguration(ManagementContextType.SAME)
    static class SameOnly {}

    @EnableManagementContext(ManagementContextType.CHILD)
    static class EnableChildContext {}

    @EnableManagementContext(ManagementContextType.SAME)
    static class EnableSameContext {}
}

