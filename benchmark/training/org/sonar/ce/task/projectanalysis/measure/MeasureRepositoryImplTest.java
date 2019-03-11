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
import com.google.common.collect.SetMultimap;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.sonar.ce.task.projectanalysis.batch.BatchReportReader;
import org.sonar.ce.task.projectanalysis.batch.BatchReportReaderRule;
import org.sonar.ce.task.projectanalysis.component.ReportComponent;
import org.sonar.ce.task.projectanalysis.metric.Metric;
import org.sonar.ce.task.projectanalysis.metric.MetricImpl;
import org.sonar.ce.task.projectanalysis.metric.MetricRepository;
import org.sonar.ce.task.projectanalysis.metric.ReportMetricValidator;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.scanner.protocol.output.ScannerReport;
import org.sonar.scanner.protocol.output.ScannerReport.Measure.StringValue;


@RunWith(DataProviderRunner.class)
public class MeasureRepositoryImplTest {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Rule
    public DbTester dbTester = DbTester.create(INSTANCE);

    @Rule
    public BatchReportReaderRule reportReader = new BatchReportReaderRule();

    private static final String FILE_COMPONENT_KEY = "file cpt key";

    private static final ReportComponent FILE_COMPONENT = ReportComponent.builder(FILE, 1).setKey(MeasureRepositoryImplTest.FILE_COMPONENT_KEY).build();

    private static final ReportComponent OTHER_COMPONENT = ReportComponent.builder(FILE, 2).setKey("some other key").build();

    private static final String METRIC_KEY_1 = "metric 1";

    private static final int METRIC_ID_1 = 1;

    private static final String METRIC_KEY_2 = "metric 2";

    private static final int METRIC_ID_2 = 2;

    private final Metric metric1 = Mockito.mock(Metric.class);

    private final Metric metric2 = Mockito.mock(Metric.class);

    private static final String LAST_ANALYSIS_UUID = "u123";

    private static final String OTHER_ANALYSIS_UUID = "u369";

    private static final Measure SOME_MEASURE = Measure.newMeasureBuilder().create("some value");

    private static final String SOME_DATA = "some data";

    private ReportMetricValidator reportMetricValidator = Mockito.mock(ReportMetricValidator.class);

    private DbClient dbClient = dbTester.getDbClient();

    private MetricRepository metricRepository = Mockito.mock(MetricRepository.class);

    private MeasureRepositoryImpl underTest = new MeasureRepositoryImpl(dbClient, reportReader, metricRepository, reportMetricValidator);

    private DbClient mockedDbClient = Mockito.mock(DbClient.class);

    private BatchReportReader mockBatchReportReader = Mockito.mock(BatchReportReader.class);

    private MeasureRepositoryImpl underTestWithMock = new MeasureRepositoryImpl(mockedDbClient, mockBatchReportReader, metricRepository, reportMetricValidator);

    private DbSession dbSession = dbTester.getSession();

    @Test
    public void getBaseMeasure_throws_NPE_and_does_not_open_session_if_component_is_null() {
        try {
            underTestWithMock.getBaseMeasure(null, metric1);
            Assert.fail("an NPE should have been raised");
        } catch (NullPointerException e) {
            Mockito.verifyZeroInteractions(mockedDbClient);
        }
    }

    @Test
    public void getBaseMeasure_throws_NPE_and_does_not_open_session_if_metric_is_null() {
        try {
            underTestWithMock.getBaseMeasure(MeasureRepositoryImplTest.FILE_COMPONENT, null);
            Assert.fail("an NPE should have been raised");
        } catch (NullPointerException e) {
            Mockito.verifyZeroInteractions(mockedDbClient);
        }
    }

    @Test
    public void getBaseMeasure_returns_absent_if_measure_does_not_exist_in_DB() {
        Optional<Measure> res = underTest.getBaseMeasure(MeasureRepositoryImplTest.FILE_COMPONENT, metric1);
        assertThat(res).isNotPresent();
    }

