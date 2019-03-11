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


import Mutation.SetCell;
import com.google.bigtable.admin.v2.Table;
import com.google.bigtable.v2.Mutation;
import com.google.cloud.bigtable.config.BigtableOptions;
import com.google.cloud.bigtable.grpc.BigtableSession;
import com.google.cloud.bigtable.grpc.BigtableTableAdminClient;
import com.google.protobuf.ByteString;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.GenerateSequence;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * End-to-end tests of BigtableWrite.
 */
@RunWith(JUnit4.class)
public class BigtableWriteIT implements Serializable {
    /**
     * These tests requires a static instances because the writers go through a serialization step
     * when executing the test and would not affect passed-in objects otherwise.
     */
    private static final String COLUMN_FAMILY_NAME = "cf";

    private static BigtableTestOptions options;

    private BigtableOptions bigtableOptions;

    private static BigtableSession session;

    private static BigtableTableAdminClient tableAdminClient;

    private final String tableId = String.format("BigtableWriteIT-%tF-%<tH-%<tM-%<tS-%<tL", new Date());

    private String project;

    @Test
    public void testE2EBigtableWrite() throws Exception {
        final String tableName = bigtableOptions.getInstanceName().toTableNameStr(tableId);
        final String instanceName = bigtableOptions.getInstanceName().toString();
        final int numRows = 1000;
        final List<KV<ByteString, ByteString>> testData = generateTableData(numRows);
        createEmptyTable(instanceName, tableId);
        Pipeline p = Pipeline.create(BigtableWriteIT.options);
        p.apply(GenerateSequence.from(0).to(numRows)).apply(ParDo.of(new org.apache.beam.sdk.transforms.DoFn<Long, KV<ByteString, Iterable<Mutation>>>() {
            @ProcessElement
            public void processElement(ProcessContext c) {
                int index = c.element().intValue();
                Iterable<Mutation> mutations = ImmutableList.of(Mutation.newBuilder().setSetCell(SetCell.newBuilder().setValue(testData.get(index).getValue()).setFamilyName(BigtableWriteIT.COLUMN_FAMILY_NAME)).build());
                c.output(KV.of(testData.get(index).getKey(), mutations));
            }
        })).apply(BigtableIO.write().withBigtableOptions(bigtableOptions).withTableId(tableId));
        p.run();
        // Test number of column families and column family name equality
        Table table = getTable(tableName);
        Assert.assertThat(table.getColumnFamiliesMap().keySet(), Matchers.hasSize(1));
        Assert.assertThat(table.getColumnFamiliesMap(), Matchers.hasKey(BigtableWriteIT.COLUMN_FAMILY_NAME));
        // Test table data equality
        List<KV<ByteString, ByteString>> tableData = getTableData(tableName);
        Assert.assertThat(tableData, Matchers.containsInAnyOrder(testData.toArray()));
    }
}

