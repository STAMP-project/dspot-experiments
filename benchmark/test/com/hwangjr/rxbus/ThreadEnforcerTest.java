package com.hwangjr.rxbus;


import com.hwangjr.rxbus.thread.ThreadEnforcer;
import junit.framework.Assert;
import org.junit.Test;


public class ThreadEnforcerTest {
    private static class RecordingThreadEnforcer implements ThreadEnforcer {
        boolean called = false;

        @Override
        public void enforce(Bus bus) {
            called = true;
        }
    }

    @Test
    public void enforerCalledForRegister() {
        ThreadEnforcerTest.RecordingThreadEnforcer enforcer = new ThreadEnforcerTest.RecordingThreadEnforcer();
        Bus bus = new Bus(enforcer);
        Assert.assertFalse(enforcer.called);
        bus.register(this);
        Assert.assertTrue(enforcer.called);
    }

    @Test
    public void enforcerCalledForPost() {
        ThreadEnforcerTest.RecordingThreadEnforcer enforcer = new ThreadEnforcerTest.RecordingThreadEnforcer();
        Bus bus = new Bus(enforcer);
        Assert.assertFalse(enforcer.called);
        bus.post(this);
        Assert.assertTrue(enforcer.called);
    }

    @Test
    public void enforcerCalledForUnregister() {
        ThreadEnforcerTest.RecordingThreadEnforcer enforcer = new ThreadEnforcerTest.RecordingThreadEnforcer();
        Bus bus = new Bus(enforcer);
        Assert.assertFalse(enforcer.called);
        bus.unregister(this);
        Assert.assertTrue(enforcer.called);
    }
}

