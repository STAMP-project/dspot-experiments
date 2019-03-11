/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.queryhandling;


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Stream;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.utils.MockException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public class DefaultQueryGatewayTest {
    private QueryBus mockBus;

    private DefaultQueryGateway testSubject;

    private QueryResponseMessage<String> answer;

    @Test
    public void testDispatchSingleResultQuery() throws Exception {
        Mockito.when(mockBus.query(anyMessage(String.class, String.class))).thenReturn(CompletableFuture.completedFuture(answer));
        CompletableFuture<String> actual = testSubject.query("query", String.class);
        Assert.assertEquals("answer", actual.get());
        Mockito.verify(mockBus).query(ArgumentMatchers.argThat(((ArgumentMatcher<QueryMessage<String, String>>) (( x) -> "query".equals(x.getPayload())))));
    }

    @Test
    public void testDispatchSingleResultQueryWhenBusReportsAnError() throws Exception {
        Throwable expected = new Throwable("oops");
        Mockito.when(mockBus.query(anyMessage(String.class, String.class))).thenReturn(CompletableFuture.completedFuture(new GenericQueryResponseMessage(String.class, expected)));
        CompletableFuture<String> result = testSubject.query("query", String.class);
        Assert.assertTrue(result.isDone());
        Assert.assertTrue(result.isCompletedExceptionally());
        Assert.assertEquals(expected.getMessage(), result.exceptionally(Throwable::getMessage).get());
    }

    @Test
    public void testDispatchSingleResultQueryWhenBusThrowsException() throws Exception {
        Throwable expected = new Throwable("oops");
        CompletableFuture<QueryResponseMessage<String>> queryResponseMessageCompletableFuture = new CompletableFuture<>();
        queryResponseMessageCompletableFuture.completeExceptionally(expected);
        Mockito.when(mockBus.query(anyMessage(String.class, String.class))).thenReturn(queryResponseMessageCompletableFuture);
        CompletableFuture<String> result = testSubject.query("query", String.class);
        Assert.assertTrue(result.isDone());
        Assert.assertTrue(result.isCompletedExceptionally());
        Assert.assertEquals(expected.getMessage(), result.exceptionally(Throwable::getMessage).get());
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testDispatchMultiResultQuery() {
        Mockito.when(mockBus.scatterGather(anyMessage(String.class, String.class), ArgumentMatchers.anyLong(), ArgumentMatchers.any())).thenReturn(Stream.of(answer));
        Stream<String> actual = testSubject.scatterGather("query", ResponseTypes.instanceOf(String.class), 1, TimeUnit.SECONDS);
        Assert.assertEquals("answer", actual.findFirst().get());
        Mockito.verify(mockBus).scatterGather(ArgumentMatchers.argThat(((ArgumentMatcher<QueryMessage<String, String>>) (( x) -> "query".equals(x.getPayload())))), ArgumentMatchers.eq(1L), ArgumentMatchers.eq(TimeUnit.SECONDS));
    }

    @Test
    public void testDispatchSubscriptionQuery() {
        Mockito.when(mockBus.subscriptionQuery(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyInt())).thenReturn(new DefaultSubscriptionQueryResult(Mono.empty(), Flux.empty(), () -> true));
        testSubject.subscriptionQuery("query", ResponseTypes.instanceOf(String.class), ResponseTypes.instanceOf(String.class));
        Mockito.verify(mockBus).subscriptionQuery(ArgumentMatchers.argThat(((ArgumentMatcher<SubscriptionQueryMessage<String, String, String>>) (( x) -> "query".equals(x.getPayload())))), ArgumentMatchers.any(), ArgumentMatchers.anyInt());
    }

    @Test
    public void testDispatchInterceptor() {
        Mockito.when(mockBus.query(anyMessage(String.class, String.class))).thenReturn(CompletableFuture.completedFuture(answer));
        testSubject.registerDispatchInterceptor(( messages) -> ( integer, queryMessage) -> new GenericQueryMessage<>(("dispatch-" + (queryMessage.getPayload())), queryMessage.getQueryName(), queryMessage.getResponseType()));
        testSubject.query("query", String.class).join();
        Mockito.verify(mockBus).query(ArgumentMatchers.argThat(((ArgumentMatcher<QueryMessage<String, String>>) (( x) -> "dispatch-query".equals(x.getPayload())))));
    }

    @Test
    public void testExceptionInInitialResultOfSubscriptionQueryReportedInMono() {
        Mockito.when(mockBus.subscriptionQuery(anySubscriptionMessage(String.class, String.class), ArgumentMatchers.any(), ArgumentMatchers.anyInt())).thenReturn(new DefaultSubscriptionQueryResult(Mono.just(new GenericQueryResponseMessage(String.class, new MockException())), Flux.empty(), () -> true));
        SubscriptionQueryResult<String, String> actual = testSubject.subscriptionQuery("Test", ResponseTypes.instanceOf(String.class), ResponseTypes.instanceOf(String.class));
        Assert.assertEquals(MockException.class, actual.initialResult().map(( i) -> null).onErrorResume(( e) -> Mono.just(e.getClass())).block());
    }

    @Test
    public void testNullInitialResultOfSubscriptionQueryReportedAsEmptyMono() {
        Mockito.when(mockBus.subscriptionQuery(anySubscriptionMessage(String.class, String.class), ArgumentMatchers.any(), ArgumentMatchers.anyInt())).thenReturn(new DefaultSubscriptionQueryResult(Mono.just(new GenericQueryResponseMessage(String.class, ((String) (null)))), Flux.empty(), () -> true));
        SubscriptionQueryResult<String, String> actual = testSubject.subscriptionQuery("Test", ResponseTypes.instanceOf(String.class), ResponseTypes.instanceOf(String.class));
        Assert.assertNull(actual.initialResult().block());
    }

    @Test
    public void testNullUpdatesOfSubscriptionQuerySkipped() {
        Mockito.when(mockBus.subscriptionQuery(anySubscriptionMessage(String.class, String.class), ArgumentMatchers.any(), ArgumentMatchers.anyInt())).thenReturn(new DefaultSubscriptionQueryResult(Mono.empty(), Flux.just(new GenericSubscriptionQueryUpdateMessage(String.class, null)), () -> true));
        SubscriptionQueryResult<String, String> actual = testSubject.subscriptionQuery("Test", ResponseTypes.instanceOf(String.class), ResponseTypes.instanceOf(String.class));
        Assert.assertNull(actual.initialResult().block());
        Assert.assertEquals(((Long) (0L)), actual.updates().count().block());
    }

    @Test
    public void testPayloadExtractionProblemsReportedInException() throws InterruptedException, ExecutionException {
        Mockito.when(mockBus.query(anyMessage(String.class, String.class))).thenReturn(CompletableFuture.completedFuture(new GenericQueryResponseMessage<String>("test") {
            @Override
            public String getPayload() {
                throw new MockException("Faking serialization problem");
            }
        }));
        CompletableFuture<String> actual = testSubject.query("query", String.class);
        Assert.assertTrue(actual.isDone());
        Assert.assertTrue(actual.isCompletedExceptionally());
        Assert.assertEquals("Faking serialization problem", actual.exceptionally(Throwable::getMessage).get());
    }
}

