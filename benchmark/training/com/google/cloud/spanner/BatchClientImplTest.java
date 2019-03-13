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


import com.google.cloud.spanner.spi.v1.SpannerRpc;
import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;
import com.google.spanner.v1.BeginTransactionRequest;
import com.google.spanner.v1.Session;
import com.google.spanner.v1.Transaction;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Unit tests for {@link com.google.cloud.spanner.BatchClientImpl}.
 */
@RunWith(JUnit4.class)
public final class BatchClientImplTest {
    private static final String DB_NAME = "projects/my-project/instances/my-instance/databases/my-db";

    private static final String SESSION_NAME = (BatchClientImplTest.DB_NAME) + "/sessions/s1";

    private static final ByteString TXN_ID = ByteString.copyFromUtf8("my-txn");

    private static final String TIMESTAMP = "2017-11-15T10:54:20Z";

    @Mock
    private SpannerRpc gapicRpc;

    @Mock
    private SpannerOptions spannerOptions;

    @Captor
    private ArgumentCaptor<Map<SpannerRpc.Option, Object>> optionsCaptor;

    @Mock
    private BatchTransactionId txnID;

    private BatchClient client;

    @Test
    public void testBatchReadOnlyTxnWithBound() throws Exception {
        Session sessionProto = Session.newBuilder().setName(BatchClientImplTest.SESSION_NAME).build();
        Mockito.when(gapicRpc.createSession(ArgumentMatchers.eq(BatchClientImplTest.DB_NAME), ((Map<String, String>) (ArgumentMatchers.anyMap())), optionsCaptor.capture())).thenReturn(sessionProto);
        Timestamp timestamp = Timestamps.parse(BatchClientImplTest.TIMESTAMP);
        Transaction txnMetadata = Transaction.newBuilder().setId(BatchClientImplTest.TXN_ID).setReadTimestamp(timestamp).build();
        Mockito.when(spannerOptions.getSpannerRpcV1()).thenReturn(gapicRpc);
        Mockito.when(gapicRpc.beginTransaction(Mockito.<BeginTransactionRequest>any(), optionsCaptor.capture())).thenReturn(txnMetadata);
        BatchReadOnlyTransaction batchTxn = client.batchReadOnlyTransaction(TimestampBound.strong());
        assertThat(batchTxn.getBatchTransactionId().getSessionId()).isEqualTo(BatchClientImplTest.SESSION_NAME);
        assertThat(batchTxn.getBatchTransactionId().getTransactionId()).isEqualTo(BatchClientImplTest.TXN_ID);
        com.google.cloud.Timestamp t = com.google.cloud.Timestamp.parseTimestamp(BatchClientImplTest.TIMESTAMP);
        assertThat(batchTxn.getReadTimestamp()).isEqualTo(t);
        assertThat(batchTxn.getReadTimestamp()).isEqualTo(batchTxn.getBatchTransactionId().getTimestamp());
    }

    @Test
    public void testBatchReadOnlyTxnWithTxnId() throws Exception {
        Mockito.when(txnID.getSessionId()).thenReturn(BatchClientImplTest.SESSION_NAME);
        Mockito.when(txnID.getTransactionId()).thenReturn(BatchClientImplTest.TXN_ID);
        com.google.cloud.Timestamp t = com.google.cloud.Timestamp.parseTimestamp(BatchClientImplTest.TIMESTAMP);
        Mockito.when(txnID.getTimestamp()).thenReturn(t);
        BatchReadOnlyTransaction batchTxn = client.batchReadOnlyTransaction(txnID);
        assertThat(batchTxn.getBatchTransactionId().getSessionId()).isEqualTo(BatchClientImplTest.SESSION_NAME);
        assertThat(batchTxn.getBatchTransactionId().getTransactionId()).isEqualTo(BatchClientImplTest.TXN_ID);
        assertThat(batchTxn.getReadTimestamp()).isEqualTo(t);
        assertThat(batchTxn.getReadTimestamp()).isEqualTo(batchTxn.getBatchTransactionId().getTimestamp());
    }
}

