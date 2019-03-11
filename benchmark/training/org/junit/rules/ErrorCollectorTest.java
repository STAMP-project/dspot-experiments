package org.junit.rules;


import java.util.concurrent.Callable;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;
import org.junit.internal.AssumptionViolatedException;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class ErrorCollectorTest {
    @Parameterized.Parameter(0)
    public Class<?> classUnderTest;

    @Parameterized.Parameter(1)
    public Matcher<EventCollector> matcher;

    @Test
    public void runTestClassAndVerifyEvents() {
        EventCollector collector = new EventCollector();
        JUnitCore core = new JUnitCore();
        core.addListener(collector);
        core.run(classUnderTest);
        MatcherAssert.assertThat(collector, matcher);
    }

    public static class AddSingleError {
        @Rule
        public ErrorCollector collector = new ErrorCollector();

        @Test
        public void example() {
            collector.addError(new Throwable("message"));
        }
    }

    public static class AddTwoErrors {
        @Rule
        public ErrorCollector collector = new ErrorCollector();

        @Test
        public void example() {
            collector.addError(new Throwable("first thing went wrong"));
            collector.addError(new Throwable("second thing went wrong"));
        }
    }

    public static class AddInternalAssumptionViolatedException {
        @Rule
        public ErrorCollector collector = new ErrorCollector();

        @Test
        public void example() {
            collector.addError(new AssumptionViolatedException("message"));
        }
    }

    public static class CheckMatcherThatDoesNotFailWithProvidedReason {
        @Rule
        public ErrorCollector collector = new ErrorCollector();

        @Test
        public void example() {
            collector.checkThat("dummy reason", 3, CoreMatchers.is(3));
        }
    }

    public static class CheckMatcherThatDoesNotFailWithoutProvidedReason {
        @Rule
        public ErrorCollector collector = new ErrorCollector();

        @Test
        public void example() {
            collector.checkThat(3, CoreMatchers.is(3));
        }
    }

    public static class CheckMatcherThatFailsWithoutProvidedReason {
        @Rule
        public ErrorCollector collector = new ErrorCollector();

        @Test
        public void example() {
            collector.checkThat(3, CoreMatchers.is(4));
        }
    }

    public static class CheckMatcherThatFailsWithProvidedReason {
        @Rule
        public ErrorCollector collector = new ErrorCollector();

        @Test
        public void example() {
            collector.checkThat("reason", 3, CoreMatchers.is(4));
        }
    }

    public static class CheckTwoMatchersThatFail {
        @Rule
        public ErrorCollector collector = new ErrorCollector();

        @Test
        public void example() {
            collector.checkThat(3, CoreMatchers.is(4));
            collector.checkThat("reason", 7, CoreMatchers.is(8));
        }
    }

    public static class CheckCallableThatThrowsAnException {
        @Rule
        public ErrorCollector collector = new ErrorCollector();

        @Test
        public void example() {
            collector.checkSucceeds(new Callable<Object>() {
                public Object call() throws Exception {
                    throw new RuntimeException("first!");
                }
            });
        }
    }

    public static class CheckTwoCallablesThatThrowExceptions {
        @Rule
        public ErrorCollector collector = new ErrorCollector();

        @Test
        public void example() {
            collector.checkSucceeds(new Callable<Object>() {
                public Object call() throws Exception {
                    throw new RuntimeException("first!");
                }
            });
            collector.checkSucceeds(new Callable<Integer>() {
                public Integer call() throws Exception {
                    throw new RuntimeException("second!");
                }
            });
        }
    }

    public static class CheckCallableThatThrowsInternalAssumptionViolatedException {
        @Rule
        public ErrorCollector collector = new ErrorCollector();

        @Test
        public void example() {
            collector.checkSucceeds(new Callable<Object>() {
                public Object call() throws Exception {
                    throw new AssumptionViolatedException("message");
                }
            });
        }
    }

    public static class CheckCallableWithFailingAssumption {
        @Rule
        public ErrorCollector collector = new ErrorCollector();

        @Test
        public void example() {
            collector.checkSucceeds(new Callable<Object>() {
                public Object call() throws Exception {
                    Assume.assumeTrue(false);
                    return null;
                }
            });
        }
    }

    public static class CheckCallableThatDoesNotThrowAnException {
        @Rule
        public ErrorCollector collector = new ErrorCollector();

        @Test
        public void example() {
            Object result = collector.checkSucceeds(new Callable<Object>() {
                public Object call() throws Exception {
                    return 3;
                }
            });
            Assert.assertEquals(3, result);
        }
    }

    public static class CheckRunnableThatThrowsExpectedTypeOfException {
        @Rule
        public ErrorCollector collector = new ErrorCollector();

        @Test
        public void example() {
            collector.checkThrows(IllegalArgumentException.class, new ThrowingRunnable() {
                public void run() throws Throwable {
                    throw new IllegalArgumentException();
                }
            });
        }
    }

    public static class CheckRunnableThatThrowsUnexpectedTypeOfException {
        @Rule
        public ErrorCollector collector = new ErrorCollector();

        @Test
        public void example() {
            collector.checkThrows(IllegalArgumentException.class, new ThrowingRunnable() {
                public void run() throws Throwable {
                    throw new NullPointerException();
                }
            });
        }
    }

    public static class CheckRunnableThatThrowsNoExceptionAlthoughOneIsExpected {
        @Rule
        public ErrorCollector collector = new ErrorCollector();

        @Test
        public void example() {
            checkThrows(IllegalArgumentException.class, new ThrowingRunnable() {
                public void run() throws Throwable {
                }
            });
        }
    }

    public static class ErrorCollectorNotCalledBySuccessfulTest {
        @Rule
        public ErrorCollector collector = new ErrorCollector();

        @Test
        public void example() {
        }
    }

    public static class ErrorCollectorNotCalledByFailingTest {
        @Rule
        public ErrorCollector collector = new ErrorCollector();

        @Test
        public void example() {
            Assert.fail();
        }
    }
}

