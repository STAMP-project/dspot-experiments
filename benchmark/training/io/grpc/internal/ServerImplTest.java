/**
 * Copyright 2014 The gRPC Authors
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
package io.grpc.internal;


import Attributes.EMPTY;
import Attributes.Key;
import Context.CancellableContext;
import Context.ROOT;
import Grpc.TRANSPORT_ATTR_REMOTE_ADDR;
import MethodDescriptor.MethodType.UNKNOWN;
import ServerStreamTracer.Factory;
import Status.ABORTED;
import Status.CANCELLED;
import Status.Code.UNIMPLEMENTED;
import Status.OK;
import StreamListener.MessageProducer;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import io.grpc.Attributes;
import io.grpc.BinaryLog;
import io.grpc.Channel;
import io.grpc.Context;
import io.grpc.HandlerRegistry;
import io.grpc.IntegerMarshaller;
import io.grpc.InternalChannelz;
import io.grpc.InternalChannelz.ServerSocketsList;
import io.grpc.InternalChannelz.SocketStats;
import io.grpc.InternalInstrumented;
import io.grpc.InternalLogId;
import io.grpc.InternalServerInterceptors;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.ServerMethodDefinition;
import io.grpc.ServerServiceDefinition;
import io.grpc.ServerStreamTracer;
import io.grpc.ServerTransportFilter;
import io.grpc.Status;
import io.grpc.StringMarshaller;
import io.grpc.internal.ServerImpl.JumpToApplicationThreadServerStreamListener;
import io.grpc.internal.testing.SingleMessageProducer;
import io.grpc.internal.testing.TestServerStreamTracer;
import io.grpc.util.MutableHandlerRegistry;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nullable;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Unit tests for {@link ServerImpl}.
 */
@RunWith(JUnit4.class)
public class ServerImplTest {
    private static final IntegerMarshaller INTEGER_MARSHALLER = IntegerMarshaller.INSTANCE;

    private static final StringMarshaller STRING_MARSHALLER = StringMarshaller.INSTANCE;

    private static final MethodDescriptor<String, Integer> METHOD = MethodDescriptor.<String, Integer>newBuilder().setType(UNKNOWN).setFullMethodName("Waiter/serve").setRequestMarshaller(ServerImplTest.STRING_MARSHALLER).setResponseMarshaller(ServerImplTest.INTEGER_MARSHALLER).build();

    private static final Context.Key<String> SERVER_ONLY = Context.key("serverOnly");

    private static final Context.Key<String> SERVER_TRACER_ADDED_KEY = Context.key("tracer-added");

    private static final CancellableContext SERVER_CONTEXT = ROOT.withValue(io.grpc.internal.SERVER_ONLY, "yes").withCancellation();

    private static final FakeClock.TaskFilter CONTEXT_CLOSER_TASK_FITLER = new FakeClock.TaskFilter() {
        @Override
        public boolean shouldAccept(Runnable runnable) {
            return runnable instanceof ServerImpl.ContextCloser;
        }
    };

    private static final String AUTHORITY = "some_authority";

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private final FakeClock executor = new FakeClock();

    private final FakeClock timer = new FakeClock();

    private final InternalChannelz channelz = new InternalChannelz();

    @Mock
    private Factory streamTracerFactory;

    private List<ServerStreamTracer.Factory> streamTracerFactories;

    private final TestServerStreamTracer streamTracer = new TestServerStreamTracer() {
        @Override
        public Context filterContext(Context context) {
            Context newCtx = super.filterContext(context);
            return newCtx.withValue(io.grpc.internal.SERVER_TRACER_ADDED_KEY, "context added by tracer");
        }
    };

    @Mock
    private ObjectPool<Executor> executorPool;

    private ServerImplTest.Builder builder = new ServerImplTest.Builder();

    private MutableHandlerRegistry mutableFallbackRegistry = new MutableHandlerRegistry();

    private HandlerRegistry fallbackRegistry = Mockito.mock(HandlerRegistry.class, AdditionalAnswers.delegatesTo(new HandlerRegistry() {
        @Override
        public ServerMethodDefinition<?, ?> lookupMethod(String methodName, @Nullable
        String authority) {
            return mutableFallbackRegistry.lookupMethod(methodName, authority);
        }

        @Override
        public List<ServerServiceDefinition> getServices() {
            return mutableFallbackRegistry.getServices();
        }
    }));

    private ServerImplTest.SimpleServer transportServer = new ServerImplTest.SimpleServer();

    private ServerImpl server;

    @Captor
    private ArgumentCaptor<Status> statusCaptor;

    @Captor
    private ArgumentCaptor<Metadata> metadataCaptor;

    @Captor
    private ArgumentCaptor<ServerStreamListener> streamListenerCaptor;

    @Mock
    private ServerStream stream;

    @Mock
    private ServerCall.Listener<String> callListener;

    @Mock
    private ServerCallHandler<String, Integer> callHandler;

