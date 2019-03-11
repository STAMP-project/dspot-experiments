/**
 * (c) 2017-2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.tasks;


import java.sql.SQLException;
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
import org.geoserver.taskmanager.util.SqlUtil;
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
 *
 *
 * @author Niels Charlier
 * @author Timothy De Bock
 */
public class CreateViewTaskTest extends AbstractTaskManagerTest {
    // configure these constants
    private static final String DB_NAME = "testsourcedb";

    private static final String TABLE_NAME = "gw_beleid.grondwaterlichamen_new";

    private static final String VIEW_NAME = "gw_beleid.vw_grondwaterlichamen_new";

    private static final String SELECT = "dataengine_id";

    private static final String WHERE = "gwl like 'BL%'";

    private static final int NUMBER_OF_RECORDS = 7;

    private static final int NUMBER_OF_COLUMNS = 1;

    private static final String VIEW_NAME_NEW_SCHEMA = "newschema.vw_grondwaterlichamen";

    // attributes
    private static final String ATT_DB_NAME = "db";

    private static final String ATT_TABLE_NAME = "table_name";

    private static final String ATT_VIEW_NAME = "view_name";

    private static final String ATT_SELECT = "select";

    private static final String ATT_WHERE = "where";

    private static final String ATT_FAIL = "fail";

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
    private LookupService<DbSource> dbSources;

    @Autowired
    private Scheduler scheduler;

    private Configuration config;

    private Batch batch;

    @Test
    public void testSimpleView() throws SQLException, SchedulerException {
        dataUtil.setConfigurationAttribute(config, CreateViewTaskTest.ATT_DB_NAME, CreateViewTaskTest.DB_NAME);
        dataUtil.setConfigurationAttribute(config, CreateViewTaskTest.ATT_TABLE_NAME, CreateViewTaskTest.TABLE_NAME);
        dataUtil.setConfigurationAttribute(config, CreateViewTaskTest.ATT_VIEW_NAME, CreateViewTaskTest.VIEW_NAME);
        dataUtil.setConfigurationAttribute(config, CreateViewTaskTest.ATT_SELECT, "*");
        config = dao.save(config);
        Trigger trigger = TriggerBuilder.newTrigger().forJob(batch.getId().toString()).startNow().build();
        scheduler.scheduleJob(trigger);
        while ((scheduler.getTriggerState(trigger.getKey())) != (TriggerState.NONE)) {
        } 
        Assert.assertFalse(viewExists(SqlUtil.schema(CreateViewTaskTest.VIEW_NAME), "_temp%"));
        Assert.assertTrue(viewExists(SqlUtil.schema(CreateViewTaskTest.VIEW_NAME), SqlUtil.notQualified(CreateViewTaskTest.VIEW_NAME)));
        Assert.assertEquals(getNumberOfRecords(CreateViewTaskTest.TABLE_NAME), getNumberOfRecords(CreateViewTaskTest.VIEW_NAME));
        Assert.assertEquals(getNumberOfColumns(CreateViewTaskTest.TABLE_NAME), getNumberOfColumns(CreateViewTaskTest.VIEW_NAME));
        Assert.assertTrue(taskUtil.cleanup(config));
        Assert.assertFalse(viewExists(SqlUtil.schema(CreateViewTaskTest.VIEW_NAME), SqlUtil.notQualified(CreateViewTaskTest.VIEW_NAME)));
    }

    @Test
    public void testComplexView() throws SQLException, SchedulerException {
        dataUtil.setConfigurationAttribute(config, CreateViewTaskTest.ATT_DB_NAME, CreateViewTaskTest.DB_NAME);
        dataUtil.setConfigurationAttribute(config, CreateViewTaskTest.ATT_TABLE_NAME, CreateViewTaskTest.TABLE_NAME);
        dataUtil.setConfigurationAttribute(config, CreateViewTaskTest.ATT_VIEW_NAME, CreateViewTaskTest.VIEW_NAME);
        dataUtil.setConfigurationAttribute(config, CreateViewTaskTest.ATT_SELECT, CreateViewTaskTest.SELECT);
        dataUtil.setConfigurationAttribute(config, CreateViewTaskTest.ATT_WHERE, CreateViewTaskTest.WHERE);
        config = dao.save(config);
        Trigger trigger = TriggerBuilder.newTrigger().forJob(batch.getId().toString()).startNow().build();
        scheduler.scheduleJob(trigger);
        while ((scheduler.getTriggerState(trigger.getKey())) != (TriggerState.NONE)) {
        } 
        Assert.assertFalse(viewExists(SqlUtil.schema(CreateViewTaskTest.VIEW_NAME), "_temp%"));
        Assert.assertTrue(viewExists(SqlUtil.schema(CreateViewTaskTest.VIEW_NAME), SqlUtil.notQualified(CreateViewTaskTest.VIEW_NAME)));
        Assert.assertEquals(CreateViewTaskTest.NUMBER_OF_RECORDS, getNumberOfRecords(CreateViewTaskTest.VIEW_NAME));
        Assert.assertEquals(CreateViewTaskTest.NUMBER_OF_COLUMNS, getNumberOfColumns(CreateViewTaskTest.VIEW_NAME));
        Assert.assertTrue(taskUtil.cleanup(config));
        Assert.assertFalse(viewExists(SqlUtil.schema(CreateViewTaskTest.VIEW_NAME), SqlUtil.notQualified(CreateViewTaskTest.VIEW_NAME)));
    }

