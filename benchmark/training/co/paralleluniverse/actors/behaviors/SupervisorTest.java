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


import co.paralleluniverse.actors.Actor;
import co.paralleluniverse.actors.ActorRef;
import co.paralleluniverse.actors.ActorRegistry;
import co.paralleluniverse.actors.ActorSpec;
import co.paralleluniverse.actors.BasicActor;
import co.paralleluniverse.actors.LifecycleMessage;
import co.paralleluniverse.actors.LocalActor;
import co.paralleluniverse.actors.ShutdownMessage;
import co.paralleluniverse.actors.behaviors.Supervisor.ChildMode;
import co.paralleluniverse.actors.behaviors.SupervisorActor.RestartStrategy;
import co.paralleluniverse.common.test.TestUtil;
import co.paralleluniverse.fibers.FiberFactory;
import co.paralleluniverse.fibers.FiberForkJoinScheduler;
import co.paralleluniverse.fibers.FiberScheduler;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.Strand;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * These tests are also good tests for sendSync, as they test sendSync (and receive) from both fibers and threads.
 *
 * @author pron
 */
public class SupervisorTest {
    @Rule
    public TestName name = new TestName();

    @Rule
    public TestRule watchman = TestUtil.WATCHMAN;

    private static final Logger LOG = LoggerFactory.getLogger(SupervisorActor.class);

    static final int mailboxSize = 10;

    private static FiberScheduler scheduler;

    private static FiberFactory factory;

    public SupervisorTest() throws Exception {
        SupervisorTest.factory = SupervisorTest.scheduler = new FiberForkJoinScheduler("test", 4, null, false);
        // java.util.logging.LogManager.getLogManager().readConfiguration(); // gradle messes with the configurations
    }

    private static class Actor1 extends BasicActor<Object, Integer> {
        public Actor1(String name) {
            super(name);
        }

        @Override
        protected Integer doRun() throws SuspendExecution, InterruptedException {
            register();
            int i = 0;
            try {
                for (; ;) {
                    Object m = receive();
                    i++;
                }
            } catch (InterruptedException e) {
                return i;
            }
        }

        @Override
        protected Object handleLifecycleMessage(LifecycleMessage m) {
            if (m instanceof ShutdownMessage)
                Strand.currentStrand().interrupt();
            else
                super.handleLifecycleMessage(m);

            return null;
        }
    }

    private static class BadActor1 extends BasicActor<Object, Integer> {
        public BadActor1(String name) {
            super(name);
        }

        @Override
        protected Integer doRun() throws SuspendExecution, InterruptedException {
            register();
            int i = 0;
            try {
                for (; ;) {
                    Object m = receive();
                    i++;
                    throw new RuntimeException("Ha!");
                }
            } catch (InterruptedException e) {
                return i;
            }
        }
    }

    @Test
    public void startChild() throws Exception {
        final Supervisor sup = new SupervisorActor(RestartStrategy.ONE_FOR_ONE, new co.paralleluniverse.actors.behaviors.Supervisor.ChildSpec("actor1", ChildMode.PERMANENT, 5, 1, TimeUnit.SECONDS, 3, ActorSpec.of(SupervisorTest.Actor1.class, "actor1"))).spawn(SupervisorTest.factory);
        ActorRef<Object> a;
        a = getChild(sup, "actor1", 1000);
        for (int i = 0; i < 3; i++)
            a.send(1);

        a.send(new ShutdownMessage(null));
        Assert.assertThat(LocalActor.<Integer>get(a), CoreMatchers.is(3));
        sup.shutdown();
        LocalActor.join(sup);
    }

    @Test
    public void whenChildDiesThenRestart() throws Exception {
        final Supervisor sup = new SupervisorActor(RestartStrategy.ONE_FOR_ONE, new co.paralleluniverse.actors.behaviors.Supervisor.ChildSpec("actor1", ChildMode.PERMANENT, 5, 1, TimeUnit.SECONDS, 3, ActorSpec.of(SupervisorTest.Actor1.class, "actor1"))).spawn(SupervisorTest.factory);
        ActorRef<Object> a;
        a = getChild(sup, "actor1", 1000);
        for (int i = 0; i < 3; i++)
            a.send(1);

        a.send(new ShutdownMessage(null));
        Assert.assertThat(LocalActor.<Integer>get(a), CoreMatchers.is(3));
        a = getChild(sup, "actor1", 1000);
        for (int i = 0; i < 5; i++)
            a.send(1);

        a.send(new ShutdownMessage(null));
        Assert.assertThat(LocalActor.<Integer>get(a), CoreMatchers.is(5));
        sup.shutdown();
        LocalActor.join(sup);
    }

