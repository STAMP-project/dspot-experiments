/**
 * (c) 2017-2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.tasks;


import java.io.IOException;
import java.util.logging.Logger;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.catalog.CoverageStoreInfo;
import org.geoserver.taskmanager.AbstractTaskManagerTest;
import org.geoserver.taskmanager.data.Batch;
import org.geoserver.taskmanager.data.Configuration;
import org.geoserver.taskmanager.data.TaskManagerDao;
import org.geoserver.taskmanager.data.TaskManagerFactory;
import org.geoserver.taskmanager.external.FileService;
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


public class FileLocalS3PublicationTaskTest extends AbstractTaskManagerTest {
    private static final Logger LOGGER = Logging.getLogger(FileLocalS3PublicationTaskTest.class);

    // configure these constants
    private static final String FILE_LOCATION = "test/world.tiff";

    private static final String FILE_SERVICE = "data-directory";

    private static final String WORKSPACE = "gs";

    private static final String COVERAGE_NAME = "world";

    private static final String LAYER_NAME = ((FileLocalS3PublicationTaskTest.WORKSPACE) + ":") + (FileLocalS3PublicationTaskTest.COVERAGE_NAME);

    private static final String REMOTE_FILE_LOCATION = "test/salinity.tif";

    private static final String REMOTE_FILE_SERVICE = "s3-test-source";

    // attributes
    private static final String ATT_FILE_SERVICE = "fileService";

    private static final String ATT_FILE = "file";

    private static final String ATT_LAYER = "layer";

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
    private LookupService<FileService> fileServices;

    private Configuration config;

    private Batch batch;

    @Test
    public void testSuccessAndCleanup() throws IOException, SchedulerException {
        FileService fileService = null;
        try {
            fileService = fileServices.get(FileLocalS3PublicationTaskTest.REMOTE_FILE_SERVICE);
            Assume.assumeNotNull(fileService);
            Assume.assumeTrue("File exists on s3 service", fileService.checkFileExists(FileLocalS3PublicationTaskTest.REMOTE_FILE_LOCATION));
        } catch (Exception e) {
            FileLocalS3PublicationTaskTest.LOGGER.severe(e.getMessage());
            Assume.assumeTrue("S3 service is configured and available", false);
        }
        dataUtil.setConfigurationAttribute(config, FileLocalS3PublicationTaskTest.ATT_FILE_SERVICE, FileLocalS3PublicationTaskTest.REMOTE_FILE_SERVICE);
        dataUtil.setConfigurationAttribute(config, FileLocalS3PublicationTaskTest.ATT_FILE, FileLocalS3PublicationTaskTest.REMOTE_FILE_LOCATION);
        dataUtil.setConfigurationAttribute(config, FileLocalS3PublicationTaskTest.ATT_LAYER, FileLocalS3PublicationTaskTest.LAYER_NAME);
        config = dao.save(config);
        Trigger trigger = TriggerBuilder.newTrigger().forJob(batch.getId().toString()).startNow().build();
        scheduler.scheduleJob(trigger);
        while ((scheduler.getTriggerState(trigger.getKey())) != (TriggerState.NONE)) {
        } 
        Assert.assertNotNull(catalog.getLayerByName(FileLocalS3PublicationTaskTest.LAYER_NAME));
        CoverageStoreInfo csi = catalog.getStoreByName(FileLocalS3PublicationTaskTest.WORKSPACE, FileLocalS3PublicationTaskTest.COVERAGE_NAME, CoverageStoreInfo.class);
        Assert.assertNotNull(csi);
        Assert.assertEquals(fileService.getURI(FileLocalS3PublicationTaskTest.REMOTE_FILE_LOCATION).toString(), csi.getURL());
        Assert.assertNotNull(catalog.getResourceByName(FileLocalS3PublicationTaskTest.LAYER_NAME, CoverageInfo.class));
        taskUtil.cleanup(config);
        Assert.assertNull(catalog.getLayerByName(FileLocalS3PublicationTaskTest.LAYER_NAME));
        Assert.assertNull(catalog.getStoreByName(FileLocalS3PublicationTaskTest.WORKSPACE, FileLocalS3PublicationTaskTest.COVERAGE_NAME, CoverageStoreInfo.class));
        Assert.assertNull(catalog.getResourceByName(FileLocalS3PublicationTaskTest.LAYER_NAME, CoverageInfo.class));
    }
}

