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
package org.apache.shardingsphere.core.routing.type.broadcast;


import org.apache.shardingsphere.core.routing.type.RoutingResult;
import org.apache.shardingsphere.core.rule.ShardingRule;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class TableBroadcastRoutingEngineTest {
    private ShardingRule shardingRule;

    @Test
    public void assertRoutingResultForDQLStatement() {
        Assert.assertThat(createDQLStatementRoutingResult(), CoreMatchers.instanceOf(RoutingResult.class));
    }

    @Test
    public void assertIsSingleRoutingForDQLStatement() {
        Assert.assertFalse(createDQLStatementRoutingResult().isSingleRouting());
    }

    @Test
    public void assertTableUnitsForDQLStatement() {
        RoutingResult routingResult = createDQLStatementRoutingResult();
        Assert.assertThat(routingResult.getTableUnits().getTableUnits().size(), CoreMatchers.is(0));
    }

    @Test
    public void assertRoutingResultForDDLStatement() {
        Assert.assertThat(createDDLStatementRoutingResult(), CoreMatchers.instanceOf(RoutingResult.class));
    }

    @Test
    public void assertIsSingleRoutingForDDLStatement() {
        Assert.assertFalse(createDDLStatementRoutingResult().isSingleRouting());
    }

    @Test
    public void assertTableUnitsForDDLStatement() {
        RoutingResult routingResult = createDDLStatementRoutingResult();
        Assert.assertThat(routingResult.getTableUnits().getTableUnits().size(), CoreMatchers.is(6));
    }
}

