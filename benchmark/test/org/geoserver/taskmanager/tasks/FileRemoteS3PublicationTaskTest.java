/**
 * (c) 2017-2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.tasks;


import it.geosolutions.geoserver.rest.GeoServerRESTManager;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import org.geoserver.taskmanager.AbstractTaskManagerTest;
import org.geoserver.taskmanager.data.Batch;
import org.geoserver.taskmanager.data.Configuration;
import org.geoserver.taskmanager.data.TaskManagerDao;
import org.geoserver.taskmanager.data.TaskManagerFactory;
import org.geoserver.taskmanager.external.ExternalGS;
import org.geoserver.taskmanager.external.FileService;
import org.geoserver.taskmanager.external.impl.S3FileServiceImpl;
import org.geoserver.taskmanager.schedule.BatchJobService;
import org.geoserver.taskmanager.util.LookupService;
import org.geoserver.taskmanager.util.TaskManagerDataUtil;
import org.geoserver.taskmanager.util.TaskManagerTaskUtil;
import org.geotools.util.logging.Logging;
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
 * To run this test you should have a geoserver running on http://localhost:9090/geoserver.
 *
 * @author Niels Charlier
 */
public class FileRemoteS3PublicationTaskTest extends AbstractTaskManagerTest {
    private static final Logger LOGGER = Logging.getLogger(FileRemoteS3PublicationTaskTest.class);

    // configure these constants
    private static QName REMOTE_COVERAGE = new QName("gs", "mylayer", "gs");

    private static String REMOTE_COVERAGE_ALIAS = "test";

    private static String REMOTE_COVERAGE_BUCKET = "source";

    private static String REMOTE_COVERAGE_FILE_LOCATION = "test/salinity.tif";

    private static String REMOTE_COVERAGE_URL = ((((FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE_ALIAS) + "://") + (FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE_BUCKET)) + "/") + (FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE_FILE_LOCATION);

    private static String REMOTE_COVERAGE_OTHER_BUCKET = "target";

    private static String REMOTE_COVERAGE_OTHER_FILE_LOCATION_PATTERN = "test/salinity.###.tif";

    private static String REMOTE_COVERAGE_OTHER_FILE_LOCATION_OLD = "test/salinity.41.tif";

    private static String REMOTE_COVERAGE_OTHER_FILE_LOCATION = "test/salinity.42.tif";

    private static String REMOTE_COVERAGE_OTHER_URL = ((((FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE_ALIAS) + "://") + (FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE_OTHER_BUCKET)) + "/") + (FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE_OTHER_FILE_LOCATION);

    private static String REMOTE_COVERAGE_TYPE = "S3GeoTiff";

    private static final String ATT_LAYER = "layer";

    private static final String ATT_EXT_GS = "geoserver";

    private static final String ATT_FILE_SERVICE = "fileService";

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

    @Autowired
    private LookupService<FileService> fileServices;

    private Configuration config;

    private Batch batch;

    @Test
    public void testS3SuccessAndCleanup() throws MalformedURLException, SQLException, SchedulerException {
        dataUtil.setConfigurationAttribute(config, FileRemoteS3PublicationTaskTest.ATT_LAYER, (((FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE.getPrefix()) + ":") + (FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE.getLocalPart())));
        dataUtil.setConfigurationAttribute(config, FileRemoteS3PublicationTaskTest.ATT_EXT_GS, "mygs");
        config = dao.save(config);
        Trigger trigger = TriggerBuilder.newTrigger().forJob(batch.getId().toString()).startNow().build();
        scheduler.scheduleJob(trigger);
        while ((scheduler.getTriggerState(trigger.getKey())) != (TriggerState.NONE)) {
        } 
        GeoServerRESTManager restManager = extGeoservers.get("mygs").getRESTManager();
        Assert.assertTrue(restManager.getReader().existsCoveragestore(FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE.getPrefix(), FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE.getLocalPart()));
        Assert.assertTrue(restManager.getReader().existsCoverage(FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE.getPrefix(), FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE.getLocalPart(), FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE.getLocalPart()));
        Assert.assertTrue(restManager.getReader().existsLayer(FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE.getPrefix(), FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE.getLocalPart(), true));
        Assert.assertTrue(taskUtil.cleanup(config));
        Assert.assertFalse(restManager.getReader().existsCoveragestore(FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE.getPrefix(), FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE.getLocalPart()));
        Assert.assertFalse(restManager.getReader().existsCoverage(FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE.getPrefix(), FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE.getLocalPart(), FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE.getLocalPart()));
        Assert.assertFalse(restManager.getReader().existsLayer(FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE.getPrefix(), FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE.getLocalPart(), true));
    }

