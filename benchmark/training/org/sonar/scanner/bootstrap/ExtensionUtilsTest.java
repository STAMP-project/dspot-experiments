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
package org.sonar.scanner.bootstrap;


import InstantiationStrategy.PER_BATCH;
import InstantiationStrategy.PER_PROJECT;
import org.junit.Test;
import org.sonar.api.batch.InstantiationStrategy;
import org.sonar.api.batch.ScannerSide;
import org.sonar.api.ce.ComputeEngineSide;
import org.sonar.api.server.ServerSide;


public class ExtensionUtilsTest {
    @Test
    public void shouldBeBatchInstantiationStrategy() {
        assertThat(ExtensionUtils.isInstantiationStrategy(ExtensionUtilsTest.ProjectService.class, PER_BATCH)).isFalse();
        assertThat(ExtensionUtils.isInstantiationStrategy(new ExtensionUtilsTest.ProjectService(), PER_BATCH)).isFalse();
        assertThat(ExtensionUtils.isInstantiationStrategy(ExtensionUtilsTest.DefaultService.class, PER_BATCH)).isFalse();
        assertThat(ExtensionUtils.isInstantiationStrategy(new ExtensionUtilsTest.DefaultService(), PER_BATCH)).isFalse();
        assertThat(ExtensionUtils.isInstantiationStrategy(ExtensionUtilsTest.DefaultScannerService.class, PER_BATCH)).isFalse();
        assertThat(ExtensionUtils.isInstantiationStrategy(new ExtensionUtilsTest.DefaultScannerService(), PER_BATCH)).isFalse();
    }

    @Test
    public void shouldBeProjectInstantiationStrategy() {
        assertThat(ExtensionUtils.isInstantiationStrategy(ExtensionUtilsTest.ProjectService.class, PER_PROJECT)).isTrue();
        assertThat(ExtensionUtils.isInstantiationStrategy(new ExtensionUtilsTest.ProjectService(), PER_PROJECT)).isTrue();
        assertThat(ExtensionUtils.isInstantiationStrategy(ExtensionUtilsTest.DefaultService.class, PER_PROJECT)).isTrue();
        assertThat(ExtensionUtils.isInstantiationStrategy(new ExtensionUtilsTest.DefaultService(), PER_PROJECT)).isTrue();
        assertThat(ExtensionUtils.isInstantiationStrategy(ExtensionUtilsTest.DefaultScannerService.class, PER_PROJECT)).isTrue();
        assertThat(ExtensionUtils.isInstantiationStrategy(new ExtensionUtilsTest.DefaultScannerService(), PER_PROJECT)).isTrue();
    }

    @Test
    public void testIsScannerSide() {
        assertThat(ExtensionUtils.isDeprecatedScannerSide(ExtensionUtilsTest.ScannerService.class)).isTrue();
        assertThat(ExtensionUtils.isDeprecatedScannerSide(ExtensionUtilsTest.ServerService.class)).isFalse();
        assertThat(ExtensionUtils.isDeprecatedScannerSide(new ExtensionUtilsTest.ServerService())).isFalse();
        assertThat(ExtensionUtils.isDeprecatedScannerSide(new ExtensionUtilsTest.WebServerService())).isFalse();
        assertThat(ExtensionUtils.isDeprecatedScannerSide(new ExtensionUtilsTest.ComputeEngineService())).isFalse();
    }

    @ScannerSide
    @InstantiationStrategy(InstantiationStrategy.PER_BATCH)
    public static class ScannerService {}

    @ScannerSide
    @InstantiationStrategy(InstantiationStrategy.PER_PROJECT)
    public static class ProjectService {}

    @ScannerSide
    public static class DefaultService {}

    @ScannerSide
    public static class DefaultScannerService {}

    @ServerSide
    public static class ServerService {}

    @ServerSide
    public static class WebServerService {}

    @ComputeEngineSide
    public static class ComputeEngineService {}
}

