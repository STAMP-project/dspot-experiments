package com.hwangjr.rxbus;


import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.thread.EventThread;
import com.hwangjr.rxbus.thread.ThreadEnforcer;
import junit.framework.Assert;
import org.junit.Test;


/**
 * Stress test of {@link Bus} against inner classes. The anon inner class tests
 * were broken when we switched to weak references.
 */
public class EventBusInnerClassStressTest {
    public static final int REPS = 1000000;

    boolean called;

    class Sub {
        @Subscribe(thread = EventThread.IMMEDIATE)
        public void in(Object o) {
            called = true;
        }
    }

    EventBusInnerClassStressTest.Sub sub = new EventBusInnerClassStressTest.Sub();

    @Test
    public void eventBusOkayWithNonStaticInnerClass() {
        Bus eb = new Bus(ThreadEnforcer.ANY);
        eb.register(sub);
        int i = 0;
        while (i < (EventBusInnerClassStressTest.REPS)) {
            called = false;
            i++;
            eb.post(EventBusInnerClassStressTest.nextEvent(i));
            Assert.assertTrue(("Failed at " + i), called);
        } 
    }

    @Test
    public void eventBusFailWithAnonInnerClass() {
        Bus eb = new Bus(ThreadEnforcer.ANY);
        eb.register(new Object() {
            @Subscribe(thread = EventThread.IMMEDIATE)
            public void in(String o) {
                called = true;
            }
        });
        int i = 0;
        while (i < (EventBusInnerClassStressTest.REPS)) {
            called = false;
            i++;
            eb.post(EventBusInnerClassStressTest.nextEvent(i));
            Assert.assertTrue(("Failed at " + i), called);
        } 
    }

    @Test
    public void eventBusNpeWithAnonInnerClassWaitingForObject() {
        Bus eb = new Bus(ThreadEnforcer.ANY);
        eb.register(new Object() {
            @Subscribe(thread = EventThread.IMMEDIATE)
            public void in(Object o) {
                called = true;
            }
        });
        int i = 0;
        while (i < (EventBusInnerClassStressTest.REPS)) {
            called = false;
            i++;
            eb.post(EventBusInnerClassStressTest.nextEvent(i));
            Assert.assertTrue(("Failed at " + i), called);
        } 
    }
}

