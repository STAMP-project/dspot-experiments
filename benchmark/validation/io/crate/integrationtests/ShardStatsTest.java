/**
 * Licensed to CRATE Technology GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial agreement.
 */
package io.crate.integrationtests;


import ESIntegTestCase.ClusterScope;
import IndexMetaData.SETTING_NUMBER_OF_REPLICAS;
import IndexMetaData.SETTING_NUMBER_OF_SHARDS;
import io.crate.blob.v2.BlobAdminClient;
import org.elasticsearch.common.settings.Settings;
import org.hamcrest.Matchers;
import org.junit.Test;


@ClusterScope(numDataNodes = 2)
public class ShardStatsTest extends SQLTransportIntegrationTest {
    @Test
    public void testSelectZeroLimit() throws Exception {
        execute("create table test (col1 int) clustered into 3 shards with (number_of_replicas=0)");
        ensureGreen();
        execute("select * from sys.shards limit 0");
        assertEquals(0L, response.rowCount());
    }

    @Test
    public void testShardSelect() throws Exception {
        execute("create table test (col1 int) clustered into 3 shards with (number_of_replicas=0)");
        ensureGreen();
        execute("select count(*) from sys.shards where table_name='test'");
        assertEquals(1, response.rowCount());
        assertEquals(3L, response.rows()[0][0]);
    }

    @Test
    public void testSelectIncludingUnassignedShards() throws Exception {
        execute(("create table locations (id integer primary key, name string) " + ("clustered into 2 shards " + "with (number_of_replicas=2, \"write.wait_for_active_shards\"=1)")));
        assertBusy(() -> {
            execute("select state, \"primary\", recovery[\'stage\'] from sys.shards where table_name = \'locations\' order by state, \"primary\"");
            assertThat(printedTable(response.rows()), is(("STARTED| false| DONE\n" + (((("STARTED| false| DONE\n" + "STARTED| true| DONE\n") + "STARTED| true| DONE\n") + "UNASSIGNED| false| NULL\n") + "UNASSIGNED| false| NULL\n"))));
        });
    }

    @Test
    public void testSelectGroupByIncludingUnassignedShards() throws Exception {
        execute(("create table locations (id integer primary key, name string) " + ("clustered into 5 shards " + "with(number_of_replicas=2 , \"write.wait_for_active_shards\"=1)")));
        ensureYellow();
        execute(("select count(*), state, \"primary\" from sys.shards " + "group by state, \"primary\" order by state desc"));
        assertThat(response.rowCount(), Matchers.greaterThanOrEqualTo(2L));
        assertEquals(3, response.cols().length);
        assertThat(((Long) (response.rows()[0][0])), Matchers.greaterThanOrEqualTo(5L));
        assertEquals("UNASSIGNED", response.rows()[0][1]);
        assertEquals(false, response.rows()[0][2]);
    }

    @Test
    public void testSelectCountIncludingUnassignedShards() throws Exception {
        execute(("create table locations (id integer primary key, name string) " + ("clustered into 5 shards " + "with(number_of_replicas=2, \"write.wait_for_active_shards\"=1)")));
        ensureYellow();
        execute("select count(*) from sys.shards where table_name='locations'");
        assertThat(response.rowCount(), Matchers.is(1L));
        assertThat(((Long) (response.rows()[0][0])), Matchers.is(15L));
    }

    @Test
    public void testTableNameBlobTable() throws Exception {
        BlobAdminClient blobAdminClient = internalCluster().getInstance(BlobAdminClient.class);
        Settings indexSettings = Settings.builder().put(SETTING_NUMBER_OF_REPLICAS, 0).put(SETTING_NUMBER_OF_SHARDS, 1).build();
        blobAdminClient.createBlobTable("blobs", indexSettings).get();
        ensureGreen();
        execute("select schema_name, table_name from sys.shards where table_name = 'blobs'");
        assertThat(response.rowCount(), Matchers.is(2L));
        for (int i = 0; i < (response.rowCount()); i++) {
            assertThat(((String) (response.rows()[i][0])), Matchers.is("blob"));
            assertThat(((String) (response.rows()[i][1])), Matchers.is("blobs"));
        }
        execute(("create blob table sbolb clustered into 4 shards " + "with (number_of_replicas=0)"));
        ensureYellow();
        execute("select schema_name, table_name from sys.shards where table_name = 'sbolb'");
        assertThat(response.rowCount(), Matchers.is(4L));
        for (int i = 0; i < (response.rowCount()); i++) {
            assertThat(((String) (response.rows()[i][0])), Matchers.is("blob"));
            assertThat(((String) (response.rows()[i][1])), Matchers.is("sbolb"));
        }
        execute(("select count(*) from sys.shards " + ("where schema_name='blob' and table_name != 'blobs' " + "and table_name != 'sbolb'")));
        assertThat(response.rowCount(), Matchers.is(1L));
        assertThat(((Long) (response.rows()[0][0])), Matchers.is(0L));
    }
}

