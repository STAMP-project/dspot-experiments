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
import org.elasticsearch.cluster.service.ClusterService;
import org.hamcrest.core.Is;
import org.junit.Test;


public class ResizeShardsITest extends SQLTransportIntegrationTest {
    @Test
    public void testShrinkShardsOfTable() {
        execute(("create table quotes (id integer, quote string, date timestamp) " + "clustered into 3 shards"));
        ensureYellow();
        execute("insert into quotes (id, quote, date) values (?, ?, ?), (?, ?, ?)", new Object[]{ 1, "Don't panic", 1395874800000L, 2, "Now panic", 1395961200000L });
        execute("refresh table quotes");
        ClusterService clusterService = internalCluster().getInstance(ClusterService.class);
        final String resizeNodeName = getADataNodeName(clusterService.state());
        execute("alter table quotes set (\"routing.allocation.require._name\"=?, \"blocks.write\"=?)", RandomizedTest.$(resizeNodeName, true));
        ensureYellowAndNoInitializingShards();
        execute("alter table quotes set (number_of_shards=?)", RandomizedTest.$(1));
        ensureYellow();
        execute("select number_of_shards from information_schema.tables where table_name = 'quotes'");
        assertThat(TestingHelpers.printedTable(response.rows()), Is.is("1\n"));
        execute("select id from quotes");
        assertThat(response.rowCount(), Is.is(2L));
    }

    @Test
    public void testShrinkShardsEnsureLeftOverIndicesAreRemoved() {
        execute(("create table quotes (id integer, quote string, date timestamp) " + "clustered into 3 shards"));
        ensureYellow();
        final String resizeIndex = ".resized." + (getFqn("quotes"));
        createIndex(resizeIndex);
        ClusterService clusterService = internalCluster().getInstance(ClusterService.class);
        assertThat(clusterService.state().metaData().hasIndex(resizeIndex), Is.is(true));
        final String resizeNodeName = getADataNodeName(clusterService.state());
        execute("alter table quotes set (\"routing.allocation.require._name\"=?, \"blocks.write\"=?)", RandomizedTest.$(resizeNodeName, true));
        ensureYellowAndNoInitializingShards();
        execute("alter table quotes set (number_of_shards=?)", RandomizedTest.$(1));
        execute("select number_of_shards from information_schema.tables where table_name = 'quotes'");
        assertThat(TestingHelpers.printedTable(response.rows()), Is.is("1\n"));
        assertThat(clusterService.state().metaData().hasIndex(resizeIndex), Is.is(false));
    }

    @Test
    public void testShrinkShardsOfPartition() {
        execute(("create table quotes (id integer, quote string, date timestamp) " + "partitioned by(date) clustered into 3 shards"));
        ensureYellow();
        execute("insert into quotes (id, quote, date) values (?, ?, ?), (?, ?, ?)", new Object[]{ 1, "Don't panic", 1395874800000L, 2, "Now panic", 1395961200000L });
        execute("refresh table quotes");
        ClusterService clusterService = internalCluster().getInstance(ClusterService.class);
        final String resizeNodeName = getADataNodeName(clusterService.state());
        execute(("alter table quotes partition (date=1395874800000) " + "set (\"routing.allocation.require._name\"=?, \"blocks.write\"=?)"), RandomizedTest.$(resizeNodeName, true));
        ensureYellowAndNoInitializingShards();
        execute("alter table quotes partition (date=1395874800000) set (number_of_shards=?)", RandomizedTest.$(1));
        ensureYellow();
        execute(("select number_of_shards from information_schema.table_partitions " + ("where table_name = 'quotes' " + "and values = \'{\"date\": 1395874800000}\'")));
        assertThat(TestingHelpers.printedTable(response.rows()), Is.is("1\n"));
        execute("select id from quotes");
        assertThat(response.rowCount(), Is.is(2L));
    }

    @Test
    public void testNumberOfShardsOfATableCanBeIncreased() {
        execute("create table t1 (x int, p int) clustered into 1 shards with (number_of_routing_shards = 10)");
        execute("insert into t1 (x, p) values (1, 1), (2, 1)");
        execute("alter table t1 set (\"blocks.write\" = true)");
        execute("alter table t1 set (number_of_shards = 2)");
        ensureYellow();
        execute("select number_of_shards from information_schema.tables where table_name = 't1'");
        assertThat(TestingHelpers.printedTable(response.rows()), Is.is("2\n"));
        execute("select x from t1");
        assertThat(response.rowCount(), Is.is(2L));
    }

    @Test
    public void testNumberOfShardsOfAPartitionCanBeIncreased() {
        execute(("create table t1 (x int, p int) partitioned by (p) clustered into 1 shards " + "with (number_of_routing_shards = 10)"));
        execute("insert into t1 (x, p) values (1, 1), (2, 1)");
        execute("alter table t1 partition (p = 1) set (\"blocks.write\" = true)");
        execute("alter table t1 partition (p = 1) set (number_of_shards = 2)");
        ensureYellow();
        execute("select number_of_shards from information_schema.table_partitions where table_name = 't1'");
        assertThat(TestingHelpers.printedTable(response.rows()), Is.is("2\n"));
        execute("select x from t1");
        assertThat(response.rowCount(), Is.is(2L));
    }
}

