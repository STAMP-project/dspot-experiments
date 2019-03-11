/**
 * (c) 2017-2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.tasks;


import DbLocalPublicationTaskTypeImpl.NAME;
import DbLocalPublicationTaskTypeImpl.PARAM_DB_NAME;
import DbLocalPublicationTaskTypeImpl.PARAM_LAYER;
import DbLocalPublicationTaskTypeImpl.PARAM_TABLE_NAME;
import DbRemotePublicationTaskTypeImpl.PARAM_EXT_GS;
import it.geosolutions.geoserver.rest.GeoServerRESTManager;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.SQLException;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.taskmanager.AbstractTaskManagerTest;
import org.geoserver.taskmanager.beans.TestTaskTypeImpl;
import org.geoserver.taskmanager.data.Batch;
import org.geoserver.taskmanager.data.Configuration;
import org.geoserver.taskmanager.data.Task;
import org.geoserver.taskmanager.data.TaskManagerDao;
import org.geoserver.taskmanager.data.TaskManagerFactory;
import org.geoserver.taskmanager.external.DbSource;
import org.geoserver.taskmanager.external.ExternalGS;
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
 * Tests temp values with commit and rollback, in this case of copy table followed by create view.
 * (The create view must use the temp table from the copy table).
 *
 * @author Niels Charlier
 */
public class ConsecutiveTest extends AbstractTaskManagerTest {
    // configure these constants
    private static final String SOURCEDB_NAME = "testsourcedb";

    private static final String TARGETDB_NAME = "testtargetdb";

    private static final String TARGETDB_PUB_NAME = "mypostgresdb";

    private static final String TABLE_NAME = "gw_beleid.grondwaterlichamen_new";

    private static final String VIEW_NAME = "gw_beleid.vw_grondwaterlichamen";

    private static final String SELECT = "\"DATAENGINE_ID\"";

    private static final String WHERE = "\"GWL\" like \'BL%\'";

    private static final String LAYER_NAME = "grondwaterlichamen";

    private static final int NUMBER_OF_RECORDS = 7;

    private static final int NUMBER_OF_COLUMNS = 1;

    // attributes
    private static final String ATT_SOURCE_DB = "source_db";

    private static final String ATT_TARGET_DB = "target_db";

    private static final String ATT_TABLE_NAME = "table_name";

    private static final String ATT_VIEW_NAME = "view_name";

    private static final String ATT_SELECT = "select";

    private static final String ATT_WHERE = "where";

    private static final String ATT_LAYER = "layer";

    private static final String ATT_FAIL = "fail";

    private static final String ATT_EXT_GS = "ext_gs";

    @Autowired
    private LookupService<ExternalGS> extGeoservers;

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

    @Autowired
    private Catalog catalog;

    private Configuration config;

    private Batch batch;

