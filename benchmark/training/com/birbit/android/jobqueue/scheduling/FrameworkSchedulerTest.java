package com.birbit.android.jobqueue.scheduling;


import Build.VERSION_CODES;
import FrameworkScheduler.KEY_ID;
import JobInfo.NETWORK_TYPE_ANY;
import JobInfo.NETWORK_TYPE_NONE;
import JobInfo.NETWORK_TYPE_UNMETERED;
import NetworkUtil.DISCONNECTED;
import NetworkUtil.METERED;
import NetworkUtil.UNMETERED;
import Scheduler.Callback;
import SharedPreferences.Editor;
import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.PersistableBundle;
import com.birbit.android.jobqueue.BuildConfig;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
@TargetApi(VERSION_CODES.LOLLIPOP)
public class FrameworkSchedulerTest {
    FrameworkScheduler fwScheduler;

    Context mockContext;

    Callback mockCallback;

    JobScheduler mockJobScheduler;

    SharedPreferences mockSharedPreferences;

    Editor mockEditor;

    @Test
    public void componentName() {
        MatcherAssert.assertThat(fwScheduler.getComponentName(), CoreMatchers.equalTo(new android.content.ComponentName(mockContext, MockFwService.class)));
    }

    @Test
    public void requestSimple() {
        networkTest(METERED, NETWORK_TYPE_ANY);
    }

    @Test
    public void requestUnmetered() {
        networkTest(UNMETERED, NETWORK_TYPE_UNMETERED);
    }

    @Test
    public void requestAnyNetwork() {
        networkTest(DISCONNECTED, NETWORK_TYPE_NONE);
    }

    @Test
    public void delay() {
        SchedulerConstraint constraint = Mockito.mock(SchedulerConstraint.class);
        Mockito.when(constraint.getNetworkStatus()).thenReturn(DISCONNECTED);
        Mockito.when(constraint.getDelayInMs()).thenReturn(133L);
        JobInfo jobInfo = schedule(constraint);
        MatcherAssert.assertThat(jobInfo.isPersisted(), CoreMatchers.is(true));
        MatcherAssert.assertThat(jobInfo.getId(), CoreMatchers.is(1));
        MatcherAssert.assertThat(jobInfo.getMinLatencyMillis(), CoreMatchers.is(133L));
        MatcherAssert.assertThat(jobInfo.getMaxExecutionDelayMillis(), CoreMatchers.is(0L));
    }

    @Test
    public void deadline() {
        SchedulerConstraint constraint = Mockito.mock(SchedulerConstraint.class);
        Mockito.when(constraint.getNetworkStatus()).thenReturn(DISCONNECTED);
        Mockito.when(constraint.getOverrideDeadlineInMs()).thenReturn(255L);
        JobInfo jobInfo = schedule(constraint);
        MatcherAssert.assertThat(jobInfo.isPersisted(), CoreMatchers.is(true));
        MatcherAssert.assertThat(jobInfo.getId(), CoreMatchers.is(1));
        MatcherAssert.assertThat(jobInfo.getMinLatencyMillis(), CoreMatchers.is(0L));
        MatcherAssert.assertThat(jobInfo.getMaxExecutionDelayMillis(), CoreMatchers.is(255L));
    }

    @Test
    public void deadlineAndDelay() {
        SchedulerConstraint constraint = Mockito.mock(SchedulerConstraint.class);
        Mockito.when(constraint.getNetworkStatus()).thenReturn(DISCONNECTED);
        Mockito.when(constraint.getOverrideDeadlineInMs()).thenReturn(255L);
        Mockito.when(constraint.getDelayInMs()).thenReturn(133L);
        JobInfo jobInfo = schedule(constraint);
        MatcherAssert.assertThat(jobInfo.isPersisted(), CoreMatchers.is(true));
        MatcherAssert.assertThat(jobInfo.getId(), CoreMatchers.is(1));
        MatcherAssert.assertThat(jobInfo.getMinLatencyMillis(), CoreMatchers.is(133L));
        MatcherAssert.assertThat(jobInfo.getMaxExecutionDelayMillis(), CoreMatchers.is(255L));
    }

