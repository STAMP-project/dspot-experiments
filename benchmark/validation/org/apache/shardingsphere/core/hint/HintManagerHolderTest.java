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
package org.apache.shardingsphere.core.hint;


import org.apache.shardingsphere.api.hint.HintManager;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class HintManagerHolderTest {
    private final HintManager hintManager = HintManager.getInstance();

    @Test(expected = IllegalStateException.class)
    public void assertSetHintManagerTwice() {
        HintManagerHolder.setHintManager(HintManager.getInstance());
    }

    @Test
    public void assertGetDatabaseShardingValuesWithoutLogicTable() {
        hintManager.setDatabaseShardingValue(1);
        Assert.assertThat(HintManagerHolder.getDatabaseShardingValues().size(), CoreMatchers.is(1));
        Assert.assertTrue(HintManagerHolder.getDatabaseShardingValues().contains(1));
    }

    @Test
    public void assertGetDatabaseShardingValuesWithLogicTable() {
        hintManager.addDatabaseShardingValue("logic_table", 1);
        Assert.assertThat(HintManagerHolder.getDatabaseShardingValues("logic_table").size(), CoreMatchers.is(1));
        Assert.assertTrue(HintManagerHolder.getDatabaseShardingValues("logic_table").contains(1));
    }

    @Test
    public void assertGetTableShardingValues() {
        hintManager.addTableShardingValue("logic_table", 1);
        Assert.assertThat(HintManagerHolder.getTableShardingValues("logic_table").size(), CoreMatchers.is(1));
        Assert.assertTrue(HintManagerHolder.getTableShardingValues("logic_table").contains(1));
    }

    @Test
    public void assertIsDatabaseShardingOnly() {
        hintManager.setDatabaseShardingValue(1);
        Assert.assertTrue(HintManagerHolder.isDatabaseShardingOnly());
    }

    @Test
    public void assertIsDatabaseShardingOnlyWithoutSet() {
        hintManager.close();
        Assert.assertFalse(HintManagerHolder.isDatabaseShardingOnly());
    }

    @Test
    public void assertIsMasterRouteOnly() {
        hintManager.setMasterRouteOnly();
        Assert.assertTrue(HintManagerHolder.isMasterRouteOnly());
    }

    @Test
    public void assertIsMasterRouteOnlyWithoutSet() {
        hintManager.close();
        Assert.assertFalse(HintManagerHolder.isMasterRouteOnly());
    }

    @Test
    public void assertClear() {
        hintManager.addDatabaseShardingValue("logic_table", 1);
        hintManager.addTableShardingValue("logic_table", 1);
        hintManager.close();
        Assert.assertTrue(HintManagerHolder.getDatabaseShardingValues("logic_table").isEmpty());
        Assert.assertTrue(HintManagerHolder.getTableShardingValues("logic_table").isEmpty());
    }
}

