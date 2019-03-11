/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wps.resource;


import java.io.File;
import java.io.FileOutputStream;
import org.geoserver.platform.resource.Resource;
import org.geoserver.wps.WPSTestSupport;
import org.geoserver.wps.executor.ProcessStatusTracker;
import org.junit.Assert;
import org.junit.Test;


public class WPSResourceManagerTest extends WPSTestSupport {
    WPSResourceManager resourceMgr;

    ProcessStatusTracker tracker;

    private static final File WPS_RESOURCE_DIR = new File("target/gs_datadir/tmp/wps");

    @Test
    public void testAddResourceNoExecutionId() throws Exception {
        File f = File.createTempFile("dummy", "dummy", new File("target"));
        resourceMgr.addResource(new WPSFileResource(f));
    }

    @Test
    public void testCleanupResource() throws Exception {
        String executionId = resourceMgr.getExecutionId(true);
        // Output resource
        Resource result = resourceMgr.getOutputResource(executionId, "test.txt");
        File file = result.file();
        try (FileOutputStream fos = new FileOutputStream(file)) {
            // Write some dummy content
            fos.write("dummy".getBytes());
        }
        // Temporary resource
        Resource temp = resourceMgr.getTemporaryResource("tmp");
        File tempFile = temp.file();
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            // Write some content
            fos.write("temptemptemp".getBytes());
        }
        Assert.assertTrue(file.exists());
        Assert.assertTrue(tempFile.exists());
        // Check the processId folder exists
        File processDir = new File(WPSResourceManagerTest.WPS_RESOURCE_DIR, executionId);
        Assert.assertTrue(processDir.exists());
        Assert.assertTrue(processDir.isDirectory());
        Assert.assertEquals(2, processDir.listFiles().length);// 2 subfolders exists (out and tmp)

        // Sleep a few milliseconds
        Thread.sleep(2);
        resourceMgr.cleanExpiredResources(System.currentTimeMillis(), tracker);
        // Check the processId folder doesn't exist anymore
        Assert.assertFalse(processDir.exists());
    }
}

