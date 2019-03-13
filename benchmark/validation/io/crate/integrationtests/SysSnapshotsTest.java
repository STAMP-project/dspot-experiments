/**
 * Licensed to CRATE.IO GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import SnapshotState.SUCCESS;
import Version.CURRENT;
import io.crate.testing.UseJdbc;
import io.crate.types.DataType;
import io.crate.types.StringType;
import io.crate.types.TimestampType;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


// missing column types
@ClusterScope
@UseJdbc(0)
public class SysSnapshotsTest extends SQLTransportIntegrationTest {
    @ClassRule
    public static TemporaryFolder TEMP_FOLDER = new TemporaryFolder();

    private static String REPOSITORY_NAME = "test_snapshots_repo";

    private List<String> snapshots = new ArrayList<>();

    private long createdTime;

    private long finishedTime;

    @Test
    public void testQueryAllColumns() throws Exception {
        execute("select * from sys.snapshots");
        assertThat(response.rowCount(), Matchers.is(1L));
        assertThat(response.cols().length, Matchers.is(7));
        assertThat(response.cols(), Matchers.is(new String[]{ "concrete_indices", "finished", "name", "repository", "started", "state", "version" }));
        assertThat(response.columnTypes(), Matchers.is(new DataType[]{ new io.crate.types.ArrayType(StringType.INSTANCE), TimestampType.INSTANCE, StringType.INSTANCE, StringType.INSTANCE, TimestampType.INSTANCE, StringType.INSTANCE, StringType.INSTANCE }));
        assertThat(((Object[]) (response.rows()[0][0])), Matchers.arrayContaining(getFqn("test_table")));
        assertThat(((Long) (response.rows()[0][1])), Matchers.lessThanOrEqualTo(finishedTime));
        assertThat(((String) (response.rows()[0][2])), Matchers.is("test_snap_1"));
        assertThat(((String) (response.rows()[0][3])), Matchers.is(SysSnapshotsTest.REPOSITORY_NAME));
        assertThat(((Long) (response.rows()[0][4])), Matchers.greaterThanOrEqualTo(createdTime));
        assertThat(((String) (response.rows()[0][5])), Matchers.is(SUCCESS.name()));
        assertThat(((String) (response.rows()[0][6])), Matchers.is(CURRENT.toString()));
    }
}

