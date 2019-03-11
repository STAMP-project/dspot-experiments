/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wcs.kvp;


import GridCS.GCSGrid2dSquare;
import GridType.GT2dGridIn2dCrs;
import GridType.GT2dGridIn3dCrs;
import GridType.GT2dSimpleGrid;
import java.util.Map;
import net.opengis.wcs11.GetCoverageType;
import org.geoserver.wcs.test.WCSTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.vfny.geoserver.wcs.WcsException;


public class GetCoverageReaderTest extends WCSTestSupport {
    static GetCoverageRequestReader reader;

    @Test
    public void testMissingParams() throws Exception {
        Map<String, Object> raw = baseMap();
        try {
            GetCoverageReaderTest.reader.read(GetCoverageReaderTest.reader.createRequest(), parseKvp(raw), raw);
            Assert.fail("Hey, format is missing, this should have failed");
        } catch (WcsException e) {
            Assert.assertEquals("MissingParameterValue", e.getCode());
        }
        final String layerId = getLayerId(TASMANIA_BM);
        raw.put("identifier", layerId);
        try {
            GetCoverageReaderTest.reader.read(GetCoverageReaderTest.reader.createRequest(), parseKvp(raw), raw);
            Assert.fail("Hey, format is missing, this should have failed");
        } catch (WcsException e) {
            Assert.assertEquals("MissingParameterValue", e.getCode());
        }
        raw.put("format", "image/tiff");
        try {
            GetCoverageReaderTest.reader.read(GetCoverageReaderTest.reader.createRequest(), parseKvp(raw), raw);
            Assert.fail("Hey, boundingBox is missing, this should have failed");
        } catch (WcsException e) {
            Assert.assertEquals("MissingParameterValue", e.getCode());
        }
        raw.put("BoundingBox", "-45,146,-42,147");
        try {
            GetCoverageReaderTest.reader.read(GetCoverageReaderTest.reader.createRequest(), parseKvp(raw), raw);
        } catch (WcsException e) {
            Assert.fail("This time all mandatory params where provided?");
            Assert.assertEquals("MissingParameterValue", e.getCode());
        }
    }

    @Test
    public void testUnknownCoverageParams() throws Exception {
        Map<String, Object> raw = baseMap();
        final String layerId = "fairyTales:rumpelstilskin";
        raw.put("identifier", layerId);
        raw.put("format", "SuperCoolFormat");
        raw.put("BoundingBox", "-45,146,-42,147");
        try {
            GetCoverageReaderTest.reader.read(GetCoverageReaderTest.reader.createRequest(), parseKvp(raw), raw);
            Assert.fail("That coverage is not registered???");
        } catch (WcsException e) {
            Assert.assertEquals(InvalidParameterValue.toString(), e.getCode());
            Assert.assertEquals("identifier", e.getLocator());
        }
    }

    @Test
    public void testBasic() throws Exception {
        Map<String, Object> raw = baseMap();
        final String layerId = getLayerId(TASMANIA_BM);
        raw.put("identifier", layerId);
        raw.put("format", "image/tiff");
        raw.put("BoundingBox", "-45,146,-42,147");
        raw.put("store", "false");
        raw.put("GridBaseCRS", "urn:ogc:def:crs:EPSG:6.6:4326");
        GetCoverageType getCoverage = ((GetCoverageType) (GetCoverageReaderTest.reader.read(GetCoverageReaderTest.reader.createRequest(), parseKvp(raw), raw)));
        Assert.assertEquals(layerId, getCoverage.getIdentifier().getValue());
        Assert.assertEquals("image/tiff", getCoverage.getOutput().getFormat());
        Assert.assertFalse(getCoverage.getOutput().isStore());
        Assert.assertEquals("urn:ogc:def:crs:EPSG:6.6:4326", getCoverage.getOutput().getGridCRS().getGridBaseCRS());
    }

    @Test
    public void testUnsupportedCRS() throws Exception {
        Map<String, Object> raw = baseMap();
        final String layerId = getLayerId(TASMANIA_BM);
        raw.put("identifier", layerId);
        raw.put("format", "image/tiff");
        raw.put("GridBaseCRS", "urn:ogc:def:crs:EPSG:6.6:-1000");
        try {
            GetCoverageReaderTest.reader.read(GetCoverageReaderTest.reader.createRequest(), parseKvp(raw), raw);
            Assert.fail("We should have had a WcsException here?");
        } catch (WcsException e) {
            Assert.assertEquals("GridBaseCRS", e.getLocator());
            Assert.assertEquals("InvalidParameterValue", e.getCode());
        }
    }

