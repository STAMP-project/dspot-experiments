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
package co.paralleluniverse.strands.channels.transfer;


import co.paralleluniverse.common.test.TestUtil;
import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.FiberForkJoinScheduler;
import co.paralleluniverse.fibers.FiberScheduler;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.channels.Channel;
import co.paralleluniverse.strands.channels.Channels.OverflowPolicy;
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
 * @author circlespainter
 */
@RunWith(Parameterized.class)
public class PipelineTest {
    @Rule
    public TestName name = new TestName();

    @Rule
    public TestRule watchman = TestUtil.WATCHMAN;

    private final int mailboxSize;

    private final OverflowPolicy policy;

    private final boolean singleConsumer;

    private final boolean singleProducer;

    private final FiberScheduler scheduler;

    private final int parallelism;

    public PipelineTest(final int mailboxSize, final OverflowPolicy policy, final boolean singleConsumer, final boolean singleProducer, final int parallelism) {
        scheduler = new FiberForkJoinScheduler("test", 4, null, false);
        this.mailboxSize = mailboxSize;
        this.policy = policy;
        this.singleConsumer = singleConsumer;
        this.singleProducer = singleProducer;
        this.parallelism = parallelism;
    }

    @Test
    public void testPipeline() throws Exception {
        final Channel<Integer> i = newChannel();
        final Channel<Integer> o = newChannel();
        final Pipeline<Integer, Integer> p = new Pipeline(i, o, new co.paralleluniverse.strands.SuspendableAction2<Integer, Channel<Integer>>() {
            @Override
            public void call(final Integer i, final Channel<Integer> out) throws SuspendExecution, InterruptedException {
                out.send((i + 1));
                out.close();
            }
        }, parallelism);
        final Fiber<Long> pf = start();
        final Fiber receiver = start();
        i.send(1);
        i.send(2);
        i.send(3);
        i.send(4);
        i.close();
        long transferred = pf.get();// Join pipeline

        Assert.assertThat(transferred, CoreMatchers.equalTo(p.getTransferred()));
        Assert.assertThat(transferred, CoreMatchers.equalTo(4L));
        receiver.join();
    }
}

