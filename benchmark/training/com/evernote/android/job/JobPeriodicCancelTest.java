package com.evernote.android.job;


import Build.VERSION_CODES;
import android.support.annotation.NonNull;
import com.evernote.android.job.test.JobRobolectricTestRunner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.robolectric.annotation.Config;

import static Result.SUCCESS;


/**
 *
 *
 * @author rwondratschek
 */
@RunWith(JobRobolectricTestRunner.class)
@FixMethodOrder(MethodSorters.JVM)
public class JobPeriodicCancelTest extends BaseJobManagerTest {
    private JobPeriodicCancelTest.PeriodicJob mJob;

    @Test
    @Config(sdk = VERSION_CODES.N)
    public void verifyPeriodicFlexNotRescheduledN() {
        runJobAndCancelAllDuringExecution(true, false);
        assertThat(manager().getAllJobRequests()).isEmpty();
    }

    @Test
    @Config(sdk = VERSION_CODES.N)
    public void verifyPeriodicNotRescheduledN() {
        runJobAndCancelAllDuringExecution(false, false);
        assertThat(manager().getAllJobRequests()).isEmpty();
    }

    @Test
    @Config(sdk = VERSION_CODES.N)
    public void verifyPeriodicFlexNotRescheduledNSwap() {
        runJobAndCancelAllDuringExecution(true, true);
        assertThat(manager().getAllJobRequests()).isEmpty();
    }

    @Test
    @Config(sdk = VERSION_CODES.N)
    public void verifyPeriodicNotRescheduledNSwao() {
        runJobAndCancelAllDuringExecution(false, true);
        assertThat(manager().getAllJobRequests()).isEmpty();
    }

    @Test
    @Config(sdk = VERSION_CODES.M)
    public void verifyPeriodicFlexNotRescheduledM() {
        runJobAndCancelAllDuringExecution(true, false);
        assertThat(manager().getAllJobRequests()).isEmpty();
    }

    @Test
    @Config(sdk = VERSION_CODES.M)
    public void verifyPeriodicNotRescheduledM() {
        runJobAndCancelAllDuringExecution(false, false);
        assertThat(manager().getAllJobRequests()).isEmpty();
    }

    @Test
    @Config(sdk = VERSION_CODES.M)
    public void verifyPeriodicFlexNotRescheduledMSwap() {
        runJobAndCancelAllDuringExecution(true, true);
        assertThat(manager().getAllJobRequests()).isEmpty();
    }

    @Test
    @Config(sdk = VERSION_CODES.M)
    public void verifyPeriodicNotRescheduledMSwap() {
        runJobAndCancelAllDuringExecution(false, true);
        assertThat(manager().getAllJobRequests()).isEmpty();
    }

    @Test
    @Config(sdk = VERSION_CODES.KITKAT)
    public void verifyPeriodicFlexNotRescheduledK() {
        runJobAndCancelAllDuringExecution(true, false);
        assertThat(manager().getAllJobRequests()).isEmpty();
    }

    @Test
    @Config(sdk = VERSION_CODES.KITKAT)
    public void verifyPeriodicNotRescheduledK() {
        runJobAndCancelAllDuringExecution(false, false);
        assertThat(manager().getAllJobRequests()).isEmpty();
    }

    @Test
    @Config(sdk = VERSION_CODES.KITKAT)
    public void verifyPeriodicFlexNotRescheduledKSwap() {
        runJobAndCancelAllDuringExecution(true, true);
        assertThat(manager().getAllJobRequests()).isEmpty();
    }

    @Test
    @Config(sdk = VERSION_CODES.KITKAT)
    public void verifyPeriodicNotRescheduledKSwap() {
        runJobAndCancelAllDuringExecution(false, true);
        assertThat(manager().getAllJobRequests()).isEmpty();
    }

    private static class PeriodicJob extends Job {
        private final CountDownLatch mStartedLatch = new CountDownLatch(1);

        private final CountDownLatch mBlockingLatch = new CountDownLatch(1);

        @NonNull
        @Override
        protected Result onRunJob(@NonNull
        Params params) {
            mStartedLatch.countDown();
            try {
                mBlockingLatch.await(10, TimeUnit.SECONDS);
            } catch (InterruptedException ignored) {
            }
            return SUCCESS;
        }
    }
}

