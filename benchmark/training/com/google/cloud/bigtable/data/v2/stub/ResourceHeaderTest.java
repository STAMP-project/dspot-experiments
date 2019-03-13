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
package com.google.cloud.bigtable.data.v2.stub;


import com.google.api.gax.grpc.testing.InProcessServer;
import com.google.api.gax.grpc.testing.LocalChannelProvider;
import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.internal.NameUtil;
import com.google.cloud.bigtable.data.v2.models.BulkMutationBatcher;
import com.google.cloud.bigtable.data.v2.models.BulkMutationBatcher.BulkMutationFailure;
import com.google.cloud.bigtable.data.v2.models.ConditionalRowMutation;
import com.google.cloud.bigtable.data.v2.models.Mutation;
import com.google.cloud.bigtable.data.v2.models.Query;
import com.google.cloud.bigtable.data.v2.models.ReadModifyWriteRow;
import com.google.cloud.bigtable.data.v2.models.RowMutation;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class ResourceHeaderTest {
    private static final String PROJECT_ID = "fake-project";

    private static final String INSTANCE_ID = "fake-instance";

    private static final String TABLE_ID = "fake-table";

    private static final String NAME = "resource-header-test:123";

    private static final Pattern EXPECTED_HEADER_PATTERN = Pattern.compile(((".*" + (NameUtil.formatTableName(ResourceHeaderTest.PROJECT_ID, ResourceHeaderTest.INSTANCE_ID, ResourceHeaderTest.TABLE_ID))) + ".*"));

    private static final String HEADER_NAME = "x-goog-request-params";

    private static final String TEST_HEADER_NAME = "simple-header-name";

    private static final String TEST_HEADER_VALUE = "simple-header-value";

    private static final Pattern TEST_PATTERN = Pattern.compile(((".*" + (ResourceHeaderTest.TEST_HEADER_VALUE)) + ".*"));

    private InProcessServer<?> server;

    private LocalChannelProvider channelProvider;

    private BigtableDataClient client;

    @Test
    public void readRowsTest() {
        client.readRows(Query.create(ResourceHeaderTest.TABLE_ID));
        verifyHeaderSent();
    }

    @Test
    public void sampleRowKeysTest() {
        client.sampleRowKeysAsync(ResourceHeaderTest.TABLE_ID);
        verifyHeaderSent();
    }

    @Test
    public void mutateRowTest() {
        client.mutateRowAsync(RowMutation.create(ResourceHeaderTest.TABLE_ID, "fake-key").deleteRow());
        verifyHeaderSent();
    }

    @Test
    public void mutateRowsTest() throws InterruptedException, TimeoutException {
        try (BulkMutationBatcher batcher = client.newBulkMutationBatcher()) {
            batcher.add(RowMutation.create(ResourceHeaderTest.TABLE_ID, "fake-key").deleteRow());
        } catch (BulkMutationFailure e) {
            // Ignore the errors: none of the methods are actually implemented
        }
        verifyHeaderSent();
    }

    @Test
    public void checkAndMutateRowTest() {
        client.checkAndMutateRowAsync(ConditionalRowMutation.create(ResourceHeaderTest.TABLE_ID, "fake-key").then(Mutation.create().deleteRow()));
        verifyHeaderSent();
    }

    @Test
    public void readModifyWriteTest() {
        client.readModifyWriteRowAsync(ReadModifyWriteRow.create(ResourceHeaderTest.TABLE_ID, "fake-key").increment("cf", "q", 1));
        verifyHeaderSent();
    }
}

