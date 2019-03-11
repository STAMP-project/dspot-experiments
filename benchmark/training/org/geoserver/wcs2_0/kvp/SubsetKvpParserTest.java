/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wcs2_0.kvp;


import net.opengis.wcs20.DimensionSliceType;
import net.opengis.wcs20.DimensionTrimType;
import org.geoserver.platform.OWS20Exception;
import org.junit.Assert;
import org.junit.Test;


public class SubsetKvpParserTest {
    SubsetKvpParser parser = new SubsetKvpParser();

    @Test
    public void testInvalidValues() throws Exception {
        try {
            parser.parse("test");
            Assert.fail("should have thrown an exception");
        } catch (OWS20Exception e) {
            checkInvalidSyntaxException(e);
        }
        try {
            parser.parse("test,EPSG:4326");
            Assert.fail("should have thrown an exception");
        } catch (OWS20Exception e) {
            checkInvalidSyntaxException(e);
        }
        try {
            parser.parse("test(,");
            Assert.fail("should have thrown an exception");
        } catch (OWS20Exception e) {
            checkInvalidSyntaxException(e);
        }
        try {
            parser.parse("test(,)");
            Assert.fail("should have thrown an exception");
        } catch (OWS20Exception e) {
            checkInvalidSyntaxException(e);
        }
        try {
            parser.parse("test(\"abc,def\",abc)");
            Assert.fail("should have thrown an exception");
        } catch (OWS20Exception e) {
            checkInvalidSyntaxException(e);
        }
    }

    @Test
    public void testSliceString() throws Exception {
        DimensionSliceType result = ((DimensionSliceType) (parser.parse("mydim(\"myvalue\")")));
        Assert.assertEquals("mydim", result.getDimension());
        Assert.assertEquals("myvalue", result.getSlicePoint());
    }

    @Test
    public void testSliceNumber() throws Exception {
        DimensionSliceType result = ((DimensionSliceType) (parser.parse("mydim(12345)")));
        Assert.assertEquals("mydim", result.getDimension());
        Assert.assertEquals("12345", result.getSlicePoint());
    }

    @Test
    public void testTrimOpen() throws Exception {
        // not sure if this makes any sense, but the sytax allows it, and it's like not
        // specifying anything
        DimensionTrimType result = ((DimensionTrimType) (parser.parse("lon,EPSG:4326(*,*)")));
        Assert.assertEquals("lon", result.getDimension());
        Assert.assertEquals("EPSG:4326", result.getCRS());
        Assert.assertNull(result.getTrimLow());
        Assert.assertNull(result.getTrimHigh());
    }

    @Test
    public void testTrimNumbers() throws Exception {
        // not sure if this makes any sense, but the sytax allows it, and it's like not
        // specifying anything
        DimensionTrimType result = ((DimensionTrimType) (parser.parse("lon,EPSG:4326(10,20)")));
        Assert.assertEquals("lon", result.getDimension());
        Assert.assertEquals("EPSG:4326", result.getCRS());
        Assert.assertEquals("10", result.getTrimLow());
        Assert.assertEquals("20", result.getTrimHigh());
    }

    @Test
    public void testTrimStringsCommas() throws Exception {
        // not sure if this makes any sense, but the sytax allows it, and it's like not
        // specifying anything
        DimensionTrimType result = ((DimensionTrimType) (parser.parse("mydim(\"a,b\",\"c,d\")")));
        Assert.assertEquals("mydim", result.getDimension());
        Assert.assertNull(result.getCRS());
        Assert.assertEquals("a,b", result.getTrimLow());
        Assert.assertEquals("c,d", result.getTrimHigh());
    }
}