    @Test
    public void bundle() throws Exception {
        SchedulerConstraint constraint = Mockito.mock(SchedulerConstraint.class);
        Mockito.when(constraint.getNetworkStatus()).thenReturn(METERED);
        Mockito.when(constraint.getUuid()).thenReturn("abc");
        Mockito.when(constraint.getDelayInMs()).thenReturn(345L);
        Mockito.when(constraint.getOverrideDeadlineInMs()).thenReturn(235L);
        JobInfo jobInfo = schedule(constraint);
        PersistableBundle extras = jobInfo.getExtras();
        SchedulerConstraint fromBundle = FrameworkScheduler.fromBundle(extras);
        MatcherAssert.assertThat(fromBundle.getNetworkStatus(), CoreMatchers.is(METERED));
        MatcherAssert.assertThat(fromBundle.getUuid(), CoreMatchers.is("abc"));
        MatcherAssert.assertThat(fromBundle.getDelayInMs(), CoreMatchers.is(345L));
        MatcherAssert.assertThat(fromBundle.getOverrideDeadlineInMs(), CoreMatchers.is(235L));
    }

    @Test
    public void bundleNullDeadline() throws Exception {
        SchedulerConstraint constraint = Mockito.mock(SchedulerConstraint.class);
        Mockito.when(constraint.getNetworkStatus()).thenReturn(METERED);
        Mockito.when(constraint.getUuid()).thenReturn("abc");
        Mockito.when(constraint.getDelayInMs()).thenReturn(345L);
        Mockito.when(constraint.getOverrideDeadlineInMs()).thenReturn(null);
        JobInfo jobInfo = schedule(constraint);
        PersistableBundle extras = jobInfo.getExtras();
        SchedulerConstraint fromBundle = FrameworkScheduler.fromBundle(extras);
        MatcherAssert.assertThat(fromBundle.getNetworkStatus(), CoreMatchers.is(METERED));
        MatcherAssert.assertThat(fromBundle.getUuid(), CoreMatchers.is("abc"));
        MatcherAssert.assertThat(fromBundle.getDelayInMs(), CoreMatchers.is(345L));
        MatcherAssert.assertThat(fromBundle.getOverrideDeadlineInMs(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void badBundleOnStart() {
        // see https://github.com/yigit/android-priority-jobqueue/issues/254
        JobParameters params = Mockito.mock(JobParameters.class);
        PersistableBundle badBundle = Mockito.mock(PersistableBundle.class);
        Mockito.when(badBundle.getString(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenThrow(new NullPointerException());
        Mockito.when(badBundle.getString(ArgumentMatchers.anyString())).thenThrow(new NullPointerException());
        MatcherAssert.assertThat(fwScheduler.onStartJob(params), CoreMatchers.is(false));
    }

    @Test
    public void badBundleOnStop() {
        // see https://github.com/yigit/android-priority-jobqueue/issues/254
        JobParameters params = Mockito.mock(JobParameters.class);
        PersistableBundle badBundle = Mockito.mock(PersistableBundle.class);
        Mockito.when(badBundle.getString(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenThrow(new NullPointerException());
        Mockito.when(badBundle.getString(ArgumentMatchers.anyString())).thenThrow(new NullPointerException());
        MatcherAssert.assertThat(fwScheduler.onStopJob(params), CoreMatchers.is(false));
    }

    @Test
    public void onStart1() {
        onStartTest(DISCONNECTED, 0, null);
    }

    @Test
    public void onStart2() {
        onStartTest(METERED, 0, null);
    }

    @Test
    public void onStart3() {
        onStartTest(UNMETERED, 0, null);
    }

    @Test
    public void onStart4() {
        onStartTest(METERED, 10, null);
    }

    @Test
    public void onStart5() {
        onStartTest(METERED, 10, null);
    }

    @Test
    public void onStart6() {
        onStartTest(METERED, 0, 100L);
    }

    @Test
    public void onStart7() {
        onStartTest(UNMETERED, 35, 77L);
    }

    @Test
    public void onFinished() {
        SchedulerConstraint constraint = Mockito.mock(SchedulerConstraint.class);
        Mockito.when(constraint.getNetworkStatus()).thenReturn(METERED);
        Mockito.when(constraint.getUuid()).thenReturn("abc");
        Mockito.when(constraint.getDelayInMs()).thenReturn(22L);
        Mockito.when(constraint.getOverrideDeadlineInMs()).thenReturn(null);
        JobParameters[] outParams = new JobParameters[1];
        SchedulerConstraint received = triggerOnStart(constraint, outParams);
        fwScheduler.onFinished(received, false);
        // TODO would be nice to use powermock and assert onFinished call
    }

    @Test
    public void cancelAll() {
        fwScheduler.cancelAll();
        Mockito.verify(mockJobScheduler).cancelAll();
    }

    @Test
    public void createId() {
        Mockito.when(mockSharedPreferences.getInt(KEY_ID, 0)).thenReturn(33);
        MatcherAssert.assertThat(fwScheduler.createId(), CoreMatchers.is(34));
        Mockito.verify(mockEditor).putInt(KEY_ID, 34);
    }
}

