package org.junit.tests.running.core;


import org.junit.Assert;
import org.junit.Test;


public class JUnitCoreReturnsCorrectExitCodeTest {
    public static class Fail {
        @Test
        public void kaboom() {
            Assert.fail();
        }
    }

    @Test
    public void failureCausesExitCodeOf1() throws Exception {
        runClass(((getClass().getName()) + "$Fail"), 1);
    }

    @Test
    public void missingClassCausesExitCodeOf1() throws Exception {
        runClass("Foo", 1);
    }

    public static class Succeed {
        @Test
        public void peacefulSilence() {
        }
    }

    @Test
    public void successCausesExitCodeOf0() throws Exception {
        runClass(((getClass().getName()) + "$Succeed"), 0);
    }
}

