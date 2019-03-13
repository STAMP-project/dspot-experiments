package org.gridkit.jvmtool.codec;


import org.gridkit.jvmtool.codec.stacktrace.EventEqualToCondition;
import org.gridkit.jvmtool.codec.stacktrace.ThreadSnapshotEvent;
import org.gridkit.jvmtool.codec.stacktrace.ThreadSnapshotEventPojo;
import org.gridkit.jvmtool.event.CommonEvent;
import org.gridkit.jvmtool.event.ErrorEvent;
import org.gridkit.jvmtool.event.EventDecorator;
import org.gridkit.jvmtool.event.GenericEvent;
import org.gridkit.jvmtool.event.SimpleErrorEvent;
import org.junit.Test;

import static java.lang.Thread.State.NEW;


public class EventDecoratorTest {
    @Test
    public void verify_generic_event() {
        GenericEvent a = new GenericEvent();
        a.timestamp(10000);
        a.tags().put("A", "1");
        a.tags().put("B", "2");
        a.tags().put("B", "3");
        a.tags().put("C", "4");
        a.counters().set("X", 10);
        a.counters().set("zzz", Long.MAX_VALUE);
        GenericEvent b = new GenericEvent(a);
        assertThat(b).is(EventEqualToCondition.eventEquals(a));
        EventDecorator deco = new EventDecorator();
        CommonEvent c = deco.wrap(b);
        assertThat(c).is(EventEqualToCondition.eventEquals(a));
        deco.timestamp(77777);
        deco.tags().put("F", "10");
        deco.counters().set("__", 25);
        assertThat(c.timestamp()).isEqualTo(77777);
        assertThat(c.tags()).containsOnly("A", "B", "C", "F");
        assertThat(c.counters()).containsOnly("X", "zzz", "__");
    }

    @Test
    public void verify_thread_snapshot_event() {
        ThreadSnapshotEventPojo a = new ThreadSnapshotEventPojo();
        a.timestamp(10000);
        a.tags().put("A", "1");
        a.tags().put("B", "2");
        a.tags().put("B", "3");
        a.tags().put("C", "4");
        a.counters().set("X", 10);
        a.counters().set("zzz", Long.MAX_VALUE);
        a.threadName("Test");
        a.threadState(NEW);
        ThreadSnapshotEventPojo b = new ThreadSnapshotEventPojo();
        b.loadFrom(a);
        assertThat(b).is(EventEqualToCondition.eventEquals(a));
        EventDecorator deco = new EventDecorator();
        ThreadSnapshotEvent c = deco.wrap(b);
        assertThat(c).is(EventEqualToCondition.eventEquals(a));
        deco.timestamp(77777);
        deco.tags().put("F", "10");
        deco.counters().set("__", 25);
        assertThat(c.timestamp()).isEqualTo(77777);
        assertThat(c.tags()).containsOnly("A", "B", "C", "F");
        assertThat(c.counters()).containsOnly("X", "zzz", "__");
        assertThat(c.threadName()).isEqualTo("Test");
        assertThat(c.threadState()).isEqualTo(NEW);
    }

    @Test
    public void verify_error_event() {
        SimpleErrorEvent ee = new SimpleErrorEvent(new Exception("Boom"));
        EventDecorator deco = new EventDecorator();
        ErrorEvent c = deco.wrap(ee);
        assertThat(c).is(EventEqualToCondition.eventEquals(ee));
    }
}

