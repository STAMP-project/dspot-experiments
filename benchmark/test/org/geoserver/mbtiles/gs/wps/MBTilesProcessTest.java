/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.mbtiles.gs.wps;


import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geoserver.wps.WPSTestSupport;
import org.geotools.mbtiles.MBTilesFile;
import org.geotools.mbtiles.MBTilesMetadata;
import org.geotools.util.logging.Logging;
import org.junit.Assert;
import org.junit.Test;


public class MBTilesProcessTest extends WPSTestSupport {
    private static final Logger LOGGER = Logging.getLogger(MBTilesProcessTest.class);

    @Test
    public void testMBTilesProcess() throws Exception {
        File path = getDataDirectory().findOrCreateDataRoot();
        String urlPath = string(post("wps", getXml(path))).trim();
        File file = new File(path, "World.mbtiles");
        // File file = getDataDirectory().findFile("data", "test.mbtiles");
        Assert.assertNotNull(file);
        Assert.assertTrue(file.exists());
        MBTilesFile mbtiles = new MBTilesFile(file);
        MBTilesMetadata metadata = mbtiles.loadMetaData();
        Assert.assertEquals(11, mbtiles.maxZoom());
        Assert.assertEquals(10, mbtiles.minZoom());
        Assert.assertEquals("World", metadata.getName());
        Assert.assertEquals((-0.17578125), metadata.getBounds().getMinimum(0), 1.0E-4);
        Assert.assertEquals((-0.087890625), metadata.getBounds().getMinimum(1), 1.0E-4);
        Assert.assertEquals(0.17578125, metadata.getBounds().getMaximum(0), 1.0E-4);
        Assert.assertEquals(0.087890625, metadata.getBounds().getMaximum(1), 1.0E-4);
        try {
            mbtiles.close();
        } catch (Exception e) {
            MBTilesProcessTest.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}

