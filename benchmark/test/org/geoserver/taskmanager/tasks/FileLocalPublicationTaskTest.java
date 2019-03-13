/**
 * (c) 2017-2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.tasks;


import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.catalog.CoverageStoreInfo;
import org.geoserver.taskmanager.AbstractTaskManagerTest;
import org.geoserver.taskmanager.beans.TestTaskTypeImpl;
import org.geoserver.taskmanager.data.Batch;
import org.geoserver.taskmanager.data.Configuration;
import org.geoserver.taskmanager.data.Task;
import org.geoserver.taskmanager.data.TaskManagerDao;
import org.geoserver.taskmanager.data.TaskManagerFactory;
import org.geoserver.taskmanager.external.FileService;
import org.geoserver.taskmanager.schedule.BatchJobService;
import org.geoserver.taskmanager.util.LookupService;
import org.geoserver.taskmanager.util.TaskManagerDataUtil;
import org.geoserver.taskmanager.util.TaskManagerTaskUtil;
import org.junit.Assert;
import org.junit.Test;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;


public class FileLocalPublicationTaskTest extends AbstractTaskManagerTest {
    // configure these constants
    private static final String FILE_LOCATION = "test/world.tiff";

    private static final String FILE_SERVICE = "data-directory";

    private static final String WORKSPACE = "gs";

    private static final String COVERAGE_NAME = "world";

    private static final String LAYER_NAME = ((FileLocalPublicationTaskTest.WORKSPACE) + ":") + (FileLocalPublicationTaskTest.COVERAGE_NAME);

    // attributes
    private static final String ATT_FILE_SERVICE = "fileService";

    private static final String ATT_FILE = "file";

    private static final String ATT_LAYER = "layer";

    private static final String ATT_FAIL = "fail";

    @Autowired
    private TaskManagerDao dao;

    @Autowired
    private TaskManagerFactory fac;

    @Autowired
    private TaskManagerDataUtil dataUtil;

    @Autowired
    private BatchJobService bjService;

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private Catalog catalog;

    @Autowired
    private TaskManagerTaskUtil taskUtil;

    @Autowired
    private LookupService<FileService> fileServices;

    private Configuration config;

    private Batch batch;

    @Test
    public void testSuccessAndCleanup() throws SchedulerException {
        dataUtil.setConfigurationAttribute(config, FileLocalPublicationTaskTest.ATT_FILE_SERVICE, FileLocalPublicationTaskTest.FILE_SERVICE);
        dataUtil.setConfigurationAttribute(config, FileLocalPublicationTaskTest.ATT_FILE, FileLocalPublicationTaskTest.FILE_LOCATION);
        dataUtil.setConfigurationAttribute(config, FileLocalPublicationTaskTest.ATT_LAYER, FileLocalPublicationTaskTest.LAYER_NAME);
        config = dao.save(config);
        Trigger trigger = TriggerBuilder.newTrigger().forJob(batch.getId().toString()).startNow().build();
        scheduler.scheduleJob(trigger);
        while ((scheduler.getTriggerState(trigger.getKey())) != (TriggerState.NONE)) {
        } 
        Assert.assertNotNull(catalog.getLayerByName(FileLocalPublicationTaskTest.LAYER_NAME));
        CoverageStoreInfo csi = catalog.getStoreByName(FileLocalPublicationTaskTest.WORKSPACE, FileLocalPublicationTaskTest.COVERAGE_NAME, CoverageStoreInfo.class);
        Assert.assertNotNull(csi);
        Assert.assertEquals(fileServices.get(FileLocalPublicationTaskTest.FILE_SERVICE).getURI(FileLocalPublicationTaskTest.FILE_LOCATION).toString(), csi.getURL());
        Assert.assertNotNull(catalog.getResourceByName(FileLocalPublicationTaskTest.LAYER_NAME, CoverageInfo.class));
        taskUtil.cleanup(config);
        Assert.assertNull(catalog.getLayerByName(FileLocalPublicationTaskTest.LAYER_NAME));
        Assert.assertNull(catalog.getStoreByName(FileLocalPublicationTaskTest.WORKSPACE, FileLocalPublicationTaskTest.COVERAGE_NAME, CoverageStoreInfo.class));
        Assert.assertNull(catalog.getResourceByName(FileLocalPublicationTaskTest.LAYER_NAME, CoverageInfo.class));
    }

    @Test
    public void testRollback() throws SchedulerException {
        Task task2 = fac.createTask();
        task2.setName("task2");
        task2.setType(TestTaskTypeImpl.NAME);
        dataUtil.setTaskParameterToAttribute(task2, TestTaskTypeImpl.PARAM_FAIL, FileLocalPublicationTaskTest.ATT_FAIL);
        dataUtil.addTaskToConfiguration(config, task2);
        dataUtil.setConfigurationAttribute(config, FileLocalPublicationTaskTest.ATT_FILE_SERVICE, FileLocalPublicationTaskTest.FILE_SERVICE);
        dataUtil.setConfigurationAttribute(config, FileLocalPublicationTaskTest.ATT_FILE, FileLocalPublicationTaskTest.FILE_LOCATION);
        dataUtil.setConfigurationAttribute(config, FileLocalPublicationTaskTest.ATT_LAYER, FileLocalPublicationTaskTest.LAYER_NAME);
        dataUtil.setConfigurationAttribute(config, FileLocalPublicationTaskTest.ATT_FAIL, Boolean.TRUE.toString());
        config = dao.save(config);
        task2 = config.getTasks().get("task2");
        dataUtil.addBatchElement(batch, task2);
        batch = bjService.saveAndSchedule(batch);
        Trigger trigger = TriggerBuilder.newTrigger().forJob(batch.getId().toString()).startNow().build();
        scheduler.scheduleJob(trigger);
        while ((scheduler.getTriggerState(trigger.getKey())) != (TriggerState.NONE)) {
        } 
        Assert.assertNull(catalog.getLayerByName(FileLocalPublicationTaskTest.LAYER_NAME));
        Assert.assertNull(catalog.getStoreByName(FileLocalPublicationTaskTest.WORKSPACE, FileLocalPublicationTaskTest.COVERAGE_NAME, CoverageStoreInfo.class));
        Assert.assertNull(catalog.getResourceByName(FileLocalPublicationTaskTest.LAYER_NAME, CoverageInfo.class));
    }
}

