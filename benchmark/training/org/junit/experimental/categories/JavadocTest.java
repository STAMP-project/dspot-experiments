package org.junit.experimental.categories;


import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


/**
 *
 *
 * @author tibor17
 * @version 4.12
 * @since 4.12
 */
public class JavadocTest {
    public static interface FastTests {}

    public static interface SlowTests {}

    public static interface SmokeTests {}

    public static class A {
        public void a() {
            Assert.fail();
        }

        @Category(JavadocTest.SlowTests.class)
        @Test
        public void b() {
        }

        @Category({ JavadocTest.FastTests.class, JavadocTest.SmokeTests.class })
        @Test
        public void c() {
        }
    }

    @Category({ JavadocTest.SlowTests.class, JavadocTest.FastTests.class })
    public static class B {
        @Test
        public void d() {
        }
    }

    // Will run A.b and B.d, but not A.a and A.c
    @RunWith(Categories.class)
    @Categories.IncludeCategory(JavadocTest.SlowTests.class)
    @Suite.SuiteClasses({ JavadocTest.A.class, JavadocTest.B.class })
    public static class SlowTestSuite {}

    // Will run A.c and B.d, but not A.b because it is not any of FastTests or SmokeTests
    @RunWith(Categories.class)
    @Categories.IncludeCategory({ JavadocTest.FastTests.class, JavadocTest.SmokeTests.class })
    @Suite.SuiteClasses({ JavadocTest.A.class, JavadocTest.B.class })
    public static class FastOrSmokeTestSuite {}

    @Test
    public void slowTests() {
        Result testResult = JUnitCore.runClasses(JavadocTest.SlowTestSuite.class);
        Assert.assertTrue(testResult.wasSuccessful());
        Assert.assertThat("unexpected run count", testResult.getRunCount(), Is.is(2));
        Assert.assertThat("unexpected failure count", testResult.getFailureCount(), Is.is(0));
    }

    @Test
    public void fastSmokeTests() {
        Result testResult = JUnitCore.runClasses(JavadocTest.FastOrSmokeTestSuite.class);
        Assert.assertTrue(testResult.wasSuccessful());
        Assert.assertThat("unexpected run count", testResult.getRunCount(), Is.is(2));
        Assert.assertThat("unexpected failure count", testResult.getFailureCount(), Is.is(0));
    }
}

