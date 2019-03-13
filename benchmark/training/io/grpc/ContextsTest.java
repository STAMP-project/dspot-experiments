/**
 * Copyright 2016 The gRPC Authors
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
package io.grpc;


import Context.CancellableContext;
import Context.ROOT;
import Status.Code.CANCELLED;
import Status.DEADLINE_EXCEEDED;
import Status.UNKNOWN;
import io.grpc.internal.FakeClock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link Contexts}.
 */
@RunWith(JUnit4.class)
public class ContextsTest {
    private static Context.Key<Object> contextKey = Context.key("key");

    /**
     * For use in comparing context by reference.
     */
    private Context uniqueContext = ROOT.withValue(contextKey, new Object());

    @SuppressWarnings("unchecked")
    private ServerCall<Object, Object> call = new io.grpc.internal.NoopServerCall();

    private Metadata headers = new Metadata();

    @Test
    public void interceptCall_basic() {
        Context origContext = Context.current();
        final Object message = new Object();
        final List<Integer> methodCalls = new ArrayList<>();
        final ServerCall.Listener<Object> listener = new ServerCall.Listener<Object>() {
            @Override
            public void onMessage(Object messageIn) {
                assertSame(message, messageIn);
                assertSame(uniqueContext, Context.current());
                methodCalls.add(1);
            }

            @Override
            public void onHalfClose() {
                assertSame(uniqueContext, Context.current());
                methodCalls.add(2);
            }

            @Override
            public void onCancel() {
                assertSame(uniqueContext, Context.current());
                methodCalls.add(3);
            }

            @Override
            public void onComplete() {
                assertSame(uniqueContext, Context.current());
                methodCalls.add(4);
            }

            @Override
            public void onReady() {
                assertSame(uniqueContext, Context.current());
                methodCalls.add(5);
            }
        };
        ServerCall.Listener<Object> wrapped = Contexts.interceptCall(uniqueContext, call, headers, new ServerCallHandler<Object, Object>() {
            @Override
            public ServerCall.Listener<Object> startCall(ServerCall<Object, Object> call, Metadata headers) {
                Assert.assertSame(ContextsTest.this.call, call);
                Assert.assertSame(ContextsTest.this.headers, headers);
                Assert.assertSame(uniqueContext, Context.current());
                return listener;
            }
        });
        Assert.assertSame(origContext, Context.current());
        wrapped.onMessage(message);
        wrapped.onHalfClose();
        wrapped.onCancel();
        wrapped.onComplete();
        wrapped.onReady();
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5), methodCalls);
        Assert.assertSame(origContext, Context.current());
    }

    @Test
    public void interceptCall_restoresIfNextThrows() {
        Context origContext = Context.current();
        try {
            Contexts.interceptCall(uniqueContext, call, headers, new ServerCallHandler<Object, Object>() {
                @Override
                public ServerCall.Listener<Object> startCall(ServerCall<Object, Object> call, Metadata headers) {
                    throw new RuntimeException();
                }
            });
            Assert.fail("Expected exception");
        } catch (RuntimeException expected) {
        }
        Assert.assertSame(origContext, Context.current());
    }

    @Test
    public void interceptCall_restoresIfListenerThrows() {
        Context origContext = Context.current();
        final ServerCall.Listener<Object> listener = new ServerCall.Listener<Object>() {
            @Override
            public void onMessage(Object messageIn) {
                throw new RuntimeException();
            }

            @Override
            public void onHalfClose() {
                throw new RuntimeException();
            }

            @Override
            public void onCancel() {
                throw new RuntimeException();
            }

            @Override
            public void onComplete() {
                throw new RuntimeException();
            }

            @Override
            public void onReady() {
                throw new RuntimeException();
            }
        };
        ServerCall.Listener<Object> wrapped = Contexts.interceptCall(uniqueContext, call, headers, new ServerCallHandler<Object, Object>() {
            @Override
            public ServerCall.Listener<Object> startCall(ServerCall<Object, Object> call, Metadata headers) {
                return listener;
            }
        });
        try {
            wrapped.onMessage(new Object());
            Assert.fail("Exception expected");
        } catch (RuntimeException expected) {
        }
        try {
            wrapped.onHalfClose();
            Assert.fail("Exception expected");
        } catch (RuntimeException expected) {
        }
        try {
            wrapped.onCancel();
            Assert.fail("Exception expected");
        } catch (RuntimeException expected) {
        }
        try {
            wrapped.onComplete();
            Assert.fail("Exception expected");
        } catch (RuntimeException expected) {
        }
        try {
            wrapped.onReady();
            Assert.fail("Exception expected");
        } catch (RuntimeException expected) {
        }
        Assert.assertSame(origContext, Context.current());
    }

    @Test
    public void statusFromCancelled_returnNullIfCtxNotCancelled() {
        Context context = Context.current();
        Assert.assertFalse(context.isCancelled());
        Assert.assertNull(Contexts.statusFromCancelled(context));
    }

    @Test
    public void statusFromCancelled_returnStatusAsSetOnCtx() {
        Context.CancellableContext cancellableContext = Context.current().withCancellation();
        cancellableContext.cancel(DEADLINE_EXCEEDED.withDescription("foo bar").asException());
        Status status = Contexts.statusFromCancelled(cancellableContext);
        Assert.assertNotNull(status);
        Assert.assertEquals(Status.Code.DEADLINE_EXCEEDED, status.getCode());
        Assert.assertEquals("foo bar", status.getDescription());
    }

    @Test
    public void statusFromCancelled_shouldReturnStatusWithCauseAttached() {
        Context.CancellableContext cancellableContext = Context.current().withCancellation();
        Throwable t = new Throwable();
        cancellableContext.cancel(t);
        Status status = Contexts.statusFromCancelled(cancellableContext);
        Assert.assertNotNull(status);
        Assert.assertEquals(CANCELLED, status.getCode());
        Assert.assertSame(t, status.getCause());
    }

    @Test
    public void statusFromCancelled_TimeoutExceptionShouldMapToDeadlineExceeded() {
        FakeClock fakeClock = new FakeClock();
        Context.CancellableContext cancellableContext = Context.current().withDeadlineAfter(100, TimeUnit.NANOSECONDS, fakeClock.getScheduledExecutorService());
        fakeClock.forwardTime(System.nanoTime(), TimeUnit.NANOSECONDS);
        fakeClock.forwardNanos(100);
        Assert.assertTrue(cancellableContext.isCancelled());
        Assert.assertThat(cancellableContext.cancellationCause(), IsInstanceOf.instanceOf(TimeoutException.class));
        Status status = Contexts.statusFromCancelled(cancellableContext);
        Assert.assertNotNull(status);
        Assert.assertEquals(Status.Code.DEADLINE_EXCEEDED, status.getCode());
        Assert.assertEquals("context timed out", status.getDescription());
    }

    @Test
    public void statusFromCancelled_returnCancelledIfCauseIsNull() {
        Context.CancellableContext cancellableContext = Context.current().withCancellation();
        cancellableContext.cancel(null);
        Assert.assertTrue(cancellableContext.isCancelled());
        Status status = Contexts.statusFromCancelled(cancellableContext);
        Assert.assertNotNull(status);
        Assert.assertEquals(CANCELLED, status.getCode());
    }

    /**
     * This is a whitebox test, to verify a special case of the implementation.
     */
    @Test
    public void statusFromCancelled_StatusUnknownShouldWork() {
        Context.CancellableContext cancellableContext = Context.current().withCancellation();
        Exception e = UNKNOWN.asException();
        cancellableContext.cancel(e);
        Assert.assertTrue(cancellableContext.isCancelled());
        Status status = Contexts.statusFromCancelled(cancellableContext);
        Assert.assertNotNull(status);
        Assert.assertEquals(Status.Code.UNKNOWN, status.getCode());
        Assert.assertSame(e, status.getCause());
    }

    @Test
    public void statusFromCancelled_shouldThrowIfCtxIsNull() {
        try {
            Contexts.statusFromCancelled(null);
            Assert.fail("NPE expected");
        } catch (NullPointerException npe) {
            Assert.assertEquals("context must not be null", npe.getMessage());
        }
    }
}

