/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.clients.producer.internals;


import CoordinatorType.GROUP;
import CoordinatorType.TRANSACTION;
import Errors.CONCURRENT_TRANSACTIONS;
import Errors.COORDINATOR_LOAD_IN_PROGRESS;
import Errors.COORDINATOR_NOT_AVAILABLE;
import Errors.GROUP_AUTHORIZATION_FAILED;
import Errors.INVALID_PRODUCER_EPOCH;
import Errors.NONE;
import Errors.NOT_COORDINATOR;
import Errors.NOT_LEADER_FOR_PARTITION;
import Errors.OPERATION_NOT_ATTEMPTED;
import Errors.OUT_OF_ORDER_SEQUENCE_NUMBER;
import Errors.REQUEST_TIMED_OUT;
import Errors.TOPIC_AUTHORIZATION_FAILED;
import Errors.TRANSACTIONAL_ID_AUTHORIZATION_FAILED;
import Errors.UNKNOWN_SERVER_ERROR;
import Errors.UNKNOWN_TOPIC_OR_PARTITION;
import Errors.UNSUPPORTED_FOR_MESSAGE_FORMAT;
import Record.EMPTY_HEADERS;
import RecordBatch.NO_PRODUCER_EPOCH;
import TransactionManager.TxnRequestHandler;
import TransactionResult.ABORT;
import TransactionResult.COMMIT;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.apache.kafka.clients.AbstractRequest;
import org.apache.kafka.clients.ApiVersions;
import org.apache.kafka.clients.MockClient;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.GroupAuthorizationException;
import org.apache.kafka.common.errors.OutOfOrderSequenceException;
import org.apache.kafka.common.errors.TimeoutException;
import org.apache.kafka.common.errors.TopicAuthorizationException;
import org.apache.kafka.common.errors.TransactionalIdAuthorizationException;
import org.apache.kafka.common.errors.UnsupportedForMessageFormatException;
import org.apache.kafka.common.errors.UnsupportedVersionException;
import org.apache.kafka.common.internals.ClusterResourceListeners;
import org.apache.kafka.common.protocol.Errors;
import org.apache.kafka.common.requests.FindCoordinatorRequest;
import org.apache.kafka.common.requests.InitProducerIdRequest;
import org.apache.kafka.common.utils.LogContext;
import org.apache.kafka.common.utils.MockTime;
import org.junit.Assert;
import org.junit.Test;


public class TransactionManagerTest {
    private static final int MAX_REQUEST_SIZE = 1024 * 1024;

    private static final short ACKS_ALL = -1;

    private static final int MAX_RETRIES = Integer.MAX_VALUE;

    private static final String CLIENT_ID = "clientId";

    private static final int MAX_BLOCK_TIMEOUT = 1000;

    private static final int REQUEST_TIMEOUT = 1000;

    private static final long DEFAULT_RETRY_BACKOFF_MS = 100L;

    private final String transactionalId = "foobar";

    private final int transactionTimeoutMs = 1121;

    private final String topic = "test";

    private TopicPartition tp0 = new TopicPartition(topic, 0);

    private TopicPartition tp1 = new TopicPartition(topic, 1);

    private MockTime time = new MockTime();

    private ProducerMetadata metadata = new ProducerMetadata(0, Long.MAX_VALUE, new LogContext(), new ClusterResourceListeners(), time);

    private MockClient client = new MockClient(time, metadata);

    private ApiVersions apiVersions = new ApiVersions();

    private RecordAccumulator accumulator = null;

    private Sender sender = null;

    private TransactionManager transactionManager = null;

    private Node brokerNode = null;

    private final LogContext logContext = new LogContext();

    @Test
    public void testSenderShutdownWithPendingAddPartitions() throws Exception {
        long pid = 13131L;
        short epoch = 1;
        doInitTransactions(pid, epoch);
        transactionManager.beginTransaction();
        transactionManager.maybeAddPartitionToTransaction(tp0);
        FutureRecordMetadata sendFuture = accumulator.append(tp0, time.milliseconds(), "key".getBytes(), "value".getBytes(), EMPTY_HEADERS, null, TransactionManagerTest.MAX_BLOCK_TIMEOUT).future;
        prepareAddPartitionsToTxn(tp0, NONE);
        prepareProduceResponse(NONE, pid, epoch);
        sender.initiateClose();
        sender.run();
        Assert.assertTrue(sendFuture.isDone());
    }

    @Test
    public void testEndTxnNotSentIfIncompleteBatches() {
        long pid = 13131L;
        short epoch = 1;
        doInitTransactions(pid, epoch);
        transactionManager.beginTransaction();
        transactionManager.maybeAddPartitionToTransaction(tp0);
        prepareAddPartitionsToTxn(tp0, NONE);
        sender.run(time.milliseconds());
        Assert.assertTrue(transactionManager.isPartitionAdded(tp0));
        transactionManager.beginCommit();
        Assert.assertNull(transactionManager.nextRequestHandler(true));
        Assert.assertTrue(transactionManager.nextRequestHandler(false).isEndTxn());
    }

    @Test(expected = IllegalStateException.class)
    public void testFailIfNotReadyForSendNoProducerId() {
        transactionManager.failIfNotReadyForSend();
    }

    @Test
    public void testFailIfNotReadyForSendIdempotentProducer() {
        TransactionManager idempotentTransactionManager = new TransactionManager();
        idempotentTransactionManager.failIfNotReadyForSend();
    }

    @Test(expected = KafkaException.class)
    public void testFailIfNotReadyForSendIdempotentProducerFatalError() {
        TransactionManager idempotentTransactionManager = new TransactionManager();
        idempotentTransactionManager.transitionToFatalError(new KafkaException());
        idempotentTransactionManager.failIfNotReadyForSend();
    }

    @Test(expected = IllegalStateException.class)
    public void testFailIfNotReadyForSendNoOngoingTransaction() {
        long pid = 13131L;
        short epoch = 1;
        doInitTransactions(pid, epoch);
        transactionManager.failIfNotReadyForSend();
    }

    @Test(expected = KafkaException.class)
    public void testFailIfNotReadyForSendAfterAbortableError() {
        long pid = 13131L;
        short epoch = 1;
        doInitTransactions(pid, epoch);
        transactionManager.beginTransaction();
        transactionManager.transitionToAbortableError(new KafkaException());
        transactionManager.failIfNotReadyForSend();
    }

    @Test(expected = KafkaException.class)
    public void testFailIfNotReadyForSendAfterFatalError() {
        long pid = 13131L;
        short epoch = 1;
        doInitTransactions(pid, epoch);
        transactionManager.transitionToFatalError(new KafkaException());
        transactionManager.failIfNotReadyForSend();
    }

    @Test
    public void testHasOngoingTransactionSuccessfulAbort() {
        long pid = 13131L;
        short epoch = 1;
        TopicPartition partition = new TopicPartition("foo", 0);
        Assert.assertFalse(transactionManager.hasOngoingTransaction());
        doInitTransactions(pid, epoch);
        Assert.assertFalse(transactionManager.hasOngoingTransaction());
        transactionManager.beginTransaction();
        Assert.assertTrue(transactionManager.hasOngoingTransaction());
        transactionManager.maybeAddPartitionToTransaction(partition);
        Assert.assertTrue(transactionManager.hasOngoingTransaction());
        prepareAddPartitionsToTxn(partition, NONE);
        sender.run(time.milliseconds());
        transactionManager.beginAbort();
        Assert.assertTrue(transactionManager.hasOngoingTransaction());
        prepareEndTxnResponse(NONE, ABORT, pid, epoch);
        sender.run(time.milliseconds());
        Assert.assertFalse(transactionManager.hasOngoingTransaction());
    }

    @Test
    public void testHasOngoingTransactionSuccessfulCommit() {
        long pid = 13131L;
        short epoch = 1;
        TopicPartition partition = new TopicPartition("foo", 0);
        Assert.assertFalse(transactionManager.hasOngoingTransaction());
        doInitTransactions(pid, epoch);
        Assert.assertFalse(transactionManager.hasOngoingTransaction());
        transactionManager.beginTransaction();
        Assert.assertTrue(transactionManager.hasOngoingTransaction());
        transactionManager.maybeAddPartitionToTransaction(partition);
        Assert.assertTrue(transactionManager.hasOngoingTransaction());
        prepareAddPartitionsToTxn(partition, NONE);
        sender.run(time.milliseconds());
        transactionManager.beginCommit();
        Assert.assertTrue(transactionManager.hasOngoingTransaction());
        prepareEndTxnResponse(NONE, COMMIT, pid, epoch);
        sender.run(time.milliseconds());
        Assert.assertFalse(transactionManager.hasOngoingTransaction());
    }

    @Test
    public void testHasOngoingTransactionAbortableError() {
        long pid = 13131L;
        short epoch = 1;
        TopicPartition partition = new TopicPartition("foo", 0);
        Assert.assertFalse(transactionManager.hasOngoingTransaction());
        doInitTransactions(pid, epoch);
        Assert.assertFalse(transactionManager.hasOngoingTransaction());
        transactionManager.beginTransaction();
        Assert.assertTrue(transactionManager.hasOngoingTransaction());
        transactionManager.maybeAddPartitionToTransaction(partition);
        Assert.assertTrue(transactionManager.hasOngoingTransaction());
        prepareAddPartitionsToTxn(partition, NONE);
        sender.run(time.milliseconds());
        transactionManager.transitionToAbortableError(new KafkaException());
        Assert.assertTrue(transactionManager.hasOngoingTransaction());
        transactionManager.beginAbort();
        Assert.assertTrue(transactionManager.hasOngoingTransaction());
        prepareEndTxnResponse(NONE, ABORT, pid, epoch);
        sender.run(time.milliseconds());
        Assert.assertFalse(transactionManager.hasOngoingTransaction());
    }

    @Test
    public void testHasOngoingTransactionFatalError() {
        long pid = 13131L;
        short epoch = 1;
        TopicPartition partition = new TopicPartition("foo", 0);
        Assert.assertFalse(transactionManager.hasOngoingTransaction());
        doInitTransactions(pid, epoch);
        Assert.assertFalse(transactionManager.hasOngoingTransaction());
        transactionManager.beginTransaction();
        Assert.assertTrue(transactionManager.hasOngoingTransaction());
        transactionManager.maybeAddPartitionToTransaction(partition);
        Assert.assertTrue(transactionManager.hasOngoingTransaction());
        prepareAddPartitionsToTxn(partition, NONE);
        sender.run(time.milliseconds());
        transactionManager.transitionToFatalError(new KafkaException());
        Assert.assertFalse(transactionManager.hasOngoingTransaction());
    }

    @Test
    public void testMaybeAddPartitionToTransaction() {
        long pid = 13131L;
        short epoch = 1;
        TopicPartition partition = new TopicPartition("foo", 0);
        doInitTransactions(pid, epoch);
        transactionManager.beginTransaction();
        transactionManager.maybeAddPartitionToTransaction(partition);
        Assert.assertTrue(transactionManager.hasPartitionsToAdd());
        Assert.assertFalse(transactionManager.isPartitionAdded(partition));
        Assert.assertTrue(transactionManager.isPartitionPendingAdd(partition));
        prepareAddPartitionsToTxn(partition, NONE);
        sender.run(time.milliseconds());
        Assert.assertFalse(transactionManager.hasPartitionsToAdd());
        Assert.assertTrue(transactionManager.isPartitionAdded(partition));
        Assert.assertFalse(transactionManager.isPartitionPendingAdd(partition));
        // adding the partition again should not have any effect
        transactionManager.maybeAddPartitionToTransaction(partition);
        Assert.assertFalse(transactionManager.hasPartitionsToAdd());
        Assert.assertTrue(transactionManager.isPartitionAdded(partition));
        Assert.assertFalse(transactionManager.isPartitionPendingAdd(partition));
    }

    @Test
    public void testAddPartitionToTransactionOverridesRetryBackoffForConcurrentTransactions() {
        long pid = 13131L;
        short epoch = 1;
        TopicPartition partition = new TopicPartition("foo", 0);
        doInitTransactions(pid, epoch);
        transactionManager.beginTransaction();
        transactionManager.maybeAddPartitionToTransaction(partition);
        Assert.assertTrue(transactionManager.hasPartitionsToAdd());
        Assert.assertFalse(transactionManager.isPartitionAdded(partition));
        Assert.assertTrue(transactionManager.isPartitionPendingAdd(partition));
        prepareAddPartitionsToTxn(partition, CONCURRENT_TRANSACTIONS);
        sender.run(time.milliseconds());
        TransactionManager.TxnRequestHandler handler = transactionManager.nextRequestHandler(false);
        Assert.assertNotNull(handler);
        Assert.assertEquals(20, handler.retryBackoffMs());
    }

