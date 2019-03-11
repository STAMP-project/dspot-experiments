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
package org.sonar.server.measure.live;


import Qualifiers.DIRECTORY;
import Qualifiers.FILE;
import Qualifiers.PROJECT;
import Rating.B;
import Rating.C;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.measures.Metric;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.component.BranchDto;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.component.ComponentTesting;
import org.sonar.db.measure.LiveMeasureDto;
import org.sonar.db.metric.MetricDto;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.server.es.TestProjectIndexers;
import org.sonar.server.qualitygate.EvaluatedQualityGate;
import org.sonar.server.qualitygate.QualityGate;
import org.sonar.server.qualitygate.changeevent.QGChangeEvent;


@RunWith(DataProviderRunner.class)
public class LiveMeasureComputerImplTest {
    @Rule
    public DbTester db = DbTester.create();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private TestProjectIndexers projectIndexer = new TestProjectIndexers();

    private MetricDto intMetric;

    private MetricDto ratingMetric;

    private MetricDto alertStatusMetric;

    private OrganizationDto organization;

    private ComponentDto project;

    private ComponentDto dir;

    private ComponentDto file1;

    private ComponentDto file2;

    private LiveQualityGateComputer qGateComputer = Mockito.mock(LiveQualityGateComputer.class);

    private QualityGate qualityGate = Mockito.mock(QualityGate.class);

    private EvaluatedQualityGate newQualityGate = Mockito.mock(EvaluatedQualityGate.class);

    @Test
    public void compute_and_insert_measures_if_they_do_not_exist_yet() {
        markProjectAsAnalyzed(project);
        List<QGChangeEvent> result = run(Arrays.asList(file1, file2), newQualifierBasedIntFormula(), newRatingConstantFormula(C));
        // 2 measures per component have been created
        // Numeric value depends on qualifier (see newQualifierBasedIntFormula())
        assertThat(db.countRowsOfTable(db.getSession(), "live_measures")).isEqualTo(8);
        assertThatIntMeasureHasValue(file1, ORDERED_BOTTOM_UP.indexOf(FILE));
        assertThatRatingMeasureHasValue(file1, C);
        assertThatIntMeasureHasValue(file2, ORDERED_BOTTOM_UP.indexOf(FILE));
        assertThatRatingMeasureHasValue(file2, C);
        assertThatIntMeasureHasValue(dir, ORDERED_BOTTOM_UP.indexOf(DIRECTORY));
        assertThatRatingMeasureHasValue(dir, C);
        assertThatIntMeasureHasValue(project, ORDERED_BOTTOM_UP.indexOf(PROJECT));
        assertThatRatingMeasureHasValue(project, C);
        assertThatProjectChanged(result, project);
    }

    @Test
    public void compute_and_update_measures_if_they_already_exist() {
        markProjectAsAnalyzed(project);
        db.measures().insertLiveMeasure(project, intMetric, ( m) -> m.setValue(42.0));
        db.measures().insertLiveMeasure(dir, intMetric, ( m) -> m.setValue(42.0));
        db.measures().insertLiveMeasure(file1, intMetric, ( m) -> m.setValue(42.0));
        db.measures().insertLiveMeasure(file2, intMetric, ( m) -> m.setValue(42.0));
        // generates values 1, 2, 3
        List<QGChangeEvent> result = run(file1, newQualifierBasedIntFormula());
        assertThat(db.countRowsOfTable(db.getSession(), "live_measures")).isEqualTo(4);
        assertThatProjectChanged(result, project);
        // Numeric value depends on qualifier (see newQualifierBasedIntFormula())
        assertThatIntMeasureHasValue(file1, ORDERED_BOTTOM_UP.indexOf(FILE));
        assertThatIntMeasureHasValue(dir, ORDERED_BOTTOM_UP.indexOf(DIRECTORY));
        assertThatIntMeasureHasValue(project, ORDERED_BOTTOM_UP.indexOf(PROJECT));
        // untouched
        assertThatIntMeasureHasValue(file2, 42.0);
    }

    @Test
    public void variation_is_refreshed_when_int_value_is_changed() {
        markProjectAsAnalyzed(project);
        // value is:
        // 42 on last analysis
        // 42-12=30 on beginning of leak period
        db.measures().insertLiveMeasure(project, intMetric, ( m) -> m.setValue(42.0).setVariation(12.0));
        // new value is 44, so variation on leak period is 44-30=14
        List<QGChangeEvent> result = run(file1, newIntConstantFormula(44.0));
        LiveMeasureDto measure = assertThatIntMeasureHasValue(project, 44.0);
        assertThat(measure.getVariation()).isEqualTo(14.0);
        assertThatProjectChanged(result, project);
    }

