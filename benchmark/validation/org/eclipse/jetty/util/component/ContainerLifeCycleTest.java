/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.util.component;


import Container.InheritedListener;
import Container.Listener;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import org.eclipse.jetty.util.TypeUtil;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class ContainerLifeCycleTest {
    @Test
    public void testStartStop() throws Exception {
        ContainerLifeCycle a0 = new ContainerLifeCycle();
        ContainerLifeCycleTest.TestContainerLifeCycle a1 = new ContainerLifeCycleTest.TestContainerLifeCycle();
        a0.addBean(a1);
        a0.start();
        Assertions.assertEquals(1, a1.started.get());
        Assertions.assertEquals(0, a1.stopped.get());
        Assertions.assertEquals(0, a1.destroyed.get());
        a0.start();
        Assertions.assertEquals(1, a1.started.get());
        Assertions.assertEquals(0, a1.stopped.get());
        Assertions.assertEquals(0, a1.destroyed.get());
        a0.stop();
        Assertions.assertEquals(1, a1.started.get());
        Assertions.assertEquals(1, a1.stopped.get());
        Assertions.assertEquals(0, a1.destroyed.get());
        a0.start();
        Assertions.assertEquals(2, a1.started.get());
        Assertions.assertEquals(1, a1.stopped.get());
        Assertions.assertEquals(0, a1.destroyed.get());
        a0.stop();
        Assertions.assertEquals(2, a1.started.get());
        Assertions.assertEquals(2, a1.stopped.get());
        Assertions.assertEquals(0, a1.destroyed.get());
    }

    @Test
    public void testStartStopDestroy() throws Exception {
        ContainerLifeCycle a0 = new ContainerLifeCycle();
        ContainerLifeCycleTest.TestContainerLifeCycle a1 = new ContainerLifeCycleTest.TestContainerLifeCycle();
        a0.start();
        Assertions.assertEquals(0, a1.started.get());
        Assertions.assertEquals(0, a1.stopped.get());
        Assertions.assertEquals(0, a1.destroyed.get());
        a0.addBean(a1);
        Assertions.assertEquals(0, a1.started.get());
        Assertions.assertEquals(0, a1.stopped.get());
        Assertions.assertEquals(0, a1.destroyed.get());
        Assertions.assertFalse(a0.isManaged(a1));
        a0.start();
        Assertions.assertEquals(0, a1.started.get());
        Assertions.assertEquals(0, a1.stopped.get());
        Assertions.assertEquals(0, a1.destroyed.get());
        start();
        a0.manage(a1);
        Assertions.assertEquals(1, a1.started.get());
        Assertions.assertEquals(0, a1.stopped.get());
        Assertions.assertEquals(0, a1.destroyed.get());
        a0.removeBean(a1);
        Assertions.assertEquals(1, a1.started.get());
        Assertions.assertEquals(1, a1.stopped.get());
        Assertions.assertEquals(0, a1.destroyed.get());
        a0.stop();
        a0.destroy();
        Assertions.assertEquals(1, a1.started.get());
        Assertions.assertEquals(1, a1.stopped.get());
        Assertions.assertEquals(0, a1.destroyed.get());
        stop();
        Assertions.assertEquals(1, a1.started.get());
        Assertions.assertEquals(1, a1.stopped.get());
        Assertions.assertEquals(0, a1.destroyed.get());
        a1.destroy();
        Assertions.assertEquals(1, a1.started.get());
        Assertions.assertEquals(1, a1.stopped.get());
        Assertions.assertEquals(1, a1.destroyed.get());
    }

    @Test
    public void testIllegalToStartAfterDestroy() throws Exception {
        ContainerLifeCycle container = new ContainerLifeCycle();
        container.start();
        container.stop();
        container.destroy();
        Assertions.assertThrows(IllegalStateException.class, () -> {
            container.start();
        });
    }

    @Test
    public void testDisJoint() throws Exception {
        ContainerLifeCycle a0 = new ContainerLifeCycle();
        ContainerLifeCycleTest.TestContainerLifeCycle a1 = new ContainerLifeCycleTest.TestContainerLifeCycle();
        // Start the a1 bean before adding, makes it auto disjoint
        start();
        // Now add it
        a0.addBean(a1);
        Assertions.assertFalse(a0.isManaged(a1));
        a0.start();
        Assertions.assertEquals(1, a1.started.get());
        Assertions.assertEquals(0, a1.stopped.get());
        Assertions.assertEquals(0, a1.destroyed.get());
        a0.start();
        Assertions.assertEquals(1, a1.started.get());
        Assertions.assertEquals(0, a1.stopped.get());
        Assertions.assertEquals(0, a1.destroyed.get());
        a0.stop();
        Assertions.assertEquals(1, a1.started.get());
        Assertions.assertEquals(0, a1.stopped.get());
        Assertions.assertEquals(0, a1.destroyed.get());
        stop();
        Assertions.assertEquals(1, a1.started.get());
        Assertions.assertEquals(1, a1.stopped.get());
        Assertions.assertEquals(0, a1.destroyed.get());
        a0.start();
        Assertions.assertEquals(1, a1.started.get());
        Assertions.assertEquals(1, a1.stopped.get());
        Assertions.assertEquals(0, a1.destroyed.get());
        a0.manage(a1);
        Assertions.assertTrue(a0.isManaged(a1));
        a0.stop();
        Assertions.assertEquals(1, a1.started.get());
        Assertions.assertEquals(1, a1.stopped.get());
        Assertions.assertEquals(0, a1.destroyed.get());
        a0.start();
        Assertions.assertEquals(2, a1.started.get());
        Assertions.assertEquals(1, a1.stopped.get());
        Assertions.assertEquals(0, a1.destroyed.get());
        a0.stop();
        Assertions.assertEquals(2, a1.started.get());
        Assertions.assertEquals(2, a1.stopped.get());
        Assertions.assertEquals(0, a1.destroyed.get());
        a0.unmanage(a1);
        Assertions.assertFalse(a0.isManaged(a1));
        a0.destroy();
        Assertions.assertEquals(2, a1.started.get());
        Assertions.assertEquals(2, a1.stopped.get());
        Assertions.assertEquals(0, a1.destroyed.get());
        a1.destroy();
        Assertions.assertEquals(2, a1.started.get());
        Assertions.assertEquals(2, a1.stopped.get());
        Assertions.assertEquals(1, a1.destroyed.get());
    }

    @Test
    public void testDumpable() throws Exception {
        ContainerLifeCycle a0 = new ContainerLifeCycle();
        String dump = trim(a0.dump());
        dump = check(dump, "ContainerLifeCycl");
        ContainerLifeCycle aa0 = new ContainerLifeCycle();
        a0.addBean(aa0);
        dump = trim(a0.dump());
        dump = check(dump, "ContainerLifeCycl");
        dump = check(dump, "+? ContainerLife");
        ContainerLifeCycle aa1 = new ContainerLifeCycle();
        a0.addBean(aa1);
        dump = trim(a0.dump());
        dump = check(dump, "ContainerLifeCycl");
        dump = check(dump, "+? ContainerLife");
        dump = check(dump, "+? ContainerLife");
        dump = check(dump, "");
        ContainerLifeCycle aa2 = new ContainerLifeCycle();
        a0.addBean(aa2, false);
        dump = trim(a0.dump());
        dump = check(dump, "ContainerLifeCycl");
        dump = check(dump, "+? ContainerLife");
        dump = check(dump, "+? ContainerLife");
        dump = check(dump, "+~ ContainerLife");
        dump = check(dump, "");
        aa1.start();
        a0.start();
        dump = trim(a0.dump());
        dump = check(dump, "ContainerLifeCycl");
        dump = check(dump, "+= ContainerLife");
        dump = check(dump, "+~ ContainerLife");
        dump = check(dump, "+~ ContainerLife");
        dump = check(dump, "");
        a0.manage(aa1);
        a0.removeBean(aa2);
        dump = trim(a0.dump());
        dump = check(dump, "ContainerLifeCycl");
        dump = check(dump, "+= ContainerLife");
        dump = check(dump, "+= ContainerLife");
        dump = check(dump, "");
        ContainerLifeCycle aaa0 = new ContainerLifeCycle();
        aa0.addBean(aaa0);
        dump = trim(a0.dump());
        dump = check(dump, "ContainerLifeCycl");
        dump = check(dump, "+= ContainerLife");
        dump = check(dump, "|  +~ Container");
        dump = check(dump, "+= ContainerLife");
        dump = check(dump, "");
        ContainerLifeCycle aa10 = new ContainerLifeCycle();
        aa1.addBean(aa10, true);
        dump = trim(a0.dump());
        dump = check(dump, "ContainerLifeCycl");
        dump = check(dump, "+= ContainerLife");
        dump = check(dump, "|  +~ Container");
        dump = check(dump, "+= ContainerLife");
        dump = check(dump, "   += Container");
        dump = check(dump, "");
        final ContainerLifeCycle a1 = new ContainerLifeCycle();
        final ContainerLifeCycle a2 = new ContainerLifeCycle();
        final ContainerLifeCycle a3 = new ContainerLifeCycle();
        final ContainerLifeCycle a4 = new ContainerLifeCycle();
        ContainerLifeCycle aa = new ContainerLifeCycle() {
            @Override
            public void dump(Appendable out, String indent) throws IOException {
                Dumpable.dumpObjects(out, indent, this.toString(), TypeUtil.asList(new Object[]{ a1, a2 }), TypeUtil.asList(new Object[]{ a3, a4 }));
            }
        };
        a0.addBean(aa, true);
        dump = trim(a0.dump());
        dump = check(dump, "ContainerLifeCycl");
        dump = check(dump, "+= ContainerLife");
        dump = check(dump, "|  +~ Container");
        dump = check(dump, "+= ContainerLife");
        dump = check(dump, "|  += Container");
        dump = check(dump, "+= ContainerLife");
        dump = check(dump, "   +> java.util.Arrays$ArrayList");
        dump = check(dump, "   |  +: ContainerLifeCycle");
        dump = check(dump, "   |  +: ContainerLifeCycle");
        dump = check(dump, "   +> java.util.Arrays$ArrayList");
        dump = check(dump, "      +: ContainerLifeCycle");
        dump = check(dump, "      +: ContainerLifeCycle");
        dump = check(dump, "");
        a2.addBean(aa0, true);
        dump = trim(a0.dump());
        dump = check(dump, "ContainerLifeCycl");
        dump = check(dump, "+= ContainerLife");
        dump = check(dump, "|  +~ Container");
        dump = check(dump, "+= ContainerLife");
        dump = check(dump, "|  += Container");
        dump = check(dump, "+= ContainerLife");
        dump = check(dump, "   +> java.util.Arrays$ArrayList");
        dump = check(dump, "   |  +: ContainerLifeCycle");
        dump = check(dump, "   |  +: ContainerLifeCycle");
        dump = check(dump, "   |     += Conta");
        dump = check(dump, "   |        +~ C");
        dump = check(dump, "   +> java.util.Arrays$ArrayList");
        dump = check(dump, "      +: ContainerLifeCycle");
        dump = check(dump, "      +: ContainerLifeCycle");
        dump = check(dump, "");
        a2.unmanage(aa0);
        dump = trim(a0.dump());
        dump = check(dump, "ContainerLifeCycl");
        dump = check(dump, "+= ContainerLife");
        dump = check(dump, "|  +~ Container");
        dump = check(dump, "+= ContainerLife");
        dump = check(dump, "|  += Container");
        dump = check(dump, "+= ContainerLife");
        dump = check(dump, "   +> java.util.Arrays$ArrayList");
        dump = check(dump, "   |  +: ContainerLifeCycle");
        dump = check(dump, "   |  +: ContainerLifeCycle");
        dump = check(dump, "   |     +~ Conta");
        dump = check(dump, "   +> java.util.Arrays$ArrayList");
        dump = check(dump, "      +: ContainerLifeCycle");
        dump = check(dump, "      +: ContainerLifeCycle");
        dump = check(dump, "");
        a0.unmanage(aa);
        dump = trim(a0.dump());
        dump = check(dump, "ContainerLifeCycl");
        dump = check(dump, "+= ContainerLife");
        dump = check(dump, "|  +~ Container");
        dump = check(dump, "+= ContainerLife");
        dump = check(dump, "|  += Container");
        dump = check(dump, "+~ ContainerLife");
        dump = check(dump, "");
    }

    @Test
    public void listenerTest() throws Exception {
        final Queue<String> handled = new ConcurrentLinkedQueue<>();
        final Queue<String> operation = new ConcurrentLinkedQueue<>();
        final Queue<Container> parent = new ConcurrentLinkedQueue<>();
        final Queue<Object> child = new ConcurrentLinkedQueue<>();
        Container.Listener listener = new Container.Listener() {
            @Override
            public void beanRemoved(Container p, Object c) {
                handled.add(toString());
                operation.add("removed");
                parent.add(p);
                child.add(c);
            }

            @Override
            public void beanAdded(Container p, Object c) {
                handled.add(toString());
                operation.add("added");
                parent.add(p);
                child.add(c);
            }

            @Override
            public String toString() {
                return "listener";
            }
        };
        ContainerLifeCycle c0 = new ContainerLifeCycle() {
            @Override
            public String toString() {
                return "c0";
            }
        };
        ContainerLifeCycle c00 = new ContainerLifeCycle() {
            @Override
            public String toString() {
                return "c00";
            }
        };
        c0.addBean(c00);
        String b000 = "b000";
        c00.addBean(b000);
        c0.addBean(listener);
        Assertions.assertEquals("listener", handled.poll());
        Assertions.assertEquals("added", operation.poll());
        Assertions.assertEquals(c0, parent.poll());
        Assertions.assertEquals(c00, child.poll());
        Assertions.assertEquals("listener", handled.poll());
        Assertions.assertEquals("added", operation.poll());
        Assertions.assertEquals(c0, parent.poll());
        Assertions.assertEquals(listener, child.poll());
        Container.InheritedListener inherited = new Container.InheritedListener() {
            @Override
            public void beanRemoved(Container p, Object c) {
                handled.add(toString());
                operation.add("removed");
                parent.add(p);
                child.add(c);
            }

            @Override
            public void beanAdded(Container p, Object c) {
                handled.add(toString());
                operation.add("added");
                parent.add(p);
                child.add(c);
            }

            @Override
            public String toString() {
                return "inherited";
            }
        };
        c0.addBean(inherited);
        Assertions.assertEquals("inherited", handled.poll());
        Assertions.assertEquals("added", operation.poll());
        Assertions.assertEquals(c0, parent.poll());
        Assertions.assertEquals(c00, child.poll());
        Assertions.assertEquals("inherited", handled.poll());
        Assertions.assertEquals("added", operation.poll());
        Assertions.assertEquals(c0, parent.poll());
        Assertions.assertEquals(listener, child.poll());
        Assertions.assertEquals("listener", handled.poll());
        Assertions.assertEquals("added", operation.poll());
        Assertions.assertEquals(c0, parent.poll());
        Assertions.assertEquals(inherited, child.poll());
        Assertions.assertEquals("inherited", handled.poll());
        Assertions.assertEquals("added", operation.poll());
        Assertions.assertEquals(c0, parent.poll());
        Assertions.assertEquals(inherited, child.poll());
        c0.start();
        Assertions.assertEquals("inherited", handled.poll());
        Assertions.assertEquals("added", operation.poll());
        Assertions.assertEquals(c00, parent.poll());
        Assertions.assertEquals(b000, child.poll());
        Assertions.assertEquals("inherited", handled.poll());
        Assertions.assertEquals("added", operation.poll());
        Assertions.assertEquals(c00, parent.poll());
        Assertions.assertEquals(inherited, child.poll());
        c0.removeBean(c00);
        Assertions.assertEquals("inherited", handled.poll());
        Assertions.assertEquals("removed", operation.poll());
        Assertions.assertEquals(c00, parent.poll());
        Assertions.assertEquals(inherited, child.poll());
        Assertions.assertEquals("inherited", handled.poll());
        Assertions.assertEquals("removed", operation.poll());
        Assertions.assertEquals(c00, parent.poll());
        Assertions.assertEquals(b000, child.poll());
        Assertions.assertEquals("listener", handled.poll());
        Assertions.assertEquals("removed", operation.poll());
        Assertions.assertEquals(c0, parent.poll());
        Assertions.assertEquals(c00, child.poll());
        Assertions.assertEquals("inherited", handled.poll());
        Assertions.assertEquals("removed", operation.poll());
        Assertions.assertEquals(c0, parent.poll());
        Assertions.assertEquals(c00, child.poll());
    }

    private final class InheritedListenerLifeCycle extends AbstractLifeCycle implements Container.InheritedListener {
        @Override
        public void beanRemoved(Container p, Object c) {
        }

        @Override
        public void beanAdded(Container p, Object c) {
        }

        @Override
        public String toString() {
            return "inherited";
        }
    }

    @Test
    public void testInheritedListener() throws Exception {
        ContainerLifeCycle c0 = new ContainerLifeCycle() {
            @Override
            public String toString() {
                return "c0";
            }
        };
        ContainerLifeCycle c00 = new ContainerLifeCycle() {
            @Override
            public String toString() {
                return "c00";
            }
        };
        ContainerLifeCycle c01 = new ContainerLifeCycle() {
            @Override
            public String toString() {
                return "c01";
            }
        };
        Container.InheritedListener inherited = new ContainerLifeCycleTest.InheritedListenerLifeCycle();
        c0.addBean(c00);
        c0.start();
        c0.addBean(inherited);
        c0.manage(inherited);
        c0.addBean(c01);
        c01.start();
        c0.manage(c01);
        Assertions.assertTrue(c0.isManaged(inherited));
        Assertions.assertFalse(c00.isManaged(inherited));
        Assertions.assertFalse(c01.isManaged(inherited));
    }

    private static class TestContainerLifeCycle extends ContainerLifeCycle {
        private final AtomicInteger destroyed = new AtomicInteger();

        private final AtomicInteger started = new AtomicInteger();

        private final AtomicInteger stopped = new AtomicInteger();

        @Override
        protected void doStart() throws Exception {
            started.incrementAndGet();
            super.doStart();
        }

        @Override
        protected void doStop() throws Exception {
            stopped.incrementAndGet();
            super.doStop();
        }

        @Override
        public void destroy() {
            destroyed.incrementAndGet();
            super.destroy();
        }
    }

    @Test
    public void testGetBeans() throws Exception {
        ContainerLifeCycleTest.TestContainerLifeCycle root = new ContainerLifeCycleTest.TestContainerLifeCycle();
        ContainerLifeCycleTest.TestContainerLifeCycle left = new ContainerLifeCycleTest.TestContainerLifeCycle();
        root.addBean(left);
        ContainerLifeCycleTest.TestContainerLifeCycle right = new ContainerLifeCycleTest.TestContainerLifeCycle();
        root.addBean(right);
        ContainerLifeCycleTest.TestContainerLifeCycle leaf = new ContainerLifeCycleTest.TestContainerLifeCycle();
        right.addBean(leaf);
        root.addBean(Integer.valueOf(0));
        root.addBean(Integer.valueOf(1));
        left.addBean(Integer.valueOf(2));
        right.addBean(Integer.valueOf(3));
        leaf.addBean(Integer.valueOf(4));
        addBean("leaf");
        MatcherAssert.assertThat(getBeans(Container.class), Matchers.containsInAnyOrder(left, right));
        MatcherAssert.assertThat(getBeans(Integer.class), Matchers.containsInAnyOrder(Integer.valueOf(0), Integer.valueOf(1)));
        MatcherAssert.assertThat(getBeans(String.class), Matchers.containsInAnyOrder());
        MatcherAssert.assertThat(getContainedBeans(Container.class), Matchers.containsInAnyOrder(left, right, leaf));
        MatcherAssert.assertThat(getContainedBeans(Integer.class), Matchers.containsInAnyOrder(Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3), Integer.valueOf(4)));
        MatcherAssert.assertThat(getContainedBeans(String.class), Matchers.containsInAnyOrder("leaf"));
    }
}

