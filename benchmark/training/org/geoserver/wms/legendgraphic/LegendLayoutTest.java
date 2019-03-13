/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.legendgraphic;


import MockData.LAKES;
import MockData.NAMED_PLACES;
import MockData.ROAD_SEGMENTS;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.Arrays;
import java.util.HashMap;
import javax.media.jai.PlanarImage;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.StyleInfo;
import org.geoserver.wms.GetLegendGraphicRequest;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.util.FeatureUtilities;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.image.util.ImageUtilities;
import org.geotools.styling.Style;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.coverage.grid.GridCoverage;


public class LegendLayoutTest extends BaseLegendTest {
    /**
     * Tests horizontal layout for raster with RAMP
     */
    @Test
    public void testRampHorizontalRaster() throws Exception {
        Style multipleRulesStyle = getCatalog().getStyleByName("rainfall_ramp").getStyle();
        Assert.assertNotNull(multipleRulesStyle);
        GetLegendGraphicRequest req = new GetLegendGraphicRequest();
        CoverageInfo cInfo = getCatalog().getCoverageByName("world");
        Assert.assertNotNull(cInfo);
        GridCoverage coverage = cInfo.getGridCoverage(null, null);
        try {
            SimpleFeatureCollection feature;
            feature = FeatureUtilities.wrapGridCoverage(((GridCoverage2D) (coverage)));
            req.setLayer(feature.getSchema());
            req.setStyle(multipleRulesStyle);
            final int HEIGHT_HINT = 30;
            req.setHeight(HEIGHT_HINT);
            HashMap legendOptions = new HashMap();
            req.setLegendOptions(legendOptions);
            // use default values for the rest of parameters
            this.legendProducer.buildLegendGraphic(req);
            BufferedImage vImage = ((BufferedImage) (this.legendProducer.buildLegendGraphic(req)));
            // Change layout
            legendOptions = new HashMap();
            legendOptions.put("layout", "horizontal");
            req.setLegendOptions(legendOptions);
            BufferedImage hImage = ((BufferedImage) (this.legendProducer.buildLegendGraphic(req)));
            // Check rotation
            Assert.assertEquals(vImage.getHeight(), hImage.getWidth());
            Assert.assertEquals(vImage.getWidth(), hImage.getHeight());
        } finally {
            RenderedImage ri = coverage.getRenderedImage();
            if (coverage instanceof GridCoverage2D) {
                dispose(true);
            }
            if (ri instanceof PlanarImage) {
                ImageUtilities.disposePlanarImageChain(((PlanarImage) (ri)));
            }
        }
    }

    /**
     * Tests horizontal layout for raster with CLASSES
     */
    @Test
    public void testClassesHorizontalRaster() throws Exception {
        Style multipleRulesStyle = getCatalog().getStyleByName("rainfall_classes_nolabels").getStyle();
        Assert.assertNotNull(multipleRulesStyle);
        GetLegendGraphicRequest req = new GetLegendGraphicRequest();
        CoverageInfo cInfo = getCatalog().getCoverageByName("world");
        Assert.assertNotNull(cInfo);
        GridCoverage coverage = cInfo.getGridCoverage(null, null);
        try {
            SimpleFeatureCollection feature;
            feature = FeatureUtilities.wrapGridCoverage(((GridCoverage2D) (coverage)));
            req.setLayer(feature.getSchema());
            req.setStyle(multipleRulesStyle);
            final int HEIGHT_HINT = 30;
            req.setHeight(HEIGHT_HINT);
            // Change layout
            HashMap legendOptions = new HashMap();
            legendOptions.put("layout", "horizontal");
            legendOptions.put("mx", "0");
            legendOptions.put("my", "0");
            legendOptions.put("dx", "0");
            legendOptions.put("dy", "0");
            legendOptions.put("forceRule", "false");
            req.setLegendOptions(legendOptions);
            BufferedImage image = ((BufferedImage) (this.legendProducer.buildLegendGraphic(req)));
            // Check output
            Assert.assertEquals(HEIGHT_HINT, image.getHeight());
            assertPixel(image, 9, (HEIGHT_HINT / 2), new Color(115, 38, 0));
            assertPixel(image, 230, (HEIGHT_HINT / 2), new Color(38, 115, 0));
        } finally {
            RenderedImage ri = coverage.getRenderedImage();
            if (coverage instanceof GridCoverage2D) {
                dispose(true);
            }
            if (ri instanceof PlanarImage) {
                ImageUtilities.disposePlanarImageChain(((PlanarImage) (ri)));
            }
        }
    }

