/**
 * (c) 2017-2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.schedule;


import java.util.Calendar;
import java.util.Date;
import org.apache.commons.lang3.time.DateUtils;
import org.geoserver.taskmanager.AbstractTaskManagerTest;
import org.geoserver.taskmanager.data.Batch;
import org.geoserver.taskmanager.data.Configuration;
import org.geoserver.taskmanager.data.TaskManagerDao;
import org.geoserver.taskmanager.data.TaskManagerFactory;
import org.geoserver.taskmanager.util.TaskManagerDataUtil;
import org.junit.Assert;
import org.junit.Test;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Tests the batch job service.
 *
 * @author Niels Charlier
 */
public class BatchJobServiceTest extends AbstractTaskManagerTest {
    @Autowired
    private TaskManagerDao dao;

    @Autowired
    private TaskManagerFactory fac;

    @Autowired
    private TaskManagerDataUtil util;

    @Autowired
    private BatchJobService bjService;

    @Autowired
    private Scheduler scheduler;

    private Configuration config;

    private Batch batch;

    @Test
    public void testBatchJobService() throws SchedulerException {
        JobKey jobKey = new JobKey(batch.getId().toString());
        TriggerKey triggerKey = new TriggerKey(batch.getId().toString());
        // not scheduled yet
        Assert.assertTrue(scheduler.checkExists(jobKey));
        Assert.assertFalse(scheduler.checkExists(triggerKey));
        // give it a frequency
        batch.setFrequency("0 0 * * * ?");
        batch.setEnabled(true);
        bjService.saveAndSchedule(batch);
        Assert.assertTrue(scheduler.checkExists(jobKey));
        Assert.assertTrue(scheduler.checkExists(triggerKey));
        Trigger trigger = scheduler.getTrigger(triggerKey);
        Date nextFireTime = DateUtils.ceiling(new Date(), Calendar.HOUR);
        Assert.assertEquals(nextFireTime, trigger.getNextFireTime());
        // change the frequency
        batch.setFrequency("0 30 * * * ?");
        bjService.saveAndSchedule(batch);
        Assert.assertTrue(scheduler.checkExists(jobKey));
        Assert.assertTrue(scheduler.checkExists(triggerKey));
        trigger = scheduler.getTrigger(triggerKey);
        nextFireTime = DateUtils.addMinutes(DateUtils.round(new Date(), Calendar.HOUR), 30);
        Assert.assertEquals(nextFireTime, trigger.getNextFireTime());
        // de-activate it
        batch.setEnabled(false);
        bjService.saveAndSchedule(batch);
        Assert.assertTrue(scheduler.checkExists(jobKey));
        Assert.assertFalse(scheduler.checkExists(triggerKey));
        // delete it
        batch.setActive(false);
        bjService.saveAndSchedule(batch);
        Assert.assertFalse(scheduler.checkExists(jobKey));
        Assert.assertFalse(scheduler.checkExists(triggerKey));
    }
}

