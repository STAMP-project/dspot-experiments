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
package org.apache.shardingsphere.core.routing.type.defaultdb;


import java.util.List;
import org.apache.shardingsphere.core.routing.type.RoutingResult;
import org.apache.shardingsphere.core.routing.type.TableUnit;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class DefaultDatabaseRoutingEngineTest {
    private DefaultDatabaseRoutingEngine defaultDatabaseRoutingEngine;

    @Test
    public void assertRoute() {
        RoutingResult routingResult = defaultDatabaseRoutingEngine.route();
        List<TableUnit> tableUnitList = new java.util.ArrayList(routingResult.getTableUnits().getTableUnits());
        Assert.assertThat(routingResult, CoreMatchers.instanceOf(RoutingResult.class));
        Assert.assertThat(routingResult.getTableUnits().getTableUnits().size(), CoreMatchers.is(1));
        Assert.assertThat(tableUnitList.get(0).getDataSourceName(), CoreMatchers.is("ds_0"));
        Assert.assertThat(tableUnitList.get(0).getRoutingTables().size(), CoreMatchers.is(2));
        Assert.assertThat(tableUnitList.get(0).getRoutingTables().get(0).getActualTableName(), CoreMatchers.is("t_order"));
        Assert.assertThat(tableUnitList.get(0).getRoutingTables().get(0).getLogicTableName(), CoreMatchers.is("t_order"));
        Assert.assertThat(tableUnitList.get(0).getRoutingTables().get(1).getActualTableName(), CoreMatchers.is("t_order_item"));
        Assert.assertThat(tableUnitList.get(0).getRoutingTables().get(1).getLogicTableName(), CoreMatchers.is("t_order_item"));
    }
}

