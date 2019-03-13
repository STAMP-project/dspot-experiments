/**
 * (c) 2017-2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.tasks;


import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.taskmanager.AbstractTaskManagerTest;
import org.geoserver.taskmanager.beans.TestTaskTypeImpl;
import org.geoserver.taskmanager.data.Batch;
import org.geoserver.taskmanager.data.Configuration;
import org.geoserver.taskmanager.data.Task;
import org.geoserver.taskmanager.data.TaskManagerDao;
import org.geoserver.taskmanager.data.TaskManagerFactory;
import org.geoserver.taskmanager.external.DbSource;
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


/**
 * To run this test you should have a postgres running on localhost with database 'mydb' (or
 * configure in application context), initiated with create-source.sql.
 *
 * @author Niels Charlier
 */
public class DbLocalPublicationTaskTest extends AbstractTaskManagerTest {
    // configure these constants
    // private static final String DB_NAME = "mypostgresdb";
    private static final String DB_NAME = "myjndidb";

    private static final String WORKSPACE = "gs";

    private static final String TABLE_NAME = "grondwaterlichamen_new";

    private static final String LAYER_NAME = (DbLocalPublicationTaskTest.WORKSPACE) + ":grondwaterlichamen";

    // attributes
    private static final String ATT_DB_NAME = "db";

    private static final String ATT_TABLE_NAME = "table_name";

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
    private LookupService<DbSource> dbSources;

    private Configuration config;

    private Batch batch;

    @Test
    public void testSuccessAndCleanup() throws SchedulerException {
        dataUtil.setConfigurationAttribute(config, DbLocalPublicationTaskTest.ATT_DB_NAME, DbLocalPublicationTaskTest.DB_NAME);
        dataUtil.setConfigurationAttribute(config, DbLocalPublicationTaskTest.ATT_TABLE_NAME, DbLocalPublicationTaskTest.TABLE_NAME);
        dataUtil.setConfigurationAttribute(config, DbLocalPublicationTaskTest.ATT_LAYER, DbLocalPublicationTaskTest.LAYER_NAME);
        config = dao.save(config);
        Trigger trigger = TriggerBuilder.newTrigger().forJob(batch.getId().toString()).startNow().build();
        scheduler.scheduleJob(trigger);
        while ((scheduler.getTriggerState(trigger.getKey())) != (TriggerState.NONE)) {
        } 
        Assert.assertNotNull(catalog.getLayerByName(DbLocalPublicationTaskTest.LAYER_NAME));
        Assert.assertNotNull(catalog.getStoreByName(DbLocalPublicationTaskTest.WORKSPACE, DbLocalPublicationTaskTest.DB_NAME, DataStoreInfo.class));
        FeatureTypeInfo fti = catalog.getResourceByName(DbLocalPublicationTaskTest.LAYER_NAME, FeatureTypeInfo.class);
        Assert.assertNotNull(fti);
        Assert.assertEquals(DbLocalPublicationTaskTest.TABLE_NAME, fti.getNativeName());
        taskUtil.cleanup(config);
        Assert.assertNull(catalog.getLayerByName(DbLocalPublicationTaskTest.LAYER_NAME));
        Assert.assertNull(catalog.getStoreByName(DbLocalPublicationTaskTest.WORKSPACE, DbLocalPublicationTaskTest.ATT_DB_NAME, DataStoreInfo.class));
        Assert.assertNull(catalog.getResourceByName(DbLocalPublicationTaskTest.LAYER_NAME, FeatureTypeInfo.class));
    }

    @Test
    public void testRollback() throws SchedulerException {
        Task task2 = fac.createTask();
        task2.setName("task2");
        task2.setType(TestTaskTypeImpl.NAME);
        dataUtil.setTaskParameterToAttribute(task2, TestTaskTypeImpl.PARAM_FAIL, DbLocalPublicationTaskTest.ATT_FAIL);
        dataUtil.addTaskToConfiguration(config, task2);
        dataUtil.setConfigurationAttribute(config, DbLocalPublicationTaskTest.ATT_DB_NAME, DbLocalPublicationTaskTest.DB_NAME);
        dataUtil.setConfigurationAttribute(config, DbLocalPublicationTaskTest.ATT_TABLE_NAME, DbLocalPublicationTaskTest.TABLE_NAME);
        dataUtil.setConfigurationAttribute(config, DbLocalPublicationTaskTest.ATT_LAYER, DbLocalPublicationTaskTest.LAYER_NAME);
        dataUtil.setConfigurationAttribute(config, DbLocalPublicationTaskTest.ATT_FAIL, Boolean.TRUE.toString());
        config = dao.save(config);
        task2 = config.getTasks().get("task2");
        dataUtil.addBatchElement(batch, task2);
        batch = bjService.saveAndSchedule(batch);
        Trigger trigger = TriggerBuilder.newTrigger().forJob(batch.getId().toString()).startNow().build();
        scheduler.scheduleJob(trigger);
        while ((scheduler.getTriggerState(trigger.getKey())) != (TriggerState.NONE)) {
        } 
        Assert.assertNull(catalog.getLayerByName(DbLocalPublicationTaskTest.LAYER_NAME));
        Assert.assertNull(catalog.getStoreByName(DbLocalPublicationTaskTest.WORKSPACE, DbLocalPublicationTaskTest.ATT_DB_NAME, DataStoreInfo.class));
        Assert.assertNull(catalog.getResourceByName(DbLocalPublicationTaskTest.LAYER_NAME, FeatureTypeInfo.class));
    }
}

