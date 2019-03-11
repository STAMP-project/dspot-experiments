package org.junit.internal;


import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;
import junit.framework.TestCase;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.rules.TestRule;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;


public class StackTracesTest {
    private static final String EOL = System.getProperty("line.separator", "\n");

    private static ExecutorService executorService;

    @Test
    public void getTrimmedStackForJUnit4TestFailingInTestMethod() {
        Result result = StackTracesTest.runTest(StackTracesTest.TestWithOneThrowingTestMethod.class);
        Assert.assertEquals("Should run the test", 1, result.getRunCount());
        Assert.assertEquals("One test should fail", 1, result.getFailureCount());
        Failure failure = result.getFailures().get(0);
        StackTracesTest.assertHasTrimmedTrace(failure, StackTracesTest.message("java.lang.RuntimeException: cause"), StackTracesTest.at("org.junit.internal.StackTracesTest$FakeClassUnderTest.doThrowExceptionWithoutCause"), StackTracesTest.at("org.junit.internal.StackTracesTest$FakeClassUnderTest.throwsExceptionWithoutCause"), StackTracesTest.at("org.junit.internal.StackTracesTest$TestWithOneThrowingTestMethod.alwaysThrows"));
        Assert.assertNotEquals(failure.getTrace(), getTrimmedTrace());
    }

    @Test
    public void getTrimmedStackForJUnit4TestFailingInTestMethodWithCause() {
        Result result = StackTracesTest.runTest(StackTracesTest.TestWithOneThrowingTestMethodWithCause.class);
        Assert.assertEquals("Should run the test", 1, result.getRunCount());
        Assert.assertEquals("One test should fail", 1, result.getFailureCount());
        Failure failure = result.getFailures().get(0);
        StackTracesTest.assertHasTrimmedTrace(failure, StackTracesTest.message("java.lang.RuntimeException: outer"), StackTracesTest.at("org.junit.internal.StackTracesTest$FakeClassUnderTest.doThrowExceptionWithCause"), StackTracesTest.at("org.junit.internal.StackTracesTest$FakeClassUnderTest.throwsExceptionWithCause"), StackTracesTest.at("org.junit.internal.StackTracesTest$TestWithOneThrowingTestMethodWithCause.alwaysThrows"), StackTracesTest.framesTrimmed(), StackTracesTest.message("Caused by: java.lang.RuntimeException: cause"), StackTracesTest.at("org.junit.internal.StackTracesTest$FakeClassUnderTest.doThrowExceptionWithoutCause"), StackTracesTest.at("org.junit.internal.StackTracesTest$FakeClassUnderTest.throwsExceptionWithoutCause"), StackTracesTest.at("org.junit.internal.StackTracesTest$FakeClassUnderTest.doThrowExceptionWithCause"), StackTracesTest.framesInCommon());
        Assert.assertNotEquals(failure.getTrace(), getTrimmedTrace());
    }

    @Test
    public void getTrimmedStackForJUnit4TestFailingInBeforeMethod() {
        Result result = StackTracesTest.runTest(StackTracesTest.TestWithThrowingBeforeMethod.class);
        Assert.assertEquals("Should run the test", 1, result.getRunCount());
        Assert.assertEquals("One test should fail", 1, result.getFailureCount());
        Failure failure = result.getFailures().get(0);
        StackTracesTest.assertHasTrimmedTrace(failure, StackTracesTest.message("java.lang.RuntimeException: cause"), StackTracesTest.at("org.junit.internal.StackTracesTest$FakeClassUnderTest.doThrowExceptionWithoutCause"), StackTracesTest.at("org.junit.internal.StackTracesTest$FakeClassUnderTest.throwsExceptionWithoutCause"), StackTracesTest.at("org.junit.internal.StackTracesTest$TestWithThrowingBeforeMethod.alwaysThrows"));
        Assert.assertNotEquals(failure.getTrace(), getTrimmedTrace());
    }

