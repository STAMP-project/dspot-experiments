/**
 * (c) 2014 - 2015 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.map;


import AbstractGridFormat.BANDS;
import Interpolation.INTERP_BICUBIC;
import Interpolation.INTERP_NEAREST;
import MockData.BASIC_POLYGONS;
import MockData.BRIDGES;
import MockData.BUILDINGS;
import MockData.CITE_URI;
import MockData.DIVIDED_ROUTES;
import MockData.FORESTS;
import MockData.LAKES;
import MockData.MAP_NEATLINE;
import MockData.NAMED_PLACES;
import MockData.PONDS;
import MockData.ROAD_SEGMENTS;
import MockData.STREAMS;
import MockData.TASMANIA_DEM;
import SystemTestData.MULTIBAND;
import WMSInterpolation.Bicubic;
import WMSInterpolation.Bilinear;
import WMSInterpolation.Nearest;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;
import javax.media.jai.Interpolation;
import javax.media.jai.RenderedOp;
import javax.xml.namespace.QName;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.CatalogBuilder;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.catalog.CoverageStoreInfo;
import org.geoserver.catalog.CoverageView;
import org.geoserver.catalog.CoverageView.CompositionType;
import org.geoserver.catalog.CoverageView.CoverageBand;
import org.geoserver.catalog.CoverageView.InputCoverageBand;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.StyleInfo;
import org.geoserver.data.test.MockData;
import org.geoserver.platform.ServiceException;
import org.geoserver.wms.GetMapRequest;
import org.geoserver.wms.MapLayerInfo;
import org.geoserver.wms.WMS;
import org.geoserver.wms.WMSMapContent;
import org.geoserver.wms.WMSPartialMapException;
import org.geoserver.wms.WMSTestSupport;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.data.FeatureSource;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.filter.IllegalFilterException;
import org.geotools.gce.imagemosaic.ImageMosaicReader;
import org.geotools.geometry.jts.LiteShape2;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.image.test.ImageAssert;
import org.geotools.image.util.ImageUtilities;
import org.geotools.map.Layer;
import org.geotools.parameter.Parameter;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.renderer.lite.LabelCache;
import org.geotools.styling.ChannelSelection;
import org.geotools.styling.ChannelSelectionImpl;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.SelectedChannelType;
import org.geotools.styling.SelectedChannelTypeImpl;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.TextSymbolizer;
import org.geotools.util.NumberRange;
import org.geotools.util.URLs;
import org.geotools.util.logging.Logging;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Envelope;
import org.opengis.feature.Feature;
import org.opengis.feature.IllegalAttributeException;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;


public class RenderedImageMapOutputFormatTest extends WMSTestSupport {
    public static QName TAZ_BYTE = new QName(MockData.WCS_URI, "tazbyte", MockData.WCS_PREFIX);

    private static final Logger LOGGER = Logging.getLogger(RenderedImageMapOutputFormatTest.class.getPackage().getName());

    private RenderedImageMapOutputFormat rasterMapProducer;

    private String mapFormat = "image/gif";

    private static final ThreadLocal<Boolean> usedCustomLabelCache = new ThreadLocal<Boolean>();

    public static class CustomLabelCache implements LabelCache {
        public CustomLabelCache() {
        }

        @Override
        public void clear() {
        }

        @Override
        public void clear(String arg0) {
        }

        @Override
        public void disableLayer(String arg0) {
        }

        @Override
        public void enableLayer(String arg0) {
        }

        @Override
        public void end(Graphics2D arg0, Rectangle arg1) {
            RenderedImageMapOutputFormatTest.usedCustomLabelCache.set(true);
        }

        @Override
        public void endLayer(String arg0, Graphics2D arg1, Rectangle arg2) {
        }

        @Override
        public List orderedLabels() {
            return null;
        }

        @Override
        public void put(Rectangle2D arg0) {
        }

        @Override
        public void put(String arg0, TextSymbolizer arg1, Feature arg2, LiteShape2 arg3, NumberRange<Double> arg4) {
        }

        @Override
        public void start() {
        }

        @Override
        public void startLayer(String arg0) {
        }

        @Override
        public void stop() {
        }
    }

    @Test
    public void testSimpleGetMapQuery() throws Exception {
        Catalog catalog = getCatalog();
        final FeatureSource fs = catalog.getFeatureTypeByName(BASIC_POLYGONS.getPrefix(), BASIC_POLYGONS.getLocalPart()).getFeatureSource(null, null);
        final Envelope env = fs.getBounds();
        RenderedImageMapOutputFormatTest.LOGGER.info(("about to create map ctx for BasicPolygons with bounds " + env));
        GetMapRequest request = new GetMapRequest();
        final WMSMapContent map = new WMSMapContent();
        map.getViewport().setBounds(new ReferencedEnvelope(env, DefaultGeographicCRS.WGS84));
        map.setMapWidth(300);
        map.setMapHeight(300);
        map.setBgColor(Color.red);
        map.setTransparent(false);
        map.setRequest(request);
        StyleInfo styleByName = catalog.getStyleByName("Default");
        Style basicStyle = styleByName.getStyle();
        map.addLayer(new org.geotools.map.FeatureLayer(fs, basicStyle));
        request.setFormat(getMapFormat());
        RenderedImageMap imageMap = this.rasterMapProducer.produceMap(map);
        BufferedImage image = ((BufferedImage) (imageMap.getImage()));
        imageMap.dispose();
        assertNotBlank("testSimpleGetMapQuery", image);
    }

    /**
     * Test to make sure the "direct" raster path and the "nondirect" raster path produce matching
     * results. This test was originally created after fixes to GEOS-7270 where there were issues
     * with images generated during the direct raster path but not in the normal path, stemming from
     * not setting the background color the same way
     */
    @Test
    public void testDirectVsNonDirectRasterRender() throws Exception {
        Catalog catalog = getCatalog();
        CoverageInfo ci = catalog.getCoverageByName(MULTIBAND.getPrefix(), MULTIBAND.getLocalPart());
        final Envelope env = ci.boundingBox();
        RenderedImageMapOutputFormatTest.LOGGER.info(("about to create map ctx for BasicPolygons with bounds " + env));
        GetMapRequest request = new GetMapRequest();
        CoordinateReferenceSystem crs = DefaultGeographicCRS.WGS84;
        ReferencedEnvelope bbox = new ReferencedEnvelope(new Envelope((-116.90673461649858), (-114.30988665660261), 32.070937282184026, 33.8903284734844), crs);
        request.setBbox(bbox);
        request.setSRS("urn:x-ogc:def:crs:EPSG:4326");
        request.setFormat("image/png");
        final WMSMapContent map = new WMSMapContent(request);
        map.setMapWidth(300);
        map.setMapHeight(300);
        map.setBgColor(Color.red);
        map.setTransparent(false);
        map.getViewport().setBounds(bbox);
        StyleBuilder builder = new StyleBuilder();
        GridCoverage2DReader reader = ((GridCoverage2DReader) (ci.getGridCoverageReader(null, null)));
        reader.getCoordinateReferenceSystem();
        Layer l = new org.geoserver.wms.CachedGridReaderLayer(reader, builder.createStyle(builder.createRasterSymbolizer()));
        map.addLayer(l);
        RenderedImageMap imageMap = this.rasterMapProducer.produceMap(map);
        ImageAssert.assertEquals(new File("src/test/resources/org/geoserver/wms/map/direct-raster-expected.tif"), imageMap.getImage(), 0);
        imageMap.dispose();
    }

    @Test
    public void testTimeoutOption() throws Exception {
        Catalog catalog = getCatalog();
        GetMapRequest request = new GetMapRequest();
        final WMSMapContent map = new WMSMapContent();
        StyleInfo styleByName = catalog.getStyleByName("Default");
        Style basicStyle = styleByName.getStyle();
        // Build up a complex map so that we can reasonably guarantee a 1 ms timout
        SimpleFeatureSource fs = ((SimpleFeatureSource) (catalog.getFeatureTypeByName(BASIC_POLYGONS.getPrefix(), BASIC_POLYGONS.getLocalPart()).getFeatureSource(null, null)));
        Envelope env = fs.getBounds();
        SimpleFeatureCollection features = fs.getFeatures();
        SimpleFeatureCollection delayedCollection = new DelayedFeatureCollection(features, 50);
        map.addLayer(new org.geotools.map.FeatureLayer(delayedCollection, basicStyle));
        RenderedImageMapOutputFormatTest.LOGGER.info(((("about to create map ctx for " + (map.layers().size())) + " layers with bounds ") + env));
        map.getViewport().setBounds(new ReferencedEnvelope(env, DefaultGeographicCRS.WGS84));
        map.setMapWidth(1000);
        map.setMapHeight(1000);
        map.setRequest(request);
        request.setFormat(getMapFormat());
        Map formatOptions = new HashMap();
        // 1 ms timeout
        formatOptions.put("timeout", 1);
        request.setFormatOptions(formatOptions);
        try {
            RenderedImageMap imageMap = this.rasterMapProducer.produceMap(map);
            Assert.fail("Timeout was not reached");
        } catch (ServiceException e) {
            Assert.assertTrue(e.getMessage().startsWith("This request used more time than allowed"));
        }
        // Test partial image exception format
        Map rawKvp = new HashMap();
        rawKvp.put("EXCEPTIONS", "PARTIALMAP");
        request.setRawKvp(rawKvp);
        try {
            RenderedImageMap imageMap = this.rasterMapProducer.produceMap(map);
            Assert.fail("Timeout was not reached");
        } catch (ServiceException e) {
            Assert.assertTrue((e instanceof WMSPartialMapException));
            Assert.assertTrue(e.getCause().getMessage().startsWith("This request used more time than allowed"));
            RenderedImageMap partialMap = ((RenderedImageMap) (getMap()));
            Assert.assertNotNull(partialMap);
            Assert.assertNotNull(partialMap.getImage());
        }
    }

    @Test
    public void testDefaultStyle() throws Exception {
        List<FeatureTypeInfo> typeInfos = getCatalog().getFeatureTypes();
        for (FeatureTypeInfo info : typeInfos) {
            if ((info.getQualifiedName().getNamespaceURI().equals(CITE_URI)) && ((info.getFeatureType().getGeometryDescriptor()) != null))
                testDefaultStyle(info.getFeatureSource(null, null));

        }
    }

    @Test
    public void testBlueLake() throws IOException, Exception, IllegalFilterException {
        final Catalog catalog = getCatalog();
        FeatureTypeInfo typeInfo = catalog.getFeatureTypeByName(LAKES.getNamespaceURI(), LAKES.getLocalPart());
        Envelope env = typeInfo.getFeatureSource(null, null).getBounds();
        double shift = (env.getWidth()) / 6;
        env = new Envelope(((env.getMinX()) - shift), ((env.getMaxX()) + shift), ((env.getMinY()) - shift), ((env.getMaxY()) + shift));
        GetMapRequest request = new GetMapRequest();
        final WMSMapContent map = new WMSMapContent();
        int w = 400;
        int h = ((int) (Math.round((((env.getHeight()) * w) / (env.getWidth())))));
        map.setMapWidth(w);
        map.setMapHeight(h);
        map.setBgColor(WMSTestSupport.BG_COLOR);
        map.setTransparent(true);
        map.setRequest(request);
        addToMap(map, FORESTS);
        addToMap(map, LAKES);
        addToMap(map, STREAMS);
        addToMap(map, NAMED_PLACES);
        addToMap(map, ROAD_SEGMENTS);
        addToMap(map, PONDS);
        addToMap(map, BUILDINGS);
        addToMap(map, DIVIDED_ROUTES);
        addToMap(map, BRIDGES);
        addToMap(map, MAP_NEATLINE);
        map.getViewport().setBounds(new ReferencedEnvelope(env, DefaultGeographicCRS.WGS84));
        request.setFormat(getMapFormat());
        RenderedImageMap imageMap = this.rasterMapProducer.produceMap(map);
        BufferedImage image = ((BufferedImage) (imageMap.getImage()));
        imageMap.dispose();
        assertNotBlank("testBlueLake", image);
    }

    @Test
    public void testCustomLabelCache() throws IOException {
        final Catalog catalog = getCatalog();
        FeatureTypeInfo typeInfo = catalog.getFeatureTypeByName(LAKES.getNamespaceURI(), LAKES.getLocalPart());
        Envelope env = typeInfo.getFeatureSource(null, null).getBounds();
        double shift = (env.getWidth()) / 6;
        env = new Envelope(((env.getMinX()) - shift), ((env.getMaxX()) + shift), ((env.getMinY()) - shift), ((env.getMaxY()) + shift));
        GetMapRequest request = new GetMapRequest();
        final WMSMapContent map = new WMSMapContent();
        int w = 400;
        int h = ((int) (Math.round((((env.getHeight()) * w) / (env.getWidth())))));
        map.setMapWidth(w);
        map.setMapHeight(h);
        map.setBgColor(WMSTestSupport.BG_COLOR);
        map.setTransparent(true);
        map.setRequest(request);
        addToMap(map, FORESTS);
        addToMap(map, LAKES);
        addToMap(map, STREAMS);
        addToMap(map, NAMED_PLACES);
        addToMap(map, ROAD_SEGMENTS);
        addToMap(map, PONDS);
        addToMap(map, BUILDINGS);
        addToMap(map, DIVIDED_ROUTES);
        addToMap(map, BRIDGES);
        addToMap(map, MAP_NEATLINE);
        map.getViewport().setBounds(new ReferencedEnvelope(env, DefaultGeographicCRS.WGS84));
        request.setFormat(getMapFormat());
        this.rasterMapProducer.setLabelCache(new Function<WMSMapContent, LabelCache>() {
            @Override
            public LabelCache apply(WMSMapContent mapContent) {
                return new RenderedImageMapOutputFormatTest.CustomLabelCache();
            }
        });
        RenderedImageMap imageMap = this.rasterMapProducer.produceMap(map);
        BufferedImage image = ((BufferedImage) (imageMap.getImage()));
        imageMap.dispose();
        Assert.assertTrue(RenderedImageMapOutputFormatTest.usedCustomLabelCache.get());
        assertNotBlank("testBlueLake", image);
    }

    @Test
    public void testInterpolations() throws IOException, Exception, IllegalFilterException {
        final Catalog catalog = getCatalog();
        CoverageInfo coverageInfo = catalog.getCoverageByName(TASMANIA_DEM.getNamespaceURI(), TASMANIA_DEM.getLocalPart());
        Envelope env = coverageInfo.boundingBox();
        double shift = (env.getWidth()) / 6;
        env = new Envelope(((env.getMinX()) - shift), ((env.getMaxX()) + shift), ((env.getMinY()) - shift), ((env.getMaxY()) + shift));
        GetMapRequest request = new GetMapRequest();
        WMSMapContent map = new WMSMapContent();
        int w = 400;
        int h = ((int) (Math.round((((env.getHeight()) * w) / (env.getWidth())))));
        map.setMapWidth(w);
        map.setMapHeight(h);
        map.setBgColor(WMSTestSupport.BG_COLOR);
        map.setTransparent(true);
        map.setRequest(request);
        addRasterToMap(map, TASMANIA_DEM);
        map.getViewport().setBounds(new ReferencedEnvelope(env, DefaultGeographicCRS.WGS84));
        request.setInterpolations(Arrays.asList(Interpolation.getInstance(INTERP_NEAREST)));
        request.setFormat(getMapFormat());
        RenderedImageMap imageMap = this.rasterMapProducer.produceMap(map);
        RenderedOp op = ((RenderedOp) (imageMap.getImage()));
        BufferedImage imageNearest = op.getAsBufferedImage();
        imageMap.dispose();
        assertNotBlank("testInterpolationsNearest", imageNearest);
        request = new GetMapRequest();
        map = new WMSMapContent();
        map.setMapWidth(w);
        map.setMapHeight(h);
        map.setBgColor(WMSTestSupport.BG_COLOR);
        map.setTransparent(true);
        map.setRequest(request);
        addRasterToMap(map, TASMANIA_DEM);
        map.getViewport().setBounds(new ReferencedEnvelope(env, DefaultGeographicCRS.WGS84));
        request.setInterpolations(Arrays.asList(Interpolation.getInstance(INTERP_BICUBIC)));
        request.setFormat(getMapFormat());
        imageMap = this.rasterMapProducer.produceMap(map);
        op = ((RenderedOp) (imageMap.getImage()));
        BufferedImage imageBicubic = op.getAsBufferedImage();
        imageMap.dispose();
        assertNotBlank("testInterpolationsBicubic", imageBicubic);
        // test some sample pixels to check rendering is different using different interpolations
        Assert.assertNotEquals(getPixelColor(imageNearest, 160, 160).getRGB(), getPixelColor(imageBicubic, 160, 160).getRGB());
        Assert.assertNotEquals(getPixelColor(imageNearest, 300, 450).getRGB(), getPixelColor(imageBicubic, 300, 450).getRGB());
    }

    @Test
    public void testInterpolationFromLayerConfig() throws IOException, Exception, IllegalFilterException {
        final Catalog catalog = getCatalog();
        LayerInfo layerInfo = catalog.getLayerByName(TASMANIA_DEM.getLocalPart());
        MapLayerInfo mapLayer = new MapLayerInfo(layerInfo);
        Assert.assertNull(layerInfo.getDefaultWMSInterpolationMethod());
        Envelope env = layerInfo.getResource().boundingBox();
        double shift = (env.getWidth()) / 6;
        env = new Envelope(((env.getMinX()) - shift), ((env.getMaxX()) + shift), ((env.getMinY()) - shift), ((env.getMaxY()) + shift));
        // set Nearest Neighbor interpolation on layer
        GetMapRequest request = new GetMapRequest();
        request.setFormat(getMapFormat());
        request.setLayers(Arrays.asList(mapLayer));
        layerInfo.setDefaultWMSInterpolationMethod(Nearest);
        Assert.assertEquals(Nearest, request.getLayers().get(0).getLayerInfo().getDefaultWMSInterpolationMethod());
        Assert.assertTrue(request.getInterpolations().isEmpty());
        WMSMapContent map = createWMSMap(env);
        map.setRequest(request);
        RenderedImageMap imageMap = this.rasterMapProducer.produceMap(map);
        RenderedOp op = ((RenderedOp) (imageMap.getImage()));
        BufferedImage imageNearest = op.getAsBufferedImage();
        imageMap.dispose();
        assertNotBlank("testInterpolationsNearest", imageNearest);
        // set Bicubic interpolation on layer
        request = new GetMapRequest();
        request.setFormat(getMapFormat());
        request.setLayers(Arrays.asList(mapLayer));
        layerInfo.setDefaultWMSInterpolationMethod(Bicubic);
        Assert.assertEquals(Bicubic, request.getLayers().get(0).getLayerInfo().getDefaultWMSInterpolationMethod());
        Assert.assertTrue(request.getInterpolations().isEmpty());
        map = createWMSMap(env);
        map.setRequest(request);
        imageMap = this.rasterMapProducer.produceMap(map);
        op = ((RenderedOp) (imageMap.getImage()));
        BufferedImage imageBicubic = op.getAsBufferedImage();
        imageMap.dispose();
        assertNotBlank("testInterpolationsBicubic", imageBicubic);
        // test some sample pixels to check rendering is different using different interpolations
        Assert.assertNotEquals(getPixelColor(imageNearest, 160, 160).getRGB(), getPixelColor(imageBicubic, 160, 160).getRGB());
        Assert.assertNotEquals(getPixelColor(imageNearest, 300, 450).getRGB(), getPixelColor(imageBicubic, 300, 450).getRGB());
        // check also the *non* direct raster render path
        request = new GetMapRequest();
        request.setFormat(getMapFormat());
        // adding layer twice on purpose to disable direct raster render
        request.setLayers(Arrays.asList(mapLayer, mapLayer));
        layerInfo.setDefaultWMSInterpolationMethod(Bicubic);
        Assert.assertEquals(Bicubic, request.getLayers().get(0).getLayerInfo().getDefaultWMSInterpolationMethod());
        Assert.assertTrue(request.getInterpolations().isEmpty());
        map = createWMSMap(env);
        map.setRequest(request);
        // adding layer twice on purpose to disable direct raster render
        addRasterToMap(map, TASMANIA_DEM);
        imageMap = this.rasterMapProducer.produceMap(map);
        checkByLayerInterpolation(imageMap, Interpolation.getInstance(INTERP_BICUBIC));
        // interpolation method specified in the request overrides service and layer configuration
        request = new GetMapRequest();
        // request says "Bicubic"
        request.setInterpolations(Arrays.asList(Interpolation.getInstance(INTERP_BICUBIC)));
        request.setFormat(getMapFormat());
        // adding layer twice on purpose to disable direct raster render
        request.setLayers(Arrays.asList(mapLayer, mapLayer));
        // layer config says "Bilinear"
        layerInfo.setDefaultWMSInterpolationMethod(Bilinear);
        Assert.assertEquals(Bilinear, request.getLayers().get(0).getLayerInfo().getDefaultWMSInterpolationMethod());
        // service config says "Nearest"
        Assert.assertEquals(WMSInfo.WMSInterpolation.Nearest, getWMS().getServiceInfo().getInterpolation());
        map = createWMSMap(env);
        map.setRequest(request);
        // adding layer twice on purpose to disable direct raster render
        addRasterToMap(map, TASMANIA_DEM);
        imageMap = this.rasterMapProducer.produceMap(map);
        checkByLayerInterpolation(imageMap, Interpolation.getInstance(INTERP_BICUBIC));
        // if default interpolation method is not specified, service default is used
        request = new GetMapRequest();
        request.setFormat(getMapFormat());
        request.setLayers(Arrays.asList(mapLayer));
        layerInfo.setDefaultWMSInterpolationMethod(null);
        Assert.assertEquals(null, request.getLayers().get(0).getLayerInfo().getDefaultWMSInterpolationMethod());
        Assert.assertTrue(request.getInterpolations().isEmpty());
        Assert.assertEquals(WMSInfo.WMSInterpolation.Nearest, getWMS().getServiceInfo().getInterpolation());
        map = createWMSMap(env);
        map.setRequest(request);
        imageMap = this.rasterMapProducer.produceMap(map);
        op = ((RenderedOp) (imageMap.getImage()));
        BufferedImage imageServiceDefault = op.getAsBufferedImage();
        imageMap.dispose();
        assertNotBlank("testInterpolationServiceDefault", imageServiceDefault);
        // test produced image is equal to imageNearest
        Assert.assertEquals(getPixelColor(imageNearest, 200, 200).getRGB(), getPixelColor(imageServiceDefault, 200, 200).getRGB());
        Assert.assertEquals(getPixelColor(imageNearest, 300, 300).getRGB(), getPixelColor(imageServiceDefault, 300, 300).getRGB());
        Assert.assertEquals(getPixelColor(imageNearest, 250, 250).getRGB(), getPixelColor(imageServiceDefault, 250, 250).getRGB());
        Assert.assertEquals(getPixelColor(imageNearest, 150, 150).getRGB(), getPixelColor(imageServiceDefault, 150, 150).getRGB());
    }

    /**
     * Checks {@link RenderedImageMapOutputFormat} makes good use of {@link RenderExceptionStrategy}
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testRenderingErrorsHandling() throws Exception {
        // the ones that are ignorable by the renderer
        Assert.assertNotNull(forceRenderingError(new TransformException("fake transform exception")));
        Assert.assertNotNull(forceRenderingError(new NoninvertibleTransformException("fake non invertible exception")));
        Assert.assertNotNull(forceRenderingError(new IllegalAttributeException("non illegal attribute exception")));
        Assert.assertNotNull(forceRenderingError(new FactoryException("fake factory exception")));
        // any other one should make the map producer fail
        try {
            forceRenderingError(new RuntimeException("fake runtime exception"));
            Assert.fail("Expected WMSException");
        } catch (ServiceException e) {
            Assert.assertTrue(true);
        }
        try {
            forceRenderingError(new IOException("fake IO exception"));
            Assert.fail("Expected WMSException");
        } catch (ServiceException e) {
            Assert.assertTrue(true);
        }
        try {
            forceRenderingError(new IllegalArgumentException("fake IAE exception"));
            Assert.fail("Expected WMSException");
        } catch (ServiceException e) {
            Assert.assertTrue(true);
        }
    }

    /**
     * Test to check if we can successfully create a direct rendered image by using a coverage view
     * as a source, and a symbolizer defining which three bands of the input coverage view can be
     * used for RGB coloring, and with what order.
     */
    @Test
    public void testStyleUsingChannelsFromCoverageView() throws Exception {
        GetMapRequest request = new GetMapRequest();
        CoordinateReferenceSystem crs = DefaultGeographicCRS.WGS84;
        ReferencedEnvelope bbox = new ReferencedEnvelope(new Envelope((-116.90673461649858), (-114.30988665660261), 32.070937282184026, 33.8903284734844), crs);
        request.setBbox(bbox);
        request.setSRS("urn:x-ogc:def:crs:EPSG:4326");
        request.setFormat("image/png");
        final WMSMapContent map = new WMSMapContent(request);
        map.setMapWidth(300);
        map.setMapHeight(300);
        map.setTransparent(false);
        map.getViewport().setBounds(bbox);
        StyleBuilder styleBuilder = new StyleBuilder();
        Catalog catalog = getCatalog();
        // Source image
        CoverageInfo ci = catalog.getCoverageByName(MULTIBAND.getPrefix(), MULTIBAND.getLocalPart());
        GridCoverage2DReader reader = ((GridCoverage2DReader) (ci.getGridCoverageReader(null, null)));
        reader.getCoordinateReferenceSystem();
        Layer sl = new org.geoserver.wms.CachedGridReaderLayer(reader, styleBuilder.createStyle(styleBuilder.createRasterSymbolizer()));
        map.addLayer(sl);
        RenderedImageMap srcImageMap = this.rasterMapProducer.produceMap(map);
        RenderedImage srcImage = srcImageMap.getImage();
        // CoverageView band creation. We create a coverage view with 6 bands, using
        // the original bands from the multiband coverage
        // Note that first three bands are int reverse order of the bands of the source coverage
        final InputCoverageBand ib0 = new InputCoverageBand("multiband", "2");
        final CoverageBand b0 = new CoverageBand(Collections.singletonList(ib0), "multiband@2", 0, CompositionType.BAND_SELECT);
        final InputCoverageBand ib1 = new InputCoverageBand("multiband", "1");
        final CoverageBand b1 = new CoverageBand(Collections.singletonList(ib1), "multiband@1", 1, CompositionType.BAND_SELECT);
        final InputCoverageBand ib2 = new InputCoverageBand("multiband", "0");
        final CoverageBand b2 = new CoverageBand(Collections.singletonList(ib2), "multiband@0", 2, CompositionType.BAND_SELECT);
        final InputCoverageBand ib3 = new InputCoverageBand("multiband", "0");
        final CoverageBand b3 = new CoverageBand(Collections.singletonList(ib3), "multiband@0", 0, CompositionType.BAND_SELECT);
        final InputCoverageBand ib4 = new InputCoverageBand("multiband", "1");
        final CoverageBand b4 = new CoverageBand(Collections.singletonList(ib4), "multiband@1", 1, CompositionType.BAND_SELECT);
        final InputCoverageBand ib5 = new InputCoverageBand("multiband", "2");
        final CoverageBand b5 = new CoverageBand(Collections.singletonList(ib5), "multiband@2", 2, CompositionType.BAND_SELECT);
        final List<CoverageBand> coverageBands = new ArrayList<CoverageBand>(1);
        coverageBands.add(b0);
        coverageBands.add(b1);
        coverageBands.add(b2);
        coverageBands.add(b3);
        coverageBands.add(b4);
        coverageBands.add(b5);
        CoverageView multiBandCoverageView = new CoverageView("multiband_select", coverageBands);
        CoverageStoreInfo storeInfo = catalog.getCoverageStoreByName("multiband");
        CatalogBuilder builder = new CatalogBuilder(catalog);
        // Reordered bands coverage
        CoverageInfo coverageInfo = multiBandCoverageView.createCoverageInfo("multiband_select", storeInfo, builder);
        coverageInfo.getParameters().put("USE_JAI_IMAGEREAD", "false");
        catalog.add(coverageInfo);
        final LayerInfo layerInfoView = builder.buildLayer(coverageInfo);
        catalog.add(layerInfoView);
        final Envelope env = ci.boundingBox();
        RenderedImageMapOutputFormatTest.LOGGER.info(("about to create map ctx for BasicPolygons with bounds " + env));
        RasterSymbolizer symbolizer = styleBuilder.createRasterSymbolizer();
        ChannelSelection cs = new ChannelSelectionImpl();
        SelectedChannelType red = new SelectedChannelTypeImpl();
        SelectedChannelType green = new SelectedChannelTypeImpl();
        SelectedChannelType blue = new SelectedChannelTypeImpl();
        // We want to create an image where the RGB channels are in reverse order
        // regarding the band order of the input coverage view
        // Note that channel names start with index "1"
        red.setChannelName("3");
        green.setChannelName("2");
        blue.setChannelName("1");
        cs.setRGBChannels(new SelectedChannelType[]{ red, green, blue });
        symbolizer.setChannelSelection(cs);
        reader = ((GridCoverage2DReader) (coverageInfo.getGridCoverageReader(null, null)));
        reader.getCoordinateReferenceSystem();
        Layer dl = new org.geoserver.wms.CachedGridReaderLayer(reader, styleBuilder.createStyle(symbolizer));
        map.removeLayer(sl);
        map.addLayer(dl);
        RenderedImageMap dstImageMap = this.rasterMapProducer.produceMap(map);
        RenderedImage destImage = dstImageMap.getImage();
        int dWidth = destImage.getWidth();
        int dHeight = destImage.getHeight();
        int[] destImageRowBand0 = new int[dWidth * dHeight];
        int[] destImageRowBand1 = new int[destImageRowBand0.length];
        int[] destImageRowBand2 = new int[destImageRowBand0.length];
        destImage.getData().getSamples(0, 0, dWidth, dHeight, 0, destImageRowBand0);
        destImage.getData().getSamples(0, 0, dWidth, dHeight, 1, destImageRowBand1);
        destImage.getData().getSamples(0, 0, dWidth, dHeight, 2, destImageRowBand2);
        int sWidth = srcImage.getWidth();
        int sHeight = srcImage.getHeight();
        int[] srcImageRowBand0 = new int[sWidth * sHeight];
        int[] srcImageRowBand2 = new int[srcImageRowBand0.length];
        srcImage.getData().getSamples(0, 0, sWidth, sHeight, 0, srcImageRowBand0);
        // Source and result image first bands should be the same. We have reversed the order
        // of the three first bands of the source coverage and then we re-reversed the three
        // first bands using channel selection on the raster symbolizer used for rendering.
        Assert.assertTrue(Arrays.equals(destImageRowBand0, srcImageRowBand0));
        // Result band 0 should not be equal to source image band 2
        Assert.assertFalse(Arrays.equals(destImageRowBand0, srcImageRowBand2));
        srcImageMap.dispose();
        dstImageMap.dispose();
        map.dispose();
    }

    /**
     * Test to check the case where a {@link org.geotools.coverage.grid.io.AbstractGridFormat#BANDS}
     * reading parameter is passed to a coverage reader that does not support it. Reader should
     * ignore it, resulting coverage should not be affected.
     */
    @Test
    public void testBandSelectionToNormalCoverage() throws Exception {
        GetMapRequest request = new GetMapRequest();
        CoordinateReferenceSystem crs = DefaultGeographicCRS.WGS84;
        ReferencedEnvelope bbox = new ReferencedEnvelope(new Envelope((-116.90673461649858), (-114.30988665660261), 32.070937282184026, 33.8903284734844), crs);
        request.setBbox(bbox);
        request.setSRS("urn:x-ogc:def:crs:EPSG:4326");
        request.setFormat("image/png");
        final WMSMapContent map = new WMSMapContent(request);
        map.setMapWidth(300);
        map.setMapHeight(300);
        map.setBgColor(Color.red);
        map.setTransparent(false);
        map.getViewport().setBounds(bbox);
        StyleBuilder styleBuilder = new StyleBuilder();
        Catalog catalog = getCatalog();
        CoverageInfo ci = catalog.getCoverageByName(MULTIBAND.getPrefix(), MULTIBAND.getLocalPart());
        GridCoverage2DReader reader = ((GridCoverage2DReader) (ci.getGridCoverageReader(null, null)));
        reader.getCoordinateReferenceSystem();
        final Envelope env = ci.boundingBox();
        final int[] bandIndices = new int[]{ 1, 2, 0, 2, 1 };
        // Inject bandIndices read param
        Parameter<int[]> bandIndicesParam = ((Parameter<int[]>) (BANDS.createValue()));
        bandIndicesParam.setValue(bandIndices);
        List<GeneralParameterValue> paramList = new ArrayList<GeneralParameterValue>();
        paramList.add(bandIndicesParam);
        GeneralParameterValue[] readParams = paramList.toArray(new GeneralParameterValue[paramList.size()]);
        Layer sl = new org.geoserver.wms.CachedGridReaderLayer(reader, styleBuilder.createStyle(styleBuilder.createRasterSymbolizer()), readParams);
        map.addLayer(sl);
        RenderedImageMap imageMap = this.rasterMapProducer.produceMap(map);
        ImageAssert.assertEquals(new File("src/test/resources/org/geoserver/wms/map/direct-raster-expected.tif"), imageMap.getImage(), 0);
        imageMap.dispose();
    }

    @Test
    public void testGetMapOnByteNodataGrayScale() throws Exception {
        GetMapRequest request = new GetMapRequest();
        CoordinateReferenceSystem crs = DefaultGeographicCRS.WGS84;
        ReferencedEnvelope bbox = new ReferencedEnvelope(new Envelope(145, 146, (-43), (-41)), crs);
        request.setBbox(bbox);
        request.setHeight(768);
        request.setWidth(384);
        request.setSRS("urn:x-ogc:def:crs:EPSG:4326");
        request.setFormat("image/png");
        request.setTransparent(true);
        final WMSMapContent map = new WMSMapContent(request);
        map.setMapHeight(768);
        map.setMapWidth(384);
        map.setBgColor(WMSTestSupport.BG_COLOR);
        map.setTransparent(true);
        map.getViewport().setBounds(bbox);
        addRasterToMap(map, RenderedImageMapOutputFormatTest.TAZ_BYTE);
        map.getViewport().setBounds(bbox);
        RenderedImageMap imageMap = this.rasterMapProducer.produceMap(map);
        RenderedOp op = ((RenderedOp) (imageMap.getImage()));
        BufferedImage image = op.getAsBufferedImage();
        imageMap.dispose();
        // check that a pixel in nodata area is transparent
        Assert.assertEquals(0, image.getRaster().getSample(40, 400, 0));
        Assert.assertEquals(0, image.getRaster().getSample(40, 400, 1));
    }

    /**
     * Test to make sure the rendering does not skip on unmatched original envelope and tries
     * anyways to render an output
     */
    @Test
    public void testMosaicExpansion() throws Exception {
        File red1 = URLs.urlToFile(this.getClass().getResource("red_footprint_test/red1.tif"));
        File source = red1.getParentFile();
        File testDataDir = getResourceLoader().getBaseDirectory();
        File directory1 = new File(testDataDir, "redHarvest1");
        File directory2 = new File(testDataDir, "redHarvest2");
        if (directory1.exists()) {
            FileUtils.deleteDirectory(directory1);
        }
        FileUtils.copyDirectory(source, directory1);
        // move all files except red3 to the second dir
        directory2.mkdirs();
        for (File file : FileUtils.listFiles(directory1, new RegexFileFilter("red[^3].*"), null)) {
            Assert.assertTrue(file.renameTo(new File(directory2, file.getName())));
        }
        // create the first reader
        URL harvestSingleURL = URLs.fileToUrl(directory1);
        ImageMosaicReader reader = new ImageMosaicReader(directory1, null);
        // now create a second reader that won't be informed of the harvesting changes
        // (simulating changes over a cluster, where the bbox information won't be updated from one
        // node to the other)
        ImageMosaicReader reader2 = new ImageMosaicReader(directory1, null);
        try {
            // harvest the other files with the first reader
            for (File file : directory2.listFiles()) {
                Assert.assertTrue(file.renameTo(new File(directory1, file.getName())));
            }
            reader.harvest(null, directory1, null);
            // now use the render to paint a map not hitting the original envelope of reader2
            ReferencedEnvelope renderEnvelope = new ReferencedEnvelope(991000, 992000, 216000, 217000, reader2.getCoordinateReferenceSystem());
            Rectangle rasterArea = new Rectangle(0, 0, 10, 10);
            GetMapRequest request = new GetMapRequest();
            request.setBbox(renderEnvelope);
            request.setSRS("EPSG:6539");
            request.setFormat("image/png");
            final WMSMapContent map = new WMSMapContent(request);
            map.setMapWidth(10);
            map.setMapHeight(10);
            map.setBgColor(Color.BLACK);
            map.setTransparent(false);
            map.getViewport().setBounds(renderEnvelope);
            StyleBuilder builder = new StyleBuilder();
            Style style = builder.createStyle(builder.createRasterSymbolizer());
            Layer l = new org.geoserver.wms.CachedGridReaderLayer(reader2, style);
            map.addLayer(l);
            RenderedImageMap imageMap = this.rasterMapProducer.produceMap(map);
            File reference = new File("src/test/resources/org/geoserver/wms/map/red10.png");
            ImageAssert.assertEquals(reference, imageMap.getImage(), 0);
            // now again, but with a rendering transformation, different code path
            style.featureTypeStyles().get(0).setTransformation(new IdentityCoverageFunction());
            RenderedImageMap imageMap2 = this.rasterMapProducer.produceMap(map);
            ImageAssert.assertEquals(reference, imageMap2.getImage(), 0);
            imageMap.dispose();
        } finally {
            reader.dispose();
            reader2.dispose();
        }
    }

    @Test
    public void testGetMapUntiledBigSize() throws Exception {
        final int mapWidth = 8192;
        final int mapHeight = 8192;
        GetMapRequest request = new GetMapRequest();
        CoordinateReferenceSystem crs = DefaultGeographicCRS.WGS84;
        ReferencedEnvelope bbox = new ReferencedEnvelope(new Envelope(145, 146, (-43), (-41)), crs);
        request.setBbox(bbox);
        request.setHeight(mapHeight);
        request.setWidth(mapWidth);
        request.setSRS("urn:x-ogc:def:crs:EPSG:4326");
        request.setFormat("image/png");
        request.setTransparent(true);
        final WMSMapContent map = new WMSMapContent(request);
        map.setMapHeight(mapHeight);
        map.setMapWidth(mapWidth);
        map.setBgColor(WMSTestSupport.BG_COLOR);
        map.setTransparent(true);
        map.getViewport().setBounds(bbox);
        addRasterToMap(map, RenderedImageMapOutputFormatTest.TAZ_BYTE);
        map.getViewport().setBounds(bbox);
        RenderedImageMap imageMap = this.rasterMapProducer.produceMap(map);
        RenderedOp op = ((RenderedOp) (imageMap.getImage()));
        Point[] tileIndices = op.getTileIndices(new Rectangle(0, 0, mapWidth, mapHeight));
        // Assert we are getting more than a single huge tile.
        Assert.assertTrue(((tileIndices.length) > 1));
        Raster tile = op.getTile(0, 0);
        Assert.assertNotNull(tile);
        // check that inner tiling has not be set to mapWidth * mapHeight
        Assert.assertTrue(((tile.getWidth()) < mapWidth));
        Assert.assertTrue(((tile.getHeight()) < mapHeight));
        ImageUtilities.disposePlanarImageChain(op);
        imageMap.dispose();
    }

    /**
     * This dummy producer adds no functionality to DefaultRasterMapOutputFormat, just implements a
     * void formatImageOutputStream to have a concrete class over which test that
     * DefaultRasterMapOutputFormat correctly generates the BufferedImage.
     *
     * @author Gabriel Roldan
     * @version $Id: DefaultRasterMapOutputFormatTest.java 6797 2007-05-16 10:23:50Z aaime $
     */
    static class DummyRasterMapProducer extends RenderedImageMapOutputFormat {
        public DummyRasterMapProducer(WMS wms) {
            super("image/gif", new String[]{ "image/gif" }, wms);
        }
    }
}

