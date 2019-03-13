/**
 * Licensed to CRATE Technology GmbH ("Crate") under one or more contributor
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
package io.crate.analyze;


import CreateSnapshotAnalyzedStatement.ALL_INDICES;
import SnapshotSettings.IGNORE_UNAVAILABLE;
import com.google.common.collect.ImmutableList;
import io.crate.exceptions.OperationOnInaccessibleRelationException;
import io.crate.exceptions.PartitionAlreadyExistsException;
import io.crate.exceptions.PartitionUnknownException;
import io.crate.exceptions.RelationAlreadyExists;
import io.crate.exceptions.RelationUnknown;
import io.crate.exceptions.RepositoryUnknownException;
import io.crate.exceptions.SchemaUnknownException;
import io.crate.metadata.PartitionName;
import io.crate.metadata.RelationName;
import io.crate.metadata.Schemas;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import io.crate.testing.SQLExecutor;
import io.crate.testing.SettingMatcher;
import org.hamcrest.Matchers;
import org.junit.Test;


public class SnapshotRestoreAnalyzerTest extends CrateDummyClusterServiceUnitTest {
    private SQLExecutor executor;

    @Test
    public void testCreateSnapshotAll() throws Exception {
        CreateSnapshotAnalyzedStatement statement = analyze("CREATE SNAPSHOT my_repo.my_snapshot ALL WITH (wait_for_completion=true)");
        assertThat(statement.indices(), Matchers.is(ALL_INDICES));
        assertThat(statement.snapshot().getRepository(), Matchers.is("my_repo"));
        assertThat(statement.snapshot().getSnapshotId().getName(), Matchers.is("my_snapshot"));
        assertThat(statement.snapshotSettings(), Matchers.allOf(SettingMatcher.hasEntry("wait_for_completion", "true"), SettingMatcher.hasEntry("ignore_unavailable", "false")));
    }

    @Test
    public void testCreateSnapshotUnknownRepo() throws Exception {
        expectedException.expect(RepositoryUnknownException.class);
        expectedException.expectMessage("Repository 'unknown_repo' unknown");
        analyze("CREATE SNAPSHOT unknown_repo.my_snapshot ALL");
    }

    @Test
    public void testCreateSnapshotUnsupportedParameter() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("setting 'foo' not supported");
        analyze("CREATE SNAPSHOT my_repo.my_snapshot ALL with (foo=true)");
    }

    @Test
    public void testCreateSnapshotUnknownTables() throws Exception {
        expectedException.expect(RelationUnknown.class);
        expectedException.expectMessage("Relation 't2' unknown");
        analyze("CREATE SNAPSHOT my_repo.my_snapshot TABLE users, t2, custom.users");
    }

    @Test
    public void testCreateSnapshotUnknownSchema() throws Exception {
        expectedException.expect(SchemaUnknownException.class);
        expectedException.expectMessage("Schema 'myschema' unknown");
        analyze("CREATE SNAPSHOT my_repo.my_snapshot TABLE users, myschema.users");
    }

    @Test
    public void testCreateSnapshotUnknownPartition() throws Exception {
        expectedException.expect(PartitionUnknownException.class);
        expectedException.expectMessage("No partition for table 'doc.parted' with ident '04130' exists");
        analyze("CREATE SNAPSHOT my_repo.my_snapshot TABLE parted PARTITION (date='1970-01-01')");
    }

    @Test
    public void testCreateSnapshotUnknownTableIgnore() throws Exception {
        CreateSnapshotAnalyzedStatement statement = analyze("CREATE SNAPSHOT my_repo.my_snapshot TABLE users, t2 WITH (ignore_unavailable=true)");
        assertThat(statement.indices(), Matchers.contains("users"));
        assertThat(IGNORE_UNAVAILABLE.get(statement.snapshotSettings()), Matchers.is(true));
    }

    @Test
    public void testCreateSnapshotUnknownSchemaIgnore() throws Exception {
        CreateSnapshotAnalyzedStatement statement = analyze("CREATE SNAPSHOT my_repo.my_snapshot TABLE users, my_schema.t2 WITH (ignore_unavailable=true)");
        assertThat(statement.indices(), Matchers.contains("users"));
        assertThat(IGNORE_UNAVAILABLE.get(statement.snapshotSettings()), Matchers.is(true));
    }

    @Test
    public void testCreateSnapshotCreateSnapshotTables() throws Exception {
        CreateSnapshotAnalyzedStatement statement = analyze("CREATE SNAPSHOT my_repo.my_snapshot TABLE users, locations WITH (wait_for_completion=true)");
        assertThat(statement.indices(), Matchers.containsInAnyOrder("users", "locations"));
        assertThat(statement.snapshot().getRepository(), Matchers.is("my_repo"));
        assertThat(statement.snapshot().getSnapshotId().getName(), Matchers.is("my_snapshot"));
        assertThat(statement.snapshotSettings().size(), Matchers.is(2));
        assertThat(statement.snapshotSettings(), Matchers.allOf(SettingMatcher.hasEntry("wait_for_completion", "true"), SettingMatcher.hasEntry("ignore_unavailable", "false")));
    }

    @Test
    public void testCreateSnapshotNoRepoName() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Snapshot must be specified by \"<repository_name>\".\"<snapshot_name>\"");
        analyze("CREATE SNAPSHOT my_snapshot TABLE users ");
    }

    @Test
    public void testCreateSnapshotInvalidRepoName() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Invalid repository name 'my.repo'");
        analyze("CREATE SNAPSHOT my.repo.my_snapshot ALL");
    }

    @Test
    public void testCreateSnapshotSnapshotSysTable() throws Exception {
        expectedException.expect(OperationOnInaccessibleRelationException.class);
        expectedException.expectMessage(("The relation \"sys.shards\" doesn\'t support or allow " + "CREATE SNAPSHOT operations, as it is read-only."));
        analyze("CREATE SNAPSHOT my_repo.my_snapshot TABLE sys.shards");
    }

    @Test
    public void testCreateSnapshotNoWildcards() throws Exception {
        expectedException.expect(RelationUnknown.class);
        expectedException.expectMessage("Relation 'foobar*' unknown");
        analyze("CREATE SNAPSHOT my_repo.my_snapshot TABLE \"foobar*\"");
    }

    @Test
    public void testCreateSnapshotFromBlobTable() throws Exception {
        expectedException.expect(OperationOnInaccessibleRelationException.class);
        expectedException.expectMessage("The relation \"blob.my_blobs\" doesn\'t support or allow CREATE SNAPSHOT operations.");
        analyze("CREATE SNAPSHOT my_repo.my_snapshot TABLE blob.my_blobs");
    }

    @Test
    public void testCreateSnapshotListTablesTwice() throws Exception {
        CreateSnapshotAnalyzedStatement statement = analyze("CREATE SNAPSHOT my_repo.my_snapshot TABLE users, locations, users");
        assertThat(statement.indices(), Matchers.hasSize(2));
        assertThat(statement.indices(), Matchers.containsInAnyOrder("users", "locations"));
    }

    @Test
    public void testCreateSnapshotListPartitionsAndPartitionedTable() throws Exception {
        CreateSnapshotAnalyzedStatement statement = analyze("CREATE SNAPSHOT my_repo.my_snapshot TABLE parted, parted PARTITION (date=1395961200000)");
        assertThat(statement.indices(), Matchers.hasSize(3));
        assertThat(statement.indices(), Matchers.containsInAnyOrder(".partitioned.parted.04732cpp6ks3ed1o60o30c1g", ".partitioned.parted.0400", ".partitioned.parted.04732cpp6ksjcc9i60o30c1g"));
    }

    @Test
    public void testDropSnapshot() throws Exception {
        DropSnapshotAnalyzedStatement statement = analyze("drop snapshot my_repo.my_snap_1");
        assertThat(statement.repository(), Matchers.is("my_repo"));
        assertThat(statement.snapshot(), Matchers.is("my_snap_1"));
    }

    @Test
    public void testDropSnapshotUnknownRepo() throws Exception {
        expectedException.expect(RepositoryUnknownException.class);
        expectedException.expectMessage("Repository 'unknown_repo' unknown");
        analyze("drop snapshot unknown_repo.my_snap_1");
    }

    @Test
    public void testRestoreSnapshotAll() throws Exception {
        RestoreSnapshotAnalyzedStatement statement = analyze("RESTORE SNAPSHOT my_repo.my_snapshot ALL");
        assertThat(statement.snapshotName(), Matchers.is("my_snapshot"));
        assertThat(statement.repositoryName(), Matchers.is("my_repo"));
        assertThat(statement.restoreAll(), Matchers.is(true));
        assertThat(statement.restoreAll(), Matchers.is(true));
        // default settings
        assertThat(statement.settings(), Matchers.allOf(SettingMatcher.hasEntry("wait_for_completion", "false"), SettingMatcher.hasEntry("ignore_unavailable", "false")));
    }

    @Test
    public void testRestoreSnapshotSingleTable() throws Exception {
        RestoreSnapshotAnalyzedStatement statement = analyze("RESTORE SNAPSHOT my_repo.my_snapshot TABLE custom.restoreme");
        assertThat(statement.restoreTables().get(0).tableIdent(), Matchers.is(new RelationName("custom", "restoreme")));
        assertThat(statement.restoreTables().get(0).partitionName(), Matchers.is(Matchers.nullValue()));
        assertThat(statement.settings(), Matchers.allOf(SettingMatcher.hasEntry("wait_for_completion", "false"), SettingMatcher.hasEntry("ignore_unavailable", "false")));
    }

    @Test
    public void testRestoreExistingTable() throws Exception {
        expectedException.expect(RelationAlreadyExists.class);
        expectedException.expectMessage("Relation 'doc.users' already exists.");
        analyze("RESTORE SNAPSHOT my_repo.my_snapshot TABLE users");
    }

    @Test
    public void testRestoreUnsupportedParameter() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("setting 'foo' not supported");
        analyze("RESTORE SNAPSHOT my_repo.my_snapshot TABLE users WITH (foo=true)");
    }

    @Test
    public void testRestoreSinglePartition() throws Exception {
        RestoreSnapshotAnalyzedStatement statement = analyze("RESTORE SNAPSHOT my_repo.my_snapshot TABLE parted PARTITION (date=123)");
        PartitionName partition = new PartitionName(new RelationName("doc", "parted"), ImmutableList.of("123"));
        assertThat(statement.restoreTables().size(), Matchers.is(1));
        assertThat(statement.restoreTables().get(0).partitionName(), Matchers.is(partition));
        assertThat(statement.restoreTables().get(0).tableIdent(), Matchers.is(new RelationName(Schemas.DOC_SCHEMA_NAME, "parted")));
    }

    @Test
    public void testRestoreSinglePartitionToUnknownTable() throws Exception {
        RestoreSnapshotAnalyzedStatement statement = analyze("RESTORE SNAPSHOT my_repo.my_snapshot TABLE unknown_parted PARTITION (date=123)");
        PartitionName partitionName = new PartitionName(new RelationName("doc", "unknown_parted"), ImmutableList.of("123"));
        assertThat(statement.restoreTables().size(), Matchers.is(1));
        assertThat(statement.restoreTables().get(0).partitionName(), Matchers.is(partitionName));
        assertThat(statement.restoreTables().get(0).tableIdent(), Matchers.is(new RelationName(Schemas.DOC_SCHEMA_NAME, "unknown_parted")));
    }

    @Test
    public void testRestoreSingleExistingPartition() throws Exception {
        expectedException.expect(PartitionAlreadyExistsException.class);
        expectedException.expectMessage("Partition '.partitioned.parted.04732cpp6ksjcc9i60o30c1g' already exists");
        analyze("RESTORE SNAPSHOT my_repo.my_snapshot TABLE parted PARTITION (date=1395961200000)");
    }

    @Test
    public void testRestoreUnknownRepo() throws Exception {
        expectedException.expect(RepositoryUnknownException.class);
        expectedException.expectMessage("Repository 'unknown_repo' unknown");
        analyze("RESTORE SNAPSHOT unknown_repo.my_snapshot ALL");
    }
}