    @Test
    public void getTrimmedStackForJUnit3TestFailingInTestMethod() {
        Result result = StackTracesTest.runTest(StackTracesTest.JUnit3TestWithOneThrowingTestMethod.class);
        Assert.assertEquals("Should run the test", 1, result.getRunCount());
        Assert.assertEquals("One test should fail", 1, result.getFailureCount());
        Failure failure = result.getFailures().get(0);
        StackTracesTest.assertHasTrimmedTrace(failure, StackTracesTest.message("java.lang.RuntimeException: cause"), StackTracesTest.at("org.junit.internal.StackTracesTest$FakeClassUnderTest.doThrowExceptionWithoutCause"), StackTracesTest.at("org.junit.internal.StackTracesTest$FakeClassUnderTest.throwsExceptionWithoutCause"), StackTracesTest.at("org.junit.internal.StackTracesTest$JUnit3TestWithOneThrowingTestMethod.testAlwaysThrows"));
        Assert.assertNotEquals(failure.getTrace(), getTrimmedTrace());
    }

    @Test
    public void getTrimmedStackForJUnit3TestFailingInSetupMethod() {
        Result result = StackTracesTest.runTest(StackTracesTest.JUnit3TestWithThrowingSetUpMethod.class);
        Assert.assertEquals("Should run the test", 1, result.getRunCount());
        Assert.assertEquals("One test should fail", 1, result.getFailureCount());
        Failure failure = result.getFailures().get(0);
        StackTracesTest.assertHasTrimmedTrace(failure, StackTracesTest.message("java.lang.RuntimeException: cause"), StackTracesTest.at("org.junit.internal.StackTracesTest$FakeClassUnderTest.doThrowExceptionWithoutCause"), StackTracesTest.at("org.junit.internal.StackTracesTest$FakeClassUnderTest.throwsExceptionWithoutCause"), StackTracesTest.at("org.junit.internal.StackTracesTest$JUnit3TestWithThrowingSetUpMethod.setUp"));
        Assert.assertNotEquals(failure.getTrace(), getTrimmedTrace());
    }

    @Test
    public void getTrimmedStackForJUnit4TestFailingInTestRule() {
        Result result = StackTracesTest.runTest(StackTracesTest.TestWithThrowingTestRule.class);
        Assert.assertEquals("Should run the test", 1, result.getRunCount());
        Assert.assertEquals("One test should fail", 1, result.getFailureCount());
        Failure failure = result.getFailures().get(0);
        StackTracesTest.assertHasTrimmedTrace(failure, StackTracesTest.message("java.lang.RuntimeException: cause"), StackTracesTest.at("org.junit.internal.StackTracesTest$FakeClassUnderTest.doThrowExceptionWithoutCause"), StackTracesTest.at("org.junit.internal.StackTracesTest$FakeClassUnderTest.throwsExceptionWithoutCause"), StackTracesTest.at("org.junit.internal.StackTracesTest$ThrowingTestRule.apply"));
        Assert.assertNotEquals(failure.getTrace(), getTrimmedTrace());
    }

    @Test
    public void getTrimmedStackForJUnit4TestFailingInClassRule() {
        Result result = StackTracesTest.runTest(StackTracesTest.TestWithThrowingClassRule.class);
        Assert.assertEquals("No tests were executed", 0, result.getRunCount());
        Assert.assertEquals("One failure", 1, result.getFailureCount());
        Failure failure = result.getFailures().get(0);
        StackTracesTest.assertHasTrimmedTrace(failure, StackTracesTest.message("java.lang.RuntimeException: cause"), StackTracesTest.at("org.junit.internal.StackTracesTest$FakeClassUnderTest.doThrowExceptionWithoutCause"), StackTracesTest.at("org.junit.internal.StackTracesTest$FakeClassUnderTest.throwsExceptionWithoutCause"), StackTracesTest.at("org.junit.internal.StackTracesTest$ThrowingTestRule.apply"));
        Assert.assertNotEquals(failure.getTrace(), getTrimmedTrace());
    }