    @Test
    public void testAddPartitionToTransactionRetainsRetryBackoffForRegularRetriableError() {
        long pid = 13131L;
        short epoch = 1;
        TopicPartition partition = new TopicPartition("foo", 0);
        doInitTransactions(pid, epoch);
        transactionManager.beginTransaction();
        transactionManager.maybeAddPartitionToTransaction(partition);
        Assert.assertTrue(transactionManager.hasPartitionsToAdd());
        Assert.assertFalse(transactionManager.isPartitionAdded(partition));
        Assert.assertTrue(transactionManager.isPartitionPendingAdd(partition));
        prepareAddPartitionsToTxn(partition, COORDINATOR_NOT_AVAILABLE);
        sender.run(time.milliseconds());
        TransactionManager.TxnRequestHandler handler = transactionManager.nextRequestHandler(false);
        Assert.assertNotNull(handler);
        Assert.assertEquals(TransactionManagerTest.DEFAULT_RETRY_BACKOFF_MS, handler.retryBackoffMs());
    }

    @Test
    public void testAddPartitionToTransactionRetainsRetryBackoffWhenPartitionsAlreadyAdded() {
        long pid = 13131L;
        short epoch = 1;
        TopicPartition partition = new TopicPartition("foo", 0);
        doInitTransactions(pid, epoch);
        transactionManager.beginTransaction();
        transactionManager.maybeAddPartitionToTransaction(partition);
        Assert.assertTrue(transactionManager.hasPartitionsToAdd());
        Assert.assertFalse(transactionManager.isPartitionAdded(partition));
        Assert.assertTrue(transactionManager.isPartitionPendingAdd(partition));
        prepareAddPartitionsToTxn(partition, NONE);
        sender.run(time.milliseconds());
        Assert.assertTrue(transactionManager.isPartitionAdded(partition));
        TopicPartition otherPartition = new TopicPartition("foo", 1);
        transactionManager.maybeAddPartitionToTransaction(otherPartition);
        prepareAddPartitionsToTxn(otherPartition, CONCURRENT_TRANSACTIONS);
        TransactionManager.TxnRequestHandler handler = transactionManager.nextRequestHandler(false);
        Assert.assertNotNull(handler);
        Assert.assertEquals(TransactionManagerTest.DEFAULT_RETRY_BACKOFF_MS, handler.retryBackoffMs());
    }

    @Test(expected = IllegalStateException.class)
    public void testMaybeAddPartitionToTransactionBeforeInitTransactions() {
        transactionManager.maybeAddPartitionToTransaction(new TopicPartition("foo", 0));
    }

    @Test(expected = IllegalStateException.class)
    public void testMaybeAddPartitionToTransactionBeforeBeginTransaction() {
        long pid = 13131L;
        short epoch = 1;
        doInitTransactions(pid, epoch);
        transactionManager.maybeAddPartitionToTransaction(new TopicPartition("foo", 0));
    }

    @Test(expected = KafkaException.class)
    public void testMaybeAddPartitionToTransactionAfterAbortableError() {
        long pid = 13131L;
        short epoch = 1;
        doInitTransactions(pid, epoch);
        transactionManager.beginTransaction();
        transactionManager.transitionToAbortableError(new KafkaException());
        transactionManager.maybeAddPartitionToTransaction(new TopicPartition("foo", 0));
    }

    @Test(expected = KafkaException.class)
    public void testMaybeAddPartitionToTransactionAfterFatalError() {
        long pid = 13131L;
        short epoch = 1;
        doInitTransactions(pid, epoch);
        transactionManager.transitionToFatalError(new KafkaException());
        transactionManager.maybeAddPartitionToTransaction(new TopicPartition("foo", 0));
    }

    @Test
    public void testIsSendToPartitionAllowedWithPendingPartitionAfterAbortableError() {
        final long pid = 13131L;
        final short epoch = 1;
        doInitTransactions(pid, epoch);
        transactionManager.beginTransaction();
        transactionManager.maybeAddPartitionToTransaction(tp0);
        transactionManager.transitionToAbortableError(new KafkaException());
        Assert.assertFalse(transactionManager.isSendToPartitionAllowed(tp0));
        Assert.assertTrue(transactionManager.hasAbortableError());
    }

    @Test
    public void testIsSendToPartitionAllowedWithInFlightPartitionAddAfterAbortableError() {
        final long pid = 13131L;
        final short epoch = 1;
        doInitTransactions(pid, epoch);
        transactionManager.beginTransaction();
        transactionManager.maybeAddPartitionToTransaction(tp0);
        // Send the AddPartitionsToTxn request and leave it in-flight
        sender.run(time.milliseconds());
        transactionManager.transitionToAbortableError(new KafkaException());
        Assert.assertFalse(transactionManager.isSendToPartitionAllowed(tp0));
        Assert.assertTrue(transactionManager.hasAbortableError());
    }

    @Test
    public void testIsSendToPartitionAllowedWithPendingPartitionAfterFatalError() {
        final long pid = 13131L;
        final short epoch = 1;
        doInitTransactions(pid, epoch);
        transactionManager.beginTransaction();
        transactionManager.maybeAddPartitionToTransaction(tp0);
        transactionManager.transitionToFatalError(new KafkaException());
        Assert.assertFalse(transactionManager.isSendToPartitionAllowed(tp0));
        Assert.assertTrue(transactionManager.hasFatalError());
    }

    @Test
    public void testIsSendToPartitionAllowedWithInFlightPartitionAddAfterFatalError() {
        final long pid = 13131L;
        final short epoch = 1;
        doInitTransactions(pid, epoch);
        transactionManager.beginTransaction();
        transactionManager.maybeAddPartitionToTransaction(tp0);
        // Send the AddPartitionsToTxn request and leave it in-flight
        sender.run(time.milliseconds());
        transactionManager.transitionToFatalError(new KafkaException());
        Assert.assertFalse(transactionManager.isSendToPartitionAllowed(tp0));
        Assert.assertTrue(transactionManager.hasFatalError());
    }

    @Test
    public void testIsSendToPartitionAllowedWithAddedPartitionAfterAbortableError() {
        final long pid = 13131L;
        final short epoch = 1;
        doInitTransactions(pid, epoch);
        transactionManager.beginTransaction();
        transactionManager.maybeAddPartitionToTransaction(tp0);
        prepareAddPartitionsToTxnResponse(NONE, tp0, epoch, pid);
        sender.run(time.milliseconds());
        Assert.assertFalse(transactionManager.hasPartitionsToAdd());
        transactionManager.transitionToAbortableError(new KafkaException());
        Assert.assertTrue(transactionManager.isSendToPartitionAllowed(tp0));
        Assert.assertTrue(transactionManager.hasAbortableError());
    }

    @Test
    public void testIsSendToPartitionAllowedWithAddedPartitionAfterFatalError() {
        final long pid = 13131L;
        final short epoch = 1;
        doInitTransactions(pid, epoch);
        transactionManager.beginTransaction();
        transactionManager.maybeAddPartitionToTransaction(tp0);
        prepareAddPartitionsToTxnResponse(NONE, tp0, epoch, pid);
        sender.run(time.milliseconds());
        Assert.assertFalse(transactionManager.hasPartitionsToAdd());
        transactionManager.transitionToFatalError(new KafkaException());
        Assert.assertFalse(transactionManager.isSendToPartitionAllowed(tp0));
        Assert.assertTrue(transactionManager.hasFatalError());
    }

    @Test
    public void testIsSendToPartitionAllowedWithPartitionNotAdded() {
        final long pid = 13131L;
        final short epoch = 1;
        doInitTransactions(pid, epoch);
        transactionManager.beginTransaction();
        Assert.assertFalse(transactionManager.isSendToPartitionAllowed(tp0));
    }

    @Test
    public void testDefaultSequenceNumber() {
        TransactionManager transactionManager = new TransactionManager();
        Assert.assertEquals(((int) (transactionManager.sequenceNumber(tp0))), 0);
        transactionManager.incrementSequenceNumber(tp0, 3);
        Assert.assertEquals(((int) (transactionManager.sequenceNumber(tp0))), 3);
    }

    @Test
    public void testSequenceNumberOverflow() {
        TransactionManager transactionManager = new TransactionManager();
        Assert.assertEquals(((int) (transactionManager.sequenceNumber(tp0))), 0);
        transactionManager.incrementSequenceNumber(tp0, Integer.MAX_VALUE);
        Assert.assertEquals(((int) (transactionManager.sequenceNumber(tp0))), Integer.MAX_VALUE);
        transactionManager.incrementSequenceNumber(tp0, 100);
        Assert.assertEquals(((int) (transactionManager.sequenceNumber(tp0))), 99);
        transactionManager.incrementSequenceNumber(tp0, Integer.MAX_VALUE);
        Assert.assertEquals(((int) (transactionManager.sequenceNumber(tp0))), 98);
    }

    @Test
    public void testProducerIdReset() {
        TransactionManager transactionManager = new TransactionManager();
        Assert.assertEquals(((int) (transactionManager.sequenceNumber(tp0))), 0);
        transactionManager.incrementSequenceNumber(tp0, 3);
        Assert.assertEquals(((int) (transactionManager.sequenceNumber(tp0))), 3);
        transactionManager.resetProducerId();
        Assert.assertEquals(((int) (transactionManager.sequenceNumber(tp0))), 0);
    }

    @Test
    public void testBasicTransaction() throws InterruptedException {
        final long pid = 13131L;
        final short epoch = 1;
        doInitTransactions(pid, epoch);
        transactionManager.beginTransaction();
        transactionManager.maybeAddPartitionToTransaction(tp0);
        Future<RecordMetadata> responseFuture = accumulator.append(tp0, time.milliseconds(), "key".getBytes(), "value".getBytes(), EMPTY_HEADERS, null, TransactionManagerTest.MAX_BLOCK_TIMEOUT).future;
        Assert.assertFalse(responseFuture.isDone());
        prepareAddPartitionsToTxnResponse(NONE, tp0, epoch, pid);
        prepareProduceResponse(NONE, pid, epoch);
        Assert.assertFalse(transactionManager.transactionContainsPartition(tp0));
        Assert.assertFalse(transactionManager.isSendToPartitionAllowed(tp0));
        sender.run(time.milliseconds());// send addPartitions.

        // Check that only addPartitions was sent.
        Assert.assertTrue(transactionManager.transactionContainsPartition(tp0));
        Assert.assertTrue(transactionManager.isSendToPartitionAllowed(tp0));
        Assert.assertFalse(responseFuture.isDone());
        sender.run(time.milliseconds());// send produce request.

        Assert.assertTrue(responseFuture.isDone());
        Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
        offsets.put(tp1, new OffsetAndMetadata(1));
        final String consumerGroupId = "myconsumergroup";
        TransactionalRequestResult addOffsetsResult = transactionManager.sendOffsetsToTransaction(offsets, consumerGroupId);
        Assert.assertFalse(transactionManager.hasPendingOffsetCommits());
        prepareAddOffsetsToTxnResponse(NONE, consumerGroupId, pid, epoch);
        sender.run(time.milliseconds());// Send AddOffsetsRequest

        Assert.assertTrue(transactionManager.hasPendingOffsetCommits());// We should now have created and queued the offset commit request.

        Assert.assertFalse(addOffsetsResult.isCompleted());// the result doesn't complete until TxnOffsetCommit returns

        Map<TopicPartition, Errors> txnOffsetCommitResponse = new HashMap<>();
        txnOffsetCommitResponse.put(tp1, NONE);
        prepareFindCoordinatorResponse(NONE, false, GROUP, consumerGroupId);
        prepareTxnOffsetCommitResponse(consumerGroupId, pid, epoch, txnOffsetCommitResponse);
        Assert.assertNull(transactionManager.coordinator(GROUP));
        sender.run(time.milliseconds());// try to send TxnOffsetCommitRequest, but find we don't have a group coordinator.

        sender.run(time.milliseconds());// send find coordinator for group request

        Assert.assertNotNull(transactionManager.coordinator(GROUP));
        Assert.assertTrue(transactionManager.hasPendingOffsetCommits());
        sender.run(time.milliseconds());// send TxnOffsetCommitRequest commit.

        Assert.assertFalse(transactionManager.hasPendingOffsetCommits());
        Assert.assertTrue(addOffsetsResult.isCompleted());// We should only be done after both RPCs complete.

        transactionManager.beginCommit();
        prepareEndTxnResponse(NONE, COMMIT, pid, epoch);
        sender.run(time.milliseconds());// commit.

        Assert.assertFalse(transactionManager.hasOngoingTransaction());
        Assert.assertFalse(transactionManager.isCompleting());
        Assert.assertFalse(transactionManager.transactionContainsPartition(tp0));
    }