    @Test
    public void multiport() throws Exception {
        final CountDownLatch starts = new CountDownLatch(2);
        final CountDownLatch shutdowns = new CountDownLatch(2);
        final class Serv extends ServerImplTest.SimpleServer {
            @Override
            public void start(ServerListener listener) throws IOException {
                super.start(listener);
                starts.countDown();
            }

            @Override
            public void shutdown() {
                super.shutdown();
                shutdowns.countDown();
            }
        }
        ServerImplTest.SimpleServer transportServer1 = new Serv();
        ServerImplTest.SimpleServer transportServer2 = new Serv();
        Assert.assertNull(server);
        builder.fallbackHandlerRegistry(fallbackRegistry);
        builder.executorPool = executorPool;
        server = new ServerImpl(builder, ImmutableList.of(transportServer1, transportServer2), ServerImplTest.SERVER_CONTEXT);
        server.start();
        Assert.assertTrue(starts.await(1, TimeUnit.SECONDS));
        Assert.assertEquals(2, shutdowns.getCount());
        server.shutdown();
        Assert.assertTrue(shutdowns.await(1, TimeUnit.SECONDS));
        Assert.assertTrue(server.awaitTermination(1, TimeUnit.SECONDS));
    }

    @Test
    public void startStopImmediate() throws IOException {
        transportServer = new ServerImplTest.SimpleServer() {
            @Override
            public void shutdown() {
            }
        };
        createAndStartServer();
        server.shutdown();
        Assert.assertTrue(server.isShutdown());
        Assert.assertFalse(server.isTerminated());
        transportServer.listener.serverShutdown();
        Assert.assertTrue(server.isTerminated());
    }

    @Test
    public void stopImmediate() throws IOException {
        transportServer = new ServerImplTest.SimpleServer() {
            @Override
            public void shutdown() {
                throw new AssertionError("Should not be called, because wasn't started");
            }
        };
        createServer();
        server.shutdown();
        Assert.assertTrue(server.isShutdown());
        Assert.assertTrue(server.isTerminated());
        Mockito.verifyNoMoreInteractions(executorPool);
    }

    @Test
    public void startStopImmediateWithChildTransport() throws IOException {
        createAndStartServer();
        verifyExecutorsAcquired();
        class DelayedShutdownServerTransport extends ServerImplTest.SimpleServerTransport {
            boolean shutdown;

            @Override
            public void shutdown() {
                shutdown = true;
            }
        }
        DelayedShutdownServerTransport serverTransport = new DelayedShutdownServerTransport();
        transportServer.registerNewServerTransport(serverTransport);
        server.shutdown();
        Assert.assertTrue(server.isShutdown());
        Assert.assertFalse(server.isTerminated());
        Assert.assertTrue(serverTransport.shutdown);
        verifyExecutorsNotReturned();
        serverTransport.listener.transportTerminated();
        Assert.assertTrue(server.isTerminated());
        verifyExecutorsReturned();
    }

    @Test
    public void startShutdownNowImmediateWithChildTransport() throws IOException {
        createAndStartServer();
        verifyExecutorsAcquired();
        class DelayedShutdownServerTransport extends ServerImplTest.SimpleServerTransport {
            boolean shutdown;

            @Override
            public void shutdown() {
            }

            @Override
            public void shutdownNow(Status reason) {
                shutdown = true;
            }
        }
        DelayedShutdownServerTransport serverTransport = new DelayedShutdownServerTransport();
        transportServer.registerNewServerTransport(serverTransport);
        server.shutdownNow();
        Assert.assertTrue(server.isShutdown());
        Assert.assertFalse(server.isTerminated());
        Assert.assertTrue(serverTransport.shutdown);
        verifyExecutorsNotReturned();
        serverTransport.listener.transportTerminated();
        Assert.assertTrue(server.isTerminated());
        verifyExecutorsReturned();
    }

    @Test
    public void shutdownNowAfterShutdown() throws IOException {
        createAndStartServer();
        verifyExecutorsAcquired();
        class DelayedShutdownServerTransport extends ServerImplTest.SimpleServerTransport {
            boolean shutdown;

            @Override
            public void shutdown() {
            }

            @Override
            public void shutdownNow(Status reason) {
                shutdown = true;
            }
        }
        DelayedShutdownServerTransport serverTransport = new DelayedShutdownServerTransport();
        transportServer.registerNewServerTransport(serverTransport);
        server.shutdown();
        Assert.assertTrue(server.isShutdown());
        server.shutdownNow();
        Assert.assertFalse(server.isTerminated());
        Assert.assertTrue(serverTransport.shutdown);
        verifyExecutorsNotReturned();
        serverTransport.listener.transportTerminated();
        Assert.assertTrue(server.isTerminated());
        verifyExecutorsReturned();
    }

    @Test
    public void shutdownNowAfterSlowShutdown() throws IOException {
        transportServer = new ServerImplTest.SimpleServer() {
            @Override
            public void shutdown() {
                // Don't call super which calls listener.serverShutdown(). We'll call it manually.
            }
        };
        createAndStartServer();
        verifyExecutorsAcquired();
        class DelayedShutdownServerTransport extends ServerImplTest.SimpleServerTransport {
            boolean shutdown;

            @Override
            public void shutdown() {
            }

            @Override
            public void shutdownNow(Status reason) {
                shutdown = true;
            }
        }
        DelayedShutdownServerTransport serverTransport = new DelayedShutdownServerTransport();
        transportServer.registerNewServerTransport(serverTransport);
        server.shutdown();
        server.shutdownNow();
        transportServer.listener.serverShutdown();
        Assert.assertTrue(server.isShutdown());
        Assert.assertFalse(server.isTerminated());
        verifyExecutorsNotReturned();
        serverTransport.listener.transportTerminated();
        verifyExecutorsReturned();
        Assert.assertTrue(server.isTerminated());
    }

