/**
 * Quasar: lightweight threads and actors for the JVM.
 * Copyright (c) 2013-2016, Parallel Universe Software Co. All rights reserved.
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
import co.paralleluniverse.actors.ExitMessage;
import co.paralleluniverse.actors.LifecycleException;
import co.paralleluniverse.actors.MailboxConfig;
import co.paralleluniverse.common.test.TestUtil;
import co.paralleluniverse.fibers.FiberFactory;
import co.paralleluniverse.fibers.FiberForkJoinScheduler;
import co.paralleluniverse.fibers.FiberScheduler;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.Strand;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * These tests are also good tests for sendSync, as they test sendSync (and receive) from both fibers and threads.
 *
 * @author pron
 */
public class ServerTest {
    @Rule
    public TestName name = new TestName();

    @Rule
    public TestRule watchman = TestUtil.WATCHMAN;

    static final MailboxConfig mailboxConfig = new MailboxConfig(10, OverflowPolicy.THROW);

    private FiberScheduler scheduler;

    private FiberFactory factory;

    public ServerTest() {
        factory = scheduler = new FiberForkJoinScheduler("test", 4, null, false);
    }

    @Test
    public void whenServerStartsThenInitIsCalled() throws Exception {
        final ServerHandler<ServerTest.Message, Integer, ServerTest.Message> server = Mockito.mock(ServerHandler.class);
        Server<ServerTest.Message, Integer, ServerTest.Message> gs = spawnServer(server);
        try {
            LocalActor.join(gs, 100, TimeUnit.MILLISECONDS);
            Assert.fail("actor died");
        } catch (TimeoutException e) {
        }
        Mockito.verify(server).init();
    }

    @Test
    public void whenShutdownIsCalledInInitThenServerStops() throws Exception {
        Server<ServerTest.Message, Integer, ServerTest.Message> gs = spawnServer(new AbstractServerHandler<ServerTest.Message, Integer, ServerTest.Message>() {
            @Override
            public void init() {
                shutdown();
            }
        });
        LocalActor.join(gs, 100, TimeUnit.MILLISECONDS);
    }

    @Test
    public void whenCalledThenResultIsReturned() throws Exception {
        final Server<ServerTest.Message, Integer, ServerTest.Message> s = spawnServer(new AbstractServerHandler<ServerTest.Message, Integer, ServerTest.Message>() {
            @Override
            public Integer handleCall(ActorRef<?> from, Object id, ServerTest.Message m) {
                shutdown();
                return (m.a) + (m.b);
            }
        });
        Actor<ServerTest.Message, Integer> actor = spawnActor(new BasicActor<ServerTest.Message, Integer>(ServerTest.mailboxConfig) {
            protected Integer doRun() throws SuspendExecution, InterruptedException {
                return s.call(new ServerTest.Message(3, 4));
            }
        });
        int res = actor.get();
        Assert.assertThat(res, CoreMatchers.is(7));
        LocalActor.join(s, 100, TimeUnit.MILLISECONDS);
    }

    @Test
    public void whenCalledFromThreadThenResultIsReturned() throws Exception {
        Server<ServerTest.Message, Integer, ServerTest.Message> s = spawnServer(new AbstractServerHandler<ServerTest.Message, Integer, ServerTest.Message>() {
            @Override
            public Integer handleCall(ActorRef<?> from, Object id, ServerTest.Message m) {
                shutdown();
                return (m.a) + (m.b);
            }
        });
        int res = s.call(new ServerTest.Message(3, 4));
        Assert.assertThat(res, CoreMatchers.is(7));
        LocalActor.join(s, 100, TimeUnit.MILLISECONDS);
    }

