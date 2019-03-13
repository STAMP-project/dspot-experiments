/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.logging;


import org.geoserver.config.GeoServer;
import org.junit.Assert;
import org.junit.Test;


public class LegacyLoggingImporterTest {
    GeoServer gs;

    LegacyLoggingImporter importer;

    @Test
    public void test() throws Exception {
        Assert.assertEquals("DEFAULT_LOGGING.properties", importer.getConfigFileName());
        Assert.assertFalse(importer.getSuppressStdOutLogging());
        Assert.assertEquals("logs/geoserver.log", importer.getLogFile());
    }
}

