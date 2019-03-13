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


import ESIntegTestCase.ClusterScope;
import Priority.LANGUID;
import com.carrotsearch.randomizedtesting.RandomizedTest;
import io.crate.data.Bucket;
import io.crate.data.Row;
import io.crate.metadata.PartitionName;
import io.crate.metadata.RelationName;
import io.crate.testing.SQLResponse;
import io.crate.testing.SQLTransportExecutor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.elasticsearch.action.admin.cluster.reroute.ClusterRerouteRequestBuilder;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.cluster.node.DiscoveryNodes;
import org.elasticsearch.cluster.routing.ShardRouting;
import org.elasticsearch.cluster.routing.ShardRoutingState;
import org.elasticsearch.cluster.service.ClusterService;
import org.elasticsearch.common.unit.TimeValue;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;


@ClusterScope(numDataNodes = 2)
public class PartitionedTableConcurrentIntegrationTest extends SQLTransportIntegrationTest {
    private final TimeValue ACCEPTABLE_RELOCATION_TIME = new TimeValue(10, TimeUnit.SECONDS);

    /**
     * Test depends on 2 data nodes
     */
    @Test
    public void testSelectWhileShardsAreRelocating() throws Throwable {
        execute(("create table t (name string, p string) " + ("clustered into 2 shards " + "partitioned by (p) with (number_of_replicas = 0)")));
        ensureYellow();
        execute("insert into t (name, p) values (?, ?)", new Object[][]{ new Object[]{ "Marvin", "a" }, new Object[]{ "Trillian", "a" } });
        execute("refresh table t");
        final AtomicReference<Throwable> lastThrowable = new AtomicReference<>();
        final CountDownLatch selects = new CountDownLatch(100);
        Thread t = new Thread(() -> {
            while ((selects.getCount()) > 0) {
                try {
                    execute("select * from t");
                } catch (Throwable t1) {
                    // The failed job should have three started operations
                    SQLResponse res = execute("select id from sys.jobs_log where error is not null order by started desc limit 1");
                    if ((res.rowCount()) > 0) {
                        String id = ((String) (res.rows()[0][0]));
                        res = execute("select count(*) from sys.operations_log where name=? or name = ? and job_id = ?", new Object[]{ "collect", "fetchContext", id });
                        if (((long) (res.rows()[0][0])) < 3) {
                            // set the error if there where less than three attempts
                            lastThrowable.set(t1);
                        }
                    }
                } finally {
                    selects.countDown();
                }
            } 
        });
        t.start();
        PartitionName partitionName = new PartitionName(new RelationName(sqlExecutor.getCurrentSchema(), "t"), Collections.singletonList("a"));
        final String indexName = partitionName.asIndexName();
        ClusterService clusterService = internalCluster().getInstance(ClusterService.class);
        DiscoveryNodes nodes = clusterService.state().nodes();
        List<String> nodeIds = new ArrayList<>(2);
        for (DiscoveryNode node : nodes) {
            if (node.isDataNode()) {
                nodeIds.add(node.getId());
            }
        }
        final Map<String, String> nodeSwap = new HashMap<>(2);
        nodeSwap.put(nodeIds.get(0), nodeIds.get(1));
        nodeSwap.put(nodeIds.get(1), nodeIds.get(0));
        final CountDownLatch relocations = new CountDownLatch(20);
        Thread relocatingThread = new Thread(() -> {
            while ((relocations.getCount()) > 0) {
                ClusterStateResponse clusterStateResponse = admin().cluster().prepareState().setIndices(indexName).execute().actionGet();
                List<ShardRouting> shardRoutings = clusterStateResponse.getState().routingTable().allShards(indexName);
                ClusterRerouteRequestBuilder clusterRerouteRequestBuilder = admin().cluster().prepareReroute();
                int numMoves = 0;
                for (ShardRouting shardRouting : shardRoutings) {
                    if ((shardRouting.currentNodeId()) == null) {
                        continue;
                    }
                    if ((shardRouting.state()) != (ShardRoutingState.STARTED)) {
                        continue;
                    }
                    String toNode = nodeSwap.get(shardRouting.currentNodeId());
                    clusterRerouteRequestBuilder.add(new org.elasticsearch.cluster.routing.allocation.command.MoveAllocationCommand(shardRouting.getIndexName(), shardRouting.shardId().id(), shardRouting.currentNodeId(), toNode));
                    numMoves++;
                }
                if (numMoves > 0) {
                    clusterRerouteRequestBuilder.execute().actionGet();
                    admin().cluster().prepareHealth().setWaitForEvents(LANGUID).setWaitForNoRelocatingShards(false).setTimeout(ACCEPTABLE_RELOCATION_TIME).execute().actionGet();
                    relocations.countDown();
                }
            } 
        });
        relocatingThread.start();
        relocations.await(((SQLTransportExecutor.REQUEST_TIMEOUT.getSeconds()) + 1), TimeUnit.SECONDS);
        selects.await(((SQLTransportExecutor.REQUEST_TIMEOUT.getSeconds()) + 1), TimeUnit.SECONDS);
        Throwable throwable = lastThrowable.get();
        if (throwable != null) {
            throw throwable;
        }
        t.join();
        relocatingThread.join();
    }

