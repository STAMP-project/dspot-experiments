/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs;


import org.geotools.referencing.CRS;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.spatial.BBOX;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


public class WFSReprojectionUtilTest {
    private final FeatureType featureType = WFSReprojectionUtilTest.createFeatureType();

    private final FilterFactory2 filterFactory = WFSReprojectionUtilTest.createFilterFactory();

    @Test
    public void testNullCheck() {
        // used to throw an NPE if the feature type was null
        CoordinateReferenceSystem crs = WFSReprojectionUtil.getDeclaredCrs(((FeatureType) (null)), "1.1.0");
        Assert.assertNull(crs);
    }

    @Test
    public void testReprojectFilterWithTargetCrs() throws FactoryException {
        BBOX bbox = filterFactory.bbox(filterFactory.property("geom"), 10, 15, 20, 25, "EPSG:4326");
        Filter clone = WFSReprojectionUtil.reprojectFilter(bbox, featureType, CRS.decode("EPSG:3857"));
        Assert.assertNotSame(bbox, clone);
        BBOX clonedBbox = ((BBOX) (clone));
        Assert.assertEquals(bbox.getPropertyName(), clonedBbox.getPropertyName());
        Assert.assertEquals(1669792.3618991035, clonedBbox.getMinX(), 0.1);
        Assert.assertEquals(1118889.9748579597, clonedBbox.getMinY(), 0.1);
        Assert.assertEquals(2782987.269831839, clonedBbox.getMaxX(), 0.1);
        Assert.assertEquals(2273030.926987689, clonedBbox.getMaxY(), 0.1);
        Assert.assertEquals("EPSG:3857", clonedBbox.getSRS());
    }
}

