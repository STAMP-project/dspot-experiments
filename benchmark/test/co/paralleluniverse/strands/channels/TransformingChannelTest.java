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
package co.paralleluniverse.strands.channels;


import co.paralleluniverse.common.test.Matchers;
import co.paralleluniverse.common.test.TestUtil;
import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.FiberForkJoinScheduler;
import co.paralleluniverse.fibers.FiberScheduler;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.Strand;
import co.paralleluniverse.strands.SuspendableCallable;
import co.paralleluniverse.strands.SuspendableRunnable;
import co.paralleluniverse.strands.Timeout;
import co.paralleluniverse.strands.channels.Channels.OverflowPolicy;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
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
public class TransformingChannelTest {
    private static final Object GO = new Object();

    @Rule
    public TestName name = new TestName();

    @Rule
    public TestRule watchman = TestUtil.WATCHMAN;

    final int mailboxSize;

    final OverflowPolicy policy;

    final boolean singleConsumer;

    final boolean singleProducer;

    final FiberScheduler scheduler;

    // public ChannelTest() {
    // fjPool = new ForkJoinPool(4, ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, true);
    // this.mailboxSize = 0;
    // this.policy = OverflowPolicy.BLOCK;
    // this.singleConsumer = false;
    // this.singleProducer = false;
    // 
    // Debug.dumpAfter(20000, "channels.log");
    // }
    public TransformingChannelTest(int mailboxSize, OverflowPolicy policy, boolean singleConsumer, boolean singleProducer) {
        scheduler = new FiberForkJoinScheduler("test", 4, null, false);
        this.mailboxSize = mailboxSize;
        this.policy = policy;
        this.singleConsumer = singleConsumer;
        this.singleProducer = singleProducer;
    }

