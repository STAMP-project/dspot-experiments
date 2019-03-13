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
package co.paralleluniverse.actors;


import Channels.OverflowPolicy;
import co.paralleluniverse.actors.behaviors.MessageSelector;
import co.paralleluniverse.common.test.TestUtil;
import co.paralleluniverse.common.util.Debug;
import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.FiberForkJoinScheduler;
import co.paralleluniverse.fibers.FiberScheduler;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.Strand;
import co.paralleluniverse.strands.StrandFactoryBuilder;
import co.paralleluniverse.strands.channels.Channel;
import co.paralleluniverse.strands.channels.Channels;
import co.paralleluniverse.strands.channels.SendPort;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;


/**
 *
 *
 * @author pron
 */
public class ActorTest {
    @Rule
    public TestName name = new TestName();

    @Rule
    public TestRule watchman = TestUtil.WATCHMAN;

    static final MailboxConfig mailboxConfig = new MailboxConfig(10, OverflowPolicy.THROW);

    private FiberScheduler scheduler;

    public ActorTest() {
        scheduler = new FiberForkJoinScheduler("test", 4, null, false);
    }

    @Test
    public void whenActorThrowsExceptionThenGetThrowsIt() throws Exception {
        Actor<ActorTest.Message, Integer> actor = spawnActor(new BasicActor<ActorTest.Message, Integer>(ActorTest.mailboxConfig) {
            @Override
            protected Integer doRun() throws SuspendExecution, InterruptedException {
                throw new RuntimeException("foo");
            }
        });
        try {
            actor.get();
            Assert.fail();
        } catch (ExecutionException e) {
            Assert.assertThat(e.getCause(), CoreMatchers.instanceOf(RuntimeException.class));
            Assert.assertThat(e.getCause().getMessage(), CoreMatchers.is("foo"));
        }
    }

    @Test
    public void whenActorThrowsExceptionThenGetThrowsItThreadActor() throws Exception {
        Actor<ActorTest.Message, Integer> actor = new BasicActor<ActorTest.Message, Integer>(ActorTest.mailboxConfig) {
            @Override
            protected Integer doRun() throws SuspendExecution, InterruptedException {
                throw new RuntimeException("foo");
            }
        };
        actor.spawnThread();
        try {
            actor.get();
            Assert.fail();
        } catch (ExecutionException e) {
            Assert.assertThat(e.getCause(), CoreMatchers.instanceOf(RuntimeException.class));
            Assert.assertThat(e.getCause().getMessage(), CoreMatchers.is("foo"));
        }
    }

    @Test
    public void whenActorReturnsValueThenGetReturnsIt() throws Exception {
        Actor<ActorTest.Message, Integer> actor = spawnActor(new BasicActor<ActorTest.Message, Integer>(ActorTest.mailboxConfig) {
            @Override
            protected Integer doRun() throws SuspendExecution, InterruptedException {
                return 42;
            }
        });
        Assert.assertThat(actor.get(), CoreMatchers.is(42));
    }

    @Test
    public void whenActorReturnsValueThenGetReturnsItThreadActor() throws Exception {
        Actor<ActorTest.Message, Integer> actor = new BasicActor<ActorTest.Message, Integer>(ActorTest.mailboxConfig) {
            @Override
            protected Integer doRun() throws SuspendExecution, InterruptedException {
                return 42;
            }
        };
        actor.spawnThread();
        Assert.assertThat(actor.get(), CoreMatchers.is(42));
    }

    @Test
    public void testReceive() throws Exception {
        ActorRef<ActorTest.Message> actor = spawn();
        actor.send(new ActorTest.Message(15));
        Assert.assertThat(LocalActor.<Integer>get(actor), CoreMatchers.is(15));
    }

    @Test
    public void testReceiveThreadActor() throws Exception {
        ActorRef<ActorTest.Message> actor = spawnThread();
        actor.send(new ActorTest.Message(15));
        Assert.assertThat(LocalActor.<Integer>get(actor), CoreMatchers.is(15));
    }

    @Test
    public void testReceiveAfterSleep() throws Exception {
        ActorRef<ActorTest.Message> actor = spawn();
        actor.send(new ActorTest.Message(25));
        Thread.sleep(200);
        actor.send(new ActorTest.Message(17));
        Assert.assertThat(LocalActor.<Integer>get(actor), CoreMatchers.is(42));
    }

