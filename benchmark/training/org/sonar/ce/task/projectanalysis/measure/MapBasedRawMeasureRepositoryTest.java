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
package org.sonar.ce.task.projectanalysis.measure;


import Component.Type.FILE;
import Measure.Level.OK;
import Metric.MetricType;
import System2.INSTANCE;
import com.google.common.collect.ImmutableList;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.sonar.api.rule.RuleKey;
import org.sonar.ce.task.projectanalysis.batch.BatchReportReader;
import org.sonar.ce.task.projectanalysis.component.Developer;
import org.sonar.ce.task.projectanalysis.component.DumbDeveloper;
import org.sonar.ce.task.projectanalysis.component.ReportComponent;
import org.sonar.ce.task.projectanalysis.metric.Metric;
import org.sonar.ce.task.projectanalysis.metric.MetricImpl;
import org.sonar.ce.task.projectanalysis.metric.MetricRepository;
import org.sonar.ce.task.projectanalysis.metric.ReportMetricValidator;
import org.sonar.db.DbClient;
import org.sonar.db.DbTester;
import org.sonar.db.rule.RuleDto;


@RunWith(DataProviderRunner.class)
public class MapBasedRawMeasureRepositoryTest {
    @Rule
    public DbTester dbTester = DbTester.create(INSTANCE);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static final String FILE_COMPONENT_KEY = "file cpt key";

    private static final ReportComponent FILE_COMPONENT = ReportComponent.builder(FILE, 1).setKey(MapBasedRawMeasureRepositoryTest.FILE_COMPONENT_KEY).setUuid("1").build();

    private static final ReportComponent OTHER_COMPONENT = ReportComponent.builder(FILE, 2).setKey("some other key").setUuid("2").build();

    private static final String METRIC_KEY_1 = "metric 1";

    private static final String METRIC_KEY_2 = "metric 2";

    private final Metric metric1 = Mockito.mock(Metric.class);

    private final Metric metric2 = Mockito.mock(Metric.class);

    private static final Measure SOME_MEASURE = Measure.newMeasureBuilder().create("some value");

    private static final RuleDto SOME_RULE = RuleDto.createFor(RuleKey.of("A", "1")).setId(963);

    private static final Developer SOME_DEVELOPER = new DumbDeveloper("DEV1");

    private ReportMetricValidator reportMetricValidator = Mockito.mock(ReportMetricValidator.class);

    private MetricRepository metricRepository = Mockito.mock(MetricRepository.class);

    private MapBasedRawMeasureRepository<Integer> underTest = new MapBasedRawMeasureRepository(( component) -> component.getReportAttributes().getRef());

    private DbClient mockedDbClient = Mockito.mock(DbClient.class);

    private BatchReportReader mockBatchReportReader = Mockito.mock(BatchReportReader.class);

    private MeasureRepositoryImpl underTestWithMock = new MeasureRepositoryImpl(mockedDbClient, mockBatchReportReader, metricRepository, reportMetricValidator);

    @Test(expected = NullPointerException.class)
    public void add_throws_NPE_if_Component_argument_is_null() {
        underTest.add(null, metric1, MapBasedRawMeasureRepositoryTest.SOME_MEASURE);
    }

    @Test(expected = NullPointerException.class)
    public void add_throws_NPE_if_Component_metric_is_null() {
        underTest.add(MapBasedRawMeasureRepositoryTest.FILE_COMPONENT, null, MapBasedRawMeasureRepositoryTest.SOME_MEASURE);
    }

