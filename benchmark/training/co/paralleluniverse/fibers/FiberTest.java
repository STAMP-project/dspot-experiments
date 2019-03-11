/**
 * Quasar: lightweight threads and actors for the JVM.
 * Copyright (c) 2013-2014, Parallel Universe Software Co. All rights reserved.
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
package co.paralleluniverse.fibers;


import Strand.NORM_PRIORITY;
import Strand.UncaughtExceptionHandler;
import co.paralleluniverse.common.test.TestUtil;
import co.paralleluniverse.common.util.Exceptions;
import co.paralleluniverse.io.serialization.ByteArraySerializer;
import co.paralleluniverse.strands.Condition;
import co.paralleluniverse.strands.SettableFuture;
import co.paralleluniverse.strands.SimpleConditionSynchronizer;
import co.paralleluniverse.strands.Strand;
import co.paralleluniverse.strands.SuspendableRunnable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 *
 *
 * @author pron
 */
@RunWith(Parameterized.class)
public class FiberTest implements Serializable {
    @Rule
    public TestName name = new TestName();

    @Rule
    public TestRule watchman = TestUtil.WATCHMAN;

    private transient FiberScheduler scheduler;

    // public FiberTest() {
    // //        this.scheduler = new FiberExecutorScheduler("test", Executors.newFixedThreadPool(1));
    // this.scheduler = new FiberForkJoinScheduler("test", 4, null, false);
    // }
    public FiberTest(FiberScheduler scheduler) {
        this.scheduler = scheduler;
    }

    private static UncaughtExceptionHandler previousUEH;