    @Test
    public void getBaseMeasure_returns_Measure_if_measure_of_last_snapshot_only_in_DB() {
        dbTester.prepareDbUnit(getClass(), "shared.xml");
        dbClient.measureDao().insert(dbSession, MeasureRepositoryImplTest.createMeasureDto(MeasureRepositoryImplTest.METRIC_ID_1, MeasureRepositoryImplTest.FILE_COMPONENT.getUuid(), MeasureRepositoryImplTest.LAST_ANALYSIS_UUID));
        dbClient.measureDao().insert(dbSession, MeasureRepositoryImplTest.createMeasureDto(MeasureRepositoryImplTest.METRIC_ID_2, MeasureRepositoryImplTest.FILE_COMPONENT.getUuid(), MeasureRepositoryImplTest.OTHER_ANALYSIS_UUID));
        dbSession.commit();
        // metric 1 is associated to snapshot with "last=true"
        Optional<Measure> res = underTest.getBaseMeasure(MeasureRepositoryImplTest.FILE_COMPONENT, metric1);
        assertThat(res).isPresent();
        assertThat(res.get().getStringValue()).isEqualTo(MeasureRepositoryImplTest.SOME_DATA);
        // metric 2 is associated to snapshot with "last=false" => not retrieved
        res = underTest.getBaseMeasure(MeasureRepositoryImplTest.FILE_COMPONENT, metric2);
        assertThat(res).isNotPresent();
    }

    @Test
    public void add_throws_NPE_if_Component_argument_is_null() {
        expectedException.expect(NullPointerException.class);
        underTest.add(null, metric1, MeasureRepositoryImplTest.SOME_MEASURE);
    }

    @Test
    public void add_throws_NPE_if_Component_metric_is_null() {
        expectedException.expect(NullPointerException.class);
        underTest.add(MeasureRepositoryImplTest.FILE_COMPONENT, null, MeasureRepositoryImplTest.SOME_MEASURE);
    }

    @Test
    public void add_throws_NPE_if_Component_measure_is_null() {
        expectedException.expect(NullPointerException.class);
        underTest.add(MeasureRepositoryImplTest.FILE_COMPONENT, metric1, null);
    }

    @Test
    public void add_throws_UOE_if_measure_already_exists() {
        expectedException.expect(UnsupportedOperationException.class);
        underTest.add(MeasureRepositoryImplTest.FILE_COMPONENT, metric1, MeasureRepositoryImplTest.SOME_MEASURE);
        underTest.add(MeasureRepositoryImplTest.FILE_COMPONENT, metric1, MeasureRepositoryImplTest.SOME_MEASURE);
    }

    @Test
    public void update_throws_NPE_if_Component_argument_is_null() {
        expectedException.expect(NullPointerException.class);
        underTest.update(null, metric1, MeasureRepositoryImplTest.SOME_MEASURE);
    }

    @Test
    public void update_throws_NPE_if_Component_metric_is_null() {
        expectedException.expect(NullPointerException.class);
        underTest.update(MeasureRepositoryImplTest.FILE_COMPONENT, null, MeasureRepositoryImplTest.SOME_MEASURE);
    }

