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


import ErrorCode.INTERNAL;
import Type.StructField;
import com.google.cloud.Timestamp;
import com.google.cloud.spanner.spi.v1.SpannerRpc;
import com.google.protobuf.ByteString;
import com.google.protobuf.ListValue;
import com.google.protobuf.util.Timestamps;
import com.google.spanner.v1.BeginTransactionRequest;
import com.google.spanner.v1.CommitRequest;
import com.google.spanner.v1.CommitResponse;
import com.google.spanner.v1.Mutation.Write;
import com.google.spanner.v1.PartialResultSet;
import com.google.spanner.v1.Session;
import com.google.spanner.v1.Transaction;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Unit tests for {@link com.google.cloud.spanner.SpannerImpl.SessionImpl}.
 */
@RunWith(JUnit4.class)
public class SessionImplTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private SpannerRpc rpc;

    @Mock
    private SpannerOptions spannerOptions;

    private Session session;

    @Captor
    private ArgumentCaptor<Map<SpannerRpc.Option, Object>> optionsCaptor;

    private Map<SpannerRpc.Option, Object> options;

    @Test
    public void writeAtLeastOnce() throws ParseException {
        String timestampString = "2015-10-01T10:54:20.021Z";
        ArgumentCaptor<CommitRequest> commit = ArgumentCaptor.forClass(CommitRequest.class);
        CommitResponse response = CommitResponse.newBuilder().setCommitTimestamp(Timestamps.parse(timestampString)).build();
        Mockito.when(rpc.commit(commit.capture(), Mockito.eq(options))).thenReturn(response);
        Timestamp timestamp = session.writeAtLeastOnce(Arrays.asList(Mutation.newInsertBuilder("T").set("C").to("x").build()));
        assertThat(timestamp.getSeconds()).isEqualTo(SessionImplTest.utcTimeSeconds(2015, Calendar.OCTOBER, 1, 10, 54, 20));
        assertThat(timestamp.getNanos()).isEqualTo(TimeUnit.MILLISECONDS.toNanos(21));
        CommitRequest request = commit.getValue();
        assertThat(request.getSingleUseTransaction()).isNotNull();
        assertThat(request.getSingleUseTransaction().getReadWrite()).isNotNull();
        com.google.spanner.v1.Mutation mutation = com.google.spanner.v1.Mutation.newBuilder().setInsert(Write.newBuilder().setTable("T").addColumns("C").addValues(ListValue.newBuilder().addValues(com.google.protobuf.Value.newBuilder().setStringValue("x")))).build();
        assertThat(request.getMutationsList()).containsExactly(mutation);
    }

    @Test
    public void newSingleUseContextClosesOldSingleUseContext() {
        ReadContext ctx = session.singleUse(TimestampBound.strong());
        session.singleUse(TimestampBound.strong());
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("invalidated");
        ctx.read("Dummy", KeySet.all(), Arrays.asList("C"));
    }

    @Test
    public void newSingleUseContextClosesOldSingleUseReadOnlyTransactionContext() {
        ReadContext ctx = session.singleUseReadOnlyTransaction(TimestampBound.strong());
        session.singleUse(TimestampBound.strong());
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("invalidated");
        ctx.read("Dummy", KeySet.all(), Arrays.asList("C"));
    }

    @Test
    public void newSingleUseContextClosesOldMultiUseReadOnlyTransactionContext() {
        ReadContext ctx = session.singleUseReadOnlyTransaction(TimestampBound.strong());
        session.singleUse(TimestampBound.strong());
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("invalidated");
        ctx.read("Dummy", KeySet.all(), Arrays.asList("C"));
    }

    @Test
    public void newSingleUseReadOnlyTransactionContextClosesOldSingleUseContext() {
        ReadContext ctx = session.singleUse(TimestampBound.strong());
        session.singleUseReadOnlyTransaction(TimestampBound.strong());
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("invalidated");
        ctx.read("Dummy", KeySet.all(), Arrays.asList("C"));
    }

    @Test
    public void newMultiUseReadOnlyTransactionContextClosesOldSingleUseContext() {
        ReadContext ctx = session.singleUse(TimestampBound.strong());
        session.readOnlyTransaction(TimestampBound.strong());
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("invalidated");
        ctx.read("Dummy", KeySet.all(), Arrays.asList("C"));
    }

    @Test
    public void writeClosesOldSingleUseContext() throws ParseException {
        ReadContext ctx = session.singleUse(TimestampBound.strong());
        Mockito.when(rpc.commit(Mockito.<CommitRequest>any(), Mockito.eq(options))).thenReturn(CommitResponse.newBuilder().setCommitTimestamp(Timestamps.parse("2015-10-01T10:54:20.021Z")).build());
        session.writeAtLeastOnce(Arrays.<Mutation>asList());
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("invalidated");
        ctx.read("Dummy", KeySet.all(), Arrays.asList("C"));
    }

    @Test
    public void transactionClosesOldSingleUseContext() {
        ReadContext ctx = session.singleUse(TimestampBound.strong());
        // Note that we don't even run the transaction - just preparing the runner is sufficient.
        session.readWriteTransaction();
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("invalidated");
        ctx.read("Dummy", KeySet.all(), Arrays.asList("C"));
    }

    @Test
    public void singleUseContextClosesTransaction() {
        TransactionRunner runner = session.readWriteTransaction();
        session.singleUse(TimestampBound.strong());
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("invalidated");
        runner.run(new TransactionRunner.TransactionCallable<Void>() {
            @Nullable
            @Override
            public Void run(TransactionContext transaction) throws SpannerException {
                fail("Unexpected call to transaction body");
                return null;
            }
        });
    }

    @Test
    public void prepareClosesOldSingleUseContext() {
        ReadContext ctx = session.singleUse(TimestampBound.strong());
        Mockito.when(rpc.beginTransaction(Mockito.<BeginTransactionRequest>any(), Mockito.eq(options))).thenReturn(Transaction.newBuilder().setId(ByteString.copyFromUtf8("t1")).build());
        session.prepareReadWriteTransaction();
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("invalidated");
        ctx.read("Dummy", KeySet.all(), Arrays.asList("C"));
    }

    @Test
    public void singleUseReadOnlyTransactionDoesntReturnTransactionMetadata() {
        PartialResultSet resultSet = PartialResultSet.newBuilder().setMetadata(SessionImplTest.newMetadata(Type.struct(StructField.of("C", Type.string())))).build();
        mockRead(resultSet);
        ReadOnlyTransaction txn = session.singleUseReadOnlyTransaction(TimestampBound.strong());
        assertThat(txn.readRow("Dummy", Key.of(), Arrays.asList("C"))).isNull();
        // For now, getReadTimestamp() will raise an ISE because it hasn't seen a timestamp.  It would
        // be better for the read to fail with an INTERNAL error, but we can't do that until txn
        // metadata is returned for failed reads (e.g., table-not-found) as well as successful ones.
        // TODO(user): Fix this.
        expectedException.expect(IllegalStateException.class);
        txn.getReadTimestamp();
    }

    @Test
    public void singleUseReadOnlyTransactionReturnsEmptyTransactionMetadata() {
        PartialResultSet resultSet = PartialResultSet.newBuilder().setMetadata(SessionImplTest.newMetadata(Type.struct(StructField.of("C", Type.string()))).toBuilder().setTransaction(Transaction.getDefaultInstance())).build();
        mockRead(resultSet);
        ReadOnlyTransaction txn = session.singleUseReadOnlyTransaction(TimestampBound.strong());
        expectedException.expect(SpannerMatchers.isSpannerException(INTERNAL));
        txn.readRow("Dummy", Key.of(), Arrays.asList("C"));
    }

    private static class NoOpStreamingCall implements SpannerRpc.StreamingCall {
        @Override
        public void cancel(@Nullable
        String message) {
        }

        @Override
        public void request(int numMessages) {
        }
    }

    @Test
    public void multiUseReadOnlyTransactionReturnsEmptyTransactionMetadata() {
        Transaction txnMetadata = Transaction.newBuilder().setId(ByteString.copyFromUtf8("x")).build();
        PartialResultSet resultSet = PartialResultSet.newBuilder().setMetadata(SessionImplTest.newMetadata(Type.struct(StructField.of("C", Type.string())))).build();
        Mockito.when(rpc.beginTransaction(Mockito.<BeginTransactionRequest>any(), Mockito.eq(options))).thenReturn(txnMetadata);
        mockRead(resultSet);
        ReadOnlyTransaction txn = session.readOnlyTransaction(TimestampBound.strong());
        expectedException.expect(SpannerMatchers.isSpannerException(INTERNAL));
        txn.readRow("Dummy", Key.of(), Arrays.asList("C"));
    }

    @Test
    public void multiUseReadOnlyTransactionReturnsMissingTimestamp() {
        Transaction txnMetadata = Transaction.newBuilder().setId(ByteString.copyFromUtf8("x")).build();
        PartialResultSet resultSet = PartialResultSet.newBuilder().setMetadata(SessionImplTest.newMetadata(Type.struct(StructField.of("C", Type.string())))).build();
        Mockito.when(rpc.beginTransaction(Mockito.<BeginTransactionRequest>any(), Mockito.eq(options))).thenReturn(txnMetadata);
        mockRead(resultSet);
        ReadOnlyTransaction txn = session.readOnlyTransaction(TimestampBound.strong());
        expectedException.expect(SpannerMatchers.isSpannerException(INTERNAL));
        txn.readRow("Dummy", Key.of(), Arrays.asList("C"));
    }

    @Test
    public void multiUseReadOnlyTransactionReturnsMissingTransactionId() throws ParseException {
        com.google.protobuf.Timestamp t = Timestamps.parse("2015-10-01T10:54:20.021Z");
        Transaction txnMetadata = Transaction.newBuilder().setReadTimestamp(t).build();
        PartialResultSet resultSet = PartialResultSet.newBuilder().setMetadata(SessionImplTest.newMetadata(Type.struct(StructField.of("C", Type.string())))).build();
        Mockito.when(rpc.beginTransaction(Mockito.<BeginTransactionRequest>any(), Mockito.eq(options))).thenReturn(txnMetadata);
        mockRead(resultSet);
        ReadOnlyTransaction txn = session.readOnlyTransaction(TimestampBound.strong());
        expectedException.expect(SpannerMatchers.isSpannerException(INTERNAL));
        txn.readRow("Dummy", Key.of(), Arrays.asList("C"));
    }
}