    @Test
    public void testDisconnectAndRetry() {
        // This is called from the initTransactions method in the producer as the first order of business.
        // It finds the coordinator and then gets a PID.
        transactionManager.initializeTransactions();
        prepareFindCoordinatorResponse(NONE, true, TRANSACTION, transactionalId);
        sender.run(time.milliseconds());// find coordinator, connection lost.

        prepareFindCoordinatorResponse(NONE, false, TRANSACTION, transactionalId);
        sender.run(time.milliseconds());// find coordinator

        sender.run(time.milliseconds());
        Assert.assertEquals(brokerNode, transactionManager.coordinator(TRANSACTION));
    }

    @Test
    public void testUnsupportedFindCoordinator() {
        transactionManager.initializeTransactions();
        client.prepareUnsupportedVersionResponse(( body) -> {
            FindCoordinatorRequest findCoordinatorRequest = ((FindCoordinatorRequest) (body));
            Assert.assertEquals(findCoordinatorRequest.coordinatorType(), TRANSACTION);
            Assert.assertEquals(findCoordinatorRequest.coordinatorKey(), transactionalId);
            return true;
        });
        sender.run(time.milliseconds());// InitProducerRequest is queued

        sender.run(time.milliseconds());// FindCoordinator is queued after peeking InitProducerRequest

        Assert.assertTrue(transactionManager.hasFatalError());
        Assert.assertTrue(((transactionManager.lastError()) instanceof UnsupportedVersionException));
    }

    @Test
    public void testUnsupportedInitTransactions() {
        transactionManager.initializeTransactions();
        prepareFindCoordinatorResponse(NONE, false, TRANSACTION, transactionalId);
        sender.run(time.milliseconds());// InitProducerRequest is queued

        sender.run(time.milliseconds());// FindCoordinator is queued after peeking InitProducerRequest

        Assert.assertFalse(transactionManager.hasError());
        Assert.assertNotNull(transactionManager.coordinator(TRANSACTION));
        client.prepareUnsupportedVersionResponse(( body) -> {
            InitProducerIdRequest initProducerIdRequest = ((InitProducerIdRequest) (body));
            Assert.assertEquals(initProducerIdRequest.transactionalId(), transactionalId);
            Assert.assertEquals(initProducerIdRequest.transactionTimeoutMs(), transactionTimeoutMs);
            return true;
        });
        sender.run(time.milliseconds());// InitProducerRequest is dequeued

        Assert.assertTrue(transactionManager.hasFatalError());
        Assert.assertTrue(((transactionManager.lastError()) instanceof UnsupportedVersionException));
    }

    @Test
    public void testUnsupportedForMessageFormatInTxnOffsetCommit() {
        final String consumerGroupId = "consumer";
        final long pid = 13131L;
        final short epoch = 1;
        final TopicPartition tp = new TopicPartition("foo", 0);
        doInitTransactions(pid, epoch);
        transactionManager.beginTransaction();
        TransactionalRequestResult sendOffsetsResult = transactionManager.sendOffsetsToTransaction(Collections.singletonMap(tp, new OffsetAndMetadata(39L)), consumerGroupId);
        prepareAddOffsetsToTxnResponse(NONE, consumerGroupId, pid, epoch);
        sender.run(time.milliseconds());// AddOffsetsToTxn Handled, TxnOffsetCommit Enqueued

        sender.run(time.milliseconds());// FindCoordinator Enqueued

        prepareFindCoordinatorResponse(NONE, false, GROUP, consumerGroupId);
        sender.run(time.milliseconds());// FindCoordinator Returned

        prepareTxnOffsetCommitResponse(consumerGroupId, pid, epoch, Collections.singletonMap(tp, UNSUPPORTED_FOR_MESSAGE_FORMAT));
        sender.run(time.milliseconds());// TxnOffsetCommit Handled

        Assert.assertTrue(transactionManager.hasError());
        Assert.assertTrue(((transactionManager.lastError()) instanceof UnsupportedForMessageFormatException));
        Assert.assertTrue(sendOffsetsResult.isCompleted());
        Assert.assertFalse(sendOffsetsResult.isSuccessful());
        Assert.assertTrue(((sendOffsetsResult.error()) instanceof UnsupportedForMessageFormatException));
        assertFatalError(UnsupportedForMessageFormatException.class);
    }

    @Test
    public void testLookupCoordinatorOnDisconnectAfterSend() {
        // This is called from the initTransactions method in the producer as the first order of business.
        // It finds the coordinator and then gets a PID.
        final long pid = 13131L;
        final short epoch = 1;
        TransactionalRequestResult initPidResult = transactionManager.initializeTransactions();
        prepareFindCoordinatorResponse(NONE, false, TRANSACTION, transactionalId);
        sender.run(time.milliseconds());// find coordinator

        sender.run(time.milliseconds());
        Assert.assertEquals(brokerNode, transactionManager.coordinator(TRANSACTION));
        prepareInitPidResponse(NONE, true, pid, epoch);
        // send pid to coordinator, should get disconnected before receiving the response, and resend the
        // FindCoordinator and InitPid requests.
        sender.run(time.milliseconds());
        Assert.assertNull(transactionManager.coordinator(TRANSACTION));
        Assert.assertFalse(initPidResult.isCompleted());
        Assert.assertFalse(transactionManager.hasProducerId());
        prepareFindCoordinatorResponse(NONE, false, TRANSACTION, transactionalId);
        sender.run(time.milliseconds());
        Assert.assertEquals(brokerNode, transactionManager.coordinator(TRANSACTION));
        Assert.assertFalse(initPidResult.isCompleted());
        prepareInitPidResponse(NONE, false, pid, epoch);
        sender.run(time.milliseconds());// get pid and epoch

        Assert.assertTrue(initPidResult.isCompleted());// The future should only return after the second round of retries succeed.

        Assert.assertTrue(transactionManager.hasProducerId());
        Assert.assertEquals(pid, transactionManager.producerIdAndEpoch().producerId);
        Assert.assertEquals(epoch, transactionManager.producerIdAndEpoch().epoch);
    }

    @Test
    public void testLookupCoordinatorOnDisconnectBeforeSend() {
        // This is called from the initTransactions method in the producer as the first order of business.
        // It finds the coordinator and then gets a PID.
        final long pid = 13131L;
        final short epoch = 1;
        TransactionalRequestResult initPidResult = transactionManager.initializeTransactions();
        prepareFindCoordinatorResponse(NONE, false, TRANSACTION, transactionalId);
        sender.run(time.milliseconds());// one loop to realize we need a coordinator.

        sender.run(time.milliseconds());// next loop to find coordintor.

        Assert.assertEquals(brokerNode, transactionManager.coordinator(TRANSACTION));
        client.disconnect(brokerNode.idString());
        client.blackout(brokerNode, 100);
        // send pid to coordinator. Should get disconnected before the send and resend the FindCoordinator
        // and InitPid requests.
        sender.run(time.milliseconds());
        time.sleep(110);// waiting for the blackout period for the node to expire.

        Assert.assertNull(transactionManager.coordinator(TRANSACTION));
        Assert.assertFalse(initPidResult.isCompleted());
        Assert.assertFalse(transactionManager.hasProducerId());
        prepareFindCoordinatorResponse(NONE, false, TRANSACTION, transactionalId);
        sender.run(time.milliseconds());
        Assert.assertEquals(brokerNode, transactionManager.coordinator(TRANSACTION));
        Assert.assertFalse(initPidResult.isCompleted());
        prepareInitPidResponse(NONE, false, pid, epoch);
        sender.run(time.milliseconds());// get pid and epoch

        Assert.assertTrue(initPidResult.isCompleted());// The future should only return after the second round of retries succeed.

        Assert.assertTrue(transactionManager.hasProducerId());
        Assert.assertEquals(pid, transactionManager.producerIdAndEpoch().producerId);
        Assert.assertEquals(epoch, transactionManager.producerIdAndEpoch().epoch);
    }

    @Test
    public void testLookupCoordinatorOnNotCoordinatorError() {
        // This is called from the initTransactions method in the producer as the first order of business.
        // It finds the coordinator and then gets a PID.
        final long pid = 13131L;
        final short epoch = 1;
        TransactionalRequestResult initPidResult = transactionManager.initializeTransactions();
        prepareFindCoordinatorResponse(NONE, false, TRANSACTION, transactionalId);
        sender.run(time.milliseconds());// find coordinator

        sender.run(time.milliseconds());
        Assert.assertEquals(brokerNode, transactionManager.coordinator(TRANSACTION));
        prepareInitPidResponse(NOT_COORDINATOR, false, pid, epoch);
        sender.run(time.milliseconds());// send pid, get not coordinator. Should resend the FindCoordinator and InitPid requests

        Assert.assertNull(transactionManager.coordinator(TRANSACTION));
        Assert.assertFalse(initPidResult.isCompleted());
        Assert.assertFalse(transactionManager.hasProducerId());
        prepareFindCoordinatorResponse(NONE, false, TRANSACTION, transactionalId);
        sender.run(time.milliseconds());
        Assert.assertEquals(brokerNode, transactionManager.coordinator(TRANSACTION));
        Assert.assertFalse(initPidResult.isCompleted());
        prepareInitPidResponse(NONE, false, pid, epoch);
        sender.run(time.milliseconds());// get pid and epoch

        Assert.assertTrue(initPidResult.isCompleted());// The future should only return after the second round of retries succeed.

        Assert.assertTrue(transactionManager.hasProducerId());
        Assert.assertEquals(pid, transactionManager.producerIdAndEpoch().producerId);
        Assert.assertEquals(epoch, transactionManager.producerIdAndEpoch().epoch);
    }

    @Test
    public void testTransactionalIdAuthorizationFailureInFindCoordinator() {
        TransactionalRequestResult initPidResult = transactionManager.initializeTransactions();
        prepareFindCoordinatorResponse(TRANSACTIONAL_ID_AUTHORIZATION_FAILED, false, TRANSACTION, transactionalId);
        sender.run(time.milliseconds());// find coordinator

        sender.run(time.milliseconds());
        Assert.assertTrue(transactionManager.hasError());
        Assert.assertTrue(((transactionManager.lastError()) instanceof TransactionalIdAuthorizationException));
        sender.run(time.milliseconds());// one more run to fail the InitProducerId future

        Assert.assertTrue(initPidResult.isCompleted());
        Assert.assertFalse(initPidResult.isSuccessful());
        Assert.assertTrue(((initPidResult.error()) instanceof TransactionalIdAuthorizationException));
        assertFatalError(TransactionalIdAuthorizationException.class);
    }

    @Test
    public void testTransactionalIdAuthorizationFailureInInitProducerId() {
        final long pid = 13131L;
        TransactionalRequestResult initPidResult = transactionManager.initializeTransactions();
        prepareFindCoordinatorResponse(NONE, false, TRANSACTION, transactionalId);
        sender.run(time.milliseconds());// find coordinator

        sender.run(time.milliseconds());
        Assert.assertEquals(brokerNode, transactionManager.coordinator(TRANSACTION));
        prepareInitPidResponse(TRANSACTIONAL_ID_AUTHORIZATION_FAILED, false, pid, NO_PRODUCER_EPOCH);
        sender.run(time.milliseconds());
        Assert.assertTrue(transactionManager.hasError());
        Assert.assertTrue(initPidResult.isCompleted());
        Assert.assertFalse(initPidResult.isSuccessful());
        Assert.assertTrue(((initPidResult.error()) instanceof TransactionalIdAuthorizationException));
        assertFatalError(TransactionalIdAuthorizationException.class);
    }

