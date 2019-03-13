package org.junit.rules;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.TestCouldNotBeSkippedException;
import org.junit.experimental.results.PrintableResult;
import org.junit.experimental.results.ResultMatchers;
import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.matchers.ThrowableCauseMatcher;
import org.junit.internal.runners.statements.Fail;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;


public class ExternalResourceRuleTest {
    private static String callSequence;

    public static class UsesExternalResource {
        @Rule
        public ExternalResource resource = new ExternalResource() {
            @Override
            protected void before() throws Throwable {
                ExternalResourceRuleTest.callSequence += "before ";
            }

            @Override
            protected void after() {
                ExternalResourceRuleTest.callSequence += "after ";
            }
        };

        @Test
        public void testFoo() {
            ExternalResourceRuleTest.callSequence += "test ";
        }
    }

    @Test
    public void externalResourceGeneratesCorrectSequence() {
        ExternalResourceRuleTest.callSequence = "";
        MatcherAssert.assertThat(PrintableResult.testResult(ExternalResourceRuleTest.UsesExternalResource.class), ResultMatchers.isSuccessful());
        Assert.assertEquals("before test after ", ExternalResourceRuleTest.callSequence);
    }

    @Test
    public void shouldThrowMultipleFailureExceptionWhenTestFailsAndClosingResourceFails() throws Throwable {
        // given
        ExternalResource resourceRule = new ExternalResource() {
            @Override
            protected void after() {
                throw new RuntimeException("simulating resource tear down failure");
            }
        };
        Statement failingTest = new Fail(new RuntimeException("simulated test failure"));
        Description dummyDescription = Description.createTestDescription("dummy test class name", "dummy test name");
        try {
            resourceRule.apply(failingTest, dummyDescription).evaluate();
            Assert.fail("ExternalResource should throw");
        } catch (MultipleFailureException e) {
            MatcherAssert.assertThat(e.getMessage(), CoreMatchers.allOf(CoreMatchers.containsString("simulated test failure"), CoreMatchers.containsString("simulating resource tear down failure")));
        }
    }

    public static class TestFailsAndTwoClosingResourcesFail {
        @Rule
        public ExternalResource resourceRule1 = new ExternalResource() {
            @Override
            protected void after() {
                throw new RuntimeException("simulating resource1 tear down failure");
            }
        };

        @Rule
        public ExternalResource resourceRule2 = new ExternalResource() {
            @Override
            protected void after() {
                throw new RuntimeException("simulating resource2 tear down failure");
            }
        };

        @Test
        public void failingTest() {
            throw new RuntimeException("simulated test failure");
        }
    }

    @Test
    public void shouldThrowMultipleFailureExceptionWhenTestFailsAndTwoClosingResourcesFail() {
        Result result = JUnitCore.runClasses(ExternalResourceRuleTest.TestFailsAndTwoClosingResourcesFail.class);
        Assert.assertEquals(3, result.getFailures().size());
        List<String> messages = new ArrayList<String>();
        for (Failure failure : result.getFailures()) {
            messages.add(failure.getMessage());
        }
        MatcherAssert.assertThat(messages, CoreMatchers.hasItems("simulated test failure", "simulating resource1 tear down failure", "simulating resource2 tear down failure"));
    }

    @Test
    public void shouldWrapAssumptionFailuresWhenClosingResourceFails() throws Throwable {
        // given
        final AtomicReference<Throwable> externalResourceException = new AtomicReference<Throwable>();
        ExternalResource resourceRule = new ExternalResource() {
            @Override
            protected void after() {
                RuntimeException runtimeException = new RuntimeException("simulating resource tear down failure");
                externalResourceException.set(runtimeException);
                throw runtimeException;
            }
        };
        final AtomicReference<Throwable> assumptionViolatedException = new AtomicReference<Throwable>();
        Statement skippedTest = new Statement() {
            @Override
            public void evaluate() throws Throwable {
                AssumptionViolatedException assumptionFailure = new AssumptionViolatedException("skip it");
                assumptionViolatedException.set(assumptionFailure);
                throw assumptionFailure;
            }
        };
        Description dummyDescription = Description.createTestDescription("dummy test class name", "dummy test name");
        try {
            resourceRule.apply(skippedTest, dummyDescription).evaluate();
            Assert.fail("ExternalResource should throw");
        } catch (MultipleFailureException e) {
            MatcherAssert.assertThat(e.getFailures(), hasItems(CoreMatchers.instanceOf(TestCouldNotBeSkippedException.class), CoreMatchers.sameInstance(externalResourceException.get())));
            MatcherAssert.assertThat(e.getFailures(), hasItems(ThrowableCauseMatcher.hasCause(CoreMatchers.sameInstance(assumptionViolatedException.get())), CoreMatchers.sameInstance(externalResourceException.get())));
        }
    }
}