    @Test
    public void transformingReceiveChannelIsEqualToChannel() throws Exception {
        final Channel<Integer> ch = newChannel();
        ReceivePort<Integer> ch1 = Channels.filter(((ReceivePort<Integer>) (ch)), new Predicate<Integer>() {
            @Override
            public boolean apply(Integer input) {
                return (input % 2) == 0;
            }
        });
        ReceivePort<Integer> ch2 = Channels.map(((ReceivePort<Integer>) (ch)), new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer input) {
                return input + 10;
            }
        });
        ReceivePort<Integer> ch3 = Channels.flatMap(((ReceivePort<Integer>) (ch)), new Function<Integer, ReceivePort<Integer>>() {
            @Override
            public ReceivePort<Integer> apply(Integer input) {
                return Channels.toReceivePort(Arrays.asList(new Integer[]{ input * 10, input * 100, input * 1000 }));
            }
        });
        ReceivePort<Integer> ch4 = Channels.reduce(((ReceivePort<Integer>) (ch)), new co.paralleluniverse.common.util.Function2<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer accum, Integer input) {
                return accum += input;
            }
        }, 0);
        ReceivePort<Integer> ch5 = Channels.take(((ReceivePort<Integer>) (ch)), 1);
        Assert.assertTrue(ch1.equals(ch));
        Assert.assertTrue(ch.equals(ch1));
        Assert.assertTrue(ch2.equals(ch));
        Assert.assertTrue(ch.equals(ch2));
        Assert.assertTrue(ch3.equals(ch));
        Assert.assertTrue(ch.equals(ch3));
        Assert.assertTrue(ch4.equals(ch));
        Assert.assertTrue(ch.equals(ch4));
        Assert.assertTrue(ch5.equals(ch));
        Assert.assertTrue(ch.equals(ch5));
        Assert.assertTrue(ch1.equals(ch1));
        Assert.assertTrue(ch1.equals(ch2));
        Assert.assertTrue(ch1.equals(ch3));
        Assert.assertTrue(ch1.equals(ch4));
        Assert.assertTrue(ch1.equals(ch5));
        Assert.assertTrue(ch2.equals(ch1));
        Assert.assertTrue(ch2.equals(ch2));
        Assert.assertTrue(ch2.equals(ch3));
        Assert.assertTrue(ch2.equals(ch4));
        Assert.assertTrue(ch2.equals(ch5));
        Assert.assertTrue(ch3.equals(ch1));
        Assert.assertTrue(ch3.equals(ch2));
        Assert.assertTrue(ch3.equals(ch3));
        Assert.assertTrue(ch3.equals(ch4));
        Assert.assertTrue(ch3.equals(ch5));
        Assert.assertTrue(ch4.equals(ch1));
        Assert.assertTrue(ch4.equals(ch2));
        Assert.assertTrue(ch4.equals(ch3));
        Assert.assertTrue(ch4.equals(ch4));
        Assert.assertTrue(ch4.equals(ch5));
        Assert.assertTrue(ch5.equals(ch1));
        Assert.assertTrue(ch5.equals(ch2));
        Assert.assertTrue(ch5.equals(ch3));
        Assert.assertTrue(ch5.equals(ch4));
        Assert.assertTrue(ch5.equals(ch5));
    }

    @Test
    public void transformingSendChannelIsEqualToChannel() throws Exception {
        final Channel<Integer> ch = newChannel();
        SendPort<Integer> ch1 = Channels.filterSend(((SendPort<Integer>) (ch)), new Predicate<Integer>() {
            @Override
            public boolean apply(Integer input) {
                return (input % 2) == 0;
            }
        });
        SendPort<Integer> ch2 = Channels.mapSend(((SendPort<Integer>) (ch)), new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer input) {
                return input + 10;
            }
        });
        SendPort<Integer> ch3 = Channels.flatMapSend(Channels.<Integer>newChannel(1), ((SendPort<Integer>) (ch)), new Function<Integer, ReceivePort<Integer>>() {
            @Override
            public ReceivePort<Integer> apply(Integer input) {
                return Channels.toReceivePort(Arrays.asList(new Integer[]{ input * 10, input * 100, input * 1000 }));
            }
        });
        SendPort<Integer> ch4 = Channels.reduceSend(((SendPort<Integer>) (ch)), new co.paralleluniverse.common.util.Function2<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer accum, Integer input) {
                return accum += input;
            }
        }, 0);
        Assert.assertTrue(ch1.equals(ch));
        Assert.assertTrue(ch.equals(ch1));
        Assert.assertTrue(ch2.equals(ch));
        Assert.assertTrue(ch.equals(ch2));
        Assert.assertTrue(ch3.equals(ch));
        Assert.assertTrue(ch.equals(ch3));
        Assert.assertTrue(ch4.equals(ch));
        Assert.assertTrue(ch.equals(ch4));
        Assert.assertTrue(ch1.equals(ch1));
        Assert.assertTrue(ch1.equals(ch2));
        Assert.assertTrue(ch1.equals(ch3));
        Assert.assertTrue(ch1.equals(ch4));
        Assert.assertTrue(ch2.equals(ch1));
        Assert.assertTrue(ch2.equals(ch2));
        Assert.assertTrue(ch2.equals(ch3));
        Assert.assertTrue(ch2.equals(ch4));
        Assert.assertTrue(ch3.equals(ch1));
        Assert.assertTrue(ch3.equals(ch2));
        Assert.assertTrue(ch3.equals(ch3));
        Assert.assertTrue(ch3.equals(ch4));
        Assert.assertTrue(ch4.equals(ch1));
        Assert.assertTrue(ch4.equals(ch2));
        Assert.assertTrue(ch4.equals(ch3));
        Assert.assertTrue(ch4.equals(ch4));
    }

    @Test
    public void testFilterFiberToFiber() throws Exception {
        final Channel<Integer> ch = newChannel();
        Fiber fib1 = start();
        Fiber fib2 = start();
        fib1.join();
        fib2.join();
    }

    @Test
    public void testFilterThreadToFiber() throws Exception {
        final Channel<Integer> ch = newChannel();
        Fiber fib = start();
        Strand.sleep(50);
        ch.send(1);
        ch.send(2);
        Strand.sleep(50);
        ch.send(3);
        ch.send(4);
        ch.send(5);
        ch.close();
        fib.join();
    }

    @Test
    public void testFilterFiberToThread() throws Exception {
        final Channel<Integer> ch = newChannel();
        Fiber fib = start();
        ReceivePort<Integer> ch1 = Channels.filter(((ReceivePort<Integer>) (ch)), new Predicate<Integer>() {
            @Override
            public boolean apply(Integer input) {
                return (input % 2) == 0;
            }
        });
        Integer m1 = ch1.receive();
        Integer m2 = ch1.receive();
        Integer m3 = ch1.receive();
        Assert.assertThat(m1, CoreMatchers.equalTo(2));
        Assert.assertThat(m2, CoreMatchers.equalTo(4));
        Assert.assertThat(m3, CoreMatchers.is(CoreMatchers.nullValue()));
        fib.join();
    }

    @Test
    public void testFilterWithTimeouts() throws Exception {
        final Channel<Integer> ch = newChannel();
        final Channel<Object> sync = Channels.newChannel(0);
        final Fiber fib = start();
        sync.send(TransformingChannelTest.GO);// 0

        Strand.sleep(50);
        ch.send(1, 10, TimeUnit.SECONDS);// Discarded (at receive side)

        ch.send(2, 10, TimeUnit.SECONDS);
        sync.send(TransformingChannelTest.GO);// 1

        Strand.sleep(100);
        ch.send(3, 10, TimeUnit.SECONDS);// Discarded (at receive side)

        ch.send(4, 10, TimeUnit.SECONDS);
        ch.send(5, 10, TimeUnit.SECONDS);// Discarded (at receive side)

        ch.close();
        fib.join();
    }

    @Test
    public void testSendFilterFiberToFiber() throws Exception {
        final Channel<Integer> ch = newChannel();
        Fiber fib1 = start();
        Fiber fib2 = start();
        fib1.join();
        fib2.join();
    }

    @Test
    public void testSendFilterThreadToFiber() throws Exception {
        final Channel<Integer> ch = newChannel();
        Fiber fib = start();
        SendPort<Integer> ch1 = Channels.filterSend(((SendPort<Integer>) (ch)), new Predicate<Integer>() {
            @Override
            public boolean apply(Integer input) {
                return (input % 2) == 0;
            }
        });
        Strand.sleep(50);
        ch1.send(1);
        ch1.send(2);
        Strand.sleep(50);
        ch1.send(3);
        ch1.send(4);
        ch1.send(5);
        ch1.close();
        fib.join();
    }

    @Test
    public void testSendFilterFiberToThread() throws Exception {
        final Channel<Integer> ch = newChannel();
        Fiber fib = start();
        Integer m1 = ch.receive();
        Integer m2 = ch.receive();
        Integer m3 = ch.receive();
        Assert.assertThat(m1, CoreMatchers.equalTo(2));
        Assert.assertThat(m2, CoreMatchers.equalTo(4));
        Assert.assertThat(m3, CoreMatchers.is(CoreMatchers.nullValue()));
        fib.join();
    }

    @Test
    public void testSendFilterWithTimeouts() throws Exception {
        final Channel<Integer> ch = newChannel();
        final Channel<Object> sync = Channels.newChannel(0);
        final Fiber fib = start();
        SendPort<Integer> ch1 = Channels.filterSend(((SendPort<Integer>) (ch)), new Predicate<Integer>() {
            @Override
            public boolean apply(Integer input) {
                return (input % 2) == 0;
            }
        });
        sync.send(TransformingChannelTest.GO);// 0

        Strand.sleep(50);
        ch1.send(1, 10, TimeUnit.SECONDS);// Discarded (at send side)

        ch1.send(2, 10, TimeUnit.SECONDS);
        sync.send(TransformingChannelTest.GO);// 1

        Strand.sleep(50);
        ch1.send(3, 10, TimeUnit.SECONDS);// Discarded (at send side)

        ch1.send(4, 10, TimeUnit.SECONDS);
        ch1.send(5, 10, TimeUnit.SECONDS);// Discarded (at send side)

        ch1.close();
        fib.join();
    }

    @Test
    public void testMapThreadToFiber() throws Exception {
        final Channel<Integer> ch = newChannel();
        Fiber fib = start();
        Strand.sleep(50);
        ch.send(1);
        ch.send(2);
        Strand.sleep(50);
        ch.send(3);
        ch.send(4);
        ch.send(5);
        ch.close();
        fib.join();
    }

    @Test
    public void testSendMapThreadToFiber() throws Exception {
        final Channel<Integer> ch = newChannel();
        Fiber fib = start();
        SendPort<Integer> ch1 = Channels.mapSend(((SendPort<Integer>) (ch)), new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer input) {
                return input + 10;
            }
        });
        Strand.sleep(50);
        ch1.send(1);
        ch1.send(2);
        Strand.sleep(50);
        ch1.send(3);
        ch1.send(4);
        ch1.send(5);
        ch1.close();
        fib.join();
    }

    @Test
    public void testReduceThreadToFiber() throws Exception {
        final Channel<Integer> ch = newChannel();
        Fiber fib = start();
        Strand.sleep(50);
        ch.send(1);
        ch.send(2);
        Strand.sleep(50);
        ch.send(3);
        ch.send(4);
        ch.send(5);
        ch.close();
        fib.join();
    }

    @Test
    public void testReduceInitThreadToFiber() throws Exception {
        final Channel<Integer> ch = newChannel();
        Fiber fib = start();
        Strand.sleep(50);
        ch.close();
        fib.join();
    }

    @Test
    @SuppressWarnings("null")
    public void testTakeThreadToFibers() throws Exception {
        Assume.assumeThat(mailboxSize, Matchers.greaterThan(0));// TODO Reorg to try with blocking channel at least meaningful parts

        final Channel<Object> takeSourceCh = newChannel();
        // Test 2 fibers failing immediately on take 0 of 1
        final ReceivePort<Object> take0RP = Channels.take(((ReceivePort<Object>) (takeSourceCh)), 0);
        final SuspendableRunnable take0SR = new SuspendableRunnable() {
            @Override
            public void run() throws SuspendExecution, InterruptedException {
                Assert.assertThat(take0RP.receive(), CoreMatchers.is(CoreMatchers.nullValue()));
                Assert.assertThat(take0RP.tryReceive(), CoreMatchers.is(CoreMatchers.nullValue()));
                long start = System.nanoTime();
                Assert.assertThat(take0RP.receive(10, TimeUnit.SECONDS), CoreMatchers.is(CoreMatchers.nullValue()));
                long end = System.nanoTime();
                Assert.assertThat((end - start), Matchers.lessThan(new Long((((5 * 1000) * 1000) * 1000))));// Should be immediate

                start = System.nanoTime();
                Assert.assertThat(take0RP.receive(new Timeout(10, TimeUnit.SECONDS)), CoreMatchers.is(CoreMatchers.nullValue()));
                end = System.nanoTime();
                Assert.assertThat((end - start), Matchers.lessThan(new Long((((5 * 1000) * 1000) * 1000))));// Should be immediate

            }
        };
        final Fiber take0Of1Fiber1 = start();
        final Fiber take0Of1Fiber2 = start();
        takeSourceCh.send(new Object());
        take0Of1Fiber1.join();
        take0Of1Fiber2.join();
        Assert.assertThat(takeSourceCh.receive(), CoreMatchers.is(CoreMatchers.notNullValue()));// 1 left in source, check and cleanup

        // Test tryReceive failing immediately when fiber blocked in receive on take 1 of 2
        final ReceivePort<Object> take1Of2RP = Channels.take(((ReceivePort<Object>) (takeSourceCh)), 1);
        final Fiber timeoutSucceedingTake1Of2 = start();
        Thread.sleep(100);// Let the fiber blocks in receive before starting the try

        final Fiber tryFailingTake1Of2 = start();
        Thread.sleep(100);
        // Make messages available
        takeSourceCh.send(new Object());
        takeSourceCh.send(new Object());
        timeoutSucceedingTake1Of2.join();
        tryFailingTake1Of2.join();
        Assert.assertThat(takeSourceCh.receive(), CoreMatchers.is(CoreMatchers.notNullValue()));// 1 left in source, check and cleanup

        // Comprehensive take + contention test:
        // 
        // - 1 message available immediately, 2 messages available in a burst on the source after 1s
        // - take 2
        // - 5 fibers competing on the take source (1 in front)
        // 
        // - one front fiber receiving with 200ms timeout => immediate success
        // - one more front fiber receiving with 200ms timeout => fail
        // - 3rd fiber taking over, receiving with 200ms timeout => fail
        // - 4th fiber taking over, receiving with 1s timeout => success
        // - 5th fiber asking untimed receive, waiting in monitor, will bail out because of take threshold
        final ReceivePort<Object> take2Of3RPComprehensive = Channels.take(((ReceivePort<Object>) (takeSourceCh)), 2);
        final co.paralleluniverse.common.util.Function2<Long, Integer, Fiber> take1SRFun = new co.paralleluniverse.common.util.Function2<Long, Integer, Fiber>() {
            @Override
            public Fiber apply(final Long timeoutMS, final Integer position) {
                return new Fiber(((("take-1-of-2_comprehensive_receiver_" + (timeoutMS >= 0 ? timeoutMS : "unlimited")) + "ms-") + position), scheduler, new SuspendableRunnable() {
                    @Override
                    public void run() throws SuspendExecution, InterruptedException {
                        final long start = System.nanoTime();
                        final Object res = (timeoutMS >= 0) ? take2Of3RPComprehensive.receive(timeoutMS, TimeUnit.MILLISECONDS) : take2Of3RPComprehensive.receive();
                        final long end = System.nanoTime();
                        switch (position) {
                            case 1 :
                                Assert.assertThat(res, CoreMatchers.is(CoreMatchers.notNullValue()));
                                Assert.assertThat((end - start), Matchers.lessThan(new Long(((300 * 1000) * 1000))));
                                break;
                            case 2 :
                                Assert.assertThat(res, CoreMatchers.is(CoreMatchers.nullValue()));
                                Assert.assertThat((end - start), Matchers.greaterThan(new Long(((300 * 1000) * 1000))));
                                break;
                            case 3 :
                                Assert.assertThat(res, CoreMatchers.is(CoreMatchers.nullValue()));
                                Assert.assertThat((end - start), Matchers.greaterThan(new Long(((200 * 1000) * 1000))));
                                break;
                            case 4 :
                                Assert.assertThat(res, CoreMatchers.is(CoreMatchers.notNullValue()));
                                Assert.assertThat((end - start), Matchers.lessThan(new Long(((1000 * 1000) * 1000))));
                                break;
                            case 5 :
                                Assert.assertThat(res, CoreMatchers.is(CoreMatchers.nullValue()));
                                Assert.assertThat((end - start), Matchers.lessThan(new Long(((1000 * 1000) * 1000))));// Should be almost instantaneous

                                break;
                            default :
                                Assert.fail();
                                break;
                        }
                    }
                });
            }
        };
        final Fiber[] competing = new Fiber[5];
        // First front fiber winning first message
        competing[0] = take1SRFun.apply(300L, 1).start();
        // Make 1 message available immediately for the first front fiber to consume
        takeSourceCh.send(new Object());
        Thread.sleep(100);
        // Second front fiber losing (waiting too little for second message)
        competing[1] = take1SRFun.apply(300L, 2).start();
        Thread.sleep(100);
        // First waiter, will fail (not waiting enough)
        competing[2] = take1SRFun.apply(200L, 3).start();
        Thread.sleep(300);// First waiter takeover

        // Second waiter, will win second message (waiting enough)
        competing[3] = take1SRFun.apply(1000L, 4).start();
        Thread.sleep(300);// Second waiter takeover

        // Third waiter, will try after take threshold and will bail out
        competing[4] = take1SRFun.apply((-1L), 5).start();
        // Make 2 more messages available
        takeSourceCh.send(new Object());
        takeSourceCh.send(new Object());
        // Wait fibers to finsh
        for (final Fiber f : competing)
            f.join();

        Assert.assertThat(takeSourceCh.receive(), CoreMatchers.is(CoreMatchers.notNullValue()));// 1 left in source, check and cleanup

        // Explicit (and uncoupled from source) closing of TakeSP
        final ReceivePort<Object> take1Of0ExplicitClose = Channels.take(((ReceivePort<Object>) (takeSourceCh)), 1);
        final SuspendableRunnable explicitCloseSR = new SuspendableRunnable() {
            @Override
            public void run() throws SuspendExecution, InterruptedException {
                final long start = System.nanoTime();
                final Object ret = take1Of0ExplicitClose.receive();
                final long end = System.nanoTime();
                Assert.assertThat(ret, CoreMatchers.is(CoreMatchers.nullValue()));
                Assert.assertTrue(take1Of0ExplicitClose.isClosed());
                Assert.assertFalse(takeSourceCh.isClosed());
                Assert.assertThat((end - start), Matchers.lessThan(new Long(((500 * 1000) * 1000))));
            }
        };
        final Fiber explicitCloseF1 = new Fiber("take-explicit-close-1", scheduler, explicitCloseSR);
        final Fiber explicitCloseF2 = new Fiber("take-explicit-close-2", scheduler, explicitCloseSR);
        Thread.sleep(100);
        take1Of0ExplicitClose.close();
    }

    @Test
    public void testSendReduceThreadToFiber() throws Exception {
        final Channel<Integer> ch = newChannel();
        final Fiber fib = start();
        final SendPort<Integer> ch1 = Channels.reduceSend(((SendPort<Integer>) (ch)), new co.paralleluniverse.common.util.Function2<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer accum, Integer input) {
                return accum + input;
            }
        }, 0);
        Strand.sleep(50);
        ch1.send(1);
        ch1.send(2);
        Strand.sleep(50);
        ch1.send(3);
        ch1.send(4);
        ch1.send(5);
        ch1.close();
        fib.join();
    }

    @Test
    public void testSendReduceInitThreadToFiber() throws Exception {
        final Channel<Integer> ch = newChannel();
        final Fiber fib = start();
        final SendPort<Integer> ch1 = Channels.reduceSend(((SendPort<Integer>) (ch)), new co.paralleluniverse.common.util.Function2<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer accum, Integer input) {
                return accum + input;
            }
        }, 0);
        Strand.sleep(50);
        ch1.close();
        fib.join();
    }

    @Test
    public void testZipThreadToFiber() throws Exception {
        final Channel<String> ch1 = newChannel();
        final Channel<Integer> ch2 = newChannel();
        Fiber fib = start();
        Strand.sleep(50);
        ch1.send("a");
        ch2.send(1);
        ch1.send("b");
        ch2.send(2);
        ch1.send("c");
        ch2.send(3);
        ch1.send("foo");
        ch2.close();
        fib.join();
    }

    @Test
    public void testZipWithTimeoutsThreadToFiber() throws Exception {
        final Channel<String> ch1 = newChannel();
        final Channel<Integer> ch2 = newChannel();
        final Channel<Object> sync = Channels.newChannel(0);
        final Fiber fib = start();
        sync.send(TransformingChannelTest.GO);// 0

        Strand.sleep(50);
        ch1.send("a", 10, TimeUnit.SECONDS);
        ch2.send(1, 10, TimeUnit.SECONDS);
        sync.send(TransformingChannelTest.GO);// 1

        ch1.send("b", 10, TimeUnit.SECONDS);
        Strand.sleep(100);
        ch2.send(2, 10, TimeUnit.SECONDS);
        sync.send(TransformingChannelTest.GO);// 2

        ch1.send("c", 10, TimeUnit.SECONDS);
        ch2.send(3, 10, TimeUnit.SECONDS);
        sync.send(TransformingChannelTest.GO);// 3

        ch1.send("foo", 10, TimeUnit.SECONDS);
        ch2.close();// Discards previous

        fib.join();
    }

    @Test
    public void testFlatmapThreadToFiber() throws Exception {
        final Channel<Integer> ch1 = newChannel();
        Fiber fib = start();
        Strand.sleep(50);
        ch1.send(1);
        ch1.send(2);
        ch1.send(3);
        ch1.send(4);
        ch1.send(5);
        ch1.close();
        fib.join();
    }

    @Test
    public void testFlatmapWithTimeoutsThreadToFiber() throws Exception {
        final Channel<Integer> ch1 = newChannel();
        final Channel<Object> sync = Channels.newChannel(0);
        final Fiber fib = start();
        sync.send(TransformingChannelTest.GO);// 0

        Strand.sleep(50);
        ch1.send(1, 10, TimeUnit.SECONDS);
        sync.send(TransformingChannelTest.GO);// 1

        Strand.sleep(100);
        ch1.send(2, 10, TimeUnit.SECONDS);
        sync.send(TransformingChannelTest.GO);// 2

        ch1.send(3, 10, TimeUnit.SECONDS);// Discarded

        ch1.send(4, 10, TimeUnit.SECONDS);
        sync.send(TransformingChannelTest.GO);// 3

        Strand.sleep(50);
        ch1.send(5, 10, TimeUnit.SECONDS);
        sync.send(TransformingChannelTest.GO);// 4

        ch1.close();
        fib.join();
    }

    @Test
    public void testFiberTransform1() throws Exception {
        final Channel<Integer> in = newChannel();
        final Channel<Integer> out = newChannel();
        Channels.fiberTransform(in, out, new co.paralleluniverse.strands.SuspendableAction2<ReceivePort<Integer>, SendPort<Integer>>() {
            @Override
            public void call(ReceivePort<Integer> in, SendPort<Integer> out) throws SuspendExecution, InterruptedException {
                Integer x;
                while ((x = in.receive()) != null) {
                    if ((x % 2) == 0)
                        out.send((x * 10));

                } 
                out.send(1234);
                out.close();
            }
        });
        Fiber fib1 = start();
        Fiber fib2 = start();
        fib1.join();
        fib2.join();
    }

    @Test
    public void testFlatmapSendThreadToFiber() throws Exception {
        final Channel<Integer> ch = newChannel();
        Fiber fib = start();
        SendPort<Integer> ch1 = Channels.flatMapSend(Channels.<Integer>newChannel(1), ch, new Function<Integer, ReceivePort<Integer>>() {
            @Override
            public ReceivePort<Integer> apply(Integer x) {
                if (x == 3)
                    return null;

                if ((x % 2) == 0)
                    return Channels.toReceivePort(Arrays.asList(new Integer[]{ x * 10, x * 100, x * 1000 }));
                else
                    return Channels.singletonReceivePort(x);

            }
        });
        Strand.sleep(50);
        ch1.send(1);
        ch1.send(2);
        ch1.send(3);
        ch1.send(4);
        ch1.send(5);
        ch1.close();
        fib.join();
    }

    @Test
    public void testSendSplitThreadToFiber() throws Exception {
        final Channel<String> chF1 = newChannel();
        final Channel<String> chF2 = newChannel();
        final SendPort<String> splitSP = new SplitSendPort<String>() {
            @Override
            protected SendPort select(final String m) {
                if (m.equals("f1"))
                    return chF1;
                else
                    return chF2;

            }
        };
        final Fiber f1 = start();
        final Fiber f2 = start();
        splitSP.send("f1");
        splitSP.send("f2");
        splitSP.close();
        Assert.assertFalse(chF1.isClosed());
        Assert.assertFalse(chF2.isClosed());
        Thread.sleep(100);
        chF1.close();
        chF2.close();
        f1.join();
        f2.join();
    }

    @Test
    public void testForEach() throws Exception {
        final Channel<Integer> ch = newChannel();
        Fiber<List<Integer>> fib = new Fiber<List<Integer>>("fiber", scheduler, new SuspendableCallable() {
            @Override
            public List<Integer> run() throws SuspendExecution, InterruptedException {
                final List<Integer> list = new ArrayList<>();
                Channels.transform(ch).forEach(new co.paralleluniverse.strands.SuspendableAction1<Integer>() {
                    @Override
                    public void call(Integer x) throws SuspendExecution, InterruptedException {
                        list.add(x);
                    }
                });
                return list;
            }
        }).start();
        Strand.sleep(50);
        ch.send(1);
        ch.send(2);
        Strand.sleep(50);
        ch.send(3);
        ch.send(4);
        ch.send(5);
        ch.close();
        List<Integer> list = fib.get();
        Assert.assertThat(list, CoreMatchers.equalTo(Arrays.asList(new Integer[]{ 1, 2, 3, 4, 5 })));
    }
}

