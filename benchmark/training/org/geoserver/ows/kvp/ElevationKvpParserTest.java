/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.ows.kvp;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import junit.framework.TestCase;
import org.geotools.util.NumberRange;


/**
 * Test for the elevation kvp parser
 *
 * @author Simone Giannecchini, GeoSolutions SAS
 */
public class ElevationKvpParserTest extends TestCase {
    public void testPeriod() throws ParseException {
        final ElevationKvpParser parser = new ElevationKvpParser("ELEVATION");
        List elements = new ArrayList(((Collection) (parser.parse("1/100/1"))));
        TestCase.assertTrue(((elements.get(0)) instanceof Double));
        TestCase.assertTrue(((elements.size()) == 100));
        TestCase.assertEquals(1.0, ((Double) (elements.get(0))));
    }

    public void testMixed() throws ParseException {
        final ElevationKvpParser parser = new ElevationKvpParser("ELEVATION");
        List elements = new ArrayList(((Collection) (parser.parse("5,3,4,1,2,8.9,1/9"))));
        TestCase.assertTrue(((elements.get(0)) instanceof NumberRange));
        TestCase.assertEquals(1.0, ((NumberRange<Double>) (elements.get(0))).getMinimum());
        TestCase.assertEquals(9.0, ((NumberRange<Double>) (elements.get(0))).getMaximum());
    }

    public void testOutOfOrderSequence() throws ParseException {
        final ElevationKvpParser parser = new ElevationKvpParser("ELEVATION");
        List elements = new ArrayList(((Collection) (parser.parse("5,3,4,1,2,8.9"))));
        TestCase.assertEquals(1.0, elements.get(0));
        TestCase.assertEquals(2.0, elements.get(1));
        TestCase.assertEquals(3.0, elements.get(2));
        TestCase.assertEquals(4.0, elements.get(3));
        TestCase.assertEquals(5.0, elements.get(4));
        TestCase.assertEquals(8.9, elements.get(5));
    }

    public ElevationKvpParser testOrderedSequence() throws ParseException {
        final ElevationKvpParser parser = new ElevationKvpParser("ELEVATION");
        List elements = new ArrayList(((Collection) (parser.parse("1,2,3,4,5,8.9"))));
        TestCase.assertEquals(1.0, elements.get(0));
        TestCase.assertEquals(2.0, elements.get(1));
        TestCase.assertEquals(3.0, elements.get(2));
        TestCase.assertEquals(4.0, elements.get(3));
        TestCase.assertEquals(5.0, elements.get(4));
        TestCase.assertEquals(8.9, elements.get(5));
        return parser;
    }
}