    @Test
    public void getTrimmedStackForJUnit4TestFailingInMethodRule() {
        Result result = StackTracesTest.runTest(StackTracesTest.TestWithThrowingMethodRule.class);
        Assert.assertEquals("Should run the test", 1, result.getRunCount());
        Assert.assertEquals("One test should fail", 1, result.getFailureCount());
        Failure failure = result.getFailures().get(0);
        StackTracesTest.assertHasTrimmedTrace(failure, StackTracesTest.message("java.lang.RuntimeException: cause"), StackTracesTest.at("org.junit.internal.StackTracesTest$FakeClassUnderTest.doThrowExceptionWithoutCause"), StackTracesTest.at("org.junit.internal.StackTracesTest$FakeClassUnderTest.throwsExceptionWithoutCause"), StackTracesTest.at("org.junit.internal.StackTracesTest$ThrowingMethodRule.apply"));
        Assert.assertNotEquals(failure.getTrace(), getTrimmedTrace());
    }

    @Test
    public void getTrimmedStackWithSuppressedExceptions() {
        Assume.assumeTrue("Running on 1.7+", ((StackTracesTest.TestWithSuppressedException.addSuppressed) != null));
        Result result = StackTracesTest.runTest(StackTracesTest.TestWithSuppressedException.class);
        Assert.assertEquals("Should run the test", 1, result.getRunCount());
        Assert.assertEquals("One test should fail", 1, result.getFailureCount());
        Failure failure = result.getFailures().get(0);
        StackTracesTest.assertHasTrimmedTrace(failure, StackTracesTest.message("java.lang.RuntimeException: error"), StackTracesTest.at("org.junit.internal.StackTracesTest$TestWithSuppressedException.alwaysThrows"), StackTracesTest.message("\tSuppressed: java.lang.RuntimeException: suppressed"), StackTracesTest.at("org.junit.internal.StackTracesTest$TestWithSuppressedException.alwaysThrows"), StackTracesTest.framesInCommon());
        Assert.assertNotEquals(failure.getTrace(), getTrimmedTrace());
    }

    private abstract static class StringMatcher extends TypeSafeMatcher<String> {}

    /**
     * A matcher that matches the exception message in a stack trace.
     */
    private static class ExceptionMessageMatcher extends StackTracesTest.StringMatcher {
        private final Matcher<String> matcher;

        public ExceptionMessageMatcher(String message) {
            matcher = CoreMatchers.equalTo(message);
        }

        public void describeTo(Description description) {
            matcher.describeTo(description);
        }

        @Override
        protected boolean matchesSafely(String line) {
            return matcher.matches(line);
        }
    }

    /**
     * A matcher that matches the "at ..." line in a stack trace.
     */
    private static class StackTraceLineMatcher extends StackTracesTest.StringMatcher {
        private static final Pattern PATTERN = Pattern.compile("\t*at ([a-zA-Z0-9.$]+)\\([a-zA-Z0-9]+\\.java:[0-9]+\\)");

        private final String method;

        public StackTraceLineMatcher(String method) {
            this.method = method;
        }

        public void describeTo(Description description) {
            description.appendText(("A stack trace line for method " + (method)));
        }

        @Override
        protected boolean matchesSafely(String line) {
            if (!(line.startsWith("\t"))) {
                return false;
            }
            line = line.substring(1);
            java.util.regex.Matcher matcher = StackTracesTest.StackTraceLineMatcher.PATTERN.matcher(line);
            if (!(matcher.matches())) {
                Assert.fail(("Line does not look like a stack trace line: " + line));
            }
            String matchedMethod = matcher.group(1);
            return method.equals(matchedMethod);
        }
    }

    /**
     * A matcher that matches the line printed when frames were removed from a stack trace.
     */
    private static class FramesRemovedMatcher extends StackTracesTest.StringMatcher {
        private static final Pattern PATTERN = Pattern.compile("\t*\\.\\.\\. [0-9]+ ([a-z]+)");

        private final String suffix;

        public FramesRemovedMatcher(String suffix) {
            this.suffix = suffix;
        }

