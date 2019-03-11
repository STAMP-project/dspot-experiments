/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.mbtiles;


import MBTilesFile.TileIterator;
import MBTilesMetadata.t_format.PNG;
import MBTilesMetadata.t_type.OVERLAY;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.geoserver.wms.GetMapRequest;
import org.geoserver.wms.WMSMapContent;
import org.geoserver.wms.WMSTestSupport;
import org.geoserver.wms.WebMap;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.mbtiles.MBTilesFile;
import org.geotools.mbtiles.MBTilesMetadata;
import org.geotools.mbtiles.MBTilesTile;
import org.geotools.referencing.CRS;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Envelope;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


/**
 * Test For WMS GetMap Output Format for MBTiles
 *
 * @author Niels Charlier
 */
public class MBTilesGetMapOutputFormatTest extends WMSTestSupport {
    MBTilesGetMapOutputFormat format;

    @Test
    public void testTileEntries() throws Exception {
        WMSMapContent mapContent = createMapContent(WORLD, LAKES);
        mapContent.getRequest().setBbox(new Envelope((-0.17578125), (-0.087890625), 0.17578125, 0.087890625));
        mapContent.getRequest().getFormatOptions().put("min_zoom", "10");
        mapContent.getRequest().getFormatOptions().put("max_zoom", "11");
        WebMap map = format.produceMap(mapContent);
        MBTilesFile mbtiles = createMbTilesFiles(map);
        MBTilesMetadata metadata = mbtiles.loadMetaData();
        Assert.assertEquals("World_Lakes", metadata.getName());
        Assert.assertEquals("0", metadata.getVersion());
        Assert.assertEquals("World, null", metadata.getDescription());
        Assert.assertEquals((-0.17578125), metadata.getBounds().getMinimum(0), 0.001);
        Assert.assertEquals((-0.087890625), metadata.getBounds().getMaximum(0), 0.001);
        Assert.assertEquals(0.17578125, metadata.getBounds().getMaximum(1), 0.001);
        Assert.assertEquals(0.087890625, metadata.getBounds().getMinimum(1), 0.001);
        Assert.assertEquals(OVERLAY, metadata.getType());
        Assert.assertEquals(PNG, metadata.getFormat());
        Assert.assertEquals(1, mbtiles.numberOfTiles());
        MBTilesFile.TileIterator tiles = mbtiles.tiles();
        Assert.assertTrue(tiles.hasNext());
        MBTilesTile e = tiles.next();
        Assert.assertEquals(10, e.getZoomLevel());
        Assert.assertEquals(511, e.getTileColumn());
        Assert.assertEquals(512, e.getTileRow());
        Assert.assertNotNull(e.getData());
        tiles.close();
        mbtiles.close();
    }

    @Test
    public void testTileEntriesWithAddTiles() throws Exception {
        // Create a getMap request
        WMSMapContent mapContent = createMapContent(WORLD, LAKES);
        mapContent.getRequest().setBbox(new Envelope((-0.17578125), (-0.087890625), 0.17578125, 0.087890625));
        mapContent.getRequest().getFormatOptions().put("min_zoom", "10");
        mapContent.getRequest().getFormatOptions().put("max_zoom", "11");
        // Create a temporary file for the mbtiles
        File f = File.createTempFile("temp2", ".mbtiles", new File("target"));
        MBTilesFile mbtiles = new MBTilesFile(f);
        mbtiles.init();
        // Add tiles to the file(Internally uses the MBtilesFileWrapper)
        format.addTiles(mbtiles, mapContent.getRequest(), null);
        // Ensure everything is correct
        MBTilesMetadata metadata = mbtiles.loadMetaData();
        Assert.assertEquals("World_Lakes", metadata.getName());
        Assert.assertEquals("0", metadata.getVersion());
        Assert.assertEquals("World, null", metadata.getDescription());
        Assert.assertEquals((-0.17578125), metadata.getBounds().getMinimum(0), 0.001);
        Assert.assertEquals((-0.087890625), metadata.getBounds().getMaximum(0), 0.001);
        Assert.assertEquals(0.17578125, metadata.getBounds().getMaximum(1), 0.001);
        Assert.assertEquals(0.087890625, metadata.getBounds().getMinimum(1), 0.001);
        Assert.assertEquals(OVERLAY, metadata.getType());
        Assert.assertEquals(PNG, metadata.getFormat());
        Assert.assertEquals(1, mbtiles.numberOfTiles());
        MBTilesFile.TileIterator tiles = mbtiles.tiles();
        Assert.assertTrue(tiles.hasNext());
        MBTilesTile e = tiles.next();
        Assert.assertEquals(10, e.getZoomLevel());
        Assert.assertEquals(511, e.getTileColumn());
        Assert.assertEquals(512, e.getTileRow());
        Assert.assertNotNull(e.getData());
        tiles.close();
        // Closure of the files
        mbtiles.close();
        FileUtils.deleteQuietly(f);
    }

    @Test
    public void testDifferentBbox() throws FactoryException, NoSuchAuthorityCodeException {
        // Instantiate a request
        GetMapRequest req = new GetMapRequest();
        // Define CRS
        CoordinateReferenceSystem crs = CRS.decode("EPSG:4326");
        // Create the first bbox
        ReferencedEnvelope bbox1 = new ReferencedEnvelope(0, 1, 0, 1, crs);
        req.setBbox(bbox1);
        req.setCrs(crs);
        ReferencedEnvelope bounds1 = format.bounds(req);
        // Create the second bbox
        ReferencedEnvelope bbox2 = new ReferencedEnvelope(1, 2, 1, 2, crs);
        req.setBbox(bbox2);
        ReferencedEnvelope bounds2 = format.bounds(req);
        // Ensure that the 2 generated bbox are not the same so that they are not cached
        double tolerance = 0.1;
        Assert.assertNotSame(bounds1, bounds2);
        Assert.assertNotEquals(bounds1.getMinX(), bounds2.getMinX(), tolerance);
        Assert.assertNotEquals(bounds1.getMinY(), bounds2.getMinY(), tolerance);
        Assert.assertNotEquals(bounds1.getMaxX(), bounds2.getMaxX(), tolerance);
        Assert.assertNotEquals(bounds1.getMaxY(), bounds2.getMaxY(), tolerance);
    }
}

