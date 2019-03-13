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


import CoreExtension.Context;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.stream.Stream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.sonar.api.ExtensionProvider;
import org.sonar.api.Property;
import org.sonar.api.SonarRuntime;
import org.sonar.api.config.Configuration;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.core.platform.ComponentContainer;

import static CoreExtension.Context;


@RunWith(DataProviderRunner.class)
public class CoreExtensionsInstallerTest {
    private SonarRuntime sonarRuntime = Mockito.mock(SonarRuntime.class);

    private CoreExtensionRepository coreExtensionRepository = Mockito.mock(CoreExtensionRepository.class);

    private CoreExtensionsInstaller underTest = new CoreExtensionsInstaller(sonarRuntime, coreExtensionRepository, CoreExtensionsInstallerTest.WestSide.class) {};

    private ArgumentCaptor<CoreExtension.Context> contextCaptor = ArgumentCaptor.forClass(Context.class);

    private static int name_counter = 0;

    @Test
    public void install_has_no_effect_if_CoreExtensionRepository_has_no_loaded_CoreExtension() {
        ComponentContainer container = new ComponentContainer();
        underTest.install(container, CoreExtensionsInstaller.noExtensionFilter(), CoreExtensionsInstaller.noAdditionalSideFilter());
        CoreExtensionsInstallerTest.assertAddedExtensions(container, 0);
    }

    @Test
    public void install_calls_load_method_on_all_loaded_CoreExtension() {
        CoreExtension coreExtension1 = CoreExtensionsInstallerTest.newCoreExtension();
        CoreExtension coreExtension2 = CoreExtensionsInstallerTest.newCoreExtension();
        CoreExtension coreExtension3 = CoreExtensionsInstallerTest.newCoreExtension();
        CoreExtension coreExtension4 = CoreExtensionsInstallerTest.newCoreExtension();
        List<CoreExtension> coreExtensions = ImmutableList.of(coreExtension1, coreExtension2, coreExtension3, coreExtension4);
        InOrder inOrder = Mockito.inOrder(coreExtension1, coreExtension2, coreExtension3, coreExtension4);
        Mockito.when(coreExtensionRepository.loadedCoreExtensions()).thenReturn(coreExtensions.stream());
        ComponentContainer container = new ComponentContainer();
        underTest.install(container, CoreExtensionsInstaller.noExtensionFilter(), CoreExtensionsInstaller.noAdditionalSideFilter());
        inOrder.verify(coreExtension1).load(contextCaptor.capture());
        inOrder.verify(coreExtension2).load(contextCaptor.capture());
        inOrder.verify(coreExtension3).load(contextCaptor.capture());
        inOrder.verify(coreExtension4).load(contextCaptor.capture());
        // verify each core extension gets its own Context
        assertThat(contextCaptor.getAllValues()).hasSameElementsAs(ImmutableSet.copyOf(contextCaptor.getAllValues()));
    }

    @Test
    public void install_provides_runtime_from_constructor_in_context() {
        CoreExtension coreExtension1 = CoreExtensionsInstallerTest.newCoreExtension();
        CoreExtension coreExtension2 = CoreExtensionsInstallerTest.newCoreExtension();
        Mockito.when(coreExtensionRepository.loadedCoreExtensions()).thenReturn(Stream.of(coreExtension1, coreExtension2));
        ComponentContainer container = new ComponentContainer();
        underTest.install(container, CoreExtensionsInstaller.noExtensionFilter(), CoreExtensionsInstaller.noAdditionalSideFilter());
        Mockito.verify(coreExtension1).load(contextCaptor.capture());
        Mockito.verify(coreExtension2).load(contextCaptor.capture());
        assertThat(contextCaptor.getAllValues()).extracting(Context::getRuntime).containsOnly(sonarRuntime);
    }