    @Test
    public void testReceiveAfterSleepThreadActor() throws Exception {
        ActorRef<ActorTest.Message> actor = spawnThread();
        actor.send(new ActorTest.Message(25));
        Thread.sleep(200);
        actor.send(new ActorTest.Message(17));
        Assert.assertThat(LocalActor.<Integer>get(actor), CoreMatchers.is(42));
    }

    private class TypedReceiveA {}

    private class TypedReceiveB {}

    @Test
    public void testTypedReceive() throws Exception {
        Actor<Object, List<Object>> actor = spawnActor(new BasicActor<Object, List<Object>>(ActorTest.mailboxConfig) {
            @Override
            protected List<Object> doRun() throws SuspendExecution, InterruptedException {
                List<Object> list = new ArrayList<>();
                list.add(receive(ActorTest.TypedReceiveA.class));
                list.add(receive(ActorTest.TypedReceiveB.class));
                return list;
            }
        });
        final ActorTest.TypedReceiveB typedReceiveB = new ActorTest.TypedReceiveB();
        final ActorTest.TypedReceiveA typedReceiveA = new ActorTest.TypedReceiveA();
        actor.ref().send(typedReceiveB);
        Thread.sleep(2);
        actor.ref().send(typedReceiveA);
        Assert.assertThat(actor.get(500, TimeUnit.MILLISECONDS), CoreMatchers.equalTo(Arrays.asList(typedReceiveA, typedReceiveB)));
    }

    @Test
    public void testSelectiveReceive() throws Exception {
        Actor<ActorTest.ComplexMessage, List<Integer>> actor = spawnActor(new BasicActor<ActorTest.ComplexMessage, List<Integer>>(ActorTest.mailboxConfig) {
            @Override
            protected List<Integer> doRun() throws SuspendExecution, InterruptedException {
                final List<Integer> list = new ArrayList<>();
                for (int i = 0; i < 2; i++) {
                    receive(new MessageProcessor<ActorTest.ComplexMessage, ActorTest.ComplexMessage>() {
                        public ActorTest.ComplexMessage process(ActorTest.ComplexMessage m) throws SuspendExecution, InterruptedException {
                            switch (m.type) {
                                case FOO :
                                    list.add(m.num);
                                    receive(new MessageProcessor<ActorTest.ComplexMessage, ActorTest.ComplexMessage>() {
                                        public ActorTest.ComplexMessage process(ActorTest.ComplexMessage m) throws SuspendExecution, InterruptedException {
                                            switch (m.type) {
                                                case BAZ :
                                                    list.add(m.num);
                                                    return m;
                                                default :
                                                    return null;
                                            }
                                        }
                                    });
                                    return m;
                                case BAR :
                                    list.add(m.num);
                                    return m;
                                case BAZ :
                                    Assert.fail();
                                default :
                                    return null;
                            }
                        }
                    });
                }
                return list;
            }
        });
        actor.ref().send(new ActorTest.ComplexMessage(ActorTest.ComplexMessage.Type.FOO, 1));
        actor.ref().send(new ActorTest.ComplexMessage(ActorTest.ComplexMessage.Type.BAR, 2));
        actor.ref().send(new ActorTest.ComplexMessage(ActorTest.ComplexMessage.Type.BAZ, 3));
        Assert.assertThat(actor.get(), CoreMatchers.equalTo(Arrays.asList(1, 3, 2)));
    }

    @Test
    public void testSelectiveReceiveMsgSelector() throws Exception {
        Actor<Object, String> actor = spawnActor(new BasicActor<Object, String>(ActorTest.mailboxConfig) {
            @Override
            protected String doRun() throws SuspendExecution, InterruptedException {
                return receive(MessageSelector.select().ofType(String.class));
            }
        });
        actor.ref().send(1);
        actor.ref().send("hello");
        Assert.assertThat(actor.get(), CoreMatchers.equalTo("hello"));
    }