    @Test
    public void testSuccessAndCleanup() throws SQLException, SchedulerException {
        dataUtil.setConfigurationAttribute(config, ConsecutiveTest.ATT_SOURCE_DB, ConsecutiveTest.SOURCEDB_NAME);
        dataUtil.setConfigurationAttribute(config, ConsecutiveTest.ATT_TARGET_DB, ConsecutiveTest.TARGETDB_NAME);
        dataUtil.setConfigurationAttribute(config, ConsecutiveTest.ATT_TABLE_NAME, ConsecutiveTest.TABLE_NAME);
        dataUtil.setConfigurationAttribute(config, ConsecutiveTest.ATT_VIEW_NAME, ConsecutiveTest.VIEW_NAME);
        dataUtil.setConfigurationAttribute(config, ConsecutiveTest.ATT_SELECT, ConsecutiveTest.SELECT);
        dataUtil.setConfigurationAttribute(config, ConsecutiveTest.ATT_WHERE, ConsecutiveTest.WHERE);
        config = dao.save(config);
        Trigger trigger = TriggerBuilder.newTrigger().forJob(batch.getId().toString()).startNow().build();
        scheduler.scheduleJob(trigger);
        while ((scheduler.getTriggerState(trigger.getKey())) != (TriggerState.NONE)) {
        } 
        Assert.assertFalse(viewOrTableExists(SqlUtil.schema(ConsecutiveTest.VIEW_NAME), "_temp%"));
        Assert.assertTrue(viewOrTableExists(SqlUtil.schema(ConsecutiveTest.TABLE_NAME), SqlUtil.notQualified(ConsecutiveTest.TABLE_NAME)));
        Assert.assertTrue(viewOrTableExists(SqlUtil.schema(ConsecutiveTest.VIEW_NAME), SqlUtil.notQualified(ConsecutiveTest.VIEW_NAME)));
        Assert.assertEquals(ConsecutiveTest.NUMBER_OF_RECORDS, getNumberOfRecords(ConsecutiveTest.VIEW_NAME));
        Assert.assertEquals(ConsecutiveTest.NUMBER_OF_COLUMNS, getNumberOfColumns(ConsecutiveTest.VIEW_NAME));
        Assert.assertTrue(taskUtil.cleanup(config));
        Assert.assertFalse(viewOrTableExists(SqlUtil.schema(ConsecutiveTest.TABLE_NAME), SqlUtil.notQualified(ConsecutiveTest.TABLE_NAME)));
        Assert.assertFalse(viewOrTableExists(SqlUtil.schema(ConsecutiveTest.VIEW_NAME), SqlUtil.notQualified(ConsecutiveTest.VIEW_NAME)));
    }

    @Test
    public void testRollback() throws SQLException, SchedulerException {
        Task task3 = fac.createTask();
        task3.setName("task3");
        task3.setType(TestTaskTypeImpl.NAME);
        dataUtil.setTaskParameterToAttribute(task3, TestTaskTypeImpl.PARAM_FAIL, ConsecutiveTest.ATT_FAIL);
        dataUtil.addTaskToConfiguration(config, task3);
        dataUtil.setConfigurationAttribute(config, ConsecutiveTest.ATT_SOURCE_DB, ConsecutiveTest.SOURCEDB_NAME);
        dataUtil.setConfigurationAttribute(config, ConsecutiveTest.ATT_TARGET_DB, ConsecutiveTest.TARGETDB_NAME);
        dataUtil.setConfigurationAttribute(config, ConsecutiveTest.ATT_TABLE_NAME, ConsecutiveTest.TABLE_NAME);
        dataUtil.setConfigurationAttribute(config, ConsecutiveTest.ATT_VIEW_NAME, ConsecutiveTest.VIEW_NAME);
        dataUtil.setConfigurationAttribute(config, ConsecutiveTest.ATT_SELECT, "*");
        dataUtil.setConfigurationAttribute(config, ConsecutiveTest.ATT_FAIL, Boolean.TRUE.toString());
        config = dao.save(config);
        task3 = config.getTasks().get("task3");
        dataUtil.addBatchElement(batch, task3);
        batch = bjService.saveAndSchedule(batch);
        Trigger trigger = TriggerBuilder.newTrigger().forJob(batch.getId().toString()).startNow().build();
        scheduler.scheduleJob(trigger);
        while ((scheduler.getTriggerState(trigger.getKey())) != (TriggerState.NONE)) {
        } 
        Assert.assertFalse(viewOrTableExists(SqlUtil.schema(ConsecutiveTest.TABLE_NAME), SqlUtil.notQualified(ConsecutiveTest.TABLE_NAME)));
        Assert.assertFalse(viewOrTableExists(SqlUtil.schema(ConsecutiveTest.VIEW_NAME), SqlUtil.notQualified(ConsecutiveTest.VIEW_NAME)));
    }

