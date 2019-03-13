/**
 * (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.map;


import MockData.TASMANIA_DEM;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.media.jai.RenderedOp;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.wms.WMSMapContent;
import org.geoserver.wms.WMSTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.opengis.coverage.grid.GridEnvelope;


/**
 * Unit test for very slow WMS GetMap response times when the requested bounding box is much smaller
 * than the resolution of the raster data and advanced projection handling is disabled.
 */
public class TinyRasterBoundingBoxTest extends WMSTestSupport {
    private WMSMapContent map;

    private BufferedImage image;

    private RenderedOp op;

    @Test
    public void testTinyRasterBboxContained() throws Exception {
        CoverageInfo coverageInfo = addRasterToMap(TASMANIA_DEM);
        Envelope env = coverageInfo.boundingBox();
        Coordinate center = env.centre();
        GridEnvelope range = coverageInfo.getGrid().getGridRange();
        double offset = (((env.getMaxX()) - (env.getMinX())) / (range.getSpan(0))) / 10.0;
        Rectangle imageBounds = produceMap(((center.x) + offset), ((center.x) + (2 * offset)), ((center.y) + offset), ((center.y) + (2 * offset)));
        assertNotBlank("testTinyRasterBboxContained", this.image);
        Assert.assertEquals("Mosaic", this.op.getOperationName());
        Rectangle roiBounds = getRoiBounds();
        Assert.assertTrue(((("Expected " + imageBounds) + " to contain ") + roiBounds), imageBounds.contains(roiBounds));
    }

    @Test
    public void testTinyRasterBboxIntersection() throws Exception {
        CoverageInfo coverageInfo = addRasterToMap(TASMANIA_DEM);
        Envelope env = coverageInfo.boundingBox();
        GridEnvelope range = coverageInfo.getGrid().getGridRange();
        double offset = (((env.getMaxX()) - (env.getMinX())) / (range.getSpan(0))) / 20.0;
        Rectangle imageBounds = produceMap(((env.getMinX()) - offset), ((env.getMinX()) + offset), ((env.getMaxY()) - offset), ((env.getMaxY()) + offset));
        assertNotBlank("testTinyRasterBboxIntersection", this.image);
        Assert.assertEquals("Mosaic", this.op.getOperationName());
        Rectangle roiBounds = getRoiBounds();
        Assert.assertTrue(((("Expected " + imageBounds) + " to contain ") + roiBounds), imageBounds.contains(roiBounds));
    }

    @Test
    public void testTinyRasterBboxNoIntersection() throws Exception {
        CoverageInfo coverageInfo = addRasterToMap(TASMANIA_DEM);
        Envelope env = coverageInfo.boundingBox();
        GridEnvelope range = coverageInfo.getGrid().getGridRange();
        double offset = (((env.getMaxX()) - (env.getMinX())) / (range.getSpan(0))) / 10.0;
        Rectangle imageBounds = produceMap(((env.getMaxX()) + offset), ((env.getMaxX()) + (2 * offset)), ((env.getMinY()) - (2 * offset)), ((env.getMinY()) - offset));
        assertNotBlank("testTinyRasterBboxNoIntersection", this.image);
        Assert.assertEquals("Mosaic", this.op.getOperationName());
        Rectangle roiBounds = getRoiBounds();
        Assert.assertTrue(((("Expected " + imageBounds) + " to contain ") + roiBounds), imageBounds.contains(roiBounds));
    }
}

