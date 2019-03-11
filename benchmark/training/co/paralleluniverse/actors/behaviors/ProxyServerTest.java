/**
 * Quasar: lightweight threads and actors for the JVM.
 * Copyright (c) 2013-2015, Parallel Universe Software Co. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 3.0
 * as published by the Free Software Foundation.
 */
package co.paralleluniverse.actors.behaviors;


import Channels.OverflowPolicy;
import co.paralleluniverse.actors.Actor;
import co.paralleluniverse.actors.ActorRegistry;
import co.paralleluniverse.actors.LocalActor;
import co.paralleluniverse.actors.MailboxConfig;
import co.paralleluniverse.common.test.TestUtil;
import co.paralleluniverse.fibers.FiberFactory;
import co.paralleluniverse.fibers.FiberForkJoinScheduler;
import co.paralleluniverse.fibers.FiberScheduler;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.Suspendable;
import co.paralleluniverse.strands.Strand;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;


/**
 * These tests are also good tests for sendSync, as they test sendSync (and receive) from both fibers and threads.
 *
 * @author pron
 */
public class ProxyServerTest {
    @Rule
    public TestName name = new TestName();

    @Rule
    public TestRule watchman = TestUtil.WATCHMAN;

    static final MailboxConfig mailboxConfig = new MailboxConfig(10, OverflowPolicy.THROW);

    private FiberFactory factory;

    private FiberScheduler scheduler;

    public ProxyServerTest() {
        factory = scheduler = new FiberForkJoinScheduler("test", 4, null, false);
    }

    @Suspendable
    public static interface A {
        int foo(String str, int x);// throws SuspendExecution;


        void bar(int x);// throws SuspendExecution;

    }

    @Test
    public void testShutdown() throws Exception {
        final Server<?, ?, ?> a = spawnServer(false, new ProxyServerTest.A() {
            public int foo(String str, int x) {
                return (str.length()) + x;
            }

            public void bar(int x) {
                throw new UnsupportedOperationException();
            }
        });
        a.shutdown();
        LocalActor.join(a, 100, TimeUnit.MILLISECONDS);
    }

