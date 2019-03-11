/**
 * (c) 2017-2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.schedule;


import Filter.FAILED_AND_CANCELLED;
import Filter.FAILED_ONLY;
import Run.Status.COMMITTED;
import Run.Status.FAILED;
import Run.Status.ROLLED_BACK;
import org.geoserver.taskmanager.AbstractTaskManagerTest;
import org.geoserver.taskmanager.beans.TestReportServiceImpl;
import org.geoserver.taskmanager.beans.TestTaskTypeImpl;
import org.geoserver.taskmanager.data.Batch;
import org.geoserver.taskmanager.data.BatchRun;
import org.geoserver.taskmanager.data.Configuration;
import org.geoserver.taskmanager.data.TaskManagerDao;
import org.geoserver.taskmanager.data.TaskManagerFactory;
import org.geoserver.taskmanager.util.TaskManagerDataUtil;
import org.junit.Assert;
import org.junit.Test;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Tests the batch job.
 *
 * @author Niels Charlier
 */
public class BatchJobTest extends AbstractTaskManagerTest {
    private static final String ATT_DELAY = "delay";

    private static final String ATT_FAIL = "fail";

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

    @Autowired
    private TestTaskTypeImpl testTaskType;

    @Autowired
    private TestReportServiceImpl testReportService;

    private Configuration config;

    private Batch batch;

    @Test
    public void testSuccess() throws InterruptedException, SchedulerException {
        Trigger trigger = TriggerBuilder.newTrigger().forJob(batch.getId().toString()).startNow().build();
        scheduler.scheduleJob(trigger);
        while ((scheduler.getTriggerState(trigger.getKey())) != (TriggerState.NONE)) {
        } 
        Assert.assertEquals(4, testTaskType.getStatus().get("my_batch:my_config/task1").intValue());
        Assert.assertEquals(4, testTaskType.getStatus().get("my_batch:my_config/task2").intValue());
        Assert.assertEquals(4, testTaskType.getStatus().get("my_batch:my_config/task3").intValue());
        Assert.assertEquals(COMMITTED, dao.getLatestRun(batch.getElements().get(0)).getStatus());
        Assert.assertEquals(COMMITTED, dao.getLatestRun(batch.getElements().get(1)).getStatus());
        Assert.assertEquals(COMMITTED, dao.getLatestRun(batch.getElements().get(2)).getStatus());
        Assert.assertEquals("Report: Batch my_batch was successful", testReportService.getLastReport().getTitle());
        Assert.assertTrue(testReportService.getLastReport().getContent().contains("my_config/task1, started"));
        Assert.assertTrue(testReportService.getLastReport().getContent().contains("my_config/task2, started"));
        Assert.assertTrue(testReportService.getLastReport().getContent().contains("my_config/task3, started"));
        Assert.assertTrue(testReportService.getLastReport().getContent().contains(", ended"));
        Assert.assertTrue(testReportService.getLastReport().getContent().contains("status is COMMITTED"));
        // repeat with different report filter
        testReportService.clear();
        testReportService.setFilter(FAILED_AND_CANCELLED);
        scheduler.scheduleJob(trigger);
        while ((scheduler.getTriggerState(trigger.getKey())) != (TriggerState.NONE)) {
        } 
        Assert.assertNull(testReportService.getLastReport());
    }

    @Test
    public void testFailed() throws InterruptedException, SchedulerException {
        util.setConfigurationAttribute(config, BatchJobTest.ATT_FAIL, "true");
        config = dao.save(config);
        Trigger trigger = TriggerBuilder.newTrigger().forJob(batch.getId().toString()).startNow().build();
        scheduler.scheduleJob(trigger);
        while ((scheduler.getTriggerState(trigger.getKey())) != (TriggerState.NONE)) {
        } 
        Assert.assertEquals(0, testTaskType.getStatus().get("my_batch:my_config/task1").intValue());
        Assert.assertEquals(0, testTaskType.getStatus().get("my_batch:my_config/task2").intValue());
        Assert.assertEquals(2, testTaskType.getStatus().get("my_batch:my_config/task3").intValue());
        Assert.assertEquals(ROLLED_BACK, dao.getLatestRun(batch.getElements().get(0)).getStatus());
        Assert.assertEquals(ROLLED_BACK, dao.getLatestRun(batch.getElements().get(1)).getStatus());
        Assert.assertEquals(FAILED, dao.getLatestRun(batch.getElements().get(2)).getStatus());
        Assert.assertEquals("Report: Batch my_batch has failed", testReportService.getLastReport().getTitle());
        Assert.assertTrue(testReportService.getLastReport().getContent().contains("status is ROLLED_BACK"));
        Assert.assertTrue(testReportService.getLastReport().getContent().contains("status is FAILED"));
        Assert.assertTrue(testReportService.getLastReport().getContent().contains("message: purposely failed task (check logs for more details"));
    }

