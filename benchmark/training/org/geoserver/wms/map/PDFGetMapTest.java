/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.map;


import java.io.File;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.graphics.pattern.PDTilingPattern;
import org.geoserver.wms.WMSTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import static PDFMapResponse.ENCODE_TILING_PATTERNS;


public class PDFGetMapTest extends WMSTestSupport {
    String bbox = "-1.5,-0.5,1.5,1.5";

    String layers = getLayerId(MockData.BASIC_POLYGONS);

    String requestBase = (((("wms?bbox=" + (bbox)) + "&layers=") + (layers)) + "&Format=application/pdf&request=GetMap") + "&width=300&height=300&srs=EPSG:4326";

    static boolean tilingPatterDefault = ENCODE_TILING_PATTERNS;

    @Test
    public void testBasicPolygonMap() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((requestBase) + "&styles="));
        Assert.assertEquals("application/pdf", response.getContentType());
        PDTilingPattern tilingPattern = getTilingPattern(response.getContentAsByteArray());
        Assert.assertNull(tilingPattern);
    }

    @Test
    public void testSvgFillOptimization() throws Exception {
        // get a single polygon to ease testing
        MockHttpServletResponse response = getAsServletResponse(((requestBase) + "&styles=burg-fill&featureId=BasicPolygons.1107531493630"));
        Assert.assertEquals("application/pdf", response.getContentType());
        PDTilingPattern tilingPattern = getTilingPattern(response.getContentAsByteArray());
        Assert.assertNotNull(tilingPattern);
        Assert.assertEquals(20, tilingPattern.getXStep(), 0.0);
        Assert.assertEquals(20, tilingPattern.getYStep(), 0.0);
    }

    @Test
    public void testSvgFillOptimizationDisabled() throws Exception {
        ENCODE_TILING_PATTERNS = false;
        // get a single polygon to ease testing
        MockHttpServletResponse response = getAsServletResponse(((requestBase) + "&styles=burg-fill&featureId=BasicPolygons.1107531493630"));
        Assert.assertEquals("application/pdf", response.getContentType());
        // the tiling pattern encoding has been disabled
        PDTilingPattern tilingPattern = getTilingPattern(response.getContentAsByteArray());
        Assert.assertNull(tilingPattern);
    }

    @Test
    public void testTriangleFillOptimization() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((requestBase) + "&styles=triangle-fill&featureId=BasicPolygons.1107531493630"));
        Assert.assertEquals("application/pdf", response.getContentType());
        File file = new File("./target/test.pdf");
        FileUtils.writeByteArrayToFile(file, response.getContentAsByteArray());
        PDTilingPattern tilingPattern = getTilingPattern(response.getContentAsByteArray());
        Assert.assertNotNull(tilingPattern);
        Assert.assertEquals(20, tilingPattern.getXStep(), 0.0);
        Assert.assertEquals(20, tilingPattern.getYStep(), 0.0);
    }

    @Test
    public void testHatchFillOptimization() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((requestBase) + "&styles=hatch-fill&featureId=BasicPolygons.1107531493630"));
        Assert.assertEquals("application/pdf", response.getContentType());
        // for hatches we keep the existing "set of parallel lines" optimization approach, need to
        // determine
        // if we want to remove it or not yet
        PDTilingPattern tilingPattern = getTilingPattern(response.getContentAsByteArray());
        Assert.assertNull(tilingPattern);
    }
}

