/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.shardingsphere.opentracing.hook;


import ShardingTags.COMPONENT_NAME;
import ShardingTags.DB_BIND_VARIABLES;
import Tags.COMPONENT;
import Tags.DB_INSTANCE;
import Tags.DB_STATEMENT;
import Tags.DB_TYPE;
import Tags.PEER_HOSTNAME;
import Tags.PEER_PORT;
import Tags.SPAN_KIND;
import Tags.SPAN_KIND_CLIENT;
import io.opentracing.ActiveSpan;
import io.opentracing.mock.MockSpan;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import org.apache.shardingsphere.core.executor.ShardingExecuteDataMap;
import org.apache.shardingsphere.core.metadata.datasource.DataSourceMetaData;
import org.apache.shardingsphere.core.spi.hook.SPISQLExecutionHook;
import org.apache.shardingsphere.spi.hook.SQLExecutionHook;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public final class OpenTracingSQLExecutionHookTest extends BaseOpenTracingHookTest {
    private final SQLExecutionHook sqlExecutionHook = new SPISQLExecutionHook();

    private ActiveSpan activeSpan;

    @Test
    public void assertExecuteSuccessForTrunkThread() {
        DataSourceMetaData dataSourceMetaData = Mockito.mock(DataSourceMetaData.class);
        Mockito.when(dataSourceMetaData.getHostName()).thenReturn("localhost");
        Mockito.when(dataSourceMetaData.getPort()).thenReturn(8888);
        sqlExecutionHook.start(createRouteUnit("success_ds", "SELECT * FROM success_tbl;", Arrays.<Object>asList("1", 2)), dataSourceMetaData, true, null);
        sqlExecutionHook.finishSuccess();
        MockSpan actual = getActualSpan();
        Assert.assertThat(actual.operationName(), CoreMatchers.is("/Sharding-Sphere/executeSQL/"));
        Map<String, Object> actualTags = actual.tags();
        Assert.assertThat(actualTags.get(COMPONENT.getKey()), CoreMatchers.<Object>is(COMPONENT_NAME));
        Assert.assertThat(actualTags.get(SPAN_KIND.getKey()), CoreMatchers.<Object>is(SPAN_KIND_CLIENT));
        Assert.assertThat(actualTags.get(PEER_HOSTNAME.getKey()), CoreMatchers.<Object>is("localhost"));
        Assert.assertThat(actualTags.get(PEER_PORT.getKey()), CoreMatchers.<Object>is(8888));
        Assert.assertThat(actualTags.get(DB_TYPE.getKey()), CoreMatchers.<Object>is("sql"));
        Assert.assertThat(actualTags.get(DB_INSTANCE.getKey()), CoreMatchers.<Object>is("success_ds"));
        Assert.assertThat(actualTags.get(DB_STATEMENT.getKey()), CoreMatchers.<Object>is("SELECT * FROM success_tbl;"));
        Assert.assertThat(actualTags.get(DB_BIND_VARIABLES.getKey()), CoreMatchers.<Object>is("[1, 2]"));
        Mockito.verify(activeSpan, Mockito.times(0)).deactivate();
    }

    @Test
    public void assertExecuteSuccessForBranchThread() {
        DataSourceMetaData dataSourceMetaData = Mockito.mock(DataSourceMetaData.class);
        Mockito.when(dataSourceMetaData.getHostName()).thenReturn("localhost");
        Mockito.when(dataSourceMetaData.getPort()).thenReturn(8888);
        sqlExecutionHook.start(createRouteUnit("success_ds", "SELECT * FROM success_tbl;", Arrays.<Object>asList("1", 2)), dataSourceMetaData, false, ShardingExecuteDataMap.getDataMap());
        sqlExecutionHook.finishSuccess();
        MockSpan actual = getActualSpan();
        Assert.assertThat(actual.operationName(), CoreMatchers.is("/Sharding-Sphere/executeSQL/"));
        Map<String, Object> actualTags = actual.tags();
        Assert.assertThat(actualTags.get(COMPONENT.getKey()), CoreMatchers.<Object>is(COMPONENT_NAME));
        Assert.assertThat(actualTags.get(SPAN_KIND.getKey()), CoreMatchers.<Object>is(SPAN_KIND_CLIENT));
        Assert.assertThat(actualTags.get(PEER_HOSTNAME.getKey()), CoreMatchers.<Object>is("localhost"));
        Assert.assertThat(actualTags.get(PEER_PORT.getKey()), CoreMatchers.<Object>is(8888));
        Assert.assertThat(actualTags.get(DB_TYPE.getKey()), CoreMatchers.<Object>is("sql"));
        Assert.assertThat(actualTags.get(DB_INSTANCE.getKey()), CoreMatchers.<Object>is("success_ds"));
        Assert.assertThat(actualTags.get(DB_STATEMENT.getKey()), CoreMatchers.<Object>is("SELECT * FROM success_tbl;"));
        Assert.assertThat(actualTags.get(DB_BIND_VARIABLES.getKey()), CoreMatchers.<Object>is("[1, 2]"));
        Mockito.verify(activeSpan).deactivate();
    }

    @Test
    public void assertExecuteFailure() {
        DataSourceMetaData dataSourceMetaData = Mockito.mock(DataSourceMetaData.class);
        Mockito.when(dataSourceMetaData.getHostName()).thenReturn("localhost");
        Mockito.when(dataSourceMetaData.getPort()).thenReturn(8888);
        sqlExecutionHook.start(createRouteUnit("failure_ds", "SELECT * FROM failure_tbl;", Collections.emptyList()), dataSourceMetaData, true, null);
        sqlExecutionHook.finishFailure(new RuntimeException("SQL execution error"));
        MockSpan actual = getActualSpan();
        Assert.assertThat(actual.operationName(), CoreMatchers.is("/Sharding-Sphere/executeSQL/"));
        Map<String, Object> actualTags = actual.tags();
        Assert.assertThat(actualTags.get(COMPONENT.getKey()), CoreMatchers.<Object>is(COMPONENT_NAME));
        Assert.assertThat(actualTags.get(SPAN_KIND.getKey()), CoreMatchers.<Object>is(SPAN_KIND_CLIENT));
        Assert.assertThat(actualTags.get(PEER_HOSTNAME.getKey()), CoreMatchers.<Object>is("localhost"));
        Assert.assertThat(actualTags.get(PEER_PORT.getKey()), CoreMatchers.<Object>is(8888));
        Assert.assertThat(actualTags.get(DB_TYPE.getKey()), CoreMatchers.<Object>is("sql"));
        Assert.assertThat(actualTags.get(DB_INSTANCE.getKey()), CoreMatchers.<Object>is("failure_ds"));
        Assert.assertThat(actualTags.get(DB_STATEMENT.getKey()), CoreMatchers.<Object>is("SELECT * FROM failure_tbl;"));
        Assert.assertThat(actualTags.get(DB_BIND_VARIABLES.getKey()), CoreMatchers.<Object>is(""));
        assertSpanError(RuntimeException.class, "SQL execution error");
        Mockito.verify(activeSpan, Mockito.times(0)).deactivate();
    }
}

