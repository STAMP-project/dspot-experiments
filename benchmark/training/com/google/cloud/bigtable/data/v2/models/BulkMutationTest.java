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


import MutateRowsRequest.Builder;
import com.google.bigtable.v2.MutateRowsRequest;
import com.google.cloud.bigtable.data.v2.internal.NameUtil;
import com.google.cloud.bigtable.data.v2.internal.RequestContext;
import com.google.protobuf.ByteString;
import com.google.protobuf.TextFormat;
import com.google.protobuf.TextFormat.ParseException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class BulkMutationTest {
    private static final String PROJECT_ID = "fake-project";

    private static final String INSTANCE_ID = "fake-instance";

    private static final String TABLE_ID = "fake-table";

    private static final String APP_PROFILE = "fake-profile";

    private static final RequestContext REQUEST_CONTEXT = RequestContext.create(BulkMutationTest.PROJECT_ID, BulkMutationTest.INSTANCE_ID, BulkMutationTest.APP_PROFILE);

    @Test
    public void test() throws ParseException {
        BulkMutation m = BulkMutation.create(BulkMutationTest.TABLE_ID).add("key-a", Mutation.create().setCell("fake-family1", "fake-qualifier1", 1000, "fake-value1").setCell("fake-family2", "fake-qualifier2", 2000, "fake-value2")).add(ByteString.copyFromUtf8("key-b"), Mutation.create().setCell("fake-family3", "fake-qualifier3", 3000, "fake-value3"));
        MutateRowsRequest actual = m.toProto(BulkMutationTest.REQUEST_CONTEXT);
        MutateRowsRequest.Builder expected = MutateRowsRequest.newBuilder().setTableName(NameUtil.formatTableName(BulkMutationTest.PROJECT_ID, BulkMutationTest.INSTANCE_ID, BulkMutationTest.TABLE_ID)).setAppProfileId(BulkMutationTest.APP_PROFILE);
        TextFormat.merge(("entries {" + (((((((((((((((((((((((((((("  row_key: 'key-a'" + "  mutations {") + "    set_cell {") + "      family_name: 'fake-family1'") + "      column_qualifier: 'fake-qualifier1'") + "      timestamp_micros: 1000") + "      value: 'fake-value1'") + "    }") + "  }") + "  mutations {") + "    set_cell {") + "      family_name: 'fake-family2'") + "      column_qualifier: 'fake-qualifier2'") + "      timestamp_micros: 2000") + "      value: 'fake-value2'") + "    }") + "  }") + "}") + "entries {") + "  row_key: 'key-b'") + "  mutations {") + "    set_cell {") + "      family_name: 'fake-family3'") + "      column_qualifier: 'fake-qualifier3'") + "      timestamp_micros: 3000") + "      value: 'fake-value3'") + "    }") + "  }") + "}")), expected);
        assertThat(actual).isEqualTo(expected.build());
    }

    @Test
    public void serializationTest() throws IOException, ClassNotFoundException {
        BulkMutation expected = BulkMutation.create(BulkMutationTest.TABLE_ID).add("key-a", Mutation.create().setCell("fake-family1", "fake-qualifier1", 1000, "fake-value1"));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(expected);
        oos.close();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
        BulkMutation actual = ((BulkMutation) (ois.readObject()));
        assertThat(actual.toProto(BulkMutationTest.REQUEST_CONTEXT)).isEqualTo(expected.toProto(BulkMutationTest.REQUEST_CONTEXT));
    }
}

