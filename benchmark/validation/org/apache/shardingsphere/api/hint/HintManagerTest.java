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
package org.apache.shardingsphere.api.hint;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class HintManagerTest {
    @Test(expected = IllegalStateException.class)
    public void assertGetInstanceTwice() {
        try {
            HintManager.getInstance();
            HintManager.getInstance();
        } finally {
            HintManager.clear();
        }
    }

    @Test
    public void assertSetDatabaseShardingValue() {
        try (HintManager hintManager = HintManager.getInstance()) {
            hintManager.setDatabaseShardingValue(1);
            hintManager.setDatabaseShardingValue(3);
            Assert.assertTrue(HintManager.isDatabaseShardingOnly());
            Assert.assertThat(HintManager.getDatabaseShardingValues("").size(), CoreMatchers.is(1));
            Assert.assertTrue(HintManager.getDatabaseShardingValues("").contains(3));
        }
    }

    @Test
    public void assertAddDatabaseShardingValue() {
        try (HintManager hintManager = HintManager.getInstance()) {
            hintManager.addDatabaseShardingValue("logicTable", 1);
            hintManager.addDatabaseShardingValue("logicTable", 3);
            Assert.assertThat(HintManager.getDatabaseShardingValues("logicTable").size(), CoreMatchers.is(2));
            Assert.assertTrue(HintManager.getDatabaseShardingValues("logicTable").contains(1));
            Assert.assertTrue(HintManager.getDatabaseShardingValues("logicTable").contains(3));
        }
    }

    @Test
    public void assertAddTableShardingValue() {
        try (HintManager hintManager = HintManager.getInstance()) {
            hintManager.addTableShardingValue("logicTable", 1);
            hintManager.addTableShardingValue("logicTable", 3);
            Assert.assertThat(HintManager.getTableShardingValues("logicTable").size(), CoreMatchers.is(2));
            Assert.assertTrue(HintManager.getTableShardingValues("logicTable").contains(1));
            Assert.assertTrue(HintManager.getTableShardingValues("logicTable").contains(3));
        }
    }

    @Test
    public void assertGetDatabaseShardingValuesWithoutLogicTable() {
        try (HintManager hintManager = HintManager.getInstance()) {
            hintManager.setDatabaseShardingValue(1);
            Assert.assertThat(HintManager.getDatabaseShardingValues().size(), CoreMatchers.is(1));
            Assert.assertTrue(HintManager.getDatabaseShardingValues().contains(1));
        }
    }

    @Test
    public void assertGetDatabaseShardingValuesWithLogicTable() {
        try (HintManager hintManager = HintManager.getInstance()) {
            hintManager.addDatabaseShardingValue("logic_table", 1);
            Assert.assertThat(HintManager.getDatabaseShardingValues("logic_table").size(), CoreMatchers.is(1));
            Assert.assertTrue(HintManager.getDatabaseShardingValues("logic_table").contains(1));
        }
    }

    @Test
    public void assertGetTableShardingValues() {
        try (HintManager hintManager = HintManager.getInstance()) {
            hintManager.addTableShardingValue("logic_table", 1);
            Assert.assertThat(HintManager.getTableShardingValues("logic_table").size(), CoreMatchers.is(1));
            Assert.assertTrue(HintManager.getTableShardingValues("logic_table").contains(1));
        }
    }

    @Test
    public void assertIsDatabaseShardingOnly() {
        try (HintManager hintManager = HintManager.getInstance()) {
            hintManager.setDatabaseShardingValue(1);
            Assert.assertTrue(HintManager.isDatabaseShardingOnly());
        }
    }

    @Test
    public void assertIsDatabaseShardingOnlyWithoutSet() {
        HintManager hintManager = HintManager.getInstance();
        hintManager.close();
        Assert.assertFalse(HintManager.isDatabaseShardingOnly());
    }

    @Test
    public void assertSetMasterRouteOnly() {
        try (HintManager hintManager = HintManager.getInstance()) {
            hintManager.setMasterRouteOnly();
            Assert.assertTrue(HintManager.isMasterRouteOnly());
        }
    }

    @Test
    public void assertIsMasterRouteOnly() {
        try (HintManager hintManager = HintManager.getInstance()) {
            hintManager.setMasterRouteOnly();
            Assert.assertTrue(HintManager.isMasterRouteOnly());
        }
    }

    @Test
    public void assertIsMasterRouteOnlyWithoutSet() {
        HintManager hintManager = HintManager.getInstance();
        hintManager.close();
        Assert.assertFalse(HintManager.isMasterRouteOnly());
    }

    @Test
    public void assertClose() {
        HintManager hintManager = HintManager.getInstance();
        hintManager.addDatabaseShardingValue("logic_table", 1);
        hintManager.addTableShardingValue("logic_table", 1);
        hintManager.close();
        Assert.assertTrue(HintManager.getDatabaseShardingValues("logic_table").isEmpty());
        Assert.assertTrue(HintManager.getTableShardingValues("logic_table").isEmpty());
    }
}

