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


import co.paralleluniverse.actors.ActorRef;
import co.paralleluniverse.actors.LocalActor;
import co.paralleluniverse.common.test.TestUtil;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;
import org.mockito.Mockito;


/**
 *
 *
 * @author pron
 */
public class FiniteStateMachineTest {
    @Rule
    public TestName name = new TestName();

    @Rule
    public TestRule watchman = TestUtil.WATCHMAN;

    static final int mailboxSize = 10;

    public FiniteStateMachineTest() {
    }

    @Test
    public void testInitializationAndTermination() throws Exception {
        final Initializer init = Mockito.mock(Initializer.class);
        ActorRef<Object> a = spawn();
        Thread.sleep(100);
        Mockito.verify(init).init();
        LocalActor.join(a, 100, TimeUnit.MILLISECONDS);
        Mockito.verify(init).terminate(null);
    }

    @Test
    public void testStates() throws Exception {
        final AtomicBoolean success = new AtomicBoolean();
        ActorRef<Object> a = spawn();
        a.send("b");
        a.send("a");
        LocalActor.join(a, 100, TimeUnit.MILLISECONDS);
        Assert.assertTrue(success.get());
    }
}

