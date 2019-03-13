/**
 * Quasar: lightweight threads and actors for the JVM.
 * Copyright (c) 2015, Parallel Universe Software Co. All rights reserved.
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
package co.paralleluniverse.fibers.instrument;


import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.SuspendableRunnable;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author circlespainter
 */
public class InstrumentationOptimizerTest {
    @Test
    public void testSkipForwardsToSuspendableVoid() throws SuspendExecution, InterruptedException, ExecutionException {
        new Fiber(new SuspendableRunnable() {
            @Override
            public void run() throws SuspendExecution, InterruptedException {
                skipForwardsToSuspendableVoid();
            }
        }).start().join();
        Assert.assertTrue(isOptimized("skipForwardsToSuspendableVoid"));
    }

    @Test
    public void testSkipForwardsToSuspendableObject() throws SuspendExecution, InterruptedException, ExecutionException {
        new Fiber(new SuspendableRunnable() {
            @Override
            public void run() throws SuspendExecution, InterruptedException {
                skipForwardsToSuspendableObject();
            }
        }).start().join();
        Assert.assertTrue(isOptimized("skipForwardsToSuspendableObject"));
    }

    @Test
    public void testSkipForwardsToSuspendableDouble() throws SuspendExecution, InterruptedException, ExecutionException {
        new Fiber(new SuspendableRunnable() {
            @Override
            public void run() throws SuspendExecution, InterruptedException {
                skipForwardsToSuspendableDouble();
            }
        }).start().join();
        Assert.assertTrue(isOptimized("skipForwardsToSuspendableDouble"));
    }

    @Test
    public void testSkipForwardsToSuspendableFloat() throws SuspendExecution, InterruptedException, ExecutionException {
        new Fiber(new SuspendableRunnable() {
            @Override
            public void run() throws SuspendExecution, InterruptedException {
                skipForwardsToSuspendableFloat();
            }
        }).start().join();
        Assert.assertTrue(isOptimized("skipForwardsToSuspendableFloat"));
    }

    @Test
    public void testSkipForwardsToSuspendableInt() throws SuspendExecution, InterruptedException, ExecutionException {
        new Fiber(new SuspendableRunnable() {
            @Override
            public void run() throws SuspendExecution, InterruptedException {
                skipForwardsToSuspendableInt();
            }
        }).start().join();
        Assert.assertTrue(isOptimized("skipForwardsToSuspendableInt"));
    }

    @Test
    public void testSkipForwardsToSuspendableLong() throws SuspendExecution, InterruptedException, ExecutionException {
        new Fiber(new SuspendableRunnable() {
            @Override
            public void run() throws SuspendExecution, InterruptedException {
                skipForwardsToSuspendableLong();
            }
        }).start().join();
        Assert.assertTrue(isOptimized("skipForwardsToSuspendableLong"));
    }

    @Test
    public void testDontSkipForwardsWithTryCatch() throws SuspendExecution, InterruptedException, ExecutionException {
        new Fiber(new SuspendableRunnable() {
            @Override
            public void run() throws SuspendExecution, InterruptedException {
                dontSkipForwardsWithTryCatch();
            }
        }).start().join();
        Assert.assertFalse(isOptimized("skipForwardsWithTryCatch"));
    }

    @Test
    public void testDontSkipForwardsWithLoop() throws SuspendExecution, InterruptedException, ExecutionException {
        new Fiber(new SuspendableRunnable() {
            @Override
            public void run() throws SuspendExecution, InterruptedException {
                dontSkipForwardsWithLoop();
            }
        }).start().join();
        Assert.assertFalse(isOptimized("skipForwardsWithLoop"));
    }

    @Test
    public void testDontSkipForwardsWithLoopBefore() throws SuspendExecution, InterruptedException, ExecutionException {
        new Fiber(new SuspendableRunnable() {
            @Override
            public void run() throws SuspendExecution, InterruptedException {
                dontSkipForwardsWithLoopBefore();
            }
        }).start().join();
        Assert.assertFalse(isOptimized("skipForwardsWithLoopBefore"));
    }

    @Test
    public void testSkipForwardsWithLoopAfter() throws SuspendExecution, InterruptedException, ExecutionException {
        new Fiber(new SuspendableRunnable() {
            @Override
            public void run() throws SuspendExecution, InterruptedException {
                skipForwardsWithLoopAfter();
            }
        }).start().join();
        Assert.assertTrue(isOptimized("skipForwardsWithLoopAfter"));
    }

    @Test
    public void testDontSkipForwardsWithMethodBefore() throws SuspendExecution, InterruptedException, ExecutionException {
        new Fiber(new SuspendableRunnable() {
            @Override
            public void run() throws SuspendExecution, InterruptedException {
                dontSkipForwardsWithMethodBefore();
            }
        }).start().join();
        Assert.assertFalse(isOptimized("skipForwardsWithMethodBefore"));
    }

    @Test
    public void testSkipForwardsWithMethodAfter() throws SuspendExecution, InterruptedException, ExecutionException {
        new Fiber(new SuspendableRunnable() {
            @Override
            public void run() throws SuspendExecution, InterruptedException {
                skipForwardsWithMethodAfter();
            }
        }).start().join();
        Assert.assertTrue(isOptimized("skipForwardsWithMethodAfter"));
    }

    @Test
    public void testDontSkipForwardsWithReflectiveCalls() throws SuspendExecution, InterruptedException, ExecutionException {
        new Fiber(new SuspendableRunnable() {
            @Override
            public void run() throws SuspendExecution, InterruptedException {
                try {
                    dontSkipForwardsWithReflectiveCalls();
                } catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }).start().join();
        Assert.assertFalse(isOptimized("skipForwardsWithReflectiveCalls"));
    }
}

