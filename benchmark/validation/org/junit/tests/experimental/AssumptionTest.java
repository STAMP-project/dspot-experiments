package org.junit.tests.experimental;


import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.AssumptionViolatedException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.experimental.results.ResultMatchers;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;


public class AssumptionTest {
    public static class HasFailingAssumption {
        @Test
        public void assumptionsFail() {
            Assume.assumeThat(3, CoreMatchers.is(4));
            Assert.fail();
        }
    }

    @Test
    public void failedAssumptionsMeanPassing() {
        Result result = JUnitCore.runClasses(AssumptionTest.HasFailingAssumption.class);
        Assert.assertThat(result.getRunCount(), CoreMatchers.is(1));
        Assert.assertThat(result.getIgnoreCount(), CoreMatchers.is(0));
        Assert.assertThat(result.getFailureCount(), CoreMatchers.is(0));
    }

    private static int assumptionFailures = 0;

    @Test
    public void failedAssumptionsCanBeDetectedByListeners() {
        AssumptionTest.assumptionFailures = 0;
        JUnitCore core = new JUnitCore();
        core.addListener(new RunListener() {
            @Override
            public void testAssumptionFailure(Failure failure) {
                (AssumptionTest.assumptionFailures)++;
            }
        });
        core.run(AssumptionTest.HasFailingAssumption.class);
        Assert.assertThat(AssumptionTest.assumptionFailures, CoreMatchers.is(1));
    }

    public static class HasPassingAssumption {
        @Test
        public void assumptionsFail() {
            Assume.assumeThat(3, CoreMatchers.is(3));
            Assert.fail();
        }
    }

    @Test
    public void passingAssumptionsScootThrough() {
        Result result = JUnitCore.runClasses(AssumptionTest.HasPassingAssumption.class);
        Assert.assertThat(result.getRunCount(), CoreMatchers.is(1));
        Assert.assertThat(result.getIgnoreCount(), CoreMatchers.is(0));
        Assert.assertThat(result.getFailureCount(), CoreMatchers.is(1));
    }

    @Test
    public void assumeThatWorks() {
        try {
            Assume.assumeThat(1, CoreMatchers.is(2));
            Assert.fail("should throw AssumptionViolatedException");
        } catch (AssumptionViolatedException e) {
            // expected
        }
    }

    @Test
    public void assumeThatPasses() {
        Assume.assumeThat(1, CoreMatchers.is(1));
        assertCompletesNormally();
    }

    @Test
    public void assumeThatPassesOnStrings() {
        Assume.assumeThat("x", CoreMatchers.is("x"));
        assertCompletesNormally();
    }

    @Test
    public void assumeNotNullThrowsException() {
        Object[] objects = new Object[]{ 1, 2, null };
        try {
            Assume.assumeNotNull(objects);
            Assert.fail("should throw AssumptionViolatedException");
        } catch (AssumptionViolatedException e) {
            // expected
        }
    }

    @Test
    public void assumeNotNullThrowsExceptionForNullArray() {
        try {
            Assume.assumeNotNull(((Object[]) (null)));
            Assert.fail("should throw AssumptionViolatedException");
        } catch (AssumptionViolatedException e) {
            // expected
        }
    }

    @Test
    public void assumeNotNullPasses() {
        Object[] objects = new Object[]{ 1, 2 };
        Assume.assumeNotNull(objects);
        assertCompletesNormally();
    }

