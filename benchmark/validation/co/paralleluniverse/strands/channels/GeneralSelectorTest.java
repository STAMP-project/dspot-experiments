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
import co.paralleluniverse.fibers.FiberForkJoinScheduler;
import co.paralleluniverse.fibers.FiberScheduler;
import co.paralleluniverse.strands.channels.Channels.OverflowPolicy;
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
public class GeneralSelectorTest {
    @Rule
    public TestName name = new TestName();

    @Rule
    public TestRule watchman = TestUtil.WATCHMAN;

    final boolean fiber;

    final int mailboxSize;

    final OverflowPolicy policy;

    final boolean singleConsumer;

    final boolean singleProducer;

    final FiberScheduler scheduler;

    // public GeneralSelectorTest() {
    // fjPool = new ForkJoinPool(4, ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, true);
    // this.mailboxSize = 1;
    // this.policy = OverflowPolicy.BLOCK;
    // this.singleConsumer = false;
    // this.singleProducer = false;
    // this.fiber = false;
    // 
    // Debug.dumpAfter(15000, "channels.log");
    // }
    public GeneralSelectorTest(int mailboxSize, OverflowPolicy policy, boolean singleConsumer, boolean singleProducer) {
        scheduler = new FiberForkJoinScheduler("test", 4, null, false);
        this.mailboxSize = mailboxSize;
        this.policy = policy;
        this.singleConsumer = singleConsumer;
        this.singleProducer = singleProducer;
        fiber = true;
    }

    @Test
    public void testFans1() throws Exception {
        int nchans = 3;
        int n = 200;
        final Channel<Integer> out = newChannel();
        final Channel<Integer> in = fanin(fanout(out, nchans));
        for (int i = 0; i < n; i++) {
            // System.out.println("send: " + i);
            out.send(i);
            // System.out.println("receiving");
            Integer x = in.receive();
            // System.out.println("receied " + x);
            Assert.assertThat(x, CoreMatchers.is(i));
        }
        out.close();
        Assert.assertThat(in.receive(), CoreMatchers.nullValue());
        Assert.assertThat(in.isClosed(), CoreMatchers.is(true));
    }

    @Test
    public void testFans2() throws Exception {
        Assume.assumeThat(mailboxSize, CoreMatchers.is(1));
        int nchans = 10;
        int n = nchans;
        final Channel<Integer> out = newChannel();
        final Channel<Integer> in = fanin(fanout(out, nchans));
        for (int i = 0; i < n; i++) {
            out.send(i);
        }
        Thread.sleep(500);
        boolean[] ms = new boolean[n];
        for (int i = 0; i < n; i++) {
            Integer m = in.receive();
            ms[m] = true;
        }
        for (int i = 0; i < n; i++)
            Assert.assertThat(ms[i], CoreMatchers.is(true));

        out.close();
        Assert.assertThat(in.receive(), CoreMatchers.nullValue());
        Assert.assertThat(in.isClosed(), CoreMatchers.is(true));
    }
}

