/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.wicket;


import org.apache.wicket.model.Model;
import org.geotools.referencing.CRS;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


public class WKTToCRSModelTest {
    @Test
    public void testNullSRS() throws Exception {
        Model wkt = new Model(null);
        WKTToCRSModel crs = new WKTToCRSModel(wkt);
        Assert.assertNull(crs.getObject());
        crs.setObject(null);
        Assert.assertEquals(null, wkt.getObject());
    }

    @Test
    public void testNonNullSRS() throws Exception {
        CoordinateReferenceSystem utm32n = CRS.decode("EPSG:32632");
        CoordinateReferenceSystem wgs84 = CRS.decode("EPSG:4326");
        Model wkt = new Model(utm32n.toString());
        WKTToCRSModel crs = new WKTToCRSModel(wkt);
        Assert.assertTrue(CRS.equalsIgnoreMetadata(utm32n, crs.getObject()));
        crs.setObject(wgs84);
        Assert.assertEquals(wgs84.toString(), wkt.getObject());
    }
}

