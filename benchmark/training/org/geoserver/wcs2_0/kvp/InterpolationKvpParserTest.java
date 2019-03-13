/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wcs2_0.kvp;


import java.util.List;
import net.opengis.wcs20.InterpolationAxisType;
import net.opengis.wcs20.InterpolationType;
import org.eclipse.emf.common.util.EList;
import org.geoserver.ows.KvpParser;
import org.geoserver.ows.util.KvpUtils;
import org.geoserver.platform.GeoServerExtensions;
import org.geoserver.platform.OWS20Exception;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class InterpolationKvpParserTest extends GeoServerSystemTestSupport {
    InterpolationKvpParser parser = new InterpolationKvpParser();

    private String axisPrefix;

    public InterpolationKvpParserTest(String axisPrefix) {
        this.axisPrefix = axisPrefix;
    }

    @Test
    public void testInvalidValues() throws Exception {
        try {
            parser.parse(":interpolation");
            Assert.fail("should have thrown an exception");
        } catch (OWS20Exception e) {
            checkInvalidSyntaxException(e);
        }
        try {
            parser.parse("a:linear,,b:nearest");
            Assert.fail("should have thrown an exception");
        } catch (OWS20Exception e) {
            checkInvalidSyntaxException(e);
        }
        try {
            parser.parse("a::linear");
            Assert.fail("should have thrown an exception");
        } catch (OWS20Exception e) {
            checkInvalidSyntaxException(e);
        }
    }

    @Test
    public void testUniformValue() throws Exception {
        InterpolationType it = ((InterpolationType) (parser.parse("http://www.opengis.net/def/interpolation/OGC/1/linear")));
        Assert.assertEquals("http://www.opengis.net/def/interpolation/OGC/1/linear", it.getInterpolationMethod().getInterpolationMethod());
    }

    @Test
    public void testSingleAxis() throws Exception {
        InterpolationType it = ((InterpolationType) (parser.parse(((axisPrefix) + "latitude:http://www.opengis.net/def/interpolation/OGC/1/linear"))));
        EList<InterpolationAxisType> axes = it.getInterpolationAxes().getInterpolationAxis();
        Assert.assertEquals(1, axes.size());
        Assert.assertEquals(((axisPrefix) + "latitude"), axes.get(0).getAxis());
        Assert.assertEquals("http://www.opengis.net/def/interpolation/OGC/1/linear", axes.get(0).getInterpolationMethod());
    }

    @Test
    public void testMultiAxis() throws Exception {
        InterpolationType it = ((InterpolationType) (parser.parse(((((((axisPrefix) + "latitude:") + "http://www.opengis.net/def/interpolation/OGC/1/linear,") + (axisPrefix)) + "longitude:") + "http://www.opengis.net/def/interpolation/OGC/1/nearest"))));
        EList<InterpolationAxisType> axes = it.getInterpolationAxes().getInterpolationAxis();
        Assert.assertEquals(2, axes.size());
        Assert.assertEquals(((axisPrefix) + "latitude"), axes.get(0).getAxis());
        Assert.assertEquals("http://www.opengis.net/def/interpolation/OGC/1/linear", axes.get(0).getInterpolationMethod());
        Assert.assertEquals(((axisPrefix) + "longitude"), axes.get(1).getAxis());
        Assert.assertEquals("http://www.opengis.net/def/interpolation/OGC/1/nearest", axes.get(1).getInterpolationMethod());
    }

    @Test
    public void testParserForVersion() throws Exception {
        // look up parser objects
        List<KvpParser> parsers = GeoServerExtensions.extensions(KvpParser.class);
        KvpParser parser = KvpUtils.findParser("interpolation", "WCS", null, "2.0.0", parsers);
        Assert.assertNotNull(parser);
        // Ensure the correct parser is taken
        Assert.assertEquals(parser.getClass(), InterpolationKvpParser.class);
        // Version 2.0.1
        parser = KvpUtils.findParser("interpolation", "WCS", null, "2.0.1", parsers);
        Assert.assertNotNull(parser);
        // Ensure the correct parser is taken
        Assert.assertEquals(parser.getClass(), InterpolationKvpParser.class);
    }
}

