package org.junit.tests.running.classes;


import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;


public class IgnoreClassTest {
    @Ignore("For a good reason")
    public static class IgnoreMe {
        @Test
        public void iFail() {
            Assert.fail();
        }

        @Test
        public void iFailToo() {
            Assert.fail();
        }
    }

    @Test
    public void ignoreClass() {
        Result result = JUnitCore.runClasses(IgnoreClassTest.IgnoreMe.class);
        Assert.assertEquals(0, result.getFailureCount());
        Assert.assertEquals(1, result.getIgnoreCount());
    }
}

