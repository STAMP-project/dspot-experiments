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
package org.sonar.ce.configuration;


import java.util.Random;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.config.internal.ConfigurationBridge;
import org.sonar.api.config.internal.MapSettings;


public class CeConfigurationImplTest {
    public static final ConfigurationBridge EMPTY_CONFIGURATION = new ConfigurationBridge(new MapSettings());

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private CeConfigurationImplTest.SimpleWorkerCountProvider workerCountProvider = new CeConfigurationImplTest.SimpleWorkerCountProvider();

    @Test
    public void getWorkerCount_returns_1_when_there_is_no_WorkerCountProvider() {
        assertThat(getWorkerCount()).isEqualTo(1);
    }

    @Test
    public void getWorkerMaxCount_returns_1_when_there_is_no_WorkerCountProvider() {
        assertThat(getWorkerMaxCount()).isEqualTo(1);
    }

    @Test
    public void getWorkerCount_returns_value_returned_by_WorkerCountProvider_when_available() {
        int value = CeConfigurationImplTest.randomValidWorkerCount();
        workerCountProvider.set(value);
        assertThat(getWorkerCount()).isEqualTo(value);
    }

    @Test
    public void getWorkerMaxCount_returns_10_whichever_the_value_returned_by_WorkerCountProvider() {
        int value = CeConfigurationImplTest.randomValidWorkerCount();
        workerCountProvider.set(value);
        assertThat(getWorkerMaxCount()).isEqualTo(10);
    }

    @Test
    public void constructor_throws_MessageException_when_WorkerCountProvider_returns_0() {
        workerCountProvider.set(0);
        expectMessageException(0);
        new CeConfigurationImpl(CeConfigurationImplTest.EMPTY_CONFIGURATION, workerCountProvider);
    }

    @Test
    public void constructor_throws_MessageException_when_WorkerCountProvider_returns_less_than_0() {
        int value = (-1) - (Math.abs(new Random().nextInt()));
        workerCountProvider.set(value);
        expectMessageException(value);
        new CeConfigurationImpl(CeConfigurationImplTest.EMPTY_CONFIGURATION, workerCountProvider);
    }

    @Test
    public void constructor_throws_MessageException_when_WorkerCountProvider_returns_more_then_10() {
        int value = 10 + (Math.abs(new Random().nextInt()));
        workerCountProvider.set(value);
        expectMessageException(value);
        new CeConfigurationImpl(CeConfigurationImplTest.EMPTY_CONFIGURATION, workerCountProvider);
    }

    @Test
    public void getCleanCeTasksInitialDelay_returns_0() {
        assertThat(getCleanCeTasksInitialDelay()).isEqualTo(0L);
        workerCountProvider.set(1);
        assertThat(getCleanCeTasksInitialDelay()).isEqualTo(0L);
    }

    @Test
    public void getCleanCeTasksDelay_returns_2() {
        assertThat(getCleanCeTasksDelay()).isEqualTo(2L);
        workerCountProvider.set(1);
        assertThat(getCleanCeTasksDelay()).isEqualTo(2L);
    }

    private static final class SimpleWorkerCountProvider implements WorkerCountProvider {
        private int value = 0;

        void set(int value) {
            this.value = value;
        }

        @Override
        public int get() {
            return value;
        }
    }
}