    /**
     * Tests horizontal layout for raster with CLASSES and columns limits
     */
    @Test
    public void testClassesRasterColumnsLimits() throws Exception {
        Style multipleRulesStyle = getCatalog().getStyleByName("rainfall_classes_nolabels").getStyle();
        Assert.assertNotNull(multipleRulesStyle);
        GetLegendGraphicRequest req = new GetLegendGraphicRequest();
        CoverageInfo cInfo = getCatalog().getCoverageByName("world");
        Assert.assertNotNull(cInfo);
        GridCoverage coverage = cInfo.getGridCoverage(null, null);
        try {
            SimpleFeatureCollection feature;
            feature = FeatureUtilities.wrapGridCoverage(((GridCoverage2D) (coverage)));
            req.setLayer(feature.getSchema());
            req.setStyle(multipleRulesStyle);
            final int HEIGHT_HINT = 30;
            req.setHeight(HEIGHT_HINT);
            // Change layout
            HashMap legendOptions = new HashMap();
            legendOptions.put("layout", "vertical");
            legendOptions.put("columnheight", "85");
            legendOptions.put("columns", "1");
            legendOptions.put("mx", "0");
            legendOptions.put("my", "0");
            legendOptions.put("dx", "0");
            legendOptions.put("dy", "0");
            legendOptions.put("forceRule", "false");
            req.setLegendOptions(legendOptions);
            BufferedImage image = ((BufferedImage) (this.legendProducer.buildLegendGraphic(req)));
            // Check output
            Assert.assertEquals((3 * HEIGHT_HINT), image.getHeight());
            assertPixel(image, 9, 13, new Color(115, 38, 0));
            assertPixel(image, 9, 43, new Color(168, 0, 0));
        } finally {
            RenderedImage ri = coverage.getRenderedImage();
            if (coverage instanceof GridCoverage2D) {
                dispose(true);
            }
            if (ri instanceof PlanarImage) {
                ImageUtilities.disposePlanarImageChain(((PlanarImage) (ri)));
            }
        }
    }

    /**
     * Tests horizontal layout for raster with CLASSES and rows limits
     */
    @Test
    public void testClassesRasterRowsLimits() throws Exception {
        Style multipleRulesStyle = getCatalog().getStyleByName("rainfall_classes_nolabels").getStyle();
        Assert.assertNotNull(multipleRulesStyle);
        GetLegendGraphicRequest req = new GetLegendGraphicRequest();
        CoverageInfo cInfo = getCatalog().getCoverageByName("world");
        Assert.assertNotNull(cInfo);
        GridCoverage coverage = cInfo.getGridCoverage(null, null);
        try {
            SimpleFeatureCollection feature;
            feature = FeatureUtilities.wrapGridCoverage(((GridCoverage2D) (coverage)));
            req.setLayer(feature.getSchema());
            req.setStyle(multipleRulesStyle);
            final int HEIGHT_HINT = 30;
            req.setHeight(HEIGHT_HINT);
            // Change layout
            HashMap legendOptions = new HashMap();
            legendOptions.put("layout", "horizontal");
            legendOptions.put("rowwidth", "100");
            legendOptions.put("rows", "2");
            legendOptions.put("mx", "0");
            legendOptions.put("my", "0");
            legendOptions.put("dx", "0");
            legendOptions.put("dy", "0");
            legendOptions.put("forceRule", "false");
            req.setLegendOptions(legendOptions);
            BufferedImage image = ((BufferedImage) (this.legendProducer.buildLegendGraphic(req)));
            // Check output
            Assert.assertEquals((2 * HEIGHT_HINT), image.getHeight());
            assertPixel(image, 9, 13, new Color(115, 38, 0));
            assertPixel(image, 110, 43, new Color(38, 115, 0));
        } finally {
            RenderedImage ri = coverage.getRenderedImage();
            if (coverage instanceof GridCoverage2D) {
                dispose(true);
            }
            if (ri instanceof PlanarImage) {
                ImageUtilities.disposePlanarImageChain(((PlanarImage) (ri)));
            }
        }
    }

