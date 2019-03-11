package junit.tests.runner;


import java.util.List;
import junit.framework.TestCase;
import junit.tests.framework.Success;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.tests.running.methods.AnnotationTest;


public class ResultTest extends TestCase {
    private Result fromStream;

    public void testRunFailureResultCanBeSerialised() throws Exception {
        JUnitCore runner = new JUnitCore();
        Result result = runner.run(AnnotationTest.FailureTest.class);
        assertResultSerializable(result);
    }

    public void testRunFailureResultCanBeReserialised_v4_12() throws Exception {
        JUnitCore runner = new JUnitCore();
        Result result = runner.run(AnnotationTest.FailureTest.class);
        assertResultReserializable(result, ResultTest.SerializationFormat.V4_12);
    }

    public void testRunAssumptionFailedResultCanBeSerialised() throws Exception {
        JUnitCore runner = new JUnitCore();
        Result result = runner.run(ResultTest.AssumptionFailedTest.class);
        assertResultSerializable(result);
    }

    public void testRunAssumptionFailedResultCanBeReserialised_v4_12() throws Exception {
        JUnitCore runner = new JUnitCore();
        Result result = runner.run(ResultTest.AssumptionFailedTest.class);
        assertResultReserializable(result, ResultTest.SerializationFormat.V4_12);
    }

    public void testRunAssumptionFailedResultCanBeReserialised_v4_13() throws Exception {
        JUnitCore runner = new JUnitCore();
        Result result = runner.run(ResultTest.AssumptionFailedTest.class);
        assertResultReserializable(result, ResultTest.SerializationFormat.V4_13);
    }

    public void testRunSuccessResultCanBeSerialised() throws Exception {
        JUnitCore runner = new JUnitCore();
        Result result = runner.run(Success.class);
        assertResultSerializable(result);
    }

    public void testRunSuccessResultCanBeReserialised_v4_12() throws Exception {
        JUnitCore runner = new JUnitCore();
        Result result = runner.run(Success.class);
        assertResultReserializable(result, ResultTest.SerializationFormat.V4_12);
    }

    public void testRunSuccessResultCanBeReserialised_v4_13() throws Exception {
        JUnitCore runner = new JUnitCore();
        Result result = runner.run(Success.class);
        assertResultReserializable(result, ResultTest.SerializationFormat.V4_13);
    }

    private enum SerializationFormat {

        V4_12,
        V4_13;}

    public static class AssumptionFailedTest {
        @Test
        public void assumptionFailed() throws Exception {
            Assume.assumeTrue(false);
        }
    }

    /**
     * A version of {@code Result} that returns a hard-coded runtime.
     * This makes values returned by the methods deterministic.
     */
    private static class ResultWithFixedRunTime extends Result {
        private static final long serialVersionUID = 1L;

        private final Result delegate;

        public ResultWithFixedRunTime(Result delegate) {
            this.delegate = delegate;
        }

        @Override
        public int getRunCount() {
            return delegate.getRunCount();
        }

        @Override
        public int getFailureCount() {
            return delegate.getFailureCount();
        }

        @Override
        public long getRunTime() {
            return 2;
        }

        @Override
        public List<Failure> getFailures() {
            return delegate.getFailures();
        }

        @Override
        public int getIgnoreCount() {
            return delegate.getIgnoreCount();
        }

        @Override
        public int getAssumptionFailureCount() {
            return delegate.getAssumptionFailureCount();
        }
    }
}

