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
package org.sonar.scanner.sensor;


import ModuleSensorContext.NO_OP_NEW_ANALYSIS_ERROR;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.sonar.api.SonarRuntime;
import org.sonar.api.batch.fs.internal.DefaultFileSystem;
import org.sonar.api.batch.rule.ActiveRules;
import org.sonar.api.batch.sensor.internal.SensorStorage;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.utils.Version;


public class ModuleSensorContextTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ActiveRules activeRules;

    private DefaultFileSystem fs;

    private ModuleSensorContext adaptor;

    private MapSettings settings;

    private SensorStorage sensorStorage;

    private SonarRuntime runtime;

    @Test
    public void shouldProvideComponents() {
        assertThat(adaptor.activeRules()).isEqualTo(activeRules);
        assertThat(adaptor.fileSystem()).isEqualTo(fs);
        assertThat(adaptor.settings()).isEqualTo(settings);
        assertThat(adaptor.getSonarQubeVersion()).isEqualTo(Version.parse("5.5"));
        assertThat(adaptor.runtime()).isEqualTo(runtime);
        assertThat(adaptor.newIssue()).isNotNull();
        assertThat(adaptor.newExternalIssue()).isNotNull();
        assertThat(adaptor.newAdHocRule()).isNotNull();
        assertThat(adaptor.newMeasure()).isNotNull();
        assertThat(adaptor.newAnalysisError()).isEqualTo(NO_OP_NEW_ANALYSIS_ERROR);
        assertThat(adaptor.isCancelled()).isFalse();
        assertThat(adaptor.newSignificantCode()).isNotNull();
    }
}

