/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.core.extension;


import CoreExtensionsLoader.ServiceLoaderWrapper;
import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import java.util.Random;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class CoreExtensionsLoaderTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private CoreExtensionRepository coreExtensionRepository = Mockito.mock(CoreExtensionRepository.class);

    private ServiceLoaderWrapper serviceLoaderWrapper = Mockito.mock(ServiceLoaderWrapper.class);

    private CoreExtensionsLoader underTest = new CoreExtensionsLoader(coreExtensionRepository, serviceLoaderWrapper);

    @Test
    public void load_has_no_effect_if_there_is_no_ServiceLoader_for_CoreExtension_class() {
        Mockito.when(serviceLoaderWrapper.load(ArgumentMatchers.any())).thenReturn(Collections.emptySet());
        underTest.load();
        Mockito.verify(serviceLoaderWrapper).load(CoreExtensionsLoader.class.getClassLoader());
        Mockito.verify(coreExtensionRepository).setLoadedCoreExtensions(Collections.emptySet());
        Mockito.verifyNoMoreInteractions(serviceLoaderWrapper, coreExtensionRepository);
    }

    @Test
    public void load_sets_loaded_core_extensions_into_repository() {
        Set<CoreExtension> coreExtensions = IntStream.range(0, (1 + (new Random().nextInt(5)))).mapToObj(( i) -> CoreExtensionsLoaderTest.newCoreExtension(("core_ext_" + i))).collect(Collectors.toSet());
        Mockito.when(serviceLoaderWrapper.load(ArgumentMatchers.any())).thenReturn(coreExtensions);
        underTest.load();
        Mockito.verify(serviceLoaderWrapper).load(CoreExtensionsLoader.class.getClassLoader());
        Mockito.verify(coreExtensionRepository).setLoadedCoreExtensions(coreExtensions);
        Mockito.verifyNoMoreInteractions(serviceLoaderWrapper, coreExtensionRepository);
    }

    @Test
    public void load_fails_with_ISE_if_multiple_core_extensions_declare_same_name() {
        Set<CoreExtension> coreExtensions = ImmutableSet.of(CoreExtensionsLoaderTest.newCoreExtension("a"), CoreExtensionsLoaderTest.newCoreExtension("a"));
        Mockito.when(serviceLoaderWrapper.load(ArgumentMatchers.any())).thenReturn(coreExtensions);
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Multiple core extensions declare the following names: a");
        underTest.load();
    }

    @Test
    public void load_fails_with_ISE_if_multiple_core_extensions_declare_same_names() {
        Set<CoreExtension> coreExtensions = ImmutableSet.of(CoreExtensionsLoaderTest.newCoreExtension("a"), CoreExtensionsLoaderTest.newCoreExtension("a"), CoreExtensionsLoaderTest.newCoreExtension("b"), CoreExtensionsLoaderTest.newCoreExtension("b"));
        Mockito.when(serviceLoaderWrapper.load(ArgumentMatchers.any())).thenReturn(coreExtensions);
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Multiple core extensions declare the following names: a, b");
        underTest.load();
    }
}

