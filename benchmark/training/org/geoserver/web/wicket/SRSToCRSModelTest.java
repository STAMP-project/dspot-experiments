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


public class SRSToCRSModelTest {
    @Test
    public void testNullSRS() throws Exception {
        Model srs = new Model(null);
        SRSToCRSModel crs = new SRSToCRSModel(srs);
        Assert.assertNull(crs.getObject());
        crs.setObject(null);
        Assert.assertEquals(null, srs.getObject());
    }

    @Test
    public void testNonNullSRS() throws Exception {
        Model srs = new Model("EPSG:32632");
        SRSToCRSModel crs = new SRSToCRSModel(srs);
        Assert.assertEquals(CRS.decode("EPSG:32632"), crs.getObject());
        crs.setObject(CRS.decode("EPSG:4326"));
        Assert.assertEquals("EPSG:4326", srs.getObject());
    }
}