    @Test
    public void testCancel() throws InterruptedException, SchedulerException {
        config.getAttributes().remove("");
        config = dao.save(config);
        util.setConfigurationAttribute(config, BatchJobTest.ATT_DELAY, "5000");
        config = dao.save(config);
        Trigger trigger = TriggerBuilder.newTrigger().forJob(batch.getId().toString()).startNow().build();
        scheduler.scheduleJob(trigger);
        while ((testTaskType.getStatus().get("my_batch:my_config/task3")) == null) {
        } 
        Thread.sleep(1000);
        batch = dao.initHistory(batch);
        BatchRun br = batch.getBatchRuns().get(((batch.getBatchRuns().size()) - 1));
        br.setInterruptMe(true);
        br = dao.save(br);
        while ((scheduler.getTriggerState(trigger.getKey())) != (TriggerState.NONE)) {
        } 
        Assert.assertEquals(0, testTaskType.getStatus().get("my_batch:my_config/task1").intValue());
        Assert.assertEquals(0, testTaskType.getStatus().get("my_batch:my_config/task2").intValue());
        Assert.assertEquals(0, testTaskType.getStatus().get("my_batch:my_config/task3").intValue());
        Assert.assertEquals(ROLLED_BACK, dao.getLatestRun(batch.getElements().get(0)).getStatus());
        Assert.assertEquals(ROLLED_BACK, dao.getLatestRun(batch.getElements().get(1)).getStatus());
        Assert.assertEquals(ROLLED_BACK, dao.getLatestRun(batch.getElements().get(2)).getStatus());
        Assert.assertEquals("Report: Batch my_batch was cancelled", testReportService.getLastReport().getTitle());
        Assert.assertTrue(testReportService.getLastReport().getContent().contains("status is ROLLED_BACK"));
        // run with filter
        testReportService.clear();
        testReportService.setFilter(FAILED_ONLY);
        scheduler.scheduleJob(trigger);
        while ((scheduler.getTriggerState(trigger.getKey())) != (TriggerState.NONE)) {
        } 
        Assert.assertNull(testReportService.getLastReport());
    }

    @Test
    public void testParameterValidation() throws InterruptedException, SchedulerException {
        util.setConfigurationAttribute(config, BatchJobTest.ATT_DELAY, "bla");
        config = dao.save(config);
        Trigger trigger = TriggerBuilder.newTrigger().forJob(batch.getId().toString()).startNow().build();
        scheduler.scheduleJob(trigger);
        while ((scheduler.getTriggerState(trigger.getKey())) != (TriggerState.NONE)) {
        } 
        Assert.assertEquals(0, testTaskType.getStatus().get("my_batch:my_config/task1").intValue());
        Assert.assertEquals(0, testTaskType.getStatus().get("my_batch:my_config/task2").intValue());
        Assert.assertEquals(1, testTaskType.getStatus().get("my_batch:my_config/task3").intValue());
        Assert.assertEquals(ROLLED_BACK, dao.getLatestRun(batch.getElements().get(0)).getStatus());
        Assert.assertEquals(ROLLED_BACK, dao.getLatestRun(batch.getElements().get(1)).getStatus());
        Assert.assertEquals(FAILED, dao.getLatestRun(batch.getElements().get(2)).getStatus());
        Assert.assertEquals("Report: Batch my_batch has failed", testReportService.getLastReport().getTitle());
        Assert.assertTrue(testReportService.getLastReport().getContent().contains("status is ROLLED_BACK"));
        Assert.assertTrue(testReportService.getLastReport().getContent().contains("status is FAILED"));
        Assert.assertTrue(testReportService.getLastReport().getContent().contains("message: There were validation errors: [bla is not a valid parameter value for parameter delay in task type Test] (check logs for more details)"));
    }
}