    @Test
    public void transportServerFailsStartup() {
        final IOException ex = new IOException();
        class FailingStartupServer extends ServerImplTest.SimpleServer {
            @Override
            public void start(ServerListener listener) throws IOException {
                throw ex;
            }
        }
        transportServer = new FailingStartupServer();
        createServer();
        try {
            server.start();
            Assert.fail("expected exception");
        } catch (IOException e) {
            Assert.assertSame(ex, e);
        }
        Mockito.verifyNoMoreInteractions(executorPool);
    }

    @Test
    public void transportHandshakeTimeout_expired() throws Exception {
        class ShutdownRecordingTransport extends ServerImplTest.SimpleServerTransport {
            Status shutdownNowStatus;

            @Override
            public void shutdownNow(Status status) {
                shutdownNowStatus = status;
                super.shutdownNow(status);
            }
        }
        handshakeTimeout(60, TimeUnit.SECONDS);
        createAndStartServer();
        ShutdownRecordingTransport serverTransport = new ShutdownRecordingTransport();
        transportServer.registerNewServerTransport(serverTransport);
        timer.forwardTime(59, TimeUnit.SECONDS);
        Assert.assertNull("shutdownNow status", serverTransport.shutdownNowStatus);
        // Don't call transportReady() in time
        timer.forwardTime(2, TimeUnit.SECONDS);
        Assert.assertNotNull("shutdownNow status", serverTransport.shutdownNowStatus);
    }

    @Test
    public void methodNotFound() throws Exception {
        createAndStartServer();
        ServerTransportListener transportListener = transportServer.registerNewServerTransport(new ServerImplTest.SimpleServerTransport());
        transportListener.transportReady(EMPTY);
        Metadata requestHeaders = new Metadata();
        StatsTraceContext statsTraceCtx = StatsTraceContext.newServerContext(streamTracerFactories, "Waiter/nonexist", requestHeaders);
        Mockito.when(stream.statsTraceContext()).thenReturn(statsTraceCtx);
        transportListener.streamCreated(stream, "Waiter/nonexist", requestHeaders);
        Mockito.verify(stream).setListener(ArgumentMatchers.isA(ServerStreamListener.class));
        Mockito.verify(stream, Mockito.atLeast(1)).statsTraceContext();
        Assert.assertEquals(1, executor.runDueTasks());
        Mockito.verify(stream).close(statusCaptor.capture(), ArgumentMatchers.any(Metadata.class));
        Status status = statusCaptor.getValue();
        Assert.assertEquals(UNIMPLEMENTED, status.getCode());
        Assert.assertEquals("Method not found: Waiter/nonexist", status.getDescription());
        Mockito.verify(streamTracerFactory).newServerStreamTracer(ArgumentMatchers.eq("Waiter/nonexist"), ArgumentMatchers.same(requestHeaders));
        Assert.assertNull(streamTracer.getServerCallInfo());
        Assert.assertEquals(UNIMPLEMENTED, statusCaptor.getValue().getCode());
    }

    @Test
    public void decompressorNotFound() throws Exception {
        String decompressorName = "NON_EXISTENT_DECOMPRESSOR";
        createAndStartServer();
        ServerTransportListener transportListener = transportServer.registerNewServerTransport(new ServerImplTest.SimpleServerTransport());
        transportListener.transportReady(EMPTY);
        Metadata requestHeaders = new Metadata();
        requestHeaders.put(GrpcUtil.MESSAGE_ENCODING_KEY, decompressorName);
        StatsTraceContext statsTraceCtx = StatsTraceContext.newServerContext(streamTracerFactories, "Waiter/nonexist", requestHeaders);
        Mockito.when(stream.statsTraceContext()).thenReturn(statsTraceCtx);
        transportListener.streamCreated(stream, "Waiter/nonexist", requestHeaders);
        Mockito.verify(stream).close(statusCaptor.capture(), ArgumentMatchers.any(Metadata.class));
        Status status = statusCaptor.getValue();
        Assert.assertEquals(UNIMPLEMENTED, status.getCode());
        Assert.assertEquals(("Can't find decompressor for " + decompressorName), status.getDescription());
        Mockito.verifyNoMoreInteractions(stream);
    }

    @Test
    public void basicExchangeSuccessful() throws Exception {
        createAndStartServer();
        basicExchangeHelper(ServerImplTest.METHOD, "Lots of pizza, please", 314, 50);
    }