    @Test
    public void testS3ReplaceUrlSuccessAndCleanup() throws IOException, SQLException, SchedulerException {
        FileService otherFileService = null;
        try {
            FileService fileService = fileServices.get(S3FileServiceImpl.name(FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE_ALIAS, FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE_BUCKET));
            otherFileService = fileServices.get(S3FileServiceImpl.name(FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE_ALIAS, FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE_OTHER_BUCKET));
            Assume.assumeNotNull(otherFileService);
            try (InputStream is = fileService.read(FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE_FILE_LOCATION)) {
                otherFileService.create(FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE_OTHER_FILE_LOCATION_OLD, is);
            }
            try (InputStream is = fileService.read(FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE_FILE_LOCATION)) {
                otherFileService.create(FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE_OTHER_FILE_LOCATION, is);
            }
        } catch (Exception e) {
            FileRemoteS3PublicationTaskTest.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            Assume.assumeTrue("S3 service is configured and available", false);
        }
        dataUtil.setConfigurationAttribute(config, FileRemoteS3PublicationTaskTest.ATT_LAYER, (((FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE.getPrefix()) + ":") + (FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE.getLocalPart())));
        dataUtil.setConfigurationAttribute(config, FileRemoteS3PublicationTaskTest.ATT_FILE_SERVICE, S3FileServiceImpl.name(FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE_ALIAS, FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE_OTHER_BUCKET));
        dataUtil.setConfigurationAttribute(config, FileRemoteS3PublicationTaskTest.ATT_FILE, FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE_OTHER_FILE_LOCATION_PATTERN);
        dataUtil.setConfigurationAttribute(config, FileRemoteS3PublicationTaskTest.ATT_EXT_GS, "mygs");
        config = dao.save(config);
        Trigger trigger = TriggerBuilder.newTrigger().forJob(batch.getId().toString()).startNow().build();
        scheduler.scheduleJob(trigger);
        while ((scheduler.getTriggerState(trigger.getKey())) != (TriggerState.NONE)) {
        } 
        GeoServerRESTManager restManager = extGeoservers.get("mygs").getRESTManager();
        Assert.assertTrue(restManager.getReader().existsCoveragestore(FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE.getPrefix(), FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE.getLocalPart()));
        Assert.assertEquals(FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE_OTHER_URL, restManager.getReader().getCoverageStore(FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE.getPrefix(), FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE.getLocalPart()).getURL());
        Assert.assertTrue(restManager.getReader().existsCoverage(FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE.getPrefix(), FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE.getLocalPart(), FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE.getLocalPart()));
        Assert.assertTrue(restManager.getReader().existsLayer(FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE.getPrefix(), FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE.getLocalPart(), true));
        Assert.assertTrue(taskUtil.cleanup(config));
        Assert.assertFalse(restManager.getReader().existsCoveragestore(FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE.getPrefix(), FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE.getLocalPart()));
        Assert.assertFalse(restManager.getReader().existsCoverage(FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE.getPrefix(), FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE.getLocalPart(), FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE.getLocalPart()));
        Assert.assertFalse(restManager.getReader().existsLayer(FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE.getPrefix(), FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE.getLocalPart(), true));
        otherFileService.delete(FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE_OTHER_FILE_LOCATION);
        otherFileService.delete(FileRemoteS3PublicationTaskTest.REMOTE_COVERAGE_OTHER_FILE_LOCATION_OLD);
    }
}

