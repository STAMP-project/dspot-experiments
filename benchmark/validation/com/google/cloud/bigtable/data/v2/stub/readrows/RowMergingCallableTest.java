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
package com.google.cloud.bigtable.data.v2.stub.readrows;


import ByteString.EMPTY;
import com.google.bigtable.v2.ReadRowsRequest;
import com.google.bigtable.v2.ReadRowsResponse;
import com.google.bigtable.v2.ReadRowsResponse.CellChunk;
import com.google.cloud.bigtable.data.v2.models.DefaultRowAdapter;
import com.google.cloud.bigtable.data.v2.models.Row;
import com.google.cloud.bigtable.data.v2.models.RowCell;
import com.google.cloud.bigtable.gaxx.testing.FakeStreamingApi;
import com.google.common.collect.Lists;
import com.google.common.truth.Truth;
import com.google.protobuf.ByteString;
import com.google.protobuf.BytesValue;
import com.google.protobuf.StringValue;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Additional tests in addition to {@link com.google.cloud.bigtable.data.v2.stub.readrows.ReadRowsMergingAcceptanceTest}. At some point
 * they should be reintegrated into the json file.
 */
@RunWith(JUnit4.class)
public class RowMergingCallableTest {
    @Test
    public void scanMarker() {
        FakeStreamingApi.ServerStreamingStashCallable<ReadRowsRequest, ReadRowsResponse> inner = new FakeStreamingApi.ServerStreamingStashCallable(// send a scan marker
        Lists.newArrayList(ReadRowsResponse.newBuilder().setLastScannedRowKey(ByteString.copyFromUtf8("key1")).build()));
        RowMergingCallable<Row> rowMergingCallable = new RowMergingCallable(inner, new DefaultRowAdapter());
        List<Row> results = rowMergingCallable.all().call(ReadRowsRequest.getDefaultInstance());
        Truth.assertThat(results).containsExactly(Row.create(ByteString.copyFromUtf8("key1"), Lists.<RowCell>newArrayList()));
    }

    @Test
    public void invalidMarkerInCell() {
        FakeStreamingApi.ServerStreamingStashCallable<ReadRowsRequest, ReadRowsResponse> inner = new FakeStreamingApi.ServerStreamingStashCallable(// send a scan marker
        // finish the cell & row
        Lists.newArrayList(ReadRowsResponse.newBuilder().addChunks(CellChunk.newBuilder().setRowKey(ByteString.copyFromUtf8("key1")).setFamilyName(StringValue.newBuilder().setValue("family")).setQualifier(BytesValue.newBuilder().setValue(EMPTY)).setTimestampMicros(1000).setValue(ByteString.copyFromUtf8("a")).setValueSize(2)).build(), ReadRowsResponse.newBuilder().setLastScannedRowKey(ByteString.copyFromUtf8("key1")).build(), ReadRowsResponse.newBuilder().addChunks(CellChunk.newBuilder().setValue(ByteString.copyFromUtf8("b")).setValueSize(0).setCommitRow(true)).build()));
        RowMergingCallable<Row> rowMergingCallable = new RowMergingCallable(inner, new DefaultRowAdapter());
        Throwable actualError = null;
        try {
            rowMergingCallable.all().call(ReadRowsRequest.getDefaultInstance());
        } catch (Throwable t) {
            actualError = t;
        }
        Truth.assertThat(actualError).isInstanceOf(IllegalStateException.class);
    }
}

