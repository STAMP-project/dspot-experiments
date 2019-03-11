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


import com.google.api.gax.rpc.ServerStream;
import com.google.bigtable.v2.ReadRowsRequest;
import com.google.cloud.bigtable.data.v2.models.DefaultRowAdapter;
import com.google.cloud.bigtable.data.v2.models.Row;
import com.google.cloud.bigtable.gaxx.testing.FakeStreamingApi;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class FilterMarkerRowsCallableTest {
    private final DefaultRowAdapter rowAdapter = new DefaultRowAdapter();

    @Test
    public void testEmpty() {
        FakeStreamingApi.ServerStreamingStashCallable<ReadRowsRequest, Row> innerCallable = new FakeStreamingApi.ServerStreamingStashCallable<>();
        FilterMarkerRowsCallable<Row> filterCallable = new FilterMarkerRowsCallable(innerCallable, rowAdapter);
        ServerStream<Row> results = filterCallable.call(ReadRowsRequest.getDefaultInstance());
        assertThat(results).isEmpty();
    }

    @Test
    public void testOnlyMarker() {
        FakeStreamingApi.ServerStreamingStashCallable<ReadRowsRequest, Row> innerCallable = new FakeStreamingApi.ServerStreamingStashCallable(Lists.newArrayList(buildScanMarker()));
        FilterMarkerRowsCallable<Row> filterCallable = new FilterMarkerRowsCallable(innerCallable, rowAdapter);
        ServerStream<Row> results = filterCallable.call(ReadRowsRequest.getDefaultInstance());
        assertThat(results).isEmpty();
    }

    @Test
    public void testRealRow() {
        Row row = buildRealRow();
        FakeStreamingApi.ServerStreamingStashCallable<ReadRowsRequest, Row> innerCallable = new FakeStreamingApi.ServerStreamingStashCallable(Lists.newArrayList(row));
        FilterMarkerRowsCallable<Row> filterCallable = new FilterMarkerRowsCallable(innerCallable, rowAdapter);
        ServerStream<Row> results = filterCallable.call(ReadRowsRequest.getDefaultInstance());
        assertThat(results).containsExactly(row);
    }

    @Test
    public void testMixed() {
        Row row = buildRealRow();
        Row markerRow = buildScanMarker();
        FakeStreamingApi.ServerStreamingStashCallable<ReadRowsRequest, Row> innerCallable = new FakeStreamingApi.ServerStreamingStashCallable(Lists.newArrayList(row, markerRow));
        FilterMarkerRowsCallable<Row> filterCallable = new FilterMarkerRowsCallable(innerCallable, rowAdapter);
        ServerStream<Row> results = filterCallable.call(ReadRowsRequest.getDefaultInstance());
        assertThat(results).containsExactly(row);
    }
}

