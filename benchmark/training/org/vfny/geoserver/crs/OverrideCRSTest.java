/**
 * (c) 2014 - 2015 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.vfny.geoserver.crs;


import org.geoserver.test.GeoServerSystemTestSupport;
import org.geoserver.test.SystemTest;
import org.geotools.referencing.CRS;
import org.geotools.referencing.datum.BursaWolfParameters;
import org.geotools.referencing.datum.DefaultGeodeticDatum;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


@Category(SystemTest.class)
public class OverrideCRSTest extends GeoServerSystemTestSupport {
    @Test
    public void testOverride() throws Exception {
        CoordinateReferenceSystem epsg3003 = CRS.decode("EPSG:3003");
        DefaultGeodeticDatum datum3003 = ((DefaultGeodeticDatum) (getDatum()));
        BursaWolfParameters[] bwParamArray3003 = datum3003.getBursaWolfParameters();
        Assert.assertEquals(1, bwParamArray3003.length);
        BursaWolfParameters bw3003 = bwParamArray3003[0];
        double tol = 1.0E-7;
        Assert.assertEquals((-104.1), bw3003.dx, tol);
        Assert.assertEquals((-49.1), bw3003.dy, tol);
        Assert.assertEquals((-9.9), bw3003.dz, tol);
        Assert.assertEquals(0.971, bw3003.ex, tol);
        Assert.assertEquals((-2.917), bw3003.ey, tol);
        Assert.assertEquals(0.714, bw3003.ez, tol);
        Assert.assertEquals((-11.68), bw3003.ppm, tol);
        // without an override they should be the same as 3002
        CoordinateReferenceSystem epsg3002 = CRS.decode("EPSG:3002");
        DefaultGeodeticDatum datum3002 = ((DefaultGeodeticDatum) (getDatum()));
        BursaWolfParameters[] bwParamArray3002 = datum3002.getBursaWolfParameters();
        Assert.assertEquals(1, bwParamArray3002.length);
        BursaWolfParameters bw3002 = bwParamArray3002[0];
        Assert.assertFalse(bw3002.equals(bw3003));
    }
}

