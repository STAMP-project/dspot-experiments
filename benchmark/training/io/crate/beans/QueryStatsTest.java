/**
 * This file is part of a module with proprietary Enterprise Features.
 *
 * Licensed to Crate.io Inc. ("Crate.io") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 *
 * To use this file, Crate.io must have given you permission to enable and
 * use such Enterprise Features and you must have a valid Enterprise or
 * Subscription Agreement with Crate.io.  If you enable or use the Enterprise
 * Features, you represent and warrant that you have a valid Enterprise or
 * Subscription Agreement with Crate.io.  Your use of the Enterprise Features
 * if governed by the terms and conditions of your Enterprise or Subscription
 * Agreement with Crate.io.
 */
package io.crate.beans;


import QueryStats.Metric;
import StatementType.COPY;
import StatementType.DDL;
import StatementType.DELETE;
import StatementType.INSERT;
import StatementType.MANAGEMENT;
import StatementType.SELECT;
import StatementType.UNDEFINED;
import StatementType.UPDATE;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import io.crate.metadata.sys.MetricsView;
import io.crate.planner.Plan.StatementType;
import io.crate.planner.operators.StatementClassifier.Classification;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class QueryStatsTest {
    private static final Classification SELECT_CLASSIFICATION = new Classification(StatementType.SELECT);

    private static final Classification UPDATE_CLASSIFICATION = new Classification(StatementType.UPDATE);

    private static final Classification DELETE_CLASSIFICATION = new Classification(StatementType.DELETE);

    private static final Classification INSERT_CLASSIFICATION = new Classification(StatementType.INSERT);

    private static final Classification DDL_CLASSIFICATION = new Classification(StatementType.DDL);

    private static final Classification COPY_CLASSIFICATION = new Classification(StatementType.COPY);

    private final List<MetricsView> metrics = ImmutableList.of(createMetric(QueryStatsTest.SELECT_CLASSIFICATION, 35), createMetric(QueryStatsTest.SELECT_CLASSIFICATION, 35), createMetric(QueryStatsTest.UPDATE_CLASSIFICATION, 20), createMetric(QueryStatsTest.INSERT_CLASSIFICATION, 19), createMetric(QueryStatsTest.DELETE_CLASSIFICATION, 5), createMetric(QueryStatsTest.DELETE_CLASSIFICATION, 10), createMetric(QueryStatsTest.DDL_CLASSIFICATION, 1), createFailedExecutionMetric(QueryStatsTest.SELECT_CLASSIFICATION, 20), createFailedExecutionMetric(QueryStatsTest.COPY_CLASSIFICATION, 0));

    @Test
    public void testTrackedStatementTypes() {
        List<MetricsView> oneMetricForEachStatementType = new ArrayList<>();
        for (StatementType type : StatementType.values()) {
            if (type.equals(UNDEFINED)) {
                continue;
            }
            oneMetricForEachStatementType.add(createMetric(new Classification(type), 1));
        }
        Map<StatementType, QueryStats.Metric> metricsByCommand = QueryStats.createMetricsMap(oneMetricForEachStatementType);
        Assert.assertThat(metricsByCommand.size(), Matchers.is(7));
        Assert.assertThat(metricsByCommand.get(SELECT), Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(metricsByCommand.get(UPDATE), Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(metricsByCommand.get(INSERT), Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(metricsByCommand.get(DELETE), Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(metricsByCommand.get(DDL), Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(metricsByCommand.get(MANAGEMENT), Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(metricsByCommand.get(COPY), Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(metricsByCommand.get(UNDEFINED), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void testCreateMetricsMap() {
        Map<StatementType, QueryStats.Metric> metricsByCommand = QueryStats.createMetricsMap(metrics);
        Assert.assertThat(metricsByCommand.size(), Matchers.is(6));
        Assert.assertThat(metricsByCommand.get(SELECT).totalCount(), Matchers.is(3L));
        Assert.assertThat(metricsByCommand.get(SELECT).failedCount(), Matchers.is(1L));
        Assert.assertThat(metricsByCommand.get(SELECT).sumOfDurations(), Matchers.is(90L));
        Assert.assertThat(metricsByCommand.get(INSERT).totalCount(), Matchers.is(1L));
        Assert.assertThat(metricsByCommand.get(INSERT).failedCount(), Matchers.is(0L));
        Assert.assertThat(metricsByCommand.get(INSERT).sumOfDurations(), Matchers.is(19L));
        Assert.assertThat(metricsByCommand.get(UPDATE).totalCount(), Matchers.is(1L));
        Assert.assertThat(metricsByCommand.get(UPDATE).failedCount(), Matchers.is(0L));
        Assert.assertThat(metricsByCommand.get(UPDATE).sumOfDurations(), Matchers.is(20L));
        Assert.assertThat(metricsByCommand.get(DELETE).totalCount(), Matchers.is(2L));
        Assert.assertThat(metricsByCommand.get(DELETE).failedCount(), Matchers.is(0L));
        Assert.assertThat(metricsByCommand.get(DELETE).sumOfDurations(), Matchers.is(15L));
        Assert.assertThat(metricsByCommand.get(DDL).totalCount(), Matchers.is(1L));
        Assert.assertThat(metricsByCommand.get(DDL).failedCount(), Matchers.is(0L));
        Assert.assertThat(metricsByCommand.get(DDL).sumOfDurations(), Matchers.is(1L));
        Assert.assertThat(metricsByCommand.get(COPY).totalCount(), Matchers.is(1L));
        Assert.assertThat(metricsByCommand.get(COPY).failedCount(), Matchers.is(1L));
        Assert.assertThat(metricsByCommand.get(COPY).sumOfDurations(), Matchers.is(0L));
    }

    @Test
    public void testSameTypeWithDifferentLabelsClassificationsAreMerged() {
        List<MetricsView> selectMetrics = ImmutableList.of(createMetric(QueryStatsTest.SELECT_CLASSIFICATION, 35), createMetric(new Classification(StatementType.SELECT, ImmutableSet.of("lookup")), 55));
        Map<StatementType, QueryStats.Metric> metricsByCommand = QueryStats.createMetricsMap(selectMetrics);
        Assert.assertThat(metricsByCommand.size(), Matchers.is(1));
        Assert.assertThat(metricsByCommand.get(SELECT).totalCount(), Matchers.is(2L));
        Assert.assertThat(metricsByCommand.get(SELECT).sumOfDurations(), Matchers.is(90L));
    }
}

