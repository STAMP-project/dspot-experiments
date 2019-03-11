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
package org.sonar.application;


import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.application.config.AppSettings;
import org.sonar.application.config.AppSettingsLoader;
import org.sonar.application.config.TestAppSettings;


public class AppReloaderImplTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private AppSettingsLoader settingsLoader = Mockito.mock(AppSettingsLoader.class);

    private FileSystem fs = Mockito.mock(FileSystem.class);

    private AppState state = Mockito.mock(AppState.class);

    private AppLogging logging = Mockito.mock(AppLogging.class);

    private AppReloaderImpl underTest = new AppReloaderImpl(settingsLoader, fs, state, logging);

    @Test
    public void reload_configuration_then_reset_all() throws IOException {
        AppSettings settings = new TestAppSettings().set("foo", "bar");
        AppSettings newSettings = new TestAppSettings().set("foo", "newBar").set("newProp", "newVal");
        Mockito.when(settingsLoader.load()).thenReturn(newSettings);
        underTest.reload(settings);
        assertThat(settings.getProps().rawProperties()).contains(entry("foo", "newBar")).contains(entry("newProp", "newVal"));
        Mockito.verify(logging).configure();
        Mockito.verify(state).reset();
        Mockito.verify(fs).reset();
    }

    @Test
    public void throw_ISE_if_cluster_is_enabled() throws IOException {
        AppSettings settings = new TestAppSettings().set(CLUSTER_ENABLED.getKey(), "true");
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Restart is not possible with cluster mode");
        underTest.reload(settings);
        Mockito.verifyZeroInteractions(logging);
        Mockito.verifyZeroInteractions(state);
        Mockito.verifyZeroInteractions(fs);
    }

    @Test
    public void throw_MessageException_if_path_properties_are_changed() throws IOException {
        verifyFailureIfPropertyValueChanged(PATH_DATA.getKey());
        verifyFailureIfPropertyValueChanged(PATH_LOGS.getKey());
        verifyFailureIfPropertyValueChanged(PATH_TEMP.getKey());
        verifyFailureIfPropertyValueChanged(PATH_WEB.getKey());
    }

    @Test
    public void throw_MessageException_if_cluster_mode_changed() throws IOException {
        verifyFailureIfPropertyValueChanged(CLUSTER_ENABLED.getKey());
    }
}

