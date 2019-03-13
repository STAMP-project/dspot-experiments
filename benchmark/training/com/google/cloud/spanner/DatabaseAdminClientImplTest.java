/**
 * Copyright 2017 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.spanner;


import DatabaseInfo.State.READY;
import SpannerImpl.DatabaseAdminClientImpl;
import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.spanner.spi.v1.SpannerRpc;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.protobuf.Empty;
import com.google.spanner.admin.database.v1.CreateDatabaseMetadata;
import com.google.spanner.admin.database.v1.Database;
import com.google.spanner.admin.database.v1.UpdateDatabaseDdlMetadata;
import com.google.spanner.admin.database.v1.com.google.cloud.spanner.Database;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Unit tests for {@link com.google.cloud.spanner.SpannerImpl.DatabaseAdminClientImpl}.
 */
@RunWith(JUnit4.class)
public class DatabaseAdminClientImplTest {
    private static final String PROJECT_ID = "my-project";

    private static final String INSTANCE_ID = "my-instance";

    private static final String INSTANCE_NAME = "projects/my-project/instances/my-instance";

    private static final String DB_ID = "my-db";

    private static final String DB_NAME = "projects/my-project/instances/my-instance/databases/my-db";

    private static final String DB_NAME2 = "projects/my-project/instances/my-instance/databases/my-db2";

    @Mock
    SpannerRpc rpc;

    DatabaseAdminClientImpl client;

    @Test
    public void getDatabase() {
        Mockito.when(rpc.getDatabase(DatabaseAdminClientImplTest.DB_NAME)).thenReturn(getDatabaseProto());
        com.google.cloud.spanner.Database db = client.getDatabase(DatabaseAdminClientImplTest.INSTANCE_ID, DatabaseAdminClientImplTest.DB_ID);
        assertThat(db.getId().getName()).isEqualTo(DatabaseAdminClientImplTest.DB_NAME);
        assertThat(db.getState()).isEqualTo(READY);
    }

    @Test
    public void createDatabase() throws Exception {
        OperationFuture<Database, CreateDatabaseMetadata> rawOperationFuture = OperationFutureUtil.immediateOperationFuture("createDatabase", getDatabaseProto(), CreateDatabaseMetadata.getDefaultInstance());
        Mockito.when(rpc.createDatabase(DatabaseAdminClientImplTest.INSTANCE_NAME, (("CREATE DATABASE `" + (DatabaseAdminClientImplTest.DB_ID)) + "`"), Collections.<String>emptyList())).thenReturn(rawOperationFuture);
        OperationFuture<com.google.cloud.spanner.Database, CreateDatabaseMetadata> op = client.createDatabase(DatabaseAdminClientImplTest.INSTANCE_ID, DatabaseAdminClientImplTest.DB_ID, Collections.<String>emptyList());
        assertThat(op.isDone()).isTrue();
        assertThat(op.get().getId().getName()).isEqualTo(DatabaseAdminClientImplTest.DB_NAME);
    }

    @Test
    public void updateDatabaseDdl() throws Exception {
        String opName = (DatabaseAdminClientImplTest.DB_NAME) + "/operations/myop";
        String opId = "myop";
        List<String> ddl = ImmutableList.of();
        OperationFuture<Empty, UpdateDatabaseDdlMetadata> rawOperationFuture = OperationFutureUtil.immediateOperationFuture(opName, Empty.getDefaultInstance(), UpdateDatabaseDdlMetadata.getDefaultInstance());
        Mockito.when(rpc.updateDatabaseDdl(DatabaseAdminClientImplTest.DB_NAME, ddl, opId)).thenReturn(rawOperationFuture);
        OperationFuture<Void, UpdateDatabaseDdlMetadata> op = client.updateDatabaseDdl(DatabaseAdminClientImplTest.INSTANCE_ID, DatabaseAdminClientImplTest.DB_ID, ddl, opId);
        assertThat(op.isDone()).isTrue();
        assertThat(op.getName()).isEqualTo(opName);
    }

    @Test
    public void updateDatabaseDdlOpAlreadyExists() throws Exception {
        String originalOpName = (DatabaseAdminClientImplTest.DB_NAME) + "/operations/originalop";
        List<String> ddl = ImmutableList.of();
        OperationFuture<Empty, UpdateDatabaseDdlMetadata> originalOp = OperationFutureUtil.immediateOperationFuture(originalOpName, Empty.getDefaultInstance(), UpdateDatabaseDdlMetadata.getDefaultInstance());
        String newOpName = (DatabaseAdminClientImplTest.DB_NAME) + "/operations/newop";
        String newOpId = "newop";
        OperationFuture<Empty, UpdateDatabaseDdlMetadata> newop = OperationFutureUtil.immediateOperationFuture(newOpName, Empty.getDefaultInstance(), UpdateDatabaseDdlMetadata.getDefaultInstance());
        Mockito.when(rpc.updateDatabaseDdl(DatabaseAdminClientImplTest.DB_NAME, ddl, newOpId)).thenReturn(originalOp);
        OperationFuture<Void, UpdateDatabaseDdlMetadata> op = client.updateDatabaseDdl(DatabaseAdminClientImplTest.INSTANCE_ID, DatabaseAdminClientImplTest.DB_ID, ddl, newOpId);
        assertThat(op.getName()).isEqualTo(originalOpName);
    }

    @Test
    public void dropDatabase() {
        client.dropDatabase(DatabaseAdminClientImplTest.INSTANCE_ID, DatabaseAdminClientImplTest.DB_ID);
        Mockito.verify(rpc).dropDatabase(DatabaseAdminClientImplTest.DB_NAME);
    }

    @Test
    public void getDatabaseDdl() {
        List<String> ddl = ImmutableList.of("CREATE TABLE mytable()");
        Mockito.when(rpc.getDatabaseDdl(DatabaseAdminClientImplTest.DB_NAME)).thenReturn(ddl);
        assertThat(client.getDatabaseDdl(DatabaseAdminClientImplTest.INSTANCE_ID, DatabaseAdminClientImplTest.DB_ID)).isEqualTo(ddl);
    }

    @Test
    public void listDatabases() {
        String pageToken = "token";
        Mockito.when(rpc.listDatabases(DatabaseAdminClientImplTest.INSTANCE_NAME, 1, null)).thenReturn(new com.google.cloud.spanner.spi.v1.SpannerRpc.Paginated(ImmutableList.<Database>of(getDatabaseProto()), pageToken));
        Mockito.when(rpc.listDatabases(DatabaseAdminClientImplTest.INSTANCE_NAME, 1, pageToken)).thenReturn(new com.google.cloud.spanner.spi.v1.SpannerRpc.Paginated(ImmutableList.<Database>of(getAnotherDatabaseProto()), ""));
        List<com.google.cloud.spanner.Database> dbs = Lists.newArrayList(client.listDatabases(DatabaseAdminClientImplTest.INSTANCE_ID, Options.pageSize(1)).iterateAll());
        assertThat(dbs.get(0).getId().getName()).isEqualTo(DatabaseAdminClientImplTest.DB_NAME);
        assertThat(dbs.get(1).getId().getName()).isEqualTo(DatabaseAdminClientImplTest.DB_NAME2);
        assertThat(dbs.size()).isEqualTo(2);
    }
}