    @Test
    public void variation_is_refreshed_when_rating_value_is_changed() {
        markProjectAsAnalyzed(project);
        // value is:
        // B on last analysis
        // D on beginning of leak period --> variation is -2
        db.measures().insertLiveMeasure(project, ratingMetric, ( m) -> m.setValue(((double) (Rating.B.getIndex()))).setData("B").setVariation((-2.0)));
        // new value is C, so variation on leak period is D to C = -1
        List<QGChangeEvent> result = run(file1, newRatingConstantFormula(C));
        LiveMeasureDto measure = assertThatRatingMeasureHasValue(project, C);
        assertThat(measure.getVariation()).isEqualTo((-1.0));
        assertThatProjectChanged(result, project);
    }

    @Test
    public void variation_does_not_change_if_rating_value_does_not_change() {
        markProjectAsAnalyzed(project);
        // value is:
        // B on last analysis
        // D on beginning of leak period --> variation is -2
        db.measures().insertLiveMeasure(project, ratingMetric, ( m) -> m.setValue(((double) (Rating.B.getIndex()))).setData("B").setVariation((-2.0)));
        // new value is still B, so variation on leak period is still -2
        List<QGChangeEvent> result = run(file1, newRatingConstantFormula(B));
        LiveMeasureDto measure = assertThatRatingMeasureHasValue(project, B);
        assertThat(measure.getVariation()).isEqualTo((-2.0));
        assertThatProjectChanged(result, project);
    }

    @Test
    public void refresh_leak_measures() {
        markProjectAsAnalyzed(project);
        db.measures().insertLiveMeasure(project, intMetric, ( m) -> m.setVariation(42.0).setValue(null));
        db.measures().insertLiveMeasure(project, ratingMetric, ( m) -> m.setVariation(((double) (Rating.E.getIndex()))));
        db.measures().insertLiveMeasure(dir, intMetric, ( m) -> m.setVariation(42.0).setValue(null));
        db.measures().insertLiveMeasure(dir, ratingMetric, ( m) -> m.setVariation(((double) (Rating.D.getIndex()))));
        db.measures().insertLiveMeasure(file1, intMetric, ( m) -> m.setVariation(42.0).setValue(null));
        db.measures().insertLiveMeasure(file1, ratingMetric, ( m) -> m.setVariation(((double) (Rating.C.getIndex()))));
        // generates values 1, 2, 3 on leak measures
        List<QGChangeEvent> result = run(file1, newQualifierBasedIntLeakFormula(), newRatingLeakFormula(B));
        assertThat(db.countRowsOfTable(db.getSession(), "live_measures")).isEqualTo(6);
        // Numeric value depends on qualifier (see newQualifierBasedIntLeakFormula())
        assertThatIntMeasureHasLeakValue(file1, ORDERED_BOTTOM_UP.indexOf(FILE));
        assertThatRatingMeasureHasLeakValue(file1, B);
        assertThatIntMeasureHasLeakValue(dir, ORDERED_BOTTOM_UP.indexOf(DIRECTORY));
        assertThatRatingMeasureHasLeakValue(dir, B);
        assertThatIntMeasureHasLeakValue(project, ORDERED_BOTTOM_UP.indexOf(PROJECT));
        assertThatRatingMeasureHasLeakValue(project, B);
        assertThatProjectChanged(result, project);
    }

    @Test
    public void do_nothing_if_project_has_not_been_analyzed() {
        // project has no snapshots
        List<QGChangeEvent> result = run(file1, newIncrementalFormula());
        assertThat(db.countRowsOfTable(db.getSession(), "live_measures")).isEqualTo(0);
        assertThatProjectNotChanged(result, project);
    }

    @Test
    public void do_nothing_if_input_components_are_empty() {
        List<QGChangeEvent> result = run(Collections.emptyList(), newIncrementalFormula());
        assertThat(db.countRowsOfTable(db.getSession(), "live_measures")).isEqualTo(0);
        assertThatProjectNotChanged(result, project);
    }