    @Test
    public void testGroupAuthorizationFailureInFindCoordinator() {
        final String consumerGroupId = "consumer";
        final long pid = 13131L;
        final short epoch = 1;
        doInitTransactions(pid, epoch);
        transactionManager.beginTransaction();
        TransactionalRequestResult sendOffsetsResult = transactionManager.sendOffsetsToTransaction(Collections.singletonMap(new TopicPartition("foo", 0), new OffsetAndMetadata(39L)), consumerGroupId);
        prepareAddOffsetsToTxnResponse(NONE, consumerGroupId, pid, epoch);
        sender.run(time.milliseconds());// AddOffsetsToTxn Handled, TxnOffsetCommit Enqueued

        sender.run(time.milliseconds());// FindCoordinator Enqueued

        prepareFindCoordinatorResponse(GROUP_AUTHORIZATION_FAILED, false, GROUP, consumerGroupId);
        sender.run(time.milliseconds());// FindCoordinator Failed

        sender.run(time.milliseconds());// TxnOffsetCommit Aborted

        Assert.assertTrue(transactionManager.hasError());
        Assert.assertTrue(((transactionManager.lastError()) instanceof GroupAuthorizationException));
        Assert.assertTrue(sendOffsetsResult.isCompleted());
        Assert.assertFalse(sendOffsetsResult.isSuccessful());
        Assert.assertTrue(((sendOffsetsResult.error()) instanceof GroupAuthorizationException));
        GroupAuthorizationException exception = ((GroupAuthorizationException) (sendOffsetsResult.error()));
        Assert.assertEquals(consumerGroupId, exception.groupId());
        assertAbortableError(GroupAuthorizationException.class);
    }

    @Test
    public void testGroupAuthorizationFailureInTxnOffsetCommit() {
        final String consumerGroupId = "consumer";
        final long pid = 13131L;
        final short epoch = 1;
        final TopicPartition tp1 = new TopicPartition("foo", 0);
        doInitTransactions(pid, epoch);
        transactionManager.beginTransaction();
        TransactionalRequestResult sendOffsetsResult = transactionManager.sendOffsetsToTransaction(Collections.singletonMap(tp1, new OffsetAndMetadata(39L)), consumerGroupId);
        prepareAddOffsetsToTxnResponse(NONE, consumerGroupId, pid, epoch);
        sender.run(time.milliseconds());// AddOffsetsToTxn Handled, TxnOffsetCommit Enqueued

        sender.run(time.milliseconds());// FindCoordinator Enqueued

        prepareFindCoordinatorResponse(NONE, false, GROUP, consumerGroupId);
        sender.run(time.milliseconds());// FindCoordinator Returned

        prepareTxnOffsetCommitResponse(consumerGroupId, pid, epoch, Collections.singletonMap(tp1, GROUP_AUTHORIZATION_FAILED));
        sender.run(time.milliseconds());// TxnOffsetCommit Handled

        Assert.assertTrue(transactionManager.hasError());
        Assert.assertTrue(((transactionManager.lastError()) instanceof GroupAuthorizationException));
        Assert.assertTrue(sendOffsetsResult.isCompleted());
        Assert.assertFalse(sendOffsetsResult.isSuccessful());
        Assert.assertTrue(((sendOffsetsResult.error()) instanceof GroupAuthorizationException));
        Assert.assertFalse(transactionManager.hasPendingOffsetCommits());
        GroupAuthorizationException exception = ((GroupAuthorizationException) (sendOffsetsResult.error()));
        Assert.assertEquals(consumerGroupId, exception.groupId());
        assertAbortableError(GroupAuthorizationException.class);
    }

    @Test
    public void testTransactionalIdAuthorizationFailureInAddOffsetsToTxn() {
        final String consumerGroupId = "consumer";
        final long pid = 13131L;
        final short epoch = 1;
        final TopicPartition tp = new TopicPartition("foo", 0);
        doInitTransactions(pid, epoch);
        transactionManager.beginTransaction();
        TransactionalRequestResult sendOffsetsResult = transactionManager.sendOffsetsToTransaction(Collections.singletonMap(tp, new OffsetAndMetadata(39L)), consumerGroupId);
        prepareAddOffsetsToTxnResponse(TRANSACTIONAL_ID_AUTHORIZATION_FAILED, consumerGroupId, pid, epoch);
        sender.run(time.milliseconds());// AddOffsetsToTxn Handled

        Assert.assertTrue(transactionManager.hasError());
        Assert.assertTrue(((transactionManager.lastError()) instanceof TransactionalIdAuthorizationException));
        Assert.assertTrue(sendOffsetsResult.isCompleted());
        Assert.assertFalse(sendOffsetsResult.isSuccessful());
        Assert.assertTrue(((sendOffsetsResult.error()) instanceof TransactionalIdAuthorizationException));
        assertFatalError(TransactionalIdAuthorizationException.class);
    }

    @Test
    public void testTransactionalIdAuthorizationFailureInTxnOffsetCommit() {
        final String consumerGroupId = "consumer";
        final long pid = 13131L;
        final short epoch = 1;
        final TopicPartition tp = new TopicPartition("foo", 0);
        doInitTransactions(pid, epoch);
        transactionManager.beginTransaction();
        TransactionalRequestResult sendOffsetsResult = transactionManager.sendOffsetsToTransaction(Collections.singletonMap(tp, new OffsetAndMetadata(39L)), consumerGroupId);
        prepareAddOffsetsToTxnResponse(NONE, consumerGroupId, pid, epoch);
        sender.run(time.milliseconds());// AddOffsetsToTxn Handled, TxnOffsetCommit Enqueued

        sender.run(time.milliseconds());// FindCoordinator Enqueued

        prepareFindCoordinatorResponse(NONE, false, GROUP, consumerGroupId);
        sender.run(time.milliseconds());// FindCoordinator Returned

        prepareTxnOffsetCommitResponse(consumerGroupId, pid, epoch, Collections.singletonMap(tp, TRANSACTIONAL_ID_AUTHORIZATION_FAILED));
        sender.run(time.milliseconds());// TxnOffsetCommit Handled

        Assert.assertTrue(transactionManager.hasError());
        Assert.assertTrue(((transactionManager.lastError()) instanceof TransactionalIdAuthorizationException));
        Assert.assertTrue(sendOffsetsResult.isCompleted());
        Assert.assertFalse(sendOffsetsResult.isSuccessful());
        Assert.assertTrue(((sendOffsetsResult.error()) instanceof TransactionalIdAuthorizationException));
        assertFatalError(TransactionalIdAuthorizationException.class);
    }

    @Test
    public void testTopicAuthorizationFailureInAddPartitions() {
        final long pid = 13131L;
        final short epoch = 1;
        final TopicPartition tp0 = new TopicPartition("foo", 0);
        final TopicPartition tp1 = new TopicPartition("bar", 0);
        doInitTransactions(pid, epoch);
        transactionManager.beginTransaction();
        transactionManager.maybeAddPartitionToTransaction(tp0);
        transactionManager.maybeAddPartitionToTransaction(tp1);
        Map<TopicPartition, Errors> errors = new HashMap<>();
        errors.put(tp0, TOPIC_AUTHORIZATION_FAILED);
        errors.put(tp1, OPERATION_NOT_ATTEMPTED);
        prepareAddPartitionsToTxn(errors);
        sender.run(time.milliseconds());
        Assert.assertTrue(transactionManager.hasError());
        Assert.assertTrue(((transactionManager.lastError()) instanceof TopicAuthorizationException));
        Assert.assertFalse(transactionManager.isPartitionPendingAdd(tp0));
        Assert.assertFalse(transactionManager.isPartitionPendingAdd(tp1));
        Assert.assertFalse(transactionManager.isPartitionAdded(tp0));
        Assert.assertFalse(transactionManager.isPartitionAdded(tp1));
        Assert.assertFalse(transactionManager.hasPartitionsToAdd());
        TopicAuthorizationException exception = ((TopicAuthorizationException) (transactionManager.lastError()));
        Assert.assertEquals(Collections.singleton(tp0.topic()), exception.unauthorizedTopics());
        assertAbortableError(TopicAuthorizationException.class);
    }

    @Test
    public void testRecoveryFromAbortableErrorTransactionNotStarted() throws Exception {
        final long pid = 13131L;
        final short epoch = 1;
        final TopicPartition unauthorizedPartition = new TopicPartition("foo", 0);
        doInitTransactions(pid, epoch);
        transactionManager.beginTransaction();
        transactionManager.maybeAddPartitionToTransaction(unauthorizedPartition);
        Future<RecordMetadata> responseFuture = accumulator.append(unauthorizedPartition, time.milliseconds(), "key".getBytes(), "value".getBytes(), EMPTY_HEADERS, null, TransactionManagerTest.MAX_BLOCK_TIMEOUT).future;
        prepareAddPartitionsToTxn(Collections.singletonMap(unauthorizedPartition, TOPIC_AUTHORIZATION_FAILED));
        sender.run(time.milliseconds());
        Assert.assertTrue(transactionManager.hasAbortableError());
        transactionManager.beginAbort();
        sender.run(time.milliseconds());
        Assert.assertTrue(responseFuture.isDone());
        assertFutureFailed(responseFuture);
        // No partitions added, so no need to prepare EndTxn response
        sender.run(time.milliseconds());
        Assert.assertTrue(transactionManager.isReady());
        Assert.assertFalse(transactionManager.hasPartitionsToAdd());
        Assert.assertFalse(accumulator.hasIncomplete());
        // ensure we can now start a new transaction
        transactionManager.beginTransaction();
        transactionManager.maybeAddPartitionToTransaction(tp0);
        responseFuture = accumulator.append(tp0, time.milliseconds(), "key".getBytes(), "value".getBytes(), EMPTY_HEADERS, null, TransactionManagerTest.MAX_BLOCK_TIMEOUT).future;
        prepareAddPartitionsToTxn(Collections.singletonMap(tp0, NONE));
        sender.run(time.milliseconds());
        Assert.assertTrue(transactionManager.isPartitionAdded(tp0));
        Assert.assertFalse(transactionManager.hasPartitionsToAdd());
        transactionManager.beginCommit();
        prepareProduceResponse(NONE, pid, epoch);
        sender.run(time.milliseconds());
        Assert.assertTrue(responseFuture.isDone());
        Assert.assertNotNull(responseFuture.get());
        prepareEndTxnResponse(NONE, COMMIT, pid, epoch);
        sender.run(time.milliseconds());
        Assert.assertTrue(transactionManager.isReady());
    }

    @Test
    public void testRecoveryFromAbortableErrorTransactionStarted() throws Exception {
        final long pid = 13131L;
        final short epoch = 1;
        final TopicPartition unauthorizedPartition = new TopicPartition("foo", 0);
        doInitTransactions(pid, epoch);
        transactionManager.beginTransaction();
        transactionManager.maybeAddPartitionToTransaction(tp0);
        prepareAddPartitionsToTxn(tp0, NONE);
        Future<RecordMetadata> authorizedTopicProduceFuture = accumulator.append(unauthorizedPartition, time.milliseconds(), "key".getBytes(), "value".getBytes(), EMPTY_HEADERS, null, TransactionManagerTest.MAX_BLOCK_TIMEOUT).future;
        sender.run(time.milliseconds());
        Assert.assertTrue(transactionManager.isPartitionAdded(tp0));
        transactionManager.maybeAddPartitionToTransaction(unauthorizedPartition);
        Future<RecordMetadata> unauthorizedTopicProduceFuture = accumulator.append(unauthorizedPartition, time.milliseconds(), "key".getBytes(), "value".getBytes(), EMPTY_HEADERS, null, TransactionManagerTest.MAX_BLOCK_TIMEOUT).future;
        prepareAddPartitionsToTxn(Collections.singletonMap(unauthorizedPartition, TOPIC_AUTHORIZATION_FAILED));
        sender.run(time.milliseconds());
        Assert.assertTrue(transactionManager.hasAbortableError());
        Assert.assertTrue(transactionManager.isPartitionAdded(tp0));
        Assert.assertFalse(transactionManager.isPartitionAdded(unauthorizedPartition));
        Assert.assertFalse(authorizedTopicProduceFuture.isDone());
        Assert.assertFalse(unauthorizedTopicProduceFuture.isDone());
        prepareEndTxnResponse(NONE, ABORT, pid, epoch);
        transactionManager.beginAbort();
        sender.run(time.milliseconds());
        // neither produce request has been sent, so they should both be failed immediately
        assertFutureFailed(authorizedTopicProduceFuture);
        assertFutureFailed(unauthorizedTopicProduceFuture);
        Assert.assertTrue(transactionManager.isReady());
        Assert.assertFalse(transactionManager.hasPartitionsToAdd());
        Assert.assertFalse(accumulator.hasIncomplete());
        // ensure we can now start a new transaction
        transactionManager.beginTransaction();
        transactionManager.maybeAddPartitionToTransaction(tp0);
        FutureRecordMetadata nextTransactionFuture = accumulator.append(tp0, time.milliseconds(), "key".getBytes(), "value".getBytes(), EMPTY_HEADERS, null, TransactionManagerTest.MAX_BLOCK_TIMEOUT).future;
        prepareAddPartitionsToTxn(Collections.singletonMap(tp0, NONE));
        sender.run(time.milliseconds());
        Assert.assertTrue(transactionManager.isPartitionAdded(tp0));
        Assert.assertFalse(transactionManager.hasPartitionsToAdd());
        transactionManager.beginCommit();
        prepareProduceResponse(NONE, pid, epoch);
        sender.run(time.milliseconds());
        Assert.assertTrue(nextTransactionFuture.isDone());
        Assert.assertNotNull(nextTransactionFuture.get());
        prepareEndTxnResponse(NONE, COMMIT, pid, epoch);
        sender.run(time.milliseconds());
        Assert.assertTrue(transactionManager.isReady());
    }

