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
package org.axonframework.axonserver.connector.query;


import ErrorCode.QUERY_DISPATCH_ERROR;
import ErrorCode.QUERY_EXECUTION_ERROR;
import io.axoniq.axonserver.grpc.query.QueryProviderInbound;
import io.grpc.stub.StreamObserver;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.axonframework.axonserver.connector.AxonServerConfiguration;
import org.axonframework.axonserver.connector.AxonServerConnectionManager;
import org.axonframework.axonserver.connector.utils.AssertUtils;
import org.axonframework.common.Registration;
import org.axonframework.messaging.MetaData;
import org.axonframework.queryhandling.QueryExecutionException;
import org.axonframework.queryhandling.QueryMessage;
import org.axonframework.queryhandling.QueryResponseMessage;
import org.axonframework.queryhandling.SimpleQueryBus;
import org.axonframework.serialization.xml.XStreamSerializer;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Marc Gathier
 */
public class AxonServerQueryBusTest {
    private DummyMessagePlatformServer dummyMessagePlatformServer;

    private AxonServerConfiguration conf;

    private SimpleQueryBus localSegment;

    private AxonServerConnectionManager axonServerConnectionManager;

    private AtomicReference<StreamObserver<QueryProviderInbound>> inboundStreamObserverRef;

    private XStreamSerializer ser;

    private AxonServerQueryBus testSubject;

    @Test
    public void subscribe() throws Exception {
        Registration response = testSubject.subscribe("testQuery", String.class, ( q) -> "test");
        Thread.sleep(1000);
        AssertUtils.assertWithin(1000, TimeUnit.MILLISECONDS, () -> Assert.assertNotNull(dummyMessagePlatformServer.subscriptions("testQuery", String.class.getName())));
        response.cancel();
        AssertUtils.assertWithin(2000, TimeUnit.MILLISECONDS, () -> Assert.assertNull(dummyMessagePlatformServer.subscriptions("testQuery", String.class.getName())));
    }

    @Test
    public void query() throws Exception {
        QueryMessage<String, String> queryMessage = new org.axonframework.queryhandling.GenericQueryMessage("Hello, World", instanceOf(String.class));
        Assert.assertEquals("test", testSubject.query(queryMessage).get().getPayload());
    }

    @Test
    public void queryWhenQueryServiceStubFails() {
        RuntimeException expected = new RuntimeException("oops");
        testSubject = Mockito.spy(testSubject);
        Mockito.when(testSubject.queryService()).thenThrow(expected);
        QueryMessage<String, String> queryMessage = new org.axonframework.queryhandling.GenericQueryMessage("Hello, World", instanceOf(String.class));
        CompletableFuture<QueryResponseMessage<String>> result = testSubject.query(queryMessage);
        Assert.assertTrue(result.isDone());
        Assert.assertTrue(result.isCompletedExceptionally());
        try {
            result.get();
            Assert.fail("Expected an exception here");
        } catch (Exception actual) {
            Assert.assertTrue(((actual.getCause()) instanceof AxonServerQueryDispatchException));
            AxonServerQueryDispatchException queryDispatchException = ((AxonServerQueryDispatchException) (actual.getCause()));
            Assert.assertEquals(QUERY_DISPATCH_ERROR.errorCode(), queryDispatchException.code());
        }
    }

    @Test
    public void testQueryReportsCorrectException() throws InterruptedException, ExecutionException {
        QueryMessage<String, String> queryMessage = new org.axonframework.queryhandling.GenericQueryMessage("Hello, World", instanceOf(String.class)).andMetaData(Collections.singletonMap("errorCode", QUERY_EXECUTION_ERROR.errorCode()));
        CompletableFuture<QueryResponseMessage<String>> result = testSubject.query(queryMessage);
        Assert.assertNotNull(result.get());
        Assert.assertFalse(result.isCompletedExceptionally());
        Assert.assertTrue(result.get().isExceptional());
        Throwable actual = result.get().exceptionResult();
        Assert.assertTrue((actual instanceof QueryExecutionException));
        AxonServerRemoteQueryHandlingException queryDispatchException = ((AxonServerRemoteQueryHandlingException) (actual.getCause()));
        Assert.assertEquals(QUERY_EXECUTION_ERROR.errorCode(), queryDispatchException.getErrorCode());
    }

