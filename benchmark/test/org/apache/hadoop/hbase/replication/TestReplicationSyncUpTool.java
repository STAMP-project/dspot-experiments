/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hbase.replication;


import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.ReplicationTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ ReplicationTests.class, LargeTests.class })
public class TestReplicationSyncUpTool extends TestReplicationBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestReplicationSyncUpTool.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestReplicationSyncUpTool.class);

    private static final TableName t1_su = TableName.valueOf("t1_syncup");

    private static final TableName t2_su = TableName.valueOf("t2_syncup");

    protected static final byte[] famName = Bytes.toBytes("cf1");

    private static final byte[] qualName = Bytes.toBytes("q1");

    protected static final byte[] noRepfamName = Bytes.toBytes("norep");

    private HTableDescriptor t1_syncupSource;

    private HTableDescriptor t1_syncupTarget;

    private HTableDescriptor t2_syncupSource;

    private HTableDescriptor t2_syncupTarget;

    protected Table ht1Source;

    protected Table ht2Source;

    protected Table ht1TargetAtPeer1;

    protected Table ht2TargetAtPeer1;

    /**
     * Add a row to a table in each cluster, check it's replicated, delete it,
     * check's gone Also check the puts and deletes are not replicated back to
     * the originating cluster.
     */
    @Test
    public void testSyncUpTool() throws Exception {
        /**
         * Set up Replication: on Master and one Slave
         * Table: t1_syncup and t2_syncup
         * columnfamily:
         *    'cf1'  : replicated
         *    'norep': not replicated
         */
        setupReplication();
        /**
         * at Master:
         * t1_syncup: put 100 rows into cf1, and 1 rows into norep
         * t2_syncup: put 200 rows into cf1, and 1 rows into norep
         *
         * verify correctly replicated to slave
         */
        putAndReplicateRows();
        /**
         * Verify delete works
         *
         * step 1: stop hbase on Slave
         *
         * step 2: at Master:
         *  t1_syncup: delete 50 rows  from cf1
         *  t2_syncup: delete 100 rows from cf1
         *  no change on 'norep'
         *
         * step 3: stop hbase on master, restart hbase on Slave
         *
         * step 4: verify Slave still have the rows before delete
         *      t1_syncup: 100 rows from cf1
         *      t2_syncup: 200 rows from cf1
         *
         * step 5: run syncup tool on Master
         *
         * step 6: verify that delete show up on Slave
         *      t1_syncup: 50 rows from cf1
         *      t2_syncup: 100 rows from cf1
         *
         * verify correctly replicated to Slave
         */
        mimicSyncUpAfterDelete();
        /**
         * Verify put works
         *
         * step 1: stop hbase on Slave
         *
         * step 2: at Master:
         *  t1_syncup: put 100 rows  from cf1
         *  t2_syncup: put 200 rows  from cf1
         *  and put another row on 'norep'
         *  ATTN: put to 'cf1' will overwrite existing rows, so end count will
         *        be 100 and 200 respectively
         *      put to 'norep' will add a new row.
         *
         * step 3: stop hbase on master, restart hbase on Slave
         *
         * step 4: verify Slave still has the rows before put
         *      t1_syncup: 50 rows from cf1
         *      t2_syncup: 100 rows from cf1
         *
         * step 5: run syncup tool on Master
         *
         * step 6: verify that put show up on Slave
         *         and 'norep' does not
         *      t1_syncup: 100 rows from cf1
         *      t2_syncup: 200 rows from cf1
         *
         * verify correctly replicated to Slave
         */
        mimicSyncUpAfterPut();
    }
}

