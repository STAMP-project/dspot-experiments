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


import System2.INSTANCE;
import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.ce.task.projectanalysis.analysis.AnalysisMetadataHolder;
import org.sonar.ce.task.projectanalysis.batch.BatchReportReader;
import org.sonar.ce.task.step.TestComputationStepContext;
import org.sonar.core.util.CloseableIterator;
import org.sonar.core.util.UuidFactoryFast;
import org.sonar.db.DbTester;
import org.sonar.db.component.AnalysisPropertyDto;
import org.sonar.scanner.protocol.output.ScannerReport;


public class PersistAnalysisPropertiesStepTest {
    private static final String SNAPSHOT_UUID = randomAlphanumeric(40);

    private static final String SMALL_VALUE1 = randomAlphanumeric(50);

    private static final String SMALL_VALUE2 = randomAlphanumeric(50);

    private static final String SMALL_VALUE3 = randomAlphanumeric(50);

    private static final String BIG_VALUE = randomAlphanumeric(5000);

    private static final String VALUE_PREFIX_FOR_PR_PROPERTIES = "pr_";

    private static final List<ScannerReport.ContextProperty> PROPERTIES = Arrays.asList(PersistAnalysisPropertiesStepTest.newContextProperty("key1", "value1"), PersistAnalysisPropertiesStepTest.newContextProperty("key2", "value1"), PersistAnalysisPropertiesStepTest.newContextProperty("sonar.analysis", PersistAnalysisPropertiesStepTest.SMALL_VALUE1), PersistAnalysisPropertiesStepTest.newContextProperty("sonar.analysis.branch", PersistAnalysisPropertiesStepTest.SMALL_VALUE2), PersistAnalysisPropertiesStepTest.newContextProperty("sonar.analysis.empty_string", ""), PersistAnalysisPropertiesStepTest.newContextProperty("sonar.analysis.big_value", PersistAnalysisPropertiesStepTest.BIG_VALUE), PersistAnalysisPropertiesStepTest.newContextProperty("sonar.analysis.", PersistAnalysisPropertiesStepTest.SMALL_VALUE3), PersistAnalysisPropertiesStepTest.newContextProperty("sonar.pullrequest", ((PersistAnalysisPropertiesStepTest.VALUE_PREFIX_FOR_PR_PROPERTIES) + (PersistAnalysisPropertiesStepTest.SMALL_VALUE1))), PersistAnalysisPropertiesStepTest.newContextProperty("sonar.pullrequest.branch", ((PersistAnalysisPropertiesStepTest.VALUE_PREFIX_FOR_PR_PROPERTIES) + (PersistAnalysisPropertiesStepTest.SMALL_VALUE2))), PersistAnalysisPropertiesStepTest.newContextProperty("sonar.pullrequest.empty_string", ""), PersistAnalysisPropertiesStepTest.newContextProperty("sonar.pullrequest.big_value", ((PersistAnalysisPropertiesStepTest.VALUE_PREFIX_FOR_PR_PROPERTIES) + (PersistAnalysisPropertiesStepTest.BIG_VALUE))), PersistAnalysisPropertiesStepTest.newContextProperty("sonar.pullrequest.", ((PersistAnalysisPropertiesStepTest.VALUE_PREFIX_FOR_PR_PROPERTIES) + (PersistAnalysisPropertiesStepTest.SMALL_VALUE3))));

    private static final String SCM_REV_ID = "sha1";

    @Rule
    public DbTester dbTester = DbTester.create(INSTANCE);

    private BatchReportReader batchReportReader = Mockito.mock(BatchReportReader.class);

    private AnalysisMetadataHolder analysisMetadataHolder = Mockito.mock(AnalysisMetadataHolder.class);

    private PersistAnalysisPropertiesStep underTest = new PersistAnalysisPropertiesStep(dbTester.getDbClient(), analysisMetadataHolder, batchReportReader, UuidFactoryFast.getInstance());

