/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wcs.kvp;


import net.opengis.ows11.BoundingBoxType;
import org.junit.Assert;
import org.junit.Test;


public class BoundingBoxKvpParserTest {
    BoundingBoxKvpParser parser = new BoundingBoxKvpParser();

    @Test
    public void test1DRange() throws Exception {
        executeFailingBBoxTest("10", "This bbox was invalid?");
        executeFailingBBoxTest("10,20", "This bbox was invalid?");
        executeFailingBBoxTest("10,20,30", "This bbox was invalid?");
    }

    @Test
    public void testNonNumericalRange() throws Exception {
        executeFailingBBoxTest("10,20,a,b", "This bbox was invalid?");
        executeFailingBBoxTest("a,20,30,b", "This bbox was invalid?");
    }

    @Test
    public void testOutOfDimRange() throws Exception {
        executeFailingBBoxTest("10,20,30,40,50,60,EPSG:4326", "This bbox has more dimensions than the crs?");
        executeFailingBBoxTest("10,20,30,40,EPSG:4979", "This bbox has less dimensions than the crs?");
    }

    @Test
    public void testUnknownCRS() throws Exception {
        executeFailingBBoxTest("10,20,30,40,50,60,EPSG:MakeNoPrisoners!", "This crs should definitely be unknown...");
    }

    @Test
    public void testNoCrs() throws Exception {
        BoundingBoxType bbox = ((BoundingBoxType) (parser.parse("10,20,15,30")));
        Assert.assertEquals(2, bbox.getLowerCorner().size());
        Assert.assertEquals(10.0, bbox.getLowerCorner().get(0));
        Assert.assertEquals(20.0, bbox.getLowerCorner().get(1));
        Assert.assertEquals(2, bbox.getUpperCorner().size());
        Assert.assertEquals(15.0, bbox.getUpperCorner().get(0));
        Assert.assertEquals(30.0, bbox.getUpperCorner().get(1));
        Assert.assertNull(bbox.getCrs());
    }

    @Test
    public void test2DNoCrs() throws Exception {
        BoundingBoxType bbox = ((BoundingBoxType) (parser.parse("10,20,15,30,EPSG:4326")));
        Assert.assertEquals(2, bbox.getLowerCorner().size());
        Assert.assertEquals(10.0, bbox.getLowerCorner().get(0));
        Assert.assertEquals(20.0, bbox.getLowerCorner().get(1));
        Assert.assertEquals(2, bbox.getUpperCorner().size());
        Assert.assertEquals(15.0, bbox.getUpperCorner().get(0));
        Assert.assertEquals(30.0, bbox.getUpperCorner().get(1));
        Assert.assertEquals("EPSG:4326", bbox.getCrs());
    }

    @Test
    public void test3DNoCrs() throws Exception {
        BoundingBoxType bbox = ((BoundingBoxType) (parser.parse("10,20,15,30,40,50,EPSG:4979")));
        Assert.assertEquals(3, bbox.getLowerCorner().size());
        Assert.assertEquals(10.0, bbox.getLowerCorner().get(0));
        Assert.assertEquals(20.0, bbox.getLowerCorner().get(1));
        Assert.assertEquals(15.0, bbox.getLowerCorner().get(2));
        Assert.assertEquals(3, bbox.getUpperCorner().size());
        Assert.assertEquals(30.0, bbox.getUpperCorner().get(0));
        Assert.assertEquals(40.0, bbox.getUpperCorner().get(1));
        Assert.assertEquals(50.0, bbox.getUpperCorner().get(2));
        Assert.assertEquals("EPSG:4979", bbox.getCrs());
    }

    @Test
    public void testWgs84FullExtent() throws Exception {
        BoundingBoxType bbox = ((BoundingBoxType) (parser.parse("-180,-90,180,90,urn:ogc:def:crs:EPSG:4326")));
        Assert.assertEquals(2, bbox.getLowerCorner().size());
        Assert.assertEquals((-180.0), bbox.getLowerCorner().get(0));
        Assert.assertEquals((-90.0), bbox.getLowerCorner().get(1));
        Assert.assertEquals(2, bbox.getUpperCorner().size());
        Assert.assertEquals(180.0, bbox.getUpperCorner().get(0));
        Assert.assertEquals(90.0, bbox.getUpperCorner().get(1));
        Assert.assertEquals("urn:ogc:def:crs:EPSG:4326", bbox.getCrs());
    }
}