    @Test
    public void testExecuteDeleteAllPartitions_PartitionsAreDeletedMeanwhile() throws Exception {
        Bucket bucket = deletePartitionsAndExecutePlan("delete from t");
        assertThat(bucket.size(), Is.is(1));
        Row row = bucket.iterator().next();
        assertThat(row.numColumns(), Is.is(1));
        assertThat(row.get(0), Is.is((-1L)));
    }

    @Test
    public void testExecuteDeleteSomePartitions_PartitionsAreDeletedMeanwhile() throws Exception {
        Bucket bucket = deletePartitionsAndExecutePlan("delete from t where name = 'Trillian'");
        assertThat(bucket.size(), Is.is(1));
        Row row = bucket.iterator().next();
        assertThat(row.numColumns(), Is.is(1));
        assertThat(row.get(0), Is.is(0L));
    }

    @Test
    public void testExecuteDeleteByQuery_PartitionsAreDeletedMeanwhile() throws Exception {
        Bucket bucket = deletePartitionsAndExecutePlan("delete from t where p = 'a'");
        assertThat(bucket.size(), Is.is(1));
        Row row = bucket.iterator().next();
        assertThat(row.numColumns(), Is.is(1));
        assertThat(row.get(0), Is.is((-1L)));
    }

    @Test
    public void testExecuteUpdate_PartitionsAreDeletedMeanwhile() throws Exception {
        Bucket bucket = deletePartitionsAndExecutePlan("update t set name = 'BoyceCodd'");
        assertThat(bucket.size(), Is.is(1));
        Row row = bucket.iterator().next();
        assertThat(row.numColumns(), Is.is(1));
        assertThat(row.get(0), Is.is(0L));
    }

    @Test
    public void testTableUnknownExceptionIsNotRaisedIfPartitionsAreDeletedAfterPlan() throws Exception {
        Bucket bucket = deletePartitionsAndExecutePlan("select * from t");
        assertThat(bucket.size(), Is.is(0));
    }

    @Test
    public void testTableUnknownExceptionIsNotRaisedIfPartitionsAreDeletedAfterPlanSingleNode() throws Exception {
        // with a sinlge node, this test leads to empty shard collectors
        internalCluster().ensureAtMostNumDataNodes(1);
        Bucket bucket = deletePartitionsAndExecutePlan("select * from t");
        assertThat(bucket.size(), Is.is(0));
    }

    @Test
    public void testTableUnknownExceptionNotRaisedIfPartitionsDeletedAfterCountPlan() throws Exception {
        Bucket bucket = deletePartitionsAndExecutePlan("select count(*) from t");
        assertThat(bucket.iterator().next().get(0), Is.is(0L));
    }