    @Test
    public void persist_should_stores_sonarDotAnalysisDot_and_sonarDotPullRequestDot_properties() {
        Mockito.when(batchReportReader.readContextProperties()).thenReturn(CloseableIterator.from(PersistAnalysisPropertiesStepTest.PROPERTIES.iterator()));
        Mockito.when(analysisMetadataHolder.getUuid()).thenReturn(PersistAnalysisPropertiesStepTest.SNAPSHOT_UUID);
        Mockito.when(analysisMetadataHolder.getScmRevisionId()).thenReturn(Optional.of(PersistAnalysisPropertiesStepTest.SCM_REV_ID));
        underTest.execute(new TestComputationStepContext());
        assertThat(dbTester.countRowsOfTable("analysis_properties")).isEqualTo(9);
        List<AnalysisPropertyDto> propertyDtos = dbTester.getDbClient().analysisPropertiesDao().selectBySnapshotUuid(dbTester.getSession(), PersistAnalysisPropertiesStepTest.SNAPSHOT_UUID);
        assertThat(propertyDtos).extracting(AnalysisPropertyDto::getSnapshotUuid, AnalysisPropertyDto::getKey, AnalysisPropertyDto::getValue).containsExactlyInAnyOrder(tuple(PersistAnalysisPropertiesStepTest.SNAPSHOT_UUID, "sonar.analysis.branch", PersistAnalysisPropertiesStepTest.SMALL_VALUE2), tuple(PersistAnalysisPropertiesStepTest.SNAPSHOT_UUID, "sonar.analysis.empty_string", ""), tuple(PersistAnalysisPropertiesStepTest.SNAPSHOT_UUID, "sonar.analysis.big_value", PersistAnalysisPropertiesStepTest.BIG_VALUE), tuple(PersistAnalysisPropertiesStepTest.SNAPSHOT_UUID, "sonar.analysis.", PersistAnalysisPropertiesStepTest.SMALL_VALUE3), tuple(PersistAnalysisPropertiesStepTest.SNAPSHOT_UUID, "sonar.analysis.scm_revision_id", PersistAnalysisPropertiesStepTest.SCM_REV_ID), tuple(PersistAnalysisPropertiesStepTest.SNAPSHOT_UUID, "sonar.pullrequest.branch", ((PersistAnalysisPropertiesStepTest.VALUE_PREFIX_FOR_PR_PROPERTIES) + (PersistAnalysisPropertiesStepTest.SMALL_VALUE2))), tuple(PersistAnalysisPropertiesStepTest.SNAPSHOT_UUID, "sonar.pullrequest.empty_string", ""), tuple(PersistAnalysisPropertiesStepTest.SNAPSHOT_UUID, "sonar.pullrequest.big_value", ((PersistAnalysisPropertiesStepTest.VALUE_PREFIX_FOR_PR_PROPERTIES) + (PersistAnalysisPropertiesStepTest.BIG_VALUE))), tuple(PersistAnalysisPropertiesStepTest.SNAPSHOT_UUID, "sonar.pullrequest.", ((PersistAnalysisPropertiesStepTest.VALUE_PREFIX_FOR_PR_PROPERTIES) + (PersistAnalysisPropertiesStepTest.SMALL_VALUE3))));
    }

    @Test
    public void persist_should_not_stores_sonarDotAnalysisDotscm_revision_id_properties_when_its_not_available_in_report_metada() {
        Mockito.when(batchReportReader.readContextProperties()).thenReturn(CloseableIterator.from(PersistAnalysisPropertiesStepTest.PROPERTIES.iterator()));
        Mockito.when(analysisMetadataHolder.getUuid()).thenReturn(PersistAnalysisPropertiesStepTest.SNAPSHOT_UUID);
        Mockito.when(analysisMetadataHolder.getScmRevisionId()).thenReturn(Optional.empty());
        underTest.execute(new TestComputationStepContext());
        assertThat(dbTester.countRowsOfTable("analysis_properties")).isEqualTo(8);
        List<AnalysisPropertyDto> propertyDtos = dbTester.getDbClient().analysisPropertiesDao().selectBySnapshotUuid(dbTester.getSession(), PersistAnalysisPropertiesStepTest.SNAPSHOT_UUID);
        assertThat(propertyDtos).extracting(AnalysisPropertyDto::getSnapshotUuid, AnalysisPropertyDto::getKey, AnalysisPropertyDto::getValue).containsExactlyInAnyOrder(tuple(PersistAnalysisPropertiesStepTest.SNAPSHOT_UUID, "sonar.analysis.branch", PersistAnalysisPropertiesStepTest.SMALL_VALUE2), tuple(PersistAnalysisPropertiesStepTest.SNAPSHOT_UUID, "sonar.analysis.empty_string", ""), tuple(PersistAnalysisPropertiesStepTest.SNAPSHOT_UUID, "sonar.analysis.big_value", PersistAnalysisPropertiesStepTest.BIG_VALUE), tuple(PersistAnalysisPropertiesStepTest.SNAPSHOT_UUID, "sonar.analysis.", PersistAnalysisPropertiesStepTest.SMALL_VALUE3), tuple(PersistAnalysisPropertiesStepTest.SNAPSHOT_UUID, "sonar.pullrequest.branch", ((PersistAnalysisPropertiesStepTest.VALUE_PREFIX_FOR_PR_PROPERTIES) + (PersistAnalysisPropertiesStepTest.SMALL_VALUE2))), tuple(PersistAnalysisPropertiesStepTest.SNAPSHOT_UUID, "sonar.pullrequest.empty_string", ""), tuple(PersistAnalysisPropertiesStepTest.SNAPSHOT_UUID, "sonar.pullrequest.big_value", ((PersistAnalysisPropertiesStepTest.VALUE_PREFIX_FOR_PR_PROPERTIES) + (PersistAnalysisPropertiesStepTest.BIG_VALUE))), tuple(PersistAnalysisPropertiesStepTest.SNAPSHOT_UUID, "sonar.pullrequest.", ((PersistAnalysisPropertiesStepTest.VALUE_PREFIX_FOR_PR_PROPERTIES) + (PersistAnalysisPropertiesStepTest.SMALL_VALUE3))));
    }

