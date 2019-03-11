/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.csw.store.internal;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import org.geoserver.csw.CSWTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class GeoServerInternalCatalogStoreTest extends CSWTestSupport {
    @Test
    public void testModifyMappingFiles() throws IOException, InterruptedException {
        // clear any existing mappings
        File root = testData.getDataDirectoryRoot();
        File csw = new File(root, "csw");
        if (csw.exists()) {
            csw.delete();
        }
        // create new store
        InternalCatalogStore store = new GeoServerInternalCatalogStore(getGeoServer());
        // test if we have default mapping
        File record = new File(csw, "Record.properties");
        Assert.assertTrue(record.exists());
        Assert.assertNotNull(store.getMapping("MD_Metadata").getElement("fileIdentifier.CharacterString"));
        Assert.assertNotNull(store.getMapping("Record").getElement("identifier.value"));
        Assert.assertNull(store.getMapping("Record").getElement("format.value"));
        // modify mapping file
        // wait one second, so the modification time will change and change is detected (on linux
        // the resolution is 1s instead of 1ms)
        Thread.sleep(1001);
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(record, true)));
        out.println("\nformat.value=\'img/jpeg\'");
        out.close();
        // wait one second, that is exactly what it takes FileWatcher to update
        Thread.sleep(1001);
        // mapping should be automatically reloaded
        Assert.assertEquals("img/jpeg", store.getMapping("Record").getElement("format.value").getContent().toString());
    }
}

