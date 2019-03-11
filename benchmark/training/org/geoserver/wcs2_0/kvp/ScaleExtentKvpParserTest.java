/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wcs2_0.kvp;


import net.opengis.wcs20.ScaleToExtentType;
import net.opengis.wcs20.TargetAxisExtentType;
import org.geoserver.platform.OWS20Exception;
import org.junit.Assert;
import org.junit.Test;


/**
 * Parses the scaleExtent WCS 2.0 kvp key
 *
 * @author Andrea Aime - GeoSolutions
 */
public class ScaleExtentKvpParserTest {
    ScaleExtentKvpParser parser = new ScaleExtentKvpParser();

    @Test
    public void testInvalidValues() throws Exception {
        try {
            parser.parse("axisName");
            Assert.fail("should have thrown an exception");
        } catch (OWS20Exception e) {
            checkInvalidSyntaxException(e);
        }
        try {
            parser.parse("axisName,(10,20)");
            Assert.fail("should have thrown an exception");
        } catch (OWS20Exception e) {
            checkInvalidSyntaxException(e);
        }
        try {
            parser.parse("axisName,(10,");
            Assert.fail("should have thrown an exception");
        } catch (OWS20Exception e) {
            checkInvalidSyntaxException(e);
        }
        try {
            parser.parse("axisName(10,20))");
            Assert.fail("should have thrown an exception");
        } catch (OWS20Exception e) {
            checkInvalidSyntaxException(e);
        }
        try {
            parser.parse("axisName((10,20)");
            Assert.fail("should have thrown an exception");
        } catch (OWS20Exception e) {
            checkInvalidSyntaxException(e);
        }
        try {
            parser.parse("axisName(10,20,30)");
            Assert.fail("should have thrown an exception");
        } catch (OWS20Exception e) {
            checkInvalidSyntaxException(e);
        }
        try {
            parser.parse("axisName(10,20),");
            Assert.fail("should have thrown an exception");
        } catch (OWS20Exception e) {
            checkInvalidSyntaxException(e);
        }
        try {
            parser.parse("axisName(10,20),,secondAxis(10,20)");
            Assert.fail("should have thrown an exception");
        } catch (OWS20Exception e) {
            checkInvalidSyntaxException(e);
        }
    }

    @Test
    public void testSingleAxis() throws Exception {
        ScaleToExtentType se = ((ScaleToExtentType) (parser.parse("axis(10,20)")));
        Assert.assertEquals(1, se.getTargetAxisExtent().size());
        TargetAxisExtentType tax = se.getTargetAxisExtent().get(0);
        Assert.assertEquals("axis", tax.getAxis());
        Assert.assertEquals(10.0, tax.getLow(), 0.0);
        Assert.assertEquals(20.0, tax.getHigh(), 0.0);
    }

    @Test
    public void testSingleAxisSpaces() throws Exception {
        ScaleToExtentType se = ((ScaleToExtentType) (parser.parse(" axis ( 10 , 20 ) ")));
        Assert.assertEquals(1, se.getTargetAxisExtent().size());
        TargetAxisExtentType tax = se.getTargetAxisExtent().get(0);
        Assert.assertEquals("axis", tax.getAxis());
        Assert.assertEquals(10.0, tax.getLow(), 0.0);
        Assert.assertEquals(20.0, tax.getHigh(), 0.0);
    }

    @Test
    public void testMultiAxis() throws Exception {
        ScaleToExtentType se = ((ScaleToExtentType) (parser.parse("a1(10,20),a2(30,40)")));
        Assert.assertEquals(2, se.getTargetAxisExtent().size());
        TargetAxisExtentType tax = se.getTargetAxisExtent().get(0);
        Assert.assertEquals("a1", tax.getAxis());
        Assert.assertEquals(10.0, tax.getLow(), 0.0);
        Assert.assertEquals(20.0, tax.getHigh(), 0.0);
        tax = se.getTargetAxisExtent().get(1);
        Assert.assertEquals("a2", tax.getAxis());
        Assert.assertEquals(30.0, tax.getLow(), 0.0);
        Assert.assertEquals(40.0, tax.getHigh(), 0.0);
    }

    @Test
    public void testMultiAxisSpaces() throws Exception {
        ScaleToExtentType se = ((ScaleToExtentType) (parser.parse("a1( 10, 20)  , a2  ( 30 , 40  ) ")));
        Assert.assertEquals(2, se.getTargetAxisExtent().size());
        TargetAxisExtentType tax = se.getTargetAxisExtent().get(0);
        Assert.assertEquals("a1", tax.getAxis());
        Assert.assertEquals(10.0, tax.getLow(), 0.0);
        Assert.assertEquals(20.0, tax.getHigh(), 0.0);
        tax = se.getTargetAxisExtent().get(1);
        Assert.assertEquals("a2", tax.getAxis());
        Assert.assertEquals(30.0, tax.getLow(), 0.0);
        Assert.assertEquals(40.0, tax.getHigh(), 0.0);
    }
}