    @Test
    public void processQuery() {
        AxonServerQueryBus queryBus2 = new AxonServerQueryBus(axonServerConnectionManager, conf, localSegment.queryUpdateEmitter(), localSegment, ser, ser, QueryPriorityCalculator.defaultQueryPriorityCalculator());
        Registration response = queryBus2.subscribe("testQuery", String.class, ( q) -> "test: " + (q.getPayloadType()));
        QueryProviderInbound inboundMessage = testQueryMessage();
        inboundStreamObserverRef.get().onNext(inboundMessage);
        response.close();
    }

    @Test
    public void scatterGather() {
        QueryMessage<String, String> queryMessage = new org.axonframework.queryhandling.GenericQueryMessage("Hello, World", instanceOf(String.class)).andMetaData(MetaData.with("repeat", 10).and("interval", 10));
        Assert.assertEquals(10, testSubject.scatterGather(queryMessage, 12, TimeUnit.SECONDS).count());
    }

    @Test
    public void scatterGatherTimeout() {
        QueryMessage<String, String> queryMessage = new org.axonframework.queryhandling.GenericQueryMessage("Hello, World", instanceOf(String.class)).andMetaData(MetaData.with("repeat", 10).and("interval", 100));
        Assert.assertTrue((8 > (testSubject.scatterGather(queryMessage, 550, TimeUnit.MILLISECONDS).count())));
    }

    @Test
    public void dispatchInterceptor() {
        List<Object> results = new LinkedList<>();
        testSubject.registerDispatchInterceptor(( messages) -> ( a, b) -> {
            results.add(b.getPayload());
            return b;
        });
        testSubject.query(new org.axonframework.queryhandling.GenericQueryMessage("payload", new org.axonframework.messaging.responsetypes.InstanceResponseType(String.class)));
        Assert.assertEquals("payload", results.get(0));
        Assert.assertEquals(1, results.size());
    }

    @Test
    public void handlerInterceptor() {
        SimpleQueryBus localSegment = SimpleQueryBus.builder().build();
        AxonServerQueryBus bus = new AxonServerQueryBus(axonServerConnectionManager, conf, localSegment.queryUpdateEmitter(), localSegment, ser, ser, QueryPriorityCalculator.defaultQueryPriorityCalculator());
        bus.subscribe("testQuery", String.class, ( q) -> "test: " + (q.getPayloadType()));
        List<Object> results = new LinkedList<>();
        bus.registerHandlerInterceptor(( unitOfWork, interceptorChain) -> {
            results.add("Interceptor executed");
            return interceptorChain.proceed();
        });
        QueryProviderInbound inboundMessage = testQueryMessage();
        inboundStreamObserverRef.get().onNext(inboundMessage);
        AssertUtils.assertWithin(1, TimeUnit.SECONDS, () -> Assert.assertEquals(1, results.size()));
        AssertUtils.assertWithin(1, TimeUnit.SECONDS, () -> Assert.assertEquals("Interceptor executed", results.get(0)));
    }

    @Test
    public void reconnectAfterConnectionLost() throws InterruptedException {
        testSubject.subscribe("testQuery", String.class, ( q) -> "test");
        Thread.sleep(50);
        Assert.assertNotNull(dummyMessagePlatformServer.subscriptions("testQuery", String.class.getName()));
        dummyMessagePlatformServer.onError("testQuery", String.class.getName());
        Thread.sleep(200);
        Assert.assertNotNull(dummyMessagePlatformServer.subscriptions("testQuery", String.class.getName()));
    }
}