    @Test
    public void testRefreshDuringPartitionDeletion() throws Exception {
        execute("create table t (name string, p string) partitioned by (p)");
        execute("insert into t (name, p) values ('Arthur', 'a'), ('Trillian', 't')");
        execute("refresh table t");
        SQLTransportIntegrationTest.PlanForNode plan = plan("refresh table t");// create a plan in which the partitions exist

        execute("delete from t");
        // shouldn't throw an exception:
        execute(plan).getResult();// execute now that the partitions are gone

    }

    @Test
    public void testOptimizeDuringPartitionDeletion() throws Exception {
        execute("create table t (name string, p string) partitioned by (p)");
        execute("insert into t (name, p) values ('Arthur', 'a'), ('Trillian', 't')");
        execute("refresh table t");
        SQLTransportIntegrationTest.PlanForNode plan = plan("optimize table t");// create a plan in which the partitions exist

        execute("delete from t");
        // shouldn't throw an exception:
        execute(plan).getResult();// execute now that the partitions are gone

    }

    @Test
    public void testDeletePartitionWhileInsertingData() throws Exception {
        deletePartitionWhileInsertingData(false);
    }

    @Test
    public void testDeletePartitionWhileBulkInsertingData() throws Exception {
        deletePartitionWhileInsertingData(true);
    }

    @Test
    public void testInsertIntoDynamicObjectColumnAddsAllColumnsToTemplate() throws Exception {
        // regression test for issue that caused columns not being added to metadata/tableinfo of partitioned table
        // when inserting a lot of new dynamic columns to various partitions of a table
        execute(("create table dyn_parted (id int, bucket string, data object(dynamic), primary key (id, bucket)) " + ("partitioned by (bucket) " + "with (number_of_replicas = 0)")));
        ensureYellow();
        int bulkSize = 10;
        int numCols = 5;
        String[] buckets = new String[]{ "a", "b", "c", "d", "e", "f", "g", "h", "i", "j" };
        final CountDownLatch countDownLatch = new CountDownLatch(buckets.length);
        AtomicInteger numSuccessfulInserts = new AtomicInteger(0);
        for (String bucket : buckets) {
            Object[][] bulkArgs = new Object[bulkSize][];
            for (int i = 0; i < bulkSize; i++) {
                bulkArgs[i] = new Object[]{ i, bucket, PartitionedTableConcurrentIntegrationTest.createColumnMap(numCols, bucket) };
            }
            new Thread(() -> {
                try {
                    execute("insert into dyn_parted (id, bucket, data) values (?, ?, ?)", bulkArgs, TimeValue.timeValueSeconds(10));
                    numSuccessfulInserts.incrementAndGet();
                } finally {
                    countDownLatch.countDown();
                }
            }).start();
        }
        countDownLatch.await();
        // on a reasonable fast machine all inserts always work.
        assertThat("At least one insert must work without timeout", numSuccessfulInserts.get(), Matchers.greaterThanOrEqualTo(1));
        // table info is maybe not up-to-date immediately as doc table info's are cached
        // and invalidated/rebuild on cluster state changes
        assertBusy(() -> {
            execute("select count(*) from information_schema.columns where table_name = 'dyn_parted'");
            assertThat(response.rows()[0][0], is((3L + (numCols * (numSuccessfulInserts.get())))));
        }, 10L, TimeUnit.SECONDS);
    }

    @Test
    public void testConcurrentPartitionCreationWithColumnCreationTypeMissMatch() throws Exception {
        // dynamic column creation must result in a consistent type across partitions
        execute(("create table t1 (p int) " + (("clustered into 1 shards " + "partitioned by (p) ") + "with (number_of_replicas = 0)")));
        Thread insertNumbers = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    execute("insert into t1 (p, x) values (?, ?)", RandomizedTest.$(i, i));
                } catch (Throwable e) {
                    // may fail if other thread is faster
                }
            }
        });
        Thread insertStrings = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    execute("insert into t1 (p, x) values (?, ?)", RandomizedTest.$(i, ("foo" + i)));
                } catch (Throwable e) {
                    // may fail if other thread is faster
                }
            }
        });
        insertNumbers.start();
        insertStrings.start();
        insertNumbers.join();
        insertStrings.join();
        // this query should never fail
        execute("select * from t1 order by x limit 50");
    }
}