    @Test
    public void whenCalledThenResultIsReturned() throws Exception {
        final Server<?, ?, ?> a = spawnServer(false, new ProxyServerTest.A() {
            @Suspendable
            public int foo(String str, int x) {
                try {
                    Strand.sleep(50);
                    return (str.length()) + x;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (SuspendExecution e) {
                    throw new AssertionError(e);
                }
            }

            public void bar(int x) {
                throw new UnsupportedOperationException();
            }
        });
        Actor<?, Integer> actor = spawnActor(new co.paralleluniverse.actors.BasicActor<Object, Integer>(ProxyServerTest.mailboxConfig) {
            protected Integer doRun() throws SuspendExecution, InterruptedException {
                return ((ProxyServerTest.A) (a)).foo("hello", 2);
            }
        });
        int res = actor.get();
        Assert.assertThat(res, CoreMatchers.is(7));
        a.shutdown();
        LocalActor.join(a, 100, TimeUnit.MILLISECONDS);
    }

    @Test
    public void whenCalledFromThreadThenResultIsReturned() throws Exception {
        final Server<?, ?, ?> a = spawnServer(false, new ProxyServerTest.A() {
            @Suspendable
            public int foo(String str, int x) {
                try {
                    Strand.sleep(50);
                    return (str.length()) + x;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (SuspendExecution e) {
                    throw new AssertionError(e);
                }
            }

            public void bar(int x) {
                throw new UnsupportedOperationException();
            }
        });
        int res = ((ProxyServerTest.A) (a)).foo("hello", 2);
        Assert.assertThat(res, CoreMatchers.is(7));
        ((ProxyServerTest.A) (a)).bar(3);
        res = ((ProxyServerTest.A) (a)).foo("hello", 2);
        Assert.assertThat(res, CoreMatchers.is(7));
        a.shutdown();
        LocalActor.join(a, 100, TimeUnit.MILLISECONDS);
    }

    @Test
    public void whenHandleCallThrowsExceptionThenItPropagatesToCaller() throws Exception {
        final Server<?, ?, ?> a = spawnServer(false, new ProxyServerTest.A() {
            public int foo(String str, int x) {
                throw new RuntimeException("my exception");
            }

            public void bar(int x) {
                throw new UnsupportedOperationException();
            }
        });
        Actor<?, Void> actor = spawnActor(new co.paralleluniverse.actors.BasicActor<Object, Void>(ProxyServerTest.mailboxConfig) {
            protected Void doRun() throws SuspendExecution, InterruptedException {
                try {
                    int res = ((ProxyServerTest.A) (a)).foo("hello", 2);
                    Assert.fail();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    Assert.assertThat(e.getMessage(), CoreMatchers.equalTo("my exception"));
                }
                return null;
            }
        });
        actor.join();
        a.shutdown();
        LocalActor.join(a, 100, TimeUnit.MILLISECONDS);
    }

    @Test
    public void whenHandleCallThrowsExceptionThenItPropagatesToThreadCaller() throws Exception {
        final Server<?, ?, ?> a = spawnServer(false, new ProxyServerTest.A() {
            public int foo(String str, int x) {
                throw new RuntimeException("my exception");
            }

            public void bar(int x) {
                throw new UnsupportedOperationException();
            }
        });
        try {
            int res = ((ProxyServerTest.A) (a)).foo("hello", 2);
            Assert.fail();
        } catch (RuntimeException e) {
            e.printStackTrace();
            Assert.assertThat(e.getMessage(), CoreMatchers.equalTo("my exception"));
        }
        a.shutdown();
        LocalActor.join(a, 100, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testCast() throws Exception {
        final AtomicInteger called = new AtomicInteger(0);
        final Server<?, ?, ?> a = spawnServer(false, new ProxyServerTest.A() {
            public int foo(String str, int x) {
                throw new UnsupportedOperationException();
            }

            @Suspendable
            public void bar(int x) {
                try {
                    Strand.sleep(100);
                    called.set(x);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (SuspendExecution e) {
                    throw new AssertionError(e);
                }
            }
        });
        ((ProxyServerTest.A) (a)).bar(15);
        Assert.assertThat(called.get(), CoreMatchers.is(0));
        Thread.sleep(200);
        Assert.assertThat(called.get(), CoreMatchers.is(15));
        a.shutdown();
        LocalActor.join(a, 100, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testCallOnVoidMethod() throws Exception {
        final AtomicInteger called = new AtomicInteger(0);
        final Server<?, ?, ?> a = spawnServer(true, new ProxyServerTest.A() {
            public int foo(String str, int x) {
                throw new UnsupportedOperationException();
            }

            @Suspendable
            public void bar(int x) {
                try {
                    Strand.sleep(100);
                    called.set(x);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (SuspendExecution e) {
                    throw new AssertionError(e);
                }
            }
        });
        ((ProxyServerTest.A) (a)).bar(15);
        Assert.assertThat(called.get(), CoreMatchers.is(15));
        a.shutdown();
        LocalActor.join(a, 100, TimeUnit.MILLISECONDS);
    }

    @Test
    public void whenCalledAndTimeoutThenThrowTimeout() throws Exception {
        final Server<?, ?, ?> a = spawnServer(false, new ProxyServerTest.A() {
            @Suspendable
            public int foo(String str, int x) {
                try {
                    Strand.sleep(100);
                    return (str.length()) + x;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (SuspendExecution e) {
                    throw new AssertionError(e);
                }
            }

            public void bar(int x) {
                throw new UnsupportedOperationException();
            }
        });
        a.setDefaultTimeout(50, TimeUnit.MILLISECONDS);
        try {
            int res = ((ProxyServerTest.A) (a)).foo("hello", 2);
            Assert.fail(("res: " + res));
        } catch (RuntimeException e) {
            Assert.assertThat(e.getCause(), CoreMatchers.instanceOf(TimeoutException.class));
        }
        a.setDefaultTimeout(200, TimeUnit.MILLISECONDS);
        int res = ((ProxyServerTest.A) (a)).foo("hello", 2);
        Assert.assertThat(res, CoreMatchers.is(7));
        a.shutdown();
        LocalActor.join(a, 500, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testRegistration() throws Exception {
        final Server<?, ?, ?> a = spawn();
        Assert.assertTrue((((ProxyServerTest.A) (a)) == ((ProxyServerTest.A) (ActorRegistry.getActor("test1")))));
    }
}