    @Test
    public void testPublishSuccessAndCleanup() throws SQLException, SchedulerException {
        try (Connection conn = dbSources.get(ConsecutiveTest.TARGETDB_PUB_NAME).getDataSource().getConnection()) {
        } catch (SQLException e) {
            Assume.assumeTrue(false);
        }
        Task task3 = fac.createTask();
        task3.setName("task3");
        task3.setType(NAME);
        dataUtil.setTaskParameterToAttribute(task3, PARAM_DB_NAME, ConsecutiveTest.ATT_TARGET_DB);
        dataUtil.setTaskParameterToAttribute(task3, PARAM_TABLE_NAME, ConsecutiveTest.ATT_VIEW_NAME);
        dataUtil.setTaskParameterToAttribute(task3, PARAM_LAYER, ConsecutiveTest.ATT_LAYER);
        dataUtil.addTaskToConfiguration(config, task3);
        dataUtil.setConfigurationAttribute(config, ConsecutiveTest.ATT_SOURCE_DB, ConsecutiveTest.SOURCEDB_NAME);
        dataUtil.setConfigurationAttribute(config, ConsecutiveTest.ATT_TARGET_DB, ConsecutiveTest.TARGETDB_PUB_NAME);
        dataUtil.setConfigurationAttribute(config, ConsecutiveTest.ATT_TABLE_NAME, ConsecutiveTest.TABLE_NAME);
        dataUtil.setConfigurationAttribute(config, ConsecutiveTest.ATT_VIEW_NAME, ConsecutiveTest.VIEW_NAME);
        dataUtil.setConfigurationAttribute(config, ConsecutiveTest.ATT_SELECT, ConsecutiveTest.SELECT);
        dataUtil.setConfigurationAttribute(config, ConsecutiveTest.ATT_WHERE, ConsecutiveTest.WHERE);
        dataUtil.setConfigurationAttribute(config, ConsecutiveTest.ATT_LAYER, ConsecutiveTest.LAYER_NAME);
        config = dao.save(config);
        task3 = config.getTasks().get("task3");
        dataUtil.addBatchElement(batch, task3);
        batch = bjService.saveAndSchedule(batch);
        Trigger trigger = TriggerBuilder.newTrigger().forJob(batch.getId().toString()).startNow().build();
        scheduler.scheduleJob(trigger);
        while ((scheduler.getTriggerState(trigger.getKey())) != (TriggerState.NONE)) {
        } 
        Assert.assertFalse(viewOrTableExists(ConsecutiveTest.TARGETDB_PUB_NAME, SqlUtil.schema(ConsecutiveTest.VIEW_NAME), "_temp%"));
        Assert.assertTrue(viewOrTableExists(ConsecutiveTest.TARGETDB_PUB_NAME, SqlUtil.schema(ConsecutiveTest.TABLE_NAME), SqlUtil.notQualified(ConsecutiveTest.TABLE_NAME)));
        Assert.assertTrue(viewOrTableExists(ConsecutiveTest.TARGETDB_PUB_NAME, SqlUtil.schema(ConsecutiveTest.VIEW_NAME), SqlUtil.notQualified(ConsecutiveTest.VIEW_NAME)));
        Assert.assertEquals(ConsecutiveTest.NUMBER_OF_RECORDS, getNumberOfRecords(ConsecutiveTest.TARGETDB_PUB_NAME, ConsecutiveTest.VIEW_NAME));
        Assert.assertEquals(ConsecutiveTest.NUMBER_OF_COLUMNS, getNumberOfColumns(ConsecutiveTest.TARGETDB_PUB_NAME, ConsecutiveTest.VIEW_NAME));
        Assert.assertNotNull(catalog.getLayerByName(ConsecutiveTest.LAYER_NAME));
        FeatureTypeInfo fti = catalog.getResourceByName(ConsecutiveTest.LAYER_NAME, FeatureTypeInfo.class);
        Assert.assertNotNull(fti);
        Assert.assertEquals(SqlUtil.notQualified(ConsecutiveTest.VIEW_NAME), fti.getNativeName());
        Assert.assertTrue(taskUtil.cleanup(config));
        Assert.assertFalse(viewOrTableExists(ConsecutiveTest.TARGETDB_PUB_NAME, SqlUtil.schema(ConsecutiveTest.TABLE_NAME), SqlUtil.notQualified(ConsecutiveTest.TABLE_NAME)));
        Assert.assertFalse(viewOrTableExists(ConsecutiveTest.TARGETDB_PUB_NAME, SqlUtil.schema(ConsecutiveTest.VIEW_NAME), SqlUtil.notQualified(ConsecutiveTest.VIEW_NAME)));
        Assert.assertNull(catalog.getLayerByName(ConsecutiveTest.LAYER_NAME));
        Assert.assertNull(catalog.getResourceByName(ConsecutiveTest.LAYER_NAME, FeatureTypeInfo.class));
    }

