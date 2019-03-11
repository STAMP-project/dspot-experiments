/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wps.gs.download;


import DefaultGeographicCRS.WGS84;
import ProcessState.DISMISSING;
import ProcessState.RUNNING;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.util.IOUtils;
import org.geoserver.wcs.CoverageCleanerCallback;
import org.geoserver.wps.WPSTestSupport;
import org.geoserver.wps.executor.ExecutionStatus;
import org.geoserver.wps.executor.ProcessState;
import org.geoserver.wps.ppio.WFSPPIO;
import org.geoserver.wps.ppio.ZipArchivePPIO;
import org.geoserver.wps.resource.WPSResourceManager;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridEnvelope2D;
import org.geotools.coverage.util.CoverageUtilities;
import org.geotools.coverage.util.FeatureUtilities;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.util.DefaultProgressListener;
import org.geotools.data.util.NullProgressListener;
import org.geotools.feature.NameImpl;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.WKTReader2;
import org.geotools.process.ProcessException;
import org.geotools.referencing.CRS;
import org.geotools.util.logging.Logging;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.PropertyName;
import org.opengis.util.InternationalString;
import org.opengis.util.ProgressListener;

import static DownloadServiceConfiguration.DEFAULT_COMPRESSION_LEVEL;
import static DownloadServiceConfiguration.NO_LIMIT;


/**
 * This class tests checks if the DownloadProcess class behaves correctly.
 *
 * @author "Alessio Fabiani - alessio.fabiani@geo-solutions.it"
 */
public class DownloadProcessTest extends WPSTestSupport {
    private static final FilterFactory2 FF = FeatureUtilities.DEFAULT_FILTER_FACTORY;

    private static QName MIXED_RES = new QName(WCS_URI, "mixedres", WCS_PREFIX);

    private static Set<String> GTIFF_EXTENSIONS = new HashSet<String>();

    private static Set<String> PNG_EXTENSIONS = new HashSet<String>();

    private static Set<String> JPEG_EXTENSIONS = new HashSet<String>();

    private static Set<String> XML_EXTENSIONS = new HashSet<String>();

    private static Set<String> JSON_EXTENSIONS = new HashSet<String>();

    private static Map<String, Set<String>> FORMAT_TO_EXTENSIONS = new HashMap<>();

    static {
        DownloadProcessTest.GTIFF_EXTENSIONS.add("tif");
        DownloadProcessTest.GTIFF_EXTENSIONS.add("tiff");
        DownloadProcessTest.GTIFF_EXTENSIONS.add("geotiff");
        DownloadProcessTest.FORMAT_TO_EXTENSIONS.put("GTIFF", DownloadProcessTest.GTIFF_EXTENSIONS);
        DownloadProcessTest.PNG_EXTENSIONS.add("png");
        DownloadProcessTest.FORMAT_TO_EXTENSIONS.put("PNG", DownloadProcessTest.PNG_EXTENSIONS);
        DownloadProcessTest.JPEG_EXTENSIONS.add("jpg");
        DownloadProcessTest.JPEG_EXTENSIONS.add("jpeg");
        DownloadProcessTest.FORMAT_TO_EXTENSIONS.put("JPEG", DownloadProcessTest.JPEG_EXTENSIONS);
        DownloadProcessTest.XML_EXTENSIONS.add("xml");
        DownloadProcessTest.FORMAT_TO_EXTENSIONS.put("XML", DownloadProcessTest.XML_EXTENSIONS);
        DownloadProcessTest.JSON_EXTENSIONS.add("json");
        DownloadProcessTest.FORMAT_TO_EXTENSIONS.put("JSON", DownloadProcessTest.JSON_EXTENSIONS);
    }

    /**
     * Test ROI used
     */
    static final Polygon roi;

    static final Polygon ROI2;

    static final Polygon ROI3;

