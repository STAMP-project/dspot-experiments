/**
 * (c) 2017-2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.tasks;


import TestTaskTypeImpl.NAME;
import TestTaskTypeImpl.PARAM_FAIL;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;
import org.geoserver.taskmanager.AbstractTaskManagerTest;
import org.geoserver.taskmanager.data.Batch;
import org.geoserver.taskmanager.data.Configuration;
import org.geoserver.taskmanager.data.Task;
import org.geoserver.taskmanager.data.TaskManagerDao;
import org.geoserver.taskmanager.data.TaskManagerFactory;
import org.geoserver.taskmanager.external.FileService;
import org.geoserver.taskmanager.external.impl.S3FileServiceImpl;
import org.geoserver.taskmanager.schedule.BatchJobService;
import org.geoserver.taskmanager.util.LookupService;
import org.geoserver.taskmanager.util.TaskManagerDataUtil;
import org.geoserver.taskmanager.util.TaskManagerTaskUtil;
import org.geotools.util.logging.Logging;
import org.junit.Assert;
import org.junit.Test;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * To run this test you should have a geoserver running on http://localhost:9090/geoserver.
 *
 * @author Niels Charlier
 */
public class CopyS3FileTaskTest extends AbstractTaskManagerTest {
    private static final Logger LOGGER = Logging.getLogger(CopyS3FileTaskTest.class);

    // configure these constants
    private static String SOURCE_ALIAS = "test";

    private static String TARGET_ALIAS = "test";

    private static String SOURCE_BUCKET = "source";

    private static String TARGET_BUCKET = "target";

    private static String SOURCE_SERVICE = S3FileServiceImpl.name(CopyS3FileTaskTest.SOURCE_ALIAS, CopyS3FileTaskTest.SOURCE_BUCKET);

    private static String TARGET_SERVICE = S3FileServiceImpl.name(CopyS3FileTaskTest.TARGET_ALIAS, CopyS3FileTaskTest.TARGET_BUCKET);

    private static String SOURCE_FILE = "test/salinity.tif";

    private static String TARGET_FILE_PATTERN = "new/salinity.###.tif";

    private static String TARGET_FILE_OLD = "new/salinity.42.tif";

    private static String TARGET_FILE_NEW = "new/salinity.43.tif";

    private static final String ATT_SOURCE_SERVICE = "source-service";

    private static final String ATT_TARGET_SERVICE = "target-service";

    private static final String ATT_SOURCE_PATH = "source-target";

    private static final String ATT_TARGET_PATH = "target-taret";

    @Autowired
    private TaskManagerDao dao;

    @Autowired
    private TaskManagerFactory fac;

    @Autowired
    private TaskManagerDataUtil dataUtil;

    @Autowired
    private TaskManagerTaskUtil taskUtil;

    @Autowired
    private BatchJobService bjService;

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private LookupService<FileService> fileServices;

    private Configuration config;

    private Batch batch;

    @Test
    public void testSuccessAndCleanup() throws IOException, SQLException, SchedulerException {
        dataUtil.setConfigurationAttribute(config, CopyS3FileTaskTest.ATT_SOURCE_SERVICE, CopyS3FileTaskTest.SOURCE_SERVICE);
        dataUtil.setConfigurationAttribute(config, CopyS3FileTaskTest.ATT_SOURCE_PATH, CopyS3FileTaskTest.SOURCE_FILE);
        dataUtil.setConfigurationAttribute(config, CopyS3FileTaskTest.ATT_TARGET_SERVICE, CopyS3FileTaskTest.TARGET_SERVICE);
        dataUtil.setConfigurationAttribute(config, CopyS3FileTaskTest.ATT_TARGET_PATH, CopyS3FileTaskTest.TARGET_FILE_PATTERN);
        config = dao.save(config);
        FileService fileService = fileServices.get(S3FileServiceImpl.name(CopyS3FileTaskTest.TARGET_ALIAS, CopyS3FileTaskTest.TARGET_BUCKET));
        Assert.assertTrue(fileService.checkFileExists(CopyS3FileTaskTest.TARGET_FILE_OLD));
        Assert.assertFalse(fileService.checkFileExists(CopyS3FileTaskTest.TARGET_FILE_NEW));
        Trigger trigger = TriggerBuilder.newTrigger().forJob(batch.getId().toString()).startNow().build();
        scheduler.scheduleJob(trigger);
        while ((scheduler.getTriggerState(trigger.getKey())) != (TriggerState.NONE)) {
        } 
        Assert.assertFalse(fileService.checkFileExists(CopyS3FileTaskTest.TARGET_FILE_OLD));
        Assert.assertTrue(fileService.checkFileExists(CopyS3FileTaskTest.TARGET_FILE_NEW));
        Assert.assertTrue(taskUtil.cleanup(config));
        Assert.assertFalse(fileService.checkFileExists(CopyS3FileTaskTest.TARGET_FILE_OLD));
        Assert.assertFalse(fileService.checkFileExists(CopyS3FileTaskTest.TARGET_FILE_NEW));
    }

    @Test
    public void testRollback() throws IOException, SQLException, SchedulerException {
        Task task2 = fac.createTask();
        task2.setName("task2");
        task2.setType(NAME);
        dataUtil.setTaskParameterToAttribute(task2, PARAM_FAIL, "fail");
        dataUtil.addTaskToConfiguration(config, task2);
        dataUtil.setConfigurationAttribute(config, CopyS3FileTaskTest.ATT_SOURCE_SERVICE, CopyS3FileTaskTest.SOURCE_SERVICE);
        dataUtil.setConfigurationAttribute(config, CopyS3FileTaskTest.ATT_SOURCE_PATH, CopyS3FileTaskTest.SOURCE_FILE);
        dataUtil.setConfigurationAttribute(config, CopyS3FileTaskTest.ATT_TARGET_SERVICE, CopyS3FileTaskTest.TARGET_SERVICE);
        dataUtil.setConfigurationAttribute(config, CopyS3FileTaskTest.ATT_TARGET_PATH, CopyS3FileTaskTest.TARGET_FILE_PATTERN);
        dataUtil.setConfigurationAttribute(config, "fail", "true");
        config = dao.save(config);
        task2 = config.getTasks().get("task2");
        dataUtil.addBatchElement(batch, task2);
        batch = bjService.saveAndSchedule(batch);
        FileService fileService = fileServices.get(S3FileServiceImpl.name(CopyS3FileTaskTest.TARGET_ALIAS, CopyS3FileTaskTest.TARGET_BUCKET));
        Assert.assertTrue(fileService.checkFileExists(CopyS3FileTaskTest.TARGET_FILE_OLD));
        Assert.assertFalse(fileService.checkFileExists(CopyS3FileTaskTest.TARGET_FILE_NEW));
        Trigger trigger = TriggerBuilder.newTrigger().forJob(batch.getId().toString()).startNow().build();
        scheduler.scheduleJob(trigger);
        while ((scheduler.getTriggerState(trigger.getKey())) != (TriggerState.NONE)) {
        } 
        Assert.assertTrue(fileService.checkFileExists(CopyS3FileTaskTest.TARGET_FILE_OLD));
        Assert.assertFalse(fileService.checkFileExists(CopyS3FileTaskTest.TARGET_FILE_NEW));
    }
}