    @Test
    public void transportFilters() throws Exception {
        final SocketAddress remoteAddr = Mockito.mock(SocketAddress.class);
        final Attributes.Key<String> key1 = Key.create("test-key1");
        final Attributes.Key<String> key2 = Key.create("test-key2");
        final Attributes.Key<String> key3 = Key.create("test-key3");
        final AtomicReference<Attributes> filter1TerminationCallbackArgument = new AtomicReference<>();
        final AtomicReference<Attributes> filter2TerminationCallbackArgument = new AtomicReference<>();
        final AtomicInteger readyCallbackCalled = new AtomicInteger(0);
        final AtomicInteger terminationCallbackCalled = new AtomicInteger(0);
        builder.addTransportFilter(new ServerTransportFilter() {
            @Override
            public Attributes transportReady(Attributes attrs) {
                Assert.assertEquals(Attributes.newBuilder().set(TRANSPORT_ATTR_REMOTE_ADDR, remoteAddr).build(), attrs);
                readyCallbackCalled.incrementAndGet();
                return attrs.toBuilder().set(key1, "yalayala").set(key2, "blabla").build();
            }

            @Override
            public void transportTerminated(Attributes attrs) {
                terminationCallbackCalled.incrementAndGet();
                filter1TerminationCallbackArgument.set(attrs);
            }
        });
        addTransportFilter(new ServerTransportFilter() {
            @Override
            public Attributes transportReady(Attributes attrs) {
                Assert.assertEquals(Attributes.newBuilder().set(TRANSPORT_ATTR_REMOTE_ADDR, remoteAddr).set(key1, "yalayala").set(key2, "blabla").build(), attrs);
                readyCallbackCalled.incrementAndGet();
                return attrs.toBuilder().set(key1, "ouch").set(key3, "puff").build();
            }

            @Override
            public void transportTerminated(Attributes attrs) {
                terminationCallbackCalled.incrementAndGet();
                filter2TerminationCallbackArgument.set(attrs);
            }
        });
        Attributes expectedTransportAttrs = Attributes.newBuilder().set(key1, "ouch").set(key2, "blabla").set(key3, "puff").set(TRANSPORT_ATTR_REMOTE_ADDR, remoteAddr).build();
        createAndStartServer();
        ServerTransportListener transportListener = transportServer.registerNewServerTransport(new ServerImplTest.SimpleServerTransport());
        Attributes transportAttrs = transportListener.transportReady(Attributes.newBuilder().set(TRANSPORT_ATTR_REMOTE_ADDR, remoteAddr).build());
        Assert.assertEquals(expectedTransportAttrs, transportAttrs);
        server.shutdown();
        server.awaitTermination();
        Assert.assertEquals(expectedTransportAttrs, filter1TerminationCallbackArgument.get());
        Assert.assertEquals(expectedTransportAttrs, filter2TerminationCallbackArgument.get());
        Assert.assertEquals(2, readyCallbackCalled.get());
        Assert.assertEquals(2, terminationCallbackCalled.get());
    }

