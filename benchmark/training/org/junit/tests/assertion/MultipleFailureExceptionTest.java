package org.junit.tests.assertion;


import java.lang.annotation.AnnotationFormatError;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.junit.TestCouldNotBeSkippedException;
import org.junit.internal.AssumptionViolatedException;
import org.junit.runners.model.MultipleFailureException;


/**
 * Tests for {@link org.junit.runners.model.MultipleFailureException}
 *
 * @author kcooney@google.com (Kevin Cooney)
 */
public class MultipleFailureExceptionTest {
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    @Test
    public void assertEmptyDoesNotThrowForEmptyList() throws Exception {
        MultipleFailureException.assertEmpty(Collections.<Throwable>emptyList());
    }

    @Test
    public void assertEmptyRethrowsSingleRuntimeException() throws Exception {
        Throwable exception = new MultipleFailureExceptionTest.ExpectedException("pesto");
        List<Throwable> errors = Collections.singletonList(exception);
        try {
            MultipleFailureException.assertEmpty(errors);
            Assert.fail();
        } catch (MultipleFailureExceptionTest.ExpectedException e) {
            Assert.assertSame(e, exception);
        }
    }

    @Test
    public void assertEmptyRethrowsSingleError() throws Exception {
        Throwable exception = new AnnotationFormatError("changeo");
        List<Throwable> errors = Collections.singletonList(exception);
        try {
            MultipleFailureException.assertEmpty(errors);
            Assert.fail();
        } catch (AnnotationFormatError e) {
            Assert.assertSame(e, exception);
        }
    }

    @Test
    public void assertEmptyThrowsMultipleFailureExceptionForManyThrowables() throws Exception {
        List<Throwable> errors = new ArrayList<Throwable>();
        errors.add(new MultipleFailureExceptionTest.ExpectedException("basil"));
        errors.add(new RuntimeException("garlic"));
        try {
            MultipleFailureException.assertEmpty(errors);
            Assert.fail();
        } catch (MultipleFailureException expected) {
            MatcherAssert.assertThat(expected.getFailures(), CoreMatchers.equalTo(errors));
            Assert.assertTrue(expected.getMessage().startsWith(("There were 2 errors:" + (MultipleFailureExceptionTest.LINE_SEPARATOR))));
            Assert.assertTrue(expected.getMessage().contains(("ExpectedException(basil)" + (MultipleFailureExceptionTest.LINE_SEPARATOR))));
            Assert.assertTrue(expected.getMessage().contains("RuntimeException(garlic)"));
        }
    }

    @Test
    public void assertEmptyErrorListConstructorFailure() {
        try {
            new MultipleFailureException(Collections.<Throwable>emptyList());
            Assert.fail();
        } catch (IllegalArgumentException expected) {
            MatcherAssert.assertThat(expected.getMessage(), CoreMatchers.containsString("List of Throwables must not be empty"));
        }
    }

    @Test
    public void assertEmptyWrapsAssumptionFailuresForManyThrowables() throws Exception {
        List<Throwable> errors = new ArrayList<Throwable>();
        AssumptionViolatedException assumptionViolatedException = new AssumptionViolatedException("skip it");
        errors.add(assumptionViolatedException);
        errors.add(new RuntimeException("garlic"));
        try {
            MultipleFailureException.assertEmpty(errors);
            Assert.fail();
        } catch (MultipleFailureException expected) {
            MatcherAssert.assertThat(expected.getFailures().size(), CoreMatchers.equalTo(2));
            Assert.assertTrue(expected.getMessage().startsWith(("There were 2 errors:" + (MultipleFailureExceptionTest.LINE_SEPARATOR))));
            Assert.assertTrue(expected.getMessage().contains("TestCouldNotBeSkippedException(Test could not be skipped"));
            Assert.assertTrue(expected.getMessage().contains("RuntimeException(garlic)"));
            Throwable first = expected.getFailures().get(0);
            MatcherAssert.assertThat(first, CoreMatchers.instanceOf(TestCouldNotBeSkippedException.class));
            Throwable cause = getCause();
            MatcherAssert.assertThat(cause, CoreMatchers.instanceOf(AssumptionViolatedException.class));
            MatcherAssert.assertThat(((AssumptionViolatedException) (cause)), CoreMatchers.sameInstance(assumptionViolatedException));
        }
    }

    private static class ExpectedException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public ExpectedException(String message) {
            super(message);
        }
    }
}