    @Test(expected = NullPointerException.class)
    public void add_throws_NPE_if_Component_measure_is_null() {
        underTest.add(MapBasedRawMeasureRepositoryTest.FILE_COMPONENT, metric1, null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void add_throws_UOE_if_measure_already_exists() {
        underTest.add(MapBasedRawMeasureRepositoryTest.FILE_COMPONENT, metric1, MapBasedRawMeasureRepositoryTest.SOME_MEASURE);
        underTest.add(MapBasedRawMeasureRepositoryTest.FILE_COMPONENT, metric1, MapBasedRawMeasureRepositoryTest.SOME_MEASURE);
    }

    @Test(expected = NullPointerException.class)
    public void update_throws_NPE_if_Component_argument_is_null() {
        underTest.update(null, metric1, MapBasedRawMeasureRepositoryTest.SOME_MEASURE);
    }

    @Test(expected = NullPointerException.class)
    public void update_throws_NPE_if_Component_metric_is_null() {
        underTest.update(MapBasedRawMeasureRepositoryTest.FILE_COMPONENT, null, MapBasedRawMeasureRepositoryTest.SOME_MEASURE);
    }

    @Test(expected = NullPointerException.class)
    public void update_throws_NPE_if_Component_measure_is_null() {
        underTest.update(MapBasedRawMeasureRepositoryTest.FILE_COMPONENT, metric1, null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void update_throws_UOE_if_measure_does_not_exists() {
        underTest.update(MapBasedRawMeasureRepositoryTest.FILE_COMPONENT, metric1, MapBasedRawMeasureRepositoryTest.SOME_MEASURE);
    }

    private static final List<Measure> MEASURES = ImmutableList.of(Measure.newMeasureBuilder().create(1), Measure.newMeasureBuilder().create(1L), Measure.newMeasureBuilder().create(1.0, 1), Measure.newMeasureBuilder().create(true), Measure.newMeasureBuilder().create(false), Measure.newMeasureBuilder().create("sds"), Measure.newMeasureBuilder().create(OK), Measure.newMeasureBuilder().createNoValue());

    @Test
    public void add_accepts_NO_VALUE_as_measure_arg() {
        for (Metric.MetricType metricType : MetricType.values()) {
            underTest.add(MapBasedRawMeasureRepositoryTest.FILE_COMPONENT, new MetricImpl(1, ("key" + metricType), ("name" + metricType), metricType), Measure.newMeasureBuilder().createNoValue());
        }
    }

    @Test
    public void update_accepts_NO_VALUE_as_measure_arg() {
        for (Metric.MetricType metricType : MetricType.values()) {
            MetricImpl metric = new MetricImpl(1, ("key" + metricType), ("name" + metricType), metricType);
            underTest.add(MapBasedRawMeasureRepositoryTest.FILE_COMPONENT, metric, getSomeMeasureByValueType(metricType));
            underTest.update(MapBasedRawMeasureRepositoryTest.FILE_COMPONENT, metric, Measure.newMeasureBuilder().createNoValue());
        }
    }

    @Test
    public void update_supports_updating_to_the_same_value() {
        underTest.add(MapBasedRawMeasureRepositoryTest.FILE_COMPONENT, metric1, MapBasedRawMeasureRepositoryTest.SOME_MEASURE);
        underTest.update(MapBasedRawMeasureRepositoryTest.FILE_COMPONENT, metric1, MapBasedRawMeasureRepositoryTest.SOME_MEASURE);
    }

    @Test
    public void update_updates_the_stored_value() {
        Measure newMeasure = Measure.updatedMeasureBuilder(MapBasedRawMeasureRepositoryTest.SOME_MEASURE).create();
        underTest.add(MapBasedRawMeasureRepositoryTest.FILE_COMPONENT, metric1, MapBasedRawMeasureRepositoryTest.SOME_MEASURE);
        underTest.update(MapBasedRawMeasureRepositoryTest.FILE_COMPONENT, metric1, newMeasure);
        assertThat(underTest.getRawMeasure(MapBasedRawMeasureRepositoryTest.FILE_COMPONENT, metric1).get()).isSameAs(newMeasure);
    }

    @Test
    public void getRawMeasure_throws_NPE_without_reading_batch_report_if_component_arg_is_null() {
        try {
            underTestWithMock.getRawMeasure(null, metric1);
            Assert.fail("an NPE should have been raised");
        } catch (NullPointerException e) {
            Mockito.verifyNoMoreInteractions(mockBatchReportReader);
        }
    }

    @Test
    public void getRawMeasure_throws_NPE_without_reading_batch_report_if_metric_arg_is_null() {
        try {
            underTestWithMock.getRawMeasure(MapBasedRawMeasureRepositoryTest.FILE_COMPONENT, null);
            Assert.fail("an NPE should have been raised");
        } catch (NullPointerException e) {
            Mockito.verifyNoMoreInteractions(mockBatchReportReader);
        }
    }

    @Test
    public void getRawMeasure_returns_measure_added_through_add_method() {
        underTest.add(MapBasedRawMeasureRepositoryTest.FILE_COMPONENT, metric1, MapBasedRawMeasureRepositoryTest.SOME_MEASURE);
        Optional<Measure> res = underTest.getRawMeasure(MapBasedRawMeasureRepositoryTest.FILE_COMPONENT, metric1);
        assertThat(res).isPresent();
        assertThat(res.get()).isSameAs(MapBasedRawMeasureRepositoryTest.SOME_MEASURE);
        // make sure we really match on the specified component and metric
        assertThat(underTest.getRawMeasure(MapBasedRawMeasureRepositoryTest.OTHER_COMPONENT, metric1)).isNotPresent();
        assertThat(underTest.getRawMeasure(MapBasedRawMeasureRepositoryTest.FILE_COMPONENT, metric2)).isNotPresent();
    }

    @Test(expected = NullPointerException.class)
    public void getRawMeasures_for_metric_throws_NPE_if_Component_arg_is_null() {
        underTest.getRawMeasures(null, metric1);
    }

    @Test(expected = NullPointerException.class)
    public void getRawMeasures_for_metric_throws_NPE_if_Metric_arg_is_null() {
        underTest.getRawMeasures(MapBasedRawMeasureRepositoryTest.FILE_COMPONENT, null);
    }

    @Test
    public void getRawMeasures_for_metric_returns_empty_if_repository_is_empty() {
        assertThat(underTest.getRawMeasures(MapBasedRawMeasureRepositoryTest.FILE_COMPONENT, metric1)).isEmpty();
    }

    @Test
    public void getRawMeasures_for_metric_returns_developer_measure() {
        Measure devMeasure = Measure.newMeasureBuilder().forDeveloper(MapBasedRawMeasureRepositoryTest.SOME_DEVELOPER).createNoValue();
        underTest.add(MapBasedRawMeasureRepositoryTest.FILE_COMPONENT, metric1, devMeasure);
        Set<Measure> measures = underTest.getRawMeasures(MapBasedRawMeasureRepositoryTest.FILE_COMPONENT, metric1);
        assertThat(measures).hasSize(1);
        assertThat(measures.iterator().next()).isSameAs(devMeasure);
    }
}