    @Test
    public void refresh_multiple_projects_at_the_same_time() {
        markProjectAsAnalyzed(project);
        ComponentDto project2 = db.components().insertMainBranch();
        ComponentDto fileInProject2 = db.components().insertComponent(ComponentTesting.newFileDto(project2));
        markProjectAsAnalyzed(project2);
        List<QGChangeEvent> result = run(Arrays.asList(file1, fileInProject2), newQualifierBasedIntFormula());
        // generated values depend on position of qualifier in Qualifiers.ORDERED_BOTTOM_UP (see formula)
        assertThatIntMeasureHasValue(file1, 0);
        assertThatIntMeasureHasValue(dir, 2);
        assertThatIntMeasureHasValue(project, 4);
        assertThatIntMeasureHasValue(fileInProject2, 0);
        assertThatIntMeasureHasValue(project2, 4);
        // no other measures generated
        assertThat(db.countRowsOfTable(db.getSession(), "live_measures")).isEqualTo(5);
        assertThatProjectChanged(result, project, project2);
    }

    @Test
    public void refresh_multiple_branches_at_the_same_time() {
        // FIXME
    }

    @Test
    public void event_contains_no_previousStatus_if_measure_does_not_exist() {
        markProjectAsAnalyzed(project);
        List<QGChangeEvent> result = run(file1);
        assertThat(result).extracting(QGChangeEvent::getPreviousStatus).containsExactly(Optional.empty());
    }

    @Test
    public void event_contains_no_previousStatus_if_measure_exists_and_has_no_value() {
        markProjectAsAnalyzed(project);
        db.measures().insertLiveMeasure(project, alertStatusMetric, ( m) -> m.setData(((String) (null))));
        List<QGChangeEvent> result = run(file1);
        assertThat(result).extracting(QGChangeEvent::getPreviousStatus).containsExactly(Optional.empty());
    }

    @Test
    public void event_contains_no_previousStatus_if_measure_exists_and_is_empty() {
        markProjectAsAnalyzed(project);
        db.measures().insertLiveMeasure(project, alertStatusMetric, ( m) -> m.setData(""));
        List<QGChangeEvent> result = run(file1);
        assertThat(result).extracting(QGChangeEvent::getPreviousStatus).containsExactly(Optional.empty());
    }

    @Test
    public void event_contains_no_previousStatus_if_measure_exists_and_is_not_a_level() {
        markProjectAsAnalyzed(project);
        db.measures().insertLiveMeasure(project, alertStatusMetric, ( m) -> m.setData("fooBar"));
        List<QGChangeEvent> result = run(file1);
        assertThat(result).extracting(QGChangeEvent::getPreviousStatus).containsExactly(Optional.empty());
    }

    @Test
    public void event_contains_newQualityGate_computed_by_LiveQualityGateComputer() {
        markProjectAsAnalyzed(project);
        db.measures().insertLiveMeasure(project, alertStatusMetric, ( m) -> m.setData(Metric.Level.ERROR.name()));
        db.measures().insertLiveMeasure(project, intMetric, ( m) -> m.setVariation(42.0).setValue(null));
        BranchDto branch = db.getDbClient().branchDao().selectByBranchKey(db.getSession(), project.projectUuid(), "master").orElseThrow(() -> new IllegalStateException("Can't find master branch"));
        List<QGChangeEvent> result = run(file1, newQualifierBasedIntLeakFormula());
        assertThat(result).extracting(QGChangeEvent::getQualityGateSupplier).extracting(Supplier::get).containsExactly(Optional.of(newQualityGate));
        Mockito.verify(qGateComputer).loadQualityGate(ArgumentMatchers.any(DbSession.class), ArgumentMatchers.eq(organization), ArgumentMatchers.eq(project), ArgumentMatchers.eq(branch));
        Mockito.verify(qGateComputer).getMetricsRelatedTo(qualityGate);
        Mockito.verify(qGateComputer).refreshGateStatus(ArgumentMatchers.eq(project), ArgumentMatchers.same(qualityGate), ArgumentMatchers.any(MeasureMatrix.class));
    }

    @Test
    public void exception_describes_context_when_a_formula_fails() {
        markProjectAsAnalyzed(project);
        Metric metric = create();
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(((("Fail to compute " + (metric.getKey())) + " on ") + (project.getDbKey())));
        run(project, new IssueMetricFormula(metric, false, ( context, issueCounter) -> {
            throw new NullPointerException("BOOM");
        }));
    }
}