    @Test
    public void persist_filtering_of_properties_is_case_sensitive() {
        Mockito.when(analysisMetadataHolder.getScmRevisionId()).thenReturn(Optional.of(PersistAnalysisPropertiesStepTest.SCM_REV_ID));
        Mockito.when(batchReportReader.readContextProperties()).thenReturn(CloseableIterator.from(ImmutableList.of(PersistAnalysisPropertiesStepTest.newContextProperty("sonar.ANALYSIS.foo", "foo"), PersistAnalysisPropertiesStepTest.newContextProperty("sonar.anaLysis.bar", "bar"), PersistAnalysisPropertiesStepTest.newContextProperty("sonar.anaLYSIS.doo", "doh"), PersistAnalysisPropertiesStepTest.newContextProperty("sonar.PULLREQUEST.foo", "foo"), PersistAnalysisPropertiesStepTest.newContextProperty("sonar.pullRequest.bar", "bar"), PersistAnalysisPropertiesStepTest.newContextProperty("sonar.pullREQUEST.doo", "doh")).iterator()));
        Mockito.when(analysisMetadataHolder.getUuid()).thenReturn(PersistAnalysisPropertiesStepTest.SNAPSHOT_UUID);
        underTest.execute(new TestComputationStepContext());
        assertThat(dbTester.countRowsOfTable("analysis_properties")).isEqualTo(1);
    }

    @Test
    public void persist_should_only_store_scmRevisionId_if_there_is_no_context_properties() {
        Mockito.when(analysisMetadataHolder.getScmRevisionId()).thenReturn(Optional.of(PersistAnalysisPropertiesStepTest.SCM_REV_ID));
        Mockito.when(batchReportReader.readContextProperties()).thenReturn(CloseableIterator.emptyCloseableIterator());
        Mockito.when(analysisMetadataHolder.getUuid()).thenReturn(PersistAnalysisPropertiesStepTest.SNAPSHOT_UUID);
        underTest.execute(new TestComputationStepContext());
        assertThat(dbTester.countRowsOfTable("analysis_properties")).isEqualTo(1);
        List<AnalysisPropertyDto> propertyDtos = dbTester.getDbClient().analysisPropertiesDao().selectBySnapshotUuid(dbTester.getSession(), PersistAnalysisPropertiesStepTest.SNAPSHOT_UUID);
        assertThat(propertyDtos).extracting(AnalysisPropertyDto::getSnapshotUuid, AnalysisPropertyDto::getKey, AnalysisPropertyDto::getValue).containsExactlyInAnyOrder(tuple(PersistAnalysisPropertiesStepTest.SNAPSHOT_UUID, "sonar.analysis.scm_revision_id", PersistAnalysisPropertiesStepTest.SCM_REV_ID));
    }

    @Test
    public void verify_description_value() {
        assertThat(underTest.getDescription()).isEqualTo("Persist analysis properties");
    }
}