    @Test
    public void testNestedSelectiveWithEqualMessage() throws Exception {
        Actor<String, String> actor = spawnActor(new BasicActor<String, String>(ActorTest.mailboxConfig) {
            @Override
            protected String doRun() throws SuspendExecution, InterruptedException {
                // return receive(a -> a + receive(b -> b));
                return receive(new MessageProcessor<String, String>() {
                    @Override
                    public String process(String m1) throws SuspendExecution, InterruptedException {
                        return m1 + (receive(new MessageProcessor<String, String>() {
                            @Override
                            public String process(String m2) throws SuspendExecution, InterruptedException {
                                return m2;
                            }
                        }));
                    }
                });
            }
        });
        String msg = "a";
        actor.ref().send(msg);
        actor.ref().send(msg);
        Assert.assertThat(actor.get(), CoreMatchers.equalTo("aa"));
    }

    @Test
    public void whenLinkedActorDiesDuringSelectiveReceiveThenReceiverDies() throws Exception {
        final Actor<ActorTest.Message, Void> a = spawnActor(new BasicActor<ActorTest.Message, Void>(ActorTest.mailboxConfig) {
            @Override
            protected Void doRun() throws SuspendExecution, InterruptedException {
                // noinspection InfiniteLoopStatement
                for (; System.out.println(receive()););
            }
        });
        final Actor<Object, Void> m = spawnActor(new BasicActor<Object, Void>(ActorTest.mailboxConfig) {
            @Override
            protected Void doRun() throws SuspendExecution, InterruptedException {
                link(a.ref());
                receive(MessageSelector.select().ofType(String.class));
                // noinspection StatementWithEmptyBody
                for (; (receive(100, TimeUnit.MILLISECONDS)) instanceof Integer;);
                return null;
            }
        });
        try {
            a.getStrand().interrupt();
            m.ref().send(1);
            m.ref().send("hello");
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
    public void whenWatchedActorDiesDuringSelectiveReceiveThenExitMessageDeferred() throws Exception {
        final Actor<ActorTest.Message, Void> a = spawnActor(new BasicActor<ActorTest.Message, Void>(ActorTest.mailboxConfig) {
            @Override
            protected Void doRun() throws SuspendExecution, InterruptedException {
                // noinspection InfiniteLoopStatement
                for (; System.out.println(receive()););
            }
        });
        final AtomicReference<ExitMessage> emr = new AtomicReference<>();
        final Actor<Object, Object[]> m = spawnActor(new BasicActor<Object, Object[]>(ActorTest.mailboxConfig) {
            private Object watch;

            @Override
            protected Object[] doRun() throws SuspendExecution, InterruptedException {
                watch = watch(a.ref());
                final String msg = receive(MessageSelector.select().ofType(String.class));
                Object o;
                // noinspection StatementWithEmptyBody
                for (; (o = receive(100, TimeUnit.MILLISECONDS)) instanceof Integer;);
                return new Object[]{ watch, msg, o };
            }

            @Override
            protected Object handleLifecycleMessage(LifecycleMessage m) {
                if (m instanceof ExitMessage) {
                    final ExitMessage em = ((ExitMessage) (m));
                    if ((watch.equals(em.watch)) && (em.actor.equals(a.ref())))
                        emr.set(em);

                }
                return super.handleLifecycleMessage(m);
            }
        });
        try {
            a.getStrand().interrupt();
            m.ref().send(1);
            m.ref().send("hello");
            final Object[] res = m.get();
            Assert.assertNotNull(res[0]);
            Assert.assertNotNull(res[1]);
            Assert.assertNull(res[2]);
            Assert.assertNotNull(emr.get());
            Assert.assertEquals(res[1], "hello");
            Assert.assertEquals(res[0], emr.get().watch);
            Assert.assertEquals(a.ref(), emr.get().actor);
            Assert.assertNotNull(emr.get().cause);
            Assert.assertEquals(emr.get().cause.getClass(), InterruptedException.class);
        } catch (final Throwable t) {
            Assert.fail();
        }
    }

    @Test
    public void whenSimpleReceiveAndTimeoutThenReturnNull() throws Exception {
        Actor<ActorTest.Message, Void> actor = spawnActor(new BasicActor<ActorTest.Message, Void>(ActorTest.mailboxConfig) {
            @Override
            protected Void doRun() throws SuspendExecution, InterruptedException {
                ActorTest.Message m;
                m = receive(100, TimeUnit.MILLISECONDS);
                Assert.assertThat(m.num, CoreMatchers.is(1));
                m = receive(100, TimeUnit.MILLISECONDS);
                Assert.assertThat(m.num, CoreMatchers.is(2));
                m = receive(100, TimeUnit.MILLISECONDS);
                Assert.assertThat(m, CoreMatchers.is(CoreMatchers.nullValue()));
                return null;
            }
        });
        actor.ref().send(new ActorTest.Message(1));
        Thread.sleep(20);
        actor.ref().send(new ActorTest.Message(2));
        Thread.sleep(200);
        actor.ref().send(new ActorTest.Message(3));
        actor.join();
    }

    @Test
    public void testTimeoutException() throws Exception {
        Actor<ActorTest.Message, Void> actor = spawnActor(new BasicActor<ActorTest.Message, Void>(ActorTest.mailboxConfig) {
            @Override
            protected Void doRun() throws SuspendExecution, InterruptedException {
                try {
                    receive(100, TimeUnit.MILLISECONDS, new MessageProcessor<ActorTest.Message, ActorTest.Message>() {
                        public ActorTest.Message process(ActorTest.Message m) throws SuspendExecution, InterruptedException {
                            Assert.fail();
                            return m;
                        }
                    });
                    Assert.fail();
                } catch (TimeoutException e) {
                }
                return null;
            }
        });
        Thread.sleep(150);
        actor.ref().send(new ActorTest.Message(1));
        actor.join();
    }

    @Test
    public void testSendSync() throws Exception {
        final Actor<ActorTest.Message, Void> actor1 = spawnActor(new BasicActor<ActorTest.Message, Void>(ActorTest.mailboxConfig) {
            @Override
            protected Void doRun() throws SuspendExecution, InterruptedException {
                ActorTest.Message m;
                m = receive();
                Assert.assertThat(m.num, CoreMatchers.is(1));
                m = receive();
                Assert.assertThat(m.num, CoreMatchers.is(2));
                m = receive();
                Assert.assertThat(m.num, CoreMatchers.is(3));
                return null;
            }
        });
        final Actor<ActorTest.Message, Void> actor2 = spawnActor(new BasicActor<ActorTest.Message, Void>(ActorTest.mailboxConfig) {
            @Override
            protected Void doRun() throws SuspendExecution, InterruptedException {
                Fiber.sleep(20);
                actor1.ref().send(new ActorTest.Message(1));
                Fiber.sleep(10);
                actor1.sendSync(new ActorTest.Message(2));
                actor1.ref().send(new ActorTest.Message(3));
                return null;
            }
        });
        actor1.join();
        actor2.join();
    }

    @Test
    public void testLink() throws Exception {
        Actor<ActorTest.Message, Void> actor1 = spawnActor(new BasicActor<ActorTest.Message, Void>(ActorTest.mailboxConfig) {
            @Override
            protected Void doRun() throws SuspendExecution, InterruptedException {
                Fiber.sleep(100);
                return null;
            }
        });
        Actor<ActorTest.Message, Void> actor2 = spawnActor(new BasicActor<ActorTest.Message, Void>(ActorTest.mailboxConfig) {
            @Override
            protected Void doRun() throws SuspendExecution, InterruptedException {
                try {
                    for (; ;) {
                        receive();
                    }
                } catch (LifecycleException e) {
                }
                return null;
            }
        });
        actor1.link(actor2.ref());
        actor1.join();
        actor2.join();
    }

    @Test
    public void whenUnlinkedAfterDeathButBeforeReceiveThenExitMessageIgnored() throws Exception {
        final Channel<Object> sync1 = Channels.newChannel(1);
        final Channel<Object> sync2 = Channels.newChannel(1);
        final Object ping = new Object();
        final Actor<ActorTest.Message, Void> actor1 = spawnActor(new BasicActor<ActorTest.Message, Void>(ActorTest.mailboxConfig) {
            @Override
            protected final Void doRun() throws SuspendExecution, InterruptedException {
                sync1.receive();
                return null;
            }
        });
        final Actor<ActorTest.Message, Void> actor2 = spawnActor(new BasicActor<ActorTest.Message, Void>(ActorTest.mailboxConfig) {
            @Override
            protected final Void doRun() throws SuspendExecution, InterruptedException {
                try {
                    sync2.receive();
                    tryReceive();
                } catch (final LifecycleException e) {
                    Assert.fail();
                }
                return null;
            }
        });
        actor1.link(actor2.ref());// Link actor 1 and 2

        sync1.send(ping);
        // Let actor 1 go ahead
        actor1.join();
        // Wait for actor 1 to terminate
        actor1.unlink(actor2.ref());// Unlink actor 1 and 2

        sync2.send(ping);
        // Let actor 2 go ahead and check the mailbox
        actor2.join();// Wait for actor 2 to terminate

    }

    @Test
    public void testWatch() throws Exception {
        Actor<ActorTest.Message, Void> actor1 = spawnActor(new BasicActor<ActorTest.Message, Void>(ActorTest.mailboxConfig) {
            @Override
            protected Void doRun() throws SuspendExecution, InterruptedException {
                Fiber.sleep(100);
                return null;
            }
        });
        final AtomicBoolean handlerCalled = new AtomicBoolean(false);
        Actor<ActorTest.Message, Void> actor2 = spawnActor(new BasicActor<ActorTest.Message, Void>(ActorTest.mailboxConfig) {
            @Override
            protected Void doRun() throws SuspendExecution, InterruptedException {
                ActorTest.Message m = receive(200, TimeUnit.MILLISECONDS);
                Assert.assertThat(m, CoreMatchers.is(CoreMatchers.nullValue()));
                return null;
            }

            @Override
            protected ActorTest.Message handleLifecycleMessage(LifecycleMessage m) {
                super.handleLifecycleMessage(m);
                handlerCalled.set(true);
                return null;
            }
        });
        actor2.watch(actor1.ref());
        actor1.join();
        actor2.join();
        Assert.assertThat(handlerCalled.get(), CoreMatchers.is(true));
    }

    @Test
    public void whenUnwatchedAfterDeathButBeforeReceiveThenExitMessageIgnored() throws Exception {
        final Channel<Object> sync1 = Channels.newChannel(1);
        final Channel<Object> sync2 = Channels.newChannel(1);
        final Object ping = new Object();
        final Actor<ActorTest.Message, Void> actor1 = spawnActor(new BasicActor<ActorTest.Message, Void>(ActorTest.mailboxConfig) {
            @Override
            protected final Void doRun() throws SuspendExecution, InterruptedException {
                sync1.receive();
                return null;
            }
        });
        final AtomicBoolean handlerCalled = new AtomicBoolean(false);
        final Actor<ActorTest.Message, Void> actor2 = spawnActor(new BasicActor<ActorTest.Message, Void>(ActorTest.mailboxConfig) {
            @Override
            protected final Void doRun() throws SuspendExecution, InterruptedException {
                sync2.receive();
                final ActorTest.Message m = receive(200, TimeUnit.MILLISECONDS);
                Assert.assertThat(m, CoreMatchers.is(CoreMatchers.nullValue()));
                return null;
            }

            @Override
            protected final ActorTest.Message handleLifecycleMessage(LifecycleMessage m) {
                super.handleLifecycleMessage(m);
                handlerCalled.set(true);
                return null;
            }
        });
        final Object watchId = actor1.watch(actor2.ref());// Watch actor 2

        sync1.send(ping);
        // Let actor 1 go ahead
        actor1.join();
        // Wait for actor 1 to terminate
        actor1.unwatch(actor2.ref(), watchId);
        // Unwatch actor 2
        sync2.send(ping);
        // Let actor 2 go ahead and check the mailbox
        actor2.join();
        // Wait for actor 2 to terminate
        Assert.assertThat(handlerCalled.get(), CoreMatchers.is(false));
    }

    @Test
    public void testWatchGC() throws Exception {
        Assume.assumeFalse(Debug.isDebug());
        final Actor<ActorTest.Message, Void> actor = spawnActor(new BasicActor<ActorTest.Message, Void>(ActorTest.mailboxConfig) {
            @Override
            protected Void doRun() throws SuspendExecution, InterruptedException {
                Fiber.sleep(120000);
                return null;
            }
        });
        System.out.println(("actor1 is " + actor));
        WeakReference wrActor2 = new WeakReference(spawnActor(new BasicActor<ActorTest.Message, Void>(ActorTest.mailboxConfig) {
            @Override
            protected Void doRun() throws SuspendExecution, InterruptedException {
                Fiber.sleep(10);
                final Object watch = watch(actor.ref());
                // unwatch(actor, watch);
                return null;
            }
        }));
        System.out.println(("actor2 is " + (wrActor2.get())));
        for (int i = 0; i < 10; i++) {
            Thread.sleep(10);
            System.gc();
        }
        Thread.sleep(2000);
        Assert.assertEquals(null, wrActor2.get());
    }

    @Test
    public void transformingSendChannelIsEqualToActor() throws Exception {
        final ActorRef<Integer> actor = spawn();
        SendPort<Integer> ch1 = Channels.filterSend(actor, new Predicate<Integer>() {
            @Override
            public boolean apply(Integer input) {
                return (input % 2) == 0;
            }
        });
        SendPort<Integer> ch2 = Channels.mapSend(actor, new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer input) {
                return input + 10;
            }
        });
        Assert.assertTrue(ch1.equals(actor));
        Assert.assertTrue(actor.equals(ch1));
        Assert.assertTrue(ch2.equals(actor));
        Assert.assertTrue(actor.equals(ch2));
    }

