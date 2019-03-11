/**
 * (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.gwc.web.gridset;


import BoundingBox.WORLD3857;
import BoundingBox.WORLD4326;
import GridSetFactory.DEFAULT_LEVELS;
import GridSetFactory.DEFAULT_PIXEL_SIZE_METER;
import org.geowebcache.grid.GridSet;
import org.geowebcache.grid.GridSetFactory;
import org.geowebcache.grid.SRS;
import org.junit.Assert;
import org.junit.Test;


public class GridSetBuilderTest {
    /**
     * Checks yCoordinateFirst value detection based on EPSG:4326 GridSet
     */
    @Test
    public void testYCoordinateFirstEPSG4326() {
        GridSet epsg4326 = GridSetFactory.createGridSet("GlobalCRS84Geometric", SRS.getEPSG4326(), WORLD4326, false, DEFAULT_LEVELS, null, DEFAULT_PIXEL_SIZE_METER, 256, 256, false);
        epsg4326.setDescription(("A default WGS84 tile matrix set where the first zoom level " + (("covers the world with two tiles on the horizonal axis and one tile " + "over the vertical axis and each subsequent zoom level is calculated by half ") + "the resolution of its previous one.")));
        GridSetInfo info = new GridSetInfo(epsg4326, false);
        GridSet finalGridSet = GridSetBuilder.build(info);
        Assert.assertTrue(finalGridSet.isyCoordinateFirst());
    }

    /**
     * Checks yCoordinateFirst value detection based on EPSG:3857 GridSet
     */
    @Test
    public void testYCoordinateFirstEPSG3857() {
        GridSet epsg3857 = GridSetFactory.createGridSet("GoogleMapsCompatible", SRS.getEPSG3857(), WORLD3857, false, commonPractice900913Resolutions(), null, 1.0, DEFAULT_PIXEL_SIZE_METER, null, 256, 256, false);
        epsg3857.setDescription(("This well-known scale set has been defined to be compatible with Google Maps and" + ((" Microsoft Live Map projections and zoom levels. Level 0 allows representing the whole " + "world in a single 256x256 pixels. The next level represents the whole world in 2x2 tiles ") + "of 256x256 pixels and so on in powers of 2. Scale denominator is only accurate near the equator.")));
        GridSetInfo info = new GridSetInfo(epsg3857, false);
        GridSet finalGridSet = GridSetBuilder.build(info);
        Assert.assertFalse(finalGridSet.isyCoordinateFirst());
    }
}

