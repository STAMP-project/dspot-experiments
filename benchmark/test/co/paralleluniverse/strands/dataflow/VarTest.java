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
package co.paralleluniverse.strands.dataflow;


import co.paralleluniverse.common.test.TestUtil;
import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.Strand;
import co.paralleluniverse.strands.SuspendableRunnable;
import co.paralleluniverse.strands.channels.Channel;
import co.paralleluniverse.strands.channels.Channels;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
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
public class VarTest {
    @Rule
    public TestName name = new TestName();

    @Rule
    public TestRule watchman = TestUtil.WATCHMAN;

    public VarTest() {
    }

    @Test
    public void testThreadWaiter() throws Exception {
        final Var<String> var = new Var();
        final AtomicReference<String> res = new AtomicReference<>();
        final Thread t1 = new Thread(Strand.toRunnable(new SuspendableRunnable() {
            @Override
            public void run() throws SuspendExecution {
                try {
                    final String v = var.get();
                    res.set(v);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }));
        t1.start();
        Thread.sleep(100);
        var.set("yes!");
        t1.join();
        Assert.assertThat(res.get(), CoreMatchers.equalTo("yes!"));
        Assert.assertThat(var.get(), CoreMatchers.equalTo("yes!"));
    }

    @Test
    public void testFiberWaiter() throws Exception {
        final Var<String> var = new Var();
        final Fiber<String> f1 = new Fiber<String>(new co.paralleluniverse.strands.SuspendableCallable<String>() {
            @Override
            public String run() throws SuspendExecution, InterruptedException {
                final String v = var.get();
                return v;
            }
        }).start();
        Thread.sleep(100);
        var.set("yes!");
        f1.join();
        Assert.assertThat(f1.get(), CoreMatchers.equalTo("yes!"));
        Assert.assertThat(var.get(), CoreMatchers.equalTo("yes!"));
    }

    @Test
    public void testThreadAndFiberWaiters() throws Exception {
        final Var<String> var = new Var();
        final AtomicReference<String> res = new AtomicReference<>();
        final Fiber<String> f1 = new Fiber<String>(new co.paralleluniverse.strands.SuspendableCallable<String>() {
            @Override
            public String run() throws SuspendExecution, InterruptedException {
                final String v = var.get();
                return v;
            }
        }).start();
        final Thread t1 = new Thread(Strand.toRunnable(new SuspendableRunnable() {
            @Override
            public void run() throws SuspendExecution {
                try {
                    final String v = var.get();
                    res.set(v);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }));
        t1.start();
        Thread.sleep(100);
        var.set("yes!");
        t1.join();
        f1.join();
        Assert.assertThat(f1.get(), CoreMatchers.equalTo("yes!"));
        Assert.assertThat(res.get(), CoreMatchers.equalTo("yes!"));
        Assert.assertThat(var.get(), CoreMatchers.equalTo("yes!"));
    }

    @Test
    public void testHistory1() throws Exception {
        final Var<Integer> var = new Var(10);
        final Fiber<Integer> f1 = new Fiber<Integer>(new co.paralleluniverse.strands.SuspendableCallable<Integer>() {
            @Override
            public Integer run() throws SuspendExecution, InterruptedException {
                Strand.sleep(100);
                int sum = 0;
                for (int i = 0; i < 10; i++)
                    sum += var.get();

                return sum;
            }
        }).start();
        final Fiber<Integer> f2 = new Fiber<Integer>(new co.paralleluniverse.strands.SuspendableCallable<Integer>() {
            @Override
            public Integer run() throws SuspendExecution, InterruptedException {
                Strand.sleep(100);
                int sum = 0;
                for (int i = 0; i < 10; i++)
                    sum += var.get();

                return sum;
            }
        }).start();
        for (int i = 0; i < 10; i++)
            var.set((i + 1));

        Assert.assertThat(f1.get(), CoreMatchers.equalTo(55));
        Assert.assertThat(f2.get(), CoreMatchers.equalTo(55));
    }

    @Test
    public void testHistory2() throws Exception {
        final Var<Integer> var = new Var(2);
        final Fiber<Integer> f1 = new Fiber<Integer>(new co.paralleluniverse.strands.SuspendableCallable<Integer>() {
            @Override
            public Integer run() throws SuspendExecution, InterruptedException {
                Strand.sleep(100);
                int sum = 0;
                for (int i = 0; i < 10; i++)
                    sum += var.get();

                return sum;
            }
        }).start();
        final Fiber<Integer> f2 = new Fiber<Integer>(new co.paralleluniverse.strands.SuspendableCallable<Integer>() {
            @Override
            public Integer run() throws SuspendExecution, InterruptedException {
                int sum = 0;
                for (int i = 0; i < 10; i++)
                    sum += var.get();

                return sum;
            }
        }).start();
        for (int i = 0; i < 10; i++)
            var.set((i + 1));

        Assert.assertThat(f1.get(), CoreMatchers.not(CoreMatchers.equalTo(55)));
        f2.join();
    }

    @Test
    public void testFunction1() throws Exception {
        final Channel<Integer> ch = Channels.newChannel((-1));
        final Var<Integer> a = new Var<Integer>();
        final Var<Integer> b = new Var<Integer>();
        final Var<Integer> var = new Var<Integer>(2, new co.paralleluniverse.strands.SuspendableCallable<Integer>() {
            @Override
            public Integer run() throws SuspendExecution, InterruptedException {
                int c = (a.get()) + (b.get());
                ch.send(c);
                return c;
            }
        });
        b.set(2);
        Assert.assertThat(ch.receive(50, TimeUnit.MILLISECONDS), CoreMatchers.is(CoreMatchers.nullValue()));
        a.set(1);
        Assert.assertThat(ch.receive(50, TimeUnit.MILLISECONDS), CoreMatchers.is(3));
        Assert.assertThat(ch.receive(), CoreMatchers.is(3));
        Assert.assertThat(ch.receive(50, TimeUnit.MILLISECONDS), CoreMatchers.is(CoreMatchers.nullValue()));
        a.set(2);
        Assert.assertThat(ch.receive(), CoreMatchers.is(4));
        Assert.assertThat(ch.receive(50, TimeUnit.MILLISECONDS), CoreMatchers.is(CoreMatchers.nullValue()));
        b.set(3);
        Assert.assertThat(ch.receive(50, TimeUnit.MILLISECONDS), CoreMatchers.is(5));
        Assert.assertThat(ch.receive(50, TimeUnit.MILLISECONDS), CoreMatchers.is(CoreMatchers.nullValue()));
        a.set(4);
        Assert.assertThat(ch.receive(50, TimeUnit.MILLISECONDS), CoreMatchers.is(7));
    }
}

