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
package org.sonar.ce.task.projectanalysis.step;


import ShortLivingBranchQualityGate.ID;
import java.util.Collections;
import java.util.Optional;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.ce.task.projectanalysis.analysis.AnalysisMetadataHolder;
import org.sonar.ce.task.projectanalysis.component.ConfigurationRepository;
import org.sonar.ce.task.projectanalysis.qualitygate.MutableQualityGateHolderRule;
import org.sonar.ce.task.projectanalysis.qualitygate.QualityGate;
import org.sonar.ce.task.projectanalysis.qualitygate.QualityGateService;
import org.sonar.ce.task.step.TestComputationStepContext;


public class LoadQualityGateStepTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public MutableQualityGateHolderRule mutableQualityGateHolder = new MutableQualityGateHolderRule();

    private AnalysisMetadataHolder analysisMetadataHolder = Mockito.mock(AnalysisMetadataHolder.class);

    private ConfigurationRepository settingsRepository = Mockito.mock(ConfigurationRepository.class);

    private QualityGateService qualityGateService = Mockito.mock(QualityGateService.class);

    private LoadQualityGateStep underTest = new LoadQualityGateStep(settingsRepository, qualityGateService, mutableQualityGateHolder, analysisMetadataHolder);

    @Test
    public void add_hardcoded_QG_on_short_living_branch() {
        Mockito.when(analysisMetadataHolder.isShortLivingBranch()).thenReturn(true);
        QualityGate qualityGate = Mockito.mock(QualityGate.class);
        Mockito.when(qualityGateService.findById(ID)).thenReturn(Optional.of(qualityGate));
        underTest.execute(new TestComputationStepContext());
        assertThat(mutableQualityGateHolder.getQualityGate().get()).isSameAs(qualityGate);
    }

    @Test
    public void add_hardcoded_QG_on_pull_request() {
        Mockito.when(analysisMetadataHolder.isPullRequest()).thenReturn(true);
        QualityGate qualityGate = Mockito.mock(QualityGate.class);
        Mockito.when(qualityGateService.findById(ID)).thenReturn(Optional.of(qualityGate));
        underTest.execute(new TestComputationStepContext());
        assertThat(mutableQualityGateHolder.getQualityGate().get()).isSameAs(qualityGate);
    }

    @Test
    public void execute_sets_default_QualityGate_when_project_has_no_settings() {
        Mockito.when(settingsRepository.getConfiguration()).thenReturn(new MapSettings().asConfig());
        QualityGate defaultGate = Mockito.mock(QualityGate.class);
        Mockito.when(qualityGateService.findDefaultQualityGate(ArgumentMatchers.any())).thenReturn(defaultGate);
        underTest.execute(new TestComputationStepContext());
        assertThat(mutableQualityGateHolder.getQualityGate().get()).isSameAs(defaultGate);
    }

    @Test
    public void execute_sets_default_QualityGate_when_property_value_is_not_a_long() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Unsupported value (10 sds) in property sonar.qualitygate");
        Mockito.when(settingsRepository.getConfiguration()).thenReturn(new MapSettings().setProperty("sonar.qualitygate", "10 sds").asConfig());
        underTest.execute(new TestComputationStepContext());
    }

    @Test
    public void execute_sets_QualityGate_if_it_can_be_found_by_service() {
        QualityGate qualityGate = new QualityGate(10, "name", Collections.emptyList());
        Mockito.when(settingsRepository.getConfiguration()).thenReturn(new MapSettings().setProperty("sonar.qualitygate", 10).asConfig());
        Mockito.when(qualityGateService.findById(10)).thenReturn(Optional.of(qualityGate));
        underTest.execute(new TestComputationStepContext());
        assertThat(mutableQualityGateHolder.getQualityGate().get()).isSameAs(qualityGate);
    }
}