    static {
        try {
            roi = ((Polygon) (new WKTReader2().read("POLYGON (( 500116.08576537756 499994.25579707103, 500116.08576537756 500110.1012210889, 500286.2657688021 500110.1012210889, 500286.2657688021 499994.25579707103, 500116.08576537756 499994.25579707103 ))")));
            ROI2 = ((Polygon) (new WKTReader2().read("POLYGON (( -125 30, -116 30, -116 45, -125 45, -125 30))")));
            ROI3 = ((Polygon) (new WKTReader2().read("POLYGON (( 356050 5520000, 791716 5520000, 791716 5655096, 356050 5655096, 356050 5520000))")));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Test get features as shapefile.
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void testGetFeaturesAsShapefile() throws Exception {
        // Estimator process for checking limits
        DownloadEstimatorProcess limits = new DownloadEstimatorProcess(new StaticDownloadServiceConfiguration(), getGeoServer());
        final WPSResourceManager resourceManager = getResourceManager();
        // Creates the new process for the download
        DownloadProcess downloadProcess = new DownloadProcess(getGeoServer(), limits, resourceManager);
        FeatureTypeInfo ti = getCatalog().getFeatureTypeByName(getLayerId(MockData.POLYGONS));
        SimpleFeatureCollection rawSource = ((SimpleFeatureCollection) (ti.getFeatureSource(null, null).getFeatures()));
        // Download
        File shpeZip = // layerName
        // mail
        // outputFormat
        // targetCRS
        // roiCRS
        // roi
        // cropToGeometry
        // interpolation
        // targetSizeX
        // targetSizeY
        // bandSelectIndices
        // Writing params
        // progressListener
        downloadProcess.execute(getLayerId(MockData.POLYGONS), null, "application/zip", null, CRS.decode("EPSG:32615"), DownloadProcessTest.roi, false, null, null, null, null, null, new NullProgressListener());
        // Final checks on the result
        Assert.assertNotNull(shpeZip);
        SimpleFeatureCollection rawTarget = ((SimpleFeatureCollection) (decodeShape(new FileInputStream(shpeZip))));
        Assert.assertNotNull(rawTarget);
        Assert.assertEquals(rawSource.size(), rawTarget.size());
    }

    /**
     * Test downloading with a duplicate style
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void testDownloadWithDuplicateStyle() throws Exception {
        String polygonsName = getLayerId(MockData.POLYGONS);
        LayerInfo li = getCatalog().getLayerByName(polygonsName);
        // setup an alternative equal to the main style
        li.getStyles().add(li.getDefaultStyle());
        getCatalog().save(li);
        testGetFeaturesAsShapefile();
    }

    /**
     * Test filtered clipped features.
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void testFilteredClippedFeatures() throws Exception {
        // Estimator process for checking limits
        DownloadEstimatorProcess limits = new DownloadEstimatorProcess(new StaticDownloadServiceConfiguration(), getGeoServer());
        final WPSResourceManager resourceManager = getResourceManager();
        // Creates the new process for the download
        DownloadProcess downloadProcess = new DownloadProcess(getGeoServer(), limits, resourceManager);
        // ROI object
        Polygon roi = ((Polygon) (new WKTReader2().read("POLYGON ((0.0008993124415341 0.0006854377923293, 0.0008437876520112 0.0006283489242283, 0.0008566913002806 0.0005341131898971, 0.0009642217025257 0.0005188634237605, 0.0011198475210477 0.000574779232928, 0.0010932581852198 0.0006572843779233, 0.0008993124415341 0.0006854377923293))")));
        FeatureTypeInfo ti = getCatalog().getFeatureTypeByName(getLayerId(MockData.BUILDINGS));
        SimpleFeatureCollection rawSource = ((SimpleFeatureCollection) (ti.getFeatureSource(null, null).getFeatures()));
        // Download
        File shpeZip = // layerName
        // filter
        // outputFormat
        // targetCRS
        // roiCRS
        // roi
        // cropToGeometry
        // interpolation
        // targetSizeX
        // targetSizeY
        // bandSelectIndices
        // Writing params
        // progressListener
        downloadProcess.execute(getLayerId(MockData.BUILDINGS), CQL.toFilter("ADDRESS = '123 Main Street'"), "application/zip", null, WGS84, roi, true, null, null, null, null, null, new NullProgressListener());
        // Final checks on the result
        Assert.assertNotNull(shpeZip);
        SimpleFeatureCollection rawTarget = ((SimpleFeatureCollection) (decodeShape(new FileInputStream(shpeZip))));
        Assert.assertNotNull(rawTarget);
        Assert.assertEquals(1, rawTarget.size());
        SimpleFeature srcFeature = rawSource.features().next();
        SimpleFeature trgFeature = rawTarget.features().next();
        Assert.assertEquals(srcFeature.getAttribute("ADDRESS"), trgFeature.getAttribute("ADDRESS"));
        // Final checks on the ROI
        Geometry srcGeometry = ((Geometry) (srcFeature.getDefaultGeometry()));
        Geometry trgGeometry = ((Geometry) (trgFeature.getDefaultGeometry()));
        Assert.assertTrue("Target geometry clipped and included into the source one", srcGeometry.contains(trgGeometry));
    }

    /**
     * Test get features as gml.
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void testGetFeaturesAsGML() throws Exception {
        // Estimator process for checking limits
        DownloadEstimatorProcess limits = new DownloadEstimatorProcess(new StaticDownloadServiceConfiguration(), getGeoServer());
        final WPSResourceManager resourceManager = getResourceManager();
        // Creates the new process for the download
        DownloadProcess downloadProcess = new DownloadProcess(getGeoServer(), limits, resourceManager);
        FeatureTypeInfo ti = getCatalog().getFeatureTypeByName(getLayerId(MockData.POLYGONS));
        SimpleFeatureCollection rawSource = ((SimpleFeatureCollection) (ti.getFeatureSource(null, null).getFeatures()));
        // Download as GML 2
        File gml2Zip = // layerName
        // filter
        // outputFormat
        // targetCRS
        // roiCRS
        // roi
        // cropToGeometry
        // interpolation
        // targetSizeX
        // targetSizeY
        // bandSelectIndices
        // Writing params
        // progressListener
        downloadProcess.execute(getLayerId(MockData.POLYGONS), null, "application/wfs-collection-1.0", null, CRS.decode("EPSG:32615"), DownloadProcessTest.roi, false, null, null, null, null, null, new NullProgressListener());
        // Final checks on the result
        Assert.assertNotNull(gml2Zip);
        File[] files = extractFiles(gml2Zip, "XML");
        SimpleFeatureCollection rawTarget = ((SimpleFeatureCollection) (new WFSPPIO.WFS10().decode(new FileInputStream(files[0]))));
        Assert.assertNotNull(rawTarget);
        Assert.assertEquals(rawSource.size(), rawTarget.size());
        // Download as GML 3
        File gml3Zip = // layerName
        // filter
        // outputFormat
        // targetCRS
        // roiCRS
        // roi
        // cropToGeometry
        // interpolation
        // targetSizeX
        // targetSizeY
        // bandSelectIndices
        // Writing params
        // progressListener
        downloadProcess.execute(getLayerId(MockData.POLYGONS), null, "application/wfs-collection-1.1", null, CRS.decode("EPSG:32615"), DownloadProcessTest.roi, false, null, null, null, null, null, new NullProgressListener());
        // Final checks on the result
        Assert.assertNotNull(gml3Zip);
        files = extractFiles(gml2Zip, "XML");
        rawTarget = ((SimpleFeatureCollection) (new WFSPPIO.WFS11().decode(new FileInputStream(files[0]))));
        Assert.assertNotNull(rawTarget);
        Assert.assertEquals(rawSource.size(), rawTarget.size());
    }

    /**
     * Test get features as geo json.
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void testGetFeaturesAsGeoJSON() throws Exception {
        // Estimator process for checking limits
        DownloadEstimatorProcess limits = new DownloadEstimatorProcess(new StaticDownloadServiceConfiguration(), getGeoServer());
        final WPSResourceManager resourceManager = getResourceManager();
        // Creates the new process for the download
        DownloadProcess downloadProcess = new DownloadProcess(getGeoServer(), limits, resourceManager);
        FeatureTypeInfo ti = getCatalog().getFeatureTypeByName(getLayerId(MockData.POLYGONS));
        SimpleFeatureCollection rawSource = ((SimpleFeatureCollection) (ti.getFeatureSource(null, null).getFeatures()));
        // Download the file as Json
        File jsonZip = // layerName
        // filter
        // outputFormat
        // targetCRS
        // roiCRS
        // roi
        // cropToGeometry
        // interpolation
        // targetSizeX
        // targetSizeY
        // bandSelectIndices
        // Writing params
        // progressListener
        downloadProcess.execute(getLayerId(MockData.POLYGONS), null, "application/json", null, CRS.decode("EPSG:32615"), DownloadProcessTest.roi, false, null, null, null, null, null, new NullProgressListener());
        // Final checks on the result
        Assert.assertNotNull(jsonZip);
        File[] files = extractFiles(jsonZip, "JSON");
        SimpleFeatureCollection rawTarget = ((SimpleFeatureCollection) (new FeatureJSON().readFeatureCollection(new FileInputStream(files[0]))));
        Assert.assertNotNull(rawTarget);
        Assert.assertEquals(rawSource.size(), rawTarget.size());
    }

    /**
     * Test download of raster data.
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void testDownloadRaster() throws Exception {
        // Estimator process for checking limits
        DownloadEstimatorProcess limits = new DownloadEstimatorProcess(new StaticDownloadServiceConfiguration(), getGeoServer());
        final WPSResourceManager resourceManager = getResourceManager();
        // Creates the new process for the download
        DownloadProcess downloadProcess = new DownloadProcess(getGeoServer(), limits, resourceManager);
        // test ROI
        double firstXRoi = -127.57473954542964;
        double firstYRoi = 54.06575021619523;
        Polygon roi = ((Polygon) (new WKTReader2().read((((("POLYGON (( " + firstXRoi) + " ") + firstYRoi) + ", -130.88669845369998 52.00807146727025, -129.50812897394974 49.85372324691927, -130.5300633861675 49.20465679591609, -129.25955033314003 48.60392508062591, -128.00975216684665 50.986137055052474, -125.8623089087404 48.63154492960477, -123.984159178178 50.68231871628503, -126.91186316993704 52.15307567440926, -125.3444367403868 53.54787804784162, -127.57473954542964 54.06575021619523 ))"))));
        roi.setSRID(4326);
        // ROI reprojection
        Polygon roiResampled = ((Polygon) (JTS.transform(roi, CRS.findMathTransform(CRS.decode("EPSG:4326", true), CRS.decode("EPSG:900913", true)))));
        // Download the coverage as tiff (Not reprojected)
        File rasterZip = // layerName
        // filter
        // outputFormat
        // targetCRS
        // roiCRS
        // roi
        // cropToGeometry
        // interpolation
        // targetSizeX
        // targetSizeY
        // bandSelectIndices
        // Writing params
        // progressListener
        downloadProcess.execute(getLayerId(MockData.USA_WORLDIMG), null, "image/tiff", null, CRS.decode("EPSG:4326", true), roi, true, null, null, null, null, null, new NullProgressListener());
        // Final checks on the result
        Assert.assertNotNull(rasterZip);
        GeoTiffReader reader = null;
        GridCoverage2D gc = null;
        GridCoverage2D gcResampled = null;
        try {
            final File[] tiffFiles = extractFiles(rasterZip, "GTIFF");
            Assert.assertNotNull(tiffFiles);
            Assert.assertTrue(((tiffFiles.length) > 0));
            reader = new GeoTiffReader(tiffFiles[0]);
            gc = reader.read(null);
            Assert.assertNotNull(gc);
            Assert.assertEquals((-130.88669845369998), gc.getEnvelope().getLowerCorner().getOrdinate(0), 1.0E-6);
            Assert.assertEquals(48.611129008700004, gc.getEnvelope().getLowerCorner().getOrdinate(1), 1.0E-6);
            Assert.assertEquals((-123.95304462109999), gc.getEnvelope().getUpperCorner().getOrdinate(0), 1.0E-6);
            Assert.assertEquals(54.0861661371, gc.getEnvelope().getUpperCorner().getOrdinate(1), 1.0E-6);
            // Take a pixel within the ROI
            byte[] result = ((byte[]) (gc.evaluate(new DirectPosition2D(new Point2D.Double(firstXRoi, (firstYRoi - 1.0E-4))))));
            Assert.assertNotEquals(0, result[0]);
            Assert.assertNotEquals(0, result[1]);
            Assert.assertNotEquals(0, result[2]);
            // Take a pixel outside of the ROI
            result = ((byte[]) (gc.evaluate(new DirectPosition2D(new Point2D.Double((firstXRoi - 2), (firstYRoi - 0.5))))));
            Assert.assertEquals(0, result[0]);
            Assert.assertEquals(0, result[1]);
            Assert.assertEquals(0, result[2]);
        } finally {
            if (gc != null) {
                CoverageCleanerCallback.disposeCoverage(gc);
            }
            if (reader != null) {
                reader.dispose();
            }
            // clean up process
            resourceManager.finished(resourceManager.getExecutionId(true));
        }
        // Download the coverage as tiff with clipToROI set to False (Crop on envelope)
        rasterZip = // layerName
        // filter
        // outputFormat
        // targetCRS
        // roiCRS
        // roi
        // cropToGeometry
        // interpolation
        // targetSizeX
        // targetSizeY
        // bandSelectIndices
        // Writing params
        // progressListener
        downloadProcess.execute(getLayerId(MockData.USA_WORLDIMG), null, "image/tiff", null, CRS.decode("EPSG:4326", true), roi, false, null, null, null, null, null, new NullProgressListener());
        // Final checks on the result
        Assert.assertNotNull(rasterZip);
        try {
            final File[] tiffFiles = extractFiles(rasterZip, "GTIFF");
            Assert.assertNotNull(tiffFiles);
            Assert.assertTrue(((tiffFiles.length) > 0));
            reader = new GeoTiffReader(tiffFiles[0]);
            gc = reader.read(null);
            Assert.assertNotNull(gc);
            Assert.assertEquals((-130.88669845369998), gc.getEnvelope().getLowerCorner().getOrdinate(0), 1.0E-6);
            Assert.assertEquals(48.611129008700004, gc.getEnvelope().getLowerCorner().getOrdinate(1), 1.0E-6);
            Assert.assertEquals((-123.95304462109999), gc.getEnvelope().getUpperCorner().getOrdinate(0), 1.0E-6);
            Assert.assertEquals(54.0861661371, gc.getEnvelope().getUpperCorner().getOrdinate(1), 1.0E-6);
            // Take a pixel within the ROI
            byte[] result = ((byte[]) (gc.evaluate(new DirectPosition2D(new Point2D.Double(firstXRoi, (firstYRoi - 1.0E-4))))));
            Assert.assertNotEquals(0, result[0]);
            Assert.assertNotEquals(0, result[1]);
            Assert.assertNotEquals(0, result[2]);
            // Take a pixel outside of the ROI geometry but within the ROI's envelope
            // (We have set cropToROI = False)
            result = ((byte[]) (gc.evaluate(new DirectPosition2D(new Point2D.Double((firstXRoi - 2), (firstYRoi - 0.5))))));
            Assert.assertNotEquals(0, result[0]);
            Assert.assertNotEquals(0, result[1]);
            Assert.assertNotEquals(0, result[2]);
        } finally {
            if (gc != null) {
                CoverageCleanerCallback.disposeCoverage(gc);
            }
            if (reader != null) {
                reader.dispose();
            }
            // clean up process
            resourceManager.finished(resourceManager.getExecutionId(true));
        }
        // Download the coverage as tiff (Reprojected)
        File resampledZip = // layerName
        // filter
        // outputFormat
        // targetCRS
        // roiCRS
        // roi
        // cropToGeometry
        // interpolation
        // targetSizeX
        // targetSizeY
        // bandSelectIndices
        // Writing params
        // progressListener
        downloadProcess.execute(getLayerId(MockData.USA_WORLDIMG), null, "image/tiff", CRS.decode("EPSG:900913", true), CRS.decode("EPSG:900913", true), roiResampled, true, null, null, null, null, null, new NullProgressListener());
        // Final checks on the result
        Assert.assertNotNull(resampledZip);
        try {
            File[] files = extractFiles(resampledZip, "GTIFF");
            reader = new GeoTiffReader(files[((files.length) - 1)]);
            gcResampled = reader.read(null);
            Assert.assertNotNull(gcResampled);
            Assert.assertEquals((-1.457024062347863E7), gcResampled.getEnvelope().getLowerCorner().getOrdinate(0), 1.0E-6);
            Assert.assertEquals(6209706.404894806, gcResampled.getEnvelope().getLowerCorner().getOrdinate(1), 1.0E-6);
            Assert.assertEquals((-1.379838980949677E7), gcResampled.getEnvelope().getUpperCorner().getOrdinate(0), 1.0E-6);
            Assert.assertEquals(7187128.139081598, gcResampled.getEnvelope().getUpperCorner().getOrdinate(1), 1.0E-6);
        } finally {
            if (gcResampled != null) {
                CoverageCleanerCallback.disposeCoverage(gcResampled);
            }
            if (reader != null)
                reader.dispose();

            // clean up process
            resourceManager.finished(resourceManager.getExecutionId(true));
        }
    }

    /**
     * Test Writing parameters are used, nodata not being set
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void testDownloadWithWriteParametersWithoutNodata() throws Exception {
        testWriteParameters(false);
    }

    /**
     * Test Writing parameters are used
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void testDownloadWithWriteParameters() throws Exception {
        testWriteParameters(true);
    }

    /**
     * Test download of selected bands of raster data. Result contains only bands 0 and 2.
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void testDownloadRasterSelectedBands() throws Exception {
        // Estimator process for checking limits
        DownloadEstimatorProcess limits = new DownloadEstimatorProcess(new StaticDownloadServiceConfiguration(), getGeoServer());
        final WPSResourceManager resourceManager = getResourceManager();
        // Creates the new process for the download
        DownloadProcess downloadProcess = new DownloadProcess(getGeoServer(), limits, resourceManager);
        // /////////////////////////////////////
        // test full coverage           //
        // /////////////////////////////////////
        // Download the coverage as tiff
        File rasterZip = // layerName
        // filter
        // outputFormat
        // targetCRS
        // roiCRS
        // roi
        // cropToGeometry
        // interpolation
        // targetSizeX
        // targetSizeY
        // bandSelectIndices
        // Writing params
        // progressListener
        downloadProcess.execute(getLayerId(MockData.USA_WORLDIMG), null, "image/tiff", null, CRS.decode("EPSG:4326", true), null, false, null, null, null, new int[]{ 0, 2 }, null, new NullProgressListener());
        // Final checks on the result
        Assert.assertNotNull(rasterZip);
        GeoTiffReader reader = null;
        GridCoverage2D gc = null;
        try {
            final File[] tiffFiles = extractFiles(rasterZip, "GTIFF");
            Assert.assertNotNull(tiffFiles);
            Assert.assertTrue(((tiffFiles.length) > 0));
            reader = new GeoTiffReader(tiffFiles[0]);
            gc = reader.read(null);
            Assert.assertNotNull(gc);
            // check bands
            Assert.assertEquals(2, gc.getNumSampleDimensions());
            // check visible band index for new coverage
            Assert.assertEquals(0, CoverageUtilities.getVisibleBand(gc));
            // check non existing band index
            Assert.assertNotEquals(3, gc.getNumSampleDimensions());
        } finally {
            if (gc != null) {
                CoverageCleanerCallback.disposeCoverage(gc);
            }
            if (reader != null) {
                reader.dispose();
            }
            // clean up process
            resourceManager.finished(resourceManager.getExecutionId(true));
        }
    }

    /**
     * Test download of selected bands of raster data, scald and using a ROI area. Result contains
     * only band 1.
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void testDownloadRasterSelectedBandsScaledWithROI() throws Exception {
        // Estimator process for checking limits
        DownloadEstimatorProcess limits = new DownloadEstimatorProcess(new StaticDownloadServiceConfiguration(), getGeoServer());
        final WPSResourceManager resourceManager = getResourceManager();
        // Creates the new process for the download
        DownloadProcess downloadProcess = new DownloadProcess(getGeoServer(), limits, resourceManager);
        // /////////////////////////////////////
        // test full coverage           //
        // /////////////////////////////////////
        Polygon roi = ((Polygon) (new WKTReader2().read(("POLYGON (( " + ((((((((((("-127.57473954542964 54.06575021619523, " + "-130.88669845369998 52.00807146727025, ") + "-129.50812897394974 49.85372324691927, ") + "-130.5300633861675 49.20465679591609, ") + "-129.25955033314003 48.60392508062591, ") + "-128.00975216684665 50.986137055052474, ") + "-125.8623089087404 48.63154492960477, ") + "-123.984159178178 50.68231871628503, ") + "-126.91186316993704 52.15307567440926, ") + "-125.3444367403868 53.54787804784162, ") + "-127.57473954542964 54.06575021619523 ") + "))")))));
        roi.setSRID(4326);
        // Download the coverage as tiff
        File rasterZip = // layerName
        // filter
        // outputFormat
        // targetCRS
        // roiCRS
        // roi
        // cropToGeometry
        // interpolation
        // targetSizeX
        // targetSizeY
        // bandSelectIndices
        // Writing params
        // progressListener
        downloadProcess.execute(getLayerId(MockData.USA_WORLDIMG), null, "image/tiff", null, CRS.decode("EPSG:4326", true), roi, false, null, 40, 40, new int[]{ 1 }, null, new NullProgressListener());
        // Final checks on the result
        Assert.assertNotNull(rasterZip);
        GeoTiffReader reader = null;
        GridCoverage2D gc = null;
        try {
            final File[] tiffFiles = extractFiles(rasterZip, "GTIFF");
            Assert.assertNotNull(tiffFiles);
            Assert.assertTrue(((tiffFiles.length) > 0));
            reader = new GeoTiffReader(tiffFiles[0]);
            gc = reader.read(null);
            Assert.assertNotNull(gc);
            // check bands
            Assert.assertEquals(1, gc.getNumSampleDimensions());
            Rectangle2D originalGridRange = ((GridEnvelope2D) (reader.getOriginalGridRange()));
            Assert.assertEquals(40, Math.round(originalGridRange.getWidth()));
            Assert.assertEquals(40, Math.round(originalGridRange.getHeight()));
            // check envelope
            Assert.assertEquals((-130.88669845369998), gc.getEnvelope().getLowerCorner().getOrdinate(0), 1.0E-6);
            Assert.assertEquals(48.5552612829, gc.getEnvelope().getLowerCorner().getOrdinate(1), 1.0E-6);
            Assert.assertEquals((-124.05382943906582), gc.getEnvelope().getUpperCorner().getOrdinate(0), 1.0E-6);
            Assert.assertEquals(54.00577111704634, gc.getEnvelope().getUpperCorner().getOrdinate(1), 1.0E-6);
        } finally {
            if (gc != null) {
                CoverageCleanerCallback.disposeCoverage(gc);
            }
            if (reader != null) {
                reader.dispose();
            }
            // clean up process
            resourceManager.finished(resourceManager.getExecutionId(true));
        }
    }

    /**
     * Test download of raster data. The output is scaled to fit exactly the provided size.
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void testDownloadScaledRaster() throws Exception {
        // Estimator process for checking limits
        DownloadEstimatorProcess limits = new DownloadEstimatorProcess(new StaticDownloadServiceConfiguration(), getGeoServer());
        final WPSResourceManager resourceManager = getResourceManager();
        // Creates the new process for the download
        DownloadProcess downloadProcess = new DownloadProcess(getGeoServer(), limits, resourceManager);
        // /////////////////////////////////////
        // test full coverage           //
        // /////////////////////////////////////
        // Download the coverage as tiff
        File rasterZip = // layerName
        // filter
        // outputFormat
        // targetCRS
        // roiCRS
        // roi
        // cropToGeometry
        // interpolation
        // targetSizeX
        // targetSizeY
        // bandSelectIndices
        // Writing params
        // progressListener
        downloadProcess.execute(getLayerId(MockData.USA_WORLDIMG), null, "image/tiff", null, CRS.decode("EPSG:4326", true), null, false, null, 80, 80, null, null, new NullProgressListener());
        // Final checks on the result
        Assert.assertNotNull(rasterZip);
        GeoTiffReader reader = null;
        GridCoverage2D gc = null;
        try {
            final File[] tiffFiles = extractFiles(rasterZip, "GTIFF");
            Assert.assertNotNull(tiffFiles);
            Assert.assertTrue(((tiffFiles.length) > 0));
            reader = new GeoTiffReader(tiffFiles[0]);
            gc = reader.read(null);
            Assert.assertNotNull(gc);
            // check coverage size
            Rectangle2D originalGridRange = ((GridEnvelope2D) (reader.getOriginalGridRange()));
            Assert.assertEquals(80, Math.round(originalGridRange.getWidth()));
            Assert.assertEquals(80, Math.round(originalGridRange.getHeight()));
            // check envelope
            Assert.assertEquals((-130.8866985), gc.getEnvelope().getLowerCorner().getOrdinate(0), 1.0E-6);
            Assert.assertEquals(48.5552613, gc.getEnvelope().getLowerCorner().getOrdinate(1), 1.0E-6);
            Assert.assertEquals((-123.8830077), gc.getEnvelope().getUpperCorner().getOrdinate(0), 1.0E-6);
            Assert.assertEquals(54.1420339, gc.getEnvelope().getUpperCorner().getOrdinate(1), 1.0E-6);
        } finally {
            if (gc != null) {
                CoverageCleanerCallback.disposeCoverage(gc);
            }
            if (reader != null) {
                reader.dispose();
            }
            // clean up process
            resourceManager.finished(resourceManager.getExecutionId(true));
        }
        // /////////////////////////////////////
        // test partial input           //
        // /////////////////////////////////////
        // Download the coverage as tiff
        File largerZip = // layerName
        // filter
        // outputFormat
        // targetCRS
        // roiCRS
        // roi
        // cropToGeometry
        // interpolation
        // targetSizeX
        // targetSizeY not specified, will be calculated based on targetSizeX
        // and aspect ratio of the original image
        // bandSelectIndices
        // Writing params
        // progressListener
        downloadProcess.execute(getLayerId(MockData.USA_WORLDIMG), null, "image/tiff", null, CRS.decode("EPSG:4326", true), null, false, null, 160, null, null, null, new NullProgressListener());
        // Final checks on the result
        Assert.assertNotNull(largerZip);
        try {
            final File[] tiffFiles = extractFiles(largerZip, "GTIFF");
            Assert.assertNotNull(tiffFiles);
            Assert.assertTrue(((tiffFiles.length) > 0));
            reader = new GeoTiffReader(tiffFiles[0]);
            gc = reader.read(null);
            Assert.assertNotNull(gc);
            // check coverage size
            Rectangle2D originalGridRange = ((GridEnvelope2D) (reader.getOriginalGridRange()));
            Assert.assertEquals(160, Math.round(originalGridRange.getWidth()));
            Assert.assertEquals(160, Math.round(originalGridRange.getHeight()));
        } finally {
            if (gc != null) {
                CoverageCleanerCallback.disposeCoverage(gc);
            }
            if (reader != null) {
                reader.dispose();
            }
            // clean up process
            resourceManager.finished(resourceManager.getExecutionId(true));
        }
        // ////////////////////////////////
        // test with ROI           //
        // ////////////////////////////////
        Polygon roi = ((Polygon) (new WKTReader2().read("POLYGON (( -127.57473954542964 54.06575021619523, -130.88669845369998 52.00807146727025, -129.50812897394974 49.85372324691927, -130.5300633861675 49.20465679591609, -129.25955033314003 48.60392508062591, -128.00975216684665 50.986137055052474, -125.8623089087404 48.63154492960477, -123.984159178178 50.68231871628503, -126.91186316993704 52.15307567440926, -125.3444367403868 53.54787804784162, -127.57473954542964 54.06575021619523 ))")));
        roi.setSRID(4326);
        // Download the coverage as tiff
        File resampledZip = // layerName
        // filter
        // outputFormat
        // targetCRS
        // roiCRS
        // roi
        // cropToGeometry
        // interpolation
        // targetSizeX
        // targetSizeY
        // bandSelectIndices
        // Writing params
        // progressListener
        downloadProcess.execute(getLayerId(MockData.USA_WORLDIMG), null, "image/tiff", null, CRS.decode("EPSG:4326", true), roi, true, null, 80, 80, null, null, new NullProgressListener());
        // Final checks on the result
        Assert.assertNotNull(resampledZip);
        try {
            final File[] tiffFiles = extractFiles(resampledZip, "GTIFF");
            Assert.assertNotNull(tiffFiles);
            Assert.assertTrue(((tiffFiles.length) > 0));
            reader = new GeoTiffReader(tiffFiles[0]);
            gc = reader.read(null);
            Assert.assertNotNull(gc);
            // check coverage size
            Rectangle2D originalGridRange = ((GridEnvelope2D) (reader.getOriginalGridRange()));
            Assert.assertEquals(80, Math.round(originalGridRange.getWidth()));
            Assert.assertEquals(80, Math.round(originalGridRange.getHeight()));
            // check envelope
            Assert.assertEquals((-130.88669845369998), gc.getEnvelope().getLowerCorner().getOrdinate(0), 1.0E-6);
            Assert.assertEquals(48.623544058877776, gc.getEnvelope().getLowerCorner().getOrdinate(1), 1.0E-6);
            Assert.assertEquals((-123.95304462109999), gc.getEnvelope().getUpperCorner().getOrdinate(0), 1.0E-6);
            Assert.assertEquals(54.0861661371, gc.getEnvelope().getUpperCorner().getOrdinate(1), 1.0E-6);
        } finally {
            if (gc != null) {
                CoverageCleanerCallback.disposeCoverage(gc);
            }
            if (reader != null) {
                reader.dispose();
            }
            // clean up process
            resourceManager.finished(resourceManager.getExecutionId(true));
        }
    }

    /**
     * PPIO Test.
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void testZipGeoTiffPPIO() throws Exception {
        // Estimator process for checking limits
        DownloadEstimatorProcess limits = new DownloadEstimatorProcess(new StaticDownloadServiceConfiguration(), getGeoServer());
        ZipArchivePPIO ppio = new ZipArchivePPIO(DEFAULT_COMPRESSION_LEVEL);
        final WPSResourceManager resourceManager = getResourceManager();
        // Creates the new process for the download
        DownloadProcess downloadProcess = new DownloadProcess(getGeoServer(), limits, resourceManager);
        // ROI as a BBOX
        Envelope env = new Envelope((-125.074006936869), (-123.88300771369998), 48.5552612829, 49.03872);
        Polygon roi = JTS.toGeometry(env);
        // Download the data with ROI
        File rasterZip = // layerName
        // filter
        // outputFormat
        // targetCRS
        // roiCRS
        // roi
        // cropToGeometry
        // interpolation
        // targetSizeX
        // targetSizeY
        // bandSelectIndices
        // Writing params
        // progressListener
        downloadProcess.execute(getLayerId(MockData.USA_WORLDIMG), null, "image/tiff", null, CRS.decode("EPSG:4326"), roi, true, null, null, null, null, null, new NullProgressListener());
        // Final checks on the result
        Assert.assertNotNull(rasterZip);
        // make sure we create files locally so that we don't clog the sytem temp
        final File currentDirectory = new File(DownloadProcessTest.class.getResource(".").toURI());
        File tempZipFile = File.createTempFile("zipppiotemp", ".zip", currentDirectory);
        ppio.encode(rasterZip, new FileOutputStream(tempZipFile));
        Assert.assertTrue(((tempZipFile.length()) > 0));
        final File tempDir = new File(currentDirectory, Long.toString(System.nanoTime()));
        Assert.assertTrue(tempDir.mkdir());
        File tempFile = DownloadProcessTest.decode(new FileInputStream(tempZipFile), tempDir);
        Assert.assertNotNull(tempFile);
        IOUtils.delete(tempFile);
    }

    /**
     * Test download estimator for raster data. The result should exceed the limits
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void testDownloadEstimatorReadLimitsRaster() throws Exception {
        // Estimator process for checking limits
        DownloadEstimatorProcess limits = new DownloadEstimatorProcess(new StaticDownloadServiceConfiguration(new DownloadServiceConfiguration(NO_LIMIT, 10, NO_LIMIT, NO_LIMIT, DEFAULT_COMPRESSION_LEVEL, NO_LIMIT)), getGeoServer());
        final WPSResourceManager resourceManager = getResourceManager();
        // Creates the new process for the download
        DownloadProcess downloadProcess = new DownloadProcess(getGeoServer(), limits, resourceManager);
        // ROI as polygon
        Polygon roi = ((Polygon) (new WKTReader2().read("POLYGON (( -127.57473954542964 54.06575021619523, -130.8545966116691 52.00807146727025, -129.50812897394974 49.85372324691927, -130.5300633861675 49.20465679591609, -129.25955033314003 48.60392508062591, -128.00975216684665 50.986137055052474, -125.8623089087404 48.63154492960477, -123.984159178178 50.68231871628503, -126.91186316993704 52.15307567440926, -125.3444367403868 53.54787804784162, -127.57473954542964 54.06575021619523 ))")));
        roi.setSRID(4326);
        try {
            // Download the data with ROI. It should throw an exception
            // layerName
            // filter
            // outputFormat
            // targetCRS
            // roiCRS
            // roi
            // cropToGeometry
            // interpolation
            // targetSizeX
            // targetSizeY
            // bandSelectIndices
            // Writing params
            // progressListener
            downloadProcess.execute(getLayerId(MockData.USA_WORLDIMG), null, "image/tiff", null, CRS.decode("EPSG:4326", true), roi, true, null, null, null, null, null, new NullProgressListener());
            Assert.assertFalse(true);
        } catch (ProcessException e) {
            Assert.assertEquals("java.lang.IllegalArgumentException: Download Limits Exceeded. Unable to proceed!: Download Limits Exceeded. Unable to proceed!", ((e.getMessage()) + ((e.getCause()) != null ? ": " + (e.getCause().getMessage()) : "")));
        }
    }

    /**
     * Test download estimator write limits raster. The result should exceed the limits
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void testDownloadEstimatorWriteLimitsRaster() throws Exception {
        // Estimator process for checking limits
        DownloadEstimatorProcess limits = new DownloadEstimatorProcess(new StaticDownloadServiceConfiguration(new DownloadServiceConfiguration(NO_LIMIT, NO_LIMIT, NO_LIMIT, 10, DEFAULT_COMPRESSION_LEVEL, NO_LIMIT)), getGeoServer());
        final WPSResourceManager resourceManager = getResourceManager();
        // Creates the new process for the download
        DownloadProcess downloadProcess = new DownloadProcess(getGeoServer(), limits, resourceManager);
        // ROI
        Polygon roi = ((Polygon) (new WKTReader2().read("POLYGON (( -127.57473954542964 54.06575021619523, -130.88669845369998 52.00807146727025, -129.50812897394974 49.85372324691927, -130.5300633861675 49.20465679591609, -129.25955033314003 48.60392508062591, -128.00975216684665 50.986137055052474, -125.8623089087404 48.63154492960477, -123.984159178178 50.68231871628503, -126.91186316993704 52.15307567440926, -125.3444367403868 53.54787804784162, -127.57473954542964 54.06575021619523 ))")));
        roi.setSRID(4326);
        try {
            // Download the data with ROI. It should throw an exception
            // layerName
            // filter
            // outputFormat
            // targetCRS
            // roiCRS
            // roi
            // cropToGeometry
            // interpolation
            // targetSizeX
            // targetSizeY
            // bandSelectIndices
            // Writing params
            // progressListener
            downloadProcess.execute(getLayerId(MockData.USA_WORLDIMG), null, "image/tiff", null, CRS.decode("EPSG:4326", true), roi, true, null, null, null, null, null, new NullProgressListener());
            Assert.assertFalse(true);
        } catch (ProcessException e) {
            Assert.assertEquals("org.geotools.process.ProcessException: java.io.IOException: Download Exceeded the maximum HARD allowed size!: java.io.IOException: Download Exceeded the maximum HARD allowed size!", ((e.getMessage()) + ((e.getCause()) != null ? ": " + (e.getCause().getMessage()) : "")));
        }
    }

    /**
     * Test download estimator write limits raster for scaled output. Scaled image should exceed the
     * limits, whereas the original raster should not.
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void testDownloadEstimatorWriteLimitsScaledRaster() throws Exception {
        // Estimator process for checking limits
        DownloadEstimatorProcess limits = new DownloadEstimatorProcess(new StaticDownloadServiceConfiguration(// 900KB
        new DownloadServiceConfiguration(NO_LIMIT, NO_LIMIT, NO_LIMIT, 921600, DEFAULT_COMPRESSION_LEVEL, NO_LIMIT)), getGeoServer());
        final WPSResourceManager resourceManager = getResourceManager();
        // Creates the new process for the download
        DownloadProcess downloadProcess = new DownloadProcess(getGeoServer(), limits, resourceManager);
        File nonScaled = // layerName
        // filter
        // outputFormat
        // targetCRS
        // roiCRS
        // roi
        // cropToGeometry
        // interpolation
        // targetSizeX
        // targetSizeY
        // bandSelectIndices
        // Writing params
        // progressListener
        downloadProcess.execute(getLayerId(MockData.USA_WORLDIMG), null, "image/tiff", null, CRS.decode("EPSG:4326", true), null, false, null, null, null, null, null, new NullProgressListener());
        Assert.assertNotNull(nonScaled);
        GeoTiffReader reader = null;
        GridCoverage2D gc = null;
        try {
            final File[] tiffFiles = extractFiles(nonScaled, "GTIFF");
            Assert.assertNotNull(tiffFiles);
            Assert.assertTrue(((tiffFiles.length) > 0));
            reader = new GeoTiffReader(tiffFiles[0]);
            gc = reader.read(null);
            Assert.assertNotNull(gc);
            // ten times the size of the original coverage
            int targetSizeX = ((int) ((gc.getGridGeometry().getGridRange2D().getWidth()) * 10));
            int targetSizeY = ((int) ((gc.getGridGeometry().getGridRange2D().getHeight()) * 10));
            // layerName
            // filter
            // outputFormat
            // targetCRS
            // roiCRS
            // roi
            // cropToGeometry
            // interpolation
            // targetSizeX
            // targetSizeY
            // bandSelectIndices
            // Writing params
            // progressListener
            downloadProcess.execute(getLayerId(MockData.USA_WORLDIMG), null, "image/tiff", null, CRS.decode("EPSG:4326", true), null, false, null, targetSizeX, targetSizeY, null, null, new NullProgressListener());
            // exception should have been thrown at this stage
            Assert.assertFalse(true);
        } catch (ProcessException e) {
            Assert.assertEquals("org.geotools.process.ProcessException: java.io.IOException: Download Exceeded the maximum HARD allowed size!: java.io.IOException: Download Exceeded the maximum HARD allowed size!", ((e.getMessage()) + ((e.getCause()) != null ? ": " + (e.getCause().getMessage()) : "")));
        } finally {
            if (gc != null) {
                CoverageCleanerCallback.disposeCoverage(gc);
            }
            if (reader != null) {
                reader.dispose();
            }
            // clean up process
            resourceManager.finished(resourceManager.getExecutionId(true));
        }
        // Test same process for checking write output limits, using selected band indices
        limits = new DownloadEstimatorProcess(new StaticDownloadServiceConfiguration(// = 100x100 pixels x 3 bands x 1 byte (8 bits) per
        // band
        new DownloadServiceConfiguration(NO_LIMIT, NO_LIMIT, 30000, NO_LIMIT, DEFAULT_COMPRESSION_LEVEL, NO_LIMIT)), getGeoServer());
        downloadProcess = new DownloadProcess(getGeoServer(), limits, resourceManager);
        try {
            // create a scaled 100x100 raster, with 4 bands
            int targetSizeX = 100;
            int targetSizeY = 100;
            int[] bandIndices = new int[]{ 0, 2, 2, 2 };
            File scaled = // layerName
            // filter
            // outputFormat
            // targetCRS
            // roiCRS
            // roi
            // cropToGeometry
            // interpolation
            // targetSizeX
            // targetSizeY
            // bandSelectIndices
            // Writing params
            // progressListener
            downloadProcess.execute(getLayerId(MockData.USA_WORLDIMG), null, "image/tiff", null, CRS.decode("EPSG:4326", true), null, false, null, targetSizeX, targetSizeY, bandIndices, null, new NullProgressListener());
            // exception should have been thrown at this stage
            Assert.assertFalse(true);
        } catch (ProcessException e) {
            Assert.assertEquals(("java.lang.IllegalArgumentException: Download Limits Exceeded. " + "Unable to proceed!: Download Limits Exceeded. Unable to proceed!"), ((e.getMessage()) + ((e.getCause()) != null ? ": " + (e.getCause().getMessage()) : "")));
        }
    }

    /**
     * Test download estimator for raster data. The result should exceed the integer limits
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void testDownloadEstimatorIntegerMaxValueLimitRaster() throws Exception {
        // Estimator process for checking limits
        DownloadEstimatorProcess limits = new DownloadEstimatorProcess(new StaticDownloadServiceConfiguration(// huge number, way above integer limits
        new DownloadServiceConfiguration(NO_LIMIT, ((long) (1.0E12)), NO_LIMIT, NO_LIMIT, DEFAULT_COMPRESSION_LEVEL, NO_LIMIT)), getGeoServer());
        final WPSResourceManager resourceManager = getResourceManager();
        // Creates the new process for the download
        DownloadProcess downloadProcess = new DownloadProcess(getGeoServer(), limits, resourceManager);
        // ROI as polygon
        Polygon roi = ((Polygon) (new WKTReader2().read("POLYGON (( -127.57473954542964 54.06575021619523, -130.8545966116691 52.00807146727025, -129.50812897394974 49.85372324691927, -130.5300633861675 49.20465679591609, -129.25955033314003 48.60392508062591, -128.00975216684665 50.986137055052474, -125.8623089087404 48.63154492960477, -123.984159178178 50.68231871628503, -126.91186316993704 52.15307567440926, -125.3444367403868 53.54787804784162, -127.57473954542964 54.06575021619523 ))")));
        roi.setSRID(4326);
        try {
            // Download the data with ROI. It should throw an exception
            // layerName
            // filter
            // outputFormat
            // targetCRS
            // roiCRS
            // roi
            // cropToGeometry
            // interpolation
            // targetSizeX
            // targetSizeY
            // bandSelectIndices
            // Writing params
            // progressListener
            downloadProcess.execute(getLayerId(MockData.USA_WORLDIMG), null, "image/tiff", null, CRS.decode("EPSG:4326", true), roi, false, null, 100000, 60000, null, null, new NullProgressListener());
            Assert.fail();
        } catch (ProcessException e) {
            Assert.assertEquals("java.lang.IllegalArgumentException: Download Limits Exceeded. Unable to proceed!", e.getMessage());
        }
    }

    /**
     * Test download estimator for raster data. Make sure the estimator works again full raster at
     * native resolution downloads
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void testDownloadEstimatorFullNativeRaster() throws Exception {
        // Estimator process for checking limits
        DownloadEstimatorProcess limits = new DownloadEstimatorProcess(new StaticDownloadServiceConfiguration(// small number, but before fix it was not
        // triggering exception
        new DownloadServiceConfiguration(NO_LIMIT, ((long) (10)), NO_LIMIT, NO_LIMIT, DEFAULT_COMPRESSION_LEVEL, NO_LIMIT)), getGeoServer());
        // Estimate download full data at native resolution. It should return false
        Assert.assertFalse(// layerName
        // filter
        // target CRS
        // ROI CRS
        // ROI
        // clip
        // targetSizeX
        // targetSizeY
        // band indices
        // progressListener
        limits.execute(getLayerId(MockData.USA_WORLDIMG), null, null, null, null, false, null, null, null, new NullProgressListener()));
    }

    /**
     * Test download estimator for vectorial data. The result should be exceed the hard output
     * limits
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void testDownloadEstimatorHardOutputLimit() throws Exception {
        // Estimator process for checking limits
        DownloadEstimatorProcess limits = new DownloadEstimatorProcess(new StaticDownloadServiceConfiguration(new DownloadServiceConfiguration(NO_LIMIT, NO_LIMIT, NO_LIMIT, 10, DEFAULT_COMPRESSION_LEVEL, NO_LIMIT)), getGeoServer());
        final WPSResourceManager resourceManager = getResourceManager();
        // Creates the new process for the download
        DownloadProcess downloadProcess = new DownloadProcess(getGeoServer(), limits, resourceManager);
        try {
            // Download the features. It should throw an exception
            // layerName
            // filter
            // outputFormat
            // targetCRS
            // roiCRS
            // roi
            // cropToGeometry
            // interpolation
            // targetSizeX
            // targetSizeY
            // bandSelectIndices
            // Writing params
            // progressListener
            downloadProcess.execute(getLayerId(MockData.POLYGONS), null, "application/zip", null, CRS.decode("EPSG:32615"), DownloadProcessTest.roi, false, null, null, null, null, null, new NullProgressListener());
            Assert.assertFalse(true);
        } catch (ProcessException e) {
            Assert.assertEquals("java.io.IOException: Download Exceeded the maximum HARD allowed size!: Download Exceeded the maximum HARD allowed size!", ((e.getMessage()) + ((e.getCause()) != null ? ": " + (e.getCause().getMessage()) : "")));
        }
    }

    /**
     * Test download physical limit for raster data. It should throw an exception
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void testDownloadPhysicalLimitsRaster() throws Exception {
        final WPSResourceManager resourceManager = getResourceManager();
        DownloadProcessTest.ProcessListener listener = new DownloadProcessTest.ProcessListener(new ExecutionStatus(new NameImpl("gs", "DownloadEstimator"), resourceManager.getExecutionId(false), false));
        // Estimator process for checking limits
        DownloadEstimatorProcess limits = new DownloadEstimatorProcess(new StaticDownloadServiceConfiguration(), getGeoServer());
        // Creates the new process for the download
        DownloadProcess downloadProcess = new DownloadProcess(getGeoServer(), limits, resourceManager);
        // ROI data
        Polygon roi = ((Polygon) (new WKTReader2().read("POLYGON (( -127.57473954542964 54.06575021619523, -130.88669845369998 52.00807146727025, -129.50812897394974 49.85372324691927, -130.5300633861675 49.20465679591609, -129.25955033314003 48.60392508062591, -128.00975216684665 50.986137055052474, -125.8623089087404 48.63154492960477, -123.984159178178 50.68231871628503, -126.91186316993704 52.15307567440926, -125.3444367403868 53.54787804784162, -127.57473954542964 54.06575021619523 ))")));
        roi.setSRID(4326);
        try {
            // Download the data. It should throw an exception
            // layerName
            // filter
            // outputFormat
            // targetCRS
            // roiCRS
            // roi
            // cropToGeometry
            // interpolation
            // targetSizeX
            // targetSizeY
            // bandSelectIndices
            // Writing params
            // progressListener
            downloadProcess.execute(getLayerId(MockData.USA_WORLDIMG), null, "image/tiff", null, CRS.decode("EPSG:4326", true), roi, true, null, null, null, null, null, listener);
        } catch (Exception e) {
            Throwable e1 = listener.exception;
            Assert.assertNotNull(e1);
            Assert.assertEquals("org.geotools.process.ProcessException: java.io.IOException: Download Exceeded the maximum HARD allowed size!: java.io.IOException: Download Exceeded the maximum HARD allowed size!", ((e.getMessage()) + ((e.getCause()) != null ? ": " + (e.getCause().getMessage()) : "")));
        }
    }

    /**
     * Test download physical limit for vectorial data. It should throw an exception
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void testDownloadPhysicalLimitsVector() throws Exception {
        final WPSResourceManager resourceManager = getResourceManager();
        DownloadProcessTest.ProcessListener listener = new DownloadProcessTest.ProcessListener(new ExecutionStatus(new NameImpl("gs", "DownloadEstimator"), resourceManager.getExecutionId(false), false));
        // Estimator process for checking limits
        DownloadEstimatorProcess limits = new DownloadEstimatorProcess(new StaticDownloadServiceConfiguration(new DownloadServiceConfiguration(NO_LIMIT, NO_LIMIT, NO_LIMIT, 1, DEFAULT_COMPRESSION_LEVEL, NO_LIMIT)), getGeoServer());
        // Creates the new process for the download
        DownloadProcess downloadProcess = new DownloadProcess(getGeoServer(), limits, resourceManager);
        try {
            // Download the features. It should throw an exception
            // layerName
            // filter
            // outputFormat
            // targetCRS
            // roiCRS
            // roi
            // cropToGeometry
            // interpolation
            // targetSizeX
            // targetSizeY
            // bandSelectIndices
            // Writing params
            // progressListener
            downloadProcess.execute(getLayerId(MockData.POLYGONS), null, "application/zip", null, CRS.decode("EPSG:32615"), DownloadProcessTest.roi, false, null, null, null, null, null, listener);
        } catch (ProcessException e) {
            Assert.assertEquals("java.io.IOException: Download Exceeded the maximum HARD allowed size!: Download Exceeded the maximum HARD allowed size!", ((e.getMessage()) + ((e.getCause()) != null ? ": " + (e.getCause().getMessage()) : "")));
            Throwable le = listener.exception;
            Assert.assertEquals("java.io.IOException: Download Exceeded the maximum HARD allowed size!: Download Exceeded the maximum HARD allowed size!", ((le.getMessage()) + ((le.getCause()) != null ? ": " + (le.getCause().getMessage()) : "")));
            return;
        }
        Assert.assertFalse(true);
    }

    /**
     * Test with a wrong output format. It should thrown an exception.
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void testWrongOutputFormat() throws Exception {
        // Estimator process for checking limits
        DownloadEstimatorProcess limits = new DownloadEstimatorProcess(new StaticDownloadServiceConfiguration(), getGeoServer());
        final WPSResourceManager resourceManager = getResourceManager();
        // Creates the new process for the download
        DownloadProcess downloadProcess = new DownloadProcess(getGeoServer(), limits, resourceManager);
        FeatureTypeInfo ti = getCatalog().getFeatureTypeByName(getLayerId(MockData.POLYGONS));
        SimpleFeatureCollection rawSource = ((SimpleFeatureCollection) (ti.getFeatureSource(null, null).getFeatures()));
        final DefaultProgressListener progressListener = new DefaultProgressListener();
        try {
            // Download the features. It should throw an exception.
            // layerName
            // filter
            // outputFormat
            // targetCRS
            // roiCRS
            // roi
            // cropToGeometry
            // interpolation
            // targetSizeX
            // targetSizeY
            // bandSelectIndices
            // Writing params
            // progressListener
            downloadProcess.execute(getLayerId(MockData.POLYGONS), null, "IAmWrong!!!", null, CRS.decode("EPSG:32615"), DownloadProcessTest.roi, false, null, null, null, null, null, progressListener);
            Assert.assertTrue("We did not get an exception", false);
        } catch (Exception e) {
            Assert.assertTrue("Everything as expected", true);
        }
    }

    /**
     * Test download of raster data using underlying granules resolution. The sample mosaic is
     * composed of:
     *
     * <p>18km_32610.tif with resolution = 17550.948453185396000 meters 9km_32610.tif with
     * resolution = 8712.564801039759900 meters
     */
    @Test
    public void testDownloadGranuleHeterogeneousResolution() throws Exception {
        DownloadEstimatorProcess limits = new DownloadEstimatorProcess(new StaticDownloadServiceConfiguration(), getGeoServer());
        final WPSResourceManager resourceManager = getResourceManager();
        DownloadProcess downloadProcess = new DownloadProcess(getGeoServer(), limits, resourceManager);
        // Setting filter to get the granule with resolution
        final PropertyName property = DownloadProcessTest.FF.property("resolution");
        Filter filter = ((Filter) (DownloadProcessTest.FF.greaterOrEqual(property, DownloadProcessTest.FF.literal(16000))));
        testExpectedResolution(downloadProcess, filter, CRS.decode("EPSG:4326", true), DownloadProcessTest.ROI2, resourceManager, 17550.94845318, (-17550.94845318));
        // Download native resolution 2
        filter = DownloadProcessTest.FF.and(DownloadProcessTest.FF.lessOrEqual(property, DownloadProcessTest.FF.literal(10000)), DownloadProcessTest.FF.greaterOrEqual(property, DownloadProcessTest.FF.literal(1000)));
        testExpectedResolution(downloadProcess, filter, null, null, resourceManager, 8712.56480103976, (-8712.56480103976));
        // Download native resolution 3
        filter = ((Filter) (DownloadProcessTest.FF.lessOrEqual(property, DownloadProcessTest.FF.literal(1000))));
        // Final checks on the result
        testExpectedResolution(downloadProcess, filter, null, null, resourceManager, 7818.453242658203, (-10139.712928934865));
        filter = DownloadProcessTest.FF.and(DownloadProcessTest.FF.lessOrEqual(property, DownloadProcessTest.FF.literal(10000)), DownloadProcessTest.FF.greaterOrEqual(property, DownloadProcessTest.FF.literal(1000)));
        File rasterZip = // layerName
        // filter
        // outputFormat
        // targetCRS
        // roi
        // cropToGeometry
        // interpolation
        // targetSizeX
        // targetSizeY
        // bandSelectIndices
        // Writing params
        // progressListener
        downloadProcess.execute(getLayerId(DownloadProcessTest.MIXED_RES), filter, "image/tiff", null, CRS.decode("EPSG:32610", true), DownloadProcessTest.ROI3, false, null, 512, 128, null, null, new NullProgressListener());
        Assert.assertNotNull(rasterZip);
        GeoTiffReader reader = null;
        GridCoverage2D gc = null;
        try {
            final File[] tiffFiles = extractFiles(rasterZip, "GTIFF");
            Assert.assertNotNull(tiffFiles);
            Assert.assertTrue(((tiffFiles.length) > 0));
            reader = new GeoTiffReader(tiffFiles[0]);
            gc = reader.read(null);
            // check coverage size
            RenderedImage ri = gc.getRenderedImage();
            Assert.assertEquals(512, ri.getWidth());
            Assert.assertEquals(128, ri.getHeight());
        } finally {
            if (gc != null) {
                CoverageCleanerCallback.disposeCoverage(gc);
            }
            if (reader != null) {
                reader.dispose();
            }
            // clean up process
            resourceManager.finished(resourceManager.getExecutionId(true));
        }
    }

    /**
     * Test PNG outputFormat
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void testDownloadPNG() throws Exception {
        testDownloadByOutputFormat("image/png");
    }

    /**
     * Test JPEG outputFormat
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void testDownloadJPEG() throws Exception {
        testDownloadByOutputFormat("image/jpeg");
    }

    /**
     * The listener interface for receiving process events. The class that is interested in processing a process event implements this interface, and
     * the object created with that class is registered with a component using the component's <code>addProcessListener<code> method. When
     * the process event occurs, that object's appropriate
     * method is invoked.
     *
     * @see ProcessEvent
     */
    static class ProcessListener implements ProgressListener {
        /**
         * The Constant LOGGER.
         */
        static final Logger LOGGER = Logging.getLogger(DownloadProcessTest.ProcessListener.class);

        /**
         * The status.
         */
        ExecutionStatus status;

        /**
         * The task.
         */
        InternationalString task;

        /**
         * The description.
         */
        String description;

        /**
         * The exception.
         */
        Throwable exception;

        /**
         * Instantiates a new process listener.
         *
         * @param status
         * 		the status
         */
        public ProcessListener(ExecutionStatus status) {
            this.status = status;
        }

        /**
         * Gets the task.
         *
         * @return the task
         */
        public InternationalString getTask() {
            return task;
        }

        /**
         * Sets the task.
         *
         * @param task
         * 		the new task
         */
        public void setTask(InternationalString task) {
            this.task = task;
        }

        /**
         * Gets the description.
         *
         * @return the description
         */
        public String getDescription() {
            return this.description;
        }

        /**
         * Sets the description.
         *
         * @param description
         * 		the new description
         */
        public void setDescription(String description) {
            this.description = description;
        }

        /**
         * Started.
         */
        public void started() {
            status.setPhase(RUNNING);
        }

        /**
         * Progress.
         *
         * @param percent
         * 		the percent
         */
        public void progress(float percent) {
            status.setProgress(percent);
        }

        /**
         * Gets the progress.
         *
         * @return the progress
         */
        public float getProgress() {
            return status.getProgress();
        }

        /**
         * Complete.
         */
        public void complete() {
            // nothing to do
        }

        /**
         * Dispose.
         */
        public void dispose() {
            // nothing to do
        }

        /**
         * Checks if is canceled.
         *
         * @return true, if is canceled
         */
        public boolean isCanceled() {
            return (status.getPhase()) == (ProcessState.DISMISSING);
        }

        /**
         * Sets the canceled.
         *
         * @param cancel
         * 		the new canceled
         */
        public void setCanceled(boolean cancel) {
            if (cancel == true) {
                status.setPhase(DISMISSING);
            }
        }

        /**
         * Warning occurred.
         *
         * @param source
         * 		the source
         * @param location
         * 		the location
         * @param warning
         * 		the warning
         */
        public void warningOccurred(String source, String location, String warning) {
            DownloadProcessTest.ProcessListener.LOGGER.log(Level.WARNING, ((("Got a warning during process execution " + (status.getExecutionId())) + ": ") + warning));
        }

        /**
         * Exception occurred.
         *
         * @param exception
         * 		the exception
         */
        public void exceptionOccurred(Throwable exception) {
            this.exception = exception;
        }
    }
}

