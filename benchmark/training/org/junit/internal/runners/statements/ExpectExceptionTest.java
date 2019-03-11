package org.junit.internal.runners.statements;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.internal.AssumptionViolatedException;
import org.junit.runners.model.Statement;


/**
 * Integration tests can be found in {@link org.junit.tests.running.methods.ExpectedTest}.
 * See e.g. {@link org.junit.tests.running.methods.ExpectedTest#expectsAssumptionViolatedException()}
 */
public class ExpectExceptionTest {
    @Test
    public void whenExpectingAssumptionViolatedExceptionStatementsThrowingItShouldPass() {
        Statement delegate = new Fail(new AssumptionViolatedException("expected"));
        ExpectException expectException = new ExpectException(delegate, AssumptionViolatedException.class);
        try {
            expectException.evaluate();
            // then AssumptionViolatedException should not be thrown
        } catch (Throwable e) {
            // need to explicitly catch and re-throw as an AssertionError or it might be skipped
            Assert.fail(("should not throw anything, but was thrown: " + e));
        }
    }

    @Test
    public void whenExpectingAssumptionViolatedExceptionStatementsThrowingSubclassShouldPass() {
        Statement delegate = new Fail(new ExpectExceptionTest.AssumptionViolatedExceptionSubclass("expected"));
        ExpectException expectException = new ExpectException(delegate, AssumptionViolatedException.class);
        try {
            expectException.evaluate();
            // then no exception should be thrown
        } catch (Throwable e) {
            Assert.fail(("should not throw anything, but was thrown: " + e));
        }
    }

    @Test
    public void whenExpectingAssumptionViolatedExceptionStatementsThrowingDifferentExceptionShouldFail() {
        Statement delegate = new Fail(new ExpectExceptionTest.SomeException("not expected"));
        ExpectException expectException = new ExpectException(delegate, AssumptionViolatedException.class);
        try {
            expectException.evaluate();
            Assert.fail("should throw 'Unexpected exception' when statement throws an exception which is not the one expected");
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.equalTo(("Unexpected exception, expected<org.junit.internal.AssumptionViolatedException> " + "but was<org.junit.internal.runners.statements.ExpectExceptionTest$SomeException>")));
        }
    }

    @Test
    public void whenExpectingAssumptionViolatedExceptionStatementsPassingShouldFail() throws Exception {
        ExpectException expectException = new ExpectException(new ExpectExceptionTest.PassingStatement(), AssumptionViolatedException.class);
        try {
            expectException.evaluate();
        } catch (AssertionError e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString(("Expected exception: " + (AssumptionViolatedException.class.getName()))));
            return;
        }
        Assert.fail("ExpectException should throw when the given statement passes");
    }

    private static class PassingStatement extends Statement {
        public void evaluate() throws Throwable {
            // nop
        }
    }

    private static class SomeException extends RuntimeException {
        public SomeException(String message) {
            super(message);
        }
    }

    private static class AssumptionViolatedExceptionSubclass extends AssumptionViolatedException {
        public AssumptionViolatedExceptionSubclass(String assumption) {
            super(assumption);
        }
    }
}