    @Test
    public void testPriority() throws Exception {
        Fiber fiber = new Fiber(scheduler, new SuspendableRunnable() {
            @Override
            public void run() throws SuspendExecution {
            }
        });
        Assert.assertThat(fiber.getPriority(), CoreMatchers.is(NORM_PRIORITY));
        fiber.setPriority(3);
        Assert.assertThat(fiber.getPriority(), CoreMatchers.is(3));
        try {
            fiber.setPriority(((Strand.MAX_PRIORITY) + 1));
            Assert.fail();
        } catch (IllegalArgumentException e) {
        }
        try {
            fiber.setPriority(((Strand.MIN_PRIORITY) - 1));
            Assert.fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testTimeout() throws Exception {
        Fiber fiber = start();
        try {
            fiber.join(50, TimeUnit.MILLISECONDS);
            Assert.fail();
        } catch (TimeoutException e) {
        }
        fiber.join(200, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testJoinFromFiber() throws Exception {
        final Fiber<Integer> fiber1 = new Fiber<Integer>(scheduler, new co.paralleluniverse.strands.SuspendableCallable<Integer>() {
            @Override
            public Integer run() throws SuspendExecution {
                Fiber.park(100, TimeUnit.MILLISECONDS);
                return 123;
            }
        }).start();
        final Fiber<Integer> fiber2 = new Fiber<Integer>(scheduler, new co.paralleluniverse.strands.SuspendableCallable<Integer>() {
            @Override
            public Integer run() throws SuspendExecution, InterruptedException {
                try {
                    int res = fiber1.get();
                    return res;
                } catch (ExecutionException e) {
                    throw Exceptions.rethrow(e.getCause());
                }
            }
        }).start();
        int res = fiber2.get();
        Assert.assertThat(res, CoreMatchers.is(123));
        Assert.assertThat(fiber1.get(), CoreMatchers.is(123));
    }

    @Test
    public void testInterrupt() throws Exception {
        Fiber fiber = start();
        Thread.sleep(20);
        fiber.interrupt();
        fiber.join(5, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testCancel1() throws Exception {
        final AtomicBoolean started = new AtomicBoolean();
        final AtomicBoolean terminated = new AtomicBoolean();
        Fiber fiber = new Fiber(scheduler, new SuspendableRunnable() {
            @Override
            public void run() throws SuspendExecution {
                started.set(true);
                try {
                    Fiber.sleep(100);
                    Assert.fail("InterruptedException not thrown");
                } catch (InterruptedException e) {
                }
                terminated.set(true);
            }
        });
        fiber.start();
        Thread.sleep(20);
        fiber.cancel(true);
        fiber.join(5, TimeUnit.MILLISECONDS);
        Assert.assertThat(started.get(), CoreMatchers.is(true));
        Assert.assertThat(terminated.get(), CoreMatchers.is(true));
    }

    @Test
    public void testCancel2() throws Exception {
        final AtomicBoolean started = new AtomicBoolean();
        final AtomicBoolean terminated = new AtomicBoolean();
        Fiber fiber = new Fiber(scheduler, new SuspendableRunnable() {
            @Override
            public void run() throws SuspendExecution {
                started.set(true);
                try {
                    Fiber.sleep(100);
                    Assert.fail("InterruptedException not thrown");
                } catch (InterruptedException e) {
                }
                terminated.set(true);
            }
        });
        fiber.cancel(true);
        fiber.start();
        Thread.sleep(20);
        try {
            fiber.join(5, TimeUnit.MILLISECONDS);
            Assert.fail();
        } catch (CancellationException e) {
        }
        Assert.assertThat(started.get(), CoreMatchers.is(false));
        Assert.assertThat(terminated.get(), CoreMatchers.is(false));
    }

    @Test
    public void testThreadLocals() throws Exception {
        final ThreadLocal<String> tl1 = new ThreadLocal<>();
        final InheritableThreadLocal<String> tl2 = new InheritableThreadLocal<>();
        tl1.set("foo");
        tl2.set("bar");
        Fiber fiber = new Fiber(scheduler, new SuspendableRunnable() {
            @Override
            public void run() throws SuspendExecution, InterruptedException {
                Assert.assertThat(tl1.get(), CoreMatchers.is(CoreMatchers.nullValue()));
                Assert.assertThat(tl2.get(), CoreMatchers.is("bar"));
                tl1.set("koko");
                tl2.set("bubu");
                Assert.assertThat(tl1.get(), CoreMatchers.is("koko"));
                Assert.assertThat(tl2.get(), CoreMatchers.is("bubu"));
                Fiber.sleep(100);
                Assert.assertThat(tl1.get(), CoreMatchers.is("koko"));
                Assert.assertThat(tl2.get(), CoreMatchers.is("bubu"));
            }
        });
        fiber.start();
        fiber.join();
        Assert.assertThat(tl1.get(), CoreMatchers.is("foo"));
        Assert.assertThat(tl2.get(), CoreMatchers.is("bar"));
    }

    @Test
    public void testNoLocals() throws Exception {
        // shitty test
        final ThreadLocal<String> tl1 = new ThreadLocal<>();
        final InheritableThreadLocal<String> tl2 = new InheritableThreadLocal<>();
        tl1.set("foo");
        tl2.set("bar");
        Fiber fiber = setNoLocals(true);
        fiber.start();
        fiber.join();
        Assert.assertThat(tl1.get(), CoreMatchers.is("foo"));
        Assert.assertThat(tl2.get(), CoreMatchers.is("bar"));
    }

    @Test
    public void testInheritThreadLocals() throws Exception {
        final ThreadLocal<String> tl1 = new ThreadLocal<>();
        tl1.set("foo");
        Fiber fiber = new Fiber(scheduler, new SuspendableRunnable() {
            @Override
            public void run() throws SuspendExecution, InterruptedException {
                Assert.assertThat(tl1.get(), CoreMatchers.is("foo"));
                Fiber.sleep(100);
                Assert.assertThat(tl1.get(), CoreMatchers.is("foo"));
                tl1.set("koko");
                Assert.assertThat(tl1.get(), CoreMatchers.is("koko"));
                Fiber.sleep(100);
                Assert.assertThat(tl1.get(), CoreMatchers.is("koko"));
            }
        });
        fiber.inheritThreadLocals().start();
        fiber.join();
        Assert.assertThat(tl1.get(), CoreMatchers.is("foo"));
    }

    @Test
    public void testThreadLocalsParallel() throws Exception {
        final ThreadLocal<String> tl = new ThreadLocal<>();
        final int n = 100;
        final int loops = 100;
        Fiber[] fibers = new Fiber[n];
        for (int i = 0; i < n; i++) {
            final int id = i;
            Fiber fiber = new Fiber(scheduler, new SuspendableRunnable() {
                @Override
                public void run() throws SuspendExecution, InterruptedException {
                    for (int j = 0; j < loops; j++) {
                        final String tlValue = (("tl-" + id) + "-") + j;
                        tl.set(tlValue);
                        Assert.assertThat(tl.get(), CoreMatchers.equalTo(tlValue));
                        Strand.sleep(10);
                        Assert.assertThat(tl.get(), CoreMatchers.equalTo(tlValue));
                    }
                }
            });
            fiber.start();
            fibers[i] = fiber;
        }
        for (Fiber fiber : fibers)
            fiber.join();

    }

    @Test
    public void testInheritThreadLocalsParallel() throws Exception {
        final ThreadLocal<String> tl = new ThreadLocal<>();
        tl.set("foo");
        final int n = 100;
        final int loops = 100;
        Fiber[] fibers = new Fiber[n];
        for (int i = 0; i < n; i++) {
            final int id = i;
            Fiber fiber = inheritThreadLocals();
            fiber.start();
            fibers[i] = fiber;
        }
        for (Fiber fiber : fibers)
            fiber.join();

    }

    @Test
    public void whenFiberIsNewThenDumpStackReturnsNull() throws Exception {
        Fiber fiber = new Fiber(scheduler, new SuspendableRunnable() {
            @Override
            public void run() throws SuspendExecution, InterruptedException {
                foo();
            }

            private void foo() {
            }
        });
        StackTraceElement[] st = fiber.getStackTrace();
        Assert.assertThat(st, CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void whenFiberIsTerminatedThenDumpStackReturnsNull() throws Exception {
        Fiber fiber = start();
        fiber.join();
        StackTraceElement[] st = fiber.getStackTrace();
        Assert.assertThat(st, CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testDumpStackCurrentFiber() throws Exception {
        Fiber fiber = start();
        fiber.join();
    }

    @Test
    public void testDumpStackRunningFiber() throws Exception {
        Fiber fiber = start();
        Thread.sleep(200);
        StackTraceElement[] st = fiber.getStackTrace();
        // Strand.printStackTrace(st, System.err);
        boolean found = false;
        for (int i = 0; i < (st.length); i++) {
            if (st[i].getMethodName().equals("foo")) {
                found = true;
                break;
            }
        }
        Assert.assertThat(found, CoreMatchers.is(true));
        Assert.assertThat(st[((st.length) - 1)].getMethodName(), CoreMatchers.equalTo("run"));
        Assert.assertThat(st[((st.length) - 1)].getClassName(), CoreMatchers.equalTo(Fiber.class.getName()));
        fiber.join();
    }

    @Test
    public void testDumpStackWaitingFiber() throws Exception {
        final Condition cond = new SimpleConditionSynchronizer(null);
        final AtomicBoolean flag = new AtomicBoolean(false);
        Fiber fiber = start();
        Thread.sleep(200);
        StackTraceElement[] st = fiber.getStackTrace();
        // Strand.printStackTrace(st, System.err);
        Assert.assertThat(st[0].getMethodName(), CoreMatchers.equalTo("park"));
        boolean found = false;
        for (StackTraceElement ste : st) {
            if (ste.getMethodName().equals("foo")) {
                found = true;
                break;
            }
        }
        Assert.assertThat(found, CoreMatchers.is(true));
        Assert.assertThat(st[((st.length) - 1)].getMethodName(), CoreMatchers.equalTo("run"));
        Assert.assertThat(st[((st.length) - 1)].getClassName(), CoreMatchers.equalTo(Fiber.class.getName()));
        flag.set(true);
        cond.signalAll();
        fiber.join();
    }

    @Test
    public void testDumpStackWaitingFiberWhenCalledFromFiber() throws Exception {
        final Condition cond = new SimpleConditionSynchronizer(null);
        final AtomicBoolean flag = new AtomicBoolean(false);
        final Fiber fiber = start();
        Thread.sleep(200);
        Fiber fiber2 = start();
        fiber2.join();
        flag.set(true);
        cond.signalAll();
        fiber.join();
    }

    @Test
    public void testDumpStackSleepingFiber() throws Exception {
        // sleep is a special case
        Fiber fiber = start();
        Thread.sleep(200);
        StackTraceElement[] st = fiber.getStackTrace();
        // Strand.printStackTrace(st, System.err);
        Assert.assertThat(st[0].getMethodName(), CoreMatchers.equalTo("sleep"));
        boolean found = false;
        for (int i = 0; i < (st.length); i++) {
            if (st[i].getMethodName().equals("foo")) {
                found = true;
                break;
            }
        }
        Assert.assertThat(found, CoreMatchers.is(true));
        Assert.assertThat(st[((st.length) - 1)].getMethodName(), CoreMatchers.equalTo("run"));
        Assert.assertThat(st[((st.length) - 1)].getClassName(), CoreMatchers.equalTo(Fiber.class.getName()));
        fiber.join();
    }

    @Test
    public void testBadFiberDetection() throws Exception {
        Fiber good = start();
        Fiber bad = start();
        good.join();
        bad.join();
    }

    @Test
    public void testUncaughtExceptionHandler() throws Exception {
        final AtomicReference<Throwable> t = new AtomicReference<>();
        Fiber<Void> f = new Fiber<Void>() {
            @Override
            protected Void run() throws SuspendExecution, InterruptedException {
                throw new RuntimeException("foo");
            }
        };
        f.setUncaughtExceptionHandler(new Strand.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Strand f, Throwable e) {
                t.set(e);
            }
        });
        f.start();
        try {
            f.join();
            Assert.fail();
        } catch (ExecutionException e) {
            Assert.assertThat(e.getCause().getMessage(), CoreMatchers.equalTo("foo"));
        }
        Assert.assertThat(t.get().getMessage(), CoreMatchers.equalTo("foo"));
    }

    @Test
    public void testDefaultUncaughtExceptionHandler() throws Exception {
        final AtomicReference<Throwable> t = new AtomicReference<>();
        Fiber<Void> f = new Fiber<Void>() {
            @Override
            protected Void run() throws SuspendExecution, InterruptedException {
                throw new RuntimeException("foo");
            }
        };
        Fiber.setDefaultUncaughtExceptionHandler(new Strand.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Strand f, Throwable e) {
                t.set(e);
            }
        });
        f.start();
        try {
            f.join();
            Assert.fail();
        } catch (ExecutionException e) {
            Assert.assertThat(e.getCause().getMessage(), CoreMatchers.equalTo("foo"));
        }
        final Throwable th = t.get();
        Assert.assertTrue((th != null));
        Assert.assertThat(th.getMessage(), CoreMatchers.equalTo("foo"));
    }

    @Test
    public void testUtilsGet() throws Exception {
        final List<Fiber<String>> fibers = new ArrayList<>();
        final List<String> expectedResults = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            final int tmpI = i;
            expectedResults.add(("testUtilsSequence-" + tmpI));
            fibers.add(new Fiber(new co.paralleluniverse.strands.SuspendableCallable<String>() {
                @Override
                public String run() throws SuspendExecution, InterruptedException {
                    return "testUtilsSequence-" + tmpI;
                }
            }).start());
        }
        final List<String> results = FiberUtil.get(fibers);
        Assert.assertThat(results, CoreMatchers.equalTo(expectedResults));
    }

    @Test
    public void testUtilsGetWithTimeout() throws Exception {
        final List<Fiber<String>> fibers = new ArrayList<>();
        final List<String> expectedResults = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            final int tmpI = i;
            expectedResults.add(("testUtilsSequence-" + tmpI));
            fibers.add(new Fiber(new co.paralleluniverse.strands.SuspendableCallable<String>() {
                @Override
                public String run() throws SuspendExecution, InterruptedException {
                    return "testUtilsSequence-" + tmpI;
                }
            }).start());
        }
        final List<String> results = FiberUtil.get(1, TimeUnit.SECONDS, fibers);
        Assert.assertThat(results, CoreMatchers.equalTo(expectedResults));
    }

    @Test(expected = TimeoutException.class)
    public void testUtilsGetZeroWait() throws Exception {
        final List<Fiber<String>> fibers = new ArrayList<>();
        final List<String> expectedResults = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            final int tmpI = i;
            expectedResults.add(("testUtilsSequence-" + tmpI));
            fibers.add(new Fiber(new co.paralleluniverse.strands.SuspendableCallable<String>() {
                @Override
                public String run() throws SuspendExecution, InterruptedException {
                    return "testUtilsSequence-" + tmpI;
                }
            }).start());
        }
        final List<String> results = FiberUtil.get(0, TimeUnit.SECONDS, fibers);
        Assert.assertThat(results, CoreMatchers.equalTo(expectedResults));
    }

    @Test(expected = TimeoutException.class)
    public void testUtilsGetSmallWait() throws Exception {
        final List<Fiber<String>> fibers = new ArrayList<>();
        final List<String> expectedResults = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            final int tmpI = i;
            expectedResults.add(("testUtilsSequence-" + tmpI));
            fibers.add(new Fiber(new co.paralleluniverse.strands.SuspendableCallable<String>() {
                @Override
                public String run() throws SuspendExecution, InterruptedException {
                    // increase the sleep time to simulate data coming in then timeout
                    Strand.sleep((tmpI * 3), TimeUnit.MILLISECONDS);
                    return "testUtilsSequence-" + tmpI;
                }
            }).start());
        }
        // must be less than 60 (3 * 20) or else the test could sometimes pass.
        final List<String> results = FiberUtil.get(55, TimeUnit.MILLISECONDS, fibers);
        Assert.assertThat(results, CoreMatchers.equalTo(expectedResults));
    }

    @Test
    public void testSerialization1() throws Exception {
        // com.esotericsoftware.minlog.Log.set(1);
        final SettableFuture<byte[]> buf = new SettableFuture();
        Fiber<Integer> f1 = start();
        Fiber<Integer> f2 = Fiber.unparkSerialized(buf.get(), scheduler);
        Assert.assertThat(f2.get(), CoreMatchers.is(55));
    }

    static class SerFiber1 extends FiberTest.SerFiber<Integer> {
        public SerFiber1(FiberScheduler scheduler, FiberWriter fiberWriter) {
            super(scheduler, fiberWriter);
        }

        @Override
        public Integer run() throws SuspendExecution, InterruptedException {
            int sum = 0;
            for (int i = 1; i <= 10; i++) {
                sum += i;
                if (i == 5) {
                    Fiber.parkAndSerialize(fiberWriter);
                    assert (i == 5) && (sum == 15);
                }
            }
            return sum;
        }
    }

    @Test
    public void testSerialization2() throws Exception {
        // com.esotericsoftware.minlog.Log.set(1);
        final SettableFuture<byte[]> buf = new SettableFuture();
        Fiber<Integer> f1 = start();
        Fiber<Integer> f2 = Fiber.unparkSerialized(buf.get(), scheduler);
        Assert.assertThat(f2.get(), CoreMatchers.is(55));
    }

    static class SerFiber2 extends Fiber<Integer> {
        public SerFiber2(FiberScheduler scheduler, final FiberWriter fiberWriter) {
            super(scheduler, new co.paralleluniverse.strands.SuspendableCallable<Integer>() {
                @Override
                public Integer run() throws SuspendExecution, InterruptedException {
                    int sum = 0;
                    for (int i = 1; i <= 10; i++) {
                        sum += i;
                        if (i == 5) {
                            Fiber.parkAndSerialize(fiberWriter);
                            assert (i == 5) && (sum == 15);
                        }
                    }
                    return sum;
                }
            });
        }
    }

    @Test
    public void testSerializationWithThreadLocals() throws Exception {
        final ThreadLocal<String> tl1 = new ThreadLocal<>();
        final InheritableThreadLocal<String> tl2 = new InheritableThreadLocal<>();
        tl1.set("foo");
        tl2.set("bar");
        final SettableFuture<byte[]> buf = new SettableFuture();
        Fiber<Integer> f1 = start();
        Fiber<Integer> f2 = Fiber.unparkSerialized(buf.get(), scheduler);
        Assert.assertThat(f2.get(), CoreMatchers.is(55));
    }

    static class SerFiber3 extends FiberTest.SerFiber<Integer> {
        private final ThreadLocal<String> tl1;

        private final InheritableThreadLocal<String> tl2;

        public SerFiber3(FiberScheduler scheduler, FiberWriter fiberWriter, ThreadLocal<String> tl1, InheritableThreadLocal<String> tl2) {
            super(scheduler, fiberWriter);
            this.tl1 = tl1;
            this.tl2 = tl2;
        }

        @Override
        public Integer run() throws SuspendExecution, InterruptedException {
            Assert.assertThat(tl1.get(), CoreMatchers.is(CoreMatchers.nullValue()));
            Assert.assertThat(tl2.get(), CoreMatchers.is("bar"));
            tl1.set("koko");
            tl2.set("bubu");
            int sum = 0;
            for (int i = 1; i <= 10; i++) {
                sum += i;
                if (i == 5) {
                    Fiber.parkAndSerialize(fiberWriter);
                    assert (i == 5) && (sum == 15);
                }
            }
            Assert.assertThat(tl1.get(), CoreMatchers.is("koko"));
            Assert.assertThat(tl2.get(), CoreMatchers.is("bubu"));
            return sum;
        }
    }

    static class SerFiber<V> extends Fiber<V> implements Serializable {
        protected final transient FiberWriter fiberWriter;

        public SerFiber(FiberScheduler scheduler, co.paralleluniverse.strands.SuspendableCallable<V> target, FiberWriter fiberWriter) {
            super(scheduler, target);
            this.fiberWriter = fiberWriter;
        }

        public SerFiber(FiberScheduler scheduler, FiberWriter fiberWriter) {
            super(scheduler);
            this.fiberWriter = fiberWriter;
        }
    }

    @Test
    public void testCustomSerialization() throws Exception {
        // com.esotericsoftware.minlog.Log.set(1);
        final SettableFuture<byte[]> buf = new SettableFuture();
        Fiber<Integer> f1 = start();
        Fiber<Integer> f2 = Fiber.unparkSerialized(buf.get(), scheduler);
        Assert.assertThat(f2.get(), CoreMatchers.is(55));
    }

    static class CustomSerFiber extends Fiber<Integer> implements Serializable {
        private final transient CustomFiberWriter writer;

        public CustomSerFiber(FiberScheduler scheduler, CustomFiberWriter writer) {
            super(scheduler);
            this.writer = writer;
        }

        @Override
        public Integer run() throws SuspendExecution, InterruptedException {
            int sum = 0;
            for (int i = 1; i <= 10; i++) {
                sum += i;
                if (i == 5) {
                    Fiber.parkAndCustomSerialize(writer);
                    assert (i == 5) && (sum == 15);
                }
            }
            return sum;
        }
    }

    static class SettableFutureCustomWriter implements CustomFiberWriter {
        private final transient SettableFuture<byte[]> buf;

        public SettableFutureCustomWriter(SettableFuture<byte[]> buf) {
            this.buf = buf;
        }

        @Override
        public void write(Fiber fiber) {
            buf.set(Fiber.getFiberSerializer().write(fiber));
        }
    }

    // @Override
    // public void write(byte[] serFiber) {
    // buf.set(serFiber);
    // }
    static class SettableFutureFiberWriter implements FiberWriter {
        private final transient SettableFuture<byte[]> buf;

        public SettableFutureFiberWriter(SettableFuture<byte[]> buf) {
            this.buf = buf;
        }

        @Override
        public void write(Fiber fiber, ByteArraySerializer ser) {
            buf.set(ser.write(fiber));
        }
    }
}