    @Test
    public void whenChildDiesTooManyTimesThenGiveUpAndDie() throws Exception {
        final Supervisor sup = spawn();
        ActorRef<Object> a;
        ActorRef<Object> prevA = null;
        for (int k = 0; k < 4; k++) {
            a = getChildren(sup).get(0);
            Assert.assertThat(a, CoreMatchers.not(prevA));
            a.send(1);
            try {
                LocalActor.join(a);
                Assert.fail();
            } catch (ExecutionException e) {
            }
            prevA = a;
        }
        LocalActor.join(sup, 20, TimeUnit.MILLISECONDS);
    }

    @Test
    public void dontRestartTemporaryChildDeadOfNaturalCause() throws Exception {
        final Supervisor sup = spawn();
        ActorRef<Object> a;
        a = getChild(sup, "actor1", 1000);
        for (int i = 0; i < 3; i++)
            a.send(1);

        a.send(new ShutdownMessage(null));
        Assert.assertThat(LocalActor.<Integer>get(a), CoreMatchers.is(3));
        a = getChild(sup, "actor1", 200);
        Assert.assertThat(a, CoreMatchers.nullValue());
        List<ActorRef<Object>> cs = getChildren(sup);
        Assert.assertEquals(cs.size(), 0);
        sup.shutdown();
        LocalActor.join(sup);
    }

    @Test
    public void dontRestartTemporaryChildDeadOfUnnaturalCause() throws Exception {
        final Supervisor sup = new SupervisorActor(RestartStrategy.ONE_FOR_ONE, new co.paralleluniverse.actors.behaviors.Supervisor.ChildSpec("actor1", ChildMode.TEMPORARY, 5, 1, TimeUnit.SECONDS, 3, ActorSpec.of(SupervisorTest.BadActor1.class, "actor1"))).spawn(SupervisorTest.factory);
        ActorRef<Object> a;
        a = getChild(sup, "actor1", 1000);
        for (int i = 0; i < 3; i++)
            a.send(1);

        a.send(new ShutdownMessage(null));
        try {
            LocalActor.join(a);
            Assert.fail();
        } catch (ExecutionException e) {
        }
        a = getChild(sup, "actor1", 200);
        Assert.assertThat(a, CoreMatchers.nullValue());
        sup.shutdown();
        LocalActor.join(sup);
    }

    @Test
    public void dontRestartTransientChildDeadOfNaturalCause() throws Exception {
        final Supervisor sup = spawn();
        ActorRef<Object> a;
        a = getChild(sup, "actor1", 1000);
        for (int i = 0; i < 3; i++)
            a.send(1);

        a.send(new ShutdownMessage(null));
        Assert.assertThat(LocalActor.<Integer>get(a), CoreMatchers.is(3));
        a = getChild(sup, "actor1", 200);
        Assert.assertThat(a, CoreMatchers.nullValue());
        sup.shutdown();
        LocalActor.join(sup);
    }

    @Test
    public void restartTransientChildDeadOfUnnaturalCause() throws Exception {
        final Supervisor sup = spawn();
        ActorRef<Object> a;
        a = getChild(sup, "actor1", 1000);
        for (int i = 0; i < 3; i++)
            a.send(1);

        a.send(new ShutdownMessage(null));
        try {
            LocalActor.join(a);
            Assert.fail();
        } catch (ExecutionException e) {
        }
        ActorRef<Object> b = getChild(sup, "actor1", 200);
        Assert.assertThat(b, CoreMatchers.not(CoreMatchers.nullValue()));
        Assert.assertThat(b, CoreMatchers.not(CoreMatchers.equalTo(a)));
        List<ActorRef<Object>> bcs = getChildren(sup);
        Assert.assertThat(bcs.get(0), CoreMatchers.not(CoreMatchers.nullValue()));
        Assert.assertThat(bcs.get(0), CoreMatchers.not(CoreMatchers.equalTo(a)));
        Assert.assertEquals(bcs.size(), 1);
        sup.shutdown();
        LocalActor.join(sup);
    }

    private static class Actor2 extends BasicActor<Object, Integer> {
        public Actor2(String name) {
            super(name);
        }

        @Override
        protected Integer doRun() throws SuspendExecution, InterruptedException {
            register();
            int i = 0;
            try {
                for (; ;) {
                    Object m = receive();
                    i++;
                }
            } catch (InterruptedException e) {
                return i;
            }
        }