    @Test
    public void testRecoveryFromAbortableErrorProduceRequestInRetry() throws Exception {
        final long pid = 13131L;
        final short epoch = 1;
        final TopicPartition unauthorizedPartition = new TopicPartition("foo", 0);
        doInitTransactions(pid, epoch);
        transactionManager.beginTransaction();
        transactionManager.maybeAddPartitionToTransaction(tp0);
        prepareAddPartitionsToTxn(tp0, NONE);
        Future<RecordMetadata> authorizedTopicProduceFuture = accumulator.append(tp0, time.milliseconds(), "key".getBytes(), "value".getBytes(), EMPTY_HEADERS, null, TransactionManagerTest.MAX_BLOCK_TIMEOUT).future;
        sender.run(time.milliseconds());
        Assert.assertTrue(transactionManager.isPartitionAdded(tp0));
        accumulator.beginFlush();
        prepareProduceResponse(REQUEST_TIMED_OUT, pid, epoch);
        sender.run(time.milliseconds());
        Assert.assertFalse(authorizedTopicProduceFuture.isDone());
        Assert.assertTrue(accumulator.hasIncomplete());
        transactionManager.maybeAddPartitionToTransaction(unauthorizedPartition);
        Future<RecordMetadata> unauthorizedTopicProduceFuture = accumulator.append(unauthorizedPartition, time.milliseconds(), "key".getBytes(), "value".getBytes(), EMPTY_HEADERS, null, TransactionManagerTest.MAX_BLOCK_TIMEOUT).future;
        prepareAddPartitionsToTxn(Collections.singletonMap(unauthorizedPartition, TOPIC_AUTHORIZATION_FAILED));
        sender.run(time.milliseconds());
        Assert.assertTrue(transactionManager.hasAbortableError());
        Assert.assertTrue(transactionManager.isPartitionAdded(tp0));
        Assert.assertFalse(transactionManager.isPartitionAdded(unauthorizedPartition));
        Assert.assertFalse(authorizedTopicProduceFuture.isDone());
        prepareProduceResponse(NONE, pid, epoch);
        sender.run(time.milliseconds());
        assertFutureFailed(unauthorizedTopicProduceFuture);
        Assert.assertTrue(authorizedTopicProduceFuture.isDone());
        Assert.assertNotNull(authorizedTopicProduceFuture.get());
        Assert.assertTrue(authorizedTopicProduceFuture.isDone());
        prepareEndTxnResponse(NONE, ABORT, pid, epoch);
        transactionManager.beginAbort();
        sender.run(time.milliseconds());
        // neither produce request has been sent, so they should both be failed immediately
        Assert.assertTrue(transactionManager.isReady());
        Assert.assertFalse(transactionManager.hasPartitionsToAdd());
        Assert.assertFalse(accumulator.hasIncomplete());
        // ensure we can now start a new transaction
        transactionManager.beginTransaction();
        transactionManager.maybeAddPartitionToTransaction(tp0);
        FutureRecordMetadata nextTransactionFuture = accumulator.append(tp0, time.milliseconds(), "key".getBytes(), "value".getBytes(), EMPTY_HEADERS, null, TransactionManagerTest.MAX_BLOCK_TIMEOUT).future;
        prepareAddPartitionsToTxn(Collections.singletonMap(tp0, NONE));
        sender.run(time.milliseconds());
        Assert.assertTrue(transactionManager.isPartitionAdded(tp0));
        Assert.assertFalse(transactionManager.hasPartitionsToAdd());
        transactionManager.beginCommit();
        prepareProduceResponse(NONE, pid, epoch);
        sender.run(time.milliseconds());
        Assert.assertTrue(nextTransactionFuture.isDone());
        Assert.assertNotNull(nextTransactionFuture.get());
        prepareEndTxnResponse(NONE, COMMIT, pid, epoch);
        sender.run(time.milliseconds());
        Assert.assertTrue(transactionManager.isReady());
    }

    @Test
    public void testTransactionalIdAuthorizationFailureInAddPartitions() {
        final long pid = 13131L;
        final short epoch = 1;
        final TopicPartition tp = new TopicPartition("foo", 0);
        doInitTransactions(pid, epoch);
        transactionManager.beginTransaction();
        transactionManager.maybeAddPartitionToTransaction(tp);
        prepareAddPartitionsToTxn(tp, TRANSACTIONAL_ID_AUTHORIZATION_FAILED);
        sender.run(time.milliseconds());
        Assert.assertTrue(transactionManager.hasError());
        Assert.assertTrue(((transactionManager.lastError()) instanceof TransactionalIdAuthorizationException));
        assertFatalError(TransactionalIdAuthorizationException.class);
    }

    @Test
    public void testFlushPendingPartitionsOnCommit() throws InterruptedException {
        final long pid = 13131L;
        final short epoch = 1;
        doInitTransactions(pid, epoch);
        transactionManager.beginTransaction();
        transactionManager.maybeAddPartitionToTransaction(tp0);
        Future<RecordMetadata> responseFuture = accumulator.append(tp0, time.milliseconds(), "key".getBytes(), "value".getBytes(), EMPTY_HEADERS, null, TransactionManagerTest.MAX_BLOCK_TIMEOUT).future;
        Assert.assertFalse(responseFuture.isDone());
        TransactionalRequestResult commitResult = transactionManager.beginCommit();
        // we have an append, an add partitions request, and now also an endtxn.
        // The order should be:
        // 1. Add Partitions
        // 2. Produce
        // 3. EndTxn.
        Assert.assertFalse(transactionManager.transactionContainsPartition(tp0));
        prepareAddPartitionsToTxnResponse(NONE, tp0, epoch, pid);
        sender.run(time.milliseconds());// AddPartitions.

        Assert.assertTrue(transactionManager.transactionContainsPartition(tp0));
        Assert.assertFalse(responseFuture.isDone());
        Assert.assertFalse(commitResult.isCompleted());
        prepareProduceResponse(NONE, pid, epoch);
        sender.run(time.milliseconds());// Produce.

        Assert.assertTrue(responseFuture.isDone());
        prepareEndTxnResponse(NONE, COMMIT, pid, epoch);
        Assert.assertFalse(commitResult.isCompleted());
        Assert.assertTrue(transactionManager.hasOngoingTransaction());
        Assert.assertTrue(transactionManager.isCompleting());
        sender.run(time.milliseconds());
        Assert.assertTrue(commitResult.isCompleted());
        Assert.assertFalse(transactionManager.hasOngoingTransaction());
    }

    @Test
    public void testMultipleAddPartitionsPerForOneProduce() throws InterruptedException {
        final long pid = 13131L;
        final short epoch = 1;
        doInitTransactions(pid, epoch);
        transactionManager.beginTransaction();
        // User does one producer.sed
        transactionManager.maybeAddPartitionToTransaction(tp0);
        Future<RecordMetadata> responseFuture = accumulator.append(tp0, time.milliseconds(), "key".getBytes(), "value".getBytes(), EMPTY_HEADERS, null, TransactionManagerTest.MAX_BLOCK_TIMEOUT).future;
        Assert.assertFalse(responseFuture.isDone());
        prepareAddPartitionsToTxnResponse(NONE, tp0, epoch, pid);
        Assert.assertFalse(transactionManager.transactionContainsPartition(tp0));
        // Sender flushes one add partitions. The produce goes next.
        sender.run(time.milliseconds());// send addPartitions.

        // Check that only addPartitions was sent.
        Assert.assertTrue(transactionManager.transactionContainsPartition(tp0));
        // In the mean time, the user does a second produce to a different partition
        transactionManager.maybeAddPartitionToTransaction(tp1);
        Future<RecordMetadata> secondResponseFuture = accumulator.append(tp0, time.milliseconds(), "key".getBytes(), "value".getBytes(), EMPTY_HEADERS, null, TransactionManagerTest.MAX_BLOCK_TIMEOUT).future;
        prepareAddPartitionsToTxnResponse(NONE, tp1, epoch, pid);
        prepareProduceResponse(NONE, pid, epoch);
        Assert.assertFalse(transactionManager.transactionContainsPartition(tp1));
        Assert.assertFalse(responseFuture.isDone());
        Assert.assertFalse(secondResponseFuture.isDone());
        // The second add partitions should go out here.
        sender.run(time.milliseconds());// send second add partitions request

        Assert.assertTrue(transactionManager.transactionContainsPartition(tp1));
        Assert.assertFalse(responseFuture.isDone());
        Assert.assertFalse(secondResponseFuture.isDone());
        // Finally we get to the produce.
        sender.run(time.milliseconds());// send produce request

        Assert.assertTrue(responseFuture.isDone());
        Assert.assertTrue(secondResponseFuture.isDone());
    }

    @Test(expected = ExecutionException.class)
    public void testProducerFencedException() throws InterruptedException, ExecutionException {
        final long pid = 13131L;
        final short epoch = 1;
        doInitTransactions(pid, epoch);
        transactionManager.beginTransaction();
        transactionManager.maybeAddPartitionToTransaction(tp0);
        Future<RecordMetadata> responseFuture = accumulator.append(tp0, time.milliseconds(), "key".getBytes(), "value".getBytes(), EMPTY_HEADERS, null, TransactionManagerTest.MAX_BLOCK_TIMEOUT).future;
        Assert.assertFalse(responseFuture.isDone());
        prepareAddPartitionsToTxnResponse(NONE, tp0, epoch, pid);
        prepareProduceResponse(INVALID_PRODUCER_EPOCH, pid, epoch);
        sender.run(time.milliseconds());// Add partitions.

        sender.run(time.milliseconds());// send produce.

        Assert.assertTrue(responseFuture.isDone());
        Assert.assertTrue(transactionManager.hasError());
        responseFuture.get();
    }

    @Test
    public void testDisallowCommitOnProduceFailure() throws InterruptedException {
        final long pid = 13131L;
        final short epoch = 1;
        doInitTransactions(pid, epoch);
        transactionManager.beginTransaction();
        transactionManager.maybeAddPartitionToTransaction(tp0);
        Future<RecordMetadata> responseFuture = accumulator.append(tp0, time.milliseconds(), "key".getBytes(), "value".getBytes(), EMPTY_HEADERS, null, TransactionManagerTest.MAX_BLOCK_TIMEOUT).future;
        TransactionalRequestResult commitResult = transactionManager.beginCommit();
        Assert.assertFalse(responseFuture.isDone());
        prepareAddPartitionsToTxnResponse(NONE, tp0, epoch, pid);
        prepareProduceResponse(OUT_OF_ORDER_SEQUENCE_NUMBER, pid, epoch);
        sender.run(time.milliseconds());// Send AddPartitionsRequest

        Assert.assertFalse(commitResult.isCompleted());
        sender.run(time.milliseconds());// Send Produce Request, returns OutOfOrderSequenceException.

        sender.run(time.milliseconds());// try to commit.

        Assert.assertTrue(commitResult.isCompleted());// commit should be cancelled with exception without being sent.

        try {
            commitResult.await();
            Assert.fail();// the get() must throw an exception.

        } catch (KafkaException e) {
            // Expected
        }
        try {
            responseFuture.get();
            Assert.fail("Expected produce future to raise an exception");
        } catch (ExecutionException e) {
            Assert.assertTrue(((e.getCause()) instanceof OutOfOrderSequenceException));
        }
        // Commit is not allowed, so let's abort and try again.
        TransactionalRequestResult abortResult = transactionManager.beginAbort();
        prepareEndTxnResponse(NONE, ABORT, pid, epoch);
        sender.run(time.milliseconds());// Send abort request. It is valid to transition from ERROR to ABORT

        Assert.assertTrue(abortResult.isCompleted());
        Assert.assertTrue(abortResult.isSuccessful());
        Assert.assertTrue(transactionManager.isReady());// make sure we are ready for a transaction now.

    }

