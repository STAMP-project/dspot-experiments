package org.junit.tests.junit3compatibility;


import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;


public class SuiteMethodTest {
    public static boolean wasRun;

    public static class OldTest extends TestCase {
        public OldTest(String name) {
            super(name);
        }

        public static Test suite() {
            TestSuite suite = new TestSuite();
            suite.addTest(new SuiteMethodTest.OldTest("notObviouslyATest"));
            return suite;
        }

        public void notObviouslyATest() {
            SuiteMethodTest.wasRun = true;
        }
    }

    @org.junit.Test
    public void makeSureSuiteIsCalled() {
        SuiteMethodTest.wasRun = false;
        JUnitCore.runClasses(SuiteMethodTest.OldTest.class);
        Assert.assertTrue(SuiteMethodTest.wasRun);
    }

    public static class NewTest {
        @org.junit.Test
        public void sample() {
            SuiteMethodTest.wasRun = true;
        }

        public static Test suite() {
            return new JUnit4TestAdapter(SuiteMethodTest.NewTest.class);
        }
    }

    @org.junit.Test
    public void makeSureSuiteWorksWithJUnit4Classes() {
        SuiteMethodTest.wasRun = false;
        JUnitCore.runClasses(SuiteMethodTest.NewTest.class);
        Assert.assertTrue(SuiteMethodTest.wasRun);
    }

    public static class CompatibilityTest {
        @Ignore
        @org.junit.Test
        public void ignored() {
        }

        public static Test suite() {
            return new JUnit4TestAdapter(SuiteMethodTest.CompatibilityTest.class);
        }
    }

    // when executing as JUnit 3, ignored tests are stripped out before execution
    @org.junit.Test
    public void descriptionAndRunNotificationsAreConsistent() {
        Result result = JUnitCore.runClasses(SuiteMethodTest.CompatibilityTest.class);
        Assert.assertEquals(0, result.getIgnoreCount());
        Description description = Request.aClass(SuiteMethodTest.CompatibilityTest.class).getRunner().getDescription();
        Assert.assertEquals(0, description.getChildren().size());
    }

    public static class NewTestSuiteFails {
        @org.junit.Test
        public void sample() {
            SuiteMethodTest.wasRun = true;
        }

        public static Test suite() {
            Assert.fail("called with JUnit 4 runner");
            return null;
        }
    }

    @org.junit.Test
    public void suiteIsUsedWithJUnit4Classes() {
        SuiteMethodTest.wasRun = false;
        Result result = JUnitCore.runClasses(SuiteMethodTest.NewTestSuiteFails.class);
        Assert.assertEquals(1, result.getFailureCount());
        Assert.assertFalse(SuiteMethodTest.wasRun);
    }

    public static class NewTestSuiteNotUsed {
        private static boolean wasIgnoredRun;

        @org.junit.Test
        public void sample() {
            SuiteMethodTest.wasRun = true;
        }

        @Ignore
        @org.junit.Test
        public void ignore() {
            SuiteMethodTest.NewTestSuiteNotUsed.wasIgnoredRun = true;
        }

        public static Test suite() {
            return new JUnit4TestAdapter(SuiteMethodTest.NewTestSuiteNotUsed.class);
        }
    }

    @org.junit.Test
    public void makeSureSuiteNotUsedWithJUnit4Classes2() {
        SuiteMethodTest.wasRun = false;
        SuiteMethodTest.NewTestSuiteNotUsed.wasIgnoredRun = false;
        Result res = JUnitCore.runClasses(SuiteMethodTest.NewTestSuiteNotUsed.class);
        Assert.assertTrue(SuiteMethodTest.wasRun);
        Assert.assertFalse(SuiteMethodTest.NewTestSuiteNotUsed.wasIgnoredRun);
        Assert.assertEquals(0, res.getFailureCount());
        Assert.assertEquals(1, res.getRunCount());
        Assert.assertEquals(0, res.getIgnoreCount());
    }
}