    @Test
    public void testSpawnWithStrandFactory() throws Exception {
        final AtomicBoolean run = new AtomicBoolean(false);
        Actor<ActorTest.Message, Integer> actor = new BasicActor<ActorTest.Message, Integer>(ActorTest.mailboxConfig) {
            @Override
            protected Integer doRun() throws SuspendExecution, InterruptedException {
                run.set(true);
                return 3;
            }
        };
        ActorRef a = actor.spawn(new StrandFactoryBuilder().setFiber(null).setNameFormat("my-fiber-%d").build());
        Strand s = LocalActor.getStrand(a);
        Assert.assertTrue(s.isFiber());
        Assert.assertThat(s.getName(), CoreMatchers.equalTo("my-fiber-0"));
        Assert.assertThat(((Integer) (LocalActor.get(a))), CoreMatchers.equalTo(3));
        Assert.assertThat(run.get(), CoreMatchers.is(true));
        run.set(false);
        actor = new BasicActor<ActorTest.Message, Integer>(ActorTest.mailboxConfig) {
            @Override
            protected Integer doRun() throws SuspendExecution, InterruptedException {
                run.set(true);
                return 3;
            }
        };
        a = actor.spawn(new StrandFactoryBuilder().setThread(false).setNameFormat("my-thread-%d").build());
        s = LocalActor.getStrand(a);
        Assert.assertTrue((!(s.isFiber())));
        Assert.assertThat(s.getName(), CoreMatchers.equalTo("my-thread-0"));
        LocalActor.join(a);
        Assert.assertThat(run.get(), CoreMatchers.is(true));
        run.set(false);
        Actor<ActorTest.Message, Integer> actor2 = new BasicActor<ActorTest.Message, Integer>("coolactor", ActorTest.mailboxConfig) {
            @Override
            protected Integer doRun() throws SuspendExecution, InterruptedException {
                run.set(true);
                return 3;
            }
        };
        a = actor2.spawn(new StrandFactoryBuilder().setFiber(null).setNameFormat("my-fiber-%d").build());
        s = LocalActor.getStrand(a);
        Assert.assertTrue(s.isFiber());
        Assert.assertThat(s.getName(), CoreMatchers.equalTo("coolactor"));
        Assert.assertThat(((Integer) (LocalActor.get(a))), CoreMatchers.equalTo(3));
        Assert.assertThat(run.get(), CoreMatchers.is(true));
        run.set(false);
        actor2 = new BasicActor<ActorTest.Message, Integer>("coolactor", ActorTest.mailboxConfig) {
            @Override
            protected Integer doRun() throws SuspendExecution, InterruptedException {
                run.set(true);
                return 3;
            }
        };
        a = actor2.spawn(new StrandFactoryBuilder().setThread(false).setNameFormat("my-thread-%d").build());
        s = LocalActor.getStrand(a);
        Assert.assertTrue((!(s.isFiber())));
        Assert.assertThat(s.getName(), CoreMatchers.equalTo("coolactor"));
        LocalActor.join(a);
        Assert.assertThat(run.get(), CoreMatchers.is(true));
        run.set(false);
    }

    static class Message {
        final int num;

        public Message(int num) {
            this.num = num;
        }
    }

    static class ComplexMessage {
        enum Type {

            FOO,
            BAR,
            BAZ,
            WAT;}

        final ActorTest.ComplexMessage.Type type;

        final int num;

        public ComplexMessage(ActorTest.ComplexMessage.Type type, int num) {
            this.type = type;
            this.num = num;
        }
    }
}

