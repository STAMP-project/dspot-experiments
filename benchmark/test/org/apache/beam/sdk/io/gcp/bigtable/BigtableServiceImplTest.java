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
package org.apache.beam.sdk.io.gcp.bigtable;


import BigtableService.Reader;
import BigtableService.Writer;
import com.google.bigtable.v2.MutateRowResponse;
import com.google.bigtable.v2.MutateRowsRequest.Entry;
import com.google.bigtable.v2.Mutation;
import com.google.bigtable.v2.Mutation.SetCell;
import com.google.bigtable.v2.ReadRowsRequest;
import com.google.bigtable.v2.Row;
import com.google.cloud.bigtable.grpc.BigtableDataClient;
import com.google.cloud.bigtable.grpc.BigtableInstanceName;
import com.google.cloud.bigtable.grpc.BigtableSession;
import com.google.cloud.bigtable.grpc.BigtableTableName;
import com.google.cloud.bigtable.grpc.async.BulkMutation;
import com.google.cloud.bigtable.grpc.scanner.ResultScanner;
import com.google.common.util.concurrent.SettableFuture;
import com.google.protobuf.ByteString;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.apache.beam.sdk.io.gcp.bigtable.BigtableIO.BigtableSource;
import org.apache.beam.sdk.io.range.ByteKey;
import org.apache.beam.sdk.io.range.ByteKeyRange;
import org.apache.beam.sdk.options.ValueProvider.StaticValueProvider;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Unit tests of BigtableServiceImpl.
 */
@RunWith(JUnit4.class)
public class BigtableServiceImplTest {
    private static final BigtableTableName TABLE_NAME = new BigtableInstanceName("project", "instance").toTableName("table");

    @Mock
    private BigtableSession mockSession;

    @Mock
    private BulkMutation mockBulkMutation;

    @Mock
    private BigtableDataClient mockBigtableDataClient;

    @Mock
    private BigtableSource mockBigtableSource;

    /**
     * This test ensures that protobuf creation and interactions with {@link BigtableDataClient} work
     * as expected.
     *
     * @throws IOException
     * 		
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testRead() throws IOException {
        ByteKey start = ByteKey.copyFrom("a".getBytes(StandardCharsets.UTF_8));
        ByteKey end = ByteKey.copyFrom("b".getBytes(StandardCharsets.UTF_8));
        Mockito.when(mockBigtableSource.getRanges()).thenReturn(Arrays.asList(ByteKeyRange.of(start, end)));
        Mockito.when(mockBigtableSource.getTableId()).thenReturn(StaticValueProvider.of("table_name"));
        @SuppressWarnings("unchecked")
        ResultScanner<Row> mockResultScanner = Mockito.mock(ResultScanner.class);
        Row expectedRow = Row.newBuilder().setKey(ByteString.copyFromUtf8("a")).build();
        Mockito.when(mockResultScanner.next()).thenReturn(expectedRow).thenReturn(null);
        Mockito.when(mockBigtableDataClient.readRows(ArgumentMatchers.any(ReadRowsRequest.class))).thenReturn(mockResultScanner);
        BigtableService.Reader underTest = new BigtableServiceImpl.BigtableReaderImpl(mockSession, mockBigtableSource);
        underTest.start();
        Assert.assertEquals(expectedRow, underTest.getCurrentRow());
        Assert.assertFalse(underTest.advance());
        underTest.close();
        Mockito.verify(mockResultScanner, Mockito.times(1)).close();
    }

    /**
     * This test ensures that protobuf creation and interactions with {@link BulkMutation} work as
     * expected.
     *
     * @throws IOException
     * 		
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testWrite() throws IOException, InterruptedException {
        BigtableService.Writer underTest = new BigtableServiceImpl.BigtableWriterImpl(mockSession, BigtableServiceImplTest.TABLE_NAME);
        Mutation mutation = Mutation.newBuilder().setSetCell(SetCell.newBuilder().setFamilyName("Family").build()).build();
        ByteString key = ByteString.copyFromUtf8("key");
        SettableFuture<MutateRowResponse> fakeResponse = SettableFuture.create();
        Mockito.when(mockBulkMutation.add(ArgumentMatchers.any(Entry.class))).thenReturn(fakeResponse);
        underTest.writeRecord(KV.of(key, ImmutableList.of(mutation)));
        Entry expected = MutateRowsRequest.Entry.newBuilder().setRowKey(key).addMutations(mutation).build();
        Mockito.verify(mockBulkMutation, Mockito.times(1)).add(expected);
        underTest.close();
        Mockito.verify(mockBulkMutation, Mockito.times(1)).flush();
    }
}