    @Test
    public void testAllowAbortOnProduceFailure() throws InterruptedException {
        final long pid = 13131L;
        final short epoch = 1;
        doInitTransactions(pid, epoch);
        transactionManager.beginTransaction();
        transactionManager.maybeAddPartitionToTransaction(tp0);
        Future<RecordMetadata> responseFuture = accumulator.append(tp0, time.milliseconds(), "key".getBytes(), "value".getBytes(), EMPTY_HEADERS, null, TransactionManagerTest.MAX_BLOCK_TIMEOUT).future;
        Assert.assertFalse(responseFuture.isDone());
        prepareAddPartitionsToTxnResponse(NONE, tp0, epoch, pid);
        prepareProduceResponse(OUT_OF_ORDER_SEQUENCE_NUMBER, pid, epoch);
        prepareEndTxnResponse(NONE, ABORT, pid, epoch);
        sender.run(time.milliseconds());// Send AddPartitionsRequest

        sender.run(time.milliseconds());// Send Produce Request, returns OutOfOrderSequenceException.

        TransactionalRequestResult abortResult = transactionManager.beginAbort();
        sender.run(time.milliseconds());// try to abort

        Assert.assertTrue(abortResult.isCompleted());
        Assert.assertTrue(abortResult.isSuccessful());
        Assert.assertTrue(transactionManager.isReady());// make sure we are ready for a transaction now.

    }

    @Test
    public void testAbortableErrorWhileAbortInProgress() throws InterruptedException {
        final long pid = 13131L;
        final short epoch = 1;
        doInitTransactions(pid, epoch);
        transactionManager.beginTransaction();
        transactionManager.maybeAddPartitionToTransaction(tp0);
        Future<RecordMetadata> responseFuture = accumulator.append(tp0, time.milliseconds(), "key".getBytes(), "value".getBytes(), EMPTY_HEADERS, null, TransactionManagerTest.MAX_BLOCK_TIMEOUT).future;
        Assert.assertFalse(responseFuture.isDone());
        prepareAddPartitionsToTxnResponse(NONE, tp0, epoch, pid);
        sender.run(time.milliseconds());// Send AddPartitionsRequest

        sender.run(time.milliseconds());// Send Produce Request

        TransactionalRequestResult abortResult = transactionManager.beginAbort();
        Assert.assertTrue(transactionManager.isAborting());
        Assert.assertFalse(transactionManager.hasError());
        sendProduceResponse(OUT_OF_ORDER_SEQUENCE_NUMBER, pid, epoch);
        prepareEndTxnResponse(NONE, ABORT, pid, epoch);
        sender.run(time.milliseconds());// receive the produce response

        // we do not transition to ABORTABLE_ERROR since we were already aborting
        Assert.assertTrue(transactionManager.isAborting());
        Assert.assertFalse(transactionManager.hasError());
        sender.run(time.milliseconds());// handle the abort

        Assert.assertTrue(abortResult.isCompleted());
        Assert.assertTrue(abortResult.isSuccessful());
        Assert.assertTrue(transactionManager.isReady());// make sure we are ready for a transaction now.

    }

    @Test
    public void testCommitTransactionWithUnsentProduceRequest() throws Exception {
        final long pid = 13131L;
        final short epoch = 1;
        doInitTransactions(pid, epoch);
        transactionManager.beginTransaction();
        transactionManager.maybeAddPartitionToTransaction(tp0);
        Future<RecordMetadata> responseFuture = accumulator.append(tp0, time.milliseconds(), "key".getBytes(), "value".getBytes(), EMPTY_HEADERS, null, TransactionManagerTest.MAX_BLOCK_TIMEOUT).future;
        prepareAddPartitionsToTxn(tp0, NONE);
        sender.run(time.milliseconds());
        Assert.assertTrue(accumulator.hasUndrained());
        // committing the transaction should cause the unsent batch to be flushed
        transactionManager.beginCommit();
        sender.run(time.milliseconds());
        Assert.assertFalse(accumulator.hasUndrained());
        Assert.assertTrue(accumulator.hasIncomplete());
        Assert.assertFalse(transactionManager.hasInFlightTransactionalRequest());
        Assert.assertFalse(responseFuture.isDone());
        // until the produce future returns, we will not send EndTxn
        sender.run(time.milliseconds());
        Assert.assertFalse(accumulator.hasUndrained());
        Assert.assertTrue(accumulator.hasIncomplete());
        Assert.assertFalse(transactionManager.hasInFlightTransactionalRequest());
        Assert.assertFalse(responseFuture.isDone());
        // now the produce response returns
        sendProduceResponse(NONE, pid, epoch);
        sender.run(time.milliseconds());
        Assert.assertTrue(responseFuture.isDone());
        Assert.assertFalse(accumulator.hasUndrained());
        Assert.assertFalse(accumulator.hasIncomplete());
        Assert.assertFalse(transactionManager.hasInFlightTransactionalRequest());
        // now we send EndTxn
        sender.run(time.milliseconds());
        Assert.assertTrue(transactionManager.hasInFlightTransactionalRequest());
        sendEndTxnResponse(NONE, COMMIT, pid, epoch);
        sender.run(time.milliseconds());
        Assert.assertFalse(transactionManager.hasInFlightTransactionalRequest());
        Assert.assertTrue(transactionManager.isReady());
    }

    @Test
    public void testCommitTransactionWithInFlightProduceRequest() throws Exception {
        final long pid = 13131L;
        final short epoch = 1;
        doInitTransactions(pid, epoch);
        transactionManager.beginTransaction();
        transactionManager.maybeAddPartitionToTransaction(tp0);
        Future<RecordMetadata> responseFuture = accumulator.append(tp0, time.milliseconds(), "key".getBytes(), "value".getBytes(), EMPTY_HEADERS, null, TransactionManagerTest.MAX_BLOCK_TIMEOUT).future;
        prepareAddPartitionsToTxn(tp0, NONE);
        sender.run(time.milliseconds());
        Assert.assertTrue(accumulator.hasUndrained());
        accumulator.beginFlush();
        sender.run(time.milliseconds());
        Assert.assertFalse(accumulator.hasUndrained());
        Assert.assertTrue(accumulator.hasIncomplete());
        Assert.assertFalse(transactionManager.hasInFlightTransactionalRequest());
        // now we begin the commit with the produce request still pending
        transactionManager.beginCommit();
        sender.run(time.milliseconds());
        Assert.assertFalse(accumulator.hasUndrained());
        Assert.assertTrue(accumulator.hasIncomplete());
        Assert.assertFalse(transactionManager.hasInFlightTransactionalRequest());
        Assert.assertFalse(responseFuture.isDone());
        // until the produce future returns, we will not send EndTxn
        sender.run(time.milliseconds());
        Assert.assertFalse(accumulator.hasUndrained());
        Assert.assertTrue(accumulator.hasIncomplete());
        Assert.assertFalse(transactionManager.hasInFlightTransactionalRequest());
        Assert.assertFalse(responseFuture.isDone());
        // now the produce response returns
        sendProduceResponse(NONE, pid, epoch);
        sender.run(time.milliseconds());
        Assert.assertTrue(responseFuture.isDone());
        Assert.assertFalse(accumulator.hasUndrained());
        Assert.assertFalse(accumulator.hasIncomplete());
        Assert.assertFalse(transactionManager.hasInFlightTransactionalRequest());
        // now we send EndTxn
        sender.run(time.milliseconds());
        Assert.assertTrue(transactionManager.hasInFlightTransactionalRequest());
        sendEndTxnResponse(NONE, COMMIT, pid, epoch);
        sender.run(time.milliseconds());
        Assert.assertFalse(transactionManager.hasInFlightTransactionalRequest());
        Assert.assertTrue(transactionManager.isReady());
    }

    @Test
    public void testFindCoordinatorAllowedInAbortableErrorState() throws InterruptedException {
        final long pid = 13131L;
        final short epoch = 1;
        doInitTransactions(pid, epoch);
        transactionManager.beginTransaction();
        transactionManager.maybeAddPartitionToTransaction(tp0);
        Future<RecordMetadata> responseFuture = accumulator.append(tp0, time.milliseconds(), "key".getBytes(), "value".getBytes(), EMPTY_HEADERS, null, TransactionManagerTest.MAX_BLOCK_TIMEOUT).future;
        Assert.assertFalse(responseFuture.isDone());
        sender.run(time.milliseconds());// Send AddPartitionsRequest

        transactionManager.transitionToAbortableError(new KafkaException());
        sendAddPartitionsToTxnResponse(NOT_COORDINATOR, tp0, epoch, pid);
        sender.run(time.milliseconds());// AddPartitions returns

        Assert.assertTrue(transactionManager.hasAbortableError());
        Assert.assertNull(transactionManager.coordinator(TRANSACTION));
        prepareFindCoordinatorResponse(NONE, false, TRANSACTION, transactionalId);
        sender.run(time.milliseconds());// FindCoordinator handled

        Assert.assertEquals(brokerNode, transactionManager.coordinator(TRANSACTION));
        Assert.assertTrue(transactionManager.hasAbortableError());
    }

    @Test
    public void testCancelUnsentAddPartitionsAndProduceOnAbort() throws InterruptedException {
        final long pid = 13131L;
        final short epoch = 1;
        doInitTransactions(pid, epoch);
        transactionManager.beginTransaction();
        transactionManager.maybeAddPartitionToTransaction(tp0);
        Future<RecordMetadata> responseFuture = accumulator.append(tp0, time.milliseconds(), "key".getBytes(), "value".getBytes(), EMPTY_HEADERS, null, TransactionManagerTest.MAX_BLOCK_TIMEOUT).future;
        Assert.assertFalse(responseFuture.isDone());
        TransactionalRequestResult abortResult = transactionManager.beginAbort();
        // note since no partitions were added to the transaction, no EndTxn will be sent
        sender.run(time.milliseconds());// try to abort

        Assert.assertTrue(abortResult.isCompleted());
        Assert.assertTrue(abortResult.isSuccessful());
        Assert.assertTrue(transactionManager.isReady());// make sure we are ready for a transaction now.

        try {
            responseFuture.get();
            Assert.fail("Expected produce future to raise an exception");
        } catch (ExecutionException e) {
            Assert.assertTrue(((e.getCause()) instanceof KafkaException));
        }
    }

    @Test
    public void testAbortResendsAddPartitionErrorIfRetried() throws InterruptedException {
        final long producerId = 13131L;
        final short producerEpoch = 1;
        doInitTransactions(producerId, producerEpoch);
        transactionManager.beginTransaction();
        transactionManager.maybeAddPartitionToTransaction(tp0);
        prepareAddPartitionsToTxnResponse(UNKNOWN_TOPIC_OR_PARTITION, tp0, producerEpoch, producerId);
        Future<RecordMetadata> responseFuture = accumulator.append(tp0, time.milliseconds(), "key".getBytes(), "value".getBytes(), EMPTY_HEADERS, null, TransactionManagerTest.MAX_BLOCK_TIMEOUT).future;
        sender.run(time.milliseconds());// Send AddPartitions and let it fail

        Assert.assertFalse(responseFuture.isDone());
        TransactionalRequestResult abortResult = transactionManager.beginAbort();
        // we should resend the AddPartitions
        prepareAddPartitionsToTxnResponse(NONE, tp0, producerEpoch, producerId);
        prepareEndTxnResponse(NONE, ABORT, producerId, producerEpoch);
        sender.run(time.milliseconds());// Resend AddPartitions

        sender.run(time.milliseconds());// Send EndTxn

        Assert.assertTrue(abortResult.isCompleted());
        Assert.assertTrue(abortResult.isSuccessful());
        Assert.assertTrue(transactionManager.isReady());// make sure we are ready for a transaction now.

        try {
            responseFuture.get();
            Assert.fail("Expected produce future to raise an exception");
        } catch (ExecutionException e) {
            Assert.assertTrue(((e.getCause()) instanceof KafkaException));
        }
    }

    @Test
    public void testAbortResendsProduceRequestIfRetried() throws Exception {
        final long producerId = 13131L;
        final short producerEpoch = 1;
        doInitTransactions(producerId, producerEpoch);
        transactionManager.beginTransaction();
        transactionManager.maybeAddPartitionToTransaction(tp0);
        prepareAddPartitionsToTxnResponse(NONE, tp0, producerEpoch, producerId);
        prepareProduceResponse(REQUEST_TIMED_OUT, producerId, producerEpoch);
        Future<RecordMetadata> responseFuture = accumulator.append(tp0, time.milliseconds(), "key".getBytes(), "value".getBytes(), EMPTY_HEADERS, null, TransactionManagerTest.MAX_BLOCK_TIMEOUT).future;
        sender.run(time.milliseconds());// Send AddPartitions

        sender.run(time.milliseconds());// Send ProduceRequest and let it fail

        Assert.assertFalse(responseFuture.isDone());
        TransactionalRequestResult abortResult = transactionManager.beginAbort();
        // we should resend the ProduceRequest before aborting
        prepareProduceResponse(NONE, producerId, producerEpoch);
        prepareEndTxnResponse(NONE, ABORT, producerId, producerEpoch);
        sender.run(time.milliseconds());// Resend ProduceRequest

        sender.run(time.milliseconds());// Send EndTxn

        Assert.assertTrue(abortResult.isCompleted());
        Assert.assertTrue(abortResult.isSuccessful());
        Assert.assertTrue(transactionManager.isReady());// make sure we are ready for a transaction now.

        RecordMetadata recordMetadata = responseFuture.get();
        Assert.assertEquals(tp0.topic(), recordMetadata.topic());
    }

