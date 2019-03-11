/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.internal;


import State.CREATED;
import State.ERROR;
import State.NOT_RUNNING;
import State.PENDING_SHUTDOWN;
import State.REBALANCING;
import State.RUNNING;
import io.confluent.ksql.engine.KsqlEngine;
import io.confluent.ksql.util.KsqlConstants;
import io.confluent.ksql.util.QueryMetadata;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class KsqlEngineMetricsTest {
    private static final String METRIC_GROUP = "testGroup";

    private KsqlEngineMetrics engineMetrics;

    private static final String KSQL_SERVICE_ID = "test-ksql-service-id";

    private static final String metricNamePrefix = (KsqlConstants.KSQL_INTERNAL_TOPIC_PREFIX) + (KsqlEngineMetricsTest.KSQL_SERVICE_ID);

    @Mock
    private KsqlEngine ksqlEngine;

    @Mock
    private QueryMetadata query1;

    @Test
    public void shouldRemoveAllSensorsOnClose() {
        Assert.assertTrue(((engineMetrics.registeredSensors().size()) > 0));
        engineMetrics.close();
        engineMetrics.registeredSensors().forEach(( sensor) -> {
            assertThat(engineMetrics.getMetrics().getSensor(sensor.name()), is(nullValue()));
        });
    }

    @Test
    public void shouldRecordNumberOfActiveQueries() {
        Mockito.when(ksqlEngine.numberOfLiveQueries()).thenReturn(3);
        final double value = KsqlEngineMetricsTest.getMetricValue(engineMetrics.getMetrics(), ((KsqlEngineMetricsTest.metricNamePrefix) + "num-active-queries"));
        Assert.assertEquals(3.0, value, 0.0);
    }

    @Test
    public void shouldRecordNumberOfQueriesInCREATEDState() {
        Mockito.when(ksqlEngine.getPersistentQueries()).then(KsqlEngineMetricsTest.returnQueriesInState(3, CREATED));
        final long value = KsqlEngineMetricsTest.getLongMetricValue(engineMetrics.getMetrics(), ((KsqlEngineMetricsTest.metricNamePrefix) + "testGroup-query-stats-CREATED-queries"));
        Assert.assertEquals(3L, value);
    }

    @Test
    public void shouldRecordNumberOfQueriesInRUNNINGState() {
        Mockito.when(ksqlEngine.getPersistentQueries()).then(KsqlEngineMetricsTest.returnQueriesInState(3, RUNNING));
        final long value = KsqlEngineMetricsTest.getLongMetricValue(engineMetrics.getMetrics(), ((KsqlEngineMetricsTest.metricNamePrefix) + "testGroup-query-stats-RUNNING-queries"));
        Assert.assertEquals(3L, value);
    }

    @Test
    public void shouldRecordNumberOfQueriesInREBALANCINGState() {
        Mockito.when(ksqlEngine.getPersistentQueries()).then(KsqlEngineMetricsTest.returnQueriesInState(3, REBALANCING));
        final long value = KsqlEngineMetricsTest.getLongMetricValue(engineMetrics.getMetrics(), ((KsqlEngineMetricsTest.metricNamePrefix) + "testGroup-query-stats-REBALANCING-queries"));
        Assert.assertEquals(3L, value);
    }

    @Test
    public void shouldRecordNumberOfQueriesInPENDING_SHUTDOWNGState() {
        Mockito.when(ksqlEngine.getPersistentQueries()).then(KsqlEngineMetricsTest.returnQueriesInState(3, PENDING_SHUTDOWN));
        final long value = KsqlEngineMetricsTest.getLongMetricValue(engineMetrics.getMetrics(), ((KsqlEngineMetricsTest.metricNamePrefix) + "testGroup-query-stats-PENDING_SHUTDOWN-queries"));
        Assert.assertEquals(3L, value);
    }

    @Test
    public void shouldRecordNumberOfQueriesInERRORState() {
        Mockito.when(ksqlEngine.getPersistentQueries()).then(KsqlEngineMetricsTest.returnQueriesInState(3, ERROR));
        final long value = KsqlEngineMetricsTest.getLongMetricValue(engineMetrics.getMetrics(), ((KsqlEngineMetricsTest.metricNamePrefix) + "testGroup-query-stats-ERROR-queries"));
        Assert.assertEquals(3L, value);
    }

    @Test
    public void shouldRecordNumberOfQueriesInNOT_RUNNINGtate() {
        Mockito.when(ksqlEngine.getPersistentQueries()).then(KsqlEngineMetricsTest.returnQueriesInState(4, NOT_RUNNING));
        final long value = KsqlEngineMetricsTest.getLongMetricValue(engineMetrics.getMetrics(), ((KsqlEngineMetricsTest.metricNamePrefix) + "testGroup-query-stats-NOT_RUNNING-queries"));
        Assert.assertEquals(4L, value);
    }

    @Test
    public void shouldRecordNumberOfPersistentQueries() {
        Mockito.when(ksqlEngine.numberOfPersistentQueries()).thenReturn(3);
        final double value = KsqlEngineMetricsTest.getMetricValue(engineMetrics.getMetrics(), ((KsqlEngineMetricsTest.metricNamePrefix) + "num-persistent-queries"));
        Assert.assertEquals(3.0, value, 0.0);
    }

    @Test
    public void shouldRecordMessagesConsumed() {
        final int numMessagesConsumed = 500;
        KsqlEngineMetricsTest.consumeMessages(numMessagesConsumed, "group1");
        engineMetrics.updateMetrics();
        final double value = KsqlEngineMetricsTest.getMetricValue(engineMetrics.getMetrics(), ((KsqlEngineMetricsTest.metricNamePrefix) + "messages-consumed-per-sec"));
        Assert.assertEquals((numMessagesConsumed / 100), Math.floor(value), 0.01);
    }

    @Test
    public void shouldRecordMessagesProduced() {
        final int numMessagesProduced = 500;
        KsqlEngineMetricsTest.produceMessages(numMessagesProduced);
        engineMetrics.updateMetrics();
        final double value = KsqlEngineMetricsTest.getMetricValue(engineMetrics.getMetrics(), ((KsqlEngineMetricsTest.metricNamePrefix) + "messages-produced-per-sec"));
        Assert.assertEquals((numMessagesProduced / 100), Math.floor(value), 0.01);
    }

    @Test
    public void shouldRecordMessagesConsumedByQuery() {
        final int numMessagesConsumed = 500;
        KsqlEngineMetricsTest.consumeMessages(numMessagesConsumed, "group1");
        KsqlEngineMetricsTest.consumeMessages((numMessagesConsumed * 100), "group2");
        engineMetrics.updateMetrics();
        final double maxValue = KsqlEngineMetricsTest.getMetricValue(engineMetrics.getMetrics(), ((KsqlEngineMetricsTest.metricNamePrefix) + "messages-consumed-max"));
        Assert.assertEquals(numMessagesConsumed, Math.floor(maxValue), 5.0);
        final double minValue = KsqlEngineMetricsTest.getMetricValue(engineMetrics.getMetrics(), ((KsqlEngineMetricsTest.metricNamePrefix) + "messages-consumed-min"));
        Assert.assertEquals((numMessagesConsumed / 100), Math.floor(minValue), 0.01);
    }

    @Test
    public void shouldRegisterQueries() {
        // When:
        engineMetrics.registerQuery(query1);
        // Then:
        Mockito.verify(query1).registerQueryStateListener(ArgumentMatchers.any());
    }
}

