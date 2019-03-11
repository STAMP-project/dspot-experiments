package org.keycloak.testsuite.performance;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import org.jboss.logging.Logger;
import org.junit.Test;
import org.keycloak.testsuite.adapter.AbstractExampleAdapterTest;


/**
 * PerformanceTest.
 *
 * @author tkyjovsk
 */
public abstract class PerformanceTest extends AbstractExampleAdapterTest {
    public static final Logger LOG = Logger.getLogger(PerformanceTest.class);

    public static final Integer WARMUP_LOAD = Integer.parseInt(System.getProperty("warmup.load", "5"));

    public static final Integer WARMUP_DURATION = Integer.parseInt(System.getProperty("warmup.duration", "30"));

    public static final Integer INITIAL_LOAD = Integer.parseInt(System.getProperty("initial.load", "10"));// load for the first iteration


    public static final Integer LOAD_INCREASE = Integer.parseInt(System.getProperty("load.increase", "10"));// how many threads to add before each iteration


    public static final Integer LOAD_INCREASE_RATE = Integer.parseInt(System.getProperty("load.increase.rate", "2"));// how fast to add the new threads per second


    public static final Integer MEASUREMENT_DURATION = Integer.parseInt(System.getProperty("measurement.duration", "20"));// duration of one measurement iteration


    public static final Integer MAX_ITERATIONS = Integer.parseInt(System.getProperty("max.iterations", "10"));

    public static final Integer MAX_THREADS = Integer.parseInt(System.getProperty("max.threads", "1000"));

    public static final Integer SLEEP_BETWEEN_LOOPS = Integer.parseInt(System.getProperty("sleep.between.loops", "0"));

    public static final Integer THREADPOOL_TERMINATION_TIMEOUT = Integer.parseInt(System.getProperty("threadpool.termination.timeout", "10"));

    public static final Integer ADDITIONAL_SLEEP_AFTER = Integer.parseInt(System.getProperty("additional.sleep.after", "0"));

    public static final String SCENARIO_TIME = "SCENARIO";

    private int currentLoad;

    private ExecutorService executorService;

    protected PerformanceStatistics statistics = new PerformanceStatistics();

    protected PerformanceStatistics timeoutStatistics = new PerformanceStatistics();// for keeping track of # of conn. timeout exceptions


    protected List<PerformanceMeasurement> measurements = new ArrayList<>();

    @Test
    public void test() {
        increaseLoadBy(PerformanceTest.WARMUP_LOAD);// increase to warmup load

        warmup();
        for (int i = 0; i < (PerformanceTest.MAX_ITERATIONS); i++) {
            int loadIncrease = (i == 0) ? (PerformanceTest.INITIAL_LOAD) - (PerformanceTest.WARMUP_LOAD)// increase from warmup to initial load
             : PerformanceTest.LOAD_INCREASE;// increase load between measurements

            increaseLoadBy(loadIncrease);
            measurePerformance();
            if (!(isThereEnoughThreadsForNextIteration(PerformanceTest.LOAD_INCREASE))) {
                PerformanceTest.LOG.warn("Threadpool capacity reached. Stopping the test.");
                break;
            }
            if (!(isLatestMeasurementWithinLimits())) {
                PerformanceTest.LOG.warn("The latest measurement exceeded expected limit. Stopping the test.");
                break;
            }
        }
    }

    private Throwable error = null;

    public abstract class Runnable extends LoopingRunnable {
        protected final Timer timer;// for timing individual operations/requests


        private final Timer scenarioTimer;// for timing the whole scenario


        public Runnable() {
            super(((PerformanceTest.SLEEP_BETWEEN_LOOPS) * 1000));
            this.timer = new Timer();
            this.scenarioTimer = new Timer();
        }

        @Override
        public void loop() {
            try {
                scenarioTimer.reset();
                performanceScenario();
                statistics.addValue(PerformanceTest.SCENARIO_TIME, scenarioTimer.getElapsedTime());
            } catch (OperationTimeoutException ex) {
                timeoutStatistics.addValue(ex.getStatistic(), ex.getValue());
                PerformanceTest.LOG.debug(String.format("Operation %s timed out. Cause: %s.", ex.getStatistic(), ex.getCause()));
            } catch (AssertionError | Exception ex) {
                setError(ex);
                throw new RuntimeException(ex);
            }
        }

        public abstract void performanceScenario() throws Exception;
    }
}