    @Test
    public void testRemotePublishSuccessAndCleanup() throws MalformedURLException, SQLException, SchedulerException {
        Assume.assumeTrue(extGeoservers.get("mygs").getRESTManager().getReader().existGeoserver());
        try (Connection conn = dbSources.get(ConsecutiveTest.TARGETDB_PUB_NAME).getDataSource().getConnection()) {
        } catch (SQLException e) {
            Assume.assumeTrue(false);
        }
        Task task3 = fac.createTask();
        task3.setName("task3");
        task3.setType(NAME);
        dataUtil.setTaskParameterToAttribute(task3, PARAM_DB_NAME, ConsecutiveTest.ATT_TARGET_DB);
        dataUtil.setTaskParameterToAttribute(task3, PARAM_TABLE_NAME, ConsecutiveTest.ATT_VIEW_NAME);
        dataUtil.setTaskParameterToAttribute(task3, PARAM_LAYER, ConsecutiveTest.ATT_LAYER);
        dataUtil.addTaskToConfiguration(config, task3);
        Task task4 = fac.createTask();
        task4.setName("task4");
        task4.setType(DbRemotePublicationTaskTypeImpl.NAME);
        dataUtil.setTaskParameterToAttribute(task4, PARAM_EXT_GS, ConsecutiveTest.ATT_EXT_GS);
        dataUtil.setTaskParameterToAttribute(task4, DbRemotePublicationTaskTypeImpl.PARAM_DB_NAME, ConsecutiveTest.ATT_TARGET_DB);
        dataUtil.setTaskParameterToAttribute(task4, DbRemotePublicationTaskTypeImpl.PARAM_TABLE_NAME, ConsecutiveTest.ATT_VIEW_NAME);
        dataUtil.setTaskParameterToAttribute(task4, DbRemotePublicationTaskTypeImpl.PARAM_LAYER, ConsecutiveTest.ATT_LAYER);
        dataUtil.addTaskToConfiguration(config, task4);
        dataUtil.setConfigurationAttribute(config, ConsecutiveTest.ATT_SOURCE_DB, ConsecutiveTest.SOURCEDB_NAME);
        dataUtil.setConfigurationAttribute(config, ConsecutiveTest.ATT_TARGET_DB, ConsecutiveTest.TARGETDB_PUB_NAME);
        dataUtil.setConfigurationAttribute(config, ConsecutiveTest.ATT_TABLE_NAME, ConsecutiveTest.TABLE_NAME);
        dataUtil.setConfigurationAttribute(config, ConsecutiveTest.ATT_VIEW_NAME, ConsecutiveTest.VIEW_NAME);
        dataUtil.setConfigurationAttribute(config, ConsecutiveTest.ATT_SELECT, ConsecutiveTest.SELECT);
        dataUtil.setConfigurationAttribute(config, ConsecutiveTest.ATT_WHERE, ConsecutiveTest.WHERE);
        dataUtil.setConfigurationAttribute(config, ConsecutiveTest.ATT_LAYER, ConsecutiveTest.LAYER_NAME);
        dataUtil.setConfigurationAttribute(config, ConsecutiveTest.ATT_EXT_GS, "mygs");
        config = dao.save(config);
        task3 = config.getTasks().get("task3");
        dataUtil.addBatchElement(batch, task3);
        task4 = config.getTasks().get("task4");
        dataUtil.addBatchElement(batch, task4);
        batch = bjService.saveAndSchedule(batch);
        Trigger trigger = TriggerBuilder.newTrigger().forJob(batch.getId().toString()).startNow().build();
        scheduler.scheduleJob(trigger);
        while ((scheduler.getTriggerState(trigger.getKey())) != (TriggerState.NONE)) {
        } 
        Assert.assertFalse(viewOrTableExists(ConsecutiveTest.TARGETDB_PUB_NAME, SqlUtil.schema(ConsecutiveTest.VIEW_NAME), "_temp%"));
        Assert.assertTrue(viewOrTableExists(ConsecutiveTest.TARGETDB_PUB_NAME, SqlUtil.schema(ConsecutiveTest.TABLE_NAME), SqlUtil.notQualified(ConsecutiveTest.TABLE_NAME)));
        Assert.assertTrue(viewOrTableExists(ConsecutiveTest.TARGETDB_PUB_NAME, SqlUtil.schema(ConsecutiveTest.VIEW_NAME), SqlUtil.notQualified(ConsecutiveTest.VIEW_NAME)));
        Assert.assertEquals(ConsecutiveTest.NUMBER_OF_RECORDS, getNumberOfRecords(ConsecutiveTest.TARGETDB_PUB_NAME, ConsecutiveTest.VIEW_NAME));
        Assert.assertEquals(ConsecutiveTest.NUMBER_OF_COLUMNS, getNumberOfColumns(ConsecutiveTest.TARGETDB_PUB_NAME, ConsecutiveTest.VIEW_NAME));
        Assert.assertNotNull(catalog.getLayerByName(ConsecutiveTest.LAYER_NAME));
        FeatureTypeInfo fti = catalog.getResourceByName(ConsecutiveTest.LAYER_NAME, FeatureTypeInfo.class);
        Assert.assertNotNull(fti);
        Assert.assertEquals(SqlUtil.notQualified(ConsecutiveTest.VIEW_NAME), fti.getNativeName());
        GeoServerRESTManager restManager = extGeoservers.get("mygs").getRESTManager();
        Assert.assertTrue(restManager.getReader().existsLayer("gs", ConsecutiveTest.LAYER_NAME, true));
        Assert.assertEquals(SqlUtil.notQualified(ConsecutiveTest.VIEW_NAME), restManager.getReader().getFeatureType(restManager.getReader().getLayer("gs", ConsecutiveTest.LAYER_NAME)).getNativeName());
        // cleanup
        Assert.assertTrue(taskUtil.cleanup(config));
        Assert.assertFalse(viewOrTableExists(ConsecutiveTest.TARGETDB_PUB_NAME, SqlUtil.schema(ConsecutiveTest.TABLE_NAME), SqlUtil.notQualified(ConsecutiveTest.TABLE_NAME)));
        Assert.assertFalse(viewOrTableExists(ConsecutiveTest.TARGETDB_PUB_NAME, SqlUtil.schema(ConsecutiveTest.VIEW_NAME), SqlUtil.notQualified(ConsecutiveTest.VIEW_NAME)));
        Assert.assertNull(catalog.getLayerByName(ConsecutiveTest.LAYER_NAME));
        Assert.assertNull(catalog.getResourceByName(ConsecutiveTest.LAYER_NAME, FeatureTypeInfo.class));
        Assert.assertFalse(restManager.getReader().existsLayer("gs", ConsecutiveTest.LAYER_NAME, true));
    }
}