        public void describeTo(Description description) {
            description.appendText((("A line matching \"..x " + (suffix)) + "\""));
        }

        @Override
        protected boolean matchesSafely(String line) {
            if (!(line.startsWith("\t"))) {
                return false;
            }
            line = line.substring(1);
            java.util.regex.Matcher matcher = StackTracesTest.FramesRemovedMatcher.PATTERN.matcher(line);
            if (!(matcher.matches())) {
                Assert.fail(("Line does not look like a stack trace line: " + line));
            }
            return suffix.equals(matcher.group(1));
        }
    }

    public static class TestWithOneThrowingTestMethod {
        @Test
        public void alwaysThrows() {
            new StackTracesTest.FakeClassUnderTest().throwsExceptionWithoutCause();
        }
    }

    public static class JUnit3TestWithOneThrowingTestMethod extends TestCase {
        public void testAlwaysThrows() {
            new StackTracesTest.FakeClassUnderTest().throwsExceptionWithoutCause();
        }
    }

    public static class TestWithOneThrowingTestMethodWithCause {
        @Test
        public void alwaysThrows() {
            new StackTracesTest.FakeClassUnderTest().throwsExceptionWithCause();
        }
    }

    public static class TestWithThrowingBeforeMethod {
        @Before
        public void alwaysThrows() {
            new StackTracesTest.FakeClassUnderTest().throwsExceptionWithoutCause();
        }

        @Test
        public void alwaysPasses() {
        }
    }

    public static class JUnit3TestWithThrowingSetUpMethod extends TestCase {
        @Override
        protected void setUp() throws Exception {
            super.setUp();
            new StackTracesTest.FakeClassUnderTest().throwsExceptionWithoutCause();
        }

        public void testAlwaysPasses() {
        }
    }

    public static class ThrowingTestRule implements TestRule {
        public Statement apply(Statement base, org.junit.runner.Description description) {
            new StackTracesTest.FakeClassUnderTest().throwsExceptionWithoutCause();
            return base;
        }
    }

    public static class TestWithThrowingTestRule {
        @Rule
        public final TestRule rule = new StackTracesTest.ThrowingTestRule();

        @Test
        public void alwaysPasses() {
        }
    }

    public static class TestWithThrowingClassRule {
        @ClassRule
        public static final TestRule rule = new StackTracesTest.ThrowingTestRule();

        @Test
        public void alwaysPasses() {
        }
    }

    public static class ThrowingMethodRule implements MethodRule {
        public Statement apply(Statement base, FrameworkMethod method, Object target) {
            new StackTracesTest.FakeClassUnderTest().throwsExceptionWithoutCause();
            return base;
        }
    }

    public static class TestWithThrowingMethodRule {
        @Rule
        public final StackTracesTest.ThrowingMethodRule rule = new StackTracesTest.ThrowingMethodRule();

        @Test
        public void alwaysPasses() {
        }
    }

    private static class FakeClassUnderTest {
        public void throwsExceptionWithCause() {
            doThrowExceptionWithCause();
        }

        public void throwsExceptionWithoutCause() {
            doThrowExceptionWithoutCause();
        }

        private void doThrowExceptionWithCause() {
            try {
                throwsExceptionWithoutCause();
            } catch (Exception e) {
                throw new RuntimeException("outer", e);
            }
        }

        private void doThrowExceptionWithoutCause() {
            throw new RuntimeException("cause");
        }
    }

    public static class TestWithSuppressedException {
        static final Method addSuppressed = StackTracesTest.TestWithSuppressedException.initAddSuppressed();

        static Method initAddSuppressed() {
            try {
                return Throwable.class.getMethod("addSuppressed", Throwable.class);
            } catch (Throwable e) {
                return null;
            }
        }

        @Test
        public void alwaysThrows() throws Exception {
            final RuntimeException exception = new RuntimeException("error");
            StackTracesTest.TestWithSuppressedException.addSuppressed.invoke(exception, new RuntimeException("suppressed"));
            throw exception;
        }
    }
}

