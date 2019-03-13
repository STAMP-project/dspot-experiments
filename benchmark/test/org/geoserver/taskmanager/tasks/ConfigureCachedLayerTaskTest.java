package org.geoserver.taskmanager.tasks;


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
import org.geowebcache.config.XMLGridSubset;
import org.geowebcache.filter.parameters.IntegerParameterFilter;
import org.geowebcache.filter.parameters.ParameterFilter;
import org.junit.Assert;
import org.junit.Test;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;


public class ConfigureCachedLayerTaskTest extends AbstractTaskManagerTest {
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

    @Test
    public void testConfigureAndDelete() throws MalformedURLException, SQLException, SchedulerException {
        // configure caching
        GWC gwc = GWC.get();
        final GeoServerTileLayer tileLayer = new GeoServerTileLayer(gwc.getLayerInfoByName("DEM"), gwc.getConfig(), gwc.getGridSetBroker());
        tileLayer.getInfo().setEnabled(true);
        tileLayer.getInfo().setExpireCache(20);
        tileLayer.getInfo().setExpireClients(30);
        tileLayer.getInfo().setGutter(7);
        tileLayer.getInfo().setBlobStoreId("myblob");
        tileLayer.getInfo().setMetaTilingX(1);
        tileLayer.getInfo().setMetaTilingY(2);
        ParameterFilter a = new IntegerParameterFilter();
        a.setKey("a");
        a.setDefaultValue("1");
        tileLayer.getInfo().addParameterFilter(a);
        ParameterFilter b = new IntegerParameterFilter();
        b.setKey("b");
        b.setDefaultValue("2");
        tileLayer.getInfo().addParameterFilter(b);
        XMLGridSubset gridSubset = new XMLGridSubset();
        gridSubset.setGridSetName("MyGridSubSet");
        gridSubset.setZoomStart(1);
        tileLayer.getInfo().getGridSubsets().clear();
        tileLayer.getInfo().getGridSubsets().add(gridSubset);
        tileLayer.getInfo().getMimeFormats().clear();
        tileLayer.getInfo().getMimeFormats().add("my/mime");
        tileLayer.getInfo().getMimeFormats().add("my/dime");
        gwc.add(tileLayer);
        dataUtil.setConfigurationAttribute(config, ConfigureCachedLayerTaskTest.ATT_LAYER, "DEM");
        dataUtil.setConfigurationAttribute(config, ConfigureCachedLayerTaskTest.ATT_EXT_GS, "mygs");
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
        Assert.assertEquals(tileLayer.getInfo().getBlobStoreId(), enc.getBlobStoreId());
        Assert.assertEquals(tileLayer.getInfo().getExpireCache(), enc.getExpireCache());
        Assert.assertEquals(tileLayer.getInfo().getExpireClients(), enc.getExpireClients());
        Assert.assertEquals(tileLayer.getInfo().getGutter(), enc.getGutter());
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

