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
package org.sonar.server.es;


import NewIndex.SettingsConfiguration.Builder;
import java.util.Random;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.api.config.Configuration;

import static SettingsConfiguration.newBuilder;


public class NewIndexSettingsConfigurationTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Configuration mockConfiguration = Mockito.mock(Configuration.class);

    @Test
    public void newBuilder_fails_with_NPE_when_Configuration_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("configuration can't be null");
        newBuilder(null);
    }

    @Test
    public void setDefaultNbOfShards_fails_with_IAE_if_argument_is_zero() {
        NewIndex.SettingsConfiguration.Builder underTest = SettingsConfiguration.newBuilder(mockConfiguration);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("defaultNbOfShards must be >= 1");
        underTest.setDefaultNbOfShards(0);
    }

    @Test
    public void setDefaultNbOfShards_fails_with_IAE_if_argument_is_less_than_zero() {
        NewIndex.SettingsConfiguration.Builder underTest = SettingsConfiguration.newBuilder(mockConfiguration);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("defaultNbOfShards must be >= 1");
        underTest.setDefaultNbOfShards(((-1) - (new Random().nextInt(10))));
    }

    @Test
    public void setDefaultNbOfShards_accepts_1() {
        NewIndex.SettingsConfiguration.Builder underTest = SettingsConfiguration.newBuilder(mockConfiguration);
        assertThat(underTest.setDefaultNbOfShards(1).build().getDefaultNbOfShards()).isEqualTo(1);
    }

    @Test
    public void setDefaultNbOfShards_accepts_any_int_greater_than_1() {
        NewIndex.SettingsConfiguration.Builder underTest = SettingsConfiguration.newBuilder(mockConfiguration);
        int value = 1 + (new Random().nextInt(200));
        assertThat(underTest.setDefaultNbOfShards(value).build().getDefaultNbOfShards()).isEqualTo(value);
    }

    @Test
    public void getDefaultNbOfShards_returns_1_when_not_explicitly_set() {
        assertThat(newBuilder(mockConfiguration).build().getDefaultNbOfShards()).isEqualTo(1);
    }

    @Test
    public void setRefreshInterval_fails_with_IAE_if_argument_is_zero() {
        NewIndex.SettingsConfiguration.Builder underTest = SettingsConfiguration.newBuilder(mockConfiguration);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("refreshInterval must be either -1 or strictly positive");
        underTest.setRefreshInterval(0);
    }

    @Test
    public void setRefreshInterval_fails_with_IAE_if_argument_is_less_than_minus_1() {
        NewIndex.SettingsConfiguration.Builder underTest = SettingsConfiguration.newBuilder(mockConfiguration);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("refreshInterval must be either -1 or strictly positive");
        underTest.setRefreshInterval(((-2) - (new Random().nextInt(10))));
    }

    @Test
    public void setRefreshInterval_accepts_minus_1() {
        NewIndex.SettingsConfiguration.Builder underTest = SettingsConfiguration.newBuilder(mockConfiguration);
        assertThat(underTest.setRefreshInterval((-1)).build().getRefreshInterval()).isEqualTo((-1));
    }

    @Test
    public void setRefreshInterval_accepts_any_int_greater_than_1() {
        NewIndex.SettingsConfiguration.Builder underTest = SettingsConfiguration.newBuilder(mockConfiguration);
        int value = 1 + (new Random().nextInt(200));
        assertThat(underTest.setRefreshInterval(value).build().getRefreshInterval()).isEqualTo(value);
    }

    @Test
    public void getRefreshInterval_returns_30_when_not_explicitly_set() {
        assertThat(newBuilder(mockConfiguration).build().getRefreshInterval()).isEqualTo(30);
    }
}

