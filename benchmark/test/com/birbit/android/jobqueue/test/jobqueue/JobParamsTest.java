package com.birbit.android.jobqueue.test.jobqueue;


import NetworkUtil.DISCONNECTED;
import NetworkUtil.METERED;
import Params.FOREVER;
import com.birbit.android.jobqueue.BuildConfig;
import com.birbit.android.jobqueue.JobHolder;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.test.TestBase;
import com.birbit.android.jobqueue.test.jobs.DummyJob;
import com.birbit.android.jobqueue.test.timer.MockTimer;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class JobParamsTest extends TestBase {
    @Test
    public void assertParamsUnderstood() {
        MockTimer mockTimer = new MockTimer();
        JobHolder j1 = JobQueueTestBase.createNewJobHolder(new Params(1).requireNetwork(), mockTimer);
        MatcherAssert.assertThat("require network param should be understood properly", j1.getRequiredNetworkType(), CoreMatchers.equalTo(METERED));
        JobHolder j2 = JobQueueTestBase.createNewJobHolder(new Params(1).groupBy("blah"), mockTimer);
        MatcherAssert.assertThat("group param should be understood properly", j2.getGroupId(), CoreMatchers.equalTo("blah"));
        MatcherAssert.assertThat("require network param should be understood properly", j2.getRequiredNetworkType(), CoreMatchers.equalTo(DISCONNECTED));
        JobHolder j3 = JobQueueTestBase.createNewJobHolder(new Params(1).persist(), mockTimer);
        MatcherAssert.assertThat("persist param should be understood properly", j3.persistent, CoreMatchers.equalTo(true));
        JobHolder j4 = JobQueueTestBase.createNewJobHolder(new Params(1).setPersistent(false).setRequiresNetwork(false).setGroupId(null).setSingleId(null), mockTimer);
        MatcherAssert.assertThat("persist param should be understood properly", j4.persistent, CoreMatchers.equalTo(false));
        MatcherAssert.assertThat("require network param should be understood properly", j4.getRequiredNetworkType(), CoreMatchers.equalTo(DISCONNECTED));
        MatcherAssert.assertThat("group param should be understood properly", j4.groupId, CoreMatchers.nullValue());
        MatcherAssert.assertThat("single param should be understood properly", j4.getSingleInstanceId(), CoreMatchers.nullValue());
        mockTimer.incrementMs(2);
        DummyJob j15 = new DummyJob(new Params(1).singleInstanceBy("bloop"));
        MatcherAssert.assertThat("single param should be understood properly", getSingleInstanceId(), CoreMatchers.endsWith("bloop"));
        MatcherAssert.assertThat("group param should be automatically set if single instance", getRunGroupId(), CoreMatchers.notNullValue());
        mockTimer.setNow(150);
        JobHolder j6 = JobQueueTestBase.createNewJobHolder(new Params(1), mockTimer);
        MatcherAssert.assertThat("no deadline", j6.getDeadlineNs(), CoreMatchers.is(FOREVER));
        JobHolder j7 = JobQueueTestBase.createNewJobHolder(new Params(1).overrideDeadlineToCancelInMs(100), mockTimer);
        MatcherAssert.assertThat("100 ms deadline", j7.getDeadlineNs(), CoreMatchers.is(100000150L));
        MatcherAssert.assertThat("100 ms deadline", j7.shouldCancelOnDeadline(), CoreMatchers.is(true));
        JobHolder j13 = JobQueueTestBase.createNewJobHolder(new Params(1).overrideDeadlineToCancelInMs(200), mockTimer);
        MatcherAssert.assertThat("100 ms deadline", j13.getDeadlineNs(), CoreMatchers.is(200000150L));
        MatcherAssert.assertThat("100 ms deadline", j7.shouldCancelOnDeadline(), CoreMatchers.is(true));
    }
}

