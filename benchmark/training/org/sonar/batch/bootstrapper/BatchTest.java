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
package org.sonar.batch.bootstrapper;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class BatchTest {
    @Test
    public void testBuilder() {
        Batch batch = newBatch();
        Assert.assertNotNull(batch);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailIfNullComponents() {
        Batch.builder().setEnvironment(new EnvironmentInformation("Gradle", "1.0")).setComponents(null).build();
    }

    @Test
    public void shouldDisableLoggingConfiguration() {
        Batch batch = Batch.builder().setEnvironment(new EnvironmentInformation("Gradle", "1.0")).addComponent("fake").setEnableLoggingConfiguration(false).build();
        Assert.assertNull(batch.getLoggingConfiguration());
    }

    @Test
    public void loggingConfigurationShouldBeEnabledByDefault() {
        Assert.assertNotNull(newBatch().getLoggingConfiguration());
    }

    @Test
    public void shoudSetLogListener() {
        LogOutput logOutput = Mockito.mock(LogOutput.class);
        Batch batch = Batch.builder().setLogOutput(logOutput).build();
        assertThat(batch.getLoggingConfiguration().getLogOutput()).isEqualTo(logOutput);
    }
}