    @Test
    public void testHandlingOfUnknownTopicPartitionErrorOnAddPartitions() throws InterruptedException {
        final long pid = 13131L;
        final short epoch = 1;
        doInitTransactions(pid, epoch);
        transactionManager.beginTransaction();
        transactionManager.maybeAddPartitionToTransaction(tp0);
        Future<RecordMetadata> responseFuture = accumulator.append(tp0, time.milliseconds(), "key".getBytes(), "value".getBytes(), EMPTY_HEADERS, null, TransactionManagerTest.MAX_BLOCK_TIMEOUT).future;
        Assert.assertFalse(responseFuture.isDone());
        prepareAddPartitionsToTxnResponse(UNKNOWN_TOPIC_OR_PARTITION, tp0, epoch, pid);
        sender.run(time.milliseconds());// Send AddPartitionsRequest

        Assert.assertFalse(transactionManager.transactionContainsPartition(tp0));// The partition should not yet be added.

        prepareAddPartitionsToTxnResponse(NONE, tp0, epoch, pid);
        prepareProduceResponse(NONE, pid, epoch);
        sender.run(time.milliseconds());// Send AddPartitionsRequest successfully.

        Assert.assertTrue(transactionManager.transactionContainsPartition(tp0));
        sender.run(time.milliseconds());// Send ProduceRequest.

        Assert.assertTrue(responseFuture.isDone());
    }

    @Test
    public void testHandlingOfUnknownTopicPartitionErrorOnTxnOffsetCommit() {
        testRetriableErrorInTxnOffsetCommit(UNKNOWN_TOPIC_OR_PARTITION);
    }

    @Test
    public void testHandlingOfCoordinatorLoadingErrorOnTxnOffsetCommit() {
        testRetriableErrorInTxnOffsetCommit(COORDINATOR_LOAD_IN_PROGRESS);
    }

    @Test
    public void shouldNotAddPartitionsToTransactionWhenTopicAuthorizationFailed() throws Exception {
        verifyAddPartitionsFailsWithPartitionLevelError(TOPIC_AUTHORIZATION_FAILED);
    }

    @Test
    public void shouldNotSendAbortTxnRequestWhenOnlyAddPartitionsRequestFailed() {
        final long pid = 13131L;
        final short epoch = 1;
        doInitTransactions(pid, epoch);
        transactionManager.beginTransaction();
        transactionManager.maybeAddPartitionToTransaction(tp0);
        prepareAddPartitionsToTxnResponse(TOPIC_AUTHORIZATION_FAILED, tp0, epoch, pid);
        sender.run(time.milliseconds());// Send AddPartitionsRequest

        TransactionalRequestResult abortResult = transactionManager.beginAbort();
        Assert.assertFalse(abortResult.isCompleted());
        sender.run(time.milliseconds());
        Assert.assertTrue(abortResult.isCompleted());
        Assert.assertTrue(abortResult.isSuccessful());
    }

    @Test
    public void shouldNotSendAbortTxnRequestWhenOnlyAddOffsetsRequestFailed() {
        final long pid = 13131L;
        final short epoch = 1;
        doInitTransactions(pid, epoch);
        transactionManager.beginTransaction();
        Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
        offsets.put(tp1, new OffsetAndMetadata(1));
        final String consumerGroupId = "myconsumergroup";
        transactionManager.sendOffsetsToTransaction(offsets, consumerGroupId);
        TransactionalRequestResult abortResult = transactionManager.beginAbort();
        prepareAddOffsetsToTxnResponse(GROUP_AUTHORIZATION_FAILED, consumerGroupId, pid, epoch);
        sender.run(time.milliseconds());// Send AddOffsetsToTxnRequest

        Assert.assertFalse(abortResult.isCompleted());
        sender.run(time.milliseconds());
        Assert.assertTrue(transactionManager.isReady());
        Assert.assertTrue(abortResult.isCompleted());
        Assert.assertTrue(abortResult.isSuccessful());
    }

    @Test
    public void shouldFailAbortIfAddOffsetsFailsWithFatalError() {
        final long pid = 13131L;
        final short epoch = 1;
        doInitTransactions(pid, epoch);
        transactionManager.beginTransaction();
        Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
        offsets.put(tp1, new OffsetAndMetadata(1));
        final String consumerGroupId = "myconsumergroup";
        transactionManager.sendOffsetsToTransaction(offsets, consumerGroupId);
        TransactionalRequestResult abortResult = transactionManager.beginAbort();
        prepareAddOffsetsToTxnResponse(UNKNOWN_SERVER_ERROR, consumerGroupId, pid, epoch);
        sender.run(time.milliseconds());// Send AddOffsetsToTxnRequest

        Assert.assertFalse(abortResult.isCompleted());
        sender.run(time.milliseconds());
        Assert.assertTrue(abortResult.isCompleted());
        Assert.assertFalse(abortResult.isSuccessful());
        Assert.assertTrue(transactionManager.hasFatalError());
    }

    @Test
    public void testNoDrainWhenPartitionsPending() throws InterruptedException {
        final long pid = 13131L;
        final short epoch = 1;
        doInitTransactions(pid, epoch);
        transactionManager.beginTransaction();
        transactionManager.maybeAddPartitionToTransaction(tp0);
        accumulator.append(tp0, time.milliseconds(), "key".getBytes(), "value".getBytes(), EMPTY_HEADERS, null, TransactionManagerTest.MAX_BLOCK_TIMEOUT);
        transactionManager.maybeAddPartitionToTransaction(tp1);
        accumulator.append(tp1, time.milliseconds(), "key".getBytes(), "value".getBytes(), EMPTY_HEADERS, null, TransactionManagerTest.MAX_BLOCK_TIMEOUT);
        Assert.assertFalse(transactionManager.isSendToPartitionAllowed(tp0));
        Assert.assertFalse(transactionManager.isSendToPartitionAllowed(tp1));
        Node node1 = new Node(0, "localhost", 1111);
        Node node2 = new Node(1, "localhost", 1112);
        PartitionInfo part1 = new PartitionInfo(topic, 0, node1, null, null);
        PartitionInfo part2 = new PartitionInfo(topic, 1, node2, null, null);
        Cluster cluster = new Cluster(null, Arrays.asList(node1, node2), Arrays.asList(part1, part2), Collections.emptySet(), Collections.emptySet());
        Set<Node> nodes = new HashSet<>();
        nodes.add(node1);
        nodes.add(node2);
        Map<Integer, List<ProducerBatch>> drainedBatches = accumulator.drain(cluster, nodes, Integer.MAX_VALUE, time.milliseconds());
        // We shouldn't drain batches which haven't been added to the transaction yet.
        Assert.assertTrue(drainedBatches.containsKey(node1.id()));
        Assert.assertTrue(drainedBatches.get(node1.id()).isEmpty());
        Assert.assertTrue(drainedBatches.containsKey(node2.id()));
        Assert.assertTrue(drainedBatches.get(node2.id()).isEmpty());
        Assert.assertFalse(transactionManager.hasError());
    }

    @Test
    public void testAllowDrainInAbortableErrorState() throws InterruptedException {
        final long pid = 13131L;
        final short epoch = 1;
        doInitTransactions(pid, epoch);
        transactionManager.beginTransaction();
        transactionManager.maybeAddPartitionToTransaction(tp1);
        prepareAddPartitionsToTxn(tp1, NONE);
        sender.run(time.milliseconds());// Send AddPartitions, tp1 should be in the transaction now.

        Assert.assertTrue(transactionManager.transactionContainsPartition(tp1));
        transactionManager.maybeAddPartitionToTransaction(tp0);
        prepareAddPartitionsToTxn(tp0, TOPIC_AUTHORIZATION_FAILED);
        sender.run(time.milliseconds());// Send AddPartitions, should be in abortable state.

        Assert.assertTrue(transactionManager.hasAbortableError());
        Assert.assertTrue(transactionManager.isSendToPartitionAllowed(tp1));
        // Try to drain a message destined for tp1, it should get drained.
        Node node1 = new Node(1, "localhost", 1112);
        PartitionInfo part1 = new PartitionInfo(topic, 1, node1, null, null);
        Cluster cluster = new Cluster(null, Collections.singletonList(node1), Collections.singletonList(part1), Collections.emptySet(), Collections.emptySet());
        accumulator.append(tp1, time.milliseconds(), "key".getBytes(), "value".getBytes(), EMPTY_HEADERS, null, TransactionManagerTest.MAX_BLOCK_TIMEOUT);
        Map<Integer, List<ProducerBatch>> drainedBatches = accumulator.drain(cluster, Collections.singleton(node1), Integer.MAX_VALUE, time.milliseconds());
        // We should drain the appended record since we are in abortable state and the partition has already been
        // added to the transaction.
        Assert.assertTrue(drainedBatches.containsKey(node1.id()));
        Assert.assertEquals(1, drainedBatches.get(node1.id()).size());
        Assert.assertTrue(transactionManager.hasAbortableError());
    }

    @Test
    public void testRaiseErrorWhenNoPartitionsPendingOnDrain() throws InterruptedException {
        final long pid = 13131L;
        final short epoch = 1;
        doInitTransactions(pid, epoch);
        transactionManager.beginTransaction();
        // Don't execute transactionManager.maybeAddPartitionToTransaction(tp0). This should result in an error on drain.
        accumulator.append(tp0, time.milliseconds(), "key".getBytes(), "value".getBytes(), EMPTY_HEADERS, null, TransactionManagerTest.MAX_BLOCK_TIMEOUT);
        Node node1 = new Node(0, "localhost", 1111);
        PartitionInfo part1 = new PartitionInfo(topic, 0, node1, null, null);
        Cluster cluster = new Cluster(null, Collections.singletonList(node1), Collections.singletonList(part1), Collections.emptySet(), Collections.emptySet());
        Set<Node> nodes = new HashSet<>();
        nodes.add(node1);
        Map<Integer, List<ProducerBatch>> drainedBatches = accumulator.drain(cluster, nodes, Integer.MAX_VALUE, time.milliseconds());
        // We shouldn't drain batches which haven't been added to the transaction yet.
        Assert.assertTrue(drainedBatches.containsKey(node1.id()));
        Assert.assertTrue(drainedBatches.get(node1.id()).isEmpty());
    }

    @Test
    public void resendFailedProduceRequestAfterAbortableError() throws Exception {
        final long pid = 13131L;
        final short epoch = 1;
        doInitTransactions(pid, epoch);
        transactionManager.beginTransaction();
        transactionManager.maybeAddPartitionToTransaction(tp0);
        Future<RecordMetadata> responseFuture = accumulator.append(tp0, time.milliseconds(), "key".getBytes(), "value".getBytes(), EMPTY_HEADERS, null, TransactionManagerTest.MAX_BLOCK_TIMEOUT).future;
        prepareAddPartitionsToTxnResponse(NONE, tp0, epoch, pid);
        prepareProduceResponse(NOT_LEADER_FOR_PARTITION, pid, epoch);
        sender.run(time.milliseconds());// Add partitions

        sender.run(time.milliseconds());// Produce

        Assert.assertFalse(responseFuture.isDone());
        transactionManager.transitionToAbortableError(new KafkaException());
        prepareProduceResponse(NONE, pid, epoch);
        sender.run(time.milliseconds());
        Assert.assertTrue(responseFuture.isDone());
        Assert.assertNotNull(responseFuture.get());// should throw the exception which caused the transaction to be aborted.

    }

