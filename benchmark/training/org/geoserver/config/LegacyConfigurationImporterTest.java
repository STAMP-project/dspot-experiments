/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.config;


import org.geoserver.config.util.LegacyConfigurationImporter;
import org.junit.Assert;
import org.junit.Test;


public class LegacyConfigurationImporterTest {
    LegacyConfigurationImporter importer;

    @Test
    public void testGlobal() throws Exception {
        GeoServerInfo info = importer.getConfiguration().getGlobal();
        Assert.assertNotNull(info);
        LoggingInfo logging = importer.getConfiguration().getLogging();
        Assert.assertNotNull(logging);
        Assert.assertEquals("DEFAULT_LOGGING.properties", logging.getLevel());
        Assert.assertTrue(logging.isStdOutLogging());
        Assert.assertEquals("logs/geoserver.log", logging.getLocation());
        Assert.assertFalse(info.isVerbose());
        Assert.assertFalse(info.isVerboseExceptions());
        Assert.assertEquals(8, info.getNumDecimals());
        Assert.assertEquals("UTF-8", info.getCharset());
        Assert.assertEquals(3, info.getUpdateSequence());
    }
}