    @Test
    public void testSimpleViewInNewSchema() throws SQLException, SchedulerException {
        dataUtil.setConfigurationAttribute(config, CreateViewTaskTest.ATT_DB_NAME, CreateViewTaskTest.DB_NAME);
        dataUtil.setConfigurationAttribute(config, CreateViewTaskTest.ATT_TABLE_NAME, CreateViewTaskTest.TABLE_NAME);
        dataUtil.setConfigurationAttribute(config, CreateViewTaskTest.ATT_VIEW_NAME, CreateViewTaskTest.VIEW_NAME_NEW_SCHEMA);
        dataUtil.setConfigurationAttribute(config, CreateViewTaskTest.ATT_SELECT, "*");
        config = dao.save(config);
        Trigger trigger = TriggerBuilder.newTrigger().forJob(batch.getId().toString()).startNow().build();
        scheduler.scheduleJob(trigger);
        while ((scheduler.getTriggerState(trigger.getKey())) != (TriggerState.NONE)) {
        } 
        Assert.assertFalse(viewExists(SqlUtil.schema(CreateViewTaskTest.VIEW_NAME_NEW_SCHEMA), "_temp%"));
        Assert.assertTrue(viewExists(SqlUtil.schema(CreateViewTaskTest.VIEW_NAME_NEW_SCHEMA), SqlUtil.notQualified(CreateViewTaskTest.VIEW_NAME_NEW_SCHEMA)));
        Assert.assertTrue(taskUtil.cleanup(config));
        Assert.assertFalse(viewExists(SqlUtil.schema(CreateViewTaskTest.VIEW_NAME_NEW_SCHEMA), SqlUtil.notQualified(CreateViewTaskTest.VIEW_NAME_NEW_SCHEMA)));
    }

    @Test
    public void testComplexViewInNewSchema() throws SQLException, SchedulerException {
        dataUtil.setConfigurationAttribute(config, CreateViewTaskTest.ATT_DB_NAME, CreateViewTaskTest.DB_NAME);
        dataUtil.setConfigurationAttribute(config, CreateViewTaskTest.ATT_TABLE_NAME, CreateViewTaskTest.TABLE_NAME);
        dataUtil.setConfigurationAttribute(config, CreateViewTaskTest.ATT_VIEW_NAME, CreateViewTaskTest.VIEW_NAME_NEW_SCHEMA);
        dataUtil.setConfigurationAttribute(config, CreateViewTaskTest.ATT_SELECT, CreateViewTaskTest.SELECT);
        dataUtil.setConfigurationAttribute(config, CreateViewTaskTest.ATT_WHERE, CreateViewTaskTest.WHERE);
        config = dao.save(config);
        Trigger trigger = TriggerBuilder.newTrigger().forJob(batch.getId().toString()).startNow().build();
        scheduler.scheduleJob(trigger);
        while ((scheduler.getTriggerState(trigger.getKey())) != (TriggerState.NONE)) {
        } 
        Assert.assertFalse(viewExists(SqlUtil.schema(CreateViewTaskTest.VIEW_NAME_NEW_SCHEMA), "_temp%"));
        Assert.assertTrue(viewExists(SqlUtil.schema(CreateViewTaskTest.VIEW_NAME_NEW_SCHEMA), SqlUtil.notQualified(CreateViewTaskTest.VIEW_NAME_NEW_SCHEMA)));
        Assert.assertEquals(CreateViewTaskTest.NUMBER_OF_RECORDS, getNumberOfRecords(CreateViewTaskTest.VIEW_NAME_NEW_SCHEMA));
        Assert.assertEquals(CreateViewTaskTest.NUMBER_OF_COLUMNS, getNumberOfColumns(CreateViewTaskTest.VIEW_NAME_NEW_SCHEMA));
        Assert.assertTrue(taskUtil.cleanup(config));
        Assert.assertFalse(viewExists(SqlUtil.schema(CreateViewTaskTest.VIEW_NAME_NEW_SCHEMA), SqlUtil.notQualified(CreateViewTaskTest.VIEW_NAME_NEW_SCHEMA)));
    }

    @Test
    public void testRollback() throws SQLException, SchedulerException {
        Task task2 = fac.createTask();
        task2.setName("task2");
        task2.setType(TestTaskTypeImpl.NAME);
        dataUtil.setTaskParameterToAttribute(task2, TestTaskTypeImpl.PARAM_FAIL, CreateViewTaskTest.ATT_FAIL);
        dataUtil.addTaskToConfiguration(config, task2);
        dataUtil.setConfigurationAttribute(config, CreateViewTaskTest.ATT_DB_NAME, CreateViewTaskTest.DB_NAME);
        dataUtil.setConfigurationAttribute(config, CreateViewTaskTest.ATT_TABLE_NAME, CreateViewTaskTest.TABLE_NAME);
        dataUtil.setConfigurationAttribute(config, CreateViewTaskTest.ATT_VIEW_NAME, CreateViewTaskTest.VIEW_NAME);
        dataUtil.setConfigurationAttribute(config, CreateViewTaskTest.ATT_SELECT, "*");
        dataUtil.setConfigurationAttribute(config, CreateViewTaskTest.ATT_FAIL, Boolean.TRUE.toString());
        config = dao.save(config);
        task2 = config.getTasks().get("task2");
        dataUtil.addBatchElement(batch, task2);
        batch = bjService.saveAndSchedule(batch);
        Trigger trigger = TriggerBuilder.newTrigger().forJob(batch.getId().toString()).startNow().build();
        scheduler.scheduleJob(trigger);
        while ((scheduler.getTriggerState(trigger.getKey())) != (TriggerState.NONE)) {
        } 
        Assert.assertFalse(viewExists(SqlUtil.schema(CreateViewTaskTest.VIEW_NAME), SqlUtil.notQualified(CreateViewTaskTest.VIEW_NAME)));
        Assert.assertFalse(viewExists(SqlUtil.schema(CreateViewTaskTest.VIEW_NAME), "_temp%"));
    }
}

