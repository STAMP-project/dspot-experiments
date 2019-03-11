package org.junit.rules;


import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class ExpectedExceptionTest {
    private static final String ARBITRARY_MESSAGE = "arbitrary message";

    private final Class<?> classUnderTest;

    private final Matcher<EventCollector> matcher;

    public ExpectedExceptionTest(Class<?> classUnderTest, Matcher<EventCollector> matcher) {
        this.classUnderTest = classUnderTest;
        this.matcher = matcher;
    }

    @Test
    public void runTestAndVerifyResult() {
        EventCollector collector = new EventCollector();
        JUnitCore core = new JUnitCore();
        core.addListener(collector);
        core.run(classUnderTest);
        Assert.assertThat(collector, matcher);
    }

    public static class EmptyTestExpectingNoException {
        @Rule
        public ExpectedException thrown = ExpectedException.none();

        @Test
        public void throwsNothing() {
        }
    }

    public static class ThrowExceptionWithExpectedType {
        @Rule
        public ExpectedException thrown = ExpectedException.none();

        @Test
        public void throwsNullPointerException() {
            thrown.expect(NullPointerException.class);
            throw new NullPointerException();
        }
    }

    public static class ThrowExceptionWithExpectedPartOfMessage {
        @Rule
        public ExpectedException thrown = ExpectedException.none();

        @Test
        public void throwsNullPointerExceptionWithMessage() {
            thrown.expect(NullPointerException.class);
            thrown.expectMessage(ExpectedExceptionTest.ARBITRARY_MESSAGE);
            throw new NullPointerException(((ExpectedExceptionTest.ARBITRARY_MESSAGE) + "something else"));
        }
    }

    public static class ThrowExceptionWithWrongType {
        @Rule
        public ExpectedException thrown = ExpectedException.none();

        @Test
        public void throwsNullPointerException() {
            thrown.expect(NullPointerException.class);
            throw new IllegalArgumentException();
        }
    }

    public static class HasWrongMessage {
        @Rule
        public ExpectedException thrown = ExpectedException.none();

        @Test
        public void throwsNullPointerException() {
            thrown.expectMessage("expectedMessage");
            throw new IllegalArgumentException("actualMessage");
        }
    }

    public static class ThrowNoExceptionButExpectExceptionWithType {
        @Rule
        public ExpectedException thrown = ExpectedException.none();

        @Test
        public void doesntThrowNullPointerException() {
            thrown.expect(NullPointerException.class);
        }
    }

    public static class WronglyExpectsExceptionMessage {
        @Rule
        public ExpectedException thrown = ExpectedException.none();

        @Test
        public void doesntThrowAnything() {
            thrown.expectMessage("anything!");
        }
    }

    public static class ExpectsSubstring {
        @Rule
        public ExpectedException thrown = ExpectedException.none();

        @Test
        public void throwsMore() {
            thrown.expectMessage("anything!");
            throw new NullPointerException("This could throw anything! (as long as it has the right substring)");
        }
    }

    public static class ExpectsSubstringNullMessage {
        @Rule
        public ExpectedException thrown = ExpectedException.none();

        @Test
        public void throwsMore() {
            thrown.expectMessage("anything!");
            throw new NullPointerException();
        }
    }

    public static class ExpectsMessageMatcher {
        @Rule
        public ExpectedException thrown = ExpectedException.none();

        @Test
        public void throwsMore() {
            thrown.expectMessage(CoreMatchers.startsWith(ExpectedExceptionTest.ARBITRARY_MESSAGE));
            throw new NullPointerException(((ExpectedExceptionTest.ARBITRARY_MESSAGE) + "!"));
        }
    }

    public static class ExpectedMessageMatcherFails {
        @Rule
        public ExpectedException thrown = ExpectedException.none();

        @Test
        public void throwsMore() {
            thrown.expectMessage(IsEqual.equalTo("Wrong start"));
            throw new NullPointerException("Back!");
        }
    }

    public static class ExpectsMatcher {
        @Rule
        public ExpectedException thrown = ExpectedException.none();

        @Test
        public void throwsMore() {
            thrown.expect(CoreMatchers.any(Exception.class));
            throw new NullPointerException("Ack!");
        }
    }

    public static class ExpectsMultipleMatchers {
        @Rule
        public ExpectedException thrown = ExpectedException.none();

        @Test
        public void throwsMore() {
            thrown.expect(IllegalArgumentException.class);
            thrown.expectMessage("Ack!");
            throw new NullPointerException("Ack!");
        }
    }

    // https://github.com/junit-team/junit4/pull/583
    public static class ExpectAssertionErrorWhichIsNotThrown {
        @Rule
        public ExpectedException thrown = ExpectedException.none();

        @Test
        public void fails() {
            thrown.expect(AssertionError.class);
        }
    }

    public static class FailBeforeExpectingException {
        @Rule
        public ExpectedException thrown = ExpectedException.none();

        @Test
        public void fails() {
            Assert.fail(ExpectedExceptionTest.ARBITRARY_MESSAGE);
            thrown.expect(IllegalArgumentException.class);
        }
    }

    public static class FailedAssumptionAndExpectException {
        @Rule
        public ExpectedException thrown = ExpectedException.none();

        @Test
        public void failedAssumption() {
            Assume.assumeTrue(false);
            thrown.expect(NullPointerException.class);
        }
    }

    public static class ThrowExceptionWithMatchingCause {
        @Rule
        public ExpectedException thrown = ExpectedException.none();

        @Test
        public void throwExceptionWithMatchingCause() {
            NullPointerException expectedCause = new NullPointerException("expected cause");
            thrown.expect(IllegalArgumentException.class);
            thrown.expectMessage("Ack!");
            thrown.expectCause(CoreMatchers.is(expectedCause));
            throw new IllegalArgumentException("Ack!", expectedCause);
        }
    }

    public static class ThrowExpectedNullCause {
        @Rule
        public ExpectedException thrown = ExpectedException.none();

        @Test
        public void throwExpectedNullCause() {
            thrown.expect(IllegalArgumentException.class);
            thrown.expectMessage("Ack!");
            thrown.expectCause(CoreMatchers.nullValue(Throwable.class));
            throw new IllegalArgumentException("Ack!");
        }
    }

    public static class ThrowUnexpectedCause {
        @Rule
        public ExpectedException thrown = ExpectedException.none();

        @Test
        public void throwWithCause() {
            thrown.expect(IllegalArgumentException.class);
            thrown.expectMessage("Ack!");
            thrown.expectCause(CoreMatchers.is(new NullPointerException("expected cause")));
            throw new IllegalArgumentException("Ack!", new NullPointerException("an unexpected cause"));
        }
    }

    public static class UseNoCustomMessage {
        @Rule
        public ExpectedException thrown = ExpectedException.none();

        @Test
        public void noThrow() {
            thrown.expect(IllegalArgumentException.class);
        }
    }

    public static class UseCustomMessageWithPlaceHolder {
        @Rule
        public ExpectedException thrown = ExpectedException.none();

        @Test
        public void noThrow() {
            thrown.expect(IllegalArgumentException.class);
            thrown.reportMissingExceptionWithMessage(((ExpectedExceptionTest.ARBITRARY_MESSAGE) + " - %s"));
        }
    }

    public static class UseCustomMessageWithoutPlaceHolder {
        @Rule
        public ExpectedException thrown = ExpectedException.none();

        @Test
        public void noThrow() {
            thrown.expect(IllegalArgumentException.class);
            thrown.reportMissingExceptionWithMessage(ExpectedExceptionTest.ARBITRARY_MESSAGE);
        }
    }

    public static class ErrorCollectorShouldFailAlthoughExpectedExceptionDoesNot {
        @Rule
        public ErrorCollector collector = new ErrorCollector();

        @Rule(order = Integer.MAX_VALUE)
        public ExpectedException thrown = ExpectedException.none();

        @Test
        public void test() {
            collector.addError(new AssertionError(ExpectedExceptionTest.ARBITRARY_MESSAGE));
            thrown.expect(Exception.class);
            throw new RuntimeException();
        }
    }
}

