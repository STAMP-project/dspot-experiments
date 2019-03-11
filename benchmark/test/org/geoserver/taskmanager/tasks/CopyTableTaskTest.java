/**
 * (c) 2017-2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.tasks;


import java.sql.Connection;
import java.sql.ResultSet;
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
import org.junit.Assume;
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
public class CopyTableTaskTest extends AbstractTaskManagerTest {
    // configure these constants
    private static final String SOURCEDB_NAME = "testsourcedb";

    private static final String TARGETDB_NAME = "testtargetdb";

    private static final String SOURCEDB_PG_NAME = "myjndidb";

    private static final String TARGETDB_PG_NAME = "mypostgresdb";

    private static final String TABLE_NAME = "gw_beleid.grondwaterlichamen_new";

    private static final String TARGET_TABLE_NAME = "temp.grondwaterlichamen_copy";

    private static final String VIEW_NAME = "gw_beleid.vw_grondwaterlichamen";

    private static final String VIEW_W_GENERATED_ID = "gw_beleid.vw_grondwaterlichamen_generated_id";

    private static final String VIEW_CAMEL_CASE = "gw_beleid.vw_GrondwaterlichamenCamelCase";

    private static final String TARGET_TABLE_CAMELCASE_NAME = "temp.Grondwaterlichamen_Copy";

    private static final String TARGET_TABLE_FROM_VIEW_NAME = "temp.grondwaterlichamen_vw_copy";

    private static final String TARGET_TABLE_NAME_NEW_SCHEMA = "foobar.grondwaterlichamen_copy";

    // attributes
    private static final String ATT_TABLE_NAME = "table_name";

    private static final String ATT_TARGET_DB = "target_db";

    private static final String ATT_SOURCE_DB = "source_db";

    private static final String ATT_TARGET_TABLE_NAME = "target_table_name";

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
    public void testTableSuccess() throws SQLException, SchedulerException {
        dataUtil.setConfigurationAttribute(config, CopyTableTaskTest.ATT_SOURCE_DB, CopyTableTaskTest.SOURCEDB_NAME);
        dataUtil.setConfigurationAttribute(config, CopyTableTaskTest.ATT_TARGET_DB, CopyTableTaskTest.TARGETDB_NAME);
        dataUtil.setConfigurationAttribute(config, CopyTableTaskTest.ATT_TABLE_NAME, CopyTableTaskTest.TABLE_NAME);
        dataUtil.setConfigurationAttribute(config, CopyTableTaskTest.ATT_TARGET_TABLE_NAME, CopyTableTaskTest.TARGET_TABLE_NAME);
        config = dao.save(config);
        Trigger trigger = TriggerBuilder.newTrigger().forJob(batch.getId().toString()).startNow().build();
        scheduler.scheduleJob(trigger);
        while ((scheduler.getTriggerState(trigger.getKey())) != (TriggerState.NONE)) {
        } 
        String[] splitTargetTableName = CopyTableTaskTest.TARGET_TABLE_NAME.split("\\.", 2);
        if ((splitTargetTableName.length) == 2) {
            Assert.assertFalse(tableExists(CopyTableTaskTest.TARGETDB_NAME, splitTargetTableName[0], "_temp%"));
            Assert.assertTrue(tableExists(CopyTableTaskTest.TARGETDB_NAME, splitTargetTableName[0], splitTargetTableName[1]));
        } else {
            Assert.assertFalse(tableExists(CopyTableTaskTest.TARGETDB_NAME, null, "_temp%"));
            Assert.assertTrue(tableExists(CopyTableTaskTest.TARGETDB_NAME, null, CopyTableTaskTest.TARGET_TABLE_NAME));
        }
        Assert.assertEquals(getNumberOfRecords(CopyTableTaskTest.SOURCEDB_NAME, CopyTableTaskTest.TABLE_NAME), getNumberOfRecords(CopyTableTaskTest.TARGETDB_NAME, CopyTableTaskTest.TARGET_TABLE_NAME));
        Assert.assertEquals(getNumberOfColumns(CopyTableTaskTest.SOURCEDB_NAME, CopyTableTaskTest.TABLE_NAME), getNumberOfColumns(CopyTableTaskTest.TARGETDB_NAME, CopyTableTaskTest.TARGET_TABLE_NAME));
        String[] splitTableName = CopyTableTaskTest.TABLE_NAME.split("\\.", 2);
        int numberOfindexesSource = getNumberOfIndexes(CopyTableTaskTest.SOURCEDB_NAME, splitTableName[1]);
        int numberOfindexesTarget = getNumberOfIndexes(CopyTableTaskTest.TARGETDB_NAME, splitTargetTableName[1]);
        Assert.assertEquals(numberOfindexesSource, numberOfindexesTarget);
        Assert.assertTrue(taskUtil.cleanup(config));
        if ((splitTargetTableName.length) == 2) {
            Assert.assertFalse(tableExists(CopyTableTaskTest.TARGETDB_NAME, splitTargetTableName[0], splitTargetTableName[1]));
        } else {
            Assert.assertFalse(tableExists(CopyTableTaskTest.TARGETDB_NAME, null, CopyTableTaskTest.TARGET_TABLE_NAME));
        }
    }

    @Test
    public void testCopyFromViewSuccess() throws SQLException, SchedulerException {
        dataUtil.setConfigurationAttribute(config, CopyTableTaskTest.ATT_SOURCE_DB, CopyTableTaskTest.SOURCEDB_NAME);
        dataUtil.setConfigurationAttribute(config, CopyTableTaskTest.ATT_TARGET_DB, CopyTableTaskTest.TARGETDB_NAME);
        dataUtil.setConfigurationAttribute(config, CopyTableTaskTest.ATT_TABLE_NAME, CopyTableTaskTest.VIEW_NAME);
        dataUtil.setConfigurationAttribute(config, CopyTableTaskTest.ATT_TARGET_TABLE_NAME, CopyTableTaskTest.TARGET_TABLE_FROM_VIEW_NAME);
        config = dao.save(config);
        Trigger trigger = TriggerBuilder.newTrigger().forJob(batch.getId().toString()).startNow().build();
        scheduler.scheduleJob(trigger);
        while ((scheduler.getTriggerState(trigger.getKey())) != (TriggerState.NONE)) {
            // waiting to be done.
        } 
        String[] split = CopyTableTaskTest.TARGET_TABLE_FROM_VIEW_NAME.split("\\.", 2);
        if ((split.length) == 2) {
            Assert.assertFalse(tableExists(CopyTableTaskTest.TARGETDB_NAME, split[0], "_temp%"));
            Assert.assertTrue(tableExists(CopyTableTaskTest.TARGETDB_NAME, split[0], split[1]));
        } else {
            Assert.assertFalse(tableExists(CopyTableTaskTest.TARGETDB_NAME, null, "_temp%"));
            Assert.assertTrue(tableExists(CopyTableTaskTest.TARGETDB_NAME, null, CopyTableTaskTest.TARGET_TABLE_FROM_VIEW_NAME));
        }
        int numberOfRecordsSource = getNumberOfRecords(CopyTableTaskTest.SOURCEDB_NAME, CopyTableTaskTest.VIEW_NAME);
        int numberOfRecordsTarget = getNumberOfRecords(CopyTableTaskTest.TARGETDB_NAME, CopyTableTaskTest.TARGET_TABLE_FROM_VIEW_NAME);
        Assert.assertEquals(numberOfRecordsSource, numberOfRecordsTarget);
        // a primary key column was added
        Assert.assertEquals(((getNumberOfColumns(CopyTableTaskTest.SOURCEDB_NAME, CopyTableTaskTest.VIEW_NAME)) + 1), getNumberOfColumns(CopyTableTaskTest.TARGETDB_NAME, CopyTableTaskTest.TARGET_TABLE_FROM_VIEW_NAME));
        int numberOfindexesTarget = getNumberOfIndexes(CopyTableTaskTest.TARGETDB_NAME, split[1]);
        int numberOfColumnsTarget = getNumberOfColumns(CopyTableTaskTest.TARGETDB_NAME, CopyTableTaskTest.TARGET_TABLE_FROM_VIEW_NAME);
        // We did not add Geometry support on the H2 DB. So only check the index on the shape
        if (numberOfColumnsTarget == 5) {
            Assert.assertEquals(2, numberOfindexesTarget);
            Assert.assertEquals("shape", getColumnName(CopyTableTaskTest.TARGETDB_NAME, CopyTableTaskTest.TARGET_TABLE_FROM_VIEW_NAME, 3));
        } else {
            Assert.assertEquals(1, numberOfindexesTarget);
        }
        Assert.assertTrue(taskUtil.cleanup(config));
        if ((split.length) == 2) {
            Assert.assertFalse(tableExists(CopyTableTaskTest.TARGETDB_NAME, split[0], split[1]));
        } else {
            Assert.assertFalse(tableExists(CopyTableTaskTest.TARGETDB_NAME, null, CopyTableTaskTest.TARGET_TABLE_NAME));
        }
    }

    /**
     * Use the existing generated_id column if it exists.
     *
     * @throws SchedulerException
     * 		
     * @throws SQLException
     * 		
     */
    @Test
    public void testCopyViewWithGeneratedIdColumn() throws SQLException, SchedulerException {
        dataUtil.setConfigurationAttribute(config, CopyTableTaskTest.ATT_SOURCE_DB, CopyTableTaskTest.SOURCEDB_NAME);
        dataUtil.setConfigurationAttribute(config, CopyTableTaskTest.ATT_TARGET_DB, CopyTableTaskTest.TARGETDB_NAME);
        dataUtil.setConfigurationAttribute(config, CopyTableTaskTest.ATT_TABLE_NAME, CopyTableTaskTest.VIEW_W_GENERATED_ID);
        dataUtil.setConfigurationAttribute(config, CopyTableTaskTest.ATT_TARGET_TABLE_NAME, CopyTableTaskTest.TARGET_TABLE_FROM_VIEW_NAME);
        config = dao.save(config);
        Trigger trigger = TriggerBuilder.newTrigger().forJob(batch.getId().toString()).startNow().build();
        scheduler.scheduleJob(trigger);
        while ((scheduler.getTriggerState(trigger.getKey())) != (TriggerState.NONE)) {
            // waiting to be done.
        } 
        String[] split = CopyTableTaskTest.TARGET_TABLE_FROM_VIEW_NAME.split("\\.", 2);
        if ((split.length) == 2) {
            Assert.assertFalse(tableExists(CopyTableTaskTest.TARGETDB_NAME, split[0], "_temp%"));
            Assert.assertTrue(tableExists(CopyTableTaskTest.TARGETDB_NAME, split[0], split[1]));
        } else {
            Assert.assertFalse(tableExists(CopyTableTaskTest.TARGETDB_NAME, null, "_temp%"));
            Assert.assertTrue(tableExists(CopyTableTaskTest.TARGETDB_NAME, null, CopyTableTaskTest.TARGET_TABLE_FROM_VIEW_NAME));
        }
        Assert.assertEquals(getNumberOfRecords(CopyTableTaskTest.SOURCEDB_NAME, CopyTableTaskTest.VIEW_NAME), getNumberOfRecords(CopyTableTaskTest.TARGETDB_NAME, CopyTableTaskTest.TARGET_TABLE_FROM_VIEW_NAME));
        // a primary key column was not added
        Assert.assertEquals(getNumberOfColumns(CopyTableTaskTest.SOURCEDB_NAME, CopyTableTaskTest.VIEW_NAME), getNumberOfColumns(CopyTableTaskTest.TARGETDB_NAME, CopyTableTaskTest.TARGET_TABLE_FROM_VIEW_NAME));
        Assert.assertTrue(taskUtil.cleanup(config));
        if ((split.length) == 2) {
            Assert.assertFalse(tableExists(CopyTableTaskTest.TARGETDB_NAME, split[0], split[1]));
        } else {
            Assert.assertFalse(tableExists(CopyTableTaskTest.TARGETDB_NAME, null, CopyTableTaskTest.TARGET_TABLE_NAME));
        }
    }

    /**
     * keep the case of columns.
     */
    @Test
    public void testCopyKeepCase() throws SQLException, SchedulerException {
        DbSource source = dbSources.get(CopyTableTaskTest.SOURCEDB_PG_NAME);
        try (Connection conn = source.getDataSource().getConnection()) {
            try (ResultSet res = conn.getMetaData().getTables(null, SqlUtil.schema(CopyTableTaskTest.VIEW_CAMEL_CASE), SqlUtil.notQualified(CopyTableTaskTest.VIEW_CAMEL_CASE), null)) {
                Assume.assumeTrue(res.next());
            }
        } catch (SQLException e) {
            Assume.assumeTrue(false);
        }
        DbSource target = dbSources.get(CopyTableTaskTest.TARGETDB_PG_NAME);
        try (Connection conn = target.getDataSource().getConnection()) {
        } catch (SQLException e) {
            Assume.assumeTrue(false);
        }
        dataUtil.setConfigurationAttribute(config, CopyTableTaskTest.ATT_SOURCE_DB, CopyTableTaskTest.SOURCEDB_PG_NAME);
        dataUtil.setConfigurationAttribute(config, CopyTableTaskTest.ATT_TARGET_DB, CopyTableTaskTest.TARGETDB_PG_NAME);
        dataUtil.setConfigurationAttribute(config, CopyTableTaskTest.ATT_TABLE_NAME, CopyTableTaskTest.VIEW_CAMEL_CASE);
        dataUtil.setConfigurationAttribute(config, CopyTableTaskTest.ATT_TARGET_TABLE_NAME, CopyTableTaskTest.TARGET_TABLE_CAMELCASE_NAME);
        config = dao.save(config);
        Trigger trigger = TriggerBuilder.newTrigger().forJob(batch.getId().toString()).startNow().build();
        scheduler.scheduleJob(trigger);
        while ((scheduler.getTriggerState(trigger.getKey())) != (TriggerState.NONE)) {
            // waiting to be done.
        } 
        String[] split = CopyTableTaskTest.TARGET_TABLE_CAMELCASE_NAME.split("\\.", 2);
        if ((split.length) == 2) {
            Assert.assertFalse(tableExists(CopyTableTaskTest.TARGETDB_PG_NAME, split[0], "_temp%"));
            Assert.assertTrue(tableExists(CopyTableTaskTest.TARGETDB_PG_NAME, split[0], split[1]));
        } else {
            Assert.assertFalse(tableExists(CopyTableTaskTest.TARGETDB_PG_NAME, null, "_temp%"));
            Assert.assertTrue(tableExists(CopyTableTaskTest.TARGETDB_PG_NAME, null, CopyTableTaskTest.TARGET_TABLE_CAMELCASE_NAME));
        }
        // column names are the same
        Assert.assertEquals(getColumnName(CopyTableTaskTest.SOURCEDB_PG_NAME, CopyTableTaskTest.VIEW_CAMEL_CASE, 1), getColumnName(CopyTableTaskTest.TARGETDB_PG_NAME, CopyTableTaskTest.TARGET_TABLE_CAMELCASE_NAME, 1));
        Assert.assertEquals(getColumnName(CopyTableTaskTest.SOURCEDB_PG_NAME, CopyTableTaskTest.VIEW_CAMEL_CASE, 2), getColumnName(CopyTableTaskTest.TARGETDB_PG_NAME, CopyTableTaskTest.TARGET_TABLE_CAMELCASE_NAME, 2));
        Assert.assertTrue(taskUtil.cleanup(config));
        if ((split.length) == 2) {
            Assert.assertFalse(tableExists(CopyTableTaskTest.TARGETDB_PG_NAME, split[0], split[1]));
        } else {
            Assert.assertFalse(tableExists(CopyTableTaskTest.TARGETDB_PG_NAME, null, CopyTableTaskTest.TARGET_TABLE_NAME));
        }
    }

    @Test
    public void testRollback() throws SQLException, SchedulerException {
        Task task2 = fac.createTask();
        task2.setName("task2");
        task2.setType(TestTaskTypeImpl.NAME);
        dataUtil.setTaskParameterToAttribute(task2, TestTaskTypeImpl.PARAM_FAIL, "fail");
        dataUtil.addTaskToConfiguration(config, task2);
        dataUtil.setConfigurationAttribute(config, CopyTableTaskTest.ATT_SOURCE_DB, CopyTableTaskTest.SOURCEDB_NAME);
        dataUtil.setConfigurationAttribute(config, CopyTableTaskTest.ATT_TARGET_DB, CopyTableTaskTest.TARGETDB_NAME);
        dataUtil.setConfigurationAttribute(config, CopyTableTaskTest.ATT_TABLE_NAME, CopyTableTaskTest.TABLE_NAME);
        dataUtil.setConfigurationAttribute(config, CopyTableTaskTest.ATT_TARGET_TABLE_NAME, CopyTableTaskTest.TARGET_TABLE_NAME);
        dataUtil.setConfigurationAttribute(config, "fail", "true");
        config = dao.save(config);
        task2 = config.getTasks().get("task2");
        dataUtil.addBatchElement(batch, task2);
        batch = bjService.saveAndSchedule(batch);
        Trigger trigger = TriggerBuilder.newTrigger().forJob(batch.getId().toString()).startNow().build();
        scheduler.scheduleJob(trigger);
        while ((scheduler.getTriggerState(trigger.getKey())) != (TriggerState.NONE)) {
        } 
        Assert.assertFalse(tableExists(CopyTableTaskTest.TARGETDB_NAME, SqlUtil.schema(CopyTableTaskTest.TARGET_TABLE_NAME), "_temp%"));
        Assert.assertFalse(tableExists(CopyTableTaskTest.TARGETDB_NAME, SqlUtil.schema(CopyTableTaskTest.TARGET_TABLE_NAME), SqlUtil.notQualified(CopyTableTaskTest.TARGET_TABLE_NAME)));
    }

    /**
     * the copy task should create the schema if it doesn't exist.
     *
     * @throws SchedulerException
     * 		
     * @throws SQLException
     * 		
     */
    @Test
    public void testTableInNewSchema() throws SQLException, SchedulerException {
        dataUtil.setConfigurationAttribute(config, CopyTableTaskTest.ATT_SOURCE_DB, CopyTableTaskTest.SOURCEDB_NAME);
        dataUtil.setConfigurationAttribute(config, CopyTableTaskTest.ATT_TARGET_DB, CopyTableTaskTest.TARGETDB_NAME);
        dataUtil.setConfigurationAttribute(config, CopyTableTaskTest.ATT_TABLE_NAME, CopyTableTaskTest.TABLE_NAME);
        dataUtil.setConfigurationAttribute(config, CopyTableTaskTest.ATT_TARGET_TABLE_NAME, CopyTableTaskTest.TARGET_TABLE_NAME_NEW_SCHEMA);
        config = dao.save(config);
        Trigger trigger = TriggerBuilder.newTrigger().forJob(batch.getId().toString()).startNow().build();
        scheduler.scheduleJob(trigger);
        while ((scheduler.getTriggerState(trigger.getKey())) != (TriggerState.NONE)) {
        } 
        String[] splitTargetTableName = CopyTableTaskTest.TARGET_TABLE_NAME_NEW_SCHEMA.split("\\.", 2);
        if ((splitTargetTableName.length) == 2) {
            Assert.assertFalse(tableExists(CopyTableTaskTest.TARGETDB_NAME, splitTargetTableName[0], "_temp%"));
            Assert.assertTrue(tableExists(CopyTableTaskTest.TARGETDB_NAME, splitTargetTableName[0], splitTargetTableName[1]));
        } else {
            Assert.assertFalse(tableExists(CopyTableTaskTest.TARGETDB_NAME, null, "_temp%"));
            Assert.assertTrue(tableExists(CopyTableTaskTest.TARGETDB_NAME, null, CopyTableTaskTest.TARGET_TABLE_NAME_NEW_SCHEMA));
        }
        Assert.assertTrue(taskUtil.cleanup(config));
        if ((splitTargetTableName.length) == 2) {
            Assert.assertFalse(tableExists(CopyTableTaskTest.TARGETDB_NAME, splitTargetTableName[0], splitTargetTableName[1]));
        } else {
            Assert.assertFalse(tableExists(CopyTableTaskTest.TARGETDB_NAME, null, CopyTableTaskTest.TARGET_TABLE_NAME_NEW_SCHEMA));
        }
    }
}

