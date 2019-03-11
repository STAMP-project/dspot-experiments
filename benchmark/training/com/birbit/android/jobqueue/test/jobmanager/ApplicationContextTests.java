package com.birbit.android.jobqueue.test.jobmanager;


import RuntimeEnvironment.application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.birbit.android.jobqueue.BuildConfig;
import com.birbit.android.jobqueue.CancelReason;
import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.model.MultipleFailureException;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ApplicationContextTests extends JobManagerTestBase {
    static int retryCount = 0;

    static List<Throwable> errors = new ArrayList<>();

    @Test
    public void getContextNonPersistent() throws InterruptedException, MultipleFailureException {
        getContextTest(false);
    }

    @Test
    public void getContextPersistent() throws InterruptedException, MultipleFailureException {
        getContextTest(true);
    }

    public static class ContextCheckJob extends Job {
        protected ContextCheckJob(Params params) {
            super(params);
        }

        private void assertContext(String method) {
            Context applicationContext = getApplicationContext();
            try {
                MatcherAssert.assertThat(("Context should be application context in " + method), applicationContext, CoreMatchers.sameInstance(((Context) (application))));
            } catch (Throwable t) {
                ApplicationContextTests.errors.add(t);
            }
        }

        @Override
        public void onAdded() {
            assertContext("onAdded");
        }

        @Override
        public void onRun() throws Throwable {
            assertContext("onRun");
            if ((ApplicationContextTests.retryCount) < 2) {
                (ApplicationContextTests.retryCount)++;
                throw new RuntimeException("failure on purpose");
            }
        }

        @Override
        protected void onCancel(@CancelReason
        int cancelReason, @Nullable
        Throwable throwable) {
            assertContext("onCancel");
        }

        @Override
        protected RetryConstraint shouldReRunOnThrowable(@NonNull
        Throwable throwable, int runCount, int maxRunCount) {
            assertContext("shouldReRunOnThrowable");
            return (ApplicationContextTests.retryCount) < 2 ? RetryConstraint.RETRY : RetryConstraint.CANCEL;
        }
    }
}

