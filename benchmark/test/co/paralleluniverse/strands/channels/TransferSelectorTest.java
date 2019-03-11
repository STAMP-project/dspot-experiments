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
package co.paralleluniverse.strands.channels;


import co.paralleluniverse.common.test.TestUtil;
import co.paralleluniverse.common.util.Debug;
import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.FiberForkJoinScheduler;
import co.paralleluniverse.fibers.FiberScheduler;
import co.paralleluniverse.strands.channels.Channels.OverflowPolicy;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;


/**
 *
 *
 * @author pron
 */
public class TransferSelectorTest {
    @Rule
    public TestName name = new TestName();

    @Rule
    public TestRule watchman = TestUtil.WATCHMAN;

    final int mailboxSize;

    final OverflowPolicy policy;

    final boolean singleConsumer;

    final boolean singleProducer;

    final FiberScheduler scheduler;

    public TransferSelectorTest() {
        scheduler = new FiberForkJoinScheduler("test", 4, null, false);
        this.mailboxSize = 0;
        this.policy = OverflowPolicy.BLOCK;
        this.singleConsumer = false;
        this.singleProducer = false;
        Debug.dumpAfter(20000, "channels.log");
    }

    @Test
    public void testSelectReceive1() throws Exception {
        final Channel<String> channel1 = newChannel();
        final Channel<String> channel2 = newChannel();
        final Channel<String> channel3 = newChannel();
        Fiber fib = start();
        Thread.sleep(200);
        channel1.send("hello");
        Thread.sleep(200);
        channel3.send("world!");
        fib.join();
    }

    @Test
    public void testSelectReceive2() throws Exception {
        final Channel<String> channel1 = newChannel();
        final Channel<String> channel2 = newChannel();
        final Channel<String> channel3 = newChannel();
        Fiber fib = start();
        channel1.send("hello");
        channel3.send("world!");
        fib.join();
    }

    @Test
    public void testSelectReceiveWithClose1() throws Exception {
        final Channel<String> channel1 = newChannel();
        final Channel<String> channel2 = newChannel();
        final Channel<String> channel3 = newChannel();
        Fiber fib = start();
        Thread.sleep(200);
        channel3.close();
        fib.join();
    }

    @Test
    public void testSelectReceiveWithClose2() throws Exception {
        final Channel<String> channel1 = newChannel();
        final Channel<String> channel2 = newChannel();
        final Channel<String> channel3 = newChannel();
        Fiber fib = start();
        channel3.close();
        fib.join();
    }

    @Test
    public void testSelectReceiveTimeout() throws Exception {
        final Channel<String> channel1 = newChannel();
        final Channel<String> channel2 = newChannel();
        final Channel<String> channel3 = newChannel();
        Fiber fib = start();
        Thread.sleep(200);
        channel1.send("hello");
        fib.join();
    }

    @Test
    public void testSelectReceiveWithTimeoutChannel() throws Exception {
        final Channel<String> channel1 = newChannel();
        final Channel<String> channel2 = newChannel();
        final Channel<String> channel3 = newChannel();
        Fiber fib = start();
        Thread.sleep(200);
        channel1.send("hello");
        fib.join();
    }

    @Test
    public void testSelectSend1() throws Exception {
        final Channel<String> channel1 = newChannel();
        final Channel<String> channel2 = newChannel();
        final Channel<String> channel3 = newChannel();
        Fiber fib = start();
        Thread.sleep(200);
        String m1 = channel2.receive();
        Assert.assertThat(m1, CoreMatchers.equalTo("hi2"));
        Thread.sleep(200);
        String m2 = channel1.receive();
        Assert.assertThat(m2, CoreMatchers.equalTo("hi1"));
        fib.join();
    }

    @Test
    public void testSelectSend2() throws Exception {
        final Channel<String> channel1 = newChannel();
        final Channel<String> channel2 = newChannel();
        final Channel<String> channel3 = newChannel();
        Fiber fib = start();
        String m1 = channel2.receive();
        Assert.assertThat(m1, CoreMatchers.equalTo("hi2"));
        String m2 = channel1.receive();
        Assert.assertThat(m2, CoreMatchers.equalTo("hi1"));
        fib.join();
    }

    @Test
    public void testSelectSendWithClose1() throws Exception {
        final Channel<String> channel1 = newChannel();
        final Channel<String> channel2 = newChannel();
        final Channel<String> channel3 = newChannel();
        Fiber fib = start();
        Thread.sleep(200);
        channel2.close();
        fib.join();
    }

    @Test
    public void testSelectSendWithClose2() throws Exception {
        final Channel<String> channel1 = newChannel();
        final Channel<String> channel2 = newChannel();
        final Channel<String> channel3 = newChannel();
        Fiber fib = start();
        channel2.close();
        fib.join();
    }

    @Test
    public void testSelectSendTimeout() throws Exception {
        final Channel<String> channel1 = newChannel();
        final Channel<String> channel2 = newChannel();
        final Channel<String> channel3 = newChannel();
        Fiber fib = start();
        Thread.sleep(200);
        String m1 = channel3.receive();
        Assert.assertThat(m1, CoreMatchers.equalTo("bye3"));// the first send is cancelled

        fib.join();
    }

    @Test
    public void testSelectSendWithTimeoutChannel() throws Exception {
        final Channel<String> channel1 = newChannel();
        final Channel<String> channel2 = newChannel();
        final Channel<String> channel3 = newChannel();
        Fiber fib = start();
        Thread.sleep(200);
        String m1 = channel3.receive();
        Assert.assertThat(m1, CoreMatchers.equalTo("bye3"));// the first send is cancelled

        fib.join();
    }
}