    @Test
    public void whenCalledAndTimeoutThenThrowTimeout() throws Exception {
        Server<ServerTest.Message, Integer, ServerTest.Message> s = spawnServer(new AbstractServerHandler<ServerTest.Message, Integer, ServerTest.Message>() {
            @Override
            public Integer handleCall(ActorRef<?> from, Object id, ServerTest.Message m) throws SuspendExecution {
                try {
                    Strand.sleep(50);
                    shutdown();
                    return (m.a) + (m.b);
                } catch (InterruptedException ex) {
                    System.out.println(("?????: " + (Arrays.toString(ex.getStackTrace()))));
                    return 40;
                }
            }
        });
        try {
            int res = s.call(new ServerTest.Message(3, 4), 10, TimeUnit.MILLISECONDS);
            Assert.fail(("res: " + res));
        } catch (TimeoutException e) {
        }
        LocalActor.join(s, 100, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testDefaultTimeout1() throws Exception {
        Server<ServerTest.Message, Integer, ServerTest.Message> s = spawnServer(new AbstractServerHandler<ServerTest.Message, Integer, ServerTest.Message>() {
            @Override
            public Integer handleCall(ActorRef<?> from, Object id, ServerTest.Message m) throws SuspendExecution {
                try {
                    Strand.sleep(50);
                    shutdown();
                    return (m.a) + (m.b);
                } catch (InterruptedException ex) {
                    System.out.println(("?????: " + (Arrays.toString(ex.getStackTrace()))));
                    return 40;
                }
            }
        });
        s.setDefaultTimeout(10, TimeUnit.MILLISECONDS);
        try {
            int res = s.call(new ServerTest.Message(3, 4));
            Assert.fail(("res: " + res));
        } catch (RuntimeException e) {
            Assert.assertThat(e.getCause(), CoreMatchers.instanceOf(TimeoutException.class));
        }
        LocalActor.join(s, 100, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testDefaultTimeout2() throws Exception {
        Server<ServerTest.Message, Integer, ServerTest.Message> s = spawnServer(new AbstractServerHandler<ServerTest.Message, Integer, ServerTest.Message>() {
            @Override
            public Integer handleCall(ActorRef<?> from, Object id, ServerTest.Message m) throws SuspendExecution {
                try {
                    Strand.sleep(50);
                    shutdown();
                    return (m.a) + (m.b);
                } catch (InterruptedException ex) {
                    System.out.println(("?????: " + (Arrays.toString(ex.getStackTrace()))));
                    return 40;
                }
            }
        });
        s.setDefaultTimeout(100, TimeUnit.MILLISECONDS);
        int res = s.call(new ServerTest.Message(3, 4));
        Assert.assertThat(res, CoreMatchers.is(7));
        LocalActor.join(s, 100, TimeUnit.MILLISECONDS);
    }

    @Test
    public void whenHandleCallThrowsExceptionThenItPropagatesToCaller() throws Exception {
        final ServerHandler<ServerTest.Message, Integer, ServerTest.Message> server = Mockito.mock(ServerHandler.class);
        Mockito.when(server.handleCall(ArgumentMatchers.any(co.paralleluniverse.actors.ActorRef.class), ArgumentMatchers.any(), ArgumentMatchers.any(ServerTest.Message.class))).thenThrow(new RuntimeException("my exception"));
        final Server<ServerTest.Message, Integer, ServerTest.Message> s = spawnServer(server);
        Actor<ServerTest.Message, Void> actor = spawnActor(new BasicActor<ServerTest.Message, Void>(ServerTest.mailboxConfig) {
            protected Void doRun() throws SuspendExecution, InterruptedException {
                try {
                    int res = s.call(new ServerTest.Message(3, 4));
                    Assert.fail();
                } catch (RuntimeException e) {
                    Assert.assertThat(e.getMessage(), CoreMatchers.equalTo("my exception"));
                }
                return null;
            }
        });
        actor.join();
        try {
            LocalActor.join(s, 100, TimeUnit.MILLISECONDS);
            Assert.fail("actor died");
        } catch (TimeoutException e) {
        }
    }

    @Test
    public void whenHandleCallThrowsExceptionThenItPropagatesToThreadCaller() throws Exception {
        final ServerHandler<ServerTest.Message, Integer, ServerTest.Message> server = Mockito.mock(ServerHandler.class);
        Mockito.when(server.handleCall(ArgumentMatchers.any(co.paralleluniverse.actors.ActorRef.class), ArgumentMatchers.any(), ArgumentMatchers.any(ServerTest.Message.class))).thenThrow(new RuntimeException("my exception"));
        final Server<ServerTest.Message, Integer, ServerTest.Message> s = spawnServer(server);
        try {
            int res = s.call(new ServerTest.Message(3, 4));
            Assert.fail();
        } catch (RuntimeException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.equalTo("my exception"));
        }
        try {
            LocalActor.join(s, 100, TimeUnit.MILLISECONDS);
            Assert.fail("actor died");
        } catch (TimeoutException e) {
        }
    }

    @Test
    public void whenActorDiesThenCausePropagatesToThreadCaller() throws Exception {
        final ServerHandler<ServerTest.Message, Integer, ServerTest.Message> server = Mockito.mock(ServerHandler.class);
        Mockito.doThrow(new RuntimeException("my exception")).when(server).init();
        final Server<ServerTest.Message, Integer, ServerTest.Message> s = spawnServer(server);
        try {
            int res = s.call(new ServerTest.Message(3, 4));
            Assert.fail();
        } catch (RuntimeException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.equalTo("my exception"));
        }
        try {
            LocalActor.join(s, 100, TimeUnit.MILLISECONDS);
            Assert.fail();
        } catch (ExecutionException e) {
            Assert.assertThat(e.getCause().getMessage(), CoreMatchers.equalTo("my exception"));
        }
    }

    @Test
    public void whenLinkedActorDiesDuringCallThenCallerDies() throws Exception {
        final Actor<ServerTest.Message, Void> a = spawnActor(new BasicActor<ServerTest.Message, Void>(ServerTest.mailboxConfig) {
            @Override
            protected Void doRun() throws SuspendExecution, InterruptedException {
                // noinspection InfiniteLoopStatement
                for (; System.out.println(receive()););
            }
        });
        final Server<ServerTest.Message, Integer, ServerTest.Message> s = spawnServer(new AbstractServerHandler<ServerTest.Message, Integer, ServerTest.Message>() {
            @Override
            public Integer handleCall(ActorRef<?> from, Object id, ServerTest.Message m) throws SuspendExecution {
                try {
                    a.getStrand().interrupt();
                    Strand.sleep(500);
                } catch (InterruptedException ex) {
                    System.out.println(("?????: " + (Arrays.toString(ex.getStackTrace()))));
                }
                return 0;
            }
        });
        final Actor<ServerTest.Message, Void> m = spawnActor(new BasicActor<ServerTest.Message, Void>(ServerTest.mailboxConfig) {
            @Override
            protected Void doRun() throws SuspendExecution, InterruptedException {
                link(a.ref());
                s.call(new ServerTest.Message(3, 4));
                return null;
            }
        });
        try {
            m.join();
            Assert.fail();
        } catch (final ExecutionException e) {
            final Throwable cause = e.getCause();
            Assert.assertEquals(cause.getClass(), LifecycleException.class);
            final LifecycleException lce = ((LifecycleException) (cause));
            Assert.assertEquals(lce.message().getClass(), ExitMessage.class);
            final ExitMessage em = ((ExitMessage) (lce.message()));
            Assert.assertEquals(em.getCause().getClass(), InterruptedException.class);
            Assert.assertNull(em.watch);
            Assert.assertEquals(em.actor, a.ref());
        }
    }

    @Test
    public void whenWatchedActorDiesDuringCallThenExitMessageDeferred() throws Exception {
        final Actor<ServerTest.Message, Void> a = spawnActor(new BasicActor<ServerTest.Message, Void>(ServerTest.mailboxConfig) {
            @Override
            protected Void doRun() throws SuspendExecution, InterruptedException {
                // noinspection InfiniteLoopStatement
                for (; System.out.println(receive()););
            }
        });
        final Server<ServerTest.Message, Integer, ServerTest.Message> s = spawnServer(new AbstractServerHandler<ServerTest.Message, Integer, ServerTest.Message>() {
            @Override
            public Integer handleCall(ActorRef<?> from, Object id, ServerTest.Message m) throws SuspendExecution {
                try {
                    a.getStrand().interrupt();
                    Strand.sleep(500);
                } catch (InterruptedException ex) {
                    System.out.println(("?????: " + (Arrays.toString(ex.getStackTrace()))));
                }
                return 0;
            }
        });
        final AtomicReference<ExitMessage> emr = new AtomicReference<>();
        final Actor<ServerTest.Message, Object[]> m = spawnActor(new BasicActor<ServerTest.Message, Object[]>(ServerTest.mailboxConfig) {
            private Object watch;

            @Override
            protected Object[] doRun() throws SuspendExecution, InterruptedException {
                return new Object[]{ watch = watch(a.ref()), s.call(new ServerTest.Message(3, 4)), receive(100, TimeUnit.MILLISECONDS) };
            }

            @Override
            protected ServerTest.Message handleLifecycleMessage(LifecycleMessage m) {
                if (m instanceof ExitMessage) {
                    final ExitMessage em = ((ExitMessage) (m));
                    if ((watch.equals(em.watch)) && (em.actor.equals(a.ref())))
                        emr.set(em);

                }
                return super.handleLifecycleMessage(m);
            }
        });
        try {
            final Object[] res = m.get();
            Assert.assertNotNull(res[0]);
            Assert.assertNotNull(res[1]);
            Assert.assertNull(res[2]);
            Assert.assertNotNull(emr.get());
            Assert.assertEquals(res[1], 0);
            Assert.assertEquals(res[0], emr.get().watch);
            Assert.assertEquals(a.ref(), emr.get().actor);
            Assert.assertNotNull(emr.get().cause);
            Assert.assertEquals(emr.get().cause.getClass(), InterruptedException.class);
        } catch (final Throwable t) {
            Assert.fail();
        }
    }

    @Test
    public void whenTimeoutThenHandleTimeoutIsCalled() throws Exception {
        final AtomicInteger counter = new AtomicInteger(0);
        ServerActor<ServerTest.Message, Integer, ServerTest.Message> s = spawnActor(new ServerActor<ServerTest.Message, Integer, ServerTest.Message>() {
            @Override
            protected void init() {
                setTimeout(20, TimeUnit.MILLISECONDS);
            }

            @Override
            protected void handleTimeout() {
                counter.incrementAndGet();
                if ((counter.get()) >= 5)
                    shutdown();

            }
        });
        s.join(500, TimeUnit.MILLISECONDS);// should be enough

        Assert.assertThat(counter.get(), CoreMatchers.is(5));
    }

    @Test
    public void whenCalledThenDeferredResultIsReturned() throws Exception {
        final Server<ServerTest.Message, Integer, ServerTest.Message> s = spawn();
        Actor<ServerTest.Message, Integer> actor = spawnActor(new BasicActor<ServerTest.Message, Integer>(ServerTest.mailboxConfig) {
            protected Integer doRun() throws SuspendExecution, InterruptedException {
                return s.call(new ServerTest.Message(3, 4));
            }
        });
        int res = actor.get();
        Assert.assertThat(res, CoreMatchers.is(7));
        LocalActor.join(s, 100, TimeUnit.MILLISECONDS);
    }

    @Test
    public void whenCalledFromThreadThenDeferredResultIsReturned() throws Exception {
        final Server<ServerTest.Message, Integer, ServerTest.Message> s = spawn();
        int res = s.call(new ServerTest.Message(3, 4));
        Assert.assertThat(res, CoreMatchers.is(7));
        LocalActor.join(s, 100, TimeUnit.MILLISECONDS);
    }

    @Test
    public void whenActorDiesDuringDeferredHandlingThenCausePropagatesToThreadCaller() throws Exception {
        final Server<ServerTest.Message, Integer, ServerTest.Message> s = spawn();
        try {
            int res = s.call(new ServerTest.Message(3, 4));
            Assert.fail();
        } catch (RuntimeException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.equalTo("my exception"));
        }
        try {
            LocalActor.join(s, 100, TimeUnit.MILLISECONDS);
            Assert.fail();
        } catch (ExecutionException e) {
            Assert.assertThat(e.getCause().getMessage(), CoreMatchers.equalTo("my exception"));
        }
    }

    @Test
    public void whenCastThenHandleCastIsCalled() throws Exception {
        final ServerHandler<ServerTest.Message, Integer, ServerTest.Message> server = Mockito.mock(ServerHandler.class);
        final Server<ServerTest.Message, Integer, ServerTest.Message> s = spawnServer(server);
        s.cast(new ServerTest.Message(3, 4));
        s.shutdown();
        LocalActor.join(s);
        Mockito.verify(server).handleCast(ArgumentMatchers.isNull(), ArgumentMatchers.any(), ArgumentMatchers.eq(new ServerTest.Message(3, 4)));
    }

    @Test
    public void whenSentMessageHandleInfoIsCalled() throws Exception {
        final ServerHandler<ServerTest.Message, Integer, ServerTest.Message> server = Mockito.mock(ServerHandler.class);
        final Server<ServerTest.Message, Integer, ServerTest.Message> s = spawnServer(server);
        s.send("foo");
        s.shutdown();
        LocalActor.join(s);
        Mockito.verify(server).handleInfo("foo");
    }

    @Test
    public void whenSentShutdownThenTerminateIsCalledAndServerStopped() throws Exception {
        final ServerHandler<ServerTest.Message, Integer, ServerTest.Message> server = Mockito.mock(ServerHandler.class);
        final Server<ServerTest.Message, Integer, ServerTest.Message> s = spawnServer(server);
        s.shutdown();
        LocalActor.join(s);
        Mockito.verify(server).terminate(null);
    }

    @Test
    public void whenHandleInfoThrowsExceptionThenTerminateIsCalled() throws Exception {
        final ServerHandler<ServerTest.Message, Integer, ServerTest.Message> server = Mockito.mock(ServerHandler.class);
        final Exception myException = new RuntimeException("my exception");
        Mockito.doThrow(myException).when(server).handleInfo(ArgumentMatchers.any());
        final Server<ServerTest.Message, Integer, ServerTest.Message> s = spawnServer(server);
        s.send("foo");
        try {
            LocalActor.join(s);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertThat(e.getCause().getMessage(), CoreMatchers.equalTo("my exception"));
        }
        Mockito.verify(server).terminate(myException);
    }

    @Test
    public void testRegistration() throws Exception {
        Server<ServerTest.Message, Integer, ServerTest.Message> s = spawn();
        Assert.assertTrue((s == ((Server) (ActorRegistry.getActor("my-server")))));
    }

    static class Message {
        final int a;

        final int b;

        public Message(int a, int b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = (43 * hash) + (this.a);
            hash = (43 * hash) + (this.b);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null)
                return false;

            if ((getClass()) != (obj.getClass()))
                return false;

            final ServerTest.Message other = ((ServerTest.Message) (obj));
            if ((this.a) != (other.a))
                return false;

            if ((this.b) != (other.b))
                return false;

            return true;
        }
    }
}

