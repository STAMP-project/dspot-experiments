package org.geoserver.taskmanager.tasks;


import Run.Status.COMMITTED;
import it.geosolutions.geoserver.rest.GeoServerRESTManager;
import it.geosolutions.geoserver.rest.encoder.GSCachedLayerEncoder;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.Collections;
import org.geoserver.gwc.GWC;
import org.geoserver.gwc.layer.GeoServerTileLayer;
import org.geoserver.taskmanager.AbstractTaskManagerTest;
import org.geoserver.taskmanager.data.Batch;
import org.geoserver.taskmanager.data.Configuration;
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


public class ClearCachedLayerTaskTest extends AbstractTaskManagerTest {
    private static final String ATT_LAYER = "layer";

    private static final String ATT_EXT_GS = "geoserver";

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

    private Batch batchUpdate;

    private Batch batchClear;

    @Test
    public void testConfigureClearAndDelete() throws MalformedURLException, SQLException, SchedulerException {
        // configure caching
        GWC gwc = GWC.get();
        final GeoServerTileLayer tileLayer = new GeoServerTileLayer(gwc.getLayerInfoByName("DEM"), gwc.getConfig(), gwc.getGridSetBroker());
        tileLayer.getInfo().setEnabled(true);
        tileLayer.getInfo().setInMemoryCached(false);
        gwc.add(tileLayer);
        dataUtil.setConfigurationAttribute(config, ClearCachedLayerTaskTest.ATT_LAYER, "DEM");
        dataUtil.setConfigurationAttribute(config, ClearCachedLayerTaskTest.ATT_EXT_GS, "mygs");
        config = dao.save(config);
        Trigger trigger = TriggerBuilder.newTrigger().forJob(batch.getId().toString()).startNow().build();
        scheduler.scheduleJob(trigger);
        while ((scheduler.getTriggerState(trigger.getKey())) != (TriggerState.NONE)) {
        } 
        GeoServerRESTManager restManager = extGeoservers.get("mygs").getRESTManager();
        Assert.assertTrue(restManager.getReader().existsCoveragestore("wcs", "DEM"));
        Assert.assertTrue(restManager.getReader().existsCoverage("wcs", "DEM", "DEM"));
        Assert.assertTrue(restManager.getReader().existsLayer("wcs", "DEM", true));
        GSCachedLayerEncoder enc = restManager.getGeoWebCacheRest().getLayer("wcs:DEM");
        Assert.assertNotNull(enc);
        // clear caching configuration
        trigger = TriggerBuilder.newTrigger().forJob(batchClear.getId().toString()).startNow().build();
        scheduler.scheduleJob(trigger);
        while ((scheduler.getTriggerState(trigger.getKey())) != (TriggerState.NONE)) {
        } 
        // the only way to really verify is that we didn't get any errors back
        batchClear = dao.initHistory(batchClear);
        Assert.assertEquals(COMMITTED, batchClear.getBatchRuns().get(0).getStatus());
        // delete caching configuration
        gwc.removeTileLayers(Collections.singletonList("wcs:DEM"));
        trigger = TriggerBuilder.newTrigger().forJob(batchUpdate.getId().toString()).startNow().build();
        scheduler.scheduleJob(trigger);
        while ((scheduler.getTriggerState(trigger.getKey())) != (TriggerState.NONE)) {
        } 
        Assert.assertNull(null, restManager.getGeoWebCacheRest().getLayer("wcs:DEM"));
        // clean-up layer
        Assert.assertTrue(taskUtil.cleanup(config));
        Assert.assertFalse(restManager.getReader().existsCoveragestore("wcs", "DEM"));
        Assert.assertFalse(restManager.getReader().existsCoverage("wcs", "DEM", "DEM"));
        Assert.assertFalse(restManager.getReader().existsLayer("wcs", "DEM", true));
    }
}

