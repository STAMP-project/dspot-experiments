package org.junit.tests.experimental.max;


import java.io.File;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.max.MaxCore;
import org.junit.runner.Description;
import org.junit.runner.Request;


public class JUnit38SortingTest {
    private MaxCore fMax;

    private File fMaxFile;

    public static class JUnit4Test {
        @Test
        public void pass() {
        }
    }

    public static class JUnit38Test extends TestCase {
        public void testFails() {
            TestCase.fail();
        }

        public void testSucceeds() {
        }

        public void testSucceedsToo() {
        }
    }

    @Test
    public void preferRecentlyFailed38Test() {
        Request request = Request.classes(JUnit38SortingTest.JUnit4Test.class, JUnit38SortingTest.JUnit38Test.class);
        fMax.run(request);
        List<Description> tests = fMax.sortedLeavesForTest(request);
        Description dontSucceed = Description.createTestDescription(JUnit38SortingTest.JUnit38Test.class, "testFails");
        Assert.assertEquals(dontSucceed, tests.get(0));
    }
}