    /**
     * Tests horizontal layout for vector
     */
    @Test
    public void testVectorLayersHorizontal() throws Exception {
        GetLegendGraphicRequest req = new GetLegendGraphicRequest();
        FeatureTypeInfo ftInfo = getCatalog().getFeatureTypeByName(ROAD_SEGMENTS.getNamespaceURI(), ROAD_SEGMENTS.getLocalPart());
        req.setLayer(ftInfo.getFeatureType());
        req.setStyle(getCatalog().getStyleByName(ROAD_SEGMENTS.getLocalPart()).getStyle());
        final int HEIGHT_HINT = 20;
        req.setHeight(HEIGHT_HINT);
        HashMap legendOptions = new HashMap();
        legendOptions.put("layout", "horizontal");
        legendOptions.put("forceLabels", "off");
        req.setLegendOptions(legendOptions);
        this.legendProducer.buildLegendGraphic(req);
        BufferedImage image = ((BufferedImage) (this.legendProducer.buildLegendGraphic(req)));
        Assert.assertEquals(HEIGHT_HINT, image.getHeight());
        assertPixel(image, 10, (HEIGHT_HINT / 2), new Color(192, 160, 0));
        assertPixel(image, 50, (HEIGHT_HINT / 2), new Color(224, 64, 0));
    }

    @Test
    public void testLayerGroupTitles() throws Exception {
        GetLegendGraphicRequest req = new GetLegendGraphicRequest();
        FeatureTypeInfo lakesFt = getCatalog().getFeatureTypeByName(LAKES.getNamespaceURI(), LAKES.getLocalPart());
        FeatureTypeInfo placesFt = getCatalog().getFeatureTypeByName(NAMED_PLACES.getNamespaceURI(), NAMED_PLACES.getLocalPart());
        FeatureTypeInfo roadsFt = getCatalog().getFeatureTypeByName(ROAD_SEGMENTS.getNamespaceURI(), ROAD_SEGMENTS.getLocalPart());
        StyleInfo lakesStyle = getCatalog().getStyleByName(LAKES.getLocalPart());
        StyleInfo placesStyle = getCatalog().getStyleByName(NAMED_PLACES.getLocalPart());
        StyleInfo roadsStyle = getCatalog().getStyleByName(ROAD_SEGMENTS.getLocalPart());
        req.setLayers(Arrays.asList(lakesFt.getFeatureType(), placesFt.getFeatureType(), roadsFt.getFeatureType()));
        req.setStyles(Arrays.asList(lakesStyle.getStyle(), placesStyle.getStyle(), roadsStyle.getStyle()));
        // Each icon will be 20px high (Labels are 14-15px)
        // Lakes have 1 icon, places have 2, and roads have 3
        final int HEIGHT_HINT = 20;
        req.setHeight(HEIGHT_HINT);
        HashMap legendOptions = new HashMap();
        legendOptions.put("forceTitles", "on");
        legendOptions.put("fontName", "Bitstream Vera Sans");
        req.setLegendOptions(legendOptions);
        BufferedImage image = ((BufferedImage) (this.legendProducer.buildLegendGraphic(req)));
        // Title height may vary between test environments
        Assert.assertTrue(((("Expected height >= " + ((HEIGHT_HINT * 6) + 42)) + " but was ") + (image.getHeight())), (((HEIGHT_HINT * 6) + 42) <= (image.getHeight())));
        Assert.assertTrue(((("Expected height <= " + ((HEIGHT_HINT * 6) + 48)) + " but was ") + (image.getHeight())), (((HEIGHT_HINT * 6) + 48) >= (image.getHeight())));
        // Verify the first icon of each layer is in the right place
        assertPixel(image, 10, (14 + (HEIGHT_HINT / 2)), new Color(64, 64, 192));
        assertPixel(image, 10, ((28 + HEIGHT_HINT) + (HEIGHT_HINT / 2)), new Color(170, 170, 170));
        assertPixel(image, 10, ((42 + (3 * HEIGHT_HINT)) + (HEIGHT_HINT / 2)), new Color(192, 160, 0));
        legendOptions.put("forceTitles", "off");
        req.setLegendOptions(legendOptions);
        image = ((BufferedImage) (this.legendProducer.buildLegendGraphic(req)));
        Assert.assertEquals((HEIGHT_HINT * 6), image.getHeight());
        // Verify the first icon of each layer is in the right place
        assertPixel(image, 10, (HEIGHT_HINT / 2), new Color(64, 64, 192));
        assertPixel(image, 10, (HEIGHT_HINT + (HEIGHT_HINT / 2)), new Color(170, 170, 170));
        assertPixel(image, 10, ((3 * HEIGHT_HINT) + (HEIGHT_HINT / 2)), new Color(192, 160, 0));
    }

