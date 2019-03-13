package com.zegoggles.smssync.service;


import android.net.Uri;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Trigger;
import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class AlarmManagerDriverTest {
    private AlarmManagerDriver subject;

    @Test
    public void testScheduleJobWithExecutionWindowTrigger() throws Exception {
        final Job job = jobBuilder().setTrigger(Trigger.executionWindow(30, 30)).build();
        final int result = subject.schedule(job);
        assertThat(result).isEqualTo(SCHEDULE_RESULT_SUCCESS);
        assertAlarmScheduled("UNKNOWN");
    }

    @Test
    public void testScheduleJobWithExecutionWindowTriggerAndTag() throws Exception {
        final Job job = jobBuilder().setTag("REGULAR").setTrigger(Trigger.executionWindow(30, 30)).build();
        final int result = subject.schedule(job);
        assertThat(result).isEqualTo(SCHEDULE_RESULT_SUCCESS);
        assertAlarmScheduled("REGULAR");
    }

    @Test
    public void testScheduleJobWithoutTrigger() throws Exception {
        final Job job = jobBuilder().build();
        final int result = subject.schedule(job);
        assertThat(result).isEqualTo(SCHEDULE_RESULT_SUCCESS);
        assertAlarmScheduled("UNKNOWN");
    }

    @Test
    public void testScheduleJobWithUnknownTrigger() throws Exception {
        final Job job = jobBuilder().setTrigger(Trigger.contentUriTrigger(Collections.singletonList(new com.firebase.jobdispatcher.ObservedUri(Uri.parse("foo://bar"), 0)))).build();
        final int result = subject.schedule(job);
        assertThat(result).isEqualTo(SCHEDULE_RESULT_UNSUPPORTED_TRIGGER);
    }
}

