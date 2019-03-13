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
package org.apache.shardingsphere.orchestration.internal.registry.state.listener;


import org.apache.shardingsphere.orchestration.internal.registry.state.schema.OrchestrationShardingSchema;
import org.apache.shardingsphere.orchestration.reg.api.RegistryCenter;
import org.apache.shardingsphere.orchestration.reg.listener.DataChangedEvent;
import org.apache.shardingsphere.orchestration.reg.listener.DataChangedEvent.ChangedType;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public final class DataSourceStateChangedListenerTest {
    private DataSourceStateChangedListener dataSourceStateChangedListener;

    @Mock
    private RegistryCenter regCenter;

    @Test
    public void assertCreateShardingOrchestrationEvent() {
        OrchestrationShardingSchema expected = new OrchestrationShardingSchema("master_slave_db", "slave_ds_0");
        DataChangedEvent dataChangedEvent = new DataChangedEvent("/test/state/datasources/master_slave_db.slave_ds_0", "disabled", ChangedType.UPDATED);
        Assert.assertThat(dataSourceStateChangedListener.createShardingOrchestrationEvent(dataChangedEvent).getShardingSchema().getSchemaName(), CoreMatchers.is(expected.getSchemaName()));
    }
}

