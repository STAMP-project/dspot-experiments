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
package com.google.cloud.bigtable.data.v2.it;


import com.google.cloud.bigtable.data.v2.it.env.TestEnvRule;
import com.google.cloud.bigtable.data.v2.models.ConditionalRowMutation;
import com.google.cloud.bigtable.data.v2.models.Filters;
import com.google.cloud.bigtable.data.v2.models.Mutation;
import com.google.cloud.bigtable.data.v2.models.Row;
import com.google.cloud.bigtable.data.v2.models.RowMutation;
import com.google.protobuf.ByteString;
import java.util.concurrent.TimeUnit;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class CheckAndMutateIT {
    @ClassRule
    public static TestEnvRule testEnvRule = new TestEnvRule();

    @Test
    public void test() throws Exception {
        String tableId = CheckAndMutateIT.testEnvRule.env().getTableId();
        String rowKey = CheckAndMutateIT.testEnvRule.env().getRowPrefix();
        String familyId = CheckAndMutateIT.testEnvRule.env().getFamilyId();
        CheckAndMutateIT.testEnvRule.env().getDataClient().mutateRowCallable().call(RowMutation.create(tableId, rowKey).setCell(familyId, "q1", "val1").setCell(familyId, "q2", "val2"));
        CheckAndMutateIT.testEnvRule.env().getDataClient().checkAndMutateRowAsync(ConditionalRowMutation.create(tableId, rowKey).condition(Filters.FILTERS.qualifier().exactMatch("q1")).then(Mutation.create().setCell(familyId, "q3", "q1"))).get(1, TimeUnit.MINUTES);
        Row row = CheckAndMutateIT.testEnvRule.env().getDataClient().readRowsCallable().first().call(com.google.cloud.bigtable.data.v2.models.Query.create(tableId).rowKey(rowKey));
        assertThat(row.getCells()).hasSize(3);
        assertThat(row.getCells().get(2).getValue()).isEqualTo(ByteString.copyFromUtf8("q1"));
    }
}

