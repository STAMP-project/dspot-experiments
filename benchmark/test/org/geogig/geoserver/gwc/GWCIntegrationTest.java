/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geogig.geoserver.gwc;


import CacheResult.HIT;
import CacheResult.MISS;
import org.geogig.geoserver.GeoGigTestData;
import org.geoserver.gwc.GWC;
import org.geoserver.gwc.layer.GeoServerTileLayer;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.geoserver.test.TestSetup;
import org.geoserver.test.TestSetupFrequency;
import org.geowebcache.conveyor.Conveyor.CacheResult;
import org.geowebcache.conveyor.ConveyorTile;
import org.geowebcache.grid.BoundingBox;
import org.geowebcache.grid.GridSubset;
import org.geowebcache.storage.StorageBroker;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.locationtech.jts.geom.Envelope;
import org.opengis.feature.simple.SimpleFeature;


/**
 * Integration test for GeoServer cached layers using the GWC REST API
 */
@TestSetup(run = TestSetupFrequency.ONCE)
public class GWCIntegrationTest extends GeoServerSystemTestSupport {
    private static GWC mediator;

    private static GeoServerTileLayer pointsLayer;

    private static GeoServerTileLayer linesLayer;

    private static StorageBroker storageBroker;

    @Rule
    public GeoGigTestData geogigData = new GeoGigTestData();

    @Test
    public void testRemoveSingleFeature() throws Exception {
        ConveyorTile tile = createTileProto(GWCIntegrationTest.pointsLayer);
        SimpleFeature feature = geogigData.getFeature("points/p2");// POINT(1 1)

        Envelope bounds = ((Envelope) (feature.getBounds()));
        BoundingBox featureBounds = new BoundingBox(bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY());
        GridSubset gridSubset = GWCIntegrationTest.pointsLayer.getGridSubset(tile.getGridSetId());
        long[][] featureCoverages = gridSubset.getCoverageIntersections(featureBounds);
        int level = 4;
        long[] levelCoverageIntersection = featureCoverages[level];
        final long tileX = levelCoverageIntersection[0];
        final long tileY = levelCoverageIntersection[1];
        long[] xyz = tile.getStorageObject().getXYZ();
        xyz[0] = tileX;
        xyz[1] = tileY;
        xyz[2] = level;
        ConveyorTile result = GWCIntegrationTest.pointsLayer.getTile(tile);
        CacheResult cacheResult = result.getCacheResult();
        Assert.assertEquals(MISS, cacheResult);
        result = GWCIntegrationTest.pointsLayer.getTile(tile);
        cacheResult = result.getCacheResult();
        Assert.assertEquals(HIT, cacheResult);
        // 
        // 
        // 
        // 
        // 9570
        geogigData.update("points/p1", "geom", "POINT(-1 -1)").add().commit("moved POINT(1 1) to POINT(-1 -1)").update("lines/l1", "geom", "LINESTRING(0 10, 0 -10)").add().commit("moved LINESTRING(-10 0, 10 0) to LINESTRING(0 10, 0 -10)");
        // give the hook some time to run
        Thread.sleep(100);
        result = GWCIntegrationTest.pointsLayer.getTile(tile);
        cacheResult = result.getCacheResult();
        Assert.assertEquals(MISS, cacheResult);
    }
}

