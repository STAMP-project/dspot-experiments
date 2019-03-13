/**
 * Copyright (c) 2010-2019. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.queryhandling;


import MessageMonitor.MonitorCallback;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.axonframework.common.ReflectionUtils;
import org.axonframework.common.Registration;
import org.axonframework.common.transaction.Transaction;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.messaging.MessageHandler;
import org.axonframework.messaging.MetaData;
import org.axonframework.messaging.correlation.MessageOriginProvider;
import org.axonframework.messaging.responsetypes.ResponseType;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.monitoring.MessageMonitor;
import org.axonframework.utils.MockException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;


public class SimpleQueryBusTest {
    private static final String TRACE_ID = "traceId";

    private static final String CORRELATION_ID = "correlationId";

    private SimpleQueryBus testSubject;

    private MessageMonitor<QueryMessage<?, ?>> messageMonitor;

    private QueryInvocationErrorHandler errorHandler;

    private MonitorCallback monitorCallback;

    private ResponseType<String> singleStringResponse = ResponseTypes.instanceOf(String.class);

    @Test
    public void testSubscribe() {
        testSubject.subscribe("test", String.class, Message::getPayload);
        Assert.assertEquals(1, testSubject.getSubscriptions().size());
        Assert.assertEquals(1, testSubject.getSubscriptions().values().iterator().next().size());
        testSubject.subscribe("test", String.class, ( q) -> "aa" + (q.getPayload()));
        Assert.assertEquals(1, testSubject.getSubscriptions().size());
        Assert.assertEquals(2, testSubject.getSubscriptions().values().iterator().next().size());
        testSubject.subscribe("test2", String.class, ( q) -> "aa" + (q.getPayload()));
        Assert.assertEquals(2, testSubject.getSubscriptions().size());
    }

    @Test
    public void testSubscribingSameHandlerTwiceInvokedOnce() throws Exception {
        AtomicInteger invocationCount = new AtomicInteger();
        MessageHandler<QueryMessage<?, String>> handler = ( message) -> {
            invocationCount.incrementAndGet();
            return "reply";
        };
        Registration subscription = testSubject.subscribe("test", String.class, handler);
        testSubject.subscribe("test", String.class, handler);
        QueryMessage<String, String> testQueryMessage = new GenericQueryMessage("request", "test", singleStringResponse);
        String result = testSubject.query(testQueryMessage).thenApply(QueryResponseMessage::getPayload).get();
        Assert.assertEquals("reply", result);
        Assert.assertEquals(1, invocationCount.get());
        Assert.assertTrue(subscription.cancel());
        Assert.assertTrue(testSubject.query(testQueryMessage).isDone());
        Assert.assertTrue(testSubject.query(testQueryMessage).isCompletedExceptionally());
    }

    /* This test ensures that the QueryResponseMessage is created inside the scope of the Unit of Work, and therefore
    contains the correlation data registered with the Unit of Work
     */
    @Test
    public void testQueryResultContainsCorrelationData() throws Exception {
        testSubject.subscribe(String.class.getName(), String.class, ( q) -> (q.getPayload()) + "1234");
        QueryMessage<String, String> testQueryMessage = new GenericQueryMessage("hello", singleStringResponse).andMetaData(Collections.singletonMap(SimpleQueryBusTest.TRACE_ID, "fakeTraceId"));
        CompletableFuture<QueryResponseMessage<String>> result = testSubject.query(testQueryMessage);
        Assert.assertTrue("SimpleQueryBus should resolve CompletableFutures directly", result.isDone());
        Assert.assertEquals("hello1234", result.get().getPayload());
        Assert.assertEquals(MetaData.with(SimpleQueryBusTest.CORRELATION_ID, testQueryMessage.getIdentifier()).and(SimpleQueryBusTest.TRACE_ID, "fakeTraceId"), result.get().getMetaData());
    }

    @Test
    public void testNullResponseProperlyReturned() throws InterruptedException, ExecutionException {
        testSubject.subscribe(String.class.getName(), String.class, ( p) -> null);
        QueryMessage<String, String> testQueryMessage = new GenericQueryMessage("hello", singleStringResponse).andMetaData(Collections.singletonMap(SimpleQueryBusTest.TRACE_ID, "fakeTraceId"));
        CompletableFuture<QueryResponseMessage<String>> result = testSubject.query(testQueryMessage);
        Assert.assertTrue("SimpleQueryBus should resolve CompletableFutures directly", result.isDone());
        Assert.assertNull(result.get().getPayload());
        Assert.assertEquals(String.class, result.get().getPayloadType());
        Assert.assertEquals(MetaData.with(SimpleQueryBusTest.CORRELATION_ID, testQueryMessage.getIdentifier()).and(SimpleQueryBusTest.TRACE_ID, "fakeTraceId"), result.get().getMetaData());
    }

    @Test
    public void testQueryWithTransaction() throws Exception {
        TransactionManager mockTxManager = Mockito.mock(TransactionManager.class);
        Transaction mockTx = Mockito.mock(Transaction.class);
        Mockito.when(mockTxManager.startTransaction()).thenReturn(mockTx);
        testSubject = SimpleQueryBus.builder().transactionManager(mockTxManager).build();
        testSubject.subscribe(String.class.getName(), ReflectionUtils.methodOf(this.getClass(), "stringListQueryHandler").getGenericReturnType(), ( q) -> asList(((q.getPayload()) + "1234"), ((q.getPayload()) + "567")));
        QueryMessage<String, List<String>> testQueryMessage = new GenericQueryMessage("hello", ResponseTypes.multipleInstancesOf(String.class));
        CompletableFuture<List<String>> result = testSubject.query(testQueryMessage).thenApply(QueryResponseMessage::getPayload);
        Assert.assertTrue(result.isDone());
        List<String> completedResult = result.get();
        Assert.assertTrue(completedResult.contains("hello1234"));
        Assert.assertTrue(completedResult.contains("hello567"));
        Mockito.verify(mockTxManager).startTransaction();
        Mockito.verify(mockTx).commit();
    }

    @Test
    public void testQuerySingleWithTransaction() throws Exception {
        TransactionManager mockTxManager = Mockito.mock(TransactionManager.class);
        Transaction mockTx = Mockito.mock(Transaction.class);
        Mockito.when(mockTxManager.startTransaction()).thenReturn(mockTx);
        testSubject = SimpleQueryBus.builder().transactionManager(mockTxManager).build();
        testSubject.subscribe(String.class.getName(), String.class, ( q) -> (q.getPayload()) + "1234");
        QueryMessage<String, String> testQueryMessage = new GenericQueryMessage("hello", singleStringResponse);
        CompletableFuture<String> result = testSubject.query(testQueryMessage).thenApply(QueryResponseMessage::getPayload);
        Assert.assertEquals("hello1234", result.get());
        Mockito.verify(mockTxManager).startTransaction();
        Mockito.verify(mockTx).commit();
    }

    @Test
    public void testQueryForSingleResultWithUnsuitableHandlers() throws Exception {
        AtomicInteger invocationCount = new AtomicInteger();
        MessageHandler<? super QueryMessage<?, ?>> failingHandler = ( message) -> {
            invocationCount.incrementAndGet();
            throw new NoHandlerForQueryException("Mock");
        };
        MessageHandler<? super QueryMessage<?, String>> passingHandler = ( message) -> {
            invocationCount.incrementAndGet();
            return "reply";
        };
        testSubject.subscribe("query", String.class, failingHandler);
        // noinspection Convert2MethodRef
        testSubject.subscribe("query", String.class, ( message) -> failingHandler.handle(message));
        testSubject.subscribe("query", String.class, passingHandler);
        QueryMessage<String, String> testQueryMessage = new GenericQueryMessage("query", "query", singleStringResponse);
        CompletableFuture<String> result = testSubject.query(testQueryMessage).thenApply(QueryResponseMessage::getPayload);
        Assert.assertTrue(result.isDone());
        Assert.assertEquals("reply", result.get());
        Assert.assertEquals(3, invocationCount.get());
    }

    @Test
    public void testQueryWithOnlyUnsuitableResultsInException() throws Exception {
        testSubject.subscribe("query", String.class, ( message) -> {
            throw new NoHandlerForQueryException("Mock");
        });
        QueryMessage<String, String> testQueryMessage = new GenericQueryMessage("query", "query", singleStringResponse);
        CompletableFuture<QueryResponseMessage<String>> result = testSubject.query(testQueryMessage);
        Assert.assertTrue(result.isDone());
        Assert.assertTrue(result.isCompletedExceptionally());
        Assert.assertEquals("NoHandlerForQueryException", result.thenApply(QueryResponseMessage::getPayload).exceptionally(( e) -> e.getCause().getClass().getSimpleName()).get());
    }

    @Test
    public void testQueryReturnsResponseMessageFromHandlerAsIs() throws Exception {
        GenericQueryResponseMessage<String> soleResult = new GenericQueryResponseMessage("soleResult");
        testSubject.subscribe("query", String.class, ( message) -> soleResult);
        QueryMessage<String, String> testQueryMessage = new GenericQueryMessage("query", "query", singleStringResponse);
        CompletableFuture<QueryResponseMessage<String>> result = testSubject.query(testQueryMessage);
        Assert.assertTrue(result.isDone());
        Assert.assertSame(result.get(), soleResult);
    }

    @Test
    public void testQueryWithHandlersResultsInException() throws Exception {
        QueryMessage<String, String> testQueryMessage = new GenericQueryMessage("query", "query", singleStringResponse);
        CompletableFuture<QueryResponseMessage<String>> result = testSubject.query(testQueryMessage);
        Assert.assertTrue(result.isDone());
        Assert.assertTrue(result.isCompletedExceptionally());
        Assert.assertEquals("NoHandlerForQueryException", result.thenApply(QueryResponseMessage::getPayload).exceptionally(( e) -> e.getCause().getClass().getSimpleName()).get());
    }

    @Test
    public void testQueryForSingleResultWillReportErrors() throws Exception {
        MessageHandler<? super QueryMessage<?, ?>> failingHandler = ( message) -> {
            throw new MockException("Mock");
        };
        testSubject.subscribe("query", String.class, failingHandler);
        QueryMessage<String, String> testQueryMessage = new GenericQueryMessage("query", "query", singleStringResponse);
        CompletableFuture<QueryResponseMessage<String>> result = testSubject.query(testQueryMessage);
        Assert.assertTrue(result.isDone());
        Assert.assertFalse(result.isCompletedExceptionally());
        QueryResponseMessage<String> queryResponseMessage = result.get();
        Assert.assertTrue(queryResponseMessage.isExceptional());
        Assert.assertEquals("Mock", queryResponseMessage.exceptionResult().getMessage());
    }

    @Test
    public void testQueryWithInterceptors() throws Exception {
        testSubject.registerDispatchInterceptor(( messages) -> ( i, m) -> m.andMetaData(Collections.singletonMap("key", "value")));
        testSubject.registerHandlerInterceptor(( unitOfWork, interceptorChain) -> {
            if (unitOfWork.getMessage().getMetaData().containsKey("key")) {
                return "fakeReply";
            }
            return interceptorChain.proceed();
        });
        testSubject.subscribe(String.class.getName(), String.class, ( q) -> (q.getPayload()) + "1234");
        QueryMessage<String, String> testQueryMessage = new GenericQueryMessage("hello", singleStringResponse);
        CompletableFuture<String> result = testSubject.query(testQueryMessage).thenApply(QueryResponseMessage::getPayload);
        Assert.assertEquals("fakeReply", result.get());
    }

    @Test
    public void testQueryDoesNotArriveAtUnsubscribedHandler() throws Exception {
        testSubject.subscribe(String.class.getName(), String.class, ( q) -> "1234");
        testSubject.subscribe(String.class.getName(), String.class, ( q) -> (q.getPayload()) + " is not here!").close();
        QueryMessage<String, String> testQueryMessage = new GenericQueryMessage("hello", singleStringResponse);
        CompletableFuture<String> result = testSubject.query(testQueryMessage).thenApply(QueryResponseMessage::getPayload);
        Assert.assertEquals("1234", result.get());
    }

    @Test
    public void testQueryReturnsException() throws Exception {
        MockException mockException = new MockException();
        testSubject.subscribe(String.class.getName(), String.class, ( q) -> {
            throw mockException;
        });
        QueryMessage<String, String> testQueryMessage = new GenericQueryMessage("hello", singleStringResponse);
        CompletableFuture<QueryResponseMessage<String>> result = testSubject.query(testQueryMessage);
        Assert.assertTrue(result.isDone());
        Assert.assertFalse(result.isCompletedExceptionally());
        QueryResponseMessage<String> queryResponseMessage = result.get();
        Assert.assertTrue(queryResponseMessage.isExceptional());
        Assert.assertEquals(mockException, queryResponseMessage.exceptionResult());
    }

    @Test
    public void testQueryUnknown() throws Exception {
        QueryMessage<String, String> testQueryMessage = new GenericQueryMessage("hello", singleStringResponse);
        CompletableFuture<?> result = testSubject.query(testQueryMessage);
        try {
            result.get();
            Assert.fail("Expected exception");
        } catch (ExecutionException e) {
            Assert.assertEquals(NoHandlerForQueryException.class, e.getCause().getClass());
        }
    }

    @Test
    public void testQueryUnsubscribedHandlers() throws Exception {
        testSubject.subscribe(String.class.getName(), String.class, ( q) -> (q.getPayload()) + " is not here!").close();
        testSubject.subscribe(String.class.getName(), String.class, ( q) -> (q.getPayload()) + " is not here!").close();
        QueryMessage<String, String> testQueryMessage = new GenericQueryMessage("hello", singleStringResponse);
        CompletableFuture<?> result = testSubject.query(testQueryMessage);
        try {
            result.get();
            Assert.fail("Expected exception");
        } catch (ExecutionException e) {
            Assert.assertEquals(NoHandlerForQueryException.class, e.getCause().getClass());
        }
        Mockito.verify(messageMonitor, Mockito.times(1)).onMessageIngested(ArgumentMatchers.any());
        Mockito.verify(monitorCallback, Mockito.times(1)).reportFailure(ArgumentMatchers.any());
    }

    @Test
    public void testScatterGather() {
        int expectedResults = 3;
        testSubject.subscribe(String.class.getName(), String.class, ( q) -> (q.getPayload()) + "1234");
        testSubject.subscribe(String.class.getName(), String.class, ( q) -> (q.getPayload()) + "5678");
        testSubject.subscribe(String.class.getName(), String.class, ( q) -> (q.getPayload()) + "90");
        QueryMessage<String, String> testQueryMessage = new GenericQueryMessage("Hello, World", singleStringResponse);
        Set<QueryResponseMessage<String>> results = testSubject.scatterGather(testQueryMessage, 0, TimeUnit.SECONDS).collect(Collectors.toSet());
        Assert.assertEquals(expectedResults, results.size());
        Set<String> resultSet = results.stream().map(Message::getPayload).collect(Collectors.toSet());
        Assert.assertEquals(expectedResults, resultSet.size());
        Mockito.verify(messageMonitor, Mockito.times(1)).onMessageIngested(ArgumentMatchers.any());
        Mockito.verify(monitorCallback, Mockito.times(3)).reportSuccess();
    }

    @Test
    public void testScatterGatherOnArrayQueryHandlers() throws NoSuchMethodException {
        int expectedQueryResponses = 3;
        int expectedResults = 6;
        testSubject.subscribe(String.class.getName(), ReflectionUtils.methodOf(getClass(), "stringArrayQueryHandler").getGenericReturnType(), ( q) -> new String[]{ (q.getPayload()) + "12", (q.getPayload()) + "34" });
        testSubject.subscribe(String.class.getName(), ReflectionUtils.methodOf(getClass(), "stringArrayQueryHandler").getGenericReturnType(), ( q) -> new String[]{ (q.getPayload()) + "56", (q.getPayload()) + "78" });
        testSubject.subscribe(String.class.getName(), ReflectionUtils.methodOf(getClass(), "stringArrayQueryHandler").getGenericReturnType(), ( q) -> new String[]{ (q.getPayload()) + "9", (q.getPayload()) + "0" });
        QueryMessage<String, List<String>> testQueryMessage = new GenericQueryMessage("Hello, World", ResponseTypes.multipleInstancesOf(String.class));
        Set<QueryResponseMessage<List<String>>> results = testSubject.scatterGather(testQueryMessage, 0, TimeUnit.SECONDS).collect(Collectors.toSet());
        Assert.assertEquals(expectedQueryResponses, results.size());
        Set<String> resultSet = results.stream().map(Message::getPayload).flatMap(Collection::stream).collect(Collectors.toSet());
        Assert.assertEquals(expectedResults, resultSet.size());
        Mockito.verify(messageMonitor, Mockito.times(1)).onMessageIngested(ArgumentMatchers.any());
        Mockito.verify(monitorCallback, Mockito.times(3)).reportSuccess();
    }

    @Test
    public void testScatterGatherWithTransaction() {
        TransactionManager mockTxManager = Mockito.mock(TransactionManager.class);
        Transaction mockTx = Mockito.mock(Transaction.class);
        Mockito.when(mockTxManager.startTransaction()).thenReturn(mockTx);
        testSubject = SimpleQueryBus.builder().messageMonitor(messageMonitor).transactionManager(mockTxManager).errorHandler(errorHandler).build();
        testSubject.subscribe(String.class.getName(), String.class, ( q) -> (q.getPayload()) + "1234");
        testSubject.subscribe(String.class.getName(), String.class, ( q) -> (q.getPayload()) + "567");
        QueryMessage<String, String> testQueryMessage = new GenericQueryMessage("Hello, World", singleStringResponse);
        Set<Object> results = testSubject.scatterGather(testQueryMessage, 0, TimeUnit.SECONDS).collect(Collectors.toSet());
        Assert.assertEquals(2, results.size());
        Mockito.verify(messageMonitor, Mockito.times(1)).onMessageIngested(ArgumentMatchers.any());
        Mockito.verify(monitorCallback, Mockito.times(2)).reportSuccess();
        Mockito.verify(mockTxManager, Mockito.times(2)).startTransaction();
        Mockito.verify(mockTx, Mockito.times(2)).commit();
    }

    @Test
    public void testScatterGatherWithTransactionRollsBackOnFailure() {
        TransactionManager mockTxManager = Mockito.mock(TransactionManager.class);
        Transaction mockTx = Mockito.mock(Transaction.class);
        Mockito.when(mockTxManager.startTransaction()).thenReturn(mockTx);
        testSubject = SimpleQueryBus.builder().messageMonitor(messageMonitor).transactionManager(mockTxManager).errorHandler(errorHandler).build();
        testSubject.subscribe(String.class.getName(), String.class, ( q) -> (q.getPayload()) + "1234");
        testSubject.subscribe(String.class.getName(), String.class, ( q) -> {
            throw new MockException();
        });
        QueryMessage<String, String> testQueryMessage = new GenericQueryMessage("Hello, World", singleStringResponse);
        Set<Object> resulst = testSubject.scatterGather(testQueryMessage, 0, TimeUnit.SECONDS).collect(Collectors.toSet());
        Assert.assertEquals(1, resulst.size());
        Mockito.verify(messageMonitor, Mockito.times(1)).onMessageIngested(ArgumentMatchers.any());
        Mockito.verify(monitorCallback, Mockito.times(1)).reportSuccess();
        Mockito.verify(monitorCallback, Mockito.times(1)).reportFailure(ArgumentMatchers.isA(MockException.class));
        Mockito.verify(mockTxManager, Mockito.times(2)).startTransaction();
        Mockito.verify(mockTx, Mockito.times(1)).commit();
        Mockito.verify(mockTx, Mockito.times(1)).rollback();
    }

    @Test
    public void testQueryFirstFromScatterGatherWillCommitUnitOfWork() {
        TransactionManager mockTxManager = Mockito.mock(TransactionManager.class);
        Transaction mockTx = Mockito.mock(Transaction.class);
        Mockito.when(mockTxManager.startTransaction()).thenReturn(mockTx);
        testSubject = SimpleQueryBus.builder().messageMonitor(messageMonitor).transactionManager(mockTxManager).errorHandler(errorHandler).build();
        testSubject.subscribe(String.class.getName(), String.class, ( q) -> (q.getPayload()) + "1234");
        testSubject.subscribe(String.class.getName(), String.class, ( q) -> (q.getPayload()) + "567");
        QueryMessage<String, String> testQueryMessage = new GenericQueryMessage("Hello, World", singleStringResponse);
        Optional<QueryResponseMessage<String>> firstResult = testSubject.scatterGather(testQueryMessage, 0, TimeUnit.SECONDS).findFirst();
        Assert.assertTrue(firstResult.isPresent());
        Mockito.verify(messageMonitor, Mockito.times(1)).onMessageIngested(ArgumentMatchers.any());
        Mockito.verify(monitorCallback, Mockito.atMost(2)).reportSuccess();
        Mockito.verify(mockTxManager).startTransaction();
        Mockito.verify(mockTx).commit();
    }

    @Test
    public void testScatterGatherWithInterceptors() {
        testSubject.registerDispatchInterceptor(( messages) -> ( i, m) -> m.andMetaData(Collections.singletonMap("key", "value")));
        testSubject.registerHandlerInterceptor(( unitOfWork, interceptorChain) -> {
            if (unitOfWork.getMessage().getMetaData().containsKey("key")) {
                return "fakeReply";
            }
            return interceptorChain.proceed();
        });
        testSubject.subscribe(String.class.getName(), String.class, ( q) -> (q.getPayload()) + "1234");
        testSubject.subscribe(String.class.getName(), String.class, ( q) -> (q.getPayload()) + "567");
        QueryMessage<String, String> testQueryMessage = new GenericQueryMessage("Hello, World", singleStringResponse);
        List<String> results = testSubject.scatterGather(testQueryMessage, 0, TimeUnit.SECONDS).map(Message::getPayload).collect(Collectors.toList());
        Assert.assertEquals(2, results.size());
        Mockito.verify(messageMonitor, Mockito.times(1)).onMessageIngested(ArgumentMatchers.any());
        Mockito.verify(monitorCallback, Mockito.times(2)).reportSuccess();
        Assert.assertEquals(Arrays.asList("fakeReply", "fakeReply"), results);
    }

    @Test
    public void testScatterGatherReturnsEmptyStreamWhenNoHandlersAvailable() {
        QueryMessage<String, String> testQueryMessage = new GenericQueryMessage("Hello, World", singleStringResponse);
        Set<Object> allResults = testSubject.scatterGather(testQueryMessage, 0, TimeUnit.SECONDS).collect(Collectors.toSet());
        Assert.assertEquals(0, allResults.size());
        Mockito.verify(messageMonitor).onMessageIngested(ArgumentMatchers.any());
        Mockito.verify(monitorCallback).reportIgnored();
    }

    @Test
    public void testScatterGatherReportsExceptionsWithErrorHandler() {
        testSubject.subscribe(String.class.getName(), String.class, ( q) -> (q.getPayload()) + "1234");
        testSubject.subscribe(String.class.getName(), String.class, ( q) -> {
            throw new MockException();
        });
        QueryMessage<String, String> testQueryMessage = new GenericQueryMessage("Hello, World", singleStringResponse);
        Set<Object> results = testSubject.scatterGather(testQueryMessage, 0, TimeUnit.SECONDS).collect(Collectors.toSet());
        Assert.assertEquals(1, results.size());
        Mockito.verify(errorHandler).onError(ArgumentMatchers.isA(MockException.class), ArgumentMatchers.eq(testQueryMessage), ArgumentMatchers.isA(MessageHandler.class));
        Mockito.verify(messageMonitor, Mockito.times(1)).onMessageIngested(ArgumentMatchers.any());
        Mockito.verify(monitorCallback, Mockito.times(1)).reportSuccess();
        Mockito.verify(monitorCallback, Mockito.times(1)).reportFailure(ArgumentMatchers.isA(MockException.class));
    }

    @Test
    public void testQueryResponseMessageCorrelationData() throws InterruptedException, ExecutionException {
        testSubject.subscribe(String.class.getName(), String.class, ( q) -> (q.getPayload()) + "1234");
        testSubject.registerHandlerInterceptor(new org.axonframework.messaging.interceptors.CorrelationDataInterceptor(new MessageOriginProvider()));
        QueryMessage<String, String> testQueryMessage = new GenericQueryMessage("Hello, World", singleStringResponse);
        QueryResponseMessage<String> queryResponseMessage = testSubject.query(testQueryMessage).get();
        Assert.assertEquals(testQueryMessage.getIdentifier(), queryResponseMessage.getMetaData().get("traceId"));
        Assert.assertEquals(testQueryMessage.getIdentifier(), queryResponseMessage.getMetaData().get("correlationId"));
        Assert.assertEquals("Hello, World1234", queryResponseMessage.getPayload());
    }

    @Test
    public void testSubscriptionQueryReportsExceptionInInitialResult() {
        testSubject.subscribe(String.class.getName(), String.class, ( q) -> {
            throw new MockException();
        });
        SubscriptionQueryResult<QueryResponseMessage<String>, SubscriptionQueryUpdateMessage<String>> result = testSubject.subscriptionQuery(new GenericSubscriptionQueryMessage("test", ResponseTypes.instanceOf(String.class), ResponseTypes.instanceOf(String.class)));
        Mono<QueryResponseMessage<String>> initialResult = result.initialResult();
        Assert.assertFalse("Exception by handler should be reported in result, not on Mono", initialResult.map(( r) -> false).onErrorReturn(MockException.class::isInstance, true).block());
        Assert.assertTrue(initialResult.block().isExceptional());
    }

    @Test
    public void testQueryReportsExceptionInResponseMessage() throws InterruptedException, ExecutionException {
        testSubject.subscribe(String.class.getName(), String.class, ( q) -> {
            throw new MockException();
        });
        CompletableFuture<QueryResponseMessage<String>> result = testSubject.query(new GenericQueryMessage("test", ResponseTypes.instanceOf(String.class)));
        Assert.assertFalse("Exception by handler should be reported in result, not on Mono", result.thenApply(( r) -> false).exceptionally(MockException.class::isInstance).get());
        Assert.assertTrue(result.get().isExceptional());
    }

    @Test
    public void testQueryHandlerDeclaresFutureResponseType() throws Exception {
        Type responseType = ReflectionUtils.methodOf(getClass(), "futureMethod").getGenericReturnType();
        testSubject.subscribe(String.class.getName(), responseType, ( q) -> CompletableFuture.completedFuture(((q.getPayload()) + "1234")));
        QueryMessage<String, String> testQueryMessage = new GenericQueryMessage("hello", singleStringResponse);
        CompletableFuture<QueryResponseMessage<String>> result = testSubject.query(testQueryMessage);
        Assert.assertTrue("SimpleQueryBus should resolve CompletableFutures directly", result.isDone());
        Assert.assertEquals("hello1234", result.get().getPayload());
    }

    @Test
    public void testQueryHandlerDeclaresCompletableFutureResponseType() throws Exception {
        Type responseType = ReflectionUtils.methodOf(getClass(), "completableFutureMethod").getGenericReturnType();
        testSubject.subscribe(String.class.getName(), responseType, ( q) -> CompletableFuture.completedFuture(((q.getPayload()) + "1234")));
        QueryMessage<String, String> testQueryMessage = new GenericQueryMessage("hello", singleStringResponse);
        CompletableFuture<QueryResponseMessage<String>> result = testSubject.query(testQueryMessage);
        Assert.assertTrue("SimpleQueryBus should resolve CompletableFutures directly", result.isDone());
        Assert.assertEquals("hello1234", result.get().getPayload());
    }
}

