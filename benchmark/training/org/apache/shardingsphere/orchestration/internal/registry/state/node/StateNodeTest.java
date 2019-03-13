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
package org.apache.shardingsphere.orchestration.internal.registry.state.node;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class StateNodeTest {
    private StateNode stateNode = new StateNode("test");

    @Test
    public void assertGetInstancesNodeFullPath() {
        Assert.assertThat(stateNode.getInstancesNodeFullPath("testId"), CoreMatchers.is("/test/state/instances/testId"));
    }

    @Test
    public void assertGetDataSourcesNodeFullRootPath() {
        Assert.assertThat(stateNode.getDataSourcesNodeFullRootPath(), CoreMatchers.is("/test/state/datasources"));
    }

    @Test
    public void assertGetDataSourcesNodeFullPath() {
        Assert.assertThat(stateNode.getDataSourcesNodeFullPath("sharding_db"), CoreMatchers.is("/test/state/datasources/sharding_db"));
    }

    @Test
    public void assertGetOrchestrationShardingSchema() {
        Assert.assertThat(stateNode.getOrchestrationShardingSchema("/test/state/datasources/master_slave_db.slave_ds_0").getSchemaName(), CoreMatchers.is("master_slave_db"));
    }
}