    @Test
    public void testTransitionToAbortableErrorOnBatchExpiry() throws InterruptedException {
        final long pid = 13131L;
        final short epoch = 1;
        doInitTransactions(pid, epoch);
        transactionManager.beginTransaction();
        transactionManager.maybeAddPartitionToTransaction(tp0);
        Future<RecordMetadata> responseFuture = accumulator.append(tp0, time.milliseconds(), "key".getBytes(), "value".getBytes(), EMPTY_HEADERS, null, TransactionManagerTest.MAX_BLOCK_TIMEOUT).future;
        Assert.assertFalse(responseFuture.isDone());
        prepareAddPartitionsToTxnResponse(NONE, tp0, epoch, pid);
        Assert.assertFalse(transactionManager.transactionContainsPartition(tp0));
        Assert.assertFalse(transactionManager.isSendToPartitionAllowed(tp0));
        sender.run(time.milliseconds());// send addPartitions.

        // Check that only addPartitions was sent.
        Assert.assertTrue(transactionManager.transactionContainsPartition(tp0));
        Assert.assertTrue(transactionManager.isSendToPartitionAllowed(tp0));
        Assert.assertFalse(responseFuture.isDone());
        // Sleep 10 seconds to make sure that the batches in the queue would be expired if they can't be drained.
        time.sleep(10000);
        // Disconnect the target node for the pending produce request. This will ensure that sender will try to
        // expire the batch.
        Node clusterNode = metadata.fetch().nodes().get(0);
        client.disconnect(clusterNode.idString());
        client.blackout(clusterNode, 100);
        sender.run(time.milliseconds());// We should try to flush the produce, but expire it instead without sending anything.

        Assert.assertTrue(responseFuture.isDone());
        try {
            // make sure the produce was expired.
            responseFuture.get();
            Assert.fail("Expected to get a TimeoutException since the queued ProducerBatch should have been expired");
        } catch (ExecutionException e) {
            Assert.assertTrue(((e.getCause()) instanceof TimeoutException));
        }
        Assert.assertTrue(transactionManager.hasAbortableError());
    }

    @Test
    public void testTransitionToAbortableErrorOnMultipleBatchExpiry() throws InterruptedException {
        final long pid = 13131L;
        final short epoch = 1;
        doInitTransactions(pid, epoch);
        transactionManager.beginTransaction();
        transactionManager.maybeAddPartitionToTransaction(tp0);
        transactionManager.maybeAddPartitionToTransaction(tp1);
        Future<RecordMetadata> firstBatchResponse = accumulator.append(tp0, time.milliseconds(), "key".getBytes(), "value".getBytes(), EMPTY_HEADERS, null, TransactionManagerTest.MAX_BLOCK_TIMEOUT).future;
        Future<RecordMetadata> secondBatchResponse = accumulator.append(tp1, time.milliseconds(), "key".getBytes(), "value".getBytes(), EMPTY_HEADERS, null, TransactionManagerTest.MAX_BLOCK_TIMEOUT).future;
        Assert.assertFalse(firstBatchResponse.isDone());
        Assert.assertFalse(secondBatchResponse.isDone());
        Map<TopicPartition, Errors> partitionErrors = new HashMap<>();
        partitionErrors.put(tp0, NONE);
        partitionErrors.put(tp1, NONE);
        prepareAddPartitionsToTxn(partitionErrors);
        Assert.assertFalse(transactionManager.transactionContainsPartition(tp0));
        Assert.assertFalse(transactionManager.isSendToPartitionAllowed(tp0));
        sender.run(time.milliseconds());// send addPartitions.

        // Check that only addPartitions was sent.
        Assert.assertTrue(transactionManager.transactionContainsPartition(tp0));
        Assert.assertTrue(transactionManager.transactionContainsPartition(tp1));
        Assert.assertTrue(transactionManager.isSendToPartitionAllowed(tp1));
        Assert.assertTrue(transactionManager.isSendToPartitionAllowed(tp1));
        Assert.assertFalse(firstBatchResponse.isDone());
        Assert.assertFalse(secondBatchResponse.isDone());
        // Sleep 10 seconds to make sure that the batches in the queue would be expired if they can't be drained.
        time.sleep(10000);
        // Disconnect the target node for the pending produce request. This will ensure that sender will try to
        // expire the batch.
        Node clusterNode = metadata.fetch().nodes().get(0);
        client.disconnect(clusterNode.idString());
        client.blackout(clusterNode, 100);
        sender.run(time.milliseconds());// We should try to flush the produce, but expire it instead without sending anything.

        Assert.assertTrue(firstBatchResponse.isDone());
        Assert.assertTrue(secondBatchResponse.isDone());
        try {
            // make sure the produce was expired.
            firstBatchResponse.get();
            Assert.fail("Expected to get a TimeoutException since the queued ProducerBatch should have been expired");
        } catch (ExecutionException e) {
            Assert.assertTrue(((e.getCause()) instanceof TimeoutException));
        }
        try {
            // make sure the produce was expired.
            secondBatchResponse.get();
            Assert.fail("Expected to get a TimeoutException since the queued ProducerBatch should have been expired");
        } catch (ExecutionException e) {
            Assert.assertTrue(((e.getCause()) instanceof TimeoutException));
        }
        Assert.assertTrue(transactionManager.hasAbortableError());
    }

    @Test
    public void testDropCommitOnBatchExpiry() throws InterruptedException {
        final long pid = 13131L;
        final short epoch = 1;
        doInitTransactions(pid, epoch);
        transactionManager.beginTransaction();
        transactionManager.maybeAddPartitionToTransaction(tp0);
        Future<RecordMetadata> responseFuture = accumulator.append(tp0, time.milliseconds(), "key".getBytes(), "value".getBytes(), EMPTY_HEADERS, null, TransactionManagerTest.MAX_BLOCK_TIMEOUT).future;
        Assert.assertFalse(responseFuture.isDone());
        prepareAddPartitionsToTxnResponse(NONE, tp0, epoch, pid);
        Assert.assertFalse(transactionManager.transactionContainsPartition(tp0));
        Assert.assertFalse(transactionManager.isSendToPartitionAllowed(tp0));
        sender.run(time.milliseconds());// send addPartitions.

        // Check that only addPartitions was sent.
        Assert.assertTrue(transactionManager.transactionContainsPartition(tp0));
        Assert.assertTrue(transactionManager.isSendToPartitionAllowed(tp0));
        Assert.assertFalse(responseFuture.isDone());
        TransactionalRequestResult commitResult = transactionManager.beginCommit();
        // Sleep 10 seconds to make sure that the batches in the queue would be expired if they can't be drained.
        time.sleep(10000);
        // Disconnect the target node for the pending produce request. This will ensure that sender will try to
        // expire the batch.
        Node clusterNode = metadata.fetch().nodes().get(0);
        client.disconnect(clusterNode.idString());
        client.blackout(clusterNode, 100);
        sender.run(time.milliseconds());// We should try to flush the produce, but expire it instead without sending anything.

        Assert.assertTrue(responseFuture.isDone());
        try {
            // make sure the produce was expired.
            responseFuture.get();
            Assert.fail("Expected to get a TimeoutException since the queued ProducerBatch should have been expired");
        } catch (ExecutionException e) {
            Assert.assertTrue(((e.getCause()) instanceof TimeoutException));
        }
        sender.run(time.milliseconds());// the commit shouldn't be completed without being sent since the produce request failed.

        Assert.assertTrue(commitResult.isCompleted());
        Assert.assertFalse(commitResult.isSuccessful());// the commit shouldn't succeed since the produce request failed.

        Assert.assertTrue(transactionManager.hasAbortableError());
        Assert.assertTrue(transactionManager.hasOngoingTransaction());
        Assert.assertFalse(transactionManager.isCompleting());
        Assert.assertTrue(transactionManager.transactionContainsPartition(tp0));
        TransactionalRequestResult abortResult = transactionManager.beginAbort();
        prepareEndTxnResponse(NONE, ABORT, pid, epoch);
        sender.run(time.milliseconds());// send the abort.

        Assert.assertTrue(abortResult.isCompleted());
        Assert.assertTrue(abortResult.isSuccessful());
        Assert.assertFalse(transactionManager.hasOngoingTransaction());
        Assert.assertFalse(transactionManager.transactionContainsPartition(tp0));
    }

    @Test
    public void testTransitionToFatalErrorWhenRetriedBatchIsExpired() throws InterruptedException {
        final long pid = 13131L;
        final short epoch = 1;
        doInitTransactions(pid, epoch);
        transactionManager.beginTransaction();
        transactionManager.maybeAddPartitionToTransaction(tp0);
        Future<RecordMetadata> responseFuture = accumulator.append(tp0, time.milliseconds(), "key".getBytes(), "value".getBytes(), EMPTY_HEADERS, null, TransactionManagerTest.MAX_BLOCK_TIMEOUT).future;
        Assert.assertFalse(responseFuture.isDone());
        prepareAddPartitionsToTxnResponse(NONE, tp0, epoch, pid);
        Assert.assertFalse(transactionManager.transactionContainsPartition(tp0));
        Assert.assertFalse(transactionManager.isSendToPartitionAllowed(tp0));
        sender.run(time.milliseconds());// send addPartitions.

        // Check that only addPartitions was sent.
        Assert.assertTrue(transactionManager.transactionContainsPartition(tp0));
        Assert.assertTrue(transactionManager.isSendToPartitionAllowed(tp0));
        prepareProduceResponse(NOT_LEADER_FOR_PARTITION, pid, epoch);
        sender.run(time.milliseconds());// send the produce request.

        Assert.assertFalse(responseFuture.isDone());
        TransactionalRequestResult commitResult = transactionManager.beginCommit();
        // Sleep 10 seconds to make sure that the batches in the queue would be expired if they can't be drained.
        time.sleep(10000);
        // Disconnect the target node for the pending produce request. This will ensure that sender will try to
        // expire the batch.
        Node clusterNode = metadata.fetch().nodes().get(0);
        client.disconnect(clusterNode.idString());
        client.blackout(clusterNode, 100);
        sender.run(time.milliseconds());// We should try to flush the produce, but expire it instead without sending anything.

        Assert.assertTrue(responseFuture.isDone());
        try {
            // make sure the produce was expired.
            responseFuture.get();
            Assert.fail("Expected to get a TimeoutException since the queued ProducerBatch should have been expired");
        } catch (ExecutionException e) {
            Assert.assertTrue(((e.getCause()) instanceof TimeoutException));
        }
        sender.run(time.milliseconds());// Transition to fatal error since we have unresolved batches.

        sender.run(time.milliseconds());// Fail the queued transactional requests

        Assert.assertTrue(commitResult.isCompleted());
        Assert.assertFalse(commitResult.isSuccessful());// the commit should have been dropped.

        Assert.assertTrue(transactionManager.hasFatalError());
        Assert.assertFalse(transactionManager.hasOngoingTransaction());
    }

    @Test
    public void testShouldResetProducerStateAfterResolvingSequences() {
        // Create a TransactionManager without a transactionalId to test
        // shouldResetProducerStateAfterResolvingSequences.
        TransactionManager manager = new TransactionManager(logContext, null, transactionTimeoutMs, TransactionManagerTest.DEFAULT_RETRY_BACKOFF_MS);
        Assert.assertFalse(manager.shouldResetProducerStateAfterResolvingSequences());
        TopicPartition tp0 = new TopicPartition("foo", 0);
        TopicPartition tp1 = new TopicPartition("foo", 1);
        Assert.assertEquals(Integer.valueOf(0), manager.sequenceNumber(tp0));
        Assert.assertEquals(Integer.valueOf(0), manager.sequenceNumber(tp1));
        manager.incrementSequenceNumber(tp0, 1);
        manager.incrementSequenceNumber(tp1, 1);
        manager.maybeUpdateLastAckedSequence(tp0, 0);
        manager.maybeUpdateLastAckedSequence(tp1, 0);
        manager.markSequenceUnresolved(tp0);
        manager.markSequenceUnresolved(tp1);
        Assert.assertFalse(manager.shouldResetProducerStateAfterResolvingSequences());
        manager.maybeUpdateLastAckedSequence(tp0, 5);
        manager.incrementSequenceNumber(tp0, 1);
        manager.markSequenceUnresolved(tp0);
        manager.markSequenceUnresolved(tp1);
        Assert.assertTrue(manager.shouldResetProducerStateAfterResolvingSequences());
    }

    @Test
    public void testRetryAbortTransaction() throws InterruptedException {
        verifyCommitOrAbortTranscationRetriable(ABORT, ABORT);
    }

    @Test
    public void testRetryCommitTransaction() throws InterruptedException {
        verifyCommitOrAbortTranscationRetriable(COMMIT, COMMIT);
    }

    @Test(expected = KafkaException.class)
    public void testRetryAbortTransactionAfterCommitTimeout() throws InterruptedException {
        verifyCommitOrAbortTranscationRetriable(COMMIT, ABORT);
    }

    @Test(expected = KafkaException.class)
    public void testRetryCommitTransactionAfterAbortTimeout() throws InterruptedException {
        verifyCommitOrAbortTranscationRetriable(ABORT, COMMIT);
    }
}

