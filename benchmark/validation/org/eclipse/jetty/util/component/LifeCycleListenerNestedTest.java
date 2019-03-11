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


import java.util.ArrayList;
import java.util.List;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


/**
 * Testing for LifeCycleListener events on nested components
 * during runtime.
 */
@Disabled
public class LifeCycleListenerNestedTest {
    // Set this true to use test-specific workaround.
    private final boolean WORKAROUND = false;

    public static class Foo extends ContainerLifeCycle {
        @Override
        public String toString() {
            return LifeCycleListenerNestedTest.Foo.class.getSimpleName();
        }
    }

    public static class Bar extends ContainerLifeCycle {
        private final String id;

        public Bar(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = (prime * result) + ((id) == null ? 0 : id.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if ((this) == obj)
                return true;

            if (obj == null)
                return false;

            if ((getClass()) != (obj.getClass()))
                return false;

            LifeCycleListenerNestedTest.Bar other = ((LifeCycleListenerNestedTest.Bar) (obj));
            if ((id) == null) {
                if ((other.id) != null)
                    return false;

            } else
                if (!(id.equals(other.id)))
                    return false;


            return true;
        }

        @Override
        public String toString() {
            return (((LifeCycleListenerNestedTest.Bar.class.getSimpleName()) + "(") + (id)) + ")";
        }
    }

    public static enum LifeCycleEvent {

        STARTING,
        STARTED,
        FAILURE,
        STOPPING,
        STOPPED;}

    public static class CapturingListener implements Container.InheritedListener , LifeCycle.Listener {
        private List<String> events = new ArrayList<>();

        private void addEvent(Object obj, LifeCycleListenerNestedTest.LifeCycleEvent event) {
            events.add(String.format("%s - %s", obj.toString(), event.name()));
        }

        @Override
        public void lifeCycleStarting(LifeCycle event) {
            addEvent(event, LifeCycleListenerNestedTest.LifeCycleEvent.STARTING);
        }

        @Override
        public void lifeCycleStarted(LifeCycle event) {
            addEvent(event, LifeCycleListenerNestedTest.LifeCycleEvent.STARTED);
        }

        @Override
        public void lifeCycleFailure(LifeCycle event, Throwable cause) {
            addEvent(event, LifeCycleListenerNestedTest.LifeCycleEvent.FAILURE);
        }

        @Override
        public void lifeCycleStopping(LifeCycle event) {
            addEvent(event, LifeCycleListenerNestedTest.LifeCycleEvent.STOPPING);
        }

        @Override
        public void lifeCycleStopped(LifeCycle event) {
            addEvent(event, LifeCycleListenerNestedTest.LifeCycleEvent.STOPPED);
        }

        public List<String> getEvents() {
            return events;
        }

        public void assertEvents(Matcher<Iterable<? super String>> matcher) {
            MatcherAssert.assertThat(events, matcher);
        }

        @Override
        public void beanAdded(Container parent, Object child) {
            if (child instanceof LifeCycle) {
                addLifeCycleListener(this);
            }
        }

        @Override
        public void beanRemoved(Container parent, Object child) {
            if (child instanceof LifeCycle) {
                removeLifeCycleListener(this);
            }
        }
    }

    @Test
    public void testAddBean_AddListener_Start() throws Exception {
        LifeCycleListenerNestedTest.Foo foo = new LifeCycleListenerNestedTest.Foo();
        LifeCycleListenerNestedTest.Bar bara = new LifeCycleListenerNestedTest.Bar("a");
        LifeCycleListenerNestedTest.Bar barb = new LifeCycleListenerNestedTest.Bar("b");
        addBean(bara);
        addBean(barb);
        LifeCycleListenerNestedTest.CapturingListener listener = new LifeCycleListenerNestedTest.CapturingListener();
        addLifeCycleListener(listener);
        if (WORKAROUND)
            addEventListener(listener);

        try {
            start();
            MatcherAssert.assertThat("Foo.started", isStarted(), Matchers.is(true));
            MatcherAssert.assertThat("Bar(a).started", isStarted(), Matchers.is(true));
            MatcherAssert.assertThat("Bar(b).started", isStarted(), Matchers.is(true));
            listener.assertEvents(Matchers.hasItem("Foo - STARTING"));
            listener.assertEvents(Matchers.hasItem("Foo - STARTED"));
            listener.assertEvents(Matchers.hasItem("Bar(a) - STARTING"));
            listener.assertEvents(Matchers.hasItem("Bar(a) - STARTED"));
            listener.assertEvents(Matchers.hasItem("Bar(b) - STARTING"));
            listener.assertEvents(Matchers.hasItem("Bar(b) - STARTED"));
        } finally {
            stop();
        }
    }

    @Test
    public void testAddListener_AddBean_Start() throws Exception {
        LifeCycleListenerNestedTest.Foo foo = new LifeCycleListenerNestedTest.Foo();
        LifeCycleListenerNestedTest.CapturingListener listener = new LifeCycleListenerNestedTest.CapturingListener();
        addLifeCycleListener(listener);
        if (WORKAROUND)
            addEventListener(listener);

        LifeCycleListenerNestedTest.Bar bara = new LifeCycleListenerNestedTest.Bar("a");
        LifeCycleListenerNestedTest.Bar barb = new LifeCycleListenerNestedTest.Bar("b");
        addBean(bara);
        addBean(barb);
        try {
            start();
            MatcherAssert.assertThat("Foo.started", isStarted(), Matchers.is(true));
            MatcherAssert.assertThat("Bar(a).started", isStarted(), Matchers.is(true));
            MatcherAssert.assertThat("Bar(b).started", isStarted(), Matchers.is(true));
            listener.assertEvents(Matchers.hasItem("Foo - STARTING"));
            listener.assertEvents(Matchers.hasItem("Foo - STARTED"));
            listener.assertEvents(Matchers.hasItem("Bar(a) - STARTING"));
            listener.assertEvents(Matchers.hasItem("Bar(a) - STARTED"));
            listener.assertEvents(Matchers.hasItem("Bar(b) - STARTING"));
            listener.assertEvents(Matchers.hasItem("Bar(b) - STARTED"));
        } finally {
            stop();
        }
    }

    @Test
    public void testAddListener_Start_AddBean() throws Exception {
        LifeCycleListenerNestedTest.Foo foo = new LifeCycleListenerNestedTest.Foo();
        LifeCycleListenerNestedTest.Bar bara = new LifeCycleListenerNestedTest.Bar("a");
        LifeCycleListenerNestedTest.Bar barb = new LifeCycleListenerNestedTest.Bar("b");
        LifeCycleListenerNestedTest.CapturingListener listener = new LifeCycleListenerNestedTest.CapturingListener();
        addLifeCycleListener(listener);
        if (WORKAROUND)
            addEventListener(listener);

        try {
            start();
            listener.assertEvents(Matchers.hasItem("Foo - STARTING"));
            listener.assertEvents(Matchers.hasItem("Foo - STARTED"));
            addBean(bara);
            addBean(barb);
            start();
            start();
            MatcherAssert.assertThat("Bar(a).started", isStarted(), Matchers.is(true));
            MatcherAssert.assertThat("Bar(b).started", isStarted(), Matchers.is(true));
            listener.assertEvents(Matchers.hasItem("Bar(a) - STARTING"));
            listener.assertEvents(Matchers.hasItem("Bar(a) - STARTED"));
            listener.assertEvents(Matchers.hasItem("Bar(b) - STARTING"));
            listener.assertEvents(Matchers.hasItem("Bar(b) - STARTED"));
        } finally {
            stop();
            stop();
            stop();
        }
    }
}

