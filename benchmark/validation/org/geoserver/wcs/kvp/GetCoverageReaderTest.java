/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wcs.kvp;


import java.util.Map;
import net.opengis.wcs10.GetCoverageType;
import org.geoserver.wcs.test.WCSTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.vfny.geoserver.wcs.WcsException;


public class GetCoverageReaderTest extends WCSTestSupport {
    static Wcs10GetCoverageRequestReader reader;

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
        raw.put("SourceCoverage", layerId);
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
        raw.put("version", "1.0.0");
        raw.put("BBOX", "-45,146,-42,147");
        raw.put("crs", "EPSG:4326");
        raw.put("width", "150");
        raw.put("height", "150");
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
        raw.put("sourcecoverage", layerId);
        raw.put("format", "SuperCoolFormat");
        raw.put("BBOX", "-45,146,-42,147");
        raw.put("crs", "EPSG:4326");
        try {
            GetCoverageReaderTest.reader.read(GetCoverageReaderTest.reader.createRequest(), parseKvp(raw), raw);
            Assert.fail("That coverage is not registered???");
        } catch (WcsException e) {
            Assert.assertEquals(InvalidParameterValue.toString(), e.getCode());
            Assert.assertEquals("sourcecoverage", e.getLocator());
        }
    }

    @Test
    public void testBasic() throws Exception {
        Map<String, Object> raw = baseMap();
        final String layerId = getLayerId(TASMANIA_BM);
        raw.put("SourceCoverage", layerId);
        raw.put("version", "1.0.0");
        raw.put("format", "image/tiff");
        raw.put("BBOX", "-45,146,-42,147");
        raw.put("CRS", "EPSG:4326");
        raw.put("width", "150");
        raw.put("height", "150");
        GetCoverageType getCoverage = ((GetCoverageType) (GetCoverageReaderTest.reader.read(GetCoverageReaderTest.reader.createRequest(), parseKvp(raw), raw)));
        Assert.assertEquals(layerId, getCoverage.getSourceCoverage());
        Assert.assertEquals("image/tiff", getCoverage.getOutput().getFormat().getValue());
        Assert.assertEquals("EPSG:4326", getCoverage.getOutput().getCrs().getValue());
    }

    @Test
    public void testInterpolation() throws Exception {
        Map<String, Object> raw = baseMap();
        String layerId = getLayerId(TASMANIA_BM);
        raw.put("SourceCoverage", layerId);
        raw.put("version", "1.0.0");
        raw.put("format", "image/tiff");
        raw.put("BBOX", "-45,146,-42,147");
        raw.put("CRS", "EPSG:4326");
        raw.put("width", "150");
        raw.put("height", "150");
        raw.put("interpolation", "nearest neighbor");
        GetCoverageType getCoverage = ((GetCoverageType) (GetCoverageReaderTest.reader.read(GetCoverageReaderTest.reader.createRequest(), parseKvp(raw), raw)));
        Assert.assertEquals(layerId, getCoverage.getSourceCoverage());
        Assert.assertEquals("image/tiff", getCoverage.getOutput().getFormat().getValue());
        Assert.assertEquals("nearest neighbor", getCoverage.getInterpolationMethod().toString());
        // bilinear
        raw = baseMap();
        raw.put("SourceCoverage", layerId);
        raw.put("version", "1.0.0");
        raw.put("format", "image/tiff");
        raw.put("BBOX", "-45,146,-42,147");
        raw.put("CRS", "EPSG:4326");
        raw.put("width", "150");
        raw.put("height", "150");
        raw.put("interpolation", "bilinear");
        getCoverage = ((GetCoverageType) (GetCoverageReaderTest.reader.read(GetCoverageReaderTest.reader.createRequest(), parseKvp(raw), raw)));
        Assert.assertEquals(layerId, getCoverage.getSourceCoverage());
        Assert.assertEquals("image/tiff", getCoverage.getOutput().getFormat().getValue());
        Assert.assertEquals("bilinear", getCoverage.getInterpolationMethod().toString());
        // nearest
        raw = baseMap();
        raw.put("SourceCoverage", layerId);
        raw.put("version", "1.0.0");
        raw.put("format", "image/tiff");
        raw.put("BBOX", "-45,146,-42,147");
        raw.put("CRS", "EPSG:4326");
        raw.put("width", "150");
        raw.put("height", "150");
        raw.put("interpolation", "nearest");
        getCoverage = ((GetCoverageType) (GetCoverageReaderTest.reader.read(GetCoverageReaderTest.reader.createRequest(), parseKvp(raw), raw)));
        Assert.assertEquals(layerId, getCoverage.getSourceCoverage());
        Assert.assertEquals("image/tiff", getCoverage.getOutput().getFormat().getValue());
        Assert.assertEquals("nearest neighbor", getCoverage.getInterpolationMethod().toString());
        // bicubic
        raw = baseMap();
        raw.put("SourceCoverage", layerId);
        raw.put("version", "1.0.0");
        raw.put("format", "image/tiff");
        raw.put("BBOX", "-45,146,-42,147");
        raw.put("CRS", "EPSG:4326");
        raw.put("width", "150");
        raw.put("height", "150");
        raw.put("interpolation", "bicubic");
        getCoverage = ((GetCoverageType) (GetCoverageReaderTest.reader.read(GetCoverageReaderTest.reader.createRequest(), parseKvp(raw), raw)));
        Assert.assertEquals(layerId, getCoverage.getSourceCoverage());
        Assert.assertEquals("image/tiff", getCoverage.getOutput().getFormat().getValue());
        Assert.assertEquals("bicubic", getCoverage.getInterpolationMethod().toString());
    }

    @Test
    public void testUnsupportedCRS() throws Exception {
        Map<String, Object> raw = baseMap();
        final String layerId = getLayerId(TASMANIA_BM);
        raw.put("SourceCoverage", layerId);
        raw.put("version", "1.0.0");
        raw.put("format", "image/tiff");
        raw.put("CRS", "urn:ogc:def:crs:EPSG:6.6:-1000");
        raw.put("width", "150");
        raw.put("height", "150");
        try {
            GetCoverageReaderTest.reader.read(GetCoverageReaderTest.reader.createRequest(), parseKvp(raw), raw);
            Assert.fail("We should have had a WcsException here?");
        } catch (WcsException e) {
            Assert.assertEquals("crs", e.getLocator());
            Assert.assertEquals("InvalidParameterValue", e.getCode());
        }
    }
}

