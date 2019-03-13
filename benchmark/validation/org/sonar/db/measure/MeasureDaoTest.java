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
package org.sonar.db.measure;


import System2.INSTANCE;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.component.ComponentTesting;
import org.sonar.db.component.SnapshotTesting;
import org.sonar.db.metric.MetricDto;


public class MeasureDaoTest {
    private static final int COVERAGE_METRIC_ID = 10;

    private static final int COMPLEXITY_METRIC_ID = 11;

    private static final int NCLOC_METRIC_ID = 12;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public DbTester db = DbTester.create(INSTANCE);

    private DbClient dbClient = db.getDbClient();

    private DbSession dbSession = db.getSession();

    private MeasureDao underTest = db.getDbClient().measureDao();

    @Test
    public void test_selectLastMeasure() {
        MetricDto metric = db.measures().insertMetric();
        ComponentDto project = db.components().insertPrivateProject();
        ComponentDto file = db.components().insertComponent(ComponentTesting.newFileDto(project));
        org.sonar.db.component.SnapshotDto lastAnalysis = insertAnalysis(project.uuid(), true);
        org.sonar.db.component.SnapshotDto pastAnalysis = insertAnalysis(project.uuid(), false);
        MeasureDto pastMeasure = MeasureTesting.newMeasureDto(metric, file, pastAnalysis);
        MeasureDto lastMeasure = MeasureTesting.newMeasureDto(metric, file, lastAnalysis);
        underTest.insert(db.getSession(), pastMeasure);
        underTest.insert(db.getSession(), lastMeasure);
        MeasureDto selected = underTest.selectLastMeasure(db.getSession(), file.uuid(), metric.getKey()).get();
        assertThat(selected).isEqualToComparingFieldByField(lastMeasure);
        assertThat(underTest.selectLastMeasure(dbSession, "_missing_", metric.getKey())).isEmpty();
        assertThat(underTest.selectLastMeasure(dbSession, file.uuid(), "_missing_")).isEmpty();
        assertThat(underTest.selectLastMeasure(dbSession, "_missing_", "_missing_")).isEmpty();
    }

    @Test
    public void test_selectMeasure() {
        MetricDto metric = db.measures().insertMetric();
        ComponentDto project = db.components().insertPrivateProject();
        ComponentDto file = db.components().insertComponent(ComponentTesting.newFileDto(project));
        org.sonar.db.component.SnapshotDto lastAnalysis = insertAnalysis(project.uuid(), true);
        org.sonar.db.component.SnapshotDto pastAnalysis = insertAnalysis(project.uuid(), false);
        MeasureDto pastMeasure = MeasureTesting.newMeasureDto(metric, file, pastAnalysis);
        MeasureDto lastMeasure = MeasureTesting.newMeasureDto(metric, file, lastAnalysis);
        underTest.insert(db.getSession(), pastMeasure);
        underTest.insert(db.getSession(), lastMeasure);
        assertThat(underTest.selectMeasure(db.getSession(), lastAnalysis.getUuid(), file.uuid(), metric.getKey()).get()).isEqualToComparingFieldByField(lastMeasure);
        assertThat(underTest.selectMeasure(db.getSession(), pastAnalysis.getUuid(), file.uuid(), metric.getKey()).get()).isEqualToComparingFieldByField(pastMeasure);
        assertThat(underTest.selectMeasure(db.getSession(), "_missing_", file.uuid(), metric.getKey())).isEmpty();
        assertThat(underTest.selectMeasure(db.getSession(), pastAnalysis.getUuid(), "_missing_", metric.getKey())).isEmpty();
        assertThat(underTest.selectMeasure(db.getSession(), pastAnalysis.getUuid(), file.uuid(), "_missing_")).isEmpty();
    }

