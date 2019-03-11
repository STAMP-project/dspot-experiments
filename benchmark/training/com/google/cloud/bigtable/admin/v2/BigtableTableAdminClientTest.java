/**
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.bigtable.admin.v2;


import Status.Code.NOT_FOUND;
import View.SCHEMA_VIEW;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.gax.grpc.GrpcStatusCode;
import com.google.api.gax.rpc.NotFoundException;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.bigtable.admin.v2.ColumnFamily;
import com.google.bigtable.admin.v2.CreateTableRequest;
import com.google.bigtable.admin.v2.DeleteTableRequest;
import com.google.bigtable.admin.v2.DropRowRangeRequest;
import com.google.bigtable.admin.v2.GcRule;
import com.google.bigtable.admin.v2.GetTableRequest;
import com.google.bigtable.admin.v2.ListTablesRequest;
import com.google.bigtable.admin.v2.ModifyColumnFamiliesRequest;
import com.google.bigtable.admin.v2.ModifyColumnFamiliesRequest.Modification;
import com.google.bigtable.admin.v2.Table;
import com.google.bigtable.admin.v2.TableName;
import com.google.bigtable.admin.v2.com.google.bigtable.admin.v2.ListTablesRequest;
import com.google.cloud.bigtable.admin.v2.BaseBigtableTableAdminClient.ListTablesPage;
import com.google.cloud.bigtable.admin.v2.BaseBigtableTableAdminClient.ListTablesPagedResponse;
import com.google.cloud.bigtable.admin.v2.internal.NameUtil;
import com.google.cloud.bigtable.admin.v2.models.com.google.bigtable.admin.v2.CreateTableRequest;
import com.google.cloud.bigtable.admin.v2.models.com.google.bigtable.admin.v2.ModifyColumnFamiliesRequest;
import com.google.cloud.bigtable.admin.v2.models.com.google.bigtable.admin.v2.Table;
import com.google.cloud.bigtable.admin.v2.stub.EnhancedBigtableTableAdminStub;
import com.google.common.collect.Lists;
import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class BigtableTableAdminClientTest {
    private static final String PROJECT_ID = "my-project";

    private static final String INSTANCE_ID = "my-instance";

    private static final String TABLE_ID = "my-table";

    private static final String PROJECT_NAME = NameUtil.formatProjectName(BigtableTableAdminClientTest.PROJECT_ID);

    private static final String INSTANCE_NAME = NameUtil.formatInstanceName(BigtableTableAdminClientTest.PROJECT_ID, BigtableTableAdminClientTest.INSTANCE_ID);

    private static final String TABLE_NAME = NameUtil.formatTableName(BigtableTableAdminClientTest.PROJECT_ID, BigtableTableAdminClientTest.INSTANCE_ID, BigtableTableAdminClientTest.TABLE_ID);

    private BigtableTableAdminClient adminClient;

    @Mock
    private EnhancedBigtableTableAdminStub mockStub;

    @Mock
    private UnaryCallable<CreateTableRequest, Table> mockCreateTableCallable;

    @Mock
    private UnaryCallable<ModifyColumnFamiliesRequest, Table> mockModifyTableCallable;

    @Mock
    private UnaryCallable<DeleteTableRequest, Empty> mockDeleteTableCallable;

    @Mock
    private UnaryCallable<GetTableRequest, Table> mockGetTableCallable;

    @Mock
    private UnaryCallable<ListTablesRequest, ListTablesPagedResponse> mockListTableCallable;

    @Mock
    private UnaryCallable<DropRowRangeRequest, Empty> mockDropRowRangeCallable;

    @Mock
    private UnaryCallable<TableName, Void> mockAwaitReplicationCallable;

    @Test
    public void close() {
        adminClient.close();
        verify(mockStub).close();
    }

    @Test
    public void testCreateTable() {
        // Setup
        CreateTableRequest expectedRequest = com.google.bigtable.admin.v2.CreateTableRequest.newBuilder().setParent(BigtableTableAdminClientTest.INSTANCE_NAME).setTableId(BigtableTableAdminClientTest.TABLE_ID).setTable(com.google.bigtable.admin.v2.Table.getDefaultInstance()).build();
        Table expectedResponse = com.google.bigtable.admin.v2.Table.newBuilder().setName(BigtableTableAdminClientTest.TABLE_NAME.toString()).build();
        Mockito.when(mockCreateTableCallable.futureCall(expectedRequest)).thenReturn(ApiFutures.immediateFuture(expectedResponse));
        // Execute
        com.google.cloud.bigtable.admin.v2.models.Table result = adminClient.createTable(com.google.cloud.bigtable.admin.v2.models.CreateTableRequest.of(BigtableTableAdminClientTest.TABLE_ID));
        // Verify
        assertThat(result).isEqualTo(com.google.cloud.bigtable.admin.v2.models.Table.fromProto(expectedResponse));
    }

    @Test
    public void testModifyFamilies() {
        // Setup
        ModifyColumnFamiliesRequest expectedRequest = com.google.bigtable.admin.v2.ModifyColumnFamiliesRequest.newBuilder().setName(BigtableTableAdminClientTest.TABLE_NAME).addModifications(Modification.newBuilder().setId("cf").setCreate(ColumnFamily.newBuilder().setGcRule(GcRule.getDefaultInstance()))).build();
        Table fakeResponse = com.google.bigtable.admin.v2.Table.newBuilder().setName(BigtableTableAdminClientTest.TABLE_NAME).putColumnFamilies("cf", ColumnFamily.newBuilder().setGcRule(GcRule.getDefaultInstance()).build()).build();
        Mockito.when(mockModifyTableCallable.futureCall(expectedRequest)).thenReturn(ApiFutures.immediateFuture(fakeResponse));
        // Execute
        com.google.cloud.bigtable.admin.v2.models.Table actualResult = adminClient.modifyFamilies(com.google.cloud.bigtable.admin.v2.models.ModifyColumnFamiliesRequest.of(BigtableTableAdminClientTest.TABLE_ID).addFamily("cf"));
        // Verify
        assertThat(actualResult).isEqualTo(com.google.cloud.bigtable.admin.v2.models.Table.fromProto(fakeResponse));
    }

    @Test
    public void testDeleteTable() {
        // Setup
        DeleteTableRequest expectedRequest = DeleteTableRequest.newBuilder().setName(BigtableTableAdminClientTest.TABLE_NAME.toString()).build();
        final AtomicBoolean wasCalled = new AtomicBoolean(false);
        Mockito.when(mockDeleteTableCallable.futureCall(expectedRequest)).thenAnswer(new Answer<ApiFuture<Empty>>() {
            @Override
            public ApiFuture<Empty> answer(InvocationOnMock invocationOnMock) {
                wasCalled.set(true);
                return ApiFutures.immediateFuture(Empty.getDefaultInstance());
            }
        });
        // Execute
        adminClient.deleteTable(BigtableTableAdminClientTest.TABLE_ID);
        // Verify
        assertThat(wasCalled.get()).isTrue();
    }

    @Test
    public void testGetTable() {
        // Setup
        GetTableRequest expectedRequest = GetTableRequest.newBuilder().setName(BigtableTableAdminClientTest.TABLE_NAME.toString()).setView(SCHEMA_VIEW).build();
        Table expectedResponse = com.google.bigtable.admin.v2.Table.newBuilder().setName(BigtableTableAdminClientTest.TABLE_NAME.toString()).build();
        Mockito.when(mockGetTableCallable.futureCall(expectedRequest)).thenReturn(ApiFutures.immediateFuture(expectedResponse));
        // Execute
        com.google.cloud.bigtable.admin.v2.models.Table actualResult = adminClient.getTable(BigtableTableAdminClientTest.TABLE_ID);
        // Verify
        assertThat(actualResult).isEqualTo(com.google.cloud.bigtable.admin.v2.models.Table.fromProto(expectedResponse));
    }

    @Test
    public void testListTables() {
        // Setup
        ListTablesRequest expectedRequest = com.google.bigtable.admin.v2.ListTablesRequest.newBuilder().setParent(BigtableTableAdminClientTest.INSTANCE_NAME).build();
        // 3 Tables spread across 2 pages
        List<Table> expectedProtos = Lists.newArrayList();
        for (int i = 0; i < 3; i++) {
            expectedProtos.add(com.google.bigtable.admin.v2.Table.newBuilder().setName(((BigtableTableAdminClientTest.TABLE_NAME) + i)).build());
        }
        // 2 on the first page
        ListTablesPage page0 = Mockito.mock(ListTablesPage.class);
        Mockito.when(page0.getValues()).thenReturn(expectedProtos.subList(0, 2));
        Mockito.when(page0.getNextPageToken()).thenReturn("next-page");
        Mockito.when(page0.hasNextPage()).thenReturn(true);
        // 1 on the last page
        ListTablesPage page1 = Mockito.mock(ListTablesPage.class);
        Mockito.when(page1.getValues()).thenReturn(expectedProtos.subList(2, 3));
        // Link page0 to page1
        Mockito.when(page0.getNextPageAsync()).thenReturn(ApiFutures.immediateFuture(page1));
        // Link page to the response
        ListTablesPagedResponse response0 = Mockito.mock(ListTablesPagedResponse.class);
        Mockito.when(response0.getPage()).thenReturn(page0);
        Mockito.when(mockListTableCallable.futureCall(expectedRequest)).thenReturn(ApiFutures.immediateFuture(response0));
        // Execute
        List<String> actualResults = adminClient.listTables();
        // Verify
        List<String> expectedResults = Lists.newArrayList();
        for (Table expectedProto : expectedProtos) {
            expectedResults.add(TableName.parse(expectedProto.getName()).getTable());
        }
        assertThat(actualResults).containsExactlyElementsIn(expectedResults);
    }

    @Test
    public void testDropRowRange() {
        // Setup
        DropRowRangeRequest expectedRequest = DropRowRangeRequest.newBuilder().setName(BigtableTableAdminClientTest.TABLE_NAME).setRowKeyPrefix(ByteString.copyFromUtf8("rowKeyPrefix")).build();
        final Empty expectedResponse = Empty.getDefaultInstance();
        final AtomicBoolean wasCalled = new AtomicBoolean(false);
        Mockito.when(mockDropRowRangeCallable.futureCall(expectedRequest)).thenAnswer(new Answer<ApiFuture<Empty>>() {
            @Override
            public ApiFuture<Empty> answer(InvocationOnMock invocationOnMock) {
                wasCalled.set(true);
                return ApiFutures.immediateFuture(expectedResponse);
            }
        });
        // Execute
        adminClient.dropRowRange(BigtableTableAdminClientTest.TABLE_ID, "rowKeyPrefix");
        // Verify
        assertThat(wasCalled.get()).isTrue();
    }

    @Test
    public void testAwaitReplication() {
        // Setup
        @SuppressWarnings("UnnecessaryLocalVariable")
        TableName expectedRequest = TableName.parse(BigtableTableAdminClientTest.TABLE_NAME);
        final AtomicBoolean wasCalled = new AtomicBoolean(false);
        Mockito.when(mockAwaitReplicationCallable.futureCall(expectedRequest)).thenAnswer(new Answer<ApiFuture<Void>>() {
            @Override
            public ApiFuture<Void> answer(InvocationOnMock invocationOnMock) {
                wasCalled.set(true);
                return ApiFutures.immediateFuture(null);
            }
        });
        // Execute
        adminClient.awaitReplication(BigtableTableAdminClientTest.TABLE_ID);
        // Verify
        assertThat(wasCalled.get()).isTrue();
    }

    @Test
    public void testExistsTrue() {
        // Setup
        Table expectedResponse = com.google.bigtable.admin.v2.Table.newBuilder().setName(BigtableTableAdminClientTest.TABLE_NAME.toString()).build();
        Mockito.when(mockGetTableCallable.futureCall(Matchers.any(GetTableRequest.class))).thenReturn(ApiFutures.immediateFuture(expectedResponse));
        // Execute
        boolean found = adminClient.exists(BigtableTableAdminClientTest.TABLE_ID);
        // Verify
        assertThat(found).isTrue();
    }

    @Test
    public void testExistsFalse() {
        // Setup
        NotFoundException exception = new NotFoundException("fake error", null, GrpcStatusCode.of(NOT_FOUND), false);
        Mockito.when(mockGetTableCallable.futureCall(Matchers.any(GetTableRequest.class))).thenReturn(ApiFutures.<Table>immediateFailedFuture(exception));
        // Execute
        boolean found = adminClient.exists(BigtableTableAdminClientTest.TABLE_ID);
        // Verify
        assertThat(found).isFalse();
    }
}

