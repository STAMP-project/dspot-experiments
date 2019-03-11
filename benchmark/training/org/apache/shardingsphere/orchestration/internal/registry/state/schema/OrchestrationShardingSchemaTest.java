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
package org.apache.shardingsphere.orchestration.internal.registry.state.schema;


import ShardingConstant.LOGIC_SCHEMA_NAME;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class OrchestrationShardingSchemaTest {
    @Test
    public void assertNewOrchestrationSchemaWithDataSourceNameOnly() {
        OrchestrationShardingSchema actual = new OrchestrationShardingSchema("test_ds");
        Assert.assertThat(actual.getSchemaName(), CoreMatchers.is(LOGIC_SCHEMA_NAME));
        Assert.assertThat(actual.getDataSourceName(), CoreMatchers.is("test_ds"));
    }

    @Test
    public void assertNewOrchestrationSchemaWithSchemaNameAndDataSourceName() {
        OrchestrationShardingSchema actual = new OrchestrationShardingSchema("test_schema.test_ds");
        Assert.assertThat(actual.getSchemaName(), CoreMatchers.is("test_schema"));
        Assert.assertThat(actual.getDataSourceName(), CoreMatchers.is("test_ds"));
    }
}