    @Test
    public void testGridTypes() throws Exception {
        Map<String, Object> raw = baseMap();
        final String layerId = getLayerId(TASMANIA_BM);
        raw.put("identifier", layerId);
        raw.put("format", "image/tiff");
        raw.put("BoundingBox", "-45,146,-42,147");
        raw.put("gridType", GT2dGridIn2dCrs.getXmlConstant());
        GetCoverageType getCoverage = ((GetCoverageType) (GetCoverageReaderTest.reader.read(GetCoverageReaderTest.reader.createRequest(), parseKvp(raw), raw)));
        Assert.assertEquals(GT2dGridIn2dCrs.getXmlConstant(), getCoverage.getOutput().getGridCRS().getGridType());
        raw.put("gridType", GT2dSimpleGrid.getXmlConstant());
        getCoverage = ((GetCoverageType) (GetCoverageReaderTest.reader.read(GetCoverageReaderTest.reader.createRequest(), parseKvp(raw), raw)));
        Assert.assertEquals(GT2dSimpleGrid.getXmlConstant(), getCoverage.getOutput().getGridCRS().getGridType());
        // try with different case
        raw.put("gridType", GT2dSimpleGrid.getXmlConstant().toUpperCase());
        getCoverage = ((GetCoverageType) (GetCoverageReaderTest.reader.read(GetCoverageReaderTest.reader.createRequest(), parseKvp(raw), raw)));
        Assert.assertEquals(GT2dSimpleGrid.getXmlConstant(), getCoverage.getOutput().getGridCRS().getGridType());
        raw.put("gridType", GT2dGridIn3dCrs.getXmlConstant());
        try {
            GetCoverageReaderTest.reader.read(GetCoverageReaderTest.reader.createRequest(), parseKvp(raw), raw);
            Assert.fail("We should have had a WcsException here?");
        } catch (WcsException e) {
            Assert.assertEquals(InvalidParameterValue.name(), e.getCode());
            Assert.assertEquals("GridType", e.getLocator());
        }
        raw.put("gridType", "Hoolabaloola");
        try {
            GetCoverageReaderTest.reader.read(GetCoverageReaderTest.reader.createRequest(), parseKvp(raw), raw);
            Assert.fail("We should have had a WcsException here?");
        } catch (WcsException e) {
            Assert.assertEquals(InvalidParameterValue.name(), e.getCode());
            Assert.assertEquals("GridType", e.getLocator());
        }
    }

    @Test
    public void testGridCS() throws Exception {
        Map<String, Object> raw = baseMap();
        final String layerId = getLayerId(TASMANIA_BM);
        raw.put("identifier", layerId);
        raw.put("format", "image/tiff");
        raw.put("BoundingBox", "-45,146,-42,147");
        raw.put("GridCS", GCSGrid2dSquare.getXmlConstant());
        GetCoverageType getCoverage = ((GetCoverageType) (GetCoverageReaderTest.reader.read(GetCoverageReaderTest.reader.createRequest(), parseKvp(raw), raw)));
        Assert.assertEquals(GCSGrid2dSquare.getXmlConstant(), getCoverage.getOutput().getGridCRS().getGridCS());
        raw.put("GridCS", GCSGrid2dSquare.getXmlConstant().toUpperCase());
        getCoverage = ((GetCoverageType) (GetCoverageReaderTest.reader.read(GetCoverageReaderTest.reader.createRequest(), parseKvp(raw), raw)));
        Assert.assertEquals(GCSGrid2dSquare.getXmlConstant(), getCoverage.getOutput().getGridCRS().getGridCS());
        raw.put("GridCS", "Hoolabaloola");
        try {
            GetCoverageReaderTest.reader.read(GetCoverageReaderTest.reader.createRequest(), parseKvp(raw), raw);
            Assert.fail("We should have had a WcsException here?");
        } catch (WcsException e) {
            Assert.assertEquals(InvalidParameterValue.name(), e.getCode());
            Assert.assertEquals("GridCS", e.getLocator());
        }
    }

