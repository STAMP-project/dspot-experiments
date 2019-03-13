package org.junit.tests.junit3compatibility;


import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


public class JUnit4TestAdapterTest {
    public static class Test4 {
        @Test
        public void pass() throws Exception {
            // pass
        }
    }

    @RunWith(Suite.class)
    @Suite.SuiteClasses(JUnit4TestAdapterTest.Test4.class)
    public static class TestSuiteFor4 {}

    @Test
    public void testJUnit4Suite() {
        JUnit4TestAdapterTest.doTest(JUnit4TestAdapterTest.TestSuiteFor4.class);
    }

    public static class Test3 extends TestCase {
        public void testPass() throws Exception {
            // pass
        }
    }

    @RunWith(Suite.class)
    @Suite.SuiteClasses(JUnit4TestAdapterTest.Test3.class)
    public static class TestSuiteFor3 {}

    @Test
    public void testJUnit3Suite() {
        JUnit4TestAdapterTest.doTest(JUnit4TestAdapterTest.TestSuiteFor3.class);
    }

    public static class TestSuite3 {
        public static junit.framework.Test suite() {
            return new TestSuite(JUnit4TestAdapterTest.Test3.class);
        }
    }

    @RunWith(Suite.class)
    @Suite.SuiteClasses(JUnit4TestAdapterTest.TestSuite3.class)
    public static class TestSuite4ForTestSuite3 {}

    @Test
    public void testJUnit4SuiteThatContainsJUnit3SuiteClass() {
        JUnit4TestAdapterTest.doTest(JUnit4TestAdapterTest.TestSuite4ForTestSuite3.class);
    }
}