    @Test
    public void testLayerGroupLabels() throws Exception {
        GetLegendGraphicRequest req = new GetLegendGraphicRequest();
        FeatureTypeInfo lakesFt = getCatalog().getFeatureTypeByName(LAKES.getNamespaceURI(), LAKES.getLocalPart());
        FeatureTypeInfo placesFt = getCatalog().getFeatureTypeByName(NAMED_PLACES.getNamespaceURI(), NAMED_PLACES.getLocalPart());
        FeatureTypeInfo roadsFt = getCatalog().getFeatureTypeByName(ROAD_SEGMENTS.getNamespaceURI(), ROAD_SEGMENTS.getLocalPart());
        StyleInfo lakesStyle = getCatalog().getStyleByName(LAKES.getLocalPart());
        StyleInfo placesStyle = getCatalog().getStyleByName(NAMED_PLACES.getLocalPart());
        StyleInfo roadsStyle = getCatalog().getStyleByName(ROAD_SEGMENTS.getLocalPart());
        req.setLayers(Arrays.asList(lakesFt.getFeatureType(), placesFt.getFeatureType(), roadsFt.getFeatureType()));
        req.setStyles(Arrays.asList(lakesStyle.getStyle(), placesStyle.getStyle(), roadsStyle.getStyle()));
        // Each icon will be 20px high (Labels are 14-15px)
        // Lakes have 1 icon, places have 2, and roads have 3
        final int HEIGHT_HINT = 20;
        req.setHeight(HEIGHT_HINT);
        HashMap legendOptions = new HashMap();
        legendOptions.put("forceTitles", "off");
        legendOptions.put("forceLabels", "on");
        req.setLegendOptions(legendOptions);
        BufferedImage image = ((BufferedImage) (this.legendProducer.buildLegendGraphic(req)));
        Assert.assertEquals((HEIGHT_HINT * 6), image.getHeight());
        Assert.assertTrue(("Expected witdh > 40 but was " + (image.getWidth())), (40 < (image.getWidth())));
        // Verify the first icon of each layer is in the right place
        assertPixel(image, 10, (HEIGHT_HINT / 2), new Color(64, 64, 192));
        assertPixel(image, 10, (HEIGHT_HINT + (HEIGHT_HINT / 2)), new Color(170, 170, 170));
        assertPixel(image, 10, ((3 * HEIGHT_HINT) + (HEIGHT_HINT / 2)), new Color(192, 160, 0));
        legendOptions.put("forceTitles", "off");
        legendOptions.put("forceLabels", "off");
        req.setLegendOptions(legendOptions);
        image = ((BufferedImage) (this.legendProducer.buildLegendGraphic(req)));
        Assert.assertEquals((HEIGHT_HINT * 6), image.getHeight());
        // With no titles and no labels, legend should be as wide as a single icon
        Assert.assertEquals(24, image.getWidth());
        // Verify the first icon of each layer is in the right place
        assertPixel(image, 10, (HEIGHT_HINT / 2), new Color(64, 64, 192));
        assertPixel(image, 10, (HEIGHT_HINT + (HEIGHT_HINT / 2)), new Color(170, 170, 170));
        assertPixel(image, 10, ((3 * HEIGHT_HINT) + (HEIGHT_HINT / 2)), new Color(192, 160, 0));
    }