        @Override
        protected Object handleLifecycleMessage(LifecycleMessage m) {
            if (m instanceof ShutdownMessage)
                Strand.currentStrand().interrupt();
            else
                super.handleLifecycleMessage(m);

            return null;
        }

        @Override
        protected Actor<Object, Integer> reinstantiate() {
            return new SupervisorTest.Actor2(getName());
        }
    }

    @Test
    public void restartPreInstantiatedChild() throws Exception {
        final Supervisor sup = spawn();
        final ActorRef<Object> a1 = new SupervisorTest.Actor2("actor1").spawn();
        sup.addChild(new co.paralleluniverse.actors.behaviors.Supervisor.ChildSpec("actor1", ChildMode.PERMANENT, 5, 1, TimeUnit.SECONDS, 3, a1));
        ActorRef<Object> a;
        a = getChild(sup, "actor1", 1);
        Assert.assertThat(a, CoreMatchers.equalTo(a1));
        for (int i = 0; i < 3; i++)
            a.send(1);

        a.send(new ShutdownMessage(null));
        Assert.assertThat(LocalActor.<Integer>get(a), CoreMatchers.is(3));
        a = getChild(sup, "actor1", 1000);
        Assert.assertThat(a, CoreMatchers.is(CoreMatchers.not(CoreMatchers.equalTo(a1))));
        for (int i = 0; i < 5; i++)
            a.send(1);

        a.send(new ShutdownMessage(null));
        Assert.assertThat(LocalActor.<Integer>get(a), CoreMatchers.is(5));
        sup.shutdown();
        LocalActor.join(sup);
    }

    @Test
    public void testRegistration() throws Exception {
        Supervisor s = spawn();
        Assert.assertTrue((s == ((Supervisor) (ActorRegistry.getActor("test1")))));
    }

    // /////////////// Complex example ///////////////////////////////////////////
    private static class Actor3 extends BasicActor<Integer, Integer> {
        private final Supervisor mySup;

        private final AtomicInteger started;

        private final AtomicInteger terminated;

        public Actor3(String name, AtomicInteger started, AtomicInteger terminated) {
            super(name);
            mySup = LocalActor.self();
            this.started = started;
            this.terminated = terminated;
        }

        @Override
        protected Integer doRun() throws SuspendExecution, InterruptedException {
            final Server<SupervisorTest.Message1, Integer, Void> adder = new ServerActor<SupervisorTest.Message1, Integer, Void>() {
                @Override
                protected void init() throws SuspendExecution {
                    started.incrementAndGet();
                }

                @Override
                protected void terminate(Throwable cause) throws SuspendExecution {
                    terminated.incrementAndGet();
                }

                @Override
                protected Integer handleCall(ActorRef<?> from, Object id, SupervisorTest.Message1 m) throws SuspendExecution, Exception {
                    int res = (m.a) + (m.b);
                    if (res > 100)
                        throw new RuntimeException("oops!");

                    return res;
                }
            }.spawn();
            // link(adder);
            mySup.addChild(new co.paralleluniverse.actors.behaviors.Supervisor.ChildSpec(null, ChildMode.TEMPORARY, 10, 1, TimeUnit.SECONDS, 1, adder));
            int a = receive();
            int b = receive();
            int res = adder.call(new SupervisorTest.Message1(a, b));
            return res;
        }

        @Override
        protected Actor<Integer, Integer> reinstantiate() {
            return new SupervisorTest.Actor3(getName(), started, terminated);
        }
    }

    @Test
    public void testComplex1() throws Exception {
        AtomicInteger started = new AtomicInteger();
        AtomicInteger terminated = new AtomicInteger();
        final Supervisor sup = spawn();
        ActorRef<Integer> a;
        a = getChild(sup, "actor1", 1000);
        a.send(3);
        a.send(4);
        Assert.assertThat(LocalActor.<Integer>get(a), CoreMatchers.is(7));
        a = getChild(sup, "actor1", 1000);
        a.send(70);
        a.send(80);
        try {
            LocalActor.<Integer>get(a);
            Assert.fail();
        } catch (ExecutionException e) {
        }
        a = getChild(sup, "actor1", 1000);
        a.send(7);
        a.send(8);
        Assert.assertThat(LocalActor.<Integer>get(a), CoreMatchers.is(15));
        Thread.sleep(100);// give the actor time to start the GenServer

        sup.shutdown();
        LocalActor.join(sup);
        Assert.assertThat(started.get(), CoreMatchers.is(4));
        Assert.assertThat(terminated.get(), CoreMatchers.is(4));
    }

    static class Message1 {
        final int a;

        final int b;

        public Message1(int a, int b) {
            this.a = a;
            this.b = b;
        }
    }
}

