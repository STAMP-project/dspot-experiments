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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.ce.task.projectanalysis.analysis.AnalysisMetadataHolderRule;
import org.sonar.ce.task.projectanalysis.component.Component;
import org.sonar.ce.task.projectanalysis.component.ReportComponent;
import org.sonar.ce.task.projectanalysis.component.TreeRootHolderRule;
import org.sonar.ce.task.projectanalysis.measure.Measure;
import org.sonar.ce.task.projectanalysis.measure.MeasureRepositoryRule;
import org.sonar.ce.task.projectanalysis.metric.MetricRepositoryRule;
import org.sonar.ce.task.step.TestComputationStepContext;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.RowNotFoundException;
import org.sonar.db.qualityprofile.QProfileDto;
import org.sonar.db.qualityprofile.QualityProfileDbTester;


public class UpdateQualityProfilesLastUsedDateStepTest {
    static final long ANALYSIS_DATE = 1123456789L;

    private static final Component PROJECT = ReportComponent.DUMB_PROJECT;

    private QProfileDto sonarWayJava = newQualityProfileDto().setKee("sonar-way-java");

    private QProfileDto sonarWayPhp = newQualityProfileDto().setKee("sonar-way-php");

    private QProfileDto myQualityProfile = newQualityProfileDto().setKee("my-qp");

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public DbTester db = DbTester.create(INSTANCE);

    @Rule
    public AnalysisMetadataHolderRule analysisMetadataHolder = new AnalysisMetadataHolderRule().setAnalysisDate(UpdateQualityProfilesLastUsedDateStepTest.ANALYSIS_DATE);

    @Rule
    public TreeRootHolderRule treeRootHolder = new TreeRootHolderRule().setRoot(UpdateQualityProfilesLastUsedDateStepTest.PROJECT);

    @Rule
    public MetricRepositoryRule metricRepository = new MetricRepositoryRule().add(CoreMetrics.QUALITY_PROFILES);

    @Rule
    public MeasureRepositoryRule measureRepository = MeasureRepositoryRule.create(treeRootHolder, metricRepository);

    DbClient dbClient = db.getDbClient();

    DbSession dbSession = db.getSession();

    QualityProfileDbTester qualityProfileDb = new QualityProfileDbTester(db);

    UpdateQualityProfilesLastUsedDateStep underTest = new UpdateQualityProfilesLastUsedDateStep(dbClient, analysisMetadataHolder, treeRootHolder, metricRepository, measureRepository);

    @Test
    public void doest_not_update_profiles_when_no_measure() {
        qualityProfileDb.insert(sonarWayJava, sonarWayPhp, myQualityProfile);
        underTest.execute(new TestComputationStepContext());
        assertQualityProfileIsTheSame(sonarWayJava);
        assertQualityProfileIsTheSame(sonarWayPhp);
        assertQualityProfileIsTheSame(myQualityProfile);
    }

    @Test
    public void update_profiles_defined_in_quality_profiles_measure() {
        qualityProfileDb.insert(sonarWayJava, sonarWayPhp, myQualityProfile);
        measureRepository.addRawMeasure(1, CoreMetrics.QUALITY_PROFILES_KEY, Measure.newMeasureBuilder().create(UpdateQualityProfilesLastUsedDateStepTest.toJson(sonarWayJava.getKee(), myQualityProfile.getKee())));
        underTest.execute(new TestComputationStepContext());
        assertQualityProfileIsTheSame(sonarWayPhp);
        assertQualityProfileIsUpdated(sonarWayJava);
        assertQualityProfileIsUpdated(myQualityProfile);
    }

    @Test
    public void ancestor_profiles_are_updated() {
        // Parent profiles should be updated
        QProfileDto rootProfile = newQualityProfileDto().setKee("root");
        QProfileDto parentProfile = newQualityProfileDto().setKee("parent").setParentKee(rootProfile.getKee());
        // Current profile => should be updated
        QProfileDto currentProfile = newQualityProfileDto().setKee("current").setParentKee(parentProfile.getKee());
        // Child of current profile => should not be updated
        QProfileDto childProfile = newQualityProfileDto().setKee("child").setParentKee(currentProfile.getKee());
        qualityProfileDb.insert(rootProfile, parentProfile, currentProfile, childProfile);
        measureRepository.addRawMeasure(1, CoreMetrics.QUALITY_PROFILES_KEY, Measure.newMeasureBuilder().create(UpdateQualityProfilesLastUsedDateStepTest.toJson(currentProfile.getKee())));
        underTest.execute(new TestComputationStepContext());
        assertQualityProfileIsUpdated(rootProfile);
        assertQualityProfileIsUpdated(parentProfile);
        assertQualityProfileIsUpdated(currentProfile);
        assertQualityProfileIsTheSame(childProfile);
    }

    @Test
    public void fail_when_profile_is_linked_to_unknown_parent() {
        QProfileDto currentProfile = newQualityProfileDto().setKee("current").setParentKee("unknown");
        qualityProfileDb.insert(currentProfile);
        measureRepository.addRawMeasure(1, CoreMetrics.QUALITY_PROFILES_KEY, Measure.newMeasureBuilder().create(UpdateQualityProfilesLastUsedDateStepTest.toJson(currentProfile.getKee())));
        expectedException.expect(RowNotFoundException.class);
        underTest.execute(new TestComputationStepContext());
    }

    @Test
    public void test_description() {
        assertThat(underTest.getDescription()).isEqualTo("Update last usage date of quality profiles");
    }
}

