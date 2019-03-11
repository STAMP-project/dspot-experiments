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
package org.apache.shardingsphere.core.parsing.parser.context.table;


import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import java.util.Collection;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class TablesTest {
    @Test
    public void assertIsEmpty() {
        Tables tables = new Tables();
        Assert.assertTrue(tables.isEmpty());
    }

    @Test
    public void assertIsNotEmpty() {
        Tables tables = new Tables();
        tables.add(new Table("table", Optional.of("tbl")));
        Assert.assertFalse(tables.isEmpty());
    }

    @Test
    public void assertIsSameTable() {
        Tables tables = new Tables();
        tables.add(new Table("table", Optional.of("tbl_1")));
        tables.add(new Table("table", Optional.of("tbl_2")));
        Assert.assertTrue(tables.isSameTable());
    }

    @Test
    public void assertIsNotSameTable() {
        Tables tables = new Tables();
        tables.add(new Table("table_1", Optional.of("tbl_1")));
        tables.add(new Table("table_2", Optional.of("tbl_2")));
        Assert.assertFalse(tables.isSameTable());
    }

    @Test
    public void assertIsSingleTable() {
        Tables tables = new Tables();
        tables.add(new Table("table", Optional.of("tbl")));
        Assert.assertTrue(tables.isSingleTable());
    }

    @Test
    public void assertIsNotSingleTable() {
        Tables tables = new Tables();
        tables.add(new Table("table_1", Optional.of("tbl_1")));
        tables.add(new Table("table_2", Optional.of("tbl_2")));
        Assert.assertFalse(tables.isSingleTable());
    }

    @Test
    public void assertGetSingleTableName() {
        Tables tables = new Tables();
        tables.add(new Table("table", Optional.of("tbl")));
        Assert.assertThat(tables.getSingleTableName(), CoreMatchers.is("table"));
    }

    @Test
    public void assertGetTableNames() {
        Tables tables = new Tables();
        tables.add(new Table("table_1", Optional.of("tbl_1")));
        tables.add(new Table("table_2", Optional.of("tbl_2")));
        Assert.assertThat(tables.getTableNames(), CoreMatchers.<Collection<String>>is(Sets.newHashSet("table_1", "table_2")));
    }

    @Test
    public void assertFindTableWithName() {
        Tables tables = new Tables();
        tables.add(new Table("table_1", Optional.of("tbl_1")));
        tables.add(new Table("table_2", Optional.of("tbl_2")));
        Optional<Table> table = tables.find("table_1");
        Assert.assertTrue(table.isPresent());
        Assert.assertThat(table.get().getName(), CoreMatchers.is("table_1"));
        Assert.assertThat(table.get().getAlias().orNull(), CoreMatchers.is("tbl_1"));
    }

    @Test
    public void assertFindTableWithAlias() {
        Tables tables = new Tables();
        tables.add(new Table("table_1", Optional.of("tbl_1")));
        tables.add(new Table("table_2", Optional.of("tbl_2")));
        Optional<Table> table = tables.find("tbl_1");
        Assert.assertTrue(table.isPresent());
        Assert.assertThat(table.get().getName(), CoreMatchers.is("table_1"));
        Assert.assertThat(table.get().getAlias().orNull(), CoreMatchers.is("tbl_1"));
    }

    @Test
    public void assertNotFoundTable() {
        Tables tables = new Tables();
        tables.add(new Table("table_1", Optional.of("tbl_1")));
        tables.add(new Table("table_2", Optional.of("tbl_2")));
        Optional<Table> table = tables.find("table_3");
        Assert.assertFalse(table.isPresent());
    }
}

