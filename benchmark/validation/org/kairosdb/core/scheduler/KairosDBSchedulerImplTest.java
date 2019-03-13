package org.kairosdb.core.scheduler;


import java.util.Set;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.kairosdb.core.exception.KairosDBException;
import org.quartz.impl.JobDetailImpl;


public class KairosDBSchedulerImplTest {
    private KairosDBScheduler scheduler;

    @Test(expected = NullPointerException.class)
    public void testScheduleNullJobDetailInvalid() throws KairosDBException {
        scheduler.schedule(null, newTrigger().build());
    }

    @Test(expected = NullPointerException.class)
    public void testScheduleNullTriggerInvalid() throws KairosDBException {
        scheduler.schedule(new JobDetailImpl(), null);
    }

    @Test(expected = NullPointerException.class)
    public void testCancelNullIdInvalid() throws KairosDBException {
        scheduler.cancel(null);
    }

    @Test
    public void test() throws KairosDBException {
        scheduler.schedule(createJobDetail("1"), createTrigger("1"));
        scheduler.schedule(createJobDetail("2"), createTrigger("2"));
        Set<String> scheduledJobIds = scheduler.getScheduledJobIds();
        MatcherAssert.assertThat(scheduledJobIds.size(), CoreMatchers.equalTo(2));
        MatcherAssert.assertThat(scheduledJobIds, CoreMatchers.hasItem("1"));
        MatcherAssert.assertThat(scheduledJobIds, CoreMatchers.hasItem("2"));
        scheduler.cancel(getJobKey("1"));
        scheduledJobIds = scheduler.getScheduledJobIds();
        MatcherAssert.assertThat(scheduledJobIds.size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(scheduledJobIds, CoreMatchers.hasItem("2"));
        scheduler.cancel(getJobKey("2"));
        MatcherAssert.assertThat(scheduler.getScheduledJobIds().size(), CoreMatchers.equalTo(0));
    }
}

