package com.orientechnologies.common.test;


import org.junit.Test;


public abstract class SpeedTestAbstract implements SpeedTest {
    protected final SpeedTestData data;

    protected SpeedTestAbstract() {
        data = new SpeedTestData();
    }

    protected SpeedTestAbstract(final long iCycles) {
        data = new SpeedTestData(iCycles);
    }

    protected SpeedTestAbstract(final SpeedTestGroup iGroup) {
        data = new SpeedTestData(iGroup);
    }

    @Test
    public void test() {
        data.go(this);
    }
}

