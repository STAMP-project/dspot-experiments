package com.birbit.android.jobqueue.test.jobmanager;


import TagConstraint.ALL;
import android.support.annotation.NonNull;
import android.util.Log;
import com.birbit.android.jobqueue.BuildConfig;
import com.birbit.android.jobqueue.CancelResult;
import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.birbit.android.jobqueue.test.jobs.DummyJob;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class MultiThreadTest extends JobManagerTestBase {
    private static AtomicInteger multiThreadedJobCounter;

    @Test
    public void testMultiThreaded() throws Exception {
        MultiThreadTest.multiThreadedJobCounter = new AtomicInteger(0);
        final JobManager jobManager = createJobManager(loadFactor(3).maxConsumerCount(10));
        int limit = 200;
        ExecutorService executor = new ThreadPoolExecutor(20, 20, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(limit));
        final String cancelTag = "iWillBeCancelled";
        Collection<Future<?>> futures = new LinkedList<Future<?>>();
        for (int i = 0; i < limit; i++) {
            final int id = i;
            futures.add(executor.submit(new Runnable() {
                @Override
                public void run() {
                    final boolean persistent = ((Math.round(Math.random())) % 2) == 0;
                    boolean requiresNetwork = ((Math.round(Math.random())) % 2) == 0;
                    int priority = ((int) ((Math.round(Math.random())) % 10));
                    MultiThreadTest.multiThreadedJobCounter.incrementAndGet();
                    Params params = new Params(priority).setRequiresNetwork(requiresNetwork).setPersistent(persistent);
                    if ((Math.random()) < 0.1) {
                        params.addTags(cancelTag);
                    }
                    jobManager.addJob(new MultiThreadTest.DummyJobForMultiThread(id, params));
                }
            }));
        }
        // wait for some jobs to start
        // noinspection SLEEP_IN_CODE
        Thread.sleep(1000);
        CancelResult cancelResult = jobManager.cancelJobs(ALL, cancelTag);
        for (int i = 0; i < (cancelResult.getCancelledJobs().size()); i++) {
            MultiThreadTest.multiThreadedJobCounter.decrementAndGet();
        }
        for (Future<?> future : futures) {
            future.get();
        }
        Log.d("TAG", "added all jobs");
        // wait until all jobs are added
        // noinspection DIRECT_TIME_ACCESS
        long start = System.nanoTime();
        long timeLimit = ((JobManager.NS_PER_MS) * 60000) * 20;// 20 minutes

        // noinspection DIRECT_TIME_ACCESS
        while ((((System.nanoTime()) - start) < timeLimit) && ((MultiThreadTest.multiThreadedJobCounter.get()) != 0)) {
            // noinspection SLEEP_IN_CODE
            Thread.sleep(1000);
        } 
        MatcherAssert.assertThat("jobmanager count should be 0", jobManager.count(), CoreMatchers.equalTo(0));
        jobManager.stopAndWaitUntilConsumersAreFinished();
        MatcherAssert.assertThat("multiThreadedJobCounter should be 0", MultiThreadTest.multiThreadedJobCounter.get(), CoreMatchers.is(0));
    }

    public static class DummyJobForMultiThread extends DummyJob {
        private int id;

        private DummyJobForMultiThread(int id, Params params) {
            super(params);
            this.id = id;
        }

        @Override
        public void onRun() throws Throwable {
            super.onRun();
            // take some time
            // noinspection SLEEP_IN_CODE
            Thread.sleep(((long) ((Math.random()) * 1000)));
            // throw exception w/ small chance
            if ((Math.random()) < 0.1) {
                throw new Exception("decided to die, will retry");
            }
            int remaining = MultiThreadTest.multiThreadedJobCounter.decrementAndGet();
        }

        @Override
        protected int getRetryLimit() {
            return 1000;
        }

        @Override
        protected RetryConstraint shouldReRunOnThrowable(@NonNull
        Throwable throwable, int runCount, int maxRunCount) {
            return RetryConstraint.RETRY;
        }
    }
}

