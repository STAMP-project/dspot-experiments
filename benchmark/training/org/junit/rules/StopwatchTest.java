package org.junit.rules;


import java.util.concurrent.TimeUnit;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.AssumptionViolatedException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Result;


/**
 *
 *
 * @author tibor17
 * @since 4.12
 */
public class StopwatchTest {
    private static enum TestStatus {

        SUCCEEDED,
        FAILED,
        SKIPPED;}

    private static StopwatchTest.Record record;

    private static StopwatchTest.Record finishedRecord;

    private static long fakeTimeNanos = 1234;

    private static class Record {
        final long duration;

        final String name;

        final StopwatchTest.TestStatus status;

        Record() {
            this(0, null, null);
        }

        Record(long duration, Description description) {
            this(duration, null, description);
        }

        Record(long duration, StopwatchTest.TestStatus status, Description description) {
            this.duration = duration;
            this.status = status;
            this.name = (description == null) ? null : description.getMethodName();
        }
    }

    public abstract static class AbstractStopwatchTest {
        /**
         * Fake implementation of {@link Stopwatch.Clock} that increments the time
         * every time it is asked.
         */
        private final Stopwatch.Clock fakeClock = new Stopwatch.Clock() {
            @Override
            public long nanoTime() {
                return (StopwatchTest.fakeTimeNanos)++;
            }
        };

        protected final Stopwatch stopwatch = new Stopwatch(fakeClock) {
            @Override
            protected void succeeded(long nanos, Description description) {
                StopwatchTest.record = new StopwatchTest.Record(nanos, StopwatchTest.TestStatus.SUCCEEDED, description);
                StopwatchTest.simulateTimePassing(1);
            }

            @Override
            protected void failed(long nanos, Throwable e, Description description) {
                StopwatchTest.record = new StopwatchTest.Record(nanos, StopwatchTest.TestStatus.FAILED, description);
                StopwatchTest.simulateTimePassing(1);
            }

            @Override
            protected void skipped(long nanos, AssumptionViolatedException e, Description description) {
                StopwatchTest.record = new StopwatchTest.Record(nanos, StopwatchTest.TestStatus.SKIPPED, description);
                StopwatchTest.simulateTimePassing(1);
            }

            @Override
            protected void finished(long nanos, Description description) {
                StopwatchTest.finishedRecord = new StopwatchTest.Record(nanos, description);
            }
        };

        private final TestWatcher watcher = new TestWatcher() {
            @Override
            protected void finished(Description description) {
                afterStopwatchRule();
            }
        };

        @Rule
        public final RuleChain chain = RuleChain.outerRule(watcher).around(stopwatch);

        protected void afterStopwatchRule() {
        }
    }

    public static class SuccessfulTest extends StopwatchTest.AbstractStopwatchTest {
        @Test
        public void successfulTest() {
        }
    }

    public static class FailedTest extends StopwatchTest.AbstractStopwatchTest {
        @Test
        public void failedTest() {
            Assert.fail();
        }
    }

    public static class SkippedTest extends StopwatchTest.AbstractStopwatchTest {
        @Test
        public void skippedTest() {
            Assume.assumeTrue(false);
        }
    }

    public static class DurationDuringTestTest extends StopwatchTest.AbstractStopwatchTest {
        @Test
        public void duration() {
            StopwatchTest.simulateTimePassing(300L);
            Assert.assertEquals(300L, stopwatch.runtime(TimeUnit.MILLISECONDS));
            StopwatchTest.simulateTimePassing(500L);
            Assert.assertEquals(800L, stopwatch.runtime(TimeUnit.MILLISECONDS));
        }
    }

    public static class DurationAfterTestTest extends StopwatchTest.AbstractStopwatchTest {
        @Test
        public void duration() {
            StopwatchTest.simulateTimePassing(300L);
            Assert.assertEquals(300L, stopwatch.runtime(TimeUnit.MILLISECONDS));
        }

        @Override
        protected void afterStopwatchRule() {
            Assert.assertEquals(300L, stopwatch.runtime(TimeUnit.MILLISECONDS));
            StopwatchTest.simulateTimePassing(500L);
            Assert.assertEquals(300L, stopwatch.runtime(TimeUnit.MILLISECONDS));
        }
    }

    @Test
    public void succeeded() {
        Result result = StopwatchTest.runTest(StopwatchTest.SuccessfulTest.class);
        Assert.assertEquals(0, result.getFailureCount());
        Assert.assertThat(StopwatchTest.record.name, Is.is("successfulTest"));
        Assert.assertThat(StopwatchTest.record.name, Is.is(StopwatchTest.finishedRecord.name));
        Assert.assertThat(StopwatchTest.record.status, Is.is(StopwatchTest.TestStatus.SUCCEEDED));
        Assert.assertTrue("timeSpent > 0", ((StopwatchTest.record.duration) > 0));
        Assert.assertThat(StopwatchTest.record.duration, Is.is(StopwatchTest.finishedRecord.duration));
    }

    @Test
    public void failed() {
        Result result = StopwatchTest.runTest(StopwatchTest.FailedTest.class);
        Assert.assertEquals(1, result.getFailureCount());
        Assert.assertThat(StopwatchTest.record.name, Is.is("failedTest"));
        Assert.assertThat(StopwatchTest.record.name, Is.is(StopwatchTest.finishedRecord.name));
        Assert.assertThat(StopwatchTest.record.status, Is.is(StopwatchTest.TestStatus.FAILED));
        Assert.assertTrue("timeSpent > 0", ((StopwatchTest.record.duration) > 0));
        Assert.assertThat(StopwatchTest.record.duration, Is.is(StopwatchTest.finishedRecord.duration));
    }

    @Test
    public void skipped() {
        Result result = StopwatchTest.runTest(StopwatchTest.SkippedTest.class);
        Assert.assertEquals(0, result.getFailureCount());
        Assert.assertThat(StopwatchTest.record.name, Is.is("skippedTest"));
        Assert.assertThat(StopwatchTest.record.name, Is.is(StopwatchTest.finishedRecord.name));
        Assert.assertThat(StopwatchTest.record.status, Is.is(StopwatchTest.TestStatus.SKIPPED));
        Assert.assertTrue("timeSpent > 0", ((StopwatchTest.record.duration) > 0));
        Assert.assertThat(StopwatchTest.record.duration, Is.is(StopwatchTest.finishedRecord.duration));
    }

    @Test
    public void runtimeDuringTestShouldReturnTimeSinceStart() {
        Result result = StopwatchTest.runTest(StopwatchTest.DurationDuringTestTest.class);
        Assert.assertTrue(result.wasSuccessful());
    }

    @Test
    public void runtimeAfterTestShouldReturnRunDuration() {
        Result result = StopwatchTest.runTest(StopwatchTest.DurationAfterTestTest.class);
        Assert.assertTrue(result.wasSuccessful());
    }
}

