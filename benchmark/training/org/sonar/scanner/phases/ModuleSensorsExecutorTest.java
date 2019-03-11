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
package org.sonar.scanner.phases;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.sonar.api.batch.fs.internal.SensorStrategy;
import org.sonar.scanner.bootstrap.ScannerPluginRepository;
import org.sonar.scanner.sensor.ModuleSensorWrapper;
import org.sonar.scanner.sensor.ModuleSensorsExecutor;


public class ModuleSensorsExecutorTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private ModuleSensorsExecutor rootModuleExecutor;

    private ModuleSensorsExecutor subModuleExecutor;

    private SensorStrategy strategy = new SensorStrategy();

    private ModuleSensorWrapper perModuleSensor = Mockito.mock(ModuleSensorWrapper.class);

    private ModuleSensorWrapper globalSensor = Mockito.mock(ModuleSensorWrapper.class);

    private ScannerPluginRepository pluginRepository = Mockito.mock(ScannerPluginRepository.class);

    @Test
    public void should_not_execute_global_sensor_for_submodule() {
        subModuleExecutor.execute();
        Mockito.verify(perModuleSensor).analyse();
        Mockito.verify(perModuleSensor).wrappedSensor();
        Mockito.verifyZeroInteractions(globalSensor);
        Mockito.verifyNoMoreInteractions(perModuleSensor, globalSensor);
    }

    @Test
    public void should_execute_all_sensors_for_root_module() {
        rootModuleExecutor.execute();
        Mockito.verify(globalSensor).wrappedSensor();
        Mockito.verify(perModuleSensor).wrappedSensor();
        Mockito.verify(globalSensor).analyse();
        Mockito.verify(perModuleSensor).analyse();
        Mockito.verifyNoMoreInteractions(perModuleSensor, globalSensor);
    }
}

