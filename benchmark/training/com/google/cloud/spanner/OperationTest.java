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


import Code.NOT_FOUND;
import Duration.ZERO;
import ErrorCode.DEADLINE_EXCEEDED;
import Operation.Parser;
import com.google.api.core.ApiClock;
import com.google.cloud.RetryOption;
import com.google.cloud.spanner.spi.v1.SpannerRpc;
import com.google.longrunning.Operation;
import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.rpc.Status;
import com.google.spanner.admin.database.v1.CreateDatabaseMetadata;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.threeten.bp.Duration;


/**
 * Unit tests for {@link Operation}.
 */
@RunWith(JUnit4.class)
public class OperationTest {
    private static final String NAME = "projects/test-project/instances/test-instance/databases/database-1";

    @Mock
    private SpannerRpc rpc;

    @Mock
    private DatabaseAdminClient dbClient;

    @Mock
    private ApiClock clock;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private class ParserImpl implements Parser<Database, String> {
        @Override
        public Database parseResult(Any response) {
            try {
                return Database.fromProto(response.unpack(com.google.spanner.admin.database.v1.Database.class), dbClient);
            } catch (InvalidProtocolBufferException e) {
                return null;
            }
        }

        @Override
        public String parseMetadata(Any metadata) {
            try {
                return metadata.unpack(CreateDatabaseMetadata.class).getDatabase();
            } catch (InvalidProtocolBufferException e) {
                return null;
            }
        }
    }

    @Test
    public void failedOperation() {
        Operation proto = Operation.newBuilder().setName("op1").setDone(true).setError(Status.newBuilder().setCode(NOT_FOUND.getNumber())).build();
        Operation<Database, String> op = Operation.create(rpc, proto, new OperationTest.ParserImpl());
        assertThat(op.getName()).isEqualTo("op1");
        assertThat(op.isDone()).isTrue();
        assertThat(op.isSuccessful()).isFalse();
        assertThat(op.getMetadata()).isNull();
        expectedException.expect(SpannerMatchers.isSpannerException(ErrorCode.NOT_FOUND));
        op.getResult();
    }

    @Test
    public void successfulOperation() {
        com.google.spanner.admin.database.v1.Database db = com.google.spanner.admin.database.v1.Database.newBuilder().setName(OperationTest.NAME).setState(com.google.spanner.admin.database.v1.Database).build();
        Operation proto = Operation.newBuilder().setName("op1").setDone(true).setResponse(Any.pack(db)).build();
        Operation<Database, String> op = Operation.create(rpc, proto, new OperationTest.ParserImpl());
        assertThat(op.getName()).isEqualTo("op1");
        assertThat(op.isDone()).isTrue();
        assertThat(op.isSuccessful()).isTrue();
        assertThat(op.getMetadata()).isNull();
        assertThat(op.getResult().getId().getName()).isEqualTo(OperationTest.NAME);
    }

    @Test
    public void pendingOperation() {
        Operation proto = Operation.newBuilder().setName("op1").setDone(false).setMetadata(Any.pack(CreateDatabaseMetadata.newBuilder().setDatabase(OperationTest.NAME).build())).build();
        Operation<Database, String> op = Operation.create(rpc, proto, new OperationTest.ParserImpl());
        assertThat(op.getName()).isEqualTo("op1");
        assertThat(op.isDone()).isFalse();
        assertThat(op.isSuccessful()).isFalse();
        assertThat(op.getMetadata()).isEqualTo(OperationTest.NAME);
        assertThat(op.getResult()).isNull();
    }

    @Test
    public void reload() {
        Operation proto = Operation.newBuilder().setName("op1").setDone(false).build();
        Operation<Database, String> op = Operation.create(rpc, proto, new OperationTest.ParserImpl());
        com.google.spanner.admin.database.v1.Database db = com.google.spanner.admin.database.v1.Database.newBuilder().setName(OperationTest.NAME).setState(com.google.spanner.admin.database.v1.Database).build();
        proto = Operation.newBuilder().setName("op1").setDone(true).setResponse(Any.pack(db)).build();
        Mockito.when(rpc.getOperation("op1")).thenReturn(proto);
        op = op.reload();
        assertThat(op.getName()).isEqualTo("op1");
        assertThat(op.isDone()).isTrue();
        assertThat(op.isSuccessful()).isTrue();
        assertThat(op.getMetadata()).isNull();
        assertThat(op.getResult().getId().getName()).isEqualTo(OperationTest.NAME);
    }

    @Test
    public void waitForCompletes() throws Exception {
        Operation proto = Operation.newBuilder().setName("op1").setDone(false).build();
        Operation<Database, String> op = Operation.create(rpc, proto, new OperationTest.ParserImpl());
        com.google.spanner.admin.database.v1.Database db = com.google.spanner.admin.database.v1.Database.newBuilder().setName(OperationTest.NAME).setState(com.google.spanner.admin.database.v1.Database).build();
        Operation proto2 = Operation.newBuilder().setName("op1").setDone(true).setResponse(Any.pack(db)).build();
        Mockito.when(rpc.getOperation("op1")).thenReturn(proto, proto2);
        op = op.waitFor(RetryOption.totalTimeout(Duration.ofSeconds(3)), RetryOption.initialRetryDelay(ZERO));
        assertThat(op.getName()).isEqualTo("op1");
        assertThat(op.isDone()).isTrue();
        assertThat(op.isSuccessful()).isTrue();
        assertThat(op.getMetadata()).isNull();
        assertThat(op.getResult().getId().getName()).isEqualTo(OperationTest.NAME);
    }

    @Test
    public void waitForTimesout() throws Exception {
        Operation proto = Operation.newBuilder().setName("op1").setDone(false).build();
        Operation<Database, String> op = Operation.create(rpc, proto, new OperationTest.ParserImpl(), clock);
        Mockito.when(rpc.getOperation("op1")).thenReturn(proto);
        Mockito.when(clock.nanoTime()).thenReturn(0L, 50000000L, 100000000L, 150000000L);
        expectedException.expect(SpannerMatchers.isSpannerException(DEADLINE_EXCEEDED));
        op.waitFor(RetryOption.totalTimeout(Duration.ofMillis(100L)), RetryOption.initialRetryDelay(ZERO));
    }
}

