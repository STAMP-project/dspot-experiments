package org.junit.tests.running.methods;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;


public class InheritedTestTest {
    public abstract static class Super {
        @Test
        public void nothing() {
        }
    }

    public static class Sub extends InheritedTestTest.Super {}

    @Test
    public void subclassWithOnlyInheritedTestsRuns() {
        Result result = JUnitCore.runClasses(InheritedTestTest.Sub.class);
        Assert.assertTrue(result.wasSuccessful());
    }

    public static class SubWithBefore extends InheritedTestTest.Super {
        @Before
        public void gack() {
            Assert.fail();
        }
    }

    @Test
    public void subclassWithInheritedTestAndOwnBeforeRunsBefore() {
        Assert.assertFalse(JUnitCore.runClasses(InheritedTestTest.SubWithBefore.class).wasSuccessful());
    }
}

