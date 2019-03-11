/**
 * (c) 2017-2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.tasks;


import it.geosolutions.geoserver.rest.GeoServerRESTManager;
import it.geosolutions.geoserver.rest.decoder.RESTLayer;
import it.geosolutions.geoserver.rest.decoder.RESTStyle;
import java.net.MalformedURLException;
import java.sql.SQLException;
import javax.xml.namespace.QName;
import org.geoserver.catalog.LayerInfo;
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
 * To run this test you should have a geoserver running on http://localhost:9090/geoserver +
 * postgres running on localhost with database 'mydb' (or configure in application context),
 * initiated with create-source.sql.
 *
 * @author Niels Charlier
 */
public class DbRemotePublicationTaskTest extends AbstractTaskManagerTest {
    // configure these constants
    // to test with jndi, you need a jndi 'mytargetjndidb' configured in your target geoserver
    // private static final String DB_NAME = "myjndidb";
    // private static final String TABLE_NAME = "grondwaterlichamen_new";
    private static final String DB_NAME = "mypostgresdb";

    private static final String TABLE_NAME = "Grondwaterlichamen_Copy";

    private static final String STYLE = "grass";

    private static final String SECOND_STYLE = "second_grass";

    private static QName MY_TYPE = new QName(DbRemotePublicationTaskTest.DB_NAME, DbRemotePublicationTaskTest.TABLE_NAME, DbRemotePublicationTaskTest.DB_NAME);

    private static final String ATT_LAYER = "layer";

    private static final String ATT_EXT_GS = "geoserver";

    private static final String ATT_FAIL = "fail";

    private static final String ATT_DB_NAME = "dbName";

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
    private Scheduler scheduler;

    @Autowired
    private LookupService<DbSource> dbSources;

    private Configuration config;

    private Batch batch;

    @Test
    public void testSuccessAndCleanup() throws MalformedURLException, SQLException, SchedulerException {
        // set additional style
        LayerInfo li = geoServer.getCatalog().getLayerByName(DbRemotePublicationTaskTest.MY_TYPE.getLocalPart());
        li.getStyles().add(geoServer.getCatalog().getStyleByName(DbRemotePublicationTaskTest.SECOND_STYLE));
        geoServer.getCatalog().save(li);
        dataUtil.setConfigurationAttribute(config, DbRemotePublicationTaskTest.ATT_DB_NAME, DbRemotePublicationTaskTest.DB_NAME);
        dataUtil.setConfigurationAttribute(config, DbRemotePublicationTaskTest.ATT_LAYER, DbRemotePublicationTaskTest.TABLE_NAME);
        dataUtil.setConfigurationAttribute(config, DbRemotePublicationTaskTest.ATT_EXT_GS, "mygs");
        config = dao.save(config);
        Trigger trigger = TriggerBuilder.newTrigger().forJob(batch.getId().toString()).startNow().build();
        scheduler.scheduleJob(trigger);
        while ((scheduler.getTriggerState(trigger.getKey())) != (TriggerState.NONE)) {
        } 
        GeoServerRESTManager restManager = extGeoservers.get("mygs").getRESTManager();
        Assert.assertTrue(restManager.getReader().existsDatastore(DbRemotePublicationTaskTest.DB_NAME, DbRemotePublicationTaskTest.DB_NAME));
        Assert.assertTrue(restManager.getReader().existsFeatureType(DbRemotePublicationTaskTest.DB_NAME, DbRemotePublicationTaskTest.DB_NAME, DbRemotePublicationTaskTest.TABLE_NAME));
        Assert.assertTrue(restManager.getReader().existsLayer(DbRemotePublicationTaskTest.DB_NAME, DbRemotePublicationTaskTest.TABLE_NAME, true));
        // test styles
        RESTLayer layer = restManager.getReader().getLayer(DbRemotePublicationTaskTest.DB_NAME, DbRemotePublicationTaskTest.TABLE_NAME);
        Assert.assertEquals(DbRemotePublicationTaskTest.STYLE, layer.getDefaultStyle());
        Assert.assertEquals(DbRemotePublicationTaskTest.SECOND_STYLE, layer.getStyles().get(0).getName());
        RESTStyle style = restManager.getReader().getStyle(DbRemotePublicationTaskTest.STYLE);
        Assert.assertEquals(((DbRemotePublicationTaskTest.STYLE) + ".sld"), style.getFileName());
        RESTStyle second_style = restManager.getReader().getStyle(DbRemotePublicationTaskTest.SECOND_STYLE);
        Assert.assertEquals(((DbRemotePublicationTaskTest.SECOND_STYLE) + ".sld"), second_style.getFileName());
        Assert.assertTrue(taskUtil.cleanup(config));
        Assert.assertFalse(restManager.getReader().existsLayer(DbRemotePublicationTaskTest.DB_NAME, DbRemotePublicationTaskTest.TABLE_NAME, true));
        Assert.assertFalse(restManager.getReader().existsFeatureType(DbRemotePublicationTaskTest.DB_NAME, DbRemotePublicationTaskTest.DB_NAME, DbRemotePublicationTaskTest.TABLE_NAME));
    }

    @Test
    public void testRollback() throws MalformedURLException, SQLException, SchedulerException {
        Task task2 = fac.createTask();
        task2.setName("task2");
        task2.setType(TestTaskTypeImpl.NAME);
        dataUtil.setTaskParameterToAttribute(task2, TestTaskTypeImpl.PARAM_FAIL, DbRemotePublicationTaskTest.ATT_FAIL);
        dataUtil.addTaskToConfiguration(config, task2);
        dataUtil.setConfigurationAttribute(config, DbRemotePublicationTaskTest.ATT_DB_NAME, DbRemotePublicationTaskTest.DB_NAME);
        dataUtil.setConfigurationAttribute(config, DbRemotePublicationTaskTest.ATT_LAYER, DbRemotePublicationTaskTest.TABLE_NAME);
        dataUtil.setConfigurationAttribute(config, DbRemotePublicationTaskTest.ATT_EXT_GS, "mygs");
        dataUtil.setConfigurationAttribute(config, DbRemotePublicationTaskTest.ATT_FAIL, Boolean.TRUE.toString());
        config = dao.save(config);
        task2 = config.getTasks().get("task2");
        dataUtil.addBatchElement(batch, task2);
        batch = bjService.saveAndSchedule(batch);
        Trigger trigger = TriggerBuilder.newTrigger().forJob(batch.getId().toString()).startNow().build();
        scheduler.scheduleJob(trigger);
        while ((scheduler.getTriggerState(trigger.getKey())) != (TriggerState.NONE)) {
        } 
        GeoServerRESTManager restManager = extGeoservers.get("mygs").getRESTManager();
        Assert.assertFalse(restManager.getReader().existsDatastore(DbRemotePublicationTaskTest.DB_NAME, DbRemotePublicationTaskTest.DB_NAME));
        Assert.assertFalse(restManager.getReader().existsFeatureType(DbRemotePublicationTaskTest.DB_NAME, DbRemotePublicationTaskTest.DB_NAME, DbRemotePublicationTaskTest.TABLE_NAME));
        Assert.assertFalse(restManager.getReader().existsLayer(DbRemotePublicationTaskTest.DB_NAME, DbRemotePublicationTaskTest.TABLE_NAME, true));
    }
}