    @Test
    public void update_throws_NPE_if_Component_measure_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expect(NullPointerException.class);
        underTest.update(MeasureRepositoryImplTest.FILE_COMPONENT, metric1, null);
    }

    @Test
    public void update_throws_UOE_if_measure_does_not_exists() {
        expectedException.expect(UnsupportedOperationException.class);
        underTest.update(MeasureRepositoryImplTest.FILE_COMPONENT, metric1, MeasureRepositoryImplTest.SOME_MEASURE);
    }

    private static final List<Measure> MEASURES = ImmutableList.of(Measure.newMeasureBuilder().create(1), Measure.newMeasureBuilder().create(1L), Measure.newMeasureBuilder().create(1.0, 1), Measure.newMeasureBuilder().create(true), Measure.newMeasureBuilder().create(false), Measure.newMeasureBuilder().create("sds"), Measure.newMeasureBuilder().create(OK), Measure.newMeasureBuilder().createNoValue());

    @Test
    public void add_accepts_NO_VALUE_as_measure_arg() {
        for (Metric.MetricType metricType : MetricType.values()) {
            underTest.add(MeasureRepositoryImplTest.FILE_COMPONENT, new MetricImpl(1, ("key" + metricType), ("name" + metricType), metricType), Measure.newMeasureBuilder().createNoValue());
        }
    }

    @Test
    public void update_accepts_NO_VALUE_as_measure_arg() {
        for (Metric.MetricType metricType : MetricType.values()) {
            MetricImpl metric = new MetricImpl(1, ("key" + metricType), ("name" + metricType), metricType);
            underTest.add(MeasureRepositoryImplTest.FILE_COMPONENT, metric, getSomeMeasureByValueType(metricType));
            underTest.update(MeasureRepositoryImplTest.FILE_COMPONENT, metric, Measure.newMeasureBuilder().createNoValue());
        }
    }

    @Test
    public void update_supports_updating_to_the_same_value() {
        underTest.add(MeasureRepositoryImplTest.FILE_COMPONENT, metric1, MeasureRepositoryImplTest.SOME_MEASURE);
        underTest.update(MeasureRepositoryImplTest.FILE_COMPONENT, metric1, MeasureRepositoryImplTest.SOME_MEASURE);
    }

    @Test
    public void update_updates_the_stored_value() {
        Measure newMeasure = Measure.updatedMeasureBuilder(MeasureRepositoryImplTest.SOME_MEASURE).create();
        underTest.add(MeasureRepositoryImplTest.FILE_COMPONENT, metric1, MeasureRepositoryImplTest.SOME_MEASURE);
        underTest.update(MeasureRepositoryImplTest.FILE_COMPONENT, metric1, newMeasure);
        assertThat(underTest.getRawMeasure(MeasureRepositoryImplTest.FILE_COMPONENT, metric1).get()).isSameAs(newMeasure);
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
            underTestWithMock.getRawMeasure(MeasureRepositoryImplTest.FILE_COMPONENT, null);
            Assert.fail("an NPE should have been raised");
        } catch (NullPointerException e) {
            Mockito.verifyNoMoreInteractions(mockBatchReportReader);
        }
    }

    @Test
    public void getRawMeasure_returns_measure_added_through_add_method() {
        underTest.add(MeasureRepositoryImplTest.FILE_COMPONENT, metric1, MeasureRepositoryImplTest.SOME_MEASURE);
        Optional<Measure> res = underTest.getRawMeasure(MeasureRepositoryImplTest.FILE_COMPONENT, metric1);
        assertThat(res).isPresent();
        assertThat(res.get()).isSameAs(MeasureRepositoryImplTest.SOME_MEASURE);
        // make sure we really match on the specified component and metric
        assertThat(underTest.getRawMeasure(MeasureRepositoryImplTest.OTHER_COMPONENT, metric1)).isNotPresent();
        assertThat(underTest.getRawMeasure(MeasureRepositoryImplTest.FILE_COMPONENT, metric2)).isNotPresent();
    }

    @Test
    public void getRawMeasure_returns_measure_from_batch_if_not_added_through_add_method() {
        String value = "trololo";
        Mockito.when(reportMetricValidator.validate(MeasureRepositoryImplTest.METRIC_KEY_1)).thenReturn(true);
        reportReader.putMeasures(MeasureRepositoryImplTest.FILE_COMPONENT.getReportAttributes().getRef(), ImmutableList.of(ScannerReport.Measure.newBuilder().setMetricKey(MeasureRepositoryImplTest.METRIC_KEY_1).setStringValue(StringValue.newBuilder().setValue(value)).build()));
        Optional<Measure> res = underTest.getRawMeasure(MeasureRepositoryImplTest.FILE_COMPONENT, metric1);
        assertThat(res).isPresent();
        assertThat(res.get().getStringValue()).isEqualTo(value);
        // make sure we really match on the specified component and metric
        assertThat(underTest.getRawMeasure(MeasureRepositoryImplTest.FILE_COMPONENT, metric2)).isNotPresent();
        assertThat(underTest.getRawMeasure(MeasureRepositoryImplTest.OTHER_COMPONENT, metric1)).isNotPresent();
    }

    @Test
    public void getRawMeasure_returns_only_validate_measure_from_batch_if_not_added_through_add_method() {
        Mockito.when(reportMetricValidator.validate(MeasureRepositoryImplTest.METRIC_KEY_1)).thenReturn(true);
        Mockito.when(reportMetricValidator.validate(MeasureRepositoryImplTest.METRIC_KEY_2)).thenReturn(false);
        reportReader.putMeasures(MeasureRepositoryImplTest.FILE_COMPONENT.getReportAttributes().getRef(), ImmutableList.of(ScannerReport.Measure.newBuilder().setMetricKey(MeasureRepositoryImplTest.METRIC_KEY_1).setStringValue(StringValue.newBuilder().setValue("value1")).build(), ScannerReport.Measure.newBuilder().setMetricKey(MeasureRepositoryImplTest.METRIC_KEY_2).setStringValue(StringValue.newBuilder().setValue("value2")).build()));
        assertThat(underTest.getRawMeasure(MeasureRepositoryImplTest.FILE_COMPONENT, metric1)).isPresent();
        assertThat(underTest.getRawMeasure(MeasureRepositoryImplTest.FILE_COMPONENT, metric2)).isNotPresent();
    }

    @Test
    public void getRawMeasure_retrieves_added_measure_over_batch_measure() {
        Mockito.when(reportMetricValidator.validate(MeasureRepositoryImplTest.METRIC_KEY_1)).thenReturn(true);
        reportReader.putMeasures(MeasureRepositoryImplTest.FILE_COMPONENT.getReportAttributes().getRef(), ImmutableList.of(ScannerReport.Measure.newBuilder().setMetricKey(MeasureRepositoryImplTest.METRIC_KEY_1).setStringValue(StringValue.newBuilder().setValue("some value")).build()));
        Measure addedMeasure = MeasureRepositoryImplTest.SOME_MEASURE;
        underTest.add(MeasureRepositoryImplTest.FILE_COMPONENT, metric1, addedMeasure);
        Optional<Measure> res = underTest.getRawMeasure(MeasureRepositoryImplTest.FILE_COMPONENT, metric1);
        assertThat(res).isPresent();
        assertThat(res.get()).isSameAs(addedMeasure);
    }

    @Test
    public void getRawMeasure_retrieves_measure_from_batch_and_caches_it_locally_so_that_it_can_be_updated() {
        Mockito.when(reportMetricValidator.validate(MeasureRepositoryImplTest.METRIC_KEY_1)).thenReturn(true);
        reportReader.putMeasures(MeasureRepositoryImplTest.FILE_COMPONENT.getReportAttributes().getRef(), ImmutableList.of(ScannerReport.Measure.newBuilder().setMetricKey(MeasureRepositoryImplTest.METRIC_KEY_1).setStringValue(StringValue.newBuilder().setValue("some value")).build()));
        Optional<Measure> measure = underTest.getRawMeasure(MeasureRepositoryImplTest.FILE_COMPONENT, metric1);
        underTest.update(MeasureRepositoryImplTest.FILE_COMPONENT, metric1, Measure.updatedMeasureBuilder(measure.get()).create());
    }

    @Test
    public void getRawMeasures_for_metric_throws_NPE_if_Component_arg_is_null() {
        expectedException.expect(NullPointerException.class);
        underTest.getRawMeasures(null, metric1);
    }

    @Test
    public void getRawMeasures_for_metric_throws_NPE_if_Metric_arg_is_null() {
        expectedException.expect(NullPointerException.class);
        underTest.getRawMeasures(MeasureRepositoryImplTest.FILE_COMPONENT, null);
    }

    @Test
    public void getRawMeasures_for_metric_returns_empty_if_repository_is_empty() {
        assertThat(underTest.getRawMeasures(MeasureRepositoryImplTest.FILE_COMPONENT, metric1)).isEmpty();
    }

    @Test
    public void getRawMeasures_returns_added_measures_over_batch_measures() {
        Mockito.when(reportMetricValidator.validate(MeasureRepositoryImplTest.METRIC_KEY_1)).thenReturn(true);
        Mockito.when(reportMetricValidator.validate(MeasureRepositoryImplTest.METRIC_KEY_2)).thenReturn(true);
        ScannerReport.Measure batchMeasure1 = ScannerReport.Measure.newBuilder().setMetricKey(MeasureRepositoryImplTest.METRIC_KEY_1).setStringValue(StringValue.newBuilder().setValue("some value")).build();
        ScannerReport.Measure batchMeasure2 = ScannerReport.Measure.newBuilder().setMetricKey(MeasureRepositoryImplTest.METRIC_KEY_2).setStringValue(StringValue.newBuilder().setValue("some value")).build();
        reportReader.putMeasures(MeasureRepositoryImplTest.FILE_COMPONENT.getReportAttributes().getRef(), ImmutableList.of(batchMeasure1, batchMeasure2));
        Measure addedMeasure = MeasureRepositoryImplTest.SOME_MEASURE;
        underTest.add(MeasureRepositoryImplTest.FILE_COMPONENT, metric1, addedMeasure);
        SetMultimap<String, Measure> rawMeasures = underTest.getRawMeasures(MeasureRepositoryImplTest.FILE_COMPONENT);
        assertThat(rawMeasures.keySet()).hasSize(2);
        assertThat(rawMeasures.get(MeasureRepositoryImplTest.METRIC_KEY_1)).containsOnly(addedMeasure);
        assertThat(rawMeasures.get(MeasureRepositoryImplTest.METRIC_KEY_2)).containsOnly(Measure.newMeasureBuilder().create("some value"));
    }
}

