/**
 * (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.netcdf;


import org.geoserver.test.GeoServerSystemTestSupport;
import org.geotools.referencing.CRS;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


public class NetCDFCRSOverridingAuthorityFactoryTest extends GeoServerSystemTestSupport {
    @Test
    public void testCRSOverridingFactory() throws Exception {
        CoordinateReferenceSystem crs = CRS.decode("EPSG:971801");
        Assert.assertNotNull(crs);
        Integer epsgCode = CRS.lookupEpsgCode(crs, false);
        Assert.assertEquals(971801, epsgCode.intValue());
    }
}

