/**
 * (c) 2014 - 2015 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.vfny.global;


import org.geoserver.config.GeoServer;
import org.geoserver.data.test.MockData;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class TolerantStartupTest extends GeoServerSystemTestSupport {
    // @Override
    // protected String getLogConfiguration() {
    // return "/DEFAULT_LOGGING.properties";
    // }
    @Test
    public void testContextStartup() {
        GeoServer config = ((GeoServer) (getBean("geoServer")));
        Assert.assertNotNull(config.getCatalog().getFeatureTypeByName(MockData.BUILDINGS.getNamespaceURI(), MockData.BUILDINGS.getLocalPart()));
        Assert.assertNotNull(config.getCatalog().getFeatureTypeByName(MockData.BASIC_POLYGONS.getNamespaceURI(), MockData.BASIC_POLYGONS.getLocalPart()));
    }
}

