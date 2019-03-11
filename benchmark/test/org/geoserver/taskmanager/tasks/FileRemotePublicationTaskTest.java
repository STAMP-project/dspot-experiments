/**
 * (c) 2017-2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.tasks;


import it.geosolutions.geoserver.rest.GeoServerRESTManager;
import it.geosolutions.geoserver.rest.decoder.RESTCoverage;
import java.net.MalformedURLException;
import java.sql.SQLException;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.catalog.Keyword;
import org.geoserver.taskmanager.AbstractTaskManagerTest;
import org.geoserver.taskmanager.beans.TestTaskTypeImpl;
import org.geoserver.taskmanager.data.Batch;
import org.geoserver.taskmanager.data.Configuration;
import org.geoserver.taskmanager.data.Task;
import org.geoserver.taskmanager.data.TaskManagerDao;
import org.geoserver.taskmanager.data.TaskManagerFactory;
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
 * To run this test you should have a geoserver running on http://localhost:9090/geoserver.
 *
 * @author Niels Charlier
 */
public class FileRemotePublicationTaskTest extends AbstractTaskManagerTest {
    private static final String ATT_LAYER = "layer";

    private static final String ATT_EXT_GS = "geoserver";

    private static final String ATT_FAIL = "fail";

    private static final String ATT_FILE = "file";

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

    private Configuration config;

    private Batch batch;

    @Test
    public void testSuccessAndCleanup() throws MalformedURLException, SQLException, SchedulerException {
        // set some metadata
        CoverageInfo ci = geoServer.getCatalog().getCoverageByName("DEM");
        ci.setName("mydem");
        ci.setTitle("my title ?");
        ci.setAbstract("my abstract ?");
        ci.getDimensions().get(0).setName("CUSTOM_DIMENSION");
        ci.getKeywords().add(new Keyword("demmiedem"));
        geoServer.getCatalog().save(ci);
        dataUtil.setConfigurationAttribute(config, FileRemotePublicationTaskTest.ATT_LAYER, "mydem");
        dataUtil.setConfigurationAttribute(config, FileRemotePublicationTaskTest.ATT_EXT_GS, "mygs");
        config = dao.save(config);
        Trigger trigger = TriggerBuilder.newTrigger().forJob(batch.getId().toString()).startNow().build();
        scheduler.scheduleJob(trigger);
        while ((scheduler.getTriggerState(trigger.getKey())) != (TriggerState.NONE)) {
        } 
        GeoServerRESTManager restManager = extGeoservers.get("mygs").getRESTManager();
        Assert.assertTrue(restManager.getReader().existsCoveragestore("wcs", "DEM"));
        Assert.assertTrue(restManager.getReader().existsCoverage("wcs", "DEM", "mydem"));
        Assert.assertTrue(restManager.getReader().existsLayer("wcs", "mydem", true));
        Assert.assertFalse(restManager.getReader().existsCoverage("wcs", "DEM", "DEM"));
        RESTCoverage cov = restManager.getReader().getCoverage("wcs", "DEM", "mydem");
        Assert.assertEquals(ci.getTitle(), cov.getTitle());
        Assert.assertEquals(ci.getAbstract(), cov.getAbstract());
        Assert.assertEquals(ci.getDimensions().get(0).getName(), cov.getEncodedDimensionsInfoList().get(0).getName());
        Assert.assertTrue(cov.getKeywords().contains("demmiedem"));
        Assert.assertTrue(taskUtil.cleanup(config));
        Assert.assertFalse(restManager.getReader().existsCoveragestore("wcs", "DEM"));
        Assert.assertFalse(restManager.getReader().existsCoverage("wcs", "DEM", "mydem"));
        Assert.assertFalse(restManager.getReader().existsLayer("wcs", "mydem", true));
        // restore name
        ci.setName("DEM");
        geoServer.getCatalog().save(ci);
    }

    @Test
    public void testRollback() throws MalformedURLException, SQLException, SchedulerException {
        Task task2 = fac.createTask();
        task2.setName("task2");
        task2.setType(TestTaskTypeImpl.NAME);
        dataUtil.setTaskParameterToAttribute(task2, TestTaskTypeImpl.PARAM_FAIL, FileRemotePublicationTaskTest.ATT_FAIL);
        dataUtil.addTaskToConfiguration(config, task2);
        dataUtil.setConfigurationAttribute(config, FileRemotePublicationTaskTest.ATT_LAYER, "DEM");
        dataUtil.setConfigurationAttribute(config, FileRemotePublicationTaskTest.ATT_EXT_GS, "mygs");
        dataUtil.setConfigurationAttribute(config, FileRemotePublicationTaskTest.ATT_FAIL, Boolean.TRUE.toString());
        config = dao.save(config);
        task2 = config.getTasks().get("task2");
        dataUtil.addBatchElement(batch, task2);
        batch = bjService.saveAndSchedule(batch);
        Trigger trigger = TriggerBuilder.newTrigger().forJob(batch.getId().toString()).startNow().build();
        scheduler.scheduleJob(trigger);
        while ((scheduler.getTriggerState(trigger.getKey())) != (TriggerState.NONE)) {
        } 
        GeoServerRESTManager restManager = extGeoservers.get("mygs").getRESTManager();
        Assert.assertFalse(restManager.getReader().existsCoveragestore("wcs", "DEM"));
        Assert.assertFalse(restManager.getReader().existsCoverage("wcs", "DEM", "DEM"));
        Assert.assertFalse(restManager.getReader().existsLayer("wcs", "DEM", true));
    }
}