    @Test
    public void install_provides_new_Configuration_when_getBootConfiguration_is_called_and_there_is_none_in_container() {
        CoreExtension coreExtension1 = CoreExtensionsInstallerTest.newCoreExtension();
        CoreExtension coreExtension2 = CoreExtensionsInstallerTest.newCoreExtension();
        Mockito.when(coreExtensionRepository.loadedCoreExtensions()).thenReturn(Stream.of(coreExtension1, coreExtension2));
        ComponentContainer container = new ComponentContainer();
        underTest.install(container, CoreExtensionsInstaller.noExtensionFilter(), CoreExtensionsInstaller.noAdditionalSideFilter());
        Mockito.verify(coreExtension1).load(contextCaptor.capture());
        Mockito.verify(coreExtension2).load(contextCaptor.capture());
        // verify each core extension gets its own configuration
        assertThat(contextCaptor.getAllValues()).hasSameElementsAs(ImmutableSet.copyOf(contextCaptor.getAllValues()));
    }

    @Test
    public void install_provides_Configuration_from_container_when_getBootConfiguration_is_called() {
        CoreExtension coreExtension1 = CoreExtensionsInstallerTest.newCoreExtension();
        CoreExtension coreExtension2 = CoreExtensionsInstallerTest.newCoreExtension();
        Mockito.when(coreExtensionRepository.loadedCoreExtensions()).thenReturn(Stream.of(coreExtension1, coreExtension2));
        Configuration configuration = new MapSettings().asConfig();
        ComponentContainer container = new ComponentContainer();
        container.add(configuration);
        underTest.install(container, CoreExtensionsInstaller.noExtensionFilter(), CoreExtensionsInstaller.noAdditionalSideFilter());
        Mockito.verify(coreExtension1).load(contextCaptor.capture());
        Mockito.verify(coreExtension2).load(contextCaptor.capture());
        assertThat(contextCaptor.getAllValues()).extracting(Context::getBootConfiguration).containsOnly(configuration);
    }

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface WestSide {}

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface EastSide {}

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface OtherSide {}

    @CoreExtensionsInstallerTest.WestSide
    public static class WestSideClass {}

    @CoreExtensionsInstallerTest.EastSide
    public static class EastSideClass {}

    @CoreExtensionsInstallerTest.OtherSide
    public static class OtherSideClass {}

    @CoreExtensionsInstallerTest.WestSide
    @CoreExtensionsInstallerTest.EastSide
    public static class Latitude {}

    @CoreExtensionsInstallerTest.WestSide
    public static class WestSideProvider extends ExtensionProvider {
        @Override
        public Object provide() {
            return CoreExtensionsInstallerTest.WestSideProvided.class;
        }
    }

    @CoreExtensionsInstallerTest.WestSide
    public static class WestSideProvided {}

    @CoreExtensionsInstallerTest.WestSide
    public static class PartiallyWestSideProvider extends ExtensionProvider {
        @Override
        public Object provide() {
            return CoreExtensionsInstallerTest.NotWestSideProvided.class;
        }
    }

    public static class NotWestSideProvided {}

    @CoreExtensionsInstallerTest.EastSide
    public static class EastSideProvider extends ExtensionProvider {
        @Override
        public Object provide() {
            throw new IllegalStateException("EastSideProvider#provide should not be called");
        }
    }

    @Property(key = "westKey", name = "westName")
    @CoreExtensionsInstallerTest.WestSide
    public static class WestSidePropertyDefinition {}

    @Property(key = "eastKey", name = "eastName")
    @CoreExtensionsInstallerTest.EastSide
    public static class EastSidePropertyDefinition {}

    @Property(key = "otherKey", name = "otherName")
    @CoreExtensionsInstallerTest.OtherSide
    public static class OtherSidePropertyDefinition {}

    @Property(key = "latitudeKey", name = "latitudeName")
    @CoreExtensionsInstallerTest.WestSide
    @CoreExtensionsInstallerTest.EastSide
    public static class LatitudePropertyDefinition {}

    @Property(key = "blankKey", name = "blankName")
    public static class BlankPropertyDefinition {}
}

