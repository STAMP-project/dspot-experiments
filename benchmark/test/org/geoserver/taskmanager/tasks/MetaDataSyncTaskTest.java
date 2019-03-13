/**
 * (c) 2017-2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.tasks;


import it.geosolutions.geoserver.rest.GeoServerRESTManager;
import it.geosolutions.geoserver.rest.decoder.RESTCoverage;
import it.geosolutions.geoserver.rest.decoder.RESTLayer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.StyleInfo;
import org.geoserver.config.GeoServerDataDirectory;
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
import org.geoserver.util.IOUtils;
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
public class MetaDataSyncTaskTest extends AbstractTaskManagerTest {
    private static final String STYLE = "grass";

    private static final String SECOND_STYLE = "second_grass";

    private static final String ATT_LAYER = "layer";

    static final String ATT_EXT_GS = "geoserver";

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
    private GeoServerDataDirectory dd;

    private Configuration config;

    private Batch batchCreate;

    private Batch batchSync;

    @Test
    public void test() throws IOException, SQLException, SchedulerException {
        // set some metadata
        CoverageInfo ci = geoServer.getCatalog().getCoverageByName("DEM");
        ci.setTitle("original title");
        ci.setAbstract("original abstract");
        geoServer.getCatalog().save(ci);
        // set a style
        LayerInfo li = geoServer.getCatalog().getLayerByName("DEM");
        StyleInfo si = geoServer.getCatalog().getStyleByName(MetaDataSyncTaskTest.STYLE);
        li.setDefaultStyle(si);
        geoServer.getCatalog().save(li);
        dataUtil.setConfigurationAttribute(config, MetaDataSyncTaskTest.ATT_LAYER, "DEM");
        dataUtil.setConfigurationAttribute(config, MetaDataSyncTaskTest.ATT_EXT_GS, "mygs");
        config = dao.save(config);
        Trigger trigger = TriggerBuilder.newTrigger().forJob(batchCreate.getId().toString()).startNow().build();
        scheduler.scheduleJob(trigger);
        while ((scheduler.getTriggerState(trigger.getKey())) != (TriggerState.NONE)) {
        } 
        GeoServerRESTManager restManager = extGeoservers.get("mygs").getRESTManager();
        Assert.assertTrue(restManager.getReader().existsCoveragestore("wcs", "DEM"));
        Assert.assertTrue(restManager.getReader().existsCoverage("wcs", "DEM", "DEM"));
        Assert.assertTrue(restManager.getReader().existsLayer("wcs", "DEM", true));
        RESTCoverage cov = restManager.getReader().getCoverage("wcs", "DEM", "DEM");
        Assert.assertEquals(ci.getTitle(), cov.getTitle());
        Assert.assertEquals(ci.getAbstract(), cov.getAbstract());
        Assert.assertEquals(ci.getDimensions().get(0).getName(), cov.getEncodedDimensionsInfoList().get(0).getName());
        RESTLayer layer = restManager.getReader().getLayer("wcs", "DEM");
        Assert.assertEquals(MetaDataSyncTaskTest.STYLE, layer.getDefaultStyle());
        Assert.assertNull(layer.getStyles());
        // metadata sync
        ci.setTitle("new title");
        ci.setAbstract("new abstract");
        ci.getDimensions().get(0).setName("CUSTOM_DIMENSION");
        ci.getMetadata().put("something", "anything");
        geoServer.getCatalog().save(ci);
        li.getStyles().add(geoServer.getCatalog().getStyleByName(MetaDataSyncTaskTest.SECOND_STYLE));
        geoServer.getCatalog().save(li);
        try (OutputStream out = dd.style(si).out()) {
            try (InputStream in = getClass().getResource("third_grass.sld").openStream()) {
                IOUtils.copy(in, out);
            }
        }
        trigger = TriggerBuilder.newTrigger().forJob(batchSync.getId().toString()).startNow().build();
        scheduler.scheduleJob(trigger);
        while ((scheduler.getTriggerState(trigger.getKey())) != (TriggerState.NONE)) {
        } 
        cov = restManager.getReader().getCoverage("wcs", "DEM", "DEM");
        Assert.assertEquals(ci.getTitle(), cov.getTitle());
        Assert.assertEquals(ci.getAbstract(), cov.getAbstract());
        Assert.assertEquals(ci.getDimensions().get(0).getName(), cov.getEncodedDimensionsInfoList().get(0).getName());
        Assert.assertEquals("something", cov.getMetadataList().get(0).getKey());
        Assert.assertEquals("anything", cov.getMetadataList().get(0).getMetadataElem().getText());
        layer = restManager.getReader().getLayer("wcs", "DEM");
        Assert.assertEquals(MetaDataSyncTaskTest.STYLE, layer.getDefaultStyle());
        Assert.assertEquals(1, layer.getStyles().size());
        Assert.assertEquals(MetaDataSyncTaskTest.SECOND_STYLE, layer.getStyles().get(0).getName());
        String style = restManager.getStyleManager().getSLD(MetaDataSyncTaskTest.STYLE);
        Assert.assertTrue(((style.indexOf("CHANGED VERSION")) > 0));
        // clean-up
        Assert.assertTrue(taskUtil.cleanup(config));
        Assert.assertFalse(restManager.getReader().existsCoveragestore("wcs", "DEM"));
        Assert.assertFalse(restManager.getReader().existsCoverage("wcs", "DEM", "DEM"));
        Assert.assertFalse(restManager.getReader().existsLayer("wcs", "DEM", true));
    }
}

