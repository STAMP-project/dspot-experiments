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
package org.sonar.scanner.cpd;


import java.util.Optional;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.batch.fs.internal.DefaultInputProject;
import org.sonar.api.config.Configuration;


public class CpdSettingsTest {
    private CpdSettings cpdSettings;

    private Configuration configuration;

    private DefaultInputProject project;

    @Test
    public void defaultMinimumTokens() {
        Mockito.when(configuration.getInt(ArgumentMatchers.anyString())).thenReturn(Optional.empty());
        assertThat(cpdSettings.getMinimumTokens("java")).isEqualTo(100);
    }

    @Test
    public void minimumTokensByLanguage() {
        Mockito.when(configuration.getInt("sonar.cpd.java.minimumTokens")).thenReturn(Optional.of(42));
        Mockito.when(configuration.getInt("sonar.cpd.php.minimumTokens")).thenReturn(Optional.of(33));
        assertThat(cpdSettings.getMinimumTokens("java")).isEqualTo(42);
        assertThat(cpdSettings.getMinimumTokens("php")).isEqualTo(33);
    }
}