    @Test
    public void testGridOrigin() throws Exception {
        Map<String, Object> raw = baseMap();
        final String layerId = getLayerId(TASMANIA_BM);
        raw.put("identifier", layerId);
        raw.put("format", "image/tiff");
        raw.put("BoundingBox", "-45,146,-42,147");
        raw.put("GridOrigin", "10.5,-30.2");
        GetCoverageType getCoverage = ((GetCoverageType) (GetCoverageReaderTest.reader.read(GetCoverageReaderTest.reader.createRequest(), parseKvp(raw), raw)));
        Double[] origin = ((Double[]) (getCoverage.getOutput().getGridCRS().getGridOrigin()));
        Assert.assertEquals(2, origin.length);
        Assert.assertEquals(0, Double.compare(10.5, ((double) (origin[0]))));
        Assert.assertEquals(0, Double.compare((-30.2), ((double) (origin[1]))));
        raw.put("GridOrigin", "12");
        try {
            GetCoverageReaderTest.reader.read(GetCoverageReaderTest.reader.createRequest(), parseKvp(raw), raw);
            Assert.fail("We should have had a WcsException here?");
        } catch (WcsException e) {
            Assert.assertEquals(InvalidParameterValue.name(), e.getCode());
            Assert.assertEquals("GridOrigin", e.getLocator());
        }
        raw.put("GridOrigin", "12,a");
        try {
            GetCoverageReaderTest.reader.read(GetCoverageReaderTest.reader.createRequest(), parseKvp(raw), raw);
            Assert.fail("We should have had a WcsException here?");
        } catch (WcsException e) {
            Assert.assertEquals(InvalidParameterValue.name(), e.getCode());
            Assert.assertEquals("GridOrigin", e.getLocator());
        }
    }

    @Test
    public void testGridOffsets() throws Exception {
        Map<String, Object> raw = baseMap();
        final String layerId = getLayerId(TASMANIA_BM);
        raw.put("identifier", layerId);
        raw.put("format", "image/tiff");
        raw.put("BoundingBox", "-45,146,-42,147");
        raw.put("GridOffsets", "10.5,-30.2");
        raw.put("GridType", GT2dSimpleGrid.getXmlConstant());
        GetCoverageType getCoverage = ((GetCoverageType) (GetCoverageReaderTest.reader.read(GetCoverageReaderTest.reader.createRequest(), parseKvp(raw), raw)));
        Double[] offsets = ((Double[]) (getCoverage.getOutput().getGridCRS().getGridOffsets()));
        Assert.assertEquals(2, offsets.length);
        Assert.assertEquals(0, Double.compare(10.5, ((double) (offsets[0]))));
        Assert.assertEquals(0, Double.compare((-30.2), ((double) (offsets[1]))));
        raw.put("GridOffsets", "12");
        try {
            GetCoverageReaderTest.reader.read(GetCoverageReaderTest.reader.createRequest(), parseKvp(raw), raw);
            Assert.fail("We should have had a WcsException here?");
        } catch (WcsException e) {
            Assert.assertEquals(InvalidParameterValue.name(), e.getCode());
            Assert.assertEquals("GridOffsets", e.getLocator());
        }
        raw.put("GridOffsets", "12,a");
        try {
            GetCoverageReaderTest.reader.read(GetCoverageReaderTest.reader.createRequest(), parseKvp(raw), raw);
            Assert.fail("We should have had a WcsException here?");
        } catch (WcsException e) {
            Assert.assertEquals(InvalidParameterValue.name(), e.getCode());
            Assert.assertEquals("GridOffsets", e.getLocator());
        }
    }

    /**
     * Tests Bicubic (also called cubic) interpolation with a RangeSubset.
     */
    @Test
    public void testInterpolationBicubic() throws Exception {
        this.testRangeSubset("cubic");
    }

    /**
     * Tests Bilinear (also called linear) interpolation with a RangeSubset.
     */
    @Test
    public void testInterpolationBilinear() throws Exception {
        this.testRangeSubset("linear");
    }

    /**
     * Tests Nearest neighbor (also called nearest) interpolation with a RangeSubset.
     */
    @Test
    public void testInterpolationNearest() throws Exception {
        this.testRangeSubset("nearest");
    }
}

