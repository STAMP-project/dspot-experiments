package org.junit.internal.runners;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InvalidTestClassError;


public class ErrorReportingRunnerTest {
    @Test(expected = NullPointerException.class)
    public void cannotCreateWithNullClass() {
        new ErrorReportingRunner(null, new RuntimeException());
    }

    @Test(expected = NullPointerException.class)
    public void cannotCreateWithNullClass2() {
        new ErrorReportingRunner(new RuntimeException(), ((Class<?>) (null)));
    }

    @Test(expected = NullPointerException.class)
    public void cannotCreateWithNullClasses() {
        new ErrorReportingRunner(new RuntimeException(), ((Class<?>[]) (null)));
    }

    @Test(expected = NullPointerException.class)
    public void cannotCreateWithoutClass() {
        new ErrorReportingRunner(new RuntimeException());
    }

    @Test
    public void givenInvalidTestClassErrorAsCause() {
        final List<Failure> firedFailures = new ArrayList<Failure>();
        InvalidTestClassError testClassError = new InvalidTestClassError(ErrorReportingRunnerTest.TestClassWithErrors.class, Arrays.asList(new Throwable("validation error 1"), new Throwable("validation error 2")));
        ErrorReportingRunner sut = new ErrorReportingRunner(ErrorReportingRunnerTest.TestClassWithErrors.class, testClassError);
        sut.run(new RunNotifier() {
            @Override
            public void fireTestFailure(Failure failure) {
                super.fireTestFailure(failure);
                firedFailures.add(failure);
            }
        });
        MatcherAssert.assertThat(firedFailures.size(), CoreMatchers.is(1));
        Throwable exception = firedFailures.get(0).getException();
        MatcherAssert.assertThat(exception, CoreMatchers.instanceOf(InvalidTestClassError.class));
        MatcherAssert.assertThat(((InvalidTestClassError) (exception)), CoreMatchers.is(testClassError));
    }

    @Test
    public void givenInvalidTestClass_integrationTest() {
        Result result = JUnitCore.runClasses(ErrorReportingRunnerTest.TestClassWithErrors.class);
        MatcherAssert.assertThat(result.getFailureCount(), CoreMatchers.is(1));
        Throwable failure = result.getFailures().get(0).getException();
        MatcherAssert.assertThat(failure, CoreMatchers.instanceOf(InvalidTestClassError.class));
        MatcherAssert.assertThat(failure.getMessage(), CoreMatchers.allOf(CoreMatchers.startsWith((("Invalid test class '" + (ErrorReportingRunnerTest.TestClassWithErrors.class.getName())) + "'")), CoreMatchers.containsString("\n  1. "), CoreMatchers.containsString("\n  2. ")));
    }

    private static class TestClassWithErrors {
        @Before
        public static void staticBeforeMethod() {
        }

        @After
        public static void staticAfterMethod() {
        }

        @Test
        public String testMethodReturningString() {
            return "this should not be allowed";
        }
    }
}

