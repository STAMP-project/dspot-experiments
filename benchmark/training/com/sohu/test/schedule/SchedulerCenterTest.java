package com.sohu.test.schedule;


import com.sohu.cache.schedule.SchedulerCenter;
import com.sohu.test.BaseTest;
import javax.annotation.Resource;
import org.junit.Test;
import org.quartz.Trigger;
import org.quartz.TriggerKey;


/**
 *
 *
 * @unknown lingguo
 * @unknown 2014/9/2 11:47
 */
public class SchedulerCenterTest extends BaseTest {
    @Resource
    SchedulerCenter schedulerCenter;

    @Test
    public void testSchedule() {
        TriggerKey key = TriggerKey.triggerKey("appInfoAlertTrigger", "appAlert");
        Trigger trigger = schedulerCenter.getTrigger(key);
        if (trigger != null) {
            boolean isSchedule = schedulerCenter.unscheduleJob(key);
            logger.warn("isSchedule={}", isSchedule);
        }
        // try {
        // TimeUnit.SECONDS.sleep(5);
        // } catch (InterruptedException e) {
        // logger.error("{}", e);
        // }
    }
}