    @Test
    public void selectByQuery() {
        ComponentDto project1 = db.components().insertPrivateProject();
        ComponentDto module = db.components().insertComponent(ComponentTesting.newModuleDto(project1));
        db.components().insertComponent(ComponentTesting.newFileDto(module).setUuid("C1"));
        db.components().insertComponent(ComponentTesting.newFileDto(module).setUuid("C2"));
        org.sonar.db.component.SnapshotDto lastAnalysis = insertAnalysis(project1.uuid(), true);
        org.sonar.db.component.SnapshotDto pastAnalysis = insertAnalysis(project1.uuid(), false);
        ComponentDto project2 = db.components().insertPrivateProject();
        org.sonar.db.component.SnapshotDto project2LastAnalysis = insertAnalysis(project2.uuid(), true);
        // project 1
        insertMeasure("P1_M1", lastAnalysis.getUuid(), project1.uuid(), MeasureDaoTest.NCLOC_METRIC_ID);
        insertMeasure("P1_M2", lastAnalysis.getUuid(), project1.uuid(), MeasureDaoTest.COVERAGE_METRIC_ID);
        insertMeasure("P1_M3", pastAnalysis.getUuid(), project1.uuid(), MeasureDaoTest.NCLOC_METRIC_ID);
        // project 2
        insertMeasure("P2_M1", project2LastAnalysis.getUuid(), project2.uuid(), MeasureDaoTest.NCLOC_METRIC_ID);
        insertMeasure("P2_M2", project2LastAnalysis.getUuid(), project2.uuid(), MeasureDaoTest.COVERAGE_METRIC_ID);
        // component C1
        insertMeasure("M1", pastAnalysis.getUuid(), "C1", MeasureDaoTest.NCLOC_METRIC_ID);
        insertMeasure("M2", lastAnalysis.getUuid(), "C1", MeasureDaoTest.NCLOC_METRIC_ID);
        insertMeasure("M3", lastAnalysis.getUuid(), "C1", MeasureDaoTest.COVERAGE_METRIC_ID);
        // component C2
        insertMeasure("M6", lastAnalysis.getUuid(), "C2", MeasureDaoTest.NCLOC_METRIC_ID);
        db.commit();
        verifyZeroMeasures(MeasureQuery.builder().setComponentUuids(project1.uuid(), Collections.emptyList()));
        verifyZeroMeasures(MeasureQuery.builder().setComponentUuid("MISSING_COMPONENT"));
        verifyZeroMeasures(MeasureQuery.builder().setProjectUuids(Collections.emptyList()));
        verifyZeroMeasures(MeasureQuery.builder().setProjectUuids(Collections.singletonList("MISSING_COMPONENT")));
        // all measures of component C1 of last analysis
        verifyMeasures(MeasureQuery.builder().setComponentUuid("C1"), "M2", "M3");
        // all measures of component C1 of non last analysis
        verifyMeasures(MeasureQuery.builder().setComponentUuid("C1").setAnalysisUuid(pastAnalysis.getUuid()), "M1");
        // all measures of component C1 of last analysis by UUID
        verifyMeasures(MeasureQuery.builder().setComponentUuid("C1").setAnalysisUuid(lastAnalysis.getUuid()), "M2", "M3");
        // ncloc measure of component C1 of last analysis
        verifyMeasures(MeasureQuery.builder().setComponentUuid("C1").setMetricId(MeasureDaoTest.NCLOC_METRIC_ID), "M2");
        // ncloc measure of component C1 of non last analysis
        verifyMeasures(MeasureQuery.builder().setComponentUuid("C1").setAnalysisUuid(pastAnalysis.getUuid()).setMetricId(MeasureDaoTest.NCLOC_METRIC_ID), "M1");
        // ncloc measure of component C1 of last analysis by UUID
        verifyMeasures(MeasureQuery.builder().setComponentUuid("C1").setAnalysisUuid(lastAnalysis.getUuid()).setMetricId(MeasureDaoTest.NCLOC_METRIC_ID), "M2");
        // multiple measures of component C1 of last analysis
        verifyMeasures(MeasureQuery.builder().setComponentUuid("C1").setMetricIds(Arrays.asList(MeasureDaoTest.NCLOC_METRIC_ID, MeasureDaoTest.COVERAGE_METRIC_ID)), "M2", "M3");
        // multiple measures of component C1 of non last analysis
        verifyMeasures(MeasureQuery.builder().setComponentUuid("C1").setAnalysisUuid(pastAnalysis.getUuid()).setMetricIds(Arrays.asList(MeasureDaoTest.NCLOC_METRIC_ID, MeasureDaoTest.COVERAGE_METRIC_ID)), "M1");
        // multiple measures of component C1 of last analysis by UUID
        verifyMeasures(MeasureQuery.builder().setComponentUuid("C1").setAnalysisUuid(lastAnalysis.getUuid()).setMetricIds(Arrays.asList(MeasureDaoTest.NCLOC_METRIC_ID, MeasureDaoTest.COVERAGE_METRIC_ID)), "M2", "M3");
        // missing measure of component C1 of last analysis
        verifyZeroMeasures(MeasureQuery.builder().setComponentUuid("C1").setMetricId(MeasureDaoTest.COMPLEXITY_METRIC_ID));
        // missing measure of component C1 of non last analysis
        verifyZeroMeasures(MeasureQuery.builder().setComponentUuid("C1").setAnalysisUuid(pastAnalysis.getUuid()).setMetricId(MeasureDaoTest.COMPLEXITY_METRIC_ID));
        // missing measure of component C1 of last analysis by UUID
        verifyZeroMeasures(MeasureQuery.builder().setComponentUuid("C1").setAnalysisUuid(lastAnalysis.getUuid()).setMetricId(MeasureDaoTest.COMPLEXITY_METRIC_ID));
        // ncloc measures of components C1, C2 and C3 (which does not exist) of last analysis
        verifyMeasures(MeasureQuery.builder().setComponentUuids(project1.uuid(), Arrays.asList("C1", "C2", "C3")), "M2", "M3", "M6");
        // ncloc measures of components C1, C2 and C3 (which does not exist) of non last analysis
        verifyMeasures(MeasureQuery.builder().setComponentUuids(project1.uuid(), Arrays.asList("C1", "C2", "C3")).setAnalysisUuid(pastAnalysis.getUuid()), "M1");
        // ncloc measures of components C1, C2 and C3 (which does not exist) of last analysis by UUID
        verifyMeasures(MeasureQuery.builder().setComponentUuids(project1.uuid(), Arrays.asList("C1", "C2", "C3")).setAnalysisUuid(lastAnalysis.getUuid()), "M2", "M3", "M6");
        // projects measures of last analysis
        verifyMeasures(MeasureQuery.builder().setProjectUuids(Collections.singletonList(project1.uuid())).setMetricId(MeasureDaoTest.NCLOC_METRIC_ID), "P1_M1");
        verifyMeasures(MeasureQuery.builder().setProjectUuids(Arrays.asList(project1.uuid(), project2.uuid())).setMetricIds(Arrays.asList(MeasureDaoTest.NCLOC_METRIC_ID, MeasureDaoTest.COVERAGE_METRIC_ID)), "P1_M1", "P1_M2", "P2_M1", "P2_M2", "P2_M2");
        verifyMeasures(MeasureQuery.builder().setProjectUuids(Arrays.asList(project1.uuid(), project2.uuid(), "UNKNOWN")).setMetricId(MeasureDaoTest.NCLOC_METRIC_ID), "P1_M1", "P2_M1");
        // projects measures of none last analysis
        verifyMeasures(MeasureQuery.builder().setProjectUuids(Collections.singletonList(project1.uuid())).setMetricId(MeasureDaoTest.NCLOC_METRIC_ID).setAnalysisUuid(pastAnalysis.getUuid()), "P1_M3");
        verifyMeasures(MeasureQuery.builder().setProjectUuids(Arrays.asList(project1.uuid(), project2.uuid())).setMetricId(MeasureDaoTest.NCLOC_METRIC_ID).setAnalysisUuid(pastAnalysis.getUuid()), "P1_M3");
    }

