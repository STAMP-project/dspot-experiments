/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.table.client.gateway.local;


import RestartStrategies.FailureRateRestartStrategyConfiguration;
import RestartStrategies.RestartStrategyConfiguration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.Types;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.table.sinks.TableSink;
import org.apache.flink.table.sources.TableSource;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for {@link ExecutionContext}.
 */
public class ExecutionContextTest {
    private static final String DEFAULTS_ENVIRONMENT_FILE = "test-sql-client-defaults.yaml";

    private static final String STREAMING_ENVIRONMENT_FILE = "test-sql-client-streaming.yaml";

    @Test
    public void testExecutionConfig() throws Exception {
        final ExecutionContext<?> context = createDefaultExecutionContext();
        final ExecutionConfig config = context.createEnvironmentInstance().getExecutionConfig();
        Assert.assertEquals(99, config.getAutoWatermarkInterval());
        final RestartStrategies.RestartStrategyConfiguration restartConfig = config.getRestartStrategy();
        Assert.assertTrue((restartConfig instanceof RestartStrategies.FailureRateRestartStrategyConfiguration));
        final RestartStrategies.FailureRateRestartStrategyConfiguration failureRateStrategy = ((RestartStrategies.FailureRateRestartStrategyConfiguration) (restartConfig));
        Assert.assertEquals(10, failureRateStrategy.getMaxFailureRate());
        Assert.assertEquals(99000, failureRateStrategy.getFailureInterval().toMilliseconds());
        Assert.assertEquals(1000, failureRateStrategy.getDelayBetweenAttemptsInterval().toMilliseconds());
    }

    @Test
    public void testFunctions() throws Exception {
        final ExecutionContext<?> context = createDefaultExecutionContext();
        final TableEnvironment tableEnv = context.createEnvironmentInstance().getTableEnvironment();
        final String[] expected = new String[]{ "scalarUDF", "tableUDF", "aggregateUDF" };
        final String[] actual = tableEnv.listUserDefinedFunctions();
        Arrays.sort(expected);
        Arrays.sort(actual);
        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void testTables() throws Exception {
        final ExecutionContext<?> context = createDefaultExecutionContext();
        final Map<String, TableSource<?>> sources = context.getTableSources();
        final Map<String, TableSink<?>> sinks = context.getTableSinks();
        Assert.assertEquals(new HashSet(Arrays.asList("TableSourceSink", "TableNumber1", "TableNumber2")), sources.keySet());
        Assert.assertEquals(new HashSet(Collections.singletonList("TableSourceSink")), sinks.keySet());
        Assert.assertArrayEquals(new String[]{ "IntegerField1", "StringField1" }, sources.get("TableNumber1").getTableSchema().getFieldNames());
        Assert.assertArrayEquals(new TypeInformation[]{ Types.INT(), Types.STRING() }, sources.get("TableNumber1").getTableSchema().getFieldTypes());
        Assert.assertArrayEquals(new String[]{ "IntegerField2", "StringField2" }, sources.get("TableNumber2").getTableSchema().getFieldNames());
        Assert.assertArrayEquals(new TypeInformation[]{ Types.INT(), Types.STRING() }, sources.get("TableNumber2").getTableSchema().getFieldTypes());
        Assert.assertArrayEquals(new String[]{ "BooleanField", "StringField" }, sinks.get("TableSourceSink").getFieldNames());
        Assert.assertArrayEquals(new TypeInformation[]{ Types.BOOLEAN(), Types.STRING() }, sinks.get("TableSourceSink").getFieldTypes());
        final TableEnvironment tableEnv = context.createEnvironmentInstance().getTableEnvironment();
        Assert.assertArrayEquals(new String[]{ "TableNumber1", "TableNumber2", "TableSourceSink", "TestView1", "TestView2" }, tableEnv.listTables());
    }

    @Test
    public void testTemporalTables() throws Exception {
        final ExecutionContext<?> context = createStreamingExecutionContext();
        Assert.assertEquals(new HashSet(Arrays.asList("EnrichmentSource", "HistorySource")), context.getTableSources().keySet());
        final StreamTableEnvironment tableEnv = ((StreamTableEnvironment) (context.createEnvironmentInstance().getTableEnvironment()));
        Assert.assertArrayEquals(new String[]{ "EnrichmentSource", "HistorySource", "HistoryView", "TemporalTableUsage" }, tableEnv.listTables());
        Assert.assertArrayEquals(new String[]{ "SourceTemporalTable", "ViewTemporalTable" }, tableEnv.listUserDefinedFunctions());
        Assert.assertArrayEquals(new String[]{ "integerField", "stringField", "rowtimeField", "integerField0", "stringField0", "rowtimeField0" }, tableEnv.scan("TemporalTableUsage").getSchema().getFieldNames());
    }
}

