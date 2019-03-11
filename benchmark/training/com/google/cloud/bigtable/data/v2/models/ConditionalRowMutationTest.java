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
package com.google.cloud.bigtable.data.v2.models;


import Filters.FILTERS;
import com.google.bigtable.v2.CheckAndMutateRowRequest;
import com.google.bigtable.v2.Mutation.DeleteFromColumn;
import com.google.bigtable.v2.RowFilter;
import com.google.cloud.bigtable.data.v2.internal.NameUtil;
import com.google.cloud.bigtable.data.v2.internal.RequestContext;
import com.google.protobuf.ByteString;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class ConditionalRowMutationTest {
    private static final String PROJECT_ID = "fake-project";

    private static final String INSTANCE_ID = "fake-instance";

    private static final String TABLE_ID = "fake-table";

    private static final String APP_PROFILE_ID = "fake-profile";

    private static final RequestContext REQUEST_CONTEXT = RequestContext.create(ConditionalRowMutationTest.PROJECT_ID, ConditionalRowMutationTest.INSTANCE_ID, ConditionalRowMutationTest.APP_PROFILE_ID);

    private static final ByteString TEST_KEY = ByteString.copyFromUtf8("fake-key");

    @Test
    public void toProtoTest() {
        Mutation ignoredThenMutation = Mutation.create().deleteRow();
        ConditionalRowMutation mutation = ConditionalRowMutation.create(ConditionalRowMutationTest.TABLE_ID, ConditionalRowMutationTest.TEST_KEY).then(ignoredThenMutation);
        CheckAndMutateRowRequest actualProto = mutation.toProto(ConditionalRowMutationTest.REQUEST_CONTEXT).toBuilder().clearTrueMutations().build();
        assertThat(actualProto).isEqualTo(CheckAndMutateRowRequest.newBuilder().setTableName(NameUtil.formatTableName(ConditionalRowMutationTest.PROJECT_ID, ConditionalRowMutationTest.INSTANCE_ID, ConditionalRowMutationTest.TABLE_ID)).setAppProfileId(ConditionalRowMutationTest.APP_PROFILE_ID).setRowKey(ConditionalRowMutationTest.TEST_KEY).build());
    }

    @Test
    public void conditionTest() {
        ConditionalRowMutation mutation = ConditionalRowMutation.create(ConditionalRowMutationTest.TABLE_ID, ConditionalRowMutationTest.TEST_KEY).condition(FILTERS.key().regex("a.*")).then(Mutation.create().deleteRow());
        CheckAndMutateRowRequest actualProto = mutation.toProto(ConditionalRowMutationTest.REQUEST_CONTEXT);
        assertThat(actualProto.getPredicateFilter()).isEqualTo(RowFilter.newBuilder().setRowKeyRegexFilter(ByteString.copyFromUtf8("a.*")).build());
    }

    @Test
    public void thenTest() {
        ConditionalRowMutation mutation = ConditionalRowMutation.create(ConditionalRowMutationTest.TABLE_ID, ConditionalRowMutationTest.TEST_KEY).then(Mutation.create().deleteCells("family1", "qualifier1")).then(Mutation.create().deleteCells("family2", "qualifier2"));
        CheckAndMutateRowRequest actualProto = mutation.toProto(ConditionalRowMutationTest.REQUEST_CONTEXT);
        assertThat(actualProto.getTrueMutationsList()).containsExactly(com.google.bigtable.v2.Mutation.newBuilder().setDeleteFromColumn(DeleteFromColumn.newBuilder().setFamilyName("family1").setColumnQualifier(ByteString.copyFromUtf8("qualifier1"))).build(), com.google.bigtable.v2.Mutation.newBuilder().setDeleteFromColumn(DeleteFromColumn.newBuilder().setFamilyName("family2").setColumnQualifier(ByteString.copyFromUtf8("qualifier2"))).build()).inOrder();
    }

    @Test
    public void otherwiseTest() {
        ConditionalRowMutation mutation = ConditionalRowMutation.create(ConditionalRowMutationTest.TABLE_ID, ConditionalRowMutationTest.TEST_KEY).otherwise(Mutation.create().deleteCells("family1", "qualifier1")).otherwise(Mutation.create().deleteCells("family2", "qualifier2"));
        CheckAndMutateRowRequest actualProto = mutation.toProto(ConditionalRowMutationTest.REQUEST_CONTEXT);
        assertThat(actualProto.getFalseMutationsList()).containsExactly(com.google.bigtable.v2.Mutation.newBuilder().setDeleteFromColumn(DeleteFromColumn.newBuilder().setFamilyName("family1").setColumnQualifier(ByteString.copyFromUtf8("qualifier1"))).build(), com.google.bigtable.v2.Mutation.newBuilder().setDeleteFromColumn(DeleteFromColumn.newBuilder().setFamilyName("family2").setColumnQualifier(ByteString.copyFromUtf8("qualifier2"))).build()).inOrder();
    }

    @Test
    public void noEffectClausesTest() {
        ConditionalRowMutation mutation = ConditionalRowMutation.create(ConditionalRowMutationTest.TABLE_ID, ConditionalRowMutationTest.TEST_KEY).condition(FILTERS.pass());
        Throwable actualError = null;
        try {
            mutation.toProto(ConditionalRowMutationTest.REQUEST_CONTEXT);
        } catch (Throwable t) {
            actualError = t;
        }
        assertThat(actualError).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void serializationTest() throws IOException, ClassNotFoundException {
        ConditionalRowMutation expected = ConditionalRowMutation.create(ConditionalRowMutationTest.TABLE_ID, ConditionalRowMutationTest.TEST_KEY).condition(FILTERS.pass()).then(Mutation.create().deleteRow()).otherwise(Mutation.create().deleteFamily("cf"));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(expected);
        oos.close();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
        ConditionalRowMutation actual = ((ConditionalRowMutation) (ois.readObject()));
        assertThat(actual.toProto(ConditionalRowMutationTest.REQUEST_CONTEXT)).isEqualTo(expected.toProto(ConditionalRowMutationTest.REQUEST_CONTEXT));
    }
}

