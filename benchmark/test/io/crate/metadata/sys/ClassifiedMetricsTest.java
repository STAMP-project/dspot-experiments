/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.metadata.sys;


import Plan.StatementType;
import Plan.StatementType.SELECT;
import java.util.concurrent.TimeUnit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;


public class ClassifiedMetricsTest {
    @Test
    public void testRecordHighDurationDoesNotCauseArrayIndexOutOfBoundsException() {
        ClassifiedMetrics histograms = new ClassifiedMetrics();
        histograms.recordValue(new io.crate.planner.operators.StatementClassifier.Classification(StatementType.SELECT), TimeUnit.MINUTES.toMillis(30));
    }

    @Test
    public void testRecordValueWithNegativeDurationDoesNotThrowException() {
        ClassifiedMetrics histograms = new ClassifiedMetrics();
        histograms.recordValue(new io.crate.planner.operators.StatementClassifier.Classification(StatementType.SELECT), (-2));
        MetricsView metricsView = histograms.iterator().next();
        MatcherAssert.assertThat(metricsView.totalCount(), Is.is(1L));
        MatcherAssert.assertThat(metricsView.minValue(), Is.is(0L));
    }

    @Test
    public void testSumOfAllDurations() {
        ClassifiedMetrics histograms = new ClassifiedMetrics();
        histograms.recordValue(new io.crate.planner.operators.StatementClassifier.Classification(StatementType.SELECT), TimeUnit.SECONDS.toMillis(30));
        histograms.recordValue(new io.crate.planner.operators.StatementClassifier.Classification(StatementType.SELECT), TimeUnit.SECONDS.toMillis(15));
        histograms.recordValue(new io.crate.planner.operators.StatementClassifier.Classification(StatementType.UPDATE), TimeUnit.SECONDS.toMillis(10));
        histograms.recordValue(new io.crate.planner.operators.StatementClassifier.Classification(StatementType.UPDATE), TimeUnit.SECONDS.toMillis(25));
        for (MetricsView metrics : histograms) {
            if (metrics.classification().type().equals(SELECT)) {
                MatcherAssert.assertThat(metrics.sumOfDurations(), Is.is(TimeUnit.SECONDS.toMillis(45)));
            } else {
                MatcherAssert.assertThat(metrics.sumOfDurations(), Is.is(TimeUnit.SECONDS.toMillis(35)));
            }
        }
    }
}

