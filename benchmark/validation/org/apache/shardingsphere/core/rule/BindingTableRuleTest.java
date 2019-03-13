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
package org.apache.shardingsphere.core.rule;


import java.util.Arrays;
import java.util.Collection;
import org.apache.shardingsphere.core.exception.ShardingConfigurationException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class BindingTableRuleTest {
    @Test
    public void assertHasLogicTable() {
        Assert.assertTrue(createBindingTableRule().hasLogicTable("Logic_Table"));
    }

    @Test
    public void assertNotHasLogicTable() {
        Assert.assertFalse(createBindingTableRule().hasLogicTable("New_Table"));
    }

    @Test
    public void assertGetBindingActualTablesSuccess() {
        Assert.assertThat(createBindingTableRule().getBindingActualTable("ds1", "Sub_Logic_Table", "table_1"), CoreMatchers.is("sub_table_1"));
    }

    @Test(expected = ShardingConfigurationException.class)
    public void assertGetBindingActualTablesFailureWhenNotFound() {
        createBindingTableRule().getBindingActualTable("no_ds", "Sub_Logic_Table", "table_1");
    }

    @Test(expected = ShardingConfigurationException.class)
    public void assertGetBindingActualTablesFailureWhenLogicTableNotFound() {
        createBindingTableRule().getBindingActualTable("ds0", "No_Logic_Table", "table_1");
    }

    @Test
    public void assertGetAllLogicTables() {
        Assert.assertThat(createBindingTableRule().getAllLogicTables(), CoreMatchers.is(((Collection<String>) (Arrays.asList("logic_table", "sub_logic_table")))));
    }

    @Test
    public void assertGetTableRules() {
        Assert.assertThat(createBindingTableRule().getTableRules().size(), CoreMatchers.is(2));
        Assert.assertThat(createBindingTableRule().getTableRules().get(0).getLogicTable(), CoreMatchers.is(createTableRule().getLogicTable()));
        Assert.assertThat(createBindingTableRule().getTableRules().get(0).getActualDataNodes(), CoreMatchers.is(createTableRule().getActualDataNodes()));
        Assert.assertThat(createBindingTableRule().getTableRules().get(1).getLogicTable(), CoreMatchers.is(createSubTableRule().getLogicTable()));
        Assert.assertThat(createBindingTableRule().getTableRules().get(1).getActualDataNodes(), CoreMatchers.is(createSubTableRule().getActualDataNodes()));
    }
}

