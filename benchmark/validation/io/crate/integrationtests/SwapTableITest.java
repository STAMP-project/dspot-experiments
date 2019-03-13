/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.integrationtests;


import com.carrotsearch.randomizedtesting.RandomizedTest;
import io.crate.testing.TestingHelpers;
import org.hamcrest.Matchers;
import org.junit.Test;


public class SwapTableITest extends SQLTransportIntegrationTest {
    @Test
    public void testSwapTwoTablesWithDropSource() {
        execute("create table t1 (x int)");
        execute("create table t2 (x double)");
        execute("insert into t1 (x) values (1)");
        execute("insert into t2 (x) values (2)");
        execute("refresh table t1, t2");
        execute("alter cluster swap table t1 to t2 with (drop_source = ?)", RandomizedTest.$(true));
        execute("select * from t2");
        assertThat(TestingHelpers.printedTable(response.rows()), Matchers.is("1\n"));
        assertThat(TestingHelpers.printedTable(execute("select table_name from information_schema.tables where table_name in ('t1', 't2') order by 1").rows()), Matchers.is("t2\n"));
        expectedException.expectMessage("Relation 't1' unknown");
        execute("select * from t1");
    }

    @Test
    public void testSwapPartitionedTableWithNonPartitioned() {
        execute("create table t1 (x int)");
        execute("create table t2 (p int) partitioned by (p) clustered into 1 shards with (number_of_replicas = 0)");
        execute("insert into t1 (x) values (1)");
        execute("insert into t2 (p) values (2)");
        execute("refresh table t1, t2");
        execute("alter cluster swap table t1 to t2");
        assertThat(TestingHelpers.printedTable(execute("select * from t1").rows()), Matchers.is("2\n"));
        assertThat(TestingHelpers.printedTable(execute("select * from t2").rows()), Matchers.is("1\n"));
        assertThat(TestingHelpers.printedTable(execute("select table_name from information_schema.tables where table_name in ('t1', 't2') order by 1").rows()), Matchers.is(("t1\n" + "t2\n")));
    }

    @Test
    public void testSwapTwoPartitionedTablesWhereOneIsEmpty() throws Exception {
        execute("create table t1 (p int) partitioned by (p) clustered into 1 shards with (number_of_replicas = 0)");
        execute("create table t2 (p int) partitioned by (p) clustered into 1 shards with (number_of_replicas = 0)");
        execute("insert into t1 (p) values (1), (2)");
        execute("alter cluster swap table t1 to t2");
        execute("select * from t1");
        assertThat(response.rowCount(), Matchers.is(0L));
        execute("select * from t2 order by p");
        assertThat(TestingHelpers.printedTable(response.rows()), Matchers.is(("1\n" + "2\n")));
        assertThat(TestingHelpers.printedTable(execute("select table_name from information_schema.tables where table_name in ('t1', 't2') order by 1").rows()), Matchers.is(("t1\n" + "t2\n")));
    }
}