    @Test
    public void assumeNotNullIncludesParameterList() {
        try {
            Object[] objects = new Object[]{ 1, 2, null };
            Assume.assumeNotNull(objects);
        } catch (AssumptionViolatedException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("1, 2, null"));
        } catch (Exception e) {
            Assert.fail("Should have thrown AssumptionViolatedException");
        }
    }

    @Test
    public void assumeNoExceptionThrows() {
        final Throwable exception = new NullPointerException();
        try {
            Assume.assumeNoException(exception);
            Assert.fail("Should have thrown exception");
        } catch (AssumptionViolatedException e) {
            Assert.assertThat(e.getCause(), CoreMatchers.is(exception));
        }
    }

    @Test
    public void assumeTrueWorks() {
        try {
            Assume.assumeTrue(false);
            Assert.fail("should throw AssumptionViolatedException");
        } catch (AssumptionViolatedException e) {
            // expected
        }
    }

    public static class HasFailingAssumeInBefore {
        @Before
        public void checkForSomethingThatIsntThere() {
            Assume.assumeTrue(false);
        }

        @Test
        public void failing() {
            Assert.fail();
        }
    }

    @Test
    public void failingAssumptionInBeforePreventsTestRun() {
        Assert.assertThat(PrintableResult.testResult(AssumptionTest.HasFailingAssumeInBefore.class), ResultMatchers.isSuccessful());
    }

    public static class HasFailingAssumeInBeforeClass {
        @BeforeClass
        public static void checkForSomethingThatIsntThere() {
            Assume.assumeTrue(false);
        }

        @Test
        public void failing() {
            Assert.fail();
        }
    }

    @Test
    public void failingAssumptionInBeforeClassIgnoresClass() {
        Assert.assertThat(PrintableResult.testResult(AssumptionTest.HasFailingAssumeInBeforeClass.class), ResultMatchers.isSuccessful());
    }

    public static class AssumptionFailureInConstructor {
        public AssumptionFailureInConstructor() {
            Assume.assumeTrue(false);
        }

        @Test
        public void shouldFail() {
            Assert.fail();
        }
    }

    @Test
    public void failingAssumptionInConstructorIgnoresClass() {
        Assert.assertThat(PrintableResult.testResult(AssumptionTest.AssumptionFailureInConstructor.class), ResultMatchers.isSuccessful());
    }

    public static class TestClassWithAssumptionFailure {
        @Test(expected = IllegalArgumentException.class)
        public void assumeWithExpectedException() {
            Assume.assumeTrue(false);
        }
    }

    @Test
    public void assumeWithExpectedExceptionShouldThrowAssumptionViolatedException() {
        Result result = JUnitCore.runClasses(AssumptionTest.TestClassWithAssumptionFailure.class);
        Assert.assertThat(getAssumptionFailureCount(), CoreMatchers.is(1));
    }

    static final String message = "Some random message string.";

    static final Throwable e = new Throwable();

    /**
     *
     *
     * @see AssumptionTest#assumptionsWithMessage()
     */
    public static class HasAssumeWithMessage {
        @Test
        public void testMethod() {
            Assume.assumeTrue(AssumptionTest.message, false);
        }
    }

    @Test
    public void assumptionsWithMessage() {
        final List<Failure> failures = AssumptionTest.runAndGetAssumptionFailures(AssumptionTest.HasAssumeWithMessage.class);
        Assert.assertTrue(failures.get(0).getMessage().contains(AssumptionTest.message));
    }

    /**
     *
     *
     * @see AssumptionTest#assumptionsWithMessageAndCause()
     */
    public static class HasAssumeWithMessageAndCause {
        @Test
        public void testMethod() {
            Assume.assumeNoException(AssumptionTest.message, AssumptionTest.e);
        }
    }

    @Test
    public void assumptionsWithMessageAndCause() {
        final List<Failure> failures = AssumptionTest.runAndGetAssumptionFailures(AssumptionTest.HasAssumeWithMessageAndCause.class);
        Assert.assertTrue(failures.get(0).getMessage().contains(AssumptionTest.message));
        Assert.assertSame(failures.get(0).getException().getCause(), AssumptionTest.e);
    }

    public static class HasFailingAssumptionWithMessage {
        @Test
        public void assumptionsFail() {
            Assume.assumeThat(AssumptionTest.message, 3, CoreMatchers.is(4));
            Assert.fail();
        }
    }

    @Test
    public void failedAssumptionsWithMessage() {
        final List<Failure> failures = AssumptionTest.runAndGetAssumptionFailures(AssumptionTest.HasFailingAssumptionWithMessage.class);
        Assert.assertEquals(1, failures.size());
        Assert.assertTrue(failures.get(0).getMessage().contains(AssumptionTest.message));
    }
}