    @Test
    public void interceptors() throws Exception {
        final LinkedList<Context> capturedContexts = new LinkedList<>();
        final Context.Key<String> key1 = Context.key("key1");
        final Context.Key<String> key2 = Context.key("key2");
        final Context.Key<String> key3 = Context.key("key3");
        ServerInterceptor interceptor1 = new ServerInterceptor() {
            @Override
            public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
                Context ctx = Context.current().withValue(key1, "value1");
                Context origCtx = ctx.attach();
                try {
                    capturedContexts.add(ctx);
                    return next.startCall(call, headers);
                } finally {
                    ctx.detach(origCtx);
                }
            }
        };
        ServerInterceptor interceptor2 = new ServerInterceptor() {
            @Override
            public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
                Context ctx = Context.current().withValue(key2, "value2");
                Context origCtx = ctx.attach();
                try {
                    capturedContexts.add(ctx);
                    return next.startCall(call, headers);
                } finally {
                    ctx.detach(origCtx);
                }
            }
        };
        ServerCallHandler<String, Integer> callHandler = new ServerCallHandler<String, Integer>() {
            @Override
            public ServerCall.Listener<String> startCall(ServerCall<String, Integer> call, Metadata headers) {
                capturedContexts.add(Context.current().withValue(key3, "value3"));
                return callListener;
            }
        };
        mutableFallbackRegistry.addService(ServerServiceDefinition.builder(new io.grpc.ServiceDescriptor("Waiter", ServerImplTest.METHOD)).addMethod(ServerImplTest.METHOD, callHandler).build());
        builder.intercept(interceptor2);
        builder.intercept(interceptor1);
        createServer();
        server.start();
        ServerTransportListener transportListener = transportServer.registerNewServerTransport(new ServerImplTest.SimpleServerTransport());
        transportListener.transportReady(EMPTY);
        Metadata requestHeaders = new Metadata();
        StatsTraceContext statsTraceCtx = StatsTraceContext.newServerContext(streamTracerFactories, "Waiter/serve", requestHeaders);
        Mockito.when(stream.statsTraceContext()).thenReturn(statsTraceCtx);
        transportListener.streamCreated(stream, "Waiter/serve", requestHeaders);
        Assert.assertEquals(1, executor.runDueTasks());
        Context ctx1 = capturedContexts.poll();
        Assert.assertEquals("value1", key1.get(ctx1));
        Assert.assertNull(key2.get(ctx1));
        Assert.assertNull(key3.get(ctx1));
        Context ctx2 = capturedContexts.poll();
        Assert.assertEquals("value1", key1.get(ctx2));
        Assert.assertEquals("value2", key2.get(ctx2));
        Assert.assertNull(key3.get(ctx2));
        Context ctx3 = capturedContexts.poll();
        Assert.assertEquals("value1", key1.get(ctx3));
        Assert.assertEquals("value2", key2.get(ctx3));
        Assert.assertEquals("value3", key3.get(ctx3));
        Assert.assertTrue(capturedContexts.isEmpty());
    }

    @Test
    public void exceptionInStartCallPropagatesToStream() throws Exception {
        createAndStartServer();
        final Status status = ABORTED.withDescription("Oh, no!");
        mutableFallbackRegistry.addService(ServerServiceDefinition.builder(new io.grpc.ServiceDescriptor("Waiter", ServerImplTest.METHOD)).addMethod(ServerImplTest.METHOD, new ServerCallHandler<String, Integer>() {
            @Override
            public ServerCall.Listener<String> startCall(ServerCall<String, Integer> call, Metadata headers) {
                throw status.asRuntimeException();
            }
        }).build());
        ServerTransportListener transportListener = transportServer.registerNewServerTransport(new ServerImplTest.SimpleServerTransport());
        transportListener.transportReady(EMPTY);
        Metadata requestHeaders = new Metadata();
        StatsTraceContext statsTraceCtx = StatsTraceContext.newServerContext(streamTracerFactories, "Waiter/serve", requestHeaders);
        Mockito.when(stream.statsTraceContext()).thenReturn(statsTraceCtx);
        transportListener.streamCreated(stream, "Waiter/serve", requestHeaders);
        Mockito.verify(stream).setListener(streamListenerCaptor.capture());
        ServerStreamListener streamListener = streamListenerCaptor.getValue();
        Assert.assertNotNull(streamListener);
        Mockito.verify(stream, Mockito.atLeast(1)).statsTraceContext();
        Mockito.verifyNoMoreInteractions(stream);
        Mockito.verify(fallbackRegistry, Mockito.never()).lookupMethod(ArgumentMatchers.any(String.class), ArgumentMatchers.any(String.class));
        Assert.assertEquals(1, executor.runDueTasks());
        Mockito.verify(fallbackRegistry).lookupMethod("Waiter/serve", ServerImplTest.AUTHORITY);
        Mockito.verify(stream).close(ArgumentMatchers.same(status), ArgumentMatchers.notNull(Metadata.class));
        Mockito.verify(stream, Mockito.atLeast(1)).statsTraceContext();
    }

    @Test
    public void testNoDeadlockOnShutdown() throws Exception {
        final Object lock = new Object();
        final CyclicBarrier barrier = new CyclicBarrier(2);
        class MaybeDeadlockingServer extends ServerImplTest.SimpleServer {
            @Override
            public void shutdown() {
                // To deadlock, a lock would need to be held while this method is in progress.
                try {
                    barrier.await();
                } catch (Exception ex) {
                    throw new AssertionError(ex);
                }
                // If deadlock is possible with this setup, this sychronization completes the loop because
                // the serverShutdown needs a lock that Server is holding while calling this method.
                synchronized(lock) {
                }
            }
        }
        transportServer = new MaybeDeadlockingServer();
        createAndStartServer();
        new Thread() {
            @Override
            public void run() {
                synchronized(lock) {
                    try {
                        barrier.await();
                    } catch (Exception ex) {
                        throw new AssertionError(ex);
                    }
                    // To deadlock, a lock would be needed for this call to proceed.
                    transportServer.listener.serverShutdown();
                }
            }
        }.start();
        server.shutdown();
    }

    @Test
    public void testNoDeadlockOnTransportShutdown() throws Exception {
        createAndStartServer();
        final Object lock = new Object();
        final CyclicBarrier barrier = new CyclicBarrier(2);
        class MaybeDeadlockingServerTransport extends ServerImplTest.SimpleServerTransport {
            @Override
            public void shutdown() {
                // To deadlock, a lock would need to be held while this method is in progress.
                try {
                    barrier.await();
                } catch (Exception ex) {
                    throw new AssertionError(ex);
                }
                // If deadlock is possible with this setup, this sychronization completes the loop
                // because the transportTerminated needs a lock that Server is holding while calling this
                // method.
                synchronized(lock) {
                }
            }
        }
        final ServerTransportListener transportListener = transportServer.registerNewServerTransport(new MaybeDeadlockingServerTransport());
        new Thread() {
            @Override
            public void run() {
                synchronized(lock) {
                    try {
                        barrier.await();
                    } catch (Exception ex) {
                        throw new AssertionError(ex);
                    }
                    // To deadlock, a lock would be needed for this call to proceed.
                    transportListener.transportTerminated();
                }
            }
        }.start();
        server.shutdown();
    }

    @Test
    public void testCallContextIsBoundInListenerCallbacks() throws Exception {
        createAndStartServer();
        final AtomicBoolean onReadyCalled = new AtomicBoolean(false);
        final AtomicBoolean onMessageCalled = new AtomicBoolean(false);
        final AtomicBoolean onHalfCloseCalled = new AtomicBoolean(false);
        final AtomicBoolean onCancelCalled = new AtomicBoolean(false);
        mutableFallbackRegistry.addService(ServerServiceDefinition.builder(new io.grpc.ServiceDescriptor("Waiter", ServerImplTest.METHOD)).addMethod(ServerImplTest.METHOD, new ServerCallHandler<String, Integer>() {
            @Override
            public ServerCall.Listener<String> startCall(ServerCall<String, Integer> call, Metadata headers) {
                // Check that the current context is a descendant of SERVER_CONTEXT
                final Context initial = Context.current();
                Assert.assertEquals("yes", io.grpc.internal.SERVER_ONLY.get(initial));
                Assert.assertNotSame(ServerImplTest.SERVER_CONTEXT, initial);
                Assert.assertFalse(initial.isCancelled());
                return new ServerCall.Listener<String>() {
                    @Override
                    public void onReady() {
                        checkContext();
                        onReadyCalled.set(true);
                    }

                    @Override
                    public void onMessage(String message) {
                        checkContext();
                        onMessageCalled.set(true);
                    }

                    @Override
                    public void onHalfClose() {
                        checkContext();
                        onHalfCloseCalled.set(true);
                    }

                    @Override
                    public void onCancel() {
                        checkContext();
                        onCancelCalled.set(true);
                    }

                    @Override
                    public void onComplete() {
                        checkContext();
                    }

                    private void checkContext() {
                        // Check that the bound context is the same as the initial one.
                        assertSame(initial, Context.current());
                    }
                };
            }
        }).build());
        ServerTransportListener transportListener = transportServer.registerNewServerTransport(new ServerImplTest.SimpleServerTransport());
        transportListener.transportReady(EMPTY);
        Metadata requestHeaders = new Metadata();
        StatsTraceContext statsTraceCtx = StatsTraceContext.newServerContext(streamTracerFactories, "Waitier/serve", requestHeaders);
        Mockito.when(stream.statsTraceContext()).thenReturn(statsTraceCtx);
        transportListener.streamCreated(stream, "Waiter/serve", requestHeaders);
        Mockito.verify(stream).setListener(streamListenerCaptor.capture());
        ServerStreamListener streamListener = streamListenerCaptor.getValue();
        Assert.assertNotNull(streamListener);
        streamListener.onReady();
        Assert.assertEquals(1, executor.runDueTasks());
        Assert.assertTrue(onReadyCalled.get());
        streamListener.messagesAvailable(new SingleMessageProducer(new ByteArrayInputStream(new byte[0])));
        Assert.assertEquals(1, executor.runDueTasks());
        Assert.assertTrue(onMessageCalled.get());
        streamListener.halfClosed();
        Assert.assertEquals(1, executor.runDueTasks());
        Assert.assertTrue(onHalfCloseCalled.get());
        streamListener.closed(CANCELLED);
        Assert.assertEquals(1, executor.numPendingTasks(ServerImplTest.CONTEXT_CLOSER_TASK_FITLER));
        Assert.assertEquals(2, executor.runDueTasks());
        Assert.assertTrue(onCancelCalled.get());
        // Close should never be called if asserts in listener pass.
        Mockito.verify(stream, Mockito.times(0)).close(ArgumentMatchers.isA(Status.class), ArgumentMatchers.isNotNull(Metadata.class));
    }

    @Test
    public void testClientClose_cancelTriggersImmediateCancellation() throws Exception {
        AtomicBoolean contextCancelled = new AtomicBoolean(false);
        AtomicReference<Context> context = new AtomicReference<>();
        AtomicReference<ServerCall<String, Integer>> callReference = new AtomicReference<>();
        ServerStreamListener streamListener = testClientClose_setup(callReference, context, contextCancelled);
        // For close status being non OK:
        // isCancelled is expected to be true immediately after calling closed(), without needing
        // to wait for the main executor to run any tasks.
        Assert.assertFalse(callReference.get().isCancelled());
        Assert.assertFalse(context.get().isCancelled());
        streamListener.closed(CANCELLED);
        Assert.assertEquals(1, executor.numPendingTasks(ServerImplTest.CONTEXT_CLOSER_TASK_FITLER));
        Assert.assertEquals(2, executor.runDueTasks());
        Assert.assertTrue(callReference.get().isCancelled());
        Assert.assertTrue(context.get().isCancelled());
        Assert.assertTrue(contextCancelled.get());
    }

    @Test
    public void testClientClose_OkTriggersDelayedCancellation() throws Exception {
        AtomicBoolean contextCancelled = new AtomicBoolean(false);
        AtomicReference<Context> context = new AtomicReference<>();
        AtomicReference<ServerCall<String, Integer>> callReference = new AtomicReference<>();
        ServerStreamListener streamListener = testClientClose_setup(callReference, context, contextCancelled);
        // For close status OK:
        // isCancelled is expected to be true after all pending work is done
        Assert.assertFalse(callReference.get().isCancelled());
        Assert.assertFalse(context.get().isCancelled());
        streamListener.closed(OK);
        Assert.assertFalse(callReference.get().isCancelled());
        Assert.assertFalse(context.get().isCancelled());
        Assert.assertEquals(1, executor.runDueTasks());
        Assert.assertTrue(callReference.get().isCancelled());
        Assert.assertTrue(context.get().isCancelled());
        Assert.assertTrue(contextCancelled.get());
    }

    @Test
    public void getPort() throws Exception {
        final InetSocketAddress addr = new InetSocketAddress(65535);
        transportServer = new ServerImplTest.SimpleServer() {
            @Override
            public SocketAddress getListenSocketAddress() {
                return addr;
            }
        };
        createAndStartServer();
        assertThat(server.getPort()).isEqualTo(addr.getPort());
    }

    @Test
    public void getPortBeforeStartedFails() {
        transportServer = new ServerImplTest.SimpleServer();
        createServer();
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("started");
        server.getPort();
    }

    @Test
    public void getPortAfterTerminationFails() throws Exception {
        transportServer = new ServerImplTest.SimpleServer();
        createAndStartServer();
        server.shutdown();
        server.awaitTermination();
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("terminated");
        server.getPort();
    }

    @Test
    public void handlerRegistryPriorities() throws Exception {
        fallbackRegistry = Mockito.mock(HandlerRegistry.class);
        builder.addService(ServerServiceDefinition.builder(new io.grpc.ServiceDescriptor("Waiter", ServerImplTest.METHOD)).addMethod(ServerImplTest.METHOD, callHandler).build());
        transportServer = new ServerImplTest.SimpleServer();
        createAndStartServer();
        ServerTransportListener transportListener = transportServer.registerNewServerTransport(new ServerImplTest.SimpleServerTransport());
        transportListener.transportReady(EMPTY);
        Metadata requestHeaders = new Metadata();
        StatsTraceContext statsTraceCtx = StatsTraceContext.newServerContext(streamTracerFactories, "Waiter/serve", requestHeaders);
        Mockito.when(stream.statsTraceContext()).thenReturn(statsTraceCtx);
        // This call will be handled by callHandler from the internal registry
        transportListener.streamCreated(stream, "Waiter/serve", requestHeaders);
        Assert.assertEquals(1, executor.runDueTasks());
        Mockito.verify(callHandler).startCall(Matchers.<ServerCall<String, Integer>>anyObject(), Matchers.<Metadata>anyObject());
        // This call will be handled by the fallbackRegistry because it's not registred in the internal
        // registry.
        transportListener.streamCreated(stream, "Service1/Method2", requestHeaders);
        Assert.assertEquals(1, executor.runDueTasks());
        Mockito.verify(fallbackRegistry).lookupMethod("Service1/Method2", ServerImplTest.AUTHORITY);
        Mockito.verifyNoMoreInteractions(callHandler);
        Mockito.verifyNoMoreInteractions(fallbackRegistry);
    }

    @Test
    public void messageRead_errorCancelsCall() throws Exception {
        JumpToApplicationThreadServerStreamListener listener = new JumpToApplicationThreadServerStreamListener(executor.getScheduledExecutorService(), executor.getScheduledExecutorService(), stream, ROOT.withCancellation());
        ServerStreamListener mockListener = Mockito.mock(ServerStreamListener.class);
        listener.setListener(mockListener);
        ServerImplTest.TestError expectedT = new ServerImplTest.TestError();
        Mockito.doThrow(expectedT).when(mockListener).messagesAvailable(ArgumentMatchers.any(MessageProducer.class));
        // Closing the InputStream is done by the delegated listener (generally ServerCallImpl)
        listener.messagesAvailable(Mockito.mock(MessageProducer.class));
        try {
            executor.runDueTasks();
            Assert.fail("Expected exception");
        } catch (ServerImplTest.TestError t) {
            Assert.assertSame(expectedT, t);
            ensureServerStateNotLeaked();
        }
    }

    @Test
    public void messageRead_runtimeExceptionCancelsCall() throws Exception {
        JumpToApplicationThreadServerStreamListener listener = new JumpToApplicationThreadServerStreamListener(executor.getScheduledExecutorService(), executor.getScheduledExecutorService(), stream, ROOT.withCancellation());
        ServerStreamListener mockListener = Mockito.mock(ServerStreamListener.class);
        listener.setListener(mockListener);
        RuntimeException expectedT = new RuntimeException();
        Mockito.doThrow(expectedT).when(mockListener).messagesAvailable(ArgumentMatchers.any(MessageProducer.class));
        // Closing the InputStream is done by the delegated listener (generally ServerCallImpl)
        listener.messagesAvailable(Mockito.mock(MessageProducer.class));
        try {
            executor.runDueTasks();
            Assert.fail("Expected exception");
        } catch (RuntimeException t) {
            Assert.assertSame(expectedT, t);
            ensureServerStateNotLeaked();
        }
    }

    @Test
    public void halfClosed_errorCancelsCall() {
        JumpToApplicationThreadServerStreamListener listener = new JumpToApplicationThreadServerStreamListener(executor.getScheduledExecutorService(), executor.getScheduledExecutorService(), stream, ROOT.withCancellation());
        ServerStreamListener mockListener = Mockito.mock(ServerStreamListener.class);
        listener.setListener(mockListener);
        ServerImplTest.TestError expectedT = new ServerImplTest.TestError();
        Mockito.doThrow(expectedT).when(mockListener).halfClosed();
        listener.halfClosed();
        try {
            executor.runDueTasks();
            Assert.fail("Expected exception");
        } catch (ServerImplTest.TestError t) {
            Assert.assertSame(expectedT, t);
            ensureServerStateNotLeaked();
        }
    }

    @Test
    public void halfClosed_runtimeExceptionCancelsCall() {
        JumpToApplicationThreadServerStreamListener listener = new JumpToApplicationThreadServerStreamListener(executor.getScheduledExecutorService(), executor.getScheduledExecutorService(), stream, ROOT.withCancellation());
        ServerStreamListener mockListener = Mockito.mock(ServerStreamListener.class);
        listener.setListener(mockListener);
        RuntimeException expectedT = new RuntimeException();
        Mockito.doThrow(expectedT).when(mockListener).halfClosed();
        listener.halfClosed();
        try {
            executor.runDueTasks();
            Assert.fail("Expected exception");
        } catch (RuntimeException t) {
            Assert.assertSame(expectedT, t);
            ensureServerStateNotLeaked();
        }
    }

    @Test
    public void onReady_errorCancelsCall() {
        JumpToApplicationThreadServerStreamListener listener = new JumpToApplicationThreadServerStreamListener(executor.getScheduledExecutorService(), executor.getScheduledExecutorService(), stream, ROOT.withCancellation());
        ServerStreamListener mockListener = Mockito.mock(ServerStreamListener.class);
        listener.setListener(mockListener);
        ServerImplTest.TestError expectedT = new ServerImplTest.TestError();
        Mockito.doThrow(expectedT).when(mockListener).onReady();
        listener.onReady();
        try {
            executor.runDueTasks();
            Assert.fail("Expected exception");
        } catch (ServerImplTest.TestError t) {
            Assert.assertSame(expectedT, t);
            ensureServerStateNotLeaked();
        }
    }

    @Test
    public void onReady_runtimeExceptionCancelsCall() {
        JumpToApplicationThreadServerStreamListener listener = new JumpToApplicationThreadServerStreamListener(executor.getScheduledExecutorService(), executor.getScheduledExecutorService(), stream, ROOT.withCancellation());
        ServerStreamListener mockListener = Mockito.mock(ServerStreamListener.class);
        listener.setListener(mockListener);
        RuntimeException expectedT = new RuntimeException();
        Mockito.doThrow(expectedT).when(mockListener).onReady();
        listener.onReady();
        try {
            executor.runDueTasks();
            Assert.fail("Expected exception");
        } catch (RuntimeException t) {
            Assert.assertSame(expectedT, t);
            ensureServerStateNotLeaked();
        }
    }

    @Test
    public void binaryLogInstalled() throws Exception {
        final SettableFuture<Boolean> intercepted = SettableFuture.create();
        final ServerInterceptor interceptor = new ServerInterceptor() {
            @Override
            public <ReqT, RespT> Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
                intercepted.set(true);
                return next.startCall(call, headers);
            }
        };
        builder.binlog = new BinaryLog() {
            @Override
            public void close() throws IOException {
                // noop
            }

            @Override
            public <ReqT, RespT> ServerMethodDefinition<?, ?> wrapMethodDefinition(ServerMethodDefinition<ReqT, RespT> oMethodDef) {
                return ServerMethodDefinition.create(oMethodDef.getMethodDescriptor(), InternalServerInterceptors.interceptCallHandlerCreate(interceptor, oMethodDef.getServerCallHandler()));
            }

            @Override
            public Channel wrapChannel(Channel channel) {
                return channel;
            }
        };
        createAndStartServer();
        basicExchangeHelper(ServerImplTest.METHOD, "Lots of pizza, please", 314, 50);
        Assert.assertTrue(intercepted.get());
    }

    @Test
    public void channelz_membership() throws Exception {
        createServer();
        Assert.assertTrue(builder.channelz.containsServer(server.getLogId()));
        server.shutdownNow().awaitTermination();
        Assert.assertFalse(builder.channelz.containsServer(server.getLogId()));
    }

    @Test
    public void channelz_serverStats() throws Exception {
        createAndStartServer();
        Assert.assertEquals(0, server.getStats().get().callsSucceeded);
        basicExchangeHelper(ServerImplTest.METHOD, "Lots of pizza, please", 314, null);
        Assert.assertEquals(1, server.getStats().get().callsSucceeded);
    }

    @Test
    public void channelz_transport_membershp() throws Exception {
        createAndStartServer();
        ServerImplTest.SimpleServerTransport transport = new ServerImplTest.SimpleServerTransport();
        ServerSocketsList before = /* maxPageSize= */
        builder.channelz.getServerSockets(InternalChannelz.id(server), InternalChannelz.id(transport), 1);
        assertThat(before.sockets).isEmpty();
        Assert.assertTrue(before.end);
        ServerTransportListener listener = transportServer.registerNewServerTransport(transport);
        ServerSocketsList added = /* maxPageSize= */
        builder.channelz.getServerSockets(InternalChannelz.id(server), InternalChannelz.id(transport), 1);
        assertThat(added.sockets).containsExactly(transport);
        Assert.assertTrue(before.end);
        listener.transportTerminated();
        ServerSocketsList after = /* maxPageSize= */
        builder.channelz.getServerSockets(InternalChannelz.id(server), InternalChannelz.id(transport), 1);
        assertThat(after.sockets).isEmpty();
        Assert.assertTrue(after.end);
    }

    private static class SimpleServer implements io.grpc.internal.InternalServer {
        ServerListener listener;

        @Override
        public void start(ServerListener listener) throws IOException {
            this.listener = listener;
        }

        @Override
        public SocketAddress getListenSocketAddress() {
            return new InetSocketAddress(12345);
        }

        @Override
        public InternalInstrumented<SocketStats> getListenSocketStats() {
            return null;
        }

        @Override
        public void shutdown() {
            listener.serverShutdown();
        }

        public ServerTransportListener registerNewServerTransport(ServerImplTest.SimpleServerTransport transport) {
            return transport.listener = listener.transportCreated(transport);
        }
    }

    private class SimpleServerTransport implements ServerTransport {
        ServerTransportListener listener;

        InternalLogId id = /* details= */
        InternalLogId.allocate(getClass(), null);

        @Override
        public void shutdown() {
            listener.transportTerminated();
        }

        @Override
        public void shutdownNow(Status status) {
            listener.transportTerminated();
        }

        @Override
        public InternalLogId getLogId() {
            return id;
        }

        @Override
        public ScheduledExecutorService getScheduledExecutorService() {
            return timer.getScheduledExecutorService();
        }

        @Override
        public ListenableFuture<SocketStats> getStats() {
            SettableFuture<SocketStats> ret = SettableFuture.create();
            ret.set(null);
            return ret;
        }
    }

    private static class Builder extends AbstractServerImplBuilder<ServerImplTest.Builder> {
        @Override
        protected List<InternalServer> buildTransportServers(List<? extends ServerStreamTracer.Factory> streamTracerFactories) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ServerImplTest.Builder useTransportSecurity(File f1, File f2) {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Allows more precise catch blocks than plain Error to avoid catching AssertionError.
     */
    private static final class TestError extends Error {}
}