    @Test
    public void testLayerGroupLayout() throws Exception {
        GetLegendGraphicRequest req = new GetLegendGraphicRequest();
        FeatureTypeInfo lakesFt = getCatalog().getFeatureTypeByName(LAKES.getNamespaceURI(), LAKES.getLocalPart());
        FeatureTypeInfo placesFt = getCatalog().getFeatureTypeByName(NAMED_PLACES.getNamespaceURI(), NAMED_PLACES.getLocalPart());
        FeatureTypeInfo roadsFt = getCatalog().getFeatureTypeByName(ROAD_SEGMENTS.getNamespaceURI(), ROAD_SEGMENTS.getLocalPart());
        StyleInfo lakesStyle = getCatalog().getStyleByName(LAKES.getLocalPart());
        StyleInfo placesStyle = getCatalog().getStyleByName(NAMED_PLACES.getLocalPart());
        StyleInfo roadsStyle = getCatalog().getStyleByName(ROAD_SEGMENTS.getLocalPart());
        req.setLayers(Arrays.asList(lakesFt.getFeatureType(), placesFt.getFeatureType(), roadsFt.getFeatureType()));
        req.setStyles(Arrays.asList(lakesStyle.getStyle(), placesStyle.getStyle(), roadsStyle.getStyle()));
        // Each icon will be 20px high
        // Lakes have 1 icon, places have 2, and roads have 3
        final int HEIGHT_HINT = 20;
        req.setHeight(HEIGHT_HINT);
        // Test layout with grouplayout=VERTICAL
        HashMap legendOptions = new HashMap();
        legendOptions.put("forceTitles", "off");
        legendOptions.put("forceLabels", "off");
        legendOptions.put("layout", "VERTICAL");
        legendOptions.put("grouplayout", "VERTICAL");
        req.setLegendOptions(legendOptions);
        BufferedImage image = ((BufferedImage) (this.legendProducer.buildLegendGraphic(req)));
        /* Legend layout:

        L1
        P1
        P2
        R1
        R2
        R3
         */
        Assert.assertEquals((6 * HEIGHT_HINT), image.getHeight());
        Assert.assertEquals((HEIGHT_HINT + 4), image.getWidth());
        // Verify the first icon of each layer is in the right place
        assertPixel(image, 10, (HEIGHT_HINT / 2), new Color(64, 64, 192));
        assertPixel(image, 10, (HEIGHT_HINT + (HEIGHT_HINT / 2)), new Color(170, 170, 170));
        assertPixel(image, 10, ((3 * HEIGHT_HINT) + (HEIGHT_HINT / 2)), new Color(192, 160, 0));
        legendOptions.put("forceTitles", "off");
        legendOptions.put("forceLabels", "off");
        legendOptions.put("layout", "HORIZONTAL");
        legendOptions.put("grouplayout", "VERTICAL");
        req.setLegendOptions(legendOptions);
        image = ((BufferedImage) (this.legendProducer.buildLegendGraphic(req)));
        /* Legend layout:

        L1
        P1 P2
        R1 R2 R3
         */
        Assert.assertEquals((3 * HEIGHT_HINT), image.getHeight());
        Assert.assertEquals(((3 * HEIGHT_HINT) + 4), image.getWidth());
        // Verify the first icon of each layer is in the right place
        assertPixel(image, 10, (HEIGHT_HINT / 2), new Color(64, 64, 192));
        assertPixel(image, 10, (HEIGHT_HINT + (HEIGHT_HINT / 2)), new Color(170, 170, 170));
        assertPixel(image, 10, ((2 * HEIGHT_HINT) + (HEIGHT_HINT / 2)), new Color(192, 160, 0));
        // Test layout with grouplayout=HORIZONTAL
        legendOptions.put("forceTitles", "off");
        legendOptions.put("forceLabels", "off");
        legendOptions.put("layout", "VERTICAL");
        legendOptions.put("grouplayout", "HORIZONTAL");
        req.setLegendOptions(legendOptions);
        image = ((BufferedImage) (this.legendProducer.buildLegendGraphic(req)));
        /* Legend layout:

        L1 P1 R1
           P2 R2
              R3
         */
        Assert.assertEquals((3 * HEIGHT_HINT), image.getHeight());
        Assert.assertEquals(((3 * HEIGHT_HINT) + 6), image.getWidth());
        // Verify the first icon of each layer is in the right place
        assertPixel(image, (HEIGHT_HINT / 2), 10, new Color(64, 64, 192));
        assertPixel(image, (HEIGHT_HINT + (HEIGHT_HINT / 2)), 10, new Color(170, 170, 170));
        assertPixel(image, ((2 * HEIGHT_HINT) + (HEIGHT_HINT / 2)), 10, new Color(192, 160, 0));
        legendOptions.put("forceTitles", "off");
        legendOptions.put("forceLabels", "off");
        legendOptions.put("layout", "HORIZONTAL");
        legendOptions.put("grouplayout", "HORIZONTAL");
        req.setLegendOptions(legendOptions);
        image = ((BufferedImage) (this.legendProducer.buildLegendGraphic(req)));
        /* Legend layout:

        L1 P1 P2 R1 R2 R3
         */
        Assert.assertEquals(HEIGHT_HINT, image.getHeight());
        Assert.assertEquals(((6 * HEIGHT_HINT) + 6), image.getWidth());
        // Verify the first icon of each layer is in the right place
        assertPixel(image, (HEIGHT_HINT / 2), 10, new Color(64, 64, 192));
        assertPixel(image, (HEIGHT_HINT + (HEIGHT_HINT / 2)), 10, new Color(170, 170, 170));
        assertPixel(image, ((3 * HEIGHT_HINT) + (HEIGHT_HINT / 2)), 10, new Color(192, 160, 0));
    }
}

