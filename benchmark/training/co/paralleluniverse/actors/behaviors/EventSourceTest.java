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


import co.paralleluniverse.actors.ActorRegistry;
import co.paralleluniverse.actors.LocalActor;
import co.paralleluniverse.common.test.TestUtil;
import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;
import org.mockito.InOrder;
import org.mockito.Mockito;


/**
 *
 *
 * @author pron
 */
public final class EventSourceTest {
    @Rule
    public final TestName name = new TestName();

    @Rule
    public final TestRule watchman = TestUtil.WATCHMAN;

    static final int mailboxSize = 10;

    public EventSourceTest() {
    }

    @Test
    public final void testInitializationAndTermination() throws Exception {
        final Initializer init = Mockito.mock(Initializer.class);
        final EventSource<String> es = spawnEventSource(init);
        Thread.sleep(100);
        Mockito.verify(init).init();
        es.shutdown();
        LocalActor.join(es, 100, TimeUnit.MILLISECONDS);
        Mockito.verify(init).terminate(null);
    }

    @Test
    public final void testNotify() throws Exception {
        final EventHandler<String> handler1 = Mockito.mock(EventHandler.class);
        final EventHandler<String> handler2 = Mockito.mock(EventHandler.class);
        final EventSource<String> es = spawnEventSource(null);
        es.addHandler(handler1);
        es.addHandler(handler2);
        es.notify("hello");
        Thread.sleep(100);
        InOrder inOrder = Mockito.inOrder(handler1, handler2);
        inOrder.verify(handler1).handleEvent("hello");
        inOrder.verify(handler2).handleEvent("hello");
        es.removeHandler(handler1);
        es.notify("goodbye");
        es.shutdown();
        LocalActor.join(es, 100, TimeUnit.MILLISECONDS);
        Mockito.verify(handler1, Mockito.never()).handleEvent("goodbye");
        Mockito.verify(handler2).handleEvent("goodbye");
    }

    private static final class BlockingStringEventHandler implements EventHandler<String> {
        @Override
        public final void handleEvent(String s) throws SuspendExecution, InterruptedException {
            Fiber.sleep(10);
        }
    }

    @Test
    public final void testBlock() throws Exception {
        final EventHandler<String> handler1 = new EventSourceTest.BlockingStringEventHandler();
        final EventHandler<String> handler2 = new EventSourceTest.BlockingStringEventHandler();
        final EventSource<String> es = spawnEventSource(null);
        es.addHandler(handler1);
        es.addHandler(handler2);
        es.notify("hello");
        Thread.sleep(100);
        es.shutdown();
        LocalActor.join(es, 100, TimeUnit.MILLISECONDS);
    }

    @Test
    public final void testRegistration() throws Exception {
        final EventSource<String> es = spawn();
        Assert.assertTrue((es == ((EventSource) (ActorRegistry.getActor("test1")))));
    }
}

