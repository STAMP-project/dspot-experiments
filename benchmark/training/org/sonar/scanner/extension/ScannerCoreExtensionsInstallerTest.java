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
package org.sonar.scanner.extension;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.stream.Stream;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.SonarRuntime;
import org.sonar.api.ce.ComputeEngineSide;
import org.sonar.api.scanner.ScannerSide;
import org.sonar.api.server.ServerSide;
import org.sonar.core.extension.CoreExtension;
import org.sonar.core.extension.CoreExtensionRepository;
import org.sonar.core.platform.ComponentContainer;


public class ScannerCoreExtensionsInstallerTest {
    private SonarRuntime sonarRuntime = Mockito.mock(SonarRuntime.class);

    private CoreExtensionRepository coreExtensionRepository = Mockito.mock(CoreExtensionRepository.class);

    private ScannerCoreExtensionsInstaller underTest = new ScannerCoreExtensionsInstaller(sonarRuntime, coreExtensionRepository);

    @Test
    public void install_only_adds_ScannerSide_annotated_extension_to_container() {
        Mockito.when(coreExtensionRepository.loadedCoreExtensions()).thenReturn(Stream.of(new CoreExtension() {
            @Override
            public String getName() {
                return "foo";
            }

            @Override
            public void load(Context context) {
                context.addExtensions(ScannerCoreExtensionsInstallerTest.CeClass.class, ScannerCoreExtensionsInstallerTest.ScannerClass.class, ScannerCoreExtensionsInstallerTest.WebServerClass.class, ScannerCoreExtensionsInstallerTest.NoAnnotationClass.class, ScannerCoreExtensionsInstallerTest.OtherAnnotationClass.class, ScannerCoreExtensionsInstallerTest.MultipleAnnotationClass.class);
            }
        }));
        ComponentContainer container = new ComponentContainer();
        underTest.install(container, noExtensionFilter(), noAdditionalSideFilter());
        assertThat(container.getPicoContainer().getComponentAdapters()).hasSize(((ComponentContainer.COMPONENTS_IN_EMPTY_COMPONENT_CONTAINER) + 2));
        assertThat(container.getComponentByType(ScannerCoreExtensionsInstallerTest.ScannerClass.class)).isNotNull();
        assertThat(container.getComponentByType(ScannerCoreExtensionsInstallerTest.MultipleAnnotationClass.class)).isNotNull();
    }

    @ComputeEngineSide
    public static final class CeClass {}

    @ServerSide
    public static final class WebServerClass {}

    @ScannerSide
    public static final class ScannerClass {}

    @ServerSide
    @ComputeEngineSide
    @ScannerSide
    public static final class MultipleAnnotationClass {}

    public static final class NoAnnotationClass {}

    @ScannerCoreExtensionsInstallerTest.DarkSide
    public static final class OtherAnnotationClass {}

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface DarkSide {}
}