    @Test
    public void select_past_measures_with_several_analyses() {
        ComponentDto project = db.components().insertPrivateProject();
        long lastAnalysisDate = parseDate("2017-01-25").getTime();
        long previousAnalysisDate = lastAnalysisDate - 10000000000L;
        long oldAnalysisDate = lastAnalysisDate - 100000000000L;
        org.sonar.db.component.SnapshotDto lastAnalysis = dbClient.snapshotDao().insert(dbSession, SnapshotTesting.newAnalysis(project).setCreatedAt(lastAnalysisDate));
        org.sonar.db.component.SnapshotDto pastAnalysis = dbClient.snapshotDao().insert(dbSession, SnapshotTesting.newAnalysis(project).setCreatedAt(previousAnalysisDate).setLast(false));
        dbClient.snapshotDao().insert(dbSession, SnapshotTesting.newAnalysis(project).setCreatedAt(oldAnalysisDate).setLast(false));
        db.commit();
        // project
        insertMeasure("PROJECT_M1", lastAnalysis.getUuid(), project.uuid(), MeasureDaoTest.NCLOC_METRIC_ID);
        insertMeasure("PROJECT_M2", pastAnalysis.getUuid(), project.uuid(), MeasureDaoTest.NCLOC_METRIC_ID);
        insertMeasure("PROJECT_M3", "OLD_ANALYSIS_UUID", project.uuid(), MeasureDaoTest.NCLOC_METRIC_ID);
        db.commit();
        // Measures of project for last and previous analyses
        List<MeasureDto> result = underTest.selectPastMeasures(db.getSession(), new PastMeasureQuery(project.uuid(), Collections.singletonList(MeasureDaoTest.NCLOC_METRIC_ID), previousAnalysisDate, (lastAnalysisDate + 1000L)));
        assertThat(result).hasSize(2).extracting(MeasureDto::getData).containsOnly("PROJECT_M1", "PROJECT_M2");
    }
}

